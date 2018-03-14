package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import jdo.ind.IndDispenseMTool;
import java.util.Map;
import java.util.HashMap;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 药品种类出库汇总</p>
 *
 * <p>Description: 药品种类出库汇总</p>
 *
 * <p>Copyright: Copyright (c) 深圳中航 2011</p>
 *
 * <p>Company: 深圳中航</p>
 *
 * @author zhangy  更改 ZhenQin
 * @version 4.0
 */
public class INDDispenseQueryControl
    extends TControl {

    TTable table;

    public INDDispenseQueryControl() {


    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table = this.getTable("TABLE");

        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table.removeRowAll();
        this.setValue("SUM_TOT", 0);
        this.setValue("SUM_COUNT", 0);
        this.setValue("SUM_AMT", 0);

        TParm parm = new TParm();
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (!"".equals(this.getValueString("REQUEST_TYPE"))) {
            parm.setData("REQUEST_TYPE", this.getValueString("REQUEST_TYPE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("TO_ORG_CODE"))) {
            parm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
        }
        if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", this.getValueString("APP_ORG_CODE"));
        }
        if (this.getRadioButton("RadioButton1").isSelected()) {
            parm.setData("DISPENSE_OUT", "Y");
        }
        else {
            parm.setData("DISPENSE_IN", "Y");
        }
        if ("Y".equals(this.getValueString("STOP_FLG"))) {
            parm.setData("STOP_FLG", this.getValueString("STOP_FLG"));
        }
        parm.setData("REGION_CODE", Operator.getRegion());
        //System.out.println("param: " + parm);
        TParm result = IndDispenseMTool.getInstance().onQueryDispense(parm);
        //System.out.println(""+result);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }

        double sum_qty = 0;
        double sum_amt = 0;
        Map map = new HashMap();
        map.put("DEP", "部门请领");
        map.put("TEC", "备药生成");
        map.put("EXM", "补充计费");
        map.put("GIF", "药房调拨");
        map.put("RET", "退库");
        map.put("WAS", "损耗");
        map.put("THO", "其它出库");
        map.put("COS", "卫耗材领用");
        map.put("THI", "其它入库");
        for (int i = 0; i < result.getCount(); i++) {
            sum_amt += result.getDouble("OWN_AMT", i);
            sum_qty += result.getDouble("QTY", i);
            result.setData("REQTYPE_CODE", i,
                           map.get(result.getValue("REQTYPE_CODE", i)));
        }

        table.setParmValue(result);
        this.setValue("SUM_TOT", StringTool.round(sum_qty, 3));
        this.setValue("SUM_COUNT", result.getCount());
        this.setValue("SUM_AMT", StringTool.round(sum_amt, 2));

    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr =
            "REQUEST_TYPE;ORDER_CODE;ORDER_DESC;STOP_FLG;TO_ORG_CODE;"
            + "APP_ORG_CODE;SUM_TOT;SUM_COUNT;SUM_AMT";
        this.clearValue(clearStr);

        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        getRadioButton("RadioButton1").setSelected(true);
        table.removeRowAll();
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        TTable table = getTable("TABLE");
        if (table.getRowCount() <= 0) {
            this.messageBox("没有打印数据");
            return;
        }
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "药品出库表");
        date.setData("STATUS", "TEXT",
                     "状态: " +
                     (getRadioButton("RadioButton1").isSelected() ? "出库" : "入库"));
        String start_date = getValueString("START_DATE");
        String end_date = getValueString("END_DATE");
        date.setData("DATE_AREA", "TEXT",
                     "统计区间: " +
                     start_date.substring(0, 4) + "/" +
                     start_date.substring(5, 7) + "/" +
                     start_date.substring(8, 10) + " " +
                     start_date.substring(11, 13) + ":" +
                     start_date.substring(14, 16) + ":" +
                     start_date.substring(17, 19) +
                     " ~ " +
                     end_date.substring(0, 4) + "/" +
                     end_date.substring(5, 7) + "/" +
                     end_date.substring(8, 10) + " " +
                     end_date.substring(11, 13) + ":" +
                     end_date.substring(14, 16) + ":" +
                     end_date.substring(17, 19));
        date.setData("DATE", "TEXT", "制表时间: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).
                     replace('-', '/'));
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
        // 表格数据
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        String dispense_date = "";
        String warehousing_date = "";
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("STOP_FLG",
                         "Y".equals(tableParm.getValue("STOP_FLG", i)) ? "是" :
                         "否");
            parm.addData("REQTYPE_CODE", tableParm.getValue("REQTYPE_CODE", i));
            parm.addData("DISPENSE_NO", tableParm.getValue("DISPENSE_NO", i));
            parm.addData("DEPT_CHN_DESC", tableParm.getValue("DEPT_CHN_DESC", i));
            parm.addData("ORG_CHN_DESC", tableParm.getValue("ORG_CHN_DESC", i));
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
            parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("QTY", tableParm.getValue("QTY", i));
            parm.addData("OWN_PRICE", tableParm.getValue("OWN_PRICE", i));
            parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
            parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO", i));
            parm.addData("VALID_DATE",
                         tableParm.getValue("VALID_DATE", i).substring(0, 10));
            dispense_date = tableParm.getValue("DISPENSE_DATE", i);
            parm.addData("DISPENSE_DATE",
                         dispense_date.length() > 10 ?
                         dispense_date.substring(0, 10) : dispense_date);
            warehousing_date = tableParm.getValue("WAREHOUSING_DATE", i);
            parm.addData("WAREHOUSING_DATE",
                         warehousing_date.length() > 10 ?
                         warehousing_date.substring(0, 10) : warehousing_date);
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "STOP_FLG");
        parm.addData("SYSTEM", "COLUMNS", "REQTYPE_CODE");
        parm.addData("SYSTEM", "COLUMNS", "DISPENSE_NO");
        parm.addData("SYSTEM", "COLUMNS", "DEPT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "ORG_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        parm.addData("SYSTEM", "COLUMNS", "DISPENSE_DATE");
        parm.addData("SYSTEM", "COLUMNS", "WAREHOUSING_DATE");
        date.setData("TABLE", parm.getData());
        // 表尾数据
        date.setData("SUM_AMT", "TEXT",
                     "总金额： " + getValueDouble("SUM_AMT"));
        // 调用打印方法
        this.openPrintWindow(
            "%ROOT%\\config\\prt\\IND\\INDDispenseQuery.jhw", date);
    }

    /**
     * 汇出Excel
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "种类出库统计");
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
    }


    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }
}
