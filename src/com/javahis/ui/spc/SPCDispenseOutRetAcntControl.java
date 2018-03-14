package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import jdo.spc.INDSQL;
import jdo.spc.INDTool;
import jdo.spc.IndSysParmTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
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
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 出库管理
 * </p>
 *
 * <p>
 * Description: 退库出库 
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
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class SPCDispenseOutRetAcntControl  extends TControl {

    // 操作标记
    private String action;

    // 主项表格
    private TTable table_m;

    // 细项表格
    private TTable table_d;

    // 主项表格选中行
    private int row_m;

    // 细项表格选中行
    private int row_d;

    // 细项序号
    private int seq;

    // 申请单号
    private String request_no;

    // 申请部门
    private String app_org;

    // 单据类型
    private String request_type;

    // 返回结果集
    private TParm resultParm;

    // 页面上翻数据列表
    private String[] pageItem = {
        "REQTYPE_CODE", "REQUEST_NO", "APP_ORG_CODE",
        "TO_ORG_CODE", "REASON_CHN_DESC", "REQUEST_DATE", "DISPENSE_NO",
        "DESCRIPTION", "URGENT_FLG"};

    // 使用单位
    private String u_type;

    // 出库部门
    private String out_org_code;

    // 入库部门
    private String in_org_code;

    // 是否出库
    private boolean out_flg;

    // 是否入库
    private boolean in_flg;

    // 出库单号
    private String dispense_no;

    // 全院药库部门作业单据
    private boolean request_all_flg = true;

    java.text.DecimalFormat df4 = new java.text.DecimalFormat(
        "##########0.0000");

    /**
     * 初始化方法
     */
    public void onInit() {
        // 添加侦听事件
        addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
                         "onTableDChangeValue");
        // 给TABLEDEPT中的CHECKBOX添加侦听事件
        callFunction("UI|TABLE_D|addEventListener",
                     TTableEvent.CHECK_BOX_CLICKED, this,
                     "onTableCheckBoxClicked");
        // 初始画面数据
        initPage();
        
        //设置药品种
        setTypesOfDrug();
    }
    
    
    /**
     * 设置药品种类
     */
    public void setTypesOfDrug(){
    	TParm parm = getSysParm();
    	if(parm.getCount() > 0 ){
    		parm = parm.getRow(0);
    	}
    	String isSeparateReq = parm.getValue("IS_SEPARATE_REQ");
    	if(isSeparateReq != null && !isSeparateReq.equals("")){
    		if(isSeparateReq.equals("Y")){
    			getRadioButton("G_DRUGS").setSelected(true);
    		}else{
    			getRadioButton("ALL").setSelected(true);
    		}
    	}
    }

    /**
     * 查询方法
     */
    public void onQuery() {
        TParm result = new TParm();
        result = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                              "onQueryOutMAcnt", onQueryParm());//wanglong modify 20150202
        // 全院药库部门作业单据
        if (!request_all_flg) {
            TParm parm = new TParm(TJDODBTool.getInstance().select(SYSSQL.
                getOperatorDept(Operator.getID())));
            String dept_code = "";
            for (int i = result.getCount("REQTYPE_CODE") - 1; i >= 0; i--) {
                boolean flg = true;
                for (int j = 0; j < parm.getCount("DEPT_CODE"); j++) {
                    dept_code = parm.getValue("DEPT_CODE", j);
                    if ("DEP".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "TEC".equals(result.getValue("REQTYPE_CODE", i)) ||
                        "THI".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("TO_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                    else if ("GIF".equals(result.getValue("REQTYPE_CODE", i)) ||
                             "RET".equals(result.getValue("REQTYPE_CODE", i))) {
                        if (dept_code.equals(result.getValue("APP_ORG_CODE", i))) {
                            flg = false;
                            break;
                        }
                        else {
                            flg = true;
                        }
                    }
                }
                if (flg) {
                    result.removeRow(i);
                }
            }
        }
        if (result.getCount() <= 0 || result.getCount("REQUEST_NO") == 0) {
            this.messageBox("无查询结果");
            return;
        }
        table_m.setParmValue(result);
    }

    /**
     * 清空方法
     */
    public void onClear() {
        getRadioButton("UPDATE_FLG_B").setSelected(true);
        // 清空画面内容
        String clearString =
            "REQUEST_NO;APP_ORG_CODE;REQTYPE_CODE;DISPENSE_NO;"
            + "REQUEST_DATE;TO_ORG_CODE;REASON_CHN_DESC;START_DATE;END_DATE;"
            + "DESCRIPTION;URGENT_FLG;SELECT_ALL;"
            + "SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;PRICE_DIFFERENCE";
        clearValue(clearString);
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        // 初始化页面状态及数据
        table_m.setSelectionMode(0);
        table_m.removeRowAll();
        table_d.setSelectionMode(0);
        table_d.removeRowAll();
        row_m = -1;
        row_d = -1;
        getTextField("REQUEST_NO").setEnabled(true);
        getTextFormat("APP_ORG_CODE").setEnabled(true);
        setValue("REQTYPE_CODE", "RET");
        getComboBox("REQTYPE_CODE").setEnabled(false);
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
        ( (TMenuItem) getComponent("save")).setEnabled(true);
        resultParm = new TParm();
        seq = 1;
    }

    /**
     * 保存方法
     */
    public void onSave() {
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            /** 新增保存(出库单主项及细项) */
            // 药库参数信息
            TParm sysParm = getSysParm();
            // 出入库作业状态判断(1-入库确认；2-出库即入库)
            String dis_check = getDisCheckFlg(sysParm);
            // 是否回写购入价格
            String reuprice_flg = sysParm.getValue("REUPRICE_FLG", 0);
            
            if ("1".equals(dis_check)) {
                // 出库部门库存是否可异动
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("出库部门处于盘点状态，库存不可异动");
                    return;
                }
                // 判断是否有库存，并将没有库存的药品取消勾选
                String message = checkStockQty();
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                }
                // 出库在途作业/耗损、其它出库作业(出库即入库)
                if (getDispenseOutOn(out_org_code)) {
                    // 打印出库单
                    this.onPrint();
                    this.onClear();
                }
            }
            else if ("2".equals(dis_check)) {
                // 出库部门库存是否可异动
                if (!getOrgBatchFlg(out_org_code)) {
                    this.messageBox("出库部门处于盘点状态，库存不可异动");
                    return;
                }
                // 入库部门库存是否可异动
                if (!"".equals(in_org_code) && !getOrgBatchFlg(in_org_code)) {
                    this.messageBox("入库部门处于盘点状态，库存不可异动");
                    return;
                }
                // 判断是否有库存，并将没有库存的药品取消勾选
                String message = checkStockQty();
                if (!"".equals(message) && message.length() > 0) {
                    this.messageBox(message);
                }
                // 出库即入库作业(出入库部门均不为空)
                getDispenseOutIn(out_org_code, in_org_code, reuprice_flg,
                                 out_flg, in_flg);
            }
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            /** 在途更新 */
            if (row_m != -1) {
                // 更新主项
                getUpdateDispenseMOutOn();
            }
            else {
                // 更新细项
                this.messageBox("申请单已出库，不可修改");
            }
        }
        else {
            /** 完成更新 */
            this.messageBox("单据已完成，不可修改");
        }
    }

    /**
     * 取消出库方法
     */
    public void onCancle() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("没有取消单据");
            return;
        }
        if (this.messageBox("提示", "是否取消出库", 2) == 0) {
            String dispense_no = this.getValueString("DISPENSE_NO");
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("DISPENSE_NO", dispense_no);
            parm.setData("ORG_CODE", out_org_code);
            parm.setData("REQTYPE_CODE", this.getValueString("REQTYPE_CODE"));
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            parm.setData("PARM_D", table_d.getParmValue().getData());
            TParm result = TIOM_AppServer.executeAction(
                "action.spc.INDDispenseAction", "onUpdateDipenseCancelAcnt", parm);//wanglong modify 20150202
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("取消失败");
                return;
            }
            this.messageBox("取消成功");
            this.onClear();
        }
    }

    /**
     * 终止单据方法
     */
    public void onStop() {
        if (table_m.getSelectedRow() < 0) {
            this.messageBox("没有终止单据");
            return;
        }
        if (this.messageBox("提示", "是否终止单据", 2) == 0) {
            String request_no = this.getValueString("REQUEST_NO");
            TParm parm = new TParm();
            parm.setData("REQUEST_NO", request_no);
            parm.setData("UPDATE_FLG", "2");
            Timestamp date = SystemTool.getInstance().getDate();
            parm.setData("OPT_USER", Operator.getID());
            parm.setData("OPT_DATE", date);
            parm.setData("OPT_TERM", Operator.getIP());
            TParm result = TIOM_AppServer.executeAction(
                "action.spc.INDRequestAction", "onUpdateFlgAcnt", parm);//wanglong modify 20150202
            if (result == null || result.getErrCode() < 0) {
                this.messageBox("终止失败");
                return;
            }
            this.messageBox("终止成功");
            this.onClear();
        }
    }

    /**
     * 打印出库单
     */
    public void onPrint() {
        Timestamp datetime = SystemTool.getInstance().getDate();
        TTable table_d = getTable("TABLE_D");
        if ("".equals(this.getValueString("DISPENSE_NO"))) {
            this.messageBox("不存在出库单");
            return;
        }
        if (table_d.getRowCount() > 0) {
            // 打印数据
            TParm date = new TParm();
            // 表头数据
            date.setData("TITLE", "TEXT", Manager.getOrganization().
                         getHospitalCHNFullName(Operator.getRegion()) +
                         "出库单");
            //===============pangben modify 20110607 添加急件注记
            if (null !=
                getValue("URGENT_FLG") && getValue("URGENT_FLG").equals("Y"))
                date.setData("URGENT", "TEXT", "急");
            else
                date.setData("URGENT", "TEXT", "");
            //===============pangben modify 20110607 stop
            date.setData("DISP_NO", "TEXT",
                         "出库单号: " + this.getValueString("DISPENSE_NO"));
            date.setData("REQ_NO", "TEXT",
                         "申请单号: " + this.getValueString("REQUEST_NO"));
            date.setData("OUT_DATE", "TEXT",
                         "出库日期: " + this.getValueString("DISPENSE_DATE").
                         substring(0, 10).replace('-', '/'));
            date.setData("REQ_TYPE", "TEXT", "申请类别: " +
                         this.getComboBox("REQTYPE_CODE").getSelectedName());
            date.setData("ORG_CODE_APP", "TEXT", "申请部门: " +
                         this.getTextFormat("APP_ORG_CODE").getText());
            date.setData("ORG_CODE_FROM", "TEXT", "接受部门: " +
                         this.getComboBox("TO_ORG_CODE").getSelectedName());
            date.setData("DATE", "TEXT",
                         "制表日期: " +
                         datetime.toString().substring(0, 10).replace('-', '/'));

            // 表格数据
            TParm parm = new TParm();
            String order_code = "";
            String order_desc = "";
            double qty = 0;
            double sum_retail_price = 0;
            //luhai 2012-1-22 加入采购价总和
            double sum_verifyin_price = 0;

            String sql =
                    "SELECT A.ORDER_CODE, CASE WHEN B.GOODS_DESC IS NULL "
                    + " THEN B.ORDER_DESC ELSE B.ORDER_DESC  "
                    + "  END AS ORDER_DESC, "
                    + " B.SPECIFICATION, C.UNIT_CHN_DESC, "
                    + " A.QTY AS OUT_QTY, A.BATCH_NO, A.VALID_DATE,"//ADD VERIFYIN_PRICE
                    + " CASE  WHEN A.UNIT_CODE = D.STOCK_UNIT "
				    + "       THEN A.RETAIL_PRICE * D.DOSAGE_QTY  "
				    + "       WHEN A.UNIT_CODE = D.DOSAGE_UNIT "
				    + "       THEN A.RETAIL_PRICE "
				    + "       ELSE A.RETAIL_PRICE "
				    + "       END  RETAIL_PRICE, "
                    + "  A.QTY AS OUT_QTY,"
                    + "  CASE WHEN A.UNIT_CODE=D.STOCK_UNIT " 
                    + "       THEN A.INVENT_PRICE "
                    + "       WHEN A.UNIT_CODE=D.DOSAGE_UNIT THEN A.VERIFYIN_PRICE ELSE A.VERIFYIN_PRICE  END  VERIFYIN_PRICE "   //ADD VERIFYIN_PRICE
                    + " FROM SPC_DISPENSED A, SYS_FEE B, SYS_UNIT C ,PHA_TRANSUNIT D "//wanglong modfy 20150202
                    + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                    + " AND A.UNIT_CODE = C.UNIT_CODE "
                    + " AND A.ORDER_CODE=D.ORDER_CODE "
                    + " AND A.DISPENSE_NO = '" +
                    this.getValueString("DISPENSE_NO") + "' "
                    + " ORDER BY A.SEQ_NO";
              //luhai modify 出库单删除商品名 begin 
               
                TParm printData = new TParm(TJDODBTool.getInstance().select(sql));
                
                for (int i = 0; i < printData.getCount("ORDER_CODE"); i++) {
                    parm.addData("ORDER_DESC",
                                 printData.getValue("ORDER_DESC", i));
                    parm.addData("SPECIFICATION",
                                 printData.getValue("SPECIFICATION", i));
                    parm.addData("UNIT", printData.getValue("UNIT_CHN_DESC", i));
                    parm.addData("UNIT_PRICE",
                                 df4.format(printData.getDouble("RETAIL_PRICE",
                        i)));
                    parm.addData("VERIFYIN_PRICE",
                    		df4.format(printData.getDouble("VERIFYIN_PRICE",
                    				i)));
                    qty = printData.getDouble("OUT_QTY", i);
                    parm.addData("QTY",StringTool.round(qty,3));
                    parm.addData("AMT", StringTool.round(printData.getDouble(
                        "RETAIL_PRICE", i) * qty, 2));
                    parm.addData("AMT_VERIFYIN", StringTool.round(printData.getDouble(
                    		"VERIFYIN_PRICE", i) * qty, 2));
                    sum_retail_price += printData.getDouble("RETAIL_PRICE", i) *
                        qty;
                    sum_verifyin_price += printData.getDouble("VERIFYIN_PRICE", i) *qty;
                    parm.addData("BATCH_NO", printData.getValue("BATCH_NO", i));
                    parm.addData("VALID_DATE", StringTool.getString(printData.
                        getTimestamp("VALID_DATE", i), "yyyy/MM/dd"));
                }

            
            //luhai 2012-1-22 modify end
            if (parm.getCount("ORDER_DESC") <= 0) {
                this.messageBox("没有打印数据");
                return;
            }
            //luhai 2012-1-22 modify 加入采购金额，采购单价 begin
            parm.setCount(parm.getCount("ORDER_DESC"));
            parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
            parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
            parm.addData("SYSTEM", "COLUMNS", "UNIT");
            parm.addData("SYSTEM", "COLUMNS", "QTY");
            parm.addData("SYSTEM", "COLUMNS", "VERIFYIN_PRICE");
            parm.addData("SYSTEM", "COLUMNS", "AMT_VERIFYIN");
            parm.addData("SYSTEM", "COLUMNS", "BATCH_NO");
            parm.addData("SYSTEM", "COLUMNS", "VALID_DATE");	

            System.out.println("parm---:"+parm);
            date.setData("TABLE", parm.getData());

            // 表尾数据
            //luhai 2012-1-22 加入合计atm
            String atm = StringTool.round(sum_retail_price, 2)+"";
            String verifyinAtm=StringTool.round(sum_verifyin_price, 2)+"";
//            date.setData("TOT_CHN", "TEXT",
//                         "合计(大写): " + StringUtil.getInstance().numberToWord(atm));
            date.setData("BAR_CODE", "TEXT",this.getValueString("DISPENSE_NO"));
            date.setData("ATM", "TEXT",atm);
            date.setData("VERIFYIN_ATM", "TEXT",verifyinAtm);
            date.setData("VERIFYIN_ATM_DESC", "TEXT",StringUtil.getInstance().numberToWord(Double.parseDouble(verifyinAtm)));
            date.setData("TOT", "TEXT", "合计: ");
            date.setData("USER", "TEXT", "制表人: " + Operator.getName());
            date.setData("BAR_CODE", "TEXT", this.getValueString("DISPENSE_NO"));
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\spc\\DispenseOut.jhw", date);
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
        row_m = table_m.getSelectedRow();
        if (row_m != -1) {
            // 主项表格选中行数据上翻
            getTableSelectValue(table_m, row_m, pageItem);
            // 出库时间
            if (table_m.getItemData(row_m, "DISPENSE_DATE") != null) {
                this.setValue("DISPENSE_DATE", table_m.getItemData(row_m,
                    "DISPENSE_DATE"));
            }
            // 设定页面状态
            getComboBox("REQTYPE_CODE").setEnabled(false);
            getTextFormat("APP_ORG_CODE").setEnabled(false);
            getTextField("REQUEST_NO").setEnabled(false);
            // 单据类别
            request_type = getValueString("REQTYPE_CODE");
            // 申请单号
            request_no = getValueString("REQUEST_NO");
            // 出库单号
            dispense_no = getValueString("DISPENSE_NO");
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                // 明细信息
                getTableDInfo(request_no);
            }
            else {
                // 明细信息
                getTableDInfo2(dispense_no);
            }
            table_d.setSelectionMode(0);
            row_d = -1;

            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
    }

    /**
     * 明细表格(TABLE_D)单击事件
     */
    public void onTableDClicked() {
        row_d = table_d.getSelectedRow();
        if (row_d != -1) {
            table_m.setSelectionMode(0);
            row_m = -1;
            // 取得SYS_FEE信息，放置在状态栏上
            /*
             String order_code = table_d.getParmValue().getValue("ORDER_CODE",
                table_d.getSelectedRow());
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
     * 全选复选框选中事件
     */
    public void onCheckSelectAll() {
        table_d.acceptText();
        if (table_d.getRowCount() < 0) {
            getCheckBox("SELECT_ALL").setSelected(false);
            return;
        }
        if (getCheckBox("SELECT_ALL").isSelected()) {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if (!CheckDataD()) {
                    getCheckBox("SELECT_ALL").setSelected(false);
                    return;
                }
                table_d.setItem(i, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
        else {
            for (int i = 0; i < table_d.getRowCount(); i++) {
                table_d.setItem(i, "SELECT_FLG", false);
            }
            setValue("SUM_RETAIL_PRICE", 0);
            setValue("SUM_VERIFYIN_PRICE", 0);
            setValue("PRICE_DIFFERENCE", 0);
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
        int column = node.getColumn();
        if (column == 6) {
            double qty = TypeTool.getDouble(node.getValue());
            if (qty <= 0) {
                this.messageBox("出库数数量不能小于或等于0");
                return true;
            }
            double stock_qty = table_d.getItemDouble(row_d, "STOCK_QTY");
            if (qty > stock_qty) {
                this.messageBox("出库数数量不能大于库存数量");
                return true;
            }
            double dep_qty = table_d.getItemDouble(row_d, "QTY");
            if (qty > dep_qty) {
                this.messageBox("出库数数量不能大于申请数量");
                return true;
            }
            double amt1 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "STOCK_PRICE"), 2);
            double amt2 = StringTool.round(qty * table_d.getItemDouble(row_d,
                "RETAIL_PRICE"), 2);
            table_d.setItem(row_d, "STOCK_ATM", amt1);
            table_d.setItem(row_d, "RETAIL_ATM", amt2);
            table_d.setItem(row_d, "DIFF_ATM", amt2 - amt1);
            return false;
        }
        if (column == 8) {
            double price = TypeTool.getDouble(node.getValue());
            if (price <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return true;
            }
            double out_qty = table_d.getItemDouble(row_d, "OUT_QTY");
            table_d.setItem(row_d, "STOCK_ATM", StringTool.round(price
                * out_qty, 2));
            double atm = table_d.getItemDouble(row_d, "RETAIL_ATM");
            table_d.setItem(row_d, "DIFF_ATM", StringTool.round(atm - price
                * out_qty, 2));
            return false;
        }
        return true;
    }

    /**
     * 表格(TABLE)复选框改变事件
     *
     * @param obj
     */
    public void onTableCheckBoxClicked(Object obj) {
        table_d.acceptText();
        // 获得选中的列
        int column = table_d.getSelectedColumn();
        if (column == 0) {
            if ("Y".equals(table_d.getItemString(row_d, column))) {
                table_d.setItem(row_d, "SELECT_FLG", false);
            }
            else {
                table_d.setItem(row_d, "SELECT_FLG", true);
            }
            setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
            setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
            setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
                - getSumRegMoney(), 2));
        }
    }

    /**
     * 出库状态改变事件
     */
    public void onChangeSelectAction() {
        if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_A").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
           
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(true);
            getTextField("DISPENSE_NO").setEnabled(true);
            ( (TMenuItem) getComponent("stop")).setEnabled(false);
            ( (TMenuItem) getComponent("save")).setEnabled(false);
        }
        else {
            onClear();
            getRadioButton("UPDATE_FLG_C").setSelected(false);
            ( (TMenuItem) getComponent("save")).setEnabled(true);
        }
    }

    /**
     * 初始画面数据
     */
    private void initPage() {
        if (!this.getPopedem("requestAll")) {
            request_all_flg = false;
        }

        // 初始化验收时间
        // 出库日期
        Timestamp date = SystemTool.getInstance().getDate();
        // 初始化查询区间
        this.setValue("END_DATE",
                      date.toString().substring(0, 10).replace('-', '/') +
                      " 23:59:59");
        this.setValue("START_DATE",
                      StringTool.rollDate(date, -7).toString().substring(0, 10).
                      replace('-', '/') + " 00:00:00");
        setValue("DISPENSE_DATE", date);
        // 初始化TABLE
        table_m = getTable("TABLE_M");
        table_d = getTable("TABLE_D");
        row_m = -1;
        row_d = -1;
        seq = 1;
        ( (TMenuItem) getComponent("stop")).setEnabled(true);
        resultParm = new TParm();
        setValue("REQTYPE_CODE", "RET");
        getComboBox("REQTYPE_CODE").setEnabled(false);
    }

    /**
     * 查询条件参数
     *
     * @return
     */
    private TParm onQueryParm() {
        // 传入参数集
        TParm parm = new TParm();
        // 申请单号
        if (!"".equals(getValueString("REQUEST_NO"))) {
            parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
        }
        // 申请部门
        if (!"".equals(getValueString("APP_ORG_CODE"))) {
            parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
        }
        // 单号类别
        if (!"".equals(getValueString("REQTYPE_CODE"))) {
            parm.setData("REQTYPE_CODE", getValueString("REQTYPE_CODE"));
        }
        // 出库单号
        if (!"".equals(getValueString("DISPENSE_NO"))) {
            parm.setData("DISPENSE_NO", getValueString("DISPENSE_NO"));
        }
        // 查询区间
        if (!"".equals(getValueString("START_DATE"))
            && !"".equals(getValueString("END_DATE"))) {
            parm.setData("START_DATE", getValue("START_DATE"));
            parm.setData("END_DATE", getValue("END_DATE"));
        }
        //zhangyong20110517
        parm.setData("REGION_CODE", Operator.getRegion());

        if (parm == null) {
            return parm;
        }
        // 出库状态
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            // 未出库
            parm.setData("STATUS", "B");
        }
        else if (getRadioButton("UPDATE_FLG_A").isSelected()) {
            // 完成
            parm.setData("STATUS", "A");
        }
        else {
            // 途中
            parm.setData("STATUS", "C");
        }
        
        //药品种类--普药:1,麻精：2
        if(getRadioButton("G_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parm.setData("DRUG_CATEGORY","2");
        }else {
        	parm.setData("DRUG_CATEGORY","3");
        }
        
        return parm;
    }

    /**
     * 根据申请单号取得细项信息并显示在细项表格上
     *
     * @param req_no
     */
    private void getTableDInfo(String req_no) {
        // 取得未完成的细项信息
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getOutRetRequestDInfoAcnt(req_no, "3")));//wanglong modify 20150202
//        System.out.println("sql==="+INDSQL.getOutRequestDInfo(req_no, "3"));
//        System.out.println("result==" + result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;
        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }
        else if ("EXM".equals(request_type) || "COS".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = false;
        }

        // 填充结果集
        resultParm = result;
        // 填充TABLE_D
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_d.setParmValue(result);
    }

    /**
     * 根据出库单号取得细项信息并显示在细项表格上
     *
     * @param dispense_no
     */
    private void getTableDInfo2(String dispense_no) {
    	//===zhangp 20120710 start
    	String sqlGetIndDispenseDByNo = 
    		"SELECT DISTINCT UNIT_TYPE FROM SPC_DISPENSEM WHERE DISPENSE_NO = '" + dispense_no + "'";//wanglong add 20150202
/*    	TParm result = new TParm(TJDODBTool.getInstance().select(sqlGetIndDispenseDByNo));
        if (result.getCount("UNIT_TYPE") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
    	if(result.getValue("UNIT_TYPE", 0).equals("1")){
    		sqlGetIndDispenseDByNo = 
    			" SELECT CASE" +
    			" WHEN B.GOODS_DESC IS NULL" +
    			" THEN B.ORDER_DESC" +
    			" ELSE B.ORDER_DESC || '(' || B.GOODS_DESC || ')'" +
    			" END AS ORDER_DESC," +
    			" B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE," +
    			" A.VERIFYIN_PRICE AS STOCK_PRICE, A.RETAIL_PRICE AS RETAIL_PRICE," +
    			" A.BATCH_NO, A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY," +
    			" C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ" +
    			" FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C" +
    			" WHERE A.ORDER_CODE = B.ORDER_CODE" +
    			" AND A.ORDER_CODE = C.ORDER_CODE" +
    			" AND A.DISPENSE_NO = '" + dispense_no + "'";
    	}else{
    		sqlGetIndDispenseDByNo = INDSQL.getIndDispenseDByNo(dispense_no);
    	}*/
		sqlGetIndDispenseDByNo = 
			" SELECT CASE" +
			" WHEN B.GOODS_DESC IS NULL" +
			" THEN B.ORDER_DESC" +
			" ELSE B.ORDER_DESC || '(' || B.GOODS_DESC || ')'" +
			" END AS ORDER_DESC," +
			" B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE," 
		  + "  CASE WHEN A.UNIT_CODE=C.STOCK_UNIT " 
          + "       THEN A.INVENT_PRICE "
          + "       WHEN A.UNIT_CODE=C.DOSAGE_UNIT THEN A.VERIFYIN_PRICE ELSE A.VERIFYIN_PRICE  END  VERIFYIN_PRICE, "   //ADD VERIFYIN_PRICE
			//" A.VERIFYIN_PRICE AS  STOCK_PRICE, " 
           // " A.RETAIL_PRICE   AS RETAIL_PRICE," 
            + " CASE  WHEN A.UNIT_CODE = C.STOCK_UNIT "
		    + "       THEN A.RETAIL_PRICE * C.DOSAGE_QTY  "
		    + "       WHEN A.UNIT_CODE = C.DOSAGE_UNIT "
		    + "       THEN A.RETAIL_PRICE "
		    + "       ELSE A.RETAIL_PRICE "
		    + "       END  RETAIL_PRICE, "
			+" A.BATCH_NO, A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY," +
			" C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ" +
			" FROM SPC_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C" +//wanglong add 20150202
			" WHERE A.ORDER_CODE = B.ORDER_CODE" +
			" AND A.ORDER_CODE = C.ORDER_CODE" +
			" AND A.DISPENSE_NO = '" + dispense_no + "'";   
		
//		System.out.println("---sqlGetIndDispenseDByNo:"+sqlGetIndDispenseDByNo);
//        TParm result = new TParm(TJDODBTool.getInstance().select(
//            INDSQL.getIndDispenseDByNo(dispense_no)));
		TParm result = new TParm(TJDODBTool.getInstance().select(
        		sqlGetIndDispenseDByNo));
        //===zhangp 20120710 end
        //System.out.println("result----"+result);
        if (result.getCount("ORDER_CODE") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_d.removeRowAll();
        table_d.setSelectionMode(0);
        // 设定单位类型
        if ("TEC".equals(request_type) || "EXM".equals(request_type)
            || "COS".equals(request_type)) {
            u_type = "1";
        }
        else if ("DEP".equals(request_type)) {
            u_type = IndSysParmTool.getInstance().onQuery().getValue(
                "UNIT_TYPE", 0);
        }
        else {
            u_type = "0";
        }
        // 设定出入库部门
        if ("DEP".equals(request_type) || "TEC".equals(request_type)) {
            out_org_code = this.getValueString("TO_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("APP_ORG_CODE");
            in_flg = true;

        }
        else if ("GIF".equals(request_type) || "RET".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = true;
        }
        else if ("WAS".equals(request_type) || "THO".equals(request_type)) {
            out_org_code = this.getValueString("APP_ORG_CODE");
            out_flg = true;
            in_org_code = this.getValueString("TO_ORG_CODE");
            in_flg = false;
        }

        resultParm = result;
        // 填充TABLE_D
        //System.out.println("result---"+result);
        result = setTableDValue(result);
        if (result.getCount("ORDER_DESC") == 0) {
            this.messageBox("没有申请明细");
            return;
        }
        table_d.setParmValue(result);
    }


    /**
     * 填充TABLE_D
     *
     * @param table
     * @param parm
     * @param args
     */
    private TParm setTableDValue(TParm result) {
        TParm parm = new TParm();
        double qty = 0;
        double actual_qty = 0;
        double stock_price = 0;
        double retail_price = 0;
        double atm = 0;
        String order_code = "";
        boolean flg = false;
        if (getRadioButton("UPDATE_FLG_B").isSelected()) {
            flg = false;
        }
        else {
            flg = true;
        }
        for (int i = 0; i < result.getCount(); i++) {
            parm.setData("SELECT_FLG", i, flg);
            parm.setData("ORDER_DESC", i, result.getValue("ORDER_DESC", i));
            parm.setData("SPECIFICATION", i, result
                         .getValue("SPECIFICATION", i));
            qty = result.getDouble("QTY", i);
            parm.setData("QTY", i, qty);
            actual_qty = result.getDouble("ACTUAL_QTY", i);
            parm.setData("ACTUAL_QTY", i, actual_qty);
            order_code = result.getValue("ORDER_CODE", i);
            // 库存量(出库部门)
            if (!"".equals(result.getValue("BATCH_NO", i))) {
                if ("0".equals(u_type)) {//0代表库存单位是盒 所以stock_qty（片） 要除以规格=盒，1代表发药单位by liyh 20120910
                	if("RET".equals(request_type)){//如果是退药，查询库存量 不用根据batch_no,valid_date来查询，只需根据order_code就Ok by liyh 20120910
                        parm.setData("STOCK_QTY", i,
                                INDTool.getInstance().getSPCStockQTY(
                                    out_org_code, order_code) /
                                result.getDouble("DOSAGE_QTY", i));
                	}else{
	                    parm.setData("STOCK_QTY", i,
	                                 INDTool.getInstance().getStockQTYAcnt(
	                                     out_org_code, order_code,
	                                     result.getValue("BATCH_NO", i),
	                                     result.getValue("VALID_DATE",
	                        i).substring(0, 10), Operator.getRegion()) /
	                                 result.getDouble("DOSAGE_QTY", i)
	                        );
                	}
                }
                else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getStockQTYAcnt(
                                     out_org_code, order_code,
                                     result.getValue("BATCH_NO", i),
                                     result.getValue("VALID_DATE",
                        i).substring(0, 10), Operator.getRegion()));
                }
            }
            else {
                if ("0".equals(u_type)) {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getSPCStockQTY(
                                     out_org_code, order_code) /
                                 result.getDouble("DOSAGE_QTY", i));
                }
                else {
                    parm.setData("STOCK_QTY", i,
                                 INDTool.getInstance().getSPCStockQTY(
                                     out_org_code, order_code));
                }
            }
            if (getRadioButton("UPDATE_FLG_B").isSelected()) {
                parm.setData("OUT_QTY", i, qty - actual_qty);
            }
            else {
                parm.setData("OUT_QTY", i, qty);
            }
            parm.setData("UNIT_CODE", i, result.getValue("UNIT_CODE", i));
           
            /**YUANXM去掉这段代码 SQL查询时就已判断*/
            // 使用单位
            if ("0".equals(u_type)) {
                // 库存单位
                stock_price = result.getDouble("VERIFYIN_PRICE", i);  //退库取得IND_REQUEStD表的数据
                retail_price = result.getDouble("RETAIL_PRICE", i);   //数据已经是整盒的价格
            }
            else {
                // 配药单位
                stock_price = result.getDouble("VERIFYIN_PRICE", i);
                retail_price = result.getDouble("RETAIL_PRICE", i);
            } 
          
           // stock_price = result.getDouble("STOCK_PRICE", i);
           // retail_price = result.getDouble("RETAIL_PRICE", i);
            parm.setData("STOCK_PRICE", i, stock_price);
            atm = StringTool.round(stock_price * qty, 2);
            parm.setData("STOCK_ATM", i, atm);
            parm.setData("RETAIL_PRICE", i, retail_price);
            atm = StringTool.round(retail_price * qty, 2);
            parm.setData("RETAIL_ATM", i, atm);
            atm = StringTool.round(retail_price * qty - stock_price * qty, 2);
            parm.setData("DIFF_ATM", i, atm);
            parm.setData("BATCH_NO", i, result.getValue("BATCH_NO", i));
            parm.setData("VALID_DATE", i, result.getTimestamp("VALID_DATE", i));
            parm.setData("PHA_TYPE", i, result.getValue("PHA_TYPE", i));
            parm.setData("ORDER_CODE", i, order_code);
            //luhai 2012-1-16 add batch_seq
            parm.setData("BATCH_SEQ",i,result.getValue("BATCH_SEQ",i));
        }
        //System.out.println("------"+parm);
        return parm;
    }

    /**
     * 表格选中行数据上翻
     *
     * @param table
     * @param row
     * @param args
     */
    private void getTableSelectValue(TTable table, int row, String[] args) {
        for (int i = 0; i < args.length; i++) {
            setValue(args[i], table.getItemData(row, args[i]));
        }
    }

    /**
     * 数据检验
     *
     * @return
     */
    private boolean CheckDataM() {
        if ("".equals(getValueString("REQUEST_NO"))) {
            this.messageBox("申请单号不能为空");
            return false;
        }
        if ("".equals(getValueString("APP_ORG_CODE"))) {
            this.messageBox("申请部门不能为空");
            return false;
        }
        if ("".equals(getValueString("REQTYPE_CODE"))) {
            this.messageBox("单据类别不能为空");
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
        table_d.acceptText();
        for (int i = 0; i < table_d.getRowCount(); i++) {
            // 判断数据正确性
            double qty = table_d.getItemDouble(i, "OUT_QTY");
            if (qty <= 0) {
                this.messageBox("出库数数量不能小于或等于0");
                return false;
            }
            double price = table_d.getItemDouble(i, "STOCK_PRICE");
            if (price <= 0) {
                this.messageBox("成本价不能小于或等于0");
                return false;
            }
        }
        return true;
    }

    /**
     * 计算零售总金额
     *
     * @return
     */
    private double getSumRetailMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "RETAIL_ATM");
        }
        return StringTool.round(sum, 2);
    }

    /**
     * 计算成本总金额
     *
     * @return
     */
    private double getSumRegMoney() {
        table_d.acceptText();
        double sum = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            sum += table_d.getItemDouble(i, "STOCK_ATM");
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
     * 药库参数信息
     * @return TParm
     */
    private TParm getSysParm() {
        return IndSysParmTool.getInstance().onQuery();
    }

    /**
     * 出入库作业状态判断
     *
     * @return
     */
    private String getDisCheckFlg(TParm parm) {
        // 出入库作业状态判断
        if ("Y".equals(parm.getValue("DISCHECK_FLG", 0))
            && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // 需进行入库确认者且申请单状态入出库部门皆不为空-->在途状态
            return "1";
        }
        else if ("N".equals(parm.getValue("DISCHECK_FLG", 0))
                 && !"".equals(out_org_code) && !"".equals(in_org_code)) {
            // 不需进行入库确认者且申请单状态入出库部门皆不为空-->出库即入库
            return "2";
        }
        return "1";
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
            return false;
        }
        return true;
    }

    /**
     * 出库批号判断: 1,不指定批号和效期;2,指定批号和效期
     *
     * @return
     */
    private String getBatchValid(String type) {
        if ("DEP".equals(type) || "TEC".equals(type) || "EXM".equals(type)
            || "GIF".equals(type) || "COS".equals(type)) {
            return "1";
        }
        return "2";
    }

    /**
     * 出库在途作业/耗损、其它出库作业、卫耗材、科室备药(出库即入库)
     */
    private boolean getDispenseOutOn(String org_code) {
        if (!checkSelectRow()) {
            return false;
        }
        //判断请领出库
        if ("DEP".equals(request_type)) {
            String order_code = "";
            double out_qty = 0;
            String order_desc = "";
            for (int i = 0; i < table_d.getRowCount(); i++) {
                if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                    //判断中药出多批次数据
                    order_code = table_d.getParmValue().getValue("ORDER_CODE",
                        i);
                    out_qty = table_d.getItemDouble(i, "OUT_QTY");
                    String sql =
                        " SELECT A.STOCK_QTY / B.STOCK_QTY / B.DOSAGE_QTY AS "
                        + "STOCK_QTY,C.UNIT_CHN_DESC,A.BATCH_NO,A.VALID_DATE, "
                        + " A.ORDER_CODE, D.ORDER_DESC "
                        + " FROM SPC_STOCK A, PHA_TRANSUNIT B, "//wanglong modify 20150202
                        + " SYS_UNIT C, PHA_BASE D "
                        + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                        + " AND A.ORDER_CODE = D.ORDER_CODE "
                        + " AND B.ORDER_CODE = D.ORDER_CODE "
                        + " AND A.ORG_CODE = '" + org_code
                        + "' AND A.ORDER_CODE = '" + order_code
                        + "' AND A.STOCK_QTY > 0 AND A.VALID_DATE > SYSDATE "
                        + " AND A.ACTIVE_FLG = 'Y' AND D.PHA_TYPE = 'G'"
                        + " AND B.STOCK_UNIT = C.UNIT_CODE "
                        + " ORDER BY A.VALID_DATE DESC, A.BATCH_SEQ";
                    TParm checkParm = new TParm(TJDODBTool.getInstance().select(
                        sql));
                    if (checkParm.getCount("ORDER_CODE") > 0) {
                        if (out_qty > checkParm.getDouble("STOCK_QTY", 0)) {
                            order_desc = checkParm.getValue("ORDER_DESC", 0);
                            this.messageBox(order_desc + " " + order_code
                                            + " 请领出库 " + out_qty + " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + ", 批号:" +
                                            checkParm.getValue("BATCH_NO", 0) +
                                            ",效期:" +
                                            checkParm.getValue("VALID_DATE", 0).
                                            substring(0, 10) +
                                            ",当前库存为" +
                                            checkParm.getDouble("STOCK_QTY", 0) +
                                            " " +
                                            checkParm.getValue("UNIT_CHN_DESC",
                                0) + " ,建议先将其全部出库");
                            return false;
                        }
                    }
                }
            }
        }

        TParm parm = new TParm();
        // 主项信息
        if (!CheckDataM()) {
            return false;
        }
        parm = getDispenseMParm(parm, "1");
        // 细项信息
        if (!CheckDataD()) {
            return false;
        }
        parm = getDispenseDParm(parm);
        // 使用单位
        parm.setData("UNIT_TYPE", u_type);
        // 申请单类型
        parm.setData("REQTYPE_CODE", request_type);
        // 出库部门
        parm.setData("ORG_CODE", org_code);
        // 执行数据新增
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertOutOnAcnt", parm);//wanglong add 20150202

        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return false;
        }
        this.messageBox("P0001");
        this.setValue("DISPENSE_NO", dispense_no);
        return true;
    }

    /**
     * 出库即入库作业
     *
     * @param out_org_code
     * @param in_org_code
     * @param batchvalid
     */
    private void getDispenseOutIn(String out_org_code, String in_org_code,
                                  String reuprice_flg, boolean out_flg,
                                  boolean in_flg) {
        if (!checkSelectRow()) {
            return;
        }
        TParm parm = new TParm();
        // 主项信息(OUT_M)
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "3");
        // 细项信息(OUT_D)
        if (!CheckDataD()) {
            return;
        }
        parm = getDispenseDParm(parm);
        //System.out.println("PARM-> " + parm);

        // 使用单位
        parm.setData("UNIT_TYPE", u_type);
        // 申请单类型
        parm.setData("REQTYPE_CODE", request_type);
        // 出库部门
        parm.setData("OUT_ORG_CODE", out_org_code);
        // 入库部门
        parm.setData("IN_ORG_CODE", in_org_code);
        // 是否入库(IN_FLG)
        parm.setData("IN_FLG", in_flg);
        // 判断是否自动将成本价存回批发价
        parm.setData("REUPRICE_FLG", reuprice_flg);

        // 执行数据新增
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onInsertOutInAcnt", parm);//wanglong modify 20150202
        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 更新主项(在途)
     */
    private void getUpdateDispenseMOutOn() {
        TParm parm = new TParm();
        // 主项信息
        if (!CheckDataM()) {
            return;
        }
        parm = getDispenseMParm(parm, "1").getParm("OUT_M");
        // 执行数据更新
        parm = TIOM_AppServer.executeAction("action.spc.INDDispenseAction",
                                            "onUpdateMOutOnAcnt", parm);//wanglong modify 20150202
        // 保存判断
        if (parm == null || parm.getErrCode() < 0) {
            this.messageBox("E0001");
            return;
        }
        this.messageBox("P0001");
        onClear();
    }

    /**
     * 库存量判断(相同药品同时出库其数量加总一起进行扣库)
     *
     * @return
     */
    private boolean checkSameOrderQty(Map map) {
        String order_code = "";
        double qty = 0;
        double stock_qty = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            order_code = resultParm.getValue("ORDER_CODE", i);
            stock_qty = INDTool.getInstance().getSPCStockQTY(out_org_code,
                order_code);//wanglong modify 20150202
            qty = TypeTool.getDouble(map.get(order_code));
            if ("0".equals(u_type)) {
                qty = qty * resultParm.getDouble("DOSAGE_QTY", i);
            }
            if (qty > stock_qty) {
                this.messageBox("出库数量大于库存量");
                return false;
            }
        }
        return true;
    }

    /**
     * 相同药品数量加总
     *
     * @return
     */
    private Map getSumQtyByOrder() {
        /** 相同药品数量加总 */
        Map map = new HashMap();
        String order_code = "";
        double out_qty = 0;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            order_code = resultParm.getValue("ORDER_CODE", i);
            out_qty = table_d.getItemDouble(i, "OUT_QTY");
            if (map.containsKey(order_code)) {
                double qty = TypeTool.getDouble(map.get(order_code)) + out_qty;
                map.remove(order_code);
                map.put(order_code, qty);
            }
            else {
                map.put(order_code, out_qty);
            }
        }
        return map;
    }

    /**
     * 判断细项是否选中
     *
     * @return
     */
    private boolean checkSelectRow() {
        // 判断细项是否选中
        boolean flg = true;
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("Y".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                flg = false;
            }
        }
        if (flg) {
            this.messageBox("没有选中");
            return false;
        }
        return true;
    }

    /**
     * 获得主项信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseMParm(TParm parm, String update_flg) {
        TParm parmM = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        // 出库单号
        dispense_no = "";
        if ("".equals(getValueString("DISPENSE_NO"))) {
            dispense_no = SystemTool.getInstance().getNo("ALL", "IND",
                "IND_DISPENSE", "No");
        }
        else {
            dispense_no = getValueString("DISPENSE_NO");
        }
        parmM.setData("DISPENSE_NO", dispense_no);
        parmM.setData("REQTYPE_CODE", getValue("REQTYPE_CODE"));
        parmM.setData("REQUEST_NO", getValue("REQUEST_NO"));
        parmM.setData("REQUEST_DATE", getValue("REQUEST_DATE"));
        parmM.setData("APP_ORG_CODE", getValue("APP_ORG_CODE"));
        parmM.setData("TO_ORG_CODE", getValue("TO_ORG_CODE"));
        parmM.setData("URGENT_FLG", getValue("URGENT_FLG"));
        parmM.setData("DESCRIPTION", getValue("DESCRIPTION"));
        parmM.setData("DISPENSE_DATE", getValue("DISPENSE_DATE"));
        parmM.setData("DISPENSE_USER", Operator.getID());
        if (!"1".equals(update_flg)) {
            parmM.setData("WAREHOUSING_DATE", date);
            parmM.setData("WAREHOUSING_USER", Operator.getIP());
        }
        else {
            parmM.setData("WAREHOUSING_DATE", tnull);
            parmM.setData("WAREHOUSING_USER", "");
        }
        parmM.setData("REASON_CHN_DESC", getValue("REASON_CHN_DESC"));
        parmM.setData("UNIT_TYPE", u_type);
        if ("WAS".equals(getValue("REQTYPE_CODE")) ||
            "THO".equals(getValue("REQTYPE_CODE")) ||
            "COS".equals(getValue("REQTYPE_CODE")) ||
            "EXM".equals(getValue("REQTYPE_CODE"))) {
            update_flg = "3";
        }
        parmM.setData("UPDATE_FLG", update_flg);
        parmM.setData("OPT_USER", Operator.getID());
        parmM.setData("OPT_DATE", date);
        parmM.setData("OPT_TERM", Operator.getIP());
        //zhangyong20110517
        parmM.setData("REGION_CODE", Operator.getRegion());

        //药品种类--普药:1,麻精：2
        if(getRadioButton("G_DRUGS").isSelected()){
        	parmM.setData("DRUG_CATEGORY","1");
        }else if(getRadioButton("N_DRUGS").isSelected()){
        	parmM.setData("DRUG_CATEGORY","2");
        }else {
			parmM.setData("DRUG_CATEGORY",3);
		}
        
        //申请方式--全部:APP_ALL,人工:APP_ARTIFICIAL,请领建议:APP_PLE,自动拔补:APP_AUTO
        parmM.setData("APPLY_TYPE","0");
        
        if (parmM != null) {
            parm.setData("OUT_M", parmM.getData());
        }
        
       
        return parm;
    }

    /**
     * 获得明细信息
     *
     * @param parm
     * @return
     */
    private TParm getDispenseDParm(TParm parm) {
        TParm parmD = new TParm();
        Timestamp date = SystemTool.getInstance().getDate();
        TNull tnull = new TNull(Timestamp.class);
        String batch_no = "";
        int count = 0;
        String order_code = "";
        String valid_date = "";
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            parmD.setData("DISPENSE_NO", count, dispense_no);
            parmD.setData("SEQ_NO", count, seq + count);
            parmD.setData("REQUEST_SEQ", count, resultParm.getInt(
                "REQUEST_SEQ", i));
            order_code = resultParm.getValue("ORDER_CODE", i);
            parmD.setData("ORDER_CODE", count, order_code);
            parmD.setData("QTY", count, table_d.getItemDouble(i, "QTY"));
            parmD.setData("UNIT_CODE", count, table_d.getItemString(i,
                "UNIT_CODE"));
            parmD.setData("RETAIL_PRICE", count, table_d.getItemDouble(i,
                "RETAIL_PRICE"));
            parmD.setData("STOCK_PRICE", count, table_d.getItemDouble(i,
                "STOCK_PRICE"));
            parmD.setData("ACTUAL_QTY", count, table_d.getItemDouble(i,
                "OUT_QTY"));
            parmD.setData("PHA_TYPE", count, table_d.getItemString(i,
                "PHA_TYPE"));
            //luahi modify 2012-1-16 batch_seq 的值需要从Tparm中取得，因为页面中并不体现batch_seq begin
//            parmD.setData("BATCH_SEQ", count, table_d.getItemData(i,
//                "BATCH_SEQ"));
              parmD.setData("BATCH_SEQ",count,table_d.getParmValue().getInt("BATCH_SEQ",i));
            //luahi modify 2012-1-16 batch_seq 的值需要从Tparm中取得，因为页面中并不体现batch_seq end
            
            batch_no = table_d.getItemString(i, "BATCH_NO");
            parmD.setData("BATCH_NO", count, batch_no);
            valid_date = table_d.getItemString(i, "VALID_DATE");
            if ("".equals(valid_date)) {
                parmD.setData("VALID_DATE", count, tnull);
            }
            else {
                parmD.setData("VALID_DATE", count,
                              table_d.getItemTimestamp(i, "VALID_DATE"));
            }
            parmD.setData("DOSAGE_QTY", count, resultParm.getDouble(
                "DOSAGE_QTY", i));
            parmD.setData("OPT_USER", count, Operator.getID());
            parmD.setData("OPT_DATE", count, date);
            parmD.setData("OPT_TERM", count, Operator.getIP());
            
            //是否下架
            parmD.setData("IS_BOXED",count,"Y");
            parmD.setData("BOXED_USER",count,Operator.getID());
            parmD.setData("BOX_ESL_ID",count,"");
            
            //电子标签应用
            parmD.setData("ELETAG_CODE",count,resultParm.getValue(
                "ELETAG_CODE", i));
            parmD.setData("ORDER_DESC",count,resultParm.getValue("ORDER_DESC",i));
            
            count++;
        }
        if (parmD != null) {
            parm.setData("OUT_D", parmD.getData());
        }
        return parm;
    }

    /**
     * 判断是否存在中草药
     *
     * @return
     */
    private boolean checkPhaType() {
        for (int i = 0; i < table_d.getRowCount(); i++) {
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            if ("C".equals(table_d.getItemString(i, "PHA_TYPE"))) {
                return true;
            }
        }
        return false;
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
     * 判断是否有库存，并将没有库存的药品取消勾选
     * @return String
     */
    private String checkStockQty() {
        double stock_qty = 0;
        // 库存不足信息
        String message = "";
        for (int i = 0; i < table_d.getRowCount(); i++) {
            boolean flg = false;
            if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
                continue;
            }
            stock_qty = table_d.getItemDouble(i, "STOCK_QTY");
            double qty = table_d.getItemDouble(i, "OUT_QTY");

            if (qty > stock_qty) {
                flg = true;
            }
            // 没有库存的药品取消勾选，记录不足药品的信息
            if (flg) {
                table_d.setItem(i, "SELECT_FLG", "N");
                message += table_d.getItemString(i, "ORDER_DESC") + "\n";
            }
        }
        if (!"".equals(message) && message.length() > 0) {
            message += "库存不足！";
        }
        return message;
    }
}

