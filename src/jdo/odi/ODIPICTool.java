package jdo.odi;

import org.apache.commons.lang.StringUtils;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: һ���ٴ�������
 * </p>
 * 
 * <p>
 * Description: һ���ٴ�������
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2016.6.14
 * @version 1.0
 */
public class ODIPICTool extends TJDOTool {
	
	private static ODIPICTool instanceObject;

	public static ODIPICTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new ODIPICTool();
		}
		return instanceObject;
	}
	
	/**
	 * ��ѯһ���ٴ������ײ�
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryPICPackMain(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT DISTINCT A.* FROM ODI_PACK_MAIN A,ODI_PACK_ORDER B ");
		sbSql.append(" WHERE A.PACK_CODE = B.PACK_CODE AND A.DEPT_OR_DR = '1' ");
		sbSql.append(" AND A.RX_KIND = 'ST' AND PHASE_I_CLINICAL_FLG = 'Y' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("DEPTORDR_CODE"))) {
			sbSql.append(" AND A.DEPTORDR_CODE = '");
			sbSql.append(parm.getValue("DEPTORDR_CODE"));
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY A.OPT_DATE DESC ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * ��ѯ����һ���ٴ�ע�ǵ�ҽ��
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryPICOrder(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT A.* FROM ODI_ORDER A,ADM_INP B WHERE A.CASE_NO = B.CASE_NO");
		sbSql.append(" AND B.DS_DATE IS NULL AND A.DC_DATE IS NULL AND A.RX_KIND = 'ST'  ");
		sbSql.append(" AND A.NS_CHECK_DATE IS NULL AND A.PHASE_I_CLINICAL_FLG = 'Y' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND A.CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("CAT1_TYPE"))) {
			sbSql.append(" AND A.CAT1_TYPE= '");
			sbSql.append(parm.getValue("CAT1_TYPE"));
			sbSql.append("' ");
		}
		
		sbSql.append(" ORDER BY A.ORDER_NO,A.ORDER_SEQ ");
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}

	/**
	 * ��ѯһ���ٴ������ײ���ϸ
	 * 
	 * @param packCode �ײʹ���
	 * @return result �ײ�ҽ����ϸ�����
	 */
	public TParm queryPICPackOrder(String packCode) {
		String sql = "SELECT * FROM ODI_PACK_ORDER WHERE PACK_CODE = '"
				+ packCode + "' AND PHASE_I_CLINICAL_FLG = 'Y' AND CAT1_TYPE = 'LIS' ORDER BY SEQ_NO";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����ҽ���������ʱ��
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm updateOrderEffDate(TParm parm, TConnection conn) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("UPDATE ODI_ORDER SET EFF_DATE = TO_DATE('");
		sbSql.append(parm.getValue("EFF_DATE"));
		sbSql.append("','YYYY/MM/DD HH24:MI:SS') WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ_L"))) {
			sbSql.append(" AND ORDER_SEQ >= ");
			sbSql.append(parm.getInt("ORDER_SEQ_L"));
		}
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ_H"))) {
			sbSql.append(" AND ORDER_SEQ < ");
			sbSql.append(parm.getInt("ORDER_SEQ_H"));
		}
		if (StringUtils.isEmpty(parm.getValue("ORDER_SEQ_L"))
				&& StringUtils.isEmpty(parm.getValue("ORDER_SEQ_H"))
				&& StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sbSql.append(" AND ORDER_SEQ = ");
			sbSql.append(parm.getInt("ORDER_SEQ"));
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString(), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ���»�ʿִ�������Ӧִ��ʱ��
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm updateOdiDspnMDate(TParm parm, TConnection conn) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("UPDATE ODI_DSPNM SET START_DTTM = TO_CHAR(TO_DATE('");
		sbSql.append(parm.getValue("EFF_DATE"));
		sbSql.append("','YYYY/MM/DD HH24:MI:SS'),'YYYYMMDDHH24MI'),END_DTTM = TO_CHAR(TO_DATE('");
		sbSql.append(parm.getValue("EFF_DATE"));
		sbSql.append("','YYYY/MM/DD HH24:MI:SS'),'YYYYMMDDHH24MI'),ORDER_DATE = TO_DATE('");
		sbSql.append(parm.getValue("EFF_DATE"));
		sbSql.append("','YYYY/MM/DD HH24:MI:SS') WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' ");
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ_L"))) {
			sbSql.append(" AND ORDER_SEQ >= ");
			sbSql.append(parm.getInt("ORDER_SEQ_L"));
		}
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ_H"))) {
			sbSql.append(" AND ORDER_SEQ < ");
			sbSql.append(parm.getInt("ORDER_SEQ_H"));
		}
		if (StringUtils.isEmpty(parm.getValue("ORDER_SEQ_L"))
				&& StringUtils.isEmpty(parm.getValue("ORDER_SEQ_H"))
				&& StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sbSql.append(" AND ORDER_SEQ = ");
			sbSql.append(parm.getInt("ORDER_SEQ"));
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString(), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ���»�ʿִ��ϸ���Ӧִ��ʱ��
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm updateOdiDspnDDate(TParm parm, TConnection conn) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("UPDATE ODI_DSPND SET ORDER_DATE = TO_CHAR(TO_DATE('");
		sbSql.append(parm.getValue("EFF_DATE"));
		sbSql.append("','YYYY/MM/DD HH24:MI:SS'),'YYYYMMDD'),ORDER_DATETIME = TO_CHAR(TO_DATE('");
		sbSql.append(parm.getValue("EFF_DATE"));
		sbSql.append("','YYYY/MM/DD HH24:MI:SS'),'HH24MI') ");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' ");
		
		// ��ϸ��һ���޸�
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ_L"))) {
			sbSql.append(" AND ORDER_SEQ >= ");
			sbSql.append(parm.getInt("ORDER_SEQ_L"));
		}
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ_H"))) {
			sbSql.append(" AND ORDER_SEQ < ");
			sbSql.append(parm.getInt("ORDER_SEQ_H"));
		}
		if (StringUtils.isEmpty(parm.getValue("ORDER_SEQ_L"))
				&& StringUtils.isEmpty(parm.getValue("ORDER_SEQ_H"))
				&& StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ"))) {
			sbSql.append(" AND ORDER_SEQ = ");
			sbSql.append(parm.getInt("ORDER_SEQ"));
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString(), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ����MedApply��ҽ��ʱ��
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm updateMedApplyDate(TParm parm, TConnection conn) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("UPDATE MED_APPLY SET ORDER_DATE = TO_DATE('");
		sbSql.append(parm.getValue("EFF_DATE"));
		sbSql.append("','YYYY/MM/DD HH24:MI:SS') ");
		sbSql.append(" WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND APPLICATION_NO = '");
		sbSql.append(parm.getValue("APPLICATION_NO"));
		sbSql.append("' ");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sbSql.toString(), conn));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return result;
		}
		return result;
	}
	
	/**
	 * ��ѯҽ��������
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryOdiOrder(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT * FROM ODI_ORDER WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append(" AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_NO_LIST"))) {
			sbSql.append(" AND ORDER_NO IN ('");
			sbSql.append(parm.getValue("ORDER_NO_LIST"));
			sbSql.append("') ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
	/**
	 * ��ѯ��ʿִ��ϸ������
	 * 
	 * @param parm
	 * @return result
	 */
	public TParm queryOdiDspnD(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT * FROM ODI_DSPND WHERE 1 = 1 ");
		
		if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
			sbSql.append("AND CASE_NO = '");
			sbSql.append(parm.getValue("CASE_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_NO"))) {
			sbSql.append("AND ORDER_NO = '");
			sbSql.append(parm.getValue("ORDER_NO"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(parm.getValue("ORDER_SEQ_LIST"))) {
			sbSql.append(" AND ORDER_SEQ IN ('");
			sbSql.append(parm.getValue("ORDER_SEQ_LIST"));
			sbSql.append("') ");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
	}
	
}
