package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title: 分类规则下拉列表</p>
 *
 * <p>Description: 分类规则下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSCategory
    extends TComboBox {
    /**
     * 规则类别
     */
    private String ruleType; //RULE_TYPE
    /**
     * 最小分类注记
     */
    private String detailFlg; //DETAIL_FLG
    /**
     * 设置规则类别
     * @param ruleType String
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * 得到规则类别
     * @return String
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * 设置最小分类注记
     * @param detailFlg String
     */
    public void setDetailFlg(String detailFlg) {
        this.detailFlg = detailFlg;
    }

    /**
     * 得到最小分类注记
     * @return String
     */
    public String getDetailFlg() {
        return detailFlg;
    }

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
        parm.setDataN("RULE_TYPE", getTagValue(getRuleType()));
        parm.setDataN("DETAIL_FLG", getTagValue(getDetailFlg()));
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
        object.setValue("Tip", "分类规则");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSCategoryModule.x";
    }

    public String getModuleMethodName() {
        return "initCategoryCode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("RuleType", "String", "", "Left")); //增加规则分类筛选条件
        data.add(new TAttribute("DetailFlg", "String", "", "Left")); //增加最小分类注记筛选条件
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("RuleType".equalsIgnoreCase(name)) {
            setRuleType(value);
            getTObject().setValue("RuleType", value);
            return;
        }
        if ("DetailFlg".equalsIgnoreCase(name)) {
            setDetailFlg(value);
            getTObject().setValue("DetailFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
