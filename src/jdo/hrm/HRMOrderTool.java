package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

public class HRMOrderTool extends TJDOTool {

    /**
     * 实例
     */
    public static HRMOrderTool instanceObject;

    /**
     * 得到实例
     * 
     * @return HRMCompanyTool
     */
    public static HRMOrderTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new HRMOrderTool();
        }
        return instanceObject;
    }

    /**
     * 构造器
     */
    public HRMOrderTool() {
        setModuleName("hrm\\HRMOrderModule.x");
        onInit();
    }

    /**
     * 根据contract_code和mr_no删除某人的所有医嘱（未执行的）
     * 
     * @param parm
     * @param conn
     * @return
     */
    public TParm deleteHrmOrder(TParm parm, TConnection conn) {
        TParm result = update("deleteHrmOrder", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 根据case_no查询一个人的所有检查项目
     * 
     * @param parm
     * @return
     */
    public TParm selectMedApplyOrder(TParm parm) {
        TParm result = query("selectMedApplyOrder", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
}
