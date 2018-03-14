package com.javahis.system.root;

import com.dongyang.root.client.SocketLink;

/**
 *
 * <p>Title: 登录线程</p>
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
public class LoginThread implements Runnable{
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
    public LoginThread(RootClientListener listener)
    {
        this.listener = listener;
    }
    /**
     * 启动
     * @param listener RootClientListener
     */
    public static void start(RootClientListener listener)
    {
        thread = new Thread(new LoginThread(listener));
        thread.start();
    }
    /**
     * 休眠
     */
    public void sleep()
    {
        try{
            thread.sleep(5000);
        }catch(Exception e)
        {

        }
    }
    /**
     * 运行
     */
    public void run()
    {
        while(!listener.isClose())
        {
            listener.setSysStatus("在线通讯连接中...");
            listener.setClient(SocketLink.running(listener.getUserID(),
                                                  listener.getPassword()));
            if (!listener.getClient().isClose()) {
                listener.listener();
                return;
            }
            listener.setSysStatus(listener.getClient().getErrText());
            sleep();
        }
    }
}
