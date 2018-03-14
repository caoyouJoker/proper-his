package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;

/**
 * <p>Title: 频次下拉区域</p>
 *
 * <p>Description: 频次下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatSYSPhaFreq
    extends TTextFormat {
    /**
     * 西医使用注记
     */
    private String wesmedFlg; //WESMED_FLG
    /**
     * 立即用药注记
     */
    private String statFlg; //STAT_FLG
    /**
     * 设置西医使用注记
     * @param wesmedFlg String
     */
    public void setWesmedFlg(String wesmedFlg) {

        this.wesmedFlg = wesmedFlg;
        setModifySQL(true);
    }

    /**
     * 设置立即用药注记
     * @param statFlg String
     */
    public void setStatFlg(String statFlg) {
        this.statFlg = statFlg;
    }

    /**
     * 得到西医使用注记
     * @return String
     */
    public String getWesmedFlg() {
        return wesmedFlg;
    }

    /**
     * 得到立即用药注记
     * @return String
     */
    public String getStatFlg() {
        return statFlg;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT FREQ_CODE AS ID,FREQ_CHN_DESC AS NAME,FREQ_ENG_DESC AS ENNAME,PY1,PY2  FROM SYS_PHAFREQ ";
        String sql1 = " ORDER BY SEQ,FREQ_CODE ";

        StringBuffer sb = new StringBuffer();

        String wesmedFlgTemp = TypeTool.getString(getTagValue(getWesmedFlg()));
        if (wesmedFlgTemp != null && wesmedFlgTemp.length() > 0)
            sb.append(" WESMED_FLG = '" + wesmedFlgTemp + "' ");

        String statFlgTemp = TypeTool.getString(getTagValue(getStatFlg()));
        if (statFlgTemp != null && statFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" STAT_FLG = '" + statFlgTemp + "' ");
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
        object.setValue("Tip", "频次");
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
        data.add(new TAttribute("WesmedFlg", "String", "", "Left"));
        data.add(new TAttribute("StatFlg", "String", "", "Left"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("WesmedFlg".equalsIgnoreCase(name)) {
            setWesmedFlg(value);
            getTObject().setValue("WesmedFlg", value);
            return;
        }
        if ("StatFlg".equalsIgnoreCase(name)) {
            setStatFlg(value);
            getTObject().setValue("StatFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
