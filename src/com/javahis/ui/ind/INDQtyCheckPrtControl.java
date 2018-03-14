package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import jdo.ind.IndQtyCheckTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.jdo.TJDODBTool;
import jdo.ind.INDSQL;
import jdo.sys.Operator;
import jdo.util.Manager;
import com.dongyang.util.StringTool;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DecimalFormat;

/**
 * <p>
 * Title: 盘点报表
 * </p>
 *
 * <p>
 * Description: 盘点报表
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
 * @author zhangy 2009.09.10
 * @version 1.0
 */
public class INDQtyCheckPrtControl
    extends TControl {

    private TParm parm;

    private String org_code;

    public INDQtyCheckPrtControl() {
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
        }
        //org_code = "30101";
        // 初始画面数据
        initPage();
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getComboBox("FROZEN_DATE").setSelectedIndex(0);
    }

    /**
     * 打印方法
     */
    public void onPrint() {
        if (this.getRadioButton("RadioButton1").isSelected()) {
            onPrintQtyCheckM();
        }
        else if (this.getRadioButton("RadioButton2").isSelected()) {
            onPrintQtyCheckD();
        }
        else {
            onPrintProfitLoss();
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
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
    }

    /**
     * 盘点表
     */
    private void onPrintQtyCheckM() {
        String drozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (getComboBox("FROZEN_DATE").getSelectedIndex() == 0) {
            this.messageBox("请选择冻结时间");
            return;
        }
        String sql = INDSQL.getIndQtyCheckM(org_code, drozen_date);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("没有盘点数据");
            return;
        }

        // 打印数据
        TParm data = new TParm();
        // 表头数据
        data.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "盘点表");
        data.setData("ORG_CODE", "TEXT",
                     "盘点部门: " + parm.getValue("DEPT_CHN_DESC", 0));
        data.setData("FROZEN_DATE", "TEXT",
                     "冻结时间: " + this.getComboBox("FROZEN_DATE").getSelectedName());
        Timestamp datetime = StringTool.getTimestamp(new Date());
        data.setData("DATE", "TEXT",
                     "制表日期: " +
                     datetime.toString().substring(0, 10).replace('-', '/'));

        // 表格数据
        TParm parmData = new TParm();
        String pha_type = "";
        for (int i = 0; i < parm.getCount(); i++) {
            parmData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            parmData.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
            //luhai 2012-3-7 将药品分类改成批号效期 begin
//            if ("W".equals(parm.getValue("PHA_TYPE", i))) {
//                pha_type = "西药";
//            }
//            else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
//                pha_type = "中成药 ";
//            }
//            else {
//                pha_type = "中草药";
//            }
//            parmData.addData("PHA_TYPE", pha_type);
//            parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i));
//            parmData.addData("CHECK_QTY", parm.getValue("ACTUAL_CHECK_QTY", i));
//            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
//            parmData.addData("VALID_DATE",
//                             parm.getValue("VALID_DATE", i).substring(0, 10).
//                             replace('-', '/'));
//            parmData.addData("MAN_CODE", parm.getValue("MATERIAL_CHN_DESC", i));
//            parmData.addData("PHA_TYPE", pha_type);
            parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i));
//            parmData.addData("CHECK_QTY", parm.getValue("ACTUAL_CHECK_QTY", i));
            parmData.addData("CHECK_QTY", "");
            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
            parmData.addData("VALID_DATE",
                             parm.getValue("VALID_DATE", i).substring(0, 10).
                             replace('-', '/'));
            parmData.addData("MAN_CODE", parm.getValue("MATERIAL_CHN_DESC", i));
          //luhai 2012-3-7 将药品分类改成批号效期 end
        }

        parmData.setCount(parm.getCount());
        parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "CHECK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        //luhai 2012-2-13 delete batch_no man_code
