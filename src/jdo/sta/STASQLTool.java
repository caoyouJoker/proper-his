package jdo.sta;

import com.dongyang.jdo.*;

/**
 * <p>Title: STA  SQL语句Tool</p>
 *
 * <p>Description: STA  SQL语句Tool</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2009-5-25
 * @version 1.0
 */
public class STASQLTool extends TJDOTool {
    public STASQLTool() {
    }

    /**
     * 实例
     */
    public static STASQLTool instanceObject;

    /**
     * 得到实例
     * @return RegMethodTool
     */
    public static STASQLTool getInstance() {
        if (instanceObject == null)
            instanceObject = new STASQLTool();
        return instanceObject;
    }

    /**
     * 获取 查询 STA_DAILY_02表的SQL语句
     * @return String
     * ================pangben modify 20110519 添加区域参数
     */
    public String getSelectSTA_DAILY_02(String STA_DATE, String regionCode,String deptCode) {
        // ================pangben modify 20110519 start
    	 String region="";
         String dept="";
         if(null!=regionCode && regionCode.length()>0)
             region=" AND REGION_CODE='"+regionCode+"' ";
         if(null!=deptCode && deptCode.trim().length()>0)
             dept=" AND DEPT_CODE='"+deptCode+"' ";
        // ================pangben modify 20110519 stop
        String select = "SELECT " +
                        "STA_DATE, DEPT_CODE, STATION_CODE, " +
                        "DATA_01, DATA_02, DATA_03, " +
                        "DATA_04, DATA_05, DATA_06, " +
                        "DATA_07, DATA_08, DATA_08_1, " +
                        "DATA_09, DATA_10, DATA_11, " +
                        "DATA_12, DATA_13, DATA_14, " +
                        "DATA_15, DATA_15_1, DATA_16, " +
                        "DATA_17, DATA_18, DATA_19, " +
                        "DATA_20, DATA_21, DATA_22, " +
                        "DATA_23, DATA_24, DATA_25, " +
                        "DATA_26, DATA_27, DATA_28, " +
                        "DATA_29, DATA_30, DATA_31, " +
                        "DATA_32, DATA_33, DATA_34, " +
                        "DATA_35, DATA_36, DATA_37, " +
                        "DATA_38, DATA_39, DATA_40, " +
                        "DATA_41, DATA_41_1, DATA_41_2, " +
                        "CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                        "OPT_USER, OPT_DATE, OPT_TERM,SUBMIT_FLG " +
                        "FROM STA_DAILY_02 " +
                        "WHERE STA_DATE='" + STA_DATE + "'" + region+dept+" ORDER BY DEPT_CODE ";
        return select;
    }

    /**
     * 获取 工作统计报表 SQL语句
     * @return String
     */
    public String getWorkLogPrintSQL(String STA_DATE, String regionCode) {
        // ================pangben modify 20110520 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        // ================pangben modify 20110520 stop

        String sql = "SELECT " +
                     " SUBSTR(B.DEPT_CODE,0,1) AS DEPT1,SUBSTR(B.DEPT_CODE,1,3) DEPT2,B.DEPT_DESC," +
                     " A.STA_DATE, A.DEPT_CODE, A.STATION_CODE, " +
                     " A.DATA_01, A.DATA_02, A.DATA_03, " +
                     " A.DATA_04, A.DATA_05, A.DATA_06, " +
                     " A.DATA_07, A.DATA_08, A.DATA_08_1, " +
                     " A.DATA_09, A.DATA_10, A.DATA_11, " +
                     " A.DATA_12, A.DATA_13, A.DATA_14, " +
                     " A.DATA_15, A.DATA_15_1, A.DATA_16, " +
                     " A.DATA_17, A.DATA_18, A.DATA_19, " +
                     " A.DATA_20, A.DATA_21, A.DATA_22, " +
                     " A.DATA_23, A.DATA_24, A.DATA_25, " +
                     " A.DATA_26, A.DATA_27, A.DATA_28, " +
                     " A.DATA_29, A.DATA_30, A.DATA_31, " +
                     " A.DATA_32, A.DATA_33, A.DATA_34, " +
                     " A.DATA_35, A.DATA_36, A.DATA_37, " +
                     " A.DATA_38, A.DATA_39, A.DATA_40, " +
                     " A.DATA_41, A.DATA_41_1, A.DATA_41_2 " +
                     " FROM STA_DAILY_02 A,STA_OEI_DEPT_LIST B " +
                     " WHERE A.DEPT_CODE(+)=B.DEPT_CODE " +
                     " AND A.STA_DATE='" + STA_DATE + "'" + region;
        return sql;
    }

