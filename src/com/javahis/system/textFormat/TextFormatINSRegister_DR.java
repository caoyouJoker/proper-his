package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

/**
 * <p>
 * Title:ҽ���Ǽ�ҽ����������
 * </p>
 * 
 * <p>
 * Description:ҽ���Ǽ�ҽ����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) ProperSoft 2011
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author pangb 2012.04.10
 * @version 4.0
 */
public class TextFormatINSRegister_DR extends TTextFormat{
	/**
	 * ִ��Module����
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		String sql =
            " SELECT USER_ID AS ID,USER_NAME AS NAME,USER_ENG_NAME AS ENNAME,PY1,PY2 " +
            "FROM SYS_OPERATOR WHERE DR_QUALIFY_CODE IS NOT NULL OR DR_QUALIFY_CODE <>'' ORDER BY USER_ID";
		return sql;
	}

	/**
	 * �½�����ĳ�ʼֵ
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
		object.setValue("PopupMenuHeader", "����,100;����,100");
		object.setValue("PopupMenuWidth", "300");
		object.setValue("PopupMenuHeight", "300");
		object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
		object.setValue("FormatType", "combo");
		object.setValue("ShowDownButton", "Y");
		object.setValue("Tip", "ҽ���Ǽ�ҽ��");
		object.setValue("ShowColumnList", "ID;NAME");
	}

	public void onInit() {
		super.onInit();
		setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
		setLanguageMap("NAME|ENNAME");
		setPopupMenuEnHeader("Code;Name");
	}

	/**
	 * ��ʾ��������
	 * 
	 * @return String
	 */
	public String getPopupMenuHeader() {

		return "����,100;����,200";
	}

	/**
	 * ������չ����
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
	 * ��������
	 * 
	 * @param name
	 *            String ������
	 * @param value
	 *            String ����ֵ
	 */
	public void setAttribute(String name, String value) {
		super.setAttribute(name, value);
	}
}