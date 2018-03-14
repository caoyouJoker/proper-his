package com.javahis.manager;

import java.util.Vector;

import jdo.spc.IndPurorderDTool;
									
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import jdo.spc.INDTool;

/** 
 * <p>
 * Title: 药库部门验收入库观察者
 * </p>
 *
 * <p>
 * Description: 药库部门验收入库观察者
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

public class IndVerifyinObserver
    extends TObserverAdapter {

    // 获得prefech本地数据
    TDataStore dataStore;

    public IndVerifyinObserver() {
        dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT ORDER_CODE ,ORDER_DESC ,SPECIFICATION, GOODS_DESC FROM PHA_BASE");
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
            String goods_desc = getTableShowValue(orderCode, "GOODS_DESC");
            icd_name = INDTool.getInstance().getOrderDescAndGoodsDesc("",
                icd_name, goods_desc);
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
            double amount1 = parm.getDouble("VERIFYIN_QTY", row);
            double amount2 = parm.getDouble("GIFT_QTY", row);
            return StringTool.round(price * (amount1 + amount2), 2);
        }
        // 进销差价
        if ("DIFF_SUM".equals(column)) {
            double price = parm.getDouble("RETAIL_PRICE", row);
            double amount1 = parm.getDouble("VERIFYIN_QTY", row);
            double amount2 = parm.getDouble("GIFT_QTY", row);
            double amt1 = StringTool.round(price * (amount1 + amount2), 2);
            double amt2 = parm.getDouble("INVOICE_AMT", row);
            return amt1 - amt2;
        }
        if ("ACTUAL".equals(column)) {
            String purorder_no = parm.getValue("PURORDER_NO", row);
            int purseq_no = parm.getInt("PURSEQ_NO", row);
            TParm inparm = new TParm();
            inparm.setData("PURORDER_NO", purorder_no);
            inparm.setData("SEQ_NO", purseq_no);
            TParm result = IndPurorderDTool.getInstance().onQuery(inparm);
            return result.getDouble("ACTUAL_QTY", 0);
        }
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
