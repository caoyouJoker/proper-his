package com.javahis.ui.ind;

import jdo.ind.INDSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;

import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:比较IND_VERIFYIND与IND_ACCOUNT
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shendr 2013.06.27
 * @version 1.0
 */
public class SPCCompareTwoTableControl extends TControl {

	/* Table控件 */
	private TTable table_count;

	/**
	 * 获取TTable控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * 获取TTextFormat控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTextFormat getTTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

	/**
	 * 初始化
	 */
	public void onInit() {
		table_count = this.getTTable("TABLE_COUNT");
		// 初始化查询区间 默认加载本月结算时间
		String sql = INDSQL.getColseDate();
		TParm dateParm = new TParm(TJDODBTool.getInstance().select(sql));
		StringBuffer date = new StringBuffer(dateParm.getData("CLOSE_DATE", 0)
				.toString());
		date.insert(4, "/");
		date.insert(7, "/");
		String start_date = date + " 00:00:00";
		String end_date = date + " 23:59:59";
		this.setValue("START_DATE", start_date);
		this.setValue("END_DATE", end_date);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String START_DATE = getValueString("START_DATE");
		String END_DATE = getValueString("END_DATE");
		if (!StringUtil.isNullString(START_DATE)) {
			START_DATE = START_DATE.substring(0, 19);
		}
		if (!StringUtil.isNullString(END_DATE)) {
			END_DATE = END_DATE.substring(0, 19);
		}
		String sql = INDSQL.getTwoTable(START_DATE, END_DATE);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		table_count.setParmValue(result);
		getTTextFormat("START_DATE").setEnabled(false);
		getTTextFormat("END_DATE").setEnabled(false);
	}

}
