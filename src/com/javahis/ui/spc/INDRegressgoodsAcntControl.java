package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.spc.IndVerifyinDTool;
import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndVerifyinMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndRegressgoodsObserver;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 退货管理Control
 * </p>
 *
 * <p>
 * Description: 退货管理Control
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
 * @author zhangy 2009.05.21
 * @version 1.0
 */
public class INDRegressgoodsAcntControl
    extends TControl {
    /**
     * 操作标记
     */
    private String action = "insert";

    // 细项序号
    private int seq;

    private TParm resultParm;

    private Map map;

    // 全部部门权限
    private boolean dept_flg = true;

    // 打印输出格式
    java.text.DecimalFormat df1 = new java.text.DecimalFormat("##########0.0");
    java.text.DecimalFormat df2 = new java.text.DecimalFormat("##########0.00");
    java.text.DecimalFormat df3 = new java.text.DecimalFormat(
        "##########0.000");
    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
        "##########0.0000");

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
        // 传入参数集
        TParm parm = new TParm();
        if (!"".equals(getValueString("ORG_CODE"))) {
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        }
        else {
            // 没有全部部门权限
            if (!dept_flg) {
                this.messageBox("请选择查询部门");
                return;
            }
        }
        if (!"".equals(getValueString("SUP_CODE"))) {
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        }
        if (!"".equals(getValueString("REGRESSGOODS_NO"))) {
            parm.setData("REGRESSGOODS_NO", getValueString("REGRESSGOODS_NO"));
        }
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return;
        }
        // 返回结果集
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction(
            "action.spc.INDRegressgoodsAction", "onQueryMAcnt", parm);//wanglong modify 20150202

        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            // 已完成
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if ("".equals(result.getValue("CHECK_USER", i))) {
                    result.removeRow(i);
                    result.setCount(result.getCount() - 1);
                }
            }
        }
        else {
            // 未完成
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if (!"".equals(result.getValue("CHECK_USER", i))) {
                    result.removeRow(i);
                    result.setCount(result.getCount() - 1);
                }
            }
        }
        //this.messageBox_(result);
        if (result == null || result.getCount("REGRESSGOODS_NO") == 0) {
            getTable("TABLE_M").removeRowAll();
            getTable("TABLE_D").removeRowAll();
            this.messageBox("无查询信息");
            return;
        }

        // 填充表格TABLE_M
        TTable table_M = getTable("TABLE_M");
        table_M.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        getCheckBox("CHECK_FLG").setEnabled(false);
        // 清空画面内容
        String clearString =
            "REGRESSGOODS_DATE;ORG_CODE;REGRESSGOODS_NO;"
            + "REASON_CHN_DESC;DESCRIPTION;CHECK_FLG;START_DATE;END_DATE"
            + "SELECT_ALL;SUM_RETAIL_ATM;SUM_REG_ATM;PRICE_DIFFERENCE";
        clearValue(clearString);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(true);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        getComboBox("ORG_CODE").setEnabled(true);
        getTextFormat("SUP_CODE").setValue("");
        getTextFormat("SUP_CODE").setText("");
        getTextFormat("SUP_CODE").setEnabled(true);
        getTextField("REGRESSGOODS_NO").setEnabled(true);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REGRESSGOODS_DATE", date);
        // 初始化全局参数
        action = "insert";
        resultParm = null;
        // 初始化页面状态及数据
        TTable table_M = getTable("TABLE_M");
        table_M.setSelectionMode(0);
        table_M.removeRowAll();
        TTable table_D = getTable("TABLE_D");
        table_D.setSelectionMode(0);
        table_D.removeRowAll();
    }

    /**
     * 保存方法
     */
    public void onSave() {
        map = new HashMap();
        // 传入参数集
        TParm parm = new TParm();
        // 返回结果集
        TParm result = new TParm();
        TNull tnull = new TNull(Timestamp.class);
        Timestamp date = SystemTool.getInstance().getDate();
        // 新增退货信息
        if ("insert".equals(action)) {
            if (!CheckDataD()) {
                return;
            }
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
            parm.setData("REGRESSGOODS_DATE", getValue("REGRESSGOODS_DATE"));
            parm.setData("REGRESSGOODS_USER", Operator.getID());
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            //zhangyong20110517
            parm.setData("REGION_CODE", Operator.getRegion());
            // 审核
            boolean check_flg = false;
            if ("Y".equals(getValueString("CHECK_FLG"))) {
                // 审核人员
                parm.setData("CHECK_USER", Operator.getID());
                // 审核时间
                parm.setData("CHECK_DATE", date);
                check_flg = true;
            }
            else {
                parm.setData("CHECK_USER", "");
                parm.setData("CHECK_DATE", tnull);
                check_flg = false;
            }
            // 细项表格
            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();

            /** 库存是否可异动状态判断 */
            String org_code = parm.getValue("ORG_CODE");
            if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
                this.messageBox("药房批次过帐中则提示请先手动做日结");
                return;
            }

            // 细项信息
            // 退货出库单号
            String reg_no = "";
            // 所有新增行
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);

            // 退货数量
            double qty = 0.00;
            // 验收量
            double ver_qty = 0.00;
            // 库存量
            double stock_qty = 0.00;
            // 状态
            String update_flg = "0";
            // 药品代码
            String order_code = "";
            // 出入库数量转换率
            TParm getTRA = new TParm();
            // 出入库效期批号者
            TParm getSEQ = new TParm();
            // 进货转换率
            double s_qty = 0.00;
            // 库存转换率
            double d_qty = 0.00;
            // 批号
            String batch_no = "";
            // 批次序号
            int batch_seq = 1;
            // 有效期
            String valid_date = "";
            // 零售价格
            double retail = 0.00;
            // 退货单价
            double regprice = 0.00;
            // 验收单号
            String ver_no = "";
            // 验收单号序号
            int ver_no_seq = 0;
            // 已退货量
            double actual_qty = 0;
            int count = 0;

            for (int i = 0; i < newrows.length; i++) {
                //System.out.println("----1" + reg_no);
                // 判断该行的启用状态
                if (!dataStore.isActive(newrows[i])) {
                    continue;
                }
                
                // 药品代码集合
                order_code = dataStore.getItemString(newrows[i], "ORDER_CODE");
                
                //System.out.println("----2" + reg_no);
                // 根据不同的发票号生成不同的退货出库单号
                reg_no = getRegressgoodsNO(dataStore.getItemString(newrows[i],
                    "INVOICE_NO"));
                //System.out.println("----3" + reg_no);
                //System.out.println("----4" + seq);
                // 出库单号集合
                parm.setData("REGRESSGOODS", count, reg_no);
                
                parm.setData("ORDER_CODE", count, order_code);
                // 出入库数量转换率
                getTRA = INDTool.getInstance().getTransunitByCode(order_code);
                if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                    this.messageBox("药品" + order_code + "转换率错误");
                    return;
                }
                // 进货转换率集合
                s_qty = getTRA.getDouble("STOCK_QTY", 0);
                parm.setData("STOCK_QTY", count, s_qty);
                // 库存转换率集合
                d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                parm.setData("DOSAGE_QTY", count, d_qty);
                
                parm.setData("SUM_QTY", count, stock_qty);
                // 批号集合
                batch_no = dataStore.getItemString(newrows[i], "BATCH_NO");
                parm.setData("BATCH_NO", count, batch_no);
                // 有效期
                valid_date = StringTool.getString(dataStore.getItemTimestamp(
                    newrows[i], "VALID_DATE"), "yyyy/MM/dd");
                parm.setData("VALID_DATE", count, valid_date);
                //**********************************************************************************
                //luhai modify 2012-01-11 batchSeq 在验收时已经记录，删除根据批次效期查询batchSeq begin
                //**********************************************************************************
