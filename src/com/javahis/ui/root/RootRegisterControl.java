package com.javahis.ui.root;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.javahis.system.root.RootClientListener;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.javahis.system.root.CheckThread;

/**
 *
 * <p>Title: 在线通讯注册窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.22
 * @version 1.0
 */
public class RootRegisterControl extends TControl{
    /**
     * 初始化
     */
    public void onInit()
    {
        setValue("ID",Operator.getID());
        setValue("NAME",Operator.getName());
        setValue("LNAME",Operator.getName());
    }
    /**
     * 确定
     */
    public void onOK()
    {
        String name = TypeTool.getString(getValue("LNAME"));
        if(name.length() == 0)
        {
            messageBox_("请输入昵称!");
            grabFocus("LNAME");
            return;
        }
        String password = TypeTool.getString(getValue("PASSWORD"));
        String rpassword = TypeTool.getString(getValue("RPASSWORD"));
        if(password.length() == 0)
        {
            messageBox_("请输入密码!");
            grabFocus("PASSWORD");
            return;
        }
        if(!password.equals(rpassword))
        {
            messageBox_("密码和确认密码不一致!");
            grabFocus("PASSWORD");
            return;
        }
        boolean autoLogin = ((TCheckBox)getComponent("AUTO_LOGIN")).isSelected();
        boolean onlyOne = ((TCheckBox)getComponent("ONLY_ONE")).isSelected();
        int addType = 0;
        if(((TRadioButton)getComponent("R2")).isSelected())
            addType = 1;
        if(((TRadioButton)getComponent("R3")).isSelected())
            addType = 2;
        if(!CheckThread.register(Operator.getID(),name,password,onlyOne,autoLogin,addType))
        {
            messageBox_("注册失败!");
            return;
        }
        boolean loginNow = ((TCheckBox)getComponent("LOGIN_NOW")).isSelected();
        messageBox_("注册成功!");
        closeWindow();
        if(loginNow)
        {
            RootClientListener.getInstance().setPassword(password);
            RootClientListener.getInstance().login();
        }
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        RootClientListener.getInstance().setMainWindow(null);
        return true;
    }
}
