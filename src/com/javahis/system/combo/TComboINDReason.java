package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
/**
 *
 * <p>Title: 药库原因下拉列表</p>
 *
 * <p>Description: 药库原因下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboINDReason extends TComboBox{
    /**
     * 原因类别
     */
    private String reasonType;
    /**
     * 得到原因类别
     * @return String
     */
    public String getReasonType() {
        return reasonType;
    }
    /**
     * 设置原因类别
     * @param reasonType String
     */
    public void setReasonType(String reasonType) {
        this.reasonType = reasonType;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("REASON_TYPE",getTagValue(getReasonType()));
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
        object.setValue("Tip","药库原因");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "ind\\INDReasonModule.x";
    }
    public String getModuleMethodName()
    {
        return "initINDReasonCode";
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
        data.add(new TAttribute("ReasonType","String","","Left"));
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("ReasonType".equalsIgnoreCase(name))
        {
            setReasonType(value);
            getTObject().setValue("ReasonType",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
