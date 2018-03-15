package com.javahis.ui.med;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.table.TableModel;

import jdo.hl7.Hl7Communications;
import jdo.inw.InwForOdiTool;
import jdo.med.MEDApplyTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TTextFieldEvent;
import com.dongyang.ui.util.Compare;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:检验送检清单
 * </p>
 * 
 * <p>
 * Description: 检验送检清单
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2012
 * </p>
 * 
 * <p>
 * Company: ProperSoft
 * </p>
 * 
 * @author shibl
 * @version 1.0
 */
public class MedLisSendControl extends TControl {
	private Compare compare = new Compare();
	private boolean ascending = false;
	private TableModel model;
	private int sortColumn = -1;
	// 会调用HL7接口的数据
	TParm sendHL7Parm = new TParm();
	/**
	 * TABLE
	 */
	private static String TABLE = "TABLE";

	/**
	 * 初始化
	 */
	public void onInit() {
		super.onInit();
		TTextField bar = ((TTextField) getComponent("BAR_CODE"));
		bar.grabFocus();
		// Document barDoc = bar.getDocument();
		// barDoc.addDocumentListener(new javax.swing.event.DocumentListener() {
		// public void changedUpdate(DocumentEvent e) {
		// // TODO 自动生成方法存根
		// }
		//
		// public void insertUpdate(DocumentEvent e) {
		// onBarCode();
		// // TODO 自动生成方法存根
		// EventQueue.invokeLater(new Runnable() {
		// public void run() {
		// bar.setValue("");
		// }
		// });
		// bar.requestFocus();
		// }
		// public void removeUpdate(DocumentEvent e) {
		// // TODO 自动生成方法存根;
		// }
		// });
		// 给TABLE中的CHECKBOX添加侦听事件
		callFunction("UI|BAR_CODE|addEventListener",
				TTextFieldEvent.KEY_PRESSED, this, "onBarCode");
		getTTable(TABLE).addEventListener(TTableEvent.CHECK_BOX_CLICKED, this,
				"onCheckBoxValue");
		this.setValue("STATION_CODE", Operator.getStation());
		this.setValue("RE_USER", Operator.getID());
		Timestamp sysDate = SystemTool.getInstance().getDate();
		String tDate = StringTool.getString(sysDate, "yyyyMMdd");
		// 默认设置起始日期
		this.setValue("START_DATE",
				StringTool.getTimestamp(tDate + "000000", "yyyyMMddHHmmss"));
		// 默认设置终止日期
		this.setValue("END_DATE",
				StringTool.getTimestamp(tDate + "235959", "yyyyMMddHHmmss"));
		callFunction("UI|RE_START|setEnabled", false);
		callFunction("UI|RE_END|setEnabled", false);
		callFunction("UI|print|setEnabled", false);
		// 排序监听
		addListener(getTTable(TABLE));
		
		Object obj = this.getParameter();
		if (obj != null) {
			if (obj instanceof String) {
				this.setPopedem(String.valueOf(obj), true);
			}
		}
		
		if (this.getPopedem("H")) {
			this.setValue("STATION_CODE", "");
		}
	}