//                // 出入库效期批号者
//                getSEQ = onQueryStockBatchSeq(org_code, order_code, batch_no, valid_date);
//                if (getSEQ.getErrCode() < 0 || getSEQ.getCount("BATCH_SEQ") <= 0) {
//                    this.messageBox("药品" + order_code + "批次序号错误");
//                    return;
//                }
//                batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
//                // 出入库效期批号者集合
//                parm.setData("BATCH_SEQ", count, batch_seq);
                batch_seq =StringTool.getInt(dataStore.getItemString(newrows[i], "BATCH_SEQ"));
                parm.setData("BATCH_SEQ", count, batch_seq);
                //**********************************************************************************
                //luhai modify 2012-01-11 batchSeq 在验收时已经记录，删除根据批次效期查询batchSeq end
                //**********************************************************************************
                // 退货量集合
                qty = dataStore.getItemDouble(newrows[i], "QTY");
                parm.setData("QTY", count, qty);
                // 验收量集合
                ver_qty = resultParm.getDouble("VERIFYIN_QTY", newrows[i]);
                parm.setData("VERIFYIN_QTY", count, ver_qty);
                actual_qty = resultParm.getDouble("ACTUAL_QTY", newrows[i]);
                ver_qty = ver_qty - actual_qty;

                // 零售价格集合
                retail = dataStore.getItemDouble(newrows[i], "RETAIL_PRICE");
                parm.setData("RETAIL_PRICE", count, retail);
                // 退货价格集合
                regprice = dataStore.getItemDouble(newrows[i], "UNIT_PRICE");
                parm.setData("UNIT_PRICE", count, regprice);
                // 退货序号集合
                parm.setData("SEQ_NO", count, seq);
                // 验收单号集合
                ver_no = dataStore.getItemString(newrows[i], "VERIFYIN_NO");
                parm.setData("VERIFYIN_NO", count, ver_no);
                // 验收单号序号集合
                ver_no_seq = dataStore.getItemInt(newrows[i], "VERSEQ_NO");
                parm.setData("VERSEQ_NO", count, ver_no_seq);

                // 验收单号
                dataStore.setItem(newrows[i], "REGRESSGOODS_NO", reg_no);
                // 序号
                dataStore.setItem(newrows[i], "SEQ_NO", seq);

                // 状态
                if (check_flg && qty == ver_qty) {
                    update_flg = "3";
                }
                else if (check_flg && qty < ver_qty) {
                    update_flg = "1";
                }
                else if (!check_flg) {
                    update_flg = "0";
                }

                dataStore.setItem(newrows[i], "UPDATE_FLG", update_flg);
                // OPT_USER,OPT_DATE,OPT_TERM
                dataStore.setItem(newrows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(newrows[i], "OPT_DATE", date);
                dataStore.setItem(newrows[i], "OPT_TERM", Operator.getIP());
                //
                String supOrderCode = dataStore.getItemString(newrows[i], "SUP_ORDER_CODE");
                dataStore.setItem(newrows[i], "SUP_ORDER_CODE", supOrderCode);
                
                count++;
            }
            // 得到dataStore的更新SQL
            String updateSql[] = dataStore.getUpdateSQL();
            parm.setData("UPDATE_SQL", updateSql);
            // 执行数据新增
            //System.out.println("----"+parm);
            result = TIOM_AppServer.executeAction(
                "action.spc.INDRegressgoodsAction", "onInsertAcnt", parm);//wanglong modify 20150202
            //dataStore.showDebug();
            // 保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            this.setValue("REGRESSGOODS_NO", parm.getValue("REGRESSGOODS", 0));
        }
        else if ("updateM".equals(action)) {
            //System.out.println("222222222222222222");
            // 更新退货单主项
            if (!CheckDataM()) {
                return;
            }
            // 画面值取得
            parm.setData("ORG_CODE", getValueString("ORG_CODE"));
            parm.setData("SUP_CODE", getValueString("SUP_CODE"));
            parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
            parm.setData("REASON_CHN_DESC", getValueString("REASON_CHN_DESC"));

            String check_flg = getValueString("CHECK_FLG");
            if ("Y".equals(check_flg)) {
                // 审核人员
                parm.setData("CHECK_USER", Operator.getID());
                // 审核时间
                parm.setData("CHECK_DATE", date);
                // 细项表格
                TTable table_D = getTable("TABLE_D");
                table_D.acceptText();
                TDataStore dataStore = table_D.getDataStore();

                /** 库存是否可异动状态判断 */
                String org_code = parm.getValue("ORG_CODE");
                if (!INDTool.getInstance().checkIndOrgBatch(org_code)) {
                    this.messageBox("药房批次过帐中则提示请先手动做日结");
                    return;
                }

                // 细项信息
                // 退货数量
                double qty = 0.00;
                // 验收量
                double ver_qty = 0.00;
                // 库存量
                double stock_qty = 0.00;
                // 状态
                String update_flg = "0";
                // 药品代码
                String order_code = "";
                // 出入库数量转换率
                TParm getTRA = new TParm();
                // 出入库效期批号者
                TParm getSEQ = new TParm();
                // 进货转换率
                double s_qty = 0.00;
                // 库存转换率
                double d_qty = 0.00;
                // 批号
                String batch_no = "";
                // 批次序号
                int batch_seq = 1;
                // 有效期
                String valid_date = "";
                // 零售价格
                double retail = 0.00;
                // 退货单价
                double regprice = 0.00;
                // 验收单号
                String ver_no = "";
                // 验收单号序号
                int ver_no_seq = 0;
                // 已退货量
                double actual_qty = 0;
                // 出库单号
                String reg_no = this.getValueString("REGRESSGOODS_NO");
                for (int i = 0; i < table_D.getRowCount(); i++) {
                    // 出库单号集合
                    parm.setData("REGRESSGOODS", i, reg_no);
                    // 药品代码集合
                    order_code = dataStore.getItemString(i, "ORDER_CODE");
                    parm.setData("ORDER_CODE", i, order_code);
                    // 出入库数量转换率
                    getTRA = INDTool.getInstance().getTransunitByCode(
                        order_code);
                    if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                        this.messageBox("药品" + order_code + "转换率错误");
                        return;
                    }
                    // 进货转换率集合
                    s_qty = getTRA.getDouble("STOCK_QTY", 0);
                    parm.setData("STOCK_QTY", i, s_qty);
                    // 库存转换率集合
                    d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                    parm.setData("DOSAGE_QTY", i, d_qty);
                    // 库存量
                    stock_qty = getStockQTY(org_code, order_code);
                    parm.setData("SUM_QTY", i, stock_qty);
                    // 批号集合
                    batch_no = dataStore.getItemString(i, "BATCH_NO");
                    parm.setData("BATCH_NO", i, batch_no);

                    // 有效期
                    valid_date = StringTool.getString(dataStore
                        .getItemTimestamp(i, "VALID_DATE"), "yyyy/MM/dd");
                    parm.setData("VALID_DATE", i, valid_date);
//                    Vector vector = dataStore.getVector("BATCH_SEQ");
//                    String batchSeq = dataStore.getItemString(i, "BATCH_SEQ");
//                    System.out.println(batchSeq+"======================");
                    //luhai modify 2012-05-07 begin 扣库根据batchSeq 进行处理 begin
//                    // 出入库效期批号者
//                    getSEQ = onQueryStockBatchSeq(org_code, order_code,
//                                                  batch_no, valid_date);
//                    if (getSEQ.getErrCode() < 0 ||
//                        getSEQ.getCount("BATCH_SEQ") <= 0) {
//                        this.messageBox("药品" + order_code + "批次序号错误");
//                        return;
//                    }
//                    batch_seq = getSEQ.getInt("BATCH_SEQ", 0);
//                    String batchSeqStr = dataStore.getItemString(i, "BATCH_SEQ");
//                    System.out.println("======"+batchSeqStr);
                    batch_seq =StringTool.getInt(dataStore.getItemString(i, "BATCH_SEQ"));
                  //luhai modify 2012-05-07 begin 扣库根据batchSeq 进行处理 begin
                    // 出入库效期批号者集合
                    parm.setData("BATCH_SEQ", i, batch_seq);
                    // 退货量集合
                    qty = dataStore.getItemDouble(i, "QTY");
                    parm.setData("QTY", i, qty);
                    // 验收量集合
                    TParm inparm = new TParm();
                    inparm.setData("VERIFYIN_NO",
                                   table_D.getItemString(i, "VERIFYIN_NO"));
                    inparm.setData("SEQ_NO", table_D.getItemInt(i, "VERSEQ_NO"));
                    inparm = IndVerifyinDTool.getInstance().onQueryAcnt(inparm);
                    //System.out.println("inparm" + inparm);
                    ver_qty = inparm.getDouble("VERIFYIN_QTY", 0);
                    actual_qty = inparm.getDouble("ACTUAL_QTY", 0);
                    ver_qty = ver_qty - actual_qty;
                    //System.out.println("ver_qty" + ver_qty);
                    //System.out.println("actual_qty" + actual_qty);
                    //parm.setData("VERIFYIN_QTY", i, ver_qty);
                    // 零售价格集合
                    //zhangyong20091015 添加 d_qty
                    retail = dataStore.getItemDouble(i, "RETAIL_PRICE") / d_qty;
                    parm.setData("RETAIL_PRICE", i, retail);
                    // 退货价格集合
                    regprice = dataStore.getItemDouble(i, "UNIT_PRICE");
                    parm.setData("UNIT_PRICE", i, regprice);
                    // 退货序号集合
                    //parm.setData("SEQ_NO", i, seq);
                    // 验收单号集合
                    ver_no = dataStore.getItemString(i, "VERIFYIN_NO");
                    parm.setData("VERIFYIN_NO", i, ver_no);
                    // 验收单号序号集合
                    ver_no_seq = dataStore.getItemInt(i, "VERSEQ_NO");
                    parm.setData("VERSEQ_NO", i, ver_no_seq);

                    // 出库单号
                    dataStore.setItem(i, "REGRESSGOODS_NO", reg_no);
                    // 出库单序号
                    //dataStore.setItem(i, "SEQ_NO", seq);

                    //System.out.println("1-" + qty);
                    //System.out.println("2-" + ver_qty);
                    // 状态
                    if ("Y".equals(check_flg) && qty == ver_qty) {
                        update_flg = "3";
                    }
                    else if ("Y".equals(check_flg) && qty < ver_qty) {
                        update_flg = "1";
                    }
                    else if (!"Y".equals(check_flg)) {
                        update_flg = "0";
                    }
                    dataStore.setItem(i, "UPDATE_FLG", update_flg);
                    // OPT_USER,OPT_DATE,OPT_TERM
                    dataStore.setItem(i, "OPT_USER", Operator.getID());
                    dataStore.setItem(i, "OPT_DATE", date);
                    dataStore.setItem(i, "OPT_TERM", Operator.getIP());
                }
                parm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
            }
            else {
                parm.setData("CHECK_USER", "");
                parm.setData("CHECK_DATE", tnull);
            }
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("REGRESSGOODS_NO", getValueString("REGRESSGOODS_NO"));

            // 执行数据更新
            result = TIOM_AppServer.executeAction(
                "action.spc.INDRegressgoodsAction", "onUpdateAcnt", parm);//wanglong modify 20150202
            // 保存判断
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            // 调用打印方法
            onPrint();
            if ("Y".equals(check_flg)) {
                //根据退货单号判断退货出库量更新入库状态
                updateSPCRegressGoodsDUpdateFlg(getValueString(
                    "REGRESSGOODS_NO"));
            }
        }
        else if ("updateD".equals(action)) {
            //System.out.println("3333333333333333");
            // 更新退货单细项
            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();
            // 填充resultParm
            TParm inparm = new TParm();
            inparm.setData("VERIFYIN_NO", dataStore.getItemString(0,
                "VERIFYIN_NO"));
            inparm.setData("SEQ_NO", dataStore.getItemInt(0, "VERSEQ_NO"));
            resultParm = IndVerifyinMTool.getInstance().onQueryDoneAcnt(inparm);
            // 细项检查
            if (!CheckDataD()) {
                return;
            }
            // 获得全部改动的行号
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // 给固定数据配数据
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
            }
            // 细项保存判断
            if (!table_D.update()) {
                messageBox("E0001");
                return;
            }
            messageBox("P0001");
            table_D.setDSValue();
            return;
        }
        this.onClear();
        //System.out.println("44444444444444444");
    }

    /**
     * 删除方法
     */
    public void onDelete() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            this.messageBox("单据已完成不可删除");
        }
        else {
            if (getTable("TABLE_M").getSelectedRow() > -1) {
                if (this.messageBox("删除", "确定是否删除退货单", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    TParm parm = new TParm();
                    // 细项信息
                    if (table_D.getRowCount() > 0) {
                        table_D.removeRowAll();
                        String deleteSql[] = table_D.getDataStore()
                            .getUpdateSQL();
                        parm.setData("DELETE_SQL", deleteSql);
                    }
                    // 主项信息
                    parm.setData("REGRESSGOODS_NO",
                                 getValueString("REGRESSGOODS_NO"));
                    TParm result = new TParm();
                    result = TIOM_AppServer.executeAction(
                        "action.spc.INDRegressgoodsAction", "onDeleteMAcnt",//wanglong modify 20150202
                        parm);
                    // 删除判断
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("删除失败");
                        return;
                    }
                    getTable("TABLE_M").removeRow(
                        getTable("TABLE_M").getSelectedRow());
                    this.messageBox("删除成功");
                }
            }
            else {
                if (this.messageBox("删除", "确定是否删除退货细项", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    int row = table_D.getSelectedRow();
                    table_D.removeRow(row);
                    // 细项保存判断
                    if (!table_D.update()) {
                        messageBox("E0001");
                        return;
                    }
                    messageBox("P0001");
                    table_D.setDSValue();
                }
            }
        }
    }

    /**
     * 打开验收单
     */
    public void onExport() {
    	
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("退货部门不能为空");
            return;
        }
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("退货厂商不能为空");
            return;
        }
        TParm parm = new TParm();
        String orgCode =  getValueString("ORG_CODE") ;
        parm.setData("ORG_CODE",orgCode);
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        Object result = openDialog("%ROOT%\\config\\spc\\INDUnRegressgoodsAcnt.x",
                                   parm);//wanglong modify 20150423
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            resultParm = (TParm) result;
            TTable table = getTable("TABLE_D");
            double verifyin_qty = 0;
            double actual_qty = 0;
            double verifyin_price = 0;
            double retail_price = 0;
                       
            String msg = "";
            for (int i = 0; i < addParm.getCount("ORDER_CODE"); i++) {
            	
            	String orderCode = addParm.getValue("ORDER_CODE", i);
            	String orderDesc = addParm.getValue("ORDER_DESC",i);
            	double stockQty = INDTool.getInstance().getSPCStockQTY(orgCode, orderCode);//wanglong modify 20150202
            	if(stockQty <= 0 ){
            		msg += orderCode+","+orderDesc+";" ;
            		continue ;
            	}
            	//99999999
                int row = table.addRow();
                // 填充DATESTORE
                // ORDER_CODE
               
                table.getDataStore().setItem(i, "ORDER_CODE",
                                             orderCode);
                
                
                // 填充TABLE_D数据
                // 退货量
                verifyin_qty = addParm.getDouble("VERIFYIN_QTY", i);
                actual_qty = addParm.getDouble("ACTUAL_QTY", i);
                table.setItem(row, "QTY", verifyin_qty - actual_qty);
                // 单位
                table.setItem(row, "BILL_UNIT", addParm
                              .getData("BILL_UNIT", i));
                // 退货单价
                verifyin_price = addParm.getDouble("VERIFYIN_PRICE", i);
                table.setItem(row, "UNIT_PRICE", verifyin_price);
                // 退货金额
                table.setItem(row, "AMT",
                              StringTool.round( (verifyin_qty - actual_qty)
                                               * verifyin_price, 2));
                // 零售价
                retail_price = addParm.getDouble("RETAIL_PRICE", i);
                table.setItem(row, "RETAIL_PRICE", retail_price);
                // 验收单号
                table.setItem(row, "VERIFYIN_NO", addParm.getData(
                    "VERIFYIN_NO", i));
                // 验收单号序号
                table.setItem(row, "VERSEQ_NO", addParm.getData("SEQ_NO", i));
//                // 库存量
//                double sum_qty = INDTool.getInstance().getStockQTY(
//                    this.getValueString("ORG_CODE"),
//                    addParm.getValue("ORDER_CODE", i));
//                System.out.println("----"+sum_qty);
//                if (sum_qty < 0) {
//                    sum_qty = 0;
//                }
//                table.setItem(row, "SUM_QTY", sum_qty);
                //table.getDataStore().setItem(i, "SUM_QTY", sum_qty);
                // 批号
                table.setItem(row, "BATCH_NO", addParm.getData("BATCH_NO", i));
                // 有效期
                String valid_date = StringTool.getString(addParm.getTimestamp(
                    "VALID_DATE", i), "yyyy/MM/dd");
                table.setItem(row, "VALID_DATE", valid_date);
                //luhai 2012-01-11 add batchSeq begin
                table.setItem(row, "BATCH_SEQ", addParm.getValue("BATCH_SEQ", i));
                //luhai 2012-01-11 add batchSeq end
                //luhai 2012-01-11 add verifyInPrice begin
                table.setItem(row, "VERIFYIN_PRICE", addParm.getValue("VERIFYIN_PRICE", i));
                //luhai 2012-01-11 add verifyInPrice end
                
                // 发票日期
                //table.setItem(row, "INVOICE_DATE", tnull);
                //luhai add 2012-2-12 加入发票号和发票金额begin
                table.setItem(row, "INVOICE_NO", addParm.getValue("INVOICE_NO", i));
                String invoiceDate = StringTool.getString(addParm.getTimestamp(
                        "INVOICE_DATE", i), "yyyy/MM/dd");
                table.setItem(row, "INVOICE_DATE", invoiceDate);
                
                //供应商编码 
                String supOrderCode = addParm.getValue("SUP_ORDER_CODE",i) ;
                table.setItem(row,"SUP_ORDER_CODE",supOrderCode);
                //luhai add 2012-2-12 加入发票号和发票金额end
                // 设定启用状态
                table.getDataStore().setActive(row, false);
            }
            if(msg != null && msg.length() > 0 ){
            	this.messageBox(msg+"\r\n没有库存不能退货");
            }
            action = "insert";
            getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            // getTextField("REGRESSGOODS_NO").setEnabled(false);
            table.setDSValue();
            //zhangyong20110517
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
    }

    /**
     * 打印退货单
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("REGRESSGOODS_NO"))) {
            this.messageBox("不存在退货单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "退货单");
            date.setData("ORG_CODE", "TEXT", "退货单位: " +
                         this.getComboBox("ORG_CODE").getSelectedName());
            date.setData("SUP_CODE", "TEXT", "供货厂商: " +
                         this.getTextFormat("SUP_CODE").getText());
            date.setData("DATE", "TEXT",
                         "制表日期: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));
            date.setData("REG_NO", "TEXT",
                         "出库单号: " + this.getValueString("REGRESSGOODS_NO"));
            date.setData("INVOICE_NO", "TEXT",
                         "发票号: " + table_d.getItemString(0, "INVOICE_NO"));
            date.setData("INVOICE_DATE", "TEXT",
                         "发票日期: " +
                         table_d.getItemString(0, "INVOICE_DATE").
                         substring(0, 10).replace('-', '/'));
            date.setData("INVOICE_AMT", "TEXT",
                         "发票金额: " + this.getValueString("SUM_REG_ATM"));

            // 表格数据
            TParm parm = new TParm();
            String order_code = "";
            String order_desc = "";
            //luhai modify 2012-2-11 加入零售价的总额
            double ownAmt=0;
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
//                System.out.println(INDSQL.
//                    getOrderInfoByCode(order_code,
//                                       this.getValueString("SUP_CODE"), "REG"));
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code,
                                       this.getValueString("SUP_CODE"), "REG")));
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
                             df3.format(table_d.getItemDouble(i, "QTY")));
                parm.addData("UNIT_PRICE",
                             df4.format(table_d.getItemDouble(i, "UNIT_PRICE")));
                parm.addData("AMT",
                             df2.format(StringTool.round(table_d.getItemDouble(
                                 i, "UNIT_PRICE") *
                                        table_d.getItemDouble(i, "QTY"), 2)));

                parm.addData("RETAIL_PRICE",
                             df4.format(table_d.getItemDouble(i, "RETAIL_PRICE")));
                parm.addData("SELL_SUM",
                             df2.format(StringTool.round(table_d.getItemDouble(
                                 i, "RETAIL_PRICE") *
                                        table_d.getItemDouble(i, "QTY"), 2)));
                //luhai 2012-2-11 add 零售价总额
                ownAmt+=Double.parseDouble(df2.format(StringTool.round(table_d.getItemDouble(
                        i, "RETAIL_PRICE") *
                        table_d.getItemDouble(i, "QTY"), 2)));
                parm.addData("DIFF_SUM",
                             df2.format((StringTool.round(table_d.getItemDouble(
                                 i, "RETAIL_PRICE") *
                                        table_d.getItemDouble(i, "QTY"), 2))-
(StringTool.round(table_d.getItemDouble(i, "UNIT_PRICE") * table_d.getItemDouble(i, "QTY"), 2))));

                parm.addData("BATCH_NO",
                             table_d.getItemString(i, "BATCH_NO"));
                parm.addData("VALID_DATE",
                             StringTool.getString(table_d.getItemTimestamp(i, "VALID_DATE"),"yyyy/MM/dd"));
                String phaTypeName = "";
//                this.messageBox_(inparm.getValue("PHA_TYPE",0));
                if("C".equals(inparm.getValue("PHA_TYPE",0))){
                    phaTypeName = "中成药";
                }
                if("W".equals(inparm.getValue("PHA_TYPE",0))){
                    phaTypeName = "西药";
                }
                if("G".equals(inparm.getValue("PHA_TYPE",0))){
                    phaTypeName = "中草药";
                }
                //药品分类
                parm.addData("PHA_TYPE",
                             phaTypeName);
                //生产厂商
                parm.addData("MAN_CHN_DESC",
                             inparm.getValue("MAN_CHN_DESC",0));
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
            parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "RETAIL_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "SELL_SUM");
            //2012-1-23 luhai modify delete 调价损益
//            parm.addData("SYSTEM", "COLUMNS", "DIFF_SUM");
            //2012-1-23 luhai modify delete 调价损益end
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
//            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");
//            parm.addData("SYSTEM","COLUMNS","PHA_TYPE");
//            parm.addData("SYSTEM","COLUMNS","MAN_CHN_DESC");
            //System.out.println("PARM---" + parm);
            date.setData("TABLE", parm.getData());

            // 表尾数据
            date.setData("TOT", "TEXT", "" + df2.format(
                StringTool.round(Double.parseDouble(this.
                getValueString("SUM_REG_ATM")), 2)));
            //luhai 2012-2-10 加入零售价总额
            date.setData("OWN_AMT", "TEXT", "" + df2.format(
            		StringTool.round(ownAmt, 2)));
            String format = df2.format(
            		StringTool.round(Double.parseDouble(this.
            				getValueString("SUM_REG_ATM")), 2));
            date.setData("TOT_DESC", "TEXT", StringUtil.getInstance().numberToWord(Double.parseDouble(format)));
            date.setData("USER", "TEXT", "制表人: " + Operator.getName());
            // 调用打印方法
            this.openPrintWindow("%ROOT%\\config\\prt\\spc\\RegressGoods.jhw",
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
        TTable table = getTable("TABLE_M");
        int row = table.getSelectedRow();
        if (row != -1) {
            // 主项信息(TABLE中取得)
            String org_code = table.getItemString(row, "ORG_CODE");
            setValue("ORG_CODE", org_code);
            String reg_no = table.getItemString(row, "REGRESSGOODS_NO");
            setValue("REGRESSGOODS_NO", reg_no);
            Timestamp reg_date = table.getItemTimestamp(row,
                "REGRESSGOODS_DATE");
            setValue("REGRESSGOODS_DATE", reg_date);
            String sup_code = table.getItemString(row, "SUP_CODE");
            setValue("SUP_CODE", sup_code);
            String reson_code = table.getItemString(row, "REASON_CHN_DESC");
            setValue("REASON_CHN_DESC", reson_code);
            String description = table.getItemString(row, "DESCRIPTION");
            setValue("DESCRIPTION", description);
            String check_user = table.getItemString(row, "CHECK_USER");
            if ("".equals(check_user)) {
                getCheckBox("CHECK_FLG").setSelected(false);
            }
            else {
                getCheckBox("CHECK_FLG").setSelected(true);
            }
            // 设定页面状态
            getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            getTextField("REGRESSGOODS_NO").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
            getCheckBox("CHECK_FLG").setEnabled(true);
            action = "updateM";

            // 明细信息
            TTable table_D = getTable("TABLE_D");
            table_D.removeRowAll();
            table_D.setSelectionMode(0);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getSPCRegressgoodsD(reg_no));//wanglong modify 20150202
//            System.out.println("退货细项查询sql："+INDSQL.getINDRegressgoodsD(reg_no));
            tds.retrieve();
            // 观察者
            IndRegressgoodsObserver obser = new IndRegressgoodsObserver(this.
                getValueString("ORG_CODE"));
            tds.addObserver(obser);
            table_D.setDataStore(tds);
            if (tds.rowCount() == 0) {
                seq = 1;
                this.messageBox("没有退货明细");
            }
            else {
                table_D.setDSValue();
                // 查询该验收单的验收细项信息
                TParm parm = new TParm();
                parm.setData("VERIFYIN_NO",
                             table_D.getItemString(0, "VERIFYIN_NO"));
                parm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
                parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
                resultParm = IndVerifyinDTool.getInstance().onQuerySPCVerifyinDone(
                    parm);//wanglong modify 20150202
                getCheckBox("CHECK_FLG").setEnabled(true);
                this.setValue("SUM_RETAIL_ATM", getSumRetailMoney());
                this.setValue("SUM_REG_ATM", getSumRegMoney());
                this.setValue("PRICE_DIFFERENCE", StringTool.round(
                    getSumRetailMoney() - getSumRegMoney(), 2));
                //luhai add 加入验收总额 2012-2-13 
                this.setValue("SUM_VERIFYIN_AMT", getSumVerifyPrice());
            }
        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            // 主项信息
            TTable table_M = getTable("TABLE_M");
            table_M.setSelectionMode(0);
            if (getTextField("REGRESSGOODS_NO").isEnabled()) {
                action = "insert";
            }
            else {
                action = "updateD";
            }
            // 取得SYS_FEE信息，放置在状态栏上
            String order_code = table.getDataStore().getItemString(table.
                getSelectedRow(), "ORDER_CODE");
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
        // Table的列名
        TTable table = node.getTable();
        String columnName = table.getDataStoreColumnName(node.getColumn());
        int row = node.getRow();
        if ("QTY".equals(columnName)) {
        	String orgCode = getValueString("ORG_CODE");
        	String orderCode = table.getItemString(row, "ORDER_CODE");
        	String batchNo = table.getItemString(row, "BATCH_NO");
        	String validDate = table.getItemString(row,"VALID_DATE").substring(0,
                    10);
        	double stockQty = INDTool.getInstance().getStockQTYAcnt(orgCode, orderCode, batchNo,  validDate, "");
        	if(stockQty <= 0 ){
        		this.messageBox("库存不够不能退货!");
        		return true;
        	}
            double qty = TypeTool.getDouble(node.getValue());
         // 出入库数量转换率
            TParm getTRA = INDTool.getInstance().getTransunitByCode(
                orderCode);
            double sQty = getTRA.getDouble("DOSAGE_QTY",0);
            if(qty*sQty > stockQty){
            	this.messageBox("退货数量大于库存量不能退货!");
            	return true;
            }
            
            if (qty <= 0) {
                this.messageBox("退货数量不能小于或等于0");
                return true;
            }
            double amt = StringTool.round(qty
                                          *
                                          table.getItemDouble(row, "UNIT_PRICE"),
                                          2);
            table.setItem(row, "AMT", amt);
            //
            table.getDataStore().setItem(row, "AMT", amt);
            return false;
        }
        if ("UNIT_PRICE".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("验收单价不能小于或等于0");
                return true;
            }
            double amt = StringTool.round(
                qty * table.getItemDouble(row, "QTY"), 2);
            table.setItem(row, "AMT", amt);
            //
            table.getDataStore().setItem(row, "AMT", amt);
            return false;
        }
        // 发票号码
        if ("INVOICE_NO".equals(columnName) && row == 0) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.setItem(i, "INVOICE_NO", node.getValue());
            }
        }
        // 发票日期
        if ("INVOICE_DATE".equals(columnName)) {
            if (row == 0) {
                for (int i = 0; i < table.getRowCount(); i++) {
                    table.setItem(i, "INVOICE_DATE", node.getValue());
                }
            }
            return false;
        }
        return false;
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        // 获得点击的table对象
        TTable tableDown = (TTable) obj;
        // 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
        tableDown.acceptText();
        // 获得选中的列
        int column = tableDown.getSelectedColumn();
        // 获得选中的行
        int row = tableDown.getSelectedRow();
        if (column == 0) {
            if (tableDown.getDataStore().isActive(row)) {
                // 计算零售总金额
                double amount1 = tableDown.getItemDouble(row, "QTY");
                double sum = tableDown.getItemDouble(row, "RETAIL_PRICE")
                    * amount1;
                double sum_atm = this.getValueDouble("SUM_RETAIL_ATM");
                this.setValue("SUM_RETAIL_ATM", StringTool.round(sum + sum_atm,
                    2));
                // 计算退货总金额
                double reg_amt = this.getValueDouble("SUM_REG_ATM");
                double amt = tableDown.getItemDouble(row, "AMT");
                amt = StringTool.round(reg_amt + amt, 2);
                this.setValue("SUM_REG_ATM", amt);
            }
            else {
                // 计算零售总金额
                double amount1 = tableDown.getItemDouble(row, "QTY");
                double sum = tableDown.getItemDouble(row, "RETAIL_PRICE")
                    * amount1;
                double sum_atm = this.getValueDouble("SUM_RETAIL_ATM");
                if (sum_atm - sum < 0) {
                    this.setValue("SUM_RETAIL_ATM", 0);
                }
                else {
                    this.setValue("SUM_RETAIL_ATM", StringTool.round(sum_atm
                        - sum, 2));
                }
                // 计算退货总金额
                double reg_amt = this.getValueDouble("SUM_REG_ATM");
                double amt = tableDown.getItemDouble(row, "AMT");
                if (reg_amt - amt < 0) {
                    this.setValue("SUM_REG_ATM", 0);
                }
                else {
                    this.setValue("SUM_REG_ATM", StringTool.round(
                        reg_amt - amt, 2));
                }
            }
            // 进销差价
            this.setValue("PRICE_DIFFERENCE", StringTool.round(
                getValueDouble("SUM_RETAIL_ATM")
                - getValueDouble("SUM_REG_ATM"), 2));
        }
    }

    /**
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
        TTable table = getTable("TABLE_D");
        table.acceptText();
        if (table.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getDataStore().setActive(i, true);
                table.getDataStore().setItem(i, "UPDATE_FLG", "0");
                this.setValue("SUM_RETAIL_ATM", getSumRetailMoney());
                this.setValue("SUM_REG_ATM", getSumRegMoney());
                this.setValue("PRICE_DIFFERENCE", StringTool.round(
                    getSumRetailMoney() - getSumRegMoney(), 2));
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                table.getDataStore().setActive(i, false);
                table.getDataStore().setItem(i, "UPDATE_FLG", "");
                this.setValue("SUM_RETAIL_ATM", 0);
                this.setValue("SUM_REG_ATM", 0);
                this.setValue("PRICE_DIFFERENCE", 0);
            }
        }
        table.setDSValue();
    }

    /**
     * 单选框改变事件
     */
    public void onChangeRadioButton() {
        if (this.getRadioButton("UPDATE_FLG_A").isSelected()) {
            ( (TMenuItem) getComponent("save")).setEnabled(false);
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
        else {
            ( (TMenuItem) getComponent("save")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
        }
    }


    /**
     * 初始画面数据
     */
    private void initPage() {
        /**
         * 权限控制
         * 权限1:一般个人权限,只显示自已所属科室
         * 权限9:最大权限,显示全院药库部门
         */
        // 显示全院药库部门
        if (!this.getPopedem("deptAll")) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                getIndOrgByUserId(Operator.getID(), Operator.getRegion(), " AND B.ORG_TYPE = 'A' ")));
            getComboBox("ORG_CODE").setParmValue(parm);
            if (parm.getCount("NAME") > 0) {
                getComboBox("ORG_CODE").setSelectedIndex(1);
            }
            dept_flg = false;
        }

        // 添加侦听事件
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // 初始化页面状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("REGRESSGOODS_DATE", date);
        action = "insert";
