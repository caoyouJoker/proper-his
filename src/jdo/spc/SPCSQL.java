package jdo.spc;

import jdo.pha.inf.client.SpcOpdDiagrecDto;
import jdo.pha.inf.client.SpcOpdDrugAllergyDto;
import jdo.pha.inf.client.SpcOpdOrderDto;
import jdo.pha.inf.client.SpcRegPatadmDto;
import jdo.pha.inf.client.SpcSysPatinfoDto;
import jdo.spc.inf.dto.SpcCommonDto;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

/**
 * <p>
 * Title: SPC共用SQL封装
 * </p>
 * 
 * <p>
 * Description: SPC共用SQL封装
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
 * @author fuwj 2012.10.23
 * @version 1.0
 */

public class SPCSQL {

	/**
	 * 取得智能柜信息
	 * 
	 * @return
	 */
	public static String getCabinet() {
		return "SELECT CABINET_ID,CABINET_DESC,CABINET_IP,ORG_CODE,OPT_USER,OPT_DATE,OPT_TERM "
				+ "FROM IND_CABINET";
	}

	/**
	 * 取得智能柜信息
	 * 
	 * @return
	 */
	public static String getCabinetByOrgCode(String orgCode) {
		return "SELECT CABINET_ID,CABINET_DESC,CABINET_IP,ORG_CODE,OPT_USER,OPT_DATE,OPT_TERM "
				+ "FROM IND_CABINET WHERE ORG_CODE='" + orgCode + "' ";
	}

	/**
	 * 取得容器信息
	 * 
	 * @return
	 */
	public static String getContainer() {
		return "SELECT A.CONTAINER_ID,A.CONTAINER_DESC,A.RFID_ID,A.ORDER_CODE,B.ORDER_DESC,A.TOXIC_QTY FROM IND_CONTAINERM A,"
				+ "SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE";
	}

	/**
	 * 查询门禁
	 * 
	 * @return
	 */
	public static String getCabinetGuard(String cabinetId) {
		if ("".equals(cabinetId)) {
			return "";
		}
		return "SELECT GUARD_ID,GUARD_DESC,IS_TOXIC_GUARD,OPT_USER,OPT_DATE,OPT_TERM,CABINET_ID "
				+ "FROM IND_CABINET_GUARD WHERE CABINET_ID='"
				+ cabinetId
				+ "' ORDER BY GUARD_ID";
	}

	/**
	 * 修改移货申请M表状态-审核
	 * 
	 * @return
	 * @date 20121029
	 * @author liyh
	 */
	public static String upDateStatusINDPourOrderM(TParm parm) {
		return " UPDATE IND_PURORDERM SET CHECK_DATE=SYSDATE,CHECK_USER='"
				+ parm.getValue("OPT_USER") + "', " + " OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE,OPT_USER='"
				+ parm.getValue("OPT_USER") + "' " + " WHERE PURORDER_NO='"
				+ parm.getValue("PURORDER_NO") + "' ";
	}

	/**
	 * 修改移货申请D表状态-审核
	 * 
	 * @return
	 * @date 20121029
	 * @author liyh
	 */
	public static String upDateStatusINDPourOrderD(TParm parm) {
		return " UPDATE IND_PURORDERD SET UPDATE_FLG='3' ," + " OPT_TERM='"
				+ parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE,OPT_USER='"
				+ parm.getValue("OPT_USER") + "' " + " WHERE PURORDER_NO='"
				+ parm.getValue("PURORDER_NO") + "' ";
	}

	/**
	 * 保存申请主档-麻精入库时
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String saveRequestMAutoOfDrug(TParm parm) {
		String sql = " INSERT INTO IND_REQUESTM(REQUEST_NO, REQTYPE_CODE, APP_ORG_CODE, TO_ORG_CODE, REQUEST_DATE, REQUEST_USER, REASON_CHN_DESC, "
				+ " UNIT_TYPE, URGENT_FLG, OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE,DRUG_CATEGORY,APPLY_TYPE) "
				+ " VALUES(" + " '"
				+ parm.getValue("REQUEST_NO")
				+ "', '"
				+ parm.getValue("REQTYPE_CODE")
				+ "', '"
				+ parm.getValue("APP_ORG_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("TO_ORG_CODE")
				+ "',sysdate,'"
				+ parm.getValue("REQUEST_USER")
				+ "','"
				+ parm.getValue("REASON_CHN_DESC")
				+ "',"
				+ " '"
				+ parm.getValue("UNIT_TYPE")
				+ "', '"
				+ parm.getValue("URGENT_FLG")
				+ "', '"
				+ parm.getValue("OPT_USER")
				+ "', sysdate,"
				+ " '"
				+ parm.getValue("OPT_TERM")
				+ "','"
				+ parm.getValue("REGION_CODE") + "','2','3' " + " ) ";
		return sql;
	}

	/**
	 * 保存申请明细档-麻精入库时
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String saveRequestDAutoOfDrug(TParm parm) {
		String sql = " INSERT INTO IND_REQUESTD(REQUEST_NO, SEQ_NO, ORDER_CODE, QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, UPDATE_FLG, "
				+ "  OPT_USER, OPT_DATE, OPT_TERM, VERIFYIN_PRICE, BATCH_SEQ)  "
				+ " VALUES(" + " '"
				+ parm.getValue("REQUEST_NO")
				+ "', "
				+ parm.getValue("SEQ_NO")
				+ ", '"
				+ parm.getValue("ORDER_CODE")
				+ "',"
				+ "  "
				+ parm.getValue("QTY")
				+ ",'"
				+ parm.getValue("UNIT_CODE")
				+ "',"
				+ parm.getValue("RETAIL_PRICE")
				+ ","
				+ "  "
				+ parm.getValue("STOCK_PRICE")
				+ ","
				+ parm.getValue("QTY")
				+ ", '"
				+ parm.getValue("UPDATE_FLG")
				+ "',"
				+ " '"
				+ parm.getValue("OPT_USER")
				+ "', sysdate,'"
				+ parm.getValue("OPT_TERM") + "',0,0 " + " ) ";
		return sql;
	}

	/**
	 * 自动生成出库单时修改药库主档数据-麻精入库时
	 * 
	 * @param parm
	 * @return
	 */
	public static String updateStockByRequest(TParm parm) {
		String sql = "UPDATE IND_STOCK SET STOCK_QTY=STOCK_QTY-"
				+ parm.getValue("STOCK_QTY") + ", OUT_QTY=OUT_QTY+"
				+ parm.getValue("STOCK_QTY") + ",OUT_AMT=OUT_AMT+"
				+ parm.getValue("STOCK_AMT") + " ,STOCKOUT_QTY=STOCKOUT_QTY+"
				+ parm.getValue("STOCK_QTY") + ",STOCKOUT_AMT=STOCKOUT_AMT+"
				+ parm.getValue("STOCK_AMT")
				+ ",REQUEST_OUT_QTY=REQUEST_OUT_QTY+"
				+ parm.getValue("STOCK_QTY")
				+ ",REQUEST_OUT_AMT=REQUEST_OUT_AMT+"
				+ parm.getValue("STOCK_AMT") + " WHERE ORG_CODE='"
				+ parm.getValue("ORG_CODE") + "' AND ORDER_CODE='"
				+ parm.getValue("ORDER_CODE") + "' AND BATCH_SEQ="
				+ parm.getValue("BATCH_SEQ") + " ";
		return sql;
	}

	/**
	 * 自动生成出库单M-麻精入库时
	 * 
	 * @param parm
	 * @return
	 */
	public static String saveDispsenseMAutoOfDrug(TParm parm) {
		String sql = "INSERT INTO IND_DISPENSEM (DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, TO_ORG_CODE, URGENT_FLG, "
				+ " DISPENSE_DATE, DISPENSE_USER, UNIT_TYPE, UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE, DRUG_CATEGORY) "
				+ " VALUES " + " ('"
				+ parm.getValue("DISPENSE_NO")
				+ "', '"
				+ parm.getValue("REQTYPE_CODE")
				+ "', '"
				+ parm.getValue("REQUEST_NO")
				+ "', SYSDATE, "
				+ " '"
				+ parm.getValue("APP_ORG_CODE")
				+ "','"
				+ parm.getValue("TO_ORG_CODE")
				+ "', '"
				+ parm.getValue("URGENT_FLG")
				+ "',"
				+ " SYSDATE, "
				+ " '"
				+ parm.getValue("OPT_USER")
				+ "', '"
				+ parm.getValue("URGENT_FLG")
				+ "', '3', '"
				+ parm.getValue("OPT_USER")
				+ "',"
				+ " SYSDATE, '"
				+ parm.getValue("OPT_TERM")
				+ "', '"
				+ parm.getValue("REGION_CODE") + "', '2') ";
		return sql;
	}

	/**
	 * 自动生成出库单D-麻精入库时
	 * 
	 * @param parm
	 * @return
	 */
	public static String saveDispsenseDAutoOfDrug(TParm parm) {
		String sql = "INSERT INTO IND_DISPENSED (DISPENSE_NO, SEQ_NO, REQUEST_SEQ, ORDER_CODE, BATCH_SEQ, BATCH_NO, VALID_DATE, QTY, UNIT_CODE, "
				+ " RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, PHA_TYPE, OPT_USER, OPT_DATE, OPT_TERM, VERIFYIN_PRICE, IS_BOXED, IS_PUTAWAY,SUP_CODE,INVENT_PRICE,SUP_ORDER_CODE) "
				+ " VALUES " + " ('"
				+ parm.getValue("DISPENSE_NO")
				+ "', "
				+ parm.getValue("SEQ_NO")
				+ ", "
				+ parm.getValue("REQUEST_SEQ")
				+ ", "
				+ " '"
				+ parm.getValue("ORDER_CODE")
				+ "',"
				+ parm.getValue("BATCH_SEQ")
				+ ", '"
				+ parm.getValue("BATCH_NO")
				+ "',"
				+ " TO_DATE('"
				+ parm.getValue("VALID_DATE").substring(0, 10)
				+ "','yyyy-MM-dd'), "
				+ parm.getValue("QTY")
				+ ", '"
				+ parm.getValue("UNIT_CODE")
				+ "', "
				+ parm.getValue("RETAIL_PRICE")
				+ ", "
				+ " "
				+ parm.getValue("RETAIL_PRICE")
				+ ","
				+ parm.getValue("QTY")
				+ ", 'W', '"
				+ parm.getValue("OPT_USER")
				+ "', SYSDATE,"
				+ " '"
				+ parm.getValue("OPT_TERM")
				+ "', "
				+ parm.getValue("VERIFYIN_PRICE")
				+ ", 'N', 'N', '"
				+ parm.getValue("SUP_CODE")
				+ "','"
				+ parm.getValue("INVENT_PRICE") 
				+ "','" 
				+ parm.getValue("SUP_ORDER_CODE")
				+
						"')";
		System.out.println(sql+"");
		return sql;
	}

