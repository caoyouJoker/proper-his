package com.javahis.ui.reg;

import java.awt.event.FocusEvent;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.bil.BIL;
import jdo.bil.BILContractRecordTool;
import jdo.bil.BILInvoiceTool;
import jdo.bil.BILInvrcptTool;
import jdo.bil.BILREGRecpTool;
import jdo.bil.BILTool;
import jdo.bil.BilInvoice;
import jdo.ekt.EKTIO;
import jdo.ekt.EKTNewTool;
//kangy 脱卡还原     import jdo.ekt.EKTReadCard;
import jdo.ekt.EKTTool;
import jdo.ins.INSMZConfirmTool; //import jdo.ins.INSTJFlow;
import jdo.ins.INSRunTool;
import jdo.ins.INSTJFlow;
import jdo.ins.INSTJReg; //import jdo.ins.INSRunTool;
import jdo.opd.OrderTool;
import jdo.reg.PanelRoomTool;
import jdo.reg.PatAdmTool;
import jdo.reg.REGCcbReTool;
import jdo.reg.REGClinicQueTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.Reg;
import jdo.reg.RegMethodTool;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.reg.ws.RegQETool;
import jdo.sid.IdCardO;
import jdo.sys.IReportTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSOperatorTool;
import jdo.sys.SYSPostTool;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.device.EktDriver;
import com.javahis.device.NJCityInwDriver;
import com.javahis.device.NJSMCardDriver;
import com.javahis.device.NJSMCardYYDriver;
import com.javahis.system.textFormat.TextFormatSYSCtz;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;
import com.javahis.ui.ekt.testEkt.EktParam;
import com.javahis.ui.ekt.testEkt.EktTradeContext;
import com.javahis.util.StringUtil;

/**
 * 
 * 
 * <p>
 * Title:挂号主档控制类
 * </p>
 * 
 * <p>
 * Description:挂号主档控制类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author wangl 2008.09.22
 * @version 1.0
 */
public class REGPatAdmControl extends TControl {
	// 病患对象
	private Pat pat;
	// 挂号对象
	private Reg reg;
	// 门急别
	public String admType = "O";
	// 预约时间
	String startTime;
	// 医疗卡卡号
	String ektCard;
	int selectRow = -1;
	public String tredeNo;
	public String businessNo; // 挂号出现问题撤销操作
	public String tradeNoT;
	public String endInvNo;
	public TParm p3; // 医保卡参数
	private boolean feeShow = false; // =====pangben 20110815 医保中心获得费用管控
	private boolean txEKT = false; // 泰心医疗卡管理执行直接写卡操作=====pangben 20110916
	public  String ektOldSum; // 医疗卡操作失败回写金额
	public  String ektNewSum; // 扣款以后的金额
	// 错误信息标记

	public TParm insParm; // 医保出参，U 方法 A 方法参数

	public boolean tjINS = false; // 天津医保管控，判断是否执行了医疗卡操作

	public boolean insFlg = false; // 医保卡读卡成功管控
	// private String caseNo; // 医保操作刷卡时需要就诊号
	private TParm regionParm; // 获得医保区域代码
	// zhangp 20111227
	private TParm parmSum; // 执行充值操作参数
	private boolean printBil = false; // 打印票据时使用
	private TParm reSetEktParm; // 医疗卡退费使用判断是否执行医疗卡退费操作
	private String confirmNo; // 医保卡就诊号，退挂时时使用
	private String reSetCaseNo; // 退挂使用就诊号码
	private String insType; // 医保就诊类型: 1.城职普通 2.城职门特 3.城居门特 退挂使用
	private boolean tableFlg = false; // 第一个页签（全部）表格 获得焦点管控
	public double ins_amt = 0.00; // 医保金额
	public boolean ins_exe = false; // 判断是否医保执行 操作，执行操作表数据时实现在途状态
	public TParm greenParm = null;// //绿色通道使用金额
	public double accountamtforreg = 0.00;// 个人账户
	BilInvoice ektinvoice;
	BilInvoice invoice;
	SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMddHHmmss");
	//kangy 脱卡还原      private boolean dev_flg=true;
	
	String authCode = "00";//  ==zhanglei VVIP输入验证码返回一个正确的
	
	/**
	 * 初始化参数
	 */
	public void onInitParameter() {
		String parmAdmType = (String) this.getParameter();
		if (parmAdmType != null && parmAdmType.length() > 0)
			admType = parmAdmType;
		setValue("ADM_TYPE", admType);
		callFunction("UI|SESSION_CODE|setAdmType", admType);
		callFunction("UI|CLINICTYPE_CODE|setAdmType", admType);
		callFunction("UI|VIP_SESSION_CODE|setAdmType", admType);
		callFunction("UI|setTitle", "O".equals(admType) ? "门诊挂号窗口" : "急诊挂号窗口");
		callFunction("UI|ERD_LEVEL_TITLE|setVisible", false);
		callFunction("UI|ERD_LEVEL|setVisible", false);
		//到院时间add by huangjw 20150603 && 门诊不显示
		callFunction("UI|ARRIVE_DATE_TIME|setVisible", false);
		callFunction("UI|ARRIVE_DATE|setVisible", false);
		callFunction("UI|TRIAGE_NO_TITLE|setVisible", false);
		callFunction("UI|TRIAGE_NO|setVisible", false);
		
		if (admType.equals("E")) {
			callFunction("UI|ERD_LEVEL_TITLE|setVisible", true);
			callFunction("UI|ERD_LEVEL|setVisible", true);
			callFunction("UI|TRIAGE_NO_TITLE|setVisible", true);
			callFunction("UI|TRIAGE_NO|setVisible", true);
			//急诊显示到院时间 add by huangjw 20150603
			callFunction("UI|ARRIVE_DATE_TIME|setVisible", true);
			callFunction("UI|ARRIVE_DATE|setVisible", true);
			TParm selTriageFlg = REGSysParmTool.getInstance().selectdata();
			String triageFlg = selTriageFlg.getValue("TRIAGE_FLG", 0);
			if ("N".equals(triageFlg))
				callFunction("UI|ERD_LEVEL|setEnabled", false);
			setValue("ADM_DATE", SystemTool.getInstance().getDate());
			String sessionCode = initSessionCode();
			Timestamp admDate = TJDODBTool.getInstance().getDBTime();
			// 根据时段判断应该显示的日期（针对于晚班夸0点的问题，跨过0点的晚班应该显示前一天的日期）
			if (!StringUtil.isNullString(sessionCode)
					&& !StringUtil.isNullString(admType)) {
				admDate = SessionTool.getInstance().getDateForSession(admType,
						sessionCode, Operator.getRegion());
				this.setValue("ADM_DATE", admDate);
			}
        } else {
            callFunction("UI|Wrist|setVisible", false);//wanglong add 20150413
        }
		// 初始化科室Combo
		callFunction("UI|DEPT_CODE|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		// 初始化科室(普通诊sort)Combo
		callFunction("UI|DEPT_CODE_SORT|"
				+ ("O".equals(admType) ? "setOpdFitFlg" : "setEmgFitFlg"), "Y");
		regionParm = SYSRegionTool.getInstance().selectdata(
				Operator.getRegion()); // 获得医保区域代码
	}

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		
		// 初始化时段Combo,取得默认时段
		initSession();
		setValue("REGION_CODE", Operator.getRegion());
		// ========pangben modify 20110421 start 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110421 stop

		// 初始化默认(现场)挂号方式
		setValue("REGMETHOD_CODE", "A");
		// 初始化ID号输入框
		onClickRadioButton();
		initSchDay();
		// 初始化预约信息开始时间
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// 初始化VIP班表Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// 置退挂,报道按钮为灰
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|NHI_NO|setEnabled", false); // 医保卡不可编辑
		// 初始化初复诊
		TParm selVisitCode = REGSysParmTool.getInstance().selVisitCode();
		if (selVisitCode.getValue("DEFAULT_VISIT_CODE", 0).equals("1")) {
			setValue("VISIT_CODE_F", "Y");
			callFunction("UI|MR_NO|setEnabled", true);
		}
		// 初始化下一票号
		 invoice = new BilInvoice();
		invoice = invoice.initBilInvoice("REG");
		//==start==add by kangy ===20160810
		ektinvoice=invoice.initBilInvoice("EKT");
		initBilInvoice(ektinvoice.initBilInvoice("EKT"));
		callFunction("UI|BIL_CODE|setValue", ektinvoice.getUpdateNo());
		callFunction("UI|BIL_CODE|Enabled", false);
		//==end==add by kangy ===20160810
		endInvNo = invoice.getEndInvno();
		// ===zhangp 20120306 modify start
		if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
				Operator.getRegion(), invoice.getUpdateNo())) {
			setValue("NEXT_NO", invoice.getUpdateNo());
		} else {
			messageBox("票据已用完");
		}
		// ===zhangp 20120306 modify end
		// 设置默认服务等级
		setValue("SERVICE_LEVEL", "1");
		// this.onClear();
		// ======zhangp 20120224 modify start
		String id = EKTTool.getInstance().getPayTypeDefault();
		setValue("GATHER_TYPE", id);
		// ======zhangp 20120224 modify end
		if(admType.equals("E")){//初始化到院时间 add by huangjw 20150603
			this.setValue("ARRIVE_DATE", SystemTool.getInstance().getDate().toString().substring(0,16).replaceAll("-", "/"));
		}
	}
	
	/**
	 * 验证特殊身份
	 */
	public void ChangeCtz(){
		//TTextFormat aa = (TTextFormat) this.getComponent("REG_CTZ1");
		//aa.addEventListener(TComboBoxEvent.SELECTED,this,"ChangeCtz");
		String ctzNo = this.getValueString("REG_CTZ1");
		String sql = "SELECT SPECIAL_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"+ctzNo+"'";
		TParm ctzparm = new TParm(TJDODBTool.getInstance().select(sql));
		if("Y".equals(ctzparm.getValue("SPECIAL_FLG",0))){
			this.openDialog("%ROOT%\\config\\reg\\REGSpecialFlg.x");
		}
	}
	/**
	 * 初始化班表
	 */
	public void initSchDay() {
		new Thread() {
			// 线程,为节省时间提高打开挂号主界面效率
			public void run() {
				// 初始化默认支付方式
				//===zhangp 不修改支付方式 20130517
				TParm selPayWay = REGSysParmTool.getInstance().selPayWay();
				setValue("PAY_WAY", selPayWay.getValue("DEFAULT_PAY_WAY", 0));
				// 初始化带入医师排班
				onQueryDrTable();

				// 初始化带入VIP班表
				onQueryVipDrTable();
			}
		}.start();
	}

	/**
	 * 增加对Table1的监听
	 */
	public void onTable1Clicked() {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		int row = (Integer) callFunction("UI|Table1|getClickedRow");
		if (row < 0)
			return;
		//=====20130507 yanjing 添加查询日班表数据库判断对应信息是否存在
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE",admType);
//		 TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("Table1");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
	    String admDate = parm.getValue("ADM_DATE").substring(0, 4)+parm.getValue("ADM_DATE").substring(5,7)
	                +parm.getValue("ADM_DATE").substring(8,10);
	    parm.setData("ADM_DATE", admDate);
		parm.setData("CLINICROOM_NO",tableParm.getValue("CLINICROOM_NO",row ));
		TParm result = SchDayTool.getInstance().selectOneDrTable(parm);
		if (result.getCount()<=0) {
			callFunction("UI|SAVE_REG|setEnabled", false);//收费按钮不可编辑=====yanjing
			this.messageBox("日期、医师及诊室信息不一致，请刷新界面！");
			return;
		}
		//=======20130507 yanjing end
		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// =====modify by caowl 20120809 删除了待诊人数相关代码

		// 获得挂号方式执行判断是否打票操作
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // 获得是否可以打票注记
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("挂号失败");
			return;
		}
		// 不打票操作
		if (null != tableParm.getValue("TYPE", row)
				&& tableParm.getValue("TYPE", row).equals("VIP")
				&& (null == regMethodParm.getValue("PRINT_FLG", 0) || regMethodParm
						.getValue("PRINT_FLG", 0).length() <= 0)) {

			onClickClinicType(false);
		} else {
			onClickClinicType(true);
		}
		setControlEnabled(false);
		// 置退挂按钮不可编辑
		callFunction("UI|unreg|setEnabled", false);
		// 置补印按钮不可编辑
		callFunction("UI|print|setEnabled", false);
		tableFlg = true; // 第一个页签管控
		this.grabFocus("FeeS");
	}

	/**
	 * 增加对Talbe2的监听事件
	 */
	public void onTable2Clicked() {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		startTime = new String();
		int row = (Integer) callFunction("UI|Table2|getClickedRow");
		if (row < 0)
			return;
		// 拿到table控件
		TTable table2 = (TTable) callFunction("UI|table2|getThis");
		if (table2.getValueAt(row, table2.getColumnIndex("QUE_STATUS")).equals(
				"Y")) {
			this.messageBox("已占号!");
			callFunction("UI|table2|clearSelection");
			return;
		}
		// =====已过诊pangben 2012-3-26 start
		String startNowTime = StringTool.getString(SystemTool.getInstance()
				.getDate(), "HHmm");// 系统当前时间
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// 系统当前日期
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// 当前挂号日期
		TParm data = table2.getParmValue();
		if (admDate.compareTo(admNowDate) < 0) {
			this.messageBox("已经过诊不可以挂号");
			callFunction("UI|table2|clearSelection");
			return;
		} else if (admDate.compareTo(admNowDate) == 0) {
			startTime = data.getValue("START_TIME", row);
			if (startTime.compareTo(startNowTime) < 0) {
				this.messageBox("已经过诊不可以挂号");
				callFunction("UI|table2|clearSelection");
				return;
			}
		}
		// =====已过诊pangben 2012-3-26 stop
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				data, row);

		selectRow = row;
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", data.getValue("DR_CODE", row));
		onClickClinicType(true);
		this.grabFocus("FeeS");
	}

	/**
	 * 增加对Talbe3的监听事件
	 */
	public void onTable3Clicked() {
		int row = (Integer) callFunction("UI|Table3|getClickedRow");
		if (row < 0)
			return;
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		TParm parm = table3.getParmValue();
		// System.out.println("退挂信息" + parm);
		// parm.getValue("ARRIVE_FLG",row);隐藏列取法
		String arriveFlg = (String) table3.getValueAt(row, 7);
		// 判断是否预约挂号
		if ("N".equals(arriveFlg)) {
			setValueForParm(
					"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE;REGMETHOD_CODE",
					parm, row);

			// setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
			setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));			
			setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));

			onClickClinicType(true);
			// onDateReg();
			callFunction("UI|CLINICROOM_NO|onQuery");
			// 置报道按钮可编辑
			callFunction("UI|arrive|setEnabled", true);
			// setValue("FeeY", parm.getValue("ARRIVE_FLG", row));
			// setValue("FeeS", parm.getValue("ARRIVE_FLG", row));
			this.messageBox(getValue("PAT_NAME") + "有预约信息");
			// 置收费按钮不可编辑
			// ===zhangp 20120306 modify start
			callFunction("UI|SAVE_REG|setEnabled", false);
			// ===zhangp 20120306 modify end
			// 置退挂按钮为灰
			callFunction("UI|unreg|setEnabled", true);

		}

		else {
			// System.out.println("已挂信息:::"+parm);
			// this.messageBox_("已挂信息"+parm);
			setValueForParm(
					"ADM_DATE;SESSION_CODE;CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO;CONTRACT_CODE",
					parm, row);

			setValue("REG_CTZ1", parm.getValue("CTZ1_CODE", row));
			setValue("REG_CTZ2", parm.getValue("CTZ2_CODE", row));
			setValue("SERVICE_LEVEL", parm.getValue("SERVICE_LEVEL", row));
			if("E".equals(admType)){
				setValue("TRIAGE_NO", parm.getValue("TRIAGE_NO", row));
				setValue("ERD_LEVEL", parm.getValue("ERD_LEVEL", row));

			}
			callFunction("UI|DEPT_CODE|onQuery");
			callFunction("UI|DR_CODE|onQuery");
			callFunction("UI|CLINICROOM_NO|onQuery");
			callFunction("UI|CLINICTYPE_CODE|onQuery");
			// onClickClinicType( -1);
			// ==================pangben modify 20110815 修改获得票据表中的价格显示到界面
			unregFeeShow(parm.getValue("CASE_NO", row));
			setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
					parm, row);
			// 置报道按钮不可编辑
			callFunction("UI|arrive|setEnabled", false);
			// 置收费按钮不可编辑
			callFunction("UI|SAVE_REG|setEnabled", false);
			// 置退挂按钮可编辑
			callFunction("UI|unreg|setEnabled", true);
			// 置补印按钮可编辑
			callFunction("UI|print|setEnabled", true);
		}
		// onDateReg();
		//
		// onSaveRegParm();

	}

	/**
	 * 无身份注记事件
	 */
	public void onSelForeieignerFlg() {
		if (this.getValue("FOREIGNER_FLG").equals("Y"))
			this.grabFocus("BIRTH_DATE");
		if (this.getValue("FOREIGNER_FLG").equals("N"))
			this.grabFocus("IDNO");
	}

	/**
	 * 初复诊状态
	 */
	public void onClickRadioButton() {
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
//			callFunction("UI|MR_NO|setEnabled", false);
//			this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			callFunction("UI|MR_NO|setEnabled", true);
			this.grabFocus("MR_NO");
		}
		this.onClear();
	}

	/**
	 * 保存病患信息
	 */
	public void onSavePat() {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		// 不能输入空值
		if (getValue("BIRTH_DATE") == null) {
			this.messageBox("出生日期不能为空!");
			return;
		}
		if (!this.emptyTextCheck("PAT_NAME,SEX_CODE,CTZ1_CODE"))
			return;
		pat = new Pat();
		// 病患姓名
		pat.setName(TypeTool.getString(getValue("PAT_NAME")));
		// 英文名
		pat.setName1(TypeTool.getString(getValue("PAT_NAME1")));
		// 姓名拼音
		pat.setPy1(TypeTool.getString(getValue("PY1")));
		// 身份证号
		pat.setIdNo(TypeTool.getString(getValue("IDNO")));
		// 外国人注记
		pat.setForeignerFlg(TypeTool.getBoolean(getValue("FOREIGNER_FLG")));
		// 出生日期
		pat.setBirthday(TypeTool.getTimestamp(getValue("BIRTH_DATE")));
		// 性别
		pat.setSexCode(TypeTool.getString(getValue("SEX_CODE")));
		// 电话
		pat.setTelHome(TypeTool.getString(getValue("TEL_HOME")));
		// 邮编
		pat.setPostCode(TypeTool.getString(getValue("POST_CODE")));
		// 地址
		pat.setAddress(TypeTool.getString(getValue("ADDRESS")));
		// 身份1
		pat.setCtz1Code(TypeTool.getString(getValue("CTZ1_CODE")));
		// 身份2
		pat.setCtz2Code(TypeTool.getString(getValue("CTZ2_CODE")));
		// 身份3
		pat.setCtz3Code(TypeTool.getString(getValue("CTZ3_CODE")));
		// 医保卡市民卡
		pat.setNhiNo(TypeTool.getString(getValue("NHI_NO"))); // =============pangben
		// modify
		// 20110808
		if (this.messageBox("病患信息", "是否保存", 0) != 0)
			return;
		TParm patParm = new TParm();
		patParm.setData("MR_NO", getValue("MR_NO"));
		patParm.setData("PAT_NAME", getValue("PAT_NAME"));
		patParm.setData("PAT_NAME1", getValue("PAT_NAME1"));
		patParm.setData("PY1", getValue("PY1"));
		patParm.setData("IDNO", getValue("IDNO"));
		patParm.setData("BIRTH_DATE", getValue("BIRTH_DATE"));
		patParm.setData("TEL_HOME", getValue("TEL_HOME"));
		patParm.setData("SEX_CODE", getValue("SEX_CODE"));
		patParm.setData("POST_CODE", getValue("POST_CODE"));
		patParm.setData("ADDRESS", getValue("ADDRESS"));
		patParm.setData("CTZ1_CODE", getValue("CTZ1_CODE"));
		patParm.setData("CTZ2_CODE", getValue("CTZ2_CODE"));
		patParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		patParm.setData("NHI_NO", getValue("NHI_NO")); // =============pangben
		// modify 20110808
		if (StringUtil.isNullString(getValue("MR_NO").toString())) {
			patParm.setData("MR_NO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME").toString())) {
			patParm.setData("PAT_NAME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PAT_NAME1").toString())) {
			patParm.setData("PAT_NAME1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("PY1").toString())) {
			patParm.setData("PY1", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("IDNO").toString())) {
			patParm.setData("IDNO", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("BIRTH_DATE").toString())) {
			patParm.setData("BIRTH_DATE", new TNull(Timestamp.class));
		}
		if (StringUtil.isNullString(getValue("TEL_HOME").toString())) {
			patParm.setData("TEL_HOME", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("SEX_CODE").toString())) {
			patParm.setData("SEX_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("POST_CODE").toString())) {
			patParm.setData("POST_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("ADDRESS").toString())) {
			patParm.setData("ADDRESS", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ1_CODE").toString())) {
			patParm.setData("CTZ1_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ2_CODE").toString())) {
			patParm.setData("CTZ2_CODE", new TNull(String.class));
		}
		if (StringUtil.isNullString(getValue("CTZ3_CODE").toString())) {
			patParm.setData("CTZ3_CODE", new TNull(String.class));
		}
		// =============pangben modify 20110808
		if (StringUtil.isNullString(getValue("NHI_NO").toString())) {
			patParm.setData("NHI_NO", new TNull(String.class));
		}
		TParm result = new TParm();
		// ===zhangp 20120613 start
		// if ("Y".equals(getValue("VISIT_CODE_F"))) {
		if (!"".equals(getValueString("MR_NO"))) {
			// ===zhangp 20120613 end
			if (getValue("MR_NO").toString().length() == 0) {
				this.messageBox("请先检索出病患");
				return;
			}
			// 更新病患
			result = PatTool.getInstance().upDateForReg(patParm);
			setValue("MR_NO", getValue("MR_NO"));
			pat.setMrNo(getValue("MR_NO").toString());
		} else {
			// 新增病患
			// pat.setTLoad(StringTool.getBoolean("" + getValue("tLoad")));
			
			pat.onNew();
			setValue("MR_NO", pat.getMrNo());
			
			
		}
		if (result.getErrCode() != 0) {
			this.messageBox("E0005");
		} else {
			this.messageBox("P0005");
		}
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("是否解锁", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " 强制解锁[" + aa
//										+ " 病案号：" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// 20120112 zhangp 保存之后建卡
		// ===zhangp 20120309 modify start
		if (getValueBoolean("VISIT_CODE_C")) {
			ektCard();
		}
		// ===物联网 start
		if (Operator.getSpcFlg().equals("Y")) {
//			SYSPatinfoClientTool sysPatinfoClientTool = new SYSPatinfoClientTool(
//					this.getValue("MR_NO").toString());
//			SysPatinfo syspat = sysPatinfoClientTool.getSysPatinfo();
//			SpcPatInfoService_SpcPatInfoServiceImplPort_Client serviceSpcPatInfoServiceImplPortClient = new SpcPatInfoService_SpcPatInfoServiceImplPort_Client();
//			String msg = serviceSpcPatInfoServiceImplPortClient
//					.onSaveSpcPatInfo(syspat);
//			if (!msg.equals("OK")) {
//				System.out.println(msg);
//			}
			TParm spcParm = new TParm();
			spcParm.setData("MR_NO", this.getValue("MR_NO").toString());
			TParm spcReturn = TIOM_AppServer.executeAction(
	                "action.sys.SYSSPCPatAction",
	                "getPatName", spcParm);
		}
		// ===物联网 end
		this.onClear();
	}
	/**
	 * 查询是否在黑名单
	 * caowl
	 * */
	public void onBlackFlg(String mr_no){		
		String sql = "SELECT BLACK_FLG FROM SYS_PATINFO WHERE MR_NO = '"+mr_no+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getData("BLACK_FLG",0).toString().equals("Y")){
			this.messageBox("此人在黑名单中！");			
		}
		
	}
	
	public void onMrNo(){
		onQueryNO(true);
		//  ==add by zhanglei 20171116  增加挂号时若挂号身份是特殊身份弹出验证码
//		if(getREGSpecialFlg().equals("Y")){
//			checkSpecialFlg();
//		}
	}
 
	/**
	 * 查询病患信息
	 */
	public void onQueryNO(boolean flg) {
		onClearRefresh();
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		insFlg = false; // 初始化
		insType = null;// 初始化
		pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		if (pat == null) {
			this.messageBox("无此病案号!111");
			return;
		}
		
		//true 对身份证和姓名进行查重  add by huangtt 20170602
		if(flg){
			boolean checkFlg = PatTool.getInstance().selCheckIdNo(pat.getIdNo(), pat.getName());
			if(checkFlg){
				this.messageBox("该病患存在多条信息");
				TParm checkParm = new TParm();
				checkParm.setData("PAT_NAME", pat.getName());
				checkParm.setData("IDNO", pat.getIdNo());
				
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSAutoCheckDuplicate.x", checkParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
//					System.out.println("reg---"+patParm);
					if(patParm.getValue("MR_NO").length() > 0){
						pat = Pat.onQueryByMrNo(patParm.getValue("MR_NO"));
					}
					
				}

			}
		}

        String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
        setVisitCodeFC(srcMrNo); //add by huangtt 20151020 自动判断初复诊
        
        if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
        }
        setValue("MR_NO", pat.getMrNo());
        // caowl 20131105 start
        onBlackFlg(pat.getMrNo());
		//caowl 20131105 end
		setValue("PAT_NAME", pat.getName().trim());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		onPast();
		setValue("SEX_CODE", pat.getSexCode());
		setValue("TEL_HOME", pat.getTelHome());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("ADDRESS", pat.getAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("是否解锁", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " 强制解锁[" + aa
//										+ " 病案号：" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// 锁病患信息
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
			// this.messageBox_("加锁成功!");//测试专用
			selPatInfoTable();
		// =======20120216 zhangp modify start
		String sql = "select CARD_NO from EKT_ISSUELOG where mr_no = '"
				+ pat.getMrNo() + "'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
		}
		if (result.getCount() < 0) { // 如果未查出数据（未制卡）则制卡
			if (messageBox("提示", "该病患未办理医疗卡,是否办理医疗卡", 0) == 0) {
				ektCard(); // 制卡
				// ====zhangp 20120227 modify start
				this.onClear();
			}
		}
		// =======20120216 zhangp modify end
		if("E".equals(admType)){
			this.grabFocus("TRIAGE_NO");
		}else{
			this.grabFocus("CLINICROOM_NO");
		}
		
		// ===zhangp 20120413 start
		// 初始化下一票号
		BilInvoice invoice = new BilInvoice();
		invoice = invoice.initBilInvoice("REG");
		endInvNo = invoice.getEndInvno();
		if (BILTool.getInstance().compareUpdateNo("REG", Operator.getID(),
				Operator.getRegion(), invoice.getUpdateNo())) {
			setValue("NEXT_NO", invoice.getUpdateNo());
		} else {
			messageBox("票据已用完");
		}
		// ===zhangp 20120413 end
		//yanjing 当急诊时刷新日版表
//		if (admType.equals("E")) {
//			initSchDay();
//		}
		
	}

	/**
	 * 查询病患信息
	 * 
	 * @param mrNo
	 *            String
	 */
	public void onQueryMrNO(String mrNo) {
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());

		pat = Pat.onQueryByMrNo(mrNo);
		if (pat == null) {
			this.messageBox("无此病案号!");
			return;
		}
		setVisitCodeFC(mrNo); //add by huangtt 20151020 自动判断初复诊
		setValue("MR_NO", mrNo);
		setValue("PAT_NAME", pat.getName());
		setValue("PAT_NAME1", pat.getName1());
		setValue("PY1", pat.getPy1());
		setValue("IDNO", pat.getIdNo());
		setValue("FOREIGNER_FLG", pat.isForeignerFlg());
		setValue("BIRTH_DATE", pat.getBirthday());
		setValue("SEX_CODE", pat.getSexCode());
		setValue("TEL_HOME", pat.getTelHome());
		setValue("POST_CODE", pat.getPostCode());
		onPost();
		setValue("ADDRESS", pat.getAddress());
		setValue("CTZ1_CODE", pat.getCtz1Code());
		setValue("REG_CTZ1", getValue("CTZ1_CODE"));
		setValue("CTZ2_CODE", pat.getCtz2Code());
		setValue("REG_CTZ2", getValue("CTZ2_CODE"));
		setValue("CTZ3_CODE", pat.getCtz3Code());
		// setValue("REG_CTZ3", getValue("CTZ3_CODE"));
//		String aa = PatTool.getInstance().getLockParmString(pat.getMrNo());
//		// 判断是否加锁
//		if (PatTool.getInstance().isLockPat(pat.getMrNo())) {
//			if (this.messageBox("是否解锁", PatTool.getInstance()
//					.getLockParmString(pat.getMrNo()), 0) == 0) {
//				PatTool.getInstance().unLockPat(pat.getMrNo());
//				PATLockTool.getInstance()
//						.log(
//								"ODO->" + SystemTool.getInstance().getDate()
//										+ " " + Operator.getID() + " "
//										+ Operator.getName() + " 强制解锁[" + aa
//										+ " 病案号：" + pat.getMrNo() + "]");
//			} else {
//				pat = null;
//				return;
//			}
//		}
		// 锁病患信息
//		if (PatTool.getInstance().lockPat(pat.getMrNo(), "REG"))
			// this.messageBox_("加锁成功!");//测试专用
			selPatInfoTable();
		if("E".equals(admType)){
			this.grabFocus("TRIAGE_NO");
		}else{
			this.grabFocus("CLINICROOM_NO");
		}
	}

	/**
	 * 应收金额获得焦点
	 * 
	 * @param e
	 *            FocusEvent
	 */
	public void onFocusLostAction(FocusEvent e) {
		onFee();

	}
	/**
	 * 
	* @Title: onCheckQueNo
	* @Description: TODO(校验就诊号)
	* @author pangben
	* @return
	* @throws
	 */
	private boolean onCheckQueNo(){
	//  pangben 20150302 添加校验诊号重复校验
		String regsql="SELECT CASE_NO FROM REG_PATADM WHERE REGION_CODE='"+reg.getRegion()+
		"' AND ADM_TYPE='"+reg.getAdmType()+"' AND ADM_DATE=TO_DATE('"+ StringTool.getString(
				reg.getAdmDate(), "yyyy/MM/dd")+"','YYYY/MM/DD') AND SESSION_CODE='"+reg.getSessionCode()+
				"' AND CLINICROOM_NO='"+reg.getClinicroomNo()+"' AND QUE_NO='"+reg.getQueNo()+"'" +
				" AND REGCAN_USER IS NULL";  //add by huangtt 20150707 查重要排除退挂的
		TParm regQueNoParm = new TParm(TJDODBTool.getInstance().select(regsql));
		if (regQueNoParm.getErrCode()<0) {
			this.messageBox("查询诊号出现错误");
			return false;
		}
		if (regQueNoParm.getCount()>0) {
			this.messageBox("此诊号已经使用,请重新操作");
			return false;
		}
		return true;
	}
	/**
	 * 保存REG对象
	 */
	public void onSaveReg() {
		
		if(p3 != null && p3.getValue("PK_CARD_NO").length() > 0){
			TParm eParm = RegQETool.getInstance().getEktMaster(p3.getValue("PK_CARD_NO"));
			if(eParm.getCount() > 0){
				if(this.getValueDouble("EKT_CURRENT_BALANCE")-eParm.getDouble("CURRENT_BALANCE",0) != 0){
					this.messageBox("医疗卡余额发生变化 ，请重新读卡挂号!!!!!");
					return;
				}
			}
			
		}
		
		// add by wangqing 201470627 start
		// 急诊挂号必须填检伤号
		if (admType.equals("E")) {
			if(this.getValueString("TRIAGE_NO").trim().length()==0){
				this.messageBox("请输入检伤号！！！");
				return;
			}
			this.onErd();	
		}
		// add by wangqing 20170627 end
		
				
//		=====yanj 20130502 添加时间校验
		if (admType.equals("E")) {
			String admNowTime1 = StringTool.getString(SystemTool.getInstance()
					.getDate(), "HH:mm:ss");// 系统当前时间
			String sessionCode = (String)this.getValue("SESSION_CODE");
			String startTime = SessionTool.getInstance().getStartTime(admType, sessionCode);
			String endTime = SessionTool.getInstance().getEndTime(admType, sessionCode);
			if (startTime.compareTo(endTime)<0) {
				if (!(admNowTime1.compareTo(startTime)>0&&(admNowTime1.compareTo(endTime)<0))) {
					this.messageBox("请刷新界面！");
					return;
				}
			}
				else {
					if (admNowTime1.compareTo(startTime)<0&&admNowTime1.compareTo(endTime)>0) {
						this.messageBox("请刷新界面！");
						return;
					}
				}	
			}
		// ====pangben 20131030 修改校验下面的挂号身份
		if (null==this.getValue("REG_CTZ1")||this.getValue("REG_CTZ1").toString().length()<=0) {
			messageBox("请选择身份");
			this.grabFocus("REG_CTZ1");
			return;
		}
		DecimalFormat df = new DecimalFormat("##########0.00");
		// 现场挂号
		if (this.getValue("REGMETHOD_CODE").equals("A")) {
			// 输入金额校验
			if (TypeTool.getDouble(df.format(getValue("FeeS"))) < TypeTool
					.getDouble(df.format(getValue("FeeY")))) {
				this.messageBox("金额不足");
				return;
			}
		}
		// 不能输入空值
		// if (!this.emptyTextCheck("DEPT_CODE,CLINICTYPE_CODE,PAY_WAY"))
		// return;
		if (this.getValue("DEPT_CODE") == null
				|| this.getValueString("DEPT_CODE").length() == 0) {
			this.messageBox("科室不能为空");
			return;
		}
		if (this.getValue("CLINICTYPE_CODE") == null
				|| this.getValueString("CLINICTYPE_CODE").length() == 0) {
			this.messageBox("号别不能为空");
			return;
		}
		if (admType.endsWith("E")) {
			if (this.getValue("ARRIVE_DATE")==null||this.getValue("ARRIVE_DATE").toString().length()<0){//校验到院时间 add by haungjw 20150603
				this.messageBox("到院时间不能为空");
				this.grabFocus("ARRIVE_DATE");
				return;
			}
//			if (!this.emptyTextCheck("ERD_LEVEL"))
//				return;
		}
		reg = new Reg();
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		if (RegMethodTool.getInstance().selPrintFlg(
				this.getValueString("REGMETHOD_CODE"))) {
			if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null) {
				this.messageBox("尚未开账");
				return;
			}
			if (reg.getRegReceipt().getBilInvoice().getUpdateNo().compareTo(
					reg.getRegReceipt().getBilInvoice().getEndInvno()) > 0) {
				this.messageBox("票据已用完!");
				return;
			}
		}
		// 病患
		if (pat == null) {
			this.messageBox("无病患信息");
			return;
		}
		// 判断是否为黑名单病患
		if (pat.getBlackFlg())
			this.messageBox("请注意,此为黑名单病患!");
		pat.setNhiNo(this.getValueString("NHI_NO"));
		// System.out.println("pat::" + pat.getNhiNo());
		reg.setPat(pat);
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("病案号不能为空");
			return;
		}
		// 挂号主表,REG对象
		// 2门急别
		if (!onSaveRegParm(true))
			return;
		// 医保医疗操作 共用部分
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // 支付类别

		reg.setTredeNo(tredeNo);
		String regmethodCode = this.getValueString("REGMETHOD_CODE"); // 挂号方式
		// 获得挂号方式执行判断是否打票操作
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ regmethodCode + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // 获得是否可以打票注记
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("挂号失败");
			return;
		}
		if (null != regMethodParm.getValue("PRINT_FLG", 0)
				&& regMethodParm.getValue("PRINT_FLG", 0).equals("Y")) {
			// 打票
			reg.setApptCode("N");
			reg.setRegAdmTime("");
		} else if (null == regMethodParm.getValue("PRINT_FLG", 0)
				|| regMethodParm.getValue("PRINT_FLG", 0).length() <= 0
				|| regMethodParm.getValue("PRINT_FLG", 0).equals("N")) {
			// 不打票操作
			reg.setApptCode("Y");
			// 12预约时间
			reg.setRegAdmTime(startTime);
		}
		// 获得第一个页签数据
		if (tableFlg) {
			// 判断是否VIP就诊
			TTable table1 = (TTable) this.getComponent("Table1");
			TParm parm = table1.getParmValue();
			TParm temp = parm.getRow(selectRow); // 获得第一个页签数据
			String type = temp.getValue("TYPE"); // VIP 和一般
			if (type.equals("VIP")) {
				// UPDATE REG_CLINICQUE &
				temp.setData("ADM_TYPE", admType); // 挂号类型
				temp.setData("SESSION_CODE", reg.getSessionCode()); // 时段
				temp.setData("ADM_DATE", StringTool.getString(
						(Timestamp) getValue("ADM_DATE"), "yyyyMMdd"));
				temp.setData("START_TIME", StringTool.getString(SystemTool
						.getInstance().getDate(), "HHmm"));//系统当前时间
				String admNowDate = StringTool.getString(SystemTool
						.getInstance().getDate(), "yyyyMMdd");// 系统当前日期
				// String startTime = "";
				if (temp.getValue("ADM_DATE").compareTo(admNowDate) < 0) {
					this.messageBox("已经过诊不可以挂号");
					callFunction("UI|table2|clearSelection");
					return;
				}
				// // 获得vip就诊号
				queryQueNo(temp);
				//add by huangtt 20160621 判断VIP无号时不进行挂号保存
				if(reg.getQueNo() == 0){
					return;
				}
				// ===zhangp 20120629 end
				reg.setVipFlg(true); // vip就诊
				TParm regParm = reg.getParm();
				String admDate = StringTool.getString(reg.getAdmDate(),
						"yyyyMMdd");
				regParm.setData("ADM_DATE", admDate);
				// =========pangben 2012-7-1 start重号问题
				if (!onSaveQueNo(regParm)) {
					messageBox("取得就诊号失败");
					return;
				}
				// =========pangben 2012-7-1 stop
				if ("N".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(true); // 报到
				} else if ("Y".endsWith(reg.getApptCode())) {
					reg.setArriveFlg(false); // 不报到
				}
				startTime = temp.getValue("START_TIME", 0);
				reg.setRegAdmTime(startTime);
			} else if (getValueString("REGMETHOD_CODE").equals("D")) {
				messageBox("请挂VIP诊");
				return;
			}
		}
		if(!onCheckQueNo())
			return;
		// =====zhangp 20120301 modify start
		if ("A".equals(getValue("REGMETHOD_CODE").toString())) {

			if (!onInsEkt(payWay, null)) {
				// ===========pangben 2012-7-1 操作失败回滚VIP就诊号码
//				TParm regParm = reg.getParm();
//				if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
//					this.messageBox("撤销VIP就诊号码失败,请联系信息中心");
//				}
				return;
			}
		}
		if (!onSaveRegOne(payWay, ins_exe)) {
			// ===========pangben 2012-7-1 操作失败回滚VIP就诊号码
//			TParm regParm = reg.getParm();
//			if (!REGTool.getInstance().concelVIPQueNo(regParm)) {
//				this.messageBox("撤销VIP就诊号码失败,请联系信息中心");
//			}
			if(tjINS){
				TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
						"deleteOldData", insParm);
				if (result.getErrCode() < 0) {
					err(result.getErrCode() + " " + result.getErrText());
					this.messageBox("撤销医保卡数据操作失败,请联系信息中心");
					// return result;
				}
				
			}
			return;
		}
		if (ins_exe) { // 医保卡操作执行成功 ,删除在途状态数据 修改票据号
			if (!updateINSPrintNo(reg.caseNo(), "REG"))
				return;
			if (!updateReceiptNo(reg.caseNo(),"REG"))
				return;
		}
		// 打票数据
		TParm result = onPrintParm();
		if ("Y".endsWith(reg.getApptCode())) {
			this.messageBox("预约成功!");
			
			if (!this.getValue("REGMETHOD_CODE").equals("A")) {
				//huangtt start 20131101  预约成功之后发送短信
				TParm parm = new TParm();
		        parm.addData("MrNo", this.getValueString("MR_NO"));
		        parm.addData("Name", this.getValueString("PAT_NAME"));
		        TComboBox sessionCode = (TComboBox) getComponent("SESSION_CODE");
		        TTextFormat drCode = (TTextFormat) getComponent("DR_CODE");
		        String sqlSC = "SELECT SESSION_DESC FROM REG_SESSION WHERE SESSION_CODE = '"+this.getValue("SESSION_CODE")+"'";
		        TParm parmSession = new TParm(TJDODBTool.getInstance().select(sqlSC));
		        
		        String content = "您已预约成功"+
		        				this.getValue("ADM_DATE").toString().substring(0, 10).replace("-", "/")+" "+
		        				parmSession.getValue("SESSION_DESC", 0) +
		        				"第"+reg.getQueNo()+"号"+
		        				drCode.getText() +"医生的门诊，仅限"+this.getValueString("PAT_NAME")+"本人，如需取消，请提前一天拨打服务电话4001568568，为了保证您准时就诊，您需提前办理挂号手续";
//		       this.messageBox(content);
		        parm.addData("Content", content);
		        parm.addData("TEL1", this.getValueString("TEL_HOME"));
		        TParm  r =TIOM_AppServer.executeAction(
						"action.reg.REGAction", "orderMessage", parm);
		        
				//huangtt end 20131101
			}
			// 调用排队叫号
			/**
			 * if (!"true".equals(callNo("REG", ""))) { this.messageBox("叫号失败");
			 * }
			 **/
			this.onClear();
			return;
		}
		// ================pangben modify 20110817 记账单位不存在执行打票
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			// 判断当诊病患打票
			if ("N".endsWith(reg.getApptCode())) {
				// 医疗卡打票
				onPrint(result);
				BilInvoice invoice = new BilInvoice();
				invoice = invoice.initBilInvoice("REG");
				// 初始化下一票号
				// ===zhangp 20120306 modify start
				if (BILTool.getInstance().compareUpdateNo("REG",
						Operator.getID(), Operator.getRegion(),
						invoice.getUpdateNo())) {
					setValue("NEXT_NO", invoice.getUpdateNo());
				} else {
					messageBox("票据已用完");
					clearValue("NEXT_NO");
				}
				// ===zhangp 20120306 modify end
				// 调用排队叫号
				if (!"true".equals(callNo("REG", reg.caseNo()))) {
					this.messageBox("叫号失败");
				}

			}
			// 不打票执行记账操作
		} else {
			// =================pangben 20110817
			TParm parm = new TParm();
			parm.setData("RECEIPT_NO", reg.getRegReceipt().getReceiptNo()); // 收据号
			parm.setData("CONTRACT_CODE", this.getValue("CONTRACT_CODE")); // 记账单位
			parm.setData("ADM_TYPE", reg.getRegReceipt().getAdmType()); // 门急住别
			parm.setData("REGION_CODE", Operator.getRegion()); // 院区
			parm.setData("CASHIER_CODE", Operator.getID()); // 收费人员
			parm.setData("CHARGE_DATE", SystemTool.getInstance().getDate()); // 收费日期时间
			parm.setData("RECEIPT_TYPE", "REG"); // 票据类型：REG 、OPB
			parm.setData("DATA_TYPE", "REG"); // 扣款来源 REG OPB HRM
			parm.setData("CASE_NO", reg.caseNo()); // 就诊号
			parm.setData("MR_NO", reg.getPat().getMrNo());
			parm.setData("AR_AMT", reg.getRegReceipt().getArAmt()); // 应缴金额
			parm.setData("BIL_STATUS", "1"); // 记账状态1 记账 2 结算完成写入 =1
			// caowl 20130307 start
			String sqls = "SELECT * FROM BIL_CONTRACTD WHERE MR_NO = '"
					+ reg.getPat().getMrNo() + "' AND CONTRACT_CODE = '"
					+ this.getValue("CONTRACT_CODE") + "'";
//			System.out.println("卡的条件" + sqls);
			TParm parms = new TParm(TJDODBTool.getInstance().select(sqls));
			if (parms.getCount() <= 0) {
				this.messageBox("此病人不属于该合同单位，请确认！");
				return;
			}
			// caowl 20130307 end
			// 记账单位缴费时候
			// update =2
			parm.setData("RECEIPT_FLG", "1"); // 状态：1 收费 2 退费
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			TParm result1 = TIOM_AppServer.executeAction(
					"action.bil.BILContractRecordAction", "insertRecode", parm);
			if (result1.getErrCode() < 0) {
				err(result1.getErrCode() + " " + result1.getErrText());
				this.messageBox("挂号失败");
			} else
				this.messageBox("挂号成功,已经记账");
		}
