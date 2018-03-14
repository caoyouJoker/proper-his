package com.javahis.ui.inv;

import java.sql.Timestamp;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import action.inv.INVAutoRequsetAction;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ������������������
 * </p>
 * 
 * <p>
 * Description:
 * </p>
 *      
 * <p>
 * Copyright: Copyright (c) 2014
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author fux
 * @version 4.0
 */
public class INVAutoRequestControl extends TControl {
	private TTable table;
	private TParm resultWebService;
	// action��·��
	private static final String actionName = "action.inv.INVAutoRequsetAction";

	/**
	 * ��ʼ������
	 * 
	 * @param tag
	 * @param obj
	 */
	public void onInit() {
		// if(this.getPopedem("OPE_JR")){
		// this.setValue("OPE_TYPE", 2);
		// }  
		// if(this.getPopedem("OPE")){
		// this.setValue("OPE_TYPE", 1);
		// }
		this.setValue("OPE_TYPE", 2);
		// Timestamp yesterday = StringTool.rollDate(SystemTool.getInstance()
		// .getDate(), -1);
		Timestamp day = StringTool.rollDate(SystemTool.getInstance().getDate(),
				0);
		// Timestamp torrowday = StringTool.rollDate(SystemTool.getInstance()
		// .getDate(), +1);

		// setValue("OP_DATE_START", torrowday);
		// // SystemTool.getInstance().getDate()
		// setValue("OP_DATE_END", torrowday);
		setValue("OP_DATE_START", day);
		setValue("OP_DATE_END", day);
		table = getTable("TABLE");
		TParm parmIcd = new TParm();
		parmIcd.setData("OPERATION_ICD", "");
		// //�����δ��ɱ��ת��
		// onCharge();
		// ���õ����˵�
		getTextField("OPERATION_ICD").setPopupMenuParameter("UD",
				getConfigParm().newConfig("%ROOT%\\config\\sys\\sysOpICD.x"),
				parmIcd);
		// ������ܷ���ֵ����
		getTextField("OPERATION_ICD").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturnIcd");
		// �豸DD¼�¼�
		getTable("TABLE").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableComponent");
		onInitTable();
	}

	/**
	 * tabledd�����¼�
	 * 
	 * @param obj
	 *            Object
	 * @return boolean
	 */
	public boolean onTableComponent(Object obj) {
		TTable chargeTable = (TTable) obj;
		chargeTable.acceptText();
		return true;
	}

	/**
	 * ���ܷ���ֵ����(icd)
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturnIcd(String tag, Object obj) {
		TParm parm = (TParm) obj;
		if (parm == null) {
			return;
		}
		String operation_code = parm.getValue("OPERATION_ICD");
		if (!StringUtil.isNullString(operation_code))
			getTextField("OPERATION_ICD").setValue(operation_code);
		String opt_desc = parm.getValue("OPT_CHN_DESC");
		if (!StringUtil.isNullString(opt_desc))
			getTextField("OPT_CHN_DESC").setValue(opt_desc);
	}

	/**
	 * ���
	 */
	public void onClear() {
		this.clearValue("OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY");
		table = getTable("TABLE");
		table.removeRowAll();
	}

