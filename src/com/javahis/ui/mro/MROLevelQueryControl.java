package com.javahis.ui.mro;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

public class MROLevelQueryControl extends TControl {
	public void onInit(){
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("IN_START_DATE",  StringTool.rollDate(date, -7).toString().substring(0, 10).
                replace('-', '/') + " 00:00:00");
		
		this.setValue("IN_END_DATE",
                date.toString().substring(0, 10).replace('-', '/') +
                " 23:59:59");
		this.setValue("VIEW_PATTERN", "PDF");
	}
	
	public void onQuery(){
		TParm parm = getParameterValue();
		TParm result = new TParm(TJDODBTool.getInstance().select(getQueryPatientSql(parm)));
		TTable table = (TTable) this.getComponent("TABLE");
		table.setParmValue(result);
	}
	
	
	public TParm getParameterValue(){
		TParm parm = new TParm();
		
		//获取查询条件
		parm = getQueryParameter();
		
		//获取当前操作员所属全部科室
		parm = getOperatorDept(parm);
		return parm;
	}
	
	public TParm getQueryParameter(){
		TParm parm = new TParm();
		
		if(!"".equals(getValueString("IN_START_DATE"))){
			parm.setData("IN_START_DATE", getValueString("IN_START_DATE"));
		}else{
			parm.setData("IN_START_DATE", "");
		}
		
		if(!"".equals(getValueString("IN_END_DATE"))){
			parm.setData("IN_END_DATE", getValueString("IN_END_DATE"));
		}else{
			parm.setData("IN_END_DATE", "");
		}
		
		if(!"".equals(getValueString("IN_DEPT"))){
			parm.setData("IN_DEPT", getValueString("IN_DEPT"));
		}else{
			parm.setData("IN_DEPT", "");
		}
		
		if(!"".equals(getValueString("OUT_START_DATE"))){
			parm.setData("OUT_START_DATE", getValueString("OUT_START_DATE"));
		}else{
			parm.setData("OUT_START_DATE", "");
		}
		
		if(!"".equals(getValueString("OUT_END_DATE"))){
			parm.setData("OUT_END_DATE", getValueString("OUT_END_DATE"));
		}else{
			parm.setData("OUT_END_DATE", "");
		}
		
		if(!"".equals(getValueString("OUT_DEPT"))){
			parm.setData("OUT_DEPT", getValueString("OUT_DEPT"));
		}else{
			parm.setData("OUT_DEPT", "");
		}
		
		if(!"".equals(getValueString("MR_NO"))){
			parm.setData("MR_NO",getValueString("MR_NO"));
		}else{
			parm.setData("MR_NO", "");
		}
		
		return parm;
	}
	
	public String getQueryPatientSql(TParm parm){
		String sql = "SELECT A.MR_NO,A.IPD_NO,A.PAT_NAME,A.IN_DATE,A.OUT_DATE, "+
		        	 	" TRUNC(SYSDATE-IN_DATE) AS REAL_STAY_DAYS,TRUNC(SYSDATE-OUT_DATE) AS OUT_DAYS,C.DEPT_CHN_DESC AS OUT_DEPT,D.STATION_DESC AS OUT_STATION,A.CASE_NO, "+
		        	 	" A.ADMCHK_FLG,A.DIAGCHK_FLG,A.BILCHK_FLG,A.QTYCHK_FLG,B.CHN_DESC AS SEX,A.BIRTH_DATE,A.TEACH_EMR, A.TEST_EMR, A.VS_DR_CODE,A.ADM_SOURCE  "+
		        	 " FROM " +
		        	 	" MRO_RECORD A,SYS_DICTIONARY B,SYS_DEPT C,SYS_STATION D,SYS_PATINFO E "+
		        	 " WHERE " +
		        	 	" B.GROUP_ID='SYS_SEX' "+
		        	 	" AND A.SEX=B.ID "+
		        	 	" AND A.OUT_DEPT=C.DEPT_CODE(+) "+
		        	 	" AND A.OUT_STATION=D.STATION_CODE(+) "+
		        	 	" AND A.MR_NO=E.MR_NO ";
		
		//病案号
		if(!"".equals(parm.getValue("MR_NO").trim())){
            sql += " AND A.MR_NO='"+ parm.getValue("MR_NO") +"'";
        }
		
		//入院开始时间
		if (!"".equals(parm.getValue("IN_START_DATE"))) {
			sql += " AND A.IN_DATE >= TO_DATE('" +parm.getValue("IN_START_DATE").substring(0, 19) + "','YYYY-MM-DD HH24:MI:SS')";
	    }
		
		//入院结束时间
		if (!"".equals(parm.getValue("IN_END_DATE"))) {
			sql += " AND A.IN_DATE <= TO_DATE('" +parm.getValue("IN_END_DATE").substring(0, 19) + "','YYYY-MM-DD HH24:MI:SS')";
	    }
		
		//入院科室
		if (!"".equals(parm.getValue("IN_DEPT"))){
			sql += " AND A.IN_DEPT='"+ parm.getValue("IN_DEPT") +"'";
		}
		
		//出院开始时间
		if (!"".equals(parm.getValue("OUT_START_DATE"))) {
			sql += " AND A.OUT_DATE >= TO_DATE('" +parm.getValue("OUT_START_DATE").substring(0, 19) + "','YYYY-MM-DD HH24:MI:SS')";
	    }
				
		//出院结束时间
		if (!"".equals(parm.getValue("OUT_END_DATE"))) {
			sql += " AND A.OUT_DATE <= TO_DATE('" +parm.getValue("OUT_END_DATE").substring(0, 19) + "','YYYY-MM-DD HH24:MI:SS')";
	    }
				
		//出院科室
		if (!"".equals(parm.getValue("OUT_DEPT"))){
			sql += " AND A.OUT_DEPT='"+ parm.getValue("OUT_DEPT") +"'";
		}
		
		sql += getLevelSql();
		
		System.out.println("患者查询SQL："+sql);
		
		return sql;
	}
	
