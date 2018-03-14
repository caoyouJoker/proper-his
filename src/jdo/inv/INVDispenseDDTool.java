package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 * 
 * @author fux
 * @version 1.0
 */
public class INVDispenseDDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INVDispenseDDTool instanceObject;

    /**
     * 构造器
     */
    public INVDispenseDDTool() {
        setModuleName("inv\\INVDispenseDDModule.x");
        onInit();
    } 

    /**
     * 得到实例
     *
     * @return InvVerifyinDTool
     */
    public static INVDispenseDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVDispenseDDTool();
        return instanceObject; 
    }

    /**
     * 查询验收序号管理细项数据
     * @param parm TParm 
     * @return TParm
     */
    public TParm onQuery(TParm parm) {  
        TParm result = this.query("queryDispenseDD", parm); 
        System.out.println("queryDispenseDD =---result"+result);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;     
        }    
        return result;
    }

    /**
     * 新增验收序号管理细项数据
     *
     * @param parm 
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {  
        TParm result = this.update("createDispenseDD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /** 
     * 删除入库细项
     * @param parm TParm 
     * @return TParm
     */
    public TParm onDelete(TParm parm) {  
    	//module未完成       
        TParm result = this.update("deleteDispenseinD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    

}
