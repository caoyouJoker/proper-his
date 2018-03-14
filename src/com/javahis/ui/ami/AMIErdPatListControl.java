package com.javahis.ui.ami;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * ��ʹ���ļ��ﻼ���嵥
 * @author WangQing 20170424
 *
 */
public class AMIErdPatListControl extends TControl {

	TTable table;
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		// ��table��˫���¼�
		callFunction("UI|TABLE|addEventListener",
				"TABLE->" + TTableEvent.DOUBLE_CLICKED, this, "onDoubleTableClicked");
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			TParm parm = (TParm)o;
			table.setParmValue(parm);
		}
	}
	
	/**
	 * TABLE˫���¼�
	 * @param row int
	 */
	public void onDoubleTableClicked(int row){
		TParm parm = table.getParmValue().getRow(row);
		this.setReturnValue(parm);
		this.closeWindow();
	}
	
	/**
	 * �ش�
	 */
	public void onBack(){
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("��ѡ��һ������");
			return;
		}
		TParm parm = table.getParmValue().getRow(row);
		this.setReturnValue(parm);
		this.closeWindow();
	}
}