//		this.messageBox("11");
		//  ==add by zhanglei 20171116  增加挂号时若挂号身份是特殊身份弹出验证码
//		if(getREGSpecialFlg().equals("Y")){
//			String sql1 = "UPDATE REG_AUTH_CODE SET "+
//					  " CASE_NO = '"+ reg.caseNo() +"'" +
//					  " WHERE AUTH_CODE = '"+ authCode +"'";
////			this.messageBox(sql1);
////			System.out.println("66666666666666" + sql1);
//			TParm a = new TParm(TJDODBTool.getInstance().update(sql1));
//			if(a.getErrCode()<0){
////				this.messageBox("VVIP就诊号更新失败更新失败");
//				System.out.println("VVIP就诊号更新失败更新失败");
//			}
//		}
		// 解锁病患信息
//		if (PatTool.getInstance().unLockPat(pat.getMrNo()))
			this.onClear();
		initSession();
		pat = null;
		
		
	}

	/**
	 * 打票数据
	 * 
	 * @return TParm
	 */
	private TParm onPrintParm() {
		// 门诊打票操作
		TParm result = PatAdmTool.getInstance().getRegPringDate(reg.caseNo(),
				"");
		// zhangp 20120206
		result.setData("MR_NO", "TEXT", this.getValue("MR_NO"));
		result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		result.setData("PAY_WAY", this.getValue("PAY_WAY")); // 支付方式
		result.setData("INS_SUMAMT", ins_amt);
		result.setData("ACCOUNT_AMT_FORREG", accountamtforreg);// 个人账户
		return result;
	}

	/**
	 * 医保医疗操作 共用部分
	 * 
	 * @param payWay
	 *            String
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	private boolean onInsEkt(String payWay, String caseNo) {
		
		if (payWay.equals("PAY_MEDICAL_CARD") || payWay.equals("PAY_INS_CARD")){
			TParm ektSumExeParm= new TParm();
			ektSumExeParm.setData("payWay", payWay);
			ektSumExeParm.setData("caseNo", caseNo);
			
			EktParam ektParam = new EktParam();
			ektParam.setType("REG");
			ektParam.setRegPatAdmControl(this);
			ektParam.setReg(reg);
			ektParam.setPat(pat);
			ektParam.setOrderParm(ektSumExeParm);
			
			EktTradeContext ektTradeContext = new EktTradeContext(ektParam);
			try {
				
				//创建参数，打开收费界面，执行收费  
				ektTradeContext.openClient(reg);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
			

		if(payWay.equals("PAY_MEDICAL_CARD")){
			if (null != greenParm
					&& null != greenParm.getValue("GREEN_FLG")
					&& greenParm.getValue("GREEN_FLG").equals("Y")) {
				// 使用绿色通道金额
				reg.getRegReceipt().setPayMedicalCard(
						TypeTool.getDouble(greenParm.getDouble("EKT_USE")));
				reg.getRegReceipt().setOtherFee1(
						greenParm.getDouble("GREEN_USE"));
			}
		}
		

//		// 医疗卡支付
//		if (payWay.equals("PAY_MEDICAL_CARD")) {
//			// 生成CASE_NO 因为医疗卡需要CASE_NO 所以在用医疗卡支付的时候先生成CASE_NO
//			if ("N".endsWith(reg.getApptCode())) {
//				// System.out.println("222222222222222222");
//				if (null != caseNo && caseNo.length() > 0) {
//					reg.setCaseNo(caseNo);
//				} else {
//					reg.setCaseNo(SystemTool.getInstance().getNo("ALL", "REG",
//							"CASE_NO", "CASE_NO"));
//				}
//				// 保存医疗卡
//				if (!this.onEktSave("Y")) {
//					System.out.println("!!!!!!!!!!!医疗卡保存错误");
//					return false;
//				}
//				if (null != greenParm
//						&& null != greenParm.getValue("GREEN_FLG")
//						&& greenParm.getValue("GREEN_FLG").equals("Y")) {
//					// 使用绿色通道金额
//					reg.getRegReceipt().setPayMedicalCard(
//							TypeTool.getDouble(greenParm.getDouble("EKT_USE")));
//					reg.getRegReceipt().setOtherFee1(
//							greenParm.getDouble("GREEN_USE"));
//				}
//			}
//		}
//		if (payWay.equals("PAY_INS_CARD")) {
//			TParm result = null;
//			// 医保卡支付
//			result = onSaveRegTwo(payWay, ins_exe, caseNo);
//			if (null == result) {
//				return false;
//			}
//			ins_exe = result.getBoolean("INS_EXE");
//			ins_amt = result.getDouble("INS_AMT");
//			accountamtforreg = result.getDouble("ACCOUNT_AMT_FORREG");
//		}

		if (ins_exe) {
			// 执行医保 判断在途状态
			TParm runParm = new TParm();
			runParm.setData("CASE_NO", reg.caseNo());
			runParm.setData("EXE_USER", Operator.getID());
			runParm.setData("EXE_TERM", Operator.getIP());
			runParm.setData("EXE_TYPE", "REG");
			runParm = INSRunTool.getInstance().queryInsRun(runParm);
			if (runParm.getErrCode() < 0) {
				return false;
			}
			if (runParm.getCount("CASE_NO") <= 0) {
				// 没有查询到数据，说明在途状态有问题
				return false;
			} else {
				if (runParm.getInt("STUTS", 0) != 1) { // STUTS :1.在途 2.成功
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 执行保存REG_PATADM BIL_REG_RECP BIL_INVRCP 表操作(医保执行操作)
	 * 
	 * @param payWay
	 *            String
	 * @param ins_exe
	 *            boolean
	 * @return boolean
	 */
	private boolean onSaveRegOne(String payWay, boolean ins_exe) {
		TParm result = new TParm();
		if (!reg.onNew()) {
			this.messageBox("挂号失败");
//			if (payWay.equals("PAY_MEDICAL_CARD")) { // 医疗卡支付
//				result = new TParm();
//				result.setData("CURRENT_BALANCE", ektOldSum);
//				result.setData("MR_NO", p3.getValue("MR_NO"));
//				result.setData("SEQ", p3.getValue("SEQ"));
//				result = EKTIO.getInstance().TXwriteEKTATM(result,
//						reg.getPat().getMrNo()); // 回写医疗卡金额
//				if (result.getErrCode() < 0)
//					System.out.println("err:" + result.getErrText());
//				// 医疗卡挂号出现问题撤销操作
//				cancleEKTData();
//			}
			if (payWay.equals("PAY_INS_CARD")) { // 医保卡支付
				if (!ins_exe) { // 医保卡操作 ,删除在途状态数据
					return false;
				}
				result = new TParm();
				insParm.setData("EXE_TYPE", "REG");
				// 执行撤销操作----需要实现
//				if (tjINS) { // 医疗卡操作
//					result.setData("CURRENT_BALANCE", ektOldSum);
//					result.setData("MR_NO", p3.getValue("MR_NO"));
//					result.setData("SEQ", p3.getValue("SEQ"));
//					result = EKTIO.getInstance().TXwriteEKTATM(result,
//							p3.getValue("MR_NO")); // 回写医疗卡金额
//					if (result.getErrCode() < 0)
//						System.out.println("err:" + result.getErrText());
//					// 医疗卡挂号出现问题撤销操作
//					cancleEKTData();
//				}

			}
			// EKTIO.getInstance().unConsume(tredeNo, this);
			return false;
		}
		return true;
	}

