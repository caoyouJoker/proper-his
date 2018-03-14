package jdo.sta;


import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>
 * Title:出院召回查询表Tool
 * </p>
 * 
 * <p>
 * Description:出院召回查询表
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2016-08-30
 * @version JavaHis 1.0
 */
public class STAOutRecallTool extends TJDOTool {
	
	private static STAOutRecallTool mInstance;
	
	public static STAOutRecallTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new STAOutRecallTool();
		}
		return mInstance;
	}

	public STAOutRecallTool() {
		setModuleName("sta\\STAOutRecallModule.x");
		onInit();
	}
	
	/**
	 * 获取查询SQL
	 * @param parm
	 * @return
	 */
	public String getOutRecallSQL(TParm parm) {
		
//		String sqlSelect = "SELECT A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME, A.LAST_DS_DATE AS LAST_DS_DATE, " +
//				     "A.DS_DEPT_CODE AS DS_DEPT_CODE, C.REFUND_DATE AS OPT_DATE, C.REFUND_CODE AS OPT_USER" +
//				     " FROM ADM_INP A, SYS_PATINFO B, BIL_IBS_RECPM C ";
//				    // " AND A.OPT_DATE <= to_date('20160602094433','YYYY/MM/DD HH24:MI:SS')  AND A.MR_NO = B.MR_NO(+) ORDER BY A.OPT_DATE DESC;
//		StringBuilder sql = new StringBuilder(sqlSelect);
//		sql.append( "WHERE A.LAST_DS_DATE IS NOT NULL AND C.REFUND_FLG = 'Y' AND C.REFUND_DATE IS NOT NULL AND  C.REFUND_CODE IS NOT NULL ");
//		
//		String startDate = parm.getValue("START_DATE");
//		String endDate = parm.getValue("END_DATE");
//		if(!StringUtils.isEmpty(startDate)) {
//			sql.append("AND C.REFUND_DATE >= TO_DATE('" + startDate + "', 'YYYY/MM/DD HH24:MI:SS') ");
//		}
//		if(!StringUtils.isEmpty(endDate)){
//			sql.append("AND C.REFUND_DATE <= TO_DATE('" + endDate + "', 'YYYY/MM/DD HH24:MI:SS') ");
//		}
//		sql.append("AND A.MR_NO = B.MR_NO(+) AND A.MR_NO = C.MR_NO(+)  ");
//		
//		
//		String sqlUinonS = " UNION ALL SELECT D.MR_NO AS MR_NO, E.PAT_NAME AS PAT_NAME," +
//						   " D.LAST_DS_DATE AS LAST_DS_DATE," +
//				           " D.DS_DEPT_CODE AS DS_DEPT_CODE," +
//				           " MAX (F.CHG_DATE) AS OPT_DATE,F.OPT_USER AS OPT_USER" +
//				           " FROM ADM_INP D, SYS_PATINFO E, ADM_CHG F " ;
//		StringBuilder sqlUinon = new StringBuilder(sqlUinonS);
//		sqlUinon.append("WHERE D.LAST_DS_DATE IS NOT NULL ");
//		if(!StringUtils.isEmpty(startDate)) {
//			sqlUinon.append("AND F.CHG_DATE >= TO_DATE('" + startDate + "', 'YYYY/MM/DD HH24:MI:SS') ");
//		}
//		if(!StringUtils.isEmpty(endDate)){
//			sqlUinon.append("AND F.CHG_DATE <= TO_DATE('" + endDate + "', 'YYYY/MM/DD HH24:MI:SS') ");
//		}
//		sqlUinon.append(" AND D.MR_NO = E.MR_NO(+) AND D.MR_NO = F.MR_NO(+) ");
//		sqlUinon.append(" GROUP BY D.MR_NO,E.PAT_NAME,D.LAST_DS_DATE,D.DS_DEPT_CODE,F.OPT_USER ");
//		
//		
//		sql.append(sqlUinon);
//		sql.append("  ORDER BY DS_DEPT_CODE, OPT_DATE DESC ");
//		System.out.println("sql :::::::::::::::   " + sql.toString());
//		return sql.toString();
		
		String sqlSelect = "SELECT MR_NO,PAT_NAME,LAST_DS_DATE,DS_DEPT_CODE,DS_STATION_CODE,REFUND_DATE AS OPT_DATE,REFUND_CODE AS OPT_USER " 
			        + "FROM STA_OUT_RECALL WHERE 1=1 ";
		StringBuilder sql = new StringBuilder(sqlSelect);
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		if(!StringUtils.isEmpty(startDate)) {
			sql.append("AND REFUND_DATE >= TO_DATE('" + startDate + "', 'YYYY/MM/DD HH24:MI:SS') ");
		}
		if(!StringUtils.isEmpty(endDate)){
			sql.append("AND REFUND_DATE <= TO_DATE('" + endDate + "', 'YYYY/MM/DD HH24:MI:SS') ");
		}
		sql.append("  ORDER BY MR_NO, DS_DEPT_CODE, OPT_DATE DESC");
		return sql.toString();
	}
	
	/**
	 * 往表中插入数据
	 * @return
	 */
	public TParm insertRecall(TParm insertParm) {
		TParm parm = new TParm();
		parm = this.update("insertData", insertParm);
		return parm;
	}
}
