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
	 * ��ʼ��
	 */
	public void onInit(){
		TParm parm = (TParm)this.getParameter(); 
			table = (TTable)this.getComponent("TABLE");
			table.setParmValue(parm);
		}
		
	/**
	 * �ش�ѡȡ���ݵ�MR_NO��Ȼ���ѯ
	 */
	public void onReturnValue(){
		int row = table.getSelectedRow();
		result = table.getParmValue().getRow(row);
		this.setReturnValue(result);
		this.closeWindow();
	}
	
	
	
	
	
	
	
	
}
