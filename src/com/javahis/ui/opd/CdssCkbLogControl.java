package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * 
 * <p>
 * Title: 知识ID管控LOG查询
 * </p>
 * 
 * <p>
 * Description:知识ID管控LOG查询
 * </p>
 * 
 * 
 * <p>
 * Company:blucore 
 * </p>
 * 
 * @author huangtt 20150817
 * @version 1.0
 */
public class CdssCkbLogControl extends TControl {
	private TTable table;
	private String deptCode;
	private String drCode;

	public void onInit() {
		table = (TTable) getComponent("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", StringTool.rollDate(date, -30).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
	}
	
	public void onQuery(){
		
		 String date_s = getValueString("START_DATE");
	        String date_e = getValueString("END_DATE");
	        date_s =
	                date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "").replace("-", "")
	                        .replace(" ", "");
	        date_e =
	                date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "").replace("-", "")
	                        .replace(" ", "");
	        deptCode = this.getValueString("DEPT_CODE");
	        drCode = this.getValueString("DR_CODE");
		
		String sql = "SELECT A.CKB_ID,A.RISK_LEVEL,A.ADVISE, A.DEPT_CODE,A.DR_CODE," +
				" CASE WHEN A.ADM_TYPE = 'I' THEN '住院' ELSE '门诊' END ADM_TYPE," +
				" A.LOG_DATE,A.MR_NO,B.PAT_NAME,A.ORDER_CODE, C.ORDER_DESC," +
				" A.ORDER_SEQ,A.ORDER_NO,A.CASE_NO" +
				" FROM DSS_CKBLOG A, SYS_PATINFO B, SYS_FEE C" +
				" WHERE A.MR_NO = B.MR_NO AND A.ORDER_CODE = C.ORDER_CODE(+)" +
				" AND A.LOG_DATE BETWEEN TO_DATE('"+date_s+"','YYYYMMDDHH24MISS') " +
				" AND  TO_DATE('"+date_e+"','YYYYMMDDHH24MISS') ";
		if(deptCode.length()>0){
			sql += " AND A.DEPT_CODE='"+deptCode+"'";
		}
		
		if(drCode.length()>0){
			sql += " AND A.DR_CODE='"+drCode+"'";
		}
		
		sql += " ORDER BY A.LOG_DATE DESC";
		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()<0){
			this.messageBox("没有要查询的数据");
			table.removeRowAll();
			return;
			
		}
		table.setParmValue(parm);
		
	}
	public void onClear(){
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("START_DATE", StringTool.rollDate(date, -30).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.clearValue("DEPT_CODE;DR_CODE");
		deptCode="";
		drCode="";
		table.removeRowAll();
	}

}
