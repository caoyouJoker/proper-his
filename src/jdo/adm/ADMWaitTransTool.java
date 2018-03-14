package jdo.adm;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:入出转管理 </p>
 *
 * <p>Description:入出转管理 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author JiaoY 
 * @version 1.0 
 */
public class ADMWaitTransTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ADMWaitTransTool instanceObject;
    /**
     * 得到实例
     * @return SchWeekTool
     */
    public static ADMWaitTransTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ADMWaitTransTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public ADMWaitTransTool() {
        setModuleName("adm\\ADMWaitTransModule.x");
        onInit();
    }

    /**
     * 查询adm_Wait_Trans全字段
     * @param parm TParm
     * @return TParm
     */
    public TParm queryDate(TParm parm) {
        TParm result = new TParm();
        result = query("selectall", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 住院登记时插入
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveForInp(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 插入信息
     * @param parm TParm
     * @return TParm
     */
    public TParm saveForInp(TParm parm) {
        TParm result = new TParm();
        result = update("insert", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 转科时插入
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveForInOutDept(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("insertForInOutDept", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询待转入传出病患信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm selpatInfo(TParm parm) {
        TParm result = new TParm();
        result = query("selpatInfo",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询在院病患信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm selAdmPat(TParm parm) {
        TParm result = new TParm();
        result = query("selAdmPat",parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 删除
     * @param parm TParm
     * @return TParm
     */
    public TParm deleteIn(TParm parm, TConnection conn) {
        TParm result = new TParm();
        result = update("deleteIn", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 取消转科时，先删除,后插入.
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm saveForCancelTrans(TParm parm, TConnection conn) { 
        TParm result = new TParm();
        result = update("insert", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }     

}
