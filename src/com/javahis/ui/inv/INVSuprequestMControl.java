package com.javahis.ui.inv;

import com.dongyang.control.TControl;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;
import jdo.sys.SystemTool;
import java.sql.Timestamp;
import jdo.sys.Operator;
import com.dongyang.data.TParm;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;
import com.dongyang.ui.TRadioButton;

import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import java.awt.Component;

import psyg.graphic.SysObj;

import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;
import jdo.inv.InvSupRequestMTool;
import com.dongyang.manager.TIOM_AppServer;
import jdo.inv.InvSupRequestDTool;
import jdo.util.Manager;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TComboBox;
import jdo.sys.SYSSQL;
import com.dongyang.ui.TMenuItem;

/**
 * <p>
 * Title: 请领作业
 * </p>
 * 
 * <p>
 * Description: 请领作业
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2008
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author zhangy 2013.5.16
 * @version 1.0
 */
public class INVSuprequestMControl extends TControl {

	// 请领主表
	private TTable tableM;
	// 耗材请领
	private TTable tableInv;
	// 手术包请领
	private TTable tablePack;
	// 诊疗包注记
	private String type_flg;
	// 出库方式
	private String pack_mode;

	public INVSuprequestMControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		tableM = getTable("TABLEM");
		tableInv = getTable("TABLEINV");
		tablePack = getTable("TABLEPACK");

		// 初始化TABLE_M的Parm
		TParm reqParm = new TParm();
		String[] reqM = { "REQUEST_NO", "SUPTYPE_CODE", "APP_ORG_CODE",
				"TO_ORG_CODE", "REQUEST_USER", "REQUEST_DATE",
				"REASON_CHN_DESC", "URGENT_FLG", "UPDATE_FLG", "DESCRIPTION" };
		for (int i = 0; i < reqM.length; i++) {
			reqParm.setData(reqM[i], new Vector());
		}
		tableM.setParmValue(reqParm);

		// 初始化TABLE_D的Parm
		TParm invParm = new TParm();
		String[] reqInvD = { "REQUEST_NO", "SEQ_NO", "PACK_MODE",
				"SUPTYPE_CODE", "INV_CODE", "QTY", "STOCK_UNIT", "ACTUAL_QTY",
				"UPDATE_FLG", "OPT_USER", "OPT_DATE", "OPT_TERM",
				"INV_CHN_DESC", "SEQ_FLG" };
		for (int i = 0; i < reqInvD.length; i++) {
			invParm.setData(reqInvD[i], new Vector());
		}
		tableInv.setParmValue(invParm);
		TParm packParm = new TParm();
		String[] reqPackD = { "REQUEST_NO", "SEQ_NO", "PACK_MODE",
				"SUPTYPE_CODE", "INV_CODE", "QTY", "STOCK_UNIT", "ACTUAL_QTY",
				"UPDATE_FLG", "OPT_USER", "OPT_DATE", "OPT_TERM", "PACK_DESC",
				"SEQ_FLG" };
		for (int i = 0; i < reqPackD.length; i++) {
			packParm.setData(reqPackD[i], new Vector());
		}
		tablePack.setParmValue(packParm);

		// 入库日期
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		setValue("REQUEST_DATE", date);
		this.setValue("REQUEST_USER", Operator.getID());

		// 一般出库
		callFunction("UI|TABLEINV|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponentInv");
		// 手术包出库
		callFunction("UI|TABLEPACK|addEventListener",
				TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComponentPack");

		// 添加侦听事件
		addEventListener("TABLEINV->" + TTableEvent.CHANGE_VALUE,
				"onTableINVChangeValue");
		// 添加侦听事件
		addEventListener("TABLEPACK->" + TTableEvent.CHANGE_VALUE,
				"onTablePACKChangeValue");  
		this.setValue("TO_ORG_CODE",Operator.getDept());
		// onChange();
	}

