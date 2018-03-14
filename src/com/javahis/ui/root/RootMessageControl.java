package com.javahis.ui.root;

import com.dongyang.control.TControl;
import com.dongyang.ui.TRootPanel;
import com.javahis.system.root.CheckThread;
import com.javahis.system.root.RootClientListener;
import java.util.Date;
import java.sql.Timestamp;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import com.dongyang.ui.TTextArea;

/**
 *
 * <p>Title: 在线通讯消息窗口控制类</p>
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
public class RootMessageControl extends TControl{
    private String id;
    private String name;
    private TRootPanel rootPanel;
    private TTextArea messageArea;
    /**
     * 初始化
     */
    public void onInit()
    {
        rootPanel = (TRootPanel)getComponent("Root");
        messageArea = (TTextArea)getComponent("MESSAGE");
        String[] parm = (String[])getParameter();
        if(parm == null)
            return;
        id = parm[0];
        name = CheckThread.getName(id);
        rootPanel.setTitle(name);
    }
    /**
     * 得到发送消息
     * @return String
     */
    public String getSendMessage()
    {
        return getText("SENDM_ESSAGE");
    }
    /**
     * 发送消息
     */
    public void onSend()
    {
        String message = getSendMessage();
        inMessage(new Date(),Operator.getName(),message);
        RootClientListener.getInstance().getClient().sendMessage(id,message);
        setText("SENDM_ESSAGE","");
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        RootClientListener.getInstance().setMessageWindow(id,null);
        return true;
    }
    /**
     * 接收消息
     * @param time Timestamp
     * @param name String
     * @param message String
     */
    public void inMessage(Timestamp time,String name,String message)
    {
        inMessage(new Date(time.getTime()),name,message);
    }
    /**
     * 接收消息
     * @param date Date
     * @param name String
     * @param message String
     */
    public void inMessage(Date date,String name,String message)
    {
        StringBuffer s = new StringBuffer();
        s.append(getText("MESSAGE"));
        if(s.length() > 0)
            s.append("\n");
        s.append(StringTool.getString(date,"HH:mm:ss") + " " + name + "\n");
        s.append(message);
        messageArea.setText(s.toString());
        messageArea.getTextArea().setSelectionStart(s.length());
        messageArea.getTextArea().setSelectionEnd(s.length());
    }
}