	/**
	 * 得到IND_STOCK信息
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_seq
	 * @return
	 */
	public static String getIndStock(String org_code, String order_code,
			String batch_seq) {
		return "SELECT BATCH_NO,VALID_DATE,BATCH_SEQ, RETAIL_PRICE , VERIFYIN_PRICE AS STOCK_PRICE,INVENT_PRICE,SUP_CODE,SUP_ORDER_CODE "
				+ " FROM IND_STOCK   WHERE ORG_CODE = '"
				+ org_code
				+ "' AND ORDER_CODE = '"
				+ order_code
				+ "' AND BATCH_SEQ="
				+ batch_seq + " AND ACTIVE_FLG = 'Y' ";
	}

	/**
     * 得到IND_STOCK信息
     * 
     * @param org_code
     * @param order_code
     * @param batch_seq
     * @return
     */
    public static String getSpcStock(String org_code, String order_code,
            String batch_seq) {//wanglong add 20150202
        return "SELECT BATCH_NO,VALID_DATE,BATCH_SEQ, RETAIL_PRICE , VERIFYIN_PRICE AS STOCK_PRICE,INVENT_PRICE,SUP_CODE,SUP_ORDER_CODE "
                + " FROM SPC_STOCK   WHERE ORG_CODE = '"
                + org_code
                + "' AND ORDER_CODE = '"
                + order_code
                + "' AND BATCH_SEQ="
                + batch_seq + " AND ACTIVE_FLG = 'Y' ";
    }
    
	/**
	 * 查询药库解锁部门
	 * 
	 * @param batch_flg
	 * @return String
	 */
	public static String getOrgDescByOrgCode(String orgCode) {
		return "SELECT ORG_CODE,ORG_CHN_DESC,BATCH_FLG,OPT_USER,OPT_DATE"
				+ ",OPT_TERM FROM IND_ORG WHERE ORG_CODE = '" + orgCode
				+ "' ORDER BY SEQ,ORG_CODE";
	}

	/**
	 * 根据配药单号查询普药非静配非口服的药品-未统药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String getStaOrderCodeNormalNoRebinedNoActIng(String intgmedNo) {
		// return
		// " SELECT  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,SUM(A.DISPENSE_QTY)AS SUM_QTY,A.STATION_CODE,B.STATION_DESC, "
		// +
		// " A.EXEC_DEPT_CODE,A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
		// + " FROM ODI_DSPNM A,SYS_STATION B,IND_STOCKM C ,SYS_FEE D "
		// + " WHERE A.INTGMED_NO='" + intgmedNo +
		// "' AND A.DSPN_KIND='UD'  AND A.ORDER_CAT1_CODE='PHA_W' "
		// +
		// " AND A.LINK_NO IS NULL  AND A.IS_INTG='N' AND A.STATION_CODE=B.STATION_CODE "
		// +
		// " AND A.ORDER_CODE=D.ORDER_CODE AND (D.ATC_FLG_I='N' OR D.ATC_FLG_I IS NULL)    AND A.EXEC_DEPT_CODE=C.ORG_CODE AND A.ORDER_CODE=C.ORDER_CODE "
		// +
		// " GROUP  BY  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION ,A.DISPENSE_UNIT,A.STATION_CODE,B.STATION_DESC,A.EXEC_DEPT_CODE,"
		// +
		// " A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
		// +
		// " ORDER BY A.ORDER_CODE,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
		// ;

		return " SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,SUM(A.DISPENSE_QTY)AS SUM_QTY,A.STATION_CODE,B.STATION_DESC, "
				+ " A.EXEC_DEPT_CODE,A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
				+ " FROM ODI_DSPNM A,SYS_STATION B,IND_STOCKM C ,SYS_FEE D "
				+ " WHERE A.INTGMED_NO='"
				+ intgmedNo
				+ "' AND A.DSPN_KIND='UD'  AND A.ORDER_CAT1_CODE='PHA_W' "
				+ " AND NOT (A.LINK_NO IS NOT NULL AND A.ROUTE_CODE IN ('IVP','IVD','TPN'))   AND A.IS_INTG='N' AND A.STATION_CODE=B.STATION_CODE "
				+ " AND A.ORDER_CODE=D.ORDER_CODE AND (D.ATC_FLG_I='N' OR D.ATC_FLG_I IS NULL) AND A.EXEC_DEPT_CODE=C.ORG_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ " GROUP BY A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION ,A.DISPENSE_UNIT,A.STATION_CODE,B.STATION_DESC,A.EXEC_DEPT_CODE,"
				+ " A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
				+ " ORDER BY A.ORDER_CODE,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE ";
	}

	/**
	 * 根据配药单号查询普药非静配非口服的药品-已统药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String getStaOrderCodeNormalNoRebinedNoActEd(String intgmedNo) {
		// return
		// " SELECT  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,SUM(A.DISPENSE_QTY) AS SUM_QTY,A.STATION_CODE,B.STATION_DESC, "
		// +
		// " A.EXEC_DEPT_CODE,A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
		// + " FROM ODI_DSPNM A,SYS_STATION B,IND_STOCKM C "
		// + " WHERE A.INTGMED_NO='" + intgmedNo +
		// "' AND A.DSPN_KIND='UD'  AND A.ORDER_CAT1_CODE='PHA_W' "
		// +
		// " AND A.LINK_NO IS NULL AND (A.SENDATC_FLG='0' OR A.SENDATC_FLG IS NULL) AND A.IS_INTG='Y' "
		// +
		// " AND A.STATION_CODE=B.STATION_CODE  AND A.EXEC_DEPT_CODE=C.ORG_CODE AND A.ORDER_CODE=C.ORDER_CODE "
		// +
		// " GROUP  BY  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION ,A.DISPENSE_UNIT,A.STATION_CODE,B.STATION_DESC,A.EXEC_DEPT_CODE,"
		// +
		// " A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
		// +
		// " ORDER BY A.ORDER_CODE,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
		// ;
		// <------------- identify by shendr 2013.07.04
		// AND (A.SENDATC_FLG='0' OR A.SENDATC_FLG IS NULL)
		return " SELECT  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,SUM(A.DISPENSE_QTY) AS SUM_QTY,A.STATION_CODE,B.STATION_DESC, "
				+ " A.EXEC_DEPT_CODE,A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
				+ " FROM ODI_DSPNM A,SYS_STATION B,IND_STOCKM C "
				+ " WHERE A.INTGMED_NO='"
				+ intgmedNo
				+ "' AND A.DSPN_KIND='UD' AND A.ORDER_CAT1_CODE='PHA_W' "
				+ " AND NOT (A.LINK_NO IS NOT NULL AND A.ROUTE_CODE IN ('IVP','IVD','TPN')) AND A.IS_INTG='Y' "
				+ " AND A.STATION_CODE=B.STATION_CODE  AND A.EXEC_DEPT_CODE=C.ORG_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ " GROUP BY A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION ,A.DISPENSE_UNIT,A.STATION_CODE,B.STATION_DESC,A.EXEC_DEPT_CODE,"
				+ " A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
				+ " ORDER BY A.ORDER_CODE,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE ";
		// ---------->
	}

	/**
	 * 根据配药单号查询静配-统药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String getStaOrderCodeWithLiquidInfo(String intgmedNo,
			String flag) {
		// return
		// " SELECT  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,SUM(A.DISPENSE_QTY)AS SUM_QTY,A.STATION_CODE,B.STATION_DESC, "
		// +
		// " A.EXEC_DEPT_CODE,A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE,A.TURN_ESL_ID"
		// + " FROM ODI_DSPNM A,SYS_STATION B,IND_STOCKM C "
		// + " WHERE A.INTGMED_NO='" + intgmedNo +
		// "' AND A.DSPN_KIND='UD'  AND A.ORDER_CAT1_CODE='PHA_W' "
		// + " AND A.LINK_NO IS NOT NULL AND A.LINKMAIN_FLG='N' AND A.IS_INTG='"
		// + flag + "' "
		// +
		// " AND A.STATION_CODE=B.STATION_CODE  AND A.EXEC_DEPT_CODE=C.ORG_CODE AND A.ORDER_CODE=C.ORDER_CODE "
		// +
		// " GROUP  BY  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION ,A.DISPENSE_UNIT,A.STATION_CODE,B.STATION_DESC,A.EXEC_DEPT_CODE,"
		// +
		// " A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE ,A.TURN_ESL_ID"
		// +
		// " ORDER BY A.ORDER_CODE,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE "
		// ;

		return " SELECT  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.DISPENSE_UNIT,SUM(A.DISPENSE_QTY)AS SUM_QTY,A.STATION_CODE,B.STATION_DESC, "
				+ " A.EXEC_DEPT_CODE,A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE,A.TURN_ESL_ID"
				+ " FROM ODI_DSPNM A,SYS_STATION B,IND_STOCKM C "
				+ " WHERE A.INTGMED_NO='"
				+ intgmedNo
				+ "' AND A.DSPN_KIND='UD'  AND A.ORDER_CAT1_CODE='PHA_W' "
				+ " AND (A.LINK_NO IS NOT NULL AND A.ROUTE_CODE IN ('IVP','IVD','TPN')) AND A.LINKMAIN_FLG='N' AND A.IS_INTG='"
				+ flag
				+ "' "
				+ " AND A.STATION_CODE=B.STATION_CODE  AND A.EXEC_DEPT_CODE=C.ORG_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ " GROUP  BY  A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION ,A.DISPENSE_UNIT,A.STATION_CODE,B.STATION_DESC,A.EXEC_DEPT_CODE,"
				+ " A.INTGMED_NO,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE ,A.TURN_ESL_ID"
				+ " ORDER BY A.ORDER_CODE,C.MATERIAL_LOC_CODE,C.MATERIAL_LOC_SEQ,C.ELETAG_CODE ";
	}

	/**
	 * 根据配药单号查询普药静配药品-统药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String updateStateInTg(String intgmedNo, String orderCode) {
		return " UPDATE ODI_DSPNM SET IS_INTG='Y'   " + " WHERE INTGMED_NO='"
				+ intgmedNo + "' AND ORDER_CODE='" + orderCode + "' "
				+ " AND DSPN_KIND = 'UD' " + "AND ORDER_CAT1_CODE = 'PHA_W' "
				+ " AND NOT (LINK_NO IS NOT NULL "
				+ " AND ROUTE_CODE IN ('IVP', 'IVD', 'TPN')) ";
	}

	/**
	 * 静配统药-修改药品统药状态
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String updateStateInTgLiquid(String intgmedNo,
			String orderCode, String trunEseId) {
		return " UPDATE ODI_DSPNM SET IS_INTG='Y' ,TURN_ESL_ID='" + trunEseId
				+ "' " + " WHERE INTGMED_NO='" + intgmedNo
				+ "' AND ORDER_CODE='" + orderCode + "' ";
	}

	/**
	 * 静配配药-绑定周转箱
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String updateAssignInTgLiquid(String intgmedNo,
			String caseNO, String orderCode, String trunEseId) {
		return " UPDATE ODI_DSPNM SET TURN_ESL_ID='" + trunEseId + "' "
				+ " WHERE INTGMED_NO='" + intgmedNo + "' AND CASE_NO='"
				+ caseNO + "' AND ";
	}

	/**
	 * 根据部门，药品查询库存
	 * 
	 * @param parm
	 * @return
	 */
	public static String getStockQty(TParm parm) {
		String sql = " SELECT A.ORDER_CODE,C.ORDER_DESC, FLOOR(SUM (A.STOCK_QTY) / D.DOSAGE_QTY)   QTY , E.UNIT_CHN_DESC ,C.SPECIFICATION "
				+ " FROM IND_STOCK A,PHA_BASE C,PHA_TRANSUNIT D,SYS_UNIT E "
				+ " WHERE A.ORG_CODE='"
				+ parm.getValue("ORG_CODE")
				+ "' AND A.ORDER_CODE='"
				+ parm.getValue("ORDER_CODE")
				+ "' "
				+ " AND A.ACTIVE_FLG='Y' AND SYSDATE < A.VALID_DATE "
				+ " AND A.ORDER_CODE=C.ORDER_CODE AND A.ORDER_CODE=D.ORDER_CODE AND C.STOCK_UNIT = E.UNIT_CODE  "
				+ " GROUP BY A.ORDER_CODE,C.ORDER_DESC,E.UNIT_CHN_DESC,C.SPECIFICATION,D.DOSAGE_QTY ";

		return sql;
	}

