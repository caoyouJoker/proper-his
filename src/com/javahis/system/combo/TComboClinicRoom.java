package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;

/**
 *
 * <p>Title:诊室下拉列表 </p>
 *
 * <p>Description:诊室下拉列表 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.28
 * @version 1.0
 */
public class TComboClinicRoom
    extends TComboBox {
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 诊区
     */
    private String clinicArea;
    /**
     * 设置诊区
     * @param clinicArea String
     */
    public void setClinicArea(String clinicArea) {
        this.clinicArea = clinicArea;
    }
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 得到诊区
     * @return String
     */
    public String getClinicArea() {
        return clinicArea;
    }
    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("CLINICAREA_CODE", getTagValue(getClinicArea()));
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
        object.setValue("Tip", "诊室");
        object.setValue("TableShowList", "id,name");
        object.setValue("ModuleParmTag", "");
    }

    public String getModuleName() {
        return "reg\\REGClinicRoomModule.x";
    }

    public String getModuleMethodName() {
        return "initclinicroomno";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ClinicArea", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("ClinicArea".equalsIgnoreCase(name)) {
            setClinicArea(value);
            getTObject().setValue("ClinicArea", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