	public String getLevelSql(){
		//科室为当前医生所属科室
		//科室过滤条件为患者在院科室、出院科室、当前科室只要有一个满足条件的即可显示
		String sql = "";
		
		TParm level = getLevel();
		level = getOperatorDept(level);
		
		//可查看的范围，2为全院，1为本科病人（含一个医生有多个科室的情况，所有所属的科室均可看见），其他为本人
		Set<String> scopeSet = (Set<String>) level.getData("SCOPE");
		//全院
		if(scopeSet.contains("2")){
			
		}
		//本科
		else if(scopeSet.contains("1")){
			List<String> dept = (List<String>) level.getData("USER_DEPT");
			if(dept.size() > 0){
				sql += " AND ( ";
				for(int i = 0 ; i < dept.size() ; i++){
					sql += " A.IN_DEPT = '"+dept.get(i)+"' OR A.OUT_DEPT = '"+dept.get(i)+"' ";
					if(i != dept.size()-1){
						sql += " OR ";
					}
				}
				
				sql += " ) ";
			}
		}
		//本人
		else{
			sql += " AND A.VS_DR_CODE = '"+Operator.getID()+"'";
		}
		
		//可查看病人的密级
		Set<String> securitySet = (Set<String>) level.getData("SECURITY");
		if(securitySet.size() > 0){
			sql += " AND ( " ;
			for(String s : securitySet){
				sql += " E.SECURITY_CATEGORY = '"+s+"' OR ";
			}
			
			sql = sql.substring(0, sql.length()-3);
			sql += " ) ";
		}
		
		System.out.println("mmmm:"+sql);
		
		return sql;
	}
	
	public TParm getLevel(){
		TParm parm = new TParm();
		
		String user = Operator.getID();
		
		
		//String authoritySql = " SELECT A.* FROM EMR_RULE_AUTHORITY A , EMR_RULE_USER B WHERE A.EMR_RULE_CODE = B.EMR_RULE_CODE AND B.USER_EMR_ID='"+user+"' ";
		String authoritySql = " SELECT A.* FROM EMR_RUSECSCOPE A , EMR_RULE_USER B WHERE A.EMR_RULE_CODE = B.EMR_RULE_CODE AND B.USER_EMR_ID='"+user+"' ";

		TParm authority = new TParm(TJDODBTool.getInstance().select(authoritySql));
		Set<String> scopeSet = new HashSet<String>();
		Set<String> securitySet = new HashSet<String>();
		Set<String> emrSet = new HashSet<String>();
		TParm temp = null;
		System.out.println("m:"+authority.getCount());
		for(int i = 0 ; i < authority.getCount() ; i++){
			temp = authority.getRow(i);
			scopeSet.add(temp.getValue("EMR_SCOPE_CODE"));
			securitySet.add(temp.getValue("SECURITY_CATEGORY_CODE"));
			emrSet.add(temp.getValue("EMR_CLASS_CODE"));
			
		}
		
		parm.setData("SCOPE", scopeSet);
		parm.setData("SECURITY", securitySet);
		parm.setData("EMR", emrSet);
		
		return parm;
	}
	
	public void onClear(){
		this.clearValue("MR_NO;IN_START_DATE;IN_END_DATE;IN_DEPT;OUT_START_DATE;OUT_END_DATE;OUT_DEPT");
	}
	
	public TParm getOperatorDept(TParm parm){
		TParm result = Operator.getOperatorDept(Operator.getID());
		List<String> lst = new ArrayList<String>();
		for(int i = 0 ; i < result.getCount() ; i++){
			lst.add(result.getValue("DEPT_CODE",i));
		}
		parm.setData("USER_DEPT", lst);
		return parm;
	}
	
	public void onShow() {
		TTable table = ((TTable) this.getComponent("TABLE"));
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选择一个病人");
			return;
		}
		TParm parm = table.getParmValue().getRow(table.getSelectedRow());
		Runtime run = Runtime.getRuntime();
		try {
			// 得到当前使用的ip地址
			String ip = TIOM_AppServer.SOCKET
					.getServletPath("MROLevelServlet?CASE_NO=");
			// 连接网页方法
			run.exec("IEXPLORE.EXE " + ip + parm.getValue("CASE_NO")+"&MR_NO="+parm.getValue("MR_NO")+"&VIEW_PATTERN="+this.getValueString("VIEW_PATTERN")+"&USER_ID="+Operator.getID());
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}
}
