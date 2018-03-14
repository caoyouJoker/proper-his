package com.javahis.ui.inv;

import com.dongyang.control.TControl;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.dongyang.ui.TMenuItem;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.ui.TTextField;
import com.dongyang.data.TParm;
import java.awt.Component;

import com.javahis.ui.spc.ExportXmlUtil;
import com.javahis.util.StringUtil;
import com.dongyang.ui.TRadioButton;
import java.util.Vector;

import org.dom4j.Document;

import com.dongyang.ui.TTableNode;
import com.dongyang.util.TypeTool;

import jdo.spc.INDSQL;
import jdo.sys.SystemTool;
import jdo.sys.Operator;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.data.TNull;
import jdo.inv.InvPurorderDTool;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.TCheckBox;
import com.javahis.system.textFormat.TextFormatINVOrg;
import com.dongyang.jdo.TJDODBTool;
import jdo.inv.INVSQL;

/**
 * <p>
 * Title: 物资订购管理审核Control
 * </p>
 * 
 * <p>
 * Description: 物资订购管理审核Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author lit 2013.04.15
 * @version 1.0
 */

public class INVPurorderCheckControl extends TControl {
	public INVPurorderCheckControl() {
	}

	private TTable table_m;

	private TTable table_d;

	// 赠与权限
	private boolean gift_flg = true;

