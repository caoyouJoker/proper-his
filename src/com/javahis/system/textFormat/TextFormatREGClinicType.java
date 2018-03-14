package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 * <p>Title: 号别下拉区域</p>
 *
 * <p>Description: 号别下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatREGClinicType
    extends TTextFormat {
    /**
     * 门急别
     */
    private String admType;
    /**
     * 专家诊注记
     */
    private String profFlg;
    /**
     * 得到门急别
     * @return String
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * 得到专家诊注记
     * @return String
     */
    public String getProfFlg() {
        return profFlg;
    }

    /**
     * 设置门急别
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }

    /**
     * 设置专家诊注记
     * @param profFlg String
     */
    public void setProfFlg(String profFlg) {
        this.profFlg = profFlg;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CLINICTYPE_CODE AS ID,CLINICTYPE_DESC AS NAME ,PY1,PY2 " +
            "   FROM REG_CLINICTYPE ";

        String sqlEnd = " ORDER BY CLINICTYPE_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0)
            sb.append(" ADM_TYPE = '" + admType + "' ");

        String profFlg = TypeTool.getString(getTagValue(getProfFlg()));
        if (profFlg != null && profFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" PROF_FLG = '" + profFlg + "' ");
        }

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sqlEnd;
        else
            sql = sql + sqlEnd;
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
        object.setValue("Tip", "号别");
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
        data.add(new TAttribute("AdmType", "String", "", "Left"));
        data.add(new TAttribute("ProfFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("AdmType".equalsIgnoreCase(name)) {
            setAdmType(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        if ("ProfFlg".equalsIgnoreCase(name)) {
            setProfFlg(value);
            getTObject().setValue("ProfFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }

}
