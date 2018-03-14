package com.javahis.manager;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.javahis.util.OrderUtil;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author WangM
 * @version 1.0
 */
public class OdiOtherObserver extends TObserverAdapter {
    /**
     * 得到未知列值
     * @param ds TDS
     * @param parm TParm
     * @param row int
     * @param column String
     * @return Object
     */
    public Object getOtherColumnValue(TDS ds, TParm parm, int row,
                                      String column) {
//        System.out.println("值@@@@@@@@@@@@@:"+column);

        if("EFF_DATEDAY".equals(column)){
            TParm action = parm.getRow(row);
//            System.out.println("EFF_DATEDAY"+action);
            String effDate = StringTool.getString(action.getTimestamp("EFF_DATE"),"yyyy/MM/dd HH:mm:ss");
            return effDate;
        }
        if("ORDER_DESCCHN".equals(column)){
            TParm action = parm.getRow(row);
            String orderDescChn = action.getValue("ORDER_DESC")+action.getValue("GOODS_DESC")+action.getValue("DESCRIPTION")+action.getValue("SPECIFICATION");
            return orderDescChn;
        }
        return null;
    }
    /**
     * 设置未知列值
     * @param ds TDS
     * @param parm TParm
     * @param row int
     * @param column String
     * @param value Object
     * @return boolean
     */
    public boolean setOtherColumnValue(TDS ds, TParm parm, int row,
                                       String column, Object value) {
//        System.out.println("列:"+column);
        if("EFF_DATEDAY".equals(column)){
//            System.out.println("EFF_DATEDAYPARM"+parm);
//            System.out.println("EFF_DATEDAYVALUE"+value);
            ds.setItem(row,"EFF_DATE",StringTool.getTimestamp(""+value,"yyyy/MM/dd HH:mm:ss"));
        }
        return false;
    }
}
