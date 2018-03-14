package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 剂型下拉区域</p>
 *
 * <p>Description: 剂型下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatPHADose
    extends TTextFormat {
    /**
     * 剂型分类
     */
    private String doseType;
    /**
     * 设置剂型分类
     * @param doseType String
     */
    public void setDoseType(String doseType) {

        this.doseType = doseType;
        setModifySQL(true);
    }

    /**
     * 得到剂型分类
     * @return String
     */
    public String getDoseType() {
        return doseType;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DOSE_CODE AS ID,DOSE_CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM PHA_DOSE ";
        String sql1 = " ORDER BY DOSE_CODE,SEQ ";

        StringBuffer sb = new StringBuffer();

        String doseType = TypeTool.getString(getTagValue(getDoseType()));
        if (doseType != null && doseType.length() > 0)
            sb.append(" DOSE_TYPE = '" + doseType + "' ");

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
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
        object.setValue("Tip", "剂型");
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
        data.add(new TAttribute("DoseType", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("DoseType".equalsIgnoreCase(name)) {
            setDoseType(value);
            getTObject().setValue("DoseType", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
