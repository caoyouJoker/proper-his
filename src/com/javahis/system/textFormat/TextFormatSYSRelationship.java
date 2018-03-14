package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;

import jdo.sys.SystemTool;

import com.dongyang.ui.edit.TAttributeList;

/**
 *
 * <p>Title: ������ϵ��������</p>
 *
 * <p>Description: ������ϵ��������</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TextFormatSYSRelationship
    extends TTextFormat {
    /**
     * ִ��Module����
     * @return String
     */
	 // modified by WangQing 20170315
	 
    //    public String getPopupMenuSQL() {
//        String sql =
//            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' ORDER BY SEQ,ID ";
//        return sql;
//    }

public String getPopupMenuSQL() {
        String sql =
            " SELECT ID,CHN_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2 FROM SYS_DICTIONARY WHERE GROUP_ID='SYS_RELATIONSHIP' "
        		+ "AND EFF_START_DATE <= to_date ('"
                + this.dateToString((Date) SystemTool.getInstance().getDate())
        		+ "', 'YYYY-MM-DD HH24:MI:SS') "
            	+"AND EFF_END_DATE >= to_date ('"
        		+ this.dateToString((Date) SystemTool.getInstance().getDate())
            	+ "', 'YYYY-MM-DD HH24:MI:SS') ORDER BY SEQ,ID ";
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
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "������ϵ");
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
    }

    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {

        super.setAttribute(name, value);
    }
	
	
	// modified by WangQing 20170315
	/**
	 * Date->String
	 * @param date
	 * @return
	 */
	public String dateToString(Date date){
		//		Date date = new Date();
		String dateStr = "";
		//format�ĸ�ʽ��������
		//		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//			dateStr = sdf.format(date);
			//			System.out.println(dateStr);
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}
	
	
}
