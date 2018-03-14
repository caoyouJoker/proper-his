package com.javahis.ui.ins;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJAdm;
import jdo.ins.INSTJTool;
import jdo.ins.INSUpLoadTool;
import jdo.ins.InsManager;
import jdo.mro.MRORecordTool;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.TypeTool;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;



/**
 * 
 * <p>
 * Title:סԺ������ϸʵʱ�ϴ�
 * Description:סԺ���÷ָ� ����ϸ�ϴ�����Ժ����
 * @author yufh
 * @version 2.0
 */
public class INSBalanceNewControl extends TControl {
	// ����
	private Compare compare = new Compare();
	// ����
	private boolean ascending = false;
	// ����
	private int sortColumn = -1;
	// ҽ�����
	String nhiCode = "";
	private TTable tableInfo; // ����������Ϣ�б�
	private TTable tabledate; //�ϴ������б�
	private TTable oldTable; // ���÷ָ�ǰ����
	private TTable newTable; // ���÷ָ������
	private TTabbedPane tabbedPane; // ҳǩ
	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	DateFormat df1 = new SimpleDateFormat("yyyy");
	private TParm regionParm; // ҽ���������
	int index = 0; // ���÷ָ� �ۼ���Ҫ������ݸ���
	int selectNewRow; // ���÷ָ����ϸ���ݻ�õ�ǰѡ����
	String type; // TYPE: SINGLE�����ֽ�����ʾ
	// �����޸ĵ�����
	private String showValue = "IDNO;IN_DATE;STATION_CODE;BED_NO;UPLOAD_FLG;"
			+ "DRG_FLG;DIAG_CODE;DIAG_DESC2;DIAG_DESC;SOURCE_CODE;" 
			+ "HOMEDIAG_DESC;QUIT_REMARK;SINGLE_UPLOAD_TYPE"; 
	// �ڶ���ҳǩ������ҳǩ
	private String pageTwo = "CONFIRM_NO;CASE_NO;YEAR_MON;REGION_CODE;BIRTH_DATE;ADM_SEQ;"
			+ "CONFIRM_SRC;HOSP_NHI_NO;INSBRANCH_CODE;CTZ1_CODE;ADM_CATEGORY;"
			+ "DEPT_DESC;PAT_CLASS;COMPANY_TYPE;SPECIAL_PAT_CODE;"
			+ "DEPT_CODE;BASEMED_BALANCE;INS_BALANCE;"
			+ "ADM_PRJ;SPEDRS_CODE;NHI_NUM;DS_DATE;"
			+ "STATUS;RECEIPT_USER;INS_UNIT;HOSP_CLS_CODE;INP_TIME;"
			+ "HOMEBED_TIME;HOMEBED_DAYS;TRANHOSP_RESTANDARD_AMT;TRANHOSP_DESC;TRAN_CLASS;"
			+ "SEX_CODE;UNIT_CODE;UNIT_DESC;PAT_AGE;NEWADM_SEQ;ADM_DAYS;"
			+ "REFUSE_TOTAL_AMT;AUDIT_TOTAL_AMT;NHI_PAY;NHI_COMMENT;OPT_USER;OPT_DATE;OPT_TERM;"
			+ "NHI_PAY_REAL;ACCOUNT_PAY_AMT;BASICMED_ADD_RATE;MEDAI_ADD_RATE;"
			+ "OVERFLOWLIMIT_ADD_RATE;BASICMED_ADD_AMT;MEDAI_ADD_AMT;OVERFLOWLIMIT_ADD_AMT;ARMYAI_AMT;"
			+ "PUBMANAI_AMT;TOT_PUBMANADD_AMT;PERSON_ACCOUNT_AMT;UNIT_DESC1;FP_NOTE;DS_SUMMARY;SINGLE_NHI_AMT;"
			+ "SINGLE_STANDARD_AMT;SINGLE_SUPPLYING_AMT;SINGLE_STANDARD_AMT_T;START_STANDARD_AMT";

	// ������ҳǩ
	private String pageThree = "PHA_AMT;PHA_OWN_AMT;PHA_ADD_AMT;"
			+ "PHA_NHI_AMT;EXM_AMT;EXM_OWN_AMT;EXM_ADD_AMT;EXM_NHI_AMT;TREAT_AMT;TREAT_OWN_AMT;TREAT_ADD_AMT;"
			+ "TREAT_NHI_AMT;OP_AMT;OP_OWN_AMT;OP_ADD_AMT;OP_NHI_AMT;BED_AMT;BED_OWN_AMT;BED_ADD_AMT;BED_NHI_AMT;"
			+ "MATERIAL_AMT;MATERIAL_OWN_AMT;MATERIAL_ADD_AMT;MATERIAL_NHI_AMT;OTHER_AMT;OTHER_OWN_AMT;"
			+ "OTHER_ADD_AMT;OTHER_NHI_AMT;BLOODALL_AMT;BLOODALL_OWN_AMT;BLOODALL_ADD_AMT;BLOODALL_NHI_AMT;"
			+ "BLOOD_AMT;BLOOD_OWN_AMT;BLOOD_ADD_AMT;BLOOD_NHI_AMT;OWN_RATE;DECREASE_RATE;REALOWN_RATE;"
			+ "INSOWN_RATE;RESTART_STANDARD_AMT;STARTPAY_OWN_AMT;OWN_AMT;PERCOPAYMENT_RATE_AMT;ADD_AMT;"
			+ "INS_HIGHLIMIT_AMT;APPLY_AMT;TRANBLOOD_OWN_AMT;HOSP_APPLY_AMT;"
			+ "TOT_ADD_AMT;TOT_NHI_AMT;SUM_TOT_AMT;TOT_AMT;TOT_OWN_AMT;"
			//������
			+ "QFBZ_AMT_S;TC_OWN_AMT_S;JZ_OWN_AMT_S;TX_OWN_AMT_S;ZGXE_AMT_S;TOTAL_AMT_S";
	private String singleName = "SPECIAL_PAT_CODE;COMPANY_TYPE;PAT_CLASS;PROGRESS;LBL_SPECIAL_PAT_CODE;LBL_COMPANY_TYPE;LBL_PAT_CLASS;LBL_PROGRESS"; // �����ֲ�������ʾ�Ŀؼ�
	// ������ҳǩ ������ҳ����
	private String mroRecordName = "CASE_NO1;MR_NO1;MARRIGE;OCCUPATION;FOLK;NATION;OFFICE;O_ADDRESS;O_TEL;O_POSTNO;"
			+ "H_ADDRESS;H_TEL;H_POSTNO;CONTACTER;RELATIONSHIP;CONT_ADDRESS;CONT_TEL;"
			+ "IN_DEPT;IN_STATION;IN_ROOM_NO;TRANS_DEPT;OUT_DEPT;OUT_STATION;"
			+ "OUT_ROOM_NO;REAL_STAY_DAYS;OE_DIAG_CODE;IN_CONDITION;CONFIRM_DATE;"
			+ "OUT_DIAG_CODE1;CODE1_REMARK;CODE1_STATUS;OUT_DIAG_CODE2;CODE2_REMARK;CODE2_STATUS;"
			+ "OUT_DIAG_CODE3;CODE3_REMARK;CODE3_STATUS;OUT_DIAG_CODE4;CODE4_REMARK;CODE4_STATUS;"
			+ "OUT_DIAG_CODE5;CODE5_REMARK;CODE5_STATUS;OUT_DIAG_CODE6;CODE6_REMARK;CODE6_STATUS;"
			+ "INTE_DIAG_CODE;PATHOLOGY_DIAG;PATHOLOGY_REMARK;EX_RSN;ALLEGIC;HBSAG;HCV_AB;HIV_AB;"
			+ "QUYCHK_OI;QUYCHK_INOUT;QUYCHK_OPBFAF;QUYCHK_CLPA;QUYCHK_RAPA;GET_TIMES;SUCCESS_TIMES;"
			+ "DIRECTOR_DR_CODE;PROF_DR_CODE;ATTEND_DR_CODE;VS_DR_CODE;VS_DR_CODE1;INDUCATION_DR_CODE;"
			+ "GRADUATE_INTERN_CODE;INTERN_DR_CODE;ENCODER;QUALITY;CTRL_DR;CTRL_NURSE;CTRL_DATE;"
			+ "INFECT_REPORT;OP_CODE;OP_DATE;MAIN_SUGEON;OP_LEVEL;HEAL_LV;DIS_REPORT;BODY_CHECK;"
			+ "FIRST_CASE;ACCOMPANY_WEEK;ACCOMPANY_MONTH;ACCOMPANY_YEAR;ACCOMP_DATE;SAMPLE_FLG;"
			+ "BLOOD_TYPE;RH_TYPE;TRANS_REACTION;RBC;PLATE;PLASMA;WHOLE_BLOOD;OTH_BLOOD;STATUS;"
			+ "PG_OWNER;DRPG_OWNER;FNALPG_OWNER;ADMCHK_FLG;DIAGCHK_FLG;BILCHK_FLG;QTYCHK_FLG;"
			+ "IN_COUNT;HOMEPLACE_CODE;MRO_CHAT_FLG;ADDITIONAL_CODE1;ADDITIONAL_CODE2;ADDITIONAL_CODE3;"
			+ "ADDITIONAL_CODE4;ADDITIONAL_CODE5;ADDITIONAL_CODE6;OE_DIAG_CODE2;OE_DIAG_CODE3;"
			+ "INTE_DIAG_STATUS;DISEASES_CODE;TEST_EMR;TEACH_EMR;IN_DIAG_CODE;INS_DR_CODE;"
			+ "CLNCPATH_CODE;REGION_CODE;TYPERESULT;SUMSCODE;OUT_ICD_DESC1;OUT_ICD_DESC2;OUT_ICD_DESC3;"
			+ "OUT_ICD_DESC4;OUT_ICD_DESC5;OUT_ICD_CODE1;OUT_ICD_CODE2;OUT_ICD_CODE3;OUT_ICD_CODE4;OUT_ICD_CODE5";
	// ������ҳǩ�б��水ť����
	private String pageSix = "L_TIMES;M_TIMES;S_TIMES";
	// ͷ��
	private String pageHead = "CONFIRM_NO;CASE_NO;MR_NO;PAT_NAME";
	// �����������ʾ
	private String[] nameAmt = { "_AMT", "_OWN_AMT", "_ADD_AMT", "_NHI_AMT" };
	private String[] nameType = { "PHA", "EXM", "TREAT", "OP", "BED",
			"MATERIAL", "OTHER", "BLOODALL", "BLOOD" }; // �շѽ������
	// ҽ���շѽ��
	private String[] insAmt = { "RESTART_STANDARD_AMT",
			"PERCOPAYMENT_RATE_AMT", "STARTPAY_OWN_AMT", "OWN_AMT",
			"TRANBLOOD_OWN_AMT", "ADD_AMT", "INS_HIGHLIMIT_AMT" };
	// ���÷ָ�ǰ�������
	private String[] pageFour = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	// ���÷ָ��������
	private String[] pageFive = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"DOSE_CODE", "STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORDER_CODE", "NHI_ORD_CLASS_CODE", "NHI_FEE_DESC",
			"OWN_PRICE", "CHARGE_DATE" };
	private TParm newParm; // ���÷ָ�������ݷ���������¼���ʹ��
	// �ۼƉ���һ���Բ���
	double addFee = 0.00;
	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		initParm();
		// �������
		addListener(newTable);
	}

	/**
	 * ��ʼ������
	 */
	private void initParm() {
		type = (String) getParameter(); // TYPE: SINGLE ������
		tableInfo = (TTable) this.getComponent("TABLEINFO"); // ����������Ϣ�б�
		tabledate = (TTable) this.getComponent("TABLEDATE"); // �ϴ������б�
		oldTable = (TTable) this.getComponent("OLD_TABLE"); // ���÷ָ�ǰ����
		newTable = (TTable) this.getComponent("NEW_TABLE"); // ���÷ָ������
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // ҳǩ
		this.setValue("START_DATE", SystemTool.getInstance().getDate()); // ��Ժ��ʼʱ��
		this.setValue("END_DATE", SystemTool.getInstance().getDate()); // ��Ժ����ʱ��
		newTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // ���ҽ���������
		isEnable(pageTwo + ";" + pageThree + ";" + mroRecordName, false);
		//��Ժ����
		callFunction("UI|DS_DATE|setEnabled",true);
		// ֻ��text���������������ICD10������
		callFunction("UI|DIAG_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");

		// textfield���ܻش�ֵ
		callFunction("UI|DIAG_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// �����ֲ�������
		if (null != type && type.equals("SINGLE")) {
			String[] singles = singleName.split(";");
			this.setTitle("�����ַ��÷ָ�");
			for (int i = 0; i < singles.length; i++) {
				callFunction("UI|" + singles[i] + "|setVisible", false);
			}
			callFunction("UI|tPanel_6|setVisible", false);
			callFunction("UI|tPanel_6|setEnabled", false);
			callFunction("UI|tPanel_13|setVisible", true);
			callFunction("UI|tPanel_13|setEnabled", true);
		} else {
			// ������ҳҳǩ����ʾ������ť
			callFunction("UI|OP_BTN|setVisible", false);
			callFunction("UI|MRO_BTN|setVisible", false);
		}
		 //���� �д���
        this.addEventListener("NEW_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                              "onTableChangeValue");
        //�������͸�ֵ
        this.setValue("PAT_TYPE","01");
        callFunction("UI|changeInfo|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|detailupload|setEnabled", false);		
        //�ϴ�����ʱ�丳ֵ
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("UPLOAD_DATE", yesterday);
        //��ò��ָ����˳�ԭ��
        getQuitRemark();
        //��ò����ϴ���ʽ
        getSingleUploadType();
	}

	/**
	 * �������ı���س��¼�
	 */
	public void onMrNo() {
		if (null == this.getValue("PAT_TYPE")
				|| this.getValue("PAT_TYPE").toString().length() <= 0) {
			onCheck("PAT_TYPE", "�������Ͳ�����Ϊ��");
			return;
		}
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("�޴˲�����!");
			return;
		}		
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("MR_NO", pat.getMrNo());
		TParm parm = new TParm();
		StringBuffer sql = new StringBuffer();
		sql.append("SELECT MR_NO,CASE_NO FROM ADM_INP WHERE CANCEL_FLG = 'N' ");
		String temp = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
			temp = " AND  REGION_CODE='" + Operator.getRegion() + "'";
		}
		parm.setData("MR_NO", pat.getMrNo());
		sql.append(" AND MR_NO='" + pat.getMrNo() + "'" + temp);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		if (result.getCount()<=0) {
			this.messageBox("�˲���û��סԺ��Ϣ");
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
			this.setValue("CASE_NO", "");
			return;
		}
		if(this.getValue("PAT_TYPE").equals("01"))//��Ժ����
		parm.setData("FLG","N");
		else if (this.getValue("PAT_TYPE").equals("02"))//��Ժ����
		parm.setData("FLG","Y");	
		if (result.getCount("MR_NO") > 1) {
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
			this.setValue("CASE_NO", result.getValue("CASE_NO"));
		} else {
			this.setValue("CASE_NO", result.getValue("CASE_NO", 0));
		};
	}
    /**
     * ��������ѡ���¼�
     */
    public void patType(){   	
    	if(this.getValue("PAT_TYPE").equals("01")){//��Ժ����
    		callFunction("UI|changeInfo|setEnabled", false);
    		callFunction("UI|onSave|setEnabled", false);
    	}
    	else if(this.getValue("PAT_TYPE").equals("02")){//��Ժ����
    		callFunction("UI|changeInfo|setEnabled", true);
    		callFunction("UI|onSave|setEnabled", true);   		
    	}
    }
    /**
     * ����ѡ���¼�
     */
    public void updateselect(){ 
    	callFunction("UI|upload|setEnabled", false);
    	callFunction("UI|detailupload|setEnabled",false);
    }
	/**
     * �����д���
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) { // �����ϼ�����
    	newTable.acceptText();
         TTableNode node = (TTableNode) obj;
         if (node == null) {
             return;
         }
         int row = node.getRow();        
         int column = node.getColumn();
 		// ���㵱ǰ�ܽ��
      	double qty = 0.0;
      	 if (column == 9) {
      		qty = Double.parseDouble(String.valueOf(node.getValue()));
          } else {
         	 qty = Double.parseDouble(String.valueOf(newTable.
                      getItemData(row, "QTY")));
          }
        double price = newTable.getParmValue().getDouble("PRICE",row);
        TParm parm = getTotalAmt(qty,price);
		newTable.setItem(row, "TOTAL_AMT",parm.getValue("FEES"));
//		System.out.println("newTable=====:"+newTable.getParmValue());
    }
    /**
     * �����ܽ��
     */
    public TParm getTotalAmt(double total, double ownPrice) {
        TParm parm = new TParm();
        double fees =  Math.abs(StringTool.round(total * ownPrice,2));
//    	System.out.println("fees=====:"+fees);
        parm.setData("FEES", fees);
        return parm;
    }

	/**
	 * ��ѯ
	 */
	public void onQuery() {
			if (null == this.getValue("PAT_TYPE")
					|| this.getValue("PAT_TYPE").toString().length() <= 0) {
				onCheck("PAT_TYPE", "�������Ͳ�����Ϊ��");
				return;
			}
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "��Ժ��ʼʱ�䲻����Ϊ��");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "��Ժ����ʱ�䲻����Ϊ��");
			return;
		}

		if (((Timestamp) this.getValue("START_DATE")).after(((Timestamp) this
				.getValue("END_DATE")))) {
			this.messageBox("��ʼʱ�䲻���Դ��ڽ���ʱ��");
			return;
		}
	
		if (null == this.getValue("UPLOAD_DATE")
				|| this.getValue("UPLOAD_DATE").toString().length() <= 0) {
			onCheck("UPLOAD_DATE", "�ϴ�ʱ�䲻����Ϊ��");
			return;
		}		
		TParm parm = new TParm();
		if (null != this.getValue("MR_NO")
				&& this.getValue("MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("MR_NO"));
		}
		if (null != this.getValue("CASE_NO")
				&& this.getValue("CASE_NO").toString().length() > 0) {
			parm.setData("CASE_NO", this.getValue("CASE_NO"));
		}
		if (this.getValueInt("INS_CROWD_TYPE") == 1) { // ��ְ
			parm.setData("INS_CROWD_TYPE", "1");
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) { // �Ǿ�
			parm.setData("INS_CROWD_TYPE", "2");
		}else if (this.getValue("INS_CROWD_TYPE").equals("")){// ��ְ���Ǿ�
			parm.setData("INS_CROWD_TYPE", "");
		}
		parm.setData("REGION_CODE", Operator.getRegion()); // �������
		parm.setData("START_DATE", df.format(this.getValue("START_DATE"))); // ��Ժʱ��
		parm.setData("END_DATE", df.format(this.getValue("END_DATE"))); // ��Ժ����ʱ��	
		TParm result = INS_Adm_Seq(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("û�в�ѯ������");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}
	/**
	 * סԺ���÷ָ��ѯ����������Ϣ
	 * @param parm
	 * @return
	 */
	public TParm INS_Adm_Seq(TParm parm) {		
		DateFormat df = new SimpleDateFormat("yyyy");
		String Sql1="";
		String Sql2="";
		String Sql3="";
		String Sql4="";
			Sql1=" AND A.IN_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_DATE")
				+ "000000"
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ parm.getValue("END_DATE")
				+ "235959"
				+ "','YYYYMMDDHH24MISS') ";
			if (null!=parm.getValue("CASE_NO") && parm.getValue("CASE_NO").toString().length()>0){
				Sql2+=" AND A.CASE_NO='"+parm.getValue("CASE_NO")+"'";
			}
			if (null!=parm.getValue("MR_NO") && parm.getValue("MR_NO").toString().length()>0){
				Sql2+=" AND A.MR_NO='"+parm.getValue("MR_NO")+"'";
			}
			if(this.getValue("PAT_TYPE").equals("01"))//��Ժ����
				Sql3 = " AND A.DS_DATE IS NULL"; 
				else if (this.getValue("PAT_TYPE").equals("02"))//��Ժ����
				Sql3 = " AND A.DS_DATE IS NOT NULL";
			if (null!=parm.getValue("INS_CROWD_TYPE") && 
					parm.getValue("INS_CROWD_TYPE").toString().length()>0){
				Sql4 =" AND B.INS_CROWD_TYPE ='"+ parm.getValue("INS_CROWD_TYPE")+"'";
			}
		String date = df.format(SystemTool.getInstance().getDate())
				+ "/01/01";// ҽ������
		String SQL = " SELECT CASE SUBSTR(C.CONFIRM_NO,1,2) WHEN 'KN' THEN '"
				+ date
				+ "' ELSE TO_CHAR(A.IN_DATE,'YYYY/MM/DD') END AS IN_DATE, "
				+ // ҽ������
				" A.CASE_NO,C.CONFIRM_NO,C.PAT_NAME,C.SEX_CODE,A.CTZ1_CODE,C.IDNO,A.IPD_NO, "
				+ " CASE IN_STATUS WHEN '0' THEN '�ʸ�ȷ����¼��' WHEN '1' THEN '�����ѽ���' WHEN '2' THEN '�������ϴ�' "
				+ " WHEN '3' THEN '���������' WHEN '4' THEN '������֧��' WHEN '5' THEN '����ȷ����' " 
				+ " WHEN '6' THEN '�����ʸ�ȷ����ʧ��' "
				+ " WHEN '7' THEN '�ʸ�ȷ���������' ELSE '' END  AS IN_STATUS,"
				+ " A.MR_NO,C.PAT_AGE,C.ADM_SEQ,B.INS_CROWD_TYPE,C.LOCAL_FLG,C.INS_CROWD_TYPE AS INS_CROWD_TYPE_YD," 
				+ " CASE WHEN C.SDISEASE_CODE IS NULL THEN ''" 
				+ " WHEN C.SDISEASE_CODE IS NOT NULL THEN (SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY" 
				+ " WHERE SYS_DICTIONARY.GROUP_ID ='SIN_DISEASE'"
				+ " AND SYS_DICTIONARY.ID = C.SDISEASE_CODE) END AS SDISEASE_DESC,C.SDISEASE_CODE "
				+ " FROM ADM_INP A,SYS_CTZ B,INS_ADM_CONFIRM C"
				+ " WHERE A.REGION_CODE='"+ parm.getValue("REGION_CODE")+ "' "
				+ Sql1
				+ Sql2
				+ Sql3
				+ Sql4
				+ "  AND C.CASE_NO = A.CASE_NO "
				+ " AND B.CTZ_CODE=A.CTZ1_CODE "
				+ " AND B.NHI_CTZ_FLG = 'Y'"// ��ݼ���Ⱥ����
				+ // ���ҽ������������CASE_NO
				" AND (C.IN_STATUS IN ('0','1','7') OR C.IN_STATUS IS NULL) ";
//		System.out.println("SQL:::::INS_Adm_Seq"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		return result;
	}

	/**
	 * У��Ϊ�շ���
	 * 
	 * @param name
	 *            String
	 * @param message
	 *            String
	 */
	private void onCheck(String name, String message) {
		this.messageBox(message);
		this.grabFocus(name);
	}

	/**
	 * ת����ϸ
	 */
	public void onApply() {
		if (null == this.getValue("UPLOAD_DATE")
				|| this.getValue("UPLOAD_DATE").toString().length() <= 0) {
			onCheck("UPLOAD_DATE", "�ϴ�ʱ�䲻����Ϊ��");
			return;
		}
		onExe("H");
	}

	/**
	 * ִ��ת����ϸ�Ͳ����������ϲ���
	 * 
	 * @param type
	 *            ��M :ת������Ϣ���� ,H :ת����ϸ������
	 */
	private void onExe(String type) {
		TParm parm = getTableSeleted();
		//System.out.println("parm:"+parm);
		if (null == parm) {
			return;
		}
		if(type.equals("H")){
		  //�ж��Ƿ��ϴ��ۼ�����
        String sqldate =" SELECT MAX(BILL_DATE) AS BILL_DATE" +
   		     " FROM IBS_ORDD" +
   		     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'";
        TParm dateparm = new TParm(TJDODBTool.getInstance().select(sqldate));            
        String billdate = df.format(dateparm.getTimestamp("BILL_DATE",0));
        String update = df.format(this.getValue("UPLOAD_DATE"));
        if (this.getValue("PAT_TYPE").equals("02")&&
        Double.parseDouble(billdate)==Double.parseDouble(update)){
        	this.messageBox("�ָ�֮ǰ����ִ��ת������������");     	
        }
	}
		parm.setData("TYPE", type); // M :ת������Ϣ���� ,H :ת�걨����
		parm.setData("REGION_CODE", Operator.getRegion()); // ҽԺ����
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String startDate = parm.getValue("START_DATE");
		String endDate = df.format(SystemTool.getInstance().getDate());				
		String uploadDate =  StringTool.getString(TCM_Transform.getTimestamp(getValue(
			     "UPLOAD_DATE")), "yyyyMMdd"); 
		parm.setData("START_DATE", startDate); // ��ʼʱ��
		parm.setData("END_DATE", endDate); // ����ʱ��
		parm.setData("UPLOAD_DATE", uploadDate); // �ϴ�ʱ��
//		System.out.println("onExe=======1"+parm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onExeNew", parm);
//		System.out.println("onExe=======2"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ��ʧ��:"+result.getErrText());
			return;
		}
		if (result.getInt("MES")==2) {
			this.messageBox("��ϸ���ϴ�������ִ��ת����ϸ");
			return;
		}
		String Msg = "ת�����\n" + "�ɹ�����:" + result.getValue("SUCCESS_INDEX")
				+ "\n" + "ʧ�ܱ���:" + result.getValue("ERROR_INDEX");
		this.messageBox(Msg);
		if ("M".equals(type)) {
			this.setValueForParm(pageHead + ";" + pageTwo + ";" + showValue+";REALOWN_RATE",
					result.getRow(0));//pangben 2013-4-1���ʵ��֧������,�ǾӲ��˽������ʧ�ܣ�֧����������ȷ����ֵ����
			int days = StringTool.getDateDiffer((Timestamp) this
					.getValue("DS_DATE"), (Timestamp) this.getValue("IN_DATE"));
			int rollDate = days == 0 ? 1 : days;
			this.setValue("ADM_DAYS", rollDate);
			this.setValue("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));
			tabbedPane.setSelectedIndex(1);
		}
	}

	/**
	 * ��ô����
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	private String getDiagDesc(String caseNo) {
		String sql = "SELECT ICD_CODE,ICD_DESC AS ICD_CHN_DESC FROM MRO_RECORD_DIAG  WHERE CASE_NO='"
				+ caseNo + "' AND ((IO_TYPE='O' AND MAIN_FLG='N') OR IO_TYPE IN('Q','W'))";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			return "";
		}
		String diagDesc = "";
		for (int i = 0; i < result.getCount(); i++) {
			diagDesc += result.getValue("ICD_CHN_DESC", i) + ",";
		}
		if (diagDesc.length() > 0) {
			diagDesc = diagDesc.substring(0, diagDesc.lastIndexOf(","));
		}
		return diagDesc;
	}

	/**
	 * ���÷ָ�ִ�в���
	 */
	public void onUpdate() {
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
//		System.out.println("onUpdate===");
		if (!this.CheckTotAmt()) {
		} else {
//			System.out.println("onUpdate===111");
			feePartitionEnable(false);
			updateRun(); // ׼���ϴ�ҽ��
			feePartitionEnable(true);
		}

	}
	/**
	 * ���÷ָ�����а�ť�û�
	 * 
	 * @param enAble
	 *            boolean
	 */
	private void feePartitionEnable(boolean enAble) {
		callFunction("UI|save|setEnabled", enAble);
		callFunction("UI|new|setEnabled", enAble);
		callFunction("UI|delete|setEnabled", enAble);
//		callFunction("UI|query|setEnabled", enAble);
//		callFunction("UI|changeInfo|setEnabled", enAble);
//		callFunction("UI|apply|setEnabled", enAble);
//		callFunction("UI|onSave|setEnabled", enAble);
		for (int i = 1; i < 11; i++) {
			callFunction("UI|NEW_RDO_" + i + "|setEnabled", enAble);
		}
	}
	/**
	 * ���÷ָ�ִ���Ժ����ݱȽ�
	 * 
	 * @return boolean
	 */
	public boolean CheckTotAmt() {
			TParm parm = getTableSeleted();
			String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//��ʼʱ��
			String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//����ʱ��		
			if (null != parm) {
				 String sql =" SELECT SUM(A.TOTAL_AMT) AS TOTAL_AMT" +
	     		" FROM INS_IBS_UPLOAD A,INS_ADM_CONFIRM B" +
	     		" WHERE A.ADM_SEQ = B.ADM_SEQ" +
	     		" AND B.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
	     		" AND B.ADM_SEQ = '"+ parm.getValue("ADM_SEQ") + "'" +
	     		" AND A.CHARGE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
                " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
	     		" AND A.NHI_ORDER_CODE  NOT LIKE '***%'";       
		TParm ibsUpLoadParm = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("ibsUpLoadParm===" + ibsUpLoadParm);
		if (ibsUpLoadParm.getErrCode() < 0) {
			return false;
		}
		if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0)==0){
			messageBox("���Ϊ0,������÷ָ�");
			return false; 	
		}				
		// �ж��Ƿ����������� ��ý���ʱ��
