package jdo.erd;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: ����CIS����������ݹ�����
 * </p>
 * 
 * <p>
 * Description: ����CIS����������ݹ�����
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
 * @author wangbin 2015.5.7
 * @version 1.0
 */
public class ERDCISVitalSignTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static ERDCISVitalSignTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return ERDCISVitalSignTool
	 */
	public static ERDCISVitalSignTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new ERDCISVitalSignTool();
		}
		return instanceObject;
	}

	/**
	 * ������
	 */
	public ERDCISVitalSignTool() {
		setModuleName("erd\\ERDCISVitalSignModule.x");
		onInit();
	}
	
	/**
	 * ��ѯ�������������
	 * 
	 * @return result
	 * @author wangbin
	 */
	public TParm queryOPDSysparm() {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT TO_DATE(START_POOLING_TIME, 'YYYY/MM/DD HH24:MI') AS START_POOLING_TIME,");
		sbSql.append("TO_DATE(END_POOLING_TIME, 'YYYY/MM/DD HH24:MI') AS END_POOLING_TIME FROM OPD_SYSPARM");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯ����CIS����
	 * 
	 * @param parm ��ѯ����
	 * @return result
	 * @author wangbin
	 */
	public TParm queryERDCISData(TParm parm) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT ADM_TYPE,CASE_NO,MR_NO,BED_NO,SUBSTRING(CONVERT(VARCHAR(24),MONITOR_TIME,21), 0, 17) AS MONITOR_TIME,");
		sbSql.append("MONITOR_ITEM_EN,MONITOR_ITEM_CH,MONITOR_VALUE,UNIT_DESC,NORMAL_RANGE_L,NORMAL_RANGE_H,REMARKS");
		sbSql.append(" FROM dbo.V_EMD_Vitalsigns ");
		sbSql.append(" WHERE MONITOR_TIME BETWEEN '");
		sbSql.append(parm.getValue("START_POOLING_TIME").replaceAll("/", "-").substring(0, 16)+":00.000");
		sbSql.append("' AND '");
		// ��sql�к���.999��Ϊ��һ���ʱ��,����20150101102959.999=20150101103000.000
		sbSql.append(parm.getValue("END_POOLING_TIME").replaceAll("/", "-").substring(0, 16)+":59.998");
		sbSql.append("' ORDER BY BED_NO,MONITOR_ITEM_EN,MONITOR_TIME DESC");
		
		TParm result = new TParm(TJDODBTool.getInstance().select("javahisEMD",
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����CIS��������¼��ERD_CISVITALSIGN��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm insertERDCISVitalSign(TParm parm, TConnection conn) {
        TParm result = this.update("insertERDCISVitalSign", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * ��������������Ĳ�׽����ʱ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm updateOPDSysparm(TParm parm, TConnection conn) {
        TParm result = this.update("updateOPDSysparm", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * ��ѯHIS���ؼ��������������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryERDCISVitalSign(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT CASE_NO,MR_NO,BED_NO FROM ERD_CISVITALSIGN WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("BED_NO"))) {
			sbSql.append(" AND BED_NO = '");
			sbSql.append(parm.getValue("BED_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("MONITOR_TIME"))) {
			sbSql.append(" AND MONITOR_TIME = '");
			sbSql.append(parm.getValue("MONITOR_TIME"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("MONITOR_ITEM_EN"))) {
			sbSql.append(" AND MONITOR_ITEM_EN = '");
			sbSql.append(parm.getValue("MONITOR_ITEM_EN"));
			sbSql.append("' ");
		}
		
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
	 * ��ѯ����ָ�������ļ��������������
	 * 
	 * @param caseNo
	 *            �����
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryERDCISVitalSign(String caseNo) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.CASE_NO,A.MR_NO,A.PAT_NAME,A.BED_NO,MONITOR_ITEM_EN,MONITOR_TIME,MONITOR_VALUE,UNIT_DESC");
		sbSql.append(" FROM ERD_RECORD A, ERD_CISVITALSIGN B ");
		sbSql.append(" WHERE A.CASE_NO = '");
		sbSql.append(caseNo);
		sbSql.append("' AND A.BED_NO = B.BED_NO ");
		sbSql.append(" AND B.MONITOR_TIME >= TO_CHAR(A.IN_DATE,'YYYYMMDDHH24MI') ");
		sbSql.append(" AND B.MONITOR_TIME <= TO_CHAR(CASE WHEN A.OUT_DATE IS NULL THEN SYSDATE ELSE A.OUT_DATE END,'YYYYMMDDHH24MI') ");
		sbSql.append(" ORDER BY MONITOR_ITEM_EN,MONITOR_TIME DESC ");
		
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
