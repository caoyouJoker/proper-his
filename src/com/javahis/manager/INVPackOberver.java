package com.javahis.manager;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.jdo.TDS;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class INVPackOberver extends TObserverAdapter {

    TDataStore dataStore ;

    public INVPackOberver() {
        dataStore = new TDataStore();
        dataStore.setSQL(
            "SELECT INV_CODE ,INV_CHN_DESC, DESCRIPTION FROM INV_BASE");
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
        Vector vKey = (Vector) parm.getData("INV_CODE");
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
        if ("INV_CHN_DESC".equals(column)) {
            String orderCode = parm.getValue("INV_CODE", row);
            String icd_name = getTableShowValue(orderCode, "INV_CHN_DESC");
            if ("".equals(icd_name))
                return "";
            // 给这个列一个值
            return icd_name;
        }
        if ("SPECIFICATION".equals(column)) {
            String orderCode = parm.getValue("INV_CODE", row);
            String icd_name = getTableShowValue(orderCode, "DESCRIPTION");
            if ("".equals(icd_name))
                return "";
            // 给这个列一个值
            return icd_name;
        }
        return "";
    }

}
