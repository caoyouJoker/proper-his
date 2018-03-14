package jdo.dev;

import com.dongyang.jdo.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: 请购作业</p>  
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author  fux   
 * @version 1.0
 */
public class DevPurChaseTool extends TJDODBTool {
    /**
     * 实例
     */
    public static DevPurChaseTool instanceObject;
    /**
     * 得到实例
     * @return RuleTool
     */
    public static DevPurChaseTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DevPurChaseTool();
        return instanceObject;
    }
   /**
    *  保存
    * @param parm TParm
    * @param con TConnection
    * @return TParm
    */
   public TParm saveDevPurChase(TParm parm,TConnection con){
       String sqlStr[] = (String[])parm.getData("SQL");
       TParm result = new TParm( this.update(sqlStr,con));
       return result;  
   }

}
