package com.javahis.ui.emr;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTreeEvent;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: bluecore
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class EMRClpNgControl extends TControl {
	private static final String TREE = "TREE";
	/**
	 * 拿到TWORD对象
	 */
	private static final String WORD = "WORD";

	private TWord word;
	/**
	 * 树根
	 */
	private TTreeNode root;

	public void onInit() {
		// JDO类一般用于操作数据库,这个类封装了对数据库操作的大多数方法
		super.onInit();
		initWord();
		// 双击选中树项目
		addEventListener(TREE + "->" + TTreeEvent.DOUBLE_CLICKED,
				"onTreeDoubleClicked");
		loadTree();
		String subClassCode = (String) this.getParameter();
		if (subClassCode != null) {
			this.onOpenTemplet(subClassCode);
		}
	}

	/**
	 * 初始化WORD
	 */
	public void initWord() {
		word = this.getTWord(WORD);
		this.word.setCanEdit(false);
	}

	/**
	 * 得到WORD对象
	 * 
	 * @param tag
	 *            String
	 * @return TWord
	 */
	public TWord getTWord(String tag) {
		return (TWord) this.getComponent(tag);
	}

	/**
	 * 初始化树
	 */
	public void loadTree() {
		// 得到Tree的基础数据
		String sql = "SELECT * FROM EMR_CLASS WHERE CLASS_CODE>'EMR90' AND PARENT_CLASS_CODE IS NOT NULL";
		String rootsql = "SELECT * FROM EMR_CLASS WHERE CLASS_DESC='临床知识库'";
		TParm result = new TParm(this.getDBTool().select(sql));
		TParm rootParm = new TParm(this.getDBTool().select(rootsql));
		root = this.getTTree(TREE).getRoot();
		root.setText("临床知识库");
		root.setType("Root");
		root.setID(rootParm.getValue("CLASS_CODE", 0));
		// 初始化界面前清理掉所有叶子节点
		root.removeAllChildren();
		// 开始初始化树的数据
		int count = result.getCount();
		for (int i = 0; i < count; i++) {
			TParm dataParm = result.getRow(i);
			// 节点
			TTreeNode treeNode = new TTreeNode();
			treeNode.setText(dataParm.getValue("CLASS_DESC"));
			treeNode.setID(dataParm.getValue("CLASS_CODE"));
			treeNode.setType("2");
			putNodeInTree(dataParm, treeNode);
		}
		this.getTTree(TREE).update();

	}

	/**
	 * 
	 * @param dataParm
	 *            TParm
	 * @param root
	 *            TTreeNode
	 */
	private void putNodeInTree(TParm dataParm, TTreeNode treeNode) {
		// 是否叶节点,加模板
		if (dataParm.getValue("LEAF_FLG").equals("Y")) {
			String leafsql = "SELECT  * FROM  EMR_TEMPLET WHERE CLASS_CODE='"
					+ treeNode.getID() + "'";
			TParm leafParm = new TParm(this.getDBTool().select(leafsql));
			if (leafParm.getCount() > 0) {
				for (int i = 0; i < leafParm.getCount(); i++) {
					TTreeNode LEAFNode = new TTreeNode();
					LEAFNode.setText(leafParm.getValue("SUBCLASS_DESC", i));
					LEAFNode.setID(leafParm.getValue("SUBCLASS_CODE", i));
					LEAFNode.setType("4");
					LEAFNode.setData(leafParm.getRow(i));
					treeNode.addSeq(LEAFNode);
				}
			}
		}
		String sql = "SELECT  * FROM  EMR_CLASS WHERE CLASS_CODE IN (SELECT PARENT_CLASS_CODE FROM EMR_CLASS WHERE CLASS_CODE='"
				+ treeNode.getID() + "')";
		TParm result = new TParm(this.getDBTool().select(sql));
		// 母节点
		TTreeNode ParentNode = new TTreeNode();
		ParentNode.setText(result.getValue("CLASS_DESC", 0));
		ParentNode.setID(result.getValue("CLASS_CODE", 0));
		ParentNode.setType("2");
		// 节点为根节点
		if (treeNode.getID().equals(root.getID())) {
			return;
		}
		// 此节点存在
		if (root.findNodeForID(treeNode.getID()) != null) {
			return;
		}
		// 此节点的母节点存在
		else if (root.findNodeForID(ParentNode.getID()) != null) {
			root.findNodeForID(ParentNode.getID()).addSeq(treeNode);
		}
		// 此节点的母节点不存在
		else {
			ParentNode.addSeq(treeNode);
			putNodeInTree(result.getRow(0), ParentNode);
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
		TTreeNode node = this.getTTree(TREE).getSelectNode();
		// 如果是模版才可打开;
		if (node.getType().equals("4")) {
			onOpenTemplet();
		}
	}

	/**
	 * 打开模版
	 */
	public void onOpenTemplet() {
		TTreeNode node = this.getTTree(TREE).getSelectNode();
		String templetPath = ((TParm) node.getData()).getValue("TEMPLET_PATH");
		String templetName = ((TParm) node.getData()).getValue("EMT_FILENAME");
		// 打开模板
		word.onOpen(templetPath, templetName, 2, false);
		// 不可编辑
		this.word.setCanEdit(false);
	}

	/**
	 * 打开参数模板
	 * 
	 * @param subClassCode
	 *            String
	 */
	public void onOpenTemplet(String subClassCode) {
		String sql = "SELECT EMT_FILENAME,CLASS_CODE,TEMPLET_PATH,SYSTEM_CODE FROM EMR_TEMPLET WHERE SUBCLASS_CODE='"
				+ subClassCode + "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		String templetPath = parm.getValue("TEMPLET_PATH", 0);
		String templetName = parm.getValue("EMT_FILENAME", 0);
		// 打开模板
		word.onOpen(templetPath, templetName, 2, false);
		// 不可编辑
		this.word.setCanEdit(false);
		word.onPreviewWord();
	}
	/**
	 * 打印
	 */
	public void onPrint() {
		if (this.word.getFileOpenName() != null) {
			this.word.onPreviewWord();
			this.word.print();
		} else {
			// 请选择病历
			this.messageBox("E0099");
		}
	}
	/**
	 * 实例化
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 
	 * @param tag
	 *            String
	 * @return TTree
	 */
	public TTree getTTree(String tag) {
		return (TTree) this.getComponent(tag);
	}
}
