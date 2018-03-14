package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 血品规格下拉列表</p>
 *
 * <p>Description: 血品规格下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.09.28
 * @version 1.0
 */
public class TComboBMSBldsubcat
    extends TComboBox {
    /**
     * 血品代码
     */
    private String bldCode; //BLD_CODE
    /**
     * 设置血品代码
     * @param bldCode String
     */
    public void setBldCode(String bldCode) {
        this.bldCode = bldCode;
    }

    /**
     * 得到血品代码
     * @return String
     */
    public String getBldCode() {
        return bldCode;
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("BLD_CODE", getTagValue(getBldCode()));
        setModuleParm(parm);
        super.onQuery();
    }

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
        object.setValue("Tip", "诊室");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "bms\\BMSBldSubcatModule.x";
    }

    public String getModuleMethodName() {
        return "initCombo";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("BldCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("BldCode".equalsIgnoreCase(name)) {
            setBldCode(value);
            getTObject().setValue("BldCode", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
