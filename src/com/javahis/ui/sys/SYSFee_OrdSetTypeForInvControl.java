package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TDSObject;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import java.awt.Component;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import java.sql.Timestamp;
import com.dongyang.jdo.TJDODBTool;
import jdo.sys.Operator;
import com.dongyang.util.StringTool;
import jdo.sys.SYSHzpyTool;
import com.javahis.util.JavaHisDebug;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title: 手术模版字典界面
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: JAVAHIS 1.0 (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 */
public class SYSFee_OrdSetTypeForInvControl extends TControl {

	public SYSFee_OrdSetTypeForInvControl() {
	}

	/**
	 * 取得数据库访问类
	 * 
	 * @return TJDODBTool ========pangben modify 20110428
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 大对象
	 */
	private TDSObject bigObject = new TDSObject();
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
	 * 表格当前选中的行号
	 */
	int selRow = -1;
	/**
	 * 当前选中的项目代码(SYS_FEE_HISTORY中：ORDER_CODE/SYS_ORDERSETDETAI中：ORDERSET_CODE)
	 */
	String packCode = "";

	TDS sysFeeOrdPackM = new TDS();
	TDS sysFeeOrdPackD = new TDS();
	/**
	 * 所有控件的名字
	 */
	private String controlName = "PACK_DESC;ENG_NAME;DESCRIPTION;PY1;FIT_DEPT";

	/**
	 * 界面的控件
	 */
	// 树
	TTree tree;
	// 主表
	TTable upTable, downTable;
	TTextFormat FIT_DEPT;
	TTextField PACK_CODE;
	TTextField PACK_DESC;
	TTextField ENG_NAME;
	TTextField PY1;
	TTextField DESCRIPTION;
	TComboBox CLASS_CODE;
	public void onInit() { // 初始化程序
		super.onInit();
		myInitControler();

		// 初始化树
		onInitTree();
		// 给tree添加监听事件
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
		// 初始化结点
		onInitNode();
		callFunction("UI|downTable|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableCheckBoxClicked");
		canSave();
		// ========pangben modify 20110427 start 权限添加
		// 初始化院区
		setValue("REGION_CODE", Operator.getRegion());
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
		// ===========pangben modify 20110427 stop

	}

	private void canSave() {
		Object obj = this.getParameter();
		if (obj == null)
			return;
		if (obj.equals("QUERY"))
			callFunction("UI|save|setEnabled", false);
	}

	/**
	 * 表格(TABLE)复选框改变事件
	 * 
	 * @param obj
	 */
	public void onTableCheckBoxClicked(Object obj) {
		TTable table = (TTable) obj;
		table.removeRow(table.getSelectedRow());
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
		treeRoot.setText("套餐模板分类");
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
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_ORDERSET_TYPE'");
		// 如果从dataStore中拿到的数据小于0
		if (treeDataStore.retrieve() <= 0)
			return;
		// 过滤数据,是编码规则中的科室数据
		ruleTool = new SYSRuleTool("SYS_ORDERSET_TYPE");
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
		// 清空
		onClear();

		// 新增按钮不能用
		callFunction("UI|new|setEnabled", false);
		// 得到点击树的节点对象
		// TTreeNode node = (TTreeNode) parm;
		TTreeNode node = tree.getSelectNode();
		// TTreeNode node=tree.getSelectionNode();
		if (node == null)
			return;
		// 得到table对象
		TTable table = (TTable) this.callFunction("UI|upTable|getThis");
		// table接收所有改变值
		table.acceptText();
		// 判断点击的是否是树的根结点
		if (node.getType().equals("Root")) {
			// 如果是树的根接点table上不显示数据
			table.removeRowAll();
		} else { // 如果点的不是根结点
					// 拿到当前选中的节点的id值
			String id = node.getID();
			// 拿到查询TDS的SQL语句（通过上一层的ID去like表中的orderCode）
			// =======pangben modify 20110428 start
			String sql = getSQL(id, Operator.getRegion());
			// 初始化table和TDS
			initTblAndTDS(sql);

		}
		// 得到当前点击结点的ID
		String nowID = node.getID();

		int classify = 1;
		if (nowID.length() > 0)
			classify = ruleTool.getNumberClass(nowID) + 1;
		// 如果是最小节点,可以增加一行(使新增按钮可用)
		if (classify > ruleTool.getClassifyCurrent()) {
			this.callFunction("UI|new|setEnabled", true);
		}
	}

	/**
	 * 得到初始化TDS的SQL语句(查找目前正在启用的项目列表：不卡START_DATE/END_DATE 只卡ACTIVE_FLG为‘Y’)
	 * 
	 * @return String =========pangben modify 20110428 添加区域参数
	 */
	private String getSQL(String packCode, String regionCode) {
		// ==========pangben modify 20110428 start
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		String sql = "";
		// 配过滤条件
		if (packCode != null && packCode.length() > 0)
            sql = " SELECT * FROM SYS_ORDER_PACKM WHERE " +
                " PACK_CODE LIKE '" + packCode + "%'" +region+
                " ORDER BY PACK_CODE";
//		System.out.println("-----------"+sql);
		// ==========pangben modify 20110428 stop
		return sql;
	}

	/**
	 * 初始化上面的表格还有所有的控件数据
	 * 
	 * @param sql
	 *            String
	 */
	public void initTblAndTDS(String sql) {
		sysFeeOrdPackM.setSQL(sql);
		sysFeeOrdPackM.retrieve();
		// 如果没有数据清空表格上的数据
		if (sysFeeOrdPackM.rowCount() <= 0) {
			upTable.removeRowAll();
		}
		sysFeeOrdPackM.showDebug();

		upTable.setDataStore(sysFeeOrdPackM);
		upTable.setDSValue();
	}

	/**
	 * 首先得到所有UI的控件对象/注册相应的事件 设置
	 */
	public void myInitControler() {

		tree = (TTree) callFunction("UI|TREE|getThis");

		// 得到table控件
		upTable = (TTable) this.getComponent("upTable");
		downTable = (TTable) this.getComponent("downTable");

		FIT_DEPT = (TTextFormat) this.getComponent("FIT_DEPT");
		PACK_DESC = (TTextField) this.getComponent("PACK_DESC");
		ENG_NAME = (TTextField) this.getComponent("PACK_DESC");
		DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
		PACK_CODE = (TTextField) this.getComponent("PACK_CODE");
		PY1 = (TTextField) this.getComponent("PY1");
		CLASS_CODE =(TComboBox)this.getComponent("CLASS_CODE");

		// 给上table注册单击事件监听
		this.callFunction("UI|upTable|addEventListener", "upTable->"
				+ TTableEvent.CLICKED, this, "onUpTableClicked");
		// 给下table注册监听事件
		downTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateSYSFEE");
	
		downTable.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
		"onCreateINV");

		// 新增按钮不能用
		callFunction("UI|new|setEnabled", false);
	}

	/**
	 * 当TABLE创建编辑控件时
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	
	public void onCreateINV(Component com, int row, int column) {
		if (column != 6)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 查询检验检查细项--6
//		TParm parm = new TParm();
		TParm parmInv = new TParm();
	    textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig("%ROOT%\\config\\inv\\INVBasePopup.x"), parmInv);
       // 定义接受返回值方法
       textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
		"popReturnINV");
	}
	/**
	 * 当TABLE创建编辑控件时
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateSYSFEE(Component com, int row, int column) {

		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 查询检验检查细项--6
		TParm parm = new TParm();
		// parm.setData("RX_TYPE", 6);
		textFilter
				.setPopupMenuParameter(
						"IG",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
//		TParm parmInv = new TParm();				
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// 类型转换成TParm
		TParm result = (TParm) obj;
        int count = sysFeeOrdPackD.rowCount();
        for (int i = 0; i < count; i++) {//wanglong add 20141014
            if (sysFeeOrdPackD.getItemString(i, "ORDER_CODE").equals(result.getValue("ORDER_CODE"))) {
                this.messageBox("医嘱不能重复添加");
                downTable.acceptText();
                int selrow = downTable.getSelectedRow();
                downTable.setItem(selrow, "ORDER_CODE", "");
                return;
            }
        }
		String ordCode = result.getValue("ORDER_CODE");
		String ordDesc = result.getValue("ORDER_DESC");
		String unitcode = result.getValue("UNIT_CODE");

		// 根据返回的CODE设置table和TDS的值
		setDownTableAndTDS(ordCode, ordDesc, unitcode);

	}

	/**
	 * 物资返回值
	 * */
	public void popReturnINV(String tag, Object obj) {
		// 判断对象是否为空和是否为TParm类型
		if (obj == null && !(obj instanceof TParm)) {
			return;
		}
		// 类型转换成TParm
		TParm result = (TParm) obj;
	    int count = sysFeeOrdPackD.rowCount();
        for (int i = 0; i < count; i++) {//wanglong add 20141014
            if (sysFeeOrdPackD.getItemString(i, "INV_CODE").equals(result.getValue("INV_CODE"))) {
                this.messageBox("物资代码不能重复添加");
                downTable.acceptText();
                int selrow = downTable.getSelectedRow();
                downTable.setItem(selrow, "INV_CODE", "");
                return;
            }
        }
		String invCode = result.getValue("INV_CODE");
		String invDesc = result.getValue("INV_CHN_DESC");
		// 根据返回的CODE设置table和TDS的值
		setDownTableAndTDSForInv(invCode, invDesc);
	}
	
	
	public void setDownTableAndTDS(String code, String ordDesc, String unitcode) {

		downTable.acceptText();
		int selrow = downTable.getSelectedRow();
		downTable.setItem(selrow, "ORDER_CODE", code);
		downTable.setItem(selrow, "ORDER_DESC", ordDesc);
		downTable.setItem(selrow, "DOSAGE_UNIT", unitcode);
		// 当开完一个项目之后默认为1.0
		if (TypeTool.getDouble(downTable.getValueAt_(selrow, 5)) == 0.0) {
			downTable.setItem(selrow, "DOSAGE_QTY", 1.0);
		}
        String sql =
                "SELECT DISTINCT INV_CODE,INV_CHN_DESC AS INV_DESC FROM INV_BASE WHERE ACTIVE_FLG='Y' AND ORDER_CODE='#'"
                        .replaceFirst("#", code);
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        if (parm.getCount() > 1) {
            this.messageBox("由于物资代码数量大于1，无法自动带回");
        } else if (parm.getCount() > 0) {
            downTable.setItem(selrow, "INV_CODE", parm.getValue("INV_CODE", 0));//wanglong add 20140813
            downTable.setItem(selrow, "INV_DESC", parm.getValue("INV_DESC", 0));
        }
		// 新增一行
		// sysOrdSetDtlDS.insertRow();
		if (downTable.getSelectedRow() + 1 == downTable.getRowCount())
			downTable.addRow();

	}
	public void setDownTableAndTDSForInv(String code, String ordDesc) {

		//this.messageBox("走这个方法了吗");
		downTable.acceptText();
		int selrow = downTable.getSelectedRow();
//		Box("selrow---->"+selrow);
//		ththis.messageis.messageBox("INV_CODE---->"+code);
//		this.messageBox("INV_DESC---->"+ordDesc);
		//N_DEL;ORDER_CODE;ORDER_DESC;DOSAGE_UNIT;DOSAGE_QTY;N_TOTFEE;INV_CODE;INV_DESC
		downTable.setItem(selrow, "INV_CODE", code);
		downTable.setItem(selrow, "INV_DESC", ordDesc);
		//this.messageBox("走这个方法了吗111");
		// 当开完一个项目之后默认为1.0
		if (TypeTool.getDouble(downTable.getValueAt_(selrow, 5)) == 0.0) {
			downTable.setItem(selrow, "DOSAGE_QTY", 1.0);
		}
		//this.messageBox("走这个方法了吗222");
		// 新增一行
		// sysOrdSetDtlDS.insertRow();
		if (downTable.getSelectedRow() + 1 == downTable.getRowCount())
			downTable.addRow();
		//this.messageBox("走这个方法了吗333");
	}
	/**
	 * 单击上面的table事件
	 */
	public void onUpTableClicked(int row) {
		// 当前选中的行号
		selRow = row;
		// 清空下面控件的值
		clearCtl();
		// 得到表的parm
		TParm tableDate = ((TDS) upTable.getDataStore()).getBuffer(TDS.PRIMARY)
				.getRow(selRow);
		// 给所有的控件值
		// setValueForDownCtl(tableDate, row);
		setValueForDownCtl(tableDate);
		//this.messageBox("tableDate--->"+tableDate);
		// 得到当前项目代码(SYS_FEE_HISTORY中：ORDER_CODE/SYS_ORDERSETDETAI中：ORDERSET_CODE)
		packCode = PACK_CODE.getValue();
		// 初始化下面的table
		initDownTable(packCode);
	}

	/**
	 * 初始化下面的table
	 * 
	 * @param ordersetCode
	 *            String
	 */
	public void initDownTable(String packCode) {

		downTable.acceptText();
		String sqlForDtl = "SELECT * FROM SYS_ORDER_PACKD WHERE PACK_CODE = '"
				+ packCode + "'";
		sysFeeOrdPackD.setSQL(sqlForDtl);
		sysFeeOrdPackD.retrieve();
		sysFeeOrdPackD.showDebug();
		// 创建监听者
		sysOdrPackDObserver obser = new sysOdrPackDObserver();
		// 把监听者设置到某个DS中
		sysFeeOrdPackD.addObserver(obser);

		// 如果没有数据清空表格上的数据
		if (sysFeeOrdPackD.rowCount() <= 0) {
			downTable.removeRowAll();
		}

		sysFeeOrdPackD.insertRow();

		downTable.setDataStore(sysFeeOrdPackD);
		downTable.setDSValue();
	}

	/**
	 * 清空除了table以外的控件的值
	 */
	public void clearCtl() {

		this.clearValue(controlName + ";TRANS_HOSP_CODE;ORDER_CODE;TOT_FEE;CLASS_CODE");
		// ====pangben modify 20110427 start
		this.setValue("REGION_CODE", Operator.getRegion());
		// ====pangben modify 20110427 stop
	}

	/**
	 * 清空操作
	 */
	public void onClear() {
		clearCtl();
		//upTable.removeRowAll();
		downTable.removeRowAll();
	}

	/**
	 * 新建
	 */
	public void onNew() {

		clearCtl();
		cerateNewDate();
		// 恢复编辑状态
		setEnabledForCtl(true);
		String nowpackCode = PACK_CODE.getValue();
		// 初始化下面的table
		initDownTable(nowpackCode);

	}

	/**
	 * 保存
	 */
	public boolean onSave() {

		// 拿到当前选中行的数据（要更改和新建的行）
		selRow = upTable.getSelectedRow();
		// 取table数据
		TDataStore dataStore = upTable.getDataStore();
		int selRow = upTable.getSelectedRow();
		setDataInTDS(dataStore, selRow);
		// /用大对象保存，形成一个事务
		bigObject.addDS("SYS_ORDER_PACKM", sysFeeOrdPackM);
		getdownTableDS();
		bigObject.addDS("SYS_ORDER_PACKD", sysFeeOrdPackD);

		if (!bigObject.update()) {
			messageBox("E0001");
			return false;
		}
		messageBox("P0001");
		return true;

	}

	public void getdownTableDS() {

		// 当前数据库时间
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		int detailCount = sysFeeOrdPackD.rowCount() - 1;
		String packCode = PACK_CODE.getValue();
		for (int i = 0; i < detailCount; i++) {
			sysFeeOrdPackD.setItem(i, "PACK_CODE", packCode);
			sysFeeOrdPackD.setItem(i, "OPT_USER", Operator.getID());
			sysFeeOrdPackD.setItem(i, "OPT_DATE", now);
			sysFeeOrdPackD.setItem(i, "OPT_TERM", Operator.getIP());

		}
		// 删除最后一行，因为自动插入的那行没有数据
		sysFeeOrdPackD.deleteRow(detailCount);
	}

	/**
	 * 收集界面上的值保存使用
	 * 
	 * @param dataStore
	 *            TDataStore
	 */
	public void setDataInTDS(TDataStore dataStore, int row) {
		// 当前数据库时间
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// 保存的数据
		dataStore.setItem(row, "OPT_USER", Operator.getID());
		dataStore.setItem(row, "OPT_DATE", date);
		dataStore.setItem(row, "OPT_TERM", Operator.getIP());

		dataStore.setItem(row, "PACK_CODE", PACK_CODE.getValue());
		dataStore.setItem(row, "PY1", PY1.getValue());
		dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());
		dataStore.setItem(row, "FIT_DEPT", FIT_DEPT.getValue());
		dataStore.setItem(row, "PACK_DESC", PACK_DESC.getValue());
		dataStore.setItem(row, "CLASS_CODE", CLASS_CODE.getValue());		
		dataStore.setItem(row, "ENG_NAME", ENG_NAME.getValue());
		// =============pangben modify 20110427 start
		dataStore.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
		// =============pangben modify 20110427 stop

	}

	/**
	 * 当新建的时候自动生成编码号的主方法
	 */
	public void cerateNewDate() {
		String newCode = "";

		// 接收文本
		upTable.acceptText();
		// 取table数据
		// TDataStore dataStore = upTable.getDataStore();
		// 找到最大科室代码(dataStore,列名)
		// ========pangben modify 20110427 start
		// 注释去掉不需要查询表中的最大编号，通过查询数据库中的的数值显示最大编号
		// String maxCode = getMaxCode(dataStore, "ORDER_CODE");
		// System.out.println("maxCode:"+maxCode);
		// ========pangben modify 20110427 start

		// 找到选中的树节点
		TTreeNode node = tree.getSelectionNode();
		// 如果没有选中的节点
		if (node == null)
			return;
		// 得到当前ID
		String nowID = node.getID();
		int classify = 1;
		// 如果点的是树的父节点存在,得到当前编码规则
		if (nowID.length() > 0)
			classify = ruleTool.getNumberClass(nowID) + 1;
		// 如果是最小节点,可以增加一行
		if (classify > ruleTool.getClassifyCurrent()) {
			// 得到默认的自动添加的医嘱代码
			// ===pangben modify 20110427 start
			// ============查找此编号规则中编号最大值
			String sql = "SELECT MAX(PACK_CODE) AS PACK_CODE FROM SYS_ORDER_PACKM WHERE PACK_CODE LIKE '"
					+ nowID + "%'";
			TParm parm = new TParm(getDBTool().select(sql));
			String maxCode = parm.getValue("PACK_CODE", 0);
			// ===pangben modify 20110427 start

			String no = ruleTool.getNewCode(maxCode, classify);
			newCode = nowID + no;
			// 得到新添加的table数据行号(相当于TD中的insertRow()方法)
			int row = upTable.addRow();
			// 设置当前选中行为添加的行
			upTable.setSelectedRow(row);
			// 给科室代码添加默认值
			upTable.setItem(row, "PACK_CODE", newCode);
			// 默认科室名称
			upTable.setItem(row, "PACK_DESC", "(新建名称)");
			// 默认科室简称
			upTable.setItem(row, "DESCRIPTION", null);
			// 默认科室
			upTable.setItem(row, "FIT_DEPT", null);

		}
		// 把自动生成的orderCode设置到ORDER_CODE上
		PACK_CODE.setText(newCode);
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
	 * table双击选中树
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubleCleck() {

		upTable.acceptText();
		int row = upTable.getSelectedRow();
		String value = upTable.getItemString(row, 0);
		// 得到上层编码
		String partentID = ruleTool.getNumberParent(value);
		TTreeNode node = treeRoot.findNodeForID(partentID);
		if (node == null)
			return;
		// 得到界面上的树对象
		TTree tree = (TTree) callMessage("UI|TREE|getThis");
		// 设置树的选中节点
		tree.setSelectNode(node);
		tree.update();
		// 调用查询事件
		onCleckClassifyNode(partentID, upTable);
		// table中设置选中行
		int count = upTable.getRowCount(); // table的行数
		for (int i = 0; i < count; i++) {
			// 拿到物资代码
			String invCode = upTable.getItemString(i, 0);
			if (value.equals(invCode)) {
				// 设置选中行
				upTable.setSelectedRow(i);
				return;
			}
		}
	}

	/**
	 * 选中对应树节点的事件
	 * 
	 * @param parentID
	 *            String
	 * @param table
	 *            TTable
	 */
	public void onCleckClassifyNode(String parentID, TTable table) {
		// table中的datastore中查询数据sql
		table.setSQL("select * from SYS_ORDER_PACKM where PACK_CODE like '"
				+ parentID + "%'");
		// 查找数据
		table.retrieve();
		// 放置数据到table中
		table.setDSValue();
		// 设置新增按钮
		callFunction("UI|new|setEnabled", true);

	}

	/**
	 * 根据table上的某一行数据给下面的控件初始化值
	 * 
	 * @param date
	 *            TParm
	 */
	public void setValueForDownCtl(TParm date) {

		clearCtl();
		this.setValue("PACK_CODE", date.getValue("PACK_CODE"));
		this.setValue("PACK_DESC", date.getValue("PACK_DESC"));
		this.setValue("ENG_NAME", date.getValue("ENG_NAME"));
		this.setValue("PY1", date.getValue("PY1"));
		this.setValue("FIT_DEPT", date.getValue("FIT_DEPT"));
		this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));
		this.setValue("CLASS_CODE", date.getValue("CLASS_CODE"));
		// =====pangben modify 20110427 start
		this.setValue("REGION_CODE", date.getValue("REGION_CODE"));
	}

	/**
	 * 得到名称拼音
	 */
	public void onPY1() {
		String orderDesc = PACK_DESC.getText();
		String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
		PY1.setText(orderPy);
		DESCRIPTION.grabFocus();
	}

	/**
	 * 设置所有的控件是否可用（历史数据不可以修改）
	 * 
	 * @param flg
	 *            boolean
	 */
	public void setEnabledForCtl(boolean flg) {
		String tag[] = controlName.split(";");
		int count = tag.length;
		for (int i = 0; i < count; i++) {
			this.callFunction("UI|" + tag[i] + "|setEnabled", flg);
		}

	}

	// 测试用例
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		JavaHisDebug.TBuilder();
		// JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_ORDSETTYPE.x");
	}

}
