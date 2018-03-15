package com.javahis.ui.sta;

import com.dongyang.ui.TTable;
import com.dongyang.ui.TComboBox;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.util.StringTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import java.sql.Timestamp;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: STA_IN_11门急诊挂号统计报表
 * </p>
 * 
 * <p>
 * Description: 门急诊挂号统计报表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wanglong 2012.06.21
 * @version 1.0
 */
public class STAIn_11Control extends TControl {

	private StringUtil util;// 字符工具类
	private TParm tableParm; // 单档表格的TParm

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		initUI();
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	/**
	 * 初始化界面
	 */
	public void initUI() {
		Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
				.getDate(), -1);
		this.setValue("S_DATE", SystemTool.getInstance().getDate());// 重设日期
		this.setValue("E_DATE", SystemTool.getInstance().getDate());
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("ADM_TYPE", "");// 清空下拉框
		this.setValue("DEPT_CODE", "");
		this.callFunction("UI|Table|removeRowAll");// 清空表格
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initUI();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 判断门急诊类型
		if (this.getValueString("ADM_TYPE").equals("")) {
			this.messageBox("请选择门急诊类型");
			return;
		}
		String startDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("S_DATE")), "yyyyMMdd");
		String endDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("E_DATE")), "yyyyMMdd");
		String admType = "";// 门诊级别ID
		if (!this.getValue("ADM_TYPE").equals("")) {
			admType = " AND A.ADM_TYPE = '" + this.getValue("ADM_TYPE") + "'  ";
		}
		String deptCode = "";// 科室ID
		if (!this.getValue("DEPT_CODE").equals("")) {
			deptCode = " AND A.DEPT_CODE = '" + this.getValue("DEPT_CODE")
					+ "'  ";
		}
		String regionCode = "";// 区域ID
		if (!this.getValue("REGION_CODE").equals("")) {
			regionCode = " AND A.REGION_CODE= '" + this.getValue("REGION_CODE")
					+ "' ";
		}
		String dateStr = "";
		// 门诊取挂号时间
		dateStr = " AND I.BILL_DATE BETWEEN TO_DATE('" + startDate   //modify by huangtt 20141224  adm_date  改为bill_date
					+ "000000','yyyyMMddHH24miss') " + " AND TO_DATE('"
					+ endDate + "235959','yyyyMMddHH24miss') ";
		String sql = "SELECT *　FROM ( SELECT A.CASE_NO, A.MR_NO, B.PAT_NAME,"
				+ " F.CHN_DESC, B.BIRTH_DATE, B.TEL_HOME,"
				+ " D.DEPT_CHN_DESC, E.USER_NAME,"
				+ " CASE C.MAIN_DIAG_FLG WHEN 'Y' THEN  H.ICD_CHN_DESC WHEN 'N' THEN  'N' "
				+ " ELSE '' END AS ICD_CHN_DESC , "
				+ " CASE C.MAIN_DIAG_FLG WHEN 'Y' THEN  H.ICD_CODE WHEN 'N' THEN  'N' ELSE '' END AS ICD_CODE,"//add by lij 20170316
				+ " G.CTZ_DESC,A.REG_DATE"
				+ " FROM REG_PATADM A," + " SYS_PATINFO B," + " OPD_DIAGREC C,"
				+ " SYS_DEPT D," + " SYS_OPERATOR E," + " SYS_DICTIONARY F,"
				+ " SYS_CTZ G," + " SYS_DIAGNOSIS H" 
				+ " , BIL_REG_RECP I "  //add by huangtt 20141224 
				+ " WHERE A.MR_NO = B.MR_NO" + " AND A.CASE_NO= C.CASE_NO(+)"
				+ " AND A.DEPT_CODE = D.DEPT_CODE" 
				+ " AND A.CASE_NO = I.CASE_NO" //add by huangtt 20141224
				+ " AND A.DR_CODE = E.USER_ID" + " AND B.SEX_CODE = F.ID"
				+ " AND F.GROUP_ID = 'SYS_SEX'"
				+ " AND A.CTZ1_CODE = G.CTZ_CODE"
				+ " AND C.ICD_TYPE = H.ICD_TYPE(+)"
				+ " AND C.ICD_CODE = H.ICD_CODE(+)"
				+ "  AND A.REGCAN_USER IS NULL " + admType + deptCode
				+ regionCode + dateStr;
		sql += " ) TAB WHERE TAB.ICD_CHN_DESC IS NULL OR TAB.ICD_CHN_DESC<>'N' " + " ORDER BY TAB.REG_DATE";
//		System.out.println("-------------sql" + sql);
		tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		TParm nowTimeParm = new TParm(TJDODBTool.getInstance().select(
				"SELECT SYSDATE FROM DUAL"));
		int count = 0;
		if (tableParm.getCount("CASE_NO") <= 0) {
			this.messageBox("查无数据");
			this.callFunction("UI|Table|removeRowAll");// 清空表格
			// 合计笔数
			this.setValue("SUM_TOT", count + "");
			return;
		}
		count = tableParm.getCount("CASE_NO");
		// 合计笔数
		this.setValue("SUM_TOT", count + "");
		for (int i = 0; i < tableParm.getCount("CASE_NO"); i++) {
			tableParm.setData("BIRTH_DATE", i, StringUtil.showAge(
					tableParm.getTimestamp("BIRTH_DATE", i),
					nowTimeParm.getTimestamp("SYSDATE", 0)));
		}
		// 表格赋值
		this.callFunction("UI|Table|setParmValue", tableParm);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		TTable table = (TTable) callFunction("UI|Table|getThis");
		TParm parm = table.getShowParmValue();
		if (parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "门急诊挂号明细统计报表");
		}
	}

	/**
	 * 打印报表
	 */
	public void onPrint() {
		TTable table = ((TTable) this.getComponent("Table"));
		int rowCount = tableParm.getCount("CASE_NO");
		if (rowCount < 1) {
			this.messageBox("请先查询数据!");
			return;
		}
		TParm T1 = new TParm(); // 表格数据
		for (int i = 0; i < rowCount; i++) {
			T1.addRowData(tableParm, i);
		}
		T1.setCount(rowCount);
		String[] chage = table.getParmMap().split(";");
		for (int i = 0; i < chage.length; i++) {
			T1.addData("SYSTEM", "COLUMNS", chage[i]);
		}
		String sDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("S_DATE")), "yyyy/MM/dd");
		String eDate = StringTool.getString(
				TypeTool.getTimestamp(this.getValue("E_DATE")), "yyyy/MM/dd");
		String admName = new TParm(TJDODBTool.getInstance().select(
				"SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_ADMTYPE' AND ID='"
						+ this.getValue("ADM_TYPE") + "'")).getValue(
				"CHN_DESC", 0);// 门诊级别ID
		String deptName = new TParm(TJDODBTool.getInstance().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ this.getValue("DEPT_CODE") + "'")).getValue(
				"DEPT_CHN_DESC", 0);// 科室ID
		TParm printParm = new TParm();// 给报表传参的TParm
		printParm.setData("START_DATE", "TEXT", sDate);
		printParm.setData("END_DATE", "TEXT", eDate);
		printParm.setData("ADM_NAME", "TEXT", admName);
		printParm.setData("DEPT_NAME", "TEXT", deptName);
		printParm.setData("OPT_USER", "TEXT", "制表人:" + Operator.getName());
		printParm.setData("DATE", "TEXT", "制表时间: "
				+ SystemTool.getInstance().getDate().toString().substring(
						0, 10).replace('-', '/'));
		printParm.setData("TABLE", T1.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\sta\\STA_IN_11.jhw",
				printParm);
	}
}