	/**
	 * ���淽�� (����)
	 */
	public void onSave() {
		if (!CheckData()) {
			return;
		}
		TParm result = new TParm();
		for (int i = 0; i < table.getRowCount(); i++) {
			// ѡ�еĴ���
			if (table.getItemData(i, "FLG").equals("Y")) {
				result
						.addData("OPBOOK_SEQ", table.getItemData(i,
								"OPBOOK_SEQ"));
				result.addData("OP_CODE", table.getItemData(i, "OP_CODE"));
				result.addData("SUPTYPE_CODE", table.getItemData(i,
						"SUPTYPE_CODE"));
				result.addData("MR_NO", table.getItemData(i, "MR_NO"));
				result.addData("PAT_NAME", table.getItemData(i, "PAT_NAME"));
				result.addData("REMARK", table.getItemData(i, "REMARK"));
				result
						.addData("GDVAS_CODE", table.getItemData(i,
								"GDVAS_CODE"));
				result.addData("OP_DATE", TypeTool.getTimestamp(StringTool
						.getTimestamp(table.getItemData(i, "OP_DATE")
								.toString(), "yyyy/MM/dd HH:mm:ss")));
				result.addData("STATE", table.getItemData(i, "STATE"));
				result.addData("OPT_USER", Operator.getID());
				result.addData("OPT_DATE", SystemTool.getInstance().getDate());
				result.addData("OPT_TERM", Operator.getIP());
				result.addData("FINAL_FLG", "Y");
			}
		}
		TParm newresult = TIOM_AppServer.executeAction(
				"action.inv.INVOpeAndPackageAction", "onUpdateAutoRequest",
				result);
		if (newresult == null || newresult.getErrCode() < 0) {
			this.messageBox("P0005"); 
			return;
		} else {
			// ��ѯʱ���棬����ʱ������ɱ�ǲ�����
			this.messageBox("P0001");
			this.setReturnValue(result);
			this.closeWindow();
		}
	}

