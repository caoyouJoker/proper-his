package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class SPCDispenseCountWithLiquidHosStrTool extends TJDODBTool {
	
	 /** 实例*/
    public static SPCDispenseCountWithLiquidHosStrTool instanceObject;

    /**得到实例*/
    public static SPCDispenseCountWithLiquidHosStrTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCDispenseCountWithLiquidHosStrTool();
        return instanceObject;
    }
        
    /**查询*/
    public TParm query(TParm parm) {

		String start_date = parm.getValue("START_DATE");//2012-01-01 12:12:00.0
		String end_date = parm.getValue("END_DATE");
		String station_id = parm.getValue("STATION_ID");
		String intgmed_no = parm.getValue("INTGMED_NO");
		
		if(null != start_date && start_date.length() > 19){
			start_date = start_date.substring(0, 19);
		}
		
		if(null != end_date && end_date.length() > 19){
			end_date = end_date.substring(0, 19);
		}
    	
		String con1 = "";
		if(null != station_id && !"".equals(station_id.trim())){
			con1 = " AND A.STATION_CODE='" + station_id + "' " ;  
		}
		
		String con2 = "";
		if(null != intgmed_no && !"".equals(intgmed_no.trim())){
			con2 = " AND A.INTGMED_NO='" + intgmed_no + "' " ;  
		}
		
		String con3 = "";
		if(null != start_date && !"".equals(start_date.trim()) && null != end_date && !"".equals(end_date.trim())){
			
			con3 = " AND A.DSPN_DATE BETWEEN TO_DATE('"+start_date+"','yyyy-mm-dd hh24:mi:ss') " +
								 	"AND TO_DATE('"+end_date+"','yyyy-mm-dd hh24:mi:ss') ";
		}
		
    	String sql ="SELECT " +
    					"B.STATION_DESC," +
    					"A.INTGMED_NO," +
    					"to_char(A.DSPN_DATE,'yyyy-mm-dd hh24:mi:ss') as DSPN_DATE," +
    					"A.DSPN_USER, " +
    					"C.USER_NAME " +
    				"FROM ODI_DSPNM A, " +
    						"SYS_STATION B, " +
    						"SYS_OPERATOR C " +
    				"WHERE 1=1 " +
    					con3 +
    					con1 +
    					con2 +
    					"AND A.DSPN_KIND='UD' " +
    					"AND A.INTGMED_NO IS NOT NULL " +
    					"AND B.STATION_CODE=A.STATION_CODE " +
    					"AND C.USER_ID=A.DSPN_USER" ;
    	    	
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
        	
}
