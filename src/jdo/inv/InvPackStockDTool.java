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
public class InvPackStockDTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static InvPackStockDTool instanceObject;

    /**
     * 构造器
     */
    public InvPackStockDTool() {
        setModuleName("inv\\INVPackStockDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvPackStockDTool
     */
    public static InvPackStockDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvPackStockDTool();
        return instanceObject;
    }

    /**
     * 手术包打包新增序号管理细项的库存量
     *
     * @param parm
     * @return
     */
    public TParm onInsertStockQtyByPack(TParm parm, TConnection conn) {
        TParm result = this.update("insertStockQtyByPack", parm, conn);
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
     * 没有序号管理的手术包,扣除手术包库存明细中的库存量
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 有序号管理的手术包,删除手术包明细中的一次性物资
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteOnceUserInvBySupReq(TParm parm, TConnection conn) {
        TParm result = this.update("deleteOnceUserInvBySupReq", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除明细
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deleteAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
  //---------------供应室start------------------------
    /**
     * 新增手术包库存明细
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertInv(TParm parm, TConnection connection) {
        TParm result = this.update("insertInv", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    /**
     * 更新库存明细库存量
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateQty(TParm parm, TConnection connection) {
        TParm result = this.update("updateQty", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;

    }
    
    /**
     * 更新高值且一次性物品的状态
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm changeHOStatus(TParm parm, TConnection connection) {
        TParm result = this.update("changeHOStatus", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    /**
     * 没有序号管理的手术包,扣除手术包库存明细中的库存量(新改的供应室出库)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyBySupDisp(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyBySupDispense", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    //---------------供应室end------------------------

}
