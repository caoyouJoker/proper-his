package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TPasswordField;

/**
 * <p>
 * Title: 超常执行原因
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS
 * </p>
 * 
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class LateReasonControl extends TControl {

	TTextField txtReason;
	TButton ok, cancle;
	TComboBox comboReason;

	public LateReasonControl() {
	}

	public void onInit() {
		super.onInit();
		myInitCtl();
		initID();
	}

	public void initID() {
	}

	/**
	 * 得到控件
	 */
	public void myInitCtl() {
		txtReason = (TTextField) this.getComponent("TXT_REASON");
		ok = (TButton) this.getComponent("BTNOK");
		cancle = (TButton) this.getComponent("BTNCANCEL");
		comboReason = (TComboBox)this.getComponent("COMBO_REASON");
	}

	public void onOK() {
		String str = "";
		String sel = comboReason.getValue();
		if("".equals(sel)){
			this.messageBox("请选择延迟原因");
			return ;
		}
		
		if("99".equals(sel)){
			str = txtReason.getText();
		}else{
			str = comboReason.getSelectedName();
		}
		
		if("".equals(str)){
			this.messageBox("请选择延迟原因");
			return ;
		}
		
		System.out.println("@@@@str:"+str);
		TParm p = new TParm();
		p.setData("p", str);
		this.setReturnValue(p);
		this.closeWindow();
	}

	public void onCANCLE() {
		this.closeWindow();
	}

	// 测试用例
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("inw\\lateReason.x");

	}
	
	public void onComboChange(){
		String s = comboReason.getValue();
		if("99".equals(s)){
			txtReason.setEnabled(true);
		}else{
			txtReason.setText("");
			txtReason.setEnabled(false);
		}
	}

}
