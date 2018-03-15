package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.ui.*;
import com.dongyang.util.*;
import com.javahis.system.combo.TComboOrgCode;
import com.javahis.system.textFormat.TextFormatDept;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Vector;
import jdo.spc.INDSQL;
import jdo.sys.Operator;

/**
 * <p>
 * Title:ҩ��ҩ���趨
 * </p>
 * 
 * <p>
 * Description:ҩ��ҩ���趨
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
 * @author zhangy 2009.4.22
 * @version 1.0
 */
public class INDOrgControl extends TControl {
	private Vector vct;

	/**
	 * ��ʼ������
	 */
	public INDOrgControl() {
	}

	public void onInit() {
		// ��ʼ��������
		initPage();
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		TTable table = getTable("TABLE");
		int row = 0;
		if (table.getSelectedRow() < 0) {
			// ��������
			if (!CheckData())
				return;
			row = table.addRow();
		} else {
			row = table.getSelectedRow();
		}
		table.setItem(row, "ORG_CODE", getValueString("ORG_CODE"));
		TTextFormat org_code = getTextFormat("ORG_CODE");
		table.setItem(row, "ORG_CHN_DESC", org_code.getText());
		String py = TMessage.getPy(org_code.getName());
		table.getDataStore().setItem(row, "PY1", py);
		table.setItem(row, "SEQ", getValueString("SEQ"));
		String org_type = "C";
		if (getRadioButton("ORG_TYPE_A").isSelected())
			org_type = "A";
		else if (getRadioButton("ORG_TYPE_B").isSelected())
			org_type = "B";
		else
			org_type = "C";
		table.setItem(row, "ORG_TYPE", org_type);
		table.setItem(row, "SUP_ORG_CODE", getValueString("SUP_ORG_CODE"));
		table.setItem(row, "REGION_CODE", getValueString("REGION_CODE"));
		table.setItem(row, "DECOCT_CODE", getValueString("DECOCT_CODE"));
		table.setItem(row, "STATION_FLG", getValueString("STATION_FLG"));
		table.setItem(row, "EXINV_FLG", getValueString("EXINV_FLG"));
		table.setItem(row, "INJ_ORG_FLG", getValueString("INJ_ORG_FLG"));
		table.setItem(row, "ATC_FLG", getValueString("ATC_FLG"));
		//
		table.setItem(row, "BOX_FLG", getValueString("BOX_FLG"));
		
		table.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));
		table.setItem(row, "OPT_USER", Operator.getID());
		table.setItem(row, "AUTO_FILL_TYPE", getValueString("AUTO_FILL_TYPE"));
		table
				.setItem(row, "FIXEDAMOUNT_FLG",
						getValueString("FIXEDAMOUNT_FLG"));
		Timestamp date = StringTool.getTimestamp(new Date());
		table.setItem(row, "OPT_DATE", date);
		table.setItem(row, "OPT_TERM", Operator.getIP());
		// <-- ����3���ֶε�ά�� update by shendr date:2013.5.24
		table.setItem(row, "IS_ACCOUNT", getValueString("IS_ACCOUNT"));
		table.setItem(row, "ATC_ORG_CODE", getValueString("ATC_ORG_CODE"));
		table.setItem(row, "IS_SUBORG", getValueString("IS_SUBORG"));
		// -->
		table.getDataStore().setItem(row, "ORG_FLG", "Y");
		if (!table.getDataStore().update()) {
			messageBox("E0001");
			return;
		} else {
			messageBox("P0001");
			table.setDSValue();
			vct = table.getDataStore().getVector("ORG_CODE");
			onClear();
			return;
		}
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		String region = getValueString("REGION_CODE");
		String type = "";
		if (getRadioButton("ORG_TYPE_A").isSelected())
			type = "A";
		else if (getRadioButton("ORG_TYPE_B").isSelected())
			type = "B";
		else
			type = "C";
		String org = getValueString("ORG_CODE");
		String filterString = (new StringBuilder("ORG_TYPE = '")).append(type)
				.append("'").toString();
		if (region.length() > 0)
			filterString = (new StringBuilder(String.valueOf(filterString)))
					.append(" AND REGION_CODE = '").append(region).append("'")
					.toString();
		if (org.length() > 0)
			filterString = (new StringBuilder(String.valueOf(filterString)))
					.append(" AND ORG_CODE = '").append(org).append("'")
					.toString();
		TTable table = getTable("TABLE");
		table.setFilter(filterString);
		table.filter();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		// ҩ������
		TTextFormat org_code = getTextFormat("ORG_CODE");
		org_code.setEnabled(true);
		org_code.setValue("");

		// �ⷿ����
		getRadioButton("ORG_TYPE_C").setSelected(true);
		onChangeOrgType();

		// ��½����������
		TComboBox region = getComboBox("REGION_CODE");
		region.setSelectedID(Operator.getRegion());

		// ���
		TDataStore dataStroe = getTable("TABLE").getDataStore();
		int seq = getMaxSeq(dataStroe, "SEQ", dataStroe.isFilter() ? "Filter!"
				: "Primary!");
		setValue("SEQ", Integer.valueOf(seq));

		// ��ע
		setValue("DESCRIPTION", "");
		setValue("FIXEDAMOUNT_FLG", "");
		setValue("AUTO_FILL_TYPE", "");

		// TABLE
		getTable("TABLE").setSelectionMode(0);
		// <-- ����3���ֶε�ά�� update by shendr date:2013.5.24
		getCheckBox("IS_ACCOUNT").setSelected(false);
		getTextFormat("ATC_ORG_CODE").setEnabled(false);
		getCheckBox("IS_SUBORG").setSelected(false);
		// -->
	}

	/**
	 * TABLE�����¼�
	 */
	public void onTableClicked() {
		TTable table = getTable("TABLE");
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = table.getDataStore().getRowParm(row);
			String likeNames = "ORG_CODE;REGION_CODE;DECOCT_CODE;SEQ;DESCRIPTION;DECOCT_CODE";
			setValueForParm(likeNames, parm);
			String org_type = parm.getValue("ORG_TYPE");
			if ("A".equals(org_type))
				getRadioButton("ORG_TYPE_A").setSelected(true);
			else if ("B".equals(org_type))
				getRadioButton("ORG_TYPE_B").setSelected(true);
			else
				getRadioButton("ORG_TYPE_C").setSelected(true);
			onChangeOrgType();
			getCheckBox("STATION_FLG").setSelected(
					TypeTool.getBoolean(parm.getValue("STATION_FLG")));
			getCheckBox("EXINV_FLG").setSelected(
					TypeTool.getBoolean(parm.getValue("EXINV_FLG")));
			getCheckBox("INJ_ORG_FLG").setSelected(
					TypeTool.getBoolean(parm.getValue("INJ_ORG_FLG")));
			getCheckBox("ATC_FLG").setSelected(
					TypeTool.getBoolean(parm.getValue("ATC_FLG")));
			//
			getCheckBox("BOX_FLG").setSelected(
					TypeTool.getBoolean(parm.getValue("BOX_FLG")));
			// <-- ����3���ֶε�ά�� update by shendr date:2013.5.24
			getCheckBox("IS_ACCOUNT").setSelected(
					TypeTool.getBoolean(parm.getValue("IS_ACCOUNT")));
			getCheckBox("IS_SUBORG").setSelected(
					TypeTool.getBoolean(parm.getValue("IS_SUBORG")));
			if (getCheckBox("ATC_FLG").isSelected())
				setValue("ATC_ORG_CODE", parm.getValue("ATC_ORG_CODE"));
			else
				getTextFormat("ATC_ORG_CODE").setValue("");
			// -->
			// ���ݿⷿ���ͻ�ʿվ����ҩ���б�(ORG_CODE)
			onSetOrgCodeByTypeAndStation(org_type, TypeTool.getBoolean(parm
					.getValue("STATION_FLG")));
			getComboBox("SUP_ORG_CODE").setSelectedID(
					parm.getValue("SUP_ORG_CODE"));
			getTextFormat("ORG_CODE").setValue(parm.getValue("ORG_CODE"));
			TextFormatDept decoct = (TextFormatDept) getComponent("DECOCT_CODE");
			decoct.setValue(parm.getValue("DECOCT_CODE"));
			setValue("AUTO_FILL_TYPE", parm.getValue("AUTO_FILL_TYPE"));
			setValue("FIXEDAMOUNT_FLG", parm.getValue("FIXEDAMOUNT_FLG"));
		}
	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		// ��ʼ��Table
		TTable table = getTable("TABLE");
		table.removeRowAll();
		TDataStore dataStroe = new TDataStore();
		dataStroe.setSQL(INDSQL.getINDORG(Operator.getRegion()));
		dataStroe.retrieve();
		table.setDataStore(dataStroe);
		table.setDSValue();
		// ��½����������
		TComboBox region = getComboBox("REGION_CODE");
		region.setSelectedID(Operator.getRegion());
		// ����+1(SEQ)
		int seq = getMaxSeq(dataStroe, "SEQ", dataStroe.isFilter() ? "Filter!"
				: "Primary!");
		setValue("SEQ", Integer.valueOf(seq));
		// ��ѯ���е����пⷿ
		vct = dataStroe.getVector("ORG_CODE");
		// �ⷿ���͸ı��¼�
		onChangeOrgType();
	}

	/**
	 * �ⷿ���͸ı��¼�
	 */
	public void onChangeOrgType() {
		// ����ҩ��SUP_ORG_CODE
		TComboOrgCode sup_code = (TComboOrgCode) getComboBox("SUP_ORG_CODE");
		// ORG_TYPE_A:���� ��ORG_TYPE_B���п� ��ORG_TYPE_C��С��
		if (getRadioButton("ORG_TYPE_A").isSelected()) {
			onSetStatus("N,N,N,N,N,N");
			sup_code.setOrgType("");
			// ���ݿⷿ�������ҩ���б�(ORG_CODE)
			onSetOrgCodeByOrgType("A");
		} else if (getRadioButton("ORG_TYPE_B").isSelected()) {
			onSetStatus("N,Y,N,Y,Y,Y");
			sup_code.setOrgType("A");
			// ���ݿⷿ�������ҩ���б�(ORG_CODE)
			onSetOrgCodeByOrgType("B");
		} else {
			onSetStatus("Y,Y,Y,N,N,N");
			sup_code.setOrgType("B");
			// ���ݿⷿ�������ҩ���б�(ORG_CODE)
			onSetOrgCodeByOrgType("C");
		}
		sup_code.onQuery();
	}

	/**
	 * ��ʿվ(CheckBox)�ı��¼�
	 */
	public void onSelectStation() {

		String org_type = "";

		if (getRadioButton("ORG_TYPE_A").isSelected())
			org_type = "A";
		else if (getRadioButton("ORG_TYPE_B").isSelected())
			org_type = "B";
		else
			org_type = "C";
		// ���ݿⷿ���ͻ�ʿվ����ҩ���б�(ORG_CODE)
		onSetOrgCodeByTypeAndStation(org_type, getCheckBox("STATION_FLG")
				.isSelected());
	}

	/**
	 * ���ݿⷿ�������ҩ���б�(ORG_CODE)
	 * 
	 * @param org_type
	 */
	private void onSetOrgCodeByOrgType(String org_type) {
		getTextFormat("ORG_CODE").setPopupMenuSQL(
				INDSQL.getOrgCodeByOrgType(org_type, Operator.getRegion()));
		getTextFormat("ORG_CODE").onQuery();
		getTextFormat("ORG_CODE").setText("");
	}

	/**
	 * ���ݿⷿ���ͻ�ʿվ����ҩ���б�(ORG_CODE)
	 * 
	 * @param org_type
	 * @param station_flg
	 */
	private void onSetOrgCodeByTypeAndStation(String org_type,
			boolean station_flg) {
		getTextFormat("ORG_CODE").setPopupMenuSQL(
				INDSQL.getOrgCodeByTypeAndStation(org_type, station_flg,
						Operator.getRegion()));
		getTextFormat("ORG_CODE").onQuery();
	}

	/**
	 * �趨ҳ��״̬
	 * 
	 * @param parm
	 *            ״̬����
	 */
	private void onSetStatus(String parm) {
		System.out.println("=====111parm111======"+parm);
		String[] status = parm.split(",");
		// ��ʿվ
		TCheckBox station = getCheckBox("STATION_FLG");
		station.setSelected(false);
		station.setEnabled(StringTool.getBoolean(status[0]));
		// ����ҩ��
		TComboBox sup_org = getComboBox("SUP_ORG_CODE");
		sup_org.setSelectedIndex(-1);
		sup_org.setEnabled(StringTool.getBoolean(status[1]));
		// �������쵥
		TCheckBox exinv = getCheckBox("EXINV_FLG");
		exinv.setSelected(false);
		exinv.setEnabled(StringTool.getBoolean(status[2]));
		// ������Һ����
		TCheckBox inj_org = getCheckBox("INJ_ORG_FLG");
		inj_org.setSelected(false);
		inj_org.setEnabled(StringTool.getBoolean(status[3]));
		// ��ҩ��
		TCheckBox act = getCheckBox("ATC_FLG");
		act.setSelected(false);
		act.setEnabled(StringTool.getBoolean(status[4]));
		// ��ҩ�� DECOCT_CODE
		TextFormatDept decoct = (TextFormatDept) this
				.getComponent("DECOCT_CODE");
		decoct.setValue("");
		decoct.setEnabled(StringTool.getBoolean(status[5]));
	}

	/**
	 * �������
	 */
	private boolean CheckData() {
		String org_code = getValueString("ORG_CODE");
		if ("".equals(org_code)) {
			messageBox("\u5E93\u623F\u4EE3\u7801\u4E0D\u80FD\u4E3A\u7A7A");
			return false;
		}
		TComboBox sup_org = getComboBox("SUP_ORG_CODE");
		if (sup_org.isEnabled() && "".equals(getValueString("SUP_ORG_CODE"))) {
			messageBox("\u4E2D\u5C0F\u5E93\u623F\u7684\u5F52\u5C5E\u836F\u623F\u4E0D\u80FD\u4E3A\u7A7A");
			return false;
		}
		if ("".equals(getValueString("REGION_CODE"))) {
			messageBox("\u533A\u57DF\u4EE3\u7801\u4E0D\u80FD\u4E3A\u7A7A");
			return false;
		}
		for (int i = 0; i < vct.size(); i++)
			if (org_code.equals(((Vector) vct.get(i)).get(0).toString())) {
				messageBox("\u5E93\u623F\u5DF2\u5B58\u5728");
				return false;
			}

		return true;
	}

	/**
	 * �õ�CheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * �õ�TextFormat����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
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
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
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

}