package com.javahis.ui.sys;

import com.dongyang.control.TControl;

import java.util.Vector;

import com.dongyang.ui.TTreeNode;

import jdo.sys.Operator;
import jdo.sys.SYSRuleTool;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title:检验检查字典档
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 1.0 (c) 2017
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author liuyalin
 */
public class SYSMedicalDepartmentChargesControl extends TControl {

	public SYSMedicalDepartmentChargesControl() {
	}

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
	 * 当前选中的项目代码(SYS_FEE_HISTORY中：ORDER_CODE/SYS_ORDERSETDETAI中：ORDERSET_CODE)
	 */
	String orderCode = "";
	// --------------------
	TDataStore dataStore = TIOM_Database.getLocalTable("SYS_FEE");

	// 根据order拿到单价
	public double getPrice(String code) {
		if (dataStore == null)
			return 0.0;
		String bufferString = dataStore.isFilter() ? dataStore.FILTER
				: dataStore.PRIMARY;
		TParm parm = dataStore.getBuffer(bufferString);
		Vector vKey = (Vector) parm.getData("ORDER_CODE");
		Vector vPrice = (Vector) parm.getData("OWN_PRICE");
		int count = vKey.size();
		for (int i = 0; i < count; i++) {
			if (code.equals(vKey.get(i)))
				return TypeTool.getDouble(vPrice.get(i));
		}
		return 0.0;
	}

	/**
	 * 界面的控件
	 */
	// 树
	TTree tree;
	// 表
	TTable TABLE;

