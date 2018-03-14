package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 外挂动作下拉区域</p>
 *
 * <p>Description: 外挂动作下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.16
 * @version 1.0
 */
public class TextFormatSYSAction
    extends TTextFormat {
    /**
     * 模块类型
     */
    private String proType;

    /**
     * 得到模块类型
     * @return String
     */
    public String getProType() {
        return proType;
    }

    /**
     * 设置模块类型
     * @param proType String
     */
    public void setProType(String proType) {
        this.proType = proType;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ACTION_ID AS ID,ACTION_DESC AS NAME,ENNAME,PY1,PY2 FROM SYS_ACTION ";
        String sql1 = " ORDER BY ACTION_ID ";
        StringBuffer sb = new StringBuffer();
        String type = TypeTool.getString(getTagValue(getProType()));
        if (type != null && type.length() > 0)
            sb.append(" PRO_TYPE = '" + type + "' ");

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        System.out.println("sql" + sql);
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "外挂动作");
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
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("ProType", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("ProType".equalsIgnoreCase(name)) {
            setProType(value);
            getTObject().setValue("ProType", value);
            return;
        }
        super.setAttribute(name, value);
    }

}
