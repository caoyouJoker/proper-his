package jdo.phl;

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
public class PHLSQL {

    /**
     * 静点室床位卡信息查询
     * @param region_code String
     * @return String
     * ===========pangben modify 20110621 添加区域参数
     */
    public static String getPHLBedCardInfor(String region_code,String region_codeAll) {
        String region="";
        if(null!=region_codeAll &&region_codeAll.length()>0)
            region= " AND A.REGION_CODE_All = '" + region_codeAll + "' ";
        return "SELECT   A.BED_NO, A.BED_DESC, A.BED_STATUS, A.TYPE, A.MR_NO, "
            + "B.PAT_NAME, B.SEX_CODE,B.BIRTH_DATE, B.CTZ1_CODE, "
            + "C.ADM_DATE, E.USER_NAME, F.ICD_CHN_DESC, A.PAT_STATUS "
            + "FROM PHL_BED A, SYS_PATINFO B, REG_PATADM C, "
            + "OPD_DIAGREC D,  SYS_OPERATOR E, SYS_DIAGNOSIS F "
            + "WHERE A.REGION_CODE = '" + region_code + "' "
            + region
            + "AND A.MR_NO = B.MR_NO(+) AND A.CASE_NO = C.CASE_NO(+) "
            + "AND A.CASE_NO = D.CASE_NO(+) AND D.MAIN_DIAG_FLG(+) = 'Y' "
            + "AND C.DR_CODE = E.USER_ID(+) AND D.ICD_CODE = F.ICD_CODE(+) "
            + "ORDER BY A.BED_NO";
    }

    /**
     * 根据条件查询静点医嘱
     * @param adm_type String
     * @param case_no String
     * @param order_no String
     * @param order_code String
     * @return String
     */
    public static String getPHLOrder(String adm_type, String case_no,
                                     String order_no, String order_code) {
        return "SELECT ADM_TYPE, CASE_NO, ORDER_NO, SEQ_NO, ORDER_CODE, "
            + " MR_NO, DR_CODE, ORDER_DTTM, LINK_MAIN_FLG, LINK_NO, "
            + " ROUTE_CODE,FREQ_CODE,TAKE_DAYS,BAR_CODE,BAR_CODE_PRINT_FLG, "
            + " EXEC_STATUS, EXEC_USER, EXEC_DATE, DR_NOTE, NS_NOTE, "
            + " DC_CONFIRM_USER,DC_CONFIRM_DATE,OPT_USER,OPT_DATE,OPT_TERM "
            + " FROM PHL_ORDER WHERE ADM_TYPE = '" + adm_type +
            "' AND CASE_NO = '" + case_no + "' AND ORDER_NO = '" + order_no +
            "' AND ORDER_CODE = '" + order_code + "'";
    }

    /**
     * 床/座打印数据
     * @param mr_no String
     * @param case_no String
     * @return String
     */
    public static String getPHLBedCard(String mr_no, String case_no) {
        return "SELECT A.REGISTER_DATE, B.PAT_NAME, B.SEX_CODE, "
            + " B.BIRTH_DATE, C.CTZ_DESC, D.REGION_DESC, A.BED_NO, A.BED_DESC "
            + " FROM PHL_BED A, SYS_PATINFO B , SYS_CTZ C, PHL_REGION D "
            + " WHERE A.MR_NO = B.MR_NO AND B.CTZ1_CODE = C.CTZ_CODE "
            + " AND A.REGION_CODE = D.REGION_CODE AND A.CASE_NO = '" + case_no
            + "' AND A.MR_NO = '" + mr_no + "'";
    }

    /**
     * 根据病案号查询最近一次的就诊日期
     * @param mr_no String
     * @return String
     */
    public static String getLastAdmDateByMrNo(String mr_no) {
        return "SELECT ADM_DATE FROM REG_PATADM WHERE MR_NO ='" + mr_no +
            "' ORDER BY CASE_NO DESC";
    }

    /**
     * 病患报到列表
     * @param mr_no String
     * @return String
     */
    public static String getPHLRegisterList(String mr_no) {
        return "SELECT DISTINCT A.START_DATE, A.ADM_TYPE, A.MR_NO, A.CASE_NO, "
            + " B.PAT_NAME, C.BED_NO "
            + " FROM PHL_ORDER A, SYS_PATINFO B, PHL_BED C "
            + " WHERE A.MR_NO = B.MR_NO "
            + " AND A.MR_NO = C.MR_NO "
            + " AND A.CASE_NO = C.CASE_NO "
            + " AND A.MR_NO = '" + mr_no + "' "
            + " ORDER BY A.START_DATE DESC ";
    }
}
