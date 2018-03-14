package com.javahis.ui.database;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.EFixed;

/**
 *
 * <p>Title: 固定文本属性对话框</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.8.27
 * @author whao 2013~
 * @version 1.0
 */
public class MarkEditControl extends TControl {

    /**
     * 固定文本对象
     */
    private EFixed fixed;
    /**
     * 初始化
     */
    public void onInit() {
        fixed = (EFixed) getParameter();

        if (fixed == null) {
            return;
        }
        setValue("GROUP_NAME", fixed.getGroupName());
        setValue("NAME", fixed.getName());
        setValue("TEXT", fixed.getText());
        setValue("CActionType", fixed.getActionType());
        setValue("TRY_FILENAME", fixed.getTryFileName());
        setValue("TRY_NAME", fixed.getTryName());

        //是元素则不能修改
        setValue("ALLOW_NULL", fixed.isAllowNull() ? "Y" : "N");
        //setDataElements
        setValue("CHK_ISCDA", fixed.isIsDataElements() ? "Y" : "N");
        setValue("CHK_ISELE", fixed.isIsElementContent() ? "Y" : "N");
        setValue("CHK_LOCKED",fixed.isLocked()?"Y" : "N");
        //System.out.println("===fixed isInsert=="+fixed.isInsert());
        setValue("CHK_INS",fixed.isInsert()?"Y" : "N");

        //
        setValue("scriptUp", fixed.getScriptUp());
        setValue("scriptDown", fixed.getScriptDown());
    }

    /**
     * 设置下标
     */
    public void onSub() {
          setValue("CHK_SUP", "N");
       /** if (getValue("CHK_SUB").equals("Y")) {
            setValue("CHK_SUB", "N");
        }
        else {
            setValue("CHK_SUB", "Y");
            setValue("CHK_SUP", "N");
        }**/

    }

    /**
     * 设置上标
     */
    public void onSup() {
        setValue("CHK_SUB", "N");
       /** if (getValue("CHK_SUP").equals("Y")) {
            setValue("CHK_SUP", "N");
        }
        else {

            setValue("CHK_SUB", "N");
            setValue("CHK_SUP", "Y");
        }**/
    }


    /**
     * 确定
     */
    public void onOK() {
    	//
        if (getText("TEXT").length() == 0) {
            this.messageBox_("文字不能为空!");
            return;
        }
        //
        //
        if(fixed!=null&&fixed.isLocked()){
        	this.messageBox_("已锁定的控件不能修改!");
            return;
        }
        //
        fixed.setGroupName(getText("GROUP_NAME"));
        Timestamp nowDate = SystemTool.getInstance().getDate();
        //getText("NAME")
        fixed.setName("Mark_"+nowDate);
        fixed.setText(getText("TEXT"));
        fixed.setModify(true);
        //
        //fixed.setActionType(getText("CActionType"));
        //fixed.setTryFileName(getText("TRY_FILENAME"));
        //fixed.setTryName(getText("TRY_NAME"));
        //this.messageBox(""+getValue("CHK_ISCDA"));
        /*if (getValue("CHK_ISCDA").equals("Y")) {
            fixed.setDataElements(true);
        }
        else {*/
            fixed.setDataElements(false);
       //}
       /* if (getValue("CHK_ISELE").equals("Y")) {
            fixed.setElementContent(true);
        }
        else {*/
            fixed.setElementContent(false);
        //}
        fixed.setLocked(false);
        fixed.setInsert(false);
        //
        fixed.setScriptUp(getText("scriptUp"));
        fixed.setScriptDown(getText("scriptDown"));

        setReturnValue("OK");
        closeWindow();
    }

    public void onTryFileName() {

    }

    public void onTryName() {

    }
}
