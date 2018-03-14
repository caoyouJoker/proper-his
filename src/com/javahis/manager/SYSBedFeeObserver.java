package com.javahis.manager;

import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: 床位费用设定观察者
 * </p>
 * 
 * <p>
 * Description: 床位费用设定观察者
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author zhangy 2009.06.04
 * @version 1.0
 */
public class SYSBedFeeObserver extends TObserverAdapter {
	public SYSBedFeeObserver() {

	}

	// 获得prefech本地数据
	TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	/**
	 * 
	 * @param s
	 *            根据的ORDER_CODE
	 * @param colName
	 *            要查的列名
	 * @return String
	 */
	public String getTableShowValue(String s, String colName) {
		if (dataStore == null)
			return s;
		String bufferString = dataStore.isFilter() ? dataStore.FILTER
				: dataStore.PRIMARY;
		TParm parm = dataStore.getBuffer(bufferString);
		Vector vKey = (Vector) parm.getData("ORDER_CODE");
		Vector vDesc = (Vector) parm.getData(colName);
		int count = vKey.size();
		for (int i = 0; i < count; i++) {
			if (s.equals(vKey.get(i)))
				return "" + vDesc.get(i);
		}
		return s;
	}

	/**
	 * 得到其他列数据
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @return Object
	 */
	public Object getOtherColumnValue(TDS ds, TParm parm, int row, String column) {
		if ("ORDER_DESC".equals(column)) {
			String orderCode = parm.getValue("ORDER_CODE", row);
			String icd_name = getTableShowValue(orderCode, "ORDER_DESC");
			if ("".equals(icd_name))
				return "";
			// 给这个列一个值
			return icd_name;
		}
		return "";
	}

	/**
	 * 设置其他列数据
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int 激发该动作的行(table)
	 * @param column
	 *            String 激发该动作的列(table)
	 * @param value
	 *            Object
	 * @return boolean 该列目前的值
	 */
	public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
			String column, Object value) {
		return true;
	}
}
