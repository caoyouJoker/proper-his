package com.javahis.ui.inv;

import java.awt.Component;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SYSPublic;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title: 手术包字典  
 * </p>
 * 
 * <p>
 * Description:手术包设定
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fudw
 * @version 1.0
 * 
 */
public class INVPackControl extends TControl {
	/**
	 * 树根
	 */
	private TTreeNode treeRoot;
	/**
	 * 编号规则类别工具
	 */
	private SYSRuleTool ruleTool;
	/**
	 * 树的数据放入datastore用于对树的数据管理
	 */
	private TDataStore treeDataStore = new TDataStore();
	/**
	 * 主表选中的行
	 */
	private int tableMSelectedRow = -1;
	/**
	 * 主表
	 */
	private TTable tableM;
	/**
	 * 细表
	 */
	private TTable tableD;
	/**
	 * 树
	 */
	private TTree tree;

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		// 树
		tree = (TTree) getComponent("TREE");
		// 初始化树
		onInitSelectTree();
		// 给tree添加监听事件
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
		// 给Table添加侦听事件(值改变)
		addEventListener("TABLEM->" + TTableEvent.CHANGE_VALUE,
				"onTableMChangeValue");
		// 给Table添加侦听事件(值改变)
		addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeDValue");
		// table专用的监听
		callFunction("UI|TABLED|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		// 种树
		onCreatTree();
		// table不可编辑
		tableM = (TTable) getComponent("TABLEM");
		tableD = (TTable) getComponent("TABLED");
		// 初始化table
		tableM.setLockColumns("3,5,10,11,12");
//		tableM.setLockColumns("0,2,7,8,9"); // wm2013-05-28
											// tableM.setLockColumns("0,1,2,3,4,5,6,7");
		onInitTable();
		// 添加观察者
		observer();
	}

