package com.javahis.system.combo;

import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.data.TParm;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.TComboBox;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 *
 * <p>Title: 病房下拉列表</p>
 *
 * <p>Description: 病房下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSRoom extends TComboBox {
    /**
     * 病区
     */
    private String stationCode;
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 性别管制
     */
    private String sexLimitFlg;
    /**
     * 红色警戒
     */
    private int redSign;
    /**
     * 黄色警戒
     */
    private int yellowSign;
    /**
     * 设置病区
     * @param stationCode String
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }

    /**
     * 得到病区
     * @return String
     */
    public String getStationCode() {
        return stationCode;
    }

    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 设置性别管制
     * @param sexLimitFlg String
     */
    public void setSexLimitFlg(String sexLimitFlg) {
        this.sexLimitFlg = sexLimitFlg;
    }

    /**
     * 得到性别管制
     * @return String
     */
    public String getSexLimitFlg() {
        return sexLimitFlg;
    }

    /**
     * 设置红色警戒
     * @param redSign int
     */
    public void setRedSign(int redSign) {
        this.redSign = redSign;
    }

    /**
     * 得到红色警戒
     * @return int
     */
    public int getRedSign() {
        return redSign;
    }

    /**
     * 设置黄色警戒
     * @param yellowSign int
     */
    public void setYellowSign(int yellowSign) {
        this.yellowSign = yellowSign;
    }

    /**
     * 得到黄色警戒
     * @return int
     */
    public int getYellowSign() {
        return yellowSign;
    }

    /**
     * 执行Module动作
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setDataN("STATION_CODE", getTagValue(getStationCode()));
        parm.setDataN("REGION_CODE", getTagValue(getRegionCode()));
        parm.setDataN("SEX_LIMIT_FLG", getTagValue(getSexLimitFlg()));
        parm.setDataN("RED_SIGN", getRedSign());
        parm.setDataN("YELLOW_SIGN", getYellowSign());
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
        object.setValue("Tip", "病房");
        object.setValue("TableShowList", "id,name");
    }

    public String getModuleName() {
        return "sys\\SYSRoomModule.x";
    }

    public String getModuleMethodName() {
        return "initroomcode";
    }

    public String getParmMap() {
        return "id:ID;name:NAME;enname:ENNAME;Py1:PY1;py2:PY2";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("StationCode", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("SexLimitFlg", "String", "", "Left"));
        data.add(new TAttribute("RedSign", "int", "", "Left"));
        data.add(new TAttribute("YellowSign", "int", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("StationCode".equalsIgnoreCase(name)) {
            setStationCode(value);
            getTObject().setValue("StationCode", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        if ("SexLimitFlg".equalsIgnoreCase(name)) {
            setSexLimitFlg(value);
            getTObject().setValue("SexLimitFlg", value);
            return;
        }
        if ("RedSign".equalsIgnoreCase(name)) {
            setRedSign(TypeTool.getInt(value));
            getTObject().setValue("RedSign", value);
            return;
        }
        if ("YellowSign".equalsIgnoreCase(name)) {
            setYellowSign(TypeTool.getInt(value));
            getTObject().setValue("YellowSign", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
