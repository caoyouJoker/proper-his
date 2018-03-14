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
public class InvRequestMTool extends TJDOTool {
    /**
     * 实例
     */
    public static InvRequestMTool instanceObject;

    /**
     * 构造器
     */
    public InvRequestMTool() {
        setModuleName("inv\\INVRequestMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvRequestMTool
     */
    public static InvRequestMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvRequestMTool();
        return instanceObject;
    }

    /**
     * 查询申请单主档
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("queryRequestM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 新增申请单主档
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新申请单主档
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除申请单主档
     * @param parm
     * @param conn
     * @return
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteRequestM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询需要出库的申请单
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryRequestMOut(TParm parm) {
        TParm result = this.query("queryRequestMOut", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 申请单主项状态(更新申请单主项状态)
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onUpdateFinalFlg(TParm parm, TConnection conn) {
        TParm result = this.update("updateFinalFlg", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