//	/**
//	 * 执行保存 医保表数据操作
//	 * 
//	 * @param payWay
//	 *            String
//	 * @param ins_amt
//	 *            double
//	 * @param ins_exe
//	 *            boolean
//	 * @param caseNo
//	 *            String
//	 * @return TParm
//	 */
//	private TParm onSaveRegTwo(String payWay, boolean ins_exe, String caseNo) {
//		double ins_amtTemp = 0.00;// 医保金额
//		TParm result = new TParm();
//		if (payWay.equals("PAY_INS_CARD")) {
//			// 查询是否存在特批款操作
//			if (null == caseNo || caseNo.length() <= 0) {
//				caseNo = SystemTool.getInstance().getNo("ALL", "REG",
//						"CASE_NO", "CASE_NO"); // 获得就诊号
//			}
//			TParm parm = new TParm();
//			parm.setData("CASE_NO", caseNo);
//			parm = PatAdmTool.getInstance().selEKTByMrNo(parm);
//			if (parm.getErrCode() < 0) {
//				this.messageBox("E0005");
//				return null;
//			}
//
//			if (parm.getDouble("GREEN_BALANCE", 0) > 0) {
//				this.messageBox("此就诊病患使用特批款,不可以使用医保操作");
//				return null;
//			}
//			if (this.getValue("REG_CTZ1").toString().length() <= 0) {
//				this.messageBox("请选择医保卡就诊类型");
//				return null;
//			}
//			// 需要保存到REG_PATADM数据库表中1.城职普通
//			// 2.城职门特 3.城居门特
//			// 医保卡挂号
//			// 获得挂号费用代码，费用金额，费用
//			//获得当前时间
//			String sysdate =StringTool.getString(SystemTool.
//					getInstance().getDate(),"yyyyMMddHHmmss");
//			String regFeesql = "SELECT A.ORDER_CODE,B.ORDER_DESC,B.NHI_CODE_O, B.NHI_CODE_E, B.NHI_CODE_I,B.OWN_PRICE ,"
//					+ "B.OWN_PRICE AS AR_AMT ,'1' AS DOSAGE_QTY, '0' AS TAKE_DAYS, '' AS NS_NOTE, '' AS SPECIFICATION,'' AS DR_CODE,A.RECEIPT_TYPE,"
//					+ "C.DOSE_CODE FROM REG_CLINICTYPE_FEE A,SYS_FEE_HISTORY B,PHA_BASE C WHERE A.ORDER_CODE=B.ORDER_CODE(+) "
//					+ "AND A.ORDER_CODE=C.ORDER_CODE(+) AND  A.ADM_TYPE='"
//					+ admType
//					+ "'"
//					+ " AND A.CLINICTYPE_CODE='"
//					+ getValue("CLINICTYPE_CODE") + "'" 
//					+ " AND '" + sysdate+ "' BETWEEN B.START_DATE AND B.END_DATE";
//
//			// 挂号费
//			double reg_fee = BIL.getRegDetialFee(admType, TypeTool
//					.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
//					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
//							.getString(getValue("REG_CTZ2")), TypeTool
//							.getString(getValue("CTZ3_CODE")), this
//							.getValueString("SERVICE_LEVEL") == null ? ""
//							: this.getValueString("SERVICE_LEVEL"));
//			// 诊查费 计算折扣
//			double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
//					.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE",
//					TypeTool.getString(getValue("REG_CTZ1")), TypeTool
//							.getString(getValue("REG_CTZ2")), TypeTool
//							.getString(getValue("CTZ3_CODE")), this
//							.getValueString("SERVICE_LEVEL") == null ? ""
//							: this.getValueString("SERVICE_LEVEL"));
//
//			// System.out.println("regFeesql:::::" + regFeesql);
//			TParm regFeeParm = new TParm(TJDODBTool.getInstance().select(
//					regFeesql));
//			if (regFeeParm.getErrCode() < 0) {
//				err(regFeeParm.getErrCode() + " " + regFeeParm.getErrText());
//				this.messageBox("医保执行操作失败");
//				return null;
//			}
//			for (int i = 0; i < regFeeParm.getCount(); i++) {
//				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("REG_FEE")) {
//					regFeeParm.setData("RECEIPT_TYPE", i, reg_fee);
//					regFeeParm.setData("AR_AMT", i, reg_fee);
//				}
//				if (regFeeParm.getValue("RECEIPT_TYPE", i).equals("CLINIC_FEE")) {
//					regFeeParm.setData("RECEIPT_TYPE", i, clinic_fee);
//					regFeeParm.setData("AR_AMT", i, clinic_fee);
//				}
//			}
//			// System.out.println("regFeesql::" + regFeesql);
//			result = TXsaveINSCard(regFeeParm, caseNo); // 执行操作
//			// System.out.println("RESULT::::" + result);
//			if (null == result)
//				return null;
//			if (result.getErrCode() < 0) {
//				err(result.getErrCode() + " " + result.getErrText());
//				this.messageBox("医保执行操作失败");
//				return null;
//			}
//			// 24医保卡支付(REG_RECEIPT)
//			if (null != result.getValue("MESSAGE_FLG")
//					&& result.getValue("MESSAGE_FLG").equals("Y")) {
//				System.out.println("医保卡出现错误现金收取");
//			} else {
//				// 医保支付
//				ins_amtTemp = tjInsPay(result, regFeeParm);
//				ins_exe = true; // 医保执行操作 需要判断在途状态
//				reg.setInsPatType(insParm.getValue("INS_TYPE")); // 就诊医保类型
//				reg.setConfirmNo(insParm.getValue("CONFIRM_NO")); // 医保就诊号
//				// CONFIRM_NO
//			}
//
//		}
//		result.setData("INS_AMT", ins_amtTemp);
//		result.setData("INS_EXE", ins_exe);
//
//		return result;
//	}

	/**
	 * 保存操作 汇总数据
	 * 
	 * @param payWay
	 *            支付类别
	 */
	private void onSaveRegParm(String payWay) {
		// 20现金支付(REG_RECEIPT)
		if (payWay.equals("PAY_CASH")) {
			reg.getRegReceipt()
					.setPayCash(TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);

		}
		// 21银行卡支付(REG_RECEIPT)
		if (payWay.equals("PAY_BANK_CARD")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
		}
		// 22支票支付(REG_RECEIPT)
		if (payWay.equals("PAY_CHECK")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(
					TypeTool.getDouble(getValue("FeeY")));
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			// 24备注(写支票号)(REG_RECEIPT)
			reg.getRegReceipt().setRemark(
					TypeTool.getString(getValue("REMARK")));

		}
		// 22记账支付(REG_RECEIPT)
		if (payWay.equals("PAY_DEBIT")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayMedicalCard(0.00);
			reg.getRegReceipt().setPayDebit(
					TypeTool.getDouble(getValue("FeeY")));
		}
		// 23医疗卡支付(REG_RECEIPT)
		if (payWay.equals("PAY_MEDICAL_CARD")) {
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayBankCard(0.00);
			reg.getRegReceipt().setPayCheck(0.00);
			reg.getRegReceipt().setPayDebit(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY")));
		}
	}

	/**
	 * 保存操作 汇总数据
	 * 
	 * @return boolean =======pangben 2012-7-1添加参数 区分报到操作 flg=false 报到 执行 UPDATE
	 *         QUE_NO 操作
	 */
	private boolean onSaveRegParm(boolean flg) {
		String regionCode = TypeTool.getString(getValue("REGION_CODE")); // 区域
		String ctz1Code = TypeTool.getString(getValue("REG_CTZ1")); // 身份1
		String ctz2Code = TypeTool.getString(getValue("REG_CTZ2")); // 身份2
		String ctz3Code = TypeTool.getString(getValue("CTZ3_CODE")); // 身份3
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // 支付类别
		reg.setAdmType(admType);
		// 4区域
		reg.setRegion(regionCode);
		// 5看诊日期
		reg.setAdmDate(TypeTool.getTimestamp(getValue("ADM_DATE")));
		// 6挂号操作日期
		reg.setRegDate(SystemTool.getInstance().getDate());
		// 7时段
		reg.setSessionCode(TypeTool.getString(getValue("SESSION_CODE")));
		// 8诊区
		reg.setClinicareaCode((PanelRoomTool.getInstance()
				.getAreaByRoom(TypeTool.getString(getValue("CLINICROOM_NO"))))
				.getValue("CLINICAREA_CODE", 0));
		// 9诊室
		reg.setClinicroomNo(TypeTool.getString(getValue("CLINICROOM_NO")));
		// 10号别
		reg.setClinictypeCode(TypeTool.getString(getValue("CLINICTYPE_CODE")));
		// System.out.println("就诊日期"+reg.getAdmDate());
		// System.out.println("当前日期"+SystemTool.getInstance().getDate());
		// 19挂号方式
		reg.setRegmethodCode(TypeTool.getString(getValue("REGMETHOD_CODE")));
		String admDate = StringTool.getString(reg.getAdmDate(), "yyyyMMdd");
		if (RegMethodTool.getInstance().selPrintFlg(
				this.getValueString("REGMETHOD_CODE"))) {
			// 显示下一票号
			if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
					|| reg.getRegReceipt().getBilInvoice().getUpdateNo()
							.length() == 0) {
				this.messageBox("尚未开账");
				return false;
			}
		}
		/*if ("Y".equals(reg.getApptCode())) {
			if (this.getPopedem("LEADER")) {
				this.messageBox("非组长不能预约!");
				return false;
			}
		}*/
		// 17预约当诊
		// =========pangben 2012-7-1 区分报到 和当日挂号逻辑
		if (flg) {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("预约");
				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
					this.messageBox("你选择的是现场挂号,所以日期必须为今天!");
					return false;
				}
				reg.setApptCode("Y");
				// 12预约时间
				reg.setRegAdmTime(startTime);
			} else {
				// System.out.println("当诊");
				reg.setApptCode("N");
			}
			// 18初复诊
			if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C")))
				// 初诊
				reg.setVisitCode("0");
			else {
				// 复诊
				reg.setVisitCode("1");
			}
			// 11根据VIP取值，得到就诊号
			if (!tableFlg) {
				if (!onSaveParm(admDate))
					return false;
			} else {
				// ===========pangben 2012-7-1 修改 UPDATE 班表que_no 只操作一次增加就诊号码
				TTable table1 = (TTable) this.getComponent("Table1");
				TParm parm = table1.getParmValue();
				TParm temp = parm.getRow(selectRow); // 获得第一个页签数据
				String type = temp.getValue("TYPE"); // VIP 和一般
				if (type.equals("VIP")) {
					// VIP诊挂号
				} else {
					// 普通诊挂号
					int queNo = SchDayTool.getInstance().selectqueno(
							reg.getRegion(),
							reg.getAdmType(),
							TypeTool.getString(reg.getAdmDate()).replaceAll(
									"-", "").substring(0, 8),
							reg.getSessionCode(), reg.getClinicroomNo());
					if (queNo == 0) {
						this.messageBox("已无就诊号!");
						return false;
					}
					reg.setQueNo(queNo);
					if (reg.getQueNo() == -1) {
						// 已无号不能挂号
						this.messageBox("E0017");
						return false;
					}
					// ==========pangben 2012-6-18 修改重号问题
					TParm regParm = reg.getParm();
					regParm.setData("ADM_DATE", admDate);
					if (onSaveQueNo(regParm)) {
						// return true;
					} else {
						return false;
					}
				}
			}
		} else {
			if (StringTool.getDateDiffer(reg.getAdmDate(), SystemTool
					.getInstance().getDate()) > 0) {
				// System.out.println("预约");
				if ("A".equals(getValue("REGMETHOD_CODE").toString())) {
					this.messageBox("你选择的是现场挂号,所以日期必须为今天!");
					return false;
				}
			} else {
				reg.setApptCode("N");
			}
		}

		// 13科室
		reg.setDeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 14医师
		reg.setDrCode(TypeTool.getString(getValue("DR_CODE")));
		// 15实看科别(默认科室)
		reg.setRealdeptCode(TypeTool.getString(getValue("DEPT_CODE")));
		// 16实看医师(默认医师)
		reg.setRealdrCode(TypeTool.getString(getValue("DR_CODE")));
		// 20身份折扣1
		reg.setCtz1Code(ctz1Code);
		// 21身份折扣2
		reg.setCtz2Code(ctz2Code);
		// 22身份折扣3
		reg.setCtz3Code(ctz3Code);

		// 23转诊院所
		reg.setTranhospCode("");
		// 24检伤号
		reg.setTriageNo("");
		// 25记账单位
		reg.setContractCode(TCM_Transform.getString(getValue("CONTRACT_CODE")));
		// 26报到注记
		if (getValue("REGMETHOD_CODE").equals("A"))
			reg.setArriveFlg(true);
		else
			reg.setArriveFlg(false);
		// 27退挂人员
		// reg.setRegcanUser();
		// 28退挂日期
		// reg.setRegcanDate();
		// 29挂号院区
		reg.setAdmRegion(regionCode);
		// 30预防保健时程(计划免疫)
		// reg.setPreventSchCode();
		// 31DRG码
		// reg.setDrgCode();
		// 32发热注记
		// reg.setHeatFlg();
		// 33就诊进度
		reg.setAdmStatus("1");
		// 34报告状态
		reg.setReportStatus("1");
		// 35体重
		// reg.setWeight();
		// 36身高
		// reg.setHeight();
		if (admType.equals("E")){
			reg.setTriageNo(getValue("TRIAGE_NO").toString()); //add by huangtt 20151020 检伤号
			
			if(getValue("TRIAGE_NO").toString().length() == 0){
				this.messageBox("该急诊病人没有填写检伤号！！！");
			}
			
			reg.setErdLevel(getValue("ERD_LEVEL").toString());
			reg.setArriveDate(TypeTool.getTimestamp(getValue("ARRIVE_DATE")));
		}

		// 门急诊收据(For bill),REG_RECEIPT对象
		// reg.createReceipt();
		// 3门急住别(REG_RECEIPT)
		reg.getRegReceipt().setAdmType(admType);
		// 4区域(REG_RECEIPT)
		reg.getRegReceipt().setRegion(regionCode);
		// 5ID号(REG_RECEIPT)
		reg.getRegReceipt().setMrNo(TypeTool.getString(getValue("MR_NO")));
		// 6冲销收据号(REG_RECEIPT)
		// reg.getRegReceipt().setResetReceiptNo("");
		// 8记账日期(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9收费日期(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10收据打印日期(REG_RECEIPT)
		// ===================pangben modify 20110818 记账标记，PRINT_DATE 栏位为空时，进行记账
		if (this.getValueString("CONTRACT_CODE").trim().length() <= 0) {
			reg.getRegReceipt()
					.setPrintDate(SystemTool.getInstance().getDate());
			// 7收据印刷号(REG_RECEIPT)
			reg.getRegReceipt().setPrintNo(
					reg.getRegReceipt().getBilInvoice().getUpdateNo());

		}

		// 11挂号费(REG_RECEIPT)
		// ======================pangben modify 20110815
		onSaveParm(ctz1Code, ctz2Code, ctz3Code);
		// 12折扣前挂号费(REG_RECEIPT)
		reg.getRegReceipt().setRegFeeReal(
				TypeTool.getDouble(getValue("REG_FEE")));

		// 14折扣前诊查费(REG_RECEIPT)
		reg.getRegReceipt().setClinicFeeReal(
				TypeTool.getDouble(getValue("CLINIC_FEE")));
		// 15附加费(REG_RECEIPT)
		// reg.getRegReceipt().setSpcFee(0.00);
		// 16其它费用1(REG_RECEIPT)
		// reg.getRegReceipt().setOtherFee1(0.00);
		// 17其它费用2(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee2(0.00);
		// 18其它费用3(REG_RECEIPT)
		// reg.getRegReceipt().setotherFee3(0.00);
		// 19应收金额(REG_RECEIPT)
		reg.getRegReceipt().setArAmt(TypeTool.getDouble(getValue("FeeY")));
		onSaveRegParm(payWay);
		// 24医保卡支付(REG_RECEIPT)
		// reg.getRegReceipt().setPayInsCard(0.00);
		// 26门急诊财政记账(REG_RECEIPT)
		// reg.getRegReceipt().setPayIns(0.00);
		// 28收款员编码(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		// 29结帐标志(REG_RECEIPT)
		// reg.getRegReceipt().setAccountFlg("");
		// 30日结报表号(REG_RECEIPT)
		// reg.getRegReceipt().setAccountSeq("");
		// 31日结人员(REG_RECEIPT)
		// reg.getRegReceipt().setAccountUser(Operator.getName());
		// 32结账日期(REG_RECEIPT)
		// reg.getRegReceipt().setAccountDate(SystemTool.getInstance().getDate());
		// 服务等级
		reg.setServiceLevel(this.getValueString("SERVICE_LEVEL"));
		// 票据主档BilInvoice(For bil),BIL_INVOICE对象
		// reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// reg.getRegReceipt().getBilInvoice().setCashierCode(Operator.getID());
		// //操作人员
		// reg.getRegReceipt().getBilInvoice().setStartValidDate();
		// reg.getRegReceipt().getBilInvoice().setEndValidDate();
		// reg.getRegReceipt().getBilInvoice().setStatus("1");

		// 票据明细档BILInvrcpt(For bil),BIL_INVRCP对象
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1票据类型(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2发票号码(BIL_INVRCP)

		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // 操作人员(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // 总金额(BIL_INVRCP)
		// reg.getRegReceipt().getBilInvrcpt().setCancelFlg();
		// reg.getRegReceipt().getBilInvrcpt().setCancelUser();
		// reg.getRegReceipt().getBilInvrcpt().setCancelDate();
		// 判断初始化票据
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		
		return true;
	}

	/**
	 * 保存操作统计数据
	 * 
	 * @param admDate
	 *            String
	 * @return boolean flg =false 报到操作不执行 UPDATE QUE_NO 操作
	 */
	private boolean onSaveParm(String admDate) {
		if (SchDayTool.getInstance().isVipflg(reg.getRegion(),
				reg.getAdmType(), admDate, reg.getSessionCode(),
				reg.getClinicroomNo())) {
			int row = (Integer) callFunction("UI|Table2|getClickedRow");
			if (row < 0)
				return false;
			// 拿到table控件
			TTable table2 = (TTable) callFunction("UI|table2|getThis");
			TParm data = table2.getParmValue();
			setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
					data, row);
			// 20090217 新方法 -------end---------
			// =======pangben 2012-7-31 修改查询是否已经vip占号
			int queNoVIP = TypeTool.getInt(table2.getValueAt(row, table2
					.getColumnIndex("QUE_NO")));
			String vipSql = "SELECT QUE_NO,QUE_STATUS FROM REG_CLINICQUE "
					+ "WHERE ADM_TYPE='"
					+ reg.getAdmType()
					+ "' AND ADM_DATE='"
					+ TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8) + "'" + " AND SESSION_CODE='"
					+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
					+ reg.getClinicroomNo() + "' AND  QUE_NO='" + queNoVIP
					+ "' AND QUE_STATUS='N'";
			TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
			if (result.getErrCode() < 0 || result.getCount() <= 0) {
				this.messageBox("已占号!");
				// 初始化带入VIP班表
				onQueryVipDrTable();
				return false;
			}
			if (queNoVIP == 0) {
				this.messageBox("已无VIP就诊号!");
				return false;
			}
			reg.setQueNo(queNoVIP);

			reg.setVipFlg(true);
			if (reg.getQueNo() == -1) {
				this.messageBox("E0017");
				return false;
			}

		} else {
			int queNo = SchDayTool.getInstance().selectqueno(
					reg.getRegion(),
					reg.getAdmType(),
					TypeTool.getString(reg.getAdmDate()).replaceAll("-", "")
							.substring(0, 8), reg.getSessionCode(),
					reg.getClinicroomNo());
			if (queNo == 0) {
				this.messageBox("已无就诊号!");
				return false;
			}
			reg.setQueNo(queNo);
			if (reg.getQueNo() == -1) {
				// 已无号不能挂号
				this.messageBox("E0017");
				return false;
			}
		}
		// =========pangben 2012-6-18
		TParm regParm = reg.getParm();
		regParm.setData("ADM_DATE", admDate);
		if (onSaveQueNo(regParm)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 保存操作统计数据
	 * 
	 * @param ctz1Code
	 *            String
	 * @param ctz2Code
	 *            String
	 * @param ctz3Code
	 *            String
	 */
	private void onSaveParm(String ctz1Code, String ctz2Code, String ctz3Code) {
		if (!feeShow) { // 判断是否是医保中心获得的费用====南京医保使用，现在feeShow=false 不会执行
			// feeShow=true
			reg.getRegReceipt().setRegFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")), "REG_FEE",
							ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL")));
			// 13诊查费(REG_RECEIPT)
			reg.getRegReceipt().setClinicFee(
					BIL.getRegDetialFee(admType, TypeTool
							.getString(getValue("CLINICTYPE_CODE")),
							"CLINIC_FEE", ctz1Code, ctz2Code, ctz3Code,
							this.getValueString("SERVICE_LEVEL") == null ? ""
									: this.getValueString("SERVICE_LEVEL"))
									+PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee")); //add by huangtt 20170505 门特诊察费);

		} else {
			reg.getRegReceipt().setRegFee(
					TypeTool.getDouble(getValue("REG_FEE")));
			reg.getRegReceipt().setClinicFee(
					TypeTool.getDouble(getValue("CLINIC_FEE")));
		}
	}

	/**
	 * 医疗卡挂号出现问题撤销操作
	 */
	private void cancleEKTData() {
		// 医疗卡挂号出现问题撤销操作
		TParm oldParm = new TParm();
		oldParm.setData("BUSINESS_NO", businessNo);
		oldParm.setData("TREDE_NO", tredeNo);
		TParm result = TIOM_AppServer.executeAction("action.ins.EKTAction",
				"deleteRegOldData", oldParm);
		// if (result.getErrCode() < 0)
		// System.out.println("err:" + result.getErrText());
	}

	/**
	 * 泰心挂号排队叫号
	 * 
	 * @param type
	 *            String
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String callNo(String type, String caseNo) {
		TParm inParm = new TParm();
		// System.out.println("========caseNo=========="+caseNo);
		String sql = "SELECT CASE_NO, A.MR_NO,A.CLINICROOM_NO,A.ADM_TYPE,A.QUE_NO,A.REGION_CODE,";
		sql += "TO_CHAR (ADM_DATE, 'YYYY-MM-DD') ADM_DATE,A.SESSION_CODE,";
		sql += "A.CLINICAREA_CODE, A.CLINICROOM_NO, QUE_NO, REG_ADM_TIME,";
		sql += "B.DEPT_CHN_DESC, DR_CODE, REALDEPT_CODE, REALDR_CODE, APPT_CODE,";
		sql += "VISIT_CODE, REGMETHOD_CODE, A.CTZ1_CODE, A.CTZ2_CODE, A.CTZ3_CODE,";
		sql += "C.USER_NAME,D.CLINICTYPE_DESC, F.CLINICROOM_DESC, E.PAT_NAME,";
		sql += "TO_CHAR (E.BIRTH_DATE, 'YYYY-MM-DD') BIRTH_DATE, G.CHN_DESC SEX,H.SESSION_DESC";
		sql += " FROM REG_PATADM A,";
		sql += "SYS_DEPT B,";
		sql += "SYS_OPERATOR C,";
		sql += "REG_CLINICTYPE D,";
		sql += "SYS_PATINFO E,";
		sql += "REG_CLINICROOM F,";
		sql += "(SELECT * FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX') G,";
		sql += "REG_SESSION H";
		sql += " WHERE CASE_NO = '" + caseNo + "'";
		sql += " AND A.DEPT_CODE = B.DEPT_CODE(+)";
		sql += " AND A.DR_CODE = C.USER_ID(+)";
		sql += " AND A.CLINICTYPE_CODE = D.CLINICTYPE_CODE(+)";
		sql += " AND A.MR_NO = E.MR_NO(+)";
		sql += " AND A.CLINICROOM_NO = F.CLINICROOM_NO(+)";
		sql += " AND E.SEX_CODE = G.ID";
		sql += " AND A.SESSION_CODE=H.SESSION_CODE";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		// 挂号日期
		String sendString = result.getValue("ADM_DATE", 0) + "|";
		// 看诊科室
		sendString += result.getValue("DEPT_CHN_DESC", 0) + "|";
		// 医师代码
		sendString += result.getValue("DR_CODE", 0) + "|";
		// 医师姓名
		sendString += result.getValue("USER_NAME", 0) + "|";
		// 号别
		sendString += result.getValue("CLINICTYPE_DESC", 0) + "|";

		// 诊间
		sendString += result.getValue("CLINICROOM_DESC", 0) + "|";

		// 患者病案号
		sendString += result.getValue("MR_NO", 0) + "|";

		// 患者姓名
		sendString += result.getValue("PAT_NAME", 0) + "|";
		// 患者性别

		sendString += result.getValue("SEX", 0) + "|";
		// 患者生日
		sendString += result.getValue("BIRTH_DATE", 0) + "|";

		// 看诊序号
		sendString += result.getValue("QUE_NO", 0) + "|";

		// System.out.println("==adm date=="+result.getValue("ADM_DATE",0));

		String noSql = "SELECT QUE_NO,MAX_QUE FROM REG_SCHDAY";
		noSql += " WHERE REGION_CODE ='" + result.getValue("REGION_CODE", 0)
				+ "'";
		noSql += " AND ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		noSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		noSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		noSql += " AND CLINICROOM_NO ='" + result.getValue("CLINICROOM_NO", 0)
				+ "'";
		//
		TParm noParm = new TParm(TJDODBTool.getInstance().select(noSql));
		// System.out.println("===noSql=="+noSql);
		// 限挂人数
		sendString += noParm.getValue("MAX_QUE", 0) + "|";
		// 已挂人数 noParm.getValue("QUE_NO", 0)+ "|";
		sendString += (Integer.valueOf(noParm.getValue("QUE_NO", 0)) - 1) + "|";
		// this.messageBox("SESSION_CODE"+((TComboBox)
		// this.getComponent("SESSION_CODE")).getSelectedText());
		// 时间段
		sendString += result.getValue("SESSION_DESC", 0);

		String timeSql = "SELECT START_TIME FROM REG_CLINICQUE";
		timeSql += " WHERE ADM_TYPE ='" + result.getValue("ADM_TYPE", 0) + "'";
		timeSql += " AND ADM_DATE ='"
				+ result.getValue("ADM_DATE", 0).replaceAll("-", "").substring(
						0, 8) + "'";
		timeSql += " AND SESSION_CODE ='" + result.getValue("SESSION_CODE", 0)
				+ "'";
		timeSql += " AND CLINICROOM_NO ='"
				+ result.getValue("CLINICROOM_NO", 0) + "'";
		timeSql += " AND QUE_NO ='" + result.getValue("QUE_NO", 0) + "'";
		TParm startTimeParm = new TParm(TJDODBTool.getInstance()
				.select(timeSql));
		// System.out.println("===timeSql==="+timeSql);

		// 退挂叫号
		if ("UNREG".equals(type)) {
			// 预约处理

			inParm.setData("msg", sendString);
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + Que_No.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 **/
			TIOM_AppServer.executeAction("action.device.CallNoAction",
					"doUNReg", inParm);
			// this.messageBox("退挂叫号!");

		} else if ("REG".equals(type)) {
			// System.out.println("adm time===="+this.reg.getRegAdmTime());
			sendString += "|";
			// 预约处理
			if (startTimeParm.getValue("START_TIME", 0) != null
					&& !startTimeParm.getValue("START_TIME", 0).equals("")) {
				// sendString += result.getValue("ADM_DATE", 0).replaceAll("-",
				// "").substring(
				// 0, 8)+startTimeParm.getValue("START_TIME", 0) + "00";
				// System.out.println("========预约sendString=========="+sendString);
				sendString += startTimeParm.getValue("START_TIME", 0) + "00";
			} else {
				sendString += "";
			}
			// 2012-04-02|内分泌代谢科|000875|葛焕琦|主任医师|06诊室|000000001009|谷绍明|女|1936-01-05|2|60|2|上午|
			inParm.setData("msg", sendString);
			// this.messageBox("挂号叫号!");
			/**
			 * String sendString = admDate.trim() + "|" + deptDesc.trim() + "|"
			 * + Dr_Code.trim() + "|" + drName.trim() + "|" +
			 * clinicTypeDesc.trim() + "|" + clinicRoomDesc.trim() + "|" +
			 * Mr_No.trim() + "|" + patName.trim() + "|" + sex + "|" + birthday
			 * + "|" + QueNo.trim() + "|" + maxQue.trim() + "|" +
			 * curtQueNo.trim() + "|" + sessionDesc.trim();
			 * System.out.println("Reg_sendString--->" + sendString);
			 **/

			inParm.setData("msg", sendString);

			TIOM_AppServer.executeAction("action.device.CallNoAction", "doReg",
					inParm);

		}

		return "true";

	}

	/**
	 * 号别Combo值改变事件
	 * 
	 * @param flg
	 *            boolean
	 */
	public void onClickClinicType(boolean flg) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 挂号费
		this.setValue("REG_FEE", reg_fee);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		
		//门特需要增加诊查费10元钱  add by huangtt 20170330
		clinic_fee = clinic_fee + PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee");

		// 诊查费
		this.setValue("CLINIC_FEE", clinic_fee);
		// //应收费用
		// if (pat != null) {
		setValue("FeeY", reg_fee + clinic_fee);
		if (flg) { // 预约挂号不显示应收金额
			setValue("FeeS", reg_fee + clinic_fee);
		}

		// }
	}

	/**
	 * 号别Combo值改变事件
	 */
	public void onClickClinicType() {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));

		// 挂号费
		this.setValue("REG_FEE", reg_fee);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		
		//门特需要增加诊查费10元钱  add by huangtt 20170330
		clinic_fee = clinic_fee + PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee");

		// 诊查费
		this.setValue("CLINIC_FEE", clinic_fee);
		// //应收费用
		setValue("FeeY", reg_fee + clinic_fee);
		setValue("FeeS", reg_fee + clinic_fee);
		
		
	    //  ==add by zhanglei 20171116  增加挂号时若挂号身份是特殊身份弹出验证码