//		Timestamp sysTime = SystemTool.getInstance().getDate();			
//		DateFormat df1 = new SimpleDateFormat("yyyy");
//		DateFormat df = new SimpleDateFormat("yyyyMMdd");
//		String tempDate = df1.format(sysTime);//��ǰ���	
//		String startDate = parm.getValue("START_DATE");//��ʼʱ��
//		String endDate = "";//����ʱ��
//		if (Integer.parseInt(startDate) < Integer.parseInt(tempDate + "0101")) 
//			endDate = ""+ (Integer.parseInt(tempDate) -1) + "1231"+"235959";
//		else 
//			endDate = df.format(sysTime)+"235959";
//		startDate =startDate + "000000";
		String sql1 =" SELECT SUM(TOT_AMT) AS TOT_AMT" +
				     " FROM IBS_ORDD" +
				     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
				     " AND BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
                     " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
		TParm ibsOrddParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (ibsOrddParm.getErrCode() < 0) {
			return false;
		}
//		System.out.println("ibsOrddParm===" + ibsOrddParm);
		if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) != ibsOrddParm
				.getDouble("TOT_AMT", 0)){
			messageBox("���÷ָ�����������");
			return false; 
		} else {
			return true; 
		}
	}
		return true;
	}

	/**
	 * �ۼ�����
	 */
	private void update1() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// System.out.println("�ۼƉ������"+parm);
		String insAdmSql = " SELECT ADM_SEQ FROM INS_ADM_CONFIRM WHERE CONFIRM_NO = '"
				+ parm.getValue("CONFIRM_NO") + "' ";
		TParm insAdmParm = new TParm(TJDODBTool.getInstance().select(insAdmSql));
		// ҽ������˳���
		String admSeq = insAdmParm.getValue("ADM_SEQ", 0);
		String upLoadSql = " SELECT SUM (A.TOTAL_AMT) AS TOTAL_AMT, SUM (A.ADDPAY_AMT) AS ADDPAY_AMT,"
				+ "        SUM (A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,"
				+ "        MAX (CHARGE_DATE) AS CHARGE_DATE "
				+ "   FROM INS_IBS_UPLOAD A "
				+ "  WHERE ADM_SEQ = '"
				+ admSeq
				+ "' "
				+ "    AND A.NHI_ORDER_CODE NOT LIKE '***%' "
				+ "    AND A.ADDPAY_FLG = 'Y' ";
		// ��ְ ���� ��ѯ������Ӧ�����ۼ�����ΪY������
		TParm result = new TParm(TJDODBTool.getInstance().select(upLoadSql));
		if (result.getErrCode() < 0) {
			return;
		}
		addFee = result.getDouble("TOTAL_AMT", 0);
		TParm splitParm = new TParm();
		TParm splitCParm = new TParm();
		splitParm.setData("ADDPAY_ADD", result.getDouble("TOTAL_AMT", 0));
		//��ʼʱ��
		String startDate = parm.getValue("START_DATE");
		//System.out.println("startDate:"+startDate.length());
		if(startDate.length() > 8)
			startDate =startDate.substring(0,8); 
		splitParm.setData("HOSP_START_DATE", startDate);
		if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 1.��ְ 2.�Ǿ�
			// System.out.println("��ְ�������"+splitCParm);
			// ��ְ�ۼ�����
			splitCParm = INSTJTool.getInstance().DataDown_sp1_C(splitParm);
			 System.out.println("��ְ��������"+splitCParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			// System.out.println("�ǾӉ������"+splitCParm);
			// �Ǿ� סԺ�ۼ���������
			splitCParm = INSTJTool.getInstance().DataDown_sp1_H(splitParm);
			// System.out.println("�ǾӉ�������"+splitCParm);
		}
		if (!INSTJTool.getInstance().getErrParm(splitCParm)) {
			this.messageBox(splitCParm.getErrText());
			return;
		}
		TParm exeParm = new TParm();
		exeParm.setData("NHI_AMT", splitCParm.getDouble("NHI_AMT")); // �걨���
		exeParm.setData("TOTAL_AMT", result.getDouble("TOTAL_AMT", 0)); // �������
		exeParm.setData("TOTAL_NHI_AMT", splitCParm.getDouble("NHI_AMT")); // ҽ�����
		exeParm.setData("ADD_AMT", splitCParm.getDouble("ADDPAY_AMT")); // �ۼ��������
		exeParm.setData("ADDPAY_AMT", splitCParm.getDouble("ADDPAY_AMT")); // �ۼ��������
		exeParm.setData("OWN_AMT", splitCParm.getDouble("OWN_AMT")); // �Էѽ��
		exeParm.setData("CASE_NO", parm.getValue("CASE_NO")); // �������
		exeParm.setData("REGION_CODE", Operator.getRegion()); // ����
		// ��ѯ���SEQ_NO
		TParm maxSeqParm = INSIbsUpLoadTool.getInstance().queryMaxIbsUpLoad(
				parm);
