package com.javahis.ui.inv;

import com.dongyang.ui.TLabel;
import java.util.Vector;
import com.dongyang.jdo.TDataStore;
import com.dongyang.data.TParm;
/**
 *
 * <p>Title: 物资的代码换名称</p>
 *
 * <p>Description: 物资的代码换名称</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 09--5-20
 * @version 1.0
 */
public class INVBaseList extends TLabel{
    public String getTableShowValue(String s,TDataStore dataStore) {
         if (dataStore == null)
             return s;
         String bufferString = dataStore.isFilter() ? dataStore.FILTER :
             dataStore.PRIMARY;
         TParm parm = dataStore.getBuffer(bufferString);
         Vector v = (Vector) parm.getData("INV_CODE");
         Vector d = (Vector) parm.getData("INV_CHN_DESC");
//         Vector dis=(Vector) parm.getData("DESCRIPTION");
         int count = v.size();
         for (int i = 0; i < count; i++) {
             if (s.equals(v.get(i)))
//                 return "" + d.get(i)+" "+dis.get(i);
            	 return "" + d.get(i) + "";
         }
         return s;
     }

}
