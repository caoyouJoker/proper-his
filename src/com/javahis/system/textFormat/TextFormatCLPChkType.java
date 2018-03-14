package com.javahis.system.textFormat;

import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import jdo.sys.Operator;
import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.edit.TAttributeList;

/**
 * <p>Title: 查核类别下拉区域</p>
 *
 * <p>Description: 查核类别下拉区域</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl 2011.05.04
 * @version 1.0
 */
public class TextFormatCLPChkType
    extends TTextFormat {
    /**
     * 医令类别
     */
    private String orderCat1Code;
    /**
     * 区域
     */
    private String regionCode;
    /**
     * 执行Module动作
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql = " SELECT CHKTYPE_CODE AS ID,CHKTYPE_CHN_DESC AS NAME,CHKTYPE_ENG_DESC AS ENNAME,PY1,PY2 " +
            "   FROM CLP_CHKTYPE ";
        String sql1 = " ORDER BY CHKTYPE_CODE,SEQ ";
        StringBuffer sb = new StringBuffer();
        String orderCat1CodeTemp = TypeTool.getString(getTagValue(
            getOrderCat1Code()));
        if (orderCat1CodeTemp != null && orderCat1CodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" ORDER_CAT1_CODE = '" + orderCat1CodeTemp + "' ");
        }

        String RegionCodeTemp = TypeTool.getString(getTagValue(getRegionCode()));
        if (RegionCodeTemp != null && RegionCodeTemp.length() > 0) {
            if (sb.length() > 0)
                sb.append(" AND ");
            sb.append(" REGION_CODE = '" + RegionCodeTemp + "' ");
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
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "查核类别");
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
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("OrderCat1Code", "String", "", "Left"));
        data.add(new TAttribute("RegionCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * @param name String 属性名
     * @param value String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("OrderCat1Code".equalsIgnoreCase(name)) {
            setOrderCat1Code(value);
            getTObject().setValue("OrderCat1Code", value);
            return;
        }
        if ("RegionCode".equalsIgnoreCase(name)) {
            this.setRegionCode(value);
            getTObject().setValue("RegionCode", value);
            return;
        }
        super.setAttribute(name, value);
    }

    /**
     * 得到医令类别
     * @return String
     */
    public String getOrderCat1Code() {
        return orderCat1Code;
    }

    /**
     * 得到区域
     * @return String
     */
    public String getRegionCode() {
        return regionCode;
    }

    /**
     * 设置医令类别
     * @param orderCat1Code String
     */
    public void setOrderCat1Code(String orderCat1Code) {
        this.orderCat1Code = orderCat1Code;
        setModifySQL(true);
    }

    /**
     * 设置区域
     * @param regionCode String
     */
    public void setRegionCode(String regionCode) {
        this.regionCode = regionCode;
    }
}
