package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
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
public class InvStockDDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvStockDDTool instanceObject;

    /**
     * 构造器
     */
    public InvStockDDTool() {
        setModuleName("inv\\INVStockDDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvStockDTool
     */
    public static InvStockDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvStockDDTool();
        return instanceObject;
    }

    /**
     * 新增库存序号管理细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertInvStockDD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询库存序号管理细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryStockDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询库存序号管理细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryOut(TParm parm) {  
        TParm result = this.query("queryOutStockDD", parm);
        System.out.println("queryOutStockDD--result"+result);
        if (result.getErrCode() < 0) { 
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }  
        return result;
    }  
    

    /**
     * 更新库存序号管理细项(出库即入库)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyOutIn(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyOutIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        } 
        return result;
    }
  
    
    /**
     * 更新库存序号管理细项(出库即入库)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateOrginCode(TParm parm, TConnection conn) {
        TParm result = this.update("updateOrginCode", parm, conn);
        System.out.println(result);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        } 
        return result;
    }
    
    /**
     * 更新料位(盘点)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateCab(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyCheck", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result; 
        } 
        return result;
    }
  
    /**
     * 更新库存序号管理细项(出库在途)---现在无用？？？
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyOut(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 手术包打包更新序号管理细项的库存量
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByPack", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 供应室出库作业更新库存量(请领作业)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateStockQtyByReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    /**
     * 更改计费注记
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateCost(TParm parm, TConnection connection) {
        TParm result = update("updateCost", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
  //-------------供应室start---------------
    /**
     * 更改序号管理物资所在的手术包
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updatePackAge(TParm parm, TConnection connection) {
        TParm result = update("updatePackAge", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    
    //-------------供应室start---------------


}
