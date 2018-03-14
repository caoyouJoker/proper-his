package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;

public class SYSIDInformationControl extends TControl {
	TParm result ;
	public TTable table;
	public SYSIDInformationControl(){
		
	}
	/**
	 * 初始化
	 */
	public void onInit(){
		TParm parm = (TParm)this.getParameter(); 
			table = (TTable)this.getComponent("TABLE");
			table.setParmValue(parm);
		}
		
	/**
	 * 回传选取数据的MR_NO，然后查询
	 */
	public void onReturnValue(){
		int row = table.getSelectedRow();
		result = table.getParmValue().getRow(row);
		this.setReturnValue(result);
		this.closeWindow();
	}
	
	
	
	
	
	
	
	
}
