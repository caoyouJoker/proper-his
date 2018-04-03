package jdo.onw;

import org.apache.commons.lang.StringUtils;

import com.dongyang.jdo.*;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;

/**
 * <p>Title: 门诊护士站对外接口</p>
 *
 * <p>Description: 门诊护士站对外接口</p>
 *
 * <p>Copyright: Copyright (c) 2008</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author zhangk 2010-2-5
 * @version 1.0
 */
public class ONWTool
    extends TJDOTool {
    /**
     * 实例
     */
    public static ONWTool instanceObject;
    /**
     * 得到实例
     * @return PositionTool
     */
    public static ONWTool getInstance() {
        if (instanceObject == null)
            instanceObject = new ONWTool();
        return instanceObject;
    }
    public ONWTool() {
        onInit();
    }
    /**
     * 修改门急诊医嘱明细档的护士备注
     * @param parm TParm 必须参数：CASE_NO;RX_NO;SEQ_NO;NS_NOTE
     * @return TParm
     */
    public TParm updateNS_NOTE(TParm parm,TConnection conn){
        String sql = "UPDATE OPD_ORDER SET NS_NOTE='"+parm.getValue("NS_NOTE").replaceAll("'","''")+"' "+",exec_dr_desc='"+parm.getValue("EXEC_DR_DESC")+"'"+",BATCH_NO='"+parm.getValue("BATCH_NO")+"'"+",SKINTEST_FLG='"+parm.getValue("SKINTEST_FLG")+"'"+",EXEC_DATE=to_date('"+parm.getValue("EXEC_DATE").toString().substring(0,19)+"','yyyy/MM/dd HH24:MI:SS')"+",NS_EXEC_DATE='"+parm.getValue("NS_EXEC_DATE")+"' " +
            "WHERE CASE_NO='"+parm.getValue("CASE_NO")+"' AND RX_NO='"+parm.getValue("RX_NO")+"'";
        TParm result = new TParm();
        result.setData(TJDODBTool.getInstance().update(sql,conn));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
    
    /**
     * 查询急诊护士站中已办理住院登记手续的病患数据
     * 
     * @param parm
     * @return result
     * @author wangb 2016/1/25
     */
    public TParm queryInHospData(TParm parm) {
    	StringBuffer sbSql = new StringBuffer();
    	sbSql.append("SELECT ADM_TYPE,A.CASE_NO,A.MR_NO,A.REGION_CODE,A.ADM_DATE,A.REG_DATE,A.SESSION_CODE,A.CLINICAREA_CODE,");
    	sbSql.append("A.CLINICROOM_NO,A.QUE_NO,A.REG_ADM_TIME,A.DEPT_CODE,A.DR_CODE,A.REALDEPT_CODE,A.REALDR_CODE,A.APPT_CODE,");
    	sbSql.append("A.VISIT_CODE,A.REGMETHOD_CODE,A.CTZ1_CODE,A.CTZ2_CODE,A.CTZ3_CODE,A.TRANHOSP_CODE,A.TRIAGE_NO,A.CONTRACT_CODE,");
    	sbSql.append("A.ARRIVE_FLG,A.REGCAN_USER,A.REGCAN_DATE,A.ADM_REGION,A.PREVENT_SCH_CODE,A.DRG_CODE,A.HEAT_FLG,A.ADM_STATUS,");
    	sbSql.append("A.REPORT_STATUS,A.WEIGHT,A.HEIGHT,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.PAT_NAME,A.CLINICTYPE_CODE,A.VIP_FLG,");
    	sbSql.append("B.SEX_CODE,A.SERVICE_LEVEL,A.ERD_LEVEL,D.CASE_NO AS IN_CASE_NO,C.DEPT_CODE AS IN_DEPT_CODE");
    	sbSql.append(" FROM REG_PATADM A, SYS_PATINFO B, ADM_RESV C, ADM_INP D ");
    	sbSql.append(" WHERE A.MR_NO = B.MR_NO AND A.REGCAN_USER IS NULL ");
    	sbSql.append(" AND A.ARRIVE_FLG='Y'"); //add by huangtt 20160922 护士站只显示已经报道过的病人
    	sbSql.append(" AND A.ADM_DATE=TO_DATE('");
    	sbSql.append(parm.getValue("ADM_DATE"));
    	sbSql.append("','YYYYMMDD') AND A.MR_NO = C.MR_NO ");
    	sbSql.append(" AND A.CASE_NO = C.OPD_CASE_NO AND C.CAN_DATE IS NULL ");
    	sbSql.append(" AND C.IN_CASE_NO = D.CASE_NO AND D.CANCEL_FLG = 'N' ");
    	
    	// 未检伤
    	if (StringUtils.equals("Y", parm.getValue("LEVEL_FLG"))) {
    		sbSql.append(" AND A.TRIAGE_NO IS NULL ");
    	}
    	
    	if (StringUtils.isNotEmpty(parm.getValue("CASE_NO"))) {
    		sbSql.append(" AND A.CASE_NO = '");
    		sbSql.append(parm.getValue("CASE_NO"));
    		sbSql.append("' ");
    	}
    	
    	sbSql.append(" ORDER BY QUE_NO ");
    	
    	TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
        if (result.getErrCode() < 0) {
            err("ERR:" + result.getErrCode() + result.getErrText() +
                result.getErrName());
            return result;
        }
        return result;
    }
}
