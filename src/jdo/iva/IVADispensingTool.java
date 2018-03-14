package jdo.iva;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
/**
 * <p>
 * Title: 静配中心发药工具类
 * </p>
 * 
 * <p>
 * Description: 静配中心发药工具类
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
public class IVADispensingTool extends TJDODBTool {
	/** 单例模式 */
	private static IVADispensingTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static IVADispensingTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVADispensingTool();
		}
		return instanceObject;
	}

	/*
	 * 查询未发药信息
	 */
	public TParm queryInfo(TParm parm) {
		String sql = "SELECT '"+parm.getValue("FLG")+"' AS SELECT_AVG,"
					+ "C.STATION_DESC,B.PAT_NAME ,A.CASE_NO,A.MR_NO,A.STATION_CODE,"
					+ "E.BED_NO_DESC,A.IVA_DISPENSE_USER,D.CHN_DESC,A.IVA_DISPENSE_TIME "
					+ " FROM ODI_DSPNM A,SYS_PATINFO B,SYS_STATION C,"
					+ "SYS_DICTIONARY D ,SYS_BED E,ODI_DSPND F "
					+ " WHERE ";
		if(parm.getValue("FLG").equals("N")){
			sql += " A.IVA_CHECK_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') ";
		}else if(parm.getValue("FLG").equals("Y")){
			sql += " A.IVA_DISPENSE_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') ";
		}
		if (parm.getValue("STATION_DESC") != null
				&& !"".equals(parm.getValue("STATION_DESC"))) {
			sql += " AND A.STATION_CODE='" + parm.getValue("STATION_DESC")
					+ "' ";
		}
		if (parm.getValue("MR_NO") != null
				&& !"".equals(parm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + parm.getValue("MR_NO") + "' ";
		}
		if (parm.getValue("IVA_DISPENSE_USER") != null
				&& !"".equals(parm.getValue("IVA_DISPENSE_USER"))) {
			sql += " AND A.IVA_DISPENSE_USER='"
					+ parm.getValue("IVA_DISPENSE_USER") + "' ";
		}
		sql += " AND A.IVA_FLG = 'Y' "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.IVA_CHECK_USER IS NOT NULL "
				+ " AND A.MR_NO=B.MR_NO "
				+ " AND A.STATION_CODE=C.STATION_CODE "
				+ " AND D.ID=B.SEX_CODE "
				+ " AND D.GROUP_ID='SYS_SEX' "
				+ " AND A.BED_NO=E.BED_NO"
				+ " AND A.CASE_NO = F.CASE_NO "
				+ " AND A.ORDER_NO = F.ORDER_NO "
				+ " AND A.ORDER_SEQ = F.ORDER_SEQ "
				+ " AND F.ORDER_DATE || F.ORDER_DATETIME BETWEEN A.START_DTTM "
                                                		  +" AND A.END_DTTM "
                + " AND F.IVA_CHECK_USER IS NOT NULL ";
		if (parm.getValue("BAR_CODE") != null
				&& !"".equals(parm.getValue("BAR_CODE"))) {
			sql += " AND F.BAR_CODE='" + parm.getValue("BAR_CODE")+ "' ";
		}
		if (parm.getValue("BATCH_CODE") != null
				&& !"".equals(parm.getValue("BATCH_CODE"))) {
			sql += " AND F.BATCH_CODE='" + parm.getValue("BATCH_CODE")+ "' ";
		}
		if(parm.getValue("FLG").equals("N")){
			sql += " AND F.IVA_DISPENSE_USER IS NULL"
					+ " AND F.IVA_CHECK_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') ";
		}else if(parm.getValue("FLG").equals("Y")){
			sql += " AND F.IVA_DISPENSE_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND F.IVA_DISPENSE_USER IS NOT NULL ";
			if (parm.getValue("BATCH_CODE") != null
					&& !"".equals(parm.getValue("BATCH_CODE"))) {
				sql += " AND( A.DSPN_KIND = 'ST' OR TO_DATE(F.ORDER_DATE || F.ORDER_DATETIME,"
						+ "'YYYY-MM-DD HH24:MI') BETWEEN TO_DATE('"
						+ parm.getValue("START_TIME")
						+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
						+ parm.getValue("END_TIME")
						+ "','YYYY-MM-DD HH24:MI:SS')) ";
			}
		}
		String sqlEnd = " GROUP BY 'N',C.STATION_DESC,B.PAT_NAME,"
				+ "A.CASE_NO,A.MR_NO,E.BED_NO_DESC,A.STATION_CODE,"
				+ "A.IVA_DISPENSE_USER,D.CHN_DESC,A.IVA_DISPENSE_TIME "
				+ " ORDER BY A.STATION_CODE,E.BED_NO_DESC";
//		System.out.println("sql====="+sql+sqlEnd);
		return new TParm(TJDODBTool.getInstance().select(sql+sqlEnd));
	}

	/*
	 * 查询已发药信息
	 */
	public TParm querycheck(TParm parm) {
		String sql = "";
		if (parm.getValue("BAR_CODE") != null
				&& !"".equals(parm.getValue("BAR_CODE"))) {
			sql = "SELECT 'Y' AS SELECT_AVG,C.STATION_DESC,B.PAT_NAME ,A.CASE_NO,A.MR_NO,E.BED_NO_DESC,A.IVA_DISPENSE_USER,D.CHN_DESC"
					+ " FROM ODI_DSPNM A,SYS_PATINFO B,SYS_STATION C,SYS_DICTIONARY D ,SYS_BED E,ODI_DSPND F "
					+ " WHERE A.IVA_FLG = 'Y'   AND  A.IVA_CHECK_USER IS NOT NULL"
					+ " AND A.IVA_DISPENSE_USER IS NOT NULL"
					+ " AND F.BAR_CODE='" + parm.getValue("BAR_CODE") 
					+ "' AND A.CASE_NO = F.CASE_NO "
					+ " AND A.ORDER_NO = F.ORDER_NO "
					+ " AND A.ORDER_SEQ = F.ORDER_SEQ "
					+ " AND F.ORDER_DATE || F.ORDER_DATETIME BETWEEN A.START_DTTM "
                                                    		 +" AND A.END_DTTM AND ";
		}else{
			sql = "SELECT 'Y' AS SELECT_AVG,C.STATION_DESC,B.PAT_NAME ,A.CASE_NO,A.MR_NO,E.BED_NO_DESC,A.IVA_DISPENSE_USER,D.CHN_DESC"
					+ " FROM ODI_DSPNM A,SYS_PATINFO B,SYS_STATION C,SYS_DICTIONARY D ,SYS_BED E "
					+ " WHERE A.IVA_FLG = 'Y' AND  A.IVA_CHECK_USER IS NOT NULL"
					+ " AND A.IVA_DISPENSE_USER IS NOT NULL ";
		}
		if (parm.getValue("STATION_DESC") != null
				&& !"".equals(parm.getValue("STATION_DESC"))) {
			sql += " AND A.STATION_CODE='" + parm.getValue("STATION_DESC")
					+ "' ";
		}
		if (parm.getValue("MR_NO") != null
				&& !"".equals(parm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + parm.getValue("MR_NO") + "' ";
		}
		if (parm.getValue("IVA_DISPENSE_USER") != null
				&& !"".equals(parm.getValue("IVA_DISPENSE_USER"))) {
			sql += " AND A.IVA_DISPENSE_USER='"
					+ parm.getValue("IVA_DISPENSE_USER") + "' ";
		}
		if (!"".equals(parm.getValue("START_TIME"))
				&& !"".equals(parm.getValue("END_TIME"))) {

			sql += " AND A.IVA_DISPENSE_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') ";
		}
		sql += " AND A.MR_NO=B.MR_NO "
				+ " AND A.STATION_CODE=C.STATION_CODE "
				+ " AND D.ID=B.SEX_CODE "
				+ " AND D.GROUP_ID='SYS_SEX' "
				+ " AND A.BED_NO=E.BED_NO"
				+ " GROUP BY 'N',C.STATION_DESC,B.PAT_NAME ,A.CASE_NO,A.MR_NO,E.BED_NO_DESC,A.IVA_DISPENSE_USER,D.CHN_DESC ";

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
				+ tparm.getValue("CASE_NO")+"' "
				+ " AND A.IVA_FLG = 'Y' "
				+ " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') ";
		if (tparm.getValue("STATION_DESC") != null
				&& !"".equals(tparm.getValue("STATION_DESC"))) {
			sql += " AND A.STATION_CODE='" + tparm.getValue("STATION_DESC")
					+ "' ";
		}
		if (tparm.getValue("MR_NO") != null
				&& !"".equals(tparm.getValue("MR_NO"))) {
			sql += " AND A.MR_NO='" + tparm.getValue("MR_NO") + "' ";
		}
		if(tparm.getValue("FLG").equals("N")){
			sql += " AND B.IVA_CHECK_TIME BETWEEN TO_DATE('"
					+ tparm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ tparm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND B.IVA_DISPENSE_USER IS NULL ";
		}else if(tparm.getValue("FLG").equals("Y")){
			sql += " AND B.IVA_DISPENSE_TIME BETWEEN TO_DATE('"
					+ tparm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
					+ tparm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND B.IVA_DISPENSE_USER IS NOT NULL ";
			if (tparm.getValue("BATCH_CODE") != null
					&& !"".equals(tparm.getValue("BATCH_CODE"))) {
				sql += " AND (A.DSPN_KIND = 'ST' OR TO_DATE(B.ORDER_DATE || B.ORDER_DATETIME,"
						+ "'YYYY-MM-DD HH24:MI') BETWEEN TO_DATE('"
						+ tparm.getValue("START_TIME").substring(0, 16)
						+ "','YYYY-MM-DD HH24:MI')" + " AND TO_DATE('"
						+ tparm.getValue("END_TIME").substring(0, 16)
						+ "','YYYY-MM-DD HH24:MI')) ";
			}	
			if (tparm.getValue("IVA_DISPENSE_USER") != null
					&& !"".equals(tparm.getValue("IVA_DISPENSE_USER"))) {
				sql += " AND B.IVA_DISPENSE_USER='"
						+ tparm.getValue("IVA_DISPENSE_USER") + "' ";
			}
		}
		if (tparm.getValue("BAR_CODE") != null
				&& !"".equals(tparm.getValue("BAR_CODE"))) {
			sql += " AND B.BAR_CODE='" + tparm.getValue("BAR_CODE")+ "' ";
		}
		if (tparm.getValue("BATCH_CODE") != null
				&& !"".equals(tparm.getValue("BATCH_CODE"))) {
			sql += " AND B.BATCH_CODE='" + tparm.getValue("BATCH_CODE")+ "' ";
		}
		sql += " AND B.IVA_CHECK_USER IS NOT NULL "
				+ " AND B.IVA_FLG = 'Y' "
				+ " AND B.IVA_RETN_USER IS NULL "
				+ " AND B.IVA_CHECK_USER IS NOT NULL "
				+ " AND A.CASE_NO=B.CASE_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME  BETWEEN  A.START_DTTM AND  A.END_DTTM "
				+ " AND A.FREQ_CODE=C.FREQ_CODE AND A.ROUTE_CODE=D.ROUTE_CODE AND A.MEDI_UNIT=E.UNIT_CODE "
				+ " AND B.DOSAGE_UNIT = F.UNIT_CODE "
				+ " ORDER BY B.ORDER_DATE,B.ORDER_DATETIME, CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END";
//		System.out.println(sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 调配
	 */
	public TParm updateInfoM(TParm tparm, TConnection conn) {

		String sql = "UPDATE ODI_DSPNM  SET IVA_DISPENSE_USER='"
				+ tparm.getValue("IVA_DISPENSE_USER") + "',"
				+ "IVA_DISPENSE_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND IVA_FLG='Y' AND IVA_CHECK_USER IS NOT NULL";

		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	public TParm updateInfoD(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPND  SET IVA_DISPENSE_USER='"
				+ tparm.getValue("IVA_DISPENSE_USER") + "',"
				+ "IVA_DISPENSE_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND IVA_FLG='Y' AND IVA_CHECK_USER IS NOT NULL "
				+ " AND IVA_DISPENSE_USER IS NULL";

		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

}
