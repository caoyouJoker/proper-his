package jdo.ind;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 日库存交易档
 * </p>
 *
 * <p>
 * Description:日库存交易档
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.08.29
 * @version 1.0
 */
public class IndDDStockTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndDDStockTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndDDStockTool
     */
    public static IndDDStockTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndDDStockTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndDDStockTool() {
        setModuleName("ind\\INDDDStockModule.x");
        onInit();
    }

    /**
     * 日结批次作业
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertDDStock", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("qyeryDDStock", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药库日结成本金额查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayA(TParm parm) {
        TParm result = this.query("queryDDStcokDayA", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药房日结成本金额查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayB(TParm parm) {
        TParm result = this.query("queryDDStcokDayB", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药库日结零售金额查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayC(TParm parm) {
        TParm result = this.query("queryDDStcokDayC", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药房日结零售金额查询
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayD(TParm parm) {
        TParm result = this.query("queryDDStcokDayD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药库、药房日结中查询本期结存的成本金额、零售金额
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryDDStockDayStockOwn(TParm parm) {
        TParm result = this.query("queryDDStcokDayStockOwn", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
      /**
      * 药品销售月查询
      *
      * @param parm
      * @return
      */
     public TParm onQueryBildrug(TParm parm) {
         if (parm == null) {
             err("ERR:" + parm);
         }
         TParm result = this.query("getbildrugQuery", parm);
         if (result.getErrCode() < 0) {
             err("ERR:" + result.getErrCode() + result.getErrText()
                 + result.getErrName());
             return result;
         }
         return result;
    }
}