//		if(getREGSpecialFlg().equals("Y")){
//			checkSpecialFlg();
//		}
				
		//特殊权限验证  wuxinyue源程序
//		 String str = this.getValueString("REG_CTZ1");
//		 String sql = "SELECT * FROM SYS_CTZ WHERE CTZ_CODE = '"+str+"'";
//		 TParm AA = new TParm(TJDODBTool.getInstance().select(sql));
//		 if(AA.getValue("SPECIAL_FLG").equals("Y")){
//			 this.openDialog("%ROOT%\\config\\reg\\REGSpecialFlg.x");
//		 }
	}
	
	/**
	 * VVIP特殊权限验证
	 * zhanglei 20171116
	 */
//	public String getREGSpecialFlg(){
//		String sql = "SELECT SPECIAL_FLG FROM SYS_CTZ WHERE CTZ_CODE = '"
//		+ this.getValueString("REG_CTZ1") + "'";
////		this.messageBox("2:" + sql);
////		System.out.println("VVIP特殊权限验证SQL:" + sql);
//		TParm AA = new TParm(TJDODBTool.getInstance().select(sql));
//		if(AA.getValue("SPECIAL_FLG",0).equals("Y")){
//			return "Y";
//		 }else{
//			return "N";
//		 }
//	}
	
	/**
	 * 若患者身份为特殊身份打开验证窗口进行动态密码验证
	 * zhanglei 20171117 校验VVIP挂号
	 */
