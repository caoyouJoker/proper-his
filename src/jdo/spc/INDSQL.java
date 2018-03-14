package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.manager.TCM_Transform;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 药库SQL封装
 * </p>
 * 
 * <p>  
 * Description: 药库SQL封装
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
 * @author zhangy 2009.05.07  
 * @version 1.0
 */

public class INDSQL {

	/**
	 * 药库参数设定
	 * 
	 * @return String
	 */
	public static String getINDSysParm() {
		// <-- 新增3个字段的维护 update by shendr date:2013.5.24
		return "SELECT FIXEDAMOUNT_FLG,REUPRICE_FLG,DISCHECK_FLG,GPRICE_FLG,UNIT_TYPE,"
				+ "GOWN_COSTRATE,GNHI_COSTRATE,GOV_COSTRATE,UPDATE_GRETAIL_FLG,WOWN_COSTRATE,"
				+ "WNHI_COSTRATE,WGOV_COSTRATE,UPDATE_WRETAIL_FLG,MANUAL_TYPE,"
				+ "OPT_USER,AUTO_FILL_TYPE,TOXIC_STORAGE_ORG,IS_AUTO_DRUG,OPT_DATE,OPT_TERM,"
				+ "PHA_PRICE_FLG,TOXIC_LENGTH,TOXBOX_LENGTH,IS_SEPARATE_REQ,MAIN_SUP_CODE FROM IND_SYSPARM";//20150611 wangjc modify 主供应商
		// -->
	}

	/**
	 * 查询药库解锁部门
	 * 
	 * @param batch_flg
	 * @return String
	 */
	public static String getINDBatchLockORG(String batch_flg) {
		return "SELECT ORG_CODE,ORG_CHN_DESC,BATCH_FLG,OPT_USER,OPT_DATE"
				+ ",OPT_TERM FROM IND_ORG WHERE BATCH_FLG = '" + batch_flg
				+ "' ORDER BY SEQ,ORG_CODE";
	}

	// /**
	// * 校验数据
	// *
	// * @param REQUEST_NO 请领单号
	// * @return String
	// */
	// public static String checkData(String REQUEST_NO)
	// {
	// String sql="SELECT REQUEST_NO FROM IND_REQUESTD GROUP BY REQUEST_NO";
	// return sql;
	//    	
	// }

	/**
	 * 毒麻药管制(汇总)
	 */
	public static String getDrugNoM(TParm parm, String sql) {
		return "SELECT 'Y' AS SELECT_FLG, A.REQUEST_NO, A.ORDER_CODE, C.ORDER_DESC,"
				+ "C.SPECIFICATION, A.QTY AS DOSAGE_QTY, D.UNIT_CHN_DESC, A.STOCK_PRICE,"
				+ "A.STOCK_PRICE * A.QTY AS STOCK_AMT, A.RETAIL_PRICE AS OWN_PRICE,"
				+ "A.RETAIL_PRICE * A.QTY AS OWN_AMT,"
				+ "A.RETAIL_PRICE * A.QTY - A.STOCK_PRICE * A.QTY AS DIFF_AMT "
				+ "FROM IND_REQUESTD A, IND_REQUESTM B, SYS_FEE C, SYS_UNIT D,PHA_BASE E "
				+ "WHERE A.REQUEST_NO = B.REQUEST_NO "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.APP_ORG_CODE = "
				+ parm.getData("APP_ORG_CODE")
				+ "AND B.REQUEST_DATE BETWEEN TO_DATE ("
				+ parm.getData("START_DATE")
				+ ", 'YYYYMMDDHH24MISS') "
				+ "AND TO_DATE ("
				+ parm.getData("END_DATE")
				+ ", 'YYYYMMDDHH24MISS') "
				+ sql
				+ "ORDER BY A.REQUEST_NO, A.SEQ_NO";
	}

	/**
	 * 毒麻药管制(明细)
	 */
	public static String getDrugNoD(TParm parm, String sql) {
		return "SELECT 'Y' AS SELECT_FLG,F.NAME,B.OPT_DATE, A.ORDER_CODE, C.ORDER_DESC,"
				+ "C.SPECIFICATION, A.QTY AS DOSAGE_QTY, D.UNIT_CHN_DESC, A.STOCK_PRICE,"
				+ "A.STOCK_PRICE * A.QTY AS STOCK_AMT, A.RETAIL_PRICE AS OWN_PRICE,"
				+ "A.RETAIL_PRICE * A.QTY AS OWN_AMT,"
				+ "A.RETAIL_PRICE * A.QTY - A.STOCK_PRICE * A.QTY AS DIFF_AMT "
				+ "FROM IND_REQUESTD A, IND_REQUESTM B, SYS_FEE C, SYS_UNIT D,PHA_BASE E ,SKT_USER F"
				+ "WHERE A.REQUEST_NO = B.REQUEST_NO "
				+ "AND F.ID=B.OPT_USER"
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.UNIT_CODE = D.UNIT_CODE "
				+ "AND B.TO_ORG_CODE="
				+ parm.getData("TO_ORG_CODE")
				+ "AND B.APP_ORG_CODE = "
				+ parm.getData("APP_ORG_CODE")
				+ "AND B.REQUEST_DATE BETWEEN TO_DATE ("
				+ parm.getData("START_DATE")
				+ ", 'YYYYMMDDHH24MISS') "
				+ "AND TO_DATE ("
				+ parm.getData("END_DATE")
				+ ", 'YYYYMMDDHH24MISS') "
				+ sql
				+ "ORDER BY A.REQUEST_NO, A.SEQ_NO";
	}

	/**
	 * 查询药库部门信息 <br>
	 * 已经加上了多院区(region)
	 * 
	 * @return String
	 */
	public static String getINDORG(String region_code) {
		// <-- 新增3个字段的维护 update by shendr date:2013.5.24
		return "SELECT ORG_CODE,ORG_CHN_DESC,PY1,PY2,SEQ,DESCRIPTION,ORG_FLG,ORG_TYPE,"
				+ "SUP_ORG_CODE,EXINV_FLG,REGION_CODE,STATION_FLG,INJ_ORG_FLG,DECOCT_CODE,"
				+ "ATC_FLG,FIXEDAMOUNT_FLG,AUTO_FILL_TYPE,OPT_USER,OPT_DATE,OPT_TERM,ATC_ORG_CODE,"
				+ "IS_ACCOUNT,IS_SUBORG,BOX_FLG FROM IND_ORG WHERE REGION_CODE = '"
				+ region_code + "'ORDER BY ORG_CODE, SEQ";
		// -->
	}

	/**
	 * 查询药库部门信息
	 * 
	 * @param org_code
	 *            String
	 * @return String
	 */
	public static String getINDORG(String org_code, String region_code) {
		return "SELECT ORG_CODE , ORG_CHN_DESC , PY1 , PY2 , SEQ , "
				+ "DESCRIPTION , ORG_FLG , ORG_TYPE ,SUP_ORG_CODE , EXINV_FLG ,"
				+ " REGION_CODE , STATION_FLG , INJ_ORG_FLG ,DECOCT_CODE , ATC_FLG  ,FIXEDAMOUNT_FLG,AUTO_FILL_TYPE, "
				+ "OPT_USER ,OPT_DATE , OPT_TERM FROM IND_ORG WHERE ORG_CODE ='"
				+ org_code + "' AND REGION_CODE = '" + region_code
				+ "' ORDER BY SEQ, ORG_CODE ";
	}

	/**
	 * 根据条件填充药库部门列表
	 * 
	 * @param condition
	 *            String
	 * @param flg
	 *            String
	 * @return String
	 */
	public static String getIndOrgComobo(String condition, String flg,
			String region_code) {
		String type = "";
		if (!"".equals(condition)) {
			type = " WHERE ORG_TYPE ='" + condition + "'";
		}
		if (!"".equals(flg)) {
			type += " STATION_FLG = '" + flg + "' ";
		}
		return "SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME FROM IND_ORG "
				+ type + " AND REGION_CODE = '" + region_code + "'  "
				+ " ORDER BY ORG_CODE,SEQ";
	}
	
	/**
	 * 根据条件填充药库部门列表
	 * 
	 * @param condition
	 *            String
	 * @param flg
	 *            String
	 * @return String
	 */
	public static String getSPCIndOrgComobo(String condition, String flg,
			String region_code) {
		String type = "";
		if (!"".equals(condition)) {
			type = " WHERE ORG_TYPE " + condition + "";
		}
		if (!"".equals(flg)) {
			type += " STATION_FLG = '" + flg + "' ";
		}
		return "SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME FROM IND_ORG "
				+ type + " AND REGION_CODE = '" + region_code + "'  "
				+ " ORDER BY ORG_CODE,SEQ";
	}
	

	/**
	 * 查询药库部门类别
	 * 
	 * @param org_code
	 *            药库代码
	 * @return String
	 */
	public static String getINDOrgType(String org_code) {
		return "SELECT ORG_TYPE FROM IND_ORG WHERE ORG_CODE = '" + org_code
				+ "'";
	}

	/**
	 * 根据库房类别在科室表中查询药库 添加了多院区代码,region_code
	 * 
	 * @param org_type
	 *            库房类别
	 * @return String
	 */
	public static String getOrgCodeByOrgType(String org_type, String region_code) {
		String sql = "SELECT DEPT_CODE AS ID , DEPT_CHN_DESC AS NAME , PY1, PY2 FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y'";
		if ("C".equals(org_type)) {
			sql += " AND CLASSIFY != '2' AND REGION_CODE='" + region_code
					+ "' ORDER BY SEQ , DEPT_CODE";
		} else {
			sql += " AND CLASSIFY = '2' AND REGION_CODE='" + region_code
					+ "' ORDER BY SEQ , DEPT_CODE";
		}
		return sql;
	}

	/**
	 * 根据库房类别和护士站查询药库
	 * 
	 * @param org_type
	 *            库房类别
	 * @param station_flg
	 *            护士站
	 * @return
	 */
	public static String getOrgCodeByTypeAndStation(String org_type,
			boolean station_flg, String region_code) {
		String sql = "";
		if (!"C".equals(org_type)) {
			sql = "SELECT DEPT_CODE AS ID , DEPT_CHN_DESC AS NAME, PY1, PY2 FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y'"
					+ " AND CLASSIFY = '2'  AND REGION_CODE='"
					+ region_code
					+ "'  ORDER BY SEQ , DEPT_CODE";
		} else if (station_flg) {
			sql = "SELECT STATION_CODE AS ID , STATION_DESC AS NAME, PY1, PY2 FROM SYS_STATION WHERE REGION_CODE='"
					+ region_code + "'";
		} else {
			sql = "SELECT DEPT_CODE AS ID , DEPT_CHN_DESC AS NAME, PY1, PY2 FROM SYS_DEPT WHERE ACTIVE_FLG = 'Y'"
					+ " AND CLASSIFY != '2'  AND REGION_CODE='"
					+ region_code
					+ "' ORDER BY SEQ , DEPT_CODE";
		}
		return sql;
	}

	/**
	 * 查询料位
	 * 
	 * @return String
	 */
	public static String getMaterialloc() {
		return "SELECT ORG_CODE , MATERIAL_LOC_CODE , MATERIAL_CHN_DESC , MATERIAL_ENG_DESC ,PY1 ,"
				+ " PY2 ,SEQ , DESCRIPTION , OPT_USER , OPT_DATE ,ORDER_CODE,ORDER_DESC,ELETAG_CODE,"
				+ " OPT_TERM FROM IND_MATERIALLOC ORDER BY SEQ , ORG_CODE , MATERIAL_LOC_CODE";
	}

