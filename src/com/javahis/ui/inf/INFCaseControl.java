package com.javahis.ui.inf;

import com.dongyang.control.TControl;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;

import jdo.sys.Pat;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSHzpyTool;
import jdo.sys.SystemTool;

import java.awt.Component;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.dongyang.ui.TTextFormat;

import jdo.sys.Operator;

import com.dongyang.ui.TTabbedPane;

import jdo.inf.INFCaseTool;
import jdo.sys.PatTool;

import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.dongyang.util.TypeTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.DateUtil;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TButton;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TTableNode;

import jdo.adm.ADMTool;

/**
 * <p>
 * Title: ��Ⱦ���Ƹ�Ⱦ����ɸѡ�������
 * </p>
 * 
 * <p>
 * Description: ��Ⱦ���Ƹ�Ⱦ����ɸѡ�������
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
 * @author sundx
 * @version 1.0
 */
public class INFCaseControl extends TControl {

	// ��Ժ���1
	TParm inDiag1 = new TParm();
	// ��Ժ���2
	TParm inDiag2 = new TParm();
	// ��Ժ���1
	TParm outDiag1 = new TParm();
	// ��Ժ���2
	TParm outDiag2 = new TParm();
	// ��Ժ���3
	TParm outDiag3 = new TParm();
	// �п����2
	TParm infDiag1 = new TParm();
	// �п����3
	TParm infDiag2 = new TParm();
	// ��¼ҳǩ���
	int taddedPaneNo;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		onInitPanelOne();
		onInitPanelThree();
		onInitPanelFour();
		onInitPanelFive(); // 20170522 add by liuyalin

		getTable("TABLE_5_2").addEventListener(
				"TABLE_5_2->" + TTableEvent.CHANGE_VALUE, this,
				"onTableChangeValue");
		getTable("TABLE_5_2").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxClicked");
		getTable("TABLE_5_2").addEventListener(
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponent");
		getTable("TABLE_6_2").addEventListener(
				"TABLE_6_2->" + TTableEvent.CHANGE_VALUE, this,
				"onTableChangeValue");
		getTable("TABLE_6_2").addEventListener(TTableEvent.CHECK_BOX_CLICKED,
				this, "onCheckBoxClicked");
		
		// modify machao start
		setValue("GRDDLE_FLG_1", "AND");
		setValue("CHECKOUT_FLG_1", "AND");
		setValue("CHECKUP_FLG_1", "AND");
		setValue("PROTOCOL_FLG_1", "AND");
		setValue("ANTIBIOTIC_FLG_1", "AND");
		setValue("INVADE_FLG_1", "AND");
		setValue("PROGRESS_FLG_1", "AND");
		setValue("OPE_FLG", "AND");
		setValue("PROGRESS_FLG_1", "AND");

		// ���õ����˵�
		getTextField("DIACODE_1")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("DIACODE_1").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "diaPopRetrun1");

		// ���õ����˵�
		getTextField("ORDER_CODE_1")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"));
		
