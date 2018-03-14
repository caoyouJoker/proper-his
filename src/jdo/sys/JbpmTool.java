package jdo.sys;

import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import java.util.Map;
import java.util.HashMap;

/**
 *
 * <p>Title: JBPM 工具</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.5.12
 * @version 1.0
 */
public class JbpmTool {
    /**
     * 重新加载Action
     */
    public static void resetAction()
    {
        TIOM_AppServer.resetAction();
    }
    /**
     * 清除数据库
     * @return boolean
     */
    public static boolean clearDataBase()
    {
        TParm reset = TIOM_AppServer.executeAction("action.sys.JbpmAction","createSchema",new TParm());
        if(reset.getErrCode() < 0)
        {
            System.out.println(reset.getErrText());
            return false;
        }
        return true;
    }
    /**
     * 创建默认用户
     * @return boolean
     */
    public static boolean createIdentity()
    {
        return createIdentity("JavaHisIdentity.db.xml");
    }
    /**
     * 创建用户
     * @param fileName String
     * @return boolean
     */
    public static boolean createIdentity(String fileName)
    {
        TParm parm = new TParm();
        parm.setData("FILE_NAME",fileName);
        TParm reset = TIOM_AppServer.executeAction("action.sys.JbpmAction","createIdentity",parm);
        if(reset.getErrCode() < 0)
        {
            System.out.println(reset.getErrText());
            return false;
        }
        return true;
    }
    /**
     * 部署流程
     * @param fileName String
     * @return boolean
     */
    public static boolean deployProcessDefinition(String fileName)
    {
        TParm parm = new TParm();
        parm.setData("FILE_NAME",fileName);
        TParm reset = TIOM_AppServer.executeAction("action.sys.JbpmAction","deployProcessDefinition",parm);
        if(reset.getErrCode() < 0)
        {
            System.out.println(reset.getErrText());
            return false;
        }
        return true;
    }
    /**
     * 启动实例
     * @param definitionName String
     * @param actorId String
     * @param taskVariables Map
     * @return long
     */
    public static long createStartTaskInstance(String definitionName,String actorId,Map taskVariables)
    {
        TParm parm = new TParm();
        parm.setData("DEFINITION_NAME",definitionName);
        parm.setData("ACTOR_ID",actorId);
        parm.setData("TASK_MAP",taskVariables);
        TParm reset = TIOM_AppServer.executeAction("action.sys.JbpmAction","createStartTaskInstance",parm);
        if(reset.getErrCode() < 0)
        {
            System.out.println(reset.getErrText());
            return -1;
        }
        return reset.getLong("PROCESS_INSTANCE_ID");
    }
    /**
     * 查找实例
     * @param actorId String
     * @return TParm
     */
    public static TParm findTaskInstances(String actorId)
    {
        TParm parm = new TParm();
        parm.setData("ACTOR_ID",actorId);
        TParm reset = TIOM_AppServer.executeAction("action.sys.JbpmAction","findTaskInstances",parm);
        if(reset.getErrCode() < 0)
        {
            System.out.println(reset.getErrText());
            return reset;
        }
        return reset;
    }
    /**
     * 下一个
     * @param actorId String
     * @param index int
     * @return boolean
     */
    public static boolean nextTask(String actorId,int index)
    {
        return nextTask(actorId,index,"");
    }
    /**
     * 下一个
     * @param actorId String
     * @param index int
     * @param to String
     * @return boolean
     */
    public static boolean nextTask(String actorId,int index,String to)
    {
        return nextTask(actorId,index,to,new HashMap());
    }
    /**
     * 下一个
     * @param actorId String
     * @param index int
     * @param to String
     * @param taskVariables Map
     * @return boolean
     */
    public static boolean nextTask(String actorId,int index,String to,Map taskVariables)
    {
        TParm parm = new TParm();
        parm.setData("ACTOR_ID",actorId);
        parm.setData("TO",to);
        parm.setData("INDEX",index);
        parm.setData("TASK_MAP",taskVariables);
        TParm reset = TIOM_AppServer.executeAction("action.sys.JbpmAction","nextTask",parm);
        if(reset.getErrCode() < 0)
        {
            System.out.println(reset.getErrText());
            return false;
        }
        return true;
    }
}
