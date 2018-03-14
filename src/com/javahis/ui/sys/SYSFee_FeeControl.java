package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTreeNode;
import jdo.sys.SYSRuleTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TDS;
import com.dongyang.ui.TTree;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.data.TParm;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;

import psyg.graphic.SysObj;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;
import jdo.sys.Operator;
import jdo.sys.SYSHzpyTool;
import com.javahis.util.JavaHisDebug;
import com.dongyang.jdo.TDSObject;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.StringUtil;
import com.dongyang.manager.TIOM_Database;
import jdo.sys.SYSRegionTool;

/**
 * <p>
 * Title:项目收费字典档
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author ZangJH
 * @version 1.0
 */
public class SYSFee_FeeControl extends TControl {

	/**
	 * 大对象
	 */
	private TDSObject bigObject = new TDSObject();

	/**
	 * 监控三个费用值，如果有改变在SYS_FEE中生成新的一条数据
	 */
	private double oldOwnPrice = 0.0;
	private double oldNhiPrice = 0.0;
	private double oldGovPrice = 0.0;
	private String orderCode = "";
	
	private String nhiCodeI = "";
	private String nhiCodeO = "";
	private String nhiCodeE = "";
	//$$ add by lx 表格查询的SQL
	private String querySQL="";

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
	 * SYS_FEE_HISTORY表的TDS
	 */
	TDS sysFeeHisDS = new TDS();
	/**
	 * SYS_FEE表的TDS
	 */
	TDS sysFeeDS = new TDS();

	/**
	 * 表格当前选中的行号
	 */
	int selRow = -1;

	/**
	 * 所有控件的名字
	 */
	private String controlName = "LET_KEYIN_FLG;OPD_FIT_FLG;EMG_FIT_FLG;IPD_FIT_FLG;"
			+ "HRM_FIT_FLG;DR_ORDER_FLG;TRANS_OUT_FLG;EXEC_ORDER_FLG;START_DATE;SYNC_NIS_FLG;"//2017.04.13 zhanglei 同步NIS的状态
			+ "END_DATE;ORDER_DESC;PY1;NHI_FEE_DESC;"
			+ "TRADE_ENG_DESC;ALIAS_DESC;SPECIFICATION;ALIAS_PYCODE;DESCRIPTION;"
			+ "HYGIENE_TRADE_CODE;NHI_CODE_I;NHI_CODE_O;NHI_CODE_E;HABITAT_TYPE;"
			+ "UNIT_CODE;MR_CODE;CHARGE_HOSP_CODE;ORDER_CAT1_CODE;LCS_CLASS_CODE;"
			+ "EXEC_DEPT_CODE;INSPAY_TYPE;OWN_PRICE;OWN_PRICE2;"
			+ "OWN_PRICE3;ADDPAY_RATE;ADDPAY_AMT;ACTION_CODE;EXEC_PRG;IS_REMARK;IN_OPFLG;"//add by liuyalin 20170524
			+ "MAN_CODE;SUPPLIES_TYPE;OTH_TYPE;ORD_SUPERVISION";//shibl add 医疗监管种类

	/**
	 * 界面的控件
	 */
	// 树
	TTree tree;
	// 主表
	TTable upTable;

	// 全部/启用/停用标记
	TRadioButton ALL;
	TRadioButton ACTIVE_Y;
	TRadioButton ACTIVE_N;

	// 启用注记
	TCheckBox ACTIVE_FLG;
	// 允许手动记价注记
	TCheckBox LET_KEYIN_FLG;
	//2017.04.13 zhanglei 同步NIS标记
	TCheckBox SYNC_NIS_FLG;

	// 门,急,住,健,经医适用标记
	TCheckBox OPD_FIT_FLG;
	TCheckBox EMG_FIT_FLG;
	TCheckBox IPD_FIT_FLG;
	TCheckBox HRM_FIT_FLG;
	TCheckBox DR_ORDER_FLG;
	TCheckBox IN_OPFLG;//介入医嘱注记 add by liuyalin 20170524
	// 转出注记
	TCheckBox TRANS_OUT_FLG;
	TCheckBox EXEC_ORDER_FLG;

	TTextFormat START_DATE;
	TTextFormat END_DATE;
	TTextFormat ORD_SUPERVISION;
	
	TTextField ACTION_CODE;
	TTextField EXEC_PRG;

	TTextField QUERY;
	TTextField ORDER_CODE;
	TTextField ORDER_DESC;
	TTextField PY1;
	TTextField NHI_FEE_DESC;
	TTextField TRADE_ENG_DESC;
	TTextField ALIAS_DESC;
	TTextField SPECIFICATION;
	TTextField ALIAS_PYCODE;
	TTextField DESCRIPTION;
	TTextField HYGIENE_TRADE_CODE;
	TTextField NHI_CODE_I;
	TTextField NHI_CODE_O;
	TTextField NHI_CODE_E;

	TComboBox HABITAT_TYPE;
	TTextFormat UNIT_CODE;
	TTextFormat MR_CODE;
	TTextFormat CHARGE_HOSP_CODE;
	TTextFormat ORDER_CAT1_CODE;
	TComboBox LCS_CLASS_CODE;
	TComboBox TRANS_HOSP_CODE;
	TTextFormat EXEC_DEPT_CODE;
	TComboBox INSPAY_TYPE;

	TNumberTextField OWN_PRICE;
	TNumberTextField NHI_PRICE;
	TNumberTextField GOV_PRICE;
	TNumberTextField ADDPAY_RATE;
	TNumberTextField ADDPAY_AMT;

	/**
	 * 初始化
	 */
	public SYSFee_FeeControl() {
	}

