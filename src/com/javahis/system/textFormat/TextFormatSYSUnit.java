package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 计量单位下拉区域</p>
 *
 * <p>Description: 计量单位下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSUnit
    extends TTextFormat {
    /**
     * 药品用量单位注记
     */
    private String mediFlg;
    /**
     * 处置单位注记
     */
    private String disposeFlg;
    /**
     * 整包装注记
     */
    private String packageFlg;
    /**
     * 其他注记
     */
    private String otherFlg;
    /**
     * 设置药品用量单位注记
     * @param mediFlg String
     */
    public void setMediFlg(String mediFlg) {
        this.mediFlg = mediFlg;
    }

    /**
     * 得到药品用量单位注记
     * @return String
     */
    public String getMediFlg() {
        return mediFlg;
    }

    /**
     * 设置处置单位注记
     * @param disposeFlg String
     */
    public void setDisposeFlg(String disposeFlg) {
        this.disposeFlg = disposeFlg;
    }

    /**
     * 得到处置单位注记
     * @return String
     */
    public String getDisposeFlg() {
        return disposeFlg;
    }

    /**
     * 设置整包装注记
     * @param packageFlg String
     */
    public void setPackageFlg(String packageFlg) {
        this.packageFlg = packageFlg;
    }

    /**
     * 得到整包装注记
     * @return String
     */
    public String getPackageFlg() {
        return packageFlg;
    }

    /**
     * 设置其他注记
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
    }

    /**
     * 得到其他注记
     * @return String
     */
    public String getOtherFlg() {
        return otherFlg;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT UNIT_CODE AS ID,UNIT_CHN_DESC AS NAME,UNIT_ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_UNIT ";
        String sql1 = " ORDER BY UNIT_CODE ";

        StringBuffer sb = new StringBuffer();

        String mediFlg = TypeTool.getString(getTagValue(getMediFlg()));
        if (mediFlg != null && mediFlg.length() > 0)
            sb.append(" MEDI_FLG = '" + mediFlg + "' ");

        String disposeFlg = TypeTool.getString(getTagValue(getDisposeFlg()));
        if (disposeFlg != null && disposeFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DISPOSE_FLG = '" + disposeFlg + "' ");
        }

        String packageFlg = TypeTool.getString(getTagValue(getPackageFlg()));
        if (packageFlg != null && packageFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" PACKAGE_FLG = '" + packageFlg + "' ");
        }

        String otherFlg = TypeTool.getString(getTagValue(getOtherFlg()));
        if (otherFlg != null && otherFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OTHER_FLG = '" + otherFlg + "' ");
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
        object.setValue("Tip", "计量单位");
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
        data.add(new TAttribute("MediFlg", "String", "", "Left"));
        data.add(new TAttribute("DisposeFlg", "String", "", "Left"));
        data.add(new TAttribute("PackageFlg", "String", "", "Left"));
        data.add(new TAttribute("OtherFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("MediFlg".equalsIgnoreCase(name)) {
            setMediFlg(value);
            getTObject().setValue("MediFlg", value);
            return;
        }
        if ("DisposeFlg".equalsIgnoreCase(name)) {
            setDisposeFlg(value);
            getTObject().setValue("DisposeFlg", value);
            return;
        }
        if ("PackageFlg".equalsIgnoreCase(name)) {
            setPackageFlg(value);
            getTObject().setValue("PackageFlg", value);
            return;
        }
        if ("OtherFlg".equalsIgnoreCase(name)) {
            setOtherFlg(value);
            getTObject().setValue("OtherFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
