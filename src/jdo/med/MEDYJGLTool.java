package jdo.med;

import java.util.HashMap;
import java.util.Map;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p> Title: 电生理排程工具类 </p>
 * 
 * <p> Description: 电生理排程工具类 </p>
 * 
 * <p> Copyright: Copyright (c) 2013 </p>
 * 
 * <p> Company:BlueCore </p>
 * 
 * @author wanglong 2013.11.12
 * @version 1.0
 */
public class MEDYJGLTool
        extends TJDODBTool {

    /**
     * 实例
     */
    public static MEDYJGLTool instanceObject;

    /**
     * 得到实例
     * 
     * @return
     */
    public static MEDYJGLTool getInstance() {
        if (instanceObject == null) instanceObject = new MEDYJGLTool();
        return instanceObject;
    }

    /**
     * 查询人员信息
     * 
     * @param parm
     * @return
     */
    public TParm queryPatInfo(TParm parm) {
        String SQL_OE = // 门急诊专用
                "SELECT DISTINCT A.ADM_TYPE, A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE,"
                        + "      FLOOR(MONTHS_BETWEEN( SYSDATE, B.BIRTH_DATE) / 12) AS AGE, B.BIRTH_DATE,"
                        + "      ！ AS REPORT_STATE, MIN(A.START_DTTM) MIN_START_DTTM,"
                        + "      C.REALDEPT_CODE DEPT_CODE, C.REALDR_CODE DR_CODE, TO_CHAR(C.QUE_NO) NO "
                        + " FROM MED_APPLY A, SYS_PATINFO B, REG_PATADM C "
                        + "WHERE A.EXEC_DEPT_CODE = '&'    "
                        + "  AND A.MR_NO = B.MR_NO         "
                        + "  AND A.CASE_NO = C.CASE_NO     "
                        + "  AND A.ADM_TYPE IN ('O', 'E')  "
                        + "  @  ￥  "
                        + "  AND A.ORDER_DATE BETWEEN TO_DATE( '!', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "GROUP BY A.ADM_TYPE, A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, C.REALDEPT_CODE, C.REALDR_CODE, C.QUE_NO";
        String SQL_I = // 住院专用
                "SELECT DISTINCT A.ADM_TYPE, A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE,"
                        + "      FLOOR(MONTHS_BETWEEN( SYSDATE, B.BIRTH_DATE) / 12) AS AGE, B.BIRTH_DATE,"
                        + "      ！ AS REPORT_STATE, MIN(A.START_DTTM) MIN_START_DTTM, C.DEPT_CODE,"
                        + "      C.VS_DR_CODE DR_CODE, D.BED_NO_DESC NO "
                        + " FROM MED_APPLY A, SYS_PATINFO B, ADM_INP C,SYS_BED D "
                        + "WHERE A.EXEC_DEPT_CODE = '&'  "
                        + "  AND A.MR_NO = B.MR_NO       "
                        + "  AND A.CASE_NO = C.CASE_NO   "
                        + "  AND A.ADM_TYPE = 'I'        "
                        + "  AND C.BED_NO = D.BED_NO     "
                        + "  @  ￥  "
                        + "  AND A.ORDER_DATE BETWEEN TO_DATE( '!', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS') "
                        + "GROUP BY A.ADM_TYPE, A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, C.DEPT_CODE, C.VS_DR_CODE, D.BED_NO_DESC";
        String SQL_H = // 健检专用
                "SELECT DISTINCT A.ADM_TYPE, A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE,"
                        + "      FLOOR(MONTHS_BETWEEN( SYSDATE, B.BIRTH_DATE) / 12) AS AGE, B.BIRTH_DATE,"
                        + "      ！ AS REPORT_STATE, MIN(A.START_DTTM) MIN_START_DTTM, C.COMPANY_CODE DEPT_CODE, C.CONTRACT_CODE DR_CODE, TO_CHAR(D.SEQ_NO) NO "
                        + " FROM MED_APPLY A, SYS_PATINFO B, HRM_PATADM C, HRM_CONTRACTD D "
                        + "WHERE A.EXEC_DEPT_CODE = '&'            "
                        + "  AND A.MR_NO = B.MR_NO                 "
                        + "  AND A.CASE_NO = C.CASE_NO             "
                        + "  AND A.ADM_TYPE = 'H'                  "
                        + "  AND C.REPORT_DATE IS NOT NULL         "
                        + "  AND C.CONTRACT_CODE = D.CONTRACT_CODE "
                        + "  AND C.MR_NO = D.MR_NO                 "
                        + "  @  ￥  "
                        + "  AND A.ORDER_DATE BETWEEN TO_DATE( '!', 'YYYYMMDDHH24MISS') AND TO_DATE( '#', 'YYYYMMDDHH24MISS')"
                        + "GROUP BY A.ADM_TYPE, A.CASE_NO, A.MR_NO, B.PAT_NAME, B.SEX_CODE, B.BIRTH_DATE, C.COMPANY_CODE, C.CONTRACT_CODE, D.SEQ_NO";
        String sql = "";
        if (parm.getValue("ADM_TYPE").equals("O") || parm.getValue("ADM_TYPE").equals("E")) {
            sql = SQL_OE + " ORDER BY C.REALDEPT_CODE, C.REALDR_CODE, A.MR_NO";
        } else if (parm.getValue("ADM_TYPE").equals("I")) {
            sql = SQL_I + " ORDER BY C.DEPT_CODE, C.VS_DR_CODE, A.MR_NO";
        } else if (parm.getValue("ADM_TYPE").equals("H")) {
            sql = SQL_H + " ORDER BY C.COMPANY_CODE, C.CONTRACT_CODE, D.SEQ_NO";
        } else {
            sql = "SELECT * FROM (" + SQL_OE + " UNION " + SQL_I + " UNION " + SQL_H + ") ORDER BY ADM_TYPE, MR_NO";
        }
        sql = sql.replaceAll("&", parm.getValue("EXEC_DEPT_CODE"));//add by wanglong 20131127
        sql = sql.replaceAll("!", parm.getValue("START_DATE"));
        sql = sql.replaceAll("#", parm.getValue("END_DATE"));
        if (parm.getValue("REPORT_STATE").equals("ALL")) {// 全部
            sql = sql.replaceAll("！", " 'ALL' ");
            sql = sql.replaceAll("@", " AND A.STATUS < 9 ");
        } else if (parm.getValue("REPORT_STATE").equals("UNREPORT")) {// 未报到
            sql = sql.replaceAll("！", " 'UNREPORT' ");
            sql = sql.replaceAll("@", " AND A.STATUS IN (0,1,2,3,5) ");
        } else if (parm.getValue("REPORT_STATE").equals("REPORTED")) {// 已报到
            sql = sql.replaceAll("！", " 'REPORTED' ");
            sql = sql.replaceAll("@", " AND A.STATUS IN (4,6,7,8)");
        }
        if (!parm.getValue("MR_NO").equals("")) {
            sql = sql.replaceAll("￥", " AND A.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceAll("￥", "");
        }
//        System.out.println("------------------sql-------111-------"+sql);
        TParm result = new TParm(this.select(sql));
        return result;
    }

    /**
     * 查询医嘱信息
     * 
     * @param parm
     * @return
     */
    public TParm queryOrderInfo(TParm parm) {
        String sql =
                "SELECT '' FLG, A.ADM_TYPE, A.CASE_NO, A.MR_NO, A.IPD_NO, A.BED_NO, A.DEPT_CODE, A.STATION_CODE, A.CAT1_TYPE, A.APPLICATION_NO, A.ORDER_NO, A.SEQ_NO, A.ORDER_CODE, "
                        + "       CASE WHEN A.STATUS IN (0,1,2,3,5) THEN 'UNREPORT' WHEN A.STATUS IN (4,6,7,8) THEN 'REPORTED' ELSE 'ALL' END REPORT_STATE, "
                        + "       A.ORDER_DESC, A.ORDER_DATE, A.ORDER_DEPT_CODE DEPT_CODE, A.ORDER_DR_CODE DR_CODE, A.URGENT_FLG, A.DR_NOTE  "
                        + "  FROM MED_APPLY A               "
                        + " WHERE A.EXEC_DEPT_CODE = '&'    "// modify by wanglong 20131127
                        + "   AND A.ORDER_DATE BETWEEN TO_DATE('#','YYYYMMDDHH24MISS') AND TO_DATE('#','YYYYMMDDHH24MISS') @  $ $ $ "
                        + " ORDER BY A.ORDER_DATE ";
        sql = sql.replaceFirst("&", parm.getValue("EXEC_DEPT_CODE"));//add by wanglong 20131127
        sql = sql.replaceFirst("#", parm.getValue("START_DATE"));
        sql = sql.replaceFirst("#", parm.getValue("END_DATE"));
        if (parm.getValue("REPORT_STATE").equals("ALL")) {// 全部
            sql = sql.replaceFirst("@", " AND A.STATUS < 9 ");
        } else if (parm.getValue("REPORT_STATE").equals("UNREPORT")) {// 未报到
            sql = sql.replaceFirst("@", " AND A.STATUS IN (0,1,2,3,5) ");
        } else if (parm.getValue("REPORT_STATE").equals("REPORTED")) {// 已报到
            sql = sql.replaceFirst("@", " AND A.STATUS IN (4,6,7,8)");
        }
        if (!parm.getValue("MR_NO").equals("")) {
            sql = sql.replaceFirst("\\$", " AND A.MR_NO = '" + parm.getValue("MR_NO") + "' ");
        } else {
            sql = sql.replaceFirst("\\$", "");
        }
        if (!parm.getValue("ADM_TYPE").equals("")) {
            sql = sql.replaceFirst("\\$", " AND A.ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "' ");
        } else {
            sql = sql.replaceFirst("\\$", "");
        }
        if (!parm.getValue("CASE_NO").equals("")) { 
            sql = sql.replaceFirst("\\$", " AND A.CASE_NO = '" + parm.getValue("CASE_NO") + "' ");
        } else {
            sql = sql.replaceFirst("\\$", "");
        }
//        System.out.println("------------------sql-------222-------"+sql);
        TParm result = new TParm(this.select(sql));
        return result;
    }

    /**
     * 报到
     * 
     * @param param
     */
    public TParm registOrder(TParm parm) {
        TParm param = new TParm();
        String[] sql = null;
        TParm result = new TParm();
        for (int i = 0; i < parm.getCount(); i++) {
            param = parm.getRow(i);
            String orderSQL = "";
            if (param.getValue("ADM_TYPE").equals("O") || param.getValue("ADM_TYPE").equals("E")) {
                orderSQL =
                        "UPDATE OPD_ORDER SET EXEC_FLG = 'Y', EXEC_DATE=SYSDATE, EXEC_DR_CODE='@', EXEC_DR_DESC='@' "
                                + " WHERE (CASE_NO, RX_NO, ORDERSET_GROUP_NO, ORDERSET_CODE) "
                                + "IN (SELECT CASE_NO, RX_NO, ORDERSET_GROUP_NO, ORDERSET_CODE "
                                + " FROM OPD_ORDER "
                                + "WHERE CASE_NO='#' AND RX_NO='#' AND SEQ_NO=# AND ORDERSET_CODE='#')";
                if (!parm.getValue("EXEC_DR_CODE").equals("")) {//wanglong add 20140514增加更新执行人字段
                    orderSQL = orderSQL.replaceFirst("@", parm.getValue("EXEC_DR_CODE"));
                }
                if (!parm.getValue("EXEC_DR_DESC").equals("")) {
                    orderSQL = orderSQL.replaceFirst("@", parm.getValue("EXEC_DR_DESC"));
                }
                if (!param.getValue("CASE_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("CASE_NO"));
                }
                if (!param.getValue("ORDER_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_NO"));
                }
                if (!param.getValue("SEQ_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("SEQ_NO"));
                }
                if (!param.getValue("ORDER_CODE").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_CODE"));
                }
            } else if (param.getValue("ADM_TYPE").equals("I")) {
                orderSQL =
                        "UPDATE ODI_ORDER SET EXEC_FLG = 'Y' WHERE (CASE_NO, ORDER_NO,ORDERSET_GROUP_NO, ORDERSET_CODE) "
                                + "IN (SELECT CASE_NO,ORDER_NO, ORDERSET_GROUP_NO, ORDERSET_CODE "
                                + " FROM ODI_ORDER "
                                + "WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=# AND ORDERSET_CODE='#')";
                if (!param.getValue("CASE_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("CASE_NO"));
                }
                if (!param.getValue("ORDER_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_NO"));
                }
                if (!param.getValue("SEQ_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("SEQ_NO"));
                }
                if (!param.getValue("ORDER_CODE").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_CODE"));
                }
            } else if (param.getValue("ADM_TYPE").equals("H")) {
                orderSQL =
                        "UPDATE HRM_ORDER SET EXEC_FLG = 'Y', EXEC_DR_CODE='@', EXEC_DR_DESC='@' "
                                + "WHERE (CASE_NO, ORDERSET_GROUP_NO, ORDERSET_CODE) "
                                + "IN (SELECT CASE_NO, ORDERSET_GROUP_NO, ORDERSET_CODE "
                                + " FROM HRM_ORDER "
                                + "WHERE CASE_NO='#' AND SEQ_NO=# AND ORDERSET_CODE='#')";
                if (!parm.getValue("EXEC_DR_CODE").equals("")) {//wanglong add 20140514增加更新执行人字段
                    orderSQL = orderSQL.replaceFirst("@", parm.getValue("EXEC_DR_CODE"));
                }
                if (!parm.getValue("EXEC_DR_DESC").equals("")) {
                    orderSQL = orderSQL.replaceFirst("@", parm.getValue("EXEC_DR_DESC"));
                }
                if (!param.getValue("CASE_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("CASE_NO"));
                }
                if (!param.getValue("SEQ_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("SEQ_NO"));
                }
                if (!param.getValue("ORDER_CODE").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_CODE"));
                }
            }
            String medSQL =
                    "UPDATE MED_APPLY SET STATUS='4',REGISTER_DATE=SYSDATE "
                            + " WHERE APPLICATION_NO='#' AND ORDER_NO='#' AND SEQ_NO='#'";
            if (!param.getValue("APPLICATION_NO").equals("")) {
                medSQL = medSQL.replaceFirst("#", param.getValue("APPLICATION_NO"));
            }
            if (!param.getValue("ORDER_NO").equals("")) {
                medSQL = medSQL.replaceFirst("#", param.getValue("ORDER_NO"));
            }
            if (!param.getValue("SEQ_NO").equals("")) {
                medSQL = medSQL.replaceFirst("#", param.getValue("SEQ_NO"));
            }
            if (i == 0) {
                sql = new String[]{orderSQL, medSQL };
            } else sql = StringTool.copyArray(sql, new String[]{orderSQL, medSQL });
        }
        if (sql.length <= 0) {
            result.setErr(-1, "没有需要操作的数据");
            return result;
        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        if (parm.getValue("ADM_TYPE", 0).equals("I")) {// add by wanglong 20131209
            inParm.setData("ORDER", parm.getData());
            inParm.setData("TYPE", "ADD");
            inParm.setData("ADM_TYPE", "I");
        }
        inParm.setData("MED_APPLY_LUMP_FLG","N");//pangben 2016-9-12 套餐病患取消报道需要手动操作退费
        result = TIOM_AppServer.executeAction("action.med.MedAction", "onUpdate", inParm);
        return result;
    }

    /**
     * 取消报到
     * 
     * @param parm
     */
    public TParm unRegistOrder(TParm parm) {
        TParm param = new TParm();
        String[] sql = new String[]{};
        TParm result = new TParm();
        int errCount = 0;
        String errStr = "";
        for (int i = 0; i < parm.getCount(); i++) {
            param = parm.getRow(i);
            String orderSQL = "";
            if (param.getValue("ADM_TYPE").equals("O") || param.getValue("ADM_TYPE").equals("E")) {
                String bilFlg="SELECT CASE_NO, RX_NO, ORDERSET_GROUP_NO, ORDERSET_CODE,BILL_FLG "// wanglong add 20140606
                                + " FROM OPD_ORDER "
                                + "WHERE CASE_NO='#' AND RX_NO='#' AND SEQ_NO=# AND ORDERSET_CODE='#'";
                if (!param.getValue("CASE_NO").equals("")) {
                    bilFlg = bilFlg.replaceFirst("#", param.getValue("CASE_NO"));
                }
                if (!param.getValue("ORDER_NO").equals("")) {
                    bilFlg = bilFlg.replaceFirst("#", param.getValue("ORDER_NO"));
                }
                if (!param.getValue("SEQ_NO").equals("")) {
                    bilFlg = bilFlg.replaceFirst("#", param.getValue("SEQ_NO"));
                }
                if (!param.getValue("ORDER_CODE").equals("")) {
                    bilFlg = bilFlg.replaceFirst("#", param.getValue("ORDER_CODE"));
                }
                TParm bilFLgParm = new TParm(TJDODBTool.getInstance().select(bilFlg));
                if (bilFLgParm.getErrCode() < 0) {
                    return bilFLgParm;
                }
                if (bilFLgParm.getCount() < 0) {
                    TParm errParm = new TParm();
                    errParm.setErr(-1, "未查询到医嘱 " + param.getValue("ORDER_DESC"));
                    return errParm;
                }
                if (bilFLgParm.getValue("BILL_FLG", 0).equals("Y")) {
                    errCount++;
                    errStr += param.getValue("ORDER_DESC") + ",";
                    continue;
                }
                orderSQL =
                        "UPDATE OPD_ORDER SET EXEC_FLG = 'N', EXEC_DATE='', EXEC_DR_CODE='', EXEC_DR_DESC='' "//wanglong modify 20140514
                                + "WHERE (CASE_NO, RX_NO, ORDERSET_GROUP_NO, ORDERSET_CODE) "
                                + "IN (SELECT CASE_NO, RX_NO, ORDERSET_GROUP_NO, ORDERSET_CODE "
                                + " FROM OPD_ORDER "
                                + "WHERE CASE_NO='#' AND RX_NO='#' AND SEQ_NO=# AND ORDERSET_CODE='#')";
                if (!param.getValue("CASE_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("CASE_NO"));
                }
                if (!param.getValue("ORDER_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_NO"));
                }
                if (!param.getValue("SEQ_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("SEQ_NO"));
                }
                if (!param.getValue("ORDER_CODE").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_CODE"));
                }
            } else if (param.getValue("ADM_TYPE").equals("I")) {
                orderSQL =
                        "UPDATE ODI_ORDER SET EXEC_FLG = 'N' WHERE (CASE_NO, ORDER_NO,ORDERSET_GROUP_NO, ORDERSET_CODE) "
                                + "IN (SELECT CASE_NO,ORDER_NO, ORDERSET_GROUP_NO, ORDERSET_CODE "
                                + " FROM ODI_ORDER "
                                + "WHERE CASE_NO='#' AND ORDER_NO='#' AND ORDER_SEQ=# AND ORDERSET_CODE='#')";
                if (!param.getValue("CASE_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("CASE_NO"));
                }
                if (!param.getValue("ORDER_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_NO"));
                }
                if (!param.getValue("SEQ_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("SEQ_NO"));
                }
                if (!param.getValue("ORDER_CODE").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_CODE"));
                }
            } else if (param.getValue("ADM_TYPE").equals("H")) {
                orderSQL =
                        "UPDATE HRM_ORDER SET EXEC_FLG = 'N', EXEC_DR_CODE='', EXEC_DR_DESC='' "//wanglong modify 20140514
                                + " WHERE (CASE_NO, ORDERSET_GROUP_NO, ORDERSET_CODE) "
                                + "IN (SELECT CASE_NO, ORDERSET_GROUP_NO, ORDERSET_CODE "
                                + " FROM HRM_ORDER "
                                + "WHERE CASE_NO='#' AND SEQ_NO=# AND ORDERSET_CODE='#')";  
                if (!param.getValue("CASE_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("CASE_NO"));
                }
                if (!param.getValue("SEQ_NO").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("SEQ_NO"));
                }
                if (!param.getValue("ORDER_CODE").equals("")) {
                    orderSQL = orderSQL.replaceFirst("#", param.getValue("ORDER_CODE"));
                }
            }
            String medSQL =
                    "UPDATE MED_APPLY SET STATUS='5',OPT_DATE=SYSDATE "
                            + " WHERE APPLICATION_NO='#' AND ORDER_NO='#' AND SEQ_NO='#'";
            if (!param.getValue("APPLICATION_NO").equals("")) {
                medSQL = medSQL.replaceFirst("#", param.getValue("APPLICATION_NO"));
            }
            if (!param.getValue("ORDER_NO").equals("")) {
                medSQL = medSQL.replaceFirst("#", param.getValue("ORDER_NO"));
            }
            if (!param.getValue("SEQ_NO").equals("")) {
                medSQL = medSQL.replaceFirst("#", param.getValue("SEQ_NO"));
            }
            if (i == 0) {
                sql = new String[]{orderSQL, medSQL };
            } else sql = StringTool.copyArray(sql, new String[]{orderSQL, medSQL });
        }
        if (sql.length <= 0) {
            if (errStr.length() == 0) {
                result.setErr(-1, "没有需要操作的数据");
                return result;
            }
            if (errStr.length() > 0) {
                errStr = errStr.substring(0, errStr.length() - 1);
                result.setData("ERR_COUNT", errCount);
                result.setData("ERR_MSG", errStr);
                return result;
            }
        }
        TParm inParm = new TParm();
        Map inMap = new HashMap();
        inMap.put("SQL", sql);
        inParm.setData("IN_MAP", inMap);
        if (parm.getValue("ADM_TYPE", 0).equals("I")) {// add by wanglong 20131209
            inParm.setData("ORDER", parm.getData());
            inParm.setData("TYPE", "UNADD");
            inParm.setData("ADM_TYPE", "I");
        }
        inParm.setData("MED_APPLY_LUMP_FLG","Y");//pangben 2016-9-12 套餐病患取消报道需要手动操作退费
        result = TIOM_AppServer.executeAction("action.med.MedAction", "onUpdate", inParm);
        if (errStr.length() > 0) {// wanglong add 20140606
            errStr = errStr.substring(0, errStr.length() - 1);
            result.setData("ERR_COUNT", errCount);
            result.setData("ERR_MSG", errStr);
        }
        return result;
    }

    /**
     * 返回集合医嘱细项
     * 
     * @param parm
     * @return
     */
    public TParm getOrderSet(TParm parm) {
        // ORDER_DESC;SPECIFICATION;DOSAGE_QTY;MEDI_UNIT;OWN_PRICE;OWN_AMT;EXEC_DEPT_CODE;OPTITEM_CODE;INSPAY_TYPE
        String sql = "";
        if (parm.getValue("ADM_TYPE").equals("O") || parm.getValue("ADM_TYPE").equals("E")) {
            sql =
                    "SELECT ORDER_DESC,SPECIFICATION,DOSAGE_QTY,DOSAGE_UNIT MEDI_UNIT,"
                            + "OWN_PRICE,OWN_AMT,EXEC_DEPT_CODE,OPTITEM_CODE,INSPAY_TYPE "
                            + " FROM OPD_ORDER WHERE CASE_NO='#' AND RX_NO='@' AND SETMAIN_FLG='N' "
                            + " AND ORDERSET_GROUP_NO IN (SELECT ORDERSET_GROUP_NO FROM OPD_ORDER WHERE CASE_NO='#' AND RX_NO='@' AND SEQ_NO='$')";
            if (!parm.getValue("CASE_NO").equals("")) {
                sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
                sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
            }
            if (!parm.getValue("ORDER_NO").equals("")) {
                sql = sql.replaceFirst("@", parm.getValue("ORDER_NO"));
                sql = sql.replaceFirst("@", parm.getValue("ORDER_NO"));
            }
            if (!parm.getValue("SEQ_NO").equals("")) {
                sql = sql.replaceFirst("\\$", parm.getValue("SEQ_NO"));
            }
        } else if (parm.getValue("ADM_TYPE").equals("I")) {
            sql =
                    "SELECT A.ORDER_DESC,A.SPECIFICATION,A.DOSAGE_QTY,A.DOSAGE_UNIT MEDI_UNIT,"
                            + "A.OWN_PRICE,A.OWN_AMT,A.EXEC_DEPT_CODE,A.OPTITEM_CODE,B.INSPAY_TYPE "
                            + " FROM ODI_DSPNM A,SYS_FEE B "
                            + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                            + " AND A.CASE_NO='#' AND A.ORDER_NO='@' AND A.SETMAIN_FLG='N' "
                            + " AND A.ORDERSET_GROUP_NO IN (SELECT ORDERSET_GROUP_NO FROM ODI_DSPNM WHERE CASE_NO='#' AND ORDER_NO='@' AND ORDER_SEQ='$')";
            if (!parm.getValue("CASE_NO").equals("")) {
                sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
                sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
            }
            if (!parm.getValue("ORDER_NO").equals("")) {
                sql = sql.replaceFirst("@", parm.getValue("ORDER_NO"));
                sql = sql.replaceFirst("@", parm.getValue("ORDER_NO"));
            }
            if (!parm.getValue("SEQ_NO").equals("")) {
                sql = sql.replaceFirst("\\$", parm.getValue("SEQ_NO"));
            }
        } else if (parm.getValue("ADM_TYPE").equals("H")) {
            sql =
                    "SELECT ORDER_DESC,SPECIFICATION,DISPENSE_QTY DOSAGE_QTY,DISPENSE_UNIT MEDI_UNIT,"
                            + "OWN_PRICE,OWN_AMT,EXEC_DEPT_CODE,OPTITEM_CODE,INSPAY_TYPE "
                            + " FROM HRM_ORDER WHERE CASE_NO='#' AND SETMAIN_FLG='N' "
                            + " AND ORDERSET_GROUP_NO IN (SELECT ORDERSET_GROUP_NO FROM HRM_ORDER WHERE CASE_NO='#' AND SEQ_NO='@')";
            if (!parm.getValue("CASE_NO").equals("")) {
                sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
                sql = sql.replaceFirst("#", parm.getValue("CASE_NO"));
            }
            if (!parm.getValue("SEQ_NO").equals("")) {
                sql = sql.replaceFirst("@", parm.getValue("SEQ_NO"));
            }
        }
        TParm result = new TParm(this.select(sql));
        return result;
    }

    /**
     * 保存
     * 
     * @param parm
     * @param conn
     * @return
     */
    public TParm onSave(TParm parm, TConnection conn) {
        TParm result = new TParm();
        Map inMap = (HashMap) parm.getData("IN_MAP");
        String[] sql = (String[]) inMap.get("SQL");
        if (sql == null) {
            return result;
        }
        if (sql.length < 1) {
            return result;
        }
        for (String tempSql : sql) {
            result = new TParm(TJDODBTool.getInstance().update(tempSql, conn));
            if (result.getErrCode() != 0) {
                return result;
            }
        }
        return result;
    }
    
}