	/**
	 * 根据部门，药品查询库存
	 * 
	 * @author shendr
	 * @date 2013.07.11
	 * @return
	 */
	public static String getQty(TParm parm) {
//		String sql = "SELECT CASE WHEN A.STOCKQTY > 0  "
//				+ "THEN FLOOR(A.STOCKQTY/C.DOSAGE_QTY) || E.UNIT_CHN_DESC || CASE WHEN MOD(A.STOCKQTY,C.DOSAGE_QTY) > 0  "
//				+ "     THEN MOD(A.STOCKQTY,C.DOSAGE_QTY) || F.UNIT_CHN_DESC  "
//				+ "                ELSE '' END "
//				+ " ELSE '' END AS QTY,D.ORDER_DESC,D.SPECIFICATION "
//				+ " FROM (SELECT ORG_CODE,ORDER_CODE,SUM(STOCK_QTY) AS STOCKQTY "
//				+ "       FROM IND_STOCK  "
//				+ "      WHERE ORG_CODE='"
//				+ parm.getValue("ORG_CODE")
//				+ "'"
//				+ " AND ORDER_CODE='"
//				+ parm.getValue("ORDER_CODE")
//				+ "' GROUP BY ORG_CODE,ORDER_CODE) A,IND_STOCKM B,PHA_TRANSUNIT C,PHA_BASE D,SYS_UNIT E,SYS_UNIT F "
//				+ "WHERE B.ORG_CODE=A.ORG_CODE "
//				+ "  AND B.ORDER_CODE=A.ORDER_CODE "
//				+ " AND C.ORDER_CODE=A.ORDER_CODE  "
//				+ " AND D.ORDER_CODE=A.ORDER_CODE  "
//				+ " AND E.UNIT_CODE=D.STOCK_UNIT  "
//				+ "AND F.UNIT_CODE=D.DOSAGE_UNIT";
		String sql = "SELECT CASE WHEN SUM(A.STOCK_QTY) > 0 "
            	+"THEN FLOOR(SUM(A.STOCK_QTY)/C.DOSAGE_QTY) || E.UNIT_CHN_DESC ||  "
            	+"         CASE WHEN MOD(SUM(A.STOCK_QTY),C.DOSAGE_QTY) > 0 "
                +"            THEN MOD(SUM(A.STOCK_QTY),C.DOSAGE_QTY) || F.UNIT_CHN_DESC "
                +"          ELSE '' END "
                +"ELSE '' END AS QTY,D.ORDER_DESC,D.SPECIFICATION  "
                +"FROM IND_STOCKM B,IND_STOCK A,PHA_TRANSUNIT C,PHA_BASE D,SYS_UNIT E,SYS_UNIT F  "
                +"WHERE B.ORG_CODE='"+parm.getValue("ORG_CODE")+"' "
                +"AND B.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' "
                +"AND B.ORG_CODE=A.ORG_CODE(+) "
                +"AND B.ORDER_CODE=A.ORDER_CODE(+) "
                +"AND B.ORDER_CODE=C.ORDER_CODE(+) "
                +"AND B.ORDER_CODE=D.ORDER_CODE(+) "
                +"AND D.STOCK_UNIT=E.UNIT_CODE(+) "
                +"AND D.DOSAGE_UNIT=F.UNIT_CODE(+) "
                +"GROUP BY B.ORG_CODE,B.ORDER_CODE,C.DOSAGE_QTY,"
                +"E.UNIT_CHN_DESC,F.UNIT_CHN_DESC,D.ORDER_DESC,D.SPECIFICATION";
		return sql;
	}

