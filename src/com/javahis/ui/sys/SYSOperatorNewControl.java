package com.javahis.ui.sys;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;

public class SYSOperatorNewControl extends TControl {

	private static final String USER_NAME = "USER_NAME";
	private static final String USER_ID = "USER_ID";
	private static final String DEPT_CODE = "DEPT_CODE";
	private static final String REGION_CODE = "REGION_CODE";
	private static final String TABLE = "TABLE";
	private static final String TABLEDEPT = "TABLEDEPT";
	private static final String TABLELISCENSE = "TABLELISCENSE";
	private static final String POS_CODE = "POS_CODE";
	private static final String ROLE_ID = "ROLE_ID";
	private static int selectRow = -1;

	public SYSOperatorNewControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 给TABLEDEPT添加侦听事件
		callFunction("UI|TABLEDEPT|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableDeptCheckBoxClicked");
		addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (getValueString(DEPT_CODE).length() == 0) {
			onFilter();
			return;
		}
		onComboBoxSelected();
	}

	/**
	 * 过滤条件
	 */
	public void onFilter() {
		String value = "";
		String str = getValueString(USER_NAME);
		if (str.length() != 0)
			value += "USER_NAME like '" + str + "%'";
		str = getValueString(USER_ID);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "USER_ID like '" + str + "%'";
		}
		str = getValueString(REGION_CODE);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "REGION_CODE = '" + str + "'";
		}
		str = getValueString(POS_CODE);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "POS_CODE = '" + str + "'";
		}
		str = getValueString(ROLE_ID);
		if (str.length() != 0) {
			if (value.length() != 0)
				value += " AND ";
			value += "ROLE_ID = '" + str + "'";
		}
		TTable table1 = getTable(TABLE);
		if (value.length() > 0) {
			table1.setFilter(value);
			table1.filter();
			return;
		}
		table1.setFilter("");
		table1.filter();
	}

	/**
	 * 科室ComboBox选中事件
	 */
	public void onComboBoxSelected() {
		String dept = getValueString(DEPT_CODE);
		TDataStore st = new TDataStore();
		String sql = "SELECT USER_ID,DEPT_CODE FROM SYS_OPERATOR_DEPT WHERE DEPT_CODE = '"
				+ dept + "'";
		st.setSQL(sql);
		st.retrieve();
		int count = st.rowCount();
		String sql2 = "SELECT * FROM SYS_OPERATOR " + "WHERE USER_ID IN ("
				+ "'" + st.getItemData(0, USER_ID) + "'";
		for (int i = 1; i < count; i++) {
			sql2 = sql2 + ",'" + st.getItemData(i, USER_ID) + "'";
		}
		sql2 += ")";
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(sql2);
		dataStroe.retrieve();
		TTable table1 = getTable(TABLE);
		table1.setDataStore(dataStroe);
		table1.setDSValue();
		onFilter();
	}

	/**
	 * 清除方法
	 */
	public void onClear() {
		setValue(USER_NAME, "");
		setValue(USER_ID, "");
		setValue(DEPT_CODE, "");
		setValue(REGION_CODE, "");
		setValue("POS_CODE", "");
		setValue("ROLE_ID", "");
		setValue("ACTIVE_DATE", "");
		setValue("END_DATE", "");
		setValue("RCNT_LOGIN_DATE", "");
		setValue("RCNT_LOGOUT_DATE", "");
		setValue("RCNT_IP", "");
		setValue("PUB_FUNCTION", "");
		setValue("LCS_CLASS_CODE", "");
		setValue("LCS_NO", "");
		setValue("EFF_LCS_DATE", "");
		setValue("END_LCS_DATE", "");
		// 清空科室信息表
		getTable(TABLEDEPT).removeRowAll();
		// 清空证照信息表
		getTable(TABLELISCENSE).removeRowAll();
		// 清空科室列表
		getTable("TABLE2").removeRowAll();
		onFilter();
	}

	/**
	 * 保存方法
	 * 
	 * @return
	 */
	public boolean onSave() {
		// 保存科室
		TTable table = getTable(TABLEDEPT);
		TTable table1 = getTable(TABLE);
		Timestamp date = StringTool.getTimestamp(new Date());
		if (table.getDataStore().isModified()) {
			String userId = (String) table1.getItemData(selectRow, "USER_ID");
			table.acceptText();
			TDataStore dataStore = table.getDataStore();
			// 获得全部改动的行号
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			for (int i = 0; i < rows.length; i++) {
				dataStore.setItem(rows[i], "USER_ID", userId);
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			if (!table.update()) {
				messageBox("E0001");
				return false;
			}
			messageBox("P0001");
			table.setDSValue();
			return true;
		}
		// 保存使用者
		if (table1.getDataStore().isModified()) {
			this.messageBox("使用者");
			table1.acceptText();
			TDataStore dataStore = table1.getDataStore();
			// 获得全部改动的行号
			int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
			for (int i = 0; i < rows.length; i++) {
				/////////////////////////////////////////////////////
				dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
				dataStore.setItem(rows[i], "OPT_DATE", date);
				dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
			}
			if (!table1.update()) {
				messageBox("E0001");
				return false;
			}
			messageBox("P0001");
			table1.setDSValue();
			return true;
		}
		return true;
	}

	/**
	 * Table1单击事件
	 */
	public void onTable1Clicked() {
		// 清空证照信息
		setValue("LCS_CLASS_CODE", "");
		setValue("LCS_NO", "");
		setValue("EFF_LCS_DATE", "");
		setValue("END_LCS_DATE", "");
		// 得到被选中行
		TParm parm = getTable(TABLE).getDataStore().getRowParm(
				getTable(TABLE).getSelectedRow());
		String userId = parm.getValue(USER_ID);
		// 更改TABLE后确认是否保存
		if (getTable(TABLEDEPT).getDataStore().isModified()) {
			switch (messageBox("提示信息", "是否保存?", YES_NO_OPTION)) {
			case 0:
				if (!this.onSave())
					// 更改被选中行
					selectRow = getTable(TABLE).getSelectedRow();
				break;
			case 1:
				break;
			}
		}
		// 更改被选中行
		selectRow = getTable(TABLE).getSelectedRow();
		// 查询指定用户的所属科室
		TDataStore dataStroe1 = new TDataStore();
		String sql1 = "SELECT * FROM SYS_OPERATOR_DEPT WHERE USER_ID = '"
				+ userId + "'";
		dataStroe1.setSQL(sql1);
		dataStroe1.retrieve();
		TTable table = getTable(TABLEDEPT);
		table.setDataStore(dataStroe1);
		table.setDSValue();
		dataStroe1.showDebug();
		// 查询指定用户的证照信息
		TDataStore dataStroe2 = new TDataStore();
		String sql2 = "SELECT * FROM SYS_LICENSE_DETAIL WHERE USER_ID = '"
				+ userId + "'";
		dataStroe2.setSQL(sql2);
		dataStroe2.retrieve();
		TTable table2 = getTable(TABLELISCENSE);
		table2.setDataStore(dataStroe2);
		table2.setDSValue();
		// 显示基本信息
		setValue("USER_ID", parm.getData("USER_ID"));
		setValue("USER_NAME", parm.getData("USER_NAME"));
		// 获得主科室及所属科室
		String main = "";
		String dept = "";
		for (int i = 0; i < dataStroe1.rowCount(); i++) {
			if (dataStroe1.getRowParm(i).getBoolean("MAIN_FLG"))
				main = dataStroe1.getRowParm(i).getValue("DEPT_CODE");
			dept += "'" + dataStroe1.getRowParm(i).getValue("DEPT_CODE") + "',";
		}
		setValue("DEPT_CODE", main);
		setValue("REGION_CODE", parm.getData("REGION_CODE"));
		// 显示科室列表
		String sql3 = "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT";
		if (dept.length() > 0) {
			dept = dept.substring(0, dept.length() - 1);
			sql3 += " WHERE DEPT_CODE NOT IN ( " + dept + " )";
		}
		TDataStore dataStroe3 = new TDataStore();
		dataStroe3.setSQL(sql3);
		dataStroe3.retrieve();
		TTable table3 = getTable("TABLE2");
		table3.setDataStore(dataStroe3);
		table3.setDSValue();
		// 显示权限分配信息
		setValue("POS_CODE", parm.getData("POS_CODE"));
		setValue("ROLE_ID", parm.getData("ROLE_ID"));
		setValue("ACTIVE_DATE", parm.getData("ACTIVE_DATE"));
		setValue("END_DATE", parm.getData("END_DATE"));
		setValue("RCNT_LOGIN_DATE", parm.getData("RCNT_LOGIN_DATE"));
		setValue("RCNT_LOGOUT_DATE", parm.getData("RCNT_LOGOUT_DATE"));
		setValue("RCNT_IP", parm.getValue("RCNT_IP"));
		setValue("PUB_FUNCTION", parm.getValue("PUB_FUNCTION"));
	}

	/**
	 * TableLiscense单击事件
	 */
	public void onTableLiscenseClicked() {
		TParm parm = getTable(TABLELISCENSE).getDataStore().getRowParm(
				getTable(TABLELISCENSE).getSelectedRow());
		setValue("LCS_CLASS_CODE", parm.getData("LCS_CLASS_CODE"));
		setValue("LCS_NO", parm.getData("LCS_NO"));
		setValue("EFF_LCS_DATE", parm.getData("EFF_LCS_DATE"));
		setValue("END_LCS_DATE", parm.getData("END_LCS_DATE"));
	}

	/**
	 * 单击INSERT_DEPT按钮
	 */
	public void onInsertDeptClicked() {
		if (getTable("TABLE2").getSelectedRow() == -1)
			return;
		int newRow = getTable(TABLEDEPT).addRow();
		String data = getTable("TABLE2").getDataStore().getItemString(
				getTable("TABLE2").getSelectedRow(), "DEPT_CODE");
		String userId = getTable("TABLE2").getDataStore().getItemString(
				getTable("TABLE2").getSelectedRow(), "USER_ID");
		getTable(TABLEDEPT).setItem(newRow, "MAIN_FLG", "N");
		getTable(TABLEDEPT).setItem(newRow, "DEPT_CODE", data);
		getTable(TABLEDEPT).setItem(newRow, "USER_ID", userId);
		getTable("TABLE2").removeRow(getTable("TABLE2").getSelectedRow());
	}

	/**
	 * 单击REMOVE_DEPT按钮
	 */
	public void onRemoveDeptClicked() {
		if (getTable(TABLEDEPT).getSelectedRow() == -1)
			return;
		int newRow = getTable("TABLE2").addRow();
		String data = getTable(TABLEDEPT).getDataStore().getItemString(
				getTable(TABLEDEPT).getSelectedRow(), "DEPT_CODE");
		getTable("TABLE2").setItem(newRow, "DEPT_CODE", data);
		getTable("TABLE2").setItem(newRow, "DEPT_CHN_DESC", data);
		getTable(TABLEDEPT).removeRow(getTable(TABLEDEPT).getSelectedRow());
	}

	/**
	 * TABLEDEPT复选框改变事件
	 */
	public void onTableDeptCheckBoxClicked(Object obj) {
		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的行
		int row = tableDown.getSelectedRow();
		// 取消所有选中
		for (int i = 0; i < tableDown.getRowCount(); i++) {
			tableDown.setValueAt(false, i, 0);
		}
		// 选择被选中行
		tableDown.setValueAt(true, row, 0);
	}

	/**
	 * 添加一行新的数据事件
	 */
	public void onNew() {
		String userId = getValueString(USER_ID).trim();
		String userName = getValueString(USER_NAME).trim();
		if (userId.length() == 0) {
			this.messageBox("请先输入使用者");
			return;
		}
		TTable table = getTable(TABLE);
		if (table.getDataStore().exist("USER_ID='" + userId + "'")) {
			this.messageBox("使用者已存在");
			return;
		}
		if (userName.length() == 0) {
			this.messageBox("请先输入姓名");
			return;
		}
		// this.onClear();
		int row = table.addRow();
		table.setItem(row, "USER_ID", userId);
		table.setItem(row, "USER_NAME", userName);
		// 给拼音
		String py = TMessage.getPy(userName);
		table.setItem(row, "PY1", py);
		table.setItem(row, "OPT_USER", Operator.getID());
		table.setItem(row, "OPT_DATE", StringTool.getTimestamp(new Date()));
		table.setItem(row, "OPT_TERM", Operator.getIP());
	}

	/**
	 * 添加新的证照
	 */
	public void onInsertLiscenseClicked() {
		if (getTable(TABLE).getSelectedRow() == -1) {
			this.messageBox("请选择使用者");
			return;
		}
		String code = getValueString("LCS_CLASS_CODE").trim();
		TTable table = getTable(TABLELISCENSE);
		if (table.getDataStore().exist("LCS_CLASS_CODE='" + code + "'")) {
			this.messageBox("证照已存在");
			return;
		}
		if (getValueString("LCS_CLASS_CODE").trim().length() == 0) {
			this.messageBox("证照类别不能为空");
			return;
		}
		if (getValueString("LCS_NO").trim().length() == 0) {
			this.messageBox("证照号码不能为空");
			return;
		}
		Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
		if (begin == null) {
			this.messageBox("证照起日不能为空");
			return;
		}
		Timestamp end = (Timestamp) getValue("END_LCS_DATE");
		if (end == null) {
			this.messageBox("证照讫日不能为空");
			return;
		}
		if (end.compareTo(begin) <= 0) {
			this.messageBox("证照起日不能大于证照讫日");
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
		TTable table = getTable(TABLELISCENSE);
		int row = table.getSelectedRow();
		if (row == -1) {
			this.messageBox("请选择证照");
			return;
		}
		table.removeRow(row);
	}

	/**
	 * 更新证照
	 */
	public void onUpdateLiscenseAction() {
		TTable table = getTable(TABLELISCENSE);
		int row = table.getSelectedRow();
		String oldvalue = (String) table.getItemData(row, "LCS_CLASS_CODE");
		String value = getValueString("LCS_CLASS_CODE").trim();
		if (!oldvalue.equals(value)) {
			if (table.getDataStore().exist("LCS_CLASS_CODE='" + value + "'")) {
				this.messageBox("证照已存在");
				return;
			}
			table.setItem(row, "LCS_CLASS_CODE", value);
		}
		oldvalue = (String) table.getItemData(row, "LCS_NO");
		value = getValueString("LCS_NO").trim();
		if (!oldvalue.equals(value)) {
			table.setItem(row, "LCS_NO", value);
		}
		Timestamp begin = (Timestamp) getValue("EFF_LCS_DATE");
		Timestamp end = (Timestamp) getValue("END_LCS_DATE");
		if (end.compareTo(begin) <= 0) {
			this.messageBox("证照起日不能大于证照讫日");
			return;
		}
		table.setItem(row, "EFF_LCS_DATE", begin);
		table.setItem(row, "END_LCS_DATE", end);
	}

	/**
	 * 保存证照
	 */
	public boolean onSaveLiscenseClicked() {
		TTable table1 = getTable(TABLE);
		String userId = (String) table1.getItemData(table1.getSelectedRow(),
				"USER_ID");
		Timestamp date = StringTool.getTimestamp(new Date());
		TTable table = getTable(TABLELISCENSE);
		table.acceptText();
		TDataStore dataStore = table.getDataStore();
		// 获得全部改动的行号
		int rows[] = dataStore.getModifiedRows(dataStore.PRIMARY);
		for (int i = 0; i < rows.length; i++) {
			dataStore.setItem(rows[i], "USER_ID", userId);
			dataStore.setItem(rows[i], "OPT_USER", Operator.getID());
			dataStore.setItem(rows[i], "OPT_DATE", date);
			dataStore.setItem(rows[i], "OPT_TERM", Operator.getIP());
		}

		if (!table.update()) {
			messageBox("E0001");
			return false;
		}
		messageBox("P0001");
		table.setDSValue();
		return true;
	}

	// /**
	// * 显示所有科室信息
	// */
	// public void showDeptAll() {
	// String sql = "SELECT DEPT_CODE,DEPT_CHN_DESC FROM SYS_DEPT";
	// TDataStore dataStroe = new TDataStore();
	// dataStroe.setSQL(sql);
	// dataStroe.retrieve();
	// TTable table = getTable("TABLE2");
	// table.setDataStore(dataStroe);
	// table.setDSValue();
	// }

	/**
	 * TABLE值改变事件
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onTableChangeValue(Object obj) {
		// 拿到table值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 如果改变的节点数据和原来的数据相同就不改任何数据
		if (node.getValue().equals(node.getOldValue())) {
			this.messageBox(node.getValue() + ":" + node.getOldValue());
			return true;
		}
		// 拿到table上的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		// 拿到当前改变后的数据
		String value = "" + node.getValue();
		int row = node.getRow();
		// 身份证号
		if ("ID_NO".equals(columnName)) {
			// 身份证号验证
			if (StringTool.isId(value))
				node.getTable().setItem(row, "SEX_CODE",
						StringTool.isMaleFromID(value));
		}
		return false;
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

}
