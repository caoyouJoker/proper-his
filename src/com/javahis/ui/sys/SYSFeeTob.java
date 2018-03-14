package com.javahis.ui.sys;
import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.jdo.TDataStore;
import java.util.Vector;
/**
 *
 * <p>Title: sysfee观察者</p>
 *
 * <p>Description: sysfee观察者</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-7-13
 * @version 1.0
 */
public class SYSFeeTob extends TObserverAdapter{
    /**
         * 空参实例化
         */
        public SYSFeeTob()
       {
       }
       //从本地sysfee中拿到所有order
       TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");
       /**
         * 侦听得到数据事件
         * @param ds TDS
         * @param parm TParm
         * @param row int
         * @param column String
         * @return Object
         */
        public Object getOtherColumnValue(TDS ds, TParm parm, int row, String column)
        {
            //确认
            if("ORDER_DESC".equals(column))
            {
                String s = ds.getItemString(row, "ITEM_CODE");
                String orderDesc=getTableShowValue(s,dataStore);
                if(orderDesc != null && orderDesc.length() > 0){
                    return orderDesc;
                }
                return s;
            }
            return null;
        }
        /**
         * 代码换名称
         * @param s String
         * @param dataStore TDataStore
         * @return String
         */
        public String getTableShowValue(String s,TDataStore dataStore) {
         if (dataStore == null)
             return s;
         String bufferString = dataStore.isFilter() ? dataStore.FILTER :
             dataStore.PRIMARY;
         TParm parm = dataStore.getBuffer(bufferString);
         Vector v = (Vector) parm.getData("ORDER_CODE");
         Vector d = (Vector) parm.getData("ORDER_DESC");
         int count = v.size();
         for (int i = 0; i < count; i++) {
             if (s.equals(v.get(i)))
                 return "" + d.get(i);
         }
         return s;
     }



}
