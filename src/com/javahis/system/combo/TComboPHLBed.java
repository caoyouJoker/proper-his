package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;

/**
 * <p>Title: 静点床位下拉列表</p>
 *
 * <p>Description: 静点床位下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.17
 * @version 1.0
 */
public class TComboPHLBed
    extends TComboBox {
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 床位状态
     */
    private String bedStatus;
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 设置床位状态
     * @param bedStatus String
     */
    public void setBedStatus(String bedStatus) {
        this.bedStatus = bedStatus;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 得到床位状态
     * @return String
     */
    public String getBedStatus() {
        return bedStatus;
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("REGION_CODE", getTagValue(getRegionCode()));
        parm.setDataN("BED_STATUS", getTagValue(getBedStatus()));
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
        object.setValue("Tip", "静点床位");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "phl\\PHLBedModule.x";
    }

    public String getModuleMethodName() {
        return "initCombo";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("BedStatus", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("BedStatus".equalsIgnoreCase(name)) {
            setBedStatus(value);
            getTObject().setValue("BedStatus", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
