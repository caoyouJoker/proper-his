package com.javahis.manager;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TObserverAdapter;

public class OdiClnDiagObserver extends TObserverAdapter {
	/**
	 * 得到未知行值
	 * 
	 * @param ds
	 *            TDS
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @return Object
	 */
	public Object getOtherColumnValue(TDS ds, TParm parm, int row, String column) {
		if ("ICD_NAME".equals(column)) {
			
			String icd_name = parm.getValue("ICD_CHN_DESC");
			if ("".equals(icd_name))
				return "";
			// 给这个列一个值
			return icd_name;
		}
		return "";
	}

	/**
	 * 设置未知行值
	 * 
	 * @param ds
	 *            TDS
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @param column
	 *            String
	 * @param value
	 *            Object
	 * @return boolean
	 */
	public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
			String column, Object value) {
		if ("ICD_NAME".equals(column)) {
			ds.setItem(row, "ICD_CODE", parm.getData("ICD_CODE"));
		}
		return true;
	}
}
