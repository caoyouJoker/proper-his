package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;

/**
 *
 * <p>Title: 宏编辑对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author 2009.4.12
 * @version 1.0
 */
public class MacroroutineEditDialogControl extends TControl{
    private TTextField name;
    private TTextArea syntax;
    private TRadioButton expressions;
    private TRadioButton method;
    private TTextField groupName;
    private TTextField cdaName;
    /**
     * 初始化
     */
    public void onInit()
    {
        name = (TTextField)getComponent("NAME");
        syntax = (TTextArea)getComponent("SYNTAX");
        expressions = (TRadioButton)getComponent("EXPRESSIONS");
        method = (TRadioButton)getComponent("METHOD");
        Object obj[] = (Object[])getParameter();
        if(obj == null)
            return;
        name.setText((String)obj[0]);
        syntax.setText((String)obj[1]);
        switch((Integer)obj[2])
        {
            case 1:
                expressions.setSelected(true);
                break;
            case 2:
                method.setSelected(true);
                break;
        }
        setValue("LOCK_SIZE",obj[3]);
        setValue("D_WIDTH",(Integer)obj[4]);
        setValue("D_HEIGHT",(Integer)obj[5]);
        //
        try{
            setValue("GROUP_NAME", obj[6]);
        }catch(Exception e){
              //System.out.println("=====无CDA组名======");
              setValue("GROUP_NAME", "");
        }
        try{

            setValue("CDA_NAME",obj[7]);
        }catch(Exception e){
            //System.out.println("=====无CDA名称======");
            setValue("CDA_NAME","");
        }
    }
    /**
     * 得到类型
     * @return int
     */
    public int getType()
    {
        if(expressions.isSelected())
            return 1;
        if(method.isSelected())
            return 2;
        return 0;
    }
    /**
     * 确定
     */
    public void onOK()
    {
        if(name.getText().length() == 0)
        {
            messageBox_("请输入名称!");
            name.grabFocus();
            return;
        }
        setReturnValue(new Object[]{name.getValue(),
                       syntax.getValue(),getType(),getValue("LOCK_SIZE"),getValue("D_WIDTH"),getValue("D_HEIGHT"),getValue("GROUP_NAME"),getValue("CDA_NAME")});
        closeWindow();
    }
    /**
     * 取消
     */
    public void onCancel()
    {
        closeWindow();
    }
    public static void main(String args[])
    {
        Object value[] = (Object[])com.javahis.util.JavaHisDebug.runDialog("database\\MacroroutineEditDialog.x",
            new Object[]{"name","Syntax",2});
        if(value != null)
            System.out.println(value[0] + " " + value[1] + " " + value[2]);
    }
}