//	public void checkSpecialFlg(){
//		TParm a = new TParm();
//		a.setData("MR_NO",this.getValue("MR_NO"));
//		 Object b = this.openDialog("%ROOT%\\config\\reg\\REGSpecialFlg.x",a);
//		 
//		 //判断窗口是否人工关闭
//		 TParm z = new TParm();
//		 z.setData("kg",0,"Y");
//		 if (b instanceof TParm){
//			 	z = (TParm) b;
//			}
//		 if(z.getValue("kg",0).equals("N")){
//			this.setValue("REG_CTZ1", "");
//		}
//		 authCode = z.getValue("AUTH_CODE",0);//取回验证码
//	}

	/**
	 * 号别Combo值改变事件
	 * 
	 * @param fee
	 *            int
	 */
	public void onClickClinicType(int fee) {
		reg = new Reg();

		double reg_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "REG_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		// 挂号费
		this.setValue("REG_FEE", reg_fee);
		double clinic_fee = BIL.getRegDetialFee(admType, TypeTool
				.getString(getValue("CLINICTYPE_CODE")), "CLINIC_FEE", TypeTool
				.getString(getValue("REG_CTZ1")), TypeTool
				.getString(getValue("REG_CTZ2")), TypeTool
				.getString(getValue("CTZ3_CODE")), this
				.getValueString("SERVICE_LEVEL") == null ? "" : this
				.getValueString("SERVICE_LEVEL"));
		
		//门特需要增加诊查费10元钱  add by huangtt 20170330
		clinic_fee = clinic_fee + PatAdmTool.getInstance().getMTClinicFee(insParm).getDouble("fee");

		// 诊查费
		this.setValue("CLINIC_FEE", clinic_fee);
		// 应收费用
		double feeY = reg_fee + clinic_fee;

		// if (pat != null)
		setValue("FeeY", feeY * fee);
		setValue("FeeS", feeY * fee);
	}

	/**
	 * 支付方式改变事件
	 */
	public void onSelPayWay() {
		if (getValue("PAY_WAY").equals("PAY_CHECK"))
			callFunction("UI|REMARK|setEnabled", true);
		else
			callFunction("UI|REMARK|setEnabled", false);
		if (getValue("PAY_WAY").equals("PAY_DEBIT"))
			callFunction("UI|CONTRACT_CODE|setEnabled", true);
		else
			callFunction("UI|CONTRACT_CODE|setEnabled", false);

	}

	/**
	 * 查询医师排班(一般)
	 * 
	 */
	public void onQueryDrTable() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		// 筛选数据专家诊，普通诊
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		// 可是过滤权限
		if (this.getPopedem("deptFilter"))
			parm.setData("DEPT_CODE_SORT", "1101020101");
		TParm data = SchDayTool.getInstance().selectDrTable(parm);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
		TTable table = (TTable) this.getComponent("Table1");
		int selRow = table.getSelectedRow();
		if (selRow < 0)
			return;
		String drCode = table.getItemString(selRow, 4);
		String clinicroomNo = table.getItemString(selRow, 3);
		String sql = "SELECT SEE_DR_FLG FROM REG_PATADM" + "  WHERE DR_CODE='"
				+ drCode + "' " + "AND  CLINICROOM_NO ='" + clinicroomNo + "'"
				+ "AND SEE_DR_FLG='N'";
		// System.out.println("sql===="+sql);
		TParm selparm = new TParm(TJDODBTool.getInstance().select(sql));
		int count = selparm.getCount();
		this.setValue("COUNT", count + "");
	}

	/**
	 * 查询医师排班(VIP)
	 */
	public void onQueryVipDrTable() {
		TTable table2 = new TTable();
		table2.removeAll();
		TParm parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;VIP_SESSION_CODE;VIP_DEPT_CODE;VIP_DR_CODE",
				true);
		parm.setData("ADM_TYPE", admType);
		parm.setData("VIP_ADM_DATE", StringTool.getString(
				(Timestamp) getValue("VIP_ADM_DATE"), "yyyyMMdd"));
		TParm data2 = REGClinicQueTool.getInstance().selVIPDate(parm);
		if (data2.getErrCode() < 0) {
			messageBox(data2.getErrText());
			return;
		}
		this.callFunction("UI|Table2|setParmValue", data2);
	}

	/**
	 * 查询病患挂号信息
	 */
	// CASE_NO;ADM_DATE;SESSION_CODE;DEPT_CODE;DR_CODE;QUE_NO;ADM_STATUS;ARRIVE_FLG;CONFIRM_NO;INS_PAT_TYPE;REGMETHOD_CODE
	public void selPatInfoTable() {
		TParm parm = new TParm();
		String startTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_START_DATE")), "yyyyMMdd");
		String endTime = StringTool.getString(TypeTool
				.getTimestamp(getValue("YY_END_DATE")), "yyyyMMdd");
		parm.setData("MR_NO", pat.getMrNo()); 
		parm.setData("YY_START_DATE", startTime);
		parm.setData("YY_END_DATE", endTime);
		parm.setData("ADM_TYPE", admType);
		parm.setData("REGION_CODE", Operator.getRegion());
		TParm data = PatAdmTool.getInstance().selPatInfoForREG(parm);
		// System.out.println("table3显示数据" + data);
		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		
		this.callFunction("UI|Table3|setParmValue", data);

	}

	/**
	 * 根据科室下拉列表，查询医师排班(一般)
	 */
	public void onQueryDrTableByDrCombo() {

		TParm parm = getParmForTag(
				"REGION_CODE;ADM_DATE:timestamp;SESSION_CODE", true);
		parm.setData("ADM_TYPE", admType);
		parm.setDataN("DEPT_CODE_SORT", TypeTool
				.getString(getValue("DEPT_CODE_SORT")));
		// 筛选数据专家诊，普通诊
		if ("N".equalsIgnoreCase(this.getValueString("tRadioAll"))) {
			if ("Y".equalsIgnoreCase(this.getValueString("tRadioExpert"))) {
				parm.setData("EXPERT", "Y");
			}

			if ("Y".equalsIgnoreCase(this.getValueString("tRadioSort"))) {
				parm.setData("SORT", "Y");
			}
		}
		TParm data = SchDayTool.getInstance().selectDrTable(parm);

		if (data.getErrCode() < 0) {
			messageBox(data.getErrText());
			return;
		}
		this.callFunction("UI|Table1|setParmValue", data);
	}

	/**
	 * 算找零金额
	 */
	public void onFee() {
		DecimalFormat df = new DecimalFormat("##########0.00");
		// 找零金额
		setValue("FeeZ", TypeTool.getDouble(df.format(getValue("FeeS")))
				- TypeTool.getDouble(df.format(getValue("FeeY"))));
		// 得到焦点
		this.grabFocus("SAVE_REG");
	}

	/**
	 * 补印
	 */
	public void onPrint() {
		// TParm forPrtParm = new TParm();
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		String caseNo = (String) table3.getValueAt(row, 0);
		String confirmNo = (String) table3.getParmValue().getValue(
				"CONFIRM_NO", row);
		if (this.getValueString("NEXT_NO").length() <= 0
				|| this.getValueString("NEXT_NO").compareTo(endInvNo) > 0) {
			this.messageBox("票据已用完!");
			return;
		}
		TParm temp = new TParm();
		temp.setData("RECEIPT_TYPE", "REG");
		temp.setData("CASE_NO", caseNo);
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			temp.setData("REGION_CODE", Operator.getRegion());
		TParm result = BILContractRecordTool.getInstance().regRecodeQuery(temp);
		if (null != result && result.getValue("BIL_STATUS", 0).equals("1")) {
			this.messageBox("记账挂号费用没有执行结算操作,不可以打票");
			return;
		}

		TParm onREGReprintParm = new TParm();
		onREGReprintParm.setData("CASE_NO", caseNo);
		onREGReprintParm.setData("OPT_USER", Operator.getID());
		onREGReprintParm.setData("OPT_TERM", Operator.getIP());
		onREGReprintParm.setData("ADM_TYPE", admType);
		result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onREGReprint", onREGReprintParm);
		if (result.getErrCode() < 0) {
			this.messageBox("补印操作失败");
			return;
		}
		result = PatAdmTool.getInstance().getRegPringDate(caseNo, "COPY");
		result.setData("PRINT_NO", "TEXT", this.getValue("NEXT_NO"));
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("CONFIRM_NO", confirmNo);
		TParm mzConfirmParm = INSMZConfirmTool.getInstance().queryMZConfirm(
				parm); // 判断此次操作是否是医保操作
		if (mzConfirmParm.getErrCode() < 0) {
			return;
		}
		TParm printParm = null;
		if (mzConfirmParm.getCount() > 0) {
			printParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);
			insFlg = true;
		}
		onRePrint(result, mzConfirmParm, printParm);
		this.onInit();
	}

	/**
	 * 补印
	 * 
	 * @param parm
	 *            TParm
	 * @param mzConfirmParm
	 *            TParm
	 * @param printParm
	 *            TParm
	 */
	private void onRePrint(TParm parm, TParm mzConfirmParm, TParm printParm) {
		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // 科室诊室名称
		// 显示方式:科室(诊室)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "号)"); // 号别
		// 显示方式:号别(诊号)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		parm.setData("BALANCE_NAME", "TEXT", "余 额"); // 余额名称
		DecimalFormat df = new DecimalFormat("########0.00");
		if (tjINS) {
			ektNewSum = df.format(p3.getDouble("CURRENT_BALANCE"));
		}
		parm.setData("CURRENT_BALANCE", "TEXT", "￥ "
				+ df.format(Double.parseDouble(ektNewSum == null
						|| "".equals(ektNewSum) ? "0.00" : ektNewSum))); // 医疗卡剩余金额

		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_CASH", "TEXT", "现金:"
					+ StringTool.round(
							(parm.getDouble("TOTAL", "TEXT") - printParm
									.getDouble("PAY_INS_CARD", 0)), 2)); // 现金
			// 个人账户
			String sqlamt = " SELECT ACCOUNT_PAY_AMT  FROM INS_OPD "
					+ " WHERE CASE_NO ='"
					+ mzConfirmParm.getValue("CASE_NO", 0) + "'"
					+ " AND CONFIRM_NO ='"
					+ mzConfirmParm.getValue("CONFIRM_NO", 0) + "'";
			
			TParm insaccountamtParm = new TParm(TJDODBTool.getInstance()
					.select(sqlamt));
			if (insaccountamtParm.getErrCode() < 0) {

			} else {
				parm.setData("PAY_ACCOUNT", "TEXT", "账户:"
						+ StringTool.round(insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0), 2));
				parm.setData("PAY_DEBIT", "TEXT", "医保:"
						+ StringTool.round((printParm.getDouble("PAY_INS_CARD",
								0) - insaccountamtParm.getDouble(
								"ACCOUNT_PAY_AMT", 0)), 2)); // 医保支付
			}
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ mzConfirmParm.getValue("SPECIAL_PAT", 0) + "'";// 医保特殊人员身份显示
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // 日期
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		// ===zhangp 20120313 start
		if ("1".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			// parm.setData("Cost_class", "TEXT", "门统");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		} else if ("2".equals(mzConfirmParm.getValue("INS_CROWD_TYPE", 0))) {
			parm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			// parm.setData("Cost_class", "TEXT", "门特");
			if (admType.equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		}
		// ===zhangp 20120313 end
        String caseNo = parm.getValue("CASE_NO", "TEXT");//add by wanglong 20121217
        TParm oldDataRecpParm = BILREGRecpTool.getInstance().selForRePrint(caseNo);//add by wanglong 20121217
        parm.setData("RECEIPT_NO", "TEXT", oldDataRecpParm.getData("RECEIPT_NO", 0));//add by wanglong 20121217
//		this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				parm, true);
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("REGRECPPrint.jhw"),
                             IReportTool.getInstance().getReportParm("REGRECPPrint.class", parm), true);//报表合并modify by wanglong 20130730
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this.initReg();
		clearValue(" MR_NO;PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL;NHI_NO;EKT_CURRENT_BALANCE;COUNT");
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
			setValue("TRIAGE_NO", "");
			this.setValue("ARRIVE_DATE", SystemTool.getInstance().getDate().toString().substring(0,16).replaceAll("-", "/"));
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		callFunction("UI|FOREIGNER_FLG|setEnabled", true); // 其他证件可编辑======pangben
		// modify 20110808
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_C"))) {
//			callFunction("UI|MR_NO|setEnabled", false);
//			this.grabFocus("PAT_NAME");
		}
		if ("Y".equalsIgnoreCase(this.getValueString("VISIT_CODE_F"))) {
			callFunction("UI|MR_NO|setEnabled", true);
			this.grabFocus("MR_NO");
		}
		// callFunction("UI|MR_NO|setEnabled", true); //其他证件可编辑======pangben
		// modify 20110808
		callFunction("UI|CONTRACT_CODE|setEnabled", true); // 记账单位可编辑
		callFunction("UI|SAVE_REG|setEnabled", true);//收费按钮可编辑
		// 设置默认服务等级
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		// feeIstrue = false;
		ins_amt = 0.00; // 医保金额
		accountamtforreg = 0.00;// 个人账户
		feeShow = false; // 南京医保中心获得费用显示
		txEKT = false; // 泰心医疗卡写卡管控
		p3 = null; // 医疗卡读卡parm
		insFlg = false; // 医保卡读卡操作
		tjINS = false; // 医疗卡读卡完成操作
		reSetEktParm = null; // 医疗卡退费使用判断是否执行医疗卡退费操作
		confirmNo = null; // 医保卡就诊号，退挂时时使用
		reSetCaseNo = null; // 退挂使用就诊号码
		insType = null;
		tableFlg = false; // 判断选中第一个页签表格数据
		ektNewSum = "0.00"; // 医疗卡扣款后金额
		// ===zhangp 20120427 start
		greenParm = null;// //绿色通道使用金额
		insParm=null;
		// 解锁病患
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		initSchDay();
		callFunction("UI|SAVE_REG|setEnabled", true);
		pat = null;
		// reg=null;
		ins_exe = false;
	}

	/**
	 * 是否关闭窗口
	 * 
	 * @return boolean true 关闭 false 不关闭
	 */
	public boolean onClosing() {
		// 解锁病患
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
		return true;
	}

	/**
	 * 初始化时段
	 */
	public void initSession() {
		// 初始化时段Combo,取得默认时段
		String defSession = SessionTool.getInstance().getDefSessionNow(admType,
				Operator.getRegion());
		setValue("SESSION_CODE", defSession);
		setValue("VIP_SESSION_CODE", defSession);
	}

	/**
	 * 为清空后初始化
	 */
	public void initReg() {
		// 设置默认身份
		setValue("CTZ1_CODE", "99");
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// 过滤数据
		combo_ctz.setNhiFlg("");
		combo_ctz.onQuery();
		setValue("REG_CTZ1", "99");
		setValue("REGION_CODE", Operator.getRegion());
		setValue("ADM_DATE", SystemTool.getInstance().getDate());
		String sessionCode = initSessionCode();
		Timestamp admDate = TJDODBTool.getInstance().getDBTime();
		// 根据时段判断应该显示的日期（针对于晚班夸0点的问题，跨过0点的晚班应该显示前一天的日期）
		if (!StringUtil.isNullString(sessionCode)
				&& !StringUtil.isNullString(admType)) {
			admDate = SessionTool.getInstance().getDateForSession(admType,
					sessionCode, Operator.getRegion());
			this.setValue("ADM_DATE", admDate);
		}
		// 初始化默认(现场)挂号方式
		setValue("REGMETHOD_CODE", "A");

		// 初始化预约信息开始时间
		setValue("YY_START_DATE", getValue("ADM_DATE"));
		setValue("YY_END_DATE", StringTool.getTimestamp("9999/12/31",
				"yyyy/MM/dd"));
		// 初始化VIP班表Combo
		setValue("VIP_ADM_DATE", getValue("ADM_DATE"));
		// 置退挂,报道,补印按钮为灰
		callFunction("UI|unreg|setEnabled", false);
		callFunction("UI|arrive|setEnabled", false);
		callFunction("UI|print|setEnabled", false);
		// 置收费按钮可编辑
		callFunction("UI|SAVE_REG|setEnabled", true);
		// 置挂号信息界面控件可编辑
		setControlEnabled(true);
		setRegion();
	}

	/**
	 * 通信邮编的得到省市
	 */
	public void onPost() {
		String post = getValueString("POST_CODE");
		TParm parm = SYSPostTool.getInstance().getProvinceCity(post);
		if (parm.getErrCode() != 0 || parm.getCount() == 0) {
			return;
		}
		setValue("STATE", parm.getData("POST_CODE", 0).toString().substring(0,
				2));
		setValue("CITY", parm.getData("POST_CODE", 0).toString());
		this.grabFocus("ADDRESS");
	}

	/**
	 * 设置区域是否可以下拉
	 */
	public void setRegion() {
		if (!REGSysParmTool.getInstance().selOthHospRegFlg())
			callFunction("UI|REGION_CODE|setEnabled", false);
	}

	/**
	 * 通过城市带出邮政编码
	 */
	public void selectCode() {
		this.setValue("POST_CODE", this.getValue("CITY"));
	}

	/**
	 * 检测病患相同姓名
	 */
	public void onPatName() {
		String patName = this.getValueString("PAT_NAME");
		if (StringUtil.isNullString(patName)) {
			return;
		}
		String selPat = "SELECT  DISTINCT(A.MR_NO) AS MR_NO, A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE PAT_NAME = '"
				+ patName
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE,A.BIRTH_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		setPatName1();
		// 选择病患信息
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("提示信息", "已有相同姓名病患信息,是否继续保存此人信息", 0);
			if (sameCount != 1) {
				this.grabFocus("PY1");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryMrNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("PY1");
	}

	/**
	 * 检测病患相同身份证号
	 */
	public void onIDNo() {
		String idNo = this.getValueString("IDNO");
		if (StringUtil.isNullString(idNo)) {
			return;
		}
		// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
		String selPat = "SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, A.MR_NO,B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE A.IDNO = '"
				+ idNo
				+ "'  "
				+ " AND A.MR_NO = B.MR_NO (+) "
				+ " ORDER BY A.OPT_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// 选择病患信息
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("提示信息", "已有相同电话病患信息,是否继续保存此人信息", 0);
			if (sameCount != 1) {
				this.grabFocus("TEL_HOME");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryMrNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("TEL_HOME");
	}

	/**
	 * 检测病患相同身份证号
	 */
	public void onTelHome() {
		String telHome = this.getValueString("TEL_HOME");
		if (StringUtil.isNullString(telHome)) {
			return;
		}
		// REPORT_DATE;PAT_NAME;IDNO;SEX_CODE;BIRTH_DATE;POST_CODE;ADDRESS
		String selPat =
		// ===zhangp 20120319 start
		"SELECT   A.OPT_DATE AS REPORT_DATE, PAT_NAME, IDNO, SEX_CODE, BIRTH_DATE,"
				+ " POST_CODE, ADDRESS, A.MR_NO,B.EKT_CARD_NO "
				+ " FROM SYS_PATINFO A,EKT_ISSUELOG B "
				+ " WHERE A.TEL_HOME = '" + telHome + "'  "
				+ " AND A.MR_NO = B.MR_NO (+) " + " ORDER BY A.OPT_DATE";
		// ===zhangp 20120319 end
		TParm same = new TParm(TJDODBTool.getInstance().select(selPat));
		if (same.getErrCode() != 0) {
			this.messageBox_(same.getErrText());
		}
		// 选择病患信息
		if (same.getCount("MR_NO") > 0) {
			int sameCount = this.messageBox("提示信息", "已有相同电话号码病患信息,是否继续保存此人信息",
					0);
			if (sameCount != 1) {
				this.grabFocus("POST_CODE");
				return;
			}
			Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x", same);
			TParm patParm = new TParm();
			if (obj != null) {
				patParm = (TParm) obj;
				onQueryMrNO(patParm.getValue("MR_NO"));
				return;
			}
		}
		this.grabFocus("POST_CODE");
	}

	/**
	 * 报到
	 */
	public void onArrive() {
	//  ==add by zhanglei 20171116  增加挂号时若挂号身份是特殊身份弹出验证码
//		if(getREGSpecialFlg().equals("Y")){
//			checkSpecialFlg();
//		}
		TTable table3 = (TTable) callFunction("UI|table3|getThis");
		int row = table3.getSelectedRow();
		// ====zhangp 20120306 modify start
		TParm table3Parm = table3.getParmValue();
		String admdate = table3Parm.getData("ADM_DATE", row).toString();
		admdate = admdate.substring(0, 10);
		String date = SystemTool.getInstance().getDate().toString();
		date = date.substring(0, 10);
		if (!admdate.equals(date)) {
			messageBox("非当日，不能报道");
			return;
		}
		// ====zhangp 20120306 modify end
		String caseNo = (String) table3.getValueAt(row, 0);
		reg = null;
		reg = reg.onQueryByCaseNo(pat, caseNo);
		// // 保存医疗卡
		reg.setNhiNo(this.getValueString("NHI_NO"));
		if (reg.getPat().getMrNo() == null
				|| reg.getPat().getMrNo().length() == 0) {
			this.messageBox("病案号不能为空");
			return;
		}
		reg.createReceipt();
		reg.getRegReceipt().createBilInvoice();
		// 挂号主表,REG对象
		// 2门急别
		if (!onSaveRegParm(false))
			return;
		reg.setTredeNo(tredeNo);
		TParm regParm = reg.getParm();
		//add caoyong 20140311 ----start
		if("".equals(this.getValue("REG_CTZ1"))||this.getValue("REG_CTZ1")==null){
			this.messageBox("挂号身份一不能为空");
			this.grabFocus("REG_CTZ1");
			return ;
		}
		//add by huangw 20150817
		if (admType.endsWith("E")) {
			if (this.getValue("ARRIVE_DATE")==null||this.getValue("ARRIVE_DATE").toString().length()<0){//校验到院时间 add by haungjw 20150603
				this.messageBox("到院时间不能为空");
				this.grabFocus("ARRIVE_DATE");
				return;
			}
			regParm.setData("ARRIVE_DATE",this.getValue("ARRIVE_DATE").toString().substring(0,19).replaceAll("-", "/"));

		}
		//add caoyong 20140311 ----end
		regParm.setData("CTZ1_CODE", this.getValue("REG_CTZ1"));
		regParm.setData("CTZ2_CODE", this.getValue("REG_CTZ2"));
		regParm.setData("CTZ3_CODE", getValue("CTZ3_CODE"));
		String receiptNo = SystemTool.getInstance().getNo("ALL", "REG",
				"RECEIPT_NO", "RECEIPT_NO");

		reg.getRegReceipt().setCaseNo(caseNo);
		// 8记账日期(REG_RECEIPT)
		reg.getRegReceipt().setBillDate(SystemTool.getInstance().getDate());
		// 9收费日期(REG_RECEIPT)
		reg.getRegReceipt().setChargeDate(SystemTool.getInstance().getDate());
		// 10收据打印日期(REG_RECEIPT)
		reg.getRegReceipt().setPrintDate(SystemTool.getInstance().getDate());
		// 28收款员编码(REG_RECEIPT)
		reg.getRegReceipt().setCashCode(Operator.getID());
		reg.getRegReceipt().setReceiptNo(receiptNo); // 挂号收据(REG_RECEIPT)
		// 票据主档BilInvoice(For bil),BIL_INVOICE对象
		reg.getRegReceipt().createBilInvoice();
		reg.getRegReceipt().getBilInvoice().getParm();
		// 票据明细档BILInvrcpt(For bil),BIL_INVRCP对象
		reg.getRegReceipt().createBilInvrcpt();
		reg.getRegReceipt().getBilInvrcpt().setReceiptNo(receiptNo); // 票据明细档收据号(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setRecpType("REG"); // 1票据类型(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setInvNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo()); // //2发票号码(BIL_INVRCP)
		// 7收据印刷号(REG_RECEIPT)
		reg.getRegReceipt().setPrintNo(
				reg.getRegReceipt().getBilInvoice().getUpdateNo());
		reg.getRegReceipt().getBilInvrcpt().setCashierCode(Operator.getID()); // 操作人员(BIL_INVRCP)
		reg.getRegReceipt().getBilInvrcpt().setArAmt(
				TypeTool.getDouble(getValue("FeeY"))); // 总金额(BIL_INVRCP)
		// 判断初始化票据
		reg.getRegReceipt().getBilInvoice().initBilInvoice("REG");
		// 显示下一票号
		if (reg.getRegReceipt().getBilInvoice().getUpdateNo() == null
				|| reg.getRegReceipt().getBilInvoice().getUpdateNo().length() == 0) {
			this.messageBox("尚未开账");
			return;
		}
		reg.getRegReceipt().getBilInvoice().getParm();
		// 门急诊主档
		TParm saveParm = new TParm();

		// 票据主档
		TParm bilInvoiceParm = reg.getRegReceipt().getBilInvoice().getParm();
		saveParm.setData("BIL_INVOICE", bilInvoiceParm.getData());

		// 票据明细档
		TParm bilInvrcpParm = reg.getRegReceipt().getBilInvrcpt().getParm();
		bilInvrcpParm.setData("RECEIPT_NO", receiptNo);
		saveParm.setData("BIL_INVRCP", bilInvrcpParm.getData());
		saveParm.setData("TREDE_NO", reg.getTredeNo());
		// 医保医疗操作 共用部分
		String payWay = TypeTool.getString(getValue("PAY_WAY")); // 支付类别
		if (!onInsEkt(payWay, caseNo)) {
			return;
		}
		
		saveParm.setData("EKT_SQL", reg.getEktSql());  //add by huangtt 20160914  报道时医疗卡执行SQL
		
		saveParm.setData("REG", regParm.getData());
		// 门诊收据
		TParm regReceiptParm = reg.getRegReceipt().getParm();
		saveParm.setData("REG_RECEIPT", regReceiptParm.getData());
		if (ins_exe) {
			saveParm.setData("insParm", insParm.getData());// 保存医保数据执行修改REG_PADADM
			// 表中INS_PAT_TYPE 和
			// COMFIRM_NO 字段
		}
		
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onSaveRegister", saveParm);
		// System.out.println("result:::::" + result);
		if (result.getErrCode() < 0) {
			this.messageBox("报道失败");
			// EKTIO.getInstance().unConsume(tredeNo, this);
			// 医疗卡操作回写金额
//			if (payWay.equals("PAY_MEDICAL_CARD")) {
//				TParm writeParm = new TParm();
//				writeParm.setData("CURRENT_BALANCE", ektOldSum);
//				writeParm = EKTIO.getInstance().TXwriteEKTATM(writeParm,
//						pat.getMrNo()); // 回写医疗卡金额
//				if (writeParm.getErrCode() < 0)
//					System.out.println("err:" + writeParm.getErrText());
//			}
			return;
		}
		//更新医保表中的RECEIPT_NO
		if (ins_exe) {
		 String confirmNo = insParm.getValue("CONFIRM_NO");
	     String sql = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
		   " WHERE CASE_NO ='" + caseNo + "'" +
		   " AND CONFIRM_NO = '" + confirmNo + "'" +
		   " AND RECP_TYPE = 'REG'";
         TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql));
        if (updateParm.getErrCode() < 0) {
           err(updateParm.getErrCode() + " " + updateParm.getErrText());
             updateParm.setErr(-1, "更新医保表失败");
             return ;
         }
	}
		// 门诊打票操作
		result = onPrintParm();
		onPrint(result);
		this.onClear();
		// 报到调用排队叫号
		if (!"true".equals(callNo("REG", caseNo))) {
			this.messageBox("叫号失败");
		}
		BilInvoice invoice = new BilInvoice();
		invoice = invoice.initBilInvoice("REG");
		// 初始化下一票号
		setValue("NEXT_NO", invoice.getUpdateNo());
		callFunction("UI|arrive|setEnabled", false);
		// this.selPatInfoTable();
		this.callFunction("UI|table3|clearSelection");
	}

	/**
	 * 设置英文名
	 */
	public void setPatName1() {
		String patName1 = SYSHzpyTool.getInstance().charToAllPy(
				TypeTool.getString(getValue("PAT_NAME")));
		setValue("PAT_NAME1", patName1);
	}

	/**
	 * 退挂操作
	 */
	public void onUnReg() {
		// =====zhangp 20120301 modify start
		if (this.messageBox("询问", "是否退挂", 2) == 0) {
			this.callFunction("UI|unreg|setEnabled", false);
//			if (!this.getPopedem("LEADER")) { //  == zhanglei 20171201 按客户要求去掉退挂管控
//				this.messageBox("非组长不能退挂!");
//				return;
//			}
			TTable table3 = (TTable) callFunction("UI|table3|getThis");
			int row = table3.getSelectedRow();
			if (row < 0) {
				this.messageBox("请选择要退挂的数据");
				return;
			}
			// ===zhangp 20120316 start
			String arriveFlg = (String) table3.getValueAt(row, 7);
			// 判断是否预约挂号
			if ("N".equals(arriveFlg)) {
				//add by huangtt 20160530 start 退预约时，如果是Q医预约的将要给Q医发信息告诉他们一声
					String qSql = "SELECT CASE_NO,MR_NO,OPT_USER FROM REG_PATADM WHERE CASE_NO='"+table3.getParmValue().getValue("CASE_NO", row)+"' AND QEAPP_FlG='Y'";
					TParm qParm = new TParm(TJDODBTool.getInstance().select(qSql));
					if(qParm.getCount()>0){
						TParm result = TIOM_AppServer.executeAction("action.reg.REGQeAppAction",
								"unRegQe", qParm.getRow(0));
						System.out.println(result);
						if(result == null){
							System.out.println("Q医接口参数=="+qParm.getRow(0));
							this.messageBox("调用Q医退预约接口出现异常，请联系Q医工作人员手动修改");
						}
						
						
						
					}
					
				//add by huangtt 20160530 end 
				

				table3.getParmValue().getRow(row);
				String sql = "UPDATE REG_PATADM SET REGCAN_USER = '"
						+ Operator.getID()
						+ "',REGCAN_DATE = SYSDATE,OPT_USER = '"
						+ Operator.getID() + "',"
						+ "OPT_DATE = SYSDATE,OPT_TERM = '" + Operator.getIP()
						+ "' " + "WHERE CASE_NO = '"
						+ table3.getParmValue().getValue("CASE_NO", row) + "'";
				TParm updateParm = new TParm(TJDODBTool.getInstance().update(
						sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("退挂失败");
					return;
				}
				String admDate = table3.getParmValue()
						.getValue("ADM_DATE", row);
				admDate = admDate.substring(0, 4) + admDate.substring(5, 7)
						+ admDate.substring(8, 10);
				sql = "UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' WHERE ADM_TYPE = '"
						+ table3.getParmValue().getValue("ADM_TYPE", row)
						+ "'AND ADM_DATE = '"
						+ admDate
						+ "' AND "
						+ "SESSION_CODE = '"
						+ table3.getParmValue().getValue("SESSION_CODE", row)
						+ "' AND "
						+ "CLINICROOM_NO = '"
						+ table3.getParmValue().getValue("CLINICROOM_NO", row)
						+ "' AND "
						+ "QUE_NO = '"
						+ table3.getParmValue().getValue("QUE_NO", row) + "'";
				updateParm = new TParm(TJDODBTool.getInstance().update(sql));
				if (updateParm.getErrCode() < 0) {
					messageBox("退挂失败");
					return;
				}
				messageBox("预约取消成功");
				// 调用排队叫号
				if (!"true".equals(callNo("UNREG", table3.getParmValue()
						.getValue("CASE_NO", row)))) {
					this.messageBox("叫号失败");
				}
				this.onClear();
				return;
			}
			// ===zhangp 20120316 end
			String caseNo = (String) table3.getValueAt(row, 0);
			TParm tredeParm = new TParm(); // 查询此次退挂操作是否是医疗卡退挂
			tredeParm.setData("CASE_NO", caseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // 类型
			tredeParm.setData("STATE", "1"); // 状态： 0 扣款 1 扣款打票 2退挂 3 作废
			confirmNo = table3.getParmValue().getValue("CONFIRM_NO", row); // 医保就诊号
			reSetCaseNo = table3.getParmValue().getValue("CASE_NO", row); // 医保退挂使用
			insType = table3.getParmValue().getValue("INS_PAT_TYPE", row); // 医保就诊类型1.城职普通2.城职门特
			// 3.城居门特
			if (null != confirmNo && confirmNo.length() > 0) {
				// 执行医保操作
				// System.out.println("医保卡退费");
			} else {
				reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // 医疗卡退费查询
				if (reSetEktParm.getErrCode() < 0) {
					this.messageBox("退挂执行有误");
					return;
				}
				if (reSetEktParm.getCount() > 0) { // 如果存在但是没有获得医疗卡信息，提示==pangb
					// 2011-11-29
					String payWay = this.getValueString("PAY_WAY");
					if (!"PAY_MEDICAL_CARD".equals(payWay)) {
						this.messageBox("请读取医疗卡信息");
						return;
					}
				}
			}
			TParm parm = new TParm();
			parm.setData("CASE_NO", caseNo);
			parm.setData("RECEIPT_TYPE", "REG");
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0)
				parm.setData("REGION_CODE", Operator.getRegion());

			TParm result = BILContractRecordTool.getInstance().regRecodeQuery(
					parm);
			// 查询是否有记账信息
			if (null != result && result.getCount() > 0) {
				// 已经结算完成的挂号费
				if ("2".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes2(caseNo, true);
				} else if ("1".equals(result.getValue("BIL_STATUS", 0))) {
					onUnRegYes1(caseNo);
				}
				// 正常退挂
			} else {
				onUnRegNo(caseNo, false);
			}
			this.onClear();
		} else
			return;

	}

	/**
	 * 二代身份证
	 */
	public void idnoInfo() {
		this.openDialog("%ROOT%\\config\\sys\\SYSPatInfoFromID.x");
	}

	public static void main(String args[]) {
		com.javahis.util.JavaHisDebug.TBuilder();

	}

	/**
	 * 泰心医疗卡扣款操作
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return boolean
	 */
	private boolean onTXEktSave(String FLG, TParm insParm) {
		int type = 0;
		TParm parm = new TParm();
		// 如果使用医疗卡，并且扣款失败，则返回不保存
		if (EKTIO.getInstance().ektSwitch()) { // 医疗卡开关，记录在后台config文件中
			if (null == insParm)
				parm = onOpenCard(FLG);
			else
				parm = onOpenCard(FLG, insParm);
			// System.out.println("打开医疗卡parm=" + parm);
			if (parm == null) {
				this.messageBox("E0115");
				return false;
			}
			type = parm.getInt("OP_TYPE");
			// System.out.println("type===" + type);
			if (type == 3) {
				this.messageBox("E0115");
				return false;
			}
			if (type == 2) {
				return false;
			}
			if (type == -1) {
				this.messageBox("读卡错误!");
				return false;
			}
			tredeNo = parm.getValue("TREDE_NO");
			businessNo = parm.getValue("BUSINESS_NO"); // //出现医疗卡扣款操作问题使用
			ektOldSum = parm.getValue("OLD_AMT"); // 执行失败撤销的金额
			ektNewSum = parm.getValue("EKTNEW_AMT"); // 扣款以后的金额
			// 判断是否操作绿色通道
			if (null != parm.getValue("GREEN_FLG")
					&& parm.getValue("GREEN_FLG").equals("Y")) {
				greenParm = parm;
			}
			// System.out.println("ektNewSum======"+ektNewSum);
		} else {
			this.messageBox_("医疗卡接口未开启");
			return false;
		}
		return true;

	}

	/**
	 * 医疗卡保存
	 * 
	 * @param FLG
	 *            String
	 * @return boolean
	 */
	public boolean onEktSave(String FLG) {
		return onTXEktSave(FLG, null);
	}

	/**
	 * 打开医疗卡
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG) {
		if (reg == null) {
			return null;
		}
		// 准备送入医疗卡接口的数据
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.setData("INS_FLG", "N");
		// 医保出现问题现金收取
		reg.setInsPatType(""); // 就诊医保类型 需要保存到REG_PATADM数据库表中1.城职普通 2.城职门特
		// 3.城居门特
		// 送医疗卡，返回医疗卡的回传值
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))); // 医疗卡已经收费的数据
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);

		return parm;
	}

	/**
	 * 天津医保
	 * 
	 * @param FLG
	 *            String
	 * @param insParm
	 *            TParm
	 * @return TParm
	 */
	public TParm onOpenCard(String FLG, TParm insParm) {
		// 准备送入医疗卡接口的数据
		TParm orderParm = orderEKTParm(FLG);
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // 医保卡自费部分金额
		orderParm.setData("INS_AMT", insParm.getDouble("INS_SUMAMT")); // 医保卡自费部分金额
		orderParm.setData("INS_FLG", "Y"); // 医保卡注记
		orderParm.setData("OPBEKTFEE_FLG", true);// 取消按钮
		orderParm.setData("RECP_TYPE", "REG"); // 添加EKT_ACCNTDETAIL 表数据使用
		orderParm.setData("comminuteFeeParm", insParm.getParm(
				"comminuteFeeParm").getData()); // 费用分割返回参数
		orderParm.setData("ektParm", p3.getData());
		orderParm.setData("EXE_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // 此病患所有收费医嘱包括已经打票的
		orderParm.setData("SHOW_AMT", TypeTool.getDouble(getValue("FeeY"))
				- insParm.getDouble("INS_SUMAMT")); // 显示金额
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		// 送医疗卡，返回医疗卡的回传值
		TParm parm = EKTIO.getInstance().onOPDAccntClient(orderParm,
				reg.caseNo(), this);
		return parm;
	}

	/**
	 * 医疗卡入参
	 * 
	 * @param FLG
	 *            String
	 * @return TParm
	 */
	private TParm orderEKTParm(String FLG) {
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // 写固定值
		orderParm.addData("ORDER_CODE", "REG"); // 写固定值
		orderParm.addData("SEQ_NO", "1"); // 写固定值
		orderParm.addData("EXEC_FLG", "N"); // 写固定值
		orderParm.addData("RECEIPT_FLG", "N"); // 写固定值
		orderParm.addData("BILL_FLG", FLG);
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("BUSINESS_TYPE", "REG");
		return orderParm;
	}

	/**
	 * 退挂操作医疗卡退费操作
	 * 
	 * @param caseNo
	 * @param type
	 *            1.正常医疗卡退费 2.医保卡退费
	 * @return
	 */
	public TParm onOpenCardR(String caseNo) {
		// 准备送入医疗卡接口的数据
		TParm orderParm = new TParm();
		orderParm.addData("RX_NO", "REG"); // 写固定值
		orderParm.addData("ORDER_CODE", "REG"); // 写固定值
		orderParm.addData("SEQ_NO", "1"); // 写固定值
		orderParm.addData("AMT", TypeTool.getDouble(getValue("FeeY")));
		orderParm.addData("EXEC_FLG", "N"); // 写固定值
		orderParm.addData("RECEIPT_FLG", "N"); // 写固定值
		orderParm.addData("BILL_FLG", "N");
		orderParm.setData("MR_NO", pat.getMrNo());
		orderParm.setData("NAME", pat.getName());
		orderParm.setData("SEX", pat.getSexCode() != null
				&& pat.getSexCode().equals("1") ? "男" : "女");
		orderParm.setData("BUSINESS_TYPE", "REGT");
		orderParm.setData("TYPE_FLG", "Y");
		if (null != confirmNo && confirmNo.length() > 0) {
			orderParm.setData("OPBEKTFEE_FLG", true);
		}
		orderParm.setData("ektParm", p3.getData());
		// 查询此病患已收费未打票的所有数据汇总金额
		TParm parm = new TParm();
		parm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		parm.setData("CASE_NO", caseNo);
		TParm ektSumParm = EKTNewTool.getInstance().selectEktTrade(parm);
		orderParm.setData("EXE_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0)); // 医疗卡已经收费的数据
		orderParm.setData("SHOW_AMT", -ektSumParm.getDouble("AMT", 0)
				- ektSumParm.getDouble("GREEN_BUSINESS_AMT", 0));
		orderParm.setData("EKT_TRADE_TYPE", "'REG','REGT'");
		
		
		// System.out.println("MR_NO" + pat.getMrNo());
		// System.out.println("退挂传入金额"+TypeTool.getDouble(getValue("FeeY")));
		// 送医疗卡，返回医疗卡的回传值
//		parm = EKTIO.getInstance().onOPDAccntClient(orderParm, caseNo, this);
//		return parm;
		return orderParm;
	}

	/**
	 * 泰心医疗卡读卡 =========================pangben modify 20110808
	 */
	public void onEKT() {// modify by kangy 20170307
		//kangy  脱卡还原    p3 = EKTReadCard.getInstance().readEKT();
		p3 = EKTIO.getInstance().TXreadEKT();
		EKT(p3);
		/*// 南京医保卡读卡操作
		// 泰心医疗卡操作
		p3 = EKTIO.getInstance().TXreadEKT();
		// System.out.println("P3=================" + p3);
		// 6.释放读卡设备
		// int ret99 = NJSMCardDriver.FreeReader(ret0);
		// 7.注销TFReader.dll
		// int ret100 = NJSMCardDriver.close();
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // 医疗卡类型
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql
					.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // 南京医保卡
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // 泰心医疗卡
			this.setValue("PAY_WAY", "PAY_MEDICAL_CARD"); // 支付方式修改
			this.setValue("CONTRACT_CODE", "");
			callFunction("UI|CONTRACT_CODE|setEnabled", false); // 记账单位不可编辑
		}
		// 通过身份证号查找是否存在此病患信息
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//其他证件不可编辑
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("此医疗卡无效");
			return;
		}
		// 南京医保卡操作
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// ===zhangp 20120318 endg
*/	}
	public void EKT(TParm p3){//kangy
		StringBuffer sql = new StringBuffer();
		int typeEKT = -1; // 医疗卡类型
		if (null != p3 && p3.getValue("identifyNO").length() > 0) {
			sql
					.append("SELECT * FROM SYS_PATINFO WHERE MR_NO in (select max(MR_NO) from SYS_PATINFO");
			typeEKT = 1; // 南京医保卡
			sql.append(" WHERE IDNO='" + p3.getValue("identifyNO").trim()
					+ "' ) ");
		} else if (null != p3 && p3.getValue("MR_NO").length() > 0) {
			// sql
			// .append("SELECT A.MR_NO,A.NHI_NO,B.BANK_CARD_NO FROM SYS_PATINFO A,EKT_ISSUELOG B WHERE A.MR_NO = B.MR_NO AND B.CARD_NO ='"
			// + p3.getValue("MR_NO")
			// + p3.getValue("SEQ")
			// + "' AND WRITE_FLG='Y'");
			typeEKT = 2; // 泰心医疗卡
			if(null!=p3.getValue("READ_TYPE")&&"INSCARD".equals(p3.getValue("READ_TYPE"))){
				this.setValue("PAY_WAY", "PAY_INS_CARD"); // 支付方式修改
			}else if(null!=p3.getValue("READ_TYPE")&&"IDCARD".equals(p3.getValue("READ_TYPE"))){
				this.setValue("PAY_WAY", "PAY_CASH"); // 支付方式修改
			}else {
			this.setValue("PAY_WAY", "PAY_MEDICAL_CARD"); // 支付方式修改
			}
			this.setValue("CONTRACT_CODE", "");
			callFunction("UI|CONTRACT_CODE|setEnabled", false); // 记账单位不可编辑
		}
		// 通过身份证号查找是否存在此病患信息
		// callFunction("UI|FOREIGNER_FLG|setEnabled", false);//其他证件不可编辑
		if (typeEKT > 0) {
			onReadTxEkt(p3, typeEKT);
		} else {
			this.messageBox("此医疗卡无效");
			return;
		}
		// 南京医保卡操作
		if (typeEKT == 1) {
			NJSMCardDriver.close();
			NJSMCardYYDriver.close();
		}
		setValue("EKT_CURRENT_BALANCE", p3.getDouble("CURRENT_BALANCE"));
		// ===zhangp 20120318 end
	}

	/**
	 * 身份正读卡操作 ==============pangben 2013-3-18
	 */
	public void onReadIdCard() {// modify by kangy
		//kangy 脱卡还原   start
		/*TParm idParm=new TParm();
		TParm infoParm=new TParm();
		dev_flg=EKTTool.getInstance().Equipment(Operator.getIP());
		if(dev_flg){
			infoParm=EKTReadCard.getInstance().readIDCard();
		}else{
			idParm= IdCardO.getInstance().readIdCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		 p3=new TParm();
		 String sql="SELECT A.MR_NO,B.PAT_NAME,B.SEX_CODE,B.BIRTH_DATE,B.IDNO,A.EKT_CARD_NO AS CARD_NO,A.CARD_NO AS PK_CARD_NO, A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,C.CURRENT_BALANCE" 
	              +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C, SYS_PATINFO D WHERE " 
	              //+" D.IDNO='430103' "
	              +" D.IDNO='"+idParm.getValue("IDNO")+"'"
	              +" AND D.MR_NO=A.MR_NO "
	              +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
			 infoParm=new TParm(TJDODBTool.getInstance().select(sql));
			}
			if(infoParm.getCount()==0){
				p3=idParm;
			}
			if(infoParm.getCount()>1){
				p3 = (TParm) openDialog(
						"%ROOT%\\config\\reg\\REGPatMrNoSelect.x", infoParm);
			}else
			p3=infoParm.getRow(0);
		p3.setData("READ_TYPE","IDCARD");
		EKT(p3);*/
		//kangy   脱卡还原    end
		TParm idParm = IdCardO.getInstance().readIdCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		if (idParm.getCount() > 0) {// 多行数据显示
			if (idParm.getCount()==1) {//pangben 2013-8-8 只存在一条数据
				//onQueryNO(idParm.getValue("MR_NO",0));//脱卡还原      因脱卡过程中插入其他版本   方法名更换
				onQueryMrNO(idParm.getValue("MR_NO",0));
			}else{
				Object obj = openDialog("%ROOT%\\config\\sys\\SYSPatChoose.x",
						idParm);
				TParm patParm = new TParm();
				if (obj != null) {
					patParm = (TParm) obj;
					//onQueryNO(patParm.getValue("MR_NO"));//脱卡还原      因脱卡过程中插入其他版本   方法名更换
					onQueryMrNO(patParm.getValue("MR_NO"));
				}else{
					return ;
				}
			}
			setValue("VISIT_CODE_F", "Y"); // 复诊
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// 简拼
			setPatName1();// 设置英文
		} else {
			String sql="SELECT MR_NO,PAT_NAME,IDNO,SEX_CODE,BIRTH_DATE,POST_CODE,ADDRESS FROM SYS_PATINFO WHERE PAT_NAME LIKE '"
				+idParm.getValue("PAT_NAME")+"%'";
			TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));
			if (infoParm.getCount()<=0) {
				this.messageBox(idParm.getValue("MESSAGE"));
				setValue("VISIT_CODE_C", "Y"); // 默认初诊
				callFunction("UI|MR_NO|setEnabled", false); // 病案号不可编辑--初诊操作
			}else{
				this.messageBox("存在相同姓名的病患信息");
				this.grabFocus("PAT_NAME");//默认选中
			}
			this.setValue("PAT_NAME", idParm.getValue("PAT_NAME"));
			this.setValue("IDNO", idParm.getValue("IDNO"));
			this.setValue("BIRTH_DATE", idParm.getValue("BIRTH_DATE"));
			this.setValue("SEX_CODE", idParm.getValue("SEX_CODE"));
			this.setValue("ADDRESS", idParm.getValue("RESID_ADDRESS"));// 地址
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// 简拼
			setPatName1();// 设置英文
			
		}
	}

	/**
	 * 医疗卡读卡操作
	 * 
	 * @param IDParm
	 *            TParm
	 * @param typeEKT
	 *            int
	 */
	private void onReadTxEkt(TParm IDParm, int typeEKT) {
		// TParm IDParm = new TParm(TJDODBTool.getInstance().select(sql));
		// 通过身份证号查找是否存在次病患
		if (IDParm.getValue("MR_NO").length() > 0) {
			setValue("MR_NO", IDParm.getValue("MR_NO")); // 存在将病案号显示
			onQueryNO(false); // 执行赋值方法
			setValue("NHI_NO", IDParm.getValue("NHI_NO")); // ==-============pangben
			// modify
			// 20110808
			tjINS = true; // 天津医保使用，判断是否执行了医疗卡操作
			//callFunction("UI|PAY_WAY|setEnabled", false); // 支付类别 20180330 pengtianting 支付方式放开
		} else {
			this.messageBox("此医疗卡无效"); // 不存在显示市民卡上的信息：身份证号、名称、医保号
			switch (typeEKT) {
			// 南京医保卡 没有此病患信息时执行赋值操作
			case 1:
				this.setValue("IDNO", p3.getValue("identifyNO")); // 身份证号
				this.setValue("NHI_NO", p3.getValue("siNO")); // 医保号
				this.setValue("PAT_NAME", p3.getValue("patientName").trim()); // 姓名
				break;
			// 泰心医疗卡没有此病患信息时执行赋值操作
			case 2:

				// this.setValue("MR_NO",p3.getValue("MR_NO"));
				txEKT = true; // 泰心医疗卡写卡操作管控
				break;
			}
			// this.setValue("VISIT_CODE_C","N");
			callFunction("UI|MR_NO|setEnabled", false); // 病案号不可编辑
			this.grabFocus("PAT_NAME");
			setValue("VISIT_CODE_C", "Y"); // 默认初诊
		}
	}
	
	public void TXonEKTRecharge(TParm p){// add by kangy 
		if (null != p && p.getValue("MR_NO").length() > 0) {
			this.setValue("EKTMR_NO", p.getValue("MR_NO"));
			String EKTCARD_CODE = p.getData("CARD_NO").toString();
			this.setValue("EKTCARD_CODE", EKTCARD_CODE);
			this.setValue("CURRENT_BALANCE", p.getValue("CURRENT_BALANCE"));
			return;
		} else {
			this.messageBox(p.getErrText());
		}
		clearEKTValue();
	}
	
	//医疗卡信息读卡操作
	public void readCard(){
		//kangy 脱卡还原    start
		/*IccCardRWUtil DEV=new IccCardRWUtil();
		if(dev_flg){
			String cardType=DEV.getCardType();
			if("EKTCard".equals(cardType)){
				TXonEKTR();
			} else if("IDCard".equals(cardType)){
				TXonReadIdCardR();
			} else if("INSCard".equals(cardType)){
				TXonReadInsCardR();
			}
		}else*/
			//kangy 脱卡还原    end	
			TXonEKTR();
	}
	//kangy 脱卡还原      start  医疗卡充值读取身份证，医保卡两个方法
	//医疗卡充值读取身份证
	/*public void TXonReadIdCardR(){// add by kangy 右侧医疗卡充值读取身份证
		TParm idParm= EKTReadCard.getInstance().readIDCard();
		if (idParm.getErrCode()<0) {
			this.messageBox(idParm.getErrText());
			return;
		}
		if(idParm.getCount()==0){
			this.messageBox("该病人没有医疗卡，请执行制卡操作");
			return;
		}
		if(idParm.getCount()>1){
			p3 = (TParm) openDialog(
					"%ROOT%\\config\\reg\\REGPatMrNoSelect.x", idParm);
		}else
			p3=idParm.getRow(0);
		TXonEKTRecharge(p3);
	}
	//医疗卡充值读取医保卡
	public void TXonReadInsCardR(){//add by kangy 右侧医疗卡充值读取医保卡
		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		//医院编码@费用发生时间@类别
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// 费用发生时间	
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+admDate+"@"+"1";
		parm.setData("ADVANCE_CODE",advancecode);
		parm.setData("ADVANCE_TYPE","1");//正常
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSReadInsCard.x", parm);
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		int returnType = insParm.getInt("RETURN_TYPE"); // 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			return;
		}
		String sql="SELECT  B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
	               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E,SYS_PATINFO D"
	               + " WHERE "
	               //+" E.NHI_NO='6217250200000958634'"
	               +" E.NHI_NO='"+insParm.getParm("opbReadCardParm").getValue("CARD_NO").trim()+"' "
	               //+" D.IDNO='"+insParm.getParm("opbReadCardParm").getValue("SID").trim()+"'"
	               +" AND E.MR_NO=A.MR_NO "
	               + " AND A.MR_NO=D.MR_NO "
	                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
		TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));
		TXonEKTRecharge(insParm.getRow(0));
	}*/
	//kangy 脱卡还原     end
	/**
	 * 医疗卡读卡
	 */
	public void TXonEKTR() {
		
		//==start====add by kangy 20160912 ===读取医疗卡刷新下一票号
		ektinvoice=new BilInvoice();
		 callFunction("UI|BIL_CODE|setValue", ektinvoice.initBilInvoice("EKT").getUpdateNo());
     	//==end====add by kangy 20160912
		 //kangy  脱卡还原     start
		/* TParm p=new TParm();
		if(dev_flg)
		 p = EKTReadCard.getInstance().readEKT();
		else
			p= EKTReadCard.getInstance().TXreadEKT();*/
		//kangy  脱卡还原     end
		TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("此医疗卡无效");
			return;
		}
		if (null != p && p.getValue("MR_NO").length() > 0) {
			// zhangp 20111231 修改医疗卡号
			this.setValue("EKTMR_NO", p.getValue("MR_NO"));
			String EKTCARD_CODE = p.getData("CARD_NO").toString();
			this.setValue("EKTCARD_CODE", EKTCARD_CODE);
			this.setValue("CURRENT_BALANCE", p.getValue("CURRENT_BALANCE"));
			return;
		} else {
			this.messageBox(p.getErrText());
		}
		// zhangp 20111227
		clearEKTValue();
	}

	/**
	 * 充值操作
	 */
	public void TXonEKTW() {
		//==start===add by kangy ==20160826====
		if(this.getValue("EKTMR_NO").toString().length()<=0){
			this.messageBox("请先执行读卡操作");
			return;
		}
		//==end===add by kangy ==20160826====
		
		if (this.getValueDouble("TOP_UPFEE") <= 0) {
			this.messageBox("充值金额不正确");
			return;
		}
		if (((TTextFormat) this.getComponent("GATHER_TYPE")).getText().length() <= 0) {
			this.messageBox("支付方式不可以为空值");
			return;
		}
		String sql="SELECT B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE" 
	              +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C, SYS_PATINFO D WHERE " 
	              +" A.MR_NO='"+this.getValue("EKTMR_NO").toString()+"'"
	              +" AND D.MR_NO=A.MR_NO "
	              +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
			TParm p=new TParm(TJDODBTool.getInstance().select(sql));
			p=p.getRow(0);
		//TParm p = EKTIO.getInstance().TXreadEKT();
		if (p.getErrCode() < 0) {
			this.messageBox("此医疗卡无效");
			return;
		}
		// zhangp 20111227
		pat = Pat.onQueryByMrNo(p.getValue("MR_NO"));
		TParm parm = new TParm();
		parm.setData("SEQ", p.getValue("SEQ")); // 编号
		parm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2)); // 金额
		parm.setData("MR_NO", p.getValue("MR_NO")); // 病案号

		if (null != p && p.getValue("MR_NO").length() > 0) {
			// result.setData("CURRENT_BALANCE",
			// this.getValue("CURRENT_BALANCE"));
			//yanjing 注
//			TParm result = EKTIO.getInstance().TXwriteEKTATM(parm,
//					p.getValue("MR_NO"));
//			if (result.getErrCode() < 0) {
//				this.messageBox_("医疗卡充值操作失败");
//				return;
//			}
			insbilPay(parm, p);
		} else {
			this.messageBox("此医疗卡无效");
		}
		clearEKTValue();
		// =====zhangp 20120403 start
		//onEKT();
	}

	/**
	 * 医疗卡充值操作
	 * 
	 * @param parm
	 *            TParm
	 * @param p
	 *            TParm
	 */
	private void insbilPay(TParm parm, TParm p) {
		//==add by kangy===2016010
		ektinvoice=new BilInvoice();
		TParm checkparm=new TParm();
    	checkparm.setData("RECP_TYPE","EKT");
    	checkparm.setData("INV_NO",this.getValue("BIL_CODE"));
    	checkparm.setData("CASHIER_CODE",Operator.getID());
    	TParm res=BILInvoiceTool.getInstance().checkUpdateNo(checkparm);
    	if(res.getCount("RECP_TYPE")>0){
    		this.messageBox("该票据已使用！");
    		onClear();
    		return;
    	}
    	if(!compareInvno(ektinvoice.initBilInvoice("EKT").getStartInvno(),ektinvoice.initBilInvoice("EKT").getEndInvno(),this.getValue("BIL_CODE").toString())){
			this.messageBox("票号超出范围");
			onClear();
			return;
		}
		// zhangp 20111227
		TParm result = new TParm();
		parmSum = new TParm();
		parmSum.setData("CARD_NO", pat.getMrNo() + p.getValue("SEQ"));
		parmSum.setData("CURRENT_BALANCE", parm.getValue("CURRENT_BALANCE"));
		parmSum.setData("CASE_NO", "none");
		parmSum.setData("NAME", pat.getName());
		parmSum.setData("MR_NO", pat.getMrNo());
		parmSum.setData("ID_NO", null != pat.getIdNo()
				&& pat.getIdNo().length() > 0 ? pat.getIdNo() : "none");
		parmSum.setData("OPT_USER", Operator.getID());
		parmSum.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		parmSum.setData("OPT_TERM", Operator.getIP());
		parmSum.setData("FLG", false);
		parmSum.setData("ISSUERSN_CODE", "充值"); // 发卡原因
		parmSum.setData("GATHER_TYPE", this.getValue("GATHER_TYPE")); // 支付方式
		parmSum.setData("GATHER_TYPE_NAME", this.getText("GATHER_TYPE")); // 支付方式名称
		parmSum.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // 充值金额
		parmSum.setData("SEX_TYPE", this.getValue("SEX_CODE")); // 性别
		parmSum.setData("DESCRIPTION", this.getValue("DESCRIPTION")); // 备注
		parmSum.setData("BIL_CODE", this.getValue("BIL_CODE")); // 票据号
		parmSum.setData("PRINT_NO",ektinvoice.initBilInvoice("EKT").getUpdateNo());//票据号====kangy
		 parmSum.setData("CREAT_USER", Operator.getID()); //执行人员//=====yanjing
		 
		 TParm inFeeParm=new TParm();
			inFeeParm.setData("RECP_TYPE","EKT");
			inFeeParm.setData("INV_NO",ektinvoice.initBilInvoice("EKT").getUpdateNo());
			//inFeeParm.setData("RECEIPT_NO",bil_business_no);
			inFeeParm.setData("CASHIER_CODE",Operator.getID());
			inFeeParm.setData("AR_AMT",this.getValue("TOP_UPFEE"));
			inFeeParm.setData("CANCEL_FLG","0");
			inFeeParm.setData("CANCEL_USER","");
			inFeeParm.setData("CANCEL_DATE","");
			inFeeParm.setData("OPT_USER",Operator.getID().toString());
		    //infeeParm.setData("OPT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
			inFeeParm.setData("OPT_TERM",Operator.getIP().toString());
			inFeeParm.setData("ACCOUNT_FLG","");
			inFeeParm.setData("ACCOUNT_SEQ","");
			inFeeParm.setData("ACCOUNT_USER","");
			inFeeParm.setData("ACCOUNT_DATE","");
			inFeeParm.setData("PRINT_USER",Operator.getID());
			inFeeParm.setData("PRINT_DATE",sdf.format(TJDODBTool.getInstance().getDBTime()));
			inFeeParm.setData("ADM_TYPE","T");
			inFeeParm.setData("STATUS","0");
			parmSum.setData("infeeparm",inFeeParm.getData());
				
		    String updateno = StringTool.addString(ektinvoice.initBilInvoice("EKT").getUpdateNo());
         TParm updatanoParm=new TParm();
         BilInvoice bilInvo=ektinvoice.initBilInvoice("EKT");
         updatanoParm.setData("UPDATE_NO",updateno);
         updatanoParm.setData("RECP_TYPE","EKT");
         updatanoParm.setData("STATUS",bilInvo.getStatus());
         updatanoParm.setData("CASHIER_CODE",bilInvo.getCashierCode());
         updatanoParm.setData("START_INVNO",bilInvo.getStartInvno());
        parmSum.setData("updatanoparm",updatanoParm.getData());
	 
		// 明细表参数
		TParm feeParm = new TParm();
		feeParm.setData("ORIGINAL_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)); // 原金额
		feeParm.setData("BUSINESS_AMT", StringTool.round(this
				.getValueDouble("TOP_UPFEE"), 2)); // 充值金额
		feeParm.setData("CURRENT_BALANCE", StringTool.round(p
				.getDouble("CURRENT_BALANCE"), 2)
				+ StringTool.round(this.getValueDouble("TOP_UPFEE"), 2));
		// EKT_ACCNTDETAIL 数据
		parmSum.setData("businessParm", getBusinessParm(parmSum, feeParm)
				.getData());
		// zhangp 20120112 EKT_BIL_PAY 加字段
		parmSum.setData("STORE_DATE", TJDODBTool.getInstance().getDBTime()); // 售卡操作时间
		parmSum.setData("PROCEDURE_AMT", 0.00); // PROCEDURE_AMT
		// bil_pay 充值表数据
		parmSum.setData("billParm", getBillParm(parmSum, feeParm).getData());
		// 更新余额
		result = TIOM_AppServer.executeAction("action.ekt.EKTAction",
				"TXEKTonFee", parmSum); //
		callFunction("UI|tButton_5|setEnabled", false);//充值按钮不可以连续点击操作===pangben 2013-7-1
		if (result.getErrCode() < 0) {
			this.messageBox("医疗卡充值失败");
			callFunction("UI|tButton_5|setEnabled", true);//充值按钮不可以连续点击操作===pangben 2013-7-1
//			parm = EKTIO.getInstance().TXwriteEKTATM(p, p.getValue("MR_NO"));
//			if (parm.getErrCode() < 0) {
//				System.out.println("回冲医疗卡金额失败");
//			}
		} else {
			printBil = true;
			this.messageBox("医疗卡充值成功");
			callFunction("UI|tButton_5|setEnabled", true);//充值按钮不可以连续点击操作===pangben 2013-7-1
			String bil_business_no = result.getValue("BIL_BUSINESS_NO"); // 收据号
			try {
				onPrint(bil_business_no, "");
			} catch (Exception e) {
				this.messageBox("打印出现问题,请执行补印操作");
				// TODO: handle exception
			}
		}
	}

	/**
	 * 写医疗卡
	 */
	public void writeCard() {
	}

	/**
	 * 指定交易信息
	 */
	public void queryConusmeByID() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("无卡");

			return;
		}
		result = EktDriver.queryConusmeByID("1008250000000021");
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * 冲证
	 */
	public void unConsume() {
		if (EktDriver.init() != 1) {
			this.messageBox("EKTDLL init err!");
			return;
		}
		String result = EktDriver.open();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}
		result = EktDriver.hasCard();
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox("无卡");

			return;
		}
		result = EktDriver.unConsume(1000, "sys", "1008250000000021",
				StringTool.getString(new Date(), "yyyy-MM-dd HH:mm:ss"));
		if (!result.substring(0, 2).equals("00")) {
			this.messageBox(result);
			return;
		}

		EktDriver.close();
		this.messageBox(result);

	}

	/**
	 * 医疗卡条码
	 */
	public void onEKTBarcode() {
		TParm printParm = new TParm();
		if ((ektCard != null || ektCard.length() != 0)
				&& this.getValueString("MR_NO") != null) {
			printParm.setData("mrNo", "TEXT", this.getValueString("MR_NO")); // 病案号
			printParm.setData("patName", "TEXT", this
					.getValueString("PAT_NAME")); // 病患姓名
			printParm.setData("barCode", "TEXT", ektCard); // 条码号
			this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGEktCard.jhw",
					printParm);
		} else {
			this.messageBox("请先读医疗卡");
		}

	}

	/**
	 * 设置SESSION combo的门急属性，并返回当前的SESSION_CODE
	 * 
	 * @return String sessionCode
	 */
	public String initSessionCode() {
		// 为了界面的SESSION_CODE显示门急诊区别，放置一个不显示的TEXTFIELD。
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		return sessionCode;
	}

	/**
	 * 清卡 ===================pangben modify 20110808
	 */
	public void clearCard() {
		// EKTIO.getInstance().saveMRNO1(parm, this,true);
		if (null == p3) {
			this.messageBox("没有需要清卡的数据");
			return;
		}

		p3.setData("identifyNO", this.getValue("IDNO"));
		p3.setData("siNO", this.getValue("NHI_NO"));
		p3.setData("patientName", this.getValue("PAT_NAME"));
		boolean temp = EKTIO.getInstance().writeEKT(p3, true);
		if (temp) {
			// 修改将此病患医保卡号清空
			StringBuffer sql = new StringBuffer();
			sql
					.append("UPDATE SYS_PATINFO SET NHI_NO='',OPT_DATE=SYSDATE WHERE MR_NO='"
							+ this.getValueString("MR_NO").trim() + "'");
			TParm result = new TParm(TJDODBTool.getInstance().update(
					sql.toString()));
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				this.messageBox("清卡失败");
				return;
			}
			this.messageBox("清卡成功");
		}
	}

	public void onClearRefresh() {
		this.initReg();
		clearValue(" PAT_NAME;PAT_NAME1;PY1;IDNO;FOREIGNER_FLG; "
				+ " BIRTH_DATE;SEX_CODE;TEL_HOME;POST_CODE;STATE;CITY;ADDRESS; "
				+ " CTZ2_CODE;CTZ3_CODE;REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ;SERVICE_LEVEL");
		if (admType.endsWith("E")) {
			setValue("ERD_LEVEL", "");
			this.setValue("ARRIVE_DATE", SystemTool.getInstance().getDate().toString().substring(0,16).replaceAll("-", "/"));
		}
		this.callFunction("UI|Table1|clearSelection");
		this.callFunction("UI|Table2|clearSelection");
		this.callFunction("UI|Table3|removeRowAll");
		// 设置默认服务等级
		setValue("SERVICE_LEVEL", "1");
		selectRow = -1;
		// 解锁病患
//		if (pat != null)
//			PatTool.getInstance().unLockPat(pat.getMrNo());
	}

	/**
	 * 控件可编辑设置
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setControlEnabled(boolean flg) {
		callFunction("UI|REGMETHOD_CODE|setEnabled", flg);
		callFunction("UI|ADM_DATE|setEnabled", flg);
		callFunction("UI|SESSION_CODE|setEnabled", flg);
		callFunction("UI|DEPT_CODE|setEnabled", flg);
		callFunction("UI|DR_CODE|setEnabled", flg);
		callFunction("UI|CLINICROOM_NO|setEnabled", flg);
		callFunction("UI|CLINICTYPE_CODE|setEnabled", flg);
		callFunction("UI|REG_FEE|setEnabled", flg);
		callFunction("UI|CLINIC_FEE|setEnabled", flg);
	}

	/**
	 * 获得费用
	 */
	public void showXML() {
		TParm parm = NJCityInwDriver.getPame("c:/NGYB/mzghxx.xml");
		feeShow = true;
		// String
		// mr_no=parm.getValue("TBR").trim().substring(1,parm.getValue("TBR").trim().indexOf("]"));

		// System.out.println("parm:::"+parm);
		// if(this.getValueString("MR_NO").trim().equals(mr_no)){
		if (null == parm)
			return;
		// feeIstrue = true;
		this.setValue("FeeY", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]"))); // 收费
		this.setValue("REG_FEE", parm.getValue("GHF").substring(1,
				parm.getValue("GHF").indexOf("]"))); // 挂号费
		this.setValue("CLINIC_FEE", parm.getValue("ZLF").substring(1,
				parm.getValue("ZLF").indexOf("]"))); // 诊查费
		this.setValue("FeeS", parm.getValue("XJZF").substring(1,
				parm.getValue("XJZF").indexOf("]")));
		// }
	}

	/**
	 * 退挂金额显示 医保中心获得的价格显示 =====================pangben modify 20110815
	 * 
	 * @param caseNo
	 *            String
	 */
	private void unregFeeShow(String caseNo) {
		int feeunred = -1;
		StringBuffer sql = new StringBuffer();
		sql
				.append("SELECT REG_FEE,CLINIC_FEE,AR_AMT FROM BIL_REG_RECP WHERE CASE_NO='"
						+ caseNo + "'"); // 获得退挂的金额
		// System.out.println("sql:::::"+sql);
		TParm result = new TParm(TJDODBTool.getInstance()
				.select(sql.toString()));
		this.setValue("FeeY", result.getDouble("AR_AMT", 0) * feeunred); // 总费用
		this.setValue("REG_FEE", result.getDouble("REG_FEE", 0) * feeunred); // 挂号
		this.setValue("CLINIC_FEE", result.getDouble("CLINIC_FEE", 0)
				* feeunred); // 诊疗
		this.setValue("FeeS", result.getDouble("AR_AMT", 0) * feeunred); // 收取费用
	}

	/**
	 * 正常流程没有记账的操作 flg 判断是否是记账数据
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegNo(String caseNo, boolean flg) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm unRegParm = new TParm();

		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", optUser);

		// 查询当前病患是否产生费用
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		
		//add by huangtt 20160815判断该数据是否已打票
		if(unRegRecpParm.getValue("PRINT_NO", 0).length() == 0){
			this.messageBox("请去Q医指定地点打票后，再进行退号");
			return;
		}
		
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		inInvRcpParm.setData("RECP_TYPE", "REG");// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			reSetReg(unRegParm, caseNo, flg, "onUnRegForEKT", "onUnReg", "Y");
		} else {
			this.messageBox("已产生费用,不能退挂!");
			return;
		}
	}

	/**
	 * 记账退挂操作:BIL_STATUS=2 已经结算退挂操作
	 * 
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 */
	private void onUnRegYes2(String caseNo, boolean flg) {
		onUnRegNo(caseNo, flg);
	}

	/**
	 * 记账退挂操作:BIL_STATUS=1 判断是否产生费用，如果没有产出费用直接添加、修改操作BIL_REG_RECP 如果已经产生费用不可以退挂
	 * 
	 * @param caseNo
	 *            String
	 */
	private void onUnRegYes1(String caseNo) {
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm patFeeParm = new TParm();
		patFeeParm.setData("CASE_NO", caseNo);
		patFeeParm.setData("REGCAN_USER", Operator.getID());
		TParm unRegParm = new TParm();
		TParm unRegRecpParm = BILREGRecpTool.getInstance().selDataForUnReg(
				caseNo);
		
		//add by huangtt 20160815判断该数据是否已打票
		if(unRegRecpParm.getValue("PRINT_NO", 0).length() == 0){
			this.messageBox("请去Q医指定地点打票后，再进行退号");
			return;
		}
		
		String recpNo = unRegRecpParm.getValue("RECEIPT_NO", 0);
		TParm inInvRcpParm = new TParm();
		inInvRcpParm.setData("RECEIPT_NO", recpNo);
		inInvRcpParm.setData("RECP_TYPE", "REG");
		inInvRcpParm.setData("CANCEL_FLG", 0);// ======pangben 2012-3-23
		TParm unInvRcpParm = BILInvrcptTool.getInstance().selectAllData(
				inInvRcpParm);
		unRegParm.setData("CASE_NO", caseNo);
		unRegParm.setData("REGCAN_USER", optUser);
		unRegParm.setData("OPT_USER", optUser);
		unRegParm.setData("OPT_TERM", optTerm);
		unRegParm.setData("RECP_PARM", unRegRecpParm.getData());
		unRegParm.setData("INV_NO", unInvRcpParm.getData("INV_NO", 0));
		unRegParm.setData("RECEIPT_NO", recpNo);
		unRegParm.setData("OPT_NAME", Operator.getName());
		// 查询当前病患是否产生费用
		TParm selPatFeeForREG = OrderTool.getInstance().selPatFeeForREG(
				patFeeParm);
		if (selPatFeeForREG.getDouble("AR_AMT", 0) == 0) {
			// 没有执行结算的费用不用退挂
			this.messageBox("没有执行结算,不用退费");
			// 直接添加、修改操作BIL_REG_RECP
			// 现金退挂动作
			reSetReg(unRegParm, caseNo, false, "onUnRegForStatusEKT",
					"onUnRegStatus", "Y");
		} else {
			// 已产生费用
			this.messageBox("已产生费用,不能退挂!");
		}

	}

	/**
	 * 读医保卡
	 */
	public void readINSCard() {//modify by kangy 20170307
		/*String sql="SELECT distinct B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
	               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E WHERE E.NHI_NO='6217250200000958634' "
	               +" AND E.MR_NO=A.MR_NO "
	                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
		TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));
		p3=infoParm.getRow(0);
		p3.setData("READ_TYPE","INSCARD");*/
		String payWay = this.getValueString("PAY_WAY");// 支付方式
		// 天津医保卡操作
		tjReadINSCard(payWay);
	}

	/**
	 * 医疗卡保存
	 * 
	 * @return boolean
	 */
	public boolean onSaveINSData() {
		boolean result = false;
		return result;
	}

	/**
	 * 清空医疗卡信息
	 */
	public void ektOnClear() {
		clearValue("EKTMR_NO;EKTCARD_CODE;CURRENT_BALANCE;TOP_UPFEE;SUM_EKTFEE");
		//清空时刷新票号
		ektinvoice=invoice.initBilInvoice("EKT");
		initBilInvoice(ektinvoice.initBilInvoice("EKT"));
	}

	/**
	 * 门诊挂号收据打印
	 * 
	 * @param parm
	 *            TParm
	 * 
	 */
	private void onPrint(TParm parm) {
		// //处理小数
		// sOTOT_Amt = ""+ TiMath.round( Double.parseDouble(sOTOT_Amt),2);

		parm.setData("DEPT_NAME", "TEXT", parm.getValue("DEPT_CODE_OPB")
				+ "   (" + parm.getValue("CLINICROOM_DESC_OPB") + ")"); // 科室诊室名称
		// 显示方式:科室(诊室)
		parm.setData("CLINICTYPE_NAME", "TEXT", this.getText("CLINICTYPE_CODE")
				+ "   (" + parm.getValue("QUE_NO_OPB") + "号)"); // 号别
		// 显示方式:号别(诊号)
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		parm.setData("BALANCE_NAME", "TEXT", "余 额"); // 余额名称
		DecimalFormat df = new DecimalFormat("########0.00");
		// parm.setData("CURRENT_BALANCE", "TEXT", "￥ "
		// + df.format(Double.parseDouble(ektNewSum == null
		// || "".equals(ektNewSum) ? "0.00" : ektNewSum))); // 医疗卡剩余金额
		parm.setData("CURRENT_BALANCE","TEXT","￥ "
		+ df.format(Double.parseDouble(ektNewSum == null
		|| "".equals(ektNewSum) ? ""+ df.format((Double.parseDouble(getValueString("EKT_CURRENT_BALANCE").equals("") ? "0": getValueString("EKT_CURRENT_BALANCE"))- 
		parm.getDouble("TEXT","REGFEE") - parm.getDouble("TEXT","CLINICFEE"))): ektNewSum))); // 医疗卡剩余金额
		if (insFlg) {
			// =====zhangp 20120229 modify start
			parm.setData("PAY_DEBIT", "TEXT", "医保:"
					+ StringTool.round((parm.getDouble("INS_SUMAMT") - parm
							.getDouble("ACCOUNT_AMT_FORREG")), 2)); // 医保支付
			parm.setData("PAY_CASH", "TEXT", "现金:"
					+ StringTool.round((parm.getDouble("TOTAL", "TEXT") - parm
							.getDouble("INS_SUMAMT")), 2)); // 现金
			parm
					.setData("PAY_ACCOUNT", "TEXT", "账户:"
							+ StringTool.round(parm
									.getDouble("ACCOUNT_AMT_FORREG"), 2)); // 账户
			// =====zhangp 20120229 modify end
			String sql = "SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SP_PRESON_TYPE' AND ID='"
					+ insParm.getParm("opbReadCardParm").getValue(
							"SP_PRESON_TYPE") + "'";// 医保特殊人员身份显示
			TParm insPresonParm = new TParm(TJDODBTool.getInstance()
					.select(sql));
			if (insPresonParm.getErrCode() < 0) {

			} else {
				parm.setData("SPC_PERSON", "TEXT", insPresonParm.getValue(
						"CHN_DESC", 0));
			}

		}
		parm.setData("DATE", "TEXT", yMd); // 日期
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		// ===zhangp 20120313 start
		if ("1".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "门大联网已结算");
			// parm.setData("Cost_class", "TEXT", "门统");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		} else if ("2".equals(insType) || "3".equals(insType)) {
			parm.setData("TEXT_TITLE", "TEXT", "门特联网已结算");
			// parm.setData("Cost_class", "TEXT", "门特");
			if (reg.getAdmType().equals("E")) {
				parm.setData("TEXT_TITLE", "TEXT", "急诊联网已结算");
			}
		}
		// ===zhangp 20120313 end
        parm.setData("RECEIPT_NO", "TEXT", reg.getRegReceipt().getReceiptNo());//add by wanglong 20121217