//		 System.out.println("maxSeqParm====="+maxSeqParm);
		if (maxSeqParm.getErrCode() < 0) {
			return;
		}
		exeParm.setData("SEQ_NO", maxSeqParm.getInt("SEQ_NO", 0) + 1); // ˳���
		exeParm.setData("DOSE_CODE", ""); // ����
		exeParm.setData("STANDARD", ""); // ���
		exeParm.setData("PRICE", 0); // ����
		exeParm.setData("QTY", 0); // ����
		exeParm.setData("ADM_SEQ", maxSeqParm.getValue("ADM_SEQ", 0)); // ҽ�������
		exeParm.setData("OPT_USER", Operator.getID()); // ID
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("HYGIENE_TRADE_CODE", ""); // ��׼�ĺ�
		exeParm.setData("ORDER_CODE", "***018"); // ҽ������
		exeParm.setData("NHI_ORDER_CODE", "***018"); // ҽ��ҽ������
		exeParm.setData("ORDER_DESC", "һ���Բ����ۼ�����");
		exeParm.setData("ADDPAY_FLG", "Y"); // �ۼ�������־��Y���ۼ�������N�����ۼ�������
		exeParm.setData("PHAADD_FLG", "N"); // ����ҩƷ
		exeParm.setData("CARRY_FLG", "N"); // ��Ժ��ҩ
		exeParm.setData("OPT_TERM", Operator.getIP()); //
		exeParm.setData("NHI_ORD_CLASS_CODE", "06"); // ͳ�ƴ���
		exeParm.setData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("CHARGE_DATE", 0), true)); // ��ϸ¼��ʱ��
		exeParm.setData("YEAR_MON", parm.getValue("YEAR_MON")); // �ں�
		result = TIOM_AppServer.executeAction("action.ins.INSBalanceAction",
				"onAdd", exeParm);
//		 System.out.println("onAdd====="+result);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ���ۼ�����ʧ��");
			return;
		}
	}

	/**
	 * ׼���ϴ�ҽ�� �ǾӲ�����Ҫ�ж��Ƿ� ȡ��ҽ���Ƿ��Ƕ�ͯ��ҩ���ͯ������Ŀ
	 * 
	 * �����ֲ��� INS_IBS�޸Ĵ�λ���������ҽ�ò��Ϸ�������
	 */
	private void updateRun() {
		TParm commParm = getTableSeleted();
		if (null == commParm) {

			return;
		}
		TParm parmValue = newTable.getParmValue(); // ��÷��÷ָ��������
		double bedFee = regionParm.getDouble("TOP_BEDFEE", 0);
		boolean flg = false; // �����Ϣ��ܿ� �ж��Ƿ�ָ�ɹ�
		TParm tableParm = null;
		TParm newParm = new TParm(); // �ۼ�����
		// TParm ctzParm = null;
		TParm tempParm = new TParm();
		if (null == nhiCode || nhiCode.length() <= 0) {
			String sql = " SELECT CTZ1_CODE FROM INS_ADM_CONFIRM WHERE CASE_NO='"
					+ commParm.getValue("CASE_NO") + "'" +
						 " AND CANCEL_FLG = 'N'";
			tempParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (tempParm.getErrCode() < 0) {
				this.messageBox("��ò���ҽ�����ʧ��");
				return;
			}
			if (tempParm.getCount("CTZ1_CODE") <= 0) {
				this.messageBox("û���ҵ�����ҽ�����");
				return;
			}
			nhiCode = tempParm.getValue("CTZ1_CODE", 0);
		}
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			String nhiOrderCode = tableParm.getValue("NHI_ORDER_CODE");
			// �ۼ���������ʱ�����ݿ�����һ��ҽ��Ϊ***018������
			if ("***018".equals(nhiOrderCode) || nhiOrderCode.equals("")) { // ҽ������
				continue;
			}
			if (nhiOrderCode.length() > 4) {
				String billdate = tableParm.getValue("CHARGE_DATE").replace(
						"/", ""); // ��ϸ������ʱ��
				TParm parm = new TParm();
				
				parm.setData("CTZ1_CODE", nhiCode); // ���
				parm.setData("QTY", tableParm.getValue("QTY")); // ����
				parm.setData("TOTAL_AMT", tableParm.getValue("TOTAL_AMT")); // �ܽ��
				parm.setData("TIPTOP_BED_AMT", bedFee); // ��ߴ�λ��
				parm.setData("PHAADD_FLG", null != tableParm
						.getValue("PHAADD_FLG")
						&& tableParm.getValue("PHAADD_FLG").equals("Y") ? "1"
						: "0"); // ҩƷ����ע��
				parm.setData("FULL_OWN_FLG", null != tableParm
						.getValue("FULL_OWN_FLG")
						&& tableParm.getValue("FULL_OWN_FLG").equals("Y") ? "0"
						: "1"); // ȫ�Էѱ�־
				parm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // ҽ���������
				parm.setData("NHI_ORDER_CODE", nhiOrderCode);//ҽ����
				parm.setData("CHARGE_DATE", billdate); // ���÷���ʱ��
				TParm splitParm = new TParm();		
				//pangben 2012-9-6
				if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 1.��ְ 2.�Ǿ�
					// System.out.println("��ְҽ���ָ�ǰ�������"+parm);
					// סԺ������ϸ�ָ�
					splitParm = INSTJTool.getInstance().DataDown_sp1_B(parm);

				} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
					// סԺ������ϸ�ָ�
					splitParm = INSTJTool.getInstance().DataDown_sp1_G(parm);
				}
				if (!INSTJTool.getInstance().getErrParm(splitParm)) {
					flg = true;
					this.messageBox(parmValue.getValue("SEQ_NO", i) + "��ʧ��");
					break;
				}
				// �ۼ����ݲ���
				setIbsUpLoadParm(tableParm, splitParm, newParm);
			} else {
				this.messageBox("����" + parmValue.getValue("SEQ_NO", i)
						+ "��ҽ������"); // ���
			}

		}
		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		newParm.setData("TYPE", type); // �ж�ִ������ ��SINGLE:�����ֲ���
		newParm.setData("CASE_NO", commParm.getValue("CASE_NO")); // �����ֲ���ʹ��
		newParm.setData("YEAR_MON", commParm.getValue("YEAR_MON")); // �ںŵ����ֲ���ʹ��
		// ִ���޸�INS_IBS_UPLOAD�����
		// System.out.println("ִ���޸�INS_IBS_UPLOAD��������"+newParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onSaveInsUpLoad", newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		//����INS_IBS_UPLOAD�ֶ�up_flgΪ1���ѷָ�
		 for (int i = 0; i < newParm.getCount("SEQ_NO"); i++) {
			  TParm data = newParm.getRow(i);
			  String sql= " UPDATE INS_IBS_UPLOAD SET UP_FLG = '1'"+
			              " WHERE ADM_SEQ='"+ data.getValue("ADM_SEQ")+ "'"+
			              " AND SEQ_NO='"+ data.getValue("SEQ_NO")+ "'"; 
			  TParm result1 = new TParm(TJDODBTool.getInstance().update(sql));  
			  if (result1.getErrCode() < 0) {
	                return ;
	            }
//			  System.out.println("updateRun======="+result1); 	  
		 }		
		if (flg) {
			this.messageBox("�ָ�ʧ��");
		} else {
			this.messageBox("�ָ�ɹ�");
		}
	}
	/**
	 * ���÷ָ� �ۼ����� ���INS_IBS_UPLOAD �����
	 * 
	 * @param tableParm
	 *            TParm
	 * @param splitParm
	 *            TParm
	 * @param newParm
	 *            TParm
	 */
	private void setIbsUpLoadParm(TParm tableParm, TParm splitParm,
			TParm newParm) {
		newParm.addData("ADM_SEQ", tableParm.getValue("ADM_SEQ")); // ����˳���
		newParm.addData("SEQ_NO", tableParm.getValue("SEQ_NO")); // ���
		newParm.addData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				tableParm.getValue("CHARGE_DATE"), true)); // ��ϸ������ʱ��
		newParm.addData("ADDPAY_AMT", splitParm.getValue("ADDPAY_AMT")); // �������
		newParm.addData("TOTAL_NHI_AMT", splitParm.getValue("NHI_AMT")); // �걨���
		newParm.addData("OWN_AMT", splitParm.getValue("OWN_AMT")); // ȫ�Էѽ��
		newParm.addData("OWN_RATE", splitParm.getValue("OWN_RATE")); // �Ը�����
		newParm.addData("NHI_ORD_CLASS_CODE", splitParm
				.getValue("NHI_ORD_CLASS_CODE")); // ͳ�ƴ���
		newParm.addData("ADDPAY_FLG", null != splitParm.getValue("ADDPAY_FLG")
				&& splitParm.getValue("ADDPAY_FLG").equals("1") ? "Y" : "N"); // �ۼ�������־

	}
	
	/**
	 * ����ϸ�ϴ�����
	 */
	public void ondetailUpdate() {		
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = getTableSeleted();
		if (parm == null) {
			return;
		}		
		if (null == this.getValue("UPLOAD_DATE")
				|| this.getValue("UPLOAD_DATE").toString().length() <= 0) {
			onCheck("UPLOAD_DATE", "�ϴ�ʱ�䲻����Ϊ��");
			return;
		}
		
		TParm data = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//��ʼʱ��
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//����ʱ��	
		//�ж��ϴ����ݽ���Ƿ����
		 String upload =" SELECT SUM(A.TOTAL_AMT) AS TOTAL_AMT" +
	 		" FROM INS_IBS_UPLOAD A,INS_ADM_CONFIRM B" +
	 		" WHERE A.ADM_SEQ = B.ADM_SEQ" +
	 		" AND B.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
	 		" AND B.ADM_SEQ = '"+ parm.getValue("ADM_SEQ") + "'" +
	 		" AND A.CHARGE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
            " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
	 		" AND A.NHI_ORDER_CODE  NOT LIKE '***%'";       
	        TParm ibsUpLoadParm = new TParm(TJDODBTool.getInstance().select(upload));
//	        System.out.println("ibsUpLoadParm===" + ibsUpLoadParm);
	       if (ibsUpLoadParm.getErrCode() < 0) {
		        return;
	          }
	       String ordd =" SELECT SUM(TOT_AMT) AS TOT_AMT" +
		     " FROM IBS_ORDD" +
		     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
		     " AND BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
             " AND TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')";
            TParm ibsOrddParm = new TParm(TJDODBTool.getInstance().select(ordd));
//            System.out.println("ibsOrddParm===" + ibsOrddParm);
            if (ibsOrddParm.getErrCode() < 0) {
	          return;
              }
            if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) != ibsOrddParm
		          .getDouble("TOT_AMT", 0)){
	              messageBox("��ϸ�ϴ�����������");
	               return; 
               }
            //��ȡҽʦ������ջ�����Ϣ�ϴ�����
            String sql1 =" SELECT A.CONFIRM_NO,A.ADM_SEQ,A.NHIHOSP_NO,A.PERSONAL_NO,"+
	    	 " B.DEPT_CODE,B.BED_NO,C.DR_QUALIFY_CODE"+
	    	 " FROM INS_ADM_CONFIRM A,ADM_INP B,SYS_OPERATOR C"+
	    	 " WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
	    	 " AND A.CASE_NO = B.CASE_NO" +
	    	 " AND B.VS_DR_CODE = C.USER_ID "+
	    	 " AND A.CANCEL_FLG = 'N'";    
	     TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
	     if (result1.getErrCode() < 0) {
	            this.messageBox(result1.getErrText());
	            return;
	        }	          
            
//            //�ж��Ƿ��ϴ��ۼ�����
            String sqldate =" SELECT MAX(BILL_DATE) AS BILL_DATE" +
    		     " FROM IBS_ORDD" +
       		     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'";
            TParm dateparm = new TParm(TJDODBTool.getInstance().select(sqldate));            
            String billdate = df.format(dateparm.getTimestamp("BILL_DATE",0));
            String update = df.format(this.getValue("UPLOAD_DATE"));
            if (this.getValue("PAT_TYPE").equals("02")&&
            Double.parseDouble(billdate)==Double.parseDouble(update)){
            	//�ۼ���������
        		update1();
        		if(updateAddDetail(parm,result1).getErrCode()<0)
        			return;
            }
          else{
		//�ϴ�����ϸ
		 String sql = " SELECT B.CONFIRM_NO,A.ADM_SEQ,B.INSBRANCH_CODE," +
		" TO_CHAR(A.CHARGE_DATE,'yyyy-mm-dd HH:mm:ss') AS CHARGE_DATE," +
		" A.SEQ_NO,A.NHI_ORDER_CODE,A.ORDER_DESC,A.OWN_RATE,C.JX,C.GG," +
		" A.PRICE,A.QTY,A.TOTAL_AMT,A.TOTAL_NHI_AMT,A.OWN_AMT,A.ADDPAY_AMT," +
		" A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE,A.PHAADD_FLG," +
		" A.CARRY_FLG,C.PZWH,B.ADM_CATEGORY AS PAY_TYPE," +
		" TO_CHAR(A.CHARGE_DATE,'yyyymmdd') AS CHARGE_DATE_PAT" +
		" FROM  INS_IBS_UPLOAD A ,INS_ADM_CONFIRM B ,INS_RULE C" +
		" WHERE A.REGION_CODE ='"+ parm.getValue("REGION_CODE") + "'" +
		" AND A.ADM_SEQ= '"+ parm.getValue("ADM_SEQ") + "'" +
		" AND A.ADM_SEQ = B.ADM_SEQ" +
		" AND A.QTY<>0" +
		" AND A.ORDER_CODE != '***018'" +
		" AND A.NHI_ORDER_CODE = C.SFXMBM" +
		" AND A.CHARGE_DATE BETWEEN" + 
		" TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')" + 
		" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
		" AND A.CHARGE_DATE BETWEEN C.KSSJ AND C.JSSJ" +
		" ORDER BY A.SEQ_NO";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//		  System.out.println("ondetailUpdate===1" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ��ʧ��");
			return;
		}
		 data = this.DataUploadDetail(result,result1);
		 if (data.getErrCode() < 0) {			 
			 this.messageBox(data.getErrText());
			 return;
	        }		 
		//�ϴ��ջ�����Ϣ 	
		 else{
		     //�������
		     String sql2 =" SELECT A.ICD_CODE,B.ICD_CHN_DESC"+ 
		     " FROM ADM_INPDIAG A,SYS_DIAGNOSIS B"+ 
		     " WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
		     " AND A.IO_TYPE = 'M'"+ 
		     " AND A.ICD_CODE = B.ICD_CODE";    
	     TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
	     if (result2.getErrCode() < 0) {
	            this.messageBox(result2.getErrText());
	            return;
	        }
	      data = this.DataUploadPat(result,result1,result2);
		 if (data.getErrCode() < 0) {			 
			 this.messageBox(data.getErrText());
			 return;
	        }
			//����INS_IBS_UPLOAD�ֶ�up_flgΪ2�����ϴ�
		  for (int i = 0; i < result.getCount("SEQ_NO"); i++) {
				TParm data3 = result.getRow(i);
				String sql3= " UPDATE INS_IBS_UPLOAD SET UP_FLG = '2'," +
						     " UP_DATE = SYSDATE"+
				             " WHERE ADM_SEQ='"+ data3.getValue("ADM_SEQ")+ "'"+
				             " AND SEQ_NO='"+ data3.getValue("SEQ_NO")+ "'"; 
				TParm result3 = new TParm(TJDODBTool.getInstance().update(sql3));  
				if (result3.getErrCode() < 0) {
		              return ;
		        }
		    }
		 }
