package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 病房下拉区域</p>
 *
 * <p>Description: 病房下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSRoom
    extends TTextFormat {
    /**
     * 病区
     */
    private String stationCode;
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 性别管制
     */
    private String sexLimitFlg;
    /**
     * 设置病区
     * @param stationCode String
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    /**
     * 得到病区
     * @return String
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 设置性别管制
     * @param sexLimitFlg String
     */
    public void setSexLimitFlg(String sexLimitFlg) {
        this.sexLimitFlg = sexLimitFlg;
    }

    /**
     * 得到性别管制
     * @return String
     */
    public String getSexLimitFlg() {
        return sexLimitFlg;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ROOM_CODE AS ID,ROOM_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_ROOM ";
        String sql1 = " ORDER BY SEQ,ROOM_CODE ";

        StringBuffer sb = new StringBuffer();

        String stationCode = TypeTool.getString(getTagValue(getStationCode()));
        if (stationCode != null && stationCode.length() > 0)
            sb.append(" STATION_CODE = '" + stationCode + "' ");

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + regionCode + "' ");
        }

        String sexLimitFlg = TypeTool.getString(getTagValue(getSexLimitFlg()));
        if (sexLimitFlg != null && sexLimitFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" SEX_LIMIT_FLG = '" + sexLimitFlg + "' ");
        }

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
        object.setValue("Tip", "病房");
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
        data.add(new TAttribute("StationCode", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("SexLimitFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("StationCode".equalsIgnoreCase(name)) {
            setStationCode(value);
            getTObject().setValue("StationCode", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("SexLimitFlg".equalsIgnoreCase(name)) {
            setSexLimitFlg(value);
            getTObject().setValue("SexLimitFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