//		this.openPrintDialog("%ROOT%\\config\\prt\\REG\\REGRECPPrint.jhw",
//				parm, true);
	    this.openPrintDialog(IReportTool.getInstance().getReportPath("REGRECPPrint.jhw"),
                             IReportTool.getInstance().getReportParm("REGRECPPrint.class", parm), true);//报表合并modify by wanglong 20130730
	}

	/**
	 * 天津医保卡读卡操作
	 * 
	 * @param payWay
	 *            String
	 */
	private void tjReadINSCard(String payWay) {
		//yanjing 删除对SERVICE_LEVEL的清空 20130807
		clearValue("REG_CZT2;DEPT_CODE;DR_CODE; "
				+ " CLINICROOM_NO;CLINICTYPE_CODE;REG_FEE;CLINIC_FEE;REMARK;"
				+ " CONTRACT_CODE;FeeY;FeeS;FeeZ ");
		initSchDay();
	/*	if (null == pat && !this.getValueBoolean("VISIT_CODE_C")) {
			this.messageBox("请先获得病患信息");
			return;
		}
*/
		TParm parm = new TParm();
		parm.setData("MR_NO", "");
		parm.setData("CARD_TYPE", 2); // 读卡请求类型（1：购卡，2：挂号，3：收费，4：住院,5 :门特登记）
		//医院编码@费用发生时间@类别
		String admDate = StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd");// 费用发生时间	
		String advancecode = regionParm.getValue("NHI_NO", 0)+"@"+admDate+"@"+"1";
		parm.setData("ADVANCE_CODE",advancecode);
		parm.setData("ADVANCE_TYPE","1");//正常
		//kangy 脱卡还原    start
		/*insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSReadInsCard.x", parm);*/
		//kangy 脱卡还原   end
		insParm = (TParm) openDialog(
				"%ROOT%\\config\\ins\\INSConfirmApplyCard.x", parm);
		if (null == insParm) {
			this.setValue("PAY_WAY", payWay); // 支付方式修改
			return;
		}
		int returnType = insParm.getInt("RETURN_TYPE"); // 读取状态 1.成功 2.失败
		if (returnType == 0 || returnType == 2) {
			this.messageBox("读取医保卡失败");
			this.setValue("PAY_WAY", payWay); // 支付方式修改
			return;
		}
		
	/*	String sql="SELECT  B.SEX_CODE,C.CURRENT_BALANCE,A.EKT_CARD_NO AS CARD_NO, A.CARD_NO AS PK_CARD_NO,A.MR_NO,A.CARD_SEQ AS SEQ,A.BANK_CARD_NO,B.PAT_NAME,B.IDNO,B.BIRTH_DATE "
	               +" FROM EKT_ISSUELOG A,SYS_PATINFO B,EKT_MASTER C,REG_PATADM E,SYS_PATINFO D"
	               + " WHERE "
	               //+ " D.IDNO='6217250200000958634' "
	               +" E.NHI_NO='"+insParm.getParm("opbReadCardParm").getValue("CARD_NO").trim()+"' "
	              //+" D.IDNO='"+insParm.getParm("opbReadCardParm").getValue("SID").trim()+"'"
	               +" AND E.MR_NO=A.MR_NO "
	               + " AND A.MR_NO=D.MR_NO "
	                 +" AND A.MR_NO = B.MR_NO AND A.CARD_NO = C.CARD_NO AND WRITE_FLG = 'Y'";
		TParm infoParm=new TParm(TJDODBTool.getInstance().select(sql));*/
		p3=insParm.getRow(0);
		p3.setData("READ_TYPE","INSCARD");
		EKT(p3);

		/*int crowdType = insParm.getInt("CROWD_TYPE"); // 医保就医类别 1.城职 2.城居
		insType = insParm.getValue("INS_TYPE"); // 医保就诊类型: 1.城职普通 2.城职门特 3.城居门特
		// ============pangben 2012-4-8 查询数据是否存在医保校验
		TParm opbReadCardParm = insParm.getParm("opbReadCardParm");
		String sql = "";
		String name = "";
		if (insType.equals("1")) {
			name = opbReadCardParm.getValue("NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		} else {
			name = opbReadCardParm.getValue("PAT_NAME");
			sql = "SELECT PAT_NAME,MR_NO FROM SYS_PATINFO WHERE IDNO='"
					+ opbReadCardParm.getValue("SID").trim()
					+ "' AND PAT_NAME='" + name.trim() + "'";
		}
		TParm insPresonParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (this.getValueBoolean("VISIT_CODE_C")
				&& this.getValue("MR_NO").toString().trim().length() <= 0) {// 初诊获得医保数据
			this.setValue("PAT_NAME", name);
			this.setValue("IDNO", opbReadCardParm.getValue("SID").trim());
			this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // 医保卡号
			// ========pangben 2013-3-5 添加初诊病人病患信息带入
			setPatName1();
			this.setValue("PY1", SYSHzpyTool.getInstance().charToCode(
					TypeTool.getString(getValue("PAT_NAME"))));// 简拼
			// 每次刷卡，要求门特联网系统根据“门特登记结束时间”与当前时间进行比较
			if (!insType.equals("1")) {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTH_DATE") ? opbReadCardParm.getValue(
						"BIRTH_DATE").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTH_DATE")
								.substring(6, 8) : "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX_CODE"));
			} else {
				this.setValue("BIRTH_DATE", null != opbReadCardParm
						.getValue("BIRTHDAY") ? opbReadCardParm.getValue(
						"BIRTHDAY").substring(0, 4)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(4, 6)
						+ "/"
						+ opbReadCardParm.getValue("BIRTHDAY").substring(6,
								opbReadCardParm.getValue("BIRTHDAY").length())
						: "");
				this.setValue("SEX_CODE", opbReadCardParm.getValue("SEX"));
			}
			return;
		}
		if (insPresonParm.getErrCode() < 0) {
			this.messageBox("获得病患信息失败");
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") <= 0) {
			this.messageBox("此医保病患不存在医疗卡信息,\n医保信息:身份证号码:"
					+ opbReadCardParm.getValue("SID") + "\n医保病患名称:" + name);
			insParm = null;
			this.onClear();
			return;
		}
		if (insPresonParm.getCount("MR_NO") == 1) {
			if (this.getValue("MR_NO").toString().length() > 0) {
				if (!insPresonParm.getValue("MR_NO", 0).equals(
						this.getValue("MR_NO"))) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
		} else if (insPresonParm.getCount("MR_NO") > 1) {
			int flg = -1;
			if (this.getValue("MR_NO").toString().length() > 0) {
				for (int i = 0; i < insPresonParm.getCount("MR_NO"); i++) {
					if (insPresonParm.getValue("MR_NO", i).equals(
							this.getValue("MR_NO"))) {
						flg = i;
						break;
					}
				}
				if (flg == -1) {
					this.messageBox("医保信息与病患信息不符,医保病患名称:" + name);
					insParm = null;
					this.onClear();
					return;
				}
			}
			// onPatName();
		}
		// ===================pangben 2012-04-09医保管控添加
		// 每次刷卡，要求门特联网系统根据“门特登记结束时间”与当前时间进行比较
		if (!insType.equals("1")) {

			// 您的门特登记有效期至X年X月X日，请在此时间前2个月内到糖尿病鉴定中心办理复查认定
			String mtEndDate = opbReadCardParm.getValue("MT_END_DATE");// 门特登记结束时间
			this.messageBox("您的门特登记有效期至" + mtEndDate
					+ "，请在此时间前2个月内到糖尿病鉴定中心办理复查认定");
		}
		// ============pangben 2012-4-9 stop
		// 判断人群类别
		// 与身份折扣对照赋值
		// 11：城职普通 ,11:医保号\ 12：城职退休,21:医保号 \13：城职离休,51:医保号
		// 21:城居新生儿 ,11:医保号\22:城居学生儿童 12:医保号 \23：城居成年居民,13:医保号
		this.setValue("REG_CTZ1", insParm.getValue("CTZ_CODE"));
		TextFormatSYSCtz combo_ctz = (TextFormatSYSCtz) this
				.getComponent("REG_CTZ1");
		// 过滤数据
		combo_ctz.setNhiFlg(crowdType + "");
		combo_ctz.onQuery();
		insFlg = true; // 医保卡读取成功
		callFunction("UI|REG_CTZ1|setEnabled", true); // 身份类别
		callFunction("UI|PAY_WAY|setEnabled", false); // 支付类别
		this.setValue("PAY_WAY", "PAY_INS_CARD"); // 支付方式修改
		this.setValue("NHI_NO", insParm.getValue("CARD_NO")); // 医保卡号
		this.grabFocus("FeeS");*/
	}

