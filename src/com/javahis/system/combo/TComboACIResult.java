package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 事故差错后果下拉列表</p>
 *
 * <p>Description: 事故差错后果下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.09.30
 * @version 1.0
 */
public class TComboACIResult
    extends TComboBox {
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
        object.setValue("Tip", "事故差错后果");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "GROUP_ID:ACI_RESLUT");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSDictionaryModule.x";
    }

    public String getModuleMethodName() {
        return "getGroupList";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
    }
}
