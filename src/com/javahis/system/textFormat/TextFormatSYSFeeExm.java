package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 检验检查分类下拉区域</p>
 *
 * <p>Description: 检验检查分类下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSFeeExm
    extends TTextFormat {
    /**
     * 规则类别
     */
    private String ruleType; //RULE_TYPE
    /**
     * 最小分类注记
     */
    private String detailFlg; //DETAIL_FLG

    /**
     * 第一级
     */
    private String CLASSIFY1;
    /**
     * 第二级
     */
    private String CLASSIFY2;
    /**
     * 第三级
     */
    private String CLASSIFY3;
    /**
     * 第四级
     */
    private String CLASSIFY4;
    /**
     * 分类代码
     */
    private String CATEGORY_CODE;
    /**
     * 得到分类代码
     * @return String
     */
    public String getCATEGORY_CODE() {
        return CATEGORY_CODE;
    }

    /**
     * 设置规则类别
     * @param ruleType String
     */
    public void setRuleType(String ruleType) {
        this.ruleType = ruleType;
    }

    /**
     * 得到规则类别
     * @return String
     */
    public String getRuleType() {
        return ruleType;
    }

    /**
     * 设置最小分类注记
     * @param detailFlg String
     */
    public void setDetailFlg(String detailFlg) {
        this.detailFlg = detailFlg;
    }

    /**
     * 得到最小分类注记
     * @return String
     */
    public String getDetailFlg() {
        return detailFlg;
    }

    /**
     * 得到第一级
     * @return String
     */
    public String getCLASSIFY1() {
        return CLASSIFY1;
    }

    /**
     * 得到第二级
     * @return String
     */
    public String getCLASSIFY2() {
        return CLASSIFY2;
    }

    /**
     * 得到第三级
     * @return String
     */
    public String getCLASSIFY3() {
        return CLASSIFY3;
    }
    /**
     * 得到第四级
     * @return String
     */
    public String getCLASSIFY4() {
        return CLASSIFY4;
    }

    /**
     * 设置分类代码
     * @param CATEGORY_CODE String
     */
    public void setCATEGORY_CODE(String CATEGORY_CODE) {
        this.CATEGORY_CODE = CATEGORY_CODE;
    }

    /**
     * 设置第一级
     * @param CLASSIFY1 String
     */
    public void setCLASSIFY1(String CLASSIFY1) {
        this.CLASSIFY1 = CLASSIFY1;
    }

    /**
     * 设置第二级
     * @param CLASSIFY2 String
     */
    public void setCLASSIFY2(String CLASSIFY2) {
        this.CLASSIFY2 = CLASSIFY2;
    }

    /**
     * 设置第三级
     * @param CLASSIFY3 String
     */
    public void setCLASSIFY3(String CLASSIFY3) {
        this.CLASSIFY3 = CLASSIFY3;
    }
    /**
     * 设置第四级
     * @param CLASSIFY4 String
     */
    public void setCLASSIFY4(String CLASSIFY4) {
        this.CLASSIFY4 = CLASSIFY4;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CATEGORY_CODE AS ID,CATEGORY_CHN_DESC AS NAME,CATEGORY_ENG_DESC AS ENNAME FROM SYS_CATEGORY B,SYS_RULE A " +
            "  WHERE A.RULE_TYPE=B.RULE_TYPE ";
        String sql1 = " ORDER BY CATEGORY_CODE ";

        StringBuffer sb = new StringBuffer();
        String ruleType = TypeTool.getString(getTagValue(getRuleType()));
        if (ruleType != null && ruleType.length() > 0)
            sb.append(" AND B.RULE_TYPE = '" + ruleType + "' ");

        String detailFlg = TypeTool.getString(getTagValue(getDetailFlg()));
        if (detailFlg != null && detailFlg.length() > 0)
            sb.append(" AND B.DETAIL_FLG = '" + detailFlg + "' ");

        String CLASSIFY1 = TypeTool.getString(getTagValue(getCLASSIFY1()));
        if (CLASSIFY1 != null && CLASSIFY1.length() > 0)
            sb.append(" AND LENGTH(B.CATEGORY_CODE)=(SELECT A.CLASSIFY1 FROM SYS_RULE A WHERE A.RULE_TYPE='" +
                      ruleType + "') ");

        String CLASSIFY2 = TypeTool.getString(getTagValue(getCLASSIFY2()));
        if (CLASSIFY2 != null && CLASSIFY2.length() > 0) {
            sb.append(" AND (LENGTH(B.CATEGORY_CODE)=(SELECT (A.CLASSIFY2+A.CLASSIFY1) FROM SYS_RULE A WHERE A.RULE_TYPE='" +
                      ruleType + "')) ");
        }

        String CLASSIFY3 = TypeTool.getString(getTagValue(getCLASSIFY3()));
        if (CLASSIFY3 != null && CLASSIFY3.length() > 0) {
            sb.append(" AND LENGTH(B.CATEGORY_CODE)=(SELECT (A.CLASSIFY2+A.CLASSIFY1+A.CLASSIFY3) FROM SYS_RULE A WHERE A.RULE_TYPE='" +
                      ruleType + "') ");
        }
        String CLASSIFY4 = TypeTool.getString(getTagValue(getCLASSIFY4()));
        if (CLASSIFY4 != null && CLASSIFY4.length() > 0) {
            sb.append(" AND LENGTH(B.CATEGORY_CODE)=(SELECT (A.CLASSIFY2+A.CLASSIFY1+A.CLASSIFY3+CLASSIFY4) FROM SYS_RULE A WHERE A.RULE_TYPE='" +
                      ruleType + "') ");
        }

        String CATEGORY_CODE = TypeTool.getString(getTagValue(getCATEGORY_CODE()));
        if (CATEGORY_CODE != null && CATEGORY_CODE.length() > 0) {
            sb.append(" AND B.CATEGORY_CODE LIKE '" + CATEGORY_CODE + "%' ");
        }
        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//    System.out.println("sql"+sql);
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
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "检验检查分类");
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
        data.add(new TAttribute("CLASSIFY1", "String", "", "Left"));
        data.add(new TAttribute("CLASSIFY2", "String", "", "Left"));
        data.add(new TAttribute("CLASSIFY3", "String", "", "Left"));
        data.add(new TAttribute("CLASSIFY4", "String", "", "Left"));
        data.add(new TAttribute("CATEGORY_CODE", "String", "", "Left"));
        data.add(new TAttribute("RuleType", "String", "", "Left"));
        data.add(new TAttribute("DetailFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("RuleType".equalsIgnoreCase(name)) {
            setRuleType(value);
            getTObject().setValue("RuleType", value);
            return;
        }
        if ("DetailFlg".equalsIgnoreCase(name)) {
            setDetailFlg(value);
            getTObject().setValue("DetailFlg", value);
            return;
        }
        if ("CLASSIFY1".equalsIgnoreCase(name)) {
            setCLASSIFY1(value);
            getTObject().setValue("CLASSIFY1", value);
            return;
        }
        if ("CLASSIFY2".equalsIgnoreCase(name)) {
            setCLASSIFY2(value);
            getTObject().setValue("CLASSIFY2", value);
            return;
        }
        if ("CLASSIFY3".equalsIgnoreCase(name)) {
            setCLASSIFY3(value);
            getTObject().setValue("CLASSIFY3", value);
            return;
        }
        if ("CLASSIFY4".equalsIgnoreCase(name)) {
            setCLASSIFY4(value);
            getTObject().setValue("CLASSIFY4", value);
            return;
        }
        if ("CATEGORY_CODE".equalsIgnoreCase(name)) {
            setCATEGORY_CODE(value);
            getTObject().setValue("CATEGORY_CODE", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
