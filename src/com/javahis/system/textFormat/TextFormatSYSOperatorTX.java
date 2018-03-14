package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 适用人员(泰心专用)</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatSYSOperatorTX
    extends TTextFormat {
    /**
     * 科室
     */
    private String dept;
    /**
     * 区域
     *
     */
    private String regionCode;

    public TextFormatSYSOperatorTX() {
    }

    /**
     * 设置科室
     * @param dept String
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * 得到科室
     * @return String
     */
    public String getDept() {
        return dept;
    }


    /**
     * 设置区域
     * @param regionCode String
     *
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 设置区域
     * @param regionCode String
     *
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            "SELECT USER_ID AS ID,USER_NAME AS NAME,NAME_PYCODE AS PY1 FROM SYS_OPERATOR";
        String sql1 = " ORDER BY USER_ID";

        StringBuffer sb = new StringBuffer();
        String dept = TypeTool.getString(getTagValue(getDept()));
        if (dept != null && dept.length() > 0) {
            sb.append(
                " USER_ID IN (SELECT DR_CODE FROM SYS_DRDEPT WHERE DEPT_CODE='" +
                dept + "' )");
        }
        //从Mettis传入,暂时写死;
        String operatorCodeAll = "HIS";
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            if (sb.length() > 0) {
                sb.append(" AND ");
            }
            sb.append(" HOSP_AREA = '" + operatorCodeAll + "' ");
        }

        if (sb.length() > 0) {
            sql += " WHERE " + sb.toString() + sql1;
        }
        else {
            sql = sql + sql1;
        }

        return sql;
    }

    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) {
            return;
        }
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
        object.setValue("Tip", "人员");
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
        data.add(new TAttribute("Dept", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
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
        if ("Dept".equalsIgnoreCase(name)) {
            setDept(value);
            getTObject().setValue("Dept", value);
            return;
        }

        super.setAttribute(name, value);
    }


}
