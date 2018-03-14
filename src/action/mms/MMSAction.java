package action.mms;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.reg.SchDayTool;
import jdo.sys.SystemTool;

import com.dongyang.action.TAction;
import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;

/**
*
* <p>Title: 提供挂号接口类</p>
*
* <p>Description: 为其他系统提供挂号接口</p>
*
* <p>Copyright: Copyright (c) 2008</p>
*
* <p>Company: JavaHis</p>
*
* @author liuzhen 2012.8.1
* @version 1.0
*/
public class MMSAction extends TAction {
	
	//vip 从 REG_CLICENUE 表取出QUE_NO 然后加1，然后QUE_STATUS 改为Y
	//vip 挂号表增加记录 需要QUE_NO,VIP_FLG
	/**vip挂号*/
	public TParm onSaveRegVip(TParm parm){
				
		String caseNo = SystemTool.getInstance().getNo("ALL", "REG","CASE_NO", "CASE_NO") ;
		String admType = parm.getValue("admType");
			if(null!=admType)admType.trim();
		String mrNo = parm.getValue("mrNo");
		String regionCode = parm.getValue("regionCode");
		String admDate = parm.getValue("admDate");
		
			Timestamp sysDate = SystemTool.getInstance().getDate() ;
		String regDate = sysDate.toString() ;
		String sessionCode = parm.getValue("sessionCode");
		String clinicArea = parm.getValue("clinicArea");
		String clinicRoomNo = parm.getValue("clinicRoomNo");
		String clinicTypeCode = parm.getValue("clinicTypeCode");
		
	    int queNo = 0 ;
	    String deptCode = parm.getValue("deptCode");
	    String drCode = parm.getValue("drCode");
	    String realDeptCode = deptCode;
	    String realDrCOde = drCode;
	    
	    String appCode = null; //预约当诊需要判断
	    String visitCode = "1";
	    String regMethodCode = "M";
	    String ctz1Code = null;
	    String ctz2Code = null;
	    
	    String ctz3Code = null;
	    String arriveFlg = "N";
	    String admRegion = regionCode;
	    String heatFlg = "N";
	    String admStatus = "1";
	    
	    String reportStatus = "1";
	    int weight = 0;
	    int height = 0;
	    String seeDrFlg = "N";
	    String optUser = parm.getValue("optUser");

	    String optDate = sysDate.toString() ;
	    String optTerm = parm.getValue("optTerm");
	    String serviceLevel = "1";
	    String regAdmTime = null;
	    
		TParm resultParm = new TParm();
	        
	    TConnection connection = this.getConnection();	
	    
	    
	    /**判断当诊，预约*/
	    SimpleDateFormat formate = new SimpleDateFormat("yyyyMMdd");
	    Date today = new Date();
	    Date admDay;
		
	    try {
			admDay = formate.parse(admDate);
		} catch (ParseException e) {
			connection.close();
			resultParm.setErrCode(-10);
			return resultParm;
		}		
	    
	    if(admDay.getYear()> today.getYear()){
	    	appCode = "N";
	    }else if(admDay.getYear()== today.getYear()){
	    	if(admDay.getMonth()> today.getMonth()){
	    		appCode = "N";
	    	}else if(admDay.getMonth()== today.getMonth()){
	    		if(admDay.getDate()> today.getDate()){
	    			appCode = "N";	
	    		}else if(admDay.getDate()== today.getDate()){
		    											    
			    	String appCodeSql = "SELECT SESSION_CODE,START_CLINIC_TIME,END_CLINIC_TIME FROM REG_SESSION WHERE ADM_TYPE='O' AND SESSION_CODE IN ('01','02')";
				    
					TParm appCodeResult = new TParm(TJDODBTool.getInstance().select(appCodeSql));		
					if (appCodeResult.getErrCode()<0) {
						connection.close();
						resultParm.setErrCode(appCodeResult.getErrCode());
						return resultParm;
					}else{
						for(int k=0; k<appCodeResult.getCount(); k++){

							String appSessionCode = appCodeResult.getValue("SESSION_CODE",0);
							String startClinicTime = appCodeResult.getValue("START_CLINIC_TIME",0);
							String endClinicTime = appCodeResult.getValue("END_CLINIC_TIME",0);
							
							if(sessionCode.equals(appSessionCode)){
								
								int startH = Integer.parseInt(startClinicTime.substring(0,2));
								int stratM = Integer.parseInt(startClinicTime.substring(3,5));
								int endH = Integer.parseInt(endClinicTime.substring(0,2));
								int endM = Integer.parseInt(endClinicTime.substring(3,5));
								
								if(endH > today.getHours() && today.getHours() > startH ){
									appCode = "Y";
								}else if(today.getHours()== startH && today.getMinutes()> stratM){
									appCode = "Y";
								}else if(today.getHours()== endH && today.getMinutes()< endM){
									appCode = "Y";
								}else{
									appCode = "N";
								}
							}
						}
					}
	    		}
	    	}
	    }
	
	   
	    /**获得病人更多信息*/
	    String patInfoSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
	    
		TParm patInfoResult = new TParm(TJDODBTool.getInstance().select(patInfoSql));		
		if (patInfoResult.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(patInfoResult.getErrCode());
			return resultParm;
		}else{
			ctz1Code = patInfoResult.getValue("CTZ1_CODE",0);
			ctz2Code = patInfoResult.getValue("CTZ2_CODE",0);
			ctz3Code = patInfoResult.getValue("CTZ3_CODE",0);
		}

		/**VIP就诊号*/	    
	   String vipQueNoSql="SELECT QUE_NO,START_TIME REG_ADM_TIME  "+
	   			"FROM REG_CLINICQUE "+
				"WHERE ADM_TYPE='"+admType +
					"' AND ADM_DATE='"+admDate +
					"' AND SESSION_CODE='"+sessionCode+
					"' AND CLINICROOM_NO='"+clinicRoomNo+
					"' AND QUE_STATUS='N' ORDER BY QUE_NO";

		TParm vipQueNoResult = new TParm(TJDODBTool.getInstance().select(vipQueNoSql));		
		if (vipQueNoResult.getErrCode()<0 || vipQueNoResult.getInt("QUE_NO", 0)<=0) {
			connection.close();
			resultParm.setErrCode(-10);
			 return resultParm;
		}

		queNo = vipQueNoResult.getInt("QUE_NO", 0);
		regAdmTime = vipQueNoResult.getValue("REG_ADM_TIME", 0);
		
		/**更新VIP就诊号*/
		String updateVipQueNoSql1 = "UPDATE REG_SCHDAY SET "+
									"QUE_NO=QUE_NO + 1 "+
								"WHERE REGION_CODE = '"+regionCode+"' "+
									"AND ADM_TYPE = '"+admType+"' "+
									"AND ADM_DATE = '"+admDate+"' "+
									"AND SESSION_CODE = '"+sessionCode+"' "+
									"AND CLINICROOM_NO='"+clinicRoomNo+"'";
		TParm updateVipQueNoResult1 = new TParm(TJDODBTool.getInstance().update(updateVipQueNoSql1, connection));     
	         
		if (updateVipQueNoResult1.getErrCode() < 0) {
			connection.close();
			resultParm.setErrCode(updateVipQueNoResult1.getErrCode());
			 return resultParm;			
		}
		
		String updateVipQueNoSql2 = "UPDATE REG_CLINICQUE SET "+
										"QUE_STATUS='Y' "+
									"WHERE ADM_TYPE = '"+admType+"' "+
										"AND ADM_DATE = '"+admDate+"' "+
										"AND SESSION_CODE = '"+sessionCode+"' "+
										"AND CLINICROOM_NO='"+clinicRoomNo+"'"+
										"AND QUE_NO='"+queNo+"'";
		TParm updateVipQueNoResult2 = new TParm(TJDODBTool.getInstance().update(updateVipQueNoSql2, connection));     
		
		if (updateVipQueNoResult2.getErrCode() < 0) {
			connection.close();
			resultParm.setErrCode(updateVipQueNoResult2.getErrCode());
			return resultParm;			
		}       

		
		/**挂号主档插入记录*/
		String insertPatAdmSql = 
			"INSERT INTO REG_PATADM ("+
				"CASE_NO, ADM_TYPE, MR_NO, REGION_CODE, ADM_DATE, "+
				"REG_DATE, SESSION_CODE, CLINICAREA_CODE, CLINICROOM_NO, CLINICTYPE_CODE, "+
				"QUE_NO, DEPT_CODE, DR_CODE, REALDEPT_CODE, REALDR_CODE, "+
				"APPT_CODE, VISIT_CODE, REGMETHOD_CODE, CTZ1_CODE, CTZ2_CODE, "+
				"CTZ3_CODE, ARRIVE_FLG, ADM_REGION, HEAT_FLG, ADM_STATUS, "+
				"REPORT_STATUS, WEIGHT, HEIGHT, SEE_DR_FLG, OPT_USER, "+
				"OPT_DATE, OPT_TERM,SERVICE_LEVEL,REG_ADM_TIME) "+
			"VALUES ('"+caseNo+"' ,'"+admType+"' ,'"+mrNo+"' ,'"+regionCode+"' ,TO_DATE('"+admDate+" 00:00:00','YYYYMMDD HH24:mi:ss'),"+
					"TO_DATE('"+regDate.substring(0, 19)+"','YYYY-MM-DD HH24:mi:ss'),'"+sessionCode+"' ,'"+clinicArea+"' ,'"+clinicRoomNo+"' ,'"+clinicTypeCode+"' ,"+
					queNo+" ,'"+deptCode+"' ,'"+drCode+"' ,'"+realDeptCode+"' ,'"+realDrCOde+"' ,"+
					"'"+appCode+"'," + "'"+visitCode+"'," + "'"+regMethodCode+"'," + "'"+ctz1Code+"'," + "'"+ctz2Code+"'," +
					"'"+ctz3Code+"'," + "'"+arriveFlg+"'," + "'"+admRegion+"'," + "'"+heatFlg+"'," + "'"+admStatus+"'," +
					"'"+reportStatus+"'," + ""+weight+"," + ""+height+"," + "'"+seeDrFlg+"'," + "'"+optUser+"'," +
					"TO_DATE('"+optDate.substring(0, 19)+"','YYYY-MM-DD HH24:mi:ss')," + "'"+optTerm+"'," + "'"+serviceLevel+"'," +"'"+regAdmTime+"'"+
				")";			
		//System.out.println("--VIP---insertPatAdmSql-----"+insertPatAdmSql); 
		TParm insertPatAdmResult = new TParm(TJDODBTool.getInstance().update(insertPatAdmSql, connection));
		
		if (insertPatAdmResult.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(insertPatAdmResult.getErrCode());
			 return resultParm;
		}	
		
		connection.commit() ;
		connection.close() ;
		resultParm.setData("CASE_NO",queNo);
	    return resultParm ;
	}
	
	
	/**非vip挂号*/
	public TParm onSaveReg(TParm parm){
		
		String caseNo = SystemTool.getInstance().getNo("ALL", "REG","CASE_NO", "CASE_NO") ;
		String admType = parm.getValue("admType");
			if(null!=admType)admType.trim();
		String mrNo = parm.getValue("mrNo");
		String regionCode = parm.getValue("regionCode");
		String admDate = parm.getValue("admDate");
		
			Timestamp sysDate = SystemTool.getInstance().getDate() ;
		String regDate = sysDate.toString() ;
		String sessionCode = parm.getValue("sessionCode");
		String clinicArea = parm.getValue("clinicArea");
		String clinicRoomNo = parm.getValue("clinicRoomNo");
		String clinicTypeCode = parm.getValue("clinicTypeCode");
		
	    int queNo ;
	    String deptCode = parm.getValue("deptCode");
	    String drCode = parm.getValue("drCode");
	    String realDeptCode = deptCode;
	    String realDrCOde = drCode;
	    
	    String appCode = null; //预约当诊需要判断
	    String visitCode = "1";
	    String regMethodCode = "M";
	    String ctz1Code = null;
	    String ctz2Code = null;
	    
	    String ctz3Code = null;
	    String arriveFlg = "N";
	    String admRegion = regionCode;
	    String heatFlg = "N";
	    String admStatus = "1";
	    
	    String reportStatus = "1";
	    int weight = 0;
	    int height = 0;
	    String seeDrFlg = "N";
	    String optUser = parm.getValue("optUser");

	    String optDate = sysDate.toString() ;
	    String optTerm = parm.getValue("optTerm");
	    String serviceLevel = "1";
	    
	    /**返回结果集*/
		TParm resultParm = new TParm();
	        
	    TConnection connection = this.getConnection();	
	    
	    
	    /**判断当诊，预约*/
	    SimpleDateFormat formate = new SimpleDateFormat("yyyyMMdd");
	    Date today = new Date();
	    Date admDay;
		
	    try {
			admDay = formate.parse(admDate);
		} catch (ParseException e) {
			connection.close();
			resultParm.setErrCode(-10);
			return resultParm;
		}		
	    
	    if(admDay.getYear()> today.getYear()){
	    	appCode = "N";
	    }else if(admDay.getYear()== today.getYear()){
	    	if(admDay.getMonth()> today.getMonth()){
	    		appCode = "N";
	    	}else if(admDay.getMonth()== today.getMonth()){
	    		if(admDay.getDate()> today.getDate()){
	    			appCode = "N";	
	    		}else if(admDay.getDate()== today.getDate()){
		    											    
			    	String appCodeSql = "SELECT SESSION_CODE,START_CLINIC_TIME,END_CLINIC_TIME FROM REG_SESSION WHERE ADM_TYPE='O' AND SESSION_CODE IN ('01','02')";
				    
					TParm appCodeResult = new TParm(TJDODBTool.getInstance().select(appCodeSql));		
					if (appCodeResult.getErrCode()<0) {
						connection.close();
						resultParm.setErrCode(appCodeResult.getErrCode());
						return resultParm;
					}else{
						for(int k=0; k<appCodeResult.getCount(); k++){

							String appSessionCode = appCodeResult.getValue("SESSION_CODE",0);
							String startClinicTime = appCodeResult.getValue("START_CLINIC_TIME",0);
							String endClinicTime = appCodeResult.getValue("END_CLINIC_TIME",0);
							
							if(sessionCode.equals(appSessionCode)){
								
								int startH = Integer.parseInt(startClinicTime.substring(0,2));
								int stratM = Integer.parseInt(startClinicTime.substring(3,5));
								int endH = Integer.parseInt(endClinicTime.substring(0,2));
								int endM = Integer.parseInt(endClinicTime.substring(3,5));
								
								if(endH > today.getHours() && today.getHours() > startH ){
									appCode = "Y";
								}else if(today.getHours()== startH && today.getMinutes()> stratM){
									appCode = "Y";
								}else if(today.getHours()== endH && today.getMinutes()< endM){
									appCode = "Y";
								}else{
									appCode = "N";
								}
							}
						}
					}
	    		}
	    	}
	    }
	   
	    /**获得病人更多信息*/
	    String patInfoSql = "SELECT * FROM SYS_PATINFO WHERE MR_NO='"+mrNo+"'";
	    
		TParm patInfoResult = new TParm(TJDODBTool.getInstance().select(patInfoSql));		
		if (patInfoResult.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(patInfoResult.getErrCode());
			return resultParm;
		}else{
			ctz1Code = patInfoResult.getValue("CTZ1_CODE",0);
			ctz2Code = patInfoResult.getValue("CTZ2_CODE",0);
			ctz3Code = patInfoResult.getValue("CTZ3_CODE",0);
		}
		
		/**从日排班表 获取que_no, max_que */
	    String queNoSql = "SELECT QUE_NO,MAX_QUE "+
	    				"FROM REG_SCHDAY "+
	    				"WHERE REGION_CODE='"+regionCode+"' "+
	    				"AND ADM_TYPE='"+admType+"' "+
	    				"AND ADM_DATE='"+admDate+"' "+
	    				"AND SESSION_CODE='"+sessionCode+"' "+
	    				"AND CLINICROOM_NO='"+clinicRoomNo+"'";
	    
		TParm queNoResult = new TParm(TJDODBTool.getInstance().select(queNoSql));		
		if (queNoResult.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(patInfoResult.getErrCode());
			return resultParm;
		}
		queNo = queNoResult.getInt("QUE_NO",0);
		int maxQue = queNoResult.getInt("MAX_QUE",0);;
		
		if (queNo>=maxQue) {
			connection.close();
			resultParm.setErrCode(-10);
			return resultParm;
		}
		
		/**更新que_no*/		
		String updateQueNoSql = "UPDATE REG_SCHDAY SET "+
									"QUE_NO=QUE_NO + 1 "+
								"WHERE REGION_CODE = '"+regionCode+"' "+
									"AND ADM_TYPE = '"+admType+"' "+
									"AND ADM_DATE = '"+admDate+"' "+
									"AND SESSION_CODE = '"+sessionCode+"' "+
									"AND CLINICROOM_NO='"+clinicRoomNo+"'";
	           		
        TParm updateQueNoResult = new TParm(TJDODBTool.getInstance().update(updateQueNoSql, connection));
        
		if (updateQueNoResult.getErrCode() < 0) {
			connection.close();
			resultParm.setErrCode(updateQueNoResult.getErrCode());
			 return resultParm;			
		}
		
		/**挂号主档插入数据*/
		String insertPatAdmSql = 
			"INSERT INTO REG_PATADM ("+
				"CASE_NO, ADM_TYPE, MR_NO, REGION_CODE, ADM_DATE, "+
				"REG_DATE, SESSION_CODE, CLINICAREA_CODE, CLINICROOM_NO, CLINICTYPE_CODE, "+
				"QUE_NO, DEPT_CODE, DR_CODE, REALDEPT_CODE, REALDR_CODE, "+
				"APPT_CODE, VISIT_CODE, REGMETHOD_CODE, CTZ1_CODE, CTZ2_CODE, "+
				"CTZ3_CODE, ARRIVE_FLG, ADM_REGION, HEAT_FLG, ADM_STATUS, "+
				"REPORT_STATUS, WEIGHT, HEIGHT, SEE_DR_FLG, OPT_USER, "+
				"OPT_DATE, OPT_TERM,SERVICE_LEVEL) "+
			"VALUES ('"+caseNo+"' ,'"+admType+"' ,'"+mrNo+"' ,'"+regionCode+"' ,TO_DATE('"+admDate+" 00:00:00','YYYYMMDD HH24:mi:ss'),"+
					"TO_DATE('"+regDate.substring(0, 19)+"','YYYY-MM-DD HH24:mi:ss'),'"+sessionCode+"' ,'"+clinicArea+"' ,'"+clinicRoomNo+"' ,'"+clinicTypeCode+"' ,"+
					queNo+" ,'"+deptCode+"' ,'"+drCode+"' ,'"+realDeptCode+"' ,'"+realDrCOde+"' ,"+
					"'"+appCode+"'," + "'"+visitCode+"'," + "'"+regMethodCode+"'," + "'"+ctz1Code+"'," + "'"+ctz2Code+"'," +
					"'"+ctz3Code+"'," + "'"+arriveFlg+"'," + "'"+admRegion+"'," + "'"+heatFlg+"'," + "'"+admStatus+"'," +
					"'"+reportStatus+"'," + ""+weight+"," + ""+height+"," + "'"+seeDrFlg+"'," + "'"+optUser+"'," +
					"TO_DATE('"+optDate.substring(0, 19)+"','YYYY-MM-DD HH24:mi:ss')," + "'"+optTerm+"'," + "'"+serviceLevel+"'" +
				")";	
		
		 //System.out.println("--非VIP---insertPatAdmSql-----"+insertPatAdmSql);  
		
		 TParm insertPatAdmResult = new TParm(TJDODBTool.getInstance().update(insertPatAdmSql, connection));
		
		if (insertPatAdmResult.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(insertPatAdmResult.getErrCode());
			 return resultParm;
		}	
		
		connection.commit() ;
		connection.close() ;
		resultParm.setData("CASE_NO",queNo);
		return resultParm;
	}
	
