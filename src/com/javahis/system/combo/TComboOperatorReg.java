package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import jdo.sys.Operator;
/**
 *
 * <p>Title:门急诊适用人员下拉列表 </p>
 *
 * <p>Description:门急诊适用人员下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.28
 * @version 1.0
 */
public class TComboOperatorReg extends TComboBox{
    /**
     * 区域
     */
    private String region;
    /**
     * 门急别
     */
    private String admType;
    /**
     * 出诊日期
     */
    private String admDate;
    /**
     * 时段
     */
    private String sessionCode;
    /**
     * 科室
     */
    private String deptCode;
    /**
     * 诊室
     */
    private String clinicroomNo;
    /**
     * 号别
     */
    private String clinicType;
    /**
     * 设置区域
     * @param region String
     */
    public void setRegion(String region)
    {
        this.region = region;
    }
    /**
     * 得到区域
     * @return String
     */
    public String getRegion()
    {
        return region;
    }
    /**
     * 设置门急别
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * 得到门急别
     * @return String
     */
    public String getAdmType()
    {
        return admType;
    }
    /**
     * 设置时段
     * @param sessionCode String
     */
    public void setSessionCode(String sessionCode)
    {
        this.sessionCode = sessionCode;
    }
    /**
     * 得到时段
     * @return String
     */
    public String getSessionCode()
    {
        return sessionCode;
    }
    /**
     * 设置出诊日期
     * @param admDate String
     */
    public void setAdmDate(String admDate)
    {
        this.admDate = admDate;
    }
    /**
     * 得到出诊日期
     * @return String
     */
    public String getAdmDate()
    {
        return admDate;
    }
    /**
     * 设置科室
     * @param deptCode String
     */
    public void setDeptCode(String deptCode)
    {
        this.deptCode = deptCode;
    }
    /**
     * 得到科室
     * @return String
     */
    public String getDeptCode()
    {
        return deptCode;
    }
    /**
     * 设置诊室
     * @param clinicroomNo String
     */
    public void setClinicroomNo(String clinicroomNo)
    {
        this.clinicroomNo = clinicroomNo;
    }
    /**
     * 得到诊室
     * @return String
     */
    public String getClinicroomNo()
    {
        return clinicroomNo;
    }
    /**
     * 设置号别
     * @param clinicType String
     */
    public void setClinicType(String clinicType)
    {
        this.clinicType = clinicType;
    }
    /**
     * 得到号别
     * @return String
     */
    public String getClinicType()
    {
        return clinicType;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE",getTagValue(getRegion()));
        parm.setDataN("ADM_TYPE",getTagValue(getAdmType()));
        parm.setDataN("SESSION_CODE",getTagValue(getSessionCode()));
        Object value =getTagValue(getAdmDate());
        if(value instanceof Timestamp)
            parm.setDataN("ADM_DATE",StringTool.getString(TCM_Transform.getTimestamp(value),"yyyyMMdd") );
        else
        parm.setDataN("ADM_DATE",getTagValue(getAdmDate()));
        parm.setDataN("DEPT_CODE",getTagValue(getDeptCode()));
        parm.setDataN("CLINICROOM_NO",getTagValue(getClinicroomNo()));
        parm.setDataN("CLINICTYPE_CODE",getTagValue(getClinicType()));
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
        object.setValue("Tip","门急诊适用人员");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "sys\\SYSOperatorModule.x";
    }
    public String getModuleMethodName()
    {
        return "initoperatreg";
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
        data.add(new TAttribute("Region","String","","Left"));
        data.add(new TAttribute("AdmType","String","","Left"));
        data.add(new TAttribute("SessionCode","String","","Left"));
        data.add(new TAttribute("AdmDate","String","","Left"));
        data.add(new TAttribute("DeptCode","String","","Left"));
        data.add(new TAttribute("ClinicroomNo","String","","Left"));
        data.add(new TAttribute("ClinicType","String","","Left"));
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("Region".equalsIgnoreCase(name))
        {
            setRegion(value);
            getTObject().setValue("Region",value);
            return;
        }
        if("AdmType".equalsIgnoreCase(name))
        {
            setAdmType(value);
            getTObject().setValue("AdmType",value);
            return;
        }
        if("SessionCode".equalsIgnoreCase(name))
        {
            setSessionCode(value);
            getTObject().setValue("SessionCode",value);
            return;
        }
        if("AdmDate".equalsIgnoreCase(name))
        {
            setAdmDate(value);
            getTObject().setValue("AdmDate",value);
            return;
        }
        if("DeptCode".equalsIgnoreCase(name))
        {
            setDeptCode(value);
            getTObject().setValue("DeptCode",value);
            return;
        }
        if("ClinicroomNo".equalsIgnoreCase(name))
        {
            setClinicroomNo(value);
            getTObject().setValue("ClinicroomNo",value);
            return;
        }
        if("ClinicType".equalsIgnoreCase(name))
        {
            setClinicType(value);
            getTObject().setValue("ClinicType",value);
            return;
        }

        super.setAttribute(name,value);
    }
}
