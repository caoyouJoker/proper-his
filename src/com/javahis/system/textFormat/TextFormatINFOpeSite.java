package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
/**
 * <p>
 * Title:��Ⱦ������λ�����˵�
 * </p>
 * 
 * <p>
 * Description: ��Ⱦ������λ�����˵�
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author lij 20170505
 * @version 1.0
 */
public class TextFormatINFOpeSite extends TTextFormat {
	/**
	 * ��ʼ������
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
		object.setValue("PopupMenuFilter", "ID,1;NAME,1");
		object.setValue("FormatType", "combo");
		object.setValue("ShowDownButton", "Y");
		object.setValue("Tip", "��Ⱦ������λ");
		object.setValue("ShowColumnList", "ID;NAME");
	}

	public void onInit() {
		super.onInit();
		setPopupMenuFilter("ID,1;NAME,3");
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
	
	
	
	@Override
	public String getPopupMenuSQL() {
		String sql = "SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID = 'INF_OPESITE'";
		return sql;
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