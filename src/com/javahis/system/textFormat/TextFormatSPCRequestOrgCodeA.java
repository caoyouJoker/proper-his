package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;

/**
 * 
 * <p>
 * Title:物联网药库-药房请领-A部门下拉列表
 * </p>
 * 
 * <p>
 * Description: 物联网药库-药房请领-A部门下拉列表
 * </p>
 * 
 * <p>
 * Copyright (c) ProperSoft 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author liyh 20121204
 * @version 1.0
 */

public class TextFormatSPCRequestOrgCodeA extends TTextFormat {

	/**
	 * 执行Module动作
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		String sql = "  SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME,PY1,PY2 FROM IND_ORG WHERE ORG_TYPE='A' ";
		return sql;
	}

	/**
	 * 新建对象的初始值
	 * 
	 * @param object
	 *            TObject
	 */
	public void createInit(TObject object) {
		if (object == null)
			return;
		object.setValue("Width", "81");
		object.setValue("Height", "23");
		object.setValue("Text", "");
		object.setValue("HorizontalAlignment", "2");
		object.setValue("PopupMenuHeader", "编码,100;名称,100");
		object.setValue("PopupMenuWidth", "300");
		object.setValue("PopupMenuHeight", "300");
		object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
		object.setValue("FormatType", "combo");
		object.setValue("ShowDownButton", "Y");
		object.setValue("Tip", "药库");
		object.setValue("ShowColumnList", "ID;NAME");
	}

	public void onInit() {
		super.onInit();
		setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
		setLanguageMap("NAME|ENNAME");
		setPopupMenuEnHeader("Code;Name");
	}

	/**
	 * 显示区域列名
	 * 
	 * @return String
	 */
	public String getPopupMenuHeader() {

		return "编码,100;名称,200";
	}

	/**
	 * 增加扩展属性
	 * 
	 * @param data
	 *            TAttributeList
	 */
	public void getEnlargeAttributes(TAttributeList data) {
		data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
		data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
		data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
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

		super.setAttribute(name, value);
	}

}
