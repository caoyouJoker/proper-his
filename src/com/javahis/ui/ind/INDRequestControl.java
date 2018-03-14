package com.javahis.ui.ind;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.ind.INDTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.DateTool;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndRequestObserver;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 申请管理Control
 * </p>
 *
 * <p>
 * Description: 申请管理Control
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
 * @author zhangy 2009.05.23
 * @version 1.0
 */

public class INDRequestControl
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

    // 申请部门
    private String app_org;

    // 单据类型
    private String request_type;

    // 细表表头
    private String table_header;

    // 细表对应字段
    private String table_parmMap;

    // 细表锁定列
    private String table_lockColumn;

    // 细表对齐方式
    private String table_align;

    // 单位类型
    private String u_type;

    public INDRequestControl() {
        super();
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
     * 查询方法
     */
    public void onQuery() {
        // 条件查询
        TParm result = TIOM_AppServer.executeAction(
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
     * 清空方法
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_A").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;REQUEST_DATE;APP_ORG_CODE;REQUEST_NO;"
            +
            "REQTYPE_CODE;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;ORDER_DESC_FLG"
            +
            "URGENT_FLG;CHECK_FLG;STOCK_PRICE_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
            + "SUM_VERIFYIN_PRICE";
        this.clearValue(clearString);
        Timestamp date = StringTool.getTimestamp(new Date());
        setValue("REQUEST_DATE", date);
        // 画面状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        getTextField("REQUEST_NO").setEnabled(true);
        getComboBox("REQTYPE_CODE").setEnabled(true);
        getComboBox("APP_ORG_CODE").setEnabled(true);
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        action = "insertM";
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
            parm = getInsertMParm(parm);
            if ("insertM".equals(action)) {
                // 请领单号
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
            Timestamp date = StringTool.getTimestamp(new Date());
            table_d.acceptText();
            TDataStore dataStore = table_d.getDataStore();
            // 获得全部改动的行号
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // 获得全部的新增行
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
            // 给固定数据配数据
            for (int i = 0; i < newrows.length; i++) {
                dataStore.setItem(newrows[i], "REQUEST_NO",
                                  getValueString("REQUEST_NO"));
                dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
                dataStore.setItem(newrows[i], "UPDATE_FLG", "0");
            }
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            // 出库部门库存及药品主档的判断
            if (!getOrgStockCheck()) {
                table_d.removeRowAll();
                return;
            }
            // 细项保存判断
            if (!table_d.update()) {
                messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_d.setDSValue();
            return;
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
                    parm.setData("REQTYPE_CODE", request_no);
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
                    table_d.removeRow(row_d);
                    // 细项保存判断
                    if (!table_d.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_d.setDSValue();
                    onQuery();
                }
            }
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
            // 根据单据类别判断批次和有效期是否显示
            changeTableDHeader(request_type);
            // 申请单号
            request_no = getValueString("REQUEST_NO");
            // 明细信息
            getTableDInfo(request_no);
            // 添加一行细项
            onAddRow();
            table_d.setSelectionMode(0);
            row_d = -1;
            if ("TEC".equals(request_type) || "EXM".equals(request_type)
                || "COS".equals(request_type)) {
                u_type = "2";
            }
            else {
                u_type = "1";
            }
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
        }
    }

    /**
     * 申请部门变更事件
     */
    public void onChangeAppOrg() {
        app_org = getValueString("APP_ORG_CODE");
        // 根据所选部门类型设定单据类别
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getINDOrgType(app_org)));
        String type = parm.getValue("ORG_TYPE", 0);
        String request_type = "";
        getComboBox("REQTYPE_CODE").setSelectedIndex( -1);
        if ("A".equals(type)) {
            // 主库
            request_type = "[[id,name],[,],[WAS,损耗],[THO,其它出库],"
                + "[THI,其它入库]]";
        }
        else if ("B".equals(type)) {
            // 中库
            request_type = "[[id,name],[,],[DEP,部门请领],[GIF,药房调拨],"
                + "[RET,退库],[WAS,损耗],[THO,其它出库],[THI,其它入库]]";
        }
        else {
            // 小库
            request_type = "[[id,name],[,],[TEC,备药生成],[EXM,补充计费],"
                + "[COS,卫耗材领用]]";
        }
        getComboBox("REQTYPE_CODE").setStringData(request_type);
        getComboBox("REQTYPE_CODE").onQuery();
    }

    /**
     * 单据类别变更事件
     */
    public void onChangeRequestType() {
        String request_type = getValueString("REQTYPE_CODE");
        TComboOrgCode org_code = (TComboOrgCode) getComboBox("TO_ORG_CODE");
        org_code.setEnabled(true);
        org_code.setSelectedIndex( -1);
        // 根据所选单据类别设定接受部门
        if ("DEP".equals(request_type))
            org_code.setOrgType("A");
        else if ("GIF".equals(request_type))
            org_code.setOrgType("B");
        else if ("RET".equals(request_type))
            org_code.setOrgType("A");
        else if ("WAS".equals(request_type))
            org_code.setEnabled(false);
        else if ("THO".equals(request_type))
            org_code.setEnabled(false);
        else if ("THI".equals(request_type))
            org_code.setEnabled(false);
        else if ("TEC".equals(request_type))
            org_code.setOrgType("B");
        else if ("EXM".equals(request_type))
            org_code.setOrgType("B");
        else if ("COS".equals(request_type))
            org_code.setOrgType("B");
        org_code.onQuery();
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
        parm.setData("ODI_ORDER_TYPE", "A");
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
        if (!StringUtil.isNullString(order_code))
            table_d.getDataStore().setItem(row_d, "ORDER_CODE", order_code);
        String sql = INDSQL.getPHAInfoByOrder(order_code);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            this.messageBox("药品信息错误");
            return;
        }

        double stock_price = 0;
        double retail_price = 0;
        if ("1".equals(getValueString("UNIT_TYPE"))) {
            // 库存单位
            table_d.setItem(row_d, "UNIT_CODE", result
                            .getValue("STOCK_UNIT", 0));
            stock_price = StringTool.round(result.getDouble("STOCK_PRICE", 0)
                                           * result.getDouble("DOSAGE_QTY", 0),
                                           2);
            retail_price = StringTool.round(result.getDouble("RETAIL_PRICE", 0)
                                            * result.getDouble("DOSAGE_QTY", 0),
                                            2);
        }
        else {
            // 配药单位
            table_d.setItem(row_d, "UNIT_CODE", result.getValue("DOSAGE_UNIT",
                0));
            stock_price = result.getDouble("STOCK_PRICE", 0);
            retail_price = result.getDouble("RETAIL_PRICE", 0);
        }
        // 成本价
        table_d.setItem(row_d, "STOCK_PRICE", stock_price);
        // 零售价
        table_d.setItem(row_d, "RETAIL_PRICE", retail_price);
        // 设定选中行的有效性
        table_d.getDataStore().setActive(row_d, true);

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
                this.messageBox("申请数量不能小于或等于0");
                return true;
            }
            table.getDataStore().setItem(row, "QTY", qty);
            this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
            this.setValue("SUM_VERIFYIN_PRICE", getSumStockPrice());
            this.setValue("PRICE_DIFFERENCE", StringTool.round(
                getSumRetailPrice() - getSumStockPrice(), 2));
            return false;
        }
        if ("STOCK_PRICE".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("成本价价不能小于或等于0");
                return true;
            }
            table.getDataStore().setItem(row, "STOCK_PRICE", qty);
            this.setValue("SUM_RETAIL_PRICE", getSumRetailPrice());
            this.setValue("SUM_VERIFYIN_PRICE", getSumStockPrice());
            this.setValue("PRICE_DIFFERENCE", StringTool.round(
                getSumRetailPrice() - getSumStockPrice(), 2));
            return false;
        }
        // 批次序号
        if ("BATCH_NO".equals(columnName)) {
            String value = TypeTool.getString(node.getValue());
            if ("".equals(value)) {
                this.messageBox("批次序号不能为空");
                return true;
            }
            return false;
        }
        // 有效期
        if ("VALID_DATE".equals(columnName)) {
            String value = TypeTool.getString(node.getValue());
            if (!DateTool.checkDate(value, "yyyy/MM/dd")) {
                this.messageBox("时间格式不正确");
                return true;
            }
            return false;
        }
        return true;
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化验收时间
        Timestamp date = StringTool.getTimestamp(new Date());
        setValue("REQUEST_DATE", date);
        // 初始化画面状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 0;
        action = "insertM";
        table_header = table_d.getHeader();
        table_parmMap = table_d.getParmMap();
        table_lockColumn = table_d.getLockColumns();
        table_align = "0,left;1,left;2,right;3,left;4,right;"
            + "5,right;6,right;7,right;8,right;9,left;"
            + "10,left;11,right";
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
            this.messageBox("申请部门不能为空");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("单据类别不能为空");
            return false;
        }
        TComboOrgCode org_code = (TComboOrgCode) getComboBox("TO_ORG_CODE");
        if (org_code.isEnabled()) {
            if ("".equals(getValueString("TO_ORG_CODE"))) {
                this.messageBox("接受部门不能为空");
                return false;
            }
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
                this.messageBox("申请数量不能小于或等于0");
                return false;
            }
            if (table_d.getItemDouble(i, "STOCK_PRICE") <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return false;
            }
            if (table_d.getParmMap().indexOf("BATCH_NO") > 0) {
                if ("".equals(table_d.getItemString(i, "BATCH_NO"))) {
                    this.messageBox("批次不能为空");
                    return false;
                }
                if ("".equals(table_d.getItemString(i, "VALID_DATE"))) {
                    this.messageBox("效期不能为空");
                    return false;
                }
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
     * 进货总金额
     *
     * @return
     */
    private double getSumStockPrice() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if (!table_d.getDataStore().isActive(i)) {
                continue;
            }
            double amount1 = table_d.getItemDouble(i, "QTY");
            sum += table_d.getItemDouble(i, "STOCK_PRICE") * amount1;
        }
        return StringTool.round(sum, 2);
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
    }

    /**
     * 根据单据类别改变明细表的表头
     *
     * @param req_type
     */
    private void changeTableDHeader(String req_type) {
        // 无批次和有效期的表头
        String table_header1 = "药品名称,120;规格,120;申请数量,80,double,#####0;"
            + "单位,40,UNIT;成本价,60,double,#####0.00;成本金额,80,double,#####0.00;"
            + "零售价,60,double,#####0.00;零售金额,80,double,#####0.00;"
            + "进销差价,80,double,#####0.00;累计完成量,100,double,#####0;"
            + "中止,40,boolean";
        // 无批次和有效期的对应字段
        String table_parmMap1 =
            "ORDER;SPECIFICATION;QTY;UNIT_CODE;STOCK_PRICE;"
            +
            "SUM_STOCK_PRICE;RETAIL_PRICE;SUM_RETAIL_PRICE;DIFF_SUM;ACTUAL_QTY;"
            + "END_FLG";
        // 无批次和有效期的锁定列
        String table_lockColumn1 = "1,3,5,6,7,8,9,10";
        // 无批次和有效期的对齐方式
        String table_align1 = "0,left;1,left;2,right;3,left;4,right;"
            + "5,right;6,right;7,right;8,right;9,right";
        String header = "";
        String parmMap = "";
        String lockColumns = "";
        String align = "";
        // 根据所选单据类别设定接受部门
        if ("DEP".equals(req_type) || "TEC".equals(req_type)
            || "EXM".equals(req_type) || "GIF".equals(req_type)
            || "COS".equals(req_type)) {
            header = table_header1;
            parmMap = table_parmMap1;
            lockColumns = table_lockColumn1;
            align = table_align1;
        }
        else {
            header = table_header;
            parmMap = table_parmMap;
            lockColumns = table_lockColumn;
            align = table_align;
        }
        table_d.setHeader(header);
        table_d.setParmMap(parmMap);
        table_d.setLockColumns(lockColumns);
        table_d.setColumnHorizontalAlignmentData(align);
    }

    /**
     * 获得新增主项的数据PARM
     *
     * @param parm
     * @return
     */
    private TParm getInsertMParm(TParm parm) {
        Timestamp date = StringTool.getTimestamp(new Date());
        parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
        parm.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parm.setData("REQUEST_USER", Operator.getID());
        parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
        parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
        String unit_type = "1";
        if ("TEC".equals(getValueString("REQTYPE_CODE"))
            || "EXM".equals(getValueString("REQTYPE_CODE"))
            || "COS".equals(getValueString("REQTYPE_CODE"))) {
            unit_type = "2";
        }
        parm.setData("UNIT_TYPE", unit_type);
        parm.setData("URGENT_FLG", getValueString("URGENT_FLG"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        return parm;
    }

    /**
     * 出库部门库存及药品主档的判断
     */
    private boolean getOrgStockCheck() {
        /** 接受申请部门药品录入管控 */
        String org_code = "";
        String order_code = "";
        double stock_qty = 0;
        double qty = 0;
        if ("DEP".equals(request_type) || "EXM".equals(request_type)
            || "TEC".equals(request_type) || "COS".equals(request_type))
            org_code = getValueString("TO_ORG_CODE");
        else if ("GIF".equals(request_type) || "RET".equals(request_type)
                 || "WAS".equals(request_type) || "THO".equals(request_type))
            org_code = getValueString("APP_ORG_CODE");
        else
            org_code = "";
        if (!"".equals(org_code)) {
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                qty = table_d.getDataStore().getItemDouble(i, "QTY");
                TParm resultParm = new TParm();
                if ("1".equals(getValueString("UNIT_TYPE"))) {
                    String sql = INDSQL.getPHAInfoByOrder(order_code);
                    resultParm = new TParm(TJDODBTool.getInstance().select(sql));
                    if (resultParm.getErrCode() < 0) {
                        this.messageBox("药品信息错误");
                        return false;
                    }
                    qty = StringTool.round(qty
                                           // * resultParm.getDouble("STOCK_QTY", 0)
                                           *
                                           resultParm.getDouble("DOSAGE_QTY", 0),
                                           2);
                }
                TParm inparm = new TParm();
                inparm.setData("ORG_CODE", org_code);
                inparm.setData("ORDER_CODE", order_code);
                TParm resultQTY = INDTool.getInstance().getStockQTY(inparm);
                if (resultQTY.getDouble("QTY", 0) <= 0) {
                    this.messageBox(org_code + "部门无此药品");
                    return false;
                }
                // qty = table_d.getDataStore().getItemDouble(i, "QTY");
                stock_qty = resultQTY.getDouble("QTY", 0);
                // if ("1".equals(u_type)) {
                // if (qty * resultParm.getDouble("DOSAGE_QTY", i) > stock_qty)
                // {
                // this.messageBox(org_code + "部门库存数量不足其库存量为" + stock_qty);
                // return false;
                // }
                // } else {
                if (qty > stock_qty) {
                    this.messageBox(org_code + "部门库存数量不足其库存量为" + stock_qty);
                    return false;
                }
                // }
            }
        }
        return true;
    }
}
