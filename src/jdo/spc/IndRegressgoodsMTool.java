package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 退货管理主档Tool
 * </p>
 *
 * <p>
 * Description: 退货管理主档Tool
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
 * @author zhangy 2009.05.21
 * @version 1.0
 */

public class IndRegressgoodsMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndRegressgoodsMTool instanceObject;

    /**
     * 构造器
     */
    public IndRegressgoodsMTool() {
        setModuleName("ind\\INDRegressgoodsMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static IndRegressgoodsMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndRegressgoodsMTool();
        return instanceObject;
    }

    /**
     * 查询退货主档
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryRegressgoodsM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询退货主档
     *
     * @param parm
     * @return
     */
    public TParm onQueryAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("querySPCRegressgoodsM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 新增退货主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 新增退货主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("createSPCRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新退货主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 更新退货主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("updateSPCRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除退货主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 删除退货主项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDeleteAcnt(TParm parm, TConnection conn) {//wanglong add 20150202
        TParm result = this.update("deleteSPCRegressgoodsM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 药品退货出库统计(未出库)
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegressgoods(TParm parm) {
        TParm result = this.query("getQueryRegressgoods", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 药品退货出库统计(未出库)
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegressgoodsAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("getQueryRegressgoodsAcnt", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 药品退货出库统计(已出库)
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegressgoodsCheck(TParm parm) {
        TParm result = this.query("getQueryRegressgoodsCheck", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药品退货出库统计(已出库)
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegressgoodsCheckAcnt(TParm parm) {//wanglong add 20150202
        TParm result = this.query("getQueryRegressgoodsCheckAcnt", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
