package com.javahis.ui.inv;

import java.sql.Timestamp;

import jdo.inv.INVNewSterilizationTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;



/**
 * 
 * <p>
 * Title:打包单选择界面
 * </p>
 * 
 * <p>
 * Description: 打包单选择界面
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
 * @author wangming 2013-8-9
 * @version 1.0
 */
public class INVChooseRepackBillControl extends TControl{

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
		String startDate = getValueString("START_REPACK_DATE");	//起始时间
		String endDate = getValueString("END_REPACK_DATE");		//终止时间

		if (startDate == null || startDate.length() == 0 || endDate == null || endDate.length() == 0) {
			messageBox("请输入起止时间!");
			return;
		}

		TParm parm = new TParm();			//查询条件
		if(!getValueString("REPACK_NO").equals("")){
			parm.setData("REPACK_NO", getValueString("REPACK_NO"));
		}
		parm.setData("START_REPACK_DATE", startDate.substring(0, 10)+" 00:00:00");
		parm.setData("END_REPACK_DATE", endDate.substring(0, 10)+" 23:59:59");
		
		TParm result = INVNewSterilizationTool.getInstance().queryRepack(parm);
		
		table.setParmValue(result);
		
	}
	
	/**
     * 传回方法
     */
	public void onReturn(){
		int row = table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择打包单！");
			return;
		}
		
		String recycleNo = table.getParmValue().getValue("REPACK_NO", row);
		TParm result = new TParm();
		result.setData("REPACK_NO", recycleNo);
		setReturnValue(result);
        this.closeWindow();
	}
	/**
     * 清空方法
     */
	public void onClear(){
		
		this.clearValue("REPACK_NO");
		table.removeRowAll();
	}
	
	private void onInitDate() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_REPACK_DATE", date);
		this.setValue("END_REPACK_DATE", date);
	}
	
}
