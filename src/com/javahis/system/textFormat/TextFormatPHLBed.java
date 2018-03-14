package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 静点床位下拉区域</p>
 *
 * <p>Description: 静点床位下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatPHLBed
    extends TTextFormat {
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 床位状态
     */
    private String bedStatus;
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 设置床位状态
     * @param bedStatus String
     */
    public void setBedStatus(String bedStatus) {
        this.bedStatus = bedStatus;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 得到床位状态
     * @return String
     */
    public String getBedStatus() {
        return bedStatus;
    }


    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT BED_NO AS ID,BED_DESC AS NAME,ENNAME FROM PHL_BED ";
        String sql1 = " ORDER BY BED_NO ";

        StringBuffer sb = new StringBuffer();

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0)
            sb.append(" REGION_CODE = '" + regionCode + "' ");

        String bedStatus = TypeTool.getString(getTagValue(getBedStatus()));
        if (bedStatus != null && bedStatus.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BED_STATUS = '" + bedStatus + "' ");
        }

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
        object.setValue("Tip", "静点床位");
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
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("BedStatus", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("BedStatus".equalsIgnoreCase(name)) {
            setBedStatus(value);
            getTObject().setValue("BedStatus", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
