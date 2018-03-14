package com.javahis.ui.sta;

import com.dongyang.control.TControl;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import jdo.sta.STAStationLogTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TCheckBox;
import jdo.sys.Operator;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SystemTool;
import jdo.sta.STADeptListTool;

/**
 * <p>
 * Title:病区日志
 * </p>
 * 
 * <p>
 * Description:病区日志
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangk 2009-4-24
 * @version JavaHis 1.0
 */
public class STAStationLogControl extends TControl {
	String DEPT_CODE;// 记录中间表（部门对照表）的部门ID
	TParm insertParm;// 生成报表的数据
	TParm resultOUT;// 出院病人
	TParm resultIN;// 入院病人
	TParm resultINPR;// 转入病人
	TParm resultOUPR;// 传出病人
	boolean CONFIRM_FLG = false;// 确认标记 默认为false：没有提交 true：提交完毕
	String LEADER = "";// 记录是否是组长权限 如果LEADER=2那么就是组长权限
	private String DATA_StaDate = ""; // 记录目前显示的数据的主键(日期)

	public void onInit() {
		super.onInit();
		initDate();// 初始化日期
		comboboxInit();// 初始化Combo
		// 跟军当前使用者的病区CODE查询科室中间表中对应的科室CODE
		// this.setValue("STATION_CODE",
		// STADeptListTool.getInstance().selectDeptByIPDCODE(Operator.getStation()).getValue("DEPT_CODE",0));
		// 初始化权限
		if (this.getPopedem("LEADER")) {
			LEADER = "2";
		}
	}

	/**
	 * 清空事件
	 */
	public void onClear() {
		this
				.clearValue("DATA_02;DATA_03;DATA_04;DATA_05;DATA_06;DATA_06_1;DATA_01;DATA_07;DATA_08;"
						+ "DATA_08_1;DATA_15_1;DATA_16;DATA_17;DATA_18;DATA_19;DATA_20;DATA_21;DATA_11;"
						+ "DATA_12;DATA_13;DATA_14;DATA_15;DATA_10;DATA_09");
		((TTable) this.getComponent("Table_IN")).removeRowAll();
		((TTable) this.getComponent("Table_OUT")).removeRowAll();
		// 清空提交复选框的选择状态
		((TCheckBox) this.getComponent("Submit")).setSelected(false);
		this.setValue("STATION_CODE", "");
		this.setText("STATION_CODE", "");
		this.getComboBox("IDEPT_CODE").removeAllItems();
		this.getComboBox("IDEPT_CODE").setEnabled(false);
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if (CONFIRM_FLG)
			print();
		else {
			this.messageBox_("需要确认提交后方可打印！");
		}
	}

