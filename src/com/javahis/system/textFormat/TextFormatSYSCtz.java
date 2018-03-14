package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;

/**
 * <p>Title: �����������</p>
 *
 * <p>Description: �����������</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author wangl 2012.02.08
 * @version 1.0
 */
public class TextFormatSYSCtz
    extends TTextFormat {
    /**
     * �����ע��
     */
    private String mainCtzFlg; //MAIN_CTZ_FLG
    /**
     * ҽ�����ע��
     */
    private String nhiCtzFlg; //NHI_CTZ_FLG
    /**
     * ��ְ/�Ǿ�����ע��
     */
    private String nhiFlg;
    /**
     * �õ�ҽ�����ע��
     * @return String
     */
    public String getNhiCtzFlg() {
        return nhiCtzFlg;
    }

    /**
     * �õ������ע��
     * @return String
     */
    public String getMainCtzFlg() {
        return mainCtzFlg;
    }
    /**
     * �õ���ְ/�Ǿ�����ע��
     * @return String
     */
    public String getNhiFlg() {
        return nhiFlg;
    }

    /**
     * ����ҽ�����ע��
     * @param nhiCtzFlg String
     */
    public void setNhiCtzFlg(String nhiCtzFlg) {
        this.nhiCtzFlg = nhiCtzFlg;
    }


    /**
     * ���������ע��
     * @param mainCtzFlg String
     */
    public void setMainCtzFlg(String mainCtzFlg) {
        this.mainCtzFlg = mainCtzFlg;
    }
    /**
     * ���ó�ְ/�Ǿ�����ע��
     * @param nhiFlg String
     */
    public void setNhiFlg(String nhiFlg) {
        this.nhiFlg = nhiFlg;
    }

    /**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CTZ_CODE AS ID,CTZ_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_CTZ ";
        String sql1 = " ORDER BY CTZ_CODE ";

        StringBuffer sb = new StringBuffer();

        String mainCtzFlg = TypeTool.getString(getTagValue(getMainCtzFlg()));
        if (mainCtzFlg != null && mainCtzFlg.length() > 0)
            sb.append(" MAIN_CTZ_FLG = '" + mainCtzFlg + "' ");

        String nhiCtzFlg = TypeTool.getString(getTagValue(getNhiCtzFlg()));
        if (nhiCtzFlg != null && nhiCtzFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" NHI_CTZ_FLG = '" + nhiCtzFlg + "' ");
        }
        String nhiFlg = TypeTool.getString(getTagValue(getNhiFlg()));
        if (nhiFlg != null && nhiFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" CTZ_CODE like '" + nhiFlg + "%' ");
        }

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        // System.out.println("�������sql����������"+sql);
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
        object.setValue("Tip", "���");
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
        data.add(new TAttribute("MainCtzFlg", "String", "", "Left"));
        data.add(new TAttribute("NhiCtzFlg", "String", "", "Left"));
        data.add(new TAttribute("NhiFlg", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        if ("MainCtzFlg".equalsIgnoreCase(name)) {
            setMainCtzFlg(value);
            getTObject().setValue("MainCtzFlg", value);
            return;
        }
        if ("NhiCtzFlg".equalsIgnoreCase(name)) {
            setNhiCtzFlg(value);
            getTObject().setValue("NhiCtzFlg", value);
            return;
        }
        if ("NhiFlg".equalsIgnoreCase(name)) {
            setNhiFlg(value);
            getTObject().setValue("NhiFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
