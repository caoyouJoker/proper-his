package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jdo.adm.ADMInpTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILPayTool;
import jdo.bil.BilInvoice;
import jdo.ibs.IBSBilldTool;
import jdo.ibs.IBSBillmTool;
import jdo.ibs.IBSTool;
import jdo.ins.TJINSRecpTool;
import jdo.mro.MRORecordTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.NJCityInwDriver;
import com.javahis.util.StringUtil;

/**
 *
 * <p>
 * Title: �ɷ���ҵ������
 * </p>
 *
 * <p>
 * Description: �ɷ���ҵ������
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author wangl 2009.04.29
 * @version 1.0
 */
public class BILIBSRecpControl extends TControl {
    TParm endChargeParm = new TParm();
    TParm endBillPayParm = new TParm();
    // �������
    String caseNo = "";
    // ��������
    String patName = "";
    // �Ա�
    String sexCode = "";
    // ����
    String stationCode = "";
    // ��ʼƱ��
    String startInvno = "";
    // ������
    String mrNoOut = "";
    // סԺ��
    String ipdNoOut = "";
    // ��λ��
    String bedNoOut = "";
    // ���Ҵ���
    String deptCodeOut = "";
    // ��Ժ����
    Timestamp inDataOut;
    // ��������
    Timestamp birthDataOut;
    //===zhangp 20130717 start
    TParm tjInsColumns;//���ҽ��sqlcolumns��Ʊ���е�columns��ӦBILIBSRecpControl.java
    TParm insValueParm = new TParm();//���ҽ����ֵ
    //===zhangp 20130717 end

    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        TParm initParm = (TParm) obj;
        mrNoOut = initParm.getData("ODI", "MR_NO").toString();
        ipdNoOut = initParm.getData("ODI", "IPD_NO").toString();
        caseNo = initParm.getData("IBS", "CASE_NO").toString();
        patName = initParm.getData("ODI", "PAT_NAME").toString();
        sexCode = initParm.getData("ODI", "SEX_CODE").toString();
        birthDataOut = initParm.getTimestamp("ODI", "BIRTH_DATE");
        bedNoOut = initParm.getData("ODI", "BED_NO").toString();
        deptCodeOut = initParm.getData("ODI", "DEPT_CODE").toString();
        stationCode = initParm.getData("ODI", "STATION_CODE").toString();
        inDataOut = initParm.getTimestamp("ODI", "ADM_DATE");
        // ��ʼ���������
        this.initPage();
        // �˵�tableר�õļ���
        getTTable("BillTable").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
                                                this, "onBillTableComponent");
        // Ԥ����tableר�õļ���
        getTTable("BillPayTable").addEventListener(
                TTableEvent.CHECK_BOX_CLICKED, this, "onBillPayTableComponent");
        //===zhangp 20120417 start
        this.callFunction("UI|AR_AMT|SetEnabled", false);
        //===zhangp 20120417 end
    }

    /**
     * �˵�table�����¼�
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onBillTableComponent(Object obj) {
        TTable chargeTable = (TTable) obj;
        chargeTable.acceptText();
        TParm tableParm = chargeTable.getParmValue();
        endChargeParm = new TParm();
        int count = tableParm.getCount("BILL_NO");
        //=====zhangp 20120416 start
        int patselY = 0;
        //=====zhangp 20120416 end
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("PAY_SEL", i)) {
                endChargeParm.addData("BILL_NO", tableParm.getValue("BILL_NO",
                        i));
                endChargeParm.addData("BILL_SEQ", tableParm.getInt("BILL_SEQ",
                        i));
                endChargeParm
                        .addData("AR_AMT", tableParm.getValue("AR_AMT", i));
                endChargeParm.addData("APPROVE_FLG", tableParm.getValue(
                        "APPROVE_FLG", i));
                //=====zhangp 20120416 start
                patselY++;
                //=====zhangp 20120416 end
            }
        }
        int feeCount = endChargeParm.getCount("AR_AMT");
        double totAmt = 0.00;
        for (int j = 0; j < feeCount; j++) {
            totAmt = totAmt + endChargeParm.getDouble("AR_AMT", j);
        }
        setValue("TOT_AMT", totAmt);
        setValue("OWN_AMT", totAmt);
        double enArAmt = totAmt - TypeTool.getDouble(getValue("PAY_BILPAY"));
        // if(enArAmt<=0)
        // enArAmt=0;
        setValue("AR_AMT", enArAmt);
        setValue("PAY_CASH", TypeTool.getDouble(getValue("AR_AMT")));
        for(int i=0;i<chargeTable.getParmValue().getCount();i++){
        if (chargeTable.getParmValue().getBoolean("PAY_SEL",
                                                  i)) {
            chargeTable.setValueAt(chargeTable.getParmValue().getDouble(
                    "AR_AMT", i), i, 7);
        } else {
            chargeTable.setValueAt(0.00,i, 7);

        }
        }
        //��ũ�ϲ�ͬ��ʾ
        String sql = "  SELECT CTZ1_CODE FROM ADM_INP"+
                     " WHERE CASE_NO = '"+ caseNo + "'";
        TParm PARM  = new TParm(TJDODBTool.getInstance().select(sql));	
		String ctzCode = PARM.getValue("CTZ1_CODE",0);	
		if(!ctzCode.equals("88"))
            setInsValue();
		else
            setInsValueXNH();
        return true;
    }

    /**
     * Ԥ����table�����¼�
     *
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onBillPayTableComponent(Object obj) {
        TTable billPayTable = (TTable) obj;
        billPayTable.acceptText();
        TParm tableParm = billPayTable.getParmValue();
        endBillPayParm = new TParm();
        int count = tableParm.getCount("RECEIPT_NO");
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("BILLPAY_SEL", i)) {
                endBillPayParm.addData("RECEIPT_NO", tableParm.getValue(
                        "RECEIPT_NO", i));
                endBillPayParm.addData("PRE_AMT", tableParm.getValue("PRE_AMT",
                        i));
            }
        }
        int feeCount = endBillPayParm.getCount("PRE_AMT");
        double bilPayAmt = 0.00;
        for (int j = 0; j < feeCount; j++) {
            bilPayAmt = bilPayAmt + endBillPayParm.getDouble("PRE_AMT", j);
        }
        setValue("PAY_BILPAY", bilPayAmt);
        //===zhangp 20120417 start
        double enArAmt = 0;
        if (getValueDouble("OWN_AMT") >=0) {
            enArAmt = TypeTool.getDouble(getValue("OWN_AMT")) - bilPayAmt;
        } else {
            enArAmt = TypeTool.getDouble(getValue("TOT_AMT")) - bilPayAmt;
        }
        //===zhangp 20120417 end
        // if(enArAmt<=0)
        // enArAmt=0;
        setValue("AR_AMT", enArAmt);
        setValue("PAY_CASH", TypeTool.getDouble(getValue("AR_AMT")));
        return true;
    }

    /**
     * ��ʼ������
     */
    public void initPage() {
        TParm parm = new TParm();
        parm.setData("CASE_NO", caseNo);
        parm.setData("MR_NO", mrNoOut);
        boolean flg = IBSTool.getInstance().checkData(caseNo);
        if (!flg)
            this.messageBox("����δ�����˵���ҽ����Ϣ");
        // Ԥ����table��ʾ����
        TParm bilPayParm = new TParm();
        bilPayParm = BILPayTool.getInstance().selDataForCharge(parm);
        for (int i = 0; i < bilPayParm.getCount(); i++) {
            bilPayParm.addData("BILLPAY_SEL", "N");
        }
        // �˵�table��ʾ����
        TParm chargeParm = new TParm();
        chargeParm = IBSBillmTool.getInstance().selDataForCharge(parm);
        for (int i = 0; i < chargeParm.getCount(); i++) {
            chargeParm.addData("PAY_SEL", "N");
        }
        // ��Ԥ����table��ֵ
        this.getTTable("BillPayTable").setParmValue(bilPayParm);
        // �˵�table��ֵ
        this.getTTable("BillTable").setParmValue(chargeParm);
        // Ʊ����ʾ
        this.checkNo();
    }

    /**
     * �õ�TTable
     *
     * @param tag
     *            String
     * @return TTable
     */
    public TTable getTTable(String tag) {
        return (TTable)this.getComponent(tag);
    }

    /**
     * �����¼�
     *
     * @return boolean
     */
    public boolean onSave() {
    	//20150115 wangjingchun add start
    	//�жϲ����Ƿ��Ѵ�Ʊ	
        TTable billPayTable = (TTable) getComponent("BillTable");
        TParm billPayTableParm = billPayTable.getParmValue();
        int count = billPayTableParm.getCount("BILL_NO");
        if (count < 1) {
            this.messageBox("�ޱ�������");
            return false;
        }
        String isPrinted =
                "SELECT RECEIPT_NO FROM IBS_BILLM WHERE CASE_NO='" + caseNo
                        + "' AND RECEIPT_NO IS NOT NULL  AND (";
        for (int i = 0; i < count; i++) {
            if (i == 0) {
                isPrinted +=
                        " (BILL_NO='#' AND BILL_SEQ='&') "
                                .replaceFirst("#", billPayTableParm.getValue("BILL_NO", i))
                                .replaceFirst("&", billPayTableParm.getValue("BILL_SEQ", i));
            } else if (i > 0) {
                isPrinted +=
                        " OR (BILL_NO='#' AND BILL_SEQ='&') "
                                .replaceFirst("#", billPayTableParm.getValue("BILL_NO", i))
                                .replaceFirst("&", billPayTableParm.getValue("BILL_SEQ", i));
            }
        }
        isPrinted += ")";
//		System.out.println(isPrinted);
    	TParm isPrintedParm = new TParm(TJDODBTool.getInstance().select(isPrinted));
    	if (isPrintedParm.getCount() > 0) {
            messageBox("�˲����Ѵ�Ʊ");
            return false;
    	}	
    	//20150115 wangjingchun add end
        if (!checkNo()) {
            this.messageBox("��δ����,���ȿ���");
            return false;
        }
        if (!checkTotFee()) {
            this.messageBox("���ܳ����ܽ��");
            return false;
        }
        // ��ʼ����һƱ��
        BilInvoice invoice = new BilInvoice();
        invoice = invoice.initBilInvoice("IBS");
        // ��˿�����
        if (invoice == null) {
            this.messageBox_("����δ����!");
            return false;
        }
        if (invoice.getUpdateNo().compareTo(invoice.getEndInvno()) > 0) {
            this.messageBox("Ʊ��������!");
            return false;
        }
        // ������һ��Ʊ��
        if (invoice.getUpdateNo().equals(invoice.getEndInvno())) {
            this.messageBox_("���һ��Ʊ��!");
        }
        //===zhangp 20120412 start
        String inssql = "SELECT * FROM INS_ADM_CONFIRM " +
                        " WHERE CASE_NO = '" + caseNo + "' " +
                        "   AND IN_STATUS <> '5' ";
        TParm isInsParm = new TParm(TJDODBTool.getInstance().select(inssql));
        if (isInsParm.getCount() > 0) {
            messageBox("�˲���Ϊҽ������");
            //====zhangp 20120530 start
            if (!checkInsUpload(isInsParm)) {
            	this.messageBox("��δ�걨,�����걨");
            	return false;  
            }
            TParm insparm = getInsParm(caseNo);
            double insArAmt = StringTool.round(getValueDouble("TOT_AMT"),2);
            if (insArAmt != insparm.getDouble("AR_AMT")) {
				messageBox("�ָ���������,�����·ָ�");
			    return false;  
			}
        }
        //====zhangp 20120530 end
        //===zhangp 20120412 end
        //===zhangp 20120417 start
//        TTable billPayTable = (TTable) getComponent("BillTable");
//        TParm billPayTableParm = billPayTable.getParmValue();
//        int count = billPayTableParm.getCount("BILL_NO");
//        int countsel = 0;
//        for (int i = 0; i < count; i++) {
//            if (billPayTableParm.getBoolean("PAY_SEL", i)) {
//                countsel++;
//            }
//        }
//        if (countsel != count) {
//            messageBox("δѡ��ȫ���˵�");
//            return false;
//        }
        //===zhangp 20120417 end
    	// ==========modify-begin (by wanglong 20120719)===============
//        String apsql=
//        	"SELECT B.NHI_CTZ_FLG, C.IN_STATUS" +
//        	" FROM ADM_INP A, SYS_CTZ B, INS_ADM_CONFIRM C" +
//        	" WHERE A.CASE_NO = '"+caseNo+"'" +
//        	    " AND C.IN_STATUS <> '5'" +
//        		" AND A.CASE_NO = C.CASE_NO" +
//        		" AND A.CTZ1_CODE = B.CTZ_CODE";
//        TParm rstParm = new TParm(TJDODBTool.getInstance().select(apsql));
//        //System.out.println("�����Parm:"+rstParm);
//        if(rstParm.getCount()>0){//���ݿ��Ƿ��м�¼
//            if(rstParm.getValue("NHI_CTZ_FLG",0).equals("Y")){//�Ƿ�Ϊҽ���û�
//               if(!rstParm.getValue("BILL_STATUS",0).equals("2")){
//                      messageBox("δ�걨");
//                      return false;
//                }
//             }
//         }else{
//             messageBox("�����걨");
//            return false;
//         }
    	// ==========modify-end========================================
        int billDCount = endChargeParm.getCount("BILL_NO");
        double totAmt = 0.00;

        TParm endBillDParm = new TParm();
        String approveFlg = "";
        List bilrecGroup = new ArrayList(); // ========pangben modfiy 20110603
        // endBillDParm ����
        for (int i = 0; i < billDCount; i++) {
            approveFlg = endChargeParm.getValue("APPROVE_FLG", i);
            if ("N".equals(approveFlg) || approveFlg.length() == 0) {
                this.messageBox("�˵�δ���");
                return false;
            }
            String billNo = endChargeParm.getValue("BILL_NO", i);
            int bilSeq = endChargeParm.getInt("BILL_SEQ", i);
            TParm inBillDParm = new TParm();
            inBillDParm.setData("BILL_NO", billNo);
            inBillDParm.setData("BILL_SEQ", bilSeq);
//            inBillDParm.setData("CASE_NO",caseNo);
            TParm billdParm = new TParm();
            // �˵���ϸ������
            billdParm = IBSBilldTool.getInstance()
                        .selDataForCharge(inBillDParm);
//            System.out.println("�˵���ϸ������++++++++++++++++++++++"+billdParm);
            int inBillDCount = billdParm.getCount("BILL_NO");
            for (int j = 0; j < inBillDCount; j++) {
                totAmt = totAmt + billdParm.getDouble("AR_AMT", j);
                endBillDParm
                        .addData("BILL_NO", billdParm.getData("BILL_NO", j));
                endBillDParm.addData("BILL_SEQ", billdParm.getData("BILL_SEQ",
                        j));
                endBillDParm.addData("REXP_CODE", billdParm.getData(
                        "REXP_CODE", j));
                endBillDParm
                        .addData("OWN_AMT", billdParm.getData("OWN_AMT", j));
                endBillDParm.addData("AR_AMT", billdParm.getData("AR_AMT", j));
                endBillDParm.addData("PAY_AR_AMT", billdParm.getData(
                        "PAY_AR_AMT", j));
                endBillDParm.addData("OPT_USER", billdParm.getData("OPT_USER",
                        j));
                endBillDParm.addData("OPT_DATE", billdParm.getData("OPT_DATE",
                        j));
                endBillDParm.addData("OPT_TERM", billdParm.getData("OPT_TERM",
                        j));
                if (!bilrecGroup.contains(billdParm.getData("REXP_CODE", j)
                                          + billNo))
                    bilrecGroup.add(billdParm.getValue("REXP_CODE", j)
                                    + billNo);
            }
        }
        // ==============pangben modify 20110603 start �ۼ���֧ͬ����ʽ���͵Ľ��
        TParm endBillDParms = new TParm();
        for (int i = 0; i < bilrecGroup.size(); i++) {
            double sumOwnAMT = 0.00;
            double sumArAMT = 0.00;
            for (int j = 0; j < endBillDParm.getCount("REXP_CODE"); j++) {
                if (bilrecGroup.get(i).equals(
                        endBillDParm.getValue("REXP_CODE", j)
                        + endBillDParm.getValue("BILL_NO", j))) {
                    sumOwnAMT += endBillDParm.getDouble("OWN_AMT", j);
                    sumArAMT += endBillDParm.getDouble("AR_AMT", j);
                }
            }
            endBillDParms
                    .addData("BILL_NO", endBillDParm.getData("BILL_NO", i));
            endBillDParms.addData("BILL_SEQ", endBillDParm.getData("BILL_SEQ",
                    i));
            endBillDParms.addData("REXP_CODE", endBillDParm.getData(
                    "REXP_CODE", i));
            endBillDParms.addData("OWN_AMT", sumOwnAMT);
//            System.out.println("��"+i+"�ν��============="+sumArAMT);
            endBillDParms.addData("AR_AMT", sumArAMT);
            endBillDParms.addData("PAY_AR_AMT", endBillDParm.getData(
                    "PAY_AR_AMT", i));
            endBillDParms.addData("OPT_USER", endBillDParm.getData("OPT_USER",
                    i));
            endBillDParms.addData("OPT_DATE", endBillDParm.getData("OPT_DATE",
                    i));
            endBillDParms.addData("OPT_TERM", endBillDParm.getData("OPT_TERM",
                    i));
        }
        // ==============pangben modify 20110603 stop
        // סԺ��ˮ��
        String receiptNo = SystemTool.getInstance().getNo("ALL", "IBS",
                "RECEIPT_NO", "RECEIPT_NO");
        TParm inAdmParm = new TParm();
        inAdmParm.setData("CASE_NO", caseNo);
        TParm admParm = ADMInpTool.getInstance().selectall(inAdmParm);
        String mrNo = admParm.getValue("MR_NO", 0);
        String ipdNo = admParm.getValue("IPD_NO", 0);
        String regionCode = admParm.getValue("REGION_CODE", 0);
        // �վ���������
        TParm recpMParm = new TParm();
        recpMParm.setData("CASE_NO", caseNo);
        recpMParm.setData("RECEIPT_NO", receiptNo);
        recpMParm.setData("ADM_TYPE", "I");
        recpMParm.setData("IPD_NO", ipdNo);
        recpMParm.setData("MR_NO", mrNo);
        recpMParm.setData("REGION_CODE", regionCode);
        recpMParm.setData("RESET_RECEIPT_NO", "");
        recpMParm.setData("REFUND_FLG", "");
        recpMParm.setData("CASHIER_CODE", Operator.getID());
        recpMParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        recpMParm.setData("OWN_AMT", getValue("OWN_AMT") == null ? new TNull(
                String.class) : getValue("OWN_AMT"));
        recpMParm.setData("DISCT_RESON",
                          getValue("DISCT_RESON") == null ? new TNull(String.class)
                          : getValue("DISCT_RESON"));
        recpMParm.setData("DISCNT_AMT",
                          getValue("DISCNT_AMT") == null ? new TNull(String.class)
                          : getValue("DISCNT_AMT"));
        //====zhangp 20120417 start
//        recpMParm.setData("AR_AMT", getValue("OWN_AMT") == null ? new TNull(
//                String.class) : getValue("OWN_AMT"));
        recpMParm.setData("AR_AMT", getValue("TOT_AMT") == null ? new TNull(
                String.class) : getValue("TOT_AMT"));
        //=====zhangp 20120417 end
        recpMParm.setData("REFUND_CODE", "");
        recpMParm.setData("REFUND_DATE", "");
        recpMParm.setData("PAY_CASH", getValue("PAY_CASH") == null ? new TNull(
                String.class) : getValue("PAY_CASH"));
        recpMParm.setData("PAY_MEDICAL_CARD",
                          getValue("PAY_MEDICAL_CARD") == null ?
                          new TNull(String.class)
                          : getValue("PAY_MEDICAL_CARD"));
        recpMParm.setData("PAY_BANK_CARD",
                          getValue("PAY_BANK_CARD") == null ? new TNull(String.class)
                          : getValue("PAY_BANK_CARD"));
        recpMParm.setData("PAY_INS_CARD",
                          getValue("PAY_INS_CARD") == null ? new TNull(String.class)
                          : getValue("PAY_INS_CARD"));
        recpMParm.setData("PAY_CHECK",
                          getValue("PAY_CHECK") == null ? new TNull(String.class)
                          : getValue("PAY_CHECK"));
        recpMParm.setData("PAY_DEBIT",
                          getValue("PAY_DEBIT") == null ? new TNull(String.class)
                          : getValue("PAY_DEBIT"));
        recpMParm.setData("PAY_BILPAY",
                          getValue("PAY_BILPAY") == null ? new TNull(String.class)
                          : getValue("PAY_BILPAY"));
        recpMParm.setData("PAY_INS", getValue("PAY_INS") == null ? new TNull(
                String.class) : getValue("PAY_INS"));
        recpMParm.setData("PAY_OTHER1",
                          getValue("PAY_OTHER1") == null ? new TNull(String.class)
                          : getValue("PAY_OTHER1"));
        recpMParm.setData("PAY_OTHER2",
                          getValue("PAY_OTHER2") == null ? new TNull(String.class)
                          : getValue("PAY_OTHER2"));
        recpMParm.setData("PAY_REMK", getValue("PAY_REMK") == null ? new TNull(
                String.class) : getValue("PAY_REMK"));
        recpMParm.setData("PREPAY_WRTOFF", 0.00);
        recpMParm.setData("PRINT_NO", this.getValueString("PRINT_NO"));
        recpMParm.setData("OPT_USER", Operator.getID());
        recpMParm.setData("OPT_TERM", Operator.getIP());
        // �վ�����
        int recpDCount = endBillDParms.getCount("BILL_NO");
        // �վ���ϸ������
        TParm recpDParm = new TParm();
        for (int j = 0; j < recpDCount; j++) {
            recpDParm.addData("RECEIPT_NO", receiptNo);
            recpDParm.addData("BILL_NO", endBillDParms.getData("BILL_NO", j)); // =====pangben
            // modify
            // 21010603
            recpDParm.addData("REXP_CODE", endBillDParms
                              .getData("REXP_CODE", j)); // =====pangben modify 21010603
            recpDParm
                    .addData("WRT_OFF_AMT", endBillDParms.getData("AR_AMT", j)); // =====pangben
            // modify
            // 21010603
            recpDParm.addData("OPT_USER", Operator.getID());
            recpDParm.addData("OPT_TERM", Operator.getIP());
        }
//        System.out.println("recpDParm>>>>>>>>>>>>>>>>>>>>>>>>>"+recpDParm);
        // Ԥ��������
        TParm bilPayParm = new TParm();
        TParm inBilPayParm = new TParm();
        int bilPayCount = endBillPayParm.getCount("RECEIPT_NO");
        for (int i = 0; i < bilPayCount; i++) {
            bilPayParm = BILPayTool.getInstance().selAllDataByRecpNo(
                    endBillPayParm.getValue("RECEIPT_NO", i));
            inBilPayParm.addData("IBS_RECEIPT_NO", receiptNo);
            inBilPayParm.addData("RECEIPT_NO", bilPayParm.getData("RECEIPT_NO",
                    0));
            inBilPayParm.addData("CASE_NO", bilPayParm.getData("CASE_NO", 0));
            inBilPayParm.addData("IPD_NO", bilPayParm.getData("IPD_NO", 0));
            inBilPayParm.addData("MR_NO", bilPayParm.getData("MR_NO", 0));
            inBilPayParm.addData("TRANSACT_TYPE", bilPayParm.getData(
                    "TRANSACT_TYPE", 0));
            inBilPayParm.addData("REFUND_FLG", bilPayParm.getData("REFUND_FLG",
                    0));
            inBilPayParm.addData("RESET_BIL_PAY_NO", bilPayParm.getData(
                    "RESET_BIL_PAY_NO", 0));
            inBilPayParm.addData("RESET_RECP_NO", bilPayParm.getData(
                    "RESET_RECP_NO", 0));
            inBilPayParm.addData("CASHIER_CODE", bilPayParm.getData(
                    "CASHIER_CODE", 0));
            inBilPayParm.addData("CHARGE_DATE", bilPayParm.getData(
                    "CHARGE_DATE", 0));
            inBilPayParm.addData("ADM_TYPE", bilPayParm.getData("ADM_TYPE", 0));
            inBilPayParm.addData("PRE_AMT", bilPayParm.getData("PRE_AMT", 0));
            inBilPayParm.addData("PAY_TYPE", bilPayParm.getData("PAY_TYPE", 0));
            inBilPayParm.addData("CHECK_NO", bilPayParm.getData("CHECK_NO", 0));
            inBilPayParm.addData("REMARK", bilPayParm.getData("REMARK", 0));
            inBilPayParm.addData("REFUND_CODE", bilPayParm.getData(
                    "REFUND_CODE", 0));
            inBilPayParm.addData("REFUND_DATE", bilPayParm.getData(
                    "REFUND_DATE", 0));
            inBilPayParm.addData("PRINT_NO", bilPayParm.getData("PRINT_NO", 0));
            inBilPayParm.addData("OPT_USER", Operator.getID());
            inBilPayParm
                    .addData("OPT_DATE", SystemTool.getInstance().getDate());
            inBilPayParm.addData("OPT_TERM", Operator.getIP());
        }
        TParm allParm = new TParm();
//        System.out.println("endBillDParms"+endBillDParms);
        allParm.setData("BILLD", endBillDParms.getData()); // =====pangben
        // modify 21010603
        allParm.setData("RECPM", recpMParm.getData());
        //===zhangp 20130717 start
        allParm.setData("TJINS", insValueParm.getData());
        //===zhangp 20130717 end
        allParm.setData("RECPD", recpDParm.getData());
        allParm.setData("BILPAY", inBilPayParm.getData());
        // Ʊ��������Ϣ
        TParm printReceipt = new TParm();
        printReceipt.setData("UPDATE_NO", this.getValueString("PRINT_NO"));
        printReceipt.setData("RECP_TYPE", "IBS");
        printReceipt.setData("CASHIER_CODE", Operator.getID());
        printReceipt.setData("STATUS", "0");
        printReceipt.setData("START_INVNO", startInvno);
        // Ʊ����ϸ������
        TParm bilInvrcpt = new TParm();
        bilInvrcpt.setData("RECP_TYPE", "IBS");
        bilInvrcpt.setData("INV_NO", this.getValueString("PRINT_NO"));
        bilInvrcpt.setData("RECEIPT_NO", receiptNo);
        bilInvrcpt.setData("CASHIER_CODE", Operator.getID());
        bilInvrcpt.setData("AR_AMT", this.getValueString("TOT_AMT"));
        bilInvrcpt.setData("OPT_USER", Operator.getID());
        bilInvrcpt.setData("OPT_TERM", Operator.getIP());
        allParm.setData("BIL_INVOICE", printReceipt.getData());
        allParm.setData("BIL_INVRCP", bilInvrcpt.getData());
        TParm result = TIOM_AppServer.executeAction("action.ibs.IBSAction",
                "onSaveIBSCharge", allParm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        } else {
        	//===zhangp 20120803 start
            TParm printParm = getPrintData(caseNo,receiptNo);
            //===zhangp 20120803 end
            Timestamp printDate = (SystemTool.getInstance().getDate());
            TParm selAdmInp = new TParm();
            selAdmInp.setData("CASE_NO", caseNo);
            TParm admInpParm = ADMInpTool.getInstance().selectall(selAdmInp);
            Timestamp inDataOut = admInpParm.getTimestamp("IN_DATE", 0);
            Timestamp outDataOut;
            if (admInpParm.getData("DS_DATE", 0) != null)
                outDataOut = admInpParm.getTimestamp("DS_DATE", 0);

            else
                outDataOut = printDate;
            String inDate = StringTool.getString(inDataOut, "yyyyMMdd");
            String outDate = StringTool.getString(outDataOut, "yyyyMMdd");
            String pDate = StringTool.getString(SystemTool.getInstance()
                                                .getDate(), "yyyyMMdd");
            
            //caowl 20131227 �����ǺϿ����Ʊ�汾   start
          //����ҽ��סԺ����ת�� 
                        String sql1 = "SELECT CONFIRM_NO,SDISEASE_CODE FROM INS_ADM_CONFIRM WHERE CASE_NO = '" + 
                           caseNo + "' AND IN_STATUS <> '5' "; 
                          TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql1)); 
                          if (confirmParm.getErrCode() < 0)  
                           { 
                                 messageBox("ҽ����ȡ����"); 
                                 return false; 
                           }  
                          if (confirmParm.getCount() > 1) { 
                                int row  = getTTable("BillTable").getSelectedRow(); 
                                TParm parm  = getTTable("BillTable").getParmValue().getRow(row); 
                                String enddate = "";  
                                String appdate = ""; 
//                                Timestamp yesterday = StringTool.rollDate( 
//                                                       StringTool.getTimestamp(parm.getValue("END_DATE"), "yyyy-MM-dd HH:mm:ss"), -1); 
//                                enddate  = StringTool.getString(yesterday, "yyyyMMdd") ;
                                enddate  = StringTool.getString(StringTool.getTimestamp(parm.getValue("END_DATE"), "yyyy-MM-dd HH:mm:ss"), "yyyyMMdd") ;
                                Timestamp sysDate = SystemTool.getInstance().getDate(); 
                                appdate = StringTool.getString(sysDate,"yyyyMMdd"); 
                                //System.out.println("enddate:"+enddate+"  "+appdate); 
                                int count1 = confirmParm.getCount("CONFIRM_NO"); 
                               //ͬһ�꣬����confirmNo��¼Ϊ2�� ȡ��KN���ʸ�ȷ���� 
                               if (enddate.substring(0, 4).equals(appdate.substring(0, 4)) && 
                                    count1 > 1) { 
                                        inDate = enddate.substring(0,4)+"0101";  
                                        inDataOut = StringTool.getTimestamp(inDate, "yyyyMMdd"); 
                                } 
                              //ȡ��KN���ʸ�ȷ���� 
                               else { 
                                        outDate = enddate.substring(0,4)+"1231"; 
                                        outDataOut = StringTool.getTimestamp(outDate, "yyyyMMdd"); 
                                } 
                           } 
