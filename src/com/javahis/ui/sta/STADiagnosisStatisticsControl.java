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
 * Title: 感染科诊断统计表
 * Description:感染科诊断统计表
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class STADiagnosisStatisticsControl extends TControl {
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
     * 查询
     */
    public void onQuery() {
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
    	sql =" SELECT FDD.MR_NO, FDD.CASE_NO, FDD.ICD_CODE," +
    		" FDD.ICD_DESC, FZ.DEPT_CHN_DESC, FTT.OUT_DATE" +
    		" FROM MRO_RECORD_DIAG FDD," +
    		" MRO_RECORD FTT," +
    		" JAVAHIS.SYS_DEPT FZ" +
    		" WHERE FDD.IO_TYPE = 'Q'" +
    		" AND FDD.CASE_NO = FTT.CASE_NO" +
    		" AND FTT.OUT_DEPT = FZ.DEPT_CODE" +
    		" AND FTT.OUT_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')" + 
    		" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')" +
    		" ORDER BY  FDD.MR_NO, FDD.CASE_NO, FDD.IO_TYPE ";		
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
    	String title ="感染诊断统计表";	
    	    if (table.getRowCount() > 0)   		
			ExportExcelUtil.getInstance().exportExcel(table,title);
    	
    }  	
    /**
     * 清空
     */
    public void onClear() {
    	this.setValue("START_DATE",SystemTool.getInstance().getDate());	
    	this.setValue("END_DATE",SystemTool.getInstance().getDate());
 	    this.callFunction("UI|TABLE|setParmValue", new TParm());
    }
}
