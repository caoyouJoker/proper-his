package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;
import jdo.sys.Operator;

/**
 * <p>Title: 药房下拉列表</p>
 *
 * <p>Description: 药房下拉列表</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatINDOrg
    extends TTextFormat {
    /**
     * 库房标记
     */
    private String orgFlg;
    /**
     * 库房类型
     */
    private String orgType;
    /**
     * 科室请领注记
     */
    private String exinvFlg;
    /**
     * 护士站注记
     */
    private String stationFlg;
    /**
     * 静脉点滴注记
     */
    private String injOrgFlg;

    /**
     * 登录人员ID
     */
    private String operatorId; // OPERATOR_ID
    /**
     * 区域
     */
    private String regionCode;

    /**
     * 设置库房标记
     * @param orgFlg String
     */
    public void setOrgFlg(String orgFlg) {

        this.orgFlg = orgFlg;
        setModifySQL(true);
    }

    /**
     * 设置科室请领注记
     * @param exinvFlg String
     */
    public void setExinvFlg(String exinvFlg) {
        this.exinvFlg = exinvFlg;
        setModifySQL(true);
    }

    /**
     * 设置库房类型
     * @param orgType String
     */
    public void setOrgType(String orgType) {
        this.orgType = orgType;
        setModifySQL(true);
    }

    /**
     * 设置静脉点滴注记
     * @param injOrgFlg String
     */
    public void setInjOrgFlg(String injOrgFlg) {
        this.injOrgFlg = injOrgFlg;
        setModifySQL(true);
    }

    /**
     * 设置护士站注记
     * @param stationFlg String
     */
    public void setStationFlg(String stationFlg) {
        this.stationFlg = stationFlg;
        setModifySQL(true);
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
    }
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }

    /**
     * 得到库房标记
     * @return String
     */
    public String getOrgFlg() {
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
     * 得到库房类型
     * @return String
     */
    public String getOrgType() {
        return orgType;
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

    public String getOperatorId() {
        return operatorId;
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
     * @return String
     */
    public String getPopupMenuSQL() {
        String userId = TypeTool.getString(getTagValue(getOperatorId()));
        String sql = "";
        StringBuffer sb = new StringBuffer();
        if (userId == null || userId.length() == 0) {
            sql =
                " SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME,ORG_ENG_DESC AS ENNAME,PY1,PY2 FROM IND_ORG ";
            String sql1 = " ORDER BY ORG_CODE,SEQ ";
            String orgFlg = TypeTool.getString(getTagValue(getOrgFlg()));
            if (orgFlg != null && orgFlg.length() > 0)
                sb.append(" ORG_FLG = '" + orgFlg + "' ");
            String orgType = TypeTool.getString(getTagValue(getOrgType()));
            if (orgType != null && orgType.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" ORG_TYPE = '" + orgType + "' ");
            }
            String exinvFlg = TypeTool.getString(getTagValue(getExinvFlg()));
            if (exinvFlg != null && exinvFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" EXINV_FLG = '" + exinvFlg + "' ");
            }
            String stationFlg = TypeTool.getString(getTagValue(getStationFlg()));
            if (stationFlg != null && stationFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" STATION_FLG = '" + stationFlg + "' ");
            }
            String injOrgFlg = TypeTool.getString(getTagValue(getInjOrgFlg()));
            if (injOrgFlg != null && injOrgFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" INJ_ORG_FLG = '" + injOrgFlg + "' ");
            }
            String operatorCodeAll = Operator.getRegion();
            if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" REGION_CODE = '" + operatorCodeAll + "' ");
            }
            if (sb.length() > 0)
                sql += " WHERE " + sb.toString() + sql1;
            else
                sql = sql + sql1;
        }
        else {
            sql =
                " SELECT A.ORG_CODE AS ID, A.ORG_CHN_DESC AS NAME, A.PY1, A.PY2 "
                + " FROM IND_ORG A, SYS_OPERATOR_DEPT B "
                + " WHERE A.ORG_CODE = B.DEPT_CODE ";
            String sql1 = " ORDER BY A.ORG_CODE, A.SEQ ";
            String orgFlg = TypeTool.getString(getTagValue(getOrgFlg()));
            if (orgFlg != null && orgFlg.length() > 0)
                sb.append(" AND A.ORG_FLG = '" + orgFlg + "' ");

            String orgType = TypeTool.getString(getTagValue(getOrgType()));
            if (orgType != null && orgType.length() > 0) {
                sb.append(" AND A.ORG_TYPE = '" + orgType + "' ");
            }

            String exinvFlg = TypeTool.getString(getTagValue(getExinvFlg()));
            if (exinvFlg != null && exinvFlg.length() > 0) {
                sb.append(" AND A.EXINV_FLG = '" + exinvFlg + "' ");
            }

            String stationFlg = TypeTool.getString(getTagValue(getStationFlg()));
            if (stationFlg != null && stationFlg.length() > 0) {
                sb.append(" AND A.STATION_FLG = '" + stationFlg + "' ");
            }

            String injOrgFlg = TypeTool.getString(getTagValue(getInjOrgFlg()));
            if (injOrgFlg != null && injOrgFlg.length() > 0) {
                sb.append(" AND A.INJ_ORG_FLG = '" + injOrgFlg + "' ");
            }

            String operatorId = TypeTool.getString(getTagValue(getOperatorId()));
            if (operatorId != null && operatorId.length() > 0) {
                    sb.append("AND B.USER_ID = '" + operatorId + "' ");
            }
            String operatorCodeAll = Operator.getRegion();
            if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
                sb.append("  AND A.REGION_CODE = '" + operatorCodeAll + "' ");
            }

            if (sb.length() > 0)
                sql = sql + sb.toString() + sql1;
            else
                sql = sql + sql1;
        }
        //System.out.println("sql" + sql);
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "药房");
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
        data.add(new TAttribute("OrgFlg", "String", "", "Left"));
        data.add(new TAttribute("OrgType", "String", "", "Left"));
        data.add(new TAttribute("ExinvFlg", "String", "", "Left"));
        data.add(new TAttribute("StationFlg", "String", "", "Left"));
        data.add(new TAttribute("InjOrgFlg", "String", "", "Left"));
        data.add(new TAttribute("OperatorId", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("OrgFlg".equalsIgnoreCase(name)) {
            setOrgFlg(value);
            getTObject().setValue("OrgFlg", value);
            return;
        }
        if ("OrgType".equalsIgnoreCase(name)) {
            setOrgType(value);
            getTObject().setValue("OrgType", value);
            return;
        }
        if ("ExinvFlg".equalsIgnoreCase(name)) {
            setExinvFlg(value);
            getTObject().setValue("ExinvFlg", value);
            return;
        }
        if ("InjOrgFlg".equalsIgnoreCase(name)) {
            setInjOrgFlg(value);
            getTObject().setValue("InjOrgFlg", value);
            return;
        }
        if ("StationFlg".equalsIgnoreCase(name)) {
            setStationFlg(value);
            getTObject().setValue("StationFlg", value);
            return;
        }
        if ("OperatorId".equalsIgnoreCase(name)) {
            setOperatorId(value);
            getTObject().setValue("OperatorId", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
