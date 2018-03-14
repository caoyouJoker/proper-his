package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 供应厂商下拉区域</p>
 *
 * <p>Description: 供应厂商下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.09.11
 * @version 1.0
 */
public class TextFormatSYSSupplier
    extends TTextFormat {
    /**
     * 停止采购注记
     */
    private String supStopFlg;
    /**
     * 药品供货商注记
     */
    private String phaFlg;
    /**
     * 低值耗材供货商注记
     */
    private String matFlg;
    /**
     * 设备供货商注记
     */
    private String devFlg;
    /**
     * 其它供应厂商注记
     */
    private String otherFlg;
    /**
     * 得到设备供货商注记
     * @return String
     */
    public String getDevFlg() {
        return devFlg;
    }

    /**
     * 得到低值耗材供货商注记
     * @return String
     */
    public String getMatFlg() {
        return matFlg;
    }

    /**
     * 得到其它供应厂商注记
     * @return String
     */
    public String getOtherFlg() {
        return otherFlg;
    }

    /**
     * 得到药品供货商注记
     * @return String
     */
    public String getPhaFlg() {
        return phaFlg;
    }

    /**
     * 得到停止采购注记
     * @return String
     */
    public String getSupStopFlg() {
        return supStopFlg;
    }

    /**
     * 设置设备供货商注记
     * @param devFlg String
     */
    public void setDevFlg(String devFlg) {
        this.devFlg = devFlg;
        setModifySQL(true);
    }

    /**
     * 设置低值耗材供货商注记
     * @param matFlg String
     */
    public void setMatFlg(String matFlg) {
        this.matFlg = matFlg;
        setModifySQL(true);
    }

    /**
     * 设置其它供应厂商注记
     * @param otherFlg String
     */
    public void setOtherFlg(String otherFlg) {
        this.otherFlg = otherFlg;
        setModifySQL(true);
    }

    /**
     * 设置药品供货商注记
     * @param phaFlg String
     */
    public void setPhaFlg(String phaFlg) {
        this.phaFlg = phaFlg;
        setModifySQL(true);
    }

    /**
     * 设置停止采购注记
     * @param supStopFlg String
     */
    public void setSupStopFlg(String supStopFlg) {
        this.supStopFlg = supStopFlg;
        setModifySQL(true);
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT SUP_CODE AS ID,SUP_ABS_DESC AS NAME,SUP_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_SUPPLIER";
        String sql1 = " ORDER BY SEQ,SUP_CODE";

        StringBuffer sb = new StringBuffer();

        String supStopFlg = TypeTool.getString(getTagValue(getSupStopFlg()));
        if (supStopFlg != null && supStopFlg.length() > 0)
            sb.append(" SUP_STOP_FLG = '" + supStopFlg + "' ");

        String phaFlg = TypeTool.getString(getTagValue(getPhaFlg()));
        if (phaFlg != null && phaFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" PHA_FLG = '" + phaFlg + "' ");
        }

        String matFlg = TypeTool.getString(getTagValue(getMatFlg()));
        if (matFlg != null && matFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" MAT_FLG = '" + matFlg + "' ");
        }

        String devFlg = TypeTool.getString(getTagValue(getDevFlg()));
        if (devFlg != null && devFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DEV_FLG = '" + devFlg + "' ");
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
//        System.out.println("sql" + sql);
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
        object.setValue("Tip", "供应厂商");
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
        data.add(new TAttribute("SupStopFlg", "String", "", "Left"));
        data.add(new TAttribute("PhaFlg", "String", "", "Left"));
        data.add(new TAttribute("MatFlg", "String", "", "Left"));
        data.add(new TAttribute("DevFlg", "String", "", "Left"));
        data.add(new TAttribute("OtherFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("SupStopFlg".equalsIgnoreCase(name)) {
            setSupStopFlg(value);
            getTObject().setValue("SupStopFlg", value);
            return;
        }
        if ("PhaFlg".equalsIgnoreCase(name)) {
            setPhaFlg(value);
            getTObject().setValue("PhaFlg", value);
            return;
        }
        if ("MatFlg".equalsIgnoreCase(name)) {
            setMatFlg(value);
            getTObject().setValue("MatFlg", value);
            return;
        }
        if ("DevFlg".equalsIgnoreCase(name)) {
            setDevFlg(value);
            getTObject().setValue("DevFlg", value);
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
