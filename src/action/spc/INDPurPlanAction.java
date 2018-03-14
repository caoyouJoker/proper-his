package action.spc;

import jdo.spc.INDTool;
import jdo.spc.IndPurPlanMTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 采购计划Action
 * </p>
 *
 * <p>
 * Description: 采购计划Action
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.28
 * @version 1.0
 */

public class INDPurPlanAction
    extends TAction {

    /**
     * 查询采购计划单主档及订购单的生成
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryM(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryPurPlan(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增采购计划单主项
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndPurPlanMTool.getInstance().onInsert(parm, conn);
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
     * 更新采购计划单主项
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = IndPurPlanMTool.getInstance().onUpdate(parm, conn);
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
     * 删除采购计划单
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDeleteM(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onDeletePurPlanM(parm, conn);
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
     * 采购计划引用查询(按照药品查询)
     *
     * @param parm
     * @return TParm
     */
    public TParm onQueryExcerptByOrder(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryExcerptByOrder(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 采购计划生成订购单
     *
     * @param parm
     * @return
     */
    public TParm onCreatePurOrder(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onCreatePurOrder(parm, conn);
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
