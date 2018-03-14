package com.javahis.ui.bil;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;

import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.bil.BILPayTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.sys.Operator;

import com.dongyang.util.TypeTool;

import jdo.sys.SystemTool;

import com.dongyang.manager.TIOM_AppServer;
import com.javahis.util.StringUtil;

import java.sql.Timestamp;

import com.dongyang.util.StringTool;

import jdo.adm.ADMTool;
import jdo.bil.BILInvoiceTool;
import jdo.ibs.IBSOrderdTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

import java.text.DecimalFormat;

/**
 *
 * <p>Title: Ԥ���������</p>
 *
 * <p>Description: Ԥ���������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILPayControl
    extends TControl {
    int selectrow = -1;
    TParm data = new TParm();
    String startInvno = "";
    /**
     * ��ʼ������
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj instanceof TParm) {
            //סԺADM����----------start---------------
            TParm admParm = (TParm) obj;
            TParm parm = new TParm();
            TParm UIParm = new TParm();
            String caseNo = "";
//            this.messageBox_("סԺ�Ӳ�"+admParm);
            if (admParm != null) {
                if (admParm.getData("IBS", "CASE_NO") != null)
                    caseNo = admParm.getData("IBS", "CASE_NO").toString();
                else
                    caseNo = admParm.getValue("CASE_NO");
//                this.messageBox_("סԺ�Ӳ�"+caseNo);
                parm.setData("CASE_NO", caseNo);
//                setValue("PRE_AMT",admParm.getValue("PRE_AMT"));
            }
            if (caseNo.length() != 0) {
                UIParm = BILPayTool.getInstance().seldataByCaseNo(parm);
                //����ؼ���ֵ
                initPage(UIParm);
                //���table
                this.callFunction("UI|MAINTABLE|removeRowAll");
                data = new TParm();
                //��ѯ����
                data = BILPayTool.getInstance().selectAllData(parm);
                //��������
                //��table���
                this.callFunction("UI|MAINTABLE|setParmValue", data);
            }
            //סԺADM����----------end---------------
        }
        //table1ֵ�ı��¼�
        this.addEventListener("MAINTABLE->" + TTableEvent.CHANGE_VALUE,
                              "onTableChangeValue");
        //table1�������¼�
        callFunction("UI|MAINTABLE|addEventListener",
                     "MAINTABLE->" + TTableEvent.CLICKED, this, "onTableClicked");
        //��ʼ������
        initiation();
    }
    /**
     * ����ǰTOOLBAR
     */
    public void onShowWindowsFunction() {
        //��ʾUIshowTopMenu
        callFunction("UI|showTopMenu");
    }

    /**
     *���Ӷ�Table�ļ���
     * @param row int
     */
    public void onTableClicked(int row) {
        //���������¼�
        this.callFunction("UI|MAINTABLE|acceptText");
        TParm data = (TParm) callFunction("UI|MAINTABLE|getParmValue");
        setValueForParm(
            "PRE_AMT;RECEIPT_NO;TRANSACT_TYPE;PAY_TYPE;CHECK_NO;REMARK",
            data, row); //�����Ϸ�
        //�Ƿ�ɴ�ӡ
        if (data.getData("REFUND_FLG", row).equals("Y"))
            callFunction("UI|print|setEnabled", false);
        else
            callFunction("UI|print|setEnabled", true);
        //�Ƿ���˷�
        if (data.getData("REFUND_FLG", row).equals("Y") ||
            data.getData("TRANSACT_TYPE", row).equals("02"))
            callFunction("UI|returnFee|setEnabled", false);
        else
            callFunction("UI|returnFee|setEnabled", true);
        selectrow = row;
        callFunction("UI|delete|setEnabled", true);
    }

    /**
     * ����ID�Ų�ѯ(ʵ�ʲ鵱ǰID�ŵ����������)
     */
    public void onQueryByCaseNo() {
        TParm parm = new TParm();
        String mrNo = PatTool.getInstance().checkMrno(getValue("MR_NO").
            toString());
        
      //modify by huangtt 20160928 EMPI���߲�����ʾ  start
        Pat pat = Pat.onQueryByMrNo(mrNo);        
        if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
            mrNo = pat.getMrNo();
        }
        //modify by huangtt 20160928 EMPI���߲�����ʾ  end
        
        
        setValue("MR_NO", mrNo);
        parm.setData("MR_NO", mrNo);
        String caseNo = (BILPayTool.getInstance().selectPatCaseNo(parm)).
            getValue("CASE_NO", 0);
        setValue("CASE_NO", caseNo);
        if (caseNo == null) {
            this.messageBox_("�޴˾����");
            return;
        }
        parm.setData("CASE_NO", caseNo);
        TParm UIParm = new TParm();
        UIParm = BILPayTool.getInstance().seldataByCaseNo(parm);
