package action.ope;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.ope.OPEOpDetailTool;

/**
 * <p>Title: 手术记录</p>
 *
 * <p>Description: 手术记录</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author zhangk 2009-12-09
 * @version 4.0
 */
public class OPEDetailAction
    extends TAction {
    public OPEDetailAction() {
    }
    public TParm insertData(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = OPEOpDetailTool.getInstance().insertData(parm,connection);
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
