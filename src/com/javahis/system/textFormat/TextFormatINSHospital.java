package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: 医保医院下拉区域</p>
 *
 * <p>Description: 医保医院下拉区域:HOSP_TYPE:1 一级医院 2.二级医院 3.三级医院 4.三级专科医院 5.定点药店</p>
 *
 * <p>Copyright: Copyright (c) BlueCore 2011</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author pangb 2012.04.10
 * @version 4.0
 */
public class TextFormatINSHospital extends TTextFormat{
	private String hospType;
	 public String getHospType() {
		return hospType;
	}
	public void setHospType(String hospType) {
		this.hospType = hospType;
		setModifySQL(true);
	}
	/**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT HOSP_CODE AS ID,HOSP_DESC AS NAME FROM INS_HOSP_LIST ";
        String sql1 = " ORDER BY HOSP_CODE";
        String hospType = TypeTool.getString(getTagValue(getHospType()));
        if (null!=hospType &&hospType.length()>0) {
        	sql+=" WHERE HOSP_TYPE='"+hospType+"' ";
		}
        sql+=sql1;
        return sql;
    }
    /**
     * 新建对象的初始值
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null)
            return;
        object.setValue("Width", "150");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "代码,100;名称,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "医保医院");
        object.setValue("ShowColumnList", "ID;NAME");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,2");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * 显示区域列名
     * @return String
     */
    public String getPopupMenuHeader() {

        return "代码,100;名称,200";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("hospType", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
    	if ("hospType".equalsIgnoreCase(name)) {
    		setHospType(value);
            getTObject().setValue("hospType", value);
            return;
        }
        super.setAttribute(name, value);
    }
   
}
