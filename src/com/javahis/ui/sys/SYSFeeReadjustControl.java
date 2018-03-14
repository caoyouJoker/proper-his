package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TCheckBox;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.sys.SystemTool;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TNull;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.SYSFeeReadjustMTool;
import jdo.sys.SYSFeeReadjustDTool;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.awt.Component;
import com.javahis.util.StringUtil;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTabbedPane;
import jdo.util.Manager;

/**
 * <p>
 * Title:调价计划
 * </p>
 *
 * <p>
 * Description:调价计划
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.9.18
 * @version 1.0
 */
public class SYSFeeReadjustControl
    extends TControl {

    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;

    private TPanel panel_1;

    private String action = "insertM";

    // 审核权限
    private boolean check_flg = true;


    public SYSFeeReadjustControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 注册激发SYS_FEE弹出的事件
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        // 初始化画面数据
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        TParm result = new TParm();
        //this.messageBox(action);
        if ("insertM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            TParm parm = new TParm();
            TNull tnull = new TNull(Timestamp.class);
            Timestamp date = SystemTool.getInstance().getDate();
            String rpp_code = SystemTool.getInstance().getNo("ALL", "PUB",
                "SYS_FEE_READJUST", "No");
            parm.setData("RPP_CODE", rpp_code);
            parm.setData("RPP_DESC", this.getValue("RPP_DESC"));
            parm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
            parm.setData("RPP_USER", this.getValue("RPP_USER"));
            parm.setData("RPP_DATE", this.getValue("RPP_DATE"));
            parm.setData("CHK_USER", "");
            parm.setData("CHK_DATE", tnull);
            parm.setData("READJUST_DATE", this.getValue("READJUST_DATE"));
            parm.setData("READJUSTOP_DATE", tnull);
            parm.setData("RPP_STATUS", "0");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());

            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onInsertM", parm);
            // 保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            //onClear();
            this.onQuery();
        }
        else if ("updateM".equals(action)) {
            //this.messageBox("");
            if (!CheckDataM()) {
                return;
            }
            TParm parm = new TParm();
            TNull tnull = new TNull(Timestamp.class);
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
            parm.setData("RPP_DESC", this.getValue("RPP_DESC"));
            parm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
            parm.setData("RPP_USER", this.getValue("RPP_USER"));
            parm.setData("RPP_DATE", this.getValue("RPP_DATE"));
            if (this.getCheckBox("CHECK_FLG").isSelected()) {
                parm.setData("CHK_USER", this.getValueString("CHK_USER"));
                parm.setData("CHK_DATE", date);
                parm.setData("RPP_STATUS", "1");
            }
            else {
                parm.setData("CHK_USER", "");
                parm.setData("CHK_DATE", tnull);
                parm.setData("RPP_STATUS", "0");
            }
            parm.setData("READJUST_DATE", this.getValue("READJUST_DATE"));
            parm.setData("READJUSTOP_DATE", tnull);
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("START_DATE", this.getValue("READJUST_DATE"));
            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onUpdateM", parm);
            // 保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            //onClear();
            this.onQuery();
        }
        else if ("updateD".equals(action)) {
            TParm parm = new TParm();
            parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
            parm.setData("PARM_DATA", table_d.getParmValue().getData());
            parm.setData("OPT_USER", Operator.getID());
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onUpdateD", parm);
            // 保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            //onClear();
            this.onQuery();
        }
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (this.getRadioButton("UPDATE_A").isSelected()) {
            parm.setData("RPP_STATUS", "0");
        }
        else if (this.getRadioButton("UPDATE_B").isSelected()) {
            parm.setData("RPP_STATUS", "1");
        }
        else if (this.getRadioButton("UPDATE_C").isSelected()) {
            parm.setData("RPP_STATUS", "2");
        }
        if (!"".equals(this.getValueString("RPP_CODE"))) {
            parm.setData("RPP_CODE", this.getValue("RPP_CODE"));
        }
        if (!"".equals(this.getValueString("RPP_DESC"))) {
            parm.setData("RPP_DESC", this.getValue("RPP_DESC"));
        }
