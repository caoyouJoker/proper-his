package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 集合医嘱
 * </p>
 *
 * <p>
 * Description: 集合医嘱
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 *
 * <p>
 * Company: ProperSoft
 * </p>
 *
 * @author chenhong 2013.03.25
 * @version 1.0
 */


public class INDCollectionDocAdviceControl extends TControl {
	
	public INDCollectionDocAdviceControl() {
		
	}
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	public void initPage(){
		setValue("REGION_CODE","H01");
	}
	
	
	/**
     * 集合医嘱
     */
    public void onQuery(){
    	
    	String sql="SELECT b.REGION_CODE, a.ORDER_CODE,a.ORDER_DESC,b.PRICE3,a.ORDERSET_CODE,a.ORDER_DESC2,a.OWN_PRICE2,a.DOSAGE_QTY"
    			+" FROM ("
    			+"SELECT a.ORDER_CODE,a.ORDER_DESC,a.OWN_PRICE,a.ORDERSET_CODE,b.ORDER_DESC ORDER_DESC2,b.OWN_PRICE OWN_PRICE2,a.DOSAGE_QTY "
    			+"FROM ( "
    			+"Select a.ORDER_CODE,a.ORDER_DESC,a.OWN_PRICE,b.ORDER_CODE ORDERSET_CODE,b.DOSAGE_QTY"
    			+" FROM SYS_FEE a , SYS_ORDERSETDETAIL b "
    			+" WHERE  b.ORDERSET_CODE = a.ORDER_CODE"
    			+" AND ORDERSET_FLG ='Y'"
    			+" ) a,SYS_FEE b"
    			+" WHERE a.ORDERSET_CODE =b.ORDER_CODE"
    			+" GROUP BY a.ORDER_CODE,a.ORDER_DESC,a.OWN_PRICE,a.ORDERSET_CODE,b.ORDER_DESC,b.OWN_PRICE,a.DOSAGE_QTY"
    			+") a,"
    			+"("
    			+" SELECT a.ORDERSET_CODE,Sum(a.DOSAGE_QTY*b.OWN_PRICE) PRICE3, b.REGION_CODE "
    			+" FROM SYS_ORDERSETDETAIL a,SYS_FEE b "
    			+" WHERE   b.ORDER_CODE=a.ORDER_CODE"
    			+" GROUP BY ORDERSET_CODE,b.REGION_CODE"
    			+") b"
    			+" WHERE b.ORDERSET_CODE=a.ORDER_CODE"
    			+" GROUP BY a.ORDER_CODE,a.ORDER_DESC,b.PRICE3,a.ORDERSET_CODE,a.ORDER_DESC2,a.OWN_PRICE2,a.DOSAGE_QTY,b.REGION_CODE"
    			+" ORDER BY ORDER_CODE";
    	
    	
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
        ExportExcelUtil.getInstance().exportExcel(table, "集合医嘱");
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
