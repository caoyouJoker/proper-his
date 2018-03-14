package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;
/**
 * <p>Title: 临床路径套餐字典下拉区域</p>
 *
 * <p>Description: 临床路径套餐字典下拉区域</p>
 *
 * <p>Copyright: Copyright (c) pangben 2015</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author pangben 20150810
 * @version 1.0
 */
public class TextFormatClpPackAge extends TTextFormat{
	/**
	 * 临床路径CLNCPATH_CODE pangben 2012-6-4
	 */
	private String clncpathCode;//
	private String schdCode;
	/**
	 * 执行sql
	 */
	private String sqlFlg;
	public String getSqlFlg() {
		return sqlFlg;
	}

	public void setSqlFlg(String sqlFlg) {
		this.sqlFlg = sqlFlg;
	}

	/**
     * 执行Module动作
     * @return String
     */
	/**
	 * 设置 临床路径
	 * 
	 * @param nhiCtzFlg
	 *            String
	 */
	public void setClncpathCode(String clncpathCode) {
		this.clncpathCode = clncpathCode;
	}

	public String getSchdCode() {
		return schdCode;
	}

	public void setSchdCode(String schdCode) {
		this.schdCode = schdCode;
	}

	private String getClncpathCode() {
		return this.clncpathCode;
	}
    public String getPopupMenuSQL() {
    	String sqlFlg = TypeTool.getString(getTagValue(getSqlFlg()));
    	String sql="";
    	if (sqlFlg.equals("Y")) {
    		sql =" SELECT PACKAGE_CODE ID,PACKAGE_DESC AS NAME,PY AS PY1 FROM CLP_PACKAGE ORDER BY SEQ_NO ";
		}else{
			String clncpathCode = TypeTool.getString(getTagValue(getClncpathCode()));
			sql="SELECT A.PACKAGE_CODE ID,A.PACKAGE_DESC AS NAME,A.PY AS PY1 FROM CLP_PACKAGE A," +
					"(SELECT PACK_CODE FROM CLP_PACK WHERE CLNCPATH_CODE='"
				+clncpathCode+"' ";
			String schdCode = TypeTool.getString(getTagValue(getSchdCode()));
			if (schdCode!= null && schdCode.length()>0) {
				sql+=" AND SCHD_CODE='"+schdCode+"'";
			}
			sql+=" GROUP BY PACK_CODE) B WHERE A.PACKAGE_CODE=B.PACK_CODE ORDER BY A.SEQ_NO";
		}
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
        object.setValue("PopupMenuHeader", "代码,100;名称,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "临床路径套餐字典");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;PY1,1");
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
        data.add(new TAttribute("sqlFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
    	if ("sqlFlg".equalsIgnoreCase(name)) {
			getTObject().setValue("sqlFlg", value);
			return;
		}
        super.setAttribute(name, value);
    }
}
