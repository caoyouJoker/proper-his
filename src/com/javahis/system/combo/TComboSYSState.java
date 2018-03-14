package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: 省下拉列表</p>
 *
 * <p>Description: 省下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.01.15
 * @version 1.0
 */
public class TComboSYSState extends TComboBox{
    /**
     * 邮编
     */
    private String postCode;
    /**
     * 设置邮编
     * @param postCode String
     */
    public void setPostCode(String postCode)
    {
        this.postCode = postCode;
    }
    /**
     * 得到邮编
     * @return String
     */
    public String getPostCode()
    {
        return postCode;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("POST_CODE",getTagValue(getPostCode()));
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
        object.setValue("Text","TButton");
        object.setValue("showID","Y");
        object.setValue("showName","Y");
        object.setValue("showText","N");
        object.setValue("showValue","N");
        object.setValue("showPy1","Y");
        object.setValue("showPy2","Y");
        object.setValue("Editable","Y");
        object.setValue("Tip","省下拉列表");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSPostCodeModule.x";
    }
    public String getModuleMethodName()
    {
        return "selectprovince";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
        data.add(new TAttribute("PostCode","String","","Left"));
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("PostCode".equalsIgnoreCase(name))
        {
            setPostCode(value);
            getTObject().setValue("PostCode",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
