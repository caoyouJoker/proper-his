package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

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
public class SYSWriteLogAction
    extends TAction {
    public SYSWriteLogAction() {
    }

    /**
     * Ð´ÈëSYS_PATLOGÊý¾Ý
     *
     * @param parm
     * @return
     */
    public TParm onSYSPatLog(TParm parm) {
        TConnection conn = getConnection();
        String sql = parm.getValue("SQL");
        String exec_sql[] = new String[parm.getCount("MODI_ITEM")];
        for (int i = 0; i < parm.getCount("MODI_ITEM"); i++) {
            exec_sql[i] = sql.replaceFirst("#", parm.getValue("MODI_ITEM", i)).
                replaceFirst("#", parm.getValue("ITEM_OLD", i)).replaceFirst("#",
                parm.getValue("ITEM_NEW", i));
            sql = parm.getValue("SQL");
        }
        TParm result = new TParm(TJDODBTool.getInstance().update(exec_sql, conn));
        if (result == null || result.getErrCode() < 0) {
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
