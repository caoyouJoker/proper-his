package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.util.Optional;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.root.client.SocketLink;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextArea;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.OdiUtil;

public class INFTwoCommunicateControl extends TControl {
	private TParm Parameter;// 存放参数
	private String caseNo;// 记录参数中的CASE_NO
	/**
	 * Socket传送工具
	 */
	private SocketLink client1;
	private SocketLink client2;
	/**
	 * 区分 界面 临床或感染科
	 */
	private String className ="";
	
	// 初始化
	public void onInit() {
		super.onInit();
		// 接收参数

		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof TParm) { // 判断是否是TParm
				Parameter = (TParm) obj;
			} else {
				this.closeWindow();
			}
		} else {
			this.closeWindow();
		}
		gainInitParm();
		pageInit();
	}
	// 初始化参数得到感染科权限和临床权限
		public void gainInitParm() {
			if(Parameter != null) {
				className = Parameter.getValue("CLASS_NAME");
			}
			if ("HANDLE".equals(className)) {
//				getTTextArea("SEND_AREA").setVisible(false);
				((TTextArea) this.getComponent("SEND_AREA")).getTextArea().setEditable(false);
				className = "HANDLE";
			} else if ("SEND".equals(className)) {
//				getTTextArea("HANDLE_AREA").setEditBoard(false);
				((TTextArea) this.getComponent("HANDLE_AREA")).getTextArea().setEditable(false);
				getTCheckBox("INTERVENT_FLG").setEnabled(false);
				getComboBox("INTERVENT_ID").setEnabled(false);
				getTable("TABLE_2").setEnabled(false);
				className = "SEND";
			}
		}

	// 页面初始化
	private void pageInit() {
		caseNo = Parameter.getValue("CASE_NO");
		String SQL = "";
		String SQLParm = "";
		// 病患基本信息
		if (caseNo != null) {
			SQL = "SELECT A.CASE_NO,A.MR_NO,A.IN_DATE,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.DEPT_CODE,A.STATION_CODE,A.BED_NO,B.BED_NO_DESC,A.VS_DR_CODE "
					+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C "
					+ " WHERE A.MR_NO = C.MR_NO "
					+ " AND A.BED_NO = B.BED_NO "
					+ " AND A.CASE_NO = '" + caseNo + "' ";
		}
		// System.out.println("sqllijian"+SQL);
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL));
		Timestamp sysDate = SystemTool.getInstance().getDate();
		Timestamp temp;
		// 计算年龄
		String age = "0";
		// 返回的行数
		int rowCount = parm.getCount("PAT_NAME");
		for (int i = 0; i < rowCount; i++) {
			temp = parm.getTimestamp("BIRTH_DATE", i) == null ? sysDate : parm
					.getTimestamp("BIRTH_DATE", i);
			if (parm.getTimestamp("IN_DATE", i) != null) {
				age = OdiUtil.showAge(temp, parm.getTimestamp("IN_DATE", i));
			} else {
				age = "";
			}
			parm.addData("AGE", age);

		}
		this.setValue("MR_NO", parm.getValue("MR_NO", 0));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME", 0));
		this.setValue("SEX_CODE", parm.getValue("SEX_CODE", 0));
		this.setValue("AGE", parm.getValue("AGE", 0));
		this.setValue("DEPT_CODE", parm.getValue("DEPT_CODE", 0));
		this.setValue("STATION_CODE", parm.getValue("STATION_CODE", 0));
		this.setValue("BED_NO", parm.getValue("BED_NO", 0));
		this.setValue("VS_DR_CODE", parm.getValue("VS_DR_CODE", 0));
		this.setValue("CASE_NO", parm.getValue("CASE_NO", 0));

		// 感染信息
		if (caseNo != null) {
			SQLParm = "SELECT INFPOSITION_CODE, INFPOSITION_DTL, INF_SOURCE, INF_DATE, INF_AREA, "
					+ " INF_DIAG1, (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = INF_DIAG1) INF_DIAG1_DESC, "
					+ " INF_DIAG2, (SELECT ICD_CHN_DESC FROM SYS_DIAGNOSIS WHERE ICD_CODE = INF_DIAG2) INF_DIAG2_DESC "
					+ " FROM INF_CASE WHERE CASE_NO = '" + caseNo + "' ";
		}
		// System.out.println("sqlli"+SQLParm);
		TParm infparm = new TParm(TJDODBTool.getInstance().select(SQLParm));
		this.setValue("INFPOSITION_CODE",
				infparm.getData("INFPOSITION_CODE", 0));
		this.setValue("INFPOSITION_DTL", infparm.getData("INFPOSITION_DTL", 0));
		this.setValue("INF_DATE", infparm.getData("INF_DATE", 0));
		this.setValue("INF_SOURCE", infparm.getData("INF_SOURCE", 0));
		this.setValue("INF_DIAG1", infparm.getData("INF_DIAG1_DESC", 0));
		this.setValue("INF_DIAG2", infparm.getData("INF_DIAG2_DESC", 0));
		this.setValue("INF_AREA", infparm.getData("INF_AREA", 0));
		
		onQuery();
	}

	// TABLE_1 沟通记录
	public void onQuery() {
		String SQLmessage = "SELECT A.MESSAGE_NO,A.CASE_NO,A.MR_NO,A.HANDLE_INFO,A.SEND_INFO,A.URG_FLG,A.INTERVENT_ID,A.INTERVENT_OPTIONS,A.SEND_USER, "
							+ " A.SEND_DATE,B.PAT_NAME "
							+ "	FROM INF_SMS A,SYS_PATINFO B WHERE A.MR_NO = B.MR_NO AND A.STATE = '9' AND CASE_NO = '" + caseNo + "' ORDER BY A.SEND_DATE DESC ";
		TParm mesParm = new TParm(TJDODBTool.getInstance().select(SQLmessage));
		for (int i = 0; i < mesParm.getCount("MESSAGE_NO"); i++) {
			this.setValue("SEND_DATE", SystemTool.getInstance().getDate());
			this.setValue("SEND_USER", Operator.getID());
			this.setValue("HANDLE_INFO", mesParm.getData("HANDLE_INFO", i));
			this.setValue("SEND_INFO", mesParm.getData("SEND_INFO", i));
		}
		getTable("TABLE_1").setParmValue(mesParm);
	}
	
	//保存
	public void onSave() {
//		int row = getTable("TABLE_1").getSelectedRow();
//		if (row < 0) { 
//			this.messageBox("请选择一条沟通记录！");
//			return;
//		}
		
		TParm parm = new TParm();
		getTable("TABLE_2").acceptText();
//		TParm table1Parm = getTable("TABLE_1").getParmValue();
		TParm table2Parm = getTable("TABLE_2").getParmValue();

		StringBuilder intervenOptions = new StringBuilder();
		String handleArea = this.getValueString("HANDLE_AREA");
		String sendArea = this.getValueString("SEND_AREA");
		String entervenId = this.getValueString("INTERVENT_ID");
		String caseNo = this.getValueString("CASE_NO");
		String mrNo = this.getValueString("MR_NO");
		String patName = this.getValueString("PAT_NAME");
		String vsDrCode = this.getValueString("VS_DR_CODE");
		String messageNo = SystemTool.getInstance().getNo("ALL", "PUB", "SMS_CODE", "SMS_CODE");
		if("HANDLE".equals(className)){
			if(!"".equals(entervenId)){
				for (int i = 0; i < table2Parm.getCount("ID"); i++) {
					if (table2Parm.getBoolean("FLG", i)) {
						intervenOptions.append(table2Parm.getValue("ID", i) + ";");
					} else {
						intervenOptions.append("");
					}
				}
			}
			
			
		}
		
		String inOptions;
		if (!StringUtils.isEmpty(intervenOptions.toString())) {
			inOptions = intervenOptions.substring(0, intervenOptions.lastIndexOf(";"));
		} else {
			inOptions = "";
		}
		if(!"".equals(entervenId)){
			parm.setData("INTERVENT_ID", entervenId);
			parm.setData("INTERVENT_OPTIONS", inOptions);
		}else {
			parm.setData("INTERVENT_ID", "");
			parm.setData("INTERVENT_OPTIONS", "");
		}
//		parm = table1Parm.getRow(row);
		parm.setData("MESSAGE_NO", messageNo);
		parm.setData("HANDLE_INFO", handleArea);
		parm.setData("SEND_INFO", sendArea);
		parm.setData("VS_DR_CODE", vsDrCode);
		parm.setData("CASE_NO", caseNo);
		parm.setData("MR_NO", mrNo);
		parm.setData("PAT_NAME", patName);
		
		parm.setData("SEND_USER", Operator.getID());
		parm.setData("SEND_DATE", SystemTool.getInstance().getDate());
		parm.setData("STATE", "9");
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		
		if(this.getTCheckBox("MESS_FLG").isSelected()) {
			parm.setData("MESS_FLG", "Y");
			
		} else {
			parm.setData("MESS_FLG", "N");
		}
		if(this.getTCheckBox("SYS_FLG").isSelected()) {
			parm.setData("SYS_FLG", "Y");
			
		} else {
			parm.setData("SYS_FLG", "N");
		}
		if (!"Y".equals(parm.getData("URG_FLG"))) {
			parm.setData("URG_FLG", "N");
		}
		else {
			parm.setData("URG_FLG", "Y");
		}
		
		parm.setData("CLASS_NAME", className);
		if("".equals(handleArea) && "".equals(sendArea)){
			this.messageBox("请输入消息内容！");
			return;
		}
//		this.populatePublishMessage(parm);
		TParm result = TIOM_AppServer.executeAction(
				"action.inf.InfAction", "onComSave", parm);
		
		
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
			messageBox("保存失败");
			return;
		} 
