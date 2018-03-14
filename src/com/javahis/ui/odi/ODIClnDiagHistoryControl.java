package com.javahis.ui.odi;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 临床诊断操作历史 </p>
 *
 * <p> Description: 临床诊断操作历史 </p>
 *
 * <p> Copyright: Copyright (c) 2014 </p>
 *
 * <p> Company: BlueCore </p>
 *
 * @author WangLong 2014.04.16
 * @version 1.0
 */
public class ODIClnDiagHistoryControl extends TControl {
    private TTable table;
    
    /**
     * 初始化方法
     */
    public void onInit() {
        super.onInit();
        TParm parm = this.getInputParm();
        if (null == parm) {
            return;
        }
        table = (TTable) this.getComponent("TABLE");
        TDataStore tds = new DiagHistoryTDS();
        tds.setSQL("SELECT * FROM ADM_INPDIAG_LOG WHERE CASE_NO='" + parm.getValue("CASE_NO") + "' ORDER BY ROWID");
        tds.retrieve();
        table.setDataStore(tds);
        table.setDSValue();
    }
    
    class DiagHistoryTDS
            extends TDataStore {

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
        public Object getOtherColumnValue(TParm parm, int row, String column) {
            if ("ROWNUM".equalsIgnoreCase(column)) {
                return row + "";
            }
            if ("ICD_DESC".equalsIgnoreCase(column)) {
                return StringUtil.getDesc("SYS_DIAGNOSIS", "ICD_CHN_DESC",
                                          "ICD_CODE='" + parm.getValue("ICD_CODE", row) + "'");
            }
            return "";
        }
    }
}
