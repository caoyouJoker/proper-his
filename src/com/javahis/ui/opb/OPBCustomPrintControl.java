package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.bil.BILInvoiceTool;
import jdo.bil.BilInvoice;
import jdo.hrm.HRMBill;
import jdo.hrm.HRMCompanyTool;
import jdo.hrm.HRMInvRcp;
import jdo.hrm.HRMOpbReceipt;
import jdo.opb.OPBReceiptTool;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatHRMCompany;
import com.javahis.system.textFormat.TextFormatHRMContractm;
import com.javahis.util.StringUtil;

/**
 * <p> Title: �Զ����ӡ </p>
 * 
 * <p> Description: �Զ����ӡ </p>
 * 
 * <p> Copyright: Copyright (c) Liu dongyang 2008 </p>
 * 
 * <p> Company: JavaHis </p>
 * 
 * @author wangl 2010.11.20
 * @version 1.0
 */
public class OPBCustomPrintControl
        extends TControl {

    TTable table;
    private String admType = "O";
    String printNoOnly;
    private HRMInvRcp invRcp; // ��Ʊ����

    private String receiptNo="";
    private double charge1 = 0;
    // private double charge2 = 0;
    private double charge3 = 0;
    private double charge4 = 0;
    private double charge5 = 0;
    private double charge6 = 0;
    private double charge7 = 0;
    private double charge8 = 0;
    private double charge9 = 0;
    private double charge10 = 0;
    private double charge11 = 0;// ����
    // private double charge12 = 0;//����ҽ��
    private double charge13 = 0;
    private double charge14 = 0;
    private double charge15 = 0;
    private double charge16 = 0;
    private double charge17 = 0;
    private double charge18 = 0;
    private double charge19 = 0;//����
    private double arAmt = 0;// ʵ�ս��
    private double reduceAmt = 0;//�������
    private boolean flg=false;
    private boolean puhua_flg = false;//�Ƿ�Ϊ�ջ�Ȩ��
    /**
     * ��ʼ��
     */
    public void onInit() {
        super.onInit();
        Object obj = this.getParameter();
        if (obj != null) {
            this.admType = obj.toString();
        }
        table = (TTable) this.getComponent("TABLE");
        // �˵�tableר�õļ���
        table.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");
        initPage();
        invRcp = new HRMInvRcp(); 
        if(this.getPopedem("PUHUA")){
        	puhua_flg = true;
        }
    }

    /**
     * ��ʼ����������
     */
    public void initPage() {
        setUIInv();//���ý����ϵ�ǰ��Ʊ��
        if ("O".equals(this.admType)) {// modify by wanglong 20130121
            ((TRadioButton) this.getComponent("Company")).setEnabled(false);
            ((TextFormatHRMCompany) this.getComponent("COMPANY_CODE")).setEnabled(false);
            ((TextFormatHRMContractm) this.getComponent("CONTRACT_CODE")).setEnabled(false);
            ((TCheckBox) this.getComponent("MODIF")).setEnabled(false);
            ((TCheckBox) this.getComponent("MODIFD")).setEnabled(false);
        }
        if ("H".equals(this.admType) && this.getValue("Single").equals("Y")) {// modify by wanglong 20130121
            ((TextFormatHRMCompany) this.getComponent("COMPANY_CODE")).setEnabled(false);
            ((TextFormatHRMContractm) this.getComponent("CONTRACT_CODE")).setEnabled(false);
            ((TCheckBox) this.getComponent("MODIF")).setEnabled(true);
            ((TCheckBox) this.getComponent("MODIFD")).setEnabled(true);
        }
        Timestamp sysDate = SystemTool.getInstance().getDate();
        String s = StringTool.getString(sysDate, "yyyyMMdd") + "000000";
        String e = StringTool.getString(sysDate, "yyyyMMdd") + "235959";
        Timestamp sDate = StringTool.getTimestamp(s, "yyyyMMddHHmmss");
        Timestamp eDate = StringTool.getTimestamp(e, "yyyyMMddHHmmss");
        this.setValue("S_DATE", sDate);
        this.setValue("E_DATE", eDate);
    }

    /**
     * �˵�table�����¼�
     * 
     * @param obj
     *            Object
     * @return boolean
     */
    public boolean onTableComponent(Object obj) {
        TTable printTable = (TTable) obj;
        printTable.acceptText();
        int row = printTable.getSelectedRow();
        // �õ���ǰѡ��������
        for (int i = 0; i < printTable.getRowCount(); i++) {
            printTable.setValueAt("N", i, 0);
        }
        printTable.setValueAt("Y", row, 0);
        printTable.acceptText();
        return true;
    }

    /**
     * ��������ѡ���¼�
     * 
     * @param obj
     *            Object
     */
    public void onChoose(Object obj) {
        if ("1".equals(obj.toString())) {
            table.setHeader("ѡ,40,boolean;Ʊ�ݺ�,100;Ʊ��״̬,80;������,120;�������,120;�ż���,80,ADM_TYPE;�վ�����,120;�վ��ܽ��,200,double,#########0.00");
            this.clearValue("COMPANY_CODE;CONTRACT_CODE;COMPANY_DNAME;CONTRACTN_CODE;MODIFD;MODIF");
            callFunction("UI|tLabel_patName|setText", "����:");
            if ("H".equals(this.admType)) {// add by wanglong 20130121
                ((TTextField) this.getComponent("MR_NO")).setEnabled(true);
                ((TextFormatHRMCompany) this.getComponent("COMPANY_CODE")).setEnabled(false);
                ((TextFormatHRMContractm) this.getComponent("CONTRACT_CODE")).setEnabled(false);
            }
        }
        if ("2".equals(obj.toString())) {
            table.setHeader("ѡ,40,boolean;Ʊ�ݺ�,100;Ʊ��״̬,80;��˾����,180,COMPANY_CODE;��ͬ����,180,CONTRACT_CODE;�ż���,80,ADM_TYPE;�վ�����,120;�վ��ܽ��,200,double,#########0.00");
            this.clearValue("MR_NO;S_PAT_NAME");
            table.setItem("FLG;MR_NO;CASE_NO;ADM_TYPE;BILL_DATE;AR_AMT;PRINT_NO;RECEIPT_NO");
            callFunction("UI|tLabel_patName|setText", "��������:");
            if ("H".equals(this.admType)) {// add by wanglong 20130121
                ((TTextField) this.getComponent("MR_NO")).setEnabled(false);
                TextFormatHRMCompany company = ((TextFormatHRMCompany) this.getComponent("COMPANY_CODE"));
                company.setEnabled(true);
                ((TextFormatHRMContractm) this.getComponent("CONTRACT_CODE")).setEnabled(true);
                
                // add by wangb 2016/06/23 ����������Ҫ���ݲ�ͬ��¼��ɫɸѡ
        		String roleType = "";
        		if (this.getPopedem("SYSDBA")) {
        			roleType = HRMCompanyTool.ALL_ROLE_TYPE.replace(",", "','");
        		} else {
        			roleType = this.getPopedemParm().getValue("ID", 0);
        		}
                
        		// ��ѯ������Ϣ
        		TParm companyData = HRMCompanyTool.getInstance().selectCompanyComboByRoleType(roleType);
        		company.setPopupMenuData(companyData);
                company.setComboSelectRow();
                company.popupMenuShowData();
                
                TTextFormat contract = (TTextFormat) this.getComponent("CONTRACT_CODE");
                contract.getPopupMenuData().getData().clear();
                contract.filter();
            }
        }
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNo() {
        queryPatName();
        onQuery();
    }

    /**
     * ���ݲ����Ų�ѯ����
     */
    public void queryPatName() {
        String mrNo = this.getValueString("MR_NO");
        if (mrNo.trim().equals("")) {
            return;
        }
        // modify by huangtt 20160929 EMPI���߲�����ʾ start
//        TParm parm = PatTool.getInstance().getInfoForMrno(mrNo);
//        mrNo = parm.getValue("MR_NO", 0);
//        if (parm.getErrCode() < 0 || mrNo.equals("")) {
//            messageBox("�����Ų�����");
//            onClear();
//            return;
//        }
//        String parName = parm.getValue("PAT_NAME", 0);
        
        mrNo =  PatTool.getInstance().checkMrno(mrNo);// �����Ų��볤��
        this.setValue("MR_NO", mrNo);        
        Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
		}
        
		String parName = pat.getName();
		 // modify by huangtt 20160929 EMPI���߲�����ʾ end
        this.setValue("MR_NO", mrNo);
        this.setValue("S_PAT_NAME", parName);
    }

    /**
     * �޸�ѡ���¼�
     * 
     * @param obj
     *            Object
     */
    public void onSel(Object obj) {
        if ("1".equals(obj.toString())) {
            if ("Y".equals(this.getValueString("MODIF"))) {
                ((TTextField) this.getComponent("COMPANY_DNAME")).setEnabled(true);
            } else {
                ((TTextField) this.getComponent("COMPANY_DNAME")).setEnabled(false);
            }
        }
        if ("2".equals(obj.toString())) {
            if ("Y".equals(this.getValueString("MODIFD"))) {
                ((TTextField) this.getComponent("CONTRACTN_CODE")).setEnabled(true);
            } else {
                ((TTextField) this.getComponent("CONTRACTN_CODE")).setEnabled(false);
            }
        }
    }

    /**
     * ȫѡ�¼�
     */
//    public void onSelectAll() {
//        String select = getValueString("SEL_ALL");
//        TParm parm = table.getParmValue();
//        int count = parm.getCount();
//        for (int i = 0; i < count; i++) {
//            parm.setData("FLG", i, select);
//        }
//        table.setParmValue(parm);
//        table.setSelectedRow(0);// add by wanglong 20130121
//    }

    /**
     * �Զ��������ý��
     */
    public void onCustom() {
        checkData();
        double chg1 = this.getValueDouble("CHARGE01");
        // double chg2 = this.getValueDouble("CHARGE02");//�ǿ�����+������=��ҩ
        double chg3 = this.getValueDouble("CHARGE03");
        double chg4 = this.getValueDouble("CHARGE04");
        double chg5 = this.getValueDouble("CHARGE05");
        double chg6 = this.getValueDouble("CHARGE06");
        double chg7 = this.getValueDouble("CHARGE07");
        double chg8 = this.getValueDouble("CHARGE08");
        double chg9 = this.getValueDouble("CHARGE09");
        double chg10 = this.getValueDouble("CHARGE10");
        double chg11 = this.getValueDouble("CHARGE11");// �Զ������
        // double chg12 = this.getValueDouble("CHARGE12");//����ҽ�ƣ���ʱû�У�
        double chg13 = this.getValueDouble("CHARGE13");
        double chg14 = this.getValueDouble("CHARGE14");
        double chg15 = this.getValueDouble("CHARGE15");
        double chg16 = this.getValueDouble("CHARGE16");
        double chg17 = this.getValueDouble("CHARGE17");
        double chg18 = this.getValueDouble("CHARGE18");
        double chg19 = this.getValueDouble("CHARGE19");
        double exAmt = 0;
        exAmt +=
                chg1 + chg3 + chg4 + chg5 + chg6 + chg7 + chg8 + chg9 + chg10 + chg13 + chg14
                        + chg15 + chg16 + chg17 + chg18 + chg19;
        if ((exAmt + chg11) > arAmt) {
            this.messageBox("����ֵ����");
            this.setValue("CHARGE11", charge11);
            exAmt += charge11;
        }else{
            exAmt += chg11;
        }
        this.setValue("REDUCE_AMT", arAmt + reduceAmt - exAmt);// modify by wanglong 20130121
        this.setValue("AR_AMT", exAmt);
        this.setValue("AMT_TO_WORD", StringUtil.getInstance().numberToWord(exAmt));
        callFunction("UI|REDUCE_AMT|setEnabled", false);//add by wanglong 20131104
    }

    /**
     * �޸��Զ�����õĽ��
     */
    public void onCustomMerge() {// add by wanglong 20131104
        checkData();
        double chg1 = this.getValueDouble("CHARGE01");
        // double chg2 = this.getValueDouble("CHARGE02");//�ǿ�����+������=��ҩ
        double chg3 = this.getValueDouble("CHARGE03");
        double chg4 = this.getValueDouble("CHARGE04");
        double chg5 = this.getValueDouble("CHARGE05");
        double chg6 = this.getValueDouble("CHARGE06");
        double chg7 = this.getValueDouble("CHARGE07");
        double chg8 = this.getValueDouble("CHARGE08");
        double chg9 = this.getValueDouble("CHARGE09");
        double chg10 = this.getValueDouble("CHARGE10");
        double chg11 = this.getValueDouble("CHARGE11");// �Զ������
        // double chg12 = this.getValueDouble("CHARGE12");//����ҽ�ƣ���ʱû�У�
        double chg13 = this.getValueDouble("CHARGE13");
        double chg14 = this.getValueDouble("CHARGE14");
        double chg15 = this.getValueDouble("CHARGE15");
        double chg16 = this.getValueDouble("CHARGE16");
        double chg17 = this.getValueDouble("CHARGE17");
        double chg18 = this.getValueDouble("CHARGE18");
        double chg19 = this.getValueDouble("CHARGE19");
        if (chg1 == 0 && chg3 == 0 && chg4 == 0 && chg5 == 0 && chg6 == 0 && chg7 == 0 && chg8 == 0
                && chg9 == 0 && chg10 == 0 && chg13 == 0 && chg14 == 0 && chg15 == 0 && chg16 == 0
                && chg17 == 0 && chg18 == 0 && chg19 == 0) {
            double newReduceAmt = this.getValueDouble("REDUCE_AMT");
            double exAmt = arAmt + reduceAmt - newReduceAmt;
            this.setValue("AR_AMT", exAmt);
            this.setValue("AMT_TO_WORD", StringUtil.getInstance().numberToWord(exAmt));
            this.setValue("CHARGE11", exAmt);
        }
    }
    /**
     * �������
     * @return
     */
    public boolean checkData() {
        if (flg) {
            if (this.getValueDouble("CHARGE01") < 0 || this.getValueDouble("CHARGE01") > charge1) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE01", charge1);
                return false;
            }
            // if(this.getValueDouble("CHARGE02")<0 || this.getValueDouble("CHARGE02") > charge2){
            // this.messageBox("���벻��ȷ");
            // this.setValue("CHARGE02", charge2);
            // return false;
            // }
            if (this.getValueDouble("CHARGE03") < 0 || this.getValueDouble("CHARGE03") > charge3) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE03", charge3);
                return false;
            }
            if (this.getValueDouble("CHARGE04") < 0 || this.getValueDouble("CHARGE04") > charge4) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE04", charge4);
                return false;
            }
            if (this.getValueDouble("CHARGE05") < 0 || this.getValueDouble("CHARGE05") > charge5) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE05", charge5);
                return false;
            }
            if (this.getValueDouble("CHARGE06") < 0 || this.getValueDouble("CHARGE06") > charge6) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE06", charge6);
                return false;
            }
            if (this.getValueDouble("CHARGE07") < 0 || this.getValueDouble("CHARGE07") > charge7) {
                this.messageBox("���벻��ȷ"+this.getValueDouble("CHARGE07")+" "+charge7);
                this.setValue("CHARGE07", charge7);
                return false;
            }
            if (this.getValueDouble("CHARGE08") < 0 || this.getValueDouble("CHARGE08") > charge8) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE08", charge8);
                return false;
            }
            if (this.getValueDouble("CHARGE09") < 0 || this.getValueDouble("CHARGE09") > charge9) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE09", charge9);
                return false;
            }
            if (this.getValueDouble("CHARGE10") < 0 || this.getValueDouble("CHARGE10") > charge10) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE10", charge10);
                return false;
            }
            // if (this.getValueDouble("CHARGE10") < 0 ||this.getValueDouble("CHARGE11") > arAmt) {
            // this.messageBox("����ֵ����");// �Զ�����ò��ܴ���Ӧ�ս��
            // this.setValue("CHARGE11", charge11);
            // return false;
            // }
            // if(this.getValueDouble("CHARGE12")<0 || this.getValueDouble("CHARGE12") > charge12){
            // this.messageBox("���벻��ȷ");
            // this.setValue("CHARGE12", charge12);
            // return false;
            // }
            if (this.getValueDouble("CHARGE13") < 0 || this.getValueDouble("CHARGE13") > charge13) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE13", charge13);
                return false;
            }
            if (this.getValueDouble("CHARGE14") < 0 || this.getValueDouble("CHARGE14") > charge14) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE14", charge14);
                return false;
            }
            if (this.getValueDouble("CHARGE15") < 0 || this.getValueDouble("CHARGE15") > charge15) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE15", charge15);
                return false;
            }
            if (this.getValueDouble("CHARGE16") < 0 || this.getValueDouble("CHARGE16") > charge16) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE16", charge16);
                return false;
            }
            if (this.getValueDouble("CHARGE17") < 0 || this.getValueDouble("CHARGE17") > charge17) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE17", charge17);
                return false;
            }
            if (this.getValueDouble("CHARGE18") < 0 || this.getValueDouble("CHARGE18") > charge18) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE18", charge18);
                return false;
            }
            if (this.getValueDouble("CHARGE19") < 0 || this.getValueDouble("CHARGE19") > charge19) {
                this.messageBox("���벻��ȷ");
                this.setValue("CHARGE19", charge19);
                return false;
            }
        }
        return true;
    }

    /**
     * �ϲ�����
     */
    public void onMerge() {
        String CHARGE = "CHARGE0";
        for (int i = 1; i <= 19; i++) {
            if (i > 9) {
                CHARGE = "CHARGE";
            }
            try {
                ((TNumberTextField) this.getComponent(CHARGE + i)).setValue("0");
            }
            catch (Exception ex) {}
        }
        callFunction("UI|CHARGE11|setValue", this.getValueDouble("AR_AMT"));
        callFunction("UI|CUSTOM_TEXT1|setValue", "����");
        callFunction("UI|REDUCE_AMT|setEnabled", true);//add by wanglong 20131104
    }

    /**
     * ���ý����ϵ�ǰ��Ʊ�� add by wanglong 20130122
     */
    public void setUIInv() {
        String invNo = getNowInv();
        if (invNo == null || invNo.length() == 0) {
            this.messageBox("���ȿ���");
            // this.callFunction("UI|save|setEnabled", false);
            // this.callFunction("UI|query|setEnabled", false);
            // this.callFunction("UI|print|setEnabled", false);
            // return;
        }
        this.setValue("UPDATE_NO", invNo);
    }
    
    /**
     * ȡ�õ�ǰ��Ʊ�� add by wanglong 20130122
     * 
     * @return
     */
    public String getNowInv() {
        TParm selInvoice = new TParm();
        selInvoice.setData("STATUS", "0");
        selInvoice.setData("RECP_TYPE", "OPB");
        selInvoice.setData("CASHIER_CODE", Operator.getID());
        selInvoice.setData("TERM_IP", Operator.getIP());
        // System.out.println("Ʊ����������" + selInvoice);
        TParm invoice = BILInvoiceTool.getInstance().selectNowReceipt(selInvoice);
        return invoice.getValue("UPDATE_NO", 0);
    }
    
    /**
     * ��ѯ
     */
    public void onQuery() {
        onPartClear();
        TParm parm = new TParm();
        String sDate = StringTool.getString((Timestamp) this.getValue("S_DATE"), "yyyyMMddHHmmss");
        String eDate = StringTool.getString((Timestamp) this.getValue("E_DATE"), "yyyyMMddHHmmss");
        if ("O".equals(this.admType)) {
            if (this.getValueString("MR_NO").length() == 0) {
                this.messageBox("�����벡���ţ�");
                return;
            }
            if (this.getValueString("PAT_NAME").equals("")) {// add by wanglong 20130121
                queryPatName();
            }
            String sql =
                    "SELECT 'N' AS FLG, CASE WHEN NVL(PRINT_NO, '#') = '#' THEN 'δ��Ʊ' "
                            + " ELSE '�Ѵ�Ʊ' END AS PRINT_FLG, BIL_OPB_RECP.* FROM BIL_OPB_RECP "
                            + " WHERE RESET_RECEIPT_NO IS NULL " + "   AND TOT_AMT > 0 "
                            + "   AND ADM_TYPE='" + this.admType + "' "
                            + "   AND BILL_DATE BETWEEN TO_DATE('" + sDate
                            + "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate
                            + "','YYYYMMDDHH24MISS') AND MR_NO='" + this.getValueString("MR_NO")
                            + "' ORDER BY BILL_DATE DESC";
            parm.setData(this.getDBTool().select(sql));
        }
        if ("H".equals(this.admType)) {
            if (this.getValueBoolean("Single")) { // ����
                if (this.getValueString("MR_NO").length() == 0) {
                    this.messageBox("�����벡���ţ�");
                    return;
                }
                if (this.getValueString("PAT_NAME").equals("")) {// add by wanglong 20130121
                    queryPatName();
                }
                String sql =
                        "SELECT 'N' AS FLG, CASE WHEN NVL(PRINT_NO, '#') = '#' THEN 'δ��Ʊ' "
                                + " ELSE '�Ѵ�Ʊ' END AS PRINT_FLG, BIL_OPB_RECP.* FROM BIL_OPB_RECP "
                                + " WHERE RESET_RECEIPT_NO IS NULL " + "   AND TOT_AMT > 0 "
                                + "   AND ADM_TYPE='" + this.admType + "' "
                                + "   AND BILL_DATE BETWEEN TO_DATE('" + sDate
                                + "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate
                                + "','YYYYMMDDHH24MISS') AND MR_NO='"
                                + this.getValueString("MR_NO") + "' ORDER BY BILL_DATE DESC";
                // System.out.println("��ѯsql===" + sql);
                parm.setData(this.getDBTool().select(sql));
            }
            if (this.getValueBoolean("Company")) { // ����
                if (this.getValueString("COMPANY_CODE").length() == 0) {
                    this.messageBox("�������Ʋ���Ϊ�գ�");
                    return;
                }
                // ������BIL_OPB_RECP��MR_NO�ǹ�˾����(COMPANY_CODE),CASE_NO�Ǻ�ͬ����(CONTRACT_CODE)
                String sql =
                        "SELECT 'N' AS FLG, CASE WHEN NVL(PRINT_NO, '#') = '#' THEN 'δ��Ʊ' "
                                + " ELSE '�Ѵ�Ʊ' END AS PRINT_FLG, BIL_OPB_RECP.* FROM BIL_OPB_RECP "
                                + " WHERE RESET_RECEIPT_NO IS NULL " + "   AND TOT_AMT > 0 "
                                + "   AND ADM_TYPE='" + this.admType + "' "
                                + "   AND BILL_DATE BETWEEN TO_DATE('" + sDate
                                + "','YYYYMMDDHH24MISS') AND TO_DATE('" + eDate
                                + "','YYYYMMDDHH24MISS') AND MR_NO='"
                                + this.getValueString("COMPANY_CODE") + "'";
                if (this.getValueString("CONTRACT_CODE").length() != 0) {
                    sql +=
                            " AND CASE_NO='" + this.getValueString("CONTRACT_CODE")
                                    + "' ORDER BY BILL_DATE DESC";
                } else {
                    sql += " ORDER BY BILL_DATE DESC";
                }
                // System.out.println("��ѯ1sql===" + sql);
                parm.setData(this.getDBTool().select(sql));
            }
        }
        // System.out.println("tbale��ֵ" + parm);
        if (parm.getErrCode() != 0) {// add by wanglong 20130121
            messageBox(parm.getErrCode() + parm.getErrText());
            return;
        }
        if (parm.getCount() <= 0) {// add by wanglong 20130121
            messageBox("E0116");
        }
        setUIInv();
        this.callFunction("UI|SEL_ALL|setSelected", false);
        table.setParmValue(parm);
    }

    /**
     * ����
     */
    public void onSave() {
        TParm parmValue = table.getParmValue();
        int count = parmValue.getCount();
        if (count <= 0) {
            this.messageBox("�޼������ݣ�");
            return;
        }
        this.clearValue("SEL_ALL;PAT_NAME;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;"
                + "CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE19;"
                + "TOT_AMT;AMT_TO_WORD;CUSTOM_TEXT1;AR_AMT;DISCOUNT_AMT;REDUCE_AMT;REDUCE_RESPOND;REDUCE_APPROVER;REDUCE_REASON;PAY_REMARK;CHECK_NO");
        this.setValue("PAY_TYPE", "PAY_CASH");
        int row = table.getSelectedRow();
        TParm parm = parmValue.getRow(row);
        charge1 = 0;
        // charge2 = 0;
        charge3 = 0;
        charge4 = 0;
        charge5 = 0;
        charge6 = 0;
        charge7 = 0;
        charge8 = 0;
        charge9 = 0;
        charge10 = 0;
        charge11 = 0;// ����
        // charge12 = 0;
        charge13 = 0;
        charge14 = 0;
        charge15 = 0;
        charge16 = 0;
        charge17 = 0;
        charge18 = 0;
        charge19 = 0;
        double totAmt = 0;// Ӧ�ս�� add by wanglong 20130826
        reduceAmt = 0;// ������
        arAmt = 0;// ʵ�ս��
        double discountAmt = 0;//�ۿ۽��
        boolean isReduced = false;
        receiptNo = parm.getValue("RECEIPT_NO");
        charge1 = parm.getDouble("CHARGE01") + parm.getDouble("CHARGE02");
        // charge2 = parm.getDouble("CHARGE02", i);
        charge3 = parm.getDouble("CHARGE03");
        charge4 = parm.getDouble("CHARGE04");
        charge5 = parm.getDouble("CHARGE05");
        charge6 = parm.getDouble("CHARGE06");
        charge7 = parm.getDouble("CHARGE07");
        charge8 = parm.getDouble("CHARGE08");
        charge9 = parm.getDouble("CHARGE09");
        charge10 = parm.getDouble("CHARGE10");
        charge11 = parm.getDouble("CHARGE11");
        // charge12 = parm.getDouble("CHARGE12", i);
        charge13 = parm.getDouble("CHARGE13");
        charge14 = parm.getDouble("CHARGE14");
        charge15 = parm.getDouble("CHARGE15");
        charge16 = parm.getDouble("CHARGE16");
        charge17 = parm.getDouble("CHARGE17");
        charge18 = parm.getDouble("CHARGE18");
        charge19 = parm.getDouble("CHARGE19");
        totAmt = parm.getDouble("TOT_AMT");
        reduceAmt = parm.getDouble("REDUCE_AMT");// add by wanglong 20130826
        arAmt = parm.getDouble("AR_AMT");
        discountAmt = totAmt - arAmt - reduceAmt;
        if (reduceAmt > 0) {
            isReduced = true;
        }
        this.setValue("REDUCE_RESPOND", parm.getValue("REDUCE_RESPOND"));
        this.setValue("REDUCE_APPROVER", parm.getValue("REDUCE_APPROVER"));
        this.setValue("REDUCE_REASON", parm.getValue("REDUCE_REASON"));
        int flag = 0;
        if (parm.getDouble("PAY_CASH") > 0) {
            this.setValue("PAY_TYPE", "PAY_CASH");
            flag++;
            this.setValue("PAY_AMT", parm.getDouble("PAY_CASH"));
        }
        if (parm.getDouble("PAY_MEDICAL_CARD") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_MEDICAL_CARD");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_MEDICAL_CARD"));
            } else {
                this.setValue("PAY_TYPE", "PAY_MEDICAL_CARD");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_MEDICAL_CARD"));
            }
        }
        if (parm.getDouble("PAY_BANK_CARD") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_BANK_CARD");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_BANK_CARD"));
            } else {
                this.setValue("PAY_TYPE", "PAY_BANK_CARD");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_BANK_CARD"));
            }
        }
        if (parm.getDouble("PAY_INS_CARD") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_INS_CARD");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_INS_CARD"));
            } else {
                this.setValue("PAY_TYPE", "PAY_INS_CARD");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_INS_CARD"));
            }
        }
        if (parm.getDouble("PAY_CHECK") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_CHECK");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_CHECK"));
            } else {
                this.setValue("PAY_TYPE", "PAY_CHECK");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_CHECK"));
            }
        }
        if (parm.getDouble("PAY_DEBIT") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_DEBIT");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_DEBIT"));
            } else {
                this.setValue("PAY_TYPE", "PAY_DEBIT");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_DEBIT"));
            }
        }
        if (parm.getDouble("PAY_BILPAY") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_BILPAY");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_BILPAY"));
            } else {
                this.setValue("PAY_TYPE", "PAY_BILPAY");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_BILPAY"));
            }
        }
        if (parm.getDouble("PAY_INS") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_INS");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_INS"));
            } else {
                this.setValue("PAY_TYPE", "PAY_INS");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_INS"));
            }
        }
        if (parm.getDouble("PAY_OTHER1") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_OTHER1");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_OTHER1"));
            } else {
                this.setValue("PAY_TYPE", "PAY_OTHER1");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_OTHER1"));
            }
        }
        if (parm.getDouble("PAY_OTHER2") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_OTHER2");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_OTHER2"));
            } else {
                this.setValue("PAY_TYPE", "PAY_OTHER2");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_OTHER2"));
            }
        }
        if (parm.getDouble("PAY_DRAFT") > 0) {
            if (flag > 0) {
                this.setValue("PAYTYPE2_BOX", "Y");
                onChoosePayType2();
                this.setValue("PAY_TYPE2", "PAY_DRAFT");
                this.setValue("PAY_AMT2", parm.getDouble("PAY_DRAFT"));
            } else {
                this.setValue("PAY_TYPE", "PAY_DRAFT");
                flag++;
                this.setValue("PAY_AMT", parm.getDouble("PAY_DRAFT"));
            }
        }
        String remark = parm.getValue("PAY_REMARK").trim();
        if (remark.length() > 0) {
            if (remark.indexOf(" ") >= 0) {
                this.setValue("PAY_REMARK", remark.replaceFirst(" ", "#").split("#")[1]);
                this.setValue("CHECK_NO", remark.split(" ")[0]);
            } else {
                this.setValue("PAY_REMARK", remark);
            }
        }
        this.setValue("CHARGE01", charge1);
        // this.setValue("CHARGE02", charge2);
        this.setValue("CHARGE03", charge3);
        this.setValue("CHARGE04", charge4);
        this.setValue("CHARGE05", charge5);
        this.setValue("CHARGE06", charge6);
        this.setValue("CHARGE07", charge7);
        this.setValue("CHARGE08", charge8);
        this.setValue("CHARGE09", charge9);
        this.setValue("CHARGE10", charge10);
        this.setValue("CHARGE11", charge11);
        if (charge11 == 0) {
            this.setValue("CUSTOM_TEXT1", "");
        } else if (charge11 != 0) {// add by wanglong 20130118
            this.setValue("CUSTOM_TEXT1", "����");
        }
        // this.setValue("CHARGE12", charge12);//����ҽ��
        this.setValue("CHARGE13", charge13);
        this.setValue("CHARGE14", charge14);
        this.setValue("CHARGE15", charge15);
        this.setValue("CHARGE16", charge16);
        this.setValue("CHARGE17", charge17);
        this.setValue("CHARGE18", charge18);
        this.setValue("CHARGE19", charge19);
        this.setValue("TOT_AMT", totAmt);
        this.setValue("DISCOUNT_AMT", discountAmt);
        this.setValue("AR_AMT", arAmt);
        this.setValue("AMT_TO_WORD", StringUtil.getInstance().numberToWord(arAmt));
        this.setValue("REDUCE_AMT", reduceAmt);// add by wanglong 20130826
