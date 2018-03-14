package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;

/**
 *
 * <p>Title:人员下拉列表 </p>
 *
 * <p>Description:人员下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.04
 * @version 1.0
 */
public class TComboOperatorCode
    extends TComboBox {
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
     * 设置科室
     * @param dept String
     */
    public void setDept(String dept) {
        this.dept = dept;
    }

    /**
     * 得到科室
     * @return String
     */
    public String getDept() {
        return dept;
    }

    /**
     * 设置职别类别
     * @param posType String
     */
    public void setPosType(String posType) {
        this.posType = posType;
    }

    /**
     * 得到职别类别
     * @return String
     */
    public String getPosType() {
        return posType;
    }

    /**
     * 设置科室等级
     * @param deptGrade String
     */
    public void setDeptGrade(String deptGrade) {
        this.deptGrade = deptGrade;
    }

    /**
     * 得到科室等级
     * @return String
     */
    public String getDeptGrade() {
        return deptGrade;
    }

    /**
     * 设置科室归类
     * @param classify String
     */
    public void setClassify(String classify) {
        this.classify = classify;
    }

    /**
     * 得到科室归类
     * @return String
     */
    public String getClassify() {
        return classify;
    }

    /**
     * 设置最小科室
     * @param finalFlg boolean '' 全部 Y 最小科室 N 不是最下科室
     */
    public void setFinalFlg(String finalFlg) {
        this.finalFlg = finalFlg;
    }

    /**
     * 是否是最小科室
     * @return String
     */
    public String getFinalFlg() {
        return finalFlg;
    }

    /**
     * 设置门诊适用
     * @param opdFitFlg String
     */
    public void setOpdFitFlg(String opdFitFlg) {
        this.opdFitFlg = opdFitFlg;
    }

    /**
     * 得到门诊适用
     * @return String
     */
    public String getOpdFitFlg() {
        return opdFitFlg;
    }

    /**
     * 设置急诊适用
     * @param emgFitFlg String
     */
    public void setEmgFitFlg(String emgFitFlg) {
        this.emgFitFlg = emgFitFlg;
    }

    /**
     * 得到急诊适用
     * @return String
     */
    public String getEmgFitFlg() {
        return emgFitFlg;
    }

    /**
     * 设置住院适用
     * @param ipdFitFlg String
     */
    public void setIpdFitFlg(String ipdFitFlg) {
        this.ipdFitFlg = ipdFitFlg;
    }

    /**
     * 得到住院适用
     * @return String
     */
    public String getIpdFitFlg() {
        return ipdFitFlg;
    }

    /**
     * 设置健检适用
     * @param hrmFitFlg String
     */
    public void setHrmFitFlg(String hrmFitFlg) {
        this.hrmFitFlg = hrmFitFlg;
    }

    /**
     * 得到健检适用
     * @return String
     */
    public String getHrmFitFlg() {
        return hrmFitFlg;
    }

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("DEPT_CODE", getTagValue(getDept()));
        parm.setDataN("DEPT_GRADE", getTagValue(getDeptGrade()));
        parm.setDataN("POS_TYPE", getTagValue(getPosType()));
        parm.setDataN("CLASSIFY", getTagValue(getClassify()));
        parm.setDataN("FINAL_FLG", getTagValue(getFinalFlg()));
        parm.setDataN("OPD_FIT_FLG", getTagValue(getOpdFitFlg()));
        parm.setDataN("EMG_FIT_FLG", getTagValue(getEmgFitFlg()));
        parm.setDataN("IPD_FIT_FLG", getTagValue(getIpdFitFlg()));
        parm.setDataN("HRM_FIT_FLG", getTagValue(getHrmFitFlg()));
        String optRegion = Operator.getRegion();
        if(!"".equals(optRegion))
            parm.setData("REGION_CODE_ALL",optRegion);
        setModuleParm(parm);
        super.onQuery();
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
        object.setValue("Text", "TButton");
        object.setValue("showID", "Y");
        object.setValue("showName", "Y");
        object.setValue("showText", "N");
        object.setValue("showValue", "N");
        object.setValue("showPy1", "Y");
        object.setValue("showPy2", "Y");
        object.setValue("Editable", "Y");
        object.setValue("Tip", "人员");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmString", "");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "sys\\SYSOperatorModule.x";
    }

    public String getModuleMethodName() {
        return "initoperatorcode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("Dept", "String", "", "Left")); //增加科室筛选条件
        data.add(new TAttribute("PosType", "String", "", "Left"));
        data.add(new TAttribute("DeptGrade", "String", "", "Left"));
        data.add(new TAttribute("Classify", "String", "", "Left"));
        data.add(new TAttribute("FinalFlg", "String", "", "Left"));
        data.add(new TAttribute("OpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("EmgFitFlg", "String", "", "Left"));
        data.add(new TAttribute("IpdFitFlg", "String", "", "Left"));
        data.add(new TAttribute("HrmFitFlg", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("Dept".equalsIgnoreCase(name)) {
            setDept(value);
            getTObject().setValue("Dept", value);
            return;
        }
        if ("DeptGrade".equalsIgnoreCase(name)) {
            setDeptGrade(value);
            getTObject().setValue("DeptGrade", value);
            return;
        }
        if ("PosType".equalsIgnoreCase(name)) {
            setPosType(value);
            getTObject().setValue("PosType", value);
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
