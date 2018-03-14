package com.javahis.ui.clp;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: 手术患者明细查询</p>
 *
 * <p>Description:手术患者明细查询 </p>
 *
 * <p>Copyright: Copyright (c) blueCore</p>
 *
 * <p>Company: blueCore</p>
 *
 * @author caowl 20121224
 * @version 1.0
 */
public class CLPOperatorQueryControl extends TControl{
	
	// 开始时间
	private TTextFormat start_date;
	// 结束时间
	private TTextFormat end_date;
	// 表格
	private TTable table;

	public CLPOperatorQueryControl() {

	}
	/***
	 * 初始化
	 * */
	 public void onInit() {
	        super.onInit();
	        initPage();	       	      
	      
	    }
	 /**
	  * 初始化页面
	  * */
	 public void initPage(){
		 this.callFunction("UI|CLPTABLE|removeRowAll");
			initControl();
	 }
	/**
	 * 初始化控件
	 * */ 
	 public void initControl(){
		    Timestamp date = StringTool.getTimestamp(new Date());
			// 初始化时间查询区间
			this.setValue("e_Date", date.toString().substring(0, 10).replace('-',
					'/')
					+ " 23:59:59");
			this.setValue("s_Date", StringTool.rollDate(date, -7).toString()
					.substring(0, 10).replace('-', '/')
					+ " 00:00:00");
			table = (TTable) this.getComponent("Table");
			start_date = (TTextFormat) this.getComponent("s_Date");
			end_date = (TTextFormat) this.getComponent("e_Date");									
	 }
	 
	 /**
	  * 查询
	  * */
	 public TParm onQuery(){
		 TParm selParm = new TParm();
		 if (!checkData()) {
			return selParm;
		}
		//所在科别
		String deptSelect = "";
		String dept_code = this.getValueString("DEPT_CODE");
		if(dept_code != null && !dept_code.equals("")){
			deptSelect  = " AND B.DEPT_CODE = '"+dept_code+"'";
		}	
		// 得到手术日期查询条件
		String selectCondition = "";
		String startDate = this.start_date.getValue().toString();
		String endDate = this.end_date.getValue().toString();
		if (this.checkNullAndEmpty(startDate) && this.checkNullAndEmpty(endDate)) {
			startDate = SystemTool.getInstance().getDateReplace(startDate, true).toString();
			selectCondition += " AND D.OP_DATE BETWEEN TO_DATE('" + startDate
					+ "','YYYYMMDDHH24MISS') ";
			endDate = SystemTool.getInstance().getDateReplace(endDate, false).toString();
			selectCondition += " AND TO_DATE('" + endDate
			+ "','YYYYMMDDHH24MISS') ";
		}
		 String sql = "SELECT DISTINCT A.MR_NO,A.CASE_NO,A.PAT_NAME, CASE WHEN A.SEX='1' THEN '男'  ELSE '女' END AS SEX,A.BIRTH_DATE," +
		 		" A.AGE,A.IN_DIAG_CODE ,(SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE = B.DEPT_CODE) AS DEPT_CODE,C.ICD_CHN_DESC," +
		 		" D.OP_CODE,D.OP_DESC,(SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = D.MAIN_SUGEON) AS MAIN_SUGEON,(SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = D.AST_DR1) AS AST_DR1,(SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = D.AST_DR2) AS AST_DR2," +
		 		" (SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID = D.ANA_DR) AS ANA_DR,S.CHN_DESC AS ANA_WAY"+
				" FROM MRO_RECORD A,ADM_INP B,SYS_DIAGNOSIS C, MRO_RECORD_OP D,SYS_DICTIONARY S"+
				" WHERE A.CASE_NO = B.CASE_NO"+
				" AND D.ANA_WAY = S.ID "+
				" AND S.GROUP_ID = 'OPE_ANAMETHOD' "+
				" AND A.CASE_NO = D.CASE_NO"+
				" AND A.IN_DIAG_CODE = C.ICD_CODE"+
				" AND A.CLNCPATH_CODE IS NOT NULL"+deptSelect+selectCondition;
		// System.out.println("sql"+sql);
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (selParm == null || selParm.getCount() == 0) {
		    this.messageBox("查无数据");
		    return selParm;
		}
		table.setParmValue(selParm);
		return selParm;
	 }
    
	
	/**
	 * 清空
	 */
	public void onClear() {
		initControl();
		table.removeRowAll();
	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		if (table.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(table, "手术患者明细统计报表");
		}
	}
	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		String start = this.getValueString("s_Date");
		if (start == null || start.length() <= 0) {
			this.messageBox("开始时间不能为空");
			return false;
		}
		String end = this.getValueString("e_Date");
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
