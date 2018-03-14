package jdo.hrm;

import java.sql.Timestamp;

import jdo.emr.GetWordValue;

import org.apache.commons.lang.StringUtils;

import com.dongyang.config.TConfig;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.javahis.util.OdiUtil;

/**
 * <p>
 * Title: ��챨�湤����
 * </p>
 * 
 * <p>
 * Description: ��챨�湤����
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: Bluecore
 * </p>
 * 
 * @author wangb 2017.2.10
 * @version 1.0
 */
public class HRMReportDataTool extends TJDOTool {
	
	/**
     * ʵ��
     */
    public static HRMReportDataTool instanceObject;
    /**
     * �ܼ챨�没��ģ��
     */
    private String zjSubClassCode = TConfig.getSystemValue("HRM.EMR_TEMPLET.SUB_CLASS_CODE.HER");
    
    /**
     * �õ�ʵ��
     * @return HRMReportDataTool
     */
    public static HRMReportDataTool getInstance() {
        if (instanceObject == null)
            instanceObject = new HRMReportDataTool();
        return instanceObject;
    }
    
    /**
     * �����챨������
     * 
     * @param parm ��ѯ����
     * @return
     */
    public TParm getReportData(TParm parm) {
    	TParm result = new TParm();
    	
    	if (StringUtils.isEmpty(zjSubClassCode)) {
    		result.setErr(-1, "�����ļ��л�ȡ�����ܼ챨��ṹ������ģ�����");
    		System.out.println(result.getErrText());
    		return result;
    	}
    	
    	TParm reportFileInfoResult = this.queryReportFileInfo(parm);
    	if (reportFileInfoResult.getErrCode() < 0) {
    		result.setErr(-1, "��ѯ�ܼ챨��ṹ�������ļ���Ϣ�쳣");
    		System.out.println(result.getErrText());
    		return result;
    	}
    	
    	int count = reportFileInfoResult.getCount("CASE_NO");
    	
    	// ���δ��ܼ챨���ļ���������
    	TParm fileParm = new TParm();
    	// ȡ�õ��ܼ챨���ļ����ݼ���
    	TParm reportData = new TParm();
    	// �ܼ챨��ְҵ�ڵ�
		String[] professionArray = new String[] { "SENIOR_MANAGEMENT",
				"MANAGER", "SALESPERSON", "MEDIA_INDUSTRY", "EDUCATORS",
				"RETAIL_SERVICES", "RESEARCHER", "IT_INDUSTRY",
				"LITERATURE_ART", "DRIVER", "MANUAL_WORKERS", "RETIRED",
				"FREELANCE", "OTHER_OCCUPATIONS" };
    	// �ܼ챨�����ʷ�ڵ�
		String[] pastHistoryArray = new String[] { "PH_HYPERTENSION",
				"PH_STROKE", "PH_CORONARY_HEART_DISEASE", "PH_MI",
				"PH_PULMONARY_HEART_DISEASE", "PH_DIABETES", "PH_FATTY_LIVER",
				"PH_GALLBLADDER_DISEASE", "PH_KIDNEY_DISEASE",
				"PH_TUBERCULOSIS", "PH_HEPATITIS", "PH_TUMOR",
				"PH_GYNECOLOGICAL_DISEASES", "PH_SURGICAL_TRAUMA",
				"PH_OTHER_DISEASE" };
    	// �ܼ챨�����ʷ�ڵ�
    	String[] personalHistoryArray = new String[]{"SURGICAL_TRAUMA","ALLERGY_HISTORY","SMOKING_HISTORY","DRINKING_HISTORY"};
    	// �ܼ챨�����ʷ�ڵ�
		String[] familyHistoryArray = new String[] { "FH_UNKNOWN",
				"FH_HYPERTENSION", "FH_STROKE", "FH_CORONARY_HEART_DISEASE",
				"FH_MI", "FH_DIABETES", "FH_CIRRHOSIS", "FH_TUBERCULOSIS",
				"FH_KIDNEY_DISEASE", "FH_MALIGNANT_NEOPLASMS", "FH_OTHER" };
		// �ܼ챨���ı�����ץȡ�ڵ�
		String[] textSign = { "NATIONALITY", "ETHNIC_GROUP", "ADDRESS",
				"E-MAIL", "COMPANY", "PH_STATUS_QUO", "OTHER_DISCOMFORT",
				"HEIGHT", "WEIGHT", "BMI", "BMI_REFERENCE_RANGE",
				"SYSTOLIC_BP", "SYSTOLIC_REFERENCE_RANGE", "DIASTOLIC_BP",
				"DIASTOLIC_REFERENCE_RANGE", "HEART_RATE", "HEART_RHYTHM",
				"HEART_SOUNDS", "HEART_BORDER", "CHEST", "LUNG", "ABDOMEN",
				"LIVER", "GALLBLADDER", "SPLEEN", "KIDNEY", "NERVOUS_SYSTEM",
				"SKIN", "SUPERFICIAL_LYMPH_NODES", "THYROID", "BREAST",
				"SPINE", "LIMBS_JOINTS", "EXTERNAL_GENITALIA", "DRE", "VULVA",
				"VAGINAL", "CERVIX", "UTERUS", "UTERINE_ATTACHMENT",
				"CERVICAL_SMEAR", "CERVICAL_CYTOLOGY", "RIGHT_NAKED_EYE",
				"LEFT_NAKED_EYE", "COLOR_PERCEPTION", "EYELID", "LACRIMAL",
				"CONJUNCTIVA", "EYEBALL", "CORNEA", "ANTERIOR_CHAMBER", "IRIS",
				"PUPIL", "CRYSTALLINE_LENS", "VITREOUS_BODY", "FUNDUS",
				"CUP-DISC_RATIO", "LISTENING", "EXTERNAL_EAR",
				"EXTERNAL_AUDITORY_CANAL", "EARDRUM", "NASUS_EXTERNUS",
				"NASAL_CAVITY", "PARANASAL_SINUS", "PHARYNX", "TONSIL", "LIP",
				"ORAL_MUCOSA", "DENTAL_PERIPHERY", "TEETH", "TONGUE", "PALATE",
				"PAROTID_GLAND", "TEMPOROMANDIBULAR_JOINT" };
		// �ܼ챨��ȫ������ץȡ�ڵ�
		String[] contentsSign = { "SENIOR_MANAGEMENT", "MANAGER",
				"SALESPERSON", "MEDIA_INDUSTRY", "EDUCATORS",
				"RETAIL_SERVICES", "RESEARCHER", "IT_INDUSTRY",
				"LITERATURE_ART", "DRIVER", "MANUAL_WORKERS", "RETIRED",
				"FREELANCE", "OTHER_OCCUPATIONS", "PH_HYPERTENSION",
				"PH_STROKE", "PH_CORONARY_HEART_DISEASE", "PH_MI",
				"PH_PULMONARY_HEART_DISEASE", "PH_DIABETES", "PH_FATTY_LIVER",
				"PH_GALLBLADDER_DISEASE", "PH_KIDNEY_DISEASE",
				"PH_TUBERCULOSIS", "PH_HEPATITIS", "PH_TUMOR",
				"PH_GYNECOLOGICAL_DISEASES", "PH_SURGICAL_TRAUMA",
				"PH_OTHER_DISEASE", "SURGICAL_TRAUMA", "ALLERGY_HISTORY",
				"SMOKING_HISTORY", "DRINKING_HISTORY", "FH_UNKNOWN",
				"FH_HYPERTENSION", "FH_STROKE", "FH_CORONARY_HEART_DISEASE",
				"FH_MI", "FH_DIABETES", "FH_CIRRHOSIS", "FH_TUBERCULOSIS",
				"FH_KIDNEY_DISEASE", "FH_MALIGNANT_NEOPLASMS", "FH_OTHER",
				"NATIONALITY", "ETHNIC_GROUP", "ADDRESS", "E-MAIL", "COMPANY",
				"PH_STATUS_QUO", "OTHER_DISCOMFORT", "HEIGHT", "WEIGHT", "BMI",
				"BMI_REFERENCE_RANGE", "SYSTOLIC_BP",
				"SYSTOLIC_REFERENCE_RANGE", "DIASTOLIC_BP",
				"DIASTOLIC_REFERENCE_RANGE", "HEART_RATE", "HEART_RHYTHM",
				"HEART_SOUNDS", "HEART_BORDER", "CHEST", "LUNG", "ABDOMEN",
				"LIVER", "GALLBLADDER", "SPLEEN", "KIDNEY", "NERVOUS_SYSTEM",
				"SKIN", "SUPERFICIAL_LYMPH_NODES", "THYROID", "BREAST",
				"SPINE", "LIMBS_JOINTS", "EXTERNAL_GENITALIA", "DRE", "VULVA",
				"VAGINAL", "CERVIX", "UTERUS", "UTERINE_ATTACHMENT",
				"CERVICAL_SMEAR", "CERVICAL_CYTOLOGY", "RIGHT_NAKED_EYE",
				"LEFT_NAKED_EYE", "COLOR_PERCEPTION", "EYELID", "LACRIMAL",
				"CONJUNCTIVA", "EYEBALL", "CORNEA", "ANTERIOR_CHAMBER", "IRIS",
				"PUPIL", "CRYSTALLINE_LENS", "VITREOUS_BODY", "FUNDUS",
				"CUP-DISC_RATIO", "LISTENING", "EXTERNAL_EAR",
				"EXTERNAL_AUDITORY_CANAL", "EARDRUM", "NASUS_EXTERNUS",
				"NASAL_CAVITY", "PARANASAL_SINUS", "PHARYNX", "TONSIL", "LIP",
				"ORAL_MUCOSA", "DENTAL_PERIPHERY", "TEETH", "TONGUE", "PALATE",
				"PAROTID_GLAND", "TEMPOROMANDIBULAR_JOINT" };
    	
		// ְҵ
		String profession = "";
		// ����ʷ
		String pastHistory = "";
		// ����ʷ
		String personalHistory = "";
		// ����ʷ
		String familyHistory = "";
		Timestamp date;
		String birthday = "";
		TParm sexResult = this.getDictionary("SYS_SEX");// �Ա��ֵ�
		TParm marriageResult = this.getDictionary("SYS_MARRIAGE");// �����ֵ�
		
		for (int i = 0; i < count; i++) {
			profession = "";
			pastHistory = "";
			personalHistory = "";
			familyHistory = "";
			fileParm = new TParm();
			reportData = new TParm();
			fileParm.setData("FILE_PATH", "JHW\\"
					+ reportFileInfoResult.getValue("FILE_PATH", i));
			fileParm.setData("FILE_NAME", reportFileInfoResult.getValue(
					"FILE_NAME", i));
			reportData = GetWordValue.getInstance().getWordValueByName(
					fileParm, contentsSign);

			// ְҵ
			for (int j = 0; j < professionArray.length; j++) {
				if ("Y".equals(reportData.getValue(professionArray[j]
						+ "_VALUE", 0))) {
					if (StringUtils.isNotEmpty(profession)) {
						profession = profession + ";";
					}

					profession = profession
							+ ((ECheckBoxChoose) reportData.getData(
									professionArray[j], 0)).getText();
				}
			}

			// ����ʷ
			for (int k = 0; k < pastHistoryArray.length; k++) {
				if ("Y".equals(reportData.getValue(pastHistoryArray[k]
						+ "_VALUE", 0))) {
					if (StringUtils.isNotEmpty(pastHistory)) {
						pastHistory = pastHistory + ";";
					}
					pastHistory = pastHistory
							+ ((ECheckBoxChoose) reportData.getData(
									pastHistoryArray[k], 0)).getText();
				}
			}

			// ����ʷ
			for (int m = 0; m < personalHistoryArray.length; m++) {
				if ("Y".equals(reportData.getValue(personalHistoryArray[m]
						+ "_VALUE", 0))) {
					if (StringUtils.isNotEmpty(personalHistory)) {
						personalHistory = personalHistory + ";";
					}
					personalHistory = personalHistory
							+ ((ECheckBoxChoose) reportData.getData(
									personalHistoryArray[m], 0)).getText();
				}
			}

			// ����ʷ
			for (int n = 0; n < familyHistoryArray.length; n++) {
				if ("Y".equals(reportData.getValue(familyHistoryArray[n]
						+ "_VALUE", 0))) {
					if (StringUtils.isNotEmpty(familyHistory)) {
						familyHistory = familyHistory + ";";
					}
					familyHistory = familyHistory
							+ ((ECheckBoxChoose) reportData.getData(
									familyHistoryArray[n], 0)).getText();
				}
			}
			
			result.addData("CASE_NO", reportFileInfoResult.getValue("CASE_NO", i));// �����
			result.addData("REPORT_DATE", reportFileInfoResult.getValue(
					"FINAL_JUDGE_DATE", i).substring(0, 10).replace("-", "/"));// ����������
			result.addData("PHYSICAL_EXAMINATION_DATE", reportFileInfoResult.getValue(
					"REPORT_DATE", i).substring(0, 10).replace("-", "/"));// �������(��������)
			result.addData("PAT_NAME", reportFileInfoResult.getValue("PAT_NAME", i));// ����
			result.addData("SEX", this.getDesc(sexResult, reportFileInfoResult.getValue("SEX_CODE", i)));// �Ա�
			birthday = reportFileInfoResult.getValue("BIRTHDAY", i);
			if (birthday.length() > 10) {
				birthday = birthday.substring(0, 10).replace("-", "/");
			}
			result.addData("BIRTHDATE", birthday);// ��������
			date = reportFileInfoResult.getTimestamp("REPORT_DATE", i);// ����ʱ��
			result.addData("AGE", OdiUtil.showAge(reportFileInfoResult.getTimestamp("BIRTHDAY", i), date));// ����
			result.addData("IDNO", reportFileInfoResult.getValue("ID_NO", i));// ֤����
			result.addData("MARITAL_STATUS", this.getDesc(marriageResult, reportFileInfoResult.getValue("MARRIAGE_CODE", i)));// ���
			result.addData("CONTACT_INFO", reportFileInfoResult.getValue("TEL", i));// ��ϵ��ʽ
			result.addData("PROFESSION", profession);// ְҵ
			result.addData("PAST_HISTORY", pastHistory);// ����ʷ
			result.addData("PERSONAL_HISTORY", personalHistory);// ����ʷ
			result.addData("FAMILY_HISTORY", familyHistory);// ����ʷ
			// �ı�ץȡ�ڵ�
			for (int p = 0; p < textSign.length; p++) {
				result.addData(textSign[p], reportData.getValue(textSign[p]+ "_VALUE", 0));// �ı�ץȡ
			}
		}
		
    	return result;
    }
    