	public void onInit() { // 初始化程序
		super.onInit();
		myInitControler();

		// 初始化树
		onInitTree();
		// 给tree添加监听事件
		addEventListener("TREE->" + TTreeEvent.CLICKED, "onTreeClicked");
		// 初始化结点
		onInitNode();
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
	 * 初始化树
	 */
	public void onInitTree() {
		// 得到树根
		treeRoot = (TTreeNode) callMessage("UI|TREE|getRoot");
		if (treeRoot == null)
			return;
		// 给根节点添加文字显示
		treeRoot.setText("诊疗项目收费分类");
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
				.setSQL("SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='SYS_FEE_RULE'");
		// 如果从dataStore中拿到的数据小于0
		if (treeDataStore.retrieve() <= 0)
			return;
		// 过滤数据,是编码规则中的科室数据
		ruleTool = new SYSRuleTool("SYS_FEE_RULE");
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
			// ======pangben modify 20110427 添加参数
			String sql = getSQL(id, Operator.getRegion());
			//$$===add by lx 2013/01/06====$$//
			querySQL=sql;
			// 初始化table和TDS
			initTblAndTDS(sql);

		}
		// 给table数据加排序条件
		// table.setSort("ORDER_CODE");
		// table排序后重新赋值
		// table.sort();
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
	 * @return String ===============pangben modify 20110427 添加区域参数
	 */
	private String getSQL(String orderCode, String regionCode) {
		String active = "";

		if (ACTIVE_Y.isSelected()) { // 启用
			active = " AND ACTIVE_FLG='Y'";
			setEnabledForCtl(true);
		} else if (ACTIVE_N.isSelected()) { // 停用
			active = " AND ACTIVE_FLG='N'";
			setEnabledForCtl(false);
		} else { // 全部
			setEnabledForCtl(false);
		}
		// =========pangben modify 20110427 start
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		// =========pangben modify 20110427 stop
		String sql = "";
		// 配过滤条件
		if (orderCode != null && orderCode.length() > 0)
			sql = " SELECT * " + " FROM SYS_FEE_HISTORY" + " WHERE "
					+ " ORDER_CODE like '" + orderCode + "%'" + active + region
					+ " ORDER BY ORDER_CODE";

		return sql;
	}

	/**
	 * 初始化上面的表格还有所有的控件数据
	 * 
	 * @param sql
	 *            String
	 */
	public void initTblAndTDS(String sql) {
		sysFeeHisDS.setSQL(sql);
		sysFeeHisDS.retrieve();
		// 如果没有数据清空表格上的数据
		if (sysFeeHisDS.rowCount() <= 0) {
			upTable.removeRowAll();
		}

		upTable.setDataStore(sysFeeHisDS);
		upTable.setDSValue();

	}

	/**
	 * 首先得到所有UI的控件对象/注册相应的事件 设置
	 */
	public void myInitControler() {

		tree = (TTree) callFunction("UI|TREE|getThis");

		// 得到table控件
		upTable = (TTable) this.getComponent("upTable");

		ALL = (TRadioButton) this.getComponent("ALL");
		ACTIVE_Y = (TRadioButton) this.getComponent("ACTIVE_Y");
		ACTIVE_N = (TRadioButton) this.getComponent("ACTIVE_N");
		
		//2017.04.13 zhanglei 同步NIS标记
		SYNC_NIS_FLG = (TCheckBox) this.getComponent("SYNC_NIS_FLG");
		//this.messageBox("首先得到所有UI的控件对象/注册相应的事件 设置：：" + SYNC_NIS_FLG);


		ACTIVE_FLG = (TCheckBox) this.getComponent("ACTIVE_FLG");
		LET_KEYIN_FLG = (TCheckBox) this.getComponent("LET_KEYIN_FLG");
		OPD_FIT_FLG = (TCheckBox) this.getComponent("OPD_FIT_FLG");
		EMG_FIT_FLG = (TCheckBox) this.getComponent("EMG_FIT_FLG");
		IPD_FIT_FLG = (TCheckBox) this.getComponent("IPD_FIT_FLG");
		HRM_FIT_FLG = (TCheckBox) this.getComponent("HRM_FIT_FLG");
		DR_ORDER_FLG = (TCheckBox) this.getComponent("DR_ORDER_FLG");
		 IN_OPFLG = (TCheckBox) this.getComponent("IN_OPFLG");//add by liuyalin 20170524
		TRANS_OUT_FLG = (TCheckBox) this.getComponent("TRANS_OUT_FLG");
		EXEC_ORDER_FLG = (TCheckBox) this.getComponent("EXEC_ORDER_FLG");

		START_DATE = (TTextFormat) this.getComponent("START_DATE");
		END_DATE = (TTextFormat) this.getComponent("END_DATE");
		ORD_SUPERVISION=(TTextFormat)this.getComponent("ORD_SUPERVISION");
		ACTION_CODE = (TTextField) this.getComponent("ACTION_CODE");
		EXEC_PRG = (TTextField) this.getComponent("EXEC_PRG");

		QUERY = (TTextField) this.getComponent("QUERY");
		ORDER_CODE = (TTextField) this.getComponent("ORDER_CODE");
		ORDER_DESC = (TTextField) this.getComponent("ORDER_DESC");
		PY1 = (TTextField) this.getComponent("PY1");
		NHI_FEE_DESC = (TTextField) this.getComponent("NHI_FEE_DESC");
		TRADE_ENG_DESC = (TTextField) this.getComponent("TRADE_ENG_DESC");

		ALIAS_DESC = (TTextField) this.getComponent("ALIAS_DESC");
		SPECIFICATION = (TTextField) this.getComponent("SPECIFICATION");
		ALIAS_PYCODE = (TTextField) this.getComponent("ALIAS_PYCODE");
		DESCRIPTION = (TTextField) this.getComponent("DESCRIPTION");
		HYGIENE_TRADE_CODE = (TTextField) this
				.getComponent("HYGIENE_TRADE_CODE");

		NHI_CODE_I = (TTextField) this.getComponent("NHI_CODE_I");
		NHI_CODE_O = (TTextField) this.getComponent("NHI_CODE_O");
		NHI_CODE_E = (TTextField) this.getComponent("NHI_CODE_E");

		HABITAT_TYPE = (TComboBox) this.getComponent("HABITAT_TYPE");
		UNIT_CODE = (TTextFormat) this.getComponent("UNIT_CODE");
		MR_CODE = (TTextFormat) this.getComponent("MR_CODE");
		CHARGE_HOSP_CODE = (TTextFormat) this.getComponent("CHARGE_HOSP_CODE");
		ORDER_CAT1_CODE = (TTextFormat) this.getComponent("ORDER_CAT1_CODE");

		LCS_CLASS_CODE = (TComboBox) this.getComponent("LCS_CLASS_CODE");
		TRANS_HOSP_CODE = (TComboBox) this.getComponent("TRANS_HOSP_CODE");
		EXEC_DEPT_CODE = (TTextFormat) this.getComponent("EXEC_DEPT_CODE");
		INSPAY_TYPE = (TComboBox) this.getComponent("INSPAY_TYPE");

		NHI_PRICE = (TNumberTextField) this.getComponent("OWN_PRICE2");
		GOV_PRICE = (TNumberTextField) this.getComponent("OWN_PRICE3");
		OWN_PRICE = (TNumberTextField) this.getComponent("OWN_PRICE");
		ADDPAY_RATE = (TNumberTextField) this.getComponent("ADDPAY_RATE");
		ADDPAY_AMT = (TNumberTextField) this.getComponent("ADDPAY_AMT");

		// 给上下table注册单击事件监听
		this.callFunction("UI|upTable|addEventListener", "upTable->"
				+ TTableEvent.CLICKED, this, "onUpTableClicked");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "");
		// 设置弹出菜单
		QUERY.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		QUERY.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

		// 新增按钮不能用
		callFunction("UI|new|setEnabled", false);
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			ORDER_CODE.setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			ORDER_DESC.setValue(order_desc);
		// 清空查询控件
		QUERY.setValue("");
		onQuery();
	}

