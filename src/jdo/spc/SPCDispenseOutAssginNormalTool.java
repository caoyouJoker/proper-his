package jdo.spc;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class SPCDispenseOutAssginNormalTool extends TJDODBTool {
	
	
	 /** 实例*/
    public static SPCDispenseOutAssginNormalTool instanceObject;

    /**得到实例*/
    public static SPCDispenseOutAssginNormalTool getInstance() {
        if (instanceObject == null)
            instanceObject = new SPCDispenseOutAssginNormalTool();
        return instanceObject;
    }
        
    
    /**查询所有药盒*/
    public TParm queryAllBox() {
    	String sql ="SELECT " +
    					"ELETAG_CODE,AP_REGION ,CASE_NO " +
    				"FROM IND_MEDBOX" ;    	    	
		
		TParm result = new TParm(this.select(sql));
		
		return result;
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
			con1 = " AND STATION_CODE='" + station_id + "' " ;  
		}
		
		String con2 = "";
		if(null != intgmed_no && !"".equals(intgmed_no.trim())){
			con2 = " AND INTGMED_NO='" + intgmed_no + "' " ;  
		}
		
		String con3 = "";
		if(null != start_date && !"".equals(start_date.trim()) && null != end_date && !"".equals(end_date.trim())){
			
			con3 = " AND DSPN_DATE BETWEEN TO_DATE('"+start_date+"','yyyy-mm-dd hh24:mi:ss') " +
								 	"AND TO_DATE('"+end_date+"','yyyy-mm-dd hh24:mi:ss') ";
		}
		
    	String sql ="SELECT B.STATION_DESC, A.INTGMED_NO, A.DSPN_DATE, A.DSPN_USER, C.USER_NAME, A.STATION_CODE " + 
    	  			"FROM ( " +
    	  				"SELECT STATION_CODE, " +
    	  						"INTGMED_NO, " +
    	  						"MAX(TO_CHAR (DSPN_DATE, 'YYYY-MM-DD HH24:MI:SS')) AS DSPN_DATE, " +
    	  						"MAX(DSPN_USER) AS DSPN_USER " +
    	  					"FROM ODI_DSPNM " +
    	  					"WHERE 1=1 " + 
	    	  					con1 + 
	    	  					con2 + 
	    	  					con3 + 
	    	  					"AND DSPN_KIND='UD' " +
	    	  					"AND INTGMED_NO IS NOT NULL "+
	    	  					"AND INTGMED_NO IS NOT NULL " +
    	  				"GROUP BY STATION_CODE,INTGMED_NO) A, "+
    	       "SYS_STATION B, "+
    	       "SYS_OPERATOR C "+
    	 "WHERE B.STATION_CODE=A.STATION_CODE " +
    	   "AND C.USER_ID=A.DSPN_USER "+ 
    	 "ORDER BY A.STATION_CODE ";
    	 
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**查询病区所有药盒*/
    public TParm queryBox(String stationCode,String groupId) {

    	String sql ="SELECT STATION_CODE," +
    						"GROUP_ID," +
    						"SEQ_NO," +
    						"ELETAG_CODE," +
    						"AP_REGION," +
    						"OPT_USER," +
    						"OPT_DATE," +
    						"OPT_TERM " +
    				"FROM IND_MEDBOX " +
    				"WHERE STATION_CODE='" + stationCode + "' " +
    						"AND GROUP_ID='" + groupId + "' " +
    						"ORDER BY SEQ_NO" ;    	    	
    	
    	TParm result = new TParm(this.select(sql));

		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			return result;
		}
		return result;
    }
    
    /**根据病区 将该病区药盒CASE_NO字段置为null*/
    public TParm clearCaseNoByStationCode(String stationCode) {
    	String sql ="UPDATE IND_MEDBOX " +
    					"SET CASE_NO = NULL " +
    				"WHERE STATION_CODE ='" + stationCode + "'";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
    
    
    /**根据药盒电子标签更新CaseNo*/
    public TParm updateCaseNoByEleTag(String caseNo,String eleTagCode, 
    								String optUser, String optTerm) {
    	
    	String sql = "UPDATE IND_MEDBOX " +
    				"SET " +
    					"CASE_NO='" + caseNo + "'" +
//						"OPT_USER='" + optUser + "', " +
//						"OPT_DATE=SYSDATE," +
//						"OPT_TERM='" + optTerm + "' " +
    				"WHERE ELETAG_CODE='" + eleTagCode + "'";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
        
    
    
    /**根据病区、组别查询电子标签*/
    public TParm queryEleTagByTagCode(String eletagCode) {
    	String sql = "SELECT " +
    						"ELETAG_CODE,AP_REGION,CASE_NO " +
    				"FROM IND_MEDBOX " +
    				"WHERE ELETAG_CODE='" + eletagCode + "'";
    	
    	TParm result = new TParm(this.select(sql));
    	
		return result;
    }
    
    
    
    
    /**根据病区、组别、电子标签 将该药盒CASE_NO字段置为null*/
    public TParm clearCaseNoByEletagCode(String eletagCode) {
    	String sql ="UPDATE IND_MEDBOX " +
    					"SET CASE_NO = NULL " +
    				"WHERE ELETAG_CODE='"+eletagCode+"'";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
    
    
    /**根据电子标签 将该药盒CASE_NO字段更新*/
    public TParm updateCaseNoByEletagCode(String eletagCode, String caseNo) {
    	String sql ="UPDATE IND_MEDBOX " +
    					"SET CASE_NO = '" + caseNo + "' " +
    				"WHERE ELETAG_CODE='"+eletagCode+"'";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
    

    /**根据统药单号查询病患明细 包括所绑定的药盒*/
    public TParm queryPatientByIntgmedNo(String intgmedNo,String stationCode){
    	String sql = "SELECT  'FALSE' AS SELECT_FLG, " +
    							"A.MR_NO, " +
    							"C.BED_NO_DESC, " +
    							"D.PAT_NAME, " +
    							"E.ELETAG_CODE AS BOX_ESL_ID, " +
    							"E.AP_REGION, " +
    							
    							"A.CASE_NO, " +	
    							"A.INTGMED_NO, " +
    							"B.STATION_DESC, " +
    							"A.BED_NO, " +    														
								"A.STATION_CODE "+								
	//SELECT_FLG;MR_NO;BED_NO_DESC;PAT_NAME;BOX_ESL_ID;AP_REGION;CASE_NO;INTGMED_NO;STATION_DESC;BED_NO;STATION_CODE
						"FROM (SELECT MAX(INTGMED_NO) AS INTGMED_NO," +
									"CASE_NO, " +
									"MAX(BED_NO) AS BED_NO," +
									"MAX(MR_NO) AS MR_NO," +
									"MAX(BOX_ESL_ID) AS BOX_ESL_ID," +
									"MAX(STATION_CODE) AS STATION_CODE " +
								"FROM ODI_DSPNM "+
								"WHERE INTGMED_NO = '" + intgmedNo + "' AND STATION_CODE = '" + stationCode + "' " +
										"AND (ORDER_CAT1_CODE = 'PHA_W' OR ORDER_CAT1_CODE = 'PHA_C') " +
										"AND  NOT (LINK_NO IS NOT NULL AND ROUTE_CODE IN ('IVP','IVD','TPN')) "+
								"GROUP BY CASE_NO ) A," +
							"SYS_STATION B, " +
							"SYS_BED C," +
							"SYS_PATINFO D,"+
							"IND_MEDBOX E "+
							"WHERE B.STATION_CODE = A.STATION_CODE " +
									"AND E.CASE_NO(+) = A.CASE_NO " +									
									"AND C.BED_NO = A.BED_NO " +
									"AND D.MR_NO = A.MR_NO "+
							"ORDER BY A.BED_NO";
    	
    	TParm result = new TParm(this.select(sql));

		return result;
    }
    
    /**根据统药单号查询病人-用药信息-普药（非静配）-摆药*/
    public String getOrderCodeInfoDetailByPation(String intgmedNo,String caseNO) {
        return 	" SELECT A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM, "
				        + "        A.ORDER_DESC || ' ' || CASE WHEN A.SPECIFICATION IS NOT NULL THEN A.SPECIFICATION ELSE '' END  AS ORDER_DESC , "
				        + " 		TO_CHAR(A.MEDI_QTY ,'FM9999990.099') || '' || B.UNIT_CHN_DESC AS MEDI_QTY, C.ROUTE_CHN_DESC,D.FREQ_CHN_DESC,A.TAKE_DAYS, "
				        + " 		TO_CHAR(A.DISPENSE_QTY,'FM9999990.099') || '' || E.UNIT_CHN_DESC AS DISPENSE_QTY,A.OWN_AMT "
				+ " FROM ODI_DSPNM A,SYS_UNIT B,SYS_PHAROUTE C,SYS_PHAFREQ D,SYS_UNIT E  "
				+ " WHERE A.INTGMED_NO ='" + intgmedNo + "' AND A.CASE_NO='" + caseNO+ "' AND  NOT (A.LINK_NO IS NOT NULL AND A.ROUTE_CODE IN ('IVP','IVD','TPN')) " 
				        + "       AND B.UNIT_CODE=A.MEDI_UNIT AND C.ROUTE_CODE=A.ROUTE_CODE AND D.FREQ_CODE=A.FREQ_CODE AND E.UNIT_CODE=A.DISPENSE_UNIT "
				        + " ORDER BY A.ORDER_CODE ";
    }

    
    
    
    
    
    
    
    /**根据电子标签 将该药盒CASE_NO、STATION_CODE字段置为null*/
    public TParm clearInPatBoxCaseNoByEletagCode(String eletagCode) {
//    	String sql ="UPDATE IND_INPATBOX " +
//    					"SET CASE_NO = NULL ,STATION_CODE = NULL " +
//    				"WHERE ELETAG_CODE='"+eletagCode+"'";
    	String sql ="DELETE IND_INPATBOX " +
					"WHERE ELETAG_CODE='"+eletagCode+"'";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
    
    
    /**根据电子标签 将该IND_INPATBOX药盒CASE_NO、STATION_CODE字段更新*/
    public TParm updateIndPatBoxCaseNoByEletagCode(String eletagCode, String caseNo,String stationCode) {
    	String sql ="UPDATE IND_INPATBOX " +
    					"SET CASE_NO = '" + caseNo + "' ," +
    						"STATION_CODE = '" + stationCode + "' " +
    				"WHERE ELETAG_CODE='"+eletagCode+"'";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
    
    /**根据药盒标签查询当前该药盒绑定的病患caseNo*/
    public TParm queryBoxByEletag(String eleTag) {
    	String sql = "SELECT CASE_NO," +
    						"ELETAG_CODE," +
    						"STATION_CODE " +
    				"FROM IND_INPATBOX " +
    					"WHERE ELETAG_CODE = '" + eleTag + "'";
    	
    	TParm result = new TParm(this.select(sql));

		return result;
    }
    
    /**新增caseNo-药盒电子标签记录*/
    public TParm insertInPatBoxBox(String eleTagCode,String caseNo,String stationCode, 
									String optUser, String optTerm) {
    	String sql = "INSERT INTO IND_INPATBOX " +
    					"VALUES('" + eleTagCode + "','" + 
    								caseNo + "','" + 
    								stationCode + "','" + 
    								optUser + "',SYSDATE,'" + 
    								optTerm + "') ";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
    
    
    
    /**查询住院药房AP_REGION*/
    public TParm queryApRegion() {
    	String sql = "SELECT " +
    						"AP_REGION " +
    				"FROM IND_SYSPARM ";
    	
    	TParm result = new TParm(this.select(sql));
    	
		return result;
    }
    
    /**新增IND_MEDBOX电子标签*/
    public TParm insertBoxLable(String eleTagCode,String apRegion,String optUser, String optTerm) {
    	String sql = "INSERT INTO IND_MEDBOX (ELETAG_CODE,AP_REGION,OPT_USER,OPT_DATE,OPT_TERM) " +
    					"VALUES('" + eleTagCode + "','" + 
    								apRegion + "','" + 
    								optUser + "',SYSDATE,'" + 
    								optTerm + "') ";
    	
    	TParm result = new TParm(TJDODBTool.getInstance().update(sql));
    	
    	return result;
    }
    
}