//        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parmData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
//        parmData.addData("SYSTEM", "COLUMNS", "MAN_CODE");

        data.setData("TABLE", parmData.getData());

        // 表尾数据
        data.setData("USER", "TEXT", "制表人: " + Operator.getName());
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\QtyCheckM.jhw",
                             data);
    }

    /**
     * 盘点明细表
     */
    private void onPrintQtyCheckD() {
        String drozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (getComboBox("FROZEN_DATE").getSelectedIndex() == 0) {
            this.messageBox("请选择冻结时间");
            return;
        }
        String sql = INDSQL.getIndQtyCheckD(org_code, drozen_date);
//        System.out.println("sql---"+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("没有盘点明细数据");
            return;
        }

        // 打印数据
        TParm data = new TParm();
        // 表头数据
        data.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "盘点明细表");
        data.setData("ORG_CODE", "TEXT",
                     "盘点部门: " + parm.getValue("DEPT_CHN_DESC", 0));
        data.setData("FROZEN_DATE", "TEXT",
                     "冻结时间: " + this.getComboBox("FROZEN_DATE").getSelectedName());
        Timestamp datetime = StringTool.getTimestamp(new Date());
        data.setData("DATE", "TEXT",
                     "制表日期: " +
                     datetime.toString().substring(0, 10).replace('-', '/'));

        // 表格数据
        TParm parmData = new TParm();
        String pha_type = "";
        double contract_amt_w = 0;
        double own_amt_w = 0;
        double contract_amt_c = 0;
        double own_amt_c = 0;
        double contract_amt_g = 0;
        double own_amt_g = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            parmData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            parmData.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
            if ("W".equals(parm.getValue("PHA_TYPE", i))) {
                pha_type = "西药";
                contract_amt_w += parm.getDouble("STOCK_AMT", i);
                own_amt_w += parm.getDouble("OWM_AMT", i);
            }
            else if ("C".equals(parm.getValue("PHA_TYPE", i))) {
                pha_type = "中成药 ";
                contract_amt_c += parm.getDouble("STOCK_AMT", i);
                own_amt_c += parm.getDouble("OWM_AMT", i);
            }
            else {
                pha_type = "中草药";
                contract_amt_g += parm.getDouble("STOCK_AMT", i);
                own_amt_g += parm.getDouble("OWM_AMT", i);
            }
            parmData.addData("PHA_TYPE", pha_type);
            parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i));
            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
            parmData.addData("VALID_DATE",
                             parm.getValue("VALID_DATE", i).substring(0, 10).
                             replace('-', '/'));
            parmData.addData("CONTRACT_PRICE",
                             parm.getValue("CONTRACT_PRICE", i));
            parmData.addData("OWN_PRICE", parm.getValue("OWN_PRICE", i));
            parmData.addData("STOCK_AMT", parm.getValue("STOCK_AMT", i));
            parmData.addData("OWM_AMT", parm.getValue("OWM_AMT", i));
        }

        parmData.setCount(parm.getCount());
        parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmData.addData("SYSTEM", "COLUMNS", "PHA_TYPE");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        //luhai delete 2012-2-13 begin
//        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//        parmData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        //luhai delete 2012-2-13 end
        parmData.addData("SYSTEM", "COLUMNS", "CONTRACT_PRICE");
        parmData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
        parmData.addData("SYSTEM", "COLUMNS", "OWM_AMT");

        data.setData("TABLE", parmData.getData());

        // 表尾数据
        data.setData("USER", "TEXT", "制表人: " + Operator.getName());
        //删除合计信息 西药，中药，草药 luhai 2012-3-7 begin
        TParm parmEnd = new TParm();
