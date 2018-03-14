package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDSQL;
import jdo.spc.SPCOpiDrugApplicationTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;  
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TNull;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * 
 * Title: ���������龫��ҩ����Control
 * </p>
 * 
 * <p>
 * Description: ���������龫��ҩ����Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2013.07.29
 * @version 1.0
 */
public class SPCOpiDrugApplicationControl extends TControl {

	// ������
	private TTable tableM;

	// ϸ����
	private TTable tableD;

	// ���뵥��
	private String request_no;   

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		/**
		 * Ȩ�޿��� Ȩ��1:ֻ��ʾ������������ Ȩ��9:���Ȩ��,��ʾȫԺҩ�ⲿ��
		 */
		// �ж��Ƿ���ʾȫԺҩ�ⲿ��
		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndOrg()));
			getComboBox("APP_ORG_CODE").setParmValue(parm);
			if (parm.getCount("NAME") > 0) {
				getComboBox("APP_ORG_CODE").setSelectedIndex(1);
			}
			// Ԥ������ⷿgetINDORG
			TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"),
							Operator.getRegion())));
			getComboBox("TO_ORG_CODE").setSelectedID(
					sup_org_code.getValue("SUP_ORG_CODE", 0));
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// ��ʼ��TABLE
		tableM = getTable("TABLE_M");
		tableD = getTable("TABLE_D");
		//
		( (TMenuItem) getComponent("print")).setEnabled(true);
		( (TMenuItem) getComponent("printD")).setEnabled(false);
		( (TMenuItem) getComponent("exportD")).setEnabled(false);
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String START_DATE = this.getValueString("START_DATE");
		String END_DATE = this.getValueString("END_DATE");
		if (!StringUtil.isNullString(START_DATE)) {
			START_DATE = START_DATE.substring(0, 19);
			parm.setData("START_DATE", START_DATE);
		}
		if (!StringUtil.isNullString(END_DATE)) {
			END_DATE = END_DATE.substring(0, 19);
			parm.setData("END_DATE", END_DATE);
		}
		parm.setData("APP_ORG_CODE", this.getValue("APP_ORG_CODE"));
		parm.setData("TO_ORG_CODE", this.getValue("TO_ORG_CODE"));
		parm.setData("REQUEST_NO", this.getValue("REQUEST_NO"));
		// �ж����龫������ҩ
		if (getTRadioButton("DRUG").isSelected()) {
			parm.setData("CTRL_FLG", "Y");
		} else {
			parm.setData("CTRL_FLG", "N");
		}
		TParm resultM = new TParm();
		TParm resultD = new TParm();
		// ��ѯ�ж�
		if (this.getTRadioButton("REQUEST_FLG_A").isSelected()) {// ������
			parm.setData("REQUEST_FLG", "Y");
			if (this.getTRadioButton("REQUEST_TYPE_B").isSelected()) {// ��ϸ
				parm.setData("REQUEST_TYPE", "D");
				resultD = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecord(parm);
			} else {
				parm.setData("REQUEST_TYPE", "M");
				resultM = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecordM(parm);
			}
		} else {// δ����  
			parm.setData("REQUEST_FLG", "N");
			if (this.getTRadioButton("REQUEST_TYPE_B").isSelected()) {// ��ϸ
				parm.setData("REQUEST_TYPE", "D");
				resultD = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecord(parm);
			} else {
				parm.setData("REQUEST_TYPE", "M");
				resultM = SPCOpiDrugApplicationTool.getInstance()
						.querySpcInvRecordM(parm);
			}
		}