	/**
	 * 增加数据
	 */
	public void onBarCode() {
		// 未接收
		boolean NFlag = (Boolean) this.callFunction("UI|ISRE2|isSelected");
		// 开始时间
		String start = this.getValueString("START_DATE");
		// 结束时间
		String end = this.getValueString("END_DATE");
		// 接收人员
		String reUser = this.getValueString("RE_USER");
		// 病区
		String stationCode = this.getValueString("STATION_CODE");

		String startStr = start.substring(0, 19).replaceAll("-", "")
				.replaceAll(":", "");
		String endStr = end.substring(0, 19).replaceAll("-", "")
				.replaceAll(":", "");
		String sql = this.getMedLisSql();
		if (!stationCode.equals("") && stationCode.length() > 0)
			sql += " AND A.STATION_CODE='" + stationCode + "'";
		if (!startStr.equals("") && startStr.length() > 0) {
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		if (!endStr.equals("") && endStr.length() > 0) {
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		String barCode = this.getValueString("BAR_CODE");
		if (!barCode.equals("") && barCode.length() > 0)
			sql += " AND B.MED_APPLY_NO='" + barCode + "'";
		if (NFlag) {
			sql += " AND A.LIS_RE_DATE IS NULL";
		} else {
			sql += " AND A.LIS_RE_DATE IS NOT NULL";
			if (!reUser.equals("") && reUser.length() > 0)
				sql += " AND LIS_RE_USER='" + reUser + "'";
		}
		// 查询结果
		TParm parm = new TParm(this.getDBTool().select(sql));
		if (parm.getCount() <= 0) {
			this.messageBox("没有数据");
			return;
		}
		boolean flg = true;
		// 表数据
		TParm tableParm = this.getTable("TABLE").getParmValue();
		if (tableParm != null) {
			for (int i = 0; i < tableParm.getCount("MED_APPLY_NO"); i++) {
				if (tableParm.getValue("MED_APPLY_NO", i).equals(
						parm.getValue("MED_APPLY_NO", 0))) {
					this.messageBox("已扫描此条码！");
					flg = false;
					break;
				}
			}
			if (flg) {
				tableParm.addParm(parm);
				this.getTable("TABLE").setParmValue(tableParm);
			}
		} else {
			this.getTable("TABLE").setParmValue(parm);
		}
		this.setValue("BAR_CODE", "");
	}

	/**
	 * 
	 * @param obj
	 */
	public void onCheckBoxValue(Object obj) {
		TTable table = (TTable) obj;
		table.acceptText();
		int col = table.getSelectedColumn();
		String columnName = this.getTTable(TABLE).getDataStoreColumnName(col);
		int row = table.getSelectedRow();
		TParm parm = table.getParmValue();
		TParm tableParm = parm.getRow(row);
		String applicationNo = tableParm.getValue("MED_APPLY_NO");
		if ("FLG".equals(columnName)) {
			int rowCount = parm.getCount("ORDER_DESC");
			for (int i = 0; i < rowCount; i++) {
				if (i == row)
					continue;
				if (applicationNo.equals(parm.getValue("MED_APPLY_NO", i))) {
					parm.setData("FLG", i, parm.getBoolean("FLG", i) ? "N"
							: "Y");
				}
			}
			table.setParmValue(parm);
		}
	}

	/**
	 * 保存动作
	 */
	public void onSave() {
		TParm parm = this.getTable("TABLE").getParmValue();
		Timestamp now = TJDODBTool.getInstance().getDBTime();
		if (parm == null) {
			this.messageBox("无保存数据！");
			return;
		}
		TParm inParm = new TParm();
		TParm hl7Parm = new TParm();
		TParm result = new TParm();
		String applicationNo = "";
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("FLG", i))
				continue;
			applicationNo = parm.getValue("MED_APPLY_NO", i);
			inParm.setData("LIS_RE_USER", this.getValueString("RE_USER"));
			inParm.setData("LIS_RE_DATE", now);
			inParm.setData("CASE_NO", parm.getValue("CASE_NO", i));
			inParm.setData("ORDER_NO", parm.getValue("ORDER_NO", i));
			inParm.setData("ORDER_SEQ", parm.getInt("ORDER_SEQ", i));
			inParm.setData("START_DTTM", parm.getValue("START_DTTM", i));
			inParm.setData("OPT_USER", Operator.getID());
			inParm.setData("OPT_TERM", Operator.getIP());
			inParm.setData("APPLICATION_NO", applicationNo);
			// HL7PARM
			sendHL7Parm.addData("MED_APPLY_NO",
					parm.getValue("MED_APPLY_NO", i));
			sendHL7Parm.addData("CASE_NO", parm.getValue("CASE_NO", i));
			sendHL7Parm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			sendHL7Parm.addData("ORDER_SEQ", parm.getInt("ORDER_SEQ", i));
			sendHL7Parm.addData("START_DTTM", parm.getValue("START_DTTM", i));
			sendHL7Parm.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
			sendHL7Parm.addData("CAT1_TYPE", parm.getValue("CAT1_TYPE", i));
			
			// modify by wangb 2016/11/18
			if (this.getPopedem("H")) {
				// 一期临床体检送检交接
				result = MEDApplyTool.getInstance().updateMedApplyLisReceiveData(inParm);
			} else {
				result = InwForOdiTool.getInstance().updateOdidspnmLisData(
						inParm);
			}

			if (result.getErrCode() < 0) {
				this.messageBox("保存失败！");
				return;
			}
		}
		this.messageBox("保存成功！");
		sendHL7Mes();
		this.getTable("TABLE").removeRowAll();
	}

