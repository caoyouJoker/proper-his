package com.javahis.manager;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.ind.INDSQL;
import jdo.ind.IndStockDTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 药库部门安全库存量设定观察者
 * </p>
 * 
 * <p>
 * Description: 药库部门安全库存量设定观察者
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class IndMainBatchValidObserver extends TObserverAdapter {

	Map map = new HashMap();

	public IndMainBatchValidObserver() {

	}

	// 获得prefech本地数据
	TDataStore dataStore = TIOM_Database.getLocalTable("PHA_BASE");

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
		if ("DOSAGE_UNIT".equals(column)) {
			String orderCode = parm.getValue("ORDER_CODE", row);
			String dosage_unit = getTableShowValue(orderCode, "DOSAGE_UNIT");
			if ("".equals(dosage_unit))
				return "";
			// 给这个列一个值
			return dosage_unit;
		}
		if ("SUM_STOCK_QTY".equals(column)) {
			String orderCode = parm.getValue("ORDER_CODE", row);
			String org_code = parm.getValue("ORG_CODE", row);
			double sum_stock_qty = 0;
			if (map.containsKey(orderCode + "|" + org_code)) {
				sum_stock_qty = TypeTool.getDouble(map.get(orderCode + ":"
						+ org_code));
				return sum_stock_qty;
			} else {
				TParm newparm = new TParm();
				newparm.setData("ORDER_CODE", orderCode);
				newparm.setData("ORG_CODE", org_code);
				TParm result = IndStockDTool.getInstance().onQueryStockQTY(
						newparm);
				if (result == null) {
					map.put(orderCode + "|" + org_code, sum_stock_qty);
					return sum_stock_qty;
				}
				sum_stock_qty = result.getDouble("SUM(STOCK_QTY)");
				map.put(orderCode + "|" + org_code, sum_stock_qty);
				return sum_stock_qty;
			}
		}
		if ("ACTIVE_FLG_M".equals(column)) {
			String order_code = parm.getValue("ORDER_CODE", row);
			String org_code = parm.getValue("ORG_CODE", row);
			TParm result = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDStockM(org_code, order_code)));
			String active_flg_m = result.getValue("ACTIVE_FLG", 0);
			if ("".equals(active_flg_m))
				return "N";
			// 给这个列一个值
			return active_flg_m;
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
		if (column.equals("ORDER_DESC")) {
			ds.setItem(row, "ORDER_DESC", parm.getData("ORDER_DESC"));
			return true;
		}
		if (column.equals("DOSAGE_UNIT")) {
			ds.setItem(row, "DOSAGE_UNIT", parm.getData("DOSAGE_UNIT"));
			return true;
		}
		if (column.equals("SUM_STOCK_QTY")) {
			ds.setItem(row, "SUM_STOCK_QTY", parm.getData("SUM_STOCK_QTY"));
			return true;
		}
		if (column.equals("ACTIVE_FLG_M")) {
			ds.setItem(row, "ACTIVE_FLG_M", parm.getData("ACTIVE_FLG_M"));
			return true;
		}
		return true;
	}
}
