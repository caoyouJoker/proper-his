package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TComboNode;
import com.dongyang.data.TParm;

/**
 * <p>Title: 医保程序</p>
 *
 * <p>Description: 特约请款机关COMBO</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class TComboSYS_CompanyCode extends TComboBox {
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
        object.setValue("Tip", "特约请款单位");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "HOSP_AREA:HIS");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSComboSQL.x";
    }

    public String getModuleMethodName() {
        return "getGroupList";
    }

    public String getParmMap() {
        return "id:COMPANY_CODE;name:COMPANY_DESC";
    }
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
  }
    /**
     * 设置属性
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
    }

    /**
     * 执行查询方法(COMBO联动)
     */
    public void onQuery() {
        super.onQuery();
    }

}
