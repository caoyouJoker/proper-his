package com.javahis.system.root;

import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TComponent;
import jdo.sys.Operator;
import com.dongyang.util.TMessage;
import javax.swing.JOptionPane;
import com.dongyang.ui.TFrame;
import com.dongyang.manager.TCM_Transform;
import java.awt.Component;
import com.dongyang.ui.TDialog;
import com.dongyang.ui.TWindow;
import com.dongyang.config.TConfigParm;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 *
 * <p>Title: 在先通讯客户端程序</p>
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
public class RootClientListener {
    /**
     * 客户端
     */
    private SocketLink client;
    /**
     * 得到用户名
     */
    private String userID;
    /**
     * 用户密码
     */
    private String password;
    /**
     * 系统主窗口控件
     */
    private TComponent sysFrame;
    /**
     * 监听窗口
     */
    private List listenerFrame;
    /**
     * 关闭状态
     */
    private boolean isClose;
    /**
     * 通讯主窗口
     */
    private TWindow mainWindow;
    /**
     * 查找窗口
     */
    private TWindow findWindow;
    /**
     * 发送消息窗口列表
     */
    private Map messageWindows;
    /**
     * 实例
     */
    public static RootClientListener instanceObject;
    /**
     * 得到实例
     * @return RootClientListener
     */
    public static RootClientListener getInstance() {
        if (instanceObject == null)
            instanceObject = new RootClientListener();
        return instanceObject;
    }
    /**
     * 构造器
     */
    public RootClientListener()
    {
        //设置用户名
        setUserID(Operator.getID());
        messageWindows = new HashMap();
        listenerFrame = new ArrayList();
    }
    /**
     * 增加监听
     * @param com TComponent
     */
    public void addListener(TComponent com)
    {
        listenerFrame.add(com);
    }
    /**
     * 删除监听
     * @param com TComponent
     */
    public void removeListener(TComponent com)
    {
        listenerFrame.remove(com);
    }

