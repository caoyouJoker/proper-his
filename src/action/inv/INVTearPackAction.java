package action.inv;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.inv.INVTool;

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
public class INVTearPackAction extends TAction {
    public INVTearPackAction() {
    }

    /**
     * 手术包拆包（序号管理）
     * @param parm TParm
     * @return TParm
     */
    public TParm onINVTearPackA(TParm parm) {
    	
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onINVTearPackA(parm, conn);
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
     * 诊疗包拆包（非序号管理）
     * @param parm TParm
     * @return TParm
     */
    public TParm onINVTearPackB(TParm parm) {
        TConnection conn = getConnection();
        TParm result = new TParm();
        result = INVTool.getInstance().onINVTearPackB(parm, conn);
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
