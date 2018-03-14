package jdo.hrm;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

public class HRMCheckFeeTool
        extends TJDOTool {

    /**
     * 实例
     */
    public static HRMCheckFeeTool instanceObject;

    /**
     * 得到实例
     * 
     * @return HRMChargeTool
     */
    public static HRMCheckFeeTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new HRMCheckFeeTool();
        }
        return instanceObject;
    }

    /**
     * 检查费用报表 汇总查询
     * 
     * @param parm
     * @return
     */
    public TParm onQueryMaster(TParm parm) {
        String companyCode = parm.getValue("COMPANY_CODE"); // 团体代码
        String contractCode = parm.getValue("CONTRACT_CODE"); // 合同代码
        String packageCode = parm.getValue("PACKAGE_CODE"); // 套餐代码
        String MR_NO = parm.getValue("MR_NO"); // 病案号
        String reportDate = parm.getValue("REPORT_DATE"); // 报到日期
        String conditionSql = "";
        if (!StringUtil.isNullString(companyCode)) {
            conditionSql += " AND  A.COMPANY_CODE='" + companyCode + "' ";
        }
        if (!StringUtil.isNullString(contractCode)) {
            conditionSql += " AND  A.CONTRACT_CODE='" + contractCode + "' ";
        }
        if (!StringUtil.isNullString(packageCode)) {
            conditionSql += " AND  A.PACKAGE_CODE='" + packageCode + "' ";
        }
        if (!StringUtil.isNullString(MR_NO)) {
            conditionSql += " AND  A.MR_NO='" + MR_NO + "' ";
        }
        if (!StringUtil.isNullString(reportDate)) {
            conditionSql +=
                    " AND A.REAL_CHK_DATE BETWEEN TO_DATE( '" + reportDate + "', 'yyyy/mm/dd') "
                            + "AND TO_DATE( '" + reportDate
                            + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ";// modify by wanglong 20130108
        }
        if (!StringUtil.isNullString(parm.getValue("ROLE_TYPE"))) {
			conditionSql += " AND ROLE_TYPE IN ('" + parm.getValue("ROLE_TYPE")
					+ "') ";// add by wangb 2016/6/29
        }
        String conditionSql2 = " AND  A.CONTRACT_CODE='" + contractCode + "' ";
        String sql = // modify by wanglong 20130224
                "WITH D AS (SELECT A.SEQ_NO,A.STAFF_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,"
                        + "        A.MR_NO,A.PAT_NAME,A.SEX_CODE,A.IDNO,A.TEL,A.COVER_FLG,A.REAL_CHK_DATE REPORT_DATE,"
                        + "        NVL(B.BILL_FLG,'N') BILL_FLG,CASE WHEN C.RECEIPT_NO IS NULL THEN 'N' "
                        + "        WHEN C.RECEIPT_NO IS NOT NULL THEN 'Y' ELSE '' END CHARGE_FLG,NVL(SUM(B.OWN_AMT),0) OWN_AMT,"
                        + "        A.DISCNT DISCOUNT_RATE,"
                        + "        NVL(SUM(B.AR_AMT),0) AR_AMT,A.ROLE_TYPE "
                        + "  FROM HRM_CONTRACTD A, HRM_ORDER B, HRM_BILL C, HRM_PATADM D "// modify by wanglong 20130313 增加缴费状态（查HRM_BILL表）
                        + " WHERE A.MR_NO = D.MR_NO(+) "
                        + "   AND A.CONTRACT_CODE = D.CONTRACT_CODE(+) "
                        + "   AND D.CASE_NO = B.CASE_NO "
                        + "   AND B.BILL_NO = C.BILL_NO(+) "
                        + conditionSql
                        + "GROUP BY A.SEQ_NO,A.STAFF_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,"
                        + "         A.MR_NO,A.PAT_NAME,A.SEX_CODE,A.IDNO,A.TEL,A.COVER_FLG,A.REAL_CHK_DATE,A.DISCNT,C.RECEIPT_NO,B.BILL_FLG,B.DISCOUNT_RATE,A.ROLE_TYPE),"
                        + "E AS (SELECT A.*,CASE WHEN NVL(A.EXEC_DR_CODE, 'x') = 'x' THEN 'N' ELSE 'Y' END AS FINISH_FLG "
                        + "        FROM HRM_ORDER A"
                        + "       WHERE A.DEPT_ATTRIBUTE = '04' "
                        + conditionSql2
                        + "         AND A.SETMAIN_FLG = 'Y') "
                        + "SELECT DISTINCT D.*, NVL(E.FINISH_FLG, 'N') FINISH_FLG "
                        + "  FROM D,E "
                        + " WHERE D.CONTRACT_CODE = E.CONTRACT_CODE(+) "
                        + "   AND D.MR_NO = E.MR_NO(+) "
                        + "ORDER BY D.REPORT_DATE DESC NULLS LAST,D.COMPANY_CODE,D.CONTRACT_CODE,D.PACKAGE_CODE,D.MR_NO";
        return new TParm(TJDODBTool.getInstance().select(sql));
    }

    /**
     * 检查费用报表 明细查询
     * 
     * @param parm
     * @return
     */
    public TParm onQueryDetail(TParm parm) {
        String companyCode = parm.getValue("COMPANY_CODE");// 团体代码
        String contractCode = parm.getValue("CONTRACT_CODE"); // 合同代码
        String packageCode = parm.getValue("PACKAGE_CODE"); // 套餐代码
        String MR_NO = parm.getValue("MR_NO"); // 病案号
        String reportDate = parm.getValue("REPORT_DATE");// 报到日期
        String conditionSql = "";
        if (!StringUtil.isNullString(companyCode)) {
            conditionSql += " AND  A.COMPANY_CODE='" + companyCode + "' ";
        }
        if (!StringUtil.isNullString(contractCode)) {
            conditionSql += " AND  A.CONTRACT_CODE='" + contractCode + "' ";
        }
        if (!StringUtil.isNullString(packageCode)) {
            conditionSql += " AND  A.PACKAGE_CODE='" + packageCode + "' ";
        }
        if (!StringUtil.isNullString(MR_NO)) {
            conditionSql += " AND  A.MR_NO='" + MR_NO + "' ";
        }
        if (!StringUtil.isNullString(reportDate)) {
            conditionSql +=
                    " AND A.REAL_CHK_DATE BETWEEN TO_DATE( '" + reportDate + "', 'yyyy/mm/dd') "
                            + "AND TO_DATE( '" + reportDate
                            + " 23:59:59', 'yyyy/mm/dd hh24:mi:ss') ";// modify by wanglong 20130108
        }
        if (!StringUtil.isNullString(parm.getValue("ROLE_TYPE"))) {
			conditionSql += " AND ROLE_TYPE IN ('" + parm.getValue("ROLE_TYPE")
					+ "') ";// add by wangb 2016/6/29
        }
        String sql = // modify by wanglong 20130424
                "SELECT DISTINCT A.SEQ_NO,A.STAFF_NO,A.COMPANY_CODE,A.CONTRACT_CODE,A.PACKAGE_CODE,A.MR_NO,A.PAT_NAME,A.SEX_CODE,A.IDNO,A.REPORT_DATE,"
                        + "A.ORDERSET_CODE ORDER_CODE,B.ORDER_DESC,A.DISPENSE_QTY,A.OWN_PRICE/A.DISPENSE_QTY OWN_PRICE,A.OWN_PRICE OWN_AMT,"
                        + "A.DISCOUNT_RATE,A.OWN_AMT AR_AMT,A.BILL_FLG,A.ROLE_TYPE "
                        + "FROM ( "
                        + "      SELECT B.SEQ_NO,B.STAFF_NO,B.COMPANY_CODE,A.CONTRACT_CODE,B.PACKAGE_CODE,A.MR_NO,A.CASE_NO,"
                        + "             B.PAT_NAME,B.SEX_CODE,B.IDNO,B.REAL_CHK_DATE REPORT_DATE,A.ORDERSET_CODE,A.DISCOUNT_RATE,"
                        + "             SUM(A.DISPENSE_QTY) DISPENSE_QTY,SUM(A.OWN_PRICE) OWN_PRICE,SUM(A.OWN_AMT) OWN_AMT,A.BILL_FLG,A.ROLE_TYPE "
                        + "        FROM ( "
                        + "              SELECT A.MR_NO,A.CONTRACT_CODE,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISCOUNT_RATE ELSE 0 END) DISCOUNT_RATE,"
                        + "                     SUM(CASE WHEN B.ORDER_CODE = B.ORDERSET_CODE THEN B.DISPENSE_QTY ELSE 0 END) DISPENSE_QTY,"
                        + "                     SUM(B.OWN_AMT) OWN_PRICE,SUM(B.AR_AMT) OWN_AMT,B.BILL_FLG,B.ORDERSET_GROUP_NO,A.ROLE_TYPE "
                        + "                FROM HRM_CONTRACTD A, HRM_ORDER B, HRM_PATADM C "
                        + "               WHERE A.MR_NO = C.MR_NO "
                        + "                 AND A.CONTRACT_CODE = C.CONTRACT_CODE "
                        + "                 AND C.CASE_NO = B.CASE_NO "
                        + conditionSql
                        + "            GROUP BY A.MR_NO,A.CONTRACT_CODE,B.CASE_NO,A.PAT_NAME,B.ORDERSET_CODE,"
                        + "                       B.BILL_FLG,B.ORDERSET_GROUP_NO,A.ROLE_TYPE) A, HRM_CONTRACTD B "
                        + "        WHERE A.MR_NO = B.MR_NO "
                        + "          AND A.CONTRACT_CODE = B.CONTRACT_CODE "
                        + "    GROUP BY B.SEQ_NO,B.STAFF_NO,B.COMPANY_CODE,A.CONTRACT_CODE,B.PACKAGE_CODE,A.MR_NO,A.CASE_NO,B.PAT_NAME,"
                        + "             B.SEX_CODE,B.IDNO,B.REAL_CHK_DATE,A.ORDERSET_CODE,A.DISCOUNT_RATE,A.OWN_PRICE,A.BILL_FLG,A.ROLE_TYPE "
                        + "      ) A, HRM_ORDER B "
                        + " WHERE A.ORDERSET_CODE = B.ORDER_CODE "
                        + "   AND A.CASE_NO = B.CASE_NO "
                        + "ORDER BY A.REPORT_DATE DESC NULLS LAST, A.COMPANY_CODE, A.CONTRACT_CODE, A.PACKAGE_CODE";
        return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    
}