//                           System.out.println("inDate:"+inDate); 
//                           System.out.println("outDate:"+outDate); 
            //caowl 20131227 �����ǺϿ����Ʊ�汾   end
            String sYear = inDate.substring(0, 4);
            String sMonth = inDate.substring(4, 6);
            String sDate = inDate.substring(6, 8);
            String eYear = outDate.substring(0, 4);
            String eMonth = outDate.substring(4, 6);
            String eDate = outDate.substring(6, 8);
            String pYear = pDate.substring(0, 4);
            String pMonth = pDate.substring(4, 6);
            String pDay = pDate.substring(6, 8);
            // int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut) ==
            // 0 ?
            // 1 : StringTool.getDateDiffer(outDataOut, inDataOut);
            // ===========================add by wanglong 20130715
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// ��ʽ��������
            String strInDate = sdf.format(inDataOut);
            String strDsDate = sdf.format(outDataOut);
            Timestamp inDateByDay = java.sql.Timestamp.valueOf(strInDate + " 00:00:00.000");
            Timestamp outDateByDay = java.sql.Timestamp.valueOf(strDsDate + " 00:00:00.000");
            int stayDays = StringTool.getDateDiffer(outDateByDay, inDateByDay);// ����סԺ����
            stayDays = (stayDays == 0 ? 1 : stayDays);