//        stationCode = UIParm.getValue("STATION_CODE", 0);
        //����ؼ���ֵ
        initPage(UIParm);
        //���table
        this.callFunction("UI|MAINTABLE|removeRowAll");
        data = new TParm();
        //��ѯ����
        data = BILPayTool.getInstance().selectAllData(parm);
        //��������
        //��table���
        this.callFunction("UI|MAINTABLE|setParmValue", data);
    }

    /**
     * ����סԺ�Ų�ѯ����
     */
    public void onQueryByIpdNo() {
        TParm parm = new TParm();
        String ipdNo = PatTool.getInstance().checkMrno(getValue("IPD_NO").
            toString());
        setValue("IPD_NO", ipdNo);
        if (ipdNo == null) {
            this.messageBox_("�޴�סԺ��");
            return;
        }
        //���table
        this.callFunction("UI|MAINTABLE|removeRowAll");
        parm.setData("IPD_NO", ipdNo);
        TParm UIParm = new TParm();
        UIParm = BILPayTool.getInstance().seldataByIpdNo(parm);
//        stationCode = UIParm.getValue("STATION_CODE", 0);
        //����ؼ���ֵ
        initPage(UIParm);
        data = new TParm();
        //��ѯ����
        data = BILPayTool.getInstance().selectAllData(parm);
        //��������
        //��table���
        this.callFunction("UI|MAINTABLE|setParmValue", data);
    }

    /**
     * �����¼�(��Ԥ����)
     */
    public void onSave() {
    	//===zhangp 20120731 start
//        if (!checkNo()) {
    	BilInvoice bilInvoice = new BilInvoice();
       	if (!initBilInvoice(bilInvoice.initBilInvoice("PAY"))) {
//            this.messageBox("��δ����,���ȿ���");
            return;
        }
       	//===zhangp 20120731 end
        if (this.getValueString("MR_NO") == null ||
            this.getValueString("MR_NO").length() == 0) {
            this.messageBox("�����벡����");
            return;
        }
        if (getValue("PRE_AMT") == null ||
            TypeTool.getDouble(getValue("PRE_AMT")) <= 0) {
            this.messageBox("����������!");
            return;
        }
        //���������ֵ
        if (!this.emptyTextCheck("PAY_TYPE"))
            return;
        TParm parm = getParmForTag(
            "ADM_TYPE;YELLOW_SIGN:int;RED_SIGN:int;GREENPATH_VALUE:int;MR_NO;" +
            "IPD_NO;CASE_NO;BED_NO;PAT_NAME;SEX_CODE;DEPT_CODE;STATION_CODE;PRE_AMT:double;" +
            "PAY_TYPE;CHECK_NO;REMARK");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASHIER_CODE", Operator.getID());
        parm.setData("REFUND_FLG", "N");
        parm.setData("TRANSACT_TYPE", "01");
        parm.setData("START_INVNO", startInvno);
//        parm.setData("INV_NO", getValue("PRINT_NO"));
        bilInvoice = bilInvoice.initBilInvoice("PAY");      
        parm.setData("INV_NO", bilInvoice.getUpdateNo());        
        parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction("action.bil.BILPayAction",
            "onBillPay", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return;
        }
        parm.setData("RECEIPT_NO", result.getValue("RECEIPT_NO", 0));
        setValue("RECEIPT_NO", result.getValue("RECEIPT_NO", 0));
        //У���Ƿ�ֹͣ����
        TParm checkStopFee = ADMTool.getInstance().checkStopFee(this.
            getValueString("CASE_NO"));
        if (checkStopFee.getErrCode() < 0) {
            err(checkStopFee.getErrCode() + " " + checkStopFee.getErrText());
            return;
        }

        //table�ϼ���������������ʾ
        callFunction("UI|MAINTABLE|addRow", parm,
                     "RECEIPT_NO;CHARGE_DATE;PRE_AMT;TRANSACT_TYPE;PAY_TYPE;CHECK_NO;REMARK;CASHIER_CODE;" +
                     "REFUND_FLG;REFUND_CODE;REFUND_DATE;RESET_RECP_NO");
        //��ӡԤ�����վ�
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
            getDouble(getValue("PRE_AMT")));
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
        forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
