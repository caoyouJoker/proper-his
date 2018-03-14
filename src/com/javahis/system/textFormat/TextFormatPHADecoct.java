package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 煎药室下拉区域</p>
 *
 * <p>Description: 煎药室下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.05.27
 * @version 1.0
 */
public class TextFormatPHADecoct
    extends TTextFormat {
    /**
     * 最小科室注记
     */
    private String orgCode;
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql = "";
        String sql1 =
            " SELECT DEPT_CODE AS ID,DEPT_CHN_DESC AS NAME,DEPT_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_DEPT " +
            "  WHERE DEPT_GRADE = '3' " +
            "    AND CLASSIFY = '4'  " +
            "    AND ACTIVE_FLG = 'Y' " +
            "  ORDER BY DEPT_CODE,SEQ";
        String orgCodeTemp = TypeTool.getString(getTagValue(getOrgCode()));
        String sql2 =
            " SELECT A.DEPT_CODE AS ID, A.DEPT_CHN_DESC AS NAME,A.DEPT_ENG_DESC AS ENNAME,A.PY1,A.PY2 " +
            "   FROM SYS_DEPT A,IND_ORG B " +
            "  WHERE A.DEPT_CODE = B.DECOCT_CODE " +
            "    AND A.ACTIVE_FLG = 'Y' " +
            "    AND B.ORG_CODE = '" + orgCodeTemp + "' "+
            " ORDER BY A.DEPT_CODE,A.SEQ ";

        if (orgCodeTemp.length() <= 0)
            sql = sql1;
        else
            sql = sql2;
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
        object.setValue("Tip", "煎药室");
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
        data.add(new TAttribute("OrgCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("OrgCode".equalsIgnoreCase(name)) {
            setOrgCode(value);
            getTObject().setValue("OrgCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    public String getOrgCode() {
        return orgCode;
    }


    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
        setModifySQL(true);
    }

}
