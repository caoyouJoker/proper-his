package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: 住院收据主档工具类</p>
 *
 * <p>Description: 住院收据主档工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpmTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILIBSRecpmTool instanceObject;
    /**
     * 得到实例
     * @return BILIBSRecpmTool
     */
    public static BILIBSRecpmTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILIBSRecpmTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILIBSRecpmTool() {
        setModuleName("bil\\BILIBSRecpmModule.x");
        onInit();
    }

    /**
     * 查询收据主档所有数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = new TParm();
        result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 新增收据主档数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询召回主表数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForReturn(TParm parm) {
        TParm result = new TParm();
        result = query("selDateForReturn", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 更新收据主档
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = this.update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 查询住院日结数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selDateForAccount(TParm parm) {
        TParm result = query("selDateForAccount", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 查找住院未结账的收费员
     * @param parm TParm
     * @return TParm
     */
    public TParm selCashier(TParm parm)
    {
        TParm result = query("selCashier", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 更新日结标记,日结号,日结人员,日结日期
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updateAccount(TParm parm, TConnection connection) {
        TParm result = update("updateAccount", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     * 住院票据补印
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm upRecpForRePrint(TParm parm, TConnection connection) {
        TParm result = update("upRecpForRePrint", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }


}
