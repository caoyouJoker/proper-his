package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title:身份折扣下拉列表 </p>
 *
 * <p>Description:身份折扣下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.26
 * @version 1.0
 */
public class TComboSysCtz
    extends TComboBox {

    /**
     * 主身份注记
     */
    private String mainCtzFlg;
    /**
     * 医保身份注记
     */
    private String nhiCtzFlg;
    /**
     * 设置医保身份注记
     * @param nhiCtzFlg String
     */
    public void setNhiCtzFlg(String nhiCtzFlg) {
        this.nhiCtzFlg = nhiCtzFlg;
    }

    /**
     * 设置主身份
     * @param mainCtzFlg String
     */
    public void setMainCtzFlg(String mainCtzFlg) {
        this.mainCtzFlg = mainCtzFlg;
    }

    /**
     * 得到医保身份注记
     * @return String
     */
    public String getNhiCtzFlg() {
        return nhiCtzFlg;
    }

    /**
     * 得到主身份
     * @return String
     */
    public String getMainCtzFlg() {
        return mainCtzFlg;
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("MAIN_CTZ_FLG", getTagValue(getMainCtzFlg()));
        parm.setDataN("NHI_CTZ_FLG", getTagValue(getNhiCtzFlg()));
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
        object.setValue("Tip", "身份");
        object.setValue("TableShowList", "id,name");
    }

    public String getModuleName() {
        return "sys\\SYSCTZModule.x";
    }

    public String getModuleMethodName() {
        return "initCtzCode";
    }

    public String getParmMap() {
        return "id:ID;text:TEXT;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("MainCtzFlg", "String", "", "Left"));
        data.add(new TAttribute("NhiCtzFlg", "String", "", "Left"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("MainCtzFlg".equalsIgnoreCase(name)) {
            setMainCtzFlg(value);
            getTObject().setValue("MainCtzFlg", value);
            return;
        }
        if ("NhiCtzFlg".equalsIgnoreCase(name)) {
            setNhiCtzFlg(value);
            getTObject().setValue("NhiCtzFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
