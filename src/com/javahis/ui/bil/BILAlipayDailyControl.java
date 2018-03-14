package com.javahis.ui.bil;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

public class BILAlipayDailyControl extends TControl {
	
private static TTable table;
private String status = "";
private DecimalFormat df = new DecimalFormat("##########0.00");
	
	/**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        table = (TTable)this.getComponent("TABLE");

        initPage();
        select();
    }
    
    /**
     * 初始化界面数据
     */
    public void initPage() {

        Timestamp today = SystemTool.getInstance().getDate();
    	String startDate = today.toString();
        startDate = startDate.substring(0, 4)+"/"+startDate.substring(5, 7)+ "/"+startDate.substring(8, 10)+ " 00:00:00";
        String endDate = today.toString();
        endDate = endDate.substring(0, 4)+"/"+endDate.substring(5, 7)+ "/"+endDate.substring(8, 10)+ " 23:59:59";
    	setValue("START_DATE", startDate);
    	setValue("END_DATE", endDate);
    	this.setValue("FLG_O", "Y");
    	
    	
    }
    
    public void select(){

    	if("Y".equalsIgnoreCase(this.getValueString("FLG_S"))){
    		status="1";
    	}
    	if("Y".equalsIgnoreCase(this.getValueString("FLG_T"))){
    		status="3";
    	}
    	onQuery();
    }
    
    public void onQuery(){
    	String startDate = "";
		String endDate = "";
		if (!"".equals(this.getValueString("START_DATE")) &&
	            !"".equals(this.getValueString("END_DATE"))) {
			startDate = getValueString("START_DATE").substring(0, 19);
			endDate = getValueString("END_DATE").substring(0, 19);
			startDate = startDate.substring(0, 4) + startDate.substring(5, 7) +
			startDate.substring(8, 10) + startDate.substring(11, 13) +
			startDate.substring(14, 16) + startDate.substring(17, 19);
		endDate = endDate.substring(0, 4) + endDate.substring(5, 7) +
			endDate.substring(8, 10) + endDate.substring(11, 13) +
			endDate.substring(14, 16) + endDate.substring(17, 19);
		}
		
		String whereSql = ""; 
		
		if("3".equals(status)){
			whereSql = " AND A.STATE = '"+status+"'" ;
		}else{
			whereSql = "";
		}
		
		String sql = "SELECT A.TRADE_NO, TO_CHAR( A.OPT_DATE, 'YYYY-MM-DD HH24:MI:SS') ORDER_TIME, " +
				" B.AMT, A.CASE_NO," +
				" C.PAT_NAME, A.MR_NO, A.BUSINESS_TYPE" +
				" FROM EKT_TRADE A, EKT_APP_TRADE B, SYS_PATINFO C" +
				" WHERE A.TRADE_NO = B.TRADE_NO" +
				" AND B.BUSINESS_APP_TYPE IN ('8', '9')" +
				" AND A.MR_NO = C.MR_NO" +
				whereSql+
				" AND A.OPT_DATE BETWEEN TO_DATE( '"+startDate+"', 'YYYYMMDDHH24MISS')" +
				" AND TO_DATE( '"+endDate+"', 'YYYYMMDDHH24MISS')" +
				" ORDER BY A.OPT_DATE";
		
		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()< 0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
		}
		
		
		double sumAmt=0;
		for (int i = 0; i < parm.getCount(); i++) {
			if("REG".equals(parm.getValue("BUSINESS_TYPE", i))){
				parm.setData("BUSINESS_TYPE", i,"挂号");
			}
			if("OPB".equals(parm.getValue("BUSINESS_TYPE", i))){
				parm.setData("BUSINESS_TYPE", i,"缴费");
			}
			parm.setData("AMT", i, df.format(parm.getDouble("AMT", i)));
			sumAmt += parm.getDouble("AMT", i);
		}
		
		parm.addData("TRADE_NO", "总计：");
		parm.addData("ORDER_TIME", "");
		parm.addData("AMT", df.format(sumAmt));
		parm.addData("BUSINESS_TYPE", "");
		parm.addData("CASE_NO", "");
		parm.addData("PAT_NAME", "");
		parm.addData("MR_NO", "");
		parm.setCount(parm.getCount("TRADE_NO"));
		table.setParmValue(parm);
    	
    	
    }
    
    public void onClear(){
    	table.removeRowAll();
    	initPage();
    }
    
    public void onPrint(){
    	TParm tableParm = table.getParmValue();
    	if(tableParm.getCount() < 0){
    		this.messageBox("没有要打印的数据");
    		return;
    	}
    	
    	// 打印数据
		TParm date = new TParm();
		String title = "";
		if(status.equals("1")){
			title="收入";
		}
		if(status.equals("3")){
			title="退费";
		}
		String startDate = getValueString("START_DATE").replaceAll("-", "/").substring(0, 19);
		String endDate = getValueString("END_DATE").replaceAll("-", "/").substring(0, 19);
		date.setData("TITLE", "TEXT", "支付宝"+title+"明细报表");
		date.setData("DATE","TEXT","统计时间:"+startDate+"--"+endDate);
    	TParm parm = new TParm();
    	for (int i = 0; i < tableParm.getCount(); i++) {
    		parm.addData("TRADE_NO", tableParm.getValue("TRADE_NO", i));
			parm.addData("ORDER_TIME", tableParm.getValue("ORDER_TIME", i));
			parm.addData("AMT", df.format(tableParm.getDouble("AMT", i)));
			parm.addData("BUSINESS_TYPE", tableParm.getValue("BUSINESS_TYPE", i));
			parm.addData("CASE_NO", tableParm.getValue("CASE_NO", i));
			parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
			parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
		}
    	parm.setCount(parm.getCount("TRADE_NO"));
		parm.addData("SYSTEM", "COLUMNS", "TRADE_NO");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_TIME");
		parm.addData("SYSTEM", "COLUMNS", "AMT");
		parm.addData("SYSTEM", "COLUMNS", "BUSINESS_TYPE");
		parm.addData("SYSTEM", "COLUMNS", "CASE_NO");
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		date.setData("TABLE", parm.getData());
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\BIL\\BilAlipayPrint.jhw", date);
    	
    }
    
    /**
	 * 汇出Excel
	 */
	public void onExport() {
		String title = "";
		if(status.equals("1")){
			title="收入";
		}
		if(status.equals("1")){
			title="退费";
		}

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|TABLE|getThis");
		ExportExcelUtil.getInstance().exportExcel(table,  "支付宝"+title+"明细表");
	}

}
