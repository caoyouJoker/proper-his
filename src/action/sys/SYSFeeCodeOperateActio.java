package action.sys;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.StatusDetailsTool;
import jdo.sys.SYSChargeHospCodeTool;

public class SYSFeeCodeOperateActio
    extends TAction {
    public TParm delete(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        //É¾³ýÏ¸±í
        //System.out.println("fdw         sss:"+parm);
        result = StatusDetailsTool.getInstance().delete(parm, connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            connection.close();
            return result;
        }
        //É¾³ýÖ÷±í
        result = SYSChargeHospCodeTool.getInstance().delete(parm, connection);
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


}
