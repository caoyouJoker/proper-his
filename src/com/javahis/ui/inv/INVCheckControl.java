package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;

/**
 * <p>
 * Title: 物资盘点Control 
 * </p>
 *
 * <p>
 * Description: 物资盘点Control
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

public class INVCheckControl
    extends TControl {
	
    public INVCheckControl() {
    }
    
    /*
     * 界面初始化
     * @see com.dongyang.control.TControl#onInit()
     */
    public void onInit(){
    	Timestamp date = new Timestamp(new Date().getTime());
    	this.messageBox(date.toString().replace("-", "/"));
		this.setValue("START_TIME", 
				date.toString().substring(0, 10).replace("-", "/") + " 23:59:59");
    }
    
    /*
     * 查询方法
     */
	private void query() {
		TParm parm = new TParm();
		TParm result = null;
		if(this.getValueString("START_TIME") == null || this.getValueString("START_TIME").length() <= 0){
			this.messageBox("请填写冻结时间！");
			return;
		}
		//开始时间
		String startTime = this.getValueString("START_TIME").substring(0, 
				this.getValueString("START_TIME").lastIndexOf(".")).replace("-", "").
				replace(":", "").replace(" ", "");
		parm.setData("START_TIME", startTime);
		if(this.getRadioButtonCmp("CHECK_RADIO_BUTTON").isSelected()) {
			
		}else if(this.getRadioButtonCmp("DETAIL_RADIO_BUTTON").isSelected()){
			
		}
//		result = InvVerifyInReportTool.getInstance().onQueryVerifyInReport(parm);
//		if(result.getCount() <= 0){
//			this.messageBox("无查询结果");
//			return;
//		}
		openPrintWindow("%ROOT%\\config\\prt\\INV\\INVCheck.jhw", result);
	}
	
	/*
     * 清空方法
     */
    public void onClear(){
    	Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_TIME", new Timestamp(date.getTime()).toString().replace("-", "/"));
		this.getRadioButtonCmp("CHECK_RADIO_BUTTON").setSelected(true);
    }
    
    /*
     * 打印方法
     */
    public void onPrint(){
    	query();
    }
    
    /*
     * 通过组件名得到复选框对象
     */
    private TRadioButton getRadioButtonCmp(String tagName){
    	return (TRadioButton) this.getComponent(tagName);
    }
}