//          int rollDate = StringTool.getDateDiffer(outDataOut, inDataOut);
            int rollDate = stayDays;
            // ===========================add end
            printParm.setData("COPY", "TEXT", "");
            printParm.setData("sYear", "TEXT", sYear);
            printParm.setData("sMonth", "TEXT", sMonth);
            printParm.setData("sDate", "TEXT", sDate);
            printParm.setData("pYear", "TEXT", pYear);
            printParm.setData("pMonth", "TEXT", pMonth);
            printParm.setData("pDay", "TEXT", pDay);
            //====zhangp 20120224 start
            String sql = 
            	" SELECT BEGIN_DATE, END_DATE" +
            	" FROM IBS_BILLM" +
            	" WHERE RECEIPT_NO = '" + receiptNo + "'" +
            	" ORDER BY BEGIN_DATE";
            TParm beginEndParm = new TParm(TJDODBTool.getInstance().select(sql));
            inDate = StringTool.getString(beginEndParm.getTimestamp("BEGIN_DATE", 0), "yyyy/MM/dd");
            outDate = StringTool.getString(beginEndParm.getTimestamp("END_DATE", beginEndParm.getCount()-1).after(outDataOut)?outDataOut:beginEndParm.getTimestamp("END_DATE", beginEndParm.getCount()-1), "yyyy/MM/dd");
//            inDate = StringTool.getString(inDataOut, "yyyy/MM/dd");
//            outDate = StringTool.getString(outDataOut, "yyyy/MM/dd");
            //wangjingchun 20141202 add start
            //ҽ������
            String drSql = "SELECT DIRECTOR_DR_CODE, IDNO, MR_NO FROM MRO_RECORD WHERE CASE_NO = '"+caseNo+"'";
            TParm drParm = new TParm(TJDODBTool.getInstance().select(drSql));
            String dr_sql = "SELECT VS_DR_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"'";
            TParm dr_parm = new TParm(TJDODBTool.getInstance().select(dr_sql));
            String dr_sql1 = "SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = '"+dr_parm.getValue("VS_DR_CODE",0)+"' ";
            TParm dr_parm1 = new TParm(TJDODBTool.getInstance().select(dr_sql1));
            printParm.setData("DR_CODE", "TEXT", drParm.getValue("DIRECTOR_DR_CODE",0));
            printParm.setData("DR_NAME", "TEXT", dr_parm1.getValue("USER_NAME",0));
            //ҵ����ˮ��
            String ibsSql = "SELECT ADM_SEQ, MR_NO FROM INS_IBS WHERE CASE_NO = '"+caseNo+"'";
            TParm ibsParm = new TParm(TJDODBTool.getInstance().select(ibsSql));
            if(ibsParm.getValue("ADM_SEQ", 0).equals("")){
            	printParm.setData("ADM_SEQ","TEXT", receiptNo);
            }else{
            	printParm.setData("ADM_SEQ","TEXT",ibsParm.getValue("ADM_SEQ", 0));
            }
            if(ibsParm.getValue("MR_NO", 0).equals("")){
            	printParm.setData("MR_NO","TEXT",drParm.getValue("MR_NO", 0));
            }else{
            	printParm.setData("MR_NO","TEXT",ibsParm.getValue("MR_NO", 0));
            }
            //ҽ������ adm_inp
            String ctzSql = "SELECT S.CTZ_DESC,S.CTZ_CODE FROM SYS_CTZ S,ADM_INP A WHERE S.CTZ_CODE = A.CTZ1_CODE AND A.CASE_NO = '"+caseNo+"'";
            TParm ctzParm = new TParm(TJDODBTool.getInstance().select(ctzSql));
            if(!ctzParm.getValue("CTZ_CODE",0).equals("99")){
            	printParm.setData("CTZ_DESC","TEXT",ctzParm.getValue("CTZ_DESC", 0));
            }else{
            	printParm.setData("CTZ_DESC","TEXT","�Է�"); // 20170421 zhanglei �Էѻ�����ʾ�Էѱ�ʶ��
            }
            //�������֤��
            if(!ctzParm.getValue("CTZ_CODE", 0).equals("99")&&!ctzParm.getValue("CTZ_CODE", 0).equals("")){
            	//20150115 wangjingchun modify 756
            	try {
            		String idNO = drParm.getValue("IDNO",0);
            		idNO.length();
            		String idNOBefore = idNO.substring(0, 3);
            		String idNOEnd = idNO.substring(idNO.length()-3, idNO.length());
            		String x = "";
            		for(int ii=0;ii<idNO.length()-6;ii++){
            			x +="*";
            		}
            		printParm.setData("IDNO", "TEXT", idNOBefore+x+idNOEnd);
				} catch (Exception e) {
					// TODO: handle exception
					printParm.setData("IDNO", "TEXT", "");
				}
            }else{
            	printParm.setData("IDNO", "TEXT", "");
            }
            //ҽ�ƻ�������
            String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
            TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
            String hosp_class = regionParm.getValue("HOSP_CLASS",0);
            String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hosp_class+"'";
    	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
    	    printParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
            //�շ�Ա
            printParm.setData("CASHIER_CODE","TEXT", Operator.getID());
            String start_date[] = inDate.split("/");
            String end_date[] = outDate.split("/");
            printParm.setData("STARTDATE", "TEXT", start_date[0]+"   "+start_date[1]+"   "+start_date[2]);
            printParm.setData("ENDDATE", "TEXT", end_date[0]+"   "+end_date[1]+"   "+end_date[2]);
            //wangjingchun 20141202 add end
