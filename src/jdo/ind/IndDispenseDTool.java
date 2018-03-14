package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 出入库管理明细Tool
 * </p>
 *
 * <p>
 * Description: 出入库管理明细Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.25
 * @version 1.0
 */
public class IndDispenseDTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static IndDispenseDTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndStockMTool
     */
    public static IndDispenseDTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndDispenseDTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public IndDispenseDTool() {
        setModuleName("ind\\INDDispenseDModule.x");
        onInit();
    }

    /**
     * 新增细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onInsertD(TParm parm, TConnection conn) {
        TParm result = this.update("createNewDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 更新细项
     *
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateD(TParm parm, TConnection conn) {
        TParm result = this.update("updateDispenseD", parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }
}
