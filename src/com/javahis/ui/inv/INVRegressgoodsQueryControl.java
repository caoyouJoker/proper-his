package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
* <p>
* Title: 退货出库统计表
* </p>
*
* <p>
* Description: 退货出库统计表
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
* @author  Huangtt 2013.05.09
* @version 1.0
*/
public class INVRegressgoodsQueryControl extends TControl {
	public INVRegressgoodsQueryControl(){}
	
	private TTable table;
	String state="";
	
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

		
		String sql="SELECT F.DISPENSE_NO, A.DISPENSE_DATE, F.INV_CODE, F.INVSEQ_NO," +
				" C.INV_CHN_DESC, C.DESCRIPTION, B.QTY, E.UNIT_CHN_DESC, F.UNIT_PRICE," +
				" B.QTY * F.UNIT_PRICE AS AMT, B.VALID_DATE, B.BATCH_NO" +
				" FROM INV_DISPENSEM A," +
				" INV_DISPENSED B," +
				" INV_BASE C," +
				" INV_REQUESTD D," +
				" SYS_UNIT E," +
				" INV_DISPENSEDD F" +
				" WHERE A.DISPENSE_NO = B.DISPENSE_NO" +
				" AND B.DISPENSE_NO=F.DISPENSE_NO" +
				" AND F.INV_CODE = C.INV_CODE" +
				" AND A.REQUEST_NO = D.REQUEST_NO" +
				" AND B.DISPENSE_UNIT = E.UNIT_CODE";
		
		if (this.getRadioButton("RadioButton1").isSelected()) {
			sql += " AND F.FLG='N'";
			state="未出库";
		}
		if (this.getRadioButton("RadioButton2").isSelected()) {
			sql += " AND F.FLG='Y'";
			state="出库";
		}
		
		 // 查询时间
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
        	 String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "START_DATE")), "yyyyMMdd")+" 00:00:00";
	         String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
	         "END_DATE")), "yyyyMMdd")+" 23:59:59";
	         
//	         System.out.println(startTime);
	         
	         sql += " AND A.DISPENSE_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')";
        }
        
     // 退货部门
        
        if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
        	String orgCode=TypeTool.getString(getValue("TO_ORG_CODE"));
            sql +=" AND A.TO_ORG_CODE="+orgCode; 
        }else{
        	this.messageBox("退货部门不能为空");
            return;
        }
		
     // 物资代码
        if (!"".equals(this.getValueString("INV_CODE"))) {
        	String invCode=TypeTool.getString(getValue("INV_CODE"));
       	 	sql +=" AND F.INV_CODE='"+invCode+"'";
        }
        
        System.out.println(sql);
        
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        
        //计算退货总金额   SUM_REG_AMT
        Double sum = 0.00;
        for(int i=0;i<selParm.getCount();i++){
        	sum += selParm.getDouble("AMT", i);
        }
//        System.out.println(sum);
        this.setValue("SUM_REG_AMT", sum);
        
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
   	
//   	String supply=TypeTool.getString(getValue("VERIFYIN_DEPT"));
   	
        data.setData("TITLE", "TEXT", "物资退货单");
        data.setData("DEPT", "TEXT", "统计部门："+TypeTool.getString(getValue("TO_ORG_CODE")));
        data.setData("STATE", "TEXT", "出库状态："+state);
        data.setData("DATE_AREA", "TEXT", "统计区间："+startTime+"~"+endTime);
        data.setData("DATE", "TEXT", "制表日期: " +
               datetime.toString().substring(0, 10).replace('-', '/'));
        data.setData("USER", "TEXT", "制表人: " + Operator.getName());
        //数据
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();

        for(int i=0;i<table.getRowCount();i++){
        	parm.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO",i));
        	parm.addData("INV_CHN_DESC", tableParm.getValue("INV_CHN_DESC",i));
        	parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
        	parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC",i));
        	parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE",i));
        	parm.addData("QTY", tableParm.getValue("QTY",i));
        	parm.addData("AMT", tableParm.getValue("AMT",i));
        	parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO",i));
        	parm.addData("VALID_DATE", StringTool.getString(tableParm.getTimestamp("VALID_DATE",i),"yyyy/MM/dd"));
        	
   		
        }
   	
        parm.setCount(parm.getCount("DISPENSE_NO"));
        parm.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");       
        parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "AMT");
        parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");



//        System.out.println(parm);
        data.setData("TABLE",parm.getData());
   	
        this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVRegressgoodsQuery.jhw", data);

    	
    }
    
    
    
    
    /**
     * 清空方法
     */
	
	public void onClear() {
		String clearString ="INV_CODE;INV_CHN_DESC;TO_ORG_CODE;SUM_REG_AMT"; 	
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
      * 得到RadioButton对象
      *
      * @param tagName
      *            元素TAG名称
      * @return
      */
     private TRadioButton getRadioButton(String tagName) {
         return (TRadioButton) getComponent(tagName);
     }

     
	

}