	/**
	 * 保存
	 */
	public void onSave() {
		// 如果不是组长权限 那么已经提交的数据不可再修改
		if (!LEADER.equals("2")) {
			if (CONFIRM_FLG) { // 已经提交
				this.messageBox_("非组长不能修改已提交的数据！");
				return;
			}
		}
		// 将需要计算的项目 进行计算 更新页面控件
		setSum();
		TParm updateParm = new TParm(); // 记录插入STA_DAILY_01表的参数
		updateParm.setData("DATA_02", getNumber("DATA_02"));
		updateParm.setData("DATA_03", getNumber("DATA_03"));
		updateParm.setData("DATA_05", getNumber("DATA_05"));
		updateParm.setData("DATA_06", getNumber("DATA_06"));
		updateParm.setData("DATA_04", getNumber("DATA_04"));
		updateParm.setData("DATA_06_1", getNumber("DATA_06_1"));
		updateParm.setData("DATA_01", getNumber("DATA_01"));
		updateParm.setData("DATA_07", getNumber("DATA_07"));
		updateParm.setData("DATA_08", this.getValue("DATA_08"));
		updateParm.setData("DATA_08_1", getValue("DATA_08_1"));
		updateParm.setData("DATA_15_1", getValue("DATA_15_1"));
		updateParm.setData("DATA_16", getNumber("DATA_16"));
		updateParm.setData("DATA_17", getNumber("DATA_17"));
		updateParm.setData("DATA_18", getNumber("DATA_18"));
		updateParm.setData("DATA_19", getNumber("DATA_19"));
		updateParm.setData("DATA_20", getNumber("DATA_20"));
		updateParm.setData("DATA_21", getNumber("DATA_21"));
		updateParm.setData("DATA_11", getValue("DATA_11"));
		updateParm.setData("DATA_12", getValue("DATA_12"));
		updateParm.setData("DATA_13", getValue("DATA_13"));
		updateParm.setData("DATA_14", getValue("DATA_14"));
		updateParm.setData("DATA_15", getValue("DATA_15"));
		updateParm.setData("DATA_10", getNumber("DATA_10"));
		updateParm.setData("DATA_09", getNumber("DATA_09"));
		updateParm.setData("DATA_22", getNumber("DATA_22"));
		updateParm.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		updateParm.setData("DEPT_CODE", DEPT_CODE);
		updateParm.setData("STATION_CODE", this.getValue("STATION_CODE")); // 目前病区与科室code相同
		if (((TCheckBox) this.getComponent("Submit")).isSelected()) {
			updateParm.setData("CONFIRM_FLG", "Y"); // 提交标识
		
		} else {
			updateParm.setData("CONFIRM_FLG", "N"); // 提交标识
		}
		updateParm.setData("CONFIRM_USER", Operator.getID()); // 确认人
		updateParm.setData("OPT_USER", Operator.getID());
		updateParm.setData("OPT_TERM", Operator.getIP());
		// ===============pangben modify 20110520 start
		updateParm.setData("REGION_CODE", Operator.getRegion());
		// ===============pangben modify 20110520 stop
		TParm STATION_DAILY = new TParm();
		STATION_DAILY.setData("REAL_OCUU_BED_NUM", getNumber("DATA_16"));// 实有病人数
		STATION_DAILY.setData("OPT_USER", Operator.getID());
		STATION_DAILY.setData("OPT_TERM", Operator.getIP());
		STATION_DAILY.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		STATION_DAILY.setData("DEPT_CODE", DEPT_CODE);
		STATION_DAILY.setData("STATION_CODE", this.getValue("STATION_CODE"));
		TParm parm = new TParm();
		parm.setData("sta_daily_01", updateParm.getData());
		parm.setData("station_daily", STATION_DAILY.getData());
		TParm result = TIOM_AppServer.executeAction(
				"action.sta.STAStationLogAction", "updateSTA_DAILY_01", parm);
		if (result.getErrCode() < 0) {
			CONFIRM_FLG = false;// 状态 修改为 未提交 false
			this.messageBox_("修改失败！");
			return;
		}
        if (((TCheckBox) this.getComponent("Submit")).isSelected()) {
            if (CONFIRM_FLG == true) {
                this.messageBox_("修改成功！");
            } else if (CONFIRM_FLG == false) {
                CONFIRM_FLG = true;// 状态标识为 True
                this.messageBox_("提交成功！");
            }
        } else if (!((TCheckBox) this.getComponent("Submit")).isSelected()) {
            if (CONFIRM_FLG == true) {
                CONFIRM_FLG = false;// 状态标识为 False
                this.messageBox_("取消提交成功！");
            } else if (CONFIRM_FLG == false) {
                this.messageBox_("修改成功！");
            }
        }
	}

	/**
	 * 查询方法 生成数据
	 */
	public void onQuery() {
		insertData();
	}

