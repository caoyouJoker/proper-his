package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 非国药药品结算
 * </p>
 * 
 * <p>
 * Description: 非国药药品结算
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liuyalin 2017.06.06
 * @version 1.0
 */
public class SPCTCMSettleAccountsControl extends TControl {
	public TTable table;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始化查询区间
		this.setValue("START_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInitLast(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("END_DATE", rollDay);

		// 获取区域
		setValue("REGION_CODE", Operator.getRegion());

		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		table = this.getTable("TABLE");

	}

	/**
	 * 查询
	 */
	public void onQuery() {
		//获取时间范围
		String sDate = this.getValueString("START_DATE");
		String eDate = this.getValueString("END_DATE");

		String opt_dateS = "";
		String opt_dateE = "";
		sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		opt_dateS += "  TO_DATE('" + sDate + "','YYYYMMDDHH24MISS') ";
		eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		opt_dateE += "  TO_DATE('" + eDate + "','YYYYMMDDHH24MISS') ";

		//获取药品
		String codeA ="";
		String codeB ="";
		String orderCode = getValueString("ORDER_CODE");
		
		if (StringUtils.isNotEmpty(orderCode)) {
			codeA = "AND A.ORDER_CODE ='" + orderCode + "'" ;
			codeB = "AND B.ORDER_CODE ='" + orderCode + "'" ;
		}else{
			codeA = "" ;
			codeB = "" ;
		}
		String sql = "  SELECT D.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,D.OWN_PRICE,D.UNIT_CHN_DESC,"
				+ "SUM (D.DOSAGE_QTY) AS DOSAGE_QTY,SUM (D.DOSAGE_QTY) * D.OWN_PRICE AS TOT_AMT,D.ORG_CODE "
				+ "FROM (SELECT A.ORDER_CODE,A.ORDER_DESC, A.SPECIFICATION,"
				+ "A.DISPENSE_QTY1 AS DOSAGE_QTY,E.UNIT_CHN_DESC,"
				+ "A.OWN_PRICE,C.ORG_CODE "
				+ "FROM ODI_DSPNM A, IND_STOCK C, SYS_UNIT E "
				+ "WHERE     A.EXEC_DEPT_CODE = C.ORG_CODE "
				+ "AND A.DISPENSE_UNIT = E.UNIT_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.BATCH_SEQ1 = C.BATCH_SEQ "
				+ "AND C.SUP_CODE != '18' "
				+ "AND A.PHA_DOSAGE_DATE >= "
				+ opt_dateS
				+ "AND A.PHA_DOSAGE_DATE <= "
				+ opt_dateE
				+ codeA
				+ "UNION ALL "
				+ "SELECT A.ORDER_CODE,A.ORDER_DESC, A.SPECIFICATION,"
				+ "A.DISPENSE_QTY2 AS DOSAGE_QTY,E.UNIT_CHN_DESC,"
				+ "A.OWN_PRICE,C.ORG_CODE "
				+ "FROM ODI_DSPNM A, IND_STOCK C, SYS_UNIT E "
				+ "WHERE     A.EXEC_DEPT_CODE = C.ORG_CODE "
				+ "AND A.DISPENSE_UNIT = E.UNIT_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.BATCH_SEQ2 = C.BATCH_SEQ "
				+ "AND C.SUP_CODE != '18' "
				+ "AND A.PHA_DOSAGE_DATE >= "
				+ opt_dateS
				+ "AND A.PHA_DOSAGE_DATE <= "
				+ opt_dateE
				+ codeA
				+ "UNION ALL "
				+ "SELECT A.ORDER_CODE,A.ORDER_DESC, A.SPECIFICATION,"
				+ "A.DISPENSE_QTY3 AS DOSAGE_QTY,E.UNIT_CHN_DESC,"
				+ "A.OWN_PRICE,C.ORG_CODE "
				+ "FROM ODI_DSPNM A, IND_STOCK C, SYS_UNIT E "
				+ "WHERE     A.EXEC_DEPT_CODE = C.ORG_CODE "
				+ "AND A.DISPENSE_UNIT = E.UNIT_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.BATCH_SEQ3 = C.BATCH_SEQ "
				+ "AND C.SUP_CODE != '18' "
				+ "AND A.PHA_DOSAGE_DATE >= "
				+ opt_dateS
				+ "AND A.PHA_DOSAGE_DATE <= "
				+ opt_dateE
				+ codeA
				+ "UNION ALL "
				+ "SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,"
				+ "-1 * (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS DOSAGE_QTY,"
				+ "E.UNIT_CHN_DESC,A.OWN_PRICE,C.ORG_CODE "
				+ "FROM ODI_DSPNM A,PHA_BASE B,IND_STOCK C,SYS_UNIT E "
				+ "WHERE     A.PHA_RETN_DATE BETWEEN "
				+ opt_dateS
				+ " AND "
				+ opt_dateE
				+ " AND A.DISPENSE_UNIT = E.UNIT_CODE "
				+ "AND A.DSPN_KIND = 'RT'"
				+ "AND A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.EXEC_DEPT_CODE = C.ORG_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.BATCH_SEQ1 = C.BATCH_SEQ "
				+ "AND C.SUP_CODE != '18'"
				+ codeA
				+ "UNION ALL "
				+ "SELECT B.ORDER_CODE,"
				+ "E.ORDER_DESC,E.SPECIFICATION,"
				+ "CASE WHEN B.UNIT_CODE = D.STOCK_UNIT THEN B.ACTUAL_QTY * C.DOSAGE_QTY ELSE B.ACTUAL_QTY END AS DOSAGE_QTY,"
				+ "F.UNIT_CHN_DESC,B.RETAIL_PRICE AS OWN_PRICE,A.TO_ORG_CODE AS ORG_CODE "
				+ "FROM IND_DISPENSEM A,IND_DISPENSED B,PHA_TRANSUNIT C,PHA_BASE D,SYS_FEE E,SYS_UNIT F"
				+ " WHERE     (A.UPDATE_FLG = '3' OR A.UPDATE_FLG = '1') "
				+ "AND A.REQTYPE_CODE IN ('EXM', 'TEC')"
				+ "AND A.TO_ORG_CODE = '040103'"
				+ "AND A.DISPENSE_DATE BETWEEN "
				+ opt_dateS
				+ "AND "
				+ opt_dateE
				+ " AND B.SUP_CODE != '18' "
				+ "AND B.UNIT_CODE = F.UNIT_CODE "
				+ "AND A.DISPENSE_NO = B.DISPENSE_NO "
				+ "AND B.ORDER_CODE = C.ORDER_CODE "
				+ codeB
				+ "AND B.ORDER_CODE = D.ORDER_CODE "
				+ "AND B.ORDER_CODE = E.ORDER_CODE ) D,SYS_FEE A "
				+ "WHERE D.ORDER_CODE = A.ORDER_CODE "
				+ "GROUP BY D.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,D.OWN_PRICE,D.UNIT_CHN_DESC,D.ORG_CODE";
		System.out.println("非国药查询sql:::::::" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		// 在table中显示查询信息
		table.setParmValue(parm);
	}

	public void onPrint() {
		table = this.getTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}

		// 打印数据
		TParm data = new TParm();

		String startDate = getValueString("START_DATE");
		startDate = startDate.substring(0, 19);
		String endDate = getValueString("END_DATE");
		endDate = endDate.substring(0, 19);
		// 表头数据
		data.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion()) + "非国药药品结算");
		data.setData("START_DATE", "TEXT", startDate);
		data.setData("END_DATE", "TEXT", endDate);
		data.setData("DATE", "TEXT", "制表时间: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		data.setData("USER", "TEXT", "制表人: " + Operator.getName());
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getShowParmValue();
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
			parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION",
					tableParm.getValue("SPECIFICATION", i));
			parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
			parm.addData("UNIT_CHN_DESC",
					tableParm.getValue("UNIT_CHN_DESC", i));
			parm.addData("OWN_PRICE",
					tableParm.getValue("OWN_PRICE", i));
			parm.addData("TOT_AMT", tableParm.getValue("TOT_AMT", i));
			parm.addData("ORG_CODE", tableParm.getValue("ORG_CODE", i));
		}
		parm.setCount(parm.getCount("ORDER_CODE"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		parm.addData("SYSTEM", "COLUMNS", "ORG_CODE");
		data.setData("TABLE", parm.getData());

		openPrintDialog("%ROOT%\\config\\prt\\spc\\SPCTCMSettleAccounts.jhw",
				data, true);

	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		String clearStr = "ORDER_CODE;ORDER_DESC;ORG_CODE";
		this.clearValue(clearStr);
		table.removeRowAll();
		onInit();
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = this.getTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "非国药药品结算");
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
	}

	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInitLast(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		String dayCycle = sysParm.getValue("DAY_CYCLE", 0);
		int hours = Integer.parseInt(dayCycle.substring(0, 2));
		result.setHours(hours);
		int minutes = Integer.parseInt(dayCycle.substring(2, 4));
		result.setMinutes(minutes);
		int seconds = Integer.parseInt(dayCycle.substring(4, 6));
		result.setSeconds(seconds);
		return result;
	}

	/**
	 * 初始化时间整理
	 * 
	 * @param date
	 *            Timestamp
	 * @return Timestamp
	 */
	public Timestamp getDateForInit(Timestamp date) {
		String dateStr = StringTool.getString(date, "yyyyMMdd");
		TParm sysParm = BILSysParmTool.getInstance().getDayCycle("I");
		int monthM = sysParm.getInt("MONTH_CYCLE", 0) + 1;
		String monThCycle = "" + monthM;
		dateStr = dateStr.substring(0, 6) + monThCycle;
		Timestamp result = StringTool.getTimestamp(dateStr, "yyyyMMdd");
		return result;
	}

	/**
	 * 得到上个月
	 * 
	 * @param dateStr
	 *            String
	 * @return Timestamp
	 */
	public Timestamp queryFirstDayOfLastMonth(String dateStr) {
		DateFormat defaultFormatter = new SimpleDateFormat("yyyyMMdd");
		Date d = null;
		try {
			d = defaultFormatter.parse(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		GregorianCalendar cal = new GregorianCalendar();
		cal.setTime(d);
		cal.add(Calendar.MONTH, -1);
		cal.set(Calendar.DAY_OF_MONTH, 1);
		return StringTool.getTimestamp(cal.getTime());
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
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