//        if (!"".equals(this.getValueString("RPP_USER"))) {
//            parm.setData("RPP_USER", this.getValue("RPP_USER"));
//        }
//        if (!"".equals(this.getValueString("CHK_USER"))) {
//            parm.setData("CHK_USER", this.getValue("CHK_USER"));
//        }
        TParm result = SYSFeeReadjustMTool.getInstance().onQuery(parm);
        //System.out.println("result:" + result);
        if (result == null || result.getCount() == 0) {
            table_m.removeRowAll();
            table_m.setSelectionMode(0);
            table_d.removeRowAll();
            table_d.setSelectionMode(0);
            this.messageBox("没有查询数据");
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空画面内容Page1
        String clearStringPage1 =
            "START_DATE;END_DATE;RPP_CODE;RPP_DESC;READJUST_DATE;"
            + "CHECK_FLG;RPP_DATE;CHK_DATE;DESCRIPTION";
        clearValue(clearStringPage1);
        // 初始化申请时间
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.getRadioButton("UPDATE_A").setSelected(true);
        this.getTextField("RPP_CODE").setEnabled(true);
        this.setValue("READJUST_DATE",
                      StringTool.rollDate(date, 1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("RPP_DATE", date);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("create")).setEnabled(false);
        // 初始化页面状态及数据
        table_m.removeRowAll();
        table_m.setSelectionMode(0);
        table_d.removeRowAll();
        table_d.setSelectionMode(0);

        ( (TTabbedPane)this.getComponent("tTabbedPane_0")).setSelectedIndex(0);
        setPanelStatus(true);
        if (check_flg) {
            this.setValue("CHK_DATE", date);
            //getComboBox("CHK_USER").setEnabled(true);
            this.getTextFormat("CHK_DATE").setEnabled(true);
        }
        else {
            getComboBox("CHK_USER").setEnabled(false);
            this.getTextFormat("CHK_DATE").setEnabled(false);
        }
        action = "insertM";
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if (!this.getRadioButton("UPDATE_A").isSelected()) {
            this.messageBox("调价计划已审核或已完成，不可删除");
            return;
        }
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
        if (panel_1.isShowing()) {
            if (this.messageBox("删除", "确定是否删除调价计划", 2) == 0) {
                result = TIOM_AppServer.executeAction(
                    "action.sys.SYSFeeReadjustAction", "onDeleteM", parm);
                // 保存判断
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                this.messageBox("删除成功");
                this.onClear();
            }
        }
        else {
            int row = table_d.getSelectedRow();
            if (table_d.getParmValue().getDouble("SEQ_NO", row) == -1) {
                table_d.removeRow(row);
            }
            else {
                if (this.messageBox("删除", "确定是否删除调价计划细项", 2) == 0) {
                    parm.setData("SEQ_NO",
                                 table_d.getParmValue().getDouble("SEQ_NO", row));
                    result = SYSFeeReadjustDTool.getInstance().onDelete(parm);
                    table_d.removeRow(row);
                    // 删除判断
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("删除失败");
                        return;
                    }
                    this.messageBox("删除成功");
                }
            }
        }
    }

    /**
     * 执行方法
     */
    public void onCreate() {
        if (this.getRadioButton("UPDATE_A").isSelected()) {
            this.messageBox("计划单未审核,无法执行");
            return;
        }
        else if (this.getRadioButton("UPDATE_C").isSelected()) {
            this.messageBox("计划单已执行");
            return;
        }
        else if (this.getRadioButton("UPDATE_B").isSelected()) {
            Timestamp date = SystemTool.getInstance().getDate();
            if (TypeTool.getTimestamp(this.getValue("READJUST_DATE")).compareTo(
                date) <= 0) {
                this.messageBox("调价时间不可小于等于当前时间");
                return;
            }

            TParm result = new TParm();
            TParm parm = new TParm();
            parm.setData("RPP_CODE", this.getValueString("RPP_CODE"));
            //System.out.println("---"+table_d.getParmValue().getData());
            parm.setData("PARM_DATA", table_d.getParmValue().getData());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            result = TIOM_AppServer.executeAction(
                "action.sys.SYSFeeReadjustAction", "onCreateSYSFeeReadjust",
                parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("执行失败");
                return;
            }
            this.messageBox("执行成功");
            onClear();
        }
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        TParm tableParm = table_d.getParmValue();
        if (tableParm == null) {
            this.messageBox("没有打印数据");
            return;
        }
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "调价计划单");
        date.setData("READJUST_DATE", "TEXT", "调价时间: " +
                     this.getValueString("READJUST_DATE").substring(0,
                     19).replace("-", "/"));
        // 表格数据
        TParm parm = new TParm();

        for (int i = 0; i < tableParm.getCount("ORDER_CODE"); i++) {
            if ("".equals(tableParm.getValue("ORDER_CODE", i))) {
                continue;
            }
            parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
            parm.addData("START_DATE",
                         tableParm.getValue("START_DATE", i).substring(0, 19).
                         replace("-", "/"));
            parm.addData("END_DATE",
                         tableParm.getValue("END_DATE", i).substring(0, 19).
                         replace("-", "/"));
            parm.addData("OWN_PRICE_NEW", tableParm.getValue("OWN_PRICE_NEW", i));
            parm.addData("OWN_PRICE2_NEW", tableParm.getValue("OWN_PRICE2_NEW", i));
            parm.addData("OWN_PRICE3_NEW", tableParm.getValue("OWN_PRICE3_NEW", i));
            parm.addData("OWN_PRICE_OLD", tableParm.getValue("OWN_PRICE_OLD", i));
            parm.addData("OWN_PRICE2_OLD", tableParm.getValue("OWN_PRICE2_OLD", i));
            parm.addData("OWN_PRICE3_OLD", tableParm.getValue("OWN_PRICE3_OLD", i));
        }
        if (parm.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有打印数据");
            return;
        }
        parm.setCount(parm.getCount("ORDER_DESC"));
        parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parm.addData("SYSTEM", "COLUMNS", "START_DATE");
        parm.addData("SYSTEM", "COLUMNS", "END_DATE");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE_NEW");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE2_NEW");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE3_NEW");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE_OLD");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE2_OLD");
        parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE3_OLD");
        date.setData("TABLE", parm.getData());
        // 表尾数据
        date.setData("PLAN_USER", "TEXT",
                     "计划人员:" + this.getComboBox("RPP_USER").getSelectedName());
        date.setData("CHECK_USER", "TEXT",
                     "审核人员:" + this.getComboBox("CHK_USER").getSelectedName());
        date.setData("USER", "TEXT", "制表人:" + Operator.getName());
        date.setData("DATE", "TEXT",
                     "打印时间:" +
                     SystemTool.getInstance().getDate().toString().substring(0, 10).
                     replace("-", "/"));
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\SYS\\SYSFeeReadjust.jhw", date);
    }

    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        if (table_m.getSelectedRow() != -1) {
            action = "updateM";
            // 主项表格选中行数据上翻
            this.setValue("RPP_CODE",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "RPP_CODE"));
            this.setValue("RPP_DESC",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "RPP_DESC"));
            this.setValue("RPP_USER",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "RPP_USER"));
            this.setValue("RPP_DATE",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "RPP_DATE"));
            this.setValue("CHK_USER",
                          table_m.getItemString(table_m.getSelectedRow(),
                                                "CHK_USER"));
            this.setValue("CHK_DATE",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "CHK_DATE"));
            this.setValue("READJUST_DATE",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "READJUST_DATE"));
            this.setValue("DESCRIPTION",
                          table_m.getItemData(table_m.getSelectedRow(),
                                              "DESCRIPTION"));

            if ("0".equals(table_m.getItemData(table_m.getSelectedRow(),
                                               "RPP_STATUS"))) {
                this.setValue("CHECK_FLG", "N");
            }
            else {
                this.setValue("CHECK_FLG", "Y");
            }
            getTextField("RPP_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        if (table_d.getSelectedRow() != -1) {
            table_m.setSelectionMode(0);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
        }
    }

    /**
     * 表格值改变事件
     *
     * @param obj
     *            Object
     */
    public boolean onTableDChangeValue(Object obj) {
        // 值改变的单元格
        TTableNode node = (TTableNode) obj;
        if (node == null)
            return false;
        // 判断数据改变
        if (node.getValue().equals(node.getOldValue()))
            return true;
        // Table的列
        int column = node.getTable().getSelectedColumn();
        // Table的行
        int row = node.getTable().getSelectedRow();
        if (column == 2) {
            Timestamp end_date = TypeTool.getTimestamp(node.getValue());
            if (end_date.compareTo(node.getTable().getItemTimestamp(row,
                "START_DATE")) <= 0) {
                this.messageBox("失效日期不能早于生效日期");
                return true;
            }
        }
        if (column == 3) {
            double own_price = TypeTool.getDouble(node.getValue());
            if (own_price <= 0) {
                this.messageBox("新自费价不能小于或等于0");
                return true;
            }
            node.getTable().setItem(row, "OWN_PRICE2_NEW", own_price * 2);
            node.getTable().setItem(row, "OWN_PRICE3_NEW", own_price * 2.5);
        }
        if (column == 4) {
            double nhi_price = TypeTool.getDouble(node.getValue());
            if (nhi_price <= 0) {
                this.messageBox("新贵宾价不能小于或等于0");
                return true;
            }
            if (nhi_price > node.getTable().getItemDouble(row, "OWN_PRICE3_NEW")) {
                this.messageBox("新贵宾价不能大于国际医疗价");
                return true;
            }
        }
        if (column == 5) {
            double gov_price = TypeTool.getDouble(node.getValue());
            if (gov_price <= 0) {
                this.messageBox("新国际医疗价不能小于或等于0");
                return true;
            }
        }
        return false;
    }

    /**
     * 变更属性页
     */
    public void onChangeTTabbedPane() {
        if ( ( ( (TTabbedPane)this.getComponent("tTabbedPane_0"))).getSelectedIndex() ==
            0) {
            if (table_m.getSelectedRow() != -1) {
                action = "updateM";
            }
            else {
                action = "insertM";
            }
            setPanelStatus(true);
            ( (TMenuItem) getComponent("create")).setEnabled(false);
        }
        else {
            action = "updateD";
            setPanelStatus(false);
            // 明细信息
            table_d.setSelectionMode(0);
            String rpp_code = getValueString("RPP_CODE");
            TParm parm = new TParm();
            parm.setData("RPP_CODE", rpp_code);
            TParm result = SYSFeeReadjustDTool.getInstance().onQuery(parm);
            if (result == null || result.getCount() == 0) {
                this.messageBox("没有细项信息");
                ( (TMenuItem) getComponent("create")).setEnabled(false);
            }
            else {
                ( (TMenuItem) getComponent("create")).setEnabled(true);
            }
            table_d.setParmValue(result);
            int row = table_d.addRow();
            table_d.setItem(row, "START_DATE",
                            this.getTextFormat("READJUST_DATE").getValue());
            table_d.setItem(row, "END_DATE",
                            StringTool.getTimestamp("99991231235959",
                                                    "yyyyMMddHHmmss"));
            table_d.getParmValue().setData("ORDER_CODE", row, "");
            table_d.getParmValue().setData("SEQ_NO", row, -1);
        }
    }

    /**
     * 当TABLE创建编辑控件时长期
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 0)
            return;
        if (! (com instanceof TTextField))
            return;
        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UD", getConfigParm().newConfig(
            "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
                                    "popReturn");
    }

    /**
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        table_d.acceptText();
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code)) {
            table_d.setItem(table_d.getSelectedRow(), "ORDER_DESC",
                            parm.getValue("ORDER_DESC"));
            table_d.getParmValue().setData("ORDER_CODE", table_d.getSelectedRow(),
                                           order_code);
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE_NEW",
                            parm.getDouble("OWN_PRICE"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE2_NEW",
                            parm.getDouble("OWN_PRICE2"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE3_NEW",
                            parm.getDouble("OWN_PRICE3"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE_OLD",
                            parm.getDouble("OWN_PRICE"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE2_OLD",
                            parm.getDouble("OWN_PRICE2"));
            table_d.setItem(table_d.getSelectedRow(), "OWN_PRICE3_OLD",
                            parm.getDouble("OWN_PRICE3"));

            // 添加新行
            int row = table_d.addRow();
            table_d.setItem(row, "START_DATE",
                            this.getTextFormat("READJUST_DATE").getValue());
            table_d.setItem(row, "END_DATE",
                            StringTool.getTimestamp("99991231235959",
                "yyyyMMddHHmmss"));
            table_d.getParmValue().setData("ORDER_CODE", row, "");
            table_d.getParmValue().setData("SEQ_NO", row, -1);
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        ( (TMenuItem) getComponent("create")).setEnabled(false);
        // 初始化申请时间
        Timestamp date = SystemTool.getInstance().getDate();
        // 审核权限
        if (!this.getPopedem("checkFlg")) {
            getCheckBox("CHECK_FLG").setEnabled(false);
            //getComboBox("CHK_USER").setEnabled(false);
            this.setValue("CHK_DATE", "");
            getTextFormat("CHK_DATE").setEnabled(false);
            check_flg = false;
        }
        else {
            getCheckBox("CHECK_FLG").setEnabled(true);
            //getComboBox("CHK_USER").setEnabled(true);
            getTextFormat("CHK_DATE").setEnabled(true);
            this.setValue("CHK_DATE", date);
            getComboBox("CHK_USER").setValue(Operator.getID());
        }
        getComboBox("RPP_USER").setValue(Operator.getID());
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("RPP_DATE", date);
        this.setValue("READJUST_DATE",
                      StringTool.rollDate(date, 1).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        panel_1 = getPanel("tPanel_6");
    }

    /**
     * 设置页面状态
     * @param flg boolean
     */
    private void setPanelStatus(boolean flg) {
        this.getTextFormat("START_DATE").setEnabled(flg);
        this.getTextFormat("END_DATE").setEnabled(flg);
        this.getRadioButton("UPDATE_A").setEnabled(flg);
        this.getRadioButton("UPDATE_B").setEnabled(flg);
        this.getRadioButton("UPDATE_C").setEnabled(flg);
        this.getTextField("RPP_CODE").setEnabled(flg);
        this.getTextField("RPP_DESC").setEnabled(flg);
        this.getTextFormat("READJUST_DATE").setEnabled(flg);
        //this.getComboBox("RPP_USER").setEnabled(flg);
        this.getTextFormat("RPP_DATE").setEnabled(flg);
        //this.getComboBox("CHK_USER").setEnabled(flg);
        this.getTextFormat("CHK_DATE").setEnabled(flg);
        this.getTextField("DESCRIPTION").setEnabled(flg);
        if (check_flg) {
            this.getCheckBox("CHECK_FLG").setEnabled(check_flg);
        }
        else {
            this.getCheckBox("CHECK_FLG").setEnabled(check_flg);
        }
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("RPP_DESC"))) {
            this.messageBox("计划名称不能为空");
            return false;
        }
        if ("".equals(getValueString("READJUST_DATE"))) {
            this.messageBox("调价时间不能为空");
            return false;
        }
        if ("".equals(getValueString("RPP_USER"))) {
            this.messageBox("计划人员不能为空");
            return false;
        }
        if ("".equals(getValueString("RPP_DATE"))) {
            this.messageBox("计划时间不能为空");
            return false;
        }
        Timestamp date = SystemTool.getInstance().getDate();
        if (TypeTool.getTimestamp(this.getValue("READJUST_DATE")).compareTo(
            date) <= 0) {
            this.messageBox("调价时间不可小于等于当前时间");
        }
        if (this.getCheckBox("CHECK_FLG").isSelected()) {
            if ("".equals(getValueString("CHK_USER"))) {
                this.messageBox("审核人员不能为空");
                return false;
            }
            if ("".equals(getValueString("CHK_DATE"))) {
                this.messageBox("审核时间不能为空");
                return false;
            }
        }
        return true;
    }

    /**
     * 勾选审核注记
     */
    public void onCheckFlgAction() {
        //TNull tnull = new TNull(Timestamp.class);
        Timestamp date = SystemTool.getInstance().getDate();
        if (this.getCheckBox("CHECK_FLG").isSelected()) {
            this.setValue("CHK_USER", Operator.getID());
            this.setValue("CHK_DATE",date);
        }
        else {
            this.setValue("CHK_USER", "");
            this.setValue("CHK_DATE", "");
        }
    }

    /**
     * 得到TPanel对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TPanel getPanel(String tagName) {
        return (TPanel) getComponent(tagName);
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
     * 得到RadioButton对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TRadioButton getRadioButton(String tagName) {
        return (TRadioButton) getComponent(tagName);
    }

}
