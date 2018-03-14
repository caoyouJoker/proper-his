package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ESign;
import com.dongyang.util.StringTool;

/**
 * 
 * ��֤ǩ���ؼ�
 * @author lix
 *
 */
public class SignEditControl extends TControl{
	

    private ESign signField;
    /**
     * ��ʼ��
     */
    public void onInit()
    {
    	signField = (ESign) getParameter();
        if (signField == null)
            return;
        setValue("NAME", signField.getName());
        setValue("TEXT", signField.getText());
        setValue("GROUP_NAME",signField.getGroupName());
        setValue("SIGN_CODE",signField.getSignCode());
        setValue("TIME_STMP",signField.getTimestmp());
        
        String strSysDate = StringTool
		.getString(new java.sql.Timestamp(Long.parseLong(signField.getTimestmp())), "yyyy/MM/dd HH:mm:ss");
        System.out.println("-----strSysDate----"+strSysDate);
        setValue("SIGN_TIME",strSysDate);
        
        setValue("ALLOW_NULL", signField.isAllowNull() ? "Y" : "N");
        setValue("CHK_ISCDA",signField.isIsDataElements()?"Y" : "N");
        setValue("CHK_LOCKED",signField.isLocked()?"Y" : "N");

    }
    
    public void onOK()
    {
        closeWindow();
    }

}
