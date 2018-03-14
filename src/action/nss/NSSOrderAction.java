package action.nss;

import com.dongyang.action.TAction;
import jdo.nss.NSSTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 订餐信息</p>
 *
 * <p>Description: 订餐信息</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.16
 * @version 1.0
 */
public class NSSOrderAction
    extends TAction {
    public NSSOrderAction() {
    }

    /**
     * 订餐
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onInsertNSSOrder(TParm parm) {
        TConnection conn = getConnection();
        TParm result = NSSTool.getInstance().onInsertNSSOrder(parm, conn);
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
     * 收费(同时写入IBS_ORDD,IBS_ORDM)
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateNSSChagre(TParm parm) {
        TConnection conn = getConnection();
        TParm result = NSSTool.getInstance().onUpdateNSSChagre(parm, conn);
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
     * 退费(同时写入IBS_ORDD,IBS_ORDM)
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateNSSUnChagre(TParm parm) {
        TConnection conn = getConnection();
        TParm result = NSSTool.getInstance().onUpdateNSSUnChagre(parm, conn);
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
