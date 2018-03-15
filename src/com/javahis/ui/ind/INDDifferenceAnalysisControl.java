package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import jdo.spc.StringUtils;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 健康检查差异分析报表
 * </p>
 * 
 * <p>
 * Description: 健康检查差异分析报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liuyl 2017.02.21
 * @version 1.0
 */

public class INDDifferenceAnalysisControl extends TControl {

	TTable table;
	TParm newdata;

	public INDDifferenceAnalysisControl() {
	}

	// 初始化页面
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TTable");
		initPage();
	}

	/**
	 * 
	 * 初始化控件默认值
	 */
	public void initPage() {
		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 查询时间
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp DateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("DATE", DateTimestamp.toString().substring(0, 4) + "/"
				+ DateTimestamp.toString().substring(5, 7) + "/26 00:00:00");

		// 设置区域
		setValue("REGION_CODE", "H01");

	}

	/**
	 * 查询
	 * 
	 * @throws ParseException
	 */
	public void onQuery() throws ParseException {
		TTable table = (TTable) getComponent("TTable");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String Date = getValueString("DATE");
		//System.out.println("date::::::::::::::::::::" + Date);
		Date date = sdf.parse(Date);

		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, -1);
		String Date1 = sdf.format(calendar.getTime());

		Calendar calendar1 = Calendar.getInstance();
		calendar1.setTime(date);
		calendar1.add(Calendar.SECOND, -1);
		String Date2 = sdf.format(calendar1.getTime());

		Calendar calendar2 = Calendar.getInstance();
		calendar2.setTime(date);
		calendar2.add(Calendar.SECOND, -1);
		calendar2.add(Calendar.MONTH, 1);
		String Date3 = sdf.format(calendar2.getTime());

		Calendar calendar3 = Calendar.getInstance();
		calendar3.setTime(date);
		calendar3.add(Calendar.MONTH, 1);
		String Date4 = sdf.format(calendar3.getTime());

		Date = Date.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		Date1 = Date1.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		Date2 = Date2.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		Date3 = Date3.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		Date4 = Date4.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");

		String opt_date_in = "";
		String opt_date_out = "";

		opt_date_in += "  BILL_DATE BETWEEN TO_DATE ('" + Date1
				+ "','YYYYMMDDHH24MISS') " + "AND TO_DATE ('" + Date2
				+ "','YYYYMMDDHH24MISS') " + "AND REQUEST_NO IS NOT NULL)) "
				+ "AND DISPENSE_DATE >= TO_DATE ('" + Date
				+ "', 'YYYYMMDDHH24MISS')";
		opt_date_out += " BILL_DATE BETWEEN TO_DATE ('" + Date
				+ "','YYYYMMDDHH24MISS') " + "AND TO_DATE ('" + Date3
				+ "','YYYYMMDDHH24MISS') " + "AND REQUEST_NO IS NOT NULL))"
				+ "AND DISPENSE_DATE >= TO_DATE ('" + Date4
				+ "', 'YYYYMMDDHH24MISS')";

		// String sql =
		// "SELECT DISTINCT D.ORDER_CODE,E.ORDER_DESC,A.QTY1,A.PRICE1,"
		// +
		// "A.DISPENSE_DATE AS DISPENSE_DATE1,A.REQUEST_DATE AS REQUEST_DATE1,"
		// + "B.QTY2,B.PRICE2,B.DISPENSE_DATE AS DISPENSE_DATE2,"
		// + "B.REQUEST_DATE AS REQUEST_DATE2,"
		// + "(B.QTY2-A.QTY1) AS ANA_QTY,"
		// + "F.UNIT_CHN_DESC,"
		// + "(B.PRICE2-A.PRICE1)AS ANA_PRICE "
		// + "FROM IND_DISPENSED D, SYS_FEE E,SYS_UNIT F, "
		// + "(SELECT D.ORDER_CODE AS ORDER_CODE1,E.ORDER_DESC AS ORDER_DESC1,"
		// + "M.DISPENSE_DATE,"
		// + "M.REQUEST_DATE, "
		// + "NVL(SUM (D.ACTUAL_QTY),0) AS QTY1,"
		// + "NVL(SUM (D.RETAIL_PRICE * D.ACTUAL_QTY),0) AS PRICE1 "
		// + "FROM IND_DISPENSEM M,IND_DISPENSED D, SYS_FEE E "
		// + "WHERE M.DISPENSE_NO = D.DISPENSE_NO "
		// + "AND M.REQTYPE_CODE = 'EXM' "
		// + "AND D.ORDER_CODE = E.ORDER_CODE "
		// + "AND (M.REQUEST_NO IN "
		// + "(SELECT DISTINCT REQUEST_NO "
		// + "FROM HRM_ORDER "
		// + "WHERE "
		// + opt_date_in
		// + "GROUP BY D.ORDER_CODE,"
		// + "M.DISPENSE_DATE,"
		// + "M.REQUEST_DATE,"
		// + "M.DISPENSE_DATE,"
		// + "E.ORDER_DESC) A "
		// + "FULL JOIN "
		// + "(SELECT D.ORDER_CODE AS ORDER_CODE2,E.ORDER_DESC AS ORDER_DESC2,"
		// + "M.DISPENSE_DATE,"
		// + "M.REQUEST_DATE, "
		// + "NVL(SUM (D.ACTUAL_QTY),0) AS QTY2,"
		// + "NVL(SUM (D.RETAIL_PRICE * D.ACTUAL_QTY),0) AS PRICE2 "
		// + "FROM IND_DISPENSEM M,IND_DISPENSED D, SYS_FEE E "
		// + "WHERE M.DISPENSE_NO = D.DISPENSE_NO "
		// + "AND M.REQTYPE_CODE = 'EXM' "
		// + "AND D.ORDER_CODE = E.ORDER_CODE "
		// + "AND (M.REQUEST_NO IN "
		// + "(SELECT DISTINCT REQUEST_NO "
		// + "FROM HRM_ORDER "
		// + "WHERE "
		// + opt_date_out
		// + "GROUP BY D.ORDER_CODE,"
		// + "M.DISPENSE_DATE,"
		// + "M.REQUEST_DATE,"
		// + "M.DISPENSE_DATE,"
		// + "E.ORDER_DESC) B "
		// + "ON A.ORDER_CODE1=B.ORDER_CODE2 "
		// + "WHERE D.ORDER_CODE=E.ORDER_CODE "
		// + "AND E.UNIT_CODE = F.UNIT_CODE "
		// + "AND (D.ORDER_CODE = A.ORDER_CODE1 OR D.ORDER_CODE=B.ORDER_CODE2)";
		//
		// TParm newdata = new TParm(TJDODBTool.getInstance().select(sql));
		//

		String sql_1 = "SELECT DISTINCT D.ORDER_CODE AS ORDER_CODE1,E.ORDER_DESC AS ORDER_DESC1,"
				+ "M.DISPENSE_DATE AS DISPENSE_DATE1,"
				+ "M.REQUEST_DATE AS REQUEST_DATE1, "
				+ "F.UNIT_CHN_DESC AS UNIT_CHN_DESC1,"
				+ "SUM (D.ACTUAL_QTY) AS QTY1,"
				+ "SUM (D.RETAIL_PRICE * D.ACTUAL_QTY) AS PRICE1 "
				+ "FROM IND_DISPENSEM M,IND_DISPENSED D, SYS_FEE E,SYS_UNIT F "
				+ "WHERE M.DISPENSE_NO = D.DISPENSE_NO "
				+ "AND E.UNIT_CODE = F.UNIT_CODE "
				+ "AND M.REQTYPE_CODE = 'EXM' "
				+ "AND D.ORDER_CODE = E.ORDER_CODE "
				+ "AND (M.REQUEST_NO IN "
				+ "(SELECT DISTINCT REQUEST_NO "
				+ "FROM HRM_ORDER "
				+ "WHERE "
				+ opt_date_in
				+ "GROUP BY D.ORDER_CODE,"
				+ "M.DISPENSE_DATE,"
				+ "M.REQUEST_DATE,"
				+ "M.DISPENSE_DATE,"
				+ "E.ORDER_DESC,"
				+ "F.UNIT_CHN_DESC ";
		System.out.println("sql1++++" + sql_1);

		String sql_2 = "SELECT DISTINCT "
				+ "D.ORDER_CODE AS ORDER_CODE2,E.ORDER_DESC AS ORDER_DESC2,"
				+ "M.DISPENSE_DATE AS DISPENSE_DATE2,"
				+ "M.REQUEST_DATE AS REQUEST_DATE2, "
				+ "F.UNIT_CHN_DESC AS UNIT_CHN_DESC2,"
				+ "SUM (D.ACTUAL_QTY) AS QTY2,"
				+ "SUM (D.RETAIL_PRICE * D.ACTUAL_QTY) AS PRICE2 "
				+ "FROM IND_DISPENSEM M,IND_DISPENSED D, SYS_FEE E,SYS_UNIT F "
				+ "WHERE M.DISPENSE_NO = D.DISPENSE_NO "
				+ "AND E.UNIT_CODE = F.UNIT_CODE "
				+ "AND M.REQTYPE_CODE = 'EXM' "
				+ "AND D.ORDER_CODE = E.ORDER_CODE " + "AND (M.REQUEST_NO IN "
				+ "(SELECT DISTINCT REQUEST_NO " + "FROM HRM_ORDER " + "WHERE "
				+ opt_date_out + "GROUP BY D.ORDER_CODE," + "M.DISPENSE_DATE,"
				+ "M.REQUEST_DATE," + "M.DISPENSE_DATE," + "E.ORDER_DESC, "
				+ "F.UNIT_CHN_DESC ";
		System.out.println("sql2++++" + sql_2);

		TParm parm_1 = new TParm(TJDODBTool.getInstance().select(sql_1));
		TParm parm_2 = new TParm(TJDODBTool.getInstance().select(sql_2));
		System.out.println("tttttttttttt::::::::" + parm_1);
		System.out.println("dddddddddddd::::::::" + parm_2);

		if (parm_1 == null & parm_2 == null) {
			this.messageBox("没有查询数据");
			return;
		} else {
			String order_code = "";
			String order_desc = "";
			String unit_desc = "";
			String request_date = "";
			String dispense_date = "";
			String qty = "";
			String price = "";

			TParm parm = new TParm();
			for (int i = 0; i < parm_2.getCount("ORDER_CODE2"); i++) {
				boolean flg = false;
				order_code = parm_2.getValue("ORDER_CODE2", i);
				order_desc = parm_2.getValue("ORDER_DESC2", i);
				unit_desc = parm_2.getValue("UNIT_CHN_DESC2", i);
				request_date = parm_2.getValue("REQUEST_DATE2", i);
				dispense_date = parm_2.getValue("DISPENSE_DATE2", i);
				qty = parm_2.getValue("QTY2", i);
				price = parm_2.getValue("PRICE2", i);
				for (int j = 0; j < parm_1.getCount("ORDER_CODE1"); j++) {
					if (order_code.equals(parm_1.getValue("ORDER_CODE1", j))) {
						parm.addData("ORDER_CODE", order_code);
						parm.addData("ORDER_DESC", order_desc);
						parm.addData("UNIT_CHN_DESC", unit_desc);
						parm.addData("REQUEST_DATE1",
								parm_1.getValue("REQUEST_DATE1", j));
						parm.addData("DISPENSE_DATE1",
								parm_1.getValue("DISPENSE_DATE1", j));
						parm.addData("REQUEST_DATE2", request_date);
						parm.addData("DISPENSE_DATE2", dispense_date);
						parm.addData("QTY1", parm_1.getValue("QTY1", j));
						parm.addData("PRICE1", parm_1.getValue("PRICE1", j));
						parm.addData("QTY2", qty);
						parm.addData("PRICE2", price);
						parm.addData(
								"ANA_QTY",
								StringTool.round(parm_2.getDouble("QTY2", i)
										- parm_1.getDouble("QTY1", j), 0));
						parm.addData("ANA_PRICE", StringTool.round((parm_2
								.getDouble("PRICE2", i) - parm_1.getDouble(
								"PRICE1", j)), 4));
						flg = true;
						break;
					}
				}
				if (!flg) {
					parm.addData("ORDER_CODE", order_code);
					parm.addData("ORDER_DESC", order_desc);
					parm.addData("UNIT_CHN_DESC", unit_desc);
					parm.addData("REQUEST_DATE1", "");
					parm.addData("DISPENSE_DATE1", "");
					parm.addData("REQUEST_DATE2", request_date);
					parm.addData("DISPENSE_DATE2", dispense_date);
					parm.addData("QTY1", "");
					parm.addData("PRICE1", "");
					parm.addData("QTY2", qty);
					parm.addData("PRICE2", price);
					parm.addData("UNIT_CHN_DESC", unit_desc);
					parm.addData("ANA_QTY",
							StringTool.round(parm_2.getDouble("QTY2", i), 0));
					parm.addData("ANA_PRICE",
							StringTool.round(parm_2.getDouble("PRICE2", i), 4));
				}
			}
			// System.out.println("parm2:::::"+parm);

			for (int i = 0; i < parm_1.getCount("ORDER_CODE1"); i++) {
				boolean flg = false;
				order_code = parm_1.getValue("ORDER_CODE1", i);
				order_desc = parm_1.getValue("ORDER_DESC1", i);
				unit_desc = parm_1.getValue("UNIT_CHN_DESC1", i);
				request_date = parm_1.getValue("REQUEST_DATE1", i);
				dispense_date = parm_1.getValue("DISPENSE_DATE1", i);
				qty = parm_1.getValue("QTY1", i);
				price = parm_1.getValue("PRICE1", i);
				for (int j = 0; j < parm_2.getCount("ORDER_CODE2"); j++) {
					if (order_code.equals(parm_2.getValue("ORDER_CODE2", j))) {
						flg = true;
						break;
					}
				}
				if (!flg) {
					parm.addData("ORDER_CODE", order_code);
					parm.addData("ORDER_DESC", order_desc);
					parm.addData("UNIT_CHN_DESC", unit_desc);
					parm.addData("REQUEST_DATE1", request_date);
					parm.addData("DISPENSE_DATE1", dispense_date);
					parm.addData("REQUEST_DATE2", "");
					parm.addData("DISPENSE_DATE2", "");
					parm.addData("QTY1", qty);
					parm.addData("PRICE1", price);
					parm.addData("QTY2", "");
					parm.addData("PRICE2", "");
					parm.addData("ANA_QTY", ("-" + StringTool.round(
							parm_1.getDouble("QTY1", i), 0)));
					parm.addData(
							"ANA_PRICE",
							("-" + StringTool.round(
									parm_1.getDouble("PRICE1", i), 4)));
				}
			}

			if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
				this.messageBox("没有查询数据");
				return;
			}
			// System.out.println("parm:::::" + parm);
			// 在table中显示查询信息
			table.setParmValue(parm);
		}
		//table.removeRowAll();

	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = this.getTable("TTable");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "健康体检差异分析统计");
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		String clearStr = "DATE";
		this.clearValue(clearStr);
		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 查询时间
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp DateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("DATE", DateTimestamp.toString().substring(0, 4) + "/"
				+ DateTimestamp.toString().substring(5, 7) + "/26 00:00:00");

		TTable table = this.getTable("TTable");
		table.removeRowAll();
	}

	/**
	 * 打印方法
	 */
	public void onPrint() {
		TTable table = getTable("TTable");
		// 汇总
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		} else {
			// 打印数据
			TParm data = new TParm();
			// 表头数据
			data.setData("TITLE", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion())
					+ "健康体检差异分析统计");
			// String start_date = getValueString("START_DATE");
			// String end_date = getValueString("END_DATE");
			// data.setData("DATE_AREA", "TEXT", "统计时间: "
			// + start_date.substring(0, 4) + "/"
			// + start_date.substring(5, 7) + "/"
			// + start_date.substring(8, 10) + " "
			// + start_date.substring(11, 13) + ":"
			// + start_date.substring(14, 16) + ":"
			// + start_date.substring(17, 19) + " ~ "
			// + end_date.substring(0, 4) + "/" + end_date.substring(5, 7)
			// + "/" + end_date.substring(8, 10) + " "
			// + end_date.substring(11, 13) + ":"
			// + end_date.substring(14, 16) + ":"
			// + end_date.substring(17, 19));

			data.setData("DATE", "TEXT",
					"制表时间: "
							+ SystemTool.getInstance().getDate().toString()
									.substring(0, 10).replace('-', '/'));
			data.setData("USER", "TEXT", "制表人: " + Operator.getName());
			// data.setData("ORG_CODE", "TEXT",
			// "统计部门: " +
			// this.getComboBox("ORG_CODE").getSelectedName());
			// 表格数据
			TParm parm = new TParm();
			TParm tableParm = table.getShowParmValue();
			for (int i = 0; i < table.getRowCount(); i++) {
				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
				parm.addData("QTY1", tableParm.getValue("QTY1", i));
				parm.addData("PRICE1", tableParm.getValue("PRICE1", i));
				parm.addData("REQUEST_DATE1",
						tableParm.getValue("REQUEST_DATE1", i));
				parm.addData("DISPENSE_DATE1",
						tableParm.getValue("DISPENSE_DATE1", i));
				parm.addData("QTY2", tableParm.getValue("QTY2", i));
				parm.addData("PRICE2", tableParm.getValue("PRICE2", i));
				parm.addData("REQUEST_DATE2",
						tableParm.getValue("REQUEST_DATE2", i));
				parm.addData("DISPENSE_DATE2",
						tableParm.getValue("DISPENSE_DATE2", i));
				parm.addData("UNIT_CHN_DESC",
						tableParm.getValue("UNIT_CHN_DESC", i));
				parm.addData("ANA_QTY", tableParm.getValue("ANA_QTY", i));
				parm.addData("ANA_PRICE", tableParm.getValue("ANA_PRICE", i));
				// System.out.println(parm);
			}
			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "QTY1");
			parm.addData("SYSTEM", "COLUMNS", "PRICE1");
			parm.addData("SYSTEM", "COLUMNS", "REQUEST_DATE1");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_DATE1");
			parm.addData("SYSTEM", "COLUMNS", "QTY2");
			parm.addData("SYSTEM", "COLUMNS", "PRICE2");
			parm.addData("SYSTEM", "COLUMNS", "REQUEST_DATE2");
			parm.addData("SYSTEM", "COLUMNS", "DISPENSE_DATE2");
			parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "ANA_QTY");
			parm.addData("SYSTEM", "COLUMNS", "ANA_PRICE");
			// System.out.println("psrm::::::::: " + parm);
			data.setData("TABLE", parm.getData());

			// 调用打印方法
			this.openPrintDialog(
					"%ROOT%\\config\\prt\\IND\\INDDifferenceAnalysis.jhw", data);
		}

	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

}
