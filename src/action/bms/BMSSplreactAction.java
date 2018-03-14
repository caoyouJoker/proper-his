package action.bms;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.bms.BMSSplrectTool;

/**
 * <p>
 * Title: 输血反应
 * </p>
 *
 * <p>
 * Description: 输血反应
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
public class BMSSplreactAction
    extends TAction {
    public BMSSplreactAction() {
    }

    /**
     * 新增
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsert(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSSplrectTool.getInstance().onInsert(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 更新
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSSplrectTool.getInstance().onUpdate(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

    /**
     * 删除
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = BMSSplrectTool.getInstance().onDelete(parm, conn);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            conn.close();
            return result;
        }
        conn.commit();
        conn.close();
        return result;
    }

}
