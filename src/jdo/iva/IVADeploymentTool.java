package jdo.iva;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 静配中心调配工具类
 * </p>
 * 
 * <p>
 * Description: 静配中心调配工具类
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
 * @author zhangy 2013.07.21
 * @version 1.0
 */
public class IVADeploymentTool extends TJDODBTool {
	/** 单例模式 */
	private static IVADeploymentTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static IVADeploymentTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVADeploymentTool();
		}
		return instanceObject;
	}

	/*
	 * 查询未调配/已调配信息
	 */
	public TParm queryInfo(TParm parm) {
		String sql = "SELECT '"+parm.getValue("FLG")+"' AS SELECT_AVG,C.STATION_DESC,"
				+ "B.PAT_NAME,A.CASE_NO ,A.MR_NO,E.BED_NO_DESC,A.IVA_DEPLOY_USER,"
				+ "D.CHN_DESC,A.STATION_CODE FROM ODI_DSPNM A,ODI_DSPND F,SYS_PATINFO B,SYS_STATION C,"
				+ "SYS_DICTIONARY D,SYS_BED E "
				+ " WHERE ";
		if(parm.getValue("FLG").equals("N")){
			sql += " A.PREPARE_CHECK_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND A.IVA_FLG = 'Y' "
					+ " AND A.PREPARE_CHECK_USER IS NOT NULL ";
		}else if(parm.getValue("FLG").equals("Y")){
			sql += " A.IVA_DEPLOY_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND A.IVA_FLG = 'Y' "
					+ " AND A.PREPARE_CHECK_USER IS NOT NULL ";
		}
		if (parm.getValue("STATION_CODE") != null
				&& !"".equals(parm.getValue("STATION_CODE"))) {
			sql += " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "'";
		}
		if (parm.getValue("MR_NO") != null
				&& !"".equals(parm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + parm.getValue("MR_NO") + "'";
		}
		sql += " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.CASE_NO = F.CASE_NO "
				+ " AND A.ORDER_NO = F.ORDER_NO "
				+ " AND A.ORDER_SEQ = F.ORDER_SEQ "
				+ " AND F.ORDER_DATE || F.ORDER_DATETIME BETWEEN A.START_DTTM "
														 + " AND A.END_DTTM ";
		if (parm.getValue("BAR_CODE") != null
				&& !"".equals(parm.getValue("BAR_CODE"))) {
			sql += " AND F.BAR_CODE='" + parm.getValue("BAR_CODE") + "'";
		}
		if (parm.getValue("IVA_DEPLOY_USER") != null
				&& !"".equals(parm.getValue("IVA_DEPLOY_USER"))) {
			sql += " AND F.IVA_DEPLOY_USER='" + parm.getValue("IVA_DEPLOY_USER")
					+ "' ";
		}
		if(parm.getValue("FLG").equals("N")){
			sql += " AND F.IVA_DEPLOY_USER IS NULL "
					+" AND (F.DC_DATE IS NULL OR SUBSTR( TO_CHAR(F.DC_DATE,'YYYYMMDDHH24MISS'),1,12) "
//					+ " >= F.ORDER_DATE || F.ORDER_DATETIME) "
					+ " >= (SELECT F.ORDER_DATE "
					+ " || G.DCCHECK_TIME FROM ODI_BATCHTIME G WHERE G.BATCH_CODE='"
					+parm.getValue("BATCH_CODE")+"')) ";
		}else if(parm.getValue("FLG").equals("Y")){
			sql += " AND F.IVA_DEPLOY_USER IS NOT NULL ";
		}
		sql += " AND F.IVA_RETN_USER IS NULL "
				+ " AND F.IVA_FLG ='Y' "
				+ " AND F.BATCH_CODE='" + parm.getValue("BATCH_CODE") + "' "
				+ " AND A.MR_NO=B.MR_NO "
				+ " AND A.STATION_CODE=C.STATION_CODE "
				+ " AND D.ID=B.SEX_CODE "
				+ " AND D.GROUP_ID='SYS_SEX' "
				+ " AND A.BED_NO = E.BED_NO "
				+ " GROUP BY 'N',C.STATION_DESC,B.PAT_NAME ,A.CASE_NO,"
				+ " A.MR_NO,A.STATION_CODE,E.BED_NO_DESC,A.IVA_DEPLOY_USER,D.CHN_DESC "
				+ " ORDER BY A.STATION_CODE,E.BED_NO_DESC ";
//		System.out.println("sql====="+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 查询已调配信息
	 */
	public TParm querycheck(TParm parm) {
		String sql = "SELECT 'Y' AS SELECT_AVG,C.STATION_DESC,B.PAT_NAME ,A.CASE_NO ,A.MR_NO,E.BED_NO_DESC,A.IVA_DEPLOY_USER,D.CHN_DESC "
				+ "FROM ODI_DSPNM A,SYS_PATINFO B,SYS_STATION C,SYS_DICTIONARY D,SYS_BED E,ODI_DSPND F"
				+ " WHERE 1=1 ";
		if (parm.getValue("STATION_CODE") != null
				&& !"".equals(parm.getValue("STATION_CODE"))) {
			sql += " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "' ";
		}
		if (parm.getValue("MR_NO") != null
				&& !"".equals(parm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + parm.getValue("MR_NO") + "' ";
		}
		if (parm.getValue("IVA_DEPLOY_USER") != null
				&& !"".equals(parm.getValue("IVA_DEPLOY_USER"))) {
			sql += " AND A.IVA_DEPLOY_USER='" + parm.getValue("IVA_DEPLOY_USER")
					+ "' ";
		}
		if (!"".equals(parm.getValue("START_TIME"))
				&& !"".equals(parm.getValue("END_TIME"))) {

			sql += " AND A.IVA_DEPLOY_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') ";
		}
		if (parm.getValue("BAR_CODE") != null
				&& !"".equals(parm.getValue("BAR_CODE"))
				&& parm.getValue("BATCH_CODE") != null
				&& !"".equals(parm.getValue("BATCH_CODE"))) {
			sql += " AND F.BAR_CODE='" + parm.getValue("BAR_CODE") 
					+ "' AND F.BATCH_CODE='" + parm.getValue("BATCH_CODE")
					+ "' AND A.CASE_NO = F.CASE_NO "
					+ " AND A.ORDER_NO = F.ORDER_NO "
					+ " AND A.ORDER_SEQ = F.ORDER_SEQ "
					+ " AND F.ORDER_DATE || F.ORDER_DATETIME BETWEEN A.START_DTTM "
		                                                 	 + " AND A.END_DTTM ";
		}else if(parm.getValue("BAR_CODE") != null
					&& !"".equals(parm.getValue("BAR_CODE"))){
			sql += " AND F.BAR_CODE='" + parm.getValue("BAR_CODE") 
					+ "' AND A.CASE_NO = F.CASE_NO "
					+ " AND A.ORDER_NO = F.ORDER_NO "
					+ " AND A.ORDER_SEQ = F.ORDER_SEQ "
					+ " AND F.ORDER_DATE || F.ORDER_DATETIME BETWEEN A.START_DTTM "
		                                                 	 + " AND A.END_DTTM ";
		}else if(parm.getValue("BATCH_CODE") != null
					&& !"".equals(parm.getValue("BATCH_CODE"))){
			sql += " AND F.BATCH_CODE='" + parm.getValue("BATCH_CODE")
					+ "' AND A.CASE_NO = F.CASE_NO "
					+ " AND A.ORDER_NO = F.ORDER_NO "
					+ " AND A.ORDER_SEQ = F.ORDER_SEQ "
					+ " AND F.ORDER_DATE || F.ORDER_DATETIME BETWEEN A.START_DTTM "
		                                                 	 + " AND A.END_DTTM ";
		}
		sql += " AND A.PREPARE_USER IS NOT NULL "
				+ " AND A.IVA_FLG = 'Y' "
				+ " AND A.IVA_DEPLOY_USER IS NOT  NULL "
				+ " AND A.MR_NO=B.MR_NO  "
				+ " AND A.STATION_CODE=C.STATION_CODE "
				+ " AND D.ID=B.SEX_CODE  "
				+ " AND D.GROUP_ID='SYS_SEX' "
				+ " AND A.BED_NO = E.BED_NO "
				+ " GROUP BY 'N',C.STATION_DESC,B.PAT_NAME ,A.CASE_NO,A.MR_NO,E.BED_NO_DESC,A.IVA_DEPLOY_USER,D.CHN_DESC ";
//		System.out.println("sql=========="+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));

	}

	/*
	 * 患者药品明细
	 */
	public TParm querydetail(TParm tparm) {
		String sql = "SELECT  A.LINKMAIN_FLG AS SELECT_FLG,A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,"
				+ " A.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY,B.DOSAGE_QTY || F.UNIT_CHN_DESC AS DOSAGE_QTY, "
				+ "B.ORDER_DATE || B.ORDER_DATETIME AS EXEC_DATE,"
				+ "C.FREQ_CHN_DESC,A.FREQ_CODE,D.ROUTE_CHN_DESC,A.LINK_NO,B.BAR_CODE "
				+ "FROM ODI_DSPNM A,ODI_DSPND B,SYS_PHAFREQ C,SYS_PHAROUTE D,SYS_UNIT E,SYS_UNIT F "
				+ "WHERE  A.CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND A.IVA_FLG = 'Y' "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.PREPARE_CHECK_USER IS NOT NULL ";
		if (tparm.getValue("STATION_CODE") != null
				&& !"".equals(tparm.getValue("STATION_CODE"))) {
			sql += " AND A.STATION_CODE='" + tparm.getValue("STATION_CODE")
					+ "'";
		}
		if (tparm.getValue("MR_NO") != null
				&& !"".equals(tparm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + tparm.getValue("MR_NO") + "'";
		}
		sql += "AND A.CASE_NO=B.CASE_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM ";
		if(tparm.getValue("FLG").equals("N")){
			sql += " AND B.PREPARE_CHECK_TIME BETWEEN TO_DATE('"
					+ tparm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ tparm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND B.IVA_DEPLOY_USER IS NULL "
					+" AND (B.DC_DATE IS NULL OR SUBSTR( TO_CHAR(B.DC_DATE,'YYYYMMDDHH24MISS'),1,12) "
//					+ " >= B.ORDER_DATE || B.ORDER_DATETIME) "
					+ " >= (SELECT B.ORDER_DATE "
					+ " || G.DCCHECK_TIME FROM ODI_BATCHTIME G WHERE G.BATCH_CODE='"
					+tparm.getValue("BATCH_CODE")+"')) ";
		}else if(tparm.getValue("FLG").equals("Y")){
			sql += " AND B.IVA_DEPLOY_TIME BETWEEN TO_DATE('"
					+ tparm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ tparm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND B.IVA_DEPLOY_USER IS NOT NULL ";
			if (tparm.getValue("IVA_DEPLOY_USER") != null
					&& !"".equals(tparm.getValue("IVA_DEPLOY_USER"))) {
				sql += " AND B.IVA_DEPLOY_USER='" + tparm.getValue("IVA_DEPLOY_USER")
						+ "' ";
			}
		}
		if (tparm.getValue("BAR_CODE") != null
				&& !"".equals(tparm.getValue("BAR_CODE"))) {
			sql += " AND B.BAR_CODE='" + tparm.getValue("BAR_CODE") + "'";
		}
		if (tparm.getValue("BATCH_CODE") != null
				&& !"".equals(tparm.getValue("BATCH_CODE"))) {
			sql += " AND B.BATCH_CODE='" + tparm.getValue("BATCH_CODE") + "'";
		}
		sql += " AND B.IVA_FLG = 'Y' "
				+ " AND B.PREPARE_CHECK_USER IS NOT NULL "
				+ " AND B.IVA_RETN_USER IS NULL "
				+ " AND A.FREQ_CODE=C.FREQ_CODE AND A.ROUTE_CODE=D.ROUTE_CODE AND A.MEDI_UNIT=E.UNIT_CODE "
				+ " AND B.DOSAGE_UNIT = F.UNIT_CODE "
				+ " ORDER BY B.ORDER_DATE,B.ORDER_DATETIME, CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END";
//		System.out.println(sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm onqueryPatch(TParm parm) {
		String sql = "SELECT BATCH_CODE,BATCH_CHN_DESC FROM ODI_BATCHTIME";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm onqueryPatchTime(TParm parm) {
		String sql = "SELECT IVA_TIME, TREAT_START_TIME,TREAT_END_TIME FROM ODI_BATCHTIME WHERE BATCH_CODE='"
				+ parm.getValue("BATCH_CODE") + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 调配
	 */
	public TParm updateInfoM(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPNM SET IVA_DEPLOY_USER='"
				+ tparm.getValue("IVA_DEPLOY_USER") + "',"
				+ "IVA_DEPLOY_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND ORDER_NO='"+ tparm.getValue("ORDER_NO")
				+ "' AND ORDER_SEQ='"+ tparm.getValue("ORDER_SEQ")
				+ "' AND START_DTTM = '"+ tparm.getValue("START_DTTM")
				+ "' AND END_DTTM = '"+ tparm.getValue("END_DTTM")
				+ "' AND IVA_FLG='Y' AND PREPARE_USER IS NOT NULL";
//		System.out.println("sqlM========"+sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	public TParm updateInfoD(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPND SET IVA_DEPLOY_USER='"
				+ tparm.getValue("IVA_DEPLOY_USER") + "',"
				+ "IVA_DEPLOY_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND ORDER_NO='"+ tparm.getValue("ORDER_NO")
				+ "' AND ORDER_SEQ='"+ tparm.getValue("ORDER_SEQ")
				+ "' AND ORDER_DATE = '"+ tparm.getValue("ORDER_DATE")
				+ "' AND BATCH_CODE = '"+ tparm.getValue("BATCH_CODE")
				+ "' AND BAR_CODE = '"+ tparm.getValue("BAR_CODE")
				+ "' AND ORDER_DATETIME = '"+ tparm.getValue("ORDER_DATETIME")
				+ "' AND IVA_FLG='Y' AND PREPARE_CHECK_USER IS NOT NULL "
				+ " AND IVA_DEPLOY_USER IS NULL "
				+ " AND IVA_RETN_USER IS NULL ";
//		System.out.println("sqlM========"+sql);
		return new TParm(TJDODBTool.getInstance().update(sql,conn));
	}
	
	/**
	 * 根据批次代码查询静脉输液配置时间设定表
	 * @param batch_code
	 * @author shendr
	 * @return
	 */
	public TParm queryByBatchCode(String batch_code){
		String sql = "SELECT IVA_TIME,DCCHECK_TIME,BATCH_CHN_DESC,TREAT_START_TIME,TREAT_END_TIME, "
			        +"REMARK,OPT_USER,OPT_DATE,OPT_TERM,BATCH_ENG_DESC, "
			        +"PY1,PY2,SEQ,DOSAGE_DEPT "
			        +"FROM ODI_BATCHTIME "
			        +"WHERE BATCH_CODE = '"+batch_code+"'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 查询是否有未退药品
	 * @param dccheck_time
	 * @author shendr
	 * @return
	 */
	public TParm queryMDByDCTime(TParm parm){
		String sql = "SELECT COUNT(A.CASE_NO) AS COUNTS FROM ODI_DSPNM A,ODI_DSPND B "
				+ " WHERE A.PREPARE_CHECK_TIME BETWEEN TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') "
//				+ " AND A.DC_DATE <= TO_DATE('"+parm.getValue("CHECK_DATE")
//				+ "','YYYY-MM-DD HH24:MI:SS') "
				+ " AND A.IVA_FLG = 'Y' "
				+ " AND A.PREPARE_CHECK_USER IS NOT NULL "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') ";
		if (parm.getValue("STATION_CODE") != null
				&& !"".equals(parm.getValue("STATION_CODE"))) {
			sql += " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "'";
		}
		if (parm.getValue("MR_NO") != null
				&& !"".equals(parm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + parm.getValue("MR_NO") + "'";
		}
		if (parm.getValue("BAR_CODE") != null
				&& !"".equals(parm.getValue("BAR_CODE"))) {
			sql += " AND B.BAR_CODE='" + parm.getValue("BAR_CODE") + "'";
		}
		sql += " AND B.IVA_DEPLOY_USER IS NULL "
//				+ " AND SUBSTR( TO_CHAR(B.DC_DATE,'YYYYMMDDHH24MISS'),1,12) < B.ORDER_DATE || B.ORDER_DATETIME"
				+" AND (B.DC_DATE IS NOT NULL AND SUBSTR( TO_CHAR(B.DC_DATE,'YYYYMMDDHH24MISS'),1,12) "
//				+ " >= F.ORDER_DATE || F.ORDER_DATETIME) "
				+ " <= (SELECT B.ORDER_DATE "
				+ " || G.DCCHECK_TIME FROM ODI_BATCHTIME G WHERE G.BATCH_CODE='"
				+parm.getValue("BATCH_CODE")+"')) "
				+ " AND B.IVA_RETN_USER IS NULL "
				+ " AND B.IVA_FLG = 'Y' "
				+ " AND B.PREPARE_CHECK_USER IS NOT NULL "
				+ " AND B.BATCH_CODE='" + parm.getValue("BATCH_CODE") + "' "
				+ " AND A.CASE_NO = B.CASE_NO "
				+ " AND A.ORDER_NO = B.ORDER_NO "
				+ " AND A.ORDER_SEQ = B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME BETWEEN A.START_DTTM "
														 + " AND A.END_DTTM ";
//		System.out.println(sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 全部打印报表dataSource
	 * @return
	 */
	public TParm queryAllData(){
		String sql = "SELECT  H.PAT_NAME,A.MR_NO,G.STATION_DESC,F.BED_NO_DESC,A.LINKMAIN_FLG AS SELECT_FLG, "
			        +"A.ORDER_DESC,A.SPECIFICATION,B.MEDI_QTY || E.UNIT_CHN_DESC AS MEDI_QTY,B.NS_EXEC_DATE, "
			        +"C.FREQ_CHN_DESC,D.ROUTE_CHN_DESC,A.LINK_NO "
			        +"FROM ODI_DSPNM A,ODI_DSPND B,SYS_PHAFREQ C,SYS_PHAROUTE D,SYS_UNIT E ,SYS_BED F,SYS_STATION G,SYS_PATINFO H "
			        +"WHERE A.PREPARE_USER IS NOT NULL  "
			        +"AND A.ORDER_CAT1_CODE IN ('PHA_W','PHA_C') "
			        +"AND A.CASE_NO=B.CASE_NO  "
			        +"AND A.ORDER_NO=B.ORDER_NO  "
			        +"AND A.ORDER_SEQ=B.ORDER_SEQ  "
			        +"AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM " 
			        +"AND A.FREQ_CODE=C.FREQ_CODE AND A.ROUTE_CODE=D.ROUTE_CODE AND A.MEDI_UNIT=E.UNIT_CODE " 
			        +"AND A.BED_NO = F.BED_NO "
			        +"AND A.STATION_CODE = G.STATION_CODE "
			        +"AND A.MR_NO = H.MR_NO "
			        +"ORDER BY A.LINK_NO, CASE A.LINKMAIN_FLG WHEN 'Y' THEN '1' ELSE '2' END";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
