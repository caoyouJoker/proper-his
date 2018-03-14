package com.javahis.ui.spc.util;

import java.util.Date;



import com.dongyang.config.TConfigParm;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;

/**
 * IND_QTYCHECK »ØÐ´IND_STOCK µÄstock_qty
 * @author Administrator
 *
 */
public class UpdatePhaBaseOrderCodeUtil {
   
	public static final String FILE_NAME = "C:/temp/newTemp.txt";
	public static void main(String args[]) throws Exception {

		String configDir = "C:\\Program Files\\Apache Software Foundation\\Tomcat 5.5\\webapps\\web\\";
		TDBPoolManager.getInstance().init(configDir + "WEB-INF\\config\\system\\");
		TConfigParm parm1 = new TConfigParm();
		parm1.setSystemGroup("");
		if(!configDir.endsWith("\\"))
	            configDir += "\\";
	    configDir += "WEB-INF\\config\\";
	    String dir = configDir + "system\\";
		parm1.setConfig(dir+"TDBInfo.x");
		parm1.setConfigClass(dir+"TClass.x");
		TDBPoolManager m = new TDBPoolManager();
		m.init(parm1);
		
		TConnection c = m.getConnection("javahis");
		String orderString = "";
		UpdatePhaBaseOrderCodeUtilXlsMain xlsMain = new UpdatePhaBaseOrderCodeUtilXlsMain();
		TParm locParm = xlsMain.readXls();	
		int numb = 1;
		if(null != locParm ){
			int count = locParm.getCount("ORDER_CODE");
			System.out.println("count: "+count);
			for(int i = 0 ; i < count ; i++){
				String orderCode  = (String) locParm.getValue("ORDER_CODE", i);
				String orderCode1    = (String) locParm.getValue("ORDER_CODE1", i);
				String sql = " SELECT  ORDER_CODE FROM PHA_BASE  WHERE  ORDER_CODE='" + orderCode + "' ";
				TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				if(null != parm && parm.getCount() > 0){
					
					System.out.println("orderCode------------:"+orderCode+",,,,ORDER_CODE=1=======:"+orderCode1);
					//IND_STOCKM
				     String updateSql = "update PHA_BASE set SPC_MEDICINE_CODE=" + orderCode1
				     + " where   ORDER_CODE='" + orderCode + "' ";
				     TJDODBTool.getInstance().update(updateSql);
				
				}
			}
		}
		c.commit();
		c.close();

	}
}
