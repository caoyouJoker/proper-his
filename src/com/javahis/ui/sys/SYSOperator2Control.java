package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title:用户管理
 * </p>
 *
 * <p>
 * Description:用户管理
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: javahis
 * </p>
 *
 * @author zhangy 2009.6.08
 * @version 1.0
 */
public class SYSOperator2Control extends TControl {

	// 用户科室列表
	private Map deptList;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始化权限
		// if (!getPopedem("delPopedem")) {
		// ((TMenuItem) getComponent("delete")).setVisible(false);
		// }
		// 给TABLEDEPT中的CHECKBOX添加侦听事件
		callFunction("UI|TABLEDEPT|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableDeptCheckBoxClicked");
		// 给OPT_CLINICAREA_TABLE中的CHECKBOX添加侦听事件
		callFunction("UI|OPT_CLINICAREA_TABLE|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableOPT_ClinicareaCheckBoxClicked");
		// 给OPT_STATION_TABLE中的CHECKBOX添加侦听事件
		callFunction("UI|OPT_STATION_TABLE|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableOPT_StationCheckBoxClicked");
		// 初始化所有科室信息
		showDeptAll();
		// 初始化使用者所属科室列表
		showOperatorDept();
		// 初始化所有诊区
		showClinicareaAll();
		// 初始化使用者所属诊区列表
		showOperatorClinicarea();
		// 初始化所有病区
		showStationAll();
		// 初始化使用者所属病区列表
		showOperatorStation();
		// 设置删除按钮状态
		callFunction("UI|delete|setEnabled", false);
		// 设置保存按钮状态
		callFunction("UI|save|setEnabled", true);
		// 初始化当前时间
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("ACTIVE_DATE", date);

