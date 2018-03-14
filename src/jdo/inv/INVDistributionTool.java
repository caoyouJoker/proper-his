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
public class INVDistributionTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INVDistributionTool instanceObject;

    /**
     * 构造器
     */
    public INVDistributionTool() {
        setModuleName("inv\\INVDistributionModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvVerifyinMTool
     */
    public static INVDistributionTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVDistributionTool();
        return instanceObject;
    }

    /**
     * 查询智能柜ID有值的
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryCabinetIdNotNull(TParm parm){
        TParm result = this.query("queryCabinetIdNotNull", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询智能柜ID无值的
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryCabinetIdIsNull(TParm parm){
        TParm result = this.query("queryCabinetIdIsNull", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
