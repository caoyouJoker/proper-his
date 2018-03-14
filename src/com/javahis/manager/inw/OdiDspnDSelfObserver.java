package com.javahis.manager.inw;

import com.dongyang.jdo.TObserverAdapter;
import jdo.sys.Operator;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import com.dongyang.jdo.TDS;
import java.sql.Timestamp;
import com.dongyang.util.TypeTool;

/**
 * <p>Title: ODI_DSPND自监听(假列使用)  写护士备注时候也会使用</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:  2008</p>
 *
 * <p>Company: JAVAHIS 1.0</p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class OdiDspnDSelfObserver
    extends TObserverAdapter {
    public OdiDspnDSelfObserver() {
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
        //如果该列是数据库中本身没有的实际列
        //审核日期
        if ("ORDER_DATE_DAY".equals(column)) {
            String date = tParm.getValue("ORDER_DATE", row);
            if (date == null)
                return "";
            //给这个列一个值
            return date.substring(0,4)+"/"+date.substring(4,6)+"/"+date.substring(6,8);
        }
        //审核时间
        if ("ORDER_DATE_TIME".equals(column)) {
            String time = tParm.getValue("ORDER_DATE", row);
            if (time == null)
                return "";
            return time.substring(8,10)+":"+time.substring(10,12);
        }
        //执行标记
        if ("EXE_FLG".equals(column)) {
            System.out.println("执行标记2");
            //获得护士审核时间
            //Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            String nsExeCode=tParm.getValue("NS_EXEC_CODE", row);
            //当为空的时候说明没有审核，审核标记为‘N’
            if ("".equals(nsExeCode))
                return false;

            return true;
        }

        return "";
    }

    /**
     * 当非数据库表的列激发时调用
     * @param odiOrder TDS ODI_ORDER表
     * @param parm TParm 所有的数据TParm形式
     * @param row int 修改的行数
     * @param column String 被修改的那列名（假列）
     * @param value Object 假列的值
     * @return boolean
     */

    public boolean setOtherColumnValue(TDS odiOrder, TParm parm, int row,
                                       String column, Object value) {
        //查看执行标记
        if ("EXE_FLG".equals(column)) {
            //判断执行标记是否为真
            if (TypeTool.getBoolean(value)) {
                Timestamp time = TJDODBTool.getInstance().getDBTime();
                odiOrder.setItem(row, "NS_EXEC_CODE", Operator.getID());
                //odiOrder.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
                odiOrder.setItem(row, "NS_EXEC_DATE", time);
                odiOrder.setActive(row,true);
            }
            else {
                odiOrder.setItem(row, "NS_EXEC_CODE", null);
                //odiOrder.setItem(row, "EXEC_DEPT_CODE", null);
                odiOrder.setItem(row, "NS_EXEC_DATE", null);
                odiOrder.setActive(row,false);
            }
            return true;
        }
        return true;
    }

}