	/**
	 * 添加观察者
	 */
	public void observer() {
		TTable table = (TTable) callFunction("UI|TABLED|getThis");
		TDS tds = (TDS) table.getDataStore();
		// 给主表添加观察者
		TDataStore dataStore = new TDataStore();
		dataStore
				.setSQL("SELECT INV_CODE,INV_ABS_DESC AS INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
		dataStore.retrieve();
		tds.addObserver(new INVBaseTob(dataStore));
	}

	/**
	 * 初始化table
	 */
	public void onInitTable() {
		// 主表
		tableM.setSQL("SELECT * from INV_PACKM WHERE PACK_CODE IS NULL ");
		tableM.retrieve();
		// 细表
		tableD.setSQL("SELECT * from INV_PACKD WHERE PACK_CODE IS NULL ORDER BY SEQ_NO");
		tableD.retrieve();
	}

	/**
	 * 初始化树
	 */
	public void onInitSelectTree() {
		// 得到树根
		treeRoot = tree.getRoot();
		if (treeRoot == null)
			return;
		// 给根节点添加文字显示
		// zhangyong20091028 手术包管理==>灭菌包管理
		treeRoot.setText("灭菌包管理");
		// 给根节点赋tag
		treeRoot.setType("Root");
		// 设置根节点的id
		treeRoot.setID("");
		// 清空所有节点的内容
		treeRoot.removeAllChildren();
		// 调用树点初始化方法
		callMessage("UI|TREE|update");
	}

	/**
	 * 初始化树上的节点
	 */
	public void onCreatTree() {
		// 给dataStore赋值
		treeDataStore
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='INV_PACK'");
		// 如果从dataStore中拿到的数据小于0
		if (treeDataStore.retrieve() <= 0)
			return;
		// 过滤数据,是编码规则中的物资数据
		ruleTool = new SYSRuleTool("INV_PACK");
		if (ruleTool.isLoad()) {
			// 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
			TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
					"CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
			// 循环给树安插节点
			for (int i = 0; i < node.length; i++)
				treeRoot.addSeq(node[i]);
		}
		// 更新树
		tree.update();
		// 设置树的默认选中节点
		tree.setSelectNode(treeRoot);
		getTreeLoyar();
	}

	/**
	 * 单击树
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeClicked(Object parm) {
		// 得到点击树的节点对象
		TTreeNode node = (TTreeNode) parm;
		if (node == null)
			return;
		// 得到table对象
		// table接收所有改变值
		tableM.acceptText();
		// table不可编辑
		// fux
//		tableM.setLockColumns("0,2,7,8,9"); // wm2013-05-28
											// tableM.setLockColumns("0,1,2,3,4,5,6,7");
		tableM.setLockColumns("3,5,10,11,12");
		tableMSelectedRow = -1;
		// 得到父ID
		String parentID = node.getID();
		int classify = 1;
		if (parentID.length() > 0)
			classify = ruleTool.getNumberClass(parentID) + 1;

		// 如果是最小节点,可以增加一行
		if (classify >= ruleTool.getClassifyCurrent()) {// wm2013-05-28 classify
														// >
														// ruleTool.getClassifyCurrent()
			// 如有数据变更提示
			if (!checkValueChange())
				return;
			onCleckClassifyNode(parentID, tableM);
		}
	}

	/**
	 * 给展开层combox赋值
	 */
	public void getTreeLoyar() {
		TComboBox comBox = (TComboBox) this
				.callFunction("UI|TREELOYAR|getThis");
		// 吧数据付给combo
		comBox.setParmValue(SYSPublic.getTreeLoyar(treeRoot));
		// 刷现界面上的combo
		comBox.updateUI();
	}

	/**
	 * 展开到对应的层
	 */
	public void onExtendToLoyar() {
		tree.expandLayer(getValueInt("TREELOYAR") - 1);
		tree.collapseLayer(getValueInt("TREELOYAR"));
	}

	/**
	 * 选中最后一层的事件
	 * 
	 * @param parentID
	 *            String
	 * @param table
	 *            TTable
	 */
	public void onCleckClassifyNode(String parentID, TTable table) {
		// 检核是否有数据变更
		if (!checkValueChange())
			return;
		String con = "";
		// 改为权限控制 0409权限以及其他
//		if (this.getPopedem("dept0409")) {
//			con = " AND SEQ_FLG = '1' ";
//		}
		//20150304 wangjingchun add start
		//登录科室
		if(this.getPopedem("dept0409")){
			con = " AND DISTINGUISH_FLG = 'GY' ";
		}else if(this.getPopedem("ALL")){
			con = "";
		}else{
			con = " AND DISTINGUISH_FLG = 'GYS' ";
		}
		//20150304 wangjingchun add end
		// table中的datastore中查询数据sql
		table.setSQL("SELECT * FROM INV_PACKM where PACK_CODE like '"
				+ parentID + "%'" + con);
		// 查找数据
		table.retrieve();
		// 放置数据到table中
		table.setDSValue();
		// table可以编辑
//		table.setLockColumns("0"); // wm2013-07-14
		// 加入第0列为启用
		 table.setLockColumns("3");
		// 处理细表数据
		// 清空显示数据
		tableD.clearSelection();
		// 清空table上所有数据
		tableD.removeRowAll();
		// 清空保存记录
		tableD.getDataStore().resetModify();

	}

	/**
	 * table双击选中树
	 */
	public void onTableDoubleCleck() {
		tableM.acceptText();
		int row = tableM.getSelectedRow();
		String value = tableM.getItemString(row, 0);
		// 得到上层编码
		String partentID = ruleTool.getNumberParent(value);
		TTreeNode node = treeRoot.findNodeForID(partentID);
		if (node == null)
			return;
		// 设置树的选中节点
		tree.setSelectNode(node);
		tree.update();
		// 调用查询事件
		onCleckClassifyNode(partentID, tableM);
		// table中设置选中行
		int count = tableM.getRowCount(); // table的行数
		for (int i = 0; i < count; i++) {
			// 拿到物资代码
			// String invCode = tableM.getItemString(i, 0);
			String invCode = tableM.getItemString(i, 1);
			if (value.equals(invCode)) {
				// 设置选中行
				tableM.setSelectedRow(i);
				return;
			}
		}
	}

	/**
	 * 主表点击事件
	 */
	public void onTableMClicked() {
		int row = tableM.getSelectedRow();
		// 如果选择同一行，不执行任何操作
		if (tableMSelectedRow == row)
			return;
		// 检核数据是否变更
		if (!checkValueChange())
			return;
		tableMSelectedRow = row;
		// 拿到包号
		String packCode = tableM.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() <= 0) {
			this.messageBox("请先输入手术包包号!");
			return;
		}
		// 放
		this.setValue("PACK_CODED", packCode);
		// 处理细表数据
		tableD.acceptText();
		tableD.removeRowAll();
		// 取table数据
		tableD.setSQL("SELECT * FROM INV_PACKD WHERE PACK_CODE='" + packCode
				+ "' ORDER BY SEQ_NO ");
		tableD.retrieve();
		tableD.setDSValue();
		this.onNewPackD();
	}

