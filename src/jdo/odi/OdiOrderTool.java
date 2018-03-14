package jdo.odi;

import org.apache.commons.lang.StringUtils;

import com.dongyang.jdo.*;
import com.dongyang.config.TConfig;
import com.dongyang.db.TConnection;
import com.dongyang.data.TParm;

/**
 * <p>
 * Title:
 * </p>
 * 
 * <p>
 * Description:
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
 * @author not attributable
 * @version 1.0
 */
public class OdiOrderTool extends TJDODBTool {
	/**
	 * ʵ��
	 */
	public static OdiOrderTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return RuleTool
	 */
	public static OdiOrderTool getInstance() {
		if (instanceObject == null)
			instanceObject = new OdiOrderTool();
		return instanceObject;
	}

	public OdiOrderTool() {
	}

	/**
	 * ����סԺҽ��
	 * 
	 * @param parm
	 *            TParm
	 * @param con
	 *            TConnection
	 * @return TParm
	 */
	public TParm saveOrder(TParm parm, TConnection con) {
		String sqlStr[] = (String[]) parm.getData("ARRAY");
		TParm result=new TParm();
		//=================SHIBL add ����У�飨��ʿ�����ҽ��ɾҽ����======================================
		for (int i = 0; i < sqlStr.length; i++) {
			if (sqlStr[i].startsWith("DELETE FROM ODI_ORDER ")) {
				String sql = sqlStr[i].replaceFirst("DELETE", "SELECT CASE_NO,ORDER_NO,ORDER_SEQ")+" AND NS_CHECK_CODE IS NOT NULL";
				TParm queryParm=new TParm(this.select(sql));
				if(queryParm.getCount()>0){
					result.setErr(-100, "ҽ������˲���ɾ��!");
					System.out.println("У��ҽ������˲���ɾ����"+queryParm);
					return result;
				}
			}
			result= new TParm(this.update(sqlStr[i], con));
			if(result.getErrCode()<0){
				return result;
			}
		}
		return result;
	}

	/**
	 * ����Ժ�Ƿ���û��ͣ�õĳ���ҽ��
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean getUDOrder(String caseNo) {
		boolean falg = false;
		TParm parm = new TParm(this
				.select("SELECT ORDER_NO FROM ODI_ORDER WHERE CASE_NO='"
						+ caseNo + "' AND RX_KIND='UD' AND (DC_DATE IS NULL OR DC_DATE>SYSDATE) "
						+" AND OPBOOK_SEQ IS NULL "));//wanglong add 20141114 ���˵�����ҽ���������
		// System.out.println("����ҽ��sql---->"+"SELECT ORDER_NO FROM ODI_ORDER WHERE CASE_NO='"+caseNo+"' AND RX_KIND='UD' AND DC_DATE IS NULL");
		if (parm.getCount("ORDER_NO") > 0) {
			falg = true;
		}
		return falg;
	}

	/**
	 * �õ��������
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String getICDCode(String caseNo) {
		TParm parm = new TParm(
				this
						.select("SELECT B.ICD_CHN_DESC FROM ADM_INP A,SYS_DIAGNOSIS B WHERE CASE_NO='"
								+ caseNo + "' AND A.MAINDIAG=B.ICD_CODE"));
		return parm.getValue("ICD_CHN_DESC", 0);
	}

	/**
	 * �õ��������
	 * 
	 * @param caseNo
	 *            String
	 * @return String
	 */
	public String getICDCodeEng(String caseNo) {
		TParm parm = new TParm(
				this
						.select("SELECT B.ICD_ENG_DESC FROM ADM_INP A,SYS_DIAGNOSIS B WHERE CASE_NO='"
								+ caseNo + "' AND A.MAINDIAG=B.ICD_CODE"));
		return parm.getValue("ICD_ENG_DESC", 0);
	}

	/**
	 * �õ��������
	 * 
	 * @param ctzCode
	 *            String
	 * @return String
	 */
	public String getCTZDesc(String ctzCode) {
		TParm parm = new TParm(this
				.select("SELECT CTZ_DESC FROM SYS_CTZ WHERE CTZ_CODE='"
						+ ctzCode + "'"));
		return parm.getValue("CTZ_DESC", 0);
	}

	/**
	 * ����Ժʱ��ҩ�����Ƿ���� duzhw add 20130917
	 * 
	 * @param caseNo
	 *            String
	 * @return boolean
	 */
	public boolean getRtnCfmM(String caseNo) {
		boolean falg = false;
		TParm parm = new TParm(
				this
						.select("SELECT CASE_NO FROM ODI_DSPNM WHERE CASE_NO = '"
								+ caseNo
								+ "' AND  (PHA_RETN_CODE IS NULL OR PHA_RETN_CODE = '') AND (PHA_RETN_DATE IS NULL OR PHA_RETN_DATE = '') AND DSPN_KIND = 'RT'"));
		if (parm.getCount("CASE_NO") > 0) {
			falg = true;
		}
		return falg;
	}
	
	/**
	 * ��ѯ��ҩ����
	 * 
	 * @param caseNo �����
	 * @param rxKind ҽ�����
	 * @return result ���ݽ����
	 */
	public TParm queryPhaOrderData(String caseNo, String rxKind) {
		TParm result = new TParm();
		if (StringUtils.isEmpty(caseNo)) {
			result.setErr(-1, "�����Ϊ��");
			return result;
		}
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append(" SELECT ORDER_CODE,ORDER_DESC,DR_NOTE,TO_CHAR(EFF_DATE,'YYYY/MM/DD') AS EFF_DATE,");
		sbSql.append("TO_CHAR(EFF_DATE,'YYYY/MM/DD HH24:MI:SS') AS EFF_DATE_TIME,TO_CHAR (DC_DATE, 'YYYY/MM/DD') AS DC_DATE,");
		sbSql.append("TO_CHAR (DC_DATE, 'YYYY/MM/DD HH24:MI:SS') AS DC_DATE_IIME,");
		sbSql.append("MED_REPRESENTOR,B.USER_NAME AS ORDER_DR_NAME,ORDER_DR_CODE,");
		sbSql.append("MEDI_QTY,MEDI_UNIT,C.UNIT_CHN_DESC,A.FREQ_CODE,D.FREQ_CHN_DESC,");
		sbSql.append("A.ROUTE_CODE,E.ROUTE_CHN_DESC ");
		sbSql.append(" FROM ODI_ORDER A,SYS_OPERATOR B,SYS_UNIT C,SYS_PHAFREQ D,SYS_PHAROUTE E WHERE CASE_NO = '");
		sbSql.append(caseNo);
		sbSql.append("' ");
		
		if (StringUtils.isNotEmpty(rxKind)) {
			if (rxKind.contains(",")) {
				sbSql.append(" AND A.RX_KIND IN ('");
				sbSql.append(rxKind);
				sbSql.append("') ");
			} else {
				sbSql.append(" AND A.RX_KIND = '");
				sbSql.append(rxKind);
				sbSql.append("' ");
			}
		}
		
		sbSql.append(" AND A.CAT1_TYPE = 'PHA' AND A.NS_CHECK_CODE IS NOT NULL AND A.ORDER_DR_CODE = B.USER_ID ");
		sbSql.append(" AND A.MEDI_UNIT = C.UNIT_CODE AND A.FREQ_CODE = D.FREQ_CODE AND A.ROUTE_CODE = E.ROUTE_CODE ");
		sbSql.append(" ORDER BY ORDER_NO,ORDER_SEQ ");
		
		result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));

		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			return result;
		}

		return result;
	}
}