	/**
	 * 重送消息文件
	 */
	public void onGenSendHl7() {
		TParm parm = this.getTable("TABLE").getParmValue();
		for (int i = 0; i < parm.getCount(); i++) {
			if (!parm.getBoolean("FLG", i))
				continue;
			sendHL7Parm.addData("MED_APPLY_NO",
					parm.getValue("MED_APPLY_NO", i));
			sendHL7Parm.addData("CASE_NO", parm.getValue("CASE_NO", i));
			sendHL7Parm.addData("ORDER_NO", parm.getValue("ORDER_NO", i));
			sendHL7Parm.addData("ORDER_SEQ", parm.getInt("ORDER_SEQ", i));
			sendHL7Parm.addData("START_DTTM", parm.getValue("START_DTTM", i));
			sendHL7Parm.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
		}
		sendHL7Mes();
	}

	/**
	 * 查询动作
	 */
	public void onQuery() {
		// 未接收
		boolean NFlag = (Boolean) this.callFunction("UI|ISRE2|isSelected");
		// 开始时间
		String start = this.getValueString("START_DATE");
		// 结束时间
		String end = this.getValueString("END_DATE");
		// 接收人员
		String reUser = this.getValueString("RE_USER");
		// 病区
		String stationCode = this.getValueString("STATION_CODE");

		String sql = this.getMedLisSql();
		if (!start.equals("") && start.length() > 0) {
			String startStr = start.substring(0, 19).replaceAll("-", "")
					.replaceAll(":", "");
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE>=TO_DATE('" + startStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		if (!end.equals("") && end.length() > 0) {
			String endStr = end.substring(0, 19).replaceAll("-", "")
					.replaceAll(":", "");
			if (this.getPopedem("H")) {
				sql += " AND A.BLOOD_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			} else {
				sql += " AND A.NS_EXEC_DATE<=TO_DATE('" + endStr
				+ "','YYYYMMDD HH24MISS')";
			}
		}
		if (!stationCode.equals("") && stationCode.length() > 0)
			sql += " AND A.STATION_CODE='" + stationCode + "'";
		if (NFlag) {
			sql += " AND A.LIS_RE_DATE IS NULL";
		} else {
			sql += " AND A.LIS_RE_DATE IS NOT NULL";
			if (!reUser.equals("") && reUser.length() > 0)
				sql += " AND LIS_RE_USER='" + reUser + "'";
			// 开始时间
			String restart = this.getValueString("RE_START");
			// 结束时间
			String reend = this.getValueString("RE_END");
			if (!start.equals("") && start.length() > 0) {
				String REstartStr = restart.substring(0, 19)
						.replaceAll("-", "").replaceAll(":", "");
				sql += " AND A.LIS_RE_DATE>=TO_DATE('" + REstartStr
						+ "','YYYYMMDD HH24MISS')";
			}
			if (!end.equals("") && end.length() > 0) {
				String REendStr = reend.substring(0, 19).replaceAll("-", "")
						.replaceAll(":", "");
				sql += " AND A.LIS_RE_DATE<=TO_DATE('" + REendStr
						+ "','YYYYMMDD HH24MISS')";
			}
		}
		sql += " ORDER BY  A.LIS_RE_DATE,A.MR_NO DESC";
		TParm parm = new TParm(this.getDBTool().select(sql));
		if (parm.getCount() <= 0) {
			this.getTable("TABLE").removeRowAll();
			this.messageBox("无查询数据！");
			return;
		}
		this.getTable("TABLE").setParmValue(parm);
	}

	/**
	 * 改变事件
	 */
	public void onChangeButton() {
		// 已接收
		boolean YFlag = (Boolean) this.callFunction("UI|ISRE1|isSelected");
		if (YFlag) {
			callFunction("UI|save|setEnabled", false);
			callFunction("UI|print|setEnabled", true);
			callFunction("UI|RE_START|setEnabled", true);
			callFunction("UI|RE_END|setEnabled", true);
			Timestamp sysDate = SystemTool.getInstance().getDate();
			String tDate = StringTool.getString(sysDate, "yyyyMMdd");
			// 默认设置起始日期
			this.setValue("RE_START",
					StringTool.getTimestamp(tDate + "000000", "yyyyMMddHHmmss"));
			// 默认设置终止日期
			this.setValue("RE_END", StringTool.getTimestamp(("" + sysDate)
					.substring(0, 19).replaceAll("-", "").replaceAll(":", ""),
					"yyyyMMdd HHmmss"));
		} else {
			callFunction("UI|save|setEnabled", true);
			callFunction("UI|print|setEnabled", false);
			callFunction("UI|RE_START|setEnabled", false);
			callFunction("UI|RE_END|setEnabled", false);
			this.setValue("RE_START", "");
			this.setValue("RE_END", "");
		}
	}

	/**
	 * 打印方法
	 */
	public void onPrint() {
		TParm parm = this.getTable("TABLE").getParmValue();
		if (parm == null) {
			this.messageBox("无打印数据");
			return;
		}
		TParm printData = new TParm();
		int count = 0;
		for (int i = 0; i < parm.getCount("MR_NO"); i++) {
			if (!parm.getBoolean("FLG", i))
				continue;
			printData.addData("BED_NO", parm.getValue("BED_NO", i));
			printData.addData("MR_NO", parm.getValue("MR_NO", i));
			printData.addData("PAT_NAME", parm.getValue("PAT_NAME", i));
			printData.addData("ORDER_DESC", parm.getValue("ORDER_DESC", i));
			printData.addData("MED_APPLY_NO", parm.getValue("MED_APPLY_NO", i));
			printData.addData("NS_EXEC_DATE", parm.getValue("NS_EXEC_DATE", i)
					.substring(5, 16));
			printData.addData("LIS_RE_DATE", parm.getValue("LIS_RE_DATE", i)
					.substring(5, 16));
			printData.addData("DR_NOTE", parm.getValue("DR_NOTE", i));
			count++;
		}
		printData.setCount(count);
		printData.addData("SYSTEM", "COLUMNS", "BED_NO");
		printData.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		printData.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		printData.addData("SYSTEM", "COLUMNS", "MED_APPLY_NO");
		printData.addData("SYSTEM", "COLUMNS", "NS_EXEC_DATE");
		printData.addData("SYSTEM", "COLUMNS", "LIS_RE_DATE");
		printData.addData("SYSTEM", "COLUMNS", "DR_NOTE");
		printData.addData("SYSTEM", "COLUMNS", "MR_NO");
		TParm printParm = new TParm();
		printParm.setData("TITLE", "TEXT", "检验标本送检清单");
		printParm.setData("STATION_CODE", "TEXT",
				"病区：" + getStationDesc(this.getValueString("STATION_CODE")));
		if (this.getPopedem("H")) {
			printParm.setData("STATION_CODE", "TEXT", "");
		}
		
		// 开始时间
		String restart = this.getValueString("RE_START");
		// 结束时间
		String reend = this.getValueString("RE_END");
		printParm.setData("DATE", "TEXT", "交接起日："
				+ restart.substring(0, 19).replaceAll("-", "/") + " " + "交接迄日："
				+ reend.substring(0, 19).replaceAll("-", "/"));
		printParm.setData("TABLE", printData.getData());
		this.openPrintWindow("%ROOT%\\config\\prt\\MED\\MedLisSend.jhw",
				printParm);
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		this.getTable("TABLE").removeRowAll();
		this.setValue("BAR_CODE", "");
	}

	/**
	 * 全选
	 */
	public void onCheckBoxClicked() {
		boolean Flag = (Boolean) this.callFunction("UI|EXE|isSelected");
		TParm parm = this.getTable("TABLE").getParmValue();
		TTable table = this.getTable("TABLE");
		for (int i = 0; i < parm.getCount(); i++) {
			table.setItem(i, "FLG", Flag);
		}
	}

	/**
	 * 取得病区
	 * 
	 * @param stationCode
	 * @return
	 */
	public String getStationDesc(String stationCode) {
		TParm parm = new TParm(getDBTool().select(
				" SELECT STATION_DESC " + " FROM SYS_STATION "
						+ " WHERE STATION_CODE='" + stationCode + "'"));

		return parm.getValue("STATION_DESC", 0);
	}

	/**
	 * 得到TTable对象
	 * 
	 * @param tagName
	 *            String
	 * @return TTable
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到TTextField对象
	 * 
	 * @param tagName
	 *            String
	 * @return TTextField
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

	/**
	 * getDBTool 数据库工具实例
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 检验查询SQL
	 * 
	 * @return
	 */
	private String getMedLisSql() {
		String sql = "";
		if (this.getPopedem("H")) {
			sql = "SELECT 'Y' AS FLG,C.PAT_NAME,A.MR_NO,A.ORDER_DESC,A.APPLICATION_NO,B.MED_APPLY_NO,"
				+ " TO_CHAR(A.BLOOD_DATE,'YYYY/MM/DD HH24:MI:SS') AS NS_EXEC_DATE,A.DR_NOTE,"
				+ " TO_CHAR(A.LIS_RE_DATE,'YYYY/MM/DD HH24:MI:SS') AS LIS_RE_DATE,A.LIS_RE_USER,A.CASE_NO,A.CAT1_TYPE "
				+ " FROM MED_APPLY A,HRM_ORDER B ,SYS_PATINFO C "
				+ " WHERE A.CASE_NO=B.CASE_NO "
				+ " AND A.SEQ_NO=B.SEQ_NO "
				+ " AND A.APPLICATION_NO = B.MED_APPLY_NO "
				+ " AND A.MR_NO=C.MR_NO "
				+ " AND A.CAT1_TYPE='LIS' "
				+ " AND A.STATUS <> '9' ";
		} else {
			sql = "SELECT C.BED_NO_DESC AS BED_NO,D.PAT_NAME,A.MR_NO,A.ORDER_DESC,B.MED_APPLY_NO,"
					+ " TO_CHAR(A.NS_EXEC_DATE,'YYYY/MM/DD HH24:MI:SS') AS NS_EXEC_DATE,'Y' AS FLG,B.DR_NOTE,"
					+ " TO_CHAR(A.LIS_RE_DATE,'YYYY/MM/DD HH24:MI:SS') AS LIS_RE_DATE,A.LIS_RE_USER,A.CASE_NO,"
					+ " A.ORDER_NO,A.ORDER_SEQ,A.START_DTTM,A.CAT1_TYPE "
					+ " FROM ODI_DSPNM A,ODI_ORDER B ,SYS_BED C,SYS_PATINFO D "
					+ " WHERE A.CASE_NO=B.CASE_NO "
					+ " AND A.ORDER_NO=B.ORDER_NO "
					+ " AND A.ORDER_SEQ=B.ORDER_SEQ "
					+ " AND A.BED_NO=C.BED_NO "
					+ " AND A.MR_NO=D.MR_NO "
					+ " AND A.CAT1_TYPE='LIS' "
					+ " AND A.HIDE_FLG='N' AND A.NS_EXEC_CODE IS NOT NULL ";
		}
		return sql;
	}

	/**
	 * 发送HL7消息
	 * 
	 * @param catType
	 *            医令分类
	 * @param caseNo
	 *            String 就诊号
	 * @param applictionNo
	 *            String 条码号
	 */
	private void sendHL7Mes() {
		int count = ((Vector) sendHL7Parm.getData("CASE_NO")).size();
		if (count <= 0) {
			return;
		}
		List list = new ArrayList();
		Map map = new HashMap();
		String sql = "";
		TParm result = new TParm();
		TParm parm = new TParm();
		
		for (int i = 0; i < count; i++) {
			// shibl 20120830 modify
			if (map.get(sendHL7Parm.getValue("MED_APPLY_NO", i)) != null) {
				continue;
			}
			map.put(sendHL7Parm.getValue("MED_APPLY_NO", i),
					sendHL7Parm.getValue("MED_APPLY_NO", i));
			
			if (this.getPopedem("H")) {
				parm = new TParm();
				parm.setData("CASE_NO", sendHL7Parm.getValue("CASE_NO", i));
				parm.setData("LAB_NO", sendHL7Parm.getValue("MED_APPLY_NO", i));
				parm.setData("CAT1_TYPE", sendHL7Parm.getValue("CAT1_TYPE", i));
				parm.setData("ADM_TYPE", "H");
			} else {
				sql = " SELECT * FROM ODI_ORDER WHERE CASE_NO ='"
					+ sendHL7Parm.getValue("CASE_NO", i) + "' AND ORDER_NO='"
					+ sendHL7Parm.getValue("ORDER_NO", i) + "' AND ORDER_SEQ="
					+ sendHL7Parm.getInt("ORDER_SEQ", i) + "";
				result = new TParm(TJDODBTool.getInstance().select(sql));
				parm = new TParm();
				parm.setData("CASE_NO", result.getValue("CASE_NO", 0));
				parm.setData("LAB_NO", result.getValue("MED_APPLY_NO", 0));
				parm.setData("CAT1_TYPE", result.getValue("CAT1_TYPE", 0));
				parm.setData("ORDER_NO", result.getValue("ORDER_NO", 0));
				parm.setData("ORDER_SEQ", result.getInt("ORDER_SEQ", 0));
			}
			
			list.add(parm);
		}
		// 清空parm
		// modify by wangb 2016/12/7
		// 页面在初次执行送检保存不关闭界面的情况下，已完成再次扫码重送，会报错无法重送。
		// 原因为初次保存完毕后全局变量被清空，但清空的只是数值，列依然保留，且清空后sendHL7Parm中的count为0。
		// 页面不关闭的前提下，点击重送时组装好的sendHL7Parm中的count依然为0导致调用remove时越界异常
//		while (sendHL7Parm.getCount("CASE_NO") > 0) {
//			sendHL7Parm.removeRow(0);
//		}
		sendHL7Parm = new TParm();
		// 调用接口
		TParm resultParm = Hl7Communications.getInstance().Hl7SendLis(list);
		if (resultParm.getErrCode() < 0)
			this.messageBox(resultParm.getErrText());
		else
			this.messageBox("发送成功");
	}

	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable(TABLE).getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable(TABLE).getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);
				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());
		getTTable(TABLE).setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	/**
	 * 拿到TABLE
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}
}