	/**
	 * 细表点击事件
	 */
	public void onTableDClecked() {
		tableM.acceptText();
	}

	/**
	 * 新增方法
	 */
	public void onNew() {
		// 如果选中主表中或者选中细表则新增细项
		if (tableM.getSelectedRow() >= 0) {
			if(tableD.getSelectedRow()==tableD.getRowCount()-1){
			   this.onNewPackD();
			}else{
			   this.onNewPackDIns();
			}
			return;
		}
		// 新增主项
		onNewPack();
	}

	/**
	 * 新增手术包
	 */
	public void onNewPack() {
		// 接收文本
		tableM.acceptText();
		// 取table数据
		TDataStore dataStore = tableM.getDataStore();

		// 找到当前分类的最大包号
//		String maxCode = getMaxCode(dataStore, "PACK_CODE");
		// 找到选中的树节点
		TTreeNode node = tree.getSelectionNode();
		// 如果没有选中的节点
		if (node == null)
			return;
		// 得到父ID
		String parentID = node.getID();
		//20150304 wangjingchun add start
		// 找到当前分类的最大包号
		String sql = "SELECT MAX(PACK_CODE) AS PACK_CODE FROM INV_PACKM "
						+" where PACK_CODE like '"+parentID+"%' ";
		TParm max_Code_Parm = new TParm(TJDODBTool.getInstance().select(sql));
		String maxCode = max_Code_Parm.getValue("PACK_CODE",0);
		//20150304 wangjingchun add end
		int classify = 1;
		// 如果点的是树的父节点存在,得到当前编码规则
		if (parentID.length() > 0)
			classify = ruleTool.getNumberClass(parentID) + 1;
		// 如果是最小节点,可以增加一行
		if (classify >= ruleTool.getClassifyCurrent()) { // 得到默认的自动添加的科室代码
															// wm2013-05-28
															// 原classify >
															// ruleTool.getClassifyCurrent()
			String no = ruleTool.getNewCode(maxCode, classify);
			// 得到新添加的table数据行号
			int row = tableM.addRow();
			// 默认启用标记为Y
			tableM.setItem(row, "ACTIVE_FLG", "Y");
			// 默认手术包号
			tableM.setItem(row, "PACK_CODE", parentID + no);
			// 默认序号管理
			tableM.setItem(row, "SEQ_FLG", "1");

			// 选中行调整到新增行
			tableMSelectedRow = row;
			// 处理细表数据
			// 取table数据
			tableD.setSQL("SELECT * FROM INV_PACKD WHERE PACK_CODE='"
					+ (parentID + no) + "' ");
			tableD.retrieve();
			tableD.setDSValue();
		}
	}

	/**
	 * 新增包明细
	 */
	public void onNewPackD() {
		if (tableMSelectedRow < 0) {
			messageBox("请选择手术包!");
			return;
		}
		String packCode = tableM.getItemString(tableMSelectedRow, "PACK_CODE");
		if (packCode == null || packCode.length() <= 0) {
			this.messageBox("请选择手术包!");
			return;
		}
		    // 新增行
		int row = tableD.addRow();
		    // 包号
		tableD.setItem(row, "PACK_CODE", packCode);	
	}
	/**
	 * 插入包明细
	 */
	public void onNewPackDIns() {
		if (tableMSelectedRow < 0) {
			messageBox("请选择手术包!");
			return;
		}
		String packCode = tableM.getItemString(tableMSelectedRow, "PACK_CODE");
		if (packCode == null || packCode.length() <= 0) {
			this.messageBox("请选择手术包!");
			return;
		}
		int tableDSelectRow = tableD.getSelectedRow();
		if (tableDSelectRow >= 0) {
			tableD.addRow(tableDSelectRow);
			// 包号
			tableD.setItem(tableDSelectRow, "PACK_CODE", packCode);
		}
	}

