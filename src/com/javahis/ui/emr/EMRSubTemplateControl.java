package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWindow;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.ImageTool;
import com.sun.awt.AWTUtilities;

/**
 * <p>Title:子模板弹窗 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright:Copyright (c) 2014 </p>
 *
 * <p>Company:Bluecore </p>
 *
 * @author whaosoft
 * @version 1.0
 */
public class EMRSubTemplateControl extends TControl {


    private TTree tree;
    private TTreeNode treeNode;
    private TWord word;

    /**
	 * 初始化
	 */
    public void onInit() {
        super.onInit();
        TParm inParm = this.getInputParm();
        tree = (TTree) this.getComponent("TREE");
        treeNode = tree.getRoot();
        word = (TWord) inParm.getData("TWORD");
        // 单击选中树项目
        addEventListener(tree.getTag() + "->" + TTreeEvent.DOUBLE_CLICKED, "onTreeDoubled");
        this.loadSubTemplate(inParm);
    }

    /**
     * 
     * @param inParm
     */
    private void loadSubTemplate(TParm inParm) {
        TTreeNode root = tree.getRoot();
        root.removeAllChildren();
        TParm rootParam = this.getSubTemplateRootNode();
        treeNode.setText(rootParam.getValue("PHRASE_CODE", 0));
        treeNode.setType("Root");
        treeNode.setID(rootParam.getValue("CLASS_CODE", 0));
        TParm nodesParam = this.getSubTemplateChildrenNodes();
        if (nodesParam == null) return;
        int nodesCount = nodesParam.getCount();
        for (int i = 0; i < nodesCount; i++) {
            TParm temp = nodesParam.getRow(i);
            // 根据节点类型设置是否为目录节点
            String noteType = "1";
            // 叶节点(是结构化片语)
            if (nodesParam.getValue("LEAF_FLG", i).equals("Y")) {
                noteType = "4";
            }
            // 建立新节点
            TTreeNode PhraseClass = new TTreeNode("PHRASECLASS" + i, noteType);
            // 将ERM分类信息设置到节点当中
            PhraseClass.setText(nodesParam.getValue("PHRASE_CODE", i));
            PhraseClass.setID(nodesParam.getValue("CLASS_CODE", i));
            // 设置所有类型
            PhraseClass.setData(temp);
            // 第一级的节点放入根结点
            if (nodesParam.getValue("PARENT_CLASS_CODE", i).equals("ROOT")) {
                treeNode.addSeq(PhraseClass);
            }
            // 其他级别的节点放入相应的父节点下面
            else {
                if (nodesParam.getValue("PARENT_CLASS_CODE", i).length() != 0) {
                    // 假如叶节点（片语节点）,是当前科室的加入；
                    // 是叶节点
                    if (nodesParam.getValue("LEAF_FLG", i).equals("Y")) {
                        // 是主片语加入节点，补充的片语在此不加入
                        if (nodesParam.getValue("MAIN_FLG", i).equals("Y")) {
                            treeNode.findNodeForID(nodesParam.getValue("PARENT_CLASS_CODE", i))
                                    .add(PhraseClass);
                        }
                        //
                    } else {
                        treeNode.findNodeForID(nodesParam.getValue("PARENT_CLASS_CODE", i))
                                .add(PhraseClass);
                    }
                }
            }
        }
        tree.update();
    }

    /**
     * 得到子模板根节点数据
     * @return
     */
    private TParm getSubTemplateRootNode() {
        String sql =
                "SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME "
                        + " FROM OPD_COMTEMPLATE_PHRASE WHERE PARENT_CLASS_CODE IS NULL";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 得到子模板子节点数据
     * @return
     */
    private TParm getSubTemplateChildrenNodes() {
        String sql =
                "SELECT CLASS_CODE,PHRASE_CODE,LEAF_FLG,MAIN_FLG,PARENT_CLASS_CODE,FILE_PATH,FILE_NAME "
                        + " FROM OPD_COMTEMPLATE_PHRASE ORDER BY CLASS_CODE,SEQ";
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 点击树
     * 
     * @param parm
     *            Object
     */
    public void onTreeDoubled(Object parm) {
        TTreeNode node = (TTreeNode) parm;
        if (null == node) {
            return;
        }
        if ("4".equals(node.getType())) {
            TParm dataParm = (TParm) node.getData();
            TParm param = new TParm();
            param.setData("NODE", dataParm);
            param.setData("TWORD", word);
            param.addListener("onReturnContent", this, "onReturnContent");
            TWindow window = (TWindow) openWindow("%ROOT%\\config\\emr\\EMRSubTemplatePreview.x", param, true);
            //
            window.setBounds(290,135,601,351);
            window.setX(ImageTool.getScreenWidth() - window.getWidth());
    		window.setY(0);
    		//
    		AnimationWindowUtils.show(window);       
            AWTUtilities.setWindowOpacity(window, 0.9f);
            window.setVisible(true);
        }
    }

}