    /**
     * 获取STA_DAILY_02 月份合计数据  卫统2表1 使用
     * @param DATE_S String  起始日期 yyyyMMdd
     * @param DATE_E String  截止日期  yyyyMMdd
     * @param regionCode String  区域
     * @return String
     * ================pangben modify 20110520 添加区域参数
     */
    public String getSTA_DAILY_02_Sum(String DATE_S, String DATE_E,
                                      String regionCode) {
        // ================pangben modify 20110520 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        // ================pangben modify 20110520 stop
        String sql = "SELECT " +
                     " SUM(DATA_01) AS DATA_01, SUM(DATA_02) AS DATA_02, SUM(DATA_03) AS DATA_03,SUM(DATA_04) AS DATA_04, SUM(DATA_05) AS DATA_05,  " +
                     " SUM(DATA_06) AS DATA_06, SUM(DATA_08) AS DATA_08,SUM(DATA_09) AS DATA_09,SUM(DATA_10) AS DATA_10,SUM(DATA_11) AS DATA_11, " +
                     " SUM(DATA_12) AS DATA_12,SUM(DATA_13) AS DATA_13,SUM(DATA_14) AS DATA_14,SUM(DATA_17) AS DATA_17,SUM(DATA_18) AS DATA_18, " +
                     " AVG(DATA_19) AS DATA_19,SUM(DATA_20) AS DATA_20,SUM(DATA_21) AS DATA_21,AVG(DATA_31) AS DATA_31,AVG(DATA_32) AS DATA_32, " +
                     " AVG(DATA_33) AS DATA_33,SUM(DATA_34) AS DATA_34,SUM(DATA_35) AS DATA_35,AVG(DATA_36) AS DATA_36,SUM(DATA_15) AS DATA_15,SUM(DATA_15_1) AS DATA_15_1 " +
                     " FROM STA_DAILY_02 " +
                     " WHERE LENGTH(STA_DATE) = 8 " + region +
                     " AND TO_DATE(STA_DATE,'YYYYMMDD') BETWEEN TO_DATE('" +
                     DATE_S + "','YYYYMMDD') AND TO_DATE('" + DATE_E +
                     "'||'235959','YYYYMMDDHH24MISS') ";
        return sql;
    }

    /**
     * 获取STA_DAILY_02 某一天合计数据  卫统2表1 使用
     * @param STA_DATE String
     * @param regionCode String  区域
     * @return String
     * ================pangben modify 20110520 添加区域参数
     */
    public String getSTA_DAILY_02_DAY_SUM(String STA_DATE, String regionCode) {
        // ================pangben modify 20110520 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        // ================pangben modify 20110520 stop
        String sql = "SELECT " +
                     " SUM(DATA_01) AS DATA_01, SUM(DATA_02) AS DATA_02, SUM(DATA_03) AS DATA_03,SUM(DATA_04) AS DATA_04, SUM(DATA_05) AS DATA_05,  " +
                     " SUM(DATA_06) AS DATA_06, SUM(DATA_08) AS DATA_08,SUM(DATA_09) AS DATA_09,SUM(DATA_10) AS DATA_10,SUM(DATA_11) AS DATA_11, " +
                     " SUM(DATA_12) AS DATA_12,SUM(DATA_13) AS DATA_13,SUM(DATA_14) AS DATA_14,SUM(DATA_17) AS DATA_17,SUM(DATA_18) AS DATA_18, " +
                     " SUM(DATA_19) AS DATA_19,SUM(DATA_20) AS DATA_20,SUM(DATA_21) AS DATA_21,SUM(DATA_31) AS DATA_31,SUM(DATA_32) AS DATA_32, " +
                     " SUM(DATA_33) AS DATA_33,SUM(DATA_34) AS DATA_34,SUM(DATA_35) AS DATA_35,SUM(DATA_36) AS DATA_36 " +
                     " FROM STA_DAILY_02 " +
                     " WHERE STA_DATE = '" + STA_DATE + "' " + region;
        return sql;
    }

