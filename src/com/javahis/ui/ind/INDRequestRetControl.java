package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.sys.Operator;
import jdo.ind.INDSQL;
import com.dongyang.jdo.TDataStore;
import com.javahis.manager.IndRequestObserver;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.event.TPopupMenuEvent;
import java.awt.Component;
import jdo.ind.IndStockMTool;
import com.javahis.util.StringUtil;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTableNode;
import java.util.Vector;
import jdo.ind.IndStockDTool;
import jdo.util.Manager;
import jdo.ind.INDTool;

/**
 * <p>Title: 退库作业Control</p>
 *
 * <p>Description: 退库作业Control</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2009.05.23
 * @version 1.0
 */
public class INDRequestRetControl
    extends TControl {

    // 操作标记
    private String action;
    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;

    // 主项表格选中行
    private int row_m;

    // 细项表格选中行
    private int row_d;

    // 页面上翻数据列表
    private String[] pageItem = {
        "REQUEST_DATE", "REQUEST_NO", "APP_ORG_CODE",
        "REQTYPE_CODE", "TO_ORG_CODE", "REASON_CHN_DESC", "DESCRIPTION",
        "URGENT_FLG", "UNIT_TYPE"};

    // 细项序号
    private int seq;

    // 申请单号
    private String request_no;

    // 单据类型
    private String request_type;

    // 全部部门权限
    private boolean dept_flg = true;

    public INDRequestRetControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 注册激发SYS_FEE弹出的事件
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        // 添加侦听事件
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 初始化画面数据
        initPage();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        // 传入参数集
        TParm parm = new TParm();
        // 返回结果集
        TParm result = new TParm();
        if (!"updateD".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            // 填充主项信息
            parm = getRequestMParm(parm);
            if ("insertM".equals(action)) {
                // 拨补单号
                String request_no = SystemTool.getInstance().getNo("ALL",
                    "IND", "IND_REQUEST", "No");
                parm.setData("REQUEST_NO", request_no);
                // 执行数据新增
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDRequestAction", "onInsertM", parm);
            }
            else {
                parm.setData("REQUEST_NO", request_no);
                // 执行数据更新
                result = TIOM_AppServer.executeAction(
                    "action.ind.INDRequestAction", "onUpdateM", parm);
            }
            // 主项保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            onClear();
            setValue("REQUEST_NO", parm.getValue("REQUEST_NO"));
            this.messageBox("P0001");
            onQuery();
        }
        else {
            if (!CheckDataD()) {
                return;
            }
            // 供应部门库存及药品主档的判断
            if (!getOrgStockCheck()) {
                return;
            }
            Timestamp date = SystemTool.getInstance().getDate();
            table_d.acceptText();
            TDataStore dataStore = table_d.getDataStore();
            // 获得全部改动的行号
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // 获得全部的新增行
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
            // 给固定数据配数据
            Vector vct = new Vector();
            for (int i = 0; i < newrows.length; i++) {
                dataStore.setItem(newrows[i], "REQUEST_NO",
                                  getValueString("REQUEST_NO"));
                dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
                dataStore.setItem(newrows[i], "UPDATE_FLG", "0");
                vct.add(dataStore.getItemString(newrows[i], "ORDER_CODE"));
            }
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            TParm inParm = new TParm();
            inParm.setData("ORG_CODE", this.getValueString("TO_ORG_CODE"));
            inParm.setData("ORDER_CODE_LIST", vct);
            inParm.setData("OPT_USER", Operator.getID());
            inParm.setData("OPT_DATE", date);
            inParm.setData("OPT_TERM", Operator.getIP());
            inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
            inParm.setData("REGION_CODE", Operator.getRegion());//========pangben modify 20110621
            // 执行数据新增
            result = TIOM_AppServer.executeAction(
                "action.ind.INDRequestAction", "onUpdateD", inParm);
            // 主项保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_d.setDSValue();
            // 调用打印方法
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                this.onPrint();
            }
            return;
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_A").setSelected(true);
        String clearString =
            "REQUEST_DATE;APP_ORG_CODE;REQUEST_NO;" +
            "REQTYPE_CODE;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;" +
            "URGENT_FLG;SUM_RETAIL_PRICE";
        this.clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REQUEST_DATE", date);
        getComboBox("REQTYPE_CODE").setSelectedIndex(0);
        // 画面状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        getTextField("REQUEST_NO").setEnabled(true);
        getComboBox("APP_ORG_CODE").setEnabled(true);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        action = "insertM";
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();

        TParm result = new TParm();
        // 条件查询
        result = TIOM_AppServer.executeAction(
            "action.ind.INDRequestAction", "onQueryM", onQueryParm());
        // 订购状态条件过滤
        result = onFilterQueryByStatus(result);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("无查询结果");
        }
        else {
            table_m.setParmValue(result);
        }
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            this.messageBox("单据已完成不可删除");
        }
        else {
            if (row_m > -1) {
                if (this.messageBox("删除", "确定是否删除申请单", 2) == 0) {
                    TParm parm = new TParm();
                    // 细项信息
                    if (table_d.getRowCount() > 0) {
                        table_d.removeRowAll();
                        String deleteSql[] = table_d.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // 主项信息
                    parm.setData("REQUEST_NO", request_no);
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.ind.INDRequestAction", "onDeleteM", parm);
                    // 删除判断
                    if (result.getErrCode() < 0) {
                        this.messageBox("删除失败");
                        return;
                    }
                    table_m.removeRow(row_m);
                    this.messageBox("删除成功");
                    onClear();
                }
            }
            else {
                if (this.messageBox("删除", "确定是否删除申请单细项", 2) == 0) {
                    if ("".equals(table_d.getDataStore().getItemString(row_d,
                        "REQUEST_NO"))) {
                        table_d.removeRow(row_d);
                        return;
                    }
                    table_d.removeRow(row_d);
                    // 细项保存判断
                    if (!table_d.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_d.setDSValue();
                    //onQuery();
                }
            }
        }
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("REQUEST_NO"))) {
            this.messageBox("不存在退库单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         this.getComboBox("REQTYPE_CODE").getSelectedName()+"单");
            date.setData("ORG_CODE_OUT", "TEXT", "调出部门: " +
                         this.getComboBox("APP_ORG_CODE").getSelectedName());
            date.setData("ORG_CODE_IN", "TEXT", "调入部门: " +
                         this.getComboBox("TO_ORG_CODE").getSelectedName());
            date.setData("REQ_NO", "TEXT",
                         "退库单号: " + this.getValueString("REQUEST_NO"));
            date.setData("REQ_TYPE", "TEXT",
                         "请领类别: " +
                         this.getComboBox("REQTYPE_CODE").getSelectedName());
            date.setData("DATE", "TEXT",
                         "制表日期: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));

            // 表格数据
            TParm parm = new TParm();
            String order_code = "";
            String unit_type = "0";
            String order_desc = "";
            //luhai modify add 采购价总额 2012-1-23
            double verifyin_amt=0;
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code, unit_type)));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("药品信息有误");
                    return;
                }
                if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
                    order_desc = inparm.getValue("ORDER_DESC", 0);
                }
                else {
                    order_desc = inparm.getValue("ORDER_DESC", 0) + "(" +
                        inparm.getValue("GOODS_DESC", 0) + ")";
                }
                parm.addData("ORDER_DESC", order_desc);
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("QTY",
                             table_d.getItemDouble(i, "QTY"));
                parm.addData("VERIFYIN_PRICE",
                		table_d.getItemDouble(i, "VERIFYIN_PRICE"));
                parm.addData("UNIT_PRICE",
                             table_d.getItemDouble(i, "RETAIL_PRICE"));
                parm.addData("VERIFYIN_PRICE_AMT", StringTool.round(table_d.getItemDouble(i,
                "VERIFYIN_PRICE") * table_d.getItemDouble(i, "QTY"), 2));
                //luhai 2012-1-23 add verifyin_price_amt begin
                verifyin_amt+=StringTool.round(table_d.getItemDouble(i,
                "VERIFYIN_PRICE") * table_d.getItemDouble(i, "QTY"), 2);
                //luhai 2012-1-23 add verifyin_price_amt end
                parm.addData("AMT", StringTool.round(table_d.getItemDouble(i,
                    "RETAIL_PRICE") * table_d.getItemDouble(i, "QTY"), 2));
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
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE_AMT");
            parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            //System.out.println("PARM---" + parm);
            date.setData("TABLE", parm.getData());

            // 表尾数据
            //luhai modify 2012-1-23 begin
