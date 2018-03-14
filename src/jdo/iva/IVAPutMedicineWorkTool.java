package jdo.iva;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 静配中心排药工具类
 * </p>
 * 
 * <p>
 * Description: 静配中心排药工具类
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
public class IVAPutMedicineWorkTool extends TJDODBTool {
	/** 单例模式 */
	private static IVAPutMedicineWorkTool instanceObject;

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static IVAPutMedicineWorkTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVAPutMedicineWorkTool();
		}
		return instanceObject;
	}

	// 查询未排药信息
	public TParm queryInfo(TParm parm) {
		String sql = "SELECT '"+parm.getValue("FLG")+"' AS SELECT_AVG,A.CASE_NO,"
				+ " A.MR_NO,A.PREPARE_USER,B.PAT_NAME,C.STATION_DESC,D.CHN_DESC,"
				+ " E.BED_NO_DESC,A.STATION_CODE "
				+ " FROM ODI_DSPNM A,SYS_PATINFO B,SYS_STATION C,SYS_DICTIONARY D,"
				+ " SYS_BED E "
				+ " WHERE ";
		if(parm.getValue("FLG").equals("N")){
			sql += "A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
				+ parm.getValue("START_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
				+ parm.getValue("END_TIME")
				+ "','YYYY-MM-DD HH24:MI:SS') "
				+ " AND A.IVA_FLG = 'Y' "
				+ " AND A.PREPARE_USER IS NULL ";
		}else if(parm.getValue("FLG").equals("Y")){
			sql += "A.PREPARE_TIME BETWEEN TO_DATE('"
					+ parm.getValue("START_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
					+ parm.getValue("END_TIME")
					+ "','YYYY-MM-DD HH24:MI:SS') "
					+ " AND A.IVA_FLG = 'Y' "
					+ " AND A.PREPARE_USER IS NOT NULL ";
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
		if (parm.getValue("PREPARE_USER") != null
				&& !"".equals(parm.getValue("PREPARE_USER"))) {
			sql += " AND A.PREPARE_USER='" + parm.getValue("PREPARE_USER")
					+ "' ";
		}
		sql += " AND A.ORDER_CAT1_CODE  IN ('PHA_W','PHA_C') "
				+ " AND A.MR_NO=B.MR_NO "
				+ " AND A.STATION_CODE=C.STATION_CODE "
				+ " AND D.ID=B.SEX_CODE "
				+ " AND D.GROUP_ID='SYS_SEX' "
				+ " AND A.BED_NO=E.BED_NO"
				+ " AND (SELECT COUNT (F.CASE_NO) "
				+ " FROM ODI_DSPND F "
				+ " WHERE A.CASE_NO = F.CASE_NO "
				+ " AND A.ORDER_NO = F.ORDER_NO "
				+ " AND A.ORDER_SEQ = F.ORDER_SEQ "
				+ " AND F.ORDER_DATE || F.ORDER_DATETIME BETWEEN A.START_DTTM "
			                                        + " AND A.END_DTTM "
			    + " AND F.IVA_FLG='Y' ";
	    if(parm.getValue("FLG").equals("N")){
	    	sql += " AND F.IVA_RETN_USER IS NULL ";
	    }
		sql+= " )>0 "
			+ " GROUP BY 'N',C.STATION_DESC,B.PAT_NAME ,A.CASE_NO, "
			+ " A.MR_NO,E.BED_NO_DESC,A.STATION_CODE,A.PREPARE_USER,D.CHN_DESC "
			+ " ORDER BY A.STATION_CODE,E.BED_NO_DESC";
//		System.out.println("sql========="+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	public TParm queryonfo(TParm tparm) {
		return new TParm();
	}

	// 药品明细
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
				+ " AND A.CASE_NO=B.CASE_NO "
				+ " AND A.ORDER_NO=B.ORDER_NO "
				+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
				+ " AND B.ORDER_DATE || B.ORDER_DATETIME  "
				+ " BETWEEN  A.START_DTTM AND  A.END_DTTM ";
				if(tparm.getValue("FLG").equals("N")){
					sql += " AND B.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
						+ tparm.getValue("START_TIME")
						+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
						+ tparm.getValue("END_TIME")
						+ "','YYYY-MM-DD HH24:MI:SS') "
						+ " AND B.PREPARE_USER IS NULL ";
				}else if(tparm.getValue("FLG").equals("Y")){
					sql += " AND B.PREPARE_TIME BETWEEN TO_DATE('"
							+ tparm.getValue("START_TIME")
							+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
							+ tparm.getValue("END_TIME")
							+ "','YYYY-MM-DD HH24:MI:SS') ";
					if (tparm.getValue("PREPARE_USER") != null
							&& !"".equals(tparm.getValue("PREPARE_USER"))) {
						sql += " AND B.PREPARE_USER='" + tparm.getValue("PREPARE_USER")
								+ "' ";
					}
				}
				sql += " AND B.IVA_FLG = 'Y' "
						+ " AND B.IVA_RETN_USER IS NULL "
						+ " AND A.FREQ_CODE=C.FREQ_CODE AND A.ROUTE_CODE=D.ROUTE_CODE "
						+ " AND A.MEDI_UNIT=E.UNIT_CODE "
						+ " AND B.DOSAGE_UNIT = F.UNIT_CODE "
						+ " ORDER BY B.ORDER_DATE,B.ORDER_DATETIME, "
						+ " CASE WHEN A.LINKMAIN_FLG = 'Y' THEN '1' ELSE '2' END";
//		System.out.println("sql====="+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	// 排药保存
	public TParm updateInfoM(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPNM SET PREPARE_USER='"
				+ tparm.getValue("PREPARE_USER") + "',"
				+ "PREPARE_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND ORDER_NO='"+ tparm.getValue("ORDER_NO")
				+ "' AND ORDER_SEQ='"+ tparm.getValue("ORDER_SEQ")
				+ "' AND START_DTTM = '"+ tparm.getValue("START_DTTM")
				+ "' AND END_DTTM = '"+ tparm.getValue("END_DTTM")
				+ "' AND IVA_FLG='Y' AND PHA_DOSAGE_CODE IS NOT NULL ";
//		System.out.println("sql===>"+sql);
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

	public TParm updateInfoD(TParm tparm, TConnection conn) {
		String sql = "UPDATE ODI_DSPND SET PREPARE_USER='"
				+ tparm.getValue("PREPARE_USER") + "',"
				+ "PREPARE_TIME=SYSDATE WHERE CASE_NO='"
				+ tparm.getValue("CASE_NO")
				+ "' AND ORDER_NO='"+ tparm.getValue("ORDER_NO")
				+ "' AND ORDER_SEQ='"+ tparm.getValue("ORDER_SEQ")
				+ "' AND ORDER_DATE = '"+ tparm.getValue("ORDER_DATE")
				+ "' AND BATCH_CODE = '"+ tparm.getValue("BATCH_CODE")
				+ "' AND BAR_CODE = '"+ tparm.getValue("BAR_CODE")
				+ "' AND ORDER_DATETIME = '"+ tparm.getValue("ORDER_DATETIME")
				+ "' AND IVA_FLG='Y' AND PREPARE_USER IS NULL "
				+ " AND PHA_DOSAGE_CODE IS NOT NULL ";
		return new TParm(TJDODBTool.getInstance().update(sql, conn));
	}

}
