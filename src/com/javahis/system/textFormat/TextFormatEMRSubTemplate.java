package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: </p>
 *
 * <p>Description: EMR 子模版下拉列表</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatEMRSubTemplate
    extends TTextFormat {

    private String mainCategory;
    /**
     * 执行Module动作
     */
    public String getPopupMenuSQL() {
        StringBuffer sb = new StringBuffer();
        //PARENT_CLASS_CODE = ROOT 为主分类
        sb.append("SELECT CLASS_CODE AS ID,PHRASE_CODE AS NAME,PY1,PY2 FROM OPD_COMTEMPLATE_PHRASE WHERE 1=1 AND LEAF_FLG='Y' AND MAIN_FLG='N'");
        String mainCategoryTemp = TypeTool.getString(getTagValue(
            getMainCategory()));
        //System.out.println("==mainCategoryTemp=="+mainCategoryTemp);
        if (mainCategoryTemp != null && mainCategoryTemp.length() > 0) {
            sb.append(" AND MAIN_CLASS_CODE = '" + mainCategoryTemp + "' ");
        }
        sb.append(" ORDER BY SEQ");
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
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "结构化病历模版主分类");
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
        data.add(new TAttribute("MainCategory", "String", "", "Left"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("MainCategory".equalsIgnoreCase(name)) {
            setMainCategory(value);
            getTObject().setValue("MainCategory", value);
            return;
        }

        super.setAttribute(name, value);
    }

    /**
     * 设置模版主分类
     * @param mainCategory String
     */
    public void setMainCategory(String mainCategory) {
        this.mainCategory = mainCategory;
        setModifySQL(true);
    }

    /**
     * 获得模版主分类
     * @return String
     */
    public String getMainCategory() {
        return mainCategory;
    }


}
