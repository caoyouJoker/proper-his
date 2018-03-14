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
public class INVGoodsApplyTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static INVGoodsApplyTool instanceObject;

    /**
     * 构造器
     */
    public INVGoodsApplyTool() {
        setModuleName("inv\\INVGoodsApplyModule.x");
        onInit();
    }

    /**
     * 得到实例
     *
     * @return InvVerifyinMTool
     */
    public static INVGoodsApplyTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INVGoodsApplyTool();
        return instanceObject;
    }

    /**
     * 查询验收入库
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryGoodsApplyReport(TParm parm){
        TParm result = this.query("queryGoodsApplyReport", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
