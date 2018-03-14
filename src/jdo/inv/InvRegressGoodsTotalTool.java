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
public class InvRegressGoodsTotalTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static InvRegressGoodsTotalTool instanceObject;

    /**
     * 构造器
     */
    public InvRegressGoodsTotalTool() {
        setModuleName("inv\\INVRegressGoodsTotalModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvVerifyinMTool
     */
    public static InvRegressGoodsTotalTool getInstance() {
        if (instanceObject == null)
            instanceObject = new InvRegressGoodsTotalTool();
        return instanceObject;
    }

    /**
     * 退货统计
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryRegressGoodsTotal(TParm parm){
        TParm result = this.query("queryRegressGoodsTotal", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