		// TABLE
		TTable table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
                //==========pangben modify 20110524
		dataStore.setSQL(SYSSQL.getSYSOperator(Operator.getRegion()));
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();
		int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
				.isFilter() ? table.getDataStore().FILTER : table
				.getDataStore().PRIMARY);
		setValue("SEQ", seq);
		// 用户科室列表
		deptList = new HashMap();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (getValueString("DEPT_CODE").length() == 0) {
			onFilter();
			return;
		}
		onComboBoxSelected();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		String clearObj = "USER_ID;USER_NAME;DEPT_CODE;REGION_CODE;"
				+ "LCS_CLASS_CODE;LCS_NO;EFF_LCS_DATE;END_LCS_DATE;"
				+ "POS_CODE;ROLE_ID;USER_PASSWORD;ACTIVE_DATE;END_DATE;"
				+ "RCNT_LOGIN_DATE;RCNT_LOGOUT_DATE;RCNT_IP;PUB_FUNCTION;"
				+ "PY1;PY2;SEQ;FOREIGNER_FLG;ID_NO;DESCRIPTION;FULLTIME_FLG;"
				+ "CTRL_FLG;E_MAIL;SEX_CODE;ABNORMAL_TIMES";
		clearValue(clearObj);
		// 清空科室信息表
		getTable("TABLEDEPT").removeRowAll();
		// 清空证照信息表
		getTable("TABLELISCENSE").removeRowAll();
		// 清空使用者所属诊区信息表
		getTable("OPT_CLINICAREA_TABLE").removeRowAll();
		// 清空使用者所属病区信息表
		getTable("OPT_STATION_TABLE").removeRowAll();
		// 初始化所有科室信息
		showDeptAll();
		// 初始化使用者所属科室列表
		showOperatorDept();
		// 显示所有操作人员列表
		showOperator();
		// 初始化所有诊区
		showClinicareaAll();
		// 初始化使用者所属诊区列表
		showOperatorClinicarea();
		// 初始化所有病区
		showStationAll();
		// 初始化使用者所属病区列表
		showOperatorStation();
		// 初始化页面状态
		callFunction("UI|USER_ID|setEnabled", true);
		callFunction("UI|delete|setEnabled", false);
		callFunction("UI|save|setEnabled", true);
		TTable table = getTable("TABLE");
		int seq = getMaxSeq(table.getDataStore(), "SEQ", table.getDataStore()
				.isFilter() ? table.getDataStore().FILTER : table
				.getDataStore().PRIMARY);
		setValue("SEQ", seq);
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		if (!checkNewData()) {
			return;
		}
		// 使用者ID状态： 可用为新增；不可用为更新
		boolean action_flg = this.getTextField("USER_ID").isEnabled();
		TTable table = getTable("TABLE");
		int row = 0;
		String userId = "";
		Timestamp date = StringTool.getTimestamp(new Date());
		if (action_flg) {
			userId = this.getValueString("USER_ID");
			// 检测用户是否已存在
			if (userId.length() == 0) {
				messageBox("请先输入使用者");
				return;
			}
			if (table.getDataStore().exist("USER_ID='" + userId + "'")) {
				messageBox("使用者已存在");
				return;
			}
			// 得到新增行
			row = table.addRow();

		} else {
			// 跟到选中行
			row = table.getSelectedRow();
			userId = this.getValueString("USER_ID");
		}
		TDataStore dataStore = table.getDataStore();
		TParm parm = new TParm();
		// 用户信息
		dataStore.setItem(row, "USER_ID", userId);
		dataStore.setItem(row, "USER_NAME", getValueString("USER_NAME"));
		dataStore.setItem(row, "PY1", getValueString("PY1"));
		dataStore.setItem(row, "PY2", getValueString("PY2"));
		dataStore.setItem(row, "SEQ", getValueInt("SEQ"));
		dataStore.setItem(row, "FOREIGNER_FLG", getValue("FOREIGNER_FLG"));
		dataStore.setItem(row, "ID_NO", getValueString("ID_NO"));
		dataStore.setItem(row, "SEX_CODE", getValueString("SEX_CODE"));
		dataStore.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
		dataStore.setItem(row, "POS_CODE", getValueString("POS_CODE"));
		String password = OperatorTool.getInstance().encrypt(
				getValueString("USER_PASSWORD"));
		dataStore.setItem(row, "USER_PASSWORD", password);
		dataStore.setItem(row, "FULLTIME_FLG", getValue("FULLTIME_FLG"));
		dataStore.setItem(row, "CTRL_FLG", getValue("CTRL_FLG"));
		dataStore.setItem(row, "E_MAIL", getValue("E_MAIL"));
		dataStore.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
		dataStore.setItem(row, "ROLE_ID", getValueString("ROLE_ID"));
		Timestamp begin = (Timestamp) getValue("ACTIVE_DATE");
		Timestamp end = (Timestamp) getValue("END_DATE");
		dataStore.setItem(row, "ACTIVE_DATE", begin);
		dataStore.setItem(row, "END_DATE", end);
		dataStore.setItem(row, "ABNORMAL_TIMES", getValueInt("ABNORMAL_TIMES"));
		dataStore.setItem(row, "PUB_FUNCTION", getValueString("PUB_FUNCTION"));
		dataStore.setItem(row, "OPT_USER", Operator.getID());
		dataStore.setItem(row, "OPT_DATE", date);
		dataStore.setItem(row, "OPT_TERM", Operator.getIP());
		// SYS_OPERATOR表数据
		String[] operator_sql = dataStore.getUpdateSQL();
		parm.setData("OPERATOR", operator_sql);

		// // 科室信息
		// TTable tabledept = getTable("TABLEDEPT");
		// if (tabledept.getDataStore().isModified()) {
		// tabledept.acceptText();
		// if (tabledept.getDataStore().rowCount() > 0) {
		// Vector main_flg = new Vector();
		// main_flg = tabledept.getDataStore().getVector("MAIN_FLG");
		// boolean flg = false;
		// for (int i = 0; i < main_flg.size(); i++) {
		// if (((Vector) main_flg.get(i)).get(0).toString()
		// .equals("Y"))
		// flg = true;
		// }
		// if (!flg) {
		// this.messageBox("请选择主科室");
		// return;
		// }
		// }
		// tabledept.getDataStore().showDebug();
		// }

		// 保存使用者数据
		// if (table.getDataStore().isModified()) {
		// table.acceptText();
		// if (!table.update()) {
		// messageBox("E0001");
		// return;
		// }
		// table.setDSValue();
		// }
		// table.setSelectedRow(row);
		messageBox("P0001");
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		TTable table = getTable("TABLE");
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;
		// 操作者证照
		int count1 = this.getTable("TABLELISCENSE").getRowCount();
		if (count1 > 0) {
			this.messageBox("该操作者存在证照信息，请删除");
			return;
		}
		// 操作者所属科室
		int count2 = this.getTable("TABLEDEPT").getRowCount();
		if (count2 > 0) {
			this.messageBox("该操作者存在所属科室，请删除");
			return;
		}
		// 删除操作确认
		switch (messageBox("提示信息", "是否删除该操作者?", 0)) {
		case 0:
			table.removeRow(row);
			break;
		case 1:
			return;

		}
		// 删除按钮状态设置
		if (table.getRowCount() > 0) {
			table.setSelectedRow(0);
			onTableClicked();
		} else {
			callFunction("UI|delete|setEnabled", false);
		}

	}

	/**
	 * 过滤条件
	 */
	public void onFilter() {
		boolean flg = ((TTextField) getComponent("USER_ID")).isEnabled();
		// 如果为不可使用状态，则进行数据更新
		if (!flg) {
			TTable table = getTable("TABLE");
			int row = table.getSelectedRow();
			// 使用者名称
			String userName = getValueString("USER_NAME");
			table.setItem(row, "USER_NAME", userName);
			// 拼音
			String py = TMessage.getPy(userName);
			setValue("PY1", py);
			// 性别
			String sex = getValueString("SEX_CODE");
			table.setItem(row, "SEX_CODE", sex);
			// 区域
			String region = getValueString("REGION_CODE");
			table.setItem(row, "REGION_CODE", region);
			// 职称
			String pos = getValueString("POS_CODE");
			table.setItem(row, "POS_CODE", pos);
			// 角色 ROLE_ID
			String role = getValueString("ROLE_ID");
			table.setItem(row, "ROLE_ID", role);
			return;
		}
		// 查询操作者
		String value = "";
		String str = getValueString("USER_NAME");
		if (str.length() != 0)
			value += "USER_NAME like '" + str + "%'";
		str = getValueString("USER_ID");
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "USER_ID like '" + str + "%'";
		}
		str = getValueString("REGION_CODE");
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "REGION_CODE = '" + str + "'";
		}
		str = getValueString("POS_CODE");
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "POS_CODE = '" + str + "'";
		}
		str = getValueString("ROLE_ID");
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "ROLE_ID = '" + str + "'";
		}
		TTable table1 = getTable("TABLE");
		if (value.length() > 0) {
			table1.setFilter(value);
			table1.filter();
			return;
		}
		table1.setFilter("");
		table1.filter();
	}

	/**
	 * UserId回车事件
	 */
	public void onUserIdAction() {
		// onFilter();
		((TTextField) getComponent("USER_NAME")).grabFocus();
	}

	/**
	 * UserName回车事件
	 */
	public void onUserNameAction() {
		String userName = getValueString("USER_NAME");
		String py = TMessage.getPy(userName);
		setValue("PY1", py);
		((TTextField) getComponent("PY1")).grabFocus();
	}

	/**
	 * POS_CODE回车事件
	 */
	public void onPosCodeAction() {
		// onFilter();
		((TCheckBox) getComponent("FULLTIME_FLG")).grabFocus();
	}

	/**
	 * REGION_CODE回车事件
	 */
	public void onRegionCodeAction() {
		// onFilter();
		((TComboBox) getComponent("POS_CODE")).grabFocus();
	}

	/**
	 * ROLE_ID回车事件
	 */
	public void onRoleIdAction() {
		// onFilter();
		((TTextFormat) getComponent("ACTIVE_DATE")).grabFocus();
	}

	/**
	 * ACTIVE_DATE回车事件
	 */
	public void onActiveDateAction() {
		((TTextFormat) getComponent("END_DATE")).grabFocus();
	}

	/**
	 * END_DATE回车事件
	 */
	public void onEndDateAction() {
		((TTextField) getComponent("PUB_FUNCTION")).grabFocus();
	}

	/**
	 * ID_NO回车事件
	 */
	public void onIdNOAction() {
		String flg = getValueString("FOREIGNER_FLG");
		String id_no = getValueString("ID_NO");
		if (flg.equals("N") && StringTool.isId(id_no)) {
			String sex = StringTool.isMaleFromID(id_no);
			this.setValue("SEX_CODE", sex);
		}
		((TComboBox) getComponent("SEX_CODE")).grabFocus();
	}

	/**
	 * EFF_LCS_DATE回车事件
	 */
	public void onEffLCSDateAction() {
		((TTextFormat) getComponent("END_LCS_DATE")).grabFocus();
	}

	/**
	 * 科室ComboBox选中事件
	 */
	public void onComboBoxSelected() {
		String dept = getValueString("DEPT_CODE");
		String sql = "SELECT USER_ID,DEPT_CODE FROM SYS_OPERATOR_DEPT";
		if (dept.length() > 0) {
			sql += " WHERE DEPT_CODE = '" + dept + "'";
		}
		TDataStore st = new TDataStore();
		st.setSQL(sql);
		st.retrieve();
		int count = st.rowCount();
		String sql2 = "SELECT * FROM SYS_OPERATOR " + "WHERE USER_ID IN ("
				+ "'" + st.getItemData(0, "USER_ID") + "'";
		for (int i = 1; i < count; i++) {
			sql2 = sql2 + ",'" + st.getItemData(i, "USER_ID") + "'";
		}
		sql2 += ")";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql2);
		dataStroe.retrieve();
		TTable table1 = getTable("TABLE");
		table1.setDataStore(dataStroe);
		table1.setDSValue();
		onFilter();
	}

	/**
	 * 科室ComboBox过滤事件
	 */
	public void onComboBoxFilter() {
		String dept = getValueString("DEPT_CODE");
		TTable table = getTable("TABLE2");
		String filter = "";
		if (((TComboBox) getComponent("DEPT_CODE")).getSelectedIndex() != 0)
			filter = "DEPT_CODE = '" + dept + "'";
		table.setFilter(filter);
		table.filter();
		table.setDSValue();

	}

	/**
	 * 操作者信息表格(TABLE)单击事件
	 */
	public void onTableClicked() {
		// 清空科室ComboBox
		((TComboBox) getComponent("DEPT_CODE")).setSelectedIndex(0);
		// 设置新增、删除按钮状态
		if (getTable("TABLE").getSelectedRow() != -1) {
			callFunction("UI|delete|setEnabled", true);
			// callFunction("UI|new|setEnabled", false);
		}
		// 得到被选中行的数据
		TParm parm = getTable("TABLE").getDataStore().getRowParm(
				getTable("TABLE").getSelectedRow());
		// 在UI中显示选中行的数据
		setValue("USER_ID", parm.getData("USER_ID"));
		setValue("USER_NAME", parm.getData("USER_NAME"));
		setValue("PY1", parm.getData("PY1"));
		setValue("PY2", parm.getData("PY2"));
		setValue("SEQ", parm.getData("SEQ"));
		setValue("FOREIGNER_FLG", parm.getData("FOREIGNER_FLG"));
		setValue("ID_NO", parm.getData("ID_NO"));
		setValue("SEX_CODE", parm.getData("SEX_CODE"));
		setValue("DESCRIPTION", parm.getData("DESCRIPTION"));
		setValue("POS_CODE", parm.getData("POS_CODE"));
		String pwd = OperatorTool.getInstance().decrypt(
				(String) parm.getData("USER_PASSWORD"));
		setValue("USER_PASSWORD", pwd);
		setValue("FULLTIME_FLG", parm.getData("FULLTIME_FLG"));
		setValue("CTRL_FLG", parm.getData("CTRL_FLG"));
		setValue("E_MAIL", parm.getData("E_MAIL"));
		setValue("REGION_CODE", parm.getData("REGION_CODE"));
		setValue("ROLE_ID", parm.getData("ROLE_ID"));
		setValue("ACTIVE_DATE", parm.getData("ACTIVE_DATE"));
		setValue("END_DATE", parm.getData("END_DATE"));
		setValue("PUB_FUNCTION", parm.getData("PUB_FUNCTION"));
		setValue("RCNT_LOGIN_DATE", parm.getData("RCNT_LOGIN_DATE"));
		setValue("RCNT_LOGOUT_DATE", parm.getData("RCNT_LOGOUT_DATE"));
		setValue("RCNT_IP", parm.getData("RCNT_IP"));
		setValue("ABNORMAL_TIMES", parm.getData("ABNORMAL_TIMES"));
		// 显示指定操作者的所属科室
		String userId = parm.getValue("USER_ID");
		TTable table = getTable("TABLEDEPT");
		table.setFilter("USER_ID = '" + userId + "'");

		table.filter();
		table.setDSValue();

		// 显示指定操作者不在的科室
		showDeptAll();
		String dept = "";
		String deptin = "";
		for (int i = 0; i < table.getDataStore().rowCount(); i++) {
			deptin = (String) table.getDataStore().getItemData(i, "DEPT_CODE");
			dept += "DEPT_CODE <> '" + deptin + "' AND ";
		}
		if (dept.length() > 0) {
			dept = dept.substring(0, dept.length() - 5);
		}
		TTable table2 = getTable("TABLE2");
		table2.setFilter(dept);
		table2.filter();
		table2.setDSValue();

		// 显示指定操作者的所属诊区
		TTable clinicarea_table = getTable("OPT_CLINICAREA_TABLE");
		clinicarea_table.setFilter("USER_ID = '" + userId + "'");
		clinicarea_table.filter();
		clinicarea_table.setDSValue();

		// 显示指定操作者不在的诊区
		showClinicareaAll();
		String clinicare = "";
		String clinicarein = "";
		for (int i = 0; i < clinicarea_table.getDataStore().rowCount(); i++) {
			clinicarein = (String) clinicarea_table.getDataStore().getItemData(
					i, "STATION_CLINIC_CODE");
			clinicare += "CLINICAREA_CODE != '" + clinicarein + "' AND ";
		}
		if (clinicare.length() > 0) {
			clinicare = clinicare.substring(0, clinicare.length() - 5);
		}
		TTable clinicareaAll = getTable("CLINICAREA_TABLE");
		clinicareaAll.setFilter(clinicare);
		clinicareaAll.filter();
		clinicareaAll.setDSValue();

		// 显示指定操作者的所属病区
		TTable station_table = getTable("OPT_STATION_TABLE");
		station_table.setFilter("USER_ID = '" + userId + "'");
		station_table.filter();
		station_table.setDSValue();

		// 显示指定操作者不在的病区
		showStationAll();
		String station = "";
		String stationin = "";
		for (int i = 0; i < station_table.getDataStore().rowCount(); i++) {
			stationin = (String) station_table.getDataStore().getItemData(i,
					"STATION_CLINIC_CODE");
			station += "STATION_CODE != '" + stationin + "' AND ";
		}
		if (station.length() > 0) {
			station = station.substring(0, station.length() - 5);
		}
		TTable stationAll = getTable("STATION_TABLE");
		stationAll.setFilter(station);
		stationAll.filter();
		stationAll.setDSValue();

		// 查询指定用户的证照信息
		showOperatorLicense(userId);
		// 改变输入框状态
		callFunction("UI|USER_ID|setEnabled", false);
		// 设置保存按钮状态
		callFunction("UI|save|setEnabled", true);
	}

	/**
	 * 证照信息表格(TableLiscense)单击事件
	 */
	public void onTableLiscenseClicked() {
		TParm parm = getTable("TABLELISCENSE").getDataStore().getRowParm(
				getTable("TABLELISCENSE").getSelectedRow());
		setValue("LCS_CLASS_CODE", parm.getData("LCS_CLASS_CODE"));
		setValue("LCS_NO", parm.getData("LCS_NO"));
		setValue("EFF_LCS_DATE", parm.getData("EFF_LCS_DATE"));
		setValue("END_LCS_DATE", parm.getData("END_LCS_DATE"));
	}

	/**
	 * 单击INSERT_DEPT按钮
	 */
	public void onInsertDeptClicked() {
		Timestamp date = StringTool.getTimestamp(new Date());
		if (getTable("TABLE2").getSelectedRow() == -1)
			return;
		String code = getTable("TABLE2").getDataStore().getItemString(
				getTable("TABLE2").getSelectedRow(), "DEPT_CODE");
		Map map = new HashMap();
		for (int i = 0; i < getTable("TABLEDEPT").getRowCount(); i++) {
			map.put(i, getTable("TABLEDEPT").getItemData(i, "DEPT_CODE"));
		}
		if (!map.isEmpty() && map.containsValue(code)) {
			this.messageBox("科室已存在，不可添加");
			return;
		}
		int count = getTable("TABLEDEPT").getRowCount();
		int newRow = getTable("TABLEDEPT").addRow();

		String userId = getTable("TABLE").getDataStore().getItemString(
				getTable("TABLE").getSelectedRow(), "USER_ID");
		if (count == 0)
			getTable("TABLEDEPT").setItem(newRow, "MAIN_FLG", "Y");
		else
			getTable("TABLEDEPT").setItem(newRow, "MAIN_FLG", "N");
		getTable("TABLEDEPT").setItem(newRow, "DEPT_CODE", code);
		getTable("TABLEDEPT").setItem(newRow, "USER_ID", userId);
		getTable("TABLEDEPT").setItem(newRow, "OPT_USER", Operator.getID());
		getTable("TABLEDEPT").setItem(newRow, "OPT_DATE", date);
		getTable("TABLEDEPT").setItem(newRow, "OPT_TERM", Operator.getIP());
		getTable("TABLE2").removeRow(getTable("TABLE2").getSelectedRow());

		// 添加deptList
		//deptList
	}

	/**
	 * 单击REMOVE_DEPT按钮
	 */
	public void onRemoveDeptClicked() {
		if (getTable("TABLEDEPT").getSelectedRow() == -1)
			return;
		int newRow = getTable("TABLE2").addRow();
		String data = getTable("TABLEDEPT").getDataStore().getItemString(
				getTable("TABLEDEPT").getSelectedRow(), "DEPT_CODE");
		getTable("TABLE2").setItem(newRow, "DEPT_CODE", data);
		getTable("TABLE2").setItem(newRow, "DEPT_CHN_DESC", data);
		getTable("TABLEDEPT").removeRow(getTable("TABLEDEPT").getSelectedRow());
	}

	/**
	 * 单击Insert_CLINICAREA按钮
	 */
	public void onInsertClinicareaClicked() {
		Timestamp date = StringTool.getTimestamp(new Date());
		if (getTable("CLINICAREA_TABLE").getSelectedRow() == -1)
			return;
		int newRow = getTable("OPT_CLINICAREA_TABLE").addRow();
		String code = getTable("CLINICAREA_TABLE").getDataStore()
				.getItemString(getTable("CLINICAREA_TABLE").getSelectedRow(),
						"CLINICAREA_CODE");
		String userId = getTable("TABLE").getDataStore().getItemString(
				getTable("TABLE").getSelectedRow(), "USER_ID");
		getTable("OPT_CLINICAREA_TABLE").setItem(newRow, "MAIN_FLG", "N");
		getTable("OPT_CLINICAREA_TABLE").setItem(newRow, "STATION_CLINIC_CODE",
				code);
		getTable("OPT_CLINICAREA_TABLE").setItem(newRow, "USER_ID", userId);
		getTable("OPT_CLINICAREA_TABLE").setItem(newRow, "TYPE", 1);
		getTable("OPT_CLINICAREA_TABLE").setItem(newRow, "OPT_USER",
				Operator.getID());
		getTable("OPT_CLINICAREA_TABLE").setItem(newRow, "OPT_DATE", date);
		getTable("OPT_CLINICAREA_TABLE").setItem(newRow, "OPT_TERM",
				Operator.getIP());
		getTable("CLINICAREA_TABLE").removeRow(
				getTable("CLINICAREA_TABLE").getSelectedRow());
	}

	/**
	 * 单击Remove_CLINICAREA按钮
	 */
	public void onRemoveClinicareaClicked() {
		if (getTable("OPT_CLINICAREA_TABLE").getSelectedRow() == -1)
			return;
		int newRow = getTable("CLINICAREA_TABLE").addRow();
		String data = getTable("OPT_CLINICAREA_TABLE").getDataStore()
				.getItemString(
						getTable("OPT_CLINICAREA_TABLE").getSelectedRow(),
						"STATION_CLINIC_CODE");
		getTable("OPT_CLINICAREA_TABLE").removeRow(
				getTable("OPT_CLINICAREA_TABLE").getSelectedRow());
		getTable("CLINICAREA_TABLE").setItem(newRow, "CLINICAREA_CODE", data);
		getTable("CLINICAREA_TABLE").setItem(newRow, "CLINIC_DESC", data);
	}

	/**
	 * 单击Insert_Station按钮
	 */
	public void onInsertStationClicked() {
		Timestamp date = StringTool.getTimestamp(new Date());
		if (getTable("STATION_TABLE").getSelectedRow() == -1)
			return;
		int newRow = getTable("OPT_STATION_TABLE").addRow();
		String code = getTable("STATION_TABLE").getDataStore().getItemString(
				getTable("STATION_TABLE").getSelectedRow(), "STATION_CODE");
		String userId = getTable("TABLE").getDataStore().getItemString(
				getTable("TABLE").getSelectedRow(), "USER_ID");
		getTable("OPT_STATION_TABLE").setItem(newRow, "MAIN_FLG", "N");
		getTable("OPT_STATION_TABLE").setItem(newRow, "STATION_CLINIC_CODE",
				code);
		getTable("OPT_STATION_TABLE").setItem(newRow, "USER_ID", userId);
		getTable("OPT_STATION_TABLE").setItem(newRow, "TYPE", 2);
		getTable("OPT_STATION_TABLE").setItem(newRow, "OPT_USER",
				Operator.getID());
		getTable("OPT_STATION_TABLE").setItem(newRow, "OPT_DATE", date);
		getTable("OPT_STATION_TABLE").setItem(newRow, "OPT_TERM",
				Operator.getIP());
		getTable("STATION_TABLE").removeRow(
				getTable("STATION_TABLE").getSelectedRow());
	}

	/**
	 * 单击Remove_STATION按钮
	 */
	public void onRemoveStationClicked() {
		if (getTable("OPT_STATION_TABLE").getSelectedRow() == -1)
			return;
		int newRow = getTable("STATION_TABLE").addRow();
		String data = getTable("OPT_STATION_TABLE").getDataStore()
				.getItemString(getTable("OPT_STATION_TABLE").getSelectedRow(),
						"STATION_CLINIC_CODE");
		getTable("OPT_STATION_TABLE").removeRow(
				getTable("OPT_STATION_TABLE").getSelectedRow());
		getTable("STATION_TABLE").setItem(newRow, "STATION_CODE", data);
		getTable("STATION_TABLE").setItem(newRow, "STATION_DESC", data);
	}

	/**
	 * 操作者所属科室表格(TABLEDEPT)复选框改变事件
	 *
	 * @param obj
	 */
	public void onTableDeptCheckBoxClicked(Object obj) {
		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的行
		int row = tableDown.getSelectedRow();
		// 取消所有选中
		for (int i = 0; i < tableDown.getRowCount(); i++)
			tableDown.setItem(i, "MAIN_FLG", "N");
		// 选择被选中行
		tableDown.setItem(row, "MAIN_FLG", "Y");
	}

	/**
	 * 操作者所属诊区表格(OPT_CLINICAREA_TABLE)复选框改变事件
	 *
	 * @param obj
	 */
	public void onTableOPT_ClinicareaCheckBoxClicked(Object obj) {
		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的行
		int row = tableDown.getSelectedRow();
		// 取消所有选中
		for (int i = 0; i < tableDown.getRowCount(); i++)
			tableDown.setItem(i, "MAIN_FLG", "N");
		// 选择被选中行
		tableDown.setItem(row, "MAIN_FLG", "Y");
	}

	/**
	 * 操作者所属病区表格(OPT_Station_TABLE)复选框改变事件
	 *
	 * @param obj
	 */
	public void onTableOPT_StationCheckBoxClicked(Object obj) {
		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的行
		int row = tableDown.getSelectedRow();
		// 取消所有选中
		for (int i = 0; i < tableDown.getRowCount(); i++)
			tableDown.setItem(i, "MAIN_FLG", "N");
		// 选择被选中行
		tableDown.setItem(row, "MAIN_FLG", "Y");
	}

	/**
	 * 添加新的证照
	 */
	public void onInsertLiscenseClicked() {
		if (getTable("TABLE").getSelectedRow() == -1) {
			messageBox("请选择使用者");
			return;
		}
		String code = getValueString("LCS_CLASS_CODE").trim();
		TTable table = getTable("TABLELISCENSE");
		if (table.getDataStore().exist("LCS_CLASS_CODE='" + code + "'")) {
			messageBox("证照已存在");
			return;
		}
		if (getValueString("LCS_CLASS_CODE").trim().length() == 0) {
			messageBox("证照类别不能为空");
			return;
		}
		if (getValueString("LCS_NO").trim().length() == 0) {
			messageBox("证照号码不能为空");
			return;
		}
		Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
		if (begin == null) {
			messageBox("证照起日不能为空");
			return;
		}
		Timestamp end = (Timestamp) getValue("END_LCS_DATE");
		if (end == null) {
			messageBox("证照讫日不能为空");
			return;
		}
		if (end.compareTo(begin) <= 0) {
			messageBox("证照起日不能大于证照讫日");
			return;
		}
		int row = table.addRow();
		table.setItem(row, "LCS_CLASS_CODE", code);
		table.setItem(row, "LCS_NO", getValueString("LCS_NO").trim());
		table.setItem(row, "EFF_LCS_DATE", begin);
		table.setItem(row, "END_LCS_DATE", end);
	}

	/**
	 * 移除证照
	 */
	public void onRemoveLiscenseClicked() {
		TTable table = getTable("TABLELISCENSE");
		int row = table.getSelectedRow();
		if (row == -1) {
			messageBox("请选择证照");
			return;
		}
		table.removeRow(row);
	}

	/**
	 * 显示所有科室列表
	 */
	public void showDeptAll() {
		String sql = "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT";
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		TTable table = getTable("TABLE2");
		table.setDataStore(dataStore);
		table.setDSValue();
	}

	/**
	 * 显示使用者所属科室列表
	 */
	public void showOperatorDept() {
		TTable table = getTable("TABLEDEPT");
		String sql = "SELECT * FROM SYS_OPERATOR_DEPT ORDER BY MAIN_FLG DESC";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql);
		dataStroe.retrieve();
		table.setDataStore(dataStroe);
	}

	/**
	 * 查询指定用户的证照信息
	 *
	 * @param userId
	 */
	public void showOperatorLicense(String userId) {
		TDataStore dataStroe2 = new TDataStore();
		String sql2 = "SELECT * FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
				+ userId + "'";
		dataStroe2.setSQL(sql2);
		dataStroe2.retrieve();
		TTable table2 = getTable("TABLELISCENSE");
		table2.setDataStore(dataStroe2);
		table2.setDSValue();
	}

	/**
	 * 显示所有诊区
	 */
	public void showClinicareaAll() {
		TTable table = getTable("CLINICAREA_TABLE");
		String sql = "SELECT CLINICAREA_CODE,CLINIC_DESC FROM REG_CLINICAREA";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql);
		dataStroe.retrieve();
		table.setDataStore(dataStroe);
		table.setDSValue();
	}

	/**
	 * 显示所有病区
	 */
	public void showStationAll() {
		TTable table = getTable("STATION_TABLE");
		String sql = "SELECT STATION_CODE , STATION_DESC FROM SYS_STATION";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql);
		dataStroe.retrieve();
		table.setDataStore(dataStroe);
		table.setDSValue();
	}

	/**
	 * 显示使用者所属诊区列表
	 */
	public void showOperatorClinicarea() {
		TTable table = getTable("OPT_CLINICAREA_TABLE");
		String sql = "SELECT * FROM SYS_OPERATOR_STATION WHERE TYPE = '1' ORDER BY MAIN_FLG DESC";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql);
		dataStroe.retrieve();
		table.setDataStore(dataStroe);
	}

	/**
	 * 显示使用者所属病区列表
	 */
	public void showOperatorStation() {
		TTable table = getTable("OPT_STATION_TABLE");
		String sql = "SELECT * FROM SYS_OPERATOR_STATION WHERE TYPE = '2' ORDER BY MAIN_FLG DESC";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql);
		dataStroe.retrieve();
		table.setDataStore(dataStroe);
	}

	/**
	 * 显示所有操作人员列表
	 */
	public void showOperator() {
		String sql = "SELECT * FROM SYS_OPERATOR";
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		TTable table = getTable("TABLE");
		table.setDataStore(dataStore);
		table.setDSValue();
	}

	/**
	 * 检查数据的完整性和准确性
	 *
	 * @return
	 */
	public boolean checkNewData() {
		if (getValueString("USER_NAME").equals("")) {
			this.messageBox("用户姓名不能为空");
			return false;
		}
		if (this.getValue("FOREIGNER_FLG").equals("N")) {
			String id_no = getValueString("ID_NO");
			if (!StringTool.isId(id_no)) {
				this.messageBox("身份证不正确");
				return false;
			}
		}
		if (getValueString("SEX_CODE").equals("")) {
			this.messageBox("性别不能为空");
			return false;
		}
		if (getValueString("POS_CODE").equals("")) {
			this.messageBox("职称不能为空");
			return false;
		}
		if (getValueString("USER_PASSWORD").equals("")) {
			this.messageBox("口令不能为空");
			return false;
		}
		// E_MAIL
		String e_mail = getValueString("E_MAIL");
		if (!e_mail.equals("")) {
			// 邮箱验证
			if (!StringTool.isEmail(e_mail)) {
				this.messageBox("邮箱输入不合法");
				return false;
			}
		}
		if (getValueString("REGION_CODE").equals("")) {
			this.messageBox("区域不能为空");
			return false;
		}
		if (getValueString("ROLE_ID").equals("")) {
			this.messageBox("角色不能为空");
			return false;
		}
		Timestamp begin = (Timestamp) getValue("ACTIVE_DATE");
		if (begin == null) {
			messageBox("生效日期不能为空");
			return false;
		}
		Timestamp end = (Timestamp) getValue("END_DATE");
		if (end == null) {
			messageBox("失效日期不能为空");
			return false;
		}
		if (end.compareTo(begin) <= 0) {
			this.messageBox("生效日期不能大于失效日期");
			return false;
		}
		int count = this.getValueInt("ABNORMAL_TIMES");
		if (count < 0) {
			this.messageBox("异常登陆次数不能小于0");
			return false;
		}
		return true;
	}

	/**
	 * 得到页面中Table对象
	 *
	 * @param tag
	 * @return
	 */
	private TTable getTable(String tag) {
		return (TTable) callFunction("UI|" + tag + "|getThis");
	}

	/**
	 * 得到最大的编号 +1
	 *
	 * @param dataStore
	 *            TDataStore
	 * @param columnName
	 *            String
	 * @return String
	 */
	public int getMaxSeq(TDataStore dataStore, String columnName,
			String dbBuffer) {
		if (dataStore == null)
			return 0;
		// 保存数据量
		int count = dataStore.getBuffer(dbBuffer).getCount();
		// 保存最大号
		int max = 0;
		for (int i = 0; i < count; i++) {
			int value = TCM_Transform.getInt(dataStore.getItemData(i,
					columnName, dbBuffer));
			// 保存最大值
			if (max < value) {
				max = value;
				continue;
			}
		}
		// 最大号加1
		max++;
		return max;
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

}