	/**
	 * 弹出sysfee对话框
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		// 弹出sysfee对话框的列
		if (column != 0) // if (column != 0)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加sys_fee弹出窗口
		textfield.setPopupMenuParameter("INVBASE", getConfigParm().newConfig(
				"%ROOT%\\config\\inv\\INVBasePopup.x"));
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield
				.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "newInv");
	}

	/**
	 * 设置明细
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newInv(String tag, Object obj) {
		// sysfee返回的数据包
		TParm parm = (TParm) obj;
		// sysfee返回的物资代码
		String inv_code = parm.getValue("INV_CODE");
		int row = tableD.getSelectedRow();
		// 得到包名
		String packCode = tableD.getItemString(row, "PACK_CODE");
		TDataStore dataStore = tableD.getDataStore();
		if (dataStore.exist("PACK_CODE='" + packCode + "'AND INV_CODE ='"
				+ inv_code + "'")) {
			messageBox_("编号" + inv_code + "重复!");
			return;
		}
		tableD.acceptText();
		// 物资代码
		dataStore.setItem(row, "INV_CODE", inv_code);
		// 序号管理
		dataStore.setItem(row, "SEQMAN_FLG", parm.getData("SEQMAN_FLG"));
		// 单位
		dataStore.setItem(row, "STOCK_UNIT", parm.getData("STOCK_UNIT"));
		// 物资类型
		if (parm.getData("SEQMAN_FLG").equals("Y")) {
			dataStore.setItem(row, "PACK_TYPE", 0); // 非一次性
		} else {
			dataStore.setItem(row, "PACK_TYPE", 1); // 一次性
		}
		// 规格 2013-07-18
		dataStore.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));

		// //物资类型
		// dataStore.setItem(row, "PACK_TYPE", parm.getData("PACK_TYPE"));

		tableD.setDSValue();
		tableD.getTable().grabFocus();
		tableD.setSelectedColumn(2);
		tableD.setSelectedRow(row);
		if(tableD.getSelectedRow()==tableD.getRowCount()-1){
			this.onNew();
		}
	}

	/**
	 * 主表值改变事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableMChangeValue(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return false;
		// 拿到table上的parmmap的列名
		String columnName = tableM.getDataStoreColumnName(node.getColumn());
		// 得到改变的行
		int row = node.getRow();
		// 拿到当前改变后的数据
		String value = "" + node.getValue();
		// 如果是名称改变了拼音1自动带出,并且名称不能为空
		if ("PACK_DESC".equals(columnName)) {
			if (value.equals("") || value == null) {
				messageBox_("名称不能为空!");
				return true;
			}
			String py = SYSHzpyTool.getInstance().charToCode(value);
			tableM.setItem(row, "PY1", py);
			return false;
		}
		// 序号管理不能为空
		if ("SEQ_FLG".equals(columnName)) {
			if (value.equals("") || value == null) {
				messageBox_("请选择是否做序号管理!");
				return true;
			}
			return false;
		}
		// 如果是代码改变了要进行几项校验
		if ("PACK_CODE".equals(columnName)) {
			// 保存编码规则的数据长度
			int length = ruleTool.getTot();
			// 如果改编后的长度不等于编码规则中的长度返回
			if (length != value.length()) {
				messageBox_("编号" + value + "长度不等于" + length + "!");
				return true;
			}
			// 对代码进行检核...如果存在则重复
			if (tableM.getDataStore().exist("PACK_CODE='" + value + "'")) {
				messageBox_("编号" + value + "重复!");
				return true;
			}
			// 得到上层编码
			String partent = ruleTool.getNumberParent(value);
			// 检核上层编码是否存在
			if (treeRoot.findNodeForID(partent) == null) {
				messageBox_("编号分类" + partent + "不存在!");
				return true;
			}
			// 拿到选中的节点
			String nodeId = tree.getSelectionNode().getID();
			// 检核上级编号是否正确
			int ruleLength = nodeId.length();
			// 不能换分类
			if (!nodeId.equals(value.substring(0, ruleLength))) {
				messageBox_("上级编号分类" + partent + "录入错误!");
				return true;   
			}
			// 主表里的包号更改...细表带动同时更改
			// 细表的项目数
			int rowCount = tableD.getRowCount();
			for (int i = 0; i < rowCount; i++) {
				if (tableD.getItemString(i, "PACK_CODE").equals(
						node.getOldValue())) {
					messageBox("已新增细项,不可更改");
					return true;
				}
			}
			return false;
		}
		return false;
	}

	/**
	 * 细表值改变事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableDChangeValue(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return true;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// 拿到table上的parmmap的列名
		String columnName = tableD.getDataStoreColumnName(node.getColumn());
		// 如果是序号更改                                      
		if ("QTY".equals(columnName)) {
			int seq = Integer.parseInt("" + node.getValue());
			// 不能为空
			if (seq <= 0) {
				messageBox_("数量不能小于0!");
				return true;
			}
		}
		return false;
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 检核是否有数据变更
		if (!checkValueChange())
			return;
		// 得到点击树的节点对象
		TTreeNode node = tree.getSelectionNode();
		if (node == null)
			return;
		// table接收所有改变值
		tableM.acceptText();
		// 如果点击的是树的根结点
		if (node.getType().equals("Root")) {
			// 根据过滤条件过滤Tablet上的数据
//			System.out.println("select * from INV_PACKM WHERE PACK_CODE IS NOT NULL "
//							+ onFilterM());
			tableM
					.setSQL("select * from INV_PACKM WHERE PACK_CODE IS NOT NULL "
							+ onFilterM());
			tableM.retrieve();
			tableM.setDSValue();
			return;
		}
		// 得到父ID
		String parentID = node.getID();
		// table中的datastore中查询数据sql
//		System.out.println("select * from INV_PACKM where PACK_CODE like '"
//				+ parentID + "%'" + onFilterM());
		tableM.setSQL("select * from INV_PACKM where PACK_CODE like '"
				+ parentID + "%'" + onFilterM());
		// 查找数据
		tableM.retrieve();
		// 放置数据到table中
		tableM.setDSValue();
		// 处理细表数据
		// 清空选中
		tableD.clearSelection();
		// 清空table上所有数据
		tableD.removeRowAll();
		// 清空保存记录
		tableD.getDataStore().resetModify();
	}

	/**
	 * 主表过滤条件
	 * 
	 * @return String
	 */
	public String onFilterM() {
		// 过滤条件
		String filterStr = "";
		// 包号
		String value = getValueString("PACK_CODE");
		if (value.length() > 0)
			filterStr = " AND PACK_CODE like '" + value + "%' ";
		// 包名
		value = getValueString("PACK_DESC");
		if (value.length() > 0)
			filterStr += " AND PACK_DESC like '" + value + "%' ";
		// 拼音
		value = getValueString("PY1");
		if (value.length() > 0)
			filterStr += " AND PY1 like'" + value + "%' ";
		// fux modify 20140519
		// 序号管理
		value = getValueString("SEQ_FLG");
		if (value.length() > 0)
			filterStr += " AND SEQ_FLG ='" + value + "' ";
		// 备注
		value = getValueString("DESCRIPTION");
		if (value.length() > 0)
			filterStr += " AND BUYWAY_CODE like'" + value + "%' ";
		//20150304 wangjingchun add start
		// 所属
		value = getValueString("DISTINGUISH_FLG");
		if (value.length() > 0)
			filterStr += " AND DISTINGUISH_FLG = '" + value + "' ";
		//登录科室
		if(this.getPopedem("dept0409")){
			filterStr += " AND DISTINGUISH_FLG = 'GY' ";
		}else if(this.getPopedem("ALL")){
			filterStr += "";
		}else{
			filterStr += " AND DISTINGUISH_FLG = 'GYS' ";
		}
		//20150304 wangjingchun add end
		return filterStr;
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// 如有数据变更提示
		if (!checkValueChange())
			return;
		// 清空过滤条件
		clearValue("PACK_CODE;PACK_DESC;PY1;SEQ_FLG;DESCRIPTION;");
		// 主表
		tableM.acceptText();
		// 清空选中状态
		tableM.clearSelection();

		tableM.removeRowAll(); // wm2013-05-28添加
		// 清空保存记录
		tableM.getDataStore().resetModify();

		// 同样处理细表
		tableD.acceptText();
		// 清空显示数据
		tableD.clearSelection();
		// 清空table上所有数据
		tableD.removeRowAll();
		// 清空保存记录
		tableD.getDataStore().resetModify();
		// table不可编辑
		tableM.setLockColumns("3,5,10,11,12");//20150304 wangjingchun add
//		tableM.setLockColumns("0,2,7,8,9"); // wm2013-05-28
											// tableM.setLockColumns("0,1,2,3,4,5,6,7");
		// tableM.setLockColumns("1,3,8,9,10");
		// 树上选中跟节点
		tree.setSelectNode(treeRoot);
		// 设置主表选中行
		tableMSelectedRow = -1;
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		// 如果是细项删除
		int tableDSelectRow = tableD.getSelectedRow();
		if (tableDSelectRow >= 0) {
//			tableD.removeRow(tableDSelectRow);
			this.onDeleteD(tableDSelectRow, tableD);
			return;
		}

		int row = this.tableM.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要删除的数据！");
			return;
		}
		if (messageBox("提示信息", "是否删除?", this.YES_NO_OPTION) != 0)
			return;
//		this.onDelTableDEmptyRow();
		this.tableM.getDataStore().deleteRow(row);
		int rowCount = this.tableD.getDataStore().rowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			if (!this.tableD.getDataStore().isActive(i)
					&& this.tableD.getDataStore().getItemString(i,
							"RECEIPT_NO").length() == 0) {
				continue; 
			}
			this.tableD.getDataStore().deleteRow(i);
		}
		this.tableM.setDSValue();
		this.tableD.setDSValue();
		String arraySqlTable1[] = this.tableM.getUpdateSQL();
		String arraySqlTable2[] = this.tableD.getUpdateSQL();
		String arraySql[] = StringUtil.getInstance().copyArray(arraySqlTable1,
				arraySqlTable2);  
		TParm sqlParm = new TParm();
		sqlParm.setData("SQL", arraySql);  
		TParm actionParm = new TParm(TJDODBTool.getInstance().update(arraySql));           
		if (actionParm.getErrCode() < 0) {
			this.messageBox("删除失败！");
			return;        
		}
		this.messageBox("删除成功！");
		this.onClear();
	
	
