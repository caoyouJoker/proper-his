package com.javahis.ui.spc;

/**
 * <p>
 * Title: 采购计划生成订购单Control
 * </p>
 *
 * <p>
 * Description: 采购计划生成订购单Control
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
 * @author zhangy 2009.07.20
 * @version 1.0
 */
import java.sql.Timestamp;

import jdo.ind.INDTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;

public class INDOrderFormControl
    extends TControl {
    private TParm parm;
    private String org_code;
    private String plan_month;
    private String plan_no;

    private TTable table;

    public INDOrderFormControl() {
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
            plan_month = parm.getValue("PLAN_MONTH");
            plan_no = parm.getValue("PLAN_NO");
        }
        // 初始画面数据
        initPage();
    }

    /**
     * 生成方法
     */
    public void onCreate() {
        if (!CheckData()) {
            return;
        }
        TParm result = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        parm.setData("PURORDER_DATE", date);
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_DATE", date);
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("REGION_CODE",Operator.getRegion());
        result.setData("PUR_M", parm.getData());
        TParm parmD = new TParm();
        int count = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                parmD.setRowData(count, table.getParmValue().getRow(i));
                count++;
            }
        }
        result.setData("PUR_D", parmD.getData());
        result = TIOM_AppServer.executeAction("action.ind.INDPurPlanAction",
                                              "onCreatePurOrder", result);
        // 保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
    }

    /**
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "Y");
                this.setValue("SUM_MONEY", getSumMoney());
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "SELECT_FLG", "N");
                this.setValue("SUM_MONEY", 0);
            }
        }
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table.acceptText();
        // 获得选中的列
        int column = table.getSelectedColumn();
        int row = table.getSelectedRow();
        if (column == 0) {
            double atm = table.getItemDouble(row, "ATM");
            if ("Y".equals(table.getItemString(row, column))) {
                table.setItem(row, "SELECT_FLG", true);
                atm = this.getValueDouble("SUM_MONEY") + atm;
                this.setValue("SUM_MONEY", atm);
            }
            else {
                table.setItem(row, "SELECT_FLG", false);
                atm = this.getValueDouble("SUM_MONEY") - atm;
                this.setValue("SUM_MONEY", atm);
            }
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_M|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        table = getTable("TABLE_M");
        this.setValue("ORG_CODE", org_code);
        this.setValue("PLAN_MONTH", StringTool.getTimestamp(plan_month,
            "yyyy-MM-dd"));
        this.setValue("PLAN_NO", plan_no);
        TParm inparm = new TParm();
        inparm.setData("ORG_CODE", org_code);
        inparm.setData("PLAN_NO", plan_no);
        TParm result = INDTool.getInstance().onQueryCreatePurPlanD(inparm);
        table.setParmValue(result);

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
     * 计算总金额
     *
     * @return
     */
    private double getSumMoney() {
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            sum += table.getItemDouble(i, "ATM");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckData() {
        // 判断是否有被选中项
        table.acceptText();
        boolean flg = false;
        for (int i = 0; i < table.getRowCount(); i++) {
            if ("Y".equals(table.getItemString(i, "SELECT_FLG"))) {
                flg = true;
            }
        }
        if (!flg) {
            this.messageBox("没有选中项");
            return false;
        }
        return true;
    }
}
