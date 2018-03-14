package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import jdo.sys.DictionaryTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import jdo.sys.Operator;

/**
 *
 * <p>Title: 系统维护控制类</p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.9.23
 * @version 1.0
 */
public class SYSSubSystemControl extends TControl{
    private static final String TREE = "SystemTree";
    private static final String TABLE = "SystemTable";
    /**
     * 上级组编号
     */
    private String groupID;
    /**
     * 上级编号
     */
    private String parentID;
    private TTreeNode treeRoot;
    /**
     * 当前动作
     */
    private String action;
    public void onInit()
    {
        super.onInit();
        //初始化树
        onInitTree();
        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED,"onTreeClicked");
        callFunction("UI|" + TABLE + "|addEventListener",TABLE + "->"+TTableEvent.CLICKED,this,"onTableClicked");
        onClear();
    }
    /**
     * 初始化树
     */
    public void onInitTree()
    {
        out("begin");
        treeRoot = (TTreeNode)callMessage("UI|" + TREE + "|getRoot");
        if(treeRoot == null)
            return;
        treeRoot.setText("系统结构");
        treeRoot.setGroup("POPEDOM");
        treeRoot.setID("SYS_SUBSYSTEM");
        treeRoot.setType("PATH");
        treeRoot.setValue("POPEDOM:SYS_SUBSYSTEM");
        treeRoot.removeAllChildren();
        downloadRootTree(treeRoot,"SYS_SUBSYSTEM");
        callMessage("UI|" + TREE + "|update");
        out("end");
    }
    /**
     * 下载树数据
     * @param parentNode TTreeNode
     * @param group String
     */
    public void downloadRootTree(TTreeNode parentNode,String group)
    {
        if(parentNode == null)
            return;
        TParm parm = getDicTool().getGroupList(group);
        for(int i = 0;i < parm.getCount("ID");i++)
        {
            String id = parm.getValue("ID",i);
            String name = parm.getValue("NAME",i);
            String type = parm.getValue("TYPE",i);
            String data = parm.getValue("DATA",i);
            TTreeNode node = new TTreeNode(name,type);
            node.setGroup(group);
            node.setID(id);
            node.setValue(group + ":" + id);
            node.setData(data);
            parentNode.add(node);
            downloadRootTree(node,id);
        }
    }
    public void onTreeClicked(Object parm)
    {
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        String type = node.getType();
        String group = node.getGroup();
        String id = node.getID();
        String data = (String) node.getData();
        groupID = group;
        parentID = id;
        //if("PATH".equalsIgnoreCase(type))

        TParm result = getDicTool().getListAll(parentID);
        callFunction("UI|" + TABLE + "|setParmValue",result);
        onClear();
    }
    /**
     * 得到字典工具类
     * @return DictionaryTool
     */
    public DictionaryTool getDicTool()
    {
        return DictionaryTool.getInstance();
    }
    /**
     * 删除
     */
    public void onDelete()
    {
        int row = (Integer)callFunction("UI|" + TABLE + "|getSelectedRow");
        if(row < 0)
            return;
        String id = getText("ID");
        if(!(Boolean)callFunction("UI|" + TABLE + "|onDelete"))
        {
            messageBox("E0003");
            return;
        }
        TTreeNode node = treeRoot.findNodeForValue(parentID + ":" + id);
        TTreeNode parentNode = treeRoot.findNodeForValue(groupID + ":" + parentID);
        if(node != null && parentNode != null)
        {
            parentNode.remove(node);
            callMessage("UI|" + TREE + "|update");
        }
        messageBox("P0003");
    }
    public void onSave() {
        TParm parm = new TParm();
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        if ("EDIT".equals(action)) {
            if (!emptyTextCheck("NAME"))
                return;
            callFunction("UI|" + TABLE + "|setModuleParmUpdate", parm);
            if (! (Boolean) callFunction("UI|" + TABLE + "|onUpdate")) {
                messageBox("E0001");
                return;
            }
            String id = getText("ID");
            String name = getText("NAME");
            String type = getText("TYPE");
            TTreeNode node = treeRoot.findNodeForValue(parentID + ":" + id);
            if (node != null) {
                node.setText(name);
                node.setType(type);
                callMessage("UI|" + TREE + "|update");
            }
            messageBox("P0001");
        }
        else {
            if (parentID.length() == 0)
                return;
            if (!emptyTextCheck("ID,NAME"))
                return;
            parm.setData("GROUP_ID", parentID);
            callFunction("UI|" + TABLE + "|setModuleParmInsert", parm);
            if (! (Boolean) callFunction("UI|" + TABLE + "|onInsert")) {
                messageBox("E0002");
                return;
            }
            String id = getText("ID");
            String name = getText("NAME");
            String type = getText("TYPE");
            TTreeNode node = treeRoot.findNodeForValue(groupID + ":" +
                parentID);
            if (node != null) {
                TTreeNode newNode = new TTreeNode(name, type);
                newNode.setGroup(parentID);
                newNode.setID(id);
                newNode.setValue(parentID + ":" + id);
                node.add(newNode);
                callMessage("UI|" + TREE + "|update");
            }
            messageBox("P0002");
            callFunction("UI|ID|setEnabled", false);
            callFunction("UI|delete|setEnabled",true);
            action = "EDIT";
        }
    }
    /**
     * 清空
     */
    public void onClear()
    {
        clearValue("ID;NAME;ENG_DESC;TYPE;STATE;DESCRIPTION;PY1;PY2;SEQ;PARENT_ID;DATA");
        callFunction("UI|" + TABLE + "|clearSelection");
        callFunction("UI|ID|setEnabled",true);
        callFunction("UI|save|setEnabled",true);
        callFunction("UI|delete|setEnabled",false);
        action = "INSERT";
    }
    public void onTableClicked(int row)
    {
        if(row < 0)
            return;
        callFunction("UI|ID|setEnabled",false);
        callFunction("UI|delete|setEnabled",true);
        action = "EDIT";
    }
}
