package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.util.Map;

import jdo.sta.STADeptListTool;
import jdo.sta.STAIn_02Tool;
import jdo.sta.STASQLTool;
import jdo.sta.STATool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 医院门、急诊工作统计报表及台帐
 * </p>
 * 
 * <p>
 * Description: 医院门、急诊工作统计报表及台帐
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author zhangk 2009-6-15
 * @version 1.0
 */
public class STAIn_02Control extends TControl {
	private boolean STA_CONFIRM_FLG = false; // 记录是否提交
	private String DATA_StaDate = ""; // 记录目前显示的数据的主键(日期)
	private String S_TYPE = "";// 记录当前统计的类型 month:月统计 day:日期段统计
	private String DATE_Start = "";// 记录起始日期
	private String DATE_End = "";// 记录截止日期
	private String DAY_DEPT = "";// 记录按照日期段统计时统计的科室
	private String LEADER = "";// 记录是否是组长权限 如果LEADER=2那么就是组长权限

	/**
	 * 页面初始化
	 */
	public void onInit() {
		super.init();
		// 设置初始时间
		this.setValue("STA_DATE", STATool.getInstance().getLastMonth());
		initDate();
		this.setValue("PRINT_TYPE", "3");// 初始化打印模式
		// 初始化权限
		if (this.getPopedem("LEADER")) {
			LEADER = "2";
		}
		showFormat();
	}