//		  System.out.println("ondetailUpdate===2" + result);	     
		 }
            this.messageBox("�ϴ���ϸ�ɹ�");     
	}
    /**
     * סԺ�ϴ�����ϸ
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUploadDetail(TParm parm,TParm drparm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
//        System.out.println("DataDown_ssks_A=====parm"+parm);
        //�ϴ���ϸ
        int count = parm.getCount("ADM_SEQ");
//       System.out.println("DataDown_ssks_A=====count"+count);
        for (int m = 0; m < count; m++) {
            //System.out.println("����ѭ��"+m+parm.getRow(m));
            confInfoParm.addData("CONFIRM_NO", parm.getData("CONFIRM_NO", m));//ԭʼȷ�����
            confInfoParm.addData("ADM_SEQ",parm.getData("ADM_SEQ", m));//��ҽ˳���
            confInfoParm.addData("HOSP_CLEFT_CENTER",
            		parm.getData("INSBRANCH_CODE", m));//ҽԺ����������
            confInfoParm.addData("BILL_DATE", parm.getValue("CHARGE_DATE",m));//���÷���ʱ��
            String uploaddate = StringTool.getString(SystemTool.getInstance().getDate(),
            		"yyyy-MM-dd HH:mm:ss");
            confInfoParm.addData("UPLOAD_DATE", uploaddate);//ҽԺ�ϴ�ʱ��
            confInfoParm.addData("SEQ_NO", parm.getData("SEQ_NO", m));//���
            confInfoParm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO",0));//ҽԺ����
            confInfoParm.addData("NHI_ORDER_CODE",parm.getData("NHI_ORDER_CODE", m));//�շ���Ŀ����
            confInfoParm.addData("NHI_ORDER_DESC", parm.getData("XMMC", m));//�շ���Ŀ����
            confInfoParm.addData("ORDER_DESC", parm.getData("ORDER_DESC", m));//ҽԺ������Ŀ����
            confInfoParm.addData("OWN_RATE",
                                 parm.getDouble("OWN_RATE", m) == 0 ? 0.00 :
                                 parm.getDouble("OWN_RATE", m) );//�Ը�����
            confInfoParm.addData("DOSE_CODE", parm.getData("JX", m));//����
            confInfoParm.addData("STANDARD", parm.getData("GG", m));//���
            confInfoParm.addData("PRICE", parm.getData("PRICE", m));//����
            confInfoParm.addData("QTY", parm.getData("QTY", m));//����
            confInfoParm.addData("TOTAL_AMT", parm.getData("TOTAL_AMT", m));//�������
            confInfoParm.addData("TOTAL_NHI_AMT",
                                 parm.getData("TOTAL_NHI_AMT", m));//�걨���
            confInfoParm.addData("OWN_AMT", parm.getData("OWN_AMT", m));//ȫ�Էѽ��
            confInfoParm.addData("ADDPAY_AMT", parm.getData("ADDPAY_AMT", m));//�������
            confInfoParm.addData("OP_FLG", 
            		parm.getValue("OP_FLG", m).equals("Y")?"1":"0");//�������ñ�־
            confInfoParm.addData("ADDPAY_FLG", 
            		parm.getValue("ADDPAY_FLG", m).equals("Y")?"1":"0");//�ۼ�������־
            confInfoParm.addData("NHI_ORD_CLASS_CODE",
                                 parm.getData("NHI_ORD_CLASS_CODE", m));//ͳ�ƴ���
            confInfoParm.addData("PHAADD_FLG", 
            		parm.getValue("PHAADD_FLG", m).equals("Y")?"1":"0");//����ҩƷ��־
            confInfoParm.addData("CARRY_FLG", 
            		parm.getValue("CARRY_FLG", m).equals("Y")?"1":"0");//��Ժ��ҩ��־
            confInfoParm.addData("PZWH", "");//��׼�ĺ�
            confInfoParm.addData("REMARK", "");//�������˵��
            if(parm.getDouble("OWN_RATE", m) ==1)
            confInfoParm.addData("NHI_FLG", "1");//ҽ��������ʶ(�Է�)
            else
            confInfoParm.addData("NHI_FLG", "0");//ҽ��������ʶ(ҽ��)	
            confInfoParm.addData("CHANGE_SEQ_NO", "");//���������
            if(parm.getDouble("TOTAL_AMT", m)>=0)
            confInfoParm.addData("RFEE_FLG", "0");//�˷ѱ�ʶ(�շ�)
            else
            confInfoParm.addData("RFEE_FLG", "1");//�˷ѱ�ʶ	(�˷�)
            confInfoParm.addData("PAY_TYPE", parm.getData("PAY_TYPE", m));//֧�����
            confInfoParm.addData("DR_QUALIFY_CODE", drparm.getData("DR_QUALIFY_CODE", 0));//ҽʦ����
            confInfoParm.addData("PARM_COUNT", 31);
        }
        //��ְ
        if (this.getValueInt("INS_CROWD_TYPE") == 1)
        confInfoParm.setData("PIPELINE", "DataDown_ssks");
        //�Ǿ�
        else  if (this.getValueInt("INS_CROWD_TYPE") == 2)
        confInfoParm.setData("PIPELINE", "DataDown_csks");
        confInfoParm.setData("PLOT_TYPE", "A");
        result = InsManager.getInstance().safe(confInfoParm);
//        System.out.println("DataUploadDetail======" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * סԺ�ϴ��ջ�����Ϣ
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUploadPat(TParm parmdetail,TParm parmpat,TParm parmdiag) {
    	 TParm result = new TParm();
    	 TParm confInfoParm = new TParm();
    	 DecimalFormat dfamt = new DecimalFormat("##########0.00");
    	 double totalamt =0;//�������
		 double ownamt =0;//�Էѽ��
		 double addpayamt =0;//�������
		 double totalnhiamt =0;//�걨���
		 double totalamtadd =0;///�ۼ������������
		 int count = parmdetail.getCount("ADM_SEQ");//��ϸ������
	     for (int i = 0; i < count; i++) {		 
	    	 totalamt+= parmdetail.getDouble("TOTAL_AMT", i);
	    	 ownamt+= parmdetail.getDouble("OWN_AMT", i);
	    	 addpayamt+= parmdetail.getDouble("ADDPAY_AMT", i);
	    	 totalnhiamt+= parmdetail.getDouble("TOTAL_NHI_AMT", i);
	    	 if(parmdetail.getValue("NHI_ORDER_CODE",i).equals("***018"))
	    		 totalamtadd = parmdetail.getDouble("TOTAL_AMT", i);
	     }
	     confInfoParm.addData("CONFIRM_NO", parmpat.getData("CONFIRM_NO", 0));//ԭʼȷ�����
         confInfoParm.addData("ADM_SEQ",parmpat.getData("ADM_SEQ", 0));//��ҽ˳�����
         confInfoParm.addData("HOSP_NHI_NO", parmpat.getData("NHIHOSP_NO", 0));//ҽԺ����        
         String uploaddate = StringTool.getString(SystemTool.getInstance().getDate(),
         		"yyyy-MM-dd HH:mm:ss");
         confInfoParm.addData("UPLOAD_DATE", uploaddate);//ҽԺ�ϴ�ʱ��  
         confInfoParm.addData("OWN_NO", parmpat.getData("PERSONAL_NO", 0));//���˱��
         String sql = " SELECT INS_DEPT_CODE FROM INS_DEPT"+
                      " WHERE HIS_DEPT_CODE = '"+ parmpat.getValue("DEPT_CODE",0) + "'";
         TParm dept = new TParm(TJDODBTool.getInstance().select(sql)); 
//         System.out.println("INS_DEPT_CODE======" + dept.getData("INS_DEPT_CODE", 0));
         confInfoParm.addData("DEPT_CODE", dept.getData("INS_DEPT_CODE", 0));//סԺ����
         confInfoParm.addData("DR_NHI_CODE", parmpat.getData("DR_QUALIFY_CODE", 0));//ҽʦ����
         confInfoParm.addData("OPT_USER", Operator.getID());//����Ա���� 
//         System.out.println("CHARGE_DATE======" + parmdetail.getValue("CHARGE_DATE_PAT",0));
         confInfoParm.addData("BILL_DATE", parmdetail.getValue("CHARGE_DATE_PAT",0));//���÷���ʱ��
         confInfoParm.addData("TOT_AMT", dfamt.format(totalamt-totalamtadd));//�������ϼ�
         confInfoParm.addData("OWN_AMT", dfamt.format(ownamt));//�Էѽ��ϼ�
         confInfoParm.addData("ADD_AMT", dfamt.format(addpayamt));//�������ϼ�
         confInfoParm.addData("NHI_AMT", dfamt.format(totalnhiamt));//�걨���ϼ�
         confInfoParm.addData("SUM_COUNT", count);//��ϸ������
 		//��ϱ���
    	String diagecode = "";
       //�������
   		String diagedesc = ""; 
   		int count1 = parmdiag.getCount("ICD_CHN_DESC");
   		 for(int m=0;m<count1;m++){
   			diagecode +=parmdiag.getData("ICD_CODE",m)+"@";
   			diagedesc +=parmdiag.getData("ICD_CHN_DESC",m)+",";
   			
   		 } 		
   		confInfoParm.addData("DIAGE_CODE", diagecode.length()>0? 
   				diagecode.substring(0, diagecode.length() - 1):"");//�������        
   		confInfoParm.addData("DIAGE_DESC", diagedesc.length()>0? 
   				diagedesc.substring(0, diagedesc.length() - 1):"");//������� ����
   		confInfoParm.addData("DELAY", "");//�ӳٲ���ԭ�� ?
   		confInfoParm.addData("SPE_REMARK", "");//�������
   		confInfoParm.addData("BED_NO", parmpat.getData("BED_NO", 0));//��λ��
   		confInfoParm.addData("PARM_COUNT", 19);        
         //��ְ
         if (this.getValueInt("INS_CROWD_TYPE") == 1)
         confInfoParm.setData("PIPELINE", "DataDown_ssks");
         //�Ǿ�
         else  if (this.getValueInt("INS_CROWD_TYPE") == 2)
         confInfoParm.setData("PIPELINE", "DataDown_csks");
         confInfoParm.setData("PLOT_TYPE", "B");
         result = InsManager.getInstance().safe(confInfoParm);
//        System.out.println("DataUploadPat======" + result);
         if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return result;
         }
    	return result;
    }
	
	/**
	 * ҳǩ����¼�
	 */
	public void onChangeTab() {

		switch (tabbedPane.getSelectedIndex()) {
		// 3 :���÷ָ�ǰҳǩ 4�����÷ָ��ҳǩ
		case 3:
			onSplitOld();
			break;
		case 4:
			onSplitNew();
			break;
		}
	}
	/**
	 * ���÷ָ�ǰ����
	 */
	public void onSplitOld() {
		onSplitOld(true);

	}
	private void onSplitOld(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		String sql1="";
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("OLD_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"OLD_RDO_" + i).getName());
					
					sql1= " AND A.NHI_ORD_CLASS_CODE='"+ 
					parm.getValue("NHI_ORD_CLASS_CODE") + "'";
					break;
				}
			}			
		}	
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//��ʼʱ��
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//����ʱ��	
		String sql = " SELECT A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD,A.PHAADD_FLG,"+
			" A.CARRY_FLG,A.PRICE,SUM(A.QTY) AS QTY,SUM(A.TOTAL_AMT) AS TOTAL_AMT,"+
			" SUM(A.TOTAL_NHI_AMT) AS TOTAL_NHI_AMT,SUM(A.OWN_AMT) AS OWN_AMT," +
			" SUM(A.ADDPAY_AMT) AS ADDPAY_AMT,A.NHI_ORD_CLASS_CODE,"+
			" C.NHI_CODE_I,C.OWN_PRICE , MAX(TO_CHAR(A.BILL_DATE,'YYYYMMDD')) BILL_DATE"+
			" FROM INS_IBS_ORDER A ,SYS_FEE C"+
			" WHERE A.TOTAL_AMT <> 0"+
			" AND A.ORDER_CODE=C.ORDER_CODE"+
			" AND A.CASE_NO='"+ parm.getValue("CASE_NO") + "'" +
			" AND A.YEAR_MON ='"+ parm.getValue("YEAR_MON") + "'" +
			sql1+
			" AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
			" GROUP BY A.ORDER_CODE,A.ORDER_DESC,A.DOSE_DESC,A.STANDARD,A.PHAADD_FLG,"+
			" A.CARRY_FLG,A.PRICE,A.NHI_ORD_CLASS_CODE,C.NHI_CODE_I," +
			" C.OWN_PRICE ORDER BY A.ORDER_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (flg) {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				// this.messageBox("û�в�ѯ������");
				return;
			}
		} else {
			if (result.getCount() <= 0) {
				oldTable.acceptText();
				oldTable.setDSValue();
				oldTable.removeRowAll();
				return;
			}
		}
		double qty = 0.00; // ����
		double totalAmt = 0.00; // �������
		double totalNhiAmt = 0.00; // �걨���
		double ownAmt = 0.00; // �Էѽ��
		double addPayAmt = 0.00; // �������
		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
			totalNhiAmt += result.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += result.getDouble("OWN_AMT", i);
			addPayAmt += result.getDouble("ADDPAY_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pageFour.length; i++) {
			if (i == 0) {
				result.addData(pageFour[i], "�ϼ�:");
				continue;
			}
			result.addData(pageFour[i], "");
		}
		result.addData("QTY", qty);
		result.addData("TOTAL_AMT", totalAmt);
		result.addData("TOTAL_NHI_AMT", totalNhiAmt);
		result.addData("OWN_AMT", ownAmt);
		result.addData("ADDPAY_AMT", addPayAmt);
		result.setCount(result.getCount() + 1);
		oldTable.setParmValue(result);
		this.setValue("SUM_AMT", totalAmt); // ����ܽ��
	}
	/**
	 * У���Ƿ��л�ý���
	 * 
	 * @return TParm
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫִ�е�����");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", "")
				.substring(0, 6)); // �ں�
		parm.setData("CASE_NO", parm.getValue("CASE_NO")); // �������
		parm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // �ʸ�ȷ������
		parm.setData("START_DATE", parm.getValue("IN_DATE").replace("/", "")); // ��ʼʱ��
		parm.setData("MR_NO", parm.getValue("MR_NO"));
		parm.setData("PAT_AGE", parm.getValue("PAT_AGE")); // ����
		parm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));
		parm.setData("INS_CROWD_TYPE_YD", parm.getValue("INS_CROWD_TYPE_YD"));
		parm.setData("LOCAL_FLG", parm.getValue("LOCAL_FLG"));
		parm.setData("SDISEASE_CODE", parm.getValue("SDISEASE_CODE"));//�����ֱ�ǣ���Ժ����ʹ�ã�
		return parm;
	}

	/**
	 * ��õ�ѡ�ؼ�
	 * 
	 * @param name
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}
	/**
	 * ���÷ָ������
	 */
	public void onSplitNew() {
		onSplitNew(true);
	}

	private void onSplitNew(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		String sql1="";
		// ͳ�ƴ����ѯ��01 ҩƷ�ѣ�02 ���ѣ�03 ���Ʒѣ�04�����ѣ�05��λ�ѣ�06���Ϸѣ�07�����ѣ�08ȫѪ�ѣ�09�ɷ�Ѫ��
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("NEW_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"NEW_RDO_" + i).getName());					
					sql1= " AND A.NHI_ORD_CLASS_CODE='"+ 
					parm.getValue("NHI_ORD_CLASS_CODE") + "'";
					break;
				}
			}
		}
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//��ʼʱ��
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//����ʱ��	
		String sql = " SELECT A.SEQ_NO,A.ORDER_CODE,A.ORDER_DESC," +
			" A.DOSE_CODE,A.STANDARD,A.PHAADD_FLG," + 
			" A.CARRY_FLG,A.PRICE,A.QTY,A.TOTAL_AMT,A.TOTAL_NHI_AMT,A.OWN_AMT,A.ADDPAY_AMT," +
			" A.NHI_ORDER_CODE,A.NHI_ORD_CLASS_CODE, A.HYGIENE_TRADE_CODE,A.ADDPAY_FLG," +
			" C.NHI_FEE_DESC, C.OWN_PRICE , TO_CHAR(A.CHARGE_DATE,'YYYY/MM/DD')" +
			" AS CHARGE_DATE,A.ADM_SEQ,'N' AS FLG" +
			" FROM INS_IBS_UPLOAD A,SYS_FEE C " +
			" WHERE A.ORDER_CODE=C.ORDER_CODE" + 
			" AND A.ADM_SEQ='"+ parm.getValue("ADM_SEQ") + "'"+
			sql1+
			" AND A.CHARGE_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
			" AND A.TOTAL_AMT <> 0" + 
			" ORDER BY A.SEQ_NO";
		TParm upLoadParmOne = new TParm(TJDODBTool.getInstance().select(sql));	
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		//��������
		TParm upLoadParmTwo = INSIbsUpLoadTool.getInstance()
				.queryNewSplitUpLoad(parm);
		if (upLoadParmTwo.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		if (flg) {
			if (upLoadParmOne.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				// this.messageBox("û�в�ѯ������");
				callFunction("UI|upload|setEnabled", false); // û�����ݲ�����ִ�зָ����
				callFunction("UI|detailupload|setEnabled", false);
				return;
			}
		} else {
			if (upLoadParmOne.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				callFunction("UI|upload|setEnabled", false); // û�����ݲ�����ִ�зָ����
				callFunction("UI|detailupload|setEnabled", false);
				return;
			}
		}

		if (null == upLoadParmOne) {
			upLoadParmOne = new TParm();
		}
		// �ϲ�����
		if (upLoadParmTwo.getCount("ORDER_CODE") > 0) {
			for (int i = 0; i < upLoadParmTwo.getCount(); i++) {
				upLoadParmOne.setRowData(upLoadParmOne.getCount() + 1,
						upLoadParmTwo, i);
			}
			upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		}
		double qty = 0.00; // ����
		double totalAmt = 0.00; // �������
		double totalNhiAmt = 0.00; // �걨���
		double ownAmt = 0.00; // �Էѽ��
		double addPayAmt = 0.00; // �������
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			totalNhiAmt += upLoadParmOne.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += upLoadParmOne.getDouble("OWN_AMT", i);
			addPayAmt += upLoadParmOne.getDouble("ADDPAY_AMT", i);
			if (upLoadParmOne.getValue("ORDER_CODE", i).equals("***018")) { // �ϴ�ҽ���������ۼƽ��
				continue;
			}
			qty += upLoadParmOne.getDouble("QTY", i);
			totalAmt += upLoadParmOne.getDouble("TOTAL_AMT", i);
		}

		// //��Ӻϼ�
		for (int i = 0; i < pageFive.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pageFive[i], "�ϼ�:");
				continue;
			}
			upLoadParmOne.addData(pageFive[i], "");
		}
		upLoadParmOne.addData("QTY", 0);
		upLoadParmOne.addData("TOTAL_AMT", totalAmt);
		upLoadParmOne.addData("TOTAL_NHI_AMT", totalNhiAmt);
		upLoadParmOne.addData("OWN_AMT", ownAmt);
		upLoadParmOne.addData("ADDPAY_AMT", addPayAmt);
		upLoadParmOne.addData("ADM_SEQ", ""); // ����˳��� ����
		upLoadParmOne.addData("FLG", ""); // ��������
		upLoadParmOne.addData("HYGIENE_TRADE_CODE", ""); // ����׼��
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.addData("ADDPAY_FLG", "");
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// ��Ӻϼ�
		newTable.setParmValue(upLoadParmOne);
		this.setValue("NEW_SUM_AMT", totalAmt); // �ܽ����ʾ
		callFunction("UI|upload|setEnabled", flg);
		callFunction("UI|detailupload|setEnabled", flg);
	}
	/**
	 * ���÷ָ����ϸ���ݱ������
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("û����Ҫ���������");
			return;
		}
		parm.setData("OPT_USER", Operator.getID()); // id
		parm.setData("OPT_TERM", Operator.getIP()); // Ip
		parm.setData("REGION_CODE", Operator.getRegion()); // �������
		// ִ�����INS_IBS_UPLOAD�����
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "updateUpLoad", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onSplitNew(false);
		}
	}

	/**
	 * ���÷ָ����ϸ�����½�����
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOTAL_AMT", "TOTAL_NHI_AMT",
				"OWN_AMT", "ADDPAY_AMT" };
		TParm parm = newTable.getParmValue();
		TParm result = new TParm();
		// ���һ��������
		for (int i = 0; i < pageFive.length; i++) {
			result.setData(pageFive[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}

		result.setData("FLG", "Y"); // ��������
		if (parm.getCount() > 0) {
			// ��úϼ�����
			result.setData("ADM_SEQ", parm.getValue("ADM_SEQ", 0)); // ����˳��� ����
			result.setData("HYGIENE_TRADE_CODE", parm.getValue(
					"HYGIENE_TRADE_CODE", 0)); // ����׼��
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1); // �Ƴ��ϼ�
			int seqNo = -1; // ������˳�����
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1); // ˳���
			parm.setRowData(parm.getCount(), result, -1); // ����½�������
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1); // ���ϼ����·���
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("û�����ݲ������½�����");
			return;
		}
		newTable.setParmValue(parm);
	}

	/**
	 * ���÷ָ����ϸ����ɾ������
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��Ҫɾ��������");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("������ɾ���ϼ�����");
			return;
		}
		TParm result = INSIbsUpLoadTool.getInstance().deleteINSIbsUploadSeq(
				parm.getRow(row));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // ִ��ʧ��
			return;
		}
		this.messageBox("P0005"); // ִ�гɹ�
		onSplitNew(false);
	}

	/**
	 * ���SYS_FEE��������(�����鴰��)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onExaCreateEditComponent(Component com, int row, int column) {
		selectNewRow = row;
		// �����ǰ�к�
		column = newTable.getColumnModel().getColumnIndex(column);
		String columnName = newTable.getParmMap(column);
		// ҽ�� �� ��������
		if ("ORDER_CODE".equalsIgnoreCase(columnName)
				|| "QTY".equalsIgnoreCase(columnName)) {
		} else {
			return;
		}
		if ("ORDER_CODE".equalsIgnoreCase(columnName)) {
			TTextField textfield = (TTextField) com;
			TParm parm = new TParm();
			parm.setData("RX_TYPE", ""); // ������ CAT1_TYPE = LIS/RIS
			textfield.onInit();
			// ��table�ϵ���text����sys_fee��������
			textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			// ����text���ӽ���sys_fee�������ڵĻش�ֵ
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popExaReturn");
		}

	}

	/**
	 * ���¸�ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popExaReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		newTable.acceptText();
		TParm newParm = newTable.getParmValue();
		newParm
				.setData("ORDER_CODE", selectNewRow, parm
						.getValue("ORDER_CODE")); // ҽ����
		newParm
				.setData("ORDER_DESC", selectNewRow, parm
						.getValue("ORDER_DESC")); // ҽ������
		newParm.setData("NHI_FEE_DESC", selectNewRow, parm
				.getValue("NHI_FEE_DESC")); // ҽ������
		newParm.setData("PRICE", selectNewRow, parm.getDouble("OWN_PRICE")); // ����
		newParm.setData("NHI_ORDER_CODE", selectNewRow, parm
				.getValue("NHI_CODE_I")); // ҽ�����ô���
		newTable.setParmValue(newParm);
	}
	/**
	 * ת������������
	 */
	public void onQueryInfo() {
		onExe("M");
	}
	/**
	 * �������
	 */
	public void onSettlement() {
		TParm parm = getTableSeleted();		
		if (null == parm) {
			return;
		}
		
		parm.setData("OPT_USER", Operator.getID());
	    parm.setData("OPT_TERM", Operator.getIP());
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		if (null == this.getValue("DS_DATE")
				|| this.getValue("DS_DATE").toString().length() <= 0) {
			this.messageBox("��Ժʱ�䲻����Ϊ��");
			this.grabFocus("DS_DATE");
			return;
		}
		if (null == this.getValue("UPLOAD_DATE")
				|| this.getValue("UPLOAD_DATE").toString().length() <= 0) {
			onCheck("UPLOAD_DATE", "�ϴ�ʱ�䲻����Ϊ��");
			return;
		}
//		 System.out.println("onSettlement===2");
		//����Ԥ���㱣���Ժ��Ϣ	
		 if (onBlance(parm).getErrCode() < 0) {
	            return;
	        }
//		 System.out.println("onSettlement===3");
        //�õ�������ҳ������
        TParm MRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (MRO.getErrCode() < 0) {
            this.messageBox(MRO.getErrText());
            return ;
        }
        if (MRO.getData("SUM_TOT", 0) == null||
        	MRO.getData("SUM_TOT", 0).equals("")){ 
       	 this.messageBox("��ҳ����δת��,����ϵ������");
            return;
       }
       //��Ժ�����Ϣ
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return;
	        }
		//�����
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
//		System.out.println("mainDiag>>>>>>>>>>>>"+mainDiag);
		MRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//�����
		String secDiag = "";
        for (int i = 1; i < outDiagParm.getCount(); i++) 
        {
        	secDiag+=outDiagParm.getData("ICD_DESC", i)+"@"+
	         outDiagParm.getData("ICD_CODE", i)+"@"+
	         outDiagParm.getData("IN_PAT_CONDITION", i)+"%";
		}
