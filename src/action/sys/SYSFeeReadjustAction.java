package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import jdo.sys.SYSFeeReadjustMTool;
import com.dongyang.db.TConnection;
import jdo.sys.SYSFeeReadjustDTool;
import com.dongyang.manager.TIOM_Database;

/**
 * <p>
 * Title:调价计划Action
 * </p>
 *
 * <p>
 * Description:调价计划Action
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.9.18
 * @version 1.0
 */
public class SYSFeeReadjustAction
    extends TAction {

    public SYSFeeReadjustAction() {
    }

    /**
     * 新增调价计划主项
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertM(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onInsert(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 更新调价计划主项
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateM(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onUpdateM(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 更新调价计划细项
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateD(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustDTool.getInstance().onUpdateSYSFeeReadjustD(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 删除调价计划
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeleteM(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onDeleteM(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 执行调价计划
     * @param parm TParm
     * @return TParm
     */
    public TParm onCreateSYSFeeReadjust(TParm parm) {
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onCreateSYSFeeReadjust(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * SYS_FEE调用调价计划新增
     * @param parm TParm
     * @return TParm
     */
    public TParm onSYSFeeReadjustPatch(TParm parm) {
        //System.out.println("SYS_FEE调用调价计划新增");
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onSYSFeeReadjustPatch(parm,
            connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * SYS_FEE调用调价计划更新
     * @param parm TParm
     * @return TParm
     */
    public TParm onUpdateSYSFeeReadjustPatch(TParm parm) {
        //System.out.println("SYS_FEE调用调价计划更新");
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onUpdateSYSFeeReadjustPatch(
            parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * SYS_FEE调用调价计划立即执行
     * @param parm TParm
     * @return TParm
     */
    public TParm onInsertSYSFeeReadjust(TParm parm) {
        //System.out.println("SYS_FEE调用调价计划立即执行");
        TConnection connection = getConnection();
        TParm result = new TParm();
        result = SYSFeeReadjustMTool.getInstance().onInsertSYSFeeReadjust(
            parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        TIOM_Database.logTableAction("SYS_FEE");

        return result;
    }
}
