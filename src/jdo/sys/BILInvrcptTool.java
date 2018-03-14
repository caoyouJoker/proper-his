package jdo.sys;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILInvrcptTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILInvrcptTool instanceObject;
    /**
     * 得到实例
     * @return BILInvrcptTool
     */
    public static BILInvrcptTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILInvrcptTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILInvrcptTool() {
        setModuleName("sys\\BILInvrcptModule.x");
        onInit();
    }
    /**
     * 查询全部数据明细
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectData",parm);
        if(result.getErrCode() < 0)
        {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
   /**
    * 打印票据
    * @return TParm
    */
   public TParm insertData(TParm parm,TConnection connection) {
       TParm result = update("insertData",parm,connection);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
    * 更新，作废等
    * @return TParm
    */
   public TParm updataData(TParm parm,TConnection connection) {
       TParm result = update("updataData",parm,connection);
       if(result.getErrCode() < 0)
       {
           err(result.getErrCode() + " " + result.getErrText());
           return result;
       }
       return result;
   }
   /**
      * 更新数据数据
      * @return TParm
      */
     public TParm updataData(TParm parm) {
         TParm result = update("updataData",parm);
         if(result.getErrCode() < 0)
         {
             err(result.getErrCode() + " " + result.getErrText());
             return result;
         }
         return result;
   }

}
