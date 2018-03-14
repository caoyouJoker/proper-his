package com.javahis.ui.ins;

import jdo.ins.INSRuleTXTool;
import jdo.ins.INSSYSRuleTXTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTreeNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;

/**
 * <p>
 * Title:三目字典
 * </p>
 * 
 * <p>
 * Description: 医保三目字典对应：诊查
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 2.0 (c) 2011
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author pangb 2011-12-09
 */
public class INSOrderJoinControl111 extends TControl {

	/**
	 * 
	 * 界面的控件
	 */
	// 树
	TTree tree;
	/**
	 * 树根
	 */
	private TTreeNode treeRoot;

	private TParm sysFeeParm;// 医嘱信息

	private String orderCode;// 点选树形图获得医嘱号码

	private TTable upTable;// 修改表格

	private TTable ruleTable;// 三目字典表格

	private int upTableRow = -1;// upTable获得当前选中行
	/**
	 * 编号规则类别工具
	 */
	private INSSYSRuleTXTool ruleSysTool;

	/**
	 * 树的数据放入datastore用于对树的数据管理
	 */
	private TDataStore treeDataStore = new TDataStore();

	public void onInit() { // 初始化程序
		super.onInit();
		onInitTree();
		getInitParm();
		// 初始化结点
		onInitNode();
	}

