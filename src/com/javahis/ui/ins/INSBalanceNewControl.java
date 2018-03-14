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
 * Title:住院费用明细实时上传
 * Description:住院费用分割 、明细上传、出院结算
 * @author yufh
 * @version 2.0
 */
public class INSBalanceNewControl extends TControl {
	// 排序
	private Compare compare = new Compare();
	// 排序
	private boolean ascending = false;
	// 排序
	private int sortColumn = -1;
	// 医保身份
	String nhiCode = "";
	private TTable tableInfo; // 病患基本信息列表
	private TTable tabledate; //上传日期列表
	private TTable oldTable; // 费用分割前数据
	private TTable newTable; // 费用分割后数据
	private TTabbedPane tabbedPane; // 页签
	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	DateFormat df1 = new SimpleDateFormat("yyyy");
	private TParm regionParm; // 医保区域代码
	int index = 0; // 费用分割 累计需要添加数据个数
	int selectNewRow; // 费用分割后明细数据获得当前选中行
	String type; // TYPE: SINGLE单病种界面显示
	// 可以修改的数据
	private String showValue = "IDNO;IN_DATE;STATION_CODE;BED_NO;UPLOAD_FLG;"
			+ "DRG_FLG;DIAG_CODE;DIAG_DESC2;DIAG_DESC;SOURCE_CODE;" 
			+ "HOMEDIAG_DESC;QUIT_REMARK;SINGLE_UPLOAD_TYPE"; 
	// 第二个页签第三个页签
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

	// 第三个页签
	private String pageThree = "PHA_AMT;PHA_OWN_AMT;PHA_ADD_AMT;"
			+ "PHA_NHI_AMT;EXM_AMT;EXM_OWN_AMT;EXM_ADD_AMT;EXM_NHI_AMT;TREAT_AMT;TREAT_OWN_AMT;TREAT_ADD_AMT;"
			+ "TREAT_NHI_AMT;OP_AMT;OP_OWN_AMT;OP_ADD_AMT;OP_NHI_AMT;BED_AMT;BED_OWN_AMT;BED_ADD_AMT;BED_NHI_AMT;"
			+ "MATERIAL_AMT;MATERIAL_OWN_AMT;MATERIAL_ADD_AMT;MATERIAL_NHI_AMT;OTHER_AMT;OTHER_OWN_AMT;"
			+ "OTHER_ADD_AMT;OTHER_NHI_AMT;BLOODALL_AMT;BLOODALL_OWN_AMT;BLOODALL_ADD_AMT;BLOODALL_NHI_AMT;"
			+ "BLOOD_AMT;BLOOD_OWN_AMT;BLOOD_ADD_AMT;BLOOD_NHI_AMT;OWN_RATE;DECREASE_RATE;REALOWN_RATE;"
			+ "INSOWN_RATE;RESTART_STANDARD_AMT;STARTPAY_OWN_AMT;OWN_AMT;PERCOPAYMENT_RATE_AMT;ADD_AMT;"
			+ "INS_HIGHLIMIT_AMT;APPLY_AMT;TRANBLOOD_OWN_AMT;HOSP_APPLY_AMT;"
			+ "TOT_ADD_AMT;TOT_NHI_AMT;SUM_TOT_AMT;TOT_AMT;TOT_OWN_AMT;"
			//单病种
			+ "QFBZ_AMT_S;TC_OWN_AMT_S;JZ_OWN_AMT_S;TX_OWN_AMT_S;ZGXE_AMT_S;TOTAL_AMT_S";
	private String singleName = "SPECIAL_PAT_CODE;COMPANY_TYPE;PAT_CLASS;PROGRESS;LBL_SPECIAL_PAT_CODE;LBL_COMPANY_TYPE;LBL_PAT_CLASS;LBL_PROGRESS"; // 单病种操作不显示的控件
	// 第六个页签 病历首页数据
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
	// 第六个页签中保存按钮数据
	private String pageSix = "L_TIMES;M_TIMES;S_TIMES";
	// 头部
	private String pageHead = "CONFIRM_NO;CASE_NO;MR_NO;PAT_NAME";
	// 结算金额汇总显示
	private String[] nameAmt = { "_AMT", "_OWN_AMT", "_ADD_AMT", "_NHI_AMT" };
	private String[] nameType = { "PHA", "EXM", "TREAT", "OP", "BED",
			"MATERIAL", "OTHER", "BLOODALL", "BLOOD" }; // 收费金额类型
	// 医保收费金额
	private String[] insAmt = { "RESTART_STANDARD_AMT",
			"PERCOPAYMENT_RATE_AMT", "STARTPAY_OWN_AMT", "OWN_AMT",
			"TRANBLOOD_OWN_AMT", "ADD_AMT", "INS_HIGHLIMIT_AMT" };
	// 费用分割前表格数据
	private String[] pageFour = { "ORDER_CODE", "ORDER_DESC", "DOSE_DESC",
			"STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORD_CLASS_CODE", "NHI_CODE_I", "OWN_PRICE", "BILL_DATE" };
	// 费用分割后表格数据
	private String[] pageFive = { "SEQ_NO", "ORDER_CODE", "ORDER_DESC",
			"DOSE_CODE", "STANDARD", "PHAADD_FLG", "CARRY_FLG", "PRICE",
			"NHI_ORDER_CODE", "NHI_ORD_CLASS_CODE", "NHI_FEE_DESC",
			"OWN_PRICE", "CHARGE_DATE" };
	private TParm newParm; // 费用分割后表格数据发生金额重新计算使用
	// 累计増付一次性材料
	double addFee = 0.00;
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initParm();
		// 排序监听
		addListener(newTable);
	}

	/**
	 * 初始化数据
	 */
	private void initParm() {
		type = (String) getParameter(); // TYPE: SINGLE 单病种
		tableInfo = (TTable) this.getComponent("TABLEINFO"); // 病患基本信息列表
		tabledate = (TTable) this.getComponent("TABLEDATE"); // 上传日期列表
		oldTable = (TTable) this.getComponent("OLD_TABLE"); // 费用分割前数据
		newTable = (TTable) this.getComponent("NEW_TABLE"); // 费用分割后数据
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // 页签
		this.setValue("START_DATE", SystemTool.getInstance().getDate()); // 入院开始时间
		this.setValue("END_DATE", SystemTool.getInstance().getDate()); // 入院结束时间
		newTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onExaCreateEditComponent");
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
		isEnable(pageTwo + ";" + pageThree + ";" + mroRecordName, false);
		//出院日期
		callFunction("UI|DS_DATE|setEnabled",true);
		// 只有text有这个方法，调用ICD10弹出框
		callFunction("UI|DIAG_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");

		// textfield接受回传值
		callFunction("UI|DIAG_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// 单病种操作界面
		if (null != type && type.equals("SINGLE")) {
			String[] singles = singleName.split(";");
			this.setTitle("单病种费用分割");
			for (int i = 0; i < singles.length; i++) {
				callFunction("UI|" + singles[i] + "|setVisible", false);
			}
			callFunction("UI|tPanel_6|setVisible", false);
			callFunction("UI|tPanel_6|setEnabled", false);
			callFunction("UI|tPanel_13|setVisible", true);
			callFunction("UI|tPanel_13|setEnabled", true);
		} else {
			// 病历首页页签不显示操作按钮
			callFunction("UI|OP_BTN|setVisible", false);
			callFunction("UI|MRO_BTN|setVisible", false);
		}
		 //总量 列触发
        this.addEventListener("NEW_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                              "onTableChangeValue");
        //病患类型赋值
        this.setValue("PAT_TYPE","01");
        callFunction("UI|changeInfo|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|detailupload|setEnabled", false);		
        //上传费用时间赋值
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("UPLOAD_DATE", yesterday);
        //获得病种付费退出原因
        getQuitRemark();
        //获得病种上传方式
        getSingleUploadType();
	}

	/**
	 * 病案号文本框回车事件
	 */
	public void onMrNo() {
		if (null == this.getValue("PAT_TYPE")
				|| this.getValue("PAT_TYPE").toString().length() <= 0) {
			onCheck("PAT_TYPE", "病患类型不可以为空");
			return;
		}
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
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
			this.messageBox("此病患没有住院信息");
			this.setValue("MR_NO", "");
			this.setValue("PAT_NAME", "");
			this.setValue("CASE_NO", "");
			return;
		}
		if(this.getValue("PAT_TYPE").equals("01"))//在院病人
		parm.setData("FLG","N");
		else if (this.getValue("PAT_TYPE").equals("02"))//出院病人
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
     * 病患类型选择事件
     */
    public void patType(){   	
    	if(this.getValue("PAT_TYPE").equals("01")){//在院病人
    		callFunction("UI|changeInfo|setEnabled", false);
    		callFunction("UI|onSave|setEnabled", false);
    	}
    	else if(this.getValue("PAT_TYPE").equals("02")){//出院病人
    		callFunction("UI|changeInfo|setEnabled", true);
    		callFunction("UI|onSave|setEnabled", true);   		
    	}
    }
    /**
     * 日期选择事件
     */
    public void updateselect(){ 
    	callFunction("UI|upload|setEnabled", false);
    	callFunction("UI|detailupload|setEnabled",false);
    }
	/**
     * 总量列触发
     * @param obj Object
     */
    public void onTableChangeValue(Object obj) { // 数量合计数据
    	newTable.acceptText();
         TTableNode node = (TTableNode) obj;
         if (node == null) {
             return;
         }
         int row = node.getRow();        
         int column = node.getColumn();
 		// 计算当前总金额
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
     * 计算总金额
     */
    public TParm getTotalAmt(double total, double ownPrice) {
        TParm parm = new TParm();
        double fees =  Math.abs(StringTool.round(total * ownPrice,2));
//    	System.out.println("fees=====:"+fees);
        parm.setData("FEES", fees);
        return parm;
    }

	/**
	 * 查询
	 */
	public void onQuery() {
			if (null == this.getValue("PAT_TYPE")
					|| this.getValue("PAT_TYPE").toString().length() <= 0) {
				onCheck("PAT_TYPE", "病患类型不可以为空");
				return;
			}
		if (null == this.getValue("START_DATE")
				|| this.getValue("START_DATE").toString().length() <= 0) {
			onCheck("START_DATE", "入院开始时间不可以为空");
			return;
		}
		if (null == this.getValue("END_DATE")
				|| this.getValue("END_DATE").toString().length() <= 0) {
			onCheck("END_DATE", "入院结束时间不可以为空");
			return;
		}

		if (((Timestamp) this.getValue("START_DATE")).after(((Timestamp) this
				.getValue("END_DATE")))) {
			this.messageBox("开始时间不可以大于结束时间");
			return;
		}
	
		if (null == this.getValue("UPLOAD_DATE")
				|| this.getValue("UPLOAD_DATE").toString().length() <= 0) {
			onCheck("UPLOAD_DATE", "上传时间不可以为空");
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
		if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 城职
			parm.setData("INS_CROWD_TYPE", "1");
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) { // 城居
			parm.setData("INS_CROWD_TYPE", "2");
		}else if (this.getValue("INS_CROWD_TYPE").equals("")){// 城职、城居
			parm.setData("INS_CROWD_TYPE", "");
		}
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域代码
		parm.setData("START_DATE", df.format(this.getValue("START_DATE"))); // 入院时间
		parm.setData("END_DATE", df.format(this.getValue("END_DATE"))); // 入院结束时间	
		TParm result = INS_Adm_Seq(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			tableInfo.removeRowAll();
			return;
		}
		tableInfo.setParmValue(result);
	}
	/**
	 * 住院费用分割查询病患基本信息
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
			if(this.getValue("PAT_TYPE").equals("01"))//在院病人
				Sql3 = " AND A.DS_DATE IS NULL"; 
				else if (this.getValue("PAT_TYPE").equals("02"))//出院病人
				Sql3 = " AND A.DS_DATE IS NOT NULL";
			if (null!=parm.getValue("INS_CROWD_TYPE") && 
					parm.getValue("INS_CROWD_TYPE").toString().length()>0){
				Sql4 =" AND B.INS_CROWD_TYPE ='"+ parm.getValue("INS_CROWD_TYPE")+"'";
			}
		String date = df.format(SystemTool.getInstance().getDate())
				+ "/01/01";// 医保跨年
		String SQL = " SELECT CASE SUBSTR(C.CONFIRM_NO,1,2) WHEN 'KN' THEN '"
				+ date
				+ "' ELSE TO_CHAR(A.IN_DATE,'YYYY/MM/DD') END AS IN_DATE, "
				+ // 医保跨年
				" A.CASE_NO,C.CONFIRM_NO,C.PAT_NAME,C.SEX_CODE,A.CTZ1_CODE,C.IDNO,A.IPD_NO, "
				+ " CASE IN_STATUS WHEN '0' THEN '资格确认书录入' WHEN '1' THEN '费用已结算' WHEN '2' THEN '费用已上传' "
				+ " WHEN '3' THEN '下载已审核' WHEN '4' THEN '下载已支付' WHEN '5' THEN '撤销确认书' " 
				+ " WHEN '6' THEN '开具资格确认书失败' "
				+ " WHEN '7' THEN '资格确认书已审核' ELSE '' END  AS IN_STATUS,"
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
				+ " AND B.NHI_CTZ_FLG = 'Y'"// 身份加人群分类
				+ // 配合医保跨年结算添加CASE_NO
				" AND (C.IN_STATUS IN ('0','1','7') OR C.IN_STATUS IS NULL) ";
//		System.out.println("SQL:::::INS_Adm_Seq"+SQL);
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL));
		return result;
	}

	/**
	 * 校验为空方法
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
	 * 转日明细
	 */
	public void onApply() {
		if (null == this.getValue("UPLOAD_DATE")
				|| this.getValue("UPLOAD_DATE").toString().length() <= 0) {
			onCheck("UPLOAD_DATE", "上传时间不可以为空");
			return;
		}
		onExe("H");
	}

	/**
	 * 执行转日明细和病患基本资料操作
	 * 
	 * @param type
	 *            （M :转病患信息操作 ,H :转日明细操作）
	 */
	private void onExe(String type) {
		TParm parm = getTableSeleted();
		//System.out.println("parm:"+parm);
		if (null == parm) {
			return;
		}
		if(type.equals("H")){
		  //判断是否上传累计增负
        String sqldate =" SELECT MAX(BILL_DATE) AS BILL_DATE" +
   		     " FROM IBS_ORDD" +
   		     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'";
        TParm dateparm = new TParm(TJDODBTool.getInstance().select(sqldate));            
        String billdate = df.format(dateparm.getTimestamp("BILL_DATE",0));
        String update = df.format(this.getValue("UPLOAD_DATE"));
        if (this.getValue("PAT_TYPE").equals("02")&&
        Double.parseDouble(billdate)==Double.parseDouble(update)){
        	this.messageBox("分割之前请先执行转病患基本资料");     	
        }
	}
		parm.setData("TYPE", type); // M :转病患信息操作 ,H :转申报操作
		parm.setData("REGION_CODE", Operator.getRegion()); // 医院代码
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String startDate = parm.getValue("START_DATE");
		String endDate = df.format(SystemTool.getInstance().getDate());				
		String uploadDate =  StringTool.getString(TCM_Transform.getTimestamp(getValue(
			     "UPLOAD_DATE")), "yyyyMMdd"); 
		parm.setData("START_DATE", startDate); // 开始时间
		parm.setData("END_DATE", endDate); // 结束时间
		parm.setData("UPLOAD_DATE", uploadDate); // 上传时间
//		System.out.println("onExe=======1"+parm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onExeNew", parm);
//		System.out.println("onExe=======2"+result);
		if (result.getErrCode() < 0) {
			this.messageBox("执行失败:"+result.getErrText());
			return;
		}
		if (result.getInt("MES")==2) {
			this.messageBox("明细已上传，不能执行转日明细");
			return;
		}
		String Msg = "转档完毕\n" + "成功笔数:" + result.getValue("SUCCESS_INDEX")
				+ "\n" + "失败笔数:" + result.getValue("ERROR_INDEX");
		this.messageBox(Msg);
		if ("M".equals(type)) {
			this.setValueForParm(pageHead + ";" + pageTwo + ";" + showValue+";REALOWN_RATE",
					result.getRow(0));//pangben 2013-4-1添加实际支付比例,城居病人结算操作失败，支付比例不正确，赋值错误
			int days = StringTool.getDateDiffer((Timestamp) this
					.getValue("DS_DATE"), (Timestamp) this.getValue("IN_DATE"));
			int rollDate = days == 0 ? 1 : days;
			this.setValue("ADM_DAYS", rollDate);
			this.setValue("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));
			tabbedPane.setSelectedIndex(1);
		}
	}

	/**
	 * 获得次诊断
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
	 * 费用分割执行操作
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
			updateRun(); // 准备上传医保
			feePartitionEnable(true);
		}

	}
	/**
	 * 费用分割过程中按钮置灰
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
	 * 费用分割执行以后数据比较
	 * 
	 * @return boolean
	 */
	public boolean CheckTotAmt() {
			TParm parm = getTableSeleted();
			String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//开始时间
			String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//结束时间		
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
			messageBox("金额为0,无需费用分割");
			return false; 	
		}				
		// 判断是否跨年操作处理 获得结束时间
//		Timestamp sysTime = SystemTool.getInstance().getDate();			
//		DateFormat df1 = new SimpleDateFormat("yyyy");
//		DateFormat df = new SimpleDateFormat("yyyyMMdd");
//		String tempDate = df1.format(sysTime);//当前年份	
//		String startDate = parm.getValue("START_DATE");//开始时间
//		String endDate = "";//结束时间
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
			messageBox("费用分割数据有问题");
			return false; 
		} else {
			return true; 
		}
	}
		return true;
	}

	/**
	 * 累计增付
	 */
	private void update1() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// System.out.println("累计増付入参"+parm);
		String insAdmSql = " SELECT ADM_SEQ FROM INS_ADM_CONFIRM WHERE CONFIRM_NO = '"
				+ parm.getValue("CONFIRM_NO") + "' ";
		TParm insAdmParm = new TParm(TJDODBTool.getInstance().select(insAdmSql));
		// 医保就诊顺序号
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
		// 城职 操作 查询的数据应该是累计增负为Y的数据
		TParm result = new TParm(TJDODBTool.getInstance().select(upLoadSql));
		if (result.getErrCode() < 0) {
			return;
		}
		addFee = result.getDouble("TOTAL_AMT", 0);
		TParm splitParm = new TParm();
		TParm splitCParm = new TParm();
		splitParm.setData("ADDPAY_ADD", result.getDouble("TOTAL_AMT", 0));
		//开始时间
		String startDate = parm.getValue("START_DATE");
		//System.out.println("startDate:"+startDate.length());
		if(startDate.length() > 8)
			startDate =startDate.substring(0,8); 
		splitParm.setData("HOSP_START_DATE", startDate);
		if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 1.城职 2.城居
			// System.out.println("城职増付入参"+splitCParm);
			// 城职累计增付
			splitCParm = INSTJTool.getInstance().DataDown_sp1_C(splitParm);
			 System.out.println("城职増付出参"+splitCParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			// System.out.println("城居増付入参"+splitCParm);
			// 城居 住院累计增负计算
			splitCParm = INSTJTool.getInstance().DataDown_sp1_H(splitParm);
			// System.out.println("城居増付出参"+splitCParm);
		}
		if (!INSTJTool.getInstance().getErrParm(splitCParm)) {
			this.messageBox(splitCParm.getErrText());
			return;
		}
		TParm exeParm = new TParm();
		exeParm.setData("NHI_AMT", splitCParm.getDouble("NHI_AMT")); // 申报金额
		exeParm.setData("TOTAL_AMT", result.getDouble("TOTAL_AMT", 0)); // 发生金额
		exeParm.setData("TOTAL_NHI_AMT", splitCParm.getDouble("NHI_AMT")); // 医保金额
		exeParm.setData("ADD_AMT", splitCParm.getDouble("ADDPAY_AMT")); // 累计增负金额
		exeParm.setData("ADDPAY_AMT", splitCParm.getDouble("ADDPAY_AMT")); // 累计增负金额
		exeParm.setData("OWN_AMT", splitCParm.getDouble("OWN_AMT")); // 自费金额
		exeParm.setData("CASE_NO", parm.getValue("CASE_NO")); // 就诊序号
		exeParm.setData("REGION_CODE", Operator.getRegion()); // 区域
		// 查询最大SEQ_NO
		TParm maxSeqParm = INSIbsUpLoadTool.getInstance().queryMaxIbsUpLoad(
				parm);
//		 System.out.println("maxSeqParm====="+maxSeqParm);
		if (maxSeqParm.getErrCode() < 0) {
			return;
		}
		exeParm.setData("SEQ_NO", maxSeqParm.getInt("SEQ_NO", 0) + 1); // 顺序号
		exeParm.setData("DOSE_CODE", ""); // 剂型
		exeParm.setData("STANDARD", ""); // 规格
		exeParm.setData("PRICE", 0); // 单价
		exeParm.setData("QTY", 0); // 数量
		exeParm.setData("ADM_SEQ", maxSeqParm.getValue("ADM_SEQ", 0)); // 医保就诊号
		exeParm.setData("OPT_USER", Operator.getID()); // ID
		exeParm.setData("OPT_TERM", Operator.getIP());
		exeParm.setData("HYGIENE_TRADE_CODE", ""); // 批准文号
		exeParm.setData("ORDER_CODE", "***018"); // 医嘱代码
		exeParm.setData("NHI_ORDER_CODE", "***018"); // 医保医嘱代码
		exeParm.setData("ORDER_DESC", "一次性材料累计增付");
		exeParm.setData("ADDPAY_FLG", "Y"); // 累计增付标志（Y：累计增付；N：不累计增付）
		exeParm.setData("PHAADD_FLG", "N"); // 增负药品
		exeParm.setData("CARRY_FLG", "N"); // 出院带药
		exeParm.setData("OPT_TERM", Operator.getIP()); //
		exeParm.setData("NHI_ORD_CLASS_CODE", "06"); // 统计代码
		exeParm.setData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				result.getValue("CHARGE_DATE", 0), true)); // 明细录入时间
		exeParm.setData("YEAR_MON", parm.getValue("YEAR_MON")); // 期号
		result = TIOM_AppServer.executeAction("action.ins.INSBalanceAction",
				"onAdd", exeParm);
