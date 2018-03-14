package com.javahis.ui.inv;

import com.dongyang.data.TParm;
import java.util.Vector;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TLabel;
/**
 *
 * <p>Title:手术包code换desc </p>
 *
 * <p>Description:手术包code换desc </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:javahis </p>
 *
 * @author fudw 2009-5-21
 * @version 1.0
 */
public class INVPackList extends TLabel{
    public String getTableShowValue(String s,TDataStore dataStore) {
         if (dataStore == null)
             return s;
         String bufferString = dataStore.isFilter() ? dataStore.FILTER :
             dataStore.PRIMARY;
         TParm parm = dataStore.getBuffer(bufferString);
         Vector v = (Vector) parm.getData("PACK_CODE");
         Vector d = (Vector) parm.getData("PACK_DESC");
         int count = v.size();
         for (int i = 0; i < count; i++) {
             if (s.equals(v.get(i)))
                 return "" + d.get(i);
         }
         return s;
     }

}
