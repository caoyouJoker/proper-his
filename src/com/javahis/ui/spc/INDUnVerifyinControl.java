package com.javahis.ui.spc;

import jdo.spc.INDTool;
import jdo.spc.IndPurorderDTool;
import jdo.spc.IndPurorderMTool;
import jdo.spc.IndVerifyinDTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TTextFormat;

/**
 * <p>
 * Title: 引用订单Control
 * </p>
 *
 * <p>
 * Description: 引用订单Control
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
 * @author zhangy 2009.05.14
 * @version 1.0
 */

public class INDUnVerifyinControl
    extends TControl {

    // 返回数据集合
    private TParm resultParm;

    // 订购部门
    private String org_code;

    public INDUnVerifyinControl() {
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
        TParm inParm = new TParm();
        if ("".equals(getValueString("SUP_CODE"))
            && "".equals(getComboBox("PURORDER_NO").getSelectedID())) {
            this.messageBox("请选择供应厂商和订购单");
            return;
        }
        if (!"".equals(getValueString("SUP_CODE"))
            && "".equals(getComboBox("PURORDER_NO").getSelectedID())) {
            this.messageBox("请选择订购单");
            return;
        }
        // 供货厂商
        if (!"".equals(getValueString("SUP_CODE"))) {
            inParm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        // 订购单号
        if (!"".equals(getComboBox("PURORDER_NO").getSelectedID())) {
            inParm.setData("PURORDER_NO", getComboBox("PURORDER_NO")
                           .getSelectedID());
        }
        // 药品代码
        if (!"".equals(getValueString("ORDER_CODE"))) {
            inParm.setData("ORDER_CODE", getValueString("ORDER_CODE"));
        }
        TParm result = new TParm();
        result = IndPurorderMTool.getInstance().onQueryUnDone(inParm);
        if (result.getCount() == 0) {
            this.messageBox("查无资料");
            return;
        }
        for (int i = result.getCount("ORDER_CODE") - 1; i >= 0; i--) {
            TParm inparm = new TParm();
            inparm.setData("PURORDER_NO", result.getValue("PURORDER_NO", i));
            inparm.setData("PURSEQ_NO", result.getInt("SEQ_NO", i));
            TParm inresult = IndVerifyinDTool.getInstance().onQuery(inparm);
            if (inresult.getCount() > 0) {
                // 判断UPDATE_FLG状态
                String update_flg = inresult.getValue("UPDATE_FLG", i);
                if ("3".equals(update_flg) || "2".equals(update_flg)) {
                    result.removeRow(i);
                    result.setCount(result.getCount() - 1);
                }
            }
        }
        // 计算零售价,零售金额,进销差价
        for (int i = 0; i < result.getCount("ORDER_CODE"); i++) {
            String order_code = result.getValue("ORDER_CODE", i);
            double dosage_qty = INDTool.getInstance().getPhaTransUnitQty(
                order_code, "2");
            double stock_qty = INDTool.getInstance().getPhaTransUnitQty(
                order_code, "1");
            double retail_price = result.getDouble("RETAIL_PRICE", i)
                * dosage_qty * stock_qty;
            result.setData("RETAIL_PRICE", i, retail_price);

        }
        if (result.getCount() == 0) {
            this.messageBox("查无资料");
            return;
        }
        getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
        TTable table = getTable("TABLE");
        table.setParmValue(result);
        resultParm = result;
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr =
            "PURORDER_NO;ORDER_CODE;ORDER_DESC;SELECT_ALL";
        this.clearValue(clearStr);
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
        TParm result = resultParm;
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
        setReturnValue(result);
        //System.out.println("查询数据: " + result);
        this.closeWindow();
    }

    /**
     * 供应厂商改变事件
     */
    public void onSupCodeChange() {
        String sup_code = getValueString("SUP_CODE");
        TParm parm = IndPurorderDTool.getInstance().onQueryUnDonePurorderNo(
            org_code, sup_code);
        getComboBox("PURORDER_NO").setParmValue(parm);
        getComboBox("PURORDER_NO").setSelectedIndex( -1);
    }

    /**
     * 订购单改变事件
     */
    public void onPurOrderChange() {
        if ("".equals(getValueString("SUP_CODE"))) {
            TParm parm = new TParm();
            parm.setData("PURORDER_NO", getValueString("PURORDER_NO"));
            TParm result = IndPurorderMTool.getInstance().onQuery(parm);
            getTextFormat("SUP_CODE").setValue(result.getValue("SUP_CODE", 0));
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
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setItem(i, "SELECT_FLG", getValueString("SELECT_ALL"));
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
     * 初始画面数据
     */
    private void initPage() {
        // 初始化订购部门ORG_CODE
        Object obj = getParameter();
        if (obj == null)
            return;
        if (! (obj instanceof TParm))
            return;
        TParm parmOrg = (TParm) obj;
        org_code = parmOrg.getValue("ORG_CODE");
        // 初始化订购单ComboBox
        String sup_code = "";
        TParm parm = IndPurorderDTool.getInstance().onQueryUnDonePurorderNo(
            org_code, sup_code);
        getComboBox("PURORDER_NO").setParmValue(parm);

        TParm parmIn = new TParm();
        parmIn.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"), parmIn);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        resultParm = new TParm();
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
