package com.javahis.system.root;

import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.root.client.SocketLink;
import com.dongyang.util.StringTool;

/**
 *
 * <p>Title: 测试线称</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.22
 * @version 1.0
 */
public class CheckThread implements Runnable{
    /**
     * 监听主对象
     */
    private RootClientListener listener;
    /**
     * 线称
     */
    private static Thread thread;
    /**
     * 构造器
     * @param listener RootClientListener
     */
    public CheckThread(RootClientListener listener)
    {
        this.listener = listener;
    }
    /**
     * 启动
     * @param listener RootClientListener
     */
    public static void start(RootClientListener listener)
    {
        thread = new Thread(new CheckThread(listener));
        thread.start();
    }
    /**
     * 休眠
     */
    public void sleep()
    {
        try{
            thread.sleep(300);
        }catch(Exception e)
        {

        }
    }
    /**
     * 得到数据工具
     * @return TJDODBTool
     */
    public static TJDODBTool getDBTool()
    {
        return TJDODBTool.getInstance();
    }
    /**
     * 是否注册
     * @return boolean
     */
    public static boolean isRegister()
    {
        TParm parm = new TParm(getDBTool().select("SELECT COUNT(*)AS COUNT FROM SKT_USER WHERE ID='" + Operator.getID() + "'"));
        return parm.getInt("COUNT",0) > 0;
    }
    /**
     * 得到昵称
     * @return String
     */
    public static String getName()
    {
        return getName(Operator.getID());
    }
    /**
     * 得到昵称
     * @param id String
     * @return String
     */
    public static String getName(String id)
    {
        TParm parm = new TParm(getDBTool().select("SELECT NAME FROM SKT_USER WHERE ID='" + id + "'"));
        return parm.getValue("NAME",0);
    }
    /**
     * 得到密码
     * @return String
     */
    public static String getPassword()
    {
        TParm parm = new TParm(getDBTool().select("SELECT PASSWORD FROM SKT_USER WHERE ID='" + Operator.getID() + "'"));
        return parm.getValue("PASSWORD",0);
    }
    /**
     * 得到用户密码
     * @param userID String
     * @return String
     */
    public static String getUserPassword(String userID)
    {
        TParm parm = new TParm(getDBTool().select("SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID='" + userID + "'"));
        return StringTool.decrypt(parm.getValue("USER_PASSWORD",0));
    }
    /**
     * 设置密码
     * @param password String
     * @return boolean
     */
    public static boolean setPassword(String password)
    {
        TParm parm = new TParm(getDBTool().update("UPDATE SKT_USER SET PASSWORD='" + password + "' WHERE ID='" + Operator.getID() + "'"));
        return parm.getErrCode() == 0;
    }
    /**
     * 设置自动登录
     * @param autoLogin boolean
     * @return boolean
     */
    public static boolean setAutoLogin(boolean autoLogin)
    {
        TParm parm = new TParm(getDBTool().update("UPDATE SKT_USER SET AUTO_LOGIN='" + (autoLogin?"Y":"N") + "' WHERE ID='" + Operator.getID() + "'"));
        return parm.getErrCode() == 0;
    }
    /**
     * 是否自动登录
     * @return boolean
     */
    public static boolean isAutoLogin()
    {
        TParm parm = new TParm(getDBTool().select("SELECT AUTO_LOGIN FROM SKT_USER WHERE ID='" + Operator.getID() + "'"));
        return parm.getBoolean("AUTO_LOGIN",0);
    }
    /**
     * 注册
     * @param id String
     * @param name String
     * @param password String
     * @param onlyOne boolean
     * @param autoLogin boolean
     * @param addType int 0 允许 1 拒绝 2 需要确认
     * @return boolean
     */
    public static boolean register(String id,String name,String password,boolean onlyOne,boolean autoLogin,int addType)
    {
        TParm parm = new TParm(getDBTool().update("INSERT INTO SKT_USER (ID,NAME,PASSWORD,ONLY_ONE,AUTO_LOGIN,ADD_TYPE)" +
                                                  " VALUES('" + id + "','" + name + "','" + password + "','" + (onlyOne?"Y":"N") + "','" + (autoLogin?"Y":"N") + "','" + addType + "')"));
        if(parm.getErrCode() < 0)
        {
            System.out.println(parm.getErrText());
            return false;
        }
        createGroup(id,"01","我的好友",1);
        SocketLink.running("#System.Reset#","");
        return true;
    }
    /**
     * 得到添加好友类型
     * @return int
     */
    public static int getAddType()
    {
        return getAddType(Operator.getID());
    }
    /**
     * 得到添加好友类型
     * @param id String
     * @return int
     */
    public static int getAddType(String id)
    {
        TParm parm = new TParm(getDBTool().select("SELECT ADD_TYPE FROM SKT_USER WHERE ID='" + id + "'"));
        return parm.getInt("ADD_TYPE",0);
    }
    /**
     * 创建组
     * @param userID String
     * @param groupId String
     * @param groupName String
     * @param seq int
     * @return boolean
     */
    public static boolean createGroup(String userID,String groupId,String groupName,int seq)
    {
        TParm parm = new TParm(getDBTool().update("INSERT INTO SKT_GROUP_LIST (USER_ID,GROUP_ID,GROUP_NAME,SEQ)" +
                                                  " VALUES('" + userID + "','" + groupId + "','" + groupName + "','" + seq + "')"));
        return parm.getErrCode() == 0;
    }
    /**
     * 得到组
     * @return TParm
     */
    public static TParm getGroup()
    {
        return new TParm(getDBTool().select("SELECT GROUP_ID,GROUP_NAME FROM SKT_GROUP_LIST WHERE USER_ID='" + Operator.getID() + "' ORDER BY SEQ"));
    }
    /**
     * 创建组成员
     * @param linkID String
     * @param name String
     * @param groupID String
     * @return boolean
     */
    public static boolean createGroupUser(String linkID,String name,String groupID)
    {
        return createGroupUser(Operator.getID(),linkID,name,groupID,getNewGroupUserSeq(groupID));
    }
    /**
     * 创建组成员
     * @param userID String
     * @param linkID String
     * @param name String
     * @param groupID String
     * @param seq int
     * @return boolean
     */
    public static boolean createGroupUser(String userID,String linkID,String name,String groupID,int seq)
    {
        TParm parm = new TParm(getDBTool().update("INSERT INTO SKT_GROUP (ID,LINK_ID,NAME,GROUP_ID,SEQ)" +
                                                  " VALUES('" + userID + "','" + linkID + "','" + name + "','" + groupID + "','" + seq + "')"));
        return parm.getErrCode() == 0;
    }
    /**
     * 得到新增
     * @param groupID String
     * @return int
     */
    public static int getNewGroupUserSeq(String groupID)
    {
        TParm parm = new TParm(getDBTool().select("SELECT MAX(SEQ) AS SEQ FROM SKT_GROUP WHERE ID='" + Operator.getID() + "' AND GROUP_ID='" + groupID + "'"));
        int x = parm.getInt("SEQ",0);
        if(x < 0)
            x = 0;
        return x + 1;
    }
    /**
     * 得到组成员
     * @param groupID String
     * @return TParm
     */
    public static TParm getGroupUser(String groupID)
    {
        return new TParm(getDBTool().select("SELECT LINK_ID,NAME FROM SKT_GROUP "+
                                            "WHERE ID='" + Operator.getID() + "' "+
                                            "AND GROUP_ID='" + groupID + "'"+
                                            "ORDER BY SEQ"));
    }
    /**
     * 是否是好友
     * @param id String
     * @return boolean
     */
    public static boolean isFriend(String id)
    {
        TParm parm = new TParm(getDBTool().select("SELECT COUNT(*) AS COUNT FROM SKT_GROUP "+
                                            "WHERE ID='" + Operator.getID() + "' "+
                                            "AND LINK_ID='" + id + "'"));
        return parm.getInt("COUNT",0) > 0;
    }
    /**
     * 查找
     * @param id String
     * @param name String
     * @return TParm
     */
    public static TParm findUser(String id,String name)
    {
        String sql = "SELECT ID,NAME FROM SKT_USER ";
        if(id != null && id.length() > 0)
            sql += "WHERE ID='" + id + "'";
        else if(name != null && name.length() > 0)
            sql += "WHERE NAME like '" + name + "%'";
        return new TParm(getDBTool().select(sql));
    }
    /**
     * 运行
     */
    public void run()
    {
        sleep();
        //没有注册
        if(!isRegister())
            return;
        //不是自动登录
        if(!isAutoLogin())
            return;
        //读取密码
        listener.setPassword(getPassword());
        //登录
        listener.login();
    }
    public static void main(String args[])
    {
        com.javahis.util.JavaHisDebug.initClient();
        System.out.println(CheckThread.getUserPassword("ehui"));
        //CheckThread.setPassword("123");
        //System.out.println(CheckThread.isRegister() + " " + CheckThread.getPassword());
        //CheckThread.register("123","abc","ppp",true,true);

    }
}
