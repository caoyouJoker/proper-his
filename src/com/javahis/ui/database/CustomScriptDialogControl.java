package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextArea;

/**
 *
 * <p>Title: 宏自定义脚本对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2009.4.16
 * @version 1.0
 */
public class CustomScriptDialogControl extends TControl{
    private TTextArea functionScript;
    private TTextArea importScript;
    /**
     * 初始化
     */
    public void onInit()
    {
        functionScript = (TTextArea)getComponent("FUNCTION");
        importScript = (TTextArea)getComponent("IMPORT");
        Object obj[] = (Object[])getParameter();
        if(obj == null)
            return;
        importScript.setText((String)obj[0]);
        functionScript.setText((String)obj[1]);
    }
    /**
     * 确定
     */
    public void onOK()
    {
        setReturnValue(new Object[]{importScript.getValue(),
                       functionScript.getValue()});
        closeWindow();
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
}
