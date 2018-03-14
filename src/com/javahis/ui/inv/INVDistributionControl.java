package com.javahis.ui.inv;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.inv.INVDistributionTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 物资分布Control 
 * </p>
 *
 * <p>
 * Description: 物资分布Control 
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

public class INVDistributionControl
    extends TControl {
	
    public INVDistributionControl() {
    }
    
    private TTable tableNotNull;
    private TTable tableIsNull;
    
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
    	tableNotNull = (TTable) this.getComponent("TABLE_CAB_NOT_NULL");
    	tableIsNull = (TTable) this.getComponent("TABLE_CAB_NULL");
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
    	query();
    }

	private void query() {
		TParm parm = new TParm();
		//物资代码
		if(this.getValueString("INV_CODE") != null && this.getValueString("INV_CODE").length() > 0){
			String invCode = this.getValueString("INV_CODE");
			parm.setData("INV_CODE", invCode);
		}
		//智能柜ID
		if(this.getValueString("CABINET_ID") != null && this.getValueString("CABINET_ID").length() > 0){
			String cabinetId = this.getValueString("CABINET_ID");
			parm.setData("CABINET_ID", cabinetId);
		}
		TParm resultNotNull = INVDistributionTool.getInstance().onQueryCabinetIdNotNull(parm);
		TParm resultIsNull = INVDistributionTool.getInstance().onQueryCabinetIdIsNull(parm);
		if(resultNotNull.getCount() <= 0){
			this.messageBox("无查询数据！");
			tableNotNull.removeRowAll();
			return;
		}
		if(resultIsNull.getCount() <= 0){
			tableIsNull.removeRowAll();
			return;
		}
		tableNotNull.setParmValue(resultNotNull);
		tableIsNull.setParmValue(resultIsNull);
	}
	
	/*
     * 清空方法
     */
    public void onClear(){
    	this.clearValue("INV_CODE;INV_DESC;CABINET_ID");
    	tableNotNull.removeRowAll();
    	tableIsNull.removeRowAll();
    }
    
    /*
     * 打印上表方法
     */
    public void onPrintUp(){
    	TParm parm = tableNotNull.getParmValue();
    	print(parm, "up");
    }
    
    /*
     * 打印下表方法
     */
    public void onPrintDown(){
    	TParm parm = tableIsNull.getParmValue();
    	print(parm, "down");
    }

	private void print(TParm parm, String type) {
		if(tableNotNull.getRowCount() <= 0 && type.equals("up")){
			this.messageBox("上表无打印数据！");
			return;
		}else if(tableIsNull.getRowCount() <= 0 && type.equals("down")){
			this.messageBox("下表无打印数据！");
			return;
		}
		TParm tableData = parm;
		TParm printData = new TParm();
		TParm printParm = new TParm();
		for (int i = 0; i < tableData.getCount("INV_CODE"); i++) {
			//INV_CODE;INV_CHN_DESC;DESCRIPTION;MAN_CHN_DESC;CABINET_ID;CABINET_DESC;QTY
			printData.addData("INV_CODE", tableData.getData("INV_CODE", i));
			printData.addData("INV_CHN_DESC", tableData.getData("INV_CHN_DESC", i));
			printData.addData("DESCRIPTION", tableData.getData("DESCRIPTION", i));
			printData.addData("MAN_CHN_DESC", tableData.getData("MAN_CHN_DESC", i));
			if(type.equals("up")){
				printData.addData("CABINET_ID", tableData.getData("CABINET_ID", i));
				printData.addData("CABINET_DESC", tableData.getData("CABINET_DESC", i));
			}
			printData.addData("QTY", tableData.getData("QTY", i));
		}
		printData.setCount(tableData.getCount("INV_CODE"));
		printData.addData("SYSTEM", "COLUMNS", "INV_CODE");
		printData.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
		printData.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
		printData.addData("SYSTEM", "COLUMNS", "MAN_CHN_DESC");
		if(type.equals("up")){
			printData.addData("SYSTEM", "COLUMNS", "CABINET_ID");
			printData.addData("SYSTEM", "COLUMNS", "CABINET_DESC");
		}
		printData.addData("SYSTEM", "COLUMNS", "QTY");
		
		printParm.setData("TABLE", printData.getData());
		printParm.setData("TITLE", "TEXT", "物资分布");
		printParm.setData("USER", "TEXT", "制表人：" + Operator.getName());
		printParm.setData("DATE", "TEXT", "制表时间：" + 
				new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
		if(type.equals("up"))
			this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDistributionUp.jhw",	printParm);
		else if(type.equals("down"))
			this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVDistributionDown.jhw", printParm);
	}
	
	/*
     * 导出上表excel方法
     */
    public void onExeclUp(){
    	if(tableNotNull.getRowCount() <= 0){
			this.messageBox("上表没有数据！");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(tableNotNull, "物资分布统计表");
    }
    
    /*
     * 导出下表excel方法
     */
    public void onExeclDown(){
    	if(tableIsNull.getRowCount() <= 0){
			this.messageBox("下表没有数据！");
			return;
		}
    	ExportExcelUtil.getInstance().exportExcel(tableIsNull, "物资分布统计表");
    }
    
    /**
     * 得到TextField对象
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
