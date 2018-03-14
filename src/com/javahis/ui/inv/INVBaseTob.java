package com.javahis.ui.inv;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: 手术包字典观察者</p>
 *
 * <p>Description:手术包字典观察者 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw
 * @version 1.0
 */
public class INVBaseTob extends TObserverAdapter{
    /**
         * 空参实例化
         */
        public INVBaseTob(TDataStore dataStore)
       {
           this.dataStore=dataStore;
       }
       //从本地sysfee中拿到所有order
       TDataStore dataStore ;
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
            if("INV_CHN_DESC".equals(column))
            {
                String invCode = ds.getItemString(row, "INV_CODE");
                INVBaseList inv=new INVBaseList();
                String invDesc=inv.getTableShowValue(invCode,dataStore);
                if(invDesc != null && invDesc.length() > 0){
                    return invDesc;
                }
                return invCode;
            }
            return null;
        }



}
