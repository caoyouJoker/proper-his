package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;
import com.dongyang.util.TypeTool;

/**
 * <p>
 * Title: 临床路径时程下拉区域
 * </p>
 * 
 * <p>
 * Description: 临床路径时程下拉区域
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) Liu dongyang 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wangl 2011.06.02
 * @version 1.0
 */
public class TextFormatCLPDuration extends TTextFormat {
	/**
	 * 父类代码
	 */
	private String parentCode; // PARENT_CODE
	/**
	 * 叶子标志
	 */
	private String leafFlg; // LEAF_FLG
	/**
	 * 区域
	 */
	private String regionCode;
	/**
	 * 临床路径CLNCPATH_CODE pangben 2012-6-4
	 */
	private String clncpathCode;//
	/**
	 * 当前就诊病患号码 pangben 2012-6-4
	 */
	private String caseNo;//
	/**
	 * 判断执行的sql语句 pangben 2012-6-12
	 */
	private String sqlFlg;//

	/**
	 * 设置 临床路径
	 * 
	 * @param nhiCtzFlg
	 *            String
	 */
	public void setClncpathCode(String clncpathCode) {
		this.clncpathCode = clncpathCode;
	}

	private String getClncpathCode() {
		return this.clncpathCode;
	}

	/**
	 * 设置 临床路径
	 * 
	 * @param nhiCtzFlg
	 *            String
	 */
	public void setCaseNo(String caseNo) {
		this.caseNo = caseNo;
	}

	private String getCaseNo() {
		return this.caseNo;
	}

	/**
	 * 设置 判断执行的sql语句
	 * 
	 * @param nhiCtzFlg
	 *            String
	 */
	public void setSqlFlg(String sqlFlg) {
		this.sqlFlg = sqlFlg;
	}

	private String getSqlFlg() {
		return this.sqlFlg;
	}

	/**
	 * 执行Module动作
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		// =========pangben 2012-6-12 修改执行sql 判断查询不同的表
		String sqlFlg = TypeTool.getString(getTagValue(getSqlFlg()));
		String sqlName = "";
		//==modify by caowl 20121126 start
		String sql = " SELECT DISTINCT A.DURATION_CODE AS ID,A.DURATION_CHN_DESC AS NAME,A.DURATION_ENG_DESC AS ENNAME,A.PY1,A.PY2,A.SEQ "
				+ "   FROM CLP_DURATION A , CLP_THRPYSCHDM B WHERE A.DURATION_CODE=B.SCHD_CODE(+) ";
        //===modify by caowl 20121126 end

		if (sqlFlg.equals("Y")) {
			sql = " SELECT A.DURATION_CODE AS ID,DURATION_CHN_DESC AS NAME,DURATION_ENG_DESC AS ENNAME,PY1,PY2 "
					+ "   FROM CLP_DURATION A WHERE  A.LEAF_FLG='Y' ";
		}
		String sql1 = " ORDER BY A.DURATION_CODE,A.SEQ";
		StringBuffer sb = new StringBuffer();
		String parentCodeTemp = TypeTool
				.getString(getTagValue(getParentCode()));
		if (parentCodeTemp != null && parentCodeTemp.length() > 0)
			sb.append(" A.PARENT_CODE = '" + parentCodeTemp + "' ");

		String leafFlgTemp = TypeTool.getString(getTagValue(getLeafFlg()));
		if (leafFlgTemp != null && leafFlgTemp.length() > 0) {
			sb.append(" AND A.LEAF_FLG = '" + leafFlgTemp + "' ");
		}
		// =======pangben 2012-6-4 start
//		if (caseNo != null && caseNo.length() > 0) {
//			sb.append(" AND B.CASE_NO = '" + caseNo + "' ");
//		}
		String clncpathCode = TypeTool
				.getString(getTagValue(getClncpathCode()));
		if (sqlFlg.equals("Y")) {
//			if (clncpathCode != null && clncpathCode.length() > 0) {
//				sb.append(" AND A.CLNCPATH_CODE = '" + clncpathCode + "' ");
//			}
		} else {
			if (clncpathCode != null && clncpathCode.length() > 0) {
				sb.append(" AND B.CLNCPATH_CODE = '" + clncpathCode + "' ");
			}
		}
		// =======pangben 2012-6-4 stop
		String RegionCodeTemp = TypeTool
				.getString(getTagValue(getRegionCode()));
		if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append(" A.REGION_CODE = '" + RegionCodeTemp + "' ");
		}
		String operatorCodeAll = Operator.getRegion();
		if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
			sb.append("AND A.REGION_CODE = '" + operatorCodeAll + "' ");
		}

		if (sb.length() > 0)
			sql += sb.toString() + sql1;
		else
			sql = sql + sql1;
		return sql;
	}

	/**
	 * 新建对象的初始值
	 * 
	 * @param object
	 *            TObject
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
		object.setValue("Tip", "临床路径时程");
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
	 * 
	 * @return String
	 */
	public String getPopupMenuHeader() {

		return "代码,100;名称,200";
	}

	/**
	 * 增加扩展属性
	 * 
	 * @param data
	 *            TAttributeList
	 */
	public void getEnlargeAttributes(TAttributeList data) {
		data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
		data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
		data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
		data.add(new TAttribute("ParentCode", "String", "", "Left"));
		data.add(new TAttribute("LeafFlg", "String", "", "Left"));
		data.add(new TAttribute("RegionCode", "String", "", "Left"));
		// ==============pangben 2012-6-4
		data.add(new TAttribute("clncpathCode", "String", "", "Left"));
		data.add(new TAttribute("caseNo", "String", "", "Left"));
		data.add(new TAttribute("sqlFlg", "String", "", "Left"));
	}

	/**
	 * 设置属性
	 * 
	 * @param name
	 *            String 属性名
	 * @param value
	 *            String 属性值
	 */
	public void setAttribute(String name, String value) {
		if ("ParentCode".equalsIgnoreCase(name)) {
			setParentCode(value);
			getTObject().setValue("ParentCode", value);
			return;
		}
		if ("LeafFlg".equalsIgnoreCase(name)) {
			setLeafFlg(value);
			getTObject().setValue("LeafFlg", value);
			return;
		}
		if ("RegionCode".equalsIgnoreCase(name)) {
			this.setRegionCode(value);
			getTObject().setValue("RegionCode", value);
			return;
		}
		if ("clncpathCode".equalsIgnoreCase(name)) {
			setClncpathCode(value);
			getTObject().setValue("clncpathCode", value);
			return;
		}
		if ("caseNo".equalsIgnoreCase(name)) {
			setCaseNo(value);
			getTObject().setValue("caseNo", value);
			return;
		}
		if ("sqlFlg".equalsIgnoreCase(name)) {
			setCaseNo(value);
			getTObject().setValue("sqlFlg", value);
			return;
		}
		super.setAttribute(name, value);
	}

	public String getLeafFlg() {
		return leafFlg;
	}

	public String getParentCode() {
		return parentCode;
	}

	/**
	 * 得到区域
	 * 
	 * @return String
	 */
	public String getRegionCode() {
		return regionCode;
	}

	public void setLeafFlg(String leafFlg) {
		this.leafFlg = leafFlg;
		setModifySQL(true);
	}

	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
		setModifySQL(true);
	}

	/**
	 * 设置区域
	 * 
	 * @param regionCode
	 *            String
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}
}
