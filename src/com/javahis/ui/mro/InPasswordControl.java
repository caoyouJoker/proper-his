package com.javahis.ui.mro;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.ui.TPasswordField;

/**
 * <p>
 * Title: 用户密码验证
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
 * @author chenxi 2013-04-09
 * @version 4.0
 */
public class InPasswordControl extends TControl {

	TTextField id;
	TPasswordField passwd;
	TButton ok, cancle;

	public InPasswordControl() {
	}

	public void onInit() {
		super.onInit();
		myInitCtl();
		initID();
	}

	public void initID() {
		
		id.setValue(Operator.getName()) ;
		this.setValue("DATE", SystemTool.getInstance().getDate()) ;
	    passwd.grabFocus();   
		
	}

	/**
	 * 得到控件
	 */
	public void myInitCtl() {
		id = (TTextField) this.getComponent("NAME");
		passwd = (TPasswordField) this.getComponent("PASSWORD");
		ok = (TButton) this.getComponent("OK");
		cancle = (TButton) this.getComponent("CANCLE");
	}

	public void onOK() {
		String pass = passwd.getValue();
		String userID = id.getValue();
		if(userID.equals("")){
			this.messageBox("用户名不能为空");
			return;
		}
		if(pass.equals("")){
			this.messageBox("密码不能为空");
			return;
		}
		this.closeWindow();
	}

	public void onCANCLE() {
		this.closeWindow();
	}

}