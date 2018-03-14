package com.javahis.ui.dev;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.dev.DevDepTool;
import jdo.dev.DevInStorageTool;

import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

import jdo.sys.SystemTool;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTreeEvent;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.TTextFormat;

import jdo.sys.Operator;
import jdo.util.Manager;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;

import java.awt.Component;

import psyg.graphic.SysObj;

import com.dongyang.jdo.TJDODBTool;

import jdo.dev.DevTypeTool;

/**
 * <p>
 * Title: �豸���
 * </p>
 * 
 * <p>
 * Description: �豸���
 * </p>
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author fux
 * @version 1.0
 */
public class DevInStorageControl extends TControl {
	// �������,100;��ⵥ��,100;������,100,DEPT_COMBO;���յ���,100;��������,100;�����Ա,100,OPERATOR_COMBO
	// INWAREHOUSE_DATE;INWAREHOUSE_NO;INWAREHOUSE_DEPT;VERIFY_NO;RECEIPT_DATE;INWAREHOUSE_USER
	// �豸�����ϸ������
	TParm parmD = new TParm();
	// �豸��ϸ��Ź�������
	TParm parmDD = new TParm();
	// ����ϸ��parm
	TParm ReceiptD = new TParm();
	private TTable tabledd;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();

		this.setValue("CHECK_IN", "N");
    	((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(false);
		// �豸�����༭�¼�
		// �豸��ϸ���༭�¼�
		callFunction("UI|RECEIPT|addEventListener", "RECEIPT->"
				+ TTableEvent.CLICKED, this, "onDevInwarehouse");
		addEventListener("DEV_INWAREHOUSED->" + TTableEvent.CHANGE_VALUE,
				"onTableValueChange");
		// �豸¼���¼�
		getTTable("DEV_INWAREHOUSED").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponent");
		// �豸¼�¼�
		getTTable("DEV_INWAREHOUSED").addEventListener(
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableComponent");
		getTTable("DEV_INWAREHOUSEDD").addEventListener(
				"DEV_INWAREHOUSEDD->" + TTableEvent.DOUBLE_CLICKED, this,
				"onDoubleClicked");
		// ��ʼ����������ʱ�ؼ�Ĭ��ֵ
		initComponent();
		// ��ʼ���豸�����ϸ���
		initTableD();
		// ��ʼ���豸�����ϸ��Ź�����
		initTableDD();
		// �������
		addDRow();
		// ��ס��񲻿ɱ༭��λ
		setTableLock();
		tabledd = this.getTTable("DEV_INWAREHOUSEDD");
		// ����Ȩ��
		onInitOperaotrDept();
	}

	/**
	 * ����Ȩ��
	 */
	private void onInitOperaotrDept() {
		// ��ʾȫԺҩ�ⲿ��
		if (getPopedem("deptAll"))
			return;
		// ((TextFormatDEVOrg)getComponent("INWAREHOUSE_DEPT")).setOperatorId(Operator.getID());
		// ((TextFormatDEVOrg)getComponent("DEPT")).setOperatorId(Operator.getID());
	}

	/**
	 * ��ס��񲻿ɱ༭��λ
	 */
	public void setTableLock() {
		// ((TTable)getComponent("DEV_INWAREHOUSED")).setLockColumns("3,4,5,6,8,"+
		// "9,10,12,16,17,18,"+
		// "19,20,21,22,"+
		// "24,25,26,27,28");
		// ((TTable)getComponent("DEV_INWAREHOUSEDD")).setLockColumns("0,1,2,3,4,5,11,"+
		// "12,13,14,15");
	}

	/**
	 * ��ʼ������ؼ�Ĭ��ֵ
	 */
	public void initComponent() {
		Timestamp timestamp = SystemTool.getInstance().getDate();
		setValue("INWARE_START_DATE", timestamp);
		setValue("INWARE_END_DATE", timestamp);
		setValue("INWAREHOUSE_DEPT", Operator.getDept());
		setValue("INWAREHOUSE_DATE", timestamp);
		setValue("DEPT", Operator.getDept());
		setValue("OPERATOR", Operator.getID());
	}

	// RECEIPT
	// �������,100;��ⵥ��,100;������,100,DEPT_COMBO;���յ���,100;��������,100;�����Ա,100,OPERATOR_COMBO
	// INWAREHOUSE_DATE;INWAREHOUSE_NO;INWAREHOUSE_DEPT;VERIFY_NO;RECEIPT_DATE;INWAREHOUSE_USER
	/**
	 * ��ʼ���豸�����ϸ���
	 */
	public void initTableD() {
		String column = "DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;"
				+ "SPECIFICATION;MODEL;MAN_CODE;BRAND;QTY;SUM_QTY;RECEIPT_QTY;"
				+ "UNIT_CODE;UNIT_PRICE;TOT_VALUE;MAN_DATE;LAST_PRICE;"
				+ "GUAREP_DATE;DEPR_METHOD;USE_DEADLINE;DEP_DATE;"
				+ "MAN_NATION;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;"
				+ "FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE";
		String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDParm = new TParm();
		for (int i = 0; i < stringMap.length; i++) {
			tableDParm.addData(stringMap[i], "");
		}
		((TTable) getComponent("DEV_INWAREHOUSED")).setParmValue(tableDParm);
		((TTable) getComponent("DEV_INWAREHOUSED")).removeRow(0);
	}

	/**
	 * ��ʼ���豸�����ϸ��Ź�����
	 */
	public void initTableDD() {
		String column = "DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;"
				+ "DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;BARCODE;MAIN_DEV;"
				+ "MAN_DATE;MAN_SEQ;LAST_PRICE;GUAREP_DATE;DEP_DATE;"
				+ "TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO;"
				+ "SERIAL_NUM;WIRELESS_IP;IP;TERM;LOC_CODE;USE_USER";
		String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDParm = new TParm();
		for (int i = 0; i < stringMap.length; i++) {
			tableDParm.addData(stringMap[i], "");
		}
		((TTable) getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableDParm);
		((TTable) getComponent("DEV_INWAREHOUSEDD")).removeRow(0);
	}

	/**
	 * �������
	 */
	public void addDRow() {
		String column = "DEL_FLG;DEVPRO_CODE;DEV_CODE;DEV_CHN_DESC;"
				+ "SPECIFICATION;MODEL;MAN_CODE;BRAND;QTY;SUM_QTY;RECEIPT_QTY;"
				+ "UNIT_CODE;UNIT_PRICE;TOT_VALUE;MAN_DATE;LAST_PRICE;"
				+ "GUAREP_DATE;DEPR_METHOD;USE_DEADLINE;DEP_DATE;"
				+ "MAN_NATION;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;"
				+ "FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE";
		String stringMap[] = StringTool.parseLine(column, ";");
		TParm tableDParm = new TParm();
		for (int i = 0; i < stringMap.length; i++) {
			if (stringMap[i].equals("DEVPRO_CODE"))
				tableDParm.setData(stringMap[i], "A");
			else
				tableDParm.setData(stringMap[i], "");
		}
		((TTable) getComponent("DEV_INWAREHOUSED")).addRow(tableDParm);
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		((TTable) getComponent("RECEIPT")).removeRowAll();
		((TTable) getComponent("RECEIPT")).resetModify();
		TParm parm = new TParm();
		// ��ⵥ��
		if (getValueString("INWAREHOUSE_NO").length() != 0)
			parm.setData("INWAREHOUSE_NO", getValueString("INWAREHOUSE_NO"));
		// ��⿪ʼʱ��
		if (getValueString("INWARE_START_DATE").length() != 0)
			parm.setData("INWARE_START_DATE", StringTool.getTimestamp(
					getValueString("INWARE_START_DATE"), "yyyy-MM-dd"));
		// ������ʱ��
		if (getValueString("INWARE_END_DATE").length() != 0)
			parm.setData("INWARE_END_DATE", StringTool.getTimestamp(
					getValueString("INWARE_END_DATE"), "yyyy-MM-dd"));
		// ������
		if (getValueString("INWAREHOUSE_DEPT").length() != 0)
			parm
					.setData("INWAREHOUSE_DEPT",
							getValueString("INWAREHOUSE_DEPT"));
		// �����Ա
		if (getValueString("INWAREHOUSE_USER").length() != 0)
			parm
					.setData("INWAREHOUSE_USER",
							getValueString("INWAREHOUSE_USER"));
		// ���յ���
		if (getValueString("RECEIPT_NO").length() != 0)
			parm.setData("RECEIPT_NO", getValueString("RECEIPT_NO"));
		// ���տ�ʼʱ��
		if (getValueString("RECEIPT_START_DATE").length() != 0)
			parm.setData("RECEIPT_START_DATE", StringTool.getTimestamp(
					getValueString("RECEIPT_START_DATE"), "yyyy-MM-dd"));
		// ���ս���ʱ��
		if (getValueString("RECEIPT_END_DATE").length() != 0)
			parm.setData("RECEIPT_END_DATE", StringTool.getTimestamp(
					getValueString("RECEIPT_END_DATE"), "yyyy-MM-dd"));
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {
			parm.setData("CHECK_FLG", "N");
		}else if (getRadioButton("UPDATE_FLG_C").isSelected()) {
			parm.setData("CHECK_FLG", "Y");
		}
		if (parm.getNames().length == 0)
			return;
		parm = DevInStorageTool.getInstance().selectDevInStorageInf(parm);
		if (parm.getErrCode() < 0) {
			messageBox("��ѯ����");
			return;
		}
		if (parm.getCount() <= 0) {
			messageBox("�޲�ѯ���ݣ�");
			return;
		}
		((TTable) getComponent("RECEIPT")).setParmValue(parm);

	}

	/**
	 * �õ���Ӧ�������Ϣ
	 * 
	 * @param receiptNo
	 *            String
	 * @return TParm
	 */
	public TParm getSupInf(String receiptNo) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT B.SUP_CODE,B.SUP_SALES1,B.SUP_SALES1_TEL"
						+ " FROM DEV_RECEIPTM A, SYS_SUPPLIER B"
						+ " WHERE A.RECEIPT_NO = '" + receiptNo + "'"
						+ " AND   A.SUP_CODE = B.SUP_CODE"));
		return parm;
	}

	/**
	 * ��ѯ���յ�����������ⵥ��Ϣ fux
	 */
	public void onGenerateReceipt() {
		if (getValueString("INWAREHOUSE_DATE").length() == 0) {
			messageBox("��¼���������");
			return;
		}
		onClear();
		((TTable) getComponent("RECEIPT"))
				.setParmValue(((TTable) getComponent("RECEIPT")).getParmValue());
		TParm parmDiag = (TParm) openDialog("%ROOT%\\config\\dev\\ReceiptUI.x");
		if (parmDiag == null)
			return;
		String receiptNo = parmDiag.getValue("RECEIPT_NO");
		// ��SEQ_NO
		ReceiptD = DevInStorageTool.getInstance().selectReceiptD(receiptNo);
		// System.out.println("selectReceiptD"+ReceiptD);
		if (ReceiptD.getErrCode() < 0)
			return;
		if (ReceiptD.getCount() <= 0) {
			messageBox("����ⵥ�豸�Ѿ�ȫ�����");
			return;
		}
		setValue("SUP_CODE", parmDiag.getValue("SUP_CODE"));
		setValue("SUP_BOSSNAME", parmDiag.getValue("SUP_SALES1"));
		setValue("SUP_TEL", parmDiag.getValue("SUP_SALES1_TEL"));
		setValue("VERIFY_NO", receiptNo);
		Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance()
				.getDate().toString(), "yyyy-MM-dd");
		TParm tableParm = new TParm();
		TParm tableParmDD = new TParm();
		for (int i = 0; i < ReceiptD.getCount(); i++) {
			// ȡ���������
			String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
			// ����ʹ�����޼����۾���ֹ����
			int year = Integer.parseInt(inWarehouseDate.substring(0, 4))
					+ ReceiptD.getInt("USE_DEADLINE", i);
			String depDate = year
					+ inWarehouseDate.substring(4, inWarehouseDate.length());
			// �����۾���ֹ���ڱ�����ֹ�����Լ��豸����ȡ������
//			int batchNo = DevInStorageTool.getInstance().getUseBatchSeq(
//					ReceiptD.getValue("DEV_CODE", i),
//					StringTool.getTimestamp(depDate, "yyyy-MM-dd"), timestamp);
			tableParm.addData("DEL_FLG", "N");
			tableParm
					.addData("DEVPRO_CODE", ReceiptD.getData("DEVPRO_CODE", i));
			tableParm.addData("DEV_CODE", ReceiptD.getData("DEV_CODE", i));
//			tableParm.addData("BATCH_SEQ", batchNo);
			tableParm.addData("DEV_CHN_DESC", ReceiptD.getData("DEV_CHN_DESC",
					i));
			// BRAND;SPECIFICATION;MODEL
			tableParm.addData("BRAND", ReceiptD.getData("BRAND", i));
			tableParm.addData("SPECIFICATION", ReceiptD.getData(
					"SPECIFICATION", i));
			tableParm.addData("MODEL", ReceiptD.getData("MODEL", i));
			tableParm.addData("MAN_CODE", ReceiptD.getData("MAN_CODE", i));
			// �������,100,INT;�ۼ������,100;������,100;
			tableParm.addData("QTY", ReceiptD.getData("QTY", i));
			// ��ʼ�����ۼ������Ӧ��Ϊ0
			tableParm.addData("SUM_QTY", 0);
			// fux need modify
			tableParm.addData("RECEIPT_QTY", ReceiptD.getData("SUM_QTY", i));
			tableParm.addData("UNIT_CODE", ReceiptD.getData("UNIT_CODE", i));
			tableParm.addData("UNIT_PRICE", ReceiptD.getData("UNIT_PRICE", i));
			tableParm.addData("TOT_VALUE", ReceiptD.getDouble("UNIT_PRICE", i)
					* ReceiptD.getDouble("QTY", i));
			tableParm.addData("MAN_DATE", timestamp);
			tableParm.addData("LAST_PRICE", ReceiptD.getData("UNIT_PRICE", i));
			tableParm.addData("GUAREP_DATE", timestamp);
			tableParm
					.addData("DEPR_METHOD", ReceiptD.getData("DEPR_METHOD", i));
			tableParm.addData("USE_DEADLINE", ReceiptD.getData("USE_DEADLINE",
					i));
			tableParm.addData("DEP_DATE", StringTool.getTimestamp(depDate,
					"yyyy-MM-dd"));
			tableParm.addData("MAN_NATION", ReceiptD.getData("MAN_NATION", i));
			tableParm.addData("SEQMAN_FLG", ReceiptD.getData("SEQMAN_FLG", i));
			tableParm
					.addData("MEASURE_FLG", ReceiptD.getData("MEASURE_FLG", i));
			tableParm
					.addData("BENEFIT_FLG", ReceiptD.getData("BENEFIT_FLG", i));
			tableParm.addData("FILES_WAY", "");
			tableParm.addData("VERIFY_NO", receiptNo);
			tableParm.addData("VERIFY_NO_SEQ", ReceiptD.getData("SEQ_NO", i));
			tableParm.addData("INWAREHOUSE_NO", "");
			tableParm.addData("SEQ_NO", "");
			tableParm.addData("DEVKIND_CODE", ReceiptD.getData("DEVKIND_CODE",
					i));
			// ����Ź�����չ����Ź�����ϸ
			if ("Y".equals(ReceiptD.getData("SEQMAN_FLG", i)))
				addDevDDTable(tableParmDD, tableParm, i);
		}
		((TTable) getComponent("DEV_INWAREHOUSED")).setParmValue(tableParm);
		((TTable) getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmDD);
		((TTextFormat) getComponent("INWAREHOUSE_DATE")).setEnabled(false);
		setTableLock();
	}

	/**
	 * ������Ź�����ϸ����Ϣ
	 * 
	 * @param tableParmDD
	 *            TParm
	 * @param tableParm
	 *            TParm
	 * @param row
	 *            int
	 */
	public void addDevDDTable(TParm tableParmDD, TParm tableParm, int row) {
		// ɾ,30,BOOLEAN;����,100,DEV_PRO;�豸���,100;�������,100;�豸����,200;����ͺ�,100;����,100;Ч��,100,Timestamp;��������,200,MAN_TF;�������,100,INT;�ۼ������,100;������,100;��λ,100,DEV_UNIT;����,100,DOUBLE;�Ʋ���ֵ,100;��������,100,Timestamp;��ֵ,100,DOUBLE;������ֹ����,100,Timestamp;�۾�,60,DEP_METHOD;ʹ������,100;�۾���ֹ����,100,Timestamp;������,100,MAN_NATION_TF;��Ź���,100,BOOLEAN;�����豸,100,BOOLEAN;Ч������,100,BOOLEAN;�����ļ�,100;���յ���,100;���յ���ϸ���,100;��ⵥ��,100;��ⵥ���,100;�豸���,100,DEVKIND_CODE
		// DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;BATCH_SEQ;DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;BARCODE;
		// MAIN_DEV;MAN_DATE;MAN_SEQ;LAST_PRICE;GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO
		for (int i = 0; i < tableParm.getInt("QTY", row); i++) {
			// ȡ�ô�DEV_CODE�����DDSEQ_NO
			int ddseqNo = DevInStorageTool.getInstance().getMaxDevSeqNo(
					tableParm.getValue("DEV_CODE", row));
			tableParmDD.addData("DEL_FLG", "N");
			tableParmDD.addData("SELECT_FLG", "Y");
			tableParmDD.addData("PRINT_FLG", "Y");
			tableParmDD.addData("DEVPRO_CODE", tableParm.getData("DEVPRO_CODE",
					row));
//			tableParmDD.addData("BATCH_SEQ", tableParm
//					.getData("BATCH_SEQ", row));
			tableParmDD.addData("DEV_CODE", tableParm.getData("DEV_CODE", row));
			tableParmDD.addData("DDSEQ_NO", ddseqNo + i + 1);
			tableParmDD.addData("DEV_CHN_DESC", tableParm.getData(
					"DEV_CHN_DESC", row));
			tableParmDD.addData("BARCODE", "");
			tableParmDD.addData("MAIN_DEV", "");
			tableParmDD.addData("MAN_DATE", tableParm.getData("MAN_DATE", row));
			tableParmDD.addData("MAN_SEQ", "");
			tableParmDD.addData("LAST_PRICE", tableParm.getData("LAST_PRICE",
					row));
			tableParmDD.addData("GUAREP_DATE", tableParm.getData("GUAREP_DATE",
					row));
			tableParmDD.addData("DEP_DATE", tableParm.getData("DEP_DATE", row));
			tableParmDD.addData("TOT_VALUE", tableParm.getData("UNIT_PRICE",
					row));
			tableParmDD.addData("INWAREHOUSE_NO", "");
			tableParmDD.addData("SEQ_NO", "");
			tableParmDD.addData("DEVSEQ_NO", "");
			// fux modify 20131030
			// SERIAL_NUM;WIRELESS_IP;IP;TERM;LOC_CODE
			// ͨ���ʲ���Ƭά���������
			tableParmDD.addData("SERIAL_NUM", "");
			tableParmDD.addData("WIRELESS_IP", "");
			tableParmDD.addData("IP", "");
			tableParmDD.addData("TERM", "");
			tableParmDD.addData("LOC_CODE", "");
			tableParmDD.addData("USE_USER", "");
			tableParmDD.addData("BRAND", tableParm.getData("BRAND", row));
			tableParmDD.addData("SPECIFICATION", tableParm.getData(
					"SPECIFICATION", row));
			tableParmDD.addData("MODEL", tableParm.getData("MODEL", row));
			tableParmDD.addData("MAN_CODE", tableParm.getData("MAN_CODE", row));
		}
	}

	/**
	 * ������ı��¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableValueChange(Object obj) {
		// //fux modify 20131030 ����λ��
		TTableNode node = (TTableNode) obj;
		// ɾ������
		onTableValueChange0(node);

		// �豸����
		onTableValueChange1(node);

		// �豸��Ŷ���
		onTableValueChange2(node);

		// ���������
		onTableValueChange8(node);

		// ���۶���
		onTableValueChange12(node);

		// �������ڶ���
		onTableValueChange16(node);

		// �۾����ڶ���
		onTableValueChange19(node);
		return false;
	}

	/**
	 * �豸ɾ����
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onTableValueChange0(TTableNode node) {
		if (node.getColumn() != 0)
			return;
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		TTable ddTable = ((TTable) getComponent("DEV_INWAREHOUSEDD"));
		for (int i = 0; i < ddTable.getRowCount(); i++) {
			if (ddTable.getValueAt(i, 5).equals(
					dTable.getValueAt(dTable.getSelectedRow(), 2))
					&& ddTable.getValueAt(i, 4).equals(
							dTable.getValueAt(dTable.getSelectedRow(), 3)))
				updateTableData("DEV_INWAREHOUSEDD", i, 0, node.getValue());
		}
	}

	/**
	 * �豸���Զ���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onTableValueChange1(TTableNode node) {
		if (node.getColumn() != 1)
			return;
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		// �������
		String devBatchNo = "" + dTable.getValueAt(node.getRow(), 3);
		if (devBatchNo.length() != 0)
			node.setValue(dTable.getValueAt(node.getRow(), node.getColumn()));
	}

	/**
	 * �������ݱ�������ͬ���Parm
	 * 
	 * @param tableTag
	 *            String
	 * @param row
	 *            int
	 * @param column
	 *            int
	 * @param obj
	 *            Object
	 */
	public void updateTableData(String tableTag, int row, int column, Object obj) {
		((TTable) getComponent(tableTag)).setValueAt(obj, row, column);
		((TTable) getComponent(tableTag)).getParmValue().setData(
				getFactColumnName(tableTag, column), row, obj);
	}

	/**
	 * �豸��Ŷ���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onTableValueChange2(TTableNode node) {
		if (node.getColumn() != 2)
			return;
		String devCodeOld = ""
				+ getTTable("DEV_INWAREHOUSED").getValueAt(node.getRow(),
						node.getColumn());
		if (("" + getTTable("DEV_INWAREHOUSED").getValueAt(node.getRow(), 3))
				.length() != 0) {
			node.setValue(devCodeOld);
			return;
		}
	}

	/**
	 * �豸��������������
	 * 
	 * @param node
	 *            TTableNode
	 * @return boolean
	 */
	private boolean onTableValueChange8Check(TTableNode node) {
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		// if(("" + dTable.getValueAt(node.getRow(),26)).length() != 0&&
		// (Double.parseDouble("" + dTable.getValueAt(node.getRow(),11)) -
		// Double.parseDouble("" + dTable.getValueAt(node.getRow(),10)) <
		// Double.parseDouble("" + node.getValue()))){
		// messageBox("��" + String.valueOf(node.getRow()+1) +
		// "������������ɴ������������ۼ������֮��");
		// fux need modify 20130802
		if (("" + dTable.getValueAt(node.getRow(), 25)).length() != 0
				&& (Double.parseDouble(""
						+ dTable.getValueAt(node.getRow(), 10)) < Double
						.parseDouble("" + node.getValue()))) {
			messageBox("��" + String.valueOf(node.getRow() + 1) + "������������ɴ���������");
			node.setValue(dTable.getValueAt(node.getRow(), 8));
			return true;
		}
		if (("" + node.getValue()).length() == 0) {
			messageBox("��" + String.valueOf(node.getRow() + 1) + "���������������Ϊ��");
			node.setValue(dTable.getValueAt(node.getRow(), 8));
			return true;
		}
		return false;
	}

	/**
	 * �豸�����������
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onTableValueChange8(TTableNode node) {
		if (node.getColumn() != 8)
			return;
		if (node.getValue().toString().length() == 0)
			return;
		if (onTableValueChange8Check(node))
			return;
		TParm parm = ((TTable) getComponent("DEV_INWAREHOUSED")).getParmValue();
		TParm tableParmDD = new TParm();
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		// ���ݵ����Լ�����������ܼ�ֵ
		double unitPrice = 0;
		if (("" + dTable.getValueAt(node.getRow(), 12)).length() != 0)
			unitPrice = Double.parseDouble(""
					+ dTable.getValueAt(node.getRow(), 12));
		double totValue = Double.parseDouble("" + node.getValue()) * unitPrice;
		updateTableData("DEV_INWAREHOUSED", node.getRow(), 13, totValue);
		// ���½���Ź�����ϸչ��
		for (int i = 0; i < parm.getCount("DEVPRO_CODE"); i++) {
			if ("N".equals(parm.getData("SEQMAN_FLG", i)))
				continue;
			int rowCount = 0;
			String value = ("" + node.getValue()).length() == 0 ? "0"
					: ("" + node.getValue());
			if (i == node.getRow())
				rowCount = Integer.parseInt(value);
			else
				rowCount = parm.getInt("QTY", i);
			for (int j = 0; j < rowCount; j++) {
				// DEL_FLG;SELECT_FLG;PRINT_FLG;DEVPRO_CODE;DEV_CODE_DETAIL;DEV_CODE;DEVSEQ_NO;
				// DEV_CHN_DESC;BARCODE;MAIN_DEV;SPECIFICATION;MODEL;MAN_CODE;BRAND;
				// MAN_DATE;MAN_SEQ;LAST_PRICE;GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;
				// SEQ_NO;DEVSEQ_NO;SERIAL_NUM;IP;TERM;LOC_CODE;USE_USER
				tableParmDD.addData("DEL_FLG", parm.getData("DEL_FLG", i));
				tableParmDD.addData("SELECT_FLG", "N");
				tableParmDD.addData("PRINT_FLG", "N");
				tableParmDD.addData("DEVPRO_CODE", parm.getData("DEVPRO_CODE",
						i));
				tableParmDD.addData("DEV_CODE_DETAIL", "");
				tableParmDD.addData("DEV_CODE", parm.getData("DEV_CODE", i));
				tableParmDD.addData("DDSEQ_NO", j + 1);
				tableParmDD.addData("DEV_CHN_DESC", parm.getData(
						"DEV_CHN_DESC", i));
//				tableParmDD.addData("BATCH_SEQ", parm.getData("BATCH_SEQ", i));
				tableParmDD.setData("BARCODE", "");
				tableParmDD.addData("MAIN_DEV", "");
				tableParmDD.addData("SPECIFICATION", parm.getData(
						"SPECIFICATION", i));
				tableParmDD.addData("MODEL", parm.getData("MODEL", i));
				tableParmDD.addData("MAN_CODE", parm.getData("MAN_CODE", i));
				tableParmDD.addData("BRAND", parm.getData("BRAND", i));
				tableParmDD.addData("MAN_DATE", parm.getData("MAN_DATE", i));
				tableParmDD.addData("MAN_SEQ", "");
				tableParmDD
						.addData("LAST_PRICE", parm.getData("LAST_PRICE", i));
				tableParmDD.addData("GUAREP_DATE", parm.getData("GUAREP_DATE",
						i));
				tableParmDD.addData("DEP_DATE", parm.getData("DEP_DATE", i));
				tableParmDD.addData("TOT_VALUE", parm.getData("UNIT_PRICE", i));
				tableParmDD.setData("INWAREHOUSE_NO", "");
				tableParmDD.setData("SEQ_NO", "");
				tableParmDD.setData("DEVSEQ_NO", "");
				// fux modify 20131030
				tableParmDD.addData("SERIAL_NUM", "");
				tableParmDD.addData("WIRELESS_IP", "");
				tableParmDD.addData("IP", "");
				tableParmDD.addData("TERM", "");
				tableParmDD.addData("LOC_CODE", "");
				tableParmDD.addData("USE_USER", "");
			}
		}
		((TTable) getComponent("DEV_INWAREHOUSEDD")).setParmValue(tableParmDD);
	}

	/**
	 * ���۶���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onTableValueChange12(TTableNode node) {
		if (node.getColumn() != 12)
			return;
		if (node.getValue().toString().length() == 0)
			return;
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		TTable ddTable = ((TTable) getComponent("DEV_INWAREHOUSEDD"));
		// �����޸ĵĵ����Լ�������������ܼ�ֵ
		int qty = 0;
		if (("" + dTable.getValueAt(node.getRow(), 8)).length() != 0)
			qty = Integer.parseInt("" + dTable.getValueAt(node.getRow(), 8));
		double totValue = Double.parseDouble("" + node.getValue()) * qty;
		updateTableData("DEV_INWAREHOUSED", node.getRow(), 13, totValue);
		for (int i = 0; i < ddTable.getRowCount(); i++) {
			if (ddTable.getValueAt(i, 5).equals(
					dTable.getValueAt(dTable.getSelectedRow(), 2)))
				updateTableData("DEV_INWAREHOUSEDD", i, 12, node.getValue());
		}
	}

	/**
	 * �������ڶ���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onTableValueChange16(TTableNode node) {
		// fux need modify 20130802
		if (node.getColumn() != 16)
			return;
		if (node.getValue().toString().length() == 0)
			return;
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		TTable ddTable = ((TTable) getComponent("DEV_INWAREHOUSEDD"));
		Timestamp guarepDate = (Timestamp) node.getValue();
		Timestamp depDate = (Timestamp) dTable.getValueAt(dTable
				.getSelectedRow(), 17);
		String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(), 2);
//		int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,
//				depDate, guarepDate);
		for (int i = 0; i < ddTable.getRowCount(); i++) {
			if (ddTable.getValueAt(i, 5).equals(
					dTable.getValueAt(dTable.getSelectedRow(), 2))) {
				// 4ΪBATCH_SEQ
				updateTableData("DEV_INWAREHOUSEDD", i, 9, node.getValue());
//				if (batNo != Integer.parseInt("" + ddTable.getValueAt(i, 4)))
//					updateTableData("DEV_INWAREHOUSEDD", i, 4, batNo);
			}
		}
		// if(batNo!=Integer.parseInt("" +
		// dTable.getValueAt(dTable.getSelectedRow(),3)))
		// updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3,
		// batNo);
	}

	/**
	 * �۾����ڶ���
	 * 
	 * @param node
	 *            TTableNode
	 */
	public void onTableValueChange19(TTableNode node) {
		// fux need modify 20130802
		if (node.getColumn() != 19)
			return;
		if (node.getValue().toString().length() == 0)
			return;
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		TTable ddTable = ((TTable) getComponent("DEV_INWAREHOUSEDD"));
		Timestamp depDate = (Timestamp) node.getValue();
		Timestamp guarepDate = (Timestamp) dTable.getValueAt(dTable
				.getSelectedRow(), 14);
		String devCode = "" + dTable.getValueAt(dTable.getSelectedRow(), 2);
//		int batNo = DevInStorageTool.getInstance().getUseBatchSeq(devCode,
//				depDate, guarepDate);
		for (int i = 0; i < ddTable.getRowCount(); i++) {
			if (ddTable.getValueAt(i, 5).equals(
					dTable.getValueAt(dTable.getSelectedRow(), 2))) {
				updateTableData("DEV_INWAREHOUSEDD", i, 12, node.getValue());
				// 3ΪBATCH_SEQ
//				if (batNo != Integer.parseInt("" + ddTable.getValueAt(i, 4)))
//					updateTableData("DEV_INWAREHOUSEDD", i, 4, batNo);
			}
		}
		// if(batNo!=Integer.parseInt("" +
		// dTable.getValueAt(dTable.getSelectedRow(),3)))
		// updateTableData("DEV_INWAREHOUSED", dTable.getSelectedRow(), 3,
		// batNo);
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		if (getRadioButton("UPDATE_FLG_C").isSelected()) {
			return;
		}
		// ����Ƿ��б�������
		if (onSaveCheck())
			return;
		if (getValueString("INWAREHOUSE_DATE").length() == 0) {
			messageBox("������ڲ���Ϊ��");
			return;
		}
		if (getValueString("OPERATOR").length() == 0) {
			messageBox("�����Ա����Ϊ��");
			return;
		}
		if (getValueString("DEPT").length() == 0) {
			messageBox("�����Ҳ���Ϊ��");
			return;
		}
		for (int i = 0; i < getTTable("DEV_INWAREHOUSEDD").getRowCount(); i++) {
			if (getTTable("DEV_INWAREHOUSEDD")
					.getItemData(i, "DEV_CODE_DETAIL") == null) {
				messageBox("��˫���ʲ����,�Խ�����ϸ��д��");
				return;
			}
		}

		getTTable("DEV_INWAREHOUSED").acceptText();
		getTTable("DEV_INWAREHOUSEDD").acceptText();
		// �ж������������޸�
//		if (((TTable) getComponent("RECEIPT")).getSelectedRow() < 0)
			onNew();
//		else
//			onUpdate();
	}

	/**
	 * �����޸Ķ���
	 */
	private void onUpdate() {
		Timestamp timestamp = SystemTool.getInstance().getDate();
		TTable dTable = ((TTable) getComponent("DEV_INWAREHOUSED"));
		TTable ddTable = ((TTable) getComponent("DEV_INWAREHOUSEDD"));
		TParm tableParmD = dTable.getParmValue();
		TParm tableParmDD = ddTable.getParmValue();
		TParm parmInD = new TParm();
		TParm parmInDD = new TParm();
		TParm parmStockM = new TParm();
		TParm parmStockD = new TParm();
		TParm parmStockDD = new TParm();
		for (int i = 0; i < tableParmD.getCount("INWAREHOUSE_NO"); i++) {
			if (compareTo(parmD, tableParmD, i))
				continue;
			cloneTParm(tableParmD, parmInD, i);
			parmInD.addData("OPT_USER", Operator.getID());
			parmInD.addData("OPT_DATE", timestamp);
			parmInD.addData("OPT_TERM", Operator.getIP());
			cloneTParm(tableParmD, parmStockM, i);
			parmStockM.addData("OPT_USER", Operator.getID());
			parmStockM.addData("OPT_DATE", timestamp);
			parmStockM.addData("OPT_TERM", Operator.getIP());
			cloneTParm(tableParmD, parmStockD, i);
			parmStockD.addData("OPT_USER", Operator.getID());
			parmStockD.addData("OPT_DATE", timestamp);
			parmStockD.addData("OPT_TERM", Operator.getIP());
		}
		for (int i = 0; i < tableParmDD.getCount("INWAREHOUSE_NO"); i++) {
			if (compareTo(parmDD, tableParmDD, i))
				continue;
			cloneTParm(tableParmDD, parmInDD, i);
			parmInDD.addData("OPT_USER", Operator.getID());
			parmInDD.addData("OPT_DATE", timestamp);
			parmInDD.addData("OPT_TERM", Operator.getIP());
			cloneTParm(tableParmDD, parmStockDD, i);
			parmStockDD.addData("OPT_USER", Operator.getID());
			parmStockDD.addData("OPT_DATE", timestamp);
			parmStockDD.addData("OPT_TERM", Operator.getIP());
		}
		TParm parm = new TParm();
		parm.setData("DEV_INWAREHOUSED", parmInD.getData());
		parm.setData("DEV_INWAREHOUSEDD", parmInDD.getData());
		parm.setData("DEV_STOCKM", parmStockM.getData());
		parm.setData("DEV_STOCKD", parmStockD.getData());
		parm.setData("DEV_STOCKDD", parmStockDD.getData());
		parm = TIOM_AppServer.executeAction("action.dev.DevAction",
				"updateInStorageReceipt", parm);
		if (parm.getErrCode() < 0) {
			messageBox("����ʧ��");
			return;
		}
		messageBox("����ɹ�");
		onDevInwarehouse();
		onPrintReceipt();
	}

	/**
	 * ����
	 */
	private void onNew() {
		String inwarehouseNo = "";
		if(this.getValue("CHECK_IN").equals("N")){
			inwarehouseNo = DevInStorageTool.getInstance()
					.getInwarehouseNo();
		}else{
			inwarehouseNo = this.getValueString("INWAREHOUSE_NO_QUARY");
        }
		TTable tableM = this.getTTable("RECEIPT");
		if(this.getValue("CHECK_IN").equals("N") && tableM.getParmValue() != null && tableM.getParmValue().getCount()>0){
			this.messageBox("�빴ѡ���");
			return;
		}
		Timestamp timestamp = SystemTool.getInstance().getDate();
		TParm dParm = new TParm();
		TParm ddParm = new TParm();
		TParm mParm = new TParm();
		TParm stockMParm = new TParm();
		TParm stockDParm = new TParm();
		TParm stockDDParm = new TParm();
		TTable dTable = (TTable) getComponent("DEV_INWAREHOUSED");
		TTable ddTable = (TTable) getComponent("DEV_INWAREHOUSEDD");

		for (int i = 0; i < dTable.getRowCount(); ++i) {
			// DEV_CODE
			if (("" + dTable.getValueAt(i, 2)).length() == 0) {
				continue;
			}
			if (dTable.getValueAt(i, 0).equals("Y")) {
				continue;                              
			}
			// DEL_FLG;DEVPRO_CODE;DEV_CODE;BATCH_SEQ;DEV_CHN_DESC;
			// SPECIFICATION;BATCH_NO;VALID_DATE;MAN_CODE;QTY;
			// SUM_QTY;RECEIPT_QTY;UNIT_CODE;UNIT_PRICE;TOT_VALUE;
			// MAN_DATE;LAST_PRICE;GUAREP_DATE;DEPR_METHOD;USE_DEADLINE;
			// DEP_DATE;MAN_NATION;SEQMAN_FLG;MEASURE_FLG;BENEFIT_FLG;
			// FILES_WAY;VERIFY_NO;VERIFY_NO_SEQ;INWAREHOUSE_NO;SEQ_NO;DEVKIND_CODE
			Object obj = null;
			TParm parm = (TParm) obj;
			parm = dTable.getParmValue();
			String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
			int year = Integer.parseInt(inWarehouseDate.substring(0, 4))
					+ parm.getInt("USE_DEADLINE", i);
			// String depDate = year
			// + inWarehouseDate.substring(4, inWarehouseDate.length());
			dParm.addData("INWAREHOUSE_NO", inwarehouseNo);
			dParm.addData("SEQ_NO", Integer.valueOf(i + 1));
			dParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
			dParm.addData("SEQMAN_FLG", dTable.getItemData(i, "SEQMAN_FLG"));
			dParm.addData("QTY", dTable.getItemData(i, "QTY"));
			dParm.addData("UNIT_PRICE", dTable.getItemData(i, "UNIT_PRICE"));
			dParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
			// dParm.addData("SCRAP_VALUE", dTable.getItemData(i,
			// "LAST_PRICE"));
			dParm.addData("SCRAP_VALUE", 0);
			dParm.addData("GUAREP_DATE", dTable.getItemData(i, "GUAREP_DATE"));
			dParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE"));
			dParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
			dParm.addData("VERIFY_NO", dTable.getItemData(i, "VERIFY_NO"));
			dParm.addData("VERIFY_NO_SEQ", dTable.getItemData(i,
					"VERIFY_NO_SEQ"));
			dParm.addData("OPT_USER", Operator.getID());
			dParm.addData("OPT_DATE", timestamp);
			dParm.addData("OPT_TERM", Operator.getIP());
			// ��ӡ׼��
			dParm.addData("DEVPRO_CODE", dTable.getItemData(i, "DEVPRO_CODE"));
			dParm
					.addData("DEV_CHN_DESC", dTable.getItemData(i,
							"DEV_CHN_DESC"));
			if (dTable.getItemData(i, "BRAND") == null) {
				dParm.addData("BRAND", "");
			} else {
				dParm.addData("BRAND", dTable.getItemData(i, "BRAND"));
			}

			if (dTable.getItemData(i, "BRAND") == null) {
				dParm.addData("SPECIFICATION", "");
			} else {
				dParm.addData("SPECIFICATION", dTable.getItemData(i,
						"SPECIFICATION"));
			}
			if (dTable.getItemData(i, "MODEL") == null) {
				dParm.addData("MODEL", "");
			} else {
				dParm.addData("MODEL", dTable.getItemData(i, "MODEL"));
			}
			dParm.addData("TOT_VALUE", dTable.getItemData(i, "TOT_VALUE"));

			stockMParm.addData("REGION_CODE", Operator.getRegion());
			stockMParm.addData("DEPT_CODE", getValue("DEPT"));
			stockMParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
			stockMParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
			// ��ֵȷ���Ƿ�Ӧ�ö�Ϊ0
			// stockMParm.addData("SCRAP_VALUE", dTable.getItemData(i,
			// "LAST_PRICE"));
			stockMParm.addData("SCRAP_VALUE", 0);
			stockMParm.addData("GUAREP_DATE", dTable.getItemData(i,
					"GUAREP_DATE"));
			stockMParm.addData("DEP_DATE", dTable.getItemData(i, "DEP_DATE"));
			stockMParm.addData("FILES_WAY", dTable.getItemData(i, "FILES_WAY"));
			stockMParm.addData("CARE_USER", getValue("OPERATOR"));
			stockMParm.addData("USE_USER", "");
			stockMParm.addData("LOC_CODE", "");
			stockMParm.addData("INWAREHOUSE_DATE", StringTool
					.getTimestampDate(timestamp));
			stockMParm.addData("STOCK_FLG", "");
			stockMParm.addData("QTY", dTable.getItemData(i, "QTY"));
			stockMParm.addData("OPT_USER", Operator.getID());
			stockMParm.addData("OPT_DATE", timestamp);
			stockMParm.addData("OPT_TERM", Operator.getIP());

			// System.out.println("stockMParm" + stockMParm);
			TParm devBase = getDevBase("" + dTable.getItemData(i, "DEV_CODE"));

			stockDParm.addData("DEPT_CODE", getValue("DEPT"));
			stockDParm.addData("DEV_CODE", dTable.getItemData(i, "DEV_CODE"));
			// STOCK_D��ȥ������BATCH_SEQ����Ϊֻ�ÿ��Һ��豸��С�����Ϊ�������� M���Ϊֻͳ��dev_code���ֿ���
			// stockDParm.addData("BATCH_SEQ", batchSeq);
			stockDParm.addData("DEVKIND_CODE", dTable.getItemData(i,
					"DEVKIND_CODE"));
			stockDParm.addData("DEVTYPE_CODE", getDevTypeCode((String) dTable
					.getValueAt(i, 2)));
			stockDParm.addData("DEVPRO_CODE", dTable.getItemData(i,
					"DEVPRO_CODE"));
			if (devBase.getData("SETDEV_CODE", 0) != null) {
				stockDParm.addData("SETDEV_CODE", devBase.getData(
						"SETDEV_CODE", 0));
			} else {
				stockDParm.addData("SETDEV_CODE", "");
			}

			if (dTable.getItemData(i, "SPECIFICATION") != null) {
				stockDParm.addData("SPECIFICATION", dTable.getItemData(i,
						"SPECIFICATION"));
			} else {
				stockDParm.addData("SPECIFICATION", "");
			}
			if (dTable.getItemData(i, "BRAND") != null) {
				stockDParm.addData("BRAND", dTable.getItemData(i, "BRAND"));
			} else {
				stockDParm.addData("BRAND", "");
			}
			if (dTable.getItemData(i, "MODEL") != null) {
				stockDParm.addData("MODEL", dTable.getItemData(i, "MODEL"));
			} else {
				stockDParm.addData("MODEL", "");
			}
			stockDParm.addData("QTY", dTable.getItemData(i, "QTY"));
			stockDParm.addData("UNIT_PRICE", dTable
					.getItemData(i, "UNIT_PRICE"));
			stockDParm
					.addData("BUYWAY_CODE", devBase.getData("BUYWAY_CODE", 0));
			stockDParm.addData("MAN_NATION", devBase.getData("MAN_NATION", 0));
			stockDParm.addData("MAN_CODE", devBase.getData("MAN_CODE", 0));
			stockDParm.addData("SUPPLIER_CODE", "");
			stockDParm.addData("MAN_DATE", dTable.getItemData(i, "MAN_DATE"));
			stockDParm.addData("MANSEQ_NO", Integer.valueOf(0));
			stockDParm.addData("FUNDSOURCE", "");
			stockDParm.addData("APPROVE_AMT", "");
			stockDParm.addData("SELF_AMT", "");

			stockDParm
					.addData("DEPR_METHOD", devBase.getData("DEPR_METHOD", 0));

			// stockDParm.addData("SCRAP_VALUE", dTable.getItemData(i,
			// "LAST_PRICE"));
			stockDParm.addData("SCRAP_VALUE", 0);
			stockDParm.addData("QUALITY_LEVEL", "");
			stockDParm.addData("DEV_CLASS", devBase.getData("DEV_CLASS", 0));
			stockDParm.addData("STOCK_STATUS", "");
			stockDParm.addData("SERVICE_STATUS", "");
			stockDParm.addData("CARE_USER", getValue("OPERATOR"));
			stockDParm.addData("USE_USER", "");
			stockDParm.addData("LOC_CODE", "");

			stockDParm
					.addData("MEASURE_FLG", devBase.getData("MEASURE_FLG", 0));
			stockDParm.addData("MEASURE_ITEMDESC", devBase.getData(
					"MEASURE_ITEMDESC", 0));
			stockDParm.addData("MEASURE_DATE", "");
			stockDParm.addData("OPT_USER", Operator.getID());
			stockDParm.addData("OPT_DATE", timestamp);
			stockDParm.addData("OPT_TERM", Operator.getIP());

			stockDParm.addData("INWAREHOUSE_DATE", StringTool
					.getTimestampDate(timestamp));
			int maxDevSeqNo = 0;
			for (int j = 0; j < ddTable.getRowCount(); j++) {
				if (dTable.getValueAt(i, 2).equals(ddTable.getValueAt(j, 5))) {
					// if (!(dTable.getValueAt(i,
					// 3).equals(ddTable.getValueAt(j, 4))))
					// continue;
					// DEL_FLG
					if (ddTable.getValueAt(j, 0).equals("Y"))
						continue;
					// fux select_flg���޸�
					// //SELECT_FLG
					// if (ddTable.getValueAt(j, 1).equals("N"))
					// continue;
					for (int k = 0; k < ddParm.getCount("DEV_CODE"); ++k) {
						if ((!(ddParm.getValue("DEV_CODE", k).equals(ddTable
								.getValueAt(j, 5))))
								|| (maxDevSeqNo >= ddParm
										.getInt("DEVSEQ_NO", k)))
							continue;
						maxDevSeqNo = ddParm.getInt("DEVSEQ_NO", k);
					}
					if (maxDevSeqNo == 0) {
						maxDevSeqNo = DevInStorageTool.getInstance()
								.getMaxDevSeqNo("" + ddTable.getValueAt(j, 5));
					} else {
						maxDevSeqNo++;
					}
					// DEVKIND_CODE IN_DEPT OUT_DEPT
					// 2G5AH131125004 �����ʲ�����������롣����
					//  
					String barCode = SystemTool.getInstance().getNo("ALL",
							"DEV", "BARCODE_NO", "BARCODE_NO");
					// DEL_FLG;PRINT_FLG;DEVPRO_CODE;BATCH_SEQ;DEV_CODE;DDSEQ_NO;DEV_CHN_DESC;RFID;MAIN_DEV;MAN_DATE;MAN_SEQ;LAST_PRICE;
					// GUAREP_DATE;DEP_DATE;TOT_VALUE;INWAREHOUSE_NO;SEQ_NO;DEVSEQ_NO
					ddParm.addData("INWAREHOUSE_NO", inwarehouseNo);
					ddParm.addData("SEQ_NO", Integer.valueOf(i + 1));
					ddParm.addData("DEVSEQ_NO", Integer.valueOf(maxDevSeqNo));
					ddParm.addData("DEV_CODE", ddTable.getItemData(j,
							"DEV_CODE"));
					// ��Ź�����ϸ���
					// ddParm.addData("BATCH_SEQ", batchSeq);
					ddParm.addData("SEQMAN_FLG", "Y");
					if (devBase.getData("SETDEV_CODE", 0) != null) {
						ddParm.addData("SETDEV_CODE", devBase.getData(
								"SETDEV_CODE", 0));
					} else {
						ddParm.addData("SETDEV_CODE", "");
					}
					ddParm.addData("MAN_DATE", ddTable.getItemData(j,
							"MAN_DATE"));
					ddParm.addData("MANSEQ_NO", ddTable.getItemData(j,
							"MAN_SEQ"));
					// ddParm.addData("SCRAP_VALUE", ddTable.getItemData(j,
					// "LAST_PRICE"));
					ddParm.addData("SCRAP_VALUE", 0);
					ddParm.addData("GUAREP_DATE", ddTable.getItemData(j,
							"GUAREP_DATE"));
					ddParm.addData("DEP_DATE", ddTable.getItemData(j,
							"DEP_DATE"));
					ddParm.addData("UNIT_PRICE", dTable.getItemData(i,
							"UNIT_PRICE"));
					ddParm.addData("OPT_USER", Operator.getID());
					ddParm.addData("OPT_DATE", timestamp);
					ddParm.addData("OPT_TERM", Operator.getIP());
					// Ĭ��rfid��Ϊ"" ������ͨ����
					// 12 ------- �ĳ��ʲ����� 14λ�ģ�����
					ddParm.addData("BARCODE", barCode);
					ddParm.addData("RFID", "");
					ddParm.addData("DEV_CODE_DETAIL", ddTable.getItemData(j,
							"DEV_CODE_DETAIL"));
					ddParm.addData("SERIAL_NUM", ddTable.getItemData(i,
							"SERIAL_NUM"));
					if (ddTable.getItemData(i, "WIRELESS_IP") == null) {
						ddParm.addData("WIRELESS_IP", "");
					} else {
						ddParm.addData("WIRELESS_IP", ddTable.getItemData(i,
								"WIRELESS_IP"));
					}
					ddParm.addData("IP", ddTable.getItemData(i, "IP"));
					ddParm.addData("TERM", ddTable.getItemData(i, "TERM"));
					ddParm.addData("LOC_CODE", ddTable.getItemData(i,
							"LOC_CODE"));
					// SERIAL_NUM
					// WIRELESS_IP
					// IP
					// TERM
					// LOC_CODE

					devBase = getDevBase(""
							+ ddTable.getItemData(j, "DEV_CODE"));
					stockDDParm.addData("DEV_CODE_DETAIL", ddTable.getItemData(
							j, "DEV_CODE_DETAIL"));
					stockDDParm.addData("DEV_CODE", ddTable.getItemData(j,
							"DEV_CODE"));
					stockDDParm.addData("DEVSEQ_NO", Integer
							.valueOf(maxDevSeqNo));
					stockDDParm.addData("REGION_CODE", Operator.getRegion());
					// stockDDParm.addData("BATCH_SEQ", batchSeq);
					stockDDParm.addData("DEPT_CODE", getValue("DEPT"));
					stockDDParm.addData("STOCK_QTY", "1");
					stockDDParm.addData("UNIT_PRICE", dTable.getItemData(i,
							"UNIT_PRICE"));
					stockDDParm.addData("STOCK_UNIT", dTable.getItemData(i,
							"UNIT_CODE"));
					stockDDParm.addData("CHECKTOLOSE_FLG", "N");
					stockDDParm.addData("WAST_FLG", "N");
					stockDDParm.addData("INWAREHOUSE_DATE", StringTool
							.getTimestampDate(timestamp));
					stockDDParm.addData("WAIT_ORG_CODE", "");
					stockDDParm.addData("OPT_USER", Operator.getID());
					stockDDParm.addData("OPT_DATE", timestamp);
					stockDDParm.addData("OPT_TERM", Operator.getIP());
					// Ĭ��rfid��Ϊ"" ������ͨ����
					stockDDParm.addData("RFID", "");
					stockDDParm.addData("BARCODE", barCode);
					stockDDParm.addData("ACTIVE_FLG", devBase.getData(
							"ACTIVE_FLG", 0));
					stockDDParm.addData("SEQMAN_FLG", "Y");
					if (devBase.getData("SETDEV_CODE", 0) != null) {
						stockDDParm.addData("SETDEV_CODE", devBase.getData(
								"SETDEV_CODE", 0));
					} else {
						stockDDParm.addData("SETDEV_CODE", "");
					}
					// �۾��޸�
					// ԭֵ�����۾�ֵ���ۼ��۾�ֵ����ֵ(���޸�)
					 TParm parmDep =
					 DevDepTool.getInstance().selectSeqDevInf(getDevBase(ddTable.getItemString(j,
					 "DEV_CODE")),
					 dTable.getItemDouble(i, "UNIT_PRICE"),inWarehouseDate);
//					 parmDep{Data={MDEP_PRICE=100.00, CURR_PRICE=59900.00,
//					 DEP_PRICE=100.00}}
//					 System.out.println("parmDep"+parmDep);
					 Double mdepPrice =  
					 Double.parseDouble(parmDep.getData("MDEP_PRICE").toString());
					 Double depPrice =
					 Double.parseDouble(parmDep.getData("DEP_PRICE").toString());
					 Double currPrice =
					 Double.parseDouble(parmDep.getData("CURR_PRICE").toString());
//					 System.out.println("!"+ mdepPrice);
//					 System.out.println("@"+ depPrice);
//					 System.out.println("#"+ currPrice);

					 stockDDParm.addData("MDEP_PRICE", mdepPrice);
					 stockDDParm.addData("DEP_PRICE", depPrice);
					 stockDDParm.addData("CURR_PRICE", currPrice);
//					stockDDParm.addData("MDEP_PRICE", 0);
//					stockDDParm.addData("DEP_PRICE", 0);
//					stockDDParm.addData("CURR_PRICE", 0);
					// stockdd�����
					// MODEL,BRAND,SPECIFICATION,SERIAL_NUM;MAN_CODE;IP;TERM;LOC_CODE;USE_USER
					// stockDDParm.addData("MODEL", ddTable
					// .getItemData(i, "MODEL") == null ? "" : ddTable
					// .getItemData(i, "MODEL"));

					if (ddTable.getItemData(i, "MODEL") != null) {
						stockDDParm.addData("MODEL", ddTable.getItemData(i,
								"MODEL"));
					} else {
						stockDDParm.addData("MODEL", "");
					}
					if (ddTable.getItemData(i, "BRAND") != null) {
						stockDDParm.addData("BRAND", ddTable.getItemData(i,
								"BRAND"));
					} else {
						stockDDParm.addData("BRAND", "");
					}
					if (ddTable.getItemData(i, "SPECIFICATION") != null) {
						stockDDParm.addData("SPECIFICATION", ddTable
								.getItemData(i, "SPECIFICATION"));
					} else {
						stockDDParm.addData("SPECIFICATION", "");
					}
					if (ddTable.getItemData(i, "MAN_CODE") != null) {
						stockDDParm.addData("MAN_CODE", ddTable.getItemData(i,
								"MAN_CODE"));
					} else {
						stockDDParm.addData("MAN_CODE", "");
					}
					// if (ddTable.getItemData(i, "MAN_NATION") != null) {
					// stockDDParm.addData("MAN_NATION", ddTable
					// .getItemData(i, "MAN_NATION"));
					// } else {
					// stockDDParm.addData("MAN_NATION", "");
					// }
					stockDDParm.addData("MAN_NATION", devBase.getData(
							"MAN_NATION", 0));
					if (ddTable.getItemData(i, "USE_USER") == null) {
						stockDDParm.addData("USE_USER", "");
					} else {
						stockDDParm.addData("USE_USER", ddTable.getItemData(i,
								"USE_USER"));
					}

					if (ddTable.getItemData(i, "CARE_USER") == null) {
						stockDDParm.addData("CARE_USER", "");
					} else {
						stockDDParm.addData("CARE_USER", ddTable.getItemData(i,
								"CARE_USER"));
					}

					if (ddTable.getItemData(i, "SERIAL_NUM") == null) {
						stockDDParm.addData("SERIAL_NUM", "");
					} else {
						stockDDParm.addData("SERIAL_NUM", ddTable.getItemData(
								i, "SERIAL_NUM"));
					}

					if (ddTable.getItemData(i, "WIRELESS_IP") == null) {
						stockDDParm.addData("WIRELESS_IP", "");
					} else {
						stockDDParm.addData("WIRELESS_IP", ddTable.getItemData(
								i, "WIRELESS_IP"));
					}

					if (ddTable.getItemData(i, "IP") == null) {
						stockDDParm.addData("IP", "");
					} else {
						stockDDParm.addData("IP", ddTable.getItemData(i, "IP"));
					}

					if (ddTable.getItemData(i, "TERM") == null) {
						stockDDParm.addData("TERM", "");
					} else {
						stockDDParm.addData("TERM", ddTable.getItemData(i,
								"TERM"));
					}

					if (ddTable.getItemData(i, "LOC_CODE") == null) {
						stockDDParm.addData("LOC_CODE", "");
					} else {
						stockDDParm.addData("LOC_CODE", ddTable.getItemData(i,
								"LOC_CODE"));
					}
					stockDDParm.addData("GUAREP_DATE", dTable.getItemData(i,
							"GUAREP_DATE"));
					stockDDParm.addData("DEP_DATE", dTable.getItemData(i,
							"DEP_DATE"));
				}
			}
		}
		mParm.addData("INWAREHOUSE_NO", inwarehouseNo);
		mParm.addData("VERIFY_NO", dTable.getItemData(0, "VERIFY_NO"));
		mParm.addData("INWAREHOUSE_DATE", StringTool
				.getTimestampDate(timestamp));
		mParm.addData("INWAREHOUSE_USER", getValue("OPERATOR"));
		mParm.addData("INWAREHOUSE_DEPT", getValue("DEPT"));
		mParm.addData("OPT_USER", Operator.getID());
		mParm.addData("OPT_DATE", timestamp);
		mParm.addData("OPT_TERM", Operator.getIP());
		// System.out.println("mParm" + mParm);
		TParm parm = new TParm();
		parm.setData("DEV_INWAREHOUSEM", mParm.getData());
		parm.setData("DEV_INWAREHOUSED", dParm.getData());
		parm.setData("DEV_INWAREHOUSEDD", ddParm.getData());
		parm.setData("DEV_STOCKM", stockMParm.getData());
		parm.setData("DEV_STOCKD", stockDParm.getData());
		parm.setData("DEV_STOCKDD", stockDDParm.getData());
		//20150709 wangjc add start
		if(this.checkMtnDir(stockDDParm)){
			return;
		}else{
			parm.setData("DEV_MTN_DATE", this.getDevMtnDateParm(stockDDParm).getData());//ά��ʱ��
		}
		//20150709 wangjc add end
		// �¼�����
		TParm parmReceipt = new TParm();
		String receiptNo = this.getValueString("VERIFY_NO");
		parmReceipt.setData("RECEIPT_NO", receiptNo);
		parm.setData("DEV_RECEIPTD", getReceiptDData(parmReceipt).getData());
		parm.setData("DEV_RECEIPTM", getReceiptMData(parmReceipt).getData());
		parm.setData("CHECK_IN", this.getValue("CHECK_IN"));//20150605 wangjc add
//		System.out.println("parm======"+parm);
		TParm result = TIOM_AppServer.executeAction("action.dev.DevAction",
				"generateInStorageReceipt", parm);
		if (result.getErrCode() < 0) {
			messageBox("����ʧ��");
			err(result.getErrText());
			return;
		}
		messageBox("����ɹ�");
		// fux modify 20130815 ���������ӡ
		if(this.getValue("CHECK_IN").equals("Y")){
			onPrintBarcode();
			onPrintNew(parm);
		}
		// onPrintReceipt();
		onClear();
		onQuery();
	}
	
	/**
	 * ������ݣ�����豸�м������������ʿ�ע�ǣ�
	 * ��δ������Ӧ�ֵ䣬��ǿ�������ֵ��ſ���
	 * ��⡣
	 */
	public boolean checkMtnDir(TParm parm){
		for(int i=0;i<parm.getCount("DEV_CODE");i++){
			String devBaseSql = "SELECT MEASURE_FLG,MAINTENANCE_FLG,QUALITY_CONTROL_FLG "
					+ " FROM DEV_BASE WHERE DEV_CODE='"+parm.getValue("DEV_CODE", i)+"' ";
			TParm devBaseParm = new TParm(TJDODBTool.getInstance().select(devBaseSql));
			String sql = "";
			TParm result;
			if(devBaseParm.getValue("MEASURE_FLG", 0).equals("Y")){//����
				sql = "SELECT MEASUREM_CODE FROM DEV_MEASURE WHERE DEV_CODE='"
						+parm.getValue("DEV_CODE", i)+"' AND ACTIVE_FLG='Y' ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getCount()<=0){
					this.messageBox("�����ü����ֵ�");
					return true;
				}
			}
			if(devBaseParm.getValue("MAINTENANCE_FLG", 0).equals("Y")){//����
				sql = "SELECT MTN_TYPE_CODE FROM DEV_MAINTENANCEM WHERE DEV_CODE='"
						+parm.getValue("DEV_CODE", i)
						+"' AND MTN_KIND='0' AND ACTIVE_FLG='Y' ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getCount()<=0){
					this.messageBox("�����ñ����ֵ�");
					return true;
				}
			}
			if(devBaseParm.getValue("QUALITY_CONTROL_FLG", 0).equals("Y")){//�ʿ�
				sql = "SELECT MTN_TYPE_CODE FROM DEV_MAINTENANCEM WHERE DEV_CODE='"
						+parm.getValue("DEV_CODE", i)
						+"' AND MTN_KIND='1' AND ACTIVE_FLG='Y' ";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				if(result.getCount()<=0){
					this.messageBox("�������ʿ��ֵ�");
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * �����´μ������������ʿص�����
	 * @param parm
	 * @return
	 */
	public TParm getDevMtnDateParm(TParm parm){
		TParm result = new TParm();
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd");
		int n = 1;
		for(int i=0;i<parm.getCount("DEV_CODE");i++){
			String devBaseSql = "SELECT MEASURE_FLG,MAINTENANCE_FLG,QUALITY_CONTROL_FLG "
					+ " FROM DEV_BASE WHERE DEV_CODE='"+parm.getValue("DEV_CODE", i)+"' ";
//			System.out.println("devBaseSql:"+devBaseSql);
			TParm devBaseParm = new TParm(TJDODBTool.getInstance().select(devBaseSql));
			String sql = "";
			if(devBaseParm.getValue("MEASURE_FLG", 0).equals("Y")){//����
				sql = "SELECT * FROM DEV_MEASURE WHERE DEV_CODE='"
						+parm.getValue("DEV_CODE", i)+"' AND ACTIVE_FLG='Y' ";
//				System.out.println("sql:"+sql);
				TParm result1 = new TParm(TJDODBTool.getInstance().select(sql));
				if(result1.getCount()>0){
					for(int a=0;a<result1.getCount();a++){
						result.addData("DEV_CODE", parm.getValue("DEV_CODE", i));
						result.addData("MTN_KIND", "2");
						result.addData("MTN_TYPE_CODE", result1.getValue("MEASUREM_CODE", a));
						result.addData("DEVSEQ_NO", parm.getValue("DEV_CODE_DETAIL", i));
						result.addData("NEXT_MTN_DATE", sdf.format(new Date()));
						result.addData("OPT_USER", Operator.getID());
//						result.addData("OPT_DATE", "");
						result.addData("OPT_TERM", Operator.getIP());
						result.setCount(n);
						n++;
					}
				}
			}
			if(devBaseParm.getValue("MAINTENANCE_FLG", 0).equals("Y")){//����
				sql = "SELECT * FROM DEV_MAINTENANCEM WHERE DEV_CODE='"
						+parm.getValue("DEV_CODE", i)
						+"' AND MTN_KIND='0' AND ACTIVE_FLG='Y' ";
//				System.out.println("sql:"+sql);
				TParm result2 = new TParm(TJDODBTool.getInstance().select(sql));
				if(result2.getCount()>0){
					for(int b=0;b<result2.getCount();b++){
						result.addData("DEV_CODE", parm.getValue("DEV_CODE", i));
						result.addData("MTN_KIND", "0");
						result.addData("MTN_TYPE_CODE", result2.getValue("MTN_TYPE_CODE", b));
						result.addData("DEVSEQ_NO", parm.getValue("DEV_CODE_DETAIL", i));
						result.addData("NEXT_MTN_DATE", sdf.format(new Date()));
						result.addData("OPT_USER", Operator.getID());
//						result.addData("OPT_DATE", "");
						result.addData("OPT_TERM", Operator.getIP());
						result.setCount(n);
						n++;
					}
				}
			}
			if(devBaseParm.getValue("QUALITY_CONTROL_FLG", 0).equals("Y")){//�ʿ�
				sql = "SELECT * FROM DEV_MAINTENANCEM WHERE DEV_CODE='"
						+parm.getValue("DEV_CODE", i)
						+"' AND MTN_KIND='1' AND ACTIVE_FLG='Y' ";
//				System.out.println("sql:"+sql);
				TParm result3 = new TParm(TJDODBTool.getInstance().select(sql));
				if(result3.getCount()>0){
					for(int b=0;b<result3.getCount();b++){
						result.addData("DEV_CODE", parm.getValue("DEV_CODE", i));
						result.addData("MTN_KIND", "1");
						result.addData("MTN_TYPE_CODE", result3.getValue("MTN_TYPE_CODE", b));
						result.addData("DEVSEQ_NO", parm.getValue("DEV_CODE_DETAIL", i));
						result.addData("NEXT_MTN_DATE", sdf.format(new Date()));
						result.addData("OPT_USER", Operator.getID());
//						result.addData("OPT_DATE", "");
						result.addData("OPT_TERM", Operator.getIP());
						result.setCount(n);
						n++;
					}
				}
			}
		}
//		System.out.println("result>>"+result);
		return result;
	}

	// ���ڻ����ֵ�䶯,��������ҲҪ�Ķ�����øĳ�*
	/**
	 * �õ��豸����������Ϣ
	 * 
	 * @param devCode
	 *            String
	 * @return TParm
	 */
	public TParm getDevBase(String devCode) {
		String SQL = "SELECT * FROM DEV_BASE WHERE DEV_CODE = '" + devCode
				+ "'";
		TParm parm = new TParm(getDBTool().select(SQL));
		return parm;
	}

	/**
	 * �õ��豸���������Ϣ
	 * 
	 * @param code
	 *            String
	 * @return String
	 */
	private String getDevTypeCode(String code) {
		TParm parm = DevTypeTool.getInstance().getDevRule();
		int classify1 = parm.getInt("CLASSIFY1", 0);
		return code.substring(0, classify1);
	}

	/**
	 * ���յ�ϸ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getReceiptDData(TParm parm) {
		TTable dTable = (TTable) getComponent("DEV_INWAREHOUSED");
		TParm receipt = new TParm();
		for (int i = 0; i < dTable.getRowCount(); i++) {
			if ("Y".equals(dTable.getItemString(i, "DEL_FLG"))) {
				continue;
			} else if ("".equals(dTable.getParmValue().getValue("DEV_CODE", i))) {
				continue;
			} else {
				receipt.addData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
				receipt.addData("SEQ_NO", ReceiptD.getData("SEQ_NO", i));
				receipt.addData("QTY", dTable.getItemDouble(i, "QTY"));
				// QTY;SUM_QTY;RECEIPT_QTY
				// dTable.getParmValue().getDouble("SUM_QTY", i) ==
				if (dTable.getItemDouble(i, "QTY") == dTable.getParmValue()
						.getDouble("RECEIPT_QTY", i)
						|| dTable.getItemDouble(i, "SUM_QTY") == dTable
								.getParmValue().getDouble("RECEIPT_QTY", i)) {
					receipt.addData("FINAL_FLG", "1");
				} else {
					receipt.addData("FINAL_FLG", "0");
				}
				receipt.addData("OPT_USER", Operator.getID());
				receipt.addData("OPT_DATE", SystemTool.getInstance().getDate());
				receipt.addData("OPT_TERM", Operator.getIP());
			}
		}
		return receipt;
	}

	/**
	 * ���յ���������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	public TParm getReceiptMData(TParm parm) {
		TParm ReceiptM = new TParm();
		ReceiptM.addData("RECEIPT_NO", parm.getData("RECEIPT_NO"));
		boolean flg = true;
		TParm ReceiptD = parm.getParm("DEV_RECEIPTD");
		for (int i = 0; i < ReceiptD.getCount("RECEIPT_NO"); i++) {
			if ("0".equals(ReceiptD.getValue("FINAL_FLG", i))) {
				flg = false;
				break;
			}
		}
		if (flg) {
			ReceiptM.addData("FINAL_FLG", "1");
		} else {
			ReceiptM.addData("FINAL_FLG", "0");
		}
		ReceiptM.addData("OPT_USER", Operator.getID());
		ReceiptM.addData("OPT_DATE", SystemTool.getInstance().getDate());
		ReceiptM.addData("OPT_TERM", Operator.getIP());
		return ReceiptM;
	}

	/**
	 * ������ӡ�豸��ⵥ
	 * 
	 * @param inParm
	 *            TParm
	 */
	public void onPrintNew(TParm inParm) {
		// ���ñ�ͷ��Ϣ
		TParm printParm = new TParm();
		printParm.setData("HOSP_NAME_T", Operator.getHospitalCHNFullName());
		printParm.setData("INWAREHOUSE_NO_T", inParm
				.getParm("DEV_INWAREHOUSEM").getData("INWAREHOUSE_NO", 0));
		printParm.setData("MAN_CODE_T", getSupDesc(getValueString("SUP_CODE")));
		printParm.setData("INWAREHOUSE_DEPT_T", getDeptDesc(inParm.getParm(
				"DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DEPT", 0)));
		printParm.setData("INWAREHOUSE_DATE_T", inParm.getParm(
				"DEV_INWAREHOUSEM").getValue("INWAREHOUSE_DATE", 0).substring(
				0, 10).replace('-', '/'));
		printParm.setData("CONTACT_T", getValue("SUP_BOSSNAME"));
		printParm.setData("INWAREHOUSE_USER_T", getOperatorName(inParm.getParm(
				"DEV_INWAREHOUSEM").getValue("INWAREHOUSE_USER", 0)));
		printParm.setData("VERIFY_NO_T", inParm.getParm("DEV_INWAREHOUSEM")
				.getValue("VERIFY_NO", 0));
		printParm.setData("TEL_NO_T", getValue("SUP_TEL"));
		TParm parm = new TParm();
		// �����ݿ�������ⵥ����ε���
		for (int i = 0; i < inParm.getParm("DEV_INWAREHOUSED").getCount(
				"DEV_CODE"); i++) {
			cloneTParm(inParm.getParm("DEV_INWAREHOUSED"), parm, i);
		}
		// ת��ʱ�����ڸ�ʽ
		for (int i = 0; i < parm.getCount("DEVPRO_CODE"); i++) {
			parm.setData("MAN_DATE", i, parm.getValue("MAN_DATE", i).substring(
					0, 10).replace('-', '/'));
			parm.setData("GUAREP_DATE", i, parm.getValue("GUAREP_DATE", i)
					.substring(0, 10).replace('-', '/'));
			parm.setData("DEP_DATE", i, parm.getValue("DEP_DATE", i).substring(
					0, 10).replace('-', '/'));
			parm.setData("DEVPRO_CODE", i, getDevProDesc(parm.getValue(
					"DEVPRO_CODE", i)));
		}
		// ������ⵥ�����Ϣ
		parm.setCount(inParm.getParm("DEV_INWAREHOUSED").getCount("DEV_CODE"));
		parm.addData("SYSTEM", "COLUMNS", "DEVPRO_CODE");
//		parm.addData("SYSTEM", "COLUMNS", "BATCH_SEQ");
		parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "QTY");
		parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
		parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");
		parm.addData("SYSTEM", "COLUMNS", "SCRAP_VALUE");
		parm.addData("SYSTEM", "COLUMNS", "GUAREP_DATE");
		parm.addData("SYSTEM", "COLUMNS", "DEP_DATE");
		printParm.setData("TABLE", parm.getData());
		openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",
				printParm);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.setValue("CHECK_IN", "N");
    	((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(false);
		setValue("INWAREHOUSE_NO_QUARY", "");
		setValue("INWAREHOUSE_DATE", "");
		((TTextFormat) getComponent("INWAREHOUSE_DATE")).setEnabled(true);
		setValue("VERIFY_NO", "");
		setValue("DEPT", "");
		((TTextFormat) getComponent("DEPT")).setEnabled(true);
		setValue("OPERATOR", "");
		((TTextFormat) getComponent("OPERATOR")).setEnabled(true);
		((TTable) getComponent("RECEIPT")).removeRowAll();
		((TTable) getComponent("RECEIPT")).resetModify();
		setValue("SUP_CODE", "");
		setValue("SUP_BOSSNAME", "");
		setValue("SUP_TEL", "");
		parmD = new TParm();
		parmDD = new TParm();
		initComponent();
		initTableD();
		initTableDD();
		addDRow();
		setTableLock();
	}

	/**
	 * �豸��ⵥ����Ϣ�����¼�
	 */
	public void onDevInwarehouse() {
//		messageBox("�豸��ⵥ����Ϣ�����¼�");
		// fux 2013
		if (getRadioButton("UPDATE_FLG_A").isSelected()) {
        	this.setValue("CHECK_IN", "N");
        	((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(true);
        }else{
        	this.setValue("CHECK_IN", "Y");
        	((TCheckBox)this.getComponent("CHECK_IN")).setEnabled(false);
        }
		TTable table = ((TTable) getComponent("RECEIPT"));
		int row = table.getSelectedRow();
		TParm parm = DevInStorageTool.getInstance().selectDevInwarehouseD(
				"" + table.getValueAt(row, 1));
		if (parm.getErrCode() < 0)
			return;
		parmD = new TParm();
		for (int i = 0; i < parm.getCount(); i++)
			cloneTParm(parm, parmD, i);
		((TTable) getComponent("DEV_INWAREHOUSED")).setParmValue(parm);
		parm = DevInStorageTool.getInstance().selectDevInwarehouseDD(
				"" + table.getValueAt(row, 1));
		if (parm.getErrCode() < 0)
			return;
		parmDD = new TParm();

		for (int i = 0; i < parm.getCount(); i++)
			cloneTParm(parm, parmDD, i);
		((TTable) getComponent("DEV_INWAREHOUSEDD")).setParmValue(parm);
		((TTable) getComponent("DEV_INWAREHOUSED"))
				.setLockColumns("0,1,2,3,4,5,6," + "7,8,9,10,12,15,"
						+ "16,17,18,19,20," + "21,22,24,25,26,27,28");
		((TTable) getComponent("DEV_INWAREHOUSEDD"))
				.setLockColumns("0,1,2,3,4,5,10," + "11,12,13,14,15");
		setValue("INWAREHOUSE_NO_QUARY", table.getValueAt(row, 1));
		setValue("INWAREHOUSE_DATE", table.getValueAt(row, 0));
		((TTextFormat) getComponent("INWAREHOUSE_DATE")).setEnabled(false);
		setValue("VERIFY_NO", table.getValueAt(row, 3));
		setValue("DEPT", table.getValueAt(row, 2));
		((TTextFormat) getComponent("DEPT")).setEnabled(false);
		setValue("OPERATOR", table.getValueAt(row, 5));
		((TTextFormat) getComponent("OPERATOR")).setEnabled(false);
		if (("" + table.getValueAt(row, 3)).length() == 0)
			return;
		TParm supParm = getSupInf("" + table.getValueAt(row, 3));
		if (supParm.getErrCode() < 0)
			return;
		if (supParm.getCount() < 0)
			return;
		setValue("SUP_CODE", supParm.getValue("SUP_CODE", 0));
		setValue("SUP_BOSSNAME", supParm.getValue("SUP_SALES1", 0));
		setValue("SUP_TEL", supParm.getValue("SUP_SALES1_TEL", 0));
		
		
	}

	/**
	 * ����TParm
	 * 
	 * @param from
	 *            TParm
	 * @param to
	 *            TParm
	 * @param row
	 *            int
	 */
	private void cloneTParm(TParm from, TParm to, int row) {
		String names[] = from.getNames();
		for (int i = 0; i < names.length; i++) {
			Object obj = from.getData(names[i], row);
			if (obj == null)
				obj = "";
			to.addData(names[i], obj);
		}
	}

	/**
	 * �Ƚ���ͬ��TParm��ֵ�Ƿ�ı�
	 * 
	 * @param parmA
	 *            TParm
	 * @param parmB
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	private boolean compareTo(TParm parmA, TParm parmB, int row) {
		String names[] = parmA.getNames();
		for (int i = 0; i < names.length; i++) {
			if (parmA.getValue(names[i], row).equals(
					parmB.getValue(names[i], row)))
				continue;
			return false;
		}
		return true;
	}

	/**
	 * ��˻����Ƿ�����Ҫ�������Ϣ
	 * 
	 * @return boolean
	 */
	private boolean onSaveCheck() {
		int rowCount = 0;
		TTable tableD = getTTable("DEV_INWAREHOUSED");
		TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
		for (int i = 0; i < tableD.getRowCount(); i++) {
			if (("" + tableD.getValueAt(i, 0)).equals("N")
					&& ("" + tableD.getValueAt(i, 3)).length() != 0)
				rowCount++;
		}
		if (getTTable("RECEIPT").getSelectedRow() < 0 && rowCount == 0) {
			messageBox("�ޱ�����Ϣ");
			return true;
		}
		// fux modify 20140520T

		for (int i = 0; i < tableD.getRowCount(); i++) {
			double contdd = 0;
			if (tableD.getItemData(i, "SEQMAN_FLG").equals("N"))
				continue;
			double contd = tableD.getItemDouble(i, "QTY");
			for (int j = 0; j < tableDD.getRowCount(); j++) {
				if ("Y".equals(tableDD.getItemData(j, "SELECT_FLG").toString())
						&& tableD.getItemData(i, "DEV_CODE").equals(
								tableDD.getItemData(j, "DEV_CODE"))) {
					contdd++;
				} else
					continue;
			}
			if (contd != contdd) {
				this.messageBox("�豸"
						+ tableD.getItemData(i, "DEV_CODE").toString()
						+ "��ϸ��ѡ�������������������һ��");
				return true;
			}
		}
		return false;
	}

	/**
	 * �豸��ⵥ��ӡ
	 */
	public void onPrintReceipt() {
		TTable table = (TTable) getComponent("RECEIPT");
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
		TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
		for (int i = 0; i < tableParmD.getCount("INWAREHOUSE_NO"); i++) {
			if (compareTo(parmD, tableParmD, i))
				continue;
			messageBox("�������ݽ������޸��뱣����ٴ�ӡ");
			return;
		}
		for (int i = 0; i < tableParmDD.getCount("INWAREHOUSE_NO"); i++) {
			if (compareTo(parmDD, tableParmDD, i))
				continue;
			messageBox("�������ݽ������޸��뱣����ٴ�ӡ");
			return;
		}
		TParm printParm = new TParm();
		printParm.setData("HOSP_NAME_T", Operator.getHospitalCHNFullName());
		printParm.setData("INWAREHOUSE_NO_T", table.getValueAt(row, 1));
		printParm.setData("MAN_CODE_T", getSupDesc(getValueString("SUP_CODE")));
		printParm.setData("INWAREHOUSE_DEPT_T", getDeptDesc(""
				+ table.getValueAt(row, 2)));
		printParm.setData("INWAREHOUSE_DATE_T", ("" + table.getValueAt(row, 0))
				.substring(0, 10).replace('-', '/'));
		printParm.setData("CONTACT_T", getValue("SUP_BOSSNAME"));
		printParm.setData("INWAREHOUSE_USER_T", getOperatorName(""
				+ table.getValueAt(row, 5)));
		printParm.setData("VERIFY_NO_T", table.getValueAt(row, 3));
		printParm.setData("TEL_NO_T", getValue("SUP_TEL"));
		TParm tableParm = ((TTable) getComponent("DEV_INWAREHOUSED"))
				.getParmValue();
		TParm parm = new TParm();
		for (int i = 0; i < tableParm.getCount("DEV_CODE"); i++) {
			cloneTParm(tableParm, parm, i);
		}
		for (int i = 0; i < parm.getCount("DEVPRO_CODE"); i++) {
			parm.setData("MAN_DATE", i, parm.getValue("MAN_DATE", i).substring(
					0, 10).replace('-', '/'));
			parm.setData("GUAREP_DATE", i, parm.getValue("GUAREP_DATE", i)
					.substring(0, 10).replace('-', '/'));
			parm.setData("DEP_DATE", i, parm.getValue("DEP_DATE", i).substring(
					0, 10).replace('-', '/'));
			parm.setData("DEVPRO_CODE", i, getDevProDesc(parm.getValue(
					"DEVPRO_CODE", i)));
		}
		parm.setCount(tableParm.getCount("DEV_CODE"));
		// ��ⵥ��
		parm.addData("SYSTEM", "COLUMNS", "INWAREHOUSE_NO");
		// ��������
		parm.addData("SYSTEM", "COLUMNS", "MAN_CODE_T");
		// �������
		parm.addData("SYSTEM", "COLUMNS", "INWAREHOUSE_DATE");
		// �豸����
		parm.addData("SYSTEM", "COLUMNS", "DEV_CHN_DESC");
		// ���
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		// ��λ
		parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		// ����
		parm.addData("SYSTEM", "COLUMNS", "QTY");
		// ����
		parm.addData("SYSTEM", "COLUMNS", "UNIT_PRICE");
		// �ܽ��
		parm.addData("SYSTEM", "COLUMNS", "TOT_VALUE");
		// ��Ʊ����
		parm.addData("SYSTEM", "COLUMNS", "MAN_DATE");
		// ��Ʊ����
		parm.addData("SYSTEM", "COLUMNS", "LAST_PRICE");
		printParm.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion())
				+ "��ⵥ");

		printParm.setData("OPT_USER", "TEXT", this.getValueString("OPERATOR"));
		printParm.setData("PUR_USER", "TEXT", this.getValueString("OPERATOR"));
		printParm.setData("VER_USER", "TEXT", this.getValueString("OPERATOR"));
		printParm.setData("OPT_DATE", "TEXT", this.getValueString(
				"INWAREHOUSE_DATE").substring(0, 10));
		printParm.setData("DEPT", "TEXT", getDeptCostDesc(this
				.getValueString("DEPT")));

		printParm.setData("TABLE", parm.getData());
		openPrintWindow("%ROOT%\\config\\prt\\dev\\DevInStorageReceipt.jhw",
				printParm);
	}

	/**
	 * ȡ���豸������������
	 * 
	 * @param devProCode
	 *            String
	 * @return String
	 */
	public String getDevProDesc(String devProCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT CHN_DESC FROM SYS_DICTIONARY "
						+ " WHERE GROUP_ID = 'DEVPRO_CODE'" + " AND   ID = '"
						+ devProCode + "'"));
		return parm.getValue("CHN_DESC", 0);
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �õ�����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getDeptDesc(String deptCode) {
		TParm parm = new TParm(getDBTool().select(
				"SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
						+ deptCode + "'"));
		return parm.getValue("DEPT_CHN_DESC", 0);
	}

	/**
	 * �õ��û���
	 * 
	 * @param userID
	 *            String
	 * @return String
	 */
	public String getOperatorName(String userID) {
		TParm parm = new TParm(getDBTool().select(
				"SELECT USER_NAME FROM SYS_OPERATOR WHERE USER_ID='" + userID
						+ "'"));
		return parm.getValue("USER_NAME", 0);
	}

	/**
	 * �õ���Ӧ����
	 * 
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getSupDesc(String supCode) {
		TParm parm = new TParm(getDBTool().select(
				"SELECT SUP_CHN_DESC FROM SYS_SUPPLIER WHERE SUP_CODE='"
						+ supCode + "'"));
		return parm.getValue("SUP_CHN_DESC", 0);
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * �豸¼���¼�
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComoponent(Component com, int row, int column) {
		// �豸����
		String devProCode = ""
				+ getTTable("DEV_INWAREHOUSED").getValueAt(row, 1);
		// ״̬����ʾ
		callFunction("UI|setSysStatus", "");
		// �õ�����
		String columnName = getFactColumnName("DEV_INWAREHOUSED", column);
		if (!"DEV_CODE".equals(columnName))
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		if (("" + getTTable("DEV_INWAREHOUSED").getValueAt(row, column))
				.length() != 0)
			return;
		TParm parm = new TParm();
		parm.setData("DEVPRO_CODE", devProCode);
		parm.setData("ACTIVE", "Y");
		// ���õ����˵�
		textFilter.setPopupMenuParameter("DEVBASE", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\DEVBASEPopupUI.x"), parm);
		// ������ܷ���ֵ����
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * �豸¼�뷵��ֵ�¼�
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popReturn(String tag, Object obj) {
		// �ж϶����Ƿ�Ϊ�պ��Ƿ�ΪTParm����
		if (obj == null && !(obj instanceof TParm))
			return;
		if (getValueString("INWAREHOUSE_DATE").length() == 0) {
			messageBox("��¼���������");
			return;
		}
		// ����ת����TParm
		TParm parm = (TParm) obj;
		Timestamp timestamp = StringTool.getTimestamp(SystemTool.getInstance()
				.getDate().toString(), "yyyy-MM-dd");

		String inWarehouseDate = getValueString("INWAREHOUSE_DATE");
		// �������������
		int year = Integer.parseInt(inWarehouseDate.substring(0, 4))
				+ parm.getInt("USE_DEADLINE");
		String depDate = year
				+ inWarehouseDate.substring(4, inWarehouseDate.length());
		for (int i = 0; i < getTTable("DEV_INWAREHOUSED").getRowCount(); i++) {
			if (!parm.getValue("DEV_CODE").equals(
					getTTable("DEV_INWAREHOUSED").getValueAt(i, 2)))
				continue;
			// if(batchSeq !=
			// Integer.parseInt(""+getTTable("DEV_INWAREHOUSED").getValueAt(i,
			// 3)))
			// continue;
			messageBox("���豸�Ѿ�����,���ѽ�������ӵ���Ӧ����");
			int qty = 1 + Integer.parseInt(""
					+ getTTable("DEV_INWAREHOUSED").getValueAt(i, 7));
			updateTableData("DEV_INWAREHOUSED", i, 7, qty);
			updateTableData("DEV_INWAREHOUSED", i, 12, qty
					* Double.parseDouble(""
							+ getTTable("DEV_INWAREHOUSED").getValueAt(i, 11)));
			updateTableData("DEV_INWAREHOUSED", getTTable("DEV_INWAREHOUSED")
					.getSelectedRow(), 2, "");
			resetDDTableData();
			return;
		}
		callFunction("UI|setSysStatus", parm.getValue("DEV_CODE") + ":"
				+ parm.getValue("DEV_CHN_DESC")
				+ parm.getValue("SPECIFICATION"));
		getTTable("DEV_INWAREHOUSED").acceptText();
		TParm tableParm = new TParm();
		TParm tableParmDD = new TParm();
		tableParm.setData("DEL_FLG", "N");
		tableParm.setData("DEVPRO_CODE", parm.getData("DEVPRO_CODE"));
		tableParm.setData("DEV_CODE", parm.getData("DEV_CODE"));
		// tableParm.setData("BATCH_SEQ",batchSeq);
		tableParm.setData("DEV_CHN_DESC", parm.getData("DEV_CHN_DESC"));
		tableParm.setData("SPECIFICATION", parm.getData("SPECIFICATION"));
		tableParm.setData("MAN_CODE", parm.getData("MAN_CODE"));
		tableParm.setData("QTY", 1);
		tableParm.setData("SUM_QTY", 0);
		tableParm.setData("RECEIPT_QTY", 0);
		tableParm.setData("UNIT_CODE", parm.getData("UNIT_CODE"));
		tableParm.setData("UNIT_PRICE", parm.getData("UNIT_PRICE"));
		tableParm.setData("TOT_VALUE", parm.getDouble("UNIT_PRICE"));
		tableParm.setData("MAN_DATE", timestamp);
		tableParm.setData("LAST_PRICE", parm.getData("UNIT_PRICE"));
		tableParm.setData("GUAREP_DATE", timestamp);
		tableParm.setData("DEPR_METHOD", parm.getData("DEPR_METHOD"));
		tableParm.setData("USE_DEADLINE", parm.getData("USE_DEADLINE"));
		tableParm.setData("DEP_DATE", StringTool.getTimestamp(depDate,
				"yyyy-MM-dd"));
		tableParm.setData("MAN_NATION", parm.getData("MAN_NATION"));
		tableParm.setData("SEQMAN_FLG", parm.getData("SEQMAN_FLG"));
		tableParm.setData("MEASURE_FLG", parm.getData("MEASURE_FLG"));
		tableParm.setData("BENEFIT_FLG", parm.getData("BENEFIT_FLG"));
		tableParm.setData("FILES_WAY", "");
		tableParm.setData("VERIFY_NO", "");
		tableParm.setData("VERIFY_NO_SEQ", "");
		tableParm.setData("INWAREHOUSE_NO", "");
		tableParm.setData("SEQ_NO", "");
		tableParm.setData("DEVKIND_CODE", parm.getData("DEVKIND_CODE"));
		((TTable) getComponent("DEV_INWAREHOUSED"))
				.removeRow(((TTable) getComponent("DEV_INWAREHOUSED"))
						.getRowCount() - 1);
		((TTable) getComponent("DEV_INWAREHOUSED")).addRow(tableParm);
		// ����Ź�����豸չ����Ź�����ϸ
		if ("Y".equals(parm.getData("SEQMAN_FLG"))) {
			setDevDDTablePop(tableParmDD, tableParm);
			((TTable) getComponent("DEV_INWAREHOUSEDD")).addRow(tableParmDD);
		}
		addDRow();
		setTableLock();
		((TTextFormat) getComponent("INWAREHOUSE_DATE")).setEnabled(false);
	}

	/**
	 * ����������Ź�����ϸ��Ϣ
	 */
	public void resetDDTableData() {
		TTable tableD = getTTable("DEV_INWAREHOUSED");
		TParm parmD = tableD.getParmValue();
		TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
		TParm parmDD = new TParm();
		for (int i = 0; i < parmD.getCount("DEV_CODE"); i++) {
//			if (parmD.getValue("BATCH_SEQ", i).length() == 0)
//				continue;
			if (!"Y".equals(parmD.getValue("SEQMAN_FLG", i)))
				continue;
			addDevDDTable(parmDD, parmD, i);
		}
		tableDD.setParmValue(parmDD);
	}

	/**
	 * �����豸¼��ʱ��Ź�����ϸ��Ϣ
	 * 
	 * @param tableParmDD
	 *            TParm
	 * @param tableParm
	 *            TParm
	 */
	public void setDevDDTablePop(TParm tableParmDD, TParm tableParm) {
		tableParmDD.setData("DEL_FLG", "N");
		tableParmDD.setData("DEVPRO_CODE", tableParm.getData("DEVPRO_CODE"));
//		tableParmDD.setData("BATCH_SEQ", tableParm.getData("BATCH_SEQ"));
		tableParmDD.setData("DEV_CODE", tableParm.getData("DEV_CODE"));
		tableParmDD.setData("DDSEQ_NO", 1);
		tableParmDD.setData("DEV_CHN_DESC", tableParm.getData("DEV_CHN_DESC"));
		tableParmDD.setData("MAIN_DEV", "");
		tableParmDD.setData("MAN_DATE", tableParm.getData("MAN_DATE"));
		tableParmDD.setData("MAN_SEQ", "");
		tableParmDD.setData("LAST_PRICE", tableParm.getData("LAST_PRICE"));
		tableParmDD.setData("GUAREP_DATE", tableParm.getData("GUAREP_DATE"));
		tableParmDD.setData("DEP_DATE", tableParm.getData("DEP_DATE"));
		tableParmDD.setData("TOT_VALUE", tableParm.getData("UNIT_PRICE"));
		tableParmDD.setData("INWAREHOUSE_NO", "");
		tableParmDD.setData("SEQ_NO", "");
		tableParmDD.setData("DEVSEQ_NO", "");
	}

	/**
	 * �õ��������
	 * 
	 * @param tableTag
	 *            String
	 * @param column
	 *            int
	 * @return String
	 */
	public String getFactColumnName(String tableTag, int column) {
		int col = getThisColumnIndex(column);
		return getTTable(tableTag).getDataStoreColumnName(col);
	}

	/**
	 * �õ����������
	 * 
	 * @param column
	 *            int
	 * @return int
	 */
	public int getThisColumnIndex(int column) {
		return getTTable("DEV_INWAREHOUSED").getColumnModel().getColumnIndex(
				column);
	}

	/**
	 * �˵�table�����¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableComponent(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		return true;
	}

	/**
	 * �򿪸���(rfid�����ʱ����)
	 */
	public void onAddRfid() {
		if ("".equals(getValueString("DEPT"))) {
			messageBox("�����Ҳ���Ϊ��");
			return;
		}
		TTable tableDD = getTTable("DEV_INWAREHOUSEDD");
		tableDD.acceptText();
		TParm parm = tableDD.getShowParmValue();
		Object result = openDialog("%ROOT%\\config\\dev\\DEVBarcodeAndRFID.x",
				parm);
		if (result != null) {
			TParm addParm = (TParm) result;
			if (addParm == null)
				;
			Map map = new HashMap();
			String rfid;
			for (int i = 0; i < addParm.getCount("RFID"); ++i) {
				rfid = addParm.getValue("RFID", i);
				String code = addParm.getValue("ORGIN_CODE", i);
				map.put(rfid, code);
			}
			for (int i = 0; i < tableDD.getRowCount(); ++i) {
				rfid = tableDD.getItemData(i, "RFID").toString();
				tableDD.setItem(i, "ORGIN_CODE", map.get(rfid));
			}
		}
	}

	// //��ӡ��
	// /**
	// * �����豸����
	// */
	// public void onBarcodePrint(){
	// if(getTTable("RECEIPT").getSelectedRow() < 0){
	// messageBox("��ѡ��һ����ⵥ�ٴ�ӡ����");
	// return;
	// }
	// TParm tableParmD = getTTable("DEV_INWAREHOUSED").getParmValue();
	// TParm tableParmDD = getTTable("DEV_INWAREHOUSEDD").getParmValue();
	// for(int i = 0;i<tableParmD.getCount("INWAREHOUSE_NO");i++){
	// if(compareTo(parmD,tableParmD,i))
	// continue;
	// messageBox("�������ݽ������޸��뱣����ڴ�ӡ");
	// return;
	// }
	// for(int i = 0;i<tableParmDD.getCount("INWAREHOUSE_NO");i++){
	// if(compareTo(parmDD,tableParmDD,i))
	// continue;
	// messageBox("�������ݽ������޸��뱣����ڴ�ӡ");
	// return;
	// }
	// TParm parm = new TParm();
	// parm.setData("DEV_INWAREHOUSED",((TTable)getComponent("DEV_INWAREHOUSED")).getParmValue().getData());
	// parm.setData("DEV_INWAREHOUSEDD",((TTable)getComponent("DEV_INWAREHOUSEDD")).getParmValue().getData());
	// parm.setData("INWAREHOUSE_NO",getValue("INWAREHOUSE_NO_QUARY"));
	// parm.setData("INWAREHOUSE_DATE",getValue("INWAREHOUSE_DATE"));
	// parm.setData("INWAREHOUSE_DEPT",getValue("DEPT"));
	// parm.setData("INWAREHOUSE_USER",getValue("OPERATOR"));
	// openDialog("%ROOT%\\config\\dev\\DEVBarcodeUI.x", parm);
	// }
	/**
	 * ��ӡ����(�������)
	 */
	// fux need modify ��ӡһ������
	public void onPrintBarcode() {
		if (getTTable("DEV_INWAREHOUSEDD").getRowCount() <= 0) {
			messageBox("��ѡ����ϸ��");
		}
		TParm parm = getTTable("DEV_INWAREHOUSEDD").getShowParmValue();

		for (int i = 0; i < getTTable("DEV_INWAREHOUSEDD").getRowCount(); ++i) {
			TParm newParm = new TParm();
			// SELECT_FLG;PRINT_FLG
			if (!("Y".equals(getTTable("DEV_INWAREHOUSEDD").getItemData(i,
					"SELECT_FLG")))) {
				continue;
			}
			if (!("Y".equals(getTTable("DEV_INWAREHOUSEDD").getItemData(i,
					"PRINT_FLG")))) {
				continue;
			}

			// newParm.setData("DEV_CODE", parm.getData("DEV_CODE",
			// i).toString()
			// .trim());
			TParm printParm = new TParm();
			// ����������
			// newParm.setData("DEV_DEPT", this.getValueString("DEPT"));
			// newParm.setData("DEV_DESC", parm.getData("DEV_CHN_DESC", i));
			// newParm.setData("DEV_BARCODE", parm.getData("DEV_CODE_DETAIL",
			// i));
//			System.out.println("parm====="+parm);
			newParm.setData("DEV_DEPT", "TEXT", getDeptCostDesc(this
					.getValueString("DEPT")));
			newParm
					.setData("DEV_DESC", "TEXT", parm
							.getData("DEV_CHN_DESC", i));
			newParm.setData("DEV_BARCODE", "TEXT", parm.getData(
					"DEV_CODE_DETAIL", i));
			openPrintWindow("%ROOT%\\config\\prt\\dev\\DevBarcode.jhw", newParm);
			try {
				Thread.sleep(500L);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ��ѯ�ɱ���������
	 */
	private String getDeptCostDesc(String data) {
		String sql = " SELECT A.COST_CENTER_CHN_DESC FROM SYS_COST_CENTER A "
				+ " WHERE A.COST_CENTER_CODE = '" + data + "' ";
		// messageBox("sql:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		String result = parm.getValue("COST_CENTER_CHN_DESC", 0);
		return result;
	}

	/**
	 * ˫�����ʲ���Ƭ����
	 */
	public void onDoubleClicked(int row) {
		if (row < 0)
			return;
		callFunction("UI|setVisible", false);
		onSelected();
	}

	/**
	 * ѡ��
	 */
	public void onSelected() {
		TParm tableParm = new TParm();
		int row = getTTable("DEV_INWAREHOUSEDD").getSelectedRow();
		if (row < 0)
			return;
		TParm exportParm = getTTable("DEV_INWAREHOUSEDD").getParmValue()
				.getRow(row);
		String devCodenew = getTTable("DEV_INWAREHOUSEDD").getParmValue()
				.getRow(row).getValue("DEV_CODE").toString();
		// rowΪ��һ��
		if (row == 0) {
			exportParm.setData("DEV_CODE_DETAIL_OLD", 0, "");
		}
		// row����1��
		if (row >= 1) {
			String devCodeold = getTTable("DEV_INWAREHOUSEDD").getParmValue()
					.getRow(row - 1).getValue("DEV_CODE").toString();
			// ���ѡ���к���һ��        
			if (devCodeold == devCodenew) {
				exportParm.setData("DEV_CODE_DETAIL_OLD", 0, getTTable(
						"DEV_INWAREHOUSEDD").getShowParmValue().getRow(row - 1)
						.getValue("DEV_CODE_DETAIL"));
			} else {
				exportParm.setData("DEV_CODE_DETAIL_OLD", 0, "");
			}
			
		}
		exportParm.setData("DEV_CODE_DETAIL",getTTable(    
		"DEV_INWAREHOUSEDD").getShowParmValue().getRow(row)
		.getValue("DEV_CODE_DETAIL") );                         
		exportParm.setData("ROW", row);
		// �ʲ���Ƭ����д�����������  
//		int tot = 0;
//		for (int j = 0; j < getTTable("DEV_INWAREHOUSEDD").getRowCount(); j++) {
//			
//			String devCodeShow = getTTable("DEV_INWAREHOUSEDD").getParmValue()
//					.getValue("DEV_CODE", j);
//			if (devCodeShow == devCodenew) {
//				if (getTTable("DEV_INWAREHOUSEDD").getShowParmValue().getRow(j)
//						.getValue("DEV_CODE_DETAIL") == null
//						|| "".equals(getTTable("DEV_INWAREHOUSEDD")
//								.getShowParmValue().getRow(j).getValue(
//										"DEV_CODE_DETAIL"))) {
//					tot++;
//				}  
//			}
//		}
//		exportParm.setData("TOT", tot);
//		System.out.println(">>>>"+exportParm);
		TParm parmDiag = (TParm) openDialog(
				"%ROOT%\\config\\dev\\DEVPropertyCards.x", exportParm);
//		System.out.println(1111);
		if (parmDiag == null)
			return;
		for (int i = row; i < parmDiag.getDouble("ROW", 0); i++) {
			// DD������ʲ���Ƭ�ش�����
			// DEV_BASE���NUM(��ˮ��)
			//�����Լ���д��
			String DevDetailCode = parmDiag
					.getValue("DEV_CODE_DETAIL", i - row).toString();  
			// ȡ���������
			// �̶��ʲ�����     
			String devCode = exportParm.getValue("DEV_CODE", i);
			tabledd.setItem(i, "DEV_CODE_DETAIL", DevDetailCode);
			tabledd.setItem(i, "SERIAL_NUM", parmDiag.getData("SERIAL_NUM", i
					- row));
			tabledd.setItem(i, "WIRELESS_IP", parmDiag.getData("WIRELESS_IP", i
					- row));
			tabledd.setItem(i, "IP", parmDiag.getData("IP", i - row));
			tabledd.setItem(i, "TERM", parmDiag.getData("TERM", i - row));
			tabledd.setItem(i, "LOC_CODE", parmDiag
					.getData("LOC_CODE", i - row));
			tabledd.setItem(i, "USE_USER", parmDiag
					.getData("USE_USER", i - row));
			tabledd.setItem(i, "SPECIFICATION", parmDiag.getData(
					"SPECIFICATION", i - row));
			tabledd.setItem(i, "MODEL", parmDiag.getData("MODEL", i - row));
			tabledd.setItem(i, "BRAND", parmDiag.getData("BRAND", i - row));
			tabledd.setItem(i, "MAN_CODE", parmDiag
					.getData("MAN_CODE", i - row));

			tableParm.setData("DEV_CODE_DETAIL", i, DevDetailCode);
			tableParm.setData("SERIAL_NUM", i, parmDiag.getData("SERIAL_NUM", i
					- row));
			tableParm.setData("WIRELESS_IP", i, parmDiag.getData("WIRELESS_IP",
					i - row));         
			tableParm.setData("IP", i, parmDiag.getData("IP", i - row));
			tableParm.setData("TERM", i, parmDiag.getData("TERM", i - row));
			tableParm.setData("LOC_CODE", i, parmDiag.getData("LOC_CODE", i
					- row));
			tableParm.setData("USE_USER", i, parmDiag.getData("USE_USER", i
					- row));
			tableParm.setData("SPECIFICATION", i, parmDiag.getData(
					"SPECIFICATION", i - row));
			tableParm.setData("MODEL", i, parmDiag.getData("MODEL", i - row));
			tableParm.setData("BRAND", i, parmDiag.getData("BRAND", i - row));
			tableParm.setData("MAN_CODE", i, parmDiag.getData("MAN_CODE", i
					- row));
		}
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
}