//        if(reduceAmt>0){//delete by wanglong 20131104
//            onMerge();
//        }
        if (((TRadioButton) this.getComponent("Single")).isSelected()) {// add by wanglong 20130121
            this.setValue("PAT_NAME", this.getValueString("S_PAT_NAME"));
        } else {
            if (this.getText("COMPANY_CODE").endsWith("")) {
                TParm showParm = table.getShowParmValue();
                this.setValue("PAT_NAME", showParm.getValue("MR_NO", row));
            } else this.setValue("PAT_NAME", this.getText("COMPANY_CODE"));
        }
        if (!isReduced) {
            this.setValue("REDUCE_RESPOND", Operator.getID());// ������
        }
        flg = true;
        callFunction("UI|REDUCE_AMT|setEnabled", false);//add by wanglong 20131104
    }

    /**
     * ��ӡ
     */
    public void onPrint() {
        if (this.getValueDouble("CHARGE11") > 0
                && this.getValueString("CUSTOM_TEXT1").trim().equals("")) {// add by wanglong 20130118
            this.messageBox("�������Զ�����õ�����");
            this.callFunction("UI|CUSTOM_TEXT1|requestFocus");
            return;
        }
        if (this.getValueString("PAT_NAME").trim().equals("")) {// add by wanglong 20130121
            messageBox("������" + callFunction("UI|tLabel_patName|getText").toString().split(":")[0]);
            return;
        }
        double sumAmt = 0;
        for (int i = 1; i <= 30; i++) {
            if (i < 10) {
                if (this.getValueDouble("CHARGE0" + i) < 0) {
                    messageBox("����Ϊ��");
                    this.callFunction("UI|" + ("CHARGE0" + i) + "|requestFocus");
                    return;
                }
                sumAmt += this.getValueDouble("CHARGE0" + i);
            } else {
                if (this.getValueDouble("CHARGE" + i) < 0) {
                    messageBox("����Ϊ��");
                    this.callFunction("UI|" + ("CHARGE" + i) + "|requestFocus");
                    return;
                }
                sumAmt += this.getValueDouble("CHARGE" + i);
            }
        }
        sumAmt = StringTool.round(sumAmt, 2);
        double arAmt = this.getValueDouble("AR_AMT");
        if (arAmt <= 0) {// add by wanglong 20130121
            messageBox("����ִ�м������");
            return;
        }
        arAmt = StringTool.round(arAmt, 2);// add by wanglong 20130121
        if (sumAmt != arAmt) {//add by wanglong 20131104
            this.messageBox("ϸ���������ʵ�ս��ȣ������ϸ����");
            return;
        }
        double totAmt = this.getValueDouble("TOT_AMT");
        totAmt = StringTool.round(totAmt, 2);// add by wanglong 20130121
        double reduceAmt = this.getValueDouble("REDUCE_AMT");
        reduceAmt = StringTool.round(reduceAmt, 2);
        double discountAmt = this.getValueDouble("DISCOUNT_AMT");
        discountAmt = StringTool.round(discountAmt, 2);
        double totAmt1 = StringTool.round(discountAmt + reduceAmt + arAmt, 2);
        if (totAmt != totAmt1) {
            this.messageBox("��������˶ԣ�");
            return;
        }
        TParm parm = getRePrintData();
        String printNo = parm.getValue("PRINT_NO");
        //==============================�ж��Ƿ���printnNo,û�в��߲�ӡ����
        if (printNo == null || printNo.equals("") || printNo.equals("null")) {//��ӡ�ɷ�ʱδ����Ʊ��
            if (reduceAmt > 0) {
                if (this.getValueString("REDUCE_RESPOND").trim().equals("")) {
                    this.messageBox("�����˲���Ϊ��");
                    return;
                }
                if (this.getValueString("REDUCE_APPROVER").trim().equals("")) {
                    this.messageBox("�����˲���Ϊ��");
                    return;
                }
            }
            if (this.getValueString("PAY_TYPE").equals("")) {
                this.messageBox("�����븶�ʽ");
                return;
            }
            if (this.getValueString("PAY_TYPE").equals("PAY_CHECK")
                    && this.getValueString("CHECK_NO").trim().equals("")) {
                this.messageBox("������֧Ʊ��");
                return;
            }
            if (this.getValue("PAYTYPE2_BOX").equals("Y")) {// add by wanglong 20131220
                if (this.getValueString("PAY_TYPE2").equals("")) {
                    this.messageBox("�����븶�ʽ2");
                    return;
                }
                if (this.getValueDouble("PAY_AMT") < 0 || this.getValueDouble("PAY_AMT2") < 0) {
                    this.messageBox("�������Ϊ����");
                    return;
                }
                double payAmt =
                        StringTool.round(this.getValueDouble("PAY_AMT") + this.getValueDouble("PAY_AMT2"), 2);
                if (payAmt != arAmt) {
                    this.messageBox("���ָ��ʽ��������ʵ�ս��ȣ������");
                    return;
                }
                if (this.getValueString("PAY_TYPE2").equals(this.getValueString("PAY_TYPE"))) {
                    this.messageBox("���ָ��ʽ������ͬ");
                    return;
                }
            }
            String receiptNo = parm.getValue("RECEIPT_NO");
            String invNo =getNowInv();
            if (!invNo.equals(this.getValueString("UPDATE_NO"))) {
                this.setValue("UPDATE_NO", invNo);
            }
            if (invNo == null || invNo.length() == 0) {
                this.messageBox("���ȿ���");
            }
            String reduceDeptCode = "";
            if (admType.equals("H") && this.getValueBoolean("Single")) {
                String sql =
                        "SELECT DISTINCT B.DEPT_CODE FROM HRM_BILL A, HRM_PATADM B"
                                + " WHERE A.CONTRACT_CODE = B.CONTRACT_CODE AND A.RECEIPT_NO = '#' ORDER BY B.DEPT_CODE";
                sql = sql.replaceFirst("#", receiptNo);
                reduceDeptCode =
                        new TParm(TJDODBTool.getInstance().select(sql)).getValue("DEPT_CODE", 0);
            } else if (admType.equals("H") && this.getValueBoolean("Company")) {
                String sql =
                        "SELECT DISTINCT C.DEPT_CODE FROM HRM_BILL A, HRM_ORDER B, HRM_PATADM C"
                                + " WHERE A.BILL_NO = B.BILL_NO AND B.CASE_NO = C.CASE_NO AND A.RECEIPT_NO = '#' ORDER BY C.DEPT_CODE";
                sql = sql.replaceFirst("#", receiptNo);
                reduceDeptCode =
                        new TParm(TJDODBTool.getInstance().select(sql)).getValue("DEPT_CODE", 0);
            }
            // ����BIL_OPB_RECP��
            String mrNo = parm.getValue("MR_NO");
            String caseNo = parm.getValue("CASE_NO");
            Timestamp now = TJDODBTool.getInstance().getDBTime();
            HRMOpbReceipt receipt = new HRMOpbReceipt();
            receipt.onQueryByReceiptNo(receiptNo);
            receipt.setItem(0, "PRINT_NO", invNo);
            receipt.setItem(0, "PRINT_DATE", now);
            receipt.setItem(0, "CASHIER_CODE", Operator.getID());
            receipt.setItem(0, "OPT_USER", Operator.getID());
            receipt.setItem(0, "OPT_DATE", now);
            receipt.setItem(0, "OPT_TERM", Operator.getIP());
            receipt.setItem(0, "AR_AMT", arAmt);
            receipt.setItem(0, "REDUCE_AMT", reduceAmt);
            if (reduceAmt != 0) {
                receipt.setItem(0, "REDUCE_REASON", this.getValue("REDUCE_REASON"));
                receipt.setItem(0, "REDUCE_RESPOND", this.getValue("REDUCE_RESPOND"));
                receipt.setItem(0, "REDUCE_APPROVER", this.getValue("REDUCE_APPROVER"));
                receipt.setItem(0, "REDUCE_DEPT_CODE", reduceDeptCode);
                receipt.setItem(0, "REDUCE_DATE", now);//add by wanglong 20131104
            }
            receipt.setItem(0, "PAY_CASH", 0);
            receipt.setItem(0, "PAY_MEDICAL_CARD", 0);
            receipt.setItem(0, "PAY_BANK_CARD", 0);
            receipt.setItem(0, "PAY_INS_CARD", 0);
            receipt.setItem(0, "PAY_CHECK", 0);
            receipt.setItem(0, "PAY_DEBIT", 0);
            receipt.setItem(0, "PAY_BILPAY", 0);
            receipt.setItem(0, "PAY_INS", 0);
            receipt.setItem(0, "PAY_OTHER1", 0);
            receipt.setItem(0, "PAY_OTHER2", 0);
            receipt.setItem(0, "PAY_DRAFT", 0);
//            receipt.setItem(0, this.getValueString("PAY_TYPE"), arAmt);//delete by wanglong 20131220
            receipt.setItem(0, this.getValueString("PAY_TYPE"), StringTool.round(this.getValueDouble("PAY_AMT"), 2));// add by wanglong 20131220
            receipt.setItem(0, this.getValueString("PAY_TYPE2"), StringTool.round(this.getValueDouble("PAY_AMT2"), 2));//add by wanglong 20131220
            receipt.setItem(0, "PAY_REMARK", this.getValueString("CHECK_NO") + " " + this.getValueString("PAY_REMARK"));
            String sql[] = receipt.getUpdateSQL();
//            String updateBilOpbRecp =
//                    " UPDATE BIL_OPB_RECP A SET A.PRINT_NO='" + invNo + "', A.PRINT_DATE=SYSDATE ,"
//                            + " OPT_USER='" + Operator.getID() + "',OPT_DATE=SYSDATE,OPT_TERM='"
//                            + Operator.getIP() + "' " 
//                            + " ,A.AR_AMT=" + arAmt
//                            + " ,A.REDUCE_AMT=" + reduceAmt         //add by wanglong 20130826
//                            + " ,A.REDUCE_REASON='" + this.getValue("REDUCE_REASON")+"'"
//                            + " ,A.REDUCE_RESPOND='" + this.getValue("REDUCE_RESPOND") + "'"
//                            + " ,A.REDUCE_APPROVER='" + this.getValue("REDUCE_APPROVER") + "'"
//                            + " ,A.REDUCE_DEPT_CODE='" + reduceDeptCode + "'"
//                            + " ,A.REDUCE_DATE=SYSDATE "   //add end
//                            + " WHERE A.MR_NO='" + mrNo
//                            + "' AND A.CASE_NO='" + caseNo 
//                            + "' AND A.RECEIPT_NO='" + receiptNo
//                            + "' ";
//            String sql[] = new String[]{updateBilOpbRecp };//����BIL_OPB_RECP��
            invRcp.onQuery();
            // System.out.println("3---"+SystemTool.getInstance().getDate());
            TParm invRcpParm = new TParm();
            invRcpParm.setData("RECP_TYPE", "OPB");
            invRcpParm.setData("INV_NO", invNo);
            invRcpParm.setData("RECEIPT_NO", receiptNo);
            invRcpParm.setData("AR_AMT", arAmt);// modify by wanglong 20131111
            invRcpParm.setData("STATUS", "0");
            invRcp.insert(invRcpParm);
            // System.out.println("wrong sql"+invRcp.getUpdateSQL()[0]);
            sql = StringTool.copyArray(sql, invRcp.getUpdateSQL());//����BIL_INVRCP��
            BilInvoice bilInvoice = new BilInvoice("OPB");
            // ����Ʊ�ŵ�SQL
            String sqlUpdate =
                    "UPDATE BIL_INVOICE SET UPDATE_NO='"
                            + StringTool.addString(invNo)// modify by wanglong 20130122
                            + "' WHERE RECP_TYPE='OPB' AND START_INVNO='"
                            + bilInvoice.getStartInvno() + "'";
            // System.out.println("sqlUPdate="+sqlUpdate);
            String[] temp = new String[]{sqlUpdate };//����BIL_INVOICE��
            sql = StringTool.copyArray(sql, temp);
            if (reduceAmt > 0) {// add by wanglong 20131111
                HRMBill bill = new HRMBill();
                // ����HRM_BILL��CUT_AMT
                bill.onQueryByReceiptNo(receiptNo);
                bill.setItem(0, "CUT_AMT", reduceAmt);
                double arAmt1 = 0;
                arAmt1 = bill.getItemDouble(0, "AR_AMT") - reduceAmt;
                arAmt1 = StringTool.round(arAmt1, 2);
                bill.setItem(0, "AR_AMT", arAmt);
                bill.setItem(0, "CUT_DESCRIPTION", this.getValue("REDUCE_REASON"));
                bill.setItem(0, "CUT_USER", this.getValue("REDUCE_APPROVER"));
                bill.setItem(0, "CASHIER_CODE", this.getValue("REDUCE_RESPOND"));
                bill.setItem(0, "CASHIER_DATE", now);
                sql = StringTool.copyArray(sql, bill.getUpdateSQL());
            }
            // �ͺ�̨����
            TParm inParm = new TParm();
            Map inMap = new HashMap();
//            for (int i = 0; i < sql.length; i++) {
//                System.out.println("----------sql["+i+"]------------"+sql[i]);
//            }
            inMap.put("SQL", sql);
            inParm.setData("IN_MAP", inMap);
            TParm result =
                    TIOM_AppServer.executeAction("action.hrm.HRMChargeAction", "onSaveCharge",
                                                 inParm);
            // ��鱣����
            if (result.getErrCode() != 0) {
                this.messageBox("E0001");
                return;// add by wanglong 20130121
            } else {
                this.messageBox("P0001");
            }
            String[] receiptArr = new String[]{receiptNo };
            dealPrintData(receiptArr,
                          this.getValueString("PAT_NAME"),// modify by wanglong 20130121
                          ((TextFormatHRMCompany) this.getComponent("COMPANY_CODE")).getText(),
                          this.getValueString("SEX_CODE"));
        } else {// ����Ʊ��
            if (reduceAmt != StringTool.round(parm.getDouble("REDUCE_AMT"), 2)) {
                this.messageBox("��ӡʱ���ܸ��ļ�����");
                return;
            }
            if (!onSaveRePrint()) {
                messageBox("��ӡʧ��!");
                return;
            }
            messageBox("P0001");
            // //�õ�һ��Ʊ��
            // OPBReceipt opbReceipt = (OPBReceipt) opb.getReceiptList().get(row);
            // if (opbReceipt == null)
            // return;
            TParm actionParm = new TParm();
            actionParm.setData("PAT_NAME", this.getValueString("PAT_NAME"));
            actionParm.setData("DEPT_CODE", "");
            actionParm.setData("SEX_CODE", "");
            
            actionParm.setData("OPT_USER", Operator.getName());
            actionParm.setData("OPT_ID", Operator.getID());
            actionParm.setData("PRINT_DATE", SystemTool.getInstance().getDate());
            actionParm.setData("MR_NO", "TEXT", "");
            actionParm.setData("DEPT_CODE", "TEXT", "");
            actionParm.setData("CLINICROOM_DESC", "TEXT", "");
            actionParm.setData("DR_CODE", "TEXT", "");
            
            String CUSTOM_TEXT1 = this.getValueString("CUSTOM_TEXT1");
            String CUSTOM_TEXT2 = this.getValueString("CUSTOM_TEXT2");
            actionParm.setData("CUSTOM_TEXT1", "TEXT", CUSTOM_TEXT1);
            actionParm.setData("CUSTOM_TEXT2", "TEXT", CUSTOM_TEXT2);
            actionParm.setData("QUE_NO", "TEXT", "���" + "");
            actionParm.setData("PAT_NAME", "TEXT", this.getValueString("PAT_NAME"));
            actionParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
                    .getHospitalCHNFullName(Operator.getRegion()));
            actionParm.setData("RECEIPT_NO", "TEXT", actionParm.getData("RECEIPT_NO"));
            actionParm.setData("UPDATE_NO", "TEXT", printNoOnly);
            // actionParm.setData("TOT_AMT", "TEXT", StringTool.round(totAmt, 2));
            // actionParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
            // .numberToWord(StringTool.round(totAmt, 2)));
            //add by wanglong 20130826
          //��ѯҽ������
            String caseNo = parm.getValue("CASE_NO");
            String receiptNo = parm.getValue("RECEIPT_NO");
    		String CtzDescSql = "";
    		if(!"H".equals(this.admType)){
    			CtzDescSql = " SELECT B.CTZ_DESC,C.USER_NAME FROM REG_PATADM A,SYS_CTZ B,SYS_OPERATOR C"+
    			" WHERE A.CASE_NO = '"+caseNo+"'"+
    			" AND A.CTZ1_CODE = B.CTZ_CODE"+
    			" AND A.REALDR_CODE =C.USER_ID";
    		}else{
    			CtzDescSql = " SELECT C.USER_NAME FROM HRM_BILL A, HRM_ORDER B, SYS_OPERATOR C "+
    			"  WHERE     A.RECEIPT_NO = '"+receiptNo+"' "+
    			" AND A.BILL_NO = B.BILL_NO "+
    			" AND B.DR_CODE = C.USER_ID ORDER BY B.ORDER_DATE ";
    		}
    		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));
    		actionParm.setData("DR_NAME", "TEXT", CtzDescParm.getValue("USER_NAME",0));
            if(puhua_flg){//�ջ�
            	actionParm.setData("TOT_AMT", "TEXT", StringTool.round(this.getValueDouble("AR_AMT"), 2));//�ջ�������ʾ�ֶ�
            }else{//̩��
            	String totAmtStr = this.getComboBox("PAY_TYPE").getSelectedName()+ ":" + StringTool.round(this.getValueDouble("PAY_AMT"), 2);
                actionParm.setData("TOT_AMT", "TEXT", totAmtStr);
            }
            if(this.getComboBox("PAY_TYPE2").isEnabled() && this.getComboBox("PAY_TYPE2").getSelectedName()!=null && !"".equals(this.getComboBox("PAY_TYPE2").getSelectedName())){
            	actionParm.setData("TOT_AMT1", "TEXT", this.getComboBox("PAY_TYPE2").getSelectedName()+":"+StringTool.round(this.getValueDouble("PAY_AMT2"), 2));
            }
            
            actionParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
                    .numberToWord(StringTool.round(this.getValueDouble("AR_AMT"), 2)));
            // add end
            //actionParm.setData("USER_NAME", "TEXT", "" + Operator.getName());
            actionParm.setData("USER_NAME", "TEXT", "" + Operator.getID());//modify by wanglong 20130123
            String printDate =
                    StringTool.getString(SystemTool.getInstance().getDate(), "yyyy  MM  dd");
            actionParm.setData("OPT_DATE", "TEXT", printDate);
