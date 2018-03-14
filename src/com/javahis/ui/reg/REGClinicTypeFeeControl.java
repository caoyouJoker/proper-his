package com.javahis.ui.reg;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTreeEvent;
import jdo.reg.PanelTypeTool;
import jdo.reg.PanelTypeFeeTool;
import com.dongyang.ui.TTree;
/**
 *
 * <p>Title:号别费用总控制类 </p>
 *
 * <p>Description:号别费用总控制类 </p>
 *
 * <p>Copyright: Copyright (c) Liu dongyang 2008</p>
 *
 * <p>Company:Javahis </p>
 *
 * @author wangl 2008.09.16
 * @version 1.0
 */
public class REGClinicTypeFeeControl
    extends TControl {
    public static String TREE = "Tree";
    TTreeNode treeRoot;
    TParm treedata;
    //TParm TABLETYPEFEEDATA = new TParm();
    TParm TABLETYPEDATA = new TParm();
    public void onInit() {
        super.onInit();
        //初始化树
        onInitTree();
        //单击选中树项目
        addEventListener(TREE + "->" + TTreeEvent.CLICKED, "onTreeClicked");
        callFunction("UI|TypeFeePanel|addItem", "REGPanelType",
             "%ROOT%\\config\\reg\\REGPanelType.x");

//        //置保存和删除键为空
//        callFunction("UI|save|setEnabled", false);
//        callFunction("UI|delete|setEnabled", false);

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
        treeRoot.setText("号别分类");
        treeRoot.setID("ROOT");
        treeRoot.setType("ROOT");
        treeRoot.removeAllChildren();
        initClinicType(treeRoot);
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
//        callFunction("UI|DIAPANEL|addItem", node.getName(),"%ROOT%\\config\\opd\\"+node.getName());
            callFunction("UI|TypeFeePanel|addItem", "REGPanelType",
                         "%ROOT%\\config\\reg\\REGPanelType.x");
            return;
        }
        if ("CLINICTYPE".equals(node.getType())) {
            String id = node.getID();
            String admType = node.getName();
            callFunction("UI|TypeFeePanel|addItem", "REGPanelTypeFee",
                         "%ROOT%\\config\\reg\\REGPanelTypeFee.x",new String[]{id,admType});
            //typeNodeClick(id);
           return;
        }
        //if ("CLINICTYPEFEE".equals(node.getType())) {
            //selecttypefeefortable(node.getID());
        //    return;
        //}

    }

    /**
     * 查询当前号别，给号别费用table赋值（Tree）
     * @param clinicTypeCode String
     */
    public void typeNodeClick(String clinicTypeCode) {
        //显示当前号别 table 数据
        TParm TABLETYPEFEEDATA = PanelTypeFeeTool.getInstance().selectdata(clinicTypeCode);
        if (TABLETYPEFEEDATA.getErrCode() < 0) {
            messageBox(TABLETYPEFEEDATA.getErrText());
            return;
        }
        this.callFunction("UI|TABLETYPEFEE|setParmValue", TABLETYPEFEEDATA);

    }
//    /**
//     * 查询当前号别，给号别费用table赋值（Table）
//     * @param clinicTypeCode String
//     */
//    public void selecttypefeefortable(String clinicTypeCode) {
//        //显示当前号别 table 数据
//        TParm TABLETYPEFEEDATA = PanelTypeFeeTool.getInstance().queryTree(clinicTypeCode);
//        if (TABLETYPEFEEDATA.getErrCode() < 0) {
//            messageBox(TABLETYPEFEEDATA.getErrText());
//            return;
//        }
//        if (TABLETYPEFEEDATA.getCount() == 0) {
//            //显示提示信息“查无资料”
//            this.messageBox("E0008");
//            return;
//        }
//        setTextForParm(
//            "ADM_TYPE;CLINICTYPE_CODE;ORDER_CODE;RECEIPT_TYPE",
//            TABLETYPEFEEDATA, 0);
//
//
//    }

    /**
     * 装载号别
     * @param parentNode TTreeNode
     */
    public void initClinicType(TTreeNode parentNode) {
        TABLETYPEDATA = PanelTypeTool.getInstance().queryTree();
        if (TABLETYPEDATA == null)
            return;
        for (int i = 0; i < TABLETYPEDATA.getCount(); i++) {
            String id = TABLETYPEDATA.getValue("CLINICTYPE_CODE", i);
            String name = TABLETYPEDATA.getValue("CLINICTYPE_DESC", i);
            String admType = TABLETYPEDATA.getValue("ADM_TYPE", i);
            String type = "CLINICTYPE";
            TTreeNode node = new TTreeNode(name, type);
            node.setName(admType);
            node.setID(id);
            parentNode.add(node);
            //initClinicTypeFee(node, id);
        }
    }
    /**
     * 装载号别费用
     * @param parentNode TTreeNode
     * @param clinicTypeCode String
     */
    public void initClinicTypeFee(TTreeNode parentNode, String clinicTypeCode) {
        TParm parm = PanelTypeFeeTool.getInstance().queryTree(clinicTypeCode);

        if (parm == null) {
            this.messageBox("non");
            return;
        }

        for (int i = 0; i < parm.getCount(); i++) {
            String id = parm.getValue("CLINICTYPE_CODE", i);
            String name = parm.getValue("ORDER_CODE", i);
            String type = "CLINICTYPEFEE";
            TTreeNode node = new TTreeNode(name, type);
            node.setID(id);
            parentNode.add(node);
        }

    }
    /**
     * 同部树更新(新增)
     * @param id String
     * @param name String
     */
    public void addNode(String id,String name) {
        //拿到树对象
        TTree tree = (TTree) callFunction("UI|TREE|getThis");
        //找到根节点
        TTreeNode root = tree.getRoot();
        //如果结点为空
        if (root == null)
            return;
        //树上新种节点
        String type = "CLINICTYPE";
        //新建一个结点
        TTreeNode nodeNew = new TTreeNode(name, type);
        //给结点赋名称
        nodeNew.setText(name);
        //赋id
        nodeNew.setID(id);
        //添加到root上
        root.add(nodeNew);
        //刷新树
        tree.update();

    }
    /**
     * 同步树更新(删除)
     * @param id String
     */
    public void deleteNode(String id){
        //拿到树对象
        TTree tree = (TTree) callFunction("UI|TREE|getThis");
        //找到根节点
        TTreeNode root = tree.getRoot();
        //删除树上的节点
        TTreeNode treenodeChild = root.findNodeForID(id);
        if(treenodeChild != null)
            root.remove(treenodeChild);
        //刷新树
        tree.update();
    }
}
