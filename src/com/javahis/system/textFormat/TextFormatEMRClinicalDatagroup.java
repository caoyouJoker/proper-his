package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;

/**
 * <p>Title: 结构化病历数据原组下拉区域</p>
 *
 * <p>Description: 结构化病历数据原组下拉区域</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatEMRClinicalDatagroup
    extends TTextFormat {
    /**
     * 元件类别
     */
    private String componentType;
    /**
     * 数据元组大分类
     */
    private String groupCode; //GROUP_CODE

    /**
     * 文档类型
     */
    private String documentType;

    /**
     * 执行Module动作
     */
    public String getPopupMenuSQL() {
        /**String sql =
            " SELECT DATA_CODE AS ID,DATA_DESC AS NAME,PY1,PY2 FROM EMR_CLINICAL_DATAGROUP ";

        String sql1 = " ORDER BY DATA_CODE ";
        StringBuffer sb = new StringBuffer();
        String componentTypeTemp = TypeTool.getString(getTagValue(
            getComponentType()));
        if (componentTypeTemp != null && componentTypeTemp.length() > 0)
            sb.append(" COMPONENT_TYPE = '" + componentTypeTemp + "' ");
        String groupCodeTemp = TypeTool.getString(getTagValue(getGroupCode()));
        if (groupCodeTemp != null && groupCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" GROUP_CODE = '" + groupCodeTemp + "' ");
        }

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;*/
        StringBuffer sb = new StringBuffer();
       //文档类型条件
       String documentType = TypeTool.getString(getTagValue(getDocumentType()));
       //存在文档类型情况
       if (documentType != null && documentType.length() > 0) {
           sb.append("SELECT a.DATA_CODE AS ID,a.DATA_DESC AS NAME,PY1,PY2 FROM EMR_CLINICAL_DATAGROUP a,EMR_XSD b");
           sb.append(" WHERE a.GROUP_CODE=b.GROUP_CODE and a.DATA_CODE=b.DATA_CODE");
           sb.append(" AND DOCUMENT_TYPE = '" + documentType + "' ");
       //没文档类型，和原来一样全部数据;
       }else{
           sb.append("SELECT DATA_CODE AS ID,DATA_DESC AS NAME,PY1,PY2 FROM EMR_CLINICAL_DATAGROUP a WHERE 1=1");
       }

       String componentTypeTemp = TypeTool.getString(getTagValue(
            getComponentType()));
       if (componentTypeTemp != null && componentTypeTemp.length() > 0){
           sb.append(" AND a.COMPONENT_TYPE = '" + componentTypeTemp + "' ");

       }

       String groupCodeTemp = TypeTool.getString(getTagValue(getGroupCode()));
        if (groupCodeTemp != null && groupCodeTemp.length() > 0) {
            sb.append(" AND a.GROUP_CODE = '" + groupCodeTemp + "' ");
        }

        sb.append(" ORDER BY a.DATA_CODE");
        //System.out.println("==========sql TextFormatEMRClinicalDatagroup==========="+sb.toString());
        return sb.toString();
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
        object.setValue("Tip", "结构化病历数据原组");
        object.setValue("ShowColumnList", "ID;NAME");

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
        data.add(new TAttribute("ComponentType", "String", "", "Left"));
        data.add(new TAttribute("GroupCode", "String", "", "Left"));
        data.add(new TAttribute("DocumentType", "String", "", "Left"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("ComponentType".equalsIgnoreCase(name)) {
            setComponentType(value);
            getTObject().setValue("ComponentType", value);
            return;
        }
        if ("GroupCode".equalsIgnoreCase(name)) {
            setGroupCode(value);
            getTObject().setValue("GroupCode", value);
            return;
        }

        if ("DocumentType".equalsIgnoreCase(name)) {
           setDocumentType(value);
           getTObject().setValue("DocumentType", value);
           return;
       }

        super.setAttribute(name, value);
    }

    /**
     * 得到元件类别
     * @return String
     */
    public String getComponentType() {
        return componentType;
    }

    /**
     * 得到数据元组大分类
     * @return String
     */
    public String getGroupCode() {
        return groupCode;
    }

    /**
     * 设置元件类别
     * @param componentType String
     */
    public void setComponentType(String componentType) {
        this.componentType = componentType;
        setModifySQL(true);
    }

    /**
     * 设置数据元组大分类
     * @param groupCode String
     */
    public void setGroupCode(String groupCode) {
        this.groupCode = groupCode;
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
