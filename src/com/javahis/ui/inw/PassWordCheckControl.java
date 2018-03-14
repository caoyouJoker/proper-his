package com.javahis.ui.inw;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;

import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TPasswordField;

/**
 * <p>
 * Title: 用户保存密码验证
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
public class PassWordCheckControl extends TControl {

	TTextField id;
	TPasswordField passwd;
	TButton ok, cancle;
	String outType;
	TParm Parm;
	

	public PassWordCheckControl() {
	}
	public void onInit() {
		super.onInit();
		myInitCtl();
		if (this.getParameter() instanceof String)		
	     initID();		
		else
		initemr();	
	}
	public void initID() {
		outType = (String) this.getParameter();
		if (outType.equals("singleExe")) {
			callFunction("UI|ID|setEnabled", true);
			this.grabFocus("ID");
		} else if (outType.equals("nurseTransfer")) {
			callFunction("UI|ID|setEnabled", true);
			this.grabFocus("ID");
		} else {
			id.setValue(Operator.getID());
			passwd.grabFocus();
		}
	}

	public void initemr() {
		Parm =(TParm)this.getParameter();
		outType =Parm.getValue("TYPE") ;
		if (outType.equals("transfer")) {
			callFunction("UI|ID|setEnabled", true);
			this.grabFocus("ID");
		} else {
			id.setValue(Operator.getID());
			passwd.grabFocus();
		}	
	}
	/**
	 * 得到控件
	 */
	public void myInitCtl() {
		id = (TTextField) this.getComponent("ID");
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
		
		// add by wangb 验证护工用户密码 START
		if (outType.equals("nurseTransfer")) {
			TParm result = OperatorTool.getInstance().getOperatorNurseInfo(userID);
			if (result.getErrCode() < 0) {
				this.messageBox("查询用户信息错误");
				return;
			} else if (result.getCount() < 1) {
				this.messageBox("该用户不存在!");
				return;
			} else {
				if (!pass.equals(OperatorTool.getInstance().decrypt(
						result.getValue("USER_PASSWORD", 0)))) {
					this.messageBox("密码错误!");
					passwd.setValue("");
					return;
				} else {
					TParm parm=new TParm();
					parm.setData("USER_ID", userID);
					parm.setData("RESULT", "OK");
					this.setReturnValue(parm);
					this.closeWindow();
					return;
				}
			}
		}
		// add by wangb 验证护工用户密码 END
		
		// 判断密码
		if (!pass.equals(OperatorTool.getInstance().decrypt(
				OperatorTool.getInstance().getOperatorPassword(userID)))) {
			this.messageBox("密码错误!");
			passwd.setValue("");
			return;
		}
		if (outType.equals("singleExe")) {
			TParm parm=new TParm();
			parm.setData("USER_ID", userID);
			parm.setData("RESULT", "OK");
			this.setReturnValue(parm);
		}
		else if (outType.equals("transfer")) {
			TParm parm=new TParm();
			parm.setData("USER_ID", userID);
			parm.setData("RESULT", "OK");
			this.setReturnValue(parm);
			Parm.runListener("onReturnfromuser",parm);
			Parm.runListener("onReturntouser",parm);
		}
		else {
			this.setReturnValue("OK");
		}
		this.closeWindow();
	}

	public void onCANCLE() {
		this.closeWindow();
	}
	
	/**
	 * 用户代码回车事件
	 */
	public void onUserIdEnter() {
		grabFocus("PASSWORD");
	}

	// 测试用例
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("inw\\passWordCheck.x");

	}

}
