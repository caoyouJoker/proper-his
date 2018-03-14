package action.inv;

import jdo.inv.INVTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: 物资事务</p>
 *
 * <p>Description: 物资事务</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: javahis</p>
 *
 * @author fudw
 * @version 1.0
 */

public class INVAction
    extends TAction {
//    /**
//     * 保存验收入库
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveInvVerifyin(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onSaveInvVerifyin(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//
//    }
//
//    /**
//     * 保存供应室入库
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveInvDispense(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onSaveIvndspen(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
    /**
     * 保存手术包打包入库
     * @param parm TParm
     * @return TParm
     */
    public TParm onSavePackAge(TParm parm) {
//    	System.out.println("------INVActionGYS------");
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();
        result = INVTool.getInstance().onSavePackAgeGYS(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
//
//    /**
//     * 保存供应室出库
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveOutStock(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveOutStockPack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }

    /**
     * 保存消毒和回收
     * @param parm TParm
     * @return TParm
     */
    public TParm saveBackPackAndDisnfection(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();
        result = INVTool.getInstance().saveBackPackAndDisnfection(parm,
            connection);
        if (result == null || result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

//    /**
//     * 保存手术包补充打包
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveRePackAge(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveRePack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * 保存手术包拆包
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm saveTearPack(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveTearPack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * 保存供应室退库
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveBackDispense(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveBackDispense(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * 保存手术包自动补充打包
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onSaveAutoRePackAge(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().saveAutoRePack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.rollback();
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * zhangyong20091112
//     * 取消供应室出库
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onCancelInvDispense(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onCancelInvDispense(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * 诊疗包回收消毒
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm saveCallBackPack(TParm parm){
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onSaveCallBackPack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
//
//    /**
//     * 诊疗包借用归还
//     * @param parm TParm
//     * @return TParm
//     */
//    public TParm onBorrowBack(TParm parm) {
//        TParm result = new TParm();
//        if (parm == null)
//            return result.newErrParm( -1, "参数为空");
//        TConnection connection = getConnection();
//        result = INVTool.getInstance().onBorrowBack(parm, connection);
//        if (result == null || result.getErrCode() < 0) {
//            connection.close();
//            return result;
//        }
//        connection.commit();
//        connection.close();
//        return result;
//    }
}
