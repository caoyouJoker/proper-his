package com.javahis.ui.ins;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.ins.INSMTRegisterTool;
import jdo.ins.INSTJReg;
import jdo.ins.INSTJTool;
import jdo.ins.InsManager;
import jdo.opd.DiagRecTool;
import jdo.reg.PatAdmTool;
import jdo.reg.Reg;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * 
 * Title: 门特登记 \审核
 * 
 * Description:门特登记 \审核:门特登记开具或下载 \审核查询
 * 
 * Copyright: ProperSoft (c) 2011
 * 
 * @author pangben 2012-1-12
 * @version 2.0
 */
public class INSMTRegisterControl extends TControl {
	private TTable table;// 门特病人列表
	private TTable tableAudit;// 审核列表
	private TParm insParm;// 读医保卡出参
	private TTabbedPane tabPanel;// 页签
	private String cardNo;// 医保卡号
	private TParm regionParm;// 区域代码
	// DateFormat df1 = new SimpleDateFormat("yyyyMMddhhmmss");
	private int selectRow = -1;// 选中数据
	// 病患对象
	private Pat pat;
	// 挂号对象
	private Reg reg;
	// 就诊号
	private String caseNo;
	private String statusType;// 本地审核状态 下载时使用
	public static final String SEPARATED = "@";// 解析结构化病历

	/**
	 * 
	 * @param str
	 * @param suff
	 * @return
	 */
	private String[] getArray(String str, String suff) {
		String arr[] = str.split(suff);

		return arr;

	}

	// 第二个页签数据
	private String insMZMTRegister = "MR_NO;PAT_NAME;REG_DATE;CLINICROOM_NO;REALDR_CODE;CASE_NO;REGISTER_NO;OWN_NO;CASE_NO;BRANCH_CODE;SEX_CODE;BIRTH_DATE;PAT_AGE;PAT_TYPE;COMPANY_NO;COMPANY_CODE;COMPANY_DESC;"
			+ "MED_HELP_COMPANY;ENTERPRISE_TYPE;REGISTER_SERIAL_NO;PAY_KIND;DISEASE_CODE;DIAG_HOSP_CODE;"
			+ "REGISTER_DR_CODE1;REGISTER_DR_CODE2;PAT_PHONE;PAT_ZIP_CODE;"
			+ "PAT_ADDRESS;REGISTER_TYPE;DISEASE_HISTORY;ASSISTANT_EXAMINE;DIAG_CODE;"
			+ "HOSP_CODE_LEVEL1;HOSP_CODE_LEVEL2;HOSP_CODE_LEVEL3;HOSP_CODE_LEVEL3_PRO;"
			+ "DRUGSTORE_CODE;EFFECT_FLG;DATA_SOURCE;REGISTER_DATE;"
			+ "REGISTER_RESPONSIBLE;REGISTER_USER;STATUS_TYPE;AUDIT_CENTER_USER;"
			+ "INS_CROWD_TYPE;UNPASS_REASON;BEGIN_DATE;DIAG_DESC";
	// 第三个页签
	private String threePage = "REGISTER_NO1;PAT_NAME1;REGISTER_NO1;BEGIN_DATE1;SEX_CODE1;DISEASE_CODE1;REGISTER_USER1;"
			+ "REGISTER_SERIAL_NO;HOSP_CODE_LEVEL3_1;HOSP_CODE_LEVEL2_1;HOSP_CODE_LEVEL1_1;HOSP_CODE_LEVEL3_PRO1;DRUGSTORE_CODE1";

