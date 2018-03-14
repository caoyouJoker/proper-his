package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
/*
 * �ҺŽ���һ�����֤���������ѡ�񲡰��� 
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
	 * ����
	*/
	public void onReturnValue(){
		
		int row=table.getSelectedRow();
		if(row<0){
			this.messageBox("��ѡ��Ҫ���ص�����");
			return;
		}
		result=table.getParmValue().getRow(row);
		this.setReturnValue(result);
		this.closeWindow();
	}
}