    /**
     * 获取门诊中间表的健康检查 月份合计统计  卫统2表1 使用
     * @param DATE_S String 起始日期 yyyyMMdd
     * @param DATE_E String 截止日期  yyyyMMdd
     * @param regionCode String 区域
     * @return String
     * ================pangben modify 20110519 添加区域
     */
    public String getSTA_OPD_DAILY_Sum(String DATE_S, String DATE_E,
                                       String regionCode) {
        // ================pangben modify 20110519 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "'";
        // ================pangben modify 20110519 stop

        String sql =
                "SELECT SUM(HRM_NUM) AS HRM_NUM,SUM(OTHER_NUM) AS OTHER_NUM " +
                " FROM STA_OPD_DAILY " +
                " WHERE LENGTH(STA_DATE) = 8 " + region +
                " AND TO_DATE(STA_DATE,'YYYYMMDD') BETWEEN TO_DATE('" + DATE_S +
                "','YYYYMMDD') AND TO_DATE('" + DATE_E +
                "'||'235959','YYYYMMDDHH24MISS')";
        return sql;
    }

    /**
     * 卫统2表1 STA_DAILY_01 上部分数据
     * @param sta_date String
     * @return String
     * ==============pangben modify 20110520 添加区域参数
     */
    public String getSTA_OUT_01_1(String sta_date, String regionCode) {
        //==============pangben modify 20110520 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //==============pangben modify 20110520 stop
        String sql = "SELECT STA_DATE,DATA_01,DATA_02,DATA_03,DATA_04,DATA_05," +
                     " DATA_06,DATA_07,DATA_08,DATA_09,DATA_10," +
                     " DATA_11,DATA_12,DATA_13,DATA_14,DATA_15," +
                     " DATA_16,DATA_17,DATA_35 " +
                     " FROM STA_OUT_01 WHERE STA_DATE='" + sta_date + "'" +
                     region;
        return sql;
    }

    /**
     * 卫统2表1 STA_DAILY_01 下部分数据
     * @param sta_date String
     * @return String
     * ==============pangben modify 20110520 添加区域参数
     */
    public String getSTA_OUT_01_2(String sta_date, String regionCode) {
        //==============pangben modify 20110520 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //==============pangben modify 20110520 stop

        String sql = "SELECT STA_DATE,DATA_18,DATA_19,DATA_20,DATA_21,DATA_22," +
                     " DATA_23,DATA_24,DATA_25,DATA_26,DATA_27," +
                     " DATA_28,DATA_29,DATA_30,DATA_31,DATA_32," +
                     " DATA_33,DATA_34,CONFIRM_FLG,CONFIRM_USER, " +
                     " CONFIRM_DATE,OPT_USER,OPT_DATE,OPT_TERM " +
                     " FROM STA_OUT_01 WHERE STA_DATE='" + sta_date + "'" +
                     region;
        return sql;
    }

    /**
     * 卫统5表1  STA_DAILY_02
     * @param STA_DATE String
     * @return String
     *  ==============pangben modify 20110520 添加区域参数
     */
    public String getSTA_OUT_02(String STA_DATE, String regionCode) {
        //==============pangben modify 20110520 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //==============pangben modify 20110520 stop

        String sql = "SELECT STA_DATE, SEQ, DATA_01, " +
                     " DATA_02, DATA_03, DATA_04, " +
                     " DATA_05, DATA_06, DATA_07, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_OUT_02 " +
                     " WHERE STA_DATE='" + STA_DATE + "' " + region +
                     " ORDER BY TO_NUMBER(SEQ) ";
        return sql;
    }

    /**
     * 卫统5表2 STA_DAILY_03
     * @param STA_DATE String 统计年月
     * @param DATA_TYPE String 统计类型  总计；男；女
     * @param regionCode String 区域
     * @return String
     * =============pangben modify 20110523 添加区域
     */
    public String getSTA_OUT_03(String STA_DATE, String DATA_TYPE,
                                String regionCode) {
        //=============pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //=============pangben modify 20110523 stop
        String sql = "SELECT " +
                     " STA_DATE, SEQ, DATA_TYPE, " +
                     " DATA_01, DATA_02, DATA_03, " +
                     " DATA_04, DATA_05, DATA_06, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_OUT_03 " +
                     " WHERE STA_DATE='" + STA_DATE + "' " +
                     " AND DATA_TYPE='" + DATA_TYPE + "' " + region +
                     " ORDER BY TO_NUMBER(SEQ)";
        return sql;
    }

