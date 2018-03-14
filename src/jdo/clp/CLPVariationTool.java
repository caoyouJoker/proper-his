package jdo.clp;

import com.dongyang.jdo.TJDOTool;
import com.dongyang.data.TParm;
import java.util.Map;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 临床路径变异分析</p>
 *
 * <p>Description: 临床路径变异分析</p>
 *
 * <p>Copyright: Copyright (c) 2009</p>
 *
 * <p>Company: </p>
 *
 * @author not attributable
 * @version 1.0
 */
public class CLPVariationTool extends TJDOTool {
    private static CLPVariationTool instanceObject;
    public CLPVariationTool() {
        setModuleName("clp\\CLPVariationModule.x");
        onInit();
    }

    /**
     * 得到实例
     * @return CLPDurationTool
     */
    public static CLPVariationTool getInstance() {
        if (instanceObject == null)
            instanceObject = new CLPVariationTool();
        return instanceObject;
    }

    /**
     * 查询病人信息
     * @param parm TParm
     * @return TParm
     */
    public TParm selectPatientInfo(TParm parm) {
        //system.out.println("病人查询方法");
        TParm result = this.query("selectPatientInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }

    /**
     * 查询病人信息
     * @param parm TParm
     * @return TParm 返回的数据为列1：父时程名称 2:子时程名称 其他信息见sql
     */
    public TParm selectDuringInfo(TParm parm) {
        //system.out.println("病人时程信息查询方法");
        TParm result = this.query("selectPatientDuringInfo", parm);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    /**
     * 保存变异信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm savaVariance(TParm parm ,TConnection conn){
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(" UPDATE CLP_MANAGED SET ");
//        appendUpdateColumnStr(parm,"MEDICAL_MONCAT","MEDICAL_MONCAT",sqlbf,true,String.class);
        sqlbf.append(" MEDICAL_MONCAT ='" + parm.getValue("MEDICAL_MONCAT")+"', ");
        sqlbf.append(" MEDICAL_VARIANCE ='" + parm.getValue("MEDICAL_VARIANCE")+"', ");
        sqlbf.append(" PROGRESS_CODE ='" + parm.getValue("PROGRESS_CODE")+"', ");
        sqlbf.append(" MAINCFM_USER ='" + parm.getValue("MAINCFM_USER")+"', ");
        sqlbf.append(" MEDICAL_NOTE ='" + parm.getValue("MEDICAL_NOTE")+"', ");
        sqlbf.append(" MANAGE_MONCAT ='" + parm.getValue("MANAGE_MONCAT")+"', ");
        sqlbf.append(" MANAGE_VARIANCE ='" + parm.getValue("MANAGE_VARIANCE")+"', ");
        sqlbf.append(" MANAGE_NOTE ='" + parm.getValue("MANAGE_NOTE")+"', ");
        sqlbf.append(" R_DEPT_CODE ='" + parm.getValue("R_DEPT_CODE")+"', ");
        sqlbf.append(" R_USER ='" + parm.getValue("R_USER")+"' ");
        String case_no = parm.getValue("CASE_NO");
        String clncPathCode=parm.getValue("CLNCPATH_CODE");
        String schdCode=parm.getValue("SCHD_CODE");
        String orderNo=parm.getValue("ORDER_NO");
        String orderSeq=parm.getValue("ORDER_SEQ");
        sqlbf.append(" WHERE CASE_NO='"+case_no+"'");
        sqlbf.append(" AND CLNCPATH_CODE='"+clncPathCode+"'");
        sqlbf.append(" AND SCHD_CODE='"+schdCode+"'");
        sqlbf.append(" AND ORDER_NO='"+orderNo+"'");
        sqlbf.append(" AND ORDER_SEQ='"+orderSeq+"'");
        //system.out.println("更新sql："+sqlbf.toString());
        Map mapresult = TJDODBTool.getInstance().update(sqlbf.toString(),conn);
        TParm result = new TParm(mapresult);
        return result;
    }
    /**
     * add update column information string
     * @param parm TParm
     * @param columnName String
     * @param realColumnName String
     */
    private void appendUpdateColumnStr(TParm parm,String columnName,String realColumnName,StringBuffer sqlbf,boolean ishasComa,Class type){
        if("".equals(parm.getValue(columnName))||parm.getValue(columnName)==null){
            return;
        }
        if(String.class==type){
            sqlbf.append(realColumnName+"='"+parm.getValue(columnName)+"'");
        }else{
            sqlbf.append(realColumnName+"="+parm.getValue(columnName)+"");
        }
    }
    /**
     * 新增变异信息
     * @param parm TParm
     * @param conn TConnection
     * @return TParm
     */
    public TParm addVariance(TParm parm, TConnection conn) {
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append("INSERT INTO CLP_MANAGED (");
        sqlbf.append("CASE_NO,CLNCPATH_CODE,SCHD_CODE,ORDER_NO,ORDER_SEQ,REGION_CODE,CHKTYPE_CODE,CHKUSER_CODE,MAINORD_CODE,MAINTOT,MAINDISPENSE_UNIT,OPT_USER,OPT_DATE,OPT_TERM,STANDING_DTTM,TOT,STANDARD,ORDER_FLG,CHANGE_FLG,STANDARD_FLG,MAIN_AMT ");
        sqlbf.append(")VALUES(");
        sqlbf.append("'"+parm.getValue("CASE_NO")+"',");
        sqlbf.append("'"+parm.getValue("CLNCPATH_CODE")+"',");
        sqlbf.append("'"+parm.getValue("SCHD_CODE")+"',");
        sqlbf.append("'"+parm.getValue("ORDER_NO")+"',");
        sqlbf.append("(SELECT CASE  (COUNT(MAX(ORDER_SEQ)+1)) WHEN 0 THEN '1' ELSE TO_CHAR((MAX(ORDER_SEQ)+1)) END FROM CLP_MANAGED GROUP BY ORDER_SEQ), ");
        sqlbf.append("'"+parm.getValue("REGION_CODE")+"',");
        sqlbf.append("'"+parm.getValue("CHKTYPE_CODE")+"',");
        sqlbf.append("'"+parm.getValue("CHKUSER_CODE")+"',");
        sqlbf.append("'"+parm.getValue("MAINORD_CODE")+"',");
        sqlbf.append(" "+("".equals(parm.getValue("MAINTOT"))?"0":parm.getValue("MAINTOT"))+",");
        sqlbf.append(" '"+parm.getValue("MAINDISPENSE_UNIT")+"',");
        sqlbf.append("'"+parm.getValue("OPT_USER")+"',");
        sqlbf.append("TO_DATE('"+parm.getValue("OPT_DATE")+"','YYYYMMDD'),");
        sqlbf.append("'"+parm.getValue("OPT_TERM")+"',");
        sqlbf.append("TO_DATE('"+parm.getValue("OPT_DATE")+"','YYYYMMDD'),");
        sqlbf.append(" 0 ,");
        sqlbf.append(" 0 ,");
        sqlbf.append("'"+parm.getValue("ORDER_FLG")+"',");
        sqlbf.append("'N',");
        sqlbf.append("'N',");
        sqlbf.append(parm.getValue("MAIN_AMT"));
        sqlbf.append(")");
        //system.out.println("执行sql:"+sqlbf.toString());
        Map mapresult = TJDODBTool.getInstance().update(sqlbf.toString(), conn);
        TParm result = new TParm(mapresult);
        return result;
    }

    /**
     * 变异信息查询方法
     * @param parm TParm
     * @return String
     */
    public TParm selectVariation(TParm parm) {
        //处理时程
        String schdCodeStr= this.getSchdCodeStrWithParentCode(parm);
        parm.setData("SCHD_CODE_STR",schdCodeStr);
        //根据查询类型加入查询条件
        String type=parm.getValue("type");
        String sqlType="";
        if("2".equals(type)){
            sqlType=" AND TABLETMP.ORDER_CODE IS NOT NULL AND TABLETMP.MAINORD_CODE IS NULL AND (TABLETMP.S_GROUP IS NOT NULL AND TABLETMP.S_GROUP <> '') ";
        }
        if("3".equals(type)){
            sqlType=" AND  TABLETMP.ORDER_CODE IS NULL AND TABLETMP.MAINORD_CODE IS NOT NULL AND (TABLETMP.A_GROUP IS NOT NULL AND TABLETMP.A_GROUP <> '')";
        }
//        //除了2，3 以外的数据
//        if("4".equals(type)){
//            sqlType=" AND  TABLETMP.ORDER_CODE IS NOT NULL AND TABLETMP.MAINORD_CODE IS NOT NULL AND (TABLETMP.A_GROUP IS NOT NULL AND TABLETMP.A_GROUP <> '')";
//        }
        String orderFlag = parm.getValue("ORDER_FLG")==null?"":parm.getValue("ORDER_FLG");
        String sqltmp="";
        if("Y".equals(orderFlag.toUpperCase().trim())){
            sqltmp=getTempSql(parm,"Y");
        }else if("N".equals(orderFlag.toUpperCase().trim())){
            sqltmp=getTempSql(parm,"N");
        }else if("O".equals(orderFlag.toUpperCase().trim())){
            sqltmp=getTempSql(parm,"O");
        }else{
            sqltmp=getTempSql(parm,"");
        }
        //sqltmp需要过滤下stationCode
        sqltmp="SELECT B.* FROM  ADM_INP A,("+sqltmp+") B WHERE B.CASE_NO=A.CASE_NO " ;
        String stationCode=parm.getValue("CLP_STATION_CODE");
        if(this.checkNullAndEmpty(stationCode)){
            sqltmp+=" AND A.STATION_CODE = '"+stationCode+"' ";
        }
        StringBuffer totalSqlbf = new StringBuffer();
    	String  combination = parm.getData("combination").toString();
    	StringBuffer where=new StringBuffer();
        if(combination.equals("Y")){
        	if(this.checkNullAndEmpty(parm.getValue("IPD_CHARGE_CODE"))){
        		where.append(" AND IPD_CHARGE_CODE='"+parm.getValue("IPD_CHARGE_CODE")+"' ");
            }
            if (this.checkNullAndEmpty(parm.getValue("ORDTYPE_CODE"))) {
            	where.append(" AND A.ORDTYPE_CODE= '"+parm.getValue("ORDTYPE_CODE")+"' ");
            }
            if (this.checkNullAndEmpty(parm.getValue("DEPT_CODE"))) {
            	where.append(" AND A.DEPT_CODE ='"+parm.getValue("DEPT_CODE")+"' ");
            }
            if (this.checkNullAndEmpty(parm.getValue("EXE_DEPT_CODE"))) {
            	where.append(" AND A.EXE_DEPT_CODE='"+parm.getValue("EXE_DEPT_CODE")+"' ");
            }
            if (this.checkNullAndEmpty(parm.getValue("EXE_DEPT_CODE"))) {
            	where.append(" AND A.EXE_DEPT_CODE='"+parm.getValue("EXE_DEPT_CODE")+"' ");
            }
            if (this.checkNullAndEmpty(parm.getValue("CHK_USER"))) {
            	where.append(" AND A.CHKUSER_CODE='"+parm.getValue("CHK_USER")+"' ");
            }
            totalSqlbf.append(" SELECT TABLETMP.* FROM ("+sqltmp.replace("#", where.toString())+") TABLETMP ");
            totalSqlbf.append(" LEFT JOIN SYS_FEE B ON    TABLETMP.ORDER_CODE = B.ORDER_CODE ");///// TABLETMP.REGION_CODE = B.REGION_CODE AND
            totalSqlbf.append(" LEFT JOIN SYS_CHARGE_HOSP C ON   C.CHARGE_HOSP_CODE = B.CHARGE_HOSP_CODE ");
            totalSqlbf.append(" WHERE 1=1 ");
        }else{
        	if(this.checkNullAndEmpty(parm.getValue("IPD_CHARGE_CODE"))){
        		where.append(" AND IPD_CHARGE_CODE='"+parm.getValue("IPD_CHARGE_CODE")+"'");
            }
            if (this.checkNullAndEmpty(parm.getValue("ORDTYPE_CODE"))) {
            	where.append(" AND TABLETMP.ORDTYPE_CODE= '"+parm.getValue("ORDTYPE_CODE")+"' ");
            }
            if (this.checkNullAndEmpty(parm.getValue("DEPT_CODE"))) {
            	where.append(" AND TABLETMP.DEPT_CODE ='"+parm.getValue("DEPT_CODE")+"'");
            }
            if (this.checkNullAndEmpty(parm.getValue("EXE_DEPT_CODE"))) {
            	where.append(" AND TABLETMP.EXE_DEPT_CODE='"+parm.getValue("EXE_DEPT_CODE")+"'");
            }
            if (this.checkNullAndEmpty(parm.getValue("EXE_DEPT_CODE"))) {
            	where.append(" AND TABLETMP.EXE_DEPT_CODE='"+parm.getValue("EXE_DEPT_CODE")+"'");
            }
            if (this.checkNullAndEmpty(parm.getValue("CHK_USER"))) {
            	where.append(" AND TABLETMP.CHKUSER_CODE='"+parm.getValue("CHK_USER")+"'");
            }
            totalSqlbf.append(" SELECT TABLETMP.* FROM ("+sqltmp+") TABLETMP ");
            totalSqlbf.append(" LEFT JOIN SYS_FEE B ON    TABLETMP.ORDER_CODE = B.ORDER_CODE ");///// TABLETMP.REGION_CODE = B.REGION_CODE AND
            totalSqlbf.append(" LEFT JOIN SYS_CHARGE_HOSP C ON   C.CHARGE_HOSP_CODE = B.CHARGE_HOSP_CODE ");
            totalSqlbf.append(" WHERE 1=1 ");
            totalSqlbf.append(sqlType).append(where);
        }
    	if(combination.equals("Y")){
    		totalSqlbf.append(" ORDER BY TABLETMP.CHKTYPE_CODE,TABLETMP.ORDER_DESC,TABLETMP.MAINORD_CODE,TABLETMP.ORDER_CODE");
    	}else{
    		 totalSqlbf.append(" ORDER BY TABLETMP.CHKTYPE_CODE,TABLETMP.CHKUSER_CODE,TABLETMP.ORDER_DESC,TABLETMP.MAINORD_CODE,TABLETMP.ORDER_CODE");
    	}
        //System.out.println("执行totalSqlbf::sss:::"+totalSqlbf);
        Map mapresult = TJDODBTool.getInstance().select(totalSqlbf.toString());
        TParm result = new TParm(mapresult);
        return result;
    }
    /**
     * 根据父时程代码得到时程代码
     * @param parentCode String
     * @return String
     */
    private String getSchdCodeStrWithParentCode(TParm parm) {
        String returnstr = "";
        String durationCode = parm.getValue("DURATION_CODE");
        //system.out.println("变异信息查询方法");
        StringBuffer sqlbf = new StringBuffer();
        sqlbf.append(
                "SELECT DURATION_CODE FROM CLP_DURATION WHERE PARENT_CODE ='" +
                durationCode + "'");
        Map mapresult = TJDODBTool.getInstance().select(sqlbf.toString());
        TParm result = new TParm(mapresult);
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
        } else {
            for (int i = 0; i < result.getCount(); i++) {
                returnstr += "'"+result.getRow(i).getValue("DURATION_CODE") +"'"+ ",";
            }
//            if (returnstr.lastIndexOf(",") != -1) {
//                returnstr = returnstr.substring(0, returnstr.lastIndexOf(","));
//            }
        }
        //查询出来的时程代码还要加入自身
        returnstr+="'"+durationCode+"'";
        //system.out.println("查询出来的时程代码:"+returnstr);
        return returnstr;
    }

    //得到医嘱套餐的sql Y
    private String getYSql(TParm parm) {
        String regionCode = parm.getValue("REGION_CODE");
        String case_no = parm.getValue("CASE_NO");
        String clncPathCode = parm.getValue("CLNCPATH_CODE");
        String schdCodeStr = parm.getValue("SCHD_CODE_STR");
        String orderFlag = parm.getValue("ORDER_FLG");
        String theExecFlg = parm.getValue("EXEC_FLG");
        String checkUser = parm.getValue("CHK_USER");
        StringBuffer sqlbf = new StringBuffer("");
        sqlbf.append(" SELECT D.OPT_TERM,'' AS FLG,D.OPT_DATE,D.OPT_USER,D.CASE_NO,D.CLNCPATH_CODE,'' AS STATUS, D.EXEC_FLG, D.CHKUSER_CODE, D.CHKTYPE_CODE, ");//加入CASE_NO
        sqlbf.append("DECODE(D.ORDER_CODE,'','',");
        sqlbf.append("(SELECT S.ORDER_DESC||CASE WHEN S.DESCRIPTION IS NOT NULL AND S.DESCRIPTION<>'' THEN '('||S.DESCRIPTION||')' ELSE '' END || CASE WHEN B.TYPE_CHN_DESC IS NOT NULL AND B.TYPE_CHN_DESC<>'' THEN '('||B.TYPE_CHN_DESC||')' ELSE '' END");
        sqlbf.append(" FROM SYS_FEE S  ");
        sqlbf.append(" LEFT JOIN  CLP_ORDERTYPE T ON T.ORDER_CODE =S.ORDER_CODE AND T.ORDER_FLG='Y' ");
        sqlbf.append(" LEFT JOIN  CLP_ORDTYPE B ON S.REGION_CODE = B.REGION_CODE AND  B.TYPE_CODE=T.ORDTYPE_CODE ");
        sqlbf.append(" WHERE (S.REGION_CODE = D.REGION_CODE OR S.REGION_CODE IS NULL ) AND ");
        sqlbf.append("  S.ORDER_CODE = D.ORDER_CODE ");
        sqlbf.append(" )) AS ORDER_DESC,  ");
        sqlbf.append(" D.TOT, D.DISPENSE_UNIT, D.STANDARD,'' AS SPACE ,  ");
        sqlbf.append(" DECODE(D.MAINORD_CODE,'','', ");
        sqlbf.append(" (SELECT S.ORDER_DESC||CASE WHEN S.DESCRIPTION IS NOT NULL AND S.DESCRIPTION<>'' THEN '('||S.DESCRIPTION||')' ELSE '' END || CASE WHEN B.TYPE_CHN_DESC IS NOT NULL AND B.TYPE_CHN_DESC<>'' THEN '('||B.TYPE_CHN_DESC||')' ELSE '' END  ");
        sqlbf.append(" FROM SYS_FEE S ");
        sqlbf.append(
                " LEFT JOIN  CLP_ORDERTYPE T ON T.ORDER_CODE =S.ORDER_CODE AND T.ORDER_FLG='Y' ");
        sqlbf.append(" LEFT JOIN  CLP_ORDTYPE B ON   B.TYPE_CODE=T.ORDTYPE_CODE ");/////S.REGION_CODE = B.REGION_CODE AND
        sqlbf.append(" WHERE  (S.REGION_CODE = D.REGION_CODE OR S.REGION_CODE IS NULL ) AND ");
        sqlbf.append("  S.ORDER_CODE = D.MAINORD_CODE ");
        sqlbf.append(" ))  AS MAIN_ORDER_CODE_DESC, ");//MAIN_ORDER_CODE_DESC
        sqlbf.append(" D.MAINTOT, D.MAINDISPENSE_UNIT,CASE WHEN D.MEDICAL_MONCAT IS NULL OR D.MEDICAL_MONCAT ='' THEN '' ELSE 'Y' END  AS VARIANCEFLG, ");//变异
       // sqlbf.append(" '',  '', ");//D.MEDICAL_MONCAT, D.MEDICAL_VARIANCE,D.PROGRESS_CODE,
        sqlbf.append("  DECODE(D.MAINCFM_USER,'NULL','','NULL','',D.MAINCFM_USER) AS MAINCFM_USER,");// D.MEDICAL_NOTE,
        sqlbf.append("  ");//D.MANAGE_MONCAT, D.MANAGE_VARIANCE, D.MANAGE_NOTE,
        sqlbf.append(" 'N', D.ORDER_CODE, ");//D.R_DEPT_CODE, D.R_USER
        sqlbf.append(" D.MAINORD_CODE, D.STANDARD_FLG, D.CHANGE_FLG, ");
        sqlbf.append(" D.ORDER_FLG, D.SCHD_CODE, TO_CHAR(D.START_DAY)AS START_DAY, ");
        sqlbf.append(
                " TO_CHAR(D.STANDING_DTTM,'YYYYMMDD'), D.MEDICAL_MONCAT, D.MEDICAL_VARIANCE,  ");
        sqlbf.append(" D.MEDICAL_NOTE, D.MANAGE_MONCAT, D.MANAGE_VARIANCE,  ");
        sqlbf.append(" D.MANAGE_NOTE, D.R_DEPT_CODE, D.R_USER, ");
        sqlbf.append(
                " (D.TOT*((100-D.STANDARD)/100)), (D.TOT*((100+D.STANDARD)/100)), ");
        sqlbf.append(
                " D.PROGRESS_CODE, D.ORDER_NO, D.ORDER_SEQ, D.TOT_AMT, D.MAIN_AMT , '' AS ISEXE,'' AS RDEPTFLG ");//执行, 责任科室标示
        sqlbf.append(" ,DECODE(D.ORDER_CODE,'','',  ");
        sqlbf.append(" (SELECT  S.CLPGROUP_CODE  ");
        sqlbf.append(" FROM SYS_FEE S  ");
        sqlbf.append(" WHERE ");///////S.REGION_CODE = D.REGION_CODE
        sqlbf.append("  S.ORDER_CODE = D.ORDER_CODE  ");
        sqlbf.append(" )) AS S_GROUP,  ");
        sqlbf.append(" DECODE(D.MAINORD_CODE,'','', ");
        sqlbf.append(" (SELECT S.CLPGROUP_CODE  ");
        sqlbf.append(" FROM SYS_FEE S  ");
        sqlbf.append(" WHERE ");////S.REGION_CODE = D.REGION_CODE
        sqlbf.append("  S.ORDER_CODE = D.MAINORD_CODE  ");
        sqlbf.append(
                " )) AS A_GROUP,D.DEPT_CODE,D.EXE_DEPT_CODE,D.ORDTYPE_CODE,D.REGION_CODE ");
        sqlbf.append(" FROM CLP_MANAGED D ");
        sqlbf.append(" WHERE D.REGION_CODE = '" + regionCode + "' ");
        sqlbf.append(" AND D.CASE_NO = '" + case_no + "'  ");
        sqlbf.append(" AND D.CLNCPATH_CODE = '" + clncPathCode + "'");
        sqlbf.append(" AND D.SCHD_CODE IN (" + schdCodeStr + ")");
        sqlbf.append(" AND D.ORDER_FLG = 'Y'");
        sqlbf.append(this.getConditionSql(parm));
        return sqlbf.toString();
    }
    /**
     * 得到医嘱套餐的sql 共用 
     * @param parm
     * @return
     * 现在CLP_ORDERTYPE 表中 ORDER_FLG都为Y
     * =================pangben 2012-7-6 
     */
    private String getTempSql(TParm parm,String order_flg){
    	String regionCode = parm.getValue("REGION_CODE");
        String case_no = parm.getValue("CASE_NO");
        String clncPathCode = parm.getValue("CLNCPATH_CODE");
        String schdCodeStr = parm.getValue("SCHD_CODE_STR");
        String execFlg=parm.getValue("EXEC_FLG");
        String rDeptCode=parm.getValue("R_DEPT_CODE");
        String progressCode=parm.getValue("PROGRESS_CODE");
    	StringBuffer sql=new StringBuffer();
    	
    	//====modify by caowl 20120817 start 合并同类项
    	String  combination = parm.getData("combination").toString();
    	if(combination.equals("Y")){
    		//STATUS;EXEC_FLG;CHKTYPE_CODE;ORDER_FLG;ORDER_DESC;TOT;DISPENSE_UNIT;STANDARD;FLG;MAIN_ORDER_CODE_DESC;MAINTOT;MAINDISPENSE_UNIT
    		sql.append("SELECT '' AS STATUS,'' AS FLG,F.TYPE_CHN_DESC AS ORDER_DESC ,A.EXEC_FLG,A.CHKTYPE_CODE,SUM(A.TOT) TOT,A.DISPENSE_UNIT, SUM(A.MAINTOT) MAINTOT, A.MAINDISPENSE_UNIT,");
        	sql.append("C.ORDER_DESC || CASE WHEN C.SPECIFICATION IS NOT NULL THEN '(' || C.SPECIFICATION || ')' ELSE '' END");
        	sql.append(" || CASE WHEN G.TYPE_CHN_DESC IS NOT NULL  THEN '(' || G.TYPE_CHN_DESC || ')' ELSE '' END AS MAIN_ORDER_CODE_DESC,'' AS SPACE,A.CASE_NO,C.ORDER_CODE,A.MAINORD_CODE ");
        	sql.append(" FROM CLP_MANAGED A ,SYS_FEE B,SYS_FEE C,CLP_ORDERTYPE D,CLP_ORDERTYPE E,CLP_ORDTYPE F,CLP_ORDTYPE G,SYS_CHARGE_HOSP H WHERE "); 
        	sql.append("A.ORDER_CODE =B.ORDER_CODE AND  B.ORDER_CODE = D.ORDER_CODE AND B.charge_hosp_code =H.charge_hosp_code(+) AND D.ORDER_FLG = 'Y' AND D.ORDTYPE_CODE = F.TYPE_CODE AND "); 
        	sql.append("A.MAINORD_CODE =C.ORDER_CODE(+) AND  A.MAINORD_CODE = E.ORDER_CODE(+) AND E.ORDTYPE_CODE = G.TYPE_CODE(+) #"); 
    	}else{
    		sql.append("SELECT A.OPT_TERM, '' AS FLG, A.OPT_DATE, A.OPT_USER, A.CASE_NO,");
        	sql.append("A.CLNCPATH_CODE, '' AS STATUS, A.EXEC_FLG, A.CHKUSER_CODE,A.CHKTYPE_CODE,");
    	   	sql.append("B.ORDER_DESC || CASE WHEN B.SPECIFICATION IS NOT NULL  THEN '(' || B.SPECIFICATION || ')' ELSE '' END");
    	   	sql.append(" || CASE WHEN F.TYPE_CHN_DESC IS NOT NULL  THEN '(' || F.TYPE_CHN_DESC || ')' ELSE '' END AS ORDER_DESC ,");
    		//====modify by caowl 20120817 end    	    	
        	sql.append("C.ORDER_DESC || CASE WHEN C.SPECIFICATION IS NOT NULL  THEN '(' || C.SPECIFICATION || ')' ELSE '' END");
        	sql.append(" || CASE WHEN G.TYPE_CHN_DESC IS NOT NULL THEN '(' || G.TYPE_CHN_DESC || ')' ELSE '' END AS MAIN_ORDER_CODE_DESC,");
        	sql.append("A.TOT,A.DISPENSE_UNIT, A.MAINTOT, A.MAINDISPENSE_UNIT,");
        	sql.append("CASE WHEN A.MEDICAL_MONCAT IS NULL OR A.MEDICAL_MONCAT = '' THEN '' ELSE 'Y' END AS VARIANCEFLG,");
        	sql.append("A.STANDARD, '' AS SPACE, A.MAINCFM_USER,'N', A.ORDER_CODE, A.MAINORD_CODE, A.STANDARD_FLG, A.CHANGE_FLG,");  
        	sql.append("A.ORDER_FLG, A.SCHD_CODE, TO_CHAR (A.START_DAY) AS START_DAY,");  
        	sql.append("TO_CHAR (A.STANDING_DTTM, 'YYYYMMDD'), A.MEDICAL_MONCAT,");  
        	sql.append("A.MEDICAL_VARIANCE, A.MEDICAL_NOTE, A.MANAGE_MONCAT, A.MANAGE_VARIANCE,");  
        	sql.append("A.MANAGE_NOTE, A.R_DEPT_CODE, A.R_USER, A.PROGRESS_CODE, A.ORDER_NO,");  
//        	sql.append("(A.TOT * ((100 - A.STANDARD) / 100)),");  
//        	sql.append("(A.TOT * ((100 + A.STANDARD) / 100))");  
        	sql.append("A.ORDER_SEQ, A.TOT_AMT, A.MAIN_AMT, '' AS ISEXE, '' AS RDEPTFLG,");  
        	sql.append("A.DEPT_CODE, A.EXE_DEPT_CODE, A.ORDTYPE_CODE, A.REGION_CODE,A.MAINORDTYPE_CODE ");  
        	sql.append("FROM CLP_MANAGED A ,SYS_FEE B,SYS_FEE C,CLP_ORDERTYPE D,CLP_ORDERTYPE E,CLP_ORDTYPE F,CLP_ORDTYPE G WHERE "); 
        	sql.append("A.ORDER_CODE =B.ORDER_CODE AND  B.ORDER_CODE = D.ORDER_CODE AND D.ORDER_FLG = 'Y' AND D.ORDTYPE_CODE = F.TYPE_CODE AND "); 
        	sql.append("A.MAINORD_CODE =C.ORDER_CODE(+) AND  A.MAINORD_CODE = E.ORDER_CODE(+) AND E.ORDTYPE_CODE = G.TYPE_CODE(+) "); 

    	}   
    	 if (this.checkNullAndEmpty(rDeptCode)) {
    		 sql.append(" AND A.R_DEPT_CODE='" + rDeptCode +
                          "' ");
         }
         //进度状态
         if(this.checkNullAndEmpty(progressCode)){
        	 sql.append(" AND A.PROGRESS_CODE='" + progressCode +
                          "' ");
         }
        sql.append(" AND A.REGION_CODE = '" + regionCode + "' ");
    	sql.append(" AND A.CASE_NO = '" + case_no + "'  ");
    	sql.append(" AND A.CLNCPATH_CODE = '" + clncPathCode + "'");
    	sql.append(" AND A.SCHD_CODE IN (" + schdCodeStr + ")");
        if (order_flg.equals("Y")) {
        	sql.append(" AND A.ORDER_FLG ='Y' ");
		}else if(order_flg.equals("N")){
			sql.append(" AND A.ORDER_FLG ='N' ");
		}else if(order_flg.equals("O")){
			sql.append(" AND A.ORDER_FLG = 'O' ");
		}else {
			sql.append(" AND A.ORDER_FLG IN ('O','Y','N') ");
		}  
        if (null!=execFlg&&execFlg.equals("Y")) {
        	sql.append(" AND A.EXEC_FLG ='Y' ");
		}
        if(combination.equals("Y")){
        	sql.append(" GROUP BY  F.TYPE_CHN_DESC,C.ORDER_DESC,G.TYPE_CHN_DESC,A.DISPENSE_UNIT,A.MAINDISPENSE_UNIT,C.SPECIFICATION,A.CHKTYPE_CODE,A.CASE_NO,C.ORDER_CODE,A.MAINORD_CODE, A.EXEC_FLG ");
        }
        return sql.toString();    
    }
    //得到关键诊疗项目类别时的sql N
    private String getNSql(TParm parm){
        String regionCode = parm.getValue("REGION_CODE");
        String case_no = parm.getValue("CASE_NO");
        String clncPathCode = parm.getValue("CLNCPATH_CODE");
        String schdCodeStr = parm.getValue("SCHD_CODE_STR");
        String orderFlag = parm.getValue("ORDER_FLG");
        String theExecFlg = parm.getValue("EXEC_FLG");
        String checkUser = parm.getValue("CHK_USER");
        StringBuffer sqlbf = new StringBuffer("");
        sqlbf.append(" SELECT D.OPT_TERM,'' AS FLG,D.OPT_DATE,D.OPT_USER,D.CASE_NO,D.CLNCPATH_CODE,'' AS STATUS, D.EXEC_FLG, D.CHKUSER_CODE, D.CHKTYPE_CODE, ");//加入CASE_NO
        sqlbf.append(" DECODE(D.ORDER_CODE,'','',  ");
        sqlbf.append("(SELECT S.CHKITEM_CHN_DESC || CASE WHEN B.TYPE_CHN_DESC IS NOT NULL AND B.TYPE_CHN_DESC<>'' THEN '('||B.TYPE_CHN_DESC||')' ELSE '' END ");
        sqlbf.append(" FROM CLP_CHKITEM S ");
        sqlbf.append(" LEFT JOIN  CLP_ORDERTYPE T ON T.ORDER_CODE =S.CHKITEM_CODE AND T.ORDER_FLG='N' ");
        sqlbf.append(" LEFT JOIN  CLP_ORDTYPE B ON S.REGION_CODE = B.REGION_CODE AND  B.TYPE_CODE=T.ORDTYPE_CODE ");
        sqlbf.append(" WHERE S.REGION_CODE = D.REGION_CODE ");
        //sqlbf.append(" AND S.CHKTYPE_CODE = D.CHKTYPE_CODE  ");//----------删除
        sqlbf.append(" AND S.CHKITEM_CODE = D.ORDER_CODE ))  AS ORDER_DESC, ");
        sqlbf.append(" D.TOT, D.DISPENSE_UNIT, D.STANDARD,'' AS SPACE, ");
        sqlbf.append(" DECODE(D.STANDARD_FLG,'Y', ");
        sqlbf.append(" (SELECT S.CHKITEM_CHN_DESC || CASE WHEN B.TYPE_CHN_DESC IS NOT NULL AND B.TYPE_CHN_DESC<>'' THEN '('||B.TYPE_CHN_DESC||')' ELSE '' END ");
        sqlbf.append(" FROM CLP_CHKITEM S  ");
        sqlbf.append(" LEFT JOIN  CLP_ORDERTYPE T ON T.ORDER_CODE =S.CHKITEM_CODE AND T.ORDER_FLG='N' ");
        sqlbf.append(" LEFT JOIN  CLP_ORDTYPE B ON S.REGION_CODE = B.REGION_CODE AND  B.TYPE_CODE=T.ORDTYPE_CODE ");
        sqlbf.append(" WHERE S.REGION_CODE = D.REGION_CODE ");
        //sqlbf.append(" AND S.CHKTYPE_CODE = D.CHKTYPE_CODE ");//---------------删除
        sqlbf.append(" AND S.CHKITEM_CODE = D.ORDER_CODE), ");
        sqlbf.append(" (SELECT S.CHKITEM_CHN_DESC || CASE WHEN B.TYPE_CHN_DESC IS NOT NULL AND B.TYPE_CHN_DESC<>'' THEN '('||B.TYPE_CHN_DESC||')' ELSE '' END ");
        sqlbf.append(" FROM CLP_CHKITEM S  ");
        sqlbf.append(" LEFT JOIN  CLP_ORDERTYPE T ON T.ORDER_CODE =S.CHKITEM_CODE AND T.ORDER_FLG='N' ");
        sqlbf.append(" LEFT JOIN  CLP_ORDTYPE B ON S.REGION_CODE = B.REGION_CODE AND  B.TYPE_CODE=T.ORDTYPE_CODE ");
        sqlbf.append(" WHERE S.REGION_CODE = D.REGION_CODE ");
        //sqlbf.append(" AND S.CHKTYPE_CODE = D.CHKTYPE_CODE ");//--------------------删除
        sqlbf.append(" AND S.CHKITEM_CODE = D.MAINORD_CODE))  AS MAIN_ORDER_CODE_DESC, ");// MAIN_ORDER_CODE_DESC
        sqlbf.append(" DECODE(D.STANDARD_FLG,'Y', D.TOT, D.MAINTOT) AS MAINTOT, DECODE(D.STANDARD_FLG,'Y',D.DISPENSE_UNIT,D.MAINDISPENSE_UNIT) AS MAINDISPENSE_UNIT, '' AS VARIANCEFLG, ");//变异
        //sqlbf.append(" '', '',  ");//D.MEDICAL_MONCAT, D.MEDICAL_VARIANCE,
        sqlbf.append("  DECODE(D.MAINCFM_USER,'NULL','','NULL','',D.MAINCFM_USER) AS  MAINCFM_USER, ");//, D.MEDICAL_NOTE D.PROGRESS_CODE,
        //sqlbf.append(" D.MANAGE_MONCAT, D.MANAGE_VARIANCE, D.MANAGE_NOTE,  ");
        sqlbf.append(" 'N',  D.ORDER_CODE, ");//D.R_DEPT_CODE, D.R_USER,
        sqlbf.append(" DECODE(D.STANDARD_FLG,'Y', D.ORDER_CODE, D.MAINORD_CODE) AS MAINORD_CODE, D.STANDARD_FLG, D.CHANGE_FLG,  ");
        sqlbf.append(" D.ORDER_FLG, D.SCHD_CODE, TO_CHAR(D.START_DAY)AS START_DAY,  ");
        sqlbf.append(" TO_CHAR(D.STANDING_DTTM,'YYYYMMDD'), D.MEDICAL_MONCAT, D.MEDICAL_VARIANCE, ");
        sqlbf.append(" D.MEDICAL_NOTE, D.MANAGE_MONCAT, D.MANAGE_VARIANCE, ");
        sqlbf.append(" D.MANAGE_NOTE, D.R_DEPT_CODE, D.R_USER,  ");
        sqlbf.append(" (D.TOT*((100-D.STANDARD)/100)), (D.TOT*((100+D.STANDARD)/100)), ");
        sqlbf.append(" D.PROGRESS_CODE, D.ORDER_NO, D.ORDER_SEQ, D.TOT_AMT, D.MAIN_AMT ,'' AS ISEXE ,'' AS RDEPTFLG ");//执行, 责任科室标示
        sqlbf.append(" , '' AS S_GROUP, '' A_GROUP,D.DEPT_CODE,D.EXE_DEPT_CODE,D.ORDTYPE_CODE,D.REGION_CODE ");
        sqlbf.append(" FROM CLP_MANAGED D ");
        sqlbf.append(" WHERE D.REGION_CODE = '" + regionCode + "' ");
        sqlbf.append(" AND D.CASE_NO = '" + case_no + "'  ");
        sqlbf.append(" AND D.CLNCPATH_CODE = '" + clncPathCode + "'");
        sqlbf.append(" AND D.SCHD_CODE IN (" + schdCodeStr + ")");
        sqlbf.append(" AND D.ORDER_FLG ='N'  ");
        sqlbf.append(this.getConditionSql(parm));
        return sqlbf.toString();
    }
    //得到护理计划项目类别时的sql O
 private String getOSql(TParm parm){
     String regionCode = parm.getValue("REGION_CODE");
     String case_no = parm.getValue("CASE_NO");
     String clncPathCode = parm.getValue("CLNCPATH_CODE");
     String schdCodeStr = parm.getValue("SCHD_CODE_STR");
     StringBuffer sqlbf = new StringBuffer("");
     sqlbf.append(" SELECT D.OPT_TERM,'' AS FLG,D.OPT_DATE,D.OPT_USER, D.CASE_NO,D.CLNCPATH_CODE,'' AS STATUS, D.EXEC_FLG, D.CHKUSER_CODE, D.CHKTYPE_CODE, ");//add case_no
     sqlbf.append(" DECODE(D.ORDER_CODE,'','', ");
     sqlbf.append(" (SELECT S.ORDER_CHN_DESC || CASE WHEN B.TYPE_CHN_DESC IS NOT NULL AND B.TYPE_CHN_DESC<>'' THEN '('||B.TYPE_CHN_DESC||')' ELSE '' END  ");
     sqlbf.append(" FROM CLP_NURSORDER S  ");
     sqlbf.append(" LEFT JOIN  CLP_ORDERTYPE T ON T.ORDER_CODE =S.ORDER_CODE AND T.ORDER_FLG='O' ");
     sqlbf.append(" LEFT JOIN  CLP_ORDTYPE B ON S.REGION_CODE = B.REGION_CODE AND  B.TYPE_CODE=T.ORDTYPE_CODE  ");
     sqlbf.append(" WHERE S.REGION_CODE = D.REGION_CODE  ");
     sqlbf.append(" AND S.ORDER_CODE = D.ORDER_CODE))  AS ORDER_DESC, ");
     sqlbf.append(" D.TOT, D.DISPENSE_UNIT, D.STANDARD,'' AS SPACE ,");//empty column space
     sqlbf.append(" DECODE(D.MAINORD_CODE,'','',  ");
     sqlbf.append(" (SELECT S.ORDER_CHN_DESC ||CASE WHEN B.B.TYPE_CHN_DESC IS NOT NULL AND B.B.TYPE_CHN_DESC<>'' THEN '('||B.TYPE_CHN_DESC||')' ELSE '' END ");
     sqlbf.append(" FROM CLP_NURSORDER S ");
     sqlbf.append(" LEFT JOIN  CLP_ORDERTYPE T ON T.ORDER_CODE =S.ORDER_CODE AND T.ORDER_FLG='O'");
     sqlbf.append(" LEFT JOIN  CLP_ORDTYPE B ON S.REGION_CODE = B.REGION_CODE AND  B.TYPE_CODE=T.ORDTYPE_CODE ");
     sqlbf.append(" WHERE S.REGION_CODE = D.REGION_CODE ");
     sqlbf.append(" AND S.ORDER_CODE = D.MAINORD_CODE)) AS MAIN_ORDER_CODE_DESC, ");//MAIN_ORDER_CODE_DESC
     sqlbf.append(" D.MAINTOT, D.MAINDISPENSE_UNIT,'' AS VARIANCEFLG, ");//变异
     //sqlbf.append(" '',  '',");//D.MEDICAL_MONCAT, D.MEDICAL_VARIANCE,
     sqlbf.append("  DECODE(D.MAINCFM_USER,'NULL','','NULL','',D.MAINCFM_USER) AS MAINCFM_USER, ");// D.MEDICAL_NOTE, D.PROGRESS_CODE,
     //sqlbf.append(" D.MANAGE_MONCAT, D.MANAGE_VARIANCE, D.MANAGE_NOTE, ");
     sqlbf.append(" 'N', D.ORDER_CODE, ");//D.R_DEPT_CODE, D.R_USER,
     sqlbf.append(" DECODE(D.MAINORD_CODE,'NULL','','NULL','', D.MAINORD_CODE) AS MAINORD_CODE, D.STANDARD_FLG, D.CHANGE_FLG, ");
     sqlbf.append(" D.ORDER_FLG, D.SCHD_CODE, TO_CHAR(D.START_DAY)AS START_DAY,  ");
     sqlbf.append(" TO_CHAR(D.STANDING_DTTM,'YYYYMMDD'), D.MEDICAL_MONCAT, D.MEDICAL_VARIANCE, ");
     sqlbf.append(" D.MEDICAL_NOTE, D.MANAGE_MONCAT, D.MANAGE_VARIANCE, ");
     sqlbf.append(" D.MANAGE_NOTE, D.R_DEPT_CODE, D.R_USER, ");
     sqlbf.append(" (D.TOT*((100-D.STANDARD)/100)), (D.TOT*((100+D.STANDARD)/100)),  ");
     sqlbf.append(" D.PROGRESS_CODE, D.ORDER_NO, D.ORDER_SEQ, D.TOT_AMT, D.MAIN_AMT, '' AS ISEXE ,'' AS RDEPTFLG");//执行, 责任科室标示
     sqlbf.append(" , '' AS S_GROUP, '' A_GROUP,D.DEPT_CODE,D.EXE_DEPT_CODE,D.ORDTYPE_CODE ,D.REGION_CODE  ");
     sqlbf.append(" FROM CLP_MANAGED D  ");
     sqlbf.append(" WHERE D.REGION_CODE = '" + regionCode + "' ");
     sqlbf.append(" AND D.CASE_NO = '" + case_no + "'  ");
     sqlbf.append(" AND D.CLNCPATH_CODE = '" + clncPathCode + "'");
     sqlbf.append(" AND D.SCHD_CODE IN (" + schdCodeStr + ")");
     sqlbf.append(" AND D.ORDER_FLG ='O'  ");
     sqlbf.append(this.getConditionSql(parm));
     return sqlbf.toString();
 }

 /**
  * 得到y,n,o 的查询条件sql
  * @param parm TParm
  * @return String
  */
 private String getConditionSql(TParm parm){
     StringBuffer sqlbf = new StringBuffer();
     String orderFlag = parm.getValue("ORDER_FLG");
     String theExecFlg = parm.getValue("EXEC_FLG");
     String checkUser = parm.getValue("CHK_USER");
     String deptCode=parm.getValue("DEPT_CODE");
     String execdeptCode=parm.getValue("EXE_DEPT_CODE");
     String rDeptCode=parm.getValue("R_DEPT_CODE");
     String progressCode=parm.getValue("PROGRESS_CODE");
     String moncat_code=parm.getValue("MONCAT_CODE");
     String variance_code=parm.getValue("VARIANCE_CODE");
     //execflag 只有选中时才加入条件，即仅加入值为Y的情况
     if (this.checkNullAndEmpty(theExecFlg) && "Y".equals(theExecFlg)) {
         sqlbf.append(" AND D.EXEC_FLG='Y'");
     }
     if (this.checkNullAndEmpty(checkUser)) {
         sqlbf.append(" AND D.CHKUSER_CODE='" + checkUser + "'");
     }
     if (this.checkNullAndEmpty(parm.getValue("ORDTYPE_CODE"))) {
         sqlbf.append(" AND D.ORDTYPE_CODE='" + parm.getValue("ORDTYPE_CODE") +
                      "'");
     }
     if(this.checkNullAndEmpty(deptCode)){
         sqlbf.append(" AND D.DEPT_CODE='" + deptCode +
                      "'");
     }
     if (this.checkNullAndEmpty(execdeptCode)) {
         sqlbf.append(" AND D.EXE_DEPT_CODE='" + execdeptCode +
                      "'");
     }
     if (this.checkNullAndEmpty(rDeptCode)) {
         sqlbf.append(" AND D.R_DEPT_CODE='" + rDeptCode +
                      "'");
     }
     //进度状态
     if(this.checkNullAndEmpty(progressCode)){
         sqlbf.append(" AND D.PROGRESS_CODE='" + progressCode +
                      "'");
     }
     //医/护变异类别
     if (this.checkNullAndEmpty(moncat_code)) {
         sqlbf.append(" AND D.MANAGE_MONCAT='" + moncat_code +
                      "'");
     }
     //医/护变异原因
     if (this.checkNullAndEmpty(variance_code)) {
         sqlbf.append(" AND D.MANAGE_VARIANCE='" + variance_code +
                      "'");
     }

     return sqlbf.toString();
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
