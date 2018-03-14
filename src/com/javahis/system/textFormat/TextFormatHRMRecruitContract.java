/**
 * 
 */
package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: �ٴ�������������</p>
 *
 * <p>Description: �ٴ�������������</p>
 *
 * <p>Company: Bluecore</p>
 *
 * @author guangl 201606012
 * @version 1.0
 */
public class TextFormatHRMRecruitContract extends TTextFormat {
	//���������Ŀ��
	private String projectCode;

	public String getProjectCode() {
		return projectCode;
	}

	public void setProjectCode(String projectCode) {
		this.projectCode = projectCode;
		setModifySQL(true);
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
        object.setValue("PopupMenuHeader", "����,100;����,200");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "�ٴ�����");
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
     * @return String
     */
    public String getPopupMenuHeader() {

        return "���,100;����,200";
    }
	
	/**
     * ִ��Module����
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CONTRACT_CODE AS ID,CONTRACT_DESC AS NAME FROM HRM_RECRUIT ";
        StringBuffer sb = new StringBuffer();
        String projectCodeTemp = TypeTool.getString(getTagValue(getProjectCode()));
        if (projectCodeTemp != null && projectCodeTemp.length() > 0)
            sb.append(" PROJECT_CODE = '" + projectCodeTemp + "' ");
        String sql1 = " GROUP BY CONTRACT_CODE , CONTRACT_DESC ORDER BY CONTRACT_CODE ";
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        System.out.println(sql);
        return sql;
        
        
    }
    
    /**
     * ��������
     * @param name String ������
     * @param value String ����ֵ
     */
    public void setAttribute(String name, String value) {
    	
        if ("ProjectCode".equalsIgnoreCase(name)) {
            this.setProjectCode(value);
            getTObject().setValue("ProjectCode", value);
            return;
        }
        super.setAttribute(name, value);
         
    }
    
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("ProjectCode", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        
    }

}