	/**
	 * 根据统药单号查询病人-普药（非静配）-摆药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String getOrderCodeInfoByPation(String intgmedNo) {

		// return "SELECT   A.INTGMED_NO, " +
		// "A.CASE_NO, " +
		// "B.STATION_DESC, " +
		// "A.BED_NO, " +
		// "C.BED_NO_DESC," +
		// "A.MR_NO, " +
		// "D.PAT_NAME, " +
		// "A.BOX_ESL_ID, " +
		// "A.STATION_CODE "+
		// "FROM (SELECT MAX(INTGMED_NO) AS INTGMED_NO," +
		// "CASE_NO, " +
		// "MAX(BED_NO) AS BED_NO," +
		// "MAX(MR_NO) AS MR_NO," +
		// "MAX(BOX_ESL_ID) AS BOX_ESL_ID," +
		// "MAX(STATION_CODE) AS STATION_CODE " +
		// "FROM ODI_DSPNM "+
		// "WHERE INTGMED_NO = '" + intgmedNo + "' " +
		// "AND (ORDER_CAT1_CODE = 'PHA_W' OR ORDER_CAT1_CODE = 'PHA_C') " +
		// "AND LINK_NO IS NULL "+
		// "GROUP BY CASE_NO ) A," +
		// "SYS_STATION B, " +
		// "SYS_BED C," +
		// "SYS_PATINFO D "+
		// "WHERE  B.STATION_CODE = A.STATION_CODE " +
		// "AND C.BED_NO = A.BED_NO " +
		// "AND D.MR_NO(+) = A.MR_NO " +
		// "AND C.BED_NO = A.BED_NO " +
		// "AND D.MR_NO = A.MR_NO "+
		// "ORDER BY A.BED_NO";

		return "SELECT   A.INTGMED_NO, "
				+ "A.CASE_NO, "
				+ "B.STATION_DESC, "
				+ "A.BED_NO, "
				+ "C.BED_NO_DESC,"
				+ "A.MR_NO, "
				+ "D.PAT_NAME, "
				+ "A.BOX_ESL_ID, "
				+ "A.STATION_CODE "
				+ "FROM (SELECT MAX(INTGMED_NO) AS INTGMED_NO,"
				+ "CASE_NO, "
				+ "MAX(BED_NO) AS BED_NO,"
				+ "MAX(MR_NO) AS MR_NO,"
				+ "MAX(BOX_ESL_ID) AS BOX_ESL_ID,"
				+ "MAX(STATION_CODE) AS STATION_CODE "
				+ "FROM ODI_DSPNM "
				+ "WHERE INTGMED_NO = '"
				+ intgmedNo
				+ "' "
				+ "AND (ORDER_CAT1_CODE = 'PHA_W' OR ORDER_CAT1_CODE = 'PHA_C') "
				+ "AND  NOT (LINK_NO IS NOT NULL AND ROUTE_CODE IN ('IVP','IVD','TPN')) "
				+ "GROUP BY CASE_NO ) A," + "SYS_STATION B, " + "SYS_BED C,"
				+ "SYS_PATINFO D " + "WHERE  B.STATION_CODE = A.STATION_CODE "
				+ "AND C.BED_NO = A.BED_NO " + "AND D.MR_NO(+) = A.MR_NO "
				+ "AND C.BED_NO = A.BED_NO " + "AND D.MR_NO = A.MR_NO "
				+ "ORDER BY A.BED_NO";

	}

	/**
	 * 根据统药单号查询病人-用药信息-普药（非静配）-摆药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String getOrderCodeInfoDetailByPation(String intgmedNo,
			String caseNO) {
		// return " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM, "
		// +
		// "        A.ORDER_DESC || ' ' || CASE WHEN A.SPECIFICATION IS NOT NULL THEN A.SPECIFICATION ELSE '' END  AS ORDER_DESC , "
		// +
		// " 		TO_CHAR(A.MEDI_QTY ,'FM9999990.099') || '' || B.UNIT_CHN_DESC AS MEDI_QTY, C.ROUTE_CHN_DESC,D.FREQ_CHN_DESC,A.TAKE_DAYS, "
		// +
		// " 		TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') || '' || E.UNIT_CHN_DESC AS DISPENSE_QTY,A.OWN_AMT "
		// +
		// " FROM ODI_DSPNM A,SYS_UNIT B,SYS_PHAROUTE C,SYS_PHAFREQ D,SYS_UNIT E  "
		// + " WHERE A.INTGMED_NO ='" + intgmedNo + "' AND A.CASE_NO='" +
		// caseNO+ "'  AND A.LINK_NO IS NULL "
		// +
		// "       AND B.UNIT_CODE=A.MEDI_UNIT AND C.ROUTE_CODE=A.ROUTE_CODE AND D.FREQ_CODE=A.FREQ_CODE AND E.UNIT_CODE=A.DISPENSE_UNIT "
		// + " ORDER BY A.ORDER_CODE ";

		return " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM, "
				+ "        A.ORDER_DESC || ' ' || CASE WHEN A.SPECIFICATION IS NOT NULL THEN A.SPECIFICATION ELSE '' END  AS ORDER_DESC , "
				+ " 		TO_CHAR(A.MEDI_QTY ,'FM9999990.099') || '' || B.UNIT_CHN_DESC AS MEDI_QTY, C.ROUTE_CHN_DESC,D.FREQ_CHN_DESC,A.TAKE_DAYS, "
				+ " 		TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') || '' || E.UNIT_CHN_DESC AS DISPENSE_QTY,A.OWN_AMT "
				+ " FROM ODI_DSPNM A,SYS_UNIT B,SYS_PHAROUTE C,SYS_PHAFREQ D,SYS_UNIT E  "
				+ " WHERE A.INTGMED_NO ='"
				+ intgmedNo
				+ "' AND A.CASE_NO='"
				+ caseNO
				+ "' AND  NOT (A.LINK_NO IS NOT NULL AND A.ROUTE_CODE IN ('IVP','IVD','TPN')) "
				+ "       AND B.UNIT_CODE=A.MEDI_UNIT AND C.ROUTE_CODE=A.ROUTE_CODE AND D.FREQ_CODE=A.FREQ_CODE AND E.UNIT_CODE=A.DISPENSE_UNIT "
				+ " ORDER BY A.ORDER_CODE ";
	}

	/**
	 * 根据统药单号查询病人信息-普药（静配）-摆药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String getOrderCodeInfoLiquidByPation(String intgmedNo) {
		// return
		// " SELECT  'Y' AS SELECT_FLG, A.INTGMED_NO, A.CASE_NO, B.STATION_DESC, C.BED_NO_DESC, A.MR_NO, D.PAT_NAME, A.BOX_ESL_ID,A.ORDER_CODE,E.ORDER_DESC , "
		// +
		// " F.ROUTE_CHN_DESC,G.FREQ_CHN_DESC ,TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') || '' || K.UNIT_CHN_DESC AS DISPENSE_QTY,A.LINK_NO,'' AS IS_CHECK  "
		// + " ,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.TURN_ESL_ID ,A.LINK_NO "
		// +
		// " FROM ODI_DSPNM A, SYS_STATION B, SYS_BED C, SYS_PATINFO D,PHA_BASE E ,SYS_PHAROUTE F,SYS_PHAFREQ G,SYS_UNIT K"
		// + " WHERE A.INTGMED_NO ='" + intgmedNo +
		// "'  AND A.ORDER_CAT1_CODE='PHA_W' AND A.LINK_NO IS NOT NULL AND A.LINKMAIN_FLG='Y' "
		// +
		// " AND B.STATION_CODE = A.STATION_CODE   AND A.ROUTE_CODE = F.ROUTE_CODE AND A.FREQ_CODE=G.FREQ_CODE AND A.DISPENSE_UNIT=K.UNIT_CODE "
		// +
		// " AND C.BED_NO = A.BED_NO  AND D.MR_NO = A.MR_NO  AND C.BED_NO = A.BED_NO  AND D.MR_NO = A.MR_NO AND A.ORDER_CODE=E.ORDER_CODE "
		// + " ORDER BY A.BED_NO,A.ROUTE_CODE ";

		return " SELECT 'Y' AS SELECT_FLG, A.INTGMED_NO,A.CASE_NO,B.STATION_DESC,C.BED_NO_DESC, A.MR_NO, D.PAT_NAME, A.BOX_ESL_ID,A.ORDER_CODE,E.ORDER_DESC , "
				+ " F.ROUTE_CHN_DESC,G.FREQ_CHN_DESC ,TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') || '' || K.UNIT_CHN_DESC AS DISPENSE_QTY,A.LINK_NO,'' AS IS_CHECK  "
				+ " ,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.TURN_ESL_ID ,A.LINK_NO "
				+ " FROM ODI_DSPNM A, SYS_STATION B, SYS_BED C, SYS_PATINFO D,PHA_BASE E ,SYS_PHAROUTE F,SYS_PHAFREQ G,SYS_UNIT K"
				+ " WHERE A.INTGMED_NO ='"
				+ intgmedNo
				+ "'  AND A.ORDER_CAT1_CODE='PHA_W' AND (A.LINK_NO IS NOT NULL AND A.ROUTE_CODE IN ('IVP','IVD','TPN')) AND A.LINKMAIN_FLG='Y' "
				+ " AND B.STATION_CODE = A.STATION_CODE   AND A.ROUTE_CODE = F.ROUTE_CODE AND A.FREQ_CODE=G.FREQ_CODE AND A.DISPENSE_UNIT=K.UNIT_CODE "
				+ " AND C.BED_NO = A.BED_NO  AND D.MR_NO = A.MR_NO  AND C.BED_NO = A.BED_NO  AND D.MR_NO = A.MR_NO AND A.ORDER_CODE=E.ORDER_CODE "
				+ " ORDER BY A.BED_NO,A.ROUTE_CODE ";
	}

	/**
	 * 根据统药单号查询病人-用药信息-普药（静配）-摆药
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String getOrderCodeInfoDLiquidByPation(String intgmedNo,
			String caseNO, String linkNo) {
		return " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM, "
				+ "        A.ORDER_DESC ,A.SPECIFICATION, "
				+ " 		TO_CHAR(A.MEDI_QTY,'FM9999990.099') || '' || B.UNIT_CHN_DESC AS MEDI_QTY, C.ROUTE_CHN_DESC,D.FREQ_CHN_DESC,A.TAKE_DAYS, "
				+ " 		TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') || '' || E.UNIT_CHN_DESC AS DISPENSE_QTY,A.OWN_AMT "
				+ " FROM ODI_DSPNM A,SYS_UNIT B,SYS_PHAROUTE C,SYS_PHAFREQ D,SYS_UNIT E  "
				+ " WHERE A.INTGMED_NO ='"
				+ intgmedNo
				+ "'  AND A.CASE_NO ='"
				+ caseNO
				+ "' AND A.LINK_NO='"
				+ linkNo
				+ "' AND A.LINKMAIN_FLG='N' "
				+ "       AND B.UNIT_CODE=A.MEDI_UNIT AND C.ROUTE_CODE=A.ROUTE_CODE AND D.FREQ_CODE=A.FREQ_CODE AND E.UNIT_CODE=A.DISPENSE_UNIT "
				+ " ORDER BY A.ORDER_CODE ";
	}

	/**
	 * 修改住院普药-非静配 配药-病人小药盒
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String updateBoxEslStateDspnM(String intgmedNo,
			String caseNo, String boxElsId, String orderNo, String orderSeq,
			String startDttm) {
		String sql = " UPDATE ODI_DSPNM SET BOX_ESL_ID='" + boxElsId + "' "
				+ " WHERE INTGMED_NO ='" + intgmedNo + "'  AND CASE_NO='"
				+ caseNo + "' AND LINK_NO IS NULL AND ORDER_CAT1_CODE='PHA_W' ";// //
		// if(null != orderSeq && orderSeq.length()>0){
		// sql += "AND ORDER_SEQ='" + orderSeq + "' ";/////////
		// }
		// if(null != orderSeq && orderSeq.length()>0){
		// sql += "' AND START_DTTM='" + startDttm + "' ";///////////////
		// }
		return sql;
	}

	/**
	 * 修改住院普药-非静配 配药-病人小药盒
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String updateBoxEslStateDspnD(String intgmedNo,
			String caseNo, String orderNo, String orderSeq, String startDttm,
			String boxElsId) {
		return "  UPDATE ODI_DSPND A " + "SET A.BOX_ESL_ID='" + boxElsId + "' "
				+ "  WHERE A.INTGMED_NO ='" + intgmedNo + "' "
				+ "AND A.CASE_NO='" + caseNo + "'";
		// + "  AND ( SELECT COUNT(B.CASE_NO) "
		// + "		FROM ODI_DSPNM B "
		// +
		// "  		WHERE  B.CASE_NO=A.CASE_NO AND B.INTGMED_NO=A.INTGMED_NO AND B.LINK_NO IS NULL AND B.ORDER_CAT1_CODE='PHA_W' "
		// +
		// "     		   AND B.ORDER_NO=A.ORDER_NO AND B.ORDER_SEQ=A.ORDER_SEQ  AND A.ORDER_DATE||A.ORDER_DATETIME >=B.START_DTTM "
		// + "  	  ) > 0 ";
	}

	/**
	 * 修改住院普药-非静配 配药-病人小药盒
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String updateBoxEslStateDspnDD(String intgmedNo,
			String caseNo, String orderNo, String orderSeq, String startDttm,
			String boxElsId) {
		return " UPDATE ODI_DSPND A SET A.BOX_ESL_ID='" + boxElsId + "' "
				+ " WHERE CASE_NO='" + caseNo + "' AND ORDER_NO='" + orderNo
				+ "' AND ORDER_DATE='" + startDttm + "' ";
	}

	/**
	 * 修改住院普药-静配 配药-绑定周转箱
	 * 
	 * @param intgmedNo
	 * @return
	 */
	public static String updateTrunElsIdDspnM(String intgmedNo, String caseNo,
			String orderNo, String orderSeq, String startDttm, String turnEslId) {
		return " UPDATE ODI_DSPNM A SET TURN_ESL_ID='" + turnEslId + "' "
				+ " WHERE CASE_NO='" + caseNo + "' AND ORDER_NO='" + orderNo
				+ "' AND ORDER_SEQ='" + orderSeq + "' AND START_DTTM='"
				+ startDttm + "' ";
	}

