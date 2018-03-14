package com.javahis.ui.reg;

import java.sql.Timestamp;

import jdo.reg.REGTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.TypeTool;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

public class REGErdLevelUpdateControl extends TControl{
	
	private String levelCode = ""; 
	private TTable table;
	
	public void onInit(){
		table = (TTable) this.getComponent("TABLE");
		Object obj = this.getParameter();
        if (obj instanceof TParm) {
        	 TParm acceptData = (TParm) obj;
        	 String tradeNo = acceptData.getData("TRIAGE_NO").toString();
        	 this.setValue("TRIAGE_NO", tradeNo);
        	 this.onQuery();
        }
	}
	
	public void onSave(){
		if(this.getValueString("REASON").length() == 0){
			this.messageBox("请填写更改原因");
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("ID", REGTool.getInstance().getSystemTime());
		parm.setData("TRIAGE_NO", this.getValueString("TRIAGE_NO"));
		parm.setData("CASE_NO", this.getValueString("CASE_NO"));
		parm.setData("MR_NO", this.getValueString("MR_NO"));
		parm.setData("BEFORE_LEVEL_CODE", levelCode);
		parm.setData("AFTER_LEVEL_CODE", this.getValueString("LEVEL_CODE"));
		parm.setData("REASON", this.getValueString("REASON"));
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("OPT_USER", Operator.getID());
		TParm result = TIOM_AppServer.executeAction("action.reg.REGAction","onSaveErdLevelLog", parm);
		if(result.getErrCode()<0){
			this.messageBox("保存失败！");
			
		}else{
			this.messageBox("保存成功！");
			this.onQuery();
		}
		
	}
	
	
	public void onMrNo(){
		//modify by huangtt 20160927 EMPI患者查重提示  start
		 String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO"));
		 Pat pat = Pat.onQueryByMrNo(TypeTool.getString(getValue("MR_NO")));
		 if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {// wanglong add 20150423
	            this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
	        }
		//modify by huangtt 20160927 EMPI患者查重提示  end
		
		this.setValue("MR_NO", pat.getMrNo());
		onQuery();
	}
	
	public void onQuery(){
		table.removeRowAll();
		levelCode ="";
		String mrNo = this.getValueString("MR_NO");
		String caseNo = this.getValueString("CASE_NO");
		String triageNo = this.getValueString("TRIAGE_NO");
		if(mrNo.length() == 0 && caseNo.length()== 0 && triageNo.length()==0){
			return;
		}
		
		String sql = "SELECT A.TRIAGE_NO,A.CASE_NO,A.MR_NO,A.ADM_TYPE,A.DEPT_CODE,A.CLINICAREA_CODE," +
				"B.PAT_NAME,B.SEX_CODE,A.LEVEL_CODE,B.BIRTH_DATE,A.TRIAGE_USER FROM ERD_EVALUTION A,SYS_PATINFO B WHERE A.MR_NO=B.MR_NO ";
		if(mrNo.length()>0){
			sql += " AND A.MR_NO='"+mrNo+"'";
		}
		if(caseNo.length()>0){
			sql += " AND A.CASE_NO='"+caseNo+"'";
		}
		if(triageNo.length()>0){
			sql += " AND A.TRIAGE_NO='"+triageNo+"'";
		}
		sql += " ORDER BY A.ADM_DATE DESC,A.COME_TIME DESC";
//		System.out.println(sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			this.messageBox("没有要查询的数据");
			return;
		}		
		setValueForParm("TRIAGE_NO;CASE_NO;MR_NO;ADM_TYPE;DEPT_CODE;CLINICAREA_CODE;PAT_NAME;SEX_CODE;LEVEL_CODE;TRIAGE_USER;BIRTH_DATE",parm.getRow(0));
		if(getValue("BIRTH_DATE").toString().length()>0){
			Timestamp birthDate = TypeTool.getTimestamp(getValue("BIRTH_DATE"));
			String age = OdoUtil.showAge( birthDate,SystemTool.getInstance().getDate());
			this.setValue("AGE", age);
		}
		
		
		levelCode = parm.getValue("LEVEL_CODE", 0);
		
		this.callFunction("UI|TRIAGE_NO|setEnabled", false); 
		this.callFunction("UI|CASE_NO|setEnabled", false); 
		this.callFunction("UI|MR_NO|setEnabled", false); 
		
		triageNo = this.getValueString("TRIAGE_NO");
		sql = "SELECT A.TRIAGE_NO, B.LEVEL_DESC BEFORE_LEVEL_CODE," +
				" C.LEVEL_DESC AFTER_LEVEL_CODE,A.REASON,A.OPT_USER," +
				"TO_CHAR(A.OPT_DATE,'YYYY/MM/DD HH24:MI:SS') OPT_DATE " +
				" FROM ERD_LEVEL_LOG A,REG_ERD_LEVEL B,REG_ERD_LEVEL C" +
				" WHERE A.BEFORE_LEVEL_CODE = B.LEVEL_CODE(+)" +
				" AND A.AFTER_LEVEL_CODE = C.LEVEL_CODE(+)  " +
				" AND A.TRIAGE_NO='"+triageNo+"' ORDER BY A.OPT_DATE";

		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));

		table.setParmValue(tableParm);

		
		
	}
	
	public void onClear(){
		this.clearValue("TRIAGE_NO;CASE_NO;MR_NO;ADM_TYPE;DEPT_CODE;CLINICAREA_CODE;PAT_NAME;SEX_CODE;LEVEL_CODE;REASON");
		levelCode="";
		table.removeRowAll();
		this.callFunction("UI|TRIAGE_NO|setEnabled", true); 
		this.callFunction("UI|CASE_NO|setEnabled", true); 
		this.callFunction("UI|MR_NO|setEnabled", true); 

	}

}