	/**
	 * 调用物资弹出窗口(一般物资出库)
	 *  
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponentInv(Component com, int row, int column) {
		// 弹出INV_BASE对话框的列
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;     
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加INV_BASE弹出窗口
		textfield.setPopupMenuParameter("INVBASE", getConfigParm().newConfig(
				"%ROOT%\\config\\inv\\INVBasePopup.x"));
		// 给新text增加接受sys_fee弹出窗口的回传值
		textfield
				.addEventListener(TPopupMenuEvent.RETURN_VALUE, this, "newInv");
	}

	/**
	 * 一般物资设置明细
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newInv(String tag, Object obj) {
		if (obj == null)
			return;
		// invBase返回的数据包
		TParm parm = (TParm) obj;
		// System.out.println("parm==="+parm);
		String invCode = parm.getValue("INV_CODE");
		TParm invParm = tableInv.getParmValue();
		int row = tableInv.getSelectedRow();
		for (int i = 0; i < invParm.getCount("INV_CODE"); i++) {
			if (i == row) {
				continue;
			}
			if (invCode.equals(invParm.getValue("INV_CODE", i))) {
				this.messageBox("已有相同物资!");
				tableInv.setItem(row, "INV_CODE", "");
				tableInv.setItem(row, "INV_CHN_DESC", "");
				return;
			}
		}
		tableInv.setItem(row, "REQUEST_NO", this.getValueString("REQUEST_NO"));
		tableInv.setItem(row, "INV_CODE", invCode);
		tableInv.setItem(row, "INV_CHN_DESC", parm.getValue("INV_CHN_DESC"));
		tableInv.setItem(row, "STOCK_UNIT", parm.getValue("STOCK_UNIT"));
		tableInv.setItem(row, "QTY", 1);
		tableInv.setItem(row, "OPT_USER", Operator.getID());
		tableInv.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
		tableInv.setItem(row, "OPT_TERM", Operator.getIP());
		tableInv.setItem(row, "SEQ_FLG", parm.getValue("SEQMAN_FLG"));
		tableInv.acceptText();

		onNewRequestD();// 2014-1-1添加
		// System.out.println(tableInv.getParmValue());
	}

	/**
	 * 调用物资弹出窗口(手术包)
	 * 
	 * @param com
	 *            Component
	 * @param row
	 *            int
	 * @param column
	 *            int
	 */
	public void onCreateEditComponentPack(Component com, int row, int column) {
		// 弹出INV_PACK对话框的列
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		TTextField textfield = (TTextField) com;
		textfield.onInit();
		// 给table上的新text增加INV_PACK弹出窗口
		TParm parm = new TParm();
		// 诊疗包注记type_flg
		parm.setData("SEQ_FLG", "Y".equals(type_flg) ? "0" : "1");
		textfield.setPopupMenuParameter("PACK", getConfigParm().newConfig(
				"%ROOT%\\config\\inv\\INVPackPopup.x"), parm);
		// 给新text增加接受INV_PACK弹出窗口的回传值
		textfield.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"newPack");
	}

	/**
	 * 设置明细
	 * 
	 * @param tag
	 *            String
	 * @param obj
	 *            Object
	 */
	public void newPack(String tag, Object obj) {
		if (obj == null)
			return;
		// sysfee返回的数据包
		TParm parm = (TParm) obj;
		// System.out.println("parm==="+parm);
		String invCode = parm.getValue("PACK_CODE");
		TParm packParm = tablePack.getParmValue();
		int row = tablePack.getSelectedRow();
		for (int i = 0; i < packParm.getCount("INV_CODE"); i++) { // 2014-1-5改为INV_CODE
																	// 原为PACK_CODE
			if (i == row) {
				continue;
			}
			if (invCode.equals(packParm.getValue("INV_CODE", i))) {// 2014-1-5改为INV_CODE
																	// 原为PACK_CODE
				this.messageBox("已有相同手术包!");
				tablePack.setItem(row, "INV_CODE", "");
				tablePack.setItem(row, "PACK_DESC", "");
				return;
			}
		}
		tablePack.setItem(row, "REQUEST_NO", this.getValueString("REQUEST_NO"));
		tablePack.setItem(row, "INV_CODE", invCode);
		tablePack.setItem(row, "PACK_DESC", parm.getValue("PACK_DESC"));
		tablePack.setItem(row, "QTY", 1);
		tablePack.setItem(row, "OPT_USER", Operator.getID());
		tablePack.setItem(row, "OPT_DATE", SystemTool.getInstance().getDate());
		tablePack.setItem(row, "OPT_TERM", Operator.getIP());
		tablePack.setItem(row, "SEQ_FLG",
				"0".equals(parm.getValue("SEQ_FLG")) ? "Y" : "N");
		tablePack.acceptText();

		onNewRequestD();// 2014-1-1添加
		// System.out.println(tablePack.getParmValue());
	}

	/**
	 * 表格值改变事件
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTableINVChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 判断数据改变
		if (node.getValue().equals(node.getOldValue()))
			return true;
		int column = node.getColumn();
		if (column == 2) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("请领数量小于或等于0");
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * 表格值改变事件
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTablePACKChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 判断数据改变
		if (node.getValue().equals(node.getOldValue()))
			return true;
		int column = node.getColumn();
		if (column == 2) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("请领数量小于或等于0");
				return true;
			}
			return false;
		}
		return true;
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm parm = new TParm();
		parm.setData("START_DATE", this.getValue("START_DATE"));
		parm.setData("END_DATE", this.getValue("END_DATE"));
		if (getRadioButton("UPDATE_A").isSelected()) {
			parm.setData("UPDATE_FLG_A", "Y");
		} else {
			parm.setData("UPDATE_FLG_B", "Y");
		}
		if (!"".equals(this.getValueString("SUPTYPE_CODE_Q"))) {
			parm.setData("SUPTYPE_CODE", getValueString("SUPTYPE_CODE_Q"));
		}
		if (!"".equals(this.getValueString("REQUEST_NO_Q"))) {
			parm.setData("REQUEST_NO", getValueString("REQUEST_NO_Q"));
		}
		if (!"".equals(this.getValueString("TO_ORG_CODE_Q"))) {
			parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE_Q"));
		}
		if (!"".equals(this.getValueString("APP_ORG_CODE_Q"))) {
			parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE_Q"));
		}

		TParm result = InvSupRequestMTool.getInstance().onQuery(parm);
		if (result == null || result.getCount() <= 0) {
			this.messageBox("没有查询数据");
			tableM.removeRowAll(); // 2013-8-1 添加
			tablePack.removeRowAll(); // 2013-8-1 添加
			tableInv.removeRowAll(); // 2013-8-1 添加
			return;
		}
		tableM.setParmValue(result);
		tablePack.removeRowAll(); // 2013-8-1 添加
		tableInv.removeRowAll(); // 2013-8-1 添加
	}

	/**
	 * 新增方法
	 */
	public void onNew() {
		if (tableM.getSelectedRow() < 0) {
			// 新增请领单主项
			if (!checkDataM()) {
				return;
			}
			// 2013-08-01添加
			if (tableM.getRowCount() > 0) {
				return;
			}
			onNewRequestM();

			onNewRequestD();// 2014-1-1添加
		}
		// 2014-1-1注释start
		// else {
		// // 新增请领单细项
		// onNewRequestD();
		// }
		// 2014-1-1注释end
	}

	/**
	 * 新增请领单主项
	 */
	public void onNewRequestM() {
		// 新增行
		int row = tableM.addRow();
		tableM.setItem(row, "REQUEST_NO", getValueString("REQUEST_NO"));
		tableM.setItem(row, "SUPTYPE_CODE", getValueString("SUPTYPE_CODE"));
		tableM.setItem(row, "TO_ORG_CODE", getValueString("TO_ORG_CODE"));
		tableM.setItem(row, "APP_ORG_CODE", getValueString("APP_ORG_CODE"));
		tableM.setItem(row, "REQUEST_USER", getValueString("REQUEST_USER"));
		tableM.setItem(row, "REQUEST_DATE", getValue("REQUEST_DATE"));
		tableM.setItem(row, "REASON_CHN_DESC", getValueString("REASON_CODE"));
		tableM.setItem(row, "URGENT_FLG", "Y"
				.equals(getValueString("URGENT_FLG")) ? "Y" : "N");
		tableM.setItem(row, "UPDATE_FLG", "0");
		tableM.setItem(row, "DESCRIPTION", getValueString("DESCRIPTION"));

		tableM.setSelectedRow(row);
	}

	/**
	 * 新增请领单细项
	 */
	public void onNewRequestD() {
		if ("0".equals(pack_mode)) {
			int row = tableInv.addRow();
  
			if (row == 0) {
				tableInv.setItem(row, "SEQ_NO", row + 1);
			} else {
				tableInv.setItem(row, "SEQ_NO", tableInv.getParmValue().getInt(
						"SEQ_NO", row - 1) + 1);
			} 
			tableInv.setItem(row, "PACK_MODE", pack_mode);
			tableInv.setItem(row, "SUPTYPE_CODE",
					getValueString("SUPTYPE_CODE"));
			tableInv.setItem(row, "UPDATE_FLG", "0");
		} else {
			int row = tablePack.addRow();
			if (row == 0) {
				tablePack.setItem(row, "SEQ_NO", row + 1);
			} else {
				tablePack.setItem(row, "SEQ_NO", tablePack.getParmValue()
						.getInt("SEQ_NO", row - 1) + 1);
			}
			tablePack.setItem(row, "PACK_MODE", pack_mode);
			tablePack.setItem(row, "SUPTYPE_CODE",
					getValueString("SUPTYPE_CODE"));
			tablePack.setItem(row, "UPDATE_FLG", "0");
		}
	}

	/**
	 * 主项数据检核
	 * 
	 * @return boolean
	 */
	private boolean checkDataM() {
		if ("".equals(this.getValueString("TO_ORG_CODE"))) {
			this.messageBox("供应部门不能为空");
			return false;
		}
		if ("".equals(this.getValueString("APP_ORG_CODE"))) {
			this.messageBox("请领部门不能为空");
			return false;
		}
		if ("".equals(this.getValueString("SUPTYPE_CODE"))) {
			this.messageBox("请领类别不能为空");
			return false;
		}
		return true;
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		clearValue("SUPTYPE_CODE_Q;REQUEST_NO_Q;TO_ORG_CODE_Q;APP_ORG_CODE_Q;"
				+ "REQUEST_NO;TO_ORG_CODE;APP_ORG_CODE;REASON_CODE;"
				+ "SUPTYPE_CODE;PACK_MODE;URGENT_FLG");
		((TRadioButton) this.getComponent("UPDATE_B")).setSelected(true);
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");    
		setValue("REQUEST_DATE", date);

		tableM.removeRowAll();
		tableInv.removeRowAll();
		tablePack.removeRowAll();
		tableInv.setVisible(true);
		tablePack.setVisible(false);
		onChangeRadioButton();
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		// 数据检核
		if (!checkDataM()) {
			return;
		}
		if ("0".equals(pack_mode)) {

			// 2014-01-05添加start
			int row = tableM.getSelectedRow();
			String tag = tableM.getItemString(row, "UPDATE_FLG");
			if (!tag.equals("0")) {
				this.messageBox("已完成的请领单不能修改或删除！");
				return;
			}
			// 2014-01-05添加end

			if (!checkDataINV()) {
				return;
			}
		} else {

			// 2014-01-05添加start
			int row = tableM.getSelectedRow();
			String tag = tableM.getItemString(row, "UPDATE_FLG");
			if (!tag.equals("0")) {
				this.messageBox("已完成的请领单不能修改或删除！");
				return;
			}
			// 2014-01-05添加end

			if (!checkDataPACK()) {
				return;
			}
		}

		TParm result = new TParm();
		TParm parm = new TParm();
		TParm tableMParm = new TParm();
		// 取得主表的数据信息
		// 2013-07-05注释 getTableMData(tableMParm);
		tableMParm = getTableMData(tableMParm); // 07-05改
		// 取得细表的数据信息
		TParm tableDParm = new TParm();
		// 2013-07-05注释 getTableDData(tableDParm);
		tableDParm = getTableDData(tableDParm); // 07-05改

		// 2014-01-05添加start
		if (tableDParm.getCount("INV_CODE") < 1) {
			this.messageBox("请领单细项无信息，无法保存！");
			return;
		}
		// 2014-01-05添加end

		if ("".equals(this.getValueString("REQUEST_NO"))) {
			// 新增保存
			String request_no = SystemTool.getInstance().getNo("ALL", "INV",
					"INV_SUPREQUEST", "No");
			tableMParm.setData("REQUEST_NO", request_no);
			parm.setData("INV_SUPREQUESTM", tableMParm.getData());
			for (int i = 0; i < tableDParm.getCount("SEQ_NO"); i++) {
				tableDParm.addData("REQUEST_NO", request_no);
			}
			parm.setData("INV_SUPREQUESTD", tableDParm.getData());
			// 新增申请单
			result = TIOM_AppServer.executeAction(
					"action.inv.INVSupRequestAction", "onInsert", parm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
			this.setValue("REQUEST_NO", request_no);
		} else {
			// 更新保存
			String request_no = this.getValueString("REQUEST_NO");
			tableMParm.setData("REQUEST_NO", request_no);
			parm.setData("INV_SUPREQUESTM", tableMParm.getData());
			for (int i = 0; i < tableDParm.getCount("SEQ_NO"); i++) {
				tableDParm.addData("REQUEST_NO", request_no);
			}
			parm.setData("INV_SUPREQUESTD", tableDParm.getData());
			// 更新申请单
			result = TIOM_AppServer.executeAction(
					"action.inv.INVSupRequestAction", "onUpdate", parm);
			if (result == null || result.getErrCode() < 0) {
				this.messageBox("E0001");
				return;
			}
		}
		this.messageBox("P0001");
		// 2013-11-24注释 onPrint();
		onClear();
	}

	/**
	 * 取得主表的数据信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getTableMData(TParm parm) {
		int row = tableM.getSelectedRow(); 
		parm.setData("SUPTYPE_CODE", tableM.getItemString(row, "SUPTYPE_CODE"));
		parm.setData("APP_ORG_CODE", tableM.getItemString(row, "APP_ORG_CODE"));
		parm.setData("TO_ORG_CODE", tableM.getItemString(row, "TO_ORG_CODE"));
		parm.setData("REQUEST_DATE", tableM.getItemTimestamp(row,
				"REQUEST_DATE"));
		parm.setData("REQUEST_USER", tableM.getItemString(row, "REQUEST_USER"));
		parm.setData("REASON_CHN_DESC", tableM.getItemString(row,
				"REASON_CHN_DESC"));
		parm.setData("DESCRIPTION", tableM.getItemString(row, "DESCRIPTION"));
		parm.setData("URGENT_FLG", tableM.getItemString(row, "URGENT_FLG"));
		parm.setData("UPDATE_FLG", tableM.getItemString(row, "UPDATE_FLG"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", SystemTool.getInstance().getDate());
		parm.setData("OPT_TERM", Operator.getIP());
		return parm;
	}

	/**
	 * 取得细表的数据信息
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getTableDData(TParm parm) {
		Timestamp datetime = SystemTool.getInstance().getDate();
		if ("0".equals(pack_mode)) {
			tableInv.acceptText();
			for (int i = 0; i < tableInv.getRowCount() - 1; i++) {// 2014-1-1添加-1
				parm.addData("SEQ_NO", tableInv.getItemData(i, "SEQ_NO"));
				parm.addData("PACK_MODE", tableInv
						.getItemString(i, "PACK_MODE"));
				parm.addData("SUPTYPE_CODE", tableInv.getItemString(i,
						"SUPTYPE_CODE"));
				parm.addData("INV_CODE", tableInv.getParmValue().getValue(
						"INV_CODE", i));
				parm.addData("QTY", tableInv.getItemDouble(i, "QTY"));
				parm.addData("STOCK_UNIT", tableInv.getItemString(i,
						"STOCK_UNIT"));
				parm.addData("ACTUAL_QTY", 0);
				parm.addData("UPDATE_FLG", tableInv.getItemString(i,
						"UPDATE_FLG"));
				parm.addData("OPT_USER", Operator.getID());
				parm.addData("OPT_DATE", datetime);
				parm.addData("OPT_TERM", Operator.getIP());
			}
		} else {
			Map<String, Integer> map = new TreeMap<String, Integer>();
			Map<String, String> mapPackCode = new TreeMap<String, String>();
			String packCode = "";
			int qty = 0;
			tablePack.acceptText();

			for (int i = 0; i < tablePack.getRowCount() - 1; i++) {// 2014-1-1添加-1
				packCode = tablePack.getParmValue().getValue("INV_CODE", i);
				if (map.containsKey(packCode)) {
					qty = (Integer) map.get(packCode);
					qty = qty + tablePack.getParmValue().getInt("QTY", i);
					map.remove(packCode);
					map.put(packCode, qty);
					mapPackCode.put(packCode, packCode);

				} else {
					map
							.put(packCode, tablePack.getParmValue().getInt(
									"QTY", i));
					mapPackCode.put(packCode, packCode);
				}
			}
			Object s[] = map.keySet().toArray();
			Object sInv[] = mapPackCode.keySet().toArray();
			System.out.println("Size:" + map.size());
			for (int j = 0; j < map.size(); j++) {
				String invCode = (String) mapPackCode.get(sInv[j]);
				int qtyIn = (Integer) map.get(s[j]);
				parm.addData("SEQ_NO", tablePack.getItemData(j, "SEQ_NO"));
				parm.addData("PACK_MODE", tablePack.getItemString(j,
						"PACK_MODE"));
				parm.addData("SUPTYPE_CODE", tablePack.getItemString(j,
						"SUPTYPE_CODE"));
				parm.addData("INV_CODE", invCode);
				parm.addData("QTY", qtyIn);
				parm.addData("STOCK_UNIT", "");
				parm.addData("ACTUAL_QTY", 0);
				parm.addData("UPDATE_FLG", tablePack.getItemString(j,
						"UPDATE_FLG"));
				parm.addData("OPT_USER", Operator.getID());
				parm.addData("OPT_DATE", datetime);
				parm.addData("OPT_TERM", Operator.getIP());
			}
		}
		// System.out.println("保存parm："+parm);
		return parm;
	}

	/**
	 * 请领细项
	 * 
	 * @return boolean
	 */
	private boolean checkDataINV() {
		tableInv.acceptText();
		TParm parmInv = tableInv.getParmValue();
		for (int i = 0; i < parmInv.getCount("INV_CODE") - 1; i++) {// 2014-1-1添加-1
			if ("".equals(parmInv.getValue("INV_CODE", i))) {
				this.messageBox("请领物资不能为空");
				return false;
			}
			if (parmInv.getDouble("QTY", i) <= 0) {
				this.messageBox("请领数量不能小于或等于0");
				return false;
			}
		}
		return true;
	}

	/**
	 * 请领细项
	 * 
	 * @return boolean
	 */
	private boolean checkDataPACK() {
		tablePack.acceptText();
		TParm parmPack = tablePack.getParmValue();
		for (int i = 0; i < parmPack.getCount("INV_CODE") - 1; i++) {// 2014-1-1添加-1
			if ("".equals(parmPack.getValue("INV_CODE", i))) {
				this.messageBox("请领物资不能为空");
				return false;
			}
			if (parmPack.getDouble("QTY", i) <= 0) {
				this.messageBox("请领数量不能小于或等于0");
				return false;
			}
		}
		return true;
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		int tableM_row = tableM.getSelectedRow();
		int tableInv_row = tableInv.getSelectedRow();
		int tablePack_row = tablePack.getSelectedRow();

		// 2014-01-05添加start
		int row = tableM.getSelectedRow();
		String tag = tableM.getItemString(row, "UPDATE_FLG");
		if (!tag.equals("0")) {
			this.messageBox("已完成的请领单不能修改或删除！");
			return;
		}
		// 2014-01-05添加end

		if ("".equals(this.getValueString("REQUEST_NO"))) {// 新建的请领单
			// 直接删除，不更新数据
			if ("0".equals(pack_mode)) {
				if (tableInv_row == tableInv.getRowCount() - 1) {
					return;
				}

				if (tableInv_row >= 0) {
					// 删除细项
					tableInv.removeRow(tableInv_row);
				} else {
					if (this.messageBox("删除", "确定是否删除订单", 2) == 0) {
						// 删除主项
						tableM.removeRow(tableM_row);
						tableInv.removeRowAll(); // 2013-8-1 添加
					}

				}
			} else {
				if (tablePack_row == tablePack.getRowCount() - 1) {
					return;
				}

				if (tablePack_row >= 0) {
					// 删除细项
					tablePack.removeRow(tablePack_row);
				} else {
					if (this.messageBox("删除", "确定是否删除订单", 2) == 0) {
						// 删除主项
						tableM.removeRow(tableM_row);
						tablePack.removeRowAll(); // 2013-8-1 添加
					}
				}
			}
		} else { // 已建，但尚未出库的请领单
			TParm result = new TParm();
			// 删除并且更新数据
			if ("0".equals(pack_mode)) {
				if (tableInv_row >= 0) {
					// 删除细项
					result = InvSupRequestDTool.getInstance().onDelete(
							tableInv.getParmValue().getRow(tableInv_row));
					if (result == null || result.getErrCode() < 0) {
						this.messageBox("删除失败");
						return;
					}
					tableInv.removeRow(tableInv_row);
					this.messageBox("删除成功");
				} else {
					// 删除主项
					if (this.messageBox("删除", "确定是否删除订单", 2) == 0) {
						TParm parm = new TParm();
						parm.setData("REQUEST_NO", this
								.getValueString("REQUEST_NO"));
						result = TIOM_AppServer.executeAction(
								"action.inv.INVSupRequestAction", "onDelete",
								parm);
						if (result == null || result.getErrCode() < 0) {
							this.messageBox("删除失败");
							return;
						}
						onTableMClicked(); // 2014-1-1添加
						// 2014-1-1注释 tableM.removeRow(tableM_row);
						this.messageBox("删除成功");
						onClear();
					}
				}
			} else {
				if (tablePack_row >= 0) {
					// 删除细项
					result = InvSupRequestDTool.getInstance().onDelete(
							tablePack.getParmValue().getRow(tablePack_row));
					if (result == null || result.getErrCode() < 0) {
						this.messageBox("删除失败");
						return;
					}
					onTableMClicked(); // 2014-1-1添加
					// 2014-1-1注释 tablePack.removeRow(tableInv_row);
					this.messageBox("删除成功");
				} else {
					// 删除主项
					if (this.messageBox("删除", "确定是否删除订单", 2) == 0) {
						TParm parm = new TParm();
						parm.setData("REQUEST_NO", this
								.getValueString("REQUEST_NO"));
						result = TIOM_AppServer.executeAction(
								"action.inv.INVSupRequestAction", "onDelete",
								parm);
						if (result == null || result.getErrCode() < 0) {
							this.messageBox("删除失败");
							return;
						}
						tableM.removeRow(tableM_row);
						this.messageBox("删除成功");
						onClear();
					}
				}
			}
		}
	}

	/**
	 * 打印请领单
	 */
	public void onPrint() {
		if ("".equals(this.getValueString("REQUEST_NO"))) {
			this.messageBox("没有打印数据");
			return;
		}
		// 打印数据
		TParm date = new TParm();
		// 表头数据
		date.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion())
				+ "供应室请领单");
		date.setData("REQUEST_NO", "TEXT", "请领单号: "
				+ this.getValueString("REQUEST_NO"));
		date.setData("REQUEST_DATE", "TEXT", "请领日期: "
				+ this.getValueString("REQUEST_DATE").substring(0, 10).replace(
						'-', '/'));
		date.setData("DATE", "TEXT", "制表日期: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		date.setData("TO_ORG_CODE", "TEXT", "供应部门: "
				+ this.getTextFormat("TO_ORG_CODE").getText());
		date.setData("APP_ORG_CODE", "TEXT", "请领部门: "
				+ this.getTextFormat("APP_ORG_CODE").getText());
		date.setData("SUPTYPE_CODE", "TEXT", "供应类别: "
				+ this.getTextFormat("SUPTYPE_CODE").getText());
		date.setData("PACK_MODE", "TEXT", "出库方式: "
				+ this.getComboBox("PACK_MODE").getSelectedName());
		// 表格数据
		TParm parm = new TParm();
		if ("0".equals(pack_mode)) {
			// 一般物资出库
			for (int i = 0; i < tableInv.getRowCount() - 1; i++) {// 2014-1-1添加-1
				parm.addData("INV_CHN_DESC", tableInv.getItemString(i,
						"INV_CHN_DESC"));
				parm.addData("QTY", tableInv.getItemDouble(i, "QTY"));
				TParm stock_unit = new TParm(TJDODBTool.getInstance().select(
						SYSSQL.getSYSUnit(tableInv.getItemString(i,
								"STOCK_UNIT"))));
				parm.addData("UNIT", stock_unit.getValue("UNIT_CHN_DESC", 0));
			}
			parm.setCount(parm.getCount("INV_CHN_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "INV_CHN_DESC");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			date.setData("TABLE", parm.getData());
			// 表尾数据
			date.setData("CHECK", "TEXT", "审核： ");
			date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\INV\\SupRequestInv.jhw",
					date);
		} else {
			// 手术包出库
			for (int i = 0; i < tablePack.getRowCount() - 1; i++) {// 2014-1-1添加-1
				parm.addData("PACK_DESC", tablePack.getItemString(i,
						"PACK_DESC"));
				parm.addData("QTY", tablePack.getItemDouble(i, "QTY"));
			}
			parm.setCount(parm.getCount("PACK_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "PACK_DESC");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			date.setData("TABLE", parm.getData());
			// 表尾数据
			date.setData("CHECK", "TEXT", "审核： ");
			date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			// 调用打印方法
			this.openPrintWindow(
					"%ROOT%\\config\\prt\\INV\\SupRequestPackNew.jhw", date);
		}
	}

	/**
	 * 表格单击事件
	 */
	public void onTableMClicked() {
		TParm tableMParm = tableM.getParmValue()
				.getRow(tableM.getSelectedRow());
		if ("".equals(tableMParm.getValue("REQUEST_NO"))) {
			return;
		}
		this.setValueForParm("REQUEST_NO;REQUEST_USER;REQUEST_DATE;"
				+ "TO_ORG_CODE;APP_ORG_CODE;REASON_CODE;"
				+ "SUPTYPE_CODE;URGENT_FLG", tableMParm);
		onSupTypeSelected();
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
		if ("0".equals(pack_mode)) {
			result = InvSupRequestDTool.getInstance().onQueryInv(parm);
			tableInv.setParmValue(result);
		} else {
			result = InvSupRequestDTool.getInstance().onQueryPack(parm);
			tablePack.setParmValue(result);
		}

		onNewRequestD();// 2014-1-1添加
	}

	/**
	 * 打开自动申请界面
	 */
	public void onOpenAuto() {
		if (!checkDataM()) {
			return;
		}
		TParm parmDiag = (TParm) openDialog("%ROOT%\\config\\inv\\INV_Request.x");
		// 手术包可见
		tableInv.setVisible(false);
		tablePack.setVisible(true);
		if (tableM.getSelectedRow() < 0) {
			// 新增请领单主项
			// 2013-08-01添加
			if (tableM.getRowCount() > 0) {
				return;
			}
			onNewRequestM();
		}
		System.out.println("STATE:" + parmDiag.getCount("STATE"));
		for (int j = 0; j < parmDiag.getCount("STATE"); j++) {
			// 试验多种的 同icd多个包
			String type = this.getValueString("SUPTYPE_CODE");
			String opCode = parmDiag.getValue("OP_CODE", j).toString();
			String gdvac_code = parmDiag.getValue("GDVAS_CODE", j).toString();
			//opCode 术式   type 包类型   gdvac_code  入录血管
			TParm parm = onOpcode(opCode, type,gdvac_code);
			for (int k = 0; k < parm.getCount("PACK_CODE"); k++) {
				int row = tablePack.addRow();
				if (row == 0) {
					tablePack.setItem(row, "SEQ_NO", row + 1);
				} else {
					tablePack.setItem(row, "SEQ_NO", tablePack.getParmValue()
							.getInt("SEQ_NO", row - 1) + 1);
				}
				tablePack.setItem(row, "REQUEST_NO", this
						.getValueString("REQUEST_NO"));

				tablePack.setItem(row, "INV_CODE", parm
						.getValue("PACK_CODE", k));
				tablePack.setItem(row, "PACK_DESC", parm.getValue("PACK_DESC",
						k));
				tablePack.setItem(row, "QTY", parm.getDouble("QTY", k));
				tablePack.setItem(row, "PACK_MODE", pack_mode);
				tablePack.setItem(row, "SUPTYPE_CODE", "0".equals(parm
						.getDouble("SEQ_FLG", k)) ? "100003" : "100001");
				tablePack.setItem(row, "UPDATE_FLG", "0");
			}
			// SEQ_NO;PACK_DESC;QTY;PACK_MODE;SUPTYPE_CODE;UPDATE_FLG
		}
		int row = tablePack.addRow();
		if (row == 0) {
			tablePack.setItem(row, "SEQ_NO", row + 1);
		} else {
			tablePack.setItem(row, "SEQ_NO", tablePack.getParmValue().getInt(
					"SEQ_NO", row - 1) + 1);
		}
		tablePack.setItem(row, "REQUEST_NO", this.getValueString("REQUEST_NO"));
		// tablePack.setItem(row, "INV_CODE", "");
		// tablePack.setItem(row, "PACK_DESC", "");
		// tablePack.setItem(row, "QTY", "");
		tablePack.setItem(row, "PACK_MODE", pack_mode);
		tablePack.setItem(row, "SUPTYPE_CODE", getValueString("SUPTYPE_CODE"));
		tablePack.setItem(row, "UPDATE_FLG", "0");
	}

	public TParm onOpcode(String opCode, String type ,String gdvac_code) {
		// "0".equals(parm.getDouble("SEQ_FLG",k)) ? "100003" : "100001"
		String sql = " SELECT PACK_CODE,QTY,PACK_DESC,SEQ_FLG FROM OPE_ICDPACKAGE"
				+ " WHERE OPERATION_ICD = '" + opCode + "'";
		StringBuffer SQL = new StringBuffer();
		SQL.append(sql);
		if (type.equals("100003")) {
			SQL.append(" AND SEQ_FLG = '0' ");
		}
		if (type.equals("100001")) {
			SQL.append(" AND SEQ_FLG = '1' ");  
		} 
		if (!gdvac_code.equals("")) {  
			SQL.append(" AND GDVAS_CODE = '"+gdvac_code+"' ");
			SQL.append(" UNION ");
			String sql2 = " SELECT PACK_CODE,QTY,PACK_DESC,SEQ_FLG FROM OPE_ICDPACKAGE"
				+ " WHERE OPERATION_ICD = '" + opCode + "'";
			SQL.append(sql2);
			if (type.equals("100003")) {      
				SQL.append(" AND SEQ_FLG = '0' ");
			}
			if (type.equals("100001")) {    
				SQL.append(" AND SEQ_FLG = '1' ");
			}        
			SQL.append(" AND GDVAS_CODE IS NULL ");
		}   
		System.out.println("SQL" + SQL);      
		TParm selParm = new TParm(TJDODBTool.getInstance().select(
				SQL.toString()));
		return selParm;
	}

	/**
	 * 供应类别改变事件
	 */
	public void onSupTypeSelected() {
		// 供应类别
		String sup_type = this.getValueString("SUPTYPE_CODE");
		TParm parm = new TParm(TJDODBTool.getInstance().select(
				INVSQL.getINVSupType(sup_type)));
		pack_mode = parm.getValue("PACK_MODE", 0); 
		this.setValue("PACK_MODE", pack_mode);    
		if ("0".equals(pack_mode)) {          
			tableInv.setVisible(true);
			tablePack.setVisible(false);           
		} else {
			tableInv.setVisible(false);  
			tablePack.setVisible(true);
		}
		type_flg = parm.getValue("TYPE_FLG", 0);
	}

	/**
	 * 变更单选按钮
	 */
	public void onChangeRadioButton() {
		if (getRadioButton("UPDATE_B").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TMenuItem) getComponent("new")).setEnabled(true);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			tableInv.setLockColumns("0,3,4,5,6");
			tablePack.setLockColumns("0,3,4,5,6");
		} else {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TMenuItem) getComponent("new")).setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(false);
			tableInv.setLockColumns("all");
			tablePack.setLockColumns("all");
		}

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
	 * 得到TRadioButton对象
	 * 
	 * @param tagName
	 *            String
	 * @return TRadioButton
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	/**
	 * 得到TextFormat对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}

	/**
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

}