//		else{
//			messageBox("保存成功");
//		}
		
		//发送系统消息
		if("Y".equals(parm.getData("SYS_FLG"))){
			//1.发送系统消息
			sendInfMessages();
			
			//2.发送公告栏
//			parm.setData("CASE_NO", caseNo);
			String sql = "SELECT USER_ID, USER_NAME, ROLE_ID FROM SYS_OPERATOR WHERE USER_ID = '"+ parm.getValue("VS_DR_CODE") +"'";
	        TParm mesParm = new TParm(TJDODBTool.getInstance().select(sql));
	        String sql1 = "SELECT USER_ID, USER_NAME, ROLE_ID FROM SYS_OPERATOR WHERE USER_ID = '"+ TConfig.getSystemValue("INF_DIRECTOR") +"'";
	        TParm mesParm1 = new TParm(TJDODBTool.getInstance().select(sql1));
	        if("HANDLE".equals(parm.getValue("CLASS_NAME"))){
	        	parm.setData("USER_ID", parm.getValue("VS_DR_CODE"));
	        	parm.setData("ROLE_ID", mesParm.getValue("ROLE_ID").substring(1, mesParm.getValue("ROLE_ID").length()-1));
	        	parm.setData("USER_NAME", mesParm.getValue("USER_NAME"));
	        }else if("SEND".equals(parm.getValue("CLASS_NAME"))){
	        	if(!"".equals(TConfig.getSystemValue("INF_DIRECTOR"))){
	        		parm.setData("USER_ID", TConfig.getSystemValue("INF_DIRECTOR"));
	        	}else {
	        		parm.setData("USER_ID", "");
	        	}
	        	parm.setData("ROLE_ID", mesParm1.getValue("ROLE_ID"));
	        	parm.setData("USER_NAME", mesParm1.getValue("USER_NAME"));
	        }
			// 执行数据新增
			TParm result1 = TIOM_AppServer.executeAction(
	                "action.inf.InfAction", "onBoardMessage", parm);
	        // 保存判断
	        if (result1 == null || !result1.getErrText().equals("")) {
	            this.messageBox("发送失败" + " , " + result1.getErrText());
	            return;
	        }
			this.messageBox("系统消息发送成功！");
		}
		onClear();
		onQuery();
	}
	
	//清除选择
	public void onClear() {
		setValue("HANDLE_AREA", "");
		setValue("INTERVENT_ID", "");
		setValue("INTERVENT_FLG", "");
		setValue("SEND_AREA", "");
		getTable("TABLE_2").removeRowAll();
		getComboBox("INTERVENT_ID").setEnabled(false);
		// 强制失去编辑焦点
		TTable table = getTable("TABLE_1");
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
	}
	// 查询TABLE_2 干预措施选项
	public void getIntOptions() {
		String intId = this.getValueString("INTERVENT_ID");
		String SQLint = "SELECT 'N' FLG,ID,INTERVENT_ID,CHN_DESC FROM INF_INTERVENTION_OPTIONS WHERE INTERVENT_ID = '"
				+ intId + "' ";
		TParm intParm = new TParm(TJDODBTool.getInstance().select(SQLint));
		for (int i = 0; i < intParm.getCount("ID"); i++) {
			// this.setValue("FLG",intParm.getData("FLG", i));
			this.setValue("CHN_DESC", intParm.getData("CHN_DESC", i));
		}
		getTable("TABLE_2").setParmValue(intParm);
	}

	// 表格单击事件 
	public void onTable() {
		getTable("TABLE_2").removeRowAll();
		int row = getTable("TABLE_1").getSelectedRow();
		TParm tableParm = getTable("TABLE_1").getParmValue();
		setValue("HANDLE_AREA", tableParm.getData("HANDLE_INFO", row));
		setValue("SEND_AREA", tableParm.getData("SEND_INFO", row));
		String intId = (String) tableParm.getData("INTERVENT_ID", row);
		setValue("INTERVENT_ID", intId);
		String inOptions = (String) tableParm.getData("INTERVENT_OPTIONS", row);
		if(!"".equals(inOptions)){
			String SQLint = "SELECT 'N' FLG,ID,INTERVENT_ID,CHN_DESC FROM INF_INTERVENTION_OPTIONS WHERE INTERVENT_ID = '"
					+ intId + "' ";
			TParm intParm = new TParm(TJDODBTool.getInstance().select(SQLint));
			String [] ins = inOptions.split(";");
			for(int i=0;i < ins.length;i++){
				for(int j = 0; j < intParm.getCount("ID"); j++) {
					if(ins[i].equals(intParm.getValue("ID", j))) {
						intParm.setData("FLG", j, "Y");
						intParm.setData("CHN_DESC", j ,intParm.getData("CHN_DESC", i));
						break;
					}
				}
			}
			
			this.callFunction("UI|TABLE_2|setLockColumns","0,1"); 
			getTable("TABLE_2").setParmValue(intParm);
		}
	}

	// 勾选干预措施释放权限
	public void onIntevent() {
		if ("HANDLE".equals(className)){
			TTable table = getTable("TABLE_2");
			if (!this.getTCheckBox("INTERVENT_FLG").isSelected()) {
				getComboBox("INTERVENT_ID").setEnabled(false);
				// 强制失去编辑焦点
				if (table.getTable().isEditing()) {
					table.getTable().getCellEditor().stopCellEditing();
				}
				//锁列
				this.callFunction("UI|TABLE_2|setLockColumns","0,1"); 
				
			} else {
				getComboBox("INTERVENT_ID").setEnabled(true);
				this.callFunction("UI|TABLE_2|setLockColumns","1"); 
			}
		}else if("SEND".equals(className)){
			getComboBox("INTERVENT_ID").setEnabled(false);
			this.callFunction("UI|TABLE_2|setLockColumns","0,1"); 
		}
	}
	
	//感染科和临床互发消息
	public void sendInfMessages(){
		String handleArea = this.getValueString("HANDLE_AREA");
		String sendArea = this.getValueString("SEND_AREA");
		String vsDrCode = this.getValueString("VS_DR_CODE");
		String sql = "SELECT ID, PASSWORD FROM SKT_USER WHERE ID = '" + vsDrCode + "'";
//		System.out.println("sqllj:"+sql);
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		//给临床医生发消息
		if("HANDLE".equals(className)){
			client1 = SocketLink
					.running("", TConfig.getSystemValue("INF_DIRECTOR"), TConfig.getSystemValue("INF_PASSWORD"));
			if (client1.isClose()) {
				out(client1.getErrText());
				return;
			}
			
			client1.sendMessage(vsDrCode, this.getValueString("PAT_NAME")+ "， (病案号:" + this.getValueString("MR_NO") + ")"
					+ "， 感染科发送的消息内容为 [ " + handleArea + " ]"
					+ "，请到住院医生站点击沟通按钮查看！");

			if (client1 == null)
				return;
			client1.close();
		}else if("SEND".equals(className)){
			//给感染科发消息
			client2 = SocketLink
					.running("", parm.getValue("ID",0), parm.getValue("PASSWORD",0));
			if (client2.isClose()) {
				out(client2.getErrText());
				return;
			}
//			messageBox_(TConfig.getSystemValue("INF_DIRECTOR"));
			client2.sendMessage(TConfig.getSystemValue("INF_DIRECTOR"), this.getValueString("PAT_NAME")+ "， (病案号:" + this.getValueString("MR_NO") + ")"
					+ "， 临床医生发送的消息内容为 [ " + sendArea + " ]"
					+ "，请到感染病例登记点击沟通按钮查看！");
			if (client2 == null)
				return;
			client2.close();
		}
		
	}

	/**
	 * 取得Table控件
	 * 
	 * @param tableTag
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tableTag) {
		return ((TTable) getComponent(tableTag));
	}


	/**
	 * 取得TCheckBox控件
	 * @param checkBoxTag
	 *            String
	 * @return TCheckBox
	 */
	private TCheckBox getTCheckBox(String checkBoxTag) {
		return ((TCheckBox) getComponent(checkBoxTag));
	}
	
	/**
	 * 取得TComboBox控件
	 * @param checkBoxTag
	 *            String
	 * @return TCheckBox
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
}