	/**
	 * 保存料位
	 * 
	 * @param parm
	 * @return String
	 * @author liyh
	 * @date 20121019
	 */
	public static String saveMaterialLoc(TParm parm) {
		String sql = " INSERT INTO JAVAHIS.IND_MATERIALLOC (ORG_CODE, MATERIAL_LOC_CODE, MATERIAL_CHN_DESC, MATERIAL_ENG_DESC, PY1, PY2, SEQ, "
				+ " DESCRIPTION, OPT_USER, OPT_DATE, OPT_TERM, ORDER_CODE, ELETAG_CODE, ORDER_DESC) VALUES "
				+ " ('"
				+ parm.getValue("ORG_CODE")
				+ "', '"
				+ parm.getValue("MATERIAL_LOC_CODE")
				+ "', '"
				+ parm.getValue("MATERIAL_CHN_DESC")
				+ "', "
				+ " '"
				+ parm.getValue("MATERIAL_ENG_DESC")
				+ "', '"
				+ parm.getValue("PY1")
				+ "', '"
				+ parm.getValue("PY2")
				+ "', "
				+ " "
				+ parm.getValue("SEQ")
				+ ", '"
				+ parm.getValue("DESCRIPTION")
				+ "', '"
				+ parm.getValue("OPT_USER")
				+ "', "
				+ " sysdate, '"
				+ parm.getValue("OPT_TERM")
				+ "', '"
				+ parm.getValue("ORDER_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("ELETAG_CODE")
				+ "', '"
				+ parm.getValue("ORDER_DESC") + "') ";
		return sql;
	}

	/**
	 * 根据药库部门和料位代码查询
	 * 
	 * @param org_code
	 *            药库部门代码
	 * @param material_loc_code
	 *            料位代码
	 * @return String
	 */
	public static String getMaterialloc(String org_code,
			String material_loc_code) {
		return "SELECT ORG_CODE , MATERIAL_LOC_CODE FROM IND_MATERIALLOC"
				+ " WHERE ORG_CODE = '" + org_code
				+ "' AND MATERIAL_LOC_CODE = '" + material_loc_code + "'";
	}

	/**
	 * 查询料位最大序号
	 * 
	 * @return String
	 * @author liyh
	 * @date 20121019
	 */
	public static String getMaxSeqByMaterialLoc() {
		return " SELECT MAX(NVL(SEQ,0)) AS SEQ FROM IND_MATERIALLOC ";
	}

	/**
	 * 根据药库部门查询料位
	 * 
	 * @param org_code
	 *            药库部门
	 * @return String
	 */
	public static String getMaterialloc(String org_code) {
		return "SELECT MATERIAL_LOC_CODE , MATERIAL_CHN_DESC FROM IND_MATERIALLOC"
				+ " WHERE ORG_CODE ='" + org_code + "' ORDER BY SEQ";
	}

	/**
	 * 自动拨补部门
	 * 
	 * @return String
	 */
	public static String getAssignorg() {
		return "SELECT ORG_CODE , CYCLE_TYPE , ASSIGNED_DAY , OPT_USER , OPT_DATE ,"
				+ " OPT_TERM FROM IND_ASSIGNORG ORDER BY ORG_CODE";
	}

	/**
	 * 自动拨补部门
	 * 
	 * @param org_code
	 *            部门代码
	 * @return String
	 */
	public static String getAssignorg(String org_code) {
		return "SELECT ORG_CODE , CYCLE_TYPE FROM IND_ASSIGNORG "
				+ "WHERE ORG_CODE = '" + org_code + "'";
	}

	/**
	 * 药库原因
	 * 
	 * @return String
	 */
	public static String getReason() {
		return "SELECT REASON_CODE , REASON_TYPE , REASON_CHN_DESC , REASON_ENG_DESC ,PY1 ,"
				+ " PY2 ,SEQ , DESCRIPTION , OPT_USER , OPT_DATE ,"
				+ " OPT_TERM FROM IND_REASON ORDER BY SEQ , REASON_CODE";
	}

	/**
	 * 药库原因
	 * 
	 * @param reason_code
	 *            原因代码
	 * @return String
	 */
	public static String getReason(String reason_code) {
		return "SELECT REASON_CODE FROM IND_REASON WHERE REASON_CODE = '"
				+ reason_code + "'";
	}

	/**
	 * 药库原因
	 * 
	 * @param reason_type
	 *            原因类型
	 * @return String
	 */
	public static String getReasonByType(String reason_type) {
		return "SELECT REASON_CODE, REASON_CHN_DESC FROM IND_REASON WHERE REASON_TYPE = '"
				+ reason_type + "' ORDER BY SEQ";
	}

	/**
	 * 查询药库库存主档
	 * 
	 * @return String
	 */
	public static String getINDStockM() {
		return "SELECT ORG_CODE , ORDER_CODE , REGION_CODE , DISPENSE_FLG , "
				+ " DISPENSE_ORG_CODE , QTY_TYPE , MM_USE_QTY , DD_USE_QTY , MATERIAL_LOC_CODE , "
				+ " MAX_QTY , SAFE_QTY ,MIN_QTY , ECONOMICBUY_QTY , BUY_UNRECEIVE_QTY , "
				+ " STANDING_QTY , ACTIVE_FLG ,OPT_USER , OPT_DATE , OPT_TERM "
				+ " FROM IND_STOCKM WHERE ORDER BY ORG_CODE , ORDER_CODE";
	}

	/**
	 * 查询药库库存主档
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStockM(String org_code, String order_code) {
		return "SELECT ORG_CODE , ORDER_CODE , REGION_CODE , DISPENSE_FLG , "
				+ " DISPENSE_ORG_CODE , QTY_TYPE , MM_USE_QTY , DD_USE_QTY , MATERIAL_LOC_CODE , "
				+ " MAX_QTY , SAFE_QTY ,MIN_QTY , ECONOMICBUY_QTY , BUY_UNRECEIVE_QTY , "
				+ " STANDING_QTY , ACTIVE_FLG ,OPT_USER , OPT_DATE , OPT_TERM "
				+ " FROM IND_STOCKM WHERE ORG_CODE = '" + org_code
				+ "' AND ORDER_CODE = '" + order_code + "' ";
	}

	/**
	 * 查询药库库存明细
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStock(String org_code, String order_code,
			String region_code) {
		return "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
				+ "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
				+ "OPT_DATE , OPT_TERM "
				+ "FROM IND_STOCK "
				+ "WHERE ORG_CODE = '"
				+ org_code
				+ "' AND ORDER_CODE = '"
				+ order_code
				+ "' AND ACTIVE_FLG='Y' AND REGION_CODE = '"
				+ region_code + "'  ORDER BY VALID_DATE";
	}

	/**
     * 查询药库库存明细
     * 
     * @param org_code
     *            药库代码
     * @param order_code
     *            药品代码
     * @return String
     */
    public static String getSPCStock(String org_code, String order_code,
            String region_code) {//wanglong add 20150202
        return "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
                + "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
                + "OPT_DATE , OPT_TERM "
                + "FROM SPC_STOCK "
                + "WHERE ORG_CODE = '"
                + org_code
                + "' AND ORDER_CODE = '"
                + order_code
                + "' AND ACTIVE_FLG='Y' AND REGION_CODE = '"
                + region_code + "'  ORDER BY VALID_DATE";
    }
    
	/**
	 * 
	 * @param org_code
	 *            String
	 * @param ordr_code
	 *            String
	 * @param pha_type
	 *            String
	 * @return String
	 */
	public static String getINDStock(String org_code, String ordr_code,
			int batch_seq, String pha_type, String region_code) {
		return " SELECT A.ORDER_CODE, A.VERIFYIN_PRICE, C.OWN_PRICE, C.ORDER_DESC "
				+ " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.ORDER_CODE = C.ORDER_CODE "
				+ " AND A.ORG_CODE = '"
				+ org_code
				+ "' "
				+ " AND A.ORDER_CODE = '"
				+ ordr_code
				+ "' "
				+ " AND A.BATCH_SEQ = "
				+ batch_seq
				+ " AND B.PHA_TYPE = '"
				+ pha_type + "'  AND A.REGION_CODE = '" + region_code + "'  ";
	}

	/**
	 * 查询药库库存明细
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param batch_no
	 *            String
	 * @param valid_date
	 *            Timestamp
	 * @return String
	 */
	public static String getINDStock(String org_code, String order_code,
			String batch_no, String valid_date) {
		return "SELECT * FROM IND_STOCK WHERE ORG_CODE = '" + org_code
				+ "' AND ORDER_CODE = '" + order_code + "' AND BATCH_NO = '"
				+ batch_no + "' AND VALID_DATE = TO_DATE('" + valid_date
				+ "','YYYY-MM-DD') AND ACTIVE_FLG = 'Y' AND STOCK_QTY>0 ";
	}
	
	/**
     * 查询药库库存明细
     * 
     * @param org_code
     *            String
     * @param order_code
     *            String
     * @param batch_no
     *            String
     * @param valid_date
     *            Timestamp
     * @return String
     */
    public static String getSPCStock(String org_code, String order_code,
            String batch_no, String valid_date) {//wanglong add 20150202
        return "SELECT * FROM SPC_STOCK WHERE ORG_CODE = '" + org_code
                + "' AND ORDER_CODE = '" + order_code + "' AND BATCH_NO = '"
                + batch_no + "' AND VALID_DATE = TO_DATE('" + valid_date
                + "','YYYY-MM-DD') AND ACTIVE_FLG = 'Y' AND STOCK_QTY>0 ";
    } 
	
	/**
	 * 查询药库库存明细  包含过期
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param batch_no
	 *            String
	 * @param valid_date
	 *            Timestamp
	 * @return String
	 */
	public static String getINDStockAll(String org_code, String order_code,
			String batch_no, String valid_date) {
		return "SELECT SUM (STOCK_QTY) STOCK_QTY FROM IND_STOCK WHERE ORG_CODE = '" + org_code
				+ "' AND ORDER_CODE = '" + order_code + "' AND BATCH_NO = '"
				+ batch_no + "' AND VALID_DATE = TO_DATE('" + valid_date
				+ "','YYYY-MM-DD') ";
	}
	
	
	public static String getINDStockReturn(String org_code, String order_code,
			String batch_no, String valid_date) {
		
		return	" SELECT SUM (A.STOCK_QTY) / B.DOSAGE_QTY STOCK_QTY "+
			" FROM IND_STOCK A, PHA_TRANSUNIT B "+
			" WHERE     A.ORDER_CODE = B.ORDER_CODE "+
			"         AND A.ORG_CODE = '"+org_code+"'"+ 
			"         AND A.ORDER_CODE = '"+order_code+"'"+
			"         AND A.BATCH_NO = '"+batch_no+"'"+
			"         AND A.VALID_DATE = TO_DATE ('"+valid_date+"', 'YYYY-MM-DD')"+
			"         AND A.ACTIVE_FLG = 'Y' " +
			" GROUP BY A.ORDER_CODE, B.DOSAGE_QTY " ;
	}

	/**
	 * 查询药库库存全部明细
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStockAll(String org_code, String order_code,
			String region_code) {
		return "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
				+ "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
				+ "OPT_DATE , OPT_TERM "
				+ "FROM IND_STOCK "
				+ "WHERE ORG_CODE = '"
				+ org_code
				+ "' AND ORDER_CODE = '"
				+ order_code
				+ "'  AND REGION_CODE = '"
				+ region_code
				+ "' ORDER BY VALID_DATE";
	}

	/**
	 * 查询药库库存全部明细
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStockAllOfStockM(String org_code,
			String order_code, String region_code) {
		// <--- 界面修改 by shendr
		return "SELECT A.ORG_CODE,C.ORDER_DESC,A.ORDER_CODE,A.BATCH_SEQ,A.BATCH_NO, "
				+ "A.VALID_DATE,A.MATERIAL_LOC_CODE,A.ACTIVE_FLG, "
				+ "FLOOR (A.STOCK_QTY / B.DOSAGE_QTY)|| E.UNIT_CHN_DESC|| MOD (A.STOCK_QTY, B.DOSAGE_QTY)|| G.UNIT_CHN_DESC AS STOCK_QTY, "
				+ "A.VERIFYIN_PRICE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,B.STOCK_UNIT AS DOSAGE_UNIT, "
				+ "(SELECT SUP_CHN_DESC "
				+ "   FROM SYS_SUPPLIER D "
				+ "  WHERE A.SUP_CODE = D.SUP_CODE) "
				+ "   AS SUP_CHN_DESC, "
				+ "(CASE WHEN VALID_DATE < SYSDATE THEN 'Y' ELSE 'N' END) AS OUT_FLG "
				+ "FROM IND_STOCK A,PHA_TRANSUNIT B,PHA_BASE C,SYS_UNIT E,SYS_UNIT G "
				+ "WHERE A.STOCK_QTY <> 0 "
				+ "AND A.ORG_CODE = '"
				+ org_code
				+ "' "
				+ "AND A.ORDER_CODE = '"
				+ order_code
				+ "' "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.REGION_CODE = '"
				+ region_code
				+ "' "
				+ "AND G.UNIT_CODE = C.DOSAGE_UNIT "
				+ "AND E.UNIT_CODE = C.STOCK_UNIT " + "ORDER BY A.VALID_DATE";
		// --->
	}

	/**
	 * 查询StockQty
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getStockQtyOfStockM(String org_code, String order_code) {
		// <--- 界面修改 by shendr
		return "SELECT A.ORG_CODE,C.ORDER_DESC,A.ORDER_CODE, "
				+ "FLOOR(SUM(A.STOCK_QTY) / B.DOSAGE_QTY) || E.UNIT_CHN_DESC || MOD(SUM(A.STOCK_QTY),  "
				+ "B.DOSAGE_QTY) || G.UNIT_CHN_DESC AS STOCK_QTY,B.STOCK_UNIT AS DOSAGE_UNIT "
				+ "FROM IND_STOCK A,  PHA_TRANSUNIT B ,PHA_BASE C,SYS_UNIT  E,SYS_UNIT  G  "
				+ "WHERE A.STOCK_QTY<>0  "
				+ "AND A.ORG_CODE = '"
				+ org_code
				+ "' "
				+ "AND A.ORDER_CODE ='"
				+ order_code
				+ "' "
				+ "AND A.ORDER_CODE=C.ORDER_CODE  AND A.ORDER_CODE = B.ORDER_CODE "
				+ " AND G.UNIT_CODE=C.DOSAGE_UNIT AND E.UNIT_CODE=C.STOCK_UNIT  "
				+ " GROUP BY A.ORG_CODE, "
				+ " C.ORDER_DESC, "
				+ " A.ORDER_CODE,B.DOSAGE_QTY,B.STOCK_UNIT,E.UNIT_CHN_DESC ,G.UNIT_CHN_DESC";
		// --->
	}

	/**
	 * 查询药库库存明细
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStock(String org_code, String order_code,
			int batch_seq, String region_code) {
		return "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
				+ "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
				+ "OPT_DATE , OPT_TERM "
				+ "FROM IND_STOCK "
				+ "WHERE ORG_CODE = '"
				+ org_code
				+ "' AND ORDER_CODE = '"
				+ order_code
				+ "' AND BATCH_SEQ="
				+ batch_seq
				+ " AND ACTIVE_FLG='Y' AND REGION_CODE = '"
				+ region_code
				+ "' ";
	}

	/**
	 * 查询药库库存明细
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStock(String org_code, String order_code,
			int batch_seq) {
		return "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
				+ "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE ,INVENT_PRICE, OPT_USER , "
				+ "OPT_DATE , OPT_TERM "
				+ "FROM IND_STOCK "
				+ "WHERE ORG_CODE = '"
				+ org_code
				+ "' AND ORDER_CODE = '"
				+ order_code
				+ "' AND BATCH_SEQ="
				+ batch_seq
				+ " AND ACTIVE_FLG='Y'  ";
	}

	/**
	 * 查询药库库存明细
	 * 
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStock(String order_code, String region_code) {
		String where = "";
		if (!"".equals(region_code)) {
			where = " AND REGION_CODE = '" + region_code + "' ";
		}
		return "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
				+ "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , "
				+ "OPT_USER ,OPT_DATE , OPT_TERM "
				+ "FROM IND_STOCK "
				+ "WHERE ORDER_CODE = '"
				+ order_code
				+ "' AND ACTIVE_FLG='Y' "
				+ where;
	}

	/**
	 * 查询药库库存明细
	 * 
	 * @param org_code
	 *            药库代码
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getINDStockOrderBySeq(String org_code,
			String order_code) {
		return "SELECT ORG_CODE , ORDER_CODE , BATCH_SEQ , BATCH_NO , VALID_DATE , "
				+ "MATERIAL_LOC_CODE , ACTIVE_FLG , STOCK_QTY ,VERIFYIN_PRICE , OPT_USER , "
				+ "OPT_DATE , OPT_TERM "
				+ "FROM IND_STOCK "
				+ "WHERE ORG_CODE = '"
				+ org_code
				+ "' AND ORDER_CODE = '"
				+ order_code + "' AND ACTIVE_FLG='Y'  ORDER BY BATCH_SEQ";
	}

	/**
	 * 查询采购计划明细
	 * 
	 * @param org_code
	 *            药库代码
	 * @param plan_no
	 *            计划单号
	 * @return String
	 */
	public static String getINDPurPlandD(String org_code, String plan_no) {
		return "SELECT ORG_CODE, PLAN_NO, SEQ, ORDER_CODE, PLAN_QTY,"
				+ "PUR_QTY, ACTUAL_QTY, CHECK_QTY, PURCH_UNIT, LASTPUR_QTY,"
				+ "LASTCON_QTY, STOCK_QTY, STOCK_PRICE, SAFE_QTY, MAX_QTY,"
				+ "BUY_UNRECEIVE_QTY, SUP_CODE, START_DATE, END_DATE, OPT_USER,"
				+ "OPT_DATE, OPT_TERM " + "FROM IND_PURPLAND "
				+ "WHERE ORG_CODE = '" + org_code + "' AND PLAN_NO = '"
				+ plan_no + "' ORDER BY SEQ";
	}

	/**
	 * 查询退货明细
	 * 
	 * @param org_code
	 * @param reg_no
	 * @return
	 */
	public static String getINDRegressgoodsD(String reg_no) {
		return " SELECT REGRESSGOODS_NO, SEQ_NO, VERIFYIN_NO, VERSEQ_NO, ORDER_CODE,"
				+ "QTY, BILL_UNIT, UNIT_PRICE, AMT, RETAIL_PRICE,"
				+ "ACTUAL_QTY, BATCH_NO, VALID_DATE, INVOICE_NO, INVOICE_DATE,"
				+ "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE,SUP_ORDER_CODE  "// luhai
				// modify
				// 2012-1-11
				// 加入batchSeq
				// VERIFYIN_PRICE
				+ "FROM IND_REGRESSGOODSD "
				+ "WHERE REGRESSGOODS_NO = '"
				+ reg_no + "' ORDER BY REGRESSGOODS_NO , SEQ_NO";
	}

    /**
     * 查询退货明细
     * 
     * @param org_code
     * @param reg_no
     * @return
     */
    public static String getSPCRegressgoodsD(String reg_no) {//wanglong add 20150202
        return " SELECT REGRESSGOODS_NO, SEQ_NO, VERIFYIN_NO, VERSEQ_NO, ORDER_CODE,"
                + "QTY, BILL_UNIT, UNIT_PRICE, AMT, RETAIL_PRICE,"
                + "ACTUAL_QTY, BATCH_NO, VALID_DATE, INVOICE_NO, INVOICE_DATE,"
                + "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE,SUP_ORDER_CODE  "// luhai
                // modify
                // 2012-1-11
                // 加入batchSeq
                // VERIFYIN_PRICE
                + "FROM SPC_REGRESSGOODSD "
                + "WHERE REGRESSGOODS_NO = '"
                + reg_no + "' ORDER BY REGRESSGOODS_NO , SEQ_NO";
    }
    
	/**
	 * 在订购主档中查询计划单号
	 * 
	 * @param plan_no
	 *            计划单号
	 * @return String
	 */
	public static String getPlanNoInPurorder(String plan_no) {
		if ("".equals(plan_no)) {
			return "";
		}
		return "SELECT PLAN_NO FROM IND_PURORDERM WHERE PLAN_NO = '" + plan_no
				+ "'";
	}

	/**
	 * 在验收入库主档中查询计划单号
	 * 
	 * @param plan_no
	 *            计划单号
	 * @return String
	 */
	public static String getPlanNoInVerifyin(String plan_no) {
		if ("".equals(plan_no)) {
			return "";
		}
		return "SELECT PLAN_NO FROM IND_VERIFYINM WHERE PLAN_NO = '" + plan_no
				+ "'";
	}

	/**
	 * 查询供货厂商药品信息
	 * 
	 * @param sup_code
	 *            供货厂商
	 * @return String
	 */
	public static String getSupOrder(String sup_code, String order_code) {
		if ("".equals(sup_code)) {
			return "";
		}
		String sql = "SELECT A.ORDER_CODE , B.ORDER_DESC ,B.SPECIFICATION ,B.PURCH_UNIT , A.CONTRACT_PRICE "
				+ "FROM IND_AGENT A,PHA_BASE B,SYS_FEE C "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND C.ACTIVE_FLG='Y'"
				+ "AND A.SUP_CODE='"
				+ sup_code + "' ";
		// 药品的查询加入简拼和化学名称 luhai 2012-2-28 begin
		// if (!"".equals(order_code)) {
		// sql += "AND (A.ORDER_CODE LIKE '%" + order_code + "%' "
		// + "OR B.ORDER_DESC LIKE '%" + order_code + "%' "
		// + "OR C.PY1 LIKE '%" + order_code + "%' )";
		// }
		if (!"".equals(order_code)) {
			sql += "AND (A.ORDER_CODE LIKE '%" + order_code + "%' "
					+ "OR B.ORDER_DESC LIKE '%" + order_code + "%' "
					+ "OR C.GOODS_PYCODE LIKE '%" + order_code + "%' "
					+ "OR C.ALIAS_PYCODE LIKE '%" + order_code + "%' "
					+ "OR C.PY1 LIKE '%" + order_code + "%' )";
		}
		// 药品的查询加入简拼和化学名称 luhai 2012-2-28 end

		return sql;
	}

	/**
	 * 查询订购管理明细
	 * 
	 * @return
	 */
	public static String getPurOrderD() {
		return "SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, "
				+ "BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, "
				+ "OPT_USER, OPT_DATE, OPT_TERM FROM IND_PURORDERD ";
	}

	/**
	 * 查询订购管理明细
	 * 
	 * @param purorder_no
	 *            订购单号
	 * @return
	 */
	public static String getPurOrderDByNo(String purorder_no) {
		if ("".equals(purorder_no)) {
			return "";
		}
		return "SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY,  "
				+ "BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG,  "
				+ "OPT_USER, OPT_DATE, OPT_TERM "
				+ "FROM IND_PURORDERD "
				+ "WHERE PURORDER_NO='"
				+ purorder_no
				+ "' "
				+ "ORDER BY SEQ_NO";
	}

	/**
	 * 查询申请管理明细
	 * 
	 * @param request_no
	 * @return
	 */
	public static String getRequestDByNo(String request_no) {
		if ("".equals(request_no)) {
			return "";
		}
		return "SELECT REQUEST_NO, SEQ_NO, ORDER_CODE, BATCH_NO, VALID_DATE, "
				+ "QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, "
				+ "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE "// luhai
				// 2012-01-12
				// add
				// batchSeq
				+ "FROM IND_REQUESTD WHERE REQUEST_NO='" + request_no + "' "
				+ "ORDER BY SEQ_NO";
	}

	/**
     * 查询申请管理明细
     * 
     * @param request_no
     * @return
     */
    public static String getSPCRequestDByNo(String request_no) {//wanglong add 20150202
        if ("".equals(request_no)) {
            return "";
        }
        return "SELECT REQUEST_NO, SEQ_NO, ORDER_CODE, BATCH_NO, VALID_DATE, "
                + "QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, "
                + "UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM,BATCH_SEQ,VERIFYIN_PRICE "// luhai
                // 2012-01-12
                // add
                // batchSeq
                + "FROM SPC_REQUESTD WHERE REQUEST_NO='" + request_no + "' "
                + "ORDER BY SEQ_NO";
    }
    
	/**
	 * 查询药库部门批次过帐注记
	 * 
	 * @param org_code
	 *            药库部门
	 * @return String
	 */
	public static String getIndOrgBatchFlg(String org_code) {
		return "SELECT BATCH_FLG FROM IND_ORG WHERE ORG_CODE='" + org_code
				+ "'";
	}

	/**
	 * 判断是否该药品正在进行调价
	 * 
	 * @param org_code
	 *            药库部门
	 * @param order_code
	 *            药品代码
	 * @return String
	 */
	public static String getIndStockReadjustpFlg(String org_code,
			String order_code) {
		return "SELECT READJUSTP_FLG FROM IND_STOCK WHERE ORG_CODE='"
				+ org_code + "' AND ORDER_CODE='" + order_code + "'";
	}

	/**
	 * 根据药库编号及药品代码查询库存量大于0的药品的批次序号、库存和零售价并以有效期进行排序
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param sort
	 *            String
	 * @return String
	 */
	public static String getIndStockBatchAndQty(String org_code,
			String order_code, String sort) {
		return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE, A.STOCK_QTY AS QTY,"
				+ " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3,B.PHA_TYPE,  "
				+ " A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE,A.SUP_CODE,A.INVENT_PRICE, "
				+ " A.SUP_ORDER_CODE "
				+ " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.ORDER_CODE = C.ORDER_CODE "
				+ " AND B.ORDER_CODE = C.ORDER_CODE " + " AND A.ORG_CODE = '"
				+ org_code + "' AND A.ORDER_CODE = '" + order_code
				+ "' AND A.ACTIVE_FLG='Y' " + " AND SYSDATE < A.VALID_DATE "
				+ " AND A.STOCK_QTY  > 0 " + " ORDER BY A.VALID_DATE " + sort
				+ " , A.BATCH_SEQ";
	}
	
	/**
	 * 根据药库编号及药品代码查询库存量大于0的药品的批次序号、库存和零售价并以有效期进行排序
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String  
	 * @param sort
	 *            String
	 * @return String
	 */
	public static String getIndStockBatchAndQty(String org_code,  
			String order_code, String batch_no,String sort) {
		return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE, A.STOCK_QTY AS QTY,"
				+ " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3,B.PHA_TYPE,  "
				+ " A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE,A.SUP_CODE,A.INVENT_PRICE, "
				+ " A.SUP_ORDER_CODE "
				+ " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "       
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.ORDER_CODE = C.ORDER_CODE "
				+ " AND B.ORDER_CODE = C.ORDER_CODE " + " AND A.ORG_CODE = '"
				+ org_code + "' AND A.ORDER_CODE = '" + order_code 
				//fux modify 加入批号
				+ "' AND A.BATCH_NO = '" + batch_no +"'  AND A.ACTIVE_FLG='Y' " + " AND SYSDATE < A.VALID_DATE "
				+ " AND A.STOCK_QTY  > 0 " + " ORDER BY A.VALID_DATE " + sort
				+ " , A.BATCH_SEQ";
	}
	

	/**
     * 根据药库编号及药品代码查询库存量大于0的药品的批次序号、库存和零售价并以有效期进行排序
     * 
     * @param org_code
     *            String
     * @param order_code
     *            String
     * @param sort
     *            String
     * @return String
     */
    public static String getSPCStockBatchAndQty(String org_code,
            String order_code, String sort) {//wanglong modify 20150202
        return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE, A.STOCK_QTY AS QTY,"
                + " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3,B.PHA_TYPE,  "
                + " A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE,A.SUP_CODE,A.INVENT_PRICE, "
                + " A.SUP_ORDER_CODE "
                + " FROM SPC_STOCK A, PHA_BASE B, SYS_FEE C "
                + " WHERE A.ORDER_CODE = B.ORDER_CODE "
                + " AND A.ORDER_CODE = C.ORDER_CODE "
                + " AND B.ORDER_CODE = C.ORDER_CODE " + " AND A.ORG_CODE = '"
                + org_code + "' AND A.ORDER_CODE = '" + order_code
                + "' AND A.ACTIVE_FLG='Y' " + " AND SYSDATE < A.VALID_DATE "
                + " AND A.STOCK_QTY  > 0 " + " ORDER BY A.VALID_DATE " + sort
                + " , A.BATCH_SEQ";
    }
    
	/**
	 * 根据药库编号及药品代码查询库存和零售价并以有效期进行排序
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param sort
	 *            String
	 * @return String
	 */
	public static String getIndStockQty(String org_code, String order_code,
			String sort) {
		return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,"
				+ " A.STOCK_QTY  AS QTY,"
				+ " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3, "
				+ " B.PHA_TYPE, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE "
				+ " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.ORDER_CODE = C.ORDER_CODE "
				+ " AND B.ORDER_CODE = C.ORDER_CODE " + " AND A.ORG_CODE = '"
				+ org_code + "' AND A.ORDER_CODE = '" + order_code
				+ "' AND A.ACTIVE_FLG='Y' " + " ORDER BY A.VALID_DATE " + sort
				+ " , A.BATCH_SEQ";
	}

	/**
	 * 根据药库编号及药品代码查询药品最近的VALI_DATE和有库存的batch_seq
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param sort
	 *            String
	 * @return String
	 * @date 20120806
	 * @author liyh
	 */
	public static String getIndStockQtyTwo(String org_code, String order_code,
			String sort) {
		return "SELECT A.BATCH_SEQ,A.BATCH_NO,A.VALID_DATE,"
				+ " A.STOCK_QTY  AS QTY,"
				+ " C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, C.OWN_PRICE3, "
				+ " B.PHA_TYPE, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, A.VERIFYIN_PRICE "
				+ " FROM IND_STOCK A, PHA_BASE B, SYS_FEE C "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND A.ORDER_CODE = C.ORDER_CODE "
				+ " AND B.ORDER_CODE = C.ORDER_CODE " + " AND A.ORG_CODE = '"
				+ org_code + "' " + " AND A.ORDER_CODE = '" + order_code + "' "
				+ " AND A.ACTIVE_FLG='Y' AND A.STOCK_QTY > 0"
				+ " ORDER BY A.VALID_DATE DESC , A.BATCH_SEQ DESC ";
	}

	/**
	 * 取得IND_STOCK最大批次序号
	 * 
	 * @param org_code
	 * @param order_code
	 * @return
	 */
	public static String getIndStockMaxBatchSeq(String org_code,
			String order_code) {
		return "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ, MATERIAL_LOC_CODE "
				+ "FROM IND_STOCK WHERE ORG_CODE = '" + org_code
				+ "' AND ORDER_CODE = '" + order_code
				+ "' GROUP BY MATERIAL_LOC_CODE ORDER BY BATCH_SEQ DESC ";
	}


    /**
     * 取得IND_STOCK最大批次序号
     * 
     * @param org_code
     * @param order_code
     * @return
     */
    public static String getSPCStockMaxBatchSeq(String org_code,
            String order_code) {//wanglong add 20150202
        return "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ, MATERIAL_LOC_CODE "
                + "FROM SPC_STOCK WHERE ORG_CODE = '" + org_code
                + "' AND ORDER_CODE = '" + order_code
                + "' GROUP BY MATERIAL_LOC_CODE ORDER BY BATCH_SEQ DESC ";
    }
    
	/**
	 * 根据药库编号,药品代码,批号,有效期查询药品的批次序号
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_no
	 * @param valid_date
	 * @return
	 */
	public static String getIndStockBatchSeq(String org_code,
			String order_code, String batch_no, String valid_date) {
		return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
				+ " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
				+ org_code + "' AND ORDER_CODE = '" + order_code
				+ "' AND BATCH_NO = '" + batch_no
				+ "' AND VALID_DATE = TO_DATE('" + valid_date
				+ "','yyyy-MM-dd') " + "AND ACTIVE_FLG = 'Y' ";
	}
	
	/**
	 * 根据药库编号,药品代码,批号,有效期查询药品的批次序号
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_no
	 * @param valid_date
	 * @return
	 */
	public static String getIndStockBatchSeqAll(String org_code,
			String order_code, String batch_no, String valid_date) {
		return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
				+ " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
				+ org_code + "' AND ORDER_CODE = '" + order_code
				+ "' AND BATCH_NO = '" + batch_no
				+ "' AND VALID_DATE = TO_DATE('" + valid_date
				+ "','yyyy-MM-dd') " ;
	}

	/**
	 * luhai modify 2012-1-30 加入ownPrice的相关信息 根据药库编号,药品代码,批次序号查询药品的批次序号
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_no
	 * @param valid_date
	 * @return
	 */
	public static String getIndStockBatchSeq(String org_code,
			String order_code, String batch_seq) {
		// luhai modify 2012-1-30 加入ownPrice的相关信息 begin
		// return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
		// " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
		// + org_code + "' AND ORDER_CODE = '" + order_code
		// + "' AND BATCH_SEQ = " + batch_seq
		// + " AND ACTIVE_FLG = 'Y' ";
		return "SELECT A.BATCH_SEQ, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
				+ " A.VERIFYIN_PRICE,C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, " 
				+ " C.OWN_PRICE3,A.INVENT_PRICE,A.SUP_CODE,A.SUP_ORDER_CODE  " 
				+ " FROM IND_STOCK A , SYS_FEE C "
				+ "WHERE A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '"
				+ order_code + "' AND A.BATCH_SEQ = " + batch_seq
				+ " AND A.ACTIVE_FLG = 'Y' "
				+ " AND A.ORDER_CODE = C.ORDER_CODE ";
		// luhai modify 2012-1-30 加入ownPrice的相关信息 begin
	}

	/**
     * luhai modify 2012-1-30 加入ownPrice的相关信息 根据药库编号,药品代码,批次序号查询药品的批次序号
     * 
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public static String getSPCStockBatchSeq(String org_code,
            String order_code, String batch_seq) {//wanglong add 20150202
        // luhai modify 2012-1-30 加入ownPrice的相关信息 begin
        // return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
        // " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
        // + org_code + "' AND ORDER_CODE = '" + order_code
        // + "' AND BATCH_SEQ = " + batch_seq
        // + " AND ACTIVE_FLG = 'Y' ";
        return "SELECT A.BATCH_SEQ, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
                + " A.VERIFYIN_PRICE,C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, " 
                + " C.OWN_PRICE3,A.INVENT_PRICE,A.SUP_CODE,A.SUP_ORDER_CODE  " 
                + " FROM SPC_STOCK A , SYS_FEE C "
                + "WHERE A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '"
                + order_code + "' AND A.BATCH_SEQ = " + batch_seq
                + " AND A.ACTIVE_FLG = 'Y' "
                + " AND A.ORDER_CODE = C.ORDER_CODE ";
        // luhai modify 2012-1-30 加入ownPrice的相关信息 begin
    }
    
    /**
     * luhai modify 2012-1-30 加入ownPrice的相关信息 根据药库编号,药品代码,批次序号查询药品的批次序号
     * 
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @return
     */
    public static String getIndStockBatchSeqAcnt(String org_code,
            String order_code, String batch_seq) {//wanglong add 20150202
        // luhai modify 2012-1-30 加入ownPrice的相关信息 begin
        // return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, " +
        // " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
        // + org_code + "' AND ORDER_CODE = '" + order_code
        // + "' AND BATCH_SEQ = " + batch_seq
        // + " AND ACTIVE_FLG = 'Y' ";
        return "SELECT A.BATCH_SEQ, A.RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
                + " A.VERIFYIN_PRICE,C.OWN_PRICE AS RETAIL_PRICE, C.OWN_PRICE2, " 
                + " C.OWN_PRICE3,A.INVENT_PRICE,A.SUP_CODE,A.SUP_ORDER_CODE  " 
                + " FROM SPC_STOCK A , SYS_FEE C "
                + "WHERE A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '"
                + order_code + "' AND A.BATCH_SEQ = " + batch_seq
                + " AND A.ACTIVE_FLG = 'Y' "
                + " AND A.ORDER_CODE = C.ORDER_CODE ";
        // luhai modify 2012-1-30 加入ownPrice的相关信息 begin
    }
    
	/**
	 * 
	 * 根据药库编号,药品代码,批号,有效期,验收价格查询药品的批次序号
	 * 
	 *luhai 2012-01-10 add （药库零差价需求）
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_no
	 * @param valid_date
	 * @return
	 */
	public static String getIndStockBatchSeq(String org_code,
			String order_code, String batch_no, String valid_date,
			String verifyInPrice) {
		return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
				+ " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
				+ org_code + "' AND ORDER_CODE = '" + order_code
				+ "' AND BATCH_NO = '" + batch_no
				+ "' AND VALID_DATE = TO_DATE('" + valid_date
				+ "','yyyy-MM-dd') " + " AND ACTIVE_FLG = 'Y' "
				+ " AND VERIFYIN_PRICE=" + verifyInPrice;
	}
	
	
	/**
	 * 根据药库编号,药品代码,批号,有效期,验收价格、供应商查询药品的批次序号
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_no
	 * @param valid_date
	 * @param verifyInPrice
	 * @param sup_code
	 * @return
	 */
	public static String getIndStockBatchSeqBy(String org_code,
			String order_code, String batch_no, String valid_date,
			String verifyInPrice, String sup_code) {
		return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
				+ " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
				+ org_code + "' AND ORDER_CODE = '" + order_code
				+ "' AND BATCH_NO = '" + batch_no
				+ "' AND VALID_DATE = TO_DATE('" + valid_date
				+ "','yyyy-MM-dd') " + " AND ACTIVE_FLG = 'Y' "
				+ " AND VERIFYIN_PRICE=" + verifyInPrice + " AND SUP_CODE='"
				+ sup_code + "' ";
	}

    /**
     * 根据药库编号,药品代码,批号,有效期,验收价格、供应商查询药品的批次序号
     * 
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @param verifyInPrice
     * @param sup_code
     * @return
     */
    public static String getSpcStockBatchSeqBy(String org_code,
            String order_code, String batch_no, String valid_date,
            String verifyInPrice, String sup_code) {//wanglong add 20150202
        return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
                + " VERIFYIN_PRICE FROM SPC_STOCK " + "WHERE ORG_CODE = '"
                + org_code + "' AND ORDER_CODE = '" + order_code
                + "' AND BATCH_NO = '" + batch_no
                + "' AND VALID_DATE = TO_DATE('" + valid_date
                + "','yyyy-MM-dd') " + " AND ACTIVE_FLG = 'Y' "
                + " AND VERIFYIN_PRICE=" + verifyInPrice + " AND SUP_CODE='"
                + sup_code + "' ";
    }
    
	/**
	 * 根据药库编号,药品代码,批号,有效期,验收价格、供应商查询药品的批次序号
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_no
	 * @param valid_date
	 * @param verifyInPrice
	 * @param sup_code
	 * @return
	 */
	public static String getIndStockBatchSeqBy(String org_code,
			String order_code, String batch_no, String valid_date,
			String verifyInPrice, String sup_code,String supOrderCode) {
		return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
				+ " VERIFYIN_PRICE FROM IND_STOCK " + "WHERE ORG_CODE = '"
				+ org_code + "' AND ORDER_CODE = '" + order_code
				+ "' AND BATCH_NO = '" + batch_no
				+ "' AND VALID_DATE = TO_DATE('" + valid_date
				+ "','yyyy-MM-dd') " + " AND ACTIVE_FLG = 'Y' "
				+ " AND VERIFYIN_PRICE=" + verifyInPrice + " AND SUP_CODE='"
				+ sup_code + "'  AND SUP_ORDER_CODE='"+supOrderCode+"'  ";
	}

	/**
     * 根据药库编号,药品代码,批号,有效期,验收价格、供应商查询药品的批次序号
     * 
     * @param org_code
     * @param order_code
     * @param batch_no
     * @param valid_date
     * @param verifyInPrice
     * @param sup_code
     * @return
     */
    public static String getSPCStockBatchSeqBy(String org_code,
            String order_code, String batch_no, String valid_date,
            String verifyInPrice, String sup_code,String supOrderCode) {//wanglong add 20150202
        return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
                + " VERIFYIN_PRICE FROM SPC_STOCK " + "WHERE ORG_CODE = '"
                + org_code + "' AND ORDER_CODE = '" + order_code
                + "' AND BATCH_NO = '" + batch_no
                + "' AND VALID_DATE = TO_DATE('" + valid_date
                + "','yyyy-MM-dd') " + " AND ACTIVE_FLG = 'Y' "
                + " AND VERIFYIN_PRICE=" + verifyInPrice + " AND SUP_CODE='"
                + sup_code + "'  AND SUP_ORDER_CODE='"+supOrderCode+"'  ";
    }
    
	/**
	 * 
	 * luhai 2012-1-16 modify 得到IND_STOCK信息
	 * 
	 * @param org_code
	 * @param order_code
	 * @param batch_seq
	 * @return
	 */
	public static String getIndStock(String org_code, String order_code,
			String batch_seq) {
		return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
				+ " VERIFYIN_PRICE FROM IND_STOCK " + " WHERE ORG_CODE = '"
				+ org_code + "' AND ORDER_CODE = '" + order_code
				+ "' AND BATCH_SEQ=" + batch_seq + " AND ACTIVE_FLG = 'Y' ";
	}

	/**
     * 
     * luhai 2012-1-16 modify 得到IND_STOCK信息
     * 
     * @param org_code
     * @param order_code
     * @param batch_seq
     * @return
     */
    public static String getSpcStock(String org_code, String order_code,
            String batch_seq) {//wanglong add 20150202
        return "SELECT BATCH_SEQ, RETAIL_PRICE AS STOCK_RETAIL_PRICE, "
                + " VERIFYIN_PRICE FROM SPC_STOCK " + " WHERE ORG_CODE = '"
                + org_code + "' AND ORDER_CODE = '" + order_code
                + "' AND BATCH_SEQ=" + batch_seq + " AND ACTIVE_FLG = 'Y' ";
    }
    
	/**
	 * 查询所有未完成的订购单号
	 * 
	 * @return String
	 */
	public static String getUnDonePurorderNo(String org_code, String sup_code) {
		String sql = "SELECT DISTINCT (A.PURORDER_NO) "
				+ "FROM IND_PURORDERM A , IND_PURORDERD B "
				+ "WHERE A.PURORDER_NO = B.PURORDER_NO " + "AND A.ORG_CODE = '"
				+ org_code + "' "
				+ "AND (B.UPDATE_FLG = '0' OR B.UPDATE_FLG = '1') ";
		if (!"".equals(sup_code))
			sql += "AND A.SUP_CODE = '" + sup_code + "' ";
		sql += "ORDER BY A.PURORDER_NO DESC ";
		return sql;
	}

	/**
	 * 得到验收明细
	 * 
	 * @param verifyin_no
	 *            验收单号
	 * @return String
	 */
	public static String getVerifyinDByNo(String verifyin_no) {
		return "SELECT VERIFYIN_NO, SEQ_NO, PURORDER_NO, PURSEQ_NO, ORDER_CODE, "
				+ "VERIFYIN_QTY, GIFT_QTY, BILL_UNIT, VERIFYIN_PRICE, INVOICE_AMT, "
				+ "INVOICE_NO, INVOICE_DATE, BATCH_NO, VALID_DATE, REASON_CHN_DESC, "
				+ "QUALITY_DEDUCT_AMT, RETAIL_PRICE, ACTUAL_QTY, UPDATE_FLG, OPT_USER, "
				+ "OPT_DATE, OPT_TERM, MAN_CODE,BATCH_SEQ,SPC_BOX_BARCODE,ERP_PACKING_ID,SUP_ORDER_CODE,PRC "// 加入验收时存入库中的BATCH_SEQ
				// luahi
				// 2012-01-01				
				// modify
				+ "FROM IND_VERIFYIND WHERE VERIFYIN_NO='" + verifyin_no + "'";
	}

	/**
	 * 根据供应商和科室查询已完成验收单号
	 * 
	 * @param org_code
	 * @param sup_code
	 * @return
	 */
	public static String getDoneVerifyinByOrgAndSup(String org_code,
			String sup_code) {
		return "SELECT DISTINCT (A.VERIFYIN_NO) "
				+ "FROM IND_VERIFYINM A , IND_VERIFYIND B "
				+ "WHERE A.VERIFYIN_NO = B.VERIFYIN_NO "
				+ "AND (B.UPDATE_FLG = '1' OR B.UPDATE_FLG = '3') "
				// + "AND B.ACTUAL_QTY < B.VERIFYIN_QTY "
				+ "AND A.ORG_CODE='" + org_code + "' AND A.SUP_CODE='"
				+ sup_code + "' ORDER BY A.VERIFYIN_NO DESC ";
	}
	
    /**
     * 根据供应商和科室查询已完成验收单号
     * 
     * @param org_code
     * @param sup_code
     * @return
     */
    public static String getDoneVerifyinByOrgAndSupAcnt(String org_code,
            String sup_code) {//wanglong add 20150423
        return "SELECT DISTINCT (A.VERIFYIN_NO) "
                + "FROM SPC_VERIFYINM A , SPC_VERIFYIND B "
                + "WHERE A.VERIFYIN_NO = B.VERIFYIN_NO "
                + "AND (B.UPDATE_FLG = '1' OR B.UPDATE_FLG = '3') "
                // + "AND B.ACTUAL_QTY < B.VERIFYIN_QTY "
                + "AND A.ORG_CODE='" + org_code + "' AND A.SUP_CODE='"
                + sup_code + "' ORDER BY A.VERIFYIN_NO DESC ";
    }
    
	/**
	 * 出入库数量转换率
	 * 
	 * @param order_code
	 * @return
	 */
	public static String getTransunitByCode(String order_code) {
		return "SELECT STOCK_QTY, DOSAGE_QTY, PURCH_UNIT FROM PHA_TRANSUNIT"
				+ " WHERE ORDER_CODE='" + order_code + "'";
	}

	/**
	 * 根据药品代码获得PHA信息
	 * 
	 * @param order_code
	 * @return
	 */
	public static String getPHAInfoByOrder(String order_code) {
		return "SELECT A.DOSAGE_UNIT, A.STOCK_UNIT, A.PURCH_UNIT, A.RETAIL_PRICE, A.TRADE_PRICE, "
				+ "A.STOCK_PRICE, B.PURCH_QTY, B.STOCK_QTY, B.DOSAGE_QTY, A.PHA_TYPE, B.MEDI_UNIT, B.MEDI_QTY "
				+ "FROM PHA_BASE A, PHA_TRANSUNIT B "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = '"
				+ order_code + "'";
	}

	public static String getIndAgent(String order_code) {
		return "SELECT CONTRACT_PRICE FROM IND_AGENT WHERE ORDER_CODE='"
				+ order_code + "' ORDER BY MAIN_FLG DESC";
	}

	/**
	 * 根据药品代码获得PHA信息修改
	 * 
	 * @param order_code
	 * @return
	 */
	public static String getPHAInfoByOrderNew(String order_code) {
		return "SELECT A.DOSAGE_UNIT, A.STOCK_UNIT, A.PURCH_UNIT, A.RETAIL_PRICE, A.TRADE_PRICE, "
				+ "A.STOCK_PRICE, B.PURCH_QTY, B.STOCK_QTY, B.DOSAGE_QTY, A.PHA_TYPE, B.MEDI_UNIT, B.MEDI_QTY,C.CONTRACT_PRICE "
				+ "FROM PHA_BASE A, PHA_TRANSUNIT B,IND_AGENT C "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.ORDER_CODE = '" + order_code + "'";
	}

	/**
	 * 根据申请单号取得出入库单信息
	 * 
	 * @param request_no
	 * @return
	 */
	public static String getDispenseByReqNo(String request_no) {
		return "SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, "
				+ "TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, "
				+ "WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG, "
				+ "OPT_USER, OPT_DATE, OPT_TERM "
				+ "FROM IND_DISPENSEM "
				+ "WHERE REQUEST_NO = '" + request_no + "' ";
	}

	/**
	 * 通过申请单取得出库单明细
	 * 
	 * @param request_no
	 *            String
	 * @return String
	 */
	public static String getDispenseDByReqNo(String request_no) {
		return "SELECT B.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, B.QTY, B.BATCH_NO, "
				+ "B.VALID_DATE, B.UNIT_CODE, B.RETAIL_PRICE, B.STOCK_PRICE, B.ACTUAL_QTY, "
				+ "B.RETAIL_PRICE * B.QTY AS SUM_RETAIL_PRICE, "
				+ "B.STOCK_PRICE * B.QTY AS SUM_STOCK_PRICE, "
				+ "B.RETAIL_PRICE * B.QTY - B.STOCK_PRICE * B.QTY AS DIFF_SUM ,E.UPDATE_FLG AS END_FLG, A.DISPENSE_NO "
				+ "FROM IND_DISPENSEM A, IND_DISPENSED B, IND_REQUESTM C, PHA_BASE D , IND_REQUESTD E "
				+ "WHERE A.DISPENSE_NO = B.DISPENSE_NO AND A.REQUEST_NO = C.REQUEST_NO "
				+ "AND A.REQTYPE_CODE = C.REQTYPE_CODE AND B.ORDER_CODE = D.ORDER_CODE "
				+ "AND A.REQUEST_NO = E.REQUEST_NO AND B.REQUEST_SEQ = E.SEQ_NO "
				+ "AND C.REQUEST_NO = E.REQUEST_NO AND D.ORDER_CODE = E.ORDER_CODE AND C.REQUEST_NO = '"
				+ request_no + "'";
	}

	/**
	 * 根据出入库单号取得出入库单信息
	 * 
	 * @param dispense_no
	 * @return
	 */
	public static String getDispenseByDisNo(String dispense_no) {
		return "SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE, "
				+ "TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, "
				+ "WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG, "
				+ "OPT_USER, OPT_DATE, OPT_TERM "
				+ "FROM IND_DISPENSEM "
				+ "WHERE DISPENSE_NO = '" + dispense_no + "' ";
	}

	/**
	 * 根据申请单号取得申请单明细信息（取得未完成的细项信息）
	 * 
	 * @param request_no
	 * @return
	 */
	public static String getOutRequestDInfo(String request_no, String update_flg) {
		String sql = "";
		if (!"".equals(update_flg)) {
			sql = "AND UPDATE_FLG <> '" + update_flg + "' ";
		}
		// luhai modify 2012-2-12 将stockPrice改成verifyinPrice begin
		return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
				+ "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
				+ "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
				+ "B.STOCK_PRICE, B.RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
				+ "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
				+ "B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ,A.VERIFYIN_PRICE "// luhai
				// 2012-1-12
				// 加入verifyin_price
				// batch_seq
				+ "FROM IND_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE " + "AND A.REQUEST_NO = '"
				+ request_no + "' " + sql;
	}
	
	/**
     * 根据申请单号取得申请单明细信息（取得未完成的细项信息）
     * 
     * @param request_no
     * @return
     */
    public static String getOutRequestDInfoAcnt(String request_no, String update_flg) {//wanglong add 20150202
        String sql = "";
        if (!"".equals(update_flg)) {
            sql = "AND UPDATE_FLG <> '" + update_flg + "' ";
        }
        // luhai modify 2012-2-12 将stockPrice改成verifyinPrice begin
        return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
                + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
                + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
                + "B.STOCK_PRICE, B.RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
                + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
                + "B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ,A.VERIFYIN_PRICE "// luhai
                // 2012-1-12
                // 加入verifyin_price
                // batch_seq
                + "FROM SPC_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE "
                + "AND A.ORDER_CODE = C.ORDER_CODE " + "AND A.REQUEST_NO = '"
                + request_no + "' " + sql;
    }
    
	/**
	 * 根据申请单号取得申请单明细信息（取得未完成的细项信息）
	 * 
	 * @param request_no
	 * @return
	 */
	public static String getOutRetRequestDInfo(String request_no, String update_flg) {
		String sql = "";
		if (!"".equals(update_flg)) {
			sql = "AND UPDATE_FLG <> '" + update_flg + "' ";
		}
		// luhai modify 2012-2-12 将stockPrice改成verifyinPrice begin
		return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
				+ "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
				+ "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
				+ "A.BATCH_NO, A.VALID_DATE, A.RETAIL_PRICE, "
				+ "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
				+ "B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ,A.VERIFYIN_PRICE "// luhai
				// 2012-1-12
				// 加入verifyin_price
				// batch_seq
				+ "FROM IND_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE " + "AND A.REQUEST_NO = '"
				+ request_no + "' " + sql;
	}

    /**
     * 根据申请单号取得申请单明细信息（取得未完成的细项信息）
     * 
     * @param request_no
     * @return
     */
    public static String getOutRetRequestDInfoAcnt(String request_no, String update_flg) {//wanglong add 20150202
        String sql = "";
        if (!"".equals(update_flg)) {
            sql = "AND UPDATE_FLG <> '" + update_flg + "' ";
        }
        // luhai modify 2012-2-12 将stockPrice改成verifyinPrice begin
        return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
                + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
                + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
                + "A.BATCH_NO, A.VALID_DATE, A.RETAIL_PRICE, "
                + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
                + "B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ,A.VERIFYIN_PRICE "// luhai
                // 2012-1-12
                // 加入verifyin_price
                // batch_seq
                + "FROM SPC_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C "
                + "WHERE A.ORDER_CODE = B.ORDER_CODE "
                + "AND A.ORDER_CODE = C.ORDER_CODE " + "AND A.REQUEST_NO = '"
                + request_no + "' " + sql;
    }
    
	/**
	 * 根据申请单号取得申请单明细信息
	 * 
	 * luhai 2012-1-16 加入与ind_stock 的batch_seq 的关联
	 * 
	 * @param request_no
	 * @return
	 */

	public static String getOutRequestDInfo(String request_no) {
		return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
				+ "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
				+ "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
				+ "E.VERIFYIN_PRICE AS STOCK_PRICE, E.VERIFYIN_PRICE AS VERIFYIN_PRICE,  "
				+ "A.RETAIL_PRICE / C.DOSAGE_QTY AS RETAIL_PRICE,"
				+ " A.BATCH_NO, "
				+ "A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, "
				+ "C.DOSAGE_QTY, B.TRADE_PRICE , A.SEQ_NO AS REQUEST_SEQ,A.BATCH_SEQ "// luhai
				// 2012-01-13add
				// batch_seq
				+ "FROM IND_REQUESTD A, PHA_BASE B, PHA_TRANSUNIT C, "
				+ "IND_REQUESTM D, IND_STOCK E "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.REQUEST_NO = D.REQUEST_NO "
				+ "AND D.APP_ORG_CODE = E.ORG_CODE "
				+ "AND A.ORDER_CODE = E.ORDER_CODE "
				+ "AND A.BATCH_NO = E.BATCH_NO "
				+ "AND A.VALID_DATE = E.VALID_DATE "
				// luhai 2012-01-16 加入batch_seq 的关联 药品零差价
				+ " AND A.BATCH_SEQ=E.BATCH_SEQ  " + "AND A.REQUEST_NO = '"
				+ request_no + "' ";
	}

	/**
	 * 根据出库单号取得出库单明细信息
	 * 
	 * @param dispense_no
	 * @return
	 */
	public static String getIndDispenseDByNo(String dispense_no) {
		// String sql =
		// "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
		// + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
		// + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
		// + "A.STOCK_PRICE / C.STOCK_QTY / C.DOSAGE_QTY AS STOCK_PRICE, "
		// + "A.RETAIL_PRICE / C.STOCK_QTY / C.DOSAGE_QTY "
		// + "AS RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
		// + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
		// + "B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ "
		// + "FROM IND_DISPENSED A , PHA_BASE B , PHA_TRANSUNIT C "
		// + "WHERE A.ORDER_CODE = B.ORDER_CODE "
		// + "AND A.ORDER_CODE = C.ORDER_CODE "
		// + "AND A.DISPENSE_NO = '"
		// + dispense_no + "'";
		String sql = "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
				+ "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
				+ "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
				+ "A.VERIFYIN_PRICE /  C.DOSAGE_QTY AS STOCK_PRICE, "
				+ "A.RETAIL_PRICE / C.DOSAGE_QTY "
				+ "AS RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
				+ "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
				+ "B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ "
				+ "FROM IND_DISPENSED A , PHA_BASE B , PHA_TRANSUNIT C "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.DISPENSE_NO = '"
				+ dispense_no + "'";
		return sql;
	}

	/**
	 * 根据出库单号取得入库单明细信息
	 * 
	 * @param dispense_no
	 * @return
	 */
	public static String getInDispenseDInfo(String dispense_no) {
		return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
				+ "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
				+ "B.SPECIFICATION, A.QTY, E.ACTUAL_QTY, A.ACTUAL_QTY AS OUT_QTY, "
				+ "A.UNIT_CODE, A.STOCK_PRICE  AS STOCK_PRICE ,"
				+ " A.RETAIL_PRICE AS RETAIL_PRICE, A.BATCH_NO, "
				+ "A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, "
				+ "C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ, A.BATCH_SEQ "
				+ "FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C, IND_DISPENSEM D, IND_REQUESTD E "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.DISPENSE_NO = D.DISPENSE_NO "
				+ "AND A.REQUEST_SEQ = E.SEQ_NO "
				+ "AND D.REQUEST_NO = E.REQUEST_NO AND A.DISPENSE_NO = '"
				+ dispense_no + "'";
	}

	/**
	 * 根据出库单号取得入库单明细信息
	 * 
	 * @param dispense_no
	 * @return
	 */
	public static String getInDispenseDInfoSpc(String dispense_no) {

		return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
					+ "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
					+ "B.SPECIFICATION, A.QTY, E.ACTUAL_QTY, A.ACTUAL_QTY AS OUT_QTY, "
					+ "A.UNIT_CODE, A.STOCK_PRICE / C.DOSAGE_QTY AS STOCK_PRICE ,"
					+ "A.RETAIL_PRICE  AS RETAIL_PRICE, A.BATCH_NO, "
					+ "A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, "
					+ "C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ, A.BATCH_SEQ, "
					+ "A.SUP_CODE, A.VERIFYIN_PRICE,A.INVENT_PRICE ,F.ELETAG_CODE ,B.ORDER_DESC AS ORDERDESC,  "
					+ "A.SUP_ORDER_CODE  "
			+ "FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C, IND_DISPENSEM D, IND_REQUESTD E, IND_STOCKM F  "
			+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.DISPENSE_NO = D.DISPENSE_NO "
					+ "AND A.REQUEST_SEQ = E.SEQ_NO "
					+ "AND D.APP_ORG_CODE=F.ORG_CODE "
					+ "AND A.ORDER_CODE=F.ORDER_CODE "
					+ "AND D.REQUEST_NO = E.REQUEST_NO AND A.DISPENSE_NO = '"
					+ dispense_no + "'";
	}

	/**
	 * 盘点查询(全库盘点)
	 * 
	 * @param org_code
	 * @param sort
	 * @return
	 */
	public static String getQtyCheck(String org_code, String sort) {
		String sql = "";
		if ("1".equals(sort)) {
			// 按药品分类排列
			sql = "ORDER BY B.TYPE_CODE";
		} else if ("2".equals(sort)) {
			// 按照药品剂型排列
			sql = "ORDER BY B.DOSE_CODE";
		} else if ("3".equals(sort)) {
			// 按照药品料位排列
			sql = "ORDER BY C.MATERIAL_LOC_CODE";
		} else if ("4".equals(sort)) {
			// 按照药品拼音排列
			sql = "ORDER BY D.PY1";
		}
		return "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
				+ "A.BATCH_SEQ, B.STOCK_UNIT , A.STOCK_QTY AS STOCK_QTY, "
				+ "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
				+ "FROM IND_STOCK A, PHA_BASE B , IND_STOCKM C , SYS_FEE D "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORG_CODE = C.ORG_CODE(+) "
				+ " AND A.ORDER_CODE = C.ORDER_CODE(+)"
				+ "AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND B.ORDER_CODE = D.ORDER_CODE "
				// + "AND A.ACTIVE_FLG = 'Y' "
				+ "AND A.ORG_CODE='" + org_code + "' " + sql;
	}

	/**
	 * 根据盘点时间查询(全库盘点)
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @param sort
	 *            String
	 * @return String
	 */
	public static String getQtyCheck(String org_code, String frozen_date,
			String sort) {
		String sql = "";
		if ("1".equals(sort)) {
			// 按药品分类排列
			sql = "ORDER BY B.TYPE_CODE";
		} else if ("2".equals(sort)) {
			// 按照药品剂型排列
			sql = "ORDER BY B.DOSE_CODE";
		} else if ("3".equals(sort)) {
			// 按照药品料位排列
			sql = "ORDER BY C.MATERIAL_LOC_CODE";
		} else if ("4".equals(sort)) {
			// 按照药品拼音排列
			sql = "ORDER BY D.PY1";
		}
		return "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
				+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
				+ "A.BATCH_SEQ, B.STOCK_UNIT, A.STOCK_QTY "
				+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_MATERIALLOC C, SYS_FEE D "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORG_CODE = C.ORG_CODE(+) "
				+ "AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND B.ORDER_CODE = D.ORDER_CODE "
				+ "AND A.ORG_CODE = '"
				+ org_code
				+ "' AND A.FROZEN_DATE = '"
				+ frozen_date
				+ "' "
				+ sql;
	}

	/**
	 * 盘点查询(抽样盘点--大类抽样盘点)
	 * 
	 * @param order_code
	 * @param sort
	 * @return
	 */
	public static String getQtyCheckTypeB(String org_code, String order_code,
			String sort) {
		String sql = "";
		if ("1".equals(sort)) {
			// 按药品分类排列
			sql = "ORDER BY B.TYPE_CODE";
		} else if ("2".equals(sort)) {
			// 按照药品剂型排列
			sql = "ORDER BY B.DOSE_CODE";
		} else if ("3".equals(sort)) {
			// 按照药品料位排列
			sql = "ORDER BY C.MATERIAL_LOC_CODE";
		} else if ("4".equals(sort)) {
			// 按照药品拼音排列
			sql = "ORDER BY D.PY1";
		}
		return "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
				+ "A.BATCH_SEQ, B.STOCK_UNIT , "
				+ " A.STOCK_QTY AS STOCK_QTY, "
				+ "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
				+ "FROM IND_STOCK A, PHA_BASE B , IND_STOCKM C , SYS_FEE D "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORG_CODE = C.ORG_CODE(+) "
				+ "AND A.ORDER_CODE = C.ORDER_CODE(+) "
				+ "AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND B.ORDER_CODE = D.ORDER_CODE "
				// + "AND A.ACTIVE_FLG = 'Y' "
				+ "AND A.ORG_CODE = '"
				+ org_code
				+ "' "
				+ "AND A.ORDER_CODE LIKE '" + order_code + "%' " + sql;
	}

	/**
	 * 
	 * @param order_code
	 * @param vaild_date
	 * @param sort
	 * @return
	 */
	public static String getQtyCheckTypeC(String org_code, String order_code,
			String valid_date, String sort) {
		String sql = "";
		if (!"".equals(valid_date)) {
			sql = "AND A.VALID_DATE = TO_DATE(" + valid_date
					+ ",'yyyy/MM/dd') ";
		}
		if ("1".equals(sort)) {
			// 按药品分类排列
			sql = "ORDER BY B.TYPE_CODE";
		} else if ("2".equals(sort)) {
			// 按照药品剂型排列
			sql = "ORDER BY B.DOSE_CODE";
		} else if ("3".equals(sort)) {
			// 按照药品料位排列
			sql = "ORDER BY C.MATERIAL_LOC_CODE";
		} else if ("4".equals(sort)) {
			// 按照药品拼音排列
			sql = "ORDER BY D.PY1";
		}
		return "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
				+ "A.BATCH_SEQ, B.STOCK_UNIT , "
				+ " A.STOCK_QTY AS STOCK_QTY, "
				+ "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
				+ "FROM IND_STOCK A, PHA_BASE B , IND_STOCKM C , SYS_FEE D "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORG_CODE = C.ORG_CODE(+) "
				+ "AND A.ORDER_CODE = C.ORDER_CODE(+) "
				+ "AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND B.ORDER_CODE = D.ORDER_CODE "
				// + "AND A.ACTIVE_FLG = 'Y' "
				+ "AND A.ORG_CODE = '"
				+ org_code
				+ "' "
				+ "AND A.ORDER_CODE = '" + order_code + "' " + sql;
	}

	/**
	 * 盘点查询(抽样盘点--大类抽样盘点)
	 * 
	 * @param mac_code
	 * @param sort
	 * @return
	 */
	public static String getQtyCheckTypeD(String org_code, String mac_code,
			String sort) {
		String sql = "";
		if ("1".equals(sort)) {
			// 按药品分类排列
			sql = "ORDER BY B.TYPE_CODE";
		} else if ("2".equals(sort)) {
			// 按照药品剂型排列
			sql = "ORDER BY B.DOSE_CODE";
		} else if ("3".equals(sort)) {
			// 按照药品料位排列
			sql = "ORDER BY C.MATERIAL_LOC_CODE";
		} else if ("4".equals(sort)) {
			// 按照药品拼音排列
			sql = "ORDER BY D.PY1";
		}
		return "SELECT A.STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
				+ "A.BATCH_SEQ, B.STOCK_UNIT , "
				+ " A.STOCK_QTY AS STOCK_QTY, "
				+ "B.DOSAGE_UNIT, B.TRADE_PRICE, B.STOCK_PRICE, B.RETAIL_PRICE , C.MATERIAL_LOC_CODE "
				+ "FROM IND_STOCK A, PHA_BASE B , IND_STOCKM C , SYS_FEE D "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORG_CODE = C.ORG_CODE(+) "
				+ "AND A.ORDER_CODE = C.ORDER_CODE(+) "
				+ "AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND B.ORDER_CODE = D.ORDER_CODE "
				// + "AND A.ACTIVE_FLG = 'Y' "
				+ "AND A.ORG_CODE = '"
				+ org_code
				+ "' "
				+ "AND C.MATERIAL_LOC_CODE = '" + mac_code + "' " + sql;
	}

	/**
	 * 根据药库部门和冻结时间查询盘点历史信息
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getQtyCheckHistoryInfo(String org_code,
			String frozen_date) {
		return "SELECT CASE WHEN Q.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
				+ "END AS STOCK_FLG, Q.ORDER_CODE, S.ORDER_DESC, Q.VALID_DATE, "
				+ "Q.BATCH_NO, Q.BATCH_SEQ, M.MATERIAL_LOC_CODE "
				+ "FROM IND_QTYCHECK Q, SYS_FEE S, IND_STOCK K, IND_MATERIALLOC M "
				+ "WHERE Q.ORDER_CODE = S.ORDER_CODE "
				+ "AND Q.ORDER_CODE = K.ORDER_CODE "
				+ "AND Q.ORG_CODE = K.ORG_CODE "
				+ "AND Q.BATCH_SEQ = K.BATCH_SEQ "
				+ "AND S.ORDER_CODE = K.ORDER_CODE "
				+ "AND K.ORG_CODE = M.ORG_CODE(+) "
				+ "AND K.MATERIAL_LOC_CODE = M.MATERIAL_LOC_CODE(+) "
				+ "AND Q.ORG_CODE = '"
				+ org_code
				+ "' "
				+ "AND Q.FROZEN_DATE = '"
				+ frozen_date
				+ "' "
				+ "ORDER BY ORDER_CODE";
	}

	/**
	 * 取得采建表
	 * 
	 * @param org_code
	 * @param sup_code
	 * @param plan_no
	 * @return
	 */
	public static String getIndPlanAdvice(String org_code, String sup_code,
			String plan_no) {
		return "SELECT 'N' AS SELECT_FLG, A.ORDER_CODE, B.ORDER_DESC, B.SPECIFICATION, "
				+ "A.CHECK_QTY, A.PURCH_UNIT, A.STOCK_PRICE, A.CHECK_QTY * A.STOCK_PRICE AS ATM, A.SUP_CODE,"
				+ "A.PLAN_NO FROM IND_PURPLAND A, PHA_BASE B "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = '"
				+ org_code
				+ "' AND A.SUP_CODE = '"
				+ sup_code
				+ "' AND A.PLAN_NO = '" + plan_no + "'";
	}

	/**
	 * 更新申请单状态为完成
	 * 
	 * @param request_no
	 *            String
	 * @return String
	 */
	public static String onUpdateRequestFlg(String request_no) {
		return "UPDATE IND_REQUESTD SET UPDATE_FLG = '3' WHERE REQUEST_NO = '"
				+ request_no + "' AND ACTUAL_QTY = QTY";
	}

	/**
     * 更新申请单状态为完成
     * 
     * @param request_no
     *            String
     * @return String
     */
    public static String onUpdateRequestFlgAcnt(String request_no) {//wanglong add 20150202
        return "UPDATE SPC_REQUESTD SET UPDATE_FLG = '3' WHERE REQUEST_NO = '"
                + request_no + "' AND ACTUAL_QTY = QTY";
    }
    
	/**
	 * 全库盘点
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @param active_flg
	 *            String
	 * @param valid_date
	 *            String
	 * @param sort
	 *            String
	 * @return String
	 */
	public static String getQtyCheckDataByType0(String org_code,
			String frozen_date, String active_flg, String valid_flg, String sort) {
		// luhai modify 2012-04-25 begin 药品名称加入规格 begin
		// //System.out.println("---");
		// String sql = "";
		// String group = "";
		// if ("N".equals(valid_flg)) {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
		// +
		// "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
		// +
		// "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
		// +
		// "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
		// +
		// "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
		// }
		// else {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
		// +
		// "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
		// + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
		// + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
		// + "|| F.UNIT_CHN_DESC "
		// + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
		// + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
		// +
		// "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
		// group =
		// " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
		// +
		// "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
		// }
		// String where = "";
		// if ("Y".equals(active_flg)) {
		// where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
		// "','YYYYMMDDHH24MISS')  ";
		// }
		// String order = "";
		// if ("1".equals(sort)) {
		// order = " ORDER BY B.TYPE_CODE ";
		// group += " , B.TYPE_CODE ";
		// }
		// else if ("2".equals(sort)) {
		// order = " ORDER BY B.DOSE_CODE ";
		// group += " , B.DOSE_CODE ";
		// }
		// else if ("3".equals(sort)) {
		// order = " ORDER BY C.MATERIAL_LOC_CODE ";
		// }
		// else if ("4".equals(sort)) {
		// order = " ORDER BY D.PY1 ";
		// group += " , D.PY1 ";
		// }
		// //System.out.println("---"+sql + where + group + order);
		// return sql + where + group + order;
		// System.out.println("---");
		String sql = "";
		String group = "";
		if ("N".equals(valid_flg)) {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
					+ "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
					+ "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
					+ "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
					+ "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
		} else {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
					+ "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
					+ "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
					+ "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
					+ "|| F.UNIT_CHN_DESC "
					+ "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
					+ "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
					+ "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '" + frozen_date + "' ";
			group = " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
					+ "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
		}
		String where = "";
		if ("Y".equals(active_flg)) {
			where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date
					+ "','YYYYMMDDHH24MISS')  ";
		}
		String order = "";
		if ("1".equals(sort)) {
			// order = " ORDER BY A.ORDER_CODE ";
			group += " , B.TYPE_CODE ";
		} else if ("2".equals(sort)) {
			// order = " ORDER BY A.ORDER_CODE ";
			group += " , B.DOSE_CODE ";
		} else if ("3".equals(sort)) {
			// order = " ORDER BY A.ORDER_CODE ";
		} else if ("4".equals(sort)) {
			// order = " ORDER BY A.ORDER_CODE ";
			group += " , D.PY1 ";
		}
		order = " ORDER BY A.ORDER_CODE ";
		// System.out.println("-sql   --"+sql + where + group + order);
		return sql + where + group + order;
		// luhai modify 2012-04-25 begin 药品名称加入规格 end
	}

	/**
	 * 盘点查询(抽样盘点--大类抽样盘点)
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @param active_flg
	 *            String
	 * @param valid_date
	 *            String
	 * @param sort
	 *            String
	 * @param order_code
	 *            String
	 * @return String
	 */
	public static String getQtyCheckDataByTypeB(String org_code,
			String frozen_date, String active_flg, String valid_flg,
			String sort, String order_code) {
		// luhai modify 2012-05-25 药品名称后加入规格 begin
		String sql = "";
		String group = "";
		if ("N".equals(valid_flg)) {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
					+ "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
					+ "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
					+ "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
					+ "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '"
					+ frozen_date
					+ "' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
		} else {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
					+ "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
					+ "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
					+ "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
					+ "|| F.UNIT_CHN_DESC "
					+ "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
					+ "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
					+ "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '"
					+ frozen_date
					+ "' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
			group = " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
					+ "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
		}
		String where = "";
		if ("Y".equals(active_flg)) {
			where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date
					+ "','YYYYMMDDHH24MISS') ";
		}
		String order = "";
		if ("1".equals(sort)) {
			order = " ORDER BY B.TYPE_CODE ";
			group += " , B.TYPE_CODE ";
		} else if ("2".equals(sort)) {
			order = " ORDER BY B.DOSE_CODE ";
			group += " , B.DOSE_CODE ";
		} else if ("3".equals(sort)) {
			order = " ORDER BY C.MATERIAL_LOC_CODE ";
		} else if ("4".equals(sort)) {
			order = " ORDER BY D.PY1 ";
			group += " , D.PY1 ";
		}
		return sql + where + group + order;
		// String sql = "";
		// String group = "";
		// if ("N".equals(valid_flg)) {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
		// +
		// "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
		// +
		// "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
		// +
		// "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
		// +
		// "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date +
		// "' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
		// }
		// else {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
		// +
		// "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
		// + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
		// + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
		// + "|| F.UNIT_CHN_DESC "
		// + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
		// + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
		// +
		// "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date +
		// "' AND A.ORDER_CODE LIKE '" + order_code + "%' ";
		// group =
		// " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
		// +
		// "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
		// }
		// String where = "";
		// if ("Y".equals(active_flg)) {
		// where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
		// "','YYYYMMDDHH24MISS') ";
		// }
		// String order = "";
		// if ("1".equals(sort)) {
		// order = " ORDER BY B.TYPE_CODE ";
		// group += " , B.TYPE_CODE ";
		// }
		// else if ("2".equals(sort)) {
		// order = " ORDER BY B.DOSE_CODE ";
		// group += " , B.DOSE_CODE ";
		// }
		// else if ("3".equals(sort)) {
		// order = " ORDER BY C.MATERIAL_LOC_CODE ";
		// }
		// else if ("4".equals(sort)) {
		// order = " ORDER BY D.PY1 ";
		// group += " , D.PY1 ";
		// }
		// return sql + where + group + order;
		// luhai modify 2012-05-25 药品名称后加入规格 end
	}

	/**
	 * 盘点查询(抽样盘点--随机抽样盘点)
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @param active_flg
	 *            String
	 * @param valid_flg
	 *            String
	 * @param sort
	 *            String
	 * @param order_code
	 *            String
	 * @param valid_date
	 *            String
	 * @return String
	 */
	public static String getQtyCheckDataByTypeC(String org_code,
			String frozen_date, String active_flg, String valid_flg,
			String sort, String order_code, String valid_date) {
		// luhai modify 加入规格 2012-04-24 begin
		// String sql = "";
		// String group = "";
		// if ("N".equals(valid_flg)) {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
		// +
		// "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
		// +
		// "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
		// +
		// "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
		// +
		// "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date +
		// "' AND A.ORDER_CODE = '" + order_code + "' ";
		// }
		// else {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
		// +
		// "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
		// + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
		// + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
		// + "|| F.UNIT_CHN_DESC "
		// + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
		// + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
		// +
		// "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date +
		// "' AND A.ORDER_CODE = '" + order_code + "' ";
		// group =
		// " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
		// +
		// "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
		//
		// }
		// String where = "";
		// if ("Y".equals(active_flg)) {
		// where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
		// "','YYYYMMDDHH24MISS') ";
		// }
		// if (!"".equals(valid_date)) {
		// where += " AND A.VALID_DATE=TO_DATE('" + valid_date +
		// "','YYYYMMDDHH24MISS') ";
		// }
		// String order = "";
		// if ("1".equals(sort)) {
		// order = " ORDER BY B.TYPE_CODE ";
		// group += " , B.TYPE_CODE ";
		// }
		// else if ("2".equals(sort)) {
		// order = " ORDER BY B.DOSE_CODE ";
		// group += " , B.DOSE_CODE ";
		// }
		// else if ("3".equals(sort)) {
		// order = " ORDER BY C.MATERIAL_LOC_CODE ";
		// }
		// else if ("4".equals(sort)) {
		// order = " ORDER BY D.PY1 ";
		// group += " , D.PY1 ";
		// }
		// return sql + where + group + order;
		String sql = "";
		String group = "";
		if ("N".equals(valid_flg)) {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
					+ "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
					+ "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
					+ "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
					+ "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '"
					+ frozen_date
					+ "' AND A.ORDER_CODE = '" + order_code + "' ";
		} else {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE,B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
					+ "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
					+ "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
					+ "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
					+ "|| F.UNIT_CHN_DESC "
					+ "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
					+ "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
					+ "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '"
					+ frozen_date
					+ "' AND A.ORDER_CODE = '" + order_code + "' ";
			group = " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
					+ "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";

		}
		String where = "";
		if ("Y".equals(active_flg)) {
			where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date
					+ "','YYYYMMDDHH24MISS') ";
		}
		if (!"".equals(valid_date)) {
			where += " AND A.VALID_DATE=TO_DATE('" + valid_date
					+ "','YYYYMMDDHH24MISS') ";
		}
		String order = "";
		if ("1".equals(sort)) {
			order = " ORDER BY B.TYPE_CODE ";
			group += " , B.TYPE_CODE ";
		} else if ("2".equals(sort)) {
			order = " ORDER BY B.DOSE_CODE ";
			group += " , B.DOSE_CODE ";
		} else if ("3".equals(sort)) {
			order = " ORDER BY C.MATERIAL_LOC_CODE ";
		} else if ("4".equals(sort)) {
			order = " ORDER BY D.PY1 ";
			group += " , D.PY1 ";
		}
		return sql + where + group + order;
		// luhai modify 加入规格 2012-04-24 begin
	}

	/**
	 * 盘点查询(抽样盘点--料位抽样盘点)
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @param active_flg
	 *            String
	 * @param valid_flg
	 *            String
	 * @param sort
	 *            String
	 * @param mat_code
	 *            String
	 * @return String
	 */
	public static String getQtyCheckDataByTypeD(String org_code,
			String frozen_date, String active_flg, String valid_flg,
			String sort, String mat_code) {
		// luhai modify 2012-04-25 加入规格 begin
		// String sql = "";
		// String group = "";
		// if ("N".equals(valid_flg)) {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
		// +
		// "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
		// +
		// "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
		// +
		// "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
		// +
		// "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date +
		// "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
		// }
		// else {
		// sql =
		// "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
		// +
		// "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, '' AS VALID_DATE, "
		// +
		// "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
		// + "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
		// + "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
		// + "|| F.UNIT_CHN_DESC "
		// + "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
		// + "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
		// +
		// "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
		// +
		// "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
		// +
		// "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// +
		// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
		// +
		// "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
		// +
		// "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + "AND A.ORG_CODE = '" + org_code
		// + "' AND A.FROZEN_DATE = '" + frozen_date +
		// "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
		// group =
		// " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
		// +
		// "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";
		//
		// }
		// String where = "";
		// if ("Y".equals(active_flg)) {
		// where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date +
		// "','YYYYMMDDHH24MISS') ";
		// }
		// String order = "";
		// if ("1".equals(sort)) {
		// order = " ORDER BY B.TYPE_CODE ";
		// group += " , B.TYPE_CODE ";
		// }
		// else if ("2".equals(sort)) {
		// order = " ORDER BY B.DOSE_CODE ";
		// group += " , B.DOSE_CODE ";
		// }
		// else if ("3".equals(sort)) {
		// order = " ORDER BY C.MATERIAL_LOC_CODE ";
		// }
		// else if ("4".equals(sort)) {
		// order = " ORDER BY D.PY1 ";
		// group += " , D.PY1 ";
		// }
		// return sql + where + group + order;
		String sql = "";
		String group = "";
		if ("N".equals(valid_flg)) {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
					+ "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
					+ "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
					+ "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR ((A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
					+ "A.STOCK_UNIT AS STOCK_UNIT_A, MOD ((A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '"
					+ frozen_date
					+ "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
		} else {
			sql = "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
					+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC||'('||D.SPECIFICATION||')' AS ORDER_DESC, '' AS VALID_DATE, "
					+ "'' AS BATCH_NO, C.MATERIAL_LOC_CODE, SUM (A.MODI_QTY) AS MODI_QTY, "
					+ "SUM (A.STOCK_QTY) AS STOCK_QTY_F, "
					+ "FLOOR (SUM (A.STOCK_QTY) / E.DOSAGE_QTY) "
					+ "|| F.UNIT_CHN_DESC "
					+ "|| MOD (SUM (A.STOCK_QTY), E.DOSAGE_QTY) "
					+ "|| G.UNIT_CHN_DESC AS STOCK_QTY_M, "
					+ "FLOOR (SUM (A.STOCK_QTY + A.MODI_QTY) / E.DOSAGE_QTY) AS ACTUAL_QTY_F, A.STOCK_UNIT AS STOCK_UNIT_A, MOD (SUM (A.STOCK_QTY + A.MODI_QTY), E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
					+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.DOSAGE_QTY, '' AS BATCH_SEQ "
					+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
					+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.FROZEN_DATE = '"
					+ frozen_date
					+ "' AND C.MATERIAL_LOC_CODE = '" + mat_code + "' ";
			group = " GROUP BY A.UNFREEZE_DATE, A.ORDER_CODE, B.ORDER_DESC, C.MATERIAL_LOC_CODE, "
					+ "A.STOCK_UNIT, A.DOSAGE_UNIT, E.DOSAGE_QTY, E.STOCK_QTY, F.UNIT_CHN_DESC, G.UNIT_CHN_DESC";

		}
		String where = "";
		if ("Y".equals(active_flg)) {
			where = " AND A.VALID_DATE>=TO_DATE('" + frozen_date
					+ "','YYYYMMDDHH24MISS') ";
		}
		String order = "";
		if ("1".equals(sort)) {
			order = " ORDER BY B.TYPE_CODE ";
			group += " , B.TYPE_CODE ";
		} else if ("2".equals(sort)) {
			order = " ORDER BY B.DOSE_CODE ";
			group += " , B.DOSE_CODE ";
		} else if ("3".equals(sort)) {
			order = " ORDER BY C.MATERIAL_LOC_CODE ";
		} else if ("4".equals(sort)) {
			order = " ORDER BY D.PY1 ";
			group += " , D.PY1 ";
		}
		return sql + where + group + order;
		// luhai modify 2012-04-25 加入规格 end
	}

	/**
	 * 其余方式
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getQtyCheckDataByTypeOther(String org_code,
			String frozen_date) {
		return "SELECT CASE WHEN A.UNFREEZE_DATE IS NOT NULL THEN 'N' ELSE 'Y' "
				+ "END AS STOCK_FLG, A.ORDER_CODE, B.ORDER_DESC, A.VALID_DATE, A.BATCH_NO, "
				+ "C.MATERIAL_LOC_CODE, A.MODI_QTY, A.STOCK_QTY AS STOCK_QTY_F, "
				+ "FLOOR (A.STOCK_QTY / E.DOSAGE_QTY)||F.UNIT_CHN_DESC||MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
				+ "||G.UNIT_CHN_DESC AS STOCK_QTY_M, FLOOR (A.STOCK_QTY / E.DOSAGE_QTY + A.MODI_QTY / E.DOSAGE_QTY) AS ACTUAL_QTY_F, "
				+ "A.STOCK_UNIT AS STOCK_UNIT_A, MOD (A.STOCK_QTY + A.MODI_QTY, E.DOSAGE_QTY) AS ACTUAL_QTY_M, "
				+ "A.DOSAGE_UNIT AS DOSAGE_UNIT_A, E.STOCK_QTY, E.DOSAGE_QTY, A.BATCH_SEQ "
				+ "FROM IND_QTYCHECK A, PHA_BASE B, IND_STOCK C, SYS_FEE D, PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE = C.ORG_CODE AND A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.BATCH_SEQ = C.BATCH_SEQ AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
				+ "AND B.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = E.ORDER_CODE "
				+ "AND C.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = E.ORDER_CODE AND D.ORDER_CODE = E.ORDER_CODE "
				+ "AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
				+ "AND A.ORG_CODE = '"
				+ org_code
				+ "' AND A.FROZEN_DATE = '"
				+ frozen_date + "' ORDER BY A.ORDER_CODE ";
	}

	/**
	 * 取盘点作业中指定时间，指定部门，指定药品的最大批次序号
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getMaxSeqByQtyCheck(String org_code,
			String order_code, String frozen_date) {
		return "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM IND_QTYCHECK WHERE ORG_CODE='"
				+ org_code
				+ "' AND ORDER_CODE='"
				+ order_code
				+ "' AND FROZEN_DATE='" + frozen_date + "'";
	}

	/**
	 * 取盘点作业中指定时间，指定部门，指定药品的批次序号和调整量
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getQtyCheckBatchSeqAndModi(String org_code,
			String order_code, String frozen_date) {
		return "SELECT A.ORDER_CODE, A.BATCH_SEQ, A.MODI_QTY , A.MODI_QTY * B.RETAIL_PRICE AS MODI_ATM "
				+ "FROM IND_QTYCHECK A, PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE='"
				+ org_code
				+ "' AND A.ORDER_CODE='"
				+ order_code
				+ "' AND A.FROZEN_DATE='" + frozen_date + "'";
	}

	/**
	 * 取盘点作业中指定时间，指定部门，指定药品，指定批次序号的调整量,调整金额
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getQtyCheckBatchSeqAndModi(String org_code,
			String order_code, String frozen_date, int batch_seq) {
		return "SELECT A.ORDER_CODE, A.MODI_QTY , A.MODI_QTY * B.RETAIL_PRICE AS MODI_ATM "
				+ "FROM IND_QTYCHECK A, PHA_BASE B WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORG_CODE='"
				+ org_code
				+ "' AND A.ORDER_CODE='"
				+ order_code
				+ "' AND A.FROZEN_DATE='"
				+ frozen_date
				+ "' AND A.BATCH_SEQ="
				+ batch_seq;
	}

	/**
	 * 根据药品代码取得药品信息，用于打印
	 * 
	 * @return String
	 */
	public static String getOrderInfoByCode(String order_code, String sup_code,
			String type) {
		if ("PUR".equals(type) || "REG".equals(type)) {
			// luhai modify 2012-04-26 begin
			// // 打印订购单和退货单
			// return "SELECT A.ORDER_CODE, "
			// +
			// "CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
			// +
			// "D.SPECIFICATION, C.UNIT_CHN_DESC, A.PHA_TYPE , E.MAN_CHN_DESC , D.GOODS_DESC "
			// +
			// "FROM PHA_BASE A, IND_AGENT B, SYS_UNIT C ,SYS_FEE D, SYS_MANUFACTURER E "
			// +
			// "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.PURCH_UNIT = C.UNIT_CODE "
			// +
			// "AND A.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) "
			// + "AND A.ORDER_CODE ='" + order_code
			// + "' AND B.SUP_CODE='" + sup_code + "'";
			// 打印订购单和退货单
			return "SELECT A.ORDER_CODE, "
					+ " D.ORDER_DESC  AS ORDER_DESC, "
					+
					// ===zhangp 20120706 start
					// "D.SPECIFICATION, C.UNIT_CHN_DESC, A.PHA_TYPE , E.MAN_CHN_DESC , D.GOODS_DESC "
					"D.SPECIFICATION, C.UNIT_CHN_DESC, A.PHA_TYPE , D.MAN_CODE MAN_CHN_DESC, D.GOODS_DESC "
					+
					// "FROM PHA_BASE A, IND_AGENT B, SYS_UNIT C ,SYS_FEE D, SYS_MANUFACTURER E "
					"FROM PHA_BASE A, IND_AGENT B, SYS_UNIT C ,SYS_FEE D "
					// ===zhangp 20120706 end
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE(+) AND A.PURCH_UNIT = C.UNIT_CODE "
					+
					// ===zhangp 20120706 start
					// "AND A.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) "
					"AND A.ORDER_CODE = D.ORDER_CODE "
					// ===zhangp 20120706 end
					+ "AND A.ORDER_CODE ='" + order_code + "' ";// 去掉
			// sup_code查询条件
			// ind_agent右连接
			// by
			// liyh20121026
			// luhai modify 2012-04-26 end
		} else if ("VER".equals(type)) {
			// 打印验收入库单
			// luhai modify 2012-04-27 begin 验收入库单删除商品名
			// return
			// "SELECT A.ORDER_CODE, "
			// +
			// "CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
			// + "D.SPECIFICATION, B.UNIT_CHN_DESC, "
			// +
			// "D.OWN_PRICE * C.STOCK_QTY AS OWN_PRICE, A.PHA_TYPE, E.MAN_CHN_DESC, A.BID_FLG , D.GOODS_DESC "
			// +
			// "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, SYS_MANUFACTURER E ,IND_AGENT F "
			// +
			// "WHERE A.PURCH_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE AND A.ORDER_CODE = D.ORDER_CODE "
			// +
			// "AND C.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) AND A.ORDER_CODE = F.ORDER_CODE "
			// +
			// "AND C.ORDER_CODE = F.ORDER_CODE AND D.ORDER_CODE = F.ORDER_CODE AND A.ORDER_CODE ='"
			// + order_code + "' AND F.SUP_CODE='" + sup_code + "'";
			return "SELECT A.ORDER_CODE, "
					+ " D.ORDER_DESC  AS ORDER_DESC, "
					+ "D.SPECIFICATION, B.UNIT_CHN_DESC, "
					+
					// ===zhangp 20120706 start
					// "D.OWN_PRICE * C.STOCK_QTY AS OWN_PRICE, A.PHA_TYPE, E.MAN_CHN_DESC, A.BID_FLG , D.GOODS_DESC "
					"D.OWN_PRICE * C.STOCK_QTY AS OWN_PRICE, A.PHA_TYPE, D.MAN_CODE MAN_CHN_DESC, A.BID_FLG , D.GOODS_DESC "
					+
					// "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, SYS_MANUFACTURER E ,IND_AGENT F "
					"FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, IND_AGENT F "
					// ===zhangp 20120706 end
					+ "WHERE A.PURCH_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE AND A.ORDER_CODE = D.ORDER_CODE "
					// ===zhangp 20120706 start
					// +
					// "AND C.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) AND A.ORDER_CODE = F.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = F.ORDER_CODE "
					// ===zhangp 20120706 end
					+ "AND C.ORDER_CODE = F.ORDER_CODE AND D.ORDER_CODE = F.ORDER_CODE AND A.ORDER_CODE ='"
					+ order_code + "' AND F.SUP_CODE='" + sup_code + "'";
			// luhai modify 2012-04-27 end
		} else if ("PLAN".equals(type)) {
			// 打印采购计划单
			return "SELECT A.ORDER_CODE, "
					+ "CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
					+ "D.SPECIFICATION, B.UNIT_CHN_DESC, "
					// ===zhangp 20120706 start
					// +
					// "F.CONTRACT_PRICE,D.OWN_PRICE,G.SUP_CHN_DESC,E.MAN_CHN_DESC,"
					+ "F.CONTRACT_PRICE,D.OWN_PRICE,G.SUP_CHN_DESC,D.MAN_CODE MAN_CHN_DESC,"
					// ===zhangp 20120706 end
					+ "C.DOSAGE_QTY , D.GOODS_DESC "
					// ===zhangp 20120706 start
					// +
					// "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, SYS_MANUFACTURER E, IND_AGENT F, SYS_SUPPLIER G "
					+ "FROM PHA_BASE A, SYS_UNIT B, PHA_TRANSUNIT C, SYS_FEE D, IND_AGENT F, SYS_SUPPLIER G "
					// ===zhangp 20120706 end
					+ "WHERE A.PURCH_UNIT = B.UNIT_CODE AND A.ORDER_CODE = C.ORDER_CODE AND A.ORDER_CODE = D.ORDER_CODE "
					// ===zhangp 20120706 start
					// +
					// "AND C.ORDER_CODE = D.ORDER_CODE AND D.MAN_CODE = E.MAN_CODE(+) AND A.ORDER_CODE = F.ORDER_CODE "
					+ "AND C.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = F.ORDER_CODE "
					// ===zhangp 20120706 end
					+ "AND C.ORDER_CODE = F.ORDER_CODE AND D.ORDER_CODE = F.ORDER_CODE AND A.SUP_CODE = G.SUP_CODE "
					+ "AND A.ORDER_CODE ='" + order_code + "' AND F.SUP_CODE='"
					+ sup_code + "'";
		}
		return "";
	}

	/**
	 * 根据药品代码取得药品信息，用于打印
	 * 
	 * @return String
	 */
	public static String getOrderInfoByCode(String order_code, String unit_type) {
		// 打印申请单,出库单
		String unit = "";
		if ("0".equals(unit_type)) {
			unit = "A.STOCK_UNIT";
		} else {
			unit = "A.DOSAGE_UNIT";
		}
		return "SELECT A.ORDER_CODE , A.ORDER_DESC , A.SPECIFICATION , B.UNIT_CHN_DESC , A.GOODS_DESC FROM PHA_BASE A,SYS_UNIT B "
				+ "WHERE "
				+ unit
				+ " = B.UNIT_CODE AND A.ORDER_CODE ='"
				+ order_code + "'";
	}

	/**
	 * 药品差异分析
	 * 
	 * @param plan_no
	 *            String
	 * @return String
	 */
	public static String getOrderInfoByPlan(String plan_no) {
		return " SELECT C.SUP_CHN_DESC, "
				+ " CASE WHEN D.GOODS_DESC IS NULL THEN D.ORDER_DESC ELSE "
				+ " D.ORDER_DESC || '(' || D.GOODS_DESC || ')' END AS ORDER_DESC, "
				+ " D.SPECIFICATION, E.UNIT_CHN_DESC,"
				+ " A.CHECK_QTY AS PLAN_QTY, A.STOCK_PRICE AS PLAN_PRICE,"
				+ " NVL (H.VERIFYIN_QTY, 0) AS IN_QTY,"
				+ " NVL (H.VERIFYIN_PRICE, 0) AS IN_PRICE,"
				+ " A.CHECK_QTY * A.STOCK_PRICE AS PLAN_AMT,"
				+ " NVL (H.VERIFYIN_QTY * H.VERIFYIN_PRICE, 0) AS IN_AMT,"
				+ " NVL (H.VERIFYIN_QTY - A.CHECK_QTY, 0) AS DIFF_QTY,"
				+ " NVL (H.VERIFYIN_QTY * H.VERIFYIN_PRICE - "
				+ " A.CHECK_QTY * A.STOCK_PRICE,0) AS DIFF_AMT"
				+ " FROM IND_PURPLAND A,IND_PURPLANM B,SYS_SUPPLIER C,SYS_FEE D, "
				+ " SYS_UNIT E, IND_PURORDERM F,IND_PURORDERD G, IND_VERIFYIND H"
				+ " WHERE A.PLAN_NO = B.PLAN_NO"
				+ " AND A.SUP_CODE = C.SUP_CODE"
				+ " AND A.ORDER_CODE = D.ORDER_CODE"
				+ " AND A.PURCH_UNIT = E.UNIT_CODE"
				+ " AND A.PLAN_NO = F.PLAN_NO"
				+ " AND A.ORDER_CODE = G.ORDER_CODE"
				+ " AND B.PLAN_NO = F.PLAN_NO"
				+ " AND F.PURORDER_NO = G.PURORDER_NO"
				+ " AND G.PURORDER_NO = H.PURORDER_NO(+)"
				+ " AND G.SEQ_NO = H.PURSEQ_NO(+)" + " AND A.PLAN_NO = '"
				+ plan_no + "'";
	}

	/**
	 * 查询药品的批次和效期
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param unit_type
	 *            String
	 * @return String
	 */
	public static String getOrderBatchNoValid(String org_code,
			String order_code, String unit_type) {
		// luhai modify 2012-3-16 根据unittype进行处理
		// return
		// "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
		// + "A.BATCH_SEQ, A.STOCK_QTY, B.STOCK_UNIT, "
		// +
		// "A.RETAIL_PRICE * C.STOCK_QTY * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE "
		// + "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
		// + "WHERE A.ORDER_CODE = B.ORDER_CODE " +
		// "AND A.ORDER_CODE = C.ORDER_CODE "
		// + "AND B.ORDER_CODE = C.ORDER_CODE "
		// + "AND A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '" +
		// order_code + "' AND A.ACTIVE_FLG = 'Y'";
		if ("0".equals(unit_type)) {
			return "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
					+ "A.BATCH_SEQ, A.STOCK_QTY/C.DOSAGE_QTY AS STOCK_QTY, B.STOCK_UNIT, "
					+ "A.RETAIL_PRICE * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE* C.DOSAGE_QTY AS VERIFYIN_PRICE, "
					+" A.INVENT_PRICE   "
					+ "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.ORDER_CODE = '"
					+ order_code
					+ "' AND A.ACTIVE_FLG = 'Y'";

		} else {
			return "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
					+ "A.BATCH_SEQ, A.STOCK_QTY, B.DOSAGE_UNIT, "
					+ "A.RETAIL_PRICE  AS OWN_PRICE, A.VERIFYIN_PRICE "
					+ "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.ORDER_CODE = '"
					+ order_code
					+ "' AND A.ACTIVE_FLG = 'Y'";
		}
		// luhai modify 2012-3-16 根据unittype进行处理end
	}

    /**
     * 查询药品的批次和效期
     * 
     * @param org_code
     *            String
     * @param order_code
     *            String
     * @param unit_type
     *            String
     * @return String
     */
    public static String getOrderBatchNoValidAcnt(String org_code,
            String order_code, String unit_type) {//wanglong add 20150202
        // luhai modify 2012-3-16 根据unittype进行处理
        // return
        // "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
        // + "A.BATCH_SEQ, A.STOCK_QTY, B.STOCK_UNIT, "
        // +
        // "A.RETAIL_PRICE * C.STOCK_QTY * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE "
        // + "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
        // + "WHERE A.ORDER_CODE = B.ORDER_CODE " +
        // "AND A.ORDER_CODE = C.ORDER_CODE "
        // + "AND B.ORDER_CODE = C.ORDER_CODE "
        // + "AND A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '" +
        // order_code + "' AND A.ACTIVE_FLG = 'Y'";
        if ("0".equals(unit_type)) {
            return "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
                    + "A.BATCH_SEQ, A.STOCK_QTY/C.DOSAGE_QTY AS STOCK_QTY, B.STOCK_UNIT, "
                    + "A.RETAIL_PRICE * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE* C.DOSAGE_QTY AS VERIFYIN_PRICE, "
                    +" A.INVENT_PRICE   "
                    + "FROM SPC_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
                    + "WHERE A.ORDER_CODE = B.ORDER_CODE "
                    + "AND A.ORDER_CODE = C.ORDER_CODE "
                    + "AND B.ORDER_CODE = C.ORDER_CODE "
                    + "AND A.ORG_CODE = '"
                    + org_code
                    + "' AND A.ORDER_CODE = '"
                    + order_code
                    + "' AND A.ACTIVE_FLG = 'Y'";

        } else {
            return "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
                    + "A.BATCH_SEQ, A.STOCK_QTY, B.DOSAGE_UNIT, "
                    + "A.RETAIL_PRICE  AS OWN_PRICE, A.VERIFYIN_PRICE "
                    + "FROM SPC_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
                    + "WHERE A.ORDER_CODE = B.ORDER_CODE "
                    + "AND A.ORDER_CODE = C.ORDER_CODE "
                    + "AND B.ORDER_CODE = C.ORDER_CODE "
                    + "AND A.ORG_CODE = '"
                    + org_code
                    + "' AND A.ORDER_CODE = '"
                    + order_code
                    + "' AND A.ACTIVE_FLG = 'Y'";
        }
        // luhai modify 2012-3-16 根据unittype进行处理end
    }
    
	/**
	 * 查询药品的批次和效期  包含过期
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param unit_type
	 *            String
	 * @return String
	 */
	public static String getOrderBatchNoValidAll(String org_code,
			String order_code, String unit_type) {
		// luhai modify 2012-3-16 根据unittype进行处理
		// return
		// "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
		// + "A.BATCH_SEQ, A.STOCK_QTY, B.STOCK_UNIT, "
		// +
		// "A.RETAIL_PRICE * C.STOCK_QTY * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE "
		// + "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
		// + "WHERE A.ORDER_CODE = B.ORDER_CODE " +
		// "AND A.ORDER_CODE = C.ORDER_CODE "
		// + "AND B.ORDER_CODE = C.ORDER_CODE "
		// + "AND A.ORG_CODE = '" + org_code + "' AND A.ORDER_CODE = '" +
		// order_code + "' AND A.ACTIVE_FLG = 'Y'";
		if ("0".equals(unit_type)) {
			return "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
					+ "A.BATCH_SEQ, A.STOCK_QTY/C.DOSAGE_QTY AS STOCK_QTY, B.STOCK_UNIT, "
					+ "A.RETAIL_PRICE * C.STOCK_QTY * C.DOSAGE_QTY AS OWN_PRICE, A.VERIFYIN_PRICE* C.STOCK_QTY * C.DOSAGE_QTY AS VERIFYIN_PRICE "
					+ "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.ORDER_CODE = '"
					+ order_code
					+ "'  ";

		} else {
			return "SELECT A.ORDER_CODE, B.ORDER_DESC, A.BATCH_NO, A.VALID_DATE, "
					+ "A.BATCH_SEQ, A.STOCK_QTY, B.DOSAGE_UNIT, "
					+ "A.RETAIL_PRICE  AS OWN_PRICE, A.VERIFYIN_PRICE "
					+ "FROM IND_STOCK A, PHA_BASE B, PHA_TRANSUNIT C "
					+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
					+ "AND A.ORDER_CODE = C.ORDER_CODE "
					+ "AND B.ORDER_CODE = C.ORDER_CODE "
					+ "AND A.ORG_CODE = '"
					+ org_code
					+ "' AND A.ORDER_CODE = '"
					+ order_code
					+ "' ";
		}
		// luhai modify 2012-3-16 根据unittype进行处理end
	}


	/**
	 * 根据用户ID查询所属药库科室
	 * 
	 * @param user_id
	 *            String
	 * @return String
	 */
	public static String getIndOrgByUserId(String user_id, String region_code) {
		return "SELECT B.ORG_CODE AS ID, B.ORG_CHN_DESC AS NAME, B.PY1, B.PY2 "
				+ "FROM SYS_OPERATOR_DEPT A, IND_ORG B "
				+ "WHERE A.DEPT_CODE = B.ORG_CODE AND A.USER_ID = '" + user_id
				+ "' AND " + " B.REGION_CODE = '" + region_code
				+ "' ORDER BY A.MAIN_FLG DESC";
	}

	/**
	 * 根据用户ID查询所属药库科室
	 * 
	 * @param user_id
	 *            String
	 * @param where
	 *            String
	 * @return String
	 */
	public static String getIndOrgByUserId(String user_id, String region_code,
			String where) {
		return "SELECT B.ORG_CODE AS ID, B.ORG_CHN_DESC AS NAME, B.PY1, B.PY2 "
				+ " FROM SYS_OPERATOR_DEPT A, IND_ORG B "
				+ " WHERE A.DEPT_CODE = B.ORG_CODE AND A.USER_ID = '" + user_id
				+ "' AND B.REGION_CODE = '" + region_code + "' " + where
				+ " ORDER BY A.MAIN_FLG DESC";
	}

	/**
	 * 根据用户ID查询所属科室
	 * 
	 * @param user_id
	 *            String
	 * @param where
	 *            String
	 * @return String
	 */
	public static String initTextFormatSysDept(String region_code) {
		return "SELECT DEPT_CODE AS ID, DEPT_ABS_DESC AS NAME, PY1, PY2 "
				+ "FROM SYS_DEPT "
				+ "WHERE FINAL_FLG = 'Y' AND DEPT_GRADE = '3' "
				+ " AND REGION_CODE = '" + region_code + "'  "
				+ " ORDER BY DEPT_CODE";
	}

	/**
	 * 根据用户ID查询所属科室
	 * 
	 * @param user_id
	 *            String
	 * @param where
	 *            String
	 * @return String
	 */
	public static String initTextFormatSysDept(String user_id,
			String region_code) {
		return "SELECT B.DEPT_CODE AS ID, B.DEPT_ABS_DESC AS NAME,B.PY1, B.PY2 "
				+ "FROM SYS_OPERATOR_DEPT A, SYS_DEPT B "
				+ "WHERE A.DEPT_CODE = B.DEPT_CODE AND A.USER_ID = '"
				+ user_id
				+ "' AND B.FINAL_FLG = 'Y' AND B.DEPT_GRADE = '3' "
				+ " AND B.REGION_CODE = '"
				+ region_code
				+ "' "
				+ " ORDER BY A.MAIN_FLG DESC";
	}

	/**
	 * 根据region查询科室
	 * 
	 * @param user_id
	 *            String
	 * @param where
	 *            String
	 * @return String luhai 2012-04-07
	 */
	public static String initTextFormatSysDeptWithRegion(String region_code) {
		return "SELECT B.DEPT_CODE AS ID, B.DEPT_ABS_DESC AS NAME,B.PY1, B.PY2 "
				+ "FROM SYS_DEPT B "
				+ "WHERE "
				+ " B.FINAL_FLG = 'Y' AND B.DEPT_GRADE = '3' "
				+ " AND B.REGION_CODE = '"
				+ region_code
				+ "' "
				+ " ORDER BY B.DEPT_CODE ";
	}

	/**
	 * 初始化TextFormat
	 * 
	 * @return String
	 */
	public static String initTextFormatIndOrg(String org_type,
			String region_code) {
		return "SELECT ORG_CODE AS ID, ORG_CHN_DESC AS NAME, PY1, PY2 "
				+ "FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = '"
				+ org_type + "' AND REGION_CODE = '" + region_code + "' ";
	}

	/**
	 * 初始化TextFormat
	 * 
	 * @return String
	 */
	public static String initTextFormatIndOrg(String org_type, String exinvflg,
			String region_code) {
		return "SELECT ORG_CODE AS ID, ORG_CHN_DESC AS NAME, PY1, PY2 "
				+ "FROM IND_ORG WHERE ORG_FLG = 'Y' AND ORG_TYPE = '"
				+ org_type + "' AND EXINV_FLG = '" + exinvflg + "'";
	}

	/**
	 * 部门批次过账查询
	 * 
	 * @param org_code
	 *            String
	 * @param tran_date
	 *            String
	 * @return String
	 */
	public static String getStockBatchByOrgCode(String org_code,
			String tran_date) {
		return "SELECT COUNT(*) AS NUM FROM IND_DDSTOCK WHERE ORG_CODE = '"
				+ org_code + "' AND TRANDATE = '" + tran_date + "'";
	}

	/**
	 * 查询药品条件前的价格
	 * 
	 * @param order_code
	 *            String
	 * @param date
	 *            String
	 * @return String
	 */
	public static String getOrderSysFeeHistoryPrice(String order_code,
			String yesterday) {
		return "SELECT ORDER_CODE , OWN_PRICE FROM SYS_FEE_HISTORY WHERE ORDER_CODE = '"
				+ order_code
				+ "' AND START_DATE >= '"
				+ yesterday
				+ "000000"
				+ "' AND START_DATE <= '" + yesterday + "2359595" + "'";
	}

	/**
	 * 药库日结中查询药房可发药的药品分类
	 * 
	 * @param org_code
	 *            String
	 * @return String
	 */
	public static String getIndDDStockOrderTypeCode(String org_code) {
		return "SELECT B.TYPE_CODE , C.CHN_DESC "
				+ " FROM IND_DDSTOCK A, PHA_BASE B , SYS_DICTIONARY C "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND B.TYPE_CODE = C.ID " + " AND A.ORG_CODE = '" + org_code
				+ "' AND C.GROUP_ID='SYS_PHATYPE' "
				+ " GROUP BY B.TYPE_CODE,C.CHN_DESC ORDER BY B.TYPE_CODE";
	}

	/**
	 * 查询该药房/药库中药品的个数
	 * 
	 * @param org_code
	 *            String
	 * @param type_code
	 *            String
	 * @return String
	 */
	public static String getIndDDStockOrderTypeCode(String org_code,
			String type_code) {
		return "SELECT DISTINCT A.ORDER_CODE FROM IND_DDSTOCK A, PHA_BASE B "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE " + "AND A.ORG_CODE = '"
				+ org_code + "' AND B.TYPE_CODE = '" + type_code
				+ "' GROUP BY A.ORDER_CODE ORDER BY A.ORDER_CODE ";
	}

	/**
	 * 上期结存成本金额
	 * 
	 * @param trandate
	 *            String
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param stock_price
	 *            double
	 * @return String
	 */
	public static String getIndDDStockLastStockAMT(String trandate,
			String org_code, String order_code, double stock_price) {
		return "SELECT SUM(LAST_TOTSTOCK_QTY * VERIFYIN_PRICE ) "
				+ " AS LAST_STOCK_AMT FROM IND_DDSTOCK WHERE TRANDATE='"
				+ trandate + "' AND ORG_CODE='" + org_code
				+ "' AND ORDER_CODE ='" + order_code + "'";
	}

	/**
	 * 上期结存零售金额
	 * 
	 * @param trandate
	 *            String
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @param stock_price
	 *            double
	 * @return String
	 */
	public static String getIndDDStockLastOwnAMT(String trandate,
			String org_code, String order_code) {
		return "SELECT SUM(LAST_TOTSTOCK_QTY * RETAIL_PRICE) "
				+ " AS LAST_OWN_AMT FROM IND_DDSTOCK WHERE TRANDATE='"
				+ trandate + "' AND ORG_CODE='" + org_code
				+ "' AND ORDER_CODE ='" + order_code + "'";
	}

	/**
	 * 盘点表
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getIndQtyCheckM(String org_code, String frozen_date,String stockFlg) {
		//fux modify 20150413
		String sql = "";
		if(stockFlg.equals("Y")){
			 
		}
		else{   
			sql = " AND B.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0";
		}
		return "SELECT C.DEPT_CHN_DESC, CASE WHEN (D.GOODS_DESC IS NULL) "
				+ "THEN D.ORDER_DESC ELSE D.ORDER_DESC || ' ('|| D.GOODS_DESC||')'"
				+ "END AS ORDER_DESC, D.SPECIFICATION, J.PHA_TYPE, "
				+ "FLOOR (B.STOCK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
				+ "|| MOD (B.STOCK_QTY, E.DOSAGE_QTY) "
				+ "|| G.UNIT_CHN_DESC AS STOCK_QTY, "
				+ "FLOOR (A.ACTUAL_CHECK_QTY/E.DOSAGE_QTY)||F.UNIT_CHN_DESC "
				+ "|| MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "                   
				+ "|| G.UNIT_CHN_DESC AS ACTUAL_CHECK_QTY, H.MATERIAL_CHN_DESC, "                
				+ " A.VALID_DATE , A.BATCH_NO "
				+ "FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D, "
				+ "PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G, PHA_BASE J, "
				+ "IND_MATERIALLOC H WHERE A.ORG_CODE = B.ORG_CODE "
				+ "AND A.ORDER_CODE = B.ORDER_CODE AND A.BATCH_SEQ = B.BATCH_SEQ "
				+ "AND A.ORG_CODE = C.DEPT_CODE AND A.ORDER_CODE = D.ORDER_CODE "
				+ "AND A.ORDER_CODE = E.ORDER_CODE AND A.STOCK_UNIT = F.UNIT_CODE "
				+ "AND A.DOSAGE_UNIT =G.UNIT_CODE AND A.ORDER_CODE = J.ORDER_CODE "
				+ "AND B.MATERIAL_LOC_CODE = H.MATERIAL_LOC_CODE(+) "
				+ sql
				//+ "AND B.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0 "
				+ "AND A.ORG_CODE = '" + org_code + "' AND A.FROZEN_DATE = '"
				+ frozen_date + "' ORDER BY A.ORDER_CODE";
	}

	/**
	 * 盘点明细表
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getIndQtyCheckD(String org_code, String frozen_date,String stockFlg) {

		// luhai modify 2012-1-23 将成本价改用验收价格 begin
		// return "SELECT C.DEPT_CHN_DESC, "
		// + "CASE WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
		// + " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
		// + " END AS ORDER_DESC, D.SPECIFICATION, J.PHA_TYPE, "
		// + " FLOOR (A.STOCK_QTY / E.DOSAGE_QTY) "
		// + " || F.UNIT_CHN_DESC || MOD (A.STOCK_QTY, E.DOSAGE_QTY) "
		// + " || G.UNIT_CHN_DESC AS STOCK_QTY, "
		// + "A.STOCK_PRICE  * E.DOSAGE_QTY  AS CONTRACT_PRICE "
		// + ", A.RETAIL_PRICE * E.DOSAGE_QTY AS OWN_PRICE, "
		// + " A.STOCK_PRICE * A.STOCK_QTY AS STOCK_AMT,"
		// +
		// " A.RETAIL_PRICE * A.STOCK_QTY AS OWM_AMT, A.VALID_DATE, A.BATCH_NO "
		// + " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D, "
		// +
		// " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G, PHA_BASE J "
		// + " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE "
		// + " AND A.BATCH_SEQ = B.BATCH_SEQ AND A.ORG_CODE = C.DEPT_CODE "
		// +
		// " AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + " AND A.ORDER_CODE = J.ORDER_CODE AND A.STOCK_QTY > 0 "
		// + " AND A.ORG_CODE = '" + org_code +
		// "'  AND A.FROZEN_DATE = '" + frozen_date +
		// "' ORDER BY A.ORDER_CODE";
		//fux modify 20150413
		String sql = "";
		if(stockFlg.equals("Y")){
			 
		}
		else{   
			sql = " AND B.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0";
		}
		return "SELECT C.DEPT_CHN_DESC, "
				+ "CASE WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
				+ " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
				+ " END AS ORDER_DESC, D.SPECIFICATION, J.PHA_TYPE, "
				+ " FLOOR (A.ACTUAL_CHECK_QTY / E.DOSAGE_QTY) "
				+ " || F.UNIT_CHN_DESC || MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
				+ " || G.UNIT_CHN_DESC AS STOCK_QTY, "
				+ "A.VERIFYIN_PRICE  * E.DOSAGE_QTY  AS CONTRACT_PRICE "
				+ ", A.RETAIL_PRICE * E.DOSAGE_QTY AS OWN_PRICE, "
				+ " A.VERIFYIN_PRICE * A.ACTUAL_CHECK_QTY AS STOCK_AMT,"
				+ " A.RETAIL_PRICE * A.ACTUAL_CHECK_QTY AS OWM_AMT, A.VALID_DATE, A.BATCH_NO "
				+ " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D, "
				+ " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G, PHA_BASE J "
				// luhai modify 2012-05-09 begin 盘点表打印时indstock 使用左连 begin
				+ " WHERE A.ORG_CODE = B.ORG_CODE(+) AND A.ORDER_CODE = B.ORDER_CODE(+) "
				+ " AND A.BATCH_SEQ = B.BATCH_SEQ(+) AND A.ORG_CODE = C.DEPT_CODE "
				+
				// luhai modify 2012-05-09 begin 盘点表打印时indstock 使用左连 end
				" AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
				+ " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
				+ " AND A.ORDER_CODE = J.ORDER_CODE " 
				+ sql
				//+ " AND A.STOCK_QTY > 0  AND A.ACTUAL_CHECK_QTY > 0"
				+ " AND A.ORG_CODE = '" + org_code + "'  AND A.FROZEN_DATE = '"
				+ frozen_date + "' ORDER BY A.ORDER_CODE";
		// luhai modify 2012-1-23 将成本价改用验收价格 end
	}

	/**
	 * 盈亏表
	 * 
	 * @param org_code
	 *            String
	 * @param frozen_date
	 *            String
	 * @return String
	 */
	public static String getIndQtyProfitLoss(String org_code, String frozen_date,String stockFlg) {  
		// luhai modify 2012-2-13 verifyinPrice begin
		// return "SELECT C.DEPT_CHN_DESC, CASE "
		// + " WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
		// + " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
		// + " END AS ORDER_DESC, D.SPECIFICATION, A.VALID_DATE, A.BATCH_NO, "
		// + " FLOOR (A.STOCK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
		// +
		// " || MOD (A.STOCK_QTY, E.DOSAGE_QTY)||G.UNIT_CHN_DESC AS STOCK_QTY, "
		// + " FLOOR (A.ACTUAL_CHECK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
		// + " || MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
		// + " || G.UNIT_CHN_DESC AS ACTUAL_CHECK_QTY, "
		// + " FLOOR (A.MODI_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
		// + " || MOD (A.MODI_QTY, E.DOSAGE_QTY) "
		// + " || G.UNIT_CHN_DESC AS MODI_QTY,A.RETAIL_PRICE AS OWN_PRICE, "
		// + " A.MODI_QTY * A.RETAIL_PRICE AS FREEZE_AMT, "
		// + " A.RETAIL_PRICE * A.ACTUAL_CHECK_QTY AS OWN_AMT,A.UNFREEZE_DATE"
		// + " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D,  "
		// + " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
		// + " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE "
		// + " AND A.BATCH_SEQ = B.BATCH_SEQ AND A.ORG_CODE = C.DEPT_CODE "
		// +
		// " AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
		// +
		// " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
		// + " AND A.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0 "
		// + " AND A.ORG_CODE = '" + org_code + "' AND A.FROZEN_DATE = '" +
		// frozen_date + "' ORDER BY A.ORDER_CODE";
		//fux modify 20150413
		String sql = "";
		if(stockFlg.equals("Y")){
			 
		}
		else{   
			sql = " AND A.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0";
		}
		return "SELECT C.DEPT_CHN_DESC, CASE "
				+ " WHEN (D.GOODS_DESC IS NULL) THEN D.ORDER_DESC "
				+ " ELSE D.ORDER_DESC || ' (' || D.GOODS_DESC || ')' "
				+ " END AS ORDER_DESC, D.SPECIFICATION, A.VALID_DATE, A.BATCH_NO, "
				+ " FLOOR (A.STOCK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
				+ " || MOD (A.STOCK_QTY, E.DOSAGE_QTY)||G.UNIT_CHN_DESC AS STOCK_QTY, "
				+ " FLOOR (A.ACTUAL_CHECK_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
				+ " || MOD (A.ACTUAL_CHECK_QTY, E.DOSAGE_QTY) "
				+ " || G.UNIT_CHN_DESC AS ACTUAL_CHECK_QTY, "
				+ " FLOOR (A.MODI_QTY / E.DOSAGE_QTY) || F.UNIT_CHN_DESC "
				+ " || MOD (A.MODI_QTY, E.DOSAGE_QTY) "   
				+ " || G.UNIT_CHN_DESC AS MODI_QTY,A.VERIFYIN_PRICE AS OWN_PRICE, "
				+ " A.MODI_QTY * A.VERIFYIN_PRICE AS FREEZE_AMT, "
				+ " A.VERIFYIN_PRICE * A.ACTUAL_CHECK_QTY AS OWN_AMT,A.UNFREEZE_DATE"
				+ " FROM IND_QTYCHECK A, IND_STOCK B, SYS_DEPT C, SYS_FEE D,  "
				+ " PHA_TRANSUNIT E, SYS_UNIT F, SYS_UNIT G "
				// luhai modify 2012-05-09 begin 盘点表打印时indstock 使用左连 begin
				+ " WHERE A.ORG_CODE = B.ORG_CODE(+) AND A.ORDER_CODE = B.ORDER_CODE(+) "
				+ " AND A.BATCH_SEQ = B.BATCH_SEQ(+) AND A.ORG_CODE = C.DEPT_CODE "
				// luhai modify 2012-05-09 begin 盘点表打印时indstock 使用左连 end
				+ " AND A.ORDER_CODE = D.ORDER_CODE AND A.ORDER_CODE = E.ORDER_CODE "
				+ " AND A.STOCK_UNIT = F.UNIT_CODE AND A.DOSAGE_UNIT = G.UNIT_CODE "
				+ sql
				//+ " AND A.STOCK_QTY > 0 AND A.ACTUAL_CHECK_QTY > 0 "
				+ " AND A.ORG_CODE = '" + org_code + "' AND A.FROZEN_DATE = '"
				+ frozen_date + "' ORDER BY A.ORDER_CODE";
		// luhai modify 2012-2-13 verifyinPrice end
	}

	/**
	 * 取得库存主档信息
	 * 
	 * @return String
	 */
	public static String getIndStockMInfo(String org_code, String order_code,
			String material_loc_code) {
		// <--- 界面修改 by shendr
		String sql = "SELECT A.ORDER_CODE, C.ORDER_DESC, A.MATERIAL_LOC_CODE,A.MATERIAL_LOC_DESC,A.ELETAG_CODE, A.DISPENSE_FLG,C.GOODS_DESC, "
				+ "A.DISPENSE_ORG_CODE,  C.STOCK_UNIT AS UNIT_CODE, A.SAFE_QTY, A.MAX_QTY, "
				+ "A.MIN_QTY, A.ECONOMICBUY_QTY, ABS(FLOOR(A.BUY_UNRECEIVE_QTY/ B.DOSAGE_QTY)) AS BUY_UNRECEIVE_QTY, A.MM_USE_QTY, "
				+ "0 AS STOCK_QTY, A.DD_USE_QTY, A.STANDING_QTY,A.MATERIAL_LOC_SEQ,A.SUP_CODE, "
				+ "A.QTY_TYPE, A.ACTIVE_FLG,C.SPECIFICATION FROM IND_STOCKM A, PHA_BASE C,PHA_TRANSUNIT B,SYS_UNIT D "
				+ "WHERE A.ORDER_CODE = C.ORDER_CODE "
				+ "AND A.ORDER_CODE=B.ORDER_CODE "
				+ "AND B.STOCK_UNIT=D.UNIT_CODE "
				+ "AND A.ORG_CODE = '"
				+ org_code + "'";
		// --->
		if (!"".equals(order_code)) {
			sql += " AND A.ORDER_CODE = '" + order_code + "'";
		}
		if (!"".equals(material_loc_code)) {
			sql += " AND A.MATERIAL_LOC_CODE = '" + material_loc_code + "'";
		}
		sql += " ORDER BY A.ORDER_CODE ";
		return sql;
	}

	/**
	 * 更新IND_STOCK的料位
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @return String
	 */
	public static String onUpdateIndStcokMaterialLocCode(String org_code,
			String order_code, String material_loc_code) {
		return "UPDATE IND_STOCK SET MATERIAL_LOC_CODE ='" + material_loc_code
				+ "' WHERE ORG_CODE = '" + org_code + "' AND ORDER_CODE = '"
				+ order_code + "'";
	}

	/**
	 * 药品明细账汇总查询
	 * 
	 * @return String
	 */
	public static String getINDPhaDetailMQuery(String org_code,
			String start_date, String end_date, String order_code) {

		String sql = "SELECT CASE WHEN (B.GOODS_DESC IS NULL) THEN B.ORDER_DESC "
				+ " ELSE B.ORDER_DESC ||'('|| B.GOODS_DESC ||')' "
				+ " END AS ORDER_DESC, "
				+ " B.SPECIFICATION, "
				+ " SUM(A.LAST_TOTSTOCK_QTY) AS LAST_TOTSTOCK_QTY,C.UNIT_CHN_DESC,"
				+ " SUM (A.LAST_TOTSTOCK_QTY * A.VERIFYIN_PRICE) AS LAST_TOTSTOCK_AMT, "
				+ " SUM (A.IN_QTY) AS STOCKIN_QTY, "
				+ " SUM (A.IN_QTY *  A.VERIFYIN_PRICE) AS STOCKIN_AMT, "
				+ " SUM (A.OUT_QTY) AS STOCKOUT_QTY, "
				+ " SUM (A.OUT_QTY *  A.VERIFYIN_PRICE) AS STOCKOUT_AMT, "
				+ " SUM (A.CHECKMODI_QTY) AS CHECKMODI_QTY, "
				+ " SUM (A.CHECKMODI_QTY *  A.VERIFYIN_PRICE) AS CHECKMODI_AMT, "
				+ " SUM (A.STOCK_QTY) AS STOCK_QTY, "
				+ " SUM (A.STOCK_QTY *  A.VERIFYIN_PRICE) AS STOCK_AMT, A.ORDER_CODE, "
				+ " A.ORG_CODE,  A.VERIFYIN_PRICE AS OWN_PRICE, D.ORG_CHN_DESC "
				+ " FROM IND_DDSTOCK A, SYS_FEE B, SYS_UNIT C, IND_ORG D "
				+ " WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ " AND B.UNIT_CODE = C.UNIT_CODE "
				+ " AND A.ORG_CODE = D.ORG_CODE "
				+ " AND A.ORG_CODE = '"
				+ org_code
				+ "' AND TO_DATE(A.TRANDATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE("
				+ start_date
				+ ", 'YYYYMMDDHH24MISS') AND TO_DATE("
				+ end_date
				+ ", 'YYYYMMDDHH24MISS') ";
		String where = "";
		if (!"".equals(order_code)) {
			where = " AND A.ORDER_CODE = '" + order_code + "'";
		}
		return sql
				+ where
				+ " GROUP BY B.ORDER_DESC, B.SPECIFICATION, "
				+ " C.UNIT_CHN_DESC, A.ORDER_CODE, A.ORG_CODE, A.VERIFYIN_PRICE, "
				+ " D.ORG_CHN_DESC,B.GOODS_DESC ORDER BY A.ORDER_CODE ";
		// luhai modify 2012-1-24 将零售价改成采购价显示 end
	}

	/**
	 * 药品明细账明细查询(主库)
	 * 
	 * @return String
	 */
	public static String getINDPhaDetailDQueryA(String org_code,
			String start_date, String end_date, String qty_in, String qty_out,
			String qty_check, String order_code, String drugLvl2) {
		
		String drugLvl2Sql = "  ";
		if(null != drugLvl2 && "Y".equals(drugLvl2)){
			drugLvl2Sql = "  AND C.CTRLDRUGCLASS_CODE='03'  ";
		}
		
		String order_code_sql = "";
		String sql_start = "";
		String sql_end = "";
		// 按药品查询
		if (!"".equals(order_code)) {
			order_code_sql = " AND C.ORDER_CODE = '" + order_code + "' ";
		}
		if("Y".equals(qty_in) || "Y".equals(qty_out) || "Y".equals(qty_check)){
			sql_start = "SELECT * FROM( ";
			sql_end = " ) ORDER BY STATUS ";
		}
		String sql_in = "";
		String sql_out = "";
		String sql_check = "";
		// 入库部分
		if ("Y".equals(qty_in)) {
			// 验收入库部分
			sql_in += "SELECT A.CHECK_DATE AS CHECK_DATE, 'VER' AS STATUS, C.ORDER_DESC, "
					+ "C.SPECIFICATION," 
					+ "SUM(B.VERIFYIN_QTY) AS QTY, "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " SUM(B.VERIFYIN_QTY)*B.VERIFYIN_PRICE AS AMT, "
					+ " F.ORG_CHN_DESC "
					+ " FROM IND_VERIFYINM A, IND_VERIFYIND B, PHA_BASE C, "
					+ " SYS_UNIT E, IND_ORG F "
					+ " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.BILL_UNIT = E.UNIT_CODE "
					+ " AND A.ORG_CODE = F.ORG_CODE "
					+ " AND A.CHECK_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ " AND A.ORG_CODE = '"
					+ org_code
					+ "' "
					+ drugLvl2Sql
					+ "AND B.UPDATE_FLG IN ('1','3') "
					+ order_code_sql
					+ " GROUP BY A.CHECK_DATE, C.ORDER_DESC, C.SPECIFICATION, "
					+ " E.UNIT_CHN_DESC, F.ORG_CHN_DESC, B.VERIFYIN_PRICE "
					
					+ " UNION ALL"
					// 退库部分
					+ " SELECT A.WAREHOUSING_DATE AS CHECK_DATE, 'RET' AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION,CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY, "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(B.VERIFYIN_PRICE * (CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, F.ORG_CHN_DESC "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " SYS_UNIT E, IND_ORG F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND A.TO_ORG_CODE = F.ORG_CODE "
					+ " AND B.ORDER_CODE = K.ORDER_CODE "
					+ " AND A.REQTYPE_CODE = 'RET' "
					+ " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql
					+ " AND A.WAREHOUSING_DATE IS NOT NULL "
					+ " AND A.UPDATE_FLG IN ('1','3') "
					+ " AND A.TO_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC, C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT "
					
					+ " UNION ALL"
					// 其他入库部分
					+ " SELECT A.WAREHOUSING_DATE AS CHECK_DATE, 'THI' AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION,CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY, "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(B.VERIFYIN_PRICE * (CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, F.ORG_CHN_DESC "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " SYS_UNIT E, IND_ORG F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND A.APP_ORG_CODE = F.ORG_CODE "
					+ " AND B.ORDER_CODE = K.ORDER_CODE "
					+ " AND A.REQTYPE_CODE = 'THI' "
					+ " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql
					+ " AND A.WAREHOUSING_DATE IS NOT NULL "
					+ " AND A.UPDATE_FLG = '3' "
					+ " AND A.APP_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT ";
		}
		// 出库部分
		if ("Y".equals(qty_out)) {
			if (!"".equals(sql_in))
				sql_out = sql_out + " UNION ";
			sql_out = sql_out
					// 请领出库部分
					+ " SELECT A.DISPENSE_DATE AS CHECK_DATE, A.REQTYPE_CODE AS STATUS, "
					+ " C.ORDER_DESC, C.SPECIFICATION, "
					+ " CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY, "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(B.VERIFYIN_PRICE*(CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, F.ORG_CHN_DESC "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " SYS_UNIT E, IND_ORG F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND A.TO_ORG_CODE = F.ORG_CODE "
					+ " AND B.ORDER_CODE = K.ORDER_CODE "
					+ " AND A.REQTYPE_CODE IN('DEP','GIF','TEC','EXM','COS') "
					+ " AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql
					+ " AND A.DISPENSE_DATE IS NOT NULL "
					+ " AND A.UPDATE_FLG <> '2' "
					+ " AND A.TO_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.DISPENSE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC, "
					+ " A.REQTYPE_CODE, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT "
					
					+ " UNION ALL"
					// 调拨,耗损，其他出库部分
					+ " SELECT A.DISPENSE_DATE AS CHECK_DATE, A.REQTYPE_CODE AS STATUS, "
					+ " C.ORDER_DESC, C.SPECIFICATION, "
					+ " CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY, "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(B.VERIFYIN_PRICE*(CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, F.ORG_CHN_DESC "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " SYS_UNIT E, IND_ORG F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND A.APP_ORG_CODE = F.ORG_CODE "
					+ " AND B.ORDER_CODE = K.ORDER_CODE "
					+ " AND A.REQTYPE_CODE IN ('WAS', 'THO') "
					+ " AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql
					+ " AND A.DISPENSE_DATE IS NOT NULL "
					+ " AND A.UPDATE_FLG <> '2' "
					+ " AND A.APP_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.DISPENSE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC, "
					+ " A.REQTYPE_CODE, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT "
					+ " UNION ALL"
					// 退货出库部分
					+ " SELECT A.CHECK_DATE AS CHECK_DATE, 'REG' AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION,SUM(B.QTY) AS QTY,  "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " SUM(B.QTY)*B.VERIFYIN_PRICE AS AMT, F.ORG_CHN_DESC "
					+ " FROM IND_REGRESSGOODSM A, IND_REGRESSGOODSD B, "
					+ " PHA_BASE C, SYS_UNIT E, IND_ORG F "
					+ " WHERE A.REGRESSGOODS_NO = B.REGRESSGOODS_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.BILL_UNIT = E.UNIT_CODE "
					+ " AND A.ORG_CODE = F.ORG_CODE "
					+ " AND A.CHECK_DATE BETWEEN TO_DATE('" + start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql
					+ " AND A.CHECK_DATE IS NOT NULL "
					+ " AND B.UPDATE_FLG IN ('1', '3') " + " AND A.ORG_CODE = '"
					+ org_code + "' " + order_code_sql
					+ " GROUP BY A.CHECK_DATE, C.ORDER_DESC, C.SPECIFICATION, "
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.ORG_CHN_DESC ";
		}
		// 盘点部分
		if ("Y".equals(qty_check)) {
			if (!"".equals(sql_out) || !"".equals(sql_in))
				sql_check = sql_check + " UNION ";
			// 盘点作业
			sql_check = sql_check
					+ " SELECT TO_DATE (A.FROZEN_DATE, 'YYYY-MM-DD HH24:MI:SS') AS CHECK_DATE "
					+ " , 'FRO' AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION, SUM (A.MODI_QTY) AS QTY, "
					+ " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(SUM (A.VERIFYIN_PRICE * A.MODI_QTY),7) AS AMT, "
					+ " F.ORG_CHN_DESC "
					+ " FROM IND_QTYCHECK A, PHA_BASE C,"
					+ " SYS_UNIT E, IND_ORG F "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.ORG_CODE = F.ORG_CODE "
					+ " AND TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
					+ " BETWEEN TO_DATE('" + start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql
					+ " AND A.UNFREEZE_DATE IS NOT NULL "
					+ " AND A.MODI_QTY <> 0 " + " AND A.ORG_CODE = '"
					+ org_code + "' " + order_code_sql
					+ " GROUP BY A.FROZEN_DATE,C.ORDER_DESC,C.SPECIFICATION, "
					+ " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE, F.ORG_CHN_DESC ";
		}
		String sql = sql_start+sql_in + sql_out + sql_check+sql_end;
		return sql;
		// luhai modify 2012-1-24 将零售价改成采购价 end
	}

	/**
	 * 药品明细账明细查询(中库)
	 * 
	 * @return String
	 */
	public static String getINDPhaDetailDQueryB(String org_code,
			String start_date, String end_date, String qty_in, String qty_out,
			String qty_check, String order_code, String drugLvl2) {
		
		String drugLvl2Sql = "  ";
		if(null != drugLvl2 && "Y".equals(drugLvl2)){
			drugLvl2Sql = "  AND C.CTRLDRUGCLASS_CODE='03'  ";
		}
		
		String order_code_sql = "";
		// 按药品查询
		if (!"".equals(order_code)) {
			order_code_sql = " AND C.ORDER_CODE = '" + order_code + "' ";
		}
		String sql_start = "";
		String sql_end = "";
		if("Y".equals(qty_in) || "Y".equals(qty_out) || "Y".equals(qty_check)){
			sql_start = "SELECT * FROM( ";
			sql_end = " ) ORDER BY STATUS ";
		}
		String sql_in = "";
		String sql_out = "";
		String sql_check = "";
		// 入库部分
		if ("Y".equals(qty_in)) {
			// 请领入库,其它入库部分
			sql_in += " SELECT A.WAREHOUSING_DATE AS CHECK_DATE, "
					+ "  A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, C.SPECIFICATION, "
					+ " CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY, "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(B.VERIFYIN_PRICE*(CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, F.DEPT_CHN_DESC, "
					+ " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " SYS_UNIT E, SYS_DEPT F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND A.APP_ORG_CODE = F.DEPT_CODE "
					+ " AND B.ORDER_CODE = K.ORDER_CODE "
					+ " AND A.REQTYPE_CODE IN ('DEP','THI','GIF','TEC') "
					+ " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					
					+ drugLvl2Sql 
					+ " AND A.WAREHOUSING_DATE IS NOT NULL "
					+ " AND A.UPDATE_FLG = '3' "
					+ " AND A.APP_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.DEPT_CHN_DESC, "
					+ " A.REQTYPE_CODE, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT "
					
					+ " UNION ALL"
					// 退库入库,调拨入库部分
					+ " SELECT A.WAREHOUSING_DATE, A.REQTYPE_CODE AS STATUS, "
					+ " C.ORDER_DESC,C.SPECIFICATION, "
					+ " CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY, "
					+ " E.UNIT_CHN_DESC,B.VERIFYIN_PRICE AS OWN_PRICE, " 
					+ " ROUND(B.VERIFYIN_PRICE*(CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, F.DEPT_CHN_DESC, "
					+ " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " SYS_UNIT E, SYS_DEPT F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND A.TO_ORG_CODE = F.DEPT_CODE "
					+ " AND B.ORDER_CODE = K.ORDER_CODE "
					+ " AND A.REQTYPE_CODE ='RET' "
					+ " AND A.WAREHOUSING_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.WAREHOUSING_DATE IS NOT NULL "
					+ " AND A.UPDATE_FLG = '3' "
					+ " AND A.TO_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.WAREHOUSING_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, F.DEPT_CHN_DESC, "
					+ " A.REQTYPE_CODE, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT "
					
					+ " UNION ALL	"
					// 门急诊退药入库部分
					+ " SELECT A.PHA_RETN_DATE , A.ADM_TYPE||'_RET' AS STATUS, "
					+ " C.ORDER_DESC, C.SPECIFICATION, SUM (A.DOSAGE_QTY) AS QTY, "
					+ " E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DOSAGE_QTY) AS OWN_PRICE, "
					+ " ROUND(SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM OPD_ORDER A,PHA_BASE C,PHA_TRANSUNIT D,SYS_UNIT E, "
					+ " SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.ORDER_CODE = D.ORDER_CODE "
					+ " AND D.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND C.ORDER_CODE = D.ORDER_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.PHA_RETN_DATE IS NOT NULL "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.PHA_RETN_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.PHA_RETN_DATE, C.ORDER_DESC, C.SPECIFICATION, "
					+ " E.UNIT_CHN_DESC,F.DEPT_CHN_DESC,"
					+ " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.DOSAGE_QTY "
					
					+ " UNION ALL"
					// 住院退药入库部分--修改 By liyh 20120815 除数为0 ，
					+ " SELECT A.DSPN_DATE, 'I_RET' AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION, SUM (A.RTN_DOSAGE_QTY) -  "
					+ " NVL (SUM (A.CANCEL_DOSAGE_QTY), 0) AS QTY, "
					+ " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE1 AS OWN_PRICE,  "
					+ " ROUND(SUM(A.VERIFYIN_PRICE1*(A.RTN_DOSAGE_QTY-NVL(A.CANCEL_DOSAGE_QTY,0))),7) AS AMT,  "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM ODI_DSPNM A, PHA_BASE C, "
					+ " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.DSPN_KIND='RT' AND A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.DISPENSE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.DSPN_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.DSPN_DATE, C.ORDER_DESC, C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, C.STOCK_PRICE, F.DEPT_CHN_DESC, A.MR_NO, "
					+ " G.PAT_NAME, A.CASE_NO,A.VERIFYIN_PRICE1 ";
			// +
			// ",A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3 ";
		}
		// 出库部分
		if ("Y".equals(qty_out)) {
			if (!"".equals(sql_in))
				sql_out = sql_out + " UNION ";
			sql_out = sql_out
					// 备药补充出库,科室备药出库,卫耗材出库部分
					+ " SELECT A.DISPENSE_DATE AS CHECK_DATE, "
					+ " A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION, CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(B.VERIFYIN_PRICE*(CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, '' AS MR_NO, '' AS PAT_NAME, "
					+ " '' AS CASE_NO "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " PHA_TRANSUNIT D, SYS_UNIT E, SYS_DEPT F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.ORDER_CODE = D.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND C.ORDER_CODE = D.ORDER_CODE "
					+ " AND A.TO_ORG_CODE = F.DEPT_CODE "
					+ " AND B.ORDER_CODE = K.ORDER_CODE "
					+ " AND A.REQTYPE_CODE IN ('EXM', 'TEC', 'COS') "
					+ " AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.TO_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " AND A.UPDATE_FLG <> '2' "
					+ " GROUP BY A.DISPENSE_DATE, C.ORDER_DESC, "
					+ " C.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, "
					+ " F.DEPT_CHN_DESC, A.REQTYPE_CODE, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT "
					+ " UNION ALL "
					
					// 调拨出库,损耗出库,其它出库,退库出库部分
					+ " SELECT A.DISPENSE_DATE AS CHECK_DATE, "
					+ " A.REQTYPE_CODE AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION, CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END AS QTY,"
					+ " E.UNIT_CHN_DESC, B.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(B.VERIFYIN_PRICE*(CASE WHEN B.UNIT_CODE=K.STOCK_UNIT THEN SUM(B.ACTUAL_QTY)*K.DOSAGE_QTY ELSE SUM(B.ACTUAL_QTY) END),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, '' AS MR_NO, '' AS PAT_NAME, "
					+ " '' AS CASE_NO "
					+ " FROM IND_DISPENSEM A, IND_DISPENSED B, PHA_BASE C, "
					+ " SYS_UNIT E, SYS_DEPT F, PHA_TRANSUNIT K "
					+ " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
					+ " AND B.ORDER_CODE = C.ORDER_CODE "
					+ " AND B.UNIT_CODE = E.UNIT_CODE "
					+ " AND A.APP_ORG_CODE = F.DEPT_CODE "
					+ " AND A.REQTYPE_CODE IN ('GIF', 'THO', 'WAS' , 'RET') "
					+ " AND A.DISPENSE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.APP_ORG_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " AND A.UPDATE_FLG <> '2' "
					+ " GROUP BY A.DISPENSE_DATE, C.ORDER_DESC, "
					+ " C.SPECIFICATION, E.UNIT_CHN_DESC, B.VERIFYIN_PRICE, "
					+ " F.DEPT_CHN_DESC, A.REQTYPE_CODE, K.DOSAGE_QTY, B.UNIT_CODE, K.STOCK_UNIT "
					+ " UNION ALL"
					
					// 门急诊发药出库部分
					+ " SELECT A.PHA_DOSAGE_DATE,A.ADM_TYPE||'_DPN' AS STATUS,"
					+ " C.ORDER_DESC,C.SPECIFICATION,SUM(A.DOSAGE_QTY) AS QTY,"
					+ " E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DOSAGE_QTY) AS OWN_PRICE, "
					+ " ROUND(SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY2)),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM OPD_ORDER A,PHA_BASE C,PHA_TRANSUNIT D,SYS_UNIT E,"
					+ " SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.ORDER_CODE = D.ORDER_CODE "
					+ " AND D.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.PHA_DOSAGE_DATE IS NOT NULL "
					+ " AND A.PHA_RETN_CODE IS NULL "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC, F.DEPT_CHN_DESC,"
					+ " A.MR_NO, G.PAT_NAME, A.CASE_NO, A.ADM_TYPE,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.DOSAGE_QTY "
					
					+ " UNION ALL"
					// 住院发药出库部分
					+ " SELECT A.PHA_DOSAGE_DATE, 'I_DPN' AS STATUS, "
					+ " C.ORDER_DESC, C.SPECIFICATION, SUM(A.DOSAGE_QTY) "
					+ " AS QTY, E.UNIT_CHN_DESC, (A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY3)/(A.DOSAGE_QTY) AS OWN_PRICE, "
					+ " ROUND(SUM ((A.VERIFYIN_PRICE1*A.DISPENSE_QTY1+A.VERIFYIN_PRICE2*A.DISPENSE_QTY2+A.VERIFYIN_PRICE3*A.DISPENSE_QTY2)),7) AS AMT, "
					+ " F.DEPT_CHN_DESC, A.MR_NO, G.PAT_NAME, A.CASE_NO "
					+ " FROM ODI_DSPNM A, PHA_BASE C, PHA_TRANSUNIT D, "
					+ " SYS_UNIT E,  SYS_DEPT F, SYS_PATINFO G "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.ORDER_CODE = D.ORDER_CODE "
					+ " AND D.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.EXEC_DEPT_CODE = F.DEPT_CODE "
					+ " AND A.MR_NO = G.MR_NO "
					+ " AND A.PHA_DOSAGE_DATE IS NOT NULL "
					+ " AND A.PHA_RETN_DATE IS NULL "
					+ " AND A.CAT1_TYPE='PHA' "
					+ " AND A.TAKEMED_ORG = '2' "
					+ " AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE('"
					+ start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('"
					+ end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.EXEC_DEPT_CODE = '"
					+ org_code
					+ "' "
					+ order_code_sql
					+ " GROUP BY A.PHA_DOSAGE_DATE,C.ORDER_DESC,C.SPECIFICATION,"
					+ " E.UNIT_CHN_DESC,F.DEPT_CHN_DESC,A.MR_NO,"
					+ " G.PAT_NAME, A.CASE_NO,A.VERIFYIN_PRICE1,A.DISPENSE_QTY1,A.VERIFYIN_PRICE2,A.DISPENSE_QTY2,A.VERIFYIN_PRICE3,A.DISPENSE_QTY3,A.DOSAGE_QTY ";
		}
		// 盘点部分
		if ("Y".equals(qty_check)) {
			if (!"".equals(sql_out) || !"".equals(sql_in))
				sql_check = sql_check + " UNION ";
			// 盘点作业
			sql_check = sql_check
					+ " SELECT TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
					+ " AS CHECK_DATE, 'FRO' AS STATUS, C.ORDER_DESC, "
					+ " C.SPECIFICATION, SUM (A.MODI_QTY) AS QTY, "
					+ " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE AS OWN_PRICE, "
					+ " ROUND(SUM (A.VERIFYIN_PRICE * A.MODI_QTY),7) AS AMT, "
					+ " F.ORG_CHN_DESC, "
					+ " '' AS MR_NO, '' AS PAT_NAME, '' AS CASE_NO "
					+ " FROM IND_QTYCHECK A, PHA_BASE C, PHA_TRANSUNIT D, "
					+ " SYS_UNIT E, IND_ORG F "
					+ " WHERE A.ORDER_CODE = C.ORDER_CODE "
					+ " AND A.ORDER_CODE = D.ORDER_CODE "
					+ " AND A.DOSAGE_UNIT = E.UNIT_CODE "
					+ " AND A.ORG_CODE = F.ORG_CODE "
					+ " AND TO_DATE (A.FROZEN_DATE, 'YYYYMMDDHH24MISS') "
					+ " BETWEEN TO_DATE('" + start_date
					+ "', 'YYYYMMDDHH24MISS') AND TO_DATE('" + end_date
					+ "','YYYYMMDDHH24MISS') "
					+ drugLvl2Sql 
					+ " AND A.UNFREEZE_DATE IS NOT NULL "
					+ " AND A.MODI_QTY <> 0 " + " AND A.ORG_CODE = '"
					+ org_code + "' " + order_code_sql
					+ " GROUP BY A.FROZEN_DATE,C.ORDER_DESC,C.SPECIFICATION, "
					+ " E.UNIT_CHN_DESC, A.VERIFYIN_PRICE, F.ORG_CHN_DESC ";
		}
		String sql = sql_start+sql_in + sql_out + sql_check+sql_end;
		// System.out.println("明细账查询sql:"+sql);
		return sql;
		// ************************************************
		// luhai modify 2012-1-24 将零售价格改成采购价格end
		// ************************************************
	}

	/**
	 * 按病区整批配药的执行SQL
	 * 
	 * @return String
	 */
	public static String getUpdateReduceIndStockSql(String org_code,
			String order_code, int batch_seq, double out_qty, double out_amt,
			String opt_user, String opt_date, String opt_term) {
		return "UPDATE IND_STOCK SET OUT_QTY=OUT_QTY+" + out_qty
				+ ", OUT_AMT=OUT_AMT+" + out_amt + ", STOCK_QTY=STOCK_QTY-"
				+ out_qty + ", DOSEAGE_QTY=DOSEAGE_QTY+" + out_qty
				+ ", DOSAGE_AMT=DOSAGE_AMT+" + out_amt + ", OPT_USER='"
				+ opt_user + "', OPT_DATE=SYSDATE, OPT_TERM='" + opt_term
				+ "' WHERE ORG_CODE='" + org_code + "' AND ORDER_CODE='"
				+ order_code + "' AND BATCH_SEQ = " + batch_seq;
	}

	/**
	 * 根据药品orderCode batchSeq 得到药品的库存单位采购价
	 * 
	 * @return
	 */
	public static String getIndVerifyInPrice(String orderCode, int batchSeq) {
		StringBuffer sqlbf = new StringBuffer();
		sqlbf
				.append(" SELECT  A.VERIFYIN_PRICE/B.PURCH_QTY AS VERIFYIN_PRICE ");
		sqlbf.append(" FROM  IND_VERIFYIND A,PHA_TRANSUNIT B  ");
		sqlbf.append(" WHERE A.ORDER_CODE=B.ORDER_CODE ");
		sqlbf.append(" AND A.ORDER_CODE='" + orderCode + "' ");
		sqlbf.append(" AND A.BATCH_SEQ=" + batchSeq + " ");
		return sqlbf.toString();
	}

	/**
	 * 得到药品的库存单位采购价
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @param batchSeq
	 * @return
	 * @author liyh
	 * @date 20120801
	 */
	public static String getIndVerifyInPrice(String orgCode, String orderCode,
			int batchSeq) {
		String sqlbf = " SELECT  A.VERIFYIN_PRICE*B.DOSAGE_QTY AS VERIFYIN_PRICE "
				+ " FROM    IND_STOCK A, PHA_TRANSUNIT B  "
				+ " WHERE   A.ORDER_CODE=B.ORDER_CODE  AND A.ORG_CODE='"
				+ orderCode
				+ "' AND "
				+ "         A.ORDER_CODE='"
				+ orderCode
				+ "'  AND A.BATCH_SEQ=" + batchSeq + " ";
		return sqlbf;
	}
	
    /**
     * 得到药品的库存单位采购价
     * 
     * @param orgCode
     * @param orderCode
     * @param batchSeq
     * @return
     * @author liyh
     * @date 20120801
     */
    public static String getSpcVerifyInPrice(String orgCode, String orderCode,
            int batchSeq) {//wanglong add 20150202
        String sqlbf = " SELECT  A.VERIFYIN_PRICE*B.DOSAGE_QTY AS VERIFYIN_PRICE "
                + " FROM    SPC_STOCK A, PHA_TRANSUNIT B  "
                + " WHERE   A.ORDER_CODE=B.ORDER_CODE  AND A.ORG_CODE='"
                + orderCode
                + "' AND "
                + "         A.ORDER_CODE='"
                + orderCode
                + "'  AND A.BATCH_SEQ=" + batchSeq + " ";
        return sqlbf;
    }
    
	// 主项
	/**
	 * 医院药品销售统计查询主项SQL
	 * 
	 * @param orgCode
	 *            部门编码 （门诊，住院，补充计价）
	 * @param deptCode
	 *            科室编码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            截止日期
	 * @param orderCode
	 *            药品编码
	 * @param antibioticCode
	 *            抗生素编码
	 * @param ctrldrugclassCode
	 *            管制药品编码（毒麻药）
	 * @param sysGrugCalss
	 *            药物种类（国家补贴....）
	 * @param typeCode
	 *            药品分类
	 * @parm dspnKind 住院药品开立方式( ST 临时、UD长期、DS 出院带药、RT 退药)
	 * @parm inspayType 给付类别（A：医保 B：增付C：自费）
	 * @parm dsDateFlag 是否出院（0：全部，1：在院，2：出院）
	 * @return
	 */
	public static String getStatisticsSQLM(String orgCode, String deptCode,
			String startDate, String endDate, String orderCode,
			String antibioticCode, String ctrldrugclassCode,
			String sysGrugCalss, String typeCode, String dspnKind,
			String inspayType, String dsDateFlag) {
		// 执行科室查询条件0：门急诊 1：补充计价，2 住院
		String[] deptCodeArr = getDeptCodeArr(deptCode);
		// 得到药品查询条件
		String[] orderCodeArr = getOrderCodeArr(orderCode);
		// 得到抗生素药品查询条件
		String[] antibioticCodeArr = getAntibioticCode(antibioticCode);
		// 得到管制药品查询条件
		String[] ctrldrugclassCodeArr = getCtrldrugclassCode(ctrldrugclassCode);
		// 得到 药物种类 查询条件
		String[] sysGrugClassArr = getSysGrugClass(sysGrugCalss);
		// 得到 药品分类 查询条件
		String[] typeCodeArr = getTypeCode(typeCode);
		// 得到 住院药品开立方式 查询条件
		String dspnKindStr = getDspnKind(dspnKind);
		// 得到 给付类别 查询条件
		String[] inspayTypeArr = getInspayType(inspayType);
		// 得到 是否出院 查询条件
		String dsDateStr = getDsDate(dsDateFlag);
		StringBuffer sb = new StringBuffer();
		sb
				.append(
						" SELECT abc.region_chn_abn as REGION_CHN_DESC,abc.exec_dept_code,abc.dept_chn_desc,abc.order_code,abc.order_desc,abc.specification,abc.own_price,abc.unit_chn_desc,sum(abc.tot_qty) as sm_qty,sum(abc.tot_amt) as sum_amt,'' as udd_numb  ")
				.append(" from ").append(" ( ");
		if ("".equals(orgCode)) {

			/*************************** 门急诊药品销售统计 ****************************/
			sb
					.append(
							"  SELECT e.region_chn_abn,a.dept_code as exec_dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc , b.SPECIFICATION , a.own_price , D.UNIT_CHN_DESC ,sum(a.dosage_qty) AS tot_qty,sum(a.own_amt) AS tot_amt ")
					.append(
							"  FROM opd_order a,pha_base b,SYS_DEPT C,SYS_UNIT D,sys_region e ,sys_fee f ")
					.append(
							"  where a.order_code=b.order_code  AND  A.Dept_Code=C.DEPT_CODE  and  a.region_code=e.region_code ")
					.append(
							"  AND  A.DISPENSE_UNIT=D.UNIT_CODE and  a.order_code=f.order_code  and  a.adm_type in('O','E')  ")
					.append(deptCodeArr[0])
					.append(orderCodeArr[0])
					.append(antibioticCodeArr[0])
					.append(ctrldrugclassCodeArr[0])
					.append(sysGrugClassArr[0])
					.append(typeCodeArr[0])
					.append(inspayTypeArr[0])
					.append("  and  a.order_cat1_code in ('PHA_C','PHA_W')  ")
					.append("  and  a.bill_date between TO_DATE ('")
					.append(startDate)
					.append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('")
					.append(endDate)
					.append("', 'yyyymmdd hh24:mi:ss')  ")
					.append(
							"  group by e.region_chn_abn,a.dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc, b.SPECIFICATION,a.own_price, D.UNIT_CHN_DESC   ")

					/*************************** 补充计费药品销售统计 ************************/
					.append("  union all  ")
					.append(
							"   select g.region_chn_abn AS region_chn_abn,a.dept_code as exec_dept_code,d.dept_chn_desc,a.order_code, b.ORDER_DESC , b.SPECIFICATION ,a.OWN_PRICE,c.unit_chn_desc,  sum(a.dosage_qty) AS tot_qty, SUM (tot_amt) AS tot_amt  ")
					.append(
							"   from ibs_ordd a,pha_base b ,sys_unit c,sys_dept d ,sys_fee e ,ibs_ordm f,sys_region g ")
					.append(
							"	  where a.order_cat1_code in ('PHA_C','PHA_W') and  a.order_code=b.order_code and  a.dosage_unit=c.unit_code and a.dept_code=d.dept_code and a.order_code=e.order_code and a.case_no=f.case_no  ")
					.append(
							"   and a.case_no_seq=f.case_no_seq and f.region_code=g.region_code ")
					.append(deptCodeArr[1])
					.append(orderCodeArr[1])
					.append(antibioticCodeArr[1])
					.append(ctrldrugclassCodeArr[1])
					.append(sysGrugClassArr[1])
					.append(typeCodeArr[1])
					.append(inspayTypeArr[1])
					.append("  and  a.bill_date between TO_DATE ('")
					.append(startDate)
					.append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('")
					.append(endDate)
					.append("', 'yyyymmdd hh24:mi:ss')  ")
					.append(
							"   group by g.region_chn_abn,d.dept_chn_desc,a.dept_code,a.order_code, b.ORDER_DESC, b.SPECIFICATION ,a.OWN_PRICE ,c.unit_chn_desc ")

					/*************************** 住院药品销售统计 *************************/
					.append("  union all  ")
					.append(
							"  select e.region_chn_abn,a.dept_code as exec_dept_code,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc,sum(a.dosage_qty) as tot_qty,sum(a.own_amt) as tot_amt  ")
					.append(
							"  from odi_dspnm a,sys_dept b, pha_base c,sys_unit d,sys_region e ,sys_fee f,adm_inp g ")
					.append(
							"  where a.dept_code=b.dept_code and   a.order_code=c.order_code and   a.dosage_unit=d.unit_code and   a.region_code=e.region_code  ")
					.append(
							"  and   a.order_code=f.order_code and   a.case_no=g.case_no  ")
					.append(deptCodeArr[2])
					.append(orderCodeArr[2])
					.append(antibioticCodeArr[2])
					.append(ctrldrugclassCodeArr[2])
					.append(sysGrugClassArr[2])
					.append(typeCodeArr[2])
					.append(inspayTypeArr[2])
					.append(dspnKindStr)
					.append(dsDateStr)
					.append("  and  a.dspn_date between TO_DATE ('")
					.append(startDate)
					.append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('")
					.append(endDate)
					.append("', 'yyyymmdd hh24:mi:ss')  ")
					.append(
							"  group by e.region_chn_abn,a.dept_code ,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc ");

		} else {
			if ("040103".equals(orgCode)) {
				sb.append(
						getOrgCodeB(orgCode, deptCodeArr, orderCodeArr,
								antibioticCodeArr, ctrldrugclassCodeArr,
								sysGrugClassArr, typeCodeArr, inspayTypeArr,
								startDate, endDate)).append("  union all  ")
						.append(
								getOrgCodeZ(orgCode, deptCodeArr, orderCodeArr,
										antibioticCodeArr,
										ctrldrugclassCodeArr, sysGrugClassArr,
										typeCodeArr, inspayTypeArr, startDate,
										endDate, dspnKindStr, dsDateStr));

			}
			if ("040104".equals(orgCode) || "040102".equals(orgCode)) {
				sb.append(getOrgCodeM(orgCode, deptCodeArr, orderCodeArr,
						antibioticCodeArr, ctrldrugclassCodeArr,
						sysGrugClassArr, typeCodeArr, inspayTypeArr, startDate,
						endDate));
			}
		}
		sb
				.append(" ) abc ")
				.append(
						" group by abc.region_chn_abn,abc.exec_dept_code ,abc.dept_chn_desc,abc.order_code,abc.order_desc,abc.specification,abc.own_price,abc.unit_chn_desc ")
				.append(
						" order  by abc.region_chn_abn,abc.exec_dept_code ,abc.dept_chn_desc,abc.order_code,abc.order_desc,abc.specification,abc.own_price,abc.unit_chn_desc ");

		return sb.toString();
	}

	/**
	 * 住院药房查询条件
	 * 
	 * @param orgCode
	 *            部门编码 （门诊，住院，补充计价）
	 * @param deptCode
	 *            科室编码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            截止日期
	 * @param orderCode
	 *            药品编码
	 * @param antibioticCode
	 *            抗生素编码
	 * @param ctrldrugclassCode
	 *            管制药品编码（毒麻药）
	 * @param sysGrugCalss
	 *            药物种类（国家补贴....）
	 * @param typeCode
	 *            药品分类
	 * @parm dspnKind 住院药品开立方式( ST 临时、UD长期、DS 出院带药、RT 退药)
	 * @parm inspayType 给付类别（A：医保 B：增付C：自费）
	 * @parm dsDateFlag 是否出院（0：全部，1：在院，2：出院） *
	 */
	private static String getOrgCodeZ(String orgCode, String[] deptCodeArr,
			String[] orderCodeArr, String[] antibioticCodeArr,
			String[] ctrldrugclassCodeArr, String[] sysGrugClassArr,
			String[] typeCodeArr, String[] inspayTypeArr, String startDate,
			String endDate, String dspnKindStr, String dsDateStr) {
		StringBuffer sb = new StringBuffer();
		/*************************** 住院药品销售统计 *************************/
		sb
				.append(
						"  select e.region_chn_abn,a.dept_code as exec_dept_code,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc,sum(a.dosage_qty) as tot_qty,sum(a.own_amt) as tot_amt  ")
				.append(
						"  from odi_dspnm a,sys_dept b, pha_base c,sys_unit d,sys_region e ,sys_fee f,adm_inp g ")
				.append(
						"  where a.dept_code=b.dept_code and   a.order_code=c.order_code and   a.dosage_unit=d.unit_code and   a.region_code=e.region_code  ")
				.append(
						"  and   a.order_code=f.order_code and   a.case_no=g.case_no  ")
				.append(deptCodeArr[2])
				.append(orderCodeArr[2])
				.append(antibioticCodeArr[2])
				.append(ctrldrugclassCodeArr[2])
				.append(sysGrugClassArr[2])
				.append(typeCodeArr[2])
				.append(inspayTypeArr[2])
				.append(dspnKindStr)
				.append(dsDateStr)
				.append("  and  a.dspn_date between TO_DATE ('")
				.append(startDate)
				.append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('")
				.append(endDate)
				.append("', 'yyyymmdd hh24:mi:ss')  ")
				.append(
						"  group by e.region_chn_abn,a.dept_code ,b.dept_chn_desc,a.order_code,c.order_desc,c.specification,a.own_price,d.unit_chn_desc ");

		return sb.toString();

	}

	/**
	 * 补充计价查询条件
	 * 
	 * @param orgCode
	 *            部门编码 （门诊，住院，补充计价）
	 * @param deptCode
	 *            科室编码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            截止日期
	 * @param orderCode
	 *            药品编码
	 * @param antibioticCode
	 *            抗生素编码
	 * @param ctrldrugclassCode
	 *            管制药品编码（毒麻药）
	 * @param sysGrugCalss
	 *            药物种类（国家补贴....）
	 * @param typeCode
	 *            药品分类
	 * @parm dspnKind 住院药品开立方式( ST 临时、UD长期、DS 出院带药、RT 退药)
	 * @parm inspayType 给付类别（A：医保 B：增付C：自费）
	 * @parm dsDateFlag 是否出院（0：全部，1：在院，2：出院） *
	 */
	private static String getOrgCodeB(String orgCode, String[] deptCodeArr,
			String[] orderCodeArr, String[] antibioticCodeArr,
			String[] ctrldrugclassCodeArr, String[] sysGrugClassArr,
			String[] typeCodeArr, String[] inspayTypeArr, String startDate,
			String endDate) {
		StringBuffer sb = new StringBuffer();
		/*************************** 补充计费药品销售统计 ************************/
		sb
				.append(
						"   select g.region_chn_abn AS region_chn_abn,a.dept_code as exec_dept_code,d.dept_chn_desc,a.order_code, b.ORDER_DESC , b.SPECIFICATION ,a.OWN_PRICE,c.unit_chn_desc,  sum(a.dosage_qty) AS tot_qty, SUM (tot_amt) AS tot_amt  ")
				.append(
						"   from ibs_ordd a,pha_base b ,sys_unit c,sys_dept d ,sys_fee e ,ibs_ordm f,sys_region g ")
				.append(
						"	  where a.order_cat1_code in ('PHA_C','PHA_W') and  a.order_code=b.order_code and  a.dosage_unit=c.unit_code and a.dept_code=d.dept_code and a.order_code=e.order_code and a.case_no=f.case_no  ")
				.append(
						"   and a.case_no_seq=f.case_no_seq and f.region_code=g.region_code ")
				.append(deptCodeArr[1])
				.append(orderCodeArr[1])
				.append(antibioticCodeArr[1])
				.append(ctrldrugclassCodeArr[1])
				.append(sysGrugClassArr[1])
				.append(typeCodeArr[1])
				.append(inspayTypeArr[1])
				.append("  and  a.bill_date between TO_DATE ('")
				.append(startDate)
				.append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('")
				.append(endDate)
				.append("', 'yyyymmdd hh24:mi:ss')  ")
				.append(
						"   group by g.region_chn_abn,d.dept_chn_desc,a.dept_code,a.order_code, b.ORDER_DESC, b.SPECIFICATION ,a.OWN_PRICE ,c.unit_chn_desc ");

		return sb.toString();

	}

	/**
	 * 门急诊药房查询条件
	 * 
	 * @param orgCode
	 *            部门编码 （门诊，住院，补充计价）
	 * @param deptCode
	 *            科室编码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            截止日期
	 * @param orderCode
	 *            药品编码
	 * @param antibioticCode
	 *            抗生素编码
	 * @param ctrldrugclassCode
	 *            管制药品编码（毒麻药）
	 * @param sysGrugCalss
	 *            药物种类（国家补贴....）
	 * @param typeCode
	 *            药品分类
	 * @parm dspnKind 住院药品开立方式( ST 临时、UD长期、DS 出院带药、RT 退药)
	 * @parm inspayType 给付类别（A：医保 B：增付C：自费）
	 * @parm dsDateFlag 是否出院（0：全部，1：在院，2：出院）
	 */
	private static String getOrgCodeM(String orgCode, String[] deptCodeArr,
			String[] orderCodeArr, String[] antibioticCodeArr,
			String[] ctrldrugclassCodeArr, String[] sysGrugClassArr,
			String[] typeCodeArr, String[] inspayTypeArr, String startDate,
			String endDate) {
		StringBuffer sb = new StringBuffer();
		/*************************** 门急诊药品销售统计 ****************************/
		sb
				.append(
						"  SELECT e.region_chn_abn,a.dept_code as exec_dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc , b.SPECIFICATION , a.own_price , D.UNIT_CHN_DESC ,sum(a.dosage_qty) AS tot_qty,sum(a.own_amt) AS tot_amt ")
				.append(
						"  FROM opd_order a,pha_base b,SYS_DEPT C,SYS_UNIT D,sys_region e ,sys_fee f,reg_patadm g")
				.append(
						"  where a.order_code=b.order_code  AND  A.Dept_Code=C.DEPT_CODE  and  a.region_code=e.region_code ")
				.append(
						"  AND  A.DISPENSE_UNIT=D.UNIT_CODE and  a.order_code=f.order_code  AND  a.case_no = g.case_no  ");
		if ("040102".equals(orgCode)) {
			sb
					.append("  and a.adm_type in('O','E') and g.realdept_code != '020103'  ");
		} else if ("040104".equals(orgCode)) {
			// and a.adm_type='O'
			sb.append("  and g.realdept_code = '020103' ");
		}
		sb
				.append(deptCodeArr[0])
				.append(orderCodeArr[0])
				.append(antibioticCodeArr[0])
				.append(ctrldrugclassCodeArr[0])
				.append(sysGrugClassArr[0])
				.append(typeCodeArr[0])
				.append(inspayTypeArr[0])
				.append("  and  a.order_cat1_code in ('PHA_C','PHA_W')  ")
				.append("  and  a.bill_date between TO_DATE ('")
				.append(startDate)
				.append("', 'yyyymmdd hh24:mi:ss') and TO_DATE ('")
				.append(endDate)
				.append("', 'yyyymmdd hh24:mi:ss')  ")
				.append(
						"  group by e.region_chn_abn,a.dept_code,C.DEPT_CHN_DESC,a.order_code, b.order_desc, b.SPECIFICATION,a.own_price, D.UNIT_CHN_DESC  ");

		return sb.toString();
	}

	// 细项
	/**
	 * 医院药品销售统计查询明细项SQL
	 * 
	 * @param deptCode
	 *            部门编码
	 * @param orderCode
	 *            药品编码
	 * @param startDate
	 *            开始时间
	 * @param endDate
	 *            结束时间
	 * @param endDate2
	 * @param startDate2
	 * @param smQty
	 * @param specification
	 * @param endDate
	 * @param startDate
	 * @param smQty2
	 * @return
	 */
	public static String getStatisticsSQLD(String deptCode, String startDate,
			String endDate) {
		StringBuffer sb = new StringBuffer();
		sb
				.append(
						" SELECT to_char(A.OPT_DATE,'yyyy-MM-dd') as OPT_DATE,G.ORDER_DESC,F.MR_NO,B.PAT_NAME,C.SPECIFICATION AS  SPECIFICATION,  ")
				// LIRUI 20120613 加药品名称
				.append(
						" D.UNIT_CHN_DESC AS UNIT_CHN_DESC,G.RETAIL_PRICE AS RETAIL_PRICE,CASE   M.RX_KIND  WHEN 'ST' THEN '临时' WHEN 'UD' THEN '长期' WHEN 'DS' THEN '出院带药' ELSE '退药' END   AS DSPN_KIND, ")
				.append(
						" E.FREQ_CHN_DESC AS  FREQ_CHN_DESC,A.DOSAGE_QTY as DOSAGE_QTY , a.TOT_AMT AS TOT_AMT,A.OWN_AMT,'' AS UDD_NUMB,  H.USER_NAME  ")
				// LIRUI 20120613 去掉了病人id
				// .append("        CASE   M.RX_KIND  WHEN 'ST' THEN '临时' WHEN 'UD' THEN '长期' WHEN 'DS' THEN '出院带药' ELSE '退药' END   AS DSPN_KIND, E.FREQ_CHN_DESC AS  FREQ_CHN_DESC, ")
				// .append("  	   A.DOSAGE_QTY as DOSAGE_QTY , a.TOT_AMT AS TOT_AMT,A.OWN_AMT,G.UDD_NUMB,  H.USER_NAME ")
				.append(
						" FROM IBS_ORDD A, SYS_PATINFO B, SYS_FEE C,  SYS_UNIT D, SYS_PHAFREQ E,ADM_INP F,ODI_ORDER M  ,PHA_BASE G,SYS_OPERATOR H  ")
				.append(
						" WHERE A.CASE_NO=F.CASE_NO(+) AND F.MR_NO = B.MR_NO AND A.ORDER_CODE = C.ORDER_CODE AND C.UNIT_CODE = D.UNIT_CODE ")
				.append(
						"       AND A.ORDER_CODE=G.ORDER_CODE AND M.ORDER_DR_CODE=H.USER_ID  ")
				.append(
						"       AND A.FREQ_CODE = E.FREQ_CODE  AND A.ORDER_CAT1_CODE IN ('PHA_C','PHA_W')  AND A.DOSAGE_QTY>0 AND A.ORDER_NO IS NOT NULL  ")
				// .append("C.REGION_CODE='").append(regionDesec)
				.append(" 	  AND A.BILL_DATE BETWEEN  TO_DATE ('")
				.append(startDate)
				.append("', 'YYYYMMDDHH24MISS')  AND TO_DATE ('")
				.append(endDate)
				.append("', 'YYYYMMDDHH24MISS')  ")
				// .append("       AND A.ORDER_CODE = '").append(orderCode).append("'")
				.append("   AND A.DEPT_CODE= '")
				.append(deptCode)
				.append(
						"'  AND A.CASE_NO=M.CASE_NO(+) AND A.ORDER_SEQ=M.ORDER_SEQ(+) AND A.ORDER_NO=M.ORDER_NO(+) ")
				.append(" UNION ALL  ")
				.append(
						" SELECT to_char(A.OPT_DATE,'yyyy-MM-dd') AS OPT_DATE,C.ORDER_DESC, E.MR_NO,  B.PAT_NAME,C.SPECIFICATION AS SPECIFICATION,D.UNIT_CHN_DESC,C.RETAIL_PRICE, ")
				// LIRUI 20120613 加药品名称
				.append(
						"        '' AS DSPN_KIND, '' AS FREQ_CHN_DESC,A.DOSAGE_QTY AS DOSAGE_QTY , A.TOT_AMT AS TOT_AMT,A.OWN_AMT,'' AS UDD_NUMB,F.USER_NAME ")
				// LIRUI 20120613 去掉了病人id
				.append(
						" FROM IBS_ORDD A, SYS_PATINFO B, PHA_BASE C,  SYS_UNIT D,ADM_INP E ,SYS_OPERATOR F,ODI_ORDER M   ")
				.append(
						" WHERE A.CASE_NO=E.CASE_NO(+) AND E.MR_NO = B.MR_NO  AND A.ORDER_CODE = C.ORDER_CODE  AND C.DOSAGE_UNIT = D.UNIT_CODE ")
				.append(
						"       AND M.ORDER_DR_CODE=F.USER_ID AND A.ORDER_CAT1_CODE IN ('PHA_C','PHA_W') AND A.ORDER_NO IS NOT NULL AND A.DOSAGE_QTY < 0  ")
				.append("   AND A.DEPT_CODE = '")
				.append(deptCode)
				.append(
						"'  AND A.CASE_NO=M.CASE_NO(+) AND A.ORDER_SEQ=M.ORDER_SEQ(+) AND A.ORDER_NO=M.ORDER_NO(+)  ")
				.append("       AND A.BILL_DATE  BETWEEN TO_DATE ('").append(
						startDate).append(
						"', 'YYYYMMDDHH24MISS')  AND TO_DATE ('").append(
						endDate).append("', 'YYYYMMDDHH24MISS') ")
		// .append("       AND A.ORDER_CODE = '").append(orderCode).append("'")

		;
		return sb.toString();
	}

	// lirui 2012-6-7 end 查询药品销售统计情况
	/**
	 * 组装 执行科室 查询条件
	 * 
	 * @param deptCode
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String[] getDeptCodeArr(String deptCode) {
		String[] strArr = new String[3];
		if (null != deptCode && !"".equals(deptCode)) {
			strArr[0] = " and a.dept_code='" + deptCode + "' ";
			strArr[1] = " and a.dept_code='" + deptCode + "' ";
			strArr[2] = " and a.dept_code='" + deptCode + "' ";
		} else {
			strArr[0] = "   ";
			strArr[1] = "   ";
			strArr[2] = "   ";
		}
		return strArr;
	}

	/**
	 * 组装药品编码查询条件
	 * 
	 * @param orderCode
	 *            药品编码
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String[] getOrderCodeArr(String orderCode) {
		String[] strArr = new String[3];
		if (null != orderCode && !"".equals(orderCode)) {
			strArr[0] = " and a.order_code='" + orderCode + "' ";
			strArr[1] = " and a.order_code='" + orderCode + "' ";
			strArr[2] = " and a.order_code='" + orderCode + "' ";
		} else {
			strArr[0] = "   ";
			strArr[1] = "   ";
			strArr[2] = "   ";
		}
		return strArr;
	}

	/**
	 * 组装抗生素药品查询条件
	 * 
	 * @param antibioticCode
	 *            抗生素编码
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String[] getAntibioticCode(String antibioticCode) {
		String[] strArr = new String[3];
		if (null != antibioticCode && !"".equals(antibioticCode)) {
			strArr[0] = " and b.antibiotic_code='" + antibioticCode + "' ";
			strArr[1] = " and b.antibiotic_code='" + antibioticCode + "' ";
			strArr[2] = " and c.antibiotic_code='" + antibioticCode + "' ";
		} else {
			strArr[0] = "   ";
			strArr[1] = "   ";
			strArr[2] = "   ";
		}
		return strArr;
	}

	/**
	 * 组装管制(毒麻药)药品查询条件
	 * 
	 * @param orgCode
	 *            管制药品编码
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String[] getCtrldrugclassCode(String ctrldrugclassCode) {
		String[] strArr = new String[3];
		if (null != ctrldrugclassCode && !"".equals(ctrldrugclassCode)) {
			strArr[0] = " and b.ctrldrugclass_code='" + ctrldrugclassCode
					+ "' ";
			strArr[1] = " and b.ctrldrugclass_code='" + ctrldrugclassCode
					+ "' ";
			strArr[2] = " and c.ctrldrugclass_code='" + ctrldrugclassCode
					+ "' ";
		} else {
			strArr[0] = "   ";
			strArr[1] = "   ";
			strArr[2] = "   ";
		}
		return strArr;
	}

	/**
	 * 组装 药物种类 查询条件（国家基本药品，自采药品）
	 * 
	 * @param orgCode
	 *            药物种类编码
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String[] getSysGrugClass(String sysGrugClass) {
		String[] strArr = new String[3];
		if (null != sysGrugClass && !"".equals(sysGrugClass)) {
			strArr[0] = " and f.sys_grug_class='" + sysGrugClass + "' ";
			strArr[1] = " and e.sys_grug_class='" + sysGrugClass + "' ";
			strArr[2] = " and f.sys_grug_class='" + sysGrugClass + "' ";
		} else {
			strArr[0] = "   ";
			strArr[1] = "   ";
			strArr[2] = "   ";
		}
		return strArr;
	}

	/**
	 * 组装 药品分类查询条件(西药，中药，中成药)
	 * 
	 * @param orgCode
	 *            药物分类编码
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String[] getTypeCode(String typeCode) {
		String[] strArr = new String[3];
		if (null != typeCode && !"".equals(typeCode)) {
			strArr[0] = " and b.type_code='" + typeCode + "' ";
			strArr[1] = " and b.type_code='" + typeCode + "' ";
			strArr[2] = " and c.type_code='" + typeCode + "' ";
		} else {
			strArr[0] = "   ";
			strArr[1] = "   ";
			strArr[2] = "   ";
		}
		return strArr;
	}

	/**
	 * 组装 住院药品开立方式 查询条件(针对 住院) 查ODI_DSPNM.DSPN_KIND(ST 临时、UD长期、DS 出院带药、RT 退药
	 * 
	 * @param dspn_kind
	 *            开立方式编码
	 * @return str
	 */
	private static String getDspnKind(String dspnKind) {
		String str = " ";
		if (null != dspnKind && !"".equals(dspnKind)) {
			str = " and a.dspn_kind='" + dspnKind + "' ";
		}
		return str;
	}

	/**
	 * 组装 给付类别 查询条件 医保给付类别A：医保 B：增付C：自费
	 * 
	 * @param inspayType
	 *            给付类别编码
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String[] getInspayType(String inspayType) {
		String[] strArr = new String[3];
		if (null != inspayType && !"".equals(inspayType)) {
			strArr[0] = " and f.inspay_type='" + inspayType + "' ";
			strArr[1] = " and e.inspay_type='" + inspayType + "' ";
			strArr[2] = " and f.inspay_type='" + inspayType + "' ";
		} else {
			strArr[0] = "   ";
			strArr[1] = "   ";
			strArr[2] = "   ";
		}
		return strArr;
	}

	/**
	 * 组装 是否出院 查询条件
	 * 
	 * @param orgCode
	 *            0/null:全部；1:在院，2：出院
	 * @return str[0,1,2] 分别对应 门诊，补充计价，住院
	 */
	private static String getDsDate(String dsDateFlag) {
		String str = " ";
		if (null != dsDateFlag && !"".equals(dsDateFlag)) {
			str = "  and g.ds_date is not null  and  CANCEL_FLG ='N' ";
		} else {// if{(null != dsDateFlag && !"".equals(dsDateFlag)){
			str = "  and g.ds_date is  null  and  CANCEL_FLG ='N'  ";
		}
		return str;
	}

	/**
	 * 部门库存查询-不显示批次序号
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20120808
	 */
	public static String getOrgStockQueryNotBatchSQL(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String batchNo = parm.getValue("BATCH_NO");
		String matCode = parm.getValue("MATERIAL_LOC_CODE");
		String typeCode = parm.getValue("TYPE_CODE");
		String safeQty = parm.getValue("SAFE_QTY");
		String stockQty = parm.getValue("STOCK_QTY");
		String phaType = parm.getValue("PHA_TYPE");
		 
    	boolean activeFlg = parm.getBoolean("ACTIVE_FLG");
    	String supCode = parm.getValue("SUP_CODE");//供应厂商 wanglong add 20150130
		// 用于查询安全库存量
		String childSql = " ";
		String sql = " SELECT A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, H.CHN_DESC,SUM(A.STOCK_QTY) AS STOCK_QTY, G.UNIT_CHN_DESC, "
				+ "        FLOOR(SUM(A.STOCK_QTY) / F.DOSAGE_QTY) || E.UNIT_CHN_DESC || MOD(SUM(A.STOCK_QTY), F.DOSAGE_QTY) || G.UNIT_CHN_DESC AS QTY, "
				+ "        A.RETAIL_PRICE * F.DOSAGE_QTY || '/' || E.UNIT_CHN_DESC || ';' ||   A.RETAIL_PRICE || '/' || G.UNIT_CHN_DESC AS PRICE, "
				+ "        A.VERIFYIN_PRICE AS STOCK_PRICE, SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE AS STOCK_AMT, SUM(A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT, "
				+ "        (SUM(A.STOCK_QTY) * A.RETAIL_PRICE - SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE) AS DIFF_AMT,'' AS BATCH_NO,'' AS VALID_DATE, "
				+ "		   A.STOCK_FLG,B.SAFE_QTY, D.PHA_TYPE, A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG,B.MATERIAL_LOC_CODE,F.DOSAGE_QTY,K.ORG_CHN_DESC "
				+ " FROM   IND_STOCK  A, IND_STOCKM  B,  SYS_FEE  C,  PHA_BASE  D, SYS_UNIT  E,PHA_TRANSUNIT   F, "
				+ "		  SYS_UNIT   G,SYS_DICTIONARY  H ,IND_ORG K"
				+ " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE  AND A.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = C.ORDER_CODE  "
				+ "		 AND A.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = D.ORDER_CODE AND D.STOCK_UNIT = E.UNIT_CODE "
				+ "       AND A.ORDER_CODE = F.ORDER_CODE AND B.ORDER_CODE = F.ORDER_CODE  AND C.ORDER_CODE = F.ORDER_CODE "
				+ "       AND D.ORDER_CODE = F.ORDER_CODE AND D.DOSAGE_UNIT = G.UNIT_CODE  AND H.GROUP_ID = 'SYS_PHATYPE' AND D.TYPE_CODE = H.ID " 
				+ "       AND A.ORG_CODE = K.ORG_CODE ";
		if (!StringUtil.isNullString(orgCode)) {
			sql += "  AND A.ORG_CODE='" + orgCode + "' ";
		}
		if (null != orderCode && !"".equals(orderCode)) {
			sql += " AND A.ORDER_CODE='" + orderCode + "' ";
			childSql = " AND G.ORDER_CODE='" + orderCode + "' ";
		}
		if (null != batchNo && !"".equals(batchNo)) {
			sql += " AND A.BATCH_NO=='" + batchNo + "' ";
		}
		/*
		 * if(null != matCode && !"".equals(matCode)){ sql +=
		 * " AND A.MATERIAL_LOC_CODE=='" + matCode + "' "; }
		 */
		if (null != typeCode && !"".equals(typeCode)) {
			sql += " AND D.TYPE_CODE='" + typeCode + "' ";
		}
		if (null != safeQty && !"".equals(safeQty)) {
			sql += " AND A.ORDER_CODE in ( "
					+ " SELECT  NVL(G.ORDER_CODE ,0) AS ORDER_CODE "
					+ " FROM IND_STOCK G,IND_STOCKM K "
					+ " WHERE   G.ORG_CODE=K.ORG_CODE AND G.ORDER_CODE=K.ORDER_CODE ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += "  AND G.ORG_CODE='" + orgCode + "' ";
			}
			sql += childSql + " "
				+ " GROUP BY G.ORG_CODE,G.ORDER_CODE,K.SAFE_QTY "
				+ " HAVING SUM(G.STOCK_QTY)<K.SAFE_QTY   " + " ) ";
		}
		if (null != stockQty && !"".equals(stockQty)) {
			sql += " AND A.STOCK_QTY!=0 ";
		}
		//fux modify 20130115
   // 	if(activeFlg == true||"Y".equals(activeFlg)){
    		sql += " AND C.ACTIVE_FLG = 'Y'";
   // 	}else{
   // 		sql += " AND C.ACTIVE_FLG = 'N'";
   // 	}	
        if (!TCM_Transform.isNull(supCode)) {// wanglong add 20140130
            sql += " AND A.SUP_CODE='" + supCode + "' ";
        }
		sql += "  GROUP BY A.ORDER_CODE,C.ORDER_DESC, C.SPECIFICATION,H.CHN_DESC,E.UNIT_CHN_DESC,F.DOSAGE_QTY,G.UNIT_CHN_DESC,A.RETAIL_PRICE, "
				+ " A.VERIFYIN_PRICE,  A.STOCK_FLG,  B.SAFE_QTY,  D.PHA_TYPE,   A.ACTIVE_FLG,B.MATERIAL_LOC_CODE,K.ORG_CHN_DESC  ORDER BY A.ORDER_CODE ";
		return sql;
	}
	
	/**
	 * 部门库存查询-不显示批次序号
	 * 
	 * @param parm
	 * @return
	 * @author wangjc
	 * @date 20150611
	 */
	public static String getOrgStockQueryNotBatchSQLBySupCode(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String batchNo = parm.getValue("BATCH_NO");
		String matCode = parm.getValue("MATERIAL_LOC_CODE");
		String typeCode = parm.getValue("TYPE_CODE");
		String safeQty = parm.getValue("SAFE_QTY");
		String stockQty = parm.getValue("STOCK_QTY");
		String phaType = parm.getValue("PHA_TYPE");
		 
    	boolean activeFlg = parm.getBoolean("ACTIVE_FLG");
    	String supCode = parm.getValue("SUP_CODE");//供应厂商 wanglong add 20150130
		// 用于查询安全库存量
		String childSql = " ";
		String sql = " SELECT A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, H.CHN_DESC,SUM(A.STOCK_QTY) AS STOCK_QTY, G.UNIT_CHN_DESC, "
				+ "        FLOOR(SUM(A.STOCK_QTY) / F.DOSAGE_QTY) || E.UNIT_CHN_DESC || MOD(SUM(A.STOCK_QTY), F.DOSAGE_QTY) || G.UNIT_CHN_DESC AS QTY, "
				+ "        A.RETAIL_PRICE * F.DOSAGE_QTY || '/' || E.UNIT_CHN_DESC || ';' ||   A.RETAIL_PRICE || '/' || G.UNIT_CHN_DESC AS PRICE, "
				+ "        A.VERIFYIN_PRICE AS STOCK_PRICE, SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE AS STOCK_AMT, SUM(A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT, "
				+ "        (SUM(A.STOCK_QTY) * A.RETAIL_PRICE - SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE) AS DIFF_AMT,'' AS BATCH_NO,'' AS VALID_DATE, "
				+ "		   A.STOCK_FLG,B.SAFE_QTY, D.PHA_TYPE, A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG,B.MATERIAL_LOC_CODE,F.DOSAGE_QTY,K.ORG_CHN_DESC "
				+ " FROM   IND_STOCK  A, IND_STOCKM  B,  SYS_FEE  C,  PHA_BASE  D, SYS_UNIT  E,PHA_TRANSUNIT   F, "
				+ "		  SYS_UNIT   G,SYS_DICTIONARY  H ,IND_ORG K ";
		if(parm.getValue("SUP_CODE_NEW").equals("Y")){
			sql += " ,IND_SYSPARM M ";
		}
		sql += " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE  AND A.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = C.ORDER_CODE  "
				+ "		 AND A.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = D.ORDER_CODE AND D.STOCK_UNIT = E.UNIT_CODE "
				+ "       AND A.ORDER_CODE = F.ORDER_CODE AND B.ORDER_CODE = F.ORDER_CODE  AND C.ORDER_CODE = F.ORDER_CODE "
				+ "       AND D.ORDER_CODE = F.ORDER_CODE AND D.DOSAGE_UNIT = G.UNIT_CODE  AND H.GROUP_ID = 'SYS_PHATYPE' AND D.TYPE_CODE = H.ID " 
				+ "       AND A.ORG_CODE = K.ORG_CODE ";
		if (!StringUtil.isNullString(orgCode)) {
			sql += "  AND A.ORG_CODE='" + orgCode + "' ";
		}
		if (null != orderCode && !"".equals(orderCode)) {
			sql += " AND A.ORDER_CODE='" + orderCode + "' ";
			childSql = " AND G.ORDER_CODE='" + orderCode + "' ";
		}
		if (null != batchNo && !"".equals(batchNo)) {
			sql += " AND A.BATCH_NO=='" + batchNo + "' ";
		}
		/*
		 * if(null != matCode && !"".equals(matCode)){ sql +=
		 * " AND A.MATERIAL_LOC_CODE=='" + matCode + "' "; }
		 */
		if (null != typeCode && !"".equals(typeCode)) {
			sql += " AND D.TYPE_CODE='" + typeCode + "' ";
		}
		if (null != safeQty && !"".equals(safeQty)) {
			sql += " AND A.ORDER_CODE in ( "
					+ " SELECT  NVL(G.ORDER_CODE ,0) AS ORDER_CODE "
					+ " FROM IND_STOCK G,IND_STOCKM K "
					+ " WHERE   G.ORG_CODE=K.ORG_CODE AND G.ORDER_CODE=K.ORDER_CODE ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += "  AND G.ORG_CODE='" + orgCode + "' ";
			}
			sql += childSql + " "
				+ " GROUP BY G.ORG_CODE,G.ORDER_CODE,K.SAFE_QTY "
				+ " HAVING SUM(G.STOCK_QTY)<K.SAFE_QTY   " + " ) ";
		}
		if (null != stockQty && !"".equals(stockQty)) {
			sql += " AND A.STOCK_QTY!=0 ";
		}
		//fux modify 20130115
   // 	if(activeFlg == true||"Y".equals(activeFlg)){
    		sql += " AND C.ACTIVE_FLG = 'Y'";
   // 	}else{
   // 		sql += " AND C.ACTIVE_FLG = 'N'";
   // 	}	
        if (!TCM_Transform.isNull(supCode)) {// wanglong add 20140130
            sql += " AND A.SUP_CODE='" + supCode + "' ";
        }
        if(parm.getValue("SUP_CODE_NEW").equals("Y")){
			sql += " AND (A.SUP_CODE <> M.MAIN_SUP_CODE OR D.TYPE_CODE='4') ";
		}
		sql += "  GROUP BY A.ORDER_CODE,C.ORDER_DESC, C.SPECIFICATION,H.CHN_DESC,E.UNIT_CHN_DESC,F.DOSAGE_QTY,G.UNIT_CHN_DESC,A.RETAIL_PRICE, "
				+ " A.VERIFYIN_PRICE,  A.STOCK_FLG,  B.SAFE_QTY,  D.PHA_TYPE,   A.ACTIVE_FLG,B.MATERIAL_LOC_CODE,K.ORG_CHN_DESC  ORDER BY A.ORDER_CODE ";
		return sql;
	}
	

	/**
	 * 查询药品信息
	 * 
	 * @param orderCode
	 *            药品代码
	 * @return String
	 */
	public static String getPHABaseInfo(String orderCode) {
		return " SELECT A.ORDER_CODE,A.ORDER_DESC,A.SPECIFICATION,A.RETAIL_PRICE,A.STOCK_PRICE,A.PURCH_UNIT,B.MAN_CODE,A.CONVERSION_RATIO,C.STOCK_UNIT "
				+ " FROM PHA_BASE A, SYS_FEE B,PHA_TRANSUNIT C "
				+ " WHERE A.ORDER_CODE = '"
				+ orderCode + "' AND A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE ";
	}

	/**
	 * 根据订单号和药品查询订购管理明细
	 * 
	 * @param purorder_no
	 *            订购单号
	 * @return
	 */
	public static String getPurOrderDByNoAndOrder(String purorder_no,
			String orderCode) {
		if (null == purorder_no || null == orderCode || "".equals(purorder_no)
				|| "".equals(orderCode)) {
			return "";
		}
		return "SELECT PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, "
				+ "BILL_UNIT, PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM "
				+ "FROM IND_PURORDERD "
				+ "WHERE PURORDER_NO='"
				+ purorder_no
				+ "' AND ORDER_CODE='" + orderCode + "' " + "ORDER BY SEQ_NO";
	}

	/**
	 * 修改代理商信息SQL
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20120907
	 */
	public static String updateAgent(TParm parm) {
		String sql = " UPDATE IND_AGENT " + " SET CONTRACT_PRICE="
				+ parm.getValue("CONTRACT_PRICE")
				+ ",LAST_ORDER_DATE=TO_DATE('"
				+ parm.getValue("LAST_ORDER_DATE")
				+ "','yyyy-MM-dd hh24:mi:ss'), " + " LAST_ORDER_QTY="
				+ parm.getValue("LAST_ORDER_QTY") + ",LAST_ORDER_PRICE="
				+ parm.getValue("LAST_ORDER_PRICE") + ", " + " LAST_ORDER_NO='"
				+ parm.getValue("LAST_ORDER_NO")
				+ "',LAST_VERIFY_DATE=SYSDATE, " + " LAST_VERIFY_PRICE="
				+ parm.getValue("LAST_VERIFY_PRICE") + ",OPT_USER='"
				+ parm.getValue("OPT_USER") + "', "
				+ " OPT_DATE=SYSDATE,OPT_TERM='" + parm.getValue("OPT_TERM")
				+ "' " + " WHERE SUP_CODE='" + parm.getValue("SUP_CODE")
				+ "' AND ORDER_CODE='" + parm.getValue("ORDER_CODE") + "' ";
		return sql;
	}

	/**
	 * 查询订购信息
	 * 
	 * @param parm
	 * @return
	 * @author yuanxm
	 * @date 20120911
	 */
	public static String getPurOrderDSqlByNo(String purorder_no) {
		String sql = " SELECT A.SUP_CODE,B.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, D.UNIT_CHN_DESC, "
				+ " B.PURORDER_QTY,B.PURORDER_PRICE ,C.MAN_CODE, A.PURORDER_NO ,B.SEQ_NO ,E.SPC_MEDICINE_CODE "
				+ " FROM IND_PURORDERM A,IND_PURORDERD B, SYS_FEE C,SYS_UNIT D, PHA_BASE E  "
				+ "WHERE A.PURORDER_NO='"
				+ purorder_no
				+ "' AND A.PURORDER_NO=B.PURORDER_NO AND B.ORDER_CODE=C.ORDER_CODE AND "
				+ "B.BILL_UNIT=D.UNIT_CODE AND B.ORDER_CODE=E.ORDER_CODE ";
		return sql;
	}

	/**
	 * 查询订购信息(区分供应商)
	 * 
	 * @param parm
	 * @return
	 * @author yuanxm
	 * @date 20120911
	 */
	public static String getPurOrderDSqlByNoNew(String purorder_no) {
		String sql = " SELECT A.SUP_CODE,B.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, D.UNIT_CHN_DESC, "
				+ " B.PURORDER_QTY,B.PURORDER_PRICE ,C.MAN_CODE, A.PURORDER_NO ,B.SEQ_NO ,E.SPC_MEDICINE_CODE,F.SUP_ORDER_CODE "
				+ " FROM IND_PURORDERM A,IND_PURORDERD B, SYS_FEE C,SYS_UNIT D, PHA_BASE E,IND_CODE_MAP F  "
				+ "WHERE A.PURORDER_NO='"
				+ purorder_no
				+ "' AND A.PURORDER_NO=B.PURORDER_NO AND B.ORDER_CODE=C.ORDER_CODE AND B.ORDER_CODE=F.ORDER_CODE AND "
				+ "B.BILL_UNIT=D.UNIT_CODE AND B.ORDER_CODE=E.ORDER_CODE ";
		return sql;
	}

	/**
	 * 更新药库主档电子标签ID
	 * 
	 * @param eleTagCode
	 * @param orgCode
	 * @param orderCode
	 * @return sql
	 * @author liyh
	 * @date 20121013
	 */
	public static String updateEleTagCode(String eleTagCode, String orgCode,
			String orderCode) {
		String sql = "  UPDATE IND_STOCKM SET ELETAG_CODE='" + eleTagCode
				+ "' WHERE ORG_CODE='" + orgCode + "' AND ORDER_CODE='"
				+ orderCode + "' ";
		return sql;
	}

	/**
	 * 根据条件查询库部门
	 * 
	 * @param condition
	 * @param noCondition
	 * @param region_code
	 * @return
	 * @author liyh
	 * @date 20121019
	 */
	public static String queryOrgInfo(String condition, String noCondition,
			String region_code) {
		String type = "WHERE REGION_CODE='" + region_code + "' ";
		if (!"".equals(condition)) {
			type += condition;
		}
		return "SELECT ORG_CODE AS ID,ORG_CHN_DESC AS NAME FROM IND_ORG "
				+ type + " ORDER BY ORG_CODE,SEQ";
	}

	/**
	 * 根据部门查询库存主档信息-自动拨补
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121019
	 */
	public static String queryStockM(String orgCode, String fixedType) {
		String condition = " ";
		if ("1".equals(fixedType)) {//
			condition = "   HAVING (SUM(B.STOCK_QTY)/D.DOSAGE_QTY) < A.SAFE_QTY ";
		} else if ("2".equals(fixedType)) {
			condition = "   HAVING (SUM(B.STOCK_QTY)/D.DOSAGE_QTY) < A.MIN_QTY ";
		}

		String sql = " SELECT A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,CEIL(SUM(B.STOCK_QTY)/D.DOSAGE_QTY) AS STOCK_QTY "
				+ " FROM IND_STOCKM A,IND_STOCK B,PHA_BASE C,PHA_TRANSUNIT D "
				+ " WHERE A.ORG_CODE='"
				+ orgCode  
				+ "' AND A.DISPENSE_FLG='Y' AND A.ORG_CODE=B.ORG_CODE AND A.ORDER_CODE=B.ORDER_CODE "
				+ "  AND A.ORDER_CODE=C.ORDER_CODE AND C.CTRLDRUGCLASS_CODE IS NULL AND A.ORDER_CODE=D.ORDER_CODE "
				+ condition   
				+ " "
				+ " GROUP BY A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY ,D.DOSAGE_QTY ";
		return sql;
	}

	/**
	 * 根据部门查询库存主档信息-自动拨补-赌麻
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121019
	 */
	public static String queryStockMDrug(String orgCode, String fixedType) {
		String condition = " ";
		if ("1".equals(fixedType)) {//
			condition = "   HAVING (SUM(B.STOCK_QTY)/E.DOSAGE_QTY)  < A.SAFE_QTY ";
		} else if ("2".equals(fixedType)) {
			condition = "   HAVING (SUM(B.STOCK_QTY)/E.DOSAGE_QTY)  < A.MIN_QTY ";
		}

		String sql = " SELECT A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,CEIL(SUM(B.STOCK_QTY)/E.DOSAGE_QTY) AS STOCK_QTY "
				+ " FROM IND_STOCKM A,IND_STOCK B ,PHA_BASE C,SYS_CTRLDRUGCLASS D,PHA_TRANSUNIT E "
				+ " WHERE A.ORG_CODE='"
				+ orgCode
				+ "' AND A.DISPENSE_FLG='Y' AND A.ORG_CODE=B.ORG_CODE AND A.ORDER_CODE=B.ORDER_CODE "
				+ "  AND A.ORDER_CODE=C.ORDER_CODE AND C.CTRLDRUGCLASS_CODE IS NOT NULL AND C.CTRLDRUGCLASS_CODE=D.CTRLDRUGCLASS_CODE " +
						" AND D.CTRL_FLG='Y' AND A.ORDER_CODE=E.ORDER_CODE "
				+ condition  
				+ " "
				+ " GROUP BY A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,E.DOSAGE_QTY ";
		return sql;
	}

	/**
	 * 根据部门查询库存主档信息-自动拨补移货建议-普药
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121019
	 */
	public static String queryStockMOfSuggest(String orgCode, String fixedType) {
		String condition = " ";
		if ("1".equals(fixedType)) {//
			condition = "   HAVING (SUM(B.STOCK_QTY)/D.DOSAGE_QTY) < A.SAFE_QTY ";
		} else if ("2".equals(fixedType)) {
			condition = "   HAVING (SUM(B.STOCK_QTY)/D.DOSAGE_QTY) < A.MIN_QTY ";
		}

		String sql = " SELECT A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,CEIL(SUM(B.STOCK_QTY)/D.DOSAGE_QTY/D.STOCK_QTY) AS STOCK_QTY "
				+ " FROM IND_STOCKM A,IND_STOCK B,PHA_BASE C  ,PHA_TRANSUNIT D,SYS_FEE E "
				+ " WHERE A.ORG_CODE='"
				+ orgCode
				+ "' AND  A.ORG_CODE=B.ORG_CODE AND A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE=E.ORDER_CODE AND E.ACTIVE_FLG='Y' "
				+ "  AND A.ORDER_CODE=C.ORDER_CODE AND C.CTRLDRUGCLASS_CODE IS NULL AND A.ORDER_CODE=D.ORDER_CODE  "
				+ condition
				+ " "  
				+ " GROUP BY A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY ,D.DOSAGE_QTY,D.STOCK_QTY ";
		System.out.println("sql-----:"+sql);
		return sql;
	}

	/**
	 * 根据部门查询库存主档信息-移货建议-赌麻
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121019
	 */                      
	public static String queryStockMDrugOfSuggest(String orgCode,
			String fixedType) {  
		String condition = " ";
		if ("1".equals(fixedType)) {//
			condition = "   HAVING (SUM(B.STOCK_QTY)/E.DOSAGE_QTY) < A.SAFE_QTY ";
		} else if ("2".equals(fixedType)) {
			condition = "   HAVING (SUM(B.STOCK_QTY)/E.DOSAGE_QTY) < A.MIN_QTY ";
		}

		String sql = " SELECT A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,CEIL(SUM(B.STOCK_QTY)/E.DOSAGE_QTY) AS STOCK_QTY "
				+ " FROM IND_STOCKM A,IND_STOCK B ,PHA_BASE C,SYS_CTRLDRUGCLASS D  ,PHA_TRANSUNIT E,SYS_FEE F "
				+ " WHERE A.ORG_CODE='"
				+ orgCode
				+ "' AND  A.ORG_CODE=B.ORG_CODE AND A.ORDER_CODE=B.ORDER_CODE AND A.ORDER_CODE=F.ORDER_CODE AND F.ACTIVE_FLG='Y' "
				+ "  AND A.ORDER_CODE=C.ORDER_CODE AND C.CTRLDRUGCLASS_CODE IS NOT NULL AND C.CTRLDRUGCLASS_CODE=D.CTRLDRUGCLASS_CODE "
				+ " AND D.CTRL_FLG='Y'  AND A.ORDER_CODE=E.ORDER_CODE "
				+ condition
				+ " "
				+ " GROUP BY A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY ,E.DOSAGE_QTY "
				+ " ORDER BY A.ORDER_CODE ";
		return sql;
	}

	/**
	 * 查询部门信息-自动拨补
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String queryOrgCodeAuto() {
		String sql = " SELECT  A.ORG_CODE,NVL(A.DISPENSE_ORG_CODE,'-1') AS DISPENSE_ORG_CODE ,B.ORG_TYPE "
				+ " FROM IND_STOCKM A,IND_ORG B  "
				+ " WHERE A.DISPENSE_FLG='Y'  AND A.ORG_CODE=B.ORG_CODE  "
				+ " GROUP BY A.ORG_CODE,A.DISPENSE_ORG_CODE  ,B.ORG_TYPE ";
		return sql;
	}

	/**
	 * 保存申请主档-自动拨补
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String saveRequestMAuto(TParm parm) {
		String sql = " INSERT INTO IND_REQUESTM(REQUEST_NO, REQTYPE_CODE, APP_ORG_CODE, TO_ORG_CODE, REQUEST_DATE, REQUEST_USER, REASON_CHN_DESC, "
				+ " UNIT_TYPE, URGENT_FLG, OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE,DRUG_CATEGORY,APPLY_TYPE) "
				+ " VALUES(" + " '"
				+ parm.getValue("REQUEST_NO")
				+ "', 'ATO', '"
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
				+ "', 'OPTUSER', sysdate,"
				+ " 'OPTID','"
				+ parm.getValue("REGION_CODE")
				+ "','"
				+ parm.getValue("DRUG_CATEGORY") + "','3' " + " ) ";
		return sql;
	}

	/**
	 * 保存申请明细档-自动拨补
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String saveRequestDAuto(TParm parm) {
		String sql = " INSERT INTO IND_REQUESTD(REQUEST_NO, SEQ_NO, ORDER_CODE, QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, UPDATE_FLG, "
				+ "  OPT_USER, OPT_DATE, OPT_TERM, VERIFYIN_PRICE, BATCH_SEQ)  "
				+ " VALUES("
				+ " '"
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
				+ ",0, '0','OPTUSER', sysdate,'OPTIP',0,0 " + " ) ";
		return sql;
	}

	/**
	 * 保存订购/移货主档-自动拨补
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String savePurOrderMAuto(TParm parm) {
		String sql = " INSERT INTO IND_PURORDERM (PURORDER_NO, PURORDER_DATE, ORG_CODE, SUP_CODE, RES_DELIVERY_DATE, REASON_CHN_DESC "
				+ "  , OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE,DRUG_CATEGORY,APPLY_TYPE) "
				+ " VALUES("
				+ " '"
				+ parm.getValue("PURORDER_NO")
				+ "',sysdate, '"
				+ parm.getValue("ORG_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("SUP_CODE")
				+ "',null,'"
				+ parm.getValue("REASON_CHN_DESC")
				+ "','OPTUSER', sysdate,'OPTIP','"
				+ parm.getValue("REGION_CODE")
				+ "','"
				+ parm.getValue("DRUG_CATEGORY") + "','3' " + " ) ";
		return sql;
	}

	/**
	 * 保存订购/移货明细档-自动拨补
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String savePurOrderDAuto(TParm parm) {
		String sql = " INSERT INTO IND_PURORDERD (PURORDER_NO, SEQ_NO, ORDER_CODE, PURORDER_QTY, GIFT_QTY, BILL_UNIT, "
				+ "  PURORDER_PRICE, ACTUAL_QTY, QUALITY_DEDUCT_AMT, UPDATE_FLG, OPT_USER, OPT_DATE, OPT_TERM) "
				+ " VALUES("
				+ " '"
				+ parm.getValue("PURORDER_NO")
				+ "', "
				+ parm.getValue("SEQ_NO")
				+ ", '"
				+ parm.getValue("ORDER_CODE")
				+ "',"
				+ "  "
				+ parm.getValue("PURORDER_QTY")
				+ ",0,'"
				+ parm.getValue("UNIT_CODE")
				+ "',"
				+ parm.getValue("PURCH_PRICE")
				+ ","
				+ "  0, 0,'0','OPTUSER', sysdate,'OPTIP' " + " ) ";
		return sql;
	}

	/**
	 * 查询药库部门类别
	 * 
	 * @param org_code
	 *            药库代码
	 * @return String
	 */
	public static String getSubOrgInfo(String org_code) {
		return "SELECT ORG_CODE,SUP_ORG_CODE FROM IND_ORG WHERE ORG_CODE = '"
				+ org_code + "'";
	}

	/**
	 * 更新IND_STOCK的料位
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @return String
	 * @author liyh
	 * @date 20121022
	 */
	public static String onUpdateStcokMaterialLocCode(String org_code,
			String order_code, String material_loc_code) {
		return "UPDATE IND_STOCKM SET MATERIAL_LOC_CODE ='" + material_loc_code
				+ "' WHERE ORG_CODE = '" + org_code + "' AND ORDER_CODE = '"
				+ order_code + "'";
	}

	/**
	 *判断药品是是否是麻精
	 * 
	 * @param org_code
	 *            String
	 * @param order_code
	 *            String
	 * @return
	 * @author liyh
	 * @date 20121025
	 */
	public static String isDrug(String orderCode) {
		return " SELECT ORDER_CODE FROM SYS_FEE WHERE ORDER_CODE='" + orderCode
				+ "' AND CTRL_FLG='Y' ";
	}

	/**
	 * 保存库存主动的电子标签和料位
	 * 
	 * @param parm
	 * @return String
	 * @author liyh
	 * @date 20121026
	 */
	public static String onSaveMatLocStockM(TParm parm) {
		String sql = " INSERT INTO IND_STOCKM "
				+ " (ORG_CODE, ORDER_CODE, REGION_CODE, DISPENSE_FLG, MM_USE_QTY, DD_USE_QTY, MAX_QTY, SAFE_QTY, MIN_QTY, ECONOMICBUY_QTY, "
				+ "  BUY_UNRECEIVE_QTY, STANDING_QTY, ACTIVE_FLG, OPT_USER, OPT_DATE, OPT_TERM, MATERIAL_LOC_CODE,MATERIAL_LOC_DESC,ELETAG_CODE ) "
				+ " VALUES " + " ('" + parm.getValue("ORG_CODE") + "', '"
				+ parm.getValue("ORDER_CODE") + "', '"
				+ parm.getValue("REGION_CODE") + "', "
				+ " 'N', 0,0,0,0,0,0,0,0,'N','" + parm.getValue("OPT_USER")
				+ "',sysdate,'" + parm.getValue("OPT_TERM") + "', " + " '"
				+ parm.getValue("MATERIAL_LOC_CODE") + "', '"
				+ parm.getValue("MATERIAL_LOC_DESC") + "', '"
				+ parm.getValue("ELETAG_CODE") + "' " + " ) ";
		return sql;
	}

	/**
	 * 更新库存主动的电子标签和料位
	 * 
	 * @param parm
	 * @return String
	 * @author liyh
	 * @date 20121026
	 */
	public static String onUpdateMatLocStockM(TParm parm) {
		String sql = " UPDATE  IND_STOCKM " + " SET MATERIAL_LOC_CODE='"
				+ parm.getValue("MATERIAL_LOC_CODE") + "', MATERIAL_LOC_DESC='"
				+ parm.getValue("MATERIAL_LOC_DESC") + "',"
				+ " ELETAG_CODE = '" + parm.getValue("ELETAG_CODE") + "' "
				+ " WHERE ORG_CODE='" + parm.getValue("ORG_CODE")
				+ "' AND ORDER_CODE = '" + parm.getValue("ORDER_CODE") + "' ";
		return sql;
	}

	/**
	 * 修改移货申请状态-审核
	 * 
	 * @return
	 * @date 20121029
	 * @author liyh
	 */
	public static String upDateStatusINDPourOrder(TParm parm) {
		/*
		 * return " UPDATE IND_PURORDERM SET CHECK_DATE=SYSDATE(),CHECK_USER='"
		 * + parm.getValue("OPT_USER") + "', " + " OPT_TERM='" +
		 * parm.getValue("OPT_TERM") + "',OPT_DATE=SYSDATE,OPT_USER='" +
		 * parm.getValue("OPT_USER") + "' " + " WHERE PURORDER_NO='" +
		 * parm.getValue("PURORDER_NO") + "' ";
		 */
		return " UPDATE IND_PURORDERM SET OPT_USER='D005'  WHERE PURORDER_NO='121026000001' ";
	}

	/**
	 * 根据药品代码获得PHA信息
	 * 
	 * @param order_code
	 * @return
	 */
	public static String getPHAInfoByOrderSpc(String order_code) {
		return "SELECT A.DOSAGE_UNIT, A.STOCK_UNIT, A.PURCH_UNIT, A.RETAIL_PRICE, A.TRADE_PRICE, "
				+ "A.STOCK_PRICE, B.PURCH_QTY, B.STOCK_QTY, B.DOSAGE_QTY, A.PHA_TYPE, B.MEDI_UNIT, B.MEDI_QTY,A.SPECIFICATION,A.ORDER_DESC "
				+ "FROM PHA_BASE A, PHA_TRANSUNIT B "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
				+ "AND A.ORDER_CODE = '"
				+ order_code + "'";
	}

	/**
	 * 药品明细账汇总查询
	 * 
	 * @return String
	 */
	public static String getINDPhaSumQuery(String org_code, String start_date,
			String end_date, String order_code, String drugLvl2) {
		String startTime = start_date.substring(0, 8);
		String endTime = end_date.substring(0, 8);
		String orderCodeSql = " ";
		if (StringUtils.isNotEmpty(order_code) && order_code.length() > 4) {
			orderCodeSql = " AND A.ORDER_CODE='" + order_code + "' ";
		}
		
		String drugLvl2Sql1 = "  ";
		String drugLvl2Sql2 = "  ";
		if(null != drugLvl2 && "Y".equals(drugLvl2)){
			drugLvl2Sql1 = "  AND B.CTRLDRUGCLASS_CODE='03'  ";
			drugLvl2Sql2 = "  AND A.ORDER_CODE=B.ORDER_CODE AND B.CTRLDRUGCLASS_CODE='03'  ";
		}
		
		String sql =

		" SELECT A1.ORG_CHN_DESC,                                                                                "
				+ "        A1.ORG_CODE,                                                                                    "
				+ "        A1.ORDER_CODE,                                                                                  "
				+ "        A1.ORDER_DESC,                                                                                  "
				+ "        A1.SPECIFICATION,                                                                               "
				+ "        A1.UNIT_CHN_DESC,                                                                               "
				+ "        TO_CHAR (A1.LAST_TOTSTOCK_QTY) AS LAST_TOTSTOCK_QTY,                                            "
				+ "        A1.LAST_TOTSTOCK_AMT,                                                                           "
				+ "        TO_CHAR (B1.STOCKIN_QTY) AS STOCKIN_QTY,                                                        "
				+ "        B1.STOCKIN_AMT,                                                                                 "
				+ "        TO_CHAR (B1.STOCKOUT_QTY) AS STOCKOUT_QTY,                                                      "
				+ "        B1.STOCKOUT_AMT,                                                                                "
				+ "        TO_CHAR (B1.CHECKMODI_QTY) AS CHECKMODI_QTY,                                                    "
				+ "        B1.CHECKMODI_AMT,                                                                               "
				+ "        TO_CHAR (C1.STOCK_QTY) AS STOCK_QTY ,                                                           "
				+ "        C1.STOCK_AMT                                                                                    "
				+ "   FROM (  SELECT C.ORG_CHN_DESC,                                                                       "
				+ // 查询上期结存
				"                  A.ORG_CODE,                                                                           "
				+ "                  A.ORDER_CODE,                                                                         "
				+ "                  B.ORDER_DESC,                                                                         "
				+ "                  B.SPECIFICATION,                                                                      "
				+ "                  D.UNIT_CHN_DESC,                                                                      "
				+ "                  SUM (A.LAST_TOTSTOCK_QTY) AS LAST_TOTSTOCK_QTY,                                       "
				+ "                  ROUND(SUM (A.LAST_TOTSTOCK_QTY * A.VERIFYIN_PRICE),7)                                          "
				+ "                     AS LAST_TOTSTOCK_AMT                                                               "
				+ "             FROM IND_DDSTOCK A,                                                                        "
				+ "                  PHA_BASE B,                                                                           "
				+ "                  IND_ORG C,                                                                            "
				+ "                  SYS_UNIT D                                                                            "
				+ "            WHERE     A.ORG_CODE = '"
				+ org_code
				+ "'                                                   "
				+ "                  AND A.TRANDATE = '"
				+ startTime
				+ "'                                                  "
				+ orderCodeSql
				+ "                  AND A.ORDER_CODE = B.ORDER_CODE                                                       "
				+ "                  AND A.ORG_CODE = C.ORG_CODE                                                           "
				+ "                  AND B.DOSAGE_UNIT = D.UNIT_CODE                                                       "
				+ drugLvl2Sql1
				+ "         GROUP BY C.ORG_CHN_DESC,                                                                       "
				+ "                  A.ORG_CODE,                                                                           "
				+ "                  A.ORDER_CODE,                                                                         "
				+ "                  B.ORDER_DESC,                                                                         "
				+ "                  B.SPECIFICATION,                                                                      "
				+ "                  D.UNIT_CHN_DESC) A1,                                                                  "
				+ "        (  SELECT A.ORG_CODE,                                                                             "
				+ // 查询本期汇总，出库，入库
				"                  A.ORDER_CODE,                                                                           "
				+ "                  SUM (A.IN_QTY) AS STOCKIN_QTY,                                                        "
				+ "                  ROUND(SUM (A.IN_QTY * A.VERIFYIN_PRICE),7) AS STOCKIN_AMT,                                     "
				+ "                  SUM (A.OUT_QTY) AS STOCKOUT_QTY,                                                      "
				+ "                  ROUND(SUM (A.OUT_QTY * A.VERIFYIN_PRICE),7) AS STOCKOUT_AMT,                                   "
				+ "                  SUM (A.CHECKMODI_QTY) AS CHECKMODI_QTY,                                               "
				+ "                  ROUND(SUM (A.CHECKMODI_QTY * A.VERIFYIN_PRICE),7) AS CHECKMODI_AMT                             "
				+ "             FROM IND_DDSTOCK A ,PHA_BASE B                                                                      "
				+ "            WHERE     A.ORG_CODE = '"
				+ org_code
				+ "'                                                   "
				+ drugLvl2Sql2
				+ orderCodeSql
				+ "                  AND TO_DATE (A.TRANDATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE (                        "
				+ "                                                                          '"
				+ start_date
				+ "',         "
				+ "                                                                          'YYYYMMDDHH24MISS')           "
				+ "                                                                   AND TO_DATE (                        "
				+ "                                                                          '"
				+ end_date
				+ "',           "
				+ "                                                                          'YYYYMMDDHH24MISS')           "
				+ "         GROUP BY A.ORG_CODE, A.ORDER_CODE) B1,                                                         "
				+ "        (  SELECT A.ORG_CODE,                                                                             "
				+ // 查询本期结存
				"                  A.ORDER_CODE,                                                                           "
				+ "                  SUM (A.STOCK_QTY) AS STOCK_QTY,                                                         "
				+ "                  ROUND(SUM (A.STOCK_QTY * A.VERIFYIN_PRICE),7) AS STOCK_AMT                                         "
				+ "             FROM IND_DDSTOCK   A, PHA_BASE B                                                                       "
				+ "            WHERE A.ORG_CODE = '"
				+ org_code
				+ "'   AND A.TRANDATE = '"
				+ endTime
				+ "'                      "
				+ drugLvl2Sql2
				+ orderCodeSql
				+ "         GROUP BY A.ORG_CODE, A.ORDER_CODE) C1                                                              "
				+ "  WHERE     A1.ORG_CODE = B1.ORG_CODE                                                                   "
				+ "        AND A1.ORDER_CODE = B1.ORDER_CODE                                                               "
				+ "        AND A1.ORG_CODE = C1.ORG_CODE                                                                   "
				+ "        AND A1.ORDER_CODE = C1.ORDER_CODE                                                               "
				+ " UNION ALL                                                                                              "
				+ " SELECT '' AS ORG_CHN_DESC,                                                                             "
				+ // union 合计
				"        '' AS ORG_CODE,                                                                                 "
				+ "        '合计 ' AS ORDER_CODE,                                                                           "
				+ "        '合计' AS ORDER_DESC,                                                                           "
				+ "        '' AS SPECIFICATION,                                                                            "
				+ "        '' AS UNIT_CHN_DESC,                                                                            "
				+ "        '' AS LAST_TOTSTOCK_QTY,                                                                        "
				+ "        ROUND(SUM (LAST_TOTSTOCK_AMT),7) AS LAST_TOTSTOCK_AMT,                                           "
				+ "        '' AS STOCKIN_QTY,                                                                              "
				+ "        ROUND(SUM (STOCKIN_AMT),7) AS STOCKIN_AMT,                                                       "
				+ "        '' AS STOCKOUT_QTY,                                                                             "
				+ "        ROUND(SUM (STOCKOUT_AMT),7) AS STOCKOUT_AMT,                                                     "
				+ "        '' AS CHECKMODI_QTY,                                                                            "
				+ "        ROUND(SUM (CHECKMODI_AMT),7) AS CHECKMODI_AMT,                                                   "
				+ "        '' AS STOCK_QTY,                                                                                "
				+ "        ROUND(SUM (STOCK_AMT),7) AS STOCK_AMT                                                            "
				+ "   FROM (SELECT A1.ORG_CHN_DESC,                                                                        "
				+ "                A1.ORG_CODE,                                                                            "
				+ "                A1.ORDER_CODE,                                                                          "
				+ "                A1.ORDER_DESC,                                                                          "
				+ "                A1.SPECIFICATION,                                                                       "
				+ "                A1.UNIT_CHN_DESC,                                                                       "
				+ "                TO_CHAR (A1.LAST_TOTSTOCK_QTY),                                                         "
				+ "                A1.LAST_TOTSTOCK_AMT,                                                                   "
				+ "                TO_CHAR (B1.STOCKIN_QTY),                                                               "
				+ "                B1.STOCKIN_AMT,                                                                         "
				+ "                TO_CHAR (B1.STOCKOUT_QTY),                                                              "
				+ "                B1.STOCKOUT_AMT,                                                                        "
				+ "                TO_CHAR (B1.CHECKMODI_QTY),                                                             "
				+ "                B1.CHECKMODI_AMT,                                                                       "
				+ "                TO_CHAR (C1.STOCK_QTY),                                                                 "
				+ "                C1.STOCK_AMT                                                                            "
				+ "           FROM (  SELECT C.ORG_CHN_DESC,                                                               "
				+ "                          A.ORG_CODE,                                                                   "
				+ "                          A.ORDER_CODE,                                                                 "
				+ "                          B.ORDER_DESC,                                                                 "
				+ "                          B.SPECIFICATION,                                                              "
				+ "                          D.UNIT_CHN_DESC,                                                              "
				+ "                          SUM (A.LAST_TOTSTOCK_QTY) AS LAST_TOTSTOCK_QTY,                               "
				+ "                          ROUND(SUM (A.LAST_TOTSTOCK_QTY * A.VERIFYIN_PRICE),7)                                  "
				+ "                             AS LAST_TOTSTOCK_AMT                                                       "
				+ "                     FROM IND_DDSTOCK A,                                                                "
				+ "                          PHA_BASE B,                                                                   "
				+ "                          IND_ORG C,                                                                    "
				+ "                          SYS_UNIT D                                                                    "
				+ "                    WHERE     A.ORG_CODE =  '"
				+ org_code
				+ "'                                          "
				+ "                          AND A.TRANDATE =  '"
				+ startTime
				+ "'                                         "
				+ drugLvl2Sql1
				+ orderCodeSql
				+ "                          AND A.ORDER_CODE = B.ORDER_CODE                                               "
				+ "                          AND A.ORG_CODE = C.ORG_CODE                                                   "
				+ "                          AND B.DOSAGE_UNIT = D.UNIT_CODE                                               "
				+ "                 GROUP BY C.ORG_CHN_DESC,                                                               "
				+ "                          A.ORG_CODE,                                                                   "
				+ "                          A.ORDER_CODE,                                                                 "
				+ "                          B.ORDER_DESC,                                                                 "
				+ "                          B.SPECIFICATION,                                                              "
				+ "                          D.UNIT_CHN_DESC) A1,                                                          "
				+ "                (  SELECT A.ORG_CODE,                                                                     "
				+ "                          A.ORDER_CODE,                                                                   "
				+ "                          SUM (A.IN_QTY) AS STOCKIN_QTY,                                                "
				+ "                          ROUND(SUM (A.IN_QTY * A.VERIFYIN_PRICE),7) AS STOCKIN_AMT,                             "
				+ "                          SUM (A.OUT_QTY) AS STOCKOUT_QTY,                                              "
				+ "                          ROUND(SUM (A.OUT_QTY * A.VERIFYIN_PRICE),7) AS STOCKOUT_AMT,                           "
				+ "                          SUM (A.CHECKMODI_QTY) AS CHECKMODI_QTY,                                       "
				+ "                          ROUND(SUM (A.CHECKMODI_QTY * A.VERIFYIN_PRICE),7)                                      "
				+ "                             AS CHECKMODI_AMT                                                           "
				+ "                     FROM IND_DDSTOCK A ,PHA_BASE B                                                                "
				+ "                    WHERE     A.ORG_CODE = '"
				+ org_code
				+ "'                                           "
				+ drugLvl2Sql2
				+ orderCodeSql
				+ "                          AND TO_DATE (A.TRANDATE, 'YYYYMMDDHH24MISS') BETWEEN TO_DATE (                "
				+ "                                                                                  '"
				+ start_date
				+ "', "
				+ "                                                                                  'YYYYMMDDHH24MISS')   "
				+ "                                                                           AND TO_DATE (                "
				+ "                                                                                  '"
				+ end_date
				+ "',   "
				+ "                                                                                  'YYYYMMDDHH24MISS')   "
				+ "                 GROUP BY A.ORG_CODE, A.ORDER_CODE) B1,                                                 "
				+ "                (  SELECT A.ORG_CODE,                                                                     "
				+ "                          A.ORDER_CODE,                                                                   "
				+ "                          SUM (A.STOCK_QTY) AS STOCK_QTY,                                                 "
				+ "                          ROUND(SUM (A.STOCK_QTY * A.VERIFYIN_PRICE),7) AS STOCK_AMT                                 "
				+ "                     FROM IND_DDSTOCK  A ,PHA_BASE FF                                                             "
				+ "                    WHERE A.ORG_CODE = '"
				+ org_code
				+ "'   AND A.TRANDATE = '"
				+ endTime
				+ "'              "
				+ orderCodeSql
				+ "                 GROUP BY A.ORG_CODE, A.ORDER_CODE) C1                                                      "
				+ "          WHERE     A1.ORG_CODE = B1.ORG_CODE                                                           "
				+ "                AND A1.ORDER_CODE = B1.ORDER_CODE                                                       "
				+ "                AND A1.ORG_CODE = C1.ORG_CODE                                                           "
				+ "                AND A1.ORDER_CODE = C1.ORDER_CODE)                                                      "
				+ " ORDER BY ORDER_CODE";
		return sql;
	}

	/**
	 * 部门库存查询-不显示批次序号(仅查麻精药品)
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20120808
	 */
	public static String getOrgStockQueryNotBatchSQLNew(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String batchNo = parm.getValue("BATCH_NO");
		String matCode = parm.getValue("MATERIAL_LOC_CODE");
		String typeCode = parm.getValue("TYPE_CODE");
		String safeQty = parm.getValue("SAFE_QTY");
		String stockQty = parm.getValue("STOCK_QTY");
		String phaType = parm.getValue("PHA_TYPE");
		String mjFlg = parm.getValue("MJ_FLG");
		// fux modify 20130115
		boolean activeFlg = parm.getBoolean("ACTIVE_FLG");
		String orgType = parm.getValue("ORG_TYPE");
		String supCode = parm.getValue("SUP_CODE");//供应厂商 wanglong add 20150130
		// 用于查询安全库存量
		String childSql = " ";
		String sql = " SELECT A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, H.CHN_DESC,SUM(A.STOCK_QTY) AS STOCK_QTY, G.UNIT_CHN_DESC, "
				+ "        FLOOR(SUM(A.STOCK_QTY) / F.DOSAGE_QTY) || E.UNIT_CHN_DESC || MOD(SUM(A.STOCK_QTY), F.DOSAGE_QTY) || G.UNIT_CHN_DESC AS QTY, "
				+ "        A.RETAIL_PRICE * F.DOSAGE_QTY || '/' || E.UNIT_CHN_DESC || ';' ||   A.RETAIL_PRICE || '/' || G.UNIT_CHN_DESC AS PRICE, "
				+ "        A.VERIFYIN_PRICE AS STOCK_PRICE, SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE AS STOCK_AMT, SUM(A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT, "
				+ "        (SUM(A.STOCK_QTY) * A.RETAIL_PRICE - SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE) AS DIFF_AMT,'' AS BATCH_NO,'' AS VALID_DATE, "
				+ "		   A.STOCK_FLG,B.SAFE_QTY, D.PHA_TYPE,A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG,J.CTRL_FLG,F.DOSAGE_QTY,K.ORG_CHN_DESC "
				+ " FROM   IND_STOCK  A, IND_STOCKM  B,  SYS_FEE  C,  PHA_BASE  D, SYS_UNIT  E,PHA_TRANSUNIT   F, "
				+ "		  SYS_UNIT   G,SYS_DICTIONARY  H,SYS_CTRLDRUGCLASS J,IND_ORG K "
				+ " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = C.ORDER_CODE  "
				+ "		 AND A.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = D.ORDER_CODE AND D.STOCK_UNIT = E.UNIT_CODE "
				+ "       AND A.ORDER_CODE = F.ORDER_CODE AND B.ORDER_CODE = F.ORDER_CODE  AND C.ORDER_CODE = F.ORDER_CODE "
				+ "       AND D.ORDER_CODE = F.ORDER_CODE AND D.DOSAGE_UNIT = G.UNIT_CODE  AND H.GROUP_ID = 'SYS_PHATYPE' AND D.TYPE_CODE = H.ID "
				+ "		 AND D.CTRLDRUGCLASS_CODE=J.CTRLDRUGCLASS_CODE AND J.CTRL_FLG='Y' AND A.ORG_CODE=K.ORG_CODE ";
		if (!StringUtil.isNullString(orgCode)) {
			sql += "   AND A.ORG_CODE='" + orgCode + "' ";
		}		
		String drugSql = "";
		if (null != orderCode && !"".equals(orderCode)) {
			sql += " AND A.ORDER_CODE='" + orderCode + "' ";
			childSql = " AND G.ORDER_CODE='" + orderCode + "' ";
			drugSql = "  AND A.ORDER_CODE='" + orderCode + "' ";
		}
		if (null != batchNo && !"".equals(batchNo)) {
			sql += " AND A.BATCH_NO=='" + batchNo + "' ";
		}
		if (null != matCode && !"".equals(matCode)) {
			sql += " AND A.MATERIAL_LOC_CODE=='" + matCode + "' ";
		}
		if (null != typeCode && !"".equals(typeCode)) {
			sql += " AND D.TYPE_CODE='" + typeCode + "' ";
		}
		if (null != safeQty && !"".equals(safeQty)) {
			sql += " AND A.ORDER_CODE in ( "
					+ " SELECT  NVL(G.ORDER_CODE ,0) AS ORDER_CODE "
					+ " FROM IND_STOCK G,IND_STOCKM K "
					+ " WHERE   G.ORG_CODE=K.ORG_CODE AND G.ORDER_CODE=K.ORDER_CODE  ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += "   AND G.ORG_CODE='" + orgCode + "' ";
			}	
			sql+= childSql
			+ " GROUP BY G.ORG_CODE,G.ORDER_CODE,K.SAFE_QTY "
			+ " HAVING SUM(G.STOCK_QTY)<K.SAFE_QTY   " + " ) ";
		}
		if (null != stockQty && !"".equals(stockQty)) {
			sql += " AND A.STOCK_QTY!=0 ";
		}
		// fux modify 20130115
	//	if (activeFlg == true || "Y".equals(activeFlg)) {
			sql += " AND C.ACTIVE_FLG = 'Y'";
	//	} else {
	//		sql += " AND C.ACTIVE_FLG = 'N'";
	//	}
        if (!TCM_Transform.isNull(supCode)) {// wanglong add 20140130
            sql += " AND A.SUP_CODE='" + supCode + "' ";
        }
		sql += "  GROUP BY A.ORDER_CODE,C.ORDER_DESC, C.SPECIFICATION,H.CHN_DESC,E.UNIT_CHN_DESC,F.DOSAGE_QTY,G.UNIT_CHN_DESC,A.RETAIL_PRICE, "
				+ " A.VERIFYIN_PRICE,  A.STOCK_FLG,  B.SAFE_QTY,  D.PHA_TYPE,A.ACTIVE_FLG,J.CTRL_FLG,K.ORG_CHN_DESC "; //ORDER BY A.ORDER_CODE 
		
		if(orgType != null && orgType.equals("C")){
			sql += " UNION ALL " ;
			sql += " SELECT A.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, H.CHN_DESC, A.STOCK_QTY AS STOCK_QTY, "+
						" G.UNIT_CHN_DESC,FLOOR (A.STOCK_QTY / F.DOSAGE_QTY)|| E.UNIT_CHN_DESC || MOD (A.STOCK_QTY, F.DOSAGE_QTY) || G.UNIT_CHN_DESC AS QTY,"+
						" A.RETAIL_PRICE * F.DOSAGE_QTY || '/'|| E.UNIT_CHN_DESC|| ';' || A.RETAIL_PRICE || '/' || G.UNIT_CHN_DESC AS PRICE, "+
						" A.VERIFYIN_PRICE AS STOCK_PRICE, A.STOCK_QTY * A.VERIFYIN_PRICE AS STOCK_AMT, "+
						" (A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT, (A.STOCK_QTY * A.RETAIL_PRICE - (A.STOCK_QTY) * A.VERIFYIN_PRICE) AS DIFF_AMT,"+
						" '' AS BATCH_NO, '' AS VALID_DATE, '' AS STOCK_FLG, 0 AS SAFE_QTY, D.PHA_TYPE, "+
						"  A.RETAIL_PRICE AS OWN_PRICE, 'Y' ACTIVE_FLG, J.CTRL_FLG,F.DOSAGE_QTY,A.ORG_CHN_DESC"+
                   " FROM (  SELECT COUNT (A.CONTAINER_ID) AS STOCK_QTY,A.ORDER_CODE, A.RETAIL_PRICE, A.VERIFYIN_PRICE,C.ORG_CHN_DESC "+
                   "         FROM IND_CONTAINERD A, IND_CABINET B, IND_ORG C "+
                   "         WHERE A.CABINET_ID = B.CABINET_ID "+
                   "           AND B.ORG_CODE = C.ORG_CODE ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += "   AND B.ORG_CODE='" + orgCode + "' ";
			}
	        if (!TCM_Transform.isNull(supCode)) {// wanglong add 20140130
	            sql += " AND A.SUP_CODE='" + supCode + "' ";
	        }
            sql += drugSql+
               " GROUP BY A.ORDER_CODE, A.RETAIL_PRICE, A.VERIFYIN_PRICE,C.ORG_CHN_DESC) A, "+
               " PHA_BASE D, SYS_UNIT E, PHA_TRANSUNIT F, SYS_UNIT G, SYS_DICTIONARY H, "+
               " SYS_CTRLDRUGCLASS J "+
               " WHERE     A.ORDER_CODE = D.ORDER_CODE "+
               " AND D.STOCK_UNIT = E.UNIT_CODE "+
               " AND A.ORDER_CODE = F.ORDER_CODE "+
               " AND D.DOSAGE_UNIT = G.UNIT_CODE "+
               "  AND H.GROUP_ID = 'SYS_PHATYPE' "+
               " AND D.TYPE_CODE = H.ID "+
               " AND D.CTRLDRUGCLASS_CODE = J.CTRLDRUGCLASS_CODE "+
               "  AND J.CTRL_FLG = 'Y' "+
               " GROUP BY A.ORDER_CODE, "+
               "  D.ORDER_DESC, "+
               "  D.SPECIFICATION, "+
               "  H.CHN_DESC, "+
               "  E.UNIT_CHN_DESC, "+
               "  F.DOSAGE_QTY, "+
               "  G.UNIT_CHN_DESC, "+
               "  A.RETAIL_PRICE, "+
               "  A.VERIFYIN_PRICE, "+
               "  D.PHA_TYPE, "+
               "  J.CTRL_FLG, "+
               "  A.STOCK_QTY "+ 
               "  A.ORG_CHN_DESC ";
              // "  ORDER BY A.ORDER_CODE ";
			 
		}
		return sql;
	}
	
	/**
	 * 部门库存查询-不显示批次序号(仅查麻精药品)
	 * 
	 * @param parm
	 * @return
	 * @author wangjc
	 * @date 20150611
	 */
	public static String getOrgStockQueryNotBatchSQLNewByInSubCode(TParm parm) {
		String orgCode = parm.getValue("ORG_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String batchNo = parm.getValue("BATCH_NO");
		String matCode = parm.getValue("MATERIAL_LOC_CODE");
		String typeCode = parm.getValue("TYPE_CODE");
		String safeQty = parm.getValue("SAFE_QTY");
		String stockQty = parm.getValue("STOCK_QTY");
		String phaType = parm.getValue("PHA_TYPE");
		String mjFlg = parm.getValue("MJ_FLG");
		// fux modify 20130115
		boolean activeFlg = parm.getBoolean("ACTIVE_FLG");
		String orgType = parm.getValue("ORG_TYPE");
		String supCode = parm.getValue("SUP_CODE");//供应厂商 wanglong add 20150130
		// 用于查询安全库存量
		String childSql = " ";
		String sql = " SELECT A.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION, H.CHN_DESC,SUM(A.STOCK_QTY) AS STOCK_QTY, G.UNIT_CHN_DESC, "
				+ "        FLOOR(SUM(A.STOCK_QTY) / F.DOSAGE_QTY) || E.UNIT_CHN_DESC || MOD(SUM(A.STOCK_QTY), F.DOSAGE_QTY) || G.UNIT_CHN_DESC AS QTY, "
				+ "        A.RETAIL_PRICE * F.DOSAGE_QTY || '/' || E.UNIT_CHN_DESC || ';' ||   A.RETAIL_PRICE || '/' || G.UNIT_CHN_DESC AS PRICE, "
				+ "        A.VERIFYIN_PRICE AS STOCK_PRICE, SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE AS STOCK_AMT, SUM(A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT, "
				+ "        (SUM(A.STOCK_QTY) * A.RETAIL_PRICE - SUM(A.STOCK_QTY) * A.VERIFYIN_PRICE) AS DIFF_AMT,'' AS BATCH_NO,'' AS VALID_DATE, "
				+ "		   A.STOCK_FLG,B.SAFE_QTY, D.PHA_TYPE,A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG,J.CTRL_FLG,F.DOSAGE_QTY,K.ORG_CHN_DESC "
				+ " FROM   IND_STOCK  A, IND_STOCKM  B,  SYS_FEE  C,  PHA_BASE  D, SYS_UNIT  E,PHA_TRANSUNIT   F, "
				+ "		  SYS_UNIT   G,SYS_DICTIONARY  H,SYS_CTRLDRUGCLASS J,IND_ORG K ";
		if(parm.getValue("SUP_CODE_NEW").equals("Y")){
			sql += " ,IND_SYSPARM M";
		}
		sql += " WHERE A.ORG_CODE = B.ORG_CODE AND A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE = C.ORDER_CODE AND B.ORDER_CODE = C.ORDER_CODE  "
				+ "		 AND A.ORDER_CODE = D.ORDER_CODE AND B.ORDER_CODE = D.ORDER_CODE AND C.ORDER_CODE = D.ORDER_CODE AND D.STOCK_UNIT = E.UNIT_CODE "
				+ "       AND A.ORDER_CODE = F.ORDER_CODE AND B.ORDER_CODE = F.ORDER_CODE  AND C.ORDER_CODE = F.ORDER_CODE "
				+ "       AND D.ORDER_CODE = F.ORDER_CODE AND D.DOSAGE_UNIT = G.UNIT_CODE  AND H.GROUP_ID = 'SYS_PHATYPE' AND D.TYPE_CODE = H.ID "
				+ "		 AND D.CTRLDRUGCLASS_CODE=J.CTRLDRUGCLASS_CODE AND J.CTRL_FLG='Y' AND A.ORG_CODE=K.ORG_CODE ";
		if (!StringUtil.isNullString(orgCode)) {
			sql += "   AND A.ORG_CODE='" + orgCode + "' ";
		}		
		String drugSql = "";
		if (null != orderCode && !"".equals(orderCode)) {
			sql += " AND A.ORDER_CODE='" + orderCode + "' ";
			childSql = " AND G.ORDER_CODE='" + orderCode + "' ";
			drugSql = "  AND A.ORDER_CODE='" + orderCode + "' ";
		}
		if (null != batchNo && !"".equals(batchNo)) {
			sql += " AND A.BATCH_NO=='" + batchNo + "' ";
		}
		if (null != matCode && !"".equals(matCode)) {
			sql += " AND A.MATERIAL_LOC_CODE=='" + matCode + "' ";
		}
		if (null != typeCode && !"".equals(typeCode)) {
			sql += " AND D.TYPE_CODE='" + typeCode + "' ";
		}
		if (null != safeQty && !"".equals(safeQty)) {
			sql += " AND A.ORDER_CODE in ( "
					+ " SELECT  NVL(G.ORDER_CODE ,0) AS ORDER_CODE "
					+ " FROM IND_STOCK G,IND_STOCKM K "
					+ " WHERE   G.ORG_CODE=K.ORG_CODE AND G.ORDER_CODE=K.ORDER_CODE  ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += "   AND G.ORG_CODE='" + orgCode + "' ";
			}	
			sql+= childSql
			+ " GROUP BY G.ORG_CODE,G.ORDER_CODE,K.SAFE_QTY "
			+ " HAVING SUM(G.STOCK_QTY)<K.SAFE_QTY   " + " ) ";
		}
		if (null != stockQty && !"".equals(stockQty)) {
			sql += " AND A.STOCK_QTY!=0 ";
		}
		// fux modify 20130115
	//	if (activeFlg == true || "Y".equals(activeFlg)) {
			sql += " AND C.ACTIVE_FLG = 'Y'";
	//	} else {
	//		sql += " AND C.ACTIVE_FLG = 'N'";
	//	}
        if (!TCM_Transform.isNull(supCode)) {// wanglong add 20140130
            sql += " AND A.SUP_CODE='" + supCode + "' ";
        }
        if(parm.getValue("SUP_CODE_NEW").equals("Y")){
			sql += " AND (A.SUP_CODE <> M.MAIN_SUP_CODE OR D.TYPE_CODE='4') ";
		}
		sql += "  GROUP BY A.ORDER_CODE,C.ORDER_DESC, C.SPECIFICATION,H.CHN_DESC,E.UNIT_CHN_DESC,F.DOSAGE_QTY,G.UNIT_CHN_DESC,A.RETAIL_PRICE, "
				+ " A.VERIFYIN_PRICE,  A.STOCK_FLG,  B.SAFE_QTY,  D.PHA_TYPE,A.ACTIVE_FLG,J.CTRL_FLG,K.ORG_CHN_DESC "; //ORDER BY A.ORDER_CODE 
		
		if(orgType != null && orgType.equals("C")){
			sql += " UNION ALL " ;
			sql += " SELECT A.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, H.CHN_DESC, A.STOCK_QTY AS STOCK_QTY, "+
						" G.UNIT_CHN_DESC,FLOOR (A.STOCK_QTY / F.DOSAGE_QTY)|| E.UNIT_CHN_DESC || MOD (A.STOCK_QTY, F.DOSAGE_QTY) || G.UNIT_CHN_DESC AS QTY,"+
						" A.RETAIL_PRICE * F.DOSAGE_QTY || '/'|| E.UNIT_CHN_DESC|| ';' || A.RETAIL_PRICE || '/' || G.UNIT_CHN_DESC AS PRICE, "+
						" A.VERIFYIN_PRICE AS STOCK_PRICE, A.STOCK_QTY * A.VERIFYIN_PRICE AS STOCK_AMT, "+
						" (A.STOCK_QTY) * A.RETAIL_PRICE AS OWN_AMT, (A.STOCK_QTY * A.RETAIL_PRICE - (A.STOCK_QTY) * A.VERIFYIN_PRICE) AS DIFF_AMT,"+
						" '' AS BATCH_NO, '' AS VALID_DATE, '' AS STOCK_FLG, 0 AS SAFE_QTY, D.PHA_TYPE, "+
						"  A.RETAIL_PRICE AS OWN_PRICE, 'Y' ACTIVE_FLG, J.CTRL_FLG,F.DOSAGE_QTY,A.ORG_CHN_DESC"+
                   " FROM (  SELECT COUNT (A.CONTAINER_ID) AS STOCK_QTY,A.ORDER_CODE, A.RETAIL_PRICE, A.VERIFYIN_PRICE,C.ORG_CHN_DESC "+
                   "         FROM IND_CONTAINERD A, IND_CABINET B, IND_ORG C "+
                   "         WHERE A.CABINET_ID = B.CABINET_ID "+
                   "           AND B.ORG_CODE = C.ORG_CODE ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += "   AND B.ORG_CODE='" + orgCode + "' ";
			}
	        if (!TCM_Transform.isNull(supCode)) {// wanglong add 20140130
	            sql += " AND A.SUP_CODE='" + supCode + "' ";
	        }
            sql += drugSql+
               " GROUP BY A.ORDER_CODE, A.RETAIL_PRICE, A.VERIFYIN_PRICE,C.ORG_CHN_DESC) A, "+
               " PHA_BASE D, SYS_UNIT E, PHA_TRANSUNIT F, SYS_UNIT G, SYS_DICTIONARY H, "+
               " SYS_CTRLDRUGCLASS J "+
               " WHERE     A.ORDER_CODE = D.ORDER_CODE "+
               " AND D.STOCK_UNIT = E.UNIT_CODE "+
               " AND A.ORDER_CODE = F.ORDER_CODE "+
               " AND D.DOSAGE_UNIT = G.UNIT_CODE "+
               "  AND H.GROUP_ID = 'SYS_PHATYPE' "+
               " AND D.TYPE_CODE = H.ID "+
               " AND D.CTRLDRUGCLASS_CODE = J.CTRLDRUGCLASS_CODE "+
               "  AND J.CTRL_FLG = 'Y' "+
               " GROUP BY A.ORDER_CODE, "+
               "  D.ORDER_DESC, "+
               "  D.SPECIFICATION, "+
               "  H.CHN_DESC, "+
               "  E.UNIT_CHN_DESC, "+
               "  F.DOSAGE_QTY, "+
               "  G.UNIT_CHN_DESC, "+
               "  A.RETAIL_PRICE, "+
               "  A.VERIFYIN_PRICE, "+
               "  D.PHA_TYPE, "+
               "  J.CTRL_FLG, "+
               "  A.STOCK_QTY "+ 
               "  A.ORG_CHN_DESC ";
              // "  ORDER BY A.ORDER_CODE ";
			 
		}
		return sql;
	}

	/**
	 * 部门库存查询-显示批次序号(仅查麻精药品)
	 * 
	 * @param parm
	 * @return
	 * @author liyh
	 * @date 20120808
	 */
	public static String getOrgStockDrugQuery(TParm parm){
		String orgCode = parm.getValue("ORG_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String batchNo = parm.getValue("BATCH_NO");
		String matCode = parm.getValue("MATERIAL_LOC_CODE");
		String typeCode = parm.getValue("TYPE_CODE");
		String safeQty = parm.getValue("SAFE_QTY");
		String stockQty = parm.getValue("STOCK_QTY");
		String phaType = parm.getValue("PHA_TYPE");
		String supCode = parm.getValue("SUP_CODE");//供应厂商 wanglong add 20150130
    	boolean activeFlg = parm.getBoolean("ACTIVE_FLG");
    	String orgType = parm.getValue("ORG_TYPE");
    	String sql = "SELECT A.ORDER_CODE, C.ORDER_DESC, C.SPECIFICATION, H.CHN_DESC, A.STOCK_QTY,  "+
			       	 "       G.UNIT_CHN_DESC,  "+
			         "       FLOOR (A.STOCK_QTY / F.DOSAGE_QTY)  "+
			         "       || E.UNIT_CHN_DESC    "+
			         "       || MOD (A.STOCK_QTY, F.DOSAGE_QTY)    "+
			         "       || G.UNIT_CHN_DESC AS QTY,   "+
			         "       A.RETAIL_PRICE * F.DOSAGE_QTY    "+
			         "       || '/'    "+
			         "       || E.UNIT_CHN_DESC    "+
			         "       || ';'    "+
			         "        || A.RETAIL_PRICE    "+
			         "       || '/'    "+
			         "        || G.UNIT_CHN_DESC AS PRICE,    "+
			         "       A.VERIFYIN_PRICE AS STOCK_PRICE, A.STOCK_QTY * A.VERIFYIN_PRICE AS STOCK_AMT,    "+
			         "       A.STOCK_QTY * C.OWN_PRICE AS OWN_AMT,    "+
			         "       (A.STOCK_QTY * A.RETAIL_PRICE - A.STOCK_QTY * A.VERIFYIN_PRICE    "+
			         "       ) AS DIFF_AMT, A.BATCH_NO, A.VALID_DATE, A.STOCK_FLG, B.SAFE_QTY,    "+
			         "       D.PHA_TYPE , A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG,J.CTRL_FLG,F.DOSAGE_QTY,K.ORG_CHN_DESC   "+
			         "        FROM IND_STOCK A,   "+
			         "         IND_STOCKM B,    "+
			         "         SYS_FEE C,    "+
			         "         PHA_BASE D,    "+
			         "        SYS_UNIT E,   "+
			         "        PHA_TRANSUNIT F,    "+
			         "        SYS_UNIT G,   "+
			         "       SYS_DICTIONARY H,    "+
			         "        SYS_CTRLDRUGCLASS J," +
			         "        IND_ORG K   "+
			         "       WHERE A.ORG_CODE = B.ORG_CODE    "+
			         "       AND A.ORDER_CODE = B.ORDER_CODE    "+
			         "        AND A.ORDER_CODE = C.ORDER_CODE    "+
			         "        AND A.ORDER_CODE = D.ORDER_CODE    "+
			         "       AND D.STOCK_UNIT = E.UNIT_CODE    "+
			         "        AND A.ORDER_CODE = F.ORDER_CODE   "+
			         "       AND D.DOSAGE_UNIT = G.UNIT_CODE   "+
			         "       AND H.GROUP_ID = 'SYS_PHATYPE'    "+
			         "       AND D.TYPE_CODE = H.ID    "+
			         "       AND D.CTRLDRUGCLASS_CODE=J.CTRLDRUGCLASS_CODE AND J.CTRL_FLG='Y' AND A.ORG_CODE = K.ORG_CODE   ";
    	if(orgCode != null && !orgCode.equals("")){
    		sql += " AND A.ORG_CODE='"+orgCode+"' ";
    	}
    	String drugSql = "";
    	if(orderCode !=null && !orderCode.equals("")){
    		sql += " AND A.ORDER_CODE='"+orderCode+"' ";
    		drugSql +=  " AND A.ORDER_CODE='"+orderCode+"' ";
    	}
    	if(batchNo !=null && !batchNo.equals("")){
    		sql += " AND A.BATCH_NO='"+batchNo+"' ";
    	}
    	if( typeCode!=null && !typeCode.equals("")){
    		sql += " AND D.TYPE_CODE='"+typeCode+"' ";
    	}
    	if( matCode!=null && !matCode.equals("")){
    		sql += " AND A.MATERIAL_LOC_CODE='"+matCode+"' ";
    	}
    	if (null != safeQty && !"".equals(safeQty)) {
    		sql += " AND B.SAFE_QTY>A.STOCK_QTY " ;
    	}
    	
    	if (null != stockQty && !"".equals(stockQty)) {
			sql += " AND A.STOCK_QTY!=0 ";
		}
        if (!TCM_Transform.isNull(supCode)) {//wanglong add 20140130
            sql += " AND A.SUP_CODE='"+supCode+"' ";
        }
    	 
	    //sql += "  ORDER BY A.ORDER_CODE,A.VALID_DATE ";
    	
    	
	    if(orgType != null && orgType.equals("C")){
			sql += " UNION ALL " ;
			sql += " SELECT A.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, H.CHN_DESC, A.STOCK_QTY AS STOCK_QTY,  "+
                   "        G.UNIT_CHN_DESC,   "+
				   "        FLOOR (A.STOCK_QTY / F.DOSAGE_QTY)    "+
				   "        || E.UNIT_CHN_DESC     "+
				   "        || MOD (A.STOCK_QTY, F.DOSAGE_QTY)     "+
				   "        || G.UNIT_CHN_DESC AS QTY,     "+
				   "        A.RETAIL_PRICE * F.DOSAGE_QTY    "+
				"        || '/'     "+
				"        || E.UNIT_CHN_DESC     "+
				"        || ';'     "+
				"        || A.RETAIL_PRICE    "+
				"        || '/'    "+
				"        || G.UNIT_CHN_DESC AS PRICE,    "+
				"         A.VERIFYIN_PRICE AS STOCK_PRICE, A.STOCK_QTY* A.VERIFYIN_PRICE AS STOCK_AMT,    "+
				"         A.STOCK_QTY * A.RETAIL_PRICE AS OWN_AMT,    "+
				"         (A.STOCK_QTY * A.RETAIL_PRICE - A.STOCK_QTY * A.VERIFYIN_PRICE     "+
				"         ) AS DIFF_AMT, A.BATCH_NO, A.VALID_DATE, 'N' AS STOCK_FLG, 0 AS SAFE_QTY,    "+ 
				"          D.PHA_TYPE , A.RETAIL_PRICE AS OWN_PRICE, 'Y' AS ACTIVE_FLG,J.CTRL_FLG,F.DOSAGE_QTY,A.ORG_CHN_DESC   "+
				"        FROM ( SELECT COUNT(A.CONTAINER_ID) AS STOCK_QTY ,A.BATCH_NO,A.VALID_DATE ,A.CABINET_ID ,A.ORDER_CODE,C.ORG_CHN_DESC  "+
				"                    A.VERIFYIN_PRICE ,A.RETAIL_PRICE  "+
				"             FROM IND_CONTAINERD A , IND_CABINET B , IND_ORG C "+
				"             WHERE A.CABINET_ID=B.CABINET_ID "+
				"               AND B.ORG_CODE = C.ORG_CODE ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += " AND B.ORG_CODE='"+orgCode+ "'  ";
			}
            if (!TCM_Transform.isNull(supCode)) {//wanglong add 20140130
                sql += " AND A.SUP_CODE='"+supCode+"' ";
            }
				sql += drugSql+
				"             GROUP BY A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,A.CABINET_ID,A.RETAIL_PRICE,A.VERIFYIN_PRICE,C.ORG_CHN_DESC  ) A, "+
				"           PHA_BASE D,    "+
				"          SYS_UNIT E,    "+
                   "          PHA_TRANSUNIT F,   "+
				"           SYS_UNIT G,   "+
				"          SYS_DICTIONARY H,    "+
				"          SYS_CTRLDRUGCLASS J   "+
				"        WHERE     "+
				"         A.ORDER_CODE = D.ORDER_CODE   "+
				"         AND D.STOCK_UNIT = E.UNIT_CODE   "+
				"         AND A.ORDER_CODE = F.ORDER_CODE   "+
				"        AND D.DOSAGE_UNIT = G.UNIT_CODE  "+
				"        AND H.GROUP_ID = 'SYS_PHATYPE'    "+
				"        AND D.TYPE_CODE = H.ID    "+
				"        AND D.CTRLDRUGCLASS_CODE=J.CTRLDRUGCLASS_CODE AND J.CTRL_FLG='Y'   " ;
				//"        ORDER BY A.ORDER_CODE,A.VALID_DATE " ;
	    }
    	return sql;
    	
	}
	
	/**
	 * 部门库存查询-显示批次序号(仅查麻精药品)
	 * 
	 * @param parm
	 * @return
	 * @author wangjc
	 * @date 20150611
	 */
	public static String getOrgStockDrugQueryBySupCode(TParm parm){
		String orgCode = parm.getValue("ORG_CODE");
		String orderCode = parm.getValue("ORDER_CODE");
		String batchNo = parm.getValue("BATCH_NO");
		String matCode = parm.getValue("MATERIAL_LOC_CODE");
		String typeCode = parm.getValue("TYPE_CODE");
		String safeQty = parm.getValue("SAFE_QTY");
		String stockQty = parm.getValue("STOCK_QTY");
		String phaType = parm.getValue("PHA_TYPE");
		String supCode = parm.getValue("SUP_CODE");//供应厂商 wanglong add 20150130
    	boolean activeFlg = parm.getBoolean("ACTIVE_FLG");
    	String orgType = parm.getValue("ORG_TYPE");
    	String sql = "SELECT A.ORDER_CODE, C.ORDER_DESC, C.SPECIFICATION, H.CHN_DESC, A.STOCK_QTY,  "+
			       	 "       G.UNIT_CHN_DESC,  "+
			         "       FLOOR (A.STOCK_QTY / F.DOSAGE_QTY)  "+
			         "       || E.UNIT_CHN_DESC    "+
			         "       || MOD (A.STOCK_QTY, F.DOSAGE_QTY)    "+
			         "       || G.UNIT_CHN_DESC AS QTY,   "+
			         "       A.RETAIL_PRICE * F.DOSAGE_QTY    "+
			         "       || '/'    "+
			         "       || E.UNIT_CHN_DESC    "+
			         "       || ';'    "+
			         "        || A.RETAIL_PRICE    "+
			         "       || '/'    "+
			         "        || G.UNIT_CHN_DESC AS PRICE,    "+
			         "       A.VERIFYIN_PRICE AS STOCK_PRICE, A.STOCK_QTY * A.VERIFYIN_PRICE AS STOCK_AMT,    "+
			         "       A.STOCK_QTY * C.OWN_PRICE AS OWN_AMT,    "+
			         "       (A.STOCK_QTY * A.RETAIL_PRICE - A.STOCK_QTY * A.VERIFYIN_PRICE    "+
			         "       ) AS DIFF_AMT, A.BATCH_NO, A.VALID_DATE, A.STOCK_FLG, B.SAFE_QTY,    "+
			         "       D.PHA_TYPE , A.RETAIL_PRICE AS OWN_PRICE, A.ACTIVE_FLG,J.CTRL_FLG,F.DOSAGE_QTY,K.ORG_CHN_DESC   "+
			         "        FROM IND_STOCK A,   "+
			         "         IND_STOCKM B,    "+
			         "         SYS_FEE C,    "+
			         "         PHA_BASE D,    "+
			         "        SYS_UNIT E,   "+
			         "        PHA_TRANSUNIT F,    "+
			         "        SYS_UNIT G,   "+
			         "       SYS_DICTIONARY H,    "+
			         "        SYS_CTRLDRUGCLASS J," +
			         "        IND_ORG K   "+
			         "       WHERE A.ORG_CODE = B.ORG_CODE    "+
			         "       AND A.ORDER_CODE = B.ORDER_CODE    "+
			         "        AND A.ORDER_CODE = C.ORDER_CODE    "+
			         "        AND A.ORDER_CODE = D.ORDER_CODE    "+
			         "       AND D.STOCK_UNIT = E.UNIT_CODE    "+
			         "        AND A.ORDER_CODE = F.ORDER_CODE   "+
			         "       AND D.DOSAGE_UNIT = G.UNIT_CODE   "+
			         "       AND H.GROUP_ID = 'SYS_PHATYPE'    "+
			         "       AND D.TYPE_CODE = H.ID    "+
			         "       AND D.CTRLDRUGCLASS_CODE=J.CTRLDRUGCLASS_CODE AND J.CTRL_FLG='Y' AND A.ORG_CODE = K.ORG_CODE   ";
    	if(orgCode != null && !orgCode.equals("")){
    		sql += " AND A.ORG_CODE='"+orgCode+"' ";
    	}
    	String drugSql = "";
    	if(orderCode !=null && !orderCode.equals("")){
    		sql += " AND A.ORDER_CODE='"+orderCode+"' ";
    		drugSql +=  " AND A.ORDER_CODE='"+orderCode+"' ";
    	}
    	if(batchNo !=null && !batchNo.equals("")){
    		sql += " AND A.BATCH_NO='"+batchNo+"' ";
    	}
    	if( typeCode!=null && !typeCode.equals("")){
    		sql += " AND D.TYPE_CODE='"+typeCode+"' ";
    	}
    	if( matCode!=null && !matCode.equals("")){
    		sql += " AND A.MATERIAL_LOC_CODE='"+matCode+"' ";
    	}
    	if (null != safeQty && !"".equals(safeQty)) {
    		sql += " AND B.SAFE_QTY>A.STOCK_QTY " ;
    	}
    	
    	if (null != stockQty && !"".equals(stockQty)) {
			sql += " AND A.STOCK_QTY!=0 ";
		}
        if (!TCM_Transform.isNull(supCode)) {//wanglong add 20140130
            sql += " AND A.SUP_CODE='"+supCode+"' ";
        }
    	 
	    //sql += "  ORDER BY A.ORDER_CODE,A.VALID_DATE ";
    	
    	
	    if(orgType != null && orgType.equals("C")){
			sql += " UNION ALL " ;
			sql += " SELECT A.ORDER_CODE, D.ORDER_DESC, D.SPECIFICATION, H.CHN_DESC, A.STOCK_QTY AS STOCK_QTY,  "+
                   "        G.UNIT_CHN_DESC,   "+
				   "        FLOOR (A.STOCK_QTY / F.DOSAGE_QTY)    "+
				   "        || E.UNIT_CHN_DESC     "+
				   "        || MOD (A.STOCK_QTY, F.DOSAGE_QTY)     "+
				   "        || G.UNIT_CHN_DESC AS QTY,     "+
				   "        A.RETAIL_PRICE * F.DOSAGE_QTY    "+
				"        || '/'     "+
				"        || E.UNIT_CHN_DESC     "+
				"        || ';'     "+
				"        || A.RETAIL_PRICE    "+
				"        || '/'    "+
				"        || G.UNIT_CHN_DESC AS PRICE,    "+
				"         A.VERIFYIN_PRICE AS STOCK_PRICE, A.STOCK_QTY* A.VERIFYIN_PRICE AS STOCK_AMT,    "+
				"         A.STOCK_QTY * A.RETAIL_PRICE AS OWN_AMT,    "+
				"         (A.STOCK_QTY * A.RETAIL_PRICE - A.STOCK_QTY * A.VERIFYIN_PRICE     "+
				"         ) AS DIFF_AMT, A.BATCH_NO, A.VALID_DATE, 'N' AS STOCK_FLG, 0 AS SAFE_QTY,    "+ 
				"          D.PHA_TYPE , A.RETAIL_PRICE AS OWN_PRICE, 'Y' AS ACTIVE_FLG,J.CTRL_FLG,F.DOSAGE_QTY,A.ORG_CHN_DESC   "+
				"        FROM ( SELECT COUNT(A.CONTAINER_ID) AS STOCK_QTY ,A.BATCH_NO,A.VALID_DATE ,A.CABINET_ID ,A.ORDER_CODE,C.ORG_CHN_DESC  "+
				"                    A.VERIFYIN_PRICE ,A.RETAIL_PRICE  "+
				"             FROM IND_CONTAINERD A , IND_CABINET B , IND_ORG C "+
				"             WHERE A.CABINET_ID=B.CABINET_ID "+
				"               AND B.ORG_CODE = C.ORG_CODE ";
			if (!StringUtil.isNullString(orgCode)) {
				sql += " AND B.ORG_CODE='"+orgCode+ "'  ";
			}
            if (!TCM_Transform.isNull(supCode)) {//wanglong add 20140130
                sql += " AND A.SUP_CODE='"+supCode+"' ";
            }
				sql += drugSql+
				"             GROUP BY A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,A.CABINET_ID,A.RETAIL_PRICE,A.VERIFYIN_PRICE,C.ORG_CHN_DESC  ) A, "+
				"           PHA_BASE D,    "+
				"          SYS_UNIT E,    "+
                   "          PHA_TRANSUNIT F,   "+
				"           SYS_UNIT G,   "+
				"          SYS_DICTIONARY H,    "+
				"          SYS_CTRLDRUGCLASS J   "+
				"        WHERE     "+
				"         A.ORDER_CODE = D.ORDER_CODE   "+
				"         AND D.STOCK_UNIT = E.UNIT_CODE   "+
				"         AND A.ORDER_CODE = F.ORDER_CODE   "+
				"        AND D.DOSAGE_UNIT = G.UNIT_CODE  "+
				"        AND H.GROUP_ID = 'SYS_PHATYPE'    "+
				"        AND D.TYPE_CODE = H.ID    "+
				"        AND D.CTRLDRUGCLASS_CODE=J.CTRLDRUGCLASS_CODE AND J.CTRL_FLG='Y'   " ;
				//"        ORDER BY A.ORDER_CODE,A.VALID_DATE " ;
	    }
    	return sql;
    	
	}
	
	/**
	 * 比较IND_VERIFYIND与IND_ACCOUNT
	 * 
	 * @param START_DATE
	 * @param END_DATE
	 * @author shendr
	 * @return
	 */
	public static String getTwoTable(String START_DATE, String END_DATE) {
//		String sql = "SELECT A.ORDER_CODE,NVL(A.AQTY,0) CQTY,NVL(B.BQTY,0) DQTY,NVL(A.AQTY,0) - NVL(B.BQTY,0) D_VALUE, "
//				+ "C.ORDER_DESC,C.SPECIFICATION,C.STOCK_UNIT,A.RETAIL_PRICE,A.VERIFYIN_PRICE "
//				+ "FROM (SELECT D.ORDER_CODE,SUM(D.VERIFYIN_QTY) AQTY,D.RETAIL_PRICE,D.VERIFYIN_PRICE FROM IND_VERIFYINM A,IND_VERIFYIND D "
//				+ "  WHERE A.VERIFYIN_NO = D.VERIFYIN_NO ";
//		if (!StringUtil.isNullString(START_DATE)
//				&& !StringUtil.isNullString(END_DATE)) {
//			sql += "AND VERIFYIN_DATE BETWEEN TO_DATE('" + START_DATE
//					+ "','yyyy-mm-dd hh24:mi:ss') AND TO_DATE('" + END_DATE
//					+ "','yyyy-mm-dd hh24:mi:ss') ";
//		}
//		sql += " GROUP BY D.ORDER_CODE,D.RETAIL_PRICE,D.VERIFYIN_PRICE) A, "
//				+ " (SELECT ORDER_CODE,SUM(ACCOUNT_QTY) BQTY  "
//				+ "  FROM IND_ACCOUNT " + "   GROUP BY ORDER_CODE) B, "
//				+ "   PHA_BASE C " + "WHERE A.ORDER_CODE = B.ORDER_CODE(+) "
//				+ "AND A.ORDER_CODE = C.ORDER_CODE ";
		String sql = "SELECT X.ORDER_CODE,X.ORDER_DESC,X.SPECIFICATION,X.UNIT_CHN_DESC,SUM(X.CQTY) AS CQTY, "
            +"SUM(X.DQTY) AS DQTY,SUM(X.DQTY)-SUM(X.CQTY) AS D_VALUE "
            +"FROM( "
            +"SELECT B.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,D.UNIT_CHN_DESC, "
            +"B.VERIFYIN_QTY AS CQTY,0 AS DQTY,B.VERIFYIN_QTY AS D_VALUE "
            +"FROM IND_VERIFYINM A,IND_VERIFYIND B,PHA_BASE C,SYS_UNIT D "
            +"WHERE A.VERIFYIN_DATE BETWEEN TO_DATE('"+START_DATE
            +"','yyyy-mm-dd hh24:mi:ss') "
            +"AND TO_DATE('"+END_DATE
            +"','yyyy-mm-dd hh24:mi:ss') " 
            +"AND A.DRUG_CATEGORY='1' " 
            +"AND B.VERIFYIN_NO=A.VERIFYIN_NO " 
            +"AND (SELECT COUNT(E.CLOSE_DATE) " 
            +"FROM IND_ACCOUNT E " 
            +"WHERE E.ORDER_CODE=B.ORDER_CODE) <= 0 " 
            +"AND C.ORDER_CODE=B.ORDER_CODE " 
            +"AND D.UNIT_CODE=B.BILL_UNIT " 
            +"UNION ALL " 
            +"SELECT A.ORDER_CODE,B.ORDER_DESC,B.SPECIFICATION,C.UNIT_CHN_DESC,0, " 
            +"A.ACCOUNT_QTY,A.ACCOUNT_QTY " 
            +"FROM IND_ACCOUNT A,PHA_BASE B,SYS_UNIT C " 
            +"WHERE (SELECT COUNT(E.VERIFYIN_NO) " 
            +"FROM IND_VERIFYINM D,IND_VERIFYIND E  "
            +"WHERE D.VERIFYIN_DATE BETWEEN TO_DATE('"+START_DATE
            +"','yyyy-mm-dd hh24:mi:ss') " 
            +"AND TO_DATE('"+END_DATE
            +"','yyyy-mm-dd hh24:mi:ss') " 
            +"AND D.DRUG_CATEGORY='1' " 
            +"AND E.VERIFYIN_NO=D.VERIFYIN_NO " 
            +"AND E.ORDER_CODE=A.ORDER_CODE) <= 0 "
            +"AND B.ORDER_CODE=A.ORDER_CODE " 
            +"AND C.UNIT_CODE=A.ACCOUNT_UNIT_CODE " 
            +"UNION ALL " 
            +"SELECT B.ORDER_CODE,D.ORDER_DESC,D.SPECIFICATION,E.UNIT_CHN_DESC,B.VERIFYIN_QTY, " 
            +"C.ACCOUNT_QTY,B.VERIFYIN_QTY - C.ACCOUNT_QTY " 
            +"FROM IND_VERIFYINM A,IND_VERIFYIND B,IND_ACCOUNT C,PHA_BASE D,SYS_UNIT E " 
            +"WHERE A.VERIFYIN_DATE BETWEEN TO_DATE('"+START_DATE
            +"','yyyy-mm-dd hh24:mi:ss') " 
            +"AND TO_DATE('"+END_DATE
            +"','yyyy-mm-dd hh24:mi:ss') " 
            +"AND A.DRUG_CATEGORY='1' " 
            +"AND B.VERIFYIN_NO=A.VERIFYIN_NO " 
            +"AND C.ORDER_CODE=B.ORDER_CODE " 
            +"AND C.ACCOUNT_QTY <> B.VERIFYIN_QTY " 
            +"AND D.ORDER_CODE=C.ORDER_CODE " 
            +"AND E.UNIT_CODE=B.BILL_UNIT "
            +") X "
            +"GROUP BY X.ORDER_CODE,X.ORDER_DESC,X.SPECIFICATION,X.UNIT_CHN_DESC "
            +"HAVING SUM(X.CQTY) <> SUM(X.DQTY)";
		return sql;
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
		String sql = " SELECT COUNT(*) AS COUNT_NUM  FROM IND_VERIFYIND WHERE 1=1 ";
		if (StringUtils.isNotBlank(erpId)) {
			sql += " AND ERP_PACKING_ID='" + erpId + "' ";
		}
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
	 * 查询结算数据-部门
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static String getOrgCodeInIndAccount(String closeDate) {
		String sqlString = " SELECT   ORG_CODE   "
				+ " FROM IND_ACCOUNT  GROUP BY ORG_CODE ";
		return sqlString;
	}
	
	/**
	 * 保存申请主档
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121022
	 */
	public static String createReRequestM(TParm parm) {
		String sql = " INSERT INTO IND_REQUESTM(REQUEST_NO, REQTYPE_CODE, APP_ORG_CODE, TO_ORG_CODE, REQUEST_DATE, REQUEST_USER, REASON_CHN_DESC, "
				+ " UNIT_TYPE, URGENT_FLG, OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE) "
				+ " VALUES(" + " '"
				+ parm.getValue("REQUEST_NO")
				+ "', '"
				+ parm.getValue("REQTYPE_CODE")
				+ "', '"
				+ parm.getValue("APP_ORG_CODE")
				+ "',"
				+ " '"
				+ parm.getValue("TO_ORG_CODE")
				+ "',"
				+ "TO_DATE('"
				+ parm.getValue("REQUEST_DATE")
				+ "','yyyy-mm-dd hh24:mi:ss'),'"
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
				+ parm.getValue("REGION_CODE") + "' " + " ) ";

		return sql;
	}
	
    /**
     * 保存申请主档
     * 
     * @param orgCode
     * @return
     * @author liyh
     * @date 20121022
     */
    public static String createReRequestMAcnt(TParm parm) {//wanglong add 20150202
        String sql = " INSERT INTO SPC_REQUESTM(REQUEST_NO, REQTYPE_CODE, APP_ORG_CODE, TO_ORG_CODE, REQUEST_DATE, REQUEST_USER, REASON_CHN_DESC, "
                + " UNIT_TYPE, URGENT_FLG, OPT_USER, OPT_DATE, OPT_TERM, REGION_CODE) "
                + " VALUES(" + " '"
                + parm.getValue("REQUEST_NO")
                + "', '"
                + parm.getValue("REQTYPE_CODE")
                + "', '"
                + parm.getValue("APP_ORG_CODE")
                + "',"
                + " '"
                + parm.getValue("TO_ORG_CODE")
                + "',"
                + "TO_DATE('"
                + parm.getValue("REQUEST_DATE")
                + "','yyyy-mm-dd hh24:mi:ss'),'"
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
                + parm.getValue("REGION_CODE") + "' " + " ) ";

        return sql;
    }
    
	/**
	 * 查询结算数据-生成请领单
	 * 
	 * @param orgCode
	 * @param orderCode
	 * @return
	 */
	public static String getIndAccount(String closeDate, String orgCode,
			String orderCode) {
		String sqlString = " SELECT   CLOSE_DATE, ORG_CODE, ORDER_CODE,  ACCOUNT_QTY  AS OUT_QTY  "
				+ " FROM IND_ACCOUNT WHERE  UPDATE_FLG='0' ";
		if (null != closeDate && closeDate.length() > 2) {
			sqlString += " AND CLOSE_DATE='" + closeDate + "' ";
		}

		if (null != orgCode && orgCode.length() > 2) {
			sqlString += " AND ORG_CODE='" + orgCode + "' ";
		}
		if (null != orderCode && orderCode.length() > 2) {
			sqlString += "' AND ORDER_CODE='" + orderCode + "' ";
		}
		return sqlString;
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
				+ ",0, '"
				+ parm.getValue("UPDATE_FLG")
				+ "',"
				+ " '"
				+ parm.getValue("OPT_USER")
				+ "', sysdate,'"
				+ parm.getValue("OPT_TERM") + "',0,0 " + " ) ";
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
    public static String saveRequestDAutoOfDrugAcnt(TParm parm) {//wanglong add 20150202
        String sql = " INSERT INTO SPC_REQUESTD(REQUEST_NO, SEQ_NO, ORDER_CODE, QTY, UNIT_CODE, RETAIL_PRICE, STOCK_PRICE, ACTUAL_QTY, UPDATE_FLG, "
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
                + ",0, '"
                + parm.getValue("UPDATE_FLG")
                + "',"
                + " '"
                + parm.getValue("OPT_USER")
                + "', sysdate,'"
                + parm.getValue("OPT_TERM") + "',0,0 " + " ) ";
        return sql;
    }
    
	/**
	 * 门急诊
	 * 
	 * @param requestNo
	 * @param startDate
	 * @param endDate
	 * @param execDeptCode
	 * @param orderCode
	 * @author shendr
	 * @date 2013.7.23
	 * @return
	 */
	public static String updateOpdOrderForRequestByOrderCode(String requestNo,
			String startDate, String endDate, String execDeptCode,
			String orderCode) {
		String sql = "UPDATE OPD_ORDER SET REQUEST_NO='" + requestNo + "' "
				+ " WHERE ORDER_DATE BETWEEN TO_DATE('" + startDate
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate
				+ "','YYYYMMDDHH24MISS') " + " AND EXEC_DEPT_CODE='"
				+ execDeptCode + "' AND REQUEST_NO IS NULL AND ORDER_CODE='"
				+ orderCode + "' ";
		return sql;
	}
	
	/**
	 * 手术介入麻精备药申请
	 * 
	 * @param requestNo
	 * @param startDate
	 * @param endDate
	 * @param execDeptCode
	 * @param orderCode
	 * @author shendr
	 * @date 2013.7.29
	 * @return
	 */
	public static String updateSpcInvRecordForRequestByOrderCode(
			String requestNo, String startDate, String endDate,
			String execDeptCode, String orderCode) {
		String sql = "UPDATE SPC_INV_RECORD SET REQUEST_NO='" + requestNo + "' "
				+ " WHERE BILL_DATE BETWEEN TO_DATE('" + startDate
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('" + endDate
				+ "','YYYYMMDDHH24MISS') " + " AND EXE_DEPT_CODE='"
				+ execDeptCode + "' AND REQUEST_NO IS NULL AND ORDER_CODE='"
				+ orderCode + "' ";
		return sql;
	}
	
	/**
	 * 病区补充计费
	 * 
	 * @param fromOrgCode
	 * @param toOrgCode
	 * @param startDate
	 * @param endDate
	 * @param orderType
	 * @return
	 */
	public static String updateOdiDspnmForRequestByOrderCode(String requestNo,
			String startDate, String endDate, String stationCode,
			String orderCode) {
		String sql = "UPDATE ODI_DSPNM SET REQUEST_NO='"
				+ requestNo
				+ "' "
				+ " WHERE PHA_DOSAGE_DATE BETWEEN TO_DATE('"
				+ startDate
				+ "','YYYYMMDDHH24MISS') AND TO_DATE('"
				+ endDate
				+ "','YYYYMMDDHH24MISS') "
				+ " AND STATION_CODE='"
				+ stationCode
				+ "' AND REQUEST_NO IS NULL AND CAT1_TYPE = 'PHA' " +
						"AND TAKEMED_ORG = '1' " +
						"AND TAKEMED_USER IS NOT NULL " +
						"AND ORDER_CODE='"
				+ orderCode + "' ";
		return sql;
	}

	/**
	 * 获得供应部门
	 * @param deptCode
	 * @author shendr 2013.08.12
	 * @return
	 */
	public static String getDeptDesc(String orgCode){
		String sql= "SELECT ORG_CHN_DESC FROM IND_ORG WHERE ORG_CODE='"+orgCode+"'";
		return sql;
	}
	
	/**
	 * 查询药库科室
	 * @param condition
	 * @return
	 * @author shendr 2013.08.06
	 */
	public static String getIndOrg() {
		return "SELECT ORG_CODE AS ID, ORG_CHN_DESC AS NAME, PY1, PY2 FROM IND_ORG " +
				"WHERE EXINV_FLG = 'Y' " +
				"AND ORG_TYPE='C' " +
				"AND STATION_FLG='N'";
	}
	
	
	public static String getIndOrgByCode(String orgCode){
		return " SELECT ORG_CODE, ORG_CHN_DESC, SEQ, ORG_FLG, ORG_TYPE, "+
			   "  	    SUP_ORG_CODE, EXINV_FLG, REGION_CODE, STATION_FLG, INJ_ORG_FLG, "+
			   "        ATC_FLG, BATCH_FLG, OPT_USER, OPT_DATE, OPT_TERM,  "+
			   "        IS_SUBORG, ATC_ORG_CODE, IS_ACCOUNT, BOX_FLG " +
			   " FROM IND_ORG  "+
			   " WHERE ORG_CODE='"+orgCode+"' ";
	}
	
	/**
	 * 查询供应商供货单位
	 * @return
	 */
	public static String getSupplyUnit(String sup_code,String order_code){
		return "SELECT MAX(SUPPLY_UNIT_CODE) AS SUPPLY_UNIT_CODE,MAX(CONVERSION_RATIO) " +
				"AS CONVERSION_RATIO,MAX(SUP_ORDER_CODE) AS SUP_ORDER_CODE "
				+"FROM IND_CODE_MAP "
				+"WHERE SUP_CODE='"+sup_code+"' " 
				+"AND ORDER_CODE='"+order_code+"'";
	}
	
	/**
	 * 根据供应商编码查询药品编码
	 * @param sup_code
	 * @param order_code
	 * @return
	 */
	public static String getOrderCodeBySup(String sup_code,String order_code) {
		return "SELECT A.CONVERSION_RATIO,A.ORDER_CODE," +
				"A.SUP_CODE,A.SUP_ORDER_CODE,A.SUPPLY_UNIT_CODE " +
				"FROM IND_CODE_MAP A,SYS_FEE B WHERE A.ORDER_CODE=B.ORDER_CODE " +
				"AND B.ACTIVE_FLG='Y' AND A.SUP_ORDER_CODE='"+order_code+"' AND A.SUP_CODE='"+sup_code+"'";
	}
	
	/**
	 * 查询PHA_TRANSUNIT的STOCK_QTY
	 * @return
	 */
	public static String getStockQtyPhaTransunit(String order_code){
		return "SELECT STOCK_QTY,STOCK_UNIT,DOSAGE_UNIT,DOSAGE_QTY " +
				"FROM PHA_TRANSUNIT WHERE ORDER_CODE = '"+order_code+"'";
	}
	
	/**
	 * 更新IND_STOCK过期注记
	 * @param orgCode
	 * @param orderCode
	 * @author shendr 20131106
	 * @return
	 */
	public static String updateIndStock(String orgCode,String orderCode,String active_flg,String batch_seq){
		return "UPDATE IND_STOCK SET ACTIVE_FLG = '"+active_flg+"' "
				+"WHERE ORG_CODE = '"+orgCode+"' "
				+"AND ORDER_CODE = '"+orderCode+"' "
				+"AND BATCH_SEQ = '"+batch_seq+"'";
	}
	
	public static String querySupplierDescription(String sup_code){
		return "SELECT DESCRIPTION FROM SYS_SUPPLIER " +
				"WHERE SUP_CODE='"+sup_code+"'";
	}

	/**
	 * 根据部门查询库存主档信息-移货建议-全部
	 * 
	 * @param orgCode
	 * @return
	 * @author liyh
	 * @date 20121019
	 */
	public static String queryStockMDrugOfSuggestAll(String orgCode,
			String fixedType) {
		String condition = " ";
		if ("1".equals(fixedType)) {//  
			condition = "   HAVING (SUM(B.STOCK_QTY)/E.DOSAGE_QTY) < A.SAFE_QTY ";
		} else if ("2".equals(fixedType)) {
			condition = "   HAVING (SUM(B.STOCK_QTY)/E.DOSAGE_QTY) < A.MIN_QTY ";
		}

		String sql = " SELECT A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY,"
				+ " CEIL(SUM(B.STOCK_QTY)/E.DOSAGE_QTY) AS STOCK_QTY "
				+ " FROM IND_STOCKM A,IND_STOCK B ,PHA_BASE C  ,PHA_TRANSUNIT E,SYS_FEE F "
				+ " WHERE A.ORG_CODE='"
				+ orgCode
				+ "' AND  A.ORG_CODE=B.ORG_CODE "
				+ "  AND A.ORDER_CODE=B.ORDER_CODE "
				+ "  AND A.ORDER_CODE=F.ORDER_CODE "
				+ "  AND F.ACTIVE_FLG='Y' "
				+ "  AND A.ORDER_CODE=C.ORDER_CODE   "
				+ "  AND A.ORDER_CODE=E.ORDER_CODE "
				+ condition
				+ " "
				+ " GROUP BY A.ORG_CODE,A.ORDER_CODE,A.MAX_QTY,A.MIN_QTY,A.SAFE_QTY,A.ECONOMICBUY_QTY,A.BUY_UNRECEIVE_QTY ,E.DOSAGE_QTY "
				+ " ORDER BY A.ORDER_CODE ";
		//System.out.println("sqlall-----:" + sql);
		return sql;
	}

	/**  
	 * 根据药品代码获得PHA信息修改
	 *   
	 * @param order_code
	 * @return
	 */
	public static String getPHAInfoByOrderNew(String order_code, String supCode) {
		return "SELECT A.DOSAGE_UNIT, A.STOCK_UNIT, A.PURCH_UNIT, A.RETAIL_PRICE, A.TRADE_PRICE, "
				+ "A.STOCK_PRICE, B.PURCH_QTY, B.STOCK_QTY, B.DOSAGE_QTY, A.PHA_TYPE, B.MEDI_UNIT, B.MEDI_QTY,C.CONTRACT_PRICE "
				+ "FROM PHA_BASE A, PHA_TRANSUNIT B,IND_AGENT C "
				+ "WHERE A.ORDER_CODE = B.ORDER_CODE AND A.ORDER_CODE=C.ORDER_CODE "
				+ "AND A.ORDER_CODE = '"
				+ order_code
				+ "'"
				+ " AND C.SUP_CODE = '" + supCode + "'";
	}

	/**
	 * 根据药品code 获取库存量
	 * add by wukai 20161222
	 * @param order_code : 单号
	 * @return
	 */
	public static String getStockQtyInfo(String order_code) {
		return "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
				+ "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
				+ "B.SPECIFICATION, B.STOCK_PRICE, B.RETAIL_PRICE, "
				+ "B.PHA_TYPE, B.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
				+ "B.TRADE_PRICE "
				+ "FROM PHA_BASE B, PHA_TRANSUNIT C "
				+ "WHERE B.ORDER_CODE = C.ORDER_CODE "
				+ "AND B.ORDER_CODE =  '" +  order_code + "'";
					
	}
}
