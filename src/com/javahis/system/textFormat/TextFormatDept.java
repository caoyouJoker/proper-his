package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;
import jdo.sys.Operator;

/**
 * <p>Title: 科室下拉区域</p>
 *
 * <p>Description: 科室下拉区域</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatDept
    extends TTextFormat {
    /**
     * 最小科室注记
     */
    private String finalFlg;
    /**
     * 科室等级
     */
    private String deptGrade;
    /**
     * 科室归类
     */
    private String classIfy;
    /**
     * 卫统科室
     */
    private String deptCat1;
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
     * 健康检查适用
     */
    private String hrmFigFlg;
    /**
     * 统计注记
     */
    private String statisticsFlg;
    /**
     * 启用注记
     */
    private String activeFlg;
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql = " SELECT DEPT_CODE AS ID,DEPT_CHN_DESC AS NAME,DEPT_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM SYS_DEPT ";
        String sql1 = " ORDER BY DEPT_CODE,SEQ";
        StringBuffer sb = new StringBuffer();
        String finalFlgTemp = TypeTool.getString(getTagValue(getFinalFlg()));
        if (finalFlgTemp != null && finalFlgTemp.length() > 0)
            sb.append(" FINAL_FLG = '" + finalFlgTemp + "' ");

        String deptGradeTemp = TypeTool.getString(getTagValue(getDeptGrade()));
        if (deptGradeTemp != null && deptGradeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DEPT_GRADE = '" + deptGradeTemp + "' ");
        }

        String classIfyTemp = TypeTool.getString(getTagValue(getClassIfy()));
