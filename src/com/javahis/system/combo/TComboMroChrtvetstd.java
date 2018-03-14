package com.javahis.system.combo;

import com.dongyang.ui.*;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: </p>
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
public class TComboMroChrtvetstd
    extends TComboBox {
    /**
     * 类型编码
     */
    private String typeCode;

    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "TButton");
        object.setValue("showID", "Y");
        object.setValue("showName", "Y");
        object.setValue("showText", "N");
        object.setValue("showValue", "N");
        object.setValue("showPy1", "Y");
        object.setValue("showPy2", "Y");
        object.setValue("Editable", "Y");
        object.setValue("Tip", "病案审核标准");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSMROComboModule.x";
    }

    public String getModuleMethodName() {
        return "getMroChrtvetstd";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;text:TEXT;py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("TypeCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        if ("TypeCode".equalsIgnoreCase(name)) {
            setTypeCode(value);
            getTObject().setValue("TypeCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * 执行查询方法(COMBO联动)
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("TYPE_CODE", this.getTagValue(this.getTypeCode()));
        this.setModuleParm(parm);
        super.onQuery();
    }

    /**
     * 设置主身份注记
     * @param typeCode String
     */
    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }

    /**
     * 得到主身份注记
     * @return String
     */
    public String getTypeCode() {
        return typeCode;
    }
}
