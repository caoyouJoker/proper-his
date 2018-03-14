package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import jdo.sys.DictionaryTool;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTable;
import jdo.sys.Operator;
import com.dongyang.ui.TTextField;
import com.dongyang.util.TMessage;
import jdo.sys.SYSDictionaryTool;

/**
 * <p>
 * Title:系统结构管理
 * </p>
 *
 * <p>
 * Description:系统结构管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.04
 * @version 1.0
 */
public class SYSCatalogControl
    extends TControl {

    /**
     * 树根
     */
    private TTreeNode treeRoot;
    /**
     * 上级编号
     */
    private String parentID;
    /**
     * 上级组编号
     */
    private String groupID;

    private TTable table;

    private String action;

    public SYSCatalogControl() {
    }

    /**
     * 初始化方法
     */
    public void onInit() {
        //初始化树
        onInitTree();
        //单击选中树项目
        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
        table = this.getTable("TABLE");
        onClear();
    }

    public void onSave() {
        TParm parm = new TParm();
        TParm result = new TParm();
        parm.setData("GROUP_ID", parentID);
        parm.setData("ID", this.getValueString("ID"));
        parm.setData("NAME", this.getValueString("NAME"));
        parm.setData("ENG_DESC", this.getValueString("ENG"));
        parm.setData("PY1", this.getValueString("PY1"));
        parm.setData("PY2", this.getValueString("PY2"));
        parm.setData("SEQ", this.getValue("SEQ"));
        parm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
        parm.setData("TYPE", "PATH");
        parm.setData("OPT_USER", Operator.getID());
        parm.setData("OPT_TERM", Operator.getIP());
        parm.setData("PARENT_ID", "");
        parm.setData("STATE", "");
        parm.setData("DATA", "");

        if ("EDIT".equals(action)) {
            if ("".equals(this.getValueString("NAME"))) {
                this.messageBox("目录名称不能为空");
                return;
            }
            result = SYSDictionaryTool.getInstance().update(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0001");
                return;
            }
            String id = getText("ID");
            String name = getText("NAME");
            String type = getText("TYPE");
            TTreeNode node = treeRoot.findNodeForValue(parentID + ":" + id);
            if (node != null) {
                node.setText(name);
                node.setType(type);
                callMessage("UI|TREE|update");
            }
            messageBox("P0001");
            this.onClear();
        }
        else {
            if (parentID.length() == 0) {
                this.messageBox("请选择树节点");
                return;
            }
            if ("".equals(this.getValueString("ID"))) {
                this.messageBox("目录编号不能为空");
                return;
            }
            if ("".equals(this.getValueString("NAME"))) {
                this.messageBox("目录名称不能为空");
                return;
            }
            result = SYSDictionaryTool.getInstance().select(parm);
            if (result.getCount() > 0) {
                this.messageBox("目录已存在");
                return;
            }
            result = SYSDictionaryTool.getInstance().insert(parm);
            if (result.getErrCode() < 0) {
                this.messageBox("E0002");
                return;
            }
            String id = this.getValueString("ID");
            String name = getValueString("NAME");
            String type = "PATH";
            TTreeNode node = treeRoot.findNodeForValue(parentID + ":" + id);
            if (node != null) {
                TTreeNode newNode = new TTreeNode(name, type);
                newNode.setGroup(parentID);
                newNode.setID(id);
                newNode.setValue(parentID + ":" + id);
                node.add(newNode);
                callMessage("UI|TREE|update");
            }
            messageBox("P0002");
            callFunction("UI|ID|setEnabled", false);
            callFunction("UI|delete|setEnabled", true);
            action = "EDIT";
            onInitTree();
        }
    }

    /**
     * 清空
     */
    public void onClear() {
        clearValue("ID;NAME;ENG;DESCRIPTION;PY1;PY2;SEQ");
        table.setSelectionMode(0);
        table.clearSelection();
        table.removeRowAll();
        callFunction("UI|ID|setEnabled", true);
        callFunction("UI|save|setEnabled", true);
        callFunction("UI|delete|setEnabled", false);
        action = "INSERT";
    }

    /**
     * 删除
     */
    public void onDelete() {
        int row = table.getSelectedRow();
        if (row < 0)
            return;
        if (this.messageBox("提示", "确定是否删除", 2) == 0) {
            TParm parm = new TParm();
            TParm pathParm = table.getParmValue().getRow(row);
            parm.setData("PATH_PARM", pathParm.getData());
            TParm inparm = new TParm();
            inparm.setData("GROUP_ID", table.getItemString(row, "ID"));
            inparm.setData("ID", "%");
            TParm result = SYSDictionaryTool.getInstance().select(inparm);
            if (result.getCount() > 0) {
                this.messageBox("目录下存在子节点，不可删除");
                return;
            }
            else {
                inparm.setData("GROUP_ID",
                               table.getParmValue().getValue("GROUP_ID", row));
                inparm.setData("ID", table.getParmValue().getValue("ID", row));
                //System.out.println("inparm" + inparm);
                result = SYSDictionaryTool.getInstance().delete(inparm);
                if (result.getErrCode() < 0) {
                    this.messageBox("删除失败");
                    return;
                }
                this.messageBox("删除成功");
                onInitTree();
                onClear();
            }
        }
    }

    /**
     *
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        callFunction("UI|delete|setEnabled", false);
        callFunction("UI|ID|setEnabled", true);
        clearValue("ID;NAME;ENG;DESCRIPTION;PY1;PY2;SEQ");
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        String group = node.getGroup();
        String id = node.getID();
        groupID = group;
        parentID = id;
        TParm result = getDicTool().getListAll(parentID);
        table.setParmValue(result);
        this.setValue("SEQ", table.getRowCount() + 1);
    }

    /**
     *
     */
    public void onTableClicked() {
        int row = table.getSelectedRow();
        //System.out.println(table.getParmValue());
        if ("PATH".equals(table.getParmValue().getValue("TYPE", row))) {
            this.setValue("ID", table.getParmValue().getValue("ID", row));
            this.setValue("NAME", table.getParmValue().getValue("NAME", row));
            this.setValue("ENG_DESC", table.getParmValue().getValue("ENG", row));
            this.setValue("DESCRIPTION",
                          table.getParmValue().getValue("DESCRIPTION", row));
            this.setValue("PY1", table.getParmValue().getValue("PY1", row));
            this.setValue("PY2", table.getParmValue().getValue("PY2", row));
            this.setValue("SEQ", table.getParmValue().getData("SEQ", row));
            callFunction("UI|delete|setEnabled", true);
            callFunction("UI|ID|setEnabled", false);
            action = "EDIT";
        }
        else {
            clearValue("ID;NAME;ENG;DESCRIPTION;PY1;PY2;SEQ");
            callFunction("UI|delete|setEnabled", false);
            callFunction("UI|ID|setEnabled", true);
            action = "INSERT";
        }
    }

    /**
     * 目录名称回车事件
     */
    public void onNameAction() {
        String py = TMessage.getPy(this.getValueString("NAME"));
        setValue("PY1", py);
        getTextField("PY1").grabFocus();
    }


    /**
     * 初始化树
     */
    public void onInitTree() {
        //得到树根
        treeRoot = getTree("TREE").getRoot();
        if (treeRoot == null)
            return;
        treeRoot.setText("系统结构");
        treeRoot.setGroup("POPEDOM");
        treeRoot.setID("SYS_SUBSYSTEM");
        treeRoot.setType("PATH");
        treeRoot.setValue("POPEDOM:SYS_SUBSYSTEM");
        treeRoot.removeAllChildren();
        downloadRootTree(treeRoot, "SYS_SUBSYSTEM");
        //调用树点初始化方法
        getTree("TREE").update();
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

    /**
     *
     * @param tag String
     * @return TTree
     */
    private TTree getTree(String tag) {
        return (TTree)this.getComponent(tag);
    }

    /**
     * 得到Table对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }

    /**
     * 得到TextField对象
     *
     * @param tagName
     *            元素TAG名称
     * @return
     */
    private TTextField getTextField(String tagName) {
        return (TTextField) getComponent(tagName);
    }

}