	/**
	 * 打印
	 */
	private void print() {
		TParm parm = new TParm();
		TParm pr = new TParm();
		pr.setData("STA_DATE", this.getText("STA_DATE").toString().replace("/",
				""));
		pr.setData("DEPT_CODE", DEPT_CODE);
		// ===========pangben modify 20110523 start
		pr.setData("REGION_CODE", Operator.getRegion());
		// ===========pangben modify 20110523 stop
		TParm re = STAStationLogTool.getInstance().selectSTA_DAILY_01(pr);
		if (re.getErrCode() < 0) {
			this.messageBox_("查询报表数据失败！");
			return;
		}
		if (re.getCount() == 0) {
			this.messageBox_("并没有生成相关数据！");
			return;
		}
		TParm par = new TParm();// 报表参数
		par.setData("DEPT", this.getText("STATION_CODE"));// 病区
		par.setData("DATE", this.getText("STA_DATE").toString().replace("/",
				"-"));// 日期
		TParm Iparm = new TParm();
		TParm Oparm = new TParm();
		parm.setData("All", par.getData());// 报表基本参数
		parm.setData("data", re.getData());// 报表信息
		TParm inParm = ((TTable) this.getComponent("Table_IN")).getParmValue();
		TParm outParm = ((TTable) this.getComponent("Table_OUT"))
				.getParmValue();
		for (int i = 0; i < inParm.getCount("MR_NO"); i++) {
			Iparm.addData("IPD_NO", inParm.getValue("IPD_NO", i));
			Iparm.addData("MR_NO", inParm.getValue("MR_NO", i)); // 病案号
			Iparm.addData("PAT_NAME", inParm.getValue("PAT_NAME", i));// 患者姓名
		}
		for (int i = 0; i < outParm.getCount("MR_NO"); i++) {
			Oparm.addData("IPD_NO", outParm.getValue("IPD_NO", i));
			Oparm.addData("MR_NO", outParm.getValue("MR_NO", i)); // 病案号
			Oparm.addData("PAT_NAME", outParm.getValue("PAT_NAME", i));// 患者姓名
		}
		TParm inPrParm = ((TTable) this.getComponent("Table_INPR"))
				.getParmValue();
		TParm outPrParm = ((TTable) this.getComponent("Table_OUPR"))
				.getParmValue();
		// 将他科转入病人加入到入院病人中
		for (int i = 0; i < inPrParm.getCount("MR_NO"); i++) {
			Iparm.addData("IPD_NO", inPrParm.getValue("IPD_NO", i));
			Iparm.addData("MR_NO", inPrParm.getValue("MR_NO", i)); // 病案号
			Iparm.addData("PAT_NAME", inPrParm.getValue("PAT_NAME", i) + "\n("
					+ getdeptDesc(inPrParm.getValue("DEPT_CODE", i)) + ")");// 患者姓名（来自科室名称）
		}
		// 将转出病人加入到出院病人中
		for (int i = 0; i < outPrParm.getCount("MR_NO"); i++) {
			Oparm.addData("IPD_NO", outPrParm.getValue("IPD_NO", i));
			Oparm.addData("MR_NO", outPrParm.getValue("MR_NO", i)); // 病案号
			Oparm.addData("PAT_NAME", outPrParm.getValue("PAT_NAME", i) + "\n("
					+ getdeptDesc(outPrParm.getValue("DEPT_CODE", i)) + ")");// 患者姓名（要转到的科室名称）
		}
		parm.setData("IN", Iparm.getData());// 入院病人
		parm.setData("OUT", Oparm.getData());// 出院病人
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_01.jhw", parm);
	}

	private String getdeptDesc(String deptCode) {
		String deptdesc = "";
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		if (parm.getCount() > 0)
			deptdesc = parm.getValue("DEPT_CHN_DESC", 0);
		return deptdesc;
	}

	/**
	 * 日期初始化
	 */
	private void initDate() {
		// 获取昨天的日期
		Timestamp yestaday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		this.setValue("STA_DATE", yestaday);
	}

	/**
	 * 查询 入院 和出院 人次,转入、传出病人
	 */
	public void selectDate() {
		// 中间科室信息
		TParm staDept = STADeptListTool.getInstance().selectDeptByCode(
				this.getValueString("IDEPT_CODE"),this.getValueString("STATION_CODE"),Operator.getRegion());// ===pangben
		// modify
		// 20110523
		// 查询入院人次
		TParm parm2 = new TParm();
		parm2.setData("STATION_CODE", staDept.getValue("STATION_CODE", 0));// 病区CODE
		parm2.setData("DEPT_CODE", staDept.getValue("IPD_DEPT_CODE", 0));// 住院科室code
		parm2.setData("DATE", this.getText("STA_DATE").toString().replace("/",
				""));// 查询日期
		// ===========pangben modify 20110518 start 添加区域参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm2.setData("REGION_CODE", Operator.getRegion());
		// ===========pangben modify 20110518 start
		resultIN = STAStationLogTool.getInstance().selectInNum(parm2);
		if (resultIN.getErrCode() < 0) {
			this.messageBox_("入院病人查询失败！");
			return;
		}
		((TTable) this.getComponent("Table_IN")).setParmValue(resultIN);
		resultOUT = STAStationLogTool.getInstance().selectOutNum(parm2);
		if (resultOUT.getErrCode() < 0) {
			this.messageBox_("出院病人查询失败！");
			return;
		}
		((TTable) this.getComponent("Table_OUT")).setParmValue(resultOUT);
		String deptCode = staDept.getValue("IPD_DEPT_CODE", 0);
		String stationCode=staDept.getValue("STATION_CODE", 0);
		String date = this.getText("STA_DATE").toString().replace("/", "");
		int incount = 0;
		resultINPR = new TParm();
		// ==========pangben modify 20110518 start 添加selectINPR方法区域参数
		TParm inprParm = STAStationLogTool.getInstance().selectINPR(deptCode,stationCode,
				date, Operator.getRegion());
		// System.out.println("-=-----inprParm----------"+inprParm);
		for (int i = 0; i < inprParm.getCount(); i++) {
			resultINPR.addData("IPD_NO", inprParm.getValue("IPD_NO", i));
			resultINPR.addData("MR_NO", inprParm.getValue("MR_NO", i));
			resultINPR.addData("PAT_NAME", inprParm.getValue("PAT_NAME", i));
			resultINPR.addData("DEPT_CODE", inprParm
					.getValue("IN_DEPT_CODE", i));
			resultINPR.addData("STATION_CODE", inprParm
					.getValue("IN_STATION_CODE", i));
		}
		// ==========pangben modify 20110518 stop
		if (resultINPR.getErrCode() < 0) {
			this.messageBox_("转入病人查询失败！");
			return;
		}
		((TTable) this.getComponent("Table_INPR")).setParmValue(resultINPR);
		resultOUPR = new TParm();
		int outCount = 0;
		// ==========pangben modify 20110518 start 添加selectINPR方法区域参数
		TParm ouprParm = STAStationLogTool.getInstance().selectOUPR(deptCode,stationCode,
				date, Operator.getRegion());
		// System.out.println("-=----------"+ouprParm);
		for (int i = 0; i < ouprParm.getCount(); i++) {
			resultOUPR.addData("IPD_NO", ouprParm.getValue("IPD_NO", i));
			resultOUPR.addData("MR_NO", ouprParm.getValue("MR_NO", i));
			resultOUPR.addData("PAT_NAME", ouprParm.getValue("PAT_NAME", i));
			resultOUPR.addData("DEPT_CODE", ouprParm.getValue("OUT_DEPT_CODE",
					i));
			resultOUPR.addData("STATION_CODE", ouprParm
					.getValue("OUT_STATION_CODE", i));
		}
		// ==========pangben modify 20110518 stop
		if (resultOUPR.getErrCode() < 0) {
			this.messageBox_("转出病人查询失败！");
			return;
		}
		((TTable) this.getComponent("Table_OUPR")).setParmValue(resultOUPR);
	}

