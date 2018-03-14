package com.javahis.system.textFormat;

import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.ui.TTextFormat;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.StringTool;
import com.dongyang.manager.TCM_Transform;
import java.sql.Timestamp;
import jdo.sys.Operator;

/**
 * <p>Title: 门诊适用人员下拉区域</p>
 *
 * <p>Description: 门诊适用人员下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2009.10.24
 * @version 1.0
 */
public class TextFormatSYSOperatorForReg
    extends TTextFormat {
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 门急别
     */
    private String admType;
    /**
     * 就诊日期
     */
    private String admDate;
    /**
     * 时段
     */
    private String sessionCode;
    /**
     * 科室
     */
    private String deptCode;
    /**
     * 诊室
     */
    private String clinicroomNo;
    /**
     * 号别
     */
    private String clinicType;
    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {

        this.regionCode = regionCode;
        setModifySQL(true);
    }

    /**
     * 设置门急别
     * @param admType String
     */
    public void setAdmType(String admType) {
        this.admType = admType;
        setModifySQL(true);
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }


    /**
     * 得到门急别
     * @return String
     */
    public String getAdmType() {
        return admType;
    }

    /**
     * 设置时段
     * @param sessionCode String
     */
    public void setSessionCode(String sessionCode) {
        this.sessionCode = sessionCode;
        setModifySQL(true);
    }

    /**
     * 设置就诊日期
     * @param admDate String
     */
    public void setAdmDate(String admDate) {
        this.admDate = admDate;
        setModifySQL(true);
    }


    /**
     * 得到就诊日期
     * @return String
     */
    public String getAdmDate() {
        return admDate;
    }


    /**
     * 得到时段
     * @return String
     */
    public String getSessionCode() {
        return sessionCode;
    }

    /**
     * 设置科室
     * @param deptCode String
     */
    public void setDeptCode(String deptCode) {
        this.deptCode = deptCode;
    }

    /**
     * 得到科室
     * @return String
     */
    public String getDeptCode() {
        return deptCode;
    }

    /**
     * 设置诊室
     * @param clinicroomNo String
     */
    public void setClinicroomNo(String clinicroomNo) {
        this.clinicroomNo = clinicroomNo;
    }

    /**
     * 得到诊室
     * @return String
     */
    public String getClinicroomNo() {
        return clinicroomNo;
    }

    /**
     * 设置号别
     * @param clinicType String
     */
    public void setClinicType(String clinicType) {
        this.clinicType = clinicType;
    }

    /**
     * 得到号别
     * @return String
     */
    public String getClinicType() {
        return clinicType;
    }

    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
            " SELECT DISTINCT A.REALDR_CODE AS ID, B.USER_NAME AS NAME,B.USER_ENG_NAME AS ENNAME, B.PY1 AS PY1, B.PY2 AS PY2 " +
            "   FROM REG_SCHDAY A, SYS_OPERATOR B " +
            "  WHERE A.REALDR_CODE = B.USER_ID ";
        String sql1 = " ORDER BY A.REALDR_CODE ";

        StringBuffer sb = new StringBuffer();

        String regionCode = TypeTool.getString(getTagValue(getRegionCode()));
        if (regionCode != null && regionCode.length() > 0)
            sb.append(" AND A.REGION_CODE = '" + regionCode + "' ");

        String admType = TypeTool.getString(getTagValue(getAdmType()));
        if (admType != null && admType.length() > 0) {
            sb.append(" AND A.ADM_TYPE = '" + admType + "' ");
        }
        Object value = getTagValue(getAdmDate());
        if (value instanceof Timestamp)
            value = StringTool.getString(TCM_Transform.getTimestamp(value),
                                         "yyyyMMdd");
        else
            value = getTagValue(getAdmDate());

        String admDate = TypeTool.getString(value);
        if (admDate != null && admDate.length() > 0) {
            sb.append(" AND A.ADM_DATE = '" + admDate + "' ");
        }

        String sessionCode = TypeTool.getString(getTagValue(getSessionCode()));
        if (sessionCode != null && sessionCode.length() > 0) {
            sb.append(" AND A.SESSION_CODE = '" + sessionCode + "' ");
        }
        String deptCode = TypeTool.getString(getTagValue(getDeptCode()));
        if (deptCode != null && deptCode.length() > 0) {
            sb.append(" AND A.REALDEPT_CODE = '" + deptCode + "' ");
        }
        String clinicroomNo = TypeTool.getString(getTagValue(getClinicroomNo()));
        if (clinicroomNo != null && clinicroomNo.length() > 0) {
            sb.append(" AND A.CLINICROOM_NO = '" + clinicroomNo + "' ");
        }
        String clinicType = TypeTool.getString(getTagValue(getClinicType()));
        if (clinicType != null && clinicType.length() > 0) {
            sb.append(" AND A.CLINICTYPE_CODE = '" + clinicType + "' ");
        }
        //=============pangben modify 20110420 start 添加号别区域筛选
       String operatorCodeAll = Operator.getRegion();
       if (operatorCodeAll != null && operatorCodeAll.length() > 0) {
//           if (sb.length() > 0)
//               sb.append(" AND ");
           sb.append(" AND B.REGION_CODE = '" + operatorCodeAll + "' ");
       }
       //=============pangben modify 20110420 stop

        if (sb.length() > 0)
            sql += sb.toString() + sql1;
        else
            sql = sql + sql1;
       // System.out.println("s:"+sql);
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
        object.setValue("Tip", "门诊适用人员");
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
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
        data.add(new TAttribute("AdmType", "String", "", "Left"));
        data.add(new TAttribute("AdmDate", "String", "", "Left"));
        data.add(new TAttribute("SessionCode", "String", "", "Left"));
        data.add(new TAttribute("DeptCode", "String", "", "Left"));
        data.add(new TAttribute("ClinicroomNo", "String", "", "Left"));
        data.add(new TAttribute("ClinicType", "String", "", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));

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
        if ("AdmType".equalsIgnoreCase(name)) {
            setAdmType(value);
            getTObject().setValue("AdmType", value);
            return;
        }
        if ("AdmDate".equalsIgnoreCase(name)) {
            setAdmDate(value);
            getTObject().setValue("AdmDate", value);
            return;
        }
        if ("SessionCode".equalsIgnoreCase(name)) {
            setSessionCode(value);
            getTObject().setValue("SessionCode", value);
            return;
        }
        if ("DeptCode".equalsIgnoreCase(name)) {
            setDeptCode(value);
            getTObject().setValue("DeptCode", value);
            return;
        }
        if ("ClinicroomNo".equalsIgnoreCase(name)) {
            setClinicroomNo(value);
            getTObject().setValue("ClinicroomNo", value);
            return;
        }
        if ("ClinicType".equalsIgnoreCase(name)) {
            setClinicType(value);
            getTObject().setValue("ClinicType", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