	/**退挂号*/
	public TParm cancelReg(TParm parm){
		
		TParm resultParm = new TParm();

        String caseNo = parm.getValue("caseNo");
	    String admDate = parm.getValue("admDate");
	    String admDateStr = admDate.replace("-", "");
	    String sessionCode = parm.getValue("sessionCode");
	    String admType = parm.getValue("admType");
	    String deptCode = parm.getValue("deptCode");
	    String regionCode = parm.getValue("regionCode");
	    String clinicRoomNo = parm.getValue("clinicRoomNo");
	    String queNo = parm.getValue("queNo");
	    
	    TConnection connection = this.getConnection();

		String sql = "UPDATE REG_PATADM SET "+
				"REGCAN_USER='MMS',"+
				"REGCAN_DATE = sysdate  "+
				"WHERE CASE_NO='"+caseNo+"'";
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sql, connection));
		
		if (result.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(-10);
			 return resultParm;
		}	
		
		String sql2 = 
			"UPDATE REG_SCHDAY SET QUE_NO=QUE_NO-1 "+
				"WHERE ADM_DATE='"+admDateStr+"' "+
				"AND ADM_TYPE='"+admType+"' "+
				"AND SESSION_CODE='"+sessionCode+"' "+
				"AND DEPT_CODE='"+deptCode+"' "+
				"AND REGION_CODE='"+regionCode+"'";
		   
		TParm result2 = new TParm(TJDODBTool.getInstance().update(sql2, connection));
		
		if (result2.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(-10);
			 return resultParm;
		}
		
		String sql3 = 
			"UPDATE REG_CLINICQUE SET QUE_STATUS = 'N' "+
				"WHERE ADM_DATE='"+admDateStr+"' "+
				"AND ADM_TYPE='"+admType+"' "+
				"AND SESSION_CODE='"+sessionCode+"' "+
				"AND CLINICROOM_NO='"+clinicRoomNo+"' "+
				"AND QUE_NO='"+queNo+"'";
		   
		TParm result3 = new TParm(TJDODBTool.getInstance().update(sql3, connection));
		
		if (result3.getErrCode()<0) {
			connection.close();
			resultParm.setErrCode(-10);
			 return resultParm;
		}
		
		connection.commit() ;
		connection.close() ;
		return resultParm;
	}

}
