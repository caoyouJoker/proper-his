package action.med;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

import jdo.med.MedSmsTool;
import jdo.sys.Operator;
import jdo.sys.OperatorTool;
import jdo.sys.SystemTool;
import jdo.util.XmlUtil;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:����action
 * </p>
 * 
 * <p>
 * Description:����action
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company:javahis
 * </p>
 * 
 * @author ԬС��
 * @version 1.0
 */
public class MedSmsAction extends TAction {

	/**
	 * ��������
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSave(TParm parm) {
		writerLog("\n\r");
		writerLog("��������----------------------------------------------------BEGION"
				+ SystemTool.getInstance().getDate());
		writerLog("parm--------:" + parm);
		TConnection connection = getConnection();
		TParm result = null;

		for (int i = 0; i < parm.getCount(); i++) {
			TParm parmRow = parm.getRow(i);

			/** �õ�Application_No */
			String applicationNo = parmRow.getValue("APPLICATION_NO");
			TParm applyParm = MedSmsTool.getInstance().getMedApply(
					applicationNo);
			String admType = applyParm.getValue("ADM_TYPE", 0);
			String caseNo = applyParm.getValue("CASE_NO", 0);
			String mrNo = "";

			String testitemCode = parmRow.getValue("TESTITEM_CODE");
			// �Ȳ�ѯһ����ȷ���Ƿ񱣴�
			TParm searchSmsParm = new TParm();
			searchSmsParm.setData("APPLICATION_NO", applicationNo);
			searchSmsParm.setData("CASE_NO", caseNo);
			searchSmsParm.setData("TESTITEM_CODE", testitemCode);
			TParm medParm = MedSmsTool.getInstance().onQueryMedSms(
					searchSmsParm);

