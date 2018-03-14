package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;

public class SPCOutpatientOutStoreTool extends TJDOTool {
    /**
     * 实例
     */
    public static SPCOutpatientOutStoreTool instanceObject;
    /**
     * 得到实例
     * @return ClinicRoomTool
     */
    public static SPCOutpatientOutStoreTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCOutpatientOutStoreTool();
        return instanceObject;
    }

    /**
     * 构造器
     */
    public SPCOutpatientOutStoreTool() {
        //setModuleName("spc\\SPCOutpatientOutStoreModule.x");
        onInit();
    }
    
    public TParm onQuery(TParm parm ){
    	String orgCode = parm.getValue("ORG_CODE");
    	String startDate = parm.getValue("START_DATE");
    	String endDate = parm.getValue("END_DATE");
    	String requestNo = parm.getValue("REQUEST_NO");
    	String status = parm.getValue("STATUS");
    	String sql = "" ;
    	
    	//status:N 未出库，Y已出库
    	if(status !=null && !status.equals("")){
    		if(status.equals("N")){
    			sql = getOnQueryNSql(orgCode, startDate, endDate, requestNo);
    		}else{
    			sql = getOnQueryYSql(orgCode, startDate, endDate, requestNo);
    		}
    	}
    	return new TParm(TJDODBTool.getInstance().select(sql));
    }

	private String getOnQueryNSql(String orgCode, String startDate,
			String endDate, String requestNo) {
		String sql =  "SELECT A.REQUEST_NO,A.REQTYPE_CODE,A.APP_ORG_CODE,A.TO_ORG_CODE,A.REQUEST_DATE, "
    				+ "       A.REQUEST_USER,A.REASON_CHN_DESC,A.DESCRIPTION,A.UNIT_TYPE,A.URGENT_FLG  "
    				+ "FROM IND_REQUESTM A  "
    				+ "WHERE A.REQUEST_NO = "
    				+ "	(  SELECT B.REQUEST_NO "
    				+ "	   FROM IND_REQUESTD B "
    				+ "	   WHERE  A.REQUEST_NO = B.REQUEST_NO "
                    + "         AND B.UPDATE_FLG IN ('0', '1') "
                    + "         AND B.QTY > B.ACTUAL_QTY "
                    + "	        GROUP BY B.REQUEST_NO ) "
                    + "  AND A.DRUG_CATEGORY='2'  ";
        if(orgCode != null && !orgCode.equals("")) {
        	
        	/**
        	 String orgCodes[] = orgCode.split(";");
        	 int count = orgCodes.length  ;
        	 */
        	 sql += " AND A.TO_ORG_CODE='"+orgCode+"' ";
        	 /**
        	 sql += " AND A.TO_ORG_CODE IN (  ";
        	 for(int i =  0 ; i < count; i++) {
        		 String id = orgCodes[i];
        		 if(i== count-1){
        			 sql += "'"+ id +"' " ;
        		 }else{
        			 sql += "'"+ id +"',";
        		 }
        	 }
        	 sql += " ) ";
        	 */
        	 
        }
        
        if(requestNo !=null && !requestNo.equals("")){
        	sql += " AND A.REQUEST_NO='"+requestNo+"' " ;
        }
        if(startDate !=null && !startDate.equals("")){
        	sql += " AND A.REQUEST_DATE>=TO_DATE('"+startDate+"','YYYYMMDDHH24MISS' )" ;
        }
        if(endDate !=null && !endDate.equals("")){
        	sql += " AND A.REQUEST_DATE<=TO_DATE('"+endDate+"','YYYYMMDDHH24MISS' ) ";
        }
        sql += "  ORDER BY A.REQUEST_NO DESC ";
		return sql;
	}
	
	private String getOnQueryYSql(String orgCode, String startDate,
			String endDate, String requestNo) {
		String sql =  " SELECT DISPENSE_NO, REQTYPE_CODE, REQUEST_NO, REQUEST_DATE, APP_ORG_CODE,  "
                 	 +"		  TO_ORG_CODE, URGENT_FLG, DESCRIPTION, DISPENSE_DATE, DISPENSE_USER, "
                 	 +"       WAREHOUSING_DATE, WAREHOUSING_USER, REASON_CHN_DESC, UNIT_TYPE, UPDATE_FLG,  "
                 	 +"       OPT_USER, OPT_DATE, OPT_TERM, DRUG_CATEGORY  "
                 	 +" FROM IND_DISPENSEM A "
                 	 +" WHERE  A.DRUG_CATEGORY='2' ";
		if(orgCode != null && !orgCode.equals("")) {
			 String orgCodes[] = orgCode.split(";");
        	 int count = orgCodes.length  ; 
        	 sql += " AND A.TO_ORG_CODE IN (  ";
        	 for(int i =  0 ; i < count; i++) {
        		 String id = orgCodes[i];
        		 if(i== count-1){
        			 sql += "'"+ id +"' " ;
        		 }else{
        			 sql += "'"+ id +"',";
        		 }
        	 }
        	 sql += " ) ";
        }
        
        if(requestNo !=null && !requestNo.equals("")){
        	sql += " AND A.REQUEST_NO='"+requestNo+"' " ;
        }
        if(startDate !=null && !startDate.equals("")){
        	sql += " AND A.REQUEST_DATE>=TO_DATE('"+startDate+"','YYYYMMDDHH24MISS' )" ;
        }
        if(endDate !=null && !endDate.equals("")){
        	sql += " AND A.REQUEST_DATE<=TO_DATE('"+endDate+"','YYYYMMDDHH24MISS' ) ";
        }
        sql += "  ORDER BY A.REQUEST_NO DESC ";
		return sql ;
	}
	
	public TParm onQueryNDetail(TParm parm){
		
		String requestNo = parm.getValue("REQUEST_NO");
		String updateFlg = parm.getValue("UPDATE_FLG");
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
			         " 		AND B.ORDER_CODE = D.ORDER_CODE "+
			         "	    AND A.TO_ORG_CODE = E.ORG_CODE "+
			         " 		AND B.ORDER_CODE=E.ORDER_CODE "+
			         "      AND B.UNIT_CODE = F.UNIT_CODE" +
    				 "      AND B.REQUEST_NO = '"+requestNo+"' ";
        if(updateFlg != null && !updateFlg.equals("")) {
        	sql += " AND B.UPDATE_FLG <> '"+updateFlg+"' ";
        } 	
        return new TParm(TJDODBTool.getInstance().select(sql));
	}
	
	/**
	 * 根据请领单号查询出库单号
	 * @param parm
	 * @return
	 */
	public TParm onQueryDispenseNo(TParm parm){
		String requestNo = parm.getValue("REQUEST_NO");
		String sql = " SELECT A.DISPENSE_NO,A.REQUEST_NO  FROM IND_DISPENSEM A WHERE A.REQUEST_NO='"+requestNo+"' " ;
		return new TParm(TJDODBTool.getInstance().select(sql));
	}
    
}
