package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 医嘱分类下拉区域</p>
 *
 * <p>Description: 医嘱分类下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSOrderCat1Code
    extends TTextFormat {
    /**
     * 医嘱细分类群组
     */
    private String cat1Type;
    /**
     * 设置医嘱细分类群组
     * @param cat1Type String
     */
    public void setCat1Type(String cat1Type) {
        this.cat1Type = cat1Type;
    }

    /**
     * 得到医嘱细分类群组
     * @return String
     */
    public String getCat1Type() {
        return cat1Type;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ORDER_CAT1_CODE AS ID,ORDER_CAT1_DESC AS NAME,ENNAME,PY1,PY2 FROM SYS_ORDER_CAT1 ";
        String sql1 = " ORDER BY SEQ,ORDER_CAT1_CODE ";

        StringBuffer sb = new StringBuffer();

        String cat1Type = TypeTool.getString(getTagValue(getCat1Type()));
        if (cat1Type != null && cat1Type.length() > 0)
            sb.append(" CAT1_TYPE = '" + cat1Type + "' ");

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//    System.out.println("sql"+sql);
        return sql;
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
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "代码,100;名称,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "医嘱分类");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }


    /**
     * 显示区域列明
     * @return String
     */
    public String getPopupMenuHeader() {
        return "代码,100;名称,200";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("Cat1Type", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("Cat1Type".equalsIgnoreCase(name)) {
            setCat1Type(value);
            getTObject().setValue("Cat1Type", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
