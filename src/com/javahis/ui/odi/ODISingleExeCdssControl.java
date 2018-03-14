package com.javahis.ui.odi;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;


public class ODISingleExeCdssControl extends TControl {
	private TTable table;
	private TParm parm;
	

	/**
	 * ³õÊ¼»¯
	 */
	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");	
		parm = (TParm) getParameter();		
//		System.out.println("parm==============="+parm);
		if (null == parm) {
			return;
		}				
		table.setParmValue(parm);
	}
	/**
	 * ÍË³ö
	 */
	public void OnCancel() {
		closeWindow();
	}
	
}
