package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 临床路径执行人员下拉区域</p>
 *
 * <p>Description: 临床路径执行人员下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2011.05.04
 * @version 1.0
 */
public class TextFormatCLPChkUser
    extends TTextFormat {
	private String chkFLg;//住院医生显示
	private void setChkFlg(String chkFLg){
		this.chkFLg=chkFLg;
	}
	public String getChkFlg(){
		return this.chkFLg;
	}
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT CHKUSER_CODE AS ID, CHKUSER_CHN_DESC AS NAME,CHKUSER_ENG_DESC AS ENNAME,PY1,PY2 FROM CLP_CHKUSER  ";
        String chkFlg = TypeTool.getString(getTagValue(getChkFlg()));
        //System.out.println("chkFlg:::"+chkFlg);
        String sql1 = " ORDER BY SEQ,CHKUSER_CODE";
        StringBuffer sb = new StringBuffer();
        if (chkFlg != null && chkFlg.length() > 0)
            sb.append(" CHKUSER_FLG = '" + chkFlg + "' ");
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        //System.out.println("SQL:::"+sql);
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "临床路径执行人员");
        object.setValue("ShowColumnList", "ID;NAME");

    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * 显示区域列明
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
        data.add(new TAttribute("ChkFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
    	//System.out.println("name:::"+name);
    	if ("ChkFlg".equalsIgnoreCase(name)) {
            setChkFlg(value);
            getTObject().setValue("ChkFlg", value);
            return;
        }
        super.setAttribute(name, value);
    }
}

