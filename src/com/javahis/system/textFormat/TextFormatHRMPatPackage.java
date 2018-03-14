package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p> Title: 健检人员套餐组合下拉区域  </p>
 * 
 * <p>Description: 健检人员套餐组合下拉区域  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: bluecore </p>
 * 
 * @author wanglong 20130508
 * @version 1.0
 */
public class TextFormatHRMPatPackage extends TTextFormat {

    /**
     * 合同代码
     */
    private String contractCode;

    /**
     * 过滤注记
     */
    private String filterFlg;
    
    /**
     * 获得合同代码
     * 
     * @param contractCode
     *            String
     */
    public String getContractCode() {
        return contractCode;
    }

    /**
     * 设置合同代码
     * 
     * @param contractCode
     *            String
     */
    public void setContractCode(String contractCode) {
        this.contractCode = contractCode;
        setModifySQL(true);
    }
    
    /**
     * 获得过滤注记
     * @return
     */
    public String getFilterFlg() {
        return filterFlg;
    }

    /**
     * 设置过滤注记
     * @param filterFlg
     */
    public void setFilterFlg(String filterFlg) {
        this.filterFlg = filterFlg;
    }

    /**
     * 执行Module动作
     * 
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
                " SELECT A.SEQ_NO, A.PAT_NAME, C.CHN_DESC SEX_DESC, B.PACKAGE_DESC, A.MR_NO "
                        + "  FROM HRM_CONTRACTD A, HRM_PACKAGEM B, SYS_DICTIONARY C, HRM_PATADM D "
                        + " WHERE A.PACKAGE_CODE = B.PACKAGE_CODE "
                        + "   AND C.GROUP_ID = 'SYS_SEX' AND A.SEX_CODE = C.ID "
                        + "   AND A.MR_NO = D.MR_NO(+)   AND A.CONTRACT_CODE = D.CONTRACT_CODE(+) "
                        + "   AND A.CONTRACT_CODE = '#'  AND D.CASE_NO IS # NULL "
                        + "ORDER BY A.SEQ_NO";
        String contractCode = TypeTool.getString(getTagValue(getContractCode()));
        sql = sql.replaceFirst("#", contractCode);
        String filterFlg = TypeTool.getString(getTagValue(getFilterFlg()));
        if (TypeTool.getBoolean(filterFlg)) {
            sql = sql.replaceFirst("#", "NOT");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", filterFlg.toUpperCase());
        // System.out.println("-------------下拉框sql---------------" + sql);
        return sql;
    }

    /**
     * 新建对象的初始值
     * 
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "序号,80;姓名,120;性别,70;套餐,160");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "SEQ_NO,1;PAT_NAME,1;SEX_DESC,1;PACKAGE_DESC,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "人员套餐组合");
        object.setValue("ShowColumnList", "PAT_NAME;PACKAGE_DESC");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("SEQ_NO,1;PAT_NAME,1;SEX_DESC,1;PACKAGE_DESC,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("No.;Name;Sex;Package Name");
    }

    /**
     * 显示区域列名
     * 
     * @return String
     */
    // public String getPopupMenuHeader() {
    // return "代码,100;名称,200";
    // }

    /**
     * 增加扩展属性
     * 
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("contractCode", "String", "", "Left"));
        data.add(new TAttribute("filterFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * 
     * @param name
     *            String 属性名
     * @param value
     *            String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("contractCode".equalsIgnoreCase(name)) {
            setContractCode(value);
            getTObject().setValue("contractCode", value);
            return;
        }
        if ("filterFlg".equalsIgnoreCase(name)) {
            setContractCode(value);
            getTObject().setValue("filterFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
