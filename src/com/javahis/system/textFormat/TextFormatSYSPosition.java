package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 职别下拉区域</p>
 *
 * <p>Description: 职别下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.04.12
 * @version 1.0
 */
public class TextFormatSYSPosition
    extends TTextFormat {
    /**
     * 职别类型
     */
    private String posType;
    /**
     * 得到职别
     * @return String
     */
    public String getPosType() {
        return posType;
    }

    /**
     * 设置职别
     * @param posType String
     */
    public void setPosType(String posType) {
        this.posType = posType;
        setModifySQL(true);
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT POS_CODE AS ID,POS_CHN_DESC AS NAME,POS_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_POSITION ";
        String sql1 = " ORDER BY POS_CODE,SEQ";
        StringBuffer sb = new StringBuffer();
        String posTypeTemp = TypeTool.getString(getTagValue(getPosType()));
        if (posTypeTemp != null && posTypeTemp.length() > 0)
            sb.append(" POS_TYPE = '" + posTypeTemp + "' ");
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "职别");
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
        data.add(new TAttribute("PosType", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("PosType".equalsIgnoreCase(name)) {
            setPosType(value);
            getTObject().setValue("PosType", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