    /**
     * 卫统2表3 STA_OUT_04 第一部分内容
     * @param STA_DATE String
     * @param regionCode String 区域
     * @return String
     * ====pangben modify 20110523 添加区域参数
     */
    public String getSTA_OUT_04_1(String STA_DATE, String regionCode) {
        //====pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //====pangben modify 20110523 stop
        String sql = "SELECT STA_DATE,DATA_01,DATA_02,DATA_03,DATA_04," +
                     " DATA_05,DATA_06,DATA_07,DATA_08,DATA_09," +
                     " DATA_10,DATA_11 " +
                     " FROM  STA_OUT_04 " +
                     " WHERE STA_DATE= '" + STA_DATE + "'" + region;
        return sql;
    }

    /**
     * 卫统2表3 STA_OUT_04 第二部分内容
     * @param STA_DATE String
     * @param regionCode String 区域
     * @return String
     * ====pangben modify 20110523 添加区域参数
     */
    public String getSTA_OUT_04_2(String STA_DATE, String regionCode) {
        //====pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //====pangben modify 20110523 stop

        String sql = "SELECT STA_DATE,DATA_12,DATA_13,DATA_14,DATA_15," +
                     " DATA_16,DATA_17,DATA_18,DATA_19,DATA_20," +
                     " DATA_21,DATA_22,DATA_23,DATA_24,DATA_25," +
                     " DATA_26,DATA_27,DATA_28 " +
                     " FROM  STA_OUT_04 " +
                     " WHERE STA_DATE='" + STA_DATE + "' " + region;
        return sql;
    }

    /**
     * 卫统2表3 STA_OUT_04 第三部分内容
     * @param STA_DATE String
     * @param regionCode String 区域
     * @return String
     * ====pangben modify 20110523 添加区域参数
     */
    public String getSTA_OUT_04_3(String STA_DATE, String regionCode) {
        //====pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //====pangben modify 20110523 stop

        String sql = "SELECT STA_DATE,DATA_29,DATA_30,DATA_31,DATA_32," +
                     " DATA_33,DATA_34,DATA_35,DATA_36,DATA_37," +
                     " DATA_38,DATA_39,DATA_40,DATA_41,DATA_42," +
                     " DATA_43,DATA_44,CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE," +
                     " OPT_USER,OPT_DATE,OPT_TERM " +
                     " FROM  STA_OUT_04 " +
                     " WHERE STA_DATE='" + STA_DATE + "' " + region;
        return sql;
    }

    /**
     * 卫统2表3 STA_OUT_04 第四部分内容
     * @param STA_DATE String
     * @param regionCode String 区域
     * @return String
     * ====pangben modify 20110523 添加区域参数
     */
    public String getSTA_OUT_04_4(String STA_DATE, String regionCode) {
        //====pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //====pangben modify 20110523 stop

        String sql = "SELECT STA_DATE,DATA_45,DATA_46,DATA_47,DATA_48," +
                     " DATA_49,DATA_50,DATA_51,DATA_52,DATA_53," +
                     " DATA_54,DATA_55,DATA_56,DATA_57,DATA_58," +
                     " DATA_59,DATA_60,DATA_61 " +
                     " FROM  STA_OUT_04 " +
                     " WHERE STA_DATE='" + STA_DATE + "' "+region;
        return sql;
    }

    /**
     * 卫统5表3 STA_OUT_05
     * @param STA_DATE String
     * @param regionCode String 区域
     * @return String
     * ====pangben modify 20110523 添加区域参数
     */
    public String getSTA_OUT_05(String STA_DATE,String regionCode) {
        //====pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //====pangben modify 20110523 stop
        String sql = "SELECT " +
                     " STA_DATE, SEQ, DATA_01," +
                     " DATA_02, DATA_03, DATA_04," +
                     " DATA_05, DATA_06,DATA_07,DATA_08, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_OUT_05 " +
                     " WHERE STA_DATE='" + STA_DATE + "' " +region+
                     " ORDER BY TO_NUMBER(SEQ)";
        return sql;
    }

