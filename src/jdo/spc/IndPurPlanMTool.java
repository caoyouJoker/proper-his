package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 采购计划主档Tool
 * </p>
 *
 * <p>
 * Description: 采购计划主档Tool
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

public class IndPurPlanMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndPurPlanMTool instanceObject;

    /**
     * 构造器
     */
    public IndPurPlanMTool() {
        setModuleName("ind\\INDPurPlanMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static IndPurPlanMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndPurPlanMTool();
        return instanceObject;
    }

    /**
     * 查询
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryPlanM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createNewPlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updatePlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新采购计划单生成订购单状态
     *
     * @param parm
     * @return
     */
    public TParm onUpdateCreateFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateCreateFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deletePlanM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 采购计划引用查询(按照药品查询)
     *
     * @param parm
     * @return
     */
    public TParm onQueryExcerptByOrder(TParm parm) {
        TParm result = this.query("queryExcerptByOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询上期采购量
     *
     * @param parm
     * @return
     */
    public TParm onQueryBuyQty(TParm parm) {
        TParm result = this.query("queryBuyQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询上期销售量
     *
     * @param parm
     * @return
     */
    public TParm onQuerySellQty(TParm parm) {
        TParm result = this.query("querySellQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询主库库存
     *
     * @param parm
     * @return
     */
    public TParm onQueryMainQty(TParm parm) {
        TParm result = this.query("queryMainQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询中库库存
     *
     * @param parm
     * @return
     */
    public TParm onQueryMiddQty(TParm parm) {
        TParm result = this.query("queryMiddQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
