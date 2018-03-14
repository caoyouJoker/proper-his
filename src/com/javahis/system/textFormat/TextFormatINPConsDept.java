package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

public class TextFormatINPConsDept extends TTextFormat {
	private String dept;

	public void setDept(String dept) {
		this.dept = dept;
	}

	/**
	 * 得到科室
	 * 
	 * @return String
	 */
	public String getDept() {
		return dept;
	}

	public String getPopupMenuSQL(){
		String deptWhere="";
		String dept = TypeTool.getString(getTagValue(getDept()));
        if (dept != null && dept.length() > 0){
        	deptWhere= " AND A.DEPT_CODE='" +dept+ "'";
        }
		/*String sql = " SELECT A.DR_CODE1 AS ID,B.USER_NAME AS NAME "
			
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE1=B.USER_ID(+)"+deptWhere;
		
		sql+=" UNION  SELECT A.DR_CODE2 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE2=B.USER_ID(+) "+deptWhere+" UNION  SELECT A.DR_CODE3 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE3=B.USER_ID(+) "+deptWhere+" UNION  SELECT A.DR_CODE4 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE4=B.USER_ID(+) "+deptWhere;*/
		
		String sql = " SELECT A.DR_CODE1 AS ID,B.USER_NAME AS NAME "
			
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE1=B.USER_ID(+)"+deptWhere;
		
		sql+=" UNION  SELECT A.DR_CODE2 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE2=B.USER_ID(+) "+deptWhere+" UNION  SELECT A.DR_CODE3 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE3=B.USER_ID(+) "+deptWhere+" UNION  SELECT A.DR_CODE4 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE4=B.USER_ID(+) "+deptWhere+" UNION  SELECT A.DR_CODE5 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE5=B.USER_ID(+) "+deptWhere+" UNION  SELECT A.DR_CODE6 AS ID,B.USER_NAME AS NAME "
			+ " FROM INP_SCHDAY A,SYS_OPERATOR B WHERE "
			+ " A.DR_CODE6=B.USER_ID(+) "+deptWhere ;
		
		return sql;
	}

	public void createInit(TConfigParse.TObject object) {
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
		object.setValue("Tip", "会诊医师");
		object.setValue("ShowColumnList", "ID;NAME");
	}

	public void onInit() {
		super.onInit();
		setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
		setLanguageMap("NAME|ENNAME");
		setPopupMenuEnHeader("Code;Name");
	}

	public String getPopupMenuHeader() {
		return "代码,100;名称,200";
	}

	public void getEnlargeAttributes(TAttributeList data) {
		data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
		data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
		data.add(new TAttribute("Dept", "String", "", "Left"));
		data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
		data.add(new TAttribute("EndDateFlg", "String", "", "Left"));
	}

	public void setAttribute(String name, String value) {
		if ("Dept".equalsIgnoreCase(name)) {
			setDept(value);
			getTObject().setValue("Dept", value);
			return;
		}
		super.setAttribute(name, value);

	}
}