//            actionParm.setData("YEAR", "TEXT", printDate.substring(0, 4));
//            actionParm.setData("MONTH", "TEXT", printDate.substring(5, 7));
//            actionParm.setData("DAY", "TEXT", printDate.substring(8, 10));
            actionParm.setData("OPT_ID", "TEXT", Operator.getID());
            actionParm.setData("DEPT_CODE_X", "TEXT", "");
            actionParm.setData("CHARGE01", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE01"), 2));
            // actionParm.setData("CHARGE02", "TEXT", this.getValueDouble("CHARGE02"));
            actionParm.setData("CHARGE03", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE03"), 2));
            actionParm.setData("CHARGE04", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE04"), 2));
            actionParm.setData("CHARGE05", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE05"), 2));
            actionParm.setData("CHARGE06", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE06"), 2));
            actionParm.setData("CHARGE07", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE07"), 2));
            actionParm.setData("CHARGE08", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE08"), 2));
            actionParm.setData("CHARGE09", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE09"), 2));
            actionParm.setData("CHARGE10", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE10"), 2));
            actionParm.setData("CHARGE11", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE11"), 2));
            //�Զ������
            if (CUSTOM_TEXT1.trim().equals("") && this.getValueDouble("CHARGE11") == 0) {//add by wanglong 20310123
                actionParm.setData("CHARGE11", "TEXT", "");
            }
            actionParm.setData("CHARGE12", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE12"), 2));
            actionParm.setData("CHARGE13", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE13"), 2));
            actionParm.setData("CHARGE14", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE14"), 2));
            actionParm.setData("CHARGE15", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE15"), 2));
            actionParm.setData("CHARGE16", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE16"), 2));
            actionParm.setData("CHARGE17", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE17"), 2));
            actionParm.setData("CHARGE18", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE18"), 2));
            actionParm.setData("CHARGE19", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE19"), 2));
            actionParm.setData("CHARGE20", "TEXT", "");
            actionParm.setData("CHARGE21", "TEXT", "");
            actionParm.setData("CHARGE22", "TEXT", "");
            actionParm.setData("CHARGE23", "TEXT", "");
            actionParm.setData("CHARGE24", "TEXT", "");
            actionParm.setData("CHARGE25", "TEXT", "");
            actionParm.setData("CHARGE26", "TEXT", "");
            actionParm.setData("CHARGE27", "TEXT", "");
            actionParm.setData("CHARGE28", "TEXT", "");
            actionParm.setData("CHARGE29", "TEXT", "");
            actionParm.setData("CHARGE30", "TEXT", "");
            //ҽ�ƻ�������
            String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
    	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
    	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
    	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
    	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
    	    actionParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
    	    //ҽ��ͳ��֧��
    	    actionParm.setData("PAY_DEBIT","TEXT","0.00");
    	    //�����˻�֧��
    	    actionParm.setData("DA_AMT","TEXT","0.00");
    	    //����ҽ��֧��
    	    actionParm.setData("QTYL","TEXT","0.00");
    	    
    	    actionParm.setData("Cash", "TEXT", StringTool.round(this.getValueDouble("AR_AMT"), 2));//add by huangjw
    	    
            // TParm receiptTparm= OPBTool.getInstance().getReceiptTparm(actionParm);
            // receiptTparm.setData("CUSTOM_TEXT", "TEXT",this.getValueString("CUSTOM_TEXT"));
            actionParm.setData("ADM_TYPE", this.admType);//add by wanglong 20130730
            // this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw", actionParm, true);
            this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                                 IReportTool.getInstance().getReportParm("OPBRECTPrintForCustom.class", actionParm), true);//�����ϲ�modify by wanglong 20130730
            // onClear();
            // ============xueyf modify 20120225 stop
            // add by wanglong 20130122
            this.clearValue("PAT_NAME;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;"
                    + "CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;TOT_AMT;AMT_TO_WORD;CUSTOM_TEXT1;"
                    + "AR_AMT;REDUCE_AMT;REDUCE_RESPOND;REDUCE_APPROVER;REDUCE_REASON;PAY_AMT;PAY_AMT2;PAY_TYPE2;PAYTYPE2_BOX");//������ add by wanglong 20130826
            enablePayType2(false);// add by wanglong 20131220
            this.callFunction("UI|TABLE|removeRowAll");
            this.callFunction("UI|SEL_ALL|setSelected", false);
            setUIInv();
        }
    }

    /**
     * Ʊ�ݲ���
     * 
     * @return boolean
     */
    public boolean onSaveRePrint() {
        TParm saveRePrintParm = getRePrintData();
        if (saveRePrintParm == null) return false;
        // ����OPBAction
        TParm result =
                TIOM_AppServer.executeAction("action.opb.OPBAction", "saveOPBRePrint",
                                             saveRePrintParm);
        printNoOnly = result.getValue("UPDATE_NO");
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return false;
        }
        return true;
    }

    /**
     * �õ���ӡ����
     * 
     * @return TParm
     */
    public TParm getRePrintData() {
        TParm saveParm = table.getParmValue();
        int row = table.getSelectedRow();
        TParm actionParm = saveParm.getRow(row);
        if (!actionParm.getValue("RECEIPT_NO").equals(receiptNo)) {
            this.messageBox("�벻Ҫ�ڴ�ӡǰ�ٵ�������վݼ�¼");
            return null;
        }
        actionParm.setData("OPT_USER", Operator.getID());
        actionParm.setData("OPT_TERM", Operator.getIP());
        actionParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        return actionParm;
    }
    
    /**
     * ������ӡ����
     * 
     * @param receiptNo
     *            String[]
     * @param patName
     *            String
     * @param deptCode
     *            String
     * @param sexCode
     *            String
     */
    public void dealPrintData(String[] receiptNo, String patName, String deptCode, String sexCode) {
        int size = receiptNo.length;
        for (int i = 0; i < size; i++) {
            // ȡ��һ��Ʊ�ݺ�
            String recpNo = receiptNo[i];
            if (recpNo == null || recpNo.length() == 0) return;
            // // System.out.println("recpNo="+recpNo);
            // ���ô�ӡһ��Ʊ�ݵķ���
            onPrint(OPBReceiptTool.getInstance().getOneReceipt(recpNo), patName, deptCode, sexCode,
                    recpNo);
        }
    }

    /**
     * ��ӡƱ��
     * 
     * @param recpParm
     *            TParm
     * @param patName
     *            String
     * @param deptCode
     *            String
     * @param sexCode
     *            String
     */
    public void onPrint(TParm recpParm, String patName, String deptCode, String sexCode,
                        String receiptNo) {
        // if(receiptOne==null)
        // return;
        // TParm oneReceiptParm=receiptOne.getParm();
        // // System.out.println("oneReceiptParm="+oneReceiptParm);
        // oneReceiptParm.setData("PAT_NAME",patName);
        // oneReceiptParm.setData("DEPT_CODE",deptCode);
        // oneReceiptParm.setData("SEX_CODE",sexCode);
        // this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBReceipt.jhw", oneReceiptParm);
    	
        if (recpParm == null) return;
        TParm oneReceiptParm = new TParm();
        if("1".equals(sexCode)){
        	oneReceiptParm.setData("SEX","TEXT", "��");
        } 
        if("2".equals(sexCode)){
        	oneReceiptParm.setData("SEX","TEXT", "Ů");
        }
        //ҽ�ƻ�������
        String regionSql = "SELECT HOSP_CLASS FROM SYS_REGION WHERE REGION_CODE = '"+ Operator.getRegion()+"'";
	    TParm regionParm = new TParm(TJDODBTool.getInstance().select(regionSql));
	    String hospClass = regionParm.getValue("HOSP_CLASS",0);
	    String sqlhospClass = "SELECT CHN_DESC  FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_HOSPITAL_CLASS' AND ID = '"+hospClass+"'";
	    TParm  hospClassParm =  new TParm(TJDODBTool.getInstance().select(sqlhospClass));
	    oneReceiptParm.setData("HOSP_CLASS","TEXT",hospClassParm.getValue("CHN_DESC",0));
	    //ҽ��ͳ��֧��
	    oneReceiptParm.setData("PAY_DEBIT","TEXT","0.00");
	    //�����˻�֧��
	    oneReceiptParm.setData("DA_AMT","TEXT","0.00");
	    //����ҽ��֧��
	    oneReceiptParm.setData("QTYL","TEXT","0.00");
        // Ʊ����Ϣ
        oneReceiptParm.setData("CASE_NO", recpParm.getData("CASE_NO", 0));
        String caseNo = recpParm.getValue("CASE_NO", 0);
        
      //��ѯҽ������
		String CtzDescSql = "";
		if(!"H".equals(this.admType)){
			CtzDescSql = " SELECT B.CTZ_DESC,C.USER_NAME FROM REG_PATADM A,SYS_CTZ B,SYS_OPERATOR C"+
			" WHERE A.CASE_NO = '"+caseNo+"'"+
			" AND A.CTZ1_CODE = B.CTZ_CODE"+
			" AND A.REALDR_CODE =C.USER_ID";
		}else{
			CtzDescSql = " SELECT C.USER_NAME FROM HRM_BILL A, HRM_ORDER B, SYS_OPERATOR C "+
			"  WHERE     A.RECEIPT_NO = '"+receiptNo+"' "+
			" AND A.BILL_NO = B.BILL_NO "+
			" AND B.DR_CODE = C.USER_ID ORDER BY B.ORDER_DATE ";
		}
		System.out.println("CtzDescSql:::"+CtzDescSql);
		TParm CtzDescParm = new TParm(TJDODBTool.getInstance().select(CtzDescSql));
		 // ҽ������
		oneReceiptParm.setData("DR_NAME", "TEXT", CtzDescParm.getValue(
				"USER_NAME", 0));
		
		// �ֽ�֧��
		oneReceiptParm.setData("Cash", "TEXT", StringTool.round(this.getValueDouble("AR_AMT"), 2));
		
        oneReceiptParm.setData("RECEIPT_NO", receiptNo);
        oneReceiptParm.setData("MR_NO", recpParm.getData("MR_NO", 0));
        
        oneReceiptParm.setData("BILL_DATE", recpParm.getData("BILL_DATE", 0));
        oneReceiptParm.setData("CHARGE_DATE", recpParm.getData("CHARGE_DATE", 0));
        oneReceiptParm.setData("CHARGE01",
                               recpParm.getDouble("CHARGE01", 0)
                                       + recpParm.getDouble("CHARGE02", 0));
        // oneReceiptParm.setData("CHARGE02", recpParm.getData("CHARGE02", 0));
        oneReceiptParm.setData("CHARGE03", recpParm.getData("CHARGE03", 0));
        oneReceiptParm.setData("CHARGE04", recpParm.getData("CHARGE04", 0));
        oneReceiptParm.setData("CHARGE05", recpParm.getData("CHARGE05", 0));
        oneReceiptParm.setData("CHARGE06", recpParm.getData("CHARGE06", 0));
        oneReceiptParm.setData("CHARGE07", recpParm.getData("CHARGE07", 0));
        oneReceiptParm.setData("CHARGE08", recpParm.getData("CHARGE08", 0));
        oneReceiptParm.setData("CHARGE09", recpParm.getData("CHARGE09", 0));
        oneReceiptParm.setData("CHARGE10", recpParm.getData("CHARGE10", 0));
        oneReceiptParm.setData("CHARGE11", recpParm.getData("CHARGE11", 0));
        oneReceiptParm.setData("CHARGE12", recpParm.getData("CHARGE12", 0));
        oneReceiptParm.setData("CHARGE13", recpParm.getData("CHARGE13", 0));
        oneReceiptParm.setData("CHARGE14", recpParm.getData("CHARGE14", 0));
        oneReceiptParm.setData("CHARGE15", recpParm.getData("CHARGE15", 0));
        oneReceiptParm.setData("CHARGE16", recpParm.getData("CHARGE16", 0));
        oneReceiptParm.setData("CHARGE17", recpParm.getData("CHARGE17", 0));
        oneReceiptParm.setData("CHARGE18", recpParm.getData("CHARGE18", 0));
        oneReceiptParm.setData("CHARGE19", recpParm.getData("CHARGE19", 0));
        oneReceiptParm.setData("CHARGE20", recpParm.getData("CHARGE20", 0));
        oneReceiptParm.setData("CHARGE21", recpParm.getData("CHARGE21", 0));
        oneReceiptParm.setData("CHARGE22", recpParm.getData("CHARGE22", 0));
        oneReceiptParm.setData("CHARGE23", recpParm.getData("CHARGE23", 0));
        oneReceiptParm.setData("CHARGE24", recpParm.getData("CHARGE24", 0));
        oneReceiptParm.setData("CHARGE25", recpParm.getData("CHARGE25", 0));
        oneReceiptParm.setData("CHARGE26", recpParm.getData("CHARGE26", 0));
        oneReceiptParm.setData("CHARGE27", recpParm.getData("CHARGE27", 0));
        oneReceiptParm.setData("CHARGE28", recpParm.getData("CHARGE28", 0));
        oneReceiptParm.setData("CHARGE29", recpParm.getData("CHARGE29", 0));
        oneReceiptParm.setData("CHARGE30", recpParm.getData("CHARGE30", 0));
        oneReceiptParm.setData("TOT_AMT", StringTool.round(recpParm.getDouble("TOT_AMT", 0), 2));
        oneReceiptParm.setData("AR_AMT", StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));
        oneReceiptParm.setData("CASHIER_CODE", recpParm.getData("CASHIER_CODE", 0));
        oneReceiptParm.setData("PAT_NAME", patName);
        oneReceiptParm.setData("DEPT_CODE", deptCode);
        oneReceiptParm.setData("SEX_CODE", sexCode);
        oneReceiptParm.setData("OPT_USER", Operator.getName());
        oneReceiptParm.setData("OPT_ID", Operator.getID());
        oneReceiptParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        oneReceiptParm.setData("PRINT_DATE", StringTool.getString(SystemTool.getInstance()
                .getDate(), "yyyy/MM/dd HH:mm:ss"));
        oneReceiptParm.setData("PRINT_NO", recpParm.getData("PRINT_NO", 0));
        // BILL_OPB_RECT���������MR_NO�ǹ�˾����(COMPANY_CODE)��CASE_NO�Ǻ�ͬ����(CONTRACT_CODE)
        // TParm patParm =
        // new TParm(
        // TJDODBTool
        // .getInstance()
        // .select("SELECT B.DEPT_CHN_DESC FROM HRM_PATADM A,SYS_DEPT B WHERE A.COMPANY_CODE='"
        // + recpParm.getData("MR_NO", 0)
        // + "' AND A.CONTRACT_CODE='"
        // + recpParm.getData("CASE_NO", 0)
        // + "' AND A.DEPT_CODE=B.DEPT_CODE"));
        // oneReceiptParm.setData("MR_NO", "TEXT", "��˾����:" + patName);
        // oneReceiptParm.setData("DEPT_CODE", "TEXT", "����:" + patParm.getValue("DEPT_CHN_DESC",
        // 0));
        oneReceiptParm.setData("MR_NO", "TEXT",recpParm.getData("MR_NO", 0));
        oneReceiptParm.setData("CLINICROOM_DESC", "TEXT", "");
        oneReceiptParm.setData("DR_CODE", "TEXT", "");
        oneReceiptParm.setData("QUE_NO", "TEXT", "");
        //oneReceiptParm.setData("PAT_NAME", "TEXT", patName);
        oneReceiptParm.setData("PAT_NAME", "TEXT", this.getValueString("PAT_NAME"));//modify by wanglong 20130123
        oneReceiptParm.setData("HOSP_DESC", "TEXT", Manager.getOrganization()
                .getHospitalCHNFullName(Operator.getRegion()));
        oneReceiptParm.setData("RECEIPT_NO", "TEXT", receiptNo);
        oneReceiptParm.setData("PRINT_NO", "TEXT", recpParm.getData("PRINT_NO", 0));
       
        if(this.puhua_flg){//�ջ�
        	oneReceiptParm.setData("TOT_AMT", "TEXT", StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));//�ջ�������ʾ�ֶ�
        }else{//̩��
        	 String totAmtStr = this.getComboBox("PAY_TYPE").getSelectedName()+ ":" + StringTool.round(this.getValueDouble("PAY_AMT"), 2);
             oneReceiptParm.setData("TOT_AMT", "TEXT", totAmtStr);
        }
        
        if(this.getComboBox("PAY_TYPE2").isEnabled() && this.getComboBox("PAY_TYPE2").getSelectedName()!=null && !"".equals(this.getComboBox("PAY_TYPE2").getSelectedName())){
        	oneReceiptParm.setData("TOT_AMT1", "TEXT", this.getComboBox("PAY_TYPE2").getSelectedName()+":"+StringTool.round(this.getValueDouble("PAY_AMT2"), 2));
        }
        //oneReceiptParm.setData("TOT_AMT", "TEXT", StringTool.round(recpParm.getDouble("AR_AMT", 0), 2));//��ʾ����ʵ�ս��*********
        oneReceiptParm.setData("TOTAL_AW", "TEXT", StringUtil.getInstance()
                .numberToWord(StringTool.round(recpParm.getDouble("AR_AMT", 0), 2)));
        oneReceiptParm.setData("OPT_USER", "TEXT", "����Ա:" + Operator.getName());
        String printDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy/MM/dd");
        oneReceiptParm.setData("PRINT_DATE", "TEXT", printDate);
        oneReceiptParm.setData("YEAR", "TEXT", printDate.substring(0, 4));
        oneReceiptParm.setData("MONTH", "TEXT", printDate.substring(5, 7));
        oneReceiptParm.setData("DAY", "TEXT", printDate.substring(8, 10));
        oneReceiptParm.setData("OPT_ID", "TEXT", Operator.getID());
        //==================modify by wanglong 20130123==================================
        // oneReceiptParm.setData("CHARGE01",
        // "TEXT",
        // recpParm.getDouble("CHARGE01", 0)
        // + recpParm.getDouble("CHARGE02", 0));
        // // oneReceiptParm.setData("CHARGE02", "TEXT", recpParm.getData("CHARGE02", 0));
        // oneReceiptParm.setData("CHARGE03", "TEXT", recpParm.getData("CHARGE03", 0));
        // oneReceiptParm.setData("CHARGE04", "TEXT", recpParm.getData("CHARGE04", 0));
        // oneReceiptParm.setData("CHARGE05", "TEXT", recpParm.getData("CHARGE05", 0));
        // oneReceiptParm.setData("CHARGE06", "TEXT", recpParm.getData("CHARGE06", 0));
        // oneReceiptParm.setData("CHARGE07", "TEXT", recpParm.getData("CHARGE07", 0));
        // oneReceiptParm.setData("CHARGE08", "TEXT", recpParm.getData("CHARGE08", 0));
        // oneReceiptParm.setData("CHARGE09", "TEXT", recpParm.getData("CHARGE09", 0));
        // oneReceiptParm.setData("CHARGE10", "TEXT", recpParm.getData("CHARGE10", 0));
        // oneReceiptParm.setData("CHARGE11", "TEXT", recpParm.getData("CHARGE11", 0));
        // oneReceiptParm.setData("CHARGE12", "TEXT", recpParm.getData("CHARGE12", 0));
        // oneReceiptParm.setData("CHARGE13", "TEXT", recpParm.getData("CHARGE13", 0));
        // oneReceiptParm.setData("CHARGE14", "TEXT", recpParm.getData("CHARGE14", 0));
        // oneReceiptParm.setData("CHARGE15", "TEXT", recpParm.getData("CHARGE15", 0));
        // oneReceiptParm.setData("CHARGE16", "TEXT", recpParm.getData("CHARGE16", 0));
        // oneReceiptParm.setData("CHARGE17", "TEXT", recpParm.getData("CHARGE17", 0));
        // oneReceiptParm.setData("CHARGE18", "TEXT", recpParm.getData("CHARGE18", 0));
        // oneReceiptParm.setData("CHARGE19", "TEXT", recpParm.getData("CHARGE19", 0));
        // oneReceiptParm.setData("CHARGE20", "TEXT", recpParm.getData("CHARGE20", 0));
        // oneReceiptParm.setData("CHARGE21", "TEXT", recpParm.getData("CHARGE21", 0));
        // oneReceiptParm.setData("CHARGE22", "TEXT", recpParm.getData("CHARGE22", 0));
        // oneReceiptParm.setData("CHARGE23", "TEXT", recpParm.getData("CHARGE23", 0));
        // oneReceiptParm.setData("CHARGE24", "TEXT", recpParm.getData("CHARGE24", 0));
        // oneReceiptParm.setData("CHARGE25", "TEXT", recpParm.getData("CHARGE25", 0));
        // oneReceiptParm.setData("CHARGE26", "TEXT", recpParm.getData("CHARGE26", 0));
        // oneReceiptParm.setData("CHARGE27", "TEXT", recpParm.getData("CHARGE27", 0));
        // oneReceiptParm.setData("CHARGE28", "TEXT", recpParm.getData("CHARGE28", 0));
        // oneReceiptParm.setData("CHARGE29", "TEXT", recpParm.getData("CHARGE29", 0));
        // oneReceiptParm.setData("CHARGE30", "TEXT", recpParm.getData("CHARGE30", 0));
        String CUSTOM_TEXT1 = this.getValueString("CUSTOM_TEXT1");
        String CUSTOM_TEXT2 = this.getValueString("CUSTOM_TEXT2");
        oneReceiptParm.setData("CUSTOM_TEXT1", "TEXT", CUSTOM_TEXT1);
        oneReceiptParm.setData("CUSTOM_TEXT2", "TEXT", CUSTOM_TEXT2);
        oneReceiptParm.setData("CHARGE01", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE01"), 2));
        // actionParm.setData("CHARGE02", "TEXT", this.getValueDouble("CHARGE02"));
        oneReceiptParm.setData("CHARGE03", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE03"), 2));
        oneReceiptParm.setData("CHARGE04", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE04"), 2));
        oneReceiptParm.setData("CHARGE05", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE05"), 2));
        oneReceiptParm.setData("CHARGE06", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE06"), 2));
        oneReceiptParm.setData("CHARGE07", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE07"), 2));
        oneReceiptParm.setData("CHARGE08", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE08"), 2));
        oneReceiptParm.setData("CHARGE09", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE09"), 2));
        oneReceiptParm.setData("CHARGE10", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE10"), 2));
        oneReceiptParm.setData("CHARGE11", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE11"), 2));// �Զ������
        if (CUSTOM_TEXT1.trim().equals("") && this.getValueDouble("CHARGE11") == 0) {// add by wanglong 20310123
            oneReceiptParm.setData("CHARGE11", "TEXT", "");
        }
        oneReceiptParm.setData("CHARGE12", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE12"), 2));
        oneReceiptParm.setData("CHARGE13", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE13"), 2));
        oneReceiptParm.setData("CHARGE14", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE14"), 2));
        oneReceiptParm.setData("CHARGE15", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE15"), 2));
        oneReceiptParm.setData("CHARGE16", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE16"), 2));
        oneReceiptParm.setData("CHARGE17", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE17"), 2));
        oneReceiptParm.setData("CHARGE18", "TEXT",
                               StringTool.round(this.getValueDouble("CHARGE18"), 2));
        oneReceiptParm.setData("CHARGE19", "TEXT", "");
        oneReceiptParm.setData("CHARGE20", "TEXT", "");
        oneReceiptParm.setData("CHARGE21", "TEXT", "");
        oneReceiptParm.setData("CHARGE22", "TEXT", "");
        oneReceiptParm.setData("CHARGE23", "TEXT", "");
        oneReceiptParm.setData("CHARGE24", "TEXT", "");
        oneReceiptParm.setData("CHARGE25", "TEXT", "");
        oneReceiptParm.setData("CHARGE26", "TEXT", "");
        oneReceiptParm.setData("CHARGE27", "TEXT", "");
        oneReceiptParm.setData("CHARGE28", "TEXT", "");
        oneReceiptParm.setData("CHARGE29", "TEXT", "");
        oneReceiptParm.setData("CHARGE30", "TEXT", "");
        // oneReceiptParm.setData("USER_NAME", "TEXT", "" + Operator.getName());
    
        oneReceiptParm.setData("USER_NAME", "TEXT", "" + Operator.getID());
        //==================modify end========================================
        oneReceiptParm.setData("OPT_DATE", "TEXT", 
        		StringTool.getString(SystemTool.getInstance().getDate(), "yyyy  MM  dd"));
        oneReceiptParm.setData("ADM_TYPE", this.admType);//add by wanglong 20130730
        // this.openPrintDialog("%ROOT%\\config\\prt\\opb\\OPBRECTPrint.jhw", oneReceiptParm, true);
        this.openPrintDialog(IReportTool.getInstance().getReportPath("OPBRECTPrint.jhw"),
                             IReportTool.getInstance().getReportParm("OPBRECTPrintForCustom.class", oneReceiptParm), true);//�����ϲ�modify by wanglong 20130730
        // add by wanglong 20130122
        this.clearValue("PAT_NAME;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;"
                + "CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE19;TOT_AMT;AMT_TO_WORD;CUSTOM_TEXT1;"
                + "AR_AMT;REDUCE_AMT;REDUCE_RESPOND;REDUCE_APPROVER;REDUCE_REASON;DISCOUNT_AMT;PAY_REMARK;CHECK_NO;"
                + "PAY_AMT;PAY_AMT2;PAY_TYPE2;PAYTYPE2_BOX");// ������ add by wanglong 20130826
        enablePayType2(false);// add by wanglong 20131220
        this.callFunction("UI|TABLE|removeRowAll");
        this.callFunction("UI|SEL_ALL|setSelected", false);
        setUIInv();
    }
    
    /**
     * �ı�ÿһ�ָ��ʽ�Ľ��
     */
    public void onChangePayAmt(String tag){// add by wanglong 20131220
        if (tag.equals("PAY_AMT")) {
            this.setValue("PAY_AMT2", StringTool.round(this.getValueDouble("AR_AMT")
                                                               - this.getValueDouble(tag), 2));
        } else {
            this.setValue("PAY_AMT", StringTool.round(this.getValueDouble("AR_AMT")
                                                              - this.getValueDouble(tag), 2));
        }
    }
    
    /**
     * ѡ�񸶿ʽ2CheckBox
     */
    public void onChoosePayType2() {// add by wanglong 20131220
        if (this.getValue("PAYTYPE2_BOX").equals("Y")) {
            enablePayType2(true);
        } else {
            this.clearValue("PAY_TYPE2;PAY_AMT");
            this.setValue("PAY_AMT", StringTool.round(this.getValueDouble("AR_AMT"), 2));
            enablePayType2(false);
        }
    }
    
    /**
     * ����/���ø��ʽ2�����
     * @param flag
     */
    public void enablePayType2(boolean flag) {// add by wanglong 20131220
        this.callFunction("UI|PAY_AMT|setEnabled", flag);
        this.callFunction("UI|PAY_AMT2|setEnabled", flag);
        this.callFunction("UI|PAY_TYPE2|setEnabled", flag);
    }

    /**
     * �������ݿ��������
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }

    /**
     * �ֲ����
     */
    public void onPartClear() {
        this.clearValue("SEL_ALL;PAT_NAME;CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;"
                + "CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE19;"
                + "TOT_AMT;AMT_TO_WORD;CUSTOM_TEXT1;AR_AMT;DISCOUNT_AMT;REDUCE_AMT;REDUCE_RESPOND;REDUCE_APPROVER;REDUCE_REASON;PAY_REMARK;CHECK_NO;"
                + "PAY_AMT;PAY_AMT2;PAY_TYPE2;PAYTYPE2_BOX");
        enablePayType2(false);// add by wanglong 20131220
        this.setValue("PAY_TYPE", "PAY_CASH");
        this.callFunction("UI|TABLE|removeRowAll");
        callFunction("UI|REDUCE_AMT|setEnabled", false);//add by wanglong 20131104
        flg = false;
    }
    
    /**
     * ���
     */
    public void onClear() {
        initPage();// ��ʼ��ҳ����Ϣ
        this.clearValue("MR_NO;S_PAT_NAME;COMPANY_CODE;CONTRACT_CODE;MODIF;COMPANY_DNAME;MODIFD;CONTRACTN_CODE;SEL_ALL;PAT_NAME;"
                + "CHARGE01;CHARGE02;CHARGE03;CHARGE04;CHARGE05;CHARGE06;CHARGE07;CHARGE08;CHARGE09;CHARGE10;"
                + "CHARGE11;CHARGE12;CHARGE13;CHARGE14;CHARGE15;CHARGE16;CHARGE17;CHARGE18;CHARGE19;"
                + "TOT_AMT;AMT_TO_WORD;CUSTOM_TEXT1;AR_AMT;DISCOUNT_AMT;REDUCE_AMT;REDUCE_RESPOND;REDUCE_APPROVER;REDUCE_REASON;PAY_REMARK;CHECK_NO;"// ������ add by wanglong 20130826
                + "PAY_AMT;PAY_AMT2;PAY_TYPE2;PAYTYPE2_BOX");
        enablePayType2(false);// add by wanglong 20131220
        this.setValue("PAY_TYPE", "PAY_CASH");
        this.callFunction("UI|TABLE|removeRowAll");
        callFunction("UI|REDUCE_AMT|setEnabled", false);//add by wanglong 20131104
        receiptNo="";
        flg = false;
    }
    
    /**
     * 
     * @param tagNames
     * @return
     */
    public TComboBox getComboBox(String tagNames){
    	return (TComboBox) this.getComponent(tagNames);
    }
}