	// 医保新添加数据
	private String newMzMt = "MED_HISTORY;ASSSISTANT_STUFF;JUDGE_END;JUDGE_CONTER_I;THE_JUDGE_START_DATE;"
			+ "THE_JUDGE_END_DATE";
	private String pageOneNew = "DISEASE_HISTORY;ASSISTANT_EXAMINE;MED_HISTORY;ASSSISTANT_STUFF";
	private TParm userParm;// 获得医保医生数据

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initParm();
	}

	/**
	 * 初始数据
	 */
	private void initParm() {
		// 查询开始时间
		this.callFunction("UI|START_DATE|setValue", SystemTool.getInstance()
				.getDate());
		// 结束时间
		this.callFunction("UI|END_DATE|setValue", SystemTool.getInstance()
				.getDate());
		// 开始时间
		this.callFunction("UI|BEGIN_DATE|setValue", SystemTool.getInstance()
				.getDate());
		this.setValue("INS_CROWD_TYPE", 1);// 人群类别
		table = (TTable) this.getComponent("TABLE");// 门特病人列表
		tableAudit = (TTable) this.getComponent("TABLE_AUDIT");// 审核列表
		// 只有text有这个方法，调用ICD10弹出框
		callFunction("UI|DIAG_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSICDPopup.x");
		// textfield接受回传值
		callFunction("UI|DIAG_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// 审核数据复选框事件
		tableAudit.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBox");
		tabPanel = (TTabbedPane) this.getComponent("TTABPANEL");// 页签
		initParmComponent();
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion());// 获得医保区域代码
		String sql = " SELECT USER_ID,DR_QUALIFY_CODE "
				+ "FROM SYS_OPERATOR WHERE DR_QUALIFY_CODE IS NOT NULL OR DR_QUALIFY_CODE <>'' ORDER BY USER_ID";
		userParm = new TParm(TJDODBTool.getInstance().select(sql)); // 获得所有医保医生
		this.setValue("BEGIN_DATE1",SystemTool.getInstance().getDate());
	}

	private void initParmComponent() {
		this.setValue("DIAG_HOSP_CODE", "000551");
		this.setValue("REGISTER_DR_CODE1", Operator.getID());// 门特医生1
		this.setValue("REGISTER_USER", Operator.getID());// 经办人
		this.setValue("REGISTER_RESPONSIBLE", Operator.getID());// 负责人
	}

	/**
	 * 开具保存
	 * 
	 * @return boolean
	 */
	public void onCommandButSave() {
		// 获取封锁信息
		if (null == cardNo || cardNo.length() <= 0
				&& this.getValueString("OWN_NO").length() == 0) {
			this.messageBox("请先读卡");
			return;
		}
		if (!checkOut()) {
			return;
		}
		String[] name = { "REGISTER_USER", "REGISTER_DR_CODE1",
				"REGISTER_DR_CODE2", "REGISTER_RESPONSIBLE",
				"HOSP_CODE_LEVEL1", "HOSP_CODE_LEVEL2", "HOSP_CODE_LEVEL3",
				"HOSP_CODE_LEVEL3_PRO", "DRUGSTORE_CODE" };
		for (int i = 0; i < name.length; i++) {
			if (this.getValueString(name[i]).length() <= 0) {
				this.messageBox(getTTextFormat(name[i]).getName() + "不能为空");
				this.grabFocus(name[i]);
				return;
			}
		}
		// 病种编码
		// if (!this.emptyTextCheck("DISEASE_CODE")) {
		// tabPanel.setSelectedIndex(1);// 第二个页签显示
		// return;
		// }
		getMrNoOne(false);
		if (null == pat || null == pat.getMrNo()) {
			return;
		}
		TParm parm = new TParm();
		name = insMZMTRegister.split(";");// 获得第二个页签的数据
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i], this.getValueString(name[i]));
		}
		// =====================pangben 2012-4-10 start
		// name=pageOneNew.split(";");
		// for (int i = 0; i < name.length; i++) {
		// parm.setData(name[i], this.getValueString(name[i]));
		// };
		parm.setData("MED_HISTORY", this.getText("MED_HISTORY"));// 既往史(糖尿病)
		parm.setData("ASSSISTANT_STUFF", this.getText("ASSSISTANT_STUFF"));// 辅助材料(糖尿病)
		parm.setData("DISEASE_HISTORY", this.getText("DISEASE_HISTORY"));// 既往史(糖尿病)
		parm.setData("ASSISTANT_EXAMINE", this.getText("ASSISTANT_EXAMINE"));// 辅助材料(糖尿病)
		// =====================pangben 2012-4-10 stop
		parm.setData("BEGIN_DATE", this.getValueString("BEGIN_DATE").replace(
				"-", "").substring(0, 8));// 开始时间
		parm.setData("END_DATE", this.getValueString("END_DATE").replace("-",
				"").substring(0, 8));// 结束时间
		parm.setData("CARD_NO", insParm.getValue("CARD_NO").toString());// 卡号
		parm.setData("PERSONAL_NO", insParm.getValue("PERSONAL_NO").toString());// 个人编码
		parm.setData("OWN_NO", insParm.getValue("PERSONAL_NO").toString());// 个人编码
		parm.setData("REGION_CODE", Operator.getRegion());// 区域代码
		parm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保区域代码
		parm.setData("DISEASE_CODE", insParm.getValue("DISEASE_CODE"));// 门特病种
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGISTER_DR_CODE1", getInsId(this
				.getValueString("REGISTER_DR_CODE1")));
		parm.setData("REGISTER_DR_CODE2", getInsId(this
				.getValueString("REGISTER_DR_CODE2")));
		parm.setData("REGISTER_USER", getInsId(this
				.getValueString("REGISTER_USER")));
		parm.setData("REGISTER_RESPONSIBLE", getInsId(this
				.getValueString("REGISTER_RESPONSIBLE")));
		// System.out.println("INSPARM111111::" + parm);
		TParm result = new TParm(INSTJReg.getInstance().onCommandButSave(
				parm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());// 执行失败
			return;
		}
		// ==========pangben 2012-4-9 start
		if (null != result.getValue("message")
				&& result.getValue("message").length() > 0) {
			this.messageBox(result.getValue("message"));// 医保更新 添加鉴定时间
		}
		if (null != result.getValue("messageEnd")
				&& result.getValue("messageEnd").length() > 0) {
			this.messageBox(result.getValue("messageEnd"));// 上次鉴定结论LAST_JUDGE_END:0.可认定1不予认定
		}
		// ==========pangben 2012-4-9 stop
		// 输出消息
		if (null != result.getValue("MESSAGE")
				&& result.getValue("MESSAGE").length() > 0) {
			this.messageBox(result.getValue("MESSAGE"));
			// return;
		} else {
			this.messageBox("P0005");// 执行成功
		}
		TParm MTRegisterParm = result.getParm("MTRegisterParm");
		TParm commParm = result.getParm("commParm");// 获得病患共享数据
		if (null != commParm && null != commParm.getValue("PERSONAL_NO")) {
			commParm.setData("MR_NO", MTRegisterParm.getValue("MR_NO"));
			threePageShow(commParm,true);// 第三个页签数据
		}
		// System.out.println("MTRegisterParm:" + MTRegisterParm);
		this.setValueForParm(insMZMTRegister, MTRegisterParm, 0);
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(MTRegisterParm.getValue(userName[i],0)));
		}
		// ==================pangben 2012-4-10 添加医保数据
		String[] newString = newMzMt.split(";");
		for (int i = 0; i < newString.length; i++) {
			this.setValue(newString[i] + "_OUT", MTRegisterParm.getData(
					newString[i], 0));
		}
		this.setValue("THE_JUDGE_TOT_AMT_OUT", MTRegisterParm.getDouble(
				"THE_JUDGE_TOT_AMT", 0));
		this.setValue("THE_JUDGE_APPLY_AMT_OUT", MTRegisterParm.getDouble(
				"THE_JUDGE_APPLY_AMT", 0));
		this.setValue("NUMBER_PAY_AMT_OUT", MTRegisterParm.getDouble(
				"NUMBER_PAY_AMT", 0));
		// ==================pangben 2012-4-10 stop
		this.setValue("REGISTER_NO", MTRegisterParm.getValue("REGISTER_NO", 0));// 门特登记编号
	}
	/**
	 * 获得医保医生编码
	 * @param id
	 * @return
	 */
	private String getInsId(String id) {
		for (int i = 0; i < userParm.getCount(); i++) {
			if (id.equals(userParm.getValue("USER_ID", i))) {
				return userParm.getValue("DR_QUALIFY_CODE", i);
			}
		}
		return "";
	}
	/**
	 * 获得医院医生编码
	 * @param id
	 * @return
	 */
	private String getUserId(String id){
		for (int i = 0; i < userParm.getCount(); i++) {
			if (id.equals(userParm.getValue("DR_QUALIFY_CODE", i))) {
				return userParm.getValue("USER_ID", i);
			}
		}
		return "";
	}
	/**
	 * 读卡操作
	 */
	public void onReadCard() {
		// 人群类别
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = new TParm();
		// parm.setData("MR_NO", pat.getMrNo());
		parm.setData("CARD_TYPE", 5);// 读卡请求类型（1：购卡，2：挂号，3：收费，4：住院，5：门特登记）
		parm.setData("INS_CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));// 人群类别
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null == insParm)
			return;
		// System.out.println("读取医保卡::" + insParm);
		// System.out.println("个人编码：：："+insParm.getValue("PERSONAL_NO"));
		int returnType = insParm.getInt("RETURN_TYPE");// 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			return;
		}
		// 人群类别:1.城职 2.城居br
		if (insParm.getInt("CROWD_TYPE") == 2) {
			if (!insParm.getValue("PAT_NAME").equals(this.getValue("PAT_NAME"))) {// 城居操作判断是否与选择的病患相同
				if (this.messageBox("提示", "卡片中姓名("
						+ insParm.getValue("PAT_NAME") + ")与您当前选择的病人姓名("
						+ this.getValue("PAT_NAME") + ")不符,是否继续", 2) != 0) {
					tabPanel.setSelectedIndex(0);
					return;
				}
			}
		}
		this.setValue("PAT_NAME", insParm.getValue("PAT_NAME"));// 姓名
		this.setValue("SEX_CODE", insParm.getValue("SEX_CODE"));// 性别
		this.setValue("OWN_NO", insParm.getValue("PERSONAL_NO"));// 个人编码
		this.setValue("OWN_NO1", insParm.getValue("PERSONAL_NO"));// 个人编码
		this.setValue("DISEASE_CODE", insParm.getValue("DISEASE_CODE"));// 门特病种编码
		this.setValue("INS_CROWD_TYPE", insParm.getValue("CROWD_TYPE"));
		cardNo = insParm.getValue("CARD_NO");// 医保卡号
		tabPanel.setSelectedIndex(1);// 第二个页签显示
	}

	/**
	 * 查询操作
	 */
	public void onQuery() {
		if (!this.emptyTextCheck("START_DATE,END_DATE")) {// 校验时间
			return;
		}
		TParm parm = new TParm();
		parm.setData("BEGIN_DATE", StringTool.setTime((Timestamp) this
				.getValue("START_DATE"), "00:00:00"));
		parm.setData("END_DATE", StringTool.setTime((Timestamp) this
				.getValue("END_DATE"), "23:59:59"));
		if (this.getValue("SUM_MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("SUM_MR_NO"));// 病案号
		}
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {// 区域
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// 查询数据
		TParm result = INSMTRegisterTool.getInstance().queryINSMTRegister(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有查询的数据");
			table.removeRowAll();
			return;
		}
		table.setParmValue(result);
	}

	/**
	 * 门诊病人列表双击事件
	 */
	public void onDoubleClick() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm parm = table.getParmValue();
		selectRow = row;// 选中行数据
		statusType = parm.getValue("STATUS_TYPE", row);// 本地审核状态
		caseNo = parm.getValue("CASE_NO", row);// 就诊号
		this.setValueForParm(insMZMTRegister, parm, row);
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(parm.getValue(userName[i],0)));
		}
		TParm parmValue = parm.getRow(row);
		getDrUserIdValue(parmValue);
		threePageShow(parmValue,true);
		tabPanel.setSelectedIndex(1);// 第二个页签显示
	}

	/**
	 * 本院区医生代码获得
	 * 
	 * @param parm
	 */
	private void getDrUserIdValue(TParm parm) {
		String[] drCode = { "REGISTER_DR_CODE1", "REGISTER_DR_CODE2",
				"REGISTER_RESPONSIBLE", "REGISTER_USER" };
		for (int i = 0; i < drCode.length; i++) {
			this.setValue(drCode[i], setCommboxValue(parm.getValue(drCode[i])));
		}
	}

	/**
	 * 第三个页签显示数据
	 * 
	 * @param parm
	 * @param row
	 */
	private void threePageShow(TParm parm,boolean flg) {
		// 第三个页签赋值
		this.setValue("MR_NO1", parm.getValue("MR_NO"));// 病患号码
		this.setValue("PAT_NAME1", parm.getValue("PAT_NAME"));// 病患名称
		this.setValue("SEX_CODE1", parm.getValue("SEX_CODE"));// 病患性别
		this.setValue("REGISTER_NO1", parm.getValue("REGISTER_NO"));// 门特登记编号
		this.setValue("OWN_NO1", parm.getValue("PERSONAL_NO"));// 个人编码
		this.setValue("DISEASE_CODE1", parm.getValue("DISEASE_CODE"));// 门特病种编码
		String date=parm.getValue("BEGIN_DATE");
		if (!flg) {
			date=date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
			this.setValue("BEGIN_DATE1",date);// 开始日期
		}else{
			this.setValue("BEGIN_DATE1",parm.getTimestamp("BEGIN_DATE"));// 开始日期
		}
		//Timestamp date=StringTool.getTimestamp(parm.getValue("BEGIN_DATE"), "yyyy/MM/dd");
		
		this.setValue("REGISTER_USER1", setCommboxValue(parm
				.getValue("REGISTER_USER")));// 变更经办人
		this
				.setValue("REGISTER_SERIAL_NO", parm
						.getValue("REGISTER_SERIAL_NO"));// 门特登记序号
		// this.setValue("HOSP_CODE_LEVEL3_1", "111");// 三级医院
		this.setValue("HOSP_CODE_LEVEL3_1", parm.getValue("HOSP_CODE_LEVEL3"));// 三级医院
		this.setValue("HOSP_CODE_LEVEL2_1", parm.getValue("HOSP_CODE_LEVEL2"));// 二级医院
		this.setValue("HOSP_CODE_LEVEL1_1", parm.getValue("HOSP_CODE_LEVEL1"));// 一级医院
		this.setValue("HOSP_CODE_LEVEL3_PRO1", parm
				.getValue("HOSP_CODE_LEVEL3_PRO"));// 三级专科医院
		String[] name = { "MED_HISTORY", "ASSSISTANT_STUFF", "JUDGE_CONTER_I",
				"JUDGE_END" };
		for (int i = 0; i < name.length; i++) {
			if (name[i].equals("JUDGE_END")) {
				String judgeName="";
				if (null!= parm.getValue(name[i])) {
					if (parm.getValue(name[i]).equals("0")) {
						judgeName="可认定";
					}else if (parm.getValue(name[i]).equals("1")){
						judgeName="不可认定";
					}
				}
				this.setText(name[i] + "_OUT",judgeName);
				this.setText(name[i],judgeName);
			}else{
				this.setText(name[i] + "_OUT", parm.getValue(name[i]));
				this.setText(name[i], parm.getValue(name[i]));
			}
			
		}
		this.setValueForParm(insMZMTRegister, parm);
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(parm.getValue(userName[i])));
		}
		this.setValue("DRUGSTORE_CODE1", parm.getValue("DRUGSTORE_CODE"));// 定点零售药店
	}

	/**
	 * 校验，开具下载使用
	 * 
	 * @return
	 */
	private boolean checkOut() {

		if (!this
				.emptyTextCheck("INS_CROWD_TYPE,MR_NO,CASE_NO,PAT_NAME,OWN_NO,DIAG_CODE,PAT_PHONE,PAT_ADDRESS")) {
			return false;
		}
		
		if (null == this.getValue("PAY_KIND")
				|| this.getValue("PAY_KIND").toString().length() <= 0) {// 门特病种不可以为空
			this.messageBox("支付类别不能为空");
			this.grabFocus("PAY_KIND");
			return false;
		}
		if (null == this.getValue("DISEASE_CODE")
				|| this.getValue("DISEASE_CODE").toString().length() <= 0) {// 门特病种不可以为空
			this.messageBox("门特病种不可以为空");
			this.grabFocus("DISEASE_CODE");
			return false;
		}
		if (null == this.getValue("DIAG_HOSP_CODE")
				|| this.getValue("DIAG_HOSP_CODE").toString().length() <= 0) {
			this.messageBox("门特诊断医院");
			this.grabFocus("DIAG_HOSP_CODE");
			return false;
		}
		if (null == this.getValue("REGISTER_DR_CODE1")
				|| this.getValue("REGISTER_DR_CODE1").toString().length() <= 0) {
			this.messageBox("登记医生1不可以为空");
			this.grabFocus("REGISTER_DR_CODE1");
			return false;
		}
		if (null == this.getValue("REGISTER_USER")
				|| this.getValue("REGISTER_USER").toString().length() <= 0) {
			this.messageBox("经办人不可以为空");
			this.grabFocus("REGISTER_USER");
			return false;
		}
		if (null == this.getValue("REGISTER_RESPONSIBLE")
				|| this.getValue("REGISTER_RESPONSIBLE").toString().length() <= 0) {
			this.messageBox("负责人不可以为空");
			this.grabFocus("REGISTER_RESPONSIBLE");
			return false;
		}
		return true;
	}

	/**
	 * 下载
	 */
	public void onLoadDown() {
		// 读卡
		if (null == cardNo || cardNo.length() <= 0
				&& this.getValueString("OWN_NO").length() == 0) {
			this.messageBox("请先读卡");
			return;
		}
		// 门特病种编码
		if (!this.emptyTextCheck("REGISTER_NO,CASE_NO,MR_N0")) {
			return;
		}
		if (null == this.getValue("DISEASE_CODE")
				|| this.getValue("DISEASE_CODE").toString().length() <= 0) {// 门特病种不可以为空
			this.messageBox("门特病种不可以为空");
			this.grabFocus("DISEASE_CODE");
			return;
		}
		if (null == this.getValue("PAY_KIND")
				|| this.getValue("PAY_KIND").toString().length() <= 0) {// 门特病种不可以为空
			this.messageBox("支付类别不可以为空");
			this.grabFocus("PAY_KIND");
			return;
		}
		insParm.setData("DISEASE_CODE", this.getValue("DISEASE_CODE"));// 病种编码
		insParm.setData("REGISTER_NO", this.getValue("REGISTER_NO"));// 门特登记编码
		insParm.setData("MR_NO", this.getValue("MR_NO"));// 病案号
		insParm.setData("CASE_NO", this.getValue("CASE_NO"));// 就诊号
		insParm.setData("REGION_CODE", Operator.getRegion());// 区域
		insParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO", 0));// 医保门诊区域
		insParm.setData("OPT_USER", Operator.getID());// 
		insParm.setData("OPT_TERM", Operator.getIP());// 
		insParm.setData("STATUS_TYPE", statusType==null?"0":statusType);// 本地审核状态
		// TParm parm = new TParm();
		// String[] name = insMZMTRegister.split(";");// 获得第二个页签的数据
		// for (int i = 0; i < name.length; i++) {
		// parm.setData(name[i], this.getValue(name[i]));
		// }
		String[] name = insMZMTRegister.split(";");// 获得第二个页签的数据
		for (int i = 0; i < name.length; i++) {
			insParm.setData(name[i], this.getValueString(name[i]));
		}
		insParm.setData("BEGIN_DATE", StringTool.getString(TypeTool
				.getTimestamp(this.getValue("BEGIN_DATE")), "yyyyMMdd"));// 本地审核状态
		TParm result = new TParm(INSTJReg.getInstance().onLoadDown(
				insParm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			return;
		} else {
			this.messageBox("下载成功");
		}
		getDrUserIdValue(result.getRow(0));
		this.setValueForParm(insMZMTRegister, result, 0);// 数据赋值
		String [] userName={"REGISTER_DR_CODE1","REGISTER_DR_CODE2","REGISTER_USER","REGISTER_RESPONSIBLE"};
		for (int i = 0; i < userName.length; i++) {
			this.setValue(userName[i], getUserId(result.getValue(userName[i],0)));
		}
		this.setValue("REGISTER_NO", result.getValue("REGISTER_NO", 0));// 门特登记编号
		tabPanel.setSelectedIndex(1);// 第二个页签显示
	}

	/**
	 * 共享信息 第三个页签数据
	 */
	public void onShare() {
		// 读卡
		if (null == cardNo || cardNo.length() <= 0
				&& this.getValueString("OWN_NO").length() == 0) {
			this.messageBox("请先读卡");
			return;
		}
		// 门特病种编码,人群类别
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		if (null == this.getValue("DISEASE_CODE")
				|| this.getValue("DISEASE_CODE").toString().length() <= 0) {// 门特病种不可以为空
			this.messageBox("门特病种不可以为空");
			this.grabFocus("DISEASE_CODE");
			return;
		}
		insParm.setData("DISEASE_CODE", this.getValue("DISEASE_CODE"));
		insParm.setData("NHI_REGION_CODE", regionParm.getValue("NHI_NO",0));
		TParm result = new TParm(INSTJReg.getInstance().onShare(
				insParm.getData()));
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
			return;
		} else {
			this.messageBox("P0005");
		}
		threePageShow(result,false);
		
		tabPanel.setSelectedIndex(2);// 第三个页签显示
		// 第三个页签赋值
		// this.
	}

	/**
	 * 审核查询方法
	 */
	public void onQueryAudit() {
		// 人群类别
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("INS_CROWD_TYPE", this.getValue("INS_CROWD_TYPE"));
		if (this.getValueBoolean("UNRDO_AUDIT")) {// 审核状态
			parm.setData("STATUS_TYPE", 0);// 未审核
		} else {
			parm.setData("STATUS_TYPE", 1);// 已审核
		}
		if (this.getValue("SUM_MR_NO").toString().length() > 0) {
			parm.setData("MR_NO", this.getValue("SUM_MR_NO"));// 病案号
		}
		TParm result = INSMTRegisterTool.getInstance().queryAudit(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("查询失败");
			return;
		}
		if (result.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			return;
		}
		tableAudit.setParmValue(result);
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		tableAudit.removeRowAll();// 审核列表
		table.removeRowAll();// 门特病人列表
		insParm = null;// 读医保卡出参
		cardNo = null;// 医保卡号
		selectRow = -1;// 选中数据
		pat = null;// 病患对象
		reg = null;// 挂号对象
		caseNo = null;// 就诊号
		statusType = null;// 本地审核状态 下载时使用
		this.setValue("INS_CROWD_TYPE", "1");// 人群类别默认
		clearValue(insMZMTRegister + ";" + threePage + ";" + newMzMt);
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// 第四个页签全选
		// 开始时间
		this.callFunction("UI|BEGIN_DATE|setValue", SystemTool.getInstance()
				.getDate());
		initParmComponent();
	}

	/**
	 * 通过医保医生编码获得本院的医生编码
	 * 
	 * @param insUserId
	 * @return
	 */
	private String setCommboxValue(String insUserId) {
		for (int i = 0; i < userParm.getCount(); i++) {
			if (insUserId.equals(userParm.getValue("DR_QUALIFY_CODE", i))) {
				return userParm.getValue("USER_ID", i);
			}
		}
		return "";
	}

	/**
	 * 病案号事件
	 */
	public void getMrNo() {
		getMrNoOne(true);
	}

	private void getMrNoOne(boolean flg) {
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));

		if (null == pat || null == pat.getMrNo()) {
			this.messageBox("不存在此病患");
			this.grabFocus("MR_NO");
			return;
		} else {
			
			// modify by huangtt 20160930 EMPI患者查重提示 start
			String mrNo =  PatTool.getInstance().checkMrno(TypeTool.getString(getValue("MR_NO")));
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());

			}
			// modify by huangtt 20160930 EMPI患者查重提示 end
			
			this.setValue("MR_NO", pat.getMrNo());
			this.setValue("PAT_NAME", pat.getName());
			this.setValue("SEX_CODE", pat.getSexCode());
		}
		if (flg) {
			getCaseNo();
		}

	}

	/**
	 * 变更数据 第三个页签操作
	 */
	public void onUpdate() {
		if (selectRow < 0 || null == caseNo) {
			this.messageBox("请选择要变更的数据");
			tabPanel.setSelectedIndex(0);// 第三个页签显示
			return;
		}
		// 校验
		String[] checkValue = { "REGISTER_USER1", "HOSP_CODE_LEVEL3_1",
				"HOSP_CODE_LEVEL2_1", "HOSP_CODE_LEVEL1_1", "DRUGSTORE_CODE1" };
		for (int i = 0; i < checkValue.length; i++) {
			if (null == this.getValue(checkValue[i])
					|| this.getValue(checkValue[i]).toString().length() <= 0) {
				this.messageBox(getTTextFormat(checkValue[i]).getName()
						+ "不能为空");
				this.grabFocus(checkValue[i]);
				return;
			}
		}
		TParm parm = new TParm();
		parm.setData("REGISTER_NO", this.getValue("REGISTER_NO1"));// 门特登记编码
		parm.setData("MR_NO", this.getValue("MR_NO1"));// 病案号
		parm.setData("CASE_NO", caseNo);// 就诊号
		parm.setData("HOSP_CODE_LEVEL3", this.getValue("HOSP_CODE_LEVEL3_1"));// 三级医院
		parm.setData("HOSP_CODE_LEVEL2", this.getValue("HOSP_CODE_LEVEL2_1"));// 二级医院
		parm.setData("HOSP_CODE_LEVEL1", this.getValue("HOSP_CODE_LEVEL1_1"));// 一级医院
		parm.setData("HOSP_CODE_LEVEL3_PRO", "");// 三级专科医院
		parm.setData("DRUGSTORE_CODE", this.getValue("DRUGSTORE_CODE1"));// 定点零售药店
		parm.setData("REGISTER_USER", getInsId(this
				.getValueString("REGISTER_USER1")));// 变更人
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		TParm parms = new TParm();
		parms.addData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		parms.addData("REGISTER_NO", this.getValue("REGISTER_NO1"));
		parms.addData("OWN_NO", this.getValueString("OWN_NO"));
		parms.addData("DISEASE_CODE", this.getValue("DISEASE_CODE1"));
		parms
				.addData("REGISTER_SERIAL_NO", this
						.getValue("REGISTER_SERIAL_NO"));
		parms.addData("REGISTER_BEGIN_DATE", this.getValue("BEGIN_DATE1")
				.toString().replace("/", "").replace("-", "").substring(0,8));
		parms
				.addData("HOSP_CODE_LEVEL1_1", this
						.getValue("HOSP_CODE_LEVEL1_1"));
		parms
				.addData("HOSP_CODE_LEVEL2_1", this
						.getValue("HOSP_CODE_LEVEL2_1"));
		parms
				.addData("HOSP_CODE_LEVEL3_1", this
						.getValue("HOSP_CODE_LEVEL3_1"));
		parms
		.addData("HOSP_CODE_LEVEL3_PRO1", this
				.getValue("HOSP_CODE_LEVEL3_PRO1"));
		parms.addData("DRUGSTORE_CODE1", this.getValue("DRUGSTORE_CODE1"));
		parms.addData("UPDATE_DATE", SystemTool.getInstance().getDate());
		parms.addData("UP_USER",
				getInsId(this.getValueString("REGISTER_USER1")));
		
		parms.setData("PIPELINE", "DataDown_cmts");
		parms.setData("PLOT_TYPE", "Q");
		parms.addData("PARM_COUNT", 13);
		TParm result = InsManager.getInstance().safe(parms);
		if (null == result || result.getInt("PROGRAM_STATE") <0) {
			this.messageBox(result.getValue("PROGRAM_MESSAGE"));
			return;
		}
		result = INSMTRegisterTool.getInstance().updateThreePageData(parm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("变更成功");
		}

	}

	/**
	 * 获得TTextFormat控件
	 * 
	 * @param format
	 * @return
	 */
	private TTextFormat getTTextFormat(String format) {
		return (TTextFormat) this.getComponent(format);
	}

	/**
	 * 审核修改状态操作 0:未审核 1:已审核
	 */
	public void onUpdateStutsType() {
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		onExeUpdateStutsType("1");// 未审核执行以后为已审核状态

	}

	/**
	 * 获得单选控件
	 * 
	 * @param name
	 * @return
	 */
	private TRadioButton getRadioButton(String name) {
		return (TRadioButton) this.getComponent(name);
	}

	/**
	 * 撤销操作
	 */
	public void onConcelStutsType() {
		if (!this.emptyTextCheck("INS_CROWD_TYPE")) {
			return;
		}
		TParm parmValue = tableAudit.getParmValue();// 获得审核数据
		int row = tableAudit.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要执行的数据");
			return;
		}
		if (this.messageBox("提示", "是否执行撤销操作", 2) != 0) {
			return;
		}
		TParm result = new TParm();
		TParm tempParm = parmValue.getRow(row);// 执行的数据
		tempParm.setData("BEGIN_DATE", this.getValue("BEGIN_DATE").toString()
				.replace("-", "").replace("/", ""));
		tempParm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {
			result = INSTJTool.getInstance().DataDown_mts_P(tempParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			result = INSTJTool.getInstance().DataDown_cmts_P(tempParm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.messageBox(result.getErrText());
			return;
		}
		String sql = "DELETE FROM INS_MZMT_REGISTER WHERE CASE_NO='"
				+ tempParm.getValue("CASE_NO") + "'" + " AND REGISTER_NO='"
				+ tempParm.getValue("REGISTER_NO") + "'";
		TParm deleteParm = new TParm(TJDODBTool.getInstance().update(sql));
		if (deleteParm.getErrCode() < 0) {
			this.messageBox("删除失败");
			return;
		}
		this.messageBox("P0005");

		// STATUS_TYPE
	}

	private void onExeUpdateStutsType(String stutsTypeFlg) {
		TParm parmValue = tableAudit.getParmValue();// 获得审核数据
		int row = tableAudit.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要执行的数据");
			return;
		}
		TParm tempParm = parmValue.getRow(row);// 执行的数据
		// 判断是否有选中的数据
		TParm result = new TParm();
		tempParm.setData("BEGIN_DATE", this.getValue("BEGIN_DATE").toString()
				.replace("-", "").replace("/", ""));
		tempParm.setData("HOSP_NHI_NO", regionParm.getValue("NHI_NO", 0));
		// 门特审核下载操作
		if (this.getValueInt("INS_CROWD_TYPE") == 1) {
			result = INSTJTool.getInstance().DataDown_mts_D(tempParm);
		} else if (this.getValueInt("INS_CROWD_TYPE") == 2) {
			result = INSTJTool.getInstance().DataDown_cmts_D(tempParm);
		}
		if (!INSTJTool.getInstance().getErrParm(result)) {
			this.messageBox(result.getErrText());
			return;
		}
		String statusType = result.getValue("STATUS_TYPE");
		String registerCenterUser = result.getValue("REGISTER_CENTER_USER");// 中心经办人
		String unpassReason = result.getValue("UNPASS_REASON");// 不通过原因
		if ("1".equals(statusType))
			this.messageBox("通过审核  审核人：" + registerCenterUser);
		else if ("2".equals(statusType)) {
			this.messageBox("未通过审核 原因:" + unpassReason);
		} else {
			statusType = "0";
			messageBox("中心未审核！");
		}
		tempParm.setData("OPT_USER", Operator.getID());
		tempParm.setData("OPT_TERM", Operator.getIP());
		tempParm.setData("STATUS_TYPE", statusType);// 审核状态 0:未审核 1:已审核
		tempParm.setData("AUDIT_CENTER_USER", registerCenterUser);
		tempParm.setData("UNPASS_REASON", unpassReason);

		result = INSMTRegisterTool.getInstance().updateStutsType(tempParm);
		if (result.getErrCode() < 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
			onQueryAudit();
		}

	}

	// /**
	// * 第四个页签全选
	// */
	// public void onSelectAll() {
	// TParm parmValue = tableAudit.getParmValue();// 获得审核数据
	// if (parmValue.getCount() <= 0) {
	// this.messageBox("没有要执行的数据");
	// return;
	// }
	// boolean flg = false;// 判断是否选中
	// if (this.getValueBoolean("SELECT_ALL")) {
	// flg = true;
	// } else {
	// flg = false;
	// }
	// for (int i = 0; i < parmValue.getCount(); i++) {
	// parmValue.setData("FLG", i, flg);
	// }
	// tableAudit.setParmValue(parmValue);
	// }

	/**
	 * 页签点击事件
	 */
	// public void onChangeTab() {
	// ((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);//
	// 第四个页签全选
	// // tableOpd.removeRowAll();
	// }

	/**
	 * 审核清空操作
	 */
	public void onClearA() {
		tableAudit.removeRowAll();
		((TCheckBox) this.getComponent("SELECT_ALL")).setSelected(false);// 第四个页签全选
		((TRadioButton) this.getComponent("UNRDO_AUDIT")).setSelected(false);// 未审核默认
		this.setValue("INS_CROWD_TYPE", "1");// 人群类别默认
	}

	/**
	 * 复选框选中事件
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBox(Object obj) {
		TTable table = (TTable) obj;
		// TParm oldTempParm = new TParm();
		// TParm oldParm = table.getParmValue();
		// int index = 0;
		table.acceptText();
		return false;
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

	/**
	 * 开具下载单选按钮操作
	 */
	public void onClickSave() {
		if (getRadioButton("RO_OPEN").isSelected()) {// 开具单选按钮
			callFunction("UI|REGISTER_NO|setEnabled", false);// 门特登记编号
		} else {
			callFunction("UI|REGISTER_NO|setEnabled", true);// 门特登记编号
		}
	}

	/**
	 * 获得就诊号码
	 */
	public void getCaseNo() {
		if (pat == null) {
			this.messageBox("病案号不能为空");
			this.grabFocus("MR_NO");
			return;
		}
		TParm parm = new TParm();
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("PAT_NAME", pat.getName());
		parm.setData("SEX_CODE", pat.getSexCode());
		parm.setData("AGE", getValue("AGE"));
		// 判断是否从明细点开的就诊号选择
		parm.setData("count", "0");
		String caseNo = (String) openDialog(
				"%ROOT%\\config\\opb\\OPBChooseVisit.x", parm);
		this.setValue("CASE_NO", caseNo);
		parm.setData("CASE_NO", caseNo);
		TParm diagRecparm = DiagRecTool.getInstance().queryInsData(parm);
		if (diagRecparm.getErrCode() < 0) {
			return;
		}
		// ====================pangben 2012-4-10 start
		String sql = "SELECT MED_HISTORY,DISEASE_HISTORY,ASSISTANT_EXAMINE,ASSSISTANT_STUFF FROM REG_PATADM WHERE CASE_NO='"
				+ caseNo + "'";
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlParm.getValue("DISEASE_HISTORY",0).length()>0) {
			String[] newString = pageOneNew.split(";");
			for (int i = 0; i < newString.length; i++) {
				this.setValue(newString[i], sqlParm.getValue(newString[i], 0));
			}
		}
		// ====================pangben 2012-4-10 stop
		this.setValue("DIAG_DESC", diagRecparm.getValue("ICD_CHN_DESC", 0));// 诊断名称
		this.setValue("DIAG_CODE", diagRecparm.getValue("ICD_CODE", 0));// 诊断编码
	}

	/**
	 * 审核打印
	 */
	public void onExePrint() {
		TParm parmValue = tableAudit.getParmValue();// 获得审核数据
		int row = tableAudit.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要执行的数据");
			return;
		}
		TParm tempParm = parmValue.getRow(row);// 执行的数据
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", "天津市基本医疗保险糖尿病门诊特定病种鉴定表");
		String companyCode = tempParm.getValue("COMPANY_CODE");// 单位代码
		char[] com = companyCode.toCharArray();
		for (int i = 0; i < com.length; i++) {
			parm.setData("UNIT" + (i + 1), "TEXT", com[i]);
		}
		// LAST_JUDGE_END 1.首次 2.复查 3.逾期首次
		if (tempParm.getInt("REGISTER_SERIAL_NO") == 1) {
			parm.setData("REGISTER_SERIAL_NO1", "TEXT", "√");
		} else if (tempParm.getInt("REGISTER_SERIAL_NO") == 2) {
			parm.setData("REGISTER_SERIAL_NO2", "TEXT", "√");
		} else if (tempParm.getInt("REGISTER_SERIAL_NO") == 3) {
			parm.setData("REGISTER_SERIAL_NO3", "TEXT", "√");
		}
		parm.setData("TABLE_NO", "TEXT", "18_1"); // 表号
		parm.setData("PAT_NAME", "TEXT", tempParm.getValue("PAT_NAME"));// 姓名
		parm.setData("SEX_CODE", "TEXT", null != tempParm
				&& tempParm.getInt("SEX_CODE") == 1 ? "男" : "女");// 性别
		parm.setData("AGE", "TEXT", tempParm.getValue("PAT_AGE"));// 年龄
		parm.setData("TEL_P", "TEXT", tempParm.getValue("PAT_PHONE"));// 电话号码
		parm.setData("IDNO", "TEXT", tempParm.getValue("IDNO"));// 身份证号码
		parm.setData("ADDRESS", "TEXT", tempParm.getValue("ADDRESS"));// 地址
		// 病史
		String diseaseHistory = tempParm.getValue("DISEASE_HISTORY");
		setDiseaseHistory(parm, diseaseHistory, "DISEASE_HISTORY_");
		// 既往史
		String medHistory = tempParm.getValue("MED_HISTORY");
		setDiseaseHistory(parm, medHistory, "MED_HISTORY_");
		// 辅助检查
		String assistantExamine = tempParm.getValue("ASSISTANT_EXAMINE");

		setExamine(parm, assistantExamine, "ASSISTANT_EXAMINE_");
		if (tempParm.getInt("LAST_JUDGE_END") == 1) {
			parm.setData("LAST_JUDGE_END_1", "TEXT", "√");// 临床诊断
		} else {
			parm.setData("LAST_JUDGE_END_1", "TEXT", "√");// 临床诊断
		}
		parm.setData("AUDIT_CENTER_USER", "TEXT", tempParm
				.getValue("AUDIT_CENTER_USER"));// 鉴定人员
		parm.setData("HOSP_CODE_LEVEL3", "TEXT", getHospDesc(tempParm
				.getValue("HOSP_CODE_LEVEL3")));// 三级医院
		parm.setData("HOSP_CODE_LEVEL2", "TEXT", getHospDesc(tempParm
				.getValue("HOSP_CODE_LEVEL2")));// 二级医院
		parm.setData("HOSP_CODE_LEVEL1", "TEXT", getHospDesc(tempParm
				.getValue("HOSP_CODE_LEVEL1")));// 一级医院
		parm.setData("DRUGSTORE_CODE", "TEXT", getHospDesc(tempParm
				.getValue("DRUGSTORE_CODE")));// 药店
		parm.setData("BEGIN_DATE", "TEXT", tempParm.getValue("BEGIN_DATE")
				.substring(0, 11));// 开始时间
		parm.setData("END_DATE", "TEXT", tempParm.getValue("END_DATE")
				.substring(0, 11));// 结束时间
		// 分中心

		parm.setData("BRANCH_DESC", "TEXT", getBranch(tempParm
				.getValue("BRANCH_CODE")));
		parm.setData("MED_HELP_COMPANY", "TEXT", tempParm
				.getValue("MED_HELP_COMPANY"));// 参保单位
		parm.setData("JUDGE_CONTER", "TEXT", "");// 鉴定中心
		parm.setData("JUDGE_SEQ", "TEXT", tempParm.getValue("JUDGE_SEQ"));

		parm.setData("CTZ_CODE", "TEXT", getCtzCode(tempParm
				.getInt("INS_CROWD_TYPE"), tempParm));
		parm.setData("REGISTER_USER", "TEXT", tempParm
				.getValue("REGISTER_USER"));//
		parm.setData("REGISTER_RESPONSIBLE", "TEXT", tempParm
				.getValue("REGISTER_RESPONSIBLE"));// 
		
		this.openPrintWindow("%ROOT%\\config\\prt\\INS\\INSMTRegister.jhw",
				parm);

	}

	private String getHospDesc(String code) {
		String sql = "SELECT HOSP_DESC FROM INS_HOSP_LIST WHERE HOSP_CODE='"
				+ code + "'";
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlParm.getErrCode() < 0) {
			return "";
		}
		return sqlParm.getValue("HOSP_DESC", 0);
	}

	/**
	 * 辅助检查
	 * 
	 * @return
	 */
	private void setExamine(TParm parm, String strExa, String name) {
		String str[] = getArray(strExa, SEPARATED);
		//
		for (int i = 1; i <= 11; i++) {
			parm.setData(name + i, "TEXT", str[i - 1]);
			// getNumCom("B"+i).setText(str[i-1]);
		}

	}

	/**
	 * 构造病史
	 * 
	 * @return
	 */
	private void setDiseaseHistory(TParm parm, String strDH, String name) {
		String str[] = getArray(strDH, SEPARATED);
		for (int i = 1; i <= 4; i++) {
			if (str[i - 1].equals("1")) {
				parm.setData(name + i, "TEXT", "√");//
			}
		}
		if (str.length > 4 && !name.equals("MED_HISTORY_")) {
			parm.setData("YEAR", "TEXT", str[4]);//
			parm.setData("JIN", "TEXT", str[5]);//
		}
	}

	/**
	 * 所属社保机构
	 * 
	 * @param code
	 * @return
	 */
	private String getBranch(String code) {
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='INS_FZX' AND ID='"
				+ code + "'";
		TParm sqlParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (sqlParm.getErrCode() < 0) {
			return "";
		}
		return sqlParm.getValue("CHN_DESC", 0);
	}

	/**
	 * 通过医保出参查询本地人员类别代码
	 * 
	 * @param crowType
	 * @param exeParm
	 * @return
	 */
	private String getCtzCode(int crowType, TParm exeParm) {
		String ctz_code = "";
		String sql = "";
		if (crowType == 1) {// 1 城职 2.城居
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND CTZ_CODE IN('11','12','13')";
		} else if (crowType == 2) {// 1 城职 2.城居
			ctz_code = exeParm.getValue("PAT_TYPE");
			sql = " AND CTZ_CODE IN('21','22','23')";
		}
		StringBuffer messSql = new StringBuffer();
		messSql.append(
				"SELECT CTZ_DESC,NHI_NO FROM SYS_CTZ WHERE NHI_NO='" + ctz_code
						+ "' AND NHI_CTZ_FLG='Y' ").append(sql);
		TParm ctzParm = new TParm(TJDODBTool.getInstance().select(
				messSql.toString()));
		if (ctzParm.getErrCode() < 0) {
			return "";
		}
		return ctzParm.getValue("CTZ_DESC", 0);
	}
	
	
	public void onSumMrNoAction(){
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("SUM_MR_NO")));
		Pat pat = Pat.onQueryByMrNo(getValueString("SUM_MR_NO"));
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
			mrNo = pat.getMrNo();
		}
		setValue("SUM_MR_NO",mrNo);
		
		
	}
	

}
