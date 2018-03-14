package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 订购细项Tool
 * </p>
 *
 * <p>
 * Description: 订购细项Tool
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
 * @author zhangy 2009.05.14
 * @version 1.0
 */

public class IndPurorderDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndPurorderDTool instanceObject;

    /**
     * 构造器
     */
    public IndPurorderDTool() {
        setModuleName("spc\\INDPurorderDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static IndPurorderDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndPurorderDTool();
        return instanceObject;
    }

    /**
     * 查询订购主档
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryPurOrderD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询未完成订购单号
     *
     * @param sup_code
     * @return
     */
    public TParm onQueryUnDonePurorderNo(String org_code, String sup_code) {
        TParm result = new TParm(TJDODBTool.getInstance().select(
            INDSQL.getUnDonePurorderNo(org_code, sup_code)));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 累计验收数、累计品质扣款更新
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateVer(TParm parm, TConnection conn) {
        TParm result = this.update("updatePlanDVer", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询累计生成量
     *
     * @param parm
     * @return
     */
    public TParm onQueryPurOrderQTY(TParm parm) {
        TParm result = this.query("queryPurOrderQTY", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增订购细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createPurorderD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
