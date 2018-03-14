package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVNewSterilizationTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;



/**
 * 
 * <p>
 * Title:回收单选择界面
 * </p>
 * 
 * <p>
 * Description: 回收单选择界面
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
 * @author wangming 2013-7-1
 * @version 1.0
 */
public class INVChooseRecycleBillControl extends TControl{

	private TTable  table;
	/**
     * 初始化方法
     */
    public void onInit() {
    	table = (TTable) getComponent("TABLE");
        //初始化时间控件
        this.onInitDate();
    }
	
    /**
     * 查询方法
     */
	public void onQuery(){
		String startDate = getValueString("START_RECYCLE_DATE");	//起始时间
		String endDate = getValueString("END_RECYCLE_DATE");		//终止时间

		if (startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
			messageBox("请输入起止时间!");
			return;
		}

		TParm parm = new TParm();			//查询条件
		if(!getValueString("RECYCLE_NO").equals("")){
			parm.setData("RECYCLE_NO", getValueString("RECYCLE_NO"));  
		}
//		parm.setData("START_RECYCLE_DATE", startDate.substring(0, 10)+" 00:00:00");
//		parm.setData("END_RECYCLE_DATE", endDate.substring(0, 10)+" 23:59:59");
		parm.setData("START_RECYCLE_DATE", startDate.substring(0, 10));
		parm.setData("END_RECYCLE_DATE", endDate.substring(0, 10));  
		
		TParm result = INVNewSterilizationTool.getInstance().queryBackDisnfection(parm);
		
		table.setParmValue(result);
		
	}  
	
	/**
     * 传回方法
     */
	public void onReturn(){
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择回收单！");
			return;
		}
		
		String recycleNo = table.getParmValue().getValue("RECYCLE_NO", row);
		TParm result = new TParm();
		result.setData("RECYCLE_NO", recycleNo);
		setReturnValue(result);
        this.closeWindow();
	}  
	/**
     * 清空方法
     */
	public void onClear(){
		
		this.clearValue("RECYCLE_NO");
		table.removeRowAll();
	}
	
	private void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
        Timestamp tommory = StringTool.rollDate(SystemTool.getInstance().
                getDate(), +1);  
		this.setValue("START_RECYCLE_DATE", date);
		this.setValue("END_RECYCLE_DATE", tommory);  
	}
	
}
