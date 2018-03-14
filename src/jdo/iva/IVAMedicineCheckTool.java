package jdo.iva;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: 病区统药核对工具类
 * </p>
 * 
 * <p>
 * Description: 病区统药核对工具类
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
public class IVAMedicineCheckTool extends TJDODBTool {

	/**
	 * 实例
	 */
	public static IVAMedicineCheckTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return IndAgentTool
	 */
	public static IVAMedicineCheckTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new IVAMedicineCheckTool();
		}
		return instanceObject;
	}

	/*
	 * 查询未审核/已审核单据
	 */
	public TParm queryInfo(TParm tparm) {
		String sqlStart = " AS SELECT_FLG,A.INTGMED_NO,B.STATION_DESC,"
				+ " IVA_INTGMED_CHECK_USER,IVA_INTGMED_CHECK_TIME "
				+ " FROM ODI_DSPNM A,SYS_STATION B "
				+ " WHERE 1=1 ";
		if ((!"".equals(tparm.getValue("START_TIME_N"))) && 
			    (!"".equals(tparm.getValue("END_TIME_N")))) {
			sqlStart = "SELECT 'N'"+sqlStart;
		}
		if ((!"".equals(tparm.getValue("START_TIME_Y"))) && 
			    (!"".equals(tparm.getValue("END_TIME_Y")))) {
			sqlStart = "SELECT 'Y'"+sqlStart;
		}
		if ((!"".equals(tparm.getValue("START_TIME_N"))) && 
			    (!"".equals(tparm.getValue("END_TIME_N")))) {
				sqlStart += " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
								+ tparm.getValue("START_TIME_N")
								+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
								+ tparm.getValue("END_TIME_N")
								+ "','YYYY-MM-DD HH24:MI:SS') "
								+ " AND A.IVA_INTGMED_CHECK_USER IS NULL ";
		}
		if ((!"".equals(tparm.getValue("START_TIME_Y"))) && 
			    (!"".equals(tparm.getValue("END_TIME_Y")))) {
				sqlStart += " AND A.IVA_INTGMED_CHECK_TIME BETWEEN TO_DATE('"
								+ tparm.getValue("START_TIME_Y")
								+ "','YYYY-MM-DD HH24:MI:SS') AND TO_DATE('"
								+ tparm.getValue("END_TIME_Y")
								+ "','YYYY-MM-DD HH24:MI:SS') "
								+ " AND A.IVA_INTGMED_CHECK_USER IS NOT NULL ";
		}
		if (tparm.getValue("STATION_CODE") != null
				&& !"".equals(tparm.getValue("STATION_CODE"))) {
			  sqlStart += " AND A.STATION_CODE='" + tparm.getValue("STATION_CODE")
					  		+ "' ";
		}
		if(tparm.getValue("KIND") != null
			&& !"".equals(tparm.getValue("KIND"))){
			sqlStart += " AND A.DSPN_KIND='"+tparm.getValue("KIND")+"' ";
		}
  		String sqlEnd = " AND A.IVA_FLG='Y'  AND A.STATION_CODE=B.STATION_CODE "
  				+ " GROUP BY B.STATION_DESC, A.INTGMED_NO,A.IVA_INTGMED_CHECK_USER,"
  				+ " A.IVA_INTGMED_CHECK_TIME";
//		System.out.println("sql>>>>>>>>>>>>>"+sqlStart+sqlEnd);
		return new TParm(TJDODBTool.getInstance().select(sqlStart+sqlEnd));
	}

	/*
	 * 查询单据明细
	 */
	public TParm queryInfocheck(TParm tparm) {
		String sql = "SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,"
				+ " A.DOSAGE_QTY,B.UNIT_CHN_DESC "
				+ " FROM (SELECT ORDER_CODE,ORDER_DESC,DOSAGE_UNIT,SUM(DOSAGE_QTY)"
				+ " AS DOSAGE_QTY,SPECIFICATION "
				+ " FROM ODI_DSPNM WHERE INTGMED_NO='"+tparm.getValue("INTGMED_NO")
				+ "' GROUP BY ORDER_CODE,ORDER_DESC,DOSAGE_UNIT,SPECIFICATION) A,"
				+ "SYS_UNIT B WHERE A.DOSAGE_UNIT=B.UNIT_CODE";
//		System.out.println("sql======="+sql);
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/*
	 * 取消
	 */
//	public TParm queryIn(TParm tparm) {
//		String sql = "SELECT  'Y' AS SELECT_FLG,A.INTGMED_NO ,B.STATION_DESC,A.IVA_INTGMED_CHECK_USER,A.IVA_INTGMED_CHECK_TIME"
//				+ " FROM ODI_DSPNM A ,SYS_STATION B"
//				+ " WHERE A.IVA_FLG = 'Y' ";
//		if (tparm.getValue("STATION_CODE") != null
//				&& !"".equals(tparm.getValue("STATION_CODE"))) {
//			sql += " AND A.STATION_CODE='" + tparm.getValue("STATION_CODE")
//					+ "' ";
//		}
//		if (tparm.getValue("ORDER_DR_CODE") != null
//				&& !"".equals(tparm.getValue("ORDER_DR_CODE"))) {
//			sql += " AND A.ORDER_DR_CODE='" + tparm.getValue("ORDER_DR_CODE")
//					+ "' ";
//		}
//		if (!"".equals(tparm.getValue("START_TIME"))
//				&& !"".equals(tparm.getValue("END_TIME"))) {
//			sql += " AND IVA_INTGMED_CHECK_TIME BETWEEN TO_DATE('"
//					+ tparm.getValue("START_TIME")
//					+ "','YYYY-MM-DD HH24:MI:SS')" + " AND TO_DATE('"
//					+ tparm.getValue("END_TIME")
//					+ "','YYYY-MM-DD HH24:MI:SS') ";
//		}
//		if(tparm.getValue("KIND") != null
//				&& !"".equals(tparm.getValue("KIND_UD"))){
//			sql += " AND A.DSPN_KIND='"+tparm.getValue("KIND")+"' ";
//		}
//		sql += " AND A.IVA_INTGMED_CHECK_USER IS NOT NULL AND "
//				+ "A.STATION_CODE = B.STATION_CODE"
//				+ " GROUP BY 'Y',A.INTGMED_NO ,B.STATION_DESC,A.IVA_INTGMED_CHECK_USER,A.IVA_INTGMED_CHECK_TIME";
//		return new TParm(TJDODBTool.getInstance().select(sql));
//	}

	// 审核通过
	public TParm updateInfo(TParm tparm) {
		String sqlM = "UPDATE ODI_DSPNM SET IVA_INTGMED_CHECK_USER='"
				+ tparm.getValue("IVA_INTGMED_CHECK_USER") + "',"
				+ "IVA_INTGMED_CHECK_TIME=SYSDATE WHERE INTGMED_NO='"
				+ tparm.getValue("INTGMED_NO") + "'";
		TParm parmM = new TParm(TJDODBTool.getInstance().update(sqlM));
//		if(parmM.getErrCode() < 0){
//			return parmM;
//		}
//		String sqlD = "UPDATE ODI_DSPND SET IVA_INTGMED_CHECK_USER='"
//				+ tparm.getValue("IVA_INTGMED_CHECK_USER") + "',"
//				+ "IVA_INTGMED_CHECK_TIME=SYSDATE WHERE INTGMED_NO='"
//				+ tparm.getValue("INTGMED_NO") + "'";
//		TParm parmD = new TParm(TJDODBTool.getInstance().update(sqlD));
//		if(parmD.getErrCode() < 0){
//			return parmD;
//		}
		return parmM;
	}

}
