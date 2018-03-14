package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 医疗统计系统 中间对照科室 combo</p>
 *
 * <p>Description: 医疗统计系统 中间对照科室 combo</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-6-18
 * @version 1.0
 */
public class TComboSTADept
    extends TComboBox {
    /**
     * 科室等级
     */
    private String grade;
    /**
     * 设置科室等级
     * @param grade String
     */
    public void setGrade(String grade)
    {
        this.grade = grade;
    }
    /**
     * 得到科室等级
     * @return String
     */
    public String getGrade()
    {
        return grade;
    }

    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("DEPT_LEVEL",getTagValue(getGrade()));
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
        object.setValue("Tip", "对照科室");
        object.setValue("TableShowList", "id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSSTADeptModule.x";
    }
    public String getModuleMethodName()
    {
        return "initData";
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
        data.add(new TAttribute("Grade","String","","Left"));//科室等级属性
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("Grade".equalsIgnoreCase(name))
        {
            setGrade(value);
            getTObject().setValue("Grade",value);
            return;
        }
        super.setAttribute(name,value);
    }

}
