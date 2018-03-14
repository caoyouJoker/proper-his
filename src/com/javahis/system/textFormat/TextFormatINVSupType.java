package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 供应类别下拉区域</p>
 *
 * <p>Description: 供应类别下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.02.22
 * @version 1.0
 */
public class TextFormatINVSupType
    extends TTextFormat {
    /**
     * 出库方式
     */
    private String packMode; //PACK_MODE
    /**
     * 诊疗包注记
     */
    private String typeFlg; //TYPE_FLG
    /**
     * 得到出库方式
     * @return String
     */
    public String getPackMode() {
        return packMode;
    }

    /**
     * 设置出库方式
     * @param packMode String
     */
    public void setPackMode(String packMode) {
        this.packMode = packMode;
        setModifySQL(true);

    }

    /**
     * 得到诊疗包注记
     * @return String
     */
    public String getTypeFlg() {
        return typeFlg;
    }

    /**
     * 设置诊疗包注记
     * @param typeFlg String
     */
    public void setTypeFlg(String typeFlg) {
        this.typeFlg = typeFlg;
        setModifySQL(true);
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT SUPTYPE_CODE AS ID,SUPTYPE_DESC AS NAME,ENNAME,PY1,PY2 " +
            "   FROM INV_SUPTYPE ";
        String sql1 = " ORDER BY SUPTYPE_CODE ";
        StringBuffer sb = new StringBuffer();
        String packMode = TypeTool.getString(getTagValue(getPackMode()));
        if (packMode != null && packMode.length() > 0)
            sb.append(" PACK_MODE = '" + packMode + "' ");

        String typeFlg = TypeTool.getString(getTagValue(getTypeFlg()));
        if (typeFlg != null && typeFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" TYPE_FLG = '" + typeFlg + "' ");
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
        object.setValue("Tip", "供应类别");
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
        data.add(new TAttribute("PackMode", "String", "", "Left"));
        data.add(new TAttribute("TypeFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("PackMode".equalsIgnoreCase(name)) {
            setPackMode(value);
            getTObject().setValue("PackMode", value);
            return;
        }
        if ("TypeFlg".equalsIgnoreCase(name)) {
            setTypeFlg(value);
            getTObject().setValue("TypeFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
