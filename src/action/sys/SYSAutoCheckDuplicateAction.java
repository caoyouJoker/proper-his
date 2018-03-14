package action.sys;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

import jdo.emr.OptLogTool;

/**
 * <p>Title: 电子病历自动查重action</p>
 *
 * <p>Description: 电子病历自动查重</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author Zhangjg
 * @version 1.0
 */
public class SYSAutoCheckDuplicateAction
    extends TAction {
    public SYSAutoCheckDuplicateAction() {
    }

    public TParm update(TParm parm) {
        TParm result = new TParm();
        TConnection conn = getConnection();
        int count = parm.getCount("MR_NO");
        for (int i = 0; i < count; i++) {
        	String sql = " UPDATE SYS_PATINFO SET "
        		+ " MERGE_FLG = '" + parm.getValue("MERGE_FLG", i) + "',"
                + " MERGE_TOMRNO = '" + parm.getValue("MERGE_TOMRNO", i) + "'"
                + " WHERE MR_NO = '" + parm.getValue("MR_NO", i) + "'";
            result = new TParm(TJDODBTool.getInstance().update(sql, conn));
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
