package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 预约爽约统计表
 * </p>
 * 
 * <p>
 * Description:预约爽约统计表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c)
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author huangtt 20140313
 * @version 1.0
 */

public class REGOrderFailsStatisticsControl extends TControl {
	private static TTable table;
	private static TTable table2;
	private static TTable table3;
	private String date_s;
	private String date_e;

	public void onInit() {
		table = (TTable) getComponent("TABLE");
		table2 = (TTable) getComponent("TABLE2");
		table3 = (TTable) getComponent("TABLE3");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", StringTool.rollDate(date, -30).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
	}

	public void onQuery() {
		date_s = getValueString("START_DATE");
		date_e = getValueString("END_DATE");
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		getTable();
		getTable2();
		getTable3();

	}

	public void getTable3() {
		String sql3 = "SELECT   B.USER_NAME OPT_USER, C.COST_CENTER_CHN_DESC COST_CENTER_DESC,"
				+ " A.REG_ALL ORDER_REG"
				+ " FROM (SELECT   OPT_USER, SUM (1) REG_ALL"
				+ " FROM REG_PATADM"
				+ " WHERE ADM_DATE BETWEEN TO_DATE ('"
				+ date_s
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ "  )"
				+ "  AND TO_DATE ('"
				+ date_e
				+ "',"
				+ "  'YYYYMMDDHH24MISS'"
				+ "  )"
				+ " AND APPT_CODE = 'Y'"
				+ " AND REGCAN_USER IS NULL"
				+ " AND REGCAN_DATE IS NULL"
				+ " AND ADM_TYPE = 'O'"
				+ " GROUP BY OPT_USER) A,"
				+ " SYS_OPERATOR B,"
				+ " SYS_COST_CENTER C"
				+ " WHERE A.OPT_USER = B.USER_ID AND B.COST_CENTER_CODE = C.COST_CENTER_CODE"
				+ " ORDER BY A.REG_ALL DESC";
//		System.out.println("查询sql3::" + sql3);
		TParm parm3 = new TParm(TJDODBTool.getInstance().select(sql3));
		if (parm3.getCount() < 0) {
			this.messageBox("没有要查询的数据！");
			table.removeRowAll();
			table2.removeRowAll();
			table3.removeRowAll();
			return;
		}
		table3.setParmValue(parm3);
	}

	public void getTable2() {
		String sql1 = "SELECT B.USER_NAME,C.COST_CENTER_CODE,C.COST_CENTER_CHN_DESC DEPT_DESC "
				+ " FROM (SELECT DISTINCT  OPT_USER"
				+ " FROM REG_PATADM"
				+ " WHERE ADM_DATE BETWEEN TO_DATE ('"
				+ date_s
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ " )"
				+ " AND TO_DATE ('"
				+ date_e
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ " )"
				+ " AND APPT_CODE = 'Y'"
				+ " AND REGCAN_USER IS NULL"
				+ " AND REGCAN_DATE IS NULL"
				+ " AND ADM_TYPE = 'O') A,"
				+ " SYS_OPERATOR B,            "
				+ " SYS_COST_CENTER C"
				+ " WHERE A.OPT_USER = B.USER_ID"
				+ " AND B.COST_CENTER_CODE = C.COST_CENTER_CODE";
//		System.out.println("查询sql1::" + sql1);
		TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql1));

		String sql2 = "SELECT  A.REALDEPT_CODE,B.DEPT_CHN_DESC DEPT_DESC, SUM (1) REG_COUNT"
				+ " FROM REG_SCHDAY A,SYS_DEPT B"
				+ " WHERE TO_DATE (A.ADM_DATE, 'YYYYMMDDHH24MISS')"
				+ " BETWEEN TO_DATE ('"
				+ date_s
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ "  )"
				+ " AND TO_DATE ('"
				+ date_e
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ " )"
				+ " AND A.CLINICTYPE_CODE <> '01'"
				+ " AND A.REALDEPT_CODE=B.DEPT_CODE"
				+ " GROUP BY A.REALDEPT_CODE,B.DEPT_CHN_DESC";
