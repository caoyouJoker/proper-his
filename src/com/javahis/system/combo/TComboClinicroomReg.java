package com.javahis.system.combo;

import com.dongyang.ui.TComboBox;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.edit.TAttributeList;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
/**
 *
 * <p>Title:门急诊适用诊室下拉列表 </p>
 *
 * <p>Description:门急诊适用诊室下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.28
 * @version 1.0
 */
public class TComboClinicroomReg extends TComboBox{
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
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("REGION_CDOE",getTagValue(getRegion()));
        parm.setDataN("ADM_TYPE",getTagValue(getAdmType()));
        parm.setDataN("SESSION_CODE",getTagValue(getSessionCode()));
        Object value =getTagValue(getAdmDate());
        if(value instanceof Timestamp)
            parm.setDataN("ADM_DATE",StringTool.getString(TCM_Transform.getTimestamp(value),"yyyyMMdd") );
        else
            parm.setDataN("ADM_DATE",getTagValue(getAdmDate()));
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
        object.setValue("Tip","诊室");
        object.setValue("TableShowList","id,name");
    }
    public String getModuleName()
    {
        return "reg\\REGClinicRoomModule.x";
    }
    public String getModuleMethodName()
    {
        return "initclinicroomreg";
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
        super.setAttribute(name,value);
    }
}
