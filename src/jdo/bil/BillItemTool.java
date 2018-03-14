package jdo.bil;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;


/**
 * <p>Title: 医疗项目统计工具类
 *
 * <p>Description: 医疗项目统计工具类
 *
 * <p>Copyright: Copyright (c) 2013
 *
 * <p>Company: bulecore</p>
 *
 * @author wanglong 20130613
 * @version 1.0
 */
public class BillItemTool
        extends TJDOTool {

    /**
     * 实例
     */
    public static BillItemTool instanceObject;

    /**
     * 得到实例
     * 
     * @return HRMChargeTool
     */
    public static BillItemTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new BillItemTool();
        }
        return instanceObject;
    }

    /**
     * 查询门诊医疗统计数据（根据科室）
     * 
     * @param parm
     * @return
     */
    public String onQueryOPDByDept(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "WITH AA AS (SELECT A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DOSAGE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "         SUM(A.OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT"
                        + "    FROM OPD_ORDER A"
                        + "   WHERE A.ADM_TYPE = 'O'"
                        + "     AND A.BILL_FLG = 'Y'"
                        + "     AND A.ORDERSET_CODE IS NOT NULL"
                        + "         # "// //////
                        + "     AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "                          AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "  GROUP BY A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE),"
                        // add by wanglong 20130905
                        + "BB AS (SELECT AA.CASE_NO,AA.RX_NO,AA.ORDERSET_GROUP_NO,AA.ORDERSET_CODE,AA.DISPENSE_QTY,AA.OWN_AMT,AA.AR_AMT,"
                        + "              CASE WHEN AA.DISCOUNT_RATE=0 THEN TRUNC(AA.AR_AMT/AA.OWN_AMT,2) ELSE AA.DISCOUNT_RATE END DISCOUNT_RATE,"
                        + "              B.EXEC_DEPT_CODE,B.REXP_CODE,B.ORDER_DESC,B.DOSAGE_UNIT,AA.OWN_AMT/AA.DISPENSE_QTY OWN_PRICE,B.NS_NOTE"
                        + "         FROM AA, OPD_ORDER B"
                        + "        WHERE AA.CASE_NO = B.CASE_NO"
                        + "          AND AA.RX_NO = B.RX_NO"
                        + "          AND AA.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO"
                        + "          AND AA.ORDERSET_CODE = B.ORDER_CODE),"
                        + "CC AS (SELECT BB.EXEC_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_DESC,BB.DISCOUNT_RATE,"
                        + "              BB.DOSAGE_UNIT,SUM(DISPENSE_QTY) DISPENSE_QTY,BB.OWN_PRICE,SUM(BB.OWN_AMT) OWN_AMT,"
                        + "              SUM(BB.AR_AMT) AR_AMT,BB.NS_NOTE"
                        + "         FROM BB"
                        + "     GROUP BY BB.EXEC_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_DESC,BB.DISCOUNT_RATE,BB.DOSAGE_UNIT,BB.OWN_PRICE,BB.NS_NOTE"
                        + "        UNION "
                        + "       SELECT B.EXEC_DEPT_CODE,B.REXP_CODE,B.ORDERSET_CODE,B.ORDER_DESC,B.DISCOUNT_RATE,B.DOSAGE_UNIT,"
                        + "              SUM(B.DISPENSE_QTY) DISPENSE_QTY,B.OWN_PRICE,SUM(B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) AR_AMT,B.NS_NOTE"
                        + "         FROM (SELECT A.EXEC_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE ORDERSET_CODE,A.ORDER_DESC,"
                        + "                      SUM(A.AR_AMT)/SUM(A.DOSAGE_QTY*A.OWN_PRICE) DISCOUNT_RATE,A.DOSAGE_UNIT,SUM(A.DOSAGE_QTY) DISPENSE_QTY, "
                        + "                      A.OWN_PRICE,SUM(A.DOSAGE_QTY*A.OWN_PRICE) OWN_AMT,SUM(A.AR_AMT) AR_AMT,A.NS_NOTE"
                        + "                 FROM OPD_ORDER A"
                        + "                WHERE A.ADM_TYPE = 'O'"
                        + "                  AND A.BILL_FLG = 'Y'"
                        + "                  AND A.ORDERSET_CODE IS NULL"
                        + "                  # "// //////
                        + "                  AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "                                       AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "             GROUP BY A.CASE_NO,A.RX_NO,A.SEQ_NO,A.EXEC_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE, A.ORDER_DESC,"
                        + "                      A.DISCOUNT_RATE, A.DOSAGE_UNIT, A.OWN_PRICE, A.NS_NOTE) B "
                        + "GROUP BY B.EXEC_DEPT_CODE,B.REXP_CODE,B.ORDERSET_CODE,B.ORDER_DESC,B.DISCOUNT_RATE,B.DOSAGE_UNIT,B.OWN_PRICE,B.NS_NOTE)"
                        + "SELECT CC.EXEC_DEPT_CODE DEPT_CODE,CC.REXP_CODE,B.CHN_DESC,CC.ORDERSET_CODE ORDER_CODE,RTRIM(CC.ORDER_DESC||' '||S.SPECIFICATION) ORDER_DESC,"
                        + "       CC.DISCOUNT_RATE,CC.DOSAGE_UNIT UNIT_CODE,CC.DISPENSE_QTY,CC.OWN_PRICE,CC.OWN_AMT,CC.AR_AMT,CC.NS_NOTE"
                        + "  FROM CC, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE CC.REXP_CODE = B.ID             "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE'       "
                        + "   AND CC.ORDERSET_CODE = S.ORDER_CODE " + whereSql
                        + " ORDER BY CC.EXEC_DEPT_CODE,CC.REXP_CODE,CC.ORDERSET_CODE,CC.DISCOUNT_RATE,CC.DISPENSE_QTY";
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
//        System.out.println("------------------------查询门诊医疗统计数据（根据科室）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询门诊医疗统计数据（根据病案号）
     * 
     * @param parm
     * @return
     */
    public String onQueryOPDByMr(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        // String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        // String caseNo = getLastCaseNo("O", mrNo);// 最后一次就诊号
        String caseNo = parm.getValue("CASE_NO"); // 就诊号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "WITH AA AS (SELECT A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DOSAGE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "         SUM(A.OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT"
                        + "    FROM OPD_ORDER A"
                        + "   WHERE A.ADM_TYPE = 'O'"
                        + "     AND A.BILL_FLG = 'Y'"
                        + "     AND A.ORDERSET_CODE IS NOT NULL"
                        + "     AND A.CASE_NO = '#'"// //////
//                        + "     AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                          AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "  GROUP BY A.CASE_NO,A.RX_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE),"
                        // add by wanglong 20130905
                        + "BB AS (SELECT AA.CASE_NO,AA.RX_NO,AA.ORDERSET_GROUP_NO,AA.ORDERSET_CODE,AA.DISPENSE_QTY,AA.OWN_AMT,AA.AR_AMT,"
                        + "              CASE WHEN AA.DISCOUNT_RATE=0 THEN TRUNC(AA.AR_AMT/AA.OWN_AMT,2) ELSE AA.DISCOUNT_RATE END DISCOUNT_RATE,"
                        + "              B.EXEC_DEPT_CODE,B.REXP_CODE,B.ORDER_DESC,B.DOSAGE_UNIT,AA.OWN_AMT/AA.DISPENSE_QTY OWN_PRICE,B.NS_NOTE"
                        + "         FROM AA, OPD_ORDER B"
                        + "        WHERE AA.CASE_NO = B.CASE_NO"
                        + "          AND AA.RX_NO = B.RX_NO"
                        + "          AND AA.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO"
                        + "          AND AA.ORDERSET_CODE = B.ORDER_CODE),"
                        + "CC AS (SELECT BB.EXEC_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_DESC,BB.DISCOUNT_RATE,"
                        + "              BB.DOSAGE_UNIT,SUM(DISPENSE_QTY) DISPENSE_QTY,BB.OWN_PRICE,SUM(BB.OWN_AMT) OWN_AMT,"
                        + "              SUM(BB.AR_AMT) AR_AMT,BB.NS_NOTE"
                        + "         FROM BB"
                        + "     GROUP BY BB.EXEC_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_DESC,BB.DISCOUNT_RATE,BB.DOSAGE_UNIT,BB.OWN_PRICE,BB.NS_NOTE"
                        + "        UNION "
                        + "       SELECT B.EXEC_DEPT_CODE,B.REXP_CODE,B.ORDERSET_CODE,B.ORDER_DESC,B.DISCOUNT_RATE,B.DOSAGE_UNIT,"
                        + "              SUM(B.DISPENSE_QTY) DISPENSE_QTY,B.OWN_PRICE,SUM(B.OWN_AMT) OWN_AMT,SUM(B.AR_AMT) AR_AMT,B.NS_NOTE"
                        + "         FROM (SELECT A.EXEC_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE ORDERSET_CODE,A.ORDER_DESC,"
                        + "                      SUM(A.AR_AMT)/SUM(A.DOSAGE_QTY*A.OWN_PRICE) DISCOUNT_RATE,A.DOSAGE_UNIT,SUM(A.DOSAGE_QTY) DISPENSE_QTY, "
                        + "                      A.OWN_PRICE,SUM(A.DOSAGE_QTY*A.OWN_PRICE) OWN_AMT,SUM(A.AR_AMT) AR_AMT,A.NS_NOTE"
                        + "                 FROM OPD_ORDER A"
                        + "                WHERE A.ADM_TYPE = 'O'"
                        + "                  AND A.BILL_FLG = 'Y'"
                        + "                  AND A.ORDERSET_CODE IS NULL"
                        + "          AND A.CASE_NO = '#'"// //////
//                        + "                  AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                                       AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "             GROUP BY A.CASE_NO,A.RX_NO,A.SEQ_NO,A.EXEC_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE, A.ORDER_DESC,"
                        + "                      A.DISCOUNT_RATE, A.DOSAGE_UNIT, A.OWN_PRICE, A.NS_NOTE) B "
                        + "GROUP BY B.EXEC_DEPT_CODE,B.REXP_CODE,B.ORDERSET_CODE,B.ORDER_DESC,B.DISCOUNT_RATE,B.DOSAGE_UNIT,B.OWN_PRICE,B.NS_NOTE)"
                        + "SELECT CC.EXEC_DEPT_CODE DEPT_CODE,CC.REXP_CODE,B.CHN_DESC,CC.ORDERSET_CODE ORDER_CODE,RTRIM(CC.ORDER_DESC||' '||S.SPECIFICATION) ORDER_DESC,"
                        + "       CC.DISCOUNT_RATE,CC.DOSAGE_UNIT UNIT_CODE,CC.DISPENSE_QTY,CC.OWN_PRICE,CC.OWN_AMT,CC.AR_AMT,CC.NS_NOTE"
                        + "  FROM CC, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE CC.REXP_CODE = B.ID             "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE'       "
                        + "   AND CC.ORDERSET_CODE = S.ORDER_CODE " + whereSql
                        + " ORDER BY CC.EXEC_DEPT_CODE,CC.REXP_CODE,CC.ORDERSET_CODE,CC.DISCOUNT_RATE,CC.DISPENSE_QTY";
  
        sql = sql.replaceFirst("#", caseNo);
        sql = sql.replaceFirst("#", caseNo);
//        System.out.println("------------------------查询门诊医疗统计数据（根据病案号）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询健检医疗统计数据（根据科室）
     * 
     * @param parm
     * @return
     */
    public String onQueryHRMByDept(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,UNIT_CODE,SUM(DISPENSE_QTY) DISPENSE_QTY, OWN_PRICE, SUM(OWN_AMT) OWN_AMT,SUM(AR_AMT) AR_AMT,NS_NOTE"
                        + "  FROM (SELECT B.EXEC_DEPT_CODE DEPT_CODE, B.REXP_CODE, C.CHN_DESC, A.ORDERSET_CODE ORDER_CODE, RTRIM(S.ORDER_DESC||' '||S.SPECIFICATION) ORDER_DESC, "
                        + "               A.DISCOUNT_RATE, B.DISPENSE_UNIT UNIT_CODE, SUM(A.DISPENSE_QTY) DISPENSE_QTY, A.OWN_PRICE, "
                        + "               SUM(A.DISPENSE_QTY * A.OWN_PRICE) OWN_AMT, SUM(A.AR_AMT) AR_AMT, B.NS_NOTE"
                        + "          FROM (SELECT CASE_NO, SEQ_NO, ORDERSET_GROUP_NO, ORDERSET_CODE, AR_AMT / OWN_AMT DISCOUNT_RATE"
                        + "                       , DISPENSE_QTY, OWN_AMT / DISPENSE_QTY OWN_PRICE, OWN_AMT, AR_AMT"
                        + "                  FROM (SELECT A.CASE_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
                        + "                               SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.SEQ_NO ELSE 0 END) SEQ_NO,"
                        + "                               SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                               SUM(A.OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT"
                        + "                          FROM HRM_ORDER A, HRM_PATADM B"
                        + "                         WHERE A.CASE_NO = B.CASE_NO"
                        + "                           AND B.REPORT_DATE IS NOT NULL"
                        + "                           # "// //////
                        + "                           AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "                                                AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "                        GROUP BY A.CASE_NO, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE)) A, HRM_ORDER B, SYS_DICTIONARY C, SYS_FEE S"
                        + "         WHERE A.CASE_NO = B.CASE_NO"
                        + "           AND A.ORDERSET_CODE = B.ORDER_CODE"
                        + "           AND A.SEQ_NO = B.SEQ_NO"
                        + "           AND B.REXP_CODE = C.ID"
                        + "           AND C.GROUP_ID = 'SYS_CHARGE'"
                        + "           AND A.ORDERSET_CODE = S.ORDER_CODE "
                        + whereSql
                        + "        GROUP BY B.EXEC_DEPT_CODE,B.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE,S.ORDER_DESC,S.SPECIFICATION,A.DISCOUNT_RATE,"
                        + "                 A.OWN_PRICE,B.REXP_CODE,B.DISPENSE_UNIT,B.NS_NOTE) "
                        + "GROUP BY DEPT_CODE, REXP_CODE, CHN_DESC, ORDER_CODE, ORDER_DESC,DISCOUNT_RATE, UNIT_CODE, OWN_PRICE, NS_NOTE "
                        + "ORDER BY DEPT_CODE, REXP_CODE, ORDER_CODE, DISCOUNT_RATE, DISPENSE_QTY";
        
//                "SELECT DEPT_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,UNIT_CODE,"
//                        + "SUM(DISPENSE_QTY) DISPENSE_QTY,OWN_PRICE,SUM(OWN_AMT) OWN_AMT,SUM(AR_AMT) AR_AMT,NS_NOTE "
//                        + " FROM (SELECT B.EXEC_DEPT_CODE DEPT_CODE,B.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE ORDER_CODE,S.ORDER_DESC,A.DISCOUNT_RATE,B.DISPENSE_UNIT UNIT_CODE,"
//                        + "              SUM(A.DISPENSE_QTY) DISPENSE_QTY,A.OWN_PRICE,SUM(A.DISPENSE_QTY*A.OWN_PRICE) OWN_AMT,SUM(A.AR_AMT) AR_AMT,B.NS_NOTE "
//                        + "         FROM (SELECT CASE_NO,SEQ_NO,ORDERSET_GROUP_NO,ORDERSET_CODE,DISCOUNT_RATE,DISPENSE_QTY,"
//                        + "                      OWN_AMT/DISPENSE_QTY OWN_PRICE,OWN_AMT,AR_AMT "
//                        + "                 FROM (SELECT A.CASE_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
//                        + "                              SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.SEQ_NO ELSE 0 END) SEQ_NO,"
//                        + "                              SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
//                        + "                              SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
//                        + "                              SUM(A.OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT "
//                        + "                         FROM HRM_ORDER A, HRM_PATADM B "
//                        + "                        WHERE A.CASE_NO = B.CASE_NO "
//                        + "                          AND B.REPORT_DATE IS NOT NULL "
//                        + "                              # "// //////
//                        + "                          AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                                                AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                     GROUP BY A.CASE_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE) "
//                        + "              ) A, HRM_ORDER B,SYS_DICTIONARY C,SYS_FEE S "
//                        + "         WHERE A.CASE_NO = B.CASE_NO "
//                        + "           AND A.ORDERSET_CODE = B.ORDER_CODE "
//                        + "           AND A.SEQ_NO = B.SEQ_NO "
//                        + "           AND B.REXP_CODE = C.ID "
//                        + "           AND C.GROUP_ID = 'SYS_CHARGE' "
//                        + "           AND A.ORDERSET_CODE = S.ORDER_CODE "
//                        + whereSql
//                        + "      GROUP BY B.EXEC_DEPT_CODE,B.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE,S.ORDER_DESC,A.DISCOUNT_RATE,A.OWN_PRICE,B.REXP_CODE,B.DISPENSE_UNIT,B.NS_NOTE) "
//                        + "GROUP BY DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,UNIT_CODE,OWN_PRICE,NS_NOTE "
//                        + "ORDER BY DEPT_CODE,REXP_CODE,ORDER_CODE,DISCOUNT_RATE,DISPENSE_QTY";
        
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
//        System.out.println("------------------------查询健检医疗统计数据（根据科室）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询健检医疗统计数据（根据病案号）
     * 
     * @param parm
     * @return
     */
    public String onQueryHRMByMr(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        // String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        // String caseNo = getLastCaseNo("H", mrNo);// 最后一次就诊号
        String caseNo = parm.getValue("CASE_NO"); // 就诊号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,UNIT_CODE,SUM(DISPENSE_QTY) DISPENSE_QTY, OWN_PRICE, SUM(OWN_AMT) OWN_AMT,SUM(AR_AMT) AR_AMT,NS_NOTE"
                        + "  FROM (SELECT B.EXEC_DEPT_CODE DEPT_CODE, B.REXP_CODE, C.CHN_DESC, A.ORDERSET_CODE ORDER_CODE, RTRIM(S.ORDER_DESC||' '||S.SPECIFICATION) ORDER_DESC, "
                        + "               A.DISCOUNT_RATE, B.DISPENSE_UNIT UNIT_CODE, SUM(A.DISPENSE_QTY) DISPENSE_QTY, A.OWN_PRICE, "
                        + "               SUM(A.DISPENSE_QTY * A.OWN_PRICE) OWN_AMT, SUM(A.AR_AMT) AR_AMT, B.NS_NOTE"
                        + "          FROM (SELECT CASE_NO, SEQ_NO, ORDERSET_GROUP_NO, ORDERSET_CODE, AR_AMT / OWN_AMT DISCOUNT_RATE"
                        + "                       , DISPENSE_QTY, OWN_AMT / DISPENSE_QTY OWN_PRICE, OWN_AMT, AR_AMT"
                        + "                  FROM (SELECT A.CASE_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
                        + "                               SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.SEQ_NO ELSE 0 END) SEQ_NO,"
                        + "                               SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                               SUM(A.OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT"
                        + "                          FROM HRM_ORDER A, HRM_PATADM B"
                        + "                         WHERE A.CASE_NO = B.CASE_NO"
                        + "                           AND B.REPORT_DATE IS NOT NULL"
                        + "                           AND A.CASE_NO = '#'"// //////
//                        + "                           AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                                                AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "                        GROUP BY A.CASE_NO, A.ORDERSET_GROUP_NO, A.ORDERSET_CODE)) A, HRM_ORDER B, SYS_DICTIONARY C, SYS_FEE S"
                        + "         WHERE A.CASE_NO = B.CASE_NO"
                        + "           AND A.ORDERSET_CODE = B.ORDER_CODE"
                        + "           AND A.SEQ_NO = B.SEQ_NO"
                        + "           AND B.REXP_CODE = C.ID"
                        + "           AND C.GROUP_ID = 'SYS_CHARGE'"
                        + "           AND A.ORDERSET_CODE = S.ORDER_CODE "
                        + whereSql
                        + "        GROUP BY B.EXEC_DEPT_CODE,B.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE,S.ORDER_DESC,S.SPECIFICATION,A.DISCOUNT_RATE,"
                        + "                 A.OWN_PRICE,B.REXP_CODE,B.DISPENSE_UNIT,B.NS_NOTE) "
                        + "GROUP BY DEPT_CODE, REXP_CODE, CHN_DESC, ORDER_CODE, ORDER_DESC,DISCOUNT_RATE, UNIT_CODE, OWN_PRICE, NS_NOTE "
                        + "ORDER BY DEPT_CODE, REXP_CODE, ORDER_CODE, DISCOUNT_RATE, DISPENSE_QTY";
//                "SELECT DEPT_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,UNIT_CODE,"
//                        + "SUM(DISPENSE_QTY) DISPENSE_QTY,OWN_PRICE,SUM(OWN_AMT) OWN_AMT,SUM(AR_AMT) AR_AMT,NS_NOTE "
//                        + " FROM (SELECT B.EXEC_DEPT_CODE DEPT_CODE,B.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE ORDER_CODE,S.ORDER_DESC,A.DISCOUNT_RATE,B.DISPENSE_UNIT UNIT_CODE,"
//                        + "              SUM(A.DISPENSE_QTY) DISPENSE_QTY,A.OWN_PRICE,SUM(A.DISPENSE_QTY*A.OWN_PRICE) OWN_AMT,SUM(A.AR_AMT) AR_AMT,B.NS_NOTE "
//                        + "         FROM (SELECT CASE_NO,SEQ_NO,ORDERSET_GROUP_NO,ORDERSET_CODE,DISCOUNT_RATE,DISPENSE_QTY,"
//                        + "                      OWN_AMT/DISPENSE_QTY OWN_PRICE,OWN_AMT,AR_AMT "
//                        + "                 FROM (SELECT A.CASE_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE,"
//                        + "                              SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.SEQ_NO ELSE 0 END) SEQ_NO,"
//                        + "                              SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
//                        + "                              SUM(CASE WHEN A.ORDER_CODE=A.ORDERSET_CODE THEN A.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
//                        + "                              SUM(A.OWN_AMT) OWN_AMT,SUM(A.AR_AMT) AR_AMT "
//                        + "                         FROM HRM_ORDER A, HRM_PATADM B "
//                        + "                        WHERE A.CASE_NO = B.CASE_NO "
//                        + "                          AND B.REPORT_DATE IS NOT NULL "
//                        + "                          AND A.CASE_NO = '#'"// //////
//                        + "                          AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                                                AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                     GROUP BY A.CASE_NO,A.ORDERSET_GROUP_NO,A.ORDERSET_CODE) "
//                        + "              ) A, HRM_ORDER B,SYS_DICTIONARY C,SYS_FEE S "
//                        + "         WHERE A.CASE_NO = B.CASE_NO "
//                        + "           AND A.ORDERSET_CODE = B.ORDER_CODE "
//                        + "           AND A.SEQ_NO = B.SEQ_NO "
//                        + "           AND B.REXP_CODE = C.ID "
//                        + "           AND C.GROUP_ID = 'SYS_CHARGE' "
//                        + "           AND A.ORDERSET_CODE = S.ORDER_CODE "
//                        + whereSql
//                        + "      GROUP BY B.EXEC_DEPT_CODE,B.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE,S.ORDER_DESC,A.DISCOUNT_RATE,A.OWN_PRICE,B.REXP_CODE,B.DISPENSE_UNIT,B.NS_NOTE) "
//                        + "GROUP BY DEPT_CODE,REXP_CODE,CHN_DESC,ORDER_CODE,ORDER_DESC,DISCOUNT_RATE,UNIT_CODE,OWN_PRICE,NS_NOTE "
//                        + "ORDER BY DEPT_CODE,REXP_CODE,ORDER_CODE,DISCOUNT_RATE,DISPENSE_QTY";
        sql = sql.replaceFirst("#", caseNo);
//        System.out.println("------------------------查询健检医疗统计数据（根据病案号）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询住院医疗统计数据（根据科室）
     * 
     * @param parm
     * @return
     */
    public String onQueryODIByDept(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "WITH AA AS (SELECT A.CASE_NO,A.ORDER_NO,A.ORDERSET_GROUP_NO,A.CASE_NO_SEQ,A.ORDERSET_CODE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.OWN_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DOSAGE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "         SUM(A.OWN_AMT) OWN_AMT,SUM(A.TOT_AMT) AR_AMT"
                        + "    FROM IBS_ORDD A"
                        + "   WHERE A.ORDERSET_CODE IS NOT NULL"
                        + "         # "// //////
                        + "     AND A.BILL_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "                         AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + " GROUP BY A.CASE_NO,A.ORDER_NO,A.ORDERSET_GROUP_NO,A.CASE_NO_SEQ,A.ORDERSET_CODE),"
                        // add by wanglong 20130905
                        + "BB AS (SELECT AA.CASE_NO,AA.ORDER_NO,AA.ORDERSET_GROUP_NO,AA.CASE_NO_SEQ,AA.ORDERSET_CODE,AA.DISPENSE_QTY,AA.OWN_AMT,AA.AR_AMT,"
                        + "              CASE WHEN AA.DISCOUNT_RATE=0 THEN TRUNC(AA.AR_AMT/AA.OWN_AMT,2) ELSE AA.DISCOUNT_RATE END DISCOUNT_RATE,"
                        + "              B.EXE_DEPT_CODE,B.REXP_CODE,B.ORDER_CHN_DESC,B.DOSAGE_UNIT,AA.OWN_AMT/AA.DISPENSE_QTY OWN_PRICE,B.ORDER_SEQ"
                        + "         FROM AA, IBS_ORDD B"
                        + "        WHERE AA.CASE_NO = B.CASE_NO"
                        + "          AND AA.ORDER_NO = B.ORDER_NO"
                        + "          AND AA.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO"
                        + "          AND AA.CASE_NO_SEQ = B.CASE_NO_SEQ"
                        + "          AND AA.ORDERSET_CODE = B.ORDER_CODE"
                        + "         AND AA.DISPENSE_QTY <> 0),"
                        + "CC AS (SELECT BB.EXE_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_CHN_DESC,BB.DISCOUNT_RATE,BB.DOSAGE_UNIT,"
                        + "              SUM(BB.DISPENSE_QTY) DISPENSE_QTY,BB.OWN_PRICE,SUM(BB.OWN_AMT) OWN_AMT,SUM(BB.AR_AMT) AR_AMT,C.NS_NOTE"
                        + "         FROM BB, ODI_ORDER C"
                        + "        WHERE BB.CASE_NO = C.CASE_NO"
                        + "          AND BB.ORDER_NO = C.ORDER_NO"
                        + "          AND BB.ORDER_SEQ = C.ORDER_SEQ"
                        + "     GROUP BY BB.EXE_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_CHN_DESC,BB.DISCOUNT_RATE,BB.DOSAGE_UNIT,BB.OWN_PRICE,C.NS_NOTE"
                        + "       UNION "
                        + "      SELECT A.EXE_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE ORDERSET_CODE,A.ORDER_CHN_DESC,"
                        + "             CASE WHEN A.OWN_RATE = 0 THEN 1 ELSE A.OWN_RATE END DISCOUNT_RATE,A.DOSAGE_UNIT,"
                        + "             SUM(A.DOSAGE_QTY) DISPENSE_QTY,A.OWN_PRICE,SUM(A.OWN_AMT) OWN_AMT,SUM(A.TOT_AMT) AR_AMT,B.NS_NOTE"
                        + "        FROM IBS_ORDD A, ODI_ORDER B"
                        + "       WHERE A.CASE_NO = B.CASE_NO"
                        + "         AND A.ORDER_NO = B.ORDER_NO"
                        + "         AND A.SEQ_NO = B.ORDER_SEQ"
                        + "         AND A.ORDERSET_CODE IS NULL"
                        + "             # "// //////
                        + "         AND A.BILL_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "                             AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "    GROUP BY A.EXE_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE,A.ORDER_CHN_DESC,A.OWN_RATE,A.DOSAGE_UNIT,A.OWN_PRICE,B.NS_NOTE)"
                        + "SELECT CC.EXE_DEPT_CODE DEPT_CODE,CC.REXP_CODE,B.CHN_DESC,CC.ORDERSET_CODE ORDER_CODE,RTRIM(CC.ORDER_CHN_DESC||' '||S.SPECIFICATION) ORDER_DESC,"
                        + "       CC.DISCOUNT_RATE,CC.DOSAGE_UNIT UNIT_CODE,CC.DISPENSE_QTY,CC.OWN_PRICE,CC.OWN_AMT,CC.AR_AMT,CC.NS_NOTE"
                        + "  FROM CC, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE CC.REXP_CODE = B.ID             "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE'       "
                        + "   AND CC.ORDERSET_CODE = S.ORDER_CODE " + whereSql
                        + " ORDER BY CC.EXE_DEPT_CODE,CC.REXP_CODE,CC.ORDERSET_CODE,CC.DISCOUNT_RATE,CC.DISPENSE_QTY";
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXE_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXE_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
//        System.out.println("------------------------查询住院医疗统计数据（根据科室）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询住院医疗统计数据（根据病案号）
     * 
     * @param parm
     * @return
     */
    public String onQueryODIByMr(TParm parm) {
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        // String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        // String caseNo = getLastCaseNo("I", mrNo);// 最后一次就诊号
        String caseNo = parm.getValue("CASE_NO"); // 就诊号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "WITH AA AS (SELECT A.CASE_NO,A.ORDER_NO,A.ORDERSET_GROUP_NO,A.CASE_NO_SEQ,A.ORDERSET_CODE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.OWN_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "         SUM(CASE WHEN A.ORDER_CODE = A.ORDERSET_CODE THEN A.DOSAGE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "         SUM(A.OWN_AMT) OWN_AMT,SUM(A.TOT_AMT) AR_AMT"
                        + "    FROM IBS_ORDD A"
                        + "   WHERE A.ORDERSET_CODE IS NOT NULL"
                        + "     AND A.CASE_NO = '#'"// //////
//                        + "     AND A.BILL_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                         AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + " GROUP BY A.CASE_NO,A.ORDER_NO,A.ORDERSET_GROUP_NO,A.CASE_NO_SEQ,A.ORDERSET_CODE),"
                        // add by wanglong 20130905
                        + "BB AS (SELECT AA.CASE_NO,AA.ORDER_NO,AA.ORDERSET_GROUP_NO,AA.CASE_NO_SEQ,AA.ORDERSET_CODE,AA.DISPENSE_QTY,AA.OWN_AMT,AA.AR_AMT,"
                        + "              CASE WHEN AA.DISCOUNT_RATE=0 THEN TRUNC(AA.AR_AMT/AA.OWN_AMT,2) ELSE AA.DISCOUNT_RATE END DISCOUNT_RATE,"
                        + "              B.EXE_DEPT_CODE,B.REXP_CODE,B.ORDER_CHN_DESC,B.DOSAGE_UNIT,AA.OWN_AMT/AA.DISPENSE_QTY OWN_PRICE,B.ORDER_SEQ"
                        + "         FROM AA, IBS_ORDD B"
                        + "        WHERE AA.CASE_NO = B.CASE_NO"
                        + "          AND AA.ORDER_NO = B.ORDER_NO"
                        + "          AND AA.ORDERSET_GROUP_NO = B.ORDERSET_GROUP_NO"
                        + "          AND AA.CASE_NO_SEQ = B.CASE_NO_SEQ"
                        + "          AND AA.ORDERSET_CODE = B.ORDER_CODE"
                        + "         AND AA.DISPENSE_QTY <> 0),"
                        + "CC AS (SELECT BB.EXE_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_CHN_DESC,BB.DISCOUNT_RATE,BB.DOSAGE_UNIT,"
                        + "              SUM(BB.DISPENSE_QTY) DISPENSE_QTY,BB.OWN_PRICE,SUM(BB.OWN_AMT) OWN_AMT,SUM(BB.AR_AMT) AR_AMT,C.NS_NOTE"
                        + "         FROM BB, ODI_ORDER C"
                        + "        WHERE BB.CASE_NO = C.CASE_NO"
                        + "          AND BB.ORDER_NO = C.ORDER_NO"
                        + "          AND BB.ORDER_SEQ = C.ORDER_SEQ"
                        + "     GROUP BY BB.EXE_DEPT_CODE,BB.REXP_CODE,BB.ORDERSET_CODE,BB.ORDER_CHN_DESC,BB.DISCOUNT_RATE,BB.DOSAGE_UNIT,BB.OWN_PRICE,C.NS_NOTE"
                        + "       UNION "
                        + "      SELECT A.EXE_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE ORDERSET_CODE,A.ORDER_CHN_DESC,"
                        + "             CASE WHEN A.OWN_RATE = 0 THEN 1 ELSE A.OWN_RATE END DISCOUNT_RATE,A.DOSAGE_UNIT,"
                        + "             SUM(A.DOSAGE_QTY) DISPENSE_QTY,A.OWN_PRICE,SUM(A.OWN_AMT) OWN_AMT,SUM(A.TOT_AMT) AR_AMT,B.NS_NOTE"
                        + "        FROM IBS_ORDD A, ODI_ORDER B"
                        + "       WHERE A.CASE_NO = B.CASE_NO"
                        + "         AND A.ORDER_NO = B.ORDER_NO"
                        + "         AND A.SEQ_NO = B.ORDER_SEQ"
                        + "         AND A.ORDERSET_CODE IS NULL"
                        + "         AND A.CASE_NO = '#'"// //////
//                        + "         AND A.BILL_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
//                        + "                             AND TO_DATE('#', 'YYYYMMDDHH24MISS')"// //////
                        + "    GROUP BY A.EXE_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE,A.ORDER_CHN_DESC,A.OWN_RATE,A.DOSAGE_UNIT,A.OWN_PRICE,B.NS_NOTE)"
                        + "SELECT CC.EXE_DEPT_CODE DEPT_CODE,CC.REXP_CODE,B.CHN_DESC,CC.ORDERSET_CODE ORDER_CODE,RTRIM(CC.ORDER_CHN_DESC||' '||S.SPECIFICATION) ORDER_DESC,"
                        + "       CC.DISCOUNT_RATE,CC.DOSAGE_UNIT UNIT_CODE,CC.DISPENSE_QTY,CC.OWN_PRICE,CC.OWN_AMT,CC.AR_AMT,CC.NS_NOTE"
                        + "  FROM CC, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE CC.REXP_CODE = B.ID             "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE'       "
                        + "   AND CC.ORDERSET_CODE = S.ORDER_CODE " + whereSql
                        + " ORDER BY CC.EXE_DEPT_CODE,CC.REXP_CODE,CC.ORDERSET_CODE,CC.DISCOUNT_RATE,CC.DISPENSE_QTY";
        sql = sql.replaceFirst("#", caseNo);
        sql = sql.replaceFirst("#", caseNo);
//        System.out.println("------------------------查询住院医疗统计数据（根据病案号）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }
    
    /**
     * 查询门诊医疗统计数据[汇总]（根据科室）
     * 
     * @param parm
     * @return
     */
    public String onQueryTotOPDByDept(TParm parm) {//add by wanglong 20130926
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT A.EXEC_DEPT_CODE DEPT_CODE, A.REXP_CODE, B.CHN_DESC, A.ORDER_CODE,"
                        + "       RTRIM(A.ORDER_DESC || ' ' || A.SPECIFICATION) ORDER_DESC, A.DOSAGE_UNIT UNIT_CODE,"
                        + "       SUM(A.DOSAGE_QTY) DISPENSE_QTY "
                        + "  FROM OPD_ORDER A, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE A.ADM_TYPE = 'O' "
                        + "   AND A.BILL_FLG = 'Y' "
                        + "   AND (A.ORDERSET_CODE IS NULL OR A.ORDER_CODE = A.ORDERSET_CODE) "
                        + "   #  "
                        + "   AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
                        + "                        AND TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
                        + "   AND A.REXP_CODE = B.ID "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE' "
                        + "   AND A.ORDER_CODE = S.ORDER_CODE " + whereSql
                        + "GROUP BY A.EXEC_DEPT_CODE,A.REXP_CODE,B.CHN_DESC,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DOSAGE_UNIT "
                        + "ORDER BY A.EXEC_DEPT_CODE, A.REXP_CODE, A.ORDER_CODE";// A.DOSAGE_QTY
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
//        System.out.println("------------------------查询门诊医疗统计数据[汇总]（根据科室）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询门诊医疗统计数据[汇总]（根据病案号）
     * 
     * @param parm
     * @return
     */
    public String onQueryTotOPDByMr(TParm parm) {//add by wanglong 20130926
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        // String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        // String caseNo = getLastCaseNo("O", mrNo);// 最后一次就诊号
        String caseNo = parm.getValue("CASE_NO"); // 就诊号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT A.EXEC_DEPT_CODE DEPT_CODE, A.REXP_CODE, B.CHN_DESC, A.ORDER_CODE,"
                        + "       RTRIM(A.ORDER_DESC || ' ' || A.SPECIFICATION) ORDER_DESC, A.DOSAGE_UNIT UNIT_CODE,"
                        + "       SUM(A.DOSAGE_QTY) DISPENSE_QTY "
                        + "  FROM OPD_ORDER A, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE A.ADM_TYPE = 'O' "
                        + "   AND A.BILL_FLG = 'Y' "
                        + "   AND (A.ORDERSET_CODE IS NULL OR A.ORDER_CODE = A.ORDERSET_CODE) "
                        + "   AND A.CASE_NO = '#' "
//                        + "   AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
//                        + "                        AND TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
                        + "   AND A.REXP_CODE = B.ID "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE' "
                        + "   AND A.ORDER_CODE = S.ORDER_CODE " + whereSql
                        + "GROUP BY A.EXEC_DEPT_CODE,A.REXP_CODE,B.CHN_DESC,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DOSAGE_UNIT "
                        + "ORDER BY A.EXEC_DEPT_CODE, A.REXP_CODE, A.ORDER_CODE";// A.DOSAGE_QTY
        sql = sql.replaceFirst("#", caseNo);
//        System.out.println("------------------------查询门诊医疗统计数据[汇总]（根据病案号）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询健检医疗统计数据[汇总]（根据科室）
     * 
     * @param parm
     * @return
     */
    public String onQueryTotHRMByDept(TParm parm) {//add by wanglong 20130926
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT A.EXEC_DEPT_CODE DEPT_CODE, A.REXP_CODE, C.CHN_DESC, A.ORDERSET_CODE ORDER_CODE,"
                        + "       RTRIM(A.ORDER_DESC || ' ' || A.SPECIFICATION) ORDER_DESC, A.DISPENSE_UNIT UNIT_CODE,"
                        + "       SUM(A.DISPENSE_QTY) DISPENSE_QTY "
                        + "  FROM HRM_ORDER A, HRM_PATADM B, SYS_DICTIONARY C, SYS_FEE S "
                        + " WHERE A.SETMAIN_FLG = 'Y' "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "   #  "
                        + "   AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
                        + "                        AND TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
                        + "   AND B.REPORT_DATE IS NOT NULL "
                        + "   AND A.REXP_CODE = C.ID "
                        + "   AND C.GROUP_ID = 'SYS_CHARGE' "
                        + "   AND A.ORDER_CODE = S.ORDER_CODE " + whereSql
                        + "GROUP BY A.EXEC_DEPT_CODE,A.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT "
                        + "ORDER BY A.EXEC_DEPT_CODE,A.REXP_CODE,A.ORDERSET_CODE";// DISPENSE_QTY
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXEC_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
//        System.out.println("------------------------查询健检医疗统计数据[汇总]（根据科室）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询健检医疗统计数据[汇总]（根据病案号）
     * 
     * @param parm
     * @return
     */
    public String onQueryTotHRMByMr(TParm parm) {//add by wanglong 20130926
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        // String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        // String caseNo = getLastCaseNo("H", mrNo);// 最后一次就诊号
        String caseNo = parm.getValue("CASE_NO"); // 就诊号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT A.EXEC_DEPT_CODE DEPT_CODE, A.REXP_CODE, C.CHN_DESC, A.ORDERSET_CODE ORDER_CODE,"
                        + "       RTRIM(A.ORDER_DESC || ' ' || A.SPECIFICATION) ORDER_DESC, A.DISPENSE_UNIT UNIT_CODE,"
                        + "       SUM(A.DISPENSE_QTY) DISPENSE_QTY "
                        + "  FROM HRM_ORDER A, HRM_PATADM B, SYS_DICTIONARY C, SYS_FEE S "
                        + " WHERE A.SETMAIN_FLG = 'Y' "
                        + "   AND A.CASE_NO = B.CASE_NO "
//                        + "   AND A.ORDER_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
//                        + "                        AND TO_DATE('#', 'YYYYMMDDHH24MISS') "// //////
                        + "   AND B.CASE_NO = '#' "
                        + "   AND B.REPORT_DATE IS NOT NULL "
                        + "   AND A.REXP_CODE = C.ID "
                        + "   AND C.GROUP_ID = 'SYS_CHARGE' "
                        + "   AND A.ORDER_CODE = S.ORDER_CODE " + whereSql
                        + "GROUP BY A.EXEC_DEPT_CODE,A.REXP_CODE,C.CHN_DESC,A.ORDERSET_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT "
                        + "ORDER BY A.EXEC_DEPT_CODE,A.REXP_CODE,A.ORDERSET_CODE";// DISPENSE_QTY
        sql = sql.replaceFirst("#", caseNo);
//        System.out.println("------------------------查询健检医疗统计数据[汇总]（根据病案号）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询住院医疗统计数据[汇总]（根据科室）
     * 
     * @param parm
     * @return
     */
    public String onQueryTotODIByDept(TParm parm) {//add by wanglong 20130926
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT A.EXE_DEPT_CODE DEPT_CODE, A.REXP_CODE, B.CHN_DESC, A.ORDER_CODE,"
                        + "       RTRIM(A.ORDER_CHN_DESC || ' ' || S.SPECIFICATION) ORDER_DESC, A.DOSAGE_UNIT UNIT_CODE,"
                        + "       SUM(A.DOSAGE_QTY) DISPENSE_QTY "
                        + "  FROM IBS_ORDD A, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE (A.ORDERSET_CODE IS NULL  OR A.ORDER_CODE = A.ORDERSET_CODE) "
                        + "   #  "
                        + "   AND A.BILL_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS') "
                        + "                       AND TO_DATE('#', 'YYYYMMDDHH24MISS') "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE' "
                        + "   AND A.REXP_CODE = B.ID "
                        + "   AND A.ORDER_CODE = S.ORDER_CODE " + whereSql
                        + "GROUP BY A.EXE_DEPT_CODE,A.REXP_CODE,B.CHN_DESC,A.ORDER_CODE,A.ORDER_CHN_DESC,S.SPECIFICATION,A.DOSAGE_UNIT "
                        + "ORDER BY A.EXE_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE ";//  A.DOSAGE_QTY
        if (!StringUtil.isNullString(deptCode)) {
            sql = sql.replaceFirst("#", " AND A.EXE_DEPT_CODE = '" + deptCode + "' ");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        sql = sql.replaceFirst("#", startDate);
        sql = sql.replaceFirst("#", endDate);
//        System.out.println("------------------------查询住院医疗统计数据[汇总]（根据科室）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

    /**
     * 查询住院医疗统计数据[汇总]（根据病案号）
     * 
     * @param parm
     * @return
     */
    public String onQueryTotODIByMr(TParm parm) {//add by wanglong 20130926
        String startDate =
                StringTool.getString(parm.getTimestamp("START_DATE"), "yyyyMMdd") + "000000";
        String endDate = StringTool.getString(parm.getTimestamp("END_DATE"), "yyyyMMdd") + "235959";
        // String deptCode = parm.getValue("DEPT_CODE"); // 科室代码
        // String mrNo = parm.getValue("MR_NO"); // 病案号
        // String caseNo = getLastCaseNo("I", mrNo);// 最后一次就诊号
        String caseNo = parm.getValue("CASE_NO"); // 就诊号
        String transHospCode = parm.getValue("TRANS_HOSP_CODE");// 转诊院所代码
        String whereSql = "";
        if (!StringUtil.isNullString(transHospCode)) {
            whereSql =
                    " AND S.TRANS_OUT_FLG = 'Y'  AND S.TRANS_HOSP_CODE = '" + transHospCode + "' ";
        }
        String sql =
                "SELECT A.EXE_DEPT_CODE DEPT_CODE, A.REXP_CODE, B.CHN_DESC, A.ORDER_CODE,"
                        + "       RTRIM(A.ORDER_CHN_DESC || ' ' || S.SPECIFICATION) ORDER_DESC, A.DOSAGE_UNIT UNIT_CODE,"
                        + "       SUM(A.DOSAGE_QTY) DISPENSE_QTY "
                        + "  FROM IBS_ORDD A, SYS_DICTIONARY B, SYS_FEE S "
                        + " WHERE (A.ORDERSET_CODE IS NULL  OR A.ORDER_CODE = A.ORDERSET_CODE) "
                        + "   AND A.CASE_NO = '#' "
//                        + "   AND A.BILL_DATE BETWEEN TO_DATE('#', 'YYYYMMDDHH24MISS') "
//                        + "                       AND TO_DATE('#', 'YYYYMMDDHH24MISS') "
                        + "   AND B.GROUP_ID = 'SYS_CHARGE' "
                        + "   AND A.REXP_CODE = B.ID "
                        + "   AND A.ORDER_CODE = S.ORDER_CODE " + whereSql
                        + "GROUP BY A.EXE_DEPT_CODE,A.REXP_CODE,B.CHN_DESC,A.ORDER_CODE,A.ORDER_CHN_DESC,S.SPECIFICATION,A.DOSAGE_UNIT "
                        + "ORDER BY A.EXE_DEPT_CODE,A.REXP_CODE,A.ORDER_CODE ";//  A.DOSAGE_QTY
        sql = sql.replaceFirst("#", caseNo);
//        System.out.println("------------------------查询住院医疗统计数据[汇总]（根据病案号）sql------------------" + sql);
//        return new TParm(TJDODBTool.getInstance().select(sql));
        return sql;
    }

}
