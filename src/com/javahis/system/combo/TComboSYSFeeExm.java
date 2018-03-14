package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 检验检查分类下拉列表</p>
 *
 * <p>Description: 检验检查分类下拉列表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TComboSYSFeeExm extends TComboBox{

    /**
     * 第一级
     */
    private String CLASSIFY1;
    /**
     * 第二级
     */
    private String CLASSIFY2;
    /**
     * 第三级
     */
    private String CLASSIFY3;
    /**
     * 分类代码
     */
    private String CATEGORY_CODE;


    public TComboSYSFeeExm() {
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
//        System.out.println(this.getTag() + " onQuery()");
        TParm parm = new TParm();
        parm.setDataN("CLASSIFY1", getTagValue(getCLASSIFY1()));
        parm.setDataN("CLASSIFY2", getTagValue(getCLASSIFY2()));
        parm.setDataN("CLASSIFY3", getTagValue(getCLASSIFY3()));
        String s = TypeTool.getString(getTagValue(getCATEGORY_CODE()));
        if(s != null && s.length() > 0)
            parm.setDataN("CATEGORY_CODE", s + "%");
//        System.out.println("parm=" + parm);
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
        object.setValue("Tip", "检验检查分类");
        object.setValue("TableShowList", "id,name");
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("CLASSIFY1", "String", "", "Left"));
        data.add(new TAttribute("CLASSIFY2", "String", "", "Left"));
        data.add(new TAttribute("CLASSIFY3", "String", "", "Left"));
        data.add(new TAttribute("CATEGORY_CODE", "String", "", "Left"));
    }


    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("CLASSIFY1".equalsIgnoreCase(name)) {
            setCLASSIFY1(value);
            getTObject().setValue("CLASSIFY1", value);
            return;
        }
        if ("CLASSIFY2".equalsIgnoreCase(name)) {
            setCLASSIFY2(value);
            getTObject().setValue("CLASSIFY2", value);
            return;
        }
        if ("CLASSIFY3".equalsIgnoreCase(name)) {
            setCLASSIFY3(value);
            getTObject().setValue("CLASSIFY3", value);
            return;
        }
        if ("CATEGORY_CODE".equalsIgnoreCase(name)) {
            setCATEGORY_CODE(value);
            getTObject().setValue("CATEGORY_CODE", value);
            return;
        }
        super.setAttribute(name, value);
    }



    public String getModuleName()
    {
        return "sys\\SYSFeeExmModule.x";
    }
    public String getModuleMethodName()
    {
        return "selectdata";
    }

    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    public String getCATEGORY_CODE() {
        return CATEGORY_CODE;
    }

    public String getCLASSIFY1() {
        return CLASSIFY1;
    }

    public String getCLASSIFY2() {
        return CLASSIFY2;
    }

    public String getCLASSIFY3() {
        return CLASSIFY3;
    }

    public void setCATEGORY_CODE(String CATEGORY_CODE) {
        this.CATEGORY_CODE = CATEGORY_CODE;
    }

    public void setCLASSIFY1(String CLASSIFY1) {
        this.CLASSIFY1 = CLASSIFY1;
    }

    public void setCLASSIFY2(String CLASSIFY2) {
        this.CLASSIFY2 = CLASSIFY2;
    }

    public void setCLASSIFY3(String CLASSIFY3) {
        this.CLASSIFY3 = CLASSIFY3;
    }


}
