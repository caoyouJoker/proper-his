package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;
import jdo.sys.Operator;

/**
 * <p>Title: 领药窗口下拉区域</p>
 *
 * <p>DescripAtion: 领药窗口下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.03.31
 * @version 1.0
 */
public class TextFormatPHACounterNo
    extends TTextFormat {
    /**
     * 药房代码
     */
    private String orgCode;
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT A.COUNTER_NO AS ID,A.COUNTER_DESC AS NAME,A.COUNTER_ENG_DESC AS ENNAME,A.PY1,A.PY2 " +
            "   FROM PHA_COUNTERNO A, IND_ORG B " +
            "  WHERE A.ORG_CODE = B.ORG_CODE "+
            "    AND A.CHOSEN_FLG = 'Y' ";
        String sql1 = " ORDER BY A.COUNTER_NO ";
        StringBuffer sb = new StringBuffer();
        String orgCodeTemp = TypeTool.getString(getTagValue(getOrgCode()));
        if (orgCodeTemp != null && orgCodeTemp.length() > 0)
            sb.append(" AND A.ORG_CODE = '" + orgCodeTemp + "' ");
        String RegionCodeTemp = TypeTool.getString(getTagValue(getRegionCode()));
        if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
            sb.append(" AND B.REGION_CODE = '" + RegionCodeTemp + "' ");
        }
        String operatorCodeAll = Operator.getRegion();
//        System.out.println("区域"+Operator.getRegion());
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            sb.append(" AND B.REGION_CODE = '" + operatorCodeAll + "' ");
        }
        sql = sql + sb.toString() + sql1;
//        System.out.println("领药号窗口sql》》》》》》》"+sql);
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
        object.setValue("Tip", "科室");
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
        data.add(new TAttribute("OrgCode", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("OrgCode".equalsIgnoreCase(name)) {
            setOrgCode(value);
            getTObject().setValue("OrgCode", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    public String getOrgCode() {
        return orgCode;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }


    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
        setModifySQL(true);
    }

    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

}