    /**
     * 卫统32表2 STA_OUT_06
     * @param STA_DATE String  统计年月
     * @param DATA_TYPE String  统计类型  总计；男；女
     * @param regionCode String 区域
     * @return String
     * ====pangben modify 20110523 添加区域参数
     */
    public String getSTA_OUT_06(String STA_DATE, String DATA_TYPE,String regionCode) {
        //====pangben modify 20110523 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "' ";
        //====pangben modify 20110523 stop
        String sql = "SELECT " +
                     " STA_DATE, SEQ, DATA_TYPE, " +
                     " DATA_01, DATA_02, DATA_03, " +
                     " DATA_04, DATA_05, DATA_06,DATA_07, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_OUT_06 " +
                     " WHERE STA_DATE='" + STA_DATE + "' " +
                     " AND DATA_TYPE='" + DATA_TYPE + "' " +region+
                     " ORDER BY TO_NUMBER(SEQ)";
        return sql;
    }

    /**
     * 医院门、急诊工作统计报表 STA_IN_02
     * @param STA_DATE String
     * @return String
     * ===========pangben modify 20110523 添加区域参数
     */
    public String getSTA_IN_02(String STA_DATE,String regionCode,String deptCode) {
        //===========pangben modify 20110523 start
        String region="";
        String dept="";
        if(null!=regionCode&&regionCode.length()>0)
            region=" AND REGION_CODE='"+regionCode+"' ";
        if(null!=deptCode&&deptCode.length()>0)
            dept=" AND DEPT_CODE='"+deptCode+"' ";
        //===========pangben modify 20110523 stop
        String sql = "SELECT STA_DATE,DEPT_CODE,STATION_CODE,DATA_02,DATA_03," +
                     " DATA_04,DATA_05,DATA_06,DATA_07,DATA_08," +
                     " DATA_09,DATA_10,DATA_11,DATA_12,DATA_13," +
                     " DATA_14,DATA_15,DATA_16,DATA_17,DATA_18," +
                     " DATA_19,DATA_20,DATA_21,DATA_22,DATA_23," +
                     " DATA_24,DATA_25,DATA_26,DATA_27,DATA_28," +
                     " DATA_29,DATA_30,DATA_31,DATA_32,DATA_33," +
                     " DATA_34,CONFIRM_FLG,CONFIRM_USER,CONFIRM_DATE,OPT_USER," +
                     " OPT_DATE,OPT_TERM,REGION_CODE " +
                     " FROM STA_IN_02 " +
                     " WHERE STA_DATE='" + STA_DATE + "'" +region+dept+
                     " ORDER BY DEPT_CODE";
        return sql;
    }

    /**
     * 医院住院病患动态及疗效报表 STA_IN_03
     * @param STA_DATE String
     * @return String
     * ==============pangben modify 20110523 添加区域参数
     */
    public String getSTA_IN_03(String STA_DATE,String regionCode,String deptCode) {
        //==============pangben modify 20110523 start
    	 String region="";
         String dept="";
         if(null!=regionCode&&regionCode.length()>0)
             region=" AND REGION_CODE='"+regionCode+"' ";
         if(null!=deptCode&&deptCode.length()>0)
             dept=" AND DEPT_CODE='"+deptCode+"' ";
        //==============pangben modify 20110523 stop
        String sql = "SELECT " +
                     " STA_DATE, DEPT_CODE, STATION_CODE,REGION_CODE, " +
                     " DATA_01, DATA_02, DATA_03, " +
                     " DATA_04, DATA_05, DATA_06, " +
                     " DATA_07, DATA_08, DATA_09, " +
                     " DATA_10, DATA_11, DATA_12, " +
                     " DATA_13, DATA_14, DATA_15, " +
                     " DATA_16, DATA_17, DATA_18, " +
                     " DATA_19, DATA_20, DATA_21, " +
                     " DATA_22, DATA_23, DATA_24, " +
                     " DATA_25, DATA_26, DATA_27, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_IN_03 " +
                     " WHERE STA_DATE='" + STA_DATE + "'" +region+dept+
                     " ORDER BY DEPT_CODE";
        return sql;
    }

