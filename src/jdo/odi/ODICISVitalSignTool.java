package jdo.odi;

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
 * Company: Bluecore
 * </p>
 * 
 * @author wangbin 2015.4.27
 * @version 1.0
 */
public class ODICISVitalSignTool extends TJDOTool {
	/**
	 * ʵ��
	 */
	public static ODICISVitalSignTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return ODICISVitalSignTool
	 */
	public static ODICISVitalSignTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new ODICISVitalSignTool();
		}
		return instanceObject;
	}

	/**
	 * ������
	 */
	public ODICISVitalSignTool() {
		setModuleName("odi\\ODICISVitalSignModule.x");
		onInit();
	}
	
	/**
	 * ��ѯסԺ����������
	 * 
	 * @return result
	 * @author wangbin
	 */
	public TParm queryODISysparm() {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT TO_DATE(ICU_SPOOL_TIME, 'YYYY/MM/DD HH24:MI') AS ICU_SPOOL_TIME,");
		sbSql.append("TO_DATE(ICU_EPOOL_TIME, 'YYYY-MM-DD HH24:MI') AS ICU_EPOOL_TIME,");
		sbSql.append("TO_DATE(CCU_SPOOL_TIME, 'YYYY/MM/DD HH24:MI') AS CCU_SPOOL_TIME,");
		sbSql.append("TO_DATE(CCU_EPOOL_TIME, 'YYYY-MM-DD HH24:MI') AS CCU_EPOOL_TIME,");
		sbSql.append("TO_DATE(WARD_SPOOL_TIME, 'YYYY/MM/DD HH24:MI') AS WARD_SPOOL_TIME,");
		sbSql.append("TO_DATE(WARD_EPOOL_TIME, 'YYYY-MM-DD HH24:MI') AS WARD_EPOOL_TIME FROM ODI_SYSPARM");
		
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
	 * @param viewName ��ѯ��ͼ��
	 * @param databaseName ���ݿ�����
	 * @return result
	 * @author wangbin
	 */
	public TParm queryODICISData(TParm parm, String viewName, String databaseName) {
		StringBuilder sbSql = new StringBuilder();
		sbSql.append("SELECT ADM_TYPE,CASE_NO,MR_NO,BED_NO,");
		sbSql.append("CONVERT(VARCHAR(24),MONITOR_TIME,21) AS MONITOR_TIME,MONITOR_ITEM_EN,MONITOR_ITEM_CH,MONITOR_VALUE,");
		sbSql.append("UNIT_DESC,NORMAL_RANGE_L,NORMAL_RANGE_H,REMARKS ");
		sbSql.append(" FROM ");
		sbSql.append(viewName);
		sbSql.append(" WHERE MONITOR_TIME BETWEEN '");
		sbSql.append(parm.getValue("START_POOLING_TIME").replaceAll("/", "-").substring(0, 16)+":00.000");
		sbSql.append("' AND '");
		// ��sql�к���.999��Ϊ��һ���ʱ��,����20150101102959.999=20150101103000.000
		sbSql.append(parm.getValue("END_POOLING_TIME").replaceAll("/", "-").substring(0, 16)+":59.998");
		sbSql.append("'");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ORDER BY MONITOR_ITEM_EN, MONITOR_TIME ASC");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(databaseName,
				sbSql.toString()));
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ������֢��������¼��ODI_CISVITALSIGN��������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm insertODICISVitalSign(TParm parm, TConnection conn) {
        TParm result = this.update("insertODICISVitalSign", parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * ����סԺ�������Ĳ�׽����ʱ������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm updateODISysparm(TParm parm, TConnection conn) {
        TParm result = this.update(parm.getValue("METHOD_NAME"), parm, conn);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * �������ݼ��ɽӿ���־
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm insertSysPatchLog(TParm parm) {
        TParm result = this.update("insertSysPatchLog", parm);
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
        return result;
	}
	
	/**
	 * ��ѯHIS���ز��������������
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * @author wangbin
	 */
	public TParm queryODICISVitalSign(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT CASE_NO,MR_NO,BED_NO FROM ODI_CISVITALSIGN WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
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
}
