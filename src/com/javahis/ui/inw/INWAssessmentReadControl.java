package com.javahis.ui.inw;

import org.apache.commons.lang.StringUtils;

import jdo.inw.INWAssessmentReadTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.TMessage;

/**
 * <p>
 * Title: ����������
 * </p>
 * 
 * <p>
 * Description: ����������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author huzc 2015.10.22
 * @version 1.0
 */

public class INWAssessmentReadControl extends TControl {
	// ����TTable
	private TTable table;
	private TComboBox combobox;

	public INWAssessmentReadControl() {
	}

	/**
	 * ��ʼ��
	 * 
	 * @author Huzc
	 */
	public void onInit() {
		// ��ȡTable�����
		table = (TTable) this.getComponent("TABLE");
		combobox = (TComboBox) this.getComponent("LOGIC1");
		// ��ӵ�����Ӧ�¼�
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		// Ĭ�ϲ�ѯ��������
		onQuery();
	}

	/**
	 * ��ѯ����
	 * 
	 * @author huzc
	 */
	public void onQuery() {
		String sql = "SELECT * FROM SYS_EVALUTION_DICT ORDER BY EVALUTION_CODE";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		table.setParmValue(parm);
	}

	/**
	 * ���ƻس��¼�������ƴ����
	 * 
	 * @author Huzc
	 */
	public void onAssessmentReadChDescAction() {
		String py = TMessage.getPy(this.getValueString("EVALUTION_DESC"));
		setValue("PY", py);
	}

	/**
	 * ��񵥻���Ӧ�¼�
	 * 
	 * @author Huzc
	 */
	public void onTableClicked(int row) {
		// ��ȡ�õ�ֵ����TParm���͵�parm����
		TParm parm = table.getParmValue().getRow(row);
		// ��Parm�е�ֵ����ÿ���ؼ�
		if (StringUtils.isEmpty(parm.getValue("LOGIC1"))) {
			setValueForParm(
					"EVALUTION_CODE;EVALUTION_DESC;SHORT_DESC;PY;EVALUTION_CLASS;SCORE1;SCORE_DESC;OPT_USER;OPT_DATE;OPT_TERM",
					parm);
			combobox.setSelectedID(null);
		} else {
			setValueForParm(
					"EVALUTION_CODE;EVALUTION_DESC;SHORT_DESC;PY;EVALUTION_CLASS;LOGIC1;SCORE1;SCORE_DESC;OPT_USER;OPT_DATE;OPT_TERM",
					parm);
		}
		// ������������Ϊ���ɱ༭
		TTextField text = (TTextField) getComponent("EVALUTION_CODE");
		text.setEditable(false);
	}

	/**
	 * ��շ���
	 * 
	 * @author Huzc
	 */
	public void onClear() {
		// ����ؼ�����ʾ������
		this
				.clearValue("EVALUTION_CODE;EVALUTION_DESC;SHORT_DESC;PY;EVALUTION_CLASS;SCORE1;SCORE_DESC;OPT_USER;OPT_DATE;OPT_TERM");
		combobox.setSelectedID(null);
		// ��ձ��
		table.removeRowAll();
		// ���������ſؼ���Ϊ�ɱ༭
		TTextField text = (TTextField) getComponent("EVALUTION_CODE");
		text.setEditable(true);
		return;
	}

	/**
	 * ��������
	 * 
	 * @author huzc
	 */

