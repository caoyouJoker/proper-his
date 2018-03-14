package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 套餐模版下拉区域</p>
 *
 * <p>Description: 套餐模版下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.17
 * @version 1.0
 */
public class TextFormatSYSOrderPackM
    extends TTextFormat {
    /**
     * 适用科室
     */
    private String fitDept;
    /**
     * 设置适用科室
     * @param fitDept String
     */
    public void setFitDept(String fitDept) {

        this.fitDept = fitDept;
        setModifySQL(true);
    }

    /**
     * 得到适用科室
     * @return String
     */
    public String getFitDept() {
        return fitDept;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT PACK_CODE AS ID,PACK_DESC AS NAME,ENNAME,PY1,PY2 " +
            "   FROM SYS_ORDER_PACKM ";
        String sql1 = " ORDER BY PACK_CODE,SEQ";

        StringBuffer sb = new StringBuffer();

        String fitDept = TypeTool.getString(getTagValue(getFitDept()));
        if (fitDept != null && fitDept.length() > 0)
            sb.append(" FIT_DEPT = '" + fitDept + "' ");

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
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
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "套餐模版");
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
        data.add(new TAttribute("FitDept", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("FitDept".equalsIgnoreCase(name)) {
            setFitDept(value);
            getTObject().setValue("FitDept", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
