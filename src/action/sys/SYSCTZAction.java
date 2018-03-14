package action.sys;

import com.dongyang.action.*;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.sys.StatusDetailsTool;
import jdo.sys.CTZTool;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.SYSSQL;

public class SYSCTZAction
    extends TAction {
    public TParm delete(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        //É¾³ýÏ¸±í
        result = StatusDetailsTool.getInstance().delete(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //É¾³ýÖ÷±í
        result = CTZTool.getInstance().deleteData(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * É¾³ý
     * @param parm TParm
     * @return TParm
     */
    public TParm onDelete(TParm parm) {
        TConnection connection = getConnection();
        //É¾³ýÏ¸±í
        String ctz_code = parm.getValue("CTZ_CODE");
        TParm result = new TParm(TJDODBTool.getInstance().update(SYSSQL.
            deleteSYSChargeDetail(ctz_code), connection));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //É¾³ýÖ÷±í
        String[] sql = (String[]) parm.getData("SQL_M");
        for (int i = 0; i < sql.length; i++) {
            TParm badParm = new TParm(TJDODBTool.getInstance().update(sql[i],
                connection));
            if (badParm.getErrCode() < 0) {
                connection.close();
                return badParm;
            }
        }
        connection.commit();
        connection.close();
        return result;
   }

}
