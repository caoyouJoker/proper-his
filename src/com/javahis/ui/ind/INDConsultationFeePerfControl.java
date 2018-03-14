package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 会诊费绩效
 * </p>
 *
 * <p>
 * Description: 会诊费绩效
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: BlueCore
 * </p>
 *
 * @author chenhong 2013.03.25
 * @version 1.0
 */


public class INDConsultationFeePerfControl extends TControl {
	
	public INDConsultationFeePerfControl() {
		
	}
	
	public void onInit(){
		super.init();
	}
	
	
	
	/**
     * 根据医嘱进行查询
     */
    public void onQuery(){
    	//获取医嘱
    	String order_code=null;
    	order_code=getValueString("ORDER_CODE");
    	order_code="='"+order_code+"'";
    	
    	if("=''".equals(order_code)){
    		order_code=" in	('A0000028',"
							+"'A0000029',"
							+"'A0000030',"
							+"'A0000031',"
							+"'A0000032')";
    	}
    	
    	String sql="SELECT CASE_NO,DEPT_CODE,STATION_CODE,DR_CODE,EXE_DEPT_CODE," 
    			+"EXE_STATION_CODE,EXE_DR_CODE,OWN_PRICE,NHI_PRICE,TOT_AMT "
    			+" FROM IBS_ORDD "
    			+"WHERE ORDER_CODE "
    			+order_code;
    							

    	TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	if(parm.getErrCode() < 0 ){
    		this.messageBox(parm.getErrText());
    		return;
    	}
        if(parm.getCount() <= 0)
        {
        	this.messageBox("查无数据");
        }
    	       
      //在table中显示查询信息
    	TTable  table = (TTable)this.getComponent("TTable") ;
    	
    	table.setParmValue(parm);
    	
    }
	
    
    
    
    /**
     * 汇出Excel
     */
    public void onExport() {
        TTable table = this.getTable("TTable");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "会诊费绩效");
    }
	
    /**
     * 清空方法
     */
    public void onClear() {
    	String clearStr = "ORDER_CODE";
        this.clearValue(clearStr);
        
        TTable  table = this.getTable("TTable");

        table.removeRowAll();
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
	
	

}