//            printParm.setData("STARTDATE", "TEXT", inDate);
//            printParm.setData("ENDDATE", "TEXT", outDate);
            printParm.setData("PRINTDATE", "TEXT", pDate);
            //=====zhangp 20120224 end
            printParm.setData("eYear", "TEXT", eYear);
            printParm.setData("eMonth", "TEXT", eMonth);
            printParm.setData("eDate", "TEXT", eDate);
            printParm.setData("rollDate", "TEXT", rollDate);
            printParm.setData("PAT_NAME", "TEXT", patName);
            printParm.setData("SEX_CODE", "TEXT", getDesc(sexCode));
            printParm.setData("STATION_CODE", "TEXT", getStation(stationCode));
            printParm.setData("RECEIPT_NO", "TEXT", receiptNo);
//            printParm.setData("CASHIER_CODE", "TEXT", Operator.getName());
//            printParm.setData("MR_NO", "TEXT", bilPayParm.getData("MR_NO", 0));
            printParm.setData("BILL_DATE", "TEXT", printDate);
            printParm.setData("CHARGE_DATE", "TEXT", printDate);
            printParm.setData("DEPT_CODE", "TEXT", getDept(deptCodeOut));
//            printParm.setData("OPT_USER", "TEXT", Operator.getName());
            printParm.setData("OPT_USER", "TEXT", Operator.getID());// modify by wanglong 20130123
            printParm.setData("OPT_DATE", "TEXT", printDate);
//            String printDateC = StringTool.getString(printDate, "yyyy��MM��dd��");
            String printDateC = StringTool.getString(printDate,"yyyy   MM   dd");//wangjingchun add 20141202
            printParm.setData("DATE", "TEXT", printDateC);
            printParm.setData("NO1", "TEXT", receiptNo);
            printParm.setData("NO2", "TEXT", this.getValueString("PRINT_NO"));
            printParm.setData("HOSP", "TEXT", Operator.getHospitalCHNFullName());
            printParm.setData("IPD_NO", "TEXT", ipdNo);
            printParm.setData("BIL_PAY_C", "TEXT", StringUtil.getInstance()
                              .numberToWord(
                                      Double.parseDouble(String.valueOf(this
                    .getValue("PAY_BILPAY") == null ? "0.00" :
                    getValue("PAY_BILPAY")))));
            DecimalFormat df = new DecimalFormat("##########0.00");
            printParm.setData("BIL_PAY", "TEXT", df.format(StringTool.getDouble(this.getValueString("PAY_BILPAY"))));//wangjingchun modify 20141214
            double amtD = StringTool.getDouble(this.getValueString("AR_AMT")) <
                          0 ? -StringTool
                          .getDouble(this.getValueString("AR_AMT"))
                          : StringTool.getDouble(this.getValueString("AR_AMT"));
            double tot = StringTool.getDouble(this.getValueString("TOT_AMT"));
            String amt = df.format(amtD).toString();
            String totAmtS = df.format(tot);
            //�޸� ���Ϊ�ֵģ����治�ܼ����������� caoyong 20130718
            String tmp=StringUtil.getInstance().numberToWord(StringTool.getDouble(totAmtS));
            if(tmp.lastIndexOf("��") > 0){
            	tmp = tmp.substring(0,tmp.lastIndexOf("��")+1);//ȡ����ȷ�Ĵ�д���
            }
            printParm.setData("amtToWord", "TEXT", tmp);
            printParm.setData("AR_AMT", "TEXT", StringTool.getDouble(totAmtS));
            if (StringTool.getDouble(this.getValueString("AR_AMT")) < 0) {
                printParm.setData("XS", "TEXT", 0.00);
                printParm.setData("YT", "TEXT", amt);
            } else {
                printParm.setData("XS", "TEXT", amt);
                printParm.setData("YT", "TEXT", 0.00);
            }
            //====zhangp 20120306 modify start
            double arAmt = this.getValueDouble("AR_AMT");
            if (arAmt >= 0) {
                printParm.setData("ADDAMT", "TEXT",
                                  Math.abs(StringTool.round(StringTool.
                        getDouble(this.getValueString("PAY_CASH")), 2))); //����
                //===zhangp 20120331 start
                printParm.setData("ADDCHECKAMT", "TEXT", Math.abs(
                        StringTool.round(
                                //===zhangp 20120417 start
//                				getValueDouble("PAY_CHECK")
                                getValueDouble("PAY_CHECK") +
                                getValueDouble("PAY_BANK_CARD") +
                                getValueDouble("PAY_DEBIT")
                                //===zhangp 20120417 end
                                , 2)
                                  )); //����
                //===zhangp 20120331 end
                //20141217 wangjingchun add start
				String pay_add_sum = "";
				double a1 = Math.abs(StringTool.round(StringTool.
                        getDouble(this.getValueString("PAY_CASH")), 2));
				double b1 = Math.abs(
                        StringTool.round(getValueDouble("PAY_CHECK"), 2));
				double c1 = Math.abs(
                        StringTool.round(getValueDouble("PAY_BANK_CARD"), 2));;
				if(a1 != 0){
					pay_add_sum += " �֣�"+a1;
				}
				if(b1 != 0){
					pay_add_sum += " ֧��"+b1;
				}
				if(c1 != 0){
					pay_add_sum += " ����"+c1;
				}
				printParm.setData("PAY_ADD_SUM","TEXT",pay_add_sum);
				//20141217 wangjingchun add end
            } else {
                printParm.setData("BACKAMT", "TEXT",
                                  Math.abs(StringTool.round(StringTool.
                        getDouble(this.getValueString("PAY_CASH")), 2))); //��(ȡ����ֵ)
                //===zhangp 20120331 start
                printParm.setData("BACKCHECKAMT", "TEXT", Math.abs(
                        StringTool.round(
                                //===zhangp 20120417 start
//                				getValueDouble("PAY_CHECK")
                                getValueDouble("PAY_CHECK") +
                                getValueDouble("PAY_BANK_CARD") +
                                getValueDouble("PAY_DEBIT")
                                //===zhangp 20120417 end
                                , 2))); //����
                //===zhangp 20120331 end
              //20141217 wangjingchun add start
				String back_sum = "";
				double a2 = Math.abs(StringTool.round(
						StringTool.getDouble(this.getValueString("PAY_CASH")), 2));
				double b2 = Math.abs(
                        StringTool.round(getValueDouble("PAY_CHECK"), 2));
				double c2 = Math.abs(
                        StringTool.round(getValueDouble("PAY_BANK_CARD"), 2));
				if(a2 != 0){
					back_sum += " �֣�"+a2;
				}
				if(b2 != 0){
					back_sum += " ֧��"+b2;
				}
				if(c2 != 0){
					back_sum += " ����"+c2;
				}
				printParm.setData("BACK_SUM","TEXT",back_sum);
				//20141217 wangjingchun add end
            }
            //===zhangp 20120321 start
            //===zhangp 20120412 start
            TTable table = getTTable("BillTable");
            TParm tableParm = table.getParmValue();
            int i = table.getSelectedRow();
            if(i<0){i=0;}//add by kangy 20170607
            String caseNo = tableParm.getData("CASE_NO", i).toString();
            //��ũ��Ʊ����ʾ
            if(ctzParm.getValue("CTZ_CODE", 0).equals("88")){         
            	String sql2 = " SELECT TOT_AMT,OWN_AMT,REAL_INS_AMT AS NHI_PAY"+
	             " FROM INS_XNH"+
	             " WHERE CASE_NO = '"+ caseNo + "'";
	         TParm data  = new TParm(TJDODBTool.getInstance().select(sql2));
	         TParm insParm = data.getRow(0);
	         insParm.setCount(1);
//	         System.out.println("insParm============"+insParm);
             double nhi_pay = insParm.getDouble("NHI_PAY"); //ͳ��(ֱ�����)
             double pay_cash = insParm.getDouble("OWN_AMT");//�Ը����  	
             printParm.setData("PAY_INS_CARD", "TEXT",
                     Math.abs(StringTool.round(0,2)));
             printParm.setData("PAY_INS", "TEXT",
                     Math.abs(StringTool.round(nhi_pay,
           2)));
             printParm.setData("PAY_INS_PERSON", "TEXT",
                     Math.abs(StringTool.round(pay_cash, 2)));
             printParm.setData("PAY_INS_CASH", "TEXT",
                     Math.abs(StringTool.round(pay_cash, 2)));     	
            }else{
            TParm insParm = getInsParm(caseNo);
            if (insParm.getErrCode() < 0) {
                return false;
            }
            //����ҽ����ins_ibs�е�receipt_no
       	    String confirmNo = insParm.getValue("CONFIRM_NO");
   	        String sqlUpdate = " UPDATE INS_IBS SET RECEIPT_NO ='"+ receiptNo+ "'" +
   					   " WHERE CASE_NO ='" + caseNo + "'" +
   					   " AND CONFIRM_NO = '" + confirmNo + "'";
   		    TParm updateParm = new TParm(TJDODBTool.getInstance().update(sqlUpdate));
   		    if (updateParm.getErrCode() < 0) {
   			      err(updateParm.getErrCode() + " " + updateParm.getErrText());
   			      updateParm.setErr(-1, "����ҽ����ʧ��");
   			    return false;
   		    }	    
            double armyAi_amt = insParm.getDouble("ARMYAI_AMT"); //����
            double illnesssubsidyamt = insParm.getDouble("ILLNESS_SUBSIDY_AMT"); //����󲡽��
            double account_pay_amt = insParm.getDouble("ACCOUNT_PAY_AMT"); //�����˻�
            double nhi_comment = insParm.getDouble("NHI_COMMENT"); //����
            double nhi_pay = insParm.getDouble("NHI_PAY"); //ͳ��           
            double pay_cash = 0.00;
            if(insParm.getValue("INS_CROWD_TYPE").equals("3"))
                  pay_cash = insParm.getDouble("AR_AMT")-
                  nhi_comment-nhi_pay-armyAi_amt-account_pay_amt; //�ֽ�֧��/�Ը�  	
            else{
            	if(insParm.getValue("SDISEASE_CODE").length()==0)
           		 pay_cash = insParm.getDouble("OWN_PAY"); //�ֽ�֧��/�Ը�  
              	else{
           		  String inDateSdisease = StringTool.getString(
               			insParm.getTimestamp("IN_DATE"), "yyyyMMdd");
           	      if (Integer.parseInt(inDateSdisease) < Integer.parseInt("20170101"))
                      pay_cash = insParm.getDouble("OWN_PAY"); //�ֽ�֧��/�Ը�  
           	     else
           	       pay_cash = insParm.getDouble("OWN_PAY")+
           	             insParm.getDouble("RESTART_STANDARD_AMT"); //�ֽ�֧��/�Ը�  	
           	  }	
           }          
            //wangjingchun 20141203 add start
            //�������
            printParm.setData("ARMYAI_AMT","TEXT",armyAi_amt);
            //����󲡽��
            printParm.setData("ILLNESS_SUBSIDY_AMT","TEXT",illnesssubsidyamt);
            //������
            printParm.setData("NHI_COMMENT","TEXT",nhi_comment);
            if(!ctzParm.getValue("CTZ_CODE", 0).equals("99")&&!ctzParm.getValue("CTZ_CODE", 0).equals("")){
            	printParm.setData("OTHER_INS","TEXT","����ҽ��֧���������� "+armyAi_amt+",������ "+nhi_comment+",����� "+illnesssubsidyamt);
            }else{
            	printParm.setData("OTHER_INS","TEXT","");
            	//סԺ�渶ʹ��
            	printParm.setData("ADVANCE_CASE_NO","TEXT","�渶סԺ��:"+caseNo+"���渶����ʹ�ã�");
            }
            //ҽ������֧��
            double other_ins_pay = armyAi_amt+illnesssubsidyamt+nhi_comment;           
            printParm.setData("OTHER_INS_PAY", "TEXT",Math.abs(StringTool.round(
            			other_ins_pay,2)));
            //wangjingchun 20141203 add END
            if (insParm.getCount() > 0) { 
            	//this.messageBox("��ҽ������insParm:" + insParm);
                printParm.setData("PAY_INS_CARD", "TEXT",
                                  Math.abs(StringTool.round(
                                          account_pay_amt,
                                          2)));
                printParm.setData("PAY_INS", "TEXT",
                                  Math.abs(StringTool.round(nhi_pay,
                        2)));
                printParm.setData("PAY_INS_PERSON", "TEXT",
                                  Math.abs(StringTool.round(
                                          pay_cash
                                          , 2)));
                printParm.setData("PAY_INS_CASH", "TEXT",
                                  Math.abs(StringTool.round(
                                          pay_cash
                                          , 2)));
                if(illnesssubsidyamt!=0){
                printParm.setData("JZ_TYPE","TEXT","�����");		
                printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(
                		illnesssubsidyamt, 2)));
                }
                else{
                printParm.setData("JZ_TYPE","TEXT","������");	
                printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(
                		nhi_comment, 2)));
                }
                //===zhangp 20120412 end
            } else {
            	//this.messageBox("��ҽ������insParm:" + insParm);
                printParm.setData("PAY_INS_CARD", "TEXT",
                                  Math.abs(StringTool.round(
                                          0.00,
                                          2)));
                printParm.setData("PAY_INS", "TEXT",
                                  Math.abs(StringTool.round(0.00,
                        2)));
                printParm.setData("PAY_INS_PERSON", "TEXT",
                                  Math.abs(StringTool.round(
                                          0.00,
                                          2)));
                printParm.setData("PAY_INS_CASH", "TEXT",
                		Math.abs(StringTool.round(getValueDouble("TOT_AMT"), 2)));
                				  //getValue("TOT_AMT")); // 20170421 zhanglei Ϊ�Է��û�����ʷ�=�ܶ���ʵ
//                printParm.setData("PAY_INS_CASH", "TEXT",
//                                  Math.abs(StringTool.round(
//                                          0.00,
//                                          2))); ԭ��
                printParm.setData("PAY_INS_BIG","TEXT",Math.abs(StringTool.round(
                       0.00 , 2)));//caowl 20130318 ������ʾ�����Ӵ��֧��
            }
          }
