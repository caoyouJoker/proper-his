package action.sys;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import jdo.sys.SYSPublishBoardTool;

/**
 * <p>Title: 公告栏业务类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author li.xiang@790130@gmail.com
 * @version 1.0
 */
public class SYSPublishBoardAction
    extends TAction {
    public SYSPublishBoardAction() {
    }

    /**
     * 发布公告
     * @param parm TParm
     * @return TParm
     */
    public TParm onPublishMessage(TParm parm) {
        //调用Tool获取对应的接收的用户getReceiveUsers;
        TParm users = SYSPublishBoardTool.getInstance().getReceiveUsers(
            parm);
        //System.out.println("in control users size++++++++++++" + users.getCount());
        parm.setData("RECEIVE_USERS", users);

        //起动事务发布公告
        TConnection connection = getConnection();
        TParm result = new TParm();
        //调用Tool保存方法；
        result = SYSPublishBoardTool.getInstance().sendPublishMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;

    }

    /**
     * 批量删除公告
     * @param parm TParm
     * @return TParm
     */
    public TParm onBatchDeletePublishMessage(TParm parm){
        TParm result = new TParm();
        //起动事务发布公告
        TConnection connection = getConnection();
        //调用Tool保存方法；
        result = SYSPublishBoardTool.getInstance().batchDeletePublishMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 删除公告
     * @param parm TParm
     * @return TParm
     */
    public TParm onDeletePublishMessage(TParm parm) {
        TParm result = new TParm();
        //起动事务发布公告
        TConnection connection = getConnection();
        //调用Tool保存方法；
        result = SYSPublishBoardTool.getInstance().deletePublishMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }

    /**
     * 读公告详细内容,并更新读标记及响应数
     * @param parm TParm
     * @return TParm
     */
    public TParm onReadMessage(TParm parm) {
        TParm result = new TParm();
        //起动事务发布公告
        TConnection connection = getConnection();
        //调用Tool保存方法；
        result = SYSPublishBoardTool.getInstance().readMessage(parm,
            connection);

        if (result.getErrCode() < 0) {
        	connection.rollback();
            connection.close();
            return result;
        }
        connection.commit();
        connection.close();
        return result;
    }
}