//		// 主表选中的行 
//		int tableMSelectRow = tableM.getSelectedRow();
//		// 细表选中的行
//		int tableDSelectRow = tableD.getSelectedRow();
//		// 如果选中了主表中项目
//		if (tableMSelectRow >= 0 && tableDSelectRow < 0) {
//			onDeleteM(tableMSelectRow, tableM, tableD);
//			return;
//		}
//		// 如果是细项删除
//		if (tableDSelectRow >= 0) {
//			tableD.removeRow(tableDSelectRow);
//			return;
//		}
	}
	
	
	
	

	/**
	 * 删除包
	 * 
	 * @param row
	 *            int
	 * @param tableM
	 *            TTable
	 * @param tableD
	 *            TTable
	 */  
	public void onDeleteM(int row, TTable tableM, TTable tableD) {
		// 得到要删除的手术包包号
		String packCode = tableM.getItemString(row, "PACK_CODE").trim();
		// 只有手术包号不为空才删除
		if (packCode != null && packCode.length() != 0) {
			// 得到细表数据量
			int rowCount = tableD.getRowCount();
			// 循环删除细表数
			for (int i = rowCount - 1; i >= 0; i--) {
				// 如果细表中的手术包号和主表相同则删除
				String packCodeD = tableD.getItemString(i, "PACK_CODE");
				if (packCode.equals(packCodeD))
					onDeleteD(i, tableD);
			}
		}
		tableM.removeRow(row);
		return;
	}

	/**
	 * 删除明细
	 * 
	 * @param row
	 *            int
	 * @param table
	 *            TTable
	 */
	public void onDeleteD(int row, TTable table) {
		table.removeRow(row);
	}

	/**
	 * 得到最大的编号
	 * 
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public String getMaxCode(TDataStore dataStore, String columnName) {
		if (dataStore == null)
			return "";
		int count = dataStore.rowCount();
		String s = "";
		for (int i = 0; i < count; i++) {
			String value = dataStore.getItemString(i, columnName);
			if (StringTool.compareTo(s, value) < 0)
				s = value;
		}
		return s;
	}

	/**
	 * 展开树上的节点
	 */
	public void onGetTreeNode() {
		String nodeId = getValueString("NODE").trim();
		if (nodeId == null || nodeId.length() <= 0)
			return;
		treeRoot.getAllowsChildren();
		treeRoot.getChildCount();

	}

	public TTreeNode getNode(TTreeNode node, String str) {

		int count = node.getChildCount();
		if (count <= 0)
			return null;
		for (int i = 0; i < count; i++) {
			// String nodeId=node.getChildAt(0);
			// return node.getChildAt(0);
		}
		return null;
	}

	/**
	 * 保存
	 * 
	 * @return boolean
	 */
	public boolean onUpdate() {
		
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		// 拿到保存sql的对象
		TJDODBTool dbTool = new TJDODBTool();
		// 接收文本
		tableM.acceptText();
		TDataStore dateStore = tableM.getDataStore();
		// 得到缓冲区名称
		String name = dateStore.isFilter() ? dateStore.FILTER
				: dateStore.PRIMARY;
		int rowsSup[] = dateStore.getModifiedRows(name);
		// 给固定数据配数据
		for (int i = 0; i < rowsSup.length; i++) {
			tableM.setItem(rowsSup[i], "OPT_USER", Operator.getID());
			tableM.setItem(rowsSup[i], "OPT_DATE", date);
			tableM.setItem(rowsSup[i], "OPT_TERM", Operator.getIP());
		}
		// 取出主表sql
		String[] sqlM = tableM.getUpdateSQL();

		// 得到代理产品表
//		this.onDelTableDEmptyRow();
		// 接收文本
		tableD.acceptText();
		
		dateStore = tableD.getDataStore();
		///////////////////////////////zhangs 
//		System.out.println("size:"+dateStore.getVector("INV_CODE").size());
//		System.out.println("getColumnIndex:"+dateStore.getColumnIndex("SEQ_NO"));
		int countRow=dateStore.getVector("INV_CODE").size();
		int columnSeqNo=dateStore.getColumnIndex("SEQ_NO");
		int columnCode=dateStore.getColumnIndex("INV_CODE");
		for(int i=0;i<countRow;i++){
			dateStore.setItem(i, columnSeqNo, i+1);
		}
		if(dateStore.getItemString(countRow-1, columnCode).equals("")){
			dateStore.deleteRow(countRow-1);
		}
		///////////////////////////////////zhangs
		// 拿到全数据缓冲区的名称
		name = dateStore.isFilter() ? dateStore.FILTER : dateStore.PRIMARY;
		int rowAngent[] = dateStore.getModifiedRows(name);
		// 给固定数据配数据
		for (int i = 0; i < rowAngent.length; i++) {
			tableD.setItem(rowAngent[i], "OPT_USER", Operator.getID());
			tableD.setItem(rowAngent[i], "OPT_DATE", date);
			tableD.setItem(rowAngent[i], "OPT_TERM", Operator.getIP());
//			tableD.setItem(rowAngent[i], "SEQ_NO", i+1);
		}
		// 取出细表sql
		String[] sqlD = tableD.getUpdateSQL();
		// 把两组sql合并(都不为空)
		if (sqlM != null && sqlD != null){
			sqlM = StringTool.copyArray(sqlM, sqlD);
		}
		// 主表改，细表没改      
		if (sqlM == null && sqlD != null){
			sqlM = sqlD;
		}   
		// 主细表都没改     
		if (sqlM == null && sqlD == null) {
			messageBox("没有要保存的数据");
			return true;
		}  

		for (String temp : sqlM) {
			System.out.println("onUpdate:" + temp);
		}
		// 保存数据
		TParm result = new TParm(dbTool.update(sqlM));
		if (result.getErrCode() < 0) {
			out("update err " + result.getErrText());
			this.messageBox("保存失败!");
			return false;
		}
		messageBox("保存成功!");  
//		// 清空保存痕迹  
		tableD.getDataStore().resetModify();
		tableM.getDataStore().resetModify();
		return true;
	}

	/**
	 * 检核数据变更提示信息（变更主细表时提示数据的变更）
	 * 
	 * @return boolean
	 */
	public boolean checkValueChange() {
		// 如果有数据变更
		if (checkData())
			switch (this.messageBox("提示信息", "数据变更是否保存",
					this.YES_NO_CANCEL_OPTION)) {     
			// 保存
			case 0:
				if (!onUpdate())
					return false;
				return true;
				// 不保存
			case 1:
				return true;
				// 撤销
			case 2:
				return false;
			}
		return true;
	}

	/**
	 * 是否关闭窗口
	 * 
	 * @return boolean true 关闭 false 不关闭
	 */
	public boolean onClosing() {
		// 如果有数据变更
		if (checkData())
			switch (this.messageBox("提示信息", "是否保存", this.YES_NO_CANCEL_OPTION)) {
			// 保存
			case 0:
				if (!onUpdate())
					return false;
				break;
			// 不保存
			case 1:
				return true;
				// 撤销
			case 2:
				return false;
			}
		// 没有变更的数据
		return true;
	}

	/**
	 * 检核数据变更
	 * 
	 * @return boolean
	 */
	public boolean checkData() {
		// 检核数据是否变更
		if (tableM.isModified())
			return true;
		///////////////////////////////zhangs 
//		this.onDelTableDEmptyRow();
		tableD.acceptText();
		TDataStore dateStore = tableD.getDataStore();
		int countRow=dateStore.getVector("INV_CODE").size();
		int columnCode=dateStore.getColumnIndex("INV_CODE");
		if(dateStore.getItemString(countRow-1, columnCode).equals("")){
			dateStore.deleteRow(countRow-1);
		}
	
		if (tableD.isModified())
			return true;
		return false;
	}
	
    /**
	 * 查看图片
	 * wangjingchun add 20150317
	 */
	public void onPicture(){

		TTable table = (TTable) this.getComponent("TABLEM");
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择手术包");
			return;
		}
		TDataStore dataStore = table.getDataStore();
		TParm oneRow = dataStore.getRowParm(row);
		String packCode = oneRow.getValue("PACK_CODE");
		TParm parm = new TParm();
		parm.setData("PACK_CODE", packCode);
		String photoName = parm.getValue("PACK_CODE")+ ".jpg";
		String root = TIOM_FileServer.getRoot();
		String dir = TIOM_FileServer.getPath("OpeInfPIC.ServerPath");
		dir = root + dir ;
		byte[] data = TIOM_FileServer.readFile(TIOM_FileServer.getSocket(),
				dir + photoName);
		if(data == null){
			this.messageBox("此手术包未上传图片");
			return;
		}
		String pic_dir = "C:\\JavaHisFile\\OpePic\\";
		try {
			  File file = new File(pic_dir);
			  if(!file.exists()){
				  file.mkdir();
			  }
		      // 源文件  
		      FileOutputStream fileOutputStream = new FileOutputStream(new File(pic_dir+photoName));  
		      fileOutputStream.write(data);  
		      // 关闭流  
		      fileOutputStream.close();  
		      Runtime.getRuntime().exec("rundll32 c:\\Windows\\System32\\shimgvw.dll,ImageView_Fullscreen "+pic_dir+photoName);
		  } catch (FileNotFoundException e) {  
		      e.printStackTrace();  
		  } catch (IOException e) {  
		      // TODO Auto-generated catch block  
		      e.printStackTrace();
		  }
	}

	
	/**
	 * 导入图片
	 * wangjingchun add 20150317
	 */
	public void onImportPicture(){
		TTable table = (TTable) this.getComponent("TABLEM");
		int row = table.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择手术包");
			return;
		}
		TDataStore dataStore = table.getDataStore();
		TParm oneRow = dataStore.getRowParm(row);
		String packCode = oneRow.getValue("PACK_CODE");
		TParm parm = new TParm();
		parm.setData("PACK_CODE",packCode);
		this.openDialog("%ROOT%\\config\\inv\\ImportPicture.x",parm);
	}
	/**
	 * 删除物资明细表空行
	 * zhangs add 20150917
	 */
	
	public void onDelTableDEmptyRow(){
//		tableD.acceptText();
//		System.out.println("onDelTableDEmptyRow"+
//				tableD.getRowCount());
//		System.out.println("onDelTableDEmptyRow"+
//				tableD.getParmValue());
//		System.out.println("onDelTableDEmptyRow"+
//				tableD.getColumnIndex("SEQ_NO"));
//		int countRow=tableD.getRowCount();
//		int column=tableD.getColumnIndex("SEQ_NO");
//		for(int i=0;i<countRow;i++){
//			tableD.setValueAt(i+1, i, column);	
//		}
		
		
//		tableD.removeRow(1);
//		TParm tableParm = null;
//		tableD.acceptText();
//		TParm parmValue = tableD.getParmValue();
//		if(parmValue.getCount()<=0){
//			return;
//		}
//		for (int i = 0; i < parmValue.getCount(); i++) {
//			tableParm = parmValue.getRow(parmValue.getCount()-1);
//			if (tableParm.getValue("INV_CHN_DESC").equals("")) {
//			   this.onDeleteD(parmValue.getCount()-1, tableD);
//			}
//		}
	}

}
