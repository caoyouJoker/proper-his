package jdo.inv;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;

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
public class INVVerifyInReportTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INVVerifyInReportTool instanceObject;

    /**
     * 构造器
     */
    public INVVerifyInReportTool() {
        setModuleName("inv\\INVVerifyInReportModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvVerifyinMTool
     */
    public static INVVerifyInReportTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVVerifyInReportTool();
        return instanceObject;
    }

    /**
     * 查询验收入库
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryVerifyInReport(TParm parm){
        TParm result = this.query("queryVerifyInReport", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
