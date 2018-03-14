package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TTreeEvent;
import jdo.reg.PanelAreaTool;
import jdo.reg.PanelRoomTool;

/**
 *
 * <p>Title:诊室维护总控制类 </p>
 *
 * <p>Description:诊室维护总控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.08.28
 * @version 1.0
 */
public class REGClinicRoomControl
    extends TControl {
    public static String TREE = "Tree";
    TTreeNode treeRoot;
    TParm treedata;
    //TParm TABLEROOMDATA = new TParm();
    TParm TABLEAREADATA = new TParm();
    public void onInit() {
        super.onInit();
        //初始化树
        onInitTree();
        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
        callFunction("UI|Panel|addItem", "REGPanelArea",
                     "%ROOT%\\config\\reg\\REGPanelArea.x");

        //置保存和删除键为空
        callFunction("UI|save|setEnabled", false);
        callFunction("UI|delete|setEnabled", false);

    }
    /**
     * 点击树
     * @param parm Object
     */
    public void onTreeClicked(Object parm) {
        callFunction("UI|save|setEnabled", true);

        TTreeNode node = (TTreeNode) parm;
        if (node == null)
            return;
        if ("ROOT".equals(node.getType())) {
            onInitTree();
            callFunction("UI|Panel|addItem", "REGPanelArea",
                         "%ROOT%\\config\\reg\\REGPanelArea.x");
            return;
        }
        if ("AREA".equals(node.getType())) {
            String id = node.getID();

            callFunction("UI|Panel|addItem", "REGPanelRoom",
                         "%ROOT%\\config\\reg\\REGPanelRoom.x");
            typeNodeClick(id);
           return;
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
        //----------------------//
        treeRoot.setText("诊区");
        treeRoot.setID("ROOT");
        treeRoot.setType("ROOT");
        treeRoot.removeAllChildren();
        initArea(treeRoot);
        //----------------------//
        callMessage("UI|" + TREE + "|update");
        out("end");
    }

    /**
     * 查询当前诊区，给诊室table赋值（Tree）
     * @param clinicAreaCode String
     */
    public void typeNodeClick(String clinicAreaCode) {
        //显示当前诊区 table 数据
        TParm parm = new TParm();
        parm.setData("CLINICAREA_CODE",clinicAreaCode);
        TParm TABLEAREADATA = PanelRoomTool.getInstance().selectdata(parm);
        if (TABLEAREADATA.getErrCode() < 0) {
            messageBox(TABLEAREADATA.getErrText());
            return;
        }
        this.callFunction("UI|TABLEROOM|setParmValue", TABLEAREADATA);

    }

    /**
     * 装载诊区
     * @param parentNode TTreeNode
     */
    public void initArea(TTreeNode parentNode) {
        TABLEAREADATA = PanelAreaTool.getInstance().queryTree();
        if (TABLEAREADATA == null)
            return;
        for (int i = 0; i < TABLEAREADATA.getCount(); i++) {
            String id = TABLEAREADATA.getValue("CLINICAREA_CODE", i);
            String name = TABLEAREADATA.getValue("CLINIC_DESC", i);
            String type = "AREA";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }
    }

    /**
     * 装载诊室
     * @param parentNode TTreeNode
     * @param clinicRoomNo String
     */
    public void initRoom(TTreeNode parentNode,String clinicRoomNo) {
        TParm parm = PanelRoomTool.getInstance().queryTree(clinicRoomNo);

        if (parm == null) {
            this.messageBox("non");
            return;
        }

        for (int i = 0; i < parm.getCount(); i++) {
            String id = parm.getValue("CLINICROOM_NO", i);
            String name = parm.getValue("CLINICROOM_DESC", i);
            String type = "ROOM";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }

    }
}
