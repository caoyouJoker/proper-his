package jdo.bil;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: 住院收据明细档工具类</p>
 *
 * <p>Description: 住院收据明细档工具类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILIBSRecpdTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static BILIBSRecpdTool instanceObject;
    /**
     * 得到实例
     * @return BILIBSRecpdTool
     */
    public static BILIBSRecpdTool getInstance() {
        if (instanceObject == null)
            instanceObject = new BILIBSRecpdTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public BILIBSRecpdTool() {
        setModuleName("bil\\BILIBSRecpdModule.x");
        onInit();
    }

    /**
     * 查询收据明细档所有数据
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
     * 新增收据明细档
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
     * 查询召回明细表数据
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
     * 更新收据明细档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm updataData(TParm parm, TConnection connection) {
        TParm result = new TParm();
        result = update("updataData", parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            return result;
        }
        return result;
    }
}
