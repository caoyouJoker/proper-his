package jdo.ind;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.javahis.ui.spc.util.StringUtils;
/**
 * <p>
 * Title:非医保贵重药品重点监测品种报表Tool
 * </p>
 * 
 * <p>
 * Description:非医保贵重药品重点监测品种报表Tool
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author wukai 2016-10-26
 * @version JavaHis 1.0
 */
public class INDNoInsValueDrugsTool extends TJDOTool {
	
	private static INDNoInsValueDrugsTool mInstance;
	
	public static INDNoInsValueDrugsTool getNewInstance(){
		if(mInstance == null) {
			mInstance = new INDNoInsValueDrugsTool();
		}
		return mInstance;
	}

	public INDNoInsValueDrugsTool() {
		//this.setModuleName("ind\\INDDispenseDModule.x");
		onInit();
	}
	
	/**
	 * 查询门诊用药
	 * @param parm
	 * @return
	 */
	public TParm onQueryNoInsMedA(TParm parm) {
		
		String timeSql = "";
		String date = String.valueOf(parm.getData("START_DATE"));
		if(!StringUtils.isEmpty(date)) {
			timeSql += " AND A.BILL_DATE >= TO_DATE( '" + date +  "' , 'YYYYMMDDHH24MISS') ";
		}
		date = String.valueOf(parm.getData("END_DATE"));
		if(!StringUtils.isEmpty(date)) {
			timeSql += " AND A.BILL_DATE <= TO_DATE( '"  +  date +  "' , 'YYYYMMDDHH24MISS') ";
		}
		
		String querySql = "";
		String temp = String.valueOf(parm.getData("DEPT_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND D.DEPT_CODE =  '" + temp +"' ";
		}
		temp = String.valueOf(parm.getData("DR_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND C.USER_ID = '" + temp + "' ";
		}
		temp = String.valueOf(parm.getData("ORDER_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND A.ORDER_CODE = '" + temp + "' ";
		}
		//String orderIn = "SELECT H.ORDER_CODE FROM IND_MONITOR_MED H WHERE H.MONITOR_TYPE = 'NVAL' AND H.ENABLE_FLG = 'Y' ";
		String sql =
		   " SELECT E.PAT_NAME,E.MR_NO,A.ORDER_CODE, B.ORDER_DESC, B.SPECIFICATION, A.DOSAGE_QTY, F.UNIT_CHN_DESC," +
	       " A.OWN_PRICE, A.AR_AMT AS TOT_AMT, C.USER_NAME, D.DEPT_CHN_DESC, A.BILL_DATE " +
	       " FROM (SELECT A.ORDER_CODE, A.DOSAGE_QTY, A.OWN_PRICE, A.AR_AMT, A.BILL_DATE, A.DR_CODE, A.DEPT_CODE, A.MR_NO, A.DOSAGE_UNIT " +
	       "   		FROM OPD_ORDER A " +
	       "  		WHERE A.CAT1_TYPE = 'PHA'  " +
	       			timeSql +
	       "    	AND A.ADM_TYPE = 'O') A, SYS_FEE B, SYS_OPERATOR C, SYS_DEPT D,SYS_PATINFO E,SYS_UNIT F,IND_MONITOR_MED H " +
	       " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DR_CODE = C.USER_ID AND A.DEPT_CODE = D.DEPT_CODE AND A.MR_NO = E.MR_NO AND A.DOSAGE_UNIT = F.UNIT_CODE " +
	       " AND B.ORDER_CODE = H.ORDER_CODE AND H.MONITOR_TYPE = 'NVAL' AND H.ENABLE_FLG = 'Y' " +
             querySql +
           " ORDER BY A.ORDER_CODE ";
	//	System.out.println("门诊用药 :::::::::::::    " + sql);
		
		TParm res = new TParm( TJDODBTool.getInstance().select(sql));
		return res;
	}
	
	/**
	 * 查询急诊用药
	 * @param parm
	 * @return
	 */
	public TParm onQueryNoInsMedB(TParm parm) {
		String timeSql = "";
		String date = String.valueOf(parm.getData("START_DATE"));
		if(!StringUtils.isEmpty(date)) {
			timeSql += " AND A.BILL_DATE >= TO_DATE( '" + date +  "' , 'YYYYMMDDHH24MISS') ";
		}
		date = String.valueOf(parm.getData("END_DATE"));
		if(!StringUtils.isEmpty(date)) {
			timeSql += " AND A.BILL_DATE <= TO_DATE( '"  +  date +  "' , 'YYYYMMDDHH24MISS') ";
		}
		
		String querySql = "";
		String temp = String.valueOf(parm.getData("DEPT_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND D.DEPT_CODE =  '" + temp +"' ";
		}
		temp = String.valueOf(parm.getData("DR_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND C.USER_ID = '" + temp + "' ";
		}
		temp = String.valueOf(parm.getData("ORDER_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND A.ORDER_CODE = '" + temp + "' ";
		}
		String sql =
		   " SELECT E.PAT_NAME,E.MR_NO, A.ORDER_CODE, B.ORDER_DESC, B.SPECIFICATION, A.DOSAGE_QTY, F.UNIT_CHN_DESC," +
	       " A.OWN_PRICE, A.AR_AMT AS TOT_AMT, C.USER_NAME, D.DEPT_CHN_DESC, A.BILL_DATE " +
	       " FROM (SELECT A.ORDER_CODE, A.DOSAGE_QTY, A.OWN_PRICE, A.AR_AMT, A.BILL_DATE, A.DR_CODE, A.DEPT_CODE, A.MR_NO, A.DOSAGE_UNIT " +
	       "   		FROM OPD_ORDER A " +
	       "  		WHERE A.CAT1_TYPE = 'PHA'  " +
	       			timeSql +
	       "    	AND A.ADM_TYPE = 'E') A, SYS_FEE B, SYS_OPERATOR C, SYS_DEPT D,SYS_PATINFO E,SYS_UNIT F,IND_MONITOR_MED H " +
	       " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DR_CODE = C.USER_ID AND A.DEPT_CODE = D.DEPT_CODE AND A.MR_NO = E.MR_NO AND A.DOSAGE_UNIT = F.UNIT_CODE " +
	       " AND B.ORDER_CODE = H.ORDER_CODE AND H.MONITOR_TYPE = 'NVAL' AND H.ENABLE_FLG = 'Y' " +
             querySql +
           " ORDER BY A.ORDER_CODE ";
	//	System.out.println("急诊用药 :::::::::::::    " + sql);
		TParm res = new TParm( TJDODBTool.getInstance().select(sql));
		return res;
	}
	
	/**
	 * 查询住院用药
	 * @param parm
	 * @return
	 */
	public TParm onQueryNoInsMedC(TParm parm) {
		String timeSql = "";
		String date = String.valueOf(parm.getData("START_DATE"));
		if(!StringUtils.isEmpty(date)) {
			timeSql += " AND A.BILL_DATE >= TO_DATE( '" + date +  "' , 'YYYYMMDDHH24MISS') ";
		}
		date = String.valueOf(parm.getData("END_DATE"));
		if(!StringUtils.isEmpty(date)) {
			timeSql += " AND A.BILL_DATE <= TO_DATE( '"  +  date +  "' , 'YYYYMMDDHH24MISS') ";
		}
		
		String querySql = "";
		String temp = String.valueOf(parm.getData("DEPT_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND D.DEPT_CODE =  '" + temp +"' ";
		}
		temp = String.valueOf(parm.getData("DR_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND C.USER_ID = '" + temp + "' ";
		}
		temp = String.valueOf(parm.getData("ORDER_CODE"));
		if(!StringUtils.isEmpty(temp)) {
			querySql += " AND A.ORDER_CODE = '" + temp + "' ";
		}
		String sql = " SELECT E.PAT_NAME,E.MR_NO, A.ORDER_CODE, B.ORDER_DESC, B.SPECIFICATION, A.DOSAGE_QTY, F.UNIT_CHN_DESC, " +
	                 " A.OWN_PRICE, A.TOT_AMT, C.USER_NAME, D.DEPT_CHN_DESC, A.BILL_DATE " +
	                 " FROM (SELECT A.ORDER_CODE, A.DOSAGE_QTY, A.OWN_PRICE, A.TOT_AMT, A.BILL_DATE, A.DR_CODE, " +
	                 "         A.DEPT_CODE, A.CASE_NO, A.DOSAGE_UNIT FROM IBS_ORDD A " +
	                 "         WHERE A.CAT1_TYPE = 'PHA' " +
	                           timeSql +
		             "         ) A, SYS_FEE B, SYS_OPERATOR C, SYS_DEPT D, MRO_RECORD E, SYS_UNIT F, IND_MONITOR_MED H " +
	                 " WHERE A.ORDER_CODE = B.ORDER_CODE AND A.DR_CODE = C.USER_ID AND A.DEPT_CODE = D.DEPT_CODE AND A.CASE_NO = E.CASE_NO " +
	                 "       AND A.DOSAGE_UNIT = F.UNIT_CODE " +
	                 " AND B.ORDER_CODE = H.ORDER_CODE AND H.MONITOR_TYPE = 'NVAL' AND H.ENABLE_FLG = 'Y' " +
	  	              querySql +
	  	             " ORDER BY A.ORDER_CODE ";
	//	System.out.println("住院用药 :::::::::::::    " + sql);        
		TParm res = new TParm( TJDODBTool.getInstance().select(sql));
		return res;
	}
	
}
