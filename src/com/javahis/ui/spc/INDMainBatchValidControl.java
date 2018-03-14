package com.javahis.ui.spc;

import java.awt.event.KeyEvent;
import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.manager.IndMainBatchValidObserver;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 药库部门安全库存量设定
 * </p>
 *
 * <p>
 * Description: 药库部门安全库存量设定
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
 * @author zhangy 2009.04.29
 * @version 1.0
 */

public class INDMainBatchValidControl
    extends TControl {

    public static String headName = "";
    public static String org_type = "";

    public INDMainBatchValidControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TTable table = getTable("TABLE_M");
        table.removeRowAll();

        String org_code = getValueString("ORG_CODE");
        if ("".equals(org_code)) {
            this.messageBox("部门代码不能为空");
            return;
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getINDOrgType(org_code)));
        org_type = result.getValue("ORG_TYPE", 0);
        String lockColumns = "0,1,4,5,10,11,15,16,17";

        // 得到头文件
        String tablehead = headName;
        if ("A".equals(org_type)) {
            lockColumns = "0,1,2,3,4,5,10,11,15,16,17";
            table.setHeader(tablehead);
        }
        else if ("B".equals(org_type)) {
            table.setHeader(tablehead);
        }
        else {
            tablehead =
                "药品代码,80;药品名称,120;拨补,40,boolean;供应部门,100,DISPENSE_ORG_CODE_B;"
                +
                "料位,80,MATERIAL_LOC_CODE;单位,60,DOSAGE_UNIT;安全存量,80,double,#####0;"
                +
                "最高存量,80,double,#####0;最低存量,80,double,#####0;拨补(采购)量,100,double,#####0;"
                +
                "在途量,80,double,#####0;库存量,80,double,#####0;平均日耗用量,100,double,#####0;"
                +
                "常备量,80,double,#####0;计算方式,100,QTY_TYPE;操作人员,80;操作时间,100;操作IP,80";
            table.setHeader(tablehead);
        }
        // 设置TABLE_M锁定列
        table.setLockColumns(lockColumns);
        String filterString = "ORG_CODE = '" + org_code + "'";
        String order_code = getValueString("ORDER_CODE");
        if (order_code.length() > 0)
            filterString += " AND ORDER_CODE = '" + order_code + "'";
        String material_code = getValueString("MATERIAL_LOC_CODE");
        if (material_code.length() > 0)
            filterString += " AND MATERIAL_LOC_CODE = '" + material_code + "'";

        TDS dataStroe = new TDS();
        dataStroe.setSQL(INDSQL.getINDStockM());
        dataStroe.retrieve();
        // 观察者
        IndMainBatchValidObserver obser = new IndMainBatchValidObserver();
        dataStroe.addObserver(obser);
        table.setDataStore(dataStroe);
        table.setFilter(filterString);
        table.filter();
        table.setDSValue();
        table.acceptText();
        // 低于安全库存量CHECKBOX
        if ("Y".equals(getValueString("STOCK_QTY"))) {
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                if (table.getItemDouble(i, "SUM_STOCK_QTY") >= table
                    .getItemDouble(i, "SAFE_QTY")) {
                    table.removeRow(i);
                }
            }
        }
        // 高于最高存量CHECKBOX
        if ("Y".equals(getValueString("MAX_QTY"))) {
            for (int i = table.getRowCount() - 1; i >= 0; i--) {
                if (table.getItemDouble(i, "SUM_STOCK_QTY") <= table
                    .getItemDouble(i, "MAX_QTY")) {
                    table.removeRow(i);
                }
            }
        }
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空VALUE
        String clear =
            "ORG_CODE;ORDER_CODE;ORDER_DESC;MATERIAL_LOC_CODE;STOCK_QTY;MAX_QTY";
        this.clearValue(clear);
        getTable("TABLE_M").setSelectionMode(0);
        getTable("TABLE_D").setSelectionMode(0);
    }

    /**
     * 保存方法
     */
    public void onSave() {
        Timestamp date = StringTool.getTimestamp(new Date());
        // 主档
        TTable tableM = (TTable) getComponent("TABLE_M");
        // 接收文本
        tableM.acceptText();
        TDataStore dataStoreM = tableM.getDataStore();
        // 获得全部改动的行号
        int rowsM[] = dataStoreM.getModifiedRows(dataStoreM.PRIMARY);
        String dis_flg = "";
        String dis_code = "";
        // 给固定数据配数据
        for (int i = 0; i < rowsM.length; i++) {
            // 数据检核
            dis_flg = dataStoreM.getItemString(rowsM[i], "DISPENSE_FLG");
            dis_code = dataStoreM.getItemString(rowsM[i], "DISPENSE_ORG_CODE");
            if ("Y".equals(dis_flg) && "".equals(dis_code)) {
                this.messageBox("拨补勾选时必须选择拨补部门");
                return;
            }
            if ("N".equals(dis_flg) && !"".equals(dis_code)) {
                this.messageBox("选择拨补部门时必须勾选拨补");
                return;
            }
            if ("1".equals(dataStoreM.getItemString(rowsM[i], "QTY_TYPE"))) {
                double dd_use_qty = dataStoreM.getItemDouble(rowsM[i],
                    "DD_USE_QTY");
                if (dd_use_qty <= 0) {
                    this.messageBox("以天计算时平均日耗用量必须大于0");
                    return;
                }
            }
            if ("C".equals(org_type)) {
                double standing_qty = dataStoreM.getItemDouble(rowsM[i],
                    "STANDING_QTY");
                if (standing_qty <= 0) {
                    this.messageBox("小库常备量必须大于0");
                    return;
                }
            }
            dataStoreM.setItem(rowsM[i], "OPT_USER", Operator.getID());
            dataStoreM.setItem(rowsM[i], "OPT_DATE", date);
            dataStoreM.setItem(rowsM[i], "OPT_TERM", Operator.getIP());
        }
        if (!tableM.update()) {
            messageBox("E0001");
            return;
        }
        tableM.setDSValue();

        // 明细
        TTable tableD = (TTable) getComponent("TABLE_D");
        // 接收文本
        tableD.acceptText();
        TDataStore dataStoreD = tableD.getDataStore();
        // 获得全部改动的行号
        int rowsD[] = dataStoreD.getModifiedRows(dataStoreD.PRIMARY);
        // 给固定数据配数据
        for (int i = 0; i < rowsD.length; i++) {
            // 数据检核
            if ("Y".equals(dataStoreD.getItemString(rowsD[i], "ACTIVE_FLG"))) {
                Timestamp valid_date = dataStoreD.getItemTimestamp(rowsD[i],
                    "VALID_DATE");
                if (valid_date.compareTo(date) >= 0) {
                    this.messageBox("未过有效期不可勾选过期注记");
                    return;
                }
            }
            dataStoreD.setItem(rowsD[i], "OPT_USER", Operator.getID());
            dataStoreD.setItem(rowsD[i], "OPT_DATE", date);
            dataStoreD.setItem(rowsD[i], "OPT_TERM", Operator.getIP());
        }
        if (!tableD.update()) {
            messageBox("E0001");
            return;
        }

        tableD.setDSValue();
        messageBox("P0001");
    }

    /**
     * TABLE_M单击事件
     */
    public void onTableMClicked() {
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            TParm parm = table.getDataStore().getRowParm(row);
            String order_code = parm.getValue("ORDER_CODE");
            String org_code = parm.getValue("ORG_CODE");
            TTable tableD = getTable("TABLE_D");
            TDS dataStroe = new TDS();
            dataStroe.setSQL(INDSQL.getINDStock(org_code, order_code, Operator.getRegion()));
            dataStroe.retrieve();
            // 观察者
            IndMainBatchValidObserver obser = new IndMainBatchValidObserver();
            dataStroe.addObserver(obser);
            tableD.setDataStore(dataStroe);
            tableD.setDSValue();
        }
    }

    /**
     * TABLE_D单击事件
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            TTable tableM = getTable("TABLE_M");
            tableM.setSelectionMode(0);
        }
    }

    /**
     * TABLE_M双击事件
     */
    public void onTableMDoubleClicked() {
        TTable table = getTable("TABLE_M");
        int column = table.getSelectedColumn();
        if (column == 4) {
            // 打开料位查询界面
            Object parm = openDialog("%ROOT%\\config\\ind\\INDMacValid.x",
                                     getValueString("ORG_CODE"));
            if (parm != null) {
                parm = (String) parm;
                if ("".equals(parm)) {
                    return;
                }
                int row = table.getSelectedRow();
                table.setItem(row, column, parm);
            }
        }
    }

    /**
     * TABLE_D双击事件
     */
    public void onTableDDoubleClicked() {
        TTable table = getTable("TABLE_D");
        int column = table.getSelectedColumn();
        if (column == 3) {
            // 打开料位查询界面
            Object parm = openDialog("%ROOT%\\config\\ind\\INDMacValid.x",
                                     getValueString("ORG_CODE"));
            if (parm != null) {
                parm = (String) parm;
                if ("".equals(parm)) {
                    return;
                }
                int row = table.getSelectedRow();
                table.setItem(row, column, parm);
            }
        }
    }

    /**
     * CHECKBOX(STOCK_QTY)改变事件
     */
    public void onChangeSTOCK_QTYSelect() {
        getCheckBox("MAX_QTY").setSelected(false);
    }

    /**
     * CHECKBOX(MAX_QTY)改变事件
     */
    public void onChangeMAX_QTYSelect() {
        getCheckBox("STOCK_QTY").setSelected(false);
    }

    /**
     * 库存部门变更事件
     */
    public void onChangeORG_CODE() {
        // 料位Combo
        TComboINDMaterialloc mac = (TComboINDMaterialloc) getComponent(
            "MATERIAL_LOC_CODE");
        mac.setOrgCode(getValueString("ORG_CODE"));
        mac.onQuery();
    }

    /**
     * 当TextField创建编辑控件时长期
     *
     * @param com
     */
    public void onCreateEditComoponentUD(KeyEvent obj) {
        TTextField textFilter = getTextField("ORDER_CODE");
        textFilter.onInit();
        TParm parm = new TParm();
        parm.setData("ODI_ORDER_TYPE", "A");
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
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC").setValue(order_desc);
        onOrderCodeAction();
    }

    /**
     * 药品代码回车事件
     */
    public void onOrderCodeAction() {
        ( (TComboBox) getComponent("MATERIAL_LOC_CODE")).grabFocus();
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 初始化页面状态
        headName = getTable("TABLE_M").getHeader();
        // 注册激发SYSFeePopup弹出的事件
        callFunction("UI|ORDER_CODE|addEventListener", "ORDER_CODE->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD");
        // 权限管控

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
}
