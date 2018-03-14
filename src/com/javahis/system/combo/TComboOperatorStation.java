package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;

/**
 *
 * <p>Title: 用户病区诊区下拉列表</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TComboOperatorStation extends TComboBox{
    /**
     * 用户编号
     */
    private String userID;
    /**
     * 设置用户编号
     * @param userID String
     */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    /**
     * 得到用户编号
     * @return String
     */
    public String getUserID()
    {
        return userID;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setData("USER_ID",getTagValue(getUserID()));
        setModuleParm(parm);
        super.onQuery();
    }
    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object)
    {
        if(object == null)
            return;
        object.setValue("Width","81");
        object.setValue("Height","23");
        object.setValue("Text","");
        object.setValue("showID","Y");
        object.setValue("showName","Y");
        object.setValue("showText","N");
        object.setValue("showValue","N");
        object.setValue("showPy1","Y");
        object.setValue("showPy2","Y");
        object.setValue("Editable","Y");
        object.setValue("Tip","科室");
        object.setValue("TableShowList","id,name,text");
    }
    /**
     * Module文件名
     * @return String
     */
    public String getModuleName()
    {
        return "sys\\SYSOperatorStationModule.x";
    }
    /**
     * Module方法名
     * @return String
     */
    public String getModuleMethodName()
    {
        return "getComboList";
    }
    /**
     * 对照
     * @return String
     */
    public String getParmMap()
    {
        return "id:ID;name:NAME;text:TYPE;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data)
    {
        data.add(new TAttribute("UserID","String","","Left"));
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("UserID".equalsIgnoreCase(name))
        {
            setUserID(value);
            getTObject().setValue("UserID",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
