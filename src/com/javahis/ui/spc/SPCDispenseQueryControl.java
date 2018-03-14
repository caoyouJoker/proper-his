package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCDispenseQueryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ҩ������ͳ��
 * </p>
 * 
 * <p>
 * Description: ҩ������ͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * f Company: Javahis
 * </p>
 * 
 * @author shendr
 * @version 1.0
 */
public class SPCDispenseQueryControl extends TControl {

	// ������
	public TTable table;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		table = this.getTable("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// ���õ����˵�
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("ORG_CODE", Operator.getDept());
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		// ��ѯ����
		String start_date = this.getValueString("START_DATE");
		start_date = start_date.substring(0, 4) + start_date.substring(5, 7)
				+ start_date.substring(8, 10) + start_date.substring(11, 13)
				+ start_date.substring(14, 16) + start_date.substring(17, 19);
		String end_date = this.getValueString("END_DATE");
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
		String org_code = this.getValueString("ORG_CODE");
		String order_code = this.getValueString("ORDER_CODE");
		String check_dis = this.getValueString("CHECK_DIS");
		String check_ret = this.getValueString("CHECK_RET");
		TParm result = SPCDispenseQueryTool.getInstance().querySale(org_code,
				start_date, end_date, check_dis, check_ret, order_code);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			messageBox("E0008");
			return;
		}
		table.setParmValue(result);
		setSumAmt();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		table.removeRowAll();
		String clearStr = "ORDER_CODE;ORDER_DESC";
		this.clearValue(clearStr);
		Timestamp date = SystemTool.getInstance().getDate();
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		getTCheckBox("CHECK_DIS").setSelected(true);
		getTCheckBox("CHECK_RET").setSelected(false);
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("ORG_CODE", Operator.getDept());
		setSumAmt();
	}

	/**
	 * ���Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("û�л������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "ҩ������ͳ��");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
	}

	/**
	 * �����ܽ��
	 */
	private void setSumAmt() {
		double amt = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			amt += table.getItemDouble(i, "AMT");
		}
		this.setValue("SUM_AMT", amt);
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
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * �õ�TCheckBox����
	 * 
	 * @param tag
	 * @return
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

}
