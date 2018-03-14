package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: ���������������</p>
 *
 * <p>Description: ���������������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2010.01.18
 * @version 1.0
 */
public class TextFormatREGQuegroup
    extends TTextFormat {
    /**
     * VIPע��
     */
    private String vipFlg;
    /**
     * �õ�VIPע��
     * @return String
     */
    public String getVipFlg() {
        return vipFlg;
    }

    /**
     * ����VIPע��
     * @param vipFlg String
     */
    public void setVipFlg(String vipFlg) {
        this.vipFlg = vipFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT QUEGROUP_CODE AS ID,QUEGROUP_DESC AS NAME ,ENNAME,PY1,PY2 " +
            "   FROM REG_QUEGROUP ";

        String sqlEnd = " ORDER BY QUEGROUP_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String vipFlg = TypeTool.getString(getTagValue(getVipFlg()));
        if (vipFlg != null && vipFlg.length() > 0)
            sb.append(" VIP_FLG = '" + vipFlg + "' ");

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sqlEnd;
        else
            sql = sql + sqlEnd;
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
        object.setValue("Tip", "�������");
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
        data.add(new TAttribute("VipFlg", "String", "", "Left"));
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("VipFlg".equalsIgnoreCase(name)) {
            setVipFlg(value);
            getTObject().setValue("VipFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }


}
