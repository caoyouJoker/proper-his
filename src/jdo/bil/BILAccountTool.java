package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title:个人日结交账 </p>
 *
 * <p>Description:个人日结交账 </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author FUDW
 * @version 1.0
 */
public class BILAccountTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILAccountTool instanceObject;
    /**
     * 得到实例
     * @return BILAccountTool
     */
    public static BILAccountTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILAccountTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILAccountTool() {
        setModuleName("bil\\BILAccountModule.x");
        onInit();
    }

    /**
     * 查询全部数据明细
     * @param parm TParm
     * @return TParm
     */
    public TParm selectData(TParm parm) {
        TParm result = query("selectData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 日结交账
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertAccount(TParm parm, TConnection connection) {
        TParm result = update("insertAccount", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 取消日结
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 检核是否结过账
     * @param parm TParm
     * @return TParm
     */
    public TParm selectCheckAccount(TParm parm) {
        TParm result = query("selectCheckAccount", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 查询日结数据
     * @param parm TParm
     * @return TParm
     */
    public TParm accountQuery(TParm parm) {
        TParm result = new TParm();
        result = this.query("accountQuery", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }

}
