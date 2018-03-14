package com.javahis.ui.inv;

import com.dongyang.jdo.TObserverAdapter;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
/**
 *
 * <p>Title:手术包观察者 </p>
 *
 * <p>Description: 手术包观察者</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw 2009-5-21
 * @version 1.0
 */
public class INVPackTob extends TObserverAdapter{
    /**
         * 空参实例化
         */
        public INVPackTob(TDataStore dataStore)
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
            if("PACK_DESC".equals(column))
            {
                String invCode = ds.getItemString(row, "PACK_CODE");
                if(invCode==null||invCode.length()==0)
                    invCode = ds.getItemString(row, "INV_CODE");
                if(invCode==null||invCode.length()==0)
                    return "";
                INVPackList pack=new INVPackList();
                String packDesc=pack.getTableShowValue(invCode,dataStore);
                if(packDesc != null && packDesc.length() > 0){
                    return packDesc;
                }
                return invCode;
            }
            return null;
        }



}
