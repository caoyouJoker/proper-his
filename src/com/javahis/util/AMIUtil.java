package com.javahis.util;

import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;

public class AMIUtil {
	public static boolean isE02byTriageNo(String triageNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
	            "SELECT A.TRIAGE_NO,REG.CASE_NO,B.IN_CASE_NO,B.OPD_CASE_NO FROM ERD_EVALUTION A LEFT JOIN AMI_PREERD_INFO ERD ON A.TRIAGE_NO=ERD.TRIAGE_NO "+
				"LEFT JOIN REG_PATADM REG on A.CASE_NO=REG.CASE_NO "+
				"LEFT JOIN ADM_RESV B on A.CASE_NO=B.OPD_CASE_NO "+
				"WHERE REG.ENTER_ROUTE = 'E02' "+
				"AND A.TRIAGE_NO='" + triageNo + "'"));
		if(parm.getCount()>0){
	    	return true;
	    }else{
	    	return false;
	    }
	}
	public static boolean isE02byRegCaseNo(String regCaseNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT A.TRIAGE_NO,REG.CASE_NO,B.IN_CASE_NO,B.OPD_CASE_NO FROM ERD_EVALUTION A LEFT JOIN AMI_PREERD_INFO ERD ON A.TRIAGE_NO=ERD.TRIAGE_NO "+
						"LEFT JOIN REG_PATADM REG on A.CASE_NO=REG.CASE_NO "+
						"LEFT JOIN ADM_RESV B on A.CASE_NO=B.OPD_CASE_NO "+
						"WHERE REG.ENTER_ROUTE = 'E02' "+
						"AND REG.CASE_NO='" + regCaseNo + "'"));
		if(parm.getCount()>0){
	    	return true;
	    }else{
	    	return false;
	    }
	}	
	public static boolean isE02byInCaseNo(String inCaseNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT A.TRIAGE_NO,REG.CASE_NO,B.IN_CASE_NO,B.OPD_CASE_NO FROM ERD_EVALUTION A LEFT JOIN AMI_PREERD_INFO ERD ON A.TRIAGE_NO=ERD.TRIAGE_NO "+
						"LEFT JOIN REG_PATADM REG on A.CASE_NO=REG.CASE_NO "+
						"LEFT JOIN ADM_RESV B on A.CASE_NO=B.OPD_CASE_NO "+
						"WHERE REG.ENTER_ROUTE = 'E02' "+
						"AND B.IN_CASE_NO='" + inCaseNo + "'"));
	    if(parm.getCount()>0){
	    	return true;
	    }else{
	    	return false;
	    }
	}	
	public static TParm getE02byTriageNo(String triageNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
	            "SELECT A.TRIAGE_NO,REG.CASE_NO,B.IN_CASE_NO,B.OPD_CASE_NO,REG.ENTER_ROUTE,ER.CHN_DESC as ER_DESC,REG.PATH_KIND,PK.CHN_DESC as PK_DESC "+
		        "FROM ERD_EVALUTION A LEFT JOIN AMI_PREERD_INFO ERD ON A.TRIAGE_NO=ERD.TRIAGE_NO "+
				"LEFT JOIN REG_PATADM REG on A.CASE_NO=REG.CASE_NO "+
				"LEFT JOIN ADM_RESV B on A.CASE_NO=B.OPD_CASE_NO "+
				"LEFT JOIN SYS_DICTIONARY ER on REG.ENTER_ROUTE=ER.ID AND ER.GROUP_ID='ENTER_ROUTE' "+
	            "LEFT JOIN SYS_DICTIONARY PK on REG.PATH_KIND=PK.ID and PK.GROUP_ID='PATH_KIND' "+
				"WHERE A.TRIAGE_NO='" + triageNo + "'"));
		if(parm.getCount()>0){
	    	return parm;
	    }else{
	    	return null;
	    }
	}
	public static TParm getE02byRegCaseNo(String regCaseNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT A.TRIAGE_NO,REG.CASE_NO,B.IN_CASE_NO,B.OPD_CASE_NO,REG.ENTER_ROUTE,ER.CHN_DESC as ER_DESC,REG.PATH_KIND,PK.CHN_DESC as PK_DESC "+
		        "FROM ERD_EVALUTION A LEFT JOIN AMI_PREERD_INFO ERD ON A.TRIAGE_NO=ERD.TRIAGE_NO "+
						"LEFT JOIN REG_PATADM REG on A.CASE_NO=REG.CASE_NO "+
						"LEFT JOIN ADM_RESV B on A.CASE_NO=B.OPD_CASE_NO "+
						"LEFT JOIN SYS_DICTIONARY ER on REG.ENTER_ROUTE=ER.ID AND ER.GROUP_ID='ENTER_ROUTE' "+
			            "LEFT JOIN SYS_DICTIONARY PK on REG.PATH_KIND=PK.ID and PK.GROUP_ID='PATH_KIND' "+
						"WHERE  REG.CASE_NO='" + regCaseNo + "'"));
		if(parm.getCount()>0){
	    	return parm;
	    }else{
	    	return null;
	    }
	}	
	public static TParm getE02byInCaseNo(String inCaseNo){
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				"SELECT A.TRIAGE_NO,REG.CASE_NO,B.IN_CASE_NO,B.OPD_CASE_NO,REG.ENTER_ROUTE,ER.CHN_DESC as ER_DESC,REG.PATH_KIND,PK.CHN_DESC as PK_DESC "+
		        "FROM ERD_EVALUTION A LEFT JOIN AMI_PREERD_INFO ERD ON A.TRIAGE_NO=ERD.TRIAGE_NO "+
						"LEFT JOIN REG_PATADM REG on A.CASE_NO=REG.CASE_NO "+
						"LEFT JOIN ADM_RESV B on A.CASE_NO=B.OPD_CASE_NO "+
						"LEFT JOIN SYS_DICTIONARY ER on REG.ENTER_ROUTE=ER.ID AND ER.GROUP_ID='ENTER_ROUTE' "+
			            "LEFT JOIN SYS_DICTIONARY PK on REG.PATH_KIND=PK.ID and PK.GROUP_ID='PATH_KIND' "+
						"WHERE  B.IN_CASE_NO='" + inCaseNo + "'"));
	    if(parm.getCount()>0){
	    	return parm;
	    }else{
	    	return null;
	    }
	}
}
