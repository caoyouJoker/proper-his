package jdo.spc;

import jdo.sys.SystemTool;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;

public class IndStockBatchUpdateTool extends TJDOTool {

    /**
     * 实例
     */
    public static IndStockBatchUpdateTool instanceObject;

    /**
     * 得到实例
     *
     * @return IndReStockBatchTool
     */
    public static IndStockBatchUpdateTool getInstance() {
        if (instanceObject == null)
            instanceObject = new IndStockBatchUpdateTool();
        return instanceObject;
    }
    
    /**
     * 日结过帐
     * @param org_code String
     * @param date String
     * @return boolean
     */
    public TParm onReStockBatch(TParm parm, TConnection conn) {
    	String org_code = parm.getValue("ORG_CODE");
    	String date = parm.getValue("DATE");
    	String lastDate = parm.getValue("LAST_DATE");
        // 1.删除当天的IND_DDSTOCK数据
    	TParm  result  = new TParm();
		result = deleteIndDDstock(org_code, date,conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 2.取得当前IND_STOCK数据
        TParm ind_stock = getIndDDStockHistory(org_code,lastDate);
        if (ind_stock.getErrCode() < 0) {
			return ind_stock;
		}
        // 3.插入IND_DDSTOCK
        for(int i=0;i<ind_stock.getCount();i++) {
        	TParm ddstockParm = new TParm();
        	ddstockParm.setData("TRANDATE",date);
			// 2-ORG_CODE
        	ddstockParm.setData("ORG_CODE", ind_stock.getData("ORG_CODE",i));
			// 3-ORDER_CODE
        	ddstockParm.setData("ORDER_CODE", ind_stock.getData("ORDER_CODE", i));
			// 4-BATCH_SEQ	
        	ddstockParm.setData("BATCH_SEQ", ind_stock.getData("BATCH_SEQ", i));
			// 5-BATCH_NO
        	ddstockParm.setData("BATCH_NO", ind_stock.getData("BATCH_NO", i));
			// 6-VALID_DATE
        	ddstockParm.setData("VALID_DATE", ind_stock.getData("VALID_DATE", i));
			// 7-REGION_CODE
        	ddstockParm.setData("REGION_CODE", ind_stock.getData("REGION_CODE", i));
			// 8-STOCK_QTY
        	ddstockParm.setData("STOCK_QTY", ind_stock.getData("STOCK_QTY", i));
			// 9-STOCK_AMT
        	ddstockParm.setData("STOCK_AMT", ind_stock.getData("STOCK_AMT", i));
			// 10-LAST_TOTSTOCK_QTY
        	ddstockParm.setData("LAST_TOTSTOCK_QTY", ind_stock.getData("STOCK_QTY", i));
			// 11-LAST_TOTSTOCK_AMT
        	ddstockParm.setData("LAST_TOTSTOCK_AMT", ind_stock.getData("STOCK_AMT", i));
			// 12-IN_QTY
        	ddstockParm.setData("IN_QTY", 0);
			// 13-IN_AMT
        	ddstockParm.setData("IN_AMT", 0);
			// 14-OUT_QTY
        	ddstockParm.setData("OUT_QTY", 0);
			// 15-OUT_AMT
        	ddstockParm.setData("OUT_AMT", 0);
			// 16-CHECKMODI_QTY
        	ddstockParm.setData("CHECKMODI_QTY", 0);
			// 17-CHECKMODI_AMT
        	ddstockParm.setData("CHECKMODI_AMT", 0);
			// 18-VERIFYIN_QTY
        	ddstockParm.setData("VERIFYIN_QTY", 0);
			// 19-VERIFYIN_AMT
        	ddstockParm.setData("VERIFYIN_AMT", 0);
			// 20-FAVOR_QTY
        	ddstockParm.setData("FAVOR_QTY", 0);
			// 21-REGRESSGOODS_QTY
        	ddstockParm.setData("REGRESSGOODS_QTY", 0);
			// 22-REGRESSGOODS_AMT
        	ddstockParm.setData("REGRESSGOODS_AMT", 0);
			// 23-DOSAGE_QTY
        	ddstockParm.setData("DOSAGE_QTY", 0);
			// 24-DOSAGE_AMT
        	ddstockParm.setData("DOSAGE_AMT", 0);
			// 25-REGRESSDRUG_QTY
        	ddstockParm.setData("REGRESSDRUG_QTY", 0);
			// 26-REGRESSDRUG_AMT
        	ddstockParm.setData("REGRESSDRUG_AMT", 0);
			// 27-PROFIT_LOSS_AMT
        	ddstockParm.setData("PROFIT_LOSS_AMT", 0);
			// 28-VERIFYIN_PRICE
        	ddstockParm.setData("VERIFYIN_PRICE", ind_stock.getData("VERIFYIN_PRICE", i));
			// 29-STOCK_PRICE					
        	ddstockParm.setData("STOCK_PRICE", ind_stock.getData("STOCK_PRICE", i));
			// 30-RETAIL_PRICE
        	ddstockParm.setData("RETAIL_PRICE",ind_stock.getData("RETAIL_PRICE", i));
			// 31-TRADE_PRICE			
        	ddstockParm.setData("TRADE_PRICE", ind_stock.getData("TRADE_PRICE", i));
			// 32-STOCKIN_QTY
        	ddstockParm.setData("STOCKIN_QTY", 0);
			// 33-STOCKIN_AMT
        	ddstockParm.setData("STOCKIN_AMT", 0);
			// 34-STOCKOUT_QTY
        	ddstockParm.setData("STOCKOUT_QTY", 0);
			// 35-STOCKOUT_AMT
        	ddstockParm.setData("STOCKOUT_AMT", 0);
			// 36-OPT_USER
        	ddstockParm.setData("OPT_USER", ind_stock.getData("OPT_USER",i));
			// 37-OPT_DATE   
        	ddstockParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
			// 38-OPT_TERM
        	ddstockParm.setData("OPT_TERM", ind_stock.getData("OPT_TERM",i));
			// 39-REQUEST_IN_QTY
        	ddstockParm.setData("REQUEST_IN_QTY", 0);
			// 40-REQUEST_IN_AMT
        	ddstockParm.setData("REQUEST_IN_AMT", 0);
			// 41-REQUEST_OUT_QTY
        	ddstockParm.setData("REQUEST_OUT_QTY", 0);
			// 42-REQUEST_OUT_AMT
        	ddstockParm.setData("REQUEST_OUT_AMT", 0);
			// 43-GIF_IN_QTY
        	ddstockParm.setData("GIF_IN_QTY", 0);
			// 44-GIF_IN_AMT
        	ddstockParm.setData("GIF_IN_AMT", 0);
			// 45-GIF_OUT_QTY
        	ddstockParm.setData("GIF_OUT_QTY", 0);
			// 46-GIF_OUT_AMT
        	ddstockParm.setData("GIF_OUT_AMT", 0);
			// 47-RET_IN_QTY
        	ddstockParm.setData("RET_IN_QTY", 0);
			// 48-RET_IN_AMT
        	ddstockParm.setData("RET_IN_AMT", 0);
			// 49-RET_OUT_QTY
        	ddstockParm.setData("RET_OUT_QTY", 0);
			// 50-RET_OUT_AMT
        	ddstockParm.setData("RET_OUT_AMT", 0);
			// 51-WAS_OUT_QTY
        	ddstockParm.setData("WAS_OUT_QTY", 0);
			// 52-WAS_OUT_AMT
        	ddstockParm.setData("WAS_OUT_AMT", 0);
			// 53-THO_OUT_QTY
        	ddstockParm.setData("THO_OUT_QTY", 0);
			// 54-THO_OUT_AMT
        	ddstockParm.setData("THO_OUT_AMT", 0);
			// 55-THI_IN_QTY
        	ddstockParm.setData("THI_IN_QTY", 0);
			// 56-THI_IN_AMT
        	ddstockParm.setData("THI_IN_AMT", 0);
			// 57-COS_OUT_QTY
        	ddstockParm.setData("COS_OUT_QTY", 0);
			// 58-COS_OUT_AMT
        	ddstockParm.setData("COS_OUT_AMT", 0);
			// 59-SUP_CODE
        	ddstockParm.setData("SUP_CODE", ind_stock.getData("SUP_CODE", i));
        	ddstockParm.setData("INVENT_PRICE", ind_stock.getDouble("INVENT_PRICE", i));
        	// 60-SUP_ORDER_CODE
        	ddstockParm.setData("SUP_ORDER_CODE",ind_stock.getData("SUP_ORDER_CODE",i));
        	result = IndDDStockTool.getInstance().onDDInsert(ddstockParm, conn);
			if (result.getErrCode() < 0) {
				return result;				
			}
        }
        // 4.取得验收入库量及金额VERIFYIN_QTY,VERIFYIN_AMT //药库入
        result = setVerifyin(org_code, date,conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 5.取得拨补入库量及金额GIF_IN_QTY,GIF_IN_AMT //药库药房               入
        result = setDispenseIn( org_code, date, "GIF",conn);
        if (result == null) {
            return result;
        }
        // 6.取得退库入库量及金额RET_IN_QTY,RET_IN_AMT //药库入
        result = setDispenseIn( org_code, date, "RET",conn);
        if (result == null) {
            return result;
        }
        // 7.取得其他入库量及金额THI_IN_QTY,THI_IN_AMT //药库药房                入
        result = setDispenseIn( org_code, date, "THI",conn);
        if (result == null) {
            return result;
        }
        // 8.取得请领入库量及金额REQUEST_IN_QTY,REQUEST_IN_AMT //药房入
        result = setDispenseIn(org_code, date, "DEP",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 9.取得备药入库量及金额REQUEST_IN_QTY,REQUEST_IN_AMT //药房入
        result = setDispenseIn(org_code, date, "TEC",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 10.取得部门请领出库及金额  //药库出
        result = setDispenseOut(org_code, date,"DEP",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 11.取得备药出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT  //药库出
        result = setDispenseOut(org_code, date,"TEC",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 12.取得补充计费出库量及金额REQUEST_OUT_QTY,REQUEST_OUT_AMT  //药库出
        result = setDispenseOut(org_code, date,"EXM",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 13.取得拨补出库量及金额GIF_OUT_QTY,GIF_OUT_AMT  //药库出
        result = setDispenseOut(org_code, date,"GIF",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 14.取得耗损出库量及金额WAS_OUT_QTY,WAS_OUT_AMT  //药库出
        result = setDispenseOut(org_code, date,"WAS",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 15.取得卫耗材出库量及金额COS_OUT_QTY,COS_OUT_AMT  //药库出
        result = setDispenseOut(org_code, date,"COS",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 16.取得退货量及金额VERIFYIN_QTY,VERIFYIN_AMT  //药库出
        result = setRegerssgoods(org_code, date,conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 17.取得退库出库量及金额RET_OUT_QTY,RET_OUT_AMT  //药房出
        result = setDispenseOut(org_code, date,"RET",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 18.取得其他出库量及金额THO_OUT_QTY,THO_OUT_AMT //药库药房                  出
        result = setDispenseOut(org_code, date,"THO",conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 19.取得发药量及金额DOSAGE_QTY,DOSAGE_AMT  //药房出
        result = setDosage(org_code, date,conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        // 20.取得退药量及金额REGRESSDRUG_QTY,REGRESSDRUG_AMT //药房入
        result = setRegeessDrug(org_code, date,conn);
        if (result.getErrCode() < 0) {
			return result;
		}
        return ind_stock;
    }
    
    /**
     * 删除当天的IND_DDSTOCK数据
     * @param org_code String
     * @param date String
     */
    private TParm deleteIndDDstock(String org_code, String tran_date,TConnection conn) {
        String DELETE_IND_DDSTOCK_SQL =
            "DELETE FROM IND_DDSTOCK WHERE TRANDATE = '" + tran_date +
            "' AND ORG_CODE = '" + org_code + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().update(
            DELETE_IND_DDSTOCK_SQL));
        if (parm == null || parm.getErrCode() < 0) {
            System.out.println("部门："+org_code+", IND_DDSTOCK数据删除失败！！！");
            return parm;
        }
        conn.commit();
        return parm;  
    }
	
    /**
     * 取得当前IND_STOCK数据
     * @param org_code String
     */
    private TParm getIndDDStockHistory(String org_code,String lastdate) {
    	
    	String SELECT_IND_STOCK_SQL = "SELECT * FROM IND_DDSTOCK WHERE ORG_CODE='"+org_code+"'"
    	+" AND TRANDATE='"+lastdate+"' ORDER BY ORDER_CODE, VALID_DATE, BATCH_SEQ";
        TParm parm = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_STOCK_SQL));  
        return parm;
    }
    
    
    /**
     * 取得验收入库量及金额VERIFYIN_QTY,VERIFYIN_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TParm setVerifyin(String org_code,
                                   String date,TConnection conn) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String SELECT_IND_VERIFYIN_SQL =
            "SELECT A.ORDER_CODE, A.BATCH_NO, A.VALID_DATE,B.SUP_CODE,A.SUP_ORDER_CODE, " +
            "  CASE WHEN A.BILL_UNIT=C.STOCK_UNIT THEN A.VERIFYIN_QTY*C.DOSAGE_QTY ELSE A.VERIFYIN_QTY END AS QTY, " +
            "  ROUND((CASE WHEN A.BILL_UNIT=C.STOCK_UNIT THEN A.VERIFYIN_QTY*C.DOSAGE_QTY ELSE A.VERIFYIN_QTY END) * " +
            "  (CASE WHEN A.BILL_UNIT=C.STOCK_UNIT THEN ROUND(A.RETAIL_PRICE/C.DOSAGE_QTY,4) ELSE A.RETAIL_PRICE END),2) AS AMT," +
            "  CASE WHEN A.BILL_UNIT=C.STOCK_UNIT THEN ROUND(A.VERIFYIN_PRICE/C.DOSAGE_QTY,4) ELSE A.VERIFYIN_PRICE END AS VERIFYIN_PRICE, "+
            "  CASE WHEN A.BILL_UNIT=C.STOCK_UNIT THEN ROUND(A.RETAIL_PRICE/C.DOSAGE_QTY,4) ELSE A.RETAIL_PRICE END AS RETAIL_PRICE  " +
            " FROM IND_VERIFYIND A, IND_VERIFYINM B, PHA_TRANSUNIT C " +
            " WHERE A.VERIFYIN_NO = B.VERIFYIN_NO " +
            " AND A.ORDER_CODE = C.ORDER_CODE " +			
            " AND B.ORG_CODE = '" + org_code + "' " +
            " AND A.UPDATE_FLG = '3' "+
            " AND B.CHECK_DATE BETWEEN TO_DATE ('" + start_date +
            "', 'YYYYMMDDHH24MISS') AND TO_DATE ('" + end_date +
            "', 'YYYYMMDDHH24MISS')";
        TParm result = new TParm(TJDODBTool.getInstance().select(
            SELECT_IND_VERIFYIN_SQL));
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        double verifyin_price = 0;
        String sup_code = "";
        String sup_order_code = "";
        int batch_seq = 0;
        int batchSEQ=0;
        for (int i = 0; i < result.getCount("ORDER_CODE");i++) {
        	order_code = result.getValue("ORDER_CODE",i);
        	batch_no = result.getValue("BATCH_NO",i);
        	verifyin_price = result.getDouble("VERIFYIN_PRICE",i);
        	sup_code = result.getValue("SUP_CODE",i);
        	sup_order_code = result.getValue("SUP_ORDER_CODE",i);
        	valid_date = StringTool.getString(result.getTimestamp("VALID_DATE",i), "yyyyMMdd");
        	String sql = "SELECT CASE WHEN MIN(BATCH_SEQ) IS NULL THEN 0 ELSE MIN(BATCH_SEQ) END AS BATCH_SEQ FROM IND_DDSTOCK " +
    					 "WHERE TRANDATE = '"+date+"' " +
    					 "AND ORG_CODE = '"+org_code+"' " +
    					 "AND ORDER_CODE='"+order_code+"' " +
    					 "AND BATCH_NO='"+batch_no+"' " +
    					 "AND VALID_DATE=TO_DATE('"+valid_date+"','yyyy/MM/dd') " +
    					 "AND VERIFYIN_PRICE = "+verifyin_price+
    					 "AND SUP_CODE = '"+sup_code+"' " +
    					 "AND SUP_ORDER_CODE = '"+sup_order_code+"'";
        	TParm searchParm = new TParm(TJDODBTool.getInstance().select(
        			sql));
        	batch_seq = searchParm.getInt("BATCH_SEQ",0);
        	if(batch_seq != 0) {
        		sql = "UPDATE IND_DDSTOCK SET VERIFYIN_QTY=VERIFYIN_QTY+"+result.getDouble("QTY",i)+
        		",VERIFYIN_AMT=VERIFYIN_AMT+"+result.getDouble("AMT",i)+
        		",IN_QTY=IN_QTY+"+result.getDouble("QTY",i)+
        		",IN_AMT=IN_AMT+"+result.getDouble("AMT",i)+
        		",STOCKIN_QTY=STOCKIN_QTY+"+result.getDouble("QTY",i)+
        		",STOCKIN_AMT=STOCKIN_AMT+"+result.getDouble("AMT",i)+
        		",STOCK_QTY=STOCK_QTY+"+result.getDouble("QTY",i)+
        		",STOCK_AMT=STOCK_AMT+"+result.getDouble("AMT",i)+
        		" WHERE TRANDATE='"+date+"' " +
        				"AND ORG_CODE = '"+org_code+"' " +
        				"AND ORDER_CODE= '"+order_code+"' " +
        				"AND BATCH_SEQ = "+batch_seq;
        		IndDDStockTool.getInstance().updateVerifyin(sql, conn);
        	}else {
        		 String SELECT_BATCH_SEQ = "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM IND_DDSTOCK WHERE TRANDATE='"+date+"'" +
          		" AND ORG_CODE='"+org_code+"' AND ORDER_CODE='"+result.getValue("ORDER_CODE", i)+"'";
             	 TParm seqParm = new TParm(TJDODBTool.getInstance().select(
         		 SELECT_BATCH_SEQ));	
             		//找到当前的最大批次号加1 ，没有找到默认为1  
				 if(seqParm.getCount("BATCH_SEQ") > 0){
							// 最大+1批次序号
					batchSEQ = seqParm.getInt("BATCH_SEQ", 0) + 1;
				}else{
					batchSEQ = 1 ;				
				}			   
        		TParm insertParm = new TParm();
        		insertParm.setData("TRANDATE",date);
    			// 2-ORG_CODE
        		insertParm.setData("ORG_CODE", org_code);
    			// 3-ORDER_CODE
        		insertParm.setData("ORDER_CODE", result.getData("ORDER_CODE", i));
    			// 4-BATCH_SEQ	
        		insertParm.setData("BATCH_SEQ", batchSEQ);
    			// 5-BATCH_NO
        		insertParm.setData("BATCH_NO", result.getData("BATCH_NO", i));
    			// 6-VALID_DATE
        		insertParm.setData("VALID_DATE", result.getData("VALID_DATE", i));
    			// 7-REGION_CODE
        		insertParm.setData("REGION_CODE", "H01");
    			// 8-STOCK_QTY
        		insertParm.setData("STOCK_QTY", result.getDouble("QTY",i));
    			// 9-STOCK_AMT
        		insertParm.setData("STOCK_AMT", result.getDouble("AMT",i));
    			// 10-LAST_TOTSTOCK_QTY
        		insertParm.setData("LAST_TOTSTOCK_QTY", 0);
    			// 11-LAST_TOTSTOCK_AMT
        		insertParm.setData("LAST_TOTSTOCK_AMT", 0);
    			// 12-IN_QTY
        		insertParm.setData("IN_QTY", result.getDouble("QTY",i));
    			// 13-IN_AMT
        		insertParm.setData("IN_AMT", result.getDouble("AMT",i));
    			// 14-OUT_QTY
        		insertParm.setData("OUT_QTY", 0);
    			// 15-OUT_AMT
        		insertParm.setData("OUT_AMT", 0);
    			// 16-CHECKMODI_QTY
        		insertParm.setData("CHECKMODI_QTY", 0);
    			// 17-CHECKMODI_AMT
        		insertParm.setData("CHECKMODI_AMT", 0);
    			// 18-VERIFYIN_QTY
        		insertParm.setData("VERIFYIN_QTY", result.getDouble("QTY",i));
    			// 19-VERIFYIN_AMT
        		insertParm.setData("VERIFYIN_AMT", result.getDouble("AMT",i));
    			// 20-FAVOR_QTY
        		insertParm.setData("FAVOR_QTY", 0);
    			// 21-REGRESSGOODS_QTY
        		insertParm.setData("REGRESSGOODS_QTY", 0);
    			// 22-REGRESSGOODS_AMT
        		insertParm.setData("REGRESSGOODS_AMT", 0);
    			// 23-DOSAGE_QTY
        		insertParm.setData("DOSAGE_QTY", 0);
    			// 24-DOSAGE_AMT
        		insertParm.setData("DOSAGE_AMT", 0);
    			// 25-REGRESSDRUG_QTY
        		insertParm.setData("REGRESSDRUG_QTY", 0);
    			// 26-REGRESSDRUG_AMT
        		insertParm.setData("REGRESSDRUG_AMT", 0);
    			// 27-PROFIT_LOSS_AMT
        		insertParm.setData("PROFIT_LOSS_AMT", 0);
    			// 28-VERIFYIN_PRICE
        		insertParm.setData("VERIFYIN_PRICE",result.getDouble("VERIFYIN_PRICE",i));
    			// 29-STOCK_PRICE
        		insertParm.setData("STOCK_PRICE", 0);
    			// 30-RETAIL_PRICE
        		insertParm.setData("RETAIL_PRICE", result.getDouble("RETAIL_PRICE",i));
    			// 31-TRADE_PRICE
        		insertParm.setData("TRADE_PRICE", 0);
    			// 32-STOCKIN_QTY
        		insertParm.setData("STOCKIN_QTY", result.getDouble("QTY",i));
    			// 33-STOCKIN_AMT
        		insertParm.setData("STOCKIN_AMT", result.getDouble("AMT",i));
    			// 34-STOCKOUT_QTY
        		insertParm.setData("STOCKOUT_QTY", 0);
    			// 35-STOCKOUT_AMT
        		insertParm.setData("STOCKOUT_AMT", 0);
    			// 36-OPT_USER
        		insertParm.setData("OPT_USER", "PATCH_USER");
    			// 37-OPT_DATE   
        		insertParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
    			// 38-OPT_TERM
        		insertParm.setData("OPT_TERM", "PATCH_IP");
    			// 39-REQUEST_IN_QTY
        		insertParm.setData("REQUEST_IN_QTY", 0);
    			// 40-REQUEST_IN_AMT
        		insertParm.setData("REQUEST_IN_AMT", 0);
    			// 41-REQUEST_OUT_QTY
        		insertParm.setData("REQUEST_OUT_QTY", 0);
    			// 42-REQUEST_OUT_AMT
        		insertParm.setData("REQUEST_OUT_AMT", 0);
    			// 43-GIF_IN_QTY
        		insertParm.setData("GIF_IN_QTY", 0);
    			// 44-GIF_IN_AMT
        		insertParm.setData("GIF_IN_AMT", 0);
    			// 45-GIF_OUT_QTY
        		insertParm.setData("GIF_OUT_QTY", 0);
    			// 46-GIF_OUT_AMT
        		insertParm.setData("GIF_OUT_AMT", 0);
    			// 47-RET_IN_QTY
        		insertParm.setData("RET_IN_QTY", 0);
    			// 48-RET_IN_AMT
        		insertParm.setData("RET_IN_AMT", 0);
    			// 49-RET_OUT_QTY
        		insertParm.setData("RET_OUT_QTY", 0);
    			// 50-RET_OUT_AMT
        		insertParm.setData("RET_OUT_AMT", 0);
    			// 51-WAS_OUT_QTY
        		insertParm.setData("WAS_OUT_QTY", 0);
    			// 52-WAS_OUT_AMT
        		insertParm.setData("WAS_OUT_AMT", 0);
    			// 53-THO_OUT_QTY
        		insertParm.setData("THO_OUT_QTY", 0);
    			// 54-THO_OUT_AMT
        		insertParm.setData("THO_OUT_AMT", 0);
    			// 55-THI_IN_QTY
        		insertParm.setData("THI_IN_QTY", 0);
    			// 56-THI_IN_AMT
        		insertParm.setData("THI_IN_AMT", 0);
    			// 57-COS_OUT_QTY
        		insertParm.setData("COS_OUT_QTY", 0);
    			// 58-COS_OUT_AMT
        		insertParm.setData("COS_OUT_AMT", 0);
    			// 59-SUP_CODE
        		insertParm.setData("SUP_CODE", result.getData("SUP_CODE", i));
        		// 60-SUP_ORDER_CODE
        		insertParm.setData("SUP_ORDER_CODE", result.getData("SUP_ORDER_CODE", i));
        		insertParm.setData("INVENT_PRICE", 0);
            	TParm resultParm = IndDDStockTool.getInstance().onDDInsert(insertParm, conn);
    			if (resultParm.getErrCode() < 0) {
    				return resultParm;				
    			}
        	}						
        	   
        }
        return result;
    }
    
    /**
     * 取得退货出库量及金额REGRESSGOODS_QTY,REGRESSGOODS_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TParm setRegerssgoods(String org_code,
            	String date,TConnection conn) {
    	 String start_date = date + "000000";
         String end_date = date + "235959";
         String SELECT_IND_REGRESSGOODS_SQL =
             "SELECT A.ORDER_CODE, " +
             " CASE WHEN A.BILL_UNIT=C.STOCK_QTY THEN A.QTY*C.DOSAGE_QTY ELSE A.QTY END AS QTY, " +
             "ROUND((CASE WHEN A.BILL_UNIT=C.STOCK_QTY THEN A.QTY*C.DOSAGE_QTY ELSE A.QTY END) * " +
             "(CASE WHEN A.BILL_UNIT=C.STOCK_QTY THEN A.RETAIL_PRICE/C.DOSAGE_QTY ELSE A.RETAIL_PRICE END),2) AS AMT " +
             " FROM IND_REGRESSGOODSD A, IND_REGRESSGOODSM B, PHA_TRANSUNIT C " +
             " WHERE A.REGRESSGOODS_NO = B.REGRESSGOODS_NO " +
             " AND A.ORDER_CODE = C.ORDER_CODE " +
             " AND B.ORG_CODE = '" + org_code + "' " +
             " AND A.UPDATE_FLG IN ('1','3')"+
             " AND B.CHECK_DATE BETWEEN TO_DATE ('" + start_date +
             "', 'YYYYMMDDHH24MISS') " +
             " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS')";
         TParm result = new TParm(TJDODBTool.getInstance().select(
             SELECT_IND_REGRESSGOODS_SQL));
         double reg_qty = 0;
         double reg_amt = 0;
         double stock_qty = 0;
         double stock_amt = 0;
         double diff_qty = 0;
         double diff_amt = 0;
         String order_code = "";
         for(int i=0;i<result.getCount();i++) {
        	 order_code = result.getValue("ORDER_CODE",i);
        	 reg_qty = result.getDouble("QTY",i);
        	 reg_amt = result.getDouble("AMT",i);
        	 String sql = "SELECT VALID_DATE,STOCK_QTY,STOCK_AMT,BATCH_SEQ FROM IND_DDSTOCK "
 				+"WHERE TRANDATE='"+date+"' "
 				+"AND ORG_CODE = '"+org_code+"' "
 				+"AND ORDER_CODE = '"+order_code+"' "
 				+"ORDER BY VALID_DATE";
         	 TParm searchParm = new TParm(TJDODBTool.getInstance().select(
         			sql));
         	 for (int j = 0; j < searchParm.getCount("VALID_DATE"); j++) {
         		if(searchParm.getCount()>0) {
            		stock_qty = searchParm.getDouble("STOCK_QTY",j);
            		stock_amt = searchParm.getDouble("STOCK_AMT",j);
            		if (stock_qty > 0) {
	            		diff_qty = reg_qty - stock_qty;
	            		diff_amt = reg_amt - stock_amt;
	            		int batchSeq = searchParm.getInt("BATCH_SEQ",j);
            			if (diff_qty > 0 && searchParm.getCount("VALID_DATE")-1 > j) {
		             		sql = "UPDATE IND_DDSTOCK SET REGRESSGOODS_QTY=REGRESSGOODS_QTY+"+reg_qty+
		            		",REGRESSGOODS_AMT=REGRESSGOODS_AMT+"+reg_amt+
		            		",OUT_QTY=OUT_QTY+"+reg_qty+
		            		",OUT_AMT=OUT_AMT+"+reg_amt+
		            		",STOCKOUT_QTY=STOCKOUT_QTY+"+reg_qty+
		            		",STOCKOUT_AMT=STOCKOUT_AMT+"+reg_amt+
		            		",STOCK_QTY=0"+
		            		",STOCK_AMT=0"+
		            		" WHERE TRANDATE = '"+date+"' "
                            +"AND ORG_CODE = '"+org_code+"' "
                            +"AND ORDER_CODE='"+order_code+"' "
                            +"AND BATCH_SEQ ="+batchSeq;
		             		IndDDStockTool.getInstance().updateVerifyin(sql, conn);
		            		reg_qty = diff_qty;
		            		reg_amt = diff_amt;
            			} else {
            				sql = "UPDATE IND_DDSTOCK SET REGRESSGOODS_QTY=REGRESSGOODS_QTY+"+reg_qty+
		            		",REGRESSGOODS_AMT=REGRESSGOODS_AMT+"+reg_amt+
		            		",OUT_QTY=OUT_QTY+"+reg_qty+
		            		",OUT_AMT=OUT_AMT+"+reg_amt+
		            		",STOCKOUT_QTY=STOCKOUT_QTY+"+reg_qty+
		            		",STOCKOUT_AMT=STOCKOUT_AMT+"+reg_amt+
		            		",STOCK_QTY=STOCK_QTY-"+reg_qty+
		            		",STOCK_AMT=STOCK_AMT-"+reg_amt+
		            		" WHERE TRANDATE = '"+date+"' "
                            +"AND ORG_CODE = '"+org_code+"' "
                            +"AND ORDER_CODE='"+order_code+"' "
                            +"AND BATCH_SEQ ="+batchSeq;
            				IndDDStockTool.getInstance().updateVerifyin(sql, conn);
            				j = searchParm.getCount("VALID_DATE");
						}
            		}
             	 }
			 }
         }
    	return result;
    }
    
    /**
     * 取得出库量及金额OUT_QTY,OUT_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @param request_type String
     * @return TDataStore
     */
    private TParm setDispenseOut(String org_code,
                                   String date, String request_type,TConnection conn) {
    	 String start_date = date + "000000";
         String end_date = date + "235959";
         String org_where = "";
         if("RET".equals(request_type) || "WAS".equals(request_type) || "THO".equals(request_type)) {
             org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
         } else {
            org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
         }
         String SELECT_IND_DISPENSE_OUT_SQL =
             " SELECT A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,A.VERIFYIN_PRICE,A.SUP_CODE,A.SUP_ORDER_CODE, "+
             " CASE WHEN A.UNIT_CODE=C.STOCK_UNIT THEN A.ACTUAL_QTY*C.DOSAGE_QTY ELSE A.ACTUAL_QTY END AS QTY, "+
             "ROUND((CASE WHEN A.UNIT_CODE=C.STOCK_UNIT THEN A.ACTUAL_QTY*C.DOSAGE_QTY ELSE A.ACTUAL_QTY END) * A.RETAIL_PRICE,2) AS AMT " +
             " FROM IND_DISPENSED A, IND_DISPENSEM B, PHA_TRANSUNIT C " +
             " WHERE A.DISPENSE_NO = B.DISPENSE_NO " +			
             " AND A.ORDER_CODE = C.ORDER_CODE " + org_where +
             " AND B.DISPENSE_DATE BETWEEN TO_DATE ('" + start_date +
             "','YYYYMMDDHH24MISS') " +
             " AND TO_DATE('" + end_date + "', 'YYYYMMDDHH24MISS') " +
             " AND B.REQTYPE_CODE = '" + request_type + "'" +
             " AND B.UPDATE_FLG <> '2' ";
         TParm result = new TParm(TJDODBTool.getInstance().select(
             SELECT_IND_DISPENSE_OUT_SQL));
         String order_code = "";
         double stock_qty = 0;
         double stock_amt = 0;
         double diff_qty = 0;
         double diff_amt = 0;
         double out_qty = 0;
         double out_amt = 0;
         String update1 = "";
         String type = "";
         if ("DEP".equals(request_type) || "TEC".equals(request_type) ||
             "EXM".equals(request_type)) {
             type = "REQUEST_OUT_";
         }
         else if ("GIF".equals(request_type)) {
             type = "GIF_OUT_";
         }
         else if ("RET".equals(request_type)) {
             type = "RET_OUT_";
         }
         else if ("WAS".equals(request_type)) {
             type = "WAS_OUT_";
         }
         else if ("THO".equals(request_type)) {
             type = "THO_OUT_";
         }
         else if ("COS".equals(request_type)) {
             type = "COS_OUT_";
         }
         for(int i=0;i<result.getCount();i++) {
        	 out_qty = result.getDouble("QTY",i);
        	 out_amt = result.getDouble("AMT",i);
        	 order_code = result.getValue("ORDER_CODE",i);
         	 String sql = "SELECT VALID_DATE,STOCK_QTY,STOCK_AMT,BATCH_SEQ FROM IND_DDSTOCK "
				+"WHERE TRANDATE='"+date+"' "
				+"AND ORG_CODE = '"+org_code+"' "
				+"AND ORDER_CODE = '"+order_code+"' "
				+"ORDER BY VALID_DATE";
         	 TParm searchParm = new TParm(TJDODBTool.getInstance().select(							
         			sql));
         	 for(int j=0;j<searchParm.getCount("VALID_DATE");j++) {
         		stock_qty = searchParm.getDouble("STOCK_QTY",j);
         		stock_amt = searchParm.getDouble("STOCK_AMT",j);
         		if(stock_qty > 0 ) {
	        		diff_qty = out_qty - stock_qty;
	        		diff_amt = out_amt - stock_amt;
	        		int batchSeq = searchParm.getInt("BATCH_SEQ",j);
	    			if (diff_qty > 0 && searchParm.getCount("VALID_DATE")-1 > j) {
	         			update1 = "UPDATE IND_DDSTOCK SET "+type+"QTY="+type+"QTY+"+out_qty+
	            		","+type+"AMT="+type+"AMT+"+out_amt+
	            		",OUT_QTY=OUT_QTY+"+out_qty+    
	            		",OUT_AMT=OUT_AMT+"+out_amt+ 
	            		",STOCKOUT_QTY=STOCKOUT_QTY+"+out_qty+				
	            		",STOCKOUT_AMT=STOCKOUT_AMT+"+out_amt+
	            		",STOCK_QTY=0"+
	            		",STOCK_AMT=0"+
	            		" WHERE TRANDATE = '"+date+"' "
	                    +"AND ORG_CODE = '"+org_code+"' "
	                    +"AND ORDER_CODE='"+order_code+"' "
	                    +"AND BATCH_SEQ ="+batchSeq;
	         			IndDDStockTool.getInstance().updateVerifyin(update1, conn);	
	         			out_qty = diff_qty;
	         			out_amt = diff_amt;
	    			}else {
	    				update1 = "UPDATE IND_DDSTOCK SET "+type+"QTY="+type+"QTY+"+out_qty+
	            		","+type+"AMT="+type+"AMT+"+out_amt+
	            		",OUT_QTY=OUT_QTY+"+out_qty+    
	            		",OUT_AMT=OUT_AMT+"+out_amt+ 
	            		",STOCKOUT_QTY=STOCKOUT_QTY+"+out_qty+				
	            		",STOCKOUT_AMT=STOCKOUT_AMT+"+out_amt+
	            		",STOCK_QTY=STOCK_QTY-"+out_qty+
	            		",STOCK_AMT=STOCK_AMT-"+out_amt+
	            		" WHERE TRANDATE = '"+date+"' "
	                    +"AND ORG_CODE = '"+org_code+"' "
	                    +"AND ORDER_CODE='"+order_code+"' "
	                    +"AND BATCH_SEQ ="+batchSeq;
	    				IndDDStockTool.getInstance().updateVerifyin(update1, conn);	
	    				j = searchParm.getCount("VALID_DATE");
					}
	    						
	         	 }
         	 }
         }
         return result;
    }
    
    
    /**
     * 取得验收入库量及金额VERIFYIN_QTY,VERIFYIN_AMT
     * @param dataStore TDataStore
     * @param org_code String
     * @param date String
     * @return TDataStore
     */
    private TParm setDispenseIn(String org_code,
                                   String date,String request_type,TConnection conn) {
    	  String start_date = date + "000000";
          String end_date = date + "235959";
          String org_where = "";
          if("RET".equals(request_type)){
        	  org_where = " AND B.TO_ORG_CODE = '" + org_code + "' ";
          } else {
              org_where = " AND B.APP_ORG_CODE = '" + org_code + "' ";
          }
          String SELECT_IND_DISPENSE_IN_SQL =
              "SELECT A.ORDER_CODE,A.BATCH_NO,A.VALID_DATE,A.VERIFYIN_PRICE,A.SUP_CODE,A.SUP_ORDER_CODE, "+
              " CASE WHEN A.UNIT_CODE=C.STOCK_UNIT THEN A.ACTUAL_QTY*C.DOSAGE_QTY ELSE A.ACTUAL_QTY END AS QTY, "+
              "ROUND((CASE WHEN A.UNIT_CODE=C.STOCK_UNIT THEN A.ACTUAL_QTY*C.DOSAGE_QTY ELSE A.ACTUAL_QTY END) * A.RETAIL_PRICE,2) AS AMT " +
              " FROM IND_DISPENSED A, IND_DISPENSEM B, PHA_TRANSUNIT C " +
              " WHERE A.DISPENSE_NO = B.DISPENSE_NO " +
              " AND A.ORDER_CODE = C.ORDER_CODE " + org_where +
              " AND B.WAREHOUSING_DATE BETWEEN TO_DATE ('" + start_date +
              "', 'YYYYMMDDHH24MISS') " +
              " AND TO_DATE ('" + end_date + "', 'YYYYMMDDHH24MISS') " +
              " AND B.REQTYPE_CODE = '" + request_type + "'" +
              " AND B.UPDATE_FLG <> '2' ";
        TParm result = new TParm(TJDODBTool.getInstance().select(
        		SELECT_IND_DISPENSE_IN_SQL));		
        String order_code = "";
        String batch_no = "";
        String valid_date = "";
        double verifyin_price = 0;
        String sup_code = "";
        String sup_order_code = "";
        int batch_seq = 0;
        int batchSEQ=0;
        String type = "";
        if ("DEP".equals(request_type) || "TEC".equals(request_type) || "EXM".equals(request_type)) {
            type = "REQUEST_IN_";
        }
        else if ("GIF".equals(request_type)) {
            type = "GIF_IN_";
        }
        else if ("RET".equals(request_type)) {
            type = "RET_IN_";
        }
        else {
            type = "THI_IN_";
        }
        for (int i = 0; i < result.getCount("ORDER_CODE");i++) {
        	order_code = result.getValue("ORDER_CODE",i);
        	batch_no = result.getValue("BATCH_NO",i);
        	verifyin_price = result.getDouble("VERIFYIN_PRICE",i);
        	sup_code = result.getValue("SUP_CODE",i);
        	sup_order_code = result.getValue("SUP_ORDER_CODE",i);
        	valid_date = StringTool.getString(result.getTimestamp("VALID_DATE",i), "yyyyMMdd");
        	String sql = "SELECT CASE WHEN MIN(BATCH_SEQ) IS NULL THEN 0 ELSE MIN(BATCH_SEQ) END AS BATCH_SEQ FROM IND_DDSTOCK " +
			 "WHERE TRANDATE = '"+date+"' " +
			 "AND ORG_CODE = '"+org_code+"' " +
			 "AND ORDER_CODE='"+order_code+"' " +
			 "AND BATCH_NO='"+batch_no+"' " +
			 "AND VALID_DATE=TO_DATE('"+valid_date+"','yyyy/MM/dd') " +
			 "AND VERIFYIN_PRICE = "+verifyin_price+
			 "AND SUP_CODE = '"+sup_code+"' " +
			 "AND SUP_ORDER_CODE = '"+sup_order_code+"'";
        	TParm searchParm = new TParm(TJDODBTool.getInstance().select(
        			sql));
        	batch_seq = searchParm.getInt("BATCH_SEQ",0);
        	if(batch_seq != 0) {
        		sql = "UPDATE IND_DDSTOCK SET "+type+"QTY="+type+"QTY+"+result.getDouble("QTY",i)+
        		","+type+"AMT="+type+"AMT+"+result.getDouble("AMT",i)+
        		",IN_QTY=IN_QTY+"+result.getDouble("QTY",i)+
        		",IN_AMT=IN_AMT+"+result.getDouble("AMT",i)+
        		",STOCKIN_QTY=STOCKIN_QTY+"+result.getDouble("QTY",i)+
        		",STOCKIN_AMT=STOCKIN_AMT+"+result.getDouble("AMT",i)+
        		",STOCK_QTY=STOCK_QTY+"+result.getDouble("QTY",i)+
        		",STOCK_AMT=STOCK_AMT+"+result.getDouble("AMT",i)+
        		" WHERE TRANDATE='"+date+"' " +
				" AND ORG_CODE = '"+org_code+"' " +
				" AND ORDER_CODE='"+order_code+"' " +
				" AND BATCH_SEQ = "+batch_seq;
        		IndDDStockTool.getInstance().updateVerifyin(sql, conn);
        	}else {
        		 String SELECT_BATCH_SEQ = "SELECT MAX(BATCH_SEQ) AS BATCH_SEQ FROM IND_DDSTOCK WHERE TRANDATE='"+date+"'" +
          		" AND ORG_CODE='"+org_code+"' AND ORDER_CODE='"+result.getValue("ORDER_CODE", i)+"'";
             	 TParm seqParm = new TParm(TJDODBTool.getInstance().select(
         		 SELECT_BATCH_SEQ));	
             		//找到当前的最大批次号加1 ，没有找到默认为1  
				 if(seqParm.getCount("BATCH_SEQ") > 0){
							// 最大+1批次序号
					batchSEQ = seqParm.getInt("BATCH_SEQ", 0) + 1;
				}else{
					batchSEQ = 1 ;				
				}			   
        		TParm insertParm = new TParm();
        		insertParm.setData("TRANDATE",date);
    			// 2-ORG_CODE
        		insertParm.setData("ORG_CODE", org_code);
    			// 3-ORDER_CODE
        		insertParm.setData("ORDER_CODE", result.getData("ORDER_CODE", i));
    			// 4-BATCH_SEQ	
        		insertParm.setData("BATCH_SEQ", batchSEQ);
    			// 5-BATCH_NO
        		insertParm.setData("BATCH_NO", result.getData("BATCH_NO", i));
    			// 6-VALID_DATE
        		insertParm.setData("VALID_DATE", result.getData("VALID_DATE", i));
    			// 7-REGION_CODE
        		insertParm.setData("REGION_CODE", "H01");
    			// 8-STOCK_QTY
        		insertParm.setData("STOCK_QTY", result.getDouble("QTY",i));
    			// 9-STOCK_AMT
        		insertParm.setData("STOCK_AMT", result.getDouble("AMT",i));
    			// 10-LAST_TOTSTOCK_QTY
        		insertParm.setData("LAST_TOTSTOCK_QTY", 0);
    			// 11-LAST_TOTSTOCK_AMT
        		insertParm.setData("LAST_TOTSTOCK_AMT", 0);
    			// 12-IN_QTY
        		insertParm.setData("IN_QTY", result.getDouble("QTY",i));
    			// 13-IN_AMT
        		insertParm.setData("IN_AMT", result.getDouble("AMT",i));
    			// 14-OUT_QTY
        		insertParm.setData("OUT_QTY", 0);
    			// 15-OUT_AMT
        		insertParm.setData("OUT_AMT", 0);
    			// 16-CHECKMODI_QTY
        		insertParm.setData("CHECKMODI_QTY", 0);
    			// 17-CHECKMODI_AMT
        		insertParm.setData("CHECKMODI_AMT", 0);
    			// 18-VERIFYIN_QTY
        		insertParm.setData("VERIFYIN_QTY", 0);
    			// 19-VERIFYIN_AMT
        		insertParm.setData("VERIFYIN_AMT", 0);
    			// 20-FAVOR_QTY
        		insertParm.setData("FAVOR_QTY", 0);
    			// 21-REGRESSGOODS_QTY
        		insertParm.setData("REGRESSGOODS_QTY", 0);
    			// 22-REGRESSGOODS_AMT
        		insertParm.setData("REGRESSGOODS_AMT", 0);
    			// 23-DOSAGE_QTY
        		insertParm.setData("DOSAGE_QTY", 0);
    			// 24-DOSAGE_AMT
        		insertParm.setData("DOSAGE_AMT", 0);
    			// 25-REGRESSDRUG_QTY
        		insertParm.setData("REGRESSDRUG_QTY", 0);
    			// 26-REGRESSDRUG_AMT
        		insertParm.setData("REGRESSDRUG_AMT", 0);
    			// 27-PROFIT_LOSS_AMT
        		insertParm.setData("PROFIT_LOSS_AMT", 0);
    			// 28-VERIFYIN_PRICE
        		insertParm.setData("VERIFYIN_PRICE",result.getDouble("VERIFYIN_PRICE",i));
    			// 29-STOCK_PRICE
        		insertParm.setData("STOCK_PRICE", 0);
    			// 30-RETAIL_PRICE
        		insertParm.setData("RETAIL_PRICE", result.getDouble("RETAIL_PRICE",i));
    			// 31-TRADE_PRICE
        		insertParm.setData("TRADE_PRICE", 0);
    			// 32-STOCKIN_QTY
        		insertParm.setData("STOCKIN_QTY", result.getDouble("QTY",i));
    			// 33-STOCKIN_AMT
        		insertParm.setData("STOCKIN_AMT", result.getDouble("AMT",i));
    			// 34-STOCKOUT_QTY
        		insertParm.setData("STOCKOUT_QTY", 0);
    			// 35-STOCKOUT_AMT
        		insertParm.setData("STOCKOUT_AMT", 0);
    			// 36-OPT_USER
        		insertParm.setData("OPT_USER", "PATCH_USER");
    			// 37-OPT_DATE   
        		insertParm.setData("OPT_DATE", SystemTool.getInstance().getDate());
    			// 38-OPT_TERM
        		insertParm.setData("OPT_TERM", "PATCH_IP");
    			// 39-REQUEST_IN_QTY
        		if ("DEP".equals(request_type) || "TEC".equals(request_type) || "EXM".equals(request_type)) {
        			 insertParm.setData("REQUEST_IN_QTY", result.getDouble("QTY",i));
         			// 40-REQUEST_IN_AMT
             		 insertParm.setData("REQUEST_IN_AMT", result.getDouble("AMT",i));
        	    }else {
	        		insertParm.setData("REQUEST_IN_QTY", 0);
	    			// 40-REQUEST_IN_AMT
	        		insertParm.setData("REQUEST_IN_AMT", 0);
        	    }
    			// 41-REQUEST_OUT_QTY
        		insertParm.setData("REQUEST_OUT_QTY", 0);
    			// 42-REQUEST_OUT_AMT
        		insertParm.setData("REQUEST_OUT_AMT", 0);
    			// 43-GIF_IN_QTY
        	    if ("GIF".equals(request_type)) {
	        		insertParm.setData("GIF_IN_QTY", result.getDouble("QTY",i));
	    			// 44-GIF_IN_AMT
	        		insertParm.setData("GIF_IN_AMT", result.getDouble("AMT",i));
        		 }else {
        			 insertParm.setData("GIF_IN_QTY", 0);
 	    			// 44-GIF_IN_AMT
 	        		insertParm.setData("GIF_IN_AMT", 0);
        		 }
    			// 45-GIF_OUT_QTY
        		insertParm.setData("GIF_OUT_QTY", 0);
    			// 46-GIF_OUT_AMT
        		insertParm.setData("GIF_OUT_AMT", 0);
    			// 47-RET_IN_QTY
        		if ("RET".equals(request_type)) {
	        		insertParm.setData("RET_IN_QTY", result.getDouble("QTY",i));
	    			// 48-RET_IN_AMT
	        		insertParm.setData("RET_IN_AMT", result.getDouble("AMT",i));
        		}else {
        			insertParm.setData("RET_IN_QTY", 0);
	    			// 48-RET_IN_AMT
	        		insertParm.setData("RET_IN_AMT", 0);
        		}
    			// 49-RET_OUT_QTY
        		insertParm.setData("RET_OUT_QTY", 0);
    			// 50-RET_OUT_AMT
        		insertParm.setData("RET_OUT_AMT", 0);
    			// 51-WAS_OUT_QTY
        		insertParm.setData("WAS_OUT_QTY", 0);
    			// 52-WAS_OUT_AMT
        		insertParm.setData("WAS_OUT_AMT", 0);
    			// 53-THO_OUT_QTY
        		insertParm.setData("THO_OUT_QTY", 0);
    			// 54-THO_OUT_AMT
        		insertParm.setData("THO_OUT_AMT", 0);
        		if ("THI".equals(request_type)) {
	        		insertParm.setData("THI_IN_QTY", result.getDouble("QTY",i));
	        		// 55-THI_IN_QTY
	        		insertParm.setData("THI_IN_AMT", result.getDouble("AMT",i));
        		}else {
        			insertParm.setData("THI_IN_QTY", 0);
        			// 56-THI_IN_AMT
	        		insertParm.setData("THI_IN_AMT", 0);
        		}
    			// 57-COS_OUT_QTY
        		insertParm.setData("COS_OUT_QTY", 0);
    			// 58-COS_OUT_AMT
        		insertParm.setData("COS_OUT_AMT", 0);
    			// 59-SUP_CODE
        		insertParm.setData("SUP_CODE", result.getData("SUP_CODE", i));
        		// 60-SUP_ORDER_CODE
        		insertParm.setData("SUP_ORDER_CODE", result.getData("SUP_ORDER_CODE", i));
        		insertParm.setData("INVENT_PRICE", 0);
            	TParm resultParm = IndDDStockTool.getInstance().onDDInsert(insertParm, conn);
    			if (resultParm.getErrCode() < 0) {
    				return resultParm;				
    			}
        	}						
        	   
        }
        return result;
    }
    
    /**
     * 取得退药量及金额REGRESSDRUG_QTY,REGRESSDRUG_AMT
     * @param org_code
     * @param date
     * @param conn
     * @return
     */
    private TParm setRegeessDrug(String org_code,String date,TConnection conn) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String order_code = "";
        String SELECT_REGEESS_SQL = "(SELECT A.ORDER_CODE,A.BATCH_SEQ1,A.BATCH_SEQ2,A.BATCH_SEQ3, "
				+"SUM(A.RTN_DOSAGE_QTY) AS QTY , ROUND(SUM(A.RTN_DOSAGE_QTY)*A.OWN_PRICE,2) AS AMT "
				+"FROM ODI_DSPNM A "
				+"WHERE A.EXEC_DEPT_CODE = '"+org_code+"' "
				+"AND A.DSPN_KIND = 'RT' "
				+"AND A.CAT1_TYPE='PHA' "
				+"AND A.DSPN_DATE BETWEEN TO_DATE ('"+start_date+"', 'YYYYMMDDHH24MISS') "
				+"AND TO_DATE ('"+end_date+"', 'YYYYMMDDHH24MISS') "
				+"GROUP BY A.ORDER_CODE,A.BATCH_SEQ1,A.BATCH_SEQ2,A.BATCH_SEQ3,A.OWN_PRICE) "
				+"UNION ALL "
				+"(SELECT B.ORDER_CODE,B.BATCH_SEQ1,B.BATCH_SEQ2,B.BATCH_SEQ3, "
				+"SUM(B.DOSAGE_QTY) AS QTY ,ROUND(SUM(B.DOSAGE_QTY)*B.OWN_PRICE,2) AS AMT "
				+"FROM OPD_ORDER B "
				+"WHERE B.EXEC_DEPT_CODE = '"+org_code+"' "
				+"AND B.CAT1_TYPE='PHA' "
				+"AND B.PHA_RETN_DATE BETWEEN TO_DATE('"+start_date+"','YYYYMMDDHH24MISS') "
				+"AND TO_DATE('"+end_date+"','YYYYMMDDHH24MISS') "
				+"GROUP BY B.ORDER_CODE,B.BATCH_SEQ1,B.BATCH_SEQ2,B.BATCH_SEQ3,B.OWN_PRICE)";
        TParm parm = new TParm(TJDODBTool.getInstance().select(SELECT_REGEESS_SQL));
        double ret_qty = 0;
        double ret_amt = 0;
        int batch_seq = 0;
        int batch_seq1 = 0;
        int batch_seq2 = 0;
        int batch_seq3 = 0;
        String sql = "";
        TParm batchSeqParm = null;
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
        	order_code = parm.getValue("ORDER_CODE", i);
            ret_qty = parm.getDouble("QTY", i);
            ret_amt = parm.getDouble("AMT",i);
            batch_seq1 = parm.getInt("BATCH_SEQ1");
            batch_seq2 = parm.getInt("BATCH_SEQ2");
            batch_seq3 = parm.getInt("BATCH_SEQ3");
            sql = "SELECT BATCH_SEQ FROM IND_DDSTOCK "
				+"WHERE ORG_CODE = '"+org_code+"' " 
				+"AND ORDER_CODE='"+order_code+"' "
				+"AND (BATCH_SEQ="+batch_seq1+" OR BATCH_SEQ="+batch_seq2+" OR BATCH_SEQ="+batch_seq3+") "
				+"AND TRANDATE = '"+date+"'";
            batchSeqParm = new TParm(TJDODBTool.getInstance().select(sql));
            batch_seq = batchSeqParm.getInt("BATCH_SEQ",0);
            if (batchSeqParm.getCount() <= 0) {
				sql = "SELECT MIN(BATCH_SEQ) AS BATCH_SEQ FROM IND_DDSTOCK "
					+"WHERE ORG_CODE = '"+org_code+"' "
					+"AND ORDER_CODE='"+order_code+"' "
					+"AND TRANDATE = '"+date+"'";
				batchSeqParm = new TParm(TJDODBTool.getInstance().select(sql));
				batch_seq = batchSeqParm.getInt("BATCH_SEQ",0);
			}
            if (batch_seq >= 1) {
            	sql = "UPDATE IND_DDSTOCK SET " 
                	+"STOCK_QTY = STOCK_QTY+"+ret_qty+"," 
                	+"STOCK_AMT = STOCK_AMT+"+ret_amt+","
                    +"IN_QTY = IN_QTY+"+ret_qty+"," 
                    +"IN_AMT = IN_AMT+"+ret_amt+","
                    +"STOCKIN_QTY = STOCKIN_QTY+"+ret_qty+"," 
                    +"STOCKIN_AMT = STOCKIN_AMT+"+ret_amt+","
                    +"REGRESSDRUG_QTY = REGRESSDRUG_QTY+"+ret_qty+"," 
                    +"REGRESSDRUG_AMT = REGRESSDRUG_AMT+"+ret_amt+" "
    				+"WHERE TRANDATE = '"+date+"' "
    				+"AND ORG_CODE = '"+org_code+"' "
    				+"AND ORDER_CODE='"+order_code+"' "
    				+"AND BATCH_SEQ = "+batch_seq+"";
            	IndDDStockTool.getInstance().updateVerifyin(sql, conn);
			}
        }
        return parm;
    }
    
    /**
     * 取得发药量及金额DOSAGE_QTY,DOSAGE_AMT
     * @param org_code
     * @param date
     * @param conn
     * @return
     */
    private TParm setDosage(String org_code,String date,TConnection conn) {
        String start_date = date + "000000";
        String end_date = date + "235959";
        String order_code = "";
        String SELECT_DOSAGE_SQL = "SELECT A.ORDER_CODE, "
				+"SUM(A.DOSAGE_QTY) AS QTY, "
				+"ROUND(SUM(A.DOSAGE_QTY)*A.OWN_PRICE,2) AS AMT "
				+"FROM OPD_ORDER A "
				+"WHERE A.EXEC_DEPT_CODE = '"+org_code+"' "
				+"AND A.CAT1_TYPE='PHA' "
				+"AND A.PHA_RETN_DATE IS NULL "
				+"AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('"+start_date+"', 'YYYYMMDDHH24MISS') "
				+"AND TO_DATE ('"+end_date+"', 'YYYYMMDDHH24MISS') "
				+"GROUP BY A.ORDER_CODE,A.OWN_PRICE "
				+"UNION ALL "
				+"SELECT A.ORDER_CODE,  "
				+"SUM(A.DOSAGE_QTY) AS QTY, "
				+"ROUND(SUM(A.DOSAGE_QTY)*A.OWN_PRICE,2) AS AMT "
				+"FROM ODI_DSPNM A "
				+"WHERE A.EXEC_DEPT_CODE = '"+org_code+"' "
				+"AND A.CAT1_TYPE='PHA' "
				+"AND A.DSPN_KIND <> 'RT' "
				+"AND A.PHA_DOSAGE_DATE BETWEEN TO_DATE ('"+start_date+"','YYYYMMDDHH24MISS' ) "
				+"AND TO_DATE ('"+end_date+"', 'YYYYMMDDHH24MISS' ) "
				+"GROUP BY A.ORDER_CODE,A.OWN_PRICE";
        TParm parm = new TParm(TJDODBTool.getInstance().select(SELECT_DOSAGE_SQL));
        double dosage_qty = 0;
        double dosage_amt = 0;
        double stock_qty = 0;
        double stock_amt = 0;
        double diff_qty = 0;
        double diff_amt = 0;
        String sql = "";
        TParm validParm = null;
        for (int i = 0; i < parm.getCount("ORDER_CODE"); i++) {
        	order_code = parm.getValue("ORDER_CODE", i);
        	dosage_qty = parm.getDouble("QTY", i);
        	dosage_amt = parm.getDouble("AMT", i);
        	sql = "SELECT VALID_DATE,STOCK_QTY,STOCK_AMT,RETAIL_PRICE,BATCH_SEQ FROM IND_DDSTOCK "
				+"WHERE TRANDATE='"+date+"' "
				+"AND ORG_CODE = '"+org_code+"' "
				+"AND ORDER_CODE = '"+order_code+"' "
				+"ORDER BY VALID_DATE";
        	validParm = new TParm(TJDODBTool.getInstance().select(sql));
        	for (int j = 0; j < validParm.getCount("VALID_DATE"); j++) {
        		stock_qty = validParm.getDouble("STOCK_QTY",j);
        		stock_amt = validParm.getDouble("STOCK_AMT",j);
        		if(stock_qty > 0 ) {
	        		diff_qty = dosage_qty - stock_qty;
	        		diff_amt = dosage_amt - stock_amt;
	        		int batchSeq = validParm.getInt("BATCH_SEQ",j);
	    			if (diff_qty > 0 && validParm.getCount("VALID_DATE")-1 > j) {
	    				sql = "UPDATE IND_DDSTOCK SET " 
	                 	    +"STOCK_QTY = 0,"
	                        +"STOCK_AMT = 0,"
	                        +"OUT_QTY = OUT_QTY+"+dosage_qty+","
	                        +"OUT_AMT = OUT_AMT+"+dosage_amt+","
	                        +"STOCKOUT_QTY = STOCKOUT_QTY+"+dosage_qty+","
	                        +"STOCKOUT_AMT = STOCKOUT_AMT+"+dosage_amt+","
	                        +"DOSAGE_QTY = DOSAGE_QTY+"+dosage_qty+","
	                        +"DOSAGE_AMT = DOSAGE_AMT+"+dosage_amt+" "
	                        +"WHERE TRANDATE = '"+date+"' "
	                        +"AND ORG_CODE = '"+org_code+"' "
	                        +"AND ORDER_CODE='"+order_code+"' "
	                        +"AND BATCH_SEQ ="+batchSeq;
		    			IndDDStockTool.getInstance().updateVerifyin(sql, conn);
	    				dosage_qty = diff_qty;
	    				dosage_amt = diff_amt;
	    			}else {
	    				sql = "UPDATE IND_DDSTOCK SET " 
	                 	    +"STOCK_QTY = STOCK_QTY-"+dosage_qty+","
	                        +"STOCK_AMT = STOCK_AMT-"+dosage_amt+","
	                        +"OUT_QTY = OUT_QTY+"+dosage_qty+","
	                        +"OUT_AMT = OUT_AMT+"+dosage_amt+","
	                        +"STOCKOUT_QTY = STOCKOUT_QTY+"+dosage_qty+","
	                        +"STOCKOUT_AMT = STOCKOUT_AMT+"+dosage_amt+","
	                        +"DOSAGE_QTY = DOSAGE_QTY+"+dosage_qty+","
	                        +"DOSAGE_AMT = DOSAGE_AMT+"+dosage_amt+" "
	                        +"WHERE TRANDATE = '"+date+"' "
	                        +"AND ORG_CODE = '"+org_code+"' "
	                        +"AND ORDER_CODE='"+order_code+"' "
	                        +"AND BATCH_SEQ ="+batchSeq;
	    				IndDDStockTool.getInstance().updateVerifyin(sql, conn);
	    				j = validParm.getCount("VALID_DATE");
					}
        		}
			}
		}
        return parm;
    }
    
}
