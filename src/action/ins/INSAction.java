package action.ins;

import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;
import com.dongyang.action.TAction;
import jdo.ins.INSTool;
/**
 *
 * <p>Title: 医保动作类</p>
 *
 * <p>Description: 医保动作类</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2011</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2011.12.07
 * @version 1.0
 */
public class INSAction
    extends TAction {
    /**
     * 医保挂号保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onSaveREG(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();
        result = INSTool.getInstance().onSaveREG(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

    /**
     * 医保退挂保存
     * @param parm TParm
     * @return TParm
     */
    public TParm onReturnREG(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();
        result = INSTool.getInstance().onReturnREG(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }
}
