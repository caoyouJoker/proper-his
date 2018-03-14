package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;



/**
 * 
 * <p>
 * Title:打印数量输入界面
 * </p>
 * 
 * <p>
 * Description: 打印数量输入界面
 * </p>
 * @author wangming 2013-8-30
 * @version 1.0
 */
public class INVPrintCountControl extends TControl{

	/**
     * 初始化方法
     */
    public void onInit() {
    	
    }
	
	
	/**
     * 传回方法
     */
	public void onReturn(){
		TTextField tf = (TTextField) getComponent("PRINTCOUNT");
		
		boolean tag = true;
		try {
			Integer.parseInt(tf.getText());
		} catch (NumberFormatException e) {
			tag = false;
		}
		if(!tag){
			this.messageBox("请输入数字！");
			return;
		}
		
		TParm result = new TParm();
		result.setData("PRINTCOUNT", tf.getText());
		setReturnValue(result);
        this.closeWindow();
	}
	
	
}
