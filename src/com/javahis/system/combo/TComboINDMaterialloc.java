package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title: 料位下拉列表</p>
 *
 * <p>Description: 料位下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.04.29
 * @version 1.0
 */
public class TComboINDMaterialloc
    extends TComboBox {
    /**
     * 部门代码
     */
    private String orgCode;
    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("ORG_CODE", getTagValue(getOrgCode()));
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
        object.setValue("Tip", "料位");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "ind\\INDMateriallocModule.x";
    }

    public String getModuleMethodName() {
        return "initMaterialLocCode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("OrgCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("OrgCode".equalsIgnoreCase(name)) {
            setOrgCode(value);
            getTObject().setValue("OrgCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * 得到部门代码
     * @return String
     */
    public String getOrgCode() {
        return orgCode;
    }

    /**
     * 设置部门代码
     * @param orgCode String
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }

}
