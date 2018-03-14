package com.javahis.ui.sys;

import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jdo.clp.CLPCliProjectTool;
import jdo.sys.ClinicalSortTool;
import jdo.sys.Operator;
import jdo.sys.SYSSQL;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import com.javahis.ui.sys.orm.tools.PinyinTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * title:�ٴ��о�����
 * </p>
 * <p>
 * ContentDiscription:�ٴ��о�����
 * </p>
 * <p>
 * Company:javahis
 * </p>
 * <p>
 * Time:20160519
 * </p>
 * 
 * @author wukai
 * 
 */
public class ClinicalSortControl extends TControl {

	/**
	 * �������
	 */
	private static final String TABLE = "TABLE";
	/**
	 * ��������
	 */
	private String action = "save";
	/**
	 * �ٴ������
	 */
	private TTable table;

	private ClinicalSortTool tool;

	@Override
	public void onInit() {
		super.onInit();
		tool = ClinicalSortTool.getNewInstance();
		initPage();
	}

	/**
	 * ��ʼ��ҳ��
	 */
	private void initPage() {
		// ��ȡSYS_CLINICALSORT�����е����ݲ��ӵ�table��
		table = getTable(TABLE);
		table.removeRowAll();
		table.setParmValue(tool.onQuery(new TParm()));
		TDataStore tds = new TDataStore();
		tds.setSQL(SYSSQL.getSYSClinicalSQL());
		tds.retrieve();
		// ��ʼ��˳��ţ����ȡ��������˳���+1 delete�û�
		int seq = getMaxSeq(tds, "SEQ", tds.isFilter() ? tds.FILTER
				: tds.PRIMARY);
		this.setValue("SEQ", seq);
		((TMenuItem) getComponent("delete")).setEnabled(false);
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
	 * ��ȡҳ����
	 * 
	 * @param table
	 * @return
	 */
	private TTable getTable(String table) {
		return (TTable) getComponent(table);
	}

	/**
	 * ���»��߲��������� ��������ԭʼSQL������ķ�ʽ��
	 */
	public void onSave() {
		int row = 0;
		if ("save".equals(action)) { // ����
			TTextField code = getTextField("CLINICAL_CODE");
			boolean flg = code.isEnabled(); // �ܹ�����ȡ��
			TParm parm = new TParm();
			parm.setData("CLINICAL_CODE", getValueString("CLINICAL_CODE"));
			parm.setData("CLINICAL_DESC", getValueString("CLINICAL_DESC"));
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

					this.messageBox("����ʧ�ܣ����벻���ظ�");
					return;
				}
				row = table.addRow();
			}
			table.setItem(row, "CLINICAL_CODE", parm.getData("CLINICAL_CODE"));
			table.setItem(row, "CLINICAL_DESC", parm.getData("CLINICAL_DESC"));
			table.setItem(row, "PY1", parm.getData("PY1"));
			table.setItem(row, "PY2", parm.getData("PY2"));
			table.setItem(row, "SEQ", parm.getData("SEQ"));
			table.setItem(row, "DESCRIPTION", parm.getData("DESCRIPTION"));
			table.setItem(row, "OPT_USER", parm.getData("OPT_USER"));
			table.setItem(row, "OPT_DATE", parm.getData("OPT_DATE"));
			table.setItem(row, "OPT_TERM", parm.getData("OPT_TERM"));
			this.messageBox("����ɹ�");
		}
		onClear();
	}

	/**
	 * ��ѯlike
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String code = getText("CLINICAL_CODE");
		if (!StringUtils.isEmpty(code)) {
			parm.setData("CLINICAL_CODE", "%" + code + "%");
		}
		String desc = getText("CLINICAL_DESC");
		if (!StringUtils.isEmpty(desc)) {
			parm.setData("CLINICAL_DESC", "%" + desc + "%");
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
		// this.messageBox(res.toString());
		if(res == null || res.getCount() <=0) {
			this.messageBox("���޴˲�ѯ���ݣ�");
		}
		table.setParmValue(res);
	}

	/**
	 * ��������Ƿ�Ϊ��
	 * 
	 * @return
	 */
	private boolean checkData() {
		if (StringUtils.isEmpty(getValueString("CLINICAL_CODE"))) {
			this.messageBox("���벻��Ϊ��");
			return false;
		}

		if (StringUtils.isEmpty(getValueString("CLINICAL_DESC"))) {
			this.messageBox("�������Ʋ���Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * ������ݣ�ɾ���û�
	 */
	public void onClear() {
		// ��ջ�������
		String clearString = "CLINICAL_CODE;CLINICAL_DESC;PY1;"
				+ "PY2;SEQ;DESCRIPTION";
		clearValue(clearString);
		// �������,���»�ȡ���е�����
		TDataStore dataStore = new TDataStore();
		dataStore.setSQL(SYSSQL.getSYSClinicalSQL());
		dataStore.retrieve();
		int seq = getMaxSeq(dataStore, "SEQ",
				dataStore.isFilter() ? dataStore.FILTER : dataStore.PRIMARY);
		setValue("SEQ", seq);

		table.setSelectionMode(0);
		getTextField("CLINICAL_CODE").setEnabled(true);
		getTextField("CLINICAL_DESC").setEnabled(true);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		action = "save";
	}

	/**
	 * ��ȡTTextField
	 * 
	 * @param tag
	 *            : ��ǩ
	 * @return
	 */
	private TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * ɾ������ alter by wukai on 20160530
	 */
	public void onDelete() {
		boolean flg = getTextField("CLINICAL_CODE").isEnabled();
		if (!flg) { // ѡ��һ������ɾ��
			int row = table.getSelectedRow();
			String code = table.getParmValue().getValue("CLINICAL_CODE", row);
			// �ж���Ŀ���ݿ����Ƿ��Ѿ��������code
			TParm p = CLPCliProjectTool.getNewInstance().onQuery(new TParm());
			for (int i = 0; i < p.getCount(); i++) {
				Object o = p.getData("CLASSIFY_CODE", i);
				if(code.equals(o)) {
					this.messageBox("�ٴ���Ŀ���Ѿ�ʹ���˸ô���������ɾ��");
					return;
				}
			}
			int res = this.messageBox("��ʾ", "ȷ��ɾ����", YES_NO_OPTION);
			if (res == OK_OPTION) {
				if (row != -1) {
					TParm parm = new TParm();
					parm.setData("CLINICAL_CODE", code);
					if (tool.onDelete(parm)) {
						table.removeRow(row);
						onClear();
						this.messageBox("ɾ���ɹ�");
					}
				}
			} else {
				return;
			}
		} else {
			this.messageBox("��ѡ��һ������ɾ��");
		}

	}

	/**
	 * Table����¼� ����ѡ���������䵽������
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getParmValue().getRow(row);
			String linkNames = "CLINICAL_CODE;CLINICAL_DESC;PY1;"
					+ "PY2;SEQ;DESCRIPTION";
			this.setValueForParm(linkNames, parm);
			getTextField("CLINICAL_CODE").setEnabled(false);
			getTextField("CLINICAL_DESC").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			action = "save";
		}
	}

	/**
	 * ���������������
	 */
	public void onDescFinish() {
		// this.messageBox("name finish");
		String desc = this.getValueString("CLINICAL_DESC");
		if (desc == null) {
			return;
		} else {
			String py1 = PinyinTool.getPinyin(desc);
			this.setValue("PY1", py1);
		}
	}
}
