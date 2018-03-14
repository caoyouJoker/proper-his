package jdo.sta;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * <p>Title: STA_IN_15付款方式查询 </p>
 *
 * <p>Description: STA_IN_15付款方式查询 </p>
 *
 * <p>Copyright: Copyright (c) 2013 </p>
 *
 * <p>Company: BlueCore </p>
 *
 * @author WangLong 20131008
 * @version 1.0
 */
public class STAIn_15Tool extends TJDOTool {
    /**
     * 实例
     */
    public static STAIn_15Tool instanceObject;

    /**
     * 得到实例
     * @return STAIn_10Tool
     */
    public static STAIn_15Tool getInstance() {
        if (instanceObject == null)
            instanceObject = new STAIn_15Tool();
        return instanceObject;
    }

    /**
     * 门急诊付款方式查询
     * @param parm
     * @return
     */
    public TParm selectOPD(TParm parm){
        String sql = // modify by wanglong 20140122
                "SELECT A.DEPT_CODE, A.CTZ_CODE, B.CTZ_DESC, B.PY1 PY, COUNT(*) COUNT "
                        + "  FROM (SELECT A.CASE_NO, A.DEPT_CODE,"  //modify by huangtt 20150902 REALDEPT_CODE 改为 dept_code 
                        + "               CASE WHEN A.CTZ1_CODE IS NULL THEN '99' ELSE A.CTZ1_CODE END CTZ_CODE "
                        + "          FROM REG_PATADM A, BIL_REG_RECP B"
                        + "         WHERE A.REGCAN_USER IS NULL " 
                        + "			AND A.CASE_NO = B.CASE_NO " //add by huangtt 20141224 
                        + "           AND B.BILL_DATE BETWEEN TO_DATE( '#', 'YYYY/MM/DD HH24:MI:SS') "  //modify by huangtt 20141224  adm_date  改为bill_date
                        + "                              AND TO_DATE( '#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "       ) A, SYS_CTZ B " + " WHERE A.CTZ_CODE = B.CTZ_CODE  #  "
                        + "GROUP BY A.DEPT_CODE, A.CTZ_CODE, B.CTZ_DESC, B.PY1 "
                        + "ORDER BY A.DEPT_CODE, A.CTZ_CODE";
        sql = sql.replaceFirst("#", parm.getValue("START_DATE"));
        sql = sql.replaceFirst("#", parm.getValue("END_DATE"));
        if (parm.getData("DEPT_CODE") != null) {
            sql = sql.replaceFirst("#", "AND DEPT_CODE = '" + parm.getValue("DEPT_CODE") + "'");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 出院付款方式查询
     * @param parm
     * @return
     */
    public TParm selectIPD(TParm parm){
        String sql = // modify by wanglong 20131024
                "SELECT A.DS_DEPT_CODE DEPT_CODE, A.CTZ1_CODE CTZ_CODE, B.CTZ_DESC, B.PY1 PY, COUNT(A.CASE_NO) COUNT "
                        + "  FROM ADM_INP A,SYS_CTZ B "
                        + " WHERE A.CANCEL_FLG = 'N' "
                        + "   AND A.DS_DATE BETWEEN TO_DATE( '#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "                     AND TO_DATE( '#', 'YYYY/MM/DD HH24:MI:SS') "
                        + "   # "
                        + "   AND A.CTZ1_CODE = B.CTZ_CODE "
                        + "GROUP BY A.DS_DEPT_CODE, A.CTZ1_CODE, B.CTZ_DESC, B.PY1 "
                        + "ORDER BY A.DS_DEPT_CODE, A.CTZ1_CODE";
        sql = sql.replaceFirst("#", parm.getValue("START_DATE"));
        sql = sql.replaceFirst("#", parm.getValue("END_DATE"));
        if (parm.getData("DEPT_CODE") != null) {
            sql =
                    sql.replaceFirst("#", "AND A.DS_DEPT_CODE = '" + parm.getValue("DEPT_CODE")
                            + "'");
        } else {
            sql = sql.replaceFirst("#", "");
        }
        TParm result = new TParm(TJDODBTool.getInstance().select(sql));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
