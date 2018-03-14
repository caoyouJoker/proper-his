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
 * <p>Title: ODI_ORDER表自观察，以备将来支持PDA执行使用</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: 2008</p>
 *
 * <p>Company: JAVAHIS 1.0</p>
 *
 * @author ZangJH
 * @version 1.0
 */
public class OdiOrderSelfObserver
    extends TObserverAdapter {
    public OdiOrderSelfObserver() {
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
        if ("NS_EXEC_DATE_DAY".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            if (date == null)
                return "";
            //给这个列一个值
            return StringTool.getString(date, "yyyy/MM/dd");
        }
        //审核时间
        if ("NS_EXEC_DATE_TIME".equals(column)) {
            Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            if (date == null)
                return "";
            return StringTool.getString(date, "HH:mm:ss");
        }
        //执行标记
        if ("EXE_FLG".equals(column)) {
            this.getDS().showDebug();
            //获得护士审核时间
            //Timestamp date = tParm.getTimestamp("NS_EXEC_DATE", row);
            //PS：这个捞取用于判断的“列”必须是表上显示的列（不可以是setItem中设置但表上没有的）
            String date=tParm.getValue("NS_EXEC_CODE", row);
            //当护士名字为空的时候说明没有审核，审核标记为‘N’
            if (date == null){
                return false;
            }
            return true;
        }

        //药品名称+规格ORDER_DESC_AND_SPECIFICATION
        if ("ORDER_DESC_AND_SPECIFICATION".equals(column)) {
            //获得药品名称
            String desc = tParm.getValue("ORDER_DESC", row);
            //获得药品规格
            String specification = tParm.getValue("SPECIFICATION", row);

            return specification.equals("") ? desc :
                desc + "(" + specification + ")";

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

    public boolean setOtherColumnValue(TDS odiDspnd, TParm parm, int row,
                                       String column, Object value) {
        //查看执行标记
        if ("EXE_FLG".equals(column)) {
            //判断执行标记是否为真
            if (TypeTool.getBoolean(value)) {
                odiDspnd.setItem(row, "NS_EXEC_CODE", Operator.getID());
                //获得大对象设置的时间
                Timestamp time = TJDODBTool.getInstance().getDBTime();
                odiDspnd.setItem(row, "NS_EXEC_DATE", time);
                odiDspnd.setItem(row, "EXEC_DEPT_CODE", Operator.getDept());
            }
            else {
                odiDspnd.setItem(row, "NS_EXEC_CODE", null);
                odiDspnd.setItem(row, "NS_EXEC_DATE", null);
                odiDspnd.setItem(row, "EXEC_DEPT_CODE", null);
            }
            return true;
        }
        return true;
    }

}
