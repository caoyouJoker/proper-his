package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inv.InvRegressGoodsTotalTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 物资退货统计Control 
 * </p>  
 *
 * <p>
 * Description: 物资退货统计Control
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
 * @author zhangh 2013.5.8
 * @version 1.0
 */

public class INVRegressGoodsReportControl
    extends TControl {
	
    public INVRegressGoodsReportControl() {
    }
    
    private TTable tableUp;
    private TTable tableDown;
    
    /*
     * 界面初始化
     * @see com.dongyang.control.TControl#onInit()
     */
    public void onInit(){
    	initUI();
    }

    /*
     * 初始化界面
     */
    private void initUI() {
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_TIME", new Date());
		this.setValue("END_TIME", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
		tableUp = (TTable) this.getComponent("TABLE");
		tableDown = (TTable) this.getComponent("TABLE_DOWN");
	}
    
    /*
     * 查询方法
     */
    public void onQuery(){
    	query();
    }

	private void query() {
		TParm parm = new TParm();
		if(this.getValueString("START_TIME") == null || this.getValueString("START_TIME").length() <= 0
				|| this.getValueString("END_TIME") == null || this.getValueString("END_TIME").length() <= 0){
			this.messageBox("请填写查询时间！");
			return;
		}
		//开始时间
		String startTime = this.getValueString("START_TIME").substring(0, 
				this.getValueString("START_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		parm.setData("START_TIME", startTime);
		//结束时间
		String endTime = this.getValueString("END_TIME").substring(0, 
				this.getValueString("END_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		parm.setData("END_TIME", endTime);
		//供应商
		if(this.getValueString("SUP_CODE") != null && this.getValueString("SUP_CODE").length() > 0){
			String supCode = this.getValueString("SUP_CODE");
			parm.setData("SUP_CODE", supCode);
		}
		TParm result = InvRegressGoodsTotalTool.getInstance().onQueryRegressGoodsTotal(parm);
		if(result.getCount() <= 0){
			this.messageBox("无查询结果");
			tableUp.removeRowAll();
			return;
		}
		tableUp.setParmValue(result);
	}
	

	/*
     * 清空方法
     */
    public void onClear(){
    	this.clearValue("SUP_CODE");
    	tableUp.removeRowAll();
    }
    
    /*
     * 打印上表格方法
     */
    public void onPrintUp() {
		if(tableUp.getRowCount() <= 0){
			this.messageBox("无打印数据！");
			return;
		}
		TParm tableData = tableUp.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("SUP_CHN_DESC", tableData.getValue("SUP_CHN_DESC", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "退货统计表");
		printParm.setData("USER", "TEXT", "制表人：" + Operator.getName());
		printParm.setData("DATE", "TEXT", "制表时间：" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVRegressGoodsDetail.jhw",
				printParm);
	}
    
    /*
     * 打印下表格方法
     */
    public void onPrintDown() {
		if(tableUp.getRowCount() <= 0){
			this.messageBox("无打印数据！");
			return;
		}
		TParm tableData = tableDown.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			printData.addData("RETURN_NO", tableData.getData("RETURN_NO", i));
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("RFID", tableData.getData("RFID", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("SUP_CHN_DESC", tableData.getValue("SUP_CHN_DESC", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "RETURN_NO");
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "RFID");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "SUP_CHN_DESC");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "退货统计明细表");
		printParm.setData("USER", "TEXT", "制表人：" + Operator.getName());
		printParm.setData("DATE", "TEXT", "制表时间：" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVRegressGoodsTotal.jhw",
				printParm);
	}
	
	/**
	 * 上表格单击事件  
	 */
	public void onClick(){  
		String invCode = tableUp.getValueAt(tableUp.getSelectedRow(), 0).toString();
		TParm parm = new TParm();
		parm.setData("INV_CODE", invCode);  
		TParm result = InvRegressGoodsTotalTool.getInstance().onQueryRegressGoodsTotal(parm);
		if(result.getCount() <= 0){
			return;
		}   
		tableDown.setParmValue(result);
	}
	
	/*
     * 导出上表excel方法
     */
    public void onExcelUp(){
    	if(tableUp.getRowCount() <= 0){
			this.messageBox("没有数据！");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(tableUp, "物资退货统计");
    }
    
    /*
     * 导出下表excel方法
     */
    public void onExcelDown(){
    	if(tableDown.getRowCount() <= 0){
			this.messageBox("没有数据！");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(tableDown, "物资退货明细");
    }
}