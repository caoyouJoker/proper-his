package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

import jdo.sys.Operator;
/**
 * <p>Title: 适用临床路径下拉区域</p>
 *
 * <p>Description: 适用临床路径(启用)下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author kangy 2017.03.20
 * @version 1.0
 */
public class TextFormatCLPBscInfoUsed extends TTextFormat{
	 /**
     * 科室
     */
    private String deptCode;
    /**
     * 启用注记
     */
    private String activeFlg;
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
            " SELECT CLNCPATH_CODE AS ID,CLNCPATH_CHN_DESC AS NAME,CLNCPATH_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM CLP_BSCINFO "+
            "  WHERE ACTIVE_FLG ='Y' ";
        String sql1 = " ORDER BY CLNCPATH_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String deptCodeTemp = TypeTool.getString(getTagValue(
            getDeptCode()));
        if (deptCodeTemp != null && deptCodeTemp.length() > 0) {
            sb.append(" AND DEPT_CODE = '" + deptCodeTemp + "' ");
        }
        String activeFlgTemp = TypeTool.getString(getTagValue(
            getActiveFlg()));
        if (activeFlgTemp != null && activeFlgTemp.length() > 0) {
            sb.append(" AND ACTIVE_FLG = '" + activeFlgTemp + "' ");
        }

        String RegionCodeTemp = TypeTool.getString(getTagValue(getRegionCode()));
        if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
            sb.append(" AND REGION_CODE = '" + RegionCodeTemp + "' ");
        }
        String operatorCodeAll = Operator.getRegion();
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            sb.append(" AND REGION_CODE = '" + operatorCodeAll + "' ");
        }
        sql = sql +sb.toString()+ sql1;
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
        object.setValue("Tip", "适用临床路径(启用)");
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
        data.add(new TAttribute("DeptCode", "String", "", "Left"));
        data.add(new TAttribute("ActiveFlg", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("DeptCode".equalsIgnoreCase(name)) {
            setDeptCode(value);
            getTObject().setValue("DeptCode", value);
            return;
        }
        if ("ActiveFlg".equalsIgnoreCase(name)) {
            this.setActiveFlg(value);
            getTObject().setValue("ActiveFlg", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            this.setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * 得到科室
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 得到启用注记
     * @return String
     */
    public String getActiveFlg() {
        return activeFlg;
    }

    /**
     * 设置科室
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        setModifySQL(true);
    }

    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 设置启用注记
     * @param activeFlg String
     */
    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }
}
