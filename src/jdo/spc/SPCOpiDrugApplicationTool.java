package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCOpiDrugApplicationTool extends TJDOTool {

	/**
	 * 实例
	 */
	private static SPCOpiDrugApplicationTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return PatAdmTool
	 */
	public static SPCOpiDrugApplicationTool getInstance() {
		if (instanceObject == null)
			instanceObject = new SPCOpiDrugApplicationTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	public SPCOpiDrugApplicationTool() {
		onInit();
	}

	/**
	 * 查询汇总
	 * 
	 * @return
	 */
	public TParm querySpcInvRecordM(TParm parm) {
		String app_org_code = parm.getValue("APP_ORG_CODE");
//		String to_org_code = parm.getValue("TO_ORG_CODE");
		String request_no = parm.getValue("REQUEST_NO");
		String start_date = parm.getValue("START_DATE");
		String end_date = parm.getValue("END_DATE");
		String flg = parm.getValue("REQUEST_FLG");
		String sqlAppend = "";
		if ("Y".equals(flg)) {
			sqlAppend += "AND A.REQUEST_NO IS NOT NULL ";
		} else {
			sqlAppend += "AND A.REQUEST_NO IS NULL ";
		}
		// String sql =
		// "SELECT 'Y' AS SELECT_FLG,A.REQUEST_NO,A.ORDER_DESC,C.SPECIFICATION,SUM(A.QTY) AS DOSAGE_QTY, "
		// +
		// "D.UNIT_CHN_DESC,C.STOCK_PRICE,C.STOCK_PRICE * A.QTY AS STOCK_AMT, "
		// +
		// "A.OWN_PRICE,A.OWN_PRICE * A.QTY AS OWN_AMT,SUM(G.STOCK_QTY) AS QTY,"
		// + "H.SAFE_QTY,H.SAFE_QTY-SUM(G.STOCK_QTY) AS SUP_QTY,B.ORDER_CODE "
		// + "FROM SPC_INV_RECORD A,SYS_FEE B,PHA_BASE C,SYS_UNIT D,IND_ORG E, "
		// + "IND_STOCK G,IND_STOCKM H "
		// + "WHERE A.CLASS_CODE='4' "
		// + sqlAppend;
		String sql = "SELECT 'Y' AS SELECT_FLG,A.REQUEST_NO,A.ORDER_DESC,C.SPECIFICATION,COUNT (A.QTY) AS DOSAGE_QTY, "
				+ "D.UNIT_CHN_DESC,C.STOCK_PRICE,C.STOCK_PRICE * A.QTY AS STOCK_AMT,A.OWN_PRICE, "
				+ "A.OWN_PRICE * A.QTY AS OWN_AMT,A.ORDER_CODE, F.SAFE_QTY, "
				+ "(SELECT SUM(AA.STOCK_QTY) FROM IND_STOCK AA  "
				+ "WHERE AA.ORDER_CODE=A.ORDER_CODE  "
				+ "AND AA.ORG_CODE='040103'  "
				+ "AND AA.STOCK_QTY > 0 AND AA.ACTIVE_FLG='Y' "
				+ "AND AA.VALID_DATE > SYSDATE "
				+ "GROUP BY AA.ORDER_CODE "
				+ ") AS QTY, "
				+ "(SELECT F.SAFE_QTY- SUM(AA.STOCK_QTY) FROM IND_STOCK AA  "
				+ "WHERE AA.ORDER_CODE=A.ORDER_CODE  "
				+ "AND AA.ORG_CODE='040103'  "
				+ "AND AA.STOCK_QTY > 0 AND AA.ACTIVE_FLG='Y' "
				+ "AND AA.VALID_DATE > SYSDATE "
				+ "GROUP BY AA.ORDER_CODE "
				+ ") AS SUP_QTY "
				+ "FROM SPC_INV_RECORD A,SYS_FEE B,PHA_BASE C,SYS_UNIT D, "
				+ "IND_ORG E,IND_STOCKM F "
				+ "WHERE A.CLASS_CODE='4' "
				+ sqlAppend;
		if (!"".equals(request_no) && request_no != null) {
			sql += "AND A.REQUEST_NO ='" + request_no + "' ";
		}
		sql += "AND A.BILL_DATE BETWEEN TO_DATE('" + start_date
				+ "','yyyy/mm/dd HH24:mi:ss') AND TO_DATE('" + end_date
				+ "','yyyy/mm/dd HH24:mi:ss') ";
		if (!"".equals(app_org_code) && app_org_code != null) {
			sql += "AND A.EXE_DEPT_CODE='" + app_org_code + "' ";
		}
		sql += "AND B.CTRL_FLG='"
				+ parm.getData("CTRL_FLG")
				+ "' "
				+ "AND A.ORDER_CODE=B.ORDER_CODE "
				+ "AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.UNIT_CODE=D.UNIT_CODE "
				+ "AND A.EXE_DEPT_CODE=E.ORG_CODE "
				// if (!"".equals(to_org_code) && to_org_code != null) {
				// sql += "AND E.SUP_ORG_CODE='" + to_org_code + "' ";
				// }
				+ "AND E.SUP_ORG_CODE=F.ORG_CODE "
				+ "AND A.ORDER_CODE=F.ORDER_CODE "
				+ "GROUP BY 'Y',A.REQUEST_NO,A.ORDER_DESC,C.SPECIFICATION,D.UNIT_CHN_DESC, "
				+ "C.STOCK_PRICE,A.OWN_PRICE,A.QTY,A.ORDER_CODE,F.SAFE_QTY ";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 查询明细
	 */
	public TParm querySpcInvRecord(TParm parm) {
		String app_org_code = parm.getValue("APP_ORG_CODE");
		String to_org_code = parm.getValue("TO_ORG_CODE");
		String request_no = parm.getValue("REQUEST_NO");
		String start_date = parm.getValue("START_DATE");
		String end_date = parm.getValue("END_DATE");
		String flg = parm.getValue("REQUEST_FLG");
		String sqlAppend = "";
		if ("Y".equals(flg)) {
			sqlAppend += "AND A.REQUEST_NO IS NOT NULL ";
		} else {
			sqlAppend += "AND A.REQUEST_NO IS NULL ";
		}
		String sql = "SELECT 'Y' AS SELECT_FLG,B.PAT_NAME,A.BILL_DATE,A.ORDER_DESC,C.SPECIFICATION, "
				+ "A.QTY AS DOSAGE_QTY,D.UNIT_CHN_DESC,C.STOCK_PRICE,C.STOCK_PRICE * A.QTY AS STOCK_AMT, "
				+ "A.OWN_PRICE,A.OWN_PRICE * A.QTY AS OWN_AMT "
				+ "FROM SPC_INV_RECORD A,SYS_PATINFO B,PHA_BASE C,SYS_UNIT D,SYS_FEE E,IND_ORG F "
				+ "WHERE A.CLASS_CODE='4' " + sqlAppend;
		if (!"".equals(request_no) && request_no != null) {
			sql += "AND A.REQUEST_NO ='" + request_no + "' ";
		}
		sql += "AND A.BILL_DATE BETWEEN TO_DATE('" + start_date
				+ "','yyyy/mm/dd HH24:mi:ss') AND TO_DATE('" + end_date
				+ "','yyyy/mm/dd HH24:mi:ss') ";
		if (!"".equals(app_org_code) && app_org_code != null) {
			sql += "AND A.EXE_DEPT_CODE='" + app_org_code + "' ";
		}
		sql += "AND A.MR_NO=B.MR_NO " + "AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.UNIT_CODE=D.UNIT_CODE " + "AND E.CTRL_FLG='"
				+ parm.getData("CTRL_FLG") + "' "
				+ "AND A.ORDER_CODE=E.ORDER_CODE "
				+ "AND A.EXE_DEPT_CODE=F.ORG_CODE ";
		if (!"".equals(to_org_code) && to_org_code != null) {
			sql += "AND F.SUP_ORG_CODE='" + to_org_code + "' ";
		}
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
}
