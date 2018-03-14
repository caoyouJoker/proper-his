package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.data.TParm;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import jdo.sys.Operator;

/**
 * <p>Title: 住院医生站</p>
 *
 * <p>Description: 病区COMBO</p>
 *
 * <p>Copyright: Copyright (c) 2008 JavaHis</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author Miracle
 * @version JavaHis 1.0
 */
public class TComboSYSStationCode extends TComboBox {
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 科室
     */
    private String deptCode;
    /**
     * 预设药房
     */
    private String orgCode;
    /**
     * 位置
     */
    private String locCode;
    /**
     * 得到科室
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }
    /**
     * 得到位置
     * @return String
     */
    public String getLocCode() {
        return locCode;
    }
    /**
     * 得到药房
     * @return String
     */
    public String getOrgCode() {
        return orgCode;
    }
    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }
    /**
     * 设置科室
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }
    /**
     * 设置位置
     * @param locCode String
     */
    public void setLocCode(String locCode) {
        this.locCode = locCode;
    }
    /**
     * 设置药房
     * @param orgCode String
     */
    public void setOrgCode(String orgCode) {
        this.orgCode = orgCode;
    }
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE",getTagValue(getRegionCode()));
        parm.setDataN("DEPT_CODE",getTagValue(getDeptCode()));
        parm.setDataN("ORG_CODE",getTagValue(getOrgCode()));
        parm.setDataN("LOC_CODE",getTagValue(getLocCode()));
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
       object.setValue("Tip", "病区");
       object.setValue("TableShowList", "id,name");
   }

   public String getModuleName() {
       return "sys\\SYSStationModule.x";
   }

   public String getModuleMethodName() {
       return "getStation";
   }

   public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
   }
   /**
    * 增加扩展属性
    * @param data TAttributeList
    */
   public void getEnlargeAttributes(TAttributeList data){
       data.add(new TAttribute("RegionCode","String","","Left"));
       data.add(new TAttribute("DeptCode","String","","Left"));
       data.add(new TAttribute("OrgCode","String","","Left"));
       data.add(new TAttribute("LocCode","String","","Left"));
   }
   /**
    * 设置属性
    * @param name String 属性名
    * @param value String 属性值
    */
   public void setAttribute(String name,String value)
   {
       if("RegionCode".equalsIgnoreCase(name))
       {
           setRegionCode(value);
           getTObject().setValue("RegionCode",value);
           return;
       }
       if("DeptCode".equalsIgnoreCase(name))
       {
           setDeptCode(value);
           getTObject().setValue("DeptCode",value);
           return;
       }
       if("OrgCode".equalsIgnoreCase(name))
       {
           setOrgCode(value);
           getTObject().setValue("OrgCode",value);
           return;
       }
       if("LocCode".equalsIgnoreCase(name))
       {
           setLocCode(value);
           getTObject().setValue("LocCode",value);
           return;
       }
       super.setAttribute(name,value);
   }

}
