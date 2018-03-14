package com.javahis.ui.opb;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

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


public class OPBConsultationFeePerfControl extends TControl {
	
	
	//医嘱实现那块需要个计数
	private int flag=0;
	
	public OPBConsultationFeePerfControl() {
		
	}
	
	public void onInit(){
		super.init();
		initPage();
	}
	
	public void initPage(){
		//设置区域
		setValue("REGION_CODE", "H01");


		// 定义一个容器，设置参数类型为“PHA”（即药品）
		TParm parmIn = new TParm();
		parmIn.setData("CAT1_TYPE", "PHA");

		this.getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// 删除医嘱名称所在的table一行记录
		TTable table = (TTable) this.getComponent("Table_Order");
		callFunction("UI|" + table + "|addEventListener", table + "->"
				+ TTableEvent.CLICKED, this, "onDelete");

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

	  /*
		 * 向Table_Order中添加医嘱
		 * 
		 */
	    
		public void addOrder(){
			String ORDER_CODE=getValueString("ORDER_CODE");
			String ORDER_DESC=getValueString("ORDER_DESC");
			
			TParm tParm=new TParm();
			TTable  table = (TTable)this.getComponent("Table_Order") ;
			
			if(null==ORDER_CODE||"".equals(ORDER_CODE)){
				this.messageBox("未输入医嘱！");
				return;
			}
			
			if(flag==0){
				//先新建数据，然后可以利用table.setData(tParm);添加到table中
				tParm.setData("ORDER_CODE", ORDER_CODE);
				tParm.setData("ORDER_DESC", ORDER_DESC);
				table.addRow(tParm);
				table.update();
				flag++;
			}else {
					for(int j=0;j<flag;j++){
						if(table.getItemString(j, "ORDER_CODE").equals(ORDER_CODE)){
							this.messageBox("该医嘱已添加！");
							return;
						}
					}
						
					//先新建数据，然后可以利用table.setData(tParm);添加到table中
					tParm.setData("ORDER_CODE", ORDER_CODE);
					tParm.setData("ORDER_DESC", ORDER_DESC);
					table.addRow(tParm);
					table.update();
					flag++;
			}
			
		}
		
	/*
	 * 医嘱名称所在的table中点删除就会删除一条记录
	 */
	public void onDelete() {
		TTable table = (TTable) this.getComponent("Table_Order");
		int delrow = table.getSelectedRow();
		table.removeRow(delrow);
		flag--;

	}
	
	
	
	/**
     * 根据医嘱进行查询
     */
    public void onQuery(){
    	TTable  table1 = (TTable)this.getComponent("TTable") ;
    	TTable  table = (TTable)this.getComponent("Table_Order") ;
    	
    	String order_code="";
    	//获取order_code
    	if(flag==1){
    		order_code=" ='"+table.getItemString(0, "ORDER_CODE")+"' ";
    	}else{
    			for(int i=0;i<flag-1;i++){
    				order_code="'"+(String)table.getItemString(i, "ORDER_CODE")+"','";
    				}
    			order_code+=table.getItemString(flag-1, "ORDER_CODE")+"'";
    	    	order_code="in ( "+order_code+")";
    	}
    	
    	String sql="SELECT B.REGION_CODE,A.CASE_NO,A.DEPT_CODE,A.STATION_CODE,A.DR_CODE,A.EXE_DEPT_CODE," 
    			+"A.EXE_STATION_CODE,A.EXE_DR_CODE,A.OWN_PRICE,A.NHI_PRICE,A.TOT_AMT "
    			+" FROM IBS_ORDD A, IBS_ORDM B "
    			+"WHERE A.ORDER_CODE "
    			+order_code
    			+" AND A.CASE_NO = B.CASE_NO"
    			+" AND A.CASE_NO_SEQ = B.CASE_NO_SEQ";
    							
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
    	
    	table1.setParmValue(parm);
    	
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
    	String clearStr = "ORDER_CODE;ORDER_DESC";
        this.clearValue(clearStr);
        
        TTable  table1 = this.getTable("TTable");
        TTable  table = this.getTable("Table_Order");

        table1.removeRowAll();
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

}