//        parmEnd.addData("COL_1", "");
//        parmEnd.addData("COL_2", "");
//        parmEnd.addData("COL_3", "");
//        parmEnd.addData("COL_4", "");
//        parmEnd.addData("COL_5", "西药合计:");
//        parmEnd.addData("COL_6", StringTool.round(contract_amt_w, 2));
//        parmEnd.addData("COL_7", StringTool.round(own_amt_w, 2));
//
//        parmEnd.addData("COL_1", "");
//        parmEnd.addData("COL_2", "");
//        parmEnd.addData("COL_3", "");
//        parmEnd.addData("COL_4", "");
//        parmEnd.addData("COL_5", "中成药合计:");
//        parmEnd.addData("COL_6", StringTool.round(contract_amt_c, 2));
//        parmEnd.addData("COL_7", StringTool.round(own_amt_c, 2));
//
//        parmEnd.addData("COL_1", "");
//        parmEnd.addData("COL_2", "");
//        parmEnd.addData("COL_3", "");
//        parmEnd.addData("COL_4", "");
//        parmEnd.addData("COL_5", "中草药合计:");
//        parmEnd.addData("COL_6", StringTool.round(contract_amt_g, 2));
//        parmEnd.addData("COL_7", StringTool.round(own_amt_g, 2));
      //删除合计信息 西药，中药，草药 luhai 2012-3-7 end
        parmEnd.addData("COL_1", "");
        parmEnd.addData("COL_2", "");
        parmEnd.addData("COL_3", "");
        parmEnd.addData("COL_4", "");
        parmEnd.addData("COL_5", "合计:");
        parmEnd.addData("COL_6",
                        StringTool.round(contract_amt_w + contract_amt_c + contract_amt_g,
                                         2));
        parmEnd.addData("COL_7",
                        StringTool.round(own_amt_w + own_amt_c + own_amt_g, 2));

        parmEnd.addData("COL_1", "负责人:");
        parmEnd.addData("COL_2", "药品会计:");
        parmEnd.addData("COL_3", "药房负责人:");
        parmEnd.addData("COL_4", "审核人:");
        parmEnd.addData("COL_5", "盘点人员:");
        parmEnd.addData("COL_6", "        制表人:");
        parmEnd.addData("COL_7", Operator.getName());

        parmEnd.setCount(5);
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_1");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_2");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_3");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_4");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_5");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_6");
        parmEnd.addData("SYSTEM", "COLUMNS", "COL_7");

        data.setData("TABLE_2", parmEnd.getData());

        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\QtyCheckD.jhw",
                             data);
    }

    /**
     * 盈亏表
     */
    private void onPrintProfitLoss() {
        DecimalFormat df = new DecimalFormat("##########0.00");
        DecimalFormat df2 = new DecimalFormat("##########0.0000");
        String drozen_date = getComboBox("FROZEN_DATE").getSelectedID();
        if (getComboBox("FROZEN_DATE").getSelectedIndex() == 0) {
            this.messageBox("请选择冻结时间");
            return;
        }
        String sql = INDSQL.getIndQtyProfitLoss(org_code, drozen_date);
//        System.out.println("sql=== "+sql);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm == null || parm.getCount() <= 0) {
            this.messageBox("没有盘点盈亏数据");
            return;
        }

        // 打印数据
        TParm data = new TParm();
        // 表头数据
        data.setData("TITLE", "TEXT",
                     Manager.getOrganization().
                     getHospitalCHNFullName(Operator.getRegion()) +
                     "盘点盈亏表");
        data.setData("ORG_CODE", "TEXT",
                     "盘点部门: " + parm.getValue("DEPT_CHN_DESC", 0));
        data.setData("FROZEN_DATE", "TEXT",
                     "冻结时间: " + this.getComboBox("FROZEN_DATE").getSelectedName());
        String unfreeze_date = parm.getValue("UNFREEZE_DATE", 0);
        if (unfreeze_date.length() > 19) {
            unfreeze_date = unfreeze_date.substring(0, 19).replace('-', '/');
        }
        data.setData("UN_FROZEN_DATE", "TEXT",
                     "解冻时间: " + unfreeze_date);
        Timestamp datetime = StringTool.getTimestamp(new Date());
        data.setData("DATE", "TEXT",
                     "制表日期: " +
                     datetime.toString().substring(0, 10).replace('-', '/'));

        // 表格数据
        TParm parmData = new TParm();
        double sum_freeze_amt = 0;
        double sum_own_amt = 0;
        for (int i = 0; i < parm.getCount(); i++) {
            parmData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
            parmData.addData("SPECIFICATION", parm.getValue("SPECIFICATION", i));
//            parmData.addData("BATCH_NO", parm.getValue("BATCH_NO", i));
//            parmData.addData("VALID_DATE",
//                             parm.getValue("VALID_DATE", i).substring(0, 10).
//                             replace('-', '/'));
            parmData.addData("BATCH_NO", "");
            parmData.addData("VALID_DATE","");
            parmData.addData("STOCK_QTY", parm.getValue("STOCK_QTY", i));
            parmData.addData("ACTUAL_CHECK_QTY", parm.getValue("ACTUAL_CHECK_QTY", i));
            parmData.addData("MODI_QTY", parm.getValue("MODI_QTY", i));
            parmData.addData("OWN_PRICE",df2.format(parm.getDouble("OWN_PRICE", i)));
//            parmData.addData("FREEZE_AMT", df.format(parm.getDouble("FREEZE_AMT", i)));
            sum_freeze_amt += parm.getDouble("FREEZE_AMT", i);
            parmData.addData("OWN_AMT", df.format(parm.getDouble("OWN_AMT", i)));
            sum_own_amt += parm.getDouble("OWN_AMT", i);
        }

        parmData.setCount(parm.getCount());
        parmData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
        parmData.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
        parmData.addData("SYSTEM", "COLUMNS", "BATCH_NO");
        parmData.addData("SYSTEM", "COLUMNS", "VALID_DATE");
        parmData.addData("SYSTEM", "COLUMNS", "STOCK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "ACTUAL_CHECK_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "MODI_QTY");
        parmData.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
//        parmData.addData("SYSTEM", "COLUMNS", "FREEZE_AMT");
        parmData.addData("SYSTEM", "COLUMNS", "OWN_AMT");

        //luhai 2012-3-7 删除损益金额合计 begin
//        data.setData("SUM_FREEZE_AMT", "TEXT",
//        		"损益金额合计:" + StringTool.round(sum_freeze_amt, 2));
        data.setData("SUM_FREEZE_AMT", "TEXT",
        "");
        //luhai 2012-3-7 删除损益金额合计 end
        data.setData("SUM_OWN_AMT", "TEXT",
                     "现存实际金额合计:" + StringTool.round(sum_own_amt, 2));

        data.setData("TABLE", parmData.getData());

        // 表尾数据
        data.setData("USER", "TEXT", "制表人: " + Operator.getName());
        // 调用打印方法
        this.openPrintWindow("%ROOT%\\config\\prt\\IND\\ProfitLoss.jhw",
                             data);

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

}
