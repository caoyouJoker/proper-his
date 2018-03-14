package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.datawindow.DataStore;
import com.dongyang.ui.event.TTableEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Vector;

import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import jdo.sys.SystemTool;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;

import jdo.sys.Operator;
import jdo.inv.INVSQL;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.jdo.TDS;
import com.javahis.manager.INVPackOberver;

import jdo.inv.INVOrgTool;
import jdo.inv.INVPackMTool;
import jdo.inv.INVPublicTool;

/**
 * 
 * <p>
 * Title:清洗,消毒,回收
 * </p>
 * 
 * <p>
 * Description: 清洗,消毒,回收
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
 * @author fudo 2009-5-4
 * @version 1.0
 */
public class INVBackPackAndDisnfectionNewControl extends TControl {
	/**
	 * 手术包
	 */
	private TTable table_packm;

	/** 回收记录table */
	private TTable table_dis;
	/**
	 * 物资明细
	 */
	private TTable tableD;
	/**
	 * 记录主表当先被选中的行
	 */
	private String initStatus = "";
	/** 初始化回收单号 */
	private String recycleNo;

	/**
	 * 初始化
	 */
	public void onInit() {
		/** 回收记录table */
		table_dis = (TTable) getComponent("TABLE_DIS");
		// 物资入库主表
		table_packm = (TTable) getComponent("TABLE_PACKM");
		// 物资入库明细
		tableD = (TTable) getComponent("TABLED");
		// tableD值改变事件
		this.addEventListener("TABLED->" + TTableEvent.CHANGE_VALUE,
				"onTableDChargeValue");
		// 物资弹出窗口
		callFunction("UI|PACK_CODE|setPopupMenuParameter", "PACKCODE",
				"%ROOT%\\config\\inv\\INVChoose.x");
		// 接受回传值
		callFunction("UI|PACK_CODE|addEventListener",
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		recycleNo = getRecycleNo();
		onInitTable();
		iniAppOrgCombo();// 20130425 wangzl 添加
		initAppOrgCode();// 20130425 wangzl 添加
		onIntiData();
		observer();
	}

	/**
	 * 利用主键为空先刷新三个表的datasotre
	 */
	public void onInitTable() {
		// 手术报主表
		table_packm.setSQL(INVSQL.getInitPackStockMSql());
		table_packm.retrieve();
		// 手术报细表
		tableD.setSQL(INVSQL.getInitPackStockDSql());
		tableD.retrieve();
	}

	/**
	 * 添加观察者
	 */
	public void observer() {
		// 给主表添加观察者
		TDataStore dataStore = new TDataStore();
		dataStore
				.setSQL("SELECT INV_CODE,INV_CHN_DESC,DESCRIPTION FROM INV_BASE");
		dataStore.retrieve();
		// 明细表添加观察者
		TDS tds = (TDS) tableD.getDataStore();
		tds.addObserver(new INVPackOberver());

		// 给主表添加观察者
		dataStore = new TDataStore();
		dataStore.setSQL("SELECT PACK_CODE,PACK_DESC FROM INV_PACKM");
		dataStore.retrieve();
		// 明细表添加观察者
		tds = (TDS) table_packm.getDataStore();
		tds.addObserver(new INVPackTob(dataStore));
	}

	/**
	 * 初始化供应部门
	 */
	public void iniAppOrgCombo() {
		// 查询条件
		TParm parm = INVOrgTool.getInstance().getDept();
		TComboBox comboBox = (TComboBox) this.getComponent("ORG_CODE");
		comboBox.setParmValue(parm);
		comboBox.updateUI();
		// 表中数据
		comboBox = new TComboBox();
		comboBox.setParmMap("id:ORG_CODE;name:DEPT_CHN_DESC");
		comboBox.setParmValue(parm);
		comboBox.setShowID(true);
		comboBox.setShowName(true);
		comboBox.setExpandWidth(30);
		comboBox.setTableShowList("name");
	}

	/**
	 * 得到默认供应科室
	 * 
	 * @return String
	 */
	public void initAppOrgCode() {
		String appOrgCode = "";
		TParm fromOrgParm = INVPublicTool.getInstance().getOrgCode("B");
		if (fromOrgParm.getErrCode() >= 0) {
			appOrgCode = fromOrgParm.getValue("ORG_CODE", 0);
		}
		this.setValue("ORG_CODE", appOrgCode);
	}

	/** 初始化日期控件数据 */
	public void onIntiData() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_RECYCLE_DATE", date);
		this.setValue("END_RECYCLE_DATE", date);
	}

