package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inv.INVGoodsApplyTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 物资申请汇总Control 
 * </p>
 *
 * <p>
 * Description: 物资申请汇总Control 
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

public class INVGoodsApplyControl
    extends TControl {
	
    public INVGoodsApplyControl() {
    }
    
    private TTable table;
    
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
		this.setValue("START_TIME", 
				new Timestamp(date.getTime() + -7 * 24L * 60L * 60L * 1000L).toString()
					.substring(0, 10).replace("-", "/") + " 00:00:00");
		this.setValue("END_TIME", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
		table = (TTable) this.getComponent("TABLE");
		TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "OTH");
		// 设置弹出菜单
        getTextField("INV_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"),
            parm);
		// 定义接受返回值方法
        getTextField("INV_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
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
        String order_code = parm.getValue("INV_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("INV_CODE").setValue(order_code);
        String order_desc = parm.getValue("INV_CHN_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("INV_DESC").setValue(order_desc);
    }
    
    /*
     * 查询方法
     */
    public void onQuery(){
    	table.removeRowAll();
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
		//物资代码
		if(this.getValueString("INV_CODE") != null && this.getValueString("INV_CODE").length() > 0){
			String invCode = this.getValueString("INV_CODE");
			parm.setData("INV_CODE", invCode);
		}
		//申请部门
		if(this.getValueString("ORG_CODE") != null && this.getValueString("ORG_CODE").length() > 0){
			String orgCode = this.getValueString("ORG_CODE");
			parm.setData("TO_ORG_CODE", orgCode);
		}
		TParm result = INVGoodsApplyTool.getInstance().onQueryGoodsApplyReport(parm);
		if(result.getCount() <= 0){
			this.messageBox("无查询结果");
			table.removeRowAll();
			return;
		}
		table.setParmValue(result);
	}
	
	/*
     * 清空方法
     */
    public void onClear(){
    	this.clearValue("INV_CODE;INV_DESC;SUP_CODE;ORG_CODE");
    	table.removeRowAll();
    }
    
    /*
     * 打印方法
     */
    public void onPrint(){
    	print();
    }

	private void print() {
		if(table.getRowCount() <= 0){
			this.messageBox("无打印数据！");
			return;
		}
		TParm tableData = table.getParmValue();
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("REQUEST_NO"); i++) {
			printData.addData("DEPT_CHN_DESC", tableData.getData("DEPT_CHN_DESC", i));
			printData.addData("REN_DESC", tableData.getData("REN_DESC", i));
			printData.addData("REQUEST_NO", tableData.getData("REQUEST_NO", i));
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INVSEQ_NO", tableData.getData("INVSEQ_NO", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("QTY", tableData.getData("QTY", i));
			printData.addData("BATCH_NO", tableData.getData("BATCH_NO", i));
		}
		printData.setCount(tableData.getCount("REQUEST_NO"));
		printData.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "REN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "REQUEST_NO");
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INVSEQ_NO");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		printData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "物资请领汇总表");
		if(this.getValueString("ORG_FROM_CODE") != null && this.getValueString("ORG_FROM_CODE").length() > 0){
			TTextFormat dept = (TTextFormat) getComponent("ORG_FROM_CODE");
			printParm.setData("ORG_FROM_CODE", "TEXT", "验收部门：" + dept.getText());
		}else{
			printParm.setData("ORG_FROM_CODE", "TEXT", "验收部门：所有");
		}
		printParm.setData("DATE", "TEXT", "制表时间：" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVGoodsApplyReport.jhw",
				printParm);
	}
	
	/*
     * 导出excel方法
     */
    public void onExecl(){
    	if(table.getRowCount() <= 0){
			this.messageBox("没有数据！");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(table, "物资验收入库汇总");
    }
    
    /**
     * 得到TextField对象
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
