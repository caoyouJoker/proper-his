package com.javahis.ui.emr;

import java.awt.Component;
import java.awt.Container;
import java.sql.Timestamp;

import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ���߼���
 * </p>
 * 
 * <p>
 * Description: ���߼���
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company:bluecore
 * </p>
 * 
 * @author suny 2015.05.12
 * @version 1.0
 */
public class EMRCdrPatientIndexControl extends TControl {

	private TTable table;

	public EMRCdrPatientIndexControl() {

	}

	/**
	 * ��ʼ������
	 * 
	 */
	public void onInit() {
		table = (TTable) this.getComponent("TABLE");
		onClear();
	}

	/**
	 * ����CheckBox�ı༭״̬
	 */
	public void setEdit() {
		// ������
		if (getCheckBox("MR_NO_CB").isSelected()) {
			callFunction("UI|MR_NO|setEnabled", true);
		} else {
			callFunction("UI|MR_NO|setEnabled", false);
			this.clearValue("MR_NO");
		}
		
		// ����
		if (getCheckBox("PAT_NAME_CB").isSelected()) {
			callFunction("UI|PAT_NAME|setEnabled", true);
		} else {
			callFunction("UI|PAT_NAME|setEnabled", false);
			this.clearValue("PAT_NAME");
		}
		
		// �Ա�
		if (getCheckBox("SEX_CODE_CB").isSelected()) {
			callFunction("UI|SEX_CODE|setEnabled", true);
		} else {
			callFunction("UI|SEX_CODE|setEnabled", false);
			this.clearValue("SEX_CODE");
		}

		// ����ʱ��
		if (getCheckBox("BIRTH_DATE_CB").isSelected()) {
			callFunction("UI|BIRTH_DATE_YEAR|setEnabled", true);
			callFunction("UI|BIRTH_DATE_MONTH|setEnabled", true);
		} else {
			callFunction("UI|BIRTH_DATE_YEAR|setEnabled", false);
			callFunction("UI|BIRTH_DATE_MONTH|setEnabled", false);
			this.clearValue("BIRTH_DATE_YEAR");
			this.clearValue("BIRTH_DATE_MONTH");
		}
		
		// ����
		if (getCheckBox("AGE_CB").isSelected()) {
			callFunction("UI|AGE_START|setEnabled", true);
			callFunction("UI|AGE_END|setEnabled", true);
		} else {
			callFunction("UI|AGE_START|setEnabled", false);
			callFunction("UI|AGE_END|setEnabled", false);
			this.clearValue("AGE_START");
			this.clearValue("AGE_END");
		}

		// ���
		if (getCheckBox("RIGHT_NOW").isSelected()) {
			callFunction("UI|RECENTLY|setEnabled", true);
		} else {
			callFunction("UI|RECENTLY|setEnabled", false);
			((TComboBox) getComponent("RECENTLY")).setSelectedID("9");
		}
		
		// ��������
		if (getCheckBox("ADM_TYPE_CB").isSelected()) {
			callFunction("UI|ADM_TYPE|setEnabled", true);
		} else {
			callFunction("UI|ADM_TYPE|setEnabled", false);
			this.clearValue("ADM_TYPE");
		}

		// �������
		if (getCheckBox("DEPT_CODE_CB").isSelected()) {
			callFunction("UI|DEPT_CODE|setEnabled", true);
		} else {
			callFunction("UI|DEPT_CODE|setEnabled", false);
			this.clearValue("DEPT_CODE");
		}

		if (getCheckBox("tCheckBox_0").isSelected()) {
			callFunction("UI|ADM_DATE_START|setEnabled", true);
			callFunction("UI|ADM_DATE_END|setEnabled", true);
			Timestamp now = SystemTool.getInstance().getDate();
			this.setValue("ADM_DATE_START", StringTool.rollDate(now, -7));
			this.setValue("ADM_DATE_END", now);
		} else {
			callFunction("UI|ADM_DATE_START|setEnabled", false);
			callFunction("UI|ADM_DATE_END|setEnabled", false);
			this.clearValue("ADM_DATE_START;ADM_DATE_END");
		}
		
		// ����
		if (getCheckBox("STATION_CODE_CB").isSelected()) {
			callFunction("UI|STATION_CODE|setEnabled", true);
		} else {
			callFunction("UI|STATION_CODE|setEnabled", false);
			this.clearValue("STATION_CODE");
		}
		
		// ����
		if (getCheckBox("CLINICAREA_CODE_CB").isSelected()) {
			callFunction("UI|CLINICAREA_CODE|setEnabled", true);
		} else {
			callFunction("UI|CLINICAREA_CODE|setEnabled", false);
			this.clearValue("CLINICAREA_CODE");
		}
		
		// ����
		if (getCheckBox("BED_NO_CB").isSelected()) {
			callFunction("UI|BED_NO|setEnabled", true);
		} else {
			callFunction("UI|BED_NO|setEnabled", false);
			this.clearValue("BED_NO");
		}
		
		// ����
		if (getCheckBox("CLINICROOM_NO_CB").isSelected()) {
			callFunction("UI|CLINICROOM_NO|setEnabled", true);
		} else {
			callFunction("UI|CLINICROOM_NO|setEnabled", false);
			this.clearValue("CLINICROOM_NO");
		}
		
		// ����ҽʦ
		if (getCheckBox("VS_DR_CODE_CB").isSelected()) {
			callFunction("UI|VS_DR_CODE|setEnabled", true);
		} else {
			callFunction("UI|VS_DR_CODE|setEnabled", false);
			this.clearValue("VS_DR_CODE");
		}

		// ����ҽʦ
		if (getCheckBox("ATTEND_DR_CODE_CB").isSelected()) {
			callFunction("UI|ATTEND_DR_CODE|setEnabled", true);
		} else {
			callFunction("UI|ATTEND_DR_CODE|setEnabled", false);
			this.clearValue("ATTEND_DR_CODE");
		}
		
		// ������
		if (getCheckBox("DIRECTOR_DR_CODE_CB").isSelected()) {
			callFunction("UI|DIRECTOR_DR_CODE|setEnabled", true);
		} else {
			callFunction("UI|DIRECTOR_DR_CODE|setEnabled", false);
			this.clearValue("DIRECTOR_DR_CODE");
		}

		// �˼�ּ�
		if (getCheckBox("ERD_LEVEL_CB").isSelected()) {
			callFunction("UI|ERD_LEVEL|setEnabled", true);
		} else {
			callFunction("UI|ERD_LEVEL|setEnabled", false);
			this.clearValue("ERD_LEVEL");
		}
	}

