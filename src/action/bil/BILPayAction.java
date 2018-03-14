package action.bil;

import com.dongyang.action.TAction;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import jdo.bil.BILTool;

/**
 *
 * <p>Title: 预交金动作类</p>
 *
 * <p>Description: 预交金动作类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author wangl
 * @version 1.0
 */
public class BILPayAction
    extends TAction {
    /**
     * 交预交金
     * @param parm TParm
     * @return TParm
     */
    public TParm onBillPay(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onBillPay(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 退预交金
     * @param parm TParm
     * @return TParm
     */
    public TParm onReturnBillPay(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onReturnBILPay(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
    /**
     * 预交金补印
     * @param parm TParm
     * @return TParm
     */
    public TParm onBilPayReprint(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = BILTool.getInstance().onBilPayReprint(parm, connection);
        if (result.getErrCode() < 0) {
            err(result.getErrName() + " " + result.getErrText());
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
}
