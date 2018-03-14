package jdo.bms;

/**
 * <p>
 * Title: 血库SQL封装
 * </p>
 *
 * <p>
 * Description: 血库SQL封装
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: JavaHis
 * </p>
 *
 * @author zhangy 2009.05.07
 * @version 1.0
 */
public class BMSSQL {
    public BMSSQL() {
    }

    /**
     * 药库参数设定
     *
     * @return String
     */
    public static String getBMSysParm() {
        return "SELECT DEFAULT_SAFE_QTY, END_DAYS, DEFAULT_SAFE_VOL, "
            + "OPT_USER, OPT_DATE,OPT_TERM FROM BMS_SYSPARM";
    }

    /**
     * 查询血品代码
     * @return String
     */
    public static String getBMSBloodCode() {
        return "SELECT BLD_CODE AS ID, BLDCODE_DESC AS NAME, ENNAME FROM BMS_BLDCODE ORDER BY BLD_CODE";
    }

    /**
     * 查询血品代码
     * @return String
     */
    public static String getBMSBloodCode(String bld_code) {
        String where = "";
        if (!"".equals(bld_code)) {
            where = " WHERE BLD_CODE = '" + bld_code + "'";
        }
        return "SELECT BLD_CODE, BLDCODE_DESC, PY1, PY2, DESCRIPTION, "
            + " FRONTPG_TYPE, UNIT_CODE, VALUE_DAYS, "
            + " OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM BMS_BLDCODE " + where + " ORDER BY BLD_CODE";
    }

    /**
     * 查询血品单位
     * @param bld_code String
     * @return String
     */
    public static String getBMSUnit(String bld_code) {
        return "SELECT DISTINCT B.UNIT_CHN_DESC,A.UNIT_CODE " +
            "FROM BMS_BLDSUBCAT A,SYS_UNIT B " +
            "WHERE A.BLD_CODE = '" + bld_code + "' " +
            "AND A.UNIT_CODE = B.UNIT_CODE ORDER BY A.BLD_CODE ";
    }

    /**
     * 根据申请单号查询备血主项信息
     * @param apply_no String
     * @return String
     */
    public static String getBMSApplyM(String apply_no) {
        return "";
    }

    /**
     * 根据血品代码查询血品规格信息
     * @param bld_code String
     * @return String
     */
    public static String getBMSBldVol(String bld_code) {
        return
            "SELECT BLD_CODE, SUBCAT_CODE, SUBCAT_DESC, BLD_VOL,UNIT_CODE, "
            + " OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM BMS_BLDSUBCAT WHERE BLD_CODE = '" + bld_code + "'";
    }

    /**
     * 根据血品代码查询血品规格信息
     * @param bld_code String
     * @return String
     */
    public static String getBMSBldVol(String bld_code, String subcat_code) {
        return "SELECT BLD_CODE, SUBCAT_CODE, SUBCAT_DESC, BLD_VOL, UNIT_CODE "
            + " FROM BMS_BLDSUBCAT WHERE BLD_CODE = '" + bld_code +
            "' AND SUBCAT_CODE = '" + subcat_code + "'";
    }

    /**
     * 取得备血单的打印数据
     * @param apply_no String
     * @return String
     */
    public static String getBMSApplyPrtData(String apply_no) {
        return "SELECT SIFT_RESU,HBSAG,HCVAB,HIV,STS,CLS_FLG,DEPT_CODE "
            + " FROM BMS_APPLYM WHERE APPLY_NO = '" + apply_no + "'";
    }

    /**
     * 取得血品类型信息
     * @param bld_code String
     * @return String
     */
    public static String getBMSBldCodeInfo(String bld_code){
        return "SELECT A.BLD_CODE,A.BLDCODE_DESC,A.UNIT_CODE,B.UNIT_CHN_DESC "
            + " FROM BMS_BLDCODE A, SYS_UNIT B "
            + " WHERE A.UNIT_CODE = B.UNIT_CODE AND A.BLD_CODE = '" + bld_code +
            "'";
    }

