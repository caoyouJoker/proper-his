package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: 结构化病例申请单下拉区域</p>
 *
 * <p>Description: 结构化病例申请单下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatEMRTemplet
    extends TTextFormat {
    /**
     * 模版分类
     */
    private String classCode;
    /**
     * 科室代码
     */
    private String deptCode; //DEPT_CODE
    /**
     * 门诊适用
     */
    private String opdFlg; //OPD_FLG
    /**
     * 住院注记
     */
    private String ipdFlg; //IPD_FLG
    /**
     * 急诊注记
     */
    private String emgFlg; //EMG_FLG
    /**
     * 健检注记
     */
    private String hrmFlg; //HRM_FLG
    /**
     * 设置模版分类
     * @param classCode String
     */
    public void setClassCode(String classCode) {

        this.classCode = classCode;
        setModifySQL(true);
    }

    /**
     * 设置门诊适用注记
     * @param opdFlg String
     */
    public void setOpdFlg(String opdFlg) {
        this.opdFlg = opdFlg;
        setModifySQL(true);
    }

    /**
     * 设置科室代码
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
        setModifySQL(true);
    }

    /**
     * 设置急诊注记
     * @param emgFlg String
     */
    public void setEmgFlg(String emgFlg) {
        this.emgFlg = emgFlg;
        setModifySQL(true);
    }

    /**
     * 设置住院注记
     * @param ipdFlg String
     */
    public void setIpdFlg(String ipdFlg) {
        this.ipdFlg = ipdFlg;
        setModifySQL(true);
    }

    /**
     * 设置健检注记
     * @param hrmFlg String
     */
    public void setHrmFlg(String hrmFlg) {
        this.hrmFlg = hrmFlg;
        setModifySQL(true);
    }

    /**
     * 模版分类
     * @return String
     */
    public String getClassCode() {
        return classCode;
    }

    /**
     * 得到中重度注记
     * @return String
     */
    public String getOpdFlg() {
        return opdFlg;
    }

    /**
     * 得到科室代码
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * 得到急诊注记
     * @return String
     */
    public String getEmgFlg() {
        return emgFlg;
    }

    /**
     * 得到住院注记
     * @return String
     */
    public String getIpdFlg() {
        return ipdFlg;
    }

    /**
     * 得到健检注记
     * @return String
     */
    public String getHrmFlg() {
        return hrmFlg;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT SUBCLASS_CODE AS ID,SUBCLASS_DESC AS NAME,ENNAME,PY1 " +
            "   FROM EMR_TEMPLET ";
        String sql1 = " ORDER BY SUBCLASS_CODE ";
        StringBuffer sb = new StringBuffer();

        String classCode = TypeTool.getString(getTagValue(getClassCode()));
        if (classCode != null && classCode.length() > 0)
            sb.append(" CLASS_CODE = '" + classCode + "' ");

        String deptCode = TypeTool.getString(getTagValue(getDeptCode()));
        if (deptCode != null && deptCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DEPT_CODE = '" + deptCode + "' ");
        }

        String opdFlg = TypeTool.getString(getTagValue(getOpdFlg()));
        if (opdFlg != null && opdFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OPD_FLG = '" + opdFlg + "' ");
        }

        String ipdFlg = TypeTool.getString(getTagValue(getIpdFlg()));
        if (ipdFlg != null && ipdFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" IPD_FLG = '" + ipdFlg + "' ");
        }

        String emgFlg = TypeTool.getString(getTagValue(
            getEmgFlg()));
        if (emgFlg != null && emgFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" EMG_FLG = '" + emgFlg + "' ");
        }

        String hrmFlg = TypeTool.getString(getTagValue(getHrmFlg()));
        if (hrmFlg != null && hrmFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" HRM_FLG = '" + hrmFlg + "' ");
        }

        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//    System.out.println("sql"+sql);
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
        object.setValue("Tip", "结构化病例申请单");
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
        data.add(new TAttribute("ClassCode", "String", "", "Left"));
        data.add(new TAttribute("DeptCode", "String", "", "Left"));
        data.add(new TAttribute("OpdFlg", "String", "", "Left"));
        data.add(new TAttribute("IpdFlg", "String", "", "Left"));
        data.add(new TAttribute("EmgFlg", "String", "", "Left"));
        data.add(new TAttribute("TransplantFlg", "String", "", "Left"));
        data.add(new TAttribute("HrmFlg", "boolean", "N", "Center"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("ClassCode".equalsIgnoreCase(name)) {
            setClassCode(value);
            getTObject().setValue("ClassCode", value);
            return;
        }
        if ("DeptCode".equalsIgnoreCase(name)) {
            setDeptCode(value);
            getTObject().setValue("DeptCode", value);
            return;
        }
        if ("OpdFlg".equalsIgnoreCase(name)) {
            setOpdFlg(value);
            getTObject().setValue("OpdFlg", value);
            return;
        }
        if ("IpdFlg".equalsIgnoreCase(name)) {
            setIpdFlg(value);
            getTObject().setValue("IpdFlg", value);
            return;
        }
        if ("EmgFlg".equalsIgnoreCase(name)) {
            setEmgFlg(value);
            getTObject().setValue("EmgFlg", value);
            return;
        }
        if ("HrmFlg".equalsIgnoreCase(name)) {
            setHrmFlg(value);
            getTObject().setValue("HrmFlg", value);
            return;
        }

        super.setAttribute(name, value);
    }
}
