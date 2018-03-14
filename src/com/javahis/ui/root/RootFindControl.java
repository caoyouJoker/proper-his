package com.javahis.ui.root;

import com.dongyang.control.TControl;
import com.javahis.system.root.RootClientListener;
import com.dongyang.ui.TTable;
import com.javahis.system.root.CheckThread;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import jdo.sys.Operator;
import com.dongyang.root.client.SocketLink;

/**
 *
 * <p>Title: 在线通讯查找好友</p>
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
public class RootFindControl extends TControl{
    private TTable table;
    /**
     * 初始化
     */
    public void onInit()
    {
        table = (TTable)getComponent("TABLE");
    }
    /**
     * 得到查找ID
     * @return String
     */
    public String getID()
    {
        return getText("ID");
    }
    /**
     * 得到查找昵称
     * @return String
     */
    public String getName()
    {
        return getText("NAME");
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        RootClientListener.getInstance().setFindWindow(null);
        return true;
    }
    /**
     * 查找
     */
    public void onFind()
    {
        TParm parm = CheckThread.findUser(getID(),getName());
        if(parm.getErrCode() < 0)
            return;
        table.setParmValue(parm);
        if(parm.getCount() > 0)
            table.setSelectedRow(0);
    }
    /**
     * 添加好友
     */
    public void onADD()
    {
        int row = table.getSelectedRow();
        if(row == -1)
            return;
        String id = TypeTool.getString(table.getValueAt(row,0));
        String name = TypeTool.getString(table.getValueAt(row,1));
        if(id == null)
            return;
        if(id.equals(Operator.getID()))
        {
            messageBox_("不能将自己加位好友!");
            return;
        }
        if(CheckThread.isFriend(id))
        {
            messageBox_(name + "已经是您的好友了!");
            return;
        }
        int addType = CheckThread.getAddType(id);
        if(addType == 1)
        {
            messageBox_("对方拒绝加为好友!");
            return;
        }
        RootClientListener.getInstance().openAddFindSend(id,name);
    }
}
