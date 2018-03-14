package jdo.phl;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

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
public class PhlOrderTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static PhlOrderTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndAgentTool
     */
    public static PhlOrderTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhlOrderTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhlOrderTool() {
        setModuleName("phl\\PHLOrderModule.x");
        onInit();
    }

    /**
     * 报到新增医嘱
     *
     * @param parm
     * @return
     */
    public TParm onInsert(TParm parm, TConnection conn) {
        TParm result = this.update("insertOrderDetail", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新执行医嘱
     *
     * @param parm
     * @return
     */
    public TParm onUpdate(TParm parm, TConnection conn) {
        TParm result = this.update("updateOrderDetail", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
