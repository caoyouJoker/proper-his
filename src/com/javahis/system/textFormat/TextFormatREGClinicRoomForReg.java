package com.javahis.system.textFormat;

import java.sql.*;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.manager.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;
import jdo.sys.Operator;

/**
 * <p>Title: 门诊适用诊室下拉区域</p>
 *
 * <p>Description: 门诊适用诊室下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatREGClinicRoomForReg
    extends TTextFormat {
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 门急别
     */
    private String admType;
    /**
     * 就诊日期
     */
    private String admDate;
    /**
     * 时段
     */
    private String sessionCode;
    /**
     * 静点区域
     */
    private String phlRegionCode;
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {

        this.regionCode = regionCode;
        setModifySQL(true);
    }

    /**
     * 设置门急别
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
        setModifySQL(true);
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }


    /**
     * 得到门急别
     * @return String
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * 设置时段
     * @param sessionCode String
     */
    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
        setModifySQL(true);
    }

    /**
     * 设置就诊日期
     * @param admDate String
     */
    public void setAdmDate(String admDate) {
        this.admDate = admDate;
        setModifySQL(true);
    }

    /**
     * 设置静点区域
     * @param phlRegionCode String
     */
    public void setPhlRegionCode(String phlRegionCode) {
        this.phlRegionCode = phlRegionCode;
    }


    /**
     * 得到就诊日期
     * @return String
     */
    public String getAdmDate() {
        return admDate;
    }


    /**
     * 得到时段
     * @return String
     */
    public String getSessionCode() {
        return sessionCode;
    }

    /**
     * 得到静点区域
     * @return String
     */
    public String getPhlRegionCode() {
        return phlRegionCode;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DISTINCT A.CLINICROOM_NO AS ID,B.CLINICROOM_DESC AS NAME,B.ENG_DESC AS ENNAME,B.PY1 AS PY1, B.PY2 AS PY2  " +
            "   FROM REG_SCHDAY A,REG_CLINICROOM B  " +
            "  WHERE B.ACTIVE_FLG = 'Y' " +
            "    AND A.CLINICROOM_NO = B.CLINICROOM_NO ";
        String sql1 = " ORDER BY A.CLINICROOM_NO ";

        StringBuffer sb = new StringBuffer();

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0)
            sb.append(" AND A.REGION_CODE = '" + regionCode + "' ");

        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0) {
            sb.append(" AND A.ADM_TYPE = '" + admType + "' ");
        }
        Object value = getTagValue(getAdmDate());
        if (value instanceof Timestamp)
            value = StringTool.getString(TCM_Transform.getTimestamp(value),
                                         "yyyyMMdd");
        else
            value = getTagValue(getAdmDate());

        String admDate = TypeTool.getString(value);
        if (admDate != null && admDate.length() > 0) {
            sb.append(" AND A.ADM_DATE = '" + admDate + "' ");
        }

        String sessionCode = TypeTool.getString(getTagValue(getSessionCode()));
        if (sessionCode != null && sessionCode.length() > 0) {
            sb.append(" AND A.SESSION_CODE = '" + sessionCode + "' ");
        }
        String phlRegionCode = TypeTool.getString(getTagValue(getPhlRegionCode()));
        if (phlRegionCode != null && phlRegionCode.length() > 0) {
            sb.append(" AND B.PHL_REGION_CODE = '" + phlRegionCode + "' ");
        }
        //=============pangben modify 20110420 start 添加号别区域筛选
               String operatorCodeAll = Operator.getRegion();
               if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
//                   if (sb.length() > 0)
//                       sb.append(" AND ");
                   sb.append(" AND B.REGION_CODE = '" + operatorCodeAll + "' ");
               }
       //=============pangben modify 20110420 stop
        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
       // System.out.println("sql1:::@"+ sql);
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
        object.setValue("Tip", "门诊适用诊室");
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
        data.add(new TAttribute("AdmType", "String", "", "Left"));
        data.add(new TAttribute("AdmDate", "String", "", "Left"));
        data.add(new TAttribute("SessionCode", "String", "", "Left"));
        data.add(new TAttribute("PhlRegionCode", "String", "", "Left"));
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
        if ("AdmType".equalsIgnoreCase(name)) {
            setAdmType(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        if ("AdmDate".equalsIgnoreCase(name)) {
            setAdmDate(value);
            getTObject().setValue("AdmDate", value);
            return;
        }
        if ("SessionCode".equalsIgnoreCase(name)) {
            setSessionCode(value);
            getTObject().setValue("SessionCode", value);
            return;
        }
        if ("PhlRegionCode".equalsIgnoreCase(name)) {
            setPhlRegionCode(value);
            getTObject().setValue("PhlRegionCode", value);
            return;
        }

        super.setAttribute(name, value);
    }
}