//        forPrtParm.setData("Data", "TEXT", "����:" + pDate);
        forPrtParm.setData("Data", "TEXT",  pDate);
        //forPrtParm.setData("Name", "TEXT", "����:" + getValue("PAT_NAME"));
        forPrtParm.setData("Name", "TEXT", getValue("PAT_NAME"));
        //forPrtParm.setData("TOLL_COLLECTOR", "TEXT", "�շ�Ա:" + Operator.getName());
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
        //forPrtParm.setData("MR_N0", "TEXT", "������:" + this.getValue("MR_NO"));
        forPrtParm.setData("MR_N0", "TEXT", this.getValue("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT", "סԺ��:" + this.getValue("IPD_NO"));
        String sexCode = this.getValueString("SEX_CODE");
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "��");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "Ů");
        else
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "����");
//        forPrtParm.setData("DEPT", "TEXT",
//                           "�������:" + getDeptDesc( (String) getValue("DEPT_CODE")));
      forPrtParm.setData("DEPT", "TEXT",getDeptDesc( (String) getValue("DEPT_CODE"))+ "        ����:" + getStationDesc(this.getValueString("STATION_CODE")));

//        forPrtParm.setData("STATION", "TEXT",
//                           "���ﲡ��:" + getStationDesc(this.getValueString("STATION_CODE")));
        forPrtParm.setData("Capital", "TEXT",  bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",formatObject.format(
                           TypeTool.getDouble(this.getValueString("PRE_AMT"))));
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "��ˮ��:" + this.getValueString("RECEIPT_NO"));
        forPrtParm.setData("PRINT_DATE", "TEXT", "��ӡ����:" + pDate);
        if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {
//            forPrtParm.setData("CASH", "TEXT", "��");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "");
        	forPrtParm.setData("WAY","TEXT","�ֽ�") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "��");
//            forPrtParm.setData("OTHERS", "TEXT", "");
                forPrtParm.setData("WAY","TEXT","���п�") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "��");
