package jdo.inv;

import com.dongyang.jdo.TJDOTool;
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
 * @author  fux  
 * @version 1.0
 */
public class InvDispenseDTool
    extends TJDOTool {
    /**
     * 实例  
     */    
    public static InvDispenseDTool instanceObject;

    /**
     * 构造器
     */
    public InvDispenseDTool() {
        setModuleName("inv\\INVDispenseDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvDispenseDTool
     */
    public static InvDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvDispenseDTool();
        return instanceObject; 
    }

    /**
     * 新增出库单细项
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询出库单细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDispenseDOut(TParm parm) {
        TParm result = this.query("queryDispenseDOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询需要入库的出库单细项
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryDispenseDOI(TParm parm) { 
        TParm result = this.query("queryDispenseDOI", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    /**  
     * 查询出库单细项
     * @param parm TParm
     * @return TParm 
     */
    public TParm onQueryDispenseDIn(TParm parm) {                
        TParm result = this.query("queryDispenseDIn", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    
    /**
     * 查询出库单明细(只有D表)
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDispenseD(TParm parm) { 
        TParm result = this.query("queryDispenseD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        }
        return result;  
    }
                  

    /**
     * 更新出库单细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateDispenseD(TParm parm) {  
        TParm result = this.query("onUpdateDispenseD", parm);
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
