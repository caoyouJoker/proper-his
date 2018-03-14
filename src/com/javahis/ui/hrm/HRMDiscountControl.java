package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: 健康检查折扣查询
 * </p>
 * 
 * <p>
 * Description: 健康检查折扣查询
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
 * @author caowl 2012.06.20
 * @version 1.0
 */

public class HRMDiscountControl extends TControl{

	public void onInit() {
		super.onInit();
		initPage();
	}
	
	/**
	 * 初始化界面
	 * 
	 */
	private void initPage() {
		
		// 初始化病案号和姓名为空
		this.setValue("COMPANY_DESC", "");
		this.setValue("CONTRACT_CODE", "");
		this.setValue("PACKAGE_CODE", "");

		// 移除表中所有数据
		this.callFunction("UI|Table|removeRowAll");
		// 初始化表
		TTable table = (TTable) this.getComponent("Table");

	}
	/**
	 * 查询
	 * */
	public void onQuery() {
				
		TParm selParm = new TParm();
		String dWhere = "";
		if (getValue("COMPANY_DESC") != null) {
			if (getValue("COMPANY_DESC").toString().length() != 0)
				dWhere = " AND C.COMPANY_DESC = '"+this.getValueString("COMPANY_DESC")+"' ";
		}
		
		if (getValue("CONTRACT_CODE") != null) {
			if (getValue("CONTRACT_CODE").toString().length() != 0)
				dWhere += " AND B.CONTRACT_CODE = '"+this.getValueString("CONTRACT_CODE")+"' ";
		}
		
		if (getValue("PACKAGE_CODE") != null) {
			if (getValue("PACKAGE_CODE").toString().length() != 0)
				dWhere += " AND B.PACKAGE_CODE= '"+this.getValueString("PACKAGE_CODE")+"' ";
		}
		
		String sql = "SELECT A.MR_NO,B.PAT_NAME,A.TOT_OWN,(TOT_OWN-B.DISCNT*TOT_OWN) TOT_DISCNT,(B.DISCNT*TOT_OWN) TOT_REAL " +
				"FROM (SELECT DISTINCT MR_NO,SUM(OWN_PRICE) TOT_OWN " +
					   "FROM HRM_ORDER GROUP BY MR_NO) A," +
					   "HRM_CONTRACTD B," +
					   "HRM_COMPANY C " +
					   "WHERE A.MR_NO = B.MR_NO " +
					   "AND B.COMPANY_CODE = C.COMPANY_CODE " +
					   dWhere+
					   "ORDER BY A.MR_NO";
		
		
		selParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		
		if (selParm.getCount("MR_NO") < 1) {
			// 查无数据
			this.messageBox("E0008");
			this.initPage();			
		}				
		this.callFunction("UI|Table|setParmValue", selParm);
	}
	
	
	/**
	 * 清空
	 * */
	public void onClear() {
		initPage();
		this.clearValue("COMPANY_DESC;CONTRACT_CODE;PACKAGE_CODE");
	}
	
}
