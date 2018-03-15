package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.SPCCabinetTool;
import jdo.spc.SPCSQL;
import jdo.sys.Operator;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDS;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:智能柜基本信息维护
 * </p>
 * 
 * <p>
 * Description:智能柜基本信息维护
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author fuwj 2012.10.23
 * @version 1.0
 */

public class SPCCabinetControl extends TControl {

	private String action = "insertM";

	private TTable TABLE_Z;
	private TTable TABLE_M;
	private TTable TABLE_L;
	private TPanel tPanel_Z;
	private TPanel tPanel_L;

	public SPCCabinetControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		TABLE_Z = getTable("TABLE_Z");
		TABLE_M = getTable("TABLE_M");
		TABLE_L = getTable("TABLE_L");
		this.addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,
				"onROOMTABLEChargeValue");
		initPage();
	}

	/**
	 * 初始化界面
	 */
	private void initPage() {
		TABLE_Z.removeRowAll();
		TParm parm = new TParm();
		TParm result = SPCCabinetTool.getInstance().queryInfo(parm);
		TABLE_Z.setParmValue(result);
		callFunction("UI|DELETE|setEnabled", false);
		setValue("REGION_CODE", Operator.getRegion());
		action = "insertM";
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		// 清空画面内容
		setValue("CABINET_ID", "");
		setValue("CABINET_IP", "");
		setValue("CABINET_DESC", "");
		setValue("DESCRIPTION", "");
		setValue("START_DATE", "");
		setValue("END_DATE", "");
		setValue("ORG_CODE", "");
		setValue("GUARD_IP", "");
		setValue("RFID_IP", "");
		// <-- 新增四个字段的维护 update by shendr date:2013.5.24
		setValue("ZKRFID_URL", "");
		setValue("MQ_DESC", "");
		setValue("CABINET_TYPE", "");
		getTCheckBox("ACTIVE_FLG").setSelected(false);
		// -->
		TABLE_Z.removeRowAll();
		TABLE_M.removeRowAll();
		callFunction("UI|DELETE|setEnabled", false);
		getTextField("CABINET_CODE").setEnabled(true);
		action = "insertM";
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		tPanel_Z = (TPanel) getComponent("tPanel_Z");
		tPanel_L = (TPanel) getComponent("tPanel_L");
		TParm parm = new TParm();
		String cabId = this.getValueString("CABINET_ID");
		String cabName = this.getValueString("CABINET_DESC");
		parm.setData("CABINET_ID", cabId);
		parm.setData("CABINET_DESC", cabName);
		TParm result;
		if (tPanel_Z.isShowing()) {
			TParm tparm = new TParm();
			if (!"".equals(this.getValueString("CABINET_ID"))) {
				tparm.setData("CABINET_ID", this.getValueString("CABINET_ID"));
			}
			if (!"".equals(this.getValueString("CABINET_DESC"))) {
				tparm.setData("CABINET_DESC", this
						.getValueString("CABINET_DESC"));
			}
			result = SPCCabinetTool.getInstance().queryInfo(tparm);
			TABLE_Z.setParmValue(result);
			if (result.getCount() <= 0) {
				this.messageBox("没有查询到数据");
				return;
			}
		} else if (tPanel_L.isShowing()) {
			TParm tparm = new TParm();
			if ((this.getValue("START_DATE") != null)
					&& (this.getValue("END_DATE") != null)) {
				String startDate = this.getValue("START_DATE").toString()
						.substring(0, 10).replace('-', '/')
						+ " 00:00:00";
				String endDate = this.getValue("END_DATE").toString()
						.substring(0, 10).replace('-', '/')
						+ " 23:59:59";
				tparm.setData("START_DATE", startDate);
				tparm.setData("END_DATE", endDate);
			}
			if (!"".equals(this.getValueString("TASK_TYPE"))) {
				tparm.setData("TASK_TYPE", this.getValueString("TASK_TYPE"));
			}
			if (!"".equals(this.getValueString("EVENT_TYPE"))) {
				tparm.setData("EVENT_TYPE", this.getValueString("EVENT_TYPE"));
			}
			result = SPCCabinetTool.getInstance().queryLog(tparm);
			TABLE_L.setParmValue(result);
			if (result.getCount() <= 0) {
				this.messageBox("没有查询到数据");
				return;
			}
		}
		action = "insertM";
	}

	/**
	 * TABLE单击事件
	 */
	// public void onTableClicked() {
	// int row = TABLE_Z.getSelectedRow();
	// if (row != -1) {
	// TParm parm = TABLE_Z.getParmValue();
	// this.setValue("CABINET_CODE", parm.getData("CABINET_CODE", row));
	// this.setValue("CABINET_IP", parm.getData("CABINET_IP", row));
	// this.setValue("CABINET_DESC", parm.getData("CABINET_DESC", row));
	// this.setValue("DESCRIPTION", parm.getData("DESCRIPTION", row));
	// callFunction("UI|DELETE|setEnabled", true);
	// getTextField("CABINET_CODE").setEnabled(false);
	// action = "save";
	// }
	// }

	/**
	 * 保存方法
	 */
	public void onSave() {
		tPanel_Z = (TPanel) getComponent("tPanel_Z");
		if (tPanel_Z.isShowing()) {
			TParm result;
			if ("insertM".equals(action)) {
				if (!CheckData()) {
					return;
				}
				TParm parm = new TParm();
				Timestamp date = StringTool.getTimestamp(new Date());
				parm.setData("CABINET_ID", getValueString("CABINET_ID"));
				parm.setData("CABINET_DESC", getValueString("CABINET_DESC"));
				parm.setData("CABINET_IP", getValueString("CABINET_IP"));
				parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
				parm.setData("ORG_CODE", getValueString("ORG_CODE"));
				parm.setData("GUARD_IP", getValueString("GUARD_IP"));
				parm.setData("RFID_IP", getValueString("RFID_IP"));
				parm.setData("REGION_CODE", "H01");
				parm.setData("OPT_USER", Operator.getID());
				parm.setData("OPT_DATE", date);
				parm.setData("OPT_TERM", Operator.getIP());
				// <-- 新增四个字段的维护 update by shendr date:2013.5.24
				parm.setData("ZKRFID_URL", getValueString("ZKRFID_URL"));
				parm.setData("MQ_DESC", getValueString("MQ_DESC"));
				parm.setData("CABINET_TYPE", getValueString("CABINET_TYPE"));
				parm.setData("ACTIVE_FLG", getValueString("ACTIVE_FLG"));
				// -->
				result = SPCCabinetTool.getInstance().insertInfo(parm);
				this.messageBox("保存成功");
				setValue("DESCRIPTION", "");
				setValue("CABINET_ID", "");
				setValue("CABINET_CODE", "");
				setValue("CABINET_IP", "");
				setValue("CABINET_DESC", "");
				setValue("ORG_CODE", "");
				setValue("GUARD_IP", "");
				setValue("RFID_IP", "");
				// <-- 新增四个字段的维护 update by shendr date:2013.5.24
				setValue("ZKRFID_URL", "");
				setValue("MQ_DESC", "");
				setValue("CABINET_TYPE", "");
				getTCheckBox("ACTIVE_FLG").setSelected(false);
				// -->
				onQuery();
				return;
			} else if ("updateM".equals(action)) {
				if (!CheckData()) {
					return;
				}
				TParm parm = new TParm();
				Timestamp date = StringTool.getTimestamp(new Date());
				parm.setData("CABINET_ID", getValueString("CABINET_ID"));
				parm.setData("CABINET_DESC", getValueString("CABINET_DESC"));
				parm.setData("CABINET_IP", getValueString("CABINET_IP"));
				parm.setData("DESCRIPTION", getValueString("DESCRIPTION"));
				parm.setData("ORG_CODE", getValueString("ORG_CODE"));
				parm.setData("GUARD_IP", getValueString("GUARD_IP"));
				parm.setData("RFID_IP", getValueString("RFID_IP"));
				parm.setData("OPT_USER", Operator.getID());
				parm.setData("OPT_DATE", date);
				parm.setData("OPT_TERM", Operator.getIP());
				// <-- 新增四个字段的维护 update by shendr date:2013.5.24
				parm.setData("ZKRFID_URL", getValueString("ZKRFID_URL"));
				parm.setData("MQ_DESC", getValueString("MQ_DESC"));
				parm.setData("CABINET_TYPE", getValueString("CABINET_TYPE"));
				parm.setData("ACTIVE_FLG", getValueString("ACTIVE_FLG"));
				// -->
				result = SPCCabinetTool.getInstance().updateInfo(parm);
				this.messageBox("更新成功");
				TABLE_M.removeRowAll();
				setValue("DESCRIPTION", "");
				setValue("CABINET_ID", "");
				setValue("CABINET_CODE", "");
				setValue("CABINET_IP", "");
				setValue("CABINET_DESC", "");
				setValue("ORG_CODE", "");
				setValue("GUARD_IP", "");
				setValue("RFID_IP", "");
				// <-- 新增四个字段的维护 update by shendr date:2013.5.24
				setValue("ZKRFID_URL", "");
				setValue("MQ_DESC", "");
				setValue("CABINET_TYPE", "");
				getTCheckBox("ACTIVE_FLG").setSelected(false);
				// -->
				onQuery();
				return;
			} else {
				if (!CheckDataM()) {
					return;
				}
				TTable table_M = getTable("TABLE_M");
				table_M.acceptText();
				TDataStore dataStore = table_M.getDataStore();
				// 获得全部改动的行号
				int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
				// 获得全部的新增行
				int newrows[] = dataStore.getNewRows(dataStore.PRIMARY);
				// 给固定数据配数据
				Timestamp date = StringTool.getTimestamp(new Date());
				for (int i = 0; i < newrows.length; i++) {
					dataStore.setItem(newrows[i], "CABINET_ID",
							getValueString("CABINET_ID"));
					dataStore.setItem(newrows[i], "OPT_USER", Operator.getID());
					dataStore.setItem(newrows[i], "OPT_DATE", date);
					dataStore.setItem(newrows[i], "OPT_TERM", Operator.getIP());
				}
				TParm inParm = new TParm();
				String guardId = "";
				String guardDesc = "";
				String type = "";
				int j = 0;
				for (int i = 0; i < rows.length - newrows.length; i++) {
					j++;
					guardId = dataStore.getItemString(rows[i], "GUARD_ID");
					guardDesc = dataStore.getItemString(rows[i], "GUARD_DESC");
					type = dataStore.getItemString(rows[i], "IS_TOXIC_GUARD");
					inParm.setData("GUARD_ID", i, guardId);
					inParm.setData("GUARD_DESC", i, guardDesc);
					inParm.setData("IS_TOXIC_GUARD", i, type);
					inParm.setData("OPT_USER", i, Operator.getID());
					inParm.setData("OPT_TERM", i, Operator.getIP());
					inParm.setData("NUM", j);
				}
				inParm.setData("UPDATE_SQL", dataStore.getUpdateSQL());
				result = TIOM_AppServer.executeAction(
						"action.spc.SPCCabinetAction", "onSaveCabinet", inParm);
				if (result == null || result.getErrCode() < 0) {
					this.messageBox("E0001");
					return;
				}
				this.messageBox("保存成功");
				return;
			}
		}
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		tPanel_Z = (TPanel) getComponent("tPanel_Z");
		if (tPanel_Z.isShowing()) {
			if (TABLE_M.getSelectedRow() == -1
					&& TABLE_Z.getSelectedRow() == -1) {
				this.messageBox("请选择要删除的项");
				return;
			}
			if (TABLE_M.getSelectedRow() == -1
					&& TABLE_Z.getSelectedRow() != -1) {
				int row = TABLE_Z.getSelectedRow();
				String code = TABLE_Z.getItemString(row, "CABINET_ID");
				TParm parm = new TParm();
				parm.setData("CABINET_ID", code);
				TParm result = SPCCabinetTool.getInstance().queryChild(parm);
				if (result.getCount() > 0) {
					this.messageBox("请先删除门禁信息");
					return;
				}
				if (this.messageBox("删除", "确定是否删除智能柜", 2) == 0) {
					result = SPCCabinetTool.getInstance().deleteInfo(parm);
					this.messageBox("删除成功");
					TABLE_M.removeRowAll();
					setValue("DESCRIPTION", "");
					setValue("CABINET_ID", "");
					setValue("CABINET_CODE", "");
					setValue("CABINET_IP", "");
					setValue("CABINET_DESC", "");
					setValue("ORG_CODE", "");
					setValue("GUARD_IP", "");
					setValue("RFID_IP", "");
					// <-- 新增四个字段的维护 update by shendr date:2013.5.24
					setValue("ZKRFID_URL", "");
					setValue("MQ_DESC", "");
					setValue("CABINET_TYPE", "");
					getTCheckBox("ACTIVE_FLG").setSelected(false);
					// -->
					onQuery();
					return;
				}
			}
			if (TABLE_M.getSelectedRow() != -1
					&& TABLE_Z.getSelectedRow() == -1) {
				TParm parm = new TParm();
				int row = TABLE_M.getSelectedRow();
				String code = TABLE_M.getItemString(row, "GUARD_ID");
				parm.setData("GUARD_ID", code);
				if (this.messageBox("删除", "确定是否删除门禁", 2) == 0) {
					TParm result = SPCCabinetTool.getInstance().deleteChild(
							parm);
					this.messageBox("删除成功");
					TABLE_M = getTable("TABLE_M");
					TABLE_M.removeRowAll();
					TABLE_M.setSelectionMode(0);
					TDS tds = new TDS();
					tds.setSQL(SPCSQL
							.getCabinetGuard(getValueString("CABINET_ID")));
					tds.retrieve();
					TABLE_M.setDataStore(tds);
					TABLE_M.setDSValue();
					onAddRow();
				}
			}
		}
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到TCheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getTCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * 检查数据
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("CABINET_ID"))) {
			this.messageBox("智能柜编号不能为空");
			return false;
		}
		if ("".equals(getValueString("CABINET_IP"))) {
			this.messageBox("智能柜IP不能为空");
			return false;
		}
		if ("".equals(getValueString("CABINET_DESC"))) {
			this.messageBox("智能柜名称不能为空");
			return false;
		}
		if ("".equals(getValueString("ORG_CODE"))) {
			this.messageBox("所属部门不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 主表点击事件
	 */
	public void onTableMClicked() {
		int row = TABLE_Z.getSelectedRow();
		if (row != -1) {
			setValue("CABINET_ID", TABLE_Z.getItemString(row, "CABINET_ID"));
			setValue("CABINET_DESC", TABLE_Z.getItemString(row, "CABINET_DESC"));
			setValue("CABINET_IP", TABLE_Z.getItemString(row, "CABINET_IP"));
			setValue("DESCRIPTION", TABLE_Z.getItemString(row, "DESCRIPTION"));
			setValue("ORG_CODE", TABLE_Z.getItemString(row, "ORG_CODE"));
			setValue("RFID_IP", TABLE_Z.getItemString(row, "RFID_IP"));
			setValue("GUARD_IP", TABLE_Z.getItemString(row, "GUARD_IP"));
			// <-- 新增四个字段的维护 update by shendr date:2013.5.24
			this.setValue("ZKRFID_URL", TABLE_Z
					.getItemString(row, "ZKRFID_URL"));
			this.setValue("MQ_DESC", TABLE_Z.getItemString(row, "MQ_DESC"));
			this.setValue("CABINET_TYPE", TABLE_Z.getItemString(row,
					"CABINET_TYPE"));
			if ("Y".equals(TABLE_Z.getItemString(row, "ACTIVE_FLG"))) {
				getTCheckBox("ACTIVE_FLG").setSelected(true);
			} else if ("N".equals(TABLE_Z.getItemString(row, "ACTIVE_FLG"))) {
				getTCheckBox("ACTIVE_FLG").setSelected(false);
			}
			// -->
			callFunction("UI|DELETE|setEnabled", true);
			action = "updateM";
			// 门禁信息
			TABLE_M = getTable("TABLE_M");
			TABLE_M.removeRowAll();
			TABLE_M.setSelectionMode(0);
			TDS tds = new TDS();
			tds.setSQL(SPCSQL.getCabinetGuard(getValueString("CABINET_ID")));
			tds.retrieve();
			TABLE_M.setDataStore(tds);
			TABLE_M.setDSValue();
			onAddRow();
		}
	}

	/**
	 * 添加一行细项
	 */
	private void onAddRow() {
		// 有未编辑行时返回
		if (!this.isNewRow())
			return;
		TTable table_M = getTable("TABLE_M");
		int row = table_M.addRow();
		table_M.getDataStore().setActive(row, false);
	}

	/**
	 * 是否有未编辑行
	 * 
	 * @return boolean
	 */
	private boolean isNewRow() {
		Boolean falg = false;
		TParm parmBuff = getTable("TABLE_M").getDataStore().getBuffer(
				getTable("TABLE_M").getDataStore().PRIMARY);
		int lastRow = parmBuff.getCount("#ACTIVE#");
		Object obj = parmBuff.getData("#ACTIVE#", lastRow - 1);
		if (obj != null) {
			falg = (Boolean) parmBuff.getData("#ACTIVE#", lastRow - 1);
		} else {
			falg = true;
		}
		return falg;
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		TTable table_M = getTable("TABLE_M");
		table_M.acceptText();
		for (int i = 0; i < table_M.getRowCount(); i++) {
			if (!table_M.getDataStore().isActive(i)) {
				continue;
			}
			if ("".equals(table_M.getItemString(i, "GUARD_ID"))) {
				this.messageBox("门禁编号不能为空");
				return false;
			}
			if ("".equals(table_M.getItemString(i, "GUARD_DESC"))) {
				this.messageBox("门禁名称不能为空");
				return false;
			}
		}
		return true;
	}

	public void onTableDClicked() {
		TTable table = getTable("TABLE_M");
		int row = table.getSelectedRow();
		if (row != -1) {
			action = "updateD";
			// 主项信息
			TTable table_Z = getTable("TABLE_Z");
			table_Z.setSelectionMode(0);
		}
	}

	/**
	 * 诊室table值改变事件
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onROOMTABLEChargeValue(Object obj) {
		// 拿到节点数据,存储当前改变的行号,列号,数据,列名等信息
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue()))
			return false;
		// 拿到table上的parmmap的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// 拿到table
		TTable table = node.getTable();
		// 得到改变的行
		int row = node.getRow();
		// 拿到当前改变后的数据
		String value = "" + node.getValue();
		// table.setItem(row, "CABINET_ID", "111");
		// 如果是名称改变了拼音1自动带出,并且科室名称不能为空SD
		TParm parm = new TParm();
		if ("GUARD_ID".equals(columnName)) {
			parm.setData("GUARD_ID", (String) node.getOldValue());
			parm.setData("CABINET_ID", this.getValueString("CABINET_ID"));
			TParm result = SPCCabinetTool.getInstance().queryCode(parm);
			if (result.getCount() > 0) {
				this.messageBox("门禁编号不能重复");
				return true;
			}
			table.getDataStore().setActive(row, true);
		}
		return false;
	}

	/**
	 * 主table双击事件
	 */
	public void onTableMDClicked() {
		tPanel_L = (TPanel) getComponent("tPanel_L");
		TTabbedPane aPane = (TTabbedPane) getComponent("tTabbedPane_0");
		aPane.setSelectedIndex(1);
		if (tPanel_L.isShowing()) {
			TParm parm = new TParm();
			TParm result = SPCCabinetTool.getInstance().queryLog(parm);
			if (result.getCount() <= 0) {
				this.messageBox("没有查询到数据");
				return;
			}
			TABLE_L.setParmValue(result);
		}
	}

}
