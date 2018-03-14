package jdo.inv;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

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
public class InvSupRequestDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvSupRequestDTool instanceObject;

    /**
     * 构造器
     */
    public InvSupRequestDTool() {
        setModuleName("inv\\INVSupRequestDModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvSupRequestDTool
     */
    public static InvSupRequestDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvSupRequestDTool();
        return instanceObject;
    }

    /**
     * 新增申请单明细
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新申请单细项
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("update", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询一般出库的细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryInv(TParm parm) {
        TParm result = this.query("queryInv", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询手术包出库的细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryPack(TParm parm) {
        TParm result = this.query("queryPack", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除申请单细项
     *
     * @param parm
     * @return
     */
    public TParm onDelete(TParm parm) {
        TParm result = this.update("delete", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除申请单全部细项
     *
     * @param parm
     * @return
     */
    public TParm onDeleteAll(TParm parm, TConnection conn) {
        TParm result = this.update("deleteAll", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询请领的细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm) {
        TParm result = this.query("query", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 供应室出库更新请领单状态及出库量
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateFlgAndQtyBySupDispense(TParm parm, TConnection conn) {
        TParm result = this.update("updateFlgAndQtyBySupDispense", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
