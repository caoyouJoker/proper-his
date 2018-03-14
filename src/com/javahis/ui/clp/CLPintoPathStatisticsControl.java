package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.Operator;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTable;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import jdo.clp.intoPathStatisticsTool;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.util.Map;
import java.util.Vector;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.util.ExportExcelUtil;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.base.TComboBoxModel;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author not attributable
 * @version 1.0
 */
public class CLPintoPathStatisticsControl extends TControl {

	// 开始时间
	private TTextFormat start_date;
	// 结束时间
	private TTextFormat end_date;
	// 表格
	private TTable table;
	private intoPathStatisticsTool jdo = null;

	// 定义一个日期的显示格式
	private DateFormat format = new SimpleDateFormat("yyyy/MM/dd ");

	public CLPintoPathStatisticsControl() {
		jdo = intoPathStatisticsTool.getNewInstance();
	}

	public void onInit() {
		// JDO类一般用于操作数据库,这个类封装了对数据库操作的大多数方法
		super.init();
		onInitPage();
	}

	/**
	 * 初始化页面
	 */
	public void onInitPage() {
		table = getTable("TABLE");
		Timestamp date = StringTool.getTimestamp(new Date());
		// 时间间隔为1周
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table = (TTable) this.getComponent("TABLE");
		start_date = (TTextFormat) this.getComponent("START_DATE");
		end_date = (TTextFormat) this.getComponent("END_DATE");
	}

