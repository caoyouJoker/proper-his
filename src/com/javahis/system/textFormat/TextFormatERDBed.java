package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 留观床位下拉区域</p>
 *
 * <p>Description: 留观床位下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.11.17
 * @version 1.0
 */
public class TextFormatERDBed
    extends TTextFormat {
    /**
     * 留观病区
     */
    private String erdRegionCode; //ERD_REGION_CODE
    /**
     * 占床注记
     */
    private String occupyFlg; //OCCUPY_FLG
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT BED_NO AS ID,BED_DESC AS NAME,ENNAME,PY1,PY2 " +
            "   FROM ERD_BED ";
        String sql1 = " ORDER BY BED_NO ";
        StringBuffer sb = new StringBuffer();
        String erdRegionCode = TypeTool.getString(getTagValue(getErdRegionCode()));
        if (erdRegionCode != null && erdRegionCode.length() > 0)
            sb.append(" ERD_REGION_CODE = '" + erdRegionCode + "' ");

        String occupyFlg = TypeTool.getString(getTagValue(getOccupyFlg()));
        if (occupyFlg != null && occupyFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OCCUPY_FLG = '" + occupyFlg + "' ");
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
        object.setValue("Tip", "留观床位");
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
        data.add(new TAttribute("ErdRegionCode", "String", "", "Left"));
        data.add(new TAttribute("OccupyFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("ErdRegionCode".equalsIgnoreCase(name)) {
            setErdRegionCode(value);
            getTObject().setValue("ErdRegionCode", value);
            return;
        }
        if ("OccupyFlg".equalsIgnoreCase(name)) {
            setOccupyFlg(value);
            getTObject().setValue("OccupyFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }


    public String getOccupyFlg() {
        return occupyFlg;
    }


    public String getErdRegionCode() {
        return erdRegionCode;
    }


    public void setOccupyFlg(String occupyFlg) {
        this.occupyFlg = occupyFlg;
        setModifySQL(true);
    }


    public void setErdRegionCode(String erdRegionCode) {
        this.erdRegionCode = erdRegionCode;
        setModifySQL(true);
    }

}
