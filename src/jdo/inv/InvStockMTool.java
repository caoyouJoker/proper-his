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
 * @author not attributable
 * @version 1.0
 */
public class InvStockMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvStockMTool instanceObject;

    /**
     * 构造器
     */
    public InvStockMTool() {
        setModuleName("inv\\INVStockMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvStockMTool
     */
    public static InvStockMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvStockMTool();
        return instanceObject;
    }

    /**
     * 更新库存主档的库存量
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQty(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQty", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 出库作业更新库存量
     *
     * @param parm
     * @return
     */
    public TParm onUpdateStockQtyOut(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyOut", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增库存主档
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm) {
        TParm result = this.update("createStockM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新库存主档
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm) {
        TParm result = this.update("updateStockM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增库存主档
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createStockM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 手术包打包更新库存主档的库存量
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
  //---------------供应室start--------------------
    /**
     * 查找库存量
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockQty(TParm parm) {
        TParm result = this.query("getStockQty", parm);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    /**
     * 更新已有物资库存量
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockMQty(TParm parm, TConnection connection) {
//    	System.out.println("InvStockMTool.java-----parm"+parm);
        TParm result = this.update("updateStockQtyGYS", parm, connection);//wm2013-06-04  "updateStockQty"
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    
    
    //---------------供应室end--------------------

}
