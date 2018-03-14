package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 主管分类下拉区域</p>
 *
 * <p>Description: 主管分类下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author robo 2012.06.19
 * @version 1.0
 */
public class TextFormatCompetentType extends TTextFormat {
    /**
     * 主管分类代码
     */
    private String competentTypeCode = "SYS_COMPETENT_TYPE";
    /**
     * 设置单位代码
     * @param companyCode String
     */
    public void setCompetentTypeCode(String competentTypeCode) {
        this.competentTypeCode = competentTypeCode;
        setModifySQL(true);
    }

    /**
     * 得到单位代码
     * @return String
     */
    public String getCompetentTypeCode() {
        return competentTypeCode;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,DATA FROM SYS_DICTIONARY  ";
        StringBuffer sb = new StringBuffer();
        String companyCodeTemp = TypeTool.getString(getTagValue(getCompetentTypeCode()));
        if (companyCodeTemp != null && companyCodeTemp.length() > 0)
            sb.append(" GROUP_ID = '" + companyCodeTemp + "' ");
        String sql1 = " ORDER BY SEQ,ID ";
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
        object.setValue("Tip", "主管分类");
        object.setValue("ShowColumnList", "ID;NAME");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * 显示主管分类列名
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
        data.add(new TAttribute("CompanyCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("CompetentTypeCode".equalsIgnoreCase(name)) {
            setCompetentTypeCode(value);
           // getTObject().setValue("CompanyCode", value);
            return;
        }

        super.setAttribute(name, value);
    }


}