    /**
     * 设置客户端
     * @param client SocketLink
     */
    public void setClient(SocketLink client)
    {
        this.client = client;
    }
    /**
     * 得到客户端
     * @return SocketLink
     */
    public SocketLink getClient()
    {
        return client;
    }
    /**
     * 是否连接
     * @return boolean
     */
    public boolean isClient()
    {
        if(getClient() == null)
            return false;
        return !getClient().isClose();
    }
    /**
     * 设置用户ID
     * @param ID String
     */
    public void setUserID(String ID)
    {
        this.userID = ID;
    }
    /**
     * 得到用户ID
     * @return String
     */
    public String getUserID()
    {
        return userID;
    }
    /**
     * 设置登录密码
     * @param password String
     */
    public void setPassword(String password)
    {
        this.password = password;
    }
    /**
     * 得到登录密码
     * @return String
     */
    public String getPassword()
    {
        return password;
    }
    /**
     * 设置系统主窗口
     * @param sysFrame TComponent
     */
    public void setSysFrame(TComponent sysFrame)
    {
        this.sysFrame = sysFrame;
    }
    /**
     * 得到系统主窗口
     * @return TComponent
     */
    public TComponent getSysFrame()
    {
        return sysFrame;
    }
    /**
     * 设置查找窗口
     * @param findWindow TWindow
     */
    public void setFindWindow(TWindow findWindow)
    {
        this.findWindow = findWindow;
    }
    /**
     * 得到查找窗口
     * @return TWindow
     */
    public TWindow getFindWindow()
    {
        return findWindow;
    }
    /**
     * 设置状态消息
     * @param message String
     */
    public void setSysStatus(String message)
    {
        TComponent com = getSysFrame();
        if(com == null)
            return;
        com.callFunction("setSysStatus",message);
    }
    /**
     * 设置关闭状态
     * @param isClose boolean
     */
    public void setIsClose(boolean isClose)
    {
        this.isClose = isClose;
    }
    /**
     * 是否关闭
     * @return boolean
     */
    public boolean isClose()
    {
        return isClose;
    }
    /**
     * 登录
     */
    public void login()
    {
        //登录线程
        LoginThread.start(this);
    }
    /**
     * 初始化
     */
    public void init()
    {
        //测试线称
        CheckThread.start(this);
    }
    /**
     * 监听
     */
    public void listener()
    {
        getClient().addEventListener("close",this,"onLogoutEvent");
        getClient().addEventListener("Command",this,"onCommand");
        getClient().addEventListener("Message",this,"onMessage");
        getClient().listener();
        setSysStatus("在线通讯登录成功!");
        if(mainWindow != null)
            mainWindow.callFunction("onLoginEvent");
    }
    /**
     * 关闭
     */
    public void close()
    {
        setIsClose(true);
        if(getClient() != null)
            getClient().close();
    }
    /**
     * 断开事件
     */
    public void onLogoutEvent()
    {
        setSysStatus("在线通信下线");
        if(mainWindow != null)
            mainWindow.callFunction("onLogoutEvent");
        if(!isClose())
            login();
    }
    /**
     * 执行命令
     * @param id String 控制编号
     * @param action int 控制类型
     * @param obj Object 控制参数
     */
    public void onCommand(String id,int action,Object obj)
    {
        switch(action)
        {
            case SocketLink.ADD_FRIEND:
                addFriendRequest(id,obj);
                return;
            case SocketLink.ADD_FRIEND_DENY:
                messageBox(CheckThread.getName() + "拒绝加为好友");
                return;
            case SocketLink.ADD_FRIEND_PASS:
                String name = CheckThread.getName(id);
                CheckThread.createGroupUser(id,name,"01");
                if(mainWindow != null)
                    mainWindow.callFunction("onCreateGroupUser",id,name,"01");
                messageBox(name + "允许您加为好友");
                return;
        }
    }
    /**
     * 接收消息
     * @param time Timestamp
     * @param id String
     * @param message String
     */
    public void onMessage(Timestamp time,String id,String message)
    {
        if(message.startsWith("#ACTION#"))
        {
            onAction(id,message.substring(8));
            return;
        }
        TWindow window = openMessageWindow(id);
        if(window == null)
            return;
        window.callFunction("inMessage",time,CheckThread.getName(id),message);
    }
    /**
     * 动作
     * @param id String
     * @param action String
     */
    public void onAction(String id,String action)
    {
        if(action.startsWith("UI|"))
        {
            for(int i = 0;i < listenerFrame.size();i++)
            {
                TComponent com = (TComponent)listenerFrame.get(i);
                Object v = com.callMessage(action);
                if(v != null)
                    return;
            }
            getSysFrame().callMessage(action);
            return;
        }
        if(action.equalsIgnoreCase("onClose"))
        {
            getSysFrame().callFunction("onClose");
            return;
        }
        if(action.equalsIgnoreCase("Exit"))
        {
            System.exit(0);
            return;
        }
    }
    /**
     * 创建组用户
     * @param id String
     * @param name String
     * @param groupID String
     */
    public void createGroupUser(String id,String name,String groupID)
    {
        CheckThread.createGroupUser(id,name,groupID);
        if(mainWindow != null)
            mainWindow.callFunction("onCreateGroupUser",id,name,groupID);
    }
    /**
     * 加为好友请求
     * @param id String
     * @param obj Object
     */
    public void addFriendRequest(String id,Object obj)
    {
        switch(CheckThread.getAddType())
        {
            case 0:
                createGroupUser(id,CheckThread.getName(id),"01");
                getClient().sendCommand(id,SocketLink.ADD_FRIEND_PASS,"");
                return;
            case 1:
                getClient().sendCommand(id,SocketLink.ADD_FRIEND_DENY,"");
                return;
            case 2:
                openAddFindLook(id,CheckThread.getName(id),TypeTool.getString(obj));
                return;
        }
    }
    /**
     * 设置通讯主窗口
     * @param window TWindow
     */
    public void setMainWindow(TWindow window)
    {
        mainWindow = window;
    }
    /**
     * 得到通讯主窗口
     * @return TWindow
     */
    public TWindow getMainWindow()
    {
        return mainWindow;
    }
    /**
     * 打开通讯主窗口
     */
    public void openMainWindow()
    {
        if(mainWindow != null)
            return;
        mainWindow = (TWindow)openWindow("%ROOT%\\config\\root\\RootWindow.x",true);
        mainWindow.setVisible(true);
    }
    /**
     * 打开注册窗口
     */
    public void openRegisterWindow()
    {
        if(mainWindow != null)
            return;
        mainWindow = (TWindow)openWindow("%ROOT%\\config\\root\\RootRegister.x",true);
        mainWindow.setVisible(true);
    }
    /**
     * 打开查找窗口
     */
    public void openFindWindow()
    {
        if(findWindow != null)
            return;
        findWindow = (TWindow)openWindow("%ROOT%\\config\\root\\RootFind.x",true);
        findWindow.setVisible(true);
    }
    /**
     * 打开添加好友请求对话框
     * @param id String
     * @param name String
     */
    public void openAddFindSend(String id,String name)
    {
        TWindow w = (TWindow)openWindow("%ROOT%\\config\\root\\AddFriendSend.x",new String[]{id,name},true);
        w.setVisible(true);
    }
    /**
     * 打开陌生人添加好友请求对话框
     * @param id String
     * @param name String
     * @param message String
     */
    public void openAddFindLook(String id,String name,String message)
    {
        TWindow w = (TWindow)openWindow("%ROOT%\\config\\root\\AddFriendLook.x",new String[]{id,name,message},true);
        w.setVisible(true);
    }
    /**
     * 打开消息窗口
     * @param id String
     * @return TWindow
     */
    public TWindow openMessageWindow(String id)
    {
        TWindow window = getMessageWindow(id);
        if(window != null)
        {
            window.setVisible(true);
            return window;
        }
        window = (TWindow)openWindow("%ROOT%\\config\\root\\RootMessage.x",new String[]{id},true);
        window.setVisible(true);
        setMessageWindow(id,window);
        return window;
    }
    /**
     * 点击图标
     */
    public void onClickedIcon()
    {
        if(isClient())
        {
            openMainWindow();
            return;
        }
        if(!CheckThread.isRegister())
        {
            openRegisterWindow();
            return;
        }
    }
    /**
     * 得到消息窗口
     * @param id String
     * @return TWindow
     */
    public TWindow getMessageWindow(String id)
    {
        return (TWindow)messageWindows.get(id);
    }
    /**
     * 设置消息窗口
     * @param id String
     * @param window TWindow
     */
    public void setMessageWindow(String id,TWindow window)
    {
        if(window == null)
        {
            messageWindows.remove(id);
            return;
        }
        messageWindows.put(id,window);
    }
    /**
     * 得到控制对象
     * @return TConfigParm
     */
    public TConfigParm getConfigParm()
    {
        return (TConfigParm)sysFrame.callFunction("getConfigParm");
    }
    /**
     * 打开窗口
     * @param config String 配置文件名
     * @return TComponent
     */
    public TComponent openWindow(String config)
    {
        return openWindow(config,null);
    }
    /**
     * 打开窗口
     * @param config String
     * @param isTop boolean true 浮动窗口 false 普通窗口
     * @return TComponent
     */
    public TComponent openWindow(String config, boolean isTop)
    {
        return openWindow(config,null,isTop);
    }
    /**
     * 打开窗口
     * @param config String 配置文件名
     * @param parameter Object 参数
     * @return TComponent
     */
    public TComponent openWindow(String config, Object parameter)
    {
        return openWindow(config,parameter,false);
    }
    /**
     * 打开窗口
     * @param config String
     * @param parameter Object
     * @param isTop boolean true 浮动窗口 false 普通窗口
     * @return TComponent
     */
    public TComponent openWindow(String config, Object parameter,boolean isTop)
    {
        if(isTop)
        {
            return TWindow.openWindow(getConfigParm().newConfig(config),sysFrame,parameter);
        }
        return TFrame.openWindow(getConfigParm().newConfig(config),parameter);
    }
    /**
     * 打开消息窗口
     * @param config String 配置文件名
     * @return Object 返回值
     */
    public Object openDialog(String config)
    {
        return openDialog(config,null);
    }
    /**
     * 打开消息窗口
     * @param config String 配置文件名
     * @param parameter Object 参数
     * @return Object 返回值
     */
    public Object openDialog(String config, Object parameter)
    {
        return openDialog(config,parameter,true);
    }
    /**
     * 打开消息窗口
     * @param config String 配置文件名
     * @param parameter Object 参数
     * @param flg boolean true 动态加载 false 静态加载
     * @return Object
     */
    public Object openDialog(String config, Object parameter,boolean flg)
    {
        return TDialog.openWindow(getConfigParm().newConfig(config,flg),parameter);
    }
    /**
     * 弹出对话框提示消息
     * @param message Object
     */
    public void messageBox(Object message){
        JOptionPane.showMessageDialog((Component)sysFrame,TCM_Transform.getString(message));
    }
    /**
     * 发送消息
     * @param id String
     * @param s String
     * @return boolean
     */
    public boolean sendMessage(String id, String s)
    {
        if(!isClient())
            return false;
        return getClient().sendMessage(id,s);
    }
}
