package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

public class SPCOutStoreTool extends TJDOTool {
	

	/**
     * 实例
     */
    public static SPCOutStoreTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCOutStoreTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCOutStoreTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCOutStoreTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    
    public TParm onQuery(TParm parm){
    	
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	if(StringUtil.isNullString(dispenseNo )){
    		return new TParm();
    	}
    	
    	String sql = " SELECT  B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,B.BATCH_NO,B.VALID_DATE," +
    				 "         B.QTY,F.UNIT_CHN_DESC, B.ACUM_OUTBOUND_QTY ACTUAL_QTY, B.DISPENSE_NO,A.REQTYPE_CODE,  " +
    				 "         C.ORG_CHN_DESC,B.SEQ_NO ,A.DISPENSE_NO  "+
    	             " FROM IND_DISPENSEM A,IND_DISPENSED B,IND_ORG C,PHA_BASE E,SYS_UNIT F " ;
    	            
    	
    	sql += " WHERE A.DISPENSE_NO=B.DISPENSE_NO "+
			   " AND A.TO_ORG_CODE=C.ORG_CODE "+   
			   " AND B.ORDER_CODE=E.ORDER_CODE " +
			   " AND B.UNIT_CODE= F.UNIT_CODE "+
    		   " AND A.DISPENSE_NO='"+dispenseNo+"' " +
               " AND A.DRUG_CATEGORY='2' "+
    			" ORDER BY (B.QTY-NVL(B.ACUM_OUTBOUND_QTY,0)) DESC  ";
    	//System.out.println("sql============== :"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    public TParm onUpdateDispenseD(TParm parm,TConnection conn){
    	
    	String dispenseNO = parm.getValue("DISPENSE_NO");
    	String seqNO = parm.getValue("DISPENSE_SEQ_NO");
    	if( (dispenseNO== null || dispenseNO.equals("")) || (seqNO==null|| seqNO.equals(""))){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE IND_DISPENSED A  SET A.ACUM_OUTBOUND_QTY = NVL(A.ACUM_OUTBOUND_QTY,0)+1 " +
    			     " WHERE A.DISPENSE_NO='"+dispenseNO+"' AND A.SEQ_NO='"+seqNO+"' " ;
    	//System.out.println("onUpdateDispenseD==========:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql,conn));
    }
    
    public TParm onUpdateDispenseDNum(TParm parm,TConnection conn){
    	
    	String dispenseNO = parm.getValue("DISPENSE_NO");
    	String seqNO = parm.getValue("DISPENSE_SEQ_NO");
    	int  num = parm.getInt("NUM");
    	if( (dispenseNO== null || dispenseNO.equals("")) || (seqNO==null|| seqNO.equals(""))){
    		return new TParm();
    	}
    	
    	String sql = " UPDATE IND_DISPENSED A  SET A.ACUM_OUTBOUND_QTY = NVL(A.ACUM_OUTBOUND_QTY,0)+ "+num +
    			     " WHERE A.DISPENSE_NO='"+dispenseNO+"' AND A.SEQ_NO='"+seqNO+"' " ;
    	//System.out.println("onUpdateDispenseD==========:"+sql);
    	return new TParm(TJDODBTool.getInstance().update(sql,conn));
    }
    
    /**
     * 智能柜关门时，查询智能柜里现有多少容器
     * @param parm
     * @return
     */
    public TParm onQueryContainer(TParm parm){
    	
    	String cabinetId = parm.getValue("CABINET_ID");
    	
    	if(cabinetId == null || cabinetId.equals("")){
    		return new TParm();
    	}
    	
    	String sql = " SELECT  B.CONTAINER_ID,A.CONTAINER_DESC,COUNT(B.CONTAINER_ID) NUM " +
    			     " FROM IND_CONTAINERM A,IND_CONTAINERD B  " +
    			     " WHERE A.CONTAINER_ID = B.CONTAINER_ID AND B.CABINET_ID='"+ cabinetId +"' " +
    			     " GROUP BY B.CONTAINER_ID ,A.CONTAINER_DESC " ;
    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 根据智能柜与容器ID查询
     * @param parm
     * @return
     */
    public TParm onQueryContainerDetail(TParm parm ){
    	String cabinetId = parm.getValue("CABINET_ID");
    	String containerId = parm.getValue("CONTAINER_ID");
    	if(cabinetId == null || cabinetId.equals("") ||
    			containerId == null || containerId.equals("")){
    		return new TParm() ;
    	}
    	
    	String sql = " SELECT  A.CONTAINER_DESC,A.CONTAINER_ID,B.ORDER_CODE,B.BATCH_NO, " +
    	 			 " 		   B.VALID_DATE,B.VERIFYIN_PRICE,B.BATCH_SEQ,B.TOXIC_ID,B.UNIT_CODE,  " +
    	 			 "         B.RETAIL_PRICE,B.INVENT_PRICE,B.SUP_CODE ,B.SUP_ORDER_CODE" +
    	 			 " FROM IND_CONTAINERM A,IND_CONTAINERD B " +
    	 			 " WHERE A.CONTAINER_ID=B.CONTAINER_ID AND  B.CABINET_ID='"+cabinetId+"'  AND  B.CONTAINER_ID='"+containerId+"' "  ;
    	
    	//System.out.println("onQueryContainerDetail====:"+sql);
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    
    public TParm onQueryToxic(TParm parm) {
    	
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	
    	String sql = " SELECT ROWNUM ROW_NUM, B.CONTAINER_DESC,B.CONTAINER_ID,C.ORDER_CODE,C.BATCH_NO, "+
					 " 		  C.VALID_DATE,C.VERIFYIN_PRICE,C.BATCH_SEQ,C.TOXIC_ID "+
					 " FROM IND_TOXICD A,IND_CONTAINERM B,IND_CONTAINERD C "+
					 " WHERE A.DISPENSE_NO='"+dispenseNo+"'  "+
					 "       AND A.CONTAINER_ID = B.CONTAINER_ID AND B.CONTAINER_ID = C.CONTAINER_ID  AND A.TOXIC_ID = C.TOXIC_ID";
    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
	
}
