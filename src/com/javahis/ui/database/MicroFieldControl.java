package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TButton;

/**
 *
 * <p>Title: 宏文本属性对话框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.9.1
 * @version 1.0
 */
public class MicroFieldControl extends TControl{
    /**
     * 固定文本对象
     */
    private EMicroField microField;
    /**
     * 初始化
     */
    public void onInit()
    {
        microField = (EMicroField) getParameter();
        if (microField == null)
            return;
        setValue("NAME", microField.getName());
        setValue("TEXT", microField.getText());
        setValue("MACRONAME",microField.getMicroName());
        setValue("GROUP_NAME",microField.getGroupName());

        setValue("ALLOW_NULL", microField.isAllowNull() ? "Y" : "N");
        setValue("CHK_ISCDA",microField.isIsDataElements()?"Y" : "N");
        setValue("CHK_LOCKED",microField.isLocked()?"Y" : "N");
        /**if (microField.isIsDataElements()) {

            TTextField tf_name = (TTextField) getComponent("NAME");
            TTextField tf_text = (TTextField) getComponent("TEXT");
            TCheckBox ck_text = (TCheckBox) getComponent("ALLOW_NULL");
            TTextField tf_groupname = (TTextField) getComponent("GROUP_NAME");
            TTextField tf_macroname = (TTextField) getComponent("MACRONAME");

            tf_name.setEditable(false);
            tf_text.setEditable(false);
            ck_text.setEnabled(false);
            tf_groupname.setEnabled(false);
            tf_macroname.setEnabled(false);

        }**/

    }
    /**
     * 确定
     */
    public void onOK()
    {
        if(getText("TEXT").length() == 0)
        {
            //this.messageBox_("文本不能为空!");
            //return;
        }
        if(getText("MACRONAME").length()==0){
            //this.messageBox_("宏名不能为空!");
            //return;
        }

        microField.setName(getText("NAME"));
        microField.setText(getText("TEXT"));
        microField.setMicroName(getText("MACRONAME"));
        microField.setGroupName(getText("GROUP_NAME"));
        if(getValue("CHK_ISCDA").equals("Y")){
            microField.setDataElements(true);
        }else{
            microField.setDataElements(false);
        }
        microField.setLocked(getValueBoolean("CHK_LOCKED"));

        microField.setModify(true);
        setReturnValue("OK");
        closeWindow();
    }
}
