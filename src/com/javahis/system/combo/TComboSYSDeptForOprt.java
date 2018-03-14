package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;
/**
 *
 * <p>Title: 科室下拉列表(处置专用)</p>
 *
 * <p>Description: 科室下拉列表(处置专用)</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.05.11
 * @version 1.0
 */
public class TComboSYSDeptForOprt extends TComboBox{
    /**
     * 科室等级
     */
    private String grade;
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
     * 人员
     */
    private String userID;
    /**
     * 设置科室等级
     * @param grade String
     */
    public void setGrade(String grade)
    {
        this.grade = grade;
    }
    /**
     * 得到科室等级
     * @return String
     */
    public String getGrade()
    {
        return grade;
    }
    /**
     * 设置最小科室
     * @param finalFlg boolean '' 全部 Y 最小科室 N 不是最下科室
     */
    public void setFinalFlg(String finalFlg)
    {
        this.finalFlg = finalFlg;
    }
    /**
     * 是否是最小科室
     * @return String
     */
    public String getFinalFlg()
    {
        return finalFlg;
    }
    /**
     * 设置门诊适用
     * @param opdFitFlg String
     */
    public void setOpdFitFlg(String opdFitFlg)
    {
        this.opdFitFlg = opdFitFlg;
    }
    /**
     * 得到门诊适用
     * @return String
     */
    public String getOpdFitFlg()
    {
        return opdFitFlg;
    }
    /**
     * 设置急诊适用
     * @param emgFitFlg String
     */
    public void setEmgFitFlg(String emgFitFlg)
    {
        this.emgFitFlg = emgFitFlg;
    }
    /**
     * 得到急诊适用
     * @return String
     */
    public String getEmgFitFlg()
    {
        return emgFitFlg;
    }
    /**
     * 设置住院适用
     * @param ipdFitFlg String
     */
    public void setIpdFitFlg(String ipdFitFlg)
    {
        this.ipdFitFlg = ipdFitFlg;
    }
    /**
     * 得到住院适用
     * @return String
     */
    public String getIpdFitFlg()
    {
        return ipdFitFlg;
    }
    /**
     * 设置健检适用
     * @param hrmFitFlg String
     */
    public void setHrmFitFlg(String hrmFitFlg)
    {
        this.hrmFitFlg = hrmFitFlg;
    }
    /**
     * 得到健检适用
     * @return String
     */
    public String getHrmFitFlg()
    {
        return hrmFitFlg;
    }
    /**
     * 设置人员
     * @param userID String
     */
    public void setUserID(String userID)
    {
        this.userID = userID;
    }
    /**
     * 得到人员
     * @return String
     */
    public String getUserID()
    {
        return userID;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("DEPT_GRADE",getTagValue(getGrade()));
        parm.setDataN("FINAL_FLG",getTagValue(getFinalFlg()));
        parm.setDataN("OPD_FIT_FLG",getTagValue(getOpdFitFlg()));
        parm.setDataN("EMG_FIT_FLG",getTagValue(getEmgFitFlg()));
        parm.setDataN("IPD_FIT_FLG",getTagValue(getIpdFitFlg()));
        parm.setDataN("HRM_FIT_FLG",getTagValue(getHrmFitFlg()));
        parm.setDataN("USER_ID",getTagValue(getUserID()));
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
    public void createInit(TObject object)
    {
        if(object == null)
            return;
        object.setValue("Width","81");
        object.setValue("Height","23");
        object.setValue("Text","TButton");
        object.setValue("showID","Y");
        object.setValue("showName","Y");
        object.setValue("showText","N");
        object.setValue("showValue","N");
        object.setValue("showPy1","Y");
        object.setValue("showPy2","Y");
        object.setValue("Editable","Y");
        object.setValue("Tip","科室");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "sys\\SYSDeptModule.x";
    }
    public String getModuleMethodName()
    {
        return "selDeptForOprt";
    }
    public String getParmMap()
    {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }
    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data){
        data.add(new TAttribute("Grade","String","","Left"));
        data.add(new TAttribute("FinalFlg","String","","Left"));
        data.add(new TAttribute("OpdFitFlg","String","","Left"));
        data.add(new TAttribute("EmgFitFlg","String","","Left"));
        data.add(new TAttribute("IpdFitFlg","String","","Left"));
        data.add(new TAttribute("HrmFitFlg","String","","Left"));
        data.add(new TAttribute("UserID","String","","Left"));


    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("Grade".equalsIgnoreCase(name))
        {
            setGrade(value);
            getTObject().setValue("Grade",value);
            return;
        }
        if("FinalFlg".equalsIgnoreCase(name))
        {
            setFinalFlg(value);
            getTObject().setValue("FinalFlg",value);
            return;
        }
        if("OpdFitFlg".equalsIgnoreCase(name))
        {
            setOpdFitFlg(value);
            getTObject().setValue("OpdFitFlg",value);
            return;
        }
        if("EmgFitFlg".equalsIgnoreCase(name))
        {
            setEmgFitFlg(value);
            getTObject().setValue("EmgFitFlg",value);
            return;
        }
        if("IpdFitFlg".equalsIgnoreCase(name))
        {
            setIpdFitFlg(value);
            getTObject().setValue("IpdFitFlg",value);
            return;
        }
        if("HrmFitFlg".equalsIgnoreCase(name))
        {
            setHrmFitFlg(value);
            getTObject().setValue("HrmFitFlg",value);
            return;
        }
        if("UserID".equalsIgnoreCase(name))
        {
            setUserID(value);
            getTObject().setValue("UserID",value);
            return;
        }
        super.setAttribute(name,value);
    }}
