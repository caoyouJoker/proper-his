package jdo.nss;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 套餐餐点</p>
 *
 * <p>Description: 套餐餐点</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSPackDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static NSSPackDTool instanceObject;

    /**
     * 得到实例
     *
     * @return NSSPackDTool
     */
    public static NSSPackDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new NSSPackDTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public NSSPackDTool() {
        setModuleName("nss\\NSSPackDModule.x");
        onInit();
    }

    /**
     * 查询套餐餐点
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryNSSPackD(TParm parm) {
        TParm result = this.query("queryNSSPackD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 新增套餐餐点
     *
     * @param parm
     * @return
     */
    public TParm onInsertNSSPackD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("insertNSSPackD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新套餐餐点
     *
     * @param parm
     * @return
     */
    public TParm onUpdateNSSPackD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("updateNSSPackD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除套餐餐点
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackD(TParm parm) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackD", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除套餐餐点
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onDeleteNSSPackD(TParm parm, TConnection conn) {
        if (parm == null) {
            err("ERR:" + parm);
        }
        TParm result = this.update("deleteNSSPackD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
