package jdo.sta;

import com.dongyang.data.TParm;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>
 * Title:取消入院查询表Tool
 * </p>
 * 
 * <p>
 * Description:取消入院查询表
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
public class STACancelHosInTool {
	
	private static STACancelHosInTool mInstance;
	
	public static STACancelHosInTool getNewInstance() {
		if(mInstance == null) {
			mInstance = new STACancelHosInTool();
		}
		return mInstance;
	}

	public STACancelHosInTool() {
		
	}
	
	/**
	 * 获取查询取消入院的SQL
	 * @param parm
	 * @return
	 */
	public String getCancelHosInSQL (TParm parm) {
		String selectSql = "SELECT A.IN_DEPT_CODE AS IN_DEPT_CODE, A.IN_STATION_CODE AS IN_STATION_CODE," +
				"A.MR_NO AS MR_NO, B.PAT_NAME AS PAT_NAME, A.IN_DATE AS IN_DATE, A.OPT_DATE AS OPT_DATE " +
				"FROM ADM_INP A, SYS_PATINFO B ";
		StringBuilder sql = new StringBuilder(selectSql);
		sql.append("WHERE A.CANCEL_FLG = 'Y' ");
		String startDate = parm.getValue("START_DATE");
		String endDate = parm.getValue("END_DATE");
		String mrNo = parm.getValue("MR_NO");
		if(!StringUtils.isEmpty(startDate)) {
			sql.append("AND A.OPT_DATE >= TO_DATE('" + startDate + "', 'YYYY-MM-DD HH24:MI:SS') ");
		}
		if(!StringUtils.isEmpty(endDate)){
			sql.append("AND A.OPT_DATE <= TO_DATE('" + endDate + "', 'YYYY-MM-DD HH24:MI:SS') ");
		}
		if(!StringUtils.isEmpty(mrNo)) {
			sql.append("AND A.MR_NO = '" + mrNo + "' ");
		}
		sql.append("AND A.MR_NO = B.MR_NO(+) ORDER BY A.IN_DEPT_CODE, A.OPT_DATE DESC");
		//System.out.println("sql :::::::::::::   " + sql.toString());
		return sql.toString();
	}
	
	
	
}
