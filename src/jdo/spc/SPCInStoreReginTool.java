package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title:病区普药入智能柜 </p>
 *
 * <p>Description:TODO </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: BlueCore</p>
 *
 * @author Yuanxm
 * @version 1.0
 */
public class SPCInStoreReginTool extends TJDOTool {
	
    /**
     * 实例
     */
    public static SPCInStoreReginTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCInStoreReginTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCInStoreReginTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCInStoreReginTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onQuery(TParm parm){
    	
    	String boxEslId = parm.getValue("BOX_ESL_ID");
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String isStore = parm.getValue("IS_STORE");
    	String updateFlg = parm.getValue("UPDATE_FLG");
    	//String drugCategory = parm.getValue("DRUG_CATEGORY");  --by huzc 2015/12/28
    	
    	if(StringUtil.isNullString(boxEslId) &&  StringUtil.isNullString(dispenseNo)){
    		return new TParm();
    	}
    	
    	String sql = "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE "
				        + "B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,"
				        + "B.SPECIFICATION, A.QTY, E.ACTUAL_QTY, A.ACTUAL_QTY AS OUT_QTY, "
				        + "A.UNIT_CODE, A.VERIFYIN_PRICE AS STOCK_PRICE ,"
				        +" A.RETAIL_PRICE  AS RETAIL_PRICE, A.BATCH_NO, "
				        + "A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY, "
				        + "C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ, A.BATCH_SEQ ,"
				        + "F.ORG_CHN_DESC,G.UNIT_CHN_DESC,D.REQTYPE_CODE,C.DOSAGE_UNIT ,C.STOCK_UNIT ,  "
				        + "A.VERIFYIN_PRICE,A.INVENT_PRICE ,A.SUP_CODE    "
				    + "FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C, IND_DISPENSEM D, IND_REQUESTD E ,IND_ORG F,SYS_UNIT G "
				    + "WHERE A.ORDER_CODE = B.ORDER_CODE  "
				        + "AND A.ORDER_CODE = C.ORDER_CODE  "
				        + "AND A.DISPENSE_NO = D.DISPENSE_NO  "
				        + "AND A.REQUEST_SEQ = E.SEQ_NO "
				        + "AND D.TO_ORG_CODE=F.ORG_CODE "
				        + "AND A.UNIT_CODE= G.UNIT_CODE "
				        + "AND D.REQUEST_NO = E.REQUEST_NO AND A.DISPENSE_NO = '"+ dispenseNo + "' ";
				        //+ "AND D.DRUG_CATEGORY='"+drugCategory+"' "; --by huzc 2015/12/28
    	
	    	if(isStore != null && !isStore.equals("")){
	    		 sql += " AND  A.IS_PUTAWAY='"+isStore+"' " ;
	    	}
	    	
	    	if(boxEslId != null && !boxEslId.equals("")){
	    		sql += " AND A.BOX_ESL_ID='"+boxEslId+"' " ;
	    	}
	    	
	    	/**补充计费一出库就是updateFlg=3 去掉这条件
	    	if(updateFlg != null && !updateFlg.equals("")){
	    		sql = sql  + "AND D.UPDATE_FLG = '"+updateFlg+"'  " ;
	    	}else{
	    		sql = sql  + "AND D.UPDATE_FLG <> '3'  " ;
	    	}*/
    	
    		sql =	sql +" ORDER BY  A.SEQ_NO DESC  " ;
    		//System.out.println("sql----:"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    	
    }
    
    
    /**
     * 更新标志为病区入库
     * @param parm
     * @return
     */
    public TParm onUpdateDispenseD(TParm parm ){
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String seqNo = parm.getValue("SEQ_NO");
    	String isSotre = parm.getValue("IS_STORE");
    	
    	if( (dispenseNo == null || dispenseNo.equals("")) || ( seqNo== null || seqNo.equals(""))){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE IND_DISPENSED A SET A.IS_STORE='"+isSotre+"'    WHERE A.DISPENSE_NO='"+dispenseNo+"'  AND A.SEQ_NO='"+seqNo+"' ";
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }
      
   
   /**
    * 根据出库单查询主表对应的信息
    * @param parm
    * @return
    */
   public TParm onQueryDispenseM(TParm parm) {
	   
	   String dispenseNo = parm.getValue("DISPENSE_NO");
	   if(dispenseNo == null || dispenseNo.equals("")){
		   return new TParm();
	   }
	   String sql = " SELECT * FROM IND_DISPENSEM A WHERE A.DISPENSE_NO='"+dispenseNo+"' ";
	   
	   return new TParm(TJDODBTool.getInstance().select(sql));
   }
   
   public TParm onQueryIndCbnstock(TParm parm){
	   
	   String cabinetId = parm.getValue("CABINET_ID") ;
	   String orderCode = parm.getValue("ORDER_CODE") ;
	   String batchSeq = parm.getValue("BATCH_SEQ") ;
	   
	   if(cabinetId == null || cabinetId.equals("") || 
		  orderCode == null || orderCode.equals("") ||
		  batchSeq == null || batchSeq.equals("")){
		   return new TParm() ;
	   }
	   
	   String sql = " SELECT CABINET_ID " +
	   				" FROM IND_CBNSTOCK A "+
	   				" WHERE A.CABINET_ID='"+cabinetId+"' AND "+
	   				" A.ORDER_CODE='"+orderCode+"' AND "+
	   				" A.BATCH_SEQ='"+batchSeq+"' ";
	   return new TParm(TJDODBTool.getInstance().select(sql));
   }
   
   
   /**
    * 保存普药入智能柜的库存
    * @param parm
    * @return
    */
   public TParm onSaveIndCbnstock(TParm parm,TConnection conn ){
	   
	   //日期截取最后一个.0不要
	   String validDate = parm.getValue("VALID_DATE");
	   int length = validDate.lastIndexOf(".");
	   validDate = validDate.substring(0,length);
	  
	   String sql = " INSERT INTO IND_CBNSTOCK ( " +
	   				" CABINET_ID,ORDER_CODE,BATCH_SEQ,BATCH_NO,VALID_DATE," +
	   				" VERIFYIN_PRICE,STOCK_QTY,STOCK_UNIT,OPT_USER,OPT_DATE," +
	   				" OPT_TERM  "+
	   				" ) VALUES (  " +
	   				" '"+parm.getValue("CABINET_ID")+"', '"+parm.getValue("ORDER_CODE")+"',"+parm.getValue("BATCH_SEQ")+",  "+
	   				" '"+parm.getValue("BATCH_NO")+"', TO_DATE('"+validDate+"', 'YYYY-MM-DD HH24:MI:SS')"+",'"+parm.getValue("VERIFYIN_PRICE")+"',  "+
	   				" "+parm.getDouble("OUT_QTY")+", '"+parm.getValue("UNIT_CODE")+"','"+parm.getValue("OPT_USER")+"',  "+
	   				" SYSDATE, '"+parm.getValue("OPT_TERM")+"' ) ";
	   
	   //System.out.println("sql========:"+sql);
	   
	   return new TParm(TJDODBTool.getInstance().update(sql,conn));
   }
   
   /**
    * 保存普药入智能柜的库存
    * @param parm
    * @return
    */
   public TParm onUpdateIndCbnstock(TParm parm,TConnection conn ){
	   String sql = " UPDATE IND_CBNSTOCK SET  " +
	   				"        STOCK_QTY=STOCK_QTY+"+parm.getValue("OUT_QTY")+", OPT_USER='"+parm.getValue("OPT_USER")+"',  OPT_DATE="+
	   				"        SYSDATE,OPT_TERM='"+parm.getValue("OPT_TERM")+"'  "+
	   				" WHERE CABINET_ID='"+parm.getValue("CABINET_ID")+"' AND ORDER_CODE='"+parm.getValue("ORDER_CODE")+"' AND BATCH_SEQ="+parm.getValue("BATCH_SEQ");
	   
	  // System.out.println("sql========:"+sql);
	   
	   return new TParm(TJDODBTool.getInstance().update(sql,conn));
   }
}
