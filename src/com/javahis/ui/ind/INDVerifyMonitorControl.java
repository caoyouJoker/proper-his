package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Calendar;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 2012年一、三季度短缺药品采购监测表
 * </p>
 *
 * <p>
 * Description: 2012年一、三季度短缺药品采购监测表
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


public class INDVerifyMonitorControl extends TControl {
	
	//医嘱实现那块需要个计数
	private int flag=0;
	
	public INDVerifyMonitorControl() {
	}
	
	//初始化页面
	public void onInit() {
		super.onInit();
		initPage();
	}
	
	
	//initPage
	public void initPage(){
		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月25)
		setValue("END_DATE", dateTime);

		// 起始时间(上个月26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		
		
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
		TTable  table = (TTable)this.getComponent("Table_Order") ;
        int delrow = table.getSelectedRow();
        table.removeRow(delrow);
        flag--;
    }
	
    
    /**
     * 根据药物名称以及操作时间进行查询
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
    	
    	
    	String sDate = this.getValueString("START_DATE");
    	String eDate = this.getValueString("END_DATE");
    	
    	String opt_date = "";
    	sDate = sDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	opt_date +=" AND a.opt_date > TO_DATE('" + sDate +
        "','YYYYMMDDHH24MISS') ";
    	eDate = eDate.substring(0, 19).replace(" ", "").replace("/", "").
          replace(":", "").replace("-", "");
    	opt_date +=" AND a.opt_date < TO_DATE('" + eDate +
        "','YYYYMMDDHH24MISS') ";
    	
    	String sql="SELECT b.REGION_CODE,b.ORDER_DESC,b.SPECIFICATION,b.OWN_PRICE," +
	   		       "a.VERIFYIN_PRICE, a.MAN_CODE, SUM(a.VERIFYIN_QTY) as VERIFYIN_QTY " +
	   		       "FROM IND_VERIFYIND a, SYS_FEE b  " +
	   		       "WHERE a.ORDER_CODE "+order_code+
	   		       " AND a.ORDER_CODE = b.ORDER_CODE "+opt_date+
	   		       " GROUP BY b.ORDER_DESC,b.SPECIFICATION,b.OWN_PRICE, a.VERIFYIN_PRICE, a.MAN_CODE,b.REGION_CODE";
  
    	TParm newdata = new TParm(TJDODBTool.getInstance().select(sql));
    	
    	/*System.out.println("sql++++"+sql);*/
    	
    	if(newdata.getErrCode() < 0 ){
    		this.messageBox(newdata.getErrText());
    		return;
    	}
        if(newdata.getCount() <= 0)
        {
        	this.messageBox("查无数据");
        }
    	
        
      //在table中显示查询信息
        table1.setParmValue(newdata);
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
        ExportExcelUtil.getInstance().exportExcel(table, "药品采购监测");
    }
    
    
    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr = "ORDER_CODE;ORDER_DESC;START_DATE;END_DATE";
        this.clearValue(clearStr);

		// 初始化统计区间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(
				TypeTool.getString(date).substring(0, 4) + "/"
						+ TypeTool.getString(date).substring(5, 7)
						+ "/25 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (本月25)
		setValue("END_DATE", dateTime);

		// 起始时间(上个月26)
		Calendar cd = Calendar.getInstance();
		cd.setTimeInMillis(date.getTime());
		cd.add(Calendar.MONTH, -1);
		Timestamp endDateTimestamp = new Timestamp(cd.getTimeInMillis());

		setValue("START_DATE", endDateTimestamp.toString().substring(0, 4)
				+ "/" + endDateTimestamp.toString().substring(5, 7)
				+ "/26 00:00:00");
		
        TTable  table = this.getTable("Table_Order");
        TTable  table1 = this.getTable("TTable");
        table.removeRowAll();
        table1.removeRowAll();
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
