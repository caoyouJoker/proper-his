package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;

/**
* <p>
* Title: 物资消耗月结报表
* </p>
*
* <p>
* Description: 物资消耗月结报表
* </p>
*
* <p>
* Copyright: Copyright (c) 2009
* </p>
*
* <p>
* Company: ProperSoft
* </p>
*
* @author  Huangtt 2013.05.06
* @version 1.0
*/
public class INVConsMonBalPrintControl extends TControl{
	public INVConsMonBalPrintControl(){
		
	}
	private TTable table;
	
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
                      StringTool.rollDate(date, -30).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        
     // 初始化统计区间
        this.setValue("E_DATE",
                      date.toString().substring(0, 10).replace('-', '/'));
        this.setValue("S_DATE",
                      StringTool.rollDate(date, -30).toString().substring(0, 10).
                      replace('-', '/'));
        //获得TABLE对象
        table=(	TTable)this.getComponent("TABLE");
        TTextFormat dept = (TTextFormat) getComponent("DEPT");
       // dept.setEnabled(false);
        dept.setValue(Operator.getDept());
    }
    
    /**
     * 查询方法
     */
    public void onQuery() {
    	if ("".equals(getValue("DEPT"))||getValue("DEPT")==null||getValue("DEPT").toString().length()==0) {
			messageBox("请选择部门！");
			return;       
		}
    
    	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue("START_DATE")), "yyyyMMdd")+" 00:00:00";
		String endTime = StringTool.getString(TypeTool.getTimestamp(getValue("END_DATE")), "yyyyMMdd")+" 23:59:59";
		String dept=this.getValueString("DEPT");
		String sql = "SELECT B.INV_CODE,B.INV_CHN_DESC, B.DESCRIPTION, COUNT(A.INV_CODE) QTY, C.MAN_CHN_DESC" +
				" ,A.UNIT_PRICE COST_PRICE,COUNT(A.INV_CODE)*A.UNIT_PRICE AS TOT_AMT,A.STOCK_UNIT "+//20150203 wangjingchun add
				" FROM INV_STOCKDD A, INV_BASE B, SYS_MANUFACTURER C" +
				" WHERE  B.SEQMAN_FLG='Y' and B.EXPENSIVE_FLG='Y'" +
				" AND A.WAST_FLG = 'Y'" +
				" AND A.INV_CODE = B.INV_CODE" +  
				" AND B.MAN_CODE = C.MAN_CODE" +
    			" AND A.OUT_DATE BETWEEN TO_DATE('"+startTime+ "','yyyymmdd hh24:mi:ss') " + "AND TO_DATE('" + endTime
				+ "','yyyymmdd hh24:mi:ss')"+
				" and  exists (select 1 from inv_stockm  m where a.inv_code=m.inv_code and m.org_code='"+getValue("DEPT")+"')";
		
		sql += " GROUP BY  B.INV_CODE,  C.MAN_CHN_DESC,A.INV_CODE,B.DESCRIPTION,B.INV_CHN_DESC,A.UNIT_PRICE,A.STOCK_UNIT ORDER BY  MAN_CHN_DESC ,A.INV_CODE ";//20150203 wangjingchun modify
//		System.out.println("sql==="+sql);
    	TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
    	if(selParm.getCount()<0){
        	this.messageBox("没有要查询的数据");  
        	return;
        }
    	//加载table上的数据
	    this.callFunction("UI|TABLE|setParmValue", selParm);
    
    	
    	
    }
    
    /**
     * 打印方法
     */
    public void onPrint() {
    	// HH:mm:ss
    	String startTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "START_DATE")), "yyyy/MM/dd");
        String endTime = StringTool.getString(TypeTool.getTimestamp(getValue(
        "END_DATE")), "yyyy/MM/dd");
        Timestamp datetime = SystemTool.getInstance().getDate();

        table = this.getTable("TABLE");


        if (table.getRowCount() <= 0) {
           this.messageBox("没有打印数据");
           return;
        }
        TParm data = new TParm();
        TTextFormat dept = (TTextFormat) getComponent("DEPT");
        data.setData("TITLE1", "TEXT", Operator.getHospitalCHNShortName());
        data.setData("TITLE", "TEXT", dept.getText()+"消耗器材使用清单("+startTime+"---"+endTime+")");
        data.setData("DATE", "TEXT", "制表日期: " +datetime.toString().substring(0, 10).replace('-', '/'));
        data.setData("DEPT", "TEXT", "执行科室："+dept.getText());
       
    	//数据
    	TParm parm = new TParm();
    	TParm tableParm = table.getParmValue();
    	String manDesc = tableParm.getValue("MAN_CHN_DESC", 0);
    	
    	for(int i=0;i<table.getRowCount();i++){
    		if(!manDesc.equals(tableParm.getValue("MAN_CHN_DESC", i))){
    			parm.addData("INV_DESC", "以上为"+manDesc+"提供");
        		parm.addData("DESCRIPTION", "");
        		parm.addData("QTY", "");
        		manDesc = tableParm.getValue("MAN_CHN_DESC", i);
    		}
    		parm.addData("INV_DESC", tableParm.getValue("INV_CHN_DESC",i));
    		parm.addData("DESCRIPTION", tableParm.getValue("DESCRIPTION",i));
    		parm.addData("QTY", tableParm.getValue("QTY",i));
    		
    	}
    	if(manDesc.equals(tableParm.getValue("MAN_CHN_DESC", table.getRowCount()-1))){
    		parm.addData("INV_DESC", "以上为"+manDesc+"提供");
    		parm.addData("DESCRIPTION", "");
    		parm.addData("QTY", "");
    	}
    	
    	
    	parm.setCount(parm.getCount("INV_DESC"));
    	parm.addData("SYSTEM", "COLUMNS", "INV_DESC");
    	parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
    	parm.addData("SYSTEM", "COLUMNS", "QTY");

    	
    	data.setData("TABLE",parm.getData());
    	
    	this.openPrintWindow("%ROOT%\\config\\prt\\inv\\INVConsMonBalPrint.jhw", data);
    }
    
    /**
	 * 导出Excel
	 */
	public void onExport() {

		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		TTable table = (TTable) callFunction("UI|Table|getThis");
		ExportExcelUtil.getInstance().exportExcel(table, "物资消耗月结报表");
	}
	
	/**
     * 关闭窗口
     */
    public void close(){
         this.closeWindow();
    }
    

	/**
     * 清空方法
     */
	
	public void onClear() {            
		
		String clearString ="START_DATE;END_DATE"; 	
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        // 初始化统计区间
        this.setValue("E_DATE", date.toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        this.setValue("S_DATE", StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/'));
        // 初始化查询区间                 
        this.setValue("END_DATE", date.toString().substring(0, 10).replace('-', '/') + " 23:59:59");
        this.setValue("START_DATE",StringTool.rollDate(date, -7).toString().substring(0, 10).replace('-', '/') + " 00:00:00");
        
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
