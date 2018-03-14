/**
 * 
 */
package com.javahis.ui.bil;

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
 * Title: 
 * </p>
 * 
 * <p>
 * Description: 住院收入明细查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author wu 2012-7-25上午10:39:55
 * @version 1.0
 */
public class BILIncomeDetailControl extends TControl {
	private TTable table;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		super.onInit();
		initPage();
	}
	/**
	 * 初始化页面
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");	
		// 初始化查询区间
		this.setValue("E_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 23:59:59");
		this.setValue("S_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		String date_s = getValueString("S_DATE");
		String date_e = getValueString("E_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		String sql = 
			" SELECT   A.BILL_DATE, B.DEPT_CHN_DESC, C.COST_CENTER_CHN_DESC, D.CHN_DESC," +
			" SUM (TOT_AMT) TOT_AMT" +
			" FROM (SELECT TO_CHAR (BILL_DATE, 'YYYY-MM-DD') BILL_DATE, TOT_AMT," +
			" REXP_CODE, DEPT_CODE, EXE_DEPT_CODE" +
			" FROM IBS_ORDD" +
			" WHERE BILL_DATE BETWEEN TO_DATE ('" + date_s + "','YYYYMMDDHH24MISS')" +
			" AND TO_DATE ('" + date_e + "','YYYYMMDDHH24MISS')) A," +
			" SYS_DEPT B," +
			" SYS_COST_CENTER C," +
			" (SELECT ID, CHN_DESC" +
			" FROM SYS_DICTIONARY" +
			" WHERE GROUP_ID = 'SYS_CHARGE') D" +
			" WHERE A.DEPT_CODE = B.DEPT_CODE(+) " +
			" AND A.EXE_DEPT_CODE = C.COST_CENTER_CODE(+) " +
			" AND A.REXP_CODE = D.ID " +
			" GROUP BY A.BILL_DATE, B.DEPT_CHN_DESC, C.COST_CENTER_CHN_DESC, D.CHN_DESC" +
			" ORDER BY A.BILL_DATE, B.DEPT_CHN_DESC, C.COST_CENTER_CHN_DESC, D.CHN_DESC";
		TParm result = new TParm( TJDODBTool.getInstance().select(sql));
		if(result.getErrCode() < 0){
			return;
		}
		this.table.setParmValue(result);
	}
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "住院收入明细表");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
	}
}
