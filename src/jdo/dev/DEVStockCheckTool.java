package jdo.dev;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

public class DEVStockCheckTool extends TJDOTool {
    /**
     * 实例
     */
    public static DEVStockCheckTool instanceObject;
 
    /**
     * 得到实例
     *
     * @return DEVStockCheckTool  
     */
    public static DEVStockCheckTool getInstance() {
        if (instanceObject == null)
            instanceObject = new DEVStockCheckTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public DEVStockCheckTool() {
        setModuleName("dev\\DEVStockCheckModule.x");
        onInit();
    }
    /**
     * 更新实际数量和调整量
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());        
            return result;
        }
        return result;
    }
    /**
     *新增实际数量和调整量
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm ) {
        TParm result = this.update("Insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
    * 查询
    * @param parm
    * @return
    */
   public TParm onQuery(TParm parm) {
       TParm result = this.query("query", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
}
