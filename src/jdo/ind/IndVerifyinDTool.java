package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class IndVerifyinDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndVerifyinDTool instanceObject;

    /**
     * 构造器
     */
    public IndVerifyinDTool() {
        setModuleName("ind\\INDVerifyinDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static IndVerifyinDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndVerifyinDTool();
        return instanceObject;
    }

    /**
     * 查询验收明细
     *
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("queryVerifyinD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 验收未退货明细表
     *
     * @param parm
     * @return
     */
    public TParm onQueryVerifyinDone(TParm parm) {
        TParm result = this.query("queryVerifyinDoneD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 累计退货数更新
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateReg(TParm parm, TConnection conn) {
        TParm result = this.update("updateVerifyinDReg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 药品验收入库统计(购入汇总)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinBuyMaster(TParm parm) {
        TParm result = this.query("getQueryVerifyinBuyMaster", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药品验收入库统计(购入明细)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinBuyDetail(TParm parm) {
        TParm result = this.query("getQueryVerifyinBuyDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药品验收入库统计(赠药汇总)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinGiftMaster(TParm parm) {
        TParm result = this.query("getQueryVerifyinGiftMaster", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 药品验收入库统计(赠药明细)
     * @param org_code String
     * @param order_code String
     * @param sort String
     * @return TParm
     */
    public TParm onQueryVerifyinGiftDetail(TParm parm) {
        TParm result = this.query("getQueryVerifyinGiftDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