    /**
     * 医院医疗诊断质量报表  STA_IN_04
     * @param STA_DATE String
     * @return String
     * ===========pangben modfiy 20110524 添加区域参数
     */
    public String getSTA_IN_04(String STA_DATE,String regionCode,String deptCode) {
        //=============pangben modfiy 20110524 start
    	 String region="";
         String dept="";
         if(null!=regionCode&&regionCode.length()>0)
             region=" AND REGION_CODE='"+regionCode+"' ";
         if(null!=deptCode&&deptCode.length()>0)
             dept=" AND DEPT_CODE='"+deptCode+"' ";
        //=============pangben modfiy 20110524 stop
        String sql = "SELECT " +
                     " STA_DATE, DEPT_CODE, STATION_CODE,REGION_CODE," +
                     " DATA_01, DATA_02, DATA_03, " +
                     " DATA_04, DATA_05, DATA_06, " +
                     " DATA_07, DATA_08, DATA_09, " +
                     " DATA_10, DATA_11, DATA_12, " +
                     " DATA_13, DATA_14, DATA_15, " +
                     " DATA_16, DATA_17, DATA_18, " +
                     " DATA_19, DATA_20, DATA_21, " +
                     " DATA_22, DATA_23, DATA_24, " +
                     " DATA_25, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_IN_04 " +
                     " WHERE STA_DATE='" + STA_DATE + "'" +region+dept+
                     " ORDER BY DEPT_CODE";
        return sql;
    }

    /**
     * 出院者来源及其他项目报表  STA_IN_05
     * @param STA_DATE String
     * @return String
     * ===============pangben modify 20110524  添加区域\科室参数
     */
    public String getSTA_IN_05(String STA_DATE,String regionCode,String deptCode) {
        //===============pangben modify 20110524 start
        String region="";
        String dept="";
        if(null!=regionCode && regionCode.length()>0)
            region=" AND REGION_CODE='"+regionCode+"' ";
        if(null!=deptCode && deptCode.trim().length()>0)
            dept=" AND DEPT_CODE='"+deptCode+"' ";
        //===============pangben modify 20110524 stop

        String sql = "SELECT " +
                     " STA_DATE, DEPT_CODE, STATION_CODE, " +
                     " DATA_01, DATA_02, DATA_03, " +
                     " DATA_04, DATA_05, DATA_06, " +
                     " DATA_07, DATA_08, DATA_09, " +
                     " DATA_10, DATA_11, DATA_12, " +
                     " DATA_13, DATA_14, DATA_15, " +
                     " DATA_16, DATA_17, DATA_18, " +
                     " DATA_19, DATA_20, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM ,REGION_CODE" +
                     " FROM STA_IN_05 " +
                     " WHERE STA_DATE='" + STA_DATE + "'" +region+dept+
                     " ORDER BY DEPT_CODE";
        return sql;
    }

    /**
     * 危急患者疗效分析报表 STA_IN_06
     * @param STA_DATE String
     * @return String
     * =============pangben modify 20110525 添加区域参数
     */
    public String getSTA_IN_06(String STA_DATE,String regionCode,String deptCode) {
        //=============pangben modify 20110525 start
    	  String region="";
          String dept="";
          if(null!=regionCode && regionCode.length()>0)
              region=" AND REGION_CODE='"+regionCode+"' ";
          if(null!=deptCode && deptCode.trim().length()>0)
              dept=" AND DEPT_CODE='"+deptCode+"' ";
        //=============pangben modify 20110525 stop
        String sql = "SELECT " +
                     " STA_DATE, DEPT_CODE, STATION_CODE,REGION_CODE, " +
                     " DATA_01, DATA_02, DATA_03, " +
                     " DATA_04, DATA_05, DATA_06, " +
                     " DATA_07, DATA_08, DATA_09, " +
                     " DATA_10, DATA_11, DATA_12, " +
                     " DATA_13, DATA_14, DATA_15, " +
                     " DATA_16, DATA_17, DATA_18, " +
                     " DATA_19, DATA_20, DATA_21, " +
                     " DATA_22, DATA_23, DATA_24,DATA_25,DATA_26,DATA_27 " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_IN_06 " +
                     " WHERE STA_DATE='" + STA_DATE + "'" +region+dept+
                     " ORDER BY DEPT_CODE";
        return sql;
    }

