package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;
import com.javahis.util.StringUtil;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

/**
 * <p>
 * Title: 人员下拉区域
 * </p>
 * 
 * <p>
 * Description: 人员下拉区域
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
 * @author wangl 2009.10.27
 * @version 1.0
 */
public class TextFormatSYSOperator extends TTextFormat {
	/**
	 * 科室
	 */
	private String dept;

	/**
	 * 职别类别
	 */
	private String posType;
	/**
	 * 科室等级
	 */
	private String deptGrade;
	/**
	 * 科室归类
	 */
	private String classify;
	/**
	 * 最小科室
	 */
	private String finalFlg;
	/**
	 * 门诊适用
	 */
	private String opdFitFlg;
	/**
	 * 急诊适用
	 */
	private String emgFitFlg;
	/**
	 * 住院适用
	 */
	private String ipdFitFlg;
	/**
	 * 健检适用
	 */
	private String hrmFitFlg;
	/**
	 * 区域 =====pangben modify 20110516
	 */
	private String regionCode;

	/**
	 * 病区 =====wukai modify 20160510
	 */
	private String regionName;

	/**
	 * 启用人员标记
	 */
	private String endDateFlg;

	/**
	 * 设置启用病区标记
	 * 
	 * @return
	 */
	public String getRegionName() {
		return regionName;
	}

	/**
	 * 得到病区标记
	 * 
	 * @param regionName
	 */
	public void setRegionName(String regionName) {
		this.regionName = regionName;
	}

	/**
	 * 
	 * 获得启用人员标记
	 * 
	 * @return
	 */
	public String getEndDateFlg() {
		return endDateFlg;
	}

	/**
	 * 
	 * 设置启用人员标记
	 * 
	 * @param endDateFlg
	 */
	public void setEndDateFlg(String endDateFlg) {
		this.endDateFlg = endDateFlg;
	}

	/**
	 * 设置科室
	 * 
	 * @param dept
	 *            String
	 */
	public void setDept(String dept) {
		this.dept = dept;
	}

	/**
	 * 设置区域
	 * 
	 * @param regionCode
	 *            String =====pangben modify 20110516
	 */
	public void setRegionCode(String regionCode) {
		this.regionCode = regionCode;
	}

	/**
	 * 得到科室
	 * 
	 * @return String
	 */
	public String getDept() {
		return dept;
	}

	/**
	 * 设置职别类别
	 * 
	 * @param posType
	 *            String
	 */
	public void setPosType(String posType) {
		this.posType = posType;
	}

	/**
	 * 得到职别类别
	 * 
	 * @return String
	 */
	public String getPosType() {
		return posType;
	}

	/**
	 * 设置科室等级
	 * 
	 * @param deptGrade
	 *            String
	 */
	public void setDeptGrade(String deptGrade) {
		this.deptGrade = deptGrade;
	}

	/**
	 * 得到科室等级
	 * 
	 * @return String
	 */
	public String getDeptGrade() {
		return deptGrade;
	}

	/**
	 * 设置科室归类
	 * 
	 * @param classify
	 *            String
	 */
	public void setClassify(String classify) {
		this.classify = classify;
	}

	/**
	 * 得到科室归类
	 * 
	 * @return String
	 */
	public String getClassify() {
		return classify;
	}

	/**
	 * 设置最小科室
	 * 
	 * @param finalFlg
	 *            boolean '' 全部 Y 最小科室 N 不是最下科室
	 */
	public void setFinalFlg(String finalFlg) {
		this.finalFlg = finalFlg;
	}

	/**
	 * 是否是最小科室
	 * 
	 * @return String
	 */
	public String getFinalFlg() {
		return finalFlg;
	}

	/**
	 * 设置门诊适用
	 * 
	 * @param opdFitFlg
	 *            String
	 */
	public void setOpdFitFlg(String opdFitFlg) {
		this.opdFitFlg = opdFitFlg;
	}

	/**
	 * 得到门诊适用
	 * 
	 * @return String
	 */
	public String getOpdFitFlg() {
		return opdFitFlg;
	}

	/**
	 * 设置急诊适用
	 * 
	 * @param emgFitFlg
	 *            String
	 */
	public void setEmgFitFlg(String emgFitFlg) {
		this.emgFitFlg = emgFitFlg;
	}

	/**
	 * 得到急诊适用
	 * 
	 * @return String
	 */
	public String getEmgFitFlg() {
		return emgFitFlg;
	}

	/**
	 * 设置住院适用
	 * 
	 * @param ipdFitFlg
	 *            String
	 */
	public void setIpdFitFlg(String ipdFitFlg) {
		this.ipdFitFlg = ipdFitFlg;
	}

	/**
	 * 得到住院适用
	 * 
	 * @return String
	 */
	public String getIpdFitFlg() {
		return ipdFitFlg;
	}