	/**
	 * 手术包选择返回数据处理
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		if (obj == null)
			return;
		TParm parm = (TParm) obj;
		// 调用处理返回数据的方法
		// 包号
		setValue("PACK_CODE", parm.getValue("PACK_CODE"));
		// 包名
		setValue("PACK_DESC", parm.getValue("PACK_DESC"));
	}

	/**
	 * 查询入口
	 */
	public void onQuery() {
		// 拿到查询sql
		String packCode = getValueString("PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("请输入手术包号!");
			return;
		}
		getDetialValue();
		// 得到默认回收 清洗 消毒的选择
		setCheckBoxValue();
		// 可以保存
		this.callFunction("UI|save|setEnabled", true);
		// 不可以撤销
		this.callFunction("UI|cancel|setEnabled", false);
		// 记录查询出数据的状态
		initStatus = table_packm.getItemString(0, "STATUS");
	}

	/**
	 * 扫描条码
	 */
	public void onScream() {
		String packCode = getValueString("SCREAM");
		if (packCode == null || packCode.length() == 0) {
			return;
		}
		setValue("PACK_CODE", packCode.substring(0, packCode.length() - 4));
		setValue("PACK_SEQ_NO",
				packCode.substring((packCode.length() - 4), packCode.length()));
		this.onQuery();
	}

	/**
	 * 得到明细数据
	 */
	public void getDetialValue() {
		// 得到主表数据
		String disSql = "SELECT * FROM INV_DISINFECTION WHERE 1=1";
		// 得到查询条件
		String selRecycleNo = this.getValueString("SEL_RECYCLE_NO");
		String startRecyleDate = this.getValueString("START_RECYCLE_DATE");
		startRecyleDate = startRecyleDate.substring(0, 10);

		String endRecyleDate = this.getValueString("END_RECYCLE_DATE");
		endRecyleDate = endRecyleDate.substring(0, 10);
		if (selRecycleNo.length() != 0)
			disSql += " AND RECYCLE_NO='" + selRecycleNo + "'";
		disSql += " AND RECYCLE_DATE BETWEEN TO_DATE('" + startRecyleDate
				+ "','YYYY-MM-DD') AND TO_DATE('" + endRecyleDate
				+ "','YYYY-MM-DD')";
		TParm result=new TParm(TJDODBTool.getInstance().select(disSql));
		table_dis.setParmValue(result);
	}
	/**
	 * 重新刷新表
	 * 
	 * @param table
	 *            TTable
	 * @param sql
	 *            String
	 */
	public void tableRetrive(TTable table, String sql) {
		table.setSQL(sql);
		table.retrieve();
		table.setDSValue();
	}

	/**
	 * 处理勾选事件
	 */
	public void setCheckBoxValue() {
		if (table_packm.getRowCount() <= 0)
			return;
		int selRow = table_packm.getSelectedRow();
		TParm selRowParm = table_packm.getDataStore().getRowParm(selRow);
		String status = selRowParm.getValue("STATUS");
		// 状态 0 在库，1 出库, 2 已回收 3 已清洗 4 已消毒 5 已灭菌 6 已打包 7:维修中
		// 不是以回收勾选回收
		if (status.equals("1") || status.equals("5"))
			setValue("BACK", "Y");
		else
			setValue("BACK", "N");

		Timestamp washDate = selRowParm.getTimestamp("WASH_DATE");// 得到清洗日期
		// 如果状态不为1 并且清洗日期不为null
		if (!status.equals("1") && null == washDate)
			setValue("WASH", "Y");
		else
			setValue("WASH", "N"); // 20130424 wangzl 添加

		Timestamp disinfectionDate = selRowParm
				.getTimestamp("DISINFECTION_DATE");// 得到消毒日期
		// 如果状态为1 并且清洗日期为null
		if (!status.equals("1") && null == disinfectionDate)
			setValue("DISINFECTION", "Y");
		else
			setValue("DISINFECTION", "N");
	}

