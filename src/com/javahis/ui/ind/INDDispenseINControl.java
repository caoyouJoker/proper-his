package com.javahis.ui.ind;

import java.sql.Timestamp;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TTextFormat;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title: 入库管理
 * </p>
 *
 * <p>
 * Description: 入库管理
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
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class INDDispenseINControl
    extends TControl {

    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;

    // 主项表格选中行
    private int row_m;

    // 细项表格选中行
    private int row_d;

    // 细项序号
    private int seq;

    // 返回结果集
    private TParm resultParm;

    // 页面上翻数据列表
    private String[] pageItem = {
        "REQTYPE_CODE", "REQUEST_NO", "APP_ORG_CODE", "TO_ORG_CODE",
        "REASON_CHN_DESC", "REQUEST_DATE", "DISPENSE_NO", "DESCRIPTION",
        "URGENT_FLG"};

    // 单据类型
    private String request_type;

    // 申请单号
    private String request_no;

    // 使用单位
    private String u_type;

    // 出库部门
    private String out_org_code;

    // 入库部门
    private String in_org_code;

    // 是否出库
    private boolean out_flg;

    // 是否入库
    private boolean in_flg;

    // 入库单号
    private String dispense_no;

    // 全院药库部门作业单据
    private boolean request_all_flg = true;

    public INDDispenseINControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 添加侦听事件
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                              "onQueryInM", onQueryParm());
        // 全院药库部门作业单据
        if (!request_all_flg) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getOperatorDept(Operator.getID())));
            String dept_code = "";
            //System.out.println("parm---"+parm);
            //System.out.println("result---"+result);
            for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                boolean flg = true;
                for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                    dept_code = parm.getValue("DEPT_CODE", j);
                    if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                    else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                             "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                }
                if (flg) {
                    result.removeRow(i);
                }
            }
        }
        //System.out.println("result==="+result);
        if (result == null || result.getCount("REQUEST_NO") <= 0) {
            this.messageBox("无查询结果");
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        // 清空画面内容
        String clearString =
            "REQUEST_NO;APP_ORG_CODE;REQTYPE_CODE;DISPENSE_NO;"
            + "REQUEST_DATE;TO_ORG_CODE;REASON_CHN_DESC;START_DATE;END_DATE;"
            + "DESCRIPTION;URGENT_FLG;SELECT_ALL;"
            + "SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE";
        clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("WAREHOUSING_DATE", date);
        // 初始化页面状态及数据
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        row_m = -1;
        row_d = -1;

        getTextField("REQUEST_NO").setEnabled(true);
        getTextFormat("APP_ORG_CODE").setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        ( (TMenuItem) getComponent("stop")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        resultParm = new TParm();
        seq = 1;
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            /** 更新保存(入库单主项及细项) */
            // 2.出库批号判断
            String batchvalid = getBatchValid(request_type);
            // 3.入库部门库存是否可异动
            if (!getOrgBatchFlg(in_org_code)) {
                return;
            }
            if (!"THI".equals(request_type)) {
                // 入库作业
                getDispenseOutIn(out_org_code, in_org_code, batchvalid,
                                 out_flg, in_flg);
            }
            else {
                // 其它入库作业
                getDispenseOtherIn(in_org_code, in_flg);
            }
        }
        else {
            // 更新完成入库
            this.messageBox("单据已入库，不可修改");
        }
    }

    /**
     * 终止单据方法
     */
    public void onStop() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("没有终止单据");
            return;
        }
        if (this.messageBox("提示", "是否终止单据", 2) == 0) {
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                "action.ind.INDRequestAction", "onUpdateFlg", parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("终止失败");
                return;
            }
            this.messageBox("终止成功");
            this.onClear();
        }
    }

    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // 主项表格选中行数据上翻
            getTableSelectValue(table_m, row_m, pageItem);
            // 出库时间
            if (table_m.getItemData(row_m, "DISPENSE_DATE") != null) {
                this.setValue("DISPENSE_DATE", table_m.getItemData(row_m,
                    "DISPENSE_DATE"));
            }
            // 设定页面状态
            getComboBox("REQTYPE_CODE").setEnabled(false);
            getTextFormat("APP_ORG_CODE").setEnabled(false);
            getTextField("REQUEST_NO").setEnabled(false);
            // 单据类别
            request_type = getValueString("REQTYPE_CODE");
            // 申请单号
            request_no = getValueString("REQUEST_NO");
            // 入库单号
            dispense_no = getValueString("DISPENSE_NO");
            // 明细信息
            getTableDInfo(request_no);
            table_d.setSelectionMode(0);
            row_d = -1;

        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            table_m.setSelectionMode(0);
            row_m = -1;
            // 取得SYS_FEE信息，放置在状态栏上
            /*
             String order_code = table_d.getParmValue().getValue("ORDER_CODE",
                table_d.getSelectedRow());
                         if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
                         }
                         else {
                callFunction("UI|setSysStatus", "");
                         }
             */
        }
    }

    /**
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
        table_d.acceptText();
        if (table_d.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if (!CheckDataD()) {
                    getCheckBox("SELECT_ALL").setSelected(false);
                    return;
                }
                table_d.setItem(i, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
        else {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                table_d.setItem(i, "SELECT_FLG", false);
            }
            setValue("SUM_RETAIL_PRICE", 0);
            setValue("SUM_VERIFYIN_PRICE", 0);
            setValue("PRICE_DIFFERENCE", 0);
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
        int column = node.getColumn();
        if (column == 6) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("入库数数量不能小于或等于0");
                return true;
            }
//            double stock_qty = table_d.getItemDouble(row_d, "STOCK_QTY");
//            if (qty > stock_qty) {
//                this.messageBox("出库数数量不能大于库存数量");
//                return true;
//            }
            double dep_qty = table_d.getItemDouble(row_d, "QTY");
            if (qty > dep_qty) {
                this.messageBox("入库数数量不能大于申请数量");
                return true;
            }
            double amt1 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "STOCK_PRICE"), 2);
            double amt2 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "RETAIL_PRICE"), 2);
            table_d.setItem(row_d, "STOCK_ATM", amt1);
            table_d.setItem(row_d, "RETAIL_ATM", amt2);
            table_d.setItem(row_d, "DIFF_ATM", amt2 - amt1);
            return false;
        }
        if (column == 8) {
            double price = TypeTool.getDouble(node.getValue());
            if (price <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return true;
            }
            double out_qty = table_d.getItemDouble(row_d, "OUT_QTY");
            table_d.setItem(row_d, "STOCK_ATM", StringTool.round(price
                * out_qty, 2));
            double atm = table_d.getItemDouble(row_d, "RETAIL_ATM");
            table_d.setItem(row_d, "DIFF_ATM", StringTool.round(atm - price
                * out_qty, 2));
            return false;
        }
        return true;
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table_d.acceptText();
        // 获得选中的列
        int column = table_d.getSelectedColumn();
        if (column == 0) {
            if ("Y".equals(table_d.getItemString(row_d, column))) {
                table_d.setItem(row_d, "SELECT_FLG", false);
            }
            else {
                table_d.setItem(row_d, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
    }

    /**
     * 入库状态改变事件
     */
    public void onChangeSelectAction() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_A").setSelected(true);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else {
            onClear();
            getRadioButton("UPDATE_FLG_B").setSelected(false);
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }

        // 入库日期
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("WAREHOUSING_DATE", date);
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 1;
        resultParm = new TParm();
        ( (TMenuItem) getComponent("stop")).setEnabled(false);
    }

    /**
     * 查询条件参数
     *
     * @return
     */
    private TParm onQueryParm() {
        // 传入参数集
        TParm parm = new TParm();
        // 申请单号
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        // 申请部门
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        // 单号类别
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        // 入库单号
        if (!"".equals(getValueString("DISPENSE_NO"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO"));
        }
        // 查询区间
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return parm;
        }
        // 入库状态
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            // 未入库
            parm.setData("STATUS", "B");
        }
        else {
            // 完成
            parm.setData("STATUS", "A");
        }
        return parm;
    }

    /**
     * 库存是否可异动状态判断
     *
     * @param org_code
     * @return
     */
    private boolean getOrgBatchFlg(String org_code) {
        // 库存是否可异动状态判断
        if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
            this.messageBox("药房批次过帐中则提示请先手动做日结");
            return false;
        }
        return true;
    }

    /**
     * 出库批号判断: 1,不指定批号和效期;2,指定批号和效期
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }

    /**
     * 根据申请单号取得细项信息并显示在细项表格上
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        TParm result = new TParm();
        if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
            result = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getInDispenseDInfo(dispense_no)));
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
        }
        else {
            result = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getOutRequestDInfo(req_no)));
//            System.out.println("sql:"+INDSQL.getOutRequestDInfo(req_no));
            if (this.getRadioButton("UPDATE_FLG_B").isSelected()) {
                ( (TMenuItem) getComponent("stop")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("stop")).setEnabled(false);
            }
        }
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有入库明细");
            return;
        }
        table_d.setSelectionMode(0);
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = false;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }
        else if ("THI".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = false;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        // 填充结果集
        resultParm = result;
        // 填充TABLE_D
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_d.setParmValue(result);

        setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
        setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
        setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
            - getSumRegMoney(), 2));
    }

    /**
     * 填充TABLE_D
     *
     * @param table
     * @param parm
     * @param args
     */
    private TParm setTableDValue(TParm result) {
        TParm parm = new TParm();
        double qty = 0;
        double actual_qty = 0;
        double stock_price = 0;
        double retail_price = 0;
        double atm = 0;
        String order_code = "";
        for (int i = 0; i < result.getCount(); i++) {
            parm.setData("SELECT_FLG", i, true);
            parm.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i));
            parm.setData("SPECIFICATION", i, result
                         .getValue("SPECIFICATION", i));
            qty = result.getDouble("QTY", i);
            parm.setData("QTY", i, qty);
            actual_qty = result.getDouble("ACTUAL_QTY", i);
            parm.setData("ACTUAL_QTY", i, actual_qty);
            order_code = result.getValue("ORDER_CODE", i);
            // 库存量(入库部门)
            double stock_qty = INDTool.getInstance().getStockQTY(in_org_code,
                order_code);
            if (stock_qty < 0) {
                stock_qty = 0;
            }
            if ("0".equals(u_type)) {
                parm.setData("STOCK_QTY", i,
                             stock_qty / result.getDouble("DOSAGE_QTY", i));
            }
            else {
                parm.setData("STOCK_QTY", i, stock_qty);
            }
            if ("THI".equals(request_type)) {
                parm.setData("OUT_QTY", i, qty);
            }
            else {
                parm.setData("OUT_QTY", i, result.getDouble("OUT_QTY", i));
            }
            parm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
            // 使用单位
            if ("0".equals(u_type)) {
                // 库存单位
                stock_price = result.getDouble("STOCK_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
                retail_price = result.getDouble("RETAIL_PRICE", i)
                    * result.getDouble("DOSAGE_QTY", i);
            }
            else {
                // 配药单位
                stock_price = result.getDouble("STOCK_PRICE", i);
                retail_price = result.getDouble("RETAIL_PRICE", i);
            }
            parm.setData("STOCK_PRICE", i, stock_price);
            atm = StringTool.round(stock_price * qty, 2);
            parm.setData("STOCK_ATM", i, atm);
            parm.setData("RETAIL_PRICE", i, retail_price);
            atm = StringTool.round(retail_price * qty, 2);
            parm.setData("RETAIL_ATM", i, atm);
            atm = StringTool.round(retail_price * qty - stock_price * qty, 2);
            parm.setData("DIFF_ATM", i, atm);
            parm.setData("BATCH_NO", i, result.getValue("BATCH_NO", i));
            //luhai 2012-1-13 add batch_seq 
            parm.setData("BATCH_SEQ", i, result.getValue("BATCH_SEQ", i));
            parm.setData("VALID_DATE", i, result.getTimestamp("VALID_DATE", i));
            parm.setData("PHA_TYPE", i, result.getValue("PHA_TYPE", i));
            parm.setData("ORDER_CODE", i, order_code);
        }
        return parm;
    }

    /**
     * 表格选中行数据上翻
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("REQUEST_NO"))) {
            this.messageBox("申请单号不能为空");
            return false;
        }
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("申请部门不能为空");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("单据类别不能为空");
            return false;
        }
        return true;
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataD() {
        // 判断细项是否有被选中项
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // 判断数据正确性
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("入库数数量不能小于或等于0");
                return false;
            }
            double price = table_d.getItemDouble(i, "STOCK_PRICE");
            if (price <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return false;
            }
        }
        return true;
    }

    /**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg) {
        // 药库参数信息
        TParm sysParm = getSysParm();
        // 是否回写购入价格
        String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
        parm.setData("REUPRICE_FLG", reuprice_flg);

        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        // 入库单号
        dispense_no = "";
        if ("".equals(getValueString("DISPENSE_NO"))) {
            dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_DISPENSE", "No");
        }
        else {
            dispense_no = getValueString("DISPENSE_NO");
        }
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", getValue("WAREHOUSING_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", getValue("WAREHOUSING_DATE"));
            parmM.setData("WAREHOUSING_USER", Operator.getID());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        parmM.setData("REASON_CHN_DESC", getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", u_type);
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //luhai 2012-01-13 add region_code begin
        parmM.setData("REGION_CODE", Operator.getRegion());
        //luhai 2012-01-13 add region_code end
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        return parm;
    }

    /**
     * 获得明细信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        int count = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmD.setData("DISPENSE_NO", count, dispense_no);
            if (!"THI".equals(this.getValueString("REQTYPE_CODE"))) {
                parmD.setData("SEQ_NO", count, resultParm.getInt("SEQ_NO", i));
            }
            else {
                parmD.setData("SEQ_NO", count, seq + count);
            }
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            parmD.setData("ORDER_CODE", count,
                          resultParm.getValue("ORDER_CODE", i));
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count, table_d.getItemDouble(i,
                "RETAIL_PRICE"));
            parmD.setData("STOCK_PRICE", count, table_d.getItemDouble(i,
                "STOCK_PRICE"));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getItemString(i,
                "PHA_TYPE"));
            parmD.setData("BATCH_SEQ", count, table_d.getParmValue().getValue(
                "BATCH_SEQ",i));
            parmD.setData("BATCH_NO", count,
                          table_d.getItemString(i, "BATCH_NO"));
            parmD.setData("VALID_DATE", count, table_d.getItemData(i,
                "VALID_DATE"));
            parmD.setData("DOSAGE_QTY", count, resultParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("STOCK_QTY", count, resultParm.getDouble(
                "STOCK_QTY", i));
            parmD.setData("REGION_CODE", count, Operator.getRegion());
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }

    /**
     * 判断是否存在中草药
     *
     * @return
     */
    private boolean checkPhaType() {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if ("C".equals(table_d.getItemString(i, "PHA_TYPE"))) {
                return true;
            }
        }
        return false;
    }

    /**
     * 草药自动维护收费标准
     *
     * @param parm
     * @param gown_costrate
     * @param gnhi_costrate
     * @param gov_costrate
     * @return
     */
    private TParm getGrpriceC(TParm parm, double gown_costrate,
                              double gnhi_costrate, double gov_costrate,
                              boolean flg) {
        String order_code = "";
        TParm parmC = new TParm();
        int count = 0;
        int index = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            order_code = resultParm.getValue("ORDER_CODE", i);
            TParm result = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getINDStock(in_org_code, order_code, Operator.getRegion())));
            double new_gown_price = result.getDouble("VERIFYIN_PRICE", 0)
                * gown_costrate;
            double new_gnhi_price = result.getDouble("VERIFYIN_PRICE", 0)
                * gnhi_costrate;
            double new_gov_price = result.getDouble("VERIFYIN_PRICE", 0)
                * gov_costrate;
            // 根据医嘱代码查询自费价、医保价与政府最高价
            result = SYSFeeTool.getInstance().getPrice(order_code);
            double old_gown_price = result.getDouble("OWN_PRICE", 0);
            double old_gnhi_price = result.getDouble("NHI_PRICE", 0);
            double old_gov_price = result.getDouble("GOV_PRICE", 0);

            if (new_gown_price != old_gown_price
                || new_gnhi_price != old_gnhi_price
                || new_gov_price != old_gov_price) {
                parmC.setData("ORDER_CODE", index, order_code);
                parmC.setData("OWN_PRICE", index, new_gown_price);
                parmC.setData("NHI_PRICE", index, new_gnhi_price);
                parmC.setData("GOV_PRICE", index, new_gov_price);
                parmC.setData("OLD_OWN_PRICE", index, old_gown_price);
                if (flg) {
                    parmC.setData("RETAIL_PRICE", index, new_gown_price);
                    parmC.setData("ORG_CODE", index, in_org_code);
                }
                index++;
            }
            count++;
        }
        if (parmC.getCount("ORDER_CODE") > 0) {
            if (this.messageBox("提示", "确定是否更新药品零售价,医保价与政府最高价", 2) == 0) {
                // 执行草药自动维护
                parm.setData("PARM_C", parmC.getData());
            }
        }
        return parm;
    }

    /**
     * 入库更新申请单数据
     *
     * @param parm
     * @return
     */
    private TParm getRequestParm(TParm parm) {
        TParm parmR = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        int count = 0;
        double out_qty = 0;
        double req_qty = 0;
        String update_flg = "1";
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmR.setData("REQUEST_NO", count, request_no);
            parmR.setData("SEQ_NO", count, resultParm.getInt("REQUEST_SEQ", i));
            out_qty = table_d.getItemDouble(i, "OUT_QTY");
            req_qty = table_d.getItemDouble(i, "QTY");
            parmR.setData("ACTUAL_QTY", count, out_qty);
            // 判断申请单状态
            if (out_qty == req_qty) {
                update_flg = "3";
            }
            parmR.setData("UPDATE_FLG", count, update_flg);
            // OPT
            parmR.setData("OPT_USER", count, Operator.getID());
            parmR.setData("OPT_DATE", count, date);
            parmR.setData("OPT_TERM", count, Operator.getIP());
        }
        parm.setData("PARM_R", parmR.getData());
        return parm;
    }

    /**
     * 入库作业
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String batchvalid, boolean out_flg,
                                  boolean in_flg) {
        if (!checkSelectRow()) {
            return;
        }
        TParm parm = new TParm();
        // 主项信息(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "3");
        // 细项信息(OUT_D)
        if (!CheckDataD()) {
            return;
        }
        parm = getDispenseDParm(parm);
        /*
                 // 判断是否存在中草药
                 if (checkPhaType()) {
            TParm result = IndSysParmTool.getInstance().onQuery();
            // 判断药品是否要自动维护收费标准
            if ("Y".equals(result.getValue("GPRICE_FLG"))) {
                // 草药成本加成比例(零售价)
                double gown_costrate = result.getDouble("GOWN_COSTRATE");
                // 草药成本加成比例(医保价)
                double gnhi_costrate = result.getDouble("GNHI_COSTRATE");
                // 草药成本加成比例(政府最高价)
                double gov_costrate = result.getDouble("GOV_COSTRATE");
                boolean flg = false;
                if ("Y".equals(result.getValue("UPDATE_RETAIL_FLG"))) {
                    flg = true;
                }
                // 草药自动维护收费标准及调价损益(PARM_C)
                parm = getGrpriceC(parm, gown_costrate, gnhi_costrate,
                                   gov_costrate, flg);
            }
                 }
         */

        if (!"".equals(in_org_code)) {
            // 入库部门(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // 是否入库(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        // 执行数据新增
        TParm result = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertIn", parm);

        // 保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        // 请领入库草药自动维护收费
        if ("DEP".equals(this.getValueString("REQTYPE_CODE"))) {
            // 判断药品零差价注记
            boolean pha_price_flg = false;
            TParm parmFlg = new TParm(TJDODBTool.getInstance().select(
                INDSQL.getINDSysParm()));
            if (parmFlg.getCount() > 0) {
                pha_price_flg = parmFlg.getBoolean("PHA_PRICE_FLG", 0);
            }
            updateGrpricePrice(this.getValueString("DISPENSE_NO"),
                               pha_price_flg);
        }
        onClear();
    }

    /**
     * 其他入库作业
     * @param in_org_code String
     * @param flg boolean
     */
    private void getDispenseOtherIn(String in_org_code, boolean in_flg) {
        if (!checkSelectRow()) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", in_org_code);
        // 使用单位
        parm.setData("UNIT_TYPE", u_type);
        // 申请单类型
        parm.setData("REQTYPE_CODE", request_type);
        // 主项信息(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "3");
        // 细项信息(OUT_D)
        if (!CheckDataD()) {
            return;
        }
        parm = getDispenseDParm(parm);
//        // 判断是否存在中草药
//        if (checkPhaType()) {
//            TParm result = IndSysParmTool.getInstance().onQuery();
//            // 判断药品是否要自动维护收费标准
//            if ("Y".equals(result.getValue("GPRICE_FLG"))) {
//                // 草药成本加成比例(零售价)
//                double gown_costrate = result.getDouble("GOWN_COSTRATE");
//                // 草药成本加成比例(医保价)
//                double gnhi_costrate = result.getDouble("GNHI_COSTRATE");
//                // 草药成本加成比例(政府最高价)
//                double gov_costrate = result.getDouble("GOV_COSTRATE");
//                boolean flg = false;
//                if ("Y".equals(result.getValue("UPDATE_RETAIL_FLG"))) {
//                    flg = true;
//                }
//                // 草药自动维护收费标准及调价损益(PARM_C)
//                parm = getGrpriceC(parm, gown_costrate, gnhi_costrate,
//                                   gov_costrate, flg);
//            }
//        }

        if (!"".equals(in_org_code)) {
            // 入库部门(IN_ORG)
            parm.setData("IN_ORG", in_org_code);
            // 是否入库(IN_FLG)
            parm.setData("IN_FLG", in_flg);
        }
        // 执行数据新增
        parm = TIOM_AppServer.executeAction("action.ind.INDDispenseAction",
                                            "onInsertOtherIn", parm);
        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 判断细项是否选中
     *
     * @return
     */
    private boolean checkSelectRow() {
        // 判断细项是否选中
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("没有选中");
            return false;
        }
        return true;
    }

    /**
     * 计算零售总金额
     *
     * @return
     */
    private double getSumRetailMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "RETAIL_ATM");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * 计算成本总金额
     *
     * @return
     */
    private double getSumRegMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "STOCK_ATM");
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
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm(){
        return IndSysParmTool.getInstance().onQuery();
    }

    /**
     * 请领入库草药自动维护收费(药品零差价除外)
     * @param verifyin_no String
     * @param pha_price_flg boolean
     */
    private void updateGrpricePrice(String dispense_no, boolean pha_price_flg){
        String sql =
            " SELECT A.ORDER_CODE, A.RETAIL_PRICE / D.STOCK_QTY / D.DOSAGE_QTY AS RETAIL_PRICE, B.OWN_PRICE , B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE "
            + " FROM IND_DISPENSED A, SYS_FEE B, PHA_BASE C, PHA_TRANSUNIT D "
            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
            + " AND A.ORDER_CODE = C.ORDER_CODE "
            + " AND B.ORDER_CODE = C.ORDER_CODE "
            + " AND A.ORDER_CODE = D.ORDER_CODE "
            + " AND B.ORDER_CODE = D.ORDER_CODE "
            + " AND C.ORDER_CODE = D.ORDER_CODE "
            + " AND A.RETAIL_PRICE <> B.OWN_PRICE * D.STOCK_QTY * D.DOSAGE_QTY "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' ";
        // 药品零差价注记
        if (!pha_price_flg) {
            sql += " AND C.PHA_TYPE = 'G' ";
        }

        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount("ORDER_CODE") <= 0) {
            return;
        }
        //草药新零售价
        double old_own_price = 0;
        double new_own_price = 0;
        String order_code = "";
        String order_desc = "";
        String batch_no = "";
        String valid_date = "";
        String message = "";
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            old_own_price = parm.getDouble("OWN_PRICE", i);
            new_own_price = parm.getDouble("RETAIL_PRICE", i);
            order_code = parm.getValue("ORDER_CODE", i);
            order_desc = parm.getValue("ORDER_DESC", i);
            batch_no = parm.getValue("BATCH_NO", i);
            valid_date = StringTool.getString(parm.getTimestamp("VALID_DATE", i),
                                              "yyyyMMdd");
            message = message + order_desc + "(" + order_code + ")，原零售价：" +
                old_own_price + "，新零售价:" + new_own_price + "\n";
            result.addData("ORDER_CODE", order_code);
            result.addData("ORDER_DESC", order_desc);
            result.addData("OWN_PRICE", new_own_price);
            result.addData("OLD_OWN_PRICE", old_own_price);
            result.addData("BATCH_NO", batch_no);
            result.addData("VALID_DATE", valid_date);
        }
 /*       if (result.getCount("ORDER_CODE") > 0) {
            if (this.messageBox("提示", message + "确定是否更新药品零售价?", 2) == 0) {
                // 执行草药自动维护
                TParm resultParm = new TParm();
                resultParm.setData("ORG_CODE",
                                   this.getValueString("APP_ORG_CODE"));
                resultParm.setData("DATA", result.getData());
                resultParm.setData("OPT_USER", Operator.getID());
                resultParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
                resultParm.setData("OPT_TERM", Operator.getIP());
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDDispenseAction", "onUpdateGrpricePrice",
                    resultParm);
                // 保存判断
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("更新失败");
                    return;
                }
                this.messageBox("更新成功");
            }
        }*/
    }

}