	/*
	 * ��ѯ
	 */
	public void onQuery() {
		String sql = "SELECT MR_NO, PAT_NAME, BIRTH_DATE,SEX_TYPE FROM CRP_PATIENT  WHERE 1 = 1 ";

		// ������
		if (this.getValueString("MR_NO_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("MR_NO"))) {
			String mrNo = PatTool.getInstance().checkMrno(
					this.getValueString("MR_NO"));
//			sql = sql + " AND (MR_NO = '" + mrNo + "' OR MERGE_MR_NO = '"
//					+ mrNo + "')";
			//modify by huangtt 20161019 �ಡ���Ų�ѯ
			sql = sql + " AND MR_NO IN ("+PatTool.getInstance().getMrRegMrNos(mrNo)+") ";
			
		}
		
		// ����
		if (this.getValueString("PAT_NAME_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("PAT_NAME"))) {
			sql = sql + " AND PAT_NAME LIKE '"
					+ this.getValueString("PAT_NAME") + "'%'";
		}

		// �Ա�
		if (this.getValueString("SEX_CODE_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("SEX_CODE"))) {
			sql = sql
					+ " AND (CASE SEX_TYPE WHEN 'M' THEN '1' WHEN 'F' THEN '2' ELSE '9' END) ='"
					+ this.getValueString("SEX_CODE") + "')";
		}

		// ��������
		if (this.getValueString("BIRTH_DATE_CB").equals("Y")) {
			if (StringUtils.isNotEmpty(this.getValueString("BIRTH_DATE_YEAR"))) {
				sql = sql + " AND TO_CHAR(BIRTH_DATE,'YYYY') = '"
						+ this.getValueString("BIRTH_DATE_YEAR") + "' ";
			}

			if (StringUtils.isNotEmpty(this.getValueString("BIRTH_DATE_MONTH"))) {
				sql = sql
						+ " AND TO_CHAR(BIRTH_DATE,'MM') = '"
						+ StringTool.fill("0", 2 - this.getValueString(
								"BIRTH_DATE_MONTH").length())
						+ this.getValueString("BIRTH_DATE_MONTH") + "' ";
			}
		}

		// ����
		if (this.getValueString("AGE_CB").equals("Y")) {
			if (StringUtils.isNotEmpty(this.getValueString("AGE_START"))) {
				sql = sql
						+ " AND  FLOOR (MONTHS_BETWEEN (SYSDATE, BIRTH_DATE) / 12) >= "
						+ this.getValueInt("AGE_START");
			}

			if (StringUtils.isNotEmpty(this.getValueString("AGE_END"))) {
				sql = sql
						+ " AND  FLOOR (MONTHS_BETWEEN (SYSDATE, BIRTH_DATE) / 12) <= "
						+ this.getValueInt("AGE_END");
			}
		}

		String sql2 = "WITH B AS ( "
				+ sql
				+ ")SELECT B.MR_NO,"
				+ "       B.PAT_NAME,"
				+ "       CASE B.SEX_type"
				+ "          WHEN 'M' THEN '��'"
				+ "          WHEN 'F' THEN 'Ů'"
				+ "          ELSE '����'"
				+ "       END"
				+ "          SEX_type,"
				+ "       FLOOR (MONTHS_BETWEEN (SYSDATE, B.BIRTH_DATE) / 12) age,"
				+ "       A.CASE_NO,"
				+ "       A.ADM_DATE, A.DISCHARGE_DATE, "
				+ "       CASE A.ADM_TYPE"
				+ "          WHEN 'O'"
				+ "          THEN"
				+ "             '����'"
				+ "          WHEN 'E'"
				+ "          THEN"
				+ "             '����'"
				+ "          WHEN 'H'"
				+ "          THEN"
				+ "             '����'"
				+ "          WHEN 'I'"
				+ "          THEN"
				+ "             (CASE"
				+ "                 WHEN A.DISCHARGE_DATE IS NULL THEN '��Ժ'"
				+ "                 ELSE 'סԺ'"
				+ "              END)"
				+ "          ELSE"
				+ "             NULL"
				+ "       END AS ADM_TYPE_DESC,"
				+ "          ADM_TYPE,"
				+ "       A.DEPT_DESC,"
				+ "       CASE A.ADM_TYPE"
				+ "          WHEN 'I' THEN A.STATION_DESC"
				+ "          ELSE A.CLINICAREA_DESC"
				+ "       END"
				+ "          STATION_OR_CLINICAREA,"
				+ "       CASE A.ADM_TYPE WHEN 'I' THEN A.BED_NO ELSE A.CLINICROOM_DESC END"
				+ "          BED_OR_CLINICROOM," + "       A.VS_DR_NAME,"
				+ "       A.NURSING_CLASS," + "       A.TRIAGE_LEVEL "
				+ "  FROM B, CRP_VISIT_RECORD A WHERE 1 = 1 ";
		
		// ����
		if (StringUtils.isNotEmpty(this.getValueString("REGION_CODE"))) {
			sql2 = sql2 + " AND A.REGION_CODE = '"
					+ this.getValueString("REGION_CODE") + "' ";
		}

		// ԤԼ����
		if (this.getValueString("PATIENT_RESERVE").equals("Y")) {
			sql2 = sql2 + " AND A.APPT_CODE = 'Y' ";
		}

		// �����ż��ﻼ�� ��
		if (this.getValueString("EMERGENCY_PATIENT").equals("Y")) {
			sql2 = sql2
					+ " AND A.ADM_TYPE IN ('O','E') AND TO_CHAR(A.ADM_DATE,'YYYY/MM/DD') = '"
					+ StringTool.getString(SystemTool.getInstance().getDate(),
							"yyyy/MM/dd") + "'";
		}

		// סԺ��Ժ���� ��
		if (this.getValueString("INPATIENT").equals("Y")) {
			sql2 = sql2 + " AND A.DISCHARGE_DATE IS NULL AND A.ADM_TYPE = 'I' ";
		}
		
		// ��ʼ��������
		if (this.getValue("ADM_DATE_START") != null) {
			sql2 = sql2
					+ " AND TO_CHAR(A.ADM_DATE,'YYYYMMDD') >= '"
					+ StringTool.getString(TypeTool.getTimestamp(this
							.getValue("ADM_DATE_START")), "yyyyMMdd") + "'";
		}
		
		// ��ֹ��������
		if (this.getValue("ADM_DATE_END") != null) {
			sql2 = sql2
					+ " AND TO_CHAR(A.ADM_DATE,'YYYYMMDD') <= '"
					+ StringTool.getString(TypeTool.getTimestamp(this
							.getValue("ADM_DATE_END")), "yyyyMMdd") + "'";
		}

		// ���3����
		if (this.getValueString("RIGHT_NOW").equals("Y")) {
			if (this.getValueString("RECENTLY").equals("0")) {
				sql2 = sql2 + " AND FLOOR(SYSDATE-A.ADM_DATE) <= 3 ";
			} else if (this.getValueString("RECENTLY").equals("1")) {// 5����
				sql2 = sql2 + " AND FLOOR(SYSDATE-A.ADM_DATE) <= 5 ";
			} else if (this.getValueString("RECENTLY").equals("2")) {// 7����
				sql2 = sql2 + " AND FLOOR(SYSDATE-A.ADM_DATE) <= 6 ";
			} else if (this.getValueString("RECENTLY").equals("3")) {// 10����
				sql2 = sql2 + " AND FLOOR(SYSDATE-A.ADM_DATE) <= 10 ";
			} else if (this.getValueString("RECENTLY").equals("4")) {// 15����
				sql2 = sql2 + " AND FLOOR(SYSDATE-A.ADM_DATE) <= 15 ";
			} else if (this.getValueString("RECENTLY").equals("5")) {// 1������
				sql2 = sql2 + " AND MONTHS_BETWEEN(SYSDATE,A.ADM_DATE) <= 1 ";
			} else if (this.getValueString("RECENTLY").equals("6")) {// 3������
				sql2 = sql2 + " AND MONTHS_BETWEEN(SYSDATE,A.ADM_DATE) <= 3 ";
			} else if (this.getValueString("RECENTLY").equals("7")) {// 6������
				sql2 = sql2 + " AND MONTHS_BETWEEN(SYSDATE,A.ADM_DATE) <= 6 ";
			} else if (this.getValueString("RECENTLY").equals("8")) {// 1����
				sql2 = sql2 + " AND MONTHS_BETWEEN(SYSDATE,A.ADM_DATE) <= 12 ";
			}
		}

		// ��������
		if (this.getValueString("ADM_TYPE_CB").equals("Y")) {
			if (StringUtils.isNotEmpty(this.getValueString("ADM_TYPE"))) {
				sql2 = sql2 + "  AND A.ADM_TYPE = '"
						+ this.getValueString("ADM_TYPE") + "' ";
			}
		}

		// �������
		if (this.getValueString("DEPT_CODE_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("DEPT_CODE"))) {
			sql2 = sql2 + " AND A.DEPT_CODE =  '"
					+ this.getValueString("DEPT_CODE") + "' ";
		}

		// ����
		if (this.getValueString("STATION_CODE_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("STATION_CODE"))) {
			sql2 = sql2 + "   AND A.STATION_CODE =  '"
					+ this.getValueString("STATION_CODE") + "' ";
		}

		// ����
		if (this.getValueString("CLINICAREA_CODE_CB").equals("Y")
				&& StringUtils.isNotEmpty(this
						.getValueString("CLINICAREA_CODE"))) {
			sql2 = sql2 + "  AND A.CLINICAREA_CODE = '"
					+ this.getValueString("CLINICAREA_CODE") + "' ";
		}

		// ����
		if (this.getValueString("BED_NO_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("BED_NO"))) {
			sql2 = sql2 + "  AND A.BED_CODE =  '" + this.getValueString("BED_NO")
					+ "' ";
		}

		// ����
		if (this.getValueString("CLINICROOM_NO_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("CLINICROOM_NO"))) {
			sql2 = sql2 + "  AND A.CLINICROOM_NO =  '"
					+ this.getValueString("CLINICROOM_NO") + "' ";
		}

		// ����ҽ��
		if (this.getValueString("VS_DR_CODE_CB").equals("Y")
				&& StringUtils.isNotEmpty(this.getValueString("VS_DR_CODE"))) {
			sql2 = sql2 + " AND A.VS_DR_CODE =  '"
					+ this.getValueString("VS_DR_CODE") + "' ";
		}

		// ����ҽ��
		if (this.getValueString("ATTEND_DR_CODE_CB").equals("Y")
				&& StringUtils
						.isNotEmpty(this.getValueString("ATTEND_DR_CODE"))) {
			sql2 = sql2 + "  AND A.ATTEND_DR_CODE = '"
					+ this.getValueString("ATTEND_DR_CODE") + "' ";
		}

		// ������
		if (this.getValueString("DIRECTOR_DR_CODE_CB").equals("Y")
				&& StringUtils.isNotEmpty(this
						.getValueString("DIRECTOR_DR_CODE"))) {
			sql2 = sql2 + " AND A.DIRECTOR_DR_CODE = '"
					+ this.getValueString("DIRECTOR_DR_CODE") + "' ";
		}

		// ���˷ּ�
		if (this.getValueString("ERD_LEVEL_CB").equals("Y")
				&& StringUtils.isNotEmpty(this
						.getValueString("TRIAGE_LEVEL_CB"))) {
			sql2 = sql2 + "  AND A.ERD_LEVEL = '"
					+ this.getValueString("TRIAGE_LEVEL") + "' ";
		}
		
		sql2 = sql2
				+ " AND A.MR_NO = B.MR_NO ORDER BY A.ADM_DATE DESC,B.MR_NO,CASE A.ADM_TYPE WHEN 'I' THEN 1 WHEN 'O' THEN 2 WHEN 'E' THEN 3 ELSE 4 END ";
		System.out.println("��������sql��"+sql2);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql2));
		if (parm.getErrCode() < 0) {
			this.messageBox("��ѯ����");
			err("ERR:" + parm.getErrCode() + parm.getErrText());
			return;
		}
		if (parm.getCount() <= 0) {
			this.messageBox("û�пɲ�ѯ������!"); 
			table.removeRowAll();
			return;
		} 
		
		table.setParmValue(parm);
	}

	/**
	 * �õ�TCheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * ��������
	 */
	public void onCxShow() {
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ��Ҫ�鿴��������");
			return;
		}

		TParm parm = table.getParmValue();
		String mrNo = parm.getValue("MR_NO", table.getSelectedRow());
		String caseNo = parm.getValue("CASE_NO", table.getSelectedRow());

		TParm result = queryPassword();
		String user_password = result.getValue("USER_PASSWORD", 0);
		String url = "http://" + getWebServicesIp() + "?userId="
				+ Operator.getID() + "&password=" + user_password + "&mrNo="
				+ mrNo + "&caseNo=" + caseNo;
		try {
			Runtime.getRuntime().exec(
					String.valueOf(String.valueOf((new StringBuffer(
							"cmd.exe /c start iexplore \"")).append(url)
							.append("\""))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TParm queryPassword() {
		String sql = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '"
				+ Operator.getID() + "' AND REGION_CODE = '"
				+ Operator.getRegion() + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * ��ȡ�����ļ�
	 * 
	 * @author suny
	 */
	public static TConfig getProp() {
		TConfig config = null;
		try {
			config = TConfig.getConfig("WEB-INF\\config\\system\\TConfig.x");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return config;
	}

	/**
	 * ��ȡ�����ļ��еĵ��Ӳ���������IP
	 * 
	 * @return
	 */
	public static String getWebServicesIp() {
		TConfig config = getProp();
		String url = config.getString("", "EMRIP");
		return url;
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.clearValue("PAT_NAME_CB;PAT_NAME;SEX_CODE_CB;BIRTH_DATE_CB;AGE_CB;BIRTH_DATE_YEAR;"
						+ "REGION_CODE;PATIENT_RESERVE;EMERGENCY_PATIENT;INPATIENT;ADM_DATE_START;"
						+ "RIGHT_NOW;ADM_TYPE_CB;DEPT_CODE_CB;STATION_CODE_CB;BED_NO_CB;VS_DR_CODE_CB;ATTEND_DR_CODE_CB;"
						+ "DIRECTOR_DR_CODE_CB;ERD_LEVEL_CB;STATION_CODE;DEPT_CODE;VS_DR_CODE;ATTEND_DR_CODE;DIRECTOR_DR_CODE;"
						+ "CLINICROOM_NO;BED_NO;CLINICAREA_CODE_CB;CLINICROOM_NO_CB;ADM_TYPE;CLINICAREA_CODE;SEX_CODE;ERD_LEVEL;"
						+ "BIRTH_DATE_YEAR;BIRTH_DATE_MONTH;AGE_START;AGE_END;MR_NO_CB;MR_NO;RECENTLY;ADM_DATE_END");
		table.setParmValue(new TParm());
//		Timestamp now = SystemTool.getInstance().getDate();
//		this.setValue("ADM_DATE_START", StringTool.rollDate(now, -7));
//		this.setValue("ADM_DATE_END", now);

		this.setValue("DEPT_CODE_CB", "Y");
		this.setValue("STATION_CODE_CB", "Y");
		this.setValue("VS_DR_CODE_CB", "Y");
//		callFunction("UI|ADM_DATE_START|setEnabled", true);
//		callFunction("UI|ADM_DATE_END|setEnabled", true);
		callFunction("UI|DEPT_CODE|setEnabled", true);
		callFunction("UI|STATION_CODE|setEnabled", true);
		callFunction("UI|VS_DR_CODE|setEnabled", true);
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("DEPT_CODE", Operator.getDept());
		this.setValue("STATION_CODE", Operator.getStation());
		this.setValue("VS_DR_CODE", Operator.getID());
	}

	/**
	 * �����Żس��¼�
	 */
	public void onMrNo() {
		String mrNo = this.getValueString("MR_NO").trim();
		if (mrNo.equals("")) {
			return;
		}
		mrNo = PatTool.getInstance().checkMrno(mrNo);
		this.setValue("MR_NO", mrNo);
		
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
			this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			this.setValue("MR_NO", pat.getMrNo());
		}	
		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		
	
	}

	/**
	 * �ۺϲ�ѯ
	 */
	public void onQuerySummaryInfo() {
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ��Ҫ�鿴��������");
			return;
		}
		TParm parm = table.getParmValue().getRow(selRow);

		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

        // ��ǰ�ȹرո�ҳ��
        tabbedPane.closePanel("CDR_SUMMARY_UI");
		// ���ۺϲ�ѯ����
		tabbedPane.openPanel("CDR_SUMMARY_UI",
				"%ROOT%\\config\\emr\\EMRCdrSummaryInfo.x", parm);
		TComponent component = (TComponent) callFunction(
				"UI|SYSTEM_TAB|findObject", "CDR_SUMMARY_UI");
		if (component != null) {
			tabbedPane.setSelectedComponent((Component) component);
			return;
		}
	}

	/**
	 * ���ξ����ѯ
	 */
	public void onQueryMedRecord() {
		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("��ѡ��Ҫ�鿴��������");
			return;
		}
		TParm parm = table.getParmValue().getRow(selRow);
		String panelUI = "";
		String configName = "";
		if (StringUtils.equals("I", parm.getValue("ADM_TYPE"))) {
			panelUI = "CDR_INP_MED_UI";
			configName = "%ROOT%\\config\\emr\\EMRCdrInpMedRecord.x";
		} else {
			panelUI = "CDR_OPD_MED_UI";
			configName = "%ROOT%\\config\\emr\\EMRCdrOpdMedRecord.x";
		}

		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

        // ��ǰ�ȹرո�ҳ��
        tabbedPane.closePanel(panelUI);
		// �򿪽���
		tabbedPane.openPanel(panelUI, configName, parm);
		TComponent component = (TComponent) callFunction(
				"UI|SYSTEM_TAB|findObject", panelUI);
		if (component != null) {
			tabbedPane.setSelectedComponent((Component) component);
			return; 
		}
	}
}
