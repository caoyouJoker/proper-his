package com.javahis.ui.opb;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.bil.BILSysParmTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 门诊收入统计
 * </p>
 * 
 * <p>
 * Description: 门诊收入统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author caowl 2012.07.05
 * @version 1.0
 */
public class OPBReceiptOfOutPatientControl extends TControl {

	/**
	 * 初始化
	 * */
	public void onInit() {
		super.onInit();
		initPage();
	}

	/**
	 * 初始化界面
	 * 
	 */
	private void initPage() {

		// 初始化查询区间
		this.setValue("S_DATE",
				getDateForInit(queryFirstDayOfLastMonth(StringTool.getString(
						SystemTool.getInstance().getDate(), "yyyyMMdd"))));
		Timestamp rollDay = StringTool.rollDate(getDateForInit(SystemTool
				.getInstance().getDate()), -1);
		this.setValue("E_DATE", rollDay);
		// 初始化院区为空
		this.setValue("REALDEPT_CODE", "");
		// 移除表中所有数据
		this.callFunction("UI|Table|removeRowAll");
		// 初始化表
		TTable table = (TTable) this.getComponent("Table");

	}

	/**
	 * 查询
	 * */
	public void onQuery() {
		// 获得查询区间
		String startTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("S_DATE")), "yyyyMMdd HH:mm:ss");
		System.out.println("startTime--->" + startTime);
		String endTime = StringTool.getString(
				TypeTool.getTimestamp(getValue("E_DATE")), "yyyyMMdd HH:mm:ss");
		TParm selParm = new TParm();
		String deptWhere = "";
		System.out.println();
		// 市内门急诊
		if (getValue("REALDEPT_CODE") != null) {
			if (getValue("REALDEPT_CODE").toString().equals("1"))
				deptWhere = " AND (A.ADM_TYPE = 'O' OR A.ADM_TYPE = 'E') AND A.REALDEPT_CODE = '020103'";
		}
		// 院内门诊
		if (getValue("REALDEPT_CODE") != null) {
			if (getValue("REALDEPT_CODE").toString().equals("2"))
				deptWhere = " and a.adm_type = 'O'  and a.REALDEPT_CODE <> '020103'";
		}

		// 院内急诊
		if (getValue("REALDEPT_CODE") != null) {
			if (getValue("REALDEPT_CODE").toString().equals("3"))
				deptWhere = " and a.adm_type = 'E'  and a.REALDEPT_CODE <> '020103'";
		}

		String sql = "SELECT   A.CASE_NO, A.MR_NO, A.挂号费 + A.诊查费 + B.TOT AS 合计, A.挂号费,"
				+ " A.诊查费, 抗生素, 非抗生素, 中成药, 中草药, 检查费, 治疗费, 放射费,"
				+ " 手术费, 输血费, 化验费, 体检费, 社区医疗, 观察床费, CT, MR, 自费部分,"
				+ " 材料费, 输氧费"
				+ "  FROM (SELECT   B.CASE_NO, B.MR_NO, SUM (REG_FEE_REAL) AS 挂号费,"
				+ "  SUM (CLINIC_FEE_REAL) AS 诊查费"
				+ "  FROM REG_PATADM A, BIL_REG_RECP B"
				+ " WHERE A.CASE_NO = B.CASE_NO"
				+ deptWhere
				+ "  AND A.ADM_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "',"
				+ " 'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ "  AND TO_DATE ('"
				+ endTime
				+ "',"
				+ "'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ " GROUP BY B.CASE_NO, B.MR_NO) A,"
				+ "(SELECT   A.CASE_NO, SUM (TOT_AMT) AS TOT, SUM (CHARGE01) AS 抗生素,"
				+ "  SUM (CHARGE02) AS 非抗生素, SUM (CHARGE03) AS 中成药,"
				+ "  SUM (CHARGE04) AS 中草药, SUM (CHARGE05) AS 检查费,"
				+ "  SUM (CHARGE06) AS 治疗费, SUM (CHARGE07) AS 放射费,"
				+ "  SUM (CHARGE08) AS 手术费, SUM (CHARGE09) AS 输血费,"
				+ "  SUM (CHARGE10) AS 化验费, SUM (CHARGE11) AS 体检费,"
				+ "  SUM (CHARGE12) AS 社区医疗, SUM (CHARGE13) AS 观察床费,"
				+ "  SUM (CHARGE14) AS CT, SUM (CHARGE15) AS MR,"
				+ "  SUM (CHARGE16) AS 自费部分, SUM (CHARGE17) AS 材料费,"
				+ "  SUM (CHARGE18) AS 输氧费"
				+ "  FROM REG_PATADM A, BIL_OPB_RECP B"
				+ " WHERE A.CASE_NO = B.CASE_NO"
				+ deptWhere
				+ "  AND CHARGE_DATE BETWEEN TO_DATE ('"
				+ startTime
				+ "',"
				+ "'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ " AND TO_DATE ('"
				+ endTime
				+ "',"
				+ "'yyyymmdd hh24:mi:ss '"
				+ ")"
				+ " GROUP BY A.CASE_NO) B"
				+ "  WHERE A.CASE_NO = B.CASE_NO"
				+ "  ORDER BY CASE_NO";

		System.out.println("sql---->" + sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));

		if (selParm.getCount("CASE_NO") < 1) {
			// 查无数据
			this.messageBox("E0008");
			this.initPage();
			return;
		}
		this.callFunction("UI|Table|setParmValue", selParm);

	}

	/**
	 * 清空
	 * */
	public void onClear() {
		initPage();
		this.clearValue("REALDEPT_CODE");
	}

	/**
	 * 导出Excel
	 * */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		int row = table.getRowCount();
		if (row < 1) {
			this.messageBox_("先查询数据!");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "门诊收入统计表");
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
}
