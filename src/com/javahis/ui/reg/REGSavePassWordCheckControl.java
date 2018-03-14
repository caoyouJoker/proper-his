package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TPasswordField;
import com.dongyang.ui.TTextField;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;

/**
 * 
 * <p>急诊抢救密码验证</p>
 * 
 * @author wangqing 20170923
 *
 */
public class REGSavePassWordCheckControl extends TControl {
	
	TTextField id;
	TPasswordField passwd;
	TButton ok, cancle;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		id = (TTextField) this.getComponent("ID");
		passwd = (TPasswordField) this.getComponent("PASSWORD");
		ok = (TButton) this.getComponent("OK");
		cancle = (TButton) this.getComponent("CANCLE");
		id.setValue(Operator.getID());
//		id.setValue("000481");
		passwd.grabFocus();
	}
	
	/**
	 * 确定
	 */
	public void onOK(){
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
		String sql = " SELECT USER_ID, USER_PASSWORD, ROLE_ID FROM SYS_OPERATOR WHERE USER_ID='"+userID+"' AND ROLE_ID='NWE'";// 急诊护士
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("查询用户信息错误");
			return;
		}
		if (result.getCount("USER_ID") <= 0) {
			this.messageBox("非急诊护士!");
			return;
		}
		if (!pass.equals(OperatorTool.getInstance().decrypt(
				result.getValue("USER_PASSWORD", 0)))) {
			this.messageBox("密码错误!");
			passwd.setValue("");
			return;
		}
		TParm parm=new TParm();
		parm.setData("USER_ID", userID);
		parm.setData("RESULT", "OK");
		this.setReturnValue(parm);
		System.out.println("======parm="+parm);
		this.closeWindow();
	}
	
	/**取消
	 * 
	 */
	public void onCANCLE(){
		TParm parm=new TParm();
		parm.setData("RESULT", "CANCLE");
		this.setReturnValue(parm);
		System.out.println("======parm="+parm);
		this.closeWindow();
	}
	
	/**
	 * ID回车事件
	 */
	public void onUserIdEnter(){
		grabFocus("PASSWORD");
	}
}
