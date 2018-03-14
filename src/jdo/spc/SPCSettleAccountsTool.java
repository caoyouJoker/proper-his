package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>
 * Title: 门市住院药房结算
 * </p>
 * 
 * <p>
 * Description: 门市住院药房结算
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: BLUECORE
 * </p>
 * 
 * @author YUANXM 2013.04.22
 * @version 1.0
 */
public class SPCSettleAccountsTool extends TJDOTool {
	/**
	 * 实例
	 */
	private static SPCSettleAccountsTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatAdmTool
	 */
	public static SPCSettleAccountsTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCSettleAccountsTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCSettleAccountsTool() {
		setModuleName("spc\\SPCSettleAccountsModule.x");
		onInit();
	}

	public TParm onQuery(TParm parm) {

		// 部门 门、市、住院药房
		String orgCode = parm.getValue("ORG_CODE");

		// 开始时间
		String startDate = parm.getValue("START_DATE");

		// 结束时间
		String endDate = parm.getValue("END_DATE");

		// 普药、麻精，全部
		String isDrug = parm.getValue("DRUG_CATEGORY");

		// 药品代码
		String orderCode = parm.getValue("ORDER_CODE");

		// 供应商代码
		String supCode = parm.getValue("SUP_CODE");

		// 医疗卡上线后 OPD_ORDER_HISTORY 变成 OPD_ORDER的问题
		String isNew = parm.getValue("DATA_CHOOSE");

		// 药品分类
		String phaType = parm.getValue("TYPE_CODE");

		TParm deptParm = null;

		// 住院药房
		String ipdFitFlg = "";

		// 门诊药房
		String opdFitFlg = "";

		String sql = "";

		// 如果orgCode 为空，查询所有的,否查询单个部门的
		if (orgCode == null || orgCode.equals("")) {

			// 结算部门列表
			TParm indOrgParm = getIndOrgAccount();

			int count = indOrgParm.getCount("ORG_CODE");
			for (int i = 0; i < count; i++) {
				TParm rowParm = (TParm) indOrgParm.getRow(i);

				orgCode = rowParm.getValue("ORG_CODE");
				deptParm = getSysDept(orgCode);
				if (deptParm.getCount() > 0) {
					deptParm = deptParm.getRow(0);
				}
				// 住院药房
				ipdFitFlg = deptParm.getValue("IPD_FIT_FLG");

				// 门诊药房
				opdFitFlg = deptParm.getValue("OPD_FIT_FLG");

				if (i < count - 1) {
					if ("Y".equals(ipdFitFlg)) {

						// 住院SQL
						sql += getIPDSearchSql(startDate, endDate, isDrug,
								orgCode, orderCode, supCode, phaType)
								+ " UNION ALL  ";
					} else if ("Y".equals(opdFitFlg)) {

						// 门诊、市内SQL
						// sql += getOPDSearchSql(startDate, endDate, isDrug,
						// orgCode, orderCode, supCode)
						sql += getOPDSearchSql(startDate, endDate, isDrug,
								orgCode, orderCode, supCode, isNew, phaType)
								+ " UNION ALL ";
					}
				} else {
					if ("Y".equals(ipdFitFlg)) {
						sql += getIPDSearchSql(startDate, endDate, isDrug,
								orgCode, orderCode, supCode, phaType);
					} else if ("Y".equals(opdFitFlg)) {
						// sql += getOPDSearchSql(startDate, endDate, isDrug,
						// orgCode, orderCode, supCode);
						sql += getOPDSearchSql(startDate, endDate, isDrug,
								orgCode, orderCode, supCode, isNew, phaType);
					}
				}
			}

			sql = " SELECT A.DELIVERYCODE,A.CSTCODE,A.ORDER_CODE,A.ORDER_DESC,"
					+ "A.SPECIFICATION,A.QTY,A.UNIT_CHN_DESC,A.DOSAGE_QTY,A.DOSAGE_UNIT,"
					+ "A.ODD,A.PURCHASEID,A.LAST_ODD,A.VERIFYIN_PRICE,A.CONTRACT_PRICE,"
					+ "A.ORG_CODE,A.STOCK_UNIT,A.FDOSAGE_UNIT,A.SUP_ORDER_CODE "
					+ "FROM ( " + sql + " ) A  ORDER BY A.ORDER_CODE ";

			System.out.println("结算sql--:" + sql);
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			return result;
		}

		deptParm = getSysDept(orgCode);
		if (deptParm.getCount() > 0) {
			deptParm = deptParm.getRow(0);
		} else {
			return new TParm();
		}

		// 住院药房
		ipdFitFlg = deptParm.getValue("IPD_FIT_FLG");

		// 门诊药房
		opdFitFlg = deptParm.getValue("OPD_FIT_FLG");

		if ("Y".equals(ipdFitFlg)) {
			sql = getIPDSearchSql(startDate, endDate, isDrug, orgCode,
					orderCode, supCode, phaType);
		} else if ("Y".equals(opdFitFlg)) {
			// sql = getOPDSearchSql(startDate, endDate, isDrug, orgCode,
			// orderCode, supCode);
			sql = getOPDSearchSql(startDate, endDate, isDrug, orgCode,
					orderCode, supCode, isNew, phaType);
		}
		sql = " SELECT A.DELIVERYCODE,A.CSTCODE,A.ORDER_CODE,A.ORDER_DESC,"
				+ "A.SPECIFICATION,A.QTY,A.UNIT_CHN_DESC,A.DOSAGE_QTY,A.DOSAGE_UNIT,"
				+ "A.ODD,A.PURCHASEID,A.LAST_ODD,A.VERIFYIN_PRICE,A.CONTRACT_PRICE,"
				+ "A.ORG_CODE,A.STOCK_UNIT,A.FDOSAGE_UNIT,A.SUP_ORDER_CODE "
				+ "FROM ( " + sql + " ) A  ORDER BY A.ORDER_CODE ";
		System.out.println("2结算sql--:" + sql);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	/**
	 * ODI
	 * 
	 * @param startDate
	 * @param endDate
	 * @param isDrug
	 * @param orgCode
	 * @param orderCode
	 * @param supCode
	 * @return
	 */
	private String getIPDSearchSql(String startDate, String endDate,
			String isDrug, String orgCode, String orderCode, String supCode,
			String pha_type) {

		TParm indOrgParm = getIndOrg(orgCode);

		String odiSql = getOdiDspnmSql(startDate, endDate, isDrug, supCode);

		String odiRtnSql = getOdiRtnSql(startDate, endDate, isDrug);
		// fux modify 20150629 加入厂商
		String dispenseSql = getDispenseDSql(startDate, endDate, isDrug,
				indOrgParm, orgCode, supCode);

		String indOddSql = getIndOddSql(orgCode, endDate, isDrug);

		String closeDate = getEndDateStr(endDate);

		String sql = " SELECT '932290201' AS DELIVERYCODE,'F21' AS CSTCODE,E.ORDER_CODE,F.ORDER_DESC,F.SPECIFICATION,  "
				// +
				// "  E.DOSAGE_QTY AS QTY,I.UNIT_CHN_DESC,FLOOR(E.DOSAGE_QTY/G.DOSAGE_QTY/H.CONVERSION_RATIO) AS DOSAGE_QTY, "//20150525
				// wangjc modify
				+ "  E.DOSAGE_QTY AS QTY,I.UNIT_CHN_DESC,"
				+ "  FLOOR (E.DOSAGE_QTY / G.DOSAGE_QTY / (CASE WHEN H.CONVERSION_RATIO IS NULL THEN 1 ELSE H.CONVERSION_RATIO END)) AS DOSAGE_QTY, "// 20150525
				// wangjc
				// add
				// +
				// "  J.UNIT_CHN_DESC AS DOSAGE_UNIT,MOD(E.DOSAGE_QTY,G.DOSAGE_QTY*H.CONVERSION_RATIO) AS ODD,"//20150525
				// wangjc modify
				+ "  (SELECT J.UNIT_CHN_DESC FROM SYS_UNIT J WHERE J.UNIT_CODE = H.SUPPLY_UNIT_CODE) AS DOSAGE_UNIT, "// 20150525
				// wangjc
				// add
				+ "  MOD (E.DOSAGE_QTY, G.DOSAGE_QTY * (CASE WHEN H.CONVERSION_RATIO IS NULL THEN 1 ELSE H.CONVERSION_RATIO END)) AS ODD, "// 20150525
				// wangjc
				// add
				+ "  TO_CHAR(SYSDATE,'YYYYMMDD') AS PURCHASEID , "
				+ "  (SELECT A.ODD_QTY "
				+ "		FROM IND_ODD A "
				+ "    	WHERE A.ORG_CODE='"
				+ orgCode
				+ "' AND A.CLOSE_DATE='"
				+ closeDate
				+ "' AND A.ORDER_CODE=E.ORDER_CODE AND A.SUP_ORDER_CODE=E.SUP_ORDER_CODE ) AS LAST_ODD, "
				// +
				// "   ( SELECT A.LAST_VERIFY_PRICE FROM IND_AGENT A WHERE A.SUP_CODE='"
				// + supCode
				// + "' AND A.ORDER_CODE=E.ORDER_CODE "
				// + "    ) AS VERIFYIN_PRICE, "
				+ "E.OWN_PRICE AS VERIFYIN_PRICE, " // modify by wukai on
				// 20170215
				+ "   ( SELECT A.CONTRACT_PRICE FROM IND_AGENT A WHERE A.SUP_CODE='"
				+ supCode
				+ "' AND A.ORDER_CODE=E.ORDER_CODE "
				+ "    ) AS CONTRACT_PRICE, "
				+ "   '"
				+ orgCode
				+ "'   AS ORG_CODE,F.STOCK_UNIT, F.DOSAGE_UNIT AS FDOSAGE_UNIT ,E.SUP_ORDER_CODE "
				// +
				// " FROM (   SELECT D.ORDER_CODE,SUM(D.DOSAGE_QTY) AS DOSAGE_QTY,D.SUP_ORDER_CODE "
				// //modify by wukai on 20170215
				+ " FROM (   SELECT D.ORDER_CODE,SUM(D.DOSAGE_QTY) AS DOSAGE_QTY, D.SUP_ORDER_CODE, D.OWN_PRICE "
				+ "	       FROM ( "
				+ odiSql
				+ "   		UNION ALL  "
				+ odiRtnSql
				+ "			UNION ALL "
				+ dispenseSql
				+ "         	UNION ALL	"
				+ indOddSql
				+ " )  "
				// + " D GROUP BY D.ORDER_CODE,D.SUP_ORDER_CODE  ) " //modify by
				// wukai on 20170215
				+ " D GROUP BY D.ORDER_CODE, D.SUP_ORDER_CODE, D.OWN_PRICE  ) "
				+ " E,PHA_BASE F,PHA_TRANSUNIT G,IND_CODE_MAP H,SYS_UNIT I "
				// + ",SYS_UNIT J   "//20150525 wangjc modify
				+ " WHERE F.ORDER_CODE=E.ORDER_CODE "
				+ "	AND G.ORDER_CODE=E.ORDER_CODE "
				+ "   AND I.UNIT_CODE=F.DOSAGE_UNIT  "
				// + "   AND H.SUPPLY_UNIT_CODE=J.UNIT_CODE "//20150525 wangjc
				// modify
				// + "   AND H.SUP_ORDER_CODE = E.SUP_ORDER_CODE "//20150525
				// wangjc modify
				+ "   AND H.SUP_ORDER_CODE(+) = E.SUP_ORDER_CODE "// 20150525
				// wangjc
				// add
				// + "   AND H.SUP_CODE = '" + supCode + "' "//20150525 wangjc
				// modify
				// FUX MODIFY 20150629 关联上ind_code_map
				+ "   AND H.SUP_CODE = '" + supCode + "'" + " ";
		if (orderCode != null && !orderCode.equals("")) {
			sql += " AND E.ORDER_CODE='" + orderCode + "' ";
		}

		if (!pha_type.equals("ALL")) {
			sql += " AND F.TYPE_CODE ='" + pha_type + "' ";
		}

		return sql;
	}

	/**
	 * OPD
	 * 
	 * @param startDatOe
	 * @param endDate
	 * @param isDrug
	 * @param orgCode
	 * @param orderCode
	 * @param supCode
	 * @param isNew
	 *            新 老数据 新数据.NEW 老数据.OLD
	 * @return
	 */
	public String getOPDSearchSql(String startDate, String endDate,
			String isDrug, String orgCode, String orderCode, String supCode,
			String isNew, String pha_type) {

		TParm indOrgParm = getIndOrg(orgCode);
		String closeDate = getEndDateStr(endDate);
		String opdSql = getOpdSql(startDate, endDate, isDrug, supCode, orgCode);
		String opdRtnSql = getOpdRtnSql(startDate, endDate, isDrug, supCode,
				orgCode, isNew);
		String sql = "SELECT '932290201' AS DELIVERYCODE, "
				+ "'F21' AS CSTCODE, "
				+ "E.ORDER_CODE, "
				+ "F.ORDER_DESC, "
				+ "F.SPECIFICATION, "
				+ "E.DOSAGE_QTY AS QTY, "
				+ "I.UNIT_CHN_DESC , "
				// +
				// "FLOOR(E.DOSAGE_QTY/G.DOSAGE_QTY/H.CONVERSION_RATIO) AS DOSAGE_QTY, "//20150525
				// wangjc modify
				+ "FLOOR (E.DOSAGE_QTY / G.DOSAGE_QTY / (CASE WHEN H.CONVERSION_RATIO IS NULL THEN 1 ELSE H.CONVERSION_RATIO END)) AS DOSAGE_QTY, "// 20150525
				// wangjc
				// add
				// + "J.UNIT_CHN_DESC AS DOSAGE_UNIT, "
				+ "(SELECT J.UNIT_CHN_DESC FROM SYS_UNIT J WHERE J.UNIT_CODE = H.SUPPLY_UNIT_CODE) AS DOSAGE_UNIT, "// 20150525
				// wangjc
				// add
				// +
				// "MOD(E.DOSAGE_QTY,G.DOSAGE_QTY*H.CONVERSION_RATIO) AS ODD, "//20150525
				// wangjc modify
				+ "MOD (E.DOSAGE_QTY, G.DOSAGE_QTY * (CASE WHEN H.CONVERSION_RATIO IS NULL THEN 1 ELSE H.CONVERSION_RATIO END)) AS ODD, "// 20150525
				// wangjc
				// add
				+ "TO_CHAR(SYSDATE,'YYYYMMDD') AS PURCHASEID, "
				+ "(SELECT A.ODD_QTY " + "FROM IND_ODD A "
				+ "WHERE A.ORG_CODE='"
				+ orgCode
				+ "' "
				+ "AND A.ORDER_CODE=E.ORDER_CODE "
				+ "AND A.CLOSE_DATE = '"
				+ closeDate
				+ "' "
				+ "AND A.SUP_ORDER_CODE=E.SUP_ORDER_CODE "
				+ ") AS LAST_ODD, "
				// +
				// "(SELECT J.LAST_VERIFY_PRICE FROM IND_AGENT J WHERE J.SUP_CODE='"
				// + supCode
				// + "' AND J.ORDER_CODE=E.ORDER_CODE) AS VERIFYIN_PRICE ,"
				// modify by wukai on 20170215
				+ "E.OWN_PRICE AS VERIFYIN_PRICE, "
				+ "(SELECT J.CONTRACT_PRICE FROM IND_AGENT J WHERE J.SUP_CODE='"
				+ supCode
				+ "' AND J.ORDER_CODE=E.ORDER_CODE) AS CONTRACT_PRICE ,"
				+ "   '"
				+ orgCode
				+ "'   AS ORG_CODE ,F.STOCK_UNIT, F.DOSAGE_UNIT AS FDOSAGE_UNIT,E.SUP_ORDER_CODE "
				+ "FROM ( "
				// +
				// "SELECT D.ORDER_CODE,SUM(D.DOSAGE_QTY) AS DOSAGE_QTY,D.SUP_ORDER_CODE "
				// //modify by wukai on 20170215
				+ "SELECT D.ORDER_CODE, SUM(D.DOSAGE_QTY) AS DOSAGE_QTY, D.SUP_ORDER_CODE, D.OWN_PRICE  "
				+ "FROM ( "
				+ opdSql
				+ "  UNION ALL "
				+ opdRtnSql
				+ "  UNION ALL ";

		if (indOrgParm.getCount("ORG_CODE") > 0) {
			// fux modify 20150629
			sql += getDispenseDSql(startDate, endDate, isDrug, indOrgParm,
					orgCode, supCode);
			sql += "  UNION ALL  ";

		}

		String indOddSql = getIndOddSql(orgCode, endDate, isDrug);
		sql += indOddSql // + ") D " +
				// "GROUP BY D.ORDER_CODE, D.SUP_ORDER_CODE "
				// //modify by wukai on 20170215
				+ ") D "
				+ "GROUP BY D.ORDER_CODE, D.SUP_ORDER_CODE, D.OWN_PRICE "
				+ ") E, "
				+ "PHA_BASE F, "
				+ "PHA_TRANSUNIT G, "
				+ "IND_CODE_MAP H, "
				+ "SYS_UNIT I "
				// + "SYS_UNIT J "//20150525 wangjc modify
				+ "WHERE F.ORDER_CODE=E.ORDER_CODE "
				+ "AND G.ORDER_CODE=E.ORDER_CODE "
				+ "AND I.UNIT_CODE=F.DOSAGE_UNIT   "
				// + "AND H.SUPPLY_UNIT_CODE=J.UNIT_CODE "//20150525 wangjc
				// modify
				// + "AND H.SUP_ORDER_CODE = E.SUP_ORDER_CODE "//20150525 wangjc
				// modify
				+ "AND H.SUP_ORDER_CODE(+) = E.SUP_ORDER_CODE "// 20150525
				// wangjc add
				// + "AND H.SUP_CODE = '" + supCode + "' "//20150525 wangjc
				// modify
				// + "AND H.SUP_CODE(+) = '" + supCode + "' ";//20150525 wangjc
				// add
				// FUX MODIFY 20150629 关联上ind_code_map
				+ "AND H.SUP_CODE = '" + supCode + "' ";// 20150525 wangjc add
		if (orderCode != null && !orderCode.equals("")) {
			sql += " AND E.ORDER_CODE='" + orderCode + "' ";
		}

		if (!pha_type.equals("ALL")) {
			sql += " AND F.TYPE_CODE ='" + pha_type + "' ";
		}

		// System.out.println("市，门SQL-------------:"+sql);
		return sql;
	}

	/**
	 * ODI
	 * 
	 * @param startDate
	 * @param endDate
	 * @param isDrug
	 * @param supCode
	 * @return
	 */
	public String getOdiDspnmSql(String startDate, String endDate,
			String isDrug, String supCode) {

		String sql = // "     SELECT A.ORDER_CODE, A.DISPENSE_QTY1 AS DOSAGE_QTY,C.SUP_ORDER_CODE "
		// //modify by wukai 20170215 单价取交易表 4819
		"     SELECT A.ORDER_CODE, A.OWN_PRICE, A.DISPENSE_QTY1 AS DOSAGE_QTY,C.SUP_ORDER_CODE "
				+ "		FROM ODI_DSPNM A,IND_STOCK C  "
				+ "		WHERE   A.EXEC_DEPT_CODE=C.ORG_CODE   "
				+ "		       AND A.ORDER_CODE=C.ORDER_CODE "
				+ "		       AND A.BATCH_SEQ1=C.BATCH_SEQ "
				+ "		       AND C.SUP_CODE='"
				+ supCode
				+ "' "
				+ "		       AND A.PHA_DOSAGE_DATE >= TO_DATE ('"
				+ startDate
				+ "', 'YYYYMMDDHH24MISS') "
				+ "		       AND A.PHA_DOSAGE_DATE <= TO_DATE ('"
				+ endDate
				+ "','YYYYMMDDHH24MISS') ";
		if (isDrug.equals("E")) {
			sql += " AND A.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "		       AND (   A.CTRLDRUGCLASS_CODE IS NULL "
					+ "		            OR A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "		       AND (   A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "		            	   AND A.CTRLDRUGCLASS_CODE IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		}
		sql += // "		       AND A.TAKEMED_ORG = '2' " +//wanglong delete
		// 20150126
		"		UNION ALL  "
				// +
				// "		SELECT A.ORDER_CODE, A.DISPENSE_QTY2 AS DOSAGE_QTY,C.SUP_ORDER_CODE "
				// //modify by wukai 20170215 单价取交易表 4819
				+ "		SELECT A.ORDER_CODE, A.OWN_PRICE, A.DISPENSE_QTY2 AS DOSAGE_QTY,C.SUP_ORDER_CODE "
				+ "		FROM ODI_DSPNM A, IND_STOCK C  "
				+ "		WHERE   A.EXEC_DEPT_CODE=C.ORG_CODE "
				+ "		       AND A.ORDER_CODE=C.ORDER_CODE "
				+ "		       AND A.BATCH_SEQ2=C.BATCH_SEQ "
				+ "		       AND C.SUP_CODE='" + supCode + "' "
				+ "		       AND A.PHA_DOSAGE_DATE >= TO_DATE ('" + startDate
				+ "','YYYYMMDDHH24MISS') "
				+ "		       AND A.PHA_DOSAGE_DATE <= TO_DATE ('" + endDate
				+ "','YYYYMMDDHH24MISS') ";
		if (isDrug.equals("E")) {
			sql += " AND A.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "		       AND (   A.CTRLDRUGCLASS_CODE IS NULL "
					+ "		            OR A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "		       AND (   A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "		            	   AND A.CTRLDRUGCLASS_CODE IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		}
		sql += // "		       AND A.TAKEMED_ORG = '2' " +//wanglong delete
		// 20150126
		"		UNION ALL  "
				// +
				// "		SELECT A.ORDER_CODE, A.DISPENSE_QTY3 AS DOSAGE_QTY,C.SUP_ORDER_CODE "
				// //modify by wukai 20170215 单价取交易表 4819
				+ "		SELECT A.ORDER_CODE, A.OWN_PRICE, A.DISPENSE_QTY3 AS DOSAGE_QTY,C.SUP_ORDER_CODE "
				+ "		FROM ODI_DSPNM A, IND_STOCK C  "
				+ "		WHERE  A.EXEC_DEPT_CODE=C.ORG_CODE  "
				+ "		       AND A.ORDER_CODE=C.ORDER_CODE "
				+ "		       AND A.BATCH_SEQ3=C.BATCH_SEQ "
				+ "		       AND C.SUP_CODE='" + supCode + "' "
				+ "		       AND A.PHA_DOSAGE_DATE >= TO_DATE ('" + startDate
				+ "','YYYYMMDDHH24MISS') "
				+ "		       AND A.PHA_DOSAGE_DATE <= TO_DATE ('" + endDate
				+ "','YYYYMMDDHH24MISS') ";
		if (isDrug.equals("E")) {
			sql += " AND A.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "		       AND (   A.CTRLDRUGCLASS_CODE IS NULL "
					+ "		            OR A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "		       AND (   A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "		            	   AND A.CTRLDRUGCLASS_CODE IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		}
		// sql += "		       AND A.TAKEMED_ORG = '2' ";//wanglong delete 20150126

		return sql;
	}

	/**
	 * DISPENSED
	 * 
	 * @param startDate
	 * @param endDate
	 * @param isDrug
	 * @param parm
	 * @param toOrgCode
	 * @return
	 */
	public String getDispenseDSql(String startDate, String endDate,
			String isDrug, TParm parm, String toOrgCode, String supCode) {
		// 子库组合

		String sql = "";
		sql = // "   SELECT B.ORDER_CODE, CASE WHEN B.UNIT_CODE=D.STOCK_UNIT THEN B.ACTUAL_QTY*C.DOSAGE_QTY "
		// //modify by wukai 20170215 单价取交易表 4819
		"   SELECT B.ORDER_CODE, B.RETAIL_PRICE AS OWN_PRICE, CASE WHEN B.UNIT_CODE=D.STOCK_UNIT THEN B.ACTUAL_QTY * C.DOSAGE_QTY "
				+ "          ELSE B.ACTUAL_QTY END AS DOSAGE_QTY, B.SUP_ORDER_CODE "
				+ "   FROM IND_DISPENSEM A, IND_DISPENSED B,PHA_TRANSUNIT C,PHA_BASE D "
				+ "	  WHERE ( A.UPDATE_FLG = '3' OR A.UPDATE_FLG = '1' ) AND "
				+ "         A.REQTYPE_CODE IN ('EXM','TEC') AND "
				+ "		    A.TO_ORG_CODE='"
				+ toOrgCode
				+ "'   AND "
				+ "         A.DISPENSE_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "','YYYYMMDDHH24MISS') AND "
				+ "         TO_DATE ('"
				+ endDate
				+ "','YYYYMMDDHH24MISS')  AND "
				// fux modify 20150629 加入厂商
				+ "      B.SUP_CODE='"
				+ supCode
				+ "'  AND  "
				+ "         A.DISPENSE_NO = B.DISPENSE_NO AND "
				+ "         B.ORDER_CODE=C.ORDER_CODE AND "
				+ "         B.ORDER_CODE=D.ORDER_CODE   ";
		if (isDrug.equals("E")) {
			sql += " AND D.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "		       AND (   D.CTRLDRUGCLASS_CODE IS NULL "
					+ "		            OR D.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "		       AND (   D.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "		            	   AND D.CTRLDRUGCLASS_CODE IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		}
		;

		return sql;

	}

	/**
	 * ODI 退药数据
	 * 
	 * @param startDate
	 * @param endDate
	 * @param isDrug
	 *            Y:普药 N：麻精 空全部
	 * @return
	 */
	public String getOdiRtnSql(String startDate, String endDate, String isDrug) {
		String sql = "";
		sql = // "SELECT A.ORDER_CODE, -1 * (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS DOSAGE_QTY, C.SUP_ORDER_CODE "//wanglong
		// modify 20150126
		"SELECT A.ORDER_CODE, A.OWN_PRICE, -1 * (A.RTN_DOSAGE_QTY - A.CANCEL_DOSAGE_QTY) AS DOSAGE_QTY, C.SUP_ORDER_CODE "// modify
		// by
				// wukai
				// on
				// 20170215
				+ "	  FROM ODI_DSPNM A, PHA_BASE B, IND_STOCK C  "
				// fux modify 20150702 退药时间区间 改为 实际退药时间 而不是登记或者摆药时间
				+ "		 WHERE  A.PHA_RETN_DATE BETWEEN TO_DATE ('"
				// + "		 WHERE  A.DSPN_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "', 'YYYYMMDDHH24MISS') "
				+ "        AND TO_DATE ('"
				+ endDate
				+ "', 'YYYYMMDDHH24MISS') "
				+ "   AND A.DSPN_KIND = 'RT'  "
				+ "   AND A.ORDER_CODE = B.ORDER_CODE "
				+ "        AND A.EXEC_DEPT_CODE = C.ORG_CODE  "
				+ "	       AND A.ORDER_CODE = C.ORDER_CODE "
				+ "	       AND A.BATCH_SEQ1 = C.BATCH_SEQ       ";
		if (isDrug.equals("E")) {
			sql += " AND B.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "		       AND (   B.CTRLDRUGCLASS_CODE IS NULL "
					+ "		            OR B.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "		       AND (   B.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "		            	   AND B.CTRLDRUGCLASS_CODE IN (SELECT CTRLDRUGCLASS_CODE "
					+ "		                                              FROM SYS_CTRLDRUGCLASS "
					+ "		                                             WHERE CTRL_FLG = 'Y')) ";
		}

		return sql;
	}

	/**
	 * 门急诊药房
	 * 
	 * @param startDate
	 *            :开始日期
	 * @param endDate
	 *            ：结束日期
	 * @param isDrug
	 *            ：药品种类
	 * @param supCode
	 *            ：供应商
	 * @param orgCode
	 *            ：科室
	 * @return
	 */
	public String getOpdSql(String startDate, String endDate, String isDrug,
			String supCode, String orgCode) {
		String sql = // " SELECT  A.ORDER_CODE, A.DISPENSE_QTY1 AS DOSAGE_QTY, B.SUP_ORDER_CODE "
		// //modify by wukai on 20170215
		" SELECT  A.ORDER_CODE, A.OWN_PRICE, A.DISPENSE_QTY1 AS DOSAGE_QTY, B.SUP_ORDER_CODE "
				+ " FROM OPD_ORDER A, IND_STOCK B "
				+ " WHERE     A.ORDER_CODE = B.ORDER_CODE "
				+ "      AND A.BATCH_SEQ1=B.BATCH_SEQ "
				+ "      AND A.EXEC_DEPT_CODE = B.ORG_CODE "
				+ "     AND (   A.EXEC_DEPT_CODE = '"
				+ orgCode
				+ "' "
				+ "          OR A.EXEC_DEPT_CODE = "
				+ "                (SELECT ORG_CODE "
				+ "                  FROM IND_ORG "
				+ "                  WHERE ATC_FLG = 'Y' AND ATC_ORG_CODE = '"
				+ orgCode + "')) ";
		if (isDrug.equals("E")) {
			sql += " AND A.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NULL "
					+ "           OR A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "           AND A.CTRLDRUGCLASS_CODE  IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		}
		sql += "       AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "','YYYYMMDDHH24MISS') "
				+ "                               AND TO_DATE ('"
				+ endDate
				+ "','YYYYMMDDHH24MISS') "
				+ "      AND A.CAT1_TYPE = 'PHA' "
				+ " UNION ALL  "
				// +
				// " SELECT  A.ORDER_CODE, A.DISPENSE_QTY2 AS DOSAGE_QTY, B.SUP_ORDER_CODE "
				// //modify by wukai on 20170215
				+ " SELECT  A.ORDER_CODE,  A.OWN_PRICE, A.DISPENSE_QTY2 AS DOSAGE_QTY, B.SUP_ORDER_CODE "
				+ " FROM OPD_ORDER A, IND_STOCK B "
				+ " WHERE     A.ORDER_CODE = B.ORDER_CODE "
				+ "      AND A.BATCH_SEQ2=B.BATCH_SEQ "
				+ "      AND A.EXEC_DEPT_CODE = B.ORG_CODE "
				+ "      AND (   A.EXEC_DEPT_CODE = '" + orgCode + "' "
				+ "           OR A.EXEC_DEPT_CODE = "
				+ "                 (SELECT ORG_CODE "
				+ "                    FROM IND_ORG "
				+ "                   WHERE ATC_FLG = 'Y' AND ATC_ORG_CODE = '"
				+ orgCode + "')) ";
		if (isDrug.equals("E")) {
			sql += " AND A.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NULL "
					+ "           OR A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "           AND A.CTRLDRUGCLASS_CODE  IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		}
		sql += "      AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "','YYYYMMDDHH24MISS') "
				+ "                                AND TO_DATE ('"
				+ endDate
				+ "','YYYYMMDDHH24MISS') "
				+ "      AND A.CAT1_TYPE = 'PHA' "
				+ "  UNION ALL "
				// +
				// "  SELECT  A.ORDER_CODE, A.DISPENSE_QTY3 AS DOSAGE_QTY, B.SUP_ORDER_CODE "
				// //modify by wukai on 20170215
				+ "  SELECT  A.ORDER_CODE, A.OWN_PRICE, A.DISPENSE_QTY3 AS DOSAGE_QTY, B.SUP_ORDER_CODE "
				+ "  FROM OPD_ORDER A, IND_STOCK B "
				+ "  WHERE     A.ORDER_CODE = B.ORDER_CODE "
				+ "      AND A.BATCH_SEQ3=B.BATCH_SEQ "
				+ "      AND A.EXEC_DEPT_CODE = B.ORG_CODE "
				+ "      AND (   A.EXEC_DEPT_CODE = '" + orgCode + "' "
				+ "           OR A.EXEC_DEPT_CODE = "
				+ "                 (SELECT ORG_CODE "
				+ "                    FROM IND_ORG "
				+ "                   WHERE ATC_FLG = 'Y' AND ATC_ORG_CODE = '"
				+ orgCode + "')) ";
		if (isDrug.equals("E")) {
			sql += " AND A.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NULL "
					+ "           OR A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "           AND A.CTRLDRUGCLASS_CODE  IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		}
		sql += "      AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('" + startDate
				+ "','YYYYMMDDHH24MISS') "
				+ "                                AND TO_DATE ('" + endDate
				+ "','YYYYMMDDHH24MISS') " + "      AND A.CAT1_TYPE = 'PHA' ";
		return sql;
	}

	public String getOpdRtnSql(String startDate, String endDate, String isDrug,
			String supCode, String orgCode, String isNew) {
		// FUX MODIFY 20141204
		String sql = " SELECT A.ORDER_CODE, A.OWN_PRICE, -1*  A.DOSAGE_QTY AS DOSAGE_QTY, B.SUP_ORDER_CODE "
				+ " FROM OPD_ORDER A, IND_STOCK B "
				+ " WHERE   "
				+ "     (   A.EXEC_DEPT_CODE = '"
				+ orgCode
				+ "' "
				+ "          OR A.EXEC_DEPT_CODE = "
				+ "                (SELECT ORG_CODE "
				+ "                  FROM IND_ORG "
				+ "                  WHERE ATC_FLG = 'Y' AND ATC_ORG_CODE = '"
				+ orgCode
				+ "')) "
				+ " AND A.PHA_RETN_CODE IS NOT NULL"
				+ "      AND A.CAT1_TYPE = 'PHA' "
				+ "      AND A.PHA_RETN_DATE BETWEEN TO_DATE ('"
				+ startDate
				+ "','YYYYMMDDHH24MISS') "
				+ "                              AND TO_DATE ('"
				+ endDate
				+ "','YYYYMMDDHH24MISS')"
				+ "   AND A.EXEC_DEPT_CODE = B.ORG_CODE"
				+ "   AND A.ORDER_CODE = B.ORDER_CODE "
				+ "   AND A.BATCH_SEQ1 = B.BATCH_SEQ ";
		// modify by wukai on 20170215 门诊退费数据 从OPD_ORDER_HISTORY表中查询
		// TODO: rrrr与新医疗卡上线有关系
		// String historyTable = "";
		// if("NEW".endsWith(isNew)) {
		// historyTable = "OPD_ORDER_HISTORY_NEW";
		// } else {
		// historyTable = "OPD_ORDER_HISTORY";
		// }
		// String sql =
		// " SELECT A.ORDER_CODE, A.OWN_PRICE,  -1 * A.DOSAGE_QTY AS DOSAGE_QTY, B.SUP_ORDER_CODE "
		// + "FROM BIL_OPB_RECP C, BIL_OPB_RECP D, " + historyTable +
		// " A, IND_STOCK B "
		// + " WHERE   "
		// + "  C.RESET_RECEIPT_NO IS NULL "
		// + " AND C.AR_AMT < 0 "
		// + " AND C.RECEIPT_NO = D.RESET_RECEIPT_NO "
		// + " AND D.RECEIPT_NO = A.RECEIPT_NO "
		// + " AND A.REXP_CODE IN ('010.01', '010.02') "
		// + " AND C.BILL_DATE BETWEEN TO_DATE ('"
		// + startDate
		// + "','YYYYMMDDHH24MISS') "
		// + "                              AND TO_DATE ('"
		// + endDate
		// + "','YYYYMMDDHH24MISS') "
		// + " AND A.EXEC_DEPT_CODE = B.ORG_CODE"
		// + " AND A.ORDER_CODE = B.ORDER_CODE "
		// + " AND A.BATCH_SEQ1 = B.BATCH_SEQ "
		// + " AND    (   A.EXEC_DEPT_CODE = '"
		// + orgCode
		// + "' "
		// + "          OR A.EXEC_DEPT_CODE = "
		// + "                (SELECT ORG_CODE "
		// + "                  FROM IND_ORG "
		// + "                  WHERE ATC_FLG = 'Y' AND ATC_ORG_CODE = '"
		// + orgCode
		// + "')) " ;
		//		
		if (isDrug.equals("E")) {
			sql += " AND A.CTRLDRUGCLASS_CODE = '03' ";
		} else if (isDrug.equals("Y")) {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NULL "
					+ "           OR A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		} else {
			sql += "      AND (   A.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "           AND A.CTRLDRUGCLASS_CODE  IN (SELECT CTRLDRUGCLASS_CODE "
					+ "                                             FROM SYS_CTRLDRUGCLASS "
					+ "                                            WHERE CTRL_FLG = 'Y')) ";
		}

		return sql;
	}

	public String getIndOddSql(String orgCode, String endDate, String isDrug) {

		String closeDate = getEndDateStr(endDate);

		String sql = "";
		// System.out.println("-------isDrug--------" + isDrug);
		if (isDrug.equals("E")) {// 精二
			sql = " SELECT "
					// + "A.ORDER_CODE," //modify by wukai on 20170215
					+ "A.ORDER_CODE, A.VERIFYIN_PRICE AS OWN_PRICE,"
					+ "A.ODD_QTY AS DOSAGE_QTY ," + "A.SUP_ORDER_CODE "
					+ "FROM IND_ODD A,PHA_BASE B " + "WHERE A.ORG_CODE='"
					+ orgCode + "' " + " " + "AND A.CLOSE_DATE='" + closeDate
					+ "' " + "AND A.ORDER_CODE=B.ORDER_CODE "
					+ "AND B.CTRLDRUGCLASS_CODE = '03' ";

		} else if (isDrug.equals("N")) {// 麻精

			sql = " SELECT "
					// + "A.ORDER_CODE," //modify by wukai on 20170215
					+ "A.ORDER_CODE, A.VERIFYIN_PRICE AS OWN_PRICE,"
					+ "A.ODD_QTY AS DOSAGE_QTY ,"
					+ "A.SUP_ORDER_CODE "
					+ "FROM IND_ODD A,PHA_BASE B "
					+ "WHERE A.ORG_CODE='"
					+ orgCode
					+ "' "
					+ " "
					+ "AND A.CLOSE_DATE='"
					+ closeDate
					+ "' "
					+ "AND A.ORDER_CODE=B.ORDER_CODE "
					+ "AND (B.CTRLDRUGCLASS_CODE IS NOT NULL "
					+ "AND B.CTRLDRUGCLASS_CODE IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG = 'Y') ) ";

		} else if (isDrug.equals("Y")) {// 普药
			sql = " SELECT "
					// + "A.ORDER_CODE," //modify by wukai on 20170215
					+ "A.ORDER_CODE, A.VERIFYIN_PRICE AS OWN_PRICE,"
					+ "A.ODD_QTY AS DOSAGE_QTY ,"
					+ "A.SUP_ORDER_CODE "
					+ "FROM IND_ODD A,PHA_BASE B "
					+ "WHERE A.ORG_CODE='"
					+ orgCode
					+ "' "
					+ " "
					+ "AND A.CLOSE_DATE='"
					+ closeDate
					+ "' "
					+ "AND A.ORDER_CODE=B.ORDER_CODE "
					+ "AND ( B.CTRLDRUGCLASS_CODE IS NULL "
					+ "OR B.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG = 'Y')) ";
		}

		return sql;
	}

	private String getEndDateStr(String endDate) {
		String year = endDate.substring(0, 4);
		String month = endDate.substring(4, 6);
		if (Integer.parseInt(month) == 1) {
			month = "12";
			year = String.valueOf(Integer.parseInt(year) - 1);
		} else {
			month = String.valueOf(Integer.parseInt(month) - 1);
		}
		if (month.length() < 2) {
			month = "0" + month;
		}
		String closeDate = year + month + endDate.substring(6, 8);
		return closeDate;
	}

	public TParm onSave(TParm parm, TConnection conn) {
		TParm result = new TParm();
		for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
			TParm rowParm = parm.getRow(i);

			result = this.update("onSaveIndOdd", rowParm, conn);
			if (result == null || result.getErrCode() < 0) {
				conn.rollback();
				return result;
			}

			result = update("onSaveIndAccount", rowParm, conn);
			if (result == null || result.getErrCode() < 0) {
				conn.rollback();
				return result;
			}
		}
		return result;
	}

	public TParm getSysDept(String orgCode) {
		String sql = "  SELECT DEPT_CODE, DEPT_CHN_DESC, DEPT_ABS_DESC, PY1, SEQ, "
				+ "		FINAL_FLG, REGION_CODE, DEPT_GRADE, CLASSIFY, OPD_FIT_FLG, "
				+ "  	EMG_FIT_FLG, IPD_FIT_FLG, HRM_FIT_FLG, STATISTICS_FLG, ACTIVE_FLG, "
				+ "  	OPT_USER, OPT_DATE, OPT_TERM, COST_CENTER_CODE "
				+ "  FROM SYS_DEPT " + "  WHERE  DEPT_CODE='" + orgCode + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm getIndOrg(String orgCode) {
		String sql = " SELECT ORG_CODE ,SUP_ORG_CODE " + " FROM IND_ORG "
				+ " WHERE SUP_ORG_CODE='" + orgCode + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm getIndOrgAccount() {
		String sql = " SELECT ORG_CODE,ORG_CHN_DESC "
				+ " FROM IND_ORG WHERE IS_ACCOUNT='Y' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm checkIndAccount(TParm parm) {
		String sql = "SELECT CLOSE_DATE FROM IND_ODD A WHERE A.CLOSE_DATE='"
				+ parm.getValue("CLOSE_DATE") + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm onDelete(TParm parm, TConnection conn) {

		TParm result = update("deleteIndOdd", parm, conn);
		if (result == null || result.getErrCode() < 0) {
			conn.rollback();
			return result;
		}

		result = update("deleteIndAccount", parm, conn);
		if (result == null || result.getErrCode() < 0) {
			conn.rollback();
			return result;
		}

		return result;

	}

	public TParm onQueryIndAccount(TParm parm) {
		String sql = " SELECT CLOSE_DATE,ORG_CODE,ORDER_CODE,LAST_ODD_QTY,OUT_QTY,TOTAL_OUT_QTY, "
				+ "        TOTAL_UNIT_CODE,VERIFYIN_PRICE,VERIFYIN_AMT,ACCOUNT_QTY,ACCOUNT_UNIT_CODE, "
				+ "        ODD_QTY,ODD_AMT,OPT_USER,OPT_DATE,OPT_TERM,REGION_CODE,SUP_CODE,"
				+ "        SUP_ORDER_CODE,CONTRACT_PRICE "
				+ " FROM  IND_ACCOUNT "
				+ " WHERE CLOSE_DATE='"
				+ parm.getValue("CLOSE_DATE") + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm onQueryIndOdd(TParm parm) {

		String endDate = parm.getValue("END_DATE");
		String closeDate = getEndDateStr(endDate);
		String sql = "  SELECT A.ODD_QTY,A.ORDER_CODE " + "	FROM IND_ODD A "
				+ "  WHERE A.ORG_CODE='" + parm.getValue("ORG_CODE") + "' AND "
				+ "     	  ORDER_CODE='" + parm.getValue("ORDER_CODE")
				+ "'  AND " + "   	  A.CLOSE_DATE='" + closeDate + "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm onQueryHisOrderCode(TParm parm) {
		String orderCode = parm.getValue("ORDER_CODE");
		String regionCode = parm.getValue("REGION_CODE");
		String sql = " SELECT A.HIS_ORDER_CODE,A.ORDER_CODE "
				+ " FROM SYS_FEE_SPC A " + " WHERE A.ORDER_CODE='" + orderCode
				+ "' " + " AND A.REGION_CODE='" + regionCode + "' ";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm onQuerySysSupplier(TParm parm) {
		String supCode = parm.getValue("SUP_CODE");
		String sql = " SELECT A.SUP_CODE,A.SELL_DEPT_CODE "
				+ " FROM SYS_SUPPLIER A " + " WHERE A.SUP_CODE='" + supCode
				+ "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}

	public TParm onQueryIndCodeMap(TParm parm) {
		String supCode = parm.getValue("SUP_CODE");
		String sql = " SELECT A.SUP_CODE,A.SUP_ORDER_CODE,A.ORDER_CODE,A.SUPPLY_UNIT_CODE,A.CONVERSION_RATIO, "
				+ " A.OPT_USER,A.OPT_DATE,A.OPT_TERM "
				+ " FROM IND_CODE_MAP A "
				+ " WHERE A.SUP_CODE='"
				+ supCode
				+ "' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;

	}

	public TParm onQueryIndCodeMapBy(TParm parm) {
		TParm result = new TParm();
		String supOrderCode = parm.getValue("SUP_ORDER_CODE");
		String supCode = parm.getValue("SUP_CODE");
		String sql = " SELECT A.SUP_CODE,A.SUP_ORDER_CODE,A.ORDER_CODE,A.SUPPLY_UNIT_CODE,A.CONVERSION_RATIO, "
				+ " A.OPT_USER,A.OPT_DATE,A.OPT_TERM "
				+ " FROM IND_CODE_MAP A "
				+ " WHERE A.SUP_CODE='"
				+ supCode
				+ "'  AND A.SUP_ORDER_CODE='" + supOrderCode + "' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getCount() > 0) {
			result = result.getRow(0);
		}
		return result;

	}

}