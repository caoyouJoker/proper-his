package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>
 * Title:包药机入库control
 * </p>
 * 
 * <p>
 * Description: 包药机入库control
 * </p>
 * 
 * <p>
 * Copyright (c) BlueCore 2012
 * </p>
 * 
 * <p>
 * Company: BlueCore
 * </p>
 * 
 * @author liuzhen 20121015
 * @version 1.0
 */
public class SPCPackageMachinePutUpTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCPackageMachinePutUpTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCPackageMachinePutUpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCPackageMachinePutUpTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCPackageMachinePutUpTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onQuery(TParm parm){
    	
    	String boxEslId = parm.getValue("BOX_ESL_ID");
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String isPutaway = parm.getValue("IS_PUTAWAY");
    	    	
    	String sql = "SELECT CASE WHEN B.GOODS_DESC IS NULL THEN B.ORDER_DESC ELSE  "+
	    		     "		  B.ORDER_DESC || '(' || B.GOODS_DESC || ')' END AS ORDER_DESC,  "+
	    		     "		  B.SPECIFICATION, A.QTY, E.ACTUAL_QTY, A.ACTUAL_QTY AS OUT_QTY,   "+
	    		     "		  A.UNIT_CODE, A.STOCK_PRICE / C.STOCK_QTY / C.DOSAGE_QTY AS STOCK_PRICE ,  "+
	    		     "		  A.RETAIL_PRICE AS RETAIL_PRICE, A.BATCH_NO,  "+ 
	    		     "		  A.VALID_DATE, B.PHA_TYPE, A.ORDER_CODE, C.STOCK_QTY,D.DISPENSE_NO,  "+ 
	    		     "		  C.DOSAGE_QTY, B.TRADE_PRICE, A.SEQ_NO, A.REQUEST_SEQ, A.BATCH_SEQ ,  "+
	    		     "		  F.ELETAG_CODE,F.MATERIAL_LOC_CODE,G.UNIT_CHN_DESC,H.ORG_CHN_DESC,D.APP_ORG_CODE ORG_CODE, "+
	    		     "        A.SUP_CODE,A.INVENT_PRICE,A.VERIFYIN_PRICE,A.SUP_ORDER_CODE "+
	    		     "FROM IND_DISPENSED A, PHA_BASE B, PHA_TRANSUNIT C, IND_DISPENSEM D, IND_REQUESTD E ,IND_STOCKM F ,SYS_UNIT G,IND_ORG H  "+
	    		     "WHERE A.ORDER_CODE = B.ORDER_CODE  "+ 
	    		     "		  AND A.ORDER_CODE = C.ORDER_CODE   "+
	    		     "		  AND A.DISPENSE_NO = D.DISPENSE_NO   "+
	    		     "		  AND A.REQUEST_SEQ = E.SEQ_NO   "+
	    		     "		  AND A.ORDER_CODE = F.ORDER_CODE   "+
	    		     "		  AND D.APP_ORG_CODE = F.ORG_CODE  "+
	    		     "		  AND A.UNIT_CODE =G.UNIT_CODE   "+
	    		     "		  AND D.TO_ORG_CODE = H.ORG_CODE  "+
	    		     "		  AND D.REQUEST_NO = E.REQUEST_NO   " ;
    	
    	String updateFlg = parm.getValue("UPDATE_FLG");
    	
    	//出库，在途，入库
    	if(!StringUtil.isNullString(updateFlg)){
    		sql += " AND D.UPDATE_FLG='"+updateFlg+"' " ;
    	}
    	
    	if(!StringUtil.isNullString(dispenseNo)){
    		sql += " AND D.DISPENSE_NO='"+dispenseNo+"' " ;
    	}
    	
    	if(!StringUtil.isNullString(boxEslId)){
    		sql += " AND A.BOX_ESL_ID='"+boxEslId+"' " ;
    	}
    	
    	if(!StringUtil.isNullString(isPutaway)){
    		sql += " AND A.IS_PUTAWAY='"+isPutaway+"' ";
    		
    		if(isPutaway.equals("Y")){
    			String startDate = parm.getValue("START_DATE");
    			if(startDate != null && ! startDate.equals("")){
    				
    			   sql += " AND A.PUTAWAY_DATE >="+ " TO_DATE('"+startDate+" 00:00:00','yyyy-MM-dd HH24:MI:SS')" ;
    			}
    		   
    		    String endDate = parm.getValue("END_DATE") ;
    		    if(endDate != null && ! endDate.equals("")){
    		    	
    			   sql += " AND A.PUTAWAY_DATE <="+ " TO_DATE('"+endDate +" 23:59:59','yyyy-MM-dd HH24:MI:SS')" ;
    		    }
    		}
    	}
    	
    	sql +=  " ORDER BY F.MATERIAL_LOC_CODE " ;
    	 
    	return  new TParm(TJDODBTool.getInstance().select(sql));
    	
    }
    
    public TParm updateDispensed(TParm parm ){
    	String sql = "UPDATE IND_DISPENSED A SET A.IS_PUTAWAY='"+parm.getValue("IS_PUTAWAY")+"' ,A.PUTAWAY_USER='"+parm.getValue("PUTAWAY_USER")+"',PUTAWAY_DATE=SYSDATE " +
    				 "WHERE A.DISPENSE_NO='"+parm.getValue("DISPENSE_NO")+"' AND A.SEQ_NO='"+parm.getValue("SEQ_NO")+"' ";
    	
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }

}
