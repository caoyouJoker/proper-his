package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 处方点评问题代码下拉区域</p>
 *
 * <p>Description: 处方点评问题代码下拉区域</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author zhangp 2012.10.20
 * @version 1.0
 */

public class TextFormatPESQuestionCode extends TTextFormat {
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
    	String sql =
            " SELECT ID,CHN_DESC AS NAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID = 'PES_QUESTION_CODE' ORDER BY ID ";
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
        object.setValue("PopupMenuHeader", "代码,50;名称,400;拼音,100");
        object.setValue("PopupMenuWidth", "450");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "问题代码");
        object.setValue("ShowColumnList", "ID;NAME;PY1");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;PY1,1");
        setLanguageMap("NAME");
        setPopupMenuEnHeader("Code;Name");
    }
    
    /**
     * 显示区域列明
     * @return String
     */
    public String getPopupMenuHeader() {
        return "代码,50;名称,400";
    }
    
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
    	 data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
         data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
         data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        super.setAttribute(name, value);
    }



}
