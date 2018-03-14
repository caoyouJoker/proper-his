package action.sta;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sta.STAZeroInHosTool;
import jdo.sys.Operator;

import com.dongyang.data.TParm;
import com.dongyang.db.TConnection;
import com.dongyang.db.TDBPoolManager;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.patch.Patch;
import com.dongyang.util.StringTool;
/**
 * <p>
 * Title: 0点在院病人Actoin
 * </p>
 * 
 * <p>
 * Description: 0点在院病人Actoin
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wukai 20160901
 * @version 1.0
 */
public class STAZeroInHosAction extends Patch{
	
	
	public STAZeroInHosAction() {
		
	}
	
	/**
	 * 定时执行
	 */
	@Override
	public boolean run() {
		TConnection connection = TDBPoolManager.getInstance().getConnection();
		//System.out.println("connection == null ? ____________"  +(connection == null));
	    Timestamp date = StringTool.getTimestamp(new Date());
	    //System.out.println("query date ::::::::::::::::::: " + date);
	    String queryDate = StringTool.getString(date, "yyyyMMdd") + "000000";
	    //20170328 lij 改
		String sql = "SELECT A.DEPT_CODE AS IN_DEPT_CODE, A.STATION_CODE AS IN_STATION_CODE, A.MR_NO AS MR_NO, " +
					  "B.PAT_NAME AS PAT_NAME, A.IN_DATE AS IN_DATE, A.VS_DR_CODE AS VS_DR_CODE,A.CASE_NO AS CASE_NO,A.CLNCPATH_CODE AS CLNCPATH_CODE,"+ 
					  "B.SEX_CODE AS SEX_CODE,B.BIRTH_DATE AS BIRTH_DATE,C.BED_NO_DESC AS BED_NO_DESC," +
					  "D.ICD_CHN_DESC AS ICD_CHN_DESC,A.CTZ1_CODE AS CTZ1_CODE,A.CTZ2_CODE AS CTZ2_CODE," +
					  "A.CUR_AMT AS CUR_AMT,A.SCHD_CODE AS SCHD_CODE,A.DISE_CODE AS DISE_CODE,A.DS_DATE AS DS_DATE "+
					  "FROM ADM_INP A, SYS_PATINFO B,SYS_BED C,SYS_DIAGNOSIS D " +
					  "WHERE A.DS_DATE IS NULL AND A.CANCEL_FLG = 'N' " +
					  "AND A.IN_DATE <= TO_DATE('" + queryDate + "','YYYYMMDDHH24MISS') " +
					  "AND A.MR_NO = B.MR_NO(+) " + 
					  "AND A.BED_NO=C.BED_NO AND A.MAINDIAG = D.ICD_CODE(+) "; 
//		System.out.println("sql ::::::::::::   " + sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if (parm.getCount() <= 0) {
	        connection.close();
	        return false;
	    }
		parm.setData("DAILY_DATE", date);
		//System.out.println("parm ::::::::::::   " + parm);
		try {
	        for (int i = 0; i < parm.getCount("MR_NO"); i++) {
	           insertSingleData(parm, i, connection) ;
	           
	        }
	    } catch (Exception e) {
	    	connection.rollback();
	        e.printStackTrace();
	    }
	    connection.commit();
	    connection.close();
		return true;
	}
	
	/**
	 * 插入单条数据
	 * @param parm : 数据
	 * @param row : 当前行
	 * @param connection : TConnection
	 * @return 
	 */

	//BED_NO_DESC,BIRTH_DATE,ICD_CHN_DESC,CTZ1_CODE,CTZ2_CODE,CUR_AMT,SCHD_CODE,DISE_CODE,SEX_CODE
	public boolean insertSingleData(TParm parm, int row, TConnection connection) {
		TParm actionParm = new TParm();
		actionParm.setData("IN_DEPT_CODE", parm.getData("IN_DEPT_CODE", row));
		actionParm.setData("IN_STATION_CODE", parm.getData("IN_STATION_CODE", row));
		actionParm.setData("MR_NO", parm.getData("MR_NO", row));
		actionParm.setData("PAT_NAME", parm.getData("PAT_NAME", row));
		actionParm.setData("IN_DATE", parm.getData("IN_DATE", row));
		actionParm.setData("VS_DR_CODE", parm.getData("VS_DR_CODE", row));
		actionParm.setData("OPT_USER",Operator.getID());
		actionParm.setData("OPT_TERM",Operator.getIP());
		actionParm.setData("CASE_NO",parm.getData("CASE_NO", row));
		actionParm.setData("CLNCPATH_CODE",parm.getData("CLNCPATH_CODE", row));
		Timestamp date = StringTool.getTimestamp(String.valueOf(parm.getData("DAILY_DATE")).substring(0, 10) + " 00:00:00", "yyyy-MM-dd HH:mm:ss");
		actionParm.setData("DAILY_DATE", date);
		//20170329 lij 改/***********
		actionParm.setData("BED_NO_DESC", parm.getData("BED_NO_DESC", row));
		actionParm.setData("DS_DATE", parm.getData("DS_DATE", row));
		actionParm.setData("BIRTH_DATE", parm.getData("BIRTH_DATE", row));
		actionParm.setData("ICD_CHN_DESC", parm.getData("ICD_CHN_DESC", row));
		actionParm.setData("CTZ1_CODE", parm.getData("CTZ1_CODE", row));
		actionParm.setData("CTZ2_CODE", parm.getData("CTZ2_CODE", row));
		actionParm.setData("CUR_AMT", parm.getData("CUR_AMT", row));
		actionParm.setData("SCHD_CODE", parm.getData("SCHD_CODE", row));
		actionParm.setData("DISE_CODE", parm.getData("DISE_CODE", row));
		actionParm.setData("SEX_CODE", parm.getData("SEX_CODE", row));
		//*************
		TParm result = STAZeroInHosTool.getNewInstance().insertData(actionParm, connection);
		//System.out.println(" insert result ::::::::::::  " +  result.getErrText());
		if (result.getErrCode() < 0) {
			//System.out.println(" result error text ::::::::::::  " +  result.getErrText());
	        connection.rollback();
	        connection.commit();
	        return false;
	    }
		return true;
		
	}
	
}
