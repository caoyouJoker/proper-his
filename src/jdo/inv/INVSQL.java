package jdo.inv;

import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>                              
 * Title: 物资SQL封装
 * </p>
 *
 * <p> 
 * Description: 物资SQL封装 
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
 * @author zhangy 2009.10.29
 * @version 1.0
 */

public class INVSQL {
    public INVSQL() {
    }
    
    /* **************************新增代码*************************************** */
    /**
     * 自动拨补部门
     * 
     * @return String
     */ 
    public static String getAssignorg(String orgCode) {
        return
            "SELECT ORG_CODE , CYCLE_TYPE , ASSIGNED_DAY , OPT_USER , OPT_DATE ,"
            + " OPT_TERM " +        
              " FROM INV_ASSIGNORG " +
              " WHERE ORG_CODE = '"+ orgCode +"'" +
              " ORDER BY ORG_CODE"; 
    }
    
    
    /**
     * 全部自动拨补部门
     * 
     * @return String 
     */ 
    public static String getAllAssignorg() {
        return
            "SELECT ORG_CODE , CYCLE_TYPE , ASSIGNED_DAY , OPT_USER , OPT_DATE ,"
            + " OPT_TERM " +
              " FROM INV_ASSIGNORG " +
              " ORDER BY ORG_CODE";
    }
    /* **************************新增代码*************************************** */
    /**
     * 全部物资部门
     *  
     * @return String
     */
    public static String getinvOrg() {
        return  
            "SELECT * " 
            + "  FROM INV_ORG ORDER BY ORG_CODE";
    }
    
    /**
     * 物资库库参数设定  fux modify 20130522
     *
     * @return String
     */                    
    public static String getINVSysParm() {
        return
//        " SELECT ADD_STOCK_FLG,FIXEDAMOUNT_FLG,REUPRICE_FLG,HISTORY_DAYS, " +
//        " DISCHECK_FLG,MM_DAY,OPT_USER,OPT_DATE,OPT_TERM,HIGH_VALUE_INV " +
//        " FROM INV_SYSPARM "; 
        " SELECT FIXEDAMOUNT_FLG,REUPRICE_FLG," + 
        " DISCHECK_FLG,AUTO_FILL_TYPE,OPT_USER,OPT_DATE,OPT_TERM " +
        " FROM INV_SYSPARM ";   
    }  
    
    /**
     * 查询部门信息-自动拨补
     * @param orgCode
     * @return
     * @author fux
     * @date 20130527 
     */
    public static String queryOrgCodeAuto(){
    	//测试：inv_code = '08.02.0271274' 
    	String sql = " SELECT  A.ORG_CODE,NVL(A.DISPENSE_ORG_CODE,'-1') AS DISPENSE_ORG_CODE ,B.ORG_TYPE "
    		       + " FROM INV_STOCKM A,INV_ORG B  "
    		       + " WHERE A.DISPENSE_FLG='Y'  AND A.ORG_CODE=B.ORG_CODE  "
    		       + " GROUP BY A.ORG_CODE,A.DISPENSE_ORG_CODE  ,B.ORG_TYPE ";
    	return sql;
    }
    
    /**
     * 查询部门信息-自动拨补 
     * @param orgCode
     * @return
     * @author wangming
     * @date 20131030
     */
    public static String queryAutoObtainOrgCode(){
    	String sql = " SELECT DISTINCT A.ORG_CODE  "
    		       + " FROM INV_STOCKM A,INV_ORG B  "
    		       + " WHERE A.DISPENSE_FLG='Y'  AND A.ORG_CODE=B.ORG_CODE  "
    		       + " GROUP BY A.ORG_CODE ";
    	return sql;
    }
    
    /* **************************已有代码*************************************** */

