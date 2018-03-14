package action.ind;

import com.dongyang.action.TAction;
import jdo.ind.IndAgentTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ind.INDTool;

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
public class INDStockMAction
    extends TAction {
    public INDStockMAction() {
    }

    /**
     * ¸üÐÂ
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onUpdate(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INDTool.getInstance().onUpdateIndStockM(parm, conn);
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
