package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 点评期间下拉区域</p>
 *
 * <p>Description: 点评期间下拉区域</p>
 *
 * <p>Company: Bluecore</p>
 *
 * @author zhangp 2012.10.15
 * @version 1.0
 */

public class TextFormatPESIpdNo extends TTextFormat {
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
    	String sql =
            "SELECT PES_NO ID FROM PES_IPDRESULT ORDER BY PES_NO";
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
        object.setValue("PopupMenuHeader", "点评期间,200");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "住院点评期间");
        object.setValue("ShowColumnList", "ID;NAME;PY1");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1");
        setLanguageMap("NAME");
        setPopupMenuEnHeader("Code;Name");
    }
    
    /**
     * 显示区域列明
     * @return String
     */
    public String getPopupMenuHeader() {
        return "点评期间,200";
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
