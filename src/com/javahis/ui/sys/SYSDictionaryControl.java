package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import jdo.sys.DictionaryTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTreeEvent;
import jdo.sys.Operator;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.ui.TPanel;

/**
 *
 * <p>Title: 公用字典控制类</p>
 *
 * <p>Description: 公用字典控制类</p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company: JavaHis</p>
 *
 * @author lzk 2008.8.29
 * @version 1.0
 */
public class SYSDictionaryControl
    extends TControl {
    private static final String TREE = "Tree";
    private TTreeNode treeRoot;
    private String editUITag;
    public void onInit() {
        super.onInit();
        //初始化树
        onInitTree();
        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
    }

    public void onTreeClicked(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        String type = node.getType();
        String group = node.getGroup();
        String id = node.getID();
        String data = (String) node.getData();
        if (data == null || data.length() == 0) {
            editUITag = type + "_UI";
            callFunction("UI|Panel|addItem", editUITag,
                         "%ROOT%\\config\\sys\\dictionary\\" + type + "Edit.x",
                         group + "|" + id);
        }
        else {
            String s1[] = StringTool.getHead(data, "|");
            editUITag = s1[0] + "_UI";
            callFunction("UI|Panel|addItem", editUITag, s1[0], s1[1]);
        }
        callFunction("UI|" + editUITag + "|addEventListener", "UPDATE_ACTION", this,
                     "onUpdateAction");
        callFunction("UI|" + editUITag + "|addEventListener", "INSERT_ACTION", this,
                     "onInsertAction");
        callFunction("UI|" + editUITag + "|addEventListener", "DELETE_ACTION", this,
                     "onDeleteAction");
    }

    /**
     * 修改动作
     * @param groupID String
     * @param parentID String
     * @param id String
     * @param name String
     * @param type String
     */
    public void onUpdateAction(String groupID, String parentID, String id,
                               String name, String type) {
        TTreeNode node = treeRoot.findNodeForValue(parentID + ":" + id);
        if (node != null) {
            node.setText(name);
            node.setType(type);
            callMessage("UI|" + TREE + "|update");
        }
    }

    /**
     * 新增动作
     * @param groupID String
     * @param parentID String
     * @param id String
     * @param name String
     * @param type String
     */
    public void onInsertAction(String groupID, String parentID, String id,
                               String name, String type) {
        TTreeNode node = treeRoot.findNodeForValue(groupID + ":" +
            parentID);
        if (node != null && "PATH".equals(node.getType())) {
            TTreeNode newNode = new TTreeNode(name, type);
            newNode.setGroup(parentID);
            newNode.setID(id);
            newNode.setValue(parentID + ":" + id);
            node.add(newNode);
            callMessage("UI|" + TREE + "|update");
        }
    }

    /**
     * 删除动作
     * @param groupID String
     * @param parentID String
     * @param id String
     */
    public void onDeleteAction(String groupID, String parentID, String id) {
        TTreeNode node = treeRoot.findNodeForValue(parentID + ":" + id);
        TTreeNode parentNode = treeRoot.findNodeForValue(groupID + ":" +
            parentID);
        if (node != null && parentNode != null) {
            parentNode.remove(node);
            callMessage("UI|" + TREE + "|update");
        }
    }

    /**
     * 初始化树
     */
    public void onInitTree() {
        out("begin");
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        treeRoot.setText(getDicTool().getName("ROOT", "GROUP"));
        treeRoot.setGroup("ROOT");
        treeRoot.setID("GROUP");
        treeRoot.setType("PATH");
        treeRoot.setValue("ROOT:GROUP");
        treeRoot.removeAllChildren();
        downloadRootTree(treeRoot, "GROUP");
        callMessage("UI|" + TREE + "|update");
        out("end");
    }

    /**
     * 下载树数据
     * @param parentNode TTreeNode
     * @param group String
     */
    public void downloadRootTree(TTreeNode parentNode, String group) {
        if (parentNode == null)
            return;
        TParm parm = getDicTool().getGroupList(group);
        for (int i = 0; i < parm.getCount("ID"); i++) {
            String id = parm.getValue("ID", i);
            String name = parm.getValue("NAME", i);
            String type = parm.getValue("TYPE", i);
            String data = parm.getValue("DATA", i);
            TTreeNode node = new TTreeNode(name, type);
            node.setGroup(group);
            node.setID(id);
            node.setValue(group + ":" + id);
            node.setData(data);
            parentNode.add(node);
            if ("PATH".equals(type))
                downloadRootTree(node, id);
        }
    }

    /**
     * 得到字典工具类
     * @return DictionaryTool
     */
    public DictionaryTool getDicTool() {
        return DictionaryTool.getInstance();
    }

    public void onSave() {

    }

    public void onClear() {
    }

    public void onDelete() {
    }

    public void onShowWindowsEvent() {
        //显示UIshowTopMenu
        callFunction("UI|" + editUITag + "|showTopMenu");
    }

}
