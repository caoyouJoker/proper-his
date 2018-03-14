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
 * <p>��������������֤</p>
 * 
 * @author wangqing 20170923
 *
 */
public class REGSavePassWordCheckControl extends TControl {
	
	TTextField id;
	TPasswordField passwd;
	TButton ok, cancle;

	/**
	 * ��ʼ��
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
	 * ȷ��
	 */
	public void onOK(){
		String pass = passwd.getValue();
		String userID = id.getValue();
		if(userID.equals("")){
			this.messageBox("�û�������Ϊ��");
			return;
		}
		if(pass.equals("")){
			this.messageBox("���벻��Ϊ��");
			return;
		}
		String sql = " SELECT USER_ID, USER_PASSWORD, ROLE_ID FROM SYS_OPERATOR WHERE USER_ID='"+userID+"' AND ROLE_ID='NWE'";// ���ﻤʿ
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�û���Ϣ����");
			return;
		}
		if (result.getCount("USER_ID") <= 0) {
			this.messageBox("�Ǽ��ﻤʿ!");
			return;
		}
		if (!pass.equals(OperatorTool.getInstance().decrypt(
				result.getValue("USER_PASSWORD", 0)))) {
			this.messageBox("�������!");
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
	
	/**ȡ��
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
	 * ID�س��¼�
	 */
	public void onUserIdEnter(){
		grabFocus("PASSWORD");
	}
}