	/**
	 * 设置需要计算的 条目
	 */
	private void setSum() {
		this.getNumText("DATA_10").setValue(getDATA_10());// 病人数计 = 11+12+13+14
		// this.getNumText("DATA_09").setValue(getDATA_09());//出院人数总计
		// =11+12+13+14+15
		this.getNumText("DATA_16").setValue(getDATA_16());// 实有病人数 = 7+8+8_1-9
		// 注意顺序，要先计算09后才能计算16
		this.setValue("DATA_18", this.getValue("DATA_17"));// 实际开放病床数 = 实有病人数
	}
	/**
	 * 前向
	 */
    public  void  onDaySelBef(){
    	if(this.getValue("STA_DATE")==null){
    		this.messageBox("请输入日期");
    		return;
    	}
    	Timestamp day=(Timestamp)getValue("STA_DATE");
    	Timestamp befday=StringTool.rollDate(day, -1);
		this.setValue("STA_DATE", befday);
		this.onQuery();
    }
    /**
	 * 前向
	 */
    public  void  onDaySelAf(){
    	if(this.getValue("STA_DATE")==null){
    		this.messageBox("请输入日期");
    		return;
    	}
    	Timestamp day=(Timestamp)getValue("STA_DATE");
    	Timestamp afday=StringTool.rollDate(day, +1);
		this.setValue("STA_DATE", afday);
		this.onQuery();
    }
	/**
	 * 计算 病人数计 = 11+12+13+14
	 * 
	 * @return int
	 */
	private int getDATA_10() {
		int DATA_11 = Integer.valueOf(getValueString("DATA_11"));
		int DATA_12 = Integer.valueOf(getValueString("DATA_12"));
		int DATA_13 = Integer.valueOf(getValueString("DATA_13"));
		int DATA_14 = Integer.valueOf(getValueString("DATA_14"));
		return DATA_11 + DATA_12 + DATA_13 + DATA_14;
	}

	/**
	 * 出院人数总计 =11+12+13+14+15
	 * 
	 * @return int
	 */
	private int getDATA_09() {
		int DATA_11 = Integer.valueOf(getValueString("DATA_11"));
		int DATA_12 = Integer.valueOf(getValueString("DATA_12"));
		int DATA_13 = Integer.valueOf(getValueString("DATA_13"));
		int DATA_14 = Integer.valueOf(getValueString("DATA_14"));
		int DATA_15 = Integer.valueOf(getValueString("DATA_15"));
		// int DATA_15_1 = getNumber("DATA_15_1");
		return DATA_11 + DATA_12 + DATA_13 + DATA_14 + DATA_15;
	}

	/**
	 * 计算 实有病人数 = 7+8+8_1-9-15_1
	 * 
	 * @return int
	 */
	private int getDATA_16() {
		int DATA_07 = getNumber("DATA_07");
		int DATA_08 = Integer.valueOf(getValueString("DATA_08"));
		int DATA_08_1 = Integer.valueOf(getValueString("DATA_08_1"));
		int DATA_09 = getNumber("DATA_09");
		int DATA_15_1 = Integer.valueOf(getValueString("DATA_15_1"));
		return DATA_07 + DATA_08 + DATA_08_1 - DATA_09 - DATA_15_1;
	}

