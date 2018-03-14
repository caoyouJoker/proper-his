package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 健康检查套餐下拉区域</p>
 *
 * <p>Description: 健康检查套餐下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.18
 * @version 1.0
 */
public class TextFormatHRMPackagem
    extends TTextFormat {
    /**
     * 启用注记
     */
    private String activeFlg;
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT PACKAGE_CODE AS ID,PACKAGE_DESC AS NAME,DESCRIPTION,ENNAME,PY1,PY2 " +
            "   FROM HRM_PACKAGEM ";
        String sql1 = " ORDER BY PACKAGE_CODE ";
        StringBuffer sb = new StringBuffer();
        String activeFlgTemp = TypeTool.getString(getTagValue(getActiveFlg()));
        if (activeFlgTemp != null && activeFlgTemp.length() > 0)
            sb.append(" ACTIVE_FLG = '" + activeFlgTemp + "' ");
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        //System.out.println("SQL==="+sql);
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
        object.setValue("PopupMenuHeader", "代码,100;名称,150;备注,200");
        object.setValue("PopupMenuWidth", "450");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;DESCRIPTION,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "健康检查套餐");
        object.setValue("ShowColumnList", "ID;NAME;DESCRIPTION");

    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name;Remark");
    }

    /**
     * 显示区域列明
     * @return String
     */
    public String getPopupMenuHeader() {

        return "代码,100;名称1,150;备注,200";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("ActiveFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("ActiveFlg".equalsIgnoreCase(name)) {
            setActiveFlg(value);
            getTObject().setValue("ActiveFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }


    public String getActiveFlg() {
        return activeFlg;
    }

    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }
}
