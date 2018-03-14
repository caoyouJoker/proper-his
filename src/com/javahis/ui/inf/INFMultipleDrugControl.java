package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TComboBoxEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:多重耐药菌监测结果汇总表
 * </p>
 * 
 * <p>
 * Description:多重耐药菌监测结果汇总表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author liuyalin 20170425
 * @version JavaHis 1.0
 */
public class INFMultipleDrugControl extends TControl {

	@Override
	public void onInit() {
		super.onInit();
		this.callFunction("UI|QUARTER|addEventListener",
				TComboBoxEvent.SELECTED, this, "SEASON");
		initPage();
	}

	String year = "";
	String START_DATE = "";
	String END_DATE = "";

	private void initPage() {
		this.clearValue("DEPT_CODE;PAT_NAME;START_DATE;END_DATE;YEAR;QUARTER");
		Timestamp time = SystemTool.getInstance().getDate();
		Timestamp endDate = Timestamp.valueOf(time.toString().substring(0, 10)
				+ " 23:59:59");
		Timestamp startDate = Timestamp.valueOf(StringTool.rollDate(time, -7)
				.toString().substring(0, 10)
				+ " 00:00:00");
		Timestamp year = Timestamp.valueOf(time.toString().substring(0, 10)
				+ " 23:59:59");
		this.setValue("START_DATE", startDate);
		this.setValue("END_DATE", endDate);
		this.setValue("YEAR", year);
		this.callFunction("UI|TABLE|setParmValue", new TParm());

		// 设置区域
		this.setValue("REGION_CODE", "H01");
	}

