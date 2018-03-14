package action.ind;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.ind.INDTool;

/**
 * <p>
 * Title: 日结/月结报表
 * </p>
 *
 * <p>
 * Description: 日结/月结报表
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

public class INDMonthAction
    extends TAction {
    public INDMonthAction() {
    }

    /**
     * 日月结查询
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onQueryDay(TParm parm) {
        TParm result = new TParm();
        result = INDTool.getInstance().onQueryDay(parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText()
                + result.getErrName());
            return result;
        }
        return result;
    }

}
