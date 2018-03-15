package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

public class TextFormatSmsState extends TTextFormat {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     * ���ܷ������
     */
    private String smsStateCode = "SMS_STATE_CODE";
    /**
     * ���õ�λ����
     * @param companyCode String
     */
    public void setSmsStateCode(String smsStateCode) {
        this.smsStateCode = smsStateCode;
        setModifySQL(true);
    }

    /**
     * �õ���λ����
     * @return String
     */
    public String getSmsStateCode() {
        return smsStateCode;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,TYPE,DATA FROM SYS_DICTIONARY  ";
        StringBuffer sb = new StringBuffer();
        String companyCodeTemp = TypeTool.getString(getTagValue(getSmsStateCode()));
        if (companyCodeTemp != null && companyCodeTemp.length() > 0)
            sb.append(" GROUP_ID = '" + companyCodeTemp + "' ");
        String sql1 = " ORDER BY SEQ,ID ";
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        return sql;
    }

    /**
     * �½�����ĳ�ʼֵ
     * @param object TObject
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
        object.setValue("Tip", "����״̬����");
        object.setValue("ShowColumnList", "ID;NAME");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * ��ʾ״̬��������
     * @return String
     */
    public String getPopupMenuHeader() {

        return "����,100;����,200";
    }

    /**
     * ������չ����
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("CompanyCode", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
        if ("SmsStateCode".equalsIgnoreCase(name)) {
            setSmsStateCode(value);
           // getTObject().setValue("CompanyCode", value);
            return;
        }

        super.setAttribute(name, value);
    }

}