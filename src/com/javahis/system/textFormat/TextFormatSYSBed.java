package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;
import jdo.sys.Operator;

/**
 * <p>Title: 床位下拉列表</p>
 *
 * <p>Description: 床位下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.16
 * @version 1.0
 */
public class TextFormatSYSBed
    extends TTextFormat {
    /**
     * 病房
     */
    private String roomCode; //ROOM_CODE
    /**
     * 病区/护士站
     */
    private String stationCode; //STATION_CODE
    /**
     * 区域代码
     */
    private String regionCode; //REGION_CODE
    /**
     * 床位等级
     */
    private String bedClassCode; //BED_CLASS_CODE
    /**
     * 床位类别
     */
    private String bedTypeCode; //BED_TYPE_CODE
    /**
     * 启用注记
     */
    private String activeFlg; //ACTIVE_FLG
    /**
     * 预约注记
     */
    private String apptFlg; //APPT_FLG
    /**
     * 占床注记
     */
    private String alloFlg; //ALLO_FLG
    /**
     * 包床注记
     */
    private String bedOccuFlg; //BED_OCCU_FLG
    /**
     * 保留注记
     */
    private String reserveBedFlg; //RESERVE_BED_FLG
    /**
     * 性别代码
     */
    private String sexCode; //SEX_CODE
    /**
     * 占床率计算
     */
    private String occuRateFlg; //OCCU_RATE_FLG
    /**
     * 开放医师预约
     */
    private String drApproveFlg; //DR_APPROVE_FLG
    /**
     * 婴儿床注记
     */
    private String babyBedFlg; //BABY_BED_FLG
    /**
     * 入酒店管理系统
     */
    private String htlFlg; //HTL_FLG
    /**
     * 门急住别
     */
    private String admType; //ADM_TYPE
    /**
     * 床位状态
     */
    private String bedStatus; //BED_STATUS
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

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT BED_NO AS ID,BED_NO_DESC AS NAME,ENG_DESC AS ENNAME,PY1,PY2  " +
            "   FROM SYS_BED ";
        String sql1 = " ORDER BY SEQ,BED_NO ";

        StringBuffer sb = new StringBuffer();

        String roomCode = TypeTool.getString(getTagValue(getRoomCode()));
        if (roomCode != null && roomCode.length() > 0)
            sb.append(" ROOM_CODE = '" + roomCode + "' ");

        String stationCode = TypeTool.getString(getTagValue(getStationCode()));
        if (stationCode != null && stationCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" STATION_CODE = '" + stationCode + "' ");
        }

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + regionCode + "' ");
        }

        String bedClassCode = TypeTool.getString(getTagValue(getBedClassCode()));
        if (bedClassCode != null && bedClassCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BED_CLASS_CODE = '" + bedClassCode + "' ");
        }

        String bedTypeCode = TypeTool.getString(getTagValue(getBedTypeCode()));
        if (bedTypeCode != null && bedTypeCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BED_TYPE_CODE = '" + bedTypeCode + "' ");
        }

        String activeFlg = TypeTool.getString(getTagValue(getActiveFlg()));
        if (activeFlg != null && activeFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ACTIVE_FLG = '" + activeFlg + "' ");
        }

        String apptFlg = TypeTool.getString(getTagValue(getApptFlg()));
        if (apptFlg != null && apptFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" APPT_FLG = '" + apptFlg + "' ");
        }

        String alloFlg = TypeTool.getString(getTagValue(getAlloFlg()));
        if (alloFlg != null && alloFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ALLO_FLG = '" + alloFlg + "' ");
        }

        String bedOccuFlg = TypeTool.getString(getTagValue(getBedOccuFlg()));
        if (bedOccuFlg != null && bedOccuFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BED_OCCU_FLG = '" + bedOccuFlg + "' ");
        }
        String reserveBedFlg = TypeTool.getString(getTagValue(getReserveBedFlg()));
        if (reserveBedFlg != null && reserveBedFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" RESERVE_BED_FLG = '" + reserveBedFlg + "' ");
        }
        String sexCode = TypeTool.getString(getTagValue(getSexCode()));
        if (sexCode != null && sexCode.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" SEX_CODE = '" + sexCode + "' ");
        }
        String occuRateFlg = TypeTool.getString(getTagValue(getOccuRateFlg()));
        if (occuRateFlg != null && occuRateFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" OCCU_RATE_FLG = '" + occuRateFlg + "' ");
        }
        String drApproveFlg = TypeTool.getString(getTagValue(getDrApproveFlg()));
        if (drApproveFlg != null && drApproveFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" DR_APPROVE_FLG = '" + drApproveFlg + "' ");
        }
        String babyBedFlg = TypeTool.getString(getTagValue(getBabyBedFlg()));
        if (babyBedFlg != null && babyBedFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BABY_BED_FLG = '" + babyBedFlg + "' ");
        }
        String htlFlg = TypeTool.getString(getTagValue(getHtlFlg()));
        if (htlFlg != null && htlFlg.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" HTL_FLG = '" + htlFlg + "' ");
        }
        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ADM_TYPE = '" + admType + "' ");
        }
        String bedStatus = TypeTool.getString(getTagValue(getBedStatus()));
        if (bedStatus != null && bedStatus.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" BED_STATUS = '" + bedStatus + "' ");
        }
        //===========pangben modify 20110630 start
        if (Operator.getRegion() != null && Operator.getRegion().length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + Operator.getRegion() + "' ");
        }
        //===========pangben modify 20110630 stop
        if (sb.length() > 0)
            sql += " WHERE " + sb.toString() + sql1;
        else
            sql = sql + sql1;
