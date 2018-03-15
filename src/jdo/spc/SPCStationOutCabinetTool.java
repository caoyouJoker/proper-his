package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title:临时医嘱/首日量-病区普药及麻精出智能柜 </p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCStationOutCabinetTool extends TJDOTool {
	
	/**
     * 实例
     */
    public static SPCStationOutCabinetTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCStationOutCabinetTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCStationOutCabinetTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCStationOutCabinetTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    /**
     * 临时医嘱/首日量-病区普药及麻精出智能柜 【普药】
     * @param parm
     * @return
     */
    public TParm onQueryD(TParm parm){
    	
    	String takemedNo = parm.getValue("TAKEMED_NO");
    	if(StringUtil.isNullString(takemedNo )){
    		return new TParm();
    	}
    	
    	String sql = " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.MR_NO, " +
    				 "        A.ORDER_DESC||A.SPECIFICATION AS ORDER_DESC,A.SPECIFICATION,D.PAT_NAME,A.ACUM_OUTBOUND_QTY," +
    				 "        A.DOSAGE_QTY,TO_CHAR (A.MEDI_QTY, 'FM9999990.099') || '' || E.UNIT_CHN_DESC  AS MEDI_QTY," +
    				 "		  F.ROUTE_CHN_DESC,G.FREQ_CHN_DESC,A.TAKE_DAYS, "+
    				 "        TO_CHAR (A.DISPENSE_QTY, 'FM9999990.099') AS DISPENSE_QTY, " +
    				 "        H.UNIT_CHN_DESC AS DISPENSE_UNIT,A.STATION_CODE,A.ORDER_CODE,I.BED_NO_DESC  " + 
    				 " FROM IND_CABDSPN A, "+
			         "		SYS_CTRLDRUGCLASS C, "+
			         "		SYS_PATINFO D,"+
			         "		SYS_UNIT E,"+
			         "		SYS_PHAROUTE F,"+
			         "		SYS_PHAFREQ G,"+
			         "		SYS_UNIT H,"+
			         "		SYS_BED I "+
			         "WHERE C.CTRLDRUGCLASS_CODE(+) = A.CTRLDRUGCLASS_CODE "+
				     "		AND (C.CTRL_FLG IS NULL OR C.CTRL_FLG <> 'Y') "+
				     "		AND D.MR_NO = A.MR_NO "+
				     "		AND E.UNIT_CODE(+)=A.MEDI_UNIT "+
				     "		AND F.ROUTE_CODE(+) = A.ROUTE_CODE "+
				     "		AND G.FREQ_CODE(+) = A.FREQ_CODE "+
				     "		AND H.UNIT_CODE(+)=A.DISPENSE_UNIT "+
				     "		AND I.BED_NO =A.BED_NO "+
    				 "      AND A.TAKEMED_NO = '"+takemedNo+"'" ;
    	//System.out.println("sql==============:"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 临时医嘱/首日量-病区普药及麻精出智能柜 【麻精】
     * @param parm
     * @return
     */
    public TParm onQueryM(TParm parm){
    	
    	String takemedNo = parm.getValue("TAKEMED_NO");
    	if(StringUtil.isNullString(takemedNo )){
    		return new TParm();
    	}
    	
    	String sql = " SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.MR_NO, " +
					 "        A.ORDER_DESC,A.SPECIFICATION,D.PAT_NAME,A.ACUM_OUTBOUND_QTY," +
					 "        A.DOSAGE_QTY,TO_CHAR (A.MEDI_QTY, 'FM9999990.099') || '' || E.UNIT_CHN_DESC  AS MEDI_QTY," +
					 "		  F.ROUTE_CHN_DESC,G.FREQ_CHN_DESC,A.TAKE_DAYS, "+
					 "        TO_CHAR (A.DISPENSE_QTY, 'FM9999990.099') AS DISPENSE_QTY, " +
					 "        H.UNIT_CHN_DESC AS DISPENSE_UNIT,A.STATION_CODE,A.ORDER_CODE,I.BED_NO_DESC  " + 
					 " FROM IND_CABDSPN A, "+
			         "		SYS_CTRLDRUGCLASS C, "+
			         "		SYS_PATINFO D,"+
			         "		SYS_UNIT E,"+
			         "		SYS_PHAROUTE F,"+
			         "		SYS_PHAFREQ G,"+
			         "		SYS_UNIT H,"+
			         "		SYS_BED I "+
			         "WHERE C.CTRLDRUGCLASS_CODE(+) = A.CTRLDRUGCLASS_CODE  "+
				     "		AND  C.CTRL_FLG = 'Y'  "+
				     "		AND D.MR_NO = A.MR_NO "+
				     "		AND E.UNIT_CODE(+)=A.MEDI_UNIT "+
				     "		AND F.ROUTE_CODE(+) = A.ROUTE_CODE "+
				     "		AND G.FREQ_CODE(+) = A.FREQ_CODE "+
				     "		AND H.UNIT_CODE(+)=A.DISPENSE_UNIT "+
				     "		AND I.BED_NO =A.BED_NO " + 
    				 "      AND A.TAKEMED_NO = '"+takemedNo+"' ";
    	//System.out.println("sql==============:"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    

    /**
     * 更新普药智能柜库存
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateIndCbnstock(TParm parm,TConnection conn ){
    	TParm orderParm = parm.getParm("OUT_ORDER");
    	TParm result = new TParm();
    	String cabinetId = parm.getValue("CABINET_ID");
    	String orderCode = "" ;
    	double actual_qty = 0  ;
    	
    	for(int  i = 0 ; i < orderParm.getCount("ORDER_CODE"); i++ ){
    		
    		// 实际出库数量
			double qty_out = 0;
			orderCode = orderParm.getValue("ORDER_CODE", i);
			actual_qty = orderParm.getDouble("ACTUAL_QTY", i);
			
			parm.setData("ORDER_CODE",orderCode);
			TParm returnParm = onQueryCabinet(parm);
	    	for (int j = 0; j < returnParm.getCount(); j++) {
	    		
	    		//每批次的数量
				double qty = returnParm.getDouble("STOCK_QTY", j);
				int batchSeq = returnParm.getInt("BATCH_SEQ", j);
				if (qty >= actual_qty) {
					// 药品可以一次扣库
					result = updateIndCabinet(cabinetId, orderCode, batchSeq, actual_qty, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
					break;

				}else {
					// 药品不可一次扣库
					// 更新出库量
					result = updateIndCabinet(cabinetId, orderCode, batchSeq, qty, conn);
					if (result.getErrCode() < 0) {
						return result;
					}
					// 更新出库量
					actual_qty = actual_qty - qty;
					// 更新实际出库数量
					qty_out = qty_out + qty;
					
				}
	    	}	    	 
    	}
    	return result ;
    }
    
    /**
     * 更新智能柜库存
     * @param cabinetId
     * @param orderCode
     * @param batchSeq
     * @param qty
     * @param conn
     * @return
     */
    private TParm updateIndCabinet(String cabinetId,String orderCode,int batchSeq,double qty,TConnection conn){
    	String sql = " UPDATE IND_CBNSTOCK SET STOCK_QTY=STOCK_QTY-"+qty+"  " +
	     			 " WHERE CABINET_ID='"+cabinetId+"' AND ORDER_CODE='"+orderCode+"' AND BATCH_SEQ="+batchSeq ;
    	
    	//System.out.println("updateIndCabinet---:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql,conn));
    }
    
    //更新智能柜库存 加以前库存
    public TParm updateIndCabinetAdd(TParm parm ,TConnection conn){
    	
    	double qty = parm.getDouble("QTY");
    	String orderCode = parm.getValue("ORDER_CODE");
    	int batchSeq = parm.getInt("BATCH_SEQ");
    	String cabinetId = parm.getValue("CABINET_ID");
    	
    	String sql = " UPDATE IND_CBNSTOCK SET STOCK_QTY=STOCK_QTY +"+qty+"  " +
		 " WHERE CABINET_ID='"+cabinetId+"' AND ORDER_CODE='"+orderCode+"' AND BATCH_SEQ='"+batchSeq+"' " ;
    	return new TParm(TJDODBTool.getInstance().update(sql,conn));
    }
    
    public TParm insertIndCabinet(TParm parm ,TConnection conn){
    	String sql = " INSERT INTO IND_CBNSTOCK (  "+
    				 "  CABINET_ID, ORDER_CODE, BATCH_SEQ, BATCH_NO, VALID_DATE,  "+ 
    				 "  VERIFYIN_PRICE, STOCK_QTY, STOCK_UNIT, OPT_USER, OPT_DATE,  "+ 
    				 "  OPT_TERM ) "+
    				 " Values ( "+
                     " '"+parm.getValue("CABINET_ID")+"', '"+parm.getValue("ORDER_CODE")+"', "+parm.getInt("BATCH_SEQ")+", '"+parm.getValue("BATCH_NO")+"', "+parm.getTimestamp("VALID_DATE")+",  "+
    				 " "+parm.getDouble("VERIFYIN_PRICE")+", "+parm.getDouble("STOCK_QTY")+", '"+parm.getValue("STOCK_UNIT")+"', '"+parm.getValue("OPT_USER")+"', "+parm.getTimestamp("OPT_DATE")+"   "+
    				 " '"+parm.getValue("OPT_TERM")+"') ";
    	
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }
    
    /**
     * 根据智能柜ID，orderCode查询所有的库存
     * @param parm
     * @return
     */
    public TParm onQueryCabinet(TParm parm ) {
    	String cabinetId = parm.getValue("CABINET_ID") ;
    	String orderCode = parm.getValue("ORDER_CODE") ;
    	if(cabinetId == null || cabinetId.equals("") || 
    			orderCode == null || orderCode.equals("") ){
    		return new TParm();
    	}
    	String sql = "SELECT A.CABINET_ID, A.ORDER_CODE, A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE,  "+ 
    				 "	     A.VERIFYIN_PRICE,A.STOCK_QTY, A.STOCK_UNIT "+
    				 "FROM IND_CBNSTOCK A "+
    				 "WHERE A.CABINET_ID='"+cabinetId+"' AND A.ORDER_CODE='"+orderCode+"'  " +
    				 "     AND  A.STOCK_QTY >  0  ";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    public TParm onQueryCabinetStock(TParm parm){
    	String cabinetId = parm.getValue("CABINET_ID") ;
    	String orderCode = parm.getValue("ORDER_CODE") ;
    	if(cabinetId == null || cabinetId.equals("") || 
    			orderCode == null || orderCode.equals("") ){
    		return new TParm();
    	}
    	String sql = " SELECT  SUM(A.STOCK_QTY) STOCK_QTY, A.ORDER_CODE   "+
    				 " FROM IND_CBNSTOCK A "+
    				 " WHERE A.CABINET_ID='"+cabinetId+"' AND A.ORDER_CODE='"+orderCode+"'  " +
    				 "       AND  A.STOCK_QTY >  0     "+
    				 "       GROUP BY A.ORDER_CODE  ";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 临时医嘱/首日量-病区普药及麻精出智能柜 更新库存
     * @param parm
     * @param conn
     * @return
     */
    public TParm onUpdateIndStock(TParm parm ,TConnection conn){
    	 
    	// 入库信息
		TParm stock_in = new TParm();
		TParm result = new TParm();
		int seq = 1;
		double actual_qty = 0;
		String batch_no = "";
		String valid_date = "";
		
		String order_code ;
		String out_org_code;
		
    	for(int  i = 0 ; i < parm.getCount("ORDER_CODE"); i++ ){
    		
    		// 实际出库数量
			double qty_out = 0;
			order_code = parm.getValue("ORDER_CODE", i);
			out_org_code = parm.getValue("OUT_ORG_CODE",i);
			actual_qty = parm.getDouble("ACTUAL_QTY", i);
			
			//System.out.println("order_code===:"+order_code+"out_org_code----:"+out_org_code+"actual_qty----:"+actual_qty);
			 
    		// 不指定批号和效期

			// 根据药库编号及药品代码查询药品的批次序号、库存和零售价
			TParm stock_parm = IndStockDTool.getInstance().onQueryStockBatchAndQty(out_org_code, order_code, "");
			for (int j = 0; j < stock_parm.getCount(); j++) {
				double qty = stock_parm.getDouble("QTY", j);
				int batch_seq = stock_parm.getInt("BATCH_SEQ", j);
				// System.out.println("order_code===" + order_code);
				// System.out.println("actual_qty===" + actual_qty);
				// System.out.println("qty===" + qty);
				if (qty >= actual_qty) {
					// 药品可以一次扣库
					double out_amt = StringTool.round(stock_parm.getDouble("RETAIL_PRICE", j) * actual_qty, 2);
					// 更新库存(申请单出库)
					result = IndStockDTool.getInstance().onUpdateQtyRequestOut("DEP", out_org_code, order_code, batch_seq, actual_qty, out_amt,
							parm.getValue("OPT_USER", i), parm.getTimestamp("OPT_DATE", i), parm.getValue("OPT_TERM", i), conn);
					
					 
					if (result.getErrCode() < 0) {
						return result;
					}
					seq++;
					break;
				} else {
					// 药品不可一次扣库
					double out_amt = StringTool.round(stock_parm.getDouble("RETAIL_PRICE", i) * qty, 2);
					// 更新库存(申请单出库)
					result = IndStockDTool.getInstance().onUpdateQtyRequestOut("DEP", out_org_code, order_code, batch_seq, qty, out_amt,
							parm.getValue("OPT_USER", i), parm.getTimestamp("OPT_DATE", i), parm.getValue("OPT_TERM", i), conn);

					 
					if (result.getErrCode() < 0) {
						return result;
					}
					seq++;
					// 更新出库量
					actual_qty = actual_qty - qty;
					// 更新实际出库数量
					qty_out = qty_out + qty;
				}
			}
		}
    	return result;
    }
    
    /**
     * 更新出库量
     * @param parm
     * @return
     */
    public TParm onUpdateOdiDspnm(TParm parm,TConnection conn ){
    	String caseNo = parm.getValue("CASE_NO") ;
    	String  orderNo = parm.getValue("ORDER_NO");
    	String orderSeq = parm.getValue("ORDER_SEQ");
    	String startDttm = parm.getValue("START_DTTM");
    	
    	double qty = parm.getDouble("ACTUAL_QTY");
    	
    	if(caseNo == null || caseNo.equals("") ||
    		orderNo == null || orderNo.equals("") || 
    		orderSeq == null || orderSeq.equals("") || 
    		startDttm == null || startDttm.equals("")){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE ODI_DSPNM A SET A.ACUM_OUTBOUND_QTY= NVL(A.ACUM_OUTBOUND_QTY,0) + " + qty +
    			     "    WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"' AND A.ORDER_NO='"+parm.getValue("ORDER_NO")+"' "+
    			     "          AND A.ORDER_SEQ="+parm.getValue("ORDER_SEQ")+"  AND A.START_DTTM='"+parm.getValue("START_DTTM")+"' ";
    	//System.out.println("onUpdateOdiDspnm=====:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql, conn));
    }
    
    /**
     * 更新出库量
     * @param parm
     * @return
     */
    public TParm onUpdateIndCabdspn(TParm parm,TConnection conn ){
    	String caseNo = parm.getValue("CASE_NO") ;
    	String  orderNo = parm.getValue("ORDER_NO");
    	String orderSeq = parm.getValue("ORDER_SEQ");
    	String startDttm = parm.getValue("START_DTTM");
    	String takemedUser = parm.getValue("TAKEMED_USER");
    	
    	double qty = parm.getDouble("ACTUAL_QTY");
    	
    	if(caseNo == null || caseNo.equals("") ||
    		orderNo == null || orderNo.equals("") || 
    		orderSeq == null || orderSeq.equals("") || 
    		startDttm == null || startDttm.equals("")){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE IND_CABDSPN A SET A.ACUM_OUTBOUND_QTY= NVL(A.ACUM_OUTBOUND_QTY,0) + " + qty + ",A.TAKEMED_USER='"+takemedUser+"' ,A.TAKEMED_DATE=SYSDATE " +
    			     "    WHERE A.CASE_NO='"+parm.getValue("CASE_NO")+"' AND A.ORDER_NO='"+parm.getValue("ORDER_NO")+"' "+
    			     "          AND A.ORDER_SEQ="+parm.getValue("ORDER_SEQ")+"  AND A.START_DTTM='"+parm.getValue("START_DTTM")+"' ";
    	//System.out.println("onUpdateOdiDspnm=====:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql, conn));
    }
    
    /**
     * 根据三个主键查询
     * @param parm
     * @return
     */
    public TParm onQueryCabinetBy(TParm parm ){
    	String orderCode = parm.getValue("ORDER_CODE");
    	String cabinetId = parm.getValue("CABINET_ID");
    	String batchSeq = parm.getValue("BATCH_SEQ");
    	String sql = " SELECT  A.CABINET_ID, A.ORDER_CODE, A.BATCH_SEQ, A.BATCH_NO, A.VALID_DATE,  "+ 
    				 "	     A.VERIFYIN_PRICE,A.STOCK_QTY, A.STOCK_UNIT"+
    				 " FROM IND_CBNSTOCK A "+
    				 " WHERE A.ORDER_CODE='"+orderCode+"' "+
    				 "       AND A.CABINET_ID='"+cabinetId+"' "+
    				 "       AND A.BATCH_SEQ='"+batchSeq+"' ";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    
   public TParm onQueryIndCabDspn(TParm parm){
	   String stationCode = parm.getValue("ORG_CODE");
	   String takemedNo = parm.getValue("TAKEMED_NO");
	   String startDate = parm.getValue("START_DATE");
	   String endDate = parm.getValue("END_DATE");
	   String status = parm.getValue("STATUS");
	   String drugCategory = parm.getValue("DRUG_CATEGORY");
	   String sql = " SELECT A.TAKEMED_NO, D.BED_NO_DESC AS BED_NO,A.MR_NO,B.PAT_NAME,C.USER_NAME, "+
	   				"         E.USER_NAME AS TAKEMED_USERNAME  "	+
	   				" FROM IND_CABDSPN A, SYS_PATINFO B,SYS_OPERATOR C,SYS_BED D, SYS_OPERATOR E "+
	   				" WHERE  A.MR_NO = B.MR_NO(+) " +
	   				"        AND A.VS_DR_CODE=C.USER_ID(+) "+
	   				"        AND A.TAKEMED_USER = E.USER_ID(+) "+
                    " 		 AND  A.BED_NO=D.BED_NO(+) " +
                    "        AND A.TAKEMED_NO IS NOT NULL ";
	  if(stationCode != null && !stationCode.equals("")) {
		  sql += " AND A.STATION_CODE='"+stationCode+"' ";
	  }
	  
	  if(takemedNo != null && !takemedNo.equals("")) {
		  sql += " AND A.TAKEMED_NO='"+takemedNo+"' " ;
	  }
	  
	  if(status != null && !status.equals("")){
		  if(status.equals("Y")){
			  sql += " AND A.DOSAGE_QTY = NVL (A.ACUM_OUTBOUND_QTY, 0) " ;
		  }else if(status.equals("N")){
			  sql += " AND A.DOSAGE_QTY > NVL (A.ACUM_OUTBOUND_QTY, 0) " ;
		  }
	  }
	  
	  if(startDate != null && !startDate.equals("")) {
		  sql += " AND A.START_DTTM >='"+startDate+"' " ;
	  }
	  
	  if(endDate != null && !endDate.equals("")){
		  sql += " AND A.START_DTTM <='"+endDate+"' " ;
	  }
	  if(drugCategory != null && !drugCategory.equals("")){
		  //普药\麻精
		  if(drugCategory.equals("1")){
			  sql += "  AND ( A.CTRLDRUGCLASS_CODE IS  NULL  OR  A.CTRLDRUGCLASS_CODE NOT IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) ";
		  }else if (drugCategory.equals("2")){
			  sql += " AND ( A.CTRLDRUGCLASS_CODE IS NOT NULL  AND  A.CTRLDRUGCLASS_CODE  IN (SELECT CTRLDRUGCLASS_CODE FROM SYS_CTRLDRUGCLASS WHERE CTRL_FLG='Y')) ";
		  }
	  }
                    
      sql+= " GROUP BY A.TAKEMED_NO, "+
			"	         D.BED_NO_DESC, "+
			"	         A.MR_NO, "+
			"	         B.PAT_NAME,"+ 
			"	         C.USER_NAME, "+
			"            E.USER_NAME  " +
			"	ORDER BY A.TAKEMED_NO";
      
	   return new TParm(TJDODBTool.getInstance().select(sql));
	   
   }
    
}
