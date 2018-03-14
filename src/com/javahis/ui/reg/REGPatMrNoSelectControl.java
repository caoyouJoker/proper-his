package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
/*
 * 挂号界面一个身份证多个病案号选择病案号 
*/

public class REGPatMrNoSelectControl extends TControl{
	 public TTable table;
	 public TParm result;
	public void onInit() {
		TParm parm = (TParm) this.getParameter();
		 table = (TTable) this.getComponent("TABLE");
		table.setParmValue(parm);
	}
	/*
	 * 传回
	*/
	public void onReturnValue(){
		
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox("请选择要传回的数据");
			return;
		}
		result=table.getParmValue().getRow(row);
		this.setReturnValue(result);
		this.closeWindow();
	}
}
