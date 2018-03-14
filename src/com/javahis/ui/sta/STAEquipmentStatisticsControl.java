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
 * Title: 设备使用率统计表
 * Description:设备使用率统计表
 * Copyright: Copyright (c)
 * Company:Javahis
 * @author yufh 2015
 * @version 1.0
 */
public class STAEquipmentStatisticsControl extends TControl {
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
    	sql =" SELECT A.ORDER_CODE, A.ORDER_CHN_DESC,SUM(A.DOSAGE_QTY ) AS DOSAGE_QTY"+
    		" FROM IBS_ORDD A,SYS_FEE B"+
    		" WHERE  A.ORDER_CODE = B.ORDER_CODE"+
    		" AND A.BILL_DATE BETWEEN TO_DATE('"+startDate+"','YYYYMMDDHH24MISS')"+
    		" AND  TO_DATE('"+endDate+"','YYYYMMDDHH24MISS')"+
    		" AND A.ORDER_CODE = 'L0000015'"+
    		" GROUP BY A.ORDER_CODE,A.ORDER_CHN_DESC ";		
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
    	String title ="设备使用率统计表";	
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