	/**
	 * 获得初始化数据
	 */
	private void getInitParm() {
		sysFeeParm = new TParm(TJDODBTool.getInstance().select(
				getSQL(Operator.getRegion())));
		upTable = (TTable) this.callFunction("UI|upTable|getThis");
		ruleTable = (TTable) this.callFunction("UI|ruleTable|getThis");
		// upTable的侦听事件
		callFunction("UI|upTable|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// 调用医嘱弹出框
		callFunction("UI|TXT_ORDER_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\sys\\SYSFeePopupToINS.x");
		// textfield接受回传值
		callFunction("UI|TXT_ORDER_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		// 调用三目字典弹出框
		callFunction("UI|INS_CODE|setPopupMenuParameter", "aaa",
				"%ROOT%\\config\\ins\\INSFeePopup.x");
		// textfield接受回传值
		callFunction("UI|INS_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popInsReturn");
		// onRuleQuery();
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
		treeRoot.setText("诊查分类");
		// 给根节点赋tag
		treeRoot.setType("Root");
		// 设置根节点的id
		treeRoot.setID("");
		// 清空所有节点的内容
		treeRoot.removeAllChildren();
		// 调用树点初始化方法
		callMessage("UI|TREE|update");
		// 给tree添加监听事件
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
	}

	/**
	 * 初始化树的结点
	 */

	public void onInitNode() {
		// 给dataStore赋值
		treeDataStore
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_FEE_RULE'");
		// 如果从dataStore中拿到的数据小于0
		if (treeDataStore.retrieve() <= 0)
			return;
		// 过滤数据,是编码规则中的科室数据
		ruleSysTool = new INSSYSRuleTXTool("SYS_FEE_RULE");
		if (ruleSysTool.isLoad()) { // 给树篡节点参数:datastore，节点代码,节点显示文字,,节点排序
			TTreeNode node[] = ruleSysTool.getTreeNode(treeDataStore,sysFeeParm,
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
	 * 查找目前正在启用的项目列表
	 * 
	 * @return String
	 */
	private String getSQL(String regionCode) {
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		String sql = "";
		// 配过滤条件
		sql = " SELECT * FROM SYS_FEE WHERE CHARGE_HOSP_CODE NOT IN ('2E0','2E1','2E2','M01','N01','L01','Z01')  "
				+ region + " ORDER BY ORDER_CODE";
		return sql;
	}
	/**
	 * 单击树
	 * 
	 * @param parm
	 *            Object
	 */
	public void onTreeClicked(Object parm) {
		// 清空
		// onClear();
		// 得到点击树的节点对象
		TTreeNode node = (TTreeNode) parm;
		if (node == null)
			return;
		// 得到table对象
		// TTable table = (TTable) this.callFunction("UI|upTable|getThis");
		// // table接收所有改变值
		// table.acceptText();
		// 判断点击的是否是树的根结点
		if (node.getType().equals("Root")) {
			// 如果是树的根接点table上不显示数据
			orderCode = null;
		} else { // 如果点的不是根结点
			// 拿到当前选中的节点的id值
			orderCode = node.getID();
			// System.out.println("orderCode::" + orderCode);
			// 拿到查询TDS的SQL语句（通过上一层的ID去like表中的orderCode）
			// ======pangben modify 20110427 start 添加区域参数
			// String sql = getSQL(id,Operator.getRegion());
			// ======pangben modify 20110427 stop
			// 初始化table和TDS
			// initTblAndTDS(sql);

		}
	}

	/**
	 * 添加修改医嘱
	 */
	public void addUpdateSysFee() {

		if (null == orderCode) {
			this.messageBox("请选择一条数据");
			return;
		}
		// 得到table对象

		if (null != upTable.getParmValue()) {
			for (int i = 0; i < upTable.getParmValue().getCount(); i++) {
				if (upTable.getParmValue().getValue("ORDER_CODE", i).equals(
						orderCode)) {
					this.messageBox("已经存在此医嘱信息");
					return;
				}
			}
		}
		// 获得医嘱信息
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.NHI_CODE_I,A.NHI_CODE_O,A.NHI_CODE_E," +
				"A.NHI_FEE_DESC,A.NHI_PRICE,A.INSPAY_TYPE,A.OWN_PRICE,"
				+ "B.DOSE_CODE,A.SPECIFICATION,A.HYGIENE_TRADE_CODE,A.MAN_CODE FROM SYS_FEE A ,PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND A.ORDER_CODE='"
				+ orderCode + "'";
		TParm sysParm = new TParm(TJDODBTool.getInstance().select(sql));
		if (null == upTable.getParmValue()) {
			upTable.setParmValue(new TParm());
		}
		TParm updateParm = upTable.getParmValue();
		updateParm.addRowData(sysParm, 0);// 添加医嘱
		upTable.setParmValue(updateParm);
	}

	/**
	 * 移除要修改的医嘱
	 */
	public void removeUpdateSysFee() {
		int row = upTable.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择要移除的数据");
			return;
		}
		TParm updateParm = upTable.getParmValue();
		updateParm.removeRow(row);
		upTable.setParmValue(updateParm);
	}

	/**
	 * table 点击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicked(int row) {
		upTableRow = row;
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (this.emptyTextCheck("TXT_ORDER_CODE")) {
			return;
		}

	}

	/**
	 * 获得返回值
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		TParm parmReturn = (TParm) obj;
		// 查询集合医嘱添加集合医嘱细项
		// 查询细相数据，填充细相
		orderCode = parmReturn.getValue("ORDER_CODE");
		this.setValue("TXT_ORDER_CODE", parmReturn.getValue("ORDER_CODE"));
	}

	/**
	 * 获得返回值
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popInsReturn(String tag, Object obj) {
		TParm parmReturn = (TParm) obj;
		// 查询集合医嘱添加集合医嘱细项
		// 查询细相数据，填充细相
		this.setValue("INS_CODE", parmReturn.getValue("ORDER_CODE"));
	}

	/***
	 * 查询三目字典数据
	 */
	public void onRuleQuery() {
		TParm parm = new TParm();
		if (this.getValue("INS_CODE").toString().length() > 0) {
			parm.setData("XMBM", this.getValue("INS_CODE"));
		}
		TParm ruleParm = INSRuleTXTool.getInstance().selectINSRule(parm);
		if (ruleParm.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			return;
		}
		ruleTable.setParmValue(ruleParm);
		// ( (TTable) getComponent("ruleTable")).setParmValue(ruleParm);
	}

	/**
	 * 住院医保修改
	 */
	public void nhiSaveI() {
		tempSave("NHI_CODE_I");
	}

	/**
	 * 门诊医保修改
	 */
	public void nhiSaveO() {
		tempSave("NHI_CODE_O");
	}

	/**
	 * 急诊医保修改
	 */
	public void nhiSaveE() {
		tempSave("NHI_CODE_E");
	}

	private void tempSave(String nhiCode) {
		int ruleRow = ruleTable.getSelectedRow();
		int upRow = upTable.getSelectedRow();
		if (ruleRow < 0) {
			this.messageBox("请选择三目字典的数据");
			return;
		}
		if (upRow < 0) {
			this.messageBox("请选择要修改的数据");
			return;
		}
		TParm upParm = upTable.getParmValue();// 获得修改的数据
		TParm ruleParm = ruleTable.getParmValue();// 获得三目字典数据
		upParm.setData(nhiCode, upRow, ruleParm.getValue("SFXMBM", ruleRow));
		upParm.setData("NHI_FEE_DESC", upRow, ruleParm
				.getValue("XMMC", ruleRow)); // 医保名称
		upParm.setData("NHI_PRICE", upRow, ruleParm.getValue("ZGXJ", ruleRow)); // 医保价格
		upParm
				.setData("INSPAY_TYPE", upRow, ruleParm.getValue("XMLB",
						ruleRow)); // 医保类别
		upTable.setParmValue(upParm);
		upTable.setSelectedRow(upRow);
	}

	/**
	 * 移除要修改的医保号码
	 */
	public void removeInsCode() {
		int upRow = upTable.getSelectedRow();
		if (upRow < 0) {
			this.messageBox("请选择要修改的数据");
			return;
		}
		removeInsCode("NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;NHI_FEE_DESC;NHI_PRICE;INSPAY_TYPE", upRow);
	}

	/**
	 * 移除要修改的医保号码
	 * 
	 * @param code
	 * @param upRow
	 */
	private void removeInsCode(String  code, int upRow) {
		String [] name=code.split(";");
		TParm upParm = upTable.getParmValue();// 获得修改的数据
		for (int i = 0; i < name.length; i++) {
			upParm.setData(name[i], upRow, "");
		}
		upTable.setParmValue(upParm);
	}
	/**
	 * 保存要修改的数据
	 */
	public void onSave(){
		if (upTable.getParmValue().getCount()<=0) {
			this.messageBox("没有要修改的数据");
			return ;
		}
		TParm parm =upTable.getParmValue();
		TParm result = TIOM_AppServer.executeAction("action.ins.INSTJAction",
		"updateINSToSysFee", parm);
		if (result.getErrCode()<0) {
			this.messageBox("E0005");//执行失败
		}else{
			this.messageBox("P0005");//执行成功
			onClear();
		}	
	}
	/**
	 * 清空方法
	 */
	public void onClear(){
		upTable.removeRowAll();
		ruleTable.removeRowAll();
		orderCode=null;
		this.clearValue("TXT_ORDER_CODE;INS_CODE");
	}
}
