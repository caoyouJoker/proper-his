package com.javahis.ui.sys;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;

public class SYSPYCODEControl extends TControl {

	public String[] tableNames;
	public String[] columnnames;
	public String[] columntypes;
	public TParm parm;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始化TABLENAME列表
		onClear();
		TComboBox table = getComboBox("TABLENAME");
		tableNames = TJDODBTool.getInstance().getTables();
		table.setVectorData(tableNames);
	}

	/**
	 * 过滤条件
	 */
	public void onFilter() {
		clearValue("DESC;PY");
		TComboBox table = getComboBox("TABLENAME");
		TComboBox desc = getComboBox("DESC");
		TComboBox py = getComboBox("PY");
		String tablename = table.getValue();
		columnnames = TJDODBTool.getInstance().getColumns(tablename);
		columntypes = TJDODBTool.getInstance().getColumnType(tablename,
				columnnames);
		parm = new TParm();
		for (int i = 0; i < columnnames.length; i++) {
			if ("VARCHAR2".equals(columntypes[i])) {
				parm.addData(columnnames[i], columntypes[i]);
			}
		}
		TParm pm = new TParm();
		if (parm.existData("PY1")) {
			pm.addData("PY1", "PY1");
			parm.removeData("PY1");
		}
		if (parm.existData("PY2")) {
			pm.addData("PY2", "PY2");
			parm.removeData("PY2");
		}
		py.setVectorData(pm.getNames());
		desc.setVectorData(parm.getNames());
	}

	/**
	 * 保存方法
	 */
	public boolean onSave() {
		// 检查数据非空
		if (!Check_Data())
			return false;
		String sql = "UPDATE " + getValueString("TABLENAME") + " SET "
				+ getValueString("PY") + " = py(" + getValueString("DESC")
				+ ")";
		TParm update = new TParm();
		update.setData("UPDATE_PY", sql);
		TParm result = TIOM_AppServer.executeAction(
				"action.sys.SYSPYCODEAction", "onUpdate", update);
		if (result.getErrCode() < 0) {
			this.messageBox("更新失败！" + result.getErrName()
					+ result.getErrText());
			return false;
		}
		this.messageBox("更新成功");
		return true;
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		String clearObj = "TABLENAME;DESC;PY";
		clearValue(clearObj);
		getComboBox("DESC").removeAllItems();
		getComboBox("PY").removeAllItems();
	}

	/**
	 * 检查数据非空
	 * 
	 * @return
	 */
	public boolean Check_Data() {
		if (this.getValueString("TABLENAME").equals("")) {
			this.messageBox("表名不能为空");
			return false;
		}
		if (this.getValueString("DESC").equals("")) {
			this.messageBox("DESC列不能为空");
			return false;
		}
		if (this.getValueString("PY").equals("")) {
			this.messageBox("PY列不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 获得下拉列表对象
	 * 
	 * @param tag
	 * @return
	 */
	public TComboBox getComboBox(String tag) {
		return (TComboBox) callFunction("UI|" + tag + "|getThis");
	}

}
