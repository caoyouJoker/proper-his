package com.javahis.ui.spc;

import jdo.ind.INDSQL;
import jdo.ind.IndPurPlanMTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 采建表Control
 * </p>
 *
 * <p>
 * Description: 采建表Control
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
 * @author zhangy 2009.05.13
 * @version 1.0
 */

public class INDPurAdviceControl
    extends TControl {

    private TParm parm;
    private String org_code;
    private String sup_code;

    public INDPurAdviceControl() {
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
            org_code = parm.getValue("ORG_CODE");
            sup_code = parm.getValue("SUP_CODE");
        }
        // 初始画面数据
        initPage();
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if (!CheckData()) {
            return;
        }
        else {
            String sup_code = this.getValueString("SUP_CODE");
            String plan_no = this.getValueString("PLAN_NO");
            String sql = INDSQL.getIndPlanAdvice(org_code, sup_code, plan_no);
            TParm result = new TParm(TJDODBTool.getInstance().select(sql));
            if (result.getCount() < 0) {
                this.messageBox("无查询数据");
                return;
            }
            else {
                getTable("TABLE").setParmValue(result);
            }
        }

    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearString = "PLAN_NO;SELECT_ALL";
        this.clearValue(clearString);
        getTextFormat("SUP_CODE").setText("");
        getTable("TABLE").removeRowAll();
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
        TParm result = table.getParmValue();
        for (int i = table.getRowCount() - 1; i >= 0; i--) {
            if ("N".equals(table.getItemString(i, "SELECT_FLG"))) {
                result.removeRow(i);
                result.setCount(result.getCount() - 1);
            }
        }
        if (result == null) {
            this.messageBox("没有传回数据");
            return;
        }
        result.addData("PLAN_NO", this.getValue("PLAN_NO"));
        setReturnValue(result);
        //System.out.println("查询数据: " + result);
        this.closeWindow();
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
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
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
        int column = node.getColumn();
        int row = node.getRow();
        if (column == 4) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("订购数量不能小于或等于0");
                return true;
            }
            else {
                double price = TypeTool.getDouble(node.getTable().getValueAt(
                    row, 6));
                double atm = price * qty;
                node.getTable().setItem(row, 7, StringTool.round(atm, 2));
            }
        }
        if (column == 6) {
            double price = TypeTool.getDouble(node.getValue());
            if (price <= 0) {
                this.messageBox("定购价格不能小于或等于0");
                return true;
            }
            else {
                double qty = TypeTool.getDouble(node.getTable().getValueAt(row,
                    4));
                double atm = price * qty;
                node.getTable().setItem(row, 7, StringTool.round(atm, 2));
            }
        }
        return false;
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        this.setValue("ORG_CODE", org_code);
        this.setValue("SUP_CODE", sup_code);
        parm.setData("CREATE_FLG", "N");
        parm.setData("PLANEND_USER", "Y");
        TParm result = IndPurPlanMTool.getInstance().onQuery(parm);
        getComboBox("PLAN_NO").setParmValue(result);
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckData() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("订购部门不能为空");
            return false;
        }
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("供应厂商不能为空");
            return false;
        }
        if ("".equals(getValueString("PLAN_NO"))) {
            this.messageBox("计划单号不能为空");
            return false;
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
     * 得到TextFormat对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextFormat getTextFormat(String tagName) {
        return (TTextFormat) getComponent(tagName);
    }

}