	public void onNew() {
		String evalutioncode = getValueString("EVALUTION_CODE");
		String evalutiondesc = getValueString("EVALUTION_DESC");
		String score1 = getValueString("SCORE1");
		String logic1 = combobox.getSelectedName();

		if (StringUtils.isEmpty(evalutioncode)) {
			this.messageBox("�������Ų���Ϊ�գ�");
			return;
		} else if (StringUtils.isEmpty(evalutiondesc)) {
			this.messageBox("���Ʋ���Ϊ�գ�");
			return;
		} else if (StringUtils.isEmpty(score1)) {
			this.messageBox("��������Ϊ�գ�");
			return;
		} else if (StringUtils.isEmpty(logic1)) {
			this.messageBox("�߼�����Ϊ�գ�");
			return;
		} else {
			String shortdesc = getValueString("SHORT_DESC");
			String py = getValueString("PY");
			String evalutionclass = getValueString("EVALUTION_CLASS");
			String scoredesc = getValueString("SCORE_DESC");

			TParm parm = new TParm();

			parm.setData("EVALUTION_DESC", evalutiondesc);
			parm.setData("SHORT_DESC", shortdesc);
			parm.setData("PY", py);
			parm.setData("EVALUTION_CLASS", evalutionclass);
			parm.setData("LOGIC1", logic1);
			parm.setData("SCORE1", score1);
			parm.setData("SCORE_DESC", scoredesc);
			parm.setData("OPT_USER", Operator.getName());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("OPT_DATE", SystemTool.getInstance().getDate());

			// �жϵ����Ƿ��ظ�
			String sql = " SELECT * FROM SYS_EVALUTION_DICT WHERE EVALUTION_CODE = '"
					+ evalutioncode + "' ";
			TParm parm1 = new TParm(TJDODBTool.getInstance().select(sql));
			if (parm1.getCount() > 0) {
				this.messageBox("���������Ѵ��ڣ����������룡");
				return;
			} else {
				parm.setData("EVALUTION_CODE", evalutioncode);
			}

			TParm result = new TParm();
			result = INWAssessmentReadTool.getInstance()
					.insertINWAssessmentRead(parm);
			if (result.getErrCode() < 0) {
				this.messageBox("��ӳ���");
				err("ERR:" + result.getErrCode() + result.getErrName()
						+ result.getErrText());
				return;
			}
			this.messageBox("��ӳɹ�");
		}
		onQuery();
	}

	/**
	 * �޸ķ���
	 * 
	 * @author Huzc
	 */
	public void onSave(int row) {
		String evalutiondesc = getValueString("EVALUTION_DESC");
		String score1 = getValueString("SCORE1");
		String logic1 = combobox.getSelectedName();

		if (table.getSelectedRow() < 0) {
			this.messageBox("û��ѡ����Ҫ�޸ĵ����ݣ�");
			return;
		} else if (table.getParmValue() == null
				|| table.getParmValue().getCount() < 1) {
			this.messageBox("û�������������");
		} else if (StringUtils.isEmpty(evalutiondesc)) {
			this.messageBox("���Ʋ���Ϊ�գ�");
			return;
		} else if (StringUtils.isEmpty(score1)) {
			this.messageBox("��������Ϊ�գ�");
			return;
		} else if (StringUtils.isEmpty(logic1)) {
			this.messageBox("�߼�����Ϊ�գ�");
			return;
		} else {
			String evalutioncode = getValueString("EVALUTION_CODE");
			String shortdesc = getValueString("SHORT_DESC");
			String py = getValueString("PY");
			String evalutionclass = getValueString("EVALUTION_CLASS");
			String scoredesc = getValueString("SCORE_DESC");

			TParm parm = new TParm();

			parm.setData("EVALUTION_CODE", evalutioncode);
			parm.setData("EVALUTION_DESC", evalutiondesc);
			parm.setData("SHORT_DESC", shortdesc);
			parm.setData("PY", py);
			parm.setData("EVALUTION_CLASS", evalutionclass);
			parm.setData("LOGIC1", logic1);
			parm.setData("SCORE1", score1);
			parm.setData("SCORE_DESC", scoredesc);
			parm.setData("OPT_USER", Operator.getName());
			parm.setData("OPT_TERM", Operator.getIP());
			parm.setData("OPT_DATE", SystemTool.getInstance().getDate()
					.toString().substring(0, 18));

			INWAssessmentReadTool.getInstance().updateINWAssessmentRead(parm);

			if (parm.getErrCode() < 0) {
				this.messageBox("�޸ĳ���");
				err("ERR:" + parm.getErrCode() + parm.getErrName()
						+ parm.getErrText());
				return;
			} else {
				this.messageBox("�޸ĳɹ���");
			}
			onQuery();
		}

	}

	/**
	 * ɾ������
	 * 
	 * @author Huzc
	 */
	public void onDelete() {

		if (table.getSelectedRow() < 0) {
			this.messageBox("û��ѡ����Ҫɾ�������ݣ�");
			return;
		} else {
			String evalutioncode = getValueString("EVALUTION_CODE");
			String sql = " DELETE  FROM SYS_EVALUTION_DICT WHERE EVALUTION_CODE = '"
					+ evalutioncode + "'";
			TParm result = new TParm(TJDODBTool.getInstance().update(sql));
			if (result.getErrCode() < 0) {
				this.messageBox("ɾ��ʧ�ܣ�");
				err("ERR:" + result.getErrCode() + result.getErrName()
						+ result.getErrText());
				return;
			} else {
				this.messageBox("ɾ���ɹ���");
			}
			onQuery();
		}
	}
}