//            forPrtParm.setData("OTHERS", "TEXT", "");
                forPrtParm.setData("WAY","TEXT","֧Ʊ") ;
        }
        else {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "��");
        	forPrtParm.setData("WAY","TEXT","����") ;
        }
        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment.jhw",
                             forPrtParm,true);
        this.onClear();
    }

    /**
     * ��ӡ
     */
    public void onPrint() {

        TTable table = (TTable)this.getComponent("MAINTABLE");
        if (table.getSelectedRow() < 0) {
            this.messageBox("���ѡtable����");
            return;
        }
        TParm onREGReprintParm = new TParm();
        onREGReprintParm.setData("CASE_NO", this.getValueString("CASE_NO"));
        onREGReprintParm.setData("RECEIPT_NO", this.getValueString("RECEIPT_NO"));
        onREGReprintParm.setData("OPT_USER", Operator.getID());
        onREGReprintParm.setData("OPT_TERM", Operator.getIP());
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.bil.BILPayAction",
                                              "onBilPayReprint",
                                              onREGReprintParm);
        if (result.getErrCode() < 0) {
            err(result.getErrName());
            return;
        }
        //��ӡԤ�����վ�
        TParm forPrtParm = new TParm();
        String bilPayC = StringUtil.getInstance().numberToWord(TypeTool.
            getDouble(getValue("PRE_AMT")));
        Timestamp printDate = (SystemTool.getInstance().getDate());
        String pDate = StringTool.getString(printDate, "yyyy/MM/dd HH:mm:ss");
        forPrtParm.setData("COPY", "TEXT", "");
        forPrtParm.setData("REGION_CHN_DESC", "TEXT",
                           Operator.getHospitalCHNFullName());
        forPrtParm.setData("REGION_ENG_DESC", "TEXT",
                           Operator.getHospitalENGFullName());
        int row = (Integer) callFunction("UI|MAINTABLE|getSelectedRow");
        if (row < 0)
            return;
        TParm parm = (TParm) callFunction("UI|MAINTABLE|getParmValue");
        forPrtParm.setData("Data", "TEXT",StringTool.getString(parm.
                                                getTimestamp("CHARGE_DATE", row),
                                                "yyyy/MM/dd HH:mm:ss"));
        forPrtParm.setData("CHECK_NO","TEXT",this.getValue("CHECK_NO")) ;
        forPrtParm.setData("REMARK","TEXT",this.getValue("REMARK")) ;
        forPrtParm.setData("Name", "TEXT", getValue("PAT_NAME"));
        forPrtParm.setData("TOLL_COLLECTOR", "TEXT", Operator.getID());
        forPrtParm.setData("MR_N0", "TEXT", this.getValue("MR_NO"));
        forPrtParm.setData("IPD_NO", "TEXT", "סԺ��:" + this.getValue("IPD_NO"));
        String sexCode = this.getValueString("SEX_CODE");
        if (sexCode.equals("1"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "��");
        else if (sexCode.equals("2"))
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "Ů");
        else
            forPrtParm.setData("SEX", "TEXT", "�Ա�:" + "����");
        forPrtParm.setData("DEPT", "TEXT", getDeptDesc( (String) getValue("DEPT_CODE")) +"      ����:" + getStationDesc(this.getValueString("STATION_CODE")));
//        forPrtParm.setData("STATION", "TEXT",
//                           "���ﲡ��:" + getStationDesc(this.getValueString("STATION_CODE")));
        forPrtParm.setData("Capital", "TEXT", bilPayC);
        DecimalFormat formatObject = new DecimalFormat("###########0.00");

        forPrtParm.setData("SmallCaps", "TEXT",formatObject.format(
                           TypeTool.getDouble(this.getValueString("PRE_AMT"))));
        forPrtParm.setData("SEQ_NO", "TEXT",
                           "��ˮ��:" + this.getValueString("RECEIPT_NO"));
        forPrtParm.setData("PRINT_DATE", "TEXT", "��ӡ����:" + pDate);
        if (this.getValueString("PAY_TYPE").equals("PAY_CASH")) {
//            forPrtParm.setData("CASH", "TEXT", "��");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "");
        	forPrtParm.setData("WAY","TEXT","�ֽ�") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_BANK_CARD")) {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "��");
//            forPrtParm.setData("OTHERS", "TEXT", "");
            forPrtParm.setData("WAY","TEXT","���п�") ;
        }
        else if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")) {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "��");