		// ���õ����˵�
		getTextField("ORDER_CODE_5")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"));
		
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE_1").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "orderCodeReturn1");
		// ������ܷ���ֵ����
		getTextField("ORDER_CODE_5").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "orderCodeReturn5");

		// modify machao end

		// ���õ����˵�
		getTextField("IN_DIAG1_2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("IN_DIAG1_2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "inDiag1And2");
		// ���õ����˵�
		getTextField("IN_DIAG2_2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("IN_DIAG2_2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "inDiag2And2");
		// ���õ����˵�
		getTextField("OUT_DIAG3_2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("OUT_DIAG3_2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "outDiag3And2");
		// ���õ����˵�
		getTextField("OUT_DIAG2_2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("OUT_DIAG2_2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "outDiag2And2");
		// ���õ����˵�
		getTextField("OUT_DIAG1_2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("OUT_DIAG1_2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "outDiag1And2");
		// ���õ����˵�
		getTextField("INF_DIAG1_2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("INF_DIAG1_2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "infDiag1And2");
		// ���õ����˵�
		getTextField("INF_DIAG2_2")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ������ܷ���ֵ����
		getTextField("INF_DIAG2_2").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "infDiag2And2");
		// Ĭ�ϵ�0��ҳǩ��ť�ܿ�
		((TMenuItem) getComponent("save")).setEnabled(false);
		((TMenuItem) getComponent("delete")).setEnabled(false);
		((TMenuItem) getComponent("query")).setEnabled(true);
		((TMenuItem) getComponent("clear")).setEnabled(true);
		((TMenuItem) getComponent("close")).setEnabled(true);
		((TMenuItem) getComponent("print")).setEnabled(false);
		((TMenuItem) getComponent("report")).setEnabled(false);
		((TMenuItem) getComponent("temperature")).setEnabled(false);
		((TMenuItem) getComponent("showcase")).setEnabled(false);
		((TMenuItem) getComponent("consultation")).setEnabled(true);
		((TMenuItem) getComponent("testrep")).setEnabled(false);
		taddedPaneNo = 0;
	}

	// add machao start 20170509
	/**
	 * ��Ԫ��ֵ�ı��¼�
	 * 
	 * @param node
	 * @return
	 */
	public boolean onTableChangeValue(TTableNode node) {
		if (node == null)
			return false;
		if (node.getValue().equals(node.getOldValue()))
			return true;
		int row = node.getRow();
		//this.messageBox(row+"");
		TTable t = node.getTable();
		TParm pp = t.getParmValue();
		//this.messageBox(pp+"");
		String colName = node.getTable().getParmMap(node.getColumn());
		if (node.getTable() == getTable("TABLE_5_2")) {
			TParm parmValue = getTable("TABLE_5_2").getParmValue();
			if (colName.equals("MAIN_FLG")) {
				if (node.getValue().equals("Y")) {
					if (StringUtil.isNullString(getTable("TABLE_5_2")
							.getItemString(row, "PART_CODE"))
							|| StringUtil.isNullString(getTable("TABLE_5_2")
									.getItemString(row, "ICD_DESC"))
							||	StringUtil.isNullString(getTable("TABLE_5_2")
									.getItemString(row, "INF_SYSTEMCODE"))	) {
						this.messageBox("����¼�ĸ�Ⱦϵͳ,��Ⱦ��λ����������Ϊ��");
						return true;
					}
					for (int i = 0; i < parmValue.getCount(); i++) {
						if (i != row) {
							parmValue.setData(colName, i, "N");
						}
					}
				}
			}else if (colName.equals("PART_CODE")) {
				int maxRow = parmValue.getCount("MAIN_FLG");
				if (!StringUtil.isNullString(node.getValue() + "")
						&& !StringUtil.isNullString(parmValue.getValue(
								"ICD_DESC", maxRow - 1))
				// || !StringUtil.isNullString(parmValue.getValue("UM_DESC",
				// maxRow - 1))
				) {
					parmValue.addData("MAIN_FLG", "N");
					parmValue.addData("PART_CODE", "");
					parmValue.addData("INF_SYSTEMCODE", "");
					parmValue.addData("ICD_CODE", "");
					parmValue.addData("ICD_DESC", "");
					parmValue.addData("UM_CODE", "");
					parmValue.addData("UM_DESC", "");
					parmValue.addData("SEQ", parmValue.getInt("SEQ", row) + 1);
					parmValue.setCount(maxRow + 1);
				}
			}else if(colName.equals("INF_SYSTEMCODE")){
				int maxRow = parmValue.getCount("MAIN_FLG");//getTable("TABLE_5_2").acceptText();this.messageBox(getTable("TABLE_5_2").getParmValue()+"");
				if (!StringUtil.isNullString(node.getValue() + "")
						&& !StringUtil.isNullString(parmValue.getValue(
								"ICD_DESC", maxRow - 1))){
					parmValue.addData("MAIN_FLG", "N");
					parmValue.addData("PART_CODE", "");
					parmValue.addData("INF_SYSTEMCODE", "");
					parmValue.addData("ICD_CODE", "");
					parmValue.addData("ICD_DESC", "");
					parmValue.addData("UM_CODE", "");
					parmValue.addData("UM_DESC", "");
					parmValue.addData("SEQ", parmValue.getInt("SEQ", row) + 1);
					parmValue.setCount(maxRow + 1);
				}
//				TTextFormat com = (TTextFormat)getComponent("PART_CODE");
//				com.setPopupMenuSQL(" SELECT ID, CHN_DESC AS NAME "+
//						" FROM SYS_DICTIONARY "+
//						" WHERE     GROUP_ID = 'INF_INFPOSITION' "+
//						" AND ID IN (SELECT INFPOSITIONCODE "+
//						" FROM INF_SYSTEMDICTRONARY "+
//						" WHERE INFSYSTEMCODE = '"+this.getValueString("ttt")+"') ORDER BY SEQ");
			}
			
			getTable("TABLE_5_2").setParmValue(parmValue);
		}
//		TTable tt = getTable("TABLE_5_2");
//		tt.acceptText();
//		TParm p = tt.getParmValue();
//		this.messageBox(p+"");
		return false;
	}

	/**
	 * CHECK_BOX��ѡ�¼�
	 * 
	 * @param obj
	 * @return
	 */
	public boolean onCheckBoxClicked(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		return false;
	}

	/**
	 * ��ϵ������� ICD10
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComponent(Component com, int row, int column) {
		column = getTable("TABLE_5_2").getColumnModel().getColumnIndex(column);
		String colName = getTable("TABLE_5_2").getParmMap(column);
		if (!"ICD_DESC".equalsIgnoreCase(colName)
				&& !"UM_DESC".equalsIgnoreCase(colName)) {
			return;
		}
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// ��table�ϵ���text����ICD10��������
		textfield
				.setPopupMenuParameter(
						colName,
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSICDPopup.x"));
		// ����text���ӽ���ICD10�������ڵĻش�ֵ
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popICDReturn");
	}

	/**
	 * ȡ��ICD10����ֵ
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void popICDReturn(String tag, Object obj) {
		TTable table = getTable("TABLE_5_2");
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ICD_CODE");
		String orderDesc = parm.getValue("ICD_CHN_DESC");
		int row = table.getSelectedRow();
		table.setItem(row, tag, orderDesc);
		TParm parmValue = table.getParmValue();
		if (tag.equals("ICD_DESC")) {
			parmValue.setData("ICD_CODE", row, orderCode);
			parmValue.setData("ICD_DESC", row, orderDesc);
		} else if (tag.equals("UM_DESC")) {
			parmValue.setData("UM_CODE", row, orderCode);
			parmValue.setData("UM_DESC", row, orderDesc);
		}
		int maxRow = parmValue.getCount("MAIN_FLG");
		if (!StringUtil.isNullString(parmValue
				.getValue("PART_CODE", maxRow - 1))
				&& !StringUtil.isNullString(parmValue.getValue("ICD_DESC",
						maxRow - 1))
					&&	!StringUtil.isNullString(parmValue.getValue("INF_SYSTEMCODE",
							maxRow - 1))) {
			parmValue.addData("MAIN_FLG", "N");
			parmValue.addData("PART_CODE", "");
			parmValue.addData("INF_SYSTEMCODE", "");
			parmValue.addData("ICD_CODE", "");
			parmValue.addData("ICD_DESC", "");
			parmValue.addData("UM_CODE", "");
			parmValue.addData("UM_DESC", "");
			parmValue.addData("SEQ", parmValue.getInt("SEQ", row) + 1);
			parmValue.setCount(maxRow + 1);
		}
		table.setParmValue(parmValue);
	}

	// add machao end 20170509

	/**
	 * ��������ҩ��
	 */
	public void onNewExmResult() {
		// if(getTable("TABLE_4_2").getParmValue() == null ||
		// getTable("TABLE_4_2").getRowCount() == 0){
		// getTable("TABLE_4_2").setParmValue(new TParm());
		// getTable("TABLE_4_2").addRow();
		// return;
		// }
		// int row = getTable("TABLE_4_2").getParmValue().getCount() - 1;
		// if(!getTable("TABLE_4_2").getParmValue().getData("DEL_FLG",row).equals("Y")
		// &&
		// (getTable("TABLE_4_2").getParmValue().getValue("PATHOGEN_CODE",row).length()
		// == 0 ||
		// getTable("TABLE_4_2").getParmValue().getValue("EXM_PHA",row).length()
		// == 0 ||
		// getTable("TABLE_4_2").getParmValue().getValue("RESULT",row).length()
		// == 0))
		// return;
		// getTable("TABLE_4_2").addRow();
		// ������
		String mrNo = this.getValueString("MR_NO_2");
		// �����
		String caseNo = this.getValueString("CASE_NO_2");
		if (caseNo.equals("")) {
			this.messageBox("�����Ϊ��,��סԺ������Ϣ");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		Object obj = this.openDialog("%ROOT%\\config\\inf\\INFMedCaseUI.x",
				parm);
		if (obj != null) {
			TParm temp = (TParm) obj;
			this.getTable("TABLE_4_2").setParmValue(temp);
		}
	}

	/**
	 * ȡ�ý���TTextField
	 * 
	 * @param tag
	 *            String
	 * @return TTextField
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

	/**
	 * ��ʼ����һ��ҳǩ����
	 */
	public void onInitPanelOne() {
		// modify machao
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("START_DATE_1", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/'));

		this.setValue("END_DATE_1",
				date.toString().substring(0, 10).replace('-', '/'));
		setValue("START_TEMP_1", 38);
		setValue("END_TEMP_1", 42);
		((TTextFormat) getComponent("DEPT_CODE_1"))
				.setValue(Operator.getDept());
		((TTextFormat) getComponent("STATION_CODE_1")).setValue(Operator
				.getStation());
		// end machao
	}

	/**
	 * ��ʼ��������ҳǩ����
	 */
	public void onInitPanelThree() {
		Timestamp timestamp = SystemTool.getInstance().getDate();
		((TTextFormat) getComponent("START_DATE_3")).setValue(timestamp);
		((TTextFormat) getComponent("END_DATE_3")).setValue(timestamp);
	}

	/**
	 * ��ʼ�����ĸ�ҳǩ����
	 */
	public void onInitPanelFour() {
		Timestamp timestamp = SystemTool.getInstance().getDate();
		((TTextFormat) getComponent("START_DATE_4")).setValue(timestamp);
		((TTextFormat) getComponent("END_DATE_4")).setValue(timestamp);
	}

	// liuyalin 20170522 add
	/**
	 * ��ʼ�������ҳǩ����
	 */
	public void onInitPanelFive() {
		Timestamp timestamp = SystemTool.getInstance().getDate();
		((TTextFormat) getComponent("START_DATE_5")).setValue(timestamp);
		((TTextFormat) getComponent("END_DATE_5")).setValue(timestamp);
	}

	// liuyalin 20170522 add end
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 0:
			onQueryOne();
			break;
		case 1:
			onQueryTwo();
			break;
		case 2:
			onQueryThree();
			break;
		case 3:
			onQueryFour();
			break;
		case 4: // liuyalin 20170522 add
			onQueryFive();
			break;
		}
	}

	// start machao 20170518
	public String getCheckOutStringSql(String mes, TParm parm, String sql) {
		String checkSql = "";
		String restultSql = "";
		String sId = parm.getValue("ID", 0);// ��ȡ��Ӧ�ֵ�ID
		String code = parm.getValue("PY2", 0);// ��ȡ��Ӧ�ֵ��м�����Ŀcode
		String[] code11 = code.split("/");// ���ж��������Ŀ�������
		String stCode1 = parm.getValue("STA1_CODE", 0);// ��ȡϸ������ֵ����
		String[] stCode11 = stCode1.split(";");// ���ж��ϸ���������޽������
		String stCode2 = parm.getValue("STA2_CODE", 0);// ��ȡϸ������ֵ����
		String[] stCode22 = stCode2.split(";");// ���ж��ϸ���������޽������
		String stCode3 = parm.getValue("STA3_CODE", 0);// ��ȡ��Ⱦ���ؼ���
		String[] stCode33 = stCode3.split(";");// ���ж����Ⱦ���ؼ��ֽ������
		int num1 = stCode1.split(";").length;
		int num2 = stCode3.split(";").length;
		// this.messageBox(num1+"");

		// if(sId.equals("01") || //ϸ����Ⱦ ��ϸ�� WBC 190001
		// sId.equals("02") ||//������ϸ��
		// sId.equals("04") ||//������ԭ
		// sId.equals("05") ||//Ѫ��
		// sId.equals("03")){//C��Ӧ����
		if (StringUtil.isNullString(stCode3)) {// ϸ����Ⱦ
			for (int i = 0; i < num1; i++) {
				checkSql = " (SELECT * "
						+ "       FROM (SELECT * "
						+ "             FROM MED_LIS_RPT "
						+ "             WHERE     TESTITEM_CODE = '"
						+ code11[i]
						+ "' "
						+ "                       AND REGEXP_REPLACE (TEST_VALUE,\\\'^[-\\\\+]?\\\\d+(\\\\.\\\\d+)?\\$\\\',\\\'\\\') IS NULL) C "
						+ " WHERE (C.TEST_VALUE > " + stCode22[i]
						+ " OR C.TEST_VALUE < " + stCode11[i] + ")) B ";
				if (i == 0) {
					restultSql = sql.replaceAll("#", checkSql);
					continue;
				}
				restultSql += " UNION " + sql.replaceAll("#", checkSql);
			}
			// System.out.println("mmm:"+restultSql);
		} else {// ��Ⱦ��
			checkSql = " AND TESTITEM_CODE = '" + code + "' AND( ";
			for (int i = 0; i < num2; i++) {

				checkSql += " TEST_VALUE LIKE '%" + stCode33[i] + "%' OR ";

			}
			checkSql = checkSql.substring(0, checkSql.length() - 3);
			checkSql += ")";
			restultSql = sql.replaceAll("#", "MED_LIS_RPT B");
			restultSql += checkSql;
			// System.out.println("mmmm:"+restultSql);
		}
		return restultSql;
	}

	// end machao 20170518
	/**
	 * ��һ��ҳǩ��ѯ����
	 */
	public void onQueryOne() {
		if (getValueString("START_DATE_1").length() == 0
				|| getValueString("END_DATE_1").length() == 0) {
			messageBox("��¼�뿪ʼ��������");
			return;
		}
		if (StringTool.getDateDiffer((Timestamp) getValue("START_DATE_1"),
				(Timestamp) getValue("END_DATE_1")) > 0) {
			messageBox("¼������ڲ��Ϸ�");
			return;
		}
		// ((TTable)getComponent("TABLE_1")).setParmValue(new TParm());
		// ��ѯ�������¶Ȳ�Ϊ��
		String SQLTemperature = "";
		if (getValueString("TEMPERATURE_1").equals("Y")) {
//			if (getValueString("START_TEMP_1").length() != 0
//					&& getValueString("END_TEMP_1").length() != 0
//					&& !getValueString("START_TEMP_1").equals("0")
//					&& !getValueString("END_TEMP_1").equals("0"))
				SQLTemperature = " SELECT CASE_NO " + " FROM SUM_VTSNTPRDTL "
						+ " WHERE ADM_TYPE = 'I' "
						+ " AND EXAMINE_DATE BETWEEN '"
						+ getDateString("START_DATE_1")
						+ "' "
						+ "                  AND     '"
						+ getDateString("END_DATE_1")
						+ "' "
						+ " AND TEMPERATURE BETWEEN '"
						+ getValueString("START_TEMP_1")
						+ "' "
						+ "                     AND '"
						+ getValueString("END_TEMP_1")
						+ "' "
						+ " UNION "
						+ " SELECT CASE_NO "
						+ " FROM SUM_NEWARRIVALSIGNDTL "
						+ " WHERE ADM_TYPE = 'I' "
						+ " AND EXAMINE_DATE BETWEEN '"
						+ getDateString("START_DATE_1")
						+ "' "
						+ "                  AND     '"
						+ getDateString("END_DATE_1")
						+ "' "
						+ " AND TEMPERATURE BETWEEN '"
						+ getValueString("START_TEMP_1")
						+ "' "
						+ "                     AND '"
						+ getValueString("END_TEMP_1") + "' ";
			System.out.println("eee1:" + SQLTemperature);
		}

		// ��ɸע��
		String SQLDRESS = "";
		if (getValueString("DRESS_F_1").equals("Y")) {
			SQLDRESS = " SELECT CASE_NO FROM ADM_INP WHERE INFECT_SCR_RESULT='��' "
					+ " AND ADM_DATE BETWEEN TO_DATE('"
					+ getDateString("START_DATE_1")
					+ "000000','YYYYMMDDHH24MISS') "
					+ "                        AND TO_DATE('"
					+ getDateString("END_DATE_1")
					+ "235959','YYYYMMDDHH24MISS') ";
			System.out.println("eee2:" + SQLDRESS);
		}
		// ʹ�ÿ�����ע��ѡ��
		String SQLDrug = "";
		if (getValue("ANTIBIOTIC_F_1").equals("Y")) {
			SQLDrug = " SELECT A.CASE_NO "
					+ " FROM ODI_ORDER A,PHA_BASE B "
					+ " WHERE A.EFF_DATE BETWEEN TO_DATE('"
					+ getDateString("START_DATE_1")
					+ "000000','YYYYMMDDHH24MISS') "
					+ "                       AND TO_DATE('"
					+ getDateString("END_DATE_1")
					+ "235959','YYYYMMDDHH24MISS') "
					+ " AND B.ORDER_CODE = A.ORDER_CODE "
					+ (getValueString("ANTIBIOTIC_CODE_1").length() == 0 ? " AND B.ANTIBIOTIC_CODE IS NOT NULL"
							: " AND B.ANTIBIOTIC_CODE = '"
									+ getValueString("ANTIBIOTIC_CODE_1") + "'");
			System.out.println("eee3:" + SQLDrug);
		}
		// ������� ҽ������
		String SQLORDER = "";
		if (getValueString("INVADE_F_1").equals("Y")) {
			
				SQLORDER = "SELECT CASE_NO FROM ODI_ORDER A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE "
						+ "   AND B.IPD_FIT_FLG = 'Y' "
						+ " AND A.EFF_DATE BETWEEN TO_DATE('"
						+ getDateString("START_DATE_1")
						+ "000000','YYYYMMDDHH24MISS') "
						+ "                       AND TO_DATE('"
						+ getDateString("END_DATE_1")
						+ "235959','YYYYMMDDHH24MISS') "
						+ "AND B.IN_OPFLG = 'Y'";
				if (!getValueString("ORDER_CODE_1").equals("")) {
					SQLORDER += "AND A.ORDER_CODE='"
							+ getValueString("ORDER_CODE_1") + "' ";
				} 
//				if (!getValueString("DEPT_CODE_1").equals("")) {
//					SQLORDER += " AND A.DEPT_CODE = '"
//							+ getValueString("DEPT_CODE_1") + "' ";
//				}
//				if (!getValueString("STATION_CODE_1").equals("")) {
//					SQLORDER += " AND A.STATION_CODE = '"
//							+ getValueString("STATION_CODE_1") + "' ";
//				}
				System.out.println("eee4:" + SQLORDER);
				
		}

		// ������� ��ϴ���
		String SQLDIA = "";
		if (getValueString("PROTOCOL_F_1").equals("Y")) {
			
				SQLDIA = " SELECT A.CASE_NO " + "  FROM ADM_INPDIAG A,ADM_INP B "
						+ " WHERE A.CASE_NO = B.CASE_NO "
						+ "   AND A.MAINDIAG_FLG = 'Y'  "
						+ "   AND A.IO_TYPE = 'Z'    "
						+ " AND A.OPT_DATE BETWEEN TO_DATE('"
						+ getDateString("START_DATE_1")
						+ "000000','YYYYMMDDHH24MISS') "
						+ "                        AND TO_DATE('"
						+ getDateString("END_DATE_1")
						+ "235959','YYYYMMDDHH24MISS') ";
				if (!getValueString("DIACODE_1").equals("")) {
					SQLDIA += "AND A.ICD_CODE='" + getValueString("DIACODE_1")
							+ "' ";
				}
				System.out.println("eee5:" + SQLDIA);
		}

		// this.messageBox(getValueString("CHECKOUT_VALUE_1"));
		// ����
		String SQLCheckOut = "";
		if (!StringUtil.isNullString(getValueString("CHECKOUT_VALUE_1"))) {
			SQLCheckOut = " SELECT A.CASE_NO " + " FROM MED_APPLY A, # "
					+ " WHERE A.STATUS = '7' " + " AND   A.CAT1_TYPE = 'LIS'"
					+ " AND   A.START_DTTM BETWEEN TO_DATE('"
					+ getDateString("START_DATE_1")
					+ "000000','YYYYMMDDHH24MISS') "
					+ "                        AND TO_DATE('"
					+ getDateString("END_DATE_1")
					+ "235959','YYYYMMDDHH24MISS') "
					+ " AND   A.APPLICATION_NO = B.APPLICATION_NO";
			TParm parm = new TParm(
					TJDODBTool
							.getInstance()
							.select("SELECT ID,CHN_DESC,PY2,ENG_DESC,STA1_CODE,STA2_CODE,STA3_CODE FROM SYS_DICTIONARY WHERE GROUP_ID = 'CHECK_OUT' AND ID = '"
									+ getValueString("CHECKOUT_VALUE_1") + "'"));

			SQLCheckOut = getCheckOutStringSql(
					getValueString("CHECKOUT_VALUE_1"), parm, SQLCheckOut);
			System.out.println("kkk��" + SQLCheckOut);
		}

		// ����
		String SQLOpebook = "";
		if (getValueString("OP_F").equals("Y")) {

			String endTime = StringTool
					.rollDate(TJDODBTool.getInstance().getDBTime(), 0)
					.toString().substring(0, 10).replace("-", "");
			String startTime = StringTool
					.rollDate(TJDODBTool.getInstance().getDBTime(),
							-this.getValueInt("OPB_DAYS")).toString()
					.substring(0, 10).replace("-", "");

			SQLOpebook = "SELECT CASE_NO " + " FROM OPE_OPBOOK"
					+ " WHERE OP_DATE BETWEEN TO_DATE('" + startTime
					+ "000000','YYYYMMDDHH24MISS') "
					+ "                  AND     TO_DATE('" + startTime
					+ "235959','YYYYMMDDHH24MISS') ";
			System.out.println("eee7::::" + SQLOpebook);
		}

		// ���
		String SQLCheckUp = "";
		if (!StringUtil.isNullString(getValueString("CHECKUP_VALUE_1"))) {
			SQLCheckUp = " SELECT A.CASE_NO  "
					+ " FROM MED_APPLY A, MED_RPTDTL B "
					+ " WHERE A.STATUS = '7' " + " AND   A.CAT1_TYPE = 'RIS'"
					+ " AND   B.OPT_DATE BETWEEN TO_DATE('"
					+ getDateString("START_DATE_1")
					+ "000000','YYYYMMDDHH24MISS') "
					+ "                        AND TO_DATE('"
					+ getDateString("END_DATE_1")
					+ "235959','YYYYMMDDHH24MISS') "
					+ " AND   A.APPLICATION_NO = B.APPLICATION_NO";

			String desc = new TParm(TJDODBTool.getInstance().select(
					"SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'CHECK_UP' AND ID = '"
							+ getValueString("CHECKUP_VALUE_1") + "'"))
					.getValue("CHN_DESC", 0);
			SQLCheckUp += " AND (OUTCOME_CONCLUSION LIKE '%" + desc + "%'"
					+ " OR OUTCOME_DESCRIBE LIKE '%" + desc + "%')";
			System.out.println("eee8:" + SQLCheckUp);
		}
		
		//����
		String SQLProgress = "";
		if(getValueString("PROGRESS_F_1").equals("Y")){
			SQLProgress = " SELECT CASE_NO FROM ADM_INP WHERE INF_FLG = 'Y' "
					+ " AND ADM_DATE BETWEEN TO_DATE('"
					+ getDateString("START_DATE_1")
					+ "000000','YYYYMMDDHH24MISS') "
					+ "                        AND TO_DATE('"
					+ getDateString("END_DATE_1")
					+ "235959','YYYYMMDDHH24MISS') ";
			System.out.println("eee111:" + SQLProgress);
		}

		Boolean slqFlg = false;
		if (SQLTemperature.length() != 0) {
			slqFlg = true;
		}
		if (SQLDRESS.length() != 0) {
			slqFlg = true;
		}
		if (SQLDrug.length() != 0) {
			slqFlg = true;
		}
		if (SQLORDER.length() != 0) {
			slqFlg = true;
		}
		if (SQLDIA.length() != 0) {
			slqFlg = true;
		}
		if (SQLCheckOut.length() != 0) {
			slqFlg = true;
		}
		if (SQLOpebook.length() != 0) {
			slqFlg = true;
		}
		if (SQLCheckUp.length() != 0) {
			slqFlg = true;
		}
		if(SQLProgress.length() != 0){
			slqFlg = true;
		}
		if (!slqFlg) {
			messageBox("������¼��һ����ѯ����");
			return;
		}
		
		boolean flgTemp = getValueBoolean("TEMPERATURE_1");// �¶�
		boolean flgDress = getValueBoolean("DRESS_F_1");// ��ɸ
		boolean flgCheckOut = StringUtil
				.isNullString(getValueString("CHECKOUT_VALUE_1")) ? false
				: true;// ����
		boolean flgCheckUp = StringUtil
				.isNullString(getValueString("CHECKUP_VALUE_1")) ? false : true;// ���	
		boolean flgProtocol = getValueBoolean("PROTOCOL_F_1");// �������
		boolean flgInvade = getValueBoolean("INVADE_F_1");// �������
		boolean flgAntibiotic = getValueBoolean("ANTIBIOTIC_F_1");// ������
		boolean flgProgress = getValueBoolean("PROGRESS_F_1");// ���̼�¼
		boolean flgOpeBook = getValueBoolean("OP_F");// ����
			
		
		List<Boolean> listBoolean = new ArrayList<Boolean>();
		listBoolean.add(flgTemp);//�¶�
		listBoolean.add(flgDress);//��ɸ
		listBoolean.add(flgCheckOut);//����
		listBoolean.add(flgCheckUp);//���
		listBoolean.add(flgProtocol);//�������
		listBoolean.add(flgInvade);//�������
		listBoolean.add(flgAntibiotic);//������
		listBoolean.add(flgProgress);//���̼�¼
		listBoolean.add(flgOpeBook);//����
		
		
		List<String> listSql = new ArrayList<String>();
		listSql.add(SQLTemperature);//�¶�
		listSql.add(SQLDRESS);//��ɸ
		listSql.add(SQLCheckOut);//����
		listSql.add(SQLCheckUp);//���
		listSql.add(SQLDIA);//�������
		listSql.add(SQLORDER);//�������
		listSql.add(SQLDrug);//������
		listSql.add(SQLProgress);//���̼�¼
		listSql.add(SQLOpebook);//����
		
		
		String GRDDLE_FLG_1 = getValueString("GRDDLE_FLG_1");// ��ɸ
		String CHECKOUT_FLG_1 = getValueString("CHECKOUT_FLG_1");// ����
		String CHECKUP_FLG_1 = getValueString("CHECKUP_FLG_1");// ���
		String PROTOCOL_FLG_1 = getValueString("PROTOCOL_FLG_1"); // �������
		String INVADE_FLG_1 = getValueString("INVADE_FLG_1");// �������
		String ANTIBIOTIC_FLG_1 = getValueString("ANTIBIOTIC_FLG_1");// ������
		String PROGRESS_FLG_1 = getValueString("PROGRESS_FLG_1");// ���̼�¼
		String OPE_FLG = getValueString("OPE_FLG");// ����
		
		
		List<String> listAndOr = new ArrayList<String>();
		listAndOr.add(GRDDLE_FLG_1);//��ɸ
		listAndOr.add(CHECKOUT_FLG_1);//����
		listAndOr.add(CHECKUP_FLG_1);//���
		listAndOr.add(PROTOCOL_FLG_1);//�������
		listAndOr.add(INVADE_FLG_1);//�������
		listAndOr.add(ANTIBIOTIC_FLG_1);//������
		listAndOr.add(PROGRESS_FLG_1);//���̼�¼
		listAndOr.add(OPE_FLG);//����
		
		
		String unionIntersectSql = "";
		Boolean flg = true;
		for(int i = 0 ;i<listBoolean.size();i++){
			if(listBoolean.get(i) && flg){//��һ�ν����ɳ�ʼSQL�ҵ�
				unionIntersectSql += listSql.get(i);
				flg = false;
				continue;
			}
			
			if(listBoolean.get(i) && !flg){
				if(listAndOr.get(i-1).equals("AND")){
					unionIntersectSql +=" INTERSECT "+ listSql.get(i);
				}else{
					unionIntersectSql +=" UNION "+ listSql.get(i);
				}
			}
		}

		
//		// �¶�
//		String[] sTemperature = null;
//		if (SQLTemperature.length() > 0) {
//			TParm resultTemperature = new TParm(getDBTool().select(
//					SQLTemperature));
//			sTemperature = resultTemperature.getStringArray("CASE_NO");
//		}
//
//		// ��ɸ
//		String[] sDRESS = null;
//		if (SQLDRESS.length() > 0) {
//			TParm resultDRESS = new TParm(getDBTool().select(SQLDRESS));
//			sDRESS = resultDRESS.getStringArray("CASE_NO");
//		}
//
//		// ������
//		String[] sDrug = null;
//		if (SQLDrug.length() > 0) {
//			TParm resultDrug = new TParm(getDBTool().select(SQLDrug));
//			sDrug = resultDrug.getStringArray("CASE_NO");
//		}
//
//		// �������
//		String[] sOrder = null;
//		if (SQLORDER.length() > 0) {
//			TParm resultOrder = new TParm(getDBTool().select(SQLORDER));
//			sOrder = resultOrder.getStringArray("CASE_NO");
//		}
//
//		// �������
//		String[] sDia = null;
//		if (SQLDIA.length() > 0) {
//			TParm resultDia = new TParm(getDBTool().select(SQLDIA));
//			sDia = resultDia.getStringArray("CASE_NO");
//		}
//
//		// ����
//		String[] sCheckOut = null;
//		if (SQLCheckOut.length() > 0) {
//			TParm resultCheckOut = new TParm(getDBTool().select(SQLCheckOut));
//			sCheckOut = resultCheckOut.getStringArray("CASE_NO");
//		}
//
//		// ����
//		String[] sOpbook = null;
//		if (SQLOpebook.length() > 0) {
//			TParm resultOpbook = new TParm(getDBTool().select(SQLOpebook));
//			sOpbook = resultOpbook.getStringArray("CASE_NO");
//		}
//
//		// ���
//		String[] sCheckUp = null;
//		if (SQLCheckUp.length() > 0) {
//			TParm resultCheckUp = new TParm(getDBTool().select(SQLCheckUp));
//			sCheckUp = resultCheckUp.getStringArray("CASE_NO");
//		}
//
//		boolean flgTemp = getValueBoolean("TEMPERATURE_1");// �¶�
//		boolean flgDress = getValueBoolean("DRESS_F_1");// ��ɸ
//		boolean flgCheckOut = StringUtil
//				.isNullString(getValueString("CHECKOUT_VALUE_1")) ? false
//				: true;// ���
//		boolean flgProtocol = getValueBoolean("PROTOCOL_F_1");// �������
//		boolean flgInvade = getValueBoolean("INVADE_F_1");// �������
//		boolean flgAntibiotic = getValueBoolean("ANTIBIOTIC_F_1");// ������
//		boolean flgOpeBook = getValueBoolean("OP_F");// ����
//		boolean flgCheckUp = StringUtil
//				.isNullString(getValueString("CHECKUP_VALUE_1")) ? false : true;// ���
//
//		System.out.println("�¶ȣ�" + Arrays.toString(sTemperature));
//		System.out.println("��ɸ��" + Arrays.toString(sDRESS));
//		System.out.println("���飺" + Arrays.toString(sCheckOut));
//		System.out.println("������ϣ�" + Arrays.toString(sDia));
//		System.out.println("���������" + Arrays.toString(sOrder));
//		System.out.println("�����أ�" + Arrays.toString(sDrug));
//		System.out.println("������" + Arrays.toString(sOpbook));
//		System.out.println("��飺" + Arrays.toString(sCheckUp));
//
//		String GRDDLE_FLG_1 = getValueString("GRDDLE_FLG_1");// ��ɸ
//		String CHECKOUT_FLG_1 = getValueString("CHECKOUT_FLG_1");// ���
//		String PROTOCOL_FLG_1 = getValueString("PROTOCOL_FLG_1"); // �������
//		String INVADE_FLG_1 = getValueString("INVADE_FLG_1");// �������
//		String ANTIBIOTIC_FLG_1 = getValueString("ANTIBIOTIC_FLG_1");// ������
//		String OPE_FLG = getValueString("OPE_FLG");// ����
//		String CHECKUP_FLG_1 = getValueString("CHECKUP_FLG_1");// ���
//
//		System.out.println("��ɸ��" + GRDDLE_FLG_1);
//		System.out.println("���飺" + CHECKOUT_FLG_1);
//		System.out.println("������ϣ�" + PROTOCOL_FLG_1);
//		System.out.println("���������" + INVADE_FLG_1);
//		System.out.println("�����أ�" + ANTIBIOTIC_FLG_1);
//		System.out.println("������" + OPE_FLG);
//		System.out.println("���:" + CHECKUP_FLG_1);
//
//		List<String[]> list = new ArrayList<String[]>();
//		List<String> lAndOr = new ArrayList<String>();
//		if (flgTemp) {
//			list.add(sTemperature);// �¶�
//		}
//		if (flgDress) {
//			list.add(sDRESS);// ��ɸ
//			lAndOr.add(GRDDLE_FLG_1);// ��ɸ
//		}
//		if (flgCheckOut) {
//			list.add(sCheckOut);// ���
//			lAndOr.add(CHECKOUT_FLG_1);// ���
//		}
//		if (flgProtocol) {
//			list.add(sDia);// �������
//			lAndOr.add(PROTOCOL_FLG_1); // �������
//		}
//		if (flgInvade) {
//			list.add(sOrder);// �������
//			lAndOr.add(INVADE_FLG_1);// �������
//		}
//		if (flgAntibiotic) {
//			list.add(sDrug);// ������
//			lAndOr.add(ANTIBIOTIC_FLG_1);// ������
//		}
//		if (flgOpeBook) {
//			list.add(sOpbook);// ����
//			lAndOr.add(OPE_FLG);// ����
//		}
//		if (flgCheckUp) {
//			list.add(sCheckUp);// ���
//			lAndOr.add(CHECKUP_FLG_1);// ���
//		}
//
//		for (String[] s : list) {
//			System.out.println(Arrays.toString(s));
//		}
//		System.out.println(lAndOr);
//		System.out.println("ssslAndOr:" + lAndOr.size());
//		System.out.println("list:" + list.size());
//
//		String[] sOut = getCaseNoString(list, lAndOr);
//
//		System.out.println("AAAOUT:" + sOut);
//		if (sOut == null || sOut.length <= 0) {
//			getTable("TABLE_1").setParmValue(new TParm());
//			return;
//		}

		
		
		TParm result = new TParm(getDBTool().select(unionIntersectSql));// add by wanglong 20131105
		System.out.println("mmmmm:"+unionIntersectSql);
		//this.messageBox(result.getValue("CASE_NO", 0));
        if (result.getErrCode() < 0) {// add by wanglong 20131105
            this.messageBox(result.getErrText());
            return;
        }
        if (result.getCount() < 1) {// add by wanglong 20131105
            getTable("TABLE_1").setParmValue(new TParm());
            return;
        }
		
		
		
		// // ==========pangben modify 20110624 start �����������
		StringBuffer region = new StringBuffer();
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region.append(" AND A.REGION_CODE='" + Operator.getRegion() + "' ");
		} else
			region.append("");

		// // ==========pangben modify 20110624 stop

		String SQL = " SELECT DISTINCT A.DEPT_CODE,A.IN_DATE,A.MR_NO,C.PAT_NAME,B.STATION_CODE,A.BED_NO, "
				+ "           A.VS_DR_CODE VS_DR,A.CASE_NO,C.BIRTH_DATE,C.SEX_CODE,B.BED_NO_DESC,C.IPD_NO,A.INFECT_SCR_RESULT, "
				+ "            A.FALL_RISK,CASE A.ALLERGY WHEN 'Y' THEN '��' WHEN 'N' THEN '��' END AS ALLERGY "
				+ "      FROM ADM_INP A,SYS_BED B,SYS_PATINFO C "
				+ "     WHERE (#) "
				+ "       AND A.BED_NO = B.BED_NO "
				+ "       @  @                  "
				+ "       AND A.MR_NO = C.MR_NO "
				+ region
				+ "  ORDER BY B.STATION_CODE ";

		System.out.println("eee9:" + SQL);

		//String caseNoWhere = getInStatementString(sOut, "CASE_NO", "A.CASE_NO");
		
		
		String caseNoWhere = getInStatement(result, "CASE_NO", "A.CASE_NO");// add by wanglong 20131105
		System.out.println("mm1:" + caseNoWhere);
		SQL = SQL.replaceFirst("#", caseNoWhere);
		System.out.println("eee10:" + SQL);
		if (getValueString("DEPT_CODE_1").length() == 0) {
			SQL = SQL.replaceFirst("@", "");
		} else {
			SQL = SQL.replaceFirst("@", " AND A.DEPT_CODE = '"
					+ getValueString("DEPT_CODE_1") + "'");
		}
		if (getValueString("STATION_CODE_1").length() == 0) {
			SQL = SQL.replaceFirst("@", "");
		} else {
			SQL = SQL.replaceFirst("@", " AND A.STATION_CODE = '"
					+ getValueString("STATION_CODE_1") + "'");
		}
		System.out.println("eee11:" + SQL);
		TParm parm = new TParm(getDBTool().select(SQL));
		if (parm.getCount() <= 0)
			return;
		getTable("TABLE_1").setParmValue(parm);

	}

	// machao start 20170516
	// ��ҳ���еĲ��� �� ת�� �������
	// ��ҳ���еĲ��� �� ת�� �������
	public String[] getCaseNoString(List<String[]> list1, List<String> list2) {
		List<String[]> list = list1;
		List<String> lAndOr = list2;
		String[] sOut = null;
		if ((lAndOr.size() == 1 && list.size() <= 1)
				|| (lAndOr.size() == 0 && list.size() == 1)) {
			return list.get(0);
		}
		if (lAndOr.size() == 1 && list.size() == 2) {
			if ("AND".equals(lAndOr.get(0))) {
				System.out.println("mmmmm1"+Arrays.toString(list.get(0)));
				System.out.println("mmmmm2"+Arrays.toString(list.get(1)));
				
				sOut = getStrings(list.get(0), list.get(1));
			} else {
				sOut = getStringSet(list.get(0), list.get(1));
			}
			System.out.println("sOut:" + Arrays.toString(sOut));
			return sOut;
		}
		if (lAndOr.size() < list.size()) {
			for (int i = 0; i < lAndOr.size(); i++) {
				if (i == 0) {
					if ("AND".equals(lAndOr.get(i))) {
						sOut = getStrings(list.get(i), list.get(i + 1));
					} else {
						sOut = getStringSet(list.get(i), list.get(i + 1));
					}
					System.out.println("sOut:" + Arrays.toString(sOut));
				} else {
					if ("AND".equals(lAndOr.get(i))) {
						sOut = getStrings(sOut, list.get(i + 1));
					} else {
						sOut = getStringSet(sOut, list.get(i + 1));
					}
					System.out.println("sOut:" + Arrays.toString(sOut));
				}

				// switch(i){
				// case 0:
				// if("AND".equals(lAndOr.get(i))){
				// sOut = getStrings(list.get(i),list.get(i+1));
				// }else{
				// sOut = getStringSet(list.get(i),list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 1:
				// if("AND".equals(lAndOr.get(i))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 2:
				// if("AND".equals(lAndOr.get(i))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 3:
				// if("AND".equals(lAndOr.get(i))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 4:
				// if("AND".equals(lAndOr.get(i))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 5:
				// if("AND".equals(lAndOr.get(i))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 6:
				// if("AND".equals(lAndOr.get(i))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// }
			}
		} else {
			for (int i = 0; i < lAndOr.size() - 1; i++) {
				if (i == 0) {
					if ("AND".equals(lAndOr.get(i + 1))) {
						sOut = getStrings(list.get(i), list.get(i + 1));
					} else {
						sOut = getStringSet(list.get(i), list.get(i + 1));
					}
					System.out.println("sOut:" + Arrays.toString(sOut));
				} else {
					if ("AND".equals(lAndOr.get(i + 1))) {
						sOut = getStrings(sOut, list.get(i + 1));
					} else {
						sOut = getStringSet(sOut, list.get(i + 1));
					}
					System.out.println("sOut:" + Arrays.toString(sOut));
				}
				// switch(i){
				// case 0:
				// if("AND".equals(lAndOr.get(i+1))){
				// sOut = getStrings(list.get(i),list.get(i+1));
				// }else{
				// sOut = getStringSet(list.get(i),list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 1:
				// if("AND".equals(lAndOr.get(i+1))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 2:
				// if("AND".equals(lAndOr.get(i+1))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 3:
				// if("AND".equals(lAndOr.get(i+1))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 4:
				// if("AND".equals(lAndOr.get(i+1))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// case 5:
				// if("AND".equals(lAndOr.get(i+1))){
				// sOut = getStrings(sOut,list.get(i+1));
				// }else{
				// sOut = getStringSet(sOut,list.get(i+1));
				// }
				// System.out.println("sOut:"+Arrays.toString(sOut));
				// break;
				// }
			}
		}

		return sOut;
	}
	
	//����ͬ��ȡ���� ����AND
	public String[] getStrings(String[] a, String[] b){  
        try{
        	  Set<String> same = new HashSet<String>();  //�������������������ͬ��Ԫ��  
	  	      Set<String> temp = new HashSet<String>();  //�����������a�е�Ԫ��  
	
	  	      for (int i = 0; i < a.length; i++) {  
	  	          temp.add(a[i]);   //������a�е�Ԫ�طŵ�Set�У�����ȥ���ظ���Ԫ��  
	  	      }  
	  	        
	  	      for (int j = 0; j < b.length; j++) {  
	  	        //������b�е�Ԫ����ӵ�temp��  
	  	        //���temp���Ѵ�����ͬ��Ԫ�أ���temp.add��b[j]������false  
	  	        if(!temp.add(b[j]))  
	  	            same.add(b[j]);  
	  	    }  
	  	    
	  	    ArrayList list = new ArrayList();
	  	    list.addAll(same);
	  	    String[] args = (String[]) list.toArray(new String[0]);
	  	    return args;  
        }catch(Exception e){
        	String[] args = null;
        	return args;
        }
	  }  
	

	// ���� OR
	public String[] getStringSet(String[] s1, String[] s2) {
		Set<String> set = new HashSet<String>();
		boolean b1 = false;
		boolean b2 = false;
		try {
			for (String s : s1) {
				set.add(s);
			}
		} catch (NullPointerException p) {
			b1 = true;
		}
		try {
			for (String s : s2) {
				set.add(s);
			}
		} catch (NullPointerException p) {
			b2 = true;
		}

		if (b1 == true && b2 == false) {
			String[] args = (String[]) set.toArray(new String[0]);
			return args;
		} else if (b2 == true && b1 == false) {
			String[] args = (String[]) set.toArray(new String[0]);
			return args;
		} else if (b1 == true && b2 == true) {
			String[] args = null;
			return args;
		} else {
			String[] args = (String[]) set.toArray(new String[0]);
			return args;
		}
	}

	// machao end 20170516
	/**
	 * �ڶ���ҳǩ��ѯ����
	 */
	public void onQueryTwo() {
		// ����������Ϣ
		if (getValueString("MR_NO_2").trim().length() <= 0) {
			this.messageBox("�����벡����");
			return;
		}
		TParm parmPat = new TParm();
		parmPat.setData("MR_NO",
				PatTool.getInstance().checkMrno(getValueString("MR_NO_2")));
		// ==============pangben modify 20110624 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parmPat.setData("REGION_CODE", Operator.getRegion());
		}
		// ==============pangben modify 20110624 stop
		setValue("MR_NO_2",
				PatTool.getInstance().checkMrno(getValueString("MR_NO_2")));
		((TTextField) getComponent("MR_NO_2")).setEnabled(false);
		parmPat = INFCaseTool.getInstance().caseRegisterPatInfo(parmPat);
		if (parmPat.getCount() <= 0)
			return;
		((TButton) getComponent("ADD_BUTTON_2")).setEnabled(true);
		setValue("INF_DR_2", parmPat.getData("VS_DR_CODE", 0));
		setValue("REGISTER_DATE_2", SystemTool.getInstance().getDate());
		setValue("CASE_NO_2", parmPat.getData("CASE_NO", 0));
		setValue("PAT_NAME_2", parmPat.getData("PAT_NAME", 0));
		setValue("AGE_2",
				StringTool.CountAgeByTimestamp(
						(Timestamp) parmPat.getData("BIRTH_DATE", 0),
						SystemTool.getInstance().getDate())[0]);
		setValue("IN_DATE_2", parmPat.getData("IN_DATE", 0));
		setValue("SEX_2", parmPat.getData("SEX_CODE", 0));
		setValue("OUT_DATE_2", parmPat.getData("DS_DATE", 0));
		setValue("IPD_NO_2", parmPat.getData("IPD_NO", 0));
		setValue("CHARGE_FEE_2", parmPat.getData("TOTAL_AMT", 0));
		setValue("DEPT_CODE_2", parmPat.getData("DEPT_CODE", 0));
		// סԺ�����ɸ���
		setValue("ADM_DAYS_2",
				ADMTool.getInstance()
						.getAdmDays(parmPat.getValue("CASE_NO", 0)));
		// ���������Ϣ
		TParm parmDiag = new TParm();
		parmDiag.setData("CASE_NO", parmPat.getData("CASE_NO", 0));
		parmDiag = INFCaseTool.getInstance().caseRegisterDiag(parmDiag);
		setAdmDiagInf(parmDiag);
		// �����пؼ�¼��Ϣ
		TParm parmCase = new TParm();
		parmCase.setData("CASE_NO", parmPat.getData("CASE_NO", 0));
		parmCase = INFCaseTool.getInstance().caseRegisterCase(parmCase);
		System.out.println("www:" + parmCase);
		int seqNum = parmCase.getCount() - 1;
		String infNo = parmCase.getCount() <= 0 ? "" : parmCase.getValue(
				"INF_NO", seqNum);
		setTable2And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);
		setTable3And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);
		setTable4And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);

		setTable5And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);// add
																		// by
																		// machao
																		// 20170509
		setTable6And2ByCaseInfNo(parmPat.getValue("CASE_NO", 0), infNo);// add
																		// by
																		// machao
																		// 20170509

		if (parmCase.getCount() <= 0)
			return;
		clearTParmNull(parmCase);
		setValue("INF_NO_2", parmCase.getData("INF_NO", seqNum));
		setValue("REGISTER_DATE_2", parmCase.getData("REGISTER_DATE", seqNum));
		setValue("INF_DR_2", parmCase.getData("INF_DR", seqNum));
		// סԺ�����ɸ���
		setValue("ADM_DAYS_2", parmCase.getData("ADM_DAYS", seqNum));
		// �����Ϣ�����޸�
		setValue("IN_DIAG1_2", parmCase.getData("IN_DIAG1_DESC", seqNum));
		setValue("IN_DIAG2_2", parmCase.getData("IN_DIAG2_DESC", seqNum));
		setValue("OUT_DIAG1_2", parmCase.getData("OUT_DIAG1_DESC", seqNum));
		setValue("OUT_DIAG2_2", parmCase.getData("OUT_DIAG2_DESC", seqNum));
		setValue("OUT_DIAG3_2", parmCase.getData("OUT_DIAG3_DESC", seqNum));
		setValue("INF_DATE_2", parmCase.getData("INF_DATE", seqNum));
		setValue("INFPOSITION_CODE_2",
				parmCase.getData("INFPOSITION_CODE", seqNum));
		setValue("INFPOSITION_DTL_2",
				parmCase.getData("INFPOSITION_DTL", seqNum));
		setValue("INF_DIAG1_2", parmCase.getData("INF_DIAG1_DESC", seqNum));
		setValue("INF_DIAG2_2", parmCase.getData("INF_DIAG2_DESC", seqNum));
		setInfDiagInf(parmCase, seqNum);
		setValue("DIEINFLU_CODE_2", parmCase.getData("DIEINFLU_CODE", seqNum));
		setValue("INFRETN_CODE_2", parmCase.getData("INFRETN_CODE", seqNum));
		setValue("OP_CODE_2", parmCase.getData("OP_CODE", seqNum));
		setValue("OP_DATE_2", parmCase.getData("OP_DATE", seqNum));
		setValue("OPCUT_TYPE_2", parmCase.getData("OPCUT_TYPE", seqNum));
		setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));
		setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));

		if (parmCase.getValue("URGTOP_FLG", seqNum).equals("Y"))
			setValue("URGTOP_FLG_A_2", "Y");
		else if (parmCase.getValue("URGTOP_FLG", seqNum).equals("N"))
			setValue("URGTOP_FLG_B_2", "Y");

		if (parmCase.getValue("OP_TIME", seqNum).length() >= 4) {
			setValue("OP_TIME_HH_2", parmCase.getValue("OP_TIME", seqNum)
					.substring(0, 2));
			setValue("OP_TIME_MM_2", parmCase.getValue("OP_TIME", seqNum)
					.substring(2, 4));
		}

		setValue("OP_DR_2", parmCase.getData("OP_DR", seqNum));
		setValue("INICU_DATE_2", parmCase.getData("INICU_DATE", seqNum));
		setValue("OUTICU_DATE_2", parmCase.getData("OUTICU_DATE", seqNum));

		setValue("OPB_OPSEQ_2", parmCase.getData("OPBOOK_SEQ", seqNum));
		if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("Y")) {
			setValue("ETIOLGEXM_FLG_Y_2", "Y");
			onEtiolgexmFlgYTwo();
		} else if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("N")) {
			setValue("ETIOLGEXM_FLG_N_2", "Y");
			onEtiolgexmFlgNTwo();
		}
		setValue("SPECIMEN_CODE_2", parmCase.getData("SPECIMEN_CODE", seqNum));
		setValue("EXAM_DATE_2", parmCase.getData("EXAM_DATE", seqNum));

		if (parmCase.getValue("LABWAY", seqNum).equals("1"))
			setValue("LABWAY_1_2", "Y");
		if (parmCase.getValue("LABWAY", seqNum).equals("2"))
			setValue("LABWAY_2_2", "Y");
		if (parmCase.getValue("LABWAY", seqNum).equals("3"))
			setValue("LABWAY_3_2", "Y");

		if (parmCase.getValue("LABPOSITIVE", seqNum).equals("Y"))
			setValue("LABPOSITIVE_A_2", "Y");
		if (parmCase.getValue("LABPOSITIVE", seqNum).equals("N"))
			setValue("LABPOSITIVE_B_2", "Y");
		setValue("PATHOGEN1_CODE_2", parmCase.getData("PATHOGEN1_CODE", seqNum));
		setValue("PATHOGEN2_CODE_2", parmCase.getData("PATHOGEN2_CODE", seqNum));
		setValue("PATHOGEN3_CODE_2", parmCase.getData("PATHOGEN3_CODE", seqNum));

		if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("Y"))
			setValue("ANTIBIOTEST_FLG_Y_2", "Y");
		if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("N"))
			setValue("ANTIBIOTEST_FLG_N_2", "Y");

		setTable1And2(parmCase);
		getTable("TABLE_1_2").setSelectedRow(seqNum);
		getAntiTestInfo(parmPat);
	}

	/**
	 * ȡ����������Ϣ
	 * 
	 * @param parmPat
	 *            TParm
	 */
	private void getAntiTestInfo(TParm parmPat) {
		getTable("TABLE_4_2").removeRowAll();
		TParm parmTable12 = getTable("TABLE_1_2").getParmValue();
		String SQL = " SELECT CULURE_CODE AS CULTURE_CODE,ANTI_CODE,SENS_LEVEL"
				+ " FROM INF_ANTIBIOTEST "
				+ " WHERE  CASE_NO = '"
				+ parmPat.getData("CASE_NO", 0)
				+ "'"
				+ " AND    INF_NO = '"
				+ getValue("INF_NO_2")
				+ "'"
				+ " AND    INFCASE_SEQ = '"
				+ parmTable12.getValue("INFCASE_SEQ", getTable("TABLE_1_2")
						.getSelectedRow()) + "'";
		TParm parm = new TParm(getDBTool().select(SQL));
		if (parm.getCount("CULTURE_CODE") <= 0)
			return;
		getTable("TABLE_4_2").setParmValue(parm);
	}

	//liuyalin 20170621 add ������ʱҽ��������ҽ������
	/**
	 * �õ��ż�ס��ֵ
	 * 
	 * @return String
	 */
	public String getRXRadioValue() {
		if (this.getTRadioButton("UD").isSelected())
			return "UD";
		if (this.getTRadioButton("ST").isSelected())
			return "ST";
		return "ST";
	}
	/**
	 * ����TRadonButton
	 * 
	 * @param tag
	 *            String
	 * @return TRadioButton
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) this.getComponent(tag);
	}
	//liuyalin 20170621 add end ������ʱҽ��������ҽ������

	/**
	 * ������ҳǩ��ѯ����
	 */
	public void onQueryThree() {
		if (getValueString("START_DATE_3").length() == 0
				|| getValueString("END_DATE_3").length() == 0) {
			messageBox("�����뿪ʼʱ�估����ʱ��");
			return;
		}
		if (StringTool.getDateDiffer((Timestamp) getValue("START_DATE_3"),
				(Timestamp) getValue("END_DATE_3")) > 0) {
			messageBox("¼������ڲ��Ϸ�");
			return;
		}
		TParm parm = new TParm();
		if (getValueString("DEPT_3").length() != 0)
			parm.setData("DEPT_CODE", getValue("DEPT_3"));
		if (getValueString("STATION_3").length() != 0)
			parm.setData("STATION_CODE", getValue("STATION_3"));
		if (getValueString("DR_3").length() != 0)
			parm.setData("ORDER_DR_CODE", getValue("DR_3"));
		if (getValueString("IPD_NO_3").length() != 0)
			parm.setData("IPD_NO",
					PatTool.getInstance().checkMrno(getValueString("IPD_NO_3")));
		if (getValueString("MR_NO_3").length() != 0) {
			parm.setData("MR_NO",
					PatTool.getInstance().checkMrno(getValueString("MR_NO_3")));
			setValue("MR_NO_3",
					PatTool.getInstance().checkMrno(getValueString("MR_NO_3")));
		}
//		this.messageBox(getValue("END_DATE").toString().substring(0, 19));
//		this.messageBox(getValue("START_DATE_3").toString().substring(0, 19));
		parm.setData("START_DATE", getValue("START_DATE_3").toString().substring(0, 19));
		String endDate = StringTool.getString(
				(Timestamp) getValue("END_DATE_3"), "yyyyMMddHHmmss")
				.substring(0, 8)
				+ "235959";
		parm.setData("END_DATE",
				getValue("END_DATE_3").toString().substring(0, 19));
		
		if (getValueString("ANTIBIOTIC_CODE_3").length() != 0)
			parm.setData("ANTIBIOTIC_CODE", getValue("ANTIBIOTIC_CODE_3"));
		// =========pangben modify 20110624 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// =========pangben modify 20110624 stop
		//liuyalin 20170621 modify ������ʱҽ��������ҽ������
		if ("UD".equals(getRXRadioValue())) {
			parm.setData("RX_KIND","UD");
		}
		if ("ST".equals(getRXRadioValue())) {
			parm.setData("RX_KIND","ST");
		}
		//liuyalin 20170621 modify end ������ʱҽ��������ҽ������
		parm = INFCaseTool.getInstance().selectAntibiotrcd(parm);
		getTable("TABLE_1_3").removeRowAll();
		if (parm.getErrCode() < 0)
			return;
		if (parm.getCount() <= 0)
			return;
		setValue("MR_NO_3", parm.getData("MR_NO", 0));
		setValue("IPD_NO_3", parm.getData("IPD_NO", 0));
		setValue("DEPT_3", parm.getData("DEPT_CODE", 0));
		setValue("STATION_3", parm.getData("STATION_CODE", 0));
		setValue("DR_3", parm.getData("VS_DR_CODE", 0));
		setValue("PAT_NAME_3", parm.getData("PAT_NAME", 0));
		setValue(
				"AGE_3",
				StringTool.CountAgeByTimestamp((Timestamp) parm.getData(
						"BIRTH_DATE", 0), SystemTool.getInstance().getDate())[0]);
		setValue("ANTIBIOTIC_CODE_3", parm.getData("ANTIBIOTIC_CODE", 0));
		getTable("TABLE_1_3").setParmValue(parm);
		getTable("TABLE_1_3").setSelectedRow(0);
	}

	/**
	 * ���ĸ�ҳǩ��ѯ����
	 */
	public void onQueryFour() {
		TParm parm = new TParm();
		if (getValueString("START_DATE_4").length() != 0)
			parm.setData("START_DATE", getValue("START_DATE_4"));
		if (getValueString("END_DATE_4").length() != 0)
			parm.setData("END_DATE", getValue("END_DATE_4"));
		if (getValueString("MR_NO_4").length() != 0)
			parm.setData("MR_NO", getValue("MR_NO_4"));
		if (getValueString("REPORT_DATE_4").length() != 0)
			parm.setData("REPORT_DATE", getValue("REPORT_DATE_4"));
		if (getValueString("REPORT_NO_4").length() != 0)
			parm.setData("REPORT_NO", getValue("REPORT_NO_4"));
		if (parm.getNames().length == 0) {
			messageBox("����������һ����ѯ����");
			return;
		}
		if (getValueString("START_DATE_4").length() != 0
				&& getValueString("END_DATE_4").length() != 0
				&& (StringTool.getDateDiffer(
						(Timestamp) getValue("START_DATE_4"),
						(Timestamp) getValue("END_DATE_4")) > 0)) {
			messageBox("¼������ڲ��Ϸ�");
			return;
		}
		if (getValue("REP_Y_4").equals("Y"))
			parm.setData("REPORT_DATE_NOT_NULL", "Y");
		else if (getValue("REP_N_4").equals("Y"))
			parm.setData("REPORT_DATE_NULL", "Y");
		if (getValue("IN_Y_4").equals("Y"))
			parm.setData("DS_DATE_NULL", "Y");
		else if (getValue("IN_N_4").equals("Y"))
			parm.setData("DS_DATE_NOT_NULL", "Y");
		// ==========pangben modify 20110624 start
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0)
			parm.setData("REGION_CODE", Operator.getRegion());
		// ==========pangben modify 20110624 stop
		parm = INFCaseTool.getInstance().selectInfCaseReport(parm);
		getTable("TABLE_1_4").removeRowAll();
		if (parm.getErrCode() < 0)
			return;
		if (parm.getCount() <= 0)
			return;
		getTable("TABLE_1_4").setParmValue(parm);
	}

	/**
	 * ���������Ϣ
	 * 
	 * @param parmDiag
	 *            TParm
	 */
	private void setAdmDiagInf(TParm parmDiag) {
		for (int i = 0; i < parmDiag.getCount("ICD_CODE"); i++) {
			if (parmDiag.getData("IO_TYPE", i).equals("M")
					&& parmDiag.getData("MAINDIAG_FLG", i).equals("Y")) {
				inDiag1.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
				inDiag1.setData("ICD_CHN_DESC",
						parmDiag.getData("ICD_CHN_DESC", i));
			} else if (parmDiag.getData("IO_TYPE", i).equals("M")
					&& parmDiag.getData("MAINDIAG_FLG", i).equals("N")
					&& inDiag2.getValue("ICD_CODE").length() == 0
					&& inDiag2.getValue("ICD_CHN_DESC").length() == 0) {
				inDiag2.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
				inDiag2.setData("ICD_CHN_DESC",
						parmDiag.getData("ICD_CHN_DESC", i));
			} else if (parmDiag.getData("IO_TYPE", i).equals("O")
					&& parmDiag.getData("MAINDIAG_FLG", i).equals("Y")) {
				outDiag1.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
				outDiag1.setData("ICD_CHN_DESC",
						parmDiag.getData("ICD_CHN_DESC", i));
			} else if (parmDiag.getData("IO_TYPE", i).equals("O")
					&& parmDiag.getData("MAINDIAG_FLG", i).equals("N")
					&& outDiag2.getValue("ICD_CODE").length() == 0
					&& outDiag2.getValue("ICD_CHN_DESC").length() == 0) {
				outDiag2.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
				outDiag2.setData("ICD_CHN_DESC",
						parmDiag.getData("ICD_CHN_DESC", i));
			} else if (parmDiag.getData("IO_TYPE", i).equals("O")
					&& parmDiag.getData("MAINDIAG_FLG", i).equals("N")
					&& outDiag3.getValue("ICD_CODE").length() == 0
					&& outDiag3.getValue("ICD_CHN_DESC").length() == 0) {
				outDiag3.setData("ICD_CODE", parmDiag.getData("ICD_CODE", i));
				outDiag3.setData("ICD_CHN_DESC",
						parmDiag.getData("ICD_CHN_DESC", i));
			}
		}
		setValue("IN_DIAG1_2", inDiag1.getData("ICD_CHN_DESC"));
		setValue("IN_DIAG2_2", inDiag2.getData("ICD_CHN_DESC"));
		setValue("OUT_DIAG1_2", outDiag1.getData("ICD_CHN_DESC"));
		setValue("OUT_DIAG2_2", outDiag2.getData("ICD_CHN_DESC"));
		setValue("OUT_DIAG3_2", outDiag3.getData("ICD_CHN_DESC"));
	}

	// liuyalin 20170421 add
	/**
	 * �����ҳǩ��ѯ����
	 */
	public void onQueryFive() {
		if (getValueString("START_DATE_5").length() == 0
				|| getValueString("END_DATE_5").length() == 0) {
			messageBox("�����뿪ʼʱ�估����ʱ��");
			return;
		}
		TParm parm = new TParm();
		if (getValueString("DEPT_5").length() != 0) {
			parm.setData("DEPT_CODE", getValue("DEPT_5"));
		}
		if (getValueString("STATION_5").length() != 0) {
			parm.setData("STATION_CODE", getValue("STATION_5"));
		}
		if (getValueString("DR_5").length() != 0) {
			parm.setData("VS_DR_CODE", getValue("DR_5"));
		}
		if (getValueString("IPD_NO_5").length() != 0) {
			parm.setData("IPD_NO",
					PatTool.getInstance().checkMrno(getValueString("IPD_NO_5")));
		}
		if (getValueString("MR_NO_5").length() != 0) {
			parm.setData("MR_NO",
					PatTool.getInstance().checkMrno(getValueString("MR_NO_5")));
			setValue("MR_NO_5",
					PatTool.getInstance().checkMrno(getValueString("MR_NO_5")));
		}
		if (getValueString("ORDER_CODE_5").length() != 0) {
			parm.setData("ORDER_CODE", getValue("ORDER_CODE_5"));
		}
		if (getValueString("ORDER_DESC_5").length() != 0) {
			parm.setData("ORDER_DESC", getValue("ORDER_DESC_5"));
		}
		parm.setData("START_DATE", getValue("START_DATE_5"));
		String endDate = StringTool.getString(
				(Timestamp) getValue("END_DATE_5"), "yyyyMMddHHmmss")
				.substring(0, 8)
				+ "235959";
		parm.setData("END_DATE",
				StringTool.getTimestamp(endDate, "yyyyMMddHHmmss"));
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		parm = INFCaseTool.getInstance().selectInvOpt(parm);
		getTable("TABLE_1_5").removeRowAll();
		if (parm.getErrCode() < 0)
			return;
		if (parm.getCount() <= 0)
			return;
//		this.setValue("MR_NO_5", parm.getData("MR_NO", 0));
//		this.setValue("IPD_NO_5", parm.getData("IPD_NO", 0));
//		this.setValue("DEPT_5", parm.getData("DEPT_CODE", 0));
//		this.setValue("STATION_5", parm.getData("STATION_CODE", 0));
//		this.setValue("DR_5", parm.getData("VS_DR_CODE", 0));
//		this.setValue("PAT_NAME_5", parm.getData("PAT_NAME", 0));
//		this.setValue(
//				"AGE_5",
//				StringTool.CountAgeByTimestamp((Timestamp) parm.getData(
//						"BIRTH_DATE", 0), SystemTool.getInstance().getDate())[0]);
		getTable("TABLE_1_5").setParmValue(parm);
		//getTable("TABLE_1_5").setSelectedRow(0);
		
	}

	// liuyalin 20170421 add end
	/**
	 * ���������Ϣ
	 * 
	 * @param parmCase
	 *            TParm
	 * @param seqNum
	 *            int
	 */
	private void setInfDiagInf(TParm parmCase, int seqNum) {
		inDiag1.setData("ICD_CODE", parmCase.getData("IN_DIAG1", seqNum));
		inDiag1.setData("ICD_CHN_DESC",
				parmCase.getData("IN_DIAG1_DESC", seqNum));

		inDiag2.setData("ICD_CODE", parmCase.getData("IN_DIAG2", seqNum));
		inDiag2.setData("ICD_CHN_DESC",
				parmCase.getData("IN_DIAG2_DESC", seqNum));

		outDiag1.setData("ICD_CODE", parmCase.getData("OUT_DIAG1", seqNum));
		outDiag1.setData("ICD_CHN_DESC",
				parmCase.getData("OUT_DIAG1_DESC", seqNum));

		outDiag2.setData("ICD_CODE", parmCase.getData("OUT_DIAG2", seqNum));
		outDiag2.setData("ICD_CHN_DESC",
				parmCase.getData("OUT_DIAG2_DESC", seqNum));

		outDiag3.setData("ICD_CODE", parmCase.getData("OUT_DIAG3", seqNum));
		outDiag3.setData("ICD_CHN_DESC",
				parmCase.getData("OUT_DIAG3_DESC", seqNum));

		infDiag1.setData("ICD_CODE", parmCase.getData("INF_DIAG1", seqNum));
		infDiag1.setData("ICD_CHN_DESC",
				parmCase.getData("INF_DIAG1_DESC", seqNum));

		infDiag2.setData("ICD_CODE", parmCase.getData("INF_DIAG2", seqNum));
		infDiag2.setData("ICD_CHN_DESC",
				parmCase.getData("INF_DIAG2_DESC", seqNum));

	}

	/**
	 * ����TABLE_1_2����
	 * 
	 * @param parmCase
	 *            TParm
	 */
	private void setTable1And2(TParm parmCase) {
		TParm tableParmCase = new TParm();
		for (int i = 0; i < parmCase.getCount(); i++) {
			if (parmCase.getData("REPORT_DATE", i) == null
					|| parmCase.getValue("REPORT_DATE", i).length() == 0)
				tableParmCase.addData("RPT_FLG", "N");
			else
				tableParmCase.addData("RPT_FLG", "Y");
			tableParmCase.addData("INFCASE_SEQ",
					parmCase.getData("INFCASE_SEQ", i));
			tableParmCase.addData("INF_DATE", parmCase.getData("INF_DATE", i));
			tableParmCase.addData("INF_NO", parmCase.getData("INF_NO", i));
		}
		getTable("TABLE_1_2").setParmValue(tableParmCase);
	}

	/**
	 * ���ݲ�����Ⱦ���סԺ������ø�Ⱦԭ����
	 * 
	 * @param caseNo
	 *            String
	 * @param infNo
	 *            String
	 */
	private void setTable2And2ByCaseInfNo(String caseNo, String infNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("INF_NO", infNo);
		TParm result = INFCaseTool.getInstance().selectInfReasonByCaseInfNo(
				parm);
		getTable("TABLE_2_2").setParmValue(result);
	}

	/**
	 * ���ݲ�����Ⱦ���סԺ������ø�Ⱦԭ����
	 * 
	 * @param caseNo
	 *            String
	 * @param infNo
	 *            String
	 */
	private void setTable3And2ByCaseInfNo(String caseNo, String infNo) {
		// ������Ϣ�Ƿ��õ���ȷ��
		if (true)
			return;
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("INF_NO", infNo);
		TParm result = INFCaseTool.getInstance().selectInfIntvoprecByCaseInfNo(
				parm);
		getTable("TABLE_3_2").setParmValue(result);
	}

	/**
	 * ���ݲ�����Ⱦ���סԺ������ü�ʵ�������
	 * 
	 * @param caseNo
	 *            String
	 * @param infNo
	 *            String
	 */
	private void setTable4And2ByCaseInfNo(String caseNo, String infNo) {
		// ��ṹ�иĶ���ȷ��
		if (true)
			return;
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("INF_NO", infNo);
		TParm result = INFCaseTool.getInstance().selectInfResultByCaseInfNo(
				parm);
		getTable("TABLE_4_2").setParmValue(result);
	}

	// ����������
	public void onAddOpeSeq() {
		if (StringUtil.isNullString(this.getValueString("CASE_NO_2"))) {
			this.messageBox("����������");
			return;
		}
		TParm p = new TParm();
		p.setData("CASE_NO", this.getValueString("CASE_NO_2"));
		TParm result = (TParm) this.openDialog("%ROOT%/config/ope/OPEDetail.x",
				p);

		setValue("OPB_OPSEQ_2", result.getValue("OPBOOK_SEQ"));

	}

	// machao 20170421 add
	/**
	 * ���ݲ�����Ⱦ���סԺ������ø�Ⱦ��λ��ϱ��
	 * 
	 * @param caseNo
	 *            String
	 * @param infNo
	 *            String
	 */
	private void setTable5And2ByCaseInfNo(String caseNo, String infNo) {
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("INF_NO", infNo);
		TParm result = new TParm();
		result = INFCaseTool.getInstance().selectInfICDPartByCaseInfNo(parm);

		getTable("TABLE_5_2").setParmValue(result);
		int row = getTable("TABLE_5_2").addRow();
		getTable("TABLE_5_2").getParmValue().setData("MAIN_FLG", row, "N");
		getTable("TABLE_5_2").getParmValue().setData("INF_SYSTEMCODE", row, "");
		getTable("TABLE_5_2").getParmValue().setData("PART_CODE", row, "");
		getTable("TABLE_5_2").getParmValue().setData("ICD_CODE", row, "");
		getTable("TABLE_5_2").getParmValue().setData("ICD_DESC", row, "");
		getTable("TABLE_5_2").getParmValue().setData("UM_CODE", row, "");
		getTable("TABLE_5_2").getParmValue().setData("UM_DESC", row, "");
		if (!infNo.equals("")) {
			getTable("TABLE_5_2").getParmValue().setData("SEQ", row,
					result.getInt("SEQ", row - 1) + 1);
		} else {
			getTable("TABLE_5_2").getParmValue().setData("SEQ", row, 1);
		}
	}

	/**
	 * ���ݲ�����Ⱦ���סԺ������������Բ������
	 * 
	 * @param caseNo
	 *            String
	 * @param infNo
	 *            String
	 */
	private void setTable6And2ByCaseInfNo(String caseNo, String infNo) {

		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm result = new TParm();
		if (!infNo.equals("")) {
			parm.setData("INF_NO", infNo);
			result = INFCaseTool.getInstance().selectInfIoByCaseInfNo(parm);
		} else {
			result = INFCaseTool.getInstance().selectInfIoFromOdiByCase(parm);
		}
		getTable("TABLE_6_2").setParmValue(result);
	}

	// machao 20170421 add end

	/**
	 * ȡ��Table�ؼ�
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}

	// modify machao start
	/**
	 * ʹ�ÿ�����ע��
	 */
	public void onChangeAntibiotrcdFlg() {
		((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setValue("");
		if (getValueString("ANTIBIOTIC_F_1").equals("N"))
			((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setEnabled(false);
		else
			((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setEnabled(true);
	}

	/**
	 * ������ϲ���ע��
	 */
	public void onChangeProtocolFlg() {
		((TTextField) getComponent("DIACODE_1")).setValue("");
		((TTextField) getComponent("DIACODE_DESC_1")).setValue("");
		if (getValueString("PROTOCOL_F_1").equals("N"))
			((TTextField) getComponent("DIACODE_1")).setEnabled(false);
		else
			((TTextField) getComponent("DIACODE_1")).setEnabled(true);
	}

	/**
	 * �������ע��
	 */
	public void onChangeInvadeFlg() {
		((TTextField) getComponent("ORDER_CODE_1")).setValue("");
		((TTextField) getComponent("ORDER_DESC_1")).setValue("");
		if (getValueString("INVADE_F_1").equals("N"))
			((TTextField) getComponent("ORDER_CODE_1")).setEnabled(false);
		else
			((TTextField) getComponent("ORDER_CODE_1")).setEnabled(true);
	}

	/**
	 * ����
	 */
	public void onChangeOpeFlg(){
		((TTextField) getComponent("OPB_DAYS")).setValue("");
		if(getValueString("OP_F").equals("N")){
			((TTextField) getComponent("OPB_DAYS")).setEnabled(false);
		}else{
			((TTextField) getComponent("OPB_DAYS")).setEnabled(true);
		}
	}
	// modify machao end

	/**
	 * ȡ��ʱ�������ַ�����ʽ
	 * 
	 * @param dateTag
	 *            String
	 * @return String
	 */
	private String getDateString(String dateTag) {
		return getValueString(dateTag).substring(0, 10).replace("-", "");
	}

	/**
	 * ȡ�����ݿ������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * ���涯��
	 */
	public void onSave() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 0:
			return;
		case 1:
			onSaveTwo();
			break;
		case 2:
			onSaveThree();
			break;
		case 3:
			onSaveFour();
			break;
		}
	}

	/**
	 * �ڶ���ҳǩ���涯�����
	 * 
	 * @return boolean
	 */
	private boolean onSaveTwoCheck() {
		if (getValueString("INF_DATE_2").length() == 0) {
			messageBox("��Ⱦ���ڲ���Ϊ��");
			return true;
		}

		if (getValueString("INF_DR_2").length() == 0) {
			messageBox("����ҽ������Ϊ��");
			return true;
		}
		// if (getValueString("INF_DIAG1_2").length() == 0) {
		// messageBox("��Ⱦ��ϲ���Ϊ��");
		// return true;
		// }
		// if (getValueString("INFPOSITION_CODE_2").length() == 0) {
		// messageBox("��Ⱦ��λ����Ϊ��");
		// return true;
		// }
		if (getValueString("REGISTER_DATE_2").length() == 0) {
			messageBox("�Ǽ�ʱ�䲻��Ϊ��");
			return true;
		}
		for (int i = 0; i < getTable("TABLE_1_2").getRowCount(); i++) {
			if (getValueString("INF_NO_2").length() == 0
					&& getValue("INF_DATE_2").equals(
							getTable("TABLE_1_2").getValueAt(i, 2))) {
				messageBox("�������и�Ⱦ�����Ǽ�");
				return true;
			}
		}
		TParm parm = getTable("TABLE_2_2").getParmValue();
		for (int i = 0; i < parm.getCount("SEL_FLG"); i++) {
			if (parm.getData("SEL_FLG", i).equals("N"))
				continue;
			if (parm.getValue("INFREASON_NOTE", i).length() > 100) {
				messageBox("������Ⱦ������ر�ע���Ȳ��ɳ���100");
				return true;
			}
		}
		 //machao 20170421 add
		 TParm parm5 = getTable("TABLE_5_2").getParmValue();
		 if (parm5.getCount() < 1
		 || (parm5.getCount() == 1 &&
		 (StringUtil.isNullString(parm5.getValue("PART_CODE", 0))
		 || StringUtil.isNullString(parm5.getValue("ICD_DESC", 0))
		 || StringUtil.isNullString(parm5.getValue("UM_DESC", 0))
		 || StringUtil.isNullString(parm5.getValue("INF_SYSTEMCODE", 0))))) {
		 messageBox("��Ⱦϵͳ,��Ⱦ��λ����ϱ���Ϊ��");
		 return true;
		 }
		 int j = 0;
		 for (; j < parm5.getCount("MAIN_FLG"); j++) {
		 if (parm5.getData("MAIN_FLG", j).equals("N")) continue;
		 else break;
		 }
		 if (j == parm5.getCount("MAIN_FLG")) {
		 messageBox("��Ⱦ��λ����ϱ���Ҫ��������¼");
		 return true;
		 }
		 TParm parm6 = getTable("TABLE_6_2").getParmValue();
		 for (int k = 0; k < parm6.getCount("SEL_FLG"); k++) {
		 if (parm6.getData("SEL_FLG", k).equals("N")) continue;
		 if (parm6.getValue("IO_NOTE", k).length() > 100) {
		 messageBox("�����Բ�����ı�ע���Ȳ��ɳ���100");
		 return true;
		 }
		 }
		 //machao 20170421 add end

		if(getValueString("ETIOLGEXM_FLG_Y_2").equals("Y")){
			if(StringUtil.isNullString(getValueString("SPECIMEN_CODE_2"))){
				this.messageBox("����д�걾����");
				return true;
			}
			if(StringUtil.isNullString(getValueString("EXAM_DATE_2"))){
				this.messageBox("����д�ͼ�����");
				return true;
			}
		}
		if (chkAntiTest())
			return true;
		return false;
	}

	/**
	 * �ڶ���ҳǩ���涯��
	 */
	private void onSaveTwo() {
		getTable("TABLE_1_2").acceptText();
		getTable("TABLE_2_2").acceptText();
		getTable("TABLE_4_2").acceptText();
		getTable("TABLE_5_2").acceptText();
		getTable("TABLE_6_2").acceptText();
		if (onSaveTwoCheck())
			return;
		TParm parm = new TParm();
		parm.setData("INF_NO", getValue("INF_NO_2"));
		parm.setData("CASE_NO", getValue("CASE_NO_2"));
		if (getValueString("INF_NO_2").length() > 0
				&& getTable("TABLE_1_2").getSelectedRow() >= 0)
			parm.setData(
					"INFCASE_SEQ",
					getTable("TABLE_1_2").getValueAt(
							getTable("TABLE_1_2").getSelectedRow(), 1));
		else
			parm.setData("INFCASE_SEQ", getTable("TABLE_1_2").getRowCount() + 1);
		parm.setData("IPD_NO", getValue("IPD_NO_2"));
		parm.setData("MR_NO", getValue("MR_NO_2"));
		parm.setData("INF_DATE", getValue("INF_DATE_2"));
		parm.setData("ADM_DAYS", getValue("ADM_DAYS_2"));
		parm.setData("DEPT_CODE", getValue("DEPT_CODE_2"));
		TParm parmAdm = getAdmInpInf(getValueString("CASE_NO_2"));
		if (parmAdm != null) {
			parm.setData("STATION_CODE", parmAdm.getData("STATION_CODE", 0));
			parm.setData("BED_NO", parmAdm.getData("BED_NO", 0));
			parm.setData("VS_DR", parmAdm.getData("VS_DR_CODE", 0));
		} else {
			parm.setData("STATION_CODE", "");
			parm.setData("BED_NO", "");
			parm.setData("VS_DR", "");
		}
		// if (getValueString("INF_DIAG1_2").length() != 0)
		// parm.setData("INF_DIAG1", infDiag1.getData("ICD_CODE"));
		// else {
		// parm.setData("INF_DIAG1", "");
		// infDiag1.setData("ICD_CODE", "");
		// infDiag1.setData("ICD_CHN_DESC", "");
		// }
		// if (getValueString("INF_DIAG2_2").length() != 0)
		// parm.setData("INF_DIAG2", infDiag2.getData("ICD_CODE"));
		// else {
		// parm.setData("INF_DIAG2", "");
		// infDiag2.setData("ICD_CODE", "");
		// infDiag2.setData("ICD_CHN_DESC", "");
		// }
		// parm.setData("INFPOSITION_CODE", getValue("INFPOSITION_CODE_2"));
		// parm.setData("INFPOSITION_DTL", getValue("INFPOSITION_DTL_2"));
		parm.setData("INF_DIAG1", "");
		parm.setData("INF_DIAG2", "");
		parm.setData("INFPOSITION_CODE", "");
		parm.setData("INFPOSITION_DTL", "");// add end
		parm.setData("INF_SOURCE",getValue("INF_SOURCE"));//��Ⱦ��Դ
		parm.setData("INF_AREA",getValue("INF_AREA"));//��Ⱦ�ص�
		parm.setData("DIEINFLU_CODE", getValue("DIEINFLU_CODE_2"));
		parm.setData("INFRETN_CODE", getValue("INFRETN_CODE_2"));
		parm.setData("OP_CODE", getValue("OP_CODE_2"));
		parm.setData("OP_DATE", getValue("OP_DATE_2"));
		parm.setData("OPCUT_TYPE", getValue("OPCUT_TYPE_2"));
		parm.setData("OPBOOK_SEQ", getValue("OPB_OPSEQ_2"));
		parm.setData("ANA_TYPE", getValue("ANA_TYPE_2"));
		if (getValueString("URGTOP_FLG_A_2").equals("Y"))
			parm.setData("URGTOP_FLG", "Y");
		else if (getValueString("URGTOP_FLG_B_2").equals("Y"))
			parm.setData("URGTOP_FLG", "N");
		parm.setData("OP_TIME", addZreo(getValueString("OP_TIME_HH_2"), 2)
				+ addZreo(getValueString("OP_TIME_MM_2"), 2));
		parm.setData("OP_DR", getValueString("OP_DR_2"));
		if (getValueString("IN_DIAG1_2").length() != 0)
			parm.setData("IN_DIAG1", inDiag1.getData("ICD_CODE"));
		else {
			parm.setData("IN_DIAG1", "");
			inDiag1.setData("ICD_CODE", "");
			inDiag1.setData("ICD_CHN_DESC", "");
		}
		if (getValueString("IN_DIAG2_2").length() != 0)
			parm.setData("IN_DIAG2", inDiag2.getData("ICD_CODE"));
		else {
			parm.setData("IN_DIAG2", "");
			inDiag2.setData("ICD_CODE", "");
			inDiag2.setData("ICD_CHN_DESC", "");
		}
		if (getValueString("OUT_DIAG1_2").length() != 0)
			parm.setData("OUT_DIAG1", outDiag1.getData("ICD_CODE"));
		else {
			parm.setData("OUT_DIAG1", "");
			outDiag1.setData("ICD_CODE", "");
			outDiag1.setData("ICD_CHN_DESC", "");
		}
		if (getValueString("OUT_DIAG2_2").length() != 0)
			parm.setData("OUT_DIAG2", outDiag2.getData("ICD_CODE"));
		else {
			parm.setData("OUT_DIAG2", "");
			outDiag2.setData("ICD_CODE", "");
			outDiag2.setData("ICD_CHN_DESC", "");
		}
		if (getValueString("OUT_DIAG3_2").length() != 0)
			parm.setData("OUT_DIAG3", outDiag3.getData("ICD_CODE"));
		else {
			parm.setData("OUT_DIAG3", "");
			outDiag3.setData("ICD_CODE", "");
			outDiag3.setData("ICD_CHN_DESC", "");
		}
		parm.setData("CHARGE_FEE", getValueString("CHARGE_FEE_2"));
		if (getValueString("ETIOLGEXM_FLG_Y_2").equals("Y"))
			parm.setData("ETIOLGEXM_FLG", "Y");
		else if (getValueString("ETIOLGEXM_FLG_N_2").equals("Y"))
			parm.setData("ETIOLGEXM_FLG", "N");
		parm.setData("EXAM_DATE", getValue("EXAM_DATE_2"));
		parm.setData("SPECIMEN_CODE", getValueString("SPECIMEN_CODE_2"));
		if (getValueString("LABWAY_1_2").equals("Y"))
			parm.setData("LABWAY", "1");
		else if (getValueString("LABWAY_2_2").equals("Y"))
			parm.setData("LABWAY", "2");
		else if (getValueString("LABWAY_3_2").equals("Y"))
			parm.setData("LABWAY", "3");
		else
			parm.setData("LABWAY", "");
		if (getValueString("LABPOSITIVE_A_2").equals("Y"))
			parm.setData("LABPOSITIVE", "Y");
		else if (getValueString("LABPOSITIVE_B_2").equals("Y"))
			parm.setData("LABPOSITIVE", "N");
		else
			parm.setData("LABPOSITIVE", "");
		parm.setData("PATHOGEN1_CODE", getValue("PATHOGEN1_CODE_2"));
		parm.setData("PATHOGEN2_CODE", getValue("PATHOGEN2_CODE_2"));
		parm.setData("PATHOGEN3_CODE", getValue("PATHOGEN3_CODE_2"));
		if (getValueString("ANTIBIOTEST_FLG_Y_2").equals("Y"))
			parm.setData("ANTIBIOTEST_FLG", "Y");
		else if (getValueString("ANTIBIOTEST_FLG_N_2").equals("Y"))
			parm.setData("ANTIBIOTEST_FLG", "N");
		parm.setData("REGISTER_DATE", getValue("REGISTER_DATE_2"));
		parm.setData("INF_DR", getValueString("INF_DR_2"));
		parm.setData("CLINICAL_SYMP", getValue("CLINICAL_SYMP_2"));
		// Ŀǰ���õĿ��ƴ�ʩ
		parm.setData("INF_PLAN", "");
		// �ϱ�����
		parm.setData("REPORT_DATE", "");
		// �ϱ����
		parm.setData("REPORT_NO", "");
		// �������ICH����ʱ���Ƿ�Ϸ�
		if (getValueString("INICU_DATE_2").length() == 0
				&& getValueString("OUTICU_DATE_2").length() != 0) {
			messageBox("��¼�������ICUʱ��");
			return;
		}
		if (getValueString("INICU_DATE_2").length() != 0
				&& getValueString("OUTICU_DATE_2").length() == 0) {
			messageBox("��¼�������ICUʱ��");
			return;
		}
		if (getValueString("INICU_DATE_2").length() != 0
				&& getValueString("INICU_DATE_2").length() != 0
				&& StringTool.getDateDiffer(
						(Timestamp) getValue("INICU_DATE_2"),
						(Timestamp) getValue("OUTICU_DATE_2")) > 0) {
			messageBox("¼����������ICU,��ICUʱ�䲻�Ϸ�");
			return;
		}
		parm.setData("INICU_DATE", getValue("INICU_DATE_2"));
		parm.setData("OUTICU_DATE", getValue("OUTICU_DATE_2"));
		parm.setData("CANCEL_FLG", "N");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());// ========pangben
															// modify 20110628
		clearTParmNullSingle(parm);
		TParm transParm = new TParm();
		transParm.setData("INF_CASE", parm.getData());
		transParm.setData("INF_REASON", setInfReason().getData());
		transParm.setData("INF_ANTIBIOTEST", setInfAntibioTest().getData());

		TParm infICDpartParm = setInfICDPart();
		for (int i = 0; i < infICDpartParm.getCount("MAIN_FLG"); i++) {
			if (infICDpartParm.getValue("MAIN_FLG", i).equals("Y")) {
				parm.setData("INF_DIAG1",
						infICDpartParm.getValue("ICD_CODE", i));// ��Ⱦ���
				parm.setData("INFPOSITION_CODE",
						infICDpartParm.getValue("PART_CODE", i));// ��Ⱦ��λ
				parm.setData("INF_SYSTEMCODE",
						infICDpartParm.getValue("INF_SYSTEMCODE", i));//��Ⱦϵͳ
				break;
			}
		}
		transParm.setData("INF_ICDPART", infICDpartParm.getData());
		transParm.setData("INF_IO", setInIO().getData());

		transParm.setData("REGION_CODE", Operator.getRegion()); // ========pangben
																// modify
																// 20110624

		transParm = TIOM_AppServer.executeAction("action.inf.InfAction",
				"onSaveInfCase", transParm);
		if (transParm.getErrCode() < 0) {
			messageBox("����ʧ��");
			return;
		}
		messageBox("����ɹ�");
		onQueryTwo();
	}

	/**
	 * ��˲�ԭ�����������Ƿ����
	 * 
	 * @return boolean
	 */
	private boolean chkAntiTest() {
		TParm parm = getTable("TABLE_4_2").getParmValue();
		for (int i = 0; i < getTable("TABLE_4_2").getRowCount(); i++) {
			// if (parm.getData("DEL_FLG",i).equals("Y"))
			// continue;
			if (parm.getValue("CULTURE_CODE", i).length() == 0
					|| parm.getValue("ANTI_CODE", i).length() == 0
					|| parm.getValue("SENS_LEVEL", i).length() == 0) {
				messageBox("�����ݵĲ�ԭ�塢���ҩ���������Ϊ��");
				return true;
			}
			for (int j = 0; j < getTable("TABLE_4_2").getRowCount(); j++) {
				if (i == j)
					continue;
				if (parm.getData("CULTURE_CODE", i).equals(
						parm.getData("CULTURE_CODE", j))
						&& parm.getData("ANTI_CODE", i).equals(
								parm.getData("ANTI_CODE", j))) {
					messageBox("���ظ��Ĳ�ԭ������ҩ��");
					return true;
				}
			}

		}
		return false;
	}

	/**
	 * ����
	 * 
	 * @param str
	 *            String
	 * @param length
	 *            int
	 * @return String
	 */
	private String addZreo(String str, int length) {
		for (int i = 0; i < length - str.length(); i++) {
			str = "0" + str;
		}
		return str;
	}

	/**
	 * ������ҳǩ���涯��
	 */
	private void onSaveThree() {
		getTable("TABLE_1_3").acceptText();
		TParm parmTable = getTable("TABLE_1_3").getParmValue();
		int row = getTable("TABLE_1_3").getRowCount();
		if (row <= 0) {
			messageBox("�ޱ�������");
			return;
		}
		TParm parm = new TParm();
		for (int i = 0; i < row; i++) {
			if (parmTable.getValue("ILLEGIT_REMARK", i).length() > 100) {
				messageBox("������ԭ�򳤶Ȳ��ɴ���100");
				return;
			}
			if (parmTable.getValue("MEDALLERG_SYMP", i).length() > 200) {
				messageBox("������Ӧ���Ȳ��ɴ���200");
				return;
			}
			parm.addData("ORDER_NO", parmTable.getValue("ORDER_NO", i));
			parm.addData("ORDER_SEQ", parmTable.getData("ORDER_SEQ", i));
			parm.addData("ORDER_CODE", parmTable.getValue("ORDER_CODE", i));
			parm.addData("CASE_NO", parmTable.getValue("CASE_NO", i));
			parm.addData("IPD_NO", parmTable.getValue("IPD_NO", i));
			parm.addData("MR_NO", parmTable.getValue("MR_NO", i));
			parm.addData("ILLEGIT_FLG", parmTable.getData("ILLEGIT_FLG", i));
			parm.addData("ILLEGIT_REMARK",
					parmTable.getValue("ILLEGIT_REMARK", i));
			parm.addData("MEDALLERG_SYMP",
					parmTable.getValue("MEDALLERG_SYMP", i));
			parm.addData("OPT_USER", Operator.getID());
			parm.addData("OPT_DATE", SystemTool.getInstance().getDate());
			parm.addData("OPT_TERM", Operator.getIP());
		}
		parm = TIOM_AppServer.executeAction("action.inf.InfAction",
				"onSaveInfAntibiotrcd", parm);
		if (parm.getErrCode() < 0) {
			messageBox(parm.getErrText());
			return;
		}
		messageBox("����ɹ�");
	}

	/**
	 * ���ĸ�ҳǩ���涯��
	 */
	private void onSaveFour() {
		int row = getTable("TABLE_1_4").getSelectedRow();
		if (row < 0) {
			messageBox("��ѡ�񱣴���Ϣ");
			return;
		}
		if (getValueString("REPORT_NO_4").length() == 0
				|| getValueString("REPORT_DATE_4").length() == 0) {
			messageBox("��ͬʱ�����ϱ������Լ��ϱ����");
			return;
		}
		TParm tableParm = getTable("TABLE_1_4").getParmValue();
		TParm parm = new TParm();
		parm.setData("INF_NO", tableParm.getData("INF_NO", row));
		parm.setData("CASE_NO", tableParm.getData("CASE_NO", row));
		parm.setData("INFCASE_SEQ", tableParm.getData("INFCASE_SEQ", row));
		parm.setData("REPORT_DATE", getValue("REPORT_DATE_4"));
		parm.setData("REPORT_NO", getValue("REPORT_NO_4"));
		parm = TIOM_AppServer.executeAction("action.inf.InfAction",
				"updateInfCaseReport", parm);
		if (parm.getErrCode() < 0) {
			messageBox("����ʧ��");
			return;
		}
		messageBox("����ɹ�");
		onQueryFour();
	}

	/**
	 * ���ø�Ⱦ���ر�������
	 * 
	 * @return TParm
	 */
	private TParm setInfReason() {
		TParm parm = getTable("TABLE_2_2").getParmValue();
		TParm parmReason = new TParm();
		for (int i = 0; i < getTable("TABLE_2_2").getRowCount(); i++) {
			if (parm.getData("SEL_FLG", i).equals("N"))
				continue;
			parmReason.addData("INF_NO", getValue("INF_NO_2"));
			parmReason.addData("INFREASON_CODE", parm.getData("ID", i));
			parmReason.addData("CASE_NO", getValue("CASE_NO_2"));
			parmReason.addData("IPD_NO", getValue("IPD_NO_2"));
			parmReason.addData("MR_NO", getValue("MR_NO_2"));
			if (getValueString("INF_NO_2").length() > 0
					&& getTable("TABLE_1_2").getSelectedRow() >= 0)
				parmReason.addData("INFCASE_SEQ", getTable("TABLE_1_2")
						.getValueAt(getTable("TABLE_1_2").getSelectedRow(), 1));
			else
				parmReason.addData("INFCASE_SEQ", getTable("TABLE_1_2")
						.getRowCount() + 1);
			parmReason.addData("INFREASON_NOTE",
					parm.getData("INFREASON_NOTE", i));
			parmReason.addData("OPT_USER", Operator.getID());
			parmReason.addData("OPT_DATE", SystemTool.getInstance().getDate());
			parmReason.addData("OPT_TERM", Operator.getIP());
		}
		clearTParmNull(parmReason);
		return parmReason;
	}

	// machao 20170421 add
	/**
	 * ���ø�Ⱦ��λ��������
	 * 
	 * @return TParm
	 */
	private TParm setInfICDPart() {
		TParm parm = getTable("TABLE_5_2").getParmValue();
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("MAIN_FLG"); i++) {
			if (StringUtil.isNullString(parm.getValue("PART_CODE", i))
					&& StringUtil.isNullString(parm.getValue("ICD_DESC", i))
					&& StringUtil.isNullString(parm.getValue("INF_SYSTEMCODE", i))) {
				continue;
			}
			result.addData("INF_NO", this.getValue("INF_NO_2"));
			if (getValueString("INF_NO_2").length() > 0
					&& getTable("TABLE_1_2").getSelectedRow() >= 0) {
				result.addData(
						"INFCASE_SEQ",
						getTable("TABLE_1_2").getValueAt(
								getTable("TABLE_1_2").getSelectedRow(), 1));
			} else {
				result.addData("INFCASE_SEQ", getTable("TABLE_1_2")
						.getRowCount() + 1);
			}
			result.addData("SEQ", parm.getData("SEQ", i));
			result.addData("CASE_NO", this.getValue("CASE_NO_2"));
			result.addData("IPD_NO", this.getValue("IPD_NO_2"));
			result.addData("MR_NO", this.getValue("MR_NO_2"));
			if (parm.getData("MAIN_FLG", i).equals("Y")) {
				result.addData("MAIN_FLG", "Y");
			} else {
				result.addData("MAIN_FLG", "N");
			}
			result.addData("PART_CODE", parm.getData("PART_CODE", i));
			result.addData("INF_SYSTEMCODE", parm.getData("INF_SYSTEMCODE", i));
			result.addData("ICD_CODE", parm.getData("ICD_CODE", i));
			result.addData("UM_CODE", parm.getData("UM_CODE", i));
			result.addData("OPT_USER", Operator.getID());
			result.addData("OPT_DATE", SystemTool.getInstance().getDate());
			result.addData("OPT_TERM", Operator.getIP());
		}
		clearTParmNull(result);
		return result;
	}

	/**
	 * ���������Բ�����������
	 * 
	 * @return TParm
	 */
	private TParm setInIO() {
		TParm parm = getTable("TABLE_6_2").getParmValue();
		TParm result = new TParm();
		for (int i = 0; i < getTable("TABLE_6_2").getRowCount(); i++) {
			if (parm.getData("SEL_FLG", i).equals("N"))
				continue;
			result.addData("INF_NO", this.getValue("INF_NO_2"));
			if (getValueString("INF_NO_2").length() > 0
					&& getTable("TABLE_1_2").getSelectedRow() >= 0) {
				result.addData(
						"INFCASE_SEQ",
						getTable("TABLE_1_2").getValueAt(
								getTable("TABLE_1_2").getSelectedRow(), 1));
			} else {
				result.addData("INFCASE_SEQ", getTable("TABLE_1_2")
						.getRowCount() + 1);
			}
			result.addData("CASE_NO", this.getValue("CASE_NO_2"));
			result.addData("IPD_NO", this.getValue("IPD_NO_2"));
			result.addData("MR_NO", this.getValue("MR_NO_2"));
			result.addData("IO_CODE", parm.getData("IO_CODE", i));
			result.addData("IO_NOTE", parm.getData("IO_NOTE", i));
			result.addData("OPT_USER", Operator.getID());
			result.addData("OPT_DATE", SystemTool.getInstance().getDate());
			result.addData("OPT_TERM", Operator.getIP());
		}
		clearTParmNull(result);
		return result;
	}

	// machao 20170421 add end
	/**
	 * ���ü���������
	 * 
	 * @return TParm
	 */
	private TParm setInfAntibioTest() {
		TParm infAntibioTest = new TParm();
		TParm parm = getTable("TABLE_4_2").getParmValue();
		for (int i = 0; i < getTable("TABLE_4_2").getRowCount(); i++) {
			// if(parm.getData("DEL_FLG",i).equals("Y"))
			// continue;
			// if(!parm.getData("DEL_FLG",i).equals("Y") &&
			// (parm.getValue("PATHOGEN_CODE",i).length() == 0 ||
			// parm.getValue("EXM_PHA",i).length() == 0 ||
			// parm.getValue("RESULT",i).length() == 0))
			// continue;
			infAntibioTest.addData("INF_NO", getValue("INF_NO_2"));
			if (getValueString("INF_NO_2").length() > 0
					&& getTable("TABLE_1_2").getSelectedRow() >= 0)
				infAntibioTest.addData("INFCASE_SEQ", getTable("TABLE_1_2")
						.getValueAt(getTable("TABLE_1_2").getSelectedRow(), 1));
			else
				infAntibioTest.addData("INFCASE_SEQ", getTable("TABLE_1_2")
						.getRowCount() + 1);
			infAntibioTest.addData("HOSP_AREA", "HIS");
			infAntibioTest.addData("CULURE_CODE",
					parm.getData("CULTURE_CODE", i));
			infAntibioTest.addData("ANTI_CODE", parm.getData("ANTI_CODE", i));
			infAntibioTest.addData("SENS_LEVEL", parm.getData("SENS_LEVEL", i));
			infAntibioTest.addData("CASE_NO", getValue("CASE_NO_2"));

			infAntibioTest.addData("INFECTLEVEL", "");
			infAntibioTest.addData("GRAMSTAIN", "");
			infAntibioTest.addData("COLONYCOUNT", "");
			infAntibioTest.addData("CANCEL_FLG", "N");

			infAntibioTest.addData("OPT_USER", Operator.getID());
			infAntibioTest.addData("OPT_DATE", SystemTool.getInstance()
					.getDate());
			infAntibioTest.addData("OPT_TERM", Operator.getIP());
		}
		clearTParmNull(infAntibioTest);
		return infAntibioTest;
	}

	/**
	 * ����TParm�еĿ�ֵ(����)
	 * 
	 * @param parm
	 *            TParm
	 */
	private void clearTParmNullSingle(TParm parm) {
		String names[] = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			if (parm.getData(names[i]) != null)
				continue;
			parm.setData(names[i], "");
		}
	}

	/**
	 * ����TParm�еĿ�ֵ(����)
	 * 
	 * @param parm
	 *            TParm
	 */
	private void clearTParmNull(TParm parm) {
		String names[] = parm.getNames();
		for (int i = 0; i < names.length; i++) {
			for (int j = 0; j < parm.getCount(names[i]); j++) {
				if (parm.getData(names[i], j) != null)
					continue;
				parm.setData(names[i], j, "");
			}
		}
	}

	/**
	 * ȡ�ò���סԺ������Ϣ
	 * 
	 * @param caseNo
	 *            String
	 * @return TParm
	 */
	private TParm getAdmInpInf(String caseNo) {
		String SQL = "SELECT STATION_CODE,BED_NO,VS_DR_CODE FROM ADM_INP WHERE CASE_NO = '"
				+ caseNo + "'";
		TParm parm = new TParm(getDBTool().select(SQL));
		if (parm.getCount() <= 0)
			return null;
		return parm;
	}

	/**
	 * ��������
	 */
	public void onNewAction() {
		setValue("INF_NO_2", "");
		setValue("REGISTER_DATE_2", "");
		setValue("INF_DR_2", "");
		// סԺ���������

		setValue("INF_DATE_2", "");
		setValue("INFPOSITION_CODE_2", "");
		setValue("INFPOSITION_DTL_2", "");
		setValue("IN_DIAG1_2", "");
		setValue("IN_DIAG2_2", "");
		setValue("OUT_DIAG1_2", "");
		setValue("OUT_DIAG2_2", "");
		setValue("OUT_DIAG3_2", "");
		setValue("INF_DIAG1_2", "");
		setValue("INF_DIAG2_2", "");
		setValue("DIEINFLU_CODE_2", "");
		setValue("INFRETN_CODE_2", "");
		setValue("OP_CODE_2", "");
		setValue("OP_DATE_2", "");
		setValue("OPCUT_TYPE_2", "");
		setValue("ANA_TYPE_2", "");
		setValue("ANA_TYPE_2", "");
		setValue("URGTOP_FLG_A_2", "N");
		setValue("URGTOP_FLG_B_2", "Y");
		setValue("OP_TIME_HH_2", "");
		setValue("OP_TIME_MM_2", "");
		setValue("OP_DR_2", "");
		setValue("INICU_DATE_2", "");
		setValue("OUTICU_DATE_2", "");
		setValue("ETIOLGEXM_FLG_Y_2", "N");
		setValue("ETIOLGEXM_FLG_N_2", "Y");
		onEtiolgexmFlgNTwo();
		setValue("SPECIMEN_CODE_2", "");
		setValue("EXAM_DATE_2", "");
		setValue("LABWAY_1_2", "N");
		setValue("LABWAY_2_2", "N");
		setValue("LABWAY_3_2", "N");
		setValue("LABPOSITIVE_A_2", "N");
		setValue("LABPOSITIVE_B_2", "Y");
		setValue("PATHOGEN1_CODE_2", "");
		setValue("PATHOGEN2_CODE_2", "");
		setValue("PATHOGEN3_CODE_2", "");
		setValue("ANTIBIOTEST_FLG_Y_2", "N");
		setValue("ANTIBIOTEST_FLG_N_2", "Y");
		setValue("OPB_OPSEQ_2", "");
		setValue("INF_SOURCE", "");
		setValue("INF_AREA", "");
		// �пؼ�¼����ʼ��
		getTable("TABLE_1_2")
				.setParmValue(getTable("TABLE_1_2").getParmValue());
		// ��Ⱦԭ�����ʼ��
		for (int i = 0; i < getTable("TABLE_2_2").getRowCount(); i++) {
			getTable("TABLE_2_2").setValueAt("N", i, 0);
			getTable("TABLE_2_2").getParmValue().setData("SEL_FLG", i, "N");
			getTable("TABLE_2_2").setValueAt("", i, 2);
			getTable("TABLE_2_2").getParmValue().setData("INFREASON_NOTE", i,
					"");
		}
		// ����������ʼ��
		getTable("TABLE_4_2").removeRowAll();

		// start add by machao 20170510
		TParm parm = getTable("TABLE_5_2").getParmValue();
		int num = parm.getCount("MAIN_FLG");
		for (int i = 0; i < num - 1; i++) {
			if (num == 1) {
				return;
			}
			getTable("TABLE_5_2").removeRow(0);
		}
		// end by machao 20170510
		// �����Բ�������ʼ��
		for (int i = 0; i < getTable("TABLE_6_2").getRowCount(); i++) {
			getTable("TABLE_6_2").setValueAt("N", i, 0);
			getTable("TABLE_6_2").getParmValue().setData("SEL_FLG", i, "N");
			getTable("TABLE_6_2").setValueAt("", i, 2);
			getTable("TABLE_6_2").getParmValue().setData("IO_NOTE", i, "");
		}
		// add end

		TParm parmDiag = new TParm();
		// ��Ժ���1
		inDiag1 = new TParm();
		// ��Ժ���2
		inDiag2 = new TParm();
		// ��Ժ���1
		outDiag1 = new TParm();
		// ��Ժ���2
		outDiag2 = new TParm();
		// ��Ժ���3
		outDiag3 = new TParm();
		// �п����2
		infDiag1 = new TParm();
		// �п����3
		infDiag2 = new TParm();
		parmDiag.setData("CASE_NO", getValue("CASE_NO_2"));
		parmDiag = INFCaseTool.getInstance().caseRegisterDiag(parmDiag);
		setAdmDiagInf(parmDiag);
	}

	/**
	 * ���ĸ�ҳǩ��񵥻��¼�
	 */
	public void onTableOneAndFour() {
		int row = getTable("TABLE_1_4").getSelectedRow();
		TParm tableParm = getTable("TABLE_1_4").getParmValue();
		setValue("DEPT_4", tableParm.getData("DEPT_CODE", row));
		setValue("STATION_4", tableParm.getData("STATION_CODE", row));
		setValue("IPD_NO_4", tableParm.getData("IPD_NO", row));
		setValue("MR_NO_4", tableParm.getData("MR_NO", row));
		setValue("PAT_NAME_4", tableParm.getData("PAT_NAME", row));
		setValue("REPORT_DATE_4", tableParm.getData("REPORT_DATE", row));
		setValue("REPORT_NO_4", tableParm.getData("REPORT_NO", row));
	}

	/**
	 * �ڶ���ҳǩ�пؼ�¼��񵥻��¼�
	 */
	public void onTableOneAndTwo() {
		int row = getTable("TABLE_1_2").getSelectedRow();
		TParm parmTable = getTable("TABLE_1_2").getParmValue();
		// �����пؼ�¼��Ϣ
		TParm parmCase = new TParm();
		parmCase.setData("CASE_NO", getValue("CASE_NO_2"));
		parmCase.setData("INF_NO", parmTable.getData("INF_NO", row));
		parmCase.setData("INFCASE_SEQ", parmTable.getData("INFCASE_SEQ", row));
		parmCase = INFCaseTool.getInstance().caseRegisterCase(parmCase);
		int seqNum = parmCase.getCount() - 1;
		String infNo = parmCase.getCount() <= 0 ? "" : parmCase.getValue(
				"INF_NO", seqNum);
		setTable2And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
		setTable3And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
		setTable4And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
		setTable5And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
		setTable6And2ByCaseInfNo(getValueString("CASE_NO_2"), infNo);
		if (parmCase.getCount() <= 0)
			return;
		clearTParmNull(parmCase);
		setValue("INF_NO_2", parmCase.getData("INF_NO", seqNum));
		setValue("REGISTER_DATE_2", parmCase.getData("REGISTER_DATE", seqNum));
		setValue("INF_DR_2", parmCase.getData("INF_DR", seqNum));
		// סԺ�����ɸ���
		setValue("ADM_DAYS_2", parmCase.getData("ADM_DAYS", seqNum));
		// �����Ϣ�����޸�
		setValue("IN_DIAG1_2", parmCase.getData("IN_DIAG1_DESC", seqNum));
		setValue("IN_DIAG2_2", parmCase.getData("IN_DIAG2_DESC", seqNum));
		setValue("OUT_DIAG1_2", parmCase.getData("OUT_DIAG1_DESC", seqNum));
		setValue("OUT_DIAG2_2", parmCase.getData("OUT_DIAG2_DESC", seqNum));
		setValue("OUT_DIAG3_2", parmCase.getData("OUT_DIAG3_DESC", seqNum));
		setInfDiagInf(parmCase, seqNum);
		setValue("INF_DATE_2", parmCase.getData("INF_DATE", seqNum));
		setValue("INFPOSITION_CODE_2",
				parmCase.getData("INFPOSITION_CODE", seqNum));
		setValue("INFPOSITION_DTL_2",
				parmCase.getData("INFPOSITION_DTL", seqNum));
		setValue("INF_DIAG1_2", parmCase.getData("INF_DIAG1_DESC", seqNum));
		setValue("INF_DIAG2_2", parmCase.getData("INF_DIAG2_DESC", seqNum));
		setValue("DIEINFLU_CODE_2", parmCase.getData("DIEINFLU_CODE", seqNum));
		setValue("INFRETN_CODE_2", parmCase.getData("INFRETN_CODE", seqNum));
		setValue("OP_CODE_2", parmCase.getData("OPT_CHN_DESC", seqNum));
		setValue("OP_DATE_2", parmCase.getData("OP_DATE", seqNum));
		setValue("OPCUT_TYPE_2", parmCase.getData("OPCUT_TYPE", seqNum));
		setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));
		setValue("ANA_TYPE_2", parmCase.getData("ANA_TYPE", seqNum));
		setValue("OPB_OPSEQ_2", parmCase.getData("OPBOOK_SEQ", seqNum));
		setValue("INF_AREA", parmCase.getData("INF_AREA", seqNum));
		setValue("INF_SOURCE", parmCase.getData("INF_SOURCE", seqNum));
		if (parmCase.getValue("URGTOP_FLG", seqNum).equals("Y"))
			setValue("URGTOP_FLG_A_2", "Y");
		else if (parmCase.getValue("URGTOP_FLG", seqNum).equals("N"))
			setValue("URGTOP_FLG_B_2", "Y");

		if (parmCase.getValue("OP_TIME", seqNum).length() >= 4) {
			setValue("OP_TIME_HH_2", parmCase.getValue("OP_TIME", seqNum)
					.substring(0, 2));
			setValue("OP_TIME_MM_2", parmCase.getValue("OP_TIME", seqNum)
					.substring(2, 4));
		}

		setValue("OP_DR_2", parmCase.getData("OP_DR", seqNum));
		setValue("INICU_DATE_2", parmCase.getData("INICU_DATE", seqNum));
		setValue("OUTICU_DATE_2", parmCase.getData("OUTICU_DATE", seqNum));

		if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("Y"))
			setValue("ETIOLGEXM_FLG_Y_2", "Y");
		else if (parmCase.getValue("ETIOLGEXM_FLG", seqNum).equals("N"))
			setValue("ETIOLGEXM_FLG_N_2", "Y");

		setValue("SPECIMEN_CODE_2", parmCase.getData("SPECIMEN_CODE", seqNum));
		setValue("EXAM_DATE_2", parmCase.getData("EXAM_DATE", seqNum));

		if (parmCase.getValue("LABWAY", seqNum).equals("1"))
			setValue("LABWAY_1_2", "Y");
		if (parmCase.getValue("LABWAY", seqNum).equals("2"))
			setValue("LABWAY_2_2", "Y");
		if (parmCase.getValue("LABWAY", seqNum).equals("3"))
			setValue("LABWAY_3_2", "Y");

		if (parmCase.getValue("LABPOSITIVE", seqNum).equals("Y"))
			setValue("LABPOSITIVE_A_2", "Y");
		if (parmCase.getValue("LABPOSITIVE", seqNum).equals("N"))
			setValue("LABPOSITIVE_B_2", "Y");

		setValue("PATHOGEN1_CODE_2", parmCase.getData("PATHOGEN1_CODE", seqNum));
		setValue("PATHOGEN2_CODE_2", parmCase.getData("PATHOGEN2_CODE", seqNum));
		setValue("PATHOGEN3_CODE_2", parmCase.getData("PATHOGEN3_CODE", seqNum));

		if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("Y"))
			setValue("ANTIBIOTEST_FLG_Y_2", "Y");
		if (parmCase.getValue("ANTIBIOTEST_FLG", seqNum).equals("N"))
			setValue("ANTIBIOTEST_FLG_N_2", "Y");
		TParm parm = new TParm();
		parm.addData("CASE_NO", getValueString("CASE_NO_2"));
		getAntiTestInfo(parm);
	}

	// add machao start
	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void diaPopRetrun1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			getTextField("DIACODE_1").setValue(icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("DIACODE_DESC_1").setValue(icdDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void orderCodeReturn1(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(orderCode))
			getTextField("ORDER_CODE_1").setValue(orderCode);
		String orderDesc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(orderDesc)) {
			getTextField("ORDER_DESC_1").setValue(orderDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void orderCodeReturn5(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String orderCode = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(orderCode))
			getTextField("ORDER_CODE_5").setValue(orderCode);
		String orderDesc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(orderDesc)) {
			getTextField("ORDER_DESC_5").setValue(orderDesc);
		}
	}
	// add machao end
	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void inDiag1And2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			inDiag1.setData("ICD_CODE", icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("IN_DIAG1_2").setValue(icdDesc);
			inDiag1.setData("ICD_CHN_DESC", icdDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void inDiag2And2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			inDiag2.setData("ICD_CODE", icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("IN_DIAG2_2").setValue(icdDesc);
			inDiag2.setData("ICD_CHN_DESC", icdDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void outDiag3And2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			outDiag3.setData("ICD_CODE", icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("OUT_DIAG3_2").setValue(icdDesc);
			outDiag3.setData("ICD_CHN_DESC", icdDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void outDiag2And2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			outDiag2.setData("ICD_CODE", icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("OUT_DIAG2_2").setValue(icdDesc);
			outDiag2.setData("ICD_CHN_DESC", icdDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void outDiag1And2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			outDiag1.setData("ICD_CODE", icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("OUT_DIAG1_2").setValue(icdDesc);
			outDiag1.setData("ICD_CHN_DESC", icdDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void infDiag1And2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			infDiag1.setData("ICD_CODE", icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("INF_DIAG1_2").setValue(icdDesc);
			infDiag1.setData("ICD_CHN_DESC", icdDesc);
		}
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void infDiag2And2(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String icdCode = parm.getValue("ICD_CODE");
		if (!StringUtil.isNullString(icdCode))
			infDiag2.setData("ICD_CODE", icdCode);
		String icdDesc = parm.getValue("ICD_CHN_DESC");
		if (!StringUtil.isNullString(icdDesc)) {
			getTextField("INF_DIAG2_2").setValue(icdDesc);
			infDiag2.setData("ICD_CHN_DESC", icdDesc);
		}
	}

	/**
	 * ��ҳǩ�л��¼�
	 */
	public void onTabPanel() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 0:
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(false);
			((TMenuItem) getComponent("query")).setEnabled(true);
			((TMenuItem) getComponent("clear")).setEnabled(true);
			((TMenuItem) getComponent("close")).setEnabled(true);
			((TMenuItem) getComponent("print")).setEnabled(false);
			((TMenuItem) getComponent("report")).setEnabled(false);
			((TMenuItem) getComponent("temperature")).setEnabled(false);
			((TMenuItem) getComponent("showcase")).setEnabled(false);
			((TMenuItem) getComponent("export")).setEnabled(true);
			((TMenuItem) getComponent("communicate")).setEnabled(true);// add by
																		// lij
																		// 20170417
			((TMenuItem) getComponent("consultation")).setEnabled(true);// add
																		// by
																		// machao
																		// 20170424
			((TMenuItem) getComponent("testrep")).setEnabled(false);
			taddedPaneNo = index;
			break;
		case 1:
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			((TMenuItem) getComponent("query")).setEnabled(true);
			((TMenuItem) getComponent("clear")).setEnabled(true);
			((TMenuItem) getComponent("close")).setEnabled(true);
			((TMenuItem) getComponent("print")).setEnabled(true);
			((TMenuItem) getComponent("report")).setEnabled(true);
			((TMenuItem) getComponent("temperature")).setEnabled(true);
			((TMenuItem) getComponent("showcase")).setEnabled(true);
			((TMenuItem) getComponent("export")).setEnabled(false);
			((TMenuItem) getComponent("communicate")).setEnabled(false);// add
																		// by
																		// lij
																		// 20170417
			((TMenuItem) getComponent("consultation")).setEnabled(false);// add
																			// by
																			// machao
																			// 20170424
			((TMenuItem) getComponent("testrep")).setEnabled(true);
			onTabPanelTwo();
			taddedPaneNo = index;
			break;
		case 2:
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TMenuItem) getComponent("delete")).setEnabled(false);
			((TMenuItem) getComponent("query")).setEnabled(true);
			((TMenuItem) getComponent("clear")).setEnabled(true);
			((TMenuItem) getComponent("close")).setEnabled(true);
			((TMenuItem) getComponent("print")).setEnabled(false);
			((TMenuItem) getComponent("report")).setEnabled(false);
			((TMenuItem) getComponent("temperature")).setEnabled(false);
			((TMenuItem) getComponent("showcase")).setEnabled(false);
			((TMenuItem) getComponent("export")).setEnabled(true);
			((TMenuItem) getComponent("communicate")).setEnabled(false);// add
																		// by
																		// lij
																		// 20170417
			((TMenuItem) getComponent("consultation")).setEnabled(false);// add
																			// by
																			// machao
																			// 20170424
			((TMenuItem) getComponent("testrep")).setEnabled(false);
			onTabPanelThree();
			taddedPaneNo = index;
			break;
		case 3:
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TMenuItem) getComponent("delete")).setEnabled(false);
			((TMenuItem) getComponent("query")).setEnabled(true);
			((TMenuItem) getComponent("clear")).setEnabled(true);
			((TMenuItem) getComponent("close")).setEnabled(true);
			((TMenuItem) getComponent("print")).setEnabled(false);
			((TMenuItem) getComponent("report")).setEnabled(true);
			((TMenuItem) getComponent("temperature")).setEnabled(false);
			((TMenuItem) getComponent("showcase")).setEnabled(false);
			((TMenuItem) getComponent("export")).setEnabled(true);
			((TMenuItem) getComponent("communicate")).setEnabled(true);// add by
																		// lij
																		// 20170417
			((TMenuItem) getComponent("consultation")).setEnabled(false);// add
																			// by
																			// machao
																			// 20170424
			((TMenuItem) getComponent("testrep")).setEnabled(true);
			taddedPaneNo = index;
			break;
		case 4:// add by liuyalin 20170522
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(false);
			((TMenuItem) getComponent("query")).setEnabled(true);
			((TMenuItem) getComponent("clear")).setEnabled(true);
			((TMenuItem) getComponent("close")).setEnabled(true);
			((TMenuItem) getComponent("print")).setEnabled(false);
			((TMenuItem) getComponent("report")).setEnabled(false);
			((TMenuItem) getComponent("temperature")).setEnabled(false);
			((TMenuItem) getComponent("showcase")).setEnabled(false);
			((TMenuItem) getComponent("export")).setEnabled(false);
			((TMenuItem) getComponent("communicate")).setEnabled(false);// add
																		// by
																		// lij
																		// 20170417
			((TMenuItem) getComponent("consultation")).setEnabled(false);// add
																			// by
																			// machao
																			// 20170424
			((TMenuItem) getComponent("testrep")).setEnabled(true);
			onTabPanelFive();
			taddedPaneNo = index;
			break;
		}
	}

	/**
	 * �ڶ���ҳǩ�¼�
	 */
	private void onTabPanelTwo() {
		setValue("INF_SYSTEMCODE11", "");
		setValue("PART_CODE11", "");
		
		TTextFormat com = (TTextFormat)getComponent("PART_CODE11");
		com.setPopupMenuSQL("SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID='INF_INFPOSITION' ORDER BY SEQ");
		com.onQuery();
		switch (taddedPaneNo) {
		case 0:
			onClearTwo();
			int row0 = getTable("TABLE_1").getSelectedRow();
			if (row0 < 0)
				return;
			TParm parmTable0 = getTable("TABLE_1").getParmValue();
			setValue("MR_NO_2", parmTable0.getData("MR_NO", row0));
			break;
		case 3:
			onClearTwo();
			int row4 = getTable("TABLE_1_4").getSelectedRow();
			if (row4 < 0)
				return;
			TParm parmTable4 = getTable("TABLE_1_4").getParmValue();
			setValue("MR_NO_2", parmTable4.getData("MR_NO", row4));
			break;
		}
		onQueryTwo();
	}

	/**
	 * ������ҳǩ�¼�
	 */
	private void onTabPanelThree() {
		int row = getTable("TABLE_1").getSelectedRow();
		if (row < 0)
			return;
		TParm parmTable = getTable("TABLE_1").getParmValue();
		setValue("START_DATE_3", getValue("START_DATE_1"));
		setValue("END_DATE_3", getValue("END_DATE_1"));
		setValue("MR_NO_3", parmTable.getData("MR_NO", row));
		setValue("IPD_NO_3", parmTable.getData("IPD_NO", row));
		setValue("DEPT_3", parmTable.getData("DEPT_CODE", row));
		setValue("STATION_3", parmTable.getData("STATION_CODE", row));
		setValue("DR_3", parmTable.getData("VS_DR", row));
		setValue("PAT_NAME_3", parmTable.getData("PAT_NAME", row));
		setValue("AGE_3",
				StringTool.CountAgeByTimestamp(
						(Timestamp) parmTable.getData("BIRTH_DATE", row),
						SystemTool.getInstance().getDate())[0]);
		onQueryThree();
	}

	// liuyalin 20170522 add
	/**
	 * �����ҳǩ�¼�
	 */
	private void onTabPanelFive() {
		int row = getTable("TABLE_1").getSelectedRow();
		if (row < 0)
			return;
		TParm parmTable = getTable("TABLE_1").getParmValue();
		setValue("START_DATE_5", getValue("START_DATE_1"));
		setValue("END_DATE_5", getValue("END_DATE_1"));
		setValue("ORDER_CODE_5", getValue("ORDER_CODE_1"));
		setValue("ORDER_DESC_5", getValue("ORDER_DESC_1"));
		setValue("MR_NO_5", parmTable.getData("MR_NO", row));
		setValue("IPD_NO_5", parmTable.getData("IPD_NO", row));
		setValue("DEPT_5", parmTable.getData("DEPT_CODE", row));
		setValue("STATION_5", parmTable.getData("STATION_CODE", row));
		setValue("DR_5", parmTable.getData("VS_DR", row));
		setValue("PAT_NAME_5", parmTable.getData("PAT_NAME", row));
		setValue("AGE_5",
				StringTool.CountAgeByTimestamp(
						(Timestamp) parmTable.getData("BIRTH_DATE", row),
						SystemTool.getInstance().getDate())[0]);
		onQueryFive();
	}

	// liuyalin 20170522 add end
	/**
	 * ɾ������
	 */
	public void onDelete() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 1:
			onDeleteTwo();
			break;

		}
	}

	/**
	 * �ڶ���ҳǩɾ������
	 */
	private void onDeleteTwo() {
		if (getTable("TABLE_1_2").getSelectedRow() < 0) {
			messageBox("��ɾ����¼");
			return;
		}
		TParm parm = new TParm();
		parm.setData("INF_NO", getValue("INF_NO_2"));
		parm.setData("CASE_NO", getValue("CASE_NO_2"));
		parm.setData(
				"INFCASE_SEQ",
				getTable("TABLE_1_2").getValueAt(
						getTable("TABLE_1_2").getSelectedRow(), 1));
		parm = TIOM_AppServer.executeAction("action.inf.InfAction",
				"deleteInfCase", parm);
		if (parm.getErrCode() < 0) {
			messageBox("����ʧ��");
			return;
		}
		messageBox("����ɹ�");
		String mrNo = getValueString("MR_NO_2");
		onClearTwo();
		setValue("MR_NO_2", mrNo);
		onQueryTwo();
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 0:
			onClearOne();
			break;
		case 1:
			onClearTwo();
			break;
		case 2:
			onClearThree();
			break;
		case 3:
			onClearFour();
			break;
		case 4: // liuyalin 20170522 add
			onClearFive();
			break;
		}
	}

	/**
	 * ��һ��ҳǩ��շ���
	 */
	private void onClearOne() {
		onInitPanelOne();
		setValue("START_TEMP_1", 38);
		setValue("END_TEMP_1", 42);
		setValue("ANTIBIOTRCD_FLG_1", "N");
		setValue("ANTIBIOTIC_CODE_1", "");
		((TComboBox) getComponent("ANTIBIOTIC_CODE_1")).setEnabled(false);
		setValue("WBC_FLG_1", "N");
		setValue("GRDDLE_FLG_1", "AND");
		setValue("CHECKOUT_FLG_1", "AND");
		setValue("CHECKUP_FLG_1", "AND");
		setValue("PROTOCOL_FLG_1", "AND");
		setValue("ANTIBIOTIC_FLG_1", "AND");
		setValue("INVADE_FLG_1", "AND");
		setValue("PROGRESS_FLG_1", "AND");
		setValue("OPE_FLG", "AND");
		setValue("PROGRESS_FLG_1", "AND");
		setValue("OP_F", "N");
		setValue("OPB_DAYS", "");
		
		((TCheckBox) getComponent("TEMPERATURE_1")).setSelected(false);
		((TCheckBox) getComponent("DRESS_F_1")).setSelected(false);
		((TCheckBox) getComponent("PROTOCOL_F_1")).setSelected(false);
		((TCheckBox) getComponent("INVADE_F_1")).setSelected(false);
		((TCheckBox) getComponent("ANTIBIOTIC_F_1")).setSelected(false);
		((TCheckBox) getComponent("PROGRESS_F_1")).setSelected(false);
		((TTextField) getComponent("DIACODE_1")).setEnabled(false);
		((TTextField) getComponent("ORDER_CODE_1")).setEnabled(false);
		((TTextField) getComponent("OPB_DAYS")).setEnabled(false);
		setValue("CHECKOUT_VALUE_1", "");
		setValue("CHECKUP_VALUE_1", "");
		setValue("ANTIBIOTIC_CODE_1", "");
		setValue("PROGRESS_DESC_1", "");
		setValue("PROGRESS_CODE_1", "");
		setValue("DIACODE_1", "");
		setValue("DIACODE_DESC_1", "");
		setValue("ORDER_CODE_1", "");
		setValue("ORDER_DESC_1", "");

		getTable("TABLE_1").removeRowAll();
	}

	/**
	 * �ڶ���ҳǩ��շ���
	 */
	private void onClearTwo() {
		setValue("INF_NO_2", "");
		setValue("REGISTER_DATE_2", "");
		setValue("INF_DR_2", "");
		setValue("MR_NO_2", "");
		setValue("IPD_NO_2", "");
		setValue("CASE_NO_2", "");
		setValue("DEPT_CODE_2", "");
		setValue("PAT_NAME_2", "");
		setValue("AGE_2", "");
		setValue("SEX_2", "");
		setValue("ADM_DAYS_2", "");
		setValue("IN_DATE_2", "");
		setValue("IN_DIAG1_2", "");
		setValue("IN_DIAG2_2", "");
		setValue("CHARGE_FEE_2", "");
		setValue("OUT_DATE_2", "");
		setValue("OUT_DIAG1_2", "");
		setValue("OUT_DIAG2_2", "");
		setValue("OUT_DIAG3_2", "");
		setValue("INF_DATE_2", "");
		setValue("INF_SOURCE","");
		setValue("INF_AREA","");
		setValue("INFPOSITION_CODE_2", "");
		setValue("INFPOSITION_DTL_2", "");
		setValue("INF_DIAG1_2", "");
		setValue("INF_DIAG2_2", "");
		setValue("DIEINFLU_CODE_2", "");
		setValue("INFRETN_CODE_2", "");
		setValue("OP_CODE_2", "");
		setValue("OP_DATE_2", "");
		setValue("OPCUT_TYPE_2", "");
		setValue("ANA_TYPE_2", "");
		setValue("URGTOP_FLG_A_2", "N");
		setValue("URGTOP_FLG_B_2", "Y");
		setValue("OP_TIME_HH_2", "");
		setValue("OP_TIME_MM_2", "");
		setValue("OP_DR_2", "");
		setValue("INICU_DATE_2", "");
		setValue("OUTICU_DATE_2", "");
		setValue("ETIOLGEXM_FLG_Y_2", "N");
		setValue("ETIOLGEXM_FLG_N_2", "Y");
		setValue("SPECIMEN_CODE_2", "");
		setValue("EXAM_DATE_2", "");
		setValue("LABWAY_1_2", "N");
		setValue("LABWAY_2_2", "N");
		setValue("LABWAY_3_2", "N");
		setValue("LABPOSITIVE_A_2", "N");
		setValue("LABPOSITIVE_B_2", "Y");
		setValue("PATHOGEN1_CODE_2", "");
		setValue("PATHOGEN2_CODE_2", "");
		setValue("PATHOGEN3_CODE_2", "");
		setValue("ANTIBIOTEST_FLG_Y_2", "N");
		setValue("ANTIBIOTEST_FLG_N_2", "Y");
		setValue("CLINICAL_SYMP_2", "");
		setValue("OPB_OPSEQ_2", "");
		setValue("INF_SYSTEMCODE11", "");
		setValue("PART_CODE11", "");
		
		TTextFormat com = (TTextFormat)getComponent("PART_CODE11");
		com.setPopupMenuSQL("SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID='INF_INFPOSITION' ORDER BY SEQ");
		com.onQuery();
		
		getTable("TABLE_1_2").removeRowAll();
		getTable("TABLE_2_2").removeRowAll();
		getTable("TABLE_3_2").removeRowAll();
		getTable("TABLE_4_2").removeRowAll();
		getTable("TABLE_5_2").setDSValue();
		getTable("TABLE_6_2").setDSValue();
		
		((TButton) getComponent("ADD_BUTTON_2")).setEnabled(false);
		((TTextField) getComponent("MR_NO_2")).setEnabled(true);
		// ��Ժ���1
		inDiag1 = new TParm();
		// ��Ժ���2
		inDiag2 = new TParm();
		// ��Ժ���1
		outDiag1 = new TParm();
		// ��Ժ���2
		outDiag2 = new TParm();
		// ��Ժ���3
		outDiag3 = new TParm();
		// �п����2
		infDiag1 = new TParm();
		// �п����3
		infDiag2 = new TParm();
	}

	/**
	 * ������ҳǩ����¼�
	 */
	private void onClearThree() {
		Timestamp timestamp = SystemTool.getInstance().getDate();
		((TTextFormat) getComponent("START_DATE_3")).setValue(timestamp);
		((TTextFormat) getComponent("END_DATE_3")).setValue(timestamp);
		setValue("DEPT_3", "");
		setValue("STATION_3", "");
		setValue("DR_3", "");
		setValue("IPD_NO_3", "");
		setValue("MR_NO_3", "");
		setValue("PAT_NAME_3", "");
		setValue("AGE_3", "");
		setValue("ANTIBIOTIC_CODE_3", "");
		getTable("TABLE_1_3").removeRowAll();
	}

	/**
	 * ���ĸ�ҳǩ��ն���
	 */
	private void onClearFour() {
		setValue("REP_ALL_4", "Y");
		setValue("REP_Y_4", "N");
		setValue("REP_N_4", "N");
		setValue("IN_ALL_4", "N");
		setValue("IN_Y_4", "N");
		setValue("IN_N_4", "N");
		Timestamp timestamp = SystemTool.getInstance().getDate();
		((TTextFormat) getComponent("START_DATE_4")).setValue(timestamp);
		((TTextFormat) getComponent("END_DATE_4")).setValue(timestamp);
		setValue("DEPT_4", "");
		setValue("STATION_4", "");
		setValue("IPD_NO_4", "");
		setValue("MR_NO_4", "");
		setValue("PAT_NAME_4", "");
		setValue("REPORT_DATE_4", "");
		setValue("REPORT_NO_4", "");
		getTable("TABLE_1_4").removeRowAll();
	}

	// liuyalin 20170421 add
	/**
	 * �����ҳǩ����¼�
	 */
	private void onClearFive() {
		Timestamp timestamp = SystemTool.getInstance().getDate();
		((TTextFormat) getComponent("START_DATE_5")).setValue(timestamp);
		((TTextFormat) getComponent("END_DATE_5")).setValue(timestamp);
		setValue("DEPT_5", "");
		setValue("STATION_5", "");
		setValue("DR_5", "");
		setValue("IPD_NO_5", "");
		setValue("MR_NO_5", "");
		setValue("PAT_NAME_5", "");
		setValue("ORDER_DESC_5", "");
		setValue("ORDER_CODE_5", "");
		setValue("AGE_5", "");
		getTable("TABLE_1_5").removeRowAll();
	}

	// liuyalin 20170421 add end
	/**
	 * ҽԺ��Ⱦ�������濨
	 */
	public void onPrint() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();

		switch (index) {
		case 1:
			onPrintInfCaseCard();
			break;
		}
	}

	/**
	 * ��ӡ��Ⱦ�������濨
	 */
	private void onPrintInfCaseCard() {
		if (getTable("TABLE_1_2").getSelectedRow() < 0)
			return;
		TParm parm = new TParm();
		parm.setData("INF_NO", getValue("INF_NO_2"));
		parm.setData(
				"INFCASE_SEQ",
				getTable("TABLE_1_2").getValueAt(
						getTable("TABLE_1_2").getSelectedRow(), 1));
		parm.setData("CASE_NO", getValue("CASE_NO_2"));
		TParm infCaseCardParm = INFCaseTool.getInstance().selectInfCaseCardInf(
				parm);

		if (infCaseCardParm.getErrCode() < 0)
			return;
		if (infCaseCardParm.getCount() <= 0)
			return;
		String age = StringTool.CountAgeByTimestamp(
				(Timestamp) infCaseCardParm.getData("BIRTH_DATE", 0),
				SystemTool.getInstance().getDate())[0];

		// this.messageBox(infCaseCardParm+"");

		TParm resultInfIcdPart = new TParm();
		resultInfIcdPart = INFCaseTool.getInstance()
				.selectInfICDPartByCaseInfNo(parm);
		String infIcdPart = "";
		String icdDesc = "";// ��Ⱦ���
		for (int i = 0; i < resultInfIcdPart.getCount(); i++) {
			infIcdPart += "'" + resultInfIcdPart.getValue("PART_CODE", i)
					+ "',";
			icdDesc += resultInfIcdPart.getValue("ICD_DESC", i) + ";";
		}
		String infIcdPartDesc = "";// ��Ⱦ��λ

		if (!StringUtil.isNullString(infIcdPart)) {
			infIcdPart = infIcdPart.substring(0, infIcdPart.length() - 1);
			String sqlInfIcdPart = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'INF_INFPOSITION' AND ID IN("
					+ infIcdPart + ")";
			TParm resultInfIcd = new TParm(TJDODBTool.getInstance().select(
					sqlInfIcdPart));
			if (resultInfIcd != null) {
				for (int i = 0; i < resultInfIcd.getCount(); i++) {
					infIcdPartDesc += resultInfIcd.getValue("CHN_DESC", i)
							+ ";";
				}
			}
		}
		// this.messageBox(resultInfIcdPart+"");

		String operationDesc = "";// ��������
		if (!StringUtil.isNullString(infCaseCardParm.getValue("OP_CODE", 0))) {
			String operationSql = "SELECT OPT_CHN_DESC FROM SYS_OPERATIONICD WHERE OPERATION_ICD = '"
					+ infCaseCardParm.getValue("OP_CODE", 0) + "'";
			System.out.println("ssss::" + operationSql);
			operationDesc = new TParm(TJDODBTool.getInstance().select(
					operationSql)).getValue("OPT_CHN_DESC", 0);
		}
		// this.messageBox(operationDesc);

		TParm infReasonParm = INFCaseTool.getInstance().selectInfReasonForCard(
				parm);
		if (infReasonParm.getErrCode() < 0)
			return;
		System.out.println("aaaa:" + infReasonParm);
		TParm sysDicReason = new TParm(
				TJDODBTool
						.getInstance()
						.select("SELECT GROUP_ID,ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'INF_INFREASON'"));
		// this.messageBox(sysDicReason+"");
		String reason = "";
		for (int i = 0; i < infReasonParm.getCount(); i++)
			reason += (i + 1) + "." + infReasonParm.getValue("CHN_DESC", i)
					+ '\n';
		TParm printParm = new TParm();

		cloneTParm(infCaseCardParm, printParm, 0);
		System.out.println("11111:" + printParm);
		printParm.setData("CHECKOUTRESULT",
				infCaseCardParm.getBoolean("LABPOSITIVE", 0) ? "����" : "����");
		printParm.setData("OPT_CHN_DESC", !StringUtil
				.isNullString(infCaseCardParm.getValue("OPBOOK_SEQ", 0)) ? "��"
				: "��");

		printParm.setData("AGE", age);
		printParm.setData("INF_ICDDESC", infIcdPartDesc);
		printParm.setData("INF_DIAG", icdDesc);
		// printParm.setData("OPT_CHN_DESC",operationDesc);
		printParm.setData("ETIOLGEXM", printParm.getData("ETIOLGEXM_FLG")
				.equals("Y") ? "��" : "��");
		printParm.setData("IN_DIAG1", "TEXT", printParm.getData("IN_DIAG1"));
		printParm.setData("IN_DIAG2", "TEXT", printParm.getData("IN_DIAG2"));
		printParm.setData("INF_REASON", "TEXT", reason);
		// openPrintWindow("%ROOT%\\config\\prt\\inf\\INFCaseReport.jhw",
		// printParm);

		for (int i = 0; i < infReasonParm.getCount(); i++) {
			if ("11".equals(infReasonParm.getValue("INFREASON_CODE", i))) {// ����
				printParm.setData("GLYCURESIS", "��");
				continue;
			}
			if ("10".equals(infReasonParm.getValue("INFREASON_CODE", i))) {// ��Ӳ��
				printParm.setData("CIRRHOSIS", "��");
				continue;
			}
			if ("17".equals(infReasonParm.getValue("INFREASON_CODE", i))) {// ����
				printParm.setData("RADIOTHERAPY", "��");
				continue;
			}
			if ("16".equals(infReasonParm.getValue("INFREASON_CODE", i))) {// ����
				printParm.setData("CHEMOTHERAPY", "��");
				continue;
			}
			if ("14".equals(infReasonParm.getValue("INFREASON_CODE", i))) {// �������Ƽ�
				printParm.setData("IMMUNOSUP", "��");
				continue;
			}
			if ("02".equals(infReasonParm.getValue("INFREASON_CODE", i))) {// Ӫ������
				printParm.setData("INNUTRIT", "��");
				continue;
			}
			if ("23".equals(infReasonParm.getValue("INFREASON_CODE", i))) {// ����
				printParm.setData("OTHERITEM", "��");
				continue;
			}
		}

		String sql = " SELECT distinct ( "
				+ " A.CASE_NO),B.CULTURE_CODE,B.CULTURE_CHN_DESC,B.ANTI_CODE,B.ANTI_CHN_DESC,B.SENS_LEVEL, "
				+ " A.MR_NO,A.PAT_NAME,A.APPLICATION_NO "
				+ " FROM MED_APPLY A, MED_LIS_ANTITEST B,INF_ANTIBIOTEST C,INF_CASE D "
				+ " WHERE     A.CAT1_TYPE = B.CAT1_TYPE "
				+ " AND A.APPLICATION_NO = B.APPLICATION_NO "
				+ " AND C.INF_NO = D.INF_NO "
				+ " AND C.INFCASE_SEQ = D.INFCASE_SEQ " + " AND A.CASE_NO = '"
				+ getValueString("CASE_NO_2") + "' " + " AND A.MR_NO = '"
				+ getValueString("MR_NO_2") + "' ";
		TParm resultP = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println(sql);
		System.out.println("qqq:" + resultP);
		String serology = "";
		String serologyReault = "";
		for (int i = 0; i < resultP.getCount(); i++) {
			serology += resultP.getValue("ANTI_CHN_DESC", i) + ";";
			serologyReault += resultP.getValue("SENS_LEVEL", i) + ";";
		}

		printParm.setData("SEROLOGY", serology);
		printParm.setData("SEROLOGYRESULT", serologyReault);

		printParm.setData(
				"OPCUT_TYPE",
				new TParm(TJDODBTool.getInstance().select(
						"SELECT CHN_DESC " + "FROM SYS_DICTIONARY "
								+ "WHERE GROUP_ID='OPE_INCISION' "
								+ "AND ID = '"
								+ infCaseCardParm.getValue("OPCUT_TYPE", 0)
								+ "'")).getValue("CHN_DESC", 0));
		;
		openPrintWindow("%ROOT%\\config\\prt\\inf\\INFCaseReports.jhw",
				printParm);
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
			to.setData(names[i], obj);
		}
	}

	/**
	 * ���鱨��
	 */
	public void onReport() {
		String mrNo = "";
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();

		switch (index) {
		case 1:
			if (getValueString("MR_NO_2").length() == 0)
				return;
			else
				mrNo = getValueString("MR_NO_2");
			SystemTool.getInstance().OpenLisWeb(mrNo);
			break;
		case 3:
			if (getValueString("MR_NO_4").length() == 0)
				return;
			else
				mrNo = getValueString("MR_NO_4");
			SystemTool.getInstance().OpenLisWeb(mrNo);
			break;
		}
	}

	public TPanel getTPanel() {
		return (TPanel) this.getComponent("tPanel_6");
	}

	/**
	 * ����
	 */
	public void onConsultation() {
		// this.messageBox("1");
		TTable table_1 = ((TTable) getComponent("TABLE_1"));
		int num = table_1.getSelectedRow();
		if (num < 0) {
			this.messageBox("��ѡ��Ҫ�������");
			return;
		}
		TParm parm = table_1.getParmValue().getRow(num);
		TParm p = new TParm();
		p.setData("INF", "OIDR");
		p.setData("CASE_NO", parm.getValue("CASE_NO"));
		p.setData("MR_NO", parm.getValue("MR_NO"));

		String sql = "SELECT B.CLNCPATH_CODE, A.BED_NO_DESC, C.PAT_NAME, C.SEX_CODE, C.BIRTH_DATE, B.IN_DATE, B.DS_DATE, "
				+ "       D.ICD_CHN_DESC AS MAINDIAG, B.CTZ1_CODE, B.MR_NO, B.IPD_NO, B.TOTAL_AMT, B.TOTAL_BILPAY, "
				+ "       B.GREENPATH_VALUE, B.STATION_CODE, B.RED_SIGN, B.YELLOW_SIGN, B.STOP_BILL_FLG, A.BED_NO, "
				+ "       B.CTZ2_CODE, B.CTZ3_CODE, B.VS_DR_CODE, B.DEPT_CODE, B.HEIGHT, B.WEIGHT, B.CASE_NO, "
				+ "       B.CUR_AMT, C.POST_CODE, C.ADDRESS, C.COMPANY_DESC, C.CELL_PHONE, C.TEL_HOME, C.IDNO, C.PAT_NAME1, "
				+ "       B.NURSING_CLASS, B.PATIENT_STATUS, D.ICD_CODE, E.CHECK_FLG AS MRO_CHAT_FLG, A.ENG_DESC, "
				+ "       B.SERVICE_LEVEL, B.BILL_STATUS, B.DISE_CODE "
				+ "  FROM SYS_BED A, ADM_INP B, SYS_PATINFO C, SYS_DIAGNOSIS D, MRO_MRV_TECH E "
				+ " WHERE A.BED_NO = B.BED_NO(+)   "
				+ "   AND A.CASE_NO = B.CASE_NO(+) "
				+ "   AND A.MR_NO = B.MR_NO(+)     "
				+ "   AND A.MR_NO = C.MR_NO(+)     "
				+ "   AND A.ACTIVE_FLG = 'Y'       "
				+ "   AND A.CASE_NO = E.CASE_NO(+) "
				+ "   AND A.MR_NO = E.MR_NO(+)     "
				+ "   AND A.ALLO_FLG = 'Y'         "
				+ "   AND B.CANCEL_FLG <> 'Y'      "
				+ "   AND A.BED_STATUS = '1'       "
				+ "   AND B.DS_DATE IS NULL         "
				+ "   AND B.REGION_CODE = 'H01'    "
				+ "   AND B.MAINDIAG = D.ICD_CODE(+) "
				+ "   AND B.CASE_NO = '#'              "
				+ "ORDER BY B.CASE_NO DESC             ";
		sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
		System.out.println("sss:" + sql);
		TParm actionParm = new TParm(this.getDBTool().select(sql));

		if (actionParm.getCount("CASE_NO") < 0) {
			this.messageBox("δ�鵽����");
			return;
		}

		this.openDialog("%ROOT%\\config\\odi\\ODIMainUI.x", p, false);
	}

	/**
	 * �õ���Ӧҩ��
	 * 
	 * @param stationCode
	 *            String
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getOrgCode(String stationCode, String deptCode) {
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT ORG_CODE FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		if (parm.getCount() == 0) {
			return "";
		}
		return parm.getValue("ORG_CODE", 0);
	}

	/**
	 * ��鱨��
	 */
	public void onTestrep() {
		String mrNo = "";
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();

		switch (index) {
		case 1:
			if (getValueString("MR_NO_2").length() == 0)
				return;
			else
				mrNo = getValueString("MR_NO_2");
			SystemTool.getInstance().OpenRisWeb(mrNo);
			break;
		case 3:
			if (getValueString("MR_NO_4").length() == 0)
				return;
			else
				mrNo = getValueString("MR_NO_4");
			SystemTool.getInstance().OpenRisWeb(mrNo);
			break;
		}
	}

	/**
	 * ���µ�
	 */
	public void onTemperature() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 1:
			TParm parm = new TParm();
			parm.setData("SUM", "CASE_NO", getValue("CASE_NO_2"));
			parm.setData("SUM", "ADM_TYPE", "I");
			openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
			break;
		}
	}

	/**
	 * �������
	 */
	public void onShowCase() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 1:
			TParm parm = new TParm();
			parm.setData("SYSTEM_TYPE", "ODI");
			parm.setData("ADM_TYPE", "I");
			parm.setData("CASE_NO", getValue("CASE_NO_2"));
			parm.setData("PAT_NAME", getValue("PAT_NAME_2"));
			parm.setData("MR_NO", getValue("MR_NO_2"));
			parm.setData("IPD_NO", getValue("IPD_NO_2"));
			parm.setData("ADM_DATE", getValue("IN_DATE_2"));
			parm.setData("DEPT_CODE", getValue("DEPT_CODE_2"));
			parm.setData("RULETYPE", "1");
			parm.setData("EMR_DATA_LIST", new TParm());
			parm.addListener("EMR_LISTENER", this, "emrListener");
			parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
			openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
			break;
		}
	}

	/**
	 * EMR����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void emrListener(TParm parm) {
		parm.runListener("setMicroData", "S", "A");
	}

	/**
	 * EMR�������
	 * 
	 * @param parm
	 *            TParm
	 */
	public void emrSaveListener(TParm parm) {
	}

	/**
	 * ��ԭѧ�����
	 */
	public void onEtiolgexmFlgYTwo() {
		((TTextFormat) getComponent("SPECIMEN_CODE_2")).setEnabled(true);
		((TTextFormat) getComponent("EXAM_DATE_2")).setEnabled(true);
		((TCheckBox) getComponent("LABWAY_1_2")).setEnabled(true);
		((TCheckBox) getComponent("LABWAY_2_2")).setEnabled(true);
		((TCheckBox) getComponent("LABWAY_3_2")).setEnabled(true);
		((TRadioButton) getComponent("LABPOSITIVE_A_2")).setEnabled(true);
		((TRadioButton) getComponent("LABPOSITIVE_B_2")).setEnabled(true);
	}

	/**
	 * ��ԭѧ����
	 */
	public void onEtiolgexmFlgNTwo() {
		setValue("SPECIMEN_CODE_2", "");
		((TTextFormat) getComponent("SPECIMEN_CODE_2")).setEnabled(false);
		setValue("EXAM_DATE_2", "");
		((TTextFormat) getComponent("EXAM_DATE_2")).setEnabled(false);
		setValue("LABWAY_1_2", "N");
		((TCheckBox) getComponent("LABWAY_1_2")).setEnabled(false);
		setValue("LABWAY_2_2", "N");
		((TCheckBox) getComponent("LABWAY_2_2")).setEnabled(false);
		setValue("LABWAY_3_2", "N");
		((TCheckBox) getComponent("LABWAY_3_2")).setEnabled(false);
		setValue("LABPOSITIVE_A_2", "N");
		((TRadioButton) getComponent("LABPOSITIVE_A_2")).setEnabled(false);
		setValue("LABPOSITIVE_B_2", "N");
		((TRadioButton) getComponent("LABPOSITIVE_B_2")).setEnabled(false);
	}

	/**
	 * ����ҩ������������
	 */
	public void onAntibioTestFlgY() {
		((TButton) getComponent("EXPORT_BUTTON")).setEnabled(true);
	}

	/**
	 * ����ҩ�����������
	 */
	public void onAntibioTestFlgN() {
		((TButton) getComponent("EXPORT_BUTTON")).setEnabled(false);
	}

	/**
	 * ������ҳǩ��굥���¼�
	 */
	public void onTabke1And3() {
		TParm parm = getTable("TABLE_1_3").getParmValue();
		int row = getTable("TABLE_1_3").getSelectedRow();
		setValue("MR_NO_3", parm.getData("MR_NO", row));
		setValue("IPD_NO_3", parm.getData("IPD_NO", row));
		setValue("DEPT_3", parm.getData("DEPT_CODE", row));
		setValue("STATION_3", parm.getData("STATION_CODE", row));
		setValue("DR_3", parm.getData("VS_DR_CODE", row));
		setValue("PAT_NAME_3", parm.getData("PAT_NAME", row));
		setValue(
				"AGE_3",
				StringTool.CountAgeByTimestamp((Timestamp) parm.getData(
						"BIRTH_DATE", row), SystemTool.getInstance().getDate())[0]);
		setValue("ANTIBIOTIC_CODE_3", parm.getData("ANTIBIOTIC_CODE", row));
	}

	// 20170522 liuyalin add
	/**
	 * �����ҳǩ��굥���¼�
	 */
	public void onTabke1And5() {
		TParm parm = getTable("TABLE_1_5").getParmValue();
		int row = getTable("TABLE_1_5").getSelectedRow();
		setValue("MR_NO_5", parm.getData("MR_NO", row));
		setValue("IPD_NO_5", parm.getData("IPD_NO", row));
		setValue("DEPT_5", parm.getData("DEPT_CODE", row));
		setValue("STATION_5", parm.getData("STATION_CODE", row));
		setValue("DR_5", parm.getData("VS_DR_CODE", row));
		setValue("PAT_NAME_5", parm.getData("PAT_NAME", row));
		setValue("ORDER_CODE_5", parm.getData("ORDER_CODE", row));
		setValue("ORDER_DESC_5", parm.getData("ORDER_DESC", row));
		setValue(
				"AGE_5",
				StringTool.CountAgeByTimestamp((Timestamp) parm.getData(
						"BIRTH_DATE", row), SystemTool.getInstance().getDate())[0]);
	}
	/**
	 * ��ѯ������Ϣ
	 */
	public void onQueryNO() {
		Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO_5")));
		if (pat == null) {
			clearValue("MR_NO_5;PAT_NAME_5;");
			this.messageBox("�޴˲�����!");
			return;
		}
		String mrNo = PatTool.getInstance().checkMrno(
				TypeTool.getString(getValue("MR_NO_5")));
		setValue("MR_NO_5", mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			setValue("MR_NO_5", pat.getMrNo());
		}
		setValue("PAT_NAME_5", pat.getName().trim());
		this.onQueryFive();
	}
	// 20170522 liuyalin add end
	/**
	 * ����excel
	 */
	public void onExport() {// add by wanglong 20131219
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 0:
			TTable table = getTable("TABLE_1");
			if (table.getRowCount() <= 0) {
				this.messageBox("û������");
				return;
			}
			int num = table.getHeader().split(";").length;
			String header = "";
			for (int i = 0; i < num; i++) {
				header += table.getParmMap(i) + ";";
			}
			header = header.substring(0, header.length() - 1);
			table.setParmMap(header);
			ExportExcelUtil.getInstance().exportExcel(table, "��Ⱦ����ɸ�鱨��");
			break;
		// case 1:
		// if (getTable("TABLE_4_2").getRowCount() <= 0) {
		// this.messageBox("û������");
		// return;
		// }
		// ExportExcelUtil.getInstance().exportExcel(getTable("TABLE_4_2"),
		// "�����ų̱���");
		// break;
		case 2:
			if (getTable("TABLE_1_3").getRowCount() <= 0) {
				this.messageBox("û������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(getTable("TABLE_1_3"),
					"������ɸ�鱨��");
			break;
		case 3:
			if (getTable("TABLE_1_4").getRowCount() <= 0) {
				this.messageBox("û������");
				return;
			}
			ExportExcelUtil.getInstance().exportExcel(getTable("TABLE_1_4"),
					"��Ⱦ����ά������");
			break;
		}
	}

	/**
	 * ����һ��SQL������ֶ���Ϣ���� IN()��䣬Ϊ��һ��SQL����
	 * 
	 * @param sqlName
	 * @param Nos
	 * @return
	 */
	public static String getInStatement(TParm Nos, String columnName,
			String sqlName) {// add by wanglong 20131105
		if (Nos.getCount(columnName) < 1) {
			return " 1=1 ";
		}
		StringBuffer inStr = new StringBuffer();
		inStr.append(sqlName + " IN ('");
		for (int i = 0; i < Nos.getCount(columnName); i++) {
			inStr.append(Nos.getValue(columnName, i));
			if ((i + 1) != Nos.getCount(columnName)) {
				if ((i + 1) % 999 != 0) {
					inStr.append("','");
				} else if (((i + 1) % 999 == 0)) {
					inStr.append("') OR " + sqlName + " IN ('");
				}
			}
		}
		inStr.append("')");
		return inStr.toString();
	}

	public static String getInStatementString(String[] s, String columnName,
			String sqlName) {// add by wanglong 20131105
		if (s.length < 1) {
			return " 1=1 ";
		}
		StringBuffer inStr = new StringBuffer();
		inStr.append(sqlName + " IN ('");
		for (int i = 0; i < s.length; i++) {
			// inStr.append(Nos.getValue(columnName, i));
			inStr.append(s[i]);
			if ((i + 1) != s.length) {
				if ((i + 1) % 999 != 0) {
					inStr.append("','");
				} else if (((i + 1) % 999 == 0)) {
					inStr.append("') OR " + sqlName + " IN ('");
				}
			}
		}
		inStr.append("')");
		return inStr.toString();
	}
	
	/**
	 * add lij 20170502 ��ͨ��ť����"˫��ͨ"
	 */
	public void onCommunicate() {
		int index = ((TTabbedPane) getComponent("TabbedPane_1"))
				.getSelectedIndex();
		switch (index) {
		case 0:
			onCommunicateOne();
			break;
		case 3:
			onCommunicateTwo();
			break;
		}
	}

	/**
	 * add lij 20170502 ��Ⱦ����ɸѡ����"˫��ͨ"
	 */
	public void onCommunicateOne() {
		int rowIndex = ((TTable) this.getComponent("TABLE_1")).getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("��ѡ��һ�ݲ�����");
			return;
		}
		TParm parm = new TParm();
		TParm patInfo = (TParm) callFunction("UI|TABLE_1|getParmValue");
		parm.setData("CLASS_NAME", "HANDLE");
		parm.setData("CASE_NO", patInfo.getValue("CASE_NO", rowIndex));
		this.openWindow("%ROOT%\\config\\inf\\INFTwoCommunicate.x", parm);
	}

	/**
	 * add lij 20170502 ��Ⱦ����ά������"˫��ͨ"
	 */
	public void onCommunicateTwo() {
		int rowIndex = ((TTable) this.getComponent("TABLE_1_4"))
				.getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("��ѡ��һ�ݲ�����");
			return;
		}
		TParm parm = new TParm();
		TParm patInfo = (TParm) callFunction("UI|TABLE_1_4|getParmValue");
		parm.setData("CLASS_NAME", "HANDLE");
		parm.setData("CASE_NO", patInfo.getValue("CASE_NO", rowIndex));
		this.openWindow("%ROOT%\\config\\inf\\INFTwoCommunicate.x", parm);
	}
	/**
	 * ��Ӹ�Ⱦ��Ⱦ��λ���Ⱦϵͳ
	 */
	public void ADD(){
		
		TParm parm = getTable("TABLE_5_2").getParmValue();
		if(parm == null){
			return;
		}
		String infSystemCode = this.getValueString("INF_SYSTEMCODE11");
		String partCode = this.getValueString("PART_CODE11");
		if(StringUtil.isNullString(infSystemCode)){
			this.messageBox("��Ⱦϵͳ����Ϊ��");
			return;
		}
		if(StringUtil.isNullString(partCode)){
			this.messageBox("��Ⱦ��λ����Ϊ��");
			return;
		}
		//this.messageBox(partCode);
		String sql ="SELECT * FROM INF_SYSTEMDICTRONARY WHERE INFSYSTEMCODE='"+infSystemCode+"'";
		System.out.println("22222"+sql);
		TParm p = new TParm(TJDODBTool.getInstance().select(sql));
		Boolean flg = true;
		for(int i = 0 ;i<p.getCount();i++){
			//this.messageBox(p.getValue("INFPOSITIONCODE",i));
			if(p.getValue("INFPOSITIONCODE",i).equals(partCode)){
				flg = false;
			}
		}
		
		if(flg){
			this.messageBox("��Ⱦϵͳ���Ⱦ��λ��ƥ��,������ѡ��");
			return;
		}
		if(StringUtil.isNullString(parm.getValue("INF_SYSTEMCODE",0))){
			parm.removeRow(0, "MAIN_FLG");
			parm.removeRow(0, "INF_SYSTEMCODE");
			parm.removeRow(0, "PART_CODE");
			parm.removeRow(0, "ICD_CODE");
			parm.removeRow(0, "UM_CODE");
			parm.removeRow(0, "ICD_DESC");
			parm.removeRow(0, "UM_DESC");
			parm.removeRow(0, "SEQ");
		}
		int row = parm.getCount("MAIN_FLG");
		//this.messageBox(row+"");
		parm.addData("MAIN_FLG", "N");
		parm.addData("INF_SYSTEMCODE", infSystemCode);
		parm.addData("PART_CODE", partCode);
		parm.addData("ICD_CODE", "");
		parm.addData("UM_CODE", "");
		parm.addData("ICD_DESC", "");
		parm.addData("UM_DESC", "");
		parm.addData("SEQ", row + 1);
		parm.setCount(row + 1);
			
		//this.messageBox(parm+"");
		getTable("TABLE_5_2").setParmValue(parm);
	}
	public void ComboxEvent(){
		TTextFormat com = (TTextFormat)getComponent("PART_CODE11");
		if(this.getValueString("INF_SYSTEMCODE11").equals("")){
			com.setPopupMenuSQL("SELECT ID,CHN_DESC AS NAME FROM SYS_DICTIONARY WHERE GROUP_ID='INF_INFPOSITION' ORDER BY SEQ");
		}else{
			com.setPopupMenuSQL(" SELECT ID, CHN_DESC AS NAME "+
								" FROM SYS_DICTIONARY "+
								" WHERE     GROUP_ID = 'INF_INFPOSITION' "+
								" AND ID IN (SELECT INFPOSITIONCODE "+
								" FROM INF_SYSTEMDICTRONARY "+
								" WHERE INFSYSTEMCODE = '"+this.getValueString("INF_SYSTEMCODE11")+"') ORDER BY SEQ");
                    
                   
		}
		com.onQuery();
	}
}
