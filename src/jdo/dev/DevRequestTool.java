package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class DevRequestTool extends TJDODBTool {
    /**
     * 实例
     */
    public static DevRequestTool instanceObject;
    /**
     * 得到实例
     * @return RuleTool
     */
    public static DevRequestTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DevRequestTool(); 
        return instanceObject;
    }
   /** 
    * 保存
    * @param parm TParm
    * @param con TConnection
    * @return TParm
    */ 
   public TParm saveDevRequest(TParm parm,TConnection con){
       String sqlStr[] = (String[])parm.getData("SQL");  
       TParm result = new TParm( this.update(sqlStr,con));
       return result;
   }

}
