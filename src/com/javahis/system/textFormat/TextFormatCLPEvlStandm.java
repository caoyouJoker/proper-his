package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;
import jdo.sys.*;

/**
 * <p>Title: 评估代码下拉区域</p>
 *
 * <p>Description: 评估代码下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2011.05.10
 * @version 1.0
 */
public class TextFormatCLPEvlStandm
    extends TTextFormat {
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 临床路径项目代码
     */
    private String clncpathCode;//CLNCPATH_CODE
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT EVL_CODE AS ID,EVL_CHN_DESC AS NAME,EVL_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM CLP_EVL_STANDM ";
        String sql1 = " ORDER BY EVL_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String RegionCodeTemp = TypeTool.getString(getTagValue(getRegionCode()));
        if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + RegionCodeTemp + "' ");
        }
        String clncpathCodeTemp = TypeTool.getString(getTagValue(getClncpathCode()));
        if (clncpathCodeTemp != null && clncpathCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" CLNCPATH_CODE = '" + clncpathCodeTemp + "' ");
        }
        String operatorCodeAll = Operator.getRegion();
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + operatorCodeAll + "' ");
        }
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        System.out.println("评估代码下拉区域sql>>>>>>>"+sql);
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "评估代码");
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
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("ClncpathCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("RegionCode".equalsIgnoreCase(name)) {
            this.setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("ClncpathCode".equalsIgnoreCase(name)) {
            this.setClncpathCode(value);
            getTObject().setValue("ClncpathCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }
    /**
     * 得到临床路径项目代码
     * @return String
     */
    public String getClncpathCode() {
        return clncpathCode;
    }

    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    /**
     * 设置临床路径项目代码
     * @param clncpathCode String
     */
    public void setClncpathCode(String clncpathCode) {
        this.clncpathCode = clncpathCode;
    }
}
