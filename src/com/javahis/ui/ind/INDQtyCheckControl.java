package com.javahis.ui.ind;

import jdo.ind.INDSQL;
import jdo.ind.IndQtyCheckTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;
import jdo.ind.INDTool;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.javahis.system.combo.TComboINDMaterialloc;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.TTableNode;

/**
 * <p>
 * Title: 盘点管理
 * </p>
 *
 * <p>
 * Description: 盘点管理
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
 * @author zhangy 2009.06.10
 * @version 1.0
 */
public class INDQtyCheckControl
    extends TControl {

    private String org_code;

    private String checkreason;

    private String frozen_date;

    private String check_type;

    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;

    private TParm resultParm;

    private TPanel panel_1;

    private TPanel panel_2;

    private String header = "冻结,40,boolean;药品代码,120;药品名称,150;效期,120;"
        + "批号,80;料位,80;实,60,double,#####0.000;际,60,UNIT_1;"
        + "盘,60,double,#####0.000;点,60,UNIT_2";

    private String lockColumns = "0,1,2,3,4,5,7,9";

    private String columnHorizontal = "1,left;2,left;3,left;4,left;5,left;"
        + "6,right;7,left;8,right;9,left";

    private String parmMap = "STOCK_FLG;ORDER_CODE;ORDER_DESC;VALID_DATE;"
        + "BATCH_NO;MATERIAL_LOC_CODE;ACTUAL_QTY_F;STOCK_UNIT_A;ACTUAL_QTY_M;"
        + "DOSAGE_UNIT_A";

    // 全部部门权限
    private boolean dept_flg = true;

    // 盘点冻结库存
    private boolean frozen_flg = true;

    // 盘点量输入
    private boolean qty_check_flg = true;

    // 解除冻结库存
    private boolean unfrozen_flg = true;

    private String lock_column = "";

    public INDQtyCheckControl() {
        super();
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        // 注册激发SYSFeePopup弹出的事件
        callFunction("UI|ORDER_CODE|addEventListener", "ORDER_CODE->"
                     + TKeyListener.KEY_PRESSED, this,
                     "onCreateEditComoponentUD");
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 初始画面数据
        initPage();

        lock_column = table_d.getLockColumns();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        onChangeOrgCode();
        if (panel_1.isShowing()) {
            table_m.removeRowAll();
            if (!CheckData()) {
                return;
            }
            String sort = this.getValueString("PRINT_SORT");
            if ("0".equals(checkreason)) {
                // 0-全库盘点
                TParm parm = new TParm(TJDODBTool.getInstance().select(
                    INDSQL.getQtyCheck(org_code, sort)));
                if (CheckQty(parm)) {
                    table_m.setParmValue(parm);
                    resultParm = parm;
                }
            }
            else {
                // 1-抽样盘点
                int type_a_1 = 0;
                int type_a_2 = 0;
                int type_a_3 = 0;
                String order_code = "";
                String valid_date = "";
                String material_code = "";
                if ("A".equals(check_type)) {
                    // 盘点方式:A-公式抽样盘点
                    type_a_1 = TypeTool.getInt(this.getValue("TYPE_A_1"));
                    type_a_2 = TypeTool.getInt(this.getValue("TYPE_A_2"));
                    type_a_3 = TypeTool.getInt(this.getValue("TYPE_A_3"));
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheck(org_code, sort)));
                    if (CheckQty(parm)) {
                        TParm inparm = new TParm();
                        for (int i = 0; i < type_a_3; i++) {
                            inparm.setRowData(i, parm.getRow(type_a_1 - 1));
                            type_a_1 = type_a_1 + type_a_2;
                        }
                        table_m.setParmValue(inparm);
                        resultParm = inparm;
                    }
                }
                else if ("B".equals(check_type)) {
                    // 盘点方式:B-大类抽样
                    order_code = this.getValueString("TYPE_B").toUpperCase();
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheckTypeB(org_code, order_code, sort)));
                    if (CheckQty(parm)) {
                        table_m.setParmValue(parm);
                        resultParm = parm;
                    }
                }
                else if ("C".equals(check_type)) {
                    // 盘点方式:C-随机抽样(具体药品)
                    order_code = this.getValueString("ORDER_CODE");
                    valid_date = this.getValueString("VALID_DATE");
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheckTypeC(org_code, order_code,
                                                valid_date, sort)));
                    if (CheckQty(parm)) {
                        table_m.setParmValue(parm);
                        resultParm = parm;
                    }
                }
                else {
                    // 盘点方式:D-料位抽样盘点
                    material_code = this.getValueString("MATERIAL_LOC_CODE");
                    TParm parm = new TParm(TJDODBTool.getInstance().select(
                        INDSQL.getQtyCheckTypeD(org_code, material_code, sort)));
                    if (CheckQty(parm)) {
                        table_m.setParmValue(parm);
                        resultParm = parm;
                    }
                }
            }
            // 根据主表格是否存在数据判断第二页是否为有效
            if (table_m.getRowCount() > 0) {
                ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(
                    1, true);
            }
            else {
                ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(
                    1, false);
            }
        }
    }

    /**
     * 保存方法
     */
    public void onSave() {
        table_d.acceptText();
        // 判断冻结时间
        if ("".equals(this.getValueString("FROZEN_DATE_I"))) {
            this.messageBox("冻结时间不可为空");
            return;
        }
        // 循环判断盘点Grid中是否有未冻结的数据
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "STOCK_FLG"))) {
                this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                "盘点量输入前请先冻结");
                return;
            }
        }
        // 判断是否需要进行批号效期的查询
        String valid_flg = this.getValueString("VALID_FLG_I");
        // 保存前检核录入的数据
        if (!checkQueData("SAVE", valid_flg)) {
            return;
        }
        Timestamp date = StringTool.getTimestamp(new Date());
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("FROZEN_DATE", frozen_date);
        parm.setData("ACTUAL_CHECKQTY_USER", Operator.getID());
        parm.setData("ACTUAL_CHECKQTY_DATE", date);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        TParm parm_D = new TParm();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            parm_D.setData("ORDER_CODE", i,
                           table_d.getParmValue().getData("ORDER_CODE", i));
            parm_D.setData("BATCH_SEQ", i,
                           table_d.getParmValue().getData("BATCH_SEQ", i));
            double qty = table_d.getItemDouble(i, "ACTUAL_QTY_F") *
                table_d.getParmValue().getDouble("DOSAGE_QTY", i) +
                table_d.getItemDouble(i, "ACTUAL_QTY_M");
            parm_D.setData("QTY", i, qty);
        }
        parm.setData("PARM_D", parm_D.getData());
        // 保存盘点数据
        TParm result = TIOM_AppServer.executeAction(
            "action.ind.INDQtyCheckAction", "onUpdate", parm);
        // 保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("盘点保存失败");
            return;
        }
        this.messageBox("盘点保存成功\n请记得解除冻结");
    }

    /**
     * 清空方法
     */
    public void onClear() {
        // 清空画面内容Page1
        String clearStringPage1 =
            "ORG_CODE;PRINT_SORT;STOCK_PRICE_FLG;CHECKREASON_CODE;FROZEN_DATE;"
            + "UNFREEZE_DATE;CHECK_TYPE;TYPE_A_1;TYPE_A_2;TYPE_A_3;"
            + "TYPE_B;ORDER_CODE;ORDER_DESC;MATERIAL_LOC_CODE";
        clearValue(clearStringPage1);
        // 清空画面内容Page2
        String clearStringPage2 =
            "FROZEN_DATE_I;ORDER_CODE_I;ORDER_DESC_I;ACTIVE_FLG_I;UNFREEZE_DATE_I;"
            + "MATERIAL_LOC_CODE_I;VALID_FLG_I";
        clearValue(clearStringPage2);

        // 初始化页面状态及数据
        table_m.removeRowAll();
        table_m.setSelectionMode(0);
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        getComboBox("CHECK_TYPE").setSelectedIndex(0);
        getComboBox("CHECK_TYPE").setEnabled(false);
        setCheckTypeStatus(false, false, false, false);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(0, true);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(1, false);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setSelectedIndex(0);
        ( (TMenuItem) getComponent("save")).setEnabled(false);
        ( (TMenuItem) getComponent("lock")).setEnabled(false);
        ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        ( (TMenuItem) getComponent("query")).setEnabled(true);
        ( (TMenuItem) getComponent("clear")).setEnabled(true);
    }

    /**
     * 冻结方法
     */
    public void onLock() {
        // 检查部门是否批次过账
        if (!getOrgBatchFlg(org_code)) {
            return;
        }
        // 判断是否存在未解冻的药品
        TParm parm = table_m.getParmValue();
        boolean flg = false;
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
            if ("Y".equals(parm.getValue("STOCK_FLG", i))) {
                flg = true;
                break;
            }
        }
        if (flg) {
            this.messageBox("冻结前请先解除冻结");
            return;
        }
        // 冻结药品
        Timestamp date = StringTool.getTimestamp(new Date());
        String frozen_date = date.toString();
        frozen_date = frozen_date.substring(0, 4)
            + frozen_date.substring(5, 7)
            + frozen_date.substring(8, 10)
            + frozen_date.substring(11, 13)
            + frozen_date.substring(14, 16)
            + frozen_date.substring(17, 19);
        TParm inparm = new TParm();
        inparm.setData("ORG_CODE", org_code);
        inparm.setData("FROZEN_DATE", frozen_date);
        inparm.setData("ORDER", parm.getData());
        inparm.setData("CHECKREASON_CODE", checkreason);
        if (check_type == null) {
            check_type = "";
        }
        inparm.setData("CHECK_TYPE", check_type);
        inparm.setData("MODI_QTY", 0);
        inparm.setData("OPT_USER", Operator.getID());
        inparm.setData("OPT_DATE", date);
        inparm.setData("OPT_TERM", Operator.getIP());
        TParm result = TIOM_AppServer.executeAction(
            "action.ind.INDQtyCheckAction",
            "onLock", inparm);
        // 保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("冻结失败");
            return;
        }
        this.messageBox("冻结成功");
        onQuery();
    }

    /**
     * 解冻方法
     */
    public void onUnLock() {
        // 检核部门是否处于批次过账状态
        if (!getOrgBatchFlg(org_code)) {
            return;
        }
        // 判断冻结时间不可为空
        if ("".equals(getValueString("FROZEN_DATE_I")) ||
            this.getValue("FROZEN_DATE_I") == null) {
            this.messageBox("冻结时间不能为空");
            return;
        }
        // 判断是否可以解除冻结
        if (!"".equals(getValueString("UNFREEZE_DATE_I")) ||
            this.getValue("UNFREEZE_DATE_I") != null) {
            this.messageBox("该笔冻结资料已经解除冻结,不可重复解除冻结!");
            return;
        }
        // 判断盘点数据
        if (table_d.getRowCount() > 0) {
            Timestamp date = StringTool.getTimestamp(new Date());
            // 盘点解冻
            TParm inparm = new TParm();
            inparm.setData("ORG_CODE", org_code);
            inparm.setData("FROZEN_DATE", frozen_date);
            inparm.setData("VALID_FLG", this.getValueString("VALID_FLG_I"));
            inparm.setData("OPT_USER", Operator.getID());
            inparm.setData("OPT_DATE", date);
            inparm.setData("OPT_TERM", Operator.getIP());
            TParm parm_D = new TParm();
            for (int i = 0; i < table_d.getRowCount(); i++) {
                parm_D.setData("ORDER_CODE", i,
                               table_d.getItemString(i, "ORDER_CODE"));
                parm_D.setData("BATCH_SEQ", i,
                               table_d.getParmValue().getInt("BATCH_SEQ", i));
            }
            inparm.setData("PARM_D", parm_D.getData());
            //System.out.println("parm--" + inparm);
            TParm result = TIOM_AppServer.executeAction(
                "action.ind.INDQtyCheckAction",
                "onUnLock", inparm);
            // 保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("解除冻结失败");
                return;
            }
            this.messageBox("解除冻结成功");
            onClear();
        }
        else {
            this.messageBox("无解除冻结资料的数据");
            return;
        }
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("请选择盘点部门");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        Object result = openWindow("%ROOT%\\config\\ind\\INDQtyCheckPrt.x",
                                   parm);
    }

    /**
     * 部门改变事件
     */
    public void onChangeOrgCode() {
        table_m.removeRowAll();
        org_code = this.getValueString("ORG_CODE");
        if (!"".equals(org_code)) {
            // 查询冻结时间
            TParm parm = new TParm();
            parm.setData("ORG_CODE", org_code);
            parm = IndQtyCheckTool.getInstance().onQueryFrozenDate(parm);
            String frozen_date = "";
            for (int i = 0; i < parm.getCount("FROZEN_DATE"); i++) {
                frozen_date = parm.getValue("FROZEN_DATE", i);
                frozen_date = frozen_date.substring(0, 4) + "/"
                    + frozen_date.substring(4, 6) + "/"
                    + frozen_date.substring(6, 8) + " "
                    + frozen_date.substring(8, 10) + ":"
                    + frozen_date.substring(10, 12) + ":"
                    + frozen_date.substring(12, 14);
                parm.setData("F_DATE", i, frozen_date);
            }
            getComboBox("FROZEN_DATE").setParmValue(parm);
            getComboBox("FROZEN_DATE").setSelectedIndex(0);
            getComboBox("FROZEN_DATE_I").setParmValue(parm);
            getComboBox("FROZEN_DATE_I").setSelectedIndex(0);
            this.setValue("UNFREEZE_DATE", null);
            // 设定料位
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).
                setOrgCode(org_code);
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).onQuery();
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE_I")).
                setOrgCode(org_code);
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE_I")).
                onQuery();
        }
        else {
            getComboBox("FROZEN_DATE").removeAllItems();
            getComboBox("FROZEN_DATE_I").removeAllItems();
            this.setValue("UNFREEZE_DATE", null);
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE")).
                removeAllItems();
            ( (TComboINDMaterialloc) getComponent("MATERIAL_LOC_CODE_I")).
                removeAllItems();
        }
    }

    /**
     * 盘点原因改变事件
     */
    public void onChangeCheckReason() {
        checkreason = this.getValueString("CHECKREASON_CODE");
        if (!"1".equals(checkreason)) {
            getComboBox("CHECK_TYPE").setSelectedIndex(0);
            getComboBox("CHECK_TYPE").setEnabled(false);
            if (getComboBox("FROZEN_DATE").rowCount() > 0) {
                getComboBox("FROZEN_DATE").setSelectedIndex(0);
                getComboBox("FROZEN_DATE_I").setSelectedIndex(0);
            }
            this.setValue("UNFREEZE_DATE", null);
            setCheckTypeStatus(false, false, false, false);
        }
        else {
            getComboBox("CHECK_TYPE").setEnabled(true);
            getComboBox("FROZEN_DATE").setSelectedIndex(0);
            getComboBox("FROZEN_DATE_I").setSelectedIndex(0);
            this.setValue("UNFREEZE_DATE", null);
        }
        check_type = getComboBox("CHECK_TYPE").getSelectedID();
    }

    /**
     * 冻结时间改变事件
     */
    public void onChangeFrozenDate() {
        frozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (!"".equals(frozen_date)) {
            ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(1, true);
            //getComboBox("CHECK_TYPE").setSelectedIndex(0);
            //getComboBox("CHECK_TYPE").setEnabled(false);
            //getComboBox("CHECKREASON_CODE").setSelectedIndex(0);
            TParm parm = new TParm();
            parm.setData("ORG_CODE", org_code);
            parm.setData("FROZEN_DATE", frozen_date);
            //this.messageBox(getComboBox("FROZEN_DATE").getSelectedID());
            parm = IndQtyCheckTool.getInstance().onQuery(parm);
            this.setValue("FROZEN_DATE", frozen_date);
            this.setValue("UNFREEZE_DATE", parm.getData("UNFREEZE_DATE", 0));
            //this.messageBox(getComboBox("FROZEN_DATE").getSelectedID());
            // 查询冻结数据
            String sql = INDSQL.getQtyCheckHistoryInfo(org_code, frozen_date);
            parm = new TParm(TJDODBTool.getInstance().select(sql));
            table_m.setParmValue(parm);
        }
    }

    /**
     * 冻结时间改变事件
     */
    public void onChangeFrozenDateI() {
        frozen_date = getComboBox("FROZEN_DATE_I").getSelectedID();
        //this.messageBox(frozen_date);
        if (!"".equals(frozen_date)) {
            TParm parm = new TParm();
            parm.setData("ORG_CODE", org_code);
            parm.setData("FROZEN_DATE", frozen_date);
            //System.out.println("parm---"+parm);
            parm = IndQtyCheckTool.getInstance().onQuery(parm);
            if ("".equals(parm.getValue("UNFREEZE_DATE", 0))) {
                this.setValue("UNFREEZE_DATE_I","");// by liyh 20120725 如果没解冻时间 就赋值为空 
                table_d.setLockColumns(lock_column);
            }
            else {
                this.setValue("UNFREEZE_DATE_I",
                              parm.getData("UNFREEZE_DATE", 0));
                table_d.setLockColumns("all");
            }
            // 查询冻结数据
            frozenDateSelect(frozen_date);
        }
    }

    /**
     * 显示过期批号改变事件
     */
    public void onCheckActiveFlg() {
        onChangeFrozenDateI();
    }

    /**
     * 依效期批号盘点改变事件
     */
    public void onCheckValidFlg() {
        onChangeFrozenDateI();
    }

    /**
     * 盘点方式改变事件
     */
    public void onChangeCheckType() {
        check_type = getComboBox("CHECK_TYPE").getSelectedID();
        if ("A".equals(check_type)) {
            setCheckTypeStatus(true, false, false, false);
        }
        else if ("B".equals(check_type)) {
            setCheckTypeStatus(false, true, false, false);
        }
        else if ("C".equals(check_type)) {
            setCheckTypeStatus(false, false, true, false);
        }
        else if ("D".equals(check_type)) {
            setCheckTypeStatus(false, false, false, true);
        }
        else {
            setCheckTypeStatus(false, false, false, false);
        }
    }

    /**
     * 变更属性页
     */
    public void onChangeTTabbedPane() {
        if ( ( ( (TTabbedPane)this.getComponent("TabbedPane_1"))).
            getSelectedIndex() == 0) {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("query")).setEnabled(true);
            ( (TMenuItem) getComponent("lock")).setEnabled(false);
            ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("query")).setEnabled(false);
            ( (TMenuItem) getComponent("lock")).setEnabled(false);
            if (unfrozen_flg) {
                ( (TMenuItem) getComponent("unlock")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("unlock")).setEnabled(false);
            }
        }
    }

    /**
     * 主项表格(TABLE_M)单击事件
     */
    public void onTableMClicked() {
        int row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            table_d.setSelectionMode(0);
            // 取得SYS_FEE信息，放置在状态栏上
            String order_code = table_m.getParmValue().getValue("ORDER_CODE",
                table_m.getSelectedRow());
            if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
            }
            else {
                callFunction("UI|setSysStatus", "");
            }
        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        int row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            table_m.setSelectionMode(0);
            // 取得SYS_FEE信息，放置在状态栏上
            String order_code = table_d.getParmValue().getValue("ORDER_CODE",
                table_d.getSelectedRow());
            if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
            }
            else {
                callFunction("UI|setSysStatus", "");
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
        // Table的列
        int column = node.getColumn();
        int row = node.getRow();
        int column_stock = 9;
        int column_dosage = 11;
        if (qty_check_flg) {
            column_stock = 9;
            column_dosage = 11;
        }
        else {
            column_stock = 6;
            column_dosage = 8;
        }
        if (column == column_stock) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("库存单位数量不能小于0");
                return true;
            }
            getStockModiQty(row, qty);
        }
        if (column == column_dosage) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("发药单位数量不能小于0");
                return true;
            }
            getDosageModiQty(row, qty);
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
     * 接受返回值方法
     *
     * @param tag
     * @param obj
     */
    public void popReturn_I(String tag, Object obj) {
        TParm parm = (TParm) obj;
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            getTextField("ORDER_CODE_I").setValue(order_code);
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(order_desc))
            getTextField("ORDER_DESC_I").setValue(order_desc);
    }

    /**
     * 定位药品功能
     */
    public void onOrientationAction() {
        if ("".equals(this.getValueString("ORDER_CODE_I"))) {
            this.messageBox("请输入定位药品");
            return;
        }
        boolean flg = false;
        TParm parm = table_d.getParmValue();
        String order_code = this.getValueString("ORDER_CODE_I");
        int row = table_d.getSelectedRow();
        for (int i = row + 1; i < parm.getCount("ORDER_CODE"); i++) {
            if (order_code.equals(parm.getValue("ORDER_CODE", i))) {
                row = i;
                flg = true;
                break;
            }
        }
        if (!flg) {
            this.messageBox("未找到定位药品");
        }
        else {
            table_d.setSelectedRow(row);
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(),
                                  " AND B.ORG_TYPE IN ('A', 'B', 'C') ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }

        // 盘点冻结库存
        if (!this.getPopedem("FROZEN")) {
            frozen_flg = false;
        }

        // 解除冻结库存
        if (!this.getPopedem("UNFROZEN")) {
            unfrozen_flg = false;
        }

        // 盘点量输入
        if (!this.getPopedem("QTY_CHECK")) {
            qty_check_flg = false;
            getTable("TABLE_D").setHeader(header);
            getTable("TABLE_D").setLockColumns(lockColumns);
            getTable("TABLE_D").setColumnHorizontalAlignmentData(
                columnHorizontal);
            getTable("TABLE_D").setParmMap(parmMap);
        }

        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        panel_1 = getPanel("TPanel_1");
        panel_2 = getPanel("TPanel_2");
        ( (TMenuItem) getComponent("save")).setEnabled(false);
        ( (TMenuItem) getComponent("lock")).setEnabled(false);
        ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        //resultParm = new TParm();
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(0, true);
        ( (TTabbedPane)this.getComponent("TabbedPane_1")).setEnabledAt(1, false);

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn");


        // 设置弹出菜单
        getTextField("ORDER_CODE_I").setPopupMenuParameter("UD",
            getConfigParm().newConfig(
                "%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE_I").addEventListener(TPopupMenuEvent.
            RETURN_VALUE, this, "popReturn_I");

    }

    /**
     * 数据检查
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("部门代码不可为空白");
            return false;
        }
        if ("".equals(this.getValueString("CHECKREASON_CODE"))) {
            this.messageBox("盘点原因不可为空白");
            return false;
        }
        if ("1".equals(this.getValueString("CHECKREASON_CODE"))
            && "".equals(this.getValueString("CHECK_TYPE"))) {
            this.messageBox("盘点方式不可为空白");
            return false;
        }
        if ("1".equals(this.getValueString("CHECKREASON_CODE"))) {
            if ("B".equals(this.getValueString("CHECK_TYPE"))
                && "".equals(this.getValueString("TYPE_B"))) {
                this.messageBox("开头药品代码不可为空白");
                return false;
            }
            if ("C".equals(this.getValueString("CHECK_TYPE"))
                && "".equals(this.getValueString("ORDER_CODE"))) {
                this.messageBox("药品代码不可为空白");
                return false;
            }
            if ("D".equals(this.getValueString("CHECK_TYPE"))
                && "".equals(this.getValueString("MATERIAL_LOC_CODE"))) {
                this.messageBox("料位代码不可为空白");
                return false;
            }
        }
        return true;
    }

    /**
     * 盘点查询检查
     * @param parm TParm
     */
    public boolean CheckQty(TParm parm) {
        //System.out.println(parm);
        if (parm != null && parm.getCount("ORDER_CODE") > 0) {
            if (frozen_flg) {
                ( (TMenuItem) getComponent("lock")).setEnabled(true);
            }
            else {
                ( (TMenuItem) getComponent("lock")).setEnabled(false);
            }
            ( (TMenuItem) getComponent("unlock")).setEnabled(false);
            return true;
        }
        this.messageBox("没有盘点数据");
        ( (TMenuItem) getComponent("lock")).setEnabled(false);
        ( (TMenuItem) getComponent("unlock")).setEnabled(false);
        return false;
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
     * 根据选择的冻结时间查询冻结数据
     */
    private void frozenDateSelect(String frozen_date) {
        //药品代码
        String order_code = "";
        //B:药品大类的代码
        if ("B".equals(check_type)) {
            order_code = this.getValueString("TYPE_B").toUpperCase();
        }
        else if ("C".equals(check_type)) {
            order_code = this.getValueString("ORDER_CODE");
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("CHECKREASON_CODE", this.getValueString("CHECKREASON_CODE"));
        parm.setData("CHECK_TYPE", check_type == null ? "" : check_type);
        parm.setData("ORDER_CODE", order_code);
        parm.setData("VALID_DATE",
                     this.getValueString("VALID_DATE") == null ? "" :
                     this.getValueString("VALID_DATE"));
        parm.setData("MATERIAL_LOC_CODE",
                     this.getValueString("MATERIAL_LOC_CODE"));
        parm.setData("SORT", this.getValueString("PRINT_SORT"));
        parm.setData("ACTIVE_FLG", this.getValueString("ACTIVE_FLG_I"));
        parm.setData("VALID_FLG", this.getValueString("VALID_FLG_I"));
        parm.setData("FROZEN_DATE", frozen_date);
        TParm result = IndQtyCheckTool.getInstance().onQueryQtyCheck(parm);
        table_d.setParmValue(result);
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
     * 得到TNumberTextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TNumberTextField getNumberTextField(String tagName) {
        return (TNumberTextField) getComponent(tagName);
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
     * 设定盘点的四种方式的状态
     */
    private void setCheckTypeStatus(boolean type1, boolean type2,
                                    boolean type3, boolean type4) {
        String clearString = "TYPE_A_1;TYPE_A_2;TYPE_A_3;TYPE_B;ORDER_CODE;"
            + "ORDER_DESC;MATERIAL_LOC_CODE";
        clearValue(clearString);
        getNumberTextField("TYPE_A_1").setEnabled(type1);
        getNumberTextField("TYPE_A_2").setEnabled(type1);
        getNumberTextField("TYPE_A_3").setEnabled(type1);
        getTextField("TYPE_B").setEnabled(type2);
        getTextField("ORDER_CODE").setEnabled(type3);
        getComboBox("VALID_DATE").setEnabled(type3);
        getComboBox("MATERIAL_LOC_CODE").setEnabled(type4);
    }

    /**
     * 数据检核
     * @param type String
     * @param flg String
     * @return boolean
     */
    private boolean checkQueData(String type, String flg) {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // 判断药品转换率
            if (table_d.getParmValue().getData("DOSAGE_QTY", i) == null) {
                this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                "转换率错误");
                return false;
            }
            // 判断原库存量
            if (table_d.getParmValue().getData("STOCK_QTY_F", i) == null ||
                table_d.getParmValue().getData("STOCK_QTY_M", i) == null) {
                this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                "原库存量为空");
                return false;
            }
            // 判断批次序号--如果不依批号效期显示,batch_seq就为空,且保存和解除冻结时都不使用;否则就不可为空
            if ("N".equals(flg)) {
                if (table_d.getItemData(i, "BATCH_NO") == null ||
                    table_d.getItemData(i, "VALID_DATE") == null) {
                    this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                    "批号或效期为空");
                    return false;
                }
            }
            // 如果是保存,就要去检核实际录入的数量
            if ("SAVE".equals(type)) {
                // 判断实际录入库存单位数量
                if (table_d.getItemData(i, "ACTUAL_QTY_F") == null) {
                    this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                    "实际录入库存单位数量为空");
                    return false;
                }
                // 判断实际录入发药单位数量
                if (table_d.getItemData(i, "ACTUAL_QTY_M") == null) {
                    this.messageBox(table_d.getItemString(i, "ORDER_DESC") +
                                    "实际录入发药单位数量为空");
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 计算今日盘差
     *
     * @return
     */
    private void getStockModiQty(int row, double qty) {
        table_d.acceptText();
        double rate = table_d.getParmValue().getDouble("DOSAGE_QTY", row);
        //this.messageBox(rate+"");
        if (qty_check_flg) {
            double stock_qty = table_d.getItemDouble(row,
                "STOCK_QTY_F");
            double actual_qty_m = table_d.getItemDouble(row,
                "ACTUAL_QTY_M");
            table_d.setItem(row, "MODI_QTY",
                            qty * rate + actual_qty_m - stock_qty);
        }
        else {
            double stock_qty = table_d.getParmValue().getDouble(
                "STOCK_QTY_F", row);
            double actual_qty_m = table_d.getItemDouble(row,
                "ACTUAL_QTY_M");
            table_d.getParmValue().setData("MODI_QTY", row,
                                           qty * rate + actual_qty_m -
                                           stock_qty);
        }
    }

    /**
     * 计算今日盘差
     *
     * @return
     */
    private void getDosageModiQty(int row, double qty) {
        table_d.acceptText();
        double rate = table_d.getParmValue().getDouble("DOSAGE_QTY", row);
        //this.messageBox(rate+"");
        if (qty_check_flg) {
            double stock_qty = table_d.getItemDouble(row,
                "STOCK_QTY_F");
            double actual_qty_f = table_d.getItemDouble(row,
                "ACTUAL_QTY_F");
            table_d.setItem(row, "MODI_QTY",
                            qty + actual_qty_f * rate - stock_qty);
        }
        else {
            double stock_qty = table_d.getParmValue().getDouble(
                "STOCK_QTY_F", row);
            double actual_qty_f = table_d.getItemDouble(row,
                "ACTUAL_QTY_F");
            table_d.getParmValue().setData("MODI_QTY", row,
                                           qty + actual_qty_f * rate -
                                           stock_qty);
        }
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
