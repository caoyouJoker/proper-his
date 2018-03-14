package com.javahis.ui.ope;

import java.sql.Timestamp;
import java.util.Date;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TButton;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;

import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TPasswordField;

/**
 * <p>
 * Title: �û�����������֤
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
			callFunction("UI|DATE|setVisible", false);
			callFunction("UI|DATE_LABLE|setVisible", false);
			this.grabFocus("ID");
		} else if (outType.equals("nurseTransfer")) {
			callFunction("UI|ID|setEnabled", true);
			callFunction("UI|DATE|setVisible", false);
			callFunction("UI|DATE_LABLE|setVisible", false);
			this.grabFocus("ID");
		} else if (outType.equals("bloodbagCheck")) {
			callFunction("UI|ID|setEnabled", true);
			callFunction("UI|DATE|setVisible", true);
			callFunction("UI|DATE_LABLE|setVisible", true);
			Timestamp date = StringTool.getTimestamp(new Date());
			((TTextFormat) this.getComponent("DATE")).setValue(date.toString().substring(0, 19).replace('-', '/'));
			this.grabFocus("ID");
		} else {
			callFunction("UI|DATE|setVisible", false);
			callFunction("UI|DATE_LABLE|setVisible", false);
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
	 * �õ��ؼ�
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
			this.messageBox("�û�������Ϊ��");
			return;
		}
		if(pass.equals("")){
			this.messageBox("���벻��Ϊ��");
			return;
		}
		
		// add by wangb ��֤�����û����� START
		if (outType.equals("nurseTransfer")) {
			TParm result = OperatorTool.getInstance().getOperatorNurseInfo(userID);
			if (result.getErrCode() < 0) {
				this.messageBox("��ѯ�û���Ϣ����");
				return;
			} else if (result.getCount() < 1) {
				this.messageBox("���û�������!");
				return;
			} else {
				if (!pass.equals(OperatorTool.getInstance().decrypt(
						result.getValue("USER_PASSWORD", 0)))) {
					this.messageBox("�������!");
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
		// add by wangb ��֤�����û����� END
		
		// �ж�����
		if (!pass.equals(OperatorTool.getInstance().decrypt(
				OperatorTool.getInstance().getOperatorPassword(userID)))) {
			this.messageBox("�������!");
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
		else if (outType.equals("bloodbagCheck")) {
			TParm parm=new TParm();
			parm.setData("USER_ID", userID);
			parm.setData("RESULT", "OK");
			parm.setData("DATE", ((TTextFormat) this.getComponent("DATE")).getValue());
			this.setReturnValue(parm);
		}
		else {
			this.setReturnValue("OK");
		}
		this.closeWindow();
	}

	public void onCANCLE() {
		TParm parm=new TParm();
		parm.setData("RESULT", "FALSE");
		this.setReturnValue(parm);
		this.closeWindow();
	}
	
	/**
	 * �û�����س��¼�
	 */
	public void onUserIdEnter() {
		grabFocus("PASSWORD");
	}

	// ��������
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("inw\\passWordCheck.x");

	}

}
