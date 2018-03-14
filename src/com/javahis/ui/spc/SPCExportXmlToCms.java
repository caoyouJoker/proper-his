package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.spc.SPCExportXmlToCmsTool;
import jdo.spc.SPCSysFeeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.dom4j.Document;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

public class SPCExportXmlToCms extends TControl {

	public TTable table;
	
	
	/**
	 * 初始化方法
	 */
	public void onInit() {
		Timestamp date = SystemTool.getInstance().getDate();
		
		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		cal1.setTime(date);
		cal2.setTime(date);
		
		cal2.roll(Calendar.MONTH, -1);
		// 初始化查询区间
		
//		cal.get(Calendar.m)
		String startStr = cal1.get(Calendar.YEAR) + "/" + formateMonth((cal2.get(Calendar.MONTH)+1)) + "/26" + " 00:00:00";
		String endStr = cal1.get(Calendar.YEAR) + "/" + formateMonth((cal1.get(Calendar.MONTH)+1)) + "/25" + " 23:59:59";

		// 初始化查询区间
		this.setValue("END_DATE", endStr);
		this.setValue("START_DATE", startStr);
		setValue("REGION_CODE", Operator.getRegion());
		
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		
		table = this.getTable("TABLE");
		
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		
		String sql = getIbsOrddSql();
		
		System.out.println("SQL--------------:"+sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("没有查询数据");
			table.removeRowAll();
			return;
		}
		
		TParm newParm = new TParm() ;
		 
		double sumTotAmt =  0 ;
		int count = result.getCount() ;
		double totAmt = 0 ;
		double oddAmt = 0 ;
		for(int i = 0;  i < count ; i++){
			TParm rowParm = result.getRow(i);
			double lastOdd = rowParm.getDouble("LAST_ODD");
			double qty = rowParm.getDouble("QTY");
			double currentQty = qty - lastOdd ;
			newParm.setData("CURRENT_QTY",i,currentQty);
			newParm.setData("QTY",i,qty);
			newParm.setData("VERIFYIN_PRICE",i,StringTool.round(rowParm.getDouble("VERIFYIN_PRICE"),4));
			
			try{
				totAmt = qty*rowParm.getDouble("VERIFYIN_PRICE");
				
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("总量乘以采购单价出错");
				totAmt = 0;
			}
			sumTotAmt += totAmt ;
			newParm.setData("TOT_AMT",i,StringTool.round(totAmt,2));
			newParm.setData("ORDER_CODE",i,rowParm.getValue("ORDER_CODE"));
			newParm.setData("ORDER_DESC",i,rowParm.getValue("ORDER_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("SPECIFICATION"));
			newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("LAST_ODD",i,lastOdd);
			newParm.setData("DOSAGE_QTY",i,rowParm.getDouble("DOSAGE_QTY"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("DOSAGE_UNIT"));
			newParm.setData("ODD",i,rowParm.getDouble("ODD"));
			
			double odd = rowParm.getDouble("ODD");
			oddAmt = odd * rowParm.getDouble("VERIFYIN_PRICE");
			newParm.setData("ODD_AMT",i,StringTool.round(oddAmt,2));
			newParm.setData("DELIVERYCODE",i,rowParm.getValue("DELIVERYCODE"));
			newParm.setData("CSTCODE",i,rowParm.getValue("CSTCODE"));
		}
		
		//设置总计
		
		/**
		newParm.setData("CURRENT_QTY",count,"");
		newParm.setData("QTY",count,"");
		newParm.setData("VERIFYIN_PRICE",count,"");
		 
		newParm.setData("TOT_AMT",count,StringTool.round(sumTotAmt,2));
		newParm.setData("ORDER_CODE",count,"总计");
		newParm.setData("ORDER_DESC",count,"");
		newParm.setData("SPECIFICATION",count,"");
		newParm.setData("UNIT_CHN_DESC",count,"");
		newParm.setData("LAST_ODD",count,"");
		newParm.setData("DOSAGE_QTY",count,"");
		newParm.setData("DOSAGE_UNIT",count,"");
		newParm.setData("ODD",count,"");
		newParm.setData("DELIVERYCODE",count,"");
		newParm.setData("CSTCODE",count,"");
		*/
		setValue("SUM_TOT_AMT", sumTotAmt);
		setValue("COUNT", count);
		table.setParmValue(newParm);
	}

	private String getSearchSql() {
		String start_date = getValueString("START_DATE");
		start_date = start_date.substring(0, 4)
				+ start_date.substring(5, 7) + start_date.substring(8, 10)
				+ start_date.substring(11, 13)
				+ start_date.substring(14, 16)
				+ start_date.substring(17, 19);
		String end_date = getValueString("END_DATE");
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
		String isDrug = getValueString("EXCLUDE_DRUG");
		
		
		String orgCode = "040103" ;
		
		String orderCode = getValueString("ORDER_CODE");
		//调配
		String odiSql = getOdiSql(start_date, end_date, isDrug);
		
		//调配退药
		String odiRtnSql = getOdiRtnSql(start_date, end_date, isDrug);
		
		//手术室，介入室
		String dispenseSql = getDispenseSql(start_date, end_date, isDrug);
		
		String indOddSql = getIndOddSql(orgCode);
		
		String sql =" SELECT '932290201' AS DELIVERYCODE,'18' AS CSTCODE,E.ORDER_CODE,F.ORDER_DESC,F.SPECIFICATION,  "+
					"  E.DOSAGE_QTY AS QTY,I.UNIT_CHN_DESC,FLOOR(E.DOSAGE_QTY/G.DOSAGE_QTY/G.STOCK_QTY) AS DOSAGE_QTY, "+
					"  H.UNIT_CHN_DESC AS DOSAGE_UNIT,MOD(E.DOSAGE_QTY,G.DOSAGE_QTY/G.STOCK_QTY) AS ODD,TO_CHAR(SYSDATE,'YYYYMMDD')||'040103' AS PURCHASEID , "+
					"  (SELECT A.ODD_QTY FROM IND_ODD A " +
					"    WHERE A.ORG_CODE='040103' AND A.CLOSE_DATE=(SELECT MAX(B.CLOSE_DATE) FROM IND_ODD B WHERE B.ORG_CODE=A.ORG_CODE AND B.ORDER_CODE=A.ORDER_CODE  ) AND A.ORDER_CODE=E.ORDER_CODE ) AS LAST_ODD, " +
					"   ( SELECT MAX(A.VERIFYIN_PRICE) FROM IND_STOCK A WHERE A.ORG_CODE='"+orgCode+"' AND A.ORDER_CODE=E.ORDER_CODE " +
					"    ) AS VERIFYIN_PRICE "+
					" FROM (   SELECT D.ORDER_CODE,SUM(D.DOSAGE_QTY) AS DOSAGE_QTY "+
					"	       FROM ( "+
						        odiSql    +
                    "   		UNION ALL  "+
                    			odiRtnSql+
                    "			UNION ALL "+
                   			    dispenseSql+
                    "         	UNION ALL	"+
                    			indOddSql +
                    " ) D GROUP BY D.ORDER_CODE  ORDER BY D.ORDER_CODE ) " +
                    " E,PHA_BASE F,PHA_TRANSUNIT G,SYS_UNIT H,SYS_UNIT I   "+ 
                    " WHERE F.ORDER_CODE=E.ORDER_CODE "+
                    "	AND G.ORDER_CODE=E.ORDER_CODE "+
                    "   AND H.UNIT_CODE=F.STOCK_UNIT "+
                    "   AND I.UNIT_CODE=F.DOSAGE_UNIT  ";
		if(orderCode != null && !orderCode.equals("")){
			sql += " AND E.ORDER_CODE='"+orderCode+"' ";
		}
		return sql;
	}
	
	/**
	 * 得到调配的SQL
	 * @param start_date
	 * @param end_date
	 * @param isDrug
	 * @return
	 */
	public String getOdiSql(String start_date,String end_date,String isDrug){
		String sql = "" ;
		//普药/麻精
		if(isDrug != null && isDrug.equals("N")){
			sql = " SELECT A.ORDER_CODE,A.DOSAGE_QTY "+
				  "			FROM ODI_DSPNM A  "+
				  "			WHERE A.PHA_DOSAGE_DATE BETWEEN TO_DATE ( '"+start_date+"' , 'YYYYMMDDHH24MISS')"+
				  "                    AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') ";
		}else{
			sql = "  (  SELECT A.ORDER_CODE,A.DOSAGE_QTY "+
			 	  "	  FROM ODI_DSPNM A ,SYS_CTRLDRUGCLASS CT "+
			      "	  WHERE A.PHA_DOSAGE_DATE BETWEEN TO_DATE ( '"+start_date+"' , 'YYYYMMDDHH24MISS') "+
			      "         AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') "+
			      "         AND A.CTRLDRUGCLASS_CODE IS NOT NULL AND A.CTRLDRUGCLASS_CODE=CT.CTRLDRUGCLASS_CODE  AND CT.CTRL_FLG='N'    " +
			      "   UNION ALL " +
			      "   SELECT A.ORDER_CODE,A.DOSAGE_QTY "+
				  "			FROM ODI_DSPNM A  "+
				  "			WHERE A.PHA_DOSAGE_DATE BETWEEN TO_DATE ( '"+start_date+"' , 'YYYYMMDDHH24MISS')"+
				  "                    AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') " +
				  "          AND A.CTRLDRUGCLASS_CODE IS NULL  ) ";
		}
		
	    return sql;
	}
	
	/**
	 * 手术室与介入室SQL (普药、麻精)
	 * @param start_date
	 * @param end_date
	 * @param isDrug
	 * @return
	 */
	public String getDispenseSql(String start_date,String end_date,String isDrug){
		
		String sql = "";
		if(isDrug != null && isDrug.equals("N")){
			   sql = "   SELECT B.ORDER_CODE,B.ACTUAL_QTY * C.DOSAGE_QTY * C.STOCK_QTY AS DOSAGE_QTY "+ 
                     "   FROM IND_DISPENSEM A, IND_DISPENSED B,PHA_TRANSUNIT C,PHA_BASE D"+
                     "	 WHERE A.UPDATE_FLG = '3' AND "+
                     "         A.REQTYPE_CODE IN ('EXM','TEC') AND "+
                     "		   (A.APP_ORG_CODE = '030503' OR A.APP_ORG_CODE = '0306')  AND "+
                     "         A.DISPENSE_DATE BETWEEN TO_DATE ('"+start_date+"','YYYYMMDDHH24MISS') AND "+
                     "         TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS')  AND "+
                     "         A.DISPENSE_NO = B.DISPENSE_NO AND "+
                     "         B.ORDER_CODE=C.ORDER_CODE " ;
		}else{
			sql = "   SELECT B.ORDER_CODE,CASE WHEN B.UNIT_CODE=D.STOCK_UNIT THEN B.ACTUAL_QTY*C.DOSAGE_QTY*C.STOCK_QTY ELSE B.ACTUAL_QTY END AS DOSAGE_QTY "+ 
                  "   FROM IND_DISPENSEM A, IND_DISPENSED B,PHA_TRANSUNIT C,PHA_BASE D "+
                  "	  WHERE ( A.UPDATE_FLG = '3' OR A.UPDATE_FLG = '1' ) AND "+
                  "         A.REQTYPE_CODE IN ('EXM','TEC') AND "+
                  "		   ( A.APP_ORG_CODE = '030503' OR A.APP_ORG_CODE = '0306' )  AND "+
                  "         A.DISPENSE_DATE BETWEEN TO_DATE ('"+start_date+"','YYYYMMDDHH24MISS') AND "+
                  "         TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS')  AND "+
                  "         A.DISPENSE_NO = B.DISPENSE_NO AND "+
                  "         B.ORDER_CODE=C.ORDER_CODE AND "+
                  "         B.ORDER_CODE=D.ORDER_CODE  AND "+
                  "         ( D.CTRLDRUGCLASS_CODE IS NULL OR  " +
            	  "           D.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) " ;
		}
		
		return sql ;
		
	}
	
	/**
	 * 调配退药SQL
	 * @param start_date
	 * @param end_date
	 * @param isDrug
	 * @return
	 */
	public String getOdiRtnSql(String start_date,String end_date,String isDrug){
		String sql = "" ;
		
		//全部、普药
		if(isDrug != null && isDrug.equals("N")){
			sql = "   SELECT A.ORDER_CODE,-1 * A.RTN_DOSAGE_QTY AS DOSAGE_QTY "+
                  "   FROM ODI_DSPNM A "+
                  "	  WHERE A.DSPN_DATE BETWEEN TO_DATE ( '"+start_date+"' , 'YYYYMMDDHH24MISS')"+
                  "         AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS')"+
                  "         AND DSPN_KIND='RT' " ;
		}else{
			sql = "    SELECT A.ORDER_CODE,-1 * A.RTN_DOSAGE_QTY AS DOSAGE_QTY "+
            	  "	   FROM ODI_DSPNM A ,PHA_BASE B "+
            	  "	   WHERE A.DSPN_DATE BETWEEN TO_DATE ( '"+start_date+"' , 'YYYYMMDDHH24MISS')"+
            	  "                    AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS')  AND  "+
            	  "          A.DSPN_KIND='RT' AND " +
            	  "          A.ORDER_CODE=B.ORDER_CODE AND " +
            	  "         ( B.CTRLDRUGCLASS_CODE IS NULL OR  " +
            	  "           B.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) " ;
			
		}
		return sql;
	}
	
	/**
	 * 暂估出库SQL
	 * @param orgCode
	 * @return
	 */
	public String getIndOddSql(String orgCode){
		String end_date = getValueString("END_DATE") ;
		String month = end_date.substring(5,7);
		month = String.valueOf(Integer.parseInt(month)-1 );
		if(month.length()< 2){
			month = "0"+month ;
		}
		String closeDate = end_date.substring(0, 4)+ month + end_date.substring(8, 10);
		String sql = "";
		sql =  "         SELECT A.ORDER_CODE,A.ODD_QTY AS DOSAGE_QTY  " +
        	   "         FROM IND_ODD A  "+
               "         WHERE A.ORG_CODE='"+orgCode+"' "+
               "         AND A.CLOSE_DATE='"+closeDate+"'";
		System.out.println("indOddSql----:"+sql);
		return sql ;
	}
	
	
	public String getIbsOrddSql(){
		String start_date = getValueString("START_DATE");
		start_date = start_date.substring(0, 4)
				+ start_date.substring(5, 7) + start_date.substring(8, 10)
				+ start_date.substring(11, 13)
				+ start_date.substring(14, 16)
				+ start_date.substring(17, 19);
		String end_date = getValueString("END_DATE");
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
 
		String orgCode = "040103" ;
		
		String orderCode = getValueString("ORDER_CODE");
		
		String indOddSql = getIndOddSql(orgCode);
		
		String sql =" SELECT '932290201' AS DELIVERYCODE,'18' AS CSTCODE,E.ORDER_CODE,F.ORDER_DESC,F.SPECIFICATION,  "+
					"  E.DOSAGE_QTY AS QTY,I.UNIT_CHN_DESC,FLOOR(E.DOSAGE_QTY/G.DOSAGE_QTY/G.STOCK_QTY/F.CONVERSION_RATIO) AS DOSAGE_QTY, "+
					"  H.UNIT_CHN_DESC AS DOSAGE_UNIT,MOD(E.DOSAGE_QTY,G.DOSAGE_QTY/G.STOCK_QTY/F.CONVERSION_RATIO) AS ODD,TO_CHAR(SYSDATE,'YYYYMMDD')||'040103' AS PURCHASEID , "+
					"  (SELECT A.ODD_QTY FROM IND_ODD A " +
					"    WHERE A.ORG_CODE='040103' AND A.CLOSE_DATE=(SELECT MAX(B.CLOSE_DATE) FROM IND_ODD B WHERE B.ORG_CODE=A.ORG_CODE AND B.ORDER_CODE=A.ORDER_CODE  ) AND A.ORDER_CODE=E.ORDER_CODE ) AS LAST_ODD, " +
					"   ( SELECT MAX(A.VERIFYIN_PRICE) FROM IND_STOCK A WHERE A.ORG_CODE='"+orgCode+"' AND A.ORDER_CODE=E.ORDER_CODE " +
					"    ) AS VERIFYIN_PRICE "+
					" FROM (   SELECT D.ORDER_CODE,SUM(D.DOSAGE_QTY) AS DOSAGE_QTY "+
					"	       FROM ( SELECT A.ORDER_CODE,A.DOSAGE_QTY AS DOSAGE_QTY "+
					"					FROM IBS_ORDD A ,PHA_BASE B                  "+
					"					WHERE A.ORDER_CODE=B.ORDER_CODE AND  "+ 
					"						  A.BILL_DATE BETWEEN TO_DATE( '"+start_date+"' , 'YYYYMMDDHH24MISS')"+
					"							  AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS')   AND  "+
					"		 				 ( A.EXE_DEPT_CODE ='040103' OR  A.EXE_DEPT_CODE ='030503' OR A.EXE_DEPT_CODE='0306' )  "+
					"						AND ( B.CTRLDRUGCLASS_CODE IS NULL OR "+
					"							B.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) "+
					"                    UNION ALL      " + 
										 indOddSql +
                    " ) D GROUP BY D.ORDER_CODE   ) " +
                    " E,PHA_BASE F,PHA_TRANSUNIT G,SYS_UNIT H,SYS_UNIT I   "+ 
                    " WHERE F.ORDER_CODE=E.ORDER_CODE "+
                    "	AND G.ORDER_CODE=E.ORDER_CODE "+
                    "   AND H.UNIT_CODE=F.STOCK_UNIT "+
                    "   AND I.UNIT_CODE=F.DOSAGE_UNIT  ";
		if(orderCode != null && !orderCode.equals("")){
			sql += " AND E.ORDER_CODE='"+orderCode+"' ";
		}
			sql += " ORDER BY E.ORDER_CODE " ;
		return sql;
	}
	

	
	/**
	 * 导出采购单为XML文件
	 */
	public void onExportXml() {
		 
			
			String sql= getIbsOrddSql() ;
			// 要导出来的细项总数
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			List list = new ArrayList();
	
			if (parm == null || parm.getErrCode() < 0) {
				this.messageBox("没有导出的数据");
				return;
			}
			
			String regionCode = getValueString("REGION_CODE");
			
			String msg = "";
			for (int i = 0; i < parm.getCount(); i++) {
				TParm t = (TParm) parm.getRow(i);
				// System.out.println("Parm:"+parm);
				
				double dosageQty = t.getDouble("DOSAGE_QTY") ;
				
				String orderCode = t.getValue("ORDER_CODE") ;
				TParm searchParm = new TParm();
				searchParm.setData("REGION_CODE",regionCode);
				searchParm.setData("ORDER_CODE",orderCode);
				TParm result = SPCSysFeeTool.getInstance().queryHisOrderCode(searchParm);		
				String hisOrderCode = result.getValue("HIS_ORDER_CODE", 0);
				
				if(hisOrderCode == null || hisOrderCode.equals("")){
					msg += orderCode+",";
				}else{
					if(dosageQty > 0 ){
						Map<String, String> map = new LinkedHashMap();
						map.put("deliverycode", t.getValue("DELIVERYCODE"));
						map.put("cstcode",  t.getValue("CSTCODE"));
						map.put("goods", hisOrderCode);
						map.put("goodname", t.getValue("ORDER_DESC"));
						map.put("spec", t.getValue("SPECIFICATION"));
						map.put("msunitno", t.getValue("DOSAGE_UNIT"));
						map.put("billqty",dosageQty+"");
						map.put("purchaseid", t.getValue("PURCHASEID"));
						list.add(map);
					}
				}
			}
			Document doc = ExportXmlUtil.createXml(list);
			ExportXmlUtil.exeSaveXml(doc, "040103.xml");
			if(msg.length() > 0 ){
				this.messageBox("物联网编码："+msg+"没有找到");
			}
	}
	
	public void onSave(){
		String sql = getIbsOrddSql() ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("没有结算的数据");
			return;
		}
		if(this.messageBox("提示","确定要结算吗？",2) == 0){
			String start_date = getValueString("END_DATE") ;
			String closeDate = start_date.substring(0, 4)+ start_date.substring(5, 7) + start_date.substring(8, 10);
			int count = 0;
			TParm inParm = new TParm();
			for (int i = 0; i < parm.getCount(); i++) {
				TParm rowParm = (TParm) parm.getRow(i);
				
				inParm.setData("CLOSE_DATE",count,closeDate);
				inParm.setData("ORG_CODE",count,"040103");
				inParm.setData("ORDER_CODE",count,rowParm.getValue("ORDER_CODE"));
				
				TParm searchParm = new TParm();
				searchParm.setData("ORG_CODE","040103");
				searchParm.setData("ORDER_CODE",rowParm.getValue("ORDER_CODE"));
				TParm  outParm =  SPCExportXmlToCmsTool.getInstance().onQueryIndOdd(searchParm);
				if(outParm != null && outParm.getErrCode() != -1 ){
					inParm.setData("LAST_ODD_QTY",count,StringTool.round(outParm.getRow(0).getDouble("ODD_QTY"),4));
				}else {
					inParm.setData("LAST_ODD_QTY",count,0);
				}
				
				inParm.setData("OUT_QTY",count,StringTool.round(rowParm.getDouble("QTY")-rowParm.getDouble("LAST_ODD"),4));
				inParm.setData("ODD_QTY",count,StringTool.round(rowParm.getDouble("ODD"),4));
				inParm.setData("OPT_USER",count,Operator.getID());
				//inParm.setData("OPT_DATE",count,closeDate);
				inParm.setData("OPT_TERM",count,Operator.getIP());
				inParm.setData("IS_UPDATE",count,"Y");
				count++;
			}
			TParm result = TIOM_AppServer.executeAction("action.spc.SPCExportXmlToCmsAction",
	                 "onSave", inParm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("结算失败");
				return;
			}
			this.messageBox("P0001");
		}
	}
	
	 /**
     * 汇出Excel
     */
    public void onExportXls() {
        TTable table = this.getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "住院药房结算");
    }
	
	/**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }
    
    public void onClear(){
    	 String clearStr = "ORDER_CODE;ORDER_DESC";
    	 this.clearValue(clearStr);
    }
    
    public void onPrint(){
    	String sql = getIbsOrddSql() ;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("没有打印数据");
			return;
		}
		
		 // 打印数据
        TParm date = new TParm();
        
        String startDate = getValueString("START_DATE");
        startDate = startDate.substring(0,19);
        String endDate = getValueString("END_DATE");
        endDate = endDate.substring(0,19);
        // 表头数据
        date.setData("TITLE", "住院药房结算单");
        date.setData("START_DATE",startDate);	
        date.setData("END_DATE",endDate);	
        
		TParm newParm = new TParm() ;
		 
		double sumTotAmt =  0 ;
		int count = result.getCount() ;
		for(int i = 0;  i < count ; i++){
			TParm rowParm = result.getRow(i);
			double lastOdd = rowParm.getDouble("LAST_ODD");
			double qty = rowParm.getDouble("QTY");
			double currentQty = qty - lastOdd ;
			newParm.setData("CURRENT_QTY",i,currentQty);
			newParm.setData("QTY",i,qty);
			newParm.setData("VERIFYIN_PRICE",i,StringTool.round(rowParm.getDouble("VERIFYIN_PRICE"),4));
			double totAmt = 0 ;
			try{
				totAmt = qty*rowParm.getDouble("VERIFYIN_PRICE");
				
			}catch (Exception e) {
				// TODO: handle exception
				System.out.println("总量乘以采购单价出错");
				totAmt = 0;
			}
			sumTotAmt += totAmt ;
			newParm.setData("TOT_AMT",i,StringTool.round(totAmt,2));
			newParm.setData("ORDER_CODE",i,rowParm.getValue("ORDER_CODE"));
			newParm.setData("ORDER_DESC",i,rowParm.getValue("ORDER_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("SPECIFICATION"));
			newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("LAST_ODD",i,lastOdd);
			newParm.setData("DOSAGE_QTY",i,rowParm.getDouble("DOSAGE_QTY"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("DOSAGE_UNIT"));
			newParm.setData("ODD",i,rowParm.getDouble("ODD"));
			newParm.setData("DELIVERYCODE",i,rowParm.getValue("DELIVERYCODE"));
			newParm.setData("CSTCODE",i,rowParm.getValue("CSTCODE"));
		}
		
		newParm.setCount(newParm.getCount("ORDER_CODE"));
		newParm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
		newParm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		newParm.addData("SYSTEM", "COLUMNS", "CURRENT_QTY");
		newParm.addData("SYSTEM", "COLUMNS", "LAST_ODD");
		newParm.addData("SYSTEM", "COLUMNS", "QTY");
		newParm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		newParm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
		newParm.addData("SYSTEM", "COLUMNS", "TOT_AMT");
		newParm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		newParm.addData("SYSTEM", "COLUMNS", "DOSAGE_UNIT");
		newParm.addData("SYSTEM", "COLUMNS", "ODD");
		newParm.addData("SYSTEM", "COLUMNS", "DELIVERYCODE");
		newParm.addData("SYSTEM", "COLUMNS", "CSTCODE");
		date.setData("TABLE", newParm.getData());
		
		date.setData("SUM_TOT_AMT",  df2.format(StringTool.round(sumTotAmt, 2)));//验收价格
		date.setData("OPT_USER",""); //Operator.getName()
		
		openPrintDialog("%ROOT%\\config\\prt\\ODI\\SPCExportXmlPrint.jhw",
				date, true);
		
		
    }
	
	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	private String formateMonth(int month){
		if(month < 10){
			return "0"+month;
		}else{
			return ""+month;
		} 
	}

	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");

}