	/**
	 * 设置健检适用
	 * 
	 * @param hrmFitFlg
	 *            String
	 */
	public void setHrmFitFlg(String hrmFitFlg) {
		this.hrmFitFlg = hrmFitFlg;
	}

	/**
	 * 得到健检适用
	 * 
	 * @return String
	 */
	public String getHrmFitFlg() {
		return hrmFitFlg;
	}

	/**
	 * 执行Module动作
	 * 
	 * @return String
	 */
	public String getPopupMenuSQL() {
		String sql = " SELECT USER_ID AS ID,USER_NAME AS NAME,USER_ENG_NAME AS ENNAME,PY1,PY2 FROM SYS_OPERATOR ";
		String sql1 = " ORDER BY USER_ID ";

		StringBuffer sb = new StringBuffer();
		
		// add by wangqing 20170721 
		// 过滤掉角色为T的规培医师
		sb.append(" ROLE_ID != 'T' ");
		
		
		String dept = TypeTool.getString(getTagValue(getDept()));
		// =====add wukai 20160510 start
		String region = TypeTool.getString(getTagValue(getRegionName()));
		if (dept != null && dept.length() > 0) {
			// add by wangqing 20170721
			sb.append(" AND ");
			sb.append(" USER_ID IN (SELECT USER_ID FROM SYS_OPERATOR_DEPT WHERE DEPT_CODE='"
					+ dept + "' ");
			if (region != null && region.length() > 0)
				sb.append(" UNION ALL SELECT USER_ID FROM SYS_OPERATOR_STATION WHERE STATION_CLINIC_CODE= '"
						+ region + "' ");
			sb.append(" )");
		} else {
			if (region != null && region.length() > 0)
				sb.append(" USER_ID IN (SELECT USER_ID FROM SYS_OPERATOR_STATION WHERE STATION_CLINIC_CODE= '"
						+ region + "' )");
		}
		// =====add wukai 20160510 end

		String posType = TypeTool.getString(getTagValue(getPosType()));
		if (posType != null && posType.length() > 0) {
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append(" POS_CODE IN(SELECT POS_CODE FROM SYS_POSITION WHERE POS_TYPE='"
					+ posType + "')");
		}

		String sqlWhere = " USER_ID IN (SELECT USER_ID FROM SYS_OPERATOR_DEPT "
				+ "              WHERE DEPT_CODE IN (SELECT DEPT_CODE FROM SYS_DEPT "
				+ " WHERE ";
		String sqlEnd = "))";
		StringBuffer sbIn = new StringBuffer();

		String deptGrade = TypeTool.getString(getTagValue(getDeptGrade()));
		if (deptGrade != null && deptGrade.length() > 0) {
			sbIn.append(" DEPT_GRADE = '" + deptGrade + "' ");
		}

		String classify = TypeTool.getString(getTagValue(getClassify()));
		if (classify != null && classify.length() > 0) {
			if (sbIn.length() > 0)
				sbIn.append(" AND ");
			sbIn.append(" CLASSIFY = '" + classify + "' ");
		}

		String finalFlg = TypeTool.getString(getTagValue(getFinalFlg()));
		if (finalFlg != null && finalFlg.length() > 0) {
			if (sbIn.length() > 0)
				sbIn.append(" AND ");
			sbIn.append(" FINAL_FLG = '" + finalFlg + "' ");
		}

		String opdFitFlg = TypeTool.getString(getTagValue(getOpdFitFlg()));
		if (opdFitFlg != null && opdFitFlg.length() > 0) {
			if (sbIn.length() > 0)
				sbIn.append(" AND ");
			sbIn.append(" OPD_FIT_FLG = '" + opdFitFlg + "' ");
		}

		String emgFitFlg = TypeTool.getString(getTagValue(getEmgFitFlg()));
		if (emgFitFlg != null && emgFitFlg.length() > 0) {
			if (sbIn.length() > 0)
				sbIn.append(" AND ");
			sbIn.append(" EMG_FIT_FLG = '" + emgFitFlg + "' ");
		}

		String ipdFitFlg = TypeTool.getString(getTagValue(getIpdFitFlg()));
		if (ipdFitFlg != null && ipdFitFlg.length() > 0) {
			if (sbIn.length() > 0)
				sbIn.append(" AND ");
			sbIn.append(" IPD_FIT_FLG = '" + ipdFitFlg + "' ");
		}

		String hrmFitFlg = TypeTool.getString(getTagValue(getHrmFitFlg()));
		if (hrmFitFlg != null && hrmFitFlg.length() > 0) {
			if (sbIn.length() > 0)
				sbIn.append(" AND ");
			sbIn.append(" HRM_FIT_FLG = '" + hrmFitFlg + "' ");
		}
		if (sbIn.length() > 0) {
			if (sb.length() > 0)
				sb.append(" AND ");
			sqlWhere += sbIn.toString() + sqlEnd;
			sb.append(sqlWhere);
		}
		// =============pangben modify 20110420 start 添加号别区域筛选
		String operatorCodeAll = Operator.getRegion();
		if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append(" REGION_CODE = '" + operatorCodeAll + "' ");
		}
		// =============pangben modify 20110420 stop

