package jdo.ind;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;

/**
 * <p>Title: 近效期及库存量提示</p>
 *
 * <p>Description: 近效期及库存量提示</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author zhangy 2010.10.29
 * @version 1.0
 */
public class IndValidAndQtyWarnTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndValidAndQtyWarnTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndValidAndQtyWarnTool
     */
    public static IndValidAndQtyWarnTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndValidAndQtyWarnTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndValidAndQtyWarnTool() {
        setModuleName("ind\\INDValidAndQtyWarnModule.x");
        onInit();
    }

    /**
     * 近效期查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryValid(TParm parm) {
        TParm result = this.query("queryValid", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 库存量查询
     *
     * @param parm
     * @return
     */
    public TParm onQueryQty(TParm parm) {
        TParm result = this.query("queryQty", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
