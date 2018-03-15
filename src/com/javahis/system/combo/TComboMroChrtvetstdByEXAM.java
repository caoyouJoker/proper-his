package com.javahis.system.combo;

import com.dongyang.ui.*;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 病案审核标准 </p>
 *
 * <p>Description: 使用EXAMINE_CODE检索 </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author wanglong 20130909
 * @version 1.0
 */
public class TComboMroChrtvetstdByEXAM
    extends TComboBox {
    /**
     * 审核编号
     */
    private String examineCode;

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
        data.add(new TAttribute("ExamineCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String
     * @param value String
     */
    public void setAttribute(String name, String value) {
        if ("ExamineCode".equalsIgnoreCase(name)) {
            setExamineCode(value);
            getTObject().setValue("ExamineCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * 执行查询方法(COMBO联动)
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("EXAMINE_CODE", this.getTagValue(this.getExamineCode()));
        this.setModuleParm(parm);
        super.onQuery();
    }

    /**
     * 设置审核编码
     * @return
     */
    public String getExamineCode() {
        return examineCode;
    }

    /**
     * 得到审核编码
     * @param examineCode
     */
    public void setExamineCode(String examineCode) {
        this.examineCode = examineCode;
    }
}