			if (medParm.getCount() > 0) {
				writerLog("�ظ���������--------:" + searchSmsParm);
			} else {
				/** ����ҽ�� */
				TParm telParm = new TParm();
				String deptCode = "";
				if ("E".equals(admType)) {
					telParm = MedSmsTool.getInstance().getRealdrCode(admType,
							caseNo);
					parmRow.setData("BILLING_DOCTORS", telParm.getValue(
							"REALDR_CODE", 0));
					deptCode = telParm.getValue("DEPT_CODE", 0);
					mrNo = telParm.getValue("MR_NO", 0);
				} else if ("I".equals(admType)) {
					telParm = MedSmsTool.getInstance().getVsDrCode(caseNo);
					parmRow.setData("BILLING_DOCTORS", telParm.getValue(
							"VS_DR_CODE", 0));
					deptCode = telParm.getValue("DEPT_CODE", 0);
					mrNo = telParm.getValue("MR_NO", 0);
					// add by wangb 2015/06/18 ϵͳ���� #1824 Σ��ֵ������Ϣ���� START
					// ����ҽʦ�뿪��ҽʦ��ͬʱ���ӿ���ҽʦΪ���Ž�����
					if (!StringUtils.equals(telParm.getValue("VS_DR_CODE", 0),
							applyParm.getValue("ORDER_DR_CODE", 0))) {
						String sql = "SELECT TEL1 FROM SYS_OPERATOR WHERE USER_ID = '" + applyParm.getValue("ORDER_DR_CODE", 0) + "'";
						TParm telParmOrder = new TParm(TJDODBTool.getInstance().select(sql));
						if (StringUtils.isNotEmpty(telParmOrder.getValue("TEL1", 0))) {
							telParm.addData("TEL1", telParmOrder.getValue("TEL1", 0));
						}
					}
					// add by wangb 2015/06/18 ϵͳ���� #1824 Σ��ֵ������Ϣ���� END
				} else if ("O".equals(admType)) {
					TParm telParmBill = MedSmsTool.getInstance().getRealdrCode(
							admType, caseNo);
					deptCode = telParmBill.getValue("DEPT_CODE", 0);
					String sql = " SELECT DUTY_TEL AS TEL1 FROM SYS_DEPT WHERE DEPT_CODE='020101'";
					TParm telParmDuty = new TParm(TJDODBTool.getInstance().select(sql));
					telParm.setData("REALDR_CODE", telParmBill.getValue(
							"REALDR_CODE", 0));
					telParm.addData("TEL1", telParmBill.getValue("TEL1"));
					if (null != telParmDuty.getValue("TEL1")
							&& telParmDuty.getValue("TEL1").length() > 0)
						telParm.addData("TEL1", telParmDuty.getValue("TEL1"));
					telParm.setData("DEPT_CODE", deptCode);
					telParm.setData("MR_NO", telParmBill.getValue("MR_NO"));

					parmRow.setData("BILLING_DOCTORS", telParmBill.getValue(
							"REALDR_CODE", 0));
					mrNo = telParmBill.getValue("MR_NO", 0);
					// add by wangb 2017/8/24 ���������ҵ�Σ��ֵ����
				} else if ("H".equals(admType)) {
					parmRow.setData("BILLING_DOCTORS", applyParm.getValue(
							"ORDER_DR_CODE", 0));
					mrNo = applyParm.getValue("MR_NO", 0);
					deptCode = applyParm.getValue("DEPT_CODE", 0);
					// ��ѯ����ҽ���ֻ�
					TParm drPhoneResult = MedSmsTool.getInstance().getDrPhone(
							applyParm.getValue("ORDER_DR_CODE", 0));
					if (drPhoneResult.getCount() > 0
							&& StringUtils.isNotEmpty(drPhoneResult.getValue(
									"TEL1", 0))) {
						telParm.setData("TEL1", drPhoneResult.getValue("TEL1",
								0));
					}
					telParm.setData("MR_NO", mrNo);
					telParm.setCount(1);
				}

				String deptChnDesc = "";
				if (deptCode != null && !deptCode.equals("")) {
					TParm searchParm = new TParm();
					searchParm.setData("DEPT_CODE", deptCode);
					TParm dutyParm = MedSmsTool.getInstance().getDutyTel(
							searchParm);
					if (dutyParm.getCount() > 0) {
						if ("I".equals(admType) || "E".equals(admType)
								|| "H".equals(admType)) {
							int telParmCount = telParm.getCount();
							telParm.setData("TEL1", telParmCount + 1, dutyParm
									.getRow(0).getValue("DUTY_TEL"));
							telParm.setCount(telParmCount + 1);
						}

						// ������������
						deptChnDesc = dutyParm.getRow(0).getValue(
								"DEPT_CHN_DESC");
						parmRow.setData("DEPT_CHN_DESC", deptChnDesc);
					}
				}

				parmRow.setData("STATION_CODE", applyParm.getValue(
						"STATION_CODE", 0));
				parmRow.setData("BED_NO", applyParm.getValue("BED_NO", 0));
				parmRow.setData("MR_NO", mrNo);
				parmRow.setData("DEPT_CODE", deptCode);
				parmRow.setData("IPD_NO", applyParm.getValue("IPD_NO", 0));
				parmRow.setData("CASE_NO", caseNo);
				parmRow.setData("ADM_TYPE", admType);
				parmRow.setData("STATE", "1");
				parmRow.setData("OPT_USER", Operator.getID());
				parmRow.setData("OPT_TERM", Operator.getIP());

				String content = parmRow.getValue("TESTITEM_CHN_DESC") + ","
						+ parmRow.getValue("TEST_VALUE") + ","
						+ parmRow.getValue("CRTCLLWLMT");
				parmRow.setData("SMS_CONTENT", content);

				// ���ݺ�̨����ȡ�ÿ�������
				String sql = " SELECT PERSON_CODE FROM MED_SMSDEPT_SETUP "
						+ " WHERE COMPETENT_TYPE = '3' AND DEPT_CODE = '"
						+ deptCode + "'";
				TParm directorParm = new TParm(TJDODBTool.getInstance().select(
						sql));
				String director = directorParm.getValue("PERSON_CODE", 0);
				parmRow.setData("DIRECTOR_DR_CODE", director);

				/** ҽ������ܣ�����Ժ�� */
				TParm competentParm = MedSmsTool.getInstance()
						.getDeanOrCompementTel(deptCode, "2");
				TParm deanParm = MedSmsTool.getInstance()
						.getDeanOrCompementTel(deptCode, "1");
				parmRow.setData("COMPETENT_CODE", competentParm.getValue(
						"USER_ID", 0));
				parmRow.setData("DEAN_CODE", deanParm.getValue("USER_ID", 0));
				
				//=== add by wukai 
				parmRow.setData("REPORT_USER", "");
				parmRow.setData("REPORT_DEPT_CODE", "");
				
				result = MedSmsTool.getInstance().insertMedSms(parmRow,
						connection);

				if (result.getErrCode() < 0) {
					connection.rollback();
					connection.close();
					return result;
					/** ������ɹ�����д��һ�� */
				}
				
				// add by wangbin 20140723 Σ��ֵ������������ҽʦ�ͱ���ʱ�� start
				// ����ҽ��
				String applyDoctor = OperatorTool.getInstance().getOperatorName(applyParm.getValue("ORDER_DR_CODE", 0));
				parmRow.setData("APPLY_DOCTOR", applyDoctor);
				
				// ����ʱ��
				Timestamp reportTs = StringTool.getTimestamp(parmRow.getValue("REPORT_TIME"), "yyyyMMddHHmmss");
				String reportDate = StringTool.getString(reportTs, "yyyy��MM��dd�� HHʱmm��");
				parmRow.setData("REPORT_DATE", reportDate);
				// add by wangbin 20140723 Σ��ֵ������������ҽʦ�ͱ���ʱ�� end

				writeXml(parmRow, telParm, content);
			}
		}
		writerLog("��������----------------------------------------------------END"
				+ SystemTool.getInstance().getDate());
		connection.commit();
		connection.close();
		return result;
	}
	
	private void writeXml(TParm parmRow, TParm telParm, String content) {
		// д�ļ�
		TParm xmlParm = new TParm();
		xmlParm.setData("Content", content);
		// System.out.println("writeXml  mrno="+telParm.getValue("MR_NO"));
		// xmlParm.setData("MrNo",telParm.getValue("MR_NO", 0).indexOf("[") > 0
		// ?
		// telParm.getValue("MR_NO", 0).replace("[", "").replace("]", "") :
		// telParm.getValue("MR_NO", 0));

		xmlParm.setData("MrNo", telParm.getValue("MR_NO").replace("[", "")
				.replace("]", ""));
		// �õ�����,�ż�ס���
		String deptChnCode = parmRow.getValue("DEPT_CHN_DESC");
		String admType = parmRow.getValue("ADM_TYPE");
		String admTypeChn = "";
		admTypeChn = getAdmType(admType);

		xmlParm.setData("Name", parmRow.getValue("PAT_NAME") + ","
				+ deptChnCode + "," + admTypeChn);
		// System.out.println("xmlParm:"+xmlParm);
		// System.out.println("telParm:"+telParm);
		xmlParm.setData("SysNo", "MED");
		
		// add by wangbin Σ��ֵ������������ҽʦ�ͱ���ʱ�� 20140710 start
		// ����ҽ��
		xmlParm.setData("ApplyDoctor", parmRow.getValue("APPLY_DOCTOR"));
		//����ʱ��
		xmlParm.setData("ReportDate", parmRow.getValue("REPORT_DATE"));
		// add by wangbin Σ��ֵ������������ҽʦ�ͱ���ʱ�� 20140710 end
		
		XmlUtil.createSmsFile(xmlParm, telParm);
	}

	private String getAdmType(String admType) {
		String admTypeChn = "";
		if (admType != null) {
			if (admType.equals("O")) {
				admTypeChn = "����";
			} else if (admType.equals("I")) {
				admTypeChn = "סԺ";
			} else if (admType.equals("E")) {
				admTypeChn = "����";
			} else if (admType.equals("H")) {
				admTypeChn = "�������";
			}
		}
		return admTypeChn;
	}

	public static void writerLog(String msg) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		String name = "MED_SMS";
		name += format.format(new Date());
		File f = new File("C:\\JavaHis\\logs\\" + name + ".log");
		BufferedWriter out = null;
		try {
			if (!f.exists()) {
				f.createNewFile();// ���MED_SMS.log�����ڣ��򴴽�һ�����ļ�
			}
			out = new BufferedWriter(new FileWriter(f, true));// ����true��ʾ�����׷�ӵ��ļ����ݵ�ĩβ��������ԭ��������
			out.write(msg);
			out.newLine(); // ����
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * �������� ----- ���ֹ�¼��
	 * 
	 * @param parm
	 * @return
	 */
	public TParm onSaveBySelf(TParm parm) {

		TConnection connection = getConnection();

		String mrNo = parm.getValue("MR_NO");
		// System.out.println("action mrno=" + mrNo);
		String deptCode = parm.getValue("DEPT_CODE");
		String billDoc = parm.getValue("BILLING_DOCTORS");
		String admType = parm.getValue("ADM_TYPE");
		String deptChnDesc = "";

		parm.setData("APPLICATION_NO", "");
		parm.setData("TESTITEM_CODE", "");
		parm.setData("STATE", "1");

		TParm telParm = new TParm();
		TParm result = new TParm();

		// ���ݺ�̨����ȡ�ÿ�������
		String sql = " SELECT PERSON_CODE FROM MED_SMSDEPT_SETUP "
				+ " WHERE COMPETENT_TYPE = '3' AND DEPT_CODE = '" + deptCode
				+ "'";
		TParm directorParm = new TParm(TJDODBTool.getInstance().select(sql));
		String director = directorParm.getValue("PERSON_CODE", 0);
		parm.setData("DIRECTOR_DR_CODE", director);

		String content = parm.getValue("TESTITEM_CHN_DESC") + ","
				+ parm.getValue("TEST_VALUE") + ","
				+ parm.getValue("CRTCLLWLMT");
		parm.setData("SMS_CONTENT", content);

		/** ҽ������ܣ�����Ժ�� */
		TParm competentParm = MedSmsTool.getInstance().getDeanOrCompementTel(
				deptCode, "2");
		TParm deanParm = MedSmsTool.getInstance().getDeanOrCompementTel(
				deptCode, "1");

		parm.setData("COMPETENT_CODE", competentParm.getValue("USER_ID", 0));
		parm.setData("DEAN_CODE", deanParm.getValue("USER_ID", 0));

		if (admType.equals("O")) {
			TParm telParmBill = MedSmsTool.getInstance().getDrPhone(billDoc);
			String sqlDuty = " SELECT DUTY_TEL AS TEL1 FROM SYS_DEPT WHERE DEPT_CODE='020101'";
			TParm telParmDuty = new TParm(TJDODBTool.getInstance().select(sqlDuty));
			telParm.setData("REALDR_CODE", billDoc);
			telParm.addData("TEL1", telParmBill.getValue("TEL1"));
			if (null != telParmDuty.getValue("TEL1")
					&& telParmDuty.getValue("TEL1").length() > 0)
				telParm.addData("TEL1", telParmDuty.getValue("TEL1"));
			telParm.setData("DEPT_CODE", deptCode);
			telParm.setData("MR_NO", mrNo);
		} else {
			telParm = MedSmsTool.getInstance().getDrPhone(billDoc);
			telParm.setData("REALDR_CODE", billDoc);
			telParm.setData("DEPT_CODE", deptCode);
			telParm.setData("MR_NO", mrNo);
		}
		
		if (deptCode != null && !deptCode.equals("")) {
			TParm searchParm = new TParm();
			searchParm.setData("DEPT_CODE", deptCode);
			TParm dutyParm = MedSmsTool.getInstance().getDutyTel(searchParm);
			if (dutyParm.getCount() > 0) {
				if ("I".equals(admType) || "E".equals(admType)) {
					int telParmCount = telParm.getCount();
					telParm.setData("TEL1", telParmCount + 1, dutyParm
							.getRow(0).getValue("DUTY_TEL"));
					telParm.setCount(telParmCount + 1);
				}

				// ������������
				deptChnDesc = dutyParm.getRow(0).getValue("DEPT_CHN_DESC");
				parm.setData("DEPT_CHN_DESC", deptChnDesc);
			}
		}

		result = MedSmsTool.getInstance().insertMedSms(parm);
		if (result.getErrCode() < 0) {
			connection.rollback();
			connection.close();
			return result;
		}
		
		// add by wangbin 20140710 Σ��ֵ������������ҽʦ�ͱ���ʱ�� start
		// ����ҽ��
		String applyDoctor = OperatorTool.getInstance().getOperatorName(billDoc);
		parm.setData("APPLY_DOCTOR", applyDoctor);
		
		// ����ʱ��
		String reportDate = StringTool.getString(SystemTool.getInstance().getDate(), "yyyy��MM��dd�� HHʱmm��");
		parm.setData("REPORT_DATE", reportDate);
		// add by wangbin 20140710 Σ��ֵ������������ҽʦ�ͱ���ʱ�� end

		writeXml(parm, telParm, content);

		connection.commit();
		connection.close();
		return result;
	}
}