	public void onQuery() {

		String selCode = ORDER_CODE.getValue();
		if ("".equals(selCode)) {
			this.messageBox("请输入要查询项目的编码！");
		}

		// 拿到查询code的SQL语句
		// ===============pangben modify 20110427 start 添加region参数
		String sql = getSQL(selCode, this.getValueString("REGION_CODE"));
		// ===============pangben modify 20110427 stop
		//add by lx 2013/01/06
		querySQL=sql;
		
		// 初始化table和TDS
		initTblAndTDS(sql);
		// 当查询结果只有一条数据的时候，直接显示其详细信息
		if (upTable.getRowCount() == 1) {
			onUpTableClicked(0);
		}

	}

	/**
	 * 单击上面的table事件
	 */
	public void onUpTableClicked(int row) {
		//System.out.println("==onUpTableClicked row=="+row);
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
		// 当是显示‘全部’的时候默认不可编辑(启用状态恢复编辑/停用状态不可编辑)
		if (ALL.isSelected()) {
			boolean activeFlg = ACTIVE_FLG.isSelected();
			setEnabledForCtl(activeFlg);
		}
		// 记录三个控制生成新SYSFEE数据的关键变量(如果保存的时候orderCode不变，只有三个价格变就说明要更新，插入)
		oldOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
		oldNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
		oldGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
		nhiCodeI = NHI_CODE_I.getValue();
		nhiCodeO = NHI_CODE_O.getValue();
		nhiCodeE = NHI_CODE_E.getValue();
		orderCode = ORDER_CODE.getValue();
	}

	/**
	 * 清空除了table以外的控件的值
	 */
	public void clearCtl() {

		this.clearValue(controlName + ";ORDER_CODE;ACTIVE_FLG");
		// =========pangben modify 20110426 start
		this.setValue("REGION_CODE", Operator.getRegion());
		// =========pangben modify 20110426 stop
		// =========wangzl modify 20120810 start
		this.setValue("MAN_CODE", "");
		this.setValue("SUPPLIES_TYPE", "");
		// =========wangzl modify 20120810 stop

	}

	/**
	 * 清空操作
	 */
	public void onClear() {
		clearCtl();
		//upTable.removeRowAll();
	}

	/**
	 * 新建
	 */
	public void onNew() {
		// 当前数据库时间
		Timestamp date = TJDODBTool.getInstance().getDBTime();

		clearCtl();
		cerateNewDate();
		// 默认给当前时间
		START_DATE.setValue(date);
		END_DATE.setValue(date);
		// '允许手动记价注记'默认选择
		LET_KEYIN_FLG.setSelected(true);
		// 恢复编辑状态
		setEnabledForCtl(true);
	}

	/**
	 * 保存
	 */
	public boolean onSave() {
		double newOwnPrice = TypeTool.getDouble(OWN_PRICE.getText());
		double newNhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
		double newGovPrice = TypeTool.getDouble(GOV_PRICE.getText());
		String newnhiCodeI = NHI_CODE_I.getValue();
	    String newnhiCodeO = NHI_CODE_O.getValue();
		String newnhiCodeE = NHI_CODE_E.getValue();
		String nowOrdCode = ORDER_CODE.getValue();
		if (onSaveCheck())
			return false;
		// 判断时间
		String stDate = START_DATE.getText();
		String endDate = END_DATE.getText();
		Timestamp start = (Timestamp) StringTool.getTimestamp(stDate,
				"yyyy/MM/dd HH:mm:ss");
		Timestamp end = (Timestamp) StringTool.getTimestamp(endDate,
				"yyyy/MM/dd HH:mm:ss");
		if (start.getTime() > end.getTime()) {
			this.messageBox("起效日期不可以大于失效日期！");
			return false;
		}

		// 拿到当前选中行的数据（要更改和新建的行）
		selRow = upTable.getSelectedRow();
		// 取table数据
		TDataStore dataStore = upTable.getDataStore();
		// 当拿到的数据位一行，则默认保存就是该行——0
		if (selRow == -1 && dataStore.rowCount() == 1)
			selRow = 0;
		// 如果在代码没有改变的情况下改变了三个价格任一都要在重新生成一条
		if (orderCode.equals(nowOrdCode)
				&& ((newOwnPrice != oldOwnPrice)
						|| (newNhiPrice != oldNhiPrice) 
						|| (newGovPrice != oldGovPrice))
						|| (newnhiCodeI != nhiCodeI)
						|| (newnhiCodeO != nhiCodeI)
						|| (newnhiCodeE != nhiCodeI)) {
			//add by lx 
			//$$ add by lx 2012/01/06 没选择要修改的医嘱
			if(selRow==-1){
				this.messageBox("请选择需要修改的医嘱！");
				return false;
			}
			
			// 需要改变日期（true）
			setDataInTDS(dataStore, selRow, true);
			// 插入一行新的数据，拿到行号
			int insertRowNumber = dataStore.insertRow();
			setDataInTDS(dataStore, insertRowNumber, true);
		} else {
			// 从界面上拿到数据，放到TDS中，用于更新保存
			setDataInTDS(dataStore, selRow, false);

		}
		// 增加保存SYS_FEE的TDS
		if (setDataInSysFeeTds())
			bigObject.addDS("SYS_FEE", sysFeeDS);

		bigObject.addDS("SYS_FEE_HISTORY", (TDS) dataStore);
		//test
		//bigObject.showDebugAttribute();
		
		if (!bigObject.update()) {
			messageBox("E0001");
			return false;
		}		
		 
		//add by lx 2013/01/06 刷新一下医嘱表格
		//int selectRow = upTable.getSelectedRow();
		//System.out.println("-----selectRow----"+selectRow);
		initTblAndTDS(querySQL);
		//System.out.println("====onsave selRow===="+selRow);		
		//upTable.setSelectedRow(selectRow);		
		//
		
		messageBox("P0001");
		
		// 前台刷新
		TIOM_Database.logTableAction("SYS_FEE");
		return true;

	}