	/**
	 * 添加科室combo筛选 ========pangben modify 20110526
	 */
	public void showFormat() {
		// 科室
		TTextFormat DEPT_CODE = (TTextFormat) this.getComponent("DEPT_CODE");

		// 区域条件添加
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL()
					+ " AND REGION_CODE='" + Operator.getRegion()
					+ "' ORDER BY DEPT_CODE");
		} else {
			DEPT_CODE.setPopupMenuSQL(DEPT_CODE.getPopupMenuSQL()
					+ " ORDER BY DEPT_CODE");
		}
		DEPT_CODE.onQuery();
	}

	/**
	 * 设置 日期段查询的初始时间
	 */
	private void initDate() {
		// 设置初始时间
		Timestamp lastMonth = STATool.getInstance().getLastMonth();// 获取上个月的月份
		// 上月第一天
		this.setValue("DATE_S", StringTool.getTimestamp(
				StringTool.getString(lastMonth, "yyyyMM") + "01", "yyyyMMdd"));
		// 上月最后一天
		this.setValue(
				"DATE_E",
				STATool.getInstance().getLastDayOfMonth(
						StringTool.getString(lastMonth, "yyyyMM")));
	}

	/**
	 * 根据统计方式 显示或者隐藏月份或者时间段控件
	 * 
	 * @param type
	 *            String "month":月统计 "days":日期统计
	 */
	private void setDateVisble(String type) {
		if ("month".equals(type)) {// 月统计
			this.setText("tLabel_0", "统计月份");
			((TTextFormat) this.getComponent("STA_DATE")).setVisible(true);// 显示月份控件
			((TTextFormat) this.getComponent("DATE_S")).setVisible(false);// 隐藏日期段控件
			((TTextFormat) this.getComponent("DATE_E")).setVisible(false);// 隐藏日期段控件
			((TLabel) this.getComponent("label21")).setVisible(false);
			callFunction("UI|query|setEnabled", true); // 生成按钮设置
			callFunction("UI|save|setEnabled", true); // 保存按钮设置
			callFunction("UI|Table_Read|setVisible", false);// 隐藏日统计表格
			callFunction("UI|Table|setVisible", true);// 显示月统计表格
		} else if ("days".equals(type)) {
			this.setText("tLabel_0", "统计日期");
			((TTextFormat) this.getComponent("STA_DATE")).setVisible(false);// 显示月份控件
			((TTextFormat) this.getComponent("DATE_S")).setVisible(true);// 隐藏日期段控件
			((TTextFormat) this.getComponent("DATE_E")).setVisible(true);// 隐藏日期段控件
			((TLabel) this.getComponent("label21")).setVisible(true);
			callFunction("UI|query|setEnabled", false); // 生成按钮设置
			callFunction("UI|save|setEnabled", false); // 保存按钮设置
			callFunction("UI|Table_Read|setVisible", true);// 显示日统计表格
			callFunction("UI|Table|setVisible", false);// 隐藏月统计表格
		}
		onClear();// 清空
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// 设置初始时间
		this.setValue("STA_DATE", STATool.getInstance().getLastMonth());
		initDate();
		this.clearValue("Submit");
		TTable table1 = (TTable) this.getComponent("Table");
		table1.removeRowAll();
		// table1.resetModify();
		TTable table2 = (TTable) this.getComponent("Table_Read");
		table2.removeRowAll();
		// 清空部门combo
		TTextFormat dept = (TTextFormat) this.getComponent("DEPT_CODE");
		dept.setText("");
		dept.setValue("");
		this.setValue("PRINT_TYPE", "3");// 初始化打印模式
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String dept = "";
		if (DATA_StaDate.length() > 0) {
			dept = this.getValueString("DEPT_CODE");
		}
		String STA_DATE = this.getText("STA_DATE").replace("/", "");
		gridBind(STA_DATE, dept);

	}

	/**
	 * 生成数据
	 */
	public void onGenerate() {
		if ("Y".equals(this.getValueString("R_MONTH"))) {// 生成需要数据
			// 注意：月报数据是正规数据 保存在STA_IN_02表中
			generate_MonthData();
		} else if ("Y".equals(this.getValueString("R_DAYS"))) {// 生成日期时段数据
			// 时间段查询的报表数据是非正规数据，只有查询显示功能，不进行保存不可修改
			generate_DayData();
		}
	}

	/**
	 * 生成月报数据
	 */
	private void generate_MonthData() {
		S_TYPE = "month";// 记录统计类型
		// 清空部门Combo
		TTextFormat dept = (TTextFormat) this.getComponent("DEPT_CODE");
		dept.setText("");
		dept.setValue("");

		STA_CONFIRM_FLG = false; // 初始默认数据没有提交
		String STA_DATE = this.getText("STA_DATE").replace("/", "");
		if (STA_DATE.trim().length() <= 0) {
			this.messageBox_("请选择统计时间段!");
			return;
		}
		// 检查是否可以重新生成数据
		if (!canGeneration(STA_DATE)) {
			// 如果不能重新生成数据，绑定原有数据
			gridBind(STA_DATE, ""); // 数据绑定
			return;
		}
		TParm data = STAIn_02Tool.getInstance().selectData(STA_DATE,
				Operator.getRegion());// ====pangben modify 20110523
		if (data.getErrCode() < 0) {
			this.messageBox_("统计数据错误" + data.getErrName() + data.getErrText());
			return;
		}
		String userID = Operator.getID();
		String IP = Operator.getIP();
		for (int i = 0; i < data.getCount("STA_DATE"); i++) {
			data.setData("CONFIRM_FLG", i, "N");
			data.setData("CONFIRM_USER", i, "");
			data.setData("CONFIRM_DATE", i, "");
			data.setData("OPT_USER", i, userID);
			data.setData("OPT_TERM", i, IP);
			// ====pangben modify 20110523
			data.setData("REGION_CODE", i, Operator.getRegion());
		}
		TParm re = TIOM_AppServer.executeAction("action.sta.STAIn_02Action",
				"insertSTA_IN_02", data);
		if (re.getErrCode() < 0) {
			this.messageBox_("生成失败！");
			return;
		}
		this.messageBox_("生成成功！");
		gridBind(STA_DATE, ""); // 绑定新生成的数据
	}

	/**
	 * 生成日期段报表数据
	 */
	private void generate_DayData() {
		S_TYPE = "day";// 记录统计类型
		TParm parm = new TParm();
		String DATE_S = this.getText("DATE_S").replace("/", "");
		String DATE_E = this.getText("DATE_E").replace("/", "");
		String DEPT = this.getValueString("DEPT_CODE");
		if (DATE_S.length() <= 0 || DATE_E.length() <= 0) {
			this.messageBox_("请选择日期范围");
			((TTextFormat) this.getComponent("DATE_S")).grabFocus();
			return;
		}
		parm.setData("DATE_S", DATE_S);
		parm.setData("DATE_E", DATE_E);
		// ======pangben modify 20110523 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// ======pangben modify 20110523 stop
		if (DEPT.length() > 0) {
			parm.setData("DEPT_CODE", DEPT);
		}
		TParm result = STAIn_02Tool.getInstance().selectData(parm);
		if (result.getErrCode() < 0) {
			this.messageBox_("生成失败 " + result.getErrText());
			return;
		}
		DAY_DEPT = DEPT;// 记录统计的科室
		gridBind(result);
	}

	/**
	 * 保存
	 */
	public void onSave() {
		TTable table = (TTable) this.getComponent("Table");
		table.acceptText();
		TParm tableParm = table.getParmValue();
		if (table.getRowCount() <= 0)
			return;
		// 如果不是组长权限 那么已经提交的数据不可再修改
		if (!LEADER.equals("2")) {
			if (STA_CONFIRM_FLG) {
				this.messageBox_("数据已经提交，不能修改");
				return;
			}
		}
		// table.acceptText();
		// TDataStore ds = table.getDataStore();
		String optUser = Operator.getID();
		String optIp = Operator.getIP();
		String STA_DATE = this.getText("STA_DATE").replace("/", "");
		// 获取服务器时间
		Timestamp CONFIRM_DATE = SystemTool.getInstance().getDate();
		// 是否提交
		boolean submit = ((TCheckBox) this.getComponent("Submit")).isSelected();
		String message = "修改成功！"; // 提示语
		if (submit)
			message = "提交成功！";
		TParm parm=new TParm();
		for (int i = 0; i< tableParm.getCount("DEPT_CODE"); i++) {
			// 判断是否提交
			if (submit) {
				tableParm.setData("CONFIRM_FLG", i, "Y");
				tableParm.setData("CONFIRM_USER", i, optUser);
				tableParm.setData("CONFIRM_DATE", i, CONFIRM_DATE);
			} else {
				tableParm.setData("CONFIRM_FLG", i, "N");
				tableParm.setData("CONFIRM_USER", i, "");
				tableParm.setData("CONFIRM_DATE", i, "");
			}
			tableParm.setData("OPT_USER", i, optUser);
			tableParm.setData("OPT_TERM", i, optIp);
			tableParm.setData("OPT_DATE", i, CONFIRM_DATE);
			if(!tableParm.getValue("STA_DATE", i).equals("")){
				parm.addRowData(tableParm, i);
			}
		}
		TParm re = TIOM_AppServer.executeAction("action.sta.STAIn_02Action",
				"updateSTA_IN_02", parm);
		if (re.getErrCode() < 0) {
			this.messageBox_("操作失败！");
			return;
		} else {
			this.messageBox_(message);
			if (submit)
				STA_CONFIRM_FLG = true;

		}
		this.gridBind(STA_DATE, "");
	}

	/**
	 * 打印
	 */
	public void onPrint() {
		if ("Y".equals(this.getValueString("R_MONTH"))) {// 生成需要数据
			// 注意：月报数据是正规数据 保存在STA_IN_02表中
			S_TYPE="month";
			DATA_StaDate= this.getText("STA_DATE").replace("/", "");
		} else if ("Y".equals(this.getValueString("R_DAYS"))) {// 生成日期时段数据
			// 时间段查询的报表数据是非正规数据，只有查询显示功能，不进行保存不可修改
			S_TYPE="day";
			DATE_Start = this.getText("DATE_S").replace("/", "");
			DATE_End = this.getText("DATE_E").replace("/", "");
		}
		if (DATA_StaDate.trim().length() <= 0
				&& (DATE_Start.length() <= 0 || DATE_End.length() <= 0)) {
			this.messageBox("没有要打印的数据");
			return;
		}
		// 目前不限定必须提交后才能打印
		// if (!STA_CONFIRM_FLG) {
		// this.messageBox_("数据提交后才能生成报表");
		// return;
		// }
		TParm printParm = new TParm();
		String dataDate = "";
		if ("month".equals(S_TYPE)) {
			dataDate = DATA_StaDate.substring(0, 4) + "年"
					+ DATA_StaDate.subSequence(4, 6) + "日";
		} else if ("day".equals(S_TYPE)) {
			dataDate = DATE_Start + "~" + DATE_End;
		}
		TParm data = this.getPrintData(DATA_StaDate);
		if (data.getErrCode() < 0) {
			return;
		}
		printParm.setData("T1", data.getData());
		printParm.setData("unit", "TEXT", Operator.getHospitalCHNFullName());// 填报单位
		printParm.setData("queryDate", "TEXT", dataDate);// 数据日期
		printParm.setData("TableHeader", "TEXT", "科  别");
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_02.jhw",
				printParm);
	}

	/**
	 * 获取打印数据
	 * 
	 * @param STA_DATE
	 *            String
	 * @return TParm
	 */
	private TParm getPrintData(String STA_DATE) {
		DecimalFormat df = new DecimalFormat("0.00");
		TParm printData = new TParm();// 打印数据
		String printType = this.getValueString("PRINT_TYPE");// 打印类型
		TParm DeptList = new TParm();
		Map deptOE = STADeptListTool.getInstance().getOEDeptMap(
				Operator.getRegion());// ===pangben modify 20110524
		// 如果是日期段统计 并且选择了某一个科室进行统计，那么打印的时候只显示该科室信息 否则按照报表正常形式打印
		if ("day".equals(S_TYPE) && DAY_DEPT.length() > 0) {
			DeptList = STADeptListTool.getInstance().selectNewDeptByCode(DAY_DEPT,
					Operator.getRegion());// ===pangben modify 20110523
		} else {
			if (printType.equals("3")) { // 按照三级科室打印
				// 获取1，2，3级科室
				DeptList = STATool.getInstance().getDeptByLevel(
						new String[] { "1", "2", "3" }, Operator.getRegion());// ===pangben
																				// modify
																				// 20110524
			} else if (printType.equals("4")) { // 按照四级科室打印
				// 获取1，2，3,4级科室
				DeptList = STATool.getInstance().getDeptByLevel(
						new String[] { "1", "2", "3", "4" },
						Operator.getRegion());// ===pangben modify 20110524
			}
		}
		if (DeptList.getErrCode() < 0) {
			return DeptList;
		}
		// 获取数据
		TParm data = new TParm();
		TParm Daily02 = new TParm();
		if ("month".equals(S_TYPE)) {// 如果是月统计 查询数据库 取得该月统计信息
			data = STAIn_02Tool.getInstance().selectSTA_IN_02(STA_DATE,
					Operator.getRegion());// =====pangben modify 20110523
			// =====pangben modify 20110523
			Daily02 = STAIn_02Tool.getInstance().selectSTA_DAILY_02(
					DATA_StaDate, Operator.getRegion());// 查询STA_DAILY_02表信息
														// 为了获取
														// "实有有病人数总计"和"入院人数总计"
		} else if ("day".equals(S_TYPE)) {// 如果是日期段统计 获取table上的数据进行打印
			data = ((TTable) this.getComponent("Table_Read")).getParmValue();
			TParm dailyParm = new TParm();
			dailyParm.setData("DATE_S", DATE_Start.replace("/", ""));
			dailyParm.setData("DATE_E", DATE_End.replace("/", ""));
			// =====pangben modify 20110523 start
			dailyParm.setData("REGION_CODE", Operator.getRegion());
			// =====pangben modify 20110523 stop
			Daily02 = STAIn_02Tool.getInstance().selectSTA_DAILY_02(dailyParm);
		}
		if (data.getErrCode() < 0) {
			return data;
		}
		boolean isOE = false;// 记录科室是否包含门诊科室信息
		int rowCount = 0;// 记录打印数据的行数
		for (int i = 0; i < DeptList.getCount(); i++) {
			isOE = false;
			String d_LEVEL = DeptList.getValue("DEPT_LEVEL", i); // 部门等级
			String d_CODE = DeptList.getValue("DEPT_CODE", i); // 中间部门CODE
			String d_DESC = DeptList.getValue("DEPT_DESC", i); // 中间部门名称
			int subIndex = 0; // 记录根据科室级别要截取CODE的长度
			// 如果是一级科室 code长度为1
			if (d_LEVEL.equals("1")) {
				subIndex = 1;
			}
			// 如果是二级科室 code长度为3
			else if (d_LEVEL.equals("2")) {
				subIndex = 3;
				// d_DESC = " " + d_DESC; //加入前方空格
			}
			// 如果是三级科室 code长度为5
			else if (d_LEVEL.equals("3")) {
				subIndex = 5;
				// d_DESC = "  " + d_DESC; //加入前方空格
			}
			// 如果是四级科室 code长度为7
			else if (d_LEVEL.equals("4")) {
				subIndex = 7;
			}
			// 定义变量 用来累加子部门的数值
			int DATA_02 = 0;
			int DATA_03 = 0;
			int DATA_04 = 0;
			int DATA_05 = 0;
			int DATA_06 = 0;
			int DATA_07 = 0;
			int DATA_08 = 0;
			int DATA_09 = 0;
			int DATA_10 = 0;
			int DATA_11 = 0;
			int DATA_12 = 0;
			int DATA_13 = 0;
			int DATA_14 = 0;
			int DATA_15 = 0;
			int DATA_16 = 0;
			int DATA_17 = 0;
			int DATA_18 = 0;
			double DATA_19 = 0;
			double DATA_20 = 0;
			double DATA_21 = 0;
			double DATA_22 = 0;
			double DATA_23 = 0;
			double DATA_24 = 0;
			int DATA_25 = 0;
			int DATA_26 = 0;
			double DATA_27 = 0;
			double DATA_28 = 0;
			double DATA_29 = 0;
			int DATA_30 = 0;
			double DATA_31 = 0;
			int DATA_32 = 0;
			double DATA_33 = 0;
			double DATA_34 = 0;
			int P_num = 0;// 记录实有病人数
			int inP_num = 0;// 记录入院人数总计
			int deptCount = 0;// 记录每个部门下的子部门，概率方面的数据需要取平均值

			// 循环遍历数据 取出符合条件的部门的数据进行累加
			for (int j = 0; j < data.getCount("STA_DATE"); j++) {
				// 如果部门id截取了指定长度后 等于外层循环中的部门CODE那么就是外层循环的子部门，就进行累加
				String subDept = data.getValue("DEPT_CODE", j).substring(0,
						subIndex);
				// 如果此条数据是外层循环的子科室
				if (subDept.equals(d_CODE)) {
					// 判断该子科室是否是门急诊科室如果不是 那么继续下一次循环
					if (deptOE.get(data.getValue("DEPT_CODE", j)).toString()
							.length() > 0) {
						isOE = true; // 包含门急诊科室 显示在报表上
						DATA_02 += data.getInt("DATA_02", j);
						DATA_03 += data.getInt("DATA_03", j);
						DATA_04 += data.getInt("DATA_04", j);
						DATA_05 += data.getInt("DATA_05", j);
						DATA_06 += data.getInt("DATA_06", j);
						DATA_07 += data.getInt("DATA_07", j);
						DATA_08 += data.getInt("DATA_08", j);
						DATA_09 += data.getInt("DATA_09", j);
						DATA_10 += data.getInt("DATA_10", j);
						DATA_11 += data.getInt("DATA_11", j);
						DATA_12 += data.getInt("DATA_12", j);
						DATA_13 += data.getInt("DATA_13", j);
						DATA_14 += data.getInt("DATA_14", j);
						DATA_15 += data.getInt("DATA_15", j);
						DATA_16 = data.getInt("DATA_16", j);// 门诊实际工作日 不需要累加
						DATA_17 += data.getInt("DATA_17", j);
						DATA_18 += data.getInt("DATA_18", j);
						DATA_19 += data.getDouble("DATA_19", j);
						DATA_20 += data.getDouble("DATA_20", j);
						// DATA_21 += data.getDouble("DATA_21", j);
						// DATA_22 += data.getDouble("DATA_22", j);
						// DATA_23 += data.getDouble("DATA_23", j);
						// DATA_24 += data.getDouble("DATA_24", j);
						DATA_25 += data.getInt("DATA_25", j);
						DATA_26 += data.getInt("DATA_26", j);
						// DATA_27 += data.getDouble("DATA_27", j);
						// DATA_28 += data.getDouble("DATA_28", j);
						// DATA_29 += data.getDouble("DATA_29", j);
						DATA_30 += data.getInt("DATA_30", j);
						DATA_31 += data.getDouble("DATA_31", j);
						DATA_32 += data.getInt("DATA_32", j);
						DATA_33 += data.getDouble("DATA_33", j);
						DATA_34 += data.getDouble("DATA_34", j);
						P_num += data.getDouble("P_NUM", j);
						inP_num += data.getDouble("INP_NUM", j);
						deptCount++; // 累计共汇总的多少个4级部门的数据
					}
				}
			}
			for (int j = 0; j < Daily02.getCount(); j++) {
				if (Daily02.getValue("DEPT_CODE", j).substring(0, subIndex)
						.equals(d_CODE)) {
					P_num += Daily02.getInt("DATA_16", j);// 实有有病人数总计
					inP_num += Daily02.getInt("DATA_08", j);// 入院人数总计
				}
			}

			if (DATA_15 != 0) {
				DATA_21 = (double) DATA_02 / (double) DATA_15;
			}
			// 门急诊诊次占总诊次百分比
			if (DATA_02 != 0) {
				DATA_24 = (double) DATA_03 / (double) DATA_02 * 100;
			}
			if (DATA_25 != 0) {
				DATA_27 = (double) (DATA_25 - DATA_26) / (double) DATA_25 * 100;// 急诊死亡率
				DATA_28 = (double) DATA_26 / (double) DATA_25 * 100;// 急诊抢救成功率
			}
			if (DATA_09 != 0) {
				DATA_29 = DATA_10 / DATA_09;// 观察室死亡率
			}
			// 每床与每日门急诊诊次比
			if (P_num != 0) {
				DATA_22 = (double) DATA_03 / (double) P_num;
			}
			// 每百门急诊入院人次
			if (DATA_03 != 0) {
				DATA_23 = (double) inP_num / (double) ((double) DATA_03 / 100);
			}
			// 如果科室不是（或者不包含）门急诊科室 那么不显示在报表中
			if (isOE) {
				printData.addData("DEPT_DESC", d_DESC);
				printData.addData("DATA_02", DATA_02 == 0 ? "" : DATA_02);
				printData.addData("DATA_03", DATA_03 == 0 ? "" : DATA_03);
				printData.addData("DATA_04", DATA_04 == 0 ? "" : DATA_04);
				printData.addData("DATA_05", DATA_05 == 0 ? "" : DATA_05);
				printData.addData("DATA_06", DATA_06 == 0 ? "" : DATA_06);
				printData.addData("DATA_07", DATA_07 == 0 ? "" : DATA_07);
				printData.addData("DATA_08", DATA_08 == 0 ? "" : DATA_08);
				printData.addData("DATA_09", DATA_09 == 0 ? "" : DATA_09);
				printData.addData("DATA_10", DATA_10 == 0 ? "" : DATA_10);
				printData.addData("DATA_11", DATA_11 == 0 ? "" : DATA_11);
				printData.addData("DATA_12", DATA_12 == 0 ? "" : DATA_12);
				printData.addData("DATA_13", DATA_13 == 0 ? "" : DATA_13);
				printData.addData("DATA_14", DATA_14 == 0 ? "" : DATA_14);
				printData.addData("DATA_15", DATA_15 == 0 ? "" : DATA_15);
				printData.addData("DATA_16", DATA_16 == 0 ? "" : DATA_16);
				printData.addData("DATA_17", DATA_17 == 0 ? "" : DATA_17);
				printData.addData("DATA_18", DATA_18 == 0 ? "" : DATA_18);
				printData.addData("DATA_19",
						DATA_19 == 0 ? "" : df.format(DATA_19));
				printData.addData("DATA_20",
						DATA_20 == 0 ? "" : df.format(DATA_20));
				printData.addData("DATA_21",
						DATA_21 == 0 ? "" : df.format(DATA_21));
				printData.addData("DATA_22",
						DATA_22 == 0 ? "" : df.format(DATA_22));
				printData.addData("DATA_23",
						DATA_23 == 0 ? "" : df.format(DATA_23));
				printData.addData("DATA_24",
						DATA_24 == 0 ? "" : df.format(DATA_24));
				printData.addData("DATA_25", DATA_25 == 0 ? "" : DATA_25);
				printData.addData("DATA_26", DATA_26 == 0 ? "" : DATA_26);
				printData.addData("DATA_27",
						DATA_27 == 0 ? "" : df.format(DATA_27));
				printData.addData("DATA_28",
						DATA_28 == 0 ? "" : df.format(DATA_28));
				printData.addData("DATA_29",
						DATA_29 == 0 ? "" : df.format(DATA_29));
				printData.addData("DATA_30", DATA_30 == 0 ? "" : DATA_30);
				printData.addData("DATA_31", DATA_31 == 0 ? "" : DATA_31);
				printData.addData("DATA_32", DATA_32 == 0 ? "" : DATA_32);
				printData.addData("DATA_33", DATA_33 == 0 ? "" : DATA_33);
				printData.addData("DATA_34", DATA_34 == 0 ? "" : DATA_34);
				rowCount++;
			}
		}
		printData.setCount(rowCount);
		printData.addData("SYSTEM", "COLUMNS", "DEPT_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DATA_02");
		printData.addData("SYSTEM", "COLUMNS", "DATA_03");
		printData.addData("SYSTEM", "COLUMNS", "DATA_04");
		printData.addData("SYSTEM", "COLUMNS", "DATA_05");
		printData.addData("SYSTEM", "COLUMNS", "DATA_06");
		printData.addData("SYSTEM", "COLUMNS", "DATA_07");
		printData.addData("SYSTEM", "COLUMNS", "DATA_08");
		printData.addData("SYSTEM", "COLUMNS", "DATA_09");
		printData.addData("SYSTEM", "COLUMNS", "DATA_10");
		printData.addData("SYSTEM", "COLUMNS", "DATA_11");
		printData.addData("SYSTEM", "COLUMNS", "DATA_12");
		printData.addData("SYSTEM", "COLUMNS", "DATA_13");
		printData.addData("SYSTEM", "COLUMNS", "DATA_14");
		printData.addData("SYSTEM", "COLUMNS", "DATA_15");
		printData.addData("SYSTEM", "COLUMNS", "DATA_16");
		printData.addData("SYSTEM", "COLUMNS", "DATA_17");
		printData.addData("SYSTEM", "COLUMNS", "DATA_18");
		printData.addData("SYSTEM", "COLUMNS", "DATA_19");
		printData.addData("SYSTEM", "COLUMNS", "DATA_20");
		printData.addData("SYSTEM", "COLUMNS", "DATA_21");
		printData.addData("SYSTEM", "COLUMNS", "DATA_22");
		printData.addData("SYSTEM", "COLUMNS", "DATA_23");
		printData.addData("SYSTEM", "COLUMNS", "DATA_24");
		printData.addData("SYSTEM", "COLUMNS", "DATA_25");
		printData.addData("SYSTEM", "COLUMNS", "DATA_26");
		printData.addData("SYSTEM", "COLUMNS", "DATA_27");
		printData.addData("SYSTEM", "COLUMNS", "DATA_28");
		printData.addData("SYSTEM", "COLUMNS", "DATA_29");
		printData.addData("SYSTEM", "COLUMNS", "DATA_30");
		printData.addData("SYSTEM", "COLUMNS", "DATA_31");
		printData.addData("SYSTEM", "COLUMNS", "DATA_32");
		printData.addData("SYSTEM", "COLUMNS", "DATA_33");
		printData.addData("SYSTEM", "COLUMNS", "DATA_34");
		return printData;

	}

	/**
	 * 表格数据绑定(月报表)
	 */
	private void gridBind(String STADATE, String deptCode) {
		TTable table = (TTable) this.getComponent("Table");
		String sql = STASQLTool.getInstance().getSTA_IN_02(STADATE,
				Operator.getRegion(), deptCode);// =====pangben modify 20110523
												// 添加区域参数
		// ===========pangben modify 20110523 添加参数
		TParm Daily02 = STAIn_02Tool.getInstance().selectSTA_DAILY_02(STADATE,
				Operator.getRegion()); // 查询STA_DAILY_02表信息
		// 查询所有四级部门 门急诊部门
		TParm deptList = STATool.getInstance().getDeptByLevel(
				new String[] { "4" }, "OE", Operator.getRegion());// ===========pangben
																	// modify
																	// 20110523
		int P_num = 0; // 记录实有病人数
		int inP_num = 0; // 记录入院人数总计
		// 循环统计STA_DAILY_02表相关字段的数据
		for (int j = 0; j < Daily02.getCount(); j++) {
//			if (Daily02.getValue("DEPT_CODE", j).equals(deptCode)) {
				P_num += Daily02.getInt("DATA_16", j); // 实有有病人数总计
//				this.messageBox(Daily02.getInt("------DATA_08", j)+"");
				inP_num += Daily02.getInt("DATA_08", j); // 入院人数总计
//			}
		}
		// =======pangben modify 20110526 start
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int count=result.getCount();
		if (result.getCount() <= 0) {
			table.removeRowAll();
			return;
		}
		int DATA_02 = 0;
		int DATA_03 = 0;
		int DATA_04 = 0;
		int DATA_05 = 0;
		int DATA_06 = 0;
		int DATA_07 = 0;
		int DATA_08 = 0;
		int DATA_09 = 0;
		int DATA_10 = 0;
		int DATA_11 = 0;
		int DATA_12 = 0;
		int DATA_13 = 0;
		int DATA_14 = 0;
		int DATA_15 = 0;
		int DATA_16 = 0;
		int DATA_17 = 0;
		int DATA_18 = 0;
		double DATA_19 = 0;
		double DATA_20 = 0;
		double DATA_21 = 0;
		double DATA_22 = 0;
		double DATA_23 = 0;
		double DATA_24 = 0;
		int DATA_25 = 0;
		int DATA_26 = 0;
		double DATA_27 = 0;
		double DATA_28 = 0;
		double DATA_29 = 0;
		int DATA_30 = 0;
		double DATA_31 = 0;
		int DATA_32 = 0;
		double DATA_33 = 0;
		double DATA_34 = 0;
		for (int i = 0; i < result.getCount(); i++) {
			DATA_03 += result.getInt("DATA_03", i);
			DATA_04 += result.getInt("DATA_04", i);
			DATA_05 += result.getInt("DATA_05", i);
			DATA_06 += result.getInt("DATA_06", i);
			DATA_07 += result.getInt("DATA_07", i);
			DATA_08 += result.getInt("DATA_08", i);
			DATA_09 += result.getInt("DATA_09", i);
			DATA_10 += result.getInt("DATA_10", i);
			DATA_11 += result.getInt("DATA_11", i);
			DATA_12 += result.getInt("DATA_12", i);
			DATA_13 += result.getInt("DATA_13", i);
			DATA_14 += result.getInt("DATA_14", i);
			DATA_15 += result.getInt("DATA_15", i);
			DATA_16 = result.getInt("DATA_16", i);
			DATA_17 += result.getInt("DATA_17", i);
			DATA_18 += result.getInt("DATA_18", i);
			DATA_19 += result.getDouble("DATA_19", i);
			DATA_20 += result.getDouble("DATA_20", i);
			DATA_22 += result.getDouble("DATA_22", i);
			DATA_23 += result.getDouble("DATA_23", i);
			DATA_25 += result.getInt("DATA_25", i);
			DATA_26 += result.getInt("DATA_26", i);
			DATA_30 += result.getInt("DATA_30", i);
			DATA_31 += result.getDouble("DATA_31", i);
			DATA_32 += result.getInt("DATA_32", i);
			DATA_33 += result.getDouble("DATA_33", i);
		}
		if (DATA_25 != 0) {
			DATA_27 = (DATA_25 - DATA_26) / DATA_25 * 100; // 急诊死亡率
			DATA_28 = DATA_26 / DATA_25 * 100; // 急诊抢救成功率
		}
		if (DATA_09 != 0) {
			DATA_29 = DATA_10 / DATA_09; // 观察室死亡率
		}// 总计
		DATA_02 = DATA_03 + DATA_07;
		// 医师每小时诊疗人次
		if (DATA_15 != 0) {
			DATA_21 = (double) DATA_02 / (double) DATA_15;
		}
		// 每床与每日门急诊诊次比
		if (P_num != 0) {
			DATA_22 = (double) DATA_03 / (double) P_num;
		}
		// 每百门急诊入院人次
		if (DATA_03 != 0) {
			DATA_23 = (double) inP_num / (double) ((double) DATA_03 / 100);
		}
		// 门急诊诊次占总诊次百分比
		if (DATA_02 != 0) {
			DATA_24 = (double) DATA_03 / (double) DATA_02 * 100;
		}
		if (DATA_13 != 0) {
			DATA_34 = (double) DATA_11 / (double) DATA_13;
		}
		result.addData("DEPT_CODE", "合计:");
		result.addData("DATA_02", DATA_02);
		result.addData("DATA_03", DATA_03);
		result.addData("DATA_04", DATA_04);
		result.addData("DATA_05", DATA_05);
		result.addData("DATA_06", DATA_06);
		result.addData("DATA_07", DATA_07);
		result.addData("DATA_08", DATA_08);
		result.addData("DATA_09", DATA_09);
		result.addData("DATA_10", DATA_10);
		result.addData("DATA_11", DATA_11);
		result.addData("DATA_12", DATA_12);
		result.addData("DATA_13", DATA_13);
		result.addData("DATA_14", DATA_14);
		result.addData("DATA_15", DATA_15);
		result.addData("DATA_16", DATA_16);
		result.addData("DATA_17", DATA_17);
		result.addData("DATA_18", DATA_18);
		result.addData("DATA_19", DATA_19);
		result.addData("DATA_20", DATA_20);
		result.addData("DATA_21", DATA_21);
		result.addData("DATA_22", DATA_22);
		result.addData("DATA_23", DATA_23);
		result.addData("DATA_24", DATA_24);
		result.addData("DATA_25", DATA_25);
		result.addData("DATA_26", DATA_26);
		result.addData("DATA_27", DATA_27);
		result.addData("DATA_28", DATA_28);
		result.addData("DATA_29", DATA_29);
		result.addData("DATA_30", DATA_30);
		result.addData("DATA_31", DATA_31);
		result.addData("DATA_32", DATA_32);
		result.addData("DATA_33", DATA_33);
		result.addData("DATA_34", DATA_34);
		result.setCount(count+1);
		table.setParmValue(result);
		// =======pangben modify 20110526 stop
		DATA_StaDate = STADATE;
	}

	/**
	 * 表格数据绑定(日期段报表)
	 */
	private void gridBind(TParm result) {
		TTable table = (TTable) this.getComponent("Table_Read");
		table.setParmValue(result);
		// 记录查询日期段 用于报表打印
		DATE_Start = this.getText("DATE_S");
		DATE_End = this.getText("DATE_E");
	}

	/**
	 * 检查是否可以生成数据 检核是否已经该月份的数据 数据是否已经提交 true:可以生成 false:不可以生成
	 * 
	 * @param STADATE
	 *            String
	 * @return boolean
	 */
	private boolean canGeneration(String STADATE) {
		boolean can = false;
		// 检核数据状态
		int reFlg = STATool.getInstance().checkCONFIRM_FLG("STA_IN_02",
				STADATE, Operator.getRegion());// =========pangben modify
												// 20110523
		// 数据已经提交
		if (reFlg == 2) {
			this.messageBox_("数据已经提交，不能重新生成！");
			can = false;
			STA_CONFIRM_FLG = true; // 标识数据已经提交
		}
		// 数据存在但没有提交
		else if (reFlg == 1) {
			switch (this
					.messageBox("提示信息", "数据已存在，是否重新生成？", this.YES_NO_OPTION)) {
			case 0: // 生成
				can = true;
				break;
			case 1: // 不生成
				can = false;
				break;
			}
		} else if (reFlg == 0) { // 没有数据
			can = true;
		} else if (reFlg == -1) { // 数据检核错误
			can = false;
		}
		if (STA_CONFIRM_FLG == true)
			this.setValue("Submit", true);
		else
			this.setValue("Submit", false);
		return can;
	}

	/**
	 * 月统计Radio 选择事件
	 */
	public void onR_MONTH_Click() {
		this.setDateVisble("month");
	}

	/**
	 * 日期段统计Radio 选择事件
	 */
	public void onR_DAYS_Click() {
		this.setDateVisble("days");
	}
}