//        System.out.println("secDiag>>>>>>>>>>>>"+secDiag);
        MRO.setData("OUT_DIAG_OTHER",0,secDiag.length()>0? 
        		secDiag.substring(0, secDiag.length() - 1):"");
        //�õ�����������������
        TParm MROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (MROOP.getErrCode() < 0) {
            this.messageBox(MROOP.getErrText());
            return;
        }
        System.out.println("SDISEASE_CODE==="+ parm.getValue("SDISEASE_CODE"));   
        if (this.getValueInt("INS_CROWD_TYPE") == 1){//��ְ
        //����סԺ�����걨
        this.DataDown_sp_H(parm);
//        System.out.println("onSettlement===5");
        //��ѯͬ��סԺ���Ƿ����
      if (this.DataDown_sp_Q(parm).getErrCode() < 0) {
          return ;
      }
//      System.out.println("onSettlement===6");
        //�õ���������
        parm.setData("DS_DATE", df.format(this.getValue("DS_DATE")));
        TParm result1 = INSUpLoadTool.getInstance().getIBSData(parm);
        if (result1.getErrCode() < 0) {
            this.messageBox(result1.getErrText());
            return;
        }  
        //��Ժ����  
        parm.setData("REGION_CODE", Operator.getRegion());
        //�õ��������������2
        TParm result3 = INSUpLoadTool.getInstance().getIBSHelpAmt(parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return;
        }
        //�õ�ҽʦ֤�պ�
        TParm result4 = INSUpLoadTool.getInstance().getDrQualifyCode(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return;
        }
        result1.setData("DRQUALIFYCODE", result4.getData("DRQUALIFYCODE", 0));
        result1.addData("ARMYAI_AMT", result3.getData("ARMYAI_AMT", 0));
        result1.addData("TOT_PUBMANADD_AMT",
                        result3.getData("TOT_PUBMANADD_AMT", 0));
//       System.out.println("�ϴ����"+result1);
      //�����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�
        TParm upParm = new TParm();
        if(parm.getValue("SDISEASE_CODE").length()>0){
        	 //�õ������ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�������Ϣ��ѯ
            TParm result8 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
            if (result8.getErrCode() < 0) {
                this.messageBox(result8.getErrText());
                return;
            } 	
           upParm = this.DataDown_ssks_G(result1,result8);
        }else
        //������Ϣ�ͳ�Ժ��Ϣ�ϴ�	
        upParm = this.DataDown_ssks_F(result1);
        if (upParm.getErrCode() < 0) {
            return;
        } 
//        System.out.println("onSettlement===7");
        double accountPayAmt = upParm.getDouble("ACCOUNT_PAY_AMT");
        double personAccountAmt = upParm.getDouble("OVER_AMT");
        parm.setData("ACCOUNT_PAY_AMT", accountPayAmt);
        parm.setData("PERSON_ACCOUNT_AMT", personAccountAmt);
        parm.setData("NEW_CONFIRM_NO", upParm.getValue("NEW_CONFIRM_NO"));
//        System.out.println("parm============"+parm);
        //�����¾�ҽ˳��š������ʻ�ʵ��֧�����ʻ����
        TParm result = TIOM_AppServer.executeAction("action.ins.INSUpLoadAction",
                                              "saveUpLoadData", parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }        
//        System.out.println("onSettlement===8");
    	//��ְ������ҳ����
    	this.DataUpload_G1(MRO,"CZ");
        //������ҳ�ϴ�
        if (this.DataUpload_G(MRO,"CZ").getErrCode() < 0) {        
            return;
        }
//        System.out.println("onSettlement===9");
        if(MROOP.getCount()>0){
        //סԺ������ҳ֮�����������ϴ�
          if (this.DataUpload_H(MROOP,"CZ").getErrCode() < 0) {
               return;
            }
          }
//        System.out.println("onSettlement===10");        
      }
        else  if (this.getValueInt("INS_CROWD_TYPE") == 2){//�Ǿ�
        //����סԺ�����걨
        this.DataDown_czys_I(parm);
//        System.out.println("onSettlement===11");
        //��ѯ�ʸ�ȷ����������
        TParm czysDParm = this.DataDown_czys_D(parm);
        if (czysDParm.getErrCode() < 0) {
            return ;
        }
//        System.out.println("onSettlement===12");
        if (!czysDParm.getBoolean("ALLOW_FLG_FLG"))
            return ;
//        System.out.println("onSettlement===13");
        //��Ժ����  
        parm.setData("REGION_CODE", Operator.getRegion());
//        System.out.println("onSettlement===14rrrr"+this.getValue("DS_DATE"));
        parm.setData("DS_DATE", df.format(this.getValue("DS_DATE")));
//        System.out.println("onSettlement===rrrr"+parm);
        //���ҽ��������Ϣ
        TParm result2 = INSUpLoadTool.getInstance().getINSMedAppInfo(parm);
//        System.out.println("onSettlement===dddddd"+result2);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return ;
        } 
       //�����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�
        TParm upParm = new TParm();
        if(parm.getValue("SDISEASE_CODE").length()>0){
        TParm result6 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
        if (result6.getErrCode() < 0) {
            this.messageBox(result6.getErrText());
            return;
        }
        upParm = this.DataDown_csks_G(result2,result6);
        }else
        //������Ϣ�ͳ�Ժ��Ϣ�ϴ�	
        upParm = this.DataDown_csks_F(result2);
        
//        System.out.println("onSettlement===eeeee"+csksFParm);
        if (upParm.getErrCode() < 0) {
            return ;
        }
        String newAdmSeq = upParm.getValue("NEWADM_SEQ");
        parm.setData("NEWADM_SEQ", newAdmSeq);
//        System.out.println("onSettlement===wwww"+parm);
        //�����µľ���˳���
        TParm result3 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAdmSeqData", parm);
//        System.out.println("onSettlement===qqqq"+result3);  
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return;
        }
//        System.out.println("onSettlement===14");
        //��ְ������ҳ����
    	this.DataUpload_G1(MRO,"CJ");
        //������ҳ�ϴ�
        if (this.DataUpload_G(MRO,"CJ").getErrCode() < 0) {       	
            return;
        }
