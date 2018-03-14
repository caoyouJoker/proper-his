package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TTreeNode;
import jdo.reg.PanelGroupTool;
import jdo.reg.REGQueMethodTool;
/**
 *
 * <p>Title:给号组别总控制类 </p>
 *
 * <p>Description:给号组别总控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.17
 * @version 1.0
 */
public class REGQueGroupContrl extends TControl{
    public static String TREE = "Tree";
    TTreeNode treeRoot;
    TParm treedata;
    //TParm TABLEMETHODDATA = new TParm();
    TParm TABLEGROUPDATA = new TParm();
    public void onInit() {
        super.onInit();
        //初始化树
        onInitTree();
        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
        callFunction("UI|Panel|addItem", "REGPanelGroup",
                     "%ROOT%\\config\\reg\\REGPanelGroup.x");
    }

    /**
     * 初始化树
     */
    public void onInitTree() {
        out("begin");
        treeRoot = (TTreeNode) callMessage("UI|" + TREE + "|getRoot");
        if (treeRoot == null)
            return;
        //----------------------//
        treeRoot.setText("给号组别");
        treeRoot.setID("ROOT");
        treeRoot.setType("ROOT");
        treeRoot.removeAllChildren();
        initQueGroup(treeRoot);
        //----------------------//
        callMessage("UI|" + TREE + "|update");
        out("end");
    }

    /**
     * 点击树
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if ("ROOT".equals(node.getType())) {
            onInitTree();
            callFunction("UI|Panel|addItem", "REGPanelGroup",
                         "%ROOT%\\config\\reg\\REGPanelGroup.x");
            return;
        }
        if ("QUEGROUP".equals(node.getType())) {
            String id = node.getID();
            callFunction("UI|Panel|addItem", "REGQueMethod",
                         "%ROOT%\\config\\reg\\REGQueMethod.x");
            //===========pangben modify 20110410 start
            callFunction("UI|QUEGROUP_CODE|setValue",id);
             //===========pangben modify 20110410 stop
            typeNodeClick(id);
           return;
        }

    }

    /**
     * 查询当前给号组别，给给号方式table赋值（Tree）
     * @param quegroupCode String
     */
    public void typeNodeClick(String quegroupCode) {
        //显示当前给号组别 table 数据
        TParm parm = new TParm();
        parm.setData("QUEGROUP_CODE",quegroupCode);
        TParm TABLEMETHODDATA = REGQueMethodTool.getInstance().selectdata(parm);
        if (TABLEMETHODDATA.getErrCode() < 0) {
            messageBox(TABLEMETHODDATA.getErrText());
            return;
        }
        this.callFunction("UI|TABLEMETHOD|setParmValue", TABLEMETHODDATA);

    }

    /**
     * 装载给号组别
     * @param parentNode TTreeNode
     */
    public void initQueGroup(TTreeNode parentNode) {
        TABLEGROUPDATA = PanelGroupTool.getInstance().queryTree();
        if (TABLEGROUPDATA == null)
            return;
        for (int i = 0; i < TABLEGROUPDATA.getCount(); i++) {
            String id = TABLEGROUPDATA.getValue("QUEGROUP_CODE", i);
            String name = TABLEGROUPDATA.getValue("QUEGROUP_DESC", i);
            String type = "QUEGROUP";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }
    }
    /**
     * 装载给号方式
     * @param parentNode TTreeNode
     * @param quegroupCode String
     * @param queNo String
     */
    public void initQueMethod(TTreeNode parentNode, String quegroupCode, String queNo) {
        TParm parm = REGQueMethodTool.getInstance().queryTree(quegroupCode,queNo);

        if (parm == null) {
            this.messageBox("non");
            return;
        }

        for (int i = 0; i < parm.getCount(); i++) {
            String id = parm.getValue("QUEGROUP_CODE", i);
            String name = parm.getValue("QUE_NO", i);
            String type = "QUEMETHOD";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }

    }
}
