package action.ins;

import jdo.ins.INSUpLoadTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 *
 * <p>Title: 医保申报动作类</p>
 *
 * <p>Description: 医保申报动作类</p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2012.02.10
 * @version 1.0
 */
public class INSUpLoadAction extends TAction {
    /**
     * 更新对应数据库标记位
     * @param parm TParm
     * @return TParm
     */
    public TParm saveUpLoadData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();
        //9.医保申报后更新//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbs(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }

        //10.更新费用明细档//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbsOrder(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //11.更新资格确认书状态//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbsAdmConfirm(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //12.更新上传明细表//TODO:
        result = INSUpLoadTool.getInstance().onUpINSIbsUpload(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 撤销申报更新
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdAppData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();
        result = INSUpLoadTool.getInstance().onUpInsIbsForCJ(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        result = INSUpLoadTool.getInstance().onUpInsInsAdmConfirmForCJ(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    public TParm checkConfirmData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();
        //得到医保状态
        result = INSUpLoadTool.getInstance().getInsStatusForCJ(parm);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
       // System.out.println("得到医保状态");
        //审核资格确认书
        result = INSUpLoadTool.getInstance().onUpInsInsAdmConfirmForCheckCJ(
                parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //System.out.println("审核资格确认书");
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 更新新的就诊顺序号
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdAdmSeqData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();

        result = INSUpLoadTool.getInstance().onUpInsIbsAdmSeq(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }

        result = INSUpLoadTool.getInstance().onUpInsIbsOrderAdmSeq(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 费用明细上传回写,费用明细上传回写INS_ADM_CONFIRM
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdInsIbsBackData(TParm parm) {
        TParm result = new TParm();
        if (parm == null)
            return result.newErrParm( -1, "参数为空");
        TConnection connection = getConnection();

        //费用明细上传回写
        result = INSUpLoadTool.getInstance().onUpInsIbsBack(parm, connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        //费用明细上传回写INS_ADM_CONFIRM
        result = INSUpLoadTool.getInstance().onUpInsAdmConfirmBack(parm,
                connection);
        if (result == null || result.getErrCode() < 0) {
            connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

}
