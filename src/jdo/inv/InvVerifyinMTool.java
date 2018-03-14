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
public class InvVerifyinMTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvVerifyinMTool instanceObject;

    /**
     * 构造器
     */
    public InvVerifyinMTool() {
        setModuleName("inv\\INVVerifyinMModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvVerifyinMTool
     */
    public static InvVerifyinMTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvVerifyinMTool();
        return instanceObject;
    }

    /**
     * 查询验收入库单
     * @param parm TParm
     * @return TParm
     */
    public TParm onQuery(TParm parm){
        TParm result = this.query("queryVerifyinM", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }


    /**
     * 新增验收主档
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("createVerifyinM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新验收主档
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateVerifyinM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除验收主档
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDelete(TParm parm, TConnection conn) {
        TParm result = this.update("deleteVerifyinM", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
