package jdo.emr;

import jdo.reg.PatAdmTool;
import jdo.sys.PatTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: EMRCDR������
 * </p>
 * 
 * <p>
 * Description:EMRCDR������
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
	 * ʵ��
	 */
	public static EMRCDRSummaryInfoTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return EMRCDRTool
	 */
	public static EMRCDRSummaryInfoTool getInstance() {
		if (instanceObject == null)
			instanceObject = new EMRCDRSummaryInfoTool();
		return instanceObject;
	}
	
	/**
	 * ��ѯ����������Ϣ
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryPatInfo(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT PAT_NAME,CASE SEX_TYPE WHEN 'F' THEN 'Ů' WHEN 'M' THEN '��' ELSE '����' END AS SEX_TYPE,BIRTH_DATE");
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
	 * ��ѯ�����¼
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryVisitRecord(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append(" SELECT * FROM (SELECT CASE_NO,MR_NO,ADM_TYPE,ADM_DATE,CASE ADM_TYPE WHEN 'O' THEN '����'");
		sbSql.append(" WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append(" WHEN 'I' THEN (CASE WHEN DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append(" ADM_DATE  AS ADM_DATE_DESC,DISCHARGE_DATE,");
		sbSql.append(" DEPT_CODE,DEPT_DESC,CLINICAREA_CODE AS AREA_CODE,CLINICAREA_DESC AS AREA_DESC,VS_DR_CODE,VS_DR_NAME,ATTEND_DR_CODE, 'N' AS NIS_REPORT ");
		sbSql.append(" FROM CRP_VISIT_RECORD WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			//modify by huangtt 20161014 start �ಡ���Ų�ѯ			
			sbSql.append(" AND MR_NO IN (");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
			
//			sbSql.append(" AND MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161014 end �ಡ���Ų�ѯ
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
		
		sbSql.append(" SELECT CASE_NO,MR_NO,ADM_TYPE,ADM_DATE,CASE ADM_TYPE WHEN 'O' THEN '����'");
		sbSql.append(" WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append(" WHEN 'I' THEN (CASE WHEN DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append(" ADM_DATE  AS ADM_DATE_DESC,DISCHARGE_DATE,");
		sbSql.append("DEPT_CODE,DEPT_DESC,STATION_CODE AS AREA_CODE,STATION_DESC AS AREA_DESC,VS_DR_CODE,VS_DR_NAME,ATTEND_DR_CODE, 'N' AS NIS_REPORT ");
		sbSql.append(" FROM CRP_VISIT_RECORD WHERE 1 = 1  ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
			//modify by huangtt 20161014 start �ಡ���Ų�ѯ			
			sbSql.append(" AND MR_NO IN (");
			sbSql.append(parm.getValue("MR_NO"));
			sbSql.append(") ");
			
//			sbSql.append(" AND MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161014 end �ಡ���Ų�ѯ
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
		
		//System.out.println("��ѯ�����¼sql:"+sbSql.toString());
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
	 * ��ѯ�ֲ�ʷ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryPresentIllness(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.VISIT_DATE,A.ADM_TYPE,A.MR_NO,");
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.SUBJECTIVE,A.OBJECTIVE_DATA,A.OBJECTIVE ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_PRESENT_ILLNESS A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ�ֲ�ʷ����sql:"+sbSql.toString());
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
	 * ��ѯ����ʷ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryMedicalHistory(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.VISIT_DATE,A.ADM_TYPE,A.MR_NO,");
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.PAST_HISTORY,A.FAMILY_HISTORY ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_MEDICAL_HISTORY A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ����ʷ����sql:"+sbSql.toString());
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		
		return result;
	}
	
	/**
	 * ��ѯ����ʷ����
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryAllergyHistory(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.VISIT_DATE,A.ADM_TYPE,A.MR_NO,");
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.ALLERGEN_NAME,A.SYMPTOM,A.SERIOUS_LEVEL ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_ALLERGY_HISTORY A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ����ʷ����sql:"+sbSql.toString());
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
	 * ��ѯ�������
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
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("A.DIAG_REMARKS,A.DIAG_DEFINITION,A.DEPT_NAME,A.DR_NAME,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_DIAGNOSIS A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ�������sql:"+sbSql.toString());
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
	 * ��ѯҩ������
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
		sbSql.append("CASE A.RX_KIND WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'UD' THEN 'סԺ����' WHEN 'ST' THEN 'סԺ��ʱ' WHEN 'DS' THEN '��Ժ��ҩ' ELSE '' END  RX_TYPE,");
		sbSql.append("A.DOSE_DESC,A.DEPT_NAME,A.DR_NAME,A.DR_NOTE,A.STATUS,A.STATE_TIME,");
		sbSql.append("CASE A.ANTIBIOTIC_WAY WHEN '01' THEN 'Ԥ����' WHEN '02' THEN '������' ELSE NULL END AS ANTIBIOTIC_WAY_DESC,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("CASE A.ANTIBIOTIC_LEVEL WHEN '01' THEN '��������' WHEN '02' THEN '������' WHEN '03' THEN '������' ELSE NULL END AS ANTIBIOTIC_LEVEL_DESC ");
		sbSql.append(" FROM CRP_VISIT_RECORD B, CRP_MEDICATION A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯҩ������sql:"+sbSql.toString());
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
	 * ��ѯ��������
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
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("CASE A.STATUS WHEN 0 THEN 'ҽ������' WHEN 1 THEN 'ҽ������' WHEN 2 THEN 'ԤԼ' WHEN 3 THEN 'ȡ��ԤԼ' WHEN 4 THEN '����' WHEN 5 THEN 'ȡ������' ");
		sbSql.append("WHEN 6 THEN '������' WHEN 7 THEN '�������' WHEN 8 THEN '����������' WHEN 9 THEN 'ҽ������' WHEN 10 THEN 'ȡ������' END STATUS1,'N' LIS_WORD,");
		sbSql.append("A.LAB_TYPE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_LABORDER A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ��������sql:"+sbSql.toString());
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
	 * ��ѯ�������
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
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("CASE A.OUTCOME_TYPE WHEN 'H' THEN '����' WHEN 'T' THEN '����' END OUTCOME_TYPE1,");
		sbSql.append("CASE A.STATUS WHEN 0 THEN 'ҽ������' WHEN 1 THEN 'ҽ������' WHEN 2 THEN 'ԤԼ' WHEN 3 THEN 'ȡ��ԤԼ' WHEN 4 THEN '����' WHEN 5 THEN 'ȡ������' ");
		sbSql.append("WHEN 6 THEN '������' WHEN 7 THEN '�������' WHEN 8 THEN '����������' WHEN 9 THEN 'ҽ������' WHEN 10 THEN 'ȡ������' END STATUS1,'N' SEEIMAGE,  ");
		
		
		sbSql.append("A.STATUS,A.STATE_TIME,A.DEPT_NAME,A.DR_NAME,A.DR_NOTE");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_PHYSICAL_EXAM A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ�������sql:"+sbSql.toString());
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
	 * ��ѯ��������
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
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		sbSql.append("CASE A.STATUS WHEN '0' THEN '������' WHEN '1' THEN '���ų�' WHEN '2' THEN '�ӻ���' WHEN '3' THEN '�����ҽ���' ELSE A.STATUS END STATUS1,");
		sbSql.append("'N' ANA_MED,'N' OPEING,'N' WORD,");
		sbSql.append("A.DEPT_NAME,A.DR_NAME,A.STATUS,A.STATE_TIME,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_OPERATION A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ��������sql:"+sbSql.toString());
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
	 * ��ѯ��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryTreatment(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.TR_CODE,A.START_DATE,A.ADM_TYPE,A.MR_NO,A.END_DATE,A.TR_DESC,A.FREQUENCY,A.QTY,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append(" Case A.TR_DAYS when null then  '' else  a.tr_days||''  end tr_days  ,A.IS_URGENT,A.DEPT_NAME,A.DR_NAME,A.DR_NOTE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_TREATMENT A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ��������sql:"+sbSql.toString());
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
	 * ��ѯ��Ѫ����
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
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("A.IS_ATR,STATUS,A.STATE_TIME,A.DEPT_NAME,A.DR_NAME,A.OPT_USER,A.OPT_DATE,A.OPT_TERM ");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_TRANSFUSION A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ��Ѫ����sql:"+sbSql.toString());
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
	 * ��ѯ��������
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
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("A.CONSULT_DEPT_NAME,A.REPLY_DATE,A.CONSULT_REPORT,A.OPT_USER,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_CONSULT A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ��������sql:"+sbSql.toString());
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
	 * ��ѯ������������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryMedicalRecordDocuments(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT A.CASE_NO,A.FILE_SEQ,A.MR_NO,A.CHART_TYPE,A.CLASS_CODE,A.SUBCLASS_CODE,A.CHART_NAME,A.FILE_PATH,");
		
		sbSql.append("CASE B.ADM_TYPE WHEN 'O' THEN '����' WHEN 'E' THEN '����' WHEN 'H' THEN '����'");
		sbSql.append("WHEN 'I' THEN (CASE WHEN B.DISCHARGE_DATE IS NULL THEN '��Ժ' ELSE 'סԺ' END) ELSE NULL END AS ADM_TYPE_DESC,");
		sbSql.append("B.ADM_DATE,B.DISCHARGE_DATE,B.DEPT_DESC,");
		
		sbSql.append("A.FILE_NAME,A.CONFIRM_TIME,A.OPE_BOOK_NO,A.EDIT_USER,A.EDIT_DATE,A.STATUS,A.STATE_TIME,A.OPT_DATE,A.OPT_TERM");
		sbSql.append(" FROM CRP_VISIT_RECORD B,CRP_FILE_INDEX A WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("MR_NO"))) {
//			sbSql.append(" AND A.MR_NO = '");
//			sbSql.append(parm.getValue("MR_NO"));
//			sbSql.append("' ");
			//modify by huangtt 20161018 �ಡ���ŵ����
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
		//System.out.println("��ѯ������������sql:"+sbSql.toString());
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