	/**
	 * 获取页面的TNumberText
	 * 
	 * @param tag
	 *            String
	 * @return TNumberText
	 */
	private TNumberTextField getNumText(String tag) {
		return (TNumberTextField) this.getComponent(tag);
	}

	/**
	 * 获取指定 TNumberText 的值
	 * 
	 * @param tag
	 *            String
	 * @return int
	 */
	private int getNumber(String tag) {
		return this.getNumText(tag).getValue() == null ? 0 : this
				.getValueInt(tag);
	}

	/**
	 * 部门，病区combobox初始化
	 */
	private void comboboxInit() {
		// ============pangben modify 20110519 start
		TParm deptParm = new TParm();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			deptParm.setData("REGION_CODE", Operator.getRegion());
		TParm dept = STAStationLogTool.getInstance().selectDept(deptParm);
		// ============pangben modify 20110519 stop
		// TParm station = STAStationLogTool.getInstance().selectSTAStation();
		// String sql = STASQLTool.getInstance().get_STA_Station(
		// Operator.getRegion());// ========pangben modify 20110523
		// // ((TComboBox)this.getComponent("DEPT_CODE")).setParmValue(dept);
		// TTextFormat station = (TTextFormat)
		// this.getComponent("STATION_CODE");
		// station.setPopupMenuSQL(sql);
		Object obj = this.getParameter();
		TParm parm = new TParm();
		if (obj instanceof TParm) {
			parm = (TParm) obj;
			this.setValue("STATION_CODE", parm.getValue("STATION"));
			this.callFunction("UI|STATION_CODE|setEnabled", false);
		} else {
			// shibl add
			String userId = Operator.getID();
			this.setValue("USER", userId);
			this.setValue("STATION_CODE", "");
			callFunction("UI|USER|onQuery");
		}
		this.getComboBox("IDEPT_CODE").setEnabled(false);
		this.getComboBox("IDEPT_CODE").setValue("");
	}

	public void selectStation() {
		String deptsql = "SELECT DEPT_CODE,DEPT_DESC,PY1 FROM STA_OEI_DEPT_LIST  WHERE  STATION_CODE='"
				+ this.getValueString("STATION_CODE")
				+ "' AND REGION_CODE='"
				+ Operator.getRegion() + "'";
		TParm inparm = new TParm(TJDODBTool.getInstance().select(deptsql));
		if (inparm.getCount() > 0) {
			getComboBox("IDEPT_CODE").setParmValue(inparm);
			getComboBox("IDEPT_CODE").setSelectedIndex(0);
			getComboBox("IDEPT_CODE").setEnabled(true);
		} else {
			getComboBox("IDEPT_CODE").removeAllItems();
			getComboBox("IDEPT_CODE").setEnabled(false);
			getComboBox("IDEPT_CODE").setParmValue(null);
			this.setValue("IDEPT_CODE", "");
		}
	}