//		System.out.println("//resultM :::: " + resultM);
//		System.out.println("//resultD :::: " + resultD);
		if ("M".equals(parm.getData("REQUEST_TYPE"))) {
			if (resultM.getErrCode() < 0) {
				messageBox("E0008");
			} else {
				tableM.setParmValue(resultM);
				tableM.setVisible(true);
				tableD.setVisible(false);
			}
		} else {
			if (resultD.getErrCode() < 0) {
				messageBox("E0008");
			} else {
				tableD.setParmValue(resultD);
				tableM.setVisible(false);
				tableD.setVisible(true);
			}
		}
		((TCheckBox) getComponent("SELECT_ALL")).setSelected(true);
	}

	/**
	 * �Զ��������뵥��
	 */
	public void onSave() {
		if (!CheckDataM()) {
			return;
		}   
//		if (!CheckDataD()) {
//			return;
//		}
		TParm parm = new TParm();
		// �������ݣ����뵥����
		getRequestExmParmM(parm);
		// �������ݣ����뵥ϸ��
		getRequestExmParmD(parm);
		TParm result = new TParm();
		// �����������ӿڷ���
		result = TIOM_AppServer.executeAction("action.spc.INDRequestAction",
				"onCreateDeptOpiRequestSpc", parm);
		String msg = "";
		// �����ж�  
		if (result == null || result.getErrCode() < 0) {
			// this.messageBox(result.getErrText());
			String errText = result.getErrText();
			String[] errCode = errText.split(";");
			for (int i = 0; i < errCode.length; i++) {
				String orderCode = errCode[i];
				TParm returnParm = SYSFeeTool.getInstance().getFeeAllData(
						orderCode);
				if (returnParm != null && returnParm.getCount() > 0) {
					returnParm = returnParm.getRow(0);
					msg += orderCode + " " + returnParm.getValue("ORDER_DESC")
							+ "  " + returnParm.getValue("SPECIFICATION")
							+ "\n";
					if (i == errCode.length - 1) {
						msg += "������������ҩƷ���ձ���";
					}
				} else {
					msg += orderCode + "\n";
				}
			}
			this.messageBox(msg);
			return;
		}
		this.messageBox("P0001");
		onPrint();
		onClear();
		onQuery();
	}
	
	
	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("APP_ORG_CODE"))) {
			this.messageBox("���벿�Ų���Ϊ��");
			return false;
		}
		if ("Y".equals(this.getValue("REQUEST_FLG_A"))) {
			if ("".equals(getValueString("TO_ORG_CODE"))) {
				this.messageBox("���ղ��Ų���Ϊ��");
				return false;
			}
		}
		return true;
	}
	
	/**
	 * ���ݼ���
	 * 
	 * @return boolean
	 */
	private boolean CheckDataD() {
		if ("".equals(getValueString("TO_ORG_CODE"))) {
			this.messageBox("���ܲ��Ų���Ϊ��");
			return false;
		}
		if (tableD.getRowCount() == 0) {
			this.messageBox("û����������");
			return false;
		}
		boolean flg = true;
		for (int i = 0; i < tableM.getRowCount(); i++) {
			if ("Y".equals(tableM.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("û����������");
			return false;
		}
		return true;
	}

	/**
	 * �������ݣ����뵥����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmM(TParm parm) {
		TParm inparm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		request_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		inparm.setData("REQUEST_NO", request_no);
		inparm.setData("REQTYPE_CODE", "TEC");
		inparm.setData("APP_ORG_CODE", this.getValueString("APP_ORG_CODE"));
		inparm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		inparm.setData("REQUEST_DATE", date);
		inparm.setData("REQUEST_USER", Operator.getID());
		inparm.setData("REASON_CHN_DESC", this
				.getValueString("REASON_CHN_DESC"));
		inparm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
		inparm.setData("UNIT_TYPE", "1");
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("DRUG_CATEGORY", "2");
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", Operator.getIP());
		inparm.setData("REGION_CODE", Operator.getRegion());
		//fux modify
		inparm.setData("APPLY_TYPE", "1");
		parm.setData("REQUEST_M", inparm.getData());
		return parm;
	}

	/**
	 * �������ݣ����뵥ϸ��
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmD(TParm parm) {
		TParm inparm = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();
		String user_id = Operator.getID();
		String user_ip = Operator.getIP();
		int count = 0;
		for (int i = 0; i < tableM.getRowCount(); i++) {
			if ("N".equals(tableM.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			inparm.addData("REQUEST_NO", request_no);
			inparm.addData("SEQ_NO", count + 1);
			inparm.addData("ORDER_CODE", tableM.getParmValue().getValue(
					"ORDER_CODE", i));
			inparm.addData("BATCH_NO", "");
			inparm.addData("VALID_DATE", tnull);
			inparm.addData("QTY", tableM.getItemDouble(i, "DOSAGE_QTY"));
			inparm.addData("ACTUAL_QTY", 0);
			inparm.addData("UPDATE_FLG", "0");
			inparm.addData("OPT_USER", user_id);
			inparm.addData("OPT_DATE", date);
			inparm.addData("OPT_TERM", user_ip);
			inparm.addData("EXEC_DEPT_CODE", getValueString("APP_ORG_CODE"));
			inparm.addData("START_DATE", formatString(this
					.getValueString("START_DATE")));
			inparm.addData("END_DATE", formatString(this
					.getValueString("END_DATE")));
			
			//fux modify 20150603  
			//VERIFYIN_PRICE  
			inparm.addData("VERIFYIN_PRICE", tableM.getParmValue().getDouble(
					"STOCK_PRICE", i));
			//BATCH_SEQ
			inparm.addData("BATCH_SEQ", 0);  
			//IS_UPDATE
			inparm.addData("IS_UPDATE", "");
			count++;
		}
		inparm.setCount(count);
		parm.setData("REQUEST_D", inparm.getData());
		return parm;
	}

	/**
	 * ��ӡ���뵥
	 */
	public void onPrint() {
		tableM.acceptText();
		if (tableM.getRowCount() <= 0) {
			this.messageBox("û�д�ӡ����");
			return;
		}
		// ��ӡ����
		TParm data = new TParm();
		// ��ͷ����
		data.setData("TITLE", "TEXT", "���������龫��ҩ����ͳ�Ʊ�");
		data.setData("DATE", "TEXT", "ͳ��ʱ��: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// ���뵥��
		data.setData("REQUEST_NO", "TEXT", "���뵥��:" + request_no);
		// �������
		TParm parm = new TParm();
		TParm tableParm = tableM.getParmValue();
		// ��������е�Ԫ��
		for (int i = 0; i < tableM.getRowCount(); i++) {
			if ("N".equals(tableM.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParm
					.getValue("SPECIFICATION", i));
			parm.addData("DOSAGE_QTY", tableParm.getValue("DOSAGE_QTY", i));
			parm.addData("UNIT_CHN_DESC", tableParm
					.getValue("UNIT_CHN_DESC", i));
		}
		//messageBox("parm:"+parm);
		// ������
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		// �����ŵ�������
		data.setData("TABLE", parm.getData());
		// ��β����
		data.setData("USER", "TEXT", "ͳ����: " + Operator.getName());
		// ���ô�ӡ����
		this.openPrintWindow(
				"%ROOT%\\config\\prt\\spc\\SPCOpiDrugApplication.jhw", data);
	}
	
	/**
	 * ��ӡ��ϸ��
	 */
	public void onPrintD() {
		tableD.acceptText();
		boolean flg = true;
		TParm tableParm = tableD.getShowParmValue();
		for (int i = 0; i < tableParm.getCount(); i++) {
			if (!"N".equals(tableParm.getValue("SELECT_FLG", i))) {
				flg = false;
				break;
			}
		}
		if (flg) {
			this.messageBox("û����ϸ��Ϣ");
			return;
		}
		Timestamp datetime = StringTool.getTimestamp(new Date());
		// ��ӡ����
		TParm date = new TParm();
		// ��ͷ����
		date.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion())
				+ " ���������龫��ҩ���루��ϸ��");
		date.setData("DATE_AREA", "TEXT", this.getValueString("REQUEST_NO"));
		date.setData("APP_ORG_NAME", "TEXT", this.getComboBox("APP_ORG_CODE")
				.getSelectedName());
		date.setData("TO_ORG_NAME", "TEXT", this.getComboBox("TO_ORG_CODE")
				.getSelectedName());
		// �������
		TParm parm = new TParm();
		String time = null;
		for (int i = 0; i < tableParm.getCount(); i++) {
			if ("Y".equals(tableParm.getValue("SELECT_FLG", i))) {
				parm.addData("PAT_NAME", tableParm.getData("PAT_NAME", i));
				time = tableParm.getValue("BILL_DATE");
				if(time != null && time.length() > 0) {
					parm.addData("BILL_DATE", time.replaceAll("-", "/").replaceAll("\\[", "").substring(0, 10));
				} else {
					parm.addData("BILL_DATE", "");
				}
				parm.addData("ORDER_DESC", tableParm.getData("ORDER_DESC", i));
				parm.addData("SPECIFICATION", tableParm.getData("SPECIFICATION", i));
				parm.addData("DOSAGE_QTY", tableParm.getData("DOSAGE_QTY", i));
				parm.addData("UNIT_CHN_DESC",  tableParm.getData("UNIT_CHN_DESC", i));
				parm.addData("STOCK_PRICE", tableParm.getData("STOCK_PRICE", i));
				parm.addData("STOCK_AMT", tableParm.getData("STOCK_AMT", i));
				parm.addData("OWN_PRICE", tableParm.getData("OWN_PRICE", i));
				parm.addData("OWN_AMT", tableParm.getData("OWN_AMT", i));
			}
		}
		parm.setCount(parm.getCount("BILL_DATE"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		//System.out.println("printD parm ::::: " + parm);
		date.setData("TABLED", parm.getData());
		
		//��β����
		date.setData("OPT_DATE", "TEXT", datetime.toString().substring(0, 10));
		date.setData("OPT_USER", "TEXT", Operator.getName());
//		date.setData("SUM_VERIFYIN_PRICE", "TEXT","�����ܽ�"+StringTool.round(Double.parseDouble(this
//						.getValueString("SUM_VERIFYIN_PRICE")), 4));
//
//		date.setData("SUM_RETAIL_PRICE", "TEXT","�����ܽ�"+StringTool.round(Double.parseDouble(this
//						.getValueString("SUM_RETAIL_PRICE")), 4));
		this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDDeptRequestOfUdd_D.jhw",
				date);
		
	}
	
	/**
	 * ���ͳ��״̬
	 */
	public void onChangeRequestFlg() {
		//this.messageBox("onChangeRequestFlg");
		if (this.getRadioButton("REQUEST_FLG_B").isSelected()) {
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				((TMenuItem) getComponent("print")).setEnabled(true);
				((TMenuItem) getComponent("printD")).setEnabled(false);
				//((TMenuItem) getComponent("printRecipe")).setEnabled(false);
				((TMenuItem) getComponent("exportD")).setEnabled(false);
				tableM.setVisible(true);
				tableD.setVisible(false);
			} else {
				((TMenuItem) getComponent("print")).setEnabled(false);
				((TMenuItem) getComponent("printD")).setEnabled(true);
				//((TMenuItem) getComponent("printRecipe")).setEnabled(true);
				((TMenuItem) getComponent("exportD")).setEnabled(true);
				tableM.setVisible(false);
				tableD.setVisible(true);
			}
		} else {
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				((TMenuItem) getComponent("print")).setEnabled(true);
				((TMenuItem) getComponent("printD")).setEnabled(false);
				//((TMenuItem) getComponent("printRecipe")).setEnabled(false);
				((TMenuItem) getComponent("exportD")).setEnabled(false);
				tableM.setVisible(true);
				tableD.setVisible(false);
			} else {
				((TMenuItem) getComponent("print")).setEnabled(false);
				((TMenuItem) getComponent("printD")).setEnabled(true);
				//((TMenuItem) getComponent("printRecipe")).setEnabled(true);
				((TMenuItem) getComponent("exportD")).setEnabled(true);
				tableM.setVisible(false);
				tableD.setVisible(true);
			}
		}
		onQuery();
	}
	
	/**
	 * ������(TABLE_M)�����¼�
	 */
	public void onTableMClicked() {
		
	}

	/**
	 * ������(TABLE_D)�����¼�
	 */
	public void onTableDClicked() {

	}
	
	/**
	 * �����ϸ��
	 */
	public void onExportD() {
		boolean flg = true;
		for (int i = 0; i < tableD.getRowCount(); i++) {
			if (!"N".equals(tableD.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("û����ϸ��Ϣ");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(tableD,"���������龫��ҩ���루��ϸ��");
		
	}
	
	/**
	 * ��ʽ���ַ���(ʱ���ʽ)
	 * 
	 * @param arg
	 *            String
	 * @return String YYYYMMDDHHMMSS
	 */
	private String formatString(String arg) {
		arg = arg.substring(0, 4) + arg.substring(5, 7) + arg.substring(8, 10)
				+ arg.substring(11, 13) + arg.substring(14, 16)
				+ arg.substring(17, 19);
		return arg;
	}

	/**
	 * ȫѡ
	 */
	public void onSelectAll() {
		String flg = "N";
		if (getTCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		} else {
			flg = "N";
		}
		for (int i = 0; i < tableM.getRowCount(); i++) {
			tableM.setItem(i, "SELECT_FLG", flg);
		}
		for (int i = 0; i < tableD.getRowCount(); i++) {
			tableD.setItem(i, "SELECT_FLG", flg);
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// ��ʼ����ѯ����
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		tableM.setVisible(true);
		tableM.removeRowAll();
		tableD.setVisible(false);
		tableD.removeRowAll();
		// ��ջ�������
		String clearString = "APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;"
				+ "SELECT_ALL;URGENT_FLG;CHECK_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
				+ "PRICE_DIFFERENCE";
		clearValue(clearString);
		getTRadioButton("REQUEST_FLG_B").setSelected(true);
		getTRadioButton("REQUEST_TYPE_A").setSelected(true);
		( (TMenuItem) getComponent("print")).setEnabled(true);
		( (TMenuItem) getComponent("printD")).setEnabled(false);
		( (TMenuItem) getComponent("exportD")).setEnabled(false);
	}

	/**
	 * �õ�TRadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
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
	 * �õ�TCheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getTCheckBox(String tagName) {
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
