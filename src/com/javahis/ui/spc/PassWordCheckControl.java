package com.javahis.ui.spc;

import jdo.sys.OperatorTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TPasswordField;
import com.dongyang.ui.TTextField;
import com.javahis.util.JavaHisDebug;

public class PassWordCheckControl extends TControl {

	TTextField id;
	TPasswordField passwd;
	TButton ok, cancle;
	String outType;

	public PassWordCheckControl() {
	}

	public void onInit() {
		super.onInit();
		myInitCtl();
		initID();
	}

	public void initID() {
		/**
		outType = (String) this.getParameter();
		if (outType.equals("singleExe")) {
			callFunction("UI|ID|setEnabled", true);
			this.grabFocus("ID");
		} else {
			id.setValue(Operator.getID());
			passwd.grabFocus();
		}*/
		id.grabFocus();
	}

	/**
	 * 得到控件
	 */
	public void myInitCtl() {
		id = (TTextField) this.getComponent("ID");
		//passwd = (TPasswordField) this.getComponent("PASSWORD");
		ok = (TButton) this.getComponent("OK");
		cancle = (TButton) this.getComponent("CANCLE");
	}

	public void onOK() {
		//String pass = passwd.getValue();
		String userID = id.getValue();
		if(userID.equals("")){
			this.messageBox("工卡不能为空");
			return;
		}
		/**
		if(pass.equals("")){
			this.messageBox("密码不能为空");
			return;
		}*/
		// 判断密码
		/**
		if (!pass.equals(OperatorTool.getInstance().decrypt(
				OperatorTool.getInstance().getOperatorPassword(userID)))) {
			this.messageBox("密码错误!");
			passwd.setValue("");
			return;
		}*/
		
		//判断用户是否存在
		if(!OperatorTool.getInstance().existsOperator(userID)){
			this.messageBox("工号错误!");
			id.setValue("");
			return ;
		}
		TParm parm = new TParm();
		parm.setData("USER_ID", userID);
		parm.setData("OK", "OK");
		this.setReturnValue(parm);
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
		JavaHisDebug.runFrame("spc\\passWordCheck.x");

	}

}
