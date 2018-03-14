package com.javahis.manager.inw;


import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import jdo.sys.Operator;
import java.sql.Timestamp;

/**
 * <p>Title: SUM_VITALSIGN(体温单主项)观察SUM_VTSNTPRDTL(体温单细项)</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:JAVAHIS 1.0 </p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class SumVitalSignObserverVtsntprdtl
    extends TObserverAdapter {
    public SumVitalSignObserverVtsntprdtl() {
    }

    /**
     * 插入数据的时候调用
     * @param ds TDS
     * @param row int
     * @return int
     */
    public int insertRow(TDS ds, int row) {

        if (this.getDS() == null)
            return 0;
        //目标TDS(SUM_VITALSIGN表)
        TDS sumVitalSign = getDS();
        if(row==5){
            int rowNub = sumVitalSign.insertRow();
            sumVitalSign.setItem(rowNub,"EXAMINE_DATE",(String)sumVitalSign.getAttribute("EXAMINE_DATE"));
            sumVitalSign.setItem(rowNub,"USER_ID",sumVitalSign.getAttribute("OPT_USER"));
        }


        return 0;
    }


    /**
     * 更新操作(执行模块)
     * @param dspnm TDS
     * @param row int
     * @param flg String 为“Y”是执行/“N”是取消执行
     * @return int
     */

    public int updateRow(TParm dspnm, int row, String flg, Timestamp now) {
        if (this.getDS() == null)
            return 0;
        //目标TDS
        TDS dspnd = getDS();
        dspnd.showDebug();
        int count = dspnd.rowCount();

        //循环插入每一列数据
        for (int i = 0; i < count; i++) {

            //执行的时候对ODI_DSPND的动作
            if (flg.equals("Y")) {
                dspnd.setItem(i, "NS_EXEC_CODE", Operator.getID());
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
            else { //取消执行的时候对ODI_DSPND的动作
                dspnd.setItem(i, "NS_EXEC_CODE", null);
                dspnd.setItem(i, "NS_EXEC_DATE", now);
            }
        }
        dspnd.showDebug();
        return 0;
    }


    /**
     * 删除数据
     * @param ds TDS
     * @param row int
     * @return boolean
     */
    public boolean deleteRow(TDS ds, int row) {
        return false;
    }

    /**
     * 根据列名字修改对应列的值
     * @param colNames String
     * @return boolean
     */
    public boolean updateDate(String colNames) {
        return false;
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

        return "";
    }


    /**
     * 设置其他列数据
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean 该列目前的值
     */

    public boolean setOtherColumnValue(TDS odiDspnm, TParm parm, int row,
                                       String column, Object value) {

        return true;
    }

}
