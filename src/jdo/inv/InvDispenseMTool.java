package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;

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
public class InvDispenseMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvDispenseMTool instanceObject;

    /**
     * 构造器
     */
    public InvDispenseMTool() {
        setModuleName("inv\\INVDispenseMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvRequestMTool
     */
    public static InvDispenseMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvDispenseMTool();
        return instanceObject;
    }

    /**
     * 查询出库单主档
     * @param parm TParm   
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryDispenseMOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    } 
    
    /**
     * 查询需要入库的出库单
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryOI(TParm parm) { 
        TParm result = this.query("queryDispenseMOI", parm);  
        System.out.println("查询需要入库的出库单 ==="+result);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**  
     * 查询入库单主档
     * @param parm TParm
     * @return TParm
     */ 
    public TParm onQueryIn(TParm parm) {
        TParm result = this.query("queryDispenseMIn", parm);
        System.out.println("查询入库单主档 ==="+result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        }
        return result;
    }
    
    /**
     * 查询出库单主档细项  
     * @param parm TParm
     * @return TParm
     */  
    public TParm onQueryDetail(TParm parm) {
        TParm result = this.query("queryDispenseMOutDetail", parm); 
        System.out.println("onQueryDetail==="+result); 
        if (result.getErrCode() < 0) {  
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增出库单主档(出库在途)
     *
     * @param parm
     * @return
     */
    public TParm onInsertOut(TParm parm, TConnection conn) {
        TParm result = this.update("createDispenseOutM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增出库单主档(出库即入库)
     *
     * @param parm
     * @return
     */
    public TParm onInsertOutIn(TParm parm, TConnection conn) {
        TParm result = this.update("createDispenseOutInM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 更新出库单主档(出库状态)
     *
     * @param parm
     * @return 
     */
    public TParm onUpdateDispenseM(TParm parm) {  
        TParm result = this.update("onUpdateDispenseM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 更新出库单主档(更新申请单主项状态,conn)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm   
     */   
    public TParm onUpdateFinaFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateFinaFlg", parm, conn);
        if (result.getErrCode() < 0) {  
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        }
        return result;
    }
    
     
    
    /**
     * 删除入库主项(全部)
     * @param parm TParm
     * @param conn TConnection 
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteDispenseinMAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
