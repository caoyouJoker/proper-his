package com.javahis.ui.ins;

import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.Vector;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSADMConfirmTool;
import jdo.ins.INSIbsOrderTool;
import jdo.ins.INSIbsTool;
import jdo.ins.INSIbsUpLoadTool;
import jdo.ins.INSTJAdm;
import jdo.ins.INSTJTool;
import jdo.mro.MRORecordTool;
import jdo.opd.TotQtyTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
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
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;



/**
 * 
 * <p>
 * Title:住院费用分割
 * </p>
 * 
 * <p>
 * Description:住院费用分割 合并单病种分割
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2011
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author pangb 2011-12-01
 * @version 2.0
 */
public class INSBalanceControl extends TControl {
	// 排序
	private Compare compare = new Compare();
	// 排序
	private boolean ascending = false;
	// 排序
	private int sortColumn = -1;
	// 医保身份
	String nhiCode = "";
	private TTable tableInfo; // 病患基本信息列表
	private TTable oldTable; // 费用分割前数据
	private TTable newTable; // 费用分割后数据
	private TTabbedPane tabbedPane; // 页签
	DateFormat df = new SimpleDateFormat("yyyyMMdd");
	DateFormat df1 = new SimpleDateFormat("yyyy");
	private TParm regionParm; // 医保区域代码
	int index = 0; // 费用分割 累计需要添加数据个数
	int selectNewRow; // 费用分割后明细数据获得当前选中行
	String single_type; // TYPE: SINGLE单病种界面显示
	//Color red = new Color(255, 0, 0); // 明细下载不同数据颜色
	private String showValue = "IDNO;IN_DATE;STATION_CODE;BED_NO;UPLOAD_FLG;"
			+ "DRG_FLG;DIAG_CODE;DIAG_DESC2;DIAG_DESC;SOURCE_CODE;"
			+ "HOMEDIAG_DESC;QUIT_REMARK;SINGLE_UPLOAD_TYPE"; // 可以修改的数据
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
	private String singleName = "SPECIAL_PAT_CODE;COMPANY_TYPE;PAT_CLASS;PROGRESS;" +
			"LBL_SPECIAL_PAT_CODE;LBL_COMPANY_TYPE;LBL_PAT_CLASS;LBL_PROGRESS;" +
			"LAB_LOCAL_FLG;LOCAL_FLG"; // 单病种操作不显示的控件
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
		addListener1(tableInfo);
	}

	/**
	 * 初始化数据
	 */
	private void initParm() {
		single_type = (String) getParameter(); // TYPE: SINGLE 单病种
		tableInfo = (TTable) this.getComponent("TABLEINFO"); // 病患基本信息列表
		oldTable = (TTable) this.getComponent("OLD_TABLE"); // 费用分割前数据
		newTable = (TTable) this.getComponent("NEW_TABLE"); // 费用分割后数据
		tabbedPane = (TTabbedPane) this.getComponent("TABBEDPANE"); // 页签
		this.setValue("START_DATE", SystemTool.getInstance().getDate()); // 入院开始时间
		this.setValue("END_DATE", SystemTool.getInstance().getDate()); // 入院结束时间
		//初始化数据
        onExeEnable(false);
        this.setValue("START_DATE_OUT", SystemTool.getInstance().getDate());
        this.setValue("END_DATE_OUT", SystemTool.getInstance().getDate());
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
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
		if (null != single_type && single_type.equals("SINGLE")) {
			String[] singles = singleName.split(";");
			this.setTitle("单病种费用分割");
			for (int i = 0; i < singles.length; i++) {
				callFunction("UI|" + singles[i] + "|setVisible", false);
			}
			callFunction("UI|tPanel_6|setVisible", false);
			callFunction("UI|tPanel_6|setEnabled", false);
			callFunction("UI|tPanel_13|setVisible", true);
			callFunction("UI|tPanel_13|setEnabled", true);
			//病种上传方式显示
			callFunction("UI|LAB_SINGLE_UPLOAD_TYPE|setVisible", true);
			callFunction("UI|SINGLE_UPLOAD_TYPE|setVisible", true);
			//病种付费退出原因不显示
			callFunction("UI|LAB_QUIT_REMARK|setVisible", false);
			callFunction("UI|QUIT_REMARK|setVisible", false);
		} else {
			// 病历首页页签不显示操作按钮
			callFunction("UI|OP_BTN|setVisible", false);
			callFunction("UI|MRO_BTN|setVisible", false);
			//病种上传方式不显示
			callFunction("UI|LAB_SINGLE_UPLOAD_TYPE|setVisible", false);
			callFunction("UI|SINGLE_UPLOAD_TYPE|setVisible", false);
			//病种付费退出原因显示
			callFunction("UI|LAB_QUIT_REMARK|setVisible", true);
			callFunction("UI|QUIT_REMARK|setVisible", true);
			
		}
		 //总量 列触发
        this.addEventListener("NEW_TABLE->" + TTableEvent.CHANGE_VALUE, this,
                              "onTableChangeValue");
        //获得病种付费退出原因
        getQuitRemark();
        //获得病种上传方式
        getSingleUploadType();
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
	 * 费用分割过程中按钮置灰
	 * 
	 * @param enAble
	 *            boolean
	 */
	private void feePartitionEnable(boolean enAble) {
		callFunction("UI|save|setEnabled", enAble);
		callFunction("UI|new|setEnabled", enAble);
		callFunction("UI|delete|setEnabled", enAble);
		callFunction("UI|query|setEnabled", enAble);
		callFunction("UI|changeInfo|setEnabled", enAble);
		callFunction("UI|apply|setEnabled", enAble);
		callFunction("UI|onSave|setEnabled", enAble);
		for (int i = 1; i < 11; i++) {
			callFunction("UI|NEW_RDO_" + i + "|setEnabled", enAble);
		}
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
		// TParm result =
		// INSIbsUpLoadTool.getInstance().querySumIbsUpLoad(parm);
		TParm result = new TParm(TJDODBTool.getInstance().select(upLoadSql));
		if (result.getErrCode() < 0) {
			return;
		}
//		System.out.println("result:"+result);
//		System.out.println("result.getCount(CHARGE_DATE):"+result.getCount("CHARGE_DATE"));
		if (result.getDouble("TOTAL_AMT",0)==0) {
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
		if (this.getValueInt("INS_CROWD_TYPE") == 1||
		   (this.getValueInt("INS_CROWD_TYPE") == 2
		    &&this.getValue("LOCAL_FLG").equals("N"))) { // 本地城职 、异地城职、异地城居
			// System.out.println("城职増付入参"+splitCParm);
			// 城职累计增付
			splitCParm = INSTJTool.getInstance().DataDown_sp1_C(splitParm);
			// System.out.println("城职増付出参"+splitCParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2
				&&this.getValue("LOCAL_FLG").equals("Y")) {//本地城居
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
			String sql = "SELECT CTZ1_CODE FROM INS_IBS WHERE YEAR_MON='"
					+ commParm.getValue("YEAR_MON") + "' AND CASE_NO='"
					+ this.getValueString("CASE_NO") + "'";
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
				if (this.getValueInt("INS_CROWD_TYPE") == 1||
				   (this.getValueInt("INS_CROWD_TYPE") == 2
					&&this.getValue("LOCAL_FLG").equals("N")))  { //本地城职 、异地城职、异地城居
					// System.out.println("城职医保分割前数据入参"+parm);
					// 住院费用明细分割
					splitParm = INSTJTool.getInstance().DataDown_sp1_B(parm);

				} else if (this.getValueInt("INS_CROWD_TYPE") == 2
						   &&this.getValue("LOCAL_FLG").equals("Y")) {//本地城居
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
		newParm.setData("TYPE", single_type); // 判断执行类型 ：SINGLE:单病种操作
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
	 * 获得表格对象
	 * 
	 * @param name
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String name) {
		return (TTable) this.getComponent(name);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (this.getRadioButton("IN_TYPE").isSelected()) {// 入院时间查询
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
		}else if (this.getRadioButton("OUT_TYPE").isSelected()) {//出院时间查询
			if (null == this.getValue("START_DATE_OUT")
					|| this.getValue("START_DATE_OUT").toString().length() <= 0) {
				onCheck("START_DATE_OUT", "出院开始时间不可以为空");
				return;
			}
			if (null == this.getValue("END_DATE_OUT")
					|| this.getValue("END_DATE_OUT").toString().length() <= 0) {
				onCheck("END_DATE_OUT", "出院结束时间不可以为空");
				return;
			}

			if (((Timestamp) this.getValue("START_DATE_OUT")).after(((Timestamp) this
					.getValue("END_DATE_OUT")))) {
				this.messageBox("开始时间不可以大于结束时间");
				return;
			}
		}
		TParm parm = new TParm();
		// 21.城职普通 22.城职门特 23.城居门特 SYS_CTZ 表中医保数据
		if (this.getValueInt("INS_CROWD_TYPE") == 1) { // 城职
			parm.setData("INS_CROWD_TYPE", "1");
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) { // 城居
			parm.setData("INS_CROWD_TYPE", "2");
		}else if (this.getValue("INS_CROWD_TYPE").equals("")){// 城职、城居
			parm.setData("INS_CROWD_TYPE", "");
		}
				
		if (null != this.getValue("MR_NO")
				&& this.getValue("MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("MR_NO"));
		}
		//==============pangben 2012-6-18 start
		if (null != this.getValue("CASE_NO")
				&& this.getValue("CASE_NO").toString().length() > 0) {
			parm.setData("CASE_NO", this.getValue("CASE_NO"));
		}
		//==============pangben 2012-6-18 stop
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域代码
		parm.setData("START_DATE", df.format(this.getValue("START_DATE"))); // 入院时间
		parm.setData("END_DATE", df.format(this.getValue("END_DATE"))); // 入院结束时间
		parm.setData("START_DATE_OUT", df.format(this.getValue("START_DATE_OUT"))); // 出院时间
		parm.setData("END_DATE_OUT", df.format(this.getValue("END_DATE_OUT"))); // 出院结束时间
		parm.setData("IN_STATUS", this.getValue("IN_STATUS"));//医保状态
		parm.setData("PAT_DEPT_CODE", this.getValue("PAT_DEPT_CODE"));//科室
		parm.setData("LOCAL_FLG", this.getValue("LOCAL_FLG"));//区域（本地或者异地）
		if (this.getRadioButton("IN_TYPE").isSelected()) {
			parm.setData("QUERY_TYPE", "IN");
		}else if (this.getRadioButton("OUT_TYPE").isSelected()) {
			parm.setData("QUERY_TYPE", "OUT");
		}		
		parm.setData("TYPE", single_type); // TYPE:SINGLE 单病种操作
		TParm result = INSADMConfirmTool.getInstance().INS_Adm_Seq(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			tableInfo.removeRowAll();
			return;
		}
		int count = result.getCount();	
		String indateall ="";
		String dsdateall="";
		for (int i = 0; i < count; i++) {
			if(result.getValue("DS_DATE",i).length()==0)
				result.setData("DAYS",i,"");
			else{
			String indate = result.getValue("IN_DATE",i).replace("/", "");	
			String dsdate = result.getValue("DS_DATE",i).replace("/", "");
			indateall= indate.substring(0,4)+"-"+indate.substring(4,6)+"-"+indate.substring(6,8);
			dsdateall= dsdate.substring(0,4)+"-"+dsdate.substring(4,6)+"-"+dsdate.substring(6,8);
//			System.out.println("indateall========="+indateall);
//			System.out.println("dsdateall========="+dsdateall);
		int days = StringTool.getDateDiffer(
				StringTool.getTimestamp(dsdateall+" 00:00:00", "yyyy-MM-dd HH:mm:ss"),
				StringTool.getTimestamp(indateall+" 00:00:00", "yyyy-MM-dd HH:mm:ss"));
		int rollDate = days == 0 ? 1 : days;
//		System.out.println("days========="+rollDate);
		result.setData("DAYS",i,rollDate);
		}
		//总费用
    	String sql2=" SELECT SUM(D.TOT_AMT) TOT_AMT " +
    	            " FROM ADM_INP A,IBS_ORDM M,IBS_ORDD D " +
    	            " WHERE A.CASE_NO=D.CASE_NO " +
    	            " AND M.CASE_NO = D.CASE_NO " +
    	            " AND A.CASE_NO = '"+result.getValue("CASE_NO",i)+"'"+
    	            " AND M.CASE_NO_SEQ =D.CASE_NO_SEQ" +
    	            " AND A.CANCEL_FLG = 'N'";
    	TParm result2 = new TParm(TJDODBTool.getInstance().select(sql2));
    	if (result2.getCount()>0) {
    		result.setData("TOT_AMT",i,result2.getDouble("TOT_AMT",0));		
		}
    	//获得单病种编码和名称
    	if (null != single_type && single_type.equals("SINGLE")) {
    		String sql3=" SELECT A.SDISEASE_CODE,B.CHN_DESC AS SDISEASE_DESC"+
    			        " FROM INS_ADM_CONFIRM A,SYS_DICTIONARY B"+
    			        " WHERE A.CASE_NO = '"+result.getValue("CASE_NO",i)+"'"+
    			        " AND A.SDISEASE_CODE=B.ID"+
    			        " AND B.GROUP_ID = 'SIN_DISEASE'";
        TParm result3 = new TParm(TJDODBTool.getInstance().select(sql3));
         if (result3.getCount()>0) {
	         result.setData("SDISEASE_CODE",i,result3.getValue("SDISEASE_CODE",0));	
	         result.setData("SDISEASE_DESC",i,result3.getValue("SDISEASE_DESC",0));	
    	}
      }
    }
		tableInfo.setParmValue(result);	
		this.setValue("COUNT",count);
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
	 * 转病患基本资料
	 */
	public void onQueryInfo() {
		onExe("M");
	}

	/**
	 * 转申报
	 */
	public void onApply() {
		//===========pangben 2012-7-18 start 没有出院日期不可以执行转申报操作
		if (null==this.getValue("DS_DATE") || this.getValue("DS_DATE").toString().length()<=0) {
			this.messageBox("没有获得出院日期不可以执行");
			return;
		}
		//===========pangben 2012-7-18 stop
		onExe("H");
	}

	/**
	 * 执行转病患转申报操作
	 * 
	 * @param type
	 *            （M :转病患信息操作 ,H :转申报操作 A : 自动）
	 */
	private void onExe(String type) {
		TParm parm = getTableSeleted();
		//System.out.println("parm:"+parm);
		if (null == parm) {
			return;
		}
		parm.setData("TYPE", type); // M :转病患信息操作 ,H :转申报操作 A : 自动
		parm.setData("REGION_CODE", Operator.getRegion()); // 医院代码
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		String endDate = "";
		String tempDate = df1.format(SystemTool.getInstance().getDate());
		
		// 判断是否跨年操作处理 获得结束时间
		String startDate = parm.getValue("START_DATE");
		if(this.getValue("LOCAL_FLG").equals("Y")){		
		if(startDate.length() > 8)
			startDate =startDate.substring(0,8); 
		if (Integer.parseInt(startDate) < Integer.parseInt(tempDate + "0101")) {
			endDate = ""+ (Integer.parseInt(tempDate) -1) + "1231";
		} else {
			endDate = df.format(SystemTool.getInstance().getDate());
		}
	}else {
		    endDate = df.format(SystemTool.getInstance().getDate());
	}
		parm.setData("END_DATE", endDate); // 系统时间
		parm.setData("START_DATE", startDate); // 系统时间
		parm.setData("SINGLE_TYPE", single_type);// TYPE:SINGLE 单病种操作
		//System.out.println("parm:"+parm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ins.INSBalanceAction", "onExe", parm);
		if (result.getErrCode() < 0) {
			this.messageBox("执行失败:"+result.getErrText());
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
		if (!this.emptyTextCheck("LOCAL_FLG")) {
			return;
		}
		String sql = "SELECT SOURCE_CODE,ADM_PRJ FROM INS_IBS WHERE CASE_NO='"
				+ parm.getValue("CASE_NO") + "' AND YEAR_MON='"
				+ parm.getValue("YEAR_MON") + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("查询数据有问题");
			return;
		}
		if (result.getCount("SOURCE_CODE") <= 0
				|| null == result.getValue("SOURCE_CODE", 0)
				|| result.getValue("SOURCE_CODE", 0).length() <= 0) {

			this.messageBox("出院情况没有保存,不可以执行分割操作");
			tabbedPane.setSelectedIndex(1);
			return;
		}
		if (result.getCount("ADM_PRJ") <= 0
				|| null == result.getValue("ADM_PRJ", 0)
				|| result.getValue("ADM_PRJ", 0).length() <= 0) {
			this.messageBox("就医项目没有保存,请执行转病患操作");
			tabbedPane.setSelectedIndex(1);
			return;
		}
		//判断是否存在没有完成的账单
		String sql1 = " SELECT A.CASE_NO FROM IBS_ORDD A,ADM_INP B"+
		              " WHERE A.CASE_NO='"+ parm.getValue("CASE_NO") + "'"+
		              " AND A.CASE_NO = B.CASE_NO"+
		              " AND A.TOT_AMT != 0"+
		              " AND A.BILL_NO IS NULL"+
		              " AND B.DS_DATE IS NOT NULL";
	    TParm result1 = new TParm(TJDODBTool.getInstance().select(sql1));
	    if (result1.getCount("CASE_NO")>0) {
		this.messageBox("存在未完成的账单,请重新出院");
		return;
	    }	
		if (!this.CheckTotAmt()) {
		} else {
			feePartitionEnable(false);
			updateRun(); // 准备上传医保
			update1(); // 累计增付
			feePartitionEnable(true);
		}

	}

	/**
	 * 费用分割执行以后数据比较
	 * 
	 * @return boolean
	 */
	public boolean CheckTotAmt() {
			TParm parm = getTableSeleted();
			Timestamp sysTime = SystemTool.getInstance().getDate();			
			if (null != parm) {
				 String sql =" SELECT SUM(A.TOTAL_AMT) AS TOTAL_AMT" +
	     		" FROM INS_IBS_UPLOAD A,INS_ADM_CONFIRM B" +
	     		" WHERE A.ADM_SEQ = B.ADM_SEQ" +
	     		" AND B.CASE_NO = '"+ parm.getValue("CASE_NO") + "'" +
	     		" AND B.ADM_SEQ = '"+ parm.getValue("ADM_SEQ") + "'" +
	     		" AND A.NHI_ORDER_CODE  NOT LIKE '***%'";       
		TParm ibsUpLoadParm = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("ibsUpLoadParm===" + ibsUpLoadParm);
		if (ibsUpLoadParm.getErrCode() < 0) {
			return false;
		}
		// 判断是否跨年操作处理 获得结束时间
		DateFormat df1 = new SimpleDateFormat("yyyy");
		DateFormat df = new SimpleDateFormat("yyyyMMdd");
		String tempDate = df1.format(sysTime);//当前年份	
		String startDate = parm.getValue("START_DATE");//开始时间
		String endDate = "";//结束时间
		if(this.getValue("LOCAL_FLG").equals("Y")){	
		if (Integer.parseInt(startDate) < Integer.parseInt(tempDate + "0101")) 
			endDate = ""+ (Integer.parseInt(tempDate) -1) + "1231"+"235959";
		else 
			endDate = df.format(sysTime)+"235959";
		}
		else{
			endDate = df.format(sysTime)+"235959";
		}
		startDate =startDate + "000000";
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
	
//			TParm ibsUpLoadParm = INSIbsUpLoadTool.getInstance()
//					.queryCheckSumIbsUpLoad(parm);		
//			if (ibsUpLoadParm.getErrCode() < 0) {
//				return false;
//			}
//			TParm ibsOrderParm = INSIbsOrderTool.getInstance()
//					.queryCheckSumIbsOrder(parm);
//			if (ibsOrderParm.getErrCode() < 0) {
//				return false;
//			}
//			if (ibsUpLoadParm.getDouble("TOTAL_AMT", 0) == ibsOrderParm
//					.getDouble("TOTAL_AMT", 0)) {
//				return true;
//			} else {
//				if (this.messageBox("信息", "此病患收费金额与分割后金额不符,是否继续", 2) == 0) {
//					return true;
//				}
//				return false;
//			}
		}
		return true;
	}

	private void onSplitOld(boolean flg) {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，05床位费，06材料费，07其他费，08全血费，09成分血费
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("OLD_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"OLD_RDO_" + i).getName());
					break;
				}
			}
		}
		TParm result = INSIbsOrderTool.getInstance().queryOldSplit(parm);
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
	 * 费用分割前数据
	 */
	public void onSplitOld() {
		onSplitOld(true);

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
		// 统计代码查询：01 药品费，02 检查费，03 治疗费，04手术费，05床位费，06材料费，07其他费，08全血费，09成分血费
		for (int i = 1; i <= 10; i++) {
			if (this.getRadioButton("NEW_RDO_" + i).isSelected()) {
				if (i != 1) {
					parm.setData("NHI_ORD_CLASS_CODE", this.getRadioButton(
							"NEW_RDO_" + i).getName());
					break;
				}
			}
		}
		TParm upLoadParmOne = INSIbsUpLoadTool.getInstance()
				.queryNewSplit(parm);
		if (upLoadParmOne.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		TParm upLoadParmTwo = INSIbsUpLoadTool.getInstance()
				.queryNewSplitUpLoad(parm);
		if (upLoadParmTwo.getErrCode() < 0) {
			this.messageBox("E0005"); // 执行失败
			return;
		}
		if (flg) {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				// this.messageBox("没有查询的数据");
				callFunction("UI|upload|setEnabled", false); // 没有数据不可以执行分割操作
				return;
			}
		} else {
			if (upLoadParmOne.getCount() == 0 && upLoadParmTwo.getCount() == 0) {
				newTable.acceptText();
				newTable.setDSValue();
				newTable.removeRowAll();
				callFunction("UI|upload|setEnabled", false); // 没有数据不可以执行分割操作
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
		callFunction("UI|upload|setEnabled", true);
	}

	/**
	 * 病患信息表格单击事件
	 */
	public void onTableClick() {
		onSplitOld(false);
		onSplitNew(false);
		int row = tableInfo.getSelectedRow();
		TParm parm = tableInfo.getParmValue().getRow(row);
		this.setValueForParm(pageHead, parm);
		parm.setData("YEAR_MON", parm.getValue("IN_DATE").replace("/", "")
				.substring(0, 6)); // 期号
		this.setValue("LOCAL_FLG", parm.getValue("LOCAL_FLG")); //区域
		this.setValue("IN_STATUS", parm.getValue("IN_STATUS")); //结账状态
		this.setValue("PAT_DEPT_CODE", parm.getValue("DEPT_CODE")); //科室
		this.setValue("INS_CROWD_TYPE", parm.getValue("INS_CROWD_TYPE")); //人群类别
		TParm result = INSIbsTool.getInstance().queryIbsSum(parm); // 查询数据给界面赋值
		nhiCode = result.getValue("NHI_CODE", 0);
		setSumValue(result, parm);
	}

	/**
	 * 病案号文本框回车事件
	 */
	public void onMrNo() {
		// TParm parm = getTableSeleted();
		// if (null == parm) {
		// return;
		// }
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!");
			return;
		}
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("MR_NO", pat.getMrNo());
		TParm parm = new TParm();
		//=============pangben 2012-6-18 start 添加住院信息校验 判断case_NO
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
		parm.setData("FLG","Y");
		if (result.getCount("MR_NO") > 1) {
			result = (TParm) this.openDialog(
					"%ROOT%\\config\\ins\\INSAdmNClose.x", parm);
			this.setValue("CASE_NO", result.getValue("CASE_NO"));
		} else {
			this.setValue("CASE_NO", result.getValue("CASE_NO", 0));
		}
		//=============pangben 2012-6-18 STOP
		// TParm result = INSIbsTool.getInstance().queryIbsSum(parm);//
		// 查询数据给界面赋值
		// setSumValue(result, parm);
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
			// result.setData("ADM_SEQ",parm.getValue("ADM_SEQ",0));//就诊顺序号 主键
			// parm.setRowData(parm.getCount(),result,-1);
			// parm.setCount(parm.getCount()+1);
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
	 * 结算操作
	 */
	public void onSettlement() {
		TParm parm = getTableSeleted();
		if (null == parm) {
			return;
		}
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		if (!this.emptyTextCheck("LOCAL_FLG")) {
			return;
		}		
		if (null == this.getValue("DS_DATE")
				|| this.getValue("DS_DATE").toString().length() <= 0) {
			this.messageBox("出院时间不可以为空");
			this.grabFocus("DS_DATE");
			return;
		}
		// 单病种操作校验数据
		if (null != single_type && single_type.equals("SINGLE")) {

			if (null == this.getValue("ADM_DAYS")
					|| this.getValue("ADM_DAYS").toString().length() <= 0) {
				this.messageBox("住院天数不能为空");
				this.grabFocus("ADM_DAYS");
				tabbedPane.setSelectedIndex(1);
				return;
			}
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion()); // 区域
		parm.setData("REALOWN_RATE", this.getValue("REALOWN_RATE")); // 本次实际自负比例
		parm.setData("INS_CROWD_TYPE", this.getValueInt("INS_CROWD_TYPE")); // 人群类别
		parm.setData("LOCAL_FLG", this.getValue("LOCAL_FLG")); // 异地标记
		parm.setData("TYPE", single_type); // type:SINGLE 单病种操作使用
		

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
			return;
		}
		this.messageBox("P0005"); // 执行成功
		result = INSIbsTool.getInstance().queryIbsSum(parm); // 查询数据给界面赋值
		setSumValue(result, parm);
		// isEnable(pageThree, false);
		tabbedPane.setSelectedIndex(2);
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
		int days = StringTool.getDateDiffer((Timestamp) this
				.getValue("DS_DATE"), (Timestamp) this.getValue("IN_DATE"));
		int rollDate = days == 0 ? 1 : days;
		this.setValue("ADM_DAYS", rollDate);
		this.setValue("DIAG_DESC2", getDiagDesc(parm.getValue("CASE_NO")));
		// 单病种操作执行
		if (null != single_type && single_type.equals("SINGLE")) {
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
		clearValue("SUM_AMT;NEW_SUM_AMT");
		callFunction("UI|upload|setEnabled", false);
		callFunction("UI|onSave|setEnabled", false);
		this.setValue("LOCAL_FLG", ""); //区域
		this.setValue("IN_STATUS", ""); //医保状态
		this.setValue("PAT_DEPT_CODE", ""); //科室
		this.setValue("COUNT", 0); //合计人数
//		this.setValue("START_DATE", SystemTool.getInstance().getDate()); // 入院开始时间
//		this.setValue("END_DATE", SystemTool.getInstance().getDate()); // 入院结束时间
		//初始化数据
        onExeEnable(false);
//        this.setValue("START_DATE_OUT", SystemTool.getInstance().getDate());
//        this.setValue("END_DATE_OUT", SystemTool.getInstance().getDate());
		 this.getRadioButton("OUT_TYPE").setSelected(true);
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
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 *            TTable
	 */
	public void addListener1(final TTable table) {
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
				TParm tableData = tableInfo.getParmValue();
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
				String tblColumnName = tableInfo.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam1(vct, new TParm(), strNames);

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
	 * vectory转成param
	 * 
	 * @param vectorTable
	 *            Vector
	 * @param parmTable
	 *            TParm
	 * @param columnNames
	 *            String
	 */
	private void cloneVectoryParam1(Vector vectorTable, TParm parmTable,
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
		tableInfo.setParmValue(parmTable);
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
	 * 通过医保出参查询本地人员类别代码
	 * 
	 * @param crowType
	 *            int
	 * @param exeParm
	 *            TParm
	 * @return String
	 */
	private String getCtzCode(int crowType, TParm exeParm) {
		String ctz_code = "";
		String sql = "";
		if (crowType == 1) {// 1 城职 2.城居
			ctz_code = exeParm.getValue("CTZ_CODE");
			sql = " AND CTZ_CODE IN('11','12','13')";
		} else if (crowType == 2) {// 1 城职 2.城居
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND CTZ_CODE IN('21','22','23')";
		}
		StringBuffer messSql = new StringBuffer();
		messSql.append(
				"SELECT CTZ_CODE,NHI_NO FROM SYS_CTZ WHERE NHI_NO='" + ctz_code
						+ "' AND NHI_CTZ_FLG='Y' ").append(sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
				messSql.toString()));
		if (ctzParm.getErrCode() < 0) {
			return "";
		}
		return ctzParm.getValue("CTZ_CODE", 0);
	}
	/**
	 * 单选按钮事件
	 */
	public void onExeType() {
		if (this.getRadioButton("IN_TYPE").isSelected()) {// 入院时间查询
			onExeEnable(true);
		}else if (this.getRadioButton("OUT_TYPE").isSelected()) {//出院时间查询
			onExeEnable(false);	
		}
	}	
	private void onExeEnable(boolean flg) {
		 callFunction("UI|START_DATE|setEnabled", flg);//起始时间
	     callFunction("UI|END_DATE|setEnabled", flg);//截止时间
	     callFunction("UI|START_DATE_OUT|setEnabled", flg ? false : true);//出院开始时间
	     callFunction("UI|END_DATE_OUT|setEnabled", flg ? false : true);//出院结束时间  
	     
	}	
}
