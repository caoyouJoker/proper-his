package com.javahis.ui.root;

import com.dongyang.control.TControl;
import jdo.sys.Operator;
import com.dongyang.ui.TTree;
import com.javahis.system.root.RootClientListener;
import com.javahis.system.root.CheckThread;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;

/**
 *
 * <p>Title: 在线通讯窗口控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2009.4.21
 * @version 1.0
 */
public class RootWindowControl extends TControl{
    TTree tree;
    /**
     * 初始化
     */
    public void onInit()
    {
        tree = (TTree)getComponent("TREE");
        showName();
        //初始化树
        initTree();
    }
    /**
     * 显示名称
     */
    public void showName()
    {
        setValue("NAME",CheckThread.getName() + "[" +
                 (RootClientListener.getInstance().isClient()?"我在线上":"离线")
                 + "]");
    }
    /**
     * 关闭
     * @return boolean
     */
    public boolean onClosing()
    {
        RootClientListener.getInstance().setMainWindow(null);
        return true;
    }
    /**
     * 上线事件
     */
    public void onLoginEvent()
    {
        showName();
    }

    /**
     * 离线事件
     */
    public void onLogoutEvent()
    {
        showName();
    }
    /**
     * 初始化树
     */
    public void initTree()
    {
        TTreeNode root = tree.getRoot();
        root.setText("通讯薄");
        root.setType("Root");
        root.removeAllChildren();
        TParm parm = CheckThread.getGroup();
        for(int i = 0; i < parm.getCount();i++)
        {
            String id = parm.getValue("GROUP_ID",i);
            String name = parm.getValue("GROUP_NAME",i);
            TTreeNode node = new TTreeNode(name,"Group");
            node.setID(id);
            initGroup(node,id);
            root.add(node);
        }
        tree.update();
    }
    /**
     * 初始化组
     * @param root TTreeNode
     * @param groupID String
     */
    public void initGroup(TTreeNode root,String groupID)
    {
        TParm parm = CheckThread.getGroupUser(groupID);
        for(int i = 0; i < parm.getCount();i++)
        {
            String id = parm.getValue("LINK_ID",i);
            String name = parm.getValue("NAME",i);
            TTreeNode node = new TTreeNode(name,"User");
            node.setID(id);
            root.add(node);
        }
    }
    /**
     * 查找
     */
    public void onFind()
    {
        RootClientListener.getInstance().openFindWindow();
    }
    /**
     * 创建用户组
     * @param id String
     * @param name String
     * @param groupID String
     */
    public void onCreateGroupUser(String id,String name,String groupID)
    {
        TTreeNode root = tree.getRoot();
        TTreeNode node = null;
        for(int i = 0;i < root.getChildCount();i++)
        {
            TTreeNode groupNode = (TTreeNode)root.getChildAt(i);
            if(groupNode.getID().equals(groupID))
            {
                node = new TTreeNode(name,"User");
                node.setID(id);
                groupNode.add(node);
                return;
            }
        }
        tree.update();
        if(node != null)
            tree.setSelectNode(node);
    }
    /**
     * 双击树项目
     */
    public void onDoubleItemTree()
    {
        TTreeNode node = tree.getSelectionNode();
        if(node == null)
            return;
        RootClientListener.getInstance().openMessageWindow(node.getID());
    }
}
