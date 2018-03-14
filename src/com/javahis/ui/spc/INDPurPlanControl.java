package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.IndPurPlanDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndPurPlanObserver;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import jdo.util.Manager;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextFormat;
import jdo.spc.INDTool;

/**
 * <p>
 * Title: 采购计划Control
 * </p>
 *
 * <p>
 * Description: 采购计划Control
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
 * @author zhangy 2009.04.28
 * @version 1.0
 */

public class INDPurPlanControl
    extends TControl {

    /**
     * 操作标记：1.insertM：新增采购计划单主档 2.updateM：更新采购计划单主档111
     */
    private String action = "insertM";

    // 主项被选中行号
    private int selectRow_M = -1;

    // 查询主项结果集
    private TParm parm_M;

    // 细项序号
    private int seq;

    // 全部部门权限
    private boolean dept_popedom = true;

    // 引用生成采购计划单权限
    private boolean excerpt_popedom = true;

    // 调整修改生成量权限
    private boolean plan_qty_popedom = true;

    // 删除计划单权限
    private boolean delete_popedom = true;

    // 勾选计划编辑完成注记权限
    private boolean plan_flg_popedom = true;

    // 调整修改量权限
    private boolean pur_qty_popedom = true;

    // 勾选采购量确认注记权限
    private boolean pur_flg_popedom = true;

    // 录入确认量权限
    private boolean check_qty_popedom = true;

    // 勾选审核完成权限
    private boolean check_flg_popedom = true;

    // 勾选计划完成权限
    private boolean planend_flg_popedom = true;

    public INDPurPlanControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("计划部门不能为空");
            return;
        }
        if ("".equals(getValueString("PLAN_MONTH"))) {
            this.messageBox("计划月份不能为空");
            return;
        }
        // 传入参数集
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Timestamp plan_month = (Timestamp) getValue("PLAN_MONTH");
        parm.setData("PLAN_MONTH", StringTool.getString(plan_month, "yyyy/MM"));
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());
        String plan_no = getValueString("PLAN_NO");
        if (!"".equals(plan_no)) {
            parm.setData("PLAN_NO", plan_no);
        }
        String plan_desc = getValueString("PLAN_DESC");
        if (!"".equals(plan_desc)) {
            parm.setData("PLAN_DESC", plan_desc);
        }

        // 返回结果集
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.spc.INDPurPlanAction",
                                              "onQueryM", parm);
        if (result == null || result.getCount() == 0) {
            this.messageBox("没有查询数据");
            return;
        }
        // 根据输入参数判断其状态
        result = CheckStatus(result);
        TTable table_M = getTable("TABLE_M");
        table_M.setParmValue(result);
        // 将数据保存到结果集
        parm_M = result;
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空画面内容
        String clearString =
            "ORG_CODE;PLAN_MONTH;PLAN_NO;PLAN_DESC;SUM_TOTIL;SUM_TOTIL;"
            +
            "PLAN_FLG;PUR_FLG;CHECK_FLG;PLANEND_FLG;DESCRIPTION;ORDER_CODE;ORDER_DESC";
        clearValue(clearString);
        // 初始化页面状态及数据
        getTextFormat("SUP_CODE").setValue(null);
        getTextFormat("SUP_CODE").setText("");
        TTable table_M = getTable("TABLE_M");
        table_M.setSelectionMode(0);
        table_M.removeRowAll();
        TTable table_D = getTable("TABLE_D");
        table_D.setSelectionMode(0);
        table_D.removeRowAll();
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        ( (TMenuItem) getComponent("make")).setEnabled(false);
        getComboBox("ORG_CODE").setEnabled(true);
        getTextField("PLAN_NO").setEnabled(true);
        Timestamp date = SystemTool.getInstance().getDate();;
        setValue("PLAN_MONTH", date);
        // 初始化全局参数
        action = "insertM";
        selectRow_M = -1;

        // 权限控制
        check_dept_popedom();
        check_excerpt_popedom();
        check_qty_popedom();
        check_select_flg_popedom();
        check_delete_popedom();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        // 传入参数集
        TParm parm = new TParm();
        // 返回结果集
        TParm result = new TParm();

        Timestamp date = SystemTool.getInstance().getDate();;
        // 新增采购计划单
        if ("insertM".equals(action)) {
            if (!CheckData()) {
                return;
            }
            // 调用存储过程拿到PLAN_NO
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            String plan_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_PURPLANM", "No");
            parm.setData("PLAN_NO", plan_no);
            parm.setData("PLAN_DESC", getValueString("PLAN_DESC"));
            Timestamp plan_month = (Timestamp) getValue("PLAN_MONTH");
            parm.setData("PLAN_MONTH", StringTool.getString(plan_month,
                "yyyy/MM"));
            TNull tnull = new TNull(Timestamp.class);
            parm.setData("PLAN_DATE", tnull);
            parm.setData("PLAN_USER", Operator.getID());
            parm.setData("PUR_USER", "");
            parm.setData("PUR_DATE", tnull);
            parm.setData("CHECK_USER", "");
            parm.setData("CHECK_DATE", tnull);
            parm.setData("PLANEND_USER", "");
            parm.setData("PLANEND_DATE", tnull);
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            parm.setData("CREATE_FLG", "N");
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            //zhangyong20110517
            parm.setData("REGION_CODE", Operator.getRegion());
            // 执行数据新增
            result = TIOM_AppServer.executeAction(
                "action.spc.INDPurPlanAction", "onInsertM", parm);
            parm.setData("PLAN_STATUS", "计划中");
            parm.setData("PURORDER_NO", "N");
        }
        else if ("updateM".equals(action)) {
            // 更新采购计划单
            if (!CheckData()) {
                return;
            }
            // 从查询结果集取得数据
            parm = parm_M.getRow(selectRow_M);
            TNull tnull = new TNull(Timestamp.class);
            if (parm.getData("PLAN_DATE") == null) {
                parm.setData("PLAN_DATE", tnull);
            }
            if (parm.getData("PUR_DATE") == null) {
                parm.setData("PUR_DATE", tnull);
            }
            if (parm.getData("PUR_USER") == null) {
                parm.setData("PUR_USER", "");
            }
            if (parm.getData("CHECK_DATE") == null) {
                parm.setData("CHECK_DATE", tnull);
            }
            if (parm.getData("CHECK_USER") == null) {
                parm.setData("CHECK_USER", "");
            }
            if (parm.getData("PLANEND_DATE") == null) {
                parm.setData("PLANEND_DATE", tnull);
            }
            if (parm.getData("PLANEND_USER") == null) {
                parm.setData("PLANEND_USER", "");
            }
            // 画面值取得
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            parm.setData("PLAN_NO", getValueString("PLAN_NO"));
            parm.setData("PLAN_DESC", getValueString("PLAN_DESC"));
            Timestamp plan_month = (Timestamp) getValue("PLAN_MONTH");
            parm.setData("PLAN_MONTH", StringTool.getString(plan_month,
                "yyyy/MM"));
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            // 根据状态选项填充parm
            if ("Y".equals(getValueString("PLANEND_FLG"))) {
                parm.setData("PLANEND_USER", Operator.getID());
                parm.setData("PLANEND_DATE", date);
                parm.setData("PLAN_STATUS", "计划完成");
            }
            else if ("Y".equals(getValueString("CHECK_FLG"))) {
                parm.setData("CHECK_USER", Operator.getID());
                parm.setData("CHECK_DATE", date);
                parm.setData("PLAN_STATUS", "审核确认");
                // 判断采购量确认是否完成
                if (parm.getData("PUR_DATE") == null) {
                    parm.setData("PUR_USER", Operator.getID());
                    parm.setData("PUR_DATE", date);
                }
            }
            else if ("Y".equals(getValueString("PUR_FLG"))) {
                parm.setData("PUR_USER", Operator.getID());
                parm.setData("PUR_DATE", date);
                parm.setData("PLAN_STATUS", "采购量完成");
            }
            else if ("Y".equals(getValueString("PLAN_FLG"))) {
                parm.setData("PLAN_USER", Operator.getID());
                parm.setData("PLAN_DATE", date);
                parm.setData("PLAN_STATUS", "计划编辑完成");
            }
            else {
                parm.setData("PLAN_STATUS", "计划中");
            }
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());

            // 执行数据更新
            result = TIOM_AppServer.executeAction(
                "action.spc.INDPurPlanAction", "onUpdateM", parm);
        }
        else {
            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();
            // 获得全部改动的行号
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // 给固定数据配数据
            for (int i = 0; i < rows.length; i++) {
                dataStore
                    .setItem(rows[i], "PLAN_NO", getValueString("PLAN_NO"));
                dataStore.setItem(rows[i], "ORG_CODE",
                                  getValueString("ORG_CODE"));
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            // 细项保存判断
            if (!table_D.update()) {
                messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_D.setDSValue();
            return;
        }

        // 主项保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onQuery();
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if (getTable("TABLE_M").getSelectedRow() > -1) {
            if (this.messageBox("删除", "确定是否删除采购计划单", 2) == 0) {
                TTable table_D = getTable("TABLE_D");
                table_D.removeRowAll();
                TParm parm = new TParm();
                String plan_no = getValueString("PLAN_NO");
                parm.setData("PLAN_NO", plan_no);
                String org_code = getValueString("ORG_CODE");
                parm.setData("ORG_CODE", org_code);
                // 查询所有细项
                TParm result = new TParm();
                result = IndPurPlanDTool.getInstance().onQuery(parm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                if (result.getCount() > 0) {
                    // 删除主项及细项
                    parm.setData("DELETE_SQL", table_D.getDataStore()
                                 .getUpdateSQL());
                }
                result = TIOM_AppServer.executeAction(
                    "action.spc.INDPurPlanAction", "onDeleteM", parm);
                // 删除判断
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                getTable("TABLE_M").removeRow(
                    getTable("TABLE_M").getSelectedRow());
                this.messageBox("删除成功");
                this.onClear();
            }
        }
        else {
            TTable table_D = getTable("TABLE_D");
            if (this.messageBox("删除", "确定是否删除采购计划单细项", 2) == 0) {
                table_D.removeRow(table_D.getSelectedRow());
                // 细项保存判断
                if (!table_D.update()) {
                    messageBox("E0001");
                    return;
                }
                messageBox("P0001");
                table_D.setDSValue();
                return;
            }
        }
    }

    /**
     * 打开引用单
     */
    public void onExport() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("PLAN_MONTH", getValueString("PLAN_MONTH"));
        parm.setData("PLAN_NO", getValueString("PLAN_NO"));
        parm.setData("CONTROL", this);
        this.openWindow("%ROOT%\\config\\spc\\INDPurplanExcerpt.x",
                        parm);
    }

    /**
     * 生成订购单
     */
    public void onMake() {
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("PLAN_MONTH", getValueString("PLAN_MONTH"));
        parm.setData("PLAN_NO", getValueString("PLAN_NO"));
        Object result = openDialog("%ROOT%\\config\\spc\\INDOrderForm.x",
                                   parm);
    }

    /**
     * 打印计划单
     */
    public void onPlan() {
        Timestamp datetime = SystemTool.getInstance().getDate();;
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("PLAN_NO"))) {
            this.messageBox("不存在计划单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "采购计划单");
            date.setData("ORG_CODE", "TEXT",
                         "计划部门: " + getComboBox("ORG_CODE").getSelectedName());
            date.setData("PLAN_NO", "TEXT", "计划单号: " + getValueString("PLAN_NO"));
            date.setData("DATE", "TEXT", "制表日期: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));

            // 表格数据
            TParm parm = new TParm();
            String order_code = "";
            String sup_code = "";
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                sup_code = table_d.getDataStore().getItemString(i,
                    "SUP_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(
                    INDSQL.getOrderInfoByCode(order_code, sup_code, "PLAN")));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("药品信息有误");
                    return;
                }
                parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("QTY", table_d.getItemDouble(i, "STOCK_QTY"));
                parm.addData("PLAN_QTY",
                             table_d.getItemDouble(i, "CHECK_QTY"));
                parm.addData("PRUCH_PRICE",
                             inparm.getDouble("CONTRACT_PRICE", 0));
                parm.addData("AMT",
                             StringTool.round(inparm.getDouble(
                                 "CONTRACT_PRICE",
                                 0) * table_d.getItemDouble(i, "PLAN_QTY"), 2));
                parm.addData("OWN_PRICE",
                             StringTool.round(inparm.getDouble("OWN_PRICE", 0) *
                                              inparm.getDouble("DOSAGE_QTY", 0),
                                              4));
                parm.addData("SUP_CODE",
                             inparm.getValue("SUP_CHN_DESC", 0));
                parm.addData("MAN_CODE",
                             inparm.getValue("MAN_CHN_DESC", 0));
            }
            if (parm.getCount("ORDER_DESC") == 0) {
                this.messageBox("没有打印数据");
                return;
            }

            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "PLAN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "PRUCH_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
            parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");

            date.setData("TABLE", parm.getData());

            //表尾数据
            date.setData("OPT", "TEXT", "制表人: " + Operator.getName());
            // 调用打印方法
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\PurPlan.jhw",
                                 date);
        }
        else {
            this.messageBox("没有打印数据");
            return;
        }
    }

    /**
     * 药品差异分析
     */
    public void onAnalyse() {
        Timestamp datetime = SystemTool.getInstance().getDate();;
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("PLAN_NO"))) {
            this.messageBox("不存在计划单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "药品差异分析单");
            // 表格数据
            TParm parm = new TParm();
            // 计划单号
            String plan_no = "";
            // 计划金额合计
            double sum_plan_amt = 0;
            // 入库金额合计
            double sum_in_amt = 0;
            // 差异金额合计
            double sum_diff_amt = 0;
            plan_no = this.getValueString("PLAN_NO");
            TParm inparm = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getOrderInfoByPlan(plan_no)));
            if (inparm == null || inparm.getErrCode() < 0) {
                this.messageBox("药品信息有误");
                return;
            }
            if (inparm.getCount("ORDER_DESC") <= 0) {
                this.messageBox("没有打印数据");
                return;
            }
            for (int i = 0; i < inparm.getCount("ORDER_DESC"); i++) {
                parm.addData("SUP_CODE", inparm.getValue("SUP_CHN_DESC", i));
                parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", i));
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", i));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", i));
                parm.addData("PLAN_QTY", inparm.getDouble("PLAN_QTY", i));
                parm.addData("PLAN_PRICE", inparm.getDouble("PLAN_PRICE", i));
                parm.addData("IN_QTY", inparm.getDouble("IN_QTY", i));
                parm.addData("IN_PRICE", inparm.getDouble("IN_PRICE", i));
                parm.addData("PLAN_AMT", inparm.getDouble("PLAN_AMT", i));
                sum_plan_amt += inparm.getDouble("PLAN_AMT", i);
                parm.addData("IN_AMT", inparm.getDouble("IN_AMT", i));
                sum_in_amt += inparm.getDouble("IN_AMT", i);
                parm.addData("DIFF_QTY", inparm.getDouble("DIFF_QTY", i));
                parm.addData("DIFF_AMT", inparm.getDouble("DIFF_AMT", i));
                sum_diff_amt += inparm.getDouble("DIFF_AMT", i);
            }

            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "SUP_CODE");
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "PLAN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "PLAN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "IN_QTY");
            parm.addData("SYSTEM", "COLUMNS", "IN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "PLAN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "IN_AMT");
            parm.addData("SYSTEM", "COLUMNS", "DIFF_QTY");
            parm.addData("SYSTEM", "COLUMNS", "DIFF_AMT");
            date.setData("TABLE", parm.getData());
            // 表尾数据
            date.setData("TOTLE_1", "TEXT",
                         "计划金额合计: " + sum_plan_amt);
            date.setData("TOTLE_2", "TEXT",
                         "入库金额合计: " + sum_in_amt);
            date.setData("TOTLE_3", "TEXT",
                         "差异金额合计: " + sum_diff_amt);
            date.setData("USER", "TEXT",
                         "制表人: " + Operator.getName());
            date.setData("DATE", "TEXT",
                         "制表日期: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));
            // 调用打印方法
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\DiffOrder.jhw",
                                 date);
        }
        else {
            this.messageBox("没有打印数据");
            return;
        }
    }

    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            ( (TMenuItem) getComponent("make")).setEnabled(false);
            // 主项信息(TABLE中取得)
            String org_code = table.getItemString(row, "ORG_CODE");
            setValue("ORG_CODE", org_code);
            String plan_no = table.getItemString(row, "PLAN_NO");
            setValue("PLAN_NO", plan_no);
            String plan_month = table.getItemString(row, "PLAN_MONTH");
            setValue("PLAN_MONTH", plan_month);
            String plan_status = table.getItemString(row, "PLAN_STATUS");
            // 根据状态勾选状态多选框
            setPlanStatus(plan_status);
            // 主项信息(tparm查询结果集中取得)
            String plan_desc = parm_M.getValue("PLAN_DESC", row);
            setValue("PLAN_DESC", plan_desc);
            String description = parm_M.getValue("DESCRIPTION", row);
            setValue("DESCRIPTION", description);
            // 设定页面状态
            getComboBox("ORG_CODE").setEnabled(false);
            getTextField("PLAN_NO").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
            action = "updateM";
            selectRow_M = row;
            // 明细信息
            TTable table_D = getTable("TABLE_D");
            table_D.removeRowAll();
            table_D.setSelectionMode(0);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getINDPurPlandD(org_code, plan_no));
            tds.retrieve();
            if (tds.rowCount() == 0) {
                seq = 1;
                this.messageBox("没有计划明细");
            }
            else {
                seq = getMaxSeq(tds, "SEQ");
            }
            // 观察者
            IndPurPlanObserver obser = new IndPurPlanObserver();
            tds.addObserver(obser);
            table_D.setDataStore(tds);
            table_D.setDSValue();

            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);

            // 权限控制
            check_excerpt_popedom();
            check_qty_popedom();
            check_select_flg_popedom();
            check_delete_popedom();
        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            action = "updateD";
            // 主项信息
            TTable table_M = getTable("TABLE_M");
            table_M.setSelectionMode(0);
            // 取得SYS_FEE信息，放置在状态栏上
            /*
             String order_code = table.getDataStore().getItemString(table.
                getSelectedRow(), "ORDER_CODE");
                         this.setSysStatus(order_code);
             */
        }
    }

    /**
     * 计划编辑完成FLG
     */
    public void onPlanAction() {
        if ("Y".equals(getValueString("PLAN_FLG"))) {
            TTable table = getTable("TABLE_D");
            if (table.getRowCount() == 0) {
                this.messageBox("没有计划明细不可以编辑完成！");
                this.setValue("PLAN_FLG", "N");
            }
        }
    }

    /**
     * 采购量确认完成FLG
     */
    public void onPurAction() {
        if ("Y".equals(getValueString("PUR_FLG"))) {
            TTable table_D = getTable("TABLE_D");
            if (table_D.getRowCount() == 0) {
                this.messageBox("没有计划明细不可以编辑完成！");
                this.setValue("PUR_FLG", "N");
                return;
            }
            TTable table_M = getTable("TABLE_M");
            Timestamp plan_date = table_M.getItemTimestamp(selectRow_M,
                "PLAN_DATE");
            if (plan_date == null) {
                this.messageBox("计划编辑未完成！");
                this.setValue("PUR_FLG", "N");
            }
        }
    }

    /**
     * 审核确认完成FLG
     */
    public void onCheckAction() {
        if ("Y".equals(getValueString("CHECK_FLG"))) {
            TTable table_D = getTable("TABLE_D");
            if (table_D.getRowCount() == 0) {
                this.messageBox("没有计划明细不可以编辑完成！");
                this.setValue("CHECK_FLG", "N");
                return;
            }
            TTable table_M = getTable("TABLE_M");
            Timestamp plan_date = table_M.getItemTimestamp(selectRow_M,
                "PLAN_DATE");
            if (plan_date == null) {
                this.messageBox("计划编辑未完成！");
                this.setValue("CHECK_FLG", "N");
            }
        }
    }

    /**
     * 计划完成FLG
     */
    public void onPlanendAction() {
        if ("Y".equals(getValueString("PLANEND_FLG"))) {
            TTable table_D = getTable("TABLE_D");
            if (table_D.getRowCount() == 0) {
                this.messageBox("没有计划明细不可以编辑完成！");
                this.setValue("PLANEND_FLG", "N");
                return;
            }
            TTable table_M = getTable("TABLE_M");
            Timestamp check_date = table_M.getItemTimestamp(selectRow_M,
                "CHECK_DATE");
            if (check_date == null) {
                this.messageBox("采购量审核碓认未完成！");
                this.setValue("PLANEND_FLG", "N");
            }
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
        // Table的列名
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("PLAN_QTY".equals(columnName)) {
            if ("Y".equals(this.getValue("PLAN_FLG"))) {
                this.messageBox("计划编辑完成,不可修改");
                return true;
            }
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("计划生成量不可以小于等于0");
                return true;
            }
            node.getTable().setItem(row, "PUR_QTY", qty);
            node.getTable().setItem(row, "CHECK_QTY", qty);
            node.getTable().getDataStore().setItem(row, "PUR_QTY", qty);
            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);
            return false;
        }
        if ("PUR_QTY".equals(columnName)) {
            if ("Y".equals(this.getValue("PUR_FLG"))) {
                this.messageBox("采购量确认完成,不可修改");
                return true;
            }
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("修改量不可以小于等于0");
                return true;
            }
            node.getTable().setItem(row, "CHECK_QTY", qty);
            node.getTable().getDataStore().setItem(row, "PUR_QTY", qty);
            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);
            return false;
        }
        if ("CHECK_QTY".equals(columnName)) {
            if ("Y".equals(this.getValue("PLANEND_FLG"))) {
                this.messageBox("计划完成,不可修改");
                return true;
            }
            else if ("Y".equals(this.getValue("CHECK_FLG"))) {
                this.messageBox("审核确认完成,不可修改");
                return true;
            }
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("确认量不可以小于等于0");
                return true;
            }
        }
        if ("STOCK_PRICE".equals(columnName)) {
            double price = TypeTool.getDouble(node.getValue());
            if (price <= 0) {
                this.messageBox("采购单价不可以小于等于0");
                return true;
            }
            double qty = node.getTable().getItemDouble(row, "CHECK_QTY");
            node.getTable().getDataStore().setItem(row, "PLAN_SUM",
                StringTool.round(price * qty, 2));
            node.getTable().getDataStore().setItem(row, "STOCK_PRICE", price);
            double sum_money = getSumMoney();
            this.setValue("SUM_TOTIL", sum_money);
            return false;
        }
        return false;
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
     *
     * @param parm
     */
    public void appendTableRow(Object obj) {
        TParm result = new TParm( (Map) obj);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            Object object = new Object();
            TTable table = getTable("TABLE_D");
            for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
                int row = table.addRow();
                table.setItem(row, "ORDER_CODE", addParm.getData("ORDER_CODE",
                    i));
                table.setItem(row, "SPECIFICATION", addParm.getData(
                    "SPECIFICATION", i));
                table.setItem(row, "PURCH_UNIT", addParm.getData("PURCH_UNIT",
                    i));
                object = addParm.getData("BUY_QTY", i);
                table.setItem(row, "LASTPUR_QTY", object);
                object = addParm.getData("SELL_QTY", i);
                table.setItem(row, "LASTCON_QTY", object);
                object = addParm.getData("STOCK_QTY", i);
                table.setItem(row, "STOCK_QTY",
                              addParm.getDouble("STOCK_QTY", i));
                table.setItem(row, "PLAN_QTY", addParm.getData("E_QTY", i));
                table.setItem(row, "PUR_QTY", addParm.getData("E_QTY", i));
                table.setItem(row, "CHECK_QTY", addParm.getData("E_QTY", i));
                table.setItem(row, "STOCK_PRICE", addParm.getData(
                    "CONTRACT_PRICE", i));
                table.setItem(row, "SUP_CODE", addParm.getData("SUP_CODE", i));
                table.setItem(row, "MAN_CODE", addParm.getData("MAN_CODE", i));
                table.setItem(row, "SAFE_QTY", addParm.getData("SAFE_QTY", i));
                table.setItem(row, "MAX_QTY", addParm.getData("MAX_QTY", i));
                table.setItem(row, "BUY_UNRECEIVE_QTY", addParm.getData(
                    "BUY_UNRECEIVE_QTY", i));
                table.setItem(row, "START_DATE", addParm.getData("START_DATE",
                    i));
                table.setItem(row, "END_DATE", addParm.getData("END_DATE", i));
                table.getDataStore().setItem(row, "SEQ",
                                             getMaxSeq(table.getDataStore(),
                    "SEQ"));
            }
            table.setDSValue();
            table.setSelectedRow(0);
            onTableDClicked();
        }
        double sum_money = getSumMoney();
        this.setValue("SUM_TOTIL", sum_money);
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 取得权限
        if (!this.getPopedem("deptAll")) {
            dept_popedom = false;
        }
        if (!this.getPopedem("EXCERPT")) {
            excerpt_popedom = false;
        }
        if (!this.getPopedem("PLAN_QTY")) {
            plan_qty_popedom = false;
        }
        if (!this.getPopedem("DELETE")) {
            delete_popedom = false;
        }
        if (!this.getPopedem("PLAN_FLG")) {
            plan_flg_popedom = false;
        }
        if (!this.getPopedem("PUR_QTY")) {
            pur_qty_popedom = false;
        }
        if (!this.getPopedem("PUR_FLG")) {
            pur_flg_popedom = false;
        }
        if (!this.getPopedem("CHECK_QTY")) {
            check_qty_popedom = false;
        }
        if (!this.getPopedem("CHECK_FLG")) {
            check_flg_popedom = false;
        }
        if (!this.getPopedem("PLANEND_FLG")) {
            planend_flg_popedom = false;
        }

        // 初始化页面状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        ( (TMenuItem) getComponent("make")).setEnabled(false);
        Timestamp date = SystemTool.getInstance().getDate();
        setValue("PLAN_MONTH", date);
        parm_M = new TParm();
        action = "insertM";

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");

        // 权限控制
        check_dept_popedom();
        check_excerpt_popedom();
        check_qty_popedom();
        check_select_flg_popedom();
        check_delete_popedom();
    }

    /**
     * 检查科室权限
     */
    private void check_dept_popedom() {
        // 没有全院药库部门权限，只显示所属科室
        if (!dept_popedom) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
        }
    }

    /**
     * 检查引用权限
     */
    private void check_excerpt_popedom() {
        // 没有引用生成采购计划单权限
        if (!excerpt_popedom) {
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
    }

    /**
     * 检查输入量权限
     */
    private void check_qty_popedom() {
        // 调整生成量权限,调整修改量权限,确认量权限
        if (plan_qty_popedom && pur_qty_popedom && check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,10,11,12,13,14,15,16,17,18");
        }
        // 调整生成量权限,调整修改量权限
        else if (plan_qty_popedom && pur_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,9,10,11,12,13,14,15,16,17,18");
        }
        // 调整生成量权限,确认量权限
        else if (plan_qty_popedom && check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,8,10,11,12,13,14,15,16,17,18");
        }
        // 调整修改量权限,确认量权限
        else if (pur_qty_popedom && check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,7,10,11,12,13,14,15,16,17,18");
        } // 调整生成量权限
        else if (plan_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,8,9,10,11,12,13,14,15,16,17,18");
        }
        // 调整修改量权限
        else if (pur_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,7,9,10,11,12,13,14,15,16,17,18");
        }
        // 确认量权限
        else if (check_qty_popedom) {
            getTable("TABLE_D").setLockColumns(
                "0,1,2,3,4,5,6,7,8,10,11,12,13,14,15,16,17,18");
        }
        // 全没有
        else {
            getTable("TABLE_D").setLockColumns("all");
        }
    }

    /**
     * 检查勾选权限
     */
    private void check_select_flg_popedom() {
        if (!plan_flg_popedom) {
            getCheckBox("PLAN_FLG").setEnabled(false);
        }
        if (!pur_flg_popedom) {
            getCheckBox("PUR_FLG").setEnabled(false);
        }
        if (!check_flg_popedom) {
            getCheckBox("CHECK_FLG").setEnabled(false);
        }
        if (!planend_flg_popedom) {
            getCheckBox("PLANEND_FLG").setEnabled(false);
        }
    }

    /**
     * 检查删除权限
     */
    private void check_delete_popedom() {
        // 没有删除计划单权限
        if (!delete_popedom) {
            ( (TMenuItem) getComponent("delete")).setEnabled(false);
        }
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("计划部门不能为空");
            return false;
        }
        if ("".equals(getValueString("PLAN_MONTH"))) {
            this.messageBox("计划月份不能为空");
            return false;
        }
        if ("".equals(getValueString("PLAN_DESC"))) {
            this.messageBox("计划名称不能为空");
            return false;
        }
        return true;
    }

    /**
     * 根据输入参数判断其状态
     *
     * @param parm
     * @return
     */
    private TParm CheckStatus(TParm parm) {
        for (int i = 0; i < parm.getCount(); i++) {
            if (!"".equals(parm.getValue("PLANEND_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "计划完成");
            }
            else if (!"".equals(parm.getValue("CHECK_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "审核确认");
            }
            else if (!"".equals(parm.getValue("PUR_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "采购量完成");
            }
            else if (!"".equals(parm.getValue("PLAN_DATE", i))) {
                parm.setData("PLAN_STATUS", i, "计划编辑完成");
            }
            else {
                parm.setData("PLAN_STATUS", i, "计划中");
            }
        }
        return parm;
    }

    /**
     * 根据状态勾选状态多选框(CHECKBOX)
     *
     * @param status
     */
    private void setPlanStatus(String plan_status) {
        int status = 0;
        ( (TMenuItem) getComponent("make")).setEnabled(false);
        if ("计划完成".equals(plan_status)) {
            status = 0;
            ( (TMenuItem) getComponent("make")).setEnabled(true);
        }
        else if ("审核确认".equals(plan_status)) {
            status = 1;
        }
        else if ("采购量完成".equals(plan_status)) {
            status = 2;
        }
        else if ("计划编辑完成".equals(plan_status)) {
            status = 3;
        }
        else {
            status = 4;
        }
        // 清空状态
        clearValue("PLANEND_FLG;CHECK_FLG;PUR_FLG;PLAN_FLG");
        // 设置状态
        switch (status) {
            case 0:
                setValue("PLANEND_FLG", "Y");
            case 1:
                setValue("CHECK_FLG", "Y");
            case 2:
                setValue("PUR_FLG", "Y");
            case 3:
                setValue("PLAN_FLG", "Y");
            default:
                break;
        }
    }

    /**
     * 计算总金额
     *
     * @return
     */
    private double getSumMoney() {
        TTable table = getTable("TABLE_D");
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            sum += table.getItemDouble(i, "PUR_QTY")
                * table.getItemDouble(i, "STOCK_PRICE");
        }
        return StringTool.round(sum, 2);
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
     * 得到最大的编号
     *
     * @param dataStore
     *            TDataStore
     * @param columnName
     *            String
     * @return String
     */
    private int getMaxSeq(TDataStore dataStore, String columnName) {
        if (dataStore == null)
            return 0;
        // 保存数据量
        int count = dataStore.rowCount();
        // 保存最大号
        int s = 0;
        for (int i = 0; i < count; i++) {
            int value = dataStore.getItemInt(i, columnName);
            // 保存最大值
            if (s < value) {
                s = value;
                continue;
            }
        }
        // 最大号加1
        s++;
        return s;
    }

    /**
     * 取得SYS_FEE信息，放置在状态栏上
     * @param order_code String
     */
    private void setSysStatus(String order_code) {
        TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
        String status_desc = "药品代码:" + order.getValue("ORDER_CODE")
            + " 药品名称:" + order.getValue("ORDER_DESC")
            + " 商品名:" + order.getValue("GOODS_DESC")
            + " 规格:" + order.getValue("SPECIFICATION");
        callFunction("UI|setSysStatus", status_desc);
    }
}