//            date.setData("TOT", "TEXT", "合计: " +
//                         StringTool.round(Double.parseDouble(this.
            date.setData("TOT", "TEXT", "合计: ");
            date.setData("RETAIL_TOT", "TEXT", StringTool.round(Double.parseDouble(this.
                    getValueString("SUM_RETAIL_PRICE")),
                    2));
            date.setData("VERIFY_TOT", "TEXT", StringTool.round(verifyin_amt,
                    2));
            date.setData("TOT_AMT_DESC", "TEXT", StringUtil.getInstance().numberToWord(StringTool.round(verifyin_amt,2)));
            //luhai modify 2012-1-23 end
            date.setData("USER", "TEXT", "制表人: " + Operator.getName());
            // 调用打印方法
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\RequestRet.jhw",
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
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // 主项表格选中行数据上翻
            getTableSelectValue(table_m, row_m, pageItem);
            // 设定页面状态
            getTextField("REQUEST_NO").setEnabled(false);
            getComboBox("APP_ORG_CODE").setEnabled(false);
            getComboBox("REQTYPE_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            action = "updateM";
            // 单据类别
            request_type = getValueString("REQTYPE_CODE");
            // 申请单号
            request_no = getValueString("REQUEST_NO");
            // 明细信息
            getTableDInfo(request_no);
            // 添加一行细项
            onAddRow();
            table_d.setSelectionMode(0);
        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            action = "updateD";
            table_m.setSelectionMode(0);
            row_m = -1;
            // 取得SYS_FEE信息，放置在状态栏上
            /*
             String order_code = table_d.getDataStore().getItemString(table_d.
                getSelectedRow(), "ORDER_CODE");
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
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_code))
            table_d.getDataStore().setItem(table_d.getSelectedRow(),
                                           "ORDER_CODE", order_code);

        // 检查出库部门是否存在该药品
        TParm qtyParm = new TParm();
        qtyParm.setData("ORG_CODE", this.getValueString("APP_ORG_CODE"));
        qtyParm.setData("ORDER_CODE", order_code);
        TParm getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
        if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
            this.messageBox("退库部门不存在药品:" + order_desc + "(" + order_code + ")");
            table_d.removeRow(table_d.getSelectedRow());
            onAddRow();
            return;
        }

        TParm result = new TParm(TJDODBTool.getInstance().select(INDSQL.
            getPHAInfoByOrder(order_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            this.messageBox("药品信息错误");
            return;
        }

        // 查询药品的批次和效期
        String sql = INDSQL.getOrderBatchNoValid(this.getValueString(
            "APP_ORG_CODE"), order_code, "0");
        TParm addParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (addParm == null || addParm.getErrCode() < 0) {
            this.messageBox("药品信息错误");
            return;
        }
        if (addParm.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有药品信息");
            return;
        }
        // 只有一个批号和效期的数据
        if (addParm.getCount("ORDER_CODE") == 1) {
            // 批次
            table_d.setItem(table_d.getSelectedRow(), "BATCH_NO", addParm
                            .getValue("BATCH_NO", 0));
            // 有效期
            table_d.setItem(table_d.getSelectedRow(), "VALID_DATE",
                            addParm.getTimestamp("VALID_DATE", 0));
            //***********************************************************
            //luhai 2012-01-12 modify 加入batchSeq verifyinPrice begin
            //***********************************************************
            //加入batch_seq
            table_d.setItem(table_d.getSelectedRow(), "BATCH_SEQ",
            		addParm.getValue("BATCH_SEQ",0));
            //加入verfiyin_price
            double tranUnitQty = INDTool.getInstance().getPhaTransUnitQty(order_code, "2")*INDTool.getInstance().getPhaTransUnitQty(order_code, "1");
            table_d.setItem(table_d.getSelectedRow(), "VERIFYIN_PRICE",
            		addParm.getDouble("VERIFYIN_PRICE",0));//*tranUnitQty
            //***********************************************************
            //luhai 2012-01-12 modify 加入batchSeq verifyinPrice end
            //***********************************************************
            
        }
        // 存在多批号和效期的数据
        else if (addParm.getCount("ORDER_CODE") > 1) {
            // 调用批次效期查询界面
            qtyParm.setData("UNIT_TYPE", "0");
            Object resultParm = openDialog("%ROOT%\\config\\ind\\INDForVaild.x",
                                           qtyParm);
            addParm = (TParm) resultParm;
            // 批次
            table_d.setItem(table_d.getSelectedRow(), "BATCH_NO", addParm
                            .getValue("BATCH_NO"));
            // 有效期
            table_d.setItem(table_d.getSelectedRow(), "VALID_DATE",
                            addParm.getTimestamp("VALID_DATE"));
            //***********************************************************
            //luhai 2012-01-12 modify 加入batchSeq verifyinPrice begin
            //***********************************************************
            //加入batch_seq
            table_d.setItem(table_d.getSelectedRow(), "BATCH_SEQ",
            		addParm.getValue("BATCH_SEQ"));
            //加入verfiyin_price
            double tranUnitQty = INDTool.getInstance().getPhaTransUnitQty(order_code, "2")*INDTool.getInstance().getPhaTransUnitQty(order_code, "1");
            table_d.setItem(table_d.getSelectedRow(), "VERIFYIN_PRICE",
            		addParm.getDouble("VERIFYIN_PRICE"));//*tranUnitQty luhai 2012-05-03 删除转换率
            //***********************************************************
            //luhai 2012-01-12 modify 加入batchSeq verifyinPrice end
            //***********************************************************
        }
        double stock_price = 0;
        double retail_price = 0;
        // 库存单位
        table_d.setItem(table_d.getSelectedRow(), "UNIT_CODE", result
                        .getValue("STOCK_UNIT", 0));
        stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0)
                                       * result.getDouble("DOSAGE_QTY", 0),
                                       2);
        retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0)
                                        * result.getDouble("DOSAGE_QTY", 0),
                                        2);
        // 成本价
        table_d.getDataStore().setItem(table_d.getSelectedRow(), "STOCK_PRICE",
                                       stock_price);
        // 零售价
        table_d.setItem(table_d.getSelectedRow(), "RETAIL_PRICE", retail_price);
        // 设定选中行的有效性
        table_d.getDataStore().setActive(table_d.getSelectedRow(), true);
        onAddRow();
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
        TTable table = node.getTable();
        String columnName = table.getDataStoreColumnName(node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("转出数量不能小于或等于0");
                return true;
            }
            table.getDataStore().setItem(row, "QTY", qty);
            this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
            return false;
        }
        return true;
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        /**
         * 权限控制
         * 权限1:一般个人只显示自已所属科室
         * 权限9:最大权限显示全院药库部门
         */
        // 显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(),
                                  Operator.getRegion(), " AND B.ORG_TYPE = 'B' ")));
            getComboBox("APP_ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("APP_ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }

        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        // 初始化申请时间
        setValue("REQUEST_DATE", date);
        // 初始化申请单类型
        getComboBox("REQTYPE_CODE").setSelectedIndex(0);
        // 初始化画面状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 0;
        action = "insertM";
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
     * 根据申请单号取得细项信息并显示在细项表格上
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        // 明细信息
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getRequestDByNo(req_no));
        tds.retrieve();
        if (tds.rowCount() == 0) {
            this.messageBox("没有申请明细");
            seq = 1;
        }
        else {
            seq = getMaxSeq(tds, "SEQ_NO");
        }

        // 观察者
        IndRequestObserver obser = new IndRequestObserver();
        tds.addObserver(obser);
        table_d.setDataStore(tds);
        table_d.setDSValue();
        this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
        this.setValue("SUM_VERIFYIN_AMT", getSumVerifyPrice());
    }

    /**
     * 添加一行细项
     */
    private void onAddRow() {
        // 有未编辑行时返回
        if (!this.isNewRow())
            return;
        int row = table_d.addRow();
        TParm parm = new TParm();
        parm.setData("ACTIVE", false);
        table_d.getDataStore().setActive(row, false);
    }

    /**
     * 是否有未编辑行
     *
     * @return boolean
     */
    private boolean isNewRow() {
        Boolean falg = false;
        TParm parmBuff = table_d.getDataStore().getBuffer(
            table_d.getDataStore().PRIMARY);
        int lastRow = parmBuff.getCount("#ACTIVE#");
        Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
        if (obj != null) {
            falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
        }
        else {
            falg = true;
        }
        return falg;
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("退库部门不能为空");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("单据类别不能为空");
            return false;
        }
        if ("".equals(getValueString("TO_ORG_CODE"))) {
            this.messageBox("接受部门不能为空");
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
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            if (table_d.getItemDouble(i, "QTY") <= 0) {
                this.messageBox("转出数量不能小于或等于0");
                return false;
            }
        }
        return true;
    }

    /**
     * 获得新增主项的数据PARM
     *
     * @param parm
     * @return
     */
    private TParm getRequestMParm(TParm parm) {
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        parm.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parm.setData("REQUEST_USER", Operator.getID());
        parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
        parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
        String unit_type = "0";
        parm.setData("UNIT_TYPE", unit_type);
        parm.setData("URGENT_FLG", getValueString("URGENT_FLG"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());
        return parm;
    }

    /**
     * 查询条件参数
     *
     * @return
     */
    private TParm onQueryParm() {
        TParm parm = new TParm();
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
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
        return parm;
    }

    /**
     * 根据订购状态过滤查询结果
     *
     * @param parm
     * @return
     */
    private TParm onFilterQueryByStatus(TParm parm) {
        String update_flg = "0";
        boolean flg = false;
        TDataStore ds = new TDataStore();
        for (int i = parm.getCount("REQUEST_NO") - 1; i >= 0; i--) {
            ds.setSQL(INDSQL.getRequestDByNo(parm.getValue("REQUEST_NO", i)));
            ds.retrieve();
            if (ds.rowCount() == 0) {
                flg = false;
            }
            else {
                flg = true;
                for (int j = 0; j < ds.rowCount(); j++) {
                    update_flg = ds.getItemString(j, "UPDATE_FLG");
                    if ("0".equals(update_flg) || "1".equals(update_flg)) {
                        // 未完成
                        flg = false;
                        break;
                    }
                }
            }
            // 订购状态
            if (getRadioButton("UPDATE_FLG_A").isSelected()) {
                // 未完成
                if (flg) {
                    parm.removeRow(i);
                }
            }
            else {
                // 完成
                if (!flg) {
                    parm.removeRow(i);
                }
            }
        }

        // 没有全部部门权限
        if (!dept_flg) {
            TParm optOrg = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion())));
            for (int i = parm.getCount("APP_ORG_CODE") - 1; i >= 0; i--) {
                boolean check_flg = true;
                for (int j = 0; j < optOrg.getCount("ID"); j++) {
                    if (parm.getValue("APP_ORG_CODE", i).equals(optOrg.getValue(
                        "ID", j))) {
                        check_flg = false;
                        break;
                    }
                    else {
                        continue;
                    }
                }
                if (check_flg) {
                    parm.removeRow(i);
                }
            }
        }

        return parm;
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
     * 计算零售总金额
     *
     * @return
     */
    private double getSumRetailPrice() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            double amount1 = table_d.getItemDouble(i, "QTY");
            sum += table_d.getItemDouble(i, "RETAIL_PRICE") * amount1;
        }
        return StringTool.round(sum, 2);
    }
    /**
     * 计算采购总金额
     *luhai add 2012-2-13 
     * @return
     */
    private double getSumVerifyPrice() {
    	table_d.acceptText();
    	double sum = 0;
    	for (int i = 0; i < table_d.getRowCount(); i++) {
    		if (!table_d.getDataStore().isActive(i)) {
    			continue;
    		}
    		double amount1 = table_d.getItemDouble(i, "QTY");
    		sum += table_d.getItemDouble(i, "VERIFYIN_PRICE") * amount1;
    	}
    	return StringTool.round(sum, 2);
    }

    /**
     * 出库部门库存及药品主档的判断
     */
    private boolean getOrgStockCheck() {
        /** 供应部门药品录入管控 */
        String org_code = getValueString("APP_ORG_CODE");
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        int batch_seq = 0;
        double stock_qty = 0;
        double qty = 0;

        for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            order_code = table_d.getDataStore().getItemString(i,
                "ORDER_CODE");
            qty = table_d.getDataStore().getItemDouble(i, "QTY");
            TParm resultParm = new TParm();
            if ("0".equals(getValueString("UNIT_TYPE"))) {
                String sql = INDSQL.getPHAInfoByOrder(order_code);
                resultParm = new TParm(TJDODBTool.getInstance().select(sql));
                if (resultParm.getErrCode() < 0) {
                    this.messageBox("药品信息错误");
                    return true;
                }
                qty = StringTool.round(qty *
                                       resultParm.getDouble("DOSAGE_QTY", 0), 2);
            }
            batch_no = table_d.getItemString(i, "BATCH_NO");
            valid_date = TypeTool.getString(table_d.getItemData(i, "VALID_DATE")).
                substring(0, 10);
            /*************batch_seq从页面取得，不用再查询 by liyh 20120910 start***************************/
/*            TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndStockBatchSeq(org_code, order_code, batch_no, valid_date)));
            if (inparm.getErrCode() < 0) {
                this.messageBox("药品信息错误");
                return false;
            }
            batch_seq = inparm.getInt("BATCH_SEQ", 0);*/
            TParm inparm  = new TParm();
            //取得页面的batch_seq  by liyh 20120910
            String batchSeq = table_d.getDataStore().getItemString(i,"BATCH_SEQ");
            if(null != batchSeq)
            	batch_seq = Integer.valueOf(batchSeq);
            /*************batch_seq从页面取得，不用再查询 by liyh 20120910 end***************************/
            inparm.setData("ORG_CODE", org_code);
            inparm.setData("ORDER_CODE", order_code);
            inparm.setData("BATCH_SEQ", batch_seq);
            TParm resultQTY = IndStockDTool.getInstance().
                onQueryStockQTYByBatch(inparm);
            if (resultQTY.getErrCode() < 0) {
                this.messageBox("药品信息错误");
                return true;
            }            
            stock_qty = resultQTY.getDouble("QTY", 0);
            if (stock_qty < 0) {
                stock_qty = 0;
            }
            if (qty > stock_qty) {
                TParm order = INDTool.getInstance().getSysFeeOrder(
                    order_code);
                if (this.messageBox("提示",
                                    "药品:" + order.getValue("ORDER_DESC") + "(" +
                                    order_code + ") 库存数量不足,当前库存量为" +
                                    stock_qty + ",是否继续", 2) == 0) {
                    return true;
                }
                return false;
            }
        }
        return true;
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
