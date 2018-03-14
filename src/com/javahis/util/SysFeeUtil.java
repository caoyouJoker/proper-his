package com.javahis.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class SysFeeUtil {

	public static TParm getExaData(String orderCode){
		TParm result = new TParm();
		TParm parm = new TParm(TJDODBTool.getInstance().select(
	            "SELECT A.*,OPD_CHARGE_CODE FROM SYS_FEE A,SYS_CHARGE_HOSP B "+
		        "WHERE A.CHARGE_HOSP_CODE=B.CHARGE_HOSP_CODE "+
		        "AND A.ORDER_CODE='" + orderCode + "' AND ACTIVE_FLG = 'Y' "));
        if (parm.getErrCode() < 0 || parm.getCount() <= 0)
        	return result;
	       
	    result = parm.getRow(0);
		
		return result;
	}	
}
