package com.javahis.ui.spc.util;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jdo.clp.intoPathStatisticsTool;

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
public class UpdateLocCodeUtil {
   
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
	    UpdateLocCodeXlsMain xlsMain = new UpdateLocCodeXlsMain();
		TParm locParm = xlsMain.readXls();	
		int numb = 1;
		if(null != locParm ){
			int count = locParm.getCount("ORDER_CODE");
			System.out.println("count: "+count);
			for(int i = 0 ; i < count ; i++){
				String orderCode  = (String) locParm.getValue("ORDER_CODE", i);
				String orgCode    = (String) locParm.getValue("ORG_CODE", i);
				String locCode    = (String) locParm.getValue("LOC_CODE", i);
				String eleCode    = (String) locParm.getValue("ELETAG_CODE", i);
				String sql = " SELECT  ORG_CODE,ORDER_CODE, MATERIAL_LOC_CODE, ELETAG_CODE FROM IND_STOCKM "
					+ " WHERE ORG_CODE='" + orgCode + "' AND ORDER_CODE='" + orderCode + "' ";
				TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				if(null != parm && parm.getCount() > 0){
					
					System.out.println("ORG_CODE------------:"+orgCode+"ORDER_CODE========:"+orderCode);
					//IND_STOCKM
				     String updateSql = "update IND_STOCKM set MATERIAL_LOC_CODE='" + locCode+ "',ELETAG_CODE='" +eleCode + "',MATERIAL_LOC_DESC='" + locCode + "' "
				     + " where ORG_CODE='" + orgCode + "' and ORDER_CODE='" + orderCode + "' ";
				     TJDODBTool.getInstance().update(updateSql);
				     
				     //IND_STOCK
				     String updateSqlSTOCK = "update IND_STOCK set MATERIAL_LOC_CODE='" + locCode+ "'  "
				     + " where ORG_CODE='" + orgCode + "' and ORDER_CODE='" + orderCode + "' ";
				     TJDODBTool.getInstance().update(updateSqlSTOCK);
				}
			}
		}
		c.commit();
		c.close();

	}
}
