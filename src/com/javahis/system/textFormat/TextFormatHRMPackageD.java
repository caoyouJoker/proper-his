package com.javahis.system.textFormat;

import com.dongyang.config.TConfigParse.TObject;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.edit.TAttributeList;
import com.dongyang.ui.edit.TAttributeList.TAttribute;
import com.dongyang.util.TypeTool;

/**
 * <p> Title: 健检套餐细项下拉区域  </p>
 * 
 * <p>Description: 健检套餐细项下拉区域  </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company: ProperSoft </p>
 * 
 * @author wanglong 20130422
 * @version 1.0
 */
public class TextFormatHRMPackageD extends TTextFormat {

    /**
     * 套餐代码
     */
    private String packageCode;

    /**
     * 获得套餐代码
     * 
     * @param packageCode
     *            String
     */
    public String getPackageCode() {
        return packageCode;
    }

    /**
     * 设置套餐代码
     * 
     * @param packageCode
     *            String
     */
    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
        setModifySQL(true);
    }


    /**
     * 执行Module动作
     * 
     * @return String
     */
    public String getPopupMenuSQL() {
        String sql =
                " WITH BB AS ("
                        + "SELECT ORDERSET_CODE,SUM(DISPENSE_QTY*ORIGINAL_PRICE) ORIGINAL_PRICE,SUM(DISPENSE_QTY*PACKAGE_PRICE) PACKAGE_PRICE "
                        + "  FROM HRM_PACKAGED "
                        + " WHERE 1 = 1 "
                        + "   AND PACKAGE_CODE = '#' "
                        + " GROUP BY ORDERSET_CODE) "
                        + "SELECT DISTINCT A.ORDER_CODE,A.ORDER_DESC,A.GOODS_DESC,A.SPECIFICATION,B.PACKAGE_PRICE,C.UNIT_CHN_DESC,"
                        + "       A.ORDER_CAT1_CODE,A.DISPENSE_UNIT,REGEXP_REPLACE(py(ORDER_DESC),'[^0-9a-zA-Z]+') PY1,"
                        + "       REGEXP_REPLACE(py(GOODS_DESC),'[^0-9a-zA-Z]+') PY2,A.ORDERSET_CODE,"
                        + "       1 AS DISPENSE_QTY,B.ORIGINAL_PRICE,A.SETMAIN_FLG,A.HIDE_FLG,A.OPTITEM_CODE,A.EXEC_DEPT_CODE,"
                        + "       A.DEPT_ATTRIBUTE,A.MEDI_QTY,A.MEDI_UNIT,A.FREQ_CODE,A.ROUTE_CODE,A.SEQ "
                        + "  FROM HRM_PACKAGED A, BB B, SYS_UNIT C "
                        + " WHERE A.ORDERSET_CODE = B.ORDERSET_CODE AND A.SETMAIN_FLG = 'Y' AND A.DISPENSE_UNIT = C.UNIT_CODE"
                        + "   AND A.PACKAGE_CODE = '#' "
                        + "ORDER BY A.SEQ";
        String packageCode = TypeTool.getString(getTagValue(getPackageCode()));
        sql = sql.replaceFirst("#", packageCode);
        sql = sql.replaceFirst("#", packageCode);
        return sql;
    }

    /**
     * 新建对象的初始值
     * 
     * @param object TObject
     */
    public void createInit(TObject object) {
        if (object == null) return;
        object.setValue("Width", "81");
        object.setValue("Height", "23");
        object.setValue("Text", "");
        object.setValue("HorizontalAlignment", "2");
        object.setValue("PopupMenuHeader", "医嘱码,70;医嘱名称,220;商品名,90;规格,60;单价,60,double,#########0.00;单位,60;医令分类,60");
        object.setValue("PopupMenuWidth", "300");
        object.setValue("PopupMenuHeight", "300");
        object.setValue("PopupMenuFilter", "ORDER_CODE,1;ORDER_DESC,1;GOODS_DESC,1;SPECIFICATION,1;PACKAGE_PRICE,1;UNIT_CHN_DESC,1;ORDER_CAT1_CODE,1");
        object.setValue("FormatType", "combo");
        object.setValue("ShowDownButton", "Y");
        object.setValue("Tip", "套餐细项");
        object.setValue("ShowColumnList", "ORDER_CODE;ORDER_DESC;GOODS_DESC;SPECIFICATION;PACKAGE_PRICE;UNIT_CHN_DESC;ORDER_CAT1_CODE");
    }

    public void onInit() {
        super.onInit();
        setPopupMenuFilter("ORDER_CODE,1;ORDER_DESC,1;GOODS_DESC,1;PY1,1;PY2,1");
        setLanguageMap("NAME|ENNAME");
        setPopupMenuEnHeader("Code;Name;Goods Name;Specification;Price;Unit;Class");
    }

    /**
     * 显示区域列名
     * 
     * @return String
     */
    public String getPopupMenuHeader() {
        return "医嘱码,70;医嘱名称,220;商品名,90;规格,60;单价,60,double,#########0.00;单位,60;医令分类,60";
    }
    
   
    /**
     * 增加扩展属性
     * 
     * @param data TAttributeList
     */
    public void getEnlargeAttributes(TAttributeList data) {
        data.add(new TAttribute("ShowColumnList", "String", "NAME", "Left"));
        data.add(new TAttribute("ValueColumn", "String", "ID", "Left"));
        data.add(new TAttribute("HisOneNullRow", "boolean", "N", "Center"));
        data.add(new TAttribute("packageCode", "String", "", "Left"));
    }

    /**
     * 设置属性
     * 
     * @param name
     *            String 属性名
     * @param value
     *            String 属性值
     */
    public void setAttribute(String name, String value) {
        if ("packageCode".equalsIgnoreCase(name)) {
            setPackageCode(value);
            getTObject().setValue("packageCode", value);
            return;
        }
        super.setAttribute(name, value);
    }
}
