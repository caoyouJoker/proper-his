package com.javahis.ui.emr;


import java.awt.event.KeyEvent;
import javax.swing.tree.TreeNode;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TKeyListener;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.javahis.util.StringUtil;
import jdo.emr.EMRClassTool;

/**
 * 
 * <p>
 * Title: EMR_CLASS 下拉选择框
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wanglong 2014.8.15
 * @version 1.0
 */
public class EMRClassPopupControl
        extends TControl {

    private TTree tree;
    private TTreeNode treeRoot;
    private String oldText = "";
    private boolean letterKeyFlg=true;//字母按键标记
    
    // 健检所用医嘱类型，应全为集合医嘱，但是LIS/RIS/OTH等不计
    private String hrmType;

    /**
     * 初始化
     */
    public void onInit() {
        super.onInit();
        tree = (TTree) callMessage("UI|TREE|getThis");
        callFunction("UI|EDIT|addEventListener", "EDIT->" + TKeyListener.KEY_PRESSED, this,
                     "onKeyPressed");
        callFunction("UI|EDIT|addEventListener", TTextFieldEvent.KEY_RELEASED, this,
                     "onKeyReleased");
//        addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
//        // 单击选中树项目
        addEventListener("TREE->" + TTreeEvent.DOUBLE_CLICKED, "onTreeDoubleClicked");
        Object obj = getParameter();
        if (obj == null) return;
        if (!(obj instanceof TParm)) return;
        TParm parm = (TParm) obj;
        System.out.println("-------------入参------parm-------" + parm);
        String text = parm.getValue("TEXT");
        setEditText(text);
        hrmType = parm.getValue("HRM_TYPE");
        onInitTree();
    }

    /**
     * 重新加载
     */
    public void onInitReset() {
        Object obj = getParameter();
        if (obj == null) return;
        if (!(obj instanceof TParm)) return;
        TParm parm = (TParm) obj;
        String text = parm.getValue("TEXT");
        String oldText = (String) callFunction("UI|EDIT|getText");
        if (oldText.equals(text)) {
            return;
        }
        setEditText(text);
        onInitTree();
    }

    /**
     * 初始化树
     */
    public void onInitTree() {
        treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");// 得到树根
        if (treeRoot == null) return;
        // 取根节点;
        TParm rootParm = this.getTreeRoot();
        treeRoot.setText(rootParm.getValue("CLASS_DESC", 0));// 给根节点添加文字显示
        treeRoot.setType("Root");// 给根节点赋tag
        treeRoot.setID(rootParm.getValue("CLASS_CODE", 0));
        treeRoot.removeAllChildren();// 清空所有节点的内容
        // 开始初始化树的数据
        TParm result = this.getTreeChildren("");
        int count = result.getCount();
        for (int i = 0; i < count; i++) {
            TParm dataParm = result.getRow(i);
            putNodeInTree(dataParm, treeRoot);
        }
        tree.update();// 更新树
        tree.setSelectNode(treeRoot);// 设置树的默认选中节点
    }

    /**
     * 将子节点加入到树中
     * 
     * @param childParm
     *            TParm
     * @param root
     *            TTreeNode
     */
    private void putNodeInTree(TParm childParm, TTreeNode root) {
        String noteType = "Path"; // UI
        if (childParm.getValue("LEAF_FLG").equals("Y")) {
            noteType = "Leaf"; // UI
        }
        TTreeNode node = new TTreeNode("EMRCLASS", noteType);
        node.setText(childParm.getValue("CLASS_DESC"));
        node.setID(childParm.getValue("CLASS_CODE"));
        String parentID = childParm.getValue("PARENT_CLASS_CODE");
        // System.out.println("parentID-------------:" + parentID);
        if (root.findNodeForID(childParm.getValue("CLASS_CODE")) != null) {
            // System.out.println("已经含有此节点不用执行添加操作");
        } else if (root.findNodeForID(parentID) != null) {
            root.findNodeForID(parentID).add(node);
        } else {
            TParm parentIDParm = new TParm();
            parentIDParm.setData("CLASS_CODE", parentID);
            TParm parentParm = EMRClassTool.getNewInstance().onQuery(parentIDParm);
            // System.out.println("查询出的父节点:" + resultParm);
            if (parentParm.getCount() <= 0) {
                root.add(node);
            } else {
                putNodeInTree(parentParm.getRow(0), root);
                root.findNodeForID(parentID).add(node);
            }
        }
    }

    /**
     * 树单击事件
     * 
     * @param parm
     */
    public void onTreeClicked(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        letterKeyFlg = false;
        if (node == null) return;
        if (!node.isNodeChild(node)) {
            for (int i = 0; i < node.getChildCount(); i++) {
                TreeNode childNode = node.getChildAt(i);
                if (childNode.isLeaf() && childNode instanceof TTreeNode) {
                    TTreeNode tChildNode = (TTreeNode) childNode;
                    tChildNode.setValue(node.getValue());
                }
            }
        }
    }

    /**
     * 单击选中树事件
     * 
     * @param obj
     *            Object
     */
    public void onTreeDoubleClicked(Object obj) {
        if (obj == null) {
            return;
        }
        TParm parm = new TParm();
        parm.setData("KEYINPUT_FLG", false);
        parm.setData("CLASS_CODE", tree.getSelectNode().getID());
        setReturnValue(parm);
        callFunction("UI|setVisible", false);
    }

    /**
     * 设置输入文字
     * 
     * @param s
     *            String
     */
    public void setEditText(String s) {
        callFunction("UI|EDIT|setText", s);
        int x = s.length();
        callFunction("UI|EDIT|select", x, x);
        onKeyReleased(s);
    }

    /**
     * 按键释放事件
     * 
     * @param s
     *            String
     */
    public void onKeyReleased(String s) {
        s = s.toUpperCase();
        if (oldText.equals(s)) return;
        oldText = s;
        treeRoot.removeAllChildren();// 清空所有节点的内容
        TParm childrenParm = this.getTreeChildren(s);
        for (int i = 0; i < childrenParm.getCount(); i++) {
            TParm childParm = childrenParm.getRow(i);
            putNodeInTree(childParm, treeRoot);
        }
        tree.update();// 更新树

    }

    /**
     * 按键事件
     * 
     * @param e
     *            KeyEvent
     */
    public void onKeyPressed(KeyEvent e) {
        TTreeNode node = tree.getSelectNode();
        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                this.callFunction("UI|setVisible", false);
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_UP:
                tree.getTree().setSelectionRow(tree.getTree().getRowForNode(node) - 1);
                tree.getTree().scrollRowToVisible(tree.getTree().getRowForNode(node) - 1);
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_DOWN:
                tree.getTree().setSelectionRow(tree.getTree().getRowForNode(node) + 1);
                tree.getTree().scrollRowToVisible(tree.getTree().getRowForNode(node) + 1);
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_LEFT:
                tree.getTree().collapseRow(tree.getTree().getRowForNode(node));
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_RIGHT:
                tree.getTree().expandRow(tree.getTree().getRowForNode(node));
                letterKeyFlg = false;
                break;
            case KeyEvent.VK_ENTER:
                callFunction("UI|setVisible", false);
                TParm parm = new TParm();
                if (letterKeyFlg) {
                    parm.setData("KEYINPUT_FLG", true);//键盘键入注记，false表示按上下选择出来的CLASS_CODE
                    parm.setData("CLASS_CODE", this.getValueString("EDIT").trim());
                } else {
                    parm.setData("KEYINPUT_FLG", false);
                    parm.setData("CLASS_CODE", tree.getSelectNode().getID());
                }
                setReturnValue(parm);
                break;
            default:
                letterKeyFlg = true;
        }
    }


    /**
     * 当前输入名称对应的节点
     * 
     * @return TParm
     */
    public TParm getTreeChildren(String letters) {
        String sql =
                "SELECT CLASS_CODE, CLASS_DESC, ENG_DESC, PY1, PY2, DESCRIPTION, "
                        + "SEQ, CLASS_STYLE, LEAF_FLG, PARENT_CLASS_CODE "
                        // + " FROM EMR_CLASS WHERE LEAF_FLG='N' ";
                        + " FROM EMR_CLASS WHERE 1=1  #  ORDER BY CLASS_CODE,PARENT_CLASS_CODE,SEQ";
        String classSql="";
        if (!StringUtil.isNullString(letters)) {
            letters = letters.toUpperCase();
            classSql =
                    sql.replaceFirst("#",
                                     "AND (CLASS_CODE LIKE '@%' OR CLASS_DESC LIKE '@%' OR ENG_DESC LIKE '@%' OR PY1 LIKE '@%' OR PY2 LIKE '@%')");
            classSql = classSql.replaceAll("@", letters);
        } else {
            classSql = sql.replaceFirst("#", "");
        }
//        System.out.println("===================" + classSql);
        TParm result = new TParm(TJDODBTool.getInstance().select(classSql));
        if(result.getCount()<=0){
            result = new TParm(TJDODBTool.getInstance().select(sql.replaceFirst("#", "")));
        }
         
        return result;
    }

    /**
     * 根节点
     * 
     * @return TParm
     */
    public TParm getTreeRoot() {
        String sql =
                "SELECT CLASS_CODE,CLASS_DESC,CLASS_STYLE,LEAF_FLG,PARENT_CLASS_CODE,SEQ FROM EMR_CLASS WHERE PARENT_CLASS_CODE IS NULL";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
}
