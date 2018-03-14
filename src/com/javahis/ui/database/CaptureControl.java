package com.javahis.ui.database;

import com.dongyang.control.TControl;
import com.dongyang.tui.text.ECapture;
import com.dongyang.util.TypeTool;

/**
 *
 * <p>Title: 抓取对象属性对话框控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.11.24
 * @version 1.0
 */
public class CaptureControl extends TControl{
    /**
     * 抓取对象
     */
    private ECapture capture;
    /**
     * 初始化
     */
    public void onInit()
    {
        capture = (ECapture) getParameter();
        if (capture == null)
            return;
        setValue("GROUP_NAME", capture.getGroupName());
        setValue("NAME", capture.getName());
        setValue("MACRONAME",capture.getMicroName());
        setValue("REF_FILE_NAME",capture.getRefFileName());
        

        switch(capture.getCaptureType())
        {
            case 0:
                setValue("TSTART",true);
                break;
            case 1:
                setValue("TSTOP",true);
                break;
        }

        //是元素则不能修改
        setValue("ALLOW_NULL",capture.isAllowNull()?"Y" : "N");
        setValue("CHK_ISCDA",capture.isIsDataElements()?"Y" : "N");
        //this.messageBox("CHK_LOCKED"+capture.isLocked());
        setValue("CHK_LOCKED",capture.isLocked()?"Y" : "N");
        
        setValue("REF_FILE_NAME",capture.getRefFileName());
        
       
        /**if(capture.isIsDataElements()){
            TTextField tf_name = (TTextField)getComponent("NAME");
            TCheckBox ck_text = (TCheckBox)getComponent("ALLOW_NULL");
            tf_name.setEditable(false);
            ck_text.setEnabled(false);
        }**/


    }
    /**
     * 确定
     */
    public void onOK()
    {
        capture.setGroupName(getText("GROUP_NAME"));
        capture.setName(getText("NAME"));
        capture.setMicroName(getText("MACRONAME"));
        if(TypeTool.getBoolean(getValue("TSTART")))
            capture.setCaptureType(0);
        else if(TypeTool.getBoolean(getValue("TSTOP")))
            capture.setCaptureType(1);

        if(getValue("CHK_ISCDA").equals("Y")){
            capture.setDataElements(true);
        }else{
             capture.setDataElements(false);
        }

        if(getValue("CHK_LOCKED").equals("Y")){
            capture.setLocked(true);
        }else{
            capture.setLocked(false);
        }
        
        capture.setRefFileName(getText("REF_FILE_NAME"));
        //this.messageBox("test come in");
        capture.setModify(true);
        setReturnValue("OK");
        closeWindow();
    }
}