    /**
     * ��ѯ�ܼ챨��ṹ�������ļ���Ϣ
     * 
     * @param parm ��ѯ����
     * @return result
     */
	public TParm queryReportFileInfo(TParm parm) {
		String sql = "SELECT A.*,C.NATION_CODE,C.SPECIES_CODE,C.COMPANY_DESC,C.E_MAIL,B.FILE_PATH,B.FILE_NAME "
				+ " FROM HRM_PATADM A, (SELECT CASE_NO,FILE_PATH,FILE_NAME,FILE_SEQ,SUBCLASS_CODE,ROW_NUMBER() "
				+ " OVER(PARTITION BY CASE_NO ORDER BY FILE_SEQ DESC) RN FROM EMR_FILE_INDEX) B, SYS_PATINFO C "
				+ " WHERE A.CASE_NO = B.CASE_NO AND A.MR_NO = C.MR_NO AND B.RN = 1 AND B.SUBCLASS_CODE = '"
				+ zjSubClassCode + "' ";

		// ��ѯ��ʼʱ��
		if (StringUtils.isNotEmpty(parm.getValue("QUERY_START_DATE"))) {
			sql = sql + " AND A.REPORT_DATE >= TO_DATE('"
					+ parm.getValue("QUERY_START_DATE") + " 000000', 'YYYY/MM/DD HH24MISS') ";
		}
		
		// ��ѯ��ֹʱ��
		if (StringUtils.isNotEmpty(parm.getValue("QUERY_END_DATE"))) {
			sql = sql + " AND A.REPORT_DATE <= TO_DATE('"
					+ parm.getValue("QUERY_END_DATE") + " 235959', 'YYYY/MM/DD HH24MISS') ";
		}

		// ֤����
		if (StringUtils.isNotEmpty(parm.getValue("IDNO"))) {
			sql = sql + " AND A.ID_NO = '" + parm.getValue("IDNO") + "' ";
		}

		// �绰
		if (StringUtils.isNotEmpty(parm.getValue("TEL"))) {
			sql = sql + " AND A.TEL = '" + parm.getValue("TEL") + "' ";
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			return result;
		}

		return result;
	}
	