	/**
	 * 得到SYS_FEE的TDS
	 */
	private boolean setDataInSysFeeTds() {
		String sql = "";
		String orderCode = ORDER_CODE.getValue();
		// =============pangben modify 20110427 start 添加区域参数查询
		String regionCode = this.getValueString("REGION_CODE");
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";

		sql = "SELECT * FROM SYS_FEE WHERE ORDER_CODE='" + orderCode + "'"
				+ region;
		// System.out.println("--------------");
		// ============pangben modify 20110427 stop
		sysFeeDS.setSQL(sql);
		if (sysFeeDS.retrieve() <= 0)
			return false;

		// 当前数据库时间
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// 保存的数据
		sysFeeDS.setItem(0, "OPT_USER", Operator.getID());
		sysFeeDS.setItem(0, "OPT_DATE", date);
		sysFeeDS.setItem(0, "OPT_TERM", Operator.getIP());
		sysFeeDS.setItem(0, "ORDER_CODE", ORDER_CODE.getValue());
		sysFeeDS.setItem(0, "ORDER_DESC", ORDER_DESC.getValue());
		sysFeeDS.setItem(0, "PY1", PY1.getValue());
		sysFeeDS.setItem(0, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
		sysFeeDS.setItem(0, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
		sysFeeDS.setItem(0, "ALIAS_DESC", ALIAS_DESC.getValue());
		sysFeeDS.setItem(0, "SPECIFICATION", SPECIFICATION.getValue());
		sysFeeDS.setItem(0, "DESCRIPTION", DESCRIPTION.getValue());
		sysFeeDS.setItem(0, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
		sysFeeDS.setItem(0, "HABITAT_TYPE", HABITAT_TYPE.getValue());
		sysFeeDS.setItem(0, "UNIT_CODE", UNIT_CODE.getValue());

		sysFeeDS.setItem(0, "MR_CODE", MR_CODE.getValue());
		sysFeeDS.setItem(0, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
		sysFeeDS.setItem(0, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());

		sysFeeDS
				.setItem(0, "HYGIENE_TRADE_CODE", HYGIENE_TRADE_CODE.getValue());
		sysFeeDS.setItem(0, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());
		
		//2017.04.13 zhanglei 同步NIS标记
		sysFeeDS.setItem(0, "SYNC_NIS_FLG", SYNC_NIS_FLG.getValue());
		//this.messageBox("收集界面上的值保存使用（更新保存）" + SYNC_NIS_FLG.getValue());


		sysFeeDS.setItem(0, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
		sysFeeDS.setItem(0, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
		sysFeeDS.setItem(0, "IN_OPFLG", IN_OPFLG.getValue());//add by liuyalin 20170524
		sysFeeDS.setItem(0, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
		sysFeeDS.setItem(0, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
		sysFeeDS.setItem(0, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
		sysFeeDS.setItem(0, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
		sysFeeDS.setItem(0, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

		sysFeeDS.setItem(0, "INSPAY_TYPE", INSPAY_TYPE.getValue());
		sysFeeDS.setItem(0, "ADDPAY_RATE", ADDPAY_RATE.getValue());
		sysFeeDS.setItem(0, "ADDPAY_AMT", ADDPAY_AMT.getValue());
		sysFeeDS.setItem(0, "NHI_CODE_I", NHI_CODE_I.getValue());
		sysFeeDS.setItem(0, "NHI_CODE_O", NHI_CODE_O.getValue());
		sysFeeDS.setItem(0, "NHI_CODE_E", NHI_CODE_E.getValue());
		sysFeeDS.setItem(0, "ACTION_CODE", ACTION_CODE.getValue());
		sysFeeDS.setItem(0, "EXEC_PRG", EXEC_PRG.getValue());
		sysFeeDS.setItem(0, "CAT1_TYPE", getCta1Type(""
				+ ORDER_CAT1_CODE.getValue()));
		sysFeeDS.setItem(0, "ACTIVE_FLG", getValue("ACTIVE_FLG"));
		sysFeeDS.setItem(0, "IS_REMARK", getValue("IS_REMARK"));
		// ==========pangben modify 20110427 start
		sysFeeDS.setItem(0, "REGION_CODE", getValue("REGION_CODE"));
		// ==========pangben modify 20110427 start
		// ==========wangzl modify 20110427 start
		sysFeeDS.setItem(0, "MAN_CODE", getValue("MAN_CODE"));
		sysFeeDS.setItem(0, "SUPPLIES_TYPE", getValue("SUPPLIES_TYPE"));
		sysFeeDS.setItem(0, "OTH_TYPE", getValue("OTH_TYPE"));
		// ==========wangzl modify 20110427 start
		sysFeeDS.setItem(0, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());
		return true;
	}

	/**
	 * 取得医令细分类
	 * 
	 * @param orderCat1Type
	 *            String
	 * @return String
	 */
	private String getCta1Type(String orderCat1Type) {
		String SQL = "SELECT CAT1_TYPE FROM SYS_ORDER_CAT1 WHERE ORDER_CAT1_CODE = '"
				+ orderCat1Type + "'";
		TParm parm = new TParm(getDBTool().select(SQL));
		if (parm.getErrCode() != 0)
			return "";
		return parm.getValue("CAT1_TYPE", 0);
	}

	/**
	 * 取得数据库访问类
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 收集界面上的值保存使用（更新保存）
	 * 
	 * @param dataStore
	 *            TDataStore
	 */
	public void setDataInTDS(TDataStore dataStore, int row, boolean dateFlg) {
		
		// 当前数据库时间
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// 保存的数据
		dataStore.setItem(row, "OPT_USER", Operator.getID());
		dataStore.setItem(row, "OPT_DATE", date);
		dataStore.setItem(row, "OPT_TERM", Operator.getIP());

		// 如果修改日期标记为假的时候，两个日期按界面上显示的保存
		String tempStartDate;
		String tempEndDate;
		if (!dateFlg) {
			tempStartDate = START_DATE.getText();
			String startDate = tempStartDate.substring(0, 4)
					+ tempStartDate.substring(5, 7)
					+ tempStartDate.substring(8, 10)
					+ tempStartDate.substring(11, 13)
					+ tempStartDate.substring(14, 16)
					+ tempStartDate.substring(17, 19);
			dataStore.setItem(row, "START_DATE", startDate);

			tempEndDate = END_DATE.getText();
			String endDate = tempEndDate.substring(0, 4)
					+ tempEndDate.substring(5, 7)
					+ tempEndDate.substring(8, 10)
					+ tempEndDate.substring(11, 13)
					+ tempEndDate.substring(14, 16)
					+ tempEndDate.substring(17, 19);
			dataStore.setItem(row, "END_DATE", endDate);

			// 执行标记不变
			dataStore.setItem(row, "ACTIVE_FLG", ACTIVE_FLG.getValue());
			dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
			dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
			dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());
			dataStore.setItem(row, "ACTION_CODE", ACTION_CODE.getValue());
			dataStore.setItem(row, "EXEC_PRG", EXEC_PRG.getValue());

		} else { // 如果为真START_DATE
			//System.out.println("======row======"+row);
			//System.out.println("======selRow======"+selRow);
			
			Timestamp nowTime = TJDODBTool.getInstance().getDBTime();
			if (row == selRow) { // 如果传如的行号等于被选中的行号说明是需要更新的那行
				//System.out.println("=====修改老的记录=====");
				tempEndDate = StringTool.getString(nowTime,
						"yyyy/MM/dd HH:mm:ss");
				String endDate = tempEndDate.substring(0, 4)
						+ tempEndDate.substring(5, 7)
						+ tempEndDate.substring(8, 10)
						+ tempEndDate.substring(11, 13)
						+ tempEndDate.substring(14, 16)
						+ tempEndDate.substring(17, 19);
				dataStore.setItem(row, "END_DATE", endDate);

				// 执行标记变为假
				dataStore.setItem(row, "ACTIVE_FLG", 'N');
				// 作废的那条数据记录老的价钱（其他的都是新的值）
				dataStore.setItem(row, "OWN_PRICE", oldOwnPrice);
				dataStore.setItem(row, "OWN_PRICE2", oldNhiPrice);
				dataStore.setItem(row, "OWN_PRICE3", oldGovPrice);
				dataStore.setItem(row, "ACTION_CODE", ACTION_CODE.getValue());
				dataStore.setItem(row, "EXEC_PRG", EXEC_PRG.getValue());
			} else {
				//System.out.println("=====新增的记录=====");
				tempStartDate = StringTool.getString(nowTime,
						"yyyy/MM/dd HH:mm:ss");
				String startDate = tempStartDate.substring(0, 4)
						+ tempStartDate.substring(5, 7)
						+ tempStartDate.substring(8, 10)
						+ tempStartDate.substring(11, 13)
						+ tempStartDate.substring(14, 16)
						+ tempStartDate.substring(17, 19);
				dataStore.setItem(row, "START_DATE", startDate);

				tempEndDate = END_DATE.getText();
				String endDate = tempEndDate.substring(0, 4)
						+ tempEndDate.substring(5, 7)
						+ tempEndDate.substring(8, 10)
						+ tempEndDate.substring(11, 13)
						+ tempEndDate.substring(14, 16)
						+ tempEndDate.substring(17, 19);
				//System.out.println("====新增的记录 endDate===="+endDate);
				dataStore.setItem(row, "END_DATE", endDate);

				// 执行标记变为真
				dataStore.setItem(row, "ACTIVE_FLG", 'Y');
				dataStore.setItem(row, "OWN_PRICE", OWN_PRICE.getText());
				dataStore.setItem(row, "OWN_PRICE2", NHI_PRICE.getText());
				dataStore.setItem(row, "OWN_PRICE3", GOV_PRICE.getText());
				dataStore.setItem(row, "ACTION_CODE", ACTION_CODE.getValue());
				dataStore.setItem(row, "EXEC_PRG", EXEC_PRG.getValue());
			}
		}

		dataStore.setItem(row, "ORDER_CODE", ORDER_CODE.getValue());
		dataStore.setItem(row, "ORDER_DESC", ORDER_DESC.getValue());
		dataStore.setItem(row, "PY1", PY1.getValue());
		dataStore.setItem(row, "NHI_FEE_DESC", NHI_FEE_DESC.getValue());
		dataStore.setItem(row, "TRADE_ENG_DESC", TRADE_ENG_DESC.getValue());
		dataStore.setItem(row, "ALIAS_DESC", ALIAS_DESC.getValue());
		dataStore.setItem(row, "SPECIFICATION", SPECIFICATION.getValue());
		dataStore.setItem(row, "DESCRIPTION", DESCRIPTION.getValue());
		dataStore.setItem(row, "ALIAS_PYCODE", ALIAS_PYCODE.getValue());
		dataStore.setItem(row, "HABITAT_TYPE", HABITAT_TYPE.getValue());
		dataStore.setItem(row, "UNIT_CODE", UNIT_CODE.getValue());

		dataStore.setItem(row, "MR_CODE", MR_CODE.getValue());
		dataStore.setItem(row, "CHARGE_HOSP_CODE", CHARGE_HOSP_CODE.getValue());
		dataStore.setItem(row, "ORDER_CAT1_CODE", ORDER_CAT1_CODE.getValue());

		dataStore.setItem(row, "HYGIENE_TRADE_CODE", HYGIENE_TRADE_CODE
				.getValue());
		dataStore.setItem(row, "LET_KEYIN_FLG", LET_KEYIN_FLG.getValue());
		
		//2017.04.13 zhanglei 同步NIS标记
		dataStore.setItem(row, "SYNC_NIS_FLG", SYNC_NIS_FLG.getValue());
		//this.messageBox("得到SYS_FEE的TDS" + SYNC_NIS_FLG.getValue());


		dataStore.setItem(row, "OPD_FIT_FLG", OPD_FIT_FLG.getValue());
		dataStore.setItem(row, "EMG_FIT_FLG", EMG_FIT_FLG.getValue());
		dataStore.setItem(row, "IPD_FIT_FLG", IPD_FIT_FLG.getValue());
		dataStore.setItem(row, "DR_ORDER_FLG", DR_ORDER_FLG.getValue());
		dataStore.setItem(row, "HRM_FIT_FLG", HRM_FIT_FLG.getValue());
		dataStore.setItem(row, "IN_OPFLG", IN_OPFLG.getValue());//add by liuyalin 20170524
		dataStore.setItem(row, "LCS_CLASS_CODE", LCS_CLASS_CODE.getValue());
		dataStore.setItem(row, "TRANS_OUT_FLG", TRANS_OUT_FLG.getValue());
		dataStore.setItem(row, "TRANS_HOSP_CODE", TRANS_HOSP_CODE.getValue());
		dataStore.setItem(row, "EXEC_ORDER_FLG", EXEC_ORDER_FLG.getValue());
		dataStore.setItem(row, "EXEC_DEPT_CODE", EXEC_DEPT_CODE.getValue());

		dataStore.setItem(row, "INSPAY_TYPE", INSPAY_TYPE.getValue());
		dataStore.setItem(row, "ADDPAY_RATE", ADDPAY_RATE.getValue());
		dataStore.setItem(row, "ADDPAY_AMT", ADDPAY_AMT.getValue());
		dataStore.setItem(row, "NHI_CODE_I", NHI_CODE_I.getValue());
		dataStore.setItem(row, "NHI_CODE_O", NHI_CODE_O.getValue());
		dataStore.setItem(row, "NHI_CODE_E", NHI_CODE_E.getValue());
		dataStore.setItem(row, "IS_REMARK", getValue("IS_REMARK"));
		dataStore.setItem(row, "CAT1_TYPE", getCta1Type(""
				+ getValue("ORDER_CAT1_CODE")));
		// =============pangben modify 20110426 start
		dataStore.setItem(row, "REGION_CODE", getValue("REGION_CODE"));
		// =============pangben modify 20110426 stop
		// =============wangzl modify 20120810 start
		dataStore.setItem(row, "MAN_CODE", getValue("MAN_CODE"));
		dataStore.setItem(row, "SUPPLIES_TYPE", getValue("SUPPLIES_TYPE"));
		dataStore.setItem(row, "OTH_TYPE", getValue("OTH_TYPE"));
		// =============wangzl modify 20120810 stop
		dataStore.setItem(row, "ORD_SUPERVISION", ORD_SUPERVISION.getValue());

	}

	/**
	 * 当新建的时候自动生成编码号的主方法
	 */
	public void cerateNewDate() {
		String newCode = "";

		// 接收文本
		upTable.acceptText();
		// 取table数据
		// 找到最大科室代码(dataStore,列名)
		// ========pangben modify 20110427 start
		// 注释去掉不需要查询表中的最大编号，通过查询数据库中的的数值显示最大编号
		// TDataStore dataStore = upTable.getDataStore();
		// String maxCode = getMaxCode(dataStore, "ORDER_CODE");
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
			String sql = "SELECT MAX(ORDER_CODE) AS ORDER_CODE FROM SYS_FEE_HISTORY WHERE ORDER_CODE LIKE '"
					+ nowID + "%'";
			// System.out.println("=========SQL========"+sql);
			TParm parm = new TParm(getDBTool().select(sql));
			String maxCode = parm.getValue("ORDER_CODE", 0);
			// ===pangben modify 20110427 start
			// $$=============Modified by lx 2012/08/06
			// 自定义医嘱编码为空串====================$$//
			String no = "";
			try {
				no = ruleTool.getNewCode(maxCode, classify);
				newCode = nowID + no;
			} catch (Exception e) {
				newCode = "";
			}
			// $$=============Modified by lx 2012/08/06
			// 自定义医嘱编码为空串====================$$//
			// 得到新添加的table数据行号(相当于TD中的insertRow()方法)
			int row = upTable.addRow();
			// 设置当前选中行为添加的行
			upTable.setSelectedRow(row);
			// 给科室代码添加默认值
			upTable.setItem(row, "ORDER_CODE", newCode);
			// 默认科室名称
			upTable.setItem(row, "ORDER_DESC", "(新建名称)");
			// 默认科室简称
			upTable.setItem(row, "SPECIFICATION", null);
			// 默认科室
			upTable.setItem(row, "OWN_PRICE", null);
			// 默认最小科室注记
			upTable.setItem(row, "UNIT_CODE", null);
		}
		// 把自动生成的orderCode设置到ORDER_CODE上
		ORDER_CODE.setText(newCode);
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
		String s = "";
		if (dataStore.isFilter()) {
			TParm a = dataStore.getBuffer(TDataStore.FILTER);
			int count = a.getCount();
			for (int i = 0; i < count; i++) {
				String value = a.getValue(columnName, i);
				if (StringTool.compareTo(s, value) < 0)
					s = value;
			}
		} else {
			int count = dataStore.rowCount();
			for (int i = 0; i < count; i++) {
				String value = dataStore.getItemString(i, columnName);
				if (StringTool.compareTo(s, value) < 0)
					s = value;
			}
		}
		return s;
	}

	/**
	 * 根据table上的某一行数据给下面的控件初始化值
	 * 
	 * @param date
	 *            TParm
	 */
	public void setValueForDownCtl(TParm date) {
		clearCtl();
		this.setValue("ACTIVE_FLG", date.getValue("ACTIVE_FLG"));
		Timestamp start = StringTool.getTimestamp(date.getValue("START_DATE"),
				"yyyyMMddHHmmss");
		this.setValue("START_DATE", start);
		Timestamp end = StringTool.getTimestamp(date.getValue("END_DATE"),
				"yyyyMMddHHmmss");
		this.setValue("END_DATE", end);

		this.setValue("ORDER_CODE", date.getValue("ORDER_CODE"));
		this.setValue("ORDER_DESC", date.getValue("ORDER_DESC"));
		this.setValue("PY1", date.getValue("PY1"));
		this.setValue("NHI_FEE_DESC", date.getValue("NHI_FEE_DESC"));
		this.setValue("TRADE_ENG_DESC", date.getValue("TRADE_ENG_DESC"));
		this.setValue("ALIAS_DESC", date.getValue("ALIAS_DESC"));
		this.setValue("SPECIFICATION", date.getValue("SPECIFICATION"));
		this.setValue("DESCRIPTION", date.getValue("DESCRIPTION"));
		this.setValue("HABITAT_TYPE", date.getValue("HABITAT_TYPE"));
		this.setValue("MAN_CODE", date.getValue("MAN_CODE"));
		this.setValue("ALIAS_PYCODE", date.getValue("ALIAS_PYCODE"));
		this.setValue("UNIT_CODE", date.getValue("UNIT_CODE"));

		this.setValue("MR_CODE", date.getValue("MR_CODE"));
		this.setValue("CHARGE_HOSP_CODE", date.getValue("CHARGE_HOSP_CODE"));
		this.setValue("ORDER_CAT1_CODE", date.getValue("ORDER_CAT1_CODE"));
		this.setValue("OWN_PRICE", date.getValue("OWN_PRICE"));
		this.setValue("OWN_PRICE2", date.getValue("OWN_PRICE2"));
		this.setValue("OWN_PRICE3", date.getValue("OWN_PRICE3"));
		this
				.setValue("HYGIENE_TRADE_CODE", date
						.getValue("HYGIENE_TRADE_CODE"));
		this.setValue("LET_KEYIN_FLG", date.getValue("LET_KEYIN_FLG"));
		
		//2017.04.13 zhanglei 同步NIS标记
		this.setValue("SYNC_NIS_FLG", date.getValue("SYNC_NIS_FLG"));
		//this.messageBox("根据table上的某一行数据给下面的控件初始化值" + date.getValue("SYNC_NIS_FLG"));


		this.setValue("OPD_FIT_FLG", date.getValue("OPD_FIT_FLG"));
		this.setValue("EMG_FIT_FLG", date.getValue("EMG_FIT_FLG"));
		this.setValue("IPD_FIT_FLG", date.getValue("IPD_FIT_FLG"));
		this.setValue("HRM_FIT_FLG", date.getValue("HRM_FIT_FLG"));
		this.setValue("DR_ORDER_FLG", date.getValue("DR_ORDER_FLG"));
		this.setValue("IN_OPFLG", date.getValue("IN_OPFLG"));//add by liuyalin 20170524
		this.setValue("TRANS_OUT_FLG", date.getValue("TRANS_OUT_FLG"));
		this.setValue("EXEC_ORDER_FLG", date.getValue("EXEC_ORDER_FLG"));
		this.setValue("LCS_CLASS_CODE", date.getValue("LCS_CLASS_CODE"));
		this.setValue("TRANS_HOSP_CODE", date.getValue("TRANS_HOSP_CODE"));
		this.setValue("EXEC_DEPT_CODE", date.getValue("EXEC_DEPT_CODE"));

		this.setValue("INSPAY_TYPE", date.getValue("INSPAY_TYPE"));
		this.setValue("ADDPAY_RATE", date.getValue("ADDPAY_RATE"));
		this.setValue("ADDPAY_AMT", date.getValue("ADDPAY_AMT"));
		this.setValue("NHI_CODE_I", date.getValue("NHI_CODE_I"));
		this.setValue("NHI_CODE_O", date.getValue("NHI_CODE_O"));
		this.setValue("NHI_CODE_E", date.getValue("NHI_CODE_E"));
		this.setValue("IS_REMARK", date.getValue("IS_REMARK"));
		// ===========wangzl modify 20120810 start
		String manCode = date.getValue("MAN_CODE");// 生产厂商
		if ("".equals(manCode) || manCode.equals("0")) {
			this.setValue("MAN_CODE", "");
		} else {
			this.setValue("MAN_CODE", date.getValue("MAN_CODE"));
		}
		this.setValue("SUPPLIES_TYPE", date.getValue("SUPPLIES_TYPE"));
		this.setValue("OTH_TYPE", date.getValue("OTH_TYPE"));
		// ===========wangzl modify 20120810 stop
		ACTION_CODE.setValue(date.getValue("ACTION_CODE"));
		EXEC_PRG.setValue(date.getValue("EXEC_PRG"));
		// ===========pangben modify 20110427 start
		this.setValue("REGION_CODE", date.getValue("REGION_CODE"));
		this.setValue("ORD_SUPERVISION",date.getValue("ORD_SUPERVISION"));
	}

	/**
	 * 选择TRANS_OUT_FLG标记
	 */
	public void onOutHosp() {
		String value = TRANS_OUT_FLG.getValue();
		TRANS_HOSP_CODE.setEnabled(TypeTool.getBoolean(value));
		if (!TypeTool.getBoolean(value)) {
			TRANS_HOSP_CODE.setText("");
		}
	}

	/**
	 * 选择开单即执行标记
	 */
	public void onExrOrd() {
		String value = EXEC_ORDER_FLG.getValue();
		// 不挑勾的时候
		EXEC_DEPT_CODE.setEnabled(TypeTool.getBoolean(value));
		// 清空值
		if (!TypeTool.getBoolean(value)) {
			EXEC_DEPT_CODE.setText("");
		}

	}

	/**
	 * 激发自费价事件
	 */
	public void onOwnPrice() {
		// 得到当前自费价格
		double ownPrice = TypeTool.getDouble(OWN_PRICE.getText());
		double nhiPrice = TypeTool.getDouble(NHI_PRICE.getText());
		double govPrice = TypeTool.getDouble(GOV_PRICE.getText());

		// 默认医保/政府最高价等于自费价格
		// if (nhiPrice > ownPrice || nhiPrice == 0)
		NHI_PRICE.setValue(ownPrice * 2);
		// if (govPrice < ownPrice || govPrice == 0)
		GOV_PRICE.setValue(ownPrice * 2.5);
		// 移动光标
		NHI_PRICE.grabFocus();
	}

	/**
	 * 激发医保价事件
	 */
	public void onNhiPrice() {/*
							 * //得到当前医保价格 double nhiPrice =
							 * TypeTool.getDouble(NHI_PRICE.getText()); double
							 * ownPrice =
							 * TypeTool.getDouble(OWN_PRICE.getText()); double
							 * govPrice =
							 * TypeTool.getDouble(GOV_PRICE.getText());
							 * 
							 * //如果医保价格大于自费价格 // if (ownPrice < nhiPrice ||
							 * nhiPrice == 0) OWN_PRICE.setValue(nhiPrice*0.5);
							 * //如果最高政府价格小于医保价格 // if (govPrice < nhiPrice ||
							 * govPrice == 0) GOV_PRICE.setValue(nhiPrice*1.25);
							 * //移动光标 GOV_PRICE.grabFocus();
							 */
	}

	/**
	 * 激发政府最高价事件
	 */
	public void onGovPrice() {/*
							 * //得到当前自费价格 double govPrice =
							 * TypeTool.getDouble(GOV_PRICE.getText()); double
							 * ownPrice =
							 * TypeTool.getDouble(OWN_PRICE.getText()); double
							 * nhiPrice =
							 * TypeTool.getDouble(NHI_PRICE.getText());
							 * 
							 * //如果自费价格大于政府最高价格 // if (ownPrice > govPrice ||
							 * ownPrice == 0) OWN_PRICE.setValue(govPrice*0.4);
							 * //如果医保价格大于政府最高价格 // if (nhiPrice > govPrice ||
							 * govPrice == 0) NHI_PRICE.setValue(govPrice*0.8);
							 * //移动光标 HYGIENE_TRADE_CODE.grabFocus();
							 */
	}

	/**
	 * 得到名称拼音
	 */
	public void onPY1() {
		String orderDesc = ORDER_DESC.getText();
		String orderPy = SYSHzpyTool.getInstance().charToCode(orderDesc);
		PY1.setText(orderPy);
		NHI_FEE_DESC.grabFocus();
	}

	/**
	 * 得到别名拼音
	 */
	public void onPY2() {
		String aliasDesc = ALIAS_DESC.getText();
		String aliasPy = SYSHzpyTool.getInstance().charToCode(aliasDesc);
		ALIAS_PYCODE.setText(aliasPy);
		SPECIFICATION.grabFocus();
	}

	/**
	 * 历史明细按钮
	 * 
	 * @param args
	 *            String[]
	 */
	public void onHistory() {

		String orderCode = ORDER_CODE.getText();
		if ("".equals(orderCode)) {
			this.messageBox("请选择要查看的项目！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", orderCode);
		// ==========pangben modify 20110427 start 添加区域参数
		parm.setData("REGION_CODE", this.getValueString("REGION_CODE"));
		// ==========pangben modify 20110427 stop
		// 以模态的形式打开窗口： 路径 参数
		TParm returnData = (TParm) this.openDialog(
				"%ROOT%\\config\\sys\\SYS_FEE\\SYSFEE_HISTORY.x", parm);

		if (returnData != null) {
			setValueForDownCtl(returnData);
			// 从历史中调出的数据不可以修改
			setEnabledForCtl(false);
		}
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
		ACTIVE_FLG.setEnabled(true);
	}

	public void onExe() {
		Timestamp time = TJDODBTool.getInstance().getDBTime();
		// 如果开始时间为空，则默认给当前时间
		if ("".equals(TypeTool.getString(START_DATE.getValue()))) {
			START_DATE.setValue(time);
		}
		// 决定启用的时候‘默认’把失效日期写为"9999/12/31 23:59:59"
		if (TypeTool.getBoolean(ACTIVE_FLG.getValue())) {
			END_DATE.setText("9999/12/31 23:59:59");
			START_DATE.setValue(time);  //modify chenxi 启用时更新开始时间

		} else {
			String date = StringTool.getString(time, "yyyy/MM/dd HH:mm:ss");  
			END_DATE.setText(date);
		}

		// 恢复编辑状态
		setEnabledForCtl(true);

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
		// ====pangben modify 20110427 添加区域参数
		onCleckClassifyNode(partentID, upTable, Operator.getRegion());
		// ====pangben modify 20110427 stop
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
	public void onCleckClassifyNode(String parentID, TTable table,
			String regionCode) {
		// ==========pangben modify 20110427 start 添加区域参数查询
		String region = "";
		if (null != regionCode && !"".equals(regionCode))
			region = " AND (REGION_CODE='" + regionCode
					+ "' OR REGION_CODE IS NULL OR REGION_CODE = '') ";
		// table中的datastore中查询数据sql
		table.setSQL("select * from SYS_FEE_HISTORY where ORDER_CODE like '"
				+ parentID + "%'" + region);
		// 查找数据
		table.retrieve();
		// 放置数据到table中
		table.setDSValue();
		// 设置新增按钮
		callFunction("UI|new|setEnabled", true);

	}

	/**
	 * 过滤
	 * 
	 * @param ob
	 *            Object
	 */
	public void onFilter(Object ob) {
		if ("ALL".equals(ob.toString())) {
			upTable.setFilter("");
			upTable.filter();
			// 放置数据到table中
			upTable.setDSValue();
			// 设置新增按钮
			callFunction("UI|new|setEnabled", true);
		} else if ("Y".equals(ob.toString())) {
			upTable.setFilter("ACTIVE_FLG='Y'");
			upTable.filter();
			// 放置数据到table中
			upTable.setDSValue();
			// 设置新增按钮
			callFunction("UI|new|setEnabled", true);
		} else {
			upTable.setFilter("ACTIVE_FLG='N'");
			upTable.filter();
			// 放置数据到table中
			upTable.setDSValue();
			// 设置新增按钮
			callFunction("UI|new|setEnabled", false);
		}
	}

	public boolean onSaveCheck() {
		if (getValueString("START_DATE").length() == 0) {
			messageBox("生效日期不可为空");
			return true;
		}
		if (getValueString("END_DATE").length() == 0) {
			messageBox("失效日期不可为空");
			return true;
		}
		if (getValueString("ORDER_CODE").length() == 0) {
			messageBox("项目代码不可为空");
			return true;
		}
		if (getValueString("ORDER_DESC").length() == 0) {
			messageBox("项目名称不可为空");
			return true;
		}
		if (getValueString("PY1").length() == 0) {
			messageBox("名称拼音不可为空");
			return true;
		}
		if (getValueString("UNIT_CODE").length() == 0) {
			messageBox("单位不可为空");
			return true;
		}
		if (getValueString("CHARGE_HOSP_CODE").length() == 0) {
			messageBox("院内代码不可为空");
			return true;
		}
		if (getValueString("ORDER_CAT1_CODE").length() == 0) {
			messageBox("细分类不可为空");
			return true;
		}
		if (getValueString("OWN_PRICE").length() == 0) {
			messageBox("自费价不可为空");
			return true;
		}
		if (getValueString("OWN_PRICE2").length() == 0) {
			messageBox("贵宾价不可为空");
			return true;
		}
		if (getValueString("OWN_PRICE3").length() == 0) {
			messageBox("国际医疗价不可为空");
			return true;
		}
		// if(getValueString("EXEC_DEPT_CODE").length() == 0){
		// messageBox("处理部门不可为空");
		// return true;
		// }
		return false;
	}

	// 测试用例
	public static void main(String[] args) {
		JavaHisDebug.initClient();
		// JavaHisDebug.TBuilder();

		// JavaHisDebug.TBuilder();
		JavaHisDebug.runFrame("sys\\SYS_FEE\\SYSFEE_FEE.x");
	}

}
