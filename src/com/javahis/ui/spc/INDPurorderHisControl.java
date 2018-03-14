package com.javahis.ui.spc;

import java.awt.Component;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndPurorderDTool;
import jdo.spc.IndStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import org.dom4j.Document;

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
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.IndPurOrderObserver;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: HIS订购管理Control
 * </p>
 *
 * <p>
 * Description: HIS订购管理Control
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
 * @author zhangy 2009.05.12
 * @version 1.0
 */

public class INDPurorderHisControl
    extends TControl {

    /**
     * 操作标记：1.insertM：新增订购计划单主档 2.updateM：更新订购计划单主档
     */
    private String action = "insertM";

    // 细项序号
    private int seq;

    // 赠与权限
    private boolean gift_flg = true;

    // 全部部门权限
    private boolean dept_flg = true;

    public INDPurorderHisControl() {
        super();
    }
    //格式化验收数
    java.text.DecimalFormat dfInt = new java.text.DecimalFormat("##########0");
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
        if (!"".equals(getValueString("PURORDER_NO"))) {
            parm.setData("PURORDER_NO", getValueString("PURORDER_NO"));
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
        result = TIOM_AppServer.executeAction("action.spc.INDPurorderAction",
                                              "onQueryM", parm);

        // 订购来源
        if (getRadioButton("STATIC_NO_B").isSelected()) {
            // 手工订购
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if (!"".equals(result.getValue("PLAN_NO", i))) {
                    result.removeRow(i);
                }
            }
        }
        else if (getRadioButton("STATIC_NO_C").isSelected()) {
            // 采购计划
            for (int i = result.getCount() - 1; i >= 0; i--) {
                if ("".equals(result.getValue("PLAN_NO", i))) {
                    result.removeRow(i);
                }
            }
        }
        // 查询细项订购状态
        String update = "0";
        boolean flg = false;
        TDataStore ds = new TDataStore();
        for (int i = result.getCount() - 1; i >= 0; i--) {
            ds.setSQL(INDSQL
                      .getPurOrderDByNo(result.getValue("PURORDER_NO", i)));
            ds.retrieve();
            if (ds.rowCount() == 0) {
                flg = false;
            }
            else {
                flg = true;
                for (int j = 0; j < ds.rowCount(); j++) {
                    update = ds.getItemString(j, "UPDATE_FLG");
                    if ("0".equals(update) || "1".equals(update)) {
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
                    result.removeRow(i);
                }
            }
            else {
                // 完成
                if (!flg) {
                    result.removeRow(i);
                }
            }
        }

        if (result == null || result.getCount("PURORDER_NO") == 0 ||
            result.getCount() <= 0) {
            getTable("TABLE_M").removeRowAll();
            getTable("TABLE_D").removeRowAll();
            this.messageBox("没有查询数据");
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
        getRadioButton("STATIC_NO_A").setSelected(true);
        getRadioButton("UPDATE_FLG_A").setSelected(true);
        String clearString =
            "START_DATE;END_DATE;PURORDER_DATE;ORG_CODE;PURORDER_NO;"
            + "RES_DELIVERY_DATE;PLAN_NO;REASON_CODE;DESCRIPTION";
        this.clearValue(clearString);
        getTextFormat("SUP_CODE").setValue(null);
        getTextFormat("SUP_CODE").setText("");
        Timestamp date = SystemTool.getInstance().getDate();
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("PURORDER_DATE", date);
        // 画面状态
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        getTextField("PURORDER_NO").setEnabled(true);
        getTextFormat("SUP_CODE").setEnabled(true);
        getTable("TABLE_M").setSelectionMode(0);
        getTable("TABLE_D").setSelectionMode(0);
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
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        if ("insertM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            parm.setData("PURORDER_DATE", getValue("PURORDER_DATE"));
            String purorder_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_PURORDER", "No");
            //System.out.println("Purorder_No:" + purorder_no);
            parm.setData("PURORDER_NO", purorder_no);
            parm.setData("ORG_CODE", getValue("ORG_CODE"));
            parm.setData("SUP_CODE", getValue("SUP_CODE"));
            if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
                parm.setData("RES_DELIVERY_DATE", getValue("RES_DELIVERY_DATE"));
            }
            else {
                parm.setData("RES_DELIVERY_DATE", tnull);
            }
            parm.setData("REASON_CHN_DESC", getValue("REASON_CODE"));
            parm.setData("DESCRIPTION", getValue("DESCRIPTION"));
            //System.out.println("PLAN_NO" + getValueString("PLAN_NO"));
            parm.setData("PLAN_NO", getValueString("PLAN_NO"));
            //zhangyong20110517
            parm.setData("REGION_CODE", Operator.getRegion());
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            // 执行数据新增
            result = TIOM_AppServer.executeAction(
                "action.spc.INDPurorderAction", "onInsertM", parm);
        }
        else if ("updateM".equals(action)) {
            if (!CheckDataM()) {
                return;
            }
            // 画面值取得
            parm.setData("PURORDER_DATE", getValue("PURORDER_DATE"));
            parm.setData("ORG_CODE", getValue("ORG_CODE"));
            parm.setData("SUP_CODE", getValue("SUP_CODE"));
            if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
                parm.setData("RES_DELIVERY_DATE", getValue("RES_DELIVERY_DATE"));
            }
            else {
                parm.setData("RES_DELIVERY_DATE", tnull);
            }
            parm.setData("REASON_CHN_DESC", getValue("REASON_CODE"));
            parm.setData("DESCRIPTION", getValue("DESCRIPTION"));
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("PURORDER_NO", getValue("PURORDER_NO"));
            // 执行数据更新
            result = TIOM_AppServer.executeAction(
                "action.spc.INDPurorderAction", "onUpdateM", parm);
        }
        else {
            // 细项检查
            if (!CheckDataD()) {
                return;
            }

            TTable table_D = getTable("TABLE_D");
            table_D.acceptText();
            TDataStore dataStore = table_D.getDataStore();
            // 获得全部改动的行号
            int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
            // 获得全部的新增行
            int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);

            // 给固定数据配数据
            for (int i = 0; i < newrows.length; i++) {
                dataStore.setItem(newrows[i], "PURORDER_NO",
                                  getValueString("PURORDER_NO"));
                dataStore.setItem(newrows[i], "SEQ_NO", seq + i);
                dataStore.setItem(newrows[i], "UPDATE_FLG", "0");
            }
            TParm inParm = new TParm();
            if ("insert".equals(action)) {
                inParm.setData("PLAN_NO", this.getValueString("PLAN_NO"));
                inParm.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
            }
            TParm getTRA = new TParm();
            TParm getQTY = new TParm();
            String order_code = "";
            String org_code = getValueString("ORG_CODE");
            double s_qty = 0;
            double d_qty = 0;
            for (int i = 0; i < rows.length; i++) {
                dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
                dataStore.setItem(rows[i], "OPT_DATE", date);
                dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
                order_code = dataStore.getItemString(rows[i], "ORDER_CODE");
                inParm.setData("ORDER_CODE", i, order_code);
                inParm.setData("PURORDER_QTY", i, dataStore.getItemDouble(
                    rows[i], "PURORDER_QTY"));
                // 出入库数量转换率
                getTRA = INDTool.getInstance().getTransunitByCode(order_code);
                if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                    this.messageBox("药品" + order_code + "转换率错误");
                    return;
                }
                s_qty = getTRA.getDouble("STOCK_QTY", 0);
                inParm.setData("STOCK_QTY", i, s_qty);
                d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                inParm.setData("DOSAGE_QTY", i, d_qty);
                // 库存主档设定判断
                TParm qtyParm = new TParm();
                qtyParm.setData("ORG_CODE", org_code);
                qtyParm.setData("ORDER_CODE", order_code);
                //System.out.println("ORG_CODE" + org_code);
                //System.out.println("ORDER_CODE" + order_code);
                getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
                //System.out.println("getQTY" + getQTY);
                if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
                    this.messageBox("药品" + order_code + "未设定库存主档信息");
                    return;
                }
            }
            inParm.setData("ORG_CODE", org_code);
            inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
            result = TIOM_AppServer.executeAction(
                "action.spc.INDPurorderAction", "onSavePurOrder", inParm);
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            this.messageBox("P0001");
            // 打印报表
            onPrint();
            return;
        }

        // 主项保存判断
        if (result == null || result.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        onClear();
        setValue("PURORDER_NO", parm.getValue("PURORDER_NO"));
        setValue("ORG_CODE", parm.getValue("ORG_CODE"));
        setValue("SUP_CODE", parm.getValue("SUP_CODE"));
        setValue("REASON_CODE", parm.getValue("REASON_CHN_DESC"));
        setValue("DESCRIPTION", parm.getValue("DESCRIPTION"));
        if (!"".equals(parm.getValue("RES_DELIVERY_DATE")) ||
            parm.getTimestamp("RES_DELIVERY_DATE").equals(tnull)) {
            setValue("RES_DELIVERY_DATE", parm.getValue("RES_DELIVERY_DATE"));
        }
        this.messageBox("P0001");
        onQuery();
    }

    /**
     * 打开采建表
     */
    public void onExport() {
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("请选择订购部门");
            return;
        }
        TParm parm = new TParm();
        parm.setData("ORG_CODE", getValueString("ORG_CODE"));
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        Object result = openDialog("%ROOT%\\config\\ind\\INDPurAdvice.x", parm);
        if (result != null) {
            TParm addParm = (TParm) result;
            if (addParm == null) {
                return;
            }
            //System.out.println("addParm:" + addParm);
            TTable table = getTable("TABLE_D");
            // 供应厂商
            getTextFormat("SUP_CODE").setValue(addParm.getValue("SUP_CODE", 0));
            // 计划单号
            this.setValue("PLAN_NO", addParm.getValue("PLAN_NO", 0));

            table.removeRow(0);
            for (int i = 0; i < addParm.getCount(); i++) {
                int row = table.addRow();
                // 填充DATESTORE
                // ORDER_CODE
                table.getDataStore().setItem(i, "ORDER_CODE",
                                             addParm.getValue("ORDER_CODE", i));
                // 进货单位
                String unit = addParm.getValue("PURCH_UNIT", i);
                table.setItem(i, "BILL_UNIT", unit);
                // 订购量
                table.setItem(i, "PURORDER_QTY", addParm.getValue("CHECK_QTY",
                    i));
                // 赠送量
                table.setItem(i, "GIFT_QTY", 0);
                // 订购单价
                table.setItem(i, "PURORDER_PRICE", addParm.getValue(
                    "STOCK_PRICE", i));
                // 订购金额
                table.setItem(i, "TOT_MONEY", addParm.getValue("ATM", i));
                table.setItem(i, "ACTUAL_QTY", 0);
                table.setItem(i, "QUALITY_DEDUCT_AMT", 0);
            }
            table.setDSValue();
            //getComboBox("ORG_CODE").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            action = "insert";
            // 计算总价钱
            this.setValue("SUM_MONEY", getSumMoney());
            //zhangyong20110517
            ( (TMenuItem) getComponent("export")).setEnabled(false);
        }
    }
    
    /**
     * 导出采购单为XML文件
     */
    public void onExportXml() {
    	if (getTable("TABLE_M").getSelectedRow() > -1) {
    		
    		//订购单号
    		String pur_order = getValueString("PURORDER_NO");
    		
    		//得到供应商代码
    		String  supCode = getValueString("SUP_CODE");
    		
    		//要导出来的细项总数
    	    TParm  parm =   new TParm(TJDODBTool.getInstance().select(INDSQL.getPurOrderDSqlByNo(pur_order)));
    		List list = new ArrayList();
    		
    		TParm  xmlParm = new TParm();
    		for(int i = 0 ; i < parm.getCount() ; i++ ){
    			TParm t = (TParm)parm.getRow(i);
//    			System.out.println("Parm:"+parm);
    			Map<String, String> map = new LinkedHashMap();
    			map.put("warehouse", "K932290201");
        		map.put("deliverycode", "932290201");
        		map.put("cstcode", t.getValue("SUP_CODE"));
        		map.put("goods", t.getValue("ORDER_CODE"));
        		map.put("goodname", t.getValue("ORDER_DESC"));
        		map.put("spec", t.getValue("SPECIFICATION"));
        		map.put("msunitno", t.getValue("UNIT_CHN_DESC"));
        		map.put("producer", t.getValue("MAN_CODE"));
        		map.put("billqty", t.getValue("PURORDER_QTY"));
        		map.put("prc", t.getValue("PURORDER_PRICE"));
        		
        		//订购单号
        		String  purorder_no = t.getValue("PURORDER_NO");
        		String seq = t.getValue("SEQ_NO");
        		map.put("purchaseid", purorder_no+"-"+seq);
        		list.add(map);
    		}
    		Document doc = ExportXmlUtil.createXml(list);
    		ExportXmlUtil.exeSaveXml(doc, pur_order+".xml");
    		
    	}else{
    		this.messageBox("请选择要生成的采购单!");
    		return ;
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
            if (getTable("TABLE_M").getSelectedRow() > -1) {
                if (this.messageBox("删除", "确定是否删除订单", 2) == 0) {
                    TTable table_D = getTable("TABLE_D");
                    table_D.removeRowAll();
                    TParm parm = new TParm();
                    String pur_order = getValueString("PURORDER_NO");
                    parm.setData("PURORDER_NO", pur_order);
                    String org_code = getValueString("ORG_CODE");
                    parm.setData("ORG_CODE", org_code);
                    // 查询所有细项
                    TParm result = new TParm();
                    result = IndPurorderDTool.getInstance().onQuery(parm);
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("删除失败");
                        return;
                    }
                    if (result.getCount() > 0) {
                        // 删除主项及细项
                        String order_code = "";
                        parm.setData("DELETE_SQL", table_D.getDataStore()
                                     .getUpdateSQL());
                        TParm getTRA = new TParm();
                        for (int i = 0; i < result.getCount(); i++) {
                            // 出入库数量转换率
                            order_code = result.getValue("ORDER_CODE", i);
                            getTRA = INDTool.getInstance().getTransunitByCode(
                                order_code);
                            if (getTRA.getCount() == 0
                                || getTRA.getErrCode() < 0) {
                                this.messageBox("药品" + order_code + "转换率错误");
                                return;
                            }
                            parm.setData("ORDER_CODE", i, order_code);
                            parm.setData("PURORDER_QTY", i, result.getDouble(
                                "PURORDER_QTY", i));
                            double s_qty = getTRA.getDouble("STOCK_QTY", 0);
                            parm.setData("STOCK_QTY", i, s_qty);
                            double d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                            parm.setData("DOSAGE_QTY", i, d_qty);
                        }
                    }
                    result = TIOM_AppServer.executeAction(
                        "action.spc.INDPurorderAction", "onDeleteM", parm);
                    // 删除判断
                    if (result == null || result.getErrCode() < 0) {
                        this.messageBox("删除失败");
                        return;
                    }
                    getTable("TABLE_M").removeRow(
                        getTable("TABLE_M").getSelectedRow());
                    this.messageBox("删除成功");
                    this.onClear();
                }
            }
            else {
                TParm inparm = new TParm();
                TTable table_D = getTable("TABLE_D");
                int row = table_D.getSelectedRow();
                String purorder_no = getValueString("PURORDER_NO");
                String org_code = getValueString("ORG_CODE");
                String order_code = table_D.getItemString(row, "ORDER_CODE");
                double seq_no = table_D.getDataStore().getItemDouble(row,
                    "SEQ_NO");
                inparm.setData("PURORDER_NO", purorder_no);
                inparm.setData("ORDER_CODE", order_code);
                inparm.setData("SEQ_NO", seq_no);
                // 查询细项
                TParm result = IndPurorderDTool.getInstance().onQuery(inparm);
                if (result == null || result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                if (result.getCount() == 0) {
                    table_D.removeRow(table_D.getSelectedRow());
                }
                else {
                    if (this.messageBox("删除", "确定是否删除订单细项", 2) == 0) {
                        double pur_qty = result.getDouble("PURORDER_QTY", 0);
                        inparm.setData("PURORDER_QTY", pur_qty);
                        inparm.setData("ORG_CODE", org_code);
                        table_D.removeRow(table_D.getSelectedRow());
                        String[] sql = table_D.getDataStore().getUpdateSQL();
                        inparm.setData("UPDATE_SQL", sql);
                        // 出入库数量转换率
                        TParm getTRA = INDTool.getInstance()
                            .getTransunitByCode(order_code);
                        if (getTRA.getCount() == 0 || getTRA.getErrCode() < 0) {
                            this.messageBox("药品" + order_code + "转换率错误");
                            return;
                        }
                        double s_qty = getTRA.getDouble("STOCK_QTY", 0);
                        inparm.setData("STOCK_QTY", s_qty);
                        double d_qty = getTRA.getDouble("DOSAGE_QTY", 0);
                        inparm.setData("DOSAGE_QTY", d_qty);
                        result = TIOM_AppServer.executeAction(
                            "action.spc.INDPurorderAction", "onDeleteD",
                            inparm);
                        // 删除判断
                        if (result == null || result.getErrCode() < 0) {
                            this.messageBox("删除失败");
                            return;
                        }
                        this.messageBox("删除成功");
                    }
                }
            }
        }
    }

    /**
     * 打印订购单
     */
    public void onPrint() {
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("PURORDER_NO"))) {
            this.messageBox("不存在订购单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
            date.setData("TITLE", "TEXT",
                         Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "药品订购单");
            date.setData("SUP_CODE", "TEXT",
                         "药品厂商: " +
                         this.getTextFormat("SUP_CODE").getText());
            date.setData("PUR_NO", "TEXT",
                         "订购单号: " + this.getValueString("PURORDER_NO"));
            date.setData("DATE", "TEXT",
                         "订购日期: " +
                         this.getValueString("PURORDER_DATE").substring(0, 10).
                         replace('-', '/'));
            // 表格数据
            TParm parm = new TParm();
            String order_code = "";
            double sum_money = 0;
            double amt = 0;
            for (int i = 0; i < table_d.getDataStore().rowCount(); i++) {
                if (!table_d.getDataStore().isActive(i)) {
                    continue;
                }
                if ("Y".equals(table_d.getItemString(i, "END_FLG"))) {
                    continue;
                }
                order_code = table_d.getDataStore().getItemString(i,
                    "ORDER_CODE");
                TParm inparm = new TParm(TJDODBTool.getInstance().select(INDSQL.
                    getOrderInfoByCode(order_code,
                                       this.getValueString("SUP_CODE"), "PUR")));
                if (inparm == null || inparm.getErrCode() < 0) {
                    this.messageBox("药品信息有误");
                    return;
                }
                parm.addData("ORDER_DESC", inparm.getValue("ORDER_DESC", 0));
                parm.addData("SPECIFICATION",
                             inparm.getValue("SPECIFICATION", 0));
                parm.addData("UNIT", inparm.getValue("UNIT_CHN_DESC", 0));
                parm.addData("MAN_CODE", inparm.getValue("MAN_CHN_DESC", 0));
//                parm.addData("QTY",
//                             StringTool.round(table_d.getItemDouble(i, "PURORDER_QTY"),
//                                              3));
                //luhai modify 2012-3-7 begin
//                this.messageBox(dfInt.format(table_d.getItemDouble(i, "PURORDER_QTY"))+"");
                parm.addData("QTY",
                		dfInt.format(table_d.getItemDouble(i, "PURORDER_QTY")));
              //luhai modify 2012-3-7 end
                parm.addData("PRICE", StringTool.round(table_d.getItemDouble(i,
                    "PURORDER_PRICE"), 4));
                amt = table_d.getItemDouble(i, "PURORDER_QTY") *
                    table_d.getItemDouble(i, "PURORDER_PRICE");
                parm.addData("AMT", StringTool.round(amt, 2));
                sum_money += amt;
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
            parm.addData("SYSTEM", "COLUMNS", "PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT");
            parm.addData("SYSTEM", "COLUMNS", "MAN_CODE");
            //System.out.println("PARM---" + parm);
            date.setData("TABLE", parm.getData());
            // 表尾数据
            date.setData("CHECK", "TEXT", "审核： ");
            date.setData("USER", "TEXT", "制表人: " + Operator.getName());
            date.setData("TOT", "TEXT",
                         "总金额：" + StringTool.round(sum_money, 2));

            // 调用打印方法
            this.openPrintWindow("%ROOT%\\config\\prt\\IND\\Purorder.jhw",
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
            setValue("PURORDER_DATE", table.getItemTimestamp(row,
                "PURORDER_DATE"));
            setValue("PURORDER_NO", table.getItemString(row, "PURORDER_NO"));
            setValue("ORG_CODE", table.getItemString(row, "ORG_CODE"));
            setValue("PLAN_NO", table.getItemString(row, "PLAN_NO"));
            setValue("SUP_CODE", table.getItemString(row, "SUP_CODE"));
            setValue("RES_DELIVERY_DATE", table.getItemTimestamp(row,
                "RES_DELIVERY_DATE"));
            setValue("REASON_CODE", table.getItemString(row, "REASON_CHN_DESC"));
            setValue("DESCRIPTION", table.getItemString(row, "DESCRIPTION"));
            // 设定页面状态
            getTextField("PURORDER_NO").setEnabled(false);
            getTextFormat("SUP_CODE").setEnabled(false);
            ( (TMenuItem) getComponent("delete")).setEnabled(true);
            ( (TMenuItem) getComponent("export")).setEnabled(true);
            action = "updateM";

            // 明细信息
            TTable table_D = getTable("TABLE_D");
            table_D.removeRowAll();
            table_D.setSelectionMode(0);
            TDS tds = new TDS();
            tds.setSQL(INDSQL.getPurOrderDByNo(getValueString("PURORDER_NO")));
            tds.retrieve();
            if (tds.rowCount() == 0) {
                //this.messageBox("没有订购明细");
                seq = 1;
            }
            else {
                seq = getMaxSeq(tds, "SEQ_NO");
            }

            // 观察者
            IndPurOrderObserver obser = new IndPurOrderObserver();
            tds.addObserver(obser);
            table_D.setDataStore(tds);
            table_D.setDSValue();

            // 计算总价钱
            double sum = getSumMoney();
            this.setValue("SUM_MONEY", sum);

            // 添加一行细项
            onAddRow();
        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        TTable table = getTable("TABLE_D");
        int row = table.getSelectedRow();
        if (row != -1) {
            action = "updateD";
            // 主项信息
            TTable table_M = getTable("TABLE_M");
            table_M.setSelectionMode(0);
            // 取得SYS_FEE信息，放置在状态栏上
            /*
             String order_code = table.getDataStore().getItemString(table.
                getSelectedRow(), "ORDER_CODE");
                         if (!"".equals(order_code)) {
                this.setSysStatus(order_code);
                         }
                         else {
                callFunction("UI|setSysStatus", "");
                         }
             */
        }
    }

    /**
     * 当TABLE创建编辑控件时长期
     *
     * @param com
     * @param row
     * @param column
     */
    public void onCreateEditComoponentUD(Component com, int row, int column) {
        if (column != 1)
            return;
        if (! (com instanceof TTextField))
            return;
        TParm parm = new TParm();
        parm.setData("SUP_CODE", getValueString("SUP_CODE"));
        TTextField textFilter = (TTextField) com;
        textFilter.onInit();
        // 设置弹出菜单
        textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
            "%ROOT%\\config\\ind\\INDSupOrder.x"), parm);
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
        TTable table = getTable("TABLE_D");
        table.acceptText();
        String order_code = parm.getValue("ORDER_CODE");
        if (!StringUtil.isNullString(order_code))
            table.getDataStore().setItem(table.getSelectedRow(), "ORDER_CODE",
                                         order_code);
        String specification = parm.getValue("SPECIFICATION");
        String order_desc = parm.getValue("ORDER_DESC");
        if (!StringUtil.isNullString(specification))
            table.setItem(table.getSelectedRow(), "SPECIFICATION",
                          specification);
        String purch_unit = parm.getValue("PURCH_UNIT");
        if (!StringUtil.isNullString(purch_unit))
            table.setItem(table.getSelectedRow(), "BILL_UNIT", purch_unit);
        
        String supCode = this.getValueString("SUP_CODE");

        double price = parm.getDouble("CONTRACT_PRICE");
        table.setItem(table.getSelectedRow(), "PURORDER_PRICE", price);
        table.getDataStore().setActive(table.getSelectedRow(), true);
        // 检查库存主档
        TParm qtyParm = new TParm();
        qtyParm.setData("ORG_CODE", this.getValueString("ORG_CODE"));
        qtyParm.setData("ORDER_CODE", order_code);
        TParm getQTY = IndStockMTool.getInstance().onQuery(qtyParm);
        //System.out.println("getQTY" + getQTY);
        if (getQTY.getCount() <= 0 || getQTY.getErrCode() < 0) {
            this.messageBox("药品:" + order_desc + "(" + order_code + ") 未设定库存主档信息");
            onAddRow();
            table.removeRow(table.getSelectedRow());
            return;
        }
        // 判断是否有重复数据
        for (int i = 0; i < table.getDataStore().rowCount(); i++) {
            if (i == table.getSelectedRow()) {
                continue;
            }
            if (order_code.equals(table.getDataStore().getItemData(i, "ORDER_CODE"))) {
                this.messageBox("药品:" + order_desc + "(" + order_code + ") 已存在");
                onAddRow();
                table.removeRow(table.getSelectedRow());
                return;
            }
        }
        onAddRow();
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
                if (!table.getDataStore().isActive(i)) {
                    continue;
                }
                table.getDataStore().setItem(i, "UPDATE_FLG", "2");
                this.setValue("SUM_MONEY", 0);
            }
        }
        else {
            for (int i = 0; i < table.getRowCount(); i++) {
                if (!table.getDataStore().isActive(i)) {
                    continue;
                }
                table.getDataStore().setItem(i, "UPDATE_FLG", "0");
                this.setValue("SUM_MONEY", getSumMoney());
            }
        }
        table.setDSValue();
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
        String columnName = node.getTable().getDataStoreColumnName(
            node.getColumn());
        int row = node.getRow();
        if ("PURORDER_QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("订购数量不能小于或等于0");
                return true;
            }
            else {
                // 计算总价钱
                if (!"2".equals(node.getTable().getDataStore().getItemString(
                    row, "UPDATE_FLG"))) {
                    node.getTable().getDataStore().setItem(row, "PURORDER_QTY",
                        qty);
                    this.setValue("SUM_MONEY", getSumMoney());
                }
            }
        }
        if ("GIFT_QTY".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty < 0) {
                this.messageBox("赠送数量不能小于0");
                return true;
            }
        }
        if ("PURORDER_PRICE".equals(columnName)) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("单价不能小于或等于0");
                return true;
            }
            else {
                // 计算总价钱
                if (!"2".equals(node.getTable().getDataStore().getItemString(
                    row, "UPDATE_FLG"))) {
                    node.getTable().getDataStore().setItem(row,
                        "PURORDER_PRICE", qty);
                    this.setValue("SUM_MONEY", getSumMoney());
                }
            }
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
        if (column == 0) {
            //this.messageBox("");
            String flg = tableDown.getDataStore().getItemString(tableDown.
                getSelectedRow(), "UPDATE_FLG");
            double amt = tableDown.getItemDouble(tableDown.getSelectedRow(),
                                                 "PURORDER_QTY") *
                tableDown.getItemDouble(tableDown.getSelectedRow(),
                                        "PURORDER_PRICE");
            double sum_amt = this.getValueDouble("SUM_MONEY");
            if ("2".equals(flg)) {
                sum_amt = sum_amt - amt;
            }
            else {
                sum_amt = sum_amt + amt;
            }
            this.setValue("SUM_MONEY", sum_amt);
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        /**
         * 权限控制
         * 权限1:一般个人无赠与权限,只显示自已所属科室;无赠与录入功能
         * 权限2:一般个人赠与权限,只显示自已所属科室;包含赠与录入功能
         * 权限9:最大权限,显示全院药库部门包含赠与录入功能
         */
        // 赠与权限
        if (!this.getPopedem("giftEnabled")) {
            TTable table_d = getTable("TABLE_D");
            table_d.setLockColumns("2,3,5,7,8,9");
            gift_flg = false;
        }
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

        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("PURORDER_DATE", date);
        ( (TMenuItem) getComponent("delete")).setEnabled(false);
        ( (TMenuItem) getComponent("export")).setEnabled(false);
        action = "insertM";

        // 注册激发INDSupOrder弹出的事件
        getTable("TABLE_D").addEventListener(TTableEvent.CREATE_EDIT_COMPONENT,
                                             this, "onCreateEditComoponentUD");
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
    }

    /**
     * 添加一行细项
     */
    private void onAddRow() {
        // 有未编辑行时返回
        if (!this.isNewRow())
            return;
        TTable table_D = getTable("TABLE_D");
        int row = table_D.addRow();
        TParm parm = new TParm();
        parm.setData("ACTIVE", false);
        table_D.getDataStore().setActive(row, false);
    }

    /**
     * 是否有未编辑行
     *
     * @return boolean
     */
    private boolean isNewRow() {
        Boolean falg = false;
        TParm parmBuff = getTable("TABLE_D").getDataStore().getBuffer(
            getTable("TABLE_D").getDataStore().PRIMARY);
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
        if ("".equals(getValueString("ORG_CODE"))) {
            this.messageBox("订购部门不能为空");
            return false;
        }
        if ("".equals(getValueString("SUP_CODE"))) {
            this.messageBox("供应厂商不能为空");
            return false;
        }
        if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
            if (TypeTool.getTimestamp(getValue("RES_DELIVERY_DATE")).compareTo(
                TypeTool.getTimestamp(getValue("PURORDER_DATE"))) < 0) {
                this.messageBox("交货日期不能早于订购日期");
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
        TTable table_D = getTable("TABLE_D");
        table_D.acceptText();
        for (int i = 0; i < table_D.getRowCount(); i++) {
            if (!table_D.getDataStore().isActive(i)) {
                continue;
            }
            if (table_D.getItemDouble(i, "PURORDER_QTY") <= 0) {
                this.messageBox("订购数量不能小于或等于0");
                return false;
            }
            if (table_D.getItemDouble(i, "GIFT_QTY") < 0) {
                this.messageBox("赠送数量不能小于0");
                return false;
            }
            if (table_D.getItemDouble(i, "PURORDER_PRICE") <= 0) {
                this.messageBox("单价不能小于或等于0");
                return false;
            }
        }
        return true;
    }

    /**
     * 计算总金额
     *
     * @return
     */
    private double getSumMoney() {
        TTable table = getTable("TABLE_D");
        TDataStore ds = table.getDataStore();
        table.acceptText();
        double sum = 0;
        for (int i = 0; i < table.getRowCount(); i++) {
            if (!"2".equals(ds.getItemString(i, "UPDATE_FLG"))) {
                sum += table.getItemDouble(i, "PURORDER_QTY")
                    * table.getItemDouble(i, "PURORDER_PRICE");
            }
        }
        return StringTool.round(sum, 2);
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
