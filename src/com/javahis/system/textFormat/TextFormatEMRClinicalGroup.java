package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: EMR 分类下拉框</p>
 *
 * <p>Description: EMR 分类下拉框</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: </p>
 *
 * @author li.xiang790130@gmail.com  2011.06.06
 * @version 1.0
 */
public class TextFormatEMRClinicalGroup
    extends TTextFormat {

    /**
     * 文档类型
     */
    private String documentType; //


    public TextFormatEMRClinicalGroup() {
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        StringBuffer sb = new StringBuffer();
        //文档类型条件
        String documentType = TypeTool.getString(getTagValue(getDocumentType()));
        if (documentType != null && documentType.length() > 0) {
           sb.append(" SELECT a.GROUP_CODE AS ID,a.GROUP_DESC AS NAME from EMR_CLINICAL_DATAGROUP a,EMR_XSD b WHERE  a.GROUP_CODe=b.GROUP_CODE AND a.DATA_CODE=b.DATA_CODE");
           sb.append(" AND DOCUMENT_TYPE = '" + documentType + "' ");

       }else{
           sb.append("SELECT GROUP_CODE AS ID,GROUP_DESC AS NAME FROM EMR_CLINICAL_DATAGROUP a");
       }

        String sql1 = " GROUP BY a.GROUP_CODE,a.GROUP_DESC ORDER BY a.GROUP_CODE ";
        sb.append(sql1);
        //System.out.println("=====sql========"+sb.toString());
        return sb.toString();

    }

    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) {
            return;
        }
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "代码,100;名称,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "病历模版数据分类");
        object.setValue("ShowColumnList", "ID;NAME");

    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,1");
        setLanguageMap("NAME");
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
        data.add(new TAttribute("DocumentType", "String", "", "Left"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("DocumentType".equalsIgnoreCase(name)) {
            setDocumentType(value);
            getTObject().setValue("DocumentType", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * 设置文档类型
     * @param documentType String
     */
    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

    /**
     * 得到文档类型
     * @param documentType String
     */
    public String getDocumentType() {
        return documentType;
    }


}
