package com.javahis.ui.bms;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.bms.BMSSplrectTool;
import com.javahis.util.StringUtil;
import jdo.sys.PatTool;
import jdo.sys.Pat;
import jdo.util.Manager;
import com.javahis.system.combo.TComboDept;
import com.javahis.system.combo.TComboSYSStationCode;
import com.javahis.system.combo.TComboBMSBldCode;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: ��Ѫ��Ӧ
 * </p>
 *
 * <p>
 * Description: ��Ѫ��Ӧ
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.09.24
 * @version 1.0
 */
public class BMSSpleractControl
    extends TControl {

    // �ⲿ���ô���
    //private TParm parm;

    private String action = "insert";

    private TTable table;

    public BMSSpleractControl() {
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
//        Object obj = this.getParameter();
//        if (obj instanceof TParm) {
//            parm = (TParm) obj;
//            this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE"));
//            this.setValue("STATION_CODE", parm.getValue("STATION_CODE"));
//            this.setValue("MR_NO", parm.getValue("MR_NO"));
//            this.setValue("IPD_NO", parm.getValue("IPD_NO"));
//            case_no = parm.getValue("CASE_NO");
//
//            Pat pat = Pat.onQueryByMrNo(parm.getValue("MR_NO"));
//            this.setValue("PAT_NAME", pat.getName());
//            this.setValue("SEX", pat.getSexCode());
//            Timestamp date = StringTool.getTimestamp(new Date());
//            this.setValue("AGE",
//                          StringUtil.getInstance().showAge(pat.getBirthday(),
//                date));
//            this.setValue("ID_NO", pat.getIdNo());
//            this.setValue("TEST_BLD", pat.getBloodType());
//            this.setValue("BLD_TEXT", pat.getBloodType());
//            if ("+".equals(pat.getBloodRHType())) {
//                this.getRadioButton("RH_A").setSelected(true);
//            }
//            else {
//                this.getRadioButton("RH_B").setSelected(true);
//            }
//        }
        // ��ʼ����������
        initPage();
    }
    
    public void onBloodNoAction(){
    	String bloodNo = this.getValueString("BLOOD_NO") ;
    	if(bloodNo!=null && !"".equals(bloodNo)){
    		String sql = "SELECT * FROM BMS_BLOOD A WHERE A.BLOOD_NO = '"+bloodNo+"'" ;
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getErrCode() < 0) {
                return;
            }
            if (result.getCount() <= 0) {
                return;
            }  
            String bldCode = result.getValue("BLD_CODE", 0) ;
            String bldResuCode = result.getValue("BLDRESU_CODE",0) ;
            
            this.setValue("BLD_CODE", bldCode) ;
            this.setValue("BLOOD_SOURCE", bldResuCode) ;
            
    	}
    }

    /**
     * ���淽��
     */
    public void onSave() {
        if (!CheckData()) {
            return;
        }
        TParm parm = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
        parm.setData("MR_NO", this.getValue("MR_NO"));
        parm.setData("IPD_NO", this.getValue("IPD_NO"));
        parm.setData("CASE_NO", this.getValue("CASE_NO"));
        parm.setData("BLD_CODE", this.getValue("BLD_CODE"));
        parm.setData("RECAT_SYMPTOM", this.getValue("RECAT_SYMPTOM"));
        parm.setData("REACTION_CODE", this.getValue("REACTION_CODE"));
        parm.setData("REACTION_DATE", date);
        parm.setData("TEST_USER", Operator.getID());
        parm.setData("MATCH_USER", Operator.getID());
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        parm.setData("REACT_CLASS", this.getValue("REACT_CLASS"));
        parm.setData("REACT_OTH", this.getValue("REACT_OTH"));
        parm.setData("TREAT", this.getValue("TREAT"));
        parm.setData("REACT_HIS", this.getValue("REACT_HIS"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("BLOOD_NO",this.getValue("BLOOD_NO")) ;
        TParm result = new TParm();

        if ("insert".equals(action)) {
            String react_no = SystemTool.getInstance().getNo("ALL",
                "BMS", "REACT_NO", "No");
            parm.setData("REACT_NO", react_no);
            //System.out.println("parm---"+parm);
            // ִ����������
            result = TIOM_AppServer.executeAction(
                "action.bms.BMSSplreactAction", "onInsert", parm);
        }
        else if ("update".equals(action)) {
            parm.setData("REACT_NO", this.getValue("REACT_NO"));
            // ִ�����ݸ���
            result = TIOM_AppServer.executeAction(
                "action.bms.BMSSplreactAction", "onUpdate", parm);
        }
        // ������ж�
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * ��ӡ����
     */
    public void onPrint() {
        if (table.getSelectedRow() < 0) {
            this.messageBox("��ѡ���ӡ��");
        }

        // ��ӡ����
        TParm date = new TParm();
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "��Ѫ��Ӧ��¼��");
        // TABLE_1����
        TParm table_1 = new TParm();
        table_1.addData("TABLE_1_1", "��Ӧ����:" + getValueString("REACT_NO"));
        table_1.addData("TABLE_1_2", "����:" +
                        ( (TComboDept)this.getComponent("DEPT_CODE")).
                        getSelectedName());
        table_1.addData("TABLE_1_3",
                        "����:" +
                        ( (TComboSYSStationCode)this.getComponent("STATION_CODE")).
                        getSelectedName());
        table_1.addData("TABLE_1_1",
                        "������:" + TypeTool.getString(getValueString("MR_NO")));
        table_1.addData("TABLE_1_2",
                        "�������:" + TypeTool.getString(getValueString("CASE_NO")));
        table_1.addData("TABLE_1_3",
                        "סԺ��:" + TypeTool.getString(getValueString("IPD_NO")));
        table_1.setCount(2);
        table_1.addData("SYSTEM", "COLUMNS", "TABLE_1_1");
        table_1.addData("SYSTEM", "COLUMNS", "TABLE_1_2");
        table_1.addData("SYSTEM", "COLUMNS", "TABLE_1_3");
        date.setData("TABLE_1", table_1.getData());
        // TABLE_2����
        TParm table_2 = new TParm();
        table_2.addData("TABLE_2_1",
                        "����:" + TypeTool.getString(getValueString("PAT_NAME")));
        table_2.addData("TABLE_2_2",
                        "�Ա�:" + ("1".equals(getValueString("SEX")) ? "��" : "Ů"));
        table_2.addData("TABLE_2_3",
                        "����:" + TypeTool.getString(getValueString("AGE")));
        table_2.addData("TABLE_2_4",
                        "���֤�ţ�" + TypeTool.getString(getValueString("ID_NO")));
        table_2.addData("TABLE_2_1",
                        "Ѫ��:" + TypeTool.getString(getValueString("BLD_TEXT")));
        String rh_type = "";
        if (getRadioButton("RH_A").isSelected()) {
            rh_type = "����";
        }
        else if (getRadioButton("RH_B").isSelected()) {
            rh_type = "����";
        }
        table_2.addData("TABLE_2_2", "RHѪ��:" + rh_type);
        table_2.addData("TABLE_2_3",
                        "����Ѫ��:" + TypeTool.getString(getValueString("TEST_BLD")));
        table_2.addData("TABLE_2_4", "");
        table_2.setCount(2);
        table_2.addData("SYSTEM", "COLUMNS", "TABLE_2_1");
        table_2.addData("SYSTEM", "COLUMNS", "TABLE_2_2");
        table_2.addData("SYSTEM", "COLUMNS", "TABLE_2_3");
        table_2.addData("SYSTEM", "COLUMNS", "TABLE_2_4");
        date.setData("TABLE_2", table_2.getData());
        // TABLE_3����
        TParm table_3 = new TParm();
        table_3.addData("TABLE_3_1", "��Ѫʱ��:" +
                        this.getValueString("START_DATE").substring(0, 19).
                        replaceAll("-", "/"));
        table_3.addData("TABLE_3_2", "���ʱ��:" +
                        this.getValueString("START_DATE").substring(0, 19).
                        replaceAll("-", "/"));
        table_3.addData("TABLE_3_3",
                        "��ӦѪƷ:" +
                        ( (TComboBMSBldCode)this.getComponent("BLD_CODE")).
                        getSelectedName());
        table_3.addData("TABLE_3_1",
                        "��Ӧ�ȼ�:" +
                        TypeTool.getString(getComboBox("REACT_CLASS").getSelectedName()));
        table_3.addData("TABLE_3_2",
                        "��Ѫ��Ӧ:" +
                        TypeTool.getString(getComboBox("REACTION_CODE").
                                           getSelectedName()));
        table_3.addData("TABLE_3_3",
                        "������Ӧ:" + TypeTool.getString(getValueString("REACT_OTH")));
        table_3.addData("TABLE_3_1",
                        "������Ӧʷ:" +
                        TypeTool.getString(getComboBox("REACT_HIS").getSelectedName()));
        table_3.addData("TABLE_3_2",
                        "��Ӧ֢״:" +
                        TypeTool.getString(getValueString("RECAT_SYMPTOM")));
        table_3.addData("TABLE_3_3",
                        "����:" + TypeTool.getString(getValueString("TREAT")));
        table_3.setCount(3);
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_1");
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_2");
        table_3.addData("SYSTEM", "COLUMNS", "TABLE_3_3");
        date.setData("TABLE_3", table_3.getData());
        // ��β����
        date.setData("OPT_USER", "TEXT", "�Ʊ���: " + Operator.getName());
        // ���ô�ӡ����
        this.openPrintWindow("%ROOT%\\config\\prt\\BMS\\BMSSpleract.jhw", date);
    }

    /**
     * ��ѯ����
     */
    public void onQuery() {
        TParm parm = new TParm();
        if (!"".equals(this.getValueString("REACT_NO"))) {
            parm.setData("REACT_NO", this.getValue("REACT_NO"));
        }
        if (!"".equals(this.getValueString("DEPT_CODE"))) {
            parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
        }
        if (!"".equals(this.getValueString("STATION_CODE"))) {
            parm.setData("STATION_CODE", this.getValue("STATION_CODE"));
        }
        if (!"".equals(this.getValueString("MR_NO"))) {
            parm.setData("MR_NO", this.getValue("MR_NO"));
        }
        if (!"".equals(this.getValueString("IPD_NO"))) {
            parm.setData("IPD_NO", this.getValue("IPD_NO"));
        }
        TParm result = BMSSplrectTool.getInstance().onQueryTransReaction(parm);
        if (result == null || result.getCount("REACT_NO") <= 0) {
            this.messageBox("û�в�ѯ����");
            return;
        }
        table.setParmValue(result);
    }


    /**
     * ��շ���
     */
    public void onClear() {
        String clearString =
            "REACT_NO;DEPT_CODE;STATION_CODE;MR_NO;IPD_NO;" +
            "PAT_NAME;AGE;ID_NO;TEST_BLD;VALID_DAY;IN_PRICE;" +
            "SEX;START_DATE;END_DATE;BLD_CODE;REACT_CLASS;REACTION_CODE;" +
            "REACT_OTH;REACT_HIS;RECAT_SYMPTOM;TREAT;BLD_TEXT;CASE_NO;RH_A;RH_B;BLOOD_NO;BLOOD_SOURCE";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("START_DATE", date);
        this.setValue("END_DATE", date);
        //System.out.println("1111");
        //getRadioButton("RH_A").setSelected(false);
        //getRadioButton("RH_B").setSelected(false);
        //System.out.println("2222");
        action = "insert";
        table.removeRowAll();
        table.setSelectionMode(0);
    }

    /**
     * ɾ������
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row == -1) {
            this.messageBox("û��ɾ����");
            return;
        }
        TParm parm = new TParm();
        parm.setData("REACT_NO", this.getValue("REACT_NO"));
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.bms.BMSSplreactAction",
                                              "onDelete", parm);
        if (result.getErrCode() < 0) {
            this.messageBox("ɾ��ʧ��");
            return;
        }
        table.removeRow(row);
        table.setSelectionMode(0);
        this.messageBox("ɾ���ɹ�");
        onClear();
    }

    /**
     * �����Żس��¼�
     */
    public void onMrNoAction() {
        String mr_no = PatTool.getInstance().checkMrno(getValueString("MR_NO"));
        
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		this.setValue("MR_NO", mr_no);
		Pat pat = Pat.onQueryByMrNo(mr_no);
		if (!StringUtil.isNullString(mr_no) && !mr_no.equals(pat.getMrNo())) {
			messageBox("������" + mr_no + " �Ѻϲ��� " + "" + pat.getMrNo());
			mr_no = pat.getMrNo();
			this.setValue("MR_NO", mr_no);
		}
		// modify by huangtt 20160928 EMPI���߲�����ʾ end
        
        
        TParm parm = new TParm();
        parm.setData("MR_NO", mr_no);
        TParm result = (TParm) openDialog(
            "%ROOT%\\config\\bms\\BMSSplreactQuery.x", parm);
        this.setValue("DEPT_CODE", result.getValue("DEPT_CODE"));
        this.setValue("STATION_CODE", result.getValue("STATION_CODE"));
        this.setValue("MR_NO", result.getValue("MR_NO"));
        this.setValue("CASE_NO", result.getValue("CASE_NO"));
        this.setValue("IPD_NO", result.getValue("IPD_NO"));

//        Pat pat = Pat.onQueryByMrNo(mr_no);
        this.setValue("PAT_NAME", pat.getName());
        this.setValue("SEX", pat.getSexCode());
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("AGE", StringUtil.getInstance().showAge(pat.getBirthday(),
            date));
        //this.messageBox(pat.getIdNo());
        this.setValue("ID_NO", pat.getIdNo());
        this.setValue("TEST_BLD", pat.getBloodType());
        this.setValue("BLD_TEXT", pat.getBloodType());
        String rh_type = pat.getBloodRHType();
        if ("+".equals(rh_type)) {
            this.getRadioButton("RH_A").setSelected(true);
        }
        else if ("-".equals(rh_type)) {
            this.getRadioButton("RH_B").setSelected(true);
        }
        else {
            this.getRadioButton("RH_A").setSelected(false);
            this.getRadioButton("RH_B").setSelected(false);
        }
    }

    /**
     * ��񵥻��¼�
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        if (row != -1) {
            this.setValue("REACT_NO",
                          table.getParmValue().getValue("REACT_NO", row));
            this.setValue("DEPT_CODE",
                          table.getParmValue().getValue("DEPT_CODE", row));
            this.setValue("STATION_CODE",
                          table.getParmValue().getValue("STATION_CODE", row));
            this.setValue("MR_NO", table.getParmValue().getValue("MR_NO", row));
            this.setValue("IPD_NO", table.getParmValue().getValue("IPD_NO", row));
            this.setValue("PAT_NAME",
                          table.getParmValue().getValue("PAT_NAME", row));
            this.setValue("SEX", table.getParmValue().getValue("SEX_CODE", row));
            Timestamp date = StringTool.getTimestamp(new Date());
            this.setValue("AGE",
                          StringUtil.getInstance().showAge(table.getParmValue().
                getTimestamp("BIRTH_DATE", row), date));
            this.setValue("ID_NO", table.getParmValue().getValue("IDNO", row));
            this.setValue("TEST_BLD",
                          table.getParmValue().getValue("BLOOD_TYPE", row));
            String rh_type = table.getParmValue().getValue("BLOOD_RH_TYPE", row);
            if ("+".equals(rh_type)) {
                this.getRadioButton("RH_A").setSelected(true);
            }
            else if ("-".equals(rh_type)) {
                this.getRadioButton("RH_B").setSelected(true);
            }
            else {
                this.getRadioButton("RH_A").setSelected(false);
                this.getRadioButton("RH_B").setSelected(false);
            }
            this.setValue("BLD_TEXT",
                          table.getParmValue().getValue("BLOOD_TYPE", row));
            this.setValue("START_DATE",
                          table.getParmValue().getTimestamp("START_DATE", row));
            this.setValue("END_DATE",
                          table.getParmValue().getTimestamp("END_DATE", row));
            this.setValue("BLD_CODE",
                          table.getParmValue().getValue("BLD_CODE", row));
            this.setValue("REACT_CLASS",
                          table.getParmValue().getValue("REACT_CLASS", row));
            this.setValue("REACTION_CODE",
                          table.getParmValue().getValue("REACTION_CODE", row));
            this.setValue("REACT_OTH",
                          table.getParmValue().getValue("REACT_OTH", row));
            this.setValue("REACT_HIS",
                          table.getParmValue().getValue("REACT_HIS", row));
            this.setValue("RECAT_SYMPTOM",
                          table.getParmValue().getValue("RECAT_SYMPTOM", row));
            this.setValue("TREAT", table.getParmValue().getValue("TREAT", row));
            this.setValue("CASE_NO", table.getParmValue().getValue("CASE_NO", row));
            this.setValue("BLOOD_NO", table.getParmValue().getValue("BLOOD_NO", row)) ;

            //this.getComboBox("BLOOD_SOURCE").setSelectedID(table.getParmValue().getValue("BLDRESU_CODE", row)) ;
            this.setValue("BLOOD_SOURCE", table.getParmValue().getValue("BLDRESU_CODE", row)) ;
            action = "update";
        }
    }

    /**
     * ��ʼ��������
     */
    private void initPage() {
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("START_DATE", date);
        this.setValue("END_DATE", date);
        // ��ʼ��TABLE
        table = getTable("TABLE");
    }

    /**
     * �������
     * @return boolean
     */
    private boolean CheckData() {
        if ("".equals(this.getValueString("MR_NO"))) {
            this.messageBox("�����Ų���Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("BLOOD_NO"))) {//add by wanglong 20121219
            this.messageBox("Ժ�����벻��Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("BLD_CODE"))) {
            this.messageBox("��ӦѪƷ����Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("REACT_CLASS"))) {
            this.messageBox("��Ӧ�ȼ�����Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("REACTION_CODE"))) {
            this.messageBox("��Ѫ��Ӧ����Ϊ��");
            return false;
        }
        if ("".equals(this.getValueString("REACT_HIS"))) {
            this.messageBox("������Ӧʷ����Ϊ��");
            return false;
        }
        return true;
    }

    /**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * �õ�RadioButton����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     *
     * @param tagName String
     * @return TCheckBox
     */
    private TCheckBox getCheckBox(String tagName){
        return (TCheckBox) getComponent(tagName);
    }

    /**
     *
     * @param tagName String
     * @return TComboBox
     */
    private TComboBox getComboBox(String tagName){
        return (TComboBox) getComponent(tagName);
    }


}
