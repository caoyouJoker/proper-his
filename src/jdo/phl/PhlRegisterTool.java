package jdo.phl;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>
 * Title: 静点室报到
 * </p>
 *
 * <p>
 * Description: 静点室报到
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company:
 * </p>
 *
 * @author zhangy 2009.04.22
 * @version 1.0
 */
public class PhlRegisterTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static PhlRegisterTool instanceObject;

    /**
     * 得到实例
     *
     * @return
     */
    public static PhlRegisterTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhlRegisterTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public PhlRegisterTool() {
        setModuleName("phl\\PHLRegisterModule.x");
        onInit();
    }

    /**
     * 查询病人挂号记录列表
     *
     * @param parm
     * @return
     */
    public TParm onQueryRegister(TParm parm) {
        TParm result = this.query("queryRegister", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 取得病人所开立的点滴药品
     * @param parm TParm
     * @return TParm
     */
    public TParm onQueryOrderDetail(TParm parm) {
        TParm result = this.query("queryOrderDetail", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 静点室报到保存
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm onPhlRegister(TParm parm, TConnection conn) {
        // 数据检核
        if (parm == null)
            return null;
        TParm result = new TParm();
        // 报到新增医嘱
        TParm orderParm = parm.getParm("ORDER_PARM");
        for (int i = 0; i < orderParm.getCount("ORDER_CODE"); i++) {
            result = PhlOrderTool.getInstance().onInsert(orderParm.getRow(i), conn);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText()
                    + result.getErrName());
                return result;
            }
        }

        // 更新床位信息
        TParm bedParm = parm.getParm("BED_PARM");
        result = PhlBedTool.getInstance().onUpdateBed(bedParm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
