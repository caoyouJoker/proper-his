package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

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
public class InvPurorderDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvPurorderDTool instanceObject;

    /**
     * 构造器
     */
    public InvPurorderDTool() {
        setModuleName("inv\\INVPurorderDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return IndPurPlanMTool
     */
    public static InvPurorderDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvPurorderDTool();
        return instanceObject;
    }

    /**
     * 查询订购单细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("queryPurOrderD", parm);
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

    /**
     * 更新订购细项
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updatePurOrderD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除订购细项
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deletePurOrderD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除订购细项(全部)
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deletePurOrderDAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新订购单的入库数量累计
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateInQty(TParm parm, TConnection conn) {
        TParm result = this.update("updateInQty", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新入库单细项的单据状态
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