	/**
	 * 回收勾选事件
	 */
	public void onBackSelected() {
		if (table_packm.getRowCount() <= 0)
			return;
		// 如果是不回收
		String selected = this.getValueString("BACK");
		int row = table_packm.getSelectedRow();
		// 如果要勾选回收就要检核是否在库
		String status = table_packm.getItemString(row, "STATUS");
		if (selected.equals("N") && status.equals("1")) {// 20130424 wangzl 添加
			this.setValue("WASH", "N");
			this.setValue("DISINFECTION", "N");
			return;
		}

		// 不是已回收 勾选回收
		if (!status.equals("1") && !status.equals("7")) {
			messageBox("该物资已经在库!");
			setValue("BACK", "N");
		}
	}

	/**
	 * 消毒选择事件
	 */
	public void onDisinfectionSelected() {
		if (table_packm.getRowCount() <= 0)
			return;
		// 如果是不回收
		String selected = this.getValueString("DISINFECTION");
		if (selected.equals("N"))
			return;
		int row = table_packm.getSelectedRow();
		Timestamp disinfection_date = table_packm.getItemTimestamp(row,
				"DISINFECTION_DATE");// 得到清洗日期
		// 如果要勾选回收就要检核是否在库
		String status = table_packm.getItemString(row, "STATUS");
		// 出库状态 不可消毒
		if (status.equals("1")) {
			messageBox("该物资不可以消毒!");
			setValue("DISINFECTION", "N");
		} else if (null != disinfection_date) {
			if (this.messageBox("提示", "该物资已消毒!是否再次消毒?", 2) != 0) {
				setValue("WASH", "N");
				return;
			}
		}
	}

	/**
	 * 清洗选择事件
	 */
	public void onWashSelected() {
		if (table_packm.getRowCount() <= 0)
			return;
		// 如果是不清洗
		String selected = this.getValueString("WASH");
		if (selected.equals("N"))
			return;
		int row = table_packm.getSelectedRow();
		Timestamp washDate = table_packm.getItemTimestamp(row, "WASH_DATE");// 得到清洗日期
		// 如果要勾选清洗就要检核是否在库
		String status = table_packm.getItemString(row, "STATUS");
		// 出库状态 不可清洗
		if (status.equals("1")) {
			messageBox("该物资不可以清洗!");
			setValue("WASH", "N");
		} else if (null != washDate) {
			if (this.messageBox("提示", "该物资已清洗!是否再次清洗?", 2) != 0) {
				setValue("WASH", "N");
				return;
			}
		}
	}

	/**
	 * 主表点击事件
	 */
	public void onTableMClecked() {
		table_packm.acceptText();
		// 点击的行
		int row = table_packm.getSelectedRow();
		// 拿到主表点击时要产生的查询明细表的sql
		TParm tableMParm = table_packm.getDataStore().getRowParm(row);
		setTextValue(tableMParm);
		// 明细表
		String sql = INVSQL.getQueryStockDSql(tableMParm.getValue("PACK_CODE"),
				tableMParm.getInt("PACK_SEQ_NO"));
		// TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		// tableD.setParmValue(result);
		// 刷新细表
		tableRetriveD(tableD, sql);
		setCheckBoxValue();
	}

	/**
	 * 重新刷新细表
	 * 
	 * @param table
	 *            TTable
	 * @param sql
	 *            String
	 */
	public void tableRetriveD(TTable table, String sql) {
		TDS tds = new TDS();
		tds.setSQL(sql);
		tds.retrieve();
		INVPackOberver obser = new INVPackOberver();
		tds.addObserver(obser);
		table.setDataStore(tds);
		table.setDSValue();
	}