	/**
	 * 打印报表
	 */
	public void onPrint() {
		TParm data = new TParm();
		// 需要打印的Table
		TTable table = (TTable) this.getComponent("TABLE");
		// 当列表中不存在任何数据时,提示
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		// 表头
		data.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "所有院区")
				+ "临床路径统计表");
		// 表头数据
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate)) {
			startDate = startDate.substring(0, (startDate.length() - 2));
		}
		if (this.checkNullAndEmpty(endDate)) {
			endDate = endDate.substring(0, (endDate.length() - 2));
		}
		data.setData("S_DATE", "TEXT", startDate);
		data.setData("E_DATE", "TEXT", endDate);
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("REGION_CHN_ABN", tableParm
					.getValue("REGION_CHN_ABN", i));
			// wangzhilei 12-07-20 添加
			parm.addData("DEPT_CHN_DESC", tableParm
					.getValue("DEPT_CHN_DESC", i));
			parm.addData("STATION_DESC", tableParm.getValue("STATION_DESC", i));
			parm.addData("NO1", tableParm.getValue("NO1", i));
			parm.addData("NO2", tableParm.getValue("NO2", i));
			parm.addData("IN_PRE", tableParm.getValue("IN_PRE", i));
			parm.addData("NO3", tableParm.getValue("NO3", i));
			parm.addData("OUT_PRE", tableParm.getValue("OUT_PRE", i));
		}
		parm.setCount(parm.getCount("DEPT_CHN_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "REGION_CHN_ABN");
		parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "STATION_DESC");
		parm.addData("SYSTEM", "COLUMNS", "NO1");
		parm.addData("SYSTEM", "COLUMNS", "NO2");
		parm.addData("SYSTEM", "COLUMNS", "IN_PRE");
		parm.addData("SYSTEM", "COLUMNS", "NO3");
		parm.addData("SYSTEM", "COLUMNS", "OUT_PRE");
		// 把表格数据添加进要打印的parm
		data.setData("TABLE", parm.getData());
		data.setData("NAME", "TEXT", "制作人：" + Operator.getName());
		// 调用打印方法,报表路径
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\CLP\\CLPintoPathStatistics1.jhw", data);// wangzhilei 12-07-22 修改

	}

	public void onQuery() {
		if (!checkData()) {
			return;
		}
		TTable table = (TTable) this.getComponent("TABLE");
		
		TParm parm = new TParm();
		String startdate = this.getValueString("START_DATE");
		startdate = startdate.substring(0, 4) + startdate.substring(5, 7)
				+ startdate.substring(8, 10) + startdate.substring(11, 13)
				+ startdate.substring(14, 16) + startdate.substring(17, 19);
		String enddate = this.getValueString("END_DATE");
		enddate = enddate.substring(0, 4) + enddate.substring(5, 7)
				+ enddate.substring(8, 10) + enddate.substring(11, 13)
				+ enddate.substring(14, 16) + enddate.substring(17, 19);
		parm.setData("START_DATE", startdate);
		parm.setData("END_DATE", enddate);
		parm.setData("REGION_CODE", Operator.getRegion());
		TParm result1 = intoPathStatisticsTool.getNewInstance().onQuerySum(parm);
		// 判断错误值
		if (result1 == null || result1.getCount() <= 0) {
			this.messageBox("查无数据");
			table.removeRowAll();
			return;
		}
		TParm parmTemp=null;
		TParm parmS=null;
		for (int i = 0; i < result1.getCount(); i++) {
			
			parmTemp=result1.getRow(i);
			parmTemp.setData("START_DATE", startdate);
			parmTemp.setData("END_DATE", enddate);
			parmS=intoPathStatisticsTool.getNewInstance().onQueryNo2(parmTemp);
			if (parmS.getErrCode()<0) {
				this.messageBox("查无数据");
				table.removeRowAll();
				return;
			}
			result1.setData("NO2",i, parmS.getValue("CASE_NO",0));
			parmS=intoPathStatisticsTool.getNewInstance().onQueryNo3(parmTemp);
			if (parmS.getErrCode()<0) {
				this.messageBox("查无数据");
				table.removeRowAll();
				return;
			}
			result1.setData("NO3",i, parmS.getValue("CASE_NO",0));
		}
		// 总计需要变量
		double inHospCount = 0;
		// 进入路径人数
		double inPathCount = 0;
		// 溢出路径人数
		double outPathCout = 0;
		int row = result1.getCount();
		TParm result2 = new TParm();
		for (int i = 0; i < row; i++) {
			// wangzhilei 12-07-20 添加
			result2.addData("REGION_CHN_ABN", result1.getValue("REGION_CHN_ABN", i));
			// wangzhilei 12-07-20 添加
			result2.addData("DEPT_CHN_DESC", result1.getValue("DEPT_CHN_DESC",
					i));
			result2
					.addData("STATION_DESC", result1
							.getValue("STATION_DESC", i));
			result2.addData("NO1", result1.getValue("NO1", i));
			result2.addData("NO2", result1.getValue("NO2", i));
			result2.addData("NO3", result1.getValue("NO3", i));
			String b = result1.getValue("NO1", i);
			String c = result1.getValue("NO2", i);
			String d = result1.getValue("NO3", i);
			double out_no = Double.parseDouble(b);
			inHospCount += out_no;
			double clp_no = Double.parseDouble(c);
			inPathCount += clp_no;
			double out_clp_no = Double.parseDouble(d);
			outPathCout += out_clp_no;
			double in = 0;
			Double out = 0d;
			String out_pre = "";
			String in_pre = "";
			if (out_no <= 0) {
				in_pre = in + "%" + "";
				result2.addData("IN_PRE", in_pre);
			} else {
				in = clp_no / out_no;
				// wangzhilei 12-07-22 添加
				BigDecimal bdi = new BigDecimal(in);
				bdi = bdi.setScale(4, 4);
				BigDecimal parmMuli = new BigDecimal(100);
				parmMuli = parmMuli.setScale(0);
				in = bdi.multiply(parmMuli).doubleValue();
				// wangzhilei 12-07-22 添加
				in_pre = in + "%" + "";
				result2.addData("IN_PRE", in_pre);
			}
			if (clp_no <= 0) {
				out_pre = out + "%" + "";
				result2.addData("OUT_PRE", out_pre);
			} else {
				out = out_clp_no / clp_no;
				// wangzhilei 12-07-22 添加
				BigDecimal bd = new BigDecimal(out);
				bd = bd.setScale(4, 4);
				BigDecimal parmMulo = new BigDecimal(100);
				parmMulo = parmMulo.setScale(0);
				out = bd.multiply(parmMulo).doubleValue();
				// wangzhilei 12-07-22 添加
				out_pre = out + "%" + "";
				result2.addData("OUT_PRE", out_pre);
			}
		}
		// 处理总计
		result2.addData("REGION_CHN_ABN", "总计:");
		result2.addData("DEPT_CHN_DESC", "");
		result2.addData("STATION_DESC", "");
		result2.addData("NO1", (int) inHospCount);
		result2.addData("NO2", (int) inPathCount);
		result2.addData("NO3", (int) outPathCout);
		double out_no = inHospCount;
		inHospCount += out_no;
		double clp_no = inPathCount;
		inPathCount += clp_no;
		double out_clp_no = outPathCout;
		outPathCout += out_clp_no;
		double in = 0.00;
		double out = 0.00;
		String out_pre = "";
		String in_pre = "";
		if (out_no <= 0) {
			in_pre = in + "%" ;
			result2.addData("IN_PRE", in_pre);
		} else {
			in = clp_no / out_no;
			in_pre = StringTool.round(in * 100, 2) + "%" ;
			result2.addData("IN_PRE", in_pre);
		}
		if (clp_no <= 0) {
			out_pre = out + "%" + "";
			result2.addData("OUT_PRE", out_pre);
		} else {
			out = out_clp_no / clp_no;
			out_pre = StringTool.round(out * 100 ,2)+ "%" ;
			result2.addData("OUT_PRE", out_pre);
		}
		result2.setCount(row+1);
		// 处理总计end
		table.setParmValue(result2);
	}

	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		onInitPage();
		table.removeRowAll();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "临床路径统计表");
		}
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	private boolean checkData() {
		String start = this.getValueString("START_DATE");
		if (start == null || start.length() <= 0) {
			this.messageBox("开始时间不能为空");
			return false;
		}
		String end = this.getValueString("END_DATE");
		if (end == null || end.length() <= 0) {
			this.messageBox("结束时间不能为空");
			return false;
		}

		return true;
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}
}
