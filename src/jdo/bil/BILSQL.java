package jdo.bil;

import com.dongyang.data.TParm;

/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class BILSQL {
    /**
     * 得到执行科室查询统计sql
     * @return String
     */
    public static String getExeDeptFeeQuerySql() {
        String sql =
                "SELECT D.STATION_DESC,B.MR_NO,B.CASE_NO,B.IPD_NO,C.PAT_NAME,A.ITEM_CODE," +
                " E.ORDER_DESC,G.DEPT_DESC,A.OWN_PRICE,A.CHARGE_T,A.TOT_AMT,F.USER_NAME,A.OPT_DATE" +
                " FROM IBS_ORDD A,ADM_INP B,SYS_PATINFO C,SYS_STATION D,SYS_FEE E,SYS_OPERATOR F,SYS_DEPT G " +
                " WHERE A.CASE_NO=B.CASE_NO " +
                " AND B.MR_NO=C.MR_NO " +
                " AND A.EXE_DEPT_CODE=G.DEPT_CODE" +
                " AND A.OPT_USER=F.USER_ID" +
                " AND A.DEPT_CODE=D.STATION_CODE(+) " +
                " AND A.ITEM_CODE=E.ORDER_CODE " +
                " AND (A.BILL_DATE BETWEEN E.START_DATE AND E.END_DATE) ";
        return sql;
    }

    public static String getAllOperator() {
        String sql = "";
        return sql;
    }

    /**
     *
     * @param parm TParm
     * @return String
     */
    public static String getPationSql(TParm parm) {
        String sql = "SELECT A.MR_NO, A.IPD_NO, A.CASE_NO,A.DS_DATE, A.IN_DATE, B.PAT_NAME, B.SEX_CODE" +
                     " FROM ADM_INP A, SYS_PATINFO B " +
                     " WHERE A.MR_NO = B.MR_NO " +
                     " AND A.CANCEL_FLG <> 'Y' ";
        //病案号
        String value = parm.getValue("MR_NO");
        if (value != null && value.length() != 0)
            sql += " AND A.MR_NO='" + value + "'";
        value = parm.getValue("IPD_NO");
        if (value != null && value.length() != 0)
            sql += " AND A.IPD_NO='" + value + "'";
        value = parm.getValue("CASE_NO");
        if (value != null && value.length() != 0)
            sql += " AND A.CASE_NO ='" + value + "'";
        value = parm.getValue("IN");
        if (value == null || value.length() == 0 || value.equals("N"))
            sql += " AND A.DS_DATE IS NOT NULL";
        else
            sql += " AND A.DS_DATE IS NULL";
        return sql;
    }

    /**
     * 查找日结数据的sql
     * @param parm TParm
     * @return String
     */
    public static String getAccountSql(TParm parm) {
        String sql = "";
        if (parm == null)
            return sql;
        //=====pangben modify 20110414
        //===zhangp 20120308 modify start
        sql = "SELECT 'N' S,B.REGION_CHN_ABN AS REGION_CHN_DESC,A.ACCOUNT_SEQ,A.ACCOUNT_DATE,A.ACCOUNT_USER,A.AR_AMT," +
              //==zhangp 20120308 modify end
              "       A.STATUS,A.INVALID_COUNT " +
              "  FROM BIL_ACCOUNT A, SYS_REGION B " +
              "WHERE A.REGION_CODE=B.REGION_CODE AND A.ACCOUNT_TYPE = 'OPB' AND ";
        //=================pangben modify 20110406 start 添加区域查询条件
        String region = parm.getValue("REGION_CODE");
        if (region != null && !region.trim().equals(""))
            sql += " A.REGION_CODE='" + region + "' AND ";
        //=================pangben modify 20110406 stop
        String value = parm.getValue("ACCOUNT_USER");
        if (value != null && !value.equals(""))
            sql += " A.ACCOUNT_USER='" + value + "' AND ";
        String admType = parm.getValue("ADM_TYPE");
        if (admType != null && !admType.equals(""))
            sql += " A.ADM_TYPE='" + admType + "' AND ";
//        String dispenseDate = StringTool.getString(parm.getTimestamp(
//            "ACCOUNT_DATE"), "yyyyMMddHHmmss");
//        String dispenseDateE = StringTool.getString(parm.getTimestamp(
//            "ACCOUNT_DATEE"), "yyyyMMddHHmmss");
        //============pangben modify 20110414 start
        sql += " A.ACCOUNT_DATE BETWEEN TO_DATE('" + parm.getValue(
                "ACCOUNT_DATE") +
                "','YYYYMMDDHH24MISS')" +
                " AND TO_DATE('" + parm.getValue(
                        "ACCOUNT_DATEE") + "','YYYYMMDDHH24MISS')";
        //============pangben modify 20110414 start
        sql += " ORDER BY B.REGION_CHN_DESC, A.ACCOUNT_SEQ";
        return sql;
    }

    /**
     * 门诊收费批量打印
     * @param parm TParm
     * @return String
     */
    public static String getOPBPatchPrintSql(TParm parm) {
        String sql = "";
        if (parm == null)
            return sql;
        sql =
                "  SELECT '' AS FLG, TO_CHAR (F.ADM_DATE, 'YYYY/MM/DD') ADM_DATE," +
                "         F.SESSION_CODE, F.REALDEPT_CODE, F.REALDR_CODE, F.QUE_NO, F.MR_NO," +
                "         SUM (A.AR_AMT) AS AR_AMT, F.CASE_NO, G.PAT_NAME,G.IDNO,F.CTZ1_CODE " +
                "    FROM OPD_ORDER A, EKT_ACCNTDETAIL D, REG_PATADM F, SYS_PATINFO G" +
                "   WHERE A.BUSINESS_NO = D.BUSINESS_NO" +
                "     AND A.RX_NO = D.RX_NO" +
                "     AND A.SEQ_NO = D.SEQ_NO" +
                "     AND A.BILL_FLG = 'Y'" +
                "     AND A.BILL_TYPE = 'E'" +
                "     AND A.RELEASE_FLG <> 'Y'" +
                "     AND A.RECEIPT_NO IS NULL" +
                "     AND (A.BUSINESS_NO IS NOT NULL OR A.BUSINESS_NO <> '')" +
                "     AND (A.PRINT_FLG IS NULL OR A.PRINT_FLG = 'N' OR A.PRINT_FLG = '')" +
                "     AND D.CHARGE_FLG = '1'" +
                "     AND F.MR_NO = G.MR_NO" +
                "     AND A.MR_NO = G.MR_NO" +
                "     AND F.CASE_NO = A.CASE_NO" ;

        String admType = parm.getValue("ADM_TYPE");
        if (admType != null && !admType.equals(""))
            sql += " AND F.ADM_TYPE='" + admType + "' ";
        String clinicArea = parm.getValue("CLINIC_AREA");
        if (clinicArea != null && !clinicArea.equals(""))
            sql += " AND F.CLINICAREA_CODE='" + clinicArea + "' ";
        String sessionCode = parm.getValue("SESSION_CODE");
        if (sessionCode != null && !sessionCode.equals(""))
            sql += " AND F.SESSION_CODE='" + sessionCode + "' ";
        String realDeptCode = parm.getValue("REALDEPT_CODE");
        if (realDeptCode != null && !realDeptCode.equals(""))
            sql += " AND F.REALDEPT_CODE='" + realDeptCode + "' ";
        String realDrCode = parm.getValue("REALDR_CODE");
        if (realDrCode != null && !realDrCode.equals(""))
            sql += " AND F.REALDR_CODE='" + realDrCode + "' ";
        //=================pangben modify 20110407 start 添加区域查询条件
        String region = parm.getValue("REGION_CODE");
        if (region != null && !region.trim().equals(""))
            sql += " AND F.REGION_CODE='" + region + "' ";
        //=================pangben modify 20110407 stop

        sql +=
                "GROUP BY F.ADM_DATE," +
                "         F.SESSION_CODE," +
                "         F.REALDEPT_CODE," +
                "         F.REALDR_CODE," +
                "         F.QUE_NO," +
                "         F.MR_NO," +
                "         G.PAT_NAME," +
                "         F.CASE_NO," +
                "         G.IDNO," +
                "         F.CTZ1_CODE "+
                " ORDER BY F.CASE_NO";
        return sql;
    }

    /**
     * 得到日结票据
     * @param accountSeq String
     * @return String
     */
    public static String getReceiptParm(String accountSeq) {
        if (accountSeq == null || accountSeq.equals(""))
            return "";
        String sql =
                "SELECT CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO," +
                "       RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE," +
                "       PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04," +
                "       CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09," +
                "       CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14," +
                "       CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19," +
                "       CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24," +
                "       CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29," +
                "       CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT," +
                "       REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND," +
                "       AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD," +
                "       PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,PAY_DRAFT, " +
                "       PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK," +
                "       CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM,ALIPAY " +
                "  FROM BIL_OPB_RECP " +
                " WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
        return sql;
    }
    
    /**
     * 得到日结票据(健检日结专用)
     * @param accountSeq String
     * @return String
     */
    public static String getReceiptParm2(String accountSeq) {//add by wanglong 20130927
        if (accountSeq == null || accountSeq.equals(""))
            return "";
        String sql =
                "SELECT A.CASE_NO,A.RECEIPT_NO,A.ADM_TYPE,A.REGION_CODE,A.MR_NO," +
                "       A.RESET_RECEIPT_NO,A.PRINT_NO,A.BILL_DATE,A.CHARGE_DATE," +
                "       A.PRINT_DATE,A.CHARGE01,A.CHARGE02,A.CHARGE03,A.CHARGE04," +
                "       A.CHARGE05,A.CHARGE06,A.CHARGE07,A.CHARGE08,A.CHARGE09," +
                "       A.CHARGE10,A.CHARGE11,A.CHARGE12,A.CHARGE13,A.CHARGE14," +
                "       A.CHARGE15,A.CHARGE16,A.CHARGE17,A.CHARGE18,A.CHARGE19," +
                "       A.CHARGE20,A.CHARGE21,A.CHARGE22,A.CHARGE23,A.CHARGE24," +
                "       A.CHARGE25,A.CHARGE26,A.CHARGE27,A.CHARGE28,A.CHARGE29," +
                "       A.CHARGE30,A.TOT_AMT,A.REDUCE_REASON,A.REDUCE_AMT," +
                "       A.REDUCE_DATE,A.REDUCE_DEPT_CODE,A.REDUCE_RESPOND," +
                "       A.AR_AMT,A.PAY_CASH,A.PAY_MEDICAL_CARD,A.PAY_BANK_CARD," +
                "       A.PAY_INS_CARD,A.PAY_CHECK,A.PAY_DEBIT,A.PAY_BILPAY,A.PAY_DRAFT, " +
                "       A.PAY_INS,A.PAY_OTHER1,A.PAY_OTHER2,A.PAY_REMARK," +
                "       A.CASHIER_CODE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM " +
                "       ,B.CANCEL_FLG " +
                "  FROM BIL_OPB_RECP A, BIL_INVRCP B" +
                //" WHERE A.PRINT_NO = B.INV_NO AND B.RECP_TYPE = 'OPB' AND A.ACCOUNT_SEQ = B.ACCOUNT_SEQ " +
                " WHERE A.PRINT_NO = B.INV_NO AND B.RECP_TYPE = 'OPB' " +
                "   AND A.ACCOUNT_SEQ IN (" + accountSeq + ")";
        return sql;
    }

    /**
     * 得到日结票号
     * @param accountSeq String
     * @param recpType String
     * @param admType String
     * @return String
     */
    public static String getPrintNo(String accountSeq, String recpType,
                                    String admType) {
        if (accountSeq == null || accountSeq.length() <= 0)
            return "";
        String sql =
                //==zhangp 20120319 start
//            " SELECT INV_NO FROM BIL_INVRCP " +
//            "  WHERE ACCOUNT_SEQ IN(" + accountSeq + ") " +
//            "    AND RECP_TYPE = '" + recpType + "' ";
//            if(!admType.equals("")&&admType==null){
//              sql+= "    AND ADM_TYPE = '"+admType+"' ";
//            }
//            sql+=" ORDER BY INV_NO";
            //===zhangp 20120327 start
                " SELECT INV_NO AS PRINT_NO FROM BIL_INVRCP " +
                "  WHERE ACCOUNT_SEQ IN(" + accountSeq + ") " +
                        " AND RECP_TYPE = '" + recpType + "' ";
//        " SELECT PRINT_NO FROM BIL_OPB_RECP " +
//        "  WHERE ACCOUNT_SEQ IN(" + accountSeq + ") ";
        //===zhangp 20120327 end
        if (!admType.equals("") && admType == null) {
            sql += "    AND ADM_TYPE = '" + admType + "' ";
        }
        //===zhangp 20120327 start
//        sql += " ORDER BY PRINT_NO";
        sql += " ORDER BY INV_NO";
        //===zhangp 20120327 end
        //===zhangp 20120319 end
        //=====20120218 zhangp modify end
        // System.out.println("sql"+sql);
        return sql;
    }

    /**
     * 得到日结票号
     * @param accountSeq String
     * @param recpType String
     * @param admType String
     * @return String
     */
    public static String getPrintUser(String accountSeq, String recpType,
                                      String admType) {
        if (accountSeq == null || accountSeq.length() <= 0)
            return "";
        //===zhangp 20120319 start
//        String sql = "SELECT CASHIER_CODE FROM BIL_INVRCP " +
//            " WHERE ACCOUNT_SEQ IN(" + accountSeq + ")"+
//            //===ZHANGP 20120312 START
//            "   AND ADM_TYPE LIKE '%"+admType+"%' "+
//            //===zhangp 20120312 end
//            "   AND RECP_TYPE = '" + recpType + "' ";
        String sql = "SELECT CASHIER_CODE FROM BIL_OPB_RECP " +
                     " WHERE ACCOUNT_SEQ IN(" + accountSeq + ")" +
                     //===ZHANGP 20120312 START
                     "   AND ADM_TYPE = '" + admType + "' ";
        return sql;
    }

    /**
     * 得到作废票号
     * @param accountSeq String
     * @param admType String
     * @return String
     */
    public static String getTearPringNo(String accountSeq, String admType) {
        if (accountSeq == null || accountSeq.length() <= 0)
            return "";
        String sql =
                " SELECT INV_NO,AR_AMT FROM BIL_INVRCP " +
                "  WHERE ACCOUNT_SEQ IN(" + accountSeq + ")" +
                "    AND RECP_TYPE = 'OPB' " +
                //===zhangp 20120312 start
                "    AND ADM_TYPE LIKE '%" + admType + "%' " +
                //===zhangp 20120312 end
                "    AND CANCEL_FLG IN('2','3')";
        return sql;
    }

    /**
     * 得到退费票据票号
     * @param accountSeq String
     * @return String
     */
    public static String getBackPrintNo(String accountSeq) {
        if (accountSeq == null || accountSeq.length() <= 0)
            return "";
        String sql =
                "SELECT PRINT_NO,AR_AMT FROM BIL_OPB_RECP " +
                " WHERE ACCOUNT_SEQ IN(" + accountSeq + ") " +
                "   AND AR_AMT < 0 " +
                //====zhangp 20120306 modify start
                " AND (PRINT_NO IS NOT NULL OR PRINT_NO <> '')";
        //====zhangp 20120306 modify end
        return sql;
    }

    /**
     * 得到退费票据票号(日结前)
     * @param parm TParm
     * @return String
     */
    public static String getBackPrintNo(TParm parm) {
        if (parm == null)
            return "";
        String sql =
                "SELECT PRINT_NO,AR_AMT FROM BIL_OPB_RECP " +
                " WHERE PRINT_DATE<TO_DATE('" + parm.getValue("PRINT_DATE") +
                "','YYYYMMDDHH24MISS')" +
                "   AND CASHIER_CODE = '" + parm.getValue("CASHIER_CODE") +
                "' " +
                "   AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) " +
                "   AND AR_AMT < 0 " +
                //====zhangp 20120305 modify start
                " AND (PRINT_NO IS NOT NULL OR PRINT_NO <> '') " +
                " AND ADM_TYPE = '" + parm.getValue("ADM_TYPE") + "'";
        //======zhangp 20120305 modify end
        return sql;
    }

    /**
     * 得到退费票据票号(日结前) o e h
     * @param parm TParm
     * @return String
     */
    public static String getBackPrintNoAll(TParm parm) {
        if (parm == null)
            return "";
        String sql =
                "SELECT PRINT_NO,AR_AMT FROM BIL_OPB_RECP " +
                " WHERE PRINT_DATE<TO_DATE('" + parm.getValue("PRINT_DATE") +
                "','YYYYMMDDHH24MISS')" +
                "   AND CASHIER_CODE = '" + parm.getValue("CASHIER_CODE") +
                "' " +
                "   AND (ACCOUNT_FLG='N' OR ACCOUNT_FLG IS NULL) " +
                "   AND AR_AMT < 0 " +
                //====zhangp 20120305 modify start
                " AND (PRINT_NO IS NOT NULL OR PRINT_NO <> '') ";
        //======zhangp 20120305 modify end
        return sql;
    }

    /**
     * 根据护士站代码查找护士站数据
     * @param stationCode String
     * @return String
     */
    public static String getStationSql(String stationCode) {
        String sql = "";
        if (stationCode == null || stationCode.length() <= 0)
            return sql;
        sql = "SELECT * FROM SYS_STATION WHERE STATION_CODE='" + stationCode +
              "'";

        return sql;
    }

    /**
     * 根据房间号查找房间数据
     * @param roomCode String
     * @return String
     */
    public static String getRoomSql(String roomCode) {
        String sql = "";
        if (roomCode == null || roomCode.length() <= 0)
            return sql;
        sql = "SELECT * FROM SYS_ROOM WHERE ROOM_CODE='" + roomCode + "'";
        return sql;
    }

    /**
     * 根据房间号查找房间数据
     * @param bedCode String
     * @return String
     */
    public static String getBedSql(String bedCode) {
        String sql = "";
        if (bedCode == null || bedCode.length() <= 0)
            return sql;
        sql = "SELECT * FROM SYS_BED WHERE BED_NO='" + bedCode + "'";
        return sql;
    }

    /**
     * 查找护士站名称是否重复
     * @param stationDesc String
     * @return String
     */
    public static String getStationDescSql(String stationDesc) {
        String sql = "";
        if (stationDesc == null || stationDesc.length() <= 0)
            return sql;
        sql = "SELECT STATION_DESC FROM SYS_STATION WHERE STATION_DESC='" +
              stationDesc + "'";

        return sql;
    }

    /**
     * 查找房间名称是否重复
     * @param roomDesc String
     * @return String
     */
    public static String getRoomDescSql(String roomDesc) {
        String sql = "";
        if (roomDesc == null || roomDesc.length() <= 0)
            return sql;
        sql = "SELECT ROOM_DESC FROM SYS_ROOM WHERE ROOM_DESC='" + roomDesc +
              "'";
        return sql;
    }

    /**
     * 查找床位名称是否重复
     * @param bedNoDesc String
     * @return String
     */
    public static String getBedNoDescSql(String bedNoDesc) {
        String sql = "";
        if (bedNoDesc == null || bedNoDesc.length() <= 0)
            return sql;
        sql = "SELECT BED_NO_DESC FROM SYS_BED WHERE BED_NO_DESC='" + bedNoDesc +
              "'";
        return sql;
    }

    /**
     * 票据信息(门诊明细)pangben modify 20110704 添加参数
     * @param sTime String
     * @param eTime String
     * @param regionCode String
     * @return String
     */
    public static String getRecpDataO(String sTime, String eTime,
                                      String regionCode) {
        //=========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else
            region.append("");
        //外层数据过滤
        StringBuffer regionTemp = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            regionTemp.append(" AND D.REGION_CODE='" + regionCode + "' ");
        } else
            regionTemp.append("");
        //=========pangben modify 20110704 stop
        String sql =
                " SELECT   C.INV_NO, TO_CHAR (C.PRINT_DATE," +
                "        'yyyy/MM/dd HH24:mm:ss') AS PRINT_DATE," +
                "        D.RECEIPT_NO," +
                "        CASE " +
                "           WHEN C.STATUS = '2' " +
                "            THEN '调整票号' " +
                "           WHEN C.cancel_flg = '1' " +
                "            THEN '作废' " +
                "           WHEN C.cancel_flg = '3' " +
                "            THEN '作废' " +
                "           WHEN C.STATUS = '0' " +
                "            THEN '正常' " +
                "           END AS CANCEL_FLG, " +
                "        D.PAT_NAME,D.TOT_AMT, " +
                "        D.CHARGE01+D.CHARGE02 AS CHARGE0102, D.CHARGE03, D.CHARGE04, D.CHARGE05," + // modify by wanglong 20130110
                "        D.CHARGE10, D.CHARGE06, D.CHARGE07, D.CHARGE08, D.CHARGE09, " +
                "        D.CHARGE18, D.CHARGE13, D.CHARGE14, D.CHARGE15," +
                "        D.CHARGE17, D.CHARGE16 ,D.CHARGE11, D.CHARGE12, D.CHARGE19 " +
                "    FROM (SELECT A.RECEIPT_NO, B.PAT_NAME, A.TOT_AMT, A.CHARGE01, A.CHARGE02," +
                "                 A.CHARGE03, A.CHARGE04, A.CHARGE05, A.CHARGE06, A.CHARGE07," +
                "                 A.CHARGE08, A.CHARGE09, A.CHARGE10, A.CHARGE11, A.CHARGE12," +
                "                 A.CHARGE13, A.CHARGE14, A.CHARGE15, A.CHARGE16, A.CHARGE17," +
                "                 A.CHARGE18, A.CHARGE19,A.REGION_CODE " +
                "            FROM BIL_OPB_RECP A, SYS_PATINFO B " +
                "           WHERE A.MR_NO = B.MR_NO(+)" +
                " AND A.ADM_TYPE <> 'H' "//add by wanglong 20130109
                + region +
                ") D,BIL_INVRCP C" +
                "   WHERE C.RECEIPT_NO = D.RECEIPT_NO(+) " +
                "     AND C.RECP_TYPE = 'OPB' " +
                "     AND C.ADM_TYPE <> 'H' " +
                "     AND C.PRINT_DATE BETWEEN TO_DATE('" + sTime +
                "','yyyyMMddHH24MISS') " +
                "                          AND TO_DATE('" + eTime +
                "','yyyyMMddHH24MISS') " + regionTemp +
                "  ORDER BY C.INV_NO ";
        return sql;
    }

    /**
     * 票据信息(健检明细)add by wanglong 20130109
     * @param sTime String
     * @param eTime String
     * @param regionCode String
     * @return String
     */
    public static String getRecpDataH(String sTime, String eTime, String regionCode) {
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else region.append("");
        String sql =
                "SELECT DISTINCT B.INV_NO,TO_CHAR( B.PRINT_DATE, 'yyyy/MM/dd HH24:mm:ss') AS PRINT_DATE,"
                        + "A.RECEIPT_NO, CASE WHEN B.STATUS = '2' THEN '调整票号' WHEN B.CANCEL_FLG = '1' THEN '作废' "
                        + " WHEN B.CANCEL_FLG = '3' THEN '作废' WHEN B.STATUS = '0' THEN '正常' END AS CANCEL_FLG,"
                        + "D.COMPANY_DESC,C.PAT_NAME,A.AR_AMT TOT_AMT, " //modify by wanglong 20130502
                        + "A.CHARGE01+A.CHARGE02 AS CHARGE0102, A.CHARGE03, A.CHARGE04, A.CHARGE05,"// modify by wanglong 20130110
                        + "A.CHARGE10, A.CHARGE06, A.CHARGE07, A.CHARGE08, A.CHARGE09,"
                        + "A.CHARGE18, A.CHARGE13, A.CHARGE14, A.CHARGE15,"
                        + "A.CHARGE17, A.CHARGE16, A.CHARGE11, A.CHARGE12, A.CHARGE19 "
                        + " FROM BIL_OPB_RECP A, BIL_INVRCP B, SYS_PATINFO C, HRM_COMPANY D "
                        + " WHERE A.RECEIPT_NO(+) = B.RECEIPT_NO " + " AND A.ADM_TYPE(+) = B.ADM_TYPE "//modify by wanglong 20130120
                        + " AND B.ADM_TYPE = 'H' " + " AND A.MR_NO = C.MR_NO(+) "//modify by wanglong 20130120
                        + " AND B.RECP_TYPE = 'OPB' " + " AND B.PRINT_DATE BETWEEN TO_DATE( '"
                        + sTime + "', 'yyyyMMddHH24MISS') " + " AND TO_DATE( '" + eTime
                        + "', 'yyyyMMddHH24MISS') " + " AND A.MR_NO = D.COMPANY_CODE(+) " + region
                        + " ORDER BY B.INV_NO";
        return sql;
    }
    /**
     * 票据信息(住院明细)pangben modify 20110704 添加参数
     * @param sTime String
     * @param eTime String
     * @param regionCode String
     * @return String
     */
    public static String getRecpDataI(String sTime, String eTime,
                                      String regionCode) {
        //=========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else
            region.append("");
        //=========pangben modify 20110704 stop

        String sql =
                " SELECT   C.INV_NO, C.PRINT_DATE, A.RECEIPT_NO, " +
                "          CASE " +
                "           WHEN C.STATUS = '2' " +
                "            THEN '调整票号' " +
                "           WHEN C.cancel_flg = '1' " +
                "            THEN '作废' " +
                "           WHEN C.cancel_flg = '3' " +
                "            THEN '作废' " +
                "           WHEN C.STATUS = '0' " +
                "            THEN '正常' " +
                "          END AS CANCEL_FLG, D.PAT_NAME, B.REXP_CODE, B.WRT_OFF_AMT " +
                "   FROM BIL_IBS_RECPM A, BIL_IBS_RECPD B, BIL_INVRCP C, SYS_PATINFO D " +
                "  WHERE A.RECEIPT_NO = B.RECEIPT_NO " +
                "    AND C.RECEIPT_NO = A.RECEIPT_NO " +
//            "    AND A.PRINT_NO = C.INV_NO "+
                "    AND C.RECP_TYPE = 'IBS' " +
                "    AND A.MR_NO = D.MR_NO " + region +
                "    AND C.PRINT_DATE BETWEEN TO_DATE('" + sTime +
                "','yyyyMMddHH24MISS') " +
                "                     AND TO_DATE('" + eTime +
                "','yyyyMMddHH24MISS') " +
                "  ORDER BY C.INV_NO ";
        return sql;
    }

    /**
     * 票据信息(挂号明细)add by wanglong 20130120
     * @param sTime String
     * @param eTime String
     * @param regionCode String
     * @return String
     */
    public static String getRecpDataR(String sTime, String eTime, String regionCode) {
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else region.append("");
        String sql =
                "SELECT DISTINCT B.INV_NO, TO_CHAR( B.PRINT_DATE, 'yyyy/MM/dd HH24:mm:ss') AS PRINT_DATE,"
                        + " A.RECEIPT_NO, CASE WHEN B.STATUS = '2' THEN '调整票号' WHEN B.CANCEL_FLG = '1' THEN '作废'"
                        + " WHEN B.CANCEL_FLG = '3' THEN '作废' WHEN B.STATUS = '0' THEN '正常' END AS CANCEL_FLG,"
                        + " C.PAT_NAME, A.AR_AMT, A.REG_FEE_REAL, A.CLINIC_FEE_REAL, A.SPC_FEE, A.OTHER_FEE1, A.OTHER_FEE2, A.OTHER_FEE3 "
                        + " FROM BIL_REG_RECP A, BIL_INVRCP B, SYS_PATINFO C "
                        + " WHERE A.RECEIPT_NO(+) = B.RECEIPT_NO "
                        + "   AND A.ADM_TYPE(+) = B.ADM_TYPE     " 
                        + "   AND A.MR_NO = C.MR_NO(+)        "
                        + "   AND B.RECP_TYPE = 'REG'         "
                        + "   AND B.PRINT_DATE BETWEEN TO_DATE( '" + sTime
                        + "', 'yyyyMMddHH24MISS') " + " AND TO_DATE( '"
                        + eTime + "', 'yyyyMMddHH24MISS') " + region + " ORDER BY B.INV_NO";
        return sql;
    }
    
    /**
     * 票据信息(总表)
     * @param sTime String
     * @param eTime String
     * @param recpType String
     * @param regionCode String
     * @return String
     */
    public static String getRecpDataAll(String sTime, String eTime,
                                        String recpType, String regionCode) {
        String admTyp = "";// add by wanglong 20130120
        
      //=========huangtt modify 20130926 start
    	if(recpType.equals("OPB")){
    		admTyp = "AND ADM_TYPE <> 'H'";
    	}
        //=========huangtt modify 20130926 end
        
        
        if (recpType.equals("HRM")) {// add by wanglong 20130120
            recpType = "OPB";
            admTyp = " AND ADM_TYPE='H' ";
        }
        //=========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND REGION_CODE='" + regionCode + "' ");
        } else
            region.append("");
        //=========pangben modify 20110704 stop
        String sql =
                " SELECT INV_NO FROM BIL_INVRCP " +
                "  WHERE RECP_TYPE = '" + recpType + "' " + region + admTyp + //modify by wanglong 20130120
                "    AND PRINT_DATE BETWEEN TO_DATE('" + sTime +
                "','yyyyMMddHH24MISS') " +
                "                       AND TO_DATE('" + eTime +
                "','yyyyMMddHH24MISS') ";
        return sql;
    }

    /**
     * 作废票据信息(总表)
     * @param sTime String
     * @param eTime String
     * @param recpType String
     * @param regionCode String
     * @return String
     */
    public static String getRecpDataLeft(String sTime, String eTime,
                                         String recpType, String regionCode) {
        String admTyp = "";// add by wanglong 20130120
        
        //=========huangtt modify 20130926 start
    	if(recpType.equals("OPB")){
    		admTyp = "AND ADM_TYPE <> 'H'";
    	}
    	//=========huangtt modify 20130926 end
        
        if (recpType.equals("HRM")) {// add by wanglong 20130120
            recpType = "OPB";
            admTyp = " AND ADM_TYPE='H' ";
        }
        //=========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND REGION_CODE='" + regionCode + "' ");
        } else
            region.append("");
        //=========pangben modify 20110704 stop

        String sql =
                " SELECT  INV_NO FROM BIL_INVRCP WHERE RECP_TYPE = '" +
                recpType + "' " +
                "    AND (CANCEL_FLG IN ('1','3') OR STATUS = '2') " + region + admTyp + //modify by wanglong 20130120
                "    AND PRINT_DATE BETWEEN TO_DATE('" + sTime +
                "','yyyyMMddHH24MISS') " +
                "                         AND TO_DATE('" + eTime +
                "','yyyyMMddHH24MISS') ";
        return sql;
    }

    /**
     * 得到票据信息(门诊废票发票)
     * @param sTime String
     * @param eTime String
     * @param recpType String
     * @param regionCode String
     * @return String
     */
    public static String getCancelRecpO(String sTime, String eTime,
                                        String recpType, String regionCode) {
        //=========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else
            region.append("");
        //=========pangben modify 20110704 stop

        String sql =
                " SELECT C.INV_NO, A.RECEIPT_NO,A.AR_AMT," +
                "        CASE " +
                "           WHEN C.STATUS = '2' " +
                "            THEN '调整票号' " +
                "           WHEN C.CANCEL_FLG = '1' " +
                "            THEN '退费' " +
                "           WHEN C.CANCEL_FLG = '3' " +
                "            THEN '补印' " +
                "           END AS CANCEL_FLG " +
                "           FROM BIL_OPB_RECP A, BIL_INVRCP C " +
                "          WHERE C.RECEIPT_NO = A.RECEIPT_NO(+) " +
                "            AND C.RECP_TYPE = '" + recpType + "' " +
                "            AND C.ADM_TYPE <> 'H'" +    //add by huangtt 20130926 
                "            AND (C.CANCEL_FLG IN ('1','3') OR STATUS = '2') " +
                region +
                "            AND C.PRINT_DATE BETWEEN TO_DATE('" + sTime +
                "','yyyyMMddHH24MISS') " +
                "                                 AND TO_DATE('" + eTime +
                "','yyyyMMddHH24MISS') " +
                "  ORDER BY C.INV_NO ";
        return sql;
    }

    /**
     * 得到票据信息(健检废票发票) add by wanglong 20130109
     * @param sTime String
     * @param eTime String
     * @param recpType String
     * @param regionCode String
     * @return String
     */
    public static String getCancelRecpH(String sTime, String eTime, String recpType,
                                        String regionCode) {
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else region.append("");
        String sql =
                "SELECT B.INV_NO, A.RECEIPT_NO, A.AR_AMT,"
                        + " CASE WHEN B.STATUS = '2' THEN '调整票号' WHEN B.CANCEL_FLG = '1' THEN '退费'"
                        + " WHEN B.CANCEL_FLG = '3' THEN '补印' END AS CANCEL_FLG"
                        + " FROM BIL_OPB_RECP A, BIL_INVRCP B"
                        + " WHERE A.RECEIPT_NO = B.RECEIPT_NO" + " AND A.ADM_TYPE=B.ADM_TYPE"
                        + "  AND B.RECP_TYPE = '" + recpType + "' AND A.ADM_TYPE='H'"
                        + " AND (B.CANCEL_FLG IN ('1', '3')" + " OR STATUS = '2')" + region
                        + " AND B.PRINT_DATE BETWEEN TO_DATE( '" + sTime + "', 'yyyyMMddHH24MISS')"
                        + " AND TO_DATE( '" + eTime + "', 'yyyyMMddHH24MISS')"
                        + "ORDER BY B.INV_NO";
        return sql;
    }
    
    /**
     * 得到票据信息(住院废票发票)
     * @param sTime String
     * @param eTime String
     * @param recpType String
     * @param regionCode String
     * @return String
     */
    public static String getCancelRecpI(String sTime, String eTime,
                                        String recpType, String regionCode) {
        //=========pangben modify 20110704 start
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else
            region.append("");
        //=========pangben modify 20110704 stop

        String sql =
                " SELECT C.INV_NO, A.RECEIPT_NO,A.AR_AMT," +
                "        CASE " +
                "           WHEN C.STATUS = '2' " +
                "            THEN '调整票号' " +
                "           WHEN C.cancel_flg = '1' " +
                "            THEN '作废' " +
                "           WHEN C.cancel_flg = '3' " +
                "            THEN '作废' " +
                "           WHEN C.STATUS = '0' " +
                "            THEN '正常' " +
                "           END AS CANCEL_FLG " +
                "           FROM BIL_IBS_RECPM A, BIL_INVRCP C " +
                "          WHERE C.RECEIPT_NO = A.RECEIPT_NO(+) " +
                "            AND C.RECP_TYPE = '" + recpType + "' " +
                "            AND (C.CANCEL_FLG IN ('1','3') OR STATUS = '2') " +
                region +
                "            AND C.PRINT_DATE BETWEEN TO_DATE('" + sTime +
                "','yyyyMMddHH24MISS') " +
                "                                 AND TO_DATE('" + eTime +
                "','yyyyMMddHH24MISS') " +
                "  ORDER BY C.INV_NO ";
        return sql;
    }

    /**
     * 得到票据信息(健检废票发票) add by wanglong 20130120
     * @param sTime String
     * @param eTime String
     * @param recpType String
     * @param regionCode String
     * @return String
     */
    public static String getCancelRecpR(String sTime, String eTime, String recpType,
                                        String regionCode) {
        StringBuffer region = new StringBuffer();
        if (null != regionCode && regionCode.length() > 0) {
            region.append(" AND A.REGION_CODE='" + regionCode + "' ");
        } else region.append("");
        String sql =
                "SELECT B.INV_NO, A.RECEIPT_NO, A.AR_AMT,"
                        + "CASE WHEN B.STATUS = '2' THEN '调整票号' WHEN B.CANCEL_FLG = '1' THEN '退费' "
                        + "WHEN B.CANCEL_FLG = '3' THEN '补印' END AS CANCEL_FLG "
                        + " FROM BIL_REG_RECP A, BIL_INVRCP B "
                        + "WHERE A.RECEIPT_NO = B.RECEIPT_NO "
                        + "  AND A.ADM_TYPE = B.ADM_TYPE "
                        + "  AND B.RECP_TYPE = 'REG' "
                        + "  AND (B.CANCEL_FLG IN ('1', '3') OR STATUS = '2') " + region
                        + " AND B.PRINT_DATE BETWEEN TO_DATE( '" + sTime + "', 'yyyyMMddHH24MISS') "
                        + " AND TO_DATE( '" + eTime + "', 'yyyyMMddHH24MISS') "
                        + "ORDER BY B.INV_NO";
        return sql;
    }
    
    /**
     * 得到日结票据(Q医与非Q医使用) add by huangtt 20160513 
     * @param accountSeq String
     * @return String
     */
    public static String getReceiptParmQeApp(String accountSeq,boolean flg) {
        if (accountSeq == null || accountSeq.equals(""))
            return "";
        String sql =
                "SELECT CASE_NO,RECEIPT_NO,ADM_TYPE,REGION_CODE,MR_NO," +
                "       RESET_RECEIPT_NO,PRINT_NO,BILL_DATE,CHARGE_DATE," +
                "       PRINT_DATE,CHARGE01,CHARGE02,CHARGE03,CHARGE04," +
                "       CHARGE05,CHARGE06,CHARGE07,CHARGE08,CHARGE09," +
                "       CHARGE10,CHARGE11,CHARGE12,CHARGE13,CHARGE14," +
                "       CHARGE15,CHARGE16,CHARGE17,CHARGE18,CHARGE19," +
                "       CHARGE20,CHARGE21,CHARGE22,CHARGE23,CHARGE24," +
                "       CHARGE25,CHARGE26,CHARGE27,CHARGE28,CHARGE29," +
                "       CHARGE30,TOT_AMT,REDUCE_REASON,REDUCE_AMT," +
                "       REDUCE_DATE,REDUCE_DEPT_CODE,REDUCE_RESPOND," +
                "       AR_AMT,PAY_CASH,PAY_MEDICAL_CARD,PAY_BANK_CARD," +
                "       PAY_INS_CARD,PAY_CHECK,PAY_DEBIT,PAY_BILPAY,PAY_DRAFT, " +
                "       PAY_INS,PAY_OTHER1,PAY_OTHER2,PAY_REMARK," +
                "       CASHIER_CODE,OPT_USER,OPT_DATE,OPT_TERM " +
                "  FROM BIL_OPB_RECP " +
                " WHERE ACCOUNT_SEQ IN (" + accountSeq + ")";
        if(flg){
        	sql += " AND ORDER_NO IS NOT NULL";
        }else{
        	sql += " AND ORDER_NO IS NULL";
        }
        
        return sql;
    }

}