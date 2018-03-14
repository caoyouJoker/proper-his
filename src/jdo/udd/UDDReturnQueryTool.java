package jdo.udd;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: סԺҩ����ҩͳ��
 * </p>
 * 
 * <p>
 * Description: סԺҩ����ҩͳ��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author fuwj 2009.09.22
 * @version 1.0
 */
public class UDDReturnQueryTool extends TJDOTool {

	/**
	 * ʵ��
	 */
	public static UDDReturnQueryTool instanceObject;

	/**
	 * �õ�ʵ��
	 * 
	 * @return
	 */
	public static UDDReturnQueryTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new UDDReturnQueryTool();
		}
		return instanceObject;
	}
	
	/**
	 * ��ѯ������Ϣ
	 * @param parm
	 * @return
	 */
	public TParm onQueryMaster(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String sql = "SELECT B.DEPT_CHN_DESC,A.ORDER_DESC,SUM(A.RTN_DOSAGE_QTY) AS QTY,C.UNIT_CHN_DESC,SUM(A.RTN_DOSAGE_QTY*A.OWN_PRICE) AS AMT  FROM ODI_DSPNM A,SYS_DEPT B,SYS_UNIT C WHERE A.DSPN_KIND='RT'  AND A.DEPT_CODE=B.DEPT_CODE  AND A.RTN_DOSAGE_UNIT=C.UNIT_CODE ";
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {    
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.DSPN_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orgCode) && orgCode != null) {
			sql = sql + " AND A.DEPT_CODE='" + orgCode + "'";
		}
		sql = sql +" GROUP BY B.DEPT_CHN_DESC,A.ORDER_DESC,A.ORDER_CODE,C.UNIT_CHN_DESC ORDER BY B.DEPT_CHN_DESC DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * ��ѯ��ϸ��Ϣ
	 * @param parm
	 * @return
	 */
	public TParm onQueryDetail(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String drCode = parm.getValue("VS_DR_CODE");
		String sql = "SELECT B.DEPT_CHN_DESC,A.ORDER_DESC,A.RTN_DOSAGE_QTY AS QTY,C.UNIT_CHN_DESC,A.RTN_DOSAGE_QTY*A.OWN_PRICE AS AMT,D.USER_NAME FROM ODI_DSPNM A,SYS_DEPT B,SYS_UNIT C,SYS_OPERATOR D WHERE A.DSPN_KIND='RT'  AND A.DEPT_CODE=B.DEPT_CODE  AND A.RTN_DOSAGE_UNIT=C.UNIT_CODE AND A.VS_DR_CODE=D.USER_ID ";
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {    
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.DSPN_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orgCode) && orgCode != null) {
			sql = sql + " AND A.DEPT_CODE='" + orgCode + "'";
		}
		if (!"".equals(drCode) && drCode != null) {
			sql = sql + " AND A.VS_DR_CODE='" + drCode + "'";
		}
		sql = sql +" ORDER BY D.USER_NAME DESC";					
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
