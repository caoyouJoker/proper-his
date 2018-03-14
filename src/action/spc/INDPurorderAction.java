package action.spc;

import jdo.spc.INDTool;
import jdo.spc.IndPurorderMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/** 
 * <p>
 * Title: 订购计划Action
 * </p>
 *
 * <p>
 * Description: 订购计划Action
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
 * @author zhangy 2009.05.12
 * @version 1.0
 */

public class INDPurorderAction
    extends TAction {

    /**
     * 查询订购主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = IndPurorderMTool.getInstance().onQuery(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增订购主档
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndPurorderMTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新订购单主项
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndPurorderMTool.getInstance().onUpdate(parm, conn);
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 由采购建议生成订购单
     *
     * @param parm
     * @return
     */
    public TParm onInsertFromPlan(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndPurorderMTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 删除订购单细项
     *
     * @param parm
     * @return
     */
    public TParm onDeleteD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeletePurorderD(parm, conn);
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 删除订购单(主项及细项)
     *
     * @param parm
     * @return
     */
    public TParm onDeleteM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeletePurorderM(parm, conn);
        if (result == null || result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 保存订购单
     *
     * @return
     */
    public TParm onSavePurOrder(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onSavePurOrder(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }
    
    
}
