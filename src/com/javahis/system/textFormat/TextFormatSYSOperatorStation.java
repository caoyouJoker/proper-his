package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;

/**
 * <p>Title: 用户病区诊区下拉区域</p>
 *
 * <p>Description: 用户病区诊区下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatSYSOperatorStation
    extends TTextFormat {
    /**
     * 操作者
     */
    private String userID;
    /**
     * 类别
     */
    private String Stype;
    
    /**
	 * 得到类别
	 */
	public String getStype() {
		return Stype;
	}

	/**
	 * 设置类别
	 */
	public void setStype(String stype) {
		Stype = stype;
	}
	/**
     * 得到操作者
     * @return String
     */
    public String getUserID() {
        return userID;
    }

    /**
     * 设置操作者
     * @param userID String
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sqlWhere = "";
        String userID = TypeTool.getString(getTagValue(getUserID()));
        if (userID != null && userID.length() > 0)
            sqlWhere = " AND USER_ID = '" + userID + "' ";
        String sql =
            " SELECT * FROM (SELECT CLINICAREA_CODE AS ID, CLINIC_DESC AS NAME,ENG_DESC AS ENNAME, PY1, PY2, '1' AS TYPE " +
            "    FROM REG_CLINICAREA " +
            "   WHERE CLINICAREA_CODE IN (SELECT STATION_CLINIC_CODE " +
            "    FROM SYS_OPERATOR_STATION " +
            "   WHERE TYPE = '1' " + sqlWhere + "  ) " +
            "   UNION ALL " +
            "  SELECT STATION_CODE AS ID, STATION_DESC AS NAME,ENG_DESC AS ENNAME,PY1, PY2, '2' AS TYPE " +
            "    FROM SYS_STATION " +
            "   WHERE STATION_CODE IN (SELECT STATION_CLINIC_CODE " +
            "    FROM SYS_OPERATOR_STATION " +
            "   WHERE TYPE = '2' " + sqlWhere + "  )) ";
        String sql1=  "  ORDER BY TYPE, ID ";
        String sb="";
        String type = TypeTool.getString(getTagValue(this.getStype()));
        if (type != null && type.length() > 0)
            sb = " TYPE = '" + type + "' ";
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
        object.setValue("Tip", "用户病区诊区");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * 显示区域列名
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
        data.add(new TAttribute("UserID", "String", "", "Left"));
        data.add(new TAttribute("Stype", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("UserID".equalsIgnoreCase(name)) {
            setUserID(value);
            getTObject().setValue("UserID", value);
            return;
        }
        if ("Stype".equalsIgnoreCase(name)) {
            this.setStype(value);
            getTObject().setValue("Stype", value);
            return;
        }
        super.setAttribute(name, value);
    }

}
