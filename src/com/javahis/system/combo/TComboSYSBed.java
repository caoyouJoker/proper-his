package com.javahis.system.combo;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.data.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import jdo.sys.Operator;
/**
 *
 * <p>Title: 床位下拉列表</p>
 *
 * <p>Description: 床位下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class TComboSYSBed extends TComboBox{
    /**
     * 病房
     */
    private String roomCode;//ROOM_CODE
    /**
     * 病区/护士站
     */
    private String stationCode;//STATION_CODE
    /**
     * 区域代码
     */
    private String regionCode;//REGION_CODE
    /**
     * 床位等级
     */
    private String bedClassCode;//BED_CLASS_CODE
    /**
     * 床位类别
     */
    private String bedTypeCode;//BED_TYPE_CODE
    /**
     * 启用注记
     */
    private String activeFlg;//ACTIVE_FLG
    /**
     * 预约注记
     */
    private String apptFlg;//APPT_FLG
    /**
     * 占床注记
     */
    private String alloFlg;//ALLO_FLG
    /**
     * 包床注记
     */
    private String bedOccuFlg;//BED_OCCU_FLG
    /**
     * 保留注记
     */
    private String reserveBedFlg;//RESERVE_BED_FLG
    /**
     * 性别代码
     */
    private String sexCode;//SEX_CODE
    /**
     * 占床率计算
     */
    private String occuRateFlg;//OCCU_RATE_FLG
    /**
     * 开放医师预约
     */
    private String drApproveFlg;//DR_APPROVE_FLG
    /**
     * 婴儿床注记
     */
    private String babyBedFlg;//BABY_BED_FLG
    /**
     * 入酒店管理系统
     */
    private String htlFlg;//HTL_FLG
    /**
     * 门急住别
     */
    private String admType;//ADM_TYPE
    /**
     * 床位状态
     */
    private String bedStatus;//BED_STATUS

    /**
     * 初始化
     */
    public void onInit()
    {
        super.onInit();
    }
    /**
    * 执行Module动作
    */
   public void onQuery()
   {
       TParm parm = new TParm();
       parm.setDataN("ROOM_CODE",getTagValue(getRoomCode()));
       parm.setDataN("STATION_CODE",getTagValue(getStationCode()));
       parm.setDataN("REGION_CODE",getTagValue(getRegionCode()));
       parm.setDataN("BED_CLASS_CODE",getTagValue(getBedClassCode()));
       parm.setDataN("BED_TYPE_CODE",getTagValue(getBedTypeCode()));
       parm.setDataN("ACTIVE_FLG",getTagValue(getActiveFlg()));
       parm.setDataN("APPT_FLG",getTagValue(getApptFlg()));
       parm.setDataN("ALLO_FLG",getTagValue(getAlloFlg()));
       parm.setDataN("BED_OCCU_FLG",getTagValue(getBedOccuFlg()));
       parm.setDataN("RESERVE_BED_FLG",getTagValue(getReserveBedFlg()));
       parm.setDataN("SEX_CODE",getTagValue(getSexCode()));
       parm.setDataN("OCCU_RATE_FLG",getTagValue(getOccuRateFlg()));
       parm.setDataN("DR_APPROVE_FLG",getTagValue(getDrApproveFlg()));
       parm.setDataN("BABY_BED_FLG",getTagValue(getBabyBedFlg()));
       parm.setDataN("HTL_FLG",getTagValue(getHtlFlg()));
       parm.setDataN("ADM_TYPE",getTagValue(getAdmType()));
       parm.setDataN("BED_STATUS",getTagValue(getBedStatus()));
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
       object.setValue("Tip","床位");
       object.setValue("TableShowList","id,name");
       object.setValue("ModuleParmString","");
       object.setValue("ModuleParmTag","");
   }
   public String getModuleName()
   {
       return "sys\\SYSBedModule.x";
   }
   public String getModuleMethodName()
   {
       return "initSYSBed";
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
       data.add(new TAttribute("RoomCode","String","","Left"));
       data.add(new TAttribute("StationCode","String","","Left"));
       data.add(new TAttribute("RegionCode","String","","Left"));
       data.add(new TAttribute("BedClassCode","String","","Left"));
       data.add(new TAttribute("BedTypeCode","String","","Left"));
       data.add(new TAttribute("ActiveFlg","String","","Left"));
       data.add(new TAttribute("ApptFlg","String","","Left"));
       data.add(new TAttribute("AlloFlg","String","","Left"));
       data.add(new TAttribute("BedOccuFlg","String","","Left"));
       data.add(new TAttribute("ReserveBedFlg","String","","Left"));
       data.add(new TAttribute("SexCode","String","","Left"));
       data.add(new TAttribute("OccuRateFlg","String","","Left"));
       data.add(new TAttribute("DrApproveFlg","String","","Left"));
       data.add(new TAttribute("BabyBedFlg","String","","Left"));
       data.add(new TAttribute("HtlFlg","String","","Left"));
       data.add(new TAttribute("AdmType","String","","Left"));
       data.add(new TAttribute("BedStatus","String","","Left"));
}
   /**
    * 设置属性
    * @param name String 属性名
    * @param value String 属性值
    */
   public void setAttribute(String name,String value)
   {
       if("RoomCode".equalsIgnoreCase(name))
       {
           setRoomCode(value);
           getTObject().setValue("RoomCode",value);
           return;
       }
       if("StationCode".equalsIgnoreCase(name))
       {
           setStationCode(value);
           getTObject().setValue("StationCode",value);
           return;
       }
       if("RegionCode".equalsIgnoreCase(name))
       {
           setRegionCode(value);
           getTObject().setValue("RegionCode",value);
           return;
       }
       if("BedClassCode".equalsIgnoreCase(name))
       {
           setBedClassCode(value);
           getTObject().setValue("BedClassCode",value);
           return;
       }
       if("BedTypeCode".equalsIgnoreCase(name))
       {
           setBedTypeCode(value);
           getTObject().setValue("BedTypeCode",value);
           return;
       }
       if("ActiveFlg".equalsIgnoreCase(name))
       {
           setActiveFlg(value);
           getTObject().setValue("ActiveFlg",value);
           return;
       }
       if("ApptFlg".equalsIgnoreCase(name))
       {
           setApptFlg(value);
           getTObject().setValue("ApptFlg",value);
           return;
       }
       if("AlloFlg".equalsIgnoreCase(name))
       {
           setAlloFlg(value);
           getTObject().setValue("AlloFlg",value);
           return;
       }
       if("BedOccuFlg".equalsIgnoreCase(name))
       {
           setBedOccuFlg(value);
           getTObject().setValue("BedOccuFlg",value);
           return;
       }
       if("ReserveBedFlg".equalsIgnoreCase(name))
       {
           setReserveBedFlg(value);
           getTObject().setValue("ReserveBedFlg",value);
           return;
       }
       if("SexCode".equalsIgnoreCase(name))
       {
           setSexCode(value);
           getTObject().setValue("SexCode",value);
           return;
       }
       if("OccuRateFlg".equalsIgnoreCase(name))
       {
           setOccuRateFlg(value);
           getTObject().setValue("OccuRateFlg",value);
           return;
       }
       if("DrApproveFlg".equalsIgnoreCase(name))
       {
           setDrApproveFlg(value);
           getTObject().setValue("DrApproveFlg",value);
           return;
       }
       if("BabyBedFlg".equalsIgnoreCase(name))
       {
           setBabyBedFlg(value);
           getTObject().setValue("BabyBedFlg",value);
           return;
       }
       if("HtlFlg".equalsIgnoreCase(name))
       {
           setHtlFlg(value);
           getTObject().setValue("HtlFlg",value);
           return;
       }
       if("AdmType".equalsIgnoreCase(name))
       {
           setAdmType(value);
           getTObject().setValue("AdmType",value);
           return;
       }
       if("BedStatus".equalsIgnoreCase(name))
       {
           setBedStatus(value);
           getTObject().setValue("BedStatus",value);
           return;
       }
       super.setAttribute(name,value);
   }
   /**
    * 得到病房
    * @return String
    */
   public String getRoomCode() {
        return roomCode;
    }
    /**
     * 得到预约注记
     * @return String
     */
    public String getApptFlg() {
        return apptFlg;
    }
    /**
     * 得到婴儿床注记
     * @return String
     */
    public String getBabyBedFlg() {
        return babyBedFlg;
    }
    /**
     * 得到病床等级
     * @return String
     */
    public String getBedClassCode() {
        return bedClassCode;
    }
    /**
     * 得到包床注记
     * @return String
     */
    public String getBedOccuFlg() {
        return bedOccuFlg;
    }
    /**
     * 得到床位状态
     * @return String
     */
    public String getBedStatus() {
        return bedStatus;
    }
    /**
     * 得到床位类别
     * @return String
     */
    public String getBedTypeCode() {
        return bedTypeCode;
    }
    /**
     * 得到开放医师预约
     * @return String
     */
    public String getDrApproveFlg() {
        return drApproveFlg;
    }
    /**
     * 得到入酒店管理系统
     * @return String
     */
    public String getHtlFlg() {
        return htlFlg;
    }
    /**
     * 得到占床率计算
     * @return String
     */
    public String getOccuRateFlg() {
        return occuRateFlg;
    }
    /**
     * 得到区域代码
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }
    /**
     * 得到保留床
     * @return String
     */
    public String getReserveBedFlg() {
        return reserveBedFlg;
    }
    /**
     * 得到性别代码
     * @return String
     */
    public String getSexCode() {
        return sexCode;
    }
    /**
     * 得到病区/护士站
     * @return String
     */
    public String getStationCode() {
        return stationCode;
    }
    /**
     * 得到启用注记
     * @return String
     */
    public String getActiveFlg() {
        return activeFlg;
    }
    /**
     * 得到门急住别
     * @return String
     */
    public String getAdmType() {
        return admType;
    }
    /**
     * 得到占床注记
     * @return String
     */
    public String getAlloFlg() {
        return alloFlg;
    }

    /**
     * 设置病房
     * @param roomCode String
     */
    public void setRoomCode(String roomCode) {
        this.roomCode = roomCode;
    }
    /**
     * 设置占床率计算
     * @param occuRateFlg String
     */
    public void setOccuRateFlg(String occuRateFlg) {
        this.occuRateFlg = occuRateFlg;
    }
    /**
     * 设置入酒店管理系统
     * @param htlFlg String
     */
    public void setHtlFlg(String htlFlg) {
        this.htlFlg = htlFlg;
    }
    /**
     * 设置开放医师预约
     * @param drApproveFlg String
     */
    public void setDrApproveFlg(String drApproveFlg) {
        this.drApproveFlg = drApproveFlg;
    }
    /**
     * 设置床位类别
     * @param bedTypeCode String
     */
    public void setBedTypeCode(String bedTypeCode) {
        this.bedTypeCode = bedTypeCode;
    }
    /**
     * 设置包床注记
     * @param bedOccuFlg String
     */
    public void setBedOccuFlg(String bedOccuFlg) {
        this.bedOccuFlg = bedOccuFlg;
    }
    /**
     * 设置床位等级
     * @param bedClassCode String
     */
    public void setBedClassCode(String bedClassCode) {
        this.bedClassCode = bedClassCode;
    }
    /**
     * 设置婴儿床注记
     * @param babyBedFlg String
     */
    public void setBabyBedFlg(String babyBedFlg) {
        this.babyBedFlg = babyBedFlg;
    }
    /**
     * 设置预约注记
     * @param apptFlg String
     */
    public void setApptFlg(String apptFlg) {
        this.apptFlg = apptFlg;
    }
    /**
     * 设置区域代码
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
    /**
     * 设置保留床
     * @param reserveBedFlg String
     */
    public void setReserveBedFlg(String reserveBedFlg) {
        this.reserveBedFlg = reserveBedFlg;
    }
    /**
     * 设置性别代码
     * @param sexCode String
     */
    public void setSexCode(String sexCode) {
        this.sexCode = sexCode;
    }
    /**
     * 设置病区/护士站
     * @param stationCode String
     */
    public void setStationCode(String stationCode) {
        this.stationCode = stationCode;
    }
    /**
     * 设置床位状态
     * @param bedStatus String
     */
    public void setBedStatus(String bedStatus) {
        this.bedStatus = bedStatus;
    }
    /**
     * 设置启用注记
     * @param activeFlg String
     */
    public void setActiveFlg(String activeFlg) {
        this.activeFlg = activeFlg;
    }
    /**
     * 设置门急住别
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
    }
    /**
     * 设置占床注记
     * @param alloFlg String
     */
    public void setAlloFlg(String alloFlg) {
        this.alloFlg = alloFlg;
    }
}
