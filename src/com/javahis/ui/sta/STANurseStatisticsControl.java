package com.javahis.ui.sta;

import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;



/**
 * Title: 护理部日常统计
 * Description:护理部日常统计
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class STANurseStatisticsControl extends TControl {
	private TTable table;
    public void onInit() {
        super.onInit();
        table = (TTable) this.getComponent("TABLE");
        onClear();
    }
    /**
	 * 数据检核
	 */
	private boolean checkdata(){
	   	if(this.getValue("START_DATE").equals("")){
    		this.messageBox("开始日期不能为空");
    		return true;
    	}
    	if(this.getValue("END_DATE").equals("")){
    		this.messageBox("结束日期不能为空");
    		return true;
    	}
	    return false; 
	}
	 /**
     * 人次查询
     */
    public void onSelect() {
    	//数据检核
    	if(checkdata())
		    return;
    	String sql ="";  	
    	TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+"000000";
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+"235959";
		//查询数据		
		sql =" SELECT COUNT(*) AS COUNT_SUM FROM ("+
			" SELECT  A.CASE_NO FROM ADM_INP A, IBS_ORDD B,PHA_BASE C"+
			" WHERE A.DS_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+ 
			" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
			" AND B.CASE_NO = A.CASE_NO"+
			" AND B.ORDER_CODE = C.ORDER_CODE"+
			" AND C.ROUTE_CODE IN ('IVD', 'IVP', 'PUMP')"+
			" GROUP BY A.CASE_NO"+
			" ORDER BY A.CASE_NO)";
//		 System.out.println("sql========="+sql);
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("result========="+result);   		 
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			return;
		}
	this.setValue("ZS_COUNT", result.getInt("COUNT_SUM", 0));
    }
    /**
     * 查询
     */
    public void onQuery() {
    	//数据检核
    	if(checkdata())
		    return;
    	//药品类型检核
    	if(this.getValue("PHA_TYPE").equals("")){
    		this.messageBox("药品类型不能为空");
    		return;
    	}
    	String sql ="";  	
    	TParm result = new TParm();
		String startDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("START_DATE")), "yyyyMMdd");
		String endDate = StringTool.getString(TypeTool
				.getTimestamp(getValue("END_DATE")), "yyyyMMdd");
		//查询数据
		 if(this.getValue("PHA_TYPE").equals("1"))	
    	sql =" SELECT T.STATION_DESC, T.ROUTE_CHN_DESC, COUNT(*) AS COUNT_SUM "+
    		" FROM( SELECT C.STATION_DESC, D.ROUTE_CHN_DESC, B.CASE_NO, " +
    		" B.ORDER_NO,A.LINK_NO ,B.ORDER_DATE ,B.ORDER_DATETIME "+
    		" FROM ODI_DSPNM A, ODI_DSPND B,SYS_STATION C,SYS_PHAROUTE D "+
    		" WHERE A.CASE_NO = B.CASE_NO "+
    		" AND A.ORDER_NO = B.ORDER_NO "+
    		" AND A.ORDER_SEQ = B.ORDER_SEQ "+
    		" AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM "+
    		" AND  B.ORDER_DATE BETWEEN '"+startDate+"' AND '"+endDate+"' "+ 
    		" AND A.LINK_NO IS NOT NULL "+
    		" AND A.ROUTE_CODE IN('PO','IVP','IVD','IH','IM') "+
    		" AND A.PHA_DOSAGE_DATE IS NOT NULL "+
    		" AND B.NS_EXEC_DATE IS NOT NULL "+
    		" AND A.ROUTE_CODE = D.ROUTE_CODE "+
    		" AND A.STATION_CODE = C.STATION_CODE "+
    		" GROUP BY C.STATION_DESC, D.ROUTE_CHN_DESC, B.CASE_NO, "+ 
    		" B.ORDER_NO,A.LINK_NO ,B.ORDER_DATE ,B.ORDER_DATETIME) T "+
    		" GROUP BY T.STATION_DESC, T.ROUTE_CHN_DESC "+
    		" ORDER BY T.STATION_DESC ";	
		else if(this.getValue("PHA_TYPE").equals("2"))
			sql =" SELECT C.STATION_DESC, D.ROUTE_CHN_DESC, COUNT(DISTINCT B.ROWID) AS COUNT_SUM"+
				" FROM ODI_DSPNM A, ODI_DSPND B,SYS_STATION C,SYS_PHAROUTE D"+
				" WHERE A.CASE_NO = B.CASE_NO"+
				" AND A.ORDER_NO = B.ORDER_NO"+
				" AND A.ORDER_SEQ = B.ORDER_SEQ"+
				" AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM AND A.END_DTTM"+
				" AND  B.ORDER_DATE BETWEEN '"+startDate+"' AND '"+endDate+"'"+ 
				" AND A.LINK_NO IS  NULL"+
				" AND A.ROUTE_CODE IN('PO','IVP','IVD','IH','IM')"+
				" AND A.PHA_DOSAGE_DATE IS NOT NULL"+
				" AND B.NS_EXEC_DATE IS NOT NULL"+
				" AND A.ROUTE_CODE = D.ROUTE_CODE"+
				" AND A.STATION_CODE = C.STATION_CODE"+
				" GROUP BY C.STATION_DESC, D.ROUTE_CHN_DESC"+
				" ORDER BY C.STATION_DESC ";		
//		 System.out.println("sql========="+sql);		 
		result = new TParm(TJDODBTool.getInstance().select(sql));
//		 System.out.println("result========="+result);   		 
		// 判断错误值
		if (result.getErrCode() < 0) {
			messageBox(result.getErrText());
			messageBox("E0005");//执行失败
			return;
		}
		if (result.getCount()<= 0) {
			messageBox("E0008");//查无资料
			((TTable) getComponent("TABLE")).removeRowAll();
			return;
		}	
    	((TTable) getComponent("TABLE")).setParmValue(result);    	
		
  }
    
    /**
     * 汇出
     */
    public void onExport() {
    	String title ="";
    	String a ="";
   	 if(this.getValue("PHA_TYPE").equals("1"))	 
   		 a ="有组号";	
   	else if(this.getValue("PHA_TYPE").equals("2"))
   		 a ="无组号";	
   	title =a+"用药例数统计表";
   	 if (table.getRowCount() > 0)   		
    	ExportExcelUtil.getInstance().exportExcel(table,title);		
    }   	
    /**
     * 清空
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
    	this.setValue("PHA_TYPE","1");
    	this.setValue("ZS_COUNT","0");
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
}
