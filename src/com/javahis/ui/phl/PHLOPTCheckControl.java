package com.javahis.ui.phl;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import jdo.sys.OperatorTool;

/**
 * <p>Title: 使用者确认</p>
 *
 * <p>Description: 使用者确认</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.03.30
 * @version 1.0
 */
public class PHLOPTCheckControl extends TControl {
    public PHLOPTCheckControl() {
    }

    /**
     * 确认按钮
     */
    public void onBtnOK() {
        String user_id = getValueString("USER_ID");
        String password = getValueString("USER_PASSWORD");
        if ("".equals(user_id)) {
            this.messageBox("用户名不能为空");
            return;
        }
        else if ("".equals(password)) {
            this.messageBox("密码不能为空");
            return;
        }
        else {
            if (!password.equals(OperatorTool.getInstance().decrypt(OperatorTool.
                getInstance().getOperatorPassword(user_id)))) {
                this.messageBox("密码错误，请重新输入！！！");
                return;
            }
        }
        TParm parm = new TParm();
        parm.setData("USER_ID", user_id);
        setReturnValue(parm);
        this.closeWindow();
    }
    /**
     * 取消按钮
     */
    public void onBtnCancel() {
        this.closeWindow();
    }
}
