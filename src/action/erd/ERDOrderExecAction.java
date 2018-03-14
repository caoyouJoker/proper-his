package action.erd;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.erd.ErdOrderExecTool;

/**
 * <p>Title: 急诊留观护士执行Action</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-11-12
 * @version 1.0
 */
public class ERDOrderExecAction
    extends TAction {
    public ERDOrderExecAction() {
    }

    /**
     * 执行入口
     * @param parm
     * @return TParm
     */
    public TParm onSave(TParm parm) {

        TParm result = new TParm();
        //创建一个连接，在多事物的时候连接各个操作使用
        TConnection connection = getConnection();

        //调用长期处置的展开方法
        result = ErdOrderExecTool.getInstance().onExec(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 取消执行入口
     * @param parm
     * @return TParm
     */
    public TParm onUndoSave(TParm parm) {

        TParm result = new TParm();
        //创建一个连接，在多事物的时候连接各个操作使用
        TConnection connection = getConnection();

        //调用长期处置的展开方法
        result = ErdOrderExecTool.getInstance().onUndoExec(parm, connection);
        if (result.getErrCode() < 0) {
            connection.close();
            return result;
        }

        connection.commit();
        connection.close();
        return result;
    }

}
