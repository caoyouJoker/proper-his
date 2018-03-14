package com.javahis.ui.mro;

import jdo.sys.PatTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: 封存病历问题记录 </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2012</p>
 *
 * <p>Company: bluecore</p>
 *
 * @author huangtt 20161108
 * @version 1.0
 */
public class MROSealedRecordControl extends TControl{
	
	TTable table;
	
	public void onInit(){
		
		table = (TTable) this.getComponent("TABLE");
		callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicke");
		
		Object obj = this.getParameter();
		if (obj instanceof TParm) {
			TParm acceptData = (TParm) obj;
			setValueName(acceptData);
			this.onQuery();
	
		}
		
		
	
		
	}
	
	
	
	public void onTableClicke(int row){
		table.acceptText();
		if (row < 0)
			return;
		
		TParm parm = table.getParmValue().getRow(row);
		setValueName(parm);
		
		
		
	}
	
	public void onQuery(){
		
		if(this.getValueString("MR_NO").length() == 0 &&
				this.getValueString("CASE_NO").length() == 0 &&
				this.getValueString("SEALED_USER").length() == 0){
			this.messageBox("请输入病案号，就诊号，封存 人员中的任一查询条件");
			return;
		}
				
		
		String sql = "SELECT A.MR_NO,B.PAT_NAME, A.CASE_NO," +
				" TO_CHAR (A.SEALED_DATE, 'YYYY/MM/DD HH24:MI:SS') SEALED_DATE," +
				" A.SEALED_USER, A.SEALED_PROBLEM" +
				" FROM MRO_MRV_TECH A, SYS_PATINFO B" +
				" WHERE A.MR_NO = B.MR_NO " ;

		if(this.getValueString("MR_NO").length() > 0){
			sql += " AND A.MR_NO='"+this.getValueString("MR_NO")+"'";
		}
		
		if(this.getValueString("CASE_NO").length() > 0){
			sql += " AND A.CASE_NO='"+this.getValueString("CASE_NO")+"'";
		}
		
		if(this.getValueString("SEALED_USER").length() > 0){
			sql += " AND A.SEALED_USER='"+this.getValueString("SEALED_USER")+"'";
		}
		
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount() < 0){
			table.removeRowAll();
			this.messageBox("没有要查询的数据");
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
	
	public void onSave(){
		String caseNo= this.getValueString("CASE_NO");
		if(caseNo.length() == 0){
			this.messageBox("请选择要保存的数据");
			return;
		}
		
		TParm parm = new TParm();
		parm.setData("CASE_NO", caseNo);
		parm.setData("SEALED_PROBLEM", this.getValueString("SEALED_PROBLEM"));
		
		TParm result = TIOM_AppServer.executeAction("action.mro.MROSealedAction",
				"updateMroMreTechSealedProblem", parm);
		if(result.getErrCode() < 0){
			this.messageBox("保存失败");
			return;
		}
		
		this.messageBox("保存成功");
		this.onQuery();
		
		
		
	}
	
	public void onClear(){
		
		this.clearValue("MR_NO;PAT_NAME;CASE_NO;SEALED_USER;SEALED_DATE;SEALED_PROBLEM");
		table.removeRowAll();
		callFunction("UI|CASE_NO|setEnabled", true);
		
		
	}
	
	public void onC(){
		this.closeWindow();
	}
	
	public void setValueName(TParm acceptData){
		this.setValue("MR_NO", acceptData.getValue("MR_NO"));
		this.setValue("PAT_NAME", acceptData.getValue("PAT_NAME"));
		this.setValue("CASE_NO", acceptData.getValue("CASE_NO"));
		this.setValue("SEALED_USER", acceptData.getValue("SEALED_USER"));
		this.setValue("SEALED_DATE", acceptData.getValue("SEALED_DATE"));
		this.setValue("SEALED_PROBLEM", acceptData.getValue("SEALED_PROBLEM"));
		callFunction("UI|CASE_NO|setEnabled", false);
		
	}
	

}
