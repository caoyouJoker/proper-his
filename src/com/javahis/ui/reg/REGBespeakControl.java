package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import jdo.bil.BIL;
import jdo.ekt.EKTIO;
import jdo.odo.OPDAbnormalRegTool;
import jdo.reg.ClinicRoomTool;
import jdo.reg.REGClinicQueTool;
import jdo.reg.REGSysParmTool;
import jdo.reg.SchDayTool;
import jdo.reg.SessionTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SYSRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.textFormat.TextFormatSYSOperatorForReg;

/**
 * <p>
 * Title:医疗卡特批款预约挂号
 * </p>
 * 
 * <p>
 * Description:可以执行预约操作
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangben
 * @version 4.0.1
 */
public class REGBespeakControl extends TControl {
	/**
	 * 初始化
	 */
	private String admType = "O";// 门急别
	private Pat pat;
	private String CASE_NO;
	private int queNo=-1;//就诊顺序号
	private String regAdmTime="";//预约时间
	private boolean VIP=false;
	public void onInit() {
		// 得到前台传来的数据并显示在界面上
		TParm parm = (TParm) getParameter();
		if (null == parm) {
			return;
		}
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		admType = parm.getValue("ADM_TYPE");

		pageInit();
	}

	/**
	 * 页面初始化
	 */
	public void pageInit() {
		initCombo();
		initClinicRoom();
		initSchDay();
	}

	/**
	 * 初始化combo
	 */