	/**
	 * 季度显示时间
	 * 
	 * @throws ParseException
	 */
	public void SEASON() {
		if (this.getValueString("YEAR").length() > 0) {
			year = this.getValueString("YEAR").substring(0, 4)
					.replaceAll("-", "");

			switch (this.getValueInt("QUARTER")) {
			case 1:
				START_DATE = year + "-01-01 00:00:00.0";
				END_DATE = year + "-03-31 23:59:59.0";
				break;
			case 2:
				START_DATE = year + "-04-01 00:00:00.0";
				END_DATE = year + "-06-30 23:59:59.0";
				break;
			case 3:
				START_DATE = year + "-07-01 00:00:00.0";
				END_DATE = year + "-09-30 23:59:59.0";
				break;
			case 4:
				START_DATE = year + "-10-01 00:00:00.0";
				END_DATE = year + "-12-31 23:59:59.0";
				break;
			}
			String strsDate = START_DATE;
			String streDate = END_DATE;
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date Sdate;
			try {
				Sdate = sdf.parse(strsDate);
				this.setValue("START_DATE", Timestamp
						.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(Sdate)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			Date Edate;
			try {
				Edate = sdf.parse(streDate);
				this.setValue("END_DATE", Timestamp
						.valueOf(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
								.format(Edate)));
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			this.messageBox_("请输入开始时间");
			return;
		}
	}

	/**
	 * 查询
	 * 
	 * @throws ParseException
	 */
	public void onQuery() {
		if (!StringUtil.isNullString(this.getValueString("YEAR"))) {
			SEASON();
		} else {
		}
		String sDate = this.getValueString("START_DATE");
		String eDate = this.getValueString("END_DATE");
		sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");
		eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "")
				.replace(":", "").replace("-", "");

		String in_date = "";

		in_date += " AND IN_DATE > TO_DATE('" + sDate
				+ "','YYYYMMDDHH24MISS') ";

		in_date += " AND IN_DATE < TO_DATE('" + eDate
				+ "','YYYYMMDDHH24MISS') ";
		String dept_code = this.getValueString("DEPT_CODE");
		// 同期住院患者人数
		String sql = "SELECT COUNT(CASE_NO) COUNT FROM ADM_INP WHERE DEPT_CODE = '"
				+ dept_code + "'" + in_date;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("同期住院患者人数：：：：" + sql);
		double count = Double.parseDouble(parm.getValue("COUNT", 0));

		String opt_date = " ";
		opt_date += " AND A.OPT_DATE > TO_DATE('" + sDate
				+ "','YYYYMMDDHH24MISS') ";
		opt_date += " AND A.OPT_DATE < TO_DATE('" + eDate
				+ "','YYYYMMDDHH24MISS') ";

		// CRE
		String sqlA = "SELECT COUNT(C.MR_NO) COUNT "
				+ "FROM (SELECT DISTINCT (B.MR_NO)"
				+ "FROM MED_LIS_CULRPT A, MED_APPLY B "
				+ "WHERE A.REMARK = 'CRE'" + opt_date + "AND DEPT_CODE = '"
				+ dept_code + "' " + "AND A.CAT1_TYPE = B.CAT1_TYPE "
				+ "AND A.APPLICATION_NO = B.APPLICATION_NO) C " + "WHERE 1=1";
		TParm parmA = new TParm(TJDODBTool.getInstance().select(sqlA));
		parm.setData("COUNTA", 0, parmA.getValue("COUNT", 0) == null ? "0"
				: parmA.getValue("COUNT", 0));
		double countA = Double.parseDouble(parm.getValue("COUNTA", 0));
		System.out.println("CRE感染：：：：" + sqlA);
		// CRE感染百分比
		double percentA = (countA / count) * 100;
		parm.setData("PERCENTA", 0, percentA);

		// MRSA
		String sqlB = "SELECT COUNT(C.MR_NO) COUNT "
				+ "FROM (SELECT DISTINCT (B.MR_NO)"
				+ "FROM MED_LIS_CULRPT A, MED_APPLY B "
				+ "WHERE A.REMARK = 'MRSA'" + opt_date + "AND DEPT_CODE = '"
				+ dept_code + "' " + "AND A.CAT1_TYPE = B.CAT1_TYPE "
				+ "AND A.APPLICATION_NO = B.APPLICATION_NO) C " + "WHERE 1=1";
		TParm parmB = new TParm(TJDODBTool.getInstance().select(sqlB));
		parm.setData("COUNTB", 0, parmB.getValue("COUNT", 0) == null ? "0"
				: parmB.getValue("COUNT", 0));
		double countB = Double.parseDouble(parm.getValue("COUNTB", 0));
		System.out.println("MRSA感染：：：：" + sqlB);
		// MRSA感染百分比
		double percentB = countB / count * 100;
		parm.setData("PERCENTB", 0, percentB);

		// VRE
		String sqlC = "SELECT COUNT(C.MR_NO) COUNT "
				+ "FROM (SELECT DISTINCT (B.MR_NO)"
				+ "FROM MED_LIS_CULRPT A, MED_APPLY B "
				+ "WHERE A.REMARK = 'VRE'" + opt_date + "AND DEPT_CODE = '"
				+ dept_code + "' " + "AND A.CAT1_TYPE = B.CAT1_TYPE "
				+ "AND A.APPLICATION_NO = B.APPLICATION_NO) C " + "WHERE 1=1";
		TParm parmC = new TParm(TJDODBTool.getInstance().select(sqlC));
		parm.setData("COUNTC", 0, parmC.getValue("COUNT", 0) == null ? "0"
				: parmC.getValue("COUNT", 0));
		double countC = Double.parseDouble(parm.getValue("COUNTC", 0));
		System.out.println("VRE感染：：：：" + sqlC);
		// VRE感染百分比
		double percentC = countC / count * 100;
		parm.setData("PERCENTC", 0, percentC);

		// CRABA
		String sqlD = "SELECT COUNT(C.MR_NO) COUNT "
				+ "FROM (SELECT DISTINCT (B.MR_NO)"
				+ "FROM MED_LIS_CULRPT A, MED_APPLY B "
				+ "WHERE A.REMARK = 'CRABA'" + opt_date + "AND DEPT_CODE = '"
				+ dept_code + "' " + "AND A.CAT1_TYPE = B.CAT1_TYPE "
				+ "AND A.APPLICATION_NO = B.APPLICATION_NO) C " + "WHERE 1=1";
		TParm parmD = new TParm(TJDODBTool.getInstance().select(sqlD));
		parm.setData("COUNTD", 0, parmD.getValue("COUNT", 0) == null ? "0"
				: parmD.getValue("COUNT", 0));
		double countD = Double.parseDouble(parm.getValue("COUNTD", 0));
		System.out.println("CRABA感染：：：：" + sqlD);
		// CRABA感染百分比
		double percentD = countD / count * 100;
		parm.setData("PERCENTD", 0, percentD);

		// CRPAE
		String sqlE = "SELECT COUNT(C.MR_NO) COUNT "
				+ "FROM (SELECT DISTINCT (B.MR_NO)"
				+ "FROM MED_LIS_CULRPT A, MED_APPLY B "
				+ "WHERE A.REMARK = 'CRPAE'" + opt_date + "AND DEPT_CODE = '"
				+ dept_code + "' " + "AND A.CAT1_TYPE = B.CAT1_TYPE "
				+ "AND A.APPLICATION_NO = B.APPLICATION_NO) C " + "WHERE 1=1";
		TParm parmE = new TParm(TJDODBTool.getInstance().select(sqlE));
		parm.setData("COUNTE", 0, parmE.getValue("COUNT", 0) == null ? "0"
				: parmE.getValue("COUNT", 0));
		double countE = Double.parseDouble(parm.getValue("COUNTE", 0));
		System.out.println("CRPAE感染：：：：" + sqlE);
		// CRPAE感染百分比
		double percentE = countE / count * 100;
		parm.setData("PERCENTE", 0, percentE);
		String quarter = this.getValueString("QUARTER");
		if (!StringUtil.isNullString(quarter)) {
			String season = year + "" + "年第" + quarter + "季度";
			parm.setData("QUARTER", 0, season);
		} else {
			parm.setData("QUARTER", 0, "");
		}
		getTTable("TABLE").setParmValue(parm);
	}

	/**
	 * 导出Excel
	 */
	public void onExcel() {
		TTable table = this.getTTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox_("暂无导出Excel数据");
			return;
		}
		INFMultipleDrugUtil.getInstance().exportExcel(table,
				"天津市医疗机构多重耐药菌监测结果汇总表");

	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
	}

	/**
	 * 获取TTable
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

}