    /**
     * 查询供货厂商物资信息
     *
     * @param sup_code
     *            供货厂商
     * @return String
     */
    public static String getSupInv(String sup_code, String inv_code) {
        if ("".equals(sup_code)) {
            return "";
        }
        String sql = "SELECT A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION,E.MAN_CHN_DESC,D.SUP_CHN_DESC, "
            + " A.BILL_UNIT, A.CONTRACT_PRICE, A.STOCK_UNIT, "
            + " A.GIFT_QTY, A.GIFT_RATE, A.DISCOUNT_RATE, B.MAN_CODE, "
            + " C.PURCH_QTY, C.STOCK_QTY,B.VALIDATE_FLG,B.SEQMAN_FLG "
            + " FROM INV_AGENT A, INV_BASE B, INV_TRANSUNIT C ,SYS_SUPPLIER D,SYS_MANUFACTURER E"
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.INV_CODE = C.INV_CODE "  
            + " AND B.MAN_CODE = E.MAN_CODE " 
            + " AND B.UP_SUP_CODE = D.SUP_CODE"
            //fux modify 20140815                                    
            + " AND B.ACTIVE_FLG = 'Y' "
            + " AND A.SUP_CODE='" 
            + sup_code + "' ";
        sql+=" AND EXISTS (SELECT 1 FROM INV_STOCKM  M WHERE M.INV_CODE=A.INV_CODE AND M.ORG_CODE='"+Operator.getDept()+"' )";
        if (!"".equals(inv_code)) {
            sql += "AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR B.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + "OR B.PY1 LIKE '" + inv_code + "%' )";
        }
        sql += " ORDER BY B.DESCRIPTION";
        return sql;
    }
    
    

    /**
     * 查询供货厂商物资信息
     *
     * @param sup_code
     *            供货厂商
     * @return String
     */
    public static String getSupBaseInv(String sup_code, String inv_code) {
        String sql = "SELECT A.INV_CODE, B.INV_CHN_DESC, B.DESCRIPTION, "
            + " A.BILL_UNIT, A.CONTRACT_PRICE, A.STOCK_UNIT, "
            + " A.GIFT_QTY, A.GIFT_RATE, A.DISCOUNT_RATE, B.MAN_CODE, "
            + " C.PURCH_QTY, C.STOCK_QTY "
            + " FROM INV_AGENT A, INV_BASE B, INV_TRANSUNIT C "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.INV_CODE = C.INV_CODE ";
        if (!"".equals(inv_code)) {
            sql += "AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR B.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + "OR B.PY1 LIKE '" + inv_code + "%' )";
        }
        return sql;   
    }

    /**
     * 查询部门物资信息
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getOrgInv(String org_code, String inv_code) {
        String inv_info = "";
        if (!"".equals(inv_code)) {
            inv_info = " AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + " OR A.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + " OR A.PY1 LIKE '" + inv_code + "%' )";
        }

        String sql = "SELECT A.INV_CODE, A.INV_CHN_DESC, "
            + " A.DESCRIPTION, A.PY1, B.STOCK_QTY, A.STOCK_UNIT, "
            + " B.INVSEQ_NO, A.SEQMAN_FLG, A.DISPENSE_UNIT, "
            + " C.CONTRACT_PRICE COST_PRICE, A.MAN_CODE, B.BATCH_NO, B.VALID_DATE, "
            + " B.BATCH_SEQ, A.VALIDATE_FLG, A.INVKIND_CODE "
            + " FROM INV_BASE A, INV_STOCKDD B ,INV_AGENT C "
            + " WHERE A.INV_CODE = C.INV_CODE AND  A.INV_CODE = B.INV_CODE AND"
            + " A.SEQMAN_FLG = 'Y' AND A.ACTIVE_FLG = 'Y' AND B.ORG_CODE = '"
            + org_code + "' "
            + inv_info
            + " UNION "
            + " SELECT A.INV_CODE, A.INV_CHN_DESC, A.DESCRIPTION, "
            + " A.PY1,B.STOCK_QTY,A.STOCK_UNIT,0 AS INVSEQ_NO, A.SEQMAN_FLG, "
            + " A.DISPENSE_UNIT, C.CONTRACT_PRICE  COST_PRICE , A.MAN_CODE, B.BATCH_NO, "
            + " B.VALID_DATE, B.BATCH_SEQ, A.VALIDATE_FLG, A.INVKIND_CODE "
            + " FROM INV_BASE A, INV_STOCKD B,INV_AGENT C  "
            + " WHERE A.INV_CODE = C.INV_CODE AND   A.INV_CODE = B.INV_CODE AND"
            + " A.SEQMAN_FLG = 'N' AND A.ACTIVE_FLG = 'Y' AND B.ORG_CODE = '"
            + org_code + "' "
            + inv_info;
        return sql;
    }

    /**
     * 未完成的订购单号
     * @param org_code String
     * @return String
     */
    public static String getInvPurOrderNo(String org_code) {
        return "SELECT IM.PURORDER_NO, IM.PURORDER_DATE, CASE WHEN IM.CON_ORG IS NULL THEN '011201' ELSE IM.CON_ORG END AS CON_ORG,  CASE WHEN O.ORG_DESC IS NULL THEN '医疗物资库' ELSE O.ORG_DESC END AS ORG_DESC " 
        	+ " FROM INV_PURORDERM IM LEFT JOIN INV_ORG O ON IM.CON_ORG = O.ORG_CODE " 
        	+ " WHERE IM.ORG_CODE = '" + org_code +
            "' AND IM.FINAL_FLG = 'N' AND IM.CHECK_FLG = 'Y' ORDER BY IM.PURORDER_NO DESC";
    }

    /**
     * 未完成的订购单号
     * @param org_code String
     * @return String
     */
    public static String getInvPurOrderNo(String org_code, String sup_code) {
        return "SELECT IM.PURORDER_NO, IM.PURORDER_DATE, CASE WHEN IM.CON_ORG IS NULL THEN '011201' ELSE IM.CON_ORG END AS CON_ORG, CASE WHEN O.ORG_DESC IS NULL THEN '医疗物资库' ELSE O.ORG_DESC END AS ORG_DESC " 
        	+ " FROM INV_PURORDERM IM LEFT JOIN INV_ORG O ON IM.CON_ORG = O.ORG_CODE " 
        	+ " WHERE IM.ORG_CODE = '" + org_code +
            "' AND IM.SUP_CODE = '" + sup_code +
            "' AND IM.FINAL_FLG = 'N' ORDER BY IM.PURORDER_NO DESC";
    }

    /**
     * INV_BASE数据
     * @return String
     */
    public static String getInvBasePopup(String inv_code) {
    	//==modify by wukai 20170224 数据去重
        String sql =
            "SELECT  A.INV_CODE, A.INVTYPE_CODE, A.INVKIND_CODE, A.INV_CHN_DESC, "
            + "A.INV_ENG_DESC, A.INV_ABS_DESC, A.DESCRIPTION, A.MAN_CODE, "
            +
            "A.BUYWAY_CODE, A.USE_DEADLINE, C.CONTRACT_PRICE COST_PRICE, A.ORDER_CODE, A.STOCK_UNIT, "
            +
            "A.DISPENSE_UNIT,A.HYGIENE_TRADE_CODE,A.STOPBUY_FLG,A.NORMALDRUG_FLG, "
            +
            "A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG, A.EXPENSIVE_FLG, B.PURCH_UNIT "
            + "FROM INV_BASE A, INV_TRANSUNIT B ,INV_AGENT C "
            + "WHERE A.INV_CODE = C.INV_CODE AND  A.INV_CODE = B.INV_CODE AND ACTIVE_FLG = 'Y' ";
        if (!"".equals(inv_code)) {
            sql += " AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR A.INV_CHN_DESC LIKE '%" + inv_code + "%' "  
                + "OR A.PY1 LIKE '%" + inv_code + "%' )";
        }
        return sql;
    }
    
    
    /**
     * INV_BASE数据
     * @return String
     */
    public static String getInvBaseAgentPopup(String inv_code) {
        String sql =
            "SELECT A.INV_CODE, A.INVTYPE_CODE, A.INVKIND_CODE, A.INV_CHN_DESC, "
            + "A.INV_ENG_DESC, A.INV_ABS_DESC, A.DESCRIPTION, A.MAN_CODE, "
            +
            "A.BUYWAY_CODE, A.USE_DEADLINE, A.ORDER_CODE, A.STOCK_UNIT, " 
            + 
            "A.DISPENSE_UNIT,A.HYGIENE_TRADE_CODE,A.STOPBUY_FLG,A.NORMALDRUG_FLG, "
            +      
            "A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG, A.EXPENSIVE_FLG, B.PURCH_UNIT "
            + "FROM INV_BASE A, INV_TRANSUNIT B "
            + "WHERE  A.INV_CODE = B.INV_CODE AND ACTIVE_FLG = 'Y' ";
        if (!"".equals(inv_code)) {
            sql += " AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR A.INV_CHN_DESC LIKE '%" + inv_code + "%' "
                + "OR A.PY1 LIKE '%" + inv_code + "%' )";
        }
        return sql;
    }
    
    
    
    
    /**
     * INV_BASE数据（不含序管）
     * @return String
     */
    public static String getInvBaseNotSeqPopup(String inv_code) {
        String sql =
            "SELECT A.INV_CODE, A.INVTYPE_CODE, A.INVKIND_CODE, A.INV_CHN_DESC, "
            + "A.INV_ENG_DESC, A.INV_ABS_DESC, A.DESCRIPTION, A.MAN_CODE, "
            +
            "A.BUYWAY_CODE, A.USE_DEADLINE, C.CONTRACT_PRICE COST_PRICE, A.ORDER_CODE, A.STOCK_UNIT, "
            +
            "A.DISPENSE_UNIT,A.HYGIENE_TRADE_CODE,A.STOPBUY_FLG,A.NORMALDRUG_FLG, "
            +
            "A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG, A.EXPENSIVE_FLG, B.PURCH_UNIT "
            + "FROM INV_BASE A, INV_TRANSUNIT B,INV_AGENT C  "
            + "WHERE A.INV_CODE = C.INV_CODE AND A.INV_CODE = B.INV_CODE AND ACTIVE_FLG = 'Y' AND A.SEQMAN_FLG = 'N' ";
        if (!"".equals(inv_code)) {
            sql += " AND (A.INV_CODE LIKE '" + inv_code + "%' "
                + "OR A.INV_CHN_DESC LIKE '" + inv_code + "%' "
                + "OR A.PY1 LIKE '" + inv_code + "%' )";
        }
        return sql;
    }

    /**
     * INV_PACKM数据
     * @return String
     */
    public static String getInvPackPopup(String pack_code, String seq_flg) {
        String sql = "SELECT * FROM INV_PACKM ";   
        if (!"".equals(pack_code)) {
            sql += " WHERE (PACK_CODE LIKE '" + pack_code + "%' "
                + "OR PACK_DESC LIKE '" + pack_code + "%' "
                + "OR PY1 LIKE '" + pack_code + "%' )";
        }
        if (!"".equals(pack_code) && !"".equals(seq_flg)) {
            sql += " AND SEQ_FLG = '" + seq_flg + "'";
        }
        else if (!"".equals(seq_flg)) {
            sql += " WHERE SEQ_FLG = '" + seq_flg + "'";
        }
        System.out.println("INV_PACKM_sql"+sql);
        return sql;
    }

    /**
     *
     * @param pack_code String
     * @return String
     */
    public static String getInvPackStockMPopup(String org_code,
                                               String pack_code) {
        String sql = "SELECT A.PACK_CODE, B.PACK_DESC, A.PACK_SEQ_NO, "
            + " B.SEQ_FLG, B.DESCRIPTION "
            + " FROM INV_PACKSTOCKM A, INV_PACKM B "
            + " WHERE A.PACK_CODE = B.PACK_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' ";
        if (!"".equals(pack_code)) {
            sql += " AND (B.PACK_CODE LIKE '" + pack_code + "%' "
                + "OR B.PACK_DESC LIKE '" + pack_code + "%' "
                + "OR B.PY1 LIKE '" + pack_code + "%' )";
        }
        sql += " ORDER BY A.PACK_CODE,A.PACK_SEQ_NO ";
        return sql;
    }

    /**
     * INV_BASE 数据
     * @param inv_code String
     * @return String
     */
    public static String getInvBase(String inv_code) {
        return "SELECT INV_CODE, INVTYPE_CODE, INVKIND_CODE, INV_CHN_DESC, "
            + "INV_ENG_DESC, INV_ABS_DESC, DESCRIPTION, MAN_CODE, "
            + "BUYWAY_CODE, USE_DEADLINE, COST_PRICE, ORDER_CODE, STOCK_UNIT, "
            + "DISPENSE_UNIT,HYGIENE_TRADE_CODE,STOPBUY_FLG,NORMALDRUG_FLG, "
            + "REQUEST_FLG, SEQMAN_FLG, VALIDATE_FLG, EXPENSIVE_FLG "
            + "FROM INV_BASE WHERE ACTIVE_FLG = 'Y' AND INV_CODE = '" +
            inv_code + "'";
    }

    /**
     * INV_TRANSUNIT 数据
     * @param inv_code String
     * @return String
     */
    public static String getInvTransUnit(String inv_code) {
        return "SELECT * FROM INV_TRANSUNIT WHERE INV_CODE = '" + inv_code +
            "'";
    }

    /**
     * 取得库存总量
     * @param inv_code String
     * @return String
     */
    public static String getInvStockSumQty(String inv_code) {
        return " SELECT SUM(STOCK_QTY) AS SUM_QTY FROM INV_STOCKM "
            + " WHERE INV_CODE = '" + inv_code + "'";
    }

    /**
     * 根据用户ID查询所属科室
     * @param user_id String
     * @param org_type String
     * @param station_flg String
     * @param mat_flg String
     * @param inv_flg String
     * @return String
     */
    public static String getOperatorInvOrg(String user_id, String org_type,
                                           String station_flg, String mat_flg,
                                           String inv_flg) {
        String sql =
            "SELECT A.ORG_CODE AS ID, A.ORG_DESC AS NAME, A.PY1, A.PY2"
            + " FROM INV_ORG A,SYS_OPERATOR_DEPT B "
            + " WHERE A.ORG_CODE=B.DEPT_CODE AND B.USER_ID = '" + user_id + "'";

        if (!"".equals(org_type)) {
            sql += " AND A.ORG_TYPE = '" + org_type + "'";
        }
        if (!"".equals(station_flg)) {
            sql += " AND A.STATION_FLG = '" + station_flg + "'";
        }
        if (!"".equals(mat_flg)) {
            sql += " AND A.MAT_FLG = '" + mat_flg + "'";
        }
        if (!"".equals(inv_flg)) {
            sql += " AND A.INV_FLG = '" + inv_flg + "'";
        }
        return sql;
    }

    /**
     * 根据批号和效期取得物资的批次序号
     * @param batch_no String
     * @param valid_date String
     * @return String
     */
    public static String getInvBatchSeq(String org_code, String inv_code,
                                        String batch_no, String valid_date) {
        if (!"".equals(valid_date)) {
            return "SELECT BATCH_SEQ FROM INV_STOCKD "  
                + "WHERE ORG_CODE = '" + org_code
                + "' AND INV_CODE = '" + inv_code
                + "' AND BATCH_NO = '" + batch_no
                + "' AND VALID_DATE = TO_DATE('" + valid_date
                + "','YYYY/MM/DD')";  
        }      
        else {   
            return "SELECT BATCH_SEQ FROM INV_STOCKD "   
                + "WHERE ORG_CODE = '" + org_code
                + "' AND INV_CODE = '" + inv_code
                + "' AND BATCH_NO = '" + batch_no + "'";
        }
    }

    /** 
     * 根据部门和物资代码取得最大批次序号
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvStockMaxBatchSeq(String org_code,   
                                                String inv_code) {   
        return "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM INV_STOCKD "    
            + "WHERE ORG_CODE = '" + org_code + "' AND INV_CODE = '" + inv_code +
            "'";
    }                                  

    /**
     * 取得验收入库明细表中的最大物资序号
     * @param inv_code String
     * @return String
     */
    public static String getInvMaxInvSeqNo(String inv_code) {
        return "SELECT MAX(INVSEQ_NO) AS INVSEQ_NO FROM INV_VERIFYINDD "
            + "WHERE INV_CODE = '" + inv_code + "'";
    }  
    
    /**
     * 取得验收入库明细表中的最大物资序号
     * @param inv_code String   
     * @return String
     */
    public static String getInvMaxInvSeqNoStockdd(String inv_code) {
        return "SELECT MAX(INVSEQ_NO) AS INVSEQ_NO FROM INV_STOCKDD "
            + "WHERE INV_CODE = '" + inv_code + "'";    
    }  
    

    /**
     * 取得库存主档信息
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvStockM(String org_code, String inv_code) {
        return "SELECT ORG_CODE, INV_CODE, REGION_CODE, DISPENSE_FLG, "
            + " DISPENSE_ORG_CODE, STOCK_FLG, MATERIAL_LOC_CODE, SAFE_QTY, "
            + " MIN_QTY, MAX_QTY, ECONOMICBUY_QTY, STOCK_QTY, MM_USE_QTY, "
            + " AVERAGE_DAYUSE_QTY, STOCK_UNIT, OPT_USER, OPT_DATE, OPT_TERM "
            + " FROM INV_STOCKM WHERE ORG_CODE = '" + org_code +
            "' AND INV_CODE = '" + inv_code + "'";
    }

    /**
     * 取得库存主档信息
     * @param org_code String
     * @param inv_code String
     * @param materail_loc_code String
     * @return String
     */
    public static String getInvStockM(String org_code, String inv_code,
                                      String materail_loc_code) {
        String where = "";
        if (!"".equals(inv_code)) {
            where += " AND A.INV_CODE = '" + inv_code + "' ";
        }
        if (!"".equals(materail_loc_code)) {
            where += " AND A.MATERIAL_LOC_CODE = '" + materail_loc_code + "' ";
        }

        return "SELECT A.INV_CODE, B.INV_CHN_DESC,B.DESCRIPTION, A.MATERIAL_LOC_CODE, "
            + " A.DISPENSE_FLG,A.DISPENSE_ORG_CODE, A.STOCK_UNIT, A.SAFE_QTY,  "
            + " A.MAX_QTY, A.MIN_QTY,A.ECONOMICBUY_QTY, A.STOCK_QTY,  "
            + " A.AVERAGE_DAYUSE_QTY, A.MM_USE_QTY,A.REGION_CODE,A.BASE_QTY "
            + " FROM INV_STOCKM A, INV_BASE B "
            + " WHERE A.INV_CODE = B.INV_CODE AND A.ORG_CODE = '" + org_code + "' " +
            where;
    }


    /**
     * 取得库存明细信息
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvStockD(String org_code, String inv_code) {
        return "SELECT ORG_CODE,INV_CODE,BATCH_SEQ,REGION_CODE,BATCH_NO, " +
            " VALID_DATE, STOCK_QTY, LASTDAY_TOLSTOCK_QTY, DAYIN_QTY, " +
            " DAYOUT_QTY, DAY_CHECKMODI_QTY, DAY_VERIFYIN_QTY, " +
            " DAY_VERIFYIN_AMT, GIFTIN_QTY, DAY_REGRESSGOODS_QTY, " +
            " DAY_REGRESSGOODS_AMT, DAY_REQUESTIN_QTY, DAY_REQUESTOUT_QTY, " +
            " DAY_CHANGEIN_QTY, DAY_CHANGEOUT_QTY, DAY_TRANSMITIN_QTY, " +
            " DAY_TRANSMITOUT_QTY, DAY_WASTE_QTY, DAY_DISPENSE_QTY, " +
            " DAY_REGRESS_QTY, FREEZE_TOT, UNIT_PRICE, STOCK_UNIT " +
            " FROM INV_STOCKD WHERE ORG_CODE = '" + org_code +
            "' AND INV_CODE = '" + inv_code + "' ORDER BY BATCH_SEQ ";
    }
    /**
     * 取得库存明细信息
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvStockDNew(String org_code, String inv_code) {
        return "SELECT A.INV_CODE,A.BATCH_NO,A.VALID_DATE,A.STOCK_UNIT,A.STOCK_QTY,A.UNIT_PRICE,b.INV_CHN_DESC,b.DESCRIPTION,a.BATCH_SEQ  " +
            " FROM INV_STOCKD A" +
            " left join inv_base b on b.inv_code=a.inv_code " +
            " WHERE A.ORG_CODE = '" + org_code +
            "' AND A.INV_CODE = '" + inv_code + "' ORDER BY A.BATCH_SEQ ";
    }

    /**
     * 取得库存明细信息
     * @param org_code String
     * @param inv_code String
     * @param batch_seq int
     * @return String
     */
    public static String getInvStockD(String org_code, String inv_code,
                                      int batch_seq) {
        return "SELECT ORG_CODE,INV_CODE,BATCH_SEQ,REGION_CODE,BATCH_NO, " +
            " VALID_DATE, STOCK_QTY, LASTDAY_TOLSTOCK_QTY, DAYIN_QTY, " +
            " DAYOUT_QTY, DAY_CHECKMODI_QTY, DAY_VERIFYIN_QTY, " +
            " DAY_VERIFYIN_AMT, GIFTIN_QTY, DAY_REGRESSGOODS_QTY, " +
            " DAY_REGRESSGOODS_AMT, DAY_REQUESTIN_QTY, DAY_REQUESTOUT_QTY, " +
            " DAY_CHANGEIN_QTY, DAY_CHANGEOUT_QTY, DAY_TRANSMITIN_QTY, " +
            " DAY_TRANSMITOUT_QTY, DAY_WASTE_QTY, DAY_DISPENSE_QTY, " +
            " DAY_REGRESS_QTY, FREEZE_TOT, UNIT_PRICE STOCK_UNIT " +
            " FROM INV_STOCKD WHERE ORG_CODE = '" + org_code +      
            "' AND INV_CODE = '" + inv_code + "' AND BATCH_SEQ = " + batch_seq;
    } 

    /**
     * 判断单据是否全部完成
     * @return String
     */
    public static String getInvPurorderMInQty(String purorder_no) {
        return "SELECT COUNT(*) AS SUM_COUNT FROM INV_PURORDERD " +
            " WHERE PURORDER_NO = '" + purorder_no +
            "' AND PROCESS_TYPE IN ('0','1')";
    }

    /**
     * 根据部门名称取得部门信息
     * @param org_code String
     * @return String
     */
    public static String getInvOrg(String org_code) {
        return "SELECT * FROM INV_ORG WHERE ORG_CODE = '" + org_code + "'";
    }

    /**
     * 物资参数档
     * @return String
     */
    public static String getInvSysParm() {
        return "SELECT * FROM INV_SYSPARM ";
    }

    /**
     * 物资部门部门信息
     * @return String
     */
    public static String getINVOrg(String org_code) {
        return "SELECT ORG_CODE, ORG_TYPE, STATION_FLG, STOCK_ORG_CODE, "
            + " REMARK, REGION_CODE, MAT_FLG, INV_FLG FROM INV_ORG "
            + " WHERE ORG_CODE = '" + org_code + "'";
    }

    /**
     *
     * @return String
     */
    public static String getInvBaseRule() {
        return "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5, "
            + "SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE "
            + "FROM SYS_RULE WHERE RULE_TYPE = 'INV_BASE'";
    }

    /**
     *
     * @return String
     */
    public static String getInvBaseCategory() {
        return "SELECT CATEGORY_CODE,CATEGORY_CHN_DESC,DETAIL_FLG "
            + "FROM SYS_CATEGORY WHERE RULE_TYPE = 'INV_BASE' ORDER BY SEQ";
    }

    /**
     *
     * @return String
     */
    public static String getInvBaseCategoryLength() {
        return "SELECT DISTINCT LENGTH(CATEGORY_CODE) CATEGORY_LENGTH "
            + "FROM SYS_CATEGORY WHERE  RULE_TYPE = 'INV_BASE' "
            + "ORDER BY CATEGORY_LENGTH ";
    }

    /**
     *
     * @return String
     */
    public static String getInvRule() {
        return "SELECT CLASSIFY1,CLASSIFY2,CLASSIFY3,CLASSIFY4,CLASSIFY5, "
            + "SERIAL_NUMBER,TOT_NUMBER,RULE_TYPE "
            + "FROM SYS_RULE WHERE RULE_TYPE = 'INV_BASE' ";
    }

    /**
     *
     * @param type_code String
     * @return String
     */
    public static String getInvMaxSerialNumber(String invtype_code) {
        return "SELECT MAX(INV_CODE) AS INV_CODE FROM INV_BASE "
            + "WHERE INVTYPE_CODE  = '" + invtype_code + "'";
    }

    /**
     *
     * @param invtype_code String
     * @return String
     */
    public static String getInvBaseByTypeCode(String invtype_code) {
        return "SELECT * FROM INV_BASE WHERE INVTYPE_CODE = '" + invtype_code +
            "'";
    }

    /**
     *
     * @param invtype_code String
     * @return String
     */
    public static String getInvBaseByCodeOrDesc(String inv_code,
                                                String inv_desc) {
        String sql = "";
        if (!"".equals(inv_code) && !"".equals(inv_desc)) {
            sql = " INV_CODE LIKE '" + inv_code + "%' AND INV_CHN_DESC LIKE '" +
                inv_desc + "%'";
        }
        else if (!"".equals(inv_code)) {
            sql = " INV_CODE LIKE '" + inv_code + "%' ";
        }
        else if ("".equals(inv_desc)) {
            sql = " INV_CHN_DESC LIKE '" + inv_desc + "%' ";
        }
        return "SELECT * FROM INV_BASE WHERE " + sql;
    }


    /**
     * 取得字典全部信息
     * @param inv_code String
     * @return String
     */ 
    public static String getInvBaseInfo(String inv_code) {  
        return "SELECT A.INV_CODE, A.ACTIVE_FLG, A.INVTYPE_CODE,A.PUR_QTY,A.PUR_UNIT,A.FI_CODE,A.USE_CODE,  "
            + " A.INVKIND_CODE, A.INV_CHN_DESC, A.PY1, A.INV_ENG_DESC, "
            + " A.INV_ABS_DESC, A.DESCRIPTION, A.MAN_CODE, A.MAN_NATION, "
            + " A.BUYWAY_CODE, A.USE_DEADLINE, D.CONTRACT_PRICE COST_PRICE, A.ORDER_CODE, " 
            + " A.HYGIENE_TRADE_CODE, A.STOPBUY_FLG, A.NORMALDRUG_FLG, "
            + " A.REQUEST_FLG, A.SEQMAN_FLG, A.VALIDATE_FLG, A.EXPENSIVE_FLG, "
            + " A.PY2, B.PURCH_QTY, B.PURCH_UNIT, B.STOCK_QTY, B.STOCK_UNIT,"
            + " B.DISPENSE_QTY, B.DISPENSE_UNIT, C.ORDER_DESC,A.CONSIGN_FLG,A.CONSIGN_MAN_CODE, " +
              " A.SUP_CODE,A.UP_SUP_CODE,A.INV_KIND KIND,A.INV_ACCOUNT ACCOUNT "    
            + " FROM INV_BASE A, INV_TRANSUNIT B, SYS_FEE C,INV_AGENT D "
            //fux modify 20140917 以inv_base信息为准
            + " WHERE A.INV_CODE = D.INV_CODE(+)  " +
              " AND A.INV_CODE = B.INV_CODE(+)  " +  
              " AND "  
            + " A.ORDER_CODE = C.ORDER_CODE(+) "   
            + "AND A.INV_CODE = '" + inv_code + "'";
    }

    
    
    
    /**
     *
     * @param sup_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvAgentBySupOrInv(String sup_code, String inv_code) {
        String sql = "";
        if (!"".equals(sup_code) && !"".equals(inv_code)) {
            sql = "AND A.INV_CODE = '" + inv_code + "' AND A.SUP_CODE = '" +
                sup_code + "'"; 
        }
        else if (!"".equals(inv_code)) {
            sql = "AND A.INV_CODE = '" + inv_code + "' ";
        }   
        else if ("".equals(sup_code)) { 
            sql = "AND A.SUP_CODE = '" + sup_code + "' ";
        }
        return "SELECT A.SUP_CODE, A.INV_CODE, B.INV_CHN_DESC,"
            + " A.CONTRACT_PRICE, A.BILL_UNIT, A.STOCK_UNIT,A.CONTRACT_PRICE AS STOCK_PRICE,  "
            + " A.GIFT_QTY, A.GIFT_RATE, A.DISCOUNT_RATE, A.LAST_ORDER_DATE,"
            + " A.LAST_ORDER_QTY, A.LAST_ORDER_PRICE, A.LAST_ORDER_NO,"
            + " A.LAST_VERIFY_DATE, A.LAST_VERIFY_PRICE, A.CONTRACT_NO "
            + " FROM INV_AGENT A, INV_BASE B WHERE A.INV_CODE = B.INV_CODE " +
            sql;
    }

    /**
     * 查询执行自动拨补的科室
     * @return String
     */ 
  //fux modify 20130522
    public static String getINVORG(String orgCode,String regionCode) {
        return " SELECT * FROM INV_ORG where org_Code=' "+orgCode+"' and region_Code='"+regionCode+"'";
    }
    
    
    //fux modify 20130522
    public static String getorg() {
        return " SELECT * FROM INV_ORG ";
    }

    /**
     * 根据库房类别在科室表中查询药库
     *
     * @return String
     */
    public static String getOrgCodeByOrgType() {
        return "SELECT COST_CENTER_CODE AS ID , COST_CENTER_CHN_DESC AS NAME , "
            + "PY1, PY2 FROM SYS_COST_CENTER WHERE ACTIVE_FLG = 'Y'";
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
    public static String getOrgCodeByTypeAndStation() {
        return
            "SELECT STATION_CODE AS ID , STATION_DESC AS NAME, PY1, PY2 FROM SYS_STATION";
    }

    /**
     * 物资部门料位
     * @return String
     */
    public static String getMaterialloc() {
        return "SELECT * FROM INV_MATERIALLOC";
    }

    /**
     * 物资部门料位
     * @param org_code String
     * @param material_loc_code String
     * @return String
     */
    public static String getMaterialloc(String org_code,
                                        String material_loc_code) {
        return "SELECT * FROM INV_MATERIALLOC WHERE ORG_CODE = '" + org_code +
            "' AND MATERIAL_LOC_CODE = '" + material_loc_code + "'";
    }

    /**
     * 取得手术包字典的编码规则
     * @return String
     */
    public static String getInvPackTree() {
        return "SELECT * FROM SYS_CATEGORY WHERE RULE_TYPE='INV_PACK'";
    }

    /**
     * 取得手术包字典细项
     * @param pack_code String
     * @return String
     */
    public static String getInvPackD(String pack_code) {
        return "SELECT * FROM INV_PACKD WHERE PACK_CODE= '" + pack_code + "'";
    }

    /**
     * 取得手术包字典主项
     * @param pack_code String
     * @return String
     */
    public static String getInvPackM(String pack_code) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE = '" + pack_code + "'";
    }

    /**
     * 取得手术包字典主项（模糊查询用）
     * @param pack_code String
     * @return String
     */
    public static String getInvPackMQuery(String pack_code) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE like '" +
            pack_code + "%'";
    }

    /**
     * 根据过滤条件过滤INV_PACKM的数据
     * @param filter String
     * @return String
     */
    public static String getInvPackMByFilter(String filter) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE IS NOT NULL" + filter;
    }

    /**
     * 根据结点和过滤条件过滤INV_PACKM的数据
     * @param filter String
     * @return String
     */
    public static String getInvPackMByFilter(String parentId, String filter) {
        return "SELECT * FROM INV_PACKM WHERE PACK_CODE LIKE '" + parentId +
            "%' " + filter;
    }

    /**
     * 取得手术包的最大序号
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getPackMaxSeq(String org_code, String pack_code) {
        return "SELECT MAX(PACK_SEQ_NO) AS PACK_SEQ_NO FROM INV_PACKSTOCKM "
            + "WHERE ORG_CODE = '" + org_code + "' AND PACK_CODE = '" +
            pack_code + "'";
    }
      
        

    
    /**
     * 取得手术包明细的价格
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getINVPackDByCostPrice(String pack_code) {
        return "SELECT C.CONTRACT_PRICE COST_PRICE, A.QTY, A.PACK_TYPE FROM INV_PACKD A, INV_BASE B,INV_AGENT C  "
            + " WHERE A.INV_CODE = C.INV_CODE AND A.INV_CODE = B.INV_CODE AND A.PACK_CODE = '" + pack_code +
            "'";
    }

    /**
     *
     * @param pack_code String
     * @return String
     */
    public static String getINVPackDInfo(String pack_code) {
        return "SELECT A.INV_CODE, B.INV_CHN_DESC, B.STOCK_UNIT, "
            + " A.QTY, C.CONTRACT_PRICE COST_PRICE, B.SEQMAN_FLG, '0' AS RECOUNT_TIME, "
            + "'N' AS ONCE_USE_FLG "
            + " FROM INV_PACKD A, INV_BASE B ,INV_AGENT C  "
            + " WHERE A.INV_CODE = C.INV_CODE AND  A.INV_CODE = B.INV_CODE "
            + " AND A.PACK_CODE = '" + pack_code + "'";
    }

    /**
     *
     * @param pack_code String
     * @return String
     */
    public static String getINVPackDInfo(String pack_code, int pack_seq_no) {
        return "SELECT A.INV_CODE,B.INV_CHN_DESC,B.STOCK_UNIT,C.QTY,"
            + "D.CONTRACT_PRICE COST_PRICE, B.SEQMAN_FLG, C.RECOUNT_TIME, C.ONCE_USE_FLG, "
            + "C.INVSEQ_NO "
            + " FROM INV_PACKD A, INV_BASE B, INV_PACKSTOCKD C,INV_AGENT D "
            + "WHERE A.INV_CODE = B.INV_CODE "
            + "AND A.INV_CODE = C.INV_CODE " 
            + "AND A.INV_CODE = D.INV_CODE "
            + "AND A.PACK_CODE = C.PACK_CODE "
            + "AND B.INV_CODE = C.INV_CODE "
            + "AND A.PACK_CODE = '" + pack_code +
            "' AND C.PACK_SEQ_NO = " + pack_seq_no;
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getINVStockDDNotByPack(String org_code,
                                                String inv_code) {
        return "SELECT * FROM INV_STOCKDD WHERE ORG_CODE = '" + org_code +
            "' AND INV_CODE = '" + inv_code +
            "' AND PACK_FLG = 'N' ORDER BY INVSEQ_NO ";
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getINVStockQty(String org_code,
                                        String inv_code) {
        return "SELECT SUM(A.STOCK_QTY) AS STOCK_QTY, B.INV_CHN_DESC "
            + " FROM INV_STOCKD A, INV_BASE B "
            + " WHERE A.ORG_CODE = '" + org_code + "' AND A.INV_CODE = '" +
            inv_code + "' AND A.INV_CODE = B.INV_CODE GROUP BY B.INV_CHN_DESC";
    }
 
    /** 
     * @param org_code String
     * @param pack_code String 
     * @return String
     */
    public static String getINVPackStockQty(String org_code, String pack_code,
                                            int pack_seq_no, int batch_no) {
    	String selectSql = "";
    	selectSql = " SELECT SEQ_FLG FROM INV_PACKM  " +
    			" WHERE  PACK_CODE = '" + pack_code + "'";  
    	TParm parm = new TParm(TJDODBTool.getInstance().select(selectSql)); 
    	String seqFlg = parm.getValue("SEQ_FLG", 0); 
     	String sql = "";  
    	sql = "SELECT QTY AS STOCK_QTY FROM INV_PACKSTOCKM "
            + " WHERE ORG_CODE = '" + org_code + "' AND PACK_CODE = '" + 
            pack_code + "' AND PACK_SEQ_NO = " + pack_seq_no +
            //fux modify 20140401  
            " AND PACK_BATCH_NO = "+batch_no;  
    	//SEQ_FLG 手术包        
    	//0409 中心供应室          
    	if("1".equals(seqFlg) ){           
    		sql = sql + " AND STATUS = 9 ";  
    	}   
    	//040910 医疗物资供应室      
    	else{
    		sql = sql + " AND STATUS = 0 ";
    	}
        return sql;
            //" AND STATUS = 0 AND PACK_BATCH_NO = "+batch_no;		//STATUS <> 1
    } 
 
    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     */
    public static String getINVPackStockMQty(String org_code, String pack_code,
                                             int pack_seq_no) {
        return "SELECT QTY AS STOCK_QTY FROM INV_PACKSTOCKM "
            + " WHERE ORG_CODE = '" + org_code + "' AND PACK_CODE = '" +
            pack_code + "' AND PACK_SEQ_NO = " + pack_seq_no;
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getINVStockQtyOrderBySEQ(String org_code,
                                                  String inv_code) {
        return "SELECT STOCK_QTY FROM INV_STOCKD" + " WHERE ORG_CODE = '" +
            org_code + "' AND INV_CODE = '" +
            inv_code + "' ORDER BY BATCH_SEQ";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @return String
     */
    public static String getINVPackStockM(String org_code, String pack_code,
                                          int pack_seq_no) {
        return "SELECT * FROM INV_PACKSTOCKM WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO =" +
            pack_seq_no;
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @param inv_code String
     * @return String
     */
    public static String getINVPackStockD(String org_code, String pack_code,
                                          int pack_seq_no) {
        return "SELECT * FROM INV_PACKSTOCKD WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO =" +
            pack_seq_no + "'";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @param inv_code String
     * @return String
     */
    public static String getINVPackStockD(String org_code, String pack_code,
                                          int pack_seq_no, String inv_code) {
        return "SELECT * FROM INV_PACKSTOCKD WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO =" +
            pack_seq_no + " AND INV_CODE = '" + inv_code + "'";
    }

    /**
     *
     * @param sup_type_code String
     * @return String
     */
    public static String getINVSupType(String sup_type_code) {
        return "SELECT * FROM INV_SUPTYPE WHERE SUPTYPE_CODE = '" +
            sup_type_code + "'";
    }

    /**
     *
     * @param request_no String
     * @return String
     */
    public static String getINVSupRequestD(String request_no) {
        return "SELECT * FROM INV_SUPREQUESTD WHERE REQUEST_NO = '" +
            request_no + "'";
    }

    /**
     *
     * @param request_no String
     * @return String
     */
    public static String getInvSupRequestDInv(String request_no) {
        return "SELECT 'Y' AS SELECT_FLG,A.SEQ_NO,A.INV_CODE,B.INV_CHN_DESC, "
            + " A.QTY - A.ACTUAL_QTY AS QTY, A.STOCK_UNIT, B.SEQMAN_FLG,  "
            + " C.CONTRACT_PRICE COST_PRICE FROM INV_SUPREQUESTD A, INV_BASE B ,INV_AGENT C "
            + " WHERE A.INV_CODE = C.INV_CODE AND A.INV_CODE = B.INV_CODE AND A.UPDATE_FLG IN ('0', '1') "
            + " AND A.REQUEST_NO = '" + request_no + "'";
    }

    /**
     *
     * @param request_no String
     * @return String
     */
    public static String getInvSupRequestDPack(String request_no) {
        return "SELECT 'Y' AS SELECT_FLG, A.SEQ_NO, A.INV_CODE, "
            + " B.PACK_DESC AS INV_CHN_DESC, A.QTY - A.ACTUAL_QTY AS QTY, "
            + " A.STOCK_UNIT, B.SEQ_FLG FROM INV_SUPREQUESTD A, INV_PACKM B "
            + " WHERE A.INV_CODE = B.PACK_CODE AND A.UPDATE_FLG IN ('0', '1') "
            + " AND A.REQUEST_NO = '" + request_no + "'";
    }

    /**
     *
     * @param org_code String
     * @param inv_code String
     * @return String
     */
    public static String getInvSupInvChoose(String org_code, String inv_code) {
        return "SELECT 'N' AS SELECT_FLG, A.INVSEQ_NO, C.CONTRACT_PRICE COST_PRICE, "
            + " A.ORG_CODE, A.BATCH_NO, A.VALID_DATE, A.BATCH_SEQ "
            + " FROM INV_STOCKDD A, INV_BASE B, INV_AGENT C "
            + "  WHERE A.INV_CODE = B.INV_CODE AND A.INV_CODE = C.INV_CODE AND A.ORG_CODE = '" + org_code +
            "' AND A.INV_CODE = '" + inv_code + "' ORDER BY A.INVSEQ_NO";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String  
     * @return String
     */
    public static String getINvSupPackChoose(String org_code, String pack_code) {
        return "SELECT 'N' AS SELECT_FLG, A.PACK_SEQ_NO, "
            + " A.USE_COST + A.ONCE_USE_COST AS COST_PRICE, A.ORG_CODE, A.BARCODE, A.PACK_BATCH_NO "
            + " FROM INV_PACKSTOCKM A WHERE A.ORG_CODE = '" + org_code +
            //fux modify 20140401   0为在库状态  9是灭菌  回收清洗消毒后 ->再打包->灭菌(第一次基数管理入库后不许出库)
            //"' AND A.PACK_CODE = '" + pack_code + "' AND A.STATUS = '0' "
            "' AND A.PACK_CODE = '" + pack_code + "' AND A.STATUS = '9' "    
            + " ORDER BY A.PACK_SEQ_NO";    
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @return String
     */
    public static String getInvPackCostPrice(String org_code, String pack_code) {
        return "SELECT USE_COST + ONCE_USE_COST AS COST_PRICE "
            + " FROM INV_PACKSTOCKM WHERE ORG_CODE = '" + org_code +
            "' AND PACK_CODE = '" + pack_code + "' AND PACK_SEQ_NO = 0";
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int 
     * @param qty double
     * @return String
     */
    public static String getINVPackStockDInfo(String org_code, String pack_code,
                                              int pack_seq_no, double qty, int batch_no) {
    	return "SELECT B.INV_ABS_DESC AS INV_CHN_DESC, A.INVSEQ_NO, C.QTY * "
            + qty + " AS QTY, A.STOCK_UNIT, A.COST_PRICE, A.ONCE_USE_FLG, "
            + " '' AS BATCH_NO, '' AS VALID_DATE, A.INV_CODE, A.RECOUNT_TIME "
            + " FROM INV_PACKSTOCKD A, INV_BASE B, INV_PACKD C "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.INV_CODE = C.INV_CODE " 
            + " AND A.PACK_CODE = C.PACK_CODE "
            + " AND B.INV_CODE = C.INV_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' AND A.PACK_CODE = '" +
            pack_code + "' AND A.PACK_SEQ_NO =" + pack_seq_no + " " +
            " AND A.PACK_BATCH_NO = " + batch_no;
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getINVSupDispenseDDInfo(String dispense_no,
                                                 String pack_code,
                                                 int pack_seq_no, int pack_batch_no) {
        return "SELECT B.INV_ABS_DESC AS INV_CHN_DESC, A.INVSEQ_NO, A.QTY, A.STOCK_UNIT, "
            + " A.COST_PRICE, A.ONCE_USE_FLG, A.BATCH_NO, A.VALID_DATE, "
            + " A.INV_CODE FROM INV_SUP_DISPENSEDD A, INV_BASE B "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' "
            + " AND A.PACK_CODE = '" + pack_code + "' "
            + " AND A.PACK_SEQ_NO = " + pack_seq_no 
            + " AND A.PACK_BATCH_NO = " + pack_batch_no;   //2013-10-29增加批号
    }

    /**
     *
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @return String
     */
    public static String getInvSupDispenseDD(String org_code, String pack_code,
                                             int pack_seq_no, int batch_no) {
        return "SELECT A.PACK_CODE, A.PACK_SEQ_NO, A.INV_CODE, "
            + " A.INVSEQ_NO, A.ONCE_USE_FLG, B.QTY, A.STOCK_UNIT, "
            + " A.COST_PRICE, A.BATCH_SEQ, C.BATCH_NO, C.VALID_DATE, A.BARCODE, A.PACK_BATCH_NO "
            + " FROM INV_PACKSTOCKD A, INV_PACKD B, INV_STOCKD C "
            + " WHERE A.PACK_CODE = B.PACK_CODE "
            + " AND A.INV_CODE = B.INV_CODE "
            + " AND A.ORG_CODE = C.ORG_CODE "
            + " AND A.INV_CODE = C.INV_CODE "
            + " AND A.BATCH_SEQ = C.BATCH_SEQ "
            + " AND B.INV_CODE = C.INV_CODE "
            + " AND A.ORG_CODE = '" + org_code + "' AND A.PACK_CODE = '" +
            pack_code + "' AND A.PACK_SEQ_NO = " + pack_seq_no + " AND A.PACK_BATCH_NO = " + batch_no;
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getInvSupDispenseInvForPrint(String dispense_no) {
        return "SELECT B.INV_CHN_DESC, A.INVSEQ_NO, B.DESCRIPTION, "
            + " A.COST_PRICE, A.QTY, C.UNIT_CHN_DESC, "
            + " A.QTY * A.COST_PRICE AS AMT, A.BATCH_NO, A.VALID_DATE "
            + " FROM INV_SUP_DISPENSED A, INV_BASE B, SYS_UNIT C "
            + " WHERE A.INV_CODE = B.INV_CODE "
            + " AND A.STOCK_UNIT = C.UNIT_CODE "
            + " AND B.STOCK_UNIT = C.UNIT_CODE "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' ";
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getInvSupDispensePackForPrint(String dispense_no) {
        return "SELECT B.PACK_DESC, A.INVSEQ_NO, A.COST_PRICE, A.QTY, "
            + " A.COST_PRICE * A.QTY AS AMT "
            + " FROM INV_SUP_DISPENSED A, INV_PACKM B "
            + " WHERE A.INV_CODE = B.PACK_CODE AND A.DISPENSE_NO = '" +
            dispense_no + "' ";
    }

    /**
     * 更新INV_STOCKM库存量
     * @param org_code String
     * @param inv_code String
     * @param qty double
     * @return String
     */
    public static String updateInvStockM(String org_code, String inv_code,
                                         double qty) {
        return "UPDATE INV_STOCKM SET STOCK_QTY = STOCK_QTY - " + qty +
            " WHERE ORG_CODE = '" + org_code + "' AND INV_CODE = '" + inv_code +
            "'";
    }

    /**
     * 更新INV_STOCKD库存量
     * @param org_code String
     * @param inv_code String
     * @param batch_seq int
     * @param qty double
     * @return String
     */
    public static String updateInvStockD(String org_code, String inv_code,
                                         int batch_seq, double qty) {
        return "UPDATE INV_STOCKD SET STOCK_QTY = STOCK_QTY - " + qty +
            " WHERE ORG_CODE = '" + org_code + "' AND INV_CODE = '" + inv_code +
            "' AND BATCH_SEQ = " + batch_seq;
    }

    /**
     * 更新INV_PACKSTOCKD库存量
     * @param org_code String
     * @param pack_code String
     * @param pack_seq_no int
     * @param inv_code String
     * @param qty double
     * @return String
     */
    public static String updateInvPackStockD(String org_code, String pack_code,
                                             int pack_seq_no, String inv_code,
                                             double qty, int recount_time) {
        String sql = "";
        if (recount_time != 0) {
            sql = ", RECOUNT_TIME = " + recount_time;
        }
        return "UPDATE INV_PACKSTOCKD SET QTY = QTY + " + qty +
            sql + " WHERE ORG_CODE = '" +
            org_code + "' AND PACK_CODE = '" +
            pack_code + "' AND PACK_SEQ_NO = " + pack_seq_no +
            " AND INV_CODE = '" + inv_code + "'";
    }

    /**
     *
     * @param dispense_no String
     * @return String
     */
    public static String getIncSupDispenseChoose(String dispense_no,
                                                 String org_code) {
        return "SELECT 'Y' AS SELECT_FLG, A.INVSEQ_NO AS PACK_SEQ_NO, "
            + " A.INV_CODE AS PACK_CODE, B.PACK_DESC, A.QTY, C.USE_COST, "
            + " C.ONCE_USE_COST, C.STATUS "
            + " FROM INV_SUP_DISPENSED A, INV_PACKM B, INV_PACKSTOCKM C "
            + " WHERE A.INV_CODE = B.PACK_CODE "
            + " AND A.INV_CODE = C.PACK_CODE "
            + " AND B.PACK_CODE = C.PACK_CODE "
            + " AND A.INVSEQ_NO = C.PACK_SEQ_NO "
            + " AND A.DISPENSE_NO = '" + dispense_no + "' "
            + " AND C.ORG_CODE = '" + org_code + "' ";
    }

    /**
     * 得到查询计费项目字典的sql
     * @param parm TParm
     * @return String
     */
    public static String getQuerySuptitemSql(TParm parm) {
        String sql = "SELECT * FROM INV_SUPTITEM ";
        String filter = "";
        //记账编号
        String value = parm.getValue("SUPITEM_CODE");
        if (value != null && value.length() != 0) {
            if (filter.length() != 0)
                filter += " AND ";
            filter += "SUPITEM_CODE LIKE '" + value + "%'";
        }

        //记账序号
        value = parm.getValue("SUPITEM_DESC");
        if (value != null && value.length() != 0) {
            if (filter.length() != 0)
                filter += " AND ";
            filter += " SUPITEM_DESC like '" + value + "%'";
        }
        value = parm.getValue("PY1");
        if (value != null && value.length() != 0) {
            if (filter.length() != 0)
                filter += " AND ";
            filter += " PY1 like '" + value + "%'";
        }

        if (filter.length() > 0)
            sql = sql + " WHERE " + filter;
        sql = sql + " ORDER BY SUPITEM_CODE ";
        //System.out.println("dsfdf=" + sql);
        return sql;
    }

    /**
     * 物资原因
     * @return String
     */
    public static String getReason(){
        return "SELECT * FROM INV_REASON ";
    }

    /**
     * 物资原因
     * @return String
     */
    public static String getReason(String reason_code) {
        return "SELECT * FROM INV_REASON WHERE REN_CODE = '" + reason_code +
            "'";
    }
    
    
    /**
     * 
     * 根据部门查询库存主档信息-自动拨补
     * @param 
     * @return
     * @author 	wangming
     * @date 	20131029
     * 
     * */
    public static String queryAutoReqStockM(String orgCode){
    	String sql =  " SELECT A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY, " +          
        " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY," +    
        " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY, A.BASE_QTY, " +  
//        " A.BASE_QTY-A.STOCK_QTY AS REQ_QTY, " +  
        " CASE WHEN A.BASE_QTY > 0 THEN A.BASE_QTY-A.STOCK_QTY WHEN A.BASE_QTY <=0 THEN A.ECONOMICBUY_QTY-A.STOCK_QTY END AS REQ_QTY, " + 
        " C.PURCH_UNIT, C.PURCH_QTY, C.STOCK_UNIT, C.STOCK_QTY, " +   
        //fux modify 20140408 
        " D.CONTRACT_PRICE,D.CONTRACT_PRICE AS STOCK_PRICE, B.PUR_QTY, B.SUP_CODE " +
        " FROM INV_STOCKM A,INV_BASE B,INV_TRANSUNIT C,INV_AGENT D " +  
        " WHERE A.ORG_CODE='" + orgCode + "' AND B.SUP_CODE = '19' " +  
        " AND A.DISPENSE_FLG='Y' " +   
        " AND A.INV_CODE=B.INV_CODE " +      
        " AND A.INV_CODE=C.INV_CODE " +  
        " AND A.INV_CODE=D.INV_CODE " +     
//        " AND  A.STOCK_QTY<A.BASE_QTY " ;
//        " AND ( A.STOCK_QTY<A.BASE_QTY OR A.STOCK_QTY<A.ECONOMICBUY_QTY ) " ;
        " AND ( A.STOCK_QTY < ( CASE WHEN A.BASE_QTY > 0 THEN A.BASE_QTY WHEN A.BASE_QTY <= 0 THEN A.ECONOMICBUY_QTY END ) ) " ;
//        " GROUP BY A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY,"+            
//        " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY,A.STOCK_QTY," +
//        " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY, A.STOCK_UNIT,C.STOCK_QTY  ";
    	System.out.println("sql111:::"+sql);
    	return sql;
    }
    
    
    /**
     * 
     * 根据部门查询库存主档信息-自动拨补
     * @param 
     * @return
     * @author 	wangming
     * @date 	20131218
     * 
     * */
    public static String queryAutoReqStockMOtherDept(String orgCode){
    	
    	String sql =  " SELECT A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY, " +          
        " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY," +    
        " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY, A.BASE_QTY, " +    
//        " A.BASE_QTY-A.STOCK_QTY AS REQ_QTY, " +
        " CASE WHEN A.BASE_QTY > 0 THEN A.BASE_QTY-A.STOCK_QTY WHEN A.BASE_QTY <=0 THEN A.ECONOMICBUY_QTY-A.STOCK_QTY END AS REQ_QTY, " + 
        " C.PURCH_UNIT, C.PURCH_QTY, C.STOCK_UNIT, C.STOCK_QTY, " +
        //fux modify 20140408
        //" D.CONTRACT_PRICE, D.STOCK_PRICE, B.PUR_QTY, B.SUP_CODE " +
        " D.CONTRACT_PRICE,D.CONTRACT_PRICE AS STOCK_PRICE, B.PUR_QTY, B.SUP_CODE " +
        " FROM INV_STOCKM A,INV_BASE B,INV_TRANSUNIT C,INV_AGENT D " +  
        " WHERE A.ORG_CODE='" + orgCode + "' AND B.SUP_CODE NOT IN ('19') " + 
        " AND A.DISPENSE_FLG='Y' " +   
        " AND A.INV_CODE=B.INV_CODE " +   
        " AND A.INV_CODE=C.INV_CODE " + 
        " AND A.INV_CODE=D.INV_CODE " +
//        " AND  A.STOCK_QTY<A.BASE_QTY " ;
//        " AND ( A.STOCK_QTY<A.BASE_QTY OR A.STOCK_QTY<A.ECONOMICBUY_QTY ) " ;
        " AND ( A.STOCK_QTY < ( CASE WHEN A.BASE_QTY > 0 THEN A.BASE_QTY WHEN A.BASE_QTY <= 0 THEN A.ECONOMICBUY_QTY END ) ) " ;
//        " GROUP BY A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY,"+            
//        " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY,A.STOCK_QTY," +
//        " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY, A.STOCK_UNIT,C.STOCK_QTY  ";
    	System.out.println("sql2222:::"+sql);
    	
    	return sql;
    }

    /**
     * 根据部门查询库存主档信息-自动拨补
     * @param orgCode
     * @return
     * @author fux   INV_STOCKM 和INV_STOCKD？？？
     * @date 20130522
     */
    public static String queryStockM(String orgCode,String toOrgCode,String fixedType){
    	String condition = " ";
//    	String wheresql= "" ;
    	String wheresql1 ="" ;
    	if("1".equals(fixedType)){//
    		condition = "   HAVING (SUM(A.STOCK_QTY)/C.STOCK_QTY) < A.SAFE_QTY ";
    	}else if("2".equals(fixedType)){
    		condition = "   HAVING (SUM(A.STOCK_QTY)/C.STOCK_QTY) < A.MIN_QTY ";
    	}
//    	 if(orgCode.equals("011201")){
//    		 wheresql = " AND A.INV_CODE  NOT LIKE '08%' "  ;  //主库高值耗材不能订购（暂时这样写）
//    	 } 
    	 if(toOrgCode.length()>0)   wheresql1 = " AND A.DISPENSE_ORG_CODE = '"+toOrgCode+"'"  ;
    	String sql =  " SELECT A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY, "+          
    	              " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY,A.STOCK_QTY,"+    
    	              " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY,A.STOCK_UNIT "+  
    	              " FROM INV_STOCKM A,INV_BASE B,INV_TRANSUNIT C"+  
    	              " WHERE A.ORG_CODE='" + orgCode + "'  "+ 
 //   	              wheresql+ " "+
    	              wheresql1+ " "+
    	              " AND A.DISPENSE_FLG='Y'    "+  
                      " AND A.INV_CODE=B.INV_CODE "+  
    	              " AND A.INV_CODE=C.INV_CODE "      
                       + condition + " "+   
    	              " GROUP BY A.ORG_CODE,A.INV_CODE,A.STOCK_FLG,A.SAFE_QTY,"+            
    	              " A.MIN_QTY,A.MAX_QTY,A.ECONOMICBUY_QTY,A.STOCK_QTY," +
    	              " A.MM_USE_QTY,A.AVERAGE_DAYUSE_QTY, A.STOCK_UNIT,C.STOCK_QTY  ";
    	return sql;
    }
    
    

    
    
    
	/**
     * 保存申请主档-自动拨补
     * @param orgCode
     * @return
     * @author fux 
     * @date 20130527 
     */   
    public static String saveRequestMAuto(TParm parm ){ 
    	String sql = " INSERT INTO INV_REQUESTM(REQUEST_NO,REQUEST_TYPE, REQUEST_DATE," +
    			     " FROM_ORG_CODE,TO_ORG_CODE,REN_CODE,URGENT_FLG, REMARK," +
                     " FINAL_FLG , OPT_USER, OPT_DATE, OPT_TERM) "
    		       + " VALUES("  
    		       + " '" + parm.getValue("REQUEST_NO")+ "', 'ATO', TO_DATE('" + parm.getValue("REQUEST_DATE").substring(0, 19) + "','YYYY-MM-DD HH24:MI:SS'),"
    		       + " '" + parm.getValue("FROM_ORG_CODE")+ "','" + parm.getValue("TO_ORG_CODE")+ "','" + parm.getValue("REN_CODE")+ "'," +
    		       	 " '" + parm.getValue("URGENT_FLG")+ "','" + parm.getValue("REMARK")+ "',"
    		       + " '" + parm.getValue("FINAL_FLG")+ "', 'OPTUSER', sysdate,'OPTID'"  
    		       + " ) ";   
    	return sql;  
    }   
    public static String deleteRequestMAuto(TParm parm ){ 
    	String sql = "DELETE FROM  INV_REQUESTM  WHERE REQUEST_NO = '"+parm.getValue("REQUEST_NO")+"' "  ;
    	return sql;  
    }   
       
    /**
     * 保存申请明细档-自动拨补
     * @param orgCode
     * @return
     * @author fux
     * @date 20130527  
     */  
    public static String saveRequestDAuto(TParm parm ){  
    	//fux need modify
    	String sql = " INSERT INTO INV_REQUESTD(REQUEST_NO, SEQ_NO, INV_CODE, " +
    			     " INVSEQ_NO, QTY, ACTUAL_QTY, " +
    			     " BATCH_SEQ,BATCH_NO,VALID_DATE,FINA_TYPE," +
    			     " OPT_USER, OPT_DATE, OPT_TERM )"   
    		       + " VALUES("                              
    		       + " '" + parm.getValue("REQUEST_NO")+ "', " + parm.getValue("SEQ_NO")+ ", '" + parm.getValue("INV_CODE")+ "',"
    		       + "  " + parm.getValue("INVSEQ_NO")+ "," + parm.getValue("QTY")+ ",0,"
    		       + " '" + parm.getValue("BATCH_NO")+ "','" + parm.getValue("BATCH_NO")+ "','" + parm.getValue("VALID_DATE")+ "'," +
    		       	 " '" + parm.getValue("FINA_TYPE")+ "','OPTUSER', sysdate,'OPTIP' "
    		       + " ) ";    
    	return sql;   
    }   
    
    
    /**
     * 保存订购/移货主档-自动拨补
     * @param orgCode
     * @return
     * @author fux 
     * @date 20130527    
     */  
    public static String savePurOrderMAuto(TParm parm){
    	String sql = " INSERT INTO INV_PURORDERM (PURORDER_NO, PURORDER_DATE,FINAL_FLG, " +
    			     " ORG_CODE, SUP_CODE, RES_DELIVERY_DATE,  " 
    			   + " OPT_USER, OPT_DATE, OPT_TERM,REN_CODE,CHECK_FLG,CHECK_DATE,CHECK_USER," +
    			   		" FROM_TYPE,CON_ORG,CON_FLG) "
    		       + " VALUES("                       
    		       + " '" + parm.getValue("PURORDER_NO")+ "',sysdate,'N' ," +
    		       		" '" + parm.getValue("ORG_CODE")+ "'," + " '" + parm.getValue("SUP_CODE")
    		       + "',null,'OPTUSER', sysdate,'OPTIP','R06','N',sysdate,'OPTUSER','1'," + 
    		       		" '" + parm.getValue("CON_ORG")+ "','" + parm.getValue("CON_FLG")+ "' "   
    		       + " ) ";
    	return sql;  
    } 
    
    
    /**
     * 修改订购
     * @param parm
     * @return
     */
    public static String updatePurOrderMAuto(TParm parm ){
    	String sql = "UPDATE INV_PURORDERM SET PURORDER_AMT = "+parm.getValue("PURORDER_AMT")+" WHERE PURORDER_NO ='"+parm.getValue("PURORDER_NO")+"' "  ;
    	return sql;  
    } 
    
    /**
     * 删除订购
     * @param parm
     * @return
     */
    public static String deletePurOrderMAuto(TParm parm ){
    	String sql = "DELETE FROM INV_PURORDERM WHERE PURORDER_NO ='"+parm.getValue("PURORDER_NO")+"' "  ;
    	return sql;  
    } 
   
    /**
     * 保存订购/移货明细档-自动拨补
     * @param orgCode
     * @return  
     * @author fux  
     * @date 20130527    
     */  
    public static String savePurOrderDAuto(TParm parm){ 
    	String sql = " INSERT INTO INV_PURORDERD (PURORDER_NO, SEQ_NO, " +
    			     " INV_CODE, PURORDER_QTY, GIFT_QTY, BILL_UNIT, " 
    			   + " PURORDER_PRICE,PURORDER_AMT,STOCKIN_SUM_QTY, " +   
    //			   	 " OPT_USER, OPT_DATE, OPT_TERM, STOCK_PRICE, STOCK_QTY, STOCK_UNIT) "  
    			   	 " OPT_USER, OPT_DATE, OPT_TERM ) "
    		       + " VALUES("     
    		       + " '" + parm.getValue("PURORDER_NO")+ "', " + parm.getValue("SEQ_NO")+ ", '" + parm.getValue("INV_CODE")+ "',"
    		       + "  " + parm.getValue("REQ_QTY")+ ",0,'" + parm.getValue("BILL_UNIT")+ "','" + parm.getValue("PURORDER_PRICE")+ "',"  //parm.getValue("PURORDER_QTY")
    		       + "  '" + parm.getValue("PURORDER_AMT")+ "',0,'OPTUSER', sysdate,'OPTIP' " 
 //   		       + " ,'"+parm.getValue("STOCK_PRICE")+"',"+parm.getValue("STOCK_QTY")+",'"+parm.getValue("STOCK_UNIT")+"') ";   
       		       + " ) ";   
    	return sql;   
    } 
    
    /**
     * 获取物资在途量     订购
     */
    public static String  getOnWayQty(String invCode,String conCode){
    	String sql = " SELECT  D.PURORDER_QTY,D.PURORDER_PRICE,D.STOCK_QTY,D.STOCK_PRICE  FROM  INV_PURORDERM M ,INV_PURORDERD D  WHERE " +
    			     " M.PURORDER_NO = D.PURORDER_NO " +
    			     " AND M.FINAL_FLG = 'N' " +
    			     " AND D.INV_CODE = '"+invCode+"' " + 
    			     " AND M.CON_ORG = '" + conCode + "' ";
    	return  sql ;
    }
    
    
    
    /**
     * 获取物资在途量     订购
     */
    public static String  getOnWayQuety(String invCode){
    	String sql = " SELECT  D.PURORDER_QTY  FROM  INV_PURORDERM M ,INV_PURORDERD D  WHERE " +
    			     " M.PURORDER_NO = D.PURORDER_NO " +
    			     " AND M.FINAL_FLG = 'N' " +
    			     " AND D.INV_CODE = '"+invCode+"'"   ;
    	return  sql ;
    }
    /**
     * 获取物资在途量   ，请领
     */
    public static String  getOnWayRequest(String invCode){
    	String sql = " SELECT  D.QTY  FROM  INV_REQUESTM M ,INV_REQUESTD D  WHERE " +
	     " M.REQUEST_NO= D.REQUEST_NO " +
	     " AND M.FINAL_FLG = 'N' " +
	     " AND D.INV_CODE = '"+invCode+"'"   ;
     return  sql ;
    }
    /**
     * 根据物资代码获得物资信息
     *
     * @param invcode 
     * @return   
     */ 
    public static String getINVInfoByOrder(String invcode) {    
        return " SELECT  A.INV_CODE, C.CONTRACT_PRICE COST_PRICE,B.STOCK_UNIT, B.STOCK_QTY," +
        	   " B.PURCH_QTY, B.PURCH_UNIT,B.DISPENSE_UNIT,B.DISPENSE_QTY "+
               " FROM INV_BASE A, INV_TRANSUNIT B, INV_AGENT C "+
               " WHERE A.INV_CODE = B.INV_CODE AND A.INV_CODE = C.INV_CODE "+
               " AND A.INV_CODE = '"
               + invcode + "'";    
    }
    
    
    
    
    /**
     * 保存订购/移货主档-自动拨补  (非国药)
     * @param orgCode
     * @return
     * @author wangming 
     * @date 20131218  
     */  
    public static String saveReqOrderMAuto(TParm parm){
    	String sql = " INSERT INTO INV_REQUESTM (REQUEST_NO, REQUEST_TYPE ,REQUEST_DATE,FINAL_FLG, " +
    			     " FROM_ORG_CODE, TO_ORG_CODE, URGENT_FLG, REMARK,  " 
    			   + " OPT_USER, OPT_DATE, OPT_TERM,REN_CODE) " 
    		       + " VALUES("                       
    		       + " '" + parm.getValue("REQUEST_NO")+ "','"+parm.getValue("REQUEST_TYPE")+"',sysdate,'"+parm.getValue("FINAL_FLG")+"' ," +
    		       		" '" + parm.getValue("FROM_ORG_CODE")+ "'," + " '" + parm.getValue("TO_ORG_CODE")
    		       + "','"+parm.getValue("URGENT_FLG")+"',null,'OPTUSER', sysdate,'OPTIP','R06' ) ";
    	return sql;  
    } 
    
    
    /**
     * 获取物资在途量     订购   (非国药)
     */
    public static String  getOnWayQtyOtherDept(String invCode,String orgCode){
    	String sql = " SELECT  D.QTY  FROM  INV_REQUESTM M ,INV_REQUESTD D  WHERE " +
    			     " M.REQUEST_NO = D.REQUEST_NO " +
    			     " AND M.FINAL_FLG = 'N' " +
    			     " AND D.INV_CODE = '"+invCode+"' " + 
    			     " AND M.TO_ORG_CODE = '" + orgCode + "' ";
    	return  sql ;
    }
    
    
    /**
     * 保存订购/移货明细档-自动拨补 (非国药)
     * @param orgCode
     * @return  
     * @author fux  
     * @date 20130527    
     */  
    public static String saveReqOrderDAuto(TParm parm){ 
    	String sql = " INSERT INTO INV_REQUESTD (REQUEST_NO, SEQ_NO, INVSEQ_NO," +
    			     " INV_CODE, QTY, ACTUAL_QTY, FINA_TYPE, " + 
    			   	 " OPT_USER, OPT_DATE, OPT_TERM ) "
    		       + " VALUES("     
    		       + " '" + parm.getValue("REQUEST_NO")+ "', " + parm.getValue("SEQ_NO")+ ", " + parm.getValue("SEQ_NO") + ", '" + parm.getValue("INV_CODE")+ "',"
    		       + "  " + parm.getValue("QTY")+ ", 0,'"+parm.getValue("FINA_TYPE")+"','D001', sysdate,'127.0.0.1' ) ";   
    	return sql;   
    } 
    
    
    /**
     * 删除订购   (非国药)
     * @param parm
     * @return
     */
    public static String deleteReqOrderMAuto(TParm parm ){
    	String sql = "DELETE FROM INV_REQUESTM WHERE REQUEST_NO ='"+parm.getValue("REQUEST_NO")+"' "  ;
    	return sql;  
    } 
    
//------------供应室新增方法start---------------
    
    /**
     * 查询手术包主表的sql
     * @param packCode String (包号)
     * @param packSeqNo int (序号)
     * @return String
     */  
    public static String getQueryStockDSql(String packCode, int packSeqNo) {
        String sql = "select * from INV_PACKSTOCKD";
        if (packCode != null && packCode.length() != 0) {
            sql += " WHERE PACK_CODE ='" + packCode + "'";
            if (packSeqNo >= 0)
                sql += " AND PACK_SEQ_NO=" + packSeqNo;
        }
        return sql;
    }
    
    /**
     * 初始化手术包库存主档datastore GYSUsed
     * @return String
     */
    public static String getInitPackStockMSql() {
        return "select * from INV_PACKSTOCKM WHERE PACK_CODE IS NULL";
    }

    /**
     * 初始化手术包库存明细档sql  GYSUsed
     * @return String
     */
    public static String getInitPackStockDSql() {
        return "select * from INV_PACKSTOCKD WHERE PACK_CODE IS NULL";
    }
    
    
    /**
     * 取得医院名称sql
     * @return String
     */
    public static String getHospArea() {
        String sql = "select HOSP_CHN_DESC,HOSP_CHN_ABN from SYS_HOSP_AREA";
        return sql;
    }
    
    /**
     * 取得物资名称和单位
     * @param inv_code String
     * @return String
     */
    public static String getInvInfo(String inv_code) {
        return "SELECT  "
            + " A.INV_CHN_DESC, S.UNIT_CHN_DESC "   
            + " FROM INV_BASE A, INV_TRANSUNIT B, SYS_UNIT S "
            + " WHERE A.INV_CODE = B.INV_CODE AND S.UNIT_CODE = A.STOCK_UNIT "  
            + "AND A.INV_CODE = '" + inv_code + "'";
    }
    
    //------------供应室新增方法end---------------

	/**
	 * 查询国药出货单是否已经导入到物联网-验收用
	 * 
	 * @param orderCode
	 *            物资代码
	 * @param boxCode
	 *            货箱条码(物资不需要周转箱)
	 * @param billNo
	 *            销售单号-在验收表里字段是PURORDER_NO
	 * @param erpId
	 *            国药出货单-ID(INV_VERIFYIND 暂无)
	 * @return String
	 */
	public static String getErpIdInfo(String invCode, String boxCode,
			String billNo, String erpId) {  
		String sql = " SELECT COUNT(*) AS COUNT_NUM  FROM INV_VERIFYIND WHERE 1=1 ";
		//国药出库单id     是否要在物资入库INV_VERIFYIND中加入国药出库单id？
		if (StringUtils.isNotBlank(erpId)) {
			sql += " AND ERP_PACKING_ID='" + erpId + "' ";
		} 
		if (StringUtils.isNotBlank(invCode)) {
			sql += " AND INV_CODE='" + invCode + "' ";
		}
		if (StringUtils.isNotBlank(billNo)) {
			sql += " AND PURORDER_NO='" + billNo + "' ";
		}
		//货箱条码
//		if (StringUtils.isNotBlank(boxCode)) {
//			sql += " AND SPC_BOX_CODE='" + boxCode + "' ";
//		}
		return sql;
	}
    
	/**
	 * 根据出库科室编号及物资代码查询药品的批次序号、库存并以有效期进行排序
	 * 
	 * @param org_code
	 *            String
	 * @param order_code  
	 *            String
	 * @param sort
	 *            String
	 * @return String
	 */
	public static String getInvStockBatchAndQty(String org_code,
			String order_code) {
		return " SELECT 'Y' AS SELECT_FLG, B.INV_CHN_DESC,"+
               " B.DESCRIPTION, A.STOCK_QTY AS QTY, A.STOCK_QTY AS REQUEST_QTY,"+ 
               " B.DISPENSE_UNIT,D.CONTRACT_PRICE ,A.STOCK_QTY * D.CONTRACT_PRICE  AS SUM_AMT,"+ 
               " A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE, B.MAN_CODE, 'N' AS DISPOSAL_FLG, "+
               " A.INV_CODE,B.SEQMAN_FLG, C.DISPENSE_QTY, C.STOCK_QTY,A.UNIT_PRICE AS COST_PRICE "+
               " FROM INV_STOCKD A, INV_BASE B, INV_TRANSUNIT C,INV_AGENT D "+ 
               " WHERE A.INV_CODE = B.INV_CODE "+   
               " AND A.INV_CODE = C.INV_CODE "+ 
               " AND B.INV_CODE = C.INV_CODE "+     
               " AND  A.INV_CODE = D.INV_CODE "+ 
               " AND A.ORG_CODE = '"
				+ org_code + "' "     
				+ "AND A.INV_CODE = '" + order_code
				+ "' AND B.ACTIVE_FLG='Y' " 
				//+ " AND SYSDATE < A.VALID_DATE "
				+ " AND A.STOCK_QTY  > 0 " + " " 
				+ " ORDER BY A.VALID_DATE "
				+ " , A.BATCH_SEQ";
	}
    
      
}
