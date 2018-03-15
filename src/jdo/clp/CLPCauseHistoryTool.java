package jdo.clp;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
/**
 * <p>Title: 临床路径不进入原因字典</p>
 *
 * <p>Description: 临床路径不进入原因字典</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author pangben 20150811
 * @version 1.0
 */
public class CLPCauseHistoryTool extends TJDOTool {
	 /**
     * 实例
     */
    private static CLPCauseHistoryTool instanceObject;
    /**
     * 初始化类对象
     * @return ClpchkTypeTool
     */
    public static CLPCauseHistoryTool getInstance() {
        if (null == instanceObject)
            instanceObject = new CLPCauseHistoryTool();
        return instanceObject;
    }
    /**
    * 构造方法
    */
   public CLPCauseHistoryTool() {
       this.setModuleName("clp\\CLPCauseHistoryModule.x");
       onInit();
   }
   /**
    * 
   * @Title: insertClpCauseHistory
   * @Description: TODO(添加不进入原因)
   * @author pangben
   * @param parm
   * @param conn
   * @return
   * @throws
    */
   public TParm insertClpCauseHistory(TParm parm,TConnection conn){
	   TParm result = this.update("insertClpCauseHistory", parm,conn);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText() +
               result.getErrName());
           return result;
       }
       return result;
   }
   /**
    * 
   * @Title: queryCauseHistory
   * @Description: TODO(查询数据)
   * @author pangben
   * @param parm
   * @return
   * @throws
    */
   public TParm queryCauseHistory(TParm parm){
	   TParm result = this.query("queryCauseHistory", parm);
       return result;
   }
   /**
    * 
   * @Title: queryMaxSeqNo
   * @Description: TODO(查询最大序号)
   * @author pangben
   * @param parm
   * @return
   * @throws
    */
   public TParm queryMaxSeqNo(TParm parm){
	   TParm result = this.query("queryMaxSeqNo", parm);
       return result;
   }
}
