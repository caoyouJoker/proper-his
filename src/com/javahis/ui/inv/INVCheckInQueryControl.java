package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
* <p>
* Title: 验收入库统计表
* </p>
*
* <p>
* Description: 验收入库统计表
* </p>
*
* <p>
* Copyright: Copyright (c) 2009
* </p>
*
* <p>
* Company: JavaHis
* </p>
*
* @author  Huangtt 2013.05.07
* @version 1.0
*/

public class INVCheckInQueryControl extends TControl{
	private TTable table;
	public INVCheckInQueryControl(){}
	
	/**
	 * 初始化
	 */
	public void init(){
		super.init();
		initPage();

	}
	
	/**
     * 初始画面数据
     */
    private void initPage() {
    	Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
      //获得TABLE对象
        table=(	TTable)this.getComponent("TABLE");
		
    }
    
    /**
     * 查询
     */
	public void onQuery(){
		
		
		String sql="SELECT E.SUP_ABS_DESC, A.VERIFYIN_DATE, B.INV_CODE, A.VERIFYIN_NO," +
				" C.INV_CHN_DESC, C.DESCRIPTION, B.QTY, B.GIFT_QTY, D.UNIT_CHN_DESC," +
				" B.UNIT_PRICE, A.INVOICE_AMT, B.VALID_DATE, B.BATCH_NO, B.REN_CODE," +
				" F.ORG_DESC" +
				" FROM INV_VERIFYINM A," +
				" INV_VERIFYIND B," +
				" INV_BASE C," +
				" SYS_UNIT D," +
				" SYS_SUPPLIER E," +
				" INV_ORG F" +
				" WHERE A.VERIFYIN_NO = B.VERIFYIN_NO" +
				" AND B.INV_CODE = C.INV_CODE" +
				" AND A.VERIFYIN_DEPT = F.ORG_CODE" +
				" AND A.SUP_CODE = E.SUP_CODE" +
				" AND B.STOCK_UNIT = D.UNIT_CODE";
		
		 // 查询时间
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
        	 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "START_DATE")), "yyyyMMdd")+" 00:00:00";
	         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "END_DATE")), "yyyyMMdd")+" 23:59:59";
	         
//	         System.out.println(startTime);
	         
	         sql += " AND A.VERIFYIN_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
        }
        // 验收部门
       
        if (!"".equals(this.getValueString("VERIFYIN_DEPT"))) {
        	String orgCode=TypeTool.getString(getValue("VERIFYIN_DEPT"));
            sql +=" AND A.VERIFYIN_DEPT="+orgCode; 
        }else{
        	this.messageBox("验收部门不能为空");
        	return;
        }
        // 供货厂商
        if (!"".equals(this.getValueString("SUP_CODE"))) {
        	String supCode=TypeTool.getString(getValue("SUP_CODE"));
        	sql +=" AND A.SUP_CODE="+supCode;
        }
        // 物资代码
        if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
       	 	sql +=" AND B.INV_CODE= '"+invCode+"'";
        }
       
        
//        System.out.println(sql);
        
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        
//        System.out.println(selParm);
        
        if(selParm.getCount()<0){
        	this.messageBox("没有要查询的数据");
        	return;
        }
        
       
        //计算购入总金额   SUM_CHK_AMT
        Double sum = 0.00;
        for(int i=0;i<selParm.getCount();i++){
        	sum += selParm.getDouble("INVOICE_AMT", i);
        }
//        System.out.println(sum);
        this.setValue("SUM_CHK_AMT", sum);
        
        //加载table上的数据
	    this.callFunction("UI|TABLE|setParmValue", selParm);
		
		
	}
    
    /**
     * 打印方法
     */
    public void onPrint() {
    	 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
         "START_DATE")), "yyyy/MM/dd HH:mm:ss");
         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
         "END_DATE")), "yyyy/MM/dd HH:mm:ss");
    	Timestamp datetime = SystemTool.getInstance().getDate();

    	table = this.getTable("TABLE");


    	if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
    	TParm data = new TParm();
    	//表头    getSelectedName
    	

    	

    	
//    	TTextFormat t = (TTextFormat) this.getComponent("VERIFYIN_DEPT");
    	String aa=this.getTextFormat("VERIFYIN_DEPT").getSelectText();
    	
    	
    	System.out.println("aa="+aa);

         
    	data.setData("TITLE", "TEXT", "物资验收入库单");
    	data.setData("SUPPLY", "TEXT", "验收部门："+TypeTool.getString(getValue("VERIFYIN_DEPT")));
    	data.setData("DATE_AREA", "TEXT", "统计区间："+startTime+"~"+endTime);
    	data.setData("DATE", "TEXT", "制表日期: " +
                datetime.toString().substring(0, 10).replace('-', '/'));
    	data.setData("USER", "TEXT", "制表人: " + Operator.getName());
    	//数据
    	TParm parm = new TParm();
    	TParm tableParm = table.getParmValue();

    	for(int i=0;i<table.getRowCount();i++){
    		parm.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC",i));
    		parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
    		parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC",i));
    		parm.addData("QTY", tableParm.getValue("QTY",i));
    		parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE",i));
    		parm.addData("INVOICE_AMT", tableParm.getValue("INVOICE_AMT",i));
    		parm.addData("VALID_DATE", StringTool.getString(tableParm.getTimestamp("VALID_DATE",i),"yyyy/MM/dd"));
    		parm.addData("SUP_ABS_DESC", tableParm.getValue("SUP_ABS_DESC",i));
    		parm.addData("VERIFYIN_DATE", StringTool.getString(tableParm.getTimestamp("VERIFYIN_DATE",i),"yyyy/MM/dd HH:mm:ss"));
    		parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO",i));
    		
    	}
    	
    	parm.setCount(parm.getCount("INV_CHN_DESC"));
    	parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
    	parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "QTY");
    	parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
    	parm.addData("SYSTEM", "COLUMNS", "INVOICE_AMT");
    	parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
    	parm.addData("SYSTEM", "COLUMNS", "SUP_ABS_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_DATE");
    	parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//    	System.out.println(parm);
    	data.setData("TABLE",parm.getData());
    	
    	this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVCheckInQuery.jhw", data);

    	
    	
    }
    
    
	
	/**
     * 清空方法
     */
	
	public void onClear() {
		String clearString ="INV_CODE;INV_CHN_DESC;VERIFYIN_DEPT;SUP_CODE;SUM_CHK_AMT"; 	
        this.clearValue(clearString);
     // 初始化查询区间
        Timestamp date = StringTool.getTimestamp(new Date());
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        
        table.removeRowAll();
	}
	

	
	/**
     * 关闭窗口
     */
    public void close(){
         this.closeWindow();
    }
    
    /**
	 * 导出EXECL
	 */
	public void onExecl() {
		ExportExcelUtil.getInstance().exportExcel(this.getTable("TABLE"),
				"验收入库统计表");
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
      * 得到TextFormat对象
      *
      * @param tagName
      *            元素TAG名称
      * @return
      */
     private TTextFormat getTextFormat(String tagName) {
         return (TTextFormat) getComponent(tagName);
     }

	

    
}
