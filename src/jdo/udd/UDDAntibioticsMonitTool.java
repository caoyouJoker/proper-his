package jdo.udd;

import java.util.ArrayList;
import java.util.List;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: 抗菌药物临床应用监测</p>
 *
 * <p>Description:抗菌药物临床应用监测 </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class UDDAntibioticsMonitTool  extends TJDOTool {
    
     /**
     * 实例
     */
    public static UDDAntibioticsMonitTool instanceObject;
    /**
     * 得到实例
     * @return 
     */
    public static UDDAntibioticsMonitTool getInstance() {
        if (instanceObject == null) {
            instanceObject = new UDDAntibioticsMonitTool();
        }
        return instanceObject;
    }
    
    
    public TParm onQuery(TParm parm){
        String startDate = parm.getValue("START_DATE");
        String endDate = parm.getValue("END_DATE");
        startDate = startDate.substring(0, startDate.length() - 2);
        endDate = endDate.substring(0, endDate.length() - 2);
        
        String conditionSql = "";
        //病案号
        if(!StringUtil.isNullString(parm.getValue("MR_NO"))){
            conditionSql += " AND A.MR_NO='"+parm.getValue("MR_NO")+"' ";
        }
        //出院科室
        String deptCode = parm.getValue("DEPT_CODE");
        if(!StringUtil.isNullString(deptCode)){
            conditionSql += " AND A.OUT_DEPT='"+deptCode+"' ";
        }
        if (!parm.getValue("ALL").equals("Y")) {
            // 手术
            String surgery = parm.getValue("SURGERY");
            String dead = parm.getValue("DEAD");
            if (!StringUtil.isNullString(surgery) && surgery.equals("Y")) {
                conditionSql += " AND A.OP_CODE IS NOT NULL ";// 手术
                if (!StringUtil.isNullString(dead) && dead.equals("N")) {
                    conditionSql += "  AND A.OUT_TYPE<> '4' ";// 非死亡
                } else if (!StringUtil.isNullString(dead) && dead.equals("Y")) {
                    conditionSql += "  AND A.OUT_TYPE = '4' ";// 死亡
                }
            }
            // 非手术
            String unSurgery = parm.getValue("UNSURGERY");
            if (!StringUtil.isNullString(unSurgery) && unSurgery.equals("Y")) {
                conditionSql += " AND A.OP_CODE IS NULL ";// 非手术
                if (!StringUtil.isNullString(dead) && dead.equals("N")) {
                    conditionSql += "  AND A.OUT_TYPE<> '4' ";// 非死亡
                } else if (!StringUtil.isNullString(dead) && dead.equals("Y")) {
                    conditionSql += "  AND A.OUT_TYPE = '4' ";// 死亡
                }
            }
        }
        //切口等级 add by wanglong 20130802
        String healLevel=parm.getValue("HEAL_LEVEL");
        if(!healLevel.equals("")){
            if(healLevel.equals("01")){
                conditionSql +="  AND A.HEAL_LV IN ('11','12','13','14') ";
            }else if(healLevel.equals("02")){
                conditionSql +="  AND A.HEAL_LV IN ('21','22','23','24') ";
            }else if(healLevel.equals("03")){
                conditionSql +="  AND A.HEAL_LV IN ('31','32','33','34') ";
            }
        }

        // 是否使用抗菌药物 add by wanglong 20130802
        String useAntibiotic = parm.getValue("USE_ANTIBIOTIC");
        String antibioticWayCondition = "";
        String antibioticWay = parm.getValue("ANTIBIOTIC_WAY"); // 抗菌标识
        if (useAntibiotic.equals("Y")) {
            conditionSql += " AND A.CHARGE_16 > 0 ";
            // 抗菌标识
            antibioticWayCondition += "AND B.ANTIBIOTIC_WAY ='" + antibioticWay + "' ";
        } else {
//            conditionSql += " AND A.CHARGE_16 <= 0 AND 1=2 ";//为确保查不出数据。因为不使用抗菌药物时，预防使用都是N
            conditionSql += " AND A.CHARGE_16 <= 0 ";//20150727 wangjc add
        }
        // ==========add by wanglong 20130807
        String caseNoSql =
                "SELECT CASE_NO FROM MRO_RECORD A WHERE A.OUT_DATE BETWEEN TO_DATE('#','yyyy-MM-dd HH24:MI:SS') "
                        + "                                            AND TO_DATE('#','yyyy-MM-dd HH24:MI:SS') "
                        + conditionSql;
        caseNoSql = caseNoSql.replaceFirst("#", startDate);
        caseNoSql = caseNoSql.replaceFirst("#", endDate);
//        System.out.println("caseNoSql======"+caseNoSql);
        TParm caseNoResult = new TParm(TJDODBTool.getInstance().select(caseNoSql));
        if (caseNoResult.getErrCode() < 0 || caseNoResult.getCount() < 1) {
            return caseNoResult;
        }
        // ==========add end
        String caseNoWhere1 = getInStatement("B.CASE_NO",caseNoResult);
        String caseNoWhere2 = getInStatement("A.CASE_NO",caseNoResult);
        String sql = "SELECT '' CHOOSE, A.IN_DATE, A.OUT_DATE, A.OUT_DEPT, A.MR_NO, A.PAT_NAME,A.SEX, A.AGE, "+  
                    "   (SELECT ( CASE WHEN G.WEIGHT > 0 THEN TO_CHAR(G.WEIGHT) ELSE ' ' END ) " +
                    "   FROM ADM_INP G WHERE A.CASE_NO=G.CASE_NO AND  A.MR_NO=G.MR_NO) NB_WEIGHT, "+//体重
                    "(SELECT A.IN_DIAG_CODE || ' ' || B.ICD_CHN_DESC FROM SYS_DIAGNOSIS B " +
                    "  WHERE B.ICD_CODE=A.IN_DIAG_CODE AND B.ICD_TYPE='W') INTE_DIAG_CODE, "+  //诊断1
                    "(SELECT A.OUT_DIAG_CODE1 || ' ' || C.ICD_CHN_DESC FROM SYS_DIAGNOSIS C " +
                    "  WHERE C.ICD_CODE=A.OUT_DIAG_CODE1 AND C.ICD_TYPE='W') OUT_DIAG_CODE1, "+  //诊断2
                    "(SELECT A.OUT_DIAG_CODE2 || ' ' || D.ICD_CHN_DESC FROM SYS_DIAGNOSIS D " +
                    "  WHERE D.ICD_CODE=A.OUT_DIAG_CODE2 AND D.ICD_TYPE='W') OUT_DIAG_CODE2, "+  //诊断3
                    "(SELECT A.OUT_DIAG_CODE3 || ' ' || E.ICD_CHN_DESC FROM SYS_DIAGNOSIS E " +
                    "  WHERE E.ICD_CODE=A.OUT_DIAG_CODE3 AND E.ICD_TYPE='W' ) OUT_DIAG_CODE3, "+  //诊断4
                    "(SELECT A.OUT_DIAG_CODE4 || ' ' || F.ICD_CHN_DESC FROM SYS_DIAGNOSIS F " +
                    "  WHERE F.ICD_CODE=A.OUT_DIAG_CODE4 AND F.ICD_TYPE='W') OUT_DIAG_CODE4, "+  //诊断5
                    " A.ALLEGIC, " +//过敏史
                    "A.SUM_TOT," +//住院总费用
                    "(A.CHARGE_16 + A.CHARGE_17 + A.CHARGE_18 + A.CHARGE_19) MEDICAL_TOT," +//药品总费用
                    "TO_CHAR(A.CHARGE_16,'FM999990.09') CHARGE_16, "+  //抗生素总费用
                    "(SELECT A.OP_CODE || ' ' || H.OPT_CHN_DESC FROM SYS_OPERATIONICD H " +
                    "  WHERE A.OP_CODE=H.OPERATION_ICD) OP_CODE," +//手术名称
                    "(SELECT A.HEAL_LV || ' ' || I.CHN_DESC FROM SYS_DICTIONARY I " +
                    "  WHERE I.ID=A.HEAL_LV  AND I.GROUP_ID='MRO_HEALTHLEVEL') HEAL_LV, " +//切口类别
                    //modify by wanglong 20130801
                    "ROUND((SELECT SUM(B.DOSAGE_QTY * C.MEDI_QTY / D.DDD) FROM IBS_ORDD B, PHA_TRANSUNIT C, PHA_BASE D " +
                    "  WHERE B.CAT1_TYPE = 'PHA' AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE " +
                    "    AND D.ANTIBIOTIC_CODE IS NOT NULL AND (#) AND A.CASE_NO = B.CASE_NO "+ conditionSql +"),2) DDD_SUM," +//DDD累积量
                    " A.CASE_NO,A.REAL_STAY_DAYS," +
//                    " 0 DDD, " +//抗生素强度
//                    " (SELECT CASE WHEN SUM(C.CO_USE) > 0 THEN 'Y' ELSE 'N' END " +
//                    "   FROM (SELECT B.CASE_NO, CASE WHEN COUNT(DISTINCT ORDER_CODE) > 1 THEN 1 ELSE 0 END CO_USE " +
//                    "           FROM ODI_DSPNM B " +
//                    "          WHERE B.CAT1_TYPE = 'PHA' " +
//                    "            AND B.ANTIBIOTIC_CODE IS NOT NULL " +
//                    "            AND (#) " +
//                    "            AND NS_EXEC_DATE IS NOT NULL " +
//                    "       GROUP BY CASE_NO, TRUNC(B.NS_EXEC_DATE)) C " +
//                    "  WHERE A.CASE_NO = C.CASE_NO) CO_USE," +//联合使用
                    " '' CO_USE,"+
                    " (SELECT CASE WHEN COUNT(*) > 0 THEN 'Y' ELSE 'N' END " +
                    "    FROM ODI_ORDER B WHERE B.CAT1_TYPE = 'PHA' AND (#) AND A.CASE_NO = B.CASE_NO "+ antibioticWayCondition + conditionSql +") PRE_USE," +//预防使用
                    " CASE WHEN A.RT_FLG = 'Y' THEN 'Y' ELSE 'N' END RT_FLG," +//时机合理
                    " CASE WHEN A.RC_FLG = 'Y' THEN 'Y' ELSE 'N' END RC_FLG "+//合理治疗
                    " FROM MRO_RECORD A " +
                    " WHERE A.OUT_DATE BETWEEN TO_DATE('"+startDate+"','yyyy-MM-dd HH24:MI:SS') " +
                    "                      AND TO_DATE('"+endDate+"','yyyy-MM-dd HH24:MI:SS') AND (#) " + conditionSql +
                    " ORDER BY A.IN_DATE ";//根据入院日期排序
        sql = sql.replaceFirst("#", caseNoWhere1).replaceFirst("#", caseNoWhere1);
        sql = sql.replaceFirst("#", caseNoWhere2);
//        System.out.println("============queryPatTable========="+sql);
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0 || result.getCount() < 1) {
            return result;
        }
        String coSql =
                "SELECT B.CASE_NO FROM ODI_DSPNM B WHERE B.CAT1_TYPE = 'PHA' "
                        + " AND B.ANTIBIOTIC_CODE IS NOT NULL AND B.CASE_NO = '#' AND NS_EXEC_DATE IS NOT NULL "
                        + " GROUP BY CASE_NO, TRUNC(B.NS_EXEC_DATE) HAVING COUNT(DISTINCT ORDER_CODE) > 1";
        for (int i = 0; i < caseNoResult.getCount(); i++) {
            TParm coResult =
                    new TParm(TJDODBTool.getInstance().select(coSql.replaceFirst("#", caseNoResult.getValue("CASE_NO", i))));
            if (coResult.getErrCode() < 0) {
                return coResult;
            }
            result.setData("CO_USE", i, coResult.getCount() > 0 ? "Y" : "N");
        }
        return result;
    }
    
    /**
     * 保存
     * @return
     */
    public TParm onSave(TParm parm) {//add by wanglong 20130802
        String updateSql = " UPDATE MRO_RECORD SET RT_FLG = '#', RC_FLG = '#' WHERE CASE_NO = '#' ";
        String realSql = "";
        String[] sql = new String[parm.getCount("MR_NO")];
        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
            realSql = updateSql.replaceFirst("#", parm.getValue("RT_FLG", i));
            realSql = realSql.replaceFirst("#", parm.getValue("RC_FLG", i));
            realSql = realSql.replaceFirst("#", parm.getValue("CASE_NO", i));
            sql[i] = realSql;
        }
        return new TParm(TJDODBTool.getInstance().update(sql));
    }
    
    /**
     * 第二页签根据CASE_NO查询
     * @param parm
     * @return
     */
    public TParm onQueryMedical(TParm parm ){
        String caseNoCondition = "";
        caseNoCondition = getInStatement("A.CASE_NO", parm);
                
        String sql = "SELECT A.MR_NO, B.PAT_NAME PAT_NAME,A.ORDER_DESC,C.ANTIBIOTIC_DESC ANTIBIOTIC_CODE,TO_CHAR(A.MEDI_QTY,'FM999990.09')||D.UNIT_CHN_DESC MEDI_QTY," +
                            "A.FREQ_CODE,A.ROUTE_CODE,TO_CHAR(A.ACUMMEDI_QTY,'FM999990.09')||D.UNIT_CHN_DESC ACUMMEDI_QTY," +
                            "(CASE WHEN TO_CHAR(A.DC_DATE,'MMDDHH24MI') <> ' ' THEN (TO_CHAR(A.EFF_DATE,'MMDDHH24MI')||'-'||TO_CHAR(A.DC_DATE,'MMDDHH24MI')) ELSE TO_CHAR(A.EFF_DATE,'MMDDHH24MI') END )   EDDATE, "+
                            "( SELECT O.ORDER_DESC FROM ODI_ORDER O WHERE O.CASE_NO= A.CASE_NO AND  O.LINK_NO=A.LINK_NO AND O.LINKMAIN_FLG='Y' AND ROWNUM=1 ) ORDER_DESC_Y "+
                     "FROM ODI_ORDER A,SYS_PATINFO B,SYS_ANTIBIOTIC C,SYS_UNIT D "+
                     "WHERE  "+caseNoCondition+ "  AND   A.CAT1_TYPE='PHA' AND A.ANTIBIOTIC_CODE IS NOT NULL AND " +
                           "A.MR_NO= B.MR_NO AND "+
                           "A.ANTIBIOTIC_CODE=C.ANTIBIOTIC_CODE(+) AND  "+
                           "A.MEDI_UNIT=D.UNIT_CODE(+) ";
        TParm result=new TParm(TJDODBTool.getInstance().select(sql));
        return result;
    }
    
    /**
     * 生成超过1000元素的CASE_NO IN()语句
     * @param name
     * @param Nos
     * @return
     */
    public static String getInStatement(String name, TParm Nos) {//modify by wanglong 20130806
        if (Nos.getCount("CASE_NO") < 1) {
            return " 1=1 ";
        }
        StringBuffer inStr = new StringBuffer();
        inStr.append(name + " IN ('");
        for (int i = 0; i < Nos.getCount("CASE_NO") ; i++) {
            inStr.append(Nos.getValue("CASE_NO", i));
            if ((i + 1) != Nos.getCount("CASE_NO")) {
                if ((i + 1) % 999 != 0) {
                    inStr.append("','");
                } else if (((i + 1) % 999 == 0)) {
                    inStr.append("') OR " + name + " IN ('");
                }
            }
        }
        inStr.append("')");
        return inStr.toString();
    }


}