//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//    System.out.println("sql"+sql);
        return sql;
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
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "代码,100;名称,100");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ID,1;NAME,1;PY1,1");
//        object.setValue("ShowColumnList", "NAME");
//        object.setValue("ValueColumn", "ID");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "床位");
        object.setValue("ShowColumnList", "ID;NAME");
    }
    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ID,1;NAME,3;ENNAME,3;PY1,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name");
    }

    /**
     * 显示区域列明
     * @return String
     */
    public String getPopupMenuHeader() {
        return "代码,100;名称,200";
    }

    /**
     * 增加扩展属性
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("RoomCode", "String", "", "Left"));
        data.add(new TAttribute("StationCode", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("BedClassCode", "String", "", "Left"));
        data.add(new TAttribute("BedTypeCode", "String", "", "Left"));
        data.add(new TAttribute("ActiveFlg", "String", "", "Left"));
        data.add(new TAttribute("ApptFlg", "String", "", "Left"));
        data.add(new TAttribute("AlloFlg", "String", "", "Left"));
        data.add(new TAttribute("BedOccuFlg", "String", "", "Left"));
        data.add(new TAttribute("ReserveBedFlg", "String", "", "Left"));
        data.add(new TAttribute("SexCode", "String", "", "Left"));
        data.add(new TAttribute("OccuRateFlg", "String", "", "Left"));
        data.add(new TAttribute("DrApproveFlg", "String", "", "Left"));
        data.add(new TAttribute("BabyBedFlg", "String", "", "Left"));
        data.add(new TAttribute("HtlFlg", "String", "", "Left"));
        data.add(new TAttribute("AdmType", "String", "", "Left"));
        data.add(new TAttribute("BedStatus", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("RoomCode".equalsIgnoreCase(name)) {
            setRoomCode(value);
            getTObject().setValue("RoomCode", value);
            return;
        }
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
        if ("BedClassCode".equalsIgnoreCase(name)) {
            setBedClassCode(value);
            getTObject().setValue("BedClassCode", value);
            return;
        }
        if ("BedTypeCode".equalsIgnoreCase(name)) {
            setBedTypeCode(value);
            getTObject().setValue("BedTypeCode", value);
            return;
        }
        if ("ActiveFlg".equalsIgnoreCase(name)) {
            setActiveFlg(value);
            getTObject().setValue("ActiveFlg", value);
            return;
        }
        if ("ApptFlg".equalsIgnoreCase(name)) {
            setApptFlg(value);
            getTObject().setValue("ApptFlg", value);
            return;
        }
        if ("AlloFlg".equalsIgnoreCase(name)) {
            setAlloFlg(value);
            getTObject().setValue("AlloFlg", value);
            return;
        }
        if ("BedOccuFlg".equalsIgnoreCase(name)) {
            setBedOccuFlg(value);
            getTObject().setValue("BedOccuFlg", value);
            return;
        }
        if ("ReserveBedFlg".equalsIgnoreCase(name)) {
            setReserveBedFlg(value);
            getTObject().setValue("ReserveBedFlg", value);
            return;
        }
        if ("SexCode".equalsIgnoreCase(name)) {
            setSexCode(value);
            getTObject().setValue("SexCode", value);
            return;
        }
        if ("OccuRateFlg".equalsIgnoreCase(name)) {
            setOccuRateFlg(value);
            getTObject().setValue("OccuRateFlg", value);
            return;
        }
        if ("DrApproveFlg".equalsIgnoreCase(name)) {
            setDrApproveFlg(value);
            getTObject().setValue("DrApproveFlg", value);
            return;
        }
        if ("BabyBedFlg".equalsIgnoreCase(name)) {
            setBabyBedFlg(value);
            getTObject().setValue("BabyBedFlg", value);
            return;
        }
        if ("HtlFlg".equalsIgnoreCase(name)) {
            setHtlFlg(value);
            getTObject().setValue("HtlFlg", value);
            return;
        }
        if ("AdmType".equalsIgnoreCase(name)) {
            setAdmType(value);
            getTObject().setValue("AdmType", value);
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
