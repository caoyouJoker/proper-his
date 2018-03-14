package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>Title: 配餐明细</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSOrderTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static NSSOrderTool instanceObject;

    /**
     * 得到实例
     *
     * @return NSSOrderTool
     */
    public static NSSOrderTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSOrderTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public NSSOrderTool() {
        setModuleName("nss\\NSSOrderModule.x");
        onInit();
    }

    /**
     * 得到周套餐明细
     * @param parm TParm
     * @return TParm
     */
    public TParm getNSSWeeklyOrder(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.query("getNSSWeeklyOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 得到订餐明细
     * @param parm TParm
     * @return TParm
     */
    public TParm getNSSOrder(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.query("getNSSOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询订餐明细
     * @param parm TParm
     * @return TParm
     */
    public TParm queryNSSOrder(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.query("queryNSSOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 得到收费明细
     * @param parm TParm
     * @return TParm
     */
    public TParm getNSSCharge(TParm parm){
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.query("getNSSCharge", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;

    }

    /**
     * 新增配餐明细
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSOrder(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增配餐明细
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSOrder(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSOrder", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新配餐明细
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSOrder(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新配餐明细
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSOrder(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSOrder", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 配餐收费
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSChagre(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSChagre", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 配餐收费
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSChagre(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSChagre", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除配餐
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSOrder(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除配餐
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSOrder(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSOrder", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