    /**
     * �õ��ֵ���Ϣ
     * @param groupId ����ID
     * @return result �ֵ����ݼ���
     */
	public TParm getDictionary(String groupId) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				"SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "'"));
		if (result.getErrCode() < 0) {
			err("��ѯ�����ֵ������쳣");
		}
		return result;
	}
	
	/**
	 * �õ���������
	 * 
	 * @param parm ���ݼ���
	 * @param code ����
	 * @return ��������
	 */
	private String getDesc(TParm parm, String code) {
		for (int i = 0; i < parm.getCount(); i++) {
			if (StringUtils.equals(code, parm.getValue("ID", i))) {
				return parm.getValue("CHN_DESC", i);
			}
		}
		
		return "";
	}
	
	/**
	 * ��ȡ�õ��ܼ챨��������װΪxml��ʽ����
	 * 
	 * @param parm
	 * @return
	 */
	public String createReportDataXML(TParm parm) {
		// ������Ϣ
		String basicInfo[] = { "PAT_NAME", "SEX", "BIRTHDATE", "AGE",
				"NATIONALITY", "ETHNIC_GROUP", "IDNO", "MARITAL_STATUS",
				"COMPANY", "CONTACT_INFO", "ADDRESS", "E-MAIL", "PROFESSION",
				"PAST_HISTORY", "PH_STATUS_QUO", "PERSONAL_HISTORY",
				"FAMILY_HISTORY", "OTHER_DISCOMFORT" };
		// �����
		String physicalExamination[] = { "HEIGHT", "WEIGHT", "BMI",
				"BMI_REFERENCE_RANGE", "SYSTOLIC_BP",
				"SYSTOLIC_REFERENCE_RANGE", "DIASTOLIC_BP",
				"DIASTOLIC_REFERENCE_RANGE", "HEART_RATE", "HEART_RHYTHM",
				"HEART_SOUNDS", "HEART_BORDER", "CHEST", "LUNG", "ABDOMEN",
				"LIVER", "GALLBLADDER", "SPLEEN", "KIDNEY", "NERVOUS_SYSTEM",
				"SKIN", "SUPERFICIAL_LYMPH_NODES", "THYROID", "BREAST",
				"SPINE", "LIMBS_JOINTS", "EXTERNAL_GENITALIA", "DRE", "VULVA",
				"VAGINAL", "CERVIX", "UTERUS", "UTERINE_ATTACHMENT",
				"CERVICAL_SMEAR", "CERVICAL_CYTOLOGY", "RIGHT_NAKED_EYE",
				"LEFT_NAKED_EYE", "COLOR_PERCEPTION", "EYELID", "LACRIMAL",
				"CONJUNCTIVA", "EYEBALL", "CORNEA", "ANTERIOR_CHAMBER", "IRIS",
				"PUPIL", "CRYSTALLINE_LENS", "VITREOUS_BODY", "FUNDUS",
				"CUP-DISC_RATIO", "LISTENING", "EXTERNAL_EAR",
				"EXTERNAL_AUDITORY_CANAL", "EARDRUM", "NASUS_EXTERNUS",
				"NASAL_CAVITY", "PARANASAL_SINUS", "PHARYNX", "TONSIL", "LIP",
				"ORAL_MUCOSA", "DENTAL_PERIPHERY", "TEETH", "TONGUE", "PALATE",
				"PAROTID_GLAND", "TEMPOROMANDIBULAR_JOINT" };

		int count = parm.getCount("IDNO");
		StringBuffer xml = new StringBuffer();
		String LINE_SEPARATOR = System.getProperty("line.separator");
		xml.append("<REPORT>" + LINE_SEPARATOR);
		TParm result = new TParm();
		String limitRange = "";
		String upperLimit = "";
		String lowerLimit = "";

		for (int i = 0; i < count; i++) {
			xml.append("	<REPORT_DATA>" + LINE_SEPARATOR);
			
			// add by wangb 2017/2/22 ���������
			xml.append("		<CASE_NO>");
			xml.append(parm.getValue("CASE_NO", i));
			xml.append("</CASE_NO>" + LINE_SEPARATOR);
			// �������
			xml.append("		<PHYSICAL_EXAMINATION_DATE>");
			xml.append(parm.getValue("PHYSICAL_EXAMINATION_DATE", i).substring(0, 10)
					.replace("-", "/"));
			xml.append("</PHYSICAL_EXAMINATION_DATE>" + LINE_SEPARATOR);
			// �����������
			xml.append("		<REPORT_DATE>");
			xml.append(parm.getValue("REPORT_DATE", i).substring(0, 10)
					.replace("-", "/"));
			xml.append("</REPORT_DATE>" + LINE_SEPARATOR);
			// ������Ϣ
			xml.append("		<BASIC_INFO>" + LINE_SEPARATOR);
			for (int j = 0; j < basicInfo.length; j++) {
				xml.append("			<" + basicInfo[j] + ">");
				xml.append(xmlSpecialCharacterProcessing(parm.getValue(
						basicInfo[j], i)));
				xml.append("</" + basicInfo[j] + ">" + LINE_SEPARATOR);
			}
			xml.append("		</BASIC_INFO>" + LINE_SEPARATOR);

			// �����
			xml.append("		<PHYSICAL_EXAMINATION>" + LINE_SEPARATOR);
			for (int j = 0; j < physicalExamination.length; j++) {
				xml.append("			<" + physicalExamination[j] + ">");
				xml.append(xmlSpecialCharacterProcessing(parm.getValue(
						physicalExamination[j], i).trim()));
				xml
						.append("</" + physicalExamination[j] + ">"
								+ LINE_SEPARATOR);
			}
			xml.append("		</PHYSICAL_EXAMINATION>" + LINE_SEPARATOR);

			// ȡ�ü���������
			result = this.queryLisData(parm.getValue("CASE_NO", i));
			if (result.getErrCode() == 0) {
				for (int k = 0; k < result.getCount(); k++) {
					// ������
					xml.append("		<LIS_RESULT>" + LINE_SEPARATOR);
					xml.append("			<LIS_ORDER_CODE>"
							+ result.getValue("ORDER_CODE", k)
							+ "</LIS_ORDER_CODE>" + LINE_SEPARATOR);
					xml.append("			<LIS_ORDER_DESC>"
							+ result.getValue("ORDER_DESC", k)
							+ "</LIS_ORDER_DESC>" + LINE_SEPARATOR);
					xml.append("			<LIS_TEST_CODE>"
							+ result.getValue("TESTITEM_CODE", k)
							+ "</LIS_TEST_CODE>" + LINE_SEPARATOR);
					xml.append("			<LIS_TEST_ITEM>"
							+ result.getValue("TESTITEM_CHN_DESC", k)
							+ "</LIS_TEST_ITEM>" + LINE_SEPARATOR);
					xml.append("			<LIS_TEST_VALUE>"
							+ xmlSpecialCharacterProcessing(result.getValue(
									"TEST_VALUE", k))
							+ "</LIS_TEST_VALUE>" + LINE_SEPARATOR);
					xml.append("			<LIS_TEST_UNIT>"
							+ result.getValue("TEST_UNIT", k)
							+ "</LIS_TEST_UNIT>" + LINE_SEPARATOR);

					upperLimit = result.getValue("UPPE_LIMIT", k);
					lowerLimit = result.getValue("LOWER_LIMIT", k);
					if (StringUtils.isNotEmpty(upperLimit)
							&& StringUtils.isNotEmpty(lowerLimit)) {
						limitRange = lowerLimit + "~" + upperLimit;
					} else if (StringUtils.isNotEmpty(upperLimit)) {
						limitRange = lowerLimit;
					} else {
						limitRange = upperLimit;
					}

					xml.append("			<LIS_TEST_LIMITS>"
							+ xmlSpecialCharacterProcessing(limitRange)
							+ "</LIS_TEST_LIMITS>" + LINE_SEPARATOR);
					xml.append("		</LIS_RESULT>" + LINE_SEPARATOR);
				}
			}

			// ȡ�ü��������
			result = this.queryRisData(parm.getValue("CASE_NO", i));
			if (result.getErrCode() == 0) {
				for (int m = 0; m < result.getCount(); m++) {
					// �����
					xml.append("		<RIS_RESULT>" + LINE_SEPARATOR);
					xml.append("			<RIS_ORDER_CODE>"
							+ result.getValue("ORDER_CODE", m)
							+ "</RIS_ORDER_CODE>" + LINE_SEPARATOR);
					xml.append("			<RIS_ORDER_DESC>"
							+ result.getValue("ORDER_DESC", m)
							+ "</RIS_ORDER_DESC>" + LINE_SEPARATOR);
					xml.append("			<FINDINGS-CONCLUSION>"
							+ xmlSpecialCharacterProcessing(result.getValue(
									"OUTCOME_CONCLUSION", m))
							+ "</FINDINGS-CONCLUSION>" + LINE_SEPARATOR);
					xml.append("		</RIS_RESULT>" + LINE_SEPARATOR);
				}
			}

			xml.append("	</REPORT_DATA>" + LINE_SEPARATOR);
		}

		xml.append("</REPORT>");
		
		return xml.toString();
	}
	
	/**
	 * ��ѯ����ش��������
	 * 
	 * @param caseNo �����
	 * @return
	 */
	public TParm queryLisData(String caseNo) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT T1.ORDER_CODE,T1.ORDER_DESC,T2.* ");
		sbSql.append(" FROM (SELECT APPLICATION_NO,REPLACE (MAX (ORDER_CODE), ',', ';') AS ORDER_CODE, ");
		sbSql.append(" REPLACE (MAX (ORDER_DESC), ',', '��') AS ORDER_DESC ");
		sbSql.append(" FROM (SELECT APPLICATION_NO, ");
		sbSql.append(" WM_CONCAT (ORDER_CODE) OVER (PARTITION BY APPLICATION_NO ORDER BY ORDER_CODE) AS ORDER_CODE, ");
		sbSql.append(" WM_CONCAT (ORDER_DESC) OVER (PARTITION BY APPLICATION_NO ORDER BY ORDER_CODE) AS ORDER_DESC ");
		sbSql.append(" FROM MED_APPLY WHERE CASE_NO = '");
		sbSql.append(caseNo);
		sbSql.append("' AND CAT1_TYPE = 'LIS') GROUP BY APPLICATION_NO) T1, ");
		sbSql.append("(SELECT DISTINCT SUBSTR (A.ORDER_CODE, 0, 5) AS ORDER_TYPE, B.*");
		sbSql.append(" FROM HRM_ORDER A, MED_LIS_RPT B ");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(caseNo);
		sbSql.append("' AND A.CAT1_TYPE = 'LIS' AND A.CAT1_TYPE = B.CAT1_TYPE AND A.MED_APPLY_NO = B.APPLICATION_NO) T2 ");
		sbSql.append(" WHERE T1.APPLICATION_NO = T2.APPLICATION_NO ");
		sbSql.append(" ORDER BY T1.APPLICATION_NO,T2.RPDTL_SEQ ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		
		return result;
	}
	
	/**
	 * ��ѯ���ش��������
	 * 
	 * @param caseNo �����
	 * @return
	 */
	public TParm queryRisData(String caseNo) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT SUBSTR (A.ORDER_CODE, 0, 5) AS ORDER_TYPE,A.ORDER_CODE,A.ORDER_CAT1_CODE,A.ORDER_DESC,B.* ");
		sbSql.append(" FROM HRM_ORDER A, MED_RPTDTL B ");
		sbSql.append(" WHERE A.CASE_NO = '");
		sbSql.append(caseNo);
		sbSql.append("' AND A.SETMAIN_FLG = 'Y' AND A.CAT1_TYPE = 'RIS' AND A.CAT1_TYPE = B.CAT1_TYPE(+) ");
		sbSql.append(" AND A.MED_APPLY_NO = B.APPLICATION_NO(+) ");
		sbSql.append(" ORDER BY CASE ORDER_CAT1_CODE WHEN 'ECC' THEN '1' WHEN 'ULT' THEN '2' WHEN 'RIS' THEN '3' WHEN 'PET' THEN '4' END, ");
		sbSql.append(" CASE ORDER_TYPE WHEN 'Y0202' THEN '1' WHEN 'Y0203' THEN '2' WHEN 'Y0201' THEN '3' ELSE ORDER_CODE END,B.RPDTL_SEQ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
		}
		
		return result;
	}
	
	/**
	 * XML�����ַ�ת�崦��
	 * 
	 * @param value
	 */
	public String xmlSpecialCharacterProcessing(String value) {
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">",
				"&gt;").replace("'", "&apos;").replace("\"", "&quot;");
	}
}
