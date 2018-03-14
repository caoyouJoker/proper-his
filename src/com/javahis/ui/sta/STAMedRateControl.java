package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JComponent;

import org.apache.xmlbeans.impl.jam.JComment;

import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.ui.TDateEdit;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

public class STAMedRateControl extends TControl{
	
    
	private final String sql="SELECT MED_YEAR,MED_CLASS,RATE,OPT_USER,OPT_DATE,OPT_TERM FROM STA_MED_RATE ORDER BY MED_YEAR DESC,MED_CLASS";

	private String action = "save";
	// 主项表格
	private TTable table;

	public STAMedRateControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		int row = 0;
		Timestamp date = StringTool.getTimestamp(new Date());
		if ("save".equals(action)) {
			JComponent MED_YEAR = (JComponent)getComponent("MED_YEAR");
			boolean flg = MED_YEAR.isEnabled();
			if (flg) {
				if (!CheckData())
					return;
				row = table.addRow();
			} else {
				if (!CheckData())
					return;
				row = table.getSelectedRow();
			}
			table.setItem(row, "MED_YEAR",getValueString("MED_YEAR").substring(0, 4));
			String desc = getValueString("MED_CLASS");
			table.setItem(row, "MED_CLASS", desc);
			table.setItem(row, "RATE", getValueDouble("RATE"));
			table.setItem(row, "OPT_USER", Operator.getID());
			table.setItem(row, "OPT_DATE", date);
			table.setItem(row, "OPT_TERM", Operator.getIP());
		}
		TDataStore dataStore = table.getDataStore();
		if (dataStore.isModified()) {
			table.acceptText();
			if (!table.update()) {
				messageBox("E0001");
				table.removeRow(row);
				table.setDSValue();
				onClear();
				return;
			}
			table.setDSValue();
		}
		messageBox("P0001");
		table.setDSValue();
		onClear();
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		int row = table.getTable().getSelectedRow();
		if (row < 0)
			return;
		table.removeRow(row);
		TDataStore dataStore = table.getDataStore();
		if (dataStore.isModified()) {
			table.acceptText();
			if (!table.update()) {
				messageBox("E0003");
				table.removeRow(row);
				table.setDSValue();
				onClear();
				return;
			}
			table.setDSValue();
		}
		messageBox("P0003");
		table.setDSValue();
		((TMenuItem) getComponent("delete")).setEnabled(false);
		this.onClear();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		// 初始化Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();
		String MED_YEAR = getValueString("MED_YEAR").substring(0, 4);
		String MED_CLASS = getValueString("MED_CLASS");
		String filterString = "";
		if (MED_YEAR.length() > 0 && MED_CLASS.length() > 0)
			filterString += "MED_YEAR like '" + MED_YEAR
					+ "%' AND MED_CLASS like '" + MED_CLASS + "%'";
		else if (MED_YEAR.length() > 0)
			filterString += "MED_YEAR like '" + MED_YEAR + "%'";
		else if (MED_CLASS.length() > 0)
			filterString += "MED_CLASS like '" + MED_CLASS + "%'";
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		// 清空画面内容
		String clearString = "MED_YEAR;MED_CLASS;RATE;";
		clearValue(clearString);
		table.setSelectionMode(0);
		((JComponent)getComponent("MED_YEAR")).setEnabled(true);
		((JComponent)getComponent("MED_CLASS")).setEnabled(true);
		//((TNumberTextField)getComponent("RATE")).setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * TABLE单击事件
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getDataStore().getRowParm(row);
			String likeNames = "MED_YEAR;MED_CLASS;RATE;";
			this.setValueForParm(likeNames, parm);
			((JComponent)getComponent("MED_YEAR")).setEnabled(false);
			((JComponent)getComponent("MED_CLASS")).setEnabled(false);
			//((TNumberTextField)getComponent("RATE")).setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		// 初始化Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();
		((TMenuItem) getComponent("delete")).setEnabled(false);
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
	 * 检查数据
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("MED_YEAR"))) {
			this.messageBox("年份不能为空");
			return false;
		}
		if ("".equals(getValueString("MED_CLASS"))) {
			this.messageBox("分类不能为空");
			return false;
		}
		return true;
	}

	

}
