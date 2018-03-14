package com.javahis.ui.opd;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.cdss.SysUtil;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.ui.testOpb.tools.SqlTool;
import com.javahis.ui.testOpb.tools.Type;

public class DroolsLogControl extends TControl{
	
	TTable table;
	String mrNo;
	String caseNo;
	String admType;
	
	public void onInit(){

		table = (TTable) getComponent("TABLE");
		TParm droolsLogBean = (TParm) getParameter();
		
		mrNo = droolsLogBean.getValue("MR_NO");
		caseNo = droolsLogBean.getValue("CASE_NO");
		admType = droolsLogBean.getValue("ADM_TYPE");
		TParm parm = droolsLogBean.getParm("LOG_PARM");
		
		Timestamp t = SystemTool.getInstance().getDate();
		
		String date = t.toString().substring(0, 10).replace("-", "");
		
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < parm.getCount("ID"); i++) {
			sb.append("'");
			sb.append(parm.getValue("ID", i));
			sb.append("'");
			sb.append(",");
		}
		sb.append("''");
		
		String sql = 
			" SELECT CKB_ID" +
			" FROM DSS_CKBLOG" +
			" WHERE     CASE_NO = '" + caseNo + "'" +
			" AND DR_CODE = '" + Operator.getID() + "'" +
			" AND LOG_DATE BETWEEN TO_DATE ('" + date + "000000', 'YYYYMMDDHH24MISS')" +
			" 	AND TO_DATE ('" + date + "235959', 'YYYYMMDDHH24MISS')" +
			" AND CKB_ID IN (" + sb + ")" +
			" AND IS_CONFIRM = 'Y'";
		
		TParm confirmParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		@SuppressWarnings("unchecked")
		List<String> confirmList = (List<String>) confirmParm.getData("CKB_ID");
		if(confirmList == null){
			confirmList = new ArrayList<String>();
		}
		
