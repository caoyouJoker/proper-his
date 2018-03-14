package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;
/**
 *
 * <p>Title:时段下拉列表 </p>
 *
 * <p>Description:时段下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2008.08.25
 * @version 1.0
 */
public class TComboSession extends TComboBox{
    /**
     * 门急别
     */
    private String admType;
    /**
     * 区域
     */
    private String region;
    /**
     * 设置门急别
     * @param admType String
     */
    public void setAdmType(String admType)
    {
        this.admType = admType;
    }
    /**
     * 设置区域
     * @param region String
     */
    public void setRegion(String region) {
        this.region = region;
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
     * 得到区域
     * @return String
     */
    public String getRegion() {
        return region;
    }

    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("ADM_TYPE",getTagValue(getAdmType()));
        parm.setDataN("REGION_CODE",getTagValue(getRegion()));
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
        object.setValue("Tip","时段");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmString","");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "reg\\REGSessionModule.x";
    }
    public String getModuleMethodName()
    {
        return "initsessioncode";
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
        data.add(new TAttribute("AdmType","String","","Left"));
        data.add(new TAttribute("Region","String","","Left"));
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("AdmType".equalsIgnoreCase(name))
        {
            setAdmType(value);
            getTObject().setValue("AdmType",value);
            return;
        }
        if("Region".equalsIgnoreCase(name))
        {
            setRegion(value);
            getTObject().setValue("Region",value);
            return;
        }
       super.setAttribute(name,value);
    }

}
