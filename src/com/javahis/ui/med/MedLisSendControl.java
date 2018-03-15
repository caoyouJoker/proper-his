package com.javahis.ui.med;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.TableModel;

import jdo.hl7.Hl7Communications;
import jdo.inw.InwForOdiTool;
import jdo.med.MEDApplyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:�����ͼ��嵥
 * </p>
 * 
 * <p>
 * Description: �����ͼ��嵥
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class MedLisSendControl extends TControl {
	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	// �����HL7�ӿڵ�����
	TParm sendHL7Parm = new TParm();
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";

	/**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		// Document barDoc = bar.getDocument();
		// barDoc.addDocumentListener(new javax.swing.event.DocumentListener() {
		// public void changedUpdate(DocumentEvent e) {
		// // TODO �Զ����ɷ������
		// }
		//
		// public void insertUpdate(DocumentEvent e) {
		// onBarCode();
		// // TODO �Զ����ɷ������
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// bar.setValue("");
		// }
		// });
		// bar.requestFocus();
		// }
		// public void removeUpdate(DocumentEvent e) {
		// // TODO �Զ����ɷ������;
		// }
		// });
		// ��TABLE�е�CHECKBOX��������¼�
		callFunction("UI|BAR_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onBarCode");
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
		this.setValue("STATION_CODE", Operator.getStation());
		this.setValue("RE_USER", Operator.getID());
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String tDate = StringTool.getString(sysDate, "yyyyMMdd");
		// Ĭ��������ʼ����
		this.setValue("START_DATE",
				StringTool.getTimestamp(tDate + "000000", "yyyyMMddHHmmss"));
		// Ĭ��������ֹ����
		this.setValue("END_DATE",
				StringTool.getTimestamp(tDate + "235959", "yyyyMMddHHmmss"));
		callFunction("UI|RE_START|setEnabled", false);
		callFunction("UI|RE_END|setEnabled", false);
		callFunction("UI|print|setEnabled", false);
		// �������
		addListener(getTTable(TABLE));
		
		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof String) {
				this.setPopedem(String.valueOf(obj), true);
			}
		}
		
		if (this.getPopedem("H")) {
			this.setValue("STATION_CODE", "");
		}
	}

	/**
	 * ��������
	 */
	public void onBarCode() {
		// δ����
		boolean NFlag = (Boolean) this.callFunction("UI|ISRE2|isSelected");
		// ��ʼʱ��
		String start = this.getValueString("START_DATE");
		// ����ʱ��
		String end = this.getValueString("END_DATE");
		// ������Ա
		String reUser = this.getValueString("RE_USER");
		// ����
		String stationCode = this.getValueString("STATION_CODE");

		String startStr = start.substring(0, 19).replaceAll("-", "")
				.replaceAll(":", "");
		String endStr = end.substring(0, 19).replaceAll("-", "")
				.replaceAll(":", "");
		String sql = this.getMedLisSql();
		if (!stationCode.equals("") && stationCode.length() > 0)
			sql += " AND A.STATION_CODE='" + stationCode + "'";
		if (!startStr.equals("") && startStr.length() > 0) {
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		if (!endStr.equals("") && endStr.length() > 0) {
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		String barCode = this.getValueString("BAR_CODE");
		if (!barCode.equals("") && barCode.length() > 0)
			sql += " AND B.MED_APPLY_NO='" + barCode + "'";
		if (NFlag) {
			sql += " AND A.LIS_RE_DATE IS NULL";
		} else {
			sql += " AND A.LIS_RE_DATE IS NOT NULL";
			if (!reUser.equals("") && reUser.length() > 0)
				sql += " AND LIS_RE_USER='" + reUser + "'";
		}
		// ��ѯ���
		TParm parm = new TParm(this.getDBTool().select(sql));
		if (parm.getCount() <= 0) {
			this.messageBox("û������");
			return;
		}
		boolean flg = true;
		// ������
		TParm tableParm = this.getTable("TABLE").getParmValue();
		if (tableParm != null) {
			for (int i = 0; i < tableParm.getCount("MED_APPLY_NO"); i++) {
				if (tableParm.getValue("MED_APPLY_NO", i).equals(
						parm.getValue("MED_APPLY_NO", 0))) {
					this.messageBox("��ɨ������룡");
					flg = false;
					break;
				}
			}
			if (flg) {
				tableParm.addParm(parm);
				this.getTable("TABLE").setParmValue(tableParm);
			}
		} else {
			this.getTable("TABLE").setParmValue(parm);
		}
		this.setValue("BAR_CODE", "");
	}

	/**
	 * 
	 * @param obj
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		TParm tableParm = parm.getRow(row);
		String applicationNo = tableParm.getValue("MED_APPLY_NO");
		if ("FLG".equals(columnName)) {
			int rowCount = parm.getCount("ORDER_DESC");
			for (int i = 0; i < rowCount; i++) {
				if (i == row)
					continue;
				if (applicationNo.equals(parm.getValue("MED_APPLY_NO", i))) {
					parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
							: "Y");
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * ���涯��
	 */
	public void onSave() {
		TParm parm = this.getTable("TABLE").getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		if (parm == null) {
			this.messageBox("�ޱ������ݣ�");
			return;
		}
		TParm inParm = new TParm();
		TParm hl7Parm = new TParm();
		TParm result = new TParm();
		String applicationNo = "";
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("FLG", i))
				continue;
			applicationNo = parm.getValue("MED_APPLY_NO", i);
			inParm.setData("LIS_RE_USER", this.getValueString("RE_USER"));
			inParm.setData("LIS_RE_DATE", now);
			inParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
			inParm.setData("ORDER_NO", parm.getValue("ORDER_NO", i));
			inParm.setData("ORDER_SEQ", parm.getInt("ORDER_SEQ", i));
			inParm.setData("START_DTTM", parm.getValue("START_DTTM", i));
			inParm.setData("OPT_USER", Operator.getID());
			inParm.setData("OPT_TERM", Operator.getIP());
			inParm.setData("APPLICATION_NO", applicationNo);
			// HL7PARM
			sendHL7Parm.addData("MED_APPLY_NO",
					parm.getValue("MED_APPLY_NO", i));
			sendHL7Parm.addData("CASE_NO", parm.getValue("CASE_NO", i));
			sendHL7Parm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			sendHL7Parm.addData("ORDER_SEQ", parm.getInt("ORDER_SEQ", i));
			sendHL7Parm.addData("START_DTTM", parm.getValue("START_DTTM", i));
			sendHL7Parm.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
			sendHL7Parm.addData("CAT1_TYPE", parm.getValue("CAT1_TYPE", i));
			
			// modify by wangb 2016/11/18
			if (this.getPopedem("H")) {
				// һ���ٴ�����ͼ콻��
				result = MEDApplyTool.getInstance().updateMedApplyLisReceiveData(inParm);
			} else {
				result = InwForOdiTool.getInstance().updateOdidspnmLisData(
						inParm);
			}

			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ�ܣ�");
				return;
			}
		}
		this.messageBox("����ɹ���");
		sendHL7Mes();
		this.getTable("TABLE").removeRowAll();
	}

	/**
	 * ������Ϣ�ļ�
	 */
	public void onGenSendHl7() {
		TParm parm = this.getTable("TABLE").getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("FLG", i))
				continue;
			sendHL7Parm.addData("MED_APPLY_NO",
					parm.getValue("MED_APPLY_NO", i));
			sendHL7Parm.addData("CASE_NO", parm.getValue("CASE_NO", i));
			sendHL7Parm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			sendHL7Parm.addData("ORDER_SEQ", parm.getInt("ORDER_SEQ", i));
			sendHL7Parm.addData("START_DTTM", parm.getValue("START_DTTM", i));
			sendHL7Parm.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
		}
		sendHL7Mes();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		// δ����
		boolean NFlag = (Boolean) this.callFunction("UI|ISRE2|isSelected");
		// ��ʼʱ��
		String start = this.getValueString("START_DATE");
		// ����ʱ��
		String end = this.getValueString("END_DATE");
		// ������Ա
		String reUser = this.getValueString("RE_USER");
		// ����
		String stationCode = this.getValueString("STATION_CODE");

		String sql = this.getMedLisSql();
		if (!start.equals("") && start.length() > 0) {
			String startStr = start.substring(0, 19).replaceAll("-", "")
					.replaceAll(":", "");
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		if (!end.equals("") && end.length() > 0) {
			String endStr = end.substring(0, 19).replaceAll("-", "")
					.replaceAll(":", "");
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		if (!stationCode.equals("") && stationCode.length() > 0)
			sql += " AND A.STATION_CODE='" + stationCode + "'";
		if (NFlag) {
			sql += " AND A.LIS_RE_DATE IS NULL";
		} else {
			sql += " AND A.LIS_RE_DATE IS NOT NULL";
			if (!reUser.equals("") && reUser.length() > 0)
				sql += " AND LIS_RE_USER='" + reUser + "'";
			// ��ʼʱ��
			String restart = this.getValueString("RE_START");
			// ����ʱ��
			String reend = this.getValueString("RE_END");
			if (!start.equals("") && start.length() > 0) {
				String REstartStr = restart.substring(0, 19)
						.replaceAll("-", "").replaceAll(":", "");
				sql += " AND A.LIS_RE_DATE>=TO_DATE('" + REstartStr
						+ "','YYYYMMDD HH24MISS')";
			}
			if (!end.equals("") && end.length() > 0) {
				String REendStr = reend.substring(0, 19).replaceAll("-", "")
						.replaceAll(":", "");
				sql += " AND A.LIS_RE_DATE<=TO_DATE('" + REendStr
						+ "','YYYYMMDD HH24MISS')";
			}
		}
		sql += " ORDER BY  A.LIS_RE_DATE,A.MR_NO DESC";
		TParm parm = new TParm(this.getDBTool().select(sql));
		if (parm.getCount() <= 0) {
			this.getTable("TABLE").removeRowAll();
			this.messageBox("�޲�ѯ���ݣ�");
			return;
		}
		this.getTable("TABLE").setParmValue(parm);
	}

	/**
	 * �ı��¼�
	 */
	public void onChangeButton() {
		// �ѽ���
		boolean YFlag = (Boolean) this.callFunction("UI|ISRE1|isSelected");
		if (YFlag) {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|print|setEnabled", true);
			callFunction("UI|RE_START|setEnabled", true);
			callFunction("UI|RE_END|setEnabled", true);
			Timestamp sysDate = SystemTool.getInstance().getDate();
			String tDate = StringTool.getString(sysDate, "yyyyMMdd");
			// Ĭ��������ʼ����
			this.setValue("RE_START",
					StringTool.getTimestamp(tDate + "000000", "yyyyMMddHHmmss"));
			// Ĭ��������ֹ����
			this.setValue("RE_END", StringTool.getTimestamp(("" + sysDate)
					.substring(0, 19).replaceAll("-", "").replaceAll(":", ""),
					"yyyyMMdd HHmmss"));
		} else {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|print|setEnabled", false);
			callFunction("UI|RE_START|setEnabled", false);
			callFunction("UI|RE_END|setEnabled", false);
			this.setValue("RE_START", "");
			this.setValue("RE_END", "");
		}
	}

	/**
	 * ��ӡ����
	 */
	public void onPrint() {
		TParm parm = this.getTable("TABLE").getParmValue();
		if (parm == null) {
			this.messageBox("�޴�ӡ����");
			return;
		}
		TParm printData = new TParm();
		int count = 0;
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			if (!parm.getBoolean("FLG", i))
				continue;
			printData.addData("BED_NO", parm.getValue("BED_NO", i));
			printData.addData("MR_NO", parm.getValue("MR_NO", i));
			printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
			printData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
			printData.addData("MED_APPLY_NO", parm.getValue("MED_APPLY_NO", i));
			printData.addData("NS_EXEC_DATE", parm.getValue("NS_EXEC_DATE", i)
					.substring(5, 16));
			printData.addData("LIS_RE_DATE", parm.getValue("LIS_RE_DATE", i)
					.substring(5, 16));
			printData.addData("DR_NOTE", parm.getValue("DR_NOTE", i));
			count++;
		}
		printData.setCount(count);
		printData.addData("SYSTEM", "COLUMNS", "BED_NO");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
		printData.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
		printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
		printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		TParm printParm = new TParm();
		printParm.setData("TITLE", "TEXT", "����걾�ͼ��嵥");
		printParm.setData("STATION_CODE", "TEXT",
				"������" + getStationDesc(this.getValueString("STATION_CODE")));
		if (this.getPopedem("H")) {
			printParm.setData("STATION_CODE", "TEXT", "");
		}
		
		// ��ʼʱ��
		String restart = this.getValueString("RE_START");
		// ����ʱ��
		String reend = this.getValueString("RE_END");
		printParm.setData("DATE", "TEXT", "�������գ�"
				+ restart.substring(0, 19).replaceAll("-", "/") + " " + "�������գ�"
				+ reend.substring(0, 19).replaceAll("-", "/"));
		printParm.setData("TABLE", printData.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\MED\\MedLisSend.jhw",
				printParm);
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.getTable("TABLE").removeRowAll();
		this.setValue("BAR_CODE", "");
	}

	/**
	 * ȫѡ
	 */
	public void onCheckBoxClicked() {
		boolean Flag = (Boolean) this.callFunction("UI|EXE|isSelected");
		TParm parm = this.getTable("TABLE").getParmValue();
		TTable table = this.getTable("TABLE");
		for (int i = 0; i < parm.getCount(); i++) {
			table.setItem(i, "FLG", Flag);
		}
	}

	/**
	 * ȡ�ò���
	 * 
	 * @param stationCode
	 * @return
	 */
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT STATION_DESC " + " FROM SYS_STATION "
						+ " WHERE STATION_CODE='" + stationCode + "'"));

		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * �õ�TTable����
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ�TTextField����
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * getDBTool ���ݿ⹤��ʵ��
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �����ѯSQL
	 * 
	 * @return
	 */
	private String getMedLisSql() {
		String sql = "";
		if (this.getPopedem("H")) {
			sql = "SELECT 'Y' AS FLG,C.PAT_NAME,A.MR_NO,A.ORDER_DESC,A.APPLICATION_NO,B.MED_APPLY_NO,"
				+ " TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS NS_EXEC_DATE,A.DR_NOTE,"
				+ " TO_CHAR(A.LIS_RE_DATE,'YYYY/MM/DD HH24:MI:SS') AS LIS_RE_DATE,A.LIS_RE_USER,A.CASE_NO,A.CAT1_TYPE "
				+ " FROM MED_APPLY A,HRM_ORDER B ,SYS_PATINFO C "
				+ " WHERE A.CASE_NO=B.CASE_NO "
				+ " AND A.SEQ_NO=B.SEQ_NO "
				+ " AND A.APPLICATION_NO = B.MED_APPLY_NO "
				+ " AND A.MR_NO=C.MR_NO "
				+ " AND A.CAT1_TYPE='LIS' "
				+ " AND A.STATUS <> '9' ";
		} else {
			sql = "SELECT C.BED_NO_DESC AS BED_NO,D.PAT_NAME,A.MR_NO,A.ORDER_DESC,B.MED_APPLY_NO,"
					+ " TO_CHAR(A.NS_EXEC_DATE,'YYYY/MM/DD HH24:MI:SS') AS NS_EXEC_DATE,'Y' AS FLG,B.DR_NOTE,"
					+ " TO_CHAR(A.LIS_RE_DATE,'YYYY/MM/DD HH24:MI:SS') AS LIS_RE_DATE,A.LIS_RE_USER,A.CASE_NO,"
					+ " A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.CAT1_TYPE "
					+ " FROM ODI_DSPNM A,ODI_ORDER B ,SYS_BED C,SYS_PATINFO D "
					+ " WHERE A.CASE_NO=B.CASE_NO "
					+ " AND A.ORDER_NO=B.ORDER_NO "
					+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
					+ " AND A.BED_NO=C.BED_NO "
					+ " AND A.MR_NO=D.MR_NO "
					+ " AND A.CAT1_TYPE='LIS' "
					+ " AND A.HIDE_FLG='N' AND A.NS_EXEC_CODE IS NOT NULL ";
		}
		return sql;
	}

	/**
	 * ����HL7��Ϣ
	 * 
	 * @param catType
	 *            ҽ�����
	 * @param caseNo
	 *            String �����
	 * @param applictionNo
	 *            String �����
	 */
	private void sendHL7Mes() {
		int count = ((Vector) sendHL7Parm.getData("CASE_NO")).size();
		if (count <= 0) {
			return;
		}
		List list = new ArrayList();
		Map map = new HashMap();
		String sql = "";
		TParm result = new TParm();
		TParm parm = new TParm();
		
		for (int i = 0; i < count; i++) {
			// shibl 20120830 modify
			if (map.get(sendHL7Parm.getValue("MED_APPLY_NO", i)) != null) {
				continue;
			}
			map.put(sendHL7Parm.getValue("MED_APPLY_NO", i),
					sendHL7Parm.getValue("MED_APPLY_NO", i));
			
			if (this.getPopedem("H")) {
				parm = new TParm();
				parm.setData("CASE_NO", sendHL7Parm.getValue("CASE_NO", i));
				parm.setData("LAB_NO", sendHL7Parm.getValue("MED_APPLY_NO", i));
				parm.setData("CAT1_TYPE", sendHL7Parm.getValue("CAT1_TYPE", i));
				parm.setData("ADM_TYPE", "H");
			} else {
				sql = " SELECT * FROM ODI_ORDER WHERE CASE_NO ='"
					+ sendHL7Parm.getValue("CASE_NO", i) + "' AND ORDER_NO='"
					+ sendHL7Parm.getValue("ORDER_NO", i) + "' AND ORDER_SEQ="
					+ sendHL7Parm.getInt("ORDER_SEQ", i) + "";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				parm = new TParm();
				parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
				parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
				parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));
				parm.setData("ORDER_NO", result.getValue("ORDER_NO", 0));
				parm.setData("ORDER_SEQ", result.getInt("ORDER_SEQ", 0));
			}
			
			list.add(parm);
		}
		// ���parm
		// modify by wangb 2016/12/7
		// ҳ���ڳ���ִ���ͼ챣�治�رս��������£�������ٴ�ɨ�����ͣ��ᱨ���޷����͡�
		// ԭ��Ϊ���α�����Ϻ�ȫ�ֱ�������գ�����յ�ֻ����ֵ������Ȼ����������պ�sendHL7Parm�е�countΪ0��
		// ҳ�治�رյ�ǰ���£��������ʱ��װ�õ�sendHL7Parm�е�count��ȻΪ0���µ���removeʱԽ���쳣
//		while (sendHL7Parm.getCount("CASE_NO") > 0) {
//			sendHL7Parm.removeRow(0);
//		}
		sendHL7Parm = new TParm();
		// ���ýӿ�
		TParm resultParm = Hl7Communications.getInstance().Hl7SendLis(list);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
		else
			this.messageBox("���ͳɹ�");
	}

	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * �õ�TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
}