	public void initCombo() {
		// 初始化日期
		this.setValue("ADM_DATE", SystemTool.getInstance().getDate());
		//modigy by huangtt 20141127
		if(admType.equals("E")){
			String sql = "SELECT START_REG_TIME FROM REG_SESSION WHERE ADM_TYPE='E' ORDER BY START_REG_TIME";
			TParm regTime = new TParm(TJDODBTool.getInstance().select(sql));
			String time2 = regTime.getValue("START_REG_TIME", 0);
			String time1 = SystemTool.getInstance().getDate().toString().substring(11, 19);
			this.setValue("ADM_DATE", StringTool.rollDate(SystemTool.getInstance().getDate(), +timeComp(time1,time2)));

		}
		// 初始化时段
		String sessionCode = SessionTool.getInstance().getDefSessionNow(
				admType, Operator.getRegion());
		this.setValue("SESSION_CODE", sessionCode);
		// 初始化科别，人员
		this.setValue("SERVICE_LEVEL", "1");
		// 急诊
		if (admType.equals("E")) {
			callFunction("UI|ERD_LEVEL_TITLE|setVisible", true);
			callFunction("UI|ERD_LEVEL|setVisible", true);
		}
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("REGMETHOD_CODE", "A");
		
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	/**
	 * 初始化诊间combo
	 */
	public void initClinicRoom() {
		TParm pa = new TParm();
		pa.setData("ADM_TYPE", admType);
		pa.setData("ADM_DATE", StringTool.getString(SystemTool.getInstance().getDate(), "yyyyMMdd"));
		pa.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		pa.setData("REGION_CODE", Operator.getRegion());
		TParm parm = ClinicRoomTool.getInstance().getNotUseForODO(pa);
		TTextFormat tf = (TTextFormat) this.getComponent("CLINICROOM_NO");
		tf.setPopupMenuData(parm);
		tf.setComboSelectRow();
		tf.popupMenuShowData();
	}

	/**
	 * 初始化病患信息
	 */
	public void onMrNo() {
		pat = Pat.onQueryByMrNo(this.getValueString("MR_NO"));
		if (pat == null) {
			this.messageBox_("无此病患");
			return;
		}
		this.setValue("MR_NO", pat.getMrNo());
		this.setValue("SEX_CODE", pat.getSexCode());
		this.setValue("PAT_NAME", pat.getName());
		this.setValue("CTZ1_CODE", pat.getCtz1Code());
		this.setValue("CTZ2_CODE", pat.getCtz2Code());
		this.setValue("CTZ3_CODE", pat.getCtz3Code());
		this.setValue("BIRTHDAY", pat.getBirthday());
	}

	// /**
	// * 诊别选择事件
	// */
	// public void onClickClinicType(){
	// double reg_fee =
	// BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
	// "REG_FEE",getValueString("CTZ1_CODE"),
	// getValueString("CTZ2_CODE"),
	// getValueString("CTZ3_CODE"),
	// this.getValueString("SERVICE_LEVEL"));
	// //挂号费
	// this.setValue("REG_FEE", reg_fee);
	// double clinic_fee =
	// BIL.getRegDetialFee(admType,getValueString("CLINICTYPE_CODE"),
	// "CLINIC_FEE",getValueString("CTZ1_CODE"),
	// getValueString("CTZ2_CODE"),
	// getValueString("CTZ3_CODE"),
	// this.getValueString("SERVICE_LEVEL"));
	// //诊查费
	// this.setValue("CLINIC_FEE", clinic_fee);
	// //总费用
	// setValue("AR_AMT", reg_fee + clinic_fee);
	// }
	/**
	 * 医疗卡读卡
	 */
	public void onEKT() {
		TParm patParm = EKTIO.getInstance().TXreadEKT();
		if (patParm.getErrCode() < 0) {
			this.messageBox(patParm.getErrName() + " " + patParm.getErrText());
			return;
		}
		if (patParm.getValue("MR_NO") == null
				|| patParm.getValue("MR_NO").length() == 0) {
			this.messageBox_("读卡失败");
		} else {
			this.setValue("MR_NO", patParm.getValue("MR_NO"));
			this.onMrNo();
		}
	}

	/***
	 * 保存
	 */
	public void onSave() {
		// 检核数据
		if (!checkData()) {
			return;
		}
		if (getValueString("REGMETHOD_CODE").equals("D")) {
			messageBox("请挂VIP诊");
			return;
		} 
		//校验是否预约挂号
		String sql="SELECT A.VIP_FLG,A.CLINICTYPE_CODE,B.CLINICAREA_CODE,A.QUE_NO FROM REG_SCHDAY A,REG_CLINICROOM B" +
				" WHERE A.CLINICROOM_NO = B.CLINICROOM_NO AND A.REGION_CODE='"+this.getValue("REGION_CODE")+"'"+
		        " AND A.ADM_TYPE='"+admType+"' AND A.ADM_DATE='"+StringTool.getString((Timestamp) this.getValue("ADM_DATE"), "yyyyMMdd")+
		        "'AND A.SESSION_CODE='"+this.getValueString("SESSION_CODE")+"'"+
				" AND A.CLINICROOM_NO='"+this.getValueString("CLINICROOM_NO")+"'";
		TParm temp = new TParm(TJDODBTool.getInstance().select(sql));
		if (temp.getErrCode()<0) {
			this.messageBox("E0005");
			return;
		}
		if (temp.getCount()<=0) {
			this.messageBox("医生排班不存在,请查看排班表");
			return;
		}
		queNo=temp.getInt("QUE_NO",0);
		if (null!=temp.getValue("VIP_FLG",0) && temp.getValue("VIP_FLG",0).equals("Y")) {
			if (!onBespeak()) {
				return;
			}
		}
		
		CASE_NO = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO",
		"CASE_NO");
		// 保存挂号信息
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction",
				"onNewReg", this.getSaveData(temp.getValue("CLINICTYPE_CODE",0),temp.getValue("CLINICAREA_CODE",0)));
		if (result.getErrCode() != 0) {
		//	EKTIO.getInstance().unConsume(tredeNo, this);
			this.messageBox("E0005");
			return;
		}
//		TParm parm = new TParm();
//		parm.setData("CASE_NO", CASE_NO);
//		//TParm re = OPDAbnormalRegTool.getInstance().selectRegForOPD(parm);
//		this.setReturnValue(re);
		this.messageBox("P0005");
		this.closeWindow();
	}

	/**
	 * 清空
	 */
	public void onClear() {
		this
				.clearValue("MR_NO;CLINICROOM_NO;PAT_NAME;SEX_CODE;CTZ1_CODE;CTZ2_CODE;CTZ3_CODE;SERVICE_LEVEL;REG_FEE;CLINIC_FEE;AR_AMT");
		pat = null;
		CASE_NO = "";
		regAdmTime = "";
		queNo=-1;
		initCombo();
	}

	/**
	 * 检核保存数据是否符合条件
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		if (pat == null) {
			return false;
		}
		if (this.getValueString("ADM_DATE").length() == 0) {
			this.messageBox_("请选择日期");
			this.grabFocus("DATE");
			return false;
		}
		if (this.getValueString("SESSION_CODE").length() == 0) {
			this.messageBox_("请选择时段");
			this.grabFocus("SESSION_CODE");
			return false;
		}
		if (this.getValueString("DEPT_CODE").length() == 0) {
			this.messageBox_("请选择科别");
			this.grabFocus("DEPT_CODE");
			return false;
		}
		if (this.getValueString("DR_CODE").length() == 0) {
			this.messageBox_("请选择医生");
			this.grabFocus("DR_CODE");
			return false;
		}
		if (this.getValueString("CLINICROOM_NO").length() == 0) {
			this.messageBox_("请选择诊室");
			this.grabFocus("CLINICROOM_NO");
			return false;
		}
		if (this.getValueString("CTZ1_CODE").length() == 0) {
			this.messageBox_("病患主身份不可为空");
			this.grabFocus("CTZ1_CODE");
			return false;
		}
		if (this.getValueString("SERVICE_LEVEL").length() == 0) {
			this.messageBox_("服务等级不可为空");
			this.grabFocus("SERVICE_LEVEL");
			return false;
		}
		if (!this.emptyTextCheck("REGMETHOD_CODE")) {
			return false;
		}
		// 急诊检伤
		if (admType.equals("E")
				&& this.getValueString("ERD_LEVEL").length() == 0) {
			this.messageBox_("检伤等级不可为空");
			this.grabFocus("ERD_LEVEL");
			return false;
		}
		return true;
	}

	/**
	 * 获取要保存的数据
	 * 
	 * @return TParm
	 */
	private TParm getSaveData(String clinictypeCode,String clinicareaCode) {
		TParm parm = new TParm();
		// 生成CASE_NO
		parm.setData("CASE_NO", CASE_NO);
		parm.setData("ADM_TYPE", admType);
		parm.setData("MR_NO", pat.getMrNo());
		parm.setData("REGION_CODE", Operator.getRegion());
		parm.setData("ADM_DATE", StringTool.getString((Timestamp) this
				.getValue("ADM_DATE"), "yyyyMMdd"));
		parm.setData("REG_DATE", SystemTool.getInstance().getDate());
		parm.setData("SESSION_CODE", this.getValue("SESSION_CODE"));
		parm.setData("CLINICTYPE_CODE",clinictypeCode);// 号别
		parm.setData("CLINICROOM_NO", this.getValue("CLINICROOM_NO"));// 诊室
		parm.setData("CLINICAREA_CODE", clinicareaCode);// 诊区
		parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("DR_CODE", this.getValue("DR_CODE"));
		parm.setData("REALDEPT_CODE", this.getValue("DEPT_CODE"));
		parm.setData("REALDR_CODE", this.getValue("DR_CODE"));
		parm.setData("APPT_CODE", "Y");// 预约
		parm.setData("QUE_NO", queNo);// 
		parm.setData("REG_ADM_TIME", regAdmTime);// 预约时间
		parm.setData("ERD_LEVEL", this.getValue("ERD_LEVEL"));//检伤
		
		parm.setData("VISIT_CODE", "1");// 复诊
		parm.setData("REGMETHOD_CODE", this.getValue("REGMETHOD_CODE"));// 挂号方式
		parm.setData("CTZ1_CODE", this.getValue("CTZ1_CODE"));
		parm.setData("CTZ2_CODE", this.getValue("CTZ2_CODE"));
		parm.setData("CTZ3_CODE", this.getValue("CTZ3_CODE"));
		parm.setData("ARRIVE_FLG", "N");// 报到注记
		parm.setData("ADM_REGION", Operator.getRegion());
		parm.setData("ADM_STATUS", "1");// 就诊进度 1：已挂号
		parm.setData("REPORT_STATUS", "1");// 报告状态 1全部未完成
		parm.setData("SEE_DR_FLG", "N");// 看诊注记
		parm.setData("HEAT_FLG", "N");// 发热注记
		parm.setData("VIP_FLG", VIP);// VIP注记
		parm.setData("SERVICE_LEVEL", this.getValue("SERVICE_LEVEL"));// 服务等级
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("WEIGHT", 0);
		parm.setData("HEIGHT", 0);
		parm.setData("TRANHOSP_CODE", "");
		parm.setData("TRIAGE_NO", "");
		String []name={"CONTRACT_CODE","REGCAN_USER","REGCAN_DATE","PREVENT_SCH_CODE",
				"DRG_CODE","NHI_NO","INS_PAT_TYPE","CONFIRM_NO"};
		parm.setData("ADM_REGION", Operator.getRegion());
		for (int i = 0; i < name.length; i++) {
			parm.setData(name[i],"");
		}
		parm.setData("CLINICROOM_NO", this.getValue("CLINICROOM_NO"));// 诊间
		TParm saveParm = new TParm();
		saveParm.setData("REG", parm.getData());
		saveParm.setData("EXE_FLG","Y");
		return saveParm;
	}

	/**
	 * 校验是否预约挂号
	 * 
	 * @return
	 */
	private boolean onBespeak() {
		TParm temp = new TParm();
		temp.setData("ADM_TYPE", admType); // 挂号类型
		temp.setData("SESSION_CODE", this.getValue("SESSION_CODE")); // 时段
		temp.setData("ADM_DATE", StringTool.getString(
				(Timestamp) getValue("ADM_DATE"), "yyyyMMdd"));
		temp.setData("START_TIME", StringTool.getString(SystemTool
				.getInstance().getDate(), "HHmm"));
		String admNowDate = StringTool.getString(SystemTool.getInstance()
				.getDate(), "yyyyMMdd");// 当前时间
		String startTime = "";
		if (temp.getValue("ADM_DATE").compareTo(admNowDate) < 0) {
			this.messageBox("已经过诊不可以挂号");
			callFunction("UI|table2|clearSelection");
			return false;
		} else if (temp.getValue("ADM_DATE").compareTo(admNowDate) == 0) {
			startTime += " AND START_TIME>='" + temp.getValue("START_TIME")
					+ "'";
		}
		// 预约判断是否已经过诊
		String vipSql = "SELECT CLINICROOM_NO,QUE_NO,QUE_STATUS,START_TIME,SESSION_CODE,ADM_DATE "
				+ " FROM REG_CLINICQUE WHERE  QUE_NO IN(SELECT MIN(QUE_NO) AS QUE_NO FROM REG_CLINICQUE "
				+ " WHERE  CLINICROOM_NO='"
				+ this.getValue("CLINICROOM_NO")
				+ "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE")
				+ "'"
				+ " AND SESSION_CODE='"
				+ this.getValue("SESSION_CODE")
				+ "' AND ADM_TYPE='"
				+ admType
				+ "' AND QUE_STATUS='N'"
				+ startTime
				+ ") AND CLINICROOM_NO='"
				+ this.getValue("CLINICROOM_NO")
				+ "' AND ADM_DATE='"
				+ temp.getValue("ADM_DATE")
				+ "'"
				+ " AND SESSION_CODE='"
				+ this.getValue("SESSION_CODE")
				+ "' AND ADM_TYPE='"
				+ admType
				+ "'";
		temp = new TParm(TJDODBTool.getInstance().select(vipSql));
		if (temp.getErrCode() < 0) {
			return false;
		}
		if (null == temp.getValue("QUE_NO", 0)
				|| temp.getValue("QUE_NO", 0).length() <= 0) {
			this.messageBox("已过诊,没有预约号码");
			return false;
		}
		queNo= temp.getInt("QUE_NO", 0);
		regAdmTime= temp.getValue("START_TIME", 0);
		VIP= true;
		return true;
	}
	/**
	 * 查询医师排班(一般)
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
		this.callFunction("UI|TABLE|setParmValue", data);
	}

//	/**
//	 * 查询医师排班(VIP)
//	 */
//	public void onQueryVipDrTable() {
//		TTable table2 = new TTable();
//		table2.removeAll();
//		TParm parm = getParmForTag(
//				"REGION_CODE;ADM_TYPE;VIP_SESSION_CODE;VIP_DEPT_CODE;VIP_DR_CODE",
//				true);
//		parm.setData("ADM_TYPE", admType);
//		parm.setData("VIP_ADM_DATE", StringTool.getString(
//				(Timestamp) getValue("VIP_ADM_DATE"), "yyyyMMdd"));
//		TParm data2 = REGClinicQueTool.getInstance().selVIPDate(parm);
//		if (data2.getErrCode() < 0) {
//			messageBox(data2.getErrText());
//			return;
//		}
//		this.callFunction("UI|Table2|setParmValue", data2);
//	}
	/**
	 * 初始化班表
	 */
	public void initSchDay() {
		new Thread() {
			// 线程,为节省时间提高打开挂号主界面效率
			public void run() {
				// 初始化默认支付方式
				TParm selPayWay = REGSysParmTool.getInstance().selPayWay();
				setValue("PAY_WAY", selPayWay.getValue("DEFAULT_PAY_WAY", 0));
				// 初始化带入医师排班
				onQueryDrTable();

				// 初始化带入VIP班表
				//onQueryVipDrTable();
			}
		}.start();
	}
	/**
	 * 增加对Table1的监听
	 */
	public void onTableClicked() {
		// ===zhangp 20120306 modify start
		callFunction("UI|SAVE_REG|setEnabled", true);
		// ===zhangp 20120306 modify end
		int row = (Integer) callFunction("UI|TABLE|getClickedRow");
		if (row < 0)
			return;
		TParm parm = new TParm();
		parm = getParmForTag(
				"REGION_CODE;ADM_TYPE;ADM_DATE:timestamp;SESSION_CODE", true);
		// TParm data = SchDayTool.getInstance().selectDrTable(parm);
		TTable table1 = (TTable) this.getComponent("TABLE");
		TParm tableParm = table1.getParmValue();
		setValueForParm("CLINICTYPE_CODE;DEPT_CODE;DR_CODE;CLINICROOM_NO",
				tableParm, row);
		TextFormatSYSOperatorForReg operatorForREGText = (TextFormatSYSOperatorForReg) this
				.getComponent("DR_CODE");
		operatorForREGText.onQuery();
		setValue("DR_CODE", tableParm.getValue("DR_CODE", row));
		// 获得挂号方式执行判断是否打票操作
		String sql = "SELECT REGMETHOD_CODE,PRINT_FLG FROM REG_REGMETHOD WHERE REGMETHOD_CODE='"
				+ this.getValue("REGMETHOD_CODE") + "'";
		TParm regMethodParm = new TParm(TJDODBTool.getInstance().select(sql)); // 获得是否可以打票注记
		if (regMethodParm.getErrCode() < 0) {
			this.messageBox("挂号失败");
			return;
		}
	}
	
	/**
	 * 比较时间大小
	 * @param date1
	 * @param date2
	 * @return
	 */
	public int timeComp(String date1, String date2) {
		int day = 0;
		DateFormat df=new SimpleDateFormat("HH:mm:ss");
		Calendar c1=Calendar.getInstance();
		Calendar c2=Calendar.getInstance();
		try{
		 c1.setTime(df.parse(date1));
		 c2.setTime(df.parse(date2));
		}catch(java.text.ParseException e){
			System.err.println("格式不正确");
		}
		int result=c1.compareTo(c2);
		if(result<0)
			day = -1;
//		if(result==0)
//			System.out.println("c1相等c2");
//		else if(result<0)
//			System.out.println("c1小于c2");
//		else
//			System.out.println("c1大于c2");
		return day;
	}

}
