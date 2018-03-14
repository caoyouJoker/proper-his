package com.javahis.manager;

import java.util.Vector;

import jdo.ind.INDTool;
import jdo.ind.IndVerifyinDTool;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;

/**
 * <p>
 * Title: 药库部门退货管理观察者
 * </p>
 *
 * <p>
 * Description: 药库部门退货管理观察者
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

public class IndRegressgoodsObserver
    extends TObserverAdapter {

    // 获得prefech本地数据
    TDataStore dataStore;

    String org_code = "";

    public IndRegressgoodsObserver() {
        dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT ORDER_CODE ,ORDER_DESC ,SPECIFICATION FROM SYS_FEE");
        dataStore.retrieve();
    }

    public IndRegressgoodsObserver(String org_code) {
        this.org_code = org_code;
        dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT ORDER_CODE ,ORDER_DESC ,SPECIFICATION FROM SYS_FEE");
        dataStore.retrieve();
    }

    /**
     *
     * @param s
     *            根据的ORDER_CODE
     * @param colName
     *            要查的列名
     * @return String
     */
    public String getTableShowValue(String s, String colName) {
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER
            : dataStore.PRIMARY;
        TParm parm = dataStore.getBuffer(bufferString);
        Vector vKey = (Vector) parm.getData("ORDER_CODE");
        Vector vDesc = (Vector) parm.getData(colName);
        int count = vKey.size();
        for (int i = 0; i < count; i++) {
            if (s.equals(vKey.get(i)))
                return "" + vDesc.get(i);
        }
        return s;
    }

    /**
     * 得到其他列数据
     *
     * @param parm
     *            TParm
     * @param row
     *            int
     * @param column
     *            String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm parm, int row,
                                      String column) {
        //System.out.println("parm===" + parm);
        if ("SELECT_FLG".equals(column)) {
            if ("".equals(parm.getValue("UPDATE_FLG", row))) {
                return "N";
            }
            else {
                return "Y";
            }
        }
        if ("ORDER_DESC".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "ORDER_DESC");
            if ("".equals(icd_name))
                return "";
            // 给这个列一个值
            return icd_name;
        }
        // 药品规格
        if ("SPECIFICATION".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String icd_name = getTableShowValue(orderCode, "SPECIFICATION");
            if ("".equals(icd_name))
                return "";
            // 给这个列一个值
            return icd_name;
        }
        // 零售金额
        if ("SELL_SUM".equals(column)) {
            double price = parm.getDouble("RETAIL_PRICE", row);
            double amount1 = parm.getDouble("QTY", row);
            return StringTool.round(price * amount1, 2);
        }

        // 进销差价
        if ("DIFF_SUM".equals(column)) {
            double price = parm.getDouble("RETAIL_PRICE", row);
            double amount1 = parm.getDouble("QTY", row);
            double amt1 = StringTool.round(price * amount1, 2);
            double amt2 = parm.getDouble("AMT", row);
            return amt1 - amt2;
        }

        // 库存量
        if ("SUM_QTY".equals(column)) {
            String orderCode = parm.getValue("ORDER_CODE", row);
            String batch_no = parm.getValue("BATCH_NO", row);
            if ("".equals(parm.getValue("VALID_DATE", row))) {
                return 0;
            }
            else {
                String valid_date = parm.getValue("VALID_DATE", row).substring(0,
                    10);
                double qty = INDTool.getInstance().getStockQTY(org_code, orderCode,
                    batch_no, valid_date, Operator.getRegion());
                //System.out.println("org_code---" + org_code);
                //System.out.println("qty---" + qty);
                return qty;
            }
        }

        // 累计退货数
        if ("VER_ACTUAL_QTY".equals(column)) {
            String ver_no = parm.getValue("VERIFYIN_NO", row);
            String ver_seq = parm.getValue("VERSEQ_NO", row);
            TParm inparm = new TParm();
            inparm.setData("VERIFYIN_NO", ver_no);
            inparm.setData("SEQ_NO", ver_seq);
            TParm result = IndVerifyinDTool.getInstance().onQuery(inparm);
            return result.getDouble("ACTUAL_QTY", 0);
        }
        //luhai 2012-2-13 加入成本金额 begin
        if ("VERIFYIN_AMT".equals(column)) {
        	System.out.println("=============1");
        	double verifyinPrice = parm.getDouble("VERIFYIN_PRICE",row);
        	System.out.println("=============2"+verifyinPrice);
        	double qty  = parm.getDouble("QTY",row);
        	System.out.println("=============3"+qty);
        	return StringTool.round(verifyinPrice*qty, 2)+"";
        }
        //luhai 2012-2-13 加入成本金额 begin
        return "";
    }

    /**
     * 设置其他列数据
     *
     * @param parm
     *            TParm
     * @param row
     *            int 激发该动作的行(table)
     * @param column
     *            String 激发该动作的列(table)
     * @param value
     *            Object
     * @return boolean 该列目前的值
     */
    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
                                       String column, Object value) {
        if (column.equals("ORDER")) {
            ds.setItem(row, "ORDER_CODE", parm.getValue("ORDER", row));
            return true;
        }
        if (column.equals("SELECT_FLG")) {
            if ("Y".equals(TypeTool.getString(value))) {
                ds.setActive(row, true);
            }
            else {
                ds.setActive(row, false);
            }
            return true;
        }
        return true;
    }
}
