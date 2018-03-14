package action.mro;

import com.dongyang.action.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.mro.MROQueueTool;

/**
 * <p>Title: 病历借阅管理</p>
 *
 * <p>Description: 病历借阅管理</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk  2009-5-13
 * @version 1.0
 */
public class MROQueueAction
    extends TAction {
    public MROQueueAction() {
    }
    /**
     * 病历出库
     * @param parm TParm
     * @return TParm
     */
    public TParm updateOUT(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = MROQueueTool.getInstance().updateOUT(parm,connection);
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
     * 病历入库
     * @param parm TParm
     * @return TParm
     */
    public TParm updateIN(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = MROQueueTool.getInstance().updateOUT(parm,connection);
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
     * 病历入库 (出院病历归档)
     * @param parm TParm
     * @return TParm
     */
    public TParm updateIN_FLG(TParm parm){
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "参数不能为空！");
            return result;
        }
        TConnection connection = getConnection();
        result = MROQueueTool.getInstance().updateIn_flg(parm,connection);
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
