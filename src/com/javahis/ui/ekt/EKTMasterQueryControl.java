package com.javahis.ui.ekt;

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
 * Title: 发卡原因
 * </p>
 * 
 * <p>
 * Description: 发卡原因
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author zhangpeng
 *
 */
public class EKTMasterQueryControl extends TControl {
	private static TTable table;
	/**
	 * 初始化方法
	 */
	public void onInit() {
		table = (TTable) getComponent("TABLE");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("DATE", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
	}
	/**
	 * 查询
	 */
	public void onQuery() {
		String sql = " SELECT SUM (CURRENT_BALANCE) CURRENT_BALANCE"
				+ " FROM EKT_ISSUELOG A, EKT_MASTER B"
				+ " WHERE A.CARD_NO = B.CARD_NO AND A.WRITE_FLG = 'Y'";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Timestamp date = StringTool.getTimestamp(new Date());
		TParm parm = new TParm();
		parm.addData("C1", "截止时间");
		parm.addData("C2", date.toString().substring(0, 19)
				.replaceAll("-", "/"));
		parm.addData("C1", "医疗卡总余额");
		parm.addData("C2", StringTool.round(result.getDouble("CURRENT_BALANCE", 0), 2));
		table.setParmValue(parm);
	}
	/**
	 * 清空
	 */
	public void onClear(){
		table.removeRowAll();
	}
	
	/**
	 * 汇出Excel
	 */
	public void onExport() {
		table.acceptText();
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount("C1") <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "医疗卡总余额");
	}
	
	

}