//            forPrtParm.setData("OTHERS", "TEXT", "");
                forPrtParm.setData("WAY","TEXT","֧Ʊ") ;
        }
        else {
//            forPrtParm.setData("CASH", "TEXT", "");
//            forPrtParm.setData("BANK", "TEXT", "");
//            forPrtParm.setData("OTHERS", "TEXT", "��");
        	forPrtParm.setData("WAY","TEXT","����") ;
        }

        this.openPrintWindow("%ROOT%\\config\\prt\\BIL\\BILPrepayment.jhw",
                             forPrtParm,true);
        this.onClear();
    }

    /**
     * �˷�(��Ԥ����)
     */
    public void onReturnFee() {
        int row = (Integer) callFunction("UI|MAINTABLE|getSelectedRow");
        if (row < 0)
            return;
        TParm parm = (TParm) callFunction("UI|MAINTABLE|getParmValue");
        String receiptNo = parm.getValue("RECEIPT_NO", row);
        TParm actionParm = new TParm();
        actionParm = BILPayTool.getInstance().selAllDataByRecpNo(receiptNo);
        TParm endParm = new TParm();
        endParm = actionParm.getRow(0);
        endParm.setData("RESET_BIL_PAY_NO", parm.getData("RECEIPT_NO", row));
        endParm.setData("REFUND_CODE", Operator.getID());
        //==============chenxi   2012.05.29
        endParm.setData("CASHIER_CODE", Operator.getID());
        endParm.setData("CHARGE_DATE", SystemTool.getInstance().getDate());
        //================chenxi    2012.05.29
        endParm.setData("REFUND_DATE", SystemTool.getInstance().getDate());
        endParm.setData("OPT_USER", Operator.getID());
        endParm.setData("OPT_TERM", Operator.getIP());
        TParm endDate = TIOM_AppServer.executeAction("action.bil.BILPayAction",
            "onReturnBillPay", endParm);
        if (endDate.getErrCode() < 0) {
            err(endDate.getErrCode() + " " + endDate.getErrText());
            return;
        }
        this.messageBox("P0005");
        //��ѯ����
        TParm allParm = new TParm();
        TParm allForParm = new TParm();
        allForParm.setData("IPD_NO", getValue("IPD_NO"));
        allParm = BILPayTool.getInstance().selectAllData(allForParm);
        //���table
        this.callFunction("UI|MAINTABLE|removeRowAll");
        //��������
        //��table���
        this.callFunction("UI|MAINTABLE|setParmValue", allParm);
    }

    /**
     * ���
     */
    public void onClear() {
        clearValue("ADM_TYPE;YELLOW_SIGN;RED_SIGN;GREENPATH_VALUE;MR_NO;IPD_NO;CASE_NO;BED_NO;PAT_NAME;SEX_CODE;" +
                   "DEPT_CODE;STATION_CODE;PRE_AMT;LEFT_BILPAY;SUM_TOTAL;RECEIPT_NO;CHECK_NO;REMARK;TRANSACT_TYPE");
        initiation();
        this.callFunction("UI|MAINTABLE|removeRowAll");
    }

    /**
     * ֧����ʽ�ı��¼�
     */
    public void payTypeChange() {
        if (getValue("PAY_TYPE").toString().equals("PAY_CHECK")) {
            this.callFunction("UI|CHECK_NO|setEnabled", true);
            this.callFunction("UI|REMARK|setEnabled", true);
        }
        else {
            this.callFunction("UI|CHECK_NO|setEnabled", false);
            this.callFunction("UI|REMARK|setEnabled", false);
        }
    }

    /**
     * ��ѯ�����ؼ���ֵ
     * @param parm TParm
     */
    public void initPage(TParm parm) {
    //=====add by kangy 20170824 ���úϼƴ�IBS_ORDD����ܻ��
    			TParm rexpParm = new TParm();
    			rexpParm.setData("CASE_NO", parm.getValue("CASE_NO",0));
    			TParm rexpData = new TParm();
    			rexpData = IBSOrderdTool.getInstance().selectdataAll(rexpParm);
    			double sunTotAmt = 0.00;
    			for (int i = 0; i < rexpData.getCount(); i++) {
    				double totAmt = 0.00;
    				totAmt = rexpData.getDouble("AR_AMT", i);
    				sunTotAmt = sunTotAmt + totAmt;
    			}
        setValue("ADM_TYPE", "I");
        setValue("YELLOW_SIGN", parm.getDouble("YELLOW_SIGN", 0));
        setValue("RED_SIGN", parm.getDouble("RED_SIGN", 0));
        setValue("GREENPATH_VALUE", parm.getDouble("GREENPATH_VALUE", 0));
        setValue("MR_NO", parm.getValue("MR_NO", 0));
        setValue("IPD_NO", parm.getValue("IPD_NO", 0));
        setValue("CASE_NO", parm.getValue("CASE_NO", 0));
        setValue("BED_NO", parm.getValue("BED_NO", 0));
        setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
        setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));
        setValue("DEPT_CODE", parm.getValue("DEPT_CODE", 0));
        setValue("STATION_CODE", parm.getValue("STATION_CODE", 0));
        setValue("LEFT_BILPAY", parm.getDouble("TOTAL_BILPAY", 0)-sunTotAmt);
        setValue("SUM_TOTAL", parm.getValue("CUR_AMT", 0));
    }