	// 全部部门权限
	private boolean dept_flg = true;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 初始画面数据
		initPage();
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		if (!dept_flg) {
			if ("".equals(this.getValueString("ORG_CODE_Q"))) {
				this.messageBox("请选择查询部门");
				return;
			}
		}
		TParm parm = new TParm();
		// 订购来源
		if (this.getRadioButton("STATIC_NO_B").isSelected()) {
			parm.setData("FROM_TYPE", "1");
		} else if (this.getRadioButton("STATIC_NO_C").isSelected()) {
			parm.setData("FROM_TYPE", "0");
		}
		// 订购状态
		if (this.getRadioButton("FINISH").isSelected()) {
			parm.setData("CHECK_FLG", "Y");
		} else if (this.getRadioButton("NOTFINISH").isSelected()) {
			parm.setData("CHECK_FLG", "N");
		}
		// 订购部门
		if (!"".equals(this.getValueString("ORG_CODE_Q"))) {
			parm.setData("ORG_CODE", this.getValueString("ORG_CODE_Q"));
		}
		// 实际入库部门
		if (!"".equals(this.getValueString("CON_ORG_Q"))) {
			parm.setData("CON_ORG", this.getValueString("CON_ORG_Q"));
		}
		// 供货厂商
		if (!"".equals(this.getValueString("SUP_CODE_Q"))) {
			parm.setData("SUP_CODE", this.getValueString("SUP_CODE_Q"));
		}
		// 查询时间
		if (!"".equals(this.getValueString("START_DATE"))
				&& !"".equals(this.getValueString("END_DATE"))) {
			parm.setData("START_DATE", this.getValue("START_DATE"));
			parm.setData("END_DATE", this.getValue("END_DATE"));
		}
		// 订购单号
		if (!"".equals(this.getValueString("PURORDER_NO_Q"))) {
			parm.setData("PURORDER_NO", this.getValueString("PURORDER_NO_Q"));
		}
		TParm inparm = new TParm();
		inparm.setData("PUR_M", parm.getData());
		// 查询
		TParm result = TIOM_AppServer.executeAction(
				"action.inv.INVPurorderAction", "onQueryM", inparm);
		if (result == null || result.getCount() <= 0) {
			this.messageBox("没有查询数据");
			table_m.removeRowAll();
			return;
		}
		table_m.setParmValue(result);
	}

	/**
	 * 保存方法
	 */
	public void onSave() {
		if (!checkData()) {
			onClear();
			return;
		}
		TParm parm = new TParm();
		getPurOrderMData(parm); // 取得订购单主项数据
		TParm result = new TParm();
		if ("".equals(this.getValueString("PURORDER_NO"))) {
			return;
		} else {
			result = TIOM_AppServer.executeAction(
					"action.inv.INVPurorderAction", "onUpdateCheckFlg", parm);
		}
		if (result == null || result.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
		onClear();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		getTextFormat("SUP_CODE").setEnabled(true);
		getTextFormat("ORG_CODE").setEnabled(true);
		String clearString = "START_DATE;END_DATE;ORG_CODE_Q;SUP_CODE_Q;PURORDER_NO_Q;"
				+ "PURORDER_DATE;SUP_CODE;PURORDER_NO;ORG_CODE;REASON_CODE;"
				+ "RES_DELIVERY_DATE;STATIO_NO;DESCRIPTION;SELECT_ALL;SUM_MONEY;CON_ORG";
		this.clearValue(clearString);
		this.setValue("ORG_CODE", "");
		this.setValue("SUP_CODE", "");
		Timestamp date = StringTool.getTimestamp(new Date());
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		setValue("PURORDER_DATE", date);
		// 画面状态
		// ( (TMenuItem) getComponent("delete")).setEnabled(false);
		// ( (TMenuItem) getComponent("export")).setEnabled(false);
		table_m.setSelectionMode(0);
		table_m.removeRowAll();
		table_d.setSelectionMode(0);
		table_d.removeRowAll();

		createNewRow();
	}

	/**
	 * 删除方法
	 */
	public void onDelete() {
		int row_m = table_m.getSelectedRow();
		int row_d = table_d.getSelectedRow();
		TParm parm = new TParm();
		TParm result = new TParm();
		parm.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
		if (row_d >= 0) {
			// 删除订购单细项
			if ("".equals(this.getValueString("PURORDER_NO"))) {
				table_d.removeRow(row_d);
				this.setValue("SUM_MONEY", getSumMoney());
				return;
			} else if (this.messageBox("删除", "确定是否删除订单细项", 2) == 0) {
				if (!checkUpdateFlg()) {
					this.messageBox("订购单部分数据已验收不可删除");
					return;
				}
				table_d.removeRow(row_d);
				this.setValue("SUM_MONEY", getSumMoney());
				this.onSave();
				this.messageBox("删除成功");
			}
		} else if (row_m >= 0) {
			// 删除订购单主项
			if (this.messageBox("删除", "确定是否删除订单", 2) == 0) {
				if (!checkUpdateFlg()) {
					this.messageBox("订购单部分数据已验收不可删除");
					return;
				}
				result = TIOM_AppServer.executeAction(
						"action.inv.INVPurorderAction", "onDelete", parm);
				if (result == null || result.getErrCode() < 0) {
					this.messageBox("删除失败");
					return;
				}
				table_m.removeRow(row_m);
				table_d.removeRowAll();
				this.messageBox("删除成功");
			}
		} else {
			this.messageBox("没有选中项");
			return;
		}
	}

	/**
	 * 主项表格(TABLE_M)单击事件
	 */
	public void onTableMClicked() {
		int row = table_m.getSelectedRow();
		if (row != -1) {
			getTextFormat("SUP_CODE").setEnabled(false);
			getTextFormat("ORG_CODE").setEnabled(false);
			((TMenuItem) getComponent("delete")).setEnabled(true);
			table_d.setSelectionMode(0);
			// 主项信息(TABLE中取得)
			setValue("PURORDER_DATE", table_m.getItemTimestamp(row,
					"PURORDER_DATE"));
			setValue("SUP_CODE", table_m.getItemString(row, "SUP_CODE"));
			setValue("PURORDER_NO", table_m.getItemString(row, "PURORDER_NO"));
			setValue("ORG_CODE", table_m.getItemString(row, "ORG_CODE"));
			setValue("REASON_CODE", table_m.getItemString(row, "REN_CODE"));
			setValue("RES_DELIVERY_DATE", table_m.getItemTimestamp(row,
					"RES_DELIVERY_DATE"));
			setValue("STATIO_NO", table_m.getItemString(row, "STATIO_NO"));
			setValue("DESCRIPTION", table_m.getItemString(row, "DESCRIPTION"));
			setValue("SUM_MONEY", table_m.getParmValue().getDouble(
					"ACTUAL_AMT", row));
			setValue("CON_ORG", table_m.getParmValue().getValue("CON_ORG", row));
			// 明细信息
			TParm parm = new TParm();
			parm.setData("PURORDER_NO", table_m.getItemString(row,
					"PURORDER_NO"));

			TParm result = InvPurorderDTool.getInstance().onQuery(parm);
			if (result == null || result.getCount() <= 0) {
				this.messageBox("没有订购明细");
				return;
			}
			table_d.removeRowAll();
			table_d.setParmValue(result);
			this.createNewRow();
		}
	}

	/**
	 * 主项表格(TABLE_D)单击事件
	 */
	public void onTableDClicked() {
		int row = table_d.getSelectedRow();
		if (row != -1) {
			((TMenuItem) getComponent("delete")).setEnabled(true);
		}
		// fux modify 20131206
		TParm rowParm = table_d.getParmValue().getRow(row);
		// 设置弹出菜单
		if (rowParm.getValue("INV_CODE").length() == 0) {
			return;
		}
		// 状态条显示
		else {
			callFunction("UI|setSysStatus", rowParm.getValue("INV_CODE") + " "
					+ rowParm.getValue("INV_CHN_DESC") + " "
					+ rowParm.getValue("DESCRIPTION"));
		}
	}

	/**
	 * 全选事件
	 */
	public void onSelectAll() {
		table_d.acceptText();
		if (table_d.getRowCount() < 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}

		if (getCheckBox("SELECT_ALL").isSelected()) {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
					continue;
				}
				table_d.setItem(i, "IERMINATE_FLG", "Y");
				this.setValue("SUM_MONEY", getSumMoney());
			}
		} else {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
					continue;
				}
				table_d.setItem(i, "IERMINATE_FLG", "N");
				this.setValue("SUM_MONEY", 0);
			}
		}
	}

	/**
	 * 导出采购单为XML文件
	 */
	@SuppressWarnings("unchecked")
	public void onExportXml() {
		if (getTable("TABLE_M").getSelectedRow() > -1) {
			// 要导出来的细项总数
			TParm parm = table_d.getParmValue();
			List list = new ArrayList();
			for (int i = 0; i < parm.getCount() - 1; i++) {
				TParm t = (TParm) parm.getRow(i);
				Map<String, String> map = new LinkedHashMap();
				// map.put("warehouse", "K932290201");
				// pharmacycode 物质 为 01 药品 02
				map.put("pharmacycode", "01");
				// 送货地址
				map.put("deliverycode", "922268901");
				// 申请部门
				map.put("orgCode", this.getValueString("ORG_CODE"));
				// 直接入库部门
				map.put("conOrg", this.getValueString("CON_ORG"));
				map.put("cstcode", t.getValue("SUP_CODE"));
				map.put("goods", t.getValue("INV_CODE"));
				map.put("goodname", t.getValue("INV_CHN_DESC"));
				map.put("spec", t.getValue("DESCRIPTION"));
				map.put("msunitno", t.getValue("BILL_UNIT"));
				map.put("producer", t.getValue("MAN_CODE"));
				map.put("billqty", t.getValue("PURORDER_QTY"));
				map.put("prc", t.getValue("PURORDER_PRICE"));
				// 订购单号
				String purorder_no = t.getValue("PURORDER_NO");
				String seq = t.getValue("SEQ_NO");
				map.put("purchaseid", purorder_no + "-" + seq);
				list.add(map);
			}

			try {
				Document doc = ExportXmlUtil.createXml(list);
				ExportXmlUtil.exeSaveXml(doc, "移货申请.xml");
			} catch (Exception e) {
				System.out.println("错误信息==============" + e.toString());
			}

		} else {
			this.messageBox("请选择要生成的采购单!");
			return;
		}
	}

	/**
	 * 取得订购单主项数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 * 
	 * 
	 * 
	 */
	private TParm getPurOrderMData(TParm parm) {
		TParm inparm = new TParm();
		// 订购单号
		if ("".equals(this.getValueString("PURORDER_NO"))) {
			inparm.setData("PURORDER_NO", SystemTool.getInstance().getNo("ALL",
					"INV", "INV_PURORDER", "No"));
		} else {
			inparm.setData("PURORDER_NO", this.getValueString("PURORDER_NO"));
		}
		// 订购部门
		inparm.setData("ORG_CODE", this.getValue("ORG_CODE"));
		// 供货厂商
		inparm.setData("SUP_CODE", this.getValue("SUP_CODE"));
		// 订购日期
		inparm.setData("PURORDER_DATE", this.getValue("PURORDER_DATE"));
		// 订购金额
		inparm.setData("PURORDER_AMT", this.getValue("SUM_MONEY"));
		// 订购原因
		inparm.setData("REN_CODE", this.getValueString("REASON_CODE"));
		// 备注
		inparm.setData("DESCRIPTION", this.getValue("DESCRIPTION"));
		// 交货日期
		if (!"".equals(getValueString("RES_DELIVERY_DATE"))) {
			inparm.setData("RES_DELIVERY_DATE", getValue("RES_DELIVERY_DATE"));
		} else {
			inparm.setData("RES_DELIVERY_DATE", new TNull(Timestamp.class));
		}
		// 单据状态
		inparm.setData("FINAL_FLG", "N");
		// 订购来源
		inparm.setData("FROM_TYPE",
				"".equals(this.getValueString("STATIO_NO")) ? "1" : "0");
		// 计划单号
		inparm.setData("STATIO_NO", this.getValueString("STATIO_NO"));
		// 品质扣款
		inparm.setData("QUALITY_DEDUCT_AMT", 0);
		// 罚款金额
		inparm.setData("FORFEIT_AMT", 0);
		// 实际金额
		inparm.setData("ACTUAL_AMT", this.getValue("SUM_MONEY"));
		// OPT
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", StringTool.getTimestamp(new Date()));
		inparm.setData("OPT_TERM", Operator.getIP());
		if (this.getRadioButton("CHECK1").isSelected()) {
			inparm.setData("CHECK_FLG", "Y");
		} else {
			inparm.setData("CHECK_FLG", "N");
		}
		parm.setData("PUR_M", inparm.getData());
		return parm;
	}

	/**
	 * 取得订购单细项数据
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getPurOrderDData(TParm parm) {
		TParm inparm = new TParm();
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if ("".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
				continue;
			}
			// 1.订购单号
			inparm.addData("PURORDER_NO", parm.getParm("PUR_M").getValue(
					"PURORDER_NO"));
			// 2.序号
			inparm.addData("SEQ_NO", i);
			// 3.物资代码
			inparm.addData("INV_CODE", table_d.getParmValue().getValue(
					"INV_CODE", i));
			// 4.订购数量
			inparm.addData("PURORDER_QTY", table_d.getItemDouble(i,
					"PURORDER_QTY"));
			// 5.赠送数量
			inparm.addData("GIFT_QTY", table_d.getItemDouble(i, "GIFT_QTY"));
			// 6.订购单位
			inparm.addData("BILL_UNIT", table_d.getItemString(i, "BILL_UNIT"));
			// 7.订购价格
			inparm.addData("PURORDER_PRICE", table_d.getItemDouble(i,
					"CONTRACT_PRICE"));
			// 8.订购金额
			inparm.addData("PURORDER_AMT", table_d.getItemDouble(i,
					"PURORDER_AMT"));
			// 9.入库数量累计
			inparm.addData("STOCKIN_SUM_QTY", table_d.getItemDouble(i,
					"STOCKIN_SUM_QTY"));
			// 10.未交量
			inparm.addData("UNDELIVERY_QTY", table_d.getItemDouble(i,
					"UNDELIVERY_QTY"));
			// 11.折扣率
			inparm.addData("DISCOUNT_RATE", table_d.getItemDouble(i,
					"DISCOUNT_RATE"));
			// 12.赠送比率
			inparm.addData("GIFT_RATE", table_d.getItemDouble(i, "GIFT_RATE"));
			// 13.品质扣款
			inparm.addData("QUALITY_DEDUCT_AMT", table_d.getItemDouble(i,
					"QUALITY_DEDUCT_AMT"));
			// 14.赠送注记
			inparm.addData("GIFT_FLG",
					table_d.getItemDouble(i, "GIFT_QTY") > 0 ? "Y" : "N");
			// 15.物资状态
			inparm.addData("PROCESS_TYPE", "Y".equals(table_d.getItemString(i,
					"IERMINATE_FLG")) ? "3" : "0");
			// 16.终止注记
			inparm.addData("IERMINATE_FLG", "Y".equals(table_d.getItemString(i,
					"IERMINATE_FLG")) ? "Y" : "N");
			// 17,18,19 OPT
			inparm.addData("OPT_USER", Operator.getID());
			inparm.addData("OPT_DATE", StringTool.getTimestamp(new Date()));
			inparm.addData("OPT_TERM", Operator.getIP());
		}
		parm.setData("PUR_D", inparm.getData());
		return parm;
	}

	/**
	 * 数据检核
	 * 
	 * @return boolean
	 */
	private boolean checkData() {
		if ("".equals(this.getValueString("SUP_CODE"))) {
			this.messageBox("供货厂商不能为空");
			return false;
		}
		if ("".equals(this.getValueString("ORG_CODE"))) {
			this.messageBox("订购部门不能为空");
			return false;
		}
		if (table_d.getRowCount() < 1) {
			this.messageBox("没有订购细项信息");
			return false;
		}
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
				if (table_d.getItemDouble(i, "PURORDER_QTY") <= 0) {
					this.messageBox("订购数量不能小于或等于0");
					return false;
				}
			}
		}

		return true;
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		/**
		 * 权限控制 权限1:一般个人无赠与权限,只显示自已所属科室;无赠与录入功能 权限2:一般个人赠与权限,只显示自已所属科室;包含赠与录入功能
		 * 权限9:最大权限,显示全院药库部门包含赠与录入功能
		 */
		// 赠与权限
		if (!this.getPopedem("giftEnabled")) {
			TTable table_d = getTable("TABLE_D");
			table_d.setLockColumns("0,1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16");
			gift_flg = false;
		}
		// 显示全院药库部门
		TextFormatINVOrg inv_org = (TextFormatINVOrg) this
				.getTextFormat("ORG_CODE");
		TextFormatINVOrg inv_org_q = (TextFormatINVOrg) this
				.getTextFormat("ORG_CODE_Q");
		// fux modify 20131207 start
		TextFormatINVOrg inv_org_con_q = (TextFormatINVOrg) this
				.getTextFormat("ORG_CODE_Q");
		if (!this.getPopedem("deptAll")) {
			inv_org.setOperatorId(Operator.getID());
			inv_org_q.setOperatorId(Operator.getID());
			inv_org_con_q.setOperatorId(Operator.getID());
			dept_flg = false;
		} else {
			inv_org.setOperatorId("");
			inv_org_q.setOperatorId("");
			inv_org_con_q.setOperatorId("");
			dept_flg = true;
		}
		// fux modify 20131207 end
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		setValue("PURORDER_DATE", date);

		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
		// 初始化TABLE_M的Parm
		TParm parmD = new TParm();
		String[] purD = { "PURORDER_NO", "SEQ_NO", "INV_CODE", "PURORDER_QTY",
				"GIFT_QTY", "BILL_UNIT", "PURORDER_PRICE", "PURORDER_AMT",
				"STOCKIN_SUM_QTY", "UNDELIVERY_QTY", "DISCOUNT_RATE",
				"GIFT_RATE", "QUALITY_DEDUCT_AMT", "GIFT_FLG", "PROCESS_TYPE",
				"IERMINATE_FLG", };
		for (int i = 0; i < purD.length; i++) {
			parmD.setData(purD[i], new Vector());
		}
		table_d.setParmValue(parmD);
		// 注册激发INDSupOrder弹出的事件
		table_d.addEventListener(TTableEvent.CREATE_EDIT_COMPONENT, this,
				"onCreateEditComoponentUD");
		// TABLE_D值改变事件
		addEventListener("TABLE_D->" + TTableEvent.CHANGE_VALUE,
				"onTableDChangeValue");

		createNewRow();
	}

	/**
	 * 当TABLE创建编辑控件时长期
	 * 
	 * @param com
	 * @param row
	 * @param column
	 */
	public void onCreateEditComoponentUD(Component com, int row, int column) {
		if (column != 1)
			return;
		if (!(com instanceof TTextField))
			return;
		if ("".equals(this.getValueString("SUP_CODE"))) {
			this.messageBox("供货厂商不能为空");
			return;
		}
		if ("".equals(this.getValueString("ORG_CODE"))) {
			this.messageBox("订购部门不能为空");
			return;
		}
		if (getTextFormat("SUP_CODE").isEnabled()) {
			getTextFormat("SUP_CODE").setEnabled(false);
		}
		if (getTextFormat("ORG_CODE").isEnabled()) {
			getTextFormat("ORG_CODE").setEnabled(false);
		}

		TParm parm = new TParm();
		parm.setData("SUP_CODE", getValueString("SUP_CODE"));
		TTextField textFilter = (TTextField) com;
		textFilter.onInit();
		// 设置弹出菜单
		textFilter.setPopupMenuParameter("UI", getConfigParm().newConfig(
				"%ROOT%\\config\\inv\\INVSupInv.x"), parm);
		// 定义接受返回值方法
		textFilter.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		table_d.acceptText();
		String inv_desc = parm.getValue("INV_CHN_DESC");
		if (!StringUtil.isNullString(inv_desc)) {
			// 库存主档查询
			String org_code = this.getValueString("ORG_CODE");
			String inv_code = parm.getValue("INV_CODE");
			TParm stockMParm = new TParm(TJDODBTool.getInstance().select(
					INVSQL.getInvStockM(org_code, inv_code)));
			if (stockMParm == null || stockMParm.getCount("INV_CODE") <= 0) {
				this.messageBox("没有设定库存主档信息");
				return;
			}
			setTableDValue(parm, table_d.getSelectedRow());
			if (table_d.getRowCount() == table_d.getSelectedRow() + 1) {
				createNewRow();
			}
		}
	}

	/**
	 * 表格值改变事件
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTableDChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 判断数据改变
		if (node.getValue().equals(node.getOldValue()))
			return true;
		// Table的列名
		String columnName = node.getTable().getDataStoreColumnName(
				node.getColumn());
		int row = node.getRow();
		if ("PURORDER_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("订购数量不能小于或等于0");
				return true;
			} else {
				// 赠送量
				table_d.setItem(row, "GIFT_QTY", qty
						* table_d.getItemDouble(row, "GIFT_RATE"));
				// 金额小计
				table_d.setItem(row, "PURORDER_AMT", qty
						* table_d.getItemDouble(row, "CONTRACT_PRICE")
						* table_d.getItemDouble(row, "DISCOUNT_RATE")
						- table_d.getItemDouble(row, "QUALITY_DEDUCT_AMT"));
				// 未交数量
				table_d.setItem(row, "UNDELIVERY_QTY", qty);
				// 入库数量
				table_d.setItem(row, "IN_QTY", (qty + table_d.getItemDouble(
						row, "GIFT_QTY"))
						* table_d.getParmValue().getDouble("STOCK_QTY", row));

				table_d.setItem(row, "PURORDER_QTY", qty);
				this.setValue("SUM_MONEY", getSumMoney());
				return false;
			}
		}
		if ("GIFT_QTY".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty < 0) {
				this.messageBox("赠送数量不能小于0");
				return true;
			} else {
				// 入库数量
				table_d.setItem(row, "IN_QTY", (qty + table_d.getItemDouble(
						row, "PURORDER_QTY"))
						* table_d.getParmValue().getDouble("STOCK_QTY", row));
				return false;
			}
		}
		if ("QUALITY_DEDUCT_AMT".equals(columnName)) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty < 0) {
				this.messageBox("品质扣款不能小于0");
				return true;
			} else {
				// 金额小计
				table_d.setItem(row, "PURORDER_AMT", table_d.getItemDouble(row,
						"PURORDER_AMT")
						- qty);

				table_d.setItem(row, "QUALITY_DEDUCT_AMT", qty);
				this.setValue("SUM_MONEY", getSumMoney());
			}
		}
		return false;
	}

	/**
	 * 表格(TABLE)复选框改变事件
	 * 
	 * @param obj
	 */
	public void onTableCheckBoxClicked(Object obj) {
		// 获得点击的table对象
		TTable tableDown = (TTable) obj;
		// 只有执行该方法后才可以在光标移动前接受动作效果（框架需要）
		tableDown.acceptText();
		// 获得选中的列
		int column = tableDown.getSelectedColumn();
		if (column == 0) {
			this.setValue("SUM_MONEY", this.getSumMoney());
		}
	}

	/**
	 * 计算总金额
	 * 
	 * @return 
	 */
	private double getSumMoney() {
		table_d.acceptText();
		double sum = 0;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!"Y".equals(table_d.getItemString(i, "IERMINATE_FLG"))) {
				sum += table_d.getItemDouble(i, "PURORDER_QTY")
						* table_d.getItemDouble(i, "DISCOUNT_RATE")
						* table_d.getItemDouble(i, "CONTRACT_PRICE")
						- table_d.getItemDouble(i, "QUALITY_DEDUCT_AMT");
			}
		}
		return StringTool.round(sum, 2);
	}

	/**
	 * 检测订购单是否有验收数据
	 * 
	 * @return boolean
	 */
	private boolean checkUpdateFlg() {
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!"".equals(table_d.getParmValue().getValue("INV_CODE", i))) {
				if (table_d.getItemDouble(i, "STOCKIN_SUM_QTY") > 0) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 传值给TABLE_D
	 * 
	 * @param parm
	 *            TParm
	 * @param row
	 *            int
	 */
	public void setTableDValue(TParm parm, int row) {
		table_d.setRowParmValue(row, parm);
		table_d.getParmValue().setRowData(row, parm);
		table_d.setItem(row, "SUP_CODE", this.getValue("SUP_CODE"));
	}

	/**
	 * 新增细项数据行
	 * 
	 * @return int
	 */
	private int createNewRow() {
		int row = table_d.addRow();
		return row;
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
	 * 得到RadioButton对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
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
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

}