	/**
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 生成数据
	 */
	private void insertData() {
		if (this.getValueString("STATION_CODE").trim().length() <= 0) {
			this.messageBox_("请选择病区！");
			((TTextFormat) this.getComponent("STATION_CODE"))
					.setFocusable(true);
			return;
		}
		if (this.getValueString("IDEPT_CODE").trim().length() <= 0) {
			this.messageBox_("请选择科室！");
			((TTextFormat) this.getComponent("IDEPT_CODE")).setFocusable(true);
			return;
		}
		CONFIRM_FLG = false;
		TParm checkRe = new TParm();
		TParm parm = new TParm();
		parm.setData("DEPT_CODE", this.getValue("IDEPT_CODE"));// 病区CODE
		parm.setData("STATION_CODE", this.getValue("STATION_CODE"));// 病区CODE
		parm.setData("STA_DATE", this.getText("STA_DATE").toString().replace(
				"/", ""));// 查询日期
		// ============pangben modify 20110519 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// ============pangben modify 20110519 stop
		// 先查询该日期的数据是否存在
		checkRe = STAStationLogTool.getInstance().selectSTA_DAILY_01(parm);
		if (checkRe.getErrCode() < 0) {
			this.messageBox("没有查询到该日期的数据");
			return;
		}

		// 数据存在
		if (checkRe.getCount("STA_DATE") > 0) {
			if (checkRe.getValue("CONFIRM_FLG", 0).equals("Y")) {// 数据已经提交
				CONFIRM_FLG = true;// 修改标记 为true 表示已经提交 不允许修改
				DEPT_CODE = checkRe.getValue("DEPT_CODE", 0);// 获取中间表部门CODE
				messageBox_("数据已经提交，不能重新生成！");
				setTextValue(checkRe);// 页面赋值
				selectDate();// 查询入院和出院人次
				this.setValue("Submit", true);// 提交combo设置为选中状态
				return;
			} else {// 存在数据并没有提交
				this.setValue("Submit", false);// 提交combo设置为没有选中状态
				switch (this.messageBox("提示信息", "数据已存在，是否重新生成？",
						this.YES_NO_OPTION)) {
				case 0:// 生成
					break;
				case 1:// 不生成
					DEPT_CODE = checkRe.getValue("DEPT_CODE", 0);// 获取中间表部门CODE
					setTextValue(checkRe);// 绑定数据
					selectDate();// 查询入院和出院人次
					return;
				}
			}
		}
		// 搜索中间表数据 生成STA_DAILY_01表数据
		// TParm result = STAStationLogTool.getInstance().selectData(parm);
		// 查询门急诊中间表
		TParm OPD_P = new TParm();
		OPD_P.setData("DEPT_CODE", this.getValue("IDEPT_CODE"));// 病区CODE
		OPD_P.setData("STA_DATE", this.getText("STA_DATE").toString().replace(
				"/", ""));// 查询日期
		OPD_P.setData("REGION_CODE", Operator.getRegion());// =========pangben
		// modify 20110523
		TParm resultOPD = STAStationLogTool.getInstance().selectSTA_OPD_DAILY(
				OPD_P);
		// 查询病区中间表
		TParm Sattion_P = new TParm();
		Sattion_P.setData("DEPT_CODE", this.getValue("IDEPT_CODE"));// 病区CODE
		Sattion_P.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));// 查询日期
		Sattion_P.setData("REGION_CODE", Operator.getRegion());// =========pangben
		// modify
		Sattion_P.setData("STATION_CODE",this.getValue("STATION_CODE"));//SHIBL 20130702
		
		TParm resultSattion = STAStationLogTool.getInstance()
				.selectSTA_STATION_DAILY(Sattion_P);
		if (resultOPD.getErrCode() < 0 || resultSattion.getErrCode() < 0) {
			this.messageBox_("查询失败！");
			this.onClear();
			return;
		}
		// 两个结果集都没有数据
		if (resultOPD.getCount() <= 0 && resultSattion.getCount() <= 0) {
			this.messageBox_("没有查询到相关日期的数据");
			this.onClear();
			return;
		}
		insertParm = new TParm();// 记录插入STA_DAILY_01表的参数

		insertParm.setData("DATA_02", "0");// 肌注
		insertParm.setData("DATA_03", "0");// 静注
		insertParm.setData("DATA_05", "0");// 皮试
		insertParm.setData("DATA_06", "0");// 缝合
		insertParm.setData("DATA_04", "0");// 静点
		insertParm.setData("DATA_06_1", "0");// 过关
		// 急诊总计人数
		if (resultOPD.getCount() > 0) {
			insertParm.setData("DATA_01", resultOPD.getData("ERD_NUM", 0));
			DEPT_CODE = resultOPD.getValue("DEPT_CODE", 0);// 中间档部门code
		} else
			insertParm.setData("DATA_01", "");
		if (resultSattion.getCount() > 0) {// 中间表中存在数据
			DEPT_CODE = resultSattion.getValue("DEPT_CODE", 0);// 中间档部门code
			// 获取前一天日期
			String yesterday = getYesterdayString(this.getText("STA_DATE")
					.toString().replace("/", ""));
			if (yesterday.equals("")) {
				System.out.println("计算前一天错误！");
			}
			int ORIGINAL_NUM = 0;
			String sql = " SELECT DATA_16 FROM STA_DAILY_01 WHERE STA_DATE='"
					+ yesterday + "' AND DEPT_CODE='" + DEPT_CODE + "' AND STATION_CODE='" + this.getValue("STATION_CODE") + "'";
			TParm ORIGINAL_NUMparm = new TParm(this.getDBTool().select(sql));
			if (ORIGINAL_NUMparm.getErrCode() < 0) {
				System.out.println("计算昨日原有病人数错误！");
			}
			if (ORIGINAL_NUMparm.getCount() > 0)
				ORIGINAL_NUM = ORIGINAL_NUMparm.getInt("DATA_16", 0);
			insertParm.setData("DATA_07", ORIGINAL_NUM); // 昨日原有病人数
			insertParm.setData("DATA_08", resultSattion.getInt("ADM_NUM", 0)); // 入院数
			insertParm.setData("DATA_08_1", resultSattion.getInt(
					"FROM_OTHER_DEPT", 0)); // 他科转入数
			insertParm.setData("DATA_15_1", resultSattion.getInt(
					"TRANS_DEPT_NUM", 0)); // 转往他科人数
			insertParm.setData("DATA_17", resultSattion
					.getInt("END_BED_NUM", 0)); // 实际开放病床数
			insertParm.setData("DATA_18", resultSattion
					.getInt("END_BED_NUM", 0)); // 实际开放病床日
			insertParm.setData("DATA_19", resultSattion.getInt(
					"DS_TOTAL_ADM_DAY", 0)); // 出院者住院日数
			insertParm.setData("DATA_20", resultSattion.getInt("GET_TIMES", 0)); // 危重病人抢救数（手填）
			insertParm.setData("DATA_21", resultSattion.getInt("SUCCESS_TIMES",
					0)); // 抢救成功数
			insertParm.setData("DATA_11", resultSattion
					.getInt("RECOVER_NUM", 0)); // 治 愈
			insertParm
					.setData("DATA_12", resultSattion.getInt("EFFECT_NUM", 0)); // 好
			// 转
			insertParm.setData("DATA_13", resultSattion
					.getInt("INVALED_NUM", 0)); // 未 愈
			insertParm.setData("DATA_14", resultSattion.getInt("DIED_NUM", 0)); // 死
			// 亡
			insertParm.setData("DATA_15", resultSattion.getInt("OTHER_NUM", 0)); // 其他
			// 10=11+12+13+14
			int DATA_10 = resultSattion.getInt("RECOVER_NUM", 0)
					+ resultSattion.getInt("EFFECT_NUM", 0)
					+ resultSattion.getInt("INVALED_NUM", 0)
					+ resultSattion.getInt("DIED_NUM", 0);
			// 9=11+12+13+14+15
			// int DATA_09 = resultSattion.getInt("RECOVER_NUM", 0) +
			// resultSattion.getInt("EFFECT_NUM", 0) +
			// resultSattion.getInt("INVALED_NUM", 0) +
			// resultSattion.getInt("DIED_NUM", 0) +
			// resultSattion.getInt("OTHER_NUM", 0) ;
			int DATA_16 = ORIGINAL_NUM + resultSattion.getInt("ADM_NUM", 0)
					+ resultSattion.getInt("FROM_OTHER_DEPT", 0)
					- resultSattion.getInt("DS_ADM_NUM", 0)
					- resultSattion.getInt("TRANS_DEPT_NUM", 0);// 注意要减去转往他科的人数
			insertParm.setData("DATA_10", DATA_10); // 病人数计 = 11+12+13+14
			insertParm
					.setData("DATA_09", resultSattion.getInt("DS_ADM_NUM", 0)); // 出院人数总计
			// =11+12+13+14+15
			insertParm.setData("DATA_16", DATA_16); // 实有病人数 = 7+8+8_1-9-15_1
			// 注意顺序，要先计算09后才能计算16
		} else {// 不存在数据
			insertParm.setData("DATA_07", ""); // 昨日原有病人数
			insertParm.setData("DATA_08", ""); // 入院数
			insertParm.setData("DATA_08_1", ""); // 他科转入数
			insertParm.setData("DATA_15_1", ""); // 转往他科人数
			insertParm.setData("DATA_17", ""); // 实际开放病床数
			insertParm.setData("DATA_18", ""); // 实际开放病床日
			insertParm.setData("DATA_19", ""); // 出院者住院日数
			insertParm.setData("DATA_20", ""); // 危重病人抢救数
			insertParm.setData("DATA_21", ""); // 抢救成功数
			insertParm.setData("DATA_11", ""); // 治 愈
			insertParm.setData("DATA_12", ""); // 好 转
			insertParm.setData("DATA_13", ""); // 未 愈
			insertParm.setData("DATA_14", ""); // 死 亡
			insertParm.setData("DATA_15", ""); // 其他
			insertParm.setData("DATA_10", ""); // 病人数计 = 11+12+13+14
			insertParm.setData("DATA_09", ""); // 出院人数总计 =11+12+13+14+15
			insertParm.setData("DATA_16", ""); // 实有病人数 = 7+8+8_1-9
			// 注意顺序，要先计算09后才能计算16
		}
		insertParm.setData("DATA_22", resultSattion.getInt("CARE_NUMS", 0));// 陪人数
		insertParm.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		insertParm.setData("DEPT_CODE", DEPT_CODE);
		insertParm.setData("STATION_CODE", this.getValue("STATION_CODE")); // 目前病区与科室code相同
		insertParm.setData("CONFIRM_FLG", "N"); // 提交标识 默认为“N”
		insertParm.setData("CONFIRM_USER", Operator.getID()); // 确认人
		insertParm.setData("OPT_USER", Operator.getID());
		insertParm.setData("OPT_TERM", Operator.getIP());
		// ==============pangben modify 20110520 start 添加区域列
		insertParm.setData("REGION_CODE", Operator.getRegion());
		TParm newParm = new TParm();// 总参数
		TParm parmDel = new TParm();// 删除 参数
		parmDel.setData("STA_DATE", this.getText("STA_DATE").toString()
				.replace("/", ""));
		parmDel.setData("DEPT_CODE", DEPT_CODE);
		parmDel.setData("STATION_CODE", this.getValue("STATION_CODE"));
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parmDel.setData("REGION_CODE", Operator.getRegion());
		// ==============pangben modify 20110520 stop
		// 设置参数
		newParm.setData("Del", parmDel.getData());// 加入删除参数
		newParm.setData("Insert", insertParm.getData());// 加入添加参数
		TParm re = TIOM_AppServer.executeAction(
				"action.sta.STAStationLogAction", "creatData", newParm);
		if (re.getErrCode() < 0) {
			this.messageBox_("插入失败！");
			return;
		}
		this.clearValue("Submit");
		// 查询新数据
		TParm selectNew = STAStationLogTool.getInstance().selectSTA_DAILY_01(
				insertParm);
		if (selectNew.getErrCode() < 0) {
			this.messageBox_("查询失败！");
			return;
		}
		setTextValue(selectNew);
		selectDate();// 查询入院和出院人次
		this.messageBox_("生成完毕");
	}

	/**
	 * 给控件赋值
	 * 
	 * @param parm
	 *            TParm
	 */
	private void setTextValue(TParm parm) {
		this.getNumText("DATA_02").setValue(parm.getInt("DATA_02", 0));
		this.getNumText("DATA_03").setValue(parm.getInt("DATA_03", 0));
		this.getNumText("DATA_04").setValue(parm.getInt("DATA_04", 0));
		this.getNumText("DATA_05").setValue(parm.getInt("DATA_05", 0));
		this.getNumText("DATA_06").setValue(parm.getInt("DATA_06", 0));
		this.getNumText("DATA_06_1").setValue(parm.getInt("DATA_06_1", 0));
		this.getNumText("DATA_01").setValue(parm.getInt("DATA_01", 0));
		this.getNumText("DATA_07").setValue(parm.getInt("DATA_07", 0));
		setValue("DATA_08", parm.getInt("DATA_08", 0));
		setValue("DATA_08_1", parm.getInt("DATA_08_1", 0));
		setValue("DATA_15_1", parm.getInt("DATA_15_1", 0));
		this.getNumText("DATA_16").setValue(parm.getInt("DATA_16", 0));
		this.getNumText("DATA_17").setValue(parm.getInt("DATA_17", 0));
		this.getNumText("DATA_18").setValue(parm.getInt("DATA_18", 0));
		this.getNumText("DATA_19").setValue(parm.getInt("DATA_19", 0));
		this.getNumText("DATA_20").setValue(parm.getInt("DATA_20", 0));
		this.getNumText("DATA_21").setValue(parm.getInt("DATA_21", 0));
		this.getNumText("DATA_22").setValue(parm.getInt("DATA_22", 0));
		setValue("DATA_11", parm.getInt("DATA_11", 0));
		setValue("DATA_12", parm.getInt("DATA_12", 0));
		setValue("DATA_13", parm.getInt("DATA_13", 0));
		setValue("DATA_14", parm.getInt("DATA_14", 0));
		setValue("DATA_15", parm.getInt("DATA_15", 0));
		this.getNumText("DATA_10").setValue(parm.getInt("DATA_10", 0));
		this.getNumText("DATA_09").setValue(parm.getInt("DATA_09", 0));
	}

	/**
	 * 病区下拉框选择事件
	 */
	public void onStationChoose() {
		String code = this.getValue("STATION_CODE").toString();
		this.setValue("DEPT_CODE", code);
	}

	/**
	 * 获取前一天的日期字符串
	 * 
	 * @param date
	 *            String 格式 YYYYMMDD
	 * @return String
	 */
	private String getYesterdayString(String date) {
		String yesterday = "";
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
		Date d;
		try {
			d = df.parse(date);
			Calendar ctest = Calendar.getInstance();
			ctest.setTime(d);
			ctest.add(Calendar.DATE, -1);
			d = ctest.getTime();
			yesterday = df.format(d);
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
		return yesterday;
	}

	/**
	 * getDBTool 数据库工具实例
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

}
