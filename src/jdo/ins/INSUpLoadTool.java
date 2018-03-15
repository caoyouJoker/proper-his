package jdo.ins;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>Title: 医保申报工具类</p>
 *
 * <p>Description: 医保申报工具类</p>
 *
 * <p>Copyright: Copyright (c) ProperSoft 2011</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author wangl 2012.02.10
 * @version 1.0
 */
public class INSUpLoadTool extends TJDOTool {
    /**
     * 实例
     */
    public static INSUpLoadTool instanceObject;
    /**
     * 得到实例
     * @return INSUpLoadTool
     */
    public static INSUpLoadTool getInstance() {
        if (instanceObject == null)
            instanceObject = new INSUpLoadTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public INSUpLoadTool() {
        onInit();
    }

    /**
     * 得到结算资料
     * @param parm TParm
     * @return TParm
     */
    public TParm getIBSData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT ADM_SEQ, CONFIRM_SRC, IDNO, HOSP_NHI_NO, INSBRANCH_CODE," +
                "        CTZ1_CODE,ADM_CATEGORY, TO_CHAR (IN_DATE, 'YYYYMMDD') AS IN_DATE," +
                "        TO_CHAR (DS_DATE, 'YYYYMMDD') AS DS_DATE, DIAG_CODE, DIAG_DESC, DIAG_DESC2," +
                "        SOURCE_CODE, OWN_RATE, DECREASE_RATE, REALOWN_RATE, INSOWN_RATE," +
                "        SUBSTR (CASE_NO, 1, 6) || SUBSTR (CASE_NO, 8) AS CASE_NO, STATION_DESC, BED_NO," +
                "        A.DEPT_DESC, BASEMED_BALANCE, INS_BALANCE, START_STANDARD_AMT," +
                "        YEAR_MON, PHA_AMT, PHA_NHI_AMT, EXM_AMT, EXM_NHI_AMT, TREAT_AMT," +
                "        TREAT_NHI_AMT, OP_AMT, OP_NHI_AMT, BED_AMT, BED_NHI_AMT, MATERIAL_AMT," +
                "        MATERIAL_NHI_AMT, OTHER_AMT, OTHER_NHI_AMT, BLOODALL_AMT," +
                "        BLOODALL_NHI_AMT, BLOOD_AMT, BLOOD_NHI_AMT, RESTART_STANDARD_AMT," +
                "        STARTPAY_OWN_AMT, OWN_AMT, PERCOPAYMENT_RATE_AMT, ADD_AMT," +
                "        INS_HIGHLIMIT_AMT, TRANBLOOD_OWN_AMT, NHI_PAY, NHI_COMMENT," +
                "        B.DEPT_CODE, CHEMICAL_DESC, ADM_PRJ, SPEDRS_CODE,A.CONFIRM_NO, " +
                "        A.SINGLE_NHI_AMT, A.SINGLE_STANDARD_OWN_AMT, A.SINGLE_SUPPLYING_AMT,A.ARMYAI_AMT,A.BLOODALL_OWN_AMT, " +
                "        A.PUBMANAI_AMT,A.BED_SINGLE_AMT,A.MATERIAL_SINGLE_AMT,A.OTHER_DIAGE_CODE," +
                "        A.INS_CROWD_TYPE,A.QUIT_REMARK,A.SINGLE_UPLOAD_TYPE,A.ACCOUNT_PAY_AMT " +
                "   FROM INS_IBS A, SYS_DEPT B" +
                "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND DS_DATE <= TO_DATE('" + parm.getData("DS_DATE") + "','YYYYMMDD')" +
//                "    AND (UPLOAD_FLG = 'N' OR UPLOAD_FLG = 'R' OR UPLOAD_FLG = 'F' " +
//                "          OR STATUS = 'N' ) " +
                "    AND A.REGION_CODE = B.REGION_CODE " +
                "    AND A.DEPT_CODE = B.DEPT_CODE ";

//            System.out.println("得到城职结算资料>>>>>>>>>>>>>>" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到上传数据
     * @param parm TParm
     * @return TParm
     */
    public TParm getIBSUploadData(TParm parm) {
        //System.out.println("得到上传数据>>>>>>>>后台入参"+parm);
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT A.ADM_SEQ, B.NEWADM_SEQ, B.INSBRANCH_CODE," +
                "        TO_CHAR (A.CHARGE_DATE, 'yyyy-mm-dd HH:mm:ss') AS CHARGE_DATE, A.SEQ_NO, B.HOSP_NHI_NO," +
                "        A.NHI_ORDER_CODE, A.ORDER_DESC, A.OWN_RATE, D.JX, D.GG, A.PRICE, QTY," +
                "        A.TOTAL_AMT, A.TOTAL_NHI_AMT, A.OWN_AMT, A.ADDPAY_AMT, A.OP_FLG," +
                "        A.ADDPAY_FLG, A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG," +
                "        D.PZWH,B.CONFIRM_NO " +
                "   FROM INS_IBS B, INS_IBS_UPLOAD A, INS_RULE D " +
                "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' " +
                "    AND A.ADM_SEQ = B.ADM_SEQ " +
                "    AND A.QTY <> 0 " +
                "    AND B.YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND B.CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND A.NHI_ORDER_CODE = D.SFXMBM " +
                "    AND A.CHARGE_DATE BETWEEN D.KSSJ AND D.JSSJ " +
                "  UNION "+
        " SELECT A.ADM_SEQ, B.NEWADM_SEQ, B.INSBRANCH_CODE," +
        "        TO_CHAR (A.CHARGE_DATE, 'yyyy-mm-dd HH:mm:ss') AS CHARGE_DATE, A.SEQ_NO, B.HOSP_NHI_NO," +
        "        A.NHI_ORDER_CODE, A.ORDER_DESC, A.OWN_RATE, '' AS JX, '' AS GG, A.PRICE, QTY," +
        "        A.TOTAL_AMT, A.TOTAL_NHI_AMT, A.OWN_AMT, A.ADDPAY_AMT, A.OP_FLG," +
        "        A.ADDPAY_FLG, A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG," +
        "        '' AS PZWH,B.CONFIRM_NO " +
        "   FROM INS_IBS B, INS_IBS_UPLOAD A" +
        "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") +
        "' " +
        "    AND A.ADM_SEQ = B.ADM_SEQ " +
        "    AND A.QTY = 0 " +
        "    AND B.YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
        "    AND B.CASE_NO = '" + parm.getData("CASE_NO") + "' " +
        "  ORDER BY ADM_SEQ ";
//        System.out.println("得到上传数据" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到补助金额，补助金额2
     * @param parm TParm
     * @return TParm
     */
    public TParm getIBSHelpAmt(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT CASE WHEN ARMYAI_AMT IS NULL THEN 0 ELSE ARMYAI_AMT END AS ARMYAI_AMT," +
                "        CASE WHEN TOT_PUBMANADD_AMT IS NULL THEN 0 ELSE TOT_PUBMANADD_AMT END AS TOT_PUBMANADD_AMT " +
                "   FROM INS_IBS " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("得到结算资料" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到医师证照号
     * @param parm TParm
     * @return TParm
     */
    public TParm getDrQualifyCode(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT C.DR_QUALIFY_CODE As DRQUALIFYCODE" +
                "   FROM ADM_INP A, SYS_OPERATOR C " +
                "  WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' " +
                "    AND A.CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND A.VS_DR_CODE = C.USER_ID ";
        //System.out.println("得到医师证照号" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到上传病案首页信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getMROUploadData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT L_TIMES, M_TIMES, S_TIMES, FP_NOTE, DS_SUMMARY " +
                "   FROM INS_IBS " +
                "  WHERE CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("得到上传病案首页信息" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到单病种费用分割中病历首页的内容
     * @param parm TParm
     * @return TParm
     */
    public TParm getMROAllData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT (SELECT COUNT (*) " +
                "           FROM ADM_INP " +
                "          WHERE ADM_INP.MR_NO = M.MR_NO " +
                "            AND ADM_INP.CASE_NO <= M.CASE_NO " +
                "            AND ADM_INP.CANCEL_FLG = 'N') AS IN_TIMES, " +
                "        M.OFFICE, M.O_ADDRESS, M.O_TEL, M.H_ADDRESS, M.H_TEL, M.CONTACTER, " +
                "        (SELECT R.CHN_DESC " +
                "           FROM SYS_DICTIONARY R " +
                "          WHERE R.ID = M.RELATIONSHIP AND GROUP_ID='SYS_RELATIONSHIP') AS RELATION_DESC, M.CONT_TEL, M.CONT_ADDRESS, " +
                "        (SELECT SYS_DEPT.DEPT_ABS_DESC " +
                "           FROM SYS_DEPT " +
                "          WHERE SYS_DEPT.DEPT_CODE = M.IN_DEPT) AS IN_DEPT, M.IN_ROOM_NO, " +
                "        (SELECT SYS_DEPT.DEPT_ABS_DESC " +
                "           FROM SYS_DEPT " +
                "          WHERE SYS_DEPT.DEPT_CODE = M.TRANS_DEPT) AS TRANS_DEPT, " +
                "        (SELECT SYS_DEPT.DEPT_ABS_DESC " +
                "           FROM SYS_DEPT " +
                "          WHERE SYS_DEPT.DEPT_CODE = M.OUT_DEPT) AS OUT_DEPT, M.OUT_ROOM_NO, " +
                "        M.IN_CONDITION,M.OE_DIAG_CODE,M.IN_DIAG_CODE,M.OUT_DIAG_CODE1,M.OUT_DIAG_CODE2,M.OUT_DIAG_CODE3,M.OUT_DIAG_CODE4,M.OUT_DIAG_CODE5,M.GET_TIMES,M.VS_DR_CODE,M.ATTEND_DR_CODE,M.PROF_DR_CODE,M.DIRECTOR_DR_CODE, M.PATHOLOGY_DIAG,M.INTE_DIAG_CODE, M.EX_RSN, " +
                "        (SELECT SYS_OPERATOR.DR_QUALIFY_CODE " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.ATTEND_DR_CODE) AS DR_QUALIFY_CODE, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.ATTEND_DR_CODE) AS USER_NAME, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.VS_DR_CODE) AS VS_DR_NAME1, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.VS_DR_CODE) AS VS_DR_NAME2, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.PROF_DR_CODE) AS PROF_DR_NAME, " +
                "        (SELECT SYS_OPERATOR.USER_NAME " +
                "           FROM SYS_OPERATOR " +
                "          WHERE SYS_OPERATOR.USER_ID = M.DIRECTOR_DR_CODE) AS DIRECTOR_DR_NAME, " +
                "        (SELECT A.ADM_SEQ " +
                "           FROM INS_ADM_CONFIRM A, ADM_INP B " +
                "          WHERE A.CASE_NO = B.CASE_NO AND B.CASE_NO = '" +
                parm.getData("CASE_NO") + "') AS ADM_SEQ,M.MR_NO " +
                "   FROM MRO_RECORD M " +
                "  WHERE M.MR_NO = '" + parm.getData("MR_NO") + "' " +
                "    AND M.CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("得到单病种费用分割中病历首页的内容" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到单病种费用分割中病历首页之手术资料的内容
     * @param parm TParm
     * @return TParm
     */
    public TParm getMROOpData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT TO_CHAR (O.OP_DATE, 'YYYYMMDD') AS OP_DATE, O.OP_DESC AS NAME, " +
                "        (SELECT A.USER_NAME " +
                "           FROM SYS_OPERATOR A " +
                "          WHERE A.USER_ID = O.MAIN_SUGEON) AS DOCT_NAME, (SELECT A.USER_NAME " +
                "                                               FROM SYS_OPERATOR A " +
                "                                              WHERE A.USER_ID = O.AST_DR1) AS ASSISTANT_NAME, " +
                "        (SELECT S.CHN_DESC " +
                "           FROM SYS_DICTIONARY S " +
                "          WHERE S.ID = O.ANA_WAY AND GROUP_ID = 'OPE_ANAMETHOD') AS MAZUI_MOD, (SELECT A.USER_NAME " +
                "                                                                     FROM SYS_OPERATOR A " +
                "                                                                    WHERE A.USER_ID = O.ANA_DR) AS MAZUI_DOCT, " +
                " (SELECT S.CHN_DESC  "+  
                "        FROM SYS_DICTIONARY S "+  
                "       WHERE S.ID = O.HEALTH_LEVEL  AND GROUP_ID = 'MRO_HEALTHLEVEL' "+
                "                 ) AS HEAL_LEV, O.SEQ_NO AS SEQ " +
                "   FROM MRO_RECORD_OP O " +
                "  WHERE O.CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("得到单病种费用分割中病历首页之手术资料的内容" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到单病种结算信息和出院信息上传部分信息查询
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleIBSData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT SINGLE_NHI_AMT, SINGLE_STANDARD_OWN_AMT, SINGLE_SUPPLYING_AMT, " +
                "        STARTPAY_OWN_AMT, PERCOPAYMENT_RATE_AMT, BED_SINGLE_AMT,MATERIAL_SINGLE_AMT, B.SDISEASE_CODE " +
                "   FROM INS_IBS A , INS_ADM_CONFIRM B " +
                "  WHERE A.CASE_NO = '" + parm.getData("CASE_NO") + "' "+
                "  AND A.CASE_NO  =  B.CASE_NO " +
                "  AND B.IN_STATUS <> 5";
        //System.out.println("得到单病种结算信息和出院信息上传部分信息查询" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到单病种编码
     * @param parm TParm
     * @return TParm
     */
    public TParm getSingleCode(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =
                " SELECT A.SDISEASE_CODE " +
                "   FROM INS_ADM_CONFIRM A, ADM_INP B " +
                "  WHERE B.CASE_NO = '" + parm.getData("CASE_NO") + "' " +
                "    AND A.CASE_NO = B.CASE_NO ";
        //System.out.println("得到单病种编码" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 医保申报后更新结算表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbs(TParm parm, TConnection connection) {
//        System.out.println("城职医保申报后更新结算表======入参》》》》"+parm);
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS " +
                "    SET UPLOAD_FLG = 'S', " +
                "        STATUS = 'S', " +
                "        UPLOAD_DATE = SYSDATE, " +
                "        NEWADM_SEQ = '" + parm.getData("NEW_CONFIRM_NO") + "', " +
                "        ACCOUNT_PAY_AMT = " + parm.getData("ACCOUNT_PAY_AMT") +
                ", " +
                "        PERSON_ACCOUNT_AMT = " +
                parm.getData("PERSON_ACCOUNT_AMT") + " " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' ";
//        System.out.println("医保申报后更新结算表sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 医保申报后更新费用明细档
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbsOrder(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS_ORDER " +
                "    SET NEWADM_SEQ = '" + parm.getData("NEWADM_SEQ") + "' " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") + "' " +
                "    AND YEAR_MON = '" + parm.getData("YEAR_MON") + "' " +
                "    AND CASE_NO = '" + parm.getData("CASE_NO") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 医保申报后更新资格确认书表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbsAdmConfirm(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
            " UPDATE INS_ADM_CONFIRM " +
            "    SET IN_STATUS = '2'," +
            "        OPT_USER = '" + parm.getData("OPT_USER") + "'," +
            "        OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
            "        OPT_DATE = SYSDATE," +
            "        AUD_DATE = SYSDATE " +
            "  WHERE CONFIRM_NO IN (SELECT CONFIRM_NO FROM INS_IBS WHERE NEWADM_SEQ = '" +
                parm.getValue("NEW_CONFIRM_NO") + "')";
//        System.out.println("城职医保申报后更新资格确认书表sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 医保申报后更新上传明细表
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpINSIbsUpload(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS_UPLOAD " +
                "    SET INVNO = '" + parm.getData("INVNO") + "' " +
                "  WHERE ADM_SEQ = '" + parm.getData("ADM_SEQ") + "' ";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;

    }

    /**
     * 撤销申报更新
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsForCJ(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                "  UPDATE INS_IBS " +
                "  SET UPLOAD_FLG = 'N'," +
                "   OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "   OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "   OPT_DATE = SYSDATE " +
                " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 撤销申报更新
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsInsAdmConfirmForCJ(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_ADM_CONFIRM " +
                " SET IN_STATUS = '1'," +
                "   OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "   OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "   OPT_DATE = SYSDATE " +
                " WHERE CONFIRM_NO = '" + parm.getData("CONFIRM_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到医保状态
     * @param parm TParm
     * @return TParm
     */
    public TParm getInsStatusForCJ(TParm parm) {

        TParm result = new TParm();
        String sql =
                "SELECT IN_STATUS" +
                " FROM INS_ADM_CONFIRM " +
                " WHERE ADM_SEQ = '" + parm.getData("ADM_SEQ") + "'";
        //System.out.println("sql" + sql);

        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 审核资格确认书
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsInsAdmConfirmForCheckCJ(TParm parm,
                                                TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_ADM_CONFIRM " +
                " SET IN_STATUS = '7'," +
                "  OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "  OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "  OPT_DATE = SYSDATE " +
//        	     ",  DOWN_DATE = TO_DATE ('"+parm.getData("")+"', 'YYYYMMDDHH24MISS')"+                //TODO:不知道数据来源
                " WHERE ADM_SEQ = '" + parm.getData("ADM_SEQ") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 获得医保申报信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getINSMedAppInfo(TParm parm) {
        //System.out.println("获得医保申报信息>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
//            df.format(new Date(parm.getValue("DS_DATE")));
        String sql =
                " SELECT A.ADM_SEQ, A.CONFIRM_SRC, A.IDNO, A.HOSP_NHI_NO, A.INSBRANCH_CODE," +
                "A.CTZ1_CODE, A.ADM_CATEGORY, TO_CHAR (A.IN_DATE, 'YYYYMMDD') AS IN_DATE," +
                "TO_CHAR (A.DS_DATE, 'YYYYMMDD') AS DS_DATE, A.DIAG_CODE, A.DIAG_DESC," +
                "A.DIAG_DESC2, A.SOURCE_CODE, A.OWN_RATE, A.DECREASE_RATE," +
                "A.REALOWN_RATE, A.INSOWN_RATE," +
                "SUBSTR (A.CASE_NO, 1, 6) || SUBSTR (A.CASE_NO, 8) AS CASE_NO, A.STATION_DESC," +
                "A.BED_NO, A.DEPT_DESC, A.BASEMED_BALANCE, A.INS_BALANCE," +
                "A.START_STANDARD_AMT, A.YEAR_MON, A.PHA_AMT, A.PHA_NHI_AMT, A.EXM_AMT," +
                "A.EXM_NHI_AMT, A.TREAT_AMT, A.TREAT_NHI_AMT, A.OP_AMT, A.OP_NHI_AMT," +
                "A.BED_AMT, A.BED_NHI_AMT, A.MATERIAL_AMT, A.MATERIAL_NHI_AMT," +
                "A.OTHER_AMT, A.OTHER_NHI_AMT, A.BLOODALL_AMT, A.BLOODALL_NHI_AMT," +
                "A.BLOOD_AMT, A.BLOOD_NHI_AMT, A.RESTART_STANDARD_AMT," +
                "A.STARTPAY_OWN_AMT, A.OWN_AMT, A.PERCOPAYMENT_RATE_AMT, A.ADD_AMT," +
                "A.INS_HIGHLIMIT_AMT, A.TRANBLOOD_OWN_AMT, A.NHI_PAY, NHI_COMMENT," +
                "E.DEPT_CODE, A.CHEMICAL_DESC, A.ADM_PRJ, A.SPEDRS_CODE," +
                "B.BEARING_OPERATIONS_TYPE, D.DR_QUALIFY_CODE AS LCS_NO, '', F.NHI_CTZ_FLG,A.SINGLE_STANDARD_OWN_AMT," +
                "A.SINGLE_SUPPLYING_AMT,A.ARMYAI_AMT,B.SDISEASE_CODE,A.PUBMANAI_AMT,A.BED_SINGLE_AMT,A.MATERIAL_SINGLE_AMT, " +
                "A.ILLNESS_SUBSIDY_AMT,A.OTHER_DIAGE_CODE,A.QUIT_REMARK,A.SINGLE_UPLOAD_TYPE" +//城乡大病救助和其他出院诊断
                " FROM INS_IBS A," +
                " INS_ADM_CONFIRM B," +
                " ADM_INP C," +
                " SYS_OPERATOR D," +
                " SYS_DEPT E," +
                " SYS_CTZ F" +
                " WHERE A.REGION_CODE = '" + parm.getValue("REGION_CODE") + "'" +
                " AND A.YEAR_MON = '" + parm.getValue("YEAR_MON") + "'" +
                " AND A.CASE_NO = '" + parm.getValue("CASE_NO") + "'" +
                " AND TO_CHAR(A.DS_DATE,'yyyymmdd')<= '" +
                parm.getData("DS_DATE") + "'" +
//                " AND (   A.UPLOAD_FLG = 'N'  OR A.UPLOAD_FLG = 'R' OR A.UPLOAD_FLG = 'F' OR A.STATUS = 'N')" +
                " AND A.CONFIRM_NO = B.CONFIRM_NO" +
                " AND A.REGION_CODE = C.REGION_CODE" +
                " AND A.CASE_NO = C.CASE_NO" +
                " AND C.REGION_CODE = D.REGION_CODE" +
                " AND C.VS_DR_CODE = D.USER_ID" +
                " AND A.REGION_CODE = E.REGION_CODE" +
                " AND A.DEPT_CODE = E.DEPT_CODE" +
                " AND B.CTZ1_CODE = F.CTZ_CODE";
        //System.out.println("医保申报信息" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 6更新新的就诊顺序号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsAdmSeq(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS " +
                " SET NEWADM_SEQ = '" + parm.getData("NEWADM_SEQ") + "'," +
                " OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                " OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                " OPT_DATE = SYSDATE " +
                " WHERE REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' AND YEAR_MON = '" + parm.getData("YEAR_MON") +
                "' AND CASE_NO = '" + parm.getData("CASE_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 7更新INS_IBS_ORDER新的就诊顺序号
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsOrderAdmSeq(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS_ORDER " +
                " SET NEWADM_SEQ = '" + parm.getData("NEWADM_SEQ") + "'," +
                " OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                " OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                " OPT_DATE = SYSDATE " +
                " WHERE REGION_CODE = '" + parm.getData("REGION_CODE") +
                "' AND YEAR_MON = '" + parm.getData("YEAR_MON") +
                "' AND CASE_NO = '" + parm.getData("CASE_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 费用上传明细
     * @param parm TParm
     * @return TParm
     */
    public TParm getInsMedInfo(TParm parm) {
        TParm result = new TParm();
        String sql =
                " SELECT   A.ADM_SEQ, B.NEWADM_SEQ, B.INSBRANCH_CODE," +
                " TO_CHAR (A.CHARGE_DATE, 'YYYYMMDDHH24MISS') AS CHARGE_DATE, A.SEQ_NO, B.HOSP_NHI_NO," +
                " A.NHI_ORDER_CODE, A.ORDER_DESC, A.OWN_RATE, D.JX, D.GG, A.PRICE, QTY," +
                " A.TOTAL_AMT, A.TOTAL_NHI_AMT, A.OWN_AMT, A.ADDPAY_AMT, A.OP_FLG," +
                " A.ADDPAY_FLG, A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG," +
                " D.PZWH " +
                " FROM INS_IBS B, INS_IBS_UPLOAD A, INS_RULE D" +
                " WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") + "'" +
                " AND A.ADM_SEQ = B.ADM_SEQ" +
                " AND A.QTY <> 0" +
                " AND B.YEAR_MON = '" + parm.getData("YEAR_MON") + "'" +
                " AND B.CASE_NO = '" + parm.getData("CASE_NO") + "'" +
                " AND A.NHI_ORDER_CODE = D.SFXMBM" +
                " AND A.CHARGE_DATE BETWEEN D.KSSJ AND D.JSSJ" +
                " UNION "+
                " SELECT   A.ADM_SEQ, B.NEWADM_SEQ, B.INSBRANCH_CODE," +
                " TO_CHAR (A.CHARGE_DATE, 'YYYYMMDDHH24MISS') AS CHARGE_DATE, A.SEQ_NO, B.HOSP_NHI_NO," +
                " A.NHI_ORDER_CODE, A.ORDER_DESC, A.OWN_RATE, '' AS JX, '' AS GG, A.PRICE, QTY," +
                " A.TOTAL_AMT, A.TOTAL_NHI_AMT, A.OWN_AMT, A.ADDPAY_AMT, A.OP_FLG," +
                " A.ADDPAY_FLG, A.NHI_ORD_CLASS_CODE, A.PHAADD_FLG, A.CARRY_FLG," +
                " '' AS PZWH " +
                " FROM INS_IBS B, INS_IBS_UPLOAD A" +
                " WHERE A.REGION_CODE = '" + parm.getData("REGION_CODE") + "'" +
                " AND A.ADM_SEQ = B.ADM_SEQ" +
                " AND A.QTY = 0" +
                " AND B.YEAR_MON = '" + parm.getData("YEAR_MON") + "'" +
                " AND B.CASE_NO = '" + parm.getData("CASE_NO") + "'" +
                " ORDER BY ADM_SEQ";
//        System.out.println("费用上传明细sql" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 费用明细上传回写
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsIbsBack(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_IBS " +
                "    SET UPLOAD_FLG = 'S'," +
                "        STATUS = 'S'," +
                "        UPLOAD_DATE = SYSDATE," +
                "        OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "        OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "        OPT_DATE = SYSDATE " +
                "  WHERE REGION_CODE = '" + parm.getData("REGION_CODE") +
                "'   AND YEAR_MON = '" + parm.getData("YEAR_MON") +
                "'   AND CASE_NO = '" + parm.getData("CASE_NO") + "'";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 费用明细上传回写INS_ADM_CONFIRM
     * @param parm TParm
     * @param connection TConnection
     * @return TParm
     */
    public TParm onUpInsAdmConfirmBack(TParm parm, TConnection connection) {
        TParm result = new TParm();
        String sql =
                " UPDATE INS_ADM_CONFIRM " +
                "    SET IN_STATUS = '2'," +
                "        OPT_USER = '" + parm.getData("OPT_USER") + "'," +
                "        OPT_TERM = '" + parm.getData("OPT_TERM") + "'," +
                "        OPT_DATE = SYSDATE," +
                "        AUD_DATE = SYSDATE " +
                "  WHERE CONFIRM_NO IN (SELECT CONFIRM_NO FROM INS_IBS WHERE NEWADM_SEQ = '" +
                parm.getValue("NEWADM_SEQ") + "')";
        //System.out.println("sql" + sql);
        result = new TParm(TJDODBTool.getInstance().update(sql,
                connection));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到病患信息
     * @param parm TParm
     * @return TParm
     */
    public TParm getPatInfo(TParm parm) {
        TParm result = new TParm();
        String sql =
                " SELECT 'N' FLG, O.YEAR_MON, O.CONFIRM_NO, O.MR_NO, O.PAT_NAME," +
                "        S.CHN_DESC AS SEX_DESC, C.CTZ_DESC, O.IN_DATE, O.DS_DATE, O.CASE_NO," +
                "        A.PERSONAL_NO,A.ADM_SEQ " +
                "   FROM INS_IBS O,SYS_DICTIONARY S,SYS_CTZ C,SYS_PATINFO P,INS_ADM_CONFIRM A " +
                "  WHERE A.HIS_CTZ_CODE = C.CTZ_CODE " +
                "    AND O.CTZ1_CODE = A.CTZ1_CODE "+
                "    AND C.NHI_CTZ_FLG = 'Y' " +
                "    AND C.INS_CROWD_TYPE = '"+parm.getValue("INS_PAT_TYPE")+"' " +
//                "    AND SUBSTR (C.CTZ_CODE, 0, 1) = '1' " +
                "    AND P.MR_NO = O.MR_NO " +
                "    AND O.CONFIRM_NO = A.CONFIRM_NO " +
                "    AND A.IN_STATUS = '1' " +
                "    AND P.SEX_CODE = S.ID " +
                "    AND S.GROUP_ID = 'SYS_SEX' " +
                "    AND O.CASE_NO = '" + parm.getData("CASE_NO") + "' ";
//            System.out.println("得到申报病患信息sql" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 得到医保医院代码
     * @param hospCode String
     * @return TParm
     */
    public TParm getNhiHospCode(String hospCode) {
        TParm result = new TParm();
        String sql =
                " SELECT NHI_NO,REGION_CHN_DESC FROM SYS_REGION " +
                "  WHERE REGION_CODE = '" + hospCode + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }

    /**
     * 得到中心医保身份代码
     * @param ctzCode String
     * @return TParm
     */
    public TParm getNhiCtzCode(String ctzCode) {
        TParm result = new TParm();
        String sql =
                " SELECT NHI_NO FROM SYS_CTZ " +
                "  WHERE CTZ_CODE = '" + ctzCode + "'";
        result = new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    /**
     *  得到病案首页的内容
     * @param parm TParm
     * @return TParm
     */  
    public TParm getMROData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =" SELECT (SELECT A.ADM_SEQ  FROM INS_ADM_CONFIRM A, ADM_INP B"+ 
        " WHERE A.CASE_NO = B.CASE_NO AND B.CASE_NO ='"+ parm.getData("CASE_NO")+ "'" +
        " AND A.IN_STATUS !='5' AND A.ADM_SEQ ='"+ parm.getData("ADM_SEQ")+ "') AS ADM_SEQ,C.NHI_CARDNO,"+ 
        " C.MRO_CTZ AS PAY_WAY,C.IN_COUNT AS IN_TIMES,C.MR_NO,C.PAT_NAME,"+ 
        " (SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY"+ 
        " WHERE SYS_DICTIONARY.GROUP_ID ='SYS_SEX'"+ 
        " AND SYS_DICTIONARY.ID =C.SEX ) AS SEX,"+ 
        " TO_CHAR(C.BIRTH_DATE,'YYYY-MM-DD') AS BIRTH_DATE ,C.AGE,"+ 
        " (SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY"+ 
        " WHERE SYS_DICTIONARY.GROUP_ID ='SYS_NATION'"+ 
        " AND SYS_DICTIONARY.ID =C.NATION ) AS NATION, C.NB_WEIGHT,"+ 
        " C.NB_ADM_WEIGHT AS NB_IN_WEIGHT,"+  
        " (SELECT SYS_HOMEPLACE.HOMEPLACE_DESC  FROM  SYS_HOMEPLACE"+  
        " WHERE SYS_HOMEPLACE.HOMEPLACE_CODE = C.HOMEPLACE_CODE ) AS BIRTH_ADDRESS,"+  
        " (SELECT SYS_HOMEPLACE.HOMEPLACE_DESC  FROM  SYS_HOMEPLACE"+  
        " WHERE SYS_HOMEPLACE.HOMEPLACE_CODE = C.BIRTHPLACE ) AS BIRTHPLACE,"+ 
        " (SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY"+ 
        " WHERE SYS_DICTIONARY.GROUP_ID ='SYS_SPECIES'"+ 
        " AND SYS_DICTIONARY.ID =C.FOLK) AS FOLK,C.IDNO AS ID_NO,"+ 
        " (SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY"+ 
        " WHERE SYS_DICTIONARY.GROUP_ID ='SYS_OCCUPATION'"+ 
        " AND SYS_DICTIONARY.ID =C.OCCUPATION)  AS OCCUPATION,C.MARRIGE,C.ADDRESS,"+ 
        " C.TEL AS ADDRESS_TEL ,C.POST_NO,C.H_ADDRESS,C.H_POSTNO AS POST_CODE,"+ 
        " C.O_ADDRESS,C.O_TEL,C.O_POSTNO,C.CONTACTER,"+ 
        " (SELECT SYS_DICTIONARY.CHN_DESC  FROM SYS_DICTIONARY"+ 
        " WHERE SYS_DICTIONARY.GROUP_ID ='SYS_RELATIONSHIP'"+ 
        " AND SYS_DICTIONARY.ID =C.RELATIONSHIP) AS RELATIONSHIP,C.CONT_ADDRESS,C.CONT_TEL,"+  
        " CASE WHEN C.ADM_SOURCE ='01' THEN '1'  WHEN C.ADM_SOURCE ='02' THEN '2'"+  
        " WHEN C.ADM_SOURCE ='09' THEN '3'  WHEN C.ADM_SOURCE ='99' THEN  '9'  END  AS ADM_SOURCE,"+ 
        " TO_CHAR(C.IN_DATE,'YYYYMMDD') AS IN_DATE ,"+ 
        " (SELECT SYS_DEPT.INS_DEPT_CODE FROM SYS_DEPT"+  
        " WHERE SYS_DEPT.DEPT_CODE = C.IN_DEPT) AS IN_DEPT,C.IN_ROOM_NO AS IN_STATION,"+ 
        " (SELECT SYS_DEPT.INS_DEPT_CODE FROM SYS_DEPT"+ 
        " WHERE SYS_DEPT.DEPT_CODE = C.TRANS_DEPT) AS TRANS_DEPT,"+  
        " TO_CHAR(C.OUT_DATE,'YYYYMMDD') AS OUT_DATE ,"+ 
        " (SELECT SYS_DEPT.INS_DEPT_CODE FROM SYS_DEPT"+   
        " WHERE SYS_DEPT.DEPT_CODE = C.OUT_DEPT) AS OUT_DEPT,"+  
        " C.OUT_ROOM_NO AS OUT_STATION,C.REAL_STAY_DAYS,"+ 
        " (SELECT SYS_DIAGNOSIS.ICD_CHN_DESC FROM JAVAHIS.SYS_DIAGNOSIS"+ 
        " WHERE SYS_DIAGNOSIS.ICD_CODE  = C.OE_DIAG_CODE) AS OE_DIAG_DESC,C.OE_DIAG_CODE,"+ 
        " (SELECT SYS_DIAGNOSIS.ICD_CHN_DESC FROM JAVAHIS.SYS_DIAGNOSIS"+ 
        " WHERE SYS_DIAGNOSIS.ICD_CODE  = C.EX_RSN) AS EX_RSN_DESC,C.EX_RSN AS EX_RSN_CODE,"+ 
        " (SELECT SYS_DIAGNOSIS.ICD_CHN_DESC FROM JAVAHIS.SYS_DIAGNOSIS"+ 
        " WHERE SYS_DIAGNOSIS.ICD_CODE  = C.PATHOLOGY_DIAG) AS  PATHOLOGY_DIAG,"+ 
        " C.PATHOLOGY_DIAG AS PATHOLOGY_DIAG_CODE,C.PATHOLOGY_NO,C.ALLEGIC_FLG,"+ 
        " C.ALLEGIC,C.BODY_CHECK,C.BLOOD_TYPE,C.RH_TYPE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+ 
        " WHERE SYS_OPERATOR.USER_ID = C.DIRECTOR_DR_CODE) AS DIRECTOR_DR_CODE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.PROF_DR_CODE) AS PROF_DR_CODE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.ATTEND_DR_CODE) AS ATTEND_DR_CODE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.VS_DR_CODE) AS VS_DR_CODE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.VS_NURSE_CODE) AS VS_NURSE_CODE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.INDUCATION_DR_CODE) AS INDUCATION_DR_CODE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.INTERN_DR_CODE) AS INTERN_DR_CODE,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.ENCODER) AS ENCODER,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.QUALITY) AS QUALITY,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.CTRL_DR) AS CTRL_DR,"+ 
        " (SELECT SYS_OPERATOR.USER_NAME FROM SYS_OPERATOR"+  
        " WHERE SYS_OPERATOR.USER_ID = C.CTRL_NURSE) AS CTRL_NURSE,"+ 
        " TO_CHAR(C.CTRL_DATE,'YYYY-MM-DD') AS CTRL_DATE,C.OUT_TYPE,C.TRAN_HOSP,"+ 
        " CASE WHEN C.AGN_PLAN_FLG ='N' THEN '1'  WHEN C.AGN_PLAN_FLG ='Y' THEN '2'  END AS AGN_PLAN_FLG,"+ 
        " C.AGN_INTENTION,C.AGN_PLAN_INTENTION,C.BE_COMA_TIME," +
        " C.AF_COMA_TIME,C.SUM_TOT,C.OWN_TOT,C.CHARGE_01,C.CHARGE_02,"+ 
        " C.CHARGE_03,C.CHARGE_04,C.CHARGE_05,C.CHARGE_06,C.CHARGE_07,C.CHARGE_08,C.CHARGE_10,"+ 
        " C.CHARGE_09,C.CHARGE_13,C.CHARGE_11,C.CHARGE_12,C.CHARGE_14,C.CHARGE_15,C.CHARGE_16,"+ 
        " C.CHARGE_17,C.CHARGE_18,C.CHARGE_19,C.CHARGE_20,C.CHARGE_21,C.CHARGE_22,C.CHARGE_23,"+ 
        " C.CHARGE_24,C.CHARGE_25,C.CHARGE_26,C.CHARGE_27,C.CHARGE_28," +
        " C.ICU_ROOM1,C.ICU_IN_DATE1,C.ICU_OUT_DATE1,C.ICU_ROOM2,C.ICU_IN_DATE2,C.ICU_OUT_DATE2,"+
        " C.ICU_ROOM3,C.ICU_IN_DATE3,C.ICU_OUT_DATE3,C.ICU_ROOM4,C.ICU_IN_DATE4,C.ICU_OUT_DATE4,"+
        " C.ICU_ROOM5,C.ICU_IN_DATE5,C.ICU_OUT_DATE5,C.VENTI_TIME"+ 
        " FROM MRO_RECORD C"+  
        " WHERE C.CASE_NO = '"+ parm.getData("CASE_NO")+ "'";
//        System.out.println("得到病历首页的内容" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     *  得到案首页之手术及操作的内容
     * @param parm TParm
     * @return TParm
     */
    public TParm getMROOPData(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =" SELECT (SELECT A.ADM_SEQ  FROM INS_ADM_CONFIRM A, ADM_INP B"+ 
        	" WHERE A.CASE_NO = B.CASE_NO AND B.CASE_NO ='"+ parm.getData("CASE_NO")+ "'" +
        	" AND A.IN_STATUS !='5' AND A.ADM_SEQ ='"+ parm.getData("ADM_SEQ")+ "') AS ADM_SEQ,"+ 
        	" C.OPT_USER AS OPT_CODE,TO_CHAR(C.OP_DATE,'YYYYMMDD') AS OP_DATE,"+ 
        	" (SELECT S.CHN_DESC FROM SYS_DICTIONARY S WHERE S.ID = C.OP_LEVEL " +
        	" AND GROUP_ID = 'OPE_RANK') AS OP_LEVEL,C.OP_DESC AS OP_NAME,(SELECT A.USER_NAME "+ 
        	" FROM SYS_OPERATOR A  WHERE A.USER_ID = C.MAIN_SUGEON) AS OP_DR_NAME,"+ 
        	" (SELECT A.USER_NAME  FROM SYS_OPERATOR A WHERE A.USER_ID = C.AST_DR1) AS AST_DR1,"+ 
        	" (SELECT A.USER_NAME  FROM SYS_OPERATOR A WHERE A.USER_ID = C.AST_DR2) AS AST_DR2,"+ 
        	" C.HEALTH_LEVEL AS HEAL_LEV,(SELECT S.CHN_DESC FROM SYS_DICTIONARY S"+  
        	" WHERE S.ID = C.ANA_WAY AND GROUP_ID = 'OPE_ANAMETHOD') AS ANA_WAY,"+ 
        	" (SELECT A.USER_NAME  FROM SYS_OPERATOR A  WHERE A.USER_ID = C.ANA_DR) AS ANA_DR,C.SEQ_NO"+ 
        	" FROM MRO_RECORD_OP C"+ 
        	" WHERE C.CASE_NO = '"+ parm.getData("CASE_NO")+ "'";
//        System.out.println("得到病历首页之手术资料的内容" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
	 * 出院诊断信息
	 * @param parm
	 * @return
	 */
	public TParm getDiag(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "Err:参数不能为NULL");
			return result;
		}
		String sql =" SELECT ICD_CODE, ICD_DESC , A.IN_PAT_CONDITION," +
				    " IO_TYPE , B.CHN_DESC,A.ICD_STATUS"+
	                " FROM MRO_RECORD_DIAG A LEFT JOIN   SYS_DICTIONARY  B"+
	                " ON A.IN_PAT_CONDITION   =  B.ID  AND B.GROUP_ID   = 'ADM_IN_PAT_CONDITION'"+
	                " WHERE A.CASE_NO = '"+ parm.getData("CASE_NO")+ "'"+ 
	                " AND A.IO_TYPE IN ('O','Q','W')"+
	                " ORDER BY A.IO_TYPE,A.MAIN_FLG DESC";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
    /**
	 * 得到住院死亡信息
	 * @param parm
	 * @return
	 */
	public TParm getStatusdata(TParm parm) {
		TParm result = new TParm();
		if (parm == null) {
			result.setErr(-1, "Err:参数不能为NULL");
			return result;
		}
		String sql =" SELECT A.ADM_SEQ,A.CONFIRM_NO,A.IDNO,A.PERSONAL_NO,A.PAT_AGE," +
				    " A.PAT_NAME,TO_CHAR(A.BIRTH_DATE,'YYYY-MM-DD') AS BIRTH_DATE," +
				    " A.SEX_CODE,A.DEATH_FLG,A.CASE_NO" +
				    " FROM INS_ADM_CONFIRM A"+
                    " WHERE A.CASE_NO = '"+ parm.getData("CASE_NO")+ "'"+ 
                    " AND A.ADM_SEQ ='"+ parm.getData("ADM_SEQ")+ "'"+ 
                    " AND A.IN_STATUS !='5' ";
		result = new TParm(TJDODBTool.getInstance().select(sql));
		return result;
	}
    /**
     *  得到诊疗项目外检备案信息的内容1
     * @param parm TParm
     * @return TParm
     */
    public TParm getDiagnosisData1(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =" SELECT B.TRANS_HOSP_CODE,A.DR_NOTE,C.HIS_CTZ_CODE," +
        		    " A.ORDER_CODE,A.ORDER_DESC,A.CASE_NO,C.ADM_SEQ"+
                    " FROM ODI_ORDER A ,SYS_FEE  B,INS_ADM_CONFIRM C"+
                    " WHERE A.CASE_NO = '"+ parm.getData("CASE_NO")+ "'"+
                    " AND A.CASE_NO = C.CASE_NO"+
                    " AND C.ADM_SEQ = '"+ parm.getData("ADM_SEQ")+ "'"+
                    " AND A.ORDER_CODE  = B.ORDER_CODE"+
                    " AND A.SETMAIN_FLG = 'Y'"+
                    " AND B.TRANS_OUT_FLG = 'Y'";  
//        System.out.println("得到诊疗项目外检备案信息的内容1" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }
    /**
     *  得到诊疗项目外检备案信息的内容2
     * @param parm TParm
     * @return TParm
     */
    public TParm getDiagnosisData2(TParm parm) {
        TParm result = new TParm();
        if (parm == null) {
            result.setErr( -1, "Err:参数不能为NULL");
            return result;
        }
        String sql =" SELECT B.ORDER_CODE,B.NHI_ORDER_CODE " +
        		    " FROM SYS_ORDERSETDETAIL A,INS_IBS_UPLOAD B"+
                    " WHERE A.ORDERSET_CODE ='"+ parm.getData("ORDER_CODE")+ "'"+
        	        " AND B.ADM_SEQ ='"+ parm.getData("ADM_SEQ")+ "'"+
        	        " AND A.ORDER_CODE = B.ORDER_CODE";  
//        System.out.println("得到诊疗项目外检备案信息的内容2" + sql);
        result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("数据错误 " + result.getErrText());
            return result;
        }
        return result;
    }
}
