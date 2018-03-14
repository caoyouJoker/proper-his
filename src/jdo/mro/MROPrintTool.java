package jdo.mro;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jdo.adm.ADMChildImmunityTool;
import jdo.adm.ADMTransLogTool;
import jdo.emr.EMRCreateXMLTool;
import jdo.sum.SUMNewArrivalTool;
import jdo.sys.Operator;
import jdo.sys.Pat;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TDataStore;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.manager.TCM_Transform;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.manager.sysfee.sysOdrPackDObserver;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ��ҳ��ӡTool
 * </p>
 * 
 * <p>
 * Description: ��ҳ��ӡTool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangk 2009-9-20
 * @version 4.0
 */
public class MROPrintTool extends TJDOTool {
	TDataStore ICD_DATA = TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
	TDataStore DICTIONARY;
	Map drList;
	/**
	 * ʵ��
	 */
	public static MROPrintTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RegMethodTool
	 */
	public static MROPrintTool getInstance() {
		if (instanceObject == null)
			instanceObject = new MROPrintTool();
		return instanceObject;
	}

	public MROPrintTool() {
		drList = getDrList();
		getDICTIONARY();
		// DICTIONARY.showDebug();
	}

	/**
	 * ��ȡ��ҳ��ӡ��Ϣ���ϳ���
	 * 
	 * @param CASE_NO
	 *            String
	 * @return TParm
	 */
	public TParm getMroRecordPrintData(String CASE_NO) {
		// ��ȡĳһ��������ҳ��Ϣ
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		// �жϸò����Ƿ��ǲ���
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// ��ȡ����������Ϣ ��ʾ��ĸ�׵Ĳ�����ҳ��
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// ��ѯ����������ҳ��Ϣ
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		// ��ѯ������Ϣ
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		DecimalFormat df = new DecimalFormat("0.00");
		// �����ӡ����
		TParm data = new TParm();
		// ҳü��Ϣ
		data.setData("head_mr_no", "TEXT", print.getValue("MR_NO", 0));// MR_NO
		data.setData("head_ipd_no", "TEXT", print.getValue("IPD_NO", 0));// IPD_NO
		// data.setData("CTZ1","TEXT",getDesc("SYS_CTZ","","CTZ_DESC","CTZ_CODE",print.getValue("CTZ1_CODE",0)));//���1//CTZ1
		// S.13
		/**
		 * add 20120113
		 */
		data.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// ������ҳ���
		data.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// ҽԺ����
		data.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// ��֯��������
		data.setData("NHI_NO", "TEXT", print.getValue("NHI_NO", 0));// �������� ҽ����
		TParm sumParm = new TParm();
		sumParm.setData("CASE_NO", CASE_NO);
		sumParm.setData("ADM_TYPE", "I");
		data.setData("NB_ADM_WEIGHT", "TEXT", SUMNewArrivalTool.getInstance()
				.getNewAdmWeight(sumParm));// ��������Ժ����
		data.setData("NB_WEIGHT", "TEXT", SUMNewArrivalTool.getInstance()
				.getNewBornWeight(sumParm));// ��������������
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		data.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 7 ? birthplace.substring(0, 7)
						: birthplace);// //����

		data.setData("ADDRESS", "TEXT", print.getValue("ADDRESS", 0));// ͨ�ŵ�ַ
		// HR03.00.005
		// H.05
		data.setData("POST_NO", "TEXT", print.getValue("POST_NO", 0));// ͨ���ʱ�

		data.setData("TEL", "TEXT", print.getValue("TEL", 0));// ͨ���ʱ�

		// �������HR56.00.002.05 CDAֵ
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		data.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		data.setData("IN_COUNT", "TEXT", "��" + print.getValue("IN_COUNT", 0)
				+ "��סԺ");// סԺ����
		// ����������Ϣ
		data.setData("HR02.01.001.01", "TEXT", print.getValue("PAT_NAME", 0));// ����
		data.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// �Ա�
																	// HR02.02.001
																	// H.03
		// �����Ա�CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		data.setData("HR02.02.001", "TEXT", sexCDAValue);
		data.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy��MM��dd��"));// ��������
																		// HR30.00.001
																		// H.03
		data.setData("AGE", "TEXT", print.getValue("AGE", 0));// ���� HR02.03.001
																// H.03
		data.setData("MARRIGE", "TEXT", print.getValue("MARRIGE", 0));// ����
																		// HR02.06.003
																		// H.03
		// ���û���CDA����
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		data.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		data.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// ְҵ HR02.07.011.02 H.03
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		data.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 7 ? HOEMPLACE.substring(0, 7) : HOEMPLACE);// ������
																				// ������Ҫת��
																				// HR30.00.005
																				// H.03
		data.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// ����
																				// HR02.05.001
																				// H.03
		data.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// ����
																				// HR02.04.001
																				// H.03
		data.setData("IDNO", "TEXT", print.getValue("IDNO", 0));// ���֤
																// HR01.01.002.02
																// H.02
		data.setData("OFFICE", "TEXT", print.getValue("OFFICE", 0));// ������λ
																	// HR02.07.006
																	// H.05
		data.setData("O_ADDRESS", "TEXT", print.getValue("O_ADDRESS", 0));// ��λ��ַ
																			// HR03.00.003
																			// H.05
		data.setData("O_TEL", "TEXT", print.getValue("O_TEL", 0));// ��λ�绰
																	// HR04.00.001.03
																	// H.06
		data.setData("O_POSTNO", "TEXT", print.getValue("O_POSTNO", 0));// ��λ�ʱ�
																		// HR03.00.005
																		// H.05
		data.setData("H_ADDRESS", "TEXT", print.getValue("H_ADDRESS", 0));// ���ڵ�ַ
																			// HR03.00.004.02
																			// H.05
		data.setData("H_POSTNO", "TEXT", print.getValue("H_POSTNO", 0));// �����ʱ�
																		// HR03.00.005
																		// H.05
		// HR03.00.005
		// H.05
		data.setData("CONTACTER", "TEXT", print.getValue("CONTACTER", 0));// ��ϵ������
																			// HR02.01.002
																			// H.02
		data.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// ��ϵ�˹�ϵ HR02.18.004
		data.setData("CONT_ADDRESS", "TEXT", print.getValue("CONT_ADDRESS", 0));// ��ϵ�˵�ַ
																				// HR03.00.004.02
																				// H.05
		data.setData("CONT_TEL", "TEXT", print.getValue("CONT_TEL", 0));// ��ϵ�˵绰
																		// HR04.00.001.03
																		// H.06
		data.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy��MM��dd�� HHʱ"));// ��Ժ����
																		// HR00.00.001.06
																		// H.01
		data.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// ��Ժ�Ʊ� HR21.01.100.05
														// H.08
		data.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
						print.getValue("IN_ROOM_NO", 0)));// ��Ժ���� --------�ޱ���

		data.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(getTranDept(CASE_NO)));// ת�ƿƱ� wanglong
															// modify 20150115
															// HR42.03.100
															// H.10
		data.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy��MM��dd�� HHʱ"));// ��Ժ����
																		// HR42.02.201
																		// H.10
		if (print.getData("OUT_DATE", 0) != null) {// �ж��Ƿ��Ժ
			data.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // ��Ժ���� HR42.03.100
																// H.10
			data.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_ROOM", "", "ROOM_DESC", "ROOM_CODE",
							print.getValue("OUT_ROOM_NO", 0))); // ��Ժ����
		} else {
			data.setData("OUT_DEPT", "TEXT", "");
			data.setData("OUT_ROOM", "TEXT", "");
		}
		data.setData("	", "TEXT", print.getValue("REAL_STAY_DAYS", 0));// ʵ��סԺ����
																		// HR52.02.103
																		// S.12
		data.setData("IN_CONDITION", "TEXT", print.getValue("IN_CONDITION", 0)
				.replace("0", ""));// ��Ժ���
		// ������Ժ���CDA����
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		data.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		data.setData("CONFIRM_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CONFIRM_DATE", 0), "yyyy��MM��dd��"));// ȷ������
																		// HR42.02.202
																		// S.11.002
																		// ʵ��Ҫ��������Ϊ��λ
		/**
		 * add 20120113
		 */
		data.setData("ADM_SOURCE", "TEXT",
				getNewadmSource(print.getValue("ADM_SOURCE", 0)));// ������Դ
		data.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// ��Ժ��ʽ
		data.setData("TRAN_HOSP", "TEXT", print.getValue("TRAN_HOSP", 0));// ��תԺ��
		data.setData("BE_COMA_TIME", "TEXT", print.getValue("BE_COMA_TIME", 0)
				.substring(0, 2)
				+ "��"
				+ print.getValue("BE_COMA_TIME", 0).substring(2, 4)
				+ "Сʱ"
				+ print.getValue("BE_COMA_TIME", 0).substring(4, 6) + "����");// ��Ժǰ����ʱ��
		data.setData("AF_COMA_TIME", "TEXT", print.getValue("AF_COMA_TIME", 0)
				.substring(0, 2)
				+ "��"
				+ print.getValue("AF_COMA_TIME", 0).substring(2, 4)
				+ "Сʱ"
				+ print.getValue("AF_COMA_TIME", 0).substring(4, 6) + "����");// ��Ժ�����ʱ��
		data.setData("VS_NURSE_CODE", "TEXT",
				print.getValue("VS_NURSE_CODE", 0));// ���λ�ʿ
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		data.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31��ƻ����
		data.setData("AGN_PLAN_INTENTION", "TEXT",
				print.getValue("AGN_PLAN_INTENTION", 0));// 31��ƻ�ԭ��

		String OE_DIAG_CODE = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// ��ϴ��룺���������룩HR55.02.057.05
																// S.07
																// ���������ƣ�HR55.02.057.04
																// S.07
			OE_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		}
		if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
			OE_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		}
		data.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// �ż������
		String IN_DIAG_CODE = getICD_DESC(print.getValue("IN_DIAG_CODE", 0));
		if (print.getValue("IN_DIAG_CODE2", 0).length() > 0) {
			IN_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("IN_DIAG_CODE2", 0));
		}
		if (print.getValue("IN_DIAG_CODE3", 0).length() > 0) {
			IN_DIAG_CODE += ","
					+ getICD_DESC(print.getValue("IN_DIAG_CODE3", 0));
		}
		data.setData("IN_DIAG_CODE", "TEXT", IN_DIAG_CODE);// ��Ժ���
															// HR55.02.057.04
															// S.07
		/***** ��Ժ��ϲ�����Ϣ *******************/
		// ����������� ��ô��д���������
		if (child.getCount("CASE_NO") > 0) {
			data.setData("C_OUT_DIAG_CODE1", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE1", 0))); // ��Ժ�����
			data.setData("C_OUT_DIAG_CODE2", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE2", 0))); // ��Ժ���2
			data.setData("C_OUT_DIAG_CODE3", "TEXT",
					getICD_DESC(childDiag.getValue("OUT_DIAG_CODE3", 0))); // ��Ժ���3

			data.setData("C_OUT1_" + childDiag.getValue("CODE1_STATUS", 0),
					"TEXT", "��"); // ��Ժ����� ת��
			data.setData("C_OUT2_" + childDiag.getValue("CODE2_STATUS", 0),
					"TEXT", "��"); // ��Ժ���2 ת��
			data.setData("C_OUT3_" + childDiag.getValue("CODE3_STATUS", 0),
					"TEXT", "��"); // ��Ժ���3 ת��

			data.setData("C_OUT1_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE1", 0)); // ��Ժ�����
			data.setData("C_OUT2_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE2", 0)); // ��Ժ���2
			data.setData("C_OUT3_ICD", "TEXT",
					childDiag.getValue("OUT_DIAG_CODE3", 0)); // ��Ժ���3
		}
		data.setData("OUT_DIAG_CODE1", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // ��Ժ�����
		data.setData("OUT_DIAG_CODE2", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // ��Ժ���2
		data.setData("OUT_DIAG_CODE3", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // ��Ժ���3
		data.setData("OUT_DIAG_CODE4", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // ��Ժ���4
		data.setData("OUT_DIAG_CODE5", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // ��Ժ���5
		data.setData("OUT_DIAG_CODE6", "TEXT",
				getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // ��Ժ���6
		data.setData("OUT1_" + print.getValue("CODE1_STATUS", 0), "TEXT", "��"); // ��Ժ�����
																				// ת��
		data.setData("OUT2_" + print.getValue("CODE2_STATUS", 0), "TEXT", "��"); // ��Ժ���2
																				// ת��
		data.setData("OUT3_" + print.getValue("CODE3_STATUS", 0), "TEXT", "��"); // ��Ժ���3
																				// ת��
		data.setData("OUT4_" + print.getValue("CODE4_STATUS", 0), "TEXT", "��"); // ��Ժ���4
																				// ת��
		data.setData("OUT5_" + print.getValue("CODE5_STATUS", 0), "TEXT", "��"); // ��Ժ���5
																				// ת��
		data.setData("OUT6_" + print.getValue("CODE6_STATUS", 0), "TEXT", "��"); // ��Ժ���6
																				// ת��
		data.setData("OUT1_ICD", "TEXT", print.getValue("OUT_DIAG_CODE1", 0)); // ��Ժ�����
		data.setData("OUT2_ICD", "TEXT", print.getValue("OUT_DIAG_CODE2", 0)); // ��Ժ���2
		data.setData("OUT3_ICD", "TEXT", print.getValue("OUT_DIAG_CODE3", 0)); // ��Ժ���3
		data.setData("OUT4_ICD", "TEXT", print.getValue("OUT_DIAG_CODE4", 0)); // ��Ժ���4
		data.setData("OUT5_ICD", "TEXT", print.getValue("OUT_DIAG_CODE5", 0)); // ��Ժ���5
		data.setData("OUT6_ICD", "TEXT", print.getValue("OUT_DIAG_CODE6", 0)); // ��Ժ���6
		/****** ҽʦ���� *********/
		String INTE_DIAG_CODE = getICD_DESC(print.getValue("INTE_DIAG_CODE", 0));
		data.setData("INTE_DIAG_CODE", "TEXT",
				INTE_DIAG_CODE.length() == 0 ? "��" : INTE_DIAG_CODE);// Ժ�ڸ�Ⱦ���
		data.setData("INTE_ICD", "TEXT", print.getValue("INTE_DIAG_CODE", 0));// Ժ�ڸ�Ⱦ���CODE
		data.setData("INTE_STATUS" + print.getValue("INTE_DIAG_STATUS", 0),
				"TEXT", "��");// Ժ�ڸ�Ⱦת��
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		data.setData("PATHOLOGY_DIAG", "TEXT",
				PATHOLOGY_DIAG.length() == 0 ? "��" : PATHOLOGY_DIAG);// �������

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		data.setData("EX_RSN", "TEXT", EX_RSN.length() == 0 ? "��" : EX_RSN);// ���ˡ��ж����ⲿ����
		data.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// ҩ�����
		data.setData("HBsAg", "TEXT", print.getValue("HBSAG", 0));// HBSAG
		// ����HBsAgHRCDA����
		String HBsAgHRCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HBSAG", 0));
		data.setData("HBsAgHR51.99.004.08", "TEXT", HBsAgHRCDAValue);
		data.setData("HCV-Ab", "TEXT", print.getValue("HCV_AB", 0));// HCV_AB
		// ����HCV_ABCDA����
		String HCV_ABCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HCV-Ab", 0));
		data.setData("HCV-AbHR51.99.004.08", "TEXT", HCV_ABCDAValue);
		data.setData("HIV-Ab", "TEXT", print.getValue("HIV_AB", 0));// HIV_AB
		// ����HIV-AbCDA����
		String HIV_AbCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.02", "HR51.99.004.08", print.getValue("HCV-Ab", 0));
		data.setData("HIV-AbHR51.99.004.08", "TEXT", HIV_AbCDAValue);
		data.setData("QUYCHK_OI", "TEXT", print.getValue("QUYCHK_OI", 0));// ������סԺ
		// ����������סԺ����
		String QUYCHK_OICDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_OI", 0));
		data.setData("QUYCHK_OIHR55.01.045.02", "TEXT", QUYCHK_OICDAValue);
		data.setData("QUYCHK_INOUT", "TEXT", print.getValue("QUYCHK_INOUT", 0));// ��Ժ���Ժ
		// ������Ժ���Ժ����
		String QUYCHK_INOUTCDAValue = EMRCreateXMLTool.getInstance()
				.getCDACode("S.11.002", "HR55.01.045.02",
						print.getValue("QUYCHK_INOUT", 0));
		data.setData("QUYCHK_INOUTHR55.01.045.02", "TEXT", QUYCHK_INOUTCDAValue);
		data.setData("QUYCHK_OPBFAF", "TEXT",
				print.getValue("QUYCHK_OPBFAF", 0));// ��ǰ����
		// ������ǰ���������
		String QUYCHK_OPBFAFCDAValue = EMRCreateXMLTool.getInstance()
				.getCDACode("S.11.002", "HR55.01.045.02",
						print.getValue("QUYCHK_OPBFAF", 0));
		data.setData("QUYCHK_OPBFAFHR55.01.045.02", "TEXT",
				QUYCHK_OPBFAFCDAValue);
		data.setData("QUYCHK_CLPA", "TEXT", print.getValue("QUYCHK_CLPA", 0));// �ٴ��벡��
		// �����ٴ��벡��
		String QUYCHK_CLPACDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_CLPA", 0));
		data.setData("QUYCHK_CLPAHR55.01.045.02", "TEXT", QUYCHK_CLPACDAValue);
		data.setData("QUYCHK_RAPA", "TEXT", print.getValue("QUYCHK_RAPA", 0));// �����벡��
		// ���÷����벡��
		String QUYCHK_RAPACDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.045.02", print.getValue("QUYCHK_RAPA", 0));
		data.setData("QUYCHK_RAPAHR55.01.045.02", "TEXT", QUYCHK_RAPACDAValue);
		data.setData("GET_TIMES", "TEXT", print.getValue("GET_TIMES", 0));// ���ȴ���
		data.setData("SUCCESS_TIMES", "TEXT",
				print.getValue("SUCCESS_TIMES", 0));// �ɹ�����
		data.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// ������
		data.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// ����ҽʦ
		data.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// ����ҽʦ
		data.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// סԺҽʦ
		data.setData("INDUCATION_DR_CODE", "TEXT",
				drList.get(print.getValue("INDUCATION_DR_CODE", 0)));// ����ҽʦ
		data.setData("GRADUATE_INTERN_CODE", "TEXT",
				drList.get(print.getValue("GRADUATE_INTERN_CODE", 0)));// �о���ʵϰҽʦ
		data.setData("INTERN_DR_CODE", "TEXT",
				drList.get(print.getValue("INTERN_DR_CODE", 0)));// ʵϰҽʦ
		data.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// ����Ա
		data.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// ��������
		// ���ò�������
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		data.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		data.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// �ʿ�ҽʦ
		data.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// �ʿػ�ʿ
		data.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy��MM��dd��"));// �ʿ�����
		/***** ���ò��� **********/
		data.setData("INFECT_REPORT", "TEXT",
				print.getValue("INFECT_REPORT", 0));// ��Ⱦ������
		data.setData("DIS_REPORT", "TEXT", print.getValue("DIS_REPORT", 0));// �Ĳ�����
		data.setData("BODY_CHECK", "TEXT", print.getValue("BODY_CHECK", 0));// ʬ��
		data.setData("FIRST_CASE", "TEXT", print.getValue("FIRST_CASE", 0));// ����
		// �������� �� �� �� �ж��Ƿ�����
		if (print.getValue("ACCOMPANY_WEEK", 0).length() > 0
				|| print.getValue("ACCOMPANY_MONTH", 0).length() > 0
				|| print.getValue("ACCOMPANY_YEAR", 0).length() > 0)
			data.setData("ACCOMPANY", "TEXT", "1");// ����
		else
			data.setData("ACCOMPANY", "TEXT", "2");
		data.setData(
				"ACCOMPANY_DATE",
				"TEXT",
				print.getValue("ACCOMPANY_WEEK", 0) + "��"
						+ print.getValue("ACCOMPANY_MONTH", 0) + "��"
						+ print.getValue("ACCOMPANY_YEAR", 0) + "��");// ��������
		data.setData("SAMPLE_FLG", "TEXT", print.getValue("SAMPLE_FLG", 0));// ʾ�̲���
		data.setData(
				"BLOOD_TYPE",
				"TEXT",
				print.getValue("BLOOD_TYPE", 0).length() == 0
						|| "6".equalsIgnoreCase(print.getValue("BLOOD_TYPE", 0)) ? "-"
						: print.getValue("BLOOD_TYPE", 0));// Ѫ��
		// ����Ѫ��
		String blockType = print.getValue("BLOOD_TYPE", 0).length() == 0
				|| "6".equalsIgnoreCase(print.getValue("BLOOD_TYPE", 0)) ? "-"
				: print.getValue("BLOOD_TYPE", 0);
		String BlockTypeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.02.001", "HR51.03.003", blockType);
		data.setData("HR51.03.003", "TEXT", BlockTypeCDAValue);
		// ����RH
		String rhType = print.getValue("RH_TYPE", 0).length() == 0 ? "-"
				: print.getValue("RH_TYPE", 0);
		String rhTypeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.02.001", "HR51.03.004", rhType);
		data.setData("HR51.03.004", "TEXT", rhTypeCDAValue);
		data.setData(
				"RH_TYPE",
				"TEXT",
				print.getValue("RH_TYPE", 0).length() == 0 ? "-" : print
						.getValue("RH_TYPE", 0));// RH
		data.setData(
				"TRANS_REACTION",
				"TEXT",
				print.getValue("TRANS_REACTION", 0).length() == 0 ? "-" : print
						.getValue("TRANS_REACTION", 0));// ��Ѫ��Ӧ
		// ������Ѫ��Ӧ
		String transAction = print.getValue("TRANS_REACTION", 0).length() == 0 ? "-"
				: print.getValue("TRANS_REACTION", 0);
		String transActionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.09.002", "HR55.02.050", transAction);
		data.setData("HR55.02.050", "TEXT", transActionCDAValue);
		data.setData("RBC", "TEXT", print.getValue("RBC", 0));// ��Ѫ��
		data.setData("PLATE", "TEXT", print.getValue("PLATE", 0));// ѪС��
		data.setData("PLASMA", "TEXT", print.getValue("PLASMA", 0));// Ѫ��
		data.setData("WHOLE_BLOOD", "TEXT", print.getValue("WHOLE_BLOOD", 0));// ȫѪ
		data.setData("OTH_BLOOD", "TEXT", print.getValue("OTH_BLOOD", 0));// ����
		data.setData(
				"SUMTOT",
				"TEXT",
				df.format(print.getDouble("CHARGE_01", 0)
						+ print.getDouble("CHARGE_02", 0)
						+ print.getDouble("CHARGE_03", 0)
						+ print.getDouble("CHARGE_04", 0)
						+ print.getDouble("CHARGE_05", 0)
						+ print.getDouble("CHARGE_06", 0)
						+ print.getDouble("CHARGE_07", 0)
						+ print.getDouble("CHARGE_08", 0)
						+ print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)
						+ print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)
						+ print.getDouble("CHARGE_14", 0)
						+ print.getDouble("CHARGE_15", 0)
						+ print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		data.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		data.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		data.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		data.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		data.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		data.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		data.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		data.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		data.setData("CHARGE_09", "TEXT",
				df.format(print.getDouble("CHARGE_09", 0)));
		data.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_10", 0)));
		data.setData("CHARGE_11", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		data.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		data.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_13", 0)));
		data.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		data.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		data.setData("CHARGE_16", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		data.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_17", 0)));
		data.setData("OP", getOP_DATA(op_date).getData());// ������Ϣ
		// ��������Ϣ
		if (child.getCount("CASE_NO") > 0) {
			data.setData("Child_T", true);
			data.setData("C_SEX", "TEXT", childDiag.getValue("SEX", 0));
			data.setData("C_WEIGHT", "TEXT",
					this.getChildWeight(childDiag.getValue("CASE_NO", 0)));// ��������
			data.setData("APGAR", "TEXT", child_I.getValue("APGAR_NUMBER", 0));// APGAR����
			// Ӥ��������
			if (child_I.getBoolean("BABY_VACCINE_FLG", 0))
				data.setData("C_KJ", "TEXT", "1");
			else
				data.setData("C_KJ", "TEXT", "2");
			// �Ҹ�����
			if (child_I.getBoolean("LIVER_VACCINE_FLG", 0))
				data.setData("C_YG", "TEXT", "1");
			else
				data.setData("C_YG", "TEXT", "2");
			// TSH
			if (child_I.getBoolean("TSH_FLG", 0))
				data.setData("C_TSH", "TEXT", "1");
			else
				data.setData("C_TSH", "TEXT", "2");
			// PKU_FLG
			if (child_I.getBoolean("PKU_FLG", 0))
				data.setData("C_PKU", "TEXT", "1");
			else
				data.setData("C_PKU", "TEXT", "2");

		} else {
			data.setData("Child_T", false);
		}
		return data;
	}

	/**
	 * ��ӡ���� �·���1
	 * 
	 * @param CASE_NO
	 * @return
	 */
	public TParm getNewMroRecordprintData(String CASE_NO) {
		TParm result = new TParm();
		// ��ȡĳһ��������ҳ��Ϣ
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		boolean childrenFlg = false;// ������ע��
		childrenFlg = MRORecordTool.getInstance().getNewBornFlg(parm);

		// �жϸò����Ƿ��ǲ���
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// ��ȡ����������Ϣ ��ʾ��ĸ�׵Ĳ�����ҳ��
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// ��ѯ����������ҳ��Ϣ
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		result.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// ҽԺ����
		result.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// ��֯��������
		// �������HR56.00.002.05 CDAֵ
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		result.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		result.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// ������ҳ���
		result.setData("NHI_NO", "TEXT",
				this.getcheckStr(print.getValue("NHI_NO", 0)));// ��������
		// ҽ����
		result.setData("MR_NO", "TEXT", print.getValue("MR_NO", 0));// ������
		result.setData("IPD_NO", "TEXT", print.getValue("IPD_NO", 0));// סԺ��
		// ��ȡ����������Ϣ
		Pat pat = Pat.onQueryByMrNo(print.getValue("MR_NO", 0));
		result.setData("IN_COUNT", "TEXT", "��" + print.getInt("IN_COUNT", 0)
				+ "��סԺ");// סԺ����
		result.setData("PAT_NAME", "TEXT", print.getValue("PAT_NAME", 0));// ��������
		// ����������Ϣ
		result.setData("HR02.01.002", "TEXT", print.getValue("PAT_NAME", 0));// ����
		result.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// �Ա�
		// �����Ա�CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		result.setData("HR02.02.001", "TEXT", sexCDAValue);
		result.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy��MM��dd��"));// ��������
		String[] res;
		res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
				print.getTimestamp("IN_DATE", 0));// ����
		// ����С��1����
		if (TypeTool.getInt(res[0]) < 1) {
			if (TypeTool.getInt(res[1]) >= 10)
				result.setData("MO", "TEXT", res[1]);// ������
			else
				result.setData("MO", "TEXT", " " + res[1]);// ������
			if (TypeTool.getInt(res[2]) >= 10)
				result.setData("CHDAY", "TEXT", res[2]);// ����
			else
				result.setData("CHDAY", "TEXT", " " + res[2]);// ����
			result.setData("CHCOUNT", "TEXT", "30");// �µĻ���
			result.setData("AGE", "TEXT", "-");
		} else if (TypeTool.getInt(res[0]) >= 1) {
			result.setData("AGE", "TEXT", res[0] + "��");// ����
			result.setData("MO", "TEXT", "-");
		}
		result.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// ����
		// ������
		if (true) {
			parm.setData("ADM_TYPE", "I");
			String bornweight = "";// ��������
			String inweight = "";// ��Ժ����
			TParm bornWParm = SUMNewArrivalTool.getInstance().getNewBornWeight(
					parm);
			// System.out.println("------bornWParm---------------"+bornWParm);
			if (!bornWParm.getValue("NB_WEIGHT").equals(""))
				bornweight = bornWParm.getValue("NB_WEIGHT");
			else
				bornweight = "-";
			TParm inParm = SUMNewArrivalTool.getInstance()
					.getNewAdmWeight(parm);
			// System.out.println("------inParm---------------"+inParm);
			if (!inParm.getValue("NB_ADM_WEIGHT").equals(""))
				inweight = inParm.getValue("NB_ADM_WEIGHT");
			else
				inweight = "-";
			result.setData("NB_ADM_WEIGHT", "TEXT", inweight);// ��������Ժ����
			result.setData("NB_WEIGHT", "TEXT", bornweight);// ��������������
		}
		// modify by wangb 2017/3/28 ���������غͼ���������ʾ����
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		result.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 16 ? HOEMPLACE.substring(0, 16)
						: HOEMPLACE);// ������
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		result.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 16 ? birthplace.substring(0, 16)
						: birthplace);// //����
		result.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// ����
		result.setData("IDNO", "TEXT",
				this.getcheckStr(print.getValue("IDNO", 0)));// ���֤
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		result.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// ְҵ
		result.setData("MARRIGE", "TEXT",
				this.getNewMarrige(print.getValue("MARRIGE", 0)));// ����
		// ���û���CDA����
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		result.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		result.setData("ADDRESS", "TEXT", print.getValue("ADDRESS", 0));// ͨ�ŵ�ַ
		result.setData("POST_NO", "TEXT", print.getValue("POST_NO", 0));// ͨ���ʱ�
		result.setData("TEL", "TEXT", print.getValue("TEL", 0));// ͨ�ŵ绰
		String office = print.getValue("OFFICE", 0);// wanglong add 20150226
		if (!print.getValue("O_ADDRESS", 0).equals("")) {
			office += "(" + print.getValue("O_ADDRESS", 0) + ")";
		}
		result.setData("OFFICE", "TEXT", office);// ������λ����ַ
		result.setData("O_TEL", "TEXT", print.getValue("O_TEL", 0));// ��λ�绰
		result.setData("O_POSTNO", "TEXT", print.getValue("O_POSTNO", 0));// ��λ�ʱ�
		result.setData("H_ADDRESS", "TEXT", print.getValue("H_ADDRESS", 0));// ���ڵ�ַ
		result.setData("H_POSTNO", "TEXT", print.getValue("H_POSTNO", 0));// �����ʱ�
		result.setData("CONTACTER", "TEXT", print.getValue("CONTACTER", 0));// ��ϵ������
		result.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// ��ϵ�˹�ϵ
		result.setData("CONT_ADDRESS", "TEXT",
				print.getValue("CONT_ADDRESS", 0));// ��ϵ�˵�ַ
		result.setData("CONT_TEL", "TEXT", print.getValue("CONT_TEL", 0));// ��ϵ�˵绰
		/*---------------------------------������Ϣ����-------------------------------------------------*/
		result.setData("ADM_SOURCE", "TEXT",
				this.getNewadmSource(print.getValue("ADM_SOURCE", 0)));// ��Ժ;��
		result.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy��MM��dd�� HHʱ"));// ��Ժ����
		result.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// ��Ժ�Ʊ�
		result.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
						print.getValue("IN_STATION", 0)));// ��Ժ����
		result.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(getTranDept(CASE_NO)));// ת�ƿƱ� wanglong
															// modify 20150115

		result.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy��MM��dd�� HHʱ"));// ��Ժ����

		if (print.getData("OUT_DATE", 0) != null) {// �ж��Ƿ��Ժ
			result.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // ��Ժ����
			result.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
							print.getValue("OUT_STATION", 0))); // ��Ժ����
		} else {
			result.setData("OUT_DEPT", "TEXT", "");
			result.setData("OUT_ROOM", "TEXT", "");
		}
		result.setData("REAL_STAY_DAYS", "TEXT",
				print.getValue("REAL_STAY_DAYS", 0));// ʵ��סԺ����
		// ������Ժ���CDA����
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		result.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		TParm diagnosis = new TParm(
				this.getDBTool()
						.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
								+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
								+ CASE_NO
								+ "' ORDER BY IO_TYPE ASC,MAIN_FLG DESC,SEQ_NO")); // ��ϼ�¼
		
		int outindex = 2;
		String OE_DIAG_CODE = "";
		String OE_DIAG_DESC = "";
		boolean q = true;
		boolean r = true;
		boolean typeO= false;
		boolean flg =false;
		for (int j = 0; j < diagnosis.getCount(); j++) {		// ���ҽ��վֻ��д��Ժ����ϣ�û��д��Ժ��ϵģ���Ժ����ϵ�������ʾ�ɳ�Ժ���
			if (diagnosis.getValue("TYPE", j).equals("O")) {
				typeO=true;
					
			}
			if (diagnosis.getValue("TYPE", j).equals("Q")) {
				q =false;
			}
		}
		if(!q && !typeO){//�� Q û��O
			flg =true;
		}
		
		for (int j = 0; j < diagnosis.getCount(); j++) {	//�����Ժ��Ϻ�Ժ����϶����ڲ�������ͬ����ϱ���, ȥ��Ժ�����

		if (diagnosis.getValue("TYPE", j).equals("Q")) {
			for (int i = 0; i < diagnosis.getCount(); i++) {
				if (diagnosis.getValue("TYPE", i).equals("O")) {
					if (diagnosis.getValue("CODE", j).equals(
							diagnosis.getValue("CODE", i))) {
						r = false;
						break;
					}
				}
				if(!r){
					break;
				}

			}
		}
		}
		if (diagnosis.getCount() > 0) {
			for (int j = 0; j < diagnosis.getCount(); j++) {
				if (diagnosis.getValue("TYPE", j).equals("I")) {

					if (OE_DIAG_DESC.length() > 0) {// ��ϴ��룺���������룩
						OE_DIAG_DESC += "," + diagnosis.getValue("NAME", j);
					} else {
						OE_DIAG_DESC = diagnosis.getValue("NAME", j);
					}
					if (OE_DIAG_CODE.length() > 0) {
						OE_DIAG_CODE += "," + diagnosis.getValue("CODE", j);
					} else {
						OE_DIAG_CODE = diagnosis.getValue("CODE", j);
					}
					result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// �ż������
					result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// �ż�����ϼ�������
				}
				if (diagnosis.getValue("TYPE", j).equals("O")) {
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ����ϼ�������
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ����ϼ�������
						result.setData("DIAG_TYPE1", "TEXT", ""); // ��Ժ����ϼ�������
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�������Ժ����
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ��ϼ�������
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ��ϼ�������
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"�������:"); // ��Ժ��ϼ�������
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // ��Ժ��ϼ�������
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�����Ժ����
						outindex++;
					}
				}

				

				if (flg) {
				if (diagnosis.getValue("TYPE", j).equals("Q")) {
					
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ����ϼ�������
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ����ϼ�������
						result.setData("DIAG_TYPE1", "TEXT", ""); // ��Ժ����ϼ�������
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�������Ժ����
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ��ϼ�������
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ��ϼ�������
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"�������:"); // ��Ժ��ϼ�������
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // ��Ժ��ϼ�������
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�����Ժ����
						outindex++;
					}
				}
				}else{
					if (r) {
						if (diagnosis.getValue("TYPE", j).equals("Q")) {
							result.setData("DIAG_CODE" + outindex, "TEXT",
									diagnosis.getValue("CODE", j)); // ��Ⱦ��ϼ�������
							result.setData(
									"DIAG" + outindex,
									"TEXT",
									diagnosis.getValue("NAME", j)
											+ addBrace(diagnosis.getValue("REMARK",
													j))); // ��Ⱦ��ϼ�������
							result.setData("DIAG_TYPE" + outindex, "TEXT", "");
							result.setData("DIAG_CONDITION" + outindex, "TEXT",
									diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ⱦ�����Ժ����
							outindex++;
						}
					}
				}
				if (diagnosis.getValue("TYPE", j).equals("W")) {// ����֢��� add by
																// wanglong
																// 20140411
					result.setData("DIAG_CODE" + outindex, "TEXT",
							diagnosis.getValue("CODE", j)); // ��������
					result.setData("DIAG" + outindex, "TEXT",
							"  " + diagnosis.getValue("NAME", j)
									+ addBrace(diagnosis.getValue("REMARK", j))); // ��������
					// result.setData("DIAG_TYPE"+outindex, "TEXT","����֢���:"); //
					// ��������
					result.setData("DIAG_TYPE" + outindex, "TEXT", "");
					result.setData("DIAG_CONDITION" + outindex, "TEXT",
							diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ����
					outindex++;
				}
			}
		}
		if (diagnosis.getErrCode() < 0) {
			System.out.println("��ѯMRO_RECORD_DIAG����������ݴ���");
		}
		// shibl 20120618 modify
		// String OE_DIAG_DESC = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		// String OE_DIAG_CODE = print.getValue("OE_DIAG_CODE", 0);
		// if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// ��ϴ��룺���������룩
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE2", 0);
		// }
		// if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE3", 0);
		// }
		// result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// �ż������
		// result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// �ż�����ϼ�������
		// /*---------------------------------סԺ����-----------------------------------------------------*/
		// result.setData("DIAG1", "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // ��Ժ�����
		// // result.setData("DIAG2", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // ��Ժ���2
		// // result.setData("DIAG3", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // ��Ժ���3
		// // result.setData("DIAG4", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // ��Ժ���4
		// // result.setData("DIAG5", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // ��Ժ���5
		// // result.setData("DIAG6", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // ��Ժ���6
		// result.setData("DIAG_CODE1", "TEXT",
		// print.getValue("OUT_DIAG_CODE1", 0)); // ��Ժ����ϼ�������
		// // result.setData("DIAG_CODE2", "TEXT",
		// // print.getValue("OUT_DIAG_CODE2", 0)); // ��Ժ���2��������
		// // result.setData("DIAG_CODE3", "TEXT",
		// // print.getValue("OUT_DIAG_CODE3", 0)); // ��Ժ���3��������
		// // result.setData("DIAG_CODE4", "TEXT",
		// // print.getValue("OUT_DIAG_CODE4", 0)); // ��Ժ���4��������
		// // result.setData("DIAG_CODE5", "TEXT",
		// // print.getValue("OUT_DIAG_CODE5", 0)); // ��Ժ���5��������
		// // result.setData("DIAG_CODE6", "TEXT",
		// // print.getValue("OUT_DIAG_CODE6", 0)); // ��Ժ���6��������
		// result.setData("DIAG_CONDITION1", "TEXT",
		// print.getValue("OUT_DIAG_CONDITION1", 0)); // ��Ժ�������Ժ����
		// // result.setData("DIAG_CONDITION2", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION2", 0)); // ��Ժ���2��Ժ����
		// // result.setData("DIAG_CONDITION3", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION3", 0)); // ��Ժ���3��Ժ����
		// // result.setData("DIAG_CONDITION4", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION4", 0)); // ��Ժ���4��Ժ����
		// // result.setData("DIAG_CONDITION5", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION5", 0)); // ��Ժ���5��Ժ����
		// // result.setData("DIAG_CONDITION6", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION6", 0)); // ��Ժ���6��Ժ����
		// int seq = 2;
		// for (int i = 2; i < 7; i++) {
		// if (!print.getValue("OUT_DIAG_CODE" + i, 0).equals("")) {
		// result.setData("DIAG" + seq, "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE" + i, 0)));
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CODE" + i, 0));
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CONDITION" + i, 0));
		// if (seq == 2)
		// result.setData("DIAG_TYPE" + seq, "TEXT", "�������:");
		// seq++;
		// }
		// }
		// String INTE_DIAG_CODE = getICD_DESC(print.getValue(
		// "INTE_DIAG_CODE", 0));
		// result.setData("DIAG" + seq, "TEXT", INTE_DIAG_CODE);// Ժ�ڸ�Ⱦ���
		// // ��ȷ��
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CODE", 0));// Ժ�ڸ�Ⱦ���CODE
		// // ��ȷ��
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CONDITION", 0));// Ժ�ڸ�Ⱦ�����Ժ����
		// result.setData("DIAG_TYPE" + seq, "TEXT", "��Ⱦ���:");
		/*----------------------------------------��ϱ�---------------------------------------------------*/
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		if (null != PATHOLOGY_DIAG && !"".equals(PATHOLOGY_DIAG))
			result.setData("PATHOLOGY_DIAG", "TEXT", PATHOLOGY_DIAG);// �������
		else
			result.setData("PATHOLOGY_DIAG", "TEXT", "-");// �������

		if (null != print.getValue("PATHOLOGY_DIAG", 0)
				&& !"".equals(print.getValue("PATHOLOGY_DIAG", 0)))
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT",
					print.getValue("PATHOLOGY_DIAG", 0));// ������ϼ�������
		else
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT", "-");// ������ϼ�������

		if (null != print.getValue("PATHOLOGY_NO", 0)
				&& !"".equals(print.getValue("PATHOLOGY_NO", 0)))
			result.setData("PATHOLOGY_NO", "TEXT",
					print.getValue("PATHOLOGY_NO", 0));// �����
		else
			result.setData("PATHOLOGY_NO", "TEXT", "-");// �����

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		if (null != EX_RSN && !"".equals(EX_RSN))
			result.setData("EX_RSN", "TEXT", EX_RSN);// ���ˡ��ж����ⲿ����
		else
			result.setData("EX_RSN", "TEXT", "-");// ���ˡ��ж����ⲿ����

		if (null != print.getValue("EX_RSN", 0)
				&& !"".equals(print.getValue("EX_RSN", 0)))
			result.setData("EX_RSN_CODE", "TEXT", print.getValue("EX_RSN", 0));// ���ˡ��ж����ⲿ���ؼ�������
		else
			result.setData("EX_RSN_CODE", "TEXT", "-");// ���ˡ��ж����ⲿ���ؼ�������

		result.setData("ALLEGIC_CODE", "TEXT",
				this.getcheckStr(print.getValue("ALLEGIC_FLG", 0)));// �Ƿ���ҩ�����
		result.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// ҩ�����
		result.setData("BODY_CHECK", "TEXT",
				this.getcheckStr(print.getValue("BODY_CHECK", 0)));// ʬ��
		result.setData("BLOOD_TYPE", "TEXT", print.getValue("BLOOD_TYPE", 0));// Ѫ��
		// ����RH
		String rhType = print.getValue("RH_TYPE", 0);
		result.setData("RH_TYPE", "TEXT", rhType);// RH
		result.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// ������
		result.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// ����ҽʦ
		result.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// ����ҽʦ
		result.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// סԺҽʦ
		result.setData("INDUCATION_DR_CODE", "TEXT",
				print.getValue("INDUCATION_DR_CODE", 0));// ����ҽʦ duzhw modify by
															// 20131204
		result.setData("VS_NURSE_CODE", "TEXT",
				drList.get(print.getValue("VS_NURSE_CODE", 0)));// ���λ�ʿ
		result.setData("INTERN_DR_CODE", "TEXT",
				print.getValue("INTERN_DR_CODE", 0));// ʵϰҽʦ duzhw modify by
														// 20131204
		result.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// ����Ա
		result.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// ��������
		// ���ò�������
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		result.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		result.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// �ʿ�ҽʦ
		result.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// �ʿػ�ʿ
		result.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy��MM��dd��"));// �ʿ�����
		/*-------------------------------------------------------------------------------------------*/
		// ��ѯ������Ϣ
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		// System.out.println("------op_date---------"+op_date);
		TParm anaParm = getOP_DATA(op_date);
		// System.out.println("-=-=------------------"+anaParm);
		int index = 2;
		for (int i = 0; i < op_date.getCount(); i++) {
			if (op_date.getValue("MAIN_FLG", i).equals("Y")) {
				result.setData("OPE_CODE1", "TEXT",
						op_date.getValue("OP_CODE", i));// ��������
				result.setData("OPE_DATE1", "TEXT",
						op_date.getValue("OP_DATE", i));// ��������
				result.setData("OPE_LEVEL1", "TEXT",
						op_date.getValue("OP_LEVEL", i));// ��������
				result.setData("OPE_DESC1", "TEXT",
						op_date.getValue("OP_DESC", i));// ��������
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// ����
				} else {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// ����
				}
				result.setData("AST_DR11", "TEXT",
						op_date.getValue("AST_DR1", i));// ����1
				result.setData("AST_DR21", "TEXT",
						op_date.getValue("AST_DR2", i));// ����2
				result.setData("HEL1", "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// ���ϵȼ�
				result.setData("ANA_WAY1", "TEXT",
						anaParm.getValue("ANA_WAY", i));// ����ʽ
				result.setData("ANA_DR1", "TEXT", op_date.getValue("ANA_DR", i));// ����ʦ
			} else {
				result.setData("OPE_CODE" + index, "TEXT",
						op_date.getValue("OP_CODE", i));// ��������
				result.setData("OPE_DATE" + index, "TEXT",
						op_date.getValue("OP_DATE", i));// ��������
				result.setData("OPE_LEVEL" + index, "TEXT",
						op_date.getValue("OP_LEVEL", i));// ��������
				result.setData("OPE_DESC" + index, "TEXT",
						op_date.getValue("OP_DESC", i));// ��������
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// ����
				} else {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// ����
				}
				result.setData("AST_DR1" + index, "TEXT",
						op_date.getValue("AST_DR1", i));// ����1
				result.setData("AST_DR2" + index, "TEXT",
						op_date.getValue("AST_DR2", i));// ����2
				result.setData("HEL" + index, "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// ���ϵȼ�
				result.setData("ANA_WAY" + index, "TEXT",
						anaParm.getValue("ANA_WAY", i));// ����ʽ
				result.setData("ANA_DR" + index, "TEXT",
						op_date.getValue("ANA_DR", i));// ����ʦ
				index++;
			}
		}
		result.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// ��Ժ��ʽ
		if (print.getValue("OUT_TYPE", 0).equals("2"))
			result.setData(
					"TRAN_HOSP1",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// ��תԺ��
																// ����999999�����Զ���
																// 20120918
																// shibl
		else
			result.setData("TRAN_HOSP1", "TEXT", "-");// wanglong add 20150226
		if (print.getValue("OUT_TYPE", 0).equals("3"))
			result.setData(
					"TRAN_HOSP2",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// ��ת����
																// ����999999�����Զ���
																// 20120918
																// shibl
		else
			result.setData("TRAN_HOSP2", "TEXT", "-");// wanglong add 20150226
		if (print.getValue("BE_COMA_TIME", 0).equals("")) {
			result.setData("BE_COMA_TIME", "TEXT", "-" + "��" + "-" + "Сʱ" + "-"
					+ "����");// ��Ժǰ����ʱ��
		} else {
			result.setData(
					"BE_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("BE_COMA_TIME", 0)
							.substring(0, 2))
							+ "��"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(2, 4))
							+ "Сʱ"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(4, 6)) + "����");// ��Ժǰ����ʱ��
		}
		if (print.getValue("AF_COMA_TIME", 0).equals("")) {
			result.setData("AF_COMA_TIME", "TEXT", "-" + "��" + "-" + "Сʱ" + "-"
					+ "����");// ��Ժ�����ʱ��
		} else {
			result.setData(
					"AF_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("AF_COMA_TIME", 0)
							.substring(0, 2))
							+ "��"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(2, 4))
							+ "Сʱ"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(4, 6)) + "����");// ��Ժ�����ʱ��
		}
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		result.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31��ƻ����
		result.setData("AGN_PLAN_INTENTION", "TEXT",
				this.getcheckStr(print.getValue("AGN_PLAN_INTENTION", 0)));// 31��ƻ�ԭ��
		/*------------------------------------���ô�ȷ��------------------------------------------------*/
		DecimalFormat df = new DecimalFormat("0.00");
		result.setData("SUMTOT", "TEXT",
				df.format(print.getDouble("SUM_TOT", 0)));
		result.setData("OWN_TOT", "TEXT",
				df.format(print.getDouble("OWN_TOT", 0)));
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		// һ��ҽ�Ʒ����
		result.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		// һ�����Ʋ�����
		result.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		// �����
		result.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		// ��������
		result.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		// ������Ϸ�
		result.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		// ʵ������Ϸ�
		result.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		// Ӱ��ѧ��Ϸ�
		result.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		// �ٴ������Ŀ��
		result.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		// ���������Ʒ���
		result.setData(
				"CHARGE_09",
				"TEXT",
				df.format(print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)));
		// �ٴ��������Ʒ�
		result.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_9", 0)));
		// �������Ʒ�
		result.setData(
				"CHARGE_11",
				"TEXT",
				df.format(print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)));
		// �������Ʒ�-�����
		result.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		// �������Ʒ�-������
		result.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		// ������
		result.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		// ��ҽ���Ʒ�
		result.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		// ��ҩ����
		result.setData(
				"CHARGE_16",
				"TEXT",
				df.format(print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		// ����ҩ�����
		result.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		// �г�ҩ��
		result.setData("CHARGE_18", "TEXT",
				df.format(print.getDouble("CHARGE_18", 0)));
		// �в�ҩ��
		result.setData("CHARGE_19", "TEXT",
				df.format(print.getDouble("CHARGE_19", 0)));
		// Ѫ��
		result.setData("CHARGE_20", "TEXT",
				df.format(print.getDouble("CHARGE_20", 0)));
		// �׵�������Ʒ��
		result.setData("CHARGE_21", "TEXT",
				df.format(print.getDouble("CHARGE_21", 0)));
		// �򵰰�����Ʒ��
		result.setData("CHARGE_22", "TEXT",
				df.format(print.getDouble("CHARGE_22", 0)));
		// ��Ѫ��������Ʒ��
		result.setData("CHARGE_23", "TEXT",
				df.format(print.getDouble("CHARGE_23", 0)));
		// ϸ����������Ʒ��
		result.setData("CHARGE_24", "TEXT",
				df.format(print.getDouble("CHARGE_24", 0)));
		// �����һ����ҽ�ò��Ϸ�
		result.setData("CHARGE_25", "TEXT",
				df.format(print.getDouble("CHARGE_25", 0)));
		// ������һ����ҽ�ò��Ϸ�
		result.setData("CHARGE_26", "TEXT",
				df.format(print.getDouble("CHARGE_26", 0)));
		// ������һ����ҽ�ò��Ϸ�
		result.setData("CHARGE_27", "TEXT",
				df.format(print.getDouble("CHARGE_27", 0)));
		// ������
		result.setData("CHARGE_28", "TEXT",
				df.format(print.getDouble("CHARGE_28", 0)));

		// 2013-5-23 zhangh modify ������֢�໤���ͺ�����ʹ��ʱ��
		result.setData("VENTI_TIME", "TEXT", print.getData("VENTI_TIME", 0));// ������ʹ��ʱ��
		// ��֢�໤�������
		for (int i = 1; i < 6; i++) {
			String inDate = "", icuInDate = "", outDate = "", icuOutDate = "";
			if (print.getData("ICU_IN_DATE" + i, 0) != null) {
				inDate = print
						.getData("ICU_IN_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_IN_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// �õ����ݿ��еĽ���ʱ��
			}
			if (print.getData("ICU_OUT_DATE" + i, 0) != null) {
				outDate = print
						.getData("ICU_OUT_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_OUT_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// �õ����ݿ��е��˳�ʱ��
			}
			icuInDate = getInOutDate(inDate);
			icuOutDate = getInOutDate(outDate);
			// ��ȡ�����������
			String deptCode = print.getValue("ICU_ROOM" + i, 0);
			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			result.setData("IN_DATE_" + i, "TEXT", icuInDate);
			result.setData("OUT_DATE_" + i, "TEXT", icuOutDate);
			result.setData("ICU_ROOM_" + i, "TEXT", deptDesc);
		}
		return result;
	}

	/**
	 * ��ӡ���� �·���2
	 * 
	 * @param CASE_NO
	 * @param realStayDays
	 * @return
	 */
	public TParm getNewMroRecordprintData(String CASE_NO, Object realStayDays) {
		TParm result = new TParm();
		// ��ȡĳһ��������ҳ��Ϣ
		TParm parm = new TParm();
		parm.setData("CASE_NO", CASE_NO);
		TParm print = MRORecordTool.getInstance().getInHospInfo(parm);
		if (print.getErrCode() < 0) {
			return print;
		}
		boolean childrenFlg = false;// ������ע��
		childrenFlg = MRORecordTool.getInstance().getNewBornFlg(parm);

		// �жϸò����Ƿ��ǲ���
		TParm child = ADMChildImmunityTool.getInstance()
				.checkM_CASE_NO(CASE_NO);
		TParm child_I = new TParm();
		if (child.getCount("CASE_NO") > 0) {
			// ��ȡ����������Ϣ ��ʾ��ĸ�׵Ĳ�����ҳ��
			TParm ch = new TParm();
			ch.setData("CASE_NO", child.getValue("CASE_NO", 0));
			child_I = ADMChildImmunityTool.getInstance().selectData(ch);
		}
		// ��ѯ����������ҳ��Ϣ
		TParm childParm = new TParm();
		childParm.setData("CASE_NO", child.getValue("CASE_NO", 0));
		TParm childDiag = MRORecordTool.getInstance().getInHospInfo(childParm);
		if (childDiag.getErrCode() < 0) {
			return childDiag;
		}
		result.setData("HOSP_DESC", "TEXT", Operator.getHospitalCHNFullName());// ҽԺ����
		result.setData("HOSP_ID", "TEXT", print.getValue("HOSP_ID", 0));// ��֯��������
		// �������HR56.00.002.05 CDAֵ
		String CTZ1CDAValue = EMRCreateXMLTool.getInstance().getCDACode("S.13",
				"HR56.00.002.05", print.getValue("CTZ1_CODE", 0));
		result.setData("HR56.00.002.05", "TEXT", CTZ1CDAValue);
		result.setData("MRO_CTZ", "TEXT", print.getValue("MRO_CTZ", 0));// ������ҳ���
		result.setData("NHI_NO", "TEXT",
				this.getcheckStr(print.getValue("NHI_NO", 0)));// ��������
		// ҽ����
		result.setData("MR_NO", "TEXT", print.getValue("MR_NO", 0));// ������
		result.setData("IPD_NO", "TEXT", print.getValue("IPD_NO", 0));// סԺ��
		// ��ȡ����������Ϣ
		Pat pat = Pat.onQueryByMrNo(print.getValue("MR_NO", 0));
		result.setData("IN_COUNT", "TEXT", "��" + print.getInt("IN_COUNT", 0)
				+ "��סԺ");// סԺ����
		result.setData("PAT_NAME", "TEXT", print.getValue("PAT_NAME", 0));// ��������
		// ����������Ϣ
		result.setData("HR02.01.002", "TEXT", print.getValue("PAT_NAME", 0));// ����
		result.setData("SEX_CODE", "TEXT", print.getValue("SEX", 0));// �Ա�
		// �����Ա�CDAValue
		String sexCDAValue = EMRCreateXMLTool.getInstance().getCDACode("H.03",
				"HR02.02.001", print.getValue("SEX", 0));
		result.setData("HR02.02.001", "TEXT", sexCDAValue);
		result.setData("BIRTH_DAY", "TEXT", StringTool.getString(
				print.getTimestamp("BIRTH_DATE", 0), "yyyy��MM��dd��"));// ��������
		String[] res;
		res = StringTool.CountAgeByTimestamp(pat.getBirthday(),
				print.getTimestamp("IN_DATE", 0));// ����
		// ����С��1����
		if (TypeTool.getInt(res[0]) < 1) {
			if (TypeTool.getInt(res[1]) >= 10)
				result.setData("MO", "TEXT", res[1]);// ������
			else
				result.setData("MO", "TEXT", " " + res[1]);// ������
			if (TypeTool.getInt(res[2]) >= 10)
				result.setData("CHDAY", "TEXT", res[2]);// ����
			else
				result.setData("CHDAY", "TEXT", " " + res[2]);// ����
			result.setData("CHCOUNT", "TEXT", "30");// �µĻ���
			result.setData("AGE", "TEXT", "-");
		} else if (TypeTool.getInt(res[0]) >= 1) {
			result.setData("AGE", "TEXT", res[0] + "��");// ����
			result.setData("MO", "TEXT", "-");
		}
		result.setData("NATION", "TEXT",
				getDictionaryDesc("SYS_NATION", print.getValue("NATION", 0)));// ����
		// ������
		if (true) {
			parm.setData("ADM_TYPE", "I");
			String bornweight = "";// ��������
			String inweight = "";// ��Ժ����
			TParm bornWParm = SUMNewArrivalTool.getInstance().getNewBornWeight(
					parm);
			// System.out.println("------bornWParm---------------"+bornWParm);
			if (!bornWParm.getValue("NB_WEIGHT").equals(""))
				bornweight = bornWParm.getValue("NB_WEIGHT");
			else
				bornweight = "-";
			TParm inParm = SUMNewArrivalTool.getInstance()
					.getNewAdmWeight(parm);
			// System.out.println("------inParm---------------"+inParm);
			if (!inParm.getValue("NB_ADM_WEIGHT").equals(""))
				inweight = inParm.getValue("NB_ADM_WEIGHT");
			else
				inweight = "-";
			result.setData("NB_ADM_WEIGHT", "TEXT", inweight);// ��������Ժ����
			result.setData("NB_WEIGHT", "TEXT", bornweight);// ��������������
		}
		// modify by wangb 2017/3/28 ���������غͼ���������ʾ����
		String HOEMPLACE = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("HOMEPLACE_CODE", 0));
		result.setData("HOEMPLACE", "TEXT",
				HOEMPLACE.length() > 16 ? HOEMPLACE.substring(0, 16)
						: HOEMPLACE);// ������
		String birthplace = getDesc("SYS_HOMEPLACE", "", "HOMEPLACE_DESC",
				"HOMEPLACE_CODE", print.getValue("BIRTHPLACE", 0));
		result.setData("BIRTHPLACE", "TEXT",
				birthplace.length() > 16 ? birthplace.substring(0, 16)
						: birthplace);// //����
		result.setData("FOLK", "TEXT",
				getDictionaryDesc("SYS_SPECIES", print.getValue("FOLK", 0)));// ����
		result.setData("IDNO", "TEXT",
				this.getcheckStr(print.getValue("IDNO", 0)));// ���֤
		String OCCUPATION = getDictionaryDesc("SYS_OCCUPATION",
				print.getValue("OCCUPATION", 0));
		result.setData("OCCUPATION", "TEXT",
				OCCUPATION.length() > 8 ? OCCUPATION.substring(0, 8)
						: OCCUPATION);// ְҵ
		result.setData("MARRIGE", "TEXT",
				this.getNewMarrige(print.getValue("MARRIGE", 0)));// ����
		// ���û���CDA����
		String marrigeCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"H.03", "HR02.06.003", print.getValue("MARRIGE", 0));
		result.setData("HR02.06.003", "TEXT", marrigeCDAValue);
		result.setData("ADDRESS", "TEXT", print.getValue("ADDRESS", 0));// ͨ�ŵ�ַ
		result.setData("POST_NO", "TEXT", print.getValue("POST_NO", 0));// ͨ���ʱ�
		result.setData("TEL", "TEXT", print.getValue("TEL", 0));// ͨ�ŵ绰
		String office = print.getValue("OFFICE", 0);// wanglong add 20150226
		if (!print.getValue("O_ADDRESS", 0).equals("")) {
			office += "(" + print.getValue("O_ADDRESS", 0) + ")";
		}
		result.setData("OFFICE", "TEXT", office);// ������λ����ַ
		result.setData("O_TEL", "TEXT", print.getValue("O_TEL", 0));// ��λ�绰
		result.setData("O_POSTNO", "TEXT", print.getValue("O_POSTNO", 0));// ��λ�ʱ�
		result.setData("H_ADDRESS", "TEXT", print.getValue("H_ADDRESS", 0));// ���ڵ�ַ
		result.setData("H_POSTNO", "TEXT", print.getValue("H_POSTNO", 0));// �����ʱ�
		result.setData("CONTACTER", "TEXT", print.getValue("CONTACTER", 0));// ��ϵ������
		result.setData(
				"RELATIONSHIP",
				"TEXT",
				getDictionaryDesc("SYS_RELATIONSHIP",
						print.getValue("RELATIONSHIP", 0)));// ��ϵ�˹�ϵ
		result.setData("CONT_ADDRESS", "TEXT",
				print.getValue("CONT_ADDRESS", 0));// ��ϵ�˵�ַ
		result.setData("CONT_TEL", "TEXT", print.getValue("CONT_TEL", 0));// ��ϵ�˵绰
		/*---------------------------------������Ϣ����-------------------------------------------------*/
		result.setData("ADM_SOURCE", "TEXT",
				this.getNewadmSource(print.getValue("ADM_SOURCE", 0)));// ��Ժ;��
		result.setData("IN_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("IN_DATE", 0), "yyyy��MM��dd�� HHʱ"));// ��Ժ����
		result.setData(
				"IN_DEPT",
				"TEXT",
				getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						print.getValue("IN_DEPT", 0)));// ��Ժ�Ʊ�
		result.setData(
				"IN_ROOM",
				"TEXT",
				getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
						print.getValue("IN_STATION", 0)));// ��Ժ����
		result.setData("TRANS_DEPT", "TEXT",
				this.getLineTrandept(getTranDept(CASE_NO)));// ת�ƿƱ� wanglong
															// modify 20150115
		result.setData("OUT_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("OUT_DATE", 0), "yyyy��MM��dd�� HHʱ"));// ��Ժ����

		if (print.getData("OUT_DATE", 0) != null) {// �ж��Ƿ��Ժ
			result.setData(
					"OUT_DEPT",
					"TEXT",
					getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
							print.getValue("OUT_DEPT", 0))); // ��Ժ����
			result.setData(
					"OUT_ROOM",
					"TEXT",
					getDesc("SYS_STATION", "", "STATION_DESC", "STATION_CODE",
							print.getValue("OUT_STATION", 0))); // ��Ժ����
		} else {
			result.setData("OUT_DEPT", "TEXT", "");
			result.setData("OUT_ROOM", "TEXT", "");
		}
		result.setData("REAL_STAY_DAYS", "TEXT", realStayDays);// ʵ��סԺ����
		// ������Ժ���CDA����
		String inConditionCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"", "HR55.01.044",
				print.getValue("IN_CONDITION", 0).replace("0", ""));
		result.setData("HR55.01.044", "TEXT", inConditionCDAValue);
		TParm diagnosis = new TParm(
				this.getDBTool()
						.select("SELECT IO_TYPE AS TYPE,ICD_DESC AS NAME,ICD_CODE AS CODE,MAIN_FLG AS MAIN,"
								+ " ICD_STATUS AS STATUS,IN_PAT_CONDITION,ADDITIONAL_CODE AS ADDITIONAL,ADDITIONAL_DESC,ICD_REMARK AS REMARK FROM MRO_RECORD_DIAG WHERE CASE_NO='"
								+ CASE_NO
								+ "' ORDER BY DECODE(IO_TYPE,'I','1','O','2','Q','3','W','4',IO_TYPE),MAIN_FLG DESC,SEQ_NO")); // ��ϼ�¼
																																// modify
																																// by
		//	IO_TYPE: 		O��Ժ��� 		Q��Ⱦ���																							// wanglong
																																// 20140411
		int outindex = 2;
		String OE_DIAG_CODE = "";
		String OE_DIAG_DESC = "";
		boolean q = true;
		boolean r = true;
		boolean typeO= false;
		boolean flg =false;
		for (int j = 0; j < diagnosis.getCount(); j++) {		// ���ҽ��վֻ��д��Ժ����ϣ�û��д��Ժ��ϵģ���Ժ����ϵ�������ʾ�ɳ�Ժ���
			if (diagnosis.getValue("TYPE", j).equals("O")) {
				typeO=true;
					
			}
			if (diagnosis.getValue("TYPE", j).equals("Q")) {
				q =false;
			}
		}
		if(!q && !typeO){//�� Q û��O
			flg =true;
		}
		
		for (int j = 0; j < diagnosis.getCount(); j++) {	//�����Ժ��Ϻ�Ժ����϶����ڲ�������ͬ����ϱ���, ȥ��Ժ�����

		if (diagnosis.getValue("TYPE", j).equals("Q")) {
			for (int i = 0; i < diagnosis.getCount(); i++) {
				if (diagnosis.getValue("TYPE", i).equals("O")) {
					if (diagnosis.getValue("CODE", j).equals(
							diagnosis.getValue("CODE", i))) {
						r = false;
						break;
					}
				}
				if(!r){
					break;
				}

			}
		}
		}
		if (diagnosis.getCount() > 0) {
			for (int j = 0; j < diagnosis.getCount(); j++) {
				if (diagnosis.getValue("TYPE", j).equals("I")) {

					if (OE_DIAG_DESC.length() > 0) {// ��ϴ��룺���������룩
						OE_DIAG_DESC += "," + diagnosis.getValue("NAME", j);
					} else {
						OE_DIAG_DESC = diagnosis.getValue("NAME", j);
					}
					if (OE_DIAG_CODE.length() > 0) {
						OE_DIAG_CODE += "," + diagnosis.getValue("CODE", j);
					} else {
						OE_DIAG_CODE = diagnosis.getValue("CODE", j);
					}
					result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// �ż������
					result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// �ż�����ϼ�������
				}
				if (diagnosis.getValue("TYPE", j).equals("O")) {
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ����ϼ�������
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ����ϼ�������
						result.setData("DIAG_TYPE1", "TEXT", ""); // ��Ժ����ϼ�������
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�������Ժ����
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ��ϼ�������
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ��ϼ�������
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"�������:"); // ��Ժ��ϼ�������
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // ��Ժ��ϼ�������
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�����Ժ����
						outindex++;
					}
				}

				

				if (flg) {
				if (diagnosis.getValue("TYPE", j).equals("Q")) {
				
					if (diagnosis.getValue("MAIN", j).equals("Y")) {
						result.setData("DIAG_CODE1", "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ����ϼ�������
						result.setData(
								"DIAG1",
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ����ϼ�������
						result.setData("DIAG_TYPE1", "TEXT", ""); // ��Ժ����ϼ�������
						result.setData("DIAG_CONDITION1", "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�������Ժ����
					} else {
						result.setData("DIAG_CODE" + outindex, "TEXT",
								diagnosis.getValue("CODE", j)); // ��Ժ��ϼ�������
						result.setData(
								"DIAG" + outindex,
								"TEXT",
								diagnosis.getValue("NAME", j)
										+ addBrace(diagnosis.getValue("REMARK",
												j))); // ��Ժ��ϼ�������
						if (outindex == 2 || outindex == 8)
							result.setData("DIAG_TYPE" + outindex, "TEXT",
									"�������:"); // ��Ժ��ϼ�������
						else
							result.setData("DIAG_TYPE" + outindex, "TEXT", ""); // ��Ժ��ϼ�������
						result.setData("DIAG_CONDITION" + outindex, "TEXT",
								diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ�����Ժ����
						outindex++;
					}
				}
				}else{
					if (r) {
						if (diagnosis.getValue("TYPE", j).equals("Q")) {
							result.setData("DIAG_CODE" + outindex, "TEXT",
									diagnosis.getValue("CODE", j)); // ��Ⱦ��ϼ�������
							result.setData(
									"DIAG" + outindex,
									"TEXT",
									diagnosis.getValue("NAME", j)
											+ addBrace(diagnosis.getValue("REMARK",
													j))); // ��Ⱦ��ϼ�������
							result.setData("DIAG_TYPE" + outindex, "TEXT", "");
							result.setData("DIAG_CONDITION" + outindex, "TEXT",
									diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ⱦ�����Ժ����
							outindex++;
						}
					}
				}
				if (diagnosis.getValue("TYPE", j).equals("W")) {// ����֢��� add by
																// wanglong
																// 20140411
					result.setData("DIAG_CODE" + outindex, "TEXT",
							diagnosis.getValue("CODE", j)); // ��������
					result.setData("DIAG" + outindex, "TEXT",
							"  " + diagnosis.getValue("NAME", j)
									+ addBrace(diagnosis.getValue("REMARK", j))); // ��������
					// result.setData("DIAG_TYPE"+outindex, "TEXT","����֢���:"); //
					// ��������
					result.setData("DIAG_TYPE" + outindex, "TEXT", "");
					result.setData("DIAG_CONDITION" + outindex, "TEXT",
							diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ����
					outindex++;
				}
			}
		}
		if (diagnosis.getErrCode() < 0) {
			System.out.println("��ѯMRO_RECORD_DIAG����������ݴ���");
		}
		// ======================== wanglong add 20141223
		// ���ӳ�Ժ��ϸ�ҳ����14��֮��ĳ�Ժ��Ϸ��ڸ�ҳ����ʾ��
		for (int i = diagnosis.getCount() - 1; i >= 0; i--) {
			if (!diagnosis.getValue("TYPE", i).equals("O")
					&& !diagnosis.getValue("TYPE", i).equals("Q")
					&& !diagnosis.getValue("TYPE", i).equals("W")) {
				diagnosis.removeRow(i);
			}
		}
		if (diagnosis.getCount("CODE") > 14) {
			TParm addDiagTable = new TParm();
			for (int j = 14; j < diagnosis.getCount("CODE"); j++) {
				if (diagnosis.getValue("TYPE", j).equals("O")) {// ��Ժ���
					addDiagTable.addData("DIAG_TYPE_" + (j % 2), "�������:"); // �������
				} else if (diagnosis.getValue("TYPE", j).equals("Q")) { // ��Ⱦ���
					addDiagTable.addData("DIAG_TYPE_" + (j % 2), "��Ⱦ���:"); // �������
				} else if (diagnosis.getValue("TYPE", j).equals("W")) {// ����֢���
					addDiagTable.addData("DIAG_TYPE_" + (j % 2), "����֢���:"); // �������
				} else {
					continue;
				}
				addDiagTable.addData(
						"DIAG_DESC_" + (j % 2),
						diagnosis.getValue("NAME", j)
								+ addBrace(diagnosis.getValue("REMARK", j))); // �������
				addDiagTable.addData("DIAG_CODE_" + (j % 2),
						diagnosis.getValue("CODE", j)); // ��������
				addDiagTable.addData("DIAG_CONDITION_" + (j % 2),
						diagnosis.getValue("IN_PAT_CONDITION", j)); // ��Ժ����
				addDiagTable.setData("TABLE_VALUE",
						addDiagTable.getCount("DIAG_TYPE_0") - 1,
						"#0.Bold=Y;#4.Bold=Y");
			}
			if (diagnosis.getCount() % 2 == 1) {
				addDiagTable.addData("DIAG_TYPE_1", ""); // �������
				addDiagTable.addData("DIAG_DESC_1", ""); // �������
				addDiagTable.addData("DIAG_CODE_1", ""); // ��������
				addDiagTable.addData("DIAG_CONDITION_1", ""); // ��Ժ����
			}
			addDiagTable.setCount(addDiagTable.getCount("DIAG_TYPE_0"));
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_TYPE_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_DESC_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CODE_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CONDITION_0");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_TYPE_1");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_DESC_1");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CODE_1");
			addDiagTable.addData("SYSTEM", "COLUMNS", "DIAG_CONDITION_1");
			result.setData("ADD_DIAG_TAB", addDiagTable.getData());
			TParm addDiagTitle = new TParm();
			addDiagTitle.addData("COL1", " ");
			addDiagTitle.setCount(addDiagTitle.getCount("COL1"));
			addDiagTitle.addData("SYSTEM", "COLUMNS", "COL1");
			result.setData("ADD_DIAG_TITLE", addDiagTitle.getData());
			TParm addDiagTableTitle = new TParm();
			addDiagTableTitle.addData("COL1", " ");
			addDiagTableTitle.setCount(addDiagTableTitle.getCount("COL1"));
			addDiagTableTitle.addData("SYSTEM", "COLUMNS", "COL1");
			result.setData("ADD_DIAG_TABLE_TITLE", addDiagTableTitle.getData());
		}
		// ======================== add end
		// shibl 20120618 modify
		// String OE_DIAG_DESC = getICD_DESC(print.getValue("OE_DIAG_CODE", 0));
		// String OE_DIAG_CODE = print.getValue("OE_DIAG_CODE", 0);
		// if (print.getValue("OE_DIAG_CODE2", 0).length() > 0) {// ��ϴ��룺���������룩
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE2", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE2", 0);
		// }
		// if (print.getValue("OE_DIAG_CODE3", 0).length() > 0) {
		// OE_DIAG_DESC += ","
		// + getICD_DESC(print.getValue("OE_DIAG_CODE3", 0));
		// OE_DIAG_CODE += "," + print.getValue("OE_DIAG_CODE3", 0);
		// }
		// result.setData("OE_DIAG", "TEXT", OE_DIAG_DESC);// �ż������
		// result.setData("OE_DIAG_CODE", "TEXT", OE_DIAG_CODE);// �ż�����ϼ�������
		// /*---------------------------------סԺ����-----------------------------------------------------*/
		// result.setData("DIAG1", "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE1", 0))); // ��Ժ�����
		// // result.setData("DIAG2", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE2", 0))); // ��Ժ���2
		// // result.setData("DIAG3", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE3", 0))); // ��Ժ���3
		// // result.setData("DIAG4", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE4", 0))); // ��Ժ���4
		// // result.setData("DIAG5", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE5", 0))); // ��Ժ���5
		// // result.setData("DIAG6", "TEXT",
		// // getICD_DESC(print.getValue("OUT_DIAG_CODE6", 0))); // ��Ժ���6
		// result.setData("DIAG_CODE1", "TEXT",
		// print.getValue("OUT_DIAG_CODE1", 0)); // ��Ժ����ϼ�������
		// // result.setData("DIAG_CODE2", "TEXT",
		// // print.getValue("OUT_DIAG_CODE2", 0)); // ��Ժ���2��������
		// // result.setData("DIAG_CODE3", "TEXT",
		// // print.getValue("OUT_DIAG_CODE3", 0)); // ��Ժ���3��������
		// // result.setData("DIAG_CODE4", "TEXT",
		// // print.getValue("OUT_DIAG_CODE4", 0)); // ��Ժ���4��������
		// // result.setData("DIAG_CODE5", "TEXT",
		// // print.getValue("OUT_DIAG_CODE5", 0)); // ��Ժ���5��������
		// // result.setData("DIAG_CODE6", "TEXT",
		// // print.getValue("OUT_DIAG_CODE6", 0)); // ��Ժ���6��������
		// result.setData("DIAG_CONDITION1", "TEXT",
		// print.getValue("OUT_DIAG_CONDITION1", 0)); // ��Ժ�������Ժ����
		// // result.setData("DIAG_CONDITION2", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION2", 0)); // ��Ժ���2��Ժ����
		// // result.setData("DIAG_CONDITION3", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION3", 0)); // ��Ժ���3��Ժ����
		// // result.setData("DIAG_CONDITION4", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION4", 0)); // ��Ժ���4��Ժ����
		// // result.setData("DIAG_CONDITION5", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION5", 0)); // ��Ժ���5��Ժ����
		// // result.setData("DIAG_CONDITION6", "TEXT",
		// // print.getValue("OUT_DIAG_CONDITION6", 0)); // ��Ժ���6��Ժ����
		// int seq = 2;
		// for (int i = 2; i < 7; i++) {
		// if (!print.getValue("OUT_DIAG_CODE" + i, 0).equals("")) {
		// result.setData("DIAG" + seq, "TEXT",
		// getICD_DESC(print.getValue("OUT_DIAG_CODE" + i, 0)));
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CODE" + i, 0));
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("OUT_DIAG_CONDITION" + i, 0));
		// if (seq == 2)
		// result.setData("DIAG_TYPE" + seq, "TEXT", "�������:");
		// seq++;
		// }
		// }
		// String INTE_DIAG_CODE = getICD_DESC(print.getValue(
		// "INTE_DIAG_CODE", 0));
		// result.setData("DIAG" + seq, "TEXT", INTE_DIAG_CODE);// Ժ�ڸ�Ⱦ���
		// // ��ȷ��
		// result.setData("DIAG_CODE" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CODE", 0));// Ժ�ڸ�Ⱦ���CODE
		// // ��ȷ��
		// result.setData("DIAG_CONDITION" + seq, "TEXT",
		// print.getValue("INTE_DIAG_CONDITION", 0));// Ժ�ڸ�Ⱦ�����Ժ����
		// result.setData("DIAG_TYPE" + seq, "TEXT", "��Ⱦ���:");
		/*----------------------------------------��ϱ�---------------------------------------------------*/
		String PATHOLOGY_DIAG = getICD_DESC(print.getValue("PATHOLOGY_DIAG", 0));
		if (null != PATHOLOGY_DIAG && !"".equals(PATHOLOGY_DIAG))
			result.setData("PATHOLOGY_DIAG", "TEXT", PATHOLOGY_DIAG);// �������
		else
			result.setData("PATHOLOGY_DIAG", "TEXT", "-");// �������

		if (null != print.getValue("PATHOLOGY_DIAG", 0)
				&& !"".equals(print.getValue("PATHOLOGY_DIAG", 0)))
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT",
					print.getValue("PATHOLOGY_DIAG", 0));// ������ϼ�������
		else
			result.setData("PATHOLOGY_DIAG_CODE", "TEXT", "-");// ������ϼ�������

		if (null != print.getValue("PATHOLOGY_NO", 0)
				&& !"".equals(print.getValue("PATHOLOGY_NO", 0)))
			result.setData("PATHOLOGY_NO", "TEXT",
					print.getValue("PATHOLOGY_NO", 0));// �����
		else
			result.setData("PATHOLOGY_NO", "TEXT", "-");// �����

		String EX_RSN = getICD_DESC(print.getValue("EX_RSN", 0));
		if (null != EX_RSN && !"".equals(EX_RSN))
			result.setData("EX_RSN", "TEXT", EX_RSN);// ���ˡ��ж����ⲿ����
		else
			result.setData("EX_RSN", "TEXT", "-");// ���ˡ��ж����ⲿ����

		if (null != print.getValue("EX_RSN", 0)
				&& !"".equals(print.getValue("EX_RSN", 0)))
			result.setData("EX_RSN_CODE", "TEXT", print.getValue("EX_RSN", 0));// ���ˡ��ж����ⲿ���ؼ�������
		else
			result.setData("EX_RSN_CODE", "TEXT", "-");// ���ˡ��ж����ⲿ���ؼ�������

		result.setData("ALLEGIC_CODE", "TEXT",
				this.getcheckStr(print.getValue("ALLEGIC_FLG", 0)));// �Ƿ���ҩ�����
		result.setData("ALLEGIC", "TEXT", print.getValue("ALLEGIC", 0));// ҩ�����
		result.setData("BODY_CHECK", "TEXT",
				this.getcheckStr(print.getValue("BODY_CHECK", 0)));// ʬ��
		result.setData("BLOOD_TYPE", "TEXT", print.getValue("BLOOD_TYPE", 0));// Ѫ��
		// ����RH
		String rhType = print.getValue("RH_TYPE", 0);
		result.setData("RH_TYPE", "TEXT", rhType);// RH
		result.setData("DIRECTOR_DR_CODE", "TEXT",
				drList.get(print.getValue("DIRECTOR_DR_CODE", 0)));// ������
		result.setData("PROF_DR_CODE", "TEXT",
				drList.get(print.getValue("PROF_DR_CODE", 0)));// ����ҽʦ
		result.setData("ATTEND_DR_CODE", "TEXT",
				drList.get(print.getValue("ATTEND_DR_CODE", 0)));// ����ҽʦ
		result.setData("VS_DR_CODE", "TEXT",
				drList.get(print.getValue("VS_DR_CODE", 0)));// סԺҽʦ
		result.setData("INDUCATION_DR_CODE", "TEXT",
				print.getValue("INDUCATION_DR_CODE", 0));// ����ҽʦ duzhw modify by
															// 20131204
		result.setData("VS_NURSE_CODE", "TEXT",
				drList.get(print.getValue("VS_NURSE_CODE", 0)));// ���λ�ʿ
		result.setData("INTERN_DR_CODE", "TEXT",
				print.getValue("INTERN_DR_CODE", 0));// ʵϰҽʦ duzhw modify by
														// 20131204
		result.setData("ENCODER", "TEXT",
				drList.get(print.getValue("ENCODER", 0)));// ����Ա
		result.setData("QUALITY", "TEXT", print.getValue("QUALITY", 0));// ��������
		// ���ò�������
		String QUALITYCDAValue = EMRCreateXMLTool.getInstance().getCDACode(
				"S.11.002", "HR55.01.049", print.getValue("QUYCHK_RAPA", 0));
		result.setData("HR55.01.049", "TEXT", QUALITYCDAValue);
		result.setData("CTRL_DR", "TEXT",
				drList.get(print.getValue("CTRL_DR", 0)));// �ʿ�ҽʦ
		result.setData("CTRL_NURSE", "TEXT",
				drList.get(print.getValue("CTRL_NURSE", 0)));// �ʿػ�ʿ
		result.setData("CTRL_DATE", "TEXT", StringTool.getString(
				print.getTimestamp("CTRL_DATE", 0), "yyyy��MM��dd��"));// �ʿ�����
		/*-------------------------------------------------------------------------------------------*/
		// ��ѯ������Ϣ
		SimpleDateFormat dt = new SimpleDateFormat("yyyy/MM/dd");
		TParm op_date = MRORecordTool.getInstance().queryPrintOP(CASE_NO);
		// System.out.println("------op_date---------"+op_date);
		TParm anaParm = getOP_DATA(op_date);
		// System.out.println("-=-=------------------"+anaParm);
		int index = 2;
		for (int i = 0; i < op_date.getCount(); i++) {
			if (op_date.getValue("MAIN_FLG", i).equals("Y")) {
				result.setData("OPE_CODE1", "TEXT",
						op_date.getValue("OP_CODE", i));// ��������
				result.setData("OPE_DATE1", "TEXT",
						op_date.getValue("OP_DATE", i));// ��������
				result.setData("OPE_LEVEL1", "TEXT",
						op_date.getValue("OP_LEVEL", i));// ��������
				result.setData("OPE_DESC1", "TEXT",
						op_date.getValue("OP_DESC", i));// ��������
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// ����
				} else {
					result.setData("MAIN_SUGEON1", "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// ����
				}
				result.setData("AST_DR11", "TEXT",
						op_date.getValue("AST_DR1", i));// ����1
				result.setData("AST_DR21", "TEXT",
						op_date.getValue("AST_DR2", i));// ����2
				result.setData("HEL1", "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// ���ϵȼ�
				result.setData("ANA_WAY1", "TEXT",
						anaParm.getValue("ANA_WAY", i));// ����ʽ
				result.setData("ANA_DR1", "TEXT", op_date.getValue("ANA_DR", i));// ����ʦ
			} else {
				result.setData("OPE_CODE" + index, "TEXT",
						op_date.getValue("OP_CODE", i));// ��������
				result.setData("OPE_DATE" + index, "TEXT",
						op_date.getValue("OP_DATE", i));// ��������
				result.setData("OPE_LEVEL" + index, "TEXT",
						op_date.getValue("OP_LEVEL", i));// ��������
				result.setData("OPE_DESC" + index, "TEXT",
						op_date.getValue("OP_DESC", i));// ��������
				if (op_date.getValue("MAIN_SUGEON_REMARK", i) != null
						&& !op_date.getValue("MAIN_SUGEON_REMARK", i)
								.equals("")) {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON_REMARK", i));// ����
				} else {
					result.setData("MAIN_SUGEON" + index, "TEXT",
							op_date.getValue("MAIN_SUGEON", i));// ����
				}
				result.setData("AST_DR1" + index, "TEXT",
						op_date.getValue("AST_DR1", i));// ����1
				result.setData("AST_DR2" + index, "TEXT",
						op_date.getValue("AST_DR2", i));// ����2
				result.setData("HEL" + index, "TEXT",
						op_date.getValue("HEALTH_LEVEL", i));// ���ϵȼ�
				result.setData("ANA_WAY" + index, "TEXT",
						anaParm.getValue("ANA_WAY", i));// ����ʽ
				result.setData("ANA_DR" + index, "TEXT",
						op_date.getValue("ANA_DR", i));// ����ʦ
				index++;
			}
		}
		result.setData("OUT_TYPE", "TEXT", print.getValue("OUT_TYPE", 0));// ��Ժ��ʽ
		if (print.getValue("OUT_TYPE", 0).equals("2"))
			result.setData(
					"TRAN_HOSP1",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// ��תԺ��
																// ����999999�����Զ���
																// 20120918
																// shibl
		if (print.getValue("OUT_TYPE", 0).equals("3"))
			result.setData(
					"TRAN_HOSP2",
					"TEXT",
					print.getValue("TRAN_HOSP", 0).equals("999999") ? print
							.getValue("TRAN_HOSP_OTHER", 0) : getDesc(
							"SYS_TRN_HOSP", "", "HOSP_DESC", "HOSP_CODE",
							print.getValue("TRAN_HOSP", 0)));// ��ת����
																// ����999999�����Զ���
																// 20120918
																// shibl
		if (print.getValue("BE_COMA_TIME", 0).equals("")) {
			result.setData("BE_COMA_TIME", "TEXT", "-" + "��" + "-" + "Сʱ" + "-"
					+ "����");// ��Ժǰ����ʱ��
		} else {
			result.setData(
					"BE_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("BE_COMA_TIME", 0)
							.substring(0, 2))
							+ "��"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(2, 4))
							+ "Сʱ"
							+ Integer.parseInt(print
									.getValue("BE_COMA_TIME", 0)
									.substring(4, 6)) + "����");// ��Ժǰ����ʱ��
		}
		if (print.getValue("AF_COMA_TIME", 0).equals("")) {
			result.setData("AF_COMA_TIME", "TEXT", "-" + "��" + "-" + "Сʱ" + "-"
					+ "����");// ��Ժ�����ʱ��
		} else {
			result.setData(
					"AF_COMA_TIME",
					"TEXT",
					Integer.parseInt(print.getValue("AF_COMA_TIME", 0)
							.substring(0, 2))
							+ "��"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(2, 4))
							+ "Сʱ"
							+ Integer.parseInt(print
									.getValue("AF_COMA_TIME", 0)
									.substring(4, 6)) + "����");// ��Ժ�����ʱ��
		}
		String agnFlg = "";
		if (print.getValue("AGN_PLAN_FLG", 0).equals("Y")) {
			agnFlg = "2";
		} else {
			agnFlg = "1";
		}
		result.setData("AGN_PLAN_FLG", "TEXT", agnFlg);// 31��ƻ����
		result.setData("AGN_PLAN_INTENTION", "TEXT",
				this.getcheckStr(print.getValue("AGN_PLAN_INTENTION", 0)));// 31��ƻ�ԭ��
		/*------------------------------------���ô�ȷ��------------------------------------------------*/
		DecimalFormat df = new DecimalFormat("0.00");
		result.setData("SUMTOT", "TEXT",
				df.format(print.getDouble("SUM_TOT", 0)));
		result.setData("OWN_TOT", "TEXT",
				df.format(print.getDouble("OWN_TOT", 0)));
		Map MrofeeCode = MRORecordTool.getInstance().getMROChargeName();
		// һ��ҽ�Ʒ����
		result.setData("CHARGE_01", "TEXT",
				df.format(print.getDouble("CHARGE_01", 0)));
		// һ�����Ʋ�����
		result.setData("CHARGE_02", "TEXT",
				df.format(print.getDouble("CHARGE_02", 0)));
		// �����
		result.setData("CHARGE_03", "TEXT",
				df.format(print.getDouble("CHARGE_03", 0)));
		// ��������
		result.setData("CHARGE_04", "TEXT",
				df.format(print.getDouble("CHARGE_04", 0)));
		// ������Ϸ�
		result.setData("CHARGE_05", "TEXT",
				df.format(print.getDouble("CHARGE_05", 0)));
		// ʵ������Ϸ�
		result.setData("CHARGE_06", "TEXT",
				df.format(print.getDouble("CHARGE_06", 0)));
		// Ӱ��ѧ��Ϸ�
		result.setData("CHARGE_07", "TEXT",
				df.format(print.getDouble("CHARGE_07", 0)));
		// �ٴ������Ŀ��
		result.setData("CHARGE_08", "TEXT",
				df.format(print.getDouble("CHARGE_08", 0)));
		// ���������Ʒ���
		result.setData(
				"CHARGE_09",
				"TEXT",
				df.format(print.getDouble("CHARGE_09", 0)
						+ print.getDouble("CHARGE_10", 0)));
		// �ٴ��������Ʒ�
		result.setData("CHARGE_10", "TEXT",
				df.format(print.getDouble("CHARGE_9", 0)));
		// �������Ʒ�
		result.setData(
				"CHARGE_11",
				"TEXT",
				df.format(print.getDouble("CHARGE_11", 0)
						+ print.getDouble("CHARGE_12", 0)
						+ print.getDouble("CHARGE_13", 0)));
		// �������Ʒ�-�����
		result.setData("CHARGE_12", "TEXT",
				df.format(print.getDouble("CHARGE_11", 0)));
		// �������Ʒ�-������
		result.setData("CHARGE_13", "TEXT",
				df.format(print.getDouble("CHARGE_12", 0)));
		// ������
		result.setData("CHARGE_14", "TEXT",
				df.format(print.getDouble("CHARGE_14", 0)));
		// ��ҽ���Ʒ�
		result.setData("CHARGE_15", "TEXT",
				df.format(print.getDouble("CHARGE_15", 0)));
		// ��ҩ����
		result.setData(
				"CHARGE_16",
				"TEXT",
				df.format(print.getDouble("CHARGE_16", 0)
						+ print.getDouble("CHARGE_17", 0)));
		// ����ҩ�����
		result.setData("CHARGE_17", "TEXT",
				df.format(print.getDouble("CHARGE_16", 0)));
		// �г�ҩ��
		result.setData("CHARGE_18", "TEXT",
				df.format(print.getDouble("CHARGE_18", 0)));
		// �в�ҩ��
		result.setData("CHARGE_19", "TEXT",
				df.format(print.getDouble("CHARGE_19", 0)));
		// Ѫ��
		result.setData("CHARGE_20", "TEXT",
				df.format(print.getDouble("CHARGE_20", 0)));
		// �׵�������Ʒ��
		result.setData("CHARGE_21", "TEXT",
				df.format(print.getDouble("CHARGE_21", 0)));
		// �򵰰�����Ʒ��
		result.setData("CHARGE_22", "TEXT",
				df.format(print.getDouble("CHARGE_22", 0)));
		// ��Ѫ��������Ʒ��
		result.setData("CHARGE_23", "TEXT",
				df.format(print.getDouble("CHARGE_23", 0)));
		// ϸ����������Ʒ��
		result.setData("CHARGE_24", "TEXT",
				df.format(print.getDouble("CHARGE_24", 0)));
		// �����һ����ҽ�ò��Ϸ�
		result.setData("CHARGE_25", "TEXT",
				df.format(print.getDouble("CHARGE_25", 0)));
		// ������һ����ҽ�ò��Ϸ�
		result.setData("CHARGE_26", "TEXT",
				df.format(print.getDouble("CHARGE_26", 0)));
		// ������һ����ҽ�ò��Ϸ�
		result.setData("CHARGE_27", "TEXT",
				df.format(print.getDouble("CHARGE_27", 0)));
		// ������
		result.setData("CHARGE_28", "TEXT",
				df.format(print.getDouble("CHARGE_28", 0)));

		// 2013-5-23 zhangh modify ������֢�໤���ͺ�����ʹ��ʱ��
		result.setData("VENTI_TIME", "TEXT", print.getData("VENTI_TIME", 0));// ������ʹ��ʱ��

		// ��֢�໤�������
		for (int i = 1; i < 6; i++) {
			String inDate = "", icuInDate = "", outDate = "", icuOutDate = "";
			if (print.getData("ICU_IN_DATE" + i, 0) != null) {
				inDate = print
						.getData("ICU_IN_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_IN_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// �õ����ݿ��еĽ���ʱ��
			}
			if (print.getData("ICU_OUT_DATE" + i, 0) != null) {
				outDate = print
						.getData("ICU_OUT_DATE" + i, 0)
						.toString()
						.substring(
								0,
								print.getData("ICU_OUT_DATE" + i, 0).toString()
										.lastIndexOf(".")).replace("-", "/");// �õ����ݿ��е��˳�ʱ��
			}
			icuInDate = getInOutDate(inDate);
			icuOutDate = getInOutDate(outDate);
			// ��ȡ�����������
			String deptCode = print.getValue("ICU_ROOM" + i, 0);
			String deptDesc = MRORecordTool.getInstance().getRoomDesc(deptCode);
			result.setData("IN_DATE_" + i, "TEXT", icuInDate);
			result.setData("OUT_DATE_" + i, "TEXT", icuOutDate);
			result.setData("ICU_ROOM_" + i, "TEXT", deptDesc);
		}

		// wanglong add 20141103 �����ַ�����Ϊ-
		Map b = (Map) result.getData();
		Set c = b.keySet();
		for (Object object : c) {
			if (!object.equals(TParm.DEFAULT_GROUP)) {
				if (object.toString().indexOf("DIAG_TYPE_") != -1
						|| object.toString().indexOf("DIAG_DESC_") != -1
						|| object.toString().indexOf("DIAG_CODE_") != -1
						|| object.toString().indexOf("DIAG_CONDITION_") != -1
						|| object.toString().indexOf("IN_DATE_") != -1
						|| object.toString().indexOf("OUT_DATE_") != -1
						|| object.toString().indexOf("ICU_ROOM_") != -1
						|| object.toString().matches("DIAG_CODE[0-9]+")
						|| object.toString().matches("DIAG[0-9]+")
						|| object.toString().matches("DIAG_TYPE[0-9]+")
						|| object.toString().matches("DIAG_CONDITION[0-9]+")) {// wanglong
																				// add
																				// 20150227
					continue;
				}
				if (result.getValue(object.toString(), "TEXT").trim()
						.equals("")) {
					result.setData(object.toString(), "TEXT", "-");
				}
			} else {
				String[] name = result.getNames();
				for (String str : name) {
					if (str.indexOf("DIAG_TYPE_") != -1
							|| str.indexOf("DIAG_DESC_") != -1
							|| str.indexOf("DIAG_CODE_") != -1
							|| str.indexOf("DIAG_CONDITION_") != -1
							|| str.indexOf("IN_DATE_") != -1
							|| str.indexOf("OUT_DATE_") != -1
							|| str.indexOf("ICU_ROOM_") != -1
							|| object.toString().matches("DIAG_CODE[0-9]+")
							|| object.toString().matches("DIAG[0-9]+")
							|| object.toString().matches("DIAG_TYPE[0-9]+")
							|| object.toString()
									.matches("DIAG_CONDITION[0-9]+")) {// wanglong
																		// add
																		// 20150227
						continue;
					}
					if (result.getValue(str).trim().equals("")) {
						result.setData(str, "-");
					}
				}
			}
		}
		return result;
	}

	private String getInOutDate(String date) {
		String icuDate = "", hour = "";
		if (date != null && date.length() >= 10) {
			String[] inDateParts = date.split(" ");
			String[] inYmd = inDateParts[0].split("/");
			inYmd[0] += "��";
			inYmd[1] += "��";
			inYmd[2] += "��";
			if (date.length() > 10) {
				String[] inHms = inDateParts[1].split(":");
				hour = inHms[0] + "ʱ";
			}
			for (String string : inYmd) {
				icuDate += string;
			}
			icuDate += hour;
		}
		return icuDate;
	}

	/**
	 * �滻����
	 * 
	 * @param TableName
	 *            String ����
	 * @param groupID
	 *            String ����
	 * @param descColunm
	 *            String ��������
	 * @param codeColunm
	 *            String code����
	 * @param code
	 *            String ����
	 * @return String
	 */
	public String getAnaMayDesc(String TableName, String groupID,
			String descColunm, String codeColunm, String code) {
		// TDataStore dataStore = new TDataStore();
		String SQL = "SELECT " + descColunm + " FROM " + TableName;
		String where = "";
		if (groupID.trim().length() > 0) {
			where += " WHERE GROUP_ID='" + groupID + "'";
		}
		if (descColunm.length() > 0) {
			if (where.length() > 0) {
				where += " AND " + codeColunm + " = '" + code.trim() // +
																		// " "//delete
																		// by
																		// wanglong
																		// 20120921
																		// ���¿��У��ַ�����󲻴��ո�
						+ "'";
			} else {
				where += " WHERE " + codeColunm + " = '" + code.trim() // +
																		// " "//delete
																		// by
																		// wanglong
																		// 20120921
																		// ���¿��У��ַ�����󲻴��ո�
						+ "'";
			}
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL + where));
		return result.getValue(descColunm, 0);
	}

	/**
	 * �滻����
	 * 
	 * @param TableName
	 *            String ����
	 * @param groupID
	 *            String ����
	 * @param descColunm
	 *            String ��������
	 * @param codeColunm
	 *            String code����
	 * @param code
	 *            String ����
	 * @return String
	 */
	public String getDesc(String TableName, String groupID, String descColunm,
			String codeColunm, String code) {
		// TDataStore dataStore = new TDataStore();
		String SQL = "SELECT " + descColunm + " FROM " + TableName;
		String where = "";
		if (groupID.trim().length() > 0) {
			where += " WHERE GROUP_ID='" + groupID + "'";
		}
		if (descColunm.length() > 0) {
			if (where.length() > 0) {
				where += " AND " + codeColunm + " = '" + code + "'";
			} else {
				where += " WHERE " + codeColunm + " = '" + code + "'";
			}
		}
		TParm result = new TParm(TJDODBTool.getInstance().select(SQL + where));
		return result.getValue(descColunm, 0);
	}

	String filterICD;

	/**
	 * ��ȡ�������
	 * 
	 * @param ICD
	 *            String �����
	 * @return String
	 */
	private String getICD_DESC(String ICD) {
		filterICD = ICD;
		ICD_DATA.filterObject(this, "filterICD");
		return ICD_DATA.getItemString(0, "ICD_CHN_DESC");
	}

	/**
	 * ���˷���
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filterICD(TParm parm, int row) {
		return filterICD.equals(parm.getValue("ICD_CODE", row));
	}

	/**
	 * ��ȡ������ �������� ��ѯ���������µ���Ϣ��
	 * 
	 * @param CASE_NO
	 *            String
	 * @return String
	 */
	public String getChildWeight(String CASE_NO) {
		String sql = MROSqlTool.getInstance().getChildWeightSQL(CASE_NO);
		TParm result = new TParm();
		result.setData(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return "";
		}
		return result.getValue("BORNWEIGHT", 0);
	}

	/**
	 * ����������Ϣ
	 * 
	 * @param opParm
	 *            TParm
	 * @return TParm
	 */
	private TParm getOP_DATA(TParm opParm) {
		// ѭ���滻����ʽΪ����
		for (int i = 0; i < opParm.getCount(); i++) {
			// System.out.println("1-------------"+opParm.getValue("ANA_WAY",
			// i));
			String OP_DESC = this.getAnaMayDesc("SYS_DICTIONARY",
					"OPE_ANAMETHOD", "CHN_DESC", "ID",
					opParm.getValue("ANA_WAY", i));
			// System.out.println("2-------------"+OP_DESC);
			opParm.setData("ANA_WAY", i, OP_DESC);
		}
		return opParm;
	}

	/**
	 * ��ȡҽʦ�����б�
	 * 
	 * @return Map
	 */
	private Map getDrList() {
		String sql = "SELECT USER_ID,USER_NAME FROM SYS_OPERATOR";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		Map list = new HashMap();
		for (int i = 0; i < result.getCount(); i++) {
			list.put(result.getValue("USER_ID", i),
					result.getValue("USER_NAME", i));
		}
		return list;
	}

	/**
	 * ��ȡ���ֵ�����
	 */
	private void getDICTIONARY() {
		if (DICTIONARY == null) {
			String sql = "SELECT GROUP_ID,ID,CHN_DESC FROM SYS_DICTIONARY ";
			DICTIONARY = new TDataStore();
			DICTIONARY.setSQL(sql);
			DICTIONARY.retrieve();
		}
	}

	String DictionaryID;
	String DictionaryGroup;

	/**
	 * ��ȡ���ֵ��е�����
	 * 
	 * @param groupId
	 *            String
	 * @param Id
	 *            String
	 * @return String
	 */
	private String getDictionaryDesc(String groupId, String Id) {
		if (DICTIONARY == null) {
			return "";
		}
		DictionaryGroup = groupId;
		DictionaryID = Id;
		DICTIONARY.filterObject(this, "filterDictionary");
		return DICTIONARY.getItemString(0, "CHN_DESC");
	}

	/**
	 * ���˷���
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 * @return boolean
	 */
	public boolean filterDictionary(TParm parm, int row) {
		return DictionaryGroup.equals(parm.getValue("GROUP_ID", row))
				&& DictionaryID.equals(parm.getValue("ID", row));
	}

	/**
	 * ת�����ӷ�
	 * 
	 * @param str
	 * @return
	 */
	private String getLineTrandept(String str) {
		String line = "";
		String regex = "|";
		if (str.indexOf(regex) != -1) {
			String[] dept = str.split("[|]");
			for (int i = 0; i < dept.length; i++) {
				if (line.length() > 0) {
					line += "->";
				}
				line += getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE",
						dept[i]);
			}
		} else {
			line = getDesc("SYS_DEPT", "", "DEPT_CHN_DESC", "DEPT_CODE", str);
		}
		return line;
	}

	/**
	 * ��Ժ��� ת���ɹ��ҹ涨�ı��� 1.���� 2.���� 3.����ҽ�ƻ���ת�� 9.����
	 * 
	 * @param id
	 * @return
	 */
	private String getNewadmSource(String id) {
		String code = "";
		if (id.equals("01"))
			code = "2";
		else if (id.equals("02"))
			code = "1";
		else if (id.equals("09"))
			code = "3";
		else if (id.equals("99"))
			code = "9";
		else
			code = id;
		return code;
	}

	/**
	 * ����״̬ ת���ɹ��ҹ涨�ı��� 1.δ�飻2.�ѻ飻3.ɥż��4.��飻9.������
	 * 
	 * @param id
	 * @return
	 */
	private String getNewMarrige(String id) {
		String code = "";
		if (id.equals("3"))
			code = "4";
		else if (id.equals("4"))
			code = "3";
		else
			code = id;
		return code;
	}

	/**
	 * �����ַ����Ƿ�Ϊ�� ��ʱ��"-"ֵ
	 * 
	 * @param str
	 * @return
	 */
	private String getcheckStr(String str) {
		String line = "";
		if (str.trim().length() == 0)
			line = "-";
		else
			line = str;
		return line;
	}

	// /**
	// * ������1����ģ���ʵ���������Ӧ������д�����䲻��1����ģ�����ʵ�������������д���Է�����ʽ��ʾ��
	// * �������������ִ���ʵ�����䣬�������ַ�ĸΪ30������Ϊ����1���µ��������硰2 �¡�������ʵ������Ϊ2������15�졣
	// * @param odo
	// * @return String ������ʾ������
	// */
	// public String showAge(Timestamp birthday, Timestamp t) {
	// String age = "";
	// String[] res;
	// res = StringTool.CountAgeByTimestamp(birthday, t);
	// if (TypeTool.getInt(res[0]) < 1) {
	// age =res[1]+" "+ res[2] + "����";
	// } else {
	// age = res[0] + "��";
	// }
	// return age;
	// }
	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �����ּ�����
	 */
	public String addBrace(String str) {// wanglong add 20141120
		if (str == null || str.trim().length() <= 0) {
			return "";
		}
		return "(" + str + ")";
	}

	/**
	 * ��ת�ƿ����м��"|"
	 * 
	 * @param caseNo
	 * @return
	 */
	private String getTranDept(String caseNo) {// wanglong add 20150115
		if (TCM_Transform.isNull(caseNo)) {
			return "";
		}
		String str = "";
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		TParm tranParm = ADMTransLogTool.getInstance().getTranHospFormro(parm); // ת������
		for (int i = tranParm.getCount() - 2; i >= 0; i--) {// ȥ����һ�������һ��
			if (str.trim().length() > 0) {
				str += "|";
			}
			str += tranParm.getValue("IN_DEPT_CODE", i);
		}
		return str;
	}
}
