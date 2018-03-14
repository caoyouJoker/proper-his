package com.javahis.manager.sysfee;

import com.dongyang.jdo.TDS;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TObserverAdapter;
import java.util.Vector;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;

/**
 * <p>Title: SYS_FEE界面中的录入医嘱套餐细项观察</p>
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
public class sysOdrPackDObserver
    extends TObserverAdapter {
    public sysOdrPackDObserver() {

    }

    //获得prefech本地数据
    TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
    /**
     *
     * @param s 根据的ORDER_CODE
     * @param colName 要查的列名
     * @return String
     */
    public String getTableShowValue(String s, String colName) {
        if (dataStore == null)
            return s;
        String bufferString = dataStore.isFilter() ? dataStore.FILTER :
            dataStore.PRIMARY;
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
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm tParm, int row,
                                      String column) {

        String result = "";
        if (column.equals("N_DEL")) {
            return "";
        }
        if (column.equals("N_TOTFEE")) {
            String orderCode = tParm.getValue("ORDER_CODE", row);
            double ownPrice = TypeTool.getDouble(getTableShowValue(orderCode,
                "OWN_PRICE"));
            double qty = tParm.getDouble("DOSAGE_QTY", row);
            
            return StringTool.round(ownPrice * qty, 2);
        }
        return result;

    }

    /**
     * 设置其他列数据
     * @param parm TParm
     * @param row int 激发该动作的行(table)
     * @param column String 激发该动作的列(table)
     * @param value Object
     * @return boolean 该列目前的值
     */

//    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
//                                       String column, Object value) {
//        if (column.equals("N_DEL")) {
//            if (TypeTool.getBoolean(value)) {
//                ds.deleteRow(row);
//            }
//            return true;
//        }
//        return true;
//    }

}
