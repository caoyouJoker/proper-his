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
public class UpdateStockMQtyUtil {
   
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
	    UpdateStockMQtyUtilXlsMain xlsMain = new UpdateStockMQtyUtilXlsMain();
		TParm locParm = xlsMain.readXls();	
		int numb = 1;
		if(null != locParm ){
			int count = locParm.getCount("ORDER_CODE");
			System.out.println("count: "+count);
			for(int i = 0 ; i < count ; i++){
				String orderCode  = (String) locParm.getValue("ORDER_CODE", i);
				String orgCode    = (String) locParm.getValue("ORG_CODE", i);
				String maxQty    = (String) locParm.getValue("MAX_QTY", i);
				String safQty    = (String) locParm.getValue("SAFE_QTY", i);
				String minQty    = (String) locParm.getValue("MIN_QTY", i);
				String ecoQty    = (String) locParm.getValue("ECONOMICBUY_QTY", i);
				String sql = " SELECT  ORG_CODE,ORDER_CODE FROM IND_STOCKM "
					+ " WHERE ORG_CODE='" + orgCode + "' AND ORDER_CODE='" + orderCode + "' ";
				TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
				if(null != parm && parm.getCount() > 0){
					
					System.out.println("ORG_CODE------------:"+orgCode+",,ORDER_CODE========:"+orderCode);
					//IND_STOCKM
				     String updateSql = "update IND_STOCKM set MAX_QTY=" + maxQty+ ",SAFE_QTY=" +safQty + ",MIN_QTY=" + minQty + ",ECONOMICBUY_QTY="+ecoQty
				     + " where ORG_CODE='" + orgCode + "' and ORDER_CODE='" + orderCode + "' ";
				     TJDODBTool.getInstance().update(updateSql,c);
				
				}
			}
		}
		c.commit();
		c.close();

	}
}
