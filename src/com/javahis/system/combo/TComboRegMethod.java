package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.data.TParm;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
 *
 * <p>Title:挂号方式下拉列表 </p>
 *
 * <p>Description:挂号方式下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.10.07
 * @version 1.0
 */
public class TComboRegMethod extends TComboBox {
    /**
     * 手动选择注记
     */
    private String comboFlg;
    /**
     * 得到手动选择注记
     * @return String
     */
    public String getComboFlg() {
        return comboFlg;
    }

    /**
     * 设置手动选择注记
     * @param comboFlg String
     */
    public void setComboFlg(String comboFlg) {
        this.comboFlg = comboFlg;
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("COMBO_FLG", getTagValue(getComboFlg()));
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
        object.setValue("Tip", "挂号方式");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "reg\\REGRegMethodModule.x";
    }

    public String getModuleMethodName() {
        return "getregmethodCombo";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ComboFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("ComboFlg".equalsIgnoreCase(name)) {
            setComboFlg(value);
            getTObject().setValue("ComboFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
