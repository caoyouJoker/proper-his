package com.javahis.ui.ins;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
/**
 * 
 * <p>
 * Title:特殊情况
 * </p>
 * 
 * <p>
 * Description:特殊情况
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) bluecore
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author pangb 2011-12-02
 * @version 2.0
 */
public class InsSaveSpcMemoControl extends TControl{
	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		// 得到前台传来的数据并显示在界面上
		TParm recptype = this.getInputParm();
//		System.out.println("SPC_MEMO:"+recptype);
//		System.out.println("SPC_MEMO:"+recptype.getValue("SPC_MEMO",0));
		this.setValue("SPECIAL_CASE", recptype.getValue("SPC_MEMO",0));
	}
	public void onOK(){
		if(!this.emptyTextCheck("SPECIAL_CASE")){
			return;
		}
		TParm parm=new TParm();
		parm.setData("SPECIAL_CASE",this.getValue("SPECIAL_CASE"));
		this.setReturnValue(parm);
	    this.callFunction("UI|onClose");
	}
	
}