		TParm tbParm = new TParm();
		String id;
		CdssCkgLog cdssCkgLog;
		for (int i = 0; i < parm.getCount("ID"); i++) {
			id = parm.getValue("ID", i);
			if(!confirmList.contains(id)){
				tbParm.addData("ID", parm.getValue("ID", i));
				tbParm.addData("LEVEL", parm.getValue("LEVEL", i));
				tbParm.addData("ADVICE", parm.getValue("ADVICE", i));
				tbParm.addData("REMARK", parm.getValue("REMARK", i));
				
				cdssCkgLog = new CdssCkgLog();
				cdssCkgLog.caseNo = caseNo;
				cdssCkgLog.ckbId = parm.getValue("ID", i);
				cdssCkgLog.logDate = t.toString().substring(0, 19).replace("-", "").replace(":", "");
				cdssCkgLog.admType = admType;
				cdssCkgLog.mrNo = mrNo;
				cdssCkgLog.riskLevel = parm.getValue("LEVEL", i);
				cdssCkgLog.bypassReason = parm.getValue("REMARK", i);
				cdssCkgLog.deptCode = Operator.getDept();
				cdssCkgLog.drCode = Operator.getID();
				cdssCkgLog.isConfirm = "N";
				cdssCkgLog.advise = parm.getValue("ADVICE", i);
				cdssCkgLog.optUser = Operator.getID();
				cdssCkgLog.optDate = t.toString().substring(0, 19).replace("-", "").replace(":", "");
				cdssCkgLog.optTerm = Operator.getIP();				
				cdssCkgLog.orderCode = parm.getValue("ORDER_CODE", i); //add by huangtt 20150806 
				cdssCkgLog.orderNo = parm.getValue("ORDER_NO", i); //add by huangtt 20150806
				cdssCkgLog.orderSeq = parm.getInt("ORDER_SEQ", i); //add by huangtt 20150806
				
				
				cdssCkgLog.modifyState = Type.INSERT;
				try {
					sql = SqlTool.getInstance().getSql(cdssCkgLog);
					new TParm(TJDODBTool.getInstance().update(sql));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		table.setParmValue(tbParm);
		
		class CloseTread extends Thread {
			
			DroolsLogControl droolsLogControl;
			
			public CloseTread(DroolsLogControl droolsLogControl) {
				// TODO Auto-generated constructor stub
				this.droolsLogControl = droolsLogControl;
			}

			public void run() {
//				try {
//					Thread.sleep(100);
//				} catch (InterruptedException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
				droolsLogControl.setReturnValue("Y");
				droolsLogControl.closeWindow();
			}
		}
		
		if(tbParm.getCount("ID") < 1){
			CloseTread closeTread = new CloseTread(this);
			closeTread.start();
		}
	}
	
	public void onSave(){
		stopEditing();
		table.acceptText();
		TParm parm = table.getParmValue();
		Timestamp t = SystemTool.getInstance().getDate();
		String date = t.toString().substring(0, 10).replace("-", "");
		for (int i = 0; i < parm.getCount("ID"); i++) {
			if("1".equals(parm.getValue("LEVEL", i))){
				messageBox("有管控等级为1级的建议，禁止保存");
				return;
			}
			if("2".equals(parm.getValue("LEVEL", i)) && parm.getValue("REMARK", i).length() == 0){
				messageBox("请在管控等级为2级的建议后输入医师原因");
				return;
			}
		}

		String sql1 =
			" UPDATE DSS_CKBLOG " +
			" SET BYPASS_REASON = '" + SQLRPC_BYPASS_REASON + "'," +
			" IS_CONFIRM = 'Y' " +
			" WHERE CASE_NO = '" + caseNo + "' " +
			" AND CKB_ID = '" + SQLRPC_CKB_ID + "'" +
			" AND ADM_TYPE = '" + admType + "' " +
			" AND DR_CODE = '" + Operator.getID() + "'" +
			" AND LOG_DATE BETWEEN TO_DATE ('" + date + "000000', 'YYYYMMDDHH24MISS')" +
			" 	AND TO_DATE ('" + date + "235959', 'YYYYMMDDHH24MISS')" +
					" AND ORDER_NO IS NOT NULL";
		String sql;
		for (int i = 0; i < parm.getCount("ID"); i++) {
			if("2".equals(parm.getValue("LEVEL", i)) || "3".equals(parm.getValue("LEVEL", i))){
				sql = sql1.replace(SQLRPC_CKB_ID, parm.getValue("ID", i));
				sql = sql.replace(SQLRPC_BYPASS_REASON, parm.getValue("REMARK", i));
				new TParm(TJDODBTool.getInstance().update(sql));
			}
		}
		
		setReturnValue("Y");
		closeWindow();
	}
	
	private void stopEditing(){
		for (int i = 0; i < table.getColumnCount(); i++) {
			table.getCellEditor(i).stopCellEditing();
		}
	}
	
	public void onReturn(){
		stopEditing();
		table.acceptText();
		TParm parm = table.getParmValue();
		Timestamp t = SystemTool.getInstance().getDate();
		String date = t.toString().substring(0, 10).replace("-", "");
		String sql1 =
			" UPDATE DSS_CKBLOG " +
			" SET ORDER_NO = ''," +
			" ORDER_SEQ = '' " +
			" WHERE CASE_NO = '" + caseNo + "' " +
			" AND CKB_ID = '" + SQLRPC_CKB_ID + "'" +
			" AND ADM_TYPE = '" + admType + "' " +
			" AND DR_CODE = '" + Operator.getID() + "'" +
			" AND LOG_DATE BETWEEN TO_DATE ('" + date + "000000', 'YYYYMMDDHH24MISS')" +
			" 	AND TO_DATE ('" + date + "235959', 'YYYYMMDDHH24MISS')";
		String sql;
		for (int i = 0; i < parm.getCount("ID"); i++) {
			sql = sql1.replace(SQLRPC_CKB_ID, parm.getValue("ID", i));
			new TParm(TJDODBTool.getInstance().update(sql));
		}
		closeWindow();
		
	}
	
	private final String SQLRPC_CKB_ID = "9Qgb4xrr";
	private final String SQLRPC_BYPASS_REASON = "G4H4YDKy";
	
	public static void main(String[] args) {
		SysUtil sysUtil = new SysUtil();
		System.out.println(sysUtil.generateShortUuid());
	}
	
}
