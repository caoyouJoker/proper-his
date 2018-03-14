package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 订购主档Tool
 * </p>
 *
 * <p>
 * Description: 订购主档Tool
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
 * @author zhangy 2009.05.04
 * @version 1.0
 */

public class IndPurorderMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndPurorderMTool instanceObject;

    /**
     * 构造器
     */
    public IndPurorderMTool() {
        setModuleName("spc\\INDPurorderMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static IndPurorderMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndPurorderMTool();
        return instanceObject;
    }

    /**
     * 查询订购主档中的采购计划单号
     *
     * @param parm
     * @return
     */
    public TParm onQueryPlanNo(String plan_no) {
        String sql = INDSQL.getPlanNoInPurorder(plan_no);
        if ("".equals(sql)) {
            return null;
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询订购主档
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryPurOrderM", parm);
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
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createPurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新订购主档
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updatePurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询未完成订单的信息
     *
     * @param parm
     * @return
     */
    public TParm onQueryUnDone(TParm parm) {
        TParm result = this.query("queryUnDonePurOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询所有订单的信息
     *
     * @param parm
     * @return
     */
    public TParm onQueryDone(TParm parm) {
        TParm result = this.query("queryDonePurOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除订购主档
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deletePurOrderM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新订购主档采购计划单号
     *
     * @param parm
     * @return
     */
    public TParm onUpdatePlanNo(TParm parm, TConnection conn) {
        TParm result = this.update("updatePurOrderMPlanNo", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
