package jdo.emr;

import jdo.reg.PatAdmTool;
import jdo.sys.PatTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: EMRCDR工具类
 * </p>
 * 
 * <p>
 * Description:EMRCDR工具类
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author wangbin 2015.5.13
 * @version 1.0
 */
public class EMRCDRSummaryInfoTool extends TJDOTool {
	
	/**
	 * 实例
	 */
	public static EMRCDRSummaryInfoTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return EMRCDRTool
	 */
	public static EMRCDRSummaryInfoTool getInstance() {
		if (instanceObject == null)
			instanceObject = new EMRCDRSummaryInfoTool();
		return instanceObject;
	}
	
	/**
	 * 查询病患基本信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryPatInfo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT PAT_NAME,CASE SEX_TYPE WHEN 'F' THEN '女' WHEN 'M' THEN '男' ELSE '不详' END AS SEX_TYPE,BIRTH_DATE");
		sbSql.append(" FROM CRP_PATIENT WHERE MR_NO = '");
		sbSql.append(parm.getValue("MR_NO"));
		sbSql.append("' ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}

	/**
	 * 查询就诊记录
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryVisitRecord(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT * FROM (SELECT CASE_NO,MR_NO,ADM_TYPE,ADM_DATE,CASE ADM_TYPE WHEN 'O' THEN '门诊'");
		sbSql.append(" WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append(" WHEN 'I' THEN (CASE WHEN DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append(" ADM_DATE  AS ADM_DATE_DESC,DISCHARGE_DATE,");
		sbSql.append(" DEPT_CODE,DEPT_DESC,CLINICAREA_CODE AS AREA_CODE,CLINICAREA_DESC AS AREA_DESC,VS_DR_CODE,VS_DR_NAME,ATTEND_DR_CODE, 'N' AS NIS_REPORT ");
		sbSql.append(" FROM CRP_VISIT_RECORD WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			//modify by huangtt 20161014 start 多病案号查询			
			sbSql.append(" AND MR_NO IN (");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
			
//			sbSql.append(" AND MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161014 end 多病案号查询
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND ADM_TYPE <> 'I' ");
		sbSql.append(" UNION ALL ");
		
		sbSql.append(" SELECT CASE_NO,MR_NO,ADM_TYPE,ADM_DATE,CASE ADM_TYPE WHEN 'O' THEN '门诊'");
		sbSql.append(" WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append(" WHEN 'I' THEN (CASE WHEN DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append(" ADM_DATE  AS ADM_DATE_DESC,DISCHARGE_DATE,");
		sbSql.append("DEPT_CODE,DEPT_DESC,STATION_CODE AS AREA_CODE,STATION_DESC AS AREA_DESC,VS_DR_CODE,VS_DR_NAME,ATTEND_DR_CODE, 'N' AS NIS_REPORT ");
		sbSql.append(" FROM CRP_VISIT_RECORD WHERE 1 = 1  ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			//modify by huangtt 20161014 start 多病案号查询			
			sbSql.append(" AND MR_NO IN (");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
			
//			sbSql.append(" AND MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161014 end 多病案号查询
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND ADM_TYPE = 'I' ");
		sbSql.append(") T ORDER BY T.ADM_DATE DESC,CASE T.ADM_TYPE WHEN 'I' THEN '1' WHEN 'E' THEN '2' WHEN 'O' THEN '3' ELSE '4' END");
		
		//System.out.println("查询就诊记录sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询现病史数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryPresentIllness(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.VISIT_DATE,A.ADM_TYPE,A.MR_NO,");
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.SUBJECTIVE,A.OBJECTIVE_DATA,A.OBJECTIVE ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_PRESENT_ILLNESS A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
			
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.VISIT_DATE DESC");
		//System.out.println("查询现病史数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询既往史数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryMedicalHistory(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.VISIT_DATE,A.ADM_TYPE,A.MR_NO,");
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.PAST_HISTORY,A.FAMILY_HISTORY ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_MEDICAL_HISTORY A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		sbSql.append(" ORDER BY A.VISIT_DATE DESC");
		//System.out.println("查询既往史数据sql:"+sbSql.toString());
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询过敏史数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryAllergyHistory(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.VISIT_DATE,A.ADM_TYPE,A.MR_NO,");
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.ALLERGEN_NAME,A.SYMPTOM,A.SERIOUS_LEVEL ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_ALLERGY_HISTORY A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY VISIT_DATE DESC");
		//System.out.println("查询过敏史数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询诊断数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryDiagnosis(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.DIAG_CODE,A.DIAG_TIME,A.ADM_TYPE,A.MR_NO,A.ADM_DATE,");
		sbSql.append("A.ICD_TYPE,A.DIAG_TYPE,A.CODING_TYPE,A.IS_MAIN_DIAG,A.IS_LAW_PESTILENCE,A.DIAG_DESC,");
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.DIAG_REMARKS,A.DIAG_DEFINITION,A.DEPT_NAME,A.DR_NAME,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_DIAGNOSIS A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.DIAG_TIME DESC");
		//System.out.println("查询诊断数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询药嘱数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryMedication(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.DRUG_CODE,A.START_DATE,A.ORDER_SEQ,A.ADM_TYPE,A.MR_NO,");
		sbSql.append("A.END_DATE,A.DRUG_DESC || A.SPECIFICATION AS DRUG_DESC,A.GOODS_DESC,A.SPECIFICATION,TO_CHAR(A.MEDI_QTY,'fm999999990.099999999')||' '||A.MEDI_UNIT MEDI_QTY,A.MEDI_UNIT,");
		sbSql.append("A.FREQ_CODE,A.FREQUENCY,A.ROUTE_CODE,A.ROUTE,A.TAKE_DAYS,TO_CHAR(A.DOSAGE_QTY,'fm999999990.099999999')||' '||A.DOSAGE_UNIT DOSAGE_QTY,");
		sbSql.append("A.DOSAGE_UNIT,A.DRIP_RATE,A.IVA_LINK_NO,A.IS_IVA_MAIN,A.SKINTEST_RESULT,A.CATE1_DESC,");
		sbSql.append("A.CATE2_DESC,A.CTRLCLASS_CODE,A.CTRLCLASS_DESC,A.ANTIBIOTIC_LEVEL,A.ANTIBIOTIC_WAY,A.RX_KIND ,");
		sbSql.append("CASE A.RX_KIND WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'UD' THEN '住院长期' WHEN 'ST' THEN '住院临时' WHEN 'DS' THEN '出院带药' ELSE '' END  RX_TYPE,");
		sbSql.append("A.DOSE_DESC,A.DEPT_NAME,A.DR_NAME,A.DR_NOTE,A.STATUS,A.STATE_TIME,");
		sbSql.append("CASE A.ANTIBIOTIC_WAY WHEN '01' THEN '预防性' WHEN '02' THEN '治疗性' ELSE NULL END AS ANTIBIOTIC_WAY_DESC,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("CASE A.ANTIBIOTIC_LEVEL WHEN '01' THEN '非限制类' WHEN '02' THEN '限制类' WHEN '03' THEN '特殊类' ELSE NULL END AS ANTIBIOTIC_LEVEL_DESC ");
		sbSql.append(" FROM CRP_VISIT_RECORD B, CRP_MEDICATION A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("FILTER_DATA"))) {
			sbSql.append(" AND A.DRUG_CODE like '");
			sbSql.append(parm.getValue("FILTER_DATA"));
			sbSql.append("%' ");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.START_DATE DESC,A.ORDER_SEQ,A.IVA_LINK_NO");
		//System.out.println("查询药嘱数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询检验数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryLaboratory(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CAT1_TYPE,A.APPLY_NO,A.ADM_TYPE,A.MR_NO,A.CASE_NO,A.TEST_NAME,");
		sbSql.append("A.TEST_CODE,A.CATE_CODE,A.CATE_DESC,A.ORDER_TIME,A.RECEIVE_TIME,A.REPORT_TIME,");
		sbSql.append("A.IS_URGENT,A.SPECIMEN,A.STATUS,A.STATE_TIME,A.DEPT_NAME,A.DR_NAME,A.DR_NOTE,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("CASE A.STATUS WHEN 0 THEN '医嘱开立' WHEN 1 THEN '医嘱接收' WHEN 2 THEN '预约' WHEN 3 THEN '取消预约' WHEN 4 THEN '到检' WHEN 5 THEN '取消到检' ");
		sbSql.append("WHEN 6 THEN '检查完成' WHEN 7 THEN '报告完成' WHEN 8 THEN '报告审核完成' WHEN 9 THEN '医嘱接收' WHEN 10 THEN '取消报告' END STATUS1,'N' LIS_WORD,");
		sbSql.append("A.LAB_TYPE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_LABORDER A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("FILTER_DATA"))) {
			sbSql.append(" AND A.TEST_CODE like '");
			sbSql.append(parm.getValue("FILTER_DATA"));
			sbSql.append("%' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.ORDER_TIME DESC,A.CATE_CODE");
		//System.out.println("查询检验数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询检查数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryPhysicalExam(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.APPLY_NO,A.EXAM_CODE,A.ADM_TYPE,A.MR_NO,A.ORDER_TIME,");
		sbSql.append("A.CHECK_IN_TIME,A.CONFIRM_TIME,A.EXAM_ITEM_DESC,A.EXAM_SITE,A.IS_URGENT,A.OUTCOME_DESCRIBE,");
		sbSql.append("A.OUTCOME_CONCLUSION,A.OUTCOME_TYPE,A.IS_PACS,A.CATE1_DESC,A.CATE2_DESC,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("CASE A.OUTCOME_TYPE WHEN 'H' THEN '阴性' WHEN 'T' THEN '阳性' END OUTCOME_TYPE1,");
		sbSql.append("CASE A.STATUS WHEN 0 THEN '医嘱开立' WHEN 1 THEN '医嘱接收' WHEN 2 THEN '预约' WHEN 3 THEN '取消预约' WHEN 4 THEN '到检' WHEN 5 THEN '取消到检' ");
		sbSql.append("WHEN 6 THEN '检查完成' WHEN 7 THEN '报告完成' WHEN 8 THEN '报告审核完成' WHEN 9 THEN '医嘱接收' WHEN 10 THEN '取消报告' END STATUS1,'N' SEEIMAGE,  ");
		
		
		sbSql.append("A.STATUS,A.STATE_TIME,A.DEPT_NAME,A.DR_NAME,A.DR_NOTE");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_PHYSICAL_EXAM A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("FILTER_DATA"))) {
			sbSql.append(" AND A.EXAM_CODE like '");
			sbSql.append(parm.getValue("FILTER_DATA"));
			sbSql.append("%' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.ORDER_TIME DESC,A.EXAM_CODE");
		//System.out.println("查询检查数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询手术数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryOperation(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.APPLY_NO,A.ADM_TYPE,A.MR_NO,A.STATE,A.BOOK_DATE,");
		sbSql.append("A.OP_DATE,A.IS_URGENT,A.IS_ISOLATE,A.OP_START_DATE,A.OP_END_DATE,A.OP_CODE1,");
		sbSql.append("A.OP_DESC1,A.OP_CODE2,A.OP_DESC2,A.OP_TYPE,A.OP_LEVEL,A.OP_RISK,");
		sbSql.append("A.OP_WAY,A.OP_SITE,A.INCISION_TYPE,A.ANA_WAY,A.ASA_LEVEL,A.IS_BIOPSY,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("CASE A.STATUS WHEN '0' THEN '已申请' WHEN '1' THEN '已排程' WHEN '2' THEN '接患者' WHEN '3' THEN '手术室交接' ELSE A.STATUS END STATUS1,");
		sbSql.append("'N' ANA_MED,'N' OPEING,'N' WORD,");
		sbSql.append("A.DEPT_NAME,A.DR_NAME,A.STATUS,A.STATE_TIME,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_OPERATION A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.BOOK_DATE DESC,A.OP_CODE1");
		//System.out.println("查询手术数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询治疗数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryTreatment(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.TR_CODE,A.START_DATE,A.ADM_TYPE,A.MR_NO,A.END_DATE,A.TR_DESC,A.FREQUENCY,A.QTY,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append(" Case A.TR_DAYS when null then  '' else  a.tr_days||''  end tr_days  ,A.IS_URGENT,A.DEPT_NAME,A.DR_NAME,A.DR_NOTE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_TREATMENT A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.START_DATE DESC,A.TR_CODE");
		//System.out.println("查询治疗数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询用血数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryBloodTransfusions(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.APPLY_NO,A.BLDPROD_CODE,A.ADM_TYPE,A.MR_NO,A.ORDER_TIME,");
		sbSql.append("A.TRANSFUSION_TIME,A.TRANS_REASON,A.BLDPROD_DESC,A.VOLUME,A.UNIT_DESC,A.BLOOD_SOURCE,");
		sbSql.append("A.IS_URGENT,A.BLOOD_TYPING,A.RH_TYPING,A.SUB_CROSS_TEST,A.MAIN_CROSS_TEST,A.CROSS_MATCH,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("A.IS_ATR,STATUS,A.STATE_TIME,A.DEPT_NAME,A.DR_NAME,A.OPT_USER,A.OPT_DATE,A.OPT_TERM ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_TRANSFUSION A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.ORDER_TIME DESC");
		//System.out.println("查询用血数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询会诊数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryConsultation(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.CONSULT_NO,A.CONSULT_DEPT_CODE,A.ADM_TYPE,A.MR_NO,A.ORDER_TIME,");
		sbSql.append("A.ORDER_DEPT,A.CONSULT_KIND,A.CONSULT_REASON,A.ILLNESS_STATE,A.IS_CRITICAL,A.ACCEPT_TIME,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("A.CONSULT_DEPT_NAME,A.REPLY_DATE,A.CONSULT_REPORT,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_CONSULT A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY A.ORDER_TIME DESC");
		//System.out.println("查询会诊数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * 查询病历文书数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryMedicalRecordDocuments(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.CHART_TYPE,A.CLASS_CODE,A.SUBCLASS_CODE,A.CHART_NAME,A.FILE_PATH,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '门诊' WHEN 'E' THEN '急诊' WHEN 'H' THEN '健检'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '在院' ELSE '住院' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("A.FILE_NAME,A.CONFIRM_TIME,A.OPE_BOOK_NO,A.EDIT_USER,A.EDIT_DATE,A.STATUS,A.STATE_TIME,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_FILE_INDEX A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 多病案号的情况
			sbSql.append(" AND A.MR_NO IN ( ");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("S_DATE"))
				&& StringUtils.isNotEmpty(parm.getValue("E_DATE"))) {
			sbSql.append(" AND B.ADM_DATE BETWEEN TO_DATE('");
			sbSql.append(parm.getValue("S_DATE") + " 00:00:00");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS') and TO_DATE('");
			sbSql.append(parm.getValue("E_DATE") + " 23:59:59");
			sbSql.append("', 'YYYY/MM/DD HH24:MI:SS')");
		}
		sbSql.append(" AND A.CASE_NO = B.CASE_NO ");
		sbSql.append(" ORDER BY B.ADM_DATE DESC,A.CLASS_CODE,A.SUBCLASS_CODE ");
		//System.out.println("查询病历文书数据sql:"+sbSql.toString());
		TParm result = new TParm(TJDODBTool.getInstance().select(
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
}