//            String sql =
//            	"SELECT SUM(ACCOUNT_PAY_AMT) ACCOUNT_PAY_AMT,SUM(APPLY_AMT) APPLY_AMT,SUM(OWN_AMT) OWN_AMT FROM INS_IBS " +
//            	" WHERE CASE_NO = '"+caseNo+"'";
//            TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
//            if(insParm.getCount()>0){
//            	printParm.setData("PAY_INS_CARD", "TEXT",
//                        Math.abs(StringTool.round(
//                        		insParm.getDouble("ACCOUNT_PAY_AMT", 0),
//                                         2)));
//            	printParm.setData("PAY_INS", "TEXT",
//                        Math.abs(StringTool.round(insParm.getDouble("APPLY_AMT", 0),
//                                         2)));
//            	printParm.setData("PAY_INS_PERSON", "TEXT",
//                        Math.abs(StringTool.round(
//                        		insParm.getDouble("OWN_AMT", 0),
//                                         2)));
//            	printParm.setData("PAY_INS_CASH", "TEXT",
//                        Math.abs(StringTool.round(
//                        		insParm.getDouble("OWN_AMT", 0) -
//                        		insParm.getDouble("ACCOUNT_PAY_AMT", 0),
//                                         2)));
//            }else{
//            	printParm.setData("PAY_INS_CARD", "TEXT",
//                        Math.abs(StringTool.round(this.
//                                         getValueDouble("tNumberTextField_1"),
//                                         2)));
//            	printParm.setData("PAY_INS", "TEXT",
//                        Math.abs(StringTool.round(this.getValueDouble("PAY_INS"),
//                                         2)));
//            	printParm.setData("PAY_INS_PERSON", "TEXT",
//                        Math.abs(StringTool.round(
////                        		tot -
////                                         this.getValueDouble("PAY_INS")
////                                         )
//                        		this.getValueDouble("OWN_AMT")
//                        		,2)));
//            	printParm.setData("PAY_INS_CASH", "TEXT",
//                        Math.abs(StringTool.round(
////                        		tot -
////                                         this.getValueDouble("PAY_INS") -
////                                         this.
////                                         getValueDouble("PAY_INS_CARD"),
////                                         2)));
//                        		this.getValueDouble("OWN_AMT")
//                        		,2)));
            //===zhangp 20120412 end
            //===zhangp 20120412 start
//            }
            //===zhangp 20120412 end
            //===zhangp 20120321 end
            //====zhangp 20120306 modify end
//            this.openPrintDialog("%ROOT%\\config\\prt\\IBS\\IBSRecp.jhw",
//                                 printParm, true);
            this.openPrintDialog(IReportTool.getInstance().getReportPath("IBSRecp.jhw"),
                                 IReportTool.getInstance().getReportParm("IBSRecp.class", printParm), true);//����ϲ�modify by wanglong 20130730
        }
        this.messageBox("���ɳɹ�");
//        jsXML(); // ����xml�ļ�
        //=====shibl 20120806 start
        //���²�����ҳ������
        TParm mroParm = MRORecordTool.getInstance().updateMROIbsForIBS(caseNo);
        if(mroParm.getErrCode()<0){
        	messageBox("���²�����ҳ����ʧ��");
        }
        //=====shibl 20120806 end
        onClear();
        return true;
    }

    /**
     * У��ҽ�������Ƿ��ϴ�
     * @param parm TParm
     * @return boolean
     */
    public boolean checkInsUpload(TParm parm) {
        if (!"2".equals(parm.getValue("IN_STATUS", 0))&&!"4".equals(parm.getValue("IN_STATUS", 0)))
            return false;
        return true;
    }

    /**
     * �õ�����˵��
     *
     * @param deptCode
     *            String
     * @return String
     */
    public String getDept(String deptCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
                + deptCode + "'"));
        return parm.getValue("DEPT_CHN_DESC", 0);
    }

    /**
     * �õ�����˵��
     *
     * @param code
     *            String
     * @return String
     */
    public String getDesc(String code) {
        TParm descParm = new TParm();
        descParm
                .setData(TJDODBTool
                         .getInstance()
                         .select(
                                 "SELECT ID,CHN_DESC FROM  SYS_DICTIONARY WHERE GROUP_ID='SYS_SEX'"));
        if (descParm.getCount() <= 0)
            return "";
        Vector vct = (Vector) descParm.getData("ID");
        int index = vct.indexOf(code);
        if (index < 0)
            return "";
        return descParm.getValue("CHN_DESC", index);
    }

    /**
     * �õ�����˵��
     *
     * @param stationCode
     *            String
     * @return String
     */
    public String getStation(String stationCode) {
        TParm parm = new TParm(TJDODBTool.getInstance().select(
                "SELECT STATION_CODE,STATION_DESC FROM SYS_STATION  WHERE STATION_CODE='"
                + stationCode + "'"));
        return parm.getValue("STATION_DESC", 0);
    }

    /**
     * �õ�Ʊ����Ϣ
     * @param caseNo String
     * @return TParm
     */
    public TParm getPrintData(String caseNo,String receipt_no) {
        DecimalFormat formatObject = new DecimalFormat("###########0.00");
        String selRecpD =
                " SELECT D.RECEIPT_NO, D.REXP_CODE, D.WRT_OFF_AMT " +
                "   FROM BIL_IBS_RECPM M, BIL_IBS_RECPD D " +
                "  WHERE M.CASE_NO = '" + caseNo + "' " +
                "  AND M.RECEIPT_NO = '" + receipt_no + "'" +
                "    AND M.RECEIPT_NO = D.RECEIPT_NO ";
        // ��ѯ��֧ͬ����ʽ������(�ս���)
        TParm selRecpDParm = new TParm(TJDODBTool.getInstance()
                                       .select(selRecpD));
        int recpDCount = selRecpDParm.getCount("REXP_CODE");
        TParm charge = new TParm();
        String rexpCode = "";
        double wrtOffAmt101 = 0.00;
        double wrtOffAmt102 = 0.00;
        double wrtOffAmt103 = 0.00;
        double wrtOffAmt104 = 0.00;
        double wrtOffAmt105 = 0.00;
        double wrtOffAmt106 = 0.00;
        double wrtOffAmt107 = 0.00;
        double wrtOffAmt108 = 0.00;
        double wrtOffAmt109 = 0.00;
        double wrtOffAmt110 = 0.00;
        double wrtOffAmt111 = 0.00;
        double wrtOffAmt112 = 0.00;
        double wrtOffAmt113 = 0.00;
        double wrtOffAmt114 = 0.00;
        double wrtOffAmt115 = 0.00;
        double wrtOffAmt116 = 0.00;
        double wrtOffAmt117 = 0.00;
        double wrtOffAmt118 = 0.00;
        double wrtOffAmt119 = 0.00;
        //===zhangp 20120307 modify start
        double wrtOffAmt120 = 0.00;
        //===zhangp 20120307 modify end
        double totAmt = 0.00;
        double wrtOffSingle = 0.00;
        TParm chargeParm = new TParm();
        for (int i = 0; i < recpDCount; i++) {
            rexpCode = selRecpDParm.getValue("REXP_CODE", i);
            wrtOffSingle = selRecpDParm.getDouble("WRT_OFF_AMT", i);
            totAmt = totAmt + wrtOffSingle;
            //========20120224 zhangp modify start
            if ("020".equals(rexpCode)) { // ��λ��
                wrtOffAmt101 = wrtOffAmt101 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt101);
                chargeParm.setData("CHARGE01", arAmtS);
            } else if ("021".equals(rexpCode)) { // ����
                wrtOffAmt102 = wrtOffAmt102 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt102);
                chargeParm.setData("CHARGE02", arAmtS);
            } else if ("022.01".equals(rexpCode) || "022.02".equals(rexpCode)) { // ��ҩ=������+�ǿ�����
                wrtOffAmt103 = wrtOffAmt103 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt103);
                chargeParm.setData("CHARGE03", arAmtS);
