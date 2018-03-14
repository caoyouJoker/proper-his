package com.javahis.ui.sta;

import java.sql.Timestamp;
import java.util.Date;

import javax.swing.JComponent;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

public class STAMedPerformanceControl extends TControl {

	private final String sql = "SELECT MED_YEAR,MED_CLASS,PERFORMANCE_CLASS,DIFFICULTY_DEGREE,OPT_USER,OPT_DATE,OPT_TERM FROM STA_MED_PERFORMANCE ORDER BY MED_YEAR DESC,MED_CLASS";

	private String insertsql = "INSERT INTO STA_MED_PERFORMANCE(MED_YEAR, MED_CLASS,"
			+ " PERFORMANCE_CLASS,DIFFICULTY_DEGREE, OPT_USER,"
			+ " OPT_DATE, OPT_TERM)VALUES('<MED_YEAR>','<MED_CLASS>','<PERFORMANCE_CLASS>',"
			+ " '<DIFFICULTY_DEGREE>'," + " '<OPT_USER>',SYSDATE,'<OPT_TERM>')";

	private String action = "save";
	// ������
	private TTable table;

	public STAMedPerformanceControl() {
		super();
	}

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		initPage();
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		int row = 0;
		Timestamp date = StringTool.getTimestamp(new Date());
		if ("save".equals(action)) {
			JComponent MED_YEAR = (JComponent) getComponent("MED_YEAR");
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
			table.setItem(row, "MED_YEAR", getValueString("MED_YEAR")
					.substring(0, 4));
			String desc = getValueString("MED_CLASS");
			table.setItem(row, "MED_CLASS", desc);
			table.setItem(row, "PERFORMANCE_CLASS",
					getValueString("PERFORMANCE_CLASS"));
			table.setItem(row, "DIFFICULTY_DEGREE",
					getValueInt("DIFFICULTY_DEGREE"));
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
	 * ɾ������
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
	 * ��ѯ����
	 */
	public void onQuery() {
		// ��ʼ��Table
		table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(sql);
		dataStore.retrieve();
		table.setDataStore(dataStore);
		table.setDSValue();
		String MED_YEAR = getValueString("MED_YEAR").substring(0, 4);
		String MED_CLASS = getValueString("MED_CLASS");
		String PERFORMANCE_CLASS = getValueString("PERFORMANCE_CLASS");
		StringBuffer filterString = new StringBuffer();
		if (MED_YEAR.length() > 0) {
			filterString.append("MED_YEAR like '" + MED_YEAR + "%'");
		}
		if (MED_CLASS.length() > 0) {
			if(filterString.length()>0){
				filterString.append(" AND ");
			}
			filterString.append("MED_CLASS like '" + MED_CLASS + "%'");
		}
		if (PERFORMANCE_CLASS.length() > 0) {
			if(filterString.length()>0){
				filterString.append(" AND ");
			}
			filterString.append("PERFORMANCE_CLASS like '" + PERFORMANCE_CLASS + "%'");
		}
		table.setFilter(filterString.toString());
		table.filter();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "MED_YEAR;MED_CLASS;PERFORMANCE_CLASS;DIFFICULTY_DEGREE;";
		clearValue(clearString);
		table.setSelectionMode(0);
		((JComponent) getComponent("MED_YEAR")).setEnabled(true);
		((JComponent) getComponent("MED_CLASS")).setEnabled(true);
		((JComponent) getComponent("PERFORMANCE_CLASS")).setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getDataStore().getRowParm(row);
			String likeNames = "MED_YEAR;MED_CLASS;PERFORMANCE_CLASS;DIFFICULTY_DEGREE;";
			this.setValueForParm(likeNames, parm);
			((JComponent) getComponent("MED_YEAR")).setEnabled(false);
			((JComponent) getComponent("MED_CLASS")).setEnabled(false);
			((JComponent) getComponent("PERFORMANCE_CLASS")).setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʼ��Table
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
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �������
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("MED_YEAR"))) {
			this.messageBox("��ݲ���Ϊ��");
			return false;
		}
		if ("".equals(getValueString("MED_CLASS"))) {
			this.messageBox("���಻��Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * �����������
	 * 
	 * @return boolean
	 */
	public void onInsertYear() {
		String year = (String) this
				.openDialog("%ROOT%\\config\\sta\\STASelYearUI.x");
		if (year == null) {
			return;
		}
		String preYear = String.valueOf(Integer.parseInt(year) - 1);
		String sql = "SELECT MED_YEAR,MED_CLASS,PERFORMANCE_CLASS,DIFFICULTY_DEGREE "
				+ "FROM STA_MED_PERFORMANCE WHERE  MED_YEAR='" + preYear + "'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
			this.messageBox("δ��ѯ��" + preYear + "��ݵ��ֵ�����");
			return;
		}
		String delsql = "DELETE FROM STA_MED_PERFORMANCE WHERE  MED_YEAR='"
				+ year + "'";
		String[] str = new String[parm.getCount() + 1];
		str[0] = delsql;
		for (int i = 1; i < parm.getCount() + 1; i++) {
			str[i] = onCreateSql(parm.getRow(i - 1), year);
		}
		TParm result = new TParm(TJDODBTool.getInstance().update(str));
		if (result.getErrCode() < 0) {
			this.messageBox("��������");
			return;
		}
		this.messageBox("�����ɹ�");
		onQuery();
	}

	private String onCreateSql(TParm parmRow, String year) {
		parmRow.setData("MED_YEAR", year);
		parmRow.setData("OPT_USER", Operator.getID());
		parmRow.setData("OPT_TERM", Operator.getIP());
		String sql = buildSQL(this.insertsql, parmRow);
		return sql;
	}

	/**
	 * ����SQL���
	 * 
	 * @param SQL
	 *            ԭʼ���
	 * @param obj
	 *            �滻�����?����ֵ����
	 * @return SQL���
	 */
	public String buildSQL(String SQL, TParm parm) {
		SQL = SQL.trim();
		Object[] names = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			String name = (String) names[i];
			if (SQL.indexOf("<" + name.trim() + ">") == -1)
				continue;
			SQL = replace(SQL, "<" + name.trim() + ">",
					parm.getValue((String) names[i]));
		}
		return SQL;
	}

	public String replace(String s, String name, String value) {
		int index = s.indexOf(name);
		while (index >= 0) {
			s = s.substring(0, index) + value
					+ s.substring(index + name.length(), s.length());
			index = s.indexOf(name);
		}
		return s;

	}

}