    /**
     * 取得血品入库打印信息
     * @param blood_no String
     * @return String
     */
    public static String getBMSBldStockInfo(String blood_no) {
        return "SELECT C.BLDCODE_DESC, D.SUBCAT_DESC, A.BLOOD_NO, "
            + " A.ORG_BARCODE, A.BLD_TYPE, A.RH_FLG, A.END_DATE, "
            + " A.IN_DATE, E.USER_NAME "
            + "FROM BMS_BLOOD A, BMS_BLDSTOCK B, BMS_BLDCODE C, "
            + " BMS_BLDSUBCAT D, SYS_OPERATOR E "
            + " WHERE A.BLD_CODE = B.BLD_CODE "
            + " AND A.BLD_CODE = C.BLD_CODE "
            + " AND A.BLD_CODE = D.BLD_CODE "
            + " AND A.SUBCAT_CODE = B.BLD_SUBCAT "
            + " AND A.SUBCAT_CODE = D.SUBCAT_CODE "
            + " AND A.BLD_TYPE = B.BLD_TYPE "
            + " AND B.BLD_CODE = C.BLD_CODE "
            + " AND B.BLD_CODE = D.BLD_CODE "
            + " AND B.BLD_SUBCAT = D.SUBCAT_CODE "
            + " AND C.BLD_CODE = D.BLD_CODE "
            + " AND A.TEST_USER = E.USER_ID(+) "
            + " AND A.BLOOD_NO = '" + blood_no + "'";
    }

    /**
     * 取得血品出库打印信息
     * @param blood_no String
     * @return String
     */
    public static String getBMSBloodOut(String blood_no) {
//        return "SELECT C.BLDCODE_DESC, B.SUBCAT_DESC ,A.ORG_BARCODE ,A.OUT_NO ,A.OUT_DATE,E.CHN_DESC,F.DEPT_CHN_DESC,D.DIAG_CODE1,D.DIAG_CODE2,D.DIAG_CODE3,D.OPT_DATE " 
//            + " FROM BMS_BLOOD A, BMS_BLDSUBCAT B, BMS_BLDCODE C,BMS_APPLYM D,SYS_DICTIONARY E,SYS_DEPT F "
//            + " WHERE A.BLD_CODE = B.BLD_CODE "
//            + " AND A.SUBCAT_CODE = B.SUBCAT_CODE "
//            + " AND A.BLD_CODE = C.BLD_CODE "
//            + " AND B.BLD_CODE = C.BLD_CODE "
//            + " AND A.BLOOD_NO = '" + blood_no + "'"
//            + " AND A.APPLY_NO = D.APPLY_NO "
//            + " AND E.GROUP_ID = 'BMS_TRANRSN'"
//            + " AND D.TRANRSN_CODE1 = E.ID "
//            + " AND F.DEPT_CODE = D.DEPT_CODE ";
    	
        return "SELECT C.BLDCODE_DESC,A.CASE_NO, B.SUBCAT_DESC ,B.BLD_VOL||G.UNIT_CHN_DESC AS UNIT_DESC ,A.ORG_BARCODE ,A.OUT_NO ,A.OUT_DATE,F.DEPT_CHN_DESC,D.DIAG_CODE1,D.DIAG_CODE2,D.DIAG_CODE3,D.OPT_DATE,D.TRANRSN_CODE1,D.TRANRSN_CODE2,D.TRANRSN_CODE3 " 
        + " FROM BMS_BLOOD A, BMS_BLDSUBCAT B, BMS_BLDCODE C,BMS_APPLYM D,SYS_DEPT F,SYS_UNIT G "
        + " WHERE A.BLD_CODE = B.BLD_CODE "
        + " AND A.SUBCAT_CODE = B.SUBCAT_CODE "
        + " AND A.BLD_CODE = C.BLD_CODE "
        + " AND B.BLD_CODE = C.BLD_CODE "
        + " AND A.BLOOD_NO = '" + blood_no + "'"
        + " AND A.APPLY_NO = D.APPLY_NO "
        + " AND F.DEPT_CODE = D.DEPT_CODE "
        + " AND B.UNIT_CODE = G.UNIT_CODE";    	
    }

    /**
     *
     * @param blood_no String
     * @return String
     */
    public static String getBMSBlood(String blood_no) {
        return "SELECT * FROM BMS_BLOOD WHERE BLOOD_NO = '" + blood_no + "'";
    }
    
    /**
     * 取得血品类型信息
     * @param bloodCode
     * @return
     */
    public static String getBMSBldCodeInfoExcept(String bloodCode){
    	return "SELECT A.BLD_CODE,A.BLDCODE_DESC,A.UNIT_CODE,B.UNIT_CHN_DESC "
    	+ " FROM BMS_BLDCODE A, SYS_UNIT B "
    	+ " WHERE A.UNIT_CODE = B.UNIT_CODE" 
    	+ " AND A.BLD_CODE NOT IN ('" + bloodCode + "') ORDER BY A.BLD_CODE"
    	;
    }
}
