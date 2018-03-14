package jdo.pha;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.dongyang.data.TParm;
import com.javahis.util.StringUtil;

/**
 * <p>Title: 药品销售报表 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2008.09.26
 * @version 1.0
 */
public class PhaMedSaleStaTool
    extends TJDOTool {

    /**
     * 实例
     */
    public static PhaMedSaleStaTool instanceObject;

    /**
     * 得到实例
     * @return OrderTool
     */
    public static PhaMedSaleStaTool getInstance() {
        if (instanceObject == null)
            instanceObject = new PhaMedSaleStaTool();
        return instanceObject;
    }

    public PhaMedSaleStaTool() {

        //加载Module文件
        this.setModuleName("pha\\PhaMedSaleStaModule.x");

        onInit();

    }

    /**
     * 获得‘门急诊药房分类销售报表’的主数据
     * @return TParm
     */
    public TParm getQueryDate(TParm parm, String type) {

        String various=(String) parm.getData("VARIOUS");

        TParm result = new TParm();
        //未审核查询
        if (type.equals("01")) {
            if ("D".equals(various))
                result = query("queryNOTExamine_D", parm);
            if ("M".equals(various))
//                result = query("queryNOTExamine_M", parm);
                result = queryNOTExamine_M(parm);// wanglong modify 20141204
        } //未配药查询
        else if (type.equals("02")) {
            if ("D".equals(various))
                result = query("queryNOTDosage_D", parm);
            if ("M".equals(various))
//                result = query("queryNOTDosage_M", parm);
                result = queryNOTDosage_M(parm);// wanglong modify 20141204
        } //未发药查询
        else if (type.equals("03")) {
            if ("D".equals(various))
                result = query("queryNOTDispense_D", parm);
            if ("M".equals(various))
//                result = query("queryNOTDispense_M", parm);
                result = queryNOTDispense_M(parm);// wanglong modify 20141204
        } //已发药查询
        else if (type.equals("04")) {
            if ("D".equals(various))
                result = query("queryDispenseed_D", parm);
            if ("M".equals(various))
//                result = query("queryDispenseed_M", parm);
                result = queryDispenseed_M(parm);// wanglong modify 20141204
        } //已退药查询
        else if (type.equals("05")) {
            if ("D".equals(various))
                result = query("queryReturned_D", parm);
            if ("M".equals(various))
//                result = query("queryReturned_M", parm);
                result = queryReturned_M(parm);// wanglong modify 20141204
        }
        else if (type.equals("07")) {//已配药
            if ("D".equals(various))
                result = query("queryDosaged_D", parm);
            if ("M".equals(various))
//                result = query("queryDosaged_M", parm);
                result = queryDosaged_M(parm);// wanglong modify 20141204
        }
        else{
            if ("D".equals(various))
                result = query("queryBill_D", parm);
            if ("M".equals(various))
//                result = query("queryBill_M", parm);
                result = queryBill_M(parm);// wanglong modify 20141204
        }

        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }

        return result;
    }

    /**
     * 未审核（汇总）
     */
    public TParm queryNOTExamine_M(TParm parm) {// wanglong add 20141204
        String sql =
                "WITH IA AS (SELECT * FROM (SELECT B.ORDER_CODE, B.CONTRACT_PRICE / A.DOSAGE_QTY LAST_VERIFY_PRICE, "
                        + "                        ROW_NUMBER() OVER (PARTITION BY B.ORDER_CODE ORDER BY B.OPT_DATE DESC) RN "
                        + "                   FROM PHA_TRANSUNIT A, IND_AGENT B "
                        + "                  WHERE A.ORDER_CODE = B.ORDER_CODE) "
                        + "           WHERE RN = 1) "
                        + "SELECT C.REGION_CHN_ABN AS REGION_CHN_DESC, OO.ORDER_CODE,  "
                        + "       OO.ORDER_DESC || CASE WHEN TRIM(OO.GOODS_DESC) IS NOT NULL OR TRIM(OO.GOODS_DESC) <> '' "
                        + "                        THEN  '(' || OO.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC, "
                        + "       OO.SPECIFICATION, OO.DISPENSE_UNIT, "
                        + "       SUM(OO.DISPENSE_QTY) AS DISPENSE_QTY, SUM(OO.OWN_AMT) AS OWN_AMT, "
                        + "       SUM(IA.LAST_VERIFY_PRICE * OO.DOSAGE_QTY) AS CONTRACT_PRICE "
                        + "  FROM OPD_ORDER OO, SYS_PATINFO SP, PHA_BASE PB, SYS_REGION C, IA "
                        + " WHERE OO.REGION_CODE = C.REGION_CODE "
                        + "   AND OO.ORDER_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   AND OO.ORDER_CODE = IA.ORDER_CODE "
                        + "   AND PB.ORDER_CODE(+) = OO.ORDER_CODE "
                        + "   AND OO.MR_NO = SP.MR_NO "
                        + "   AND OO.PHA_CHECK_CODE IS NULL  @  @  @  @  @  @  @  @  @  "
                        + "GROUP BY C.REGION_CHN_ABN, OO.ORDER_CODE, OO.ORDER_DESC, OO.GOODS_DESC, OO.SPECIFICATION, OO.DISPENSE_UNIT "
                        + "ORDER BY C.REGION_CHN_ABN, OO.ORDER_DESC";
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("START_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 开始时间
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("END_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 结束时间
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {// 院区
            sql =
                    sql.replaceFirst("@", " AND OO.REGION_CODE = '" + parm.getValue("REGION_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MR_NO"))) {// 病案号
            sql = sql.replaceFirst("@", " AND OO.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {// 执行科室
            sql =
                    sql.replaceFirst("@",
                                     " AND OO.EXEC_DEPT_CODE = '" + parm.getValue("EXEC_DEPT_CODE")
                                             + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CODE"))) {// 医嘱
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CODE = '" + parm.getValue("ORDER_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT_CODE"))) {// 科室
            sql =
                    sql.replaceFirst("@", " AND OO.DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DR_CODE"))) {// 医生
            sql = sql.replaceFirst("@", " AND OO.DR_CODE = '" + parm.getValue("DR_CODE") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("CTRLDRUGCLASS_CODE"))) {// 麻精
            sql = sql.replaceFirst("@", " AND IA.CTRLDRUGCLASS_CODE IN ('01','02') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE1"))) {// 药品种类
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE= '" + parm.getValue("VALUE1")
                            + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE2"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "') ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE3"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "','" + parm.getValue("VALUE3")
                            + "') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE"))) {// 抗生素登记
            sql =
                    sql.replaceFirst("@",
                                     " AND PB.ANTIBIOTIC_CODE = '"
                                             + parm.getValue("ANTIBIOTIC_CODE") + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE1"))) {// 医生
            sql = sql.replaceFirst("@", " AND PB.ANTIBIOTIC_CODE IS NOT NULL ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // System.out.println("------------------queryNOTExamine_M--------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 未配药（汇总）
     */
    public TParm queryNOTDosage_M(TParm parm) {// wanglong add 20141204
        String sql =
                "WITH IA AS (SELECT * FROM (SELECT B.ORDER_CODE, B.CONTRACT_PRICE / A.DOSAGE_QTY LAST_VERIFY_PRICE, "
                        + "                        ROW_NUMBER() OVER (PARTITION BY B.ORDER_CODE ORDER BY B.OPT_DATE DESC) RN "
                        + "                   FROM PHA_TRANSUNIT A, IND_AGENT B "
                        + "                  WHERE A.ORDER_CODE = B.ORDER_CODE) "
                        + "           WHERE RN = 1) "
                        + "SELECT C.REGION_CHN_ABN AS REGION_CHN_DESC, OO.ORDER_CODE,  "
                        + "       OO.ORDER_DESC || CASE WHEN TRIM(OO.GOODS_DESC) IS NOT NULL OR TRIM(OO.GOODS_DESC) <> '' "
                        + "                        THEN  '(' || OO.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC, "
                        + "       OO.SPECIFICATION, OO.DISPENSE_UNIT, "
                        + "       SUM(OO.DISPENSE_QTY) AS DISPENSE_QTY, SUM(OO.OWN_AMT) AS OWN_AMT, "
                        + "       SUM(IA.LAST_VERIFY_PRICE * OO.DOSAGE_QTY) AS CONTRACT_PRICE "
                        + "  FROM OPD_ORDER OO, SYS_PATINFO SP, PHA_BASE PB, SYS_REGION C, IA "
                        + " WHERE OO.REGION_CODE = C.REGION_CODE "
                        + "   AND OO.ORDER_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   AND OO.ORDER_CODE = IA.ORDER_CODE "
                        + "   AND PB.ORDER_CODE(+) = OO.ORDER_CODE "
                        + "   AND OO.MR_NO = SP.MR_NO "
                        + "   AND OO.PHA_DOSAGE_CODE IS NULL  @  @  @  @  @  @  @  @  @  "
                        + "GROUP BY C.REGION_CHN_ABN, OO.ORDER_CODE, OO.ORDER_DESC, OO.GOODS_DESC, OO.SPECIFICATION, OO.DISPENSE_UNIT "
                        + "ORDER BY C.REGION_CHN_ABN, OO.ORDER_DESC";
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("START_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 开始时间
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("END_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 结束时间
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {// 院区
            sql =
                    sql.replaceFirst("@", " AND OO.REGION_CODE = '" + parm.getValue("REGION_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MR_NO"))) {// 病案号
            sql = sql.replaceFirst("@", " AND OO.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {// 执行科室
            sql =
                    sql.replaceFirst("@",
                                     " AND OO.EXEC_DEPT_CODE = '" + parm.getValue("EXEC_DEPT_CODE")
                                             + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CODE"))) {// 医嘱
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CODE = '" + parm.getValue("ORDER_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT_CODE"))) {// 科室
            sql =
                    sql.replaceFirst("@", " AND OO.DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DR_CODE"))) {// 医生
            sql = sql.replaceFirst("@", " AND OO.DR_CODE = '" + parm.getValue("DR_CODE") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("CTRLDRUGCLASS_CODE"))) {// 麻精
            sql = sql.replaceFirst("@", " AND IA.CTRLDRUGCLASS_CODE IN ('01','02') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE1"))) {// 药品种类
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE= '" + parm.getValue("VALUE1")
                            + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE2"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "') ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE3"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "','" + parm.getValue("VALUE3")
                            + "') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE"))) {// 抗生素登记
            sql =
                    sql.replaceFirst("@",
                                     " AND PB.ANTIBIOTIC_CODE = '"
                                             + parm.getValue("ANTIBIOTIC_CODE") + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE1"))) {// 医生
            sql = sql.replaceFirst("@", " AND PB.ANTIBIOTIC_CODE IS NOT NULL ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // System.out.println("------------------queryNOTDosage_M--------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 未发药（汇总）
     */
    public TParm queryNOTDispense_M(TParm parm) {// wanglong add 20141204
        String sql =
                "WITH IA AS (SELECT * FROM (SELECT B.ORDER_CODE, B.CONTRACT_PRICE / A.DOSAGE_QTY LAST_VERIFY_PRICE, "
                        + "                        ROW_NUMBER() OVER (PARTITION BY B.ORDER_CODE ORDER BY B.OPT_DATE DESC) RN "
                        + "                   FROM PHA_TRANSUNIT A, IND_AGENT B "
                        + "                  WHERE A.ORDER_CODE = B.ORDER_CODE) "
                        + "           WHERE RN = 1) "
                        + "SELECT C.REGION_CHN_ABN AS REGION_CHN_DESC, OO.ORDER_CODE,  "
                        + "       OO.ORDER_DESC || CASE WHEN TRIM(OO.GOODS_DESC) IS NOT NULL OR TRIM(OO.GOODS_DESC) <> '' "
                        + "                        THEN  '(' || OO.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC, "
                        + "       OO.SPECIFICATION, OO.DISPENSE_UNIT, "
                        + "       SUM(OO.DISPENSE_QTY) AS DISPENSE_QTY, SUM(OO.OWN_AMT) AS OWN_AMT, "
                        + "       SUM(IA.LAST_VERIFY_PRICE * OO.DOSAGE_QTY) AS CONTRACT_PRICE "
                        + "  FROM OPD_ORDER OO, SYS_PATINFO SP, PHA_BASE PB, SYS_REGION C, IA "
                        + " WHERE OO.REGION_CODE = C.REGION_CODE "
                        + "   AND PHA_DISPENSE_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   AND OO.ORDER_CODE = IA.ORDER_CODE "
                        + "   AND PB.ORDER_CODE(+) = OO.ORDER_CODE "
                        + "   AND OO.MR_NO = SP.MR_NO "
                        + "   AND OO.PHA_DISPENSE_CODE IS NULL  @  @  @  @  @  @  @  @  @  "
                        + "GROUP BY C.REGION_CHN_ABN, OO.ORDER_CODE, OO.ORDER_DESC, OO.GOODS_DESC, OO.SPECIFICATION, OO.DISPENSE_UNIT "
                        + "ORDER BY C.REGION_CHN_ABN, OO.ORDER_DESC";
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("START_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 开始时间
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("END_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 结束时间
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {// 院区
            sql =
                    sql.replaceFirst("@", " AND OO.REGION_CODE = '" + parm.getValue("REGION_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MR_NO"))) {// 病案号
            sql = sql.replaceFirst("@", " AND OO.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {// 执行科室
            sql =
                    sql.replaceFirst("@",
                                     " AND OO.EXEC_DEPT_CODE = '" + parm.getValue("EXEC_DEPT_CODE")
                                             + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CODE"))) {// 医嘱
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CODE = '" + parm.getValue("ORDER_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT_CODE"))) {// 科室
            sql =
                    sql.replaceFirst("@", " AND OO.DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DR_CODE"))) {// 医生
            sql = sql.replaceFirst("@", " AND OO.DR_CODE = '" + parm.getValue("DR_CODE") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("CTRLDRUGCLASS_CODE"))) {// 麻精
            sql = sql.replaceFirst("@", " AND IA.CTRLDRUGCLASS_CODE IN ('01','02') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE1"))) {// 药品种类
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE= '" + parm.getValue("VALUE1")
                            + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE2"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "') ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE3"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "','" + parm.getValue("VALUE3")
                            + "') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE"))) {// 抗生素登记
            sql =
                    sql.replaceFirst("@",
                                     " AND PB.ANTIBIOTIC_CODE = '"
                                             + parm.getValue("ANTIBIOTIC_CODE") + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE1"))) {// 医生
            sql = sql.replaceFirst("@", " AND PB.ANTIBIOTIC_CODE IS NOT NULL ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // System.out.println("------------------queryNOTDispense_M--------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 已发药（汇总）
     */
    public TParm queryDispenseed_M(TParm parm) {// wanglong add 20141204
        String sql =
                "WITH IA AS (SELECT * FROM (SELECT B.ORDER_CODE, B.CONTRACT_PRICE / A.DOSAGE_QTY LAST_VERIFY_PRICE, "
                        + "                        ROW_NUMBER() OVER (PARTITION BY B.ORDER_CODE ORDER BY B.OPT_DATE DESC) RN "
                        + "                   FROM PHA_TRANSUNIT A, IND_AGENT B "
                        + "                  WHERE A.ORDER_CODE = B.ORDER_CODE) "
                        + "           WHERE RN = 1) "
                        + "SELECT C.REGION_CHN_ABN AS REGION_CHN_DESC, OO.ORDER_CODE,  "
                        + "       OO.ORDER_DESC || CASE WHEN TRIM(OO.GOODS_DESC) IS NOT NULL OR TRIM(OO.GOODS_DESC) <> '' "
                        + "                        THEN  '(' || OO.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC, "
                        + "       OO.SPECIFICATION, OO.DISPENSE_UNIT, "
                        + "       SUM(OO.DISPENSE_QTY) AS DISPENSE_QTY, SUM(OO.OWN_AMT) AS OWN_AMT, "
                        + "       SUM(IA.LAST_VERIFY_PRICE * OO.DOSAGE_QTY) AS CONTRACT_PRICE "
                        + "  FROM OPD_ORDER OO, SYS_PATINFO SP, PHA_BASE PB, SYS_REGION C, IA "
                        + " WHERE OO.REGION_CODE = C.REGION_CODE "
                        + "   AND OO.PHA_DISPENSE_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   AND OO.ORDER_CODE = IA.ORDER_CODE "
                        + "   AND PB.ORDER_CODE(+) = OO.ORDER_CODE "
                        + "   AND OO.MR_NO = SP.MR_NO "
                        + "   AND OO.PHA_DISPENSE_CODE IS NOT NULL "
                        + "   AND OO.PHA_RETN_CODE IS NULL  @  @  @  @  @  @  @  @  @  "
                        + "GROUP BY C.REGION_CHN_ABN, OO.ORDER_CODE, OO.ORDER_DESC, OO.GOODS_DESC, OO.SPECIFICATION, OO.DISPENSE_UNIT "
                        + "ORDER BY C.REGION_CHN_ABN, OO.ORDER_DESC";
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("START_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 开始时间
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("END_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 结束时间
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {// 院区
            sql =
                    sql.replaceFirst("@", " AND OO.REGION_CODE = '" + parm.getValue("REGION_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MR_NO"))) {// 病案号
            sql = sql.replaceFirst("@", " AND OO.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {// 执行科室
            sql =
                    sql.replaceFirst("@",
                                     " AND OO.EXEC_DEPT_CODE = '" + parm.getValue("EXEC_DEPT_CODE")
                                             + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CODE"))) {// 医嘱
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CODE = '" + parm.getValue("ORDER_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT_CODE"))) {// 科室
            sql =
                    sql.replaceFirst("@", " AND OO.DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DR_CODE"))) {// 医生
            sql = sql.replaceFirst("@", " AND OO.DR_CODE = '" + parm.getValue("DR_CODE") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("CTRLDRUGCLASS_CODE"))) {// 麻精
            sql = sql.replaceFirst("@", " AND IA.CTRLDRUGCLASS_CODE IN ('01','02') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE1"))) {// 药品种类
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE= '" + parm.getValue("VALUE1")
                            + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE2"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "') ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE3"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "','" + parm.getValue("VALUE3")
                            + "') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE"))) {// 抗生素登记
            sql =
                    sql.replaceFirst("@",
                                     " AND PB.ANTIBIOTIC_CODE = '"
                                             + parm.getValue("ANTIBIOTIC_CODE") + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE1"))) {// 医生
            sql = sql.replaceFirst("@", " AND PB.ANTIBIOTIC_CODE IS NOT NULL ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // System.out.println("------------------queryDispenseed_M--------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 已退药（汇总）
     */
    public TParm queryReturned_M(TParm parm) {// wanglong add 20141204
        String sql =
                "WITH IA AS (SELECT * FROM (SELECT B.ORDER_CODE, B.CONTRACT_PRICE / A.DOSAGE_QTY LAST_VERIFY_PRICE, "
                        + "                        ROW_NUMBER() OVER (PARTITION BY B.ORDER_CODE ORDER BY B.OPT_DATE DESC) RN "
                        + "                   FROM PHA_TRANSUNIT A, IND_AGENT B "
                        + "                  WHERE A.ORDER_CODE = B.ORDER_CODE) "
                        + "           WHERE RN = 1) "
                        + "SELECT C.REGION_CHN_ABN AS REGION_CHN_DESC, OO.ORDER_CODE,  "
                        + "       OO.ORDER_DESC || CASE WHEN TRIM(OO.GOODS_DESC) IS NOT NULL OR TRIM(OO.GOODS_DESC) <> '' "
                        + "                        THEN  '(' || OO.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC, "
                        + "       OO.SPECIFICATION, OO.DISPENSE_UNIT, "
                        + "       SUM(OO.DISPENSE_QTY) AS DISPENSE_QTY, SUM(OO.OWN_AMT) AS OWN_AMT, "
                        + "       SUM(IA.LAST_VERIFY_PRICE * OO.DOSAGE_QTY) AS CONTRACT_PRICE "
                        + "  FROM OPD_ORDER OO, SYS_PATINFO SP, PHA_BASE PB, SYS_REGION C, IA "
                        + " WHERE OO.REGION_CODE = C.REGION_CODE "
                        + "   AND OO.PHA_RETN_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   AND OO.ORDER_CODE = IA.ORDER_CODE "
                        + "   AND PB.ORDER_CODE(+) = OO.ORDER_CODE "
                        + "   AND OO.MR_NO = SP.MR_NO "
                        + "   AND OO.PHA_RETN_CODE IS NOT NULL  @  @  @  @  @  @  @  @  @  "
                        + "GROUP BY C.REGION_CHN_ABN, OO.ORDER_CODE, OO.ORDER_DESC, OO.GOODS_DESC, OO.SPECIFICATION, OO.DISPENSE_UNIT "
                        + "ORDER BY C.REGION_CHN_ABN, OO.ORDER_DESC";
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("START_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 开始时间
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("END_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 结束时间
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {// 院区
            sql =
                    sql.replaceFirst("@", " AND OO.REGION_CODE = '" + parm.getValue("REGION_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MR_NO"))) {// 病案号
            sql = sql.replaceFirst("@", " AND OO.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {// 执行科室
            sql =
                    sql.replaceFirst("@",
                                     " AND OO.EXEC_DEPT_CODE = '" + parm.getValue("EXEC_DEPT_CODE")
                                             + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CODE"))) {// 医嘱
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CODE = '" + parm.getValue("ORDER_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT_CODE"))) {// 科室
            sql =
                    sql.replaceFirst("@", " AND OO.DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DR_CODE"))) {// 医生
            sql = sql.replaceFirst("@", " AND OO.DR_CODE = '" + parm.getValue("DR_CODE") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("CTRLDRUGCLASS_CODE"))) {// 麻精
            sql = sql.replaceFirst("@", " AND IA.CTRLDRUGCLASS_CODE IN ('01','02') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE1"))) {// 药品种类
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE= '" + parm.getValue("VALUE1")
                            + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE2"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "') ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE3"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "','" + parm.getValue("VALUE3")
                            + "') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE"))) {// 抗生素登记
            sql =
                    sql.replaceFirst("@",
                                     " AND PB.ANTIBIOTIC_CODE = '"
                                             + parm.getValue("ANTIBIOTIC_CODE") + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE1"))) {// 医生
            sql = sql.replaceFirst("@", " AND PB.ANTIBIOTIC_CODE IS NOT NULL ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // System.out.println("------------------queryReturned_M--------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 已配药（汇总）
     */
    public TParm queryDosaged_M(TParm parm) {// wanglong add 20141204
        String sql =
                "WITH IA AS (SELECT * FROM (SELECT B.ORDER_CODE, B.CONTRACT_PRICE / A.DOSAGE_QTY LAST_VERIFY_PRICE, "
                        + "                        ROW_NUMBER() OVER (PARTITION BY B.ORDER_CODE ORDER BY B.OPT_DATE DESC) RN "
                        + "                   FROM PHA_TRANSUNIT A, IND_AGENT B "
                        + "                  WHERE A.ORDER_CODE = B.ORDER_CODE) "
                        + "           WHERE RN = 1) "
                        + "SELECT C.REGION_CHN_ABN AS REGION_CHN_DESC, OO.ORDER_CODE,  "
                        + "       OO.ORDER_DESC || CASE WHEN TRIM(OO.GOODS_DESC) IS NOT NULL OR TRIM(OO.GOODS_DESC) <> '' "
                        + "                        THEN  '(' || OO.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC, "
                        + "       OO.SPECIFICATION, OO.DISPENSE_UNIT, "
                        + "       SUM(OO.DISPENSE_QTY) AS DISPENSE_QTY, SUM(OO.OWN_AMT) AS OWN_AMT, "
                        + "       SUM(IA.LAST_VERIFY_PRICE * OO.DOSAGE_QTY) AS CONTRACT_PRICE "
                        + "  FROM OPD_ORDER OO, SYS_PATINFO SP, PHA_BASE PB, SYS_REGION C, IA "
                        + " WHERE OO.REGION_CODE = C.REGION_CODE "
                        + "   AND OO.PHA_DOSAGE_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   AND OO.ORDER_CODE = IA.ORDER_CODE "
                        + "   AND PB.ORDER_CODE(+) = OO.ORDER_CODE "
                        + "   AND OO.MR_NO = SP.MR_NO "
                        + "   AND OO.PHA_DOSAGE_CODE IS NOT NULL "
                        + "   AND OO.PHA_RETN_CODE IS NULL  @  @  @  @  @  @  @  @  @  "
                        + "GROUP BY C.REGION_CHN_ABN, OO.ORDER_CODE, OO.ORDER_DESC, OO.GOODS_DESC, OO.SPECIFICATION, OO.DISPENSE_UNIT "
                        + "ORDER BY C.REGION_CHN_ABN, OO.ORDER_DESC";
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("START_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 开始时间
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("END_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 结束时间
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {// 院区
            sql =
                    sql.replaceFirst("@", " AND OO.REGION_CODE = '" + parm.getValue("REGION_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MR_NO"))) {// 病案号
            sql = sql.replaceFirst("@", " AND OO.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {// 执行科室
            sql =
                    sql.replaceFirst("@",
                                     " AND OO.EXEC_DEPT_CODE = '" + parm.getValue("EXEC_DEPT_CODE")
                                             + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CODE"))) {// 医嘱
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CODE = '" + parm.getValue("ORDER_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT_CODE"))) {// 科室
            sql =
                    sql.replaceFirst("@", " AND OO.DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DR_CODE"))) {// 医生
            sql = sql.replaceFirst("@", " AND OO.DR_CODE = '" + parm.getValue("DR_CODE") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("CTRLDRUGCLASS_CODE"))) {// 麻精
            sql = sql.replaceFirst("@", " AND IA.CTRLDRUGCLASS_CODE IN ('01','02') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE1"))) {// 药品种类
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE= '" + parm.getValue("VALUE1")
                            + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE2"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "') ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE3"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "','" + parm.getValue("VALUE3")
                            + "') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE"))) {// 抗生素登记
            sql =
                    sql.replaceFirst("@",
                                     " AND PB.ANTIBIOTIC_CODE = '"
                                             + parm.getValue("ANTIBIOTIC_CODE") + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE1"))) {// 医生
            sql = sql.replaceFirst("@", " AND PB.ANTIBIOTIC_CODE IS NOT NULL ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // System.out.println("------------------queryDosaged_M--------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 已计费（汇总）
     */
    public TParm queryBill_M(TParm parm) {// wanglong add 20141204
        String sql =
                "WITH IA AS (SELECT * FROM (SELECT B.ORDER_CODE, B.CONTRACT_PRICE / A.DOSAGE_QTY LAST_VERIFY_PRICE, "
                        + "                        ROW_NUMBER() OVER (PARTITION BY B.ORDER_CODE ORDER BY B.OPT_DATE DESC) RN "
                        + "                   FROM PHA_TRANSUNIT A, IND_AGENT B "
                        + "                  WHERE A.ORDER_CODE = B.ORDER_CODE) "
                        + "           WHERE RN = 1) "
                        + "SELECT C.REGION_CHN_ABN AS REGION_CHN_DESC, OO.ORDER_CODE,  "
                        + "       OO.ORDER_DESC || CASE WHEN TRIM(OO.GOODS_DESC) IS NOT NULL OR TRIM(OO.GOODS_DESC) <> '' "
                        + "                        THEN  '(' || OO.GOODS_DESC || ')' ELSE '' END AS ORDER_DESC, "
                        + "       OO.SPECIFICATION, OO.DISPENSE_UNIT, "
                        + "       SUM(OO.DISPENSE_QTY) AS DISPENSE_QTY, SUM(OO.OWN_AMT) AS OWN_AMT, "
                        + "       SUM(IA.LAST_VERIFY_PRICE * OO.DOSAGE_QTY) AS CONTRACT_PRICE "
                        + "  FROM OPD_ORDER OO, SYS_PATINFO SP, PHA_BASE PB, SYS_REGION C, IA "
                        + " WHERE OO.REGION_CODE = C.REGION_CODE "
                        + "   AND OO.BILL_DATE BETWEEN TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') AND TO_DATE('#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   AND OO.ORDER_CODE = IA.ORDER_CODE "
                        + "   AND PB.ORDER_CODE(+) = OO.ORDER_CODE "
                        + "   AND OO.MR_NO = SP.MR_NO "
                        + "   AND OO.BILL_FLG = 'Y' "
                        + "   AND OO.PHA_RETN_CODE IS NULL  @  @  @  @  @  @  @  @  @  "
                        + "GROUP BY C.REGION_CHN_ABN, OO.ORDER_CODE, OO.ORDER_DESC, OO.GOODS_DESC, OO.SPECIFICATION, OO.DISPENSE_UNIT "
                        + "ORDER BY C.REGION_CHN_ABN, OO.ORDER_DESC";
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("START_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 开始时间
        sql =
                sql.replaceFirst("#", StringTool.getString(parm.getTimestamp("END_DATE"),
                                                           "yyyy/MM/dd HH:mm:ss"));// 结束时间
        if (!StringUtil.isNullString(parm.getValue("REGION_CODE"))) {// 院区
            sql =
                    sql.replaceFirst("@", " AND OO.REGION_CODE = '" + parm.getValue("REGION_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("MR_NO"))) {// 病案号
            sql = sql.replaceFirst("@", " AND OO.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("EXEC_DEPT_CODE"))) {// 执行科室
            sql =
                    sql.replaceFirst("@",
                                     " AND OO.EXEC_DEPT_CODE = '" + parm.getValue("EXEC_DEPT_CODE")
                                             + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CODE"))) {// 医嘱
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CODE = '" + parm.getValue("ORDER_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DEPT_CODE"))) {// 科室
            sql =
                    sql.replaceFirst("@", " AND OO.DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("DR_CODE"))) {// 医生
            sql = sql.replaceFirst("@", " AND OO.DR_CODE = '" + parm.getValue("DR_CODE") + "' ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("CTRLDRUGCLASS_CODE"))) {// 麻精
            sql = sql.replaceFirst("@", " AND IA.CTRLDRUGCLASS_CODE IN ('01','02') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE1"))) {// 药品种类
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE= '" + parm.getValue("VALUE1")
                            + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE2"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "') ");
        } else if (!StringUtil.isNullString(parm.getValue("ORDER_CAT1_CODE3"))) {
            sql =
                    sql.replaceFirst("@", " AND OO.ORDER_CAT1_CODE IN ('" + parm.getValue("VALUE1")
                            + "','" + parm.getValue("VALUE2") + "','" + parm.getValue("VALUE3")
                            + "') ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE"))) {// 抗生素登记
            sql =
                    sql.replaceFirst("@",
                                     " AND PB.ANTIBIOTIC_CODE = '"
                                             + parm.getValue("ANTIBIOTIC_CODE") + "' ");
        } else if (!StringUtil.isNullString(parm.getValue("ANTIBIOTIC_CODE1"))) {// 医生
            sql = sql.replaceFirst("@", " AND PB.ANTIBIOTIC_CODE IS NOT NULL ");
        } else {
            sql = sql.replaceFirst("@", "");
        }
        // System.out.println("------------------queryBill_M--------------"+sql);
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
            return result;
        }
        return result;
    }
}
