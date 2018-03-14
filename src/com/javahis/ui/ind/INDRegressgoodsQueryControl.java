package com.javahis.ui.ind;

import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import com.javahis.util.StringUtil;
import com.dongyang.control.TControl;
import jdo.ind.IndRegressgoodsMTool;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.ui.TComboBox;
import com.javahis.util.ExportExcelUtil;

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
public class INDRegressgoodsQueryControl
    extends TControl {

    TTable table;

    public INDRegressgoodsQueryControl() {
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
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("退货部门不能为空");
            return;
        }
        table.removeRowAll();
        this.setValue("SUM_COUNT", 0);
        this.setValue("SUM_REG_AMT", 0);
        this.setValue("SUM_OWN_AMT", 0);
        this.setValue("SUM_DIFF_AMT", 0);

        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (!"".equals(this.getValueString("SUP_CODE"))) {
            parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }

//        if (this.getRadioButton("RadioButton1").isSelected()) {
//            parm.setData("UPDATE_FLG_A", "Y");
//        }
//        else {
//            parm.setData("UPDATE_FLG_B", "Y");
//        }

        TParm result = new TParm();

        if (this.getRadioButton("RadioButton1").isSelected()) {
            result = IndRegressgoodsMTool.getInstance().onQueryRegressgoods(parm);
        }
        else {
            result = IndRegressgoodsMTool.getInstance().
                onQueryRegressgoodsCheck(parm);
        }
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }

        double sum_reg_amt = 0;
        double sum_own_amt = 0;
        for (int i = 0; i < result.getCount(); i++) {
            sum_reg_amt += result.getDouble("REG_AMT", i);
            sum_own_amt += result.getDouble("OWN_AMT", i);
        }

        table.setParmValue(result);
        this.setValue("SUM_COUNT", result.getCount());
        this.setValue("SUM_REG_AMT", StringTool.round(sum_reg_amt, 2));
        this.setValue("SUM_OWN_AMT", StringTool.round(sum_own_amt, 2));
        this.setValue("SUM_DIFF_AMT",
                      StringTool.round(sum_own_amt - sum_reg_amt, 2));
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr =
            "ORG_CODE;ORDER_CODE;ORDER_DESC;SUM_COUNT;SUM_REG_AMT;SUM_OWN_AMT;"
            + "SUM_DIFF_AMT";
        this.clearValue(clearStr);

        getTextFormat("SUP_CODE").setValue(null);
        getTextFormat("SUP_CODE").setText("");

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
                     "药品退货出库统计表");
        date.setData("ORG_CODE", "TEXT",
                     "统计部门: " + getComboBox("ORG_CODE").getSelectedName());
        date.setData("STATUS", "TEXT",
                     "出库状态: " +
                     (getRadioButton("RadioButton1").isSelected() ? "未出库" : "已出库"));
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
                     end_date.substring(17, 19) );
        date.setData("DATE", "TEXT", "制表时间: " +
                     SystemTool.getInstance().getDate().toString().
                     substring(0, 10).replace('-', '/'));
        date.setData("USER", "TEXT", "制表人: " + Operator.getName());
        // 表格数据
        TParm parm = new TParm();
        TParm tableParm = table.getParmValue();
        for (int i = 0; i < table.getRowCount(); i++) {
            parm.addData("REGRESSGOODS_NO", tableParm.getValue("REGRESSGOODS_NO", i));
            parm.addData("SUP_ABS_DESC", tableParm.getValue("SUP_ABS_DESC", i));
            parm.addData("REGRESSGOODS_DATE",
                         tableParm.getValue("REGRESSGOODS_DATE", i).substring(0,
                10));
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("SPECIFICATION", tableParm.getValue("SPECIFICATION", i));
            parm.addData("UNIT_CHN_DESC", tableParm.getValue("UNIT_CHN_DESC", i));
            parm.addData("QTY", tableParm.getValue("QTY", i));
            parm.addData("UNIT_PRICE", tableParm.getValue("UNIT_PRICE", i));
            parm.addData("REG_AMT", tableParm.getValue("REG_AMT", i));
            parm.addData("RETAIL_PRICE", tableParm.getValue("RETAIL_PRICE", i));
            parm.addData("OWN_AMT", tableParm.getValue("OWN_AMT", i));
            parm.addData("DIFF_ATM", tableParm.getValue("DIFF_ATM", i));
            parm.addData("INVOICE_NO", tableParm.getValue("INVOICE_NO", i));
            parm.addData("INVOICE_DATE",
                         tableParm.getValue("INVOICE_DATE", i).substring(0, 10));
            parm.addData("BATCH_NO", tableParm.getValue("BATCH_NO", i));
            parm.addData("VALID_DATE",
                         tableParm.getValue("VALID_DATE", i).substring(0, 10));
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "REGRESSGOODS_NO");
        parm.addData("SYSTEM", "COLUMNS", "SUP_ABS_DESC");
        parm.addData("SYSTEM", "COLUMNS", "REGRESSGOODS_DATE");
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "REG_AMT");
        parm.addData("SYSTEM", "COLUMNS", "RETAIL_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
        parm.addData("SYSTEM", "COLUMNS", "DIFF_ATM");
        parm.addData("SYSTEM", "COLUMNS", "INVOICE_NO");
        parm.addData("SYSTEM", "COLUMNS", "INVOICE_DATE");
        parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        date.setData("TABLE", parm.getData());
        // 表尾数据
        date.setData("SUM_REG_AMT", "TEXT",
                     "退货总金额： " + getValueDouble("SUM_REG_AMT"));
        date.setData("SUM_OWN_AMT", "TEXT",
                     "零售总金额： " + getValueDouble("SUM_OWN_AMT"));
        date.setData("SUM_DIFF_AMT", "TEXT",
                     "进销差价： " + getValueDouble("SUM_DIFF_AMT"));
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDRegressgoodsQuery.jhw",
                             date);
    }

    /**
     * 导出EXCEL
     */
    public void onExport() {
        if (table.getRowCount() <= 0) {
            this.messageBox("没有汇出数据");
            return;
        }
        ExportExcelUtil.getInstance().exportExcel(table, "药品退货出库统计");
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

    /**
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

    /**
     * 得到ComboBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TComboBox getComboBox(String tagName) {
        return (TComboBox) getComponent(tagName);
    }


}