	/**
	 * 统计查询容器麻精药品数量-合计
	 * 
	 * @return
	 */
	public static String getStaticDrugQtyInContainerM(TParm parm) {
		return "   SELECT A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC,COUNT(A.CONTAINER_ID) AS CONTAINER_QTY,SUM(A.TOT_QTY) AS TOT_QTY,A.SPECIFICATION, "
				+ "B.SAFE_QTY,B.SAFE_QTY-SUM(A.TOT_QTY) AS SUP_QTY"
				+ "   FROM( "
				+ "        SELECT B.CABINET_ID,B.ORDER_CODE,D.ORDER_DESC,B.CONTAINER_ID,C.UNIT_CHN_DESC,COUNT(B.CONTAINER_ID) AS TOT_QTY,D.SPECIFICATION "
				+ "  		FROM IND_CONTAINERD B ,SYS_UNIT C, PHA_BASE D"
				+ "		WHERE  CABINET_ID='"
				+ parm.getValue("CABINET_ID")
				+ "' AND B.ORDER_CODE=D.ORDER_CODE  AND B.UNIT_CODE=C.UNIT_CODE"
				+ "		GROUP BY B.ORDER_CODE,D.ORDER_DESC,CONTAINER_ID,B.CABINET_ID,C.UNIT_CHN_DESC,D.SPECIFICATION "
				+ " 		) A,IND_STOCKM B,IND_CABINET C "
				+ "WHERE A.ORDER_CODE=B.ORDER_CODE " 
				+		"AND A.CABINET_ID=C.CABINET_ID " 
				+		"AND C.ORG_CODE=B.ORG_CODE "
				+ "   GROUP BY  A.ORDER_CODE,A.ORDER_DESC,A.UNIT_CHN_DESC,A.SPECIFICATION,B.SAFE_QTY ";
	}

	/**
	 * 统计查询容器药品数量-明细
	 * 
	 * @return
	 */
	public static String getStaticDrugQtyInContainerD(TParm parm) {
		return "   SELECT B.CONTAINER_DESC,A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,A.TOXIC_ID,D.UNIT_CHN_DESC,A.BATCH_NO,A.VALID_DATE,A.VERIFYIN_PRICE "
				+ "   FROM IND_CONTAINERD A ,IND_CONTAINERM B,PHA_BASE C,SYS_UNIT D "
				+ "   WHERE A.CABINET_ID='"
				+ parm.getValue("CABINET_ID")
				+ "' AND A.CONTAINER_ID=B.CONTAINER_ID AND A.ORDER_CODE=C.ORDER_CODE AND A.UNIT_CODE=D.UNIT_CODE "
				+ "   ORDER BY  B.CONTAINER_DESC,A.ORDER_CODE";
	}

	/**
	 * 统计查询容器普药药品数量-合计
	 * 
	 * @return
	 */
	public static String getStaticNromalQtyInContainerM(TParm parm) {
		return "SELECT B.ORDER_CODE,D.ORDER_DESC,E.UNIT_CHN_DESC,SUM(C.STOCK_QTY) AS TOT_QTY, "
				+ "D.SPECIFICATION,B.SAFE_QTY,B.SAFE_QTY-SUM(C.STOCK_QTY) AS SUP_QTY "
				+ "FROM IND_CABINET A,IND_STOCKM B,IND_STOCK C,PHA_BASE D, SYS_UNIT E "
				+ " WHERE A.CABINET_ID = '"
				+ parm.getValue("CABINET_ID")
				+ "' "
				+ "AND B.ORG_CODE = A.ORG_CODE "
				+ "AND C.ORG_CODE = B.ORG_CODE "
				+ "AND C.ORDER_CODE = B.ORDER_CODE "
				+ "AND D.ORDER_CODE = C.ORDER_CODE "
				+ "AND E.UNIT_CODE = D.DOSAGE_UNIT "
				+ "GROUP BY B.ORDER_CODE,D.ORDER_DESC,E.UNIT_CHN_DESC,D.SPECIFICATION,B.SAFE_QTY";
	}

	/**
	 * 统计查询容器普药药品数量-明细
	 * 
	 * @return
	 */
	public static String getStaticNromalQtyInContainerD(TParm parm) {
		return "SELECT B.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,B.BATCH_SEQ, "
				+ "B.BATCH_NO,B.VALID_DATE,B.VERIFYIN_PRICE,D.UNIT_CHN_DESC,B.STOCK_QTY "
				+ "FROM IND_CABINET A,IND_STOCK B,PHA_BASE C,SYS_UNIT D "
				+ "WHERE A.CABINET_ID = '" + parm.getValue("CABINET_ID") + "' "
				+ "AND B.ORG_CODE = A.ORG_CODE "
				+ "AND C.ORDER_CODE = B.ORDER_CODE "
				+ "AND D.UNIT_CODE = C.DOSAGE_UNIT";
	}

	/**
	 * 通过条码查询配液明细
	 * 
	 * @param caseNo
	 * @param barCode
	 * @return
	 */
	public static String getOrderCodeAssignLiquid(String caseNo, String barCode) {
		return " SELECT  A.CASE_NO,A.ORDER_CODE,   B.ORDER_DESC,B.SPECIFICATION, F.LINK_NO, "
				+ " A.DOSAGE_QTY AS DOSAGE_QTY, C.UNIT_CHN_DESC AS UNIT_CHN_DESC,D.ROUTE_CHN_DESC,E.FREQ_CHN_DESC,A.MEDI_QTY,A.DOSAGE_QTY  AS DISPENSE_QTY "
				+ " FROM ODI_DSPND A, ODI_ORDER B, SYS_UNIT C  , SYS_PHAROUTE D,  SYS_PHAFREQ E ,ODI_DSPNM F"
				+ " WHERE A.CASE_NO = '"
				+ caseNo
				+ "' AND A.BAR_CODE = '"
				+ barCode
				+ "' AND  B.ROUTE_CODE = D.ROUTE_CODE   AND B.FREQ_CODE = E.FREQ_CODE "
				+ " AND B.CASE_NO = A.CASE_NO AND B.ORDER_NO = A.ORDER_NO AND B.ORDER_SEQ = A.ORDER_SEQ  AND B.LINKMAIN_FLG = 'N'  AND C.UNIT_CODE = A.DOSAGE_UNIT "
				+ " AND A.CASE_NO=F.CASE_NO  AND A.ORDER_NO = F.ORDER_NO AND A.ORDER_SEQ=F.ORDER_SEQ "
				+ " GROUP BY   A.CASE_NO,A.ORDER_CODE,  B.ORDER_DESC,B.SPECIFICATION,F.LINK_NO,  A.DOSAGE_QTY,  C.UNIT_CHN_DESC,D.ROUTE_CHN_DESC,E.FREQ_CHN_DESC,A.MEDI_QTY,A.DOSAGE_QTY";

	}

	/**
	 * 统计查询容器普药药品数量-明细
	 * 
	 * @return
	 */
	public static String getCheckQtyInContainerD(String orgCode) {
		return /*
				 * "   SELECT B.ORG_CODE,  A.ORDER_CODE, A.BATCH_SEQ,FLOOR(A.STOCK_QTY/C.DOSAGE_QTY) AS STOCK_QTY,MOD(A.STOCK_QTY,C.DOSAGE_QTY) AS STOCK_QTY_M"
				 * + "   FROM IND_CBNSTOCK A , IND_CABINET B , PHA_TRANSUNIT C"
				 * + "   WHERE B.ORG_CODE='" + orgCode +
				 * "' AND B.CABINET_ID=A.CABINET_ID AND A.ORDER_CODE=C.ORDER_CODE "
				 * + "   UNION ALL " +
				 */"   SELECT B.ORG_CODE, A.ORDER_CODE,A.BATCH_SEQ,COUNT(A.TOXIC_ID) AS STOCK_QTY,0 AS STOCK_QTY_M  "
				+ "   FROM IND_CONTAINERD A ,IND_CABINET B  "
				+ "   WHERE B.ORG_CODE='"
				+ orgCode
				+ "' AND A.CABINET_ID=B.CABINET_ID"
				+ "   GROUP BY   B.ORG_CODE,  A.ORDER_CODE,A.BATCH_SEQ ";
	}

