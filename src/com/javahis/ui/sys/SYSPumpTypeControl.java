package com.javahis.ui.sys;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jdo.sys.Operator;
import jdo.sys.SYSPumpTypeTool;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.orm.tools.PinyinTool;

/**
 * <p>
 * title:���뷽ʽ�ֵ�
 * </p>
 * <p>
 * ContentDiscription:���뷽ʽ�ֵ�
 * </p>
 * <p>
 * Company:javahis
 * </p>
 * <p>
 * Time:20160526
 * </p>
 * 
 * @author wukai
 * 
 */
public class SYSPumpTypeControl extends TControl {

	private TTable table;
	private SYSPumpTypeTool tool;

	public void onInit() {
		super.onInit();
		tool = SYSPumpTypeTool.getNewInstance();
		initPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initPage() {
		table = (TTable) this.getComponent("TABLE");
		table.removeRowAll();
		table.setParmValue(tool.onQuery(new TParm()));
		TDataStore tds = new TDataStore();
		tds.setSQL("SELECT PUMP_CODE, PUMP_DESC, PY1, PY2, DESCRIPTION, "
                + "SEQ, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_PUMPTYPE ORDER BY PUMP_CODE");
		tds.retrieve();
		// ��ʼ��˳��ţ����ȡ��������˳���+1 delete�û�
		int seq = getMaxSeq(tds, "SEQ", tds.isFilter() ? tds.FILTER
				: tds.PRIMARY);
		this.setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String code = getText("PUMP_CODE");
		if (!StringUtils.isEmpty(code)) {
			parm.setData("PUMP_CODE", "%" + code + "%");
		}
		String desc = getText("PUMP_DESC");
		if (!StringUtils.isEmpty(desc)) {
			parm.setData("PUMP_DESC", "%" + desc + "%");
		}
		String py1 = getText("PY1");
		if (!StringUtils.isEmpty(py1)) {
			parm.setData("PY1", "%" + py1 + "%");
		}
		String py2 = getText("PY2");
		if (!StringUtils.isEmpty(py2)) {
			parm.setData("PY2", "%" + py2 + "%");
		}
		String description = getText("DESCRIPTION");
		if (!StringUtils.isEmpty(description)) {
			parm.setData("DESCRIPTION", "%" + description + "%");
		}
		TParm res = tool.onQuery(parm);
		if(res == null || res.getCount() <= 0) {
			this.messageBox("�˲�ѯ������������!");
		}
		table.setParmValue(res);
	}

	/**
	 * ����
	 */
	public void onSave() {
		int row = 0;
		TTextField code = getTextField("PUMP_CODE");
		boolean flg = code.isEnabled(); // �ܹ�����ȡ��
		TParm parm = new TParm();
		parm.setData("PUMP_CODE", getValueString("PUMP_CODE"));
		parm.setData("PUMP_DESC", getValueString("PUMP_DESC"));
		parm.setData("PY1", this.getText("PY1"));
		parm.setData("PY2", this.getText("PY2"));
		parm.setData("SEQ", this.getText("SEQ"));
		parm.setData("DESCRIPTION", this.getText("DESCRIPTION"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		parm.setData("OPT_TERM", Operator.getIP());
		if (!flg) { // ��������
			if (!checkData()) {
				return;
			}
			if (!tool.onUpdate(parm)) {
				this.messageBox("����ʧ�ܣ��������ݺ�����");
				return;
			}
			row = table.getSelectedRow();
		} else { // ����������
			if (!checkData()) {
				return;
			}
			if (!tool.onSave(parm)) {
				this.messageBox("����ʧ��!");
				return;
			}
			row = table.addRow();
		}
		table.setItem(row, "PUMP_CODE", parm.getData("PUMP_CODE"));
		table.setItem(row, "PUMP_DESC", parm.getData("PUMP_DESC"));
		table.setItem(row, "PY1", parm.getData("PY1"));
		table.setItem(row, "PY2", parm.getData("PY2"));
		table.setItem(row, "SEQ", parm.getData("SEQ"));
		table.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
		table.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
		table.setItem(row, "OPT_DATE", parm.getData("OPT_DATE"));
		table.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
		this.messageBox("����ɹ�");
		onClear();
	}

	/**
	 * ɾ��
	 */
	public void onDelete() {
		boolean flg = getTextField("PUMP_CODE").isEnabled();
		if (!flg) { // ѡ��һ������ɾ��
			int row = table.getSelectedRow();
			if (row != -1) {
				String code = table.getParmValue().getValue("PUMP_CODE", row);
				TParm parm = new TParm();
				parm.setData("PUMP_CODE", code);
				if (tool.onDelete(parm)) {
					table.removeRow(row);
				} else {
					this.messageBox("ɾ��ʧ��!");
				}
				onClear();
			}
		} else {
			this.messageBox("��ѡ��һ������ɾ��");
		}
	}

	/**
	 * ��ջ���
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "PUMP_CODE;PUMP_DESC;PY1;" + "PY2;SEQ;DESCRIPTION";
		clearValue(clearString);
		// �������,���»�ȡ���е�����
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL("SELECT PUMP_CODE, PUMP_DESC, PY1, PY2, DESCRIPTION, "
                + "SEQ, OPT_USER, OPT_DATE, OPT_TERM "
                + "FROM SYS_PUMPTYPE ORDER BY PUMP_CODE");
		dataStore.retrieve();
		int seq = getMaxSeq(dataStore, "SEQ",
				dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
		setValue("SEQ", seq);

		table.setSelectionMode(0);
		getTextField("PUMP_CODE").setEnabled(true);
		getTextField("PUMP_DESC").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
	}

	/**
	 * ��ȡTextField
	 * 
	 * @param tag
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * ��������Ƿ�Ϊ��
	 * 
	 * @return
	 */
	private boolean checkData() {
		if (StringUtils.isEmpty(getValueString("PUMP_CODE"))) {
			this.messageBox("���벻��Ϊ��");
			return false;
		}

		if (StringUtils.isEmpty(getValueString("PUMP_DESC"))) {
			this.messageBox("�������Ʋ���Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * �õ����ı�� +1
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
		// ����������
		int count = dataStore.getBuffer(dbBuffer).getCount();
		// ��������
		int max = 0;
		for (int i = 0; i < count; i++) {
			int value = TCM_Transform.getInt(dataStore.getItemData(i,
					columnName, dbBuffer));
			// �������ֵ
			if (max < value) {
				max = value;
				continue;
			}
		}
		// ���ż�1
		max++;
		return max;
	}
	
	/**
	 * Table����¼� ����ѡ���������䵽������
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getParmValue().getRow(row);
			String linkNames = "PUMP_CODE;PUMP_DESC;PY1;"
					+ "PY2;SEQ;DESCRIPTION";
			this.setValueForParm(linkNames, parm);
			getTextField("PUMP_CODE").setEnabled(false);
			getTextField("PUMP_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
		}
	}
	
	/**
	 * ���������������
	 */
	public void onDescFinish() {
		// this.messageBox("name finish");
		String desc = this.getValueString("PUMP_DESC");
		if (desc == null) {
			return;
		} else {
			String py1 = PinyinTool.getPinyin(desc);
			this.setValue("PY1", py1);
		}
	}
}
