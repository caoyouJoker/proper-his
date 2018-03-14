package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:麻精空瓶回收记录查询Tool
 * </p>
 * 
 * <p>
 * Description:麻精空瓶回收记录查询Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-09-26
 * @version 1.0
 */
public class SPCRecycleQueryTool extends TJDOTool {

	private static SPCRecycleQueryTool instanceObject;

	/**
	 * 构造函数
	 */
	public SPCRecycleQueryTool() {
		super();
		onInit();
	}

	/**
	 * 获取实例对象
	 * 
	 * @return
	 */
	public static SPCRecycleQueryTool getInstance() {
		if (instanceObject == null) {
			instanceObject = new SPCRecycleQueryTool();
		}
		return instanceObject;
	}

	/**
	 * 查询SPC_INV_RECORD(手术介入)
	 */
	public TParm querySpc(TParm parm) {
		String exe_dept_code = parm.getValue("EXE_DEPT_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		String reclaim_date = parm.getValue("RECLAIM_DATE");
		String con1 = "";
		if (!StringUtil.isNullString(exe_dept_code)) {
			con1 = "AND A.EXE_DEPT_CODE = '" + exe_dept_code + "' ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		String con3 = "";
		if (!StringUtil.isNullString(reclaim_date)) {
			con3 = "AND A.RECLAIM_DATE = TO_DATE('" + reclaim_date
					+ "','yyyy-mm-dd HH24:mi:ss') ";
		}
		String sql = "SELECT A.ORDER_DESC,B.SPECIFICATION,A.BAR_CODE,C.PAT_NAME,A.EXE_DEPT_CODE, "
				+ "D.BED_NO,A.MR_NO,A.RECLAIM_USER,A.RECLAIM_DATE,A.ORDER_CODE "
				+ "FROM SPC_INV_RECORD A,SYS_FEE B,SYS_PATINFO C,ADM_INP D "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.MR_NO = D.MR_NO "
				+ "AND A.CASE_NO = D.CASE_NO " + con1 + con2 + con3;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询OPD_ORDER(急诊)
	 */
	public TParm queryOpd(TParm parm) {
		String order_code = parm.getValue("ORDER_CODE");
		String reclaim_date = parm.getValue("RECLAIM_DATE");
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		String con3 = "";
		if (!StringUtil.isNullString(reclaim_date)) {
			con3 = "AND A.RECLAIM_DATE = TO_DATE('" + reclaim_date
					+ "','yyyy-mm-dd HH24:mi:ss') ";
		}
		String sql = "SELECT A.ORDER_DESC,B.SPECIFICATION,A.TOXIC_ID1 AS BAR_CODE,C.PAT_NAME,'急诊区' EXE_DEPT_CODE, "
				+ "D.BED_NO,A.MR_NO,A.RECLAIM_USER,A.RECLAIM_DATE,A.ORDER_CODE "
				+ "FROM OPD_ORDER A,SYS_FEE B,SYS_PATINFO C,ADM_INP D "
				+ "WHERE A.ADM_TYPE='E' "
				+ "AND A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.MR_NO = D.MR_NO "
				+ "AND A.CASE_NO = D.CASE_NO " + con2 + con3;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询IND_CABDSPN(病区)
	 */
	public TParm queryInd(TParm parm) {
		String station_code = parm.getValue("EXE_DEPT_CODE");
		String order_code = parm.getValue("ORDER_CODE");
		String reclaim_date = parm.getValue("RECLAIM_DATE");
		String con1 = "";
		if (!StringUtil.isNullString(station_code)) {
			con1 = "AND A.STATION_CODE = '" + station_code + "' ";
		}
		String con2 = "";
		if (!StringUtil.isNullString(order_code)) {
			con2 = "AND A.ORDER_CODE = '" + order_code + "' ";
		}
		String con3 = "";
		if (!StringUtil.isNullString(reclaim_date)) {
			con3 = "AND A.RECLAIM_DATE = TO_DATE('" + reclaim_date
					+ "','yyyy-mm-dd HH24:mi:ss') ";
		}
		String sql = "SELECT A.ORDER_DESC,B.SPECIFICATION,A.TOXIC_ID1 AS BAR_CODE,C.PAT_NAME,A.STATION_CODE AS EXE_DEPT_CODE, "
				+ "D.BED_NO,A.MR_NO,A.RECLAIM_USER,A.RECLAIM_DATE,A.ORDER_CODE "
				+ "FROM IND_CABDSPN A,SYS_FEE B,SYS_PATINFO C,ADM_INP D "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.MR_NO = C.MR_NO "
				+ "AND A.MR_NO = D.MR_NO "
				+ "AND A.CASE_NO = D.CASE_NO " + con1 + con2 + con3;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询前判断1
	 * 
	 * @return
	 */
	public TParm queryOrg(String org_code) {
		String sql = "SELECT A.STATION_FLG,A.EXINV_FLG " + "FROM IND_ORG A "
				+ "WHERE A.ORG_CODE='" + org_code + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询前判断2
	 * 
	 * @return
	 */
	public TParm queryDept(String dept_code) {
		String sql = "SELECT A.EMG_FIT_FLG " + "FROM SYS_DEPT A "
				+ "WHERE A.DEPT_CODE='" + dept_code + "'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