    /**
     * 医院手术情况报表 STA_IN_07
     * @param STA_DATE String
     * @return String
     * ================pangben modify 20110525 添加区域参数
     */
    public String getSTA_IN_07(String STA_DATE,String regionCode,String deptCode) {
        //================pangben modify 20110525 start
    	 String region="";
         String dept="";
         if(null!=regionCode && regionCode.length()>0)
             region=" AND REGION_CODE='"+regionCode+"' ";
         if(null!=deptCode && deptCode.trim().length()>0)
             dept=" AND DEPT_CODE='"+deptCode+"' ";
        //================pangben modify 20110525 stop
        String sql = "SELECT " +
                     " STA_DATE, DEPT_CODE, STATION_CODE,REGION_CODE, " +
                     " DATA_01, DATA_02, DATA_03, " +
                     " DATA_04, DATA_05, DATA_06, " +
                     " DATA_07, DATA_08, DATA_09, " +
                     " DATA_10, DATA_11, DATA_12, " +
                     " DATA_13, DATA_14, DATA_15, " +
                     " DATA_16, DATA_17, DATA_18, " +
                     " DATA_19, DATA_20, DATA_21, " +
                     " DATA_22, DATA_23, DATA_24, " +
                     " DATA_25, DATA_26, DATA_27, " +
                     " CONFIRM_FLG, CONFIRM_USER, CONFIRM_DATE, " +
                     " OPT_USER, OPT_DATE, OPT_TERM " +
                     " FROM STA_IN_07 " +
                     " WHERE STA_DATE='" + STA_DATE + "'" +region+dept+
                     " ORDER BY DEPT_CODE";
        return sql;
    }

    /**
     * 门急诊日志
     * @param STA_DATE String
     * @return String
     * ============pangben modify 20110519 添加区域参数
     */
    public String getSTAOPDLog(String STA_DATE, String regionCode) {
        //============pangben modify 20110519 start
        String region = "";
        if (null != regionCode && regionCode.length() > 0)
            region = " AND REGION_CODE='" + regionCode + "'";
        //============pangben modify 20110519 stop
        String sql = "SELECT " +
                     " STA_DATE, DEPT_CODE, OUTP_NUM, " +
                     " ERD_NUM, HRM_NUM, OTHER_NUM, " +
                     " GET_TIMES, PROF_DR, COMM_DR, " +
                     " DR_HOURS, SUCCESS_TIMES, OBS_NUM, " +
                     " ERD_DIED_NUM, OBS_DIED_NUM, OPE_NUM, " +
                     " FIRST_NUM, FURTHER_NUM,APPT_NUM,ZR_DR_NUM,ZZ_DR_NUM,ZY_DR_NUM,ZX_DR_NUM,OPT_USER, " +
                     " OPT_DATE, OPT_TERM, CONFIRM_FLG, " +
                     " CONFIRM_USER, CONFIRM_DATE,REGION_CODE " +
                     " FROM STA_OPD_DAILY " +
                     " WHERE STA_DATE = '" + STA_DATE + "' " + region +
                     " ORDER BY DEPT_CODE";
        return sql;
    }

    /**
     * 根据部门获取本部门医师
     * @param DEPT_CODE String
     * @return String
     */
    public String getSQLDrByDept(String DEPT_CODE) {
        String sql = "SELECT A.USER_NAME,A.USER_ID,A.NAME_PYCODE,B.DEPT_CODE " +
                     " FROM SYS_OPERATOR A,SYS_OPERATOR_DEPT B " +
                     " WHERE A.USER_ID=B.USER_ID " +
                     " AND A.HOSP_AREA=B.HOSP_AREA " +
                     " AND B.MAIN_FLG='Y' " +
                     " AND B.DEPT_CODE ='" + DEPT_CODE + "'";
        return sql;
    }

    /**
     * 查询医师 （门急诊医生，住院医生，门住医生，）
     * @return String
     */
    public String getOPDrSQL() {
        String sql = "SELECT USER_NAME,USER_ID,NAME_PYCODE" +
                     " FROM SYS_OPERATOR  WHERE ROLE_ID IN ('ODO','ODI','OIDR') " +
                     " AND END_DATE >SYSDATE AND ACTIVE_DATE < SYSDATE";
        return sql;
    }

    /**
     * 查询医疗统计部门中间表中的病区部门
     * @return String
     * ======pangben modify 20110523 添加区域参数
     */
    public String get_STA_Station(String regionCode) {
        //=============pangben modify 20110523 start
        String region="";
        if(null!=regionCode && regionCode.length()>0)
            region=" AND REGION_CODE='"+regionCode+"' ";
        //=============pangben modify 20110523 stop
        String sql = "SELECT DEPT_CODE,DEPT_DESC,DEPT_LEVEL,IPD_DEPT_CODE,OE_DEPT_CODE,STATION_CODE,SEQ,PY1,REGION_CODE " +
                     " FROM STA_OEI_DEPT_LIST " +
                     " WHERE IPD_DEPT_CODE IS NOT NULL "+region+"ORDER BY TO_NUMBER(SEQ)";
        return sql;
    }
}