	/**
	 * 细表值改变事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableDChargeValue(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return false;
		// 拿到table上的parmmap的列名
		String columnName = tableD.getDataStoreColumnName(node.getColumn());
		// 赠量改变
		if ("RECOUNT_TIME".equals(columnName)) {
			// 拿到当前改变后的数据
			int value = TCM_Transform.getInt(node.getValue());
			if (value < 0) {
				messageBox("折损次数不可小于0!");
				return true;
			}
			return false;
		}
		return false;
	}

	/**
	 * 数据上翻
	 * 
	 * @param parm
	 *            TParm
	 */
	public void setTextValue(TParm parm) {
		setValueForParm("PACK_CODE;PACK_SEQ_NO", parm);
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		// 清空属性
		clearText();
		// 清空主表
		clearTable(table_dis);
		// 清空手术报
		clearTable(table_packm);
		// 清空明细表
		clearTable(tableD);
		// 可以保存
		this.callFunction("UI|save|setEnabled", true);
		// 不可以撤销
		this.callFunction("UI|cancel|setEnabled", false);
	}

	/**
	 * 清空属性 
	 */
	public void clearText() { 
		this.clearValue("PACK_CODE;PACK_SEQ_NO;PACK_DESC;SCREAM;PACK_STATUS;DISINFECTION_POTSEQ;DISINFECTION_PROGRAM;DISINFECTION_NUM");
	}

	/**
	 * 清空
	 * 
	 * @param table
	 *            TTable
	 */
	public void clearTable(TTable table) {
		// 接收最后一次修改
		table.acceptText();
		// 清空选中
		table.clearSelection();
		// 删除所有行
		table.removeRowAll();
		// 清空修改记录
		table.resetModify();
	}

	/**
	 * 取消
	 * 
	 * @return boolean
	 */
	public boolean onCancel() {
		int rowCount = table_packm.getRowCount();
		if (rowCount <= 0) {
			messageBox("没有要保存的数据!");
			return false;
		}
		// 取得保存数据
		TParm saveParm = getSaveParm(false);
		if (saveParm == null)
			return false;
		// 调用保存事务
		TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
				"saveBackPackAndDisnfection", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			messageBox("保存失败");
			return false;
		}
		messageBox("保存成功");
		// 保存后刷新界面
		dealAaterSaveData();
		// 可以保存
		this.callFunction("UI|save|setEnabled", true);
		// 不可以撤销
		this.callFunction("UI|cancel|setEnabled", false);

