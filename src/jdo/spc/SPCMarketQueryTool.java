package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 未销尽与滞销药品查询
 * </p>
 * 
 * <p>
 * Description: 未销尽与滞销药品查询
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author shendr 2013.10.18
 * @version 1.0
 */
public class SPCMarketQueryTool extends TJDOTool {

	private static SPCMarketQueryTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static SPCMarketQueryTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SPCMarketQueryTool();
		}
		return instanceObject;
	}

	public SPCMarketQueryTool() {
		onInit();
	}

	/**
	 * 滞销药品查询
	 * 
	 * @return
	 */
	public TParm queryByTypeA(TParm parm) {
		String org_code = parm.getValue("ORG_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String end_time = parm.getValue("END_TIME");
		String in_time = parm.getValue("IN_TIME");
		String sql = "SELECT F.ORG_CHN_DESC, "
				+ "G.ORDER_DESC, "
				+ "G.SPECIFICATION, "
				+ "C.BATCH_NO, "
				+ "C.VALID_DATE, "
				+ "MAX(K.IN_DATA) AS IN_DATA, "
				+ "MIN(L.OUT_DATA) AS OUT_DATA, "
				+ "FLOOR (E.STOCK_QTY / H.DOSAGE_QTY) || I.UNIT_CHN_DESC || "
				+ "   CASE WHEN MOD (E.STOCK_QTY, H.DOSAGE_QTY) > 0 "
				+ "        THEN MOD (E.STOCK_QTY, H.DOSAGE_QTY) || J.UNIT_CHN_DESC "
				+ "        ELSE '' END AS STOCK_QTY, " + "E.STOCK_COST, "
				+ "D.MAX_QTY, " + "D.SAFE_QTY, " + "D.MIN_QTY, "
				+ "D.ECONOMICBUY_QTY, " + "D.MATERIAL_LOC_CODE "
				+ "FROM (SELECT ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE "
				+ "           FROM IND_DDSTOCK " + "          WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND ORG_CODE = '" + org_code + "' ";
		}
		sql += " AND TRANDATE BETWEEN '" + start_time + "' AND '" + end_time
				+ "' ";
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND ORDER_CODE = '" + order_code + "' ";
		}
		sql += "          GROUP BY ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE HAVING SUM(OUT_QTY)<=0 "
				+ "      INTERSECT "
				+ "      SELECT ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE "
				+ "        FROM IND_DDSTOCK " + "       WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND ORG_CODE = '" + org_code + "' ";
		}
		sql += "         AND TRANDATE <= '" + in_time + "' ";
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND ORDER_CODE = '" + order_code + "' ";
		}
		sql += "       GROUP BY ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE HAVING SUM(IN_QTY) > 0"
				+ "       ) C, "
				+ "(SELECT A.ORG_CODE,A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,A.TRANDATE || ' ' ||  "
				+ "        FLOOR (A.IN_QTY / B.DOSAGE_QTY) || C.UNIT_CHN_DESC || CASE WHEN MOD (A.IN_QTY, B.DOSAGE_QTY) > 0 "
				+ "               THEN MOD (A.IN_QTY, B.DOSAGE_QTY) || D.UNIT_CHN_DESC ELSE '' END AS IN_DATA "
				+ "      FROM IND_DDSTOCK A,PHA_TRANSUNIT B,SYS_UNIT C,SYS_UNIT D "
				+ "     WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND A.ORG_CODE = '" + org_code + "' ";
		}
		sql += "       AND A.TRANDATE <= '" + start_time + "' "
				+ "       AND A.IN_QTY > 0 "
				+ "       AND A.ORDER_CODE=B.ORDER_CODE "
				+ "       AND B.STOCK_UNIT=C.UNIT_CODE "
				+ "       AND B.DOSAGE_UNIT=D.UNIT_CODE ";
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		sql += ")K, "
				+ "(SELECT A.ORG_CODE,A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,A.TRANDATE || ' ' || "
				+ "             FLOOR (A.OUT_QTY / B.DOSAGE_QTY) || C.UNIT_CHN_DESC || CASE WHEN MOD (A.OUT_QTY, B.DOSAGE_QTY) > 0 "
				+ "                    THEN MOD (A.OUT_QTY, B.DOSAGE_QTY) || D.UNIT_CHN_DESC ELSE '' END AS OUT_DATA "
				+ "           FROM IND_DDSTOCK A,PHA_TRANSUNIT B,SYS_UNIT C,SYS_UNIT D "
				+ "          WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND A.ORG_CODE = '" + org_code + "' ";
		}
		sql += "            AND A.TRANDATE > '" + end_time + "' "
				+ "            AND A.OUT_QTY > 0 "
				+ "            AND A.ORDER_CODE=B.ORDER_CODE "
				+ "            AND B.STOCK_UNIT=C.UNIT_CODE "
				+ "            AND B.DOSAGE_UNIT=D.UNIT_CODE ";
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		sql += ") L,IND_STOCKM D,"
				+ "(SELECT ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE,SUM(STOCK_QTY) AS STOCK_QTY,"
				+ "ROUND(SUM(STOCK_QTY*VERIFYIN_PRICE),2) AS STOCK_COST "
				+ "        FROM IND_STOCK " + "      WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND ORG_CODE = '" + org_code + "' ";
		}
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND ORDER_CODE = '" + order_code + "' ";
		}
		sql += "   GROUP BY ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE) E, "
				+ "IND_ORG F,PHA_BASE G, "
				+ "PHA_TRANSUNIT H,SYS_UNIT I,SYS_UNIT J "
				+ "WHERE C.ORG_CODE = L.ORG_CODE(+) "
				+ "  AND C.ORDER_CODE = L.ORDER_CODE(+) "
				+ "  AND C.BATCH_NO = L.BATCH_NO(+) "
				+ "  AND C.VALID_DATE = L.VALID_DATE(+) "
				+ "  AND C.ORG_CODE = K.ORG_CODE(+) "
				+ "  AND C.ORDER_CODE = K.ORDER_CODE(+) "
				+ "  AND C.BATCH_NO = K.BATCH_NO(+) "
				+ "  AND C.VALID_DATE = K.VALID_DATE(+) "
				+ "  AND C.ORG_CODE=D.ORG_CODE "
				+ "  AND C.ORDER_CODE=D.ORDER_CODE "
				+ "  AND C.ORG_CODE=E.ORG_CODE "
				+ "  AND C.ORDER_CODE=E.ORDER_CODE "
				+ "  AND C.BATCH_NO = E.BATCH_NO "
				+ "  AND C.VALID_DATE = E.VALID_DATE "
				+ "  AND C.ORG_CODE=F.ORG_CODE "
				+ "  AND C.ORDER_CODE=G.ORDER_CODE "
				+ "  AND C.ORDER_CODE=H.ORDER_CODE "
				+ "  AND I.UNIT_CODE = H.STOCK_UNIT "
				+ "  AND J.UNIT_CODE = H.DOSAGE_UNIT "
				+ "GROUP BY F.ORG_CHN_DESC, " + "G.ORDER_DESC, "
				+ "G.SPECIFICATION, " + "C.BATCH_NO, " + "C.VALID_DATE, "
				+ "H.DOSAGE_QTY," + "I.UNIT_CHN_DESC, " + "J.UNIT_CHN_DESC, "
				+ "D.MAX_QTY, " + "D.SAFE_QTY, " + "D.MIN_QTY, "
				+ "D.ECONOMICBUY_QTY, "
				+ "D.MATERIAL_LOC_CODE,E.STOCK_QTY,E.STOCK_COST "
				+ "ORDER BY F.ORG_CHN_DESC, " + "G.ORDER_DESC, "
				+ "G.SPECIFICATION, " + "C.BATCH_NO, " + "C.VALID_DATE";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 未销尽药品查询
	 * 
	 * @return
	 */
	public TParm queryByTypeB(TParm parm) {
		String org_code = parm.getValue("ORG_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String sql = "SELECT F.ORG_CHN_DESC, "
				+ "G.ORDER_DESC, "
				+ "G.SPECIFICATION, "
				+ "C.BATCH_NO, "
				+ "C.VALID_DATE, "
				+ "K.OUT_DATA, "
				+ "MAX(L.IN_DATA) AS IN_DATA, "
				+ "FLOOR (E.STOCK_QTY / H.DOSAGE_QTY) || I.UNIT_CHN_DESC || "
				+ "   CASE WHEN MOD (E.STOCK_QTY, H.DOSAGE_QTY) > 0 "
				+ "        THEN MOD (E.STOCK_QTY, H.DOSAGE_QTY) || J.UNIT_CHN_DESC "
				+ "        ELSE '' END AS STOCK_QTY, "
				+ "E.STOCK_COST, "
				+ "D.MAX_QTY, "
				+ "D.SAFE_QTY, "
				+ "D.MIN_QTY, "
				+ "D.ECONOMICBUY_QTY, "
				+ "D.MATERIAL_LOC_CODE "
				+ "FROM (SELECT A.ORG_CODE,A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE "
				+ "           FROM IND_DDSTOCK A,IND_STOCK B "
				+ "          WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND A.ORG_CODE = '" + org_code + "' ";
		}
		sql += "            AND A.TRANDATE <= '" + start_time + "' "
				+ "				   AND A.ORG_CODE=B.ORG_CODE "
				+ "				   AND A.ORDER_CODE=B.ORDER_CODE "
				+ "			   AND A.BATCH_NO=B.BATCH_NO "
				+ "		   AND A.VALID_DATE=B.VALID_DATE ";
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		sql += "        GROUP BY A.ORG_CODE,A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE HAVING SUM(A.IN_QTY)>0 AND SUM(B.STOCK_QTY)>0) C, "
				+ "(SELECT A.ORG_CODE,A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,A.TRANDATE || ' ' || "
				+ "           FLOOR (A.IN_QTY / B.DOSAGE_QTY) || C.UNIT_CHN_DESC || CASE WHEN MOD (A.IN_QTY, B.DOSAGE_QTY) > 0 "
				+ "                  THEN MOD (A.IN_QTY, B.DOSAGE_QTY) || D.UNIT_CHN_DESC ELSE '' END AS IN_DATA "
				+ "         FROM IND_DDSTOCK A,PHA_TRANSUNIT B,SYS_UNIT C,SYS_UNIT D "
				+ "        WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND A.ORG_CODE = '" + org_code + "' ";
		}
		sql += "          AND A.TRANDATE <= '" + start_time + "' "
				+ "          AND A.IN_QTY > 0 "
				+ "          AND A.ORDER_CODE=B.ORDER_CODE "
				+ "          AND B.STOCK_UNIT=C.UNIT_CODE "
				+ "          AND B.DOSAGE_UNIT=D.UNIT_CODE ";
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		sql += ") L,"
				+ "(SELECT A.ORG_CODE,A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE, "
				+ "       FLOOR (SUM(A.OUT_QTY) / B.DOSAGE_QTY) || C.UNIT_CHN_DESC || CASE WHEN MOD (SUM(A.OUT_QTY), B.DOSAGE_QTY) > 0 "
				+ "              THEN MOD (SUM(A.OUT_QTY), B.DOSAGE_QTY) || D.UNIT_CHN_DESC ELSE '' END AS OUT_DATA "
				+ "     FROM IND_DDSTOCK A,PHA_TRANSUNIT B,SYS_UNIT C,SYS_UNIT D "
				+ "    WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND A.ORG_CODE = '" + org_code + "' ";
		}
		sql += "      AND A.TRANDATE BETWEEN '" + start_time
				+ "' AND TO_CHAR(SYSDATE,'YYYYMMDD') "
				+ "     AND A.OUT_QTY > 0 "
				+ "     AND A.ORDER_CODE=B.ORDER_CODE "
				+ "     AND B.STOCK_UNIT=C.UNIT_CODE "
				+ "     AND B.DOSAGE_UNIT=D.UNIT_CODE ";
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		sql += "     GROUP BY A.ORG_CODE,A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,B.DOSAGE_QTY,C.UNIT_CHN_DESC,D.UNIT_CHN_DESC) K, "
				+ "IND_STOCKM D,"
				+ "(SELECT ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE,SUM(STOCK_QTY) AS STOCK_QTY,"
				+ "ROUND(SUM(STOCK_QTY*VERIFYIN_PRICE),2) AS STOCK_COST "
				+ "        FROM IND_STOCK " + "      WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND ORG_CODE = '" + org_code + "' ";
		}
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND ORDER_CODE = '" + order_code + "' ";
		}
		sql += "   GROUP BY ORG_CODE,ORDER_CODE,BATCH_NO,VALID_DATE) E, "
				+ "IND_ORG F,PHA_BASE G,PHA_TRANSUNIT H,SYS_UNIT I,SYS_UNIT J "
				+ "WHERE C.ORG_CODE = L.ORG_CODE(+) "
				+ "AND C.ORDER_CODE = L.ORDER_CODE(+) "
				+ "AND C.BATCH_NO = L.BATCH_NO(+) "
				+ "AND C.VALID_DATE = L.VALID_DATE(+) "
				+ "AND C.ORG_CODE = K.ORG_CODE(+) "
				+ "AND C.ORDER_CODE = K.ORDER_CODE(+) "
				+ "AND C.BATCH_NO = K.BATCH_NO(+) "
				+ "AND C.VALID_DATE = K.VALID_DATE(+) "
				+ "AND C.ORG_CODE=D.ORG_CODE "
				+ "AND C.ORDER_CODE=D.ORDER_CODE "
				+ "AND C.ORG_CODE=E.ORG_CODE "
				+ "AND C.ORDER_CODE=E.ORDER_CODE "
				+ "AND C.BATCH_NO = E.BATCH_NO "
				+ "AND C.VALID_DATE = E.VALID_DATE "
				+ "AND C.ORG_CODE=F.ORG_CODE "
				+ "AND C.ORDER_CODE=G.ORDER_CODE "
				+ "AND C.ORDER_CODE=H.ORDER_CODE "
				+ "AND I.UNIT_CODE = H.STOCK_UNIT "
				+ "AND J.UNIT_CODE = H.DOSAGE_UNIT "
				+ "GROUP BY F.ORG_CHN_DESC, " + "G.ORDER_DESC, "
				+ "G.SPECIFICATION, " + "C.BATCH_NO, " + "C.VALID_DATE, "
				+ "H.DOSAGE_QTY,I.UNIT_CHN_DESC,J.UNIT_CHN_DESC, "
				+ "D.MAX_QTY, " + "D.SAFE_QTY, " + "D.MIN_QTY, "
				+ "D.ECONOMICBUY_QTY, " + "D.MATERIAL_LOC_CODE, "
				+ "K.OUT_DATA,E.STOCK_QTY,E.STOCK_COST "
				+ "ORDER BY F.ORG_CHN_DESC, " + "K.OUT_DATA,"
				+ "G.ORDER_DESC, " + "G.SPECIFICATION, " + "C.BATCH_NO, "
				+ "C.VALID_DATE";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 滞销药品查询
	 * 
	 * @param parm
	 * @return
	 */
	public TParm queryByTypeC(TParm parm) {
		String org_code = parm.getValue("ORG_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		String start_time = parm.getValue("START_TIME");
		String sql = "SELECT C.ORDER_CODE,D.SUP_ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,E.MAN_CODE, "
				+ "A.BATCH_NO,B.STOCK_QTY,A.RETAIL_PRICE,B.STOCK_QTY * A.RETAIL_PRICE AS RETAIL_AMT,TO_DATE(A.TRANDATE,'YYYY-MM-DD') AS IN_DATE, "
				+ "C.NORMAL_DAYS,TO_DATE ('"
				+ start_time
				+ "', 'YYYY-MM-DD') - (TO_DATE(A.TRANDATE,'YYYY-MM-DD') + C.NORMAL_DAYS) AS UNMARKETABLE_DAYS "
				+ "FROM IND_DDSTOCK A,IND_STOCK B,PHA_BASE C,IND_CODE_MAP D,SYS_FEE E "
				+ "WHERE 1=1 ";
		if (!StringUtil.isNullString(org_code)) {
			sql += "AND A.ORG_CODE = '" + org_code + "' ";
		}
		if (!StringUtil.isNullString(order_code)) {
			sql += "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		sql += "AND A.ORG_CODE = B.ORG_CODE "
				+ "AND A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.BATCH_SEQ = B.BATCH_SEQ "
				+ "AND B.STOCK_QTY > 0 "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.SUP_CODE = D.SUP_CODE "
				+ "AND A.SUP_ORDER_CODE = D.SUP_ORDER_CODE "
				+ "AND A.ORDER_CODE = E.ORDER_CODE "
				+ "AND TO_DATE(A.TRANDATE,'YYYY-MM-DD') + C.NORMAL_DAYS <= TO_DATE('"
				+ start_time + "','YYYY-MM-DD')";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
