package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import java.util.Map;

/**
 * <p>Title:临床路径变异分析 </p>
 *
 * <p>Description:临床路径变异分析 </p>
 *
 * <p>Copyright: Copyright (c) 2011</p>
 *
 * <p>Company: </p>
 *
 * @author pangben 20110708
 * @version 1.0
 */
public class CLPVariationShowTool extends TJDOTool {
    public CLPVariationShowTool() {
        setModuleName("clp\\CLPVariationShowModule.x");
        onInit();
    }

    /**
     * 实例
     */
    public static CLPVariationShowTool instanceObject;

    /**
     * 得到实例
     * @return IBSTool
     */
    public static CLPVariationShowTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPVariationShowTool();
        return instanceObject;
    }

    /**
     * 标准有实际没有
     * @param parm TParm
     */
    public TParm selectDataStadardWithoutReal( TParm parm) {
        String regionCode=parm.getValue("REGION_CODE");
        String start_date=parm.getValue("DATE_S");
        String end_date=parm.getValue("DATE_E");
        String clncPathCode=parm.getValue("CLNCPATH_CODE");
        String checkTypeCode=parm.getValue("CHKTYPE_CODE");
        //查询条件begin
        String conditionStr="  ";
        if(this.checkNullAndEmpty(clncPathCode)){
             conditionStr+=" AND D.CLNCPATH_CODE='"+clncPathCode+"' ";
        }
        if(this.checkNullAndEmpty(checkTypeCode)){
            conditionStr+=" AND D.CHKTYPE_CODE='"+checkTypeCode+"' ";
        }
        //查询条件end
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("SELECT ALLTABLE.* FROM  ");
        sqlbf.append("(");
        sqlbf.append("(");
        sqlbf.append("SELECT A.REGION_CODE,BSCINF.CLNCPATH_CHN_DESC,A.CLNCPATH_CODE, MAINCOUNT AS MAIN_COUNT,ORDER_DESC,DURATION_CHN_DESC AS SCHD_DESC,CHKTYPE_CHN_DESC,STANDCOUNT AS STANDARD_COUNT,MAINCOUNT/STANDCOUNT AS PER ");
        sqlbf.append("FROM ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT MR.REGION_CODE,D.CLNCPATH_CODE,COUNT(D.CASE_NO) AS MAINCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE  ");
        sqlbf.append(" FROM CLP_MANAGED D,MRO_RECORD MR ");
        sqlbf.append(" WHERE   D.ORDER_CODE IS NOT NULL  AND  D.MAINORD_CODE IS   NULL AND D.ORDER_FLG='Y' ");
        sqlbf.append("  AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append(" AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append(" GROUP BY  MR.REGION_CODE,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE,D.CLNCPATH_CODE ");
        sqlbf.append(" )A, ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT MR.REGION_CODE,D.CLNCPATH_CODE, COUNT(D.CASE_NO) AS STANDCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE  ");
        sqlbf.append(" FROM CLP_MANAGED D,MRO_RECORD MR ");
        sqlbf.append(" WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='Y' AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append(" AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append(" GROUP BY MR.REGION_CODE,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE ,D.CLNCPATH_CODE");
        sqlbf.append(" )B, ");
        sqlbf.append(" SYS_FEE FE,CLP_CHKTYPE CH,CLP_DURATION DR,CLP_BSCINFO BSCINF  ");
        sqlbf.append(" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE ");
        sqlbf.append(" AND A.CLNCPATH_CODE=B.CLNCPATH_CODE ");
        sqlbf.append(" AND A.CLNCPATH_CODE=BSCINF.CLNCPATH_CODE(+) ");
        sqlbf.append(" AND FE.ORDER_CODE=B.ORDER_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE  ");
        sqlbf.append(" ) ");
        sqlbf.append(" UNION ALL ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT  A.REGION_CODE,BSCINF.CLNCPATH_CHN_DESC,A.CLNCPATH_CODE, MAINCOUNT,ITEM.CHKITEM_CHN_DESC AS ORDER_DESC,DURATION_CHN_DESC,CHKTYPE_CHN_DESC,STANDCOUNT,MAINCOUNT/STANDCOUNT AS PERCENT ");
        sqlbf.append(" FROM ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT MR.REGION_CODE,D.CLNCPATH_CODE, COUNT(D.CASE_NO) AS MAINCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE ");
        sqlbf.append(" FROM CLP_MANAGED D,MRO_RECORD MR ");
        sqlbf.append(" WHERE   D.ORDER_CODE IS NOT NULL  AND  ( D.PROGRESS_CODE NOT LIKE '%A%' OR D.PROGRESS_CODE IS NULL) AND D.ORDER_FLG='N' ");
        sqlbf.append(" AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append(" AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS')  ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append(" GROUP BY  MR.REGION_CODE,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE,D.CLNCPATH_CODE ");
        sqlbf.append(" )A, ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT MR.REGION_CODE,D.CLNCPATH_CODE, COUNT(D.CASE_NO) AS STANDCOUNT,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE ");
        sqlbf.append(" FROM CLP_MANAGED D,MRO_RECORD MR ");
        sqlbf.append(" WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='N' AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append(" AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append(" GROUP BY MR.REGION_CODE,D.ORDER_CODE,D.SCHD_CODE,D.CHKTYPE_CODE ,D.CLNCPATH_CODE ");
        sqlbf.append(" )B, ");
        sqlbf.append(" CLP_CHKITEM ITEM,CLP_CHKTYPE CH,CLP_DURATION DR,CLP_BSCINFO BSCINF ");
        sqlbf.append(" WHERE A.ORDER_CODE=B.ORDER_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE ");
        sqlbf.append(" AND ITEM.CHKITEM_CODE=B.ORDER_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE ");
        sqlbf.append(" AND A.CLNCPATH_CODE=B.CLNCPATH_CODE ");
        sqlbf.append(" AND A.CLNCPATH_CODE=BSCINF.CLNCPATH_CODE(+) ");
        sqlbf.append(" ) ");
        sqlbf.append(" )ALLTABLE	 ");
        sqlbf.append("  ");
        //System.out.println("执行sql:"+sqlbf.toString());
        Map resultMap=TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(resultMap);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 标准没有实际有
     * @param parm TParm
     */
    public TParm selectDataWithoutStandard(TParm parm) {
        String regionCode=parm.getValue("REGION_CODE");
        String start_date = parm.getValue("DATE_S");
        String end_date = parm.getValue("DATE_E");
        String clncPathCode = parm.getValue("CLNCPATH_CODE");
        String checkTypeCode = parm.getValue("CHKTYPE_CODE");
        //查询条件begin
        String conditionStr = "  ";
        if (this.checkNullAndEmpty(clncPathCode)) {
            conditionStr += " AND D.CLNCPATH_CODE='" + clncPathCode + "' ";
        }
        if (this.checkNullAndEmpty(checkTypeCode)) {
            conditionStr += " AND D.CHKTYPE_CODE='" + checkTypeCode + "' ";
        }
        //查询条件end
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" SELECT ALLTABLE.* FROM ");
        sqlbf.append(" ( ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT A.REGION_CODE,BSCINF.CLNCPATH_CHN_DESC, MAINCOUNT AS MAIN_COUNT,ORDER_DESC,DURATION_CHN_DESC AS SCHD_DESC,CHKTYPE_CHN_DESC,STANDCOUNT AS STANDARD_COUNT,(STANDCOUNT-MAINCOUNT)/STANDCOUNT AS PER  ");
        sqlbf.append(" FROM  ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT MR.REGION_CODE,D.CLNCPATH_CODE,COUNT(D.CASE_NO) AS MAINCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE  ");
        sqlbf.append(" FROM CLP_MANAGED D,MRO_RECORD MR ");
        sqlbf.append(" WHERE   D.MAINORD_CODE IS NOT NULL AND D.TOT=0 AND D.ORDER_FLG='Y' ");
        sqlbf.append(" AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append(" AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS')  ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append(" GROUP BY  MR.REGION_CODE,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE,D.CLNCPATH_CODE ");
        sqlbf.append(" )A, ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT MR.REGION_CODE,D.CLNCPATH_CODE,COUNT(D.CASE_NO) AS STANDCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE ");
        sqlbf.append(" FROM CLP_MANAGED D,MRO_RECORD MR ");
        sqlbf.append(" WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='Y' AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append("  AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append("  GROUP BY MR.REGION_CODE,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE,D.CLNCPATH_CODE ");
        sqlbf.append(" )B, ");
        sqlbf.append(" SYS_FEE FE,CLP_CHKTYPE CH,CLP_DURATION DR,CLP_BSCINFO BSCINF  ");
        sqlbf.append(" WHERE A.MAINORD_CODE=B.MAINORD_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE ");
        sqlbf.append(" AND FE.ORDER_CODE=B.MAINORD_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE ");
        sqlbf.append(" AND A.CLNCPATH_CODE=BSCINF.CLNCPATH_CODE(+) ");
        sqlbf.append(" ) ");
        sqlbf.append(" UNION ALL ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT A.REGION_CODE,BSCINF.CLNCPATH_CHN_DESC, MAINCOUNT,ITEM.CHKITEM_CHN_DESC AS ORDER_DESC,DURATION_CHN_DESC,CHKTYPE_CHN_DESC,STANDCOUNT,MAINCOUNT/STANDCOUNT AS PERCENT ");
        sqlbf.append(" FROM ");
        sqlbf.append(" ( ");
        sqlbf.append(" SELECT MR.REGION_CODE,D.CLNCPATH_CODE,COUNT(D.CASE_NO) AS MAINCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE ");
        sqlbf.append(" FROM CLP_MANAGED D,MRO_RECORD MR  ");
        sqlbf.append(" WHERE  D.PROGRESS_CODE  LIKE '%A%' AND D.TOT=0 AND D.ORDER_FLG='N' ");
        sqlbf.append(" AND D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL  AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append(" AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append(" GROUP BY  MR.REGION_CODE,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE,D.CLNCPATH_CODE ");
        sqlbf.append(" )A,  ");
        sqlbf.append(" (  ");
        sqlbf.append("  SELECT MR.REGION_CODE,D.CLNCPATH_CODE,COUNT(D.CASE_NO) AS STANDCOUNT,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE ");
        sqlbf.append("  FROM CLP_MANAGED D,MRO_RECORD MR ");
        sqlbf.append(" WHERE D.CASE_NO=MR.CASE_NO AND MR.OUT_DATE IS NOT NULL AND D.ORDER_FLG='N'  AND MR.REGION_CODE='"+regionCode+"' ");
        sqlbf.append("   AND MR.OUT_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') ");
        //路径和查核类别查询条件
        sqlbf.append(conditionStr);
        sqlbf.append("  GROUP BY MR.REGION_CODE,D.MAINORD_CODE,D.SCHD_CODE,D.CHKTYPE_CODE,D.CLNCPATH_CODE  ");
        sqlbf.append("  )B, ");
        sqlbf.append("  CLP_CHKITEM ITEM,CLP_CHKTYPE CH,CLP_DURATION DR,CLP_BSCINFO BSCINF  ");
        sqlbf.append(" WHERE A.MAINORD_CODE=B.MAINORD_CODE AND A.SCHD_CODE=B.SCHD_CODE AND A.CHKTYPE_CODE=B.CHKTYPE_CODE   ");
        sqlbf.append(" AND ITEM.CHKITEM_CODE=B.MAINORD_CODE AND CH.CHKTYPE_CODE=B.CHKTYPE_CODE AND DR.DURATION_CODE=B.SCHD_CODE  ");
        sqlbf.append(" AND A.CLNCPATH_CODE=BSCINF.CLNCPATH_CODE(+) ");
        sqlbf.append("  )  ");
        sqlbf.append(" )ALLTABLE  ");
        //System.out.println("执行FADFASFASFADSsql:"+sqlbf.toString());
        Map resultMap=TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(resultMap);
        if (result.getErrCode() < 0) {
            err(result.getErrCode() + " " + result.getErrText());
            return result;
        }
        return result;
    }

    /**
     * 检查是否为空或空串
     * @return boolean
     */
    private boolean checkNullAndEmpty(String checkstr) {
        if (checkstr == null) {
            return false;
        }
        if ("".equals(checkstr)) {
            return false;
        }
        return true;
    }


}
