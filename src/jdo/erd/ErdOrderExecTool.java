package jdo.erd;

import java.sql.Timestamp;
import com.dongyang.data.TParm;
import com.dongyang.data.TNull;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: 急诊留观护士站Tool </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: JAVAHIS </p>
 *
 * <p>Company: </p>
 *
 * @author ZangJH 2009-10-30
 * @version 1.0
 */
public class ErdOrderExecTool
    extends TJDOTool {
    public ErdOrderExecTool() {
    }

    /**
     * 实例
     */
    private static ErdOrderExecTool instanceObject;

    /**
     * 得到实例
     * @return PatTool
     */
    public static ErdOrderExecTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ErdOrderExecTool();
        return instanceObject;
    }


    /**
     * 执行
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onExec(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 前台传的数据
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            // 取消动作
            TParm execData = new TParm();
            execData.setData("CASE_NO", parm.getData("CASE_NO", i));
            execData.setData("RX_NO", parm.getData("RX_NO", i));
            execData.setData("SEQ_NO", parm.getData("SEQ_NO", i));
            execData.setData("ORDER_DATE", parm.getData("ORDER_DATE", i));//wanglong add 20150407
            execData.setData("NS_EXEC_CODE", parm.getData("OPT_USER", i));
            execData.setData("NS_EXEC_DATE", parm.getData("NS_EXEC_DATE", i));
            execData.setData("NS_NOTE", parm.getData("NS_NOTE", i) == null
                    ? new TNull(String.class) : parm.getData("NS_NOTE", i));
            execData.setData("OPT_USER", parm.getData("OPT_USER", i));
            execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));
            result = ErdForBedAndRecordTool.getInstance().updateExec(execData, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * 执行
     * 
     * @param parm
     *            TParm
     * @param connection
     *            TConnection
     * @return TParm
     */
    public TParm onUndoExec(TParm parm, TConnection connection) {
        TParm result = new TParm();
        // 前台传的数据
        int count = parm.getCount();
        for (int i = 0; i < count; i++) {
            // 取消动作
            TParm execData = new TParm();
            execData.setData("CASE_NO", parm.getData("CASE_NO", i));
            execData.setData("RX_NO", parm.getData("RX_NO", i));
            execData.setData("SEQ_NO", parm.getData("SEQ_NO", i));
            execData.setData("ORDER_DATE", parm.getData("ORDER_DATE", i));//wanglong add 20150407
            execData.setData("NS_EXEC_CODE", new TNull(String.class));
            execData.setData("NS_EXEC_DATE", new TNull(Timestamp.class));
            execData.setData("NS_NOTE", parm.getData("NS_NOTE", i) == null
                    ? new TNull(String.class) : parm.getData("NS_NOTE", i));
            execData.setData("OPT_USER", parm.getData("OPT_USER", i));
            execData.setData("OPT_TERM", parm.getData("OPT_TERM", i));
            execData.setData("OPT_DATE", parm.getData("OPT_DATE", i));
            result = ErdForBedAndRecordTool.getInstance().updateExec(execData, connection);
            if (result.getErrCode() < 0) {
                err("ERR:" + result.getErrCode() + result.getErrText() + result.getErrName());
                return result;
            }
        }
        return result;
    }

    /**
     * 病患医嘱信息
     * @param caseNo
     * @param barCode
     * @param startDate
     * @param endDate
     * @param cat1Type
     * @param isExe
     * @param doseType
     * @return
     */
    public String queryPatOrder(String caseNo, String barCode, String startDate, String endDate,
                                String cat1Type, String isExe, String doseType) {// wanglong add 20150407
        String sql =
                "SELECT '' FLG, A.EXEC_FLG, A.LINKMAIN_FLG, A.LINK_NO, A.ORDER_CODE, A.ORDER_DESC,  "
                        + "       A.ORDER_DESC || CASE WHEN TRIM(A.GOODS_DESC) IS NOT NULL OR TRIM(A.GOODS_DESC) <> '' "
                        + "                            THEN '(' || A.GOODS_DESC || ')' ELSE '' END "
                        + "                    || CASE WHEN TRIM(A.SPECIFICATION) IS NOT NULL OR TRIM(A.SPECIFICATION) <> '' "
                        + "                            THEN '(' || A.SPECIFICATION || ')' ELSE '' END "
                        + "       AS ORDER_DESC_AND_SPECIFICATION, A.MEDI_QTY, A.MEDI_UNIT,  "
                        + "       CASE WHEN A.CAT1_TYPE = 'PHA' THEN A.BAR_CODE ELSE A.MED_APPLY_NO END AS BAR_CODE, "
                        + "       A.ROUTE_CODE, A.DR_NOTE, A.DR_CODE, A.ORDER_DATE, A.NS_EXEC_CODE, A.NS_EXEC_DATE, "
                        + "       TRUNC(A.NS_EXEC_DATE) NS_EXEC_DATE_DAY, TO_CHAR(A.NS_EXEC_DATE,'HH24:MI:SS') NS_EXEC_DATE_TIME, "
                        + "       A.CASE_NO, A.SEQ_NO, A.RX_NO, A.SETMAIN_FLG, A.ORDERSET_GROUP_NO, A.DOSE_TYPE, "
                        + "       A.CAT1_TYPE, A.ORDER_CAT1_CODE, A.DOSAGE_QTY, A.DOSAGE_UNIT "
                        + "  FROM OPD_ORDER A, SYS_PHAROUTE B        "
                        + " WHERE A.CASE_NO = '#'                    "
                        + "   AND (A.SETMAIN_FLG = 'Y' OR A.ORDERSET_CODE IS NULL OR A.ORDERSET_CODE = '') "
                        + "   AND A.ROUTE_CODE = B.ROUTE_CODE(+) "
                        + "   AND A.RX_TYPE NOT IN ('7','6') ";
        sql = sql.replaceFirst("#", caseNo);
        if (barCode.equals("")) {
            sql +=
                    "   AND A.ORDER_DATE BETWEEN TO_DATE('#', 'yyyymmddhh24miss') AND TO_DATE('#', 'yyyymmddhh24miss') ";
            sql = sql.replaceFirst("#", startDate);
            sql = sql.replaceFirst("#", endDate);
        }
        if (isExe.equals("Y")) {
            sql += " AND A.NS_EXEC_CODE IS NOT NULL";
        } else if (isExe.equals("N")) {
            sql += " AND A.NS_EXEC_CODE IS NULL ";
        }
        if (cat1Type.equals("")) {
            if (!barCode.equals("")) {
                sql +=
                        " AND ( A.MED_APPLY_NO = '" + barCode + "' OR  A.BAR_CODE = '" + barCode
                                + "')";
            }
        } else {
            sql += " AND A.CAT1_TYPE IN ('" + cat1Type + "')";
            if (cat1Type.equals("PHA")) {
                if (!barCode.equals("")) {
                    sql += " AND A.BAR_CODE = '" + barCode + "' ";
                }
                if (!doseType.equals("")) {
                    sql += " AND B.CLASSIFY_TYPE = '" + doseType + "' ";
                }
            } else if (cat1Type.equals("LIS','RIS")) {
                if (!barCode.equals("")) {
                    sql += " AND A.MED_APPLY_NO = '" + barCode + "'";
                }
            }
        }
        sql += " ORDER BY A.RX_NO, A.SEQ_NO";
        return sql;
    }
}
