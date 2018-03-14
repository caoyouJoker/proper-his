package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.util.StringUtil;

/**
 * 
 * <p>Title: 周转箱查询</p>
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
public class SPCTransportTrunkQueryTool  extends TJDOTool {
    /**
     * 实例
     */
    public static SPCTransportTrunkQueryTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCTransportTrunkQueryTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCTransportTrunkQueryTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCTransportTrunkQueryTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    public TParm onQuery(TParm parm ){
    	
    	String drugCategory = parm.getValue("DRUG_CATEGORY");
    	    	
    	//1:普药;2:麻精
    	if(drugCategory.equals("1")){
    		return onQueryGenDrug(parm);
    	}else{
    		return onQueryGenDrugN(parm);
    	}
    	 
    }
    
    /**
     * 普药查询
     * @param parm
     * @return
     */
    public TParm onQueryGenDrug(TParm parm ){
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String boxEslId = parm.getValue("BOX_ESL_ID");
    	String drugCategory = parm.getValue("DRUG_CATEGORY");
    	
    	String orgCode = parm.getValue("ORG_CODE");
    	String startDate = parm.getValue("START_DATE");
    	String endDate = parm.getValue("END_DATE");
    	String isPutaway = parm.getValue("IS_PUTAWAY");
    	
    	String sql = "SELECT B.ORDER_CODE,E.ORDER_DESC,E.SPECIFICATION,B.BATCH_NO,B.VALID_DATE, " +
    			     	    "B.QTY,F.UNIT_CHN_DESC ,D.MATERIAL_LOC_CODE,D.ELETAG_CODE,B.BOX_ESL_ID, " +
    			     	    "B.SEQ_NO,C.ORG_CHN_DESC,A.DISPENSE_NO,B.BOXED_USER,B.BOXED_DATE  "+
    			     "FROM IND_DISPENSEM A,IND_DISPENSED B,IND_ORG C,IND_STOCKM D,PHA_BASE E,SYS_UNIT F "+
    			     "WHERE A.DRUG_CATEGORY='"+drugCategory+"' ";
    	
    	if(!StringUtil.isNullString(dispenseNo)){
    		sql += " AND A.DISPENSE_NO='"+dispenseNo+"' " ;
    	}
    	
    	if(!StringUtil.isNullString(boxEslId)){
    		sql += " AND B.BOX_ESL_ID='"+boxEslId+"' ";
    	}
    	if(!StringUtil.isNullString(orgCode)){
    		sql += " AND A.APP_ORG_CODE='"+orgCode+"' ";
    	}
		
    	
    	if(!StringUtil.isNullString(isPutaway)){
    		sql += " AND B.IS_PUTAWAY='"+isPutaway+"' ";
    		if(isPutaway.equals("Y")){
				if(startDate != null && ! startDate.equals("")){
					
				   sql += " AND B.PUTAWAY_DATE >="+ " TO_DATE('"+startDate+" 00:00:00','yyyy-MM-dd HH24:MI:SS')" ;
				}
			    
			    if(endDate != null && ! endDate.equals("")){
			    	
				   sql += " AND B.PUTAWAY_DATE <="+ " TO_DATE('"+endDate +" 23:59:59','yyyy-MM-dd HH24:MI:SS')" ;
			    }
    		}
    	}
    	
    	sql += " AND A.DISPENSE_NO=B.DISPENSE_NO "+
    		   " AND A.APP_ORG_CODE=C.ORG_CODE "+   
    		   " AND B.ORDER_CODE=D.ORDER_CODE "+ 
    		   " AND A.APP_ORG_CODE=D.ORG_CODE "+
    		   " AND D.ORDER_CODE=E.ORDER_CODE " +
    		   " AND B.UNIT_CODE= F.UNIT_CODE "+
    		   " ORDER BY D.MATERIAL_LOC_CODE ";
    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    /**
     * 麻精药查询
     * @param parm
     * @return
     */
    public TParm onQueryGenDrugN(TParm parm){
    	
    	String dispenseNo = parm.getValue("DISPENSE_NO");
    	String boxEslId = parm.getValue("BOX_ESL_ID");
    	
    	String sql = "SELECT A.DISPENSE_NO ,A.ORDER_CODE ,B.ORDER_DESC,B.SPECIFICATION,A.BOX_ESL_ID, "+
    		  "       A.CONTAINER_DESC,A.CONTAINER_ID,A.BOXED_USER,A.BOXED_DATE, "+
    		  "       (SELECT COUNT(F.CONTAINER_ID ) FROM IND_TOXICD F WHERE A.CONTAINER_ID=F.CONTAINER_ID  AND A.DISPENSE_NO= F.DISPENSE_NO AND A.DISPENSE_SEQ_NO=F.DISPENSE_SEQ_NO ) TOXIC_QTY "+
    		  "FROM IND_TOXICM A,PHA_BASE B  "+
    		  "WHERE  A.ORDER_CODE = B.ORDER_CODE ";
    	
    	if(!StringUtil.isNullString(dispenseNo)){
    		sql += " AND A.DISPENSE_NO='"+dispenseNo+"' " ;
    	}
    	
    	if(!StringUtil.isNullString(boxEslId)){
    		sql += " AND A.BOX_ESL_ID='"+boxEslId+"' ";
    	}
 
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    
    public TParm onQueryContainer(TParm parm){
    	
    	String containerId = parm.getValue("CONTAINER_ID");
    	if(StringUtil.isNullString(containerId)){
    		return new TParm();
    	}
    	
    	String  sql = " SELECT  A.TOXIC_ID,A.BATCH_NO,A.VALID_DATE,B.UNIT_CHN_DESC "+
    				  " FROM IND_TOXICD A ,SYS_UNIT B  "+
    				  " WHERE A.DISPENSE_NO='"+parm.getValue("DISPENSE_NO")+"' AND A.CONTAINER_ID='"+containerId+"'  AND A.UNIT_CODE=B.UNIT_CODE";
    	
    	return new TParm(TJDODBTool.getInstance().select(sql));
    	
    }
}