//        TTable table_D = getTable("TABLE_D");
//        table_D.removeRowAll();
//        table_D.setSelectionMode(0);
//        TDS tds = new TDS();
//        tds.setSQL(INDSQL.getINDRegressgoodsD(getValueString("REGRESSGOODS_NO")));
//        tds.retrieve();
//        if (tds.rowCount() == 0) {
//            seq = 1;
//        }
//        else {
//            seq = getMaxSeq(tds, "SEQ_NO");
//        }
//
//        // 观察者
//        IndRegressgoodsObserver obser = new IndRegressgoodsObserver(this.
//            getValueString("ORG_CODE"));
//        tds.addObserver(obser);
//        table_D.setDataStore(tds);
//        table_D.setDSValue();
        resultParm = new TParm();
    }

    /**
     * 取得退货单号
     *
     * @return String
     */
    private String getRegressgoodsNO(String invoiceNo) {
        String regNo = "";
        if (!map.isEmpty() && map.containsKey(invoiceNo)) {
            String result[] = ( (String) map.get(invoiceNo)).split("-");
            regNo = result[0];
            seq = Integer.parseInt(result[1]) + 1;
            map.put(invoiceNo, regNo + "-" + seq);
            //System.out.println("a"+result[0] + "--b"+result[1] + ((String) map.get(invoiceNo)));
        }
        else {
            regNo = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_REGRESSGOODS", "No");
            seq = 1;
            map.put(invoiceNo, regNo + "-" + seq);
        }
        //System.out.println("----" + regNo);
        return regNo;
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("退货部门不能为空");
            return false;
        }
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("退货厂商不能为空");
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
        // 判断细项是否有被选中项
        TTable table = getTable("TABLE_D");
        table.acceptText();
        int count = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (table.getDataStore().isActive(i)) {
                count++;
                // 判断数据正确性
                double qty = table.getItemDouble(i, "QTY");
                if (qty <= 0) {
                    this.messageBox("退货数量不能小于或等于0");
                    return false;
                }
                double pur_qty = 0;
                double actual = 0;
/*                if ("insert".equals(action)) {
                    pur_qty = resultParm.getDouble("VERIFYIN_QTY", i);
                    this.messageBox("***"+pur_qty);
                }	
*/                 if ("insert".equals(action)) {
                    TParm inparm = new TParm();
                    inparm.setData("VERIFYIN_NO",
                                   table.getItemString(i, "VERIFYIN_NO"));
                    inparm.setData("SEQ_NO", table.getItemInt(i, "VERSEQ_NO"));
                    inparm = IndVerifyinDTool.getInstance().onQueryAcnt(inparm);//wanglong modify 20150202
                    //System.out.println("inparm" + inparm);
                    pur_qty = inparm.getDouble("VERIFYIN_QTY", 0);
                    actual = inparm.getDouble("ACTUAL_QTY", 0);
                    //pur_qty = pur_qty - actual;			
                }
                if (qty > actual) {
                    this.messageBox("退货数量不能大于验收数量");
                    return false;
                }
                qty = table.getItemDouble(i, "UNIT_PRICE");
                if (qty <= 0) {
                    this.messageBox("退货单价不能小于或等于0");
                    return false;
                }
                // 发票号码
                if ("".equals(table.getItemString(i, "INVOICE_NO"))) {
                    this.messageBox("发票号码不能为空");
                    return false;
                }
                // 发票日期
                if (table.getItemData(i, "INVOICE_DATE") == null) {
                    this.messageBox("发票日期不能为空");
                    return false;
                }
                // 批号
                if ("".equals(table.getItemString(i, "BATCH_NO"))) {
                    this.messageBox("批号不能为空");
                    return false;
                }
                // 效期
                if (table.getItemData(i, "VALID_DATE") == null) {
                    this.messageBox("效期不能为空");
                    return false;
                }
            }
        }
        if (count == 0) {
            this.messageBox("没有执行数据");
            return false;
        }
        return true;
    }

    /**
     * 计算零售总金额:SUM_RETAIL_ATM
     *
     * @return
     */
    private double getSumRetailMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                double amount1 = table.getItemDouble(i, "QTY");
                sum += table.getItemDouble(i, "RETAIL_PRICE") * amount1;
            }
        }
        return StringTool.round(sum, 2);
    }
    /**
     * 计算验收总金额:SUM_VERIFYIN_PRICE
     *
     * @return
     */
    private double getSumVerifyPrice() {
    	TTable table = getTable("TABLE_D");
    	TDataStore ds = table.getDataStore();
    	table.acceptText();
    	double sum = 0;
    	for (int i = 0; i < table.getRowCount(); i++) {
    		if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
    			double amount1 = table.getItemDouble(i, "QTY");
    			sum += table.getItemDouble(i, "VERIFYIN_PRICE") * amount1;
    		}
    	}
    	return StringTool.round(sum, 2);
    }

    /**
     * 计算退货总金额:SUM_REG_ATM
     *
     * @return
     */
    private double getSumRegMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                sum += table.getItemDouble(i, "AMT");
            }
        }
        return StringTool.round(sum, 2);
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
     * 药库科室变更事件
     */
    public void onChangeOrgCode() {
        onInsertObserver();
    }

    /**
     * 添加观察者
     */
    public void onInsertObserver() {
        TTable table_D = getTable("TABLE_D");
        table_D.removeRowAll();
        table_D.setSelectionMode(0);
        TDS tds = new TDS();
        tds.setSQL(INDSQL.getSPCRegressgoodsD(getValueString("REGRESSGOODS_NO")));//wanglong modify 20150202
        tds.retrieve();
        if (tds.rowCount() == 0) {
            seq = 1;
        }
        else {
            seq = getMaxSeq(tds, "SEQ_NO");
        }

        // 观察者
        IndRegressgoodsObserver obser = new IndRegressgoodsObserver(this.
            getValueString("ORG_CODE"));
        tds.addObserver(obser);
        table_D.setDataStore(tds);
        table_D.setDSValue();
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

    //根据退货单号判断退货出库量更新入库状态
    private void updateSPCRegressGoodsDUpdateFlg(String regressgoods_no) {
        String sql =
            "UPDATE SPC_REGRESSGOODSD SET UPDATE_FLG = '3' WHERE REGRESSGOODS_NO = '" +//wanglong modify 20150202
            regressgoods_no + "'";
        TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    }

    /**
     * 根据药库编号,药品代码,批号,有效期查询药品的批次序号
     *
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public String getIndStockBatchSeq(String org_code,
                                      String order_code, String batch_no,
                                      String valid_date) {
        return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
            " VERIFYIN_PRICE FROM SPC_STOCK " + "WHERE ORG_CODE = '"//wanglong modify 20150202
            + org_code + "' AND ORDER_CODE = '" + order_code
            + "' AND BATCH_NO = '" + batch_no
            + "' AND VALID_DATE = TO_DATE('" + valid_date
            + "','yyyy-MM-dd') ";
    }

    /**
     * 根据药库编号,药品代码,批号,有效期查询药品的批次序号
     *
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public TParm onQueryStockBatchSeq(String org_code, String order_code,
                                      String batch_no, String valid_date) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            getIndStockBatchSeq(org_code, order_code, batch_no,
                                       valid_date)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    public String getIndStockQty(String order_code, String org_code) {
        return "SELECT SUM(LAST_TOTSTOCK_QTY+ IN_QTY- OUT_QTY + "
            + "CHECKMODI_QTY) AS QTY FROM SPC_STOCK WHERE ORG_CODE = '" +//wanglong modify 20150202
            org_code + "' AND ORDER_CODE = '" + order_code + "'";
    }

    /**
     * 根据药库编号及药品代码查询药库库存量
     *
     * @param org_code
     *            药库编号
     * @param order_code
     *            药品代码
     * @return QTY 库存量
     */
    public double getStockQTY(String org_code, String order_code) {
        if ("".equals(org_code) || "".equals(order_code)) {
            return -1;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", org_code);
        parm.setData("ORDER_CODE", order_code);
        TParm result = new TParm(TJDODBTool.getInstance().select(getIndStockQty(
            org_code, order_code)));
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return -1;
        }
        return result.getDouble("QTY", 0);
    }

}