//		 System.out.println("onAdd====="+result);
		if (result.getErrCode() < 0) {
			this.messageBox("执行累计增负失败");
			return;
		}
	}

	/**
	 * 准备上传医保 城居操作需要判断是否 取得医令是否是儿童用药或儿童处置项目
	 * 
	 * 单病种操作 INS_IBS修改床位费特需金额和医用材料费特需金额
	 */
	private void updateRun() {
		TParm commParm = getTableSeleted();
		if (null == commParm) {

			return;
		}
		TParm parmValue = newTable.getParmValue(); // 获得费用分割后表格数据
		double bedFee = regionParm.getDouble("TOP_BEDFEE", 0);
		boolean flg = false; // 输出消息框管控 判断是否分割成功
		TParm tableParm = null;
		TParm newParm = new TParm(); // 累计数据
		// TParm ctzParm = null;
		TParm tempParm = new TParm();
		if (null == nhiCode || nhiCode.length() <= 0) {
			String sql = " SELECT CTZ1_CODE FROM INS_ADM_CONFIRM WHERE CASE_NO='"
					+ commParm.getValue("CASE_NO") + "'" +
						 " AND CANCEL_FLG = 'N'";
			tempParm = new TParm(TJDODBTool.getInstance().select(sql));
			if (tempParm.getErrCode() < 0) {
				this.messageBox("获得病患医保身份失败");
				return;
			}
			if (tempParm.getCount("CTZ1_CODE") <= 0) {
				this.messageBox("没有找到病患医保身份");
				return;
			}
			nhiCode = tempParm.getValue("CTZ1_CODE", 0);
		}
		for (int i = 0; i < parmValue.getCount(); i++) {
			tableParm = parmValue.getRow(i);
			String nhiOrderCode = tableParm.getValue("NHI_ORDER_CODE");
			// 累计增负操作时，数据库会添加一条医嘱为***018的数据
			if ("***018".equals(nhiOrderCode) || nhiOrderCode.equals("")) { // 医保号码
				continue;
			}
			if (nhiOrderCode.length() > 4) {
				String billdate = tableParm.getValue("CHARGE_DATE").replace(
						"/", ""); // 明细帐日期时间
				TParm parm = new TParm();
				
				parm.setData("CTZ1_CODE", nhiCode); // 身份
				parm.setData("QTY", tableParm.getValue("QTY")); // 个数
				parm.setData("TOTAL_AMT", tableParm.getValue("TOTAL_AMT")); // 总金额
				parm.setData("TIPTOP_BED_AMT", bedFee); // 最高床位费
				parm.setData("PHAADD_FLG", null != tableParm
						.getValue("PHAADD_FLG")
						&& tableParm.getValue("PHAADD_FLG").equals("Y") ? "1"
						: "0"); // 药品增负注记
				parm.setData("FULL_OWN_FLG", null != tableParm
						.getValue("FULL_OWN_FLG")
						&& tableParm.getValue("FULL_OWN_FLG").equals("Y") ? "0"
						: "1"); // 全自费标志
				parm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0)); // 医保区域代码
				parm.setData("NHI_ORDER_CODE", nhiOrderCode);//医保码
				parm.setData("CHARGE_DATE", billdate); // 费用发生时间
				TParm splitParm = new TParm();		
				//pangben 2012-9-6
				if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 1.城职 2.城居
					// System.out.println("城职医保分割前数据入参"+parm);
					// 住院费用明细分割
					splitParm = INSTJTool.getInstance().DataDown_sp1_B(parm);

				} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
					// 住院费用明细分割
					splitParm = INSTJTool.getInstance().DataDown_sp1_G(parm);
				}
				if (!INSTJTool.getInstance().getErrParm(splitParm)) {
					flg = true;
					this.messageBox(parmValue.getValue("SEQ_NO", i) + "行失败");
					break;
				}
				// 累计数据操作
				setIbsUpLoadParm(tableParm, splitParm, newParm);
			} else {
				this.messageBox("请检查" + parmValue.getValue("SEQ_NO", i)
						+ "行医保编码"); // 序号
			}

		}
		newParm.setData("OPT_USER", Operator.getID());
		newParm.setData("OPT_TERM", Operator.getIP());
		newParm.setData("TYPE", type); // 判断执行类型 ：SINGLE:单病种操作
		newParm.setData("CASE_NO", commParm.getValue("CASE_NO")); // 单病种操作使用
		newParm.setData("YEAR_MON", commParm.getValue("YEAR_MON")); // 期号单病种操作使用
		// 执行修改INS_IBS_UPLOAD表操作
		// System.out.println("执行修改INS_IBS_UPLOAD表操作入参"+newParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onSaveInsUpLoad", newParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		//更新INS_IBS_UPLOAD字段up_flg为1，已分割
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
			this.messageBox("分割失败");
		} else {
			this.messageBox("分割成功");
		}
	}
	/**
	 * 费用分割 累计数据 添加INS_IBS_UPLOAD 表操作
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
		newParm.addData("ADM_SEQ", tableParm.getValue("ADM_SEQ")); // 就诊顺序号
		newParm.addData("SEQ_NO", tableParm.getValue("SEQ_NO")); // 序号
		newParm.addData("CHARGE_DATE", SystemTool.getInstance().getDateReplace(
				tableParm.getValue("CHARGE_DATE"), true)); // 明细帐日期时间
		newParm.addData("ADDPAY_AMT", splitParm.getValue("ADDPAY_AMT")); // 增负金额
		newParm.addData("TOTAL_NHI_AMT", splitParm.getValue("NHI_AMT")); // 申报金额
		newParm.addData("OWN_AMT", splitParm.getValue("OWN_AMT")); // 全自费金额
		newParm.addData("OWN_RATE", splitParm.getValue("OWN_RATE")); // 自负比例
		newParm.addData("NHI_ORD_CLASS_CODE", splitParm
				.getValue("NHI_ORD_CLASS_CODE")); // 统计代码
		newParm.addData("ADDPAY_FLG", null != splitParm.getValue("ADDPAY_FLG")
				&& splitParm.getValue("ADDPAY_FLG").equals("1") ? "Y" : "N"); // 累计增负标志

	}
	
	/**
	 * 日明细上传操作
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
			onCheck("UPLOAD_DATE", "上传时间不可以为空");
			return;
		}
		
		TParm data = new TParm();
		parm.setData("REGION_CODE", Operator.getRegion());
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//开始时间
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//结束时间	
		//判断上传数据金额是否相符
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
	              messageBox("明细上传数据有问题");
	               return; 
               }
            //获取医师编码和日基本信息上传数据
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
            
//            //判断是否上传累计增负
            String sqldate =" SELECT MAX(BILL_DATE) AS BILL_DATE" +
    		     " FROM IBS_ORDD" +
       		     " WHERE CASE_NO = '"+ parm.getValue("CASE_NO") + "'";
            TParm dateparm = new TParm(TJDODBTool.getInstance().select(sqldate));            
            String billdate = df.format(dateparm.getTimestamp("BILL_DATE",0));
            String update = df.format(this.getValue("UPLOAD_DATE"));
            if (this.getValue("PAT_TYPE").equals("02")&&
            Double.parseDouble(billdate)==Double.parseDouble(update)){
            	//累计增付计算
        		update1();
        		if(updateAddDetail(parm,result1).getErrCode()<0)
        			return;
            }
          else{
		//上传日明细
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
			this.messageBox("执行失败");
			return;
		}
		 data = this.DataUploadDetail(result,result1);
		 if (data.getErrCode() < 0) {			 
			 this.messageBox(data.getErrText());
			 return;
	        }		 
		//上传日基本信息 	
		 else{
		     //诊断数据
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
			//更新INS_IBS_UPLOAD字段up_flg为2，已上传
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
            this.messageBox("上传明细成功");     
	}
    /**
     * 住院上传日明细
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUploadDetail(TParm parm,TParm drparm) {
        TParm result = new TParm();
        TParm confInfoParm = new TParm();
//        System.out.println("DataDown_ssks_A=====parm"+parm);
        //上传明细
        int count = parm.getCount("ADM_SEQ");
//       System.out.println("DataDown_ssks_A=====count"+count);
        for (int m = 0; m < count; m++) {
            //System.out.println("进入循环"+m+parm.getRow(m));
            confInfoParm.addData("CONFIRM_NO", parm.getData("CONFIRM_NO", m));//原始确认书号
            confInfoParm.addData("ADM_SEQ",parm.getData("ADM_SEQ", m));//就医顺序号
            confInfoParm.addData("HOSP_CLEFT_CENTER",
            		parm.getData("INSBRANCH_CODE", m));//医院所属分中心
            confInfoParm.addData("BILL_DATE", parm.getValue("CHARGE_DATE",m));//费用发生时间
            String uploaddate = StringTool.getString(SystemTool.getInstance().getDate(),
            		"yyyy-MM-dd HH:mm:ss");
            confInfoParm.addData("UPLOAD_DATE", uploaddate);//医院上传时间
            confInfoParm.addData("SEQ_NO", parm.getData("SEQ_NO", m));//序号
            confInfoParm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO",0));//医院编码
            confInfoParm.addData("NHI_ORDER_CODE",parm.getData("NHI_ORDER_CODE", m));//收费项目编码
            confInfoParm.addData("NHI_ORDER_DESC", parm.getData("XMMC", m));//收费项目名称
            confInfoParm.addData("ORDER_DESC", parm.getData("ORDER_DESC", m));//医院服务项目名称
            confInfoParm.addData("OWN_RATE",
                                 parm.getDouble("OWN_RATE", m) == 0 ? 0.00 :
                                 parm.getDouble("OWN_RATE", m) );//自负比例
            confInfoParm.addData("DOSE_CODE", parm.getData("JX", m));//剂型
            confInfoParm.addData("STANDARD", parm.getData("GG", m));//规格
            confInfoParm.addData("PRICE", parm.getData("PRICE", m));//单价
            confInfoParm.addData("QTY", parm.getData("QTY", m));//数量
            confInfoParm.addData("TOTAL_AMT", parm.getData("TOTAL_AMT", m));//发生金额
            confInfoParm.addData("TOTAL_NHI_AMT",
                                 parm.getData("TOTAL_NHI_AMT", m));//申报金额
            confInfoParm.addData("OWN_AMT", parm.getData("OWN_AMT", m));//全自费金额
            confInfoParm.addData("ADDPAY_AMT", parm.getData("ADDPAY_AMT", m));//增负金额
            confInfoParm.addData("OP_FLG", 
            		parm.getValue("OP_FLG", m).equals("Y")?"1":"0");//手术费用标志
            confInfoParm.addData("ADDPAY_FLG", 
            		parm.getValue("ADDPAY_FLG", m).equals("Y")?"1":"0");//累计增负标志
            confInfoParm.addData("NHI_ORD_CLASS_CODE",
                                 parm.getData("NHI_ORD_CLASS_CODE", m));//统计代码
            confInfoParm.addData("PHAADD_FLG", 
            		parm.getValue("PHAADD_FLG", m).equals("Y")?"1":"0");//增负药品标志
            confInfoParm.addData("CARRY_FLG", 
            		parm.getValue("CARRY_FLG", m).equals("Y")?"1":"0");//出院带药标志
            confInfoParm.addData("PZWH", "");//批准文号
            confInfoParm.addData("REMARK", "");//特殊情况说明
            if(parm.getDouble("OWN_RATE", m) ==1)
            confInfoParm.addData("NHI_FLG", "1");//医保报销标识(自费)
            else
            confInfoParm.addData("NHI_FLG", "0");//医保报销标识(医保)	
            confInfoParm.addData("CHANGE_SEQ_NO", "");//被调整序号
            if(parm.getDouble("TOTAL_AMT", m)>=0)
            confInfoParm.addData("RFEE_FLG", "0");//退费标识(收费)
            else
            confInfoParm.addData("RFEE_FLG", "1");//退费标识	(退费)
            confInfoParm.addData("PAY_TYPE", parm.getData("PAY_TYPE", m));//支付类别
            confInfoParm.addData("DR_QUALIFY_CODE", drparm.getData("DR_QUALIFY_CODE", 0));//医师编码
            confInfoParm.addData("PARM_COUNT", 31);
        }
        //城职
        if (this.getValueInt("INS_CROWD_TYPE") == 1)
        confInfoParm.setData("PIPELINE", "DataDown_ssks");
        //城居
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
     * 住院上传日基本信息
     * @param parm TParm
     * @return TParm
     */
    public TParm DataUploadPat(TParm parmdetail,TParm parmpat,TParm parmdiag) {
    	 TParm result = new TParm();
    	 TParm confInfoParm = new TParm();
    	 DecimalFormat dfamt = new DecimalFormat("##########0.00");
    	 double totalamt =0;//发生金额
		 double ownamt =0;//自费金额
		 double addpayamt =0;//增负金额
		 double totalnhiamt =0;//申报金额
		 double totalamtadd =0;///累计增负发生金额
		 int count = parmdetail.getCount("ADM_SEQ");//明细总条数
	     for (int i = 0; i < count; i++) {		 
	    	 totalamt+= parmdetail.getDouble("TOTAL_AMT", i);
	    	 ownamt+= parmdetail.getDouble("OWN_AMT", i);
	    	 addpayamt+= parmdetail.getDouble("ADDPAY_AMT", i);
	    	 totalnhiamt+= parmdetail.getDouble("TOTAL_NHI_AMT", i);
	    	 if(parmdetail.getValue("NHI_ORDER_CODE",i).equals("***018"))
	    		 totalamtadd = parmdetail.getDouble("TOTAL_AMT", i);
	     }
	     confInfoParm.addData("CONFIRM_NO", parmpat.getData("CONFIRM_NO", 0));//原始确认书号
         confInfoParm.addData("ADM_SEQ",parmpat.getData("ADM_SEQ", 0));//就医顺序号心
         confInfoParm.addData("HOSP_NHI_NO", parmpat.getData("NHIHOSP_NO", 0));//医院编码        
         String uploaddate = StringTool.getString(SystemTool.getInstance().getDate(),
         		"yyyy-MM-dd HH:mm:ss");
         confInfoParm.addData("UPLOAD_DATE", uploaddate);//医院上传时间  
         confInfoParm.addData("OWN_NO", parmpat.getData("PERSONAL_NO", 0));//个人编号
         String sql = " SELECT INS_DEPT_CODE FROM INS_DEPT"+
                      " WHERE HIS_DEPT_CODE = '"+ parmpat.getValue("DEPT_CODE",0) + "'";
         TParm dept = new TParm(TJDODBTool.getInstance().select(sql)); 
//         System.out.println("INS_DEPT_CODE======" + dept.getData("INS_DEPT_CODE", 0));
         confInfoParm.addData("DEPT_CODE", dept.getData("INS_DEPT_CODE", 0));//住院科室
         confInfoParm.addData("DR_NHI_CODE", parmpat.getData("DR_QUALIFY_CODE", 0));//医师编码
         confInfoParm.addData("OPT_USER", Operator.getID());//操作员编码 
//         System.out.println("CHARGE_DATE======" + parmdetail.getValue("CHARGE_DATE_PAT",0));
         confInfoParm.addData("BILL_DATE", parmdetail.getValue("CHARGE_DATE_PAT",0));//费用发生时间
         confInfoParm.addData("TOT_AMT", dfamt.format(totalamt-totalamtadd));//发生金额合计
         confInfoParm.addData("OWN_AMT", dfamt.format(ownamt));//自费金额合计
         confInfoParm.addData("ADD_AMT", dfamt.format(addpayamt));//增负金额合计
         confInfoParm.addData("NHI_AMT", dfamt.format(totalnhiamt));//申报金额合计
         confInfoParm.addData("SUM_COUNT", count);//明细总条数
 		//诊断编码
    	String diagecode = "";
       //诊断描述
   		String diagedesc = ""; 
   		int count1 = parmdiag.getCount("ICD_CHN_DESC");
   		 for(int m=0;m<count1;m++){
   			diagecode +=parmdiag.getData("ICD_CODE",m)+"@";
   			diagedesc +=parmdiag.getData("ICD_CHN_DESC",m)+",";
   			
   		 } 		
   		confInfoParm.addData("DIAGE_CODE", diagecode.length()>0? 
   				diagecode.substring(0, diagecode.length() - 1):"");//病情诊断        
   		confInfoParm.addData("DIAGE_DESC", diagedesc.length()>0? 
   				diagedesc.substring(0, diagedesc.length() - 1):"");//病情诊断 描述
   		confInfoParm.addData("DELAY", "");//延迟补传原因 ?
   		confInfoParm.addData("SPE_REMARK", "");//特殊情况
   		confInfoParm.addData("BED_NO", parmpat.getData("BED_NO", 0));//床位号
   		confInfoParm.addData("PARM_COUNT", 19);        
         //城职
         if (this.getValueInt("INS_CROWD_TYPE") == 1)
         confInfoParm.setData("PIPELINE", "DataDown_ssks");
         //城居
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
	 * 页签点击事件
	 */
	public void onChangeTab() {

		switch (tabbedPane.getSelectedIndex()) {
		// 3 :费用分割前页签 4：费用分割后页签
		case 3:
			onSplitOld();
			break;
		case 4:
			onSplitNew();
			break;
		}
	}
	/**
	 * 费用分割前数据
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
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，05床位费，06材料费，07其他费，08全血费，09成分血费
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
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//开始时间
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//结束时间	
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
				// this.messageBox("没有查询的数据");
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
		double qty = 0.00; // 数量
		double totalAmt = 0.00; // 发生金额
		double totalNhiAmt = 0.00; // 申报金额
		double ownAmt = 0.00; // 自费金额
		double addPayAmt = 0.00; // 增负金额
		for (int i = 0; i < result.getCount(); i++) {
			qty += result.getDouble("QTY", i);
			totalAmt += result.getDouble("TOTAL_AMT", i);
			totalNhiAmt += result.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += result.getDouble("OWN_AMT", i);
			addPayAmt += result.getDouble("ADDPAY_AMT", i);
		}

		// //添加合计
		for (int i = 0; i < pageFour.length; i++) {
			if (i == 0) {
				result.addData(pageFour[i], "合计:");
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
		this.setValue("SUM_AMT", totalAmt); // 添加总金额
	}
	/**
	 * 校验是否有获得焦点
	 * 
	 * @return TParm
	 */
	private TParm getTableSeleted() {
		int row = tableInfo.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要执行的数据");
			tabbedPane.setSelectedIndex(0);
			return null;
		}
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", "")
				.substring(0, 6)); // 期号
		parm.setData("CASE_NO", parm.getValue("CASE_NO")); // 就诊号码
		parm.setData("CONFIRM_NO", parm.getValue("CONFIRM_NO")); // 资格确认书编号
		parm.setData("START_DATE", parm.getValue("IN_DATE").replace("/", "")); // 开始时间
		parm.setData("MR_NO", parm.getValue("MR_NO"));
		parm.setData("PAT_AGE", parm.getValue("PAT_AGE")); // 年龄
		parm.setData("ADM_SEQ", parm.getValue("ADM_SEQ"));
		parm.setData("INS_CROWD_TYPE_YD", parm.getValue("INS_CROWD_TYPE_YD"));
		parm.setData("LOCAL_FLG", parm.getValue("LOCAL_FLG"));
		parm.setData("SDISEASE_CODE", parm.getValue("SDISEASE_CODE"));//单病种标记（出院结算使用）
		return parm;
	}

	/**
	 * 获得单选控件
	 * 
	 * @param name
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}
	/**
	 * 费用分割后数据
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
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，05床位费，06材料费，07其他费，08全血费，09成分血费
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
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//开始时间
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//结束时间	
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
			this.messageBox("E0005"); // 执行失败
			return;
		}
		//增负数据
		TParm upLoadParmTwo = INSIbsUpLoadTool.getInstance()
				.queryNewSplitUpLoad(parm);
		if (upLoadParmTwo.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		if (flg) {
			if (upLoadParmOne.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				// this.messageBox("没有查询的数据");
				callFunction("UI|upload|setEnabled", false); // 没有数据不可以执行分割操作
				callFunction("UI|detailupload|setEnabled", false);
				return;
			}
		} else {
			if (upLoadParmOne.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				callFunction("UI|upload|setEnabled", false); // 没有数据不可以执行分割操作
				callFunction("UI|detailupload|setEnabled", false);
				return;
			}
		}

		if (null == upLoadParmOne) {
			upLoadParmOne = new TParm();
		}
		// 合并数据
		if (upLoadParmTwo.getCount("ORDER_CODE") > 0) {
			for (int i = 0; i < upLoadParmTwo.getCount(); i++) {
				upLoadParmOne.setRowData(upLoadParmOne.getCount() + 1,
						upLoadParmTwo, i);
			}
			upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		}
		double qty = 0.00; // 个数
		double totalAmt = 0.00; // 发生金额
		double totalNhiAmt = 0.00; // 申报金额
		double ownAmt = 0.00; // 自费金额
		double addPayAmt = 0.00; // 增负金额
		for (int i = 0; i < upLoadParmOne.getCount(); i++) {
			totalNhiAmt += upLoadParmOne.getDouble("TOTAL_NHI_AMT", i);
			ownAmt += upLoadParmOne.getDouble("OWN_AMT", i);
			addPayAmt += upLoadParmOne.getDouble("ADDPAY_AMT", i);
			if (upLoadParmOne.getValue("ORDER_CODE", i).equals("***018")) { // 上传医嘱不可以累计金额
				continue;
			}
			qty += upLoadParmOne.getDouble("QTY", i);
			totalAmt += upLoadParmOne.getDouble("TOTAL_AMT", i);
		}

		// //添加合计
		for (int i = 0; i < pageFive.length; i++) {
			if (i == 1) {
				upLoadParmOne.addData(pageFive[i], "合计:");
				continue;
			}
			upLoadParmOne.addData(pageFive[i], "");
		}
		upLoadParmOne.addData("QTY", 0);
		upLoadParmOne.addData("TOTAL_AMT", totalAmt);
		upLoadParmOne.addData("TOTAL_NHI_AMT", totalNhiAmt);
		upLoadParmOne.addData("OWN_AMT", ownAmt);
		upLoadParmOne.addData("ADDPAY_AMT", addPayAmt);
		upLoadParmOne.addData("ADM_SEQ", ""); // 就诊顺序号 主键
		upLoadParmOne.addData("FLG", ""); // 新增操作
		upLoadParmOne.addData("HYGIENE_TRADE_CODE", ""); // 批文准号
		upLoadParmOne.addData("CHARGE_DATE", "");
		upLoadParmOne.addData("ADDPAY_FLG", "");
		upLoadParmOne.setCount(upLoadParmOne.getCount() + 1);
		// 添加合计
		newTable.setParmValue(upLoadParmOne);
		this.setValue("NEW_SUM_AMT", totalAmt); // 总金额显示
		callFunction("UI|upload|setEnabled", flg);
		callFunction("UI|detailupload|setEnabled", flg);
	}
	/**
	 * 费用分割后明细数据保存操作
	 */
	public void onSave() {
		TParm parm = newTable.getParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("没有需要保存的数据");
			return;
		}
		parm.setData("OPT_USER", Operator.getID()); // id
		parm.setData("OPT_TERM", Operator.getIP()); // Ip
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域代码
		// 执行添加INS_IBS_UPLOAD表操作
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
	 * 费用分割后明细数据新建操作
	 */
	public void onNew() {
		String[] amtName = { "PRICE", "QTY", "TOTAL_AMT", "TOTAL_NHI_AMT",
				"OWN_AMT", "ADDPAY_AMT" };
		TParm parm = newTable.getParmValue();
		TParm result = new TParm();
		// 添加一条新数据
		for (int i = 0; i < pageFive.length; i++) {
			result.setData(pageFive[i], "");
		}
		for (int j = 0; j < amtName.length; j++) {
			result.setData(amtName[j], "0.00");
		}

		result.setData("FLG", "Y"); // 新增操作
		if (parm.getCount() > 0) {
			// 获得合计数据
			result.setData("ADM_SEQ", parm.getValue("ADM_SEQ", 0)); // 就诊顺序号 主键
			result.setData("HYGIENE_TRADE_CODE", parm.getValue(
					"HYGIENE_TRADE_CODE", 0)); // 批文准号
			TParm lastParm = parm.getRow(parm.getCount() - 1);
			parm.removeRow(parm.getCount() - 1); // 移除合计
			int seqNo = -1; // 获得最大顺序号码
			for (int i = 0; i < parm.getCount(); i++) {
				if (null != parm.getValue("SEQ_NO", i)
						&& parm.getValue("SEQ_NO", i).length() > 0) {
					if (parm.getInt("SEQ_NO", i) > seqNo) {
						seqNo = parm.getInt("SEQ_NO", i);
					}
				}
			}
			result.setData("SEQ_NO", seqNo + 1); // 顺序号
			parm.setRowData(parm.getCount(), result, -1); // 添加新建的数据
			parm.setCount(parm.getCount() + 1);
			parm.setRowData(parm.getCount(), lastParm, -1); // 将合计重新放入
			parm.setCount(parm.getCount() + 1);
		} else {
			this.messageBox("没有数据不可以新建操作");
			return;
		}
		newTable.setParmValue(parm);
	}

	/**
	 * 费用分割后明细数据删除操作
	 */
	public void onDel() {
		int row = newTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要删除的数据");
			return;

		}
		TParm parm = newTable.getParmValue();
		if (parm.getValue("FLG", row).trim().length() <= 0) {
			this.messageBox("不可以删除合计数据");
			return;
		}
		TParm result = INSIbsUpLoadTool.getInstance().deleteINSIbsUploadSeq(
				parm.getRow(row));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		this.messageBox("P0005"); // 执行成功
		onSplitNew(false);
	}

	/**
	 * 添加SYS_FEE弹出窗口(检验检查窗口)
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
		// 求出当前列号
		column = newTable.getColumnModel().getColumnIndex(column);
		String columnName = newTable.getParmMap(column);
		// 医嘱 和 数量操作
		if ("ORDER_CODE".equalsIgnoreCase(columnName)
				|| "QTY".equalsIgnoreCase(columnName)) {
		} else {
			return;
		}
		if ("ORDER_CODE".equalsIgnoreCase(columnName)) {
			TTextField textfield = (TTextField) com;
			TParm parm = new TParm();
			parm.setData("RX_TYPE", ""); // 检验检查 CAT1_TYPE = LIS/RIS
			textfield.onInit();
			// 给table上的新text增加sys_fee弹出窗口
			textfield.setPopupMenuParameter("ORDER", getConfigParm().newConfig(
					"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
			// 给新text增加接受sys_fee弹出窗口的回传值
			textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
					"popExaReturn");
		}

	}

	/**
	 * 重新赋值
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
						.getValue("ORDER_CODE")); // 医嘱码
		newParm
				.setData("ORDER_DESC", selectNewRow, parm
						.getValue("ORDER_DESC")); // 医嘱名称
		newParm.setData("NHI_FEE_DESC", selectNewRow, parm
				.getValue("NHI_FEE_DESC")); // 医保名称
		newParm.setData("PRICE", selectNewRow, parm.getDouble("OWN_PRICE")); // 单价
		newParm.setData("NHI_ORDER_CODE", selectNewRow, parm
				.getValue("NHI_CODE_I")); // 医保费用代码
		newTable.setParmValue(newParm);
	}
	/**
	 * 转病患基本资料
	 */
	public void onQueryInfo() {
		onExe("M");
	}
	/**
	 * 结算操作
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
			this.messageBox("出院时间不可以为空");
			this.grabFocus("DS_DATE");
			return;
		}
		if (null == this.getValue("UPLOAD_DATE")
				|| this.getValue("UPLOAD_DATE").toString().length() <= 0) {
			onCheck("UPLOAD_DATE", "上传时间不可以为空");
			return;
		}
//		 System.out.println("onSettlement===2");
		//费用预结算保存出院信息	
		 if (onBlance(parm).getErrCode() < 0) {
	            return;
	        }
//		 System.out.println("onSettlement===3");
        //得到病案首页的内容
        TParm MRO = INSUpLoadTool.getInstance().getMROData(parm);
        if (MRO.getErrCode() < 0) {
            this.messageBox(MRO.getErrText());
            return ;
        }
        if (MRO.getData("SUM_TOT", 0) == null||
        	MRO.getData("SUM_TOT", 0).equals("")){ 
       	 this.messageBox("首页费用未转入,请联系病案室");
            return;
       }
       //出院诊断信息
        parm.setData("IO_TYPE","O");
		TParm outDiagParm = INSUpLoadTool.getInstance().getDiag(parm);
		 if (outDiagParm.getErrCode() < 0) {
	            this.messageBox(outDiagParm.getErrText());
	            return;
	        }
		//主诊断
		String mainDiag = "";
		mainDiag=outDiagParm.getData("ICD_DESC", 0)+"@"+
		         outDiagParm.getData("ICD_CODE", 0)+"@"+
		         outDiagParm.getData("IN_PAT_CONDITION", 0);
//		System.out.println("mainDiag>>>>>>>>>>>>"+mainDiag);
		MRO.setData("OUT_DIAG_MAIN",0,mainDiag.length()>0? mainDiag:""); 
		//次诊断
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
        //得到手术及操作的内容
        TParm MROOP = INSUpLoadTool.getInstance().getMROOPData(parm);
        if (MROOP.getErrCode() < 0) {
            this.messageBox(MROOP.getErrText());
            return;
        }
        System.out.println("SDISEASE_CODE==="+ parm.getValue("SDISEASE_CODE"));   
        if (this.getValueInt("INS_CROWD_TYPE") == 1){//城职
        //撤销住院费用申报
        this.DataDown_sp_H(parm);
//        System.out.println("onSettlement===5");
        //查询同意住院书是否被审核
      if (this.DataDown_sp_Q(parm).getErrCode() < 0) {
          return ;
      }
//      System.out.println("onSettlement===6");
        //得到结算资料
        parm.setData("DS_DATE", df.format(this.getValue("DS_DATE")));
        TParm result1 = INSUpLoadTool.getInstance().getIBSData(parm);
        if (result1.getErrCode() < 0) {
            this.messageBox(result1.getErrText());
            return;
        }  
        //出院结算  
        parm.setData("REGION_CODE", Operator.getRegion());
        //得到补助金额，补助金额2
        TParm result3 = INSUpLoadTool.getInstance().getIBSHelpAmt(parm);
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return;
        }
        //得到医师证照号
        TParm result4 = INSUpLoadTool.getInstance().getDrQualifyCode(parm);
        if (result4.getErrCode() < 0) {
            this.messageBox(result4.getErrText());
            return;
        }
        result1.setData("DRQUALIFYCODE", result4.getData("DRQUALIFYCODE", 0));
        result1.addData("ARMYAI_AMT", result3.getData("ARMYAI_AMT", 0));
        result1.addData("TOT_PUBMANADD_AMT",
                        result3.getData("TOT_PUBMANADD_AMT", 0));
//       System.out.println("上传入参"+result1);
      //单病种结算信息和出院信息上传
        TParm upParm = new TParm();
        if(parm.getValue("SDISEASE_CODE").length()>0){
        	 //得到单病种结算信息和出院信息上传部分信息查询
            TParm result8 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
            if (result8.getErrCode() < 0) {
                this.messageBox(result8.getErrText());
                return;
            } 	
           upParm = this.DataDown_ssks_G(result1,result8);
        }else
        //结算信息和出院信息上传	
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
        //更新新就医顺序号、个人帐户实际支付金额、帐户余额
        TParm result = TIOM_AppServer.executeAction("action.ins.INSUpLoadAction",
                                              "saveUpLoadData", parm);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return;
        }        
//        System.out.println("onSettlement===8");
    	//城职病案首页撤销
    	this.DataUpload_G1(MRO,"CZ");
        //病案首页上传
        if (this.DataUpload_G(MRO,"CZ").getErrCode() < 0) {        
            return;
        }
//        System.out.println("onSettlement===9");
        if(MROOP.getCount()>0){
        //住院病案首页之手术及操作上传
          if (this.DataUpload_H(MROOP,"CZ").getErrCode() < 0) {
               return;
            }
          }
//        System.out.println("onSettlement===10");        
      }
        else  if (this.getValueInt("INS_CROWD_TYPE") == 2){//城居
        //撤销住院费用申报
        this.DataDown_czys_I(parm);
//        System.out.println("onSettlement===11");
        //查询资格确认书审核情况
        TParm czysDParm = this.DataDown_czys_D(parm);
        if (czysDParm.getErrCode() < 0) {
            return ;
        }
//        System.out.println("onSettlement===12");
        if (!czysDParm.getBoolean("ALLOW_FLG_FLG"))
            return ;
//        System.out.println("onSettlement===13");
        //出院结算  
        parm.setData("REGION_CODE", Operator.getRegion());
//        System.out.println("onSettlement===14rrrr"+this.getValue("DS_DATE"));
        parm.setData("DS_DATE", df.format(this.getValue("DS_DATE")));
//        System.out.println("onSettlement===rrrr"+parm);
        //获得医保结算信息
        TParm result2 = INSUpLoadTool.getInstance().getINSMedAppInfo(parm);
//        System.out.println("onSettlement===dddddd"+result2);
        if (result2.getErrCode() < 0) {
            this.messageBox(result2.getErrText());
            return ;
        } 
       //单病种结算信息和出院信息上传
        TParm upParm = new TParm();
        if(parm.getValue("SDISEASE_CODE").length()>0){
        TParm result6 = INSUpLoadTool.getInstance().getSingleIBSData(parm);
        if (result6.getErrCode() < 0) {
            this.messageBox(result6.getErrText());
            return;
        }
        upParm = this.DataDown_csks_G(result2,result6);
        }else
        //结算信息和出院信息上传	
        upParm = this.DataDown_csks_F(result2);
        
//        System.out.println("onSettlement===eeeee"+csksFParm);
        if (upParm.getErrCode() < 0) {
            return ;
        }
        String newAdmSeq = upParm.getValue("NEWADM_SEQ");
        parm.setData("NEWADM_SEQ", newAdmSeq);
//        System.out.println("onSettlement===wwww"+parm);
        //更新新的就诊顺序号
        TParm result3 = TIOM_AppServer.executeAction(
                "action.ins.INSUpLoadAction",
                "onUpdAdmSeqData", parm);
//        System.out.println("onSettlement===qqqq"+result3);  
        if (result3.getErrCode() < 0) {
            this.messageBox(result3.getErrText());
            return;
        }
//        System.out.println("onSettlement===14");
        //城职病案首页撤销
    	this.DataUpload_G1(MRO,"CJ");
        //病案首页上传
        if (this.DataUpload_G(MRO,"CJ").getErrCode() < 0) {       	
            return;
        }
//        System.out.println("onSettlement===15");
        if(MROOP.getCount()>0){
        //手术及操作上传
         if (this.DataUpload_H(MROOP,"CJ").getErrCode() < 0) {
              return;
          }
        }  
        
     }
       
		this.messageBox("P0005"); // 执行成功
	}	
	/**
	 * 最后一天上传明细和累计增负
	 */
	public TParm updateAddDetail(TParm parm,TParm drparm) {		
		TParm data = new TParm();	
		parm.setData("REGION_CODE", Operator.getRegion());
		String startDate = df.format(this.getValue("UPLOAD_DATE"))+"000000";//开始时间
		String endDate =df.format(this.getValue("UPLOAD_DATE"))+ "235959";//结束时间	
		//上传日明细
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
			this.messageBox("执行失败");
			return result;
		}
		 data = this.DataUploadDetail(result,drparm);
		 if (data.getErrCode() < 0) {			 
			 this.messageBox(data.getErrText());
			 return data;
	        }		 		
		 else{
			     //诊断数据
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
			//更新INS_IBS_UPLOAD字段up_flg为2，已上传
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
	 * 费用预结算保存出院信息	
	 */
	public TParm onBlance(TParm parm) {
		// 单病种操作校验数据
		if (null != type && type.equals("SINGLE")) {

			if (null == this.getValue("ADM_DAYS")
					|| this.getValue("ADM_DAYS").toString().length() <= 0) {
				this.messageBox("住院天数不能为空");
				this.grabFocus("ADM_DAYS");
				tabbedPane.setSelectedIndex(1);
				return null;
			}
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域
		parm.setData("REALOWN_RATE", this.getValue("REALOWN_RATE")); // 本次实际自负比例
		parm.setData("INS_CROWD_TYPE", this.getValueInt("INS_CROWD_TYPE")); // 人群类别
		parm.setData("TYPE", type); // type:SINGLE 单病种操作使用

		parm.setData("DS_DATE", SystemTool.getInstance().getDateReplace(
				this.getValueString("DS_DATE"), true)); // 出院时间 界面获得

		parm.setData("ADM_DAYS", this.getValueInt("ADM_DAYS")); // 住院天数
		String[] name = showValue.split(";"); // 界面可修改数据获得
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i], this.getValue(name[i])); // 可以修改的数据
		}
		parm.setData("CHEMICAL_DESC", this.getText("CHEMICAL_DESC")); // 化验说明
		parm.setData("ADDAMT", addFee);
		// System.out.println("结算入参parm:::::"+parm);
		// 结算操作
		TParm result = new TParm(INSTJAdm.getInstance().onSettlement(
				parm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return result;
		}
		result = INSIbsTool.getInstance().queryIbsSum(parm); // 查询数据给界面赋值
		setSumValue(result, parm);
		tabbedPane.setSelectedIndex(2);	
	    return result;
	}
    /**
     * 撤销住院费用申报
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
     * 查询同意住院书是否被审核
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
     * 撤销申报(城居)
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
     * 查询资格确认书审核情况(城居)
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
     * 病案首页上传
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
         mroParm.addData("HOSP_DESC", regionParm.getValue("REGION_CHN_DESC", 0));//医院名称
         mroParm.addData("NHI_HOSP_NO", regionParm.getValue("NHI_NO", 0));//医院代码
         mroParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//就医顺序号
         mroParm.addData("PAY_WAY", parm.getValue("PAY_WAY", 0).length()>0?
        		 parm.getValue("PAY_WAY", 0):"9");//医疗付费方式
         mroParm.addData("CARD_NO", parm.getData("CARD_NO", 0));//健康卡号
         mroParm.addData("IN_TIMES", parm.getData("IN_TIMES", 0));//年住院次数
         mroParm.addData("MR_NO", parm.getData("MR_NO", 0));//病案号
         mroParm.addData("PAT_NAME", parm.getData("PAT_NAME", 0));//姓名
         mroParm.addData("SEX", parm.getData("SEX", 0));//性别
         mroParm.addData("BIRTH_DATE", parm.getData("BIRTH_DATE", 0));//出生日期
         //获得年龄
        String age = parm.getValue("AGE", 0);
        String age1 ="";
        String age2 ="";
        String age3 ="";
         int ageflg = Integer.valueOf(age.substring(0,age.indexOf("岁")));
        if(ageflg>=1)
        	age1 = age.substring(0,age.indexOf("岁"));
        else if(ageflg<1){
        	if(age.length()>3){
        	age1 = "0";
        	age2 = age.substring(age.indexOf("岁")+1,age.indexOf("月"));
        	age3 = age.substring(age.indexOf("月")+1,age.indexOf("日"));
        	}
        	else
        	age1 = "0";	
        }
//        System.out.println("age1====:"+age1);
//        System.out.println("age2====:"+age2);
//        System.out.println("age3====:"+age3);
         mroParm.addData("AGE1", age1.length()>0?age1:"0");//年龄1
         mroParm.addData("NATION", parm.getData("NATION", 0));//国籍
         mroParm.addData("AGE2", age2.length()>0?age2:"0");//年龄2(月)
         mroParm.addData("AGE3", age3.length()>0?age3:"0");//年龄2(日)
         mroParm.addData("NB_WEIGHT", parm.getValue("NB_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_WEIGHT", 0):"0");//新生儿出生体重
         mroParm.addData("NB_IN_WEIGHT", parm.getValue("NB_IN_WEIGHT", 0).length()>0?
        		 parm.getValue("NB_IN_WEIGHT", 0):"0");//新生儿入院体重
         mroParm.addData("BIRTH_ADDRESS", parm.getData("BIRTH_ADDRESS", 0));//出生地
         mroParm.addData("BIRTHPLACE", parm.getData("BIRTHPLACE", 0));//籍贯
         mroParm.addData("FOLK", parm.getData("FOLK", 0));//民族
         mroParm.addData("ID_NO", parm.getData("ID_NO", 0));//身份证号
         mroParm.addData("OCCUPATION", parm.getData("OCCUPATION", 0));//职业
         mroParm.addData("MARRIGE", parm.getValue("MARRIGE", 0).length()>0?
        		 parm.getValue("MARRIGE", 0):"9");//婚姻状况
         mroParm.addData("ADDRESS", parm.getData("ADDRESS", 0));//现住址
         mroParm.addData("ADDRESS_TEL", parm.getData("ADDRESS_TEL", 0));//现住址电话
         mroParm.addData("POST_NO", parm.getData("POST_NO", 0));//现住址邮编
         mroParm.addData("H_ADDRESS", parm.getData("H_ADDRESS", 0));//户口地址
         mroParm.addData("POST_CODE", parm.getData("POST_CODE", 0));//户口所在地邮编
         mroParm.addData("O_ADDRESS", parm.getData("O_ADDRESS", 0));//工作单位及地址
         mroParm.addData("O_TEL", parm.getData("O_TEL", 0));//工作单位电话
         mroParm.addData("O_POSTNO", parm.getData("O_POSTNO", 0));//单位邮编
         mroParm.addData("CONTACTER", parm.getData("CONTACTER", 0));//联系人姓名
         mroParm.addData("RELATIONSHIP", parm.getData("RELATIONSHIP", 0));//与患者关系
         mroParm.addData("CONT_ADDRESS", parm.getData("CONT_ADDRESS", 0));//联系人地址
         mroParm.addData("CONT_TEL", parm.getData("CONT_TEL", 0));//联系人电话
         mroParm.addData("ADM_SOURCE", parm.getValue("ADM_SOURCE", 0).length()>0?
        		 parm.getValue("ADM_SOURCE", 0):"9");//入院途径
         mroParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//入院时间
         mroParm.addData("IN_DEPT", parm.getData("IN_DEPT", 0));//入院科别
         mroParm.addData("IN_STATION", parm.getData("IN_STATION", 0));//入院病房
         mroParm.addData("TRANS_DEPT", parm.getData("TRANS_DEPT", 0));//转科科别
         mroParm.addData("OUT_DATE", parm.getData("OUT_DATE", 0));//出院时间
         mroParm.addData("OUT_DEPT", parm.getData("OUT_DEPT", 0));//出院科别
         mroParm.addData("OUT_STATION", parm.getData("OUT_STATION", 0));//出院病房
         mroParm.addData("REAL_STAY_DAYS", parm.getData("REAL_STAY_DAYS", 0));//实际住院天数
         mroParm.addData("OE_DIAG_DESC", parm.getData("OE_DIAG_DESC", 0));//门（急）诊诊断
         mroParm.addData("OE_DIAG_CODE", parm.getData("OE_DIAG_CODE", 0));//门（急）诊疾病编码
         mroParm.addData("OUT_DIAG_MAIN", parm.getData("OUT_DIAG_MAIN", 0));//出院主要诊断
         mroParm.addData("OUT_DIAG_OTHER", parm.getData("OUT_DIAG_OTHER", 0));//出院其它诊断
         mroParm.addData("EX_RSN_DESC", parm.getData("EX_RSN_DESC", 0));//损伤、中毒的外部原因
         mroParm.addData("EX_RSN_CODE", parm.getData("EX_RSN_CODE", 0));//损伤、中毒的疾病编码
         mroParm.addData("PATHOLOGY_DIAG", parm.getData("PATHOLOGY_DIAG", 0));//病理诊断
         mroParm.addData("PATHOLOGY_DIAG_CODE", parm.getData("PATHOLOGY_DIAG_CODE", 0));//病理诊断疾病编码
         mroParm.addData("PATHOLOGY_NO", parm.getData("PATHOLOGY_NO", 0));//病理号
         mroParm.addData("ALLEGIC_FLG", parm.getValue("ALLEGIC_FLG", 0).length()>0?
        		 parm.getValue("ALLEGIC_FLG", 0):"1");//药物过敏标志
         mroParm.addData("ALLEGIC", parm.getData("ALLEGIC", 0));//过敏药物
         mroParm.addData("BODY_CHECK", parm.getValue("BODY_CHECK", 0).length()>0?
        		 parm.getValue("BODY_CHECK", 0):"1");//死亡患者尸检标志
         mroParm.addData("BLOOD_TYPE", parm.getValue("BLOOD_TYPE", 0).length()>0?
        		 parm.getValue("BLOOD_TYPE", 0):"6");//血型
         mroParm.addData("RH_TYPE", parm.getValue("RH_TYPE", 0).length()>0?
        		 parm.getValue("RH_TYPE", 0):"4");//RH
         mroParm.addData("DIRECTOR_DR_CODE", parm.getData("DIRECTOR_DR_CODE", 0));//科主任
         mroParm.addData("PROF_DR_CODE", parm.getData("PROF_DR_CODE", 0));//主任（副主任）医师
         mroParm.addData("ATTEND_DR_CODE", parm.getData("ATTEND_DR_CODE", 0));//主治医师
         mroParm.addData("VS_DR_CODE", parm.getData("VS_DR_CODE", 0));//住院医师
         mroParm.addData("VS_NURSE_CODE", parm.getData("VS_NURSE_CODE", 0));//责任护士
         mroParm.addData("INDUCATION_DR_CODE", parm.getData("INDUCATION_DR_CODE", 0));//进修医师
         mroParm.addData("INTERN_DR_CODE", parm.getData("INTERN_DR_CODE", 0));//实习医师
         mroParm.addData("ENCODER", parm.getData("ENCODER", 0));//编码员
         mroParm.addData("QUALITY", parm.getData("QUALITY", 0));//病案质量
         mroParm.addData("CTRL_DR", parm.getData("CTRL_DR", 0));//质控医师
         mroParm.addData("CTRL_NURSE", parm.getData("CTRL_NURSE", 0));//质控护士
         mroParm.addData("CTRL_DATE", parm.getData("CTRL_DATE", 0));//质控日期
         mroParm.addData("OUT_TYPE", parm.getValue("OUT_TYPE", 0).length()>0?
        		 parm.getValue("OUT_TYPE", 0):"9");//离院方式
         mroParm.addData("TRAN_HOSP", parm.getData("TRAN_HOSP", 0));//拟接收医疗机构名称
         mroParm.addData("AGN_PLAN_FLG", parm.getValue("AGN_PLAN_FLG", 0).length()>0?
        		 parm.getValue("AGN_PLAN_FLG", 0):"1");//出院31天内再住院
         mroParm.addData("AGN_INTENTION", parm.getData("AGN_PLAN_INTENTION", 0));//再住院目的
         //颅脑损伤患者昏迷入院前时间
         String becomatime = parm.getValue("BE_COMA_TIME", 0).length()>0? 
        		             parm.getValue("BE_COMA_TIME", 0):"000000";
         becomatime = becomatime.substring(0, 2)+"@"+
                      becomatime.substring(2, 4)+"@"+
                      becomatime.substring(4, 6);
         //颅脑损伤患者昏迷入院后时间
         String afcomatime = parm.getValue("AF_COMA_TIME", 0).length()>0? 
	                         parm.getValue("AF_COMA_TIME", 0):"000000";;
         afcomatime = afcomatime.substring(0, 2)+"@"+
                      afcomatime.substring(2, 4)+"@"+
                      afcomatime.substring(4, 6);
//         System.out.println("afcomatime:"+afcomatime);
         mroParm.addData("BE_COMA_TIME", becomatime);//颅脑损伤患者昏迷入院前时间
         mroParm.addData("AF_COMA_TIME", afcomatime);//颅脑损伤患者昏迷入院后时间
         mroParm.addData("SUM_TOT", parm.getData("SUM_TOT", 0));//住院总金额
         mroParm.addData("OWN_TOT", parm.getData("OWN_TOT", 0));//住院自付金额
         mroParm.addData("CHARGE_01", parm.getData("CHARGE_01", 0));//一般医疗服务费
         mroParm.addData("CHARGE_02", parm.getData("CHARGE_02", 0));//一般治疗操作费
         mroParm.addData("CHARGE_03", parm.getData("CHARGE_03", 0));//护理费
         mroParm.addData("CHARGE_04", parm.getData("CHARGE_04", 0));//综合医疗其他费用
         mroParm.addData("CHARGE_05", parm.getData("CHARGE_05", 0));//病理诊断费
         mroParm.addData("CHARGE_06", parm.getData("CHARGE_06", 0));//实验室诊断费
         mroParm.addData("CHARGE_07", parm.getData("CHARGE_07", 0));//影像学诊断费
         mroParm.addData("CHARGE_08", parm.getData("CHARGE_08", 0));//临床诊断项目费
         
         //计算非手术治疗项目费
         double charge09 = parm.getDouble("CHARGE_09",0);//临床物理治疗费
         double charge10 = parm.getDouble("CHARGE_10",0);//非临床物理治疗费    
         mroParm.addData("CHARGE_09", parm.getData("CHARGE_09", 0));//临床物理治疗费
         mroParm.addData("CHARGE_10", df.format(charge09+charge10));//非手术治疗项目费
       
         //计算手术治疗费
         double charge11 = parm.getDouble("CHARGE_11",0);//麻醉费
         double charge12 = parm.getDouble("CHARGE_12",0);//手术费
         double charge13 = parm.getDouble("CHARGE_13",0);//手术治疗费其他        
         mroParm.addData("CHARGE_13", df.format(charge11+charge12+charge13));//手术治疗费
         mroParm.addData("CHARGE_11", parm.getData("CHARGE_11", 0));//麻醉费
         mroParm.addData("CHARGE_12", parm.getData("CHARGE_12", 0));//手术费
         mroParm.addData("CHARGE_14", parm.getData("CHARGE_14", 0));//康复费
         mroParm.addData("CHARGE_15", parm.getData("CHARGE_15", 0));//中医治疗费
         //计算西药费
         double charge16 = parm.getDouble("CHARGE_16",0);
         double charge17 = parm.getDouble("CHARGE_17",0);
         mroParm.addData("CHARGE_16_17", df.format(charge16+charge17));//西药费
         mroParm.addData("CHARGE_16", parm.getData("CHARGE_16", 0));//抗菌药物费用
         mroParm.addData("CHARGE_18", parm.getData("CHARGE_18", 0));//中成药费
         mroParm.addData("CHARGE_19", parm.getData("CHARGE_19", 0));//中草药费
         mroParm.addData("CHARGE_20", parm.getData("CHARGE_20", 0));//血费
         mroParm.addData("CHARGE_21", parm.getData("CHARGE_21", 0));//白蛋白类制品费
         mroParm.addData("CHARGE_22", parm.getData("CHARGE_22", 0));//球蛋白类制品费
         mroParm.addData("CHARGE_23", parm.getData("CHARGE_23", 0));//凝血因子类制品费
         mroParm.addData("CHARGE_24", parm.getData("CHARGE_24", 0));//细胞因子类制品费
         mroParm.addData("CHARGE_25", parm.getData("CHARGE_25", 0));//检查用一次性医用材料费
         mroParm.addData("CHARGE_26", parm.getData("CHARGE_26", 0));//治疗用一次性医用材料费
         mroParm.addData("CHARGE_27", parm.getData("CHARGE_27", 0));//手术用一次性医用材料费
         mroParm.addData("CHARGE_28", parm.getData("CHARGE_28", 0));//其他费 
         //重症监护
         String icuRoom = "";
         if(parm.getValue("ICU_ROOM1", 0).length()==0&&
        	parm.getValue("ICU_ROOM2", 0).length()==0&&
        	parm.getValue("ICU_ROOM3", 0).length()==0&&
        	parm.getValue("ICU_ROOM4", 0).length()==0&&
        	parm.getValue("ICU_ROOM5", 0).length()==0)
            mroParm.addData("ICU_ROOM",icuRoom);//重症监护  
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
             mroParm.addData("ICU_ROOM",icuRoom.substring(0, icuRoom.length() - 1));//重症监护 
         }
         mroParm.addData("VENTI_TIME",parm.getValue("VENTI_TIME", 0).length()>0?
        		 parm.getValue("VENTI_TIME", 0):"0");//累计使用小时数
         mroParm.addData("PARM_COUNT", 107);//入参数量
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
     * 住院病案首页之手术及操作上传
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
        	mroopParm.addData("ADM_SEQ", parm.getValue("ADM_SEQ", i));//就医顺序号
        	mroopParm.addData("OPT_CODE", parm.getValue("OPT_CODE", i));//操作编码
        	mroopParm.addData("OP_DATE", parm.getValue("OP_DATE", i));//日期
        	mroopParm.addData("OP_LEVEL", parm.getValue("OP_LEVEL", i));//手术级别
        	mroopParm.addData("OP_NAME", parm.getValue("OP_NAME", i));//手术名称
        	mroopParm.addData("OP_DR_NAME", parm.getValue("OP_DR_NAME", i));//手术医师姓名
        	mroopParm.addData("AST_DR1", parm.getValue("AST_DR1", i));//1助姓名
        	mroopParm.addData("AST_DR2", parm.getValue("AST_DR2", i));//2助姓名
        	mroopParm.addData("HEAL_LEV", parm.getValue("HEAL_LEV", i));//切口愈合等级
        	mroopParm.addData("ANA_WAY", parm.getValue("ANA_WAY", i));//麻醉方式
        	mroopParm.addData("ANA_DR", parm.getValue("ANA_DR", i));//麻醉医师
        	mroopParm.addData("SEQ_NO", parm.getValue("SEQ_NO", i));//序号
        	mroopParm.addData("PARM_COUNT", 12);//入参数量
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
     * 结算信息和出院信息上传
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_ssks_F(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_ssks");
        confInParm.setData("PLOT_TYPE", "F");

        confInParm.addData("CONFIRM_NO", parm.getData("ADM_SEQ", 0));//就医顺序号
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));//资格确认书来源
        confInParm.addData("SID", parm.getData("IDNO", 0));//身份证号码
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));//医院编码
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));//医院所属分中心
        confInParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));//人员类别
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));//就医类别
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//入院时间
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));//出院时间
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().
        		selInsICDCode(parm.getValue("DIAG_CODE", 0)));//出院诊断
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));//出院诊断名称
        //次诊断截取传入
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }         
        confInParm.addData("DIAG_DESC2", diagdesc2);//出院诊断描述
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));//出院情况
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);//自负比例
        confInParm.addData("DECREASE_RATE",
                           parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("DECREASE_RATE", 0) / 100);//减负比例
        confInParm.addData("REALOWN_RATE",
                           parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("REALOWN_RATE", 0) / 100);//实际自负比例
        confInParm.addData("INSOWN_RATE",
                           parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("INSOWN_RATE", 0) / 100);//医疗救助自负比例
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));//住院号
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));//住院病区 
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));//住院床位号
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));//住院科别
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));//基本医疗剩余额
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));//医疗救助剩余额
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));//实际起付标准
        confInParm.addData("ISSUE", parm.getValue("YEAR_MON", 0).substring(0, 6));//期号
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));//药品费发生金额
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));//药品费申报金额
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));//检查费发生金额
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));//检查费申报金额
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));//治疗费发生金额
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));//治疗费申报金额
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));//手术费发生金额
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));//手术费申报金额
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));//床位费发生金额
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));//床位费申报金额
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));//医用材料发生金额
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));//医用材料申报金额
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));//其他发生金额
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));//其他申报金额
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));//输全血发生金额
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));//输全血申报金额
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));//成分输血发生金额
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));//成分输血申报金额
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));//本次实收起付标准金额
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));//起付标准以上自负比例金额
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));//自费项目金额
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));//医疗救助个人按比例负担金额
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));//增负项目金额
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));//医疗救助最高限额以上金额
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));//输血自负金额
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));//基本医疗社保申请金额
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));//医疗救助社保申请金额
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));//住院科别代码
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));//化验说明
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));//就医项目
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));//门特类别
        confInParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));//补助金额
        confInParm.addData("COMU_NO", "");//社区编码
        confInParm.addData("DR_CODE", parm.getData("DRQUALIFYCODE",0));//医师编码
        confInParm.addData("PUBMANAI_AMT", parm.getData("TOT_PUBMANADD_AMT", 0));//补助金额2
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));//其它出院诊断
        //病种付费退出原因
        confInParm.addData("QUIT_REMARK", parm.getValue("QUIT_REMARK", 0).length()>0?
          		 parm.getValue("QUIT_REMARK", 0):"");
        confInParm.addData("PARM_COUNT", 62);
//       System.out.println("DataDown_ssks_F接口入参======"+confInParm);
        result = InsManager.getInstance().safe(confInParm);
        //System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 单病种结算信息和出院信息上传
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
        //匹配银海诊断
        confInfoParm.addData("DIAG_CODE",INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        confInfoParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //次诊断截取传入
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
        confInfoParm.addData("NHI_OWN_AMT", parm.getData("SINGLE_NHI_AMT", 0)); //病种申报金额
        confInfoParm.addData("EXT_OWN_AMT",
                             parm.getData("SINGLE_STANDARD_OWN_AMT", 0)); //医院超病种标准自负金额
        confInfoParm.addData("COMP_AMT", parm.getData("SINGLE_SUPPLYING_AMT", 0)); //基本医疗保险补足金额
        confInfoParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        confInfoParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //统筹基金自负标准金额
        confInfoParm.addData("APPLY_OWN_AMT_STD", dataParm.getData("STARTPAY_OWN_AMT", 0));
        //医疗救助自负标准金额
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
        //补助金额
        confInfoParm.addData("ARMYAI_AMT", parm.getData("ARMYAI_AMT", 0));
        //社区编码
        confInfoParm.addData("COMU_NO", "");
        //单病种编码
        confInfoParm.addData("SIN_DISEASE_CODE", dataParm.getData("SDISEASE_CODE", 0)); 
        //医师编码
        confInfoParm.addData("DR_CODE", parm.getData("DRQUALIFYCODE",0));
        //补助金额2
        confInfoParm.addData("PUBMANAI_AMT", parm.getData("PUBMANAI_AMT", 0));
        
        //特需自费金额
        double BED_SINGLE_AMT = dataParm.getDouble("BED_SINGLE_AMT", 0);
        double MATERIAL_SINGLE_AMT = dataParm.getDouble("MATERIAL_SINGLE_AMT", 0);
        double specNeedAmt = BED_SINGLE_AMT + MATERIAL_SINGLE_AMT;
        confInfoParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //其它出院诊断
        confInfoParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //病种上传方式
        confInfoParm.addData("SINGLE_UPLOAD_TYPE", parm.getValue("SINGLE_UPLOAD_TYPE", 0).length()>0?
          		 parm.getValue("SINGLE_UPLOAD_TYPE", 0):"");
        confInfoParm.addData("PARM_COUNT", 66);
        System.out.println("DataDown_ssks_G接口入参======"+confInfoParm);
        result = InsManager.getInstance().safe(confInfoParm);
        System.out.println("result" + result);
        if (result.getErrCode() < 0) {
            this.messageBox(result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 结算信息和出院信息上传(城居)
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_csks_F(TParm parm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_csks");
        confInParm.setData("PLOT_TYPE", "F");

        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//就医顺序号
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));//资格确认书来源
        confInParm.addData("SID", parm.getData("IDNO", 0));//身份证号码
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));//医院编码
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));//医院所属分中心
        confInParm.addData("CTZ1_CODE", parm.getValue("CTZ1_CODE", 0));//人员类别
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));//就医类别
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));//入院时间
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));//出院时间
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().
        		selInsICDCode(parm.getValue("DIAG_CODE", 0)));//出院诊断
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));//出院诊断名称
        //次诊断截取传入
        String diagdesc2= parm.getValue("DIAG_DESC2", 0);
        int i = 1;
        while (i==1){
     	 byte[] buf= diagdesc2.getBytes(); 
     	 if(buf.length>=256)    
     		diagdesc2= diagdesc2.substring(0,diagdesc2.lastIndexOf(","));
     	 else
     		 break;
        }         
        confInParm.addData("DIAG_DESC2",diagdesc2);//出院诊断描述
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));//出院情况
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);//自负比例
        confInParm.addData("DECREASE_RATE",
                           parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("DECREASE_RATE", 0) / 100);//减负比例
        confInParm.addData("REALOWN_RATE",
                           parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("REALOWN_RATE", 0) / 100);//实际自负比例
        confInParm.addData("INSOWN_RATE",
                           parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("INSOWN_RATE", 0) / 100);//医疗救助自负比例
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));//住院号
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));//住院病区 
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));//住院床位号
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));//住院科别
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));//基本医疗剩余额
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));//医疗救助剩余额
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));//实际起付标准
        confInParm.addData("ISSUE", parm.getValue("YEAR_MON", 0).substring(0, 6));//期号
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));//药品费发生金额
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));//药品费申报金额
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));//检查费发生金额
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));//检查费申报金额
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));//治疗费发生金额
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));//治疗费申报金额
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));//手术费发生金额
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));//手术费申报金额
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));//床位费发生金额
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));//床位费申报金额
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));//医用材料发生金额
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));//医用材料申报金额
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));//其他发生金额
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));//其他申报金额
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));//输全血发生金额
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));//输全血申报金额
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));//成分输血发生金额
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));//成分输血申报金额
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));//本次实收起付标准金额
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));//起付标准以上自负比例金额
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));//自费项目金额
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));//医疗救助个人按比例负担金额
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));//增负项目金额
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));//医疗救助最高限额以上金额
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));//输血自负金额
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));//基本医疗社保申请金额
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));//医疗救助社保申请金额
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));//住院科别代码
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));//化验说明
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));//就医项目
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));//门特类别
        confInParm.addData("BEARING_OPERATIONS_TYPE",
                           parm.getData("BEARING_OPERATIONS_TYPE", 0));//计生手术类别
        confInParm.addData("SOAR_CODE", "");//社区编码
        confInParm.addData("DR_QUALIFY_CODE", parm.getData("LCS_NO", 0));//医师编码    
        confInParm.addData("AGENT_AMT", parm.getData("ARMYAI_AMT", 0));//补助金额
        confInParm.addData("BIRTH_TYPE", "");//生育方式      
        confInParm.addData("BABY_NO", 0);//分娩胎儿数量        
        confInParm.addData("ILLNESS_SUBSIDY_AMT", 
        		parm.getData("ILLNESS_SUBSIDY_AMT", 0));//城乡大病救助       
        confInParm.addData("OTHER_DIAGE_CODE", 
        		parm.getData("OTHER_DIAGE_CODE", 0));//其它出院诊断 
        //病种付费退出原因
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
     * 单病种结算信息和出院信息上传(城居)
     * @param parm TParm
     * @return TParm
     */
    public TParm DataDown_csks_G(TParm parm, TParm dataParm) {
        TParm result = new TParm();
        TParm confInParm = new TParm();
        confInParm.setData("PIPELINE", "DataDown_csks");
        confInParm.setData("PLOT_TYPE", "G");
        //就医顺序
        confInParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));
        //资格确认书来
        confInParm.addData("CONFIRM_SRC", parm.getData("CONFIRM_SRC", 0));
        //身份证号
        confInParm.addData("SID", parm.getData("IDNO", 0));
        //医院编码
        confInParm.addData("HOSP_NHI_NO", parm.getData("HOSP_NHI_NO", 0));
        //医院所属分中心
        confInParm.addData("HOSP_CLEFT_CENTER",
                           parm.getData("INSBRANCH_CODE", 0));
        //人员类别
        confInParm.addData("CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
        //就医类别
        confInParm.addData("ADM_CATEGORY", parm.getData("ADM_CATEGORY", 0));
        //入院时间
        confInParm.addData("IN_DATE", parm.getData("IN_DATE", 0));
        //出院时间
        confInParm.addData("OUT_HOSP_DATE", parm.getData("DS_DATE", 0));
        //出院诊断
        confInParm.addData("DIAG_CODE", INSTJTool.getInstance().selInsICDCode(parm.getValue("DIAG_CODE", 0)));
        //出院诊断名称
        confInParm.addData("DIAG_DESC", parm.getData("DIAG_DESC", 0));
        //出院诊断描述
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
        //出院清空
        confInParm.addData("SOURCE_CODE", parm.getData("SOURCE_CODE", 0));
        //自负比例
        confInParm.addData("OWN_RATE",
                           parm.getDouble("OWN_RATE", 0) == 0 ? 0.00 :
                           parm.getDouble("OWN_RATE", 0) / 100);
        //减负比例
        confInParm.addData("DECREASE_RATE", parm.getDouble("DECREASE_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("DECREASE_RATE", 0) / 100);
        //实际自负比例
        confInParm.addData("REALOWN_RATE", parm.getDouble("REALOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("REALOWN_RATE", 0) / 100);
        //医疗救助自负比例
        confInParm.addData("INSOWN_RATE", parm.getDouble("INSOWN_RATE", 0) == 0 ? 0.00 :
            parm.getDouble("INSOWN_RATE", 0) / 100);
        
        //住院号
        confInParm.addData("CASE_NO", parm.getData("CASE_NO", 0));
        //住院病区
        confInParm.addData("INHOSP_AREA", parm.getData("STATION_DESC", 0));
        //住院床位
        confInParm.addData("INHOSP_BED_NO", parm.getData("BED_NO", 0));
        //住院科别
        confInParm.addData("DEPT", parm.getData("DEPT_DESC", 0));
        //基本医疗剩余额
        confInParm.addData("BASEMED_BALANCE", parm.getData("BASEMED_BALANCE", 0));
        //医疗救助额
        confInParm.addData("INS_BALANCE", parm.getData("INS_BALANCE", 0));
        //实际起付标准
        confInParm.addData("STANDARD_AMT", parm.getData("START_STANDARD_AMT", 0));
        //期号
        confInParm.addData("ISSUE", parm.getData("YEAR_MON", 0));
        //药品发生额
        confInParm.addData("PHA_AMT", parm.getData("PHA_AMT", 0));
        //药品申报额
        confInParm.addData("PHA_NHI_AMT", parm.getData("PHA_NHI_AMT", 0));
        //检查费发生额
        confInParm.addData("EXM_AMT", parm.getData("EXM_AMT", 0));
        //检查费申报额
        confInParm.addData("EXM_NHI_AMT", parm.getData("EXM_NHI_AMT", 0));
        //治疗费发生额
        confInParm.addData("TREAT_AMT", parm.getData("TREAT_AMT", 0));
        //治疗费申报额
        confInParm.addData("TREAT_NHI_AMT", parm.getData("TREAT_NHI_AMT", 0));
        //手术费发生额
        confInParm.addData("OP_AMT", parm.getData("OP_AMT", 0));
        //手术费申报额
        confInParm.addData("OP_NHI_AMT", parm.getData("OP_NHI_AMT", 0));
        //床位费发生额
        confInParm.addData("BED_AMT", parm.getData("BED_AMT", 0));
        //床位费申报额
        confInParm.addData("BED_NHI_AMT", parm.getData("BED_NHI_AMT", 0));
        //医用材料发生金额
        confInParm.addData("MATERIAL_AMT", parm.getData("MATERIAL_AMT", 0));
        //医用材料申报金额
        confInParm.addData("MATERIAL_NHI_AMT",
                           parm.getData("MATERIAL_NHI_AMT", 0));
        //其他发生额
        confInParm.addData("ELSE_AMT", parm.getData("OTHER_AMT", 0));
        //其他申报额
        confInParm.addData("ELSE_NHI_AMT", parm.getData("OTHER_NHI_AMT", 0));
        //输全血发生额
        confInParm.addData("BLOODALL_AMT", parm.getData("BLOODALL_AMT", 0));
        //输全血申报额
        confInParm.addData("BLOODALL_NHI_AMT",
                           parm.getData("BLOODALL_NHI_AMT", 0));
        //成分输血发生额
        confInParm.addData("BLOOD_AMT", parm.getData("BLOOD_AMT", 0));
        //成分输血申报额
        confInParm.addData("BLOOD_NHI_AMT", parm.getData("BLOOD_NHI_AMT", 0));
        //本次实收起付标准金额
        confInParm.addData("BCSSQF_STANDRD_AMT",
                           parm.getData("RESTART_STANDARD_AMT", 0));
        //病种申报金额
        confInParm.addData("NHI_OWN_AMT", dataParm.getData("SINGLE_NHI_AMT", 0));
        //医院超病种标准自负金额
        confInParm.addData("EXT_OWN_AMT",dataParm.getData("SINGLE_STANDARD_OWN_AMT", 0));
        //基本医疗保险补足金额
        confInParm.addData("COMP_AMT", dataParm.getData("SINGLE_SUPPLYING_AMT", 0));
        //自费项目金额
        confInParm.addData("OWN_AMT", parm.getData("OWN_AMT", 0));
        //增付项目金额
        confInParm.addData("ADD_AMT", parm.getData("ADD_AMT", 0));
        //起付标准以上自负比例金额
        confInParm.addData("INS_STANDARD_AMT",
                           parm.getData("STARTPAY_OWN_AMT", 0));
        //医疗救助个人按比例负担金额
        confInParm.addData("PERCOPAYMENT_RATE_AMT",
                           parm.getData("PERCOPAYMENT_RATE_AMT", 0));
        //医疗救助最高限额以上金额
        confInParm.addData("INS_HIGHLIMIT_AMT",
                           parm.getData("INS_HIGHLIMIT_AMT", 0));
        //输血自负金额
        confInParm.addData("TRANBLOOD_OWN_AMT",
                           parm.getData("TRANBLOOD_OWN_AMT", 0));
        //基本医疗社保申请金额
        confInParm.addData("TOTAL_AGENT_AMT", parm.getData("NHI_PAY", 0));
        //医疗救助社保申请金额
        confInParm.addData("FLG_AGENT_AMT", parm.getData("NHI_COMMENT", 0));
        //住院科别代码
        confInParm.addData("DEPT_CODE", parm.getData("DEPT_CODE", 0));
        //化验说明
        confInParm.addData("CHEMICAL_DESC", parm.getData("CHEMICAL_DESC", 0));
        //就医项目
        confInParm.addData("CONFIRM_ITEM", parm.getData("ADM_PRJ", 0));
        //门特类别
        confInParm.addData("SPEDRS_CODE", parm.getData("SPEDRS_CODE", 0));
        //社区编码
        confInParm.addData("COMU_NO", ""); //固定空值
        //单病种编码
        confInParm.addData("SIN_DISEASE_CODE", parm.getData("SDISEASE_CODE", 0));
        //医师编码
        confInParm.addData("DR_CODE", parm.getData("LCS_NO", 0));
        //补助金额1
        double armyaiAmt = parm.getDouble("ARMYAI_AMT",0);
        //补助金额2
        double pubmanaiAmt = parm.getDouble("PUBMANAI_AMT",0);
        double agentAmt = armyaiAmt + pubmanaiAmt;
        //补助金额
        confInParm.addData("AGENT_AMT", agentAmt);
        //床位费特需金额
        double bedSingleAmt = dataParm.getDouble("BED_SINGLE_AMT",0);
        //医用材料费特需金额
        double materialSingleAmt = dataParm.getDouble("MATERIAL_SINGLE_AMT",0);
        double specNeedAmt = bedSingleAmt + materialSingleAmt;
        //System.out.println("specNeedAmt:"+specNeedAmt);
        //特需项目金额
        confInParm.addData("SPEC_NEED_AMT", specNeedAmt);
        //城乡大病救助
        confInParm.addData("ILLNESS_SUBSIDY_AMT", parm.getData("ILLNESS_SUBSIDY_AMT", 0));
        //其它出院诊断
        confInParm.addData("OTHER_DIAGE_CODE", parm.getData("OTHER_DIAGE_CODE", 0));
        //病种上传方式
        confInParm.addData("SINGLE_UPLOAD_TYPE", parm.getValue("SINGLE_UPLOAD_TYPE", 0).length()>0?
          		 parm.getValue("SINGLE_UPLOAD_TYPE", 0):"");
        //入参个数
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
     * 病案首页撤销
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
         mroParm.addData("ADM_SEQ", parm.getData("ADM_SEQ", 0));//就医顺序号
         mroParm.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));//医院代码
         mroParm.addData("PARM_COUNT", 2);//入参数量
         result = InsManager.getInstance().safe(mroParm);
         System.out.println("result病案首页撤销======" + result);
    	 return result;
    }
	/**
	 * 病患信息表格单击事件
	 */
	public void onTableClick() {
		onSplitOld(false);
		onSplitNew(false);
		int row = tableInfo.getSelectedRow();
		TParm parm = tableInfo.getParmValue().getRow(row);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", "")
				.substring(0, 6)); // 期号
		TParm result = INSIbsTool.getInstance().queryIbsSum(parm); // 查询数据给界面赋值
		nhiCode = result.getValue("NHI_CODE", 0);
		setSumValue(result, parm);
		//显示上传日期列表
		setUpdateValue(parm);
		this.setValueForParm(pageHead, parm);
		this.setValue("INS_CROWD_TYPE", parm.getValue("INS_CROWD_TYPE")); //人群类别
		if (this.getValue("PAT_TYPE").equals("02")){//出院病人
		if(parm.getValue("SDISEASE_CODE").length()>0){
			//病种上传方式显示
			callFunction("UI|SINGLE_UPLOAD_TYPE|setEnabled", true);
			//病种付费退出原因不显示
			callFunction("UI|QUIT_REMARK|setEnabled",false);
		}else{
			//病种上传方式显示
			callFunction("UI|SINGLE_UPLOAD_TYPE|setEnabled", false);
			//病种付费退出原因不显示
			callFunction("UI|QUIT_REMARK|setEnabled",true);
		}			
	}else{
		//病种上传方式显示
		callFunction("UI|SINGLE_UPLOAD_TYPE|setEnabled", false);
		//病种付费退出原因不显示
		callFunction("UI|QUIT_REMARK|setEnabled",false);
	}
	}
	/**
	 * 显示上传日期列表
	 */
	private void setUpdateValue(TParm parm) {
		tabledate.removeRowAll();
		String sql = " SELECT CHARGE_DATE FROM INS_IBS_UPLOAD " +
 		             " WHERE ADM_SEQ = '"+ parm.getValue("ADM_SEQ") + "'" +
 		             " AND UP_FLG = '2'" +
 				     " GROUP BY CHARGE_DATE";
//		 System.out.println("sql===============" + sql);
		 TParm result = new TParm(TJDODBTool.getInstance().select(sql));
//	     System.out.println("result===显示上传日期" + result);
	       if (result.getErrCode() < 0) {
		        return;
	          }	      		
	    tabledate.setParmValue(result);
		
	}
	/**
	 * 界面数据赋值
	 * 
	 * @param result
	 *            TParm
	 * @param parm
	 *            TParm
	 */
	private void setSumValue(TParm result, TParm parm) {
		this.setValueForParm(pageTwo + ";" + pageThree + ";" + showValue,
				result.getRow(0));
		this.setText("CHEMICAL_DESC", result.getValue("CHEMICAL_DESC", 0)); // 化验证明
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
		// 单病种操作执行
		if (null != type && type.equals("SINGLE")) {
			//基本信息
			TParm mroParm = MRORecordTool.getInstance().getInHospInfo(parm);
			//出院诊断信息
			parm.setData("IO_TYPE","O");
			TParm outDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			//入院信息
			parm.setData("IO_TYPE","M");
			TParm inDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			//门急诊诊断
			parm.setData("IO_TYPE","I");
			TParm oeDiagParm = MRORecordTool.getInstance().getDiagForIns(parm);
			
			//院感信息
			
			setValueForParm(mroRecordName, mroParm.getRow(0));
			setValueForParm(pageSix, result.getRow(0));
			
			//实际起付标准金额 (按病种付费没有起付标准？)
			setValue("QFBZ_AMT_S", "0.00");
			//统筹基金自负标准金额
			setValue("TC_OWN_AMT_S", result.getRow(0).getDouble("STARTPAY_OWN_AMT"));
			//医疗救助自负标准金额
			setValue("JZ_OWN_AMT_S", result.getRow(0).getDouble("PERCOPAYMENT_RATE_AMT"));
			//特需自费金额
			double txAmt  =  result.getRow(0).getDouble("BED_SINGLE_AMT")+result.getRow(0).getDouble("MATERIAL_SINGLE_AMT");
			
			setValue("TX_OWN_AMT_S", txAmt);
			//最高限额以上金额
			setValue("ZGXE_AMT_S", result.getRow(0).getDouble("INS_HIGHLIMIT_AMT"));
			//合计
			double totAmt = txAmt + result.getRow(0).getDouble("INS_HIGHLIMIT_AMT")
                            + result.getRow(0).getDouble("STARTPAY_OWN_AMT")
                            + result.getRow(0).getDouble("PERCOPAYMENT_RATE_AMT");
			
			setValue("TOTAL_AMT_S", totAmt);
			
			//首次病程记录
			setValue("FP_NOTE", result.getRow(0).getValue("FP_NOTE")); 
			//出院小结
            setValue("DS_SUMMARY", result.getRow(0).getValue("DS_SUMMARY")); 
            //住院医师
            setValue("VS_DR_CODE1", mroParm.getRow(0).getValue("VS_DR_CODE")); 
            //出院诊断
            for (int i = 0; i < outDiagParm.getCount(); i++) 
            { 	
              //出院诊断
              String icdCode = "" + outDiagParm.getData("ICD_CODE", i);
			  String icdDesc = "" + outDiagParm.getData("ICD_DESC", i);
			  String icdStatus =  "" +  outDiagParm.getData("ICD_STATUS", i);
			  setValue("OUT_ICD_CODE"+(i+1), icdCode);
			  setValue("OUT_ICD_DESC"+(i+1), icdDesc);
			  setValue("ADDITIONAL_CODE"+(i+1), icdStatus);
			}
            //门急诊诊断
            String oeDiag = "";
            for(int i = 0; i<oeDiagParm.getCount();i++)
            {
            	oeDiag += (oeDiagParm.getData("ICD_CODE", i)+"_"+oeDiagParm.getData("ICD_DESC", i));	
            }
            setValue("OE_DIAG_CODE", oeDiag);
            // 入院诊断
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
	 * 第三个页签中总金额数据赋值
	 * 
	 * @param result
	 *            TParm
	 */
	private void getTotAmtValue(TParm result) {
		// 费用合计
		for (int i = 0; i < nameAmt.length; i++) {
			double sum = 0.00;
			for (int j = 0; j < nameType.length; j++) {
				sum += result.getRow(0).getDouble(nameType[j] + nameAmt[i]);
				this.setValue("TOT" + nameAmt[i], sum);
			}
		}
		double sum = 0.00;
		// 医保金额合计
		for (int i = 0; i < insAmt.length; i++) {
			sum += this.getValueDouble(insAmt[i]);
		}
		this.setValue("SUM_TOT_AMT", sum); // 总计
	}
	/**
	 * 清空
	 */
	public void onClear() {
		// isEnable(pageThree, true);
		// 头部
		clearValue(pageHead + ";INS_CROWD_TYPE");
		// 页签
		clearValue(pageTwo + ";" + pageThree
				+ ";CHEMICAL_DESC;FP_NOTE;DS_SUMMARY;" + showValue
				+ ";" + mroRecordName + ";" + pageSix);
		// 移除数据
		tableInfo.removeRowAll();
		oldTable.acceptText();
		oldTable.setDSValue();
		oldTable.removeRowAll();
		newTable.acceptText();
		newTable.setDSValue();
		newTable.removeRowAll();
		tabbedPane.setSelectedIndex(0); // 第一个页签
		tabledate.removeRowAll();
		clearValue("SUM_AMT;NEW_SUM_AMT");
        //病患类型赋值
        this.setValue("PAT_TYPE","01");
        callFunction("UI|changeInfo|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|detailupload|setEnabled", false);
        //上传费用时间赋值
        Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance().getDate(), -1);
        this.setValue("UPLOAD_DATE", yesterday);
	}

	/**
	 * 执行编辑状态
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
	 * 第二个页签保存操作
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
		// ============pangben 去掉回车符
		String chemical = this.getText("CHEMICAL_DESC");
		parm.setData("CHEMICAL_DESC", chemical.replace("\n", "")); // 化验说明
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
	 * 单病种 手术记录查询操作
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
	 * 单病种费用分割 病历首页 中保存操作
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
	 * 主诊断码 为空时 主诊断中文不显示
	 */
	public void onDiagLost() {
		if (this.getValueString("DIAG_CODE").trim().length() <= 0) {
			this.setValue("DIAG_DESC", "");
		}
	}

	/**
	 * 诊断事件
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
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener(final TTable table) {
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = newTable.getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);
				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = newTable.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectory转成param
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
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		newTable.setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 拿到菜单
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * 得到 Vector 值
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
	 * 转换parm中的列
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
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}
	/**
	 * 获得病种付费退出原因
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
	 * 获得病种上传方式
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
