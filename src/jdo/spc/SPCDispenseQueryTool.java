package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 药房销售统计Tool
 * </p>
 * 
 * <p>
 * Description: 药房销售统计Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * f Company: Javahis
 * </p>
 * 
 * @author shendr
 * @version 1.0
 */
public class SPCDispenseQueryTool extends TJDOTool {

	/**
	 * 实例对象
	 */
	private static SPCDispenseQueryTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static SPCDispenseQueryTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SPCDispenseQueryTool();
		}
		return instanceObject;
	}

	/**
	 * 药房销售统计查询
	 * 
	 * @return
	 */
	public TParm querySale(String org_code, String start_date, String end_date,
			String check_dis, String check_ret, String order_code) {
		String order_code_sql = "";
		// 按药品查询
		if (!"".equals(order_code)) {
			order_code_sql = " AND C.ORDER_CODE = '" + order_code + "' ";
		}
		String sql_dis = "";
		String sql_ret = "";
		if ("Y".equals(check_dis)) {
			// 门急诊发药出库部分
			sql_dis += " SELECT A.PHA_DOSAGE_DATE AS CHECK_DATE,A.ADM_TYPE||'_DPN' AS STATUS,"
					+ " C.ORDER_DESC,C.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,"
					+ " E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DOSAGE_QTY) AS OWN_PRICE, "
					+ " ROUND(SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY2)),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM OPD_ORDER A,SYS_FEE C,PHA_TRANSUNIT D,SYS_UNIT E,"
					+ " SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.ORDER_CODE = D.ORDER_CODE "
					+ " AND D.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.PHA_DOSAGE_DATE IS NOT NULL "
					+ " AND A.PHA_RETN_CODE IS NULL "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, F.DEPT_CHN_DESC,"
					+ " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.DOSAGE_QTY "
					+ " UNION ALL"
					// 住院发药出库部分
					+ " SELECT A.PHA_DOSAGE_DATE AS CHECK_DATE, 'I_DPN' AS STATUS, "
					+ " C.ORDER_DESC, C.SPECIFICATION, SUM(A.DOSAGE_QTY) "
					+ " AS QTY, E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DOSAGE_QTY) AS OWN_PRICE, "
					+ " ROUND(SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY2)),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM ODI_DSPNM A, SYS_FEE C, PHA_TRANSUNIT D, "
					+ " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.ORDER_CODE = D.ORDER_CODE "
					+ " AND D.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.PHA_DOSAGE_DATE IS NOT NULL "
					+ " AND A.PHA_RETN_DATE IS NULL "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.TAKEMED_ORG = '2' "
					+ " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC,F.DEPT_CHN_DESC,A.MR_NO,"
					+ " G.PAT_NAME, A.CASE_NO,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.DOSAGE_QTY ";
		}
		if ("Y".equals(check_ret)) {
			if (!"".equals(sql_dis))
				sql_ret = sql_ret + " UNION ";
			// 门急诊退药入库部分
			sql_ret += " SELECT A.PHA_RETN_DATE AS CHECK_DATE, A.ADM_TYPE||'_RET' AS STATUS, "
					+ " C.ORDER_DESC, C.SPECIFICATION, SUM (A.DOSAGE_QTY) AS QTY, "
					+ " E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DOSAGE_QTY) AS OWN_PRICE, "
					+ " ROUND(SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM OPD_ORDER A,SYS_FEE C,PHA_TRANSUNIT D,SYS_UNIT E, "
					+ " SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.ORDER_CODE = D.ORDER_CODE "
					+ " AND D.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND C.ORDER_CODE = D.ORDER_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.PHA_RETN_DATE IS NOT NULL "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.PHA_RETN_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.PHA_RETN_DATE, C.ORDER_DESC, C.SPECIFICATION, "
					+ " E.UNIT_CHN_DESC,F.DEPT_CHN_DESC,"
					+ " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.DOSAGE_QTY "
					+ " UNION ALL"
					// 住院退药入库部分--修改 By liyh 20120815 除数为0 ，
					+ " SELECT A.DSPN_DATE AS CHECK_DATE, 'I_RET' AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION, SUM (A.RTN_DOSAGE_QTY) -  "
					+ " NVL (SUM (A.CANCEL_DOSAGE_QTY), 0) AS QTY, "
					+ " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE1 AS OWN_PRICE,  "
					+ " ROUND(SUM(A.VERIFYIN_PRICE1*(A.RTN_DOSAGE_QTY-NVL(A.CANCEL_DOSAGE_QTY,0))),7) AS AMT,  "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM ODI_DSPNM A, PHA_BASE C, "
					+ " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.DSPN_KIND='RT' AND A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.DISPENSE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.DSPN_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.DSPN_DATE, C.ORDER_DESC, C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, C.STOCK_PRICE, F.DEPT_CHN_DESC, A.MR_NO, "
					+ " G.PAT_NAME, A.CASE_NO,A.VERIFYIN_PRICE1 ";
		}
		String sql = sql_dis + sql_ret;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
}
