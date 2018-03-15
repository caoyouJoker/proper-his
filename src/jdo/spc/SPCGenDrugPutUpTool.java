package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: 普药上架</p>
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
public class SPCGenDrugPutUpTool  extends TJDOTool {
   
	/**
     * 实例
     */
    public static SPCGenDrugPutUpTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCGenDrugPutUpTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCGenDrugPutUpTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCGenDrugPutUpTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
      
    public TParm onQuery(TParm parm){
    	  
    	String verifyinNo = parm.getValue("VERIFYIN_NO");
    	String contaninerTags = parm.getValue("CONTAINER_TAGS");
    	String isPutaway = parm.getValue("IS_PUTAWAY");
    	TParm result = null;
    	
    	/**
    	if(!StringUtil.isNullString(isPutaway)){
    		if(isPutaway.equals("N")){
		    	if(StringUtil.isNullString(verifyinNo) &&  StringUtil.isNullString(contaninerTags)){
		    		return new TParm();
		    	} 
    		}
    	}*/
    	
    	if(!StringUtil.isNullString(contaninerTags)){
    		String sq = "SELECT PURORDER_NO FROM IND_VERIFYIND WHERE SPC_BOX_BARCODE='"+contaninerTags+"'";
    		result = new TParm(TJDODBTool.getInstance().select(sq));
    		if (result.getErrCode() < 0||result.getCount()<=0) {
				return new TParm();      
			}
    	}
    	
    	//验收价格取IND_VERIFYIND的验收价格/DOSAGE_QTY/STOCK_QTY的 写到IND_STOCK的验收价格。临售价格取PHA_BASE的价格。价格是最小单位。
    	//(STOCK_QTY)_数量取页面的ACTUAL_QTY*DOSTAGE_QTY*STOCK_QTY( IN_QTY,VERIFYIN_QTY),
    	//写到IND_STOCK,并回写IND_VERFIYD的ACTURY整盒. IN_AMT(实际验收量* 整盒价格（IND_VERIFYIND中取整盒价格）)
    	String sql = " SELECT B.ORDER_CODE,C.ORDER_DESC,C.SPECIFICATION,  B.BATCH_NO,B.VALID_DATE," +
    					" B.VERIFYIN_QTY,B.VERIFYIN_PRICE,B.QUALITY_DEDUCT_AMT,B.PURORDER_NO,B.PURSEQ_NO,C.RETAIL_PRICE," +
    					" B.BILL_UNIT ,B.GIFT_QTY, B.SEQ_NO,B.VERIFYIN_PRICE*B.VERIFYIN_QTY AS VERIFYIN_AMT,"+
    					" D.MATERIAL_LOC_CODE, B.VERIFYIN_NO,D.ORG_CODE,D.ELETAG_CODE,B.PUTAWAY_DATE , " +
    					" A.SUP_CODE , B.SPC_BOX_BARCODE ,B.SUP_ORDER_CODE , B.PRC   " +
    				 " FROM IND_VERIFYINM A,IND_VERIFYIND B,PHA_BASE C,IND_STOCKM D" +
    				 " WHERE A.VERIFYIN_NO=B.VERIFYIN_NO ";
    	sql += " AND B.ORDER_CODE=C.ORDER_CODE " +
			   " AND A.ORG_CODE=D.ORG_CODE " +
			   " AND B.ORDER_CODE=D.ORDER_CODE " +
			   " AND A.DRUG_CATEGORY='1'  " ;
    	
    	if(!StringUtil.isNullString(verifyinNo)){
    		sql += " AND A.VERIFYIN_NO='"+verifyinNo+"' " ;
    	}
    	
    	if(result!=null&&result.getCount()>=0){
    		sql += " AND B.PURORDER_NO in(";
    		for(int i=0;i<result.getCount();i++) {
    			sql+="'"+result.getValue("PURORDER_NO",i)+"'";
    			if(i!=result.getCount()-1) {													
    				sql+=",";
    			}     
    		}   
    		sql += ") ";
    	}
    	    
    	if(!StringUtil.isNullString(isPutaway)){
    		sql += " AND B.IS_PUTAWAY='"+isPutaway+"' ";
    		
    		if(isPutaway.equals("Y")){
    			String startDate = parm.getValue("START_DATE");
    			if(startDate != null && ! startDate.equals("")){
    				
    			   sql += " AND B.PUTAWAY_DATE >="+ " TO_DATE('"+startDate+" 00:00:00','yyyy-MM-dd HH24:MI:SS')" ;
    			}
    		   
    		    String endDate = parm.getValue("END_DATE") ;
    		    if(endDate != null && ! endDate.equals("")){
    		    	
    			   sql += " AND B.PUTAWAY_DATE <="+ " TO_DATE('"+endDate +" 23:59:59','yyyy-MM-dd HH24:MI:SS')" ;
    		    }
    		}else{
    			sql += " AND B.UPDATE_FLG IN ( '0', '1' ) ";
    		}
    	}
    	

    	sql +=	   "  ORDER BY D.MATERIAL_LOC_CODE " ;  
    	
    	return  new TParm(TJDODBTool.getInstance().select(sql));
    	
    }
    
    public TParm updateINDverifyind(TParm parm ){
    	String sql = "UPDATE IND_VERIFYIND A SET A.IS_PUTAWAY='"+parm.getValue("IS_PUTAWAY")+"',A.PUTAWAY_USER='" +parm.getValue("PUTAWAY_USER")+"',A.PUTAWAY_DATE=SYSDATE " +
    				 "WHERE A.VERIFYIN_NO='"+parm.getValue("VERIFYIN_NO")+"' AND A.SEQ_NO='"+parm.getValue("SEQ_NO")+"' ";
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }
    
    public TParm updateINDverifyindShow(TParm parm){
    	String sql = " UPDATE IND_VERIFYIND A SET A.IS_PUTAWAY='"+parm.getValue("IS_PUTAWAY")+"',A.PUTAWAY_USER='" +parm.getValue("PUTAWAY_USER")+"',A.PUTAWAY_DATE='' ,A.UPDATE_FLG='1'  " +
		 			 " WHERE A.VERIFYIN_NO='"+parm.getValue("VERIFYIN_NO")+"' ";
    	return new TParm(TJDODBTool.getInstance().update(sql));
    }
    
    public TParm updateINDverifyindSpc(TParm parm,TConnection conn ){
    	String sql = " UPDATE IND_VERIFYIND A SET A.IS_PUTAWAY='"+parm.getValue("IS_PUTAWAY")+"',A.PUTAWAY_USER='" +parm.getValue("PUTAWAY_USER")+"'," +
    				 " A.PUTAWAY_DATE=SYSDATE,A.ACTUAL_QTY="+parm.getDouble("VERIFYIN_QTY")+ ",A.UPDATE_FLG='3',A.BATCH_SEQ="+parm.getInt("BATCH_SEQ")+
    				 " WHERE A.VERIFYIN_NO='"+parm.getValue("VERIFYIN_NO")+"' AND A.SEQ_NO='"+parm.getValue("SEQ_NO")+"' ";
    	return new TParm(TJDODBTool.getInstance().update(sql,conn));   
    }
    
    public TParm onQueryLabelByOrgCode(TParm parm){
    	String sql = "  SELECT A.LABEL_ID,A.AP_REGION,A.CLASSIFY FROM IND_LABEL A WHERE A.LABEL_ID='"+parm.getValue("ORG_CODE")+"' ";
    	TParm result = new TParm(TJDODBTool.getInstance().select(sql));
    	if(result.getCount() > 0 ){
    		return result.getRow(0);
    	}
    	return null;
    }

}