		// =========lim modify 20120330 start 添加启用人员标记
		String endDate = TypeTool.getString(getTagValue(this.getEndDateFlg()));
		if (endDate != null && "1".equals(endDate)) {
			String nowDate = StringTool.getString(SystemTool.getInstance()
					.getDate(), "yyyyMMddHHmmss");
			if (sb.length() > 0)
				sb.append(" AND ");
			sb.append(" TO_CHAR(END_DATE,'yyyyMMddHH24miss')>'" + nowDate
					+ "' AND TO_CHAR(ACTIVE_DATE,'yyyyMMddHH24miss')<='"
					+ nowDate + "'");
		}
		// =========lim modify 20120330 end
		if (sb.length() > 0)
			sql += " WHERE " + sb.toString() + sql1;
		else
			sql = sql + sql1;
		// this.setPopupMenuSQL(sql);
		// super.onQuery();
		//System.out.println("TextFormatSYSOperator-sql:" +sql);
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
		object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
		// object.setValue("ShowColumnList", "NAME");
		// object.setValue("ValueColumn", "ID");
		object.setValue("FormatType", "combo");
		object.setValue("ShowDownButton", "Y");
		object.setValue("Tip", "人员");
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
		data.add(new TAttribute("Dept", "String", "", "Left"));
		data.add(new TAttribute("PosType", "String", "", "Left"));
		data.add(new TAttribute("DeptGrade", "String", "", "Left"));
		data.add(new TAttribute("Classify", "String", "", "Left"));
		// ========wukai modify 20160510
		data.add(new TAttribute("RegionName", "String", "", "Left"));
		data.add(new TAttribute("FinalFlg", "String", "", "Left"));
		data.add(new TAttribute("OpdFitFlg", "String", "", "Left"));
		data.add(new TAttribute("EmgFitFlg", "String", "", "Left"));
		data.add(new TAttribute("IpdFitFlg", "String", "", "Left"));
		data.add(new TAttribute("HrmFitFlg", "String", "", "Left"));
		data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
		// ==========pangben modify 20110516
		data.add(new TAttribute("RegionCode", "String", "", "Left"));
		// ==========lim modify 20120330
		data.add(new TAttribute("EndDateFlg", "String", "", "Left"));
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

		// =====pangben modify 20110516 start
		if ("RegionCode".equalsIgnoreCase(name)) {
			setRegionCode(value);
			getTObject().setValue("RegionCode", value);
			return;
		}
		// =====pangben modify 20110516 stop

		// =====lim modify 20120330 start
		if ("EndDateFlg".equalsIgnoreCase(name)) {
			this.setEndDateFlg(value);
			getTObject().setValue("EndDateFlg", value);
		}
		// =====lim modify 20120330 end
		if ("Dept".equalsIgnoreCase(name)) {
			setDept(value);
			getTObject().setValue("Dept", value);
			return;
		}
		// =====wukai modify 20160510
		if ("RegionName".equalsIgnoreCase(name)) {
			setRegionName(value);
			getTObject().setValue("RegionName", value);
			return;
		}
		if ("PosType".equalsIgnoreCase(name)) {
			setPosType(value);
			getTObject().setValue("PosType", value);
			return;
		}
		if ("DeptGrade".equalsIgnoreCase(name)) {
			setDeptGrade(value);
			getTObject().setValue("DeptGrade", value);
			return;
		}
		if ("Classify".equalsIgnoreCase(name)) {
			setClassify(value);
			getTObject().setValue("Classify", value);
			return;
		}
		if ("FinalFlg".equalsIgnoreCase(name)) {
			setFinalFlg(value);
			getTObject().setValue("FinalFlg", value);
			return;
		}
		if ("OpdFitFlg".equalsIgnoreCase(name)) {
			setOpdFitFlg(value);
			getTObject().setValue("OpdFitFlg", value);
			return;
		}
		if ("EmgFitFlg".equalsIgnoreCase(name)) {
			setEmgFitFlg(value);
			getTObject().setValue("EmgFitFlg", value);
			return;
		}
		if ("IpdFitFlg".equalsIgnoreCase(name)) {
			setIpdFitFlg(value);
			getTObject().setValue("IpdFitFlg", value);
			return;
		}
		if ("HrmFitFlg".equalsIgnoreCase(name)) {
			setHrmFitFlg(value);
			getTObject().setValue("HrmFitFlg", value);
			return;
		}
		super.setAttribute(name, value);
	}
}
