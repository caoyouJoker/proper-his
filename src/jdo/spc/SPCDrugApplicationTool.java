package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

public class SPCDrugApplicationTool extends TJDOTool {

	/**
	 * 实例
	 */
	private static SPCDrugApplicationTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatAdmTool
	 */
	public static SPCDrugApplicationTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCDrugApplicationTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCDrugApplicationTool() {
		onInit();
	}

	/**
	 * 查询汇总
	 * 
	 * @return
	 */
	public TParm queryOPDOrderM(TParm parm) {
		String rx_no = parm.getValue("RX_NO");
		String toxic_id = parm.getValue("TOXIC_ID");
		String ORDER_DATE_END = parm.getValue("ORDER_DATE_END");
		String RECLAIM_DATE_END = parm.getValue("RECLAIM_DATE_END");
		String RECLAIM_DATE_START = parm.getValue("RECLAIM_DATE_START");
		String ORDER_DATE_START = parm.getValue("ORDER_DATE_START");
		String flg = parm.getValue("REQUEST_FLG");
		String sqlAppend = "";
		if ("Y".equals(flg)) {
			sqlAppend += "AND A.REQUEST_NO IS NOT NULL ";
		} else {
			sqlAppend += "AND A.REQUEST_NO IS NULL ";
		}
		String sql = "SELECT 'Y' AS SEL,A.REQUEST_NO,A.ORDER_DESC,A.SPECIFICATION,A.DOSAGE_QTY, "
				+ "C.UNIT_CHN_DESC,B.STOCK_PRICE,B.STOCK_PRICE * A.DOSAGE_QTY AS STOCK_AMT,A.OWN_PRICE,A.OWN_AMT, "
				+ "SUM(DOSAGE_QTY) || C.UNIT_CHN_DESC AS DOSAGE_QTY,EXEC_DEPT_CODE,A.ORDER_CODE,TOXIC_ID "
				+ "FROM OPD_ORDER A,PHA_BASE B,SYS_UNIT C "
				+ "WHERE A.ORDER_CODE=B.ORDER_CODE "
				+ "AND A.DOSAGE_UNIT = C.UNIT_CODE "
				+ " AND A.IS_RECLAIM='Y' "
				+ sqlAppend;
		if (!"".equals(rx_no) && rx_no != null) {
			sql += "AND A.RX_NO ='" + rx_no + "' ";
		}
		if (!"".equals(toxic_id) && toxic_id != null) {
			sql += "AND TOXIC_ID='"+toxic_id+"' ";
		}
		if (!StringUtil.isNullString(RECLAIM_DATE_START)
				&& !StringUtil.isNullString(RECLAIM_DATE_END)) {
			sql += " AND A.RECLAIM_DATE BETWEEN TO_DATE('" + RECLAIM_DATE_START
					+ "','yyyy/mm/dd HH24:mi:ss') AND TO_DATE('"
					+ RECLAIM_DATE_END + "','yyyy/mm/dd HH24:mi:ss') ";
		}
		if (!StringUtil.isNullString(ORDER_DATE_START)
				&& !StringUtil.isNullString(ORDER_DATE_END)) {
			sql += " AND A.ORDER_DATE BETWEEN TO_DATE('" + ORDER_DATE_START
					+ "','yyyy/mm/dd HH24:mi:ss') AND TO_DATE('"
					+ ORDER_DATE_END + "','yyyy/mm/dd HH24:mi:ss') ";
		}
		sql += "GROUP BY 'Y',A.REQUEST_NO,A.ORDER_DESC,A.SPECIFICATION,A.DOSAGE_QTY, "
				+ "C.UNIT_CHN_DESC,B.STOCK_PRICE,A.DOSAGE_QTY,A.OWN_PRICE,A.OWN_AMT,"
				+ "EXEC_DEPT_CODE,A.ORDER_CODE,TOXIC_ID";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询明细
	 */
	public TParm queryOPDOrder(TParm parm) {
		String rx_no = parm.getValue("RX_NO");
		String toxic_id = parm.getValue("TOXIC_ID");
		String ORDER_DATE_END = parm.getValue("ORDER_DATE_END");
		String RECLAIM_DATE_END = parm.getValue("RECLAIM_DATE_END");
		String RECLAIM_DATE_START = parm.getValue("RECLAIM_DATE_START");
		String ORDER_DATE_START = parm.getValue("ORDER_DATE_START");
		String flg = parm.getValue("REQUEST_FLG");
		String sqlAppend = "";
		if ("Y".equals(flg)) {
			sqlAppend += "AND A.REQUEST_NO IS NOT NULL ";
		} else {
			sqlAppend += "AND A.REQUEST_NO IS NULL ";
		}
		String sql = "SELECT 'Y' SEL,RX_NO,MR_NO,PAT_NAME,ORDER_DESC, "
				+ " SPECIFICATION,D.FREQ_CHN_DESC,C.ROUTE_CHN_DESC,SUM(DOSAGE_QTY) || E.UNIT_CHN_DESC AS DOSAGE_QTY "
				+ ",TOXIC_ID,ORDER_CODE,EXEC_DEPT_CODE "
				+ " FROM OPD_ORDER A,SYS_OPERATOR B,SYS_PHAROUTE C,SYS_PHAFREQ D,SYS_UNIT E "
				+ " WHERE A.DR_CODE=B.USER_ID "
				+ " AND A.FREQ_CODE=D.FREQ_CODE "
				+ " AND A.ROUTE_CODE=C.ROUTE_CODE "
				+ " AND A.DOSAGE_UNIT=E.UNIT_CODE " + " AND A.IS_RECLAIM='Y' "
				+ sqlAppend;
		if (!"".equals(rx_no) && rx_no != null) {
			sql += "AND A.RX_NO ='" + rx_no + "' ";
		}
		if (!"".equals(toxic_id) && toxic_id != null) {
			sql += "AND TOXIC_ID='"+toxic_id+"' ";
		}
		if (!StringUtil.isNullString(RECLAIM_DATE_START)
				&& !StringUtil.isNullString(RECLAIM_DATE_END)) {
			sql += " AND A.RECLAIM_DATE BETWEEN TO_DATE('" + RECLAIM_DATE_START
					+ "','yyyy/mm/dd HH24:mi:ss') AND TO_DATE('"
					+ RECLAIM_DATE_END + "','yyyy/mm/dd HH24:mi:ss') ";
		}
		if (!StringUtil.isNullString(ORDER_DATE_START)
				&& !StringUtil.isNullString(ORDER_DATE_END)) {
			sql += " AND A.ORDER_DATE BETWEEN TO_DATE('" + ORDER_DATE_START
					+ "','yyyy/mm/dd HH24:mi:ss') AND TO_DATE('"
					+ ORDER_DATE_END + "','yyyy/mm/dd HH24:mi:ss') ";
		}
		sql += "GROUP BY 'Y',RX_NO,MR_NO,PAT_NAME,ORDER_DESC, "
				+ "SPECIFICATION,D.FREQ_CHN_DESC,C.ROUTE_CHN_DESC,TOXIC_ID,ORDER_CODE,"
				+ "EXEC_DEPT_CODE,UNIT_CHN_DESC";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

}