//    /**
//     * У�鿪����
//     * @return boolean
//     */
//    public boolean checkNo() {
//        TParm parm = new TParm();
//        parm.setData("RECP_TYPE", "PAY");
//        parm.setData("CASHIER_CODE", Operator.getID());
//        parm.setData("STATUS", "0");
//        parm.setData("TERM_IP", Operator.getIP());
//        TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
//        String updateNo = noParm.getValue("UPDATE_NO", 0);
//        startInvno = "";
//        startInvno = noParm.getValue("START_INVNO", 0);
//        if (updateNo == null || updateNo.length() == 0) {
//            return false;
//        }
//
//        setValue("PRINT_NO", updateNo);
//        return true;
//    }
    
	/**
	 * ��ʼ��Ʊ��
	 * 
	 * ===zhangp 20120731
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// ��˿�����
		if (bilInvoice == null) {
			this.messageBox_("����δ����!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("�޿ɴ�ӡ��Ʊ��!");
			return false;
		}
		// ��˵�ǰƱ��
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("���һ��Ʊ��!");
		}
		if (BILTool.getInstance().compareUpdateNo("PAY", Operator.getID(),
				Operator.getRegion(), bilInvoice.getUpdateNo())) {
			setValue("PRINT_NO", bilInvoice.getUpdateNo());
			System.out.println("==="+bilInvoice.getParm());
			startInvno = bilInvoice.getStartInvno();//===zhangp 20120821
		} else {
			messageBox("Ʊ��������");
			return false;
		}
		return true;
	}

    /**
     * ��ʼ�����ݷ���
     */
	public void initiation() {
		setValue("ADM_TYPE", "I");
		this.setValue("PAY_TYPE", "PAY_CASH");
		// ��ʾƱ��
		// ===zhangp 20120731 start
		// if (!checkNo()) {
		BilInvoice bilInvoice = new BilInvoice();
		initBilInvoice(bilInvoice.initBilInvoice("PAY"));
		// this.checkNo();
		// ===zhangp 20120731 end
	}

    /**
     * �õ���������
     * @param deptCode String
     * @return String
     */
    public String getDeptDesc(String deptCode) {
        String deptDesc = "";
        String selDept =
            " SELECT DEPT_CHN_DESC " +
            "   FROM SYS_DEPT " +
            "  WHERE DEPT_CODE = '" + deptCode + "' ";
//        System.out.println("selDept"+selDept);
        //��ѯ��������
        TParm selDeptParm = new TParm(TJDODBTool.getInstance().select(
            selDept));
        deptDesc = selDeptParm.getValue("DEPT_CHN_DESC", 0);
//        System.out.println("deptDesc"+deptDesc);
        return deptDesc;
    }

    /**
     * �õ���������
     * @param stationCode String
     * @return String
     */
    public String getStationDesc(String stationCode) {
        String stationDesc = "";
        String selStation =
            " SELECT STATION_DESC " +
            "   FROM SYS_STATION " +
            "  WHERE STATION_CODE = '" + stationCode + "' ";
//        System.out.println("selStation"+selStation);
        //��ѯ��������
        TParm selStationParm = new TParm(TJDODBTool.getInstance().select(
            selStation));
        stationDesc = selStationParm.getValue("STATION_DESC", 0);
//        System.out.println("stationDesc"+stationDesc);
        return stationDesc;
    }

    public static void main(String args[]) {
        com.javahis.util.JavaHisDebug.TBuilder();

    }
}
