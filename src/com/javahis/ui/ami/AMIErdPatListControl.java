package com.javahis.ui.ami;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * 胸痛中心急诊患者清单
 * @author WangQing 20170424
 *
 */
public class AMIErdPatListControl extends TControl {

	TTable table;
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		// 绑定table的双击事件
		callFunction("UI|TABLE|addEventListener",
				"TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onDoubleTableClicked");
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			TParm parm = (TParm)o;
			table.setParmValue(parm);
		}
	}
	
	/**
	 * TABLE双击事件
	 * @param row int
	 */
	public void onDoubleTableClicked(int row){
		TParm parm = table.getParmValue().getRow(row);
		this.setReturnValue(parm);
		this.closeWindow();
	}
	
	/**
	 * 回传
	 */
	public void onBack(){
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择一行数据");
			return;
		}
		TParm parm = table.getParmValue().getRow(row);
		this.setReturnValue(parm);
		this.closeWindow();
	}
}
