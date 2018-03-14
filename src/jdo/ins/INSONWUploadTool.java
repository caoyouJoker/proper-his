package jdo.ins;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

/**
 * 
 * <p>
 * Title: 门诊日门诊量上传
 * </p>
 * 
 * <p>
 * Description:门诊日门诊量上传
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) xueyf
 * </p>
 * 
 * <p>
 * Company:Javahis
 * </p>
 * 
 * @author xueyf 2011.12.30
 * @version 1.0
 */
public class INSONWUploadTool extends TJDODBTool {

	/**
	 * 实例
	 */
	public static INSONWUploadTool instanceObject;

	/**
	 * 得到实例
	 * 
	 * @return INSNoticeTool
	 */
	public static INSONWUploadTool getInstance() {
		if (instanceObject == null)
			instanceObject = new INSONWUploadTool();
		return instanceObject;
	}

	/**
	 * 构造器
	 */
	// public INSONWUploadTool() {
	// setModuleName("ins\\INSONWUploadModule.x");
	// onInit();
	// }

	/**
	 * 取门诊医保联网caseNo
	 * 
	 * @param selectCaseNoTparm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectCaseNoTparm(TParm queryTParm) {
		return null;
	}

	/**
	 * 取医保非门诊联网信息 no
	 * 
	 * @param selectCaseNoTparm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectInsNoOut(TParm queryTParm) {
		String SQL = "SELECT SUM (a.charge01 + a.charge02 + a.charge03) tTextField_NO2, SUM (a.charge04) tTextField_NO3,"
				+ "       SUM (a.charge06) tTextField_NO4,"
				+ "       SUM (  a.charge05"
				+ "            + a.charge07"
				+ "            + a.charge08"
				+ "            + a.charge09"
				+ "            + a.charge10"
				+ "            + a.charge11"
				+ "            + a.charge12"
				+ "            + a.charge13"
				+ "            + a.charge14"
				+ "            + a.charge15"
				+ "            + a.charge16"
				+ "            + a.charge17"
				+ "            + a.charge18"
				+ "            + a.charge19"
				+ "           ) tTextField_NO5"
				+ "  FROM bil_opb_recp a, reg_patadm b, bil_reg_recp d, sys_ctz e"
				+ " WHERE  "// --b.REGION_CODE = ' 'AND"
				+ "       b.regcan_user IS NULL"
				+ "   AND (b.ctz2_code IS NULL OR b.ctz2_code != '89')"
				+ "   AND b.adm_type = 'O'"
				+ "   AND d.region_code = b.region_code"
				+ "   AND a.region_code = b.region_code"
				+ "   AND b.region_code ='"
				+ queryTParm.getValue("REGION_CODE")
				+ "'"
				+ "   AND d.case_no = b.case_no and  B.ADM_DATE =TO_DATE('"
				+ queryTParm.getValue("STATISTICS_DATE")
				+ "','YYYY/MM/DD')"

				// "   -- AND a.CANCEL_FLG = 'N'"
				// "    --AND d.offreceipt_no IS NULL"
				// "    --AND a.REGION_CODE = d.REGION_CODE"
				+ "   AND a.receipt_no = d.receipt_no"
				// "   -- AND e.hosp_area = b.REGION_CODE"
				+ "   AND e.ctz_code = b.ctz1_code"
				+ "   AND e.nhi_ctz_flg = 'Y'";
		//System.out.println(SQL);
		TParm result = new TParm(select(SQL));

		return result;
	}

	/**
	 * 取医保非门诊联网挂号人次(人数)
	 * 
	 * @param queryTParm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectInsNoOutCount(TParm queryTParm) {
		// TParm tparm=query("selectInsNoOutCount", queryTParm);
		String SQL = "SELECT COUNT(CASE_NO) tTextField_NO1 FROM  REG_PATADM B, SYS_CTZ E  WHERE B.REGION_CODE ='"
				+ queryTParm.getValue("REGION_CODE")
				+ "' AND B.ADM_DATE =TO_DATE('"
				+ queryTParm.getValue("STATISTICS_DATE")
				+ "','YYYY/MM/DD') AND B.REGCAN_USER IS NULL  AND (B.CTZ2_CODE IS NULL OR B.CTZ2_CODE != '89')  AND B.ADM_TYPE = 'O'  AND E.CTZ_CODE = B.CTZ1_CODE AND E.NHI_CTZ_FLG = 'Y'";
		//System.out.println(SQL);
		TParm result = new TParm(select(SQL));
		return result;
	}

	/**
	 * 取日门诊量医保门诊联网信息
	 * 
	 * @param selectCaseNoTparm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectInsOut(TParm queryTParm) {
		String SQL = "SELECT   SUM (b.charge01 + b.charge02 + b.charge03) TTEXTFIELD_NO12,"
				+ "         SUM (b.charge04) TTEXTFIELD_NO13,"
				+ "       SUM (b.charge06) TTEXTFIELD_NO14,"
				+ "       SUM (  b.charge05"
				+ "            + b.charge07"
				+ "            + b.charge08"
				+ "            + b.charge09"
				+ "            + b.charge10"
				+ "            + b.charge11"
				+ "            + b.charge12"
				+ "            + b.charge13"
				+ "            + b.charge14"
				+ "            + b.charge15"
				+ "            + b.charge16"
				+ "            + b.charge17"
				+ "            + b.charge18"
				+ "            + b.charge19"
				+ "           ) TTEXTFIELD_NO15"
				+ "    FROM BIL_REG_RECP A, BIL_OPB_RECP  B WHERE B.region_code = '"
				+ queryTParm.getValue("REGION_CODE")
				+ "' AND B.CASE_NO IN ("
				+ "SELECT DISTINCT A.CASE_NO From REG_PATADM A, INS_OPD B Where A.ADM_DATE = TO_DATE("
				+ queryTParm.getValue("STATISTICS_DATE")
				+ ",'YYYY/MM/DD')		And A.REGCAN_USER IS NULL And A.CASE_NO = B.CASE_NO And B.INSAMT_FLG IN ('3','5') and A.region_code = B.region_code and A.region_code ='"
				+ queryTParm.getValue("REGION_CODE")
				+ "'"
				+ ") "
				// -- AND B.REFUND_FLG = 'N' AND B.OFFRECEIPT_NO IS NULL
				+ "   AND A.region_code = B.region_code  AND A.RECEIPT_NO = B.RECEIPT_NO ";

		//System.out.println(SQL);
		TParm result = new TParm(select(SQL));
		return result;
	}

	/**
	 * 取日门诊量医保门诊联网部分(人数)
	 * 
	 * @param selectCaseNoTparm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectInsOutCount(TParm queryTParm) {

		String SQL = " SELECT COUNT(DISTINCT B.CASE_NO) TTEXTFIELD_NO11 FROM  INS_OPD A, REG_PATADM B  WHERE  A.region_code = '"
				+ queryTParm.getValue("REGION_CODE")
				+ "'  AND  B.ADM_DATE = TO_DATE('"
				+ queryTParm.getValue("STATISTICS_DATE")
				+ "','YYYY/MM/DD')  AND  A.INSAMT_FLG IN ('3','5') AND  B.REGCAN_USER IS NULL  AND  A.region_code = B.region_code  AND  A.CASE_NO = B.CASE_NO ";
		//System.out.println(SQL);
		TParm result = new TParm(select(SQL));
		return result;
	}

	/**
	 * 取非医保门诊信息
	 * 
	 * @param selectCaseNoTparm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectOutNoIns(TParm queryTParm) {
		String SQL = "SELECT   SUM (d.charge01 + d.charge02 + d.charge03) ttextfield_no7,"
				+ "         SUM (d.charge04) ttextfield_no8,"
				+ "       SUM (d.charge06) tTextField_NO9,"
				+ "       SUM (  d.charge05"
				+ "            + d.charge07"
				+ "            + d.charge08"
				+ "            + d.charge09"
				+ "            + d.charge10"
				+ "            + d.charge11"
				+ "            + d.charge12"
				+ "            + d.charge13"
				+ "            + d.charge14"
				+ "            + d.charge15"
				+ "            + d.charge16"
				+ "            + d.charge17"
				+ "            + d.charge18"
				+ "            + d.charge19"
				+ "           ) tTextField_NO10"
				+ "    FROM BIL_REG_RECP a, reg_patadm b, BIL_OPB_RECP d, sys_ctz e"
				+ "   WHERE b.region_code = ''"
				+ "     AND b.adm_date = TO_DATE ('"
				+ queryTParm.getValue("STATISTICS_DATE")
				+ "', 'YYYY/MM/DD')"
				+ "     AND b.regcan_user IS NULL"
				+ "     AND (b.ctz2_code IS NULL OR b.ctz2_code != '89')"
				+ "     AND b.adm_type = 'O'"
				+ "     AND d.region_code = b.region_code"
				+ "     AND d.case_no = b.case_no"
				+ "   AND b.region_code ='"
				+ queryTParm.getValue("REGION_CODE")
				+ "'"
				+
				// -- AND d.refund_flg = 'N'"+
				// -- AND d.offreceipt_no IS NULL"+
				"     AND a.region_code = d.region_code"
				+ "     AND a.receipt_no = d.receipt_no"
				+
				// -- AND e.hosp_area = b.hosp_area"+
				"     AND e.ctz_code = b.ctz1_code"
				+ "     AND e.nhi_ctz_flg = 'N'";
		//System.out.println(SQL);
		TParm result = new TParm(select(SQL));
		return result;
	}

	/**
	 * 非医保门诊挂号人次
	 * 
	 * @param selectCaseNoTparm
	 *            TParm 查询条件
	 * @return TParm
	 */
	public TParm selectOutNoInsCount(TParm queryTParm) {
		String SQL = " SELECT COUNT(CASE_NO) tTextField_NO6 FROM  REG_PATADM B, SYS_CTZ E  WHERE B.REGION_CODE = '"
				+ queryTParm.getValue("REGION_CODE")
				+ "' AND B.ADM_DATE = TO_DATE('"
				+ queryTParm.getValue("STATISTICS_DATE")
				+ "','YYYY/MM/DD') AND B.REGCAN_USER IS NULL  AND (B.CTZ2_CODE IS NULL OR B.CTZ2_CODE != '89')  AND B.ADM_TYPE = 'O'  AND  E.CTZ_CODE = B.CTZ1_CODE AND E.NHI_CTZ_FLG = 'N'";
		//System.out.println(SQL);
		TParm result = new TParm(select(SQL));
		return result;
	}
}