//			} else if ("104".equals(rexpCode)) { // ����
//				wrtOffAmt104 = wrtOffAmt104 + wrtOffSingle;
//				String arAmtS = formatObject.format(wrtOffAmt104);
//				chargeParm.setData("CHARGE04", arAmtS);
            } else if ("023".equals(rexpCode)) { // �г�ҩ
                wrtOffAmt105 = wrtOffAmt105 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt105);
                chargeParm.setData("CHARGE05", arAmtS);
            } else if ("024".equals(rexpCode)) { // �в�ҩ
                wrtOffAmt106 = wrtOffAmt106 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt106);
                chargeParm.setData("CHARGE06", arAmtS);
            } else if ("025".equals(rexpCode)) { // ����
                wrtOffAmt107 = wrtOffAmt107 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt107);
                chargeParm.setData("CHARGE07", arAmtS);
            } else if ("026".equals(rexpCode)) { // ���Ʒ�
                wrtOffAmt108 = wrtOffAmt108 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt108);
                chargeParm.setData("CHARGE08", arAmtS);
            } else if ("027".equals(rexpCode)) { // �����
                wrtOffAmt109 = wrtOffAmt109 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt109);
                chargeParm.setData("CHARGE09", arAmtS);
            } else if ("028".equals(rexpCode)) { // ������
                wrtOffAmt110 = wrtOffAmt110 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt110);
                chargeParm.setData("CHARGE10", arAmtS);
            } else if ("029".equals(rexpCode)) { // ����
                wrtOffAmt111 = wrtOffAmt111 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt111);
                chargeParm.setData("CHARGE11", arAmtS);
            } else if ("02A".equals(rexpCode)) { // ��Ѫ��
                wrtOffAmt112 = wrtOffAmt112 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt112);
                chargeParm.setData("CHARGE12", arAmtS);
            } else if ("02B".equals(rexpCode)) { // ������
                wrtOffAmt113 = wrtOffAmt113 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt113);
                chargeParm.setData("CHARGE13", arAmtS);
            } else if ("02C".equals(rexpCode)) { // ����
                wrtOffAmt114 = wrtOffAmt114 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt114);
                chargeParm.setData("CHARGE14", arAmtS);
            } else if ("02D".equals(rexpCode)) { // ����
                wrtOffAmt115 = wrtOffAmt115 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt115);
                chargeParm.setData("CHARGE15", arAmtS);
            } else if ("02E".equals(rexpCode)) { // �Ҵ�
                wrtOffAmt116 = wrtOffAmt116 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt116);
                chargeParm.setData("CHARGE16", arAmtS);
            } else if ("032".equals(rexpCode)) { // CT
                wrtOffAmt117 = wrtOffAmt117 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt117);
                chargeParm.setData("CHARGE17", arAmtS);
            } else if ("033".equals(rexpCode)) { // MR
                wrtOffAmt118 = wrtOffAmt118 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt118);
                chargeParm.setData("CHARGE18", arAmtS);
            } else if ("02F".equals(rexpCode)) { // �Է�
                wrtOffAmt119 = wrtOffAmt119 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt119);
                chargeParm.setData("CHARGE19", arAmtS);
            }
            //==zhangp 20120307 modify start
            else if ("035".equals(rexpCode)) { // �Է�
                wrtOffAmt120 = wrtOffAmt120 + wrtOffSingle;
                String arAmtS = formatObject.format(wrtOffAmt120);
                chargeParm.setData("CHARGE20", arAmtS);
            }
            //===zhangp 20120307 nmodify end
        }
        charge.setData("CHARGE01", "TEXT",
                       chargeParm.getData("CHARGE01") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE01"));
        charge.setData("CHARGE02", "TEXT",
                       chargeParm.getData("CHARGE02") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE02"));
        charge.setData("CHARGE03", "TEXT",
                       chargeParm.getData("CHARGE03") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE03"));
//		charge.setData("CHARGE04", "TEXT",
//				chargeParm.getData("CHARGE04") == null ? 0.00 : chargeParm
//						.getData("CHARGE04"));
        //======zhangp modify end 20120224
        charge.setData("CHARGE05", "TEXT",
                       chargeParm.getData("CHARGE05") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE05"));
        charge.setData("CHARGE06", "TEXT",
                       chargeParm.getData("CHARGE06") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE06"));
        charge.setData("CHARGE07", "TEXT",
                       chargeParm.getData("CHARGE07") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE07"));
        charge.setData("CHARGE08", "TEXT",
                       chargeParm.getData("CHARGE08") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE08"));
        charge.setData("CHARGE09", "TEXT",
                       chargeParm.getData("CHARGE09") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE09"));
        charge.setData("CHARGE10", "TEXT",
                       chargeParm.getData("CHARGE10") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE10"));
        charge.setData("CHARGE11", "TEXT",
                       chargeParm.getData("CHARGE11") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE11"));
        charge.setData("CHARGE12", "TEXT",
                       chargeParm.getData("CHARGE12") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE12"));
        charge.setData("CHARGE13", "TEXT",
                       chargeParm.getData("CHARGE13") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE13"));
        charge.setData("CHARGE14", "TEXT",
                       chargeParm.getData("CHARGE14") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE14"));
        charge.setData("CHARGE15", "TEXT",
                       chargeParm.getData("CHARGE15") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE15"));
        charge.setData("CHARGE16", "TEXT",
                       chargeParm.getData("CHARGE16") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE16"));
        charge.setData("CHARGE17", "TEXT",
                       chargeParm.getData("CHARGE17") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE17"));
        charge.setData("CHARGE18", "TEXT",
                       chargeParm.getData("CHARGE18") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE18"));
        charge.setData("CHARGE19", "TEXT",
                       chargeParm.getData("CHARGE19") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE19"));
        //===zhangp 20120307 modify start
        charge.setData("CHARGE20", "TEXT",
                       chargeParm.getData("CHARGE20") == null ? 0.00 :
                       chargeParm
                       .getData("CHARGE20"));
        //===zhangp 20120307 modify end
        charge.setData("TOT_AMT", "TEXT", formatObject.format(totAmt));
        charge.setData("AR_AMT", "TEXT", formatObject.format(totAmt));
        return charge;
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("TOT_AMT;OWN_AMT;PAY_INS;PAY_BILPAY;PAY_MEDICAL_CARD;"
                   + "PAY_INS_CARD;PAY_OTHER1;PAY_OTHER2;AR_AMT;PAY_CASH;"
                   + "PAY_CHECK;PAY_BANK_CARD;PAY_DEBIT;PAY_REMK;DISCNT_AMT;"
                   + "DISCT_RESON;tNumberTextField_1");
        this.callFunction("UI|BillTable|removeRowAll");
        this.callFunction("UI|BillPayTable|removeRowAll");
        initPage();
        insValueParm = new TParm();
    }

//	/**
//	 * �ر��¼�
//	 *
//	 * @return boolean
//	 */
//	public boolean onClosing() {
//		switch (messageBox("��ʾ��Ϣ", "�Ƿ񱣴�?", this.YES_NO_CANCEL_OPTION)) {
//		case 0:
//			if (!onSave())
//				return false;
//			break;
//		case 1:
//			break;
//		case 2:
//			return false;
//		}
//		return true;
//	}

    /**
     * У�鿪����
     *
     * @return boolean
     */
    public boolean checkNo() {
        TParm parm = new TParm();
        parm.setData("RECP_TYPE", "IBS");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("STATUS", "0");
        parm.setData("TERM_IP", Operator.getIP());
        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
        String updateNo = noParm.getValue("UPDATE_NO", 0);
        startInvno = "";
        startInvno = noParm.getValue("START_INVNO", 0);
        if (updateNo == null || updateNo.length() == 0) {
            return false;
        }
        // ��ʼ����һƱ��
        BilInvoice invoice = new BilInvoice();
        invoice = invoice.initBilInvoice("IBS");
        if (invoice.getUpdateNo().compareTo(invoice.getEndInvno()) > 0) {
            this.messageBox("Ʊ��������!");
            return false;
        }
        setValue("PRINT_NO", updateNo);
        return true;
    }

    /**
     * У�����Ԥ����,�ۼ�Ӧ���ܷ��ô�С
     *
     * @param totAmt
     *            double
     * @return boolean
     */
    public boolean checkFee(double totAmt) {
        double bilPayAmt = 0.00;
        int feeCount = endBillPayParm.getCount("PRE_AMT");
        for (int j = 0; j < feeCount; j++) {
            bilPayAmt = bilPayAmt + endBillPayParm.getDouble("PRE_AMT", j);
        }
        if (bilPayAmt < totAmt)
            return false;
        return true;
    }

    /**
     * ���³�Ժ
     */
    public void onOutHosp() {
        TParm sendParm = new TParm();
        // ������
        sendParm.setData("MR_NO", mrNoOut);
        // סԺ��
        sendParm.setData("IPD_NO", ipdNoOut);
        // �����
        sendParm.setData("CASE_NO", caseNo);
        // ����
        sendParm.setData("PAT_NAME", patName);
        // �Ա�
        sendParm.setData("SEX_CODE", sexCode);
        // ��������
        if (birthDataOut == null)
            return;
        String AGE = com.javahis.util.StringUtil.showAge(birthDataOut,
                inDataOut);
        // ����
        sendParm.setData("AGE", AGE);
        // ����
        sendParm.setData("BED_NO", bedNoOut);
        // ����
        sendParm.setData("OUT_DEPT_CODE", deptCodeOut);
        // ����
        sendParm.setData("OUT_STATION_CODE", stationCode);
        // ��Ժʱ��
        sendParm.setData("IN_DATE", inDataOut);

        //==========================zhangp start
        String sql = 
        	" SELECT CASE_NO" +
        	" FROM ADM_WAIT_TRANS" +
        	" WHERE CASE_NO = '" + caseNo + "'";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if(result.getCount()>0){
        	messageBox("�˲����ڴ�ת���ת����,���봲,�ٳ�Ժ");
        	return;
        }
        //==========================zhangp end
        
        TParm reParm = (TParm)this.openDialog(
                "%ROOT%\\config\\adm\\ADMOutInp.x", sendParm);

    }

    /**
     * ����ǰTOOLBAR
     */
    public void onShowWindowsFunction() {
        // ��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    /**
     * ��Ժ������������ļ�
     */
    public void jsXML() {
        double phaAmt = 0.00;
        StringBuffer sql = new StringBuffer();
        sql
                .append(
                        "SELECT ORDER_CAT1_CODE ,TOT_AMT FROM IBS_ORDD WHERE CASE_NO='"
                        + caseNo + "'");
        TParm orderParm = new TParm(TJDODBTool.getInstance().select(
                sql.toString()));
        // �ۼ�ҩƷ�ܼ۸�
        for (int i = 0; i < orderParm.getCount(); i++) {
            if (orderParm.getValue("ORDER_CAT1_CODE", i).contains("PHA")) {
                phaAmt += orderParm.getDouble("TOT_AMT", i);
            }
        }
        DecimalFormat df = new DecimalFormat("##########0.00");
        // 1.��������
        TParm inparm = new TParm();
        inparm.insertData("TBR", 0, this.getValue("MR_NO")); // ������
        String name = null;
        if (this.getValueString("PAT_NAME").length() > 0)
            name = this.getValueString("PAT_NAME");
        else
            name = this.getValueString("PAT_NAMEOUT");
        inparm.insertData("XM", 0, name); // ����
        inparm.insertData("RYXZ", 0, "��ְ"); // ��Ա����
        inparm.insertData("XH", 0, this.getValue("IPD_NO")); // ��Ժ���
        StringBuffer SQL = new StringBuffer();
        SQL.append("SELECT DS_DATE FROM ADM_INP WHERE MR_NO='" + mrNoOut
                   + "' AND CASE_NO='" + caseNo + "'");
        TParm result = new TParm(TJDODBTool.getInstance()
                                 .select(SQL.toString()));
        // û�г�Ժ������xml
        if (null == result)
            return;
        Double totAmt = new Double(getValue("TOT_AMT").toString());
        String dDate = result.getValue("DS_DATE", 0).substring(0,
                result.getValue("DS_DATE", 0).indexOf(" ")).replace("-", "");
        inparm.insertData("CYSJ", 0, dDate); // ��Ժʱ��
        inparm.insertData("ZFY", 0, df.format(totAmt)); // ҽ�Ʒ��úϼ�
        inparm.insertData("YF", 0, phaAmt); // ҩ�Ѻϼ�
        inparm.insertData("XMF", 0, df.format(totAmt - phaAmt)); // ������Ŀ�Ѻϼ�
        inparm.insertData("GRZL", 0, 0.00); // ��������
        inparm.insertData("GRZF", 0, df.format(this.getValue("OWN_AMT"))); // �����Ը�
        inparm.insertData("YBZF", 0, ""); // ͳ��󲡺Ͳ���֧��
        inparm.insertData("ZHYE", 0, "0"); // �����˻����
        inparm.insertData("ZHZF", 0, df.format(this.getValue("OWN_AMT"))); // �����˻�֧��
        inparm.insertData("XJZF", 0, df.format(this.getValue("OWN_AMT"))); // �ֽ�֧��
        inparm.insertData("XZMC", 0, ""); // ����
        inparm.insertData("DJH", 0, ""); // ���ݺ�
        inparm.insertData("YSM", 0, ""); // �ܴ�ҽ����
        inparm.insertData("YH1", 0, "0"); // �Ż�1
        inparm.insertData("YH2", 0, "0"); // �Ż�2
        inparm.insertData("YH3", 0, "0"); // �Ż�3
        inparm.insertData("TCZF", 0, "0"); // ͳ��֧��
        inparm.insertData("DBZF", 0, "0"); // ��֧��
        inparm.insertData("BZZF", 0, "0"); // ����֧��
        inparm.addData("SYSTEM", "COLUMNS", "TBR");
        inparm.addData("SYSTEM", "COLUMNS", "XM");
        inparm.addData("SYSTEM", "COLUMNS", "RYXZ");
        inparm.addData("SYSTEM", "COLUMNS", "XH");
        inparm.addData("SYSTEM", "COLUMNS", "CYSJ");
        inparm.addData("SYSTEM", "COLUMNS", "ZFY");
        inparm.addData("SYSTEM", "COLUMNS", "YF");
        inparm.addData("SYSTEM", "COLUMNS", "XMF");
        inparm.addData("SYSTEM", "COLUMNS", "GRZL");
        inparm.addData("SYSTEM", "COLUMNS", "GRZF");
        inparm.addData("SYSTEM", "COLUMNS", "YBZF");
        inparm.addData("SYSTEM", "COLUMNS", "ZHYE");
        inparm.addData("SYSTEM", "COLUMNS", "ZHZF");
        inparm.addData("SYSTEM", "COLUMNS", "XJZF");
        inparm.addData("SYSTEM", "COLUMNS", "XZMC");
        inparm.addData("SYSTEM", "COLUMNS", "DJH");
        inparm.addData("SYSTEM", "COLUMNS", "YSM");
        inparm.addData("SYSTEM", "COLUMNS", "YH1");
        inparm.addData("SYSTEM", "COLUMNS", "YH2");
        inparm.addData("SYSTEM", "COLUMNS", "YH3");
        inparm.addData("SYSTEM", "COLUMNS", "TCZF");
        inparm.addData("SYSTEM", "COLUMNS", "DBZF");
        inparm.addData("SYSTEM", "COLUMNS", "BZZF");
        inparm.setCount(1);
//		System.out.println("=======inparm=============" + inparm);
        // 2.�����ļ�
        NJCityInwDriver.createXMLFile(inparm, "c:/NGYB/cyjsd.xml");
        this.messageBox("���ɳɹ�");
    }

    /**
     * ҽ���걨
     */
    public void onInsUpload() {
        //===zhangp 20120416 start
        TTable billPayTable = (TTable) getComponent("BillTable");
        TParm tableParm = billPayTable.getParmValue();
        int count = tableParm.getCount("BILL_NO");
        int countsel = 0;
        for (int i = 0; i < count; i++) {
            if (tableParm.getBoolean("PAY_SEL", i)) {
                countsel++;
            }
        }
//        if (countsel != count) {
//            messageBox("δѡ��ȫ���˵�");
//            return;
//        }
        //===zhangp 20120416 end
    	// ==========modify-begin (by wanglong 20120719)===============
        String repsql=
            "SELECT B.NHI_CTZ_FLG,A.BILL_STATUS"+
           	 " FROM ADM_INP A, SYS_CTZ B"+
           	" WHERE A.CASE_NO = '"+caseNo+"' AND A.CTZ1_CODE = B.CTZ_CODE";
        TParm rstParm = new TParm(TJDODBTool.getInstance().select(repsql));
        //System.out.println("�걨��Parm:"+rstParm);
        if(rstParm.getCount()>0){//���ݿ��Ƿ��м�¼
        	//caowl 20140108 start
        	String sqlAdm = "SELECT COUNT(CASE_NO) AS COUNT FROM ADM_INP WHERE DS_DATE IS NULL AND CASE_NO = '"+caseNo+"'";
        	TParm parmAdm = new TParm(TJDODBTool.getInstance().select(sqlAdm));
        	//��Ժ
        	if(parmAdm.getInt("COUNT",0)==1){
        		      		
        	}else{
        	//��Ժ   
        		 if(rstParm.getValue("NHI_CTZ_FLG",0).equals("Y")){//�Ƿ�Ϊҽ���û�
                     if(!rstParm.getValue("BILL_STATUS",0).equals("3")){
                          messageBox("����δ���");
                          return;
                      }
                  }
        	}   
        	//caowl 20140108 end
        }else{
             messageBox("�޼�¼");
             return;
        }
    	// ==========modify-end========================================
        TParm sendParm = new TParm();
//        caseNo = "120320000008";
        // �����
        sendParm.setData("CASE_NO", caseNo);
//        TParm insAdmConfirmParm = INSADMConfirmTool.getInstance().queryADMConfirm(sendParm);
        String selInsIbs =
                " SELECT SUBSTR (B.HIS_CTZ_CODE, 0, 1) AS CTZ1_CODE, B.SDISEASE_CODE  " +
                "   FROM INS_IBS A,INS_ADM_CONFIRM B " +
                "  WHERE A.CASE_NO = '" + caseNo + "' " +
                "    AND A.CASE_NO = B.CASE_NO ";
        TParm selInsIbsParm = new TParm(TJDODBTool.getInstance().select(
                selInsIbs));
        if (selInsIbsParm.getErrCode() < 0) {
            this.messageBox("" + selInsIbsParm.getErrName());
            return;
        }
        if (selInsIbsParm.getCount() <= 0) {
            this.messageBox("���걨����");
            return;
        }
        //1.��ְ 2.�Ǿ�
        sendParm.setData("INS_PAT_TYPE", selInsIbsParm.getValue("CTZ1_CODE", 0));
        //1.��ͨ 2.������
        sendParm.setData("SINGLE_TYPE",
                         selInsIbsParm.getValue("SDISEASE_CODE", 0).length() > 0 ?
                         2 : 1);
        sendParm.setData("INV_NO", this.getValueString("PRINT_NO"));
        TParm result = (TParm)this.openDialog(
                "%ROOT%\\config\\ins\\INSUpLoad.x", sendParm);
    }

    /**
     * �ֽ�֧��ʧȥ�����¼�
     */
    public void grabFocusPayCash() {
        setValue("PAY_BANK_CARD", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                  TCM_Transform.getDouble(getValue("PAY_DEBIT")) +
                  TCM_Transform.getDouble(getValue("DISCNT_AMT"))));
        this.grabFocus("PAY_BANK_CARD");

    }

    /**
     * ˢ��֧��ʧȥ�����¼�
     */
    public void grabFocusPayBankCard() {
        setValue("PAY_CHECK", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                  TCM_Transform.getDouble(getValue("PAY_DEBIT")) +
                  TCM_Transform.getDouble(getValue("DISCNT_AMT"))));
        this.grabFocus("PAY_CHECK");

    }

    /**
     * ֧Ʊ֧��ʧȥ�����¼�
     */
    public void grabFocusPayCheck() {
        setValue("PAY_DEBIT", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                  TCM_Transform.getDouble(getValue("DISCNT_AMT"))));
        this.grabFocus("PAY_DEBIT");

    }

    /**
     * ����֧��ʧȥ�����¼�
     */
    public void grabFocusPayDebit() {
        setValue("DISCNT_AMT", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("PAY_CASH")) +
                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                  TCM_Transform.getDouble(getValue("PAY_DEBIT"))));
        this.grabFocus("DISCNT_AMT");

    }

    /**
     * ����֧��ʧȥ�����¼�
     */
    public void grabFocusDiscntAmt() {
        setValue("PAY_CASH", TCM_Transform.getDouble(getValue("AR_AMT")) -
                 (TCM_Transform.getDouble(getValue("DISCNT_AMT")) +
                  TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                  TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                  TCM_Transform.getDouble(getValue("PAY_DEBIT"))));
        this.grabFocus("PAY_CASH");

    }

    /**
     * У��ϼƽ��
     * @return boolean
     */
    public boolean checkTotFee() {
        if (StringTool.round(TCM_Transform.getDouble(getValue("AR_AMT")) -
                             (TCM_Transform.getDouble(getValue("DISCNT_AMT")) +
                              TCM_Transform.getDouble(getValue("PAY_BANK_CARD")) +
                              TCM_Transform.getDouble(getValue("PAY_CHECK")) +
                              TCM_Transform.getDouble(getValue("PAY_DEBIT")) +
                              TCM_Transform.getDouble(getValue("PAY_CASH"))), 2
            ) != 0
                )
            return false;
        return true;
    }
         /** 
          * �����ʸ�ȷ����� 
          * @param parm 
          * @param type 
          * @return 
          * caowl
          */ 
      public String getConfirmNo(TParm parm, String type) { 
             String knConfirmNo = ""; 
             String confirmNo = ""; 
             for (int i = 0; i < parm.getCount("CONFIRM_NO"); i++) { 
               String s = ""+parm.getData("CONFIRM_NO", i); 
               if (s.startsWith("KN")) { 
                 knConfirmNo = s; 
               } 
               else { 
                 confirmNo = s; 
               } 
             } 
       
           if (type.equals("KN")) { 
               return knConfirmNo; 
             } 
             else { 
               return confirmNo; 
             } 
           } 

    /**
     * ���ҽ������====zhangp 20120412
     * @param caseNo String
     * @return TParm
     */
    public TParm getInsParm(String caseNo) {
    	
//    	System.out.println("getInsParm()");
        String sql = "SELECT CONFIRM_NO,SDISEASE_CODE FROM INS_ADM_CONFIRM WHERE CASE_NO = '" +
                     caseNo + "' AND IN_STATUS <> '5' ";
        TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (confirmParm.getErrCode() < 0) {
            messageBox("ҽ����ȡ����");
            return confirmParm;
        }
        if (confirmParm.getCount() < 0) {
//            messageBox("���ʸ�ȷ����");//=====wanglong delete 20140408 ��ҽ����������ʾ
            return confirmParm;
        }
        //caowl 20140107 start
//        System.out.println("confirmParm.getCount():"+confirmParm.getCount()); 
           int row  = getTTable("BillTable").getSelectedRow(); 
                 TParm parm  = getTTable("BillTable").getParmValue().getRow(row); 
//                 System.out.println("parm:"+parm); 
                 String enddate = "";  
                 String appdate = ""; 
                 String confirmNo = ""; 
//                 Timestamp yesterday = StringTool.rollDate( 
//                                         StringTool.getTimestamp(parm.getValue("END_DATE"), "yyyy-MM-dd HH:mm:ss"), -1); 
                 
//                 enddate  = StringTool.getString(yesterday, "yyyyMMdd") ; 
                 enddate = StringTool.getString(StringTool.getTimestamp(parm.getValue("END_DATE"), "yyyy-MM-dd HH:mm:ss"),"yyyyMMdd");
                 Timestamp sysDate = SystemTool.getInstance().getDate(); 
                 appdate = StringTool.getString(sysDate,"yyyyMMdd"); 
//                 System.out.println("enddate:"+enddate+"  "+appdate); 
                 int count = confirmParm.getCount("CONFIRM_NO"); 
              //ͬһ�꣬����confirmNo��¼Ϊ2�� ȡ��KN���ʸ�ȷ���� 
              if (enddate.substring(0, 4).equals(appdate.substring(0, 4)) && 
                     count > 1) { 
                   confirmNo = getConfirmNo(confirmParm, "KN"); 
                 } 
              //ȡ��KN���ʸ�ȷ���� 
              else { 
                   confirmNo = getConfirmNo(confirmParm, ""); 
                 } 
                 System.out.println("confirmNo:"+confirmNo); 
               //caowl 20140107 end
        if((""+confirmParm.getData("SDISEASE_CODE", 0)).length()==0)
        //��ְ
        {
        	sql = "SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
              " B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE,B.ARMYAI_AMT," +
              " ( CASE" +
              " WHEN TOT_PUBMANADD_AMT IS NULL" +
              " THEN 0" +
              " ELSE TOT_PUBMANADD_AMT" +
              " END" +
              " + B.NHI_PAY" +
//              " + CASE" +
//              " WHEN B.REFUSE_TOTAL_AMT IS NULL" +
//              " THEN 0" +
//              " ELSE B.REFUSE_TOTAL_AMT" +
//              " END" +
              " ) NHI_PAY," +
              " B.NHI_COMMENT," +
              " (  B.OWN_AMT" +
              " + B.ADD_AMT" +
              " + B.RESTART_STANDARD_AMT" +
              " + B.STARTPAY_OWN_AMT" +
              " + B.PERCOPAYMENT_RATE_AMT" +
              " + B.INS_HIGHLIMIT_AMT" +
              " - CASE" +
              " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
              " THEN 0" +
              " ELSE B.ACCOUNT_PAY_AMT" +
              " END" +
              " - CASE" +
              " WHEN ARMYAI_AMT IS NULL" +
              " THEN 0" +
              " ELSE ARMYAI_AMT" +
              " END" +
              " - CASE" +
              " WHEN ILLNESS_SUBSIDY_AMT IS NULL" +
              " THEN 0" +
              " ELSE ILLNESS_SUBSIDY_AMT" +
              " END" +
              " - CASE" +
              " WHEN TOT_PUBMANADD_AMT IS NULL" +
              " THEN 0" +
              " ELSE TOT_PUBMANADD_AMT" +
              " END" +
              " ) OWN_PAY," +
             // " (  B.NHI_COMMENT" +
             // " + B.NHI_PAY" +
             // " + B.OWN_AMT" +
             // " + B.ADD_AMT" +
             // " + B.RESTART_STANDARD_AMT" +
             //" + B.STARTPAY_OWN_AMT" +
             //" + B.PERCOPAYMENT_RATE_AMT" +
             // " + B.INS_HIGHLIMIT_AMT" +
             //" ) AR_AMT," +
              " (B.PHA_AMT  + B.EXM_AMT  + B.TREAT_AMT +  B.OP_AMT  +  B.BED_AMT  +" +
              " B.MATERIAL_AMT + B.OTHER_AMT + B.BLOODALL_AMT  +  B.BLOOD_AMT ) AR_AMT,"+
              " (CASE" +
              " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
              " THEN 0" +
              " ELSE B.ACCOUNT_PAY_AMT" +
              " END" +
              " ) ACCOUNT_PAY_AMT,B.ILLNESS_SUBSIDY_AMT,B.CONFIRM_NO," +
              " B.INS_CROWD_TYPE,A.IN_DATE,A.SDISEASE_CODE" +
              " FROM INS_ADM_CONFIRM A, INS_IBS B" +
              " WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
              //" AND A.CONFIRM_NO = '" + confirmParm.getData("CONFIRM_NO", 0) +
              " AND A.CONFIRM_NO = '" + confirmNo +"'" + //caowl 20140107
              " AND A.CONFIRM_NO =  B.CONFIRM_NO "+ //caowl 20140107
              " AND B.CASE_NO = '" + caseNo + "'" +
              " AND A.IN_STATUS IN ('1', '2', '3', '4')";
        }
        //������
        else
        {
        	sql = "SELECT A.INSBASE_LIMIT_BALANCE, A.INS_LIMIT_BALANCE," +
            " B.RESTART_STANDARD_AMT, A.REALOWN_RATE, A.INSOWN_RATE,B.ARMYAI_AMT," +
            " ( CASE" +
            " WHEN TOT_PUBMANADD_AMT IS NULL" +
            " THEN 0" +
            " ELSE TOT_PUBMANADD_AMT" +
            " END" +
            " + B.NHI_PAY + B.SINGLE_STANDARD_OWN_AMT - B.SINGLE_SUPPLYING_AMT" +
            " ) NHI_PAY," +
            " B.NHI_COMMENT," +
            " (  B.STARTPAY_OWN_AMT + B.BED_SINGLE_AMT + B.MATERIAL_SINGLE_AMT " +
            " + B.PERCOPAYMENT_RATE_AMT" +
            " + B.INS_HIGHLIMIT_AMT" +
            " - CASE" +
            " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
            " THEN 0" +
            " ELSE B.ACCOUNT_PAY_AMT" +
            " END" +
            " - CASE" +
            " WHEN ARMYAI_AMT IS NULL" +
            " THEN 0" +
            " ELSE ARMYAI_AMT" +
            " END" +
            " - CASE" +
            " WHEN ILLNESS_SUBSIDY_AMT IS NULL" +
            " THEN 0" +
            " ELSE ILLNESS_SUBSIDY_AMT" +
            " END" +
            " - CASE" +
            " WHEN TOT_PUBMANADD_AMT IS NULL" +
            " THEN 0" +
            " ELSE TOT_PUBMANADD_AMT" +
            " END" +
            " ) OWN_PAY," +
            //�����ֻ��߱���������ܷ��ò���
            " (B.PHA_AMT  + B.EXM_AMT  + B.TREAT_AMT +  B.OP_AMT  +  B.BED_AMT  + B.MATERIAL_AMT + B.OTHER_AMT + B.BLOODALL_AMT  +  B.BLOOD_AMT )  AS AR_AMT, "+
            " (CASE" +
            " WHEN B.ACCOUNT_PAY_AMT IS NULL" +
            " THEN 0" +
            " ELSE B.ACCOUNT_PAY_AMT" +
            " END" +
            " ) ACCOUNT_PAY_AMT,B.ILLNESS_SUBSIDY_AMT,B.CONFIRM_NO, " +
            " B.INS_CROWD_TYPE,A.IN_DATE,A.SDISEASE_CODE" +
            " FROM INS_ADM_CONFIRM A, INS_IBS B" +
            " WHERE B.REGION_CODE = '" + Operator.getRegion() + "'" +
            " AND A.CONFIRM_NO = '" + confirmParm.getData("CONFIRM_NO", 0) +
            "'" +
            " AND B.CASE_NO = '" + caseNo + "'" +
            " AND A.IN_STATUS IN ('1', '2', '3', '4')"; 
        }
//        System.out.println("У��ҽ���걨sql" + sql);
        TParm insParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (insParm.getErrCode() < 0) {
            messageBox("ҽ����ȡ����");
            return insParm;
        }
        if (insParm.getCount() < 0) {
            messageBox("��ҽ������");
            return insParm;
        }
        TParm result = new TParm();
        result = insParm.getRow(0);
        result.setCount(1);
        return result;
    }

    /**
     * Ϊҳ���ϵ�ҽ����ֵ
     * ===zhangp 20120412
     */
    public void setInsValue() {
    	//====zhangp 20130717 start
    	//TODO
//    	System.out.println("setInsValue()");
    	tjInsColumns = TJINSRecpTool.getInstance().getColumns();
    	insValueParm = new TParm();
    	TParm insParm = getInsParm(caseNo);
    	setTjInsValueParm("ARMYAI_AMT", insParm);//����
    	setTjInsValueParm("ACCOUNT_PAY_AMT", insParm);//�����˻�
    	setTjInsValueParm("NHI_COMMENT", insParm);//���
    	setTjInsValueParm("NHI_PAY", insParm);//ͳ��
    	setTjInsValueParm("ILLNESS_SUBSIDY_AMT", insParm);//����󲡽��
    	//====zhangp 20130717 end
        double armyAi_amt = insParm.getDouble("ARMYAI_AMT"); //����
        double illnesssubsidyamt = insParm.getDouble("ILLNESS_SUBSIDY_AMT"); //����󲡽��
        double account_pay_amt = insParm.getDouble("ACCOUNT_PAY_AMT"); //�����˻�
        double nhi_comment = insParm.getDouble("NHI_COMMENT"); //����
        double nhi_pay = insParm.getDouble("NHI_PAY")+armyAi_amt; //ͳ��(����ARMYAI_AMT����)
        double ins =  nhi_comment + nhi_pay + illnesssubsidyamt; //���ո���(��������󲡽��)
        double pay_cash = 0.00;
        if(insParm.getValue("INS_CROWD_TYPE").equals("3"))
              pay_cash = insParm.getDouble("AR_AMT")-nhi_comment
                         -nhi_pay-account_pay_amt; //�ֽ�֧��/�Ը�  	
        else{        	           
        	if(insParm.getValue("SDISEASE_CODE").length()==0)
        		 pay_cash = insParm.getDouble("OWN_PAY"); //�ֽ�֧��/�Ը�  
        	else{
        		String inDateSdisease = StringTool.getString(
            			insParm.getTimestamp("IN_DATE"), "yyyyMMdd");
        	   if (Integer.parseInt(inDateSdisease) < Integer.parseInt("20170101"))
                   pay_cash = insParm.getDouble("OWN_PAY"); //�ֽ�֧��/�Ը�  
        	   else
        	       pay_cash = insParm.getDouble("OWN_PAY")+
        	             insParm.getDouble("RESTART_STANDARD_AMT"); //�ֽ�֧��/�Ը�  	
        	}	
        }
             
        	
        if (insParm.getCount() > 0) {
            setValue("PAY_INS_CARD",
                     Math.abs(StringTool.round(
                             account_pay_amt,
                             2)));
            setValue("PAY_INS",
                     Math.abs(StringTool.round(nhi_pay+nhi_comment+illnesssubsidyamt,
                                               2)));
            setValue("AR_AMT",
                     Math.abs(StringTool.round(
                             pay_cash
                             , 2)));
            setValue("OWN_AMT",
                     Math.abs(StringTool.round(
                             pay_cash
                             , 2)));
            setValue("PAY_CASH",
                     Math.abs(StringTool.round(
                             pay_cash
                             , 2)));
        	setValue("tNumberTextField_1", Math.abs(StringTool.round(ins,2)));//caowl 20130318
        	
        }
    }
    /**
     * Ϊҳ���ϵ�ҽ����ֵ
     * ===zhangp 20120412
     */
    public void setInsValueXNH() {
    	tjInsColumns = TJINSRecpTool.getInstance().getColumns();
    	insValueParm = new TParm();
    	String sql = " SELECT TOT_AMT,OWN_AMT,REAL_INS_AMT AS NHI_PAY"+
    	             " FROM INS_XNH"+
    	             " WHERE CASE_NO = '"+ caseNo + "'";
    	 TParm result  = new TParm(TJDODBTool.getInstance().select(sql));
    	 TParm insParm = result.getRow(0);
    	 insParm.setCount(1);
//    	 System.out.println("insParm============"+insParm);
    	setTjInsValueParm("NHI_PAY", insParm);//ͳ��
        double nhi_pay = insParm.getDouble("NHI_PAY"); //ͳ��(ֱ�����)
        double ins =  nhi_pay; //���ո���
        double pay_cash = insParm.getDouble("OWN_AMT");//�Ը����  	
        if (insParm.getCount() > 0) {
            setValue("PAY_INS_CARD",
                     Math.abs(StringTool.round(0,2)));
            setValue("PAY_INS",
                     Math.abs(StringTool.round(nhi_pay,2)));
            setValue("AR_AMT",
                     Math.abs(StringTool.round(pay_cash, 2)));
            setValue("OWN_AMT",
                     Math.abs(StringTool.round(pay_cash, 2)));
            setValue("PAY_CASH",
                     Math.abs(StringTool.round(pay_cash, 2)));
        	setValue("tNumberTextField_1", Math.abs(StringTool.round(ins,2)));//caowl 20130318
        	
        }
    }
    /**
     * ���ҽ����ֵ
     * ===zhangp 20130717
     * @param name
     * @param insParm
     */
    private void setTjInsValueParm(String name,TParm insParm){
    	insValueParm.setData(tjInsColumns.getValue(name), insParm.getDouble(name));
    }
    /**
     * ȫѡ����
     */
    public void CheckedAll(){
    	TTable BillTable = (TTable) this.getComponent("BillTable");
    	TTable BillPayTable = (TTable) this.getComponent("BillPayTable");
		TParm billParm = BillTable.getParmValue();
		TParm billPayParm = BillPayTable.getParmValue();
		int billcount=billParm.getCount();
		int billpaycount=billPayParm.getCount();
    	if("Y".equals(this.getValueString("CHECKALL"))){
    		if(billcount>0){
    		for(int i=0;i<billcount;i++){
    			billParm.setData("PAY_SEL",i,"Y");
    		}}
    		if(billpaycount>0){
    		for(int i=0;i<billpaycount;i++){
    			billPayParm.setData("BILLPAY_SEL",i,"Y");
    		}
    		}
    	}else{
    		if(billcount>0){
    		for(int i=0;i<billcount;i++){
    			billParm.setData("PAY_SEL",i,"N");
    		}}
    		if(billpaycount>0){
    		for(int i=0;i<billpaycount;i++){
    			billPayParm.setData("BILLPAY_SEL",i,"N");
    		}
    		}
    	}
    	BillTable.setParmValue(billParm);
    	BillPayTable.setParmValue(billPayParm);
    	if(billcount>0){
    		BillTable.setSelectedRow(0);
    	}
    	if(billpaycount>0){
    		BillPayTable.setSelectedRow(0);
    	}
    	onBillTableComponent(this.getTTable("BillTable"));
    	onBillPayTableComponent(this.getTTable("BillPayTable"));
    }
}
