package jdo.bil;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

public class BILCounteTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILCounteTool instanceObject;
    /**
     * 得到实例
     * @return BILCounteTool
     */
    public static BILCounteTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILCounteTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILCounteTool() {
        setModuleName("bil\\BILCounterModule.x");
        onInit();
    }

    /**
     * 查询全部数据
     * @param parm TParm
     * @return TParm
     */
    public TParm selectAllData(TParm parm) {
        TParm result = query("selectAllData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 开账插入数据
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm insertData(TParm parm, TConnection connection) {
        TParm result = update("insertData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 更新数据数据
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
     * 更新数据数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updataData(TParm parm) {
        TParm result = update("updataData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 检核开关账
     * @param parm TParm
     * @return TParm
     */
    public TParm CheckCounter(TParm parm) {
        TParm result = query("CheckCounter", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 检核使用中的票据
     * @param parm TParm
     * @return TParm
     */
    public TParm finishData(TParm parm) {
        TParm result = query("finishData", parm);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

}
