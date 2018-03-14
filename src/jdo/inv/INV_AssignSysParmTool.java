package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class INV_AssignSysParmTool extends TJDOTool {
	  /**
     * 实例
     */
    public static INV_AssignSysParmTool instanceObject;

    /**
     * 构造器  
     */
    public INV_AssignSysParmTool() { 
        setModuleName("inv\\INV_AssignSysParmModule.x");
        onInit();
    }   

    /**
     * 得到实例
     * 
     * @return INV_AssignSysParmTool 
     */
    public static INV_AssignSysParmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INV_AssignSysParmTool();
        return instanceObject;
    }

    /**
     * 新增
     * @param parm TParm
     * @param conn TConnection 
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /** 
     * 更新Inv_sysParm
     * @param parm TParm
     * @param conn TConnection
     * @return TParm 
     */   
    public TParm onUpdateSysParm(TParm parm, TConnection conn) {
       TParm result = this.update("updatesysparm", parm, conn); 
       System.out.println("TOOl--result"+result);  
       if (result.getErrCode() < 0) {  
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName()); 
           return result;
       } 
       return result; 
   }
   /**  
     * 删除
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
       TParm result = this.update("delete", parm);
       if (result.getErrCode() < 0) {
           err("ERR:" + result.getErrCode() + result.getErrText()
               + result.getErrName());
           return result;
       }
       return result;
   }
}
