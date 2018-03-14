package jdo.inv;

import com.dongyang.data.TParm;

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

public class INVSUPSQL {
    public INVSUPSQL() {
    }
    
    /**
    *	供应室出库时pack信息表单击时查询手术包内容物明细
    * @param org_code String
    * @param pack_code String
    * @param pack_seq_no int
    * @param qty double
    * @return String
    */
   public static String getINVPackStockDInfoDisp(String org_code, String pack_code,
                                             int pack_seq_no, double qty, int batch_no) {
   	
       return "SELECT B.INV_ABS_DESC AS INV_CHN_DESC, C.QTY * "
           + qty + " AS QTY, C.STOCK_UNIT, G.CONTRACT_PRICE AS COST_PRICE, CASE WHEN C.PACK_TYPE = '1' THEN 'Y' WHEN C.PACK_TYPE = '0' THEN 'N' END AS ONCE_USE_FLG, "
           + " C.INV_CODE "
           + " FROM INV_PACKD C, INV_BASE B, INV_AGENT G "
           + " WHERE C.INV_CODE = B.INV_CODE "
           + " AND C.INV_CODE = G.INV_CODE "
           + " AND  C.PACK_CODE = '" +
           pack_code + "' ";
   }
    
    
   /**
   *
   * @param org_code String
   * @param pack_code String
   * @param pack_seq_no int
   * @return String
   */
  public static String getInvSupDispenseDD(String org_code, String pack_code,
                                           String inv_code, int batch_no) {
      return "SELECT A.PACK_CODE, A.PACK_SEQ_NO, A.INV_CODE, "
          + " A.INVSEQ_NO, A.ONCE_USE_FLG, A.QTY, A.STOCK_UNIT, "
          + " A.COST_PRICE, A.BATCH_SEQ, C.BATCH_NO, C.VALID_DATE, A.BARCODE, A.PACK_BATCH_NO "
          + " FROM INV_PACKSTOCKD A, INV_PACKD B, INV_STOCKD C "
          + " WHERE A.PACK_CODE = B.PACK_CODE "
          + " AND A.INV_CODE = B.INV_CODE "
          + " AND A.ORG_CODE = C.ORG_CODE "
          + " AND A.INV_CODE = C.INV_CODE "
          + " AND A.BATCH_SEQ = C.BATCH_SEQ "
          + " AND B.INV_CODE = C.INV_CODE AND A.QTY > 0 "
          + " AND A.ORG_CODE = '" + org_code + "' AND A.PACK_CODE = '" +
          pack_code + "' AND A.INV_CODE = '"+inv_code+"' AND A.PACK_BATCH_NO = " + batch_no;
  }
  
  
  public static String getPackDInfo(String packCode){
	  
	  return " SELECT D.PACK_CODE, D.INV_CODE, D.QTY FROM INV_PACKD D WHERE D.PACK_CODE = '"+packCode+"' ";
	  
  }

  public static String getPackMInfo(String packCode){
	  
	  return " SELECT M.PACK_CODE, M.SEQ_FLG FROM INV_PACKM M WHERE M.PACK_CODE = '"+packCode+"' ";
	  
  }
}
