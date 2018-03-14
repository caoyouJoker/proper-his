package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TPopupMenuEvent;
import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TRadioButton;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TNumberTextField;
import jdo.ind.IndVerifyinDTool;
import java.util.Map;
import java.util.HashMap;
import jdo.ind.IndQtyCheckTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 盘盈亏统计表
 * </p>
 *
 * <p>
 * Description: 盘盈亏统计表
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.09.22
 * @version 1.0
 */
public class INDQtyCheckQueryControl
    extends TControl {

    TTable table_m;

    TTable table_d;


    public INDQtyCheckQueryControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        table_m = this.getTable("TABLE_M");
        table_d = this.getTable("TABLE_D");

        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");

        TParm parm = new TParm();
        parm.setData("CAT1_TYPE", "PHA");
        // 设置弹出菜单
        getTextField("ORDER_CODE").setPopupMenuParameter("UD",
            getConfigParm().newConfig("%ROOT%\\config\\sys\\SYSFeePopup.x"),
            parm);
        // 定义接受返回值方法
        getTextField("ORDER_CODE").addEventListener(
            TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        if ("".equals(this.getValueString("ORG_CODE"))) {
            this.messageBox("统计部门不能为空");
            return;
        }
        if (this.getValueDouble("OWN_PRICE_A") >
            this.getValueDouble("OWN_PRICE_B")) {
            this.messageBox("零售价范围不正确");
            return;
        }
        if (this.getValueDouble("CHECK_QTY_A") >
            this.getValueDouble("CHECK_QTY_B")) {
            this.messageBox("盈亏数范围不正确");
            return;
        }
        if (this.getValueDouble("CHECK_AMT_A") >
            this.getValueDouble("CHECK_AMT_B")) {
            this.messageBox("盈亏额范围不正确");
            return;
        }

        table_m.removeRowAll();
        table_d.removeRowAll();
        this.setValue("SUM_COUNT", 0);
        this.setValue("SUM_CHECK_QTY", 0);
        this.setValue("SUM_CHECK_AMT", 0);

        TParm parm = new TParm();
        parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        parm.setData("START_DATE", this.getValue("START_DATE"));
        parm.setData("END_DATE", this.getValue("END_DATE"));
        if (!"".equals(this.getValueString("TYPE_CODE"))) {
            parm.setData("TYPE_CODE", this.getValueString("TYPE_CODE"));
        }
        if (!"".equals(this.getValueString("ORDER_CODE"))) {
            parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
        }
        if (!"".equals(this.getValueString("BATCH_NO"))) {
            parm.setData("BATCH_NO", this.getValueString("BATCH_NO"));
        }
        if (this.getValueDouble("OWN_PRICE_A") > 0) {
            parm.setData("OWN_PRICE_A", this.getValue("OWN_PRICE_A"));
            parm.setData("OWN_PRICE_B", this.getValue("OWN_PRICE_B"));
        }
        if (this.getValueDouble("CHECK_QTY_A") > 0) {
            parm.setData("CHECK_QTY_A", this.getValue("CHECK_QTY_A"));
            parm.setData("CHECK_QTY_B", this.getValue("CHECK_QTY_B"));
        }
        if (this.getValueDouble("CHECK_AMT_A") > 0) {
            parm.setData("CHECK_AMT_A", this.getValue("CHECK_AMT_A"));
            parm.setData("CHECK_AMT_B", this.getValue("CHECK_AMT_B"));
        }

        TParm resultM = new TParm();
        TParm resultD = new TParm();
        resultM = IndQtyCheckTool.getInstance().onQueryQtyCheckMaster(
            parm);
        resultD = IndQtyCheckTool.getInstance().onQueryQtyCheckDetail(parm);

        if (resultM == null || resultM.getCount() <= 0) {
            this.messageBox("没有查询数据");
            return;
        }

        double sum_check_qty = 0;
        double sum_check_amt = 0;

        String sql = "SELECT ID, CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_PHATYPE' ORDER BY ID";
        TParm phaType = new TParm(TJDODBTool.getInstance().select(sql));
        Map map = new HashMap();
        for (int i = 0; i < phaType.getCount("ID"); i++) {
            map.put(phaType.getValue("ID", i), phaType.getValue("CHN_DESC", i));
        }

        for (int i = 0; i < resultM.getCount(); i++) {
            resultM.setData("TYPE_CODE", i,
                            map.get(resultM.getData("TYPE_CODE", i)));
            sum_check_qty += resultM.getDouble("CHECK_QTY", i);
            sum_check_amt += resultM.getDouble("OWN_AMT", i);
        }
        for (int i = 0; i < resultD.getCount(); i++) {
            resultD.setData("TYPE_CODE", i,
                            map.get(resultD.getData("TYPE_CODE", i)));
        }
        table_m.setParmValue(resultM);
        table_d.setParmValue(resultD);
        this.setValue("SUM_COUNT", resultM.getCount());
        this.setValue("SUM_CHECK_QTY", StringTool.round(sum_check_qty, 3));
        this.setValue("SUM_CHECK_AMT", StringTool.round(sum_check_amt, 2));
    }

    /**
     * 清空方法
     */
    public void onClear() {
        String clearStr =
            "ORG_CODE;ORDER_CODE;ORDER_DESC;TYPE_CODE;BATCH_NO;"
            + "OWN_PRICE_A;OWN_PRICE_B;CHECK_QTY_A;CHECK_QTY_B;CHECK_AMT_A;"
            + "CHECK_AMT_B;SUM_COUNT;SUM_CHECK_QTY;SUM_CHECK_AMT";
        this.clearValue(clearStr);

        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        getRadioButton("RadioButton3").setSelected(true);
        table_m.removeRowAll();
        table_m.setVisible(true);
        table_d.removeRowAll();
        table_d.setVisible(false);
    }

    /**
     * 打印方法
     */
    public void onPrint() {

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
     * 查询类别选择事件
     */
    public void onSelectTypeAction() {
        if (getRadioButton("RadioButton3").isSelected()) {
            table_m.setVisible(true);
            table_d.setVisible(false);
        }
        else {
            table_d.setVisible(true);
            table_m.setVisible(false);
        }
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
