package jdo.sys;

import com.dongyang.data.TParm;

public class SysReplaceMedicineSQL {
	/**
	 * 根据药品编号和替代药品编号查询
	 * 
	 * @param batch_flg
	 * @return String
	 */
	public static String onQueryByCode(TParm parm) {
		return "SELECT ORDER_CODE,REPLACE_ORDER_CODE,SEQ,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM FROM  PHA_REDRUG "
				+ "WHERE ORDER_CODE='"
				+ parm.getData("ORDER_CODE")
				+ "'  AND REPLACE_ORDER_CODE='"
				+ parm.getData("REPLACE_ORDER_CODE")+"'";
	}
}
