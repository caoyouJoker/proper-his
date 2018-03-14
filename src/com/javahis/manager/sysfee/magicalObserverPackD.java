package com.javahis.manager.sysfee;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import java.util.Map;
import java.util.HashMap;

/**
 * <p>Title: 观察医嘱套餐外调界面的细项虚幻的table</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JAVAHIS 1.0</p>
 *
 * @author ZangJH 2009-9-10
 * @version 1.0
 */
public class magicalObserverPackD
    extends TObserverAdapter {

    Map selFlg = new HashMap();
    Map qtyMap = new HashMap();

    public magicalObserverPackD() {
    }


    /**
     * 得到其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS odiOrder, TParm tParm, int row,
                                      String column) {

        TDS realDS = this.getDS();
        if (column.equals("N_SEL")) {
            return selFlg.get(row * 2 + "");
        }
        if (column.equals("N_ORDER_DESC")) {
            return realDS.getItemData(row * 2, "ORDER_DESC");
        }
        if (column.equals("N_DOSAGE_QTY")) {
            if(qtyMap.get( (row * 2 ) + "")==null){
                return realDS.getItemData((row * 2 ), "DOSAGE_QTY");
            }
            return qtyMap.get( (row * 2 ) + "");

        }
        if (column.equals("N_DOSAGE_UNIT")) {
            return realDS.getItemData(row * 2, "DOSAGE_UNIT");
        }

        if (column.equals("S_SEL")) {
            return selFlg.get( (row * 2 + 1) + "");
        }
        if (column.equals("S_ORDER_DESC")) {
            return realDS.getItemData((row * 2 + 1), "ORDER_DESC");
        }
        if (column.equals("S_DOSAGE_QTY")) {
            //当table要刷新从这个拿数据的时候，首先会看数是否有该数据，如果没有显示从数据库查询出来的值
            if(qtyMap.get( (row * 2 + 1) + "")==null){
                return realDS.getItemData((row * 2 + 1), "DOSAGE_QTY");
            }
            return qtyMap.get( (row * 2 + 1) + "");
        }
        if (column.equals("S_DOSAGE_UNIT")) {
            return realDS.getItemData((row * 2 + 1), "DOSAGE_UNIT");

        }
        return "";
    }


    /**
     * 设置其他列数据
     * @param parm TParm
     * @param row int 激发该动作的行(table)
     * @param column String 激发该动作的列(table)
     * @param value Object
     * @return boolean 该列目前的值
     */

    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
                                       String column, Object value) {
        /**
         * 只是用于两组
         */
        //N_SEL的时候以在TDS中的行数为KEY（TDS中行数=table上的行数*2）
        if (column.equals("N_SEL")) {
            selFlg.put(row * 2 + "", value);
        }
        //S_SEL的时候以在TDS中的行数为KEY（TDS中行数=table上的行数*2+1）
        if (column.equals("S_SEL")) {
            selFlg.put( (row * 2 + 1) + "", value);
        }

        if (column.equals("N_DOSAGE_QTY")) {
            //用MAP存值，以便get时使用这个临时假值
            qtyMap.put(row * 2 + "", value);
            //真正修改ds中的值，以便contorl得到
            this.getDS().setItem(row * 2 , "DOSAGE_QTY", value);
        }

        if (column.equals("S_DOSAGE_QTY")) {
            qtyMap.put( (row * 2 + 1) + "", value);
            this.getDS().setItem(row * 2 + 1, "DOSAGE_QTY", value);
        }

        return true;
    }

}
