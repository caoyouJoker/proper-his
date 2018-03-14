package jdo.inv;

import jdo.ind.INDSQL;

import com.dongyang.jdo.TJDODBTool;
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
public class InvStockDTool  
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvStockDTool instanceObject;

    /**
     * 构造器
     */
    public InvStockDTool() {
        setModuleName("inv\\INVStockDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvStockDTool
     */
    public static InvStockDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvStockDTool();
        return instanceObject;
    }

    /**
     * 查询库存明细
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryStockQty(TParm parm) {
        TParm result = this.query("queryStockQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增库存明细
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertInvStockD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新库存明细(验收入库)
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateInvStockD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 申请单出库更新库存
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
     * 申请单入库更新库存
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateQtyIn(TParm parm, TConnection conn) {
        TParm result = this.update("updateQtyIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());  
            return result;
        }
        return result;  
    }

    /**  
     * 手术包打包更新库存明细的库存量
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
     * 手术包拆包更新库存明细的库存量
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateStockQtyByTear(TParm parm, TConnection conn) {
        TParm result = this.update("updateStockQtyByTear", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    
    
    /**
     * 查询库存明细
     * @param parm TParm
     * @return TParm
     */    
    public TParm onQueryStockQTY(TParm parm) {
        TParm result = this.query("StockQTY", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
  //-----------------供应室start------------------------
    /**
     * 查找物资的批次序号
     * @param parm TParm
     * @return TParm
     */
    public TParm getStockBatchSeq(TParm parm) {
        //（主要是查找有多少种批次）
        TParm result = query("getStockBatchSeq", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    
    /**
     * 更新已有物资库存量
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateStockQty(TParm parm, TConnection connection) {
        TParm result = this.update("updateStockQty", parm, connection);
        if (result.getErrCode() < 0)
            err(result.getErrCode() + " " + result.getErrText());
        return result;
    }
    
    
    //-----------------供应室end------------------------
    
    
    /**
     * 根据出库科室编号及物资代码查询药品的批次序号、库存
     * @param org_code String
     * @param order_code String
     * @param sort String  
     * @return TParm  
     */
    public TParm onQueryStockBatchAndQty(String org_code, String order_code) { 
    	System.out.println("20150504::::"+INVSQL.getInvStockBatchAndQty(org_code, order_code));
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INVSQL.getInvStockBatchAndQty(org_code, order_code))); 
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


}
