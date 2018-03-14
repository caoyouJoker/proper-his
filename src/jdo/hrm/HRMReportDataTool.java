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
 * Title: 体检报告工具类
 * </p>
 * 
 * <p>
 * Description: 体检报告工具类
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
     * 实例
     */
    public static HRMReportDataTool instanceObject;
    /**
     * 总检报告病历模板
     */
    private String zjSubClassCode = TConfig.getSystemValue("HRM.EMR_TEMPLET.SUB_CLASS_CODE.HER");
    
    /**
     * 得到实例
     * @return HRMReportDataTool
     */
    public static HRMReportDataTool getInstance() {
        if (instanceObject == null)
            instanceObject = new HRMReportDataTool();
        return instanceObject;
    }
    
    /**
     * 获得体检报告数据
     * 
     * @param parm 查询条件
     * @return
     */
    public TParm getReportData(TParm parm) {
    	TParm result = new TParm();
    	
    	if (StringUtils.isEmpty(zjSubClassCode)) {
    		result.setErr(-1, "配置文件中获取不到总检报告结构化病历模板参数");
    		System.out.println(result.getErrText());
    		return result;
    	}
    	
    	TParm reportFileInfoResult = this.queryReportFileInfo(parm);
    	if (reportFileInfoResult.getErrCode() < 0) {
    		result.setErr(-1, "查询总检报告结构化病历文件信息异常");
    		System.out.println(result.getErrText());
    		return result;
    	}
    	
    	int count = reportFileInfoResult.getCount("CASE_NO");
    	
    	// 依次打开总检报告文件参数集合
    	TParm fileParm = new TParm();
    	// 取得的总检报告文件内容集合
    	TParm reportData = new TParm();
    	// 总检报告职业节点
		String[] professionArray = new String[] { "SENIOR_MANAGEMENT",
				"MANAGER", "SALESPERSON", "MEDIA_INDUSTRY", "EDUCATORS",
				"RETAIL_SERVICES", "RESEARCHER", "IT_INDUSTRY",
				"LITERATURE_ART", "DRIVER", "MANUAL_WORKERS", "RETIRED",
				"FREELANCE", "OTHER_OCCUPATIONS" };
    	// 总检报告既往史节点
		String[] pastHistoryArray = new String[] { "PH_HYPERTENSION",
				"PH_STROKE", "PH_CORONARY_HEART_DISEASE", "PH_MI",
				"PH_PULMONARY_HEART_DISEASE", "PH_DIABETES", "PH_FATTY_LIVER",
				"PH_GALLBLADDER_DISEASE", "PH_KIDNEY_DISEASE",
				"PH_TUBERCULOSIS", "PH_HEPATITIS", "PH_TUMOR",
				"PH_GYNECOLOGICAL_DISEASES", "PH_SURGICAL_TRAUMA",
				"PH_OTHER_DISEASE" };
    	// 总检报告个人史节点
    	String[] personalHistoryArray = new String[]{"SURGICAL_TRAUMA","ALLERGY_HISTORY","SMOKING_HISTORY","DRINKING_HISTORY"};
    	// 总检报告家族史节点
		String[] familyHistoryArray = new String[] { "FH_UNKNOWN",
				"FH_HYPERTENSION", "FH_STROKE", "FH_CORONARY_HEART_DISEASE",
				"FH_MI", "FH_DIABETES", "FH_CIRRHOSIS", "FH_TUBERCULOSIS",
				"FH_KIDNEY_DISEASE", "FH_MALIGNANT_NEOPLASMS", "FH_OTHER" };
		// 总检报告文本数据抓取节点
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
		// 总检报告全部数据抓取节点
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
    	
		// 职业
		String profession = "";
		// 既往史
		String pastHistory = "";
		// 个人史
		String personalHistory = "";
		// 家族史
		String familyHistory = "";
		Timestamp date;
		String birthday = "";
		TParm sexResult = this.getDictionary("SYS_SEX");// 性别字典
		TParm marriageResult = this.getDictionary("SYS_MARRIAGE");// 婚姻字典
		
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

			// 职业
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

			// 既往史
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

			// 个人史
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

			// 家族史
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
			
			result.addData("CASE_NO", reportFileInfoResult.getValue("CASE_NO", i));// 就诊号
			result.addData("REPORT_DATE", reportFileInfoResult.getValue(
					"FINAL_JUDGE_DATE", i).substring(0, 10).replace("-", "/"));// 出报告日期
			result.addData("PHYSICAL_EXAMINATION_DATE", reportFileInfoResult.getValue(
					"REPORT_DATE", i).substring(0, 10).replace("-", "/"));// 体检日期(报到日期)
			result.addData("PAT_NAME", reportFileInfoResult.getValue("PAT_NAME", i));// 姓名
			result.addData("SEX", this.getDesc(sexResult, reportFileInfoResult.getValue("SEX_CODE", i)));// 性别
			birthday = reportFileInfoResult.getValue("BIRTHDAY", i);
			if (birthday.length() > 10) {
				birthday = birthday.substring(0, 10).replace("-", "/");
			}
			result.addData("BIRTHDATE", birthday);// 出生年月
			date = reportFileInfoResult.getTimestamp("REPORT_DATE", i);// 报告时间
			result.addData("AGE", OdiUtil.showAge(reportFileInfoResult.getTimestamp("BIRTHDAY", i), date));// 年龄
			result.addData("IDNO", reportFileInfoResult.getValue("ID_NO", i));// 证件号
			result.addData("MARITAL_STATUS", this.getDesc(marriageResult, reportFileInfoResult.getValue("MARRIAGE_CODE", i)));// 婚否
			result.addData("CONTACT_INFO", reportFileInfoResult.getValue("TEL", i));// 联系方式
			result.addData("PROFESSION", profession);// 职业
			result.addData("PAST_HISTORY", pastHistory);// 既往史
			result.addData("PERSONAL_HISTORY", personalHistory);// 个人史
			result.addData("FAMILY_HISTORY", familyHistory);// 家族史
			// 文本抓取节点
			for (int p = 0; p < textSign.length; p++) {
				result.addData(textSign[p], reportData.getValue(textSign[p]+ "_VALUE", 0));// 文本抓取
			}
		}
		
    	return result;
    }
    
    /**
     * 查询总检报告结构化病历文件信息
     * 
     * @param parm 查询条件
     * @return result
     */
	public TParm queryReportFileInfo(TParm parm) {
		String sql = "SELECT A.*,C.NATION_CODE,C.SPECIES_CODE,C.COMPANY_DESC,C.E_MAIL,B.FILE_PATH,B.FILE_NAME "
				+ " FROM HRM_PATADM A, (SELECT CASE_NO,FILE_PATH,FILE_NAME,FILE_SEQ,SUBCLASS_CODE,ROW_NUMBER() "
				+ " OVER(PARTITION BY CASE_NO ORDER BY FILE_SEQ DESC) RN FROM EMR_FILE_INDEX) B, SYS_PATINFO C "
				+ " WHERE A.CASE_NO = B.CASE_NO AND A.MR_NO = C.MR_NO AND B.RN = 1 AND B.SUBCLASS_CODE = '"
				+ zjSubClassCode + "' ";

		// 查询开始时间
		if (StringUtils.isNotEmpty(parm.getValue("QUERY_START_DATE"))) {
			sql = sql + " AND A.REPORT_DATE >= TO_DATE('"
					+ parm.getValue("QUERY_START_DATE") + " 000000', 'YYYY/MM/DD HH24MISS') ";
		}
		
		// 查询截止时间
		if (StringUtils.isNotEmpty(parm.getValue("QUERY_END_DATE"))) {
			sql = sql + " AND A.REPORT_DATE <= TO_DATE('"
					+ parm.getValue("QUERY_END_DATE") + " 235959', 'YYYY/MM/DD HH24MISS') ";
		}

		// 证件号
		if (StringUtils.isNotEmpty(parm.getValue("IDNO"))) {
			sql = sql + " AND A.ID_NO = '" + parm.getValue("IDNO") + "' ";
		}

		// 电话
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
     * 拿到字典信息
     * @param groupId 分组ID
     * @return result 字典数据集合
     */
	public TParm getDictionary(String groupId) {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				"SELECT ID,CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID='"
						+ groupId + "'"));
		if (result.getErrCode() < 0) {
			err("查询基础字典数据异常");
		}
		return result;
	}
	
	/**
	 * 得到中文名称
	 * 
	 * @param parm 数据集合
	 * @param code 编码
	 * @return 中文名称
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
	 * 将取得的总检报告内容组装为xml格式数据
	 * 
	 * @param parm
	 * @return
	 */
	public String createReportDataXML(TParm parm) {
		// 基本信息
		String basicInfo[] = { "PAT_NAME", "SEX", "BIRTHDATE", "AGE",
				"NATIONALITY", "ETHNIC_GROUP", "IDNO", "MARITAL_STATUS",
				"COMPANY", "CONTACT_INFO", "ADDRESS", "E-MAIL", "PROFESSION",
				"PAST_HISTORY", "PH_STATUS_QUO", "PERSONAL_HISTORY",
				"FAMILY_HISTORY", "OTHER_DISCOMFORT" };
		// 体格检查
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
			
			// add by wangb 2017/2/22 新增就诊号
			xml.append("		<CASE_NO>");
			xml.append(parm.getValue("CASE_NO", i));
			xml.append("</CASE_NO>" + LINE_SEPARATOR);
			// 体检日期
			xml.append("		<PHYSICAL_EXAMINATION_DATE>");
			xml.append(parm.getValue("PHYSICAL_EXAMINATION_DATE", i).substring(0, 10)
					.replace("-", "/"));
			xml.append("</PHYSICAL_EXAMINATION_DATE>" + LINE_SEPARATOR);
			// 报告更新日期
			xml.append("		<REPORT_DATE>");
			xml.append(parm.getValue("REPORT_DATE", i).substring(0, 10)
					.replace("-", "/"));
			xml.append("</REPORT_DATE>" + LINE_SEPARATOR);
			// 基本信息
			xml.append("		<BASIC_INFO>" + LINE_SEPARATOR);
			for (int j = 0; j < basicInfo.length; j++) {
				xml.append("			<" + basicInfo[j] + ">");
				xml.append(xmlSpecialCharacterProcessing(parm.getValue(
						basicInfo[j], i)));
				xml.append("</" + basicInfo[j] + ">" + LINE_SEPARATOR);
			}
			xml.append("		</BASIC_INFO>" + LINE_SEPARATOR);

			// 体格检查
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

			// 取得检验结果数据
			result = this.queryLisData(parm.getValue("CASE_NO", i));
			if (result.getErrCode() == 0) {
				for (int k = 0; k < result.getCount(); k++) {
					// 检验结果
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

			// 取得检查结果数据
			result = this.queryRisData(parm.getValue("CASE_NO", i));
			if (result.getErrCode() == 0) {
				for (int m = 0; m < result.getCount(); m++) {
					// 检查结果
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
	 * 查询检验回传结果数据
	 * 
	 * @param caseNo 就诊号
	 * @return
	 */
	public TParm queryLisData(String caseNo) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT T1.ORDER_CODE,T1.ORDER_DESC,T2.* ");
		sbSql.append(" FROM (SELECT APPLICATION_NO,REPLACE (MAX (ORDER_CODE), ',', ';') AS ORDER_CODE, ");
		sbSql.append(" REPLACE (MAX (ORDER_DESC), ',', '、') AS ORDER_DESC ");
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
	 * 查询检查回传结果数据
	 * 
	 * @param caseNo 就诊号
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
	 * XML特殊字符转义处理
	 * 
	 * @param value
	 */
	public String xmlSpecialCharacterProcessing(String value) {
		return value.replace("&", "&amp;").replace("<", "&lt;").replace(">",
				"&gt;").replace("'", "&apos;").replace("\"", "&quot;");
	}
}
