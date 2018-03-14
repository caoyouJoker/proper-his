package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:麻精耗用查询
 * </p>
 * 
 * <p>
 * Description:麻精耗用查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author fuwj 2013-08-06
 * @version 1.0
 */
public class SPCToxicSearchTool extends TJDOTool {

	private static SPCToxicSearchTool instanceObject;

	/**
	 * 构造器
	 */
	public SPCToxicSearchTool() {
		
	}
	
	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static SPCToxicSearchTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SPCToxicSearchTool();
		}
		return instanceObject;
	}

	/**
	 * 查询耗用部门
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getOrgCode(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String sql = "SELECT A.STATION_FLG FROM IND_ORG A where A.ORG_CODE='"
				+ orgCode + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询病区麻精耗用
	 * 
	 * @param parm
	 * @return
	 */ 
	public TParm getStationDate(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String toxicId = parm.getValue("TOXIC_ID");
		String orderCode = parm.getValue("ORDER_CODE");
		String mrNo = parm.getValue("MR_NO");
		String sql = "SELECT F.ORG_CHN_DESC,(CASE WHEN (A.IS_RECLAIM= 'Y') THEN " +
				"'已回收' ELSE '未回收' END)AS IS_RECLAIM,A.TOXIC_ID1,A.MR_NO,B.PAT_NAME," +
				"A.CASE_NO,A.OPT_DATE,A.ORDER_DESC,A.SPECIFICATION, FLOOR(MONTHS_BETWEEN(SYSDATE,B.BIRTH_DATE )/12) AS NL," +
				"C.USER_NAME AS DR_NAME,D.BATCH_NO,D.VALID_DATE,E.UNIT_CHN_DESC,A.MEDI_QTY,'1' AS SL,G.UNIT_CHN_DESC AS UNIT_CHN_DESC1," +
				"H.CHN_DESC AS SEX_NAME FROM IND_CABDSPN A,SYS_PATINFO B,SYS_OPERATOR C,SYS_DICTIONARY H,IND_CONTAINERD D,SYS_UNIT E," +
				"IND_ORG F,SYS_UNIT G WHERE A.MR_NO=B.MR_NO AND A.STATION_CODE=F.ORG_CODE AND B.SEX_CODE =H.ID  " +
				"AND C.USER_ID=A.VS_DR_CODE AND A.TOXIC_ID1=D.TOXIC_ID  AND E.UNIT_CODE=D.UNIT_CODE AND A.MEDI_UNIT=G.UNIT_CODE " +
				"AND A.TOXIC_ID1 IS NOT NULL AND A.STATION_CODE='"+orgCode+"' AND H.GROUP_ID='SYS_SEX' ";
		if (!"".equals(toxicId) && toxicId != null) {		    		
			sql = sql + " AND A.TOXIC_ID1='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.OPT_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
	//	sql = sql + " ORDER BY A.OPT_DATE DESC";	
		sql = sql + " UNION ALL SELECT F.ORG_CHN_DESC,(CASE WHEN (A.IS_RECLAIM= 'Y') THEN " +
		"'已回收' ELSE '未回收' END)AS IS_RECLAIM,A.TOXIC_ID2,A.MR_NO,B.PAT_NAME," +
		"A.CASE_NO,A.OPT_DATE,A.ORDER_DESC,A.SPECIFICATION, FLOOR(MONTHS_BETWEEN(SYSDATE,B.BIRTH_DATE )/12) AS NL," +
		"C.USER_NAME AS DR_NAME,D.BATCH_NO,D.VALID_DATE,E.UNIT_CHN_DESC,A.MEDI_QTY,'1' AS SL,G.UNIT_CHN_DESC AS UNIT_CHN_DESC1," +
		"H.CHN_DESC AS SEX_NAME FROM IND_CABDSPN A,SYS_PATINFO B,SYS_OPERATOR C,SYS_DICTIONARY H,IND_CONTAINERD D,SYS_UNIT E," +
		"IND_ORG F,SYS_UNIT G WHERE A.MR_NO=B.MR_NO AND A.STATION_CODE=F.ORG_CODE AND B.SEX_CODE =H.ID  " +
		"AND C.USER_ID=A.VS_DR_CODE AND A.TOXIC_ID2=D.TOXIC_ID  AND E.UNIT_CODE=D.UNIT_CODE AND A.MEDI_UNIT=G.UNIT_CODE " +
		"AND A.TOXIC_ID2 IS NOT NULL AND A.STATION_CODE='"+orgCode+"' AND H.GROUP_ID='SYS_SEX' ";
		if (!"".equals(toxicId) && toxicId != null) {		    		
			sql = sql + " AND A.TOXIC_ID2='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.OPT_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
	//	sql = sql + " ORDER BY A.OPT_DATE DESC";
		sql = sql + " UNION ALL SELECT F.ORG_CHN_DESC,(CASE WHEN (A.IS_RECLAIM= 'Y') THEN " +
		"'已回收' ELSE '未回收' END)AS IS_RECLAIM,A.TOXIC_ID3,A.MR_NO,B.PAT_NAME," +
		"A.CASE_NO,A.OPT_DATE,A.ORDER_DESC,A.SPECIFICATION, FLOOR(MONTHS_BETWEEN(SYSDATE,B.BIRTH_DATE )/12) AS NL," +
		"C.USER_NAME AS DR_NAME,D.BATCH_NO,D.VALID_DATE,E.UNIT_CHN_DESC,A.MEDI_QTY,'1' AS SL,G.UNIT_CHN_DESC AS UNIT_CHN_DESC1," +
		"H.CHN_DESC AS SEX_NAME FROM IND_CABDSPN A,SYS_PATINFO B,SYS_OPERATOR C,SYS_DICTIONARY H,IND_CONTAINERD D,SYS_UNIT E," +
		"IND_ORG F,SYS_UNIT G WHERE A.MR_NO=B.MR_NO AND A.STATION_CODE=F.ORG_CODE AND B.SEX_CODE =H.ID  " +
		"AND C.USER_ID=A.VS_DR_CODE AND A.TOXIC_ID3=D.TOXIC_ID  AND E.UNIT_CODE=D.UNIT_CODE AND A.MEDI_UNIT=G.UNIT_CODE " +
		"AND A.TOXIC_ID3 IS NOT NULL AND A.STATION_CODE='"+orgCode+"' AND H.GROUP_ID='SYS_SEX' ";
		if (!"".equals(toxicId) && toxicId != null) {		    		
			sql = sql + " AND A.TOXIC_ID3='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.OPT_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
	//	sql = sql + " ORDER BY A.OPT_DATE DESC";
System.out.println(sql+"==============================================");
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 门诊麻精耗用
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getOpdDate(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String toxicId = parm.getValue("TOXIC_ID");
		String orderCode = parm.getValue("ORDER_CODE");
		String mrNo = parm.getValue("MR_NO");
		String sql = "SELECT F.ORG_CHN_DESC,(CASE WHEN (A.IS_RECLAIM= 'Y') THEN '已回收' ELSE '未回收' END)AS IS_RECLAIM,A.TOXIC_ID1,A.MR_NO,B.PAT_NAME,A.CASE_NO,"
				+ "A.OPT_DATE,A.ORDER_DESC,A.SPECIFICATION,"
				+ "D.BATCH_NO,D.VALID_DATE,E.UNIT_CHN_DESC "
				+ "FROM OPD_ORDER A,SYS_PATINFO B,"
				+ "IND_CONTAINERD D,SYS_UNIT E,IND_ORG F WHERE A.MR_NO=B.MR_NO AND A.EXEC_DEPT_CODE=F.ORG_CODE "
				+ "AND A.TOXIC_ID1=D.TOXIC_ID "
				+ "AND E.UNIT_CODE=D.UNIT_CODE AND A.TOXIC_ID1 IS NOT NULL AND A.EXEC_DEPT_CODE='"
				+ orgCode + "' ";
		if (!"".equals(toxicId) && toxicId != null) {
			sql = sql + " AND A.TOXIC_ID1='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.OPT_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
		sql = sql + " ORDER BY A.OPT_DATE DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm getMrNo(TParm parm) {
		String mrNo = (String) parm.getData("MR_NO");
		String sql = "SELECT MR_NO,PAT_NAME FROM SYS_PATINFO WHERE MR_NO ='"
				+ mrNo + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 获取手术介入耗用
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getInvDate(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String toxicId = parm.getValue("TOXIC_ID");
		String orderCode = parm.getValue("ORDER_CODE");
		String mrNo = parm.getValue("MR_NO");
		String sql = "SELECT F.ORG_CHN_DESC,(CASE WHEN (A.IS_RECLAIM IS NOT NULL) THEN '已回收' ELSE '未回收' END) AS IS_RECLAIM,A.BAR_CODE AS TOXIC_ID1,A.MR_NO,"
				+ "B.PAT_NAME,A.CASE_NO,A.BILL_DATE AS OPT_DATE,"
				+ "A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,D.BATCH_NO,"
				+ "D.VALID_DATE,E.UNIT_CHN_DESC FROM SPC_INV_RECORD A,"
				+ "SYS_PATINFO B,PHA_BASE C,IND_CONTAINERD D,SYS_UNIT E,IND_ORG F "
				+ "WHERE A.CLASS_CODE='4' AND A.MR_NO=B.MR_NO AND A.ORDER_CODE=C.ORDER_CODE AND A.EXEC_DEPT_CODE=F.ORG_CODE "
				+ "AND A.BAR_CODE=D.TOXIC_ID AND A.UNIT_CODE=E.UNIT_CODE AND A.EXE_DEPT_CODE='"
				+ orgCode + "'";
		if (!"".equals(toxicId) && toxicId != null) {
			sql = sql + " AND A.BAR_CODE='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.BILL_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
		sql = sql + " ORDER BY A.BILL_DATE DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));

	}

	/**
	 * 查询全部
	 * 
	 * @param parm
	 * @return
	 */
	public TParm getAll(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String toxicId = parm.getValue("TOXIC_ID");
		String orderCode = parm.getValue("ORDER_CODE");
		String mrNo = parm.getValue("MR_NO");
		String sql =  "SELECT F.ORG_CHN_DESC,(CASE WHEN (A.IS_RECLAIM= 'Y') THEN " +
		"'已回收' ELSE '未回收' END)AS IS_RECLAIM,A.TOXIC_ID1,A.MR_NO,B.PAT_NAME," +
		"A.CASE_NO,A.OPT_DATE,A.ORDER_DESC,A.SPECIFICATION, FLOOR(MONTHS_BETWEEN(SYSDATE,B.BIRTH_DATE )/12) AS NL," +
		"C.USER_NAME AS DR_NAME,D.BATCH_NO,D.VALID_DATE,E.UNIT_CHN_DESC,A.MEDI_QTY,'1' AS SL,G.UNIT_CHN_DESC AS UNIT_CHN_DESC1," +
		"H.CHN_DESC AS SEX_NAME FROM IND_CABDSPN A,SYS_PATINFO B,SYS_OPERATOR C,SYS_DICTIONARY H,IND_CONTAINERD D,SYS_UNIT E," +
		"IND_ORG F,SYS_UNIT G WHERE A.MR_NO=B.MR_NO AND A.STATION_CODE=F.ORG_CODE AND B.SEX_CODE =H.ID  " +
		"AND C.USER_ID=A.VS_DR_CODE AND A.TOXIC_ID1=D.TOXIC_ID  AND E.UNIT_CODE=D.UNIT_CODE AND A.MEDI_UNIT=G.UNIT_CODE " +
		"AND A.TOXIC_ID1 IS NOT NULL AND H.GROUP_ID='SYS_SEX' ";
		if (!"".equals(toxicId) && toxicId != null) {
			sql = sql + " AND A.TOXIC_ID1='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.OPT_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}
		/*sql = sql
				+ "UNION ALL SELECT F.ORG_CHN_DESC,(CASE WHEN (A.IS_RECLAIM= 'Y') THEN '已回收' ELSE '未回收' END)AS IS_RECLAIM,A.TOXIC_ID1,A.MR_NO,B.PAT_NAME,A.CASE_NO,"
				+ "A.OPT_DATE,A.ORDER_DESC,A.SPECIFICATION,"
				+ "D.BATCH_NO,D.VALID_DATE,E.UNIT_CHN_DESC "
				+ "FROM OPD_ORDER A,SYS_PATINFO B,"
				+ "IND_CONTAINERD D,SYS_UNIT E,IND_ORG F WHERE A.MR_NO=B.MR_NO AND A.EXEC_DEPT_CODE=F.ORG_CODE "
				+ "AND A.TOXIC_ID1=D.TOXIC_ID "
				+ "AND E.UNIT_CODE=D.UNIT_CODE AND A.TOXIC_ID1 IS NOT NULL ";
		if (!"".equals(toxicId) && toxicId != null) {
			sql = sql + " AND A.TOXIC_ID1='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.OPT_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}*/
		/*sql = sql
				+ "UNION ALL SELECT F.ORG_CHN_DESC,(CASE WHEN (A.RECLAIM_USER IS NOT NULL) THEN '已回收' ELSE '未回收' END) AS IS_RECLAIM,A.BAR_CODE AS TOXIC_ID1,A.MR_NO,"
				+ "B.PAT_NAME,A.CASE_NO,A.BILL_DATE AS OPT_DATE,"
				+ "C.ORDER_DESC,C.SPECIFICATION,D.BATCH_NO,"
				+ "D.VALID_DATE,E.UNIT_CHN_DESC FROM SPC_INV_RECORD A,"
				+ "SYS_PATINFO B,PHA_BASE C,IND_CONTAINERD D,SYS_UNIT E,IND_ORG F "
				+ "WHERE A.CLASS_CODE='4' AND A.MR_NO=B.MR_NO AND A.ORDER_CODE=C.ORDER_CODE AND A.EXE_DEPT_CODE=F.ORG_CODE "
				+ "AND A.BAR_CODE=D.TOXIC_ID AND A.UNIT_CODE=E.UNIT_CODE ";
		if (!"".equals(toxicId) && toxicId != null) {
			sql = sql + " AND A.BAR_CODE='" + toxicId + "'";
		}
		if (startDate != null && !"".equals(startDate) && endDate != null
				&& !"".equals(endDate)) {
			startDate = startDate.substring(0, 19);
			endDate = endDate.substring(0, 19);
			sql = sql + "  AND A.BILL_DATE BETWEEN TO_DATE('" + startDate
					+ "','yyyy/MM/dd HH24:MI:SS') " + "AND TO_DATE('" + endDate
					+ "','yyyy/MM/dd HH24:MI:SS')";
		}
		if (!"".equals(orderCode) && orderCode != null) {
			sql = sql + " AND A.ORDER_CODE='" + orderCode + "'";
		}
		if (!"".equals(mrNo) && mrNo != null) {
			sql = sql + " AND A.MR_NO='" + mrNo + "'";
		}*/
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