	public void onInit() { // 初始化程序
		super.onInit();
		tree = (TTree) callFunction("UI|TREE|getThis");
		// 初始化树
		onInitTree();
		// 给tree添加监听事件
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
		// 初始化结点
		onInitNode();
		callFunction("UI|downTable|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
	}

	/**
	 * 初始化树
	 */
	public void onInitTree() {
		// 得到树根
		treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
		if (treeRoot == null)
			return;
		// 给根节点添加文字显示
		treeRoot.setText("检验检查分类");
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
	 * 初始化树的结点
	 */

	public void onInitNode() {
		// 给dataStore赋值
		treeDataStore
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='EXM_RULE'");
		// 如果从dataStore中拿到的数据小于0
		if (treeDataStore.retrieve() <= 0)
			return;
		// 过滤数据,是编码规则中的科室数据
		ruleTool = new SYSRuleTool("EXM_RULE");
		if (ruleTool.isLoad()) { // 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
			TTreeNode node[] = ruleTool.getTreeNode(treeDataStore,
					"CATEGORY_CODE", "CATEGORY_CHN_DESC", "Path", "SEQ");
			// 循环给树安插节点
			for (int i = 0; i < node.length; i++)
				treeRoot.addSeq(node[i]);
		}
		// 得到界面上的树对象
		TTree tree = (TTree) callMessage("UI|TREE|getThis");
		// 更新树
		tree.update();
		// 设置树的默认选中节点
		tree.setSelectNode(treeRoot);
	}

	/**
	 * 单击树
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeClicked(Object parm) {
		// // 清空
		// onClear();
		// 得到点击树的节点对象
		TTreeNode node = tree.getSelectNode();
		if (node == null)
			return;
		// 得到table对象
		TTable table = (TTable) this.callFunction("UI|upTable|getThis");
		// 判断点击的是否是树的根结点
		if (node.getType().equals("Root")) {
			// 如果是树的根接点table上不显示数据
			table.removeRowAll();
		} else { // 如果点的不是根结点
					// 拿到当前选中的节点的id值
			String id = node.getID();
			String sql = getSQL(id);
		}
	}

	private String getSQL(String orderCode) {
		String sql = " ";
		// 配过滤条件
		if (orderCode != null && orderCode.length() > 0)
			sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,B.PRICE3,A.ORDERSET_CODE,A.ORDER_DESC2,A.OWN_PRICE2,A.DOSAGE_QTY "
					+ "FROM (  SELECT A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,A.ORDERSET_CODE,B.ORDER_DESC ORDER_DESC2,"
					+ "B.OWN_PRICE OWN_PRICE2,A.DOSAGE_QTY "
					+ "FROM (SELECT A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,B.ORDER_CODE ORDERSET_CODE,B.DOSAGE_QTY "
					+ "FROM SYS_FEE A, SYS_ORDERSETDETAIL B "
					+ "WHERE B.ORDERSET_CODE(+) = A.ORDER_CODE "
					+ "AND ORDERSET_FLG = 'Y' "
					+ "AND A.ACTIVE_FLG = 'Y' "
					+ "AND A.ORDER_CODE LIKE 'Y%') A,SYS_FEE B "
					+ "WHERE A.ORDERSET_CODE = B.ORDER_CODE "
					+ "GROUP BY A.ORDER_CODE,A.ORDER_DESC,A.OWN_PRICE,A.ORDERSET_CODE,B.ORDER_DESC,B.OWN_PRICE,A.DOSAGE_QTY) A,"
					+ "(SELECT A.ORDERSET_CODE, SUM (A.DOSAGE_QTY * B.OWN_PRICE) PRICE3 "
					+ "FROM SYS_ORDERSETDETAIL A, SYS_FEE B "
					+ "WHERE B.ORDER_CODE = A.ORDER_CODE "
					+ "GROUP BY ORDERSET_CODE) B "
					+ "WHERE B.ORDERSET_CODE(+) = A.ORDER_CODE "
					+ "AND B.ORDERSET_CODE LIKE '"
					+ orderCode
					+ "%'"
					+ "GROUP BY A.ORDER_CODE, A.ORDER_DESC,B.PRICE3,A.ORDERSET_CODE,A.ORDER_DESC2,A.OWN_PRICE2, A.DOSAGE_QTY "
					+ "ORDER BY ORDER_CODE ";
		System.out.println("-------------" + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		this.getTTable("TABLE").setParmValue(parm);
		return null;
	}

//	/**
//	 * 打印方法
//	 */
//	public void onPrint() {
//		TTable table = getTTable("TABLE");
//		// 汇总
//		if (table.getRowCount() <= 0) {
//			this.messageBox("没有打印数据");
//			return;
//		} else {
//			// 打印数据
//			TParm data = new TParm();
//			// 表头数据
//			data.setData("TITLE", "TEXT", Manager.getOrganization()
//					.getHospitalCHNFullName(Operator.getRegion())
//					+ "医技科室收费套餐报表");
//
//			data.setData("DATE", "TEXT",
//					"制表时间: "
//							+ SystemTool.getInstance().getDate().toString()
//									.substring(0, 10).replace('-', '/'));
//			data.setData("USER", "TEXT", "制表人: " + Operator.getName());
//
//			// 表格数据
//			TParm parm = new TParm();
//			TParm tableParm = table.getShowParmValue();
//			for (int i = 0; i < table.getRowCount(); i++) {
//				parm.addData("ORDER_CODE", tableParm.getValue("ORDER_CODE", i));
//				parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
//				parm.addData("PRICE3", tableParm.getValue("PRICE3", i));
//				parm.addData("ORDERSET_CODE",
//						tableParm.getValue("ORDERSET_CODE", i));
//				parm.addData("ORDER_DESC2",
//						tableParm.getValue("ORDER_DESC2", i));
//				parm.addData("OWN_PRICE2", tableParm.getValue("OWN_PRICE2", i));
//				parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
//			}
//			parm.setCount(parm.getCount("ORDER_CODE"));
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_CODE");
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
//			parm.addData("SYSTEM", "COLUMNS", "PRICE3");
//			parm.addData("SYSTEM", "COLUMNS", "ORDERSET_CODE");
//			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC2");
//			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE2");
//			parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
//			data.setData("tablePrint", parm.getData());
//			// 调用打印方法
//			// this.messageBox_(data);
//			// this.messageBox_(parm.getData());
//			this.openPrintWindow(
//					"%ROOT%\\config\\prt\\opb\\OPBMedicalDepartmentCharges.jhw",
//					data);
//			// this.openPrintDialog(
//			// "%ROOT%\\config\\prt\\opb\\ceshi.jhw", data);
//
//		}
//
//	}

	/**
	 * 导出Excel
	 */
	public void onExport() {
		TTable table = this.getTTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox_("暂无导出Excel数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "医技科室收费套餐报表");

	}

	/**
	 * 获取TTable
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		TABLE.removeRowAll();
	}

}
