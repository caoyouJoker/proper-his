package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import jdo.inv.INVNewBackDisnfectionTool;
import jdo.inv.INVNewRepackTool;
import jdo.inv.INVNewSterilizationTool;
import jdo.inv.INVSQL;
import jdo.inv.InvPackStockMTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:���ư����⹦��
 * </p>
 * 
 * <p>
 * Description: ���ư����⹦��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author wangming 2013-11-27
 * @version 1.0
 */
public class INVSubtractiveStockControl extends TControl {

	private TTable tableM; // ����ѯ��ʾ�б�

	/**
	 * ��ʼ��
	 */
	public void onInit() {

		tableM = (TTable) getComponent("TABLEM");

		this.setTimes();

		TParm parm = new TParm();
		// ���õ����˵�
		getTextField("PACK_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("PACK_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");

	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (parm == null) {
			return;
		}
		String pack_code = parm.getValue("PACK_CODE");
		if (!StringUtil.isNullString(pack_code))
			getTextField("PACK_CODE").setValue(pack_code);
		String pack_desc = parm.getValue("PACK_DESC");
		if (!StringUtil.isNullString(pack_desc))
			getTextField("PACK_DESC").setValue(pack_desc);

	}

	/**
	 * ���
	 */
	public void onClear() {
		this.setTimes();
		getTextField("PACK_CODE").setValue("");
		getTextField("PACK_DESC").setValue("");
		((TCheckBox) getComponent("ZERO")).setSelected(true);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {

		if (!this.checkConditions()) {
			return;
		}

		String sql = this.getQuerySQL();
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		if (result.getCount() <= 0) {
			this.messageBox("�޲�ѯ���");
			tableM.removeRowAll();
			return;
		}
		tableM.setParmValue(result);
	}

	/**
	 * ��ò�ѯsql
	 */
	private String getQuerySQL() {
		String sql = "";
		sql = " SELECT D.DISPENSE_NO, D.SEQ_NO, PM.PACK_CODE, PM.PACK_DESC, D.ACTUAL_QTY, 0 AS SUB_QTY, ML.DESCRIPTION, ML.MATERIAL_LOC_CODE AS MATERIAL_LOCATION, D.PACK_BATCH_NO "
				+ "  FROM INV_SUP_DISPENSED D LEFT JOIN INV_PACKM PM ON D.INV_CODE = PM.PACK_CODE LEFT JOIN INV_MATERIALLOC ML ON D.MATERIAL_LOCATION = ML.MATERIAL_LOC_CODE "
				+ " WHERE D.PACK_MODE = '1' AND D.MATERIAL_LOCATION IS NOT NULL ";
		if (((TCheckBox) getComponent("ZERO")).isSelected()) {
			sql = sql + " AND D.ACTUAL_QTY >0 ";
		}
		sql = sql + " AND D.OPT_DATE BETWEEN TO_DATE( '"
				+ this.getValueString("START_DATE").substring(0, 19)                   
				+ "', 'yyyy/mm/dd hh24:mi:ss' ) AND TO_DATE( '"
				+ this.getValueString("END_DATE").substring(0, 19)
				+ "', 'yyyy/mm/dd hh24:mi:ss' ) ";

		sql = sql + " AND D.INV_CODE = '" + this.getValueString("PACK_CODE")
				+ "' ORDER BY D.DISPENSE_NO DESC ";
		System.out.println("sql:" + sql);
		return sql;

	}

	/**
	 * ����
	 */
	public void onSave() {

		TParm tp = tableM.getParmValue();

		if (null == tp || tp.getCount("DISPENSE_NO") <= 0) {
			messageBox("û��Ҫ���������!");
			return;    
		}

		for (int i = 0; i < tp.getCount("DISPENSE_NO"); i++) {

			try {
				Double.parseDouble(tp.getData("SUB_QTY", i).toString());
			} catch (NumberFormatException e) {
				messageBox("�����������!");
				e.printStackTrace();
				return;
			}

			double qty = tp.getDouble("ACTUAL_QTY", i);
			double sub_qty = tp.getDouble("SUB_QTY", i);

			if (sub_qty > qty) {
				messageBox("�������������ڿ����!");
				return;
			}

		}

		for (int i = 0; i < tp.getCount("DISPENSE_NO"); i++) {

			if (tp.getDouble("SUB_QTY", i) > 0) {
				// ����INV_SUP_DISPENSED��ACTUAL_QTY����
				String sql = " UPDATE INV_SUP_DISPENSED SET ACTUAL_QTY = "
						+ (tp.getDouble("ACTUAL_QTY", i) - tp.getDouble(
								"SUB_QTY", i)) + " WHERE DISPENSE_NO = '"
						+ tp.getData("DISPENSE_NO", i) + "' AND SEQ_NO = "
						+ tp.getDouble("SEQ_NO", i);

				TJDODBTool.getInstance().update(sql);

				// ����INV_SUP_DISPENSEDD�������������
				sql = " SELECT QTY,INV_CODE,PACK_CODE FROM INV_PACKD WHERE PACK_CODE = '"
						+ tp.getData("PACK_CODE", i) + "' ";
				TParm result = new TParm(TJDODBTool.getInstance().select(sql));
				if (result.getCount("PACK_CODE") > 0) {
					for (int j = 0; j < result.getCount("PACK_CODE"); j++) {
						result.setData("QTY", j, result.getDouble("QTY", j)
								* tp.getDouble("SUB_QTY", i));
					}

					for (int m = 0; m < result.getCount("PACK_CODE"); m++) {

						sql = " UPDATE INV_SUP_DISPENSEDD DD SET DD.QTY = DD.QTY - "
								+ result.getDouble("QTY", m)
								+ " "   
								+ " WHERE DD.DISPENSE_NO = '"
								+ tp.getData("DISPENSE_NO", i)
								+ "' AND DD.PACK_CODE = '" 
								+ tp.getData("PACK_CODE", i)
								+ "' "
								+ " AND DD.INV_CODE = '"
								+ result.getValue("INV_CODE", m)     
								+ "' AND DD.PACK_BATCH_NO = '"
								+ tp.getData("PACK_BATCH_NO", i) + "' ";
						TJDODBTool.getInstance().update(sql);

					}

				}

				// ������ʱ��¼��
				Timestamp date = SystemTool.getInstance().getDate();
				sql = " INSERT INTO INV_TMP_PACKQTY ( DISPENSE_NO, SEQ_NO, PACK_CODE, QTY, SUB_QTY, MATERIAL_LOCATION, OPT_USER, OPT_DATE, OPT_TERM) "
						+ " VALUES ('"
						+ tp.getData("DISPENSE_NO", i)
						+ "',"
						+ tp.getDouble("SEQ_NO", i)
						+ ",'"
						+ tp.getData("PACK_CODE", i)
						+ "',"
						+ tp.getDouble("ACTUAL_QTY", i)
						+ ","
						+ tp.getDouble("SUB_QTY", i)
						+ ",'"
						+ tp.getData("MATERIAL_LOCATION", i)
						+ "','"
						+ Operator.getID()
						+ "',TO_DATE('"
						+ date.toString().substring(0, 19)
						+ "','yyyy/mm/dd hh24:mi:ss'),'"
						+ Operator.getIP()
						+ "') ";

				TJDODBTool.getInstance().update(sql);
			}

		}

		this.onQuery();

	}

	/**
	 * ���
	 */
	private boolean checkConditions() {

		if (null == this.getValueString("PACK_CODE")
				|| "".equals(this.getValueString("PACK_CODE"))
				|| this.getValueString("PACK_CODE").length() <= 0) {
			messageBox("����������������!");
			return false;
		}
		if (null == this.getValueString("START_DATE")
				|| "".equals(this.getValueString("START_DATE"))
				|| this.getValueString("START_DATE").length() <= 0
				|| null == this.getValueString("END_DATE")
				|| "".equals(this.getValueString("END_DATE"))
				|| this.getValueString("END_DATE").length() <= 0) {
			messageBox("����������!");
			return false;
		}
		return true;
	}

	/**
	 * ��ʼ��ʱ��
	 */
	private void setTimes() {
		// ��ʼ�� �˻����ڲ�ѯ����
		Timestamp date = new Timestamp(new Date().getTime());
		this.setValue("START_DATE", new Timestamp(date.getTime() + -7 * 24L
				* 60L * 60L * 1000L).toString().substring(0, 10).replace("-",
				"/")
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace("-",
				"/")
				+ " 23:59:59");
	}

	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
}
