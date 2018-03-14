package action.nss;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.nss.NSSTool;

/**
 * <p>Title: Ì×²Í×Öµä</p>
 *
 * <p>Description: Ì×²Í×Öµä</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangy 2010.11.11
 * @version 1.0
 */
public class NSSPackAction
    extends TAction {
    public NSSPackAction() {
    }

    /**
     * É¾³ýÌ×²Í£¨°üÀ¨£ºÖ÷Ïî£¬Ï¸ÏîºÍÃ÷Ï¸£©
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDeleteNSSPack(TParm parm) {
        TConnection conn = getConnection();
        TParm result = NSSTool.getInstance().onDeleteNSSPack(parm, conn);
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
     * É¾³ý²Íµã£¨°üÀ¨£ºÏ¸ÏîºÍÃ÷Ï¸£©
     *
     * @param parm
     *            TParm
     * @return TParm
     */
    public TParm onDeleteNSSPackD(TParm parm) {
        TConnection conn = getConnection();
        TParm result = NSSTool.getInstance().onDeleteNSSPackD(parm, conn);
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
