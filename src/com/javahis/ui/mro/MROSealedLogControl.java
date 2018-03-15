package com.javahis.ui.mro;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;


/**
 * <p>Title: 封存病历日志查询 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: ProperSoft</p>
 *
 * @author huangtt 20161108
 * @version 1.0
 */
public class MROSealedLogControl extends TControl{
	
	TTable table;
	
	public void onInit(){
		
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("S_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("E_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		
		table = (TTable) this.getComponent("TABLE");
		
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm acceptData = (TParm) obj;
			this.setValue("MR_NO", acceptData.getValue("MR_NO"));
			this.onQuery();
	
		}
		
		
		
	}
	
	public void onQuery(){
		
		// 设置查询条件
		String date_s = getValueString("S_DATE");
		String date_e = getValueString("E_DATE");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
		.replace("-", "").replace(" ", "");
		
		String sql = "SELECT A.MR_NO, B.PAT_NAME,A.CASE_NO," +
				"TO_CHAR (A.SEALED_DATE, 'YYYY/MM/DD HH24:MI:SS') SEALED_DATE," +
				"A.SEALED_USER, A.OPT_TYPE, TO_CHAR (A.OPT_DATE, 'YYYY/MM/DD HH24:MI:SS') OPT_DATE" +
				" FROM MRO_MRV_SEALEDLOG A, SYS_PATINFO B" +
				"  WHERE     A.MR_NO = B.MR_NO" +
				" AND A.SEALED_DATE BETWEEN TO_DATE ('"+date_s+"','YYYYMMDDHH24MISS')" +
				" AND TO_DATE ('"+date_e+"', 'YYYYMMDDHH24MISS')";
		
		String mrNo = this.getValueString("MR_NO");
		if(mrNo.length() > 0){
			sql += " AND A.MR_NO='"+mrNo+"'";
		}
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		 if(parm.getCount() < 0){
			 this.messageBox("没有要查询的数据");
			 table.removeRowAll();
			 return;
		 }
		 
		 table.setParmValue(parm);
	
		
	}
	
	public void onMrNo(){
		if(this.getValueString("MR_NO").length() > 0){
			String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
			this.setValue("MR_NO", srcMrNo);
			onQuery();
		}
	}
	
	public void onClear(){
		this.clearValue("MR_NO");
		table.removeRowAll();
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("S_DATE",
				date.toString().substring(0, 10).replace('-', '/')
						+ " 00:00:00");
		this.setValue("E_DATE", date.toString()
				.substring(0, 10).replace('-', '/')
				+ " 23:59:59");
	}
}
