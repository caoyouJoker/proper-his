package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCOperationRoomOutTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCOperationRoomOutTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCOperationRoomOutTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCOperationRoomOutTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCOperationRoomOutTool() {
        //setModuleName("reg\\REGClinicRoomModule.x");
        onInit();
    }
    
    /**
     * 查询已出库列表
     * @param parm
     * @return
     */
    public TParm onQueryList(TParm parm){
    	String dispensedNo = parm.getValue("DISPENSE_NO");
    	String startDate = parm.getValue("START_DATE");
    	String endDate = parm.getValue("END_DATE");
    	String sql =  " SELECT A.DISPENSE_NO, B.USER_NAME, A.OPT_DATE "
    				+ " FROM IND_TOXICM A, SYS_OPERATOR B "
    				+ " WHERE     A.OPT_USER = B.USER_ID ";
    	if(dispensedNo != null && !dispensedNo.equals("")){
    		sql += " AND A.DISPENSE_NO='"+dispensedNo+"' " ;
    	}
    	sql += "  AND A.OPT_DATE BETWEEN TO_DATE ('"+startDate+"', 'YYYYMMDDHH24MISS') "
             + "               AND TO_DATE ('"+endDate+"', 'YYYYMMDDHH24MISS')"
             + " GROUP BY A.DISPENSE_NO, B.USER_NAME, A.OPT_DATE  "
             + "   ORDER BY A.OPT_DATE DESC ";
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    public TParm onQuery(TParm parm){
    	String toxicId = parm.getValue("TOXIC_ID");
    	String dispensedNo = parm.getValue("DISPENSE_NO");
    	String sql = "SELECT A.CONTAINER_DESC,A.CONTAINER_ID,B.ORDER_CODE,B.BATCH_NO,B.VALID_DATE, "
				   + "  B.VERIFYIN_PRICE,B.BATCH_SEQ,B.TOXIC_ID,B.UNIT_CODE,C.SPECIFICATION,A.DISPENSE_SEQ_NO AS SEQ_NO, "
				   + "  C.ORDER_DESC," 
				   + "( SELECT COUNT(AA.TOXIC_ID) FROM IND_TOXICD AA  "
				   + "  WHERE AA.DISPENSE_NO=B.DISPENSE_NO AND AA.DISPENSE_SEQ_NO=B.DISPENSE_SEQ_NO "
				   + "        AND AA.ORDER_CODE=B.ORDER_CODE  "
				   + "  GROUP BY A.ORDER_CODE ) AS QTY,A.OPT_DATE    "
				   + "FROM IND_TOXICM A, IND_TOXICD B, PHA_BASE C "
				   + " WHERE A.DISPENSE_NO = B.DISPENSE_NO "
				   + " AND A.DISPENSE_SEQ_NO=B.DISPENSE_SEQ_NO "
				   + " AND A.CONTAINER_ID=B.CONTAINER_ID "
				   + " AND B.ORDER_CODE=C.ORDER_CODE " ;
		if(toxicId != null && !toxicId.equals("")) {		  
			sql += " AND B.TOXIC_ID='"+toxicId+"' " ;
		}
		if(dispensedNo != null && !dispensedNo.equals("")) {		  
			sql += " AND A.DISPENSE_NO='"+dispensedNo+"' " ;
		}
		
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
    
    public TParm onQueryBy(TParm parm){
    	String toxicId = parm.getValue("TOXIC_ID");
    	String containerId = parm.getValue("CONTAINER_ID");
    	String cabinertId = parm.getValue("CABINET_ID");
    	if( (toxicId == null || toxicId.equals(""))  && 
    			(containerId == null || containerId.equals(""))){
    		return new TParm();
    	}
    	
    	String sql = " SELECT A.CONTAINER_DESC,A.CONTAINER_ID,B.ORDER_CODE,B.BATCH_NO, "
			       + " 		      B.VALID_DATE,B.VERIFYIN_PRICE,B.BATCH_SEQ,B.TOXIC_ID,B.UNIT_CODE," 
				   + "               C.SPECIFICATION, C.ORDER_DESC  "
				   + " FROM IND_CONTAINERM A,IND_CONTAINERD B,PHA_BASE C "
				   + " WHERE   A.CONTAINER_ID=B.CONTAINER_ID "
				   + "        AND B.ORDER_CODE=C.ORDER_CODE " ;
		if(toxicId != null && !toxicId.equals("")){
			sql += "       AND B.TOXIC_ID='"+ toxicId+ "' ";
		}
		if(containerId !=null && !containerId.equals("")){
			sql += " AND B.CONTAINER_ID='"+containerId+"' " ;
		}
		sql += " AND B.CABINET_ID='"+cabinertId+"' " ;
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }
}
