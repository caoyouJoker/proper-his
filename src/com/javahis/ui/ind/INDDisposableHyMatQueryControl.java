package com.javahis.ui.ind;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 一次性卫材查询
 * </p>
 *
 * <p>
 * Description: 一次性卫材查询
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


public class INDDisposableHyMatQueryControl extends TControl {
	
	public INDDisposableHyMatQueryControl() {
		
	}
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	public void initPage(){
		setValue("REGION_CODE","H01");
	}
	
	
	public void onQuery(){
    	
    	String sql="SELECT REGION_CODE, ORDER_CODE,ORDER_DESC,SPECIFICATION,START_DATE,END_DATE,UNIT_CHN_DESC,INSPAY_TYPE"
    			+" OWN_PRICE, NHI_CODE_O, NHI_CODE_E, NHI_CODE_I, EXEC_DEPT_CODE,INSPAY_TYPE"
    			+" FROM ("
    			 		+"SELECT A.ORDER_CODE, A.ORDER_DESC, A.SPECIFICATION, A.START_DATE,"
    			 		+"A.END_DATE, B.UNIT_CHN_DESC, A.OWN_PRICE, A.NHI_CODE_O,"
    			 		+"A.NHI_CODE_E, A.NHI_CODE_I, A.EXEC_DEPT_CODE, A.INSPAY_TYPE, A.REGION_CODE"
    			 		+" FROM SYS_FEE_HISTORY A, SYS_UNIT B"
    			 		+" WHERE ORDER_CODE LIKE 'S1%'"
    			 		+" AND CAT1_TYPE <> 'PHA'"
    			 		+" AND CAT1_TYPE <> 'LIS'"
    			 		+" AND CAT1_TYPE <> 'RIS'"
    			 		+" AND A.UNIT_CODE = B.UNIT_CODE)";

    	//System.out.println("sql==="+sql);
    	
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
	 * 清空方法
	 */
	public void onClear() {
		TTable table = this.getTable("TTable");

		table.removeRowAll();
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
        ExportExcelUtil.getInstance().exportExcel(table, "一次性卫材查询");
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
