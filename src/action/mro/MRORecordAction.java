package action.mro;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MRORecordActionTool;

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
public class MRORecordAction
    extends TAction {
    /**
     * 修改首页所有数据
     * @param parm TParm
     * @return TParm
     */
    public TParm updateDate(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = MRORecordActionTool.getInstance().updateRecordDate(parm,connection);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
			connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
//    /**
//     * 测试用方法
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm test(TParm parm){
//        TParm result = new TParm();
//        TConnection connection = getConnection();
//        result = MROInterfaceTool.getInstance().updateTransDept(parm,connection);
//        connection.commit();
//        connection.close();
//        return result;
//    }
}
