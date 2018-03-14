package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 套餐菜名</p>
 *
 * <p>Description: 套餐菜名</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSPackDDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static NSSPackDDTool instanceObject;

    /**
     * 得到实例
     *
     * @return NSSPackddTool
     */
    public static NSSPackDDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSPackDDTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public NSSPackDDTool() {
        setModuleName("nss\\NSSPackDDModule.x");
        onInit();
    }

    /**
     * 查询套餐菜名
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSPackDD(TParm parm) {
        TParm result = this.query("queryNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增套餐菜名
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSPackDD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新套餐菜名
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSPackDD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除套餐菜名
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackDD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackDD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除套餐菜名
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackDD(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackDD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
