package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.ui.TTextFormat;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TNumberTextField;
import jdo.inv.InvSuptitemDetailTool;
import com.dongyang.util.TypeTool;
import jdo.util.Manager;

/**
 * <p>Title: 灭菌记账明细</p>
 *
 * <p>Description: 灭菌记账明细</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 *
 * @author zhangy 2010.3.27
 * @version 1.0
 */
public class INVSuptitemDetailControl
    extends TControl {

    private TTable table;

    public INVSuptitemDetailControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        //项目主表
        table = (TTable) getComponent("TABLE");
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        this.setValue("CASHIER_CODE", Operator.getID());
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (!checkData()) {
            return;
        }
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("SUP_DETAIL_SEQ", this.getValueInt("SUP_DETAIL_SEQ"));
        parm.setData("SUP_DATE", SystemTool.getInstance().getDate());
        parm.setData("USE_DEPT", this.getValueString("USE_DEPT"));
        parm.setData("SUPITEM_CODE", this.getValueString("SUPITEM_CODE"));
        parm.setData("QTY", this.getValueDouble("QTY"));
        parm.setData("COST_PRICE", this.getValueDouble("COST_PRICE"));
        parm.setData("ADD_PRICE", this.getValueDouble("ADD_PRICE"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("CASHIER_CODE", this.getValueString("CASHIER_CODE"));
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
        parm.setData("OPT_TERM", Operator.getIP());
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        parm.setData("SUPITEM_DESC", format.getComboValue("SUPITEM_DESC"));

        if ( ( (TTextField) getComponent("SUP_DETAIL_NO")).isEnabled()) {
            //新增方法
            parm.setData("SUP_DETAIL_NO",
                         SystemTool.getInstance().getNo("ALL", "INV", "SUPDETAIL", "No"));
            result = InvSuptitemDetailTool.getInstance().onInsert(parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            table.setSelectedRow(table.addRow(parm));
            onClickedTable();
            this.onPrint();
            this.messageBox("P0001");
        }
        else {
            //更新方法
            parm.setData("SUP_DETAIL_NO", this.getValueString("SUP_DETAIL_NO"));
            result = InvSuptitemDetailTool.getInstance().onUpdate(parm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            onQuery();
            this.messageBox("P0001");
        }
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue().getRow(row);
        TParm result = InvSuptitemDetailTool.getInstance().onDelete(parm);
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("删除失败");
            return;
        }
        this.messageBox("删除成功");
        table.removeRow(row);
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;USE_DEPT;SUPITEM_CODE;"
            + "QTY;COST_PRICE;ADD_PRICE;DESCRIPTION");
        ( (TTextField) getComponent("SUP_DETAIL_NO")).setEnabled(true);
        ( (TNumberTextField) getComponent("SUP_DETAIL_SEQ")).setEnabled(true);
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm parm = new TParm();
        // 查询时间
        if (!"".equals(this.getValueString("START_DATE")) &&
            !"".equals(this.getValueString("END_DATE"))) {
            parm.setData("START_DATE", this.getValue("START_DATE"));
            parm.setData("END_DATE", this.getValue("END_DATE"));
        }
        if (!"".equals(this.getValueString("SUP_DETAIL_NO"))) {
            parm.setData("SUP_DETAIL_NO", this.getValueString("SUP_DETAIL_NO"));
        }
        parm.setData("SUP_DETAIL_SEQ", this.getValueInt("SUP_DETAIL_SEQ"));
        if (!"".equals(this.getValueString("USER_DEPT"))) {
            parm.setData("USE_DEPT", this.getValueString("USE_DEPT"));
        }
        if (!"".equals(this.getValueString("SUPITEM_CODE"))) {
            parm.setData("SUPITEM_CODE", this.getValueString("SUPITEM_CODE"));
        }
        // 数据查询
        TParm result = InvSuptitemDetailTool.getInstance().onQuery(parm);
        if (result == null || result.getCount() <= 0) {
            this.messageBox("没有查询数据");
            table.removeRowAll();
            return;
        }
        table.setParmValue(result);
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        int row = table.getSelectedRow();
        if (row < 0) {
            this.messageBox("没有打印数据");
            return;
        }
        // 打印数据
        TParm date = new TParm();
        // 表头数据
        date.setData("TITLE", "TEXT", Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "供应室灭菌记账单");
        date.setData("SUP_DETAIL_NO", "TEXT",
                     "记账单号:" + getValueString("SUP_DETAIL_NO"));
        date.setData("USE_DEPT", "TEXT",
                     "记账科室:" + getTextFormat("USE_DEPT").getText());
        date.setData("SUP_DATE", "TEXT", "记账时间:" +
                     StringTool.getString(table.getItemTimestamp(row, "SUP_DATE"),
                                          "yyyy/MM/dd hh:mm:ss"));
        date.setData("CASHIER_CODE", "TEXT",
                     "收费日期:" + getTextFormat("CASHIER_CODE").getText());
        TParm parm = new TParm();
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        parm.addData("SUPITEM_DESC", format.getComboValue("SUPITEM_DESC").toString());
        parm.addData("QTY", this.getValueDouble("QTY"));
        parm.addData("COST_PRICE", this.getValueDouble("COST_PRICE"));
        parm.addData("ADD_PRICE", this.getValueDouble("ADD_PRICE"));
        parm.addData("USER_NAME", "");
        parm.addData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setCount(1);
        parm.addData("SYSTEM", "COLUMNS", "SUPITEM_DESC");
        parm.addData("SYSTEM", "COLUMNS", "QTY");
        parm.addData("SYSTEM", "COLUMNS", "COST_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "ADD_PRICE");
        parm.addData("SYSTEM", "COLUMNS", "USER_NAME");
        parm.addData("SYSTEM", "COLUMNS", "DESCRIPTION");
        date.setData("TABLE", parm.getData());
        date.setData("OPT_USER", "TEXT", "操作人:" + Operator.getName());
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVSuptitemDetall.jhw",
                             date);
    }

    /**
     * 清空
     */
    public void onClear() {
        this.clearValue("SUP_DETAIL_NO;SUP_DETAIL_SEQ;USE_DEPT;SUPITEM_CODE;"
            + "QTY;COST_PRICE;ADD_PRICE;DESCRIPTION");
        ( (TTextField) getComponent("SUP_DETAIL_NO")).setEnabled(true);
        ( (TNumberTextField) getComponent("SUP_DETAIL_SEQ")).setEnabled(true);
        table.removeRowAll();
    }

    /**
     * 表格单击事件
     */
    public void onClickedTable() {
        int row = table.getSelectedRow();
        TParm parm = table.getParmValue().getRow(row);
        this.setValueForParm("SUP_DETAIL_NO;SUP_DETAIL_SEQ;USE_DEPT;"
                             + "SUPITEM_CODE;QTY;COST_PRICE;ADD_PRICE;"
                             + "DESCRIPTION", parm);
        ( (TTextField) getComponent("SUP_DETAIL_NO")).setEnabled(false);
        ( (TNumberTextField) getComponent("SUP_DETAIL_SEQ")).setEnabled(false);
    }

    /**
     * 记账项目变更事件
     */
    public void onSupitemCodeSelected() {
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        double qty = this.getValueDouble("QTY");
        if (qty <= 0) {
            qty = 1;
        }
        this.setValue("COST_PRICE",
                      TypeTool.getDouble(format.getComboValue("COST_PRICE")) * qty);
        this.setValue("ADD_PRICE",
                      TypeTool.getDouble(format.getComboValue("ADD_PRICE")) * qty);
    }

    /**
     * 记账数量变更事件
     */
    public void onChangeQty() {
        double qty = this.getValueDouble("QTY");
        TTextFormat format = (TTextFormat)this.getComponent("SUPITEM_CODE");
        this.setValue("COST_PRICE",
                      Double.parseDouble(format.getComboValue("COST_PRICE").
                                         toString()) * qty);
        this.setValue("ADD_PRICE",
                      Double.parseDouble(format.getComboValue("ADD_PRICE").
                                         toString()) * qty);
    }

    /**
     * 数据检核
     * @return boolean
     */
    private boolean checkData() {
        if ("".equals(this.getValueString("USE_DEPT"))) {
            this.messageBox("记账科室不能为空");
            return false;
        }
        if ("".equals(this.getValueString("SUPITEM_CODE"))) {
            this.messageBox("记账项目不能为空");
            return false;
        }
        if (this.getValueDouble("QTY") <= 0) {
            this.messageBox("记账数量不能小于等于0");
            return false;
        }
        return true;
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
