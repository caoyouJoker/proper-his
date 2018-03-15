package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 手术包传回
 * </p>
 * 
 * <p>
 * Description: 手术包传回
 * </p>
 *  
 * <p>
 * Copyright: Copyright (c) ProperSoft 2013
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author caowl
 * @version 1.0
 */
public class INVSPCPackControl extends TControl {

    TTable tableD;
    String dept;
    TCheckBox all;
    TDS packDDS = new TDS();
    TTextFormat DEPT;
  
    /**
     * 初始化
     */
    public void onInit() {
        TParm outsideParm = (TParm) getParameter();
        if (outsideParm != null) {
            dept = outsideParm.getValue("PACK", "DEPT");
        }
        tableD = ((TTable) getComponent("TABLED"));
        callFunction("UI|PACK_CODE|addEventListener", TTextFieldEvent.KEY_PRESSED, this,
                     "onBarCode");
        tableD.addEventListener(TTableEvent.CHECK_BOX_CLICKED, this, "onCheckBoxClicked");
        tableD.addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE, this, "onTableValueChange");
        TParm parm = new TParm();
        getTextField("PACK_CODE")  
                .setPopupMenuParameter("UD",
                                       getConfigParm()
                                               .newConfig("%ROOT%\\config\\inv\\INVPackPopup.x"),
                                       parm);
        getTextField("PACK_CODE").addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
        all = ((TCheckBox) getComponent("ALL"));
        DEPT = ((TTextFormat) getComponent("DEPT"));
        DEPT.setValue(dept);
    }
   
    /**
     * 接受返回值方法
     * 
     * @param tag
     * @param obj
     */
    public void popReturn(String tag, Object obj) {
        TParm parm = (TParm) obj;
        if (parm == null) {
            return;
        }
        String pack_code = parm.getValue("PACK_CODE");
        if (!StringUtil.isNullString(pack_code)) {
            this.setValue("PACK_CODE", pack_code + "000000");
            this.setValue("PACK_DESC", parm.getValue("PACK_DESC"));
        }
        onBarCode();
    }

    /**
     * 选择套餐事件
     */
    public void onPackCode() {
        TParm parm = new TParm();
        String bar_code = this.getValueString("PACK1_CODE");
        parm.setData("BAR_CODE", this.getValueString("PACK1_CODE"));
        String sql = "";
        int length = bar_code.length();
        if (length == 12) {
            if (bar_code.substring(6, 12).equals("000000")) {// 诊疗包主包
                sql =
                    "SELECT DISTINCT A.BARCODE PACK_CODE, B.PACK_DESC, D.INV_CODE, D.INV_CHN_DESC, C.DESCRIPTION, C.QTY, "
                    + "       C.STOCK_UNIT, C.OPT_USER, C.OPT_DATE, C.OPT_TERM, 0 AS USED_QTY, 0 AS NOTUSED_QTY "
                    + "  FROM INV_PACKSTOCKM A, INV_PACKM B, INV_PACKD C, INV_BASE D "
                    + " WHERE A.PACK_CODE = B.PACK_CODE "
                    + "   AND B.PACK_CODE = C.PACK_CODE "
                    + "   AND C.INV_CODE = D.INV_CODE   "
                    + "   AND A.BARCODE = '#'           ";
            } else {
                sql =
                    "SELECT DISTINCT A.BARCODE PACK_CODE, B.PACK_DESC, C.INV_CODE, C.INV_CHN_DESC, A.DESCRIPTION, A.QTY, "
                    + "       A.STOCK_UNIT, A.OPT_USER, A.OPT_DATE, A.OPT_TERM, 0 AS USED_QTY, 0 AS NOTUSED_QTY "
                    + "  FROM INV_PACKSTOCKD_HISTORY A, INV_PACKM B, INV_BASE C "
                    + " WHERE A.PACK_CODE = B.PACK_CODE "
                    + "   AND A.INV_CODE = C.INV_CODE   "
                    + "   AND A.BARCODE = '#'           ";
            }
        } else if (length == 8) {// 套餐       手术费、普通药品费、普通耗材
            sql =
                "SELECT B.PACK_CODE, B.PACK_DESC, B.CLASS_CODE, "
                + "       CASE WHEN A.INV_CODE IS NULL THEN A.ORDER_CODE ELSE A.INV_CODE END AS INV_CODE, "
                + "       CASE WHEN A.INV_CODE IS NULL THEN A.ORDER_DESC ELSE C.INV_CHN_DESC END AS INV_CHN_DESC, "
                + "       A.DOSAGE_QTY AS QTY, A.DOSAGE_UNIT AS STOCK_UNIT, A.HIDE_FLG, A.OPT_USER, A.OPT_DATE, "
                + "       A.OPT_TERM, A.OPT_TERM, 0 AS USED_QTY, 0 AS NOTUSED_QTY, '' AS DESCRIPTION "
                + "  FROM SYS_ORDER_PACKD A, SYS_ORDER_PACKM B, INV_BASE C "
                + " WHERE A.PACK_CODE = B.PACK_CODE     "
                + "   AND A.INV_CODE = C.INV_CODE(+)    "
                + "   AND A.PACK_CODE = '#'             ";
        }
        sql = sql.replaceFirst("#", bar_code);
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount() < 0) {
            return;
        }
        this.setValue("PACK_CODE", selParm.getData("PACK_CODE", 0));
        this.setValue("PACK1_DESC", selParm.getData("PACK_DESC", 0));
        String packBacthNo = "";
        if (!selParm.getValue("PACK_BATCH_NO", 0).equals("")) {
            packBacthNo = selParm.getValue("PACK_BATCH_NO", 0);
        }
        String class_code = selParm.getValue("CLASS_CODE", 0);
        showByOutside(bar_code, packBacthNo, class_code);
        this.clearValue("PACK_DESC");
    }

    /**
     * “手术包代码”回车事件
     */
    public void onBarCode() {
        if (this.getValueString("PACK_CODE").trim().length() > 0) {
            callFunction("UI|PACK_CODE|grabFocus");
        }
        TParm parm = new TParm();
        String bar_code = this.getValueString("PACK_CODE");
        parm.setData("BAR_CODE", this.getValueString("PACK_CODE"));
        String sql = "";
        int length = bar_code.length();
        if (length == 12) {// 手术包或者诊疗包
            if (bar_code.substring(6, 12).equals("000000")) {// 诊疗包主包
                sql =
                        "SELECT B.BARCODE AS PACK_CODE,A.PACK_DESC,A.PY2,A.DESCRIPTION,A.USE_COST,A.VALUE_DATE,"
                                + "A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PACK_CODE AS PACK_BATCH_NO,0 AS CLASS_CODE "
                                + " FROM INV_PACKM A, INV_PACKSTOCKM B "
                                + " WHERE A.PACK_CODE = B.PACK_CODE AND B.BARCODE = '" + bar_code
                                + "'" + " ORDER BY B.PACK_BATCH_NO DESC";
            } else {// 手术包
                sql =
                        " SELECT B.BARCODE AS PACK_CODE,A.PACK_DESC,A.PY1,A.DESCRIPTION,A.USE_COST,A.VALUE_DATE,"
                                + "A.OPT_USER,A.OPT_DATE,A.OPT_TERM,'' AS PACK_BATCH_NO,0 AS CLASS_CODE "
                                + " FROM INV_PACKM A, INV_PACKSTOCKM_HISTORY B "
                                + " WHERE A.PACK_CODE = B.PACK_CODE AND B.BARCODE = '" + bar_code
                                + "'";
            }
        } else if (length == 8) {
            sql =
                    " SELECT PACK_CODE,PACK_DESC,PY1,DESCRIPTION,OPT_USER,OPT_DATE,OPT_TERM,'' AS PACK_BATCH_NO,0 AS CLASS_CODE "
                            + " FROM SYS_ORDER_PACKM WHERE PACK_CODE = '" + bar_code + "' ";
        }
        TParm selParm = new TParm(TJDODBTool.getInstance().select(sql));
        if (selParm.getCount() > 0) {
            setValue("PACK_DESC", selParm.getData("PACK_DESC", 0));
            String packBacthNo = "";//在此方法中，PACK_BATCH_NO一直为空
            if (!selParm.getValue("PACK_BATCH_NO", 0).equals("")) {
                packBacthNo = selParm.getValue("PACK_BATCH_NO", 0);
            }
            String class_code = "0";
            showByOutside(bar_code, packBacthNo, class_code);
        }
        this.clearValue("PACK1_CODE;PACK1_DESC");
    }

    public void showByOutside(String barCode, String packBacthNo, String class_code) {
        String exeSel = getSql(barCode, packBacthNo, class_code);
        TParm selParm = new TParm(TJDODBTool.getInstance().select(exeSel));
        int count = selParm.getCount();
        TParm tableParm = new TParm();
        for (int i = 0; i < count; i += 2) {
            tableParm.addData("N_SEL", "N");
            tableParm.addData("N_INV_CHN_DESC", selParm.getValue("INV_CHN_DESC", i));
            tableParm.addData("N_QTY", selParm.getDouble("QTY", i));
            tableParm.addData("N_USERD_QTY", selParm.getDouble("USERD_QTY", i));
            tableParm.addData("N_NOTUSED_QTY", selParm.getDouble("NOTUSED_QTY", i));
            tableParm.addData("N_STOCK_UNIT", selParm.getValue("STOCK_UNIT", i));
            if (selParm.getValue("INV_CODE", i).equals("")) {// wanglong add 20140916
                tableParm.addData("N_INV_CODE", selParm.getValue("ORDER_CODE", i));
            } else {
                tableParm.addData("N_INV_CODE", selParm.getValue("INV_CODE", i));
            }
            if (selParm.getDouble("OWN_PRICE", i) != 0) {// wanglong add 20140609
                tableParm.addData("N_OWN_PRICE", selParm.getDouble("OWN_PRICE", i));
            } else {
                tableParm.addData("N_OWN_PRICE", selParm.getDouble("COST_PRICE", i));
            }
            tableParm.addData("N_ORDER_CODE", selParm.getValue("ORDER_CODE", i));//wanglong add 20141014
            tableParm.addData("BLANK", "");
            tableParm.addData("S_SEL", "N");
            tableParm.addData("S_INV_CHN_DESC", selParm.getValue("INV_CHN_DESC", i + 1));
            tableParm.addData("S_QTY", selParm.getDouble("QTY", i + 1));
            tableParm.addData("S_USERD_QTY", selParm.getDouble("USERD_QTY", i + 1));
            tableParm.addData("S_NOTUSED_QTY", selParm.getDouble("NOTUSED_QTY", i + 1));
            tableParm.addData("S_STOCK_UNIT", selParm.getValue("STOCK_UNIT", i + 1));
            if (selParm.getValue("INV_CODE", i + 1).equals("")) {// wanglong add 20140916
                tableParm.addData("S_INV_CODE", selParm.getValue("ORDER_CODE", i + 1));
            } else {
                tableParm.addData("S_INV_CODE", selParm.getValue("INV_CODE", i + 1));
            }
            if (selParm.getDouble("OWN_PRICE", i + 1) != 0) {// wanglong add 20140609
                tableParm.addData("S_OWN_PRICE", selParm.getDouble("OWN_PRICE", i + 1));
            } else {
                tableParm.addData("S_OWN_PRICE", selParm.getDouble("COST_PRICE", i + 1));
            }
            tableParm.addData("S_ORDER_CODE", selParm.getValue("ORDER_CODE", i + 1));//wanglong add 20141014
        }
        tableParm.setCount(tableParm.getCount("N_SEL"));
        tableD.setParmValue(tableParm);
    }

    public String getSql(String barCode, String packBacthNo, String class_code) {//packBacthNo参数无用
        String sql = "";
        int length = barCode.length();
        if (length == 12) {
            if (barCode.substring(6, 12).equals("000000")) {// 诊疗包细包
                barCode = barCode.substring(0, 6);
                sql =
                        " SELECT B.INV_CODE,B.INV_CHN_DESC,A.DESCRIPTION,A.QTY,A.STOCK_UNIT,"
                                //wanglong add 20140916 增加ORDER_CODE
                                + "A.OPT_USER,A.OPT_DATE,A.OPT_TERM,0 AS USED_QTY,0 AS NOTUSED_QTY, B.COST_PRICE, C.OWN_PRICE, B.ORDER_CODE "
                                + "  FROM INV_PACKD A, INV_BASE B, SYS_FEE C "
                                + " WHERE A.INV_CODE = B.INV_CODE            "
                                + "   AND A.PACK_CODE = '#'                  "
                                + "   AND B.ORDER_CODE = C.ORDER_CODE(+) ";
            } else {// 手术包细包
                sql =
                        " SELECT B.INV_CODE,B.INV_CHN_DESC,A.DESCRIPTION,A.QTY,A.STOCK_UNIT,"
                                //wanglong add 20140916 增加ORDER_CODE
                                + "A.OPT_USER,A.OPT_DATE,A.OPT_TERM,0 AS USED_QTY,0 AS NOTUSED_QTY, B.COST_PRICE, C.OWN_PRICE, B.ORDER_CODE "
                                + "  FROM INV_PACKSTOCKD_HISTORY A, INV_BASE B, SYS_FEE C  "
                                + " WHERE A.INV_CODE = B.INV_CODE "
                                + "   AND A.BARCODE = '#'                                  "
                                + "   AND B.ORDER_CODE = C.ORDER_CODE(+) ";
            }
        } else if (class_code.equals("2")) {// 低值套餐细项
            sql =
                    " SELECT A.PACK_CODE,A.INV_CODE,A.ORDER_DESC AS INV_CHN_DESC,A.DOSAGE_QTY AS QTY,A.DOSAGE_UNIT AS STOCK_UNIT,A.HIDE_FLG,"
                            //wanglong add 20140916 增加ORDER_CODE
                            + "A.OPT_USER,A.OPT_DATE,A.OPT_TERM,0 AS USED_QTY,0 AS NOTUSED_QTY,'' AS DESCRIPTION, C.OWN_PRICE, A.ORDER_CODE "
                            + "  FROM SYS_ORDER_PACKD A, SYS_ORDER_PACKM B, SYS_FEE C "
                            + " WHERE A.PACK_CODE = '#'                            "
                            + "   AND A.PACK_CODE = B.PACK_CODE                "
                          //  + "   AND INV_CODE IS NOT NULL                "//wanglong delete 20140916
                            + "   AND A.ORDER_CODE = C.ORDER_CODE(+) ";
        } else if ((class_code.equals("1")) || (class_code.equals("5"))) {
            sql =
                    " SELECT A.PACK_CODE,A.ORDER_CODE AS INV_CODE,A.ORDER_DESC AS INV_CHN_DESC,A.DOSAGE_QTY AS QTY,A.DOSAGE_UNIT AS STOCK_UNIT,A.HIDE_FLG,"
                            //wanglong add 20140916 增加ORDER_CODE
                            + "A.OPT_USER,A.OPT_DATE,A.OPT_TERM,0 AS USED_QTY,0 AS NOTUSED_QTY,'' AS DESCRIPTION, C.OWN_PRICE, A.ORDER_CODE "
                            + "  FROM SYS_ORDER_PACKD A, SYS_ORDER_PACKM B, SYS_FEE C "
                            + " WHERE A.PACK_CODE = '#'                       "
                            + "   AND A.PACK_CODE = B.PACK_CODE        "
                            + "   AND A.ORDER_CODE = C.ORDER_CODE(+)";
        } 
           // ===== wukai on 20161226  start
        else if(StringUtils.isEmpty(class_code)) {
        	//class_code 为空
        	 sql =
                     " SELECT A.PACK_CODE,A.ORDER_CODE AS INV_CODE,A.ORDER_DESC AS INV_CHN_DESC,A.DOSAGE_QTY AS QTY,A.DOSAGE_UNIT AS STOCK_UNIT,A.HIDE_FLG,"
                             //wanglong add 20140916 增加ORDER_CODE
                             + "A.OPT_USER,A.OPT_DATE,A.OPT_TERM,0 AS USED_QTY,0 AS NOTUSED_QTY,'' AS DESCRIPTION, C.OWN_PRICE, A.ORDER_CODE "
                             + "  FROM SYS_ORDER_PACKD A, SYS_ORDER_PACKM B, SYS_FEE C "
                             + " WHERE A.PACK_CODE = '#'                       "
                             + "   AND A.PACK_CODE = B.PACK_CODE        "
                             + "   AND A.ORDER_CODE = C.ORDER_CODE(+)";
        }
        	// ===== wukai on 20161226  end
        sql = sql.replaceFirst("#", barCode);
        return sql;
    }

    /**
     * 传回
     */
    public void onOK() {
        TParm retData = new TParm();
        tableD.acceptText();
        TParm parmValue = tableD.getParmValue();
        for (int i = 0; i < tableD.getRowCount(); i++) {
            if (parmValue.getBoolean("N_SEL", i)) {
                retData.addData("ORDER_CODE", parmValue.getValue("N_ORDER_CODE", i));// wanglong add 20141014
                retData.addData("INV_CODE", parmValue.getValue("N_INV_CODE", i));
                retData.addData("QTY", parmValue.getDouble("N_QTY", i));
                retData.addData("USED_QTY", parmValue.getDouble("N_USED_QTY", i));
                retData.addData("NOTUSED_QTY", parmValue.getDouble("N_NOTUSED_QTY", i));
            }
            if (parmValue.getBoolean("S_SEL", i)) {
                retData.addData("ORDER_CODE", parmValue.getValue("S_ORDER_CODE", i));// wanglong add 20141014
                retData.addData("INV_CODE", parmValue.getValue("S_INV_CODE", i));
                retData.addData("QTY", parmValue.getDouble("S_QTY", i));
                retData.addData("USED_QTY", parmValue.getDouble("S_USED_QTY", i));
                retData.addData("NOTUSED_QTY", parmValue.getDouble("S_NOTUSED_QTY", i));
            }
        }
        retData.setData("BAR_CODE", this.getValueString("PACK_CODE"));
        if (!this.getText("PACK1_CODE").equals("")) {//wanglong add 20140627
            retData.setData("PACK1_CODE", this.getText("PACK1_CODE"));
        }
        this.setReturnValue(retData);
        this.closeWindow();
    }

    /**
     * 取消
     */
    public void onCANCLE() {
        switch (this.messageBox("提示信息", "确定取消选择？", 0)) {
            case 0:
                this.closeWindow();
            case 1:
        }
    }

    /**
     * 得到TTextField对象
     * 
     * @param tagName
     *            String
     * @return TTextField
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) this.getComponent(tagName);
    }

    /**
     * “全选”事件
     */
    public void onSelAll() {
        tableD.acceptText();
        if (tableD == null) {
            return;
        }
        int row = tableD.getRowCount();
        if (all.isSelected()) {
            for (int i = 0; i < row; i++) {
                tableD.setItem(i, "N_SEL", true);
                tableD.setItem(i, "S_SEL", true);
            }
        } else {
            for (int i = 0; i < row; i++) {
                tableD.setItem(i, "N_SEL", false);
                tableD.setItem(i, "S_SEL", false);
            }
        }
        getTotFee();//计算“总计”
    }

    /**
     * 得到“总计”金额
     */
    public void getTotFee() { //wanglong add 20140609
        tableD.acceptText();
        TParm parmValue = tableD.getParmValue();
        int count = parmValue.getCount();
        double arAmt = 0;
        for (int i = 0; i < count; i++) {
            if (parmValue  .getValue("N_SEL", i).equals("Y")) {
                arAmt += parmValue.getDouble("N_QTY", i) * parmValue.getDouble("N_OWN_PRICE", i);
            }
            if (parmValue.getValue("S_SEL", i).equals("Y")) {
                arAmt += parmValue.getDouble("S_QTY", i) * parmValue.getDouble("S_OWN_PRICE", i);
            }
        }
        this.setValue("TOT_FEE", StringTool.round(arAmt, 2));
    }

    /**
     * 单元格值改变事件
     * 
     * @param tNode
     */
    public void onTableValueChange(TTableNode tNode) {
        getTotFee();//计算“总计”
        int row = tNode.getRow();
        int col = tNode.getColumn();
        String colName = tableD.getParmMap(col);
        TParm parmValue = tableD.getParmValue();
        //wanglong add 20140609
        if (colName.equals("N_SEL")) {//计算当前点击checkBox时，“总计”的变化
            if (tNode.getValue().equals("Y")) {
                double arAmt = this.getValueDouble("TOT_FEE");
                arAmt +=
                        tableD.getItemDouble(row, "N_QTY")
                                * parmValue.getDouble("N_OWN_PRICE", row);
                this.setValue("TOT_FEE", StringTool.round(arAmt, 2));//计算“总计”
            }
            if (tNode.getValue().equals("N")) {
                double arAmt = this.getValueDouble("TOT_FEE");
                arAmt -=
                        tableD.getItemDouble(row, "N_QTY")
                                * parmValue.getDouble("N_OWN_PRICE", row);
                this.setValue("TOT_FEE", StringTool.round(arAmt, 2));//计算“总计”
            }
        } else if (colName.equals("S_SEL")) {
            if (tNode.getValue().equals("Y")) {
                double arAmt = this.getValueDouble("TOT_FEE");
                arAmt +=
                        tableD.getItemDouble(row, "S_QTY")
                                * parmValue.getDouble("S_OWN_PRICE", row);
                this.setValue("TOT_FEE", StringTool.round(arAmt, 2));//计算“总计”
            }
            if (tNode.getValue().equals("N")) {
                double arAmt = this.getValueDouble("TOT_FEE");
                arAmt -=
                        tableD.getItemDouble(row, "S_QTY")
                                * parmValue.getDouble("S_OWN_PRICE", row);
                this.setValue("TOT_FEE", StringTool.round(arAmt, 2));//计算“总计”
            }
        }
        if (tNode.getColumn() == 3) {
            double oldValue = TypeTool.getDouble(tNode.getOldValue());
            double newValue = TypeTool.getDouble(tNode.getValue());
            double qty = TypeTool.getDouble(tableD.getValueAt(row, 2));
            double value = qty - newValue;
            if (newValue < 0) {
                this.messageBox("使用数量不能为负");
                tNode.setValue(oldValue);
                return;
            }
            if (newValue > qty) {
                this.messageBox("使用数量不能超过配置数量");
                tNode.setValue(oldValue);
                return;
            }
            tableD.setValueAt(value, row, 4);
        }
        if (tNode.getColumn() == 4) {
            double oldValue = TypeTool.getDouble(tNode.getOldValue());
            double newValue = TypeTool.getDouble(tNode.getValue());
            double qty = TypeTool.getDouble(tableD.getValueAt(row, 2));
            double value = qty - newValue;
            if (newValue < 0) {
                this.messageBox("剩余数量不能为负");
                tNode.setValue(oldValue);
                return;
            }
            if (newValue > qty) {
                this.messageBox("剩余数量不能超过配置数量");
                tNode.setValue(oldValue);
                return;
            }
            tableD.setValueAt(value, row, 3);
        }
        if (tNode.getColumn() == 10) {
            double oldValue = TypeTool.getDouble(tNode.getOldValue());
            double newValue = TypeTool.getDouble(tNode.getValue());
            double qty = TypeTool.getDouble(tableD.getValueAt(row, 9));
            double value = qty - newValue;
            if (newValue < 0) {
                this.messageBox("使用数量不能为负");
                tNode.setValue(oldValue);
                return;
            }
            if (newValue > qty) {
                this.messageBox("使用数量不能超过配置数量");
                tNode.setValue(oldValue);
                return;
            }
            tableD.setValueAt(value, row, 11);
        }
        if (tNode.getColumn() == 11) {
            double oldValue = TypeTool.getDouble(tNode.getOldValue());
            double newValue = TypeTool.getDouble(tNode.getValue());
            double qty = TypeTool.getDouble(tableD.getValueAt(row, 9));
            double value = qty - newValue;
            if (newValue < 0) {
                this.messageBox("剩余数量不能为负");
                tNode.setValue(oldValue);
                return;
            }
            if (newValue > qty) {
                this.messageBox("剩余数量不能超过配置数量");
                tNode.setValue(oldValue);
                return;
            }
            tableD.setValueAt(value, row, 10);
        }
    }

    /**
     * CHECK_BOX勾选事件
     * 
     * @param obj
     * @return
     */
    public boolean onCheckBoxClicked(Object obj) {
        TTable table = (TTable) obj;
        table.acceptText();
        return false;
    }
}