	/**
	 * 保持门诊医生医嘱从HIS到物联网
	 * 
	 * @param obj
	 * @return
	 */
	public static String saveOpdOrderFromHisBySpc(SpcOpdOrderDto obj) {
		String sql = " INSERT INTO OPD_ORDER "
				+ "	(CASE_NO, RX_NO, SEQ_NO, PRESRT_NO, REGION_CODE, MR_NO, ADM_TYPE, RX_TYPE, TEMPORARY_FLG, RELEASE_FLG, LINKMAIN_FLG, LINK_NO,  "
				+ // 1行
				"	ORDER_CODE, ORDER_DESC, GOODS_DESC, SPECIFICATION, ORDER_CAT1_CODE, MEDI_QTY, MEDI_UNIT, FREQ_CODE,ROUTE_CODE, TAKE_DAYS,　　   "
				+ // 2行
				"	DOSAGE_QTY, DOSAGE_UNIT, DISPENSE_QTY,DISPENSE_UNIT, GIVEBOX_FLG, OWN_PRICE, NHI_PRICE, DISCOUNT_RATE,OWN_AMT, AR_AMT,DR_NOTE,"
				+ // 3行
				"	NS_NOTE, DR_CODE,ORDER_DATE, DEPT_CODE, DC_DR_CODE, DC_ORDER_DATE, DC_DEPT_CODE, EXEC_DEPT_CODE, EXEC_DR_CODE, SETMAIN_FLG,	  "
				+ // 4行
				"	ORDERSET_GROUP_NO, ORDERSET_CODE, HIDE_FLG, RPTTYPE_CODE, OPTITEM_CODE, DEV_CODE, MR_CODE, FILE_NO, DEGREE_CODE, URGENT_FLG,  "
				+ // 5行
				"  INSPAY_TYPE, PHA_TYPE, DOSE_TYPE, EXPENSIVE_FLG, PRINTTYPEFLG_INFANT, CTRLDRUGCLASS_CODE, PRESCRIPT_NO, ATC_FLG, SENDATC_DATE,"
				+ // 6行
				" 	RECEIPT_NO, BILL_FLG, BILL_DATE, BILL_USER, PRINT_FLG, REXP_CODE, HEXP_CODE, CONTRACT_CODE, CTZ1_CODE, CTZ2_CODE, CTZ3_CODE,  "
				+ // 7行
				"	PHA_CHECK_CODE, PHA_CHECK_DATE, PHA_DOSAGE_CODE, PHA_DOSAGE_DATE, PHA_DISPENSE_CODE, PHA_DISPENSE_DATE, PHA_RETN_CODE,		  "
				+ // 8行
				" 	PHA_RETN_DATE, NS_EXEC_CODE, NS_EXEC_DATE,NS_EXEC_DEPT,DCTAGENT_CODE,DCTEXCEP_CODE, DCT_TAKE_QTY, PACKAGE_TOT,AGENCY_ORG_CODE,"
				+ // 9行
				"	DCTAGENT_FLG,DECOCT_CODE, REQUEST_FLG, REQUEST_NO, OPT_USER, OPT_DATE, OPT_TERM, MED_APPLY_NO, CAT1_TYPE, TRADE_ENG_DESC, 	  "
				+ // 10行
				"	PRINT_NO,COUNTER_NO, PSY_FLG, EXEC_FLG, RECEIPT_FLG, BILL_TYPE, FINAL_TYPE, DECOCT_REMARK, SEND_DCT_USER, SEND_DCT_DATE,	  "
				+ // 11行
				"	DECOCT_USER,DECOCT_DATE, SEND_ORG_USER, SEND_ORG_DATE, EXM_EXEC_END_DATE, EXEC_DR_DESC,COST_AMT, COST_CENTER_CODE, BATCH_SEQ1, "
				+ // 12行
				" 	VERIFYIN_PRICE1, DISPENSE_QTY1,BATCH_SEQ2, VERIFYIN_PRICE2, DISPENSE_QTY2, BATCH_SEQ3, VERIFYIN_PRICE3,DISPENSE_QTY3, "
				+ // 13行
				"  BUSINESS_NO,PAT_NAME,BIRTH_DATE,SEX_TYPE,FROM_FLG "
				+ " ) VALUES " + " ( " + " '"
				+ obj.getCaseNo()
				+ "', '"
				+ obj.getRxNo()
				+ "',"
				+ obj.getSeqNo()
				+ ", "
				+ obj.getPresrtNo()
				+ ", '"
				+ obj.getRegionCode()
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getMrNo())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getAdmType())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getRxType())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getTemporaryFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getReleaseFlg())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getLinkmainFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getLinkNo())
				+ "',"
				+ // 1行
				" '"
				+ StringUtils.trimToEmpty(obj.getOrderCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getOrderDesc())
				+ "', "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getGoodsDesc())
				+ "','"
				+ StringUtils.trimToEmpty(obj.getSpecification())
				+ "', "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getOrderCat1Code())
				+ "', "
				+ obj.getMediQty()
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getMediUnit())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getFreqCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getRouteCode())
				+ "', "
				+ obj.getTakeDays()
				+ ","
				+ // 2行
				"  "
				+ obj.getDosageQty()
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getDosageUnit())
				+ "',"
				+ obj.getDispenseQty()
				+ ",'"
				+ StringUtils.trimToEmpty(obj.getDispenseUnit())
				+ "', "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getGiveboxFlg())
				+ "', "
				+ obj.getOwnPrice()
				+ ", "
				+ obj.getNhiPrice()
				+ ", "
				+ obj.getDiscountRate()
				+ ","
				+ "  "
				+ obj.getOwnAmt()
				+ ", "
				+ obj.getArAmt()
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getDrNote())
				+ "', "
				+ // 3行
				" '"
				+ StringUtils.trimToEmpty(obj.getNsNote())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getDrCode())
				+ "', "
				+ StringUtils.trimToDatTimeSql(obj.getOrderDate())
				+ ", "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getDeptCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getDcDrCode())
				+ "', "
				+ StringUtils.trimToDatTimeSql(obj.getDcOrderDate())
				+ ","
				+ " '"
				+ StringUtils.trimToEmpty(obj.getDcDeptCode())
				+ "','"
				+ StringUtils.trimToEmpty(obj.getExecDeptCode())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getExecDrCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getSetmainFlg())
				+ "', "
				+ // 4行
				"  "
				+ obj.getOrdersetGroupNo()
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getOrdersetCode())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getHideFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getRpttypeCode())
				+ "', "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getOptitemCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getDevCode())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getMrCode())
				+ "',"
				+ obj.getFileNo()
				+ ", "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getDegreeCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getUrgentFlg())
				+ "', "
				+ // 5行
				" '"
				+ StringUtils.trimToEmpty(obj.getInspayType())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getPhaType())
				+ "','"
				+ StringUtils.trimToEmpty(obj.getDoseType())
				+ "', "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getExpensiveFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getPrinttypeflgInfant())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getCtrldrugclassCode())
				+ "',"
				+ obj.getPrescriptNo()
				+ ","
				+ " '"
				+ StringUtils.trimToEmpty(obj.getAtcFlg())
				+ "',"
				+ StringUtils.trimToDatTimeSql(obj.getSendatcDate())
				+ ","
				+ // 6行
				" '"
				+ StringUtils.trimToEmpty(obj.getReceiptNo())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getBillFlg())
				+ "',"
				+ "  "
				+ StringUtils.trimToDatTimeSql(obj.getBillDate())
				+ ",'"
				+ StringUtils.trimToEmpty(obj.getBillUser())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getPrintFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getRexpCode())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getHexpCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getContractCode())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getCtz1Code())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getCtz2Code())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getCtz3Code())
				+ "',"
				+ // 7行
				" '"
				+ StringUtils.trimToEmpty(obj.getPhaCheckCode())
				+ "', "
				+ StringUtils.trimToDatTimeSql(obj.getPhaCheckDate())
				+ ", "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getPhaDosageCode())
				+ "', "
				+ StringUtils.trimToDatTimeSql(obj.getPhaDosageDate())
				+ ", "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getPhaDispenseCode())
				+ "',"
				+ StringUtils.trimToDatTimeSql(obj.getPhaDispenseDate())
				+ ","
				+ " '"
				+ StringUtils.trimToEmpty(obj.getPhaRetnCode())
				+ "',"
				+ // 8行
				"  "
				+ StringUtils.trimToDatTimeSql(obj.getPhaRetnDate())
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getNsExecCode())
				+ "',"
				+ "  "
				+ StringUtils.trimToDatTimeSql(obj.getNsExecDate())
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getNsExecDept())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getDctagentCode())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getDctexcepCode())
				+ "' ,"
				+ "  "
				+ obj.getDctTakeQty()
				+ " ,"
				+ obj.getPackageTot()
				+ ",'"
				+ StringUtils.trimToEmpty(obj.getAgencyOrgCode())
				+ "', "
				+ // 9行
				" '"
				+ StringUtils.trimToEmpty(obj.getDctagentFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getDecoctCode())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getRequestFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getRequestNo())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getOptUser())
				+ "',sysdate, '"
				+ StringUtils.trimToEmpty(obj.getOptTerm())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getMedApplyNo())
				+ "','"
				+ StringUtils.trimToEmpty(obj.getCat1Type())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getTradeEngDesc())
				+ "', "
				+ // 10行
				" '"
				+ StringUtils.trimToEmpty(obj.getPrintNo())
				+ "',"
				+ obj.getCounterNo()
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getPsyFlg())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getExecFlg())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getReceiptFlg())
				+ "', "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getBillType())
				+ "','"
				+ StringUtils.trimToEmpty(obj.getFinalType())
				+ "',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getDecoctRemark())
				+ "', '"
				+ StringUtils.trimToEmpty(obj.getSendDctUser())
				+ "',"
				+ "  "
				+ StringUtils.trimToDatTimeSql(obj.getSendDctDate())
				+ ", "
				+ // 11行SEND_ORG_DATE, EXM_EXEC_END_DATE
				" '"
				+ StringUtils.trimToEmpty(obj.getDecoctUser())
				+ "',"
				+ StringUtils.trimToDatTimeSql(obj.getDecoctDate())
				+ ","
				+ " '"
				+ StringUtils.trimToEmpty(obj.getSendOrgUser())
				+ "',"
				+ StringUtils.trimToDatTimeSql(obj.getSendOrgDate())
				+ ","
				+ "  "
				+ StringUtils.trimToDatTimeSql(obj.getExmExecEndDate())
				+ ",'"
				+ StringUtils.trimToEmpty(obj.getExecDrDesc())
				+ "',"
				+ "  "
				+ obj.getCostAmt()
				+ ", '"
				+ StringUtils.trimToEmpty(obj.getCostCenterCode())
				+ "',"
				+ obj.getBatchSeq1()
				+ ", "
				+ // 12行
				"  "
				+ obj.getVerifyinPrice1()
				+ ","
				+ obj.getDispenseQty1()
				+ ","
				+ obj.getBatchSeq2()
				+ ","
				+ obj.getVerifyinPrice2()
				+ ","
				+ "  "
				+ obj.getDispenseQty2()
				+ ","
				+ obj.getBatchSeq3()
				+ ","
				+ obj.getVerifyinPrice3()
				+ ","
				+ obj.getDispenseQty3()
				+ ", "
				+ " '"
				+ StringUtils.trimToEmpty(obj.getBusinessNo())
				+ "','"
				+ StringUtils.trimToEmpty(obj.getPatName())
				+ "',"
				+ "  "
				+ StringUtils.trimToDatTimeSql(obj.getBirthDate())
				+ ",'"
				+ StringUtils.trimToEmpty(obj.getSexType())
				+ "','1' "
				+ " )";
		return sql;
	}

	/**
	 * 物联网-门诊发药-更新状态
	 * 
	 * @author liyh
	 * @return
	 */
	public static String updateOpdOrderSend(SpcCommonDto obj) {
		String sql = "	UPDATE OPD_ORDER SET PHA_DISPENSE_COD='"
				+ obj.getPhaDispenseCode() + "' , PHA_DISPENSE_DATE="
				+ obj.getPhaDispenseDate() + " " + "	WHERE CASE_NO='"
				+ obj.getCaseNo() + "' AND RX_NO='" + obj.getRxNo()
				+ "' AND SEQ_NO=" + obj.getSeqNo() + " ";
		return sql;
	}

	/**
	 * 物联网-更新验收表的 发票信息
	 * 
	 * @author liyh
	 * @return
	 */
	public static String updateVerifyin(String invoiceNO, String invoiceDate,
			String verifyinNo, String orderCode, String purOrderNo) {
		String sql = "	UPDATE IND_VERIFYIND SET INVOICE_NO='" + invoiceNO
				+ "' , INVOICE_DATE=TO_DATE('" + invoiceDate
				+ "','yyyy-MM-dd') " + "	WHERE VERIFYIN_NO='" + verifyinNo
				+ "' AND ORDER_CODE='" + orderCode + "' AND PURORDER_NO='"
				+ purOrderNo + "' ";
		return sql;
	}

	/**
	 * 物联网-查询病患信息是否同步过来
	 * 
	 * @param startDate
	 *            开始日期
	 * @param endDate
	 *            结束日期
	 * @param rxNo
	 *            处方号
	 * @param mrNo
	 *            病案号
	 * @return
	 */
	public static String getCountOPdOrder(String startDate, String endDate,
			String rxNo, String mrNo) {
		String sqlString = " SELECT COUNT(*) as COUNT FROM OPD_ORDER WHERE  ORDER_DATE BETWEEN TO_DATE('"
				+ startDate
				+ "','yyyy-MM-dd hh24:mi:ss') "
				+ " AND TO_DATE('"
				+ endDate
				+ "','yyyy-MM-dd hh24:mi:ss') AND PHA_CHECK_CODE IS NOT  NULL ";
		if (StringUtils.isNotBlank(rxNo)) {
			sqlString += " AND RX_NO='" + rxNo + "' ";
		}
		if (StringUtils.isNotBlank(mrNo)) {
			sqlString += " AND MR_NO='" + mrNo + "' ";
		}
		return sqlString;
	}

	/**
	 * HIS退药查询医嘱状态
	 * 
	 * @param mrNo
	 *            病案号
	 * @param rxNo
	 *            处方号
	 * @return
	 */
	public static String getPhaStateReturn(String rxNo, String caseNo,
			String seqNo) {
		String sql = " SELECT PHA_CHECK_DATE,PHA_CHECK_CODE,PHA_DOSAGE_CODE,PHA_DOSAGE_DATE,PHA_DISPENSE_CODE,PHA_DISPENSE_DATE,PHA_RETN_CODE,PHA_RETN_DATE "
				+ " FROM OPD_ORDER WHERE RX_NO='"
				+ rxNo
				+ "' AND SEQ_NO='"
				+ seqNo + "' AND CASE_NO='" + caseNo + "' ";
		return sql;
	}

	/**
	 * SPC和his药品编码对照查询
	 * 
	 * @param spcOrderCode
	 * @param hisRegionCode
	 * @param hisOrderCode
	 * @return
	 */
	public static String getSpcOrderCodeByHisOrderCode(String spcOrderCode,
			String hisRegionCode, String hisOrderCode) {
		String sql = " SELECT  REGION_CODE, ORDER_CODE, HIS_ORDER_CODE, ORDER_DESC, SPECIFICATION, "
				+ " GOODS_DESC, HIS_ORDER_DESC, HIS_SPECIFICATION, HIS_GOODS_DESC "
				+ " FROM SYS_FEE_SPC WHERE ACTIVE_FLG='Y' ";
		if (StringUtils.isNotBlank(hisRegionCode)) {
			sql += " AND REGION_CODE='" + hisRegionCode + "' ";
		}
		if (StringUtils.isNotBlank(spcOrderCode)) {
			sql += " AND ORDER_CODE='" + spcOrderCode + "' ";
		} 
		if (StringUtils.isNotBlank(hisOrderCode)) {
			sql += " AND HIS_ORDER_CODE='" + hisOrderCode + "' ";
		}
		return sql;
	}

	/**
	 * 查询代理商的药品价格
	 * 
	 * @param supCode
	 * @param orderCode
	 * @return String
	 */
	public static String getPriceOfSupCode(String supCode, String orderCode) {
		String sql = "SELECT  SUP_CODE, A.ORDER_CODE, MAIN_FLG, CONTRACT_NO, CONTRACT_PRICE, LAST_ORDER_DATE, LAST_ORDER_QTY, "
				+ " LAST_ORDER_PRICE, LAST_ORDER_NO,  LAST_VERIFY_DATE, LAST_VERIFY_PRICE "
				+ " FROM IND_AGENT A,SYS_FEE B "
				+ " WHERE A.ORDER_CODE=B.ORDER_CODE AND B.ACTIVE_FLG='Y' "
				+ " AND A.SUP_CODE='"
				+ supCode
				+ "' AND A.ORDER_CODE='"
				+ orderCode + "' ";
		return sql;
	}

	/**
	 * 查询药品信息-验收用
	 * 
	 * @param supCode
	 *            供应商编码
	 * @param orderCode
	 *            药品代码
	 * @return String
	 */
	public static String getPHABaseInfo(String orderCode, String supCode) {
		return " SELECT A.ORDER_CODE,A.SPECIFICATION,A.RETAIL_PRICE,A.STOCK_PRICE,A.PURCH_UNIT,B.MAN_CODE "
				+ " FROM PHA_BASE A, SYS_FEE B, IND_AGENT C "
				+ " WHERE A.ORDER_CODE = '"
				+ orderCode
				+ "' AND A.ORDER_CODE=B.ORDER_CODE AND C.SUP_CODE='"
				+ supCode
				+ "' AND A.ORDER_CODE=B.ORDER_CODE(+)";
	}

	/**
	 * 查询国药出货单是否已经导入到物联网-验收用
	 * 
	 * @param orderCode
	 *            药品代码
	 * @param boxCode
	 *            货箱条码
	 * @param billNo
	 *            销售单号-在验收表里字段是PURORDER_NO
	 * @param erpId
	 *            国药出货单-ID
	 * @return String
	 */
	public static String getErpIdInfo(String orderCode, String boxCode,
			String billNo, String erpId) {
		String sql = " SELECT COUNT(*) AS COUNT_NUM  FROM IND_VERIFYIND WHERE   ERP_PACKING_ID='"
				+ erpId + "' ";
		if (StringUtils.isNotBlank(orderCode)) {
			sql += " AND ORDER_CODE='" + orderCode + "' ";
		}
		if (StringUtils.isNotBlank(billNo)) {
			sql += " AND PURORDER_NO='" + billNo + "' ";
		}
		if (StringUtils.isNotBlank(boxCode)) {
			sql += " AND SPC_BOX_CODE='" + boxCode + "' ";
		}
		return sql;
	}

	/**
	 * 保存病患-过敏信息
	 * 
	 * @param obj
	 * @return
	 */
	public static String saveOpdDrugallergy(SpcOpdDrugAllergyDto obj) {
		String sql = " INSERT INTO OPD_DRUGALLERGY (MR_NO, ADM_DATE, DRUG_TYPE, DRUGORINGRD_CODE,  "
				+ " ADM_TYPE, CASE_NO, DEPT_CODE, DR_CODE, ALLERGY_NOTE, OPT_USER,    OPT_DATE, OPT_TERM ) 		"
				+ " VALUES "
				+ " ('"
				+ obj.getMrNo()
				+ "', '"
				+ obj.getAdmDate()
				+ "', '"
				+ obj.getDrugType()
				+ "', '"
				+ obj.getDrugoringrdCode()
				+ "',"
				+ " '','', "
				+ " '','',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getAllergyNote())
				+ "', 'ID', " + " sysdate, 'IP') ";
		return sql;
	}

	/**
	 * 根据主键-删除病患过敏信息
	 * 
	 * @param obj
	 * @return
	 */
	public static String deleteOpdDrugallergyById(SpcOpdDrugAllergyDto obj) {
		String sql = " DELETE FROM OPD_DRUGALLERGY WHERE MR_NO='"
				+ obj.getMrNo() + "' AND ADM_DATE='" + obj.getAdmDate() + "' "
				+ " AND DRUG_TYPE='" + obj.getDrugType()
				+ "' AND DRUGORINGRD_CODE='" + obj.getDrugoringrdCode() + "' ";
		return sql;
	}

	/**
	 * 保存-门急诊诊断档-OPD_DIAGREC
	 * 
	 * @param obj
	 * @return
	 */
	public static String saveOpdDiagrec(SpcOpdDiagrecDto obj) {
		String sql = " INSERT INTO OPD_DIAGREC  (CASE_NO, ICD_TYPE, ICD_CODE, MAIN_DIAG_FLG, ADM_TYPE, "
				+ " DIAG_NOTE, DR_CODE, ORDER_DATE, FILE_NO, OPT_USER, OPT_DATE, OPT_TERM ) 		   "
				+ " VALUES "
				+ " ('"
				+ obj.getCaseNo()
				+ "', '"
				+ obj.getIcdType()
				+ "', '"
				+ obj.getIcdCode()
				+ "','N', '',"
				+ " '"
				+ StringUtils.trimToEmpty(obj.getDiagNote())
				+ "','','',''," + " 'ID',sysdate, 'IP')　";
		return sql;
	}

	/**
	 * 删除-门急诊诊断档-OPD_DIAGREC
	 * 
	 * @param obj
	 * @return
	 */
	public static String deleteOpdDiagrec(SpcOpdDiagrecDto obj) {
		String sql = " DELETE OPD_DIAGREC WHERE CASE_NO='" + obj.getCaseNo()
				+ "' AND ICD_TYPE='" + obj.getIcdType() + "'"
				+ " AND ICD_CODE='" + obj.getIcdCode() + "'　";
		return sql;
	}

	/**
	 * 保存-病患基本信息-SYS_PATIONINFO
	 * 
	 * @param obj
	 * @return
	 */
	public static String saveSysPationInfo(SpcSysPatinfoDto obj) {
		String sql = " INSERT INTO SYS_PATINFO  (MR_NO, PAT_NAME, BIRTH_DATE, SEX_CODE, OPT_USER, OPT_DATE, OPT_TERM  )  "
				+ " VALUES "
				+ " ('"
				+ obj.getMrNo()
				+ "', '"
				+ obj.getPatName()
				+ "',"
				+ StringUtils.trimToDatTimeSql(obj.getBirthDate())
				+ ", "
				+ " '" + obj.getSexCode() + "', 'ID',sysdate, 'IP')　";
		return sql;
	}

	/**
	 * 删除-病患基本信息-SYS_PATIONINFO
	 * 
	 * @param obj
	 * @return
	 */
	public static String deleteSysPationInfo(SpcSysPatinfoDto obj) {
		String sql = " DELETE SYS_PATINFO WHERE MR_NO='" + obj.getMrNo() + "'　";
		return sql;
	}

	/**
	 * 删除-病患医嘱信息-OPD_ORDER
	 * 
	 * @param obj
	 * @return
	 */
	public static String deleteOPDOrder(SpcOpdOrderDto obj) {
		String sql = " DELETE OPD_ORDER WHERE CASE_NO='" + obj.getCaseNo()
				+ "'　AND RX_NO='" + obj.getRxNo() + "' ";
		return sql;
	}

	/**
	 * 保存-挂号主档-REG_PATADM
	 * 
	 * @param obj
	 * @return
	 */
	public static String saveRegPatadm(SpcRegPatadmDto obj) {
		String sql = " INSERT INTO REG_PATADM (CASE_NO, MR_NO, REALDEPT_CODE, REALDR_CODE, WEIGHT,HEIGHT, OPT_USER, OPT_DATE, OPT_TERM )  "
				+ " VALUES "
				+ " ('"
				+ obj.getCaseNo()
				+ "', '"
				+ obj.getMrNo()
				+ "','"
				+ obj.getRealdeptCode()
				+ "','"
				+ obj.getRealdrCode()
				+ "',"
				+ " "
				+ obj.getWeight()
				+ ","
				+ obj.getHeight()
				+ ",'ID',sysdate, 'IP')　";
		return sql;
	}

	/**
	 * 删除-挂号主档-REG_PATADM
	 * 
	 * @param obj
	 * @return
	 */
	public static String deleteRegPatadm(SpcRegPatadmDto obj) {
		String sql = " DELETE REG_PATADM WHERE CASE_NO='" + obj.getCaseNo()
				+ "'　";
		return sql;
	}

	/**
	 * 删除-挂号主档-REG_PATADM
	 * 
	 * @param obj
	 * @return
	 */
	public static String queryIndMMStock(TParm parm) {
		String sql = "  SELECT　"
				+ "   ROWID, TRANDATE, ORG_CODE, ORDER_CODE,            "
				+ "   BATCH_SEQ, BATCH_NO, VALID_DATE,                  "
				+ "   REGION_CODE, STOCK_QTY, STOCK_AMT,                "
				+ "   LAST_TOTSTOCK_QTY, LAST_TOTSTOCK_AMT, IN_QTY,     "
				+ "   IN_AMT, OUT_QTY, OUT_AMT,                         "
				+ "   CHECKMODI_QTY, CHECKMODI_AMT, VERIFYIN_QTY,       "
				+ "   VERIFYIN_AMT, FAVOR_QTY, REGRESSGOODS_QTY,        "
				+ "   REGRESSGOODS_AMT, DOSAGE_QTY, DOSAGE_AMT,         "
				+ "   REGRESSDRUG_QTY, REGRESSDRUG_AMT, PROFIT_LOSS_AMT,"
				+ "   VERIFYIN_PRICE, STOCK_PRICE, RETAIL_PRICE,        "
				+ "   TRADE_PRICE, STOCKIN_QTY, STOCKIN_AMT,            "
				+ "   STOCKOUT_QTY, STOCKOUT_AMT, OPT_USER,             "
				+ "   OPT_DATE, OPT_TERM, REQUEST_IN_QTY,               "
				+ "   REQUEST_IN_AMT, REQUEST_OUT_QTY, REQUEST_OUT_AMT, "
				+ "   GIF_IN_QTY, GIF_IN_AMT, GIF_OUT_QTY,              "
				+ "   GIF_OUT_AMT, RET_IN_QTY, RET_IN_AMT,              "
				+ "   RET_OUT_QTY, RET_OUT_AMT, WAS_OUT_QTY,            "
				+ "   WAS_OUT_AMT, THO_OUT_QTY, THO_OUT_AMT,            "
				+ "   THI_IN_QTY, THI_IN_AMT, COS_OUT_QTY,              "
				+ "   COS_OUT_AMT, SUP_CODE                             "
				+ "   FROM IND_MMSTOCK WHERE ";
		return sql;
	}

	/**
	 * 得到药品对应的包药机
	 * 
	 * @param orderCode
	 * @return
	 */
	public static String getOrgCodeOfAtc(String orgCode) {
		String sql = "SELECT ORG_CODE,ATC_ORG_CODE FROM IND_ORG WHERE ATC_ORG_CODE='"
				+ orgCode + "' ";
		return sql;
	}

	/**
	 * 查询药库主档
	 * 
	 * @param orderCode
	 * @return
	 */
	public static String getIndStockQtyAndEleTag(String orgCode,
			String orderCode) {
		String sql = "	SELECT A.ORG_CODE,A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,B.ELETAG_CODE,                 "
				+ "	ROUND(SUM(A.STOCK_QTY)/D.DOSAGE_QTY) AS STOCK_QTY                                          "
				+ "	FROM IND_STOCK A,IND_STOCKM B,PHA_BASE C,PHA_TRANSUNIT D                                   "
				+ "	WHERE A.ORG_CODE='"
				+ orgCode
				+ "' AND A.ORDER_CODE='"
				+ orderCode
				+ "' AND SYSDATE<A.VALID_DATE AND A.ACTIVE_FLG='Y'                    "
				+ "	AND A.ORG_CODE=B.ORG_CODE AND A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE      "
				+ "	AND A.Order_code=d.order_code                                                              "
				+ "	GROUP BY A.ORG_CODE,A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,B.ELETAG_CODE,D.DOSAGE_QTY   ";
		return sql;
	}

	/**
	 * 门急诊配药-修改执行科室-OPD_ORDER
	 * 
	 * @param orderCode
	 * @return
	 */
	public static String updateExecDeptCode(String caseNo, String rxNo,
			String seqNo, String orgCode) {
		String whereSql = " ";
		if (StringUtils.isNotEmpty(seqNo) && seqNo.length() > 0) {
			whereSql = " AND SEQ_NO='" + seqNo + "' ";
		}
		String sql = " UPDATE OPD_ORDER SET EXEC_DEPT_CODE='" + orgCode + "' "
				+ " WHERE CASE_NO='" + caseNo + "' AND RX_NO='" + rxNo + "' "
				+ whereSql;
		return sql;
	}

	/**
	 * 查询验收入库的数据
	 * 
	 * @param verifyinNo
	 * @return
	 */
	public static String getVerifyinSql(String verifyinNo) {
		String sql = " SELECT  ORDER_CODE, BILL_UNIT, VERIFYIN_PRICE, BATCH_NO, VALID_DATE,RETAIL_PRICE,  SUM(VERIFYIN_QTY+GIFT_QTY) AS VERIFYIN_QTY "
				+ " FROM JAVAHIS.IND_VERIFYIND "
				+ "　WHERE VERIFYIN_NO='130117000001' "
				+ " GROUP BY  ORDER_CODE, BILL_UNIT, VERIFYIN_PRICE, BATCH_NO, VALID_DATE,RETAIL_PRICE ";
		return sql;
	}

	/**
	 * 
	 * 根据药库编号,药品代码,批号,有效期,验收价格查询药品的批次序号
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_no
	 * @param valid_date
	 * @param sup_code
	 * @return
	 */
	public static String getMaxBatchSeq(String orgCode, String orderCode,
			String validDate, String price, String batchNo, String supCode) {
		return "SELECT MAX(NVL(BATCH_SEQ,0))+1 AS BATCH_SEQ FROM IND_STOCK "
				+ "WHERE ORG_CODE = '" + orgCode + "' AND ORDER_CODE = '"
				+ orderCode + "' AND BATCH_NO = '" + batchNo
				+ "' AND VALID_DATE = TO_DATE('" + validDate
				+ "','yyyy-MM-dd') " + " AND  " + " AND VERIFYIN_PRICE="
				+ price + " AND SUP_CODE='" + supCode + "' ";
	}

	/**
	 * 查询当前库存
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static String getStockQtyByOrgCodeAndOrderCode(String orgCode,
			String orderCode) {
		String sqlString = " SELECT SUM(STOCK_QTY) AS STOCK_QTY  FROM IND_STOCK  "
				+ " WHERE  ACTIVE_FLG='Y'   AND SYSDATE < VALID_DATE AND "
				+ " ORG_CODE='"
				+ orgCode
				+ "' AND ORDER_CODE='"
				+ orderCode
				+ "' ";
		return sqlString;
	}

	/**
	 * 保存盘点量
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static String updateQtyCheck(double modiQty, double acturlCheckQty,
			String acturlCheckUser, String orgCode, String frozenDate,
			String orderCode, String batchSeq, String optUser, String optTerm) {
		String sqlString = " UPDATE IND_QTYCHECK SET MODI_QTY=" + modiQty
				+ ", ACTUAL_CHECK_QTY=" + acturlCheckQty + ", "
				+ " ACTUAL_CHECKQTY_USER='" + acturlCheckUser + "' "
				+ " WHERE  ORG_CODE='" + orgCode + "' AND FROZEN_DATE='"
				+ frozenDate + "' AND ORDER_CODE='" + orderCode + "' "
				+ " AND BATCH_SEQ=" + batchSeq;
		return sqlString;
	}

	/**
	 * 查询当前库存
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static String getStockQty(String orgCode, String orderCode) {
		String sqlString = " SELECT A.ORDER_CODE,FLOOR(SUM(A.STOCK_QTY)/B.DOSAGE_QTY) || C.UNIT_CHN_DESC || MOD(SUM(A.STOCK_QTY),B.DOSAGE_QTY) || D.UNIT_CHN_DESC AS QTY "
				+ " FROM IND_STOCK A,PHA_TRANSUNIT B,SYS_UNIT C,SYS_UNIT D "
				+ " WHERE  A.ACTIVE_FLG='Y'   AND SYSDATE < A.VALID_DATE   AND A.ORDER_CODE=B.ORDER_CODE AND "
				+ " B.STOCK_UNIT=C.UNIT_CODE AND B.DOSAGE_UNIT=D.UNIT_CODE AND "
				+ " A.ORG_CODE='"
				+ orgCode
				+ "' AND A.ORDER_CODE='"
				+ orderCode
				+ "' "
				+ " GROUP BY A.ORDER_CODE,B.DOSAGE_QTY,C.UNIT_CHN_DESC,D.UNIT_CHN_DESC  "

		;
		return sqlString;
	}
	
	/**
     * 通过ORDER_CODE查询his的order_code
     * @param orderCode
     * @author shendr
     * @return
     */
    public static TParm getOrderCode(String orderCode){
    	String sql = "SELECT HIS_ORDER_CODE " +
    			"FROM SYS_FEE_SPC " +
    			"WHERE ORDER_CODE='"
    			+orderCode+"'";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 判断ORDER_CODE是否存在PHA_BASE,PHA_TRANSUNIT中
     * @param order_code
     * @author shendr
     * @return
     */
    public static TParm queryOrderCodeIsExistPha(String order_code){
    	String sql = "SELECT A.ORDER_CODE FROM PHA_BASE A,PHA_TRANSUNIT B "
					+"WHERE A.ORDER_CODE='"+order_code+"' "
					+"AND A.ORDER_CODE = B.ORDER_CODE";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 查询某一药品是否还有库存
     * @param order_code
     * @author shendr
     * @return
     */
    public static TParm getStockQtyByOrderCode(String order_code){
    	String sql = "SELECT STOCK_QTY FROM IND_STOCK "
					+"WHERE ORDER_CODE='"+order_code+"' "
					+"AND STOCK_QTY>0";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 查询某一药品是否还有请领单未入库
     * @param order_code
     * @author shendr
     * @return
     */
    public static TParm getNotInStock(String order_code){
    	String sql = "SELECT COUNT(ORDER_CODE) AS COUNTS FROM IND_REQUESTD "
					+"WHERE ORDER_CODE = '"+order_code+"' "
					+"AND UPDATE_FLG <> '3'";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
	
}