		return true;
	}

	/** 新增回收单方法 */
	public void onNew() {
		if (table_packm.getSelectedRow() < 0) {
			// 清空明细
			clearTable(tableD);
			// 清空手术包
			clearTable(table_packm);
		}
		if (table_dis.getSelectedRow() >= 0
				|| table_packm.getSelectedRow() >= 0) {
			if (this.getValueString("ORG_CODE").length() == 0) {
				messageBox("供应室部门不能为空");
				return;
			}
			if (this.getValueString("PACK_CODE").length() == 0) {
				messageBox("包号不能为空");
				return;
			}
			// 新增之前 查询 是否存在此手术包
			String sql = "SELECT * FROM INV_PACKSTOCKM WHERE ORG_CODE='"
					+ getValueString("ORG_CODE") + "' " + "AND PACK_CODE='"
					+ getValueString("PACK_CODE") + "' " + "AND PACK_SEQ_NO='"
					+ getValueInt("PACK_SEQ_NO") + "'";
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if (result.getCount() < 0) {
				messageBox("此手术包不存在");
				return;
			}
			String status = result.getValue("STATUS", 0);// 得到该手术包的状态
			this.setValue("PACK_STATUS", status);
			// 新增手术包
			newTablePack();
			return;
		}
		// 新增回收单
		newTableDis();
	}

	/** 新增手術包 */
	public void newTablePack() {
		// 检核出库单
		if (!checkNewDetial())
			return;
		// 添加属性
		String sql = "SELECT * FROM INV_PACKSTOCKM " + "	 WHERE ORG_CODE='"
				+ this.getValue("ORG_CODE") + "' " + "AND PACK_CODE='"
				+ this.getValue("PACK_CODE") + "' " + "AND PACK_SEQ_NO="
				+ this.getValue("PACK_SEQ_NO") + "";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		int row = table_packm.addRow();
		// 包名
		table_packm.setItem(row, "PACK_DESC", getValue("PACK_DESC"));
		// 包号
		table_packm.setItem(row, "PACK_CODE", getValue("PACK_CODE"));
		// 序号
		table_packm.setItem(row, "PACK_SEQ_NO", getValue("PACK_SEQ_NO"));
		// 数量
		table_packm.setItem(row, "QTY", 1);
		// 状态
		table_packm.setItem(row, "STATUS", result.getData("STATUS", 0));
		// 回收日期
		table_packm.setItem(row, "RECYCLE_DATE",
				result.getData("RECYCLE_DATE", 0));
		// 回收人员
		table_packm.setItem(row, "RECYCLE_USER",
				result.getData("RECYCLE_USER", 0));
		// 清洗日期
		table_packm.setItem(row, "WASH_DATE", result.getData("WASH_DATE", 0));
		// 清洗人员
		table_packm.setItem(row, "WASH_USER", result.getData("WASH_USER", 0));
		// 消毒日期
		table_packm.setItem(row, "DISINFECTION_DATE",
				result.getData("DISINFECTION_DATE", 0));
		// 消毒人员
		table_packm.setItem(row, "DISINFECTION_USER",
				result.getData("DISINFECTION_USER", 0));
		// 锅次
		table_packm.setItem(row, "DISINFECTION_POTSEQ",
				result.getData("DISINFECTION_POTSEQ", 0));
		// 程序
		table_packm.setItem(row, "DISINFECTION_PROGRAM",
				result.getData("DISINFECTION_PROGRAM", 0));
		// 锅号
		table_packm.setItem(row, "DISINFECTION_NUM",
				result.getData("DISINFECTION_NUM", 0));
		// 使用成本
		table_packm.setItem(row, "USE_COST", result.getDouble("USE_COST", 0));
		// 一次性材料成本
		table_packm.setItem(row, "ONCE_USE_COST",
				result.getDouble("ONCE_USE_COST", 0));
		table_packm.setSelectedRow(row);
		table_packm.setDSValue();
		// 设置主表数据
		installTableDisData();
		setCheckBoxValue();
	}

	/** 新增细项时 跟主表回收单绑定 */
	public void installTableDisData() {
		int disSleRow = table_dis.getSelectedRow();// 得到主表选中行
		int packmSleRow = table_packm.getSelectedRow();// 得到子表选中行

		table_dis.setItem(disSleRow, "PACK_CODE",
				table_packm.getItemString(packmSleRow, "PACK_CODE"));
		table_dis.setItem(disSleRow, "PACK_SEQ_NO",
				table_packm.getItemInt(packmSleRow, "PACK_SEQ_NO"));
		table_dis.setItem(disSleRow, "QTY",
				table_packm.getItemInt(packmSleRow, "QTY"));

		table_dis.setItem(disSleRow, "RECYCLE_DATE",
				table_packm.getItemTimestamp(packmSleRow, "RECYCLE_DATE"));
		table_dis.setItem(disSleRow, "RECYCLE_USER",
				table_packm.getItemString(packmSleRow, "RECYCLE_USER"));

		table_dis.setItem(disSleRow, "WASH_DATE",
				table_packm.getItemTimestamp(packmSleRow, "WASH_DATE"));
		table_dis.setItem(disSleRow, "WASH_USER",
				table_packm.getItemString(packmSleRow, "WASH_USER"));

		table_dis.setItem(disSleRow, "DISINFECTION_DATE",
				table_packm.getItemTimestamp(packmSleRow, "DISINFECTION_DATE"));
		table_dis.setItem(disSleRow, "DISINFECTION_USER",
				table_packm.getItemString(packmSleRow, "DISINFECTION_USER"));

		table_dis.setItem(disSleRow, "DISINFECTION_POTSEQ",
				table_packm.getItemString(packmSleRow, "DISINFECTION_POTSEQ"));
		table_dis.setItem(disSleRow, "DISINFECTION_PROGRAM",
				table_packm.getItemString(packmSleRow, "DISINFECTION_PROGRAM"));
		table_dis.setItem(disSleRow, "DISINFECTION_NUM",
				table_packm.getItemString(packmSleRow, "DISINFECTION_NUM"));

	}

	/**
	 * 新增明细时检核
	 * 
	 * @return boolean
	 */
	public boolean checkNewDetial() {
		// 出库单(主表是不是被选中)
		int row = table_dis.getSelectedRow();
		if (row < 0) {
			messageBox_("请选择回收单!");
			return false;
		}
		// 通用检核方法
		return true;
	}

	/**
	 * 新增回收单
	 */
	public void newTableDis() {
		int row = table_dis.addRow();
		TDataStore ds=table_dis.getDataStore();
		Timestamp date = SystemTool.getInstance().getDate();
//		table_dis.setItem(row, "RECYCLE_NO", recycleNo);
//		table_dis.setItem(row, "SEQ_NO", row + 1);
//		table_dis.setItem(row, "RECYCLE_DATE", date);
//		table_dis.setItem(row, "RECYCLE_USER", Operator.getID());
//		table_dis.setItem(row, "OPT_USER", Operator.getID());
//		table_dis.setItem(row, "OPT_DATE", date);
//		table_dis.setItem(row, "OPT_TERM", Operator.getIP());
		
		ds.setItem(row, "RECYCLE_NO", recycleNo);
		ds.setItem(row, "SEQ_NO", row + 1);
		ds.setItem(row, "RECYCLE_DATE", date);
		ds.setItem(row, "RECYCLE_USER", Operator.getID());
		ds.setItem(row, "OPT_USER", Operator.getID());
		ds.setItem(row, "OPT_DATE", date);
		ds.setItem(row, "OPT_TERM", Operator.getIP());
		table_dis.setSelectedRow(row);
		table_dis.setDSValue(row);
	}

	/**
	 * 保存入口
	 * 
	 * @return boolean
	 */
	public boolean onUpdate() {

		int rowCount = table_packm.getRowCount();
		if (rowCount <= 0) {
			messageBox("没有要保存的数据!");
			return false;
		}

		// 取得保存细表数据
		TParm saveParm = getSaveParm(true);
		if (saveParm == null)
			return false;
		// 得到最终主表数据
		table_dis.acceptText();
		TParm conditions = new TParm();
		int disCount = table_dis.getDataStore().rowCount();
		for (int i = 0; i < disCount; i++) {
			conditions = table_dis.getDataStore().getRowParm(i);
		}
		saveParm.setData("disParm", conditions.getData());

		// 调用保存事务
		TParm result = TIOM_AppServer.executeAction("action.inv.INVAction",
				"saveBackPackAndDisnfection", saveParm);
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			messageBox("保存失败!");
			onClear();
			return false;
		}
		messageBox("保存成功!");
		// 保存后刷新界面
		dealAaterSaveData();
		// 不可以保存
		// this.callFunction("UI|save|setEnabled", false);
		// 可以撤销
		this.callFunction("UI|cancel|setEnabled", true);
		return true;
	}

	/** 回收单号 */
	private String getRecycleNo() {
		String recyleNo = SystemTool.getInstance().getNo("ALL", "INV",
				"INV_SUPREQUEST", "No");
		return recyleNo;
	}

	/**
	 * 处理请领单号
	 * 
	 * @param table
	 *            TTable
	 * @param requestNo
	 *            String
	 */
	public void dealRequestNo(TTable table, String recycle_no) {
		int row = table.getRowCount();
		if (row <= 0)
			return;
		for (int i = 0; i < row; i++) {
			table.setItem(i, "RECYCLE_NO", recycle_no);
		}
	}

	/**
	 * 处理保存后的数据
	 */
	public void dealAaterSaveData() {
		// 得到回收和消毒的选择
		setCheckBoxValue();
	}

	/**
	 * 取得保存数据
	 * 
	 * @param action
	 *            boolean
	 * @return TParm
	 */
	public TParm getSaveParm(boolean action) {
		TParm saveParm = new TParm();
		TParm packmOneParm = new TParm();
		// 回收
		String backPack = this.getValueString("BACK");
		// 清洗
		String wash = this.getValueString("WASH");
		// 消毒
		String disinfection = this.getValueString("DISINFECTION");
		if (backPack.equals("N") && wash.equals("N")
				&& disinfection.equals("N")) {
			messageBox("无要保存的数据!");
			return null;
		}
		// 取系统时间
		Timestamp date = SystemTool.getInstance().getDate();
		int count = table_packm.getDataStore().rowCount();
		for (int i = 0; i < count; i++) {
			if (backPack.equals("Y")) {
				// 状态更改为已经入库
				table_packm.setItem(i, "STATUS", "2");
				table_packm.setItem(i, "RECYCLE_DATE", date);
				table_packm.setItem(i, "RECYCLE_USER", Operator.getID());
			}
			if (wash.equals("Y")) {
				table_packm.setItem(i, "STATUS", "3");
				// 清洗日期
				table_packm.setItem(i, "WASH_DATE", date);
				table_packm.setItem(i, "WASH_USER", Operator.getID());
			}
			String disPotSeq = table_packm.getItemString(i,
					"DISINFECTION_POTSEQ");// 得到锅次
			String disProgram = table_packm.getItemString(i,
					"DISINFECTION_PROGRAM");// 得到程序
			String disNum = table_packm.getItemString(i,
			        "DISINFECTION_NUM");// 得到锅号

			// 消毒
			if (disinfection.equals("Y")) {
				if (disPotSeq.length() == 0 && disProgram.length() == 0&&disNum.length() == 0) {
					messageBox("锅次 程序锅号不能为空");
					return null;
				} else {
					// 状态更改为已经消毒
					table_packm.setItem(i, "STATUS", "4");
					// 消毒日期
					table_packm.setItem(i, "DISINFECTION_DATE", date);
					// 消毒人员
					table_packm.setItem(i, "DISINFECTION_USER",
							Operator.getID());
					if (table_packm.getItemTimestamp(i,
							"DISINFECTION_VALID_DATE") == null) {
						// 设置效期 VALID_DATE
						table_packm.setItem(i, "DISINFECTION_VALID_DATE",
								StringTool.rollDate(date, 14));
					}
				}

			}
			// 操作人
			table_packm.setItem(i, "OPT_USER", Operator.getID());
			// 时间
			table_packm.setItem(i, "OPT_DATE", date);
			// 操作IP
			table_packm.setItem(i, "OPT_TERM", Operator.getIP());
			// 科室
			table_packm.setItem(i, "ORG_CODE", this.getValue("ORG_CODE"));

			table_packm.acceptText();// 得到最后的数据
			packmOneParm = table_packm.getDataStore().getRowParm(i);
		}
		saveParm.setData("TABLE_PACKM", packmOneParm.getData());
		// 取更新数据
		String[] saveSql = getsaveSql();
		saveParm.setData("SAVESQL", saveSql);
		return saveParm;
	}

	/**
	 * 得到要保存的数据
	 * 
	 * @return String[]
	 */
	public String[] getsaveSql() {
		// 取出主表sql
		String[] sqlM = table_packm.getUpdateSQL();
		String[] sqlD = tableD.getUpdateSQL();
		// 把两组sql合并
		return StringTool.copyArray(sqlM, sqlD);
	}

	/**
	 * 获得消毒记录数据..同时给数据添加清洗消毒和回收的数据
	 * 
	 * @param backPack
	 *            String
	 * @param disinfection
	 *            String
	 * @param action
	 *            boolean
	 * @return TParm
	 */
	public TParm getDisinfectionParm(String backPack, String wash,
			String disinfection, boolean action) {
		TParm dispenfectionParm = new TParm();
		// 处理取消
		dispenfectionParm = dealCancelParm();

		return dispenfectionParm;
	}

	/**
	 * 处理取消的保存parm
	 * 
	 * @return TParm
	 */
	public TParm dealCancelParm() {
		// 取系统事件
		TParm dispenfectionParm = new TParm();
		// 状态 0 在库，1 出库, 2 已回收 3 已消毒 4:维修中
		// 取系统事件
		Timestamp date = SystemTool.getInstance().getDate();
		// 消毒记录
		dispenfectionParm = table_packm.getDataStore().getRowParm(0);
		String disinfectionDate = dispenfectionParm
				.getValue("DISINFECTION_DATE");
		disinfectionDate = disinfectionDate.substring(0, 4)
				+ disinfectionDate.substring(5, 7)
				+ disinfectionDate.substring(8, 10)
				+ disinfectionDate.substring(11, 13)
				+ disinfectionDate.substring(14, 16)
				+ disinfectionDate.substring(17, 19);
		// 消毒时间
		dispenfectionParm.setData("DISINFECTION_DATE", disinfectionDate);
		// 消毒时间
		dispenfectionParm.setData("WASH_DATE",
				dispenfectionParm.getValue("WASH_DATE"));

		// 清洗日期
		table_packm.setItem(0, "WASH_DATE", "");
		// 更改状态
		table_packm.setItem(0, "STATUS", initStatus);
		// 消毒日期
		table_packm.setItem(0, "DISINFECTION_DATE", "");
		// 消毒人员
		table_packm.setItem(0, "DISINFECTION_USER", "");
		// 消毒效期
		table_packm.setItem(0, "DISINFECTION_VALID_DATE", "");
		dispenfectionParm.setCount(1);
		dispenfectionParm.setData("ACTIONMODE", "CANCEL");
		return dispenfectionParm;

	}

	/**
	 * 打印条码
	 */
	public void onPrint() {
		TParm parm = new TParm();
		int row = table_packm.getSelectedRow();
		if (row < 0)
			row = 0;
		String packCode = table_packm.getItemString(row, "PACK_CODE");
		if (packCode == null || packCode.length() == 0) {
			messageBox("手术包选择错误!");
			return;
		}
		// 手术包序号:
		int packSeqNo = table_packm.getItemInt(row, "PACK_SEQ_NO");
		String packCodeSeq = "" + packSeqNo;
		for (int i = packCodeSeq.length(); i < 4; i++) {
			packCodeSeq = "0" + packCodeSeq;
		}
		packCodeSeq = "" + packCodeSeq;
		TParm orgParm = INVPublicTool.getInstance().getOrgDesc();

		String deptDesc = orgParm.getValue("DEPT_CHN_DESC", 0);

		// 调用条码
		TParm packCodeSeqParm = new TParm();
		TParm packParm = INVPackMTool.getInstance().getPackDesc(packCode);
		String packDesc = packParm.getValue("PACK_DESC", row);
		String packDept = deptDesc;
		// 消毒日期 
		TParm result = new TParm();
//		TParm result = INVPackStockMTool.getInstance().getPackDate(packCode,
//				packSeqNo);
		String disinfectionDate = StringTool.getString(
				(Timestamp) result.getData("DISINFECTION_DATE", 0),
				"yyyy/MM/dd");
		String valueDate = StringTool.getString(
				(Timestamp) result.getData("DISINFECTION_VALID_DATE", 0),
				"yyyy/MM/dd");
		String optUser = result.getValue("OPT_USER", 0);
		String disinfectionUser = result.getValue("DISINFECTION_USER", 0);

		packCodeSeqParm.addData("PACK_CODE_SEQ", packCode + packCodeSeq);
		packCodeSeqParm.addData("PACK_DESC", packDesc);
		packCodeSeqParm.addData("PACK_DEPT", "(" + packDept + ")");
		packCodeSeqParm.addData("DISINFECTION_DATE", disinfectionDate);
		packCodeSeqParm.addData("VALUE_DATE", valueDate);
		packCodeSeqParm.addData("OPT_USER", optUser);
		packCodeSeqParm.addData("DISINFECTION_USER", disinfectionUser);
		packCodeSeqParm.setCount(1);
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_CODE_SEQ");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "PACK_DEPT");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_DATE");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "VALUE_DATE");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "OPT_USER");
		packCodeSeqParm.addData("SYSTEM", "COLUMNS", "DISINFECTION_USER");

		parm = new TParm();
		parm.setData("T1", packCodeSeqParm.getData());

		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\INV\\INVPackSeq.jhw", parm);
	}

	/**
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