//        if (classIfyTemp != null && classIfyTemp.length() > 0) {
//            if (sb.length() > 0)
//                sb.append(" AND ");
//            sb.append(" CLASSIFY = '" + classIfyTemp + "' ");
//        }
//        System.out.println("nnn1:"+classIfyTemp+"    ");
        if (classIfyTemp != null && classIfyTemp.length() > 0) {
            if (sb.length() > 0)
            		sb.append(" AND ");
            	String s = "";
                if(classIfyTemp.contains(",")){ 
                	int len = classIfyTemp.split(",").length;
                	s +=" CLASSIFY IN ( ";            	
                	for(int i = 0;i<len;i++){
                		s +="'"+classIfyTemp.split(",")[i]+"',";
                	}
                	s = s.substring(0, s.length()-1);
                	s +=" ) ";
                	sb.append(s);
                	//System.out.println("aaa:"+s);
                }else
                	sb.append(" CLASSIFY = '" + classIfyTemp + "' ");
               
        }
        
        String deptCat1Temp = TypeTool.getString(getTagValue(getDeptCat1()));
        if (deptCat1Temp != null && deptCat1Temp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DEPT_CAT1 = '" + deptCat1Temp + "' ");
        }

        String opdFitFlgTemp = TypeTool.getString(getTagValue(getOpdFitFlg()));
        if (opdFitFlgTemp != null && opdFitFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OPD_FIT_FLG = '" + opdFitFlgTemp + "' ");
        }

        String emgFitFlgTemp = TypeTool.getString(getTagValue(getEmgFitFlg()));
        if (emgFitFlgTemp != null && emgFitFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" EMG_FIT_FLG = '" + emgFitFlgTemp + "' ");
        }

        String ipdFitFlgTemp = TypeTool.getString(getTagValue(getIpdFitFlg()));
        if (ipdFitFlgTemp != null && ipdFitFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" IPD_FIT_FLG = '" + ipdFitFlgTemp + "' ");
        }

        String hrmFigFlgTemp = TypeTool.getString(getTagValue(getHrmFigFlg()));
        if (hrmFigFlgTemp != null && hrmFigFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" HRM_FIT_FLG = '" + hrmFigFlgTemp + "' ");
        }

        String statisticsFlgTemp = TypeTool.getString(getTagValue(
            getStatisticsFlg()));
        if (statisticsFlgTemp != null && statisticsFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" STATISTICS_FLG = '" + statisticsFlgTemp + "' ");
        }

        String activeFlgTemp = TypeTool.getString(getTagValue(getActiveFlg()));
        if (activeFlgTemp != null && activeFlgTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ACTIVE_FLG = '" + activeFlgTemp + "' ");
        }
        String RegionCodeTemp = TypeTool.getString(getTagValue(getRegionCode()));
        if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + RegionCodeTemp + "' ");
        }
        String operatorCodeAll = Operator.getRegion();
        if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + operatorCodeAll + "' ");
        }
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
        //System.out.println("nnn:"+sql);
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
        object.setValue("Tip", "科室");
        object.setValue("ShowColumnList", "ID;NAME");

    }
    public void onInit()
    {
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
        data.add(new TAttribute("FinalFlg", "String", "", "Left"));
        data.add(new TAttribute("DeptGrade", "String", "", "Left"));
        data.add(new TAttribute("ClassIfy", "String", "", "Left"));
        data.add(new TAttribute("DeptCat1", "String", "", "Left"));
        data.add(new TAttribute("OpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("EmgFitFlg", "String", "", "Left"));
        data.add(new TAttribute("IpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("HrmFigFlg", "String", "", "Left"));
        data.add(new TAttribute("StatisticsFlg", "String", "", "Left"));
        data.add(new TAttribute("ActiveFlg", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("FinalFlg".equalsIgnoreCase(name)) {
            setFinalFlg(value);
            getTObject().setValue("FinalFlg", value);
            return;
        }
        if ("DeptGrade".equalsIgnoreCase(name)) {
            setDeptGrade(value);
            getTObject().setValue("DeptGrade", value);
            return;
        }
        if ("ClassIfy".equalsIgnoreCase(name)) {
            setClassIfy(value);
            getTObject().setValue("ClassIfy", value);
            return;
        }
        if ("DeptCat1".equalsIgnoreCase(name)) {
            setDeptCat1(value);
            getTObject().setValue("DeptCat1", value);
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
        if ("HrmFigFlg".equalsIgnoreCase(name)) {
            setHrmFigFlg(value);
            getTObject().setValue("HrmFigFlg", value);
            return;
        }
        if ("StatisticsFlg".equalsIgnoreCase(name)) {
            setStatisticsFlg(value);
            getTObject().setValue("StatisticsFlg", value);
            return;
        }
        if ("ActiveFlg".equalsIgnoreCase(name)) {
            setActiveFlg(value);
            getTObject().setValue("ActiveFlg", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            this.setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    public String getClassIfy() {
        return classIfy;
    }

    public String getDeptCat1() {
        return deptCat1;
    }

    public String getDeptGrade() {
        return deptGrade;
    }

    public String getEmgFitFlg() {
        return emgFitFlg;
    }

    public String getFinalFlg() {
        return finalFlg;
    }

    public String getHrmFigFlg() {
        return hrmFigFlg;
    }

    public String getIpdFitFlg() {
        return ipdFitFlg;
    }

    public String getOpdFitFlg() {
        return opdFitFlg;
    }

    public String getStatisticsFlg() {
        return statisticsFlg;
    }

    public String getActiveFlg() {
        return activeFlg;
    }
    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    public void setClassIfy(String classIfy) {
        this.classIfy = classIfy;
        setModifySQL(true);
    }

    public void setDeptCat1(String deptCat1) {
        this.deptCat1 = deptCat1;
        setModifySQL(true);
    }

    public void setDeptGrade(String deptGrade) {
        this.deptGrade = deptGrade;
        setModifySQL(true);
    }

    public void setEmgFitFlg(String emgFitFlg) {
        this.emgFitFlg = emgFitFlg;
        setModifySQL(true);
    }

    public void setFinalFlg(String finalFlg) {
        this.finalFlg = finalFlg;
        setModifySQL(true);
    }

    public void setHrmFigFlg(String hrmFigFlg) {
        this.hrmFigFlg = hrmFigFlg;
        setModifySQL(true);
    }

    public void setIpdFitFlg(String ipdFitFlg) {
        this.ipdFitFlg = ipdFitFlg;
        setModifySQL(true);
    }

    public void setOpdFitFlg(String opdFitFlg) {
        this.opdFitFlg = opdFitFlg;
        setModifySQL(true);
    }

    public void setStatisticsFlg(String statisticsFlg) {
        this.statisticsFlg = statisticsFlg;
        setModifySQL(true);
    }

    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}
