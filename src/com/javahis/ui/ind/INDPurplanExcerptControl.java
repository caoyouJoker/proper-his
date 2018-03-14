package com.javahis.ui.ind;

import java.sql.Timestamp;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.RunClass;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 采购计划引用Control
 * </p>
 *
 * <p>
 * Description: 采购计划引用Control
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.08
 * @version 1.0
 */

public class INDPurplanExcerptControl
    extends TControl {

    private TParm parm;
    private String org_code;
    private Object plan_control;

    public INDPurplanExcerptControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 取得传入参数
        Object obj = getParameter();
        if (obj != null) {
            parm = (TParm) obj;
            Object control = parm.getData("CONTROL");
            if (control != null) {
                plan_control = control;
            }
            org_code = parm.getValue("ORG_CODE");
        }
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        getTable("TABLE").removeRowAll();
        TParm inParm = new TParm();
        inParm.setData("START_DATE", getValue("START_DATE"));
        inParm.setData("END_DATE", getValue("END_DATE"));
        inParm.setData("ORG_CODE", org_code);
        TParm result = new TParm();
        boolean flg = getCheckBox("CHECK_SAFE").isSelected();
        String order_code = getValueString("ORDER_CODE");
        // 药品分类
        if (!"".equals(getValueString("ORDER_TYPE"))) {
            inParm.setData("TYPE_CODE", getValueString("ORDER_TYPE"));
        }
        // 料位
        if (!"".equals(getValueString("MATERIAL_LOC_CODE"))) {
            inParm.setData("MATERIAL_LOC_CODE",
                           getValueString("MATERIAL_LOC_CODE"));
        }
        // 依照药品查询
        if (getRadioButton("GROUP_ORDER").isSelected()) {
            if (flg && !"".equals(order_code)) {
                // 安全库存量并且指定药品
                inParm.setData("CHECK_SAFE", "Y");
                inParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
            }
            else if (flg) {
                // 安全库存量
                inParm.setData("CHECK_SAFE", "Y");
            }
            else if (!"".equals(order_code)) {
                // 指定药品
                inParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
            }
        }
        else {
            // 依照生产厂商查询
            inParm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        result = TIOM_AppServer.executeAction("action.ind.INDPurPlanAction",
                                              "onQueryExcerptByOrder", inParm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }
        TTable table = getTable("TABLE");
        table.setParmValue(result);
    }

    /**
     * 传回方法
     */
    public void onReturn() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            return;
        }
        TParm result_table = table.getParmValue();
        TParm result = new TParm();
        for (int i = 0; i < result_table.getCount(); i++) {
            result.setRowData(i, result_table.getRow(i));
            result.setData("START_DATE", i, getValue("START_DATE"));
            result.setData("END_DATE", i, getValue("END_DATE"));
        }
        for (int i = result.getCount("ORDER_CODE") - 1; i >= 0; i--) {
            if ("N".equals(result.getValue("SELECT_FLG", i))) {
                result.removeRow(i);
                continue;
            }
        }
        if (result == null) {
            this.messageBox("没有传回数据");
            return;
        }
        String[] method = new String[] {
            "appendTableRow"};
        Object[] value = new Object[] {
            result.getData()};
        RunClass.invokeMethodT(plan_control, method, value);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 初始化统计区间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // 结束时间(本月的第一天)
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" +
            TypeTool.getString(date).
            substring(5, 7) +
            "/01 00:00:00",
            "yyyy/MM/dd HH:mm:ss");
        setValue("END_DATE", dateTime);
        // 起始时间(上个月第一天)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
        getRadioButton("GROUP_ORDER").setSelected(true);
        getCheckBox("CHECK_SAFE").setSelected(false);
        getCheckBox("CHECK_SAFE").setEnabled(true);
        getCheckBox("CHECK_ORDER").setSelected(false);
        getCheckBox("CHECK_ORDER").setEnabled(true);
        setValue("ORDER_CODE", "");
        getTextField("ORDER_CODE").setEnabled(false);
        setValue("ORDER_DESC", "");
        getTextFormat("SUP_CODE").setValue(null);
        getTextFormat("SUP_CODE").setText("");
        getTextFormat("SUP_CODE").setEnabled(false);
        getCheckBox("SELECT_ALL").setSelected(false);
        getComboBox("ORDER_TYPE").setSelectedIndex( -1);
        getComboBox("MATERIAL_LOC_CODE").setSelectedIndex( -1);
        setValue("SUM_MONEY", 0);
        getTable("TABLE").removeRowAll();
    }

    /**
     * 表格(TABLE)值改变事件
     */
    public boolean onTableChangeValue(Object obj) {
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        TTable table = node.getTable();
        int row = node.getRow();
        int column = node.getColumn();
        if (column == 9) {
            double count = TypeTool.getDouble(node.getValue());
            double price = TypeTool.getDouble(table.getValueAt(row, 10));
            table.setItem(row, "TOT_MONEY", StringTool.round(count * price, 2));
            if ("Y".equals(table.getItemString(row, "SELECT_FLG"))) {
                if (count <= 0) {
                    table.setItem(row, "SELECT_FLG", "N");
                }
                // 计算总价钱
                double sum = getSumMoney();
                this.setValue("SUM_MONEY", sum);
            }
            return false;
        }
        if (column == 10) {
            double price = TypeTool.getDouble(node.getValue());
            double count = TypeTool.getDouble(table.getValueAt(row, 9));
            table.setItem(row, "TOT_MONEY", StringTool.round(count * price, 2));
            if ("Y".equals(table.getItemString(row, "SELECT_FLG"))) {
                if (price <= 0) {
                    table.setItem(row, "SELECT_FLG", "N");
                }
                // 计算总价钱
                double sum = getSumMoney();
                this.setValue("SUM_MONEY", sum);
            }
            return false;
        }
        return false;
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        // 获得点击的table对象
        TTable tableDown = (TTable) obj;
        // 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        tableDown.acceptText();
        // 获得选中的行
        int row = tableDown.getSelectedRow();
        // 检查被选中行
//        if (tableDown.getItemDouble(row, "E_QTY") <= 0) {
//            tableDown.setItem(row, "SELECT_FLG", "N");
//            this.messageBox("计划生成量不能小于0");
//            return;
//        }
        if (tableDown.getItemDouble(row, "CONTRACT_PRICE") <= 0) {
            tableDown.setItem(row, "SELECT_FLG", "N");
            this.messageBox("价格不能小于等于0");
            return;
        }
        this.setValue("SUM_MONEY", getSumMoney());
    }

    /**
     * 单选按钮改变事件
     */
    public void onRadioButtonChange() {
        TRadioButton group_order = getRadioButton("GROUP_ORDER");
        if (group_order.isSelected()) {
            getCheckBox("CHECK_SAFE").setSelected(false);
            getCheckBox("CHECK_SAFE").setEnabled(true);
            getCheckBox("CHECK_ORDER").setSelected(false);
            getCheckBox("CHECK_ORDER").setEnabled(true);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
            getTextFormat("SUP_CODE").setValue(null);
            getTextFormat("SUP_CODE").setText("");
            getTextFormat("SUP_CODE").setEnabled(false);
        }
        else {
            getCheckBox("CHECK_SAFE").setSelected(false);
            getCheckBox("CHECK_SAFE").setEnabled(false);
            getCheckBox("CHECK_ORDER").setSelected(false);
            getCheckBox("CHECK_ORDER").setEnabled(false);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
            getTextFormat("SUP_CODE").setValue(null);
            getTextFormat("SUP_CODE").setText("");
            getTextFormat("SUP_CODE").setEnabled(true);
            getTextField("ORDER_CODE").setEnabled(false);
        }
    }

    /**
     * 指定药品复选框选中事件
     */
    public void onCheckOrderSelect() {
        if (getCheckBox("CHECK_ORDER").isSelected()) {
            getTextField("ORDER_CODE").setEnabled(true);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
        }
        else {
            getTextField("ORDER_CODE").setEnabled(false);
            setValue("ORDER_CODE", "");
            setValue("ORDER_DESC", "");
        }
    }

    /**
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (!getCheckData()) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "Y");
                this.setValue("SUM_MONEY", getSumMoney());
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "N");
                this.setValue("SUM_MONEY", 0);
            }
        }
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
     * TABLE双击事件
     */
    public void onTableDoubleClicked() {
        TTable table = getTable("TABLE");
        int column = table.getSelectedColumn();
        if (column == 11) {
            // 打开供货商查询界面
            // Object parm = openDialog("%ROOT%\\config\\ind\\INDMacValid.x",
            // getValueString("ORG_CODE"));
            // if (parm != null) {
            // parm = (String) parm;
            // if ("".equals(parm)) {
            // return;
            // }
            // int row = table.getSelectedRow();
            // table.setItem(row, column, parm);
            // }
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 添加侦听事件
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableChangeValue");
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter(
            "UD", getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        // 初始化统计区间
        Timestamp date = TJDODBTool.getInstance().getDBTime();
        // 结束时间(本月的第一天)
        Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date).
            substring(0, 4) + "/" +
            TypeTool.getString(date).
            substring(5, 7) +
            "/01 00:00:00",
            "yyyy/MM/dd HH:mm:ss");
        setValue("END_DATE", dateTime);
        // 起始时间(上个月第一天)
        setValue("START_DATE",
                 StringTool.rollDate(dateTime, -1).toString().substring(0, 4) +
                 "/" +
                 StringTool.rollDate(dateTime, -1).toString().substring(5, 7) +
                 "/01 00:00:00");
        // 初始化料位
        TComboINDMaterialloc mac = (TComboINDMaterialloc) getComponent(
            "MATERIAL_LOC_CODE");
        mac.setOrgCode(org_code);
        mac.onQuery();
    }

    /**
     * 计算总价钱
     *
     * @return
     */
    private double getSumMoney() {
        TTable table = getTable("TABLE");
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                sum += table.getItemDouble(i, "TOT_MONEY");
            }
        }
        return StringTool.round(sum, 2);
    }

    /**
     * 检查数据正确性
     *
     * @return
     */
    private boolean getCheckData() {
        TTable table = getTable("TABLE");
        table.acceptText();
        for (int i = 0; i < table.getRowCount(); i++) {
//            if (table.getItemDouble(i, "E_QTY") <= 0) {
//                this.messageBox("计划生成量不能小于0");
//                return false;
//            }
            if (table.getItemDouble(i, "CONTRACT_PRICE") <= 0) {
                this.messageBox("价格不能小于等于0");
                return false;
            }
        }
        return true;
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
     * 得到CheckBox对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
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
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

}