//	/**
//	 * 泰心医院医保卡保存操作
//	 * 
//	 * @param parm
//	 *            TParm
//	 * @param caseNo
//	 *            String
//	 * @return TParm
//	 */
//	private TParm TXsaveINSCard(TParm parm, String caseNo) {
//		// 没有获得医疗卡信息 判断是否执行现金收费
//		if (!tjINS && !insFlg) {
//			if (this.messageBox("提示", "没有获得医疗卡信息,执行现金收费是否继续", 2) != 0) {
//				return null;
//			}
//		}
//		if (tjINS) { // 医疗卡操作
//			if (p3.getDouble("CURRENT_BALANCE") < this.getValueDouble("FeeY")) {
//				this.messageBox("医疗卡金额不足,请充值");
//				return null;
//			}
//		}
//		TParm result = new TParm();
//		insParm.setData("REG_PARM", parm.getData()); // 医嘱信息
//		insParm.setData("DEPT_CODE", this.getValue("DEPT_CODE")); // 科室代码
//		insParm.setData("MR_NO", pat.getMrNo()); // 病患号
//
//		reg.setCaseNo(caseNo);
//		insParm.setData("RECP_TYPE", "REG"); // 类型：REG / OPB
//		insParm.setData("CASE_NO", reg.caseNo());
//		insParm.setData("REG_TYPE", "1"); // 挂号标志:1 挂号0 非挂号
//		insParm.setData("OPT_USER", Operator.getID());
//		insParm.setData("OPT_TERM", Operator.getIP());
//		insParm.setData("DR_CODE", this.getValue("DR_CODE"));// 医生代码
//		// insParm.setData("PAY_KIND", "11");// 4 支付类别:11门诊、药店21住院//支付类别12、
//		if (this.getValueString("ERD_LEVEL").length() > 0) {
//			insParm.setData("EREG_FLG", "1"); // 急诊
//		} else {
//			insParm.setData("EREG_FLG", "0"); // 普通
//		}
//
//		insParm.setData("PRINT_NO", this.getValue("NEXT_NO")); // 票号
//		insParm.setData("QUE_NO", reg.getQueNo());
//
//		TParm returnParm = insExeFee(true);
//		if (null == returnParm || null == returnParm.getValue("RETURN_TYPE")) {
//			return null;
//		}
//		int returnType = returnParm.getInt("RETURN_TYPE"); // 0.失败 1. 成功
//		if (returnType == 0 || returnType == -1) { // 取消操作
//			return null;
//		}
//
//		insParm.setData("comminuteFeeParm", returnParm.getParm(
//				"comminuteFeeParm").getData()); // 费用分割数据
//		insParm.setData("settlementDetailsParm", returnParm.getParm(
//				"settlementDetailsParm").getData()); // 费用结算
//
//		// System.out.println("insParm:::::::"+insParm);
//		result = INSTJReg.getInstance().insCommFunction(insParm.getData());
//
//		if (result.getErrCode() < 0) {
//			err(result.getErrCode() + " " + result.getErrText());
//			// this.messageBox("医保执行操作失败");
//			return result;
//		}
//		// System.out.println("医保操作出参:" + insParm);
//		// boolean messageFlg = false; // 医保金额问题 执行现金收款
//		result.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // 医保金额
//		result.setData("ACCOUNT_AMT_FORREG", returnParm
//				.getDouble("ACCOUNT_AMT_FORREG")); // 账户金额
//		insParm.setData("INS_SUMAMT", returnParm.getDouble("ACCOUNT_AMT")); // 医保金额
//		if (tjINS) { // 医疗卡操作
//		// TParm insExeParm = insExe(returnParm.getDouble("ACCOUNT_AMT"), p3,
//		// reg.caseNo(), "REG", 9);
//		// if (insExeParm.getErrCode() < 0) {
//		// return insExeParm;
//		// }
//			// 执行医疗卡扣款操作：需要判断医保金额与医疗卡金额
//			if (!onTXEktSave("Y", result)) {
//				result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
//						"deleteOldData", insParm);
//				if (result.getErrCode() < 0) {
//					err(result.getErrCode() + " " + result.getErrText());
//					result.setErr(-1, "医保卡执行操作失败");
//					// return result;
//				}
//				result.setErr(-1, "医疗卡执行操作失败");
//				return result;
//			}
//			// result = new TParm();// 执行添加数据REG_PATADM
//		}
//		return result;
//	}

	/**
	 * 医保卡执行费用显示操作 flg 是否执行退挂 false： 执行退挂 true： 正流程操作
	 * 
	 * @param flg
	 *            boolean
	 * @return TParm
	 */
	public TParm insExeFee(boolean flg) {
		TParm insFeeParm = new TParm();
		if (flg) {
			//获得当前时间
			String sysdate = StringTool.getString(SystemTool.
					getInstance().getDate(), "yyyyMMdd");
			insParm.setData("ADM_DATE", sysdate);
			insFeeParm.setData("insParm", insParm.getData()); // 医嘱信息
			insFeeParm.setData("INS_TYPE", insParm.getValue("INS_TYPE")); // 医保就医类别
		} else {
			insFeeParm.setData("CASE_NO", reSetCaseNo); // 退挂使用
			insFeeParm.setData("INS_TYPE", insType); // 退挂使用
			insFeeParm.setData("RECP_TYPE", "REG"); // 退挂使用
			insFeeParm.setData("CONFIRM_NO", confirmNo); // 退挂使用
		}
		insFeeParm.setData("NAME", pat.getName());
		insFeeParm.setData("MR_NO", pat.getMrNo()); // 病患号

		insFeeParm.setData("FeeY", this.getValueDouble("FeeY")); // 应收金额
		insFeeParm.setData("PAY_TYPE", tjINS); // 支付方式
		insFeeParm.setData("REGION_CODE", regionParm.getValue("NHI_NO", 0)); // 区域代码
		insFeeParm.setData("FEE_FLG", flg); // 判断此次操作是执行退费还是收费 ：true 收费 false 退费
		TParm returnParm = new TParm();
		if (flg) { // 正流程
			// returnParm=INSTJReg.getInstance().onInsFee(insFeeParm, this);
			returnParm = (TParm) openDialog("%ROOT%\\config\\ins\\INSFee.x",
					insFeeParm);
			if (returnParm == null
					|| null == returnParm.getValue("RETURN_TYPE")
					|| returnParm.getInt("RETURN_TYPE") == 0) {
				return null;
			}
		} else {
			// 退费流程
			TParm returnIns = reSetExeFee(insFeeParm);
			if (null == returnIns) {
				return null;
			} else {
				double accountAmt = 0.00;// 医保金额
				if (returnIns.getValue("INS_CROWD_TYPE").equals("1")) {// 城职
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - returnIns
							.getDouble("UNACCOUNT_PAY_AMT")), 2);
					this.messageBox("医保退费金额:"
							+ accountAmt
							+ " 现金退费金额:"
							+ StringTool.round(returnIns
									.getDouble("UNACCOUNT_PAY_AMT"), 2));

				} else if (returnIns.getValue("INS_CROWD_TYPE").equals("2")) {// 城居
					double payAmt = returnIns.getDouble("TOT_AMT")
							- returnIns.getDouble("TOTAL_AGENT_AMT")
							- returnIns.getDouble("FLG_AGENT_AMT")
							- returnIns.getDouble("ARMY_AI_AMT")
							- returnIns.getDouble("ILLNESS_SUBSIDY_AMT");// 现金金额
					accountAmt = StringTool.round((returnIns
							.getDouble("TOT_AMT") - payAmt), 2);

					this.messageBox("医保退费金额:" + accountAmt + " 现金退费金额:"
							+ StringTool.round(payAmt, 2));
				}

				returnParm.setData("RETURN_TYPE", 1); // 执行成功
				returnParm.setData("ACCOUNT_AMT", accountAmt);// 医保金额
			}

		}
		return returnParm;
	}

	/**
	 * 医保执行退费操作
	 * 
	 * @param parm
	 *            TParm
	 * @return double
	 */
	public TParm reSetExeFee(TParm parm) {
		TParm result = INSTJFlow.getInstance().selectResetFee(parm);
		if (result.getErrCode() < 0) {
			return null;
		}
		return result;

	}

	/**
	 * 记账操作：支付方式设置记账
	 */
	public void contractSelect() {

		if (this.getValue("CONTRACT_CODE").toString().length() > 0) {
			this.setValue("PAY_WAY", "PAY_DEBIT"); // 记账

		} else {
			this.setValue("PAY_WAY", "PAY_CASH"); // 现金
		}
	}

	/**
	 * 点击办医疗卡 zhangp 20121216
	 */
	public void ektCard() {
		TParm sendParm = new TParm();
		sendParm.setData("MR_NO", this.getValue("MR_NO"));
		TParm reParm = (TParm) this.openDialog(
				"%ROOT%\\config\\ekt\\EKTWorkUI.x", sendParm);
	}

	/**
	 * 医疗卡明细表插入数据==============zhangp 20111227
	 * 
	 * @param p
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBusinessParm(TParm p, TParm feeParm) {
		// 明细档数据
		TParm bilParm = new TParm();
		bilParm.setData("BUSINESS_SEQ", 0);
		bilParm.setData("CARD_NO", p.getValue("CARD_NO"));
		bilParm.setData("MR_NO", pat.getMrNo());
		bilParm.setData("CASE_NO", "none");
		bilParm.setData("ORDER_CODE", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("RX_NO", p.getValue("ISSUERSN_CODE"));
		bilParm.setData("SEQ_NO", 0);
		bilParm.setData("CHARGE_FLG", "3"); // 状态(1,扣款;2,退款;3,医疗卡充值,4,制卡,5,补卡)
		bilParm.setData("ORIGINAL_BALANCE", feeParm
				.getValue("ORIGINAL_BALANCE")); // 收费前余额
		bilParm.setData("BUSINESS_AMT", feeParm.getValue("BUSINESS_AMT"));
		bilParm.setData("CURRENT_BALANCE", feeParm.getValue("CURRENT_BALANCE"));
		bilParm.setData("CASHIER_CODE", Operator.getID());
		bilParm.setData("BUSINESS_DATE", TJDODBTool.getInstance().getDBTime());
		// 1：交易执行完成
		// 2：双方确认完成
		bilParm.setData("BUSINESS_STATUS", "1");
		// 1：未对帐
		// 2：对账成功
		// 3：对账失败
		bilParm.setData("ACCNT_STATUS", "1");
		bilParm.setData("ACCNT_USER", new TNull(String.class));
		bilParm.setData("ACCNT_DATE", new TNull(Timestamp.class));
		bilParm.setData("OPT_USER", Operator.getID());
		bilParm.setData("OPT_DATE", TJDODBTool.getInstance().getDBTime());
		bilParm.setData("OPT_TERM", Operator.getIP());
		// p.setData("bilParm",bilParm.getData());
		return bilParm;
	}

	/**
	 * 充值档添加数据参数==============zhangp 20111227
	 * 
	 * @param parm
	 *            TParm
	 * @param feeParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getBillParm(TParm parm, TParm feeParm) {
		TParm billParm = new TParm();
		billParm.setData("CARD_NO", parm.getValue("CARD_NO")); // 卡号
		billParm.setData("CURT_CARDSEQ", 0); // 序号
		billParm.setData("ACCNT_TYPE", "4"); // 明细帐别(1:购卡,2:换卡,3:补卡,4:充值,5:扣款,6:退费)
		billParm.setData("MR_NO", parm.getValue("MR_NO")); // 病案号
		billParm.setData("ID_NO", parm.getValue("ID_NO")); // 身份证号
		billParm.setData("NAME", parm.getValue("NAME")); // 病患名称
		billParm.setData("AMT", feeParm.getValue("BUSINESS_AMT")); // 充值金额
		billParm.setData("CREAT_USER", Operator.getID()); // 执行人员
		billParm.setData("OPT_USER", Operator.getID()); // 操作人员
		billParm.setData("OPT_TERM", Operator.getIP()); // 执行ip
		billParm.setData("GATHER_TYPE", parm.getValue("GATHER_TYPE")); // 支付方式
		// 20120112 zhangp 加字段
		billParm.setData("STORE_DATE", parm.getData("STORE_DATE"));
		billParm.setData("PROCEDURE_AMT", parm.getData("PROCEDURE_AMT"));
		return billParm;
	}

	/**
	 * 充值打印==============zhangp 20111227
	 * 
	 * @param bil_business_no
	 *            String
	 * @param copy
	 *            String
	 */
	private void onPrint(String bil_business_no, String copy) {
		if (!printBil) {
			this.messageBox("进行医疗卡充值操作才可以打印");
			return;
		}
		TParm parm = new TParm();
		parm.setData("TITLE", "TEXT", (Operator.getRegion() != null
				&& Operator.getRegion().length() > 0 ? Operator
				.getHospitalCHNFullName() : "所有医院"));
		parm.setData("MR_NO", "TEXT", parmSum.getValue("MR_NO")); // 病案号
		parm.setData("PAT_NAME", "TEXT", parmSum.getValue("NAME")); // 姓名
		parm.setData("GATHER_TYPE", "TEXT", parmSum
				.getValue("GATHER_TYPE_NAME")); // 收款方式
		parm.setData("AMT", "TEXT", StringTool.round(parmSum
				.getDouble("BUSINESS_AMT"), 2)); // 金额
		// ====zhangp 20120525 start
		// parm.setData("GATHER_NAME", "TEXT", "收 款"); //收款方式
		parm.setData("GATHER_NAME", "TEXT", ""); // 收款方式
		// ====zhangp 20120525 end
		parm.setData("TYPE", "TEXT", "预 收"); // 文本预收金额
		parm.setData("SEX_TYPE", "TEXT", pat.getSexCode().equals("1") ? "男"
				: "女"); // 性别
		parm.setData("AMT_AW", "TEXT", StringUtil.getInstance().numberToWord(
				parmSum.getDouble("BUSINESS_AMT"))); // 大写金额
		parm.setData("TOP1", "TEXT", "EKTRT001 FROM " + Operator.getID()); // 台头一
		String yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyyMMdd"); // 年月日
		String hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "hhmmss"); // 时分秒
		parm.setData("TOP2", "TEXT", "Send On " + yMd + " At " + hms); // 台头二
		yMd = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "yyyy/MM/dd"); // 年月日
		hms = StringTool.getString(TypeTool.getTimestamp(TJDODBTool
				.getInstance().getDBTime()), "HH:mm"); // 时分秒
		parm.setData("DESCRIPTION", "TEXT", parmSum.getValue("DESCRIPTION")); // 备注
		parm.setData("BILL_NO", "TEXT", parmSum.getValue("BIL_CODE")); // 票据号
		parm.setData("PRINT_NO", "TEXT", parmSum.getValue("PRINT_NO")); //票据号
		if (null == bil_business_no)
			bil_business_no = EKTTool.getInstance().getBillBusinessNo(); // 补印操作
		parm.setData("ONFEE_NO", "TEXT", bil_business_no); // 收据号
		parm.setData("PRINT_DATE", "TEXT", yMd); // 打印时间
		parm.setData("DATE", "TEXT", yMd + "    " + hms); // 日期
		parm.setData("USER_NAME", "TEXT", Operator.getID()); // 收款人
		parm.setData("COPY", "TEXT", copy); // 收款人
		// ===zhangp 20120525 start
		parm.setData("O", "TEXT", "");
		// this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_ONFEE.jhw",
		// parm,true);
		this.openPrintWindow("%ROOT%\\config\\prt\\EKT\\EKT_FEE.jhw", parm,
				true);
		// ===zhangp 20120525 end
	}

	/**
	 * 充值文本框回车事件======zhangp 20111227
	 */
	public void addFee() {
		if (this.getValueDouble("TOP_UPFEE") < 0) {
			this.messageBox("充值金额不可以为负值");
			return;
		}
		this.setValue("SUM_EKTFEE", this.getValueDouble("TOP_UPFEE")
				+ this.getValueDouble("CURRENT_BALANCE"));
	}

	/**
	 * 清空医疗卡页签============zhangp 20111227
	 */
	public void clearEKTValue() {
		ektOnClear();
		// clearValue("DESCRIPTION;TOP_UPFEE;SUM_EKTFEE");
	}

	/**
	 * 删除医保在途状态
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean deleteInsRun(String caseNo, String exeType) {
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
		TParm result = INSRunTool.getInstance().deleteInsRun(parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}

	/**
	 * 修改医保票据号
	 * 
	 * @param caseNo
	 *            String
	 * @param exeType
	 *            String
	 * @return boolean
	 */
	public boolean updateINSPrintNo(String caseNo, String exeType) {
		TParm parm = new TParm();
		if (null == caseNo && caseNo.length() <= 0) {
			return false;
		}
		parm.setData("CASE_NO", caseNo);
		parm.setData("EXE_USER", Operator.getID());
		parm.setData("EXE_TERM", Operator.getIP());
		parm.setData("EXE_TYPE", exeType);
		parm.setData("CONFIRM_NO", insParm.getValue("CONFIRM_NO"));
		parm.setData("PRINT_NO", insParm.getValue("PRINT_NO"));
		parm.setData("RECP_TYPE", insParm.getValue("RECP_TYPE"));
		TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
				"updateINSPrintNo", parm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			result.setErr(-1, "医保卡执行操作失败");
			return false;
		}
		return true;
	}
	/**
	 * 更新医保表中的RECEIPT_NO
	 * 
	 */
	public boolean updateReceiptNo(String caseNo,String recpType) {
		 String sql = " SELECT RECEIPT_NO FROM BIL_REG_RECP " +
		 		      " WHERE CASE_NO = '"+caseNo+"'";
	     TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	     String receiptNo = parm.getValue("RECEIPT_NO", 0);
	     String confirmNo = insParm.getValue("CONFIRM_NO");
	     String sql1 = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
					   " WHERE CASE_NO ='" + caseNo + "'" +
					   " AND CONFIRM_NO = '" + confirmNo + "'" +
					   " AND RECP_TYPE = '" + recpType + "'";
		TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql1));
		if (updateParm.getErrCode() < 0) {
			err(updateParm.getErrCode() + " " + updateParm.getErrText());
			updateParm.setErr(-1, "更新医保表失败");
			return false;
		}
		return true;	
	}
	/**
	 * 医保支付赋值
	 * 
	 * @param result
	 *            医保返回的参数
	 * @param regFeeParm
	 *            医保分割后医嘱的金额
	 * @return 返回医保支付总金额
	 */
	public double tjInsPay(TParm result, TParm regFeeParm) {
		reg.getRegReceipt().setPayBankCard(0.00);
		reg.getRegReceipt().setPayCheck(0.00);
		reg.getRegReceipt().setPayDebit(0.00);
		reg.getRegReceipt().setPayInsCard(result.getDouble("INS_SUMAMT")); // 医保金额
		double ins_amt = result.getDouble("INS_SUMAMT");
		if (!tjINS) { // 现金收费
			reg.getRegReceipt().setPayCash(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT"));
			reg.getRegReceipt().setPayMedicalCard(0.00); // 医疗卡金额
		} else { // 医疗卡收费
			reg.getRegReceipt().setPayCash(0.00);
			reg.getRegReceipt().setPayMedicalCard(
					TypeTool.getDouble(getValue("FeeY"))
							- result.getDouble("INS_SUMAMT")); // 医疗卡金额
		}
		TParm comminuteFeeParm = result.getParm("comminuteFeeParm"); // 费用分割
		for (int i = 0; i < regFeeParm.getCount(); i++) {
			for (int j = 0; j < comminuteFeeParm.getCount("ORDER_CODE"); j++) {
				if (regFeeParm.getValue("ORDER_CODE", i).equals(
						comminuteFeeParm.getValue("ORDER_CODE", j))) {
					if (comminuteFeeParm.getValue("RECEIPT_TYPE", j).equals(
							"REG_FEE")) {
						reg.getRegReceipt().setRegFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 12折扣前挂号费(REG_RECEIPT)
						reg.getRegReceipt().setRegFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					} else {
						reg.getRegReceipt().setClinicFee(
								comminuteFeeParm.getDouble("OWN_AMT", j));
						// 14折扣前诊查费(REG_RECEIPT)
						reg.getRegReceipt().setClinicFeeReal(
								comminuteFeeParm.getDouble("OWN_AMT", j));
					}
					break;
				}
			}
		}
		return ins_amt;
	}

	/**
	 * 退挂操作使用
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param flg
	 *            boolean
	 * @param ektName
	 *            String
	 * @param cashName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetReg(TParm unRegParm, String caseNo, boolean flg,
			String ektName, String cashName, String stutsFlg) {
		// TParm reSetInsParm=new TParm();
		if (!reSetInsSave(unRegParm.getValue("INV_NO")))
			return;
		if ("PAY_MEDICAL_CARD".equals(this.getValueString("PAY_WAY"))) {
			// 添加建行卡退挂分支====pangben 2012-12-07
			TParm ccbParm = checkCcbReSet(caseNo);// 判断是否执行建行卡操作
			if (null == ccbParm || ccbParm.getCount() <= 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				// 建行操作
				// TParm ccbp=checkCcbReSet(caseNo);
				unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));// 建行金额
				reSetCcbSave(unRegParm, caseNo, stutsFlg);
			}
		} else if ("PAY_CASH".equals(this.getValueString("PAY_WAY"))) { // 现金
			reSetCashSave(unRegParm, stutsFlg, flg, cashName);
		} else if ("PAY_INS_CARD".equals(this.getValueString("PAY_WAY"))) { // 医保卡
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {
				reSetEktSave(unRegParm, caseNo, ektName, stutsFlg);
			} else {
				TParm ccbParm = checkCcbReSet(caseNo);// 判断是否执行建行卡操作
				if (null == ccbParm || ccbParm.getCount() <= 0)
					reSetCashSave(unRegParm, stutsFlg, flg, cashName);
				else {
					// 建行操作
					unRegParm.setData("AMT", ccbParm.getDouble("AMT", 0));
					reSetCcbSave(unRegParm, caseNo, stutsFlg);
				}
			}
		}
		// 医保删除在途状态
		if (null != confirmNo && confirmNo.length() > 0) {
			if (!deleteInsRun(reSetCaseNo, "REGT"))
				return;
			//更新医保表RECEIPT_NO(退挂那一笔)
			if (!updateUnRegReceiptNo(reSetCaseNo, "REGT"))
				return;
		}


	}
	/**
	 * 更新医保表中的RECEIPT_NO(退挂那一笔)
	 * 
	 */
	public boolean updateUnRegReceiptNo(String caseNo,String recpType) {
		 String sql = " SELECT RECEIPT_NO FROM BIL_REG_RECP " +
		 		      " WHERE CASE_NO = '"+caseNo+"'" +
		 		      " AND AR_AMT < 0";
	     TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	     String receiptNo = parm.getValue("RECEIPT_NO", 0);
	     String sql1 = " UPDATE INS_OPD SET RECEIPT_NO ='"+ receiptNo+ "'" +
					   " WHERE CASE_NO ='" + caseNo + "'" +
					   " AND RECP_TYPE = '" + recpType + "'";
		TParm updateParm = new TParm(TJDODBTool.getInstance().update(sql1));
		if (updateParm.getErrCode() < 0) {
			err(updateParm.getErrCode() + " " + updateParm.getErrText());
			updateParm.setErr(-1, "更新医保表失败");
			return false;
		}
		return true;	
	}
	/**
	 * 医保退挂操作
	 * 
	 * @param invNo
	 *            String
	 * @return boolean
	 */
	private boolean reSetInsSave(String invNo) {
		TParm reSetInsParm = new TParm();
		if (null != confirmNo && confirmNo.length() > 0) {
			// 医保卡退费 需要修改医疗卡参数
			if (null == reSetCaseNo && reSetCaseNo.length() <= 0) {
				return false;
			}
			TParm tredeParm = new TParm(); // 查询此次退挂操作是否是医疗卡退挂
			tredeParm.setData("CASE_NO", reSetCaseNo);
			tredeParm.setData("BUSINESS_TYPE", "REG"); // 类型
			tredeParm.setData("STATE", "1"); // 状态： 0 扣款 1 扣款打票 2退挂 3 作废
			TParm reSetEktParm = EKTTool.getInstance().selectTradeNo(tredeParm); // 医疗卡退费查询
			if (reSetEktParm.getErrCode() < 0) {
				return false;
			}
			if (null != reSetEktParm && reSetEktParm.getCount() > 0) {// 医疗卡退挂操作
				if (p3 == null || null == p3.getValue("MR_NO")
						|| p3.getValue("MR_NO").length() <= 0) {
					this.messageBox("医疗卡退费,请执行读卡操作");
					return false;
				}
			}
			TParm parm = insExeFee(false);
			int returnType = parm.getInt("RETURN_TYPE");
			if (returnType == 0 || returnType == -1) { // 取消
				return false;
			}
			reSetInsParm.setData("CASE_NO", reSetCaseNo); // 就诊号
			reSetInsParm.setData("CONFIRM_NO", confirmNo); // 医保就诊号
			reSetInsParm.setData("INS_TYPE", insType); // 医保就诊号
			reSetInsParm.setData("RECP_TYPE", "REG"); // 收费类型
			reSetInsParm.setData("UNRECP_TYPE", "REGT"); // 退费类型
			reSetInsParm.setData("OPT_USER", Operator.getID()); // id
			reSetInsParm.setData("OPT_TERM", Operator.getIP()); // ip
			reSetInsParm.setData("REGION_CODE", regionParm
					.getValue("NHI_NO", 0)); // 医保区域代码
			reSetInsParm.setData("PAT_TYPE", this.getValue("REG_CTZ1")); // 身份
			reSetInsParm.setData("INV_NO", invNo); // 票据号
			// System.out.println("reSetInsParm::::::" + reSetInsParm);
			TParm result = INSTJReg.getInstance().insResetCommFunction(
					reSetInsParm.getData());
			if (result.getErrCode() < 0) {
				this.messageBox("医保退挂失败");
				return false;
			}
		}
		return true;
	}

	/**
	 * 校验是否建行卡退挂操作
	 * 
	 * @return
	 */
	private TParm checkCcbReSet(String reSetCaseNo) {
		String sql = "SELECT CASE_NO,SUM(AMT) AS AMT FROM EKT_CCB_TRADE WHERE CASE_NO='"
				+ reSetCaseNo + "' AND BUSINESS_TYPE='REG' group by case_no";
		TParm reSetParm = new TParm(TJDODBTool.getInstance().select(sql));
		return reSetParm;
	}

	/**
	 * 建行卡退费操作 =====pangben 2012-12-07
	 */
	private void reSetCcbSave(TParm unRegParm, String caseNo, String stutsFlg) {
		// 调用建行接口退费流程
		unRegParm.setData("NHI_NO", regionParm.getValue("NHI_NO", 0));
		unRegParm.setData("RECEIPT_NO", unRegParm.getParm("RECP_PARM")
				.getValue("RECEIPT_NO", 0));
		// 建行接口操作
		// TParm resultData=REGCcbReTool.getInstance().getCcbRe(opbParm);
		TParm result = TIOM_AppServer.executeAction(
				"action.ccb.CCBServerAction", "getCcbRe", unRegParm);
		if (result.getErrCode() < 0) {
			this.messageBox("建行接口调用出现问题,请联系信息中心");
			return;
		}
		unRegParm.setData("FLG", "N");
		result.setData("OPT_TERM", Operator.getIP());
		result.setData("OPT_USER", Operator.getID());
		result.setData("BUSINESS_TYPE", "REGT");
		result = REGCcbReTool.getInstance().saveEktCcbTrede(result);
		if (result.getErrCode() < 0) {
			this.messageBox("建行退挂失败");
			return;
		}
		result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onUnReg", unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// 调用排队叫号
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("叫号失败");
		}
		if (stutsFlg.equals("Y")) {
			this.messageBox("建行卡退挂成功!票据号:" + unRegParm.getValue("INV_NO"));
		}
	}

	/**
	 * 医疗卡退费操作
	 * 
	 * @param unRegParm
	 *            TParm
	 * @param caseNo
	 *            String
	 * @param ektName
	 *            String
	 * @param stutsFlg
	 *            String
	 */
	private void reSetEktSave(TParm unRegParm, String caseNo, String ektName,
			String stutsFlg) {
		// 医疗卡
		TParm result = new TParm();
		if (EKTIO.getInstance().ektSwitch()) {
			
			//modify by huangtt 20160914  start 退挂修改
			
			TParm orderParm = onOpenCardR(caseNo);
			reg = new Reg();
			reg.setCaseNo(caseNo);
			EktParam ektParam = new EktParam();
			ektParam.setType("REG");
			ektParam.setRegPatAdmControl(this);
			ektParam.setReg(reg);
			ektParam.setPat(pat);
			ektParam.setOrderParm(orderParm);
			
			EktTradeContext ektTradeContext = new EktTradeContext(ektParam);
			try {
				
				//创建参数，打开收费界面，执行收费
				ektTradeContext.openClientR(ektParam);
				
				
			} catch (Exception e) {
				e.printStackTrace();
				return ;
			}
			
			
			
//			int type = 0;
//			if (result == null) {
//				this.messageBox("E0115");
//				return;
//			}
//			type = result.getInt("OP_TYPE");
//			if (type == 3 || type == -1) {
//				this.messageBox("E0115");
//				return;
//			}
//			if (type == 2) {
//				return;
//			}
//			tradeNoT = result.getValue("TRADE_NO");
			
			unRegParm.setData("EKT_SQL",reg.getEktSql());
			
			//modify by huangtt 20160914  end 退挂修改
			
			
			
			
			unRegParm.setData("TRADE_NO", tradeNoT);
			// 医疗卡退挂
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					ektName, unRegParm);
			if (result.getErrCode() < 0) {
				err(result.getErrName() + " " + result.getErrText());
				EKTIO.getInstance().unConsume(tradeNoT, this);
				return;
			}
			if (stutsFlg.equals("Y")) {
				this.messageBox("退挂成功!票据号:" + unRegParm.getValue("INV_NO"));
			}
			// 调用排队叫号
			if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
				this.messageBox("叫号失败");
			}

		}
	}

	/**
	 * 现金退费操作
	 * 
	 * @param unRegParm
	 *            退挂数据
	 * @param flg
	 *            现金退挂管控
	 * @param cashName
	 *            现金调用ACTION类接口方法名称
	 * @param stutsFlg
	 *            判断是否执行提示消息框
	 */
	private void reSetCashSave(TParm unRegParm, String stutsFlg, boolean flg,
			String cashName) {
		TParm result = new TParm();
		if (stutsFlg.equals("Y")) {
			unRegParm.setData("FLG", flg);
		}
		// 现金退挂动作
		result = TIOM_AppServer.executeAction("action.reg.REGAction", cashName,
				unRegParm);
		if (result.getErrCode() < 0) {
			err(result.getErrName() + " " + result.getErrText());
			return;
		}
		// 调用排队叫号
		if (!"true".equals(callNo("UNREG", reSetCaseNo))) {
			this.messageBox("叫号失败");
		}
		if (stutsFlg.equals("Y")) {
			this.messageBox("退挂成功!票据号:" + unRegParm.getValue("INV_NO"));
		}
	}

	/**
	 * VIP 日期与挂号日期相同
	 */
	public void onDateReg() {
		this.setValue("VIP_ADM_DATE", this.getValue("ADM_DATE"));
		onQueryVipDrTable();
	}

	public void onPast() {
		if (this.getValueString("BIRTH_DATE").length() > 0
				&& this.getValueString("BIRTH_DATE") != null)
			this.grabFocus("SEX_CODE");
	}

	/**
	 * 保存QUE_NO 就诊号 解决重号问题 将原来程序的一个事物拆分出来先保存就诊号逻辑 ===============pangben
	 * 2012-6-18
	 */
	private boolean onSaveQueNo(TParm regParm) {
		// 处理号表
		TParm result = null;
		if (regParm.getBoolean("VIP_FLG")) {
			result = TIOM_AppServer.executeAction("action.reg.REGAction",
					"onSaveQueNo", regParm);
		} else {
			// 普通诊
			result = SchDayTool.getInstance().updatequeno(regParm);
		}
		if (result.getErrCode() < 0) {
			return false;
		}
		return true;
	}

	/**
	 * 查就诊号有无占号 ====zhangp 20120629
	 * 
	 * @param temp
	 */
	private void queryQueNo(TParm temp) {
		String vipSql = "SELECT MIN(QUE_NO) QUE_NO FROM REG_CLINICQUE "
				+ "WHERE ADM_TYPE='" + admType + "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE") + "'" + " AND SESSION_CODE='"
				+ reg.getSessionCode() + "' AND CLINICROOM_NO='"
				+ temp.getValue("CLINICROOM_NO") + "' AND  QUE_STATUS='N'";
		TParm result = new TParm(TJDODBTool.getInstance().select(vipSql));
		if (result.getErrCode() < 0) {
			messageBox("查号失败");
			return;
		}
		if (result.getCount() <= 0) {
			messageBox("无就诊号");
			return;
		}
		int queNo = result.getInt("QUE_NO", 0);
		//add by huangtt 20160621 start
		if(queNo == 0){
			messageBox("无就诊号");
			return;
		}
		//add by huangtt 20160621 end
		reg.setQueNo(queNo);
	}
	
    /**
     * 腕带打印
     */
    public void onWrist() {//wanglong add 20150413
        if (this.getValueString("MR_NO").length() == 0 && pat == null && reg.getPat() == null) {
            return;
        }
        String mrNo = "";
        String patName = "";
        String sex = "";
        String birthDay = "";
        if (pat != null) {
            mrNo = pat.getMrNo();
            patName = pat.getName();
            sex = pat.getSexString();
            birthDay = StringTool.getString(pat.getBirthday(), "yyyy/MM/dd");
        } else if (reg.getPat() != null) {
            mrNo = reg.getPat().getMrNo();
            patName = reg.getPat().getName();
            sex = reg.getPat().getSexString();
            birthDay = StringTool.getString(reg.getPat().getBirthday(), "yyyy/MM/dd");
        }
        TParm print = new TParm();
        print.setData("Barcode", "TEXT", mrNo);
        print.setData("PatName", "TEXT", patName);
        print.setData("Sex", "TEXT", sex);
        print.setData("BirthDay", "TEXT", birthDay);
        this.openPrintDialog("%ROOT%\\config\\prt\\ERD\\ERDWrist", print);
    }
    
    /**
     * 设置初复诊  add by huangtt 20151020
     * @param mrNo
     */
	public void setVisitCodeFC(String mrNo) {
		String sql1 = "SELECT COUNT(MR_NO) SUM FROM SYS_EMR_INDEX WHERE MR_NO = '"
				+ mrNo + "'";
		TParm selParm = new TParm(TJDODBTool.getInstance().select(sql1));
		if (selParm.getInt("SUM", 0) > 0) {
			this.setValue("VISIT_CODE_F", true);
		} else {
			this.setValue("VISIT_CODE_C", true);
		}
	}
	
	public void onErd(){
		String triageNo = this.getValueString("TRIAGE_NO");
		String sql = "SELECT LEVEL_CODE FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' AND MR_NO IS NULL	";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			this.messageBox("该检伤号已用，请重新输入");
			this.setValue("TRIAGE_NO", "");
			return;
		}
		this.setValue("ERD_LEVEL", parm.getValue("LEVEL_CODE", 0));
	}
	
	/**
	 * 初始化票据
	 * 
	 * @param bilInvoice
	 *            BilInvoice
	 * @return boolean
	 */
	private boolean initBilInvoice(BilInvoice bilInvoice) {
		// 检核开关帐
		if (bilInvoice == null) {
			this.messageBox_("你尚未对医疗卡开账!");
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().length() == 0
				|| bilInvoice.getUpdateNo() == null) {
			this.messageBox_("无可打印的医疗卡票据!");
			// this.onClear();
			return false;
		}
		// 检核当前票号
		if (bilInvoice.getUpdateNo().equals(bilInvoice.getEndInvno())) {
			this.messageBox_("最后一张医疗卡票据!");
		}
		String endNo_num = bilInvoice.getEndInvno().replaceAll("[^0-9]", "");
		String endNo_word = bilInvoice.getEndInvno().replaceAll("[0-9]", "");
		String nowNo_num = bilInvoice.getUpdateNo().replaceAll("[^0-9]", "");
		String nowNo_word = bilInvoice.getUpdateNo().replaceAll("[0-9]", "");
		if(nowNo_word.equals(endNo_word)&&Long.valueOf(nowNo_num)- Long.valueOf(endNo_num)==1){
			this.messageBox("票据已使用完，请重新领票");
			this.setValue("BIL_CODE","");
			return false;
		}
		
		if(!compareInvno(bilInvoice.getStartInvno(),bilInvoice.getEndInvno(),bilInvoice.getUpdateNo())){
			this.messageBox("票号超出范围");
			onClear();
			return false;
		}
		callFunction("UI|BIL_CODE|setValue", bilInvoice.getUpdateNo());
		return true;
	}
	/**
	 * 比较票号
	 * @return
	 */
	private boolean compareInvno(String StartInvno, String EndInvno,String UpdateNo) {
		String startNo_num = StartInvno.replaceAll("[^0-9]", "");// 去非数字
		String startNo_word = StartInvno.replaceAll("[0-9]", "");// 去数字
		String endNo_num = EndInvno.replaceAll("[^0-9]", "");
		String endNo_word = EndInvno.replaceAll("[0-9]", "");
		String nowNo_num = UpdateNo.replaceAll("[^0-9]", "");
		String nowNo_word = UpdateNo.replaceAll("[0-9]", "");
		if (startNo_word.equals(endNo_word)&&startNo_word.equals(nowNo_word)){
			if(Long.valueOf(endNo_num)- Long.valueOf(nowNo_num)>=0&&Long.valueOf(nowNo_num)- Long.valueOf(startNo_num)>=0){
			return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}
}