//		System.out.println("查询sql2::" + sql2);
		TParm parm2 = new TParm(TJDODBTool.getInstance().select(sql2));
		TParm tableParm = new TParm();
		for (int i = 0; i < parm2.getCount(); i++) {
			String deptDesc = parm2.getValue("DEPT_DESC", i);
			int drCount = 0;
			for (int j = 0; j < parm1.getCount(); j++) {
				if (parm1.getValue("DEPT_DESC", j).equals(deptDesc)) {
					tableParm.addData("DEPT_CODE", deptDesc);
					tableParm.addData("OPT_USER", parm1
							.getValue("USER_NAME", j));
					tableParm.addData("DR_COUNT", "");
					tableParm.addData("DEPT_DR_COUNT", "");
					tableParm.addData("DEPT_RATE", "");
					drCount++;
				}
			}
			if (drCount > 0) {
				tableParm.setData("DR_COUNT", tableParm.getCount("DEPT_CODE")
						- drCount, parm2.getValue("REG_COUNT", i));
				tableParm.setData("DEPT_DR_COUNT", tableParm
						.getCount("DEPT_CODE")
						- drCount, drCount);
				tableParm.setData("DEPT_RATE", tableParm.getCount("DEPT_CODE")
						- drCount, drCount / parm2.getDouble("REG_COUNT", i));
			} else {
				tableParm.addData("DEPT_CODE", deptDesc);
				tableParm.addData("OPT_USER", "");
				tableParm.addData("DR_COUNT", parm2.getValue("REG_COUNT", i));
				tableParm.addData("DEPT_DR_COUNT", 0);
				tableParm.addData("DEPT_RATE", 0);
			}
		}

		if(tableParm == null){
			this.messageBox("没有要查询的数据！");
			table.removeRowAll();
			table2.removeRowAll();
			table3.removeRowAll();
			return;
		}
		table2.setParmValue(tableParm);

	}

	public void getTable() {
		String sql = "SELECT   C.DEPT_CHN_DESC DEPT_CODE, F.COST_CENTER_CHN_DESC COST_CENTER_DESC, D.USER_NAME OPT_USER,"
				+ " E.USER_NAME DR_CODE, A.REG_ALL ORDER_REG, B.REG_NO MISS_REG,"
				+ " B.REG_NO / A.REG_ALL MISS_RATE"
				+ " FROM (SELECT   REALDEPT_CODE, OPT_USER, REALDR_CODE, CLINICTYPE_CODE,"
				+ " SUM (1) REG_ALL"
				+ " FROM REG_PATADM"
				+ " WHERE ADM_DATE BETWEEN TO_DATE ('"
				+ date_s
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ " )"
				+ " AND TO_DATE ('"
				+ date_e
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ " )"
				+ " AND APPT_CODE = 'Y'"
				+ " AND REGCAN_USER IS NULL"
				+ " AND REGCAN_DATE IS NULL"
				+ " AND ADM_TYPE = 'O'"
				+ " GROUP BY REALDEPT_CODE, OPT_USER, REALDR_CODE, CLINICTYPE_CODE) A,"
				+ " (SELECT   REALDEPT_CODE, OPT_USER, REALDR_CODE, CLINICTYPE_CODE,"
				+ " SUM (1) REG_NO"
				+ " FROM REG_PATADM"
				+ " WHERE ADM_DATE BETWEEN TO_DATE ('"
				+ date_s
				+ "',"
				+ "  'YYYYMMDDHH24MISS'"
				+ " )"
				+ " AND TO_DATE ('"
				+ date_e
				+ "',"
				+ " 'YYYYMMDDHH24MISS'"
				+ " )"
				+ " AND ARRIVE_FLG = 'N'"
				+ " AND APPT_CODE = 'Y'"
				+ " AND REGCAN_USER IS NULL"
				+ " AND REGCAN_DATE IS NULL"
				+ " AND ADM_TYPE = 'O'"
				+ " GROUP BY REALDEPT_CODE, OPT_USER, REALDR_CODE, CLINICTYPE_CODE) B,"
				+ " SYS_DEPT C,"
				+ " SYS_OPERATOR D,"
				+ " SYS_OPERATOR E,"
				+ " SYS_COST_CENTER F"
				+ " WHERE A.REALDEPT_CODE = B.REALDEPT_CODE(+)"
				+ " AND A.REALDR_CODE = B.REALDR_CODE(+)"
				+ " AND A.OPT_USER = B.OPT_USER(+)"
				+ " AND A.CLINICTYPE_CODE = B.CLINICTYPE_CODE(+)"
				+ " AND A.REALDEPT_CODE = C.DEPT_CODE"
				+ " AND A.REALDR_CODE = E.USER_ID"
				+ " AND A.OPT_USER = D.USER_ID"
				+ " AND D.COST_CENTER_CODE=F.COST_CENTER_CODE";

//		String deptCode = this.getValueString("DEPT_CODE");
//		if (deptCode.length() > 0) {
//			sql += " AND A.REALDEPT_CODE='" + deptCode + "'";
//		}
//		String drCode = this.getValueString("DR_CODE");
//		if (drCode.length() > 0) {
//			sql += " AND A.REALDR_CODE='" + drCode + "'";
//		}

		sql += " ORDER BY A.REALDEPT_CODE, E.USER_NAME, D.USER_NAME";

//		System.out.println("查询sql::" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() < 0) {
			this.messageBox("没有要查询的数据！");
			table.removeRowAll();
			table2.removeRowAll();
			table3.removeRowAll();
			return;
		}
		table.setParmValue(parm);
	}

	public void onClear() {
		table.removeRowAll();
		table2.removeRowAll();
		table3.removeRowAll();
		this.clearValue("START_DATE;END_DATE");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE", StringTool.rollDate(date, -30).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
	}

	/**
	 * 导出Excel
	 * */
	public void onExport() {
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		TTable table2 = (TTable) callFunction("UI|TABLE2|getThis");
		TTable table3 = (TTable) callFunction("UI|TABLE3|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "本院预约明细表");
		ExportExcelUtil.getInstance().exportExcel(table2, "各科医师参与预约挂号人数");
		ExportExcelUtil.getInstance().exportExcel(table3, "预约数排名");
	}

}
