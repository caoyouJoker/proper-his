package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.*;
import com.dongyang.ui.*;
import com.dongyang.ui.edit.*;
import com.dongyang.ui.edit.TAttributeList.*;
import com.dongyang.util.*;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class TextFormatDEVOrg
    extends TTextFormat {
    /**
     * 医疗设备注记
     */
    private String meddevFlg;

    /**
     * 信息设备注记
     */
    private String infdevFlg;

    /**
     * 其它固定资产注记
     */
    private String otherdevFlg;
    /**
     * 区域
     */
    private String regionCode;
    private String operatorId;

    public String getInfdevFlg() {
        return infdevFlg;
    }

    public String getMeddevFlg() {
        return meddevFlg;
    }

    public String getOtherdevFlg() {
        return otherdevFlg;
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

    public void setInfdevFlg(String infdevFlg) {
        this.infdevFlg = infdevFlg;
        setModifySQL(true);
    }

    public void setMeddevFlg(String meddevFlg) {
        this.meddevFlg = meddevFlg;
        setModifySQL(true);
    }

    public void setOtherdevFlg(String otherdevFlg) {
        this.otherdevFlg = otherdevFlg;
        setModifySQL(true);
    }

    public void setOperatorId(String operatorId) {
        this.operatorId = operatorId;
        setModifySQL(true);
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
     * @return String
     */
    public String getPopupMenuSQL() {
        String userId = TypeTool.getString(getTagValue(getOperatorId()));
        String sql = "";
        if (userId == null || userId.length() == 0) {
            sql =
                " SELECT DEPT_CODE AS ID,DEPT_DESC AS NAME,PY1,PY2 " +
                "   FROM DEV_ORG ";
            String sql1 = " ORDER BY SEQ,DEPT_CODE ";

            StringBuffer sb = new StringBuffer();

            String meddevFlg = TypeTool.getString(getTagValue(getMeddevFlg()));
            if (meddevFlg != null && meddevFlg.length() > 0)
                sb.append(" MEDDEV_FLG = '" + meddevFlg + "' ");

            String infdevFlg = TypeTool.getString(getTagValue(getInfdevFlg()));
            if (infdevFlg != null && infdevFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" INFDEV_FLG = '" + infdevFlg + "' ");
            }

            String otherdevFlg = TypeTool.getString(getTagValue(
                getOtherdevFlg()));
            if (otherdevFlg != null && otherdevFlg.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" OTHERDEV_FLG = '" + otherdevFlg + "' ");
            }
            String regionCode = TypeTool.getString(getTagValue(
                getRegionCode()));
            if (regionCode != null && regionCode.length() > 0) {
                if (sb.length() > 0)
                    sb.append(" AND ");
                sb.append(" REGION_CODE = '" + regionCode + "' ");
            }

            if (sb.length() > 0)
                sql += " WHERE " + sb.toString() + sql1;
            else
                sql = sql + sql1;

        }
        else {
            sql =
                " SELECT A.DEPT_CODE AS ID, A.DEPT_DESC AS NAME, A.PY1, A.PY2 " +
                "   FROM DEV_ORG A,SYS_OPERATOR_DEPT B " +
                " WHERE A.DEPT_CODE=B.DEPT_CODE ";
            String sql1 = " ORDER BY A.SEQ, A.DEPT_CODE ";

            StringBuffer sb2 = new StringBuffer();

            String meddevFlg = TypeTool.getString(getTagValue(getMeddevFlg()));
            if (meddevFlg != null && meddevFlg.length() > 0)
                sb2.append("AND A.MEDDEV_FLG = '" + meddevFlg + "' ");

            String infdevFlg = TypeTool.getString(getTagValue(getInfdevFlg()));
            if (infdevFlg != null && infdevFlg.length() > 0) {
                sb2.append("AND A.INFDEV_FLG = '" + infdevFlg + "' ");
            }

            String otherdevFlg = TypeTool.getString(getTagValue(
                getOtherdevFlg()));
            if (otherdevFlg != null && otherdevFlg.length() > 0) {
                sb2.append("AND A.OTHERDEV_FLG = '" + otherdevFlg + "' ");
            }

            String operatorId = TypeTool.getString(getTagValue(getOperatorId()));
            if (operatorId != null && operatorId.length() > 0) {
                sb2.append("AND B.USER_ID = '" + operatorId + "' ");
            }
            String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
            if (regionCode != null && regionCode.length() > 0) {
                sb2.append("AND A.REGION_CODE = '" + regionCode + "' ");
            }

            if (sb2.length() > 0)
                sql = sql + sb2.toString() + sql1;
            else
                sql = sql + sql1;
        }

//        this.setPopupMenuSQL(sql);
//        super.onQuery();
//        System.out.println("sql" + sql);
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
        object.setValue("Tip", " 设备部门");
        object.setValue("ShowColumnList", "ID;NAME");
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
       data.add(new TAttribute("MEDDEV_FLG", "String", "", "Left"));
       data.add(new TAttribute("INFDEV_FLG", "String", "", "Left"));
       data.add(new TAttribute("OTHERDEV_FLG", "String", "", "Left"));
       data.add(new TAttribute("OperatorId", "String", "", "Left"));
       data.add(new TAttribute("RegionCode", "String", "", "Left"));
       data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
   }

   /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {

        if ("MEDDEV_FLG".equalsIgnoreCase(name)) {
            setMeddevFlg(value);
            getTObject().setValue("MEDDEV_FLG", value);
            return;
        }
        if ("INFDEV_FLG".equalsIgnoreCase(name)) {
            setInfdevFlg(value);
            getTObject().setValue("INFDEV_FLG", value);
            return;
        }
        if ("OTHERDEV_FLG".equalsIgnoreCase(name)) {
            setOtherdevFlg(value);
            getTObject().setValue("OTHERDEV_FLG", value);
            return;
        }
        if ("OperatorId".equalsIgnoreCase(name)) {
            setOperatorId(value);
            getTObject().setValue("OperatorId", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        super.setAttribute(name, value);
    }


}
