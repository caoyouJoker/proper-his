package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;
/**
 *
 * <p>Title:药房下拉列表 </p>
 *
 * <p>Description:药房下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.27
 * @version 1.0
 */
public class TComboOrgCode extends TComboBox{
    /**
     * 库房标记
     */
    private String orgFlg;//ORG_FLG
    /**
     * 库房类型
     */
    private String orgType;
    /**
     * 科室请领注记
     */
    private String exinvFlg;//EXINV_FLG
    /**
     * 护士站注记
     */
    private String stationFlg;//STATION_FLG
    /**
     * 静脉点滴注记
     */
    private String injOrgFlg;//INJ_ORG_FLG
    /**
     * 设置库房类型
     * @param orgType String
     */
    public void setOrgType(String orgType)
    {
        this.orgType = orgType;
    }
    /**
     * 得到库房类型
     * @return String
     */
    public String getOrgType()
    {
        return orgType;
    }
    /**
     * 设置库房标记
     * @param orgFlg String
     */
    public void setOrgFlg(String orgFlg){
        this.orgFlg = orgFlg;
    }
    /**
     * 设置科室请领注记
     * @param exinvFlg String
     */
    public void setExinvFlg(String exinvFlg) {
        this.exinvFlg = exinvFlg;
    }
    /**
     * 设置静脉点滴注记
     * @param injOrgFlg String
     */
    public void setInjOrgFlg(String injOrgFlg) {
        this.injOrgFlg = injOrgFlg;
    }
    /**
     * 设置护士站注记
     * @param stationFlg String
     */
    public void setStationFlg(String stationFlg) {
        this.stationFlg = stationFlg;
    }

    /**
     * 得到库房标记
     * @return String
     */
    public String getOrgFlg(){
        return orgFlg;
    }
    /**
     * 得到科室请领注记
     * @return String
     */
    public String getExinvFlg() {
        return exinvFlg;
    }
    /**
     * 得到静脉点滴注记
     * @return String
     */
    public String getInjOrgFlg() {
        return injOrgFlg;
    }
    /**
     * 得到护士站注记
     * @return String
     */
    public String getStationFlg() {
        return stationFlg;
    }

    /**
     * 执行Module动作
     */
    public void onQuery()
    {
        TParm parm = new TParm();
        parm.setDataN("ORG_TYPE",getTagValue(getOrgType()));
        parm.setDataN("ORG_FLG",getTagValue(getOrgFlg()));
        parm.setDataN("EXINV_FLG",getTagValue(this.getExinvFlg()));
        parm.setDataN("STATION_FLG",getTagValue(this.getStationFlg()));
        parm.setDataN("INJ_ORG_FLG",getTagValue(this.getInjOrgFlg()));
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
        object.setValue("Tip","药房");
        object.setValue("TableShowList","id,name");
        object.setValue("ModuleParmTag","");
    }
    public String getModuleName()
    {
        return "ind\\INDOrgModule.x";
    }
    public String getModuleMethodName()
    {
        return "getOrgCode";
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
        data.add(new TAttribute("OrgType","String","","Left"));
        data.add(new TAttribute("OrgFlg","String","","Left"));
        data.add(new TAttribute("ExinvFlg","String","","Left"));
        data.add(new TAttribute("InjOrgFlg","String","","Left"));
        data.add(new TAttribute("StationFlg","String","","Left"));
    }
    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name,String value)
    {
        if("OrgType".equalsIgnoreCase(name))
        {
            setOrgType(value);
            getTObject().setValue("OrgType",value);
            return;
        }
        if("OrgFlg".equalsIgnoreCase(name))
        {
            setOrgFlg(value);
            getTObject().setValue("OrgFlg",value);
            return;
        }
        if("ExinvFlg".equalsIgnoreCase(name))
        {
            setExinvFlg(value);
            getTObject().setValue("ExinvFlg",value);
            return;
        }
        if("InjOrgFlg".equalsIgnoreCase(name))
        {
            setInjOrgFlg(value);
            getTObject().setValue("InjOrgFlg",value);
            return;
        }
        if("StationFlg".equalsIgnoreCase(name))
        {
            setStationFlg(value);
            getTObject().setValue("StationFlg",value);
            return;
        }
        super.setAttribute(name,value);
    }
}
