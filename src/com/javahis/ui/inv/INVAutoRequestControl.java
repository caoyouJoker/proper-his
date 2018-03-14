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
 * Title: 手术生成手术包申请
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
	// action的路径
	private static final String actionName = "action.inv.INVAutoRequsetAction";

	/**
	 * 初始化方法
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
		// //完成与未完成标记转换
		// onCharge();
		// 设置弹出菜单
		getTextField("OPERATION_ICD").setPopupMenuParameter("UD",
				getConfigParm().newConfig("%ROOT%\\config\\sys\\sysOpICD.x"),
				parmIcd);
		// 定义接受返回值方法
		getTextField("OPERATION_ICD").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturnIcd");
		// 设备DD录事件
		getTable("TABLE").addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onTableComponent");
		onInitTable();
	}

	/**
	 * tabledd监听事件
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
	 * 接受返回值方法(icd)
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
	 * 清空
	 */
	public void onClear() {
		this.clearValue("OPERATION_ICD;OPT_CHN_DESC;PACK_CODE;PACK_DESC;QTY");
		table = getTable("TABLE");
		table.removeRowAll();
	}

	/**
	 * 保存方法 (传回)
	 */
	public void onSave() {
		if (!CheckData()) {
			return;
		}
		TParm result = new TParm();
		for (int i = 0; i < table.getRowCount(); i++) {
			// 选中的传回
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
			// 查询时保存，保存时更新完成标记并返回
			this.messageBox("P0001");
			this.setReturnValue(result);
			this.closeWindow();
		}
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean CheckData() {
		if ("".equals(getValueString("OP_DATE_START"))) {
			this.messageBox("手术开始时间不能为空");
			return false;
		}
		if ("".equals(getValueString("OP_DATE_END"))) {
			this.messageBox("手术结束时间不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		if (table.getSelectedRow() < 0) {
			this.messageBox("请选择删除项");
			return;
		}
		TParm parm = new TParm();
		parm.setData("INV_CODE", this.getValueString("INV_CODE"));
		parm.setData("SUP_CODE", this.getValueString("SUP_CODE"));
		TParm result = TIOM_AppServer.executeAction(
				"action.inv.INVAgentAction", "onDelete", parm);
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("删除失败");
			return;
		}
		this.messageBox("删除成功");
		this.onClear();
	}

	/**
	 * 从webservice初始化table方法
	 */
	public void onInitTable() {
		String packCode = this.getValueString("PACK_CODE");
		String opeType = this.getValueString("OPE_TYPE");
		String opCode = "";
		String supTypeCode = "";

		String opDateS = this.getValueString("OP_DATE_START");
		String opDateE = this.getValueString("OP_DATE_END");

		String state = "";        
		// 0 申请， 1 排程完毕 ，2手术完成
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
		//System.out.println("webservice传回result:::" + result);
		// 获取result

		// 未完成
		// 初始化肯定表中没有数值,采取保存进入数值
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
				this.messageBox("没有传回手术查询数据");
				return;
			}
			// returnString 放到界面上
			table.setParmValue(tableParm);
		}
		// 未完成的显示在界面上
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
	 * 查询方法
	 */
	public void onQuery() {

		TParm result = table.getParmValue();
		// 获取result

		// 未完成
		// 初始化肯定表中没有数值,采取保存进入数值
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
				// 查询时保存，保存时更新完成标记并返回
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
				this.messageBox("没有传回手术查询数据");
				return;
			}
			// returnString 放到界面上
			table.setParmValue(tableParm);
		}
		// 未完成的显示在界面上
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
	// * 查询方法
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
	// //0 申请， 1 排程完毕 ，2手术完成
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
	// System.out.println("webservice传回result:::"+result);
	// //获取result
	//          
	// //未完成
	// //初始化肯定表中没有数值,采取保存进入数值
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
	// // INV_AGENT保存数据
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
	// //查询时保存，保存时更新完成标记并返回
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
	// this.messageBox("没有传回手术查询数据");
	// return;
	// }
	// //returnString 放到界面上
	// table.setParmValue(tableParm);
	// }
	// //未完成的显示在界面上
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
	 * 汇出Excel
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
		// 得到UI对应控件对象的方法（UI|XXTag|getThis）
		// TTable table = (TTable) callFunction("UI|Table|getThis");
		if (table.getRowCount() > 0)
			ExportExcelUtil.getInstance().exportExcel(table, "手术包申请表");
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * 得到getRadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * 得到TCheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
