package action.emr;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.emr.OptLogTool;

/**
 * <p>Title: 电子病历操作日志action</p>
 *
 * <p>Description: 日志查询和批量删除</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class EMROptLogAction
    extends TAction {
    public EMROptLogAction() {
    }

    public TParm delete(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        int count = parm.getCount("CASE_NO");
        for (int i = 0; i < count; i++) {
            result = OptLogTool.getInstance().delete(parm.getRow(i), conn);
            if (result.getErrCode() < 0) {
                conn.close();
                return result;
            }
        }
        conn.commit();
        conn.close();
        return result;

    }
}
