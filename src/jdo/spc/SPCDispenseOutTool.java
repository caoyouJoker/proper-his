package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 出库装箱Tool
 * </p>
 *
 * <p>
 * Description: 出库装箱Tool
 * </p>
 *
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 *
 * <p>
 * Company: BLUECORE
 * </p>
 *
 * @author Yuanxm 2012.12.4
 * @version 1.0
 */
public class SPCDispenseOutTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCDispenseOutTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCDispenseOutTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCDispenseOutTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCDispenseOutTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    /**
     * 根据请求单号查询所有的未出库的数据并未上架的
     * @param parm
     * @return
     */
    public TParm onQuery(TParm parm ){
    	
    	String requestNo = parm.getValue("REQUEST_NO");
    	String updateFlg = parm.getValue("UPDATE_FLG");
    	String reqtypeCode = parm.getValue("REQTYPE_CODE");
    	if(StringUtil.isNullString(requestNo)){
    		return new TParm();
    	}
    	
    	String sql = "SELECT CASE WHEN C.GOODS_DESC IS NULL THEN C.ORDER_DESC ELSE  "+
			         "   	C.ORDER_DESC || '(' || C.GOODS_DESC || ')' END AS ORDER_DESC, "+
			         "   	C.SPECIFICATION, B.QTY, B.ACTUAL_QTY, B.UNIT_CODE,  "+
			         "   	B.STOCK_PRICE, B.RETAIL_PRICE, B.BATCH_NO, B.VALID_DATE, "+
			         "   	C.PHA_TYPE, B.ORDER_CODE, D.STOCK_QTY, D.DOSAGE_QTY, "+
			         "   	C.TRADE_PRICE , B.SEQ_NO AS REQUEST_SEQ,B.BATCH_SEQ,"+
			         "   	E.ELETAG_CODE,E.MATERIAL_LOC_CODE,F.UNIT_CHN_DESC " +
			         "FROM IND_REQUESTM A,IND_REQUESTD B,PHA_BASE C,PHA_TRANSUNIT D,IND_STOCKM E,SYS_UNIT F "+
			         "WHERE  A.REQUEST_NO = B.REQUEST_NO "+
			         " 		AND B.ORDER_CODE = C.ORDER_CODE  "+
			         " 		AND B.ORDER_CODE = D.ORDER_CODE " ;
    	if(reqtypeCode.equals("WAS") ){
    		sql +=  "	    AND A.APP_ORG_CODE = E.ORG_CODE " ;
    	}else {
			sql +=  "	    AND A.TO_ORG_CODE = E.ORG_CODE " ;
    	}
			  sql += " 		AND B.ORDER_CODE=E.ORDER_CODE "+
			         "      AND B.UNIT_CODE = F.UNIT_CODE" +
    				 "      AND B.REQUEST_NO = '"+requestNo+"' ";
        if(updateFlg != null && !updateFlg.equals("")) {
        	sql += " AND B.UPDATE_FLG <> '"+updateFlg+"' ";
        } 	
       
    	return  new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 根据出库单查询对应的出库名细 
     * @param parm
     * @return
     */
    public TParm onQueryDispense(TParm parm){
    	
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String sqlGetIndDispenseDByNo = 
    		"SELECT DISTINCT UNIT_TYPE FROM IND_DISPENSEM WHERE DISPENSE_NO = '" + dispenseNo + "'";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sqlGetIndDispenseDByNo));
        if (result.getCount("UNIT_TYPE") == 0) {
            return new TParm() ;
        }
    	if(result.getValue("UNIT_TYPE", 0).equals("1")){
    		sqlGetIndDispenseDByNo = 
    			" SELECT CASE" +
    			" WHEN B.GOODS_DESC IS NULL" +
    			" THEN B.ORDER_DESC" +
    			" ELSE B.ORDER_DESC || '(' || B.GOODS_DESC || ')'" +
    			" END AS ORDER_DESC," +
    			" B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE," +
    			" A.VERIFYIN_PRICE AS STOCK_PRICE, A.RETAIL_PRICE AS RETAIL_PRICE," +
    			" A.BATCH_NO, A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY," +
    			" C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ,D.UNIT_CHN_DESC ," +
    			" A.VALID_DATE,A.SUP_CODE,F.MATERIAL_LOC_CODE,F.ELETAG_CODE  "+
    			" FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C,SYS_UNIT D , IND_DISPENSEM E,IND_STOCKM F  " +
    			" WHERE A.ORDER_CODE = B.ORDER_CODE" +
    			" AND A.ORDER_CODE = C.ORDER_CODE" +
    			" AND A.UNIT_CODE = D.UNIT_CODE(+) "+
    			" AND A.DISPENSE_NO=E.DISPENSE_NO "+
    			" AND E.TO_ORG_CODE = F.ORG_CODE "+
    			" AND A.ORDER_CODE= F.ORDER_CODE "+
    			" AND A.DISPENSE_NO = '" + dispenseNo + "'";
    	}else{
    		sqlGetIndDispenseDByNo = "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
                						  + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
                						  + "B.SPECIFICATION, A.QTY, A.ACTUAL_QTY, A.UNIT_CODE, "
                						  + "A.VERIFYIN_PRICE AS STOCK_PRICE, "
                						  + "A.RETAIL_PRICE "
                						  + "AS RETAIL_PRICE, A.BATCH_NO, A.VALID_DATE, "
                						  + "B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, C.DOSAGE_QTY, "
                						  + "B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ , D.UNIT_CHN_DESC,F.MATERIAL_LOC_CODE , F.ELETAG_CODE, "
                						  + " A.SUP_CODE "
                					+ "FROM IND_DISPENSED A , PHA_BASE B , PHA_TRANSUNIT C , SYS_UNIT D  , IND_DISPENSEM E,IND_STOCKM F   "
                					+ "WHERE A.ORDER_CODE = B.ORDER_CODE "
                						  + "AND A.ORDER_CODE = C.ORDER_CODE "
                						  +" AND A.UNIT_CODE = D.UNIT_CODE(+) "
                						  +" AND A.DISPENSE_NO=E.DISPENSE_NO "
                			    		  +" AND E.TO_ORG_CODE = F.ORG_CODE "
                			    		  +" AND A.ORDER_CODE= F.ORDER_CODE "
                						  + "AND A.DISPENSE_NO = '"+ dispenseNo + "'";
    	}
    	 
    	result = new TParm(TJDODBTool.getInstance().select(
        		sqlGetIndDispenseDByNo));
    	
    	return result ;
    	
    }
    
    /**
     * 查询是否最小单位(片)，返回TRUE 是最小，否是库存单位(盒 )
     * @param parm
     * @return
     */
    public boolean onQueryDosageUnit(TParm parm ){
    	String sql = "    SELECT A.DOSAGE_UNIT FROM PHA_BASE A " +
    			     "  WHERE A.ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' AND A.DOSAGE_UNIT='"+parm.getValue("UNIT_CODE")+"' ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount("DOSAGE_UNIT") > 0){
    		return true ;
    	}
    	return false;
    
    }
}
