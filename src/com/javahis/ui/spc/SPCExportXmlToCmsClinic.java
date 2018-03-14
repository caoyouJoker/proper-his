package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.spc.SPCExportXmlToCmsClinicTool;
import jdo.spc.SPCSysFeeTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.dom4j.Document;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

public class SPCExportXmlToCmsClinic extends TControl {

	public TTable table;
	public TRadioButton r_clinic;
	public TRadioButton r_clinic_city;
	
	
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
//		this.messageBox("endstr-----------"+endStr);
//		this.messageBox("startStr-----------"+startStr);
		
		this.setValue("END_DATE",endStr);
		
		this.setValue("START_DATE", startStr);
		
		setValue("REGION_CODE", Operator.getRegion());
		
		r_clinic = (TRadioButton) getComponent("R_CLINIC");
		r_clinic_city = (TRadioButton) getComponent("R_CLINIC_CITY");
		
		table = (TTable) getComponent("TABLE");
		
	}
	
	private String formateMonth(int month){
		if(month < 10){
			return "0"+month;
		}else{
			return ""+month;
		} 
	}
	
	/**
	 * 查询
	 */
	public void onQuery(){
		
		String sql = getSearchSql();
		System.out.println("SQL----:"+sql);
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		TParm newParm = new TParm() ;
		 
		double sumTotAmt =0;
		double oddAmt = 0 ;
		for(int i = 0;  i < result.getCount() ; i++){
			TParm rowParm = result.getRow(i);
			
			double lastOdd = rowParm.getDouble("LAST_ODD");
			double qty = rowParm.getDouble("QTY");
			double currentQty = qty - lastOdd ;
			
			double unitPrice = rowParm.getDouble("UNIT_PRICE");
			double amount = StringTool.round(unitPrice * qty,2);
			sumTotAmt = sumTotAmt + amount;
			
			newParm.setData("CURRENT_QTY",i,currentQty);
			newParm.setData("QTY",i,qty);
			
			newParm.setData("UNIT_PRICE",i,unitPrice);
			newParm.setData("TOT_AMT",i,amount);
			
			newParm.setData("ORDER_CODE",i,rowParm.getValue("ORDER_CODE"));
			newParm.setData("ORDER_DESC",i,rowParm.getValue("ORDER_DESC"));
			newParm.setData("SPECIFICATION",i,rowParm.getValue("SPECIFICATION"));
			newParm.setData("UNIT_CHN_DESC",i,rowParm.getValue("UNIT_CHN_DESC"));
			newParm.setData("LAST_ODD",i,lastOdd);
			newParm.setData("DOSAGE_QTY",i,rowParm.getDouble("DOSAGE_QTY"));
			newParm.setData("DOSAGE_UNIT",i,rowParm.getValue("DOSAGE_UNIT"));
			
			double odd = rowParm.getDouble("ODD");
			oddAmt = odd * rowParm.getDouble("UNIT_PRICE");
			newParm.setData("ODD_AMT",i,StringTool.round(oddAmt,2));
			newParm.setData("ODD",i,odd);
			
			newParm.setData("DELIVERYCODE",i,rowParm.getValue("DELIVERYCODE"));
			newParm.setData("CSTCODE",i,rowParm.getValue("CSTCODE"));
			
		}
		
		setValue("SUM_TOT_AMT", sumTotAmt);
		setValue("COUNT", result.getCount());
		
		if(result.getCount()<=0){
			this.messageBox("查询结果为空!");
		}
		
		table.setParmValue(newParm);
	}

	/**查询sql*/
	private String getSearchSql() {
		
		String org_code="";
		if(r_clinic.isSelected()){
			org_code="040102";
		}else{
			org_code="040104";
		}
		
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
		
		
		String month = end_date.substring(5,7);
		month = String.valueOf(Integer.parseInt(month)-1 );
		if(month.length()< 2){
			month = "0"+month ;
		}
		String closeDate = end_date.substring(0, 4)+ month + end_date.substring(8, 10);
		
		 //(SELECT A.ODD_QTY FROM IND_ODD A " +
					//"    WHERE A.ORG_CODE='040103' AND A.CLOSE_DATE=(SELECT MAX(B.CLOSE_DATE) FROM IND_ODD B WHERE B.ORG_CODE=A.ORG_CODE AND B.ORDER_CODE=A.ORDER_CODE  ) AND A.ORDER_CODE=E.ORDER_CODE ) AS LAST_ODD 
		String sql ="SELECT '932290201' AS DELIVERYCODE, "+
					       "'18' AS CSTCODE, "+
					       "E.ORDER_CODE, "+
					       "F.ORDER_DESC, "+
					       "F.SPECIFICATION, "+ 
					       "(SELECT A.ODD_QTY " +
					       		"FROM IND_ODD A " +
					       		"WHERE A.ORG_CODE='"+org_code+"' " +
					       			"AND A.ORDER_CODE=E.ORDER_CODE " +
					       			"AND A.CLOSE_DATE=( "+ 
					       				"SELECT MAX(B.CLOSE_DATE) " +
					       				"FROM IND_ODD B " +
					       				"WHERE B.ORG_CODE=A.ORG_CODE " +
					       					"AND B.ORDER_CODE=A.ORDER_CODE) "+ 
					    		   ") AS LAST_ODD, "+ 
					       "E.DOSAGE_QTY AS QTY, "+
					       "I.UNIT_CHN_DESC , "+
					       "(SELECT MAX(J.VERIFYIN_PRICE) FROM IND_STOCK J WHERE J.ORG_CODE='"+org_code+"' AND J.ORDER_CODE=E.ORDER_CODE) AS UNIT_PRICE, "+
					       "FLOOR(E.DOSAGE_QTY/G.DOSAGE_QTY/G.STOCK_QTY/F.CONVERSION_RATIO) AS DOSAGE_QTY, "+
					       "H.UNIT_CHN_DESC AS DOSAGE_UNIT, "+
					       "MOD(E.DOSAGE_QTY,G.DOSAGE_QTY/G.STOCK_QTY/F.CONVERSION_RATIO) AS ODD, "+
					       "TO_CHAR(SYSDATE,'YYYYMMDD')||"+org_code+" AS PURCHASEID "+
					  "FROM ( "+
							"SELECT D.ORDER_CODE,SUM(D.DOSAGE_QTY) AS DOSAGE_QTY "+
							  "FROM (SELECT A.ORDER_CODE,A.DOSAGE_QTY "+
							          "FROM OPD_ORDER A "+
							         "WHERE A.ORDER_DATE BETWEEN TO_DATE ('"+start_date+"','YYYYMMDDHH24MISS') "+
							                                     "AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') "+
							           "AND A.PHA_DISPENSE_CODE IS NOT NULL "+
							           "AND A.PHA_RETN_CODE IS NULL "+
							           "AND A.ORDER_CAT1_CODE IN ('PHA_W','PHA_C') "+
							           "AND (A.EXEC_DEPT_CODE='"+org_code+"' "+
							                "OR A.EXEC_DEPT_CODE=(SELECT ORG_CODE FROM IND_ORG WHERE ATC_FLG='Y' AND ATC_ORG_CODE='"+org_code+"')) "+
							        "UNION ALL " ;
							        if(r_clinic.isSelected()){
										sql += getElseSql(start_date, end_date);
									}
							       
						  sql +=    "SELECT A.ORDER_CODE,A.ODD_QTY AS DOSAGE_QTY "+
							          "FROM IND_ODD A "+
							         "WHERE A.ORG_CODE='"+org_code+"' "+
							           "AND A.CLOSE_DATE='"+closeDate+ "'"+
							       ") D "+
							    "GROUP BY D.ORDER_CODE "+
					  	") E, "+
		 				"PHA_BASE F, "+
		 				"PHA_TRANSUNIT G, "+
		 				"SYS_UNIT H, "+
		 				"SYS_UNIT I " +
					 "WHERE F.ORDER_CODE=E.ORDER_CODE "+
					   "AND G.ORDER_CODE=E.ORDER_CODE "+
					   "AND H.UNIT_CODE=G.STOCK_UNIT "+
					   "AND I.UNIT_CODE=F.DOSAGE_UNIT ORDER BY E.ORDER_CODE";

		return sql;
	}
	
	public String getElseSql(String start_date,String end_date){
		String sql =  " SELECT A.ORDER_CODE,CASE WHEN A.DOSAGE_UNIT=C.DOSAGE_UNIT THEN A.DOSAGE_QTY " +
	        " ELSE A.DOSAGE_QTY * C.DOSAGE_QTY * C.STOCK_QTY  END AS DOSAGE_QTY "+
	          "FROM OPD_ORDER A,PHA_TRANSUNIT C "+
	         "WHERE  A.EXEC_DEPT_CODE IN ('0404','0405') "+
	           "AND A.BILL_DATE BETWEEN TO_DATE ('"+start_date+"','YYYYMMDDHH24MISS') "+
	                                   "AND TO_DATE ('"+end_date+"','YYYYMMDDHH24MISS') "+
	           "AND A.ORDER_CAT1_CODE IN ('PHA_W','PHA_C') "+
	           "AND A.ORDER_CODE=C.ORDER_CODE "+
	        "UNION ALL " ;
		return sql ;
	}
	
	/**
	 * 导出XML文件
	 */
	public void onExportXml() {		 
			
			String sql= getSearchSql() ;
			// 要导出来的细项总数
			TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
			List list = new ArrayList();
			
			if(parm.getErrCode() <  0){
				this.messageBox("没有导出数据");
				return ;
			}
			String msg = "";
			String regionCode = getValueString("REGION_CODE");
			TParm xmlParm = new TParm();
			for (int i = 0; i < parm.getCount(); i++) {
				TParm t = (TParm) parm.getRow(i);
				
				double dosageQty =t.getDouble("DOSAGE_QTY");
				String orderCode = t.getValue("ORDER_CODE") ;
				TParm searchParm = new TParm();
				searchParm.setData("REGION_CODE",regionCode);
				searchParm.setData("ORDER_CODE",orderCode);
				TParm result = SPCSysFeeTool.getInstance().queryHisOrderCode(searchParm);		
				String hisOrderCode = result.getValue("HIS_ORDER_CODE", 0);
				
				if(hisOrderCode == null || hisOrderCode.equals("")){
					msg += orderCode+",";
				}else{
					if(dosageQty > 0){
						Map<String, String> map = new LinkedHashMap();
						map.put("deliverycode", t.getValue("DELIVERYCODE"));
						map.put("cstcode",  t.getValue("CSTCODE"));
						map.put("goods", hisOrderCode);
						map.put("goodname", t.getValue("ORDER_DESC"));
						map.put("spec", t.getValue("SPECIFICATION"));
						map.put("msunitno", t.getValue("DOSAGE_UNIT"));
						map.put("billqty", String.valueOf(dosageQty));
						map.put("purchaseid", t.getValue("PURCHASEID"));
						list.add(map);
					}
				}
								
			}
			
			Document doc = ExportXmlUtil.createXml(list);
			ExportXmlUtil.exeSaveXml(doc, "export_file.xml");
	}
	
	
	/**结算功能*/
	public void onSave(){
		
		if (this.messageBox("提示", "每期结算只能进行一次，确定现在要结算?", 2) == 0) {
	            
	        }else {
	           return;
	        }

		String sql = getSearchSql() ;
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("没有结算的数据");
			return;
		}
		
		String end_date = getValueString("END_DATE");
		
		String closeDate = end_date = end_date.substring(0, 4) + end_date.substring(5, 7) + end_date.substring(8, 10) ;
		
		int count = 0;
		TParm inParm = new TParm();
		for (int i = 0; i < parm.getCount(); i++) {
			TParm rowParm = (TParm) parm.getRow(i);
			
			inParm.setData("CLOSE_DATE",count,closeDate);
			
			
			if(r_clinic.isSelected()){
				inParm.setData("ORG_CODE",count,"040102");//org_code="040102";
			}else{
				inParm.setData("ORG_CODE",count,"040104");//org_code="040104";
			}
//			inParm.setData("ORG_CODE",count,"040103");
			
			inParm.setData("ORDER_CODE",count,rowParm.getValue("ORDER_CODE"));
			
			TParm searchParm = new TParm();
			
			if(r_clinic.isSelected()){
				searchParm.setData("ORG_CODE","040102");//org_code="040102";
			}else{
				searchParm.setData("ORG_CODE","040104");//org_code="040104";
			}
			
//			searchParm.setData("ORG_CODE","040103");
			
			searchParm.setData("ORDER_CODE",rowParm.getValue("ORDER_CODE"));
			TParm  outParm =  SPCExportXmlToCmsClinicTool.getInstance().onQueryIndOdd(searchParm);
			
			if(outParm != null && outParm.getErrCode() !=-1 ){
				inParm.setData("LAST_ODD_QTY",count,StringTool.round(outParm.getRow(0).getDouble("ODD_QTY"), 4));
			}else {
				inParm.setData("LAST_ODD_QTY",count,0);
			}
			
			inParm.setData("OUT_QTY",count, (StringTool.round(rowParm.getDouble("QTY"),4)
											-StringTool.round(rowParm.getDouble("LAST_ODD"), 4)) );
			inParm.setData("ODD_QTY",count,StringTool.round(rowParm.getDouble("ODD"),4)); 
			
			inParm.setData("OPT_USER",count,Operator.getID());
			//inParm.setData("OPT_DATE",count,closeDate);
			inParm.setData("OPT_TERM",count,Operator.getIP());
			inParm.setData("IS_UPDATE",count,"Y");
			count++;
		}
		TParm result = TIOM_AppServer.executeAction("action.spc.SPCExportXmlToCmsClinicAction",
                 "onSave", inParm);
		if (parm == null || parm.getErrCode() < 0) {
			this.messageBox("结算失败");
			return;
		}
		this.messageBox("P0001");
	}
	
	public void onPrint(){
    	String sql = getSearchSql() ;
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
        if(r_clinic.isSelected()){
			date.setData("TITLE", "门诊药房结算单");
		}else{
			date.setData("TITLE", "市内门诊药房结算单");
		}
      
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
			newParm.setData("VERIFYIN_PRICE",i,StringTool.round(rowParm.getDouble("UNIT_PRICE"),4));
			double totAmt = 0 ;
			try{
				totAmt = qty*rowParm.getDouble("UNIT_PRICE");
				
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
     * 汇出Excel
     */
    public void onExportXls() {
        TTable table = (TTable) getComponent("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        String title = "" ;
        if(r_clinic.isSelected()){
        	title = "门诊药房结算单";
		}else{
			title = "市内门诊药房结算单";
		}
        ExportExcelUtil.getInstance().exportExcel(table,title);
    }
	
	java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
	
	
}
