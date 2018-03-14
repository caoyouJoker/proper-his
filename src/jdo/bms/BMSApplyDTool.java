package jdo.bms;

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
public class BMSApplyDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BMSApplyDTool instanceObject;

    /**
     * 得到实例
     *
     * @return
     */
    public static BMSApplyDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BMSApplyDTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BMSApplyDTool() {
        setModuleName("bms\\BMSApplyDModule.x");
        onInit();
    }

    /**
     * 备血单新增
     *
     * @param parm
     * @return
     */
    public TParm onApplyInsert(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyInsert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 备血单更新
     *
     * @param parm
     * @return
     */
    public TParm onApplyUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyUpdate", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除备血单细项
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onApplyDelete(TParm parm, TConnection conn) {
        TParm result = this.update("ApplyDelete", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询备血单
     *
     * @param parm
     * @return
     */
    public TParm onApplyQuery(TParm parm) {
        TParm result = this.query("ApplyQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询取血单
     *
     * @param parm
     * @return
     */
    public TParm onTakeQuery(TParm parm) {
        TParm result = this.query("TakeQuery", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