//        System.out.println("onSettlement===15");
        if(MROOP.getCount()>0){
        //�����������ϴ�
         if (this.DataUpload_H(MROOP,"CJ").getErrCode() < 0) {
              return;
          }
        }  
        
     }
       
		this.messageBox("P0005"); // ִ�гɹ�
	}	
	/**
	 * ���һ���ϴ���ϸ���ۼ�����
	 */
	public TParm updateAddDetail(TParm parm,TParm drparm) {		
		TParm data = new TParm();	
		parm.setData("REGION_CODE", Operator.getRegion());
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//��ʼʱ��
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//����ʱ��	
		//�ϴ�����ϸ
		String sqldetal = " SELECT B.CONFIRM_NO,A.ADM_SEQ,B.INSBRANCH_CODE," +
		" TO_CHAR(A.CHARGE_DATE,'yyyy-mm-dd HH:mm:ss') AS CHARGE_DATE," +
		" A.SEQ_NO,A.NHI_ORDER_CODE,A.ORDER_DESC,A.OWN_RATE,C.JX,C.GG," +
		" A.PRICE,A.QTY,A.TOTAL_AMT,A.TOTAL_NHI_AMT,A.OWN_AMT,A.ADDPAY_AMT," +
		" A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE,A.PHAADD_FLG," +
		" A.CARRY_FLG,C.PZWH,B.ADM_CATEGORY AS PAY_TYPE," +
		" TO_CHAR(A.CHARGE_DATE,'yyyymmdd') AS CHARGE_DATE_PAT" +
		" FROM  INS_IBS_UPLOAD A ,INS_ADM_CONFIRM B ,INS_RULE C" +
		" WHERE A.REGION_CODE ='"+ parm.getValue("REGION_CODE") + "'" +
		" AND A.ADM_SEQ= '"+ parm.getValue("ADM_SEQ") + "'" +
		" AND A.ADM_SEQ = B.ADM_SEQ" +
		" AND A.QTY<>0" +
		" AND A.ORDER_CODE != '***018'" +
		" AND A.NHI_ORDER_CODE = C.SFXMBM" +
		" AND A.CHARGE_DATE BETWEEN" + 
		" TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')" + 
		" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
		" AND A.CHARGE_DATE BETWEEN C.KSSJ AND C.JSSJ" +
		" UNION  ALL"+
		" SELECT B.CONFIRM_NO,A.ADM_SEQ,B.INSBRANCH_CODE," +
		" TO_CHAR(A.CHARGE_DATE,'yyyy-mm-dd HH:mm:ss') AS CHARGE_DATE," +
		" A.SEQ_NO,A.NHI_ORDER_CODE,A.ORDER_DESC,A.OWN_RATE," +
		" A.DOSE_CODE AS JX,A.STANDARD AS GG," +
		" A.PRICE,A.QTY,A.TOTAL_AMT,A.TOTAL_NHI_AMT,A.OWN_AMT,A.ADDPAY_AMT," +
		" A.OP_FLG,A.ADDPAY_FLG,A.NHI_ORD_CLASS_CODE,A.PHAADD_FLG," +
		" A.CARRY_FLG,'',B.ADM_CATEGORY AS PAY_TYPE,TO_CHAR(A.CHARGE_DATE,'yyyymmdd') AS CHARGE_DATE_PAT" +
		" FROM  INS_IBS_UPLOAD A ,INS_ADM_CONFIRM B" +
		" WHERE A.REGION_CODE ='"+ parm.getValue("REGION_CODE") + "'" +
		" AND A.ADM_SEQ= '"+ parm.getValue("ADM_SEQ") + "'" +
		" AND A.ADM_SEQ = B.ADM_SEQ" +
		" AND A.ORDER_CODE = '***018'";
//		System.out.println("ondetailUpdate===WWWWW" + sqldetal);
		TParm result = new TParm(TJDODBTool.getInstance().select(sqldetal));
//		  System.out.println("ondetailUpdate===1" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("ִ��ʧ��");
			return result;
		}
		 data = this.DataUploadDetail(result,drparm);
		 if (data.getErrCode() < 0) {			 
			 this.messageBox(data.getErrText());
			 return data;
	        }		 		
		 else{
			     //�������
			     String sql2 =" SELECT A.ICD_CODE,B.ICD_CHN_DESC"+ 
			     " FROM ADM_INPDIAG A,SYS_DIAGNOSIS B"+ 
			     " WHERE A.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
			     " AND A.IO_TYPE = 'M'"+ 
			     " AND A.ICD_CODE = B.ICD_CODE";    
		     TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
		     if (result2.getErrCode() < 0) {
		            this.messageBox(result2.getErrText());
		            return result2;
		        }
		      data = this.DataUploadPat(result,drparm,result2);
			 if (data.getErrCode() < 0) {			 
				 this.messageBox(data.getErrText());
				 return data;
		        }	
			//����INS_IBS_UPLOAD�ֶ�up_flgΪ2�����ϴ�
		  for (int i = 0; i < result.getCount("SEQ_NO"); i++) {
				TParm data3 = result.getRow(i);
				String sql3= " UPDATE INS_IBS_UPLOAD SET UP_FLG = '2'," +
						     " UP_DATE = SYSDATE"+
				             " WHERE ADM_SEQ='"+ data3.getValue("ADM_SEQ")+ "'"+
				             " AND SEQ_NO='"+ data3.getValue("SEQ_NO")+ "'"; 
				TParm result3 = new TParm(TJDODBTool.getInstance().update(sql3));  
				if (result3.getErrCode() < 0) {
		              return result3;
		        }
		    }		
		 }		 
		 return result;	
	}
	/**
	 * ����Ԥ���㱣���Ժ��Ϣ	
	 */
	public TParm onBlance(TParm parm) {
		// �����ֲ���У������
		if (null != type && type.equals("SINGLE")) {

			if (null == this.getValue("ADM_DAYS")
					|| this.getValue("ADM_DAYS").toString().length() <= 0) {
				this.messageBox("סԺ��������Ϊ��");
				this.grabFocus("ADM_DAYS");
				tabbedPane.setSelectedIndex(1);
				return null;
			}
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion()); // ����
		parm.setData("REALOWN_RATE", this.getValue("REALOWN_RATE")); // ����ʵ���Ը�����
		parm.setData("INS_CROWD_TYPE", this.getValueInt("INS_CROWD_TYPE")); // ��Ⱥ���
		parm.setData("TYPE", type); // type:SINGLE �����ֲ���ʹ��

		parm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("DS_DATE"), true)); // ��Ժʱ�� ������

		parm.setData("ADM_DAYS", this.getValueInt("ADM_DAYS")); // סԺ����
		String[] name = showValue.split(";"); // ������޸����ݻ��
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i], this.getValue(name[i])); // �����޸ĵ�����
		}
		parm.setData("CHEMICAL_DESC", this.getText("CHEMICAL_DESC")); // ����˵��
		parm.setData("ADDAMT", addFee);
		// System.out.println("�������parm:::::"+parm);
		// �������
		TParm result = new TParm(INSTJAdm.getInstance().onSettlement(
				parm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return result;
		}
		result = INSIbsTool.getInstance().queryIbsSum(parm); // ��ѯ���ݸ����渳ֵ
		setSumValue(result, parm);
		tabbedPane.setSelectedIndex(2);	
	    return result;
	}
    /**
     * ����סԺ�����걨
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_H(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "H");
        confInfoParm.addData("CONFIRM_NO", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO",0));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ѯͬ��סԺ���Ƿ����
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_sp_Q(TParm parm) {
        TParm result = new TParm();

        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_sp");
        confInfoParm.setData("PLOT_TYPE", "Q");
        confInfoParm.addData("CONFIRM_NO", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO",0));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �����걨(�Ǿ�)
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_I(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_czys");
        confInfoParm.setData("PLOT_TYPE", "I");

        confInfoParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_CODE", regionParm.getValue("NHI_NO",0));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ��ѯ�ʸ�ȷ����������(�Ǿ�)
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_czys_D(TParm parm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_czys");
        confInfoParm.setData("PLOT_TYPE", "D");

        confInfoParm.addData("CONFIRM_NO", parm.getValue("ADM_SEQ"));
        confInfoParm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO",0));
        confInfoParm.addData("PARM_COUNT", 2);
        result = InsManager.getInstance().safe(confInfoParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ������ҳ�ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_G(TParm parm,String type) {
        DecimalFormat df = new DecimalFormat("##########0.00");
    	 TParm result = new TParm();
         TParm mroParm = new TParm();
         if(type.equals("CZ"))
         mroParm.setData("PIPELINE", "DataDown_zjks");
         else if(type.equals("CJ"))
         mroParm.setData("PIPELINE", "DataDown_cjks");	 
         mroParm.setData("PLOT_TYPE", "G");
         mroParm.addData("HOSP_DESC", regionParm.getValue("REGION_CHN_DESC", 0));//ҽԺ����
         mroParm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0));//ҽԺ����
         mroParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//��ҽ˳���
         mroParm.addData("PAY_WAY", parm.getValue("PAY_WAY", 0).length()>0?
        		 parm.getValue("PAY_WAY", 0):"9");//ҽ�Ƹ��ѷ�ʽ
         mroParm.addData("CARD_NO", parm.getData("CARD_NO", 0));//��������
         mroParm.addData("IN_TIMES", parm.getData("IN_TIMES", 0));//��סԺ����
         mroParm.addData("MR_NO", parm.getData("MR_NO", 0));//������
         mroParm.addData("PAT_NAME", parm.getData("PAT_NAME", 0));//����
         mroParm.addData("SEX", parm.getData("SEX", 0));//�Ա�
         mroParm.addData("BIRTH_DATE", parm.getData("BIRTH_DATE", 0));//��������
         //�������
        String age = parm.getValue("AGE", 0);
        String age1 ="";
        String age2 ="";
        String age3 ="";
         int ageflg = Integer.valueOf(age.substring(0,age.indexOf("��")));
        if(ageflg>=1)
        	age1 = age.substring(0,age.indexOf("��"));
        else if(ageflg<1){
        	if(age.length()>3){
        	age1 = "0";
        	age2 = age.substring(age.indexOf("��")+1,age.indexOf("��"));
        	age3 = age.substring(age.indexOf("��")+1,age.indexOf("��"));
        	}
        	else
        	age1 = "0";	
        }
//        System.out.println("age1====:"+age1);
//        System.out.println("age2====:"+age2);
//        System.out.println("age3====:"+age3);
         mroParm.addData("AGE1", age1.length()>0?age1:"0");//����1
         mroParm.addData("NATION", parm.getData("NATION", 0));//����
         mroParm.addData("AGE2", age2.length()>0?age2:"0");//����2(��)
         mroParm.addData("AGE3", age3.length()>0?age3:"0");//����2(��)
         mroParm.addData("NB_WEIGHT", parm.getValue("NB_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_WEIGHT", 0):"0");//��������������
         mroParm.addData("NB_IN_WEIGHT", parm.getValue("NB_IN_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_IN_WEIGHT", 0):"0");//��������Ժ����
         mroParm.addData("BIRTH_ADDRESS", parm.getData("BIRTH_ADDRESS", 0));//������
         mroParm.addData("BIRTHPLACE", parm.getData("BIRTHPLACE", 0));//����
         mroParm.addData("FOLK", parm.getData("FOLK", 0));//����
         mroParm.addData("ID_NO", parm.getData("ID_NO", 0));//���֤��
         mroParm.addData("OCCUPATION", parm.getData("OCCUPATION", 0));//ְҵ
         mroParm.addData("MARRIGE", parm.getValue("MARRIGE", 0).length()>0?
        		 parm.getValue("MARRIGE", 0):"9");//����״��
         mroParm.addData("ADDRESS", parm.getData("ADDRESS", 0));//��סַ
         mroParm.addData("ADDRESS_TEL", parm.getData("ADDRESS_TEL", 0));//��סַ�绰
         mroParm.addData("POST_NO", parm.getData("POST_NO", 0));//��סַ�ʱ�
         mroParm.addData("H_ADDRESS", parm.getData("H_ADDRESS", 0));//���ڵ�ַ
         mroParm.addData("POST_CODE", parm.getData("POST_CODE", 0));//�������ڵ��ʱ�
         mroParm.addData("O_ADDRESS", parm.getData("O_ADDRESS", 0));//������λ����ַ
         mroParm.addData("O_TEL", parm.getData("O_TEL", 0));//������λ�绰
         mroParm.addData("O_POSTNO", parm.getData("O_POSTNO", 0));//��λ�ʱ�
         mroParm.addData("CONTACTER", parm.getData("CONTACTER", 0));//��ϵ������
         mroParm.addData("RELATIONSHIP", parm.getData("RELATIONSHIP", 0));//�뻼�߹�ϵ
         mroParm.addData("CONT_ADDRESS", parm.getData("CONT_ADDRESS", 0));//��ϵ�˵�ַ
         mroParm.addData("CONT_TEL", parm.getData("CONT_TEL", 0));//��ϵ�˵绰
         mroParm.addData("ADM_SOURCE", parm.getValue("ADM_SOURCE", 0).length()>0?
        		 parm.getValue("ADM_SOURCE", 0):"9");//��Ժ;��
         mroParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//��Ժʱ��
         mroParm.addData("IN_DEPT", parm.getData("IN_DEPT", 0));//��Ժ�Ʊ�
         mroParm.addData("IN_STATION", parm.getData("IN_STATION", 0));//��Ժ����
         mroParm.addData("TRANS_DEPT", parm.getData("TRANS_DEPT", 0));//ת�ƿƱ�
         mroParm.addData("OUT_DATE", parm.getData("OUT_DATE", 0));//��Ժʱ��
         mroParm.addData("OUT_DEPT", parm.getData("OUT_DEPT", 0));//��Ժ�Ʊ�
         mroParm.addData("OUT_STATION", parm.getData("OUT_STATION", 0));//��Ժ����
         mroParm.addData("REAL_STAY_DAYS", parm.getData("REAL_STAY_DAYS", 0));//ʵ��סԺ����
         mroParm.addData("OE_DIAG_DESC", parm.getData("OE_DIAG_DESC", 0));//�ţ����������
         mroParm.addData("OE_DIAG_CODE", parm.getData("OE_DIAG_CODE", 0));//�ţ������Ｒ������
         mroParm.addData("OUT_DIAG_MAIN", parm.getData("OUT_DIAG_MAIN", 0));//��Ժ��Ҫ���
         mroParm.addData("OUT_DIAG_OTHER", parm.getData("OUT_DIAG_OTHER", 0));//��Ժ�������
         mroParm.addData("EX_RSN_DESC", parm.getData("EX_RSN_DESC", 0));//���ˡ��ж����ⲿԭ��
         mroParm.addData("EX_RSN_CODE", parm.getData("EX_RSN_CODE", 0));//���ˡ��ж��ļ�������
         mroParm.addData("PATHOLOGY_DIAG", parm.getData("PATHOLOGY_DIAG", 0));//�������
         mroParm.addData("PATHOLOGY_DIAG_CODE", parm.getData("PATHOLOGY_DIAG_CODE", 0));//������ϼ�������
         mroParm.addData("PATHOLOGY_NO", parm.getData("PATHOLOGY_NO", 0));//�����
         mroParm.addData("ALLEGIC_FLG", parm.getValue("ALLEGIC_FLG", 0).length()>0?
        		 parm.getValue("ALLEGIC_FLG", 0):"1");//ҩ�������־
         mroParm.addData("ALLEGIC", parm.getData("ALLEGIC", 0));//����ҩ��
         mroParm.addData("BODY_CHECK", parm.getValue("BODY_CHECK", 0).length()>0?
        		 parm.getValue("BODY_CHECK", 0):"1");//��������ʬ���־
         mroParm.addData("BLOOD_TYPE", parm.getValue("BLOOD_TYPE", 0).length()>0?
        		 parm.getValue("BLOOD_TYPE", 0):"6");//Ѫ��
         mroParm.addData("RH_TYPE", parm.getValue("RH_TYPE", 0).length()>0?
        		 parm.getValue("RH_TYPE", 0):"4");//RH
         mroParm.addData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE", 0));//������
         mroParm.addData("PROF_DR_CODE", parm.getData("PROF_DR_CODE", 0));//���Σ������Σ�ҽʦ
         mroParm.addData("ATTEND_DR_CODE", parm.getData("ATTEND_DR_CODE", 0));//����ҽʦ
         mroParm.addData("VS_DR_CODE", parm.getData("VS_DR_CODE", 0));//סԺҽʦ
         mroParm.addData("VS_NURSE_CODE", parm.getData("VS_NURSE_CODE", 0));//���λ�ʿ
         mroParm.addData("INDUCATION_DR_CODE", parm.getData("INDUCATION_DR_CODE", 0));//����ҽʦ
         mroParm.addData("INTERN_DR_CODE", parm.getData("INTERN_DR_CODE", 0));//ʵϰҽʦ
         mroParm.addData("ENCODER", parm.getData("ENCODER", 0));//����Ա
         mroParm.addData("QUALITY", parm.getData("QUALITY", 0));//��������
         mroParm.addData("CTRL_DR", parm.getData("CTRL_DR", 0));//�ʿ�ҽʦ
         mroParm.addData("CTRL_NURSE", parm.getData("CTRL_NURSE", 0));//�ʿػ�ʿ
         mroParm.addData("CTRL_DATE", parm.getData("CTRL_DATE", 0));//�ʿ�����
         mroParm.addData("OUT_TYPE", parm.getValue("OUT_TYPE", 0).length()>0?
        		 parm.getValue("OUT_TYPE", 0):"9");//��Ժ��ʽ
         mroParm.addData("TRAN_HOSP", parm.getData("TRAN_HOSP", 0));//�����ҽ�ƻ�������
         mroParm.addData("AGN_PLAN_FLG", parm.getValue("AGN_PLAN_FLG", 0).length()>0?
        		 parm.getValue("AGN_PLAN_FLG", 0):"1");//��Ժ31������סԺ
         mroParm.addData("AGN_INTENTION", parm.getData("AGN_PLAN_INTENTION", 0));//��סԺĿ��
         //­�����˻��߻�����Ժǰʱ��
         String becomatime = parm.getValue("BE_COMA_TIME", 0).length()>0? 
        		             parm.getValue("BE_COMA_TIME", 0):"000000";
         becomatime = becomatime.substring(0, 2)+"@"+
                      becomatime.substring(2, 4)+"@"+
                      becomatime.substring(4, 6);
         //­�����˻��߻�����Ժ��ʱ��
         String afcomatime = parm.getValue("AF_COMA_TIME", 0).length()>0? 
	                         parm.getValue("AF_COMA_TIME", 0):"000000";;
         afcomatime = afcomatime.substring(0, 2)+"@"+
                      afcomatime.substring(2, 4)+"@"+
                      afcomatime.substring(4, 6);
//         System.out.println("afcomatime:"+afcomatime);
         mroParm.addData("BE_COMA_TIME", becomatime);//­�����˻��߻�����Ժǰʱ��
         mroParm.addData("AF_COMA_TIME", afcomatime);//­�����˻��߻�����Ժ��ʱ��
         mroParm.addData("SUM_TOT", parm.getData("SUM_TOT", 0));//סԺ�ܽ��
         mroParm.addData("OWN_TOT", parm.getData("OWN_TOT", 0));//סԺ�Ը����
         mroParm.addData("CHARGE_01", parm.getData("CHARGE_01", 0));//һ��ҽ�Ʒ����
         mroParm.addData("CHARGE_02", parm.getData("CHARGE_02", 0));//һ�����Ʋ�����
         mroParm.addData("CHARGE_03", parm.getData("CHARGE_03", 0));//�����
         mroParm.addData("CHARGE_04", parm.getData("CHARGE_04", 0));//�ۺ�ҽ����������
         mroParm.addData("CHARGE_05", parm.getData("CHARGE_05", 0));//������Ϸ�
         mroParm.addData("CHARGE_06", parm.getData("CHARGE_06", 0));//ʵ������Ϸ�
         mroParm.addData("CHARGE_07", parm.getData("CHARGE_07", 0));//Ӱ��ѧ��Ϸ�
         mroParm.addData("CHARGE_08", parm.getData("CHARGE_08", 0));//�ٴ������Ŀ��
         
         //���������������Ŀ��
         double charge09 = parm.getDouble("CHARGE_09",0);//�ٴ��������Ʒ�
         double charge10 = parm.getDouble("CHARGE_10",0);//���ٴ��������Ʒ�    
         mroParm.addData("CHARGE_09", parm.getData("CHARGE_09", 0));//�ٴ��������Ʒ�
         mroParm.addData("CHARGE_10", df.format(charge09+charge10));//������������Ŀ��
       
         //�����������Ʒ�
         double charge11 = parm.getDouble("CHARGE_11",0);//�����
         double charge12 = parm.getDouble("CHARGE_12",0);//������
         double charge13 = parm.getDouble("CHARGE_13",0);//�������Ʒ�����        
         mroParm.addData("CHARGE_13", df.format(charge11+charge12+charge13));//�������Ʒ�
         mroParm.addData("CHARGE_11", parm.getData("CHARGE_11", 0));//�����
         mroParm.addData("CHARGE_12", parm.getData("CHARGE_12", 0));//������
         mroParm.addData("CHARGE_14", parm.getData("CHARGE_14", 0));//������
         mroParm.addData("CHARGE_15", parm.getData("CHARGE_15", 0));//��ҽ���Ʒ�
         //������ҩ��
         double charge16 = parm.getDouble("CHARGE_16",0);
         double charge17 = parm.getDouble("CHARGE_17",0);
         mroParm.addData("CHARGE_16_17", df.format(charge16+charge17));//��ҩ��
         mroParm.addData("CHARGE_16", parm.getData("CHARGE_16", 0));//����ҩ�����
         mroParm.addData("CHARGE_18", parm.getData("CHARGE_18", 0));//�г�ҩ��
         mroParm.addData("CHARGE_19", parm.getData("CHARGE_19", 0));//�в�ҩ��
         mroParm.addData("CHARGE_20", parm.getData("CHARGE_20", 0));//Ѫ��
         mroParm.addData("CHARGE_21", parm.getData("CHARGE_21", 0));//�׵�������Ʒ��
         mroParm.addData("CHARGE_22", parm.getData("CHARGE_22", 0));//�򵰰�����Ʒ��
         mroParm.addData("CHARGE_23", parm.getData("CHARGE_23", 0));//��Ѫ��������Ʒ��
         mroParm.addData("CHARGE_24", parm.getData("CHARGE_24", 0));//ϸ����������Ʒ��
         mroParm.addData("CHARGE_25", parm.getData("CHARGE_25", 0));//�����һ����ҽ�ò��Ϸ�
         mroParm.addData("CHARGE_26", parm.getData("CHARGE_26", 0));//������һ����ҽ�ò��Ϸ�
         mroParm.addData("CHARGE_27", parm.getData("CHARGE_27", 0));//������һ����ҽ�ò��Ϸ�
         mroParm.addData("CHARGE_28", parm.getData("CHARGE_28", 0));//������ 
         //��֢�໤
         String icuRoom = "";
         if(parm.getValue("ICU_ROOM1", 0).length()==0&&
        	parm.getValue("ICU_ROOM2", 0).length()==0&&
        	parm.getValue("ICU_ROOM3", 0).length()==0&&
        	parm.getValue("ICU_ROOM4", 0).length()==0&&
        	parm.getValue("ICU_ROOM5", 0).length()==0)
            mroParm.addData("ICU_ROOM",icuRoom);//��֢�໤  
         else{
        	 for (int j = 1; j < 6; j++) {
        		 System.out.println("ICU_ROOM"+j+":"+parm.getValue("ICU_ROOM"+j, 0));
            	 System.out.println("ICU_IN_DATE"+j+":"+parm.getValue("ICU_IN_DATE"+j, 0));
            	 System.out.println("ICU_OUT_DATE"+j+":"+parm.getValue("ICU_OUT_DATE"+j, 0)); 
        	    if(parm.getValue("ICU_ROOM"+j, 0).length()>0&&
        	       parm.getValue("ICU_IN_DATE"+j, 0).length()>0&&
        	       parm.getValue("ICU_OUT_DATE"+j, 0).length()>0){
        	     String indate = StringTool.getString(
        	        parm.getTimestamp("ICU_IN_DATE"+j, 0), "yyyy-MM-dd HH");
        	     String outdate = StringTool.getString(
        	        parm.getTimestamp("ICU_OUT_DATE"+j, 0), "yyyy-MM-dd HH");       	
        	     icuRoom += parm.getValue("ICU_ROOM"+j, 0)+"@"+indate+"@"+outdate+"%"; 
        	 }
           } 
             System.out.println("icuRoom:======"+icuRoom);
             mroParm.addData("ICU_ROOM",icuRoom.substring(0, icuRoom.length() - 1));//��֢�໤ 
         }
         mroParm.addData("VENTI_TIME",parm.getValue("VENTI_TIME", 0).length()>0?
        		 parm.getValue("VENTI_TIME", 0):"0");//�ۼ�ʹ��Сʱ��
         mroParm.addData("PARM_COUNT", 107);//�������
//         System.out.println("mroParm:"+mroParm);
         result = InsManager.getInstance().safe(mroParm);
//         System.out.println("result" + result);
         if (result.getErrCode() < 0) {
             this.messageBox(result.getErrText());
             return result;
         }
    	 return result; 
    }
    /**
     * סԺ������ҳ֮�����������ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_H(TParm parm,String type) {
    	TParm result = new TParm();
        TParm mroopParm = new TParm();        
        int count = parm.getCount("ADM_SEQ");
        if(type.equals("CZ"))
        mroopParm.setData("PIPELINE", "DataDown_zjks");
        else if(type.equals("CJ"))
        mroopParm.setData("PIPELINE", "DataDown_cjks");	
        mroopParm.setData("PLOT_TYPE", "H");
        for (int i = 0; i < count; i++) {
        	mroopParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ", i));//��ҽ˳���
        	mroopParm.addData("OPT_CODE", parm.getValue("OPT_CODE", i));//��������
        	mroopParm.addData("OP_DATE", parm.getValue("OP_DATE", i));//����
        	mroopParm.addData("OP_LEVEL", parm.getValue("OP_LEVEL", i));//��������
        	mroopParm.addData("OP_NAME", parm.getValue("OP_NAME", i));//��������
        	mroopParm.addData("OP_DR_NAME", parm.getValue("OP_DR_NAME", i));//����ҽʦ����
        	mroopParm.addData("AST_DR1", parm.getValue("AST_DR1", i));//1������
        	mroopParm.addData("AST_DR2", parm.getValue("AST_DR2", i));//2������
        	mroopParm.addData("HEAL_LEV", parm.getValue("HEAL_LEV", i));//�п����ϵȼ�
        	mroopParm.addData("ANA_WAY", parm.getValue("ANA_WAY", i));//����ʽ
        	mroopParm.addData("ANA_DR", parm.getValue("ANA_DR", i));//����ҽʦ
        	mroopParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));//���
        	mroopParm.addData("PARM_COUNT", 12);//�������
        }
//        System.out.println("mroopParm:"+mroopParm);
        result = InsManager.getInstance().safe(mroopParm);
//        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
    	 return result; 
    } 
    /**
     * ������Ϣ�ͳ�Ժ��Ϣ�ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_ssks_F(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_ssks");
        confInParm.setData("PLOT_TYPE", "F");

        confInParm.addData("CONFIRM_NO", parm.getData("ADM_SEQ", 0));//��ҽ˳���
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));//�ʸ�ȷ������Դ
        confInParm.addData("SID", parm.getData("IDNO", 0));//���֤����
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));//ҽԺ����
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));//ҽԺ����������
        confInParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));//��Ա���
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));//��ҽ���
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//��Ժʱ��
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));//��Ժʱ��
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().
        		selInsICDCode(parm.getValue("DIAG_CODE", 0)));//��Ժ���
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));//��Ժ�������
        //����Ͻ�ȡ����
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }         
        confInParm.addData("DIAG_DESC2", diagdesc2);//��Ժ�������
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));//��Ժ���
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);//�Ը�����
        confInParm.addData("DECREASE_RATE",
                           parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("DECREASE_RATE", 0) / 100);//��������
        confInParm.addData("REALOWN_RATE",
                           parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("REALOWN_RATE", 0) / 100);//ʵ���Ը�����
        confInParm.addData("INSOWN_RATE",
                           parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("INSOWN_RATE", 0) / 100);//ҽ�ƾ����Ը�����
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));//סԺ��
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));//סԺ���� 
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));//סԺ��λ��
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));//סԺ�Ʊ�
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));//����ҽ��ʣ���
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));//ҽ�ƾ���ʣ���
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));//ʵ���𸶱�׼
        confInParm.addData("ISSUE", parm.getValue("YEAR_MON", 0).substring(0, 6));//�ں�
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));//ҩƷ�ѷ������
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));//ҩƷ���걨���
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));//���ѷ������
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));//�����걨���
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));//���Ʒѷ������
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));//���Ʒ��걨���
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));//�����ѷ������
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));//�������걨���
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));//��λ�ѷ������
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));//��λ���걨���
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));//ҽ�ò��Ϸ������
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));//ҽ�ò����걨���
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));//�����������
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));//�����걨���
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));//��ȫѪ�������
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));//��ȫѪ�걨���
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));//�ɷ���Ѫ�������
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));//�ɷ���Ѫ�걨���
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));//����ʵ���𸶱�׼���
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));//�𸶱�׼�����Ը��������
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));//�Է���Ŀ���
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));//ҽ�ƾ������˰������������
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));//������Ŀ���
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));//ҽ�ƾ�������޶����Ͻ��
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));//��Ѫ�Ը����
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));//����ҽ���籣������
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));//ҽ�ƾ����籣������
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));//סԺ�Ʊ����
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));//����˵��
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));//��ҽ��Ŀ
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));//�������
        confInParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));//�������
        confInParm.addData("COMU_NO", "");//��������
        confInParm.addData("DR_CODE", parm.getData("DRQUALIFYCODE",0));//ҽʦ����
        confInParm.addData("PUBMANAI_AMT", parm.getData("TOT_PUBMANADD_AMT", 0));//�������2
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));//������Ժ���
        //���ָ����˳�ԭ��
        confInParm.addData("QUIT_REMARK", parm.getValue("QUIT_REMARK", 0).length()>0?
          		 parm.getValue("QUIT_REMARK", 0):"");
        confInParm.addData("PARM_COUNT", 62);
//       System.out.println("DataDown_ssks_F�ӿ����======"+confInParm);
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * �����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_ssks_G(TParm parm,TParm dataParm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
        confInfoParm.setData("PIPELINE", "DataDown_ssks");
        confInfoParm.setData("PLOT_TYPE", "G");

        confInfoParm.addData("CONFIRM_NO", parm.getData("ADM_SEQ", 0));
        confInfoParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        confInfoParm.addData("SID", parm.getData("IDNO", 0));
        confInfoParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        confInfoParm.addData("HOSP_CLEFT_CENTER",
                             parm.getData("INSBRANCH_CODE", 0));
        confInfoParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
        confInfoParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        confInfoParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        confInfoParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
        //String diagCode  =  ""+parm.getData("DIAG_CODE", 0);
        //ƥ���������
        confInfoParm.addData("DIAG_CODE",INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        confInfoParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //����Ͻ�ȡ����
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }            
        confInfoParm.addData("DIAG_DESC2", diagdesc2);
        confInfoParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        confInfoParm.addData("OWN_RATE",
                             parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("OWN_RATE", 0) / 100);
        confInfoParm.addData("DECREASE_RATE",
                             parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("DECREASE_RATE", 0) / 100);
        confInfoParm.addData("REALOWN_RATE",
                             parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("REALOWN_RATE", 0) / 100);
        confInfoParm.addData("INSOWN_RATE",
                             parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                             parm.getDouble("INSOWN_RATE", 0) / 100);
        confInfoParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        confInfoParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        confInfoParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        confInfoParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        confInfoParm.addData("BASEMED_BALANCE",
                             parm.getData("BASEMED_BALANCE", 0));
        confInfoParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
//        confInfoParm.addData("STANDARD_AMT",
//                             parm.getData("START_STANDARD_AMT", 0));
        confInfoParm.addData("STANDARD_AMT",
                parm.getData("RESTART_STANDARD_AMT", 0));       
        confInfoParm.addData("ISSUE", parm.getData("YEAR_MON", 0));
        confInfoParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        confInfoParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        confInfoParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        confInfoParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        confInfoParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        confInfoParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        confInfoParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        confInfoParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        confInfoParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        confInfoParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        confInfoParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        confInfoParm.addData("MATERIAL_NHI_AMT",
                             parm.getData("MATERIAL_NHI_AMT", 0));
        confInfoParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        confInfoParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        confInfoParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        confInfoParm.addData("BLOODALL_NHI_AMT",
                             parm.getData("BLOODALL_NHI_AMT", 0));
        confInfoParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        confInfoParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        confInfoParm.addData("NHI_OWN_AMT", parm.getData("SINGLE_NHI_AMT", 0)); //�����걨���
        confInfoParm.addData("EXT_OWN_AMT",
                             parm.getData("SINGLE_STANDARD_OWN_AMT", 0)); //ҽԺ�����ֱ�׼�Ը����
        confInfoParm.addData("COMP_AMT", parm.getData("SINGLE_SUPPLYING_AMT", 0)); //����ҽ�Ʊ��ղ�����
        confInfoParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        confInfoParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //ͳ������Ը���׼���
        confInfoParm.addData("APPLY_OWN_AMT_STD", dataParm.getData("STARTPAY_OWN_AMT", 0));
        //ҽ�ƾ����Ը���׼���
        confInfoParm.addData("INS_OWN_AMT_STD", dataParm.getData("PERCOPAYMENT_RATE_AMT", 0)); 
        confInfoParm.addData("INS_HIGHLIMIT_AMT",
                             parm.getData("INS_HIGHLIMIT_AMT", 0));
        confInfoParm.addData("TRANBLOOD_OWN_AMT",
                             parm.getData("BLOODALL_OWN_AMT", 0));
        confInfoParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        confInfoParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        confInfoParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        confInfoParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        confInfoParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        confInfoParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        //�������
        confInfoParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));
        //��������
        confInfoParm.addData("COMU_NO", "");
        //�����ֱ���
        confInfoParm.addData("SIN_DISEASE_CODE", dataParm.getData("SDISEASE_CODE", 0)); 
        //ҽʦ����
        confInfoParm.addData("DR_CODE", parm.getData("DRQUALIFYCODE",0));
        //�������2
        confInfoParm.addData("PUBMANAI_AMT", parm.getData("PUBMANAI_AMT", 0));
        
        //�����Էѽ��
        double BED_SINGLE_AMT = dataParm.getDouble("BED_SINGLE_AMT", 0);
        double MATERIAL_SINGLE_AMT = dataParm.getDouble("MATERIAL_SINGLE_AMT", 0);
        double specNeedAmt = BED_SINGLE_AMT + MATERIAL_SINGLE_AMT;
        confInfoParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //������Ժ���
        confInfoParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //�����ϴ���ʽ
        confInfoParm.addData("SINGLE_UPLOAD_TYPE", parm.getValue("SINGLE_UPLOAD_TYPE", 0).length()>0?
          		 parm.getValue("SINGLE_UPLOAD_TYPE", 0):"");
        confInfoParm.addData("PARM_COUNT", 66);
        System.out.println("DataDown_ssks_G�ӿ����======"+confInfoParm);
        result = InsManager.getInstance().safe(confInfoParm);
        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ������Ϣ�ͳ�Ժ��Ϣ�ϴ�(�Ǿ�)
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_csks_F(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_csks");
        confInParm.setData("PLOT_TYPE", "F");

        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//��ҽ˳���
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));//�ʸ�ȷ������Դ
        confInParm.addData("SID", parm.getData("IDNO", 0));//���֤����
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));//ҽԺ����
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));//ҽԺ����������
        confInParm.addData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));//��Ա���
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));//��ҽ���
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//��Ժʱ��
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));//��Ժʱ��
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().
        		selInsICDCode(parm.getValue("DIAG_CODE", 0)));//��Ժ���
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));//��Ժ�������
        //����Ͻ�ȡ����
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }         
        confInParm.addData("DIAG_DESC2",diagdesc2);//��Ժ�������
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));//��Ժ���
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);//�Ը�����
        confInParm.addData("DECREASE_RATE",
                           parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("DECREASE_RATE", 0) / 100);//��������
        confInParm.addData("REALOWN_RATE",
                           parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("REALOWN_RATE", 0) / 100);//ʵ���Ը�����
        confInParm.addData("INSOWN_RATE",
                           parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("INSOWN_RATE", 0) / 100);//ҽ�ƾ����Ը�����
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));//סԺ��
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));//סԺ���� 
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));//סԺ��λ��
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));//סԺ�Ʊ�
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));//����ҽ��ʣ���
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));//ҽ�ƾ���ʣ���
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));//ʵ���𸶱�׼
        confInParm.addData("ISSUE", parm.getValue("YEAR_MON", 0).substring(0, 6));//�ں�
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));//ҩƷ�ѷ������
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));//ҩƷ���걨���
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));//���ѷ������
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));//�����걨���
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));//���Ʒѷ������
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));//���Ʒ��걨���
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));//�����ѷ������
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));//�������걨���
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));//��λ�ѷ������
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));//��λ���걨���
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));//ҽ�ò��Ϸ������
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));//ҽ�ò����걨���
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));//�����������
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));//�����걨���
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));//��ȫѪ�������
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));//��ȫѪ�걨���
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));//�ɷ���Ѫ�������
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));//�ɷ���Ѫ�걨���
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));//����ʵ���𸶱�׼���
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));//�𸶱�׼�����Ը��������
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));//�Է���Ŀ���
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));//ҽ�ƾ������˰������������
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));//������Ŀ���
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));//ҽ�ƾ�������޶����Ͻ��
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));//��Ѫ�Ը����
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));//����ҽ���籣������
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));//ҽ�ƾ����籣������
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));//סԺ�Ʊ����
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));//����˵��
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));//��ҽ��Ŀ
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));//�������
        confInParm.addData("BEARING_OPERATIONS_TYPE",
                           parm.getData("BEARING_OPERATIONS_TYPE", 0));//�����������
        confInParm.addData("SOAR_CODE", "");//��������
        confInParm.addData("DR_QUALIFY_CODE", parm.getData("LCS_NO", 0));//ҽʦ����    
        confInParm.addData("AGENT_AMT", parm.getData("ARMYAI_AMT", 0));//�������
        confInParm.addData("BIRTH_TYPE", "");//������ʽ      
        confInParm.addData("BABY_NO", 0);//����̥������        
        confInParm.addData("ILLNESS_SUBSIDY_AMT", 
        		parm.getData("ILLNESS_SUBSIDY_AMT", 0));//����󲡾���       
        confInParm.addData("OTHER_DIAGE_CODE", 
        		parm.getData("OTHER_DIAGE_CODE", 0));//������Ժ��� 
        //���ָ����˳�ԭ��
        confInParm.addData("QUIT_REMARK", parm.getValue("QUIT_REMARK", 0).length()>0?
          		 parm.getValue("QUIT_REMARK", 0):"");
        confInParm.addData("PARM_COUNT", 65);
//        System.out.println("confInParm=====" + confInParm);
        result = InsManager.getInstance().safe(confInParm);
//        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }  
    /**
     * �����ֽ�����Ϣ�ͳ�Ժ��Ϣ�ϴ�(�Ǿ�)
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_csks_G(TParm parm, TParm dataParm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_csks");
        confInParm.setData("PLOT_TYPE", "G");
        //��ҽ˳��
        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));
        //�ʸ�ȷ������
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        //���֤��
        confInParm.addData("SID", parm.getData("IDNO", 0));
        //ҽԺ����
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        //ҽԺ����������
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));
        //��Ա���
        confInParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
        //��ҽ���
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        //��Ժʱ��
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        //��Ժʱ��
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
        //��Ժ���
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        //��Ժ�������
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //��Ժ�������
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }         
        confInParm.addData("DIAG_DESC2", diagdesc2);
        //��Ժ���
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        //�Ը�����
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);
        //��������
        confInParm.addData("DECREASE_RATE", parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("DECREASE_RATE", 0) / 100);
        //ʵ���Ը�����
        confInParm.addData("REALOWN_RATE", parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("REALOWN_RATE", 0) / 100);
        //ҽ�ƾ����Ը�����
        confInParm.addData("INSOWN_RATE", parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("INSOWN_RATE", 0) / 100);
        
        //סԺ��
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        //סԺ����
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        //סԺ��λ
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        //סԺ�Ʊ�
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        //����ҽ��ʣ���
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));
        //ҽ�ƾ�����
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
        //ʵ���𸶱�׼
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));
        //�ں�
        confInParm.addData("ISSUE", parm.getData("YEAR_MON", 0));
        //ҩƷ������
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        //ҩƷ�걨��
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        //���ѷ�����
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        //�����걨��
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        //���Ʒѷ�����
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        //���Ʒ��걨��
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        //�����ѷ�����
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        //�������걨��
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        //��λ�ѷ�����
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        //��λ���걨��
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        //ҽ�ò��Ϸ������
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        //ҽ�ò����걨���
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));
        //����������
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        //�����걨��
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        //��ȫѪ������
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        //��ȫѪ�걨��
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));
        //�ɷ���Ѫ������
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        //�ɷ���Ѫ�걨��
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        //����ʵ���𸶱�׼���
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));
        //�����걨���
        confInParm.addData("NHI_OWN_AMT", dataParm.getData("SINGLE_NHI_AMT", 0));
        //ҽԺ�����ֱ�׼�Ը����
        confInParm.addData("EXT_OWN_AMT",dataParm.getData("SINGLE_STANDARD_OWN_AMT", 0));
        //����ҽ�Ʊ��ղ�����
        confInParm.addData("COMP_AMT", dataParm.getData("SINGLE_SUPPLYING_AMT", 0));
        //�Է���Ŀ���
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        //������Ŀ���
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //�𸶱�׼�����Ը��������
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));
        //ҽ�ƾ������˰������������
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));
        //ҽ�ƾ�������޶����Ͻ��
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));
        //��Ѫ�Ը����
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));
        //����ҽ���籣������
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        //ҽ�ƾ����籣������
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        //סԺ�Ʊ����
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        //����˵��
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        //��ҽ��Ŀ
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        //�������
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        //��������
        confInParm.addData("COMU_NO", ""); //�̶���ֵ
        //�����ֱ���
        confInParm.addData("SIN_DISEASE_CODE", parm.getData("SDISEASE_CODE", 0));
        //ҽʦ����
        confInParm.addData("DR_CODE", parm.getData("LCS_NO", 0));
        //�������1
        double armyaiAmt = parm.getDouble("ARMYAI_AMT",0);
        //�������2
        double pubmanaiAmt = parm.getDouble("PUBMANAI_AMT",0);
        double agentAmt = armyaiAmt + pubmanaiAmt;
        //�������
        confInParm.addData("AGENT_AMT", agentAmt);
        //��λ��������
        double bedSingleAmt = dataParm.getDouble("BED_SINGLE_AMT",0);
        //ҽ�ò��Ϸ�������
        double materialSingleAmt = dataParm.getDouble("MATERIAL_SINGLE_AMT",0);
        double specNeedAmt = bedSingleAmt + materialSingleAmt;
        //System.out.println("specNeedAmt:"+specNeedAmt);
        //������Ŀ���
        confInParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //����󲡾���
        confInParm.addData("ILLNESS_SUBSIDY_AMT", parm.getData("ILLNESS_SUBSIDY_AMT", 0));
        //������Ժ���
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //�����ϴ���ʽ
        confInParm.addData("SINGLE_UPLOAD_TYPE", parm.getValue("SINGLE_UPLOAD_TYPE", 0).length()>0?
          		 parm.getValue("SINGLE_UPLOAD_TYPE", 0):"");
        //��θ���
        confInParm.addData("PARM_COUNT", 67);
        System.out.println("DataDown_csks:"+confInParm);
        result = InsManager.getInstance().safe(confInParm);
        System.out.println("DataDown_csks:" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * ������ҳ����
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUpload_G1(TParm parm,String type) {
    	TParm result = new TParm();
    	 TParm mroParm = new TParm();
         if(type.equals("CZ"))
         mroParm.setData("PIPELINE", "DataDown_zjks");
         else if(type.equals("CJ"))
         mroParm.setData("PIPELINE", "DataDown_cjks");	 
         mroParm.setData("PLOT_TYPE", "G1");
         mroParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//��ҽ˳���
         mroParm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));//ҽԺ����
         mroParm.addData("PARM_COUNT", 2);//�������
         result = InsManager.getInstance().safe(mroParm);
         System.out.println("result������ҳ����======" + result);
    	 return result;
    }
	/**
	 * ������Ϣ��񵥻��¼�
	 */
	public void onTableClick() {
		onSplitOld(false);
		onSplitNew(false);
		int row = tableInfo.getSelectedRow();
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", "")
				.substring(0, 6)); // �ں�
		TParm result = INSIbsTool.getInstance().queryIbsSum(parm); // ��ѯ���ݸ����渳ֵ
		nhiCode = result.getValue("NHI_CODE", 0);
		setSumValue(result, parm);
		//��ʾ�ϴ������б�
		setUpdateValue(parm);
		this.setValueForParm(pageHead, parm);
		this.setValue("INS_CROWD_TYPE", parm.getValue("INS_CROWD_TYPE")); //��Ⱥ���
		if (this.getValue("PAT_TYPE").equals("02")){//��Ժ����
		if(parm.getValue("SDISEASE_CODE").length()>0){
			//�����ϴ���ʽ��ʾ
			callFunction("UI|SINGLE_UPLOAD_TYPE|setEnabled", true);
			//���ָ����˳�ԭ����ʾ
			callFunction("UI|QUIT_REMARK|setEnabled",false);
		}else{
			//�����ϴ���ʽ��ʾ
			callFunction("UI|SINGLE_UPLOAD_TYPE|setEnabled", false);
			//���ָ����˳�ԭ����ʾ
			callFunction("UI|QUIT_REMARK|setEnabled",true);
		}			
	}else{
		//�����ϴ���ʽ��ʾ
		callFunction("UI|SINGLE_UPLOAD_TYPE|setEnabled", false);
		//���ָ����˳�ԭ����ʾ
		callFunction("UI|QUIT_REMARK|setEnabled",false);
	}
	}
	/**
	 * ��ʾ�ϴ������б�
	 */
	private void setUpdateValue(TParm parm) {
		tabledate.removeRowAll();
		String sql = " SELECT CHARGE_DATE FROM INS_IBS_UPLOAD " +
 		             " WHERE ADM_SEQ = '"+ parm.getValue("ADM_SEQ") + "'" +
 		             " AND UP_FLG = '2'" +
 				     " GROUP BY CHARGE_DATE";
//		 System.out.println("sql===============" + sql);
		 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//	     System.out.println("result===��ʾ�ϴ�����" + result);
	       if (result.getErrCode() < 0) {
		        return;
	          }	      		
	    tabledate.setParmValue(result);
		
	}
	/**
	 * �������ݸ�ֵ
	 * 
	 * @param result
	 *            TParm
	 * @param parm
	 *            TParm
	 */
	private void setSumValue(TParm result, TParm parm) {
		this.setValueForParm(pageTwo + ";" + pageThree + ";" + showValue,
				result.getRow(0));
		this.setText("CHEMICAL_DESC", result.getValue("CHEMICAL_DESC", 0)); // ����֤��
		if(this.getValue("PAT_TYPE").equals("02")){
			String sql = " SELECT IN_DATE,DS_DATE"+
					     " FROM ADM_INP"+
					     " WHERE CASE_NO = '"+parm.getValue("CASE_NO")+"'";
		TParm date = new TParm(TJDODBTool.getInstance().select(sql));
		this.setValue("IN_DATE", date.getTimestamp("IN_DATE",0));
		this.setValue("DS_DATE", date.getTimestamp("DS_DATE",0));
		int days = StringTool.getDateDiffer((Timestamp) this
				.getValue("DS_DATE"), (Timestamp) this.getValue("IN_DATE"));
		int rollDate = days == 0 ? 1 : days;
		this.setValue("ADM_DAYS", rollDate);
		}
		else
		this.setValue("ADM_DAYS", "");	
		this.setValue("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));
		// �����ֲ���ִ��
		if (null != type && type.equals("SINGLE")) {
			//������Ϣ
			TParm mroParm = MRORecordTool.getInstance().getInHospInfo(parm);
			//��Ժ�����Ϣ
			parm.setData("IO_TYPE","O");
			TParm outDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			//��Ժ��Ϣ
			parm.setData("IO_TYPE","M");
			TParm inDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			//�ż������
			parm.setData("IO_TYPE","I");
			TParm oeDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			
			//Ժ����Ϣ
			
			setValueForParm(mroRecordName, mroParm.getRow(0));
			setValueForParm(pageSix, result.getRow(0));
			
			//ʵ���𸶱�׼��� (�����ָ���û���𸶱�׼��)
			setValue("QFBZ_AMT_S", "0.00");
			//ͳ������Ը���׼���
			setValue("TC_OWN_AMT_S", result.getRow(0).getDouble("STARTPAY_OWN_AMT"));
			//ҽ�ƾ����Ը���׼���
			setValue("JZ_OWN_AMT_S", result.getRow(0).getDouble("PERCOPAYMENT_RATE_AMT"));
			//�����Էѽ��
			double txAmt  =  result.getRow(0).getDouble("BED_SINGLE_AMT")+result.getRow(0).getDouble("MATERIAL_SINGLE_AMT");
			
			setValue("TX_OWN_AMT_S", txAmt);
			//����޶����Ͻ��
			setValue("ZGXE_AMT_S", result.getRow(0).getDouble("INS_HIGHLIMIT_AMT"));
			//�ϼ�
			double totAmt = txAmt + result.getRow(0).getDouble("INS_HIGHLIMIT_AMT")
                            + result.getRow(0).getDouble("STARTPAY_OWN_AMT")
                            + result.getRow(0).getDouble("PERCOPAYMENT_RATE_AMT");
			
			setValue("TOTAL_AMT_S", totAmt);
			
			//�״β��̼�¼
			setValue("FP_NOTE", result.getRow(0).getValue("FP_NOTE")); 
			//��ԺС��
            setValue("DS_SUMMARY", result.getRow(0).getValue("DS_SUMMARY")); 
            //סԺҽʦ
            setValue("VS_DR_CODE1", mroParm.getRow(0).getValue("VS_DR_CODE")); 
            //��Ժ���
            for (int i = 0; i < outDiagParm.getCount(); i++) 
            { 	
              //��Ժ���
              String icdCode = "" + outDiagParm.getData("ICD_CODE", i);
			  String icdDesc = "" + outDiagParm.getData("ICD_DESC", i);
			  String icdStatus =  "" +  outDiagParm.getData("ICD_STATUS", i);
			  setValue("OUT_ICD_CODE"+(i+1), icdCode);
			  setValue("OUT_ICD_DESC"+(i+1), icdDesc);
			  setValue("ADDITIONAL_CODE"+(i+1), icdStatus);
			}
            //�ż������
            String oeDiag = "";
            for(int i = 0; i<oeDiagParm.getCount();i++)
            {
            	oeDiag += (oeDiagParm.getData("ICD_CODE", i)+"_"+oeDiagParm.getData("ICD_DESC", i));	
            }
            setValue("OE_DIAG_CODE", oeDiag);
            // ��Ժ���
    		String inDiag = "";
    		for (int i = 0; i < inDiagParm.getCount(); i++) 
    		{
           	  inDiag += (inDiagParm.getData("ICD_CODE", i)+"_"+inDiagParm.getData("ICD_DESC", i));
    		}           
    		setValue("IN_DIAG_CODE", inDiag);
		}
		getTotAmtValue(result);
	}

	/**
	 * ������ҳǩ���ܽ�����ݸ�ֵ
	 * 
	 * @param result
	 *            TParm
	 */
	private void getTotAmtValue(TParm result) {
		// ���úϼ�
		for (int i = 0; i < nameAmt.length; i++) {
			double sum = 0.00;
			for (int j = 0; j < nameType.length; j++) {
				sum += result.getRow(0).getDouble(nameType[j] + nameAmt[i]);
				this.setValue("TOT" + nameAmt[i], sum);
			}
		}
		double sum = 0.00;
		// ҽ�����ϼ�
		for (int i = 0; i < insAmt.length; i++) {
			sum += this.getValueDouble(insAmt[i]);
		}
		this.setValue("SUM_TOT_AMT", sum); // �ܼ�
	}
	/**
	 * ���
	 */
	public void onClear() {
		// isEnable(pageThree, true);
		// ͷ��
		clearValue(pageHead + ";INS_CROWD_TYPE");
		// ҳǩ
		clearValue(pageTwo + ";" + pageThree
				+ ";CHEMICAL_DESC;FP_NOTE;DS_SUMMARY;" + showValue
				+ ";" + mroRecordName + ";" + pageSix);
		// �Ƴ�����
		tableInfo.removeRowAll();
		oldTable.acceptText();
		oldTable.setDSValue();
		oldTable.removeRowAll();
		newTable.acceptText();
		newTable.setDSValue();
		newTable.removeRowAll();
		tabbedPane.setSelectedIndex(0); // ��һ��ҳǩ
		tabledate.removeRowAll();
		clearValue("SUM_AMT;NEW_SUM_AMT");
        //�������͸�ֵ
        this.setValue("PAT_TYPE","01");
        callFunction("UI|changeInfo|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|detailupload|setEnabled", false);
        //�ϴ�����ʱ�丳ֵ
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("UPLOAD_DATE", yesterday);
	}

	/**
	 * ִ�б༭״̬
	 * 
	 * @param name
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void isEnable(String name, boolean flg) {
		String[] pageName = name.split(";");
		for (int i = 0; i < pageName.length; i++) {
			callFunction("UI|" + pageName[i] + "|setEnabled", flg);
		}
	}

	/**
	 * �ڶ���ҳǩ�������
	 */
	public void onSaveIbs() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		String[] ibsName = showValue.split(";");
		for (int i = 0; i < ibsName.length; i++) {
			parm.setData(ibsName[i], this.getValue(ibsName[i]));
		}
		// ============pangben ȥ���س���
		String chemical = this.getText("CHEMICAL_DESC");
		parm.setData("CHEMICAL_DESC", chemical.replace("\n", "")); // ����˵��
		parm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("DS_DATE"), true));
		// System.out.println("parmparmparm:::"+parm);
		TParm result = INSIbsTool.getInstance().updateIbsOther(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
	}

	/**
	 * ������ ������¼��ѯ����
	 */
	public void onOp() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		TParm result = (TParm) this.openDialog(
				"%ROOT%\\config\\ins\\INSOperator.x", parm);
	}

	/**
	 * �����ַ��÷ָ� ������ҳ �б������
	 */
	public void onMroSave() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		String[] name = pageSix.split(";");
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i], this.getValueInt(name[i]));
		}
		parm.setData("FP_NOTE", this.getText("FP_NOTE"));
		parm.setData("DS_SUMMARY", this.getText("DS_SUMMARY"));
		TParm restult = INSIbsTool.getInstance().updateInsIbsMro(parm);
		if (restult.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
	}

	/**
	 * ������� Ϊ��ʱ ��������Ĳ���ʾ
	 */
	public void onDiagLost() {
		if (this.getValueString("DIAG_CODE").trim().length() <= 0) {
			this.setValue("DIAG_DESC", "");
		}
	}

	/**
	 * ����¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (parm == null) {
			this.setValue("DIAG_CODE", "");
			this.setValue("DIAG_DESC", "");
		} else {
			this.setValue("DIAG_CODE", parm.getValue("ICD_CODE"));
			this.setValue("DIAG_DESC", parm.getValue("ICD_CHN_DESC"));
		}
	}

	boolean sortClicked = false;

	/**
	 * �����������������
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = newTable.getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = newTable.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectoryת��param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		newTable.setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param parm
	 *            TParm
	 * @param group
	 *            String
	 * @param names
	 *            String
	 * @param size
	 *            int
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * ת��parm�е���
	 * 
	 * @param columnName
	 *            String[]
	 * @param tblColumnName
	 *            String
	 * @return int
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * ��ò��ָ����˳�ԭ��
	 */
	public void getQuitRemark() {		
		String sql =" SELECT ID,CHN_DESC FROM SYS_DICTIONARY"+
                    " WHERE GROUP_ID  = 'INS_QUIT_REMARK'";       
        TParm Parm = new TParm(TJDODBTool.getInstance().select(sql));
        String data = "";
        for (int i = 0; i < Parm.getCount(); i++) 
        {
        	data+="["+Parm.getData("ID", i)+","+
        	          Parm.getData("CHN_DESC", i)+"]"+",";
		}
        TComboBox Combo = (TComboBox) this.getComponent("QUIT_REMARK");       
        Combo.setStringData("[[id,text],[,],"+data.substring(0, data.length() - 1)+"]");

	}
	/**
	 * ��ò����ϴ���ʽ
	 */
	public void getSingleUploadType() {		
		String sql =" SELECT ID,CHN_DESC FROM SYS_DICTIONARY"+
                    " WHERE GROUP_ID  = 'INS_UPLOAD_TYPE'";       
        TParm Parm = new TParm(TJDODBTool.getInstance().select(sql));
        String data = "";
        for (int i = 0; i < Parm.getCount(); i++) 
        {
        	data+="["+Parm.getData("ID", i)+","+
        	          Parm.getData("CHN_DESC", i)+"]"+",";
		}
        TComboBox Combo = (TComboBox) this.getComponent("SINGLE_UPLOAD_TYPE");       
        Combo.setStringData("[[id,text],[,],"+data.substring(0, data.length() - 1)+"]");

	}	
	
}
