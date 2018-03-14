package com.javahis.ui.root;

import com.dongyang.control.TControl;
import com.dongyang.root.client.SocketLink;
import com.javahis.system.root.RootClientListener;
import jdo.sys.Operator;

/**
 *
 * <p>Title: 在线通讯添加好友请求对话框控制类</p>
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
public class AddFriendSendControl extends TControl{
    private String id;
    private String name;
    /**
     * 初始化
     */
    public void onInit()
    {
        String[] parm = (String[])getParameter();
        if(parm == null)
            return;
        id = parm[0];
        name = parm[1];
        setText("ID",id);
        setText("NAME",name);
        setText("MESSAGE","您好我是" + Operator.getName() + "！");
    }
    /**
     * 确定
     */
    public void onOK()
    {
        closeWindow();
        RootClientListener.getInstance().getClient().sendCommand(id,SocketLink.ADD_FRIEND,getText("MESSAGE"));
    }
}
