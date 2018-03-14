package com.javahis.ui.inv;

import java.util.Date;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;


/**
 * 
 * <p>
 * Title:科室退货查询界面
 * </p>
 * 
 * <p>
 * Description: 科室退货查询界面
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-12-20
 * @version 1.0
 */
public class INVPIReturnReportControl extends TControl{
	
	private TTabbedPane tabPane;
	/**
	 * 初始化
	 */
	public void onInit() {
	
		tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
		
		TParm parm = new TParm();
        //设置弹出菜单
        getTextField("PACK_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
        //定义接受返回值方法
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");
        
        TParm invParm = new TParm();
        //设置物资弹出窗口
        getTextField("INV_CODE").setPopupMenuParameter("INVBASE",
                                        getConfigParm().newConfig(
                                            "%ROOT%\\config\\inv\\INVBasePopup.x"), invParm);
        //定义接受返回值方法
        getTextField("INV_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                 "invReturn");


        //设置默认科室
        TTextFormat tf = (TTextFormat)getComponent("DEPT_CODE");
        tf.setValue(Operator.getDept());
        tf = (TTextFormat)getComponent("DEPT_CODE_SEC");
        tf.setValue(Operator.getDept());
        
        this.setTimes();
        
	}
	
	/**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code))
            getTextField("PACK_CODE").setValue(pack_code);
        String pack_desc = parm.getValue("PACK_DESC");
        if (!StringUtil.isNullString(pack_desc))
            getTextField("PACK_DESC").setValue(pack_desc);
 
    }
    
    public void invReturn(String tag, Object obj){
    	
    	TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String inv_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(inv_code))
            getTextField("INV_CODE").setValue(inv_code);
        String inv_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(inv_desc))
            getTextField("INV_DESC").setValue(inv_desc);
    	
    }

    public void onClear(){
    	
    	if(tabPane.getSelectedIndex() == 0){
    		getTextField("PACK_CODE").setValue("");
        	getTextField("PACK_DESC").setValue("");
        	this.setValue("DEPT_CODE", Operator.getDept());
        	((TTable) getComponent("TABLEM")).setParmValue(new TParm());
 
    	}else if(tabPane.getSelectedIndex() == 1){
    		getTextField("INV_CODE").setValue("");
        	getTextField("INV_DESC").setValue("");
        	this.setValue("DEPT_CODE_SEC", Operator.getDept());
        	((TTable) getComponent("TABLEMSEC")).removeRowAll();
    	}
    	
    }
    
    public void onQuery(){
   
    	if(tabPane.getSelectedIndex() == 0){
    		
    		if(!checkPackageConditions()){
    			return;
    		}
    		String sql = this.getPackageSql();
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		((TTable) getComponent("TABLEM")).setParmValue(result);
    		
    	}else if(tabPane.getSelectedIndex() == 1){
    		
    		if(!checkInvConditions()){
    			return;
    		}
    		String sql = this.getInvSql();
    		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    		((TTable) getComponent("TABLEMSEC")).setParmValue(result);
    		
    	}

    }
    
    private String getPackageSql(){
    	
    	String sql = "";
    	
    	sql = " SELECT D.PACK_CODE, SUM(D.QTY) AS QTY, PM.PACK_DESC " 
    		+ " FROM INV_SUP_PACKAGERETURND D LEFT JOIN INV_SUP_PACKAGERETURNM M ON D.PACKAGERETURNED_NO = M.PACKAGERETURNED_NO LEFT JOIN INV_PACKM PM ON D.PACK_CODE = PM.PACK_CODE  " 
    		+ " WHERE M.CHECK_DATE BETWEEN TO_DATE('" + this.getValueString("START_DATE").substring(0, 19) + "','yyyy/mm/dd hh24:mi:ss') AND  TO_DATE('" + this.getValueString("END_DATE").substring(0, 19) + "','yyyy/mm/dd hh24:mi:ss') AND M.FROM_ORG_CODE = '" + this.getValueString("DEPT_CODE") + "' ";
    	
    	if(null!=this.getValueString("PACK_CODE") && !this.getValueString("PACK_CODE").equals("")){
    		sql = sql + " AND D.PACK_CODE = '" + this.getValueString("PACK_CODE") + "' ";
    	}
    	
    	sql = sql + " GROUP BY D.PACK_CODE,PM.PACK_DESC ";
    	
    	return sql;
    	
    }
    
    private String getInvSql(){
    	
    	String sql = "";
    	
    	sql = " SELECT D.INV_CODE, SUM(D.QTY) AS QTY, SUM(D.ACTUAL_QTY) AS ACTUAL_QTY, B.INV_CHN_DESC, B.DESCRIPTION, U.UNIT_CHN_DESC AS STOCK_UNIT " 
    		+ " FROM INV_SUP_RETURND D LEFT JOIN INV_SUP_RETURNM M ON D.RETURNED_NO = M.RETURNED_NO LEFT JOIN INV_BASE B ON D.INV_CODE = B.INV_CODE LEFT JOIN SYS_UNIT U ON D.UNIT_CODE = U.UNIT_CODE " 
    		+ " WHERE M.CHECK_DATE BETWEEN TO_DATE('" + this.getValueString("START_DATE_SEC").substring(0, 19) + "','yyyy/mm/dd hh24:mi:ss') AND  TO_DATE('" + this.getValueString("END_DATE_SEC").substring(0, 19) + "','yyyy/mm/dd hh24:mi:ss') AND M.FROM_ORG_CODE = '" + this.getValueString("DEPT_CODE_SEC") + "' ";
    	
    	if(null!=this.getValueString("INV_CODE") && !this.getValueString("INV_CODE").equals("")){
    		sql = sql + " AND D.INV_CODE = '" + this.getValueString("INV_CODE") + "' ";
    	}
    	
    	sql = sql + " GROUP BY D.INV_CODE,B.INV_CHN_DESC,B.DESCRIPTION,U.UNIT_CHN_DESC ";
    	
    	return sql;
    	
    }
    
    private boolean checkPackageConditions(){
    	if( null == this.getValueString("DEPT_CODE") || "".equals(this.getValueString("DEPT_CODE")) || this.getValueString("DEPT_CODE").length() <= 0){
    		messageBox("请选择请领部门！");
			return false;
    	}
    	if( null == this.getValueString("START_DATE") || "".equals(this.getValueString("START_DATE")) || this.getValueString("START_DATE").length() <= 0 || null == this.getValueString("END_DATE") || "".equals(this.getValueString("END_DATE")) || this.getValueString("END_DATE").length() <= 0 ){
    		messageBox("请输入日期！");
    		return false;
    	}
    	return true;
    }
    
    
    private boolean checkInvConditions(){
    	if( null == this.getValueString("DEPT_CODE_SEC") || "".equals(this.getValueString("DEPT_CODE_SEC")) || this.getValueString("DEPT_CODE_SEC").length() <= 0){
    		messageBox("请选择请领部门！");
			return false;
    	}
    	if( null == this.getValueString("START_DATE") || "".equals(this.getValueString("START_DATE")) || this.getValueString("START_DATE").length() <= 0 || null == this.getValueString("END_DATE") || "".equals(this.getValueString("END_DATE")) || this.getValueString("END_DATE").length() <= 0 ){
    		messageBox("请输入日期！");
    		return false;
    	}
    	return true;
    }
    
	 /**
	  * 导出excel
	  * */
	 public void onExcel(){

		 if(tabPane.getSelectedIndex() == 0){
			 TTable table = (TTable) this.getComponent("TABLEM");
		     if (table.getRowCount() > 0){
		    	 ExportExcelUtil.getInstance().exportExcel(table, "诊疗包退库查询报表");
		     }
		 }else if(tabPane.getSelectedIndex() == 1){
			 TTable table = (TTable) this.getComponent("TABLEMSEC");
		     if (table.getRowCount() > 0){
		    	 ExportExcelUtil.getInstance().exportExcel(table, "物资退库查询报表");
		     }
		 }

	 }

	
	
	private void setTimes(){
		//初始化    退货日期查询区间
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_DATE", 
				new Timestamp(date.getTime() + -7 * 24L * 60L * 60L * 1000L).toString()
					.substring(0, 10).replace("-", "/") + " 00:00:00");
		this.setValue("END_DATE", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
		
		
		this.setValue("START_DATE_SEC", 
				new Timestamp(date.getTime() + -7 * 24L * 60L * 60L * 1000L).toString()
					.substring(0, 10).replace("-", "/") + " 00:00:00");
		this.setValue("END_DATE_SEC", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
		
	}
	
	private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