	/**
	 * ���ݼ���
	 * 
	 * @return
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("OP_DATE_START"))) {
			this.messageBox("������ʼʱ�䲻��Ϊ��");
			return false;
		}
		if ("".equals(getValueString("OP_DATE_END"))) {
			this.messageBox("��������ʱ�䲻��Ϊ��");
			return false;
		}
		return true;
	}

	/**
	 * ɾ������
	 */
	public void onDelete() {
		if (table.getSelectedRow() < 0) {
			this.messageBox("��ѡ��ɾ����");
			return;
		}
		TParm parm = new TParm();
		parm.setData("INV_CODE", this.getValueString("INV_CODE"));
		parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
		TParm result = TIOM_AppServer.executeAction(
				"action.inv.INVAgentAction", "onDelete", parm);
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("ɾ��ʧ��");
			return;
		}
		this.messageBox("ɾ���ɹ�");
		this.onClear();
	}

	/**
	 * ��webservice��ʼ��table����
	 */
	public void onInitTable() {
		String packCode = this.getValueString("PACK_CODE");
		String opeType = this.getValueString("OPE_TYPE");
		String opCode = "";
		String supTypeCode = "";

		String opDateS = this.getValueString("OP_DATE_START");
		String opDateE = this.getValueString("OP_DATE_END");

		String state = "";        
		// 0 ���룬 1 �ų���� ��2�������
		if (this.getRadioButton("STATE1").isSelected()) {
		} else if (this.getRadioButton("STATE2").isSelected()) {
			state = "1";
		} else if (this.getRadioButton("STATE3").isSelected()) {
			state = "0";
		}
		String optUser = Operator.getID();
		String optTerm = Operator.getIP();
		TParm parmIn = new TParm();
		parmIn.addData("OPCODE", opCode);
		parmIn.addData("SUPTYPECODE", supTypeCode);
		parmIn.addData("OPDATE_S", opDateS);
		parmIn.addData("OPDATE_E", opDateE);
		parmIn.addData("STATE", state);
		parmIn.addData("ID", optUser);
		parmIn.addData("IP", optTerm);
		TParm result = TIOM_AppServer.executeAction(actionName, "onOpePackage",
				parmIn);
		//System.out.println("webservice����result:::" + result);
		// ��ȡresult

		// δ���
		// ��ʼ���϶�����û����ֵ,��ȡ���������ֵ
		String sql = " SELECT 'Y' AS FLG ,OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,OP_DATE,STATE,MR_NO,"
				+ " PAT_NAME,REMARK,GDVAS_CODE,OPT_DATE,OPT_USER,OPT_TERM"
				+ " FROM OPE_PACKAGE" + " WHERE 1=1 ";
		StringBuffer SQL = new StringBuffer();
		SQL.append(sql);

		if (!"".equals(packCode)) {
			SQL.append(" AND PACK_CODE = '" + packCode + "'");
		}

		if (!"".equals(opeType)) {
			SQL.append(" AND SUPTYPE_CODE = '" + opeType + "'");
		}

		if (!"".equals(opDateS) && !"".equals(opDateE)) {
			opDateS = opDateS.substring(0, 10).replace('-', ' ').trim();
			opDateE = opDateE.substring(0, 10).replace('-', ' ').trim();
			SQL.append(" AND OP_DATE BETWEEN TO_DATE('" + opDateS
					+ "000000','YYYYMMDDHH24Miss')  AND TO_DATE('" + opDateE
					+ "235959','YYYYMMDDHH24Miss')");
		}
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL.toString()));

		if (parm.getCount() <= 0) {
			TParm tableParm = new TParm();
			if (!"".equals(opeType)) {
				for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
					if (result.getValue("SUPTYPE_CODE", i).equals(opeType)) {
						tableParm.setData("FLG", i, "Y");
						tableParm.setData("OPBOOK_SEQ", i, result.getValue(
								"OPBOOK_SEQ", i));
						tableParm.setData("OP_CODE", i, result.getValue(
								"OP_CODE", i));
						tableParm.setData("SUPTYPE_CODE", i, result.getValue(
								"SUPTYPE_CODE", i));
						// ,Timestamp,yyyy/MM/dd HH:mm:ss
						tableParm.setData("OP_DATE", i, result.getValue(
								"OP_DATE", i).replace('-', '/')
								.substring(0, 19));
						tableParm.setData("STATE", i, result.getValue("STATE",
								i));
						tableParm.setData("MR_NO", i, result.getValue("MR_NO",
								i));
						tableParm.setData("PAT_NAME", i, result.getValue(
								"PAT_NAME", i));
						tableParm.setData("REMARK", i, result.getValue(
								"REMARK", i));
						tableParm.setData("GDVAS_CODE", i, result.getValue(
								"GDVAS_CODE", i));
						tableParm.setData("OPT_USER", i, result.getValue(
								"OPT_USER", i));
						tableParm.setData("OPT_DATE", i, result.getValue(
								"OPT_DATE", i));
						tableParm.setData("OPT_TERM", i, result.getValue(
								"OPT_TERM", i));
					}
				}
			} else {
				for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
					tableParm.setData("FLG", i, "Y");
					tableParm.setData("OPBOOK_SEQ", i, result.getValue(
							"OPBOOK_SEQ", i));
					tableParm.setData("OP_CODE", i, result.getValue("OP_CODE",
							i));
					tableParm.setData("SUPTYPE_CODE", i, result.getValue(
							"SUPTYPE_CODE", i));
					// ,Timestamp,yyyy/MM/dd HH:mm:ss
					tableParm.setData("OP_DATE", i, result.getValue("OP_DATE",
							i).replace('-', '/').substring(0, 19));
					tableParm.setData("STATE", i, result.getValue("STATE", i));
					tableParm.setData("MR_NO", i, result.getValue("MR_NO", i));
					tableParm.setData("PAT_NAME", i, result.getValue(
							"PAT_NAME", i));
					tableParm
							.setData("REMARK", i, result.getValue("REMARK", i));
					tableParm.setData("GDVAS_CODE", i, result.getValue(
							"GDVAS_CODE", i));
					tableParm.setData("OPT_USER", i, result.getValue(
							"OPT_USER", i));
					tableParm.setData("OPT_DATE", i, result.getValue(
							"OPT_DATE", i));
					tableParm.setData("OPT_TERM", i, result.getValue(
							"OPT_TERM", i));
				}
			}
			if (tableParm.getCount("OPBOOK_SEQ") <= 0) {
				this.messageBox("û�д���������ѯ����");
				return;
			}
			// returnString �ŵ�������
			table.setParmValue(tableParm);
		}
		// δ��ɵ���ʾ�ڽ�����
		else {
			String sql2 = " SELECT 'Y' AS FLG ,OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,OP_DATE,STATE,MR_NO,"
					+ " PAT_NAME,REMARK,GDVAS_CODE,OPT_DATE,OPT_USER,OPT_TERM"
					+ " FROM OPE_PACKAGE" + " WHERE FINAL_FLG = 'N' " + "";
			StringBuffer SQL2 = new StringBuffer();
			SQL2.append(sql2);

			if (!"".equals(packCode)) {
				SQL2.append(" AND PACK_CODE = '" + packCode + "'");
			}

			if (!"".equals(opeType)) {
				SQL2.append(" AND SUPTYPE_CODE = '" + opeType + "'");
			}

			if (!"".equals(opDateS) && !"".equals(opDateE)) {
				opDateS = opDateS.substring(0, 10).replace('-', ' ').trim();
				opDateE = opDateE.substring(0, 10).replace('-', ' ').trim();
				SQL2.append(" AND OP_DATE BETWEEN TO_DATE('" + opDateS
						+ "000000','YYYYMMDDHH24Miss')  AND TO_DATE('"
						+ opDateE + "235959','YYYYMMDDHH24Miss')");
			}
			TParm parm2 = new TParm(TJDODBTool.getInstance().select(
					SQL2.toString()));
			table.setParmValue(parm2);
		}

	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {

		TParm result = table.getParmValue();
		// ��ȡresult

		// δ���
		// ��ʼ���϶�����û����ֵ,��ȡ���������ֵ
		String packCode = this.getValueString("PACK_CODE");
		String opeType = this.getValueString("OPE_TYPE");
		String opDateS = this.getValueString("OP_DATE_START");
		String opDateE = this.getValueString("OP_DATE_END");
		String sql = " SELECT 'Y' AS FLG ,OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,OP_DATE,STATE,MR_NO,"
				+ " PAT_NAME,REMARK,GDVAS_CODE,OPT_DATE,OPT_USER,OPT_TERM"
				+ " FROM OPE_PACKAGE" + " WHERE 1=1 ";
		StringBuffer SQL = new StringBuffer();
		SQL.append(sql);

		if (!"".equals(packCode)) {
			SQL.append(" AND PACK_CODE = '" + packCode + "'");
		}

		if (!"".equals(opeType)) {
			SQL.append(" AND SUPTYPE_CODE = '" + opeType + "'");
		}

		if (!"".equals(opDateS) && !"".equals(opDateE)) {
			opDateS = opDateS.substring(0, 10).replace('-', ' ').trim();
			opDateE = opDateE.substring(0, 10).replace('-', ' ').trim();
			SQL.append(" AND OP_DATE BETWEEN TO_DATE('" + opDateS
					+ "000000','YYYYMMDDHH24Miss')  AND TO_DATE('" + opDateE
					+ "235959','YYYYMMDDHH24Miss')");
		}
		TParm parm = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
		if (parm.getCount() <= 0) {
			if (!CheckData()) {
				return;
			}
			// TParm parm = table.getParmValue();
			TParm resultSave = new TParm();
			for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
				resultSave.setData("OPBOOK_SEQ", i, result.getValue(
						"OPBOOK_SEQ", i));
				resultSave.setData("OP_CODE", i, result.getValue("OP_CODE", i));
				resultSave.setData("SUPTYPE_CODE", i, result.getValue(
						"SUPTYPE_CODE", i));
				// ,Timestamp,yyyy/MM/dd HH:mm:ss
				Timestamp oPtime = StringTool.getTimestamp(result.getData(
						"OP_DATE", i).toString().substring(0, 19),
						"yyyy/MM/dd HH:ss:mm");
				resultSave.setData("OP_DATE", i, oPtime);
				resultSave.setData("STATE", i, result.getValue("STATE", i));
				resultSave.setData("MR_NO", i, result.getValue("MR_NO", i));
				resultSave.setData("PAT_NAME", i, result
						.getValue("PAT_NAME", i));
				resultSave.setData("REMARK", i, "".equals(result.getValue(
						"REMARK", i)) ? "" : result.getValue("REMARK", i));
				resultSave.setData("GDVAS_CODE", i, "".equals(result.getValue(
						"GDVAS_CODE", i)) ? "" : result.getValue("GDVAS_CODE",
						i));
				// resultSave.setData("REMARK", i, "");
				resultSave.setData("OPT_USER", i, Operator.getID());
				resultSave.setData("OPT_DATE", i, SystemTool.getInstance()
						.getDate());
				resultSave.setData("OPT_TERM", i, Operator.getIP());
				resultSave.setData("FINAL_FLG", i, "N");
			}
			TParm newresult = TIOM_AppServer.executeAction(
					"action.inv.INVOpeAndPackageAction", "onInsertAutoRequest",
					resultSave);
			if (newresult == null || newresult.getErrCode() < 0) {
				this.messageBox("E0001");
				return;  
			} else {
				// ��ѯʱ���棬����ʱ������ɱ�ǲ�����
				this.messageBox("P0001");
			}
			TParm tableParm = new TParm();
			if (!"".equals(opeType)) {
				for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
					if (result.getValue("SUPTYPE_CODE", i).equals(opeType)) {
						tableParm.setData("FLG", i, "Y");
						tableParm.setData("OPBOOK_SEQ", i, result.getValue(
								"OPBOOK_SEQ", i));
						tableParm.setData("OP_CODE", i, result.getValue(
								"OP_CODE", i));
						tableParm.setData("SUPTYPE_CODE", i, result.getValue(
								"SUPTYPE_CODE", i));
						// ,Timestamp,yyyy/MM/dd HH:mm:ss
						tableParm.setData("OP_DATE", i, result.getValue(
								"OP_DATE", i).replace('-', '/')
								.substring(0, 19));
						tableParm.setData("STATE", i, result.getValue("STATE",
								i));
						tableParm.setData("MR_NO", i, result.getValue("MR_NO",
								i));
						tableParm.setData("PAT_NAME", i, result.getValue(
								"PAT_NAME", i));
						tableParm.setData("REMARK", i, result.getValue(
								"REMARK", i));
						tableParm.setData("GDVAS_CODE", i, result.getValue(
								"GDVAS_CODE", i));
						tableParm.setData("OPT_USER", i, result.getValue(
								"OPT_USER", i));
						tableParm.setData("OPT_DATE", i, result.getValue(
								"OPT_DATE", i));
						tableParm.setData("OPT_TERM", i, result.getValue(
								"OPT_TERM", i));
					}
				}
			} else {
				for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
					tableParm.setData("FLG", i, "Y");
					tableParm.setData("OPBOOK_SEQ", i, result.getValue(
							"OPBOOK_SEQ", i));
					tableParm.setData("OP_CODE", i, result.getValue("OP_CODE",
							i));
					tableParm.setData("SUPTYPE_CODE", i, result.getValue(
							"SUPTYPE_CODE", i));
					// ,Timestamp,yyyy/MM/dd HH:mm:ss
					tableParm.setData("OP_DATE", i, result.getValue("OP_DATE",
							i).replace('-', '/').substring(0, 19));
					tableParm.setData("STATE", i, result.getValue("STATE", i));
					tableParm.setData("MR_NO", i, result.getValue("MR_NO", i));
					tableParm.setData("PAT_NAME", i, result.getValue(
							"PAT_NAME", i));
					tableParm
							.setData("REMARK", i, result.getValue("REMARK", i));
					tableParm.setData("GDVAS_CODE", i, result.getValue(
							"GDVAS_CODE", i));
					tableParm.setData("OPT_USER", i, result.getValue(
							"OPT_USER", i));
					tableParm.setData("OPT_DATE", i, result.getValue(
							"OPT_DATE", i));
					tableParm.setData("OPT_TERM", i, result.getValue(
							"OPT_TERM", i));
				}
			}
			if (tableParm.getCount("OPBOOK_SEQ") <= 0) {
				this.messageBox("û�д���������ѯ����");
				return;
			}
			// returnString �ŵ�������
			table.setParmValue(tableParm);
		}
		// δ��ɵ���ʾ�ڽ�����
		else {
			String sql2 = " SELECT 'Y' AS FLG ,OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,OP_DATE,STATE,MR_NO,"
					+ " PAT_NAME,REMARK,GDVAS_CODE,OPT_DATE,OPT_USER,OPT_TERM"
					+ " FROM OPE_PACKAGE" + " WHERE FINAL_FLG = 'N' " + "";
			StringBuffer SQL2 = new StringBuffer();
			SQL2.append(sql2);

			if (!"".equals(packCode)) {
				SQL2.append(" AND PACK_CODE = '" + packCode + "'");
			}

			if (!"".equals(opeType)) {
				SQL2.append(" AND SUPTYPE_CODE = '" + opeType + "'");
			}

			if (!"".equals(opDateS) && !"".equals(opDateE)) {
				opDateS = opDateS.substring(0, 10).replace('-', ' ').trim();
				opDateE = opDateE.substring(0, 10).replace('-', ' ').trim();
				SQL2.append(" AND OP_DATE BETWEEN TO_DATE('" + opDateS
						+ "000000','YYYYMMDDHH24Miss')  AND TO_DATE('"
						+ opDateE + "235959','YYYYMMDDHH24Miss')");
			}
			TParm parm2 = new TParm(TJDODBTool.getInstance().select(
					SQL2.toString()));
			table.setParmValue(parm2);
		}

	}

	// /**
	// * ��ѯ����
	// */
	// public void onQuery(){
	// String packCode = this.getValueString("PACK_CODE");
	// String opeType = this.getValueString("OPE_TYPE");
	// String opCode = "";
	// String supTypeCode = "";
	//        
	// String opDateS = this.getValueString("OP_DATE_START");
	// String opDateE = this.getValueString("OP_DATE_END");
	//        
	// String state = "";
	// //0 ���룬 1 �ų���� ��2�������
	// if(this.getRadioButton("STATE1").isSelected()){
	// }else if(this.getRadioButton("STATE2").isSelected()){
	// state = "1";
	// }else if(this.getRadioButton("STATE3").isSelected()){
	// state = "0";
	// }
	// String optUser = Operator.getID();
	// String optTerm = Operator.getIP();
	// TParm parmIn = new TParm();
	// parmIn.addData("OPCODE",opCode);
	// parmIn.addData("SUPTYPECODE", supTypeCode);
	// parmIn.addData("OPDATE_S", opDateS);
	// parmIn.addData("OPDATE_E", opDateE);
	// parmIn.addData("STATE", state);
	// parmIn.addData("ID", optUser);
	// parmIn.addData("IP", optTerm);
	// TParm result = TIOM_AppServer.executeAction(actionName, "onOpePackage",
	// parmIn);
	// System.out.println("webservice����result:::"+result);
	// //��ȡresult
	//          
	// //δ���
	// //��ʼ���϶�����û����ֵ,��ȡ���������ֵ
	// String sql =
	// " SELECT 'Y' AS FLG ,OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,OP_DATE,STATE,MR_NO,"
	// +
	// " PAT_NAME,REMARK,OPT_DATE,OPT_USER,OPT_TERM" +
	// " FROM OPE_PACKAGE" +
	// " WHERE 1=1 ";
	// StringBuffer SQL = new StringBuffer();
	// SQL.append(sql);
	//    
	// if(!"".equals(packCode)){
	// SQL.append(" AND PACK_CODE = '"+packCode+ "'");
	// }
	//		
	// if(!"".equals(opeType)){
	// SQL.append(" AND SUPTYPE_CODE = '"+opeType+ "'");
	// }
	//		    
	// if(!"".equals(opDateS)&&!"".equals(opDateE)){
	// opDateS = opDateS.substring(0, 10).replace('-', ' ').trim();
	// opDateE = opDateE.substring(0, 10).replace('-', ' ').trim();
	// SQL.append(" AND OP_DATE BETWEEN TO_DATE('"+opDateS+"000000','YYYYMMDDHH24Miss')  AND TO_DATE('"+opDateE+"235959','YYYYMMDDHH24Miss')");
	// }
	// TParm parm = new TParm(TJDODBTool.getInstance().select(SQL.toString()));
	//		
	// if (parm.getCount() <= 0) {
	// if (!CheckData()) {
	// return;
	// }
	// // INV_AGENT��������
	// //opCode, supTypeCode, opDateS, opDateE, state, optUser, optTerm
	//              
	// //TParm parm = table.getParmValue();
	// TParm resultSave = new TParm();
	// for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
	// if (table.getItemData(i, "FLG").equals("Y"))
	// {
	// continue;
	// }
	// resultSave.setData("OPBOOK_SEQ", i, result.getValue("OPBOOK_SEQ", i));
	// resultSave.setData("OP_CODE", i, result.getValue("OP_CODE", i));
	// resultSave.setData("SUPTYPE_CODE", i, result.getValue("SUPTYPE_CODE",
	// i));
	// //,Timestamp,yyyy/MM/dd HH:mm:ss
	// //result.getValue("OP_DATE", i)
	// // resultSave.setData("OP_DATE", i,
	// StringTool.rollDate(SystemTool.getInstance().
	// // getDate(), +1));
	// Timestamp oPtime =
	// StringTool.getTimestamp(result.getData("OP_DATE",i).toString().substring(0,19),
	// "yyyy-MM-dd HH:ss:mm");
	// resultSave.setData("OP_DATE", i, oPtime);
	// resultSave.setData("STATE", i, result.getValue("STATE", i));
	// resultSave.setData("MR_NO", i, result.getValue("MR_NO", i));
	// resultSave.setData("PAT_NAME", i, result.getValue("PAT_NAME", i));
	// resultSave.setData("REMARK", i,"".equals(result.getValue("REMARK", i)) ?
	// "" : result.getValue("REMARK", i));
	// //resultSave.setData("REMARK", i, "");
	// resultSave.setData("OPT_USER", i, Operator.getID());
	// resultSave.setData("OPT_DATE", i, SystemTool.getInstance().
	// getDate());
	// resultSave.setData("OPT_TERM", i, Operator.getIP());
	// resultSave.setData("FINAL_FLG", i, "N");
	// }
	// TParm newresult = TIOM_AppServer.executeAction(
	// "action.inv.INVOpeAndPackageAction", "onInsertAutoRequest", resultSave);
	// if (newresult == null || newresult.getErrCode() < 0) {
	// this.messageBox("E0001");
	// return;
	// }
	// else {
	// //��ѯʱ���棬����ʱ������ɱ�ǲ�����
	// this.messageBox("P0001");
	// }
	// TParm tableParm = new TParm();
	// if(!"".equals(opeType)){
	// for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
	// if (result.getValue("SUPTYPE_CODE", i).equals(opeType)) {
	// tableParm.setData("FLG", i, "Y");
	// tableParm.setData("OPBOOK_SEQ", i, result.getValue("OPBOOK_SEQ", i));
	// tableParm.setData("OP_CODE", i, result.getValue("OP_CODE", i));
	// tableParm.setData("SUPTYPE_CODE", i, result.getValue("SUPTYPE_CODE", i));
	// //,Timestamp,yyyy/MM/dd HH:mm:ss
	// tableParm.setData("OP_DATE", i, result.getValue("OP_DATE",
	// i).replace('-', '/').substring(0, 19));
	// tableParm.setData("STATE", i, result.getValue("STATE", i));
	// tableParm.setData("MR_NO", i, result.getValue("MR_NO", i));
	// tableParm.setData("PAT_NAME", i, result.getValue("PAT_NAME", i));
	// tableParm.setData("REMARK", i, result.getValue("REMARK", i));
	// tableParm.setData("OPT_USER", i, result.getValue("OPT_USER", i));
	// tableParm.setData("OPT_DATE", i, result.getValue("OPT_DATE", i));
	// tableParm.setData("OPT_TERM", i, result.getValue("OPT_TERM", i));
	// }
	// }
	// }
	// else{
	// for (int i = 0; i < result.getCount("OPBOOK_SEQ"); i++) {
	// tableParm.setData("FLG", i, "Y");
	// tableParm.setData("OPBOOK_SEQ", i, result.getValue("OPBOOK_SEQ", i));
	// tableParm.setData("OP_CODE", i, result.getValue("OP_CODE", i));
	// tableParm.setData("SUPTYPE_CODE", i, result.getValue("SUPTYPE_CODE", i));
	// //,Timestamp,yyyy/MM/dd HH:mm:ss
	// tableParm.setData("OP_DATE", i, result.getValue("OP_DATE",
	// i).replace('-', '/').substring(0, 19));
	// tableParm.setData("STATE", i, result.getValue("STATE", i));
	// tableParm.setData("MR_NO", i, result.getValue("MR_NO", i));
	// tableParm.setData("PAT_NAME", i, result.getValue("PAT_NAME", i));
	// tableParm.setData("REMARK", i, result.getValue("REMARK", i));
	// tableParm.setData("OPT_USER", i, result.getValue("OPT_USER", i));
	// tableParm.setData("OPT_DATE", i, result.getValue("OPT_DATE", i));
	// tableParm.setData("OPT_TERM", i, result.getValue("OPT_TERM", i));
	// }
	// }
	// if (tableParm.getCount("OPBOOK_SEQ") <= 0) {
	// this.messageBox("û�д���������ѯ����");
	// return;
	// }
	// //returnString �ŵ�������
	// table.setParmValue(tableParm);
	// }
	// //δ��ɵ���ʾ�ڽ�����
	// else{
	// String sql2 =
	// " SELECT 'Y' AS FLG ,OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,OP_DATE,STATE,MR_NO,"
	// +
	// " PAT_NAME,REMARK,OPT_DATE,OPT_USER,OPT_TERM" +
	// " FROM OPE_PACKAGE" +
	// " WHERE FINAL_FLG = 'N' " +
	// "";
	// StringBuffer SQL2 = new StringBuffer();
	// SQL2.append(sql2);
	//
	// if(!"".equals(packCode)){
	// SQL2.append(" AND PACK_CODE = '"+packCode+ "'");
	// }
	//	
	// if(!"".equals(opeType)){
	// SQL2.append(" AND SUPTYPE_CODE = '"+opeType+ "'");
	// }
	//	      
	// if(!"".equals(opDateS)&&!"".equals(opDateE)){
	// opDateS = opDateS.substring(0, 10).replace('-', ' ').trim();
	// opDateE = opDateE.substring(0, 10).replace('-', ' ').trim();
	// SQL2.append(" AND OP_DATE BETWEEN TO_DATE('"+opDateS+"000000','YYYYMMDDHH24Miss')  AND TO_DATE('"+opDateE+"235959','YYYYMMDDHH24Miss')");
	// }
	// TParm parm2 = new
	// TParm(TJDODBTool.getInstance().select(SQL2.toString()));
	// table.setParmValue(parm2);
	// }
	//       
	// }

	/**
	 * ���Excel
	 */
	public void onExport() {
		String packCode = this.getValueString("PACK_CODE");
		String opeType = this.getValueString("OPE_TYPE");
		String opDateS = this.getValueString("OP_DATE_START");
		String opDateE = this.getValueString("OP_DATE_END");
		String sql2 = " SELECT 'Y' AS FLG ,OPBOOK_SEQ,OP_CODE,SUPTYPE_CODE,OP_DATE,STATE,MR_NO,"
				+ " PAT_NAME,GDVAS_CODE,REMARK,OPT_DATE,OPT_USER,OPT_TERM"
				+ " FROM OPE_PACKAGE" + " WHERE FINAL_FLG = 'Y' " + "";
		StringBuffer SQL2 = new StringBuffer();
		SQL2.append(sql2);

		if (!"".equals(packCode)) {
			SQL2.append(" AND PACK_CODE = '" + packCode + "'");
		}

		if (!"".equals(opeType)) {
			SQL2.append(" AND SUPTYPE_CODE = '" + opeType + "'");
		}

		if (!"".equals(opDateS) && !"".equals(opDateE)) {
			opDateS = opDateS.substring(0, 10).replace('-', ' ').trim();
			opDateE = opDateE.substring(0, 10).replace('-', ' ').trim();
			SQL2.append(" AND OP_DATE BETWEEN TO_DATE('" + opDateS
					+ "000000','YYYYMMDDHH24Miss')  AND TO_DATE('" + opDateE
					+ "235959','YYYYMMDDHH24Miss')");
		}
		TParm parm2 = new TParm(TJDODBTool.getInstance()
				.select(SQL2.toString()));
		table.setParmValue(parm2);
		// �õ�UI��Ӧ�ؼ�����ķ�����UI|XXTag|getThis��
		// TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "�����������");
	}

	/**
	 * �õ�Table����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * �õ�getRadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * �õ�TCheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
