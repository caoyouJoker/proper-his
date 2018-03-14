package com.javahis.ui.ind;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import jdo.odo.ODO;
import jdo.odo.OpdRxSheetTool;
import jdo.pha.PhaBaseTool;
import jdo.sys.Operator;
import jdo.sys.SYSFeeTool;
import jdo.sys.SysFee;

import com.dongyang.ui.TTable;

import jdo.ind.INDSQL;
import jdo.ind.IndRequestMTool;

import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.sql.Timestamp;

import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TMenuItem;
import com.dongyang.util.TypeTool;

import jdo.sys.SystemTool;

import com.dongyang.data.TNull;

import jdo.util.Manager;
import jdo.ind.INDTool;

import com.dongyang.ui.TTableNode;
import com.javahis.ui.sys.SYSFee_FeeControl;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: 科室备药生成Control
 * </p>
 * 
 * <p>
 * Description: 科室备药生成Control
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
 * @author zhangy 2009.05.12
 * @version 1.0
 */
public class INDDeptRequestOfUddControl extends TControl {

	// 主项表格
	private TTable table_m;

	// 细项表格
	private TTable table_d;

	// 申请单号
	private String request_no;

	// 全部部门权限
	private boolean dept_flg = true;

	// 门急住类别
	private String type;
	private ODO odo;
	
	//==== add by wukai on 20170117 start 
	private String appOrdCode;
	private String toOrgCode;
	private String startDate;
	private String endDate;
	//==== add by wukai on 20170117 end 

	public INDDeptRequestOfUddControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 添加侦听事件
		addEventListener("TABLE_M->" + TTableEvent.CHANGE_VALUE,
				"onTableMChangeValue");
		// 给TABLE_M中的CHECKBOX添加侦听事件
		callFunction("UI|TABLE_M|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableMCheckBoxClicked");
		// 给TABLE_D中的CHECKBOX添加侦听事件
		callFunction("UI|TABLE_D|addEventListener",
				TTableEvent.CHECK_BOX_CLICKED, this, "onTableDCheckBoxClicked");

		// 初始画面数据
		initPage();
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		if (!CheckDataM()) {
			return;
		}
		TParm parm = new TParm();
		parm.setData("APP_ORG_CODE", getValueString("APP_ORG_CODE"));
		parm.setData("TO_ORG_CODE", getValueString("TO_ORG_CODE"));
		parm.setData("START_DATE", formatString(this
				.getValueString("START_DATE")));
		parm.setData("END_DATE", formatString(this.getValueString("END_DATE")));
		//======= add by wukai 添加查询条件 用于 保存
		this.appOrdCode = getValueString("APP_ORG_CODE");
		this.toOrgCode = getValueString("TO_ORG_CODE");
		this.startDate = formatString(this.getValueString("START_DATE"));
		this.endDate = formatString(this.getValueString("END_DATE"));
		// =======pangben modify 20110511 start 添加区域参数
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			parm.setData("REGION_CODE", Operator.getRegion());
		}
		// =======pangben modify 20110511 stop
		// 已申请
		if (this.getRadioButton("REQUEST_FLG_A").isSelected()) {
			if (this.getRadioButton("REQUEST_TYPE_B").isSelected()) {// 如果是明细，请先填
				// 请领单
				if (null == getValueString("REQUEST_NO")
						|| "".equals(getValueString("REQUEST_NO"))) {
					this.messageBox("申请单号不能为空");
					return;
				}
			}
			if (null != getValueString("REQUEST_NO")
					&& !"".equals(getValueString("REQUEST_NO"))) {
				parm.setData("REQUEST_NO", getValueString("REQUEST_NO"));
			}
			parm.setData("REQUEST_FLG_A", "Y");
		} else {
			parm.setData("REQUEST_FLG_A", "N");
		}

		// lirui 2012-6-4 start 对药品分类，管制毒麻药品
		// 返回结果集
		TParm result = new TParm();

		// 药品管制
		if (getRadioButton("Normal").isSelected()) {
			// 普通药品
			parm.setData("CTRLDRUGCLASS_CODE_A", "A");

		}
		if (getRadioButton("drug").isSelected()) {
			// 毒麻药
			parm.setData("CTRLDRUGCLASS_CODE_B", "B");
		}
		// lirui 2012-6-4 end 对药品分类，管制毒麻药品
		// 全部药品
		result = INDTool.getInstance().onQueryDeptFromOdiDspnm(parm);
		TParm parmM = result.getParm("RESULT_M", 0);
		TParm parmD = result.getParm("RESULT_D", 0);
//		 System.out.println("parmM===" + parmM);
//		 System.out.println("parmD===" + parmD);
		// if (parmM.getCount() == 0 || parmD.getCount("STOCK_PRICE") <= 0) {
		// this.messageBox("无查询数据");
		// return;
		// }
		table_m.setParmValue(parmM);
		table_d.setParmValue(parmD);
		// 默认计算总金额 by liyh 20120910
		setSumRetailMoneyOnQuery(parmM);
	}

	/**
	 * 生成请领单
	 */
	public void onSave() {
		if (!CheckDataM()) {
			return;
		}
		if (!CheckDataD()) {
			return;
		}
		TParm parm = new TParm();
		if (getRadioButton("Normal").isSelected()) {
			parm.setData("DRUG_CATEGORY", "1");
		} else if (getRadioButton("drug").isSelected()) {
			parm.setData("DRUG_CATEGORY", "2");
		}

		// 整理数据，申请单主项
		getRequestExmParmM(parm);
		// System.out.println("parm--1-" + parm);
		// 整理数据，申请单细项
		getRequestExmParmD(parm);
		// 判断更新类别(门急住)
		parm.setData("TYPE", type);
		// 整理数据，更新申请状态
		getDeptRequestUpdate(parm);
		// <---- 写入DRUG_CATEGORY值 identify by shendr 2013.7.17

		// fuxin modify 20150603
		// 非移货建议和自动拨补

		// ------->
		TParm result = new TParm();
		// System.out.println("parm--3-" + parm);

		// 调用物联网接口方法，由以前的onCreateDeptExmRequest改为onCreateDeptExmRequestSpc
		// result = TIOM_AppServer.executeAction("action.ind.INDRequestAction",
		// "onCreateDeptOdiRequestSpc", parm);
		// fux modify 20150602
		result = TIOM_AppServer.executeAction("action.ind.INDRequestAction",
				"onCreateDeptExmRequest", parm);

		String msg = "";
		// 保存判断
		if (result == null || result.getErrCode() < 0) {
			// this.messageBox(result.getErrText());
			String errText = result.getErrText();
			String[] errCode = errText.split(";");
			for (int i = 0; i < errCode.length; i++) {
				String orderCode = errCode[i];
				TParm returnParm = SYSFeeTool.getInstance().getFeeAllData(
						orderCode);
				if (returnParm != null && returnParm.getCount() > 0) {
					returnParm = returnParm.getRow(0);
					msg += orderCode + " " + returnParm.getValue("ORDER_DESC")
							+ "  " + returnParm.getValue("SPECIFICATION")
							+ "\n";
					if (i == errCode.length - 1) {
						msg += "不存在物联网药品对照编码";
					}
				} else {
					msg += orderCode + "\n";
				}
			}
			this.messageBox(msg);
			return;
		}
		this.messageBox("P0001");
		onClear();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		((TMenuItem) getComponent("save")).setEnabled(true);
		((TMenuItem) getComponent("printM")).setEnabled(true);
		((TMenuItem) getComponent("printD")).setEnabled(false);
		((TMenuItem) getComponent("printRecipe")).setEnabled(false);
		table_m.setVisible(true);
		table_m.removeRowAll();
		table_d.setVisible(false);
		table_d.removeRowAll();
		// 清空画面内容
		String clearString = "APP_ORG_CODE;REQUEST_NO;TO_ORG_CODE;REASON_CHN_DESC;DESCRIPTION;"
				+ "SELECT_ALL;URGENT_FLG;CHECK_FLG;SUM_RETAIL_PRICE;SUM_VERIFYIN_PRICE;"
				+ "PRICE_DIFFERENCE";
		clearValue(clearString);
		getRadioButton("REQUEST_FLG_B").setSelected(true);
		getRadioButton("REQUEST_TYPE_A").setSelected(true);
	}

	/**
	 * 过滤申请单号
	 */
	public boolean request() {

		// String REQUEST_NO = (String) table_m.getItemData(0,"REQUEST_NO");
		// TParm num=new
		// TParm(TJDODBTool.getInstance().select(INDSQL.checkData(REQUEST_NO)));
		// int number = num.getCount();
		// this.messageBox("wocao--"+number);
		// if(number>1)
		// {
		// this.messageBox("申请单太多！");
		//        	
		// }

		Set<String> set = new HashSet<String>();
		for (int i = 0; i < table_m.getRowCount(); i++) {
			set.add((String) table_m.getItemData(i, "REQUEST_NO"));
		}
		int number = set.size();
		if (number > 1) {
			this.messageBox("一次只能打印一个申请单的内容！");
			return false;

		}
		return true;
	}

	/**
	 * 打印汇总单
	 */
	public void onPrintM() {

		boolean flg = true;
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("没有汇总信息");
			return;
		}
		boolean no = request();
		if (no == true) {
			Timestamp datetime = StringTool.getTimestamp(new Date());

			// 打印数据
			TParm date = new TParm();
			// 表头数据
			date.setData("TITLE", "TEXT", Manager.getOrganization()
					.getHospitalCHNFullName(Operator.getRegion())
					+ "科室备药单");
			date.setData("DATE_AREA", "TEXT", "申请单号:"
					+ table_m.getItemData(0, "REQUEST_NO"));
			date.setData("ORG_CODE_IN", "TEXT", "申请部门: "
					+ this.getComboBox("APP_ORG_CODE").getSelectedName());
			date.setData("ORG_CODE_OUT", "TEXT", "接受部门: "
					+ this.getComboBox("TO_ORG_CODE").getSelectedName());
			date.setData("DATE", "TEXT", "制表时间: "
					+ datetime.toString().substring(0, 10));
			// 表格数据
			String order_code = "";
			String unit_type = "1";
			String order_desc = "";
			TParm parm = new TParm();
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				order_code = table_m.getParmValue().getValue("ORDER_CODE", i);
				TParm inparm = new TParm(TJDODBTool.getInstance().select(
						INDSQL.getOrderInfoByCode(order_code, unit_type)));
				if (inparm == null || inparm.getErrCode() < 0) {
					this.messageBox("药品信息有误");
					return;
				}
				if ("".equals(inparm.getValue("GOODS_DESC", 0))) {
					order_desc = inparm.getValue("ORDER_DESC", 0);
				} else {
					order_desc = inparm.getValue("ORDER_DESC", 0) + "("
							+ inparm.getValue("GOODS_DESC", 0) + ")";
				}
				parm.addData("ORDER_DESC", order_desc);

				parm.addData("SPECIFICATION", table_m.getItemData(i,
						"SPECIFICATION"));
				parm.addData("UNIT", table_m.getItemData(i, "UNIT_CHN_DESC"));
				parm.addData("QTY", table_m.getItemDouble(i, "DOSAGE_QTY"));
				parm.addData("STOCK_PRICE", table_m.getItemDouble(i,
						"STOCK_PRICE"));
				parm
						.addData("STOCK_AMT", table_m.getItemDouble(i,
								"STOCK_AMT"));
				parm
						.addData("OWN_PRICE", table_m.getItemDouble(i,
								"OWN_PRICE"));
				parm.addData("OWN_AMT", table_m.getItemDouble(i, "OWN_AMT"));
			}
			parm.setCount(parm.getCount("ORDER_DESC"));
			parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
			parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
			parm.addData("SYSTEM", "COLUMNS", "UNIT");
			parm.addData("SYSTEM", "COLUMNS", "QTY");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
			parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
			parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
			date.setData("TABLE", parm.getData());
			// 表尾数据
			date.setData("STOCK_AMT", "TEXT", "采购总金额: "
					+ StringTool.round(Double.parseDouble(this
							.getValueString("SUM_VERIFYIN_PRICE")), 4));

			date.setData("OWN_AMT", "TEXT", "零售总金额: "
					+ StringTool.round(Double.parseDouble(this
							.getValueString("SUM_RETAIL_PRICE")), 4));

			// date.setData("DIFF_AMT", "TEXT", "进销差价: " +
			// StringTool.round(Double.parseDouble(this.
			// getValueString("PRICE_DIFFERENCE")), 4));
			//		          
			date.setData("USER", "TEXT", "制表人: " + Operator.getName());
			// 调用打印方法
			this.openPrintWindow("%ROOT%\\config\\prt\\IND\\DeptRequestM.jhw",
					date);
		}

	}

	/**
	 * 打印明细单
	 */
	public void onPrintD() {
		boolean flg = true;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!"N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("没有明细信息");
			return;
		}
		Timestamp datetime = StringTool.getTimestamp(new Date());
		// 打印数据
		TParm date = new TParm();
		// 表头数据
		date.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion())
				+ "病区备药单（明细）");
		date.setData("DATE_AREA", "TEXT", this.getValueString("REQUEST_NO"));
		date.setData("APP_ORG_NAME", "TEXT", this.getComboBox("APP_ORG_CODE")
				.getSelectedName());
		date.setData("TO_ORG_NAME", "TEXT", this.getComboBox("TO_ORG_CODE")
				.getSelectedName());
		

		// 表格数据
		TParm parm = new TParm();
		Timestamp time = null;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			parm.addData("PAT_NAME", table_d.getItemData(i, "PAT_NAME"));
			time = table_d.getItemTimestamp(i, "BILL_DATE");
			if(time != null) {
				parm.addData("BILL_DATE", time.toString().replaceAll("-", "/").substring(0, 10));
			} else {
				parm.addData("BILL_DATE", "");
			}
			parm.addData("ORDER_DESC", table_d.getItemData(i, "ORDER_DESC"));
			parm.addData("SPECIFICATION", table_d.getItemData(i,
					"SPECIFICATION"));
			parm.addData("DOSAGE_QTY", table_d.getItemData(i, "DOSAGE_QTY"));
			parm.addData("UNIT_CHN_DESC", table_d.getItemData(i,
					"UNIT_CHN_DESC"));
			parm
					.addData("STOCK_PRICE", table_d.getItemDouble(i,
							"STOCK_PRICE"));
			parm.addData("STOCK_AMT", table_d.getItemDouble(i, "STOCK_AMT"));
			parm.addData("OWN_PRICE", table_d.getItemDouble(i, "OWN_PRICE"));
			parm.addData("OWN_AMT", table_d.getItemDouble(i, "OWN_AMT"));
		}
		parm.setCount(parm.getCount("PAT_NAME"));
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "BILL_DATE");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "DOSAGE_QTY");
		parm.addData("SYSTEM", "COLUMNS", "UNIT_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "STOCK_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "STOCK_AMT");
		parm.addData("SYSTEM", "COLUMNS", "OWN_PRICE");
		parm.addData("SYSTEM", "COLUMNS", "OWN_AMT");
		date.setData("TABLED", parm.getData());
		
		//表尾数据
		date.setData("OPT_DATE", "TEXT", datetime.toString().substring(0, 10));
		date.setData("OPT_USER", "TEXT", Operator.getName());
		date.setData("SUM_VERIFYIN_PRICE", "TEXT","销售总金额："+StringTool.round(Double.parseDouble(this
						.getValueString("SUM_VERIFYIN_PRICE")), 4));

		date.setData("SUM_RETAIL_PRICE", "TEXT","零售总金额："+StringTool.round(Double.parseDouble(this
						.getValueString("SUM_RETAIL_PRICE")), 4));
		this.openPrintWindow("%ROOT%\\config\\prt\\IND\\INDDeptRequestOfUdd_D.jhw",
				date);
	}
	
	public void onExportD() {
		boolean flg = true;
		for (int i = 0; i < table_d.getRowCount(); i++) {
			if (!"N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("没有明细信息");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table_d,"病区备药单（明细）");
		
	}

	/**
	 * 变更申请部门
	 */
	public void onChangeAppOrg() {
		if (!"".equals(this.getValueString("APP_ORG_CODE"))) {
			// 预设归属库房
			TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"),
							Operator.getRegion())));
			getComboBox("TO_ORG_CODE").setSelectedID(
					sup_org_code.getValue("SUP_ORG_CODE", 0));
		}
	}

	/**
	 * 变更统计状态
	 */
	public void onChangeRequestFlg() {
		if (this.getRadioButton("REQUEST_FLG_B").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				((TMenuItem) getComponent("printM")).setEnabled(true);
				((TMenuItem) getComponent("printD")).setEnabled(false);
				((TMenuItem) getComponent("printRecipe")).setEnabled(false);
				((TMenuItem) getComponent("export")).setEnabled(false);
				table_m.setVisible(true);
				table_d.setVisible(false);
			} else {
				((TMenuItem) getComponent("printM")).setEnabled(false);
				((TMenuItem) getComponent("printD")).setEnabled(true);
				((TMenuItem) getComponent("printRecipe")).setEnabled(true);
				((TMenuItem) getComponent("export")).setEnabled(true);
				table_m.setVisible(false);
				table_d.setVisible(true);
			}
		} else {
			((TMenuItem) getComponent("save")).setEnabled(false);
			if (this.getRadioButton("REQUEST_TYPE_A").isSelected()) {
				((TMenuItem) getComponent("printM")).setEnabled(true);
				((TMenuItem) getComponent("printD")).setEnabled(false);
				((TMenuItem) getComponent("printRecipe")).setEnabled(false);
				((TMenuItem) getComponent("export")).setEnabled(false);
				table_m.setVisible(true);
				table_d.setVisible(false);
			} else {
				((TMenuItem) getComponent("printM")).setEnabled(false);
				((TMenuItem) getComponent("printD")).setEnabled(true);
				((TMenuItem) getComponent("printRecipe")).setEnabled(true);
				((TMenuItem) getComponent("export")).setEnabled(true);
				table_m.setVisible(false);
				table_d.setVisible(true);
			}
		}
		onQuery();
	}

	/**
	 * 全选
	 */
	public void onSelectAll() {
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		} else {
			flg = "N";
		}
		for (int i = 0; i < table_m.getRowCount(); i++) {
			table_m.setItem(i, "SELECT_FLG", flg);
		}
		for (int i = 0; i < table_d.getRowCount(); i++) {
			table_d.setItem(i, "SELECT_FLG", flg);
		}
		setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
		setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
		setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
				- getSumRegMoney(), 4));
	}

	/**
	 * 表格(TABLE)复选框改变事件
	 * 
	 * @param obj
	 */
	public void onTableMCheckBoxClicked(Object obj) {
		table_m.acceptText();
		// this.messageBox("2222222222");
		// 获得选中的列
		int column = table_m.getSelectedColumn();
		if (column == 0) {
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
		}
	}

	/**
	 * 表格值改变事件
	 * 
	 * @param obj
	 *            Object
	 */
	public boolean onTableMChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null)
			return false;
		// 判断数据改变
		if (node.getValue().equals(node.getOldValue()))
			return true;
		int column = node.getColumn();
		int row = node.getRow();

		String flg = "N";
		String flgString = table_d.getItemString(row, "SELECT_FLG");
		if ("N".equals(flgString)) {
			flg = "Y";
		} else {
			flg = "N";
		}
		table_d.setItem(row, "SELECT_FLG", flg);
		if (column == 0) {
			return false;
		}
		if (column == 4) {
			double qty = TypeTool.getDouble(node.getValue());
			if (qty <= 0) {
				this.messageBox("申请数量不能小于或等于0");
				return true;
			}
			double amt1 = StringTool.round(qty
					* table_m.getItemDouble(row, "STOCK_PRICE"), 2);
			double amt2 = StringTool.round(qty
					* table_m.getItemDouble(row, "OWN_PRICE"), 2);
			table_m.setItem(row, "STOCK_AMT", amt1);
			table_m.setItem(row, "OWN_AMT", amt2);
			table_m.setItem(row, "DIFF_AMT", amt2 - amt1);
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
			return false;
		}
		return true;
	}

	/**
	 * 表格(TABLE)复选框改变事件
	 * 
	 * @param obj
	 */
	public void onTableDCheckBoxClicked(Object obj) {
		table_d.acceptText();
		// 获得选中的列
		int column = table_d.getSelectedColumn();
		if (column == 0) {
			setValue("SUM_RETAIL_PRICE", getSumRetailMoney());
			setValue("SUM_VERIFYIN_PRICE", getSumRegMoney());
			setValue("PRICE_DIFFERENCE", StringTool.round(getSumRetailMoney()
					- getSumRegMoney(), 4));
		}
	}

	public void onPrintRecipe() {
		// this.messageBox(Operator.getDept());
		int row = this.table_d.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选择打印数据！");
			return;
		}
		String caseNo = this.table_d.getParmValue().getValue("CASE_NO", row);
		String mrNo = this.table_d.getParmValue().getValue("MR_NO", row);
		String rxNo = this.table_d.getParmValue().getValue("RX_NO", row);
		String exeDeptCode = this.table_d.getParmValue().getValue(
				"EXEC_DEPT_CODE", row);
		// case_no mr_no
		odo = new ODO(caseNo, mrNo, Operator.getDept(), Operator.getID(), "O");
		// TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
		// realDeptCode, rxType, odo, rxNo,
		// order.getItemString(0, "PSY_FLG"));
		// 030101
		// System.out.println(caseNo+"====="+exeDeptCode+"===="+rxNo);
		TParm inParam = OpdRxSheetTool.getInstance().getOrderPrintParm(
				exeDeptCode, "1", odo, rxNo, "Y");
		// luhai add 处方签sql begin
		String rxNo2 = inParam.getValue("RX_NO");
		String caseNo2 = caseNo;
		// **********************************************************
		// luhai modify 2012-05-09 begin 非药品的项目不打印处方签 begin
		// **********************************************************
		String westsql = "  SELECT   CASE WHEN   OPD_ORDER.BILL_FLG='Y' THEN '√' ELSE '' END||'  '||OPD_ORDER.LINK_NO aa , "
				+ " CASE WHEN SYS_FEE.IS_REMARK = 'Y' THEN OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.ORDER_DESC  END bb , "
				+ " OPD_ORDER.SPECIFICATION cc, "
				+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '皮试' ELSE SYS_PHAROUTE.ROUTE_CHN_DESC  END dd,"
				+ " CASE WHEN OPD_ORDER.ROUTE_CODE='PS' THEN '' ELSE RTRIM(RTRIM(TO_CHAR(OPD_ORDER.MEDI_QTY,'fm9999999990.000'),'0'),'.')||''||A.UNIT_CHN_DESC  END ee,"
				+ " RPAD(SYS_PHAFREQ.FREQ_CHN_DESC, (16-LENGTH(SYS_PHAFREQ.FREQ_CHN_DESC)), ' ')|| OPD_ORDER.TAKE_DAYS FF,"
				+ " CASE WHEN OPD_ORDER.DISPENSE_QTY<1 THEN TO_CHAR(OPD_ORDER.DISPENSE_QTY,'fm9999999990.0') ELSE "
				+ " TO_CHAR(OPD_ORDER.DISPENSE_QTY) END||''|| B.UNIT_CHN_DESC er,"
				+ " CASE WHEN OPD_ORDER.RELEASE_FLG = 'Y' THEN '自备  '|| OPD_ORDER.DR_NOTE ELSE  OPD_ORDER.DR_NOTE END gg ,OPD_ORDER.DOSAGE_QTY,OPD_ORDER.OWN_PRICE "
				+ " FROM   OPD_ORDER, SYS_PHAFREQ, SYS_PHAROUTE,SYS_UNIT A, SYS_UNIT B,SYS_FEE "
				+ " WHERE       CASE_NO = '"
				+ caseNo2
				+ "'"
				+ "  AND RX_NO = '"
				+ rxNo2
				+ "'"
				+ " and SYS_PHAROUTE.ROUTE_CODE(+) = OPD_ORDER.ROUTE_CODE "
				+ "  AND SYS_PHAFREQ.FREQ_CODE(+) = OPD_ORDER.FREQ_CODE "
				+ "  AND A.UNIT_CODE(+) =  OPD_ORDER.MEDI_UNIT "
				+ "  AND B.UNIT_CODE(+) =  OPD_ORDER.DISPENSE_UNIT "
				+ "  AND OPD_ORDER.ORDER_CODE = SYS_FEE.ORDER_CODE "
				+ "  AND OPD_ORDER.CAT1_TYPE='PHA' "
				+ " ORDER BY   LINK_NO, LINKMAIN_FLG DESC, SEQ_NO";
		// **********************************************************
		// luhai modify 2012-05-09 begin 非药品的项目不打印处方签 end
		// **********************************************************
		TParm westResult = new TParm(TJDODBTool.getInstance().select(westsql));
		if (westResult.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		if (westResult.getCount() < 0) {
			this.messageBox("没有处方签数据.");
			return;
		}

		TParm westParm = new TParm();
		double pageAmt2 = 0;
		DecimalFormat df2 = new DecimalFormat("############0.00");
		for (int i = 0; i < westResult.getCount(); i++) {
			westParm.addData("AA", westResult.getData("AA", i));
			westParm.addData("BB", westResult.getData("BB", i));
			westParm.addData("CC", westResult.getData("CC", i));
			westParm.addData("DD", westResult.getData("DD", i));
			westParm.addData("EE", westResult.getData("EE", i));
			westParm.addData("FF", westResult.getData("FF", i));
			westParm.addData("ER", westResult.getData("ER", i));
			westParm.addData("GG", westResult.getData("GG", i));
			pageAmt2 += (westResult.getDouble("DOSAGE_QTY", i) * westResult
					.getDouble("OWN_PRICE", i));
			if ((i != 0 && (i + 1) % 5 == 0) || i == westResult.getCount() - 1) {
				westParm.addData("AA", "");
				westParm.addData("BB", "");
				westParm.addData("CC", "");
				westParm.addData("DD", "");
				westParm.addData("EE", "");
				westParm.addData("FF", "处方金额(￥):");
				westParm.addData("ER", df2.format(pageAmt2));
				westParm.addData("GG", "");
				pageAmt2 = 0;
			}
		}
		westParm.setCount(westParm.getCount("AA"));
		westParm.addData("SYSTEM", "COLUMNS", "AA");
		westParm.addData("SYSTEM", "COLUMNS", "BB");
		westParm.addData("SYSTEM", "COLUMNS", "CC");
		westParm.addData("SYSTEM", "COLUMNS", "DD");
		westParm.addData("SYSTEM", "COLUMNS", "EE");
		westParm.addData("SYSTEM", "COLUMNS", "FF");
		westParm.addData("SYSTEM", "COLUMNS", "ER");
		westParm.addData("SYSTEM", "COLUMNS", "GG");

		inParam.setData("ORDER_TABLE", westParm.getData());
		// luhai add 处方签sql end
		Object obj = this.openPrintDialog(
				"%ROOT%\\config\\prt\\OPD\\OpdOrderSheet.jhw", inParam, false);
	}

	/**
	 * 主项表格(TABLE_M)单击事件
	 */
	public void onTableMClicked() {
		int row_m = table_m.getSelectedRow();
		if (row_m != -1) {
			// 主项表格选中行数据上翻
			setValue("REQUEST_NO", table_m.getItemData(row_m, "REQUEST_NO"));
		}
	}

	/**
	 * 主项表格(TABLE_D)单击事件
	 */
	public void onTableDClicked() {

	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		/**
		 * 权限控制 权限1:只显示自已所属科室 权限9:最大权限,显示全院药库部门
		 */
		// 判断是否显示全院药库部门
		if (!this.getPopedem("deptAll")) {
			TParm parm = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getIndOrgByUserId(Operator.getID(), Operator
							.getRegion(),
							"AND B.ORG_TYPE = 'C' AND B.EXINV_FLG = 'Y' ")));
			getComboBox("APP_ORG_CODE").setParmValue(parm);
			dept_flg = false;
			if (parm.getCount("NAME") > 0) {
				getComboBox("APP_ORG_CODE").setSelectedIndex(1);
			}
			// 预设归属库房getINDORG
			TParm sup_org_code = new TParm(TJDODBTool.getInstance().select(
					INDSQL.getINDORG(this.getValueString("APP_ORG_CODE"),
							Operator.getRegion())));
			getComboBox("TO_ORG_CODE").setSelectedID(
					sup_org_code.getValue("SUP_ORG_CODE", 0));
		}
		Timestamp date = StringTool.getTimestamp(new Date());
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		// 初始化TABLE
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
		// ( (TMenuItem) getComponent("save")).setEnabled(false);
		((TMenuItem) getComponent("printM")).setEnabled(true);
		((TMenuItem) getComponent("printD")).setEnabled(false);
		((TMenuItem) getComponent("printRecipe")).setEnabled(false);
		((TMenuItem) getComponent("export")).setEnabled(false);
		//add by wukai on 20170117 病区备药 type
		this.type = "OD";
	}

	/**
	 * 数据检验
	 * 
	 * @return
	 */
	private boolean CheckDataM() {
		if ("".equals(getValueString("APP_ORG_CODE"))) {
			this.messageBox("申请部门不能为空");
			return false;
		}
		if ("Y".equals(this.getValue("REQUEST_FLG_A"))) {
			if ("".equals(getValueString("TO_ORG_CODE"))) {
				this.messageBox("接收部门不能为空");
				return false;
			}
		}
		return true;
	}

	/**
	 * 数据检验
	 * 
	 * @return boolean
	 */
	private boolean CheckDataD() {
		if ("".equals(getValueString("TO_ORG_CODE"))) {
			this.messageBox("接受部门不能为空");
			return false;
		}
		if (table_d.getRowCount() == 0) {
			this.messageBox("没有申请数据");
			return false;
		}
		boolean flg = true;
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if ("Y".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				flg = false;
			}
		}
		if (flg) {
			this.messageBox("没有申请数据");
			return false;
		}
		return true;
	}

	/**
	 * 整理数据，申请单主项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmM(TParm parm) {
		TParm inparm = new TParm();
		Timestamp date = StringTool.getTimestamp(new Date());
		request_no = SystemTool.getInstance().getNo("ALL", "IND",
				"IND_REQUEST", "No");
		inparm.setData("REQUEST_NO", request_no);
		inparm.setData("REQTYPE_CODE", "TEC");
		inparm.setData("APP_ORG_CODE", this.getValueString("APP_ORG_CODE"));
		inparm.setData("TO_ORG_CODE", this.getValueString("TO_ORG_CODE"));
		inparm.setData("REQUEST_DATE", date);
		inparm.setData("REQUEST_USER", Operator.getID());
		inparm.setData("REASON_CHN_DESC", this
				.getValueString("REASON_CHN_DESC"));
		inparm.setData("DESCRIPTION", this.getValueString("DESCRIPTION"));
		inparm.setData("UNIT_TYPE", "1");
		inparm.setData("URGENT_FLG", "N");
		inparm.setData("OPT_USER", Operator.getID());
		inparm.setData("OPT_DATE", date);
		inparm.setData("OPT_TERM", Operator.getIP());
		// zhangyong20110517
		inparm.setData("REGION_CODE", Operator.getRegion());
		inparm.setData("APPLY_TYPE", "1");
		if (getRadioButton("Normal").isSelected()) {
			inparm.setData("DRUG_CATEGORY", "1");
		} else if (getRadioButton("drug").isSelected()) {
			inparm.setData("DRUG_CATEGORY", "2");
		}
		parm.setData("REQUEST_M", inparm.getData());
		return parm;
	}

	/**
	 * 整理数据，申请单细项
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getRequestExmParmD(TParm parm) {
		TParm inparm = new TParm();
		TNull tnull = new TNull(Timestamp.class);
		Timestamp date = SystemTool.getInstance().getDate();
		String user_id = Operator.getID();
		String user_ip = Operator.getIP();
		int count = 0;
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			inparm.addData("REQUEST_NO", request_no);
			inparm.addData("SEQ_NO", count + 1);
			inparm.addData("ORDER_CODE", table_m.getParmValue().getValue(
					"ORDER_CODE", i));
			inparm.addData("BATCH_NO", "");
			inparm.addData("VALID_DATE", tnull);
			inparm.addData("QTY", table_m.getItemDouble(i, "DOSAGE_QTY"));
			inparm.addData("ACTUAL_QTY", 0);
			inparm.addData("UPDATE_FLG", "0");
			inparm.addData("OPT_USER", user_id);
			inparm.addData("OPT_DATE", date);
			inparm.addData("OPT_TERM", user_ip);
			inparm.addData("STATION_CODE", getValueString("APP_ORG_CODE"));
			inparm.addData("START_DATE", formatString(this
					.getValueString("START_DATE")));
			inparm.addData("END_DATE", formatString(this
					.getValueString("END_DATE")));
			// fux modify 20150603
			// VERIFYIN_PRICE
			inparm.addData("VERIFYIN_PRICE", table_m.getParmValue().getDouble(
					"STOCK_PRICE", i));
			// BATCH_SEQ
			inparm.addData("BATCH_SEQ", 0);
			// IS_UPDATE
			inparm.addData("IS_UPDATE", "");
			count++;
			// System.out.println(count+"----------inparm:"+inparm);
		}
		inparm.setCount(count);
		parm.setData("REQUEST_D", inparm.getData());
		return parm;
	}

	/**
	 * 整理数据，更新申请状态
	 * 
	 * @param parm
	 *            TParm
	 * @return TParm
	 */
	private TParm getDeptRequestUpdate(TParm parm) {
		TParm inparm = new TParm();
		//alert by wukai on 20160117 根据主表选择数据 重新获取需要更新的数据
		TParm tableMParm = table_m.getParmValue();
		TParm queryParm = new TParm();
		TParm resultD = new TParm();
		for (int i = 0; i < table_m.getRowCount(); i++) {
			if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
				continue;
			}
			queryParm.setData("APP_ORG_CODE", this.appOrdCode);
			queryParm.setData("TO_ORG_CODE", this.toOrgCode);
			queryParm.setData("START_DATE", this.startDate);
			queryParm.setData("END_DATE", this.endDate);
			
			queryParm.setData("ORDER_CODE", tableMParm.getData("ORDER_CODE", i));
			queryParm.setData("ORDER_DESC", tableMParm.getData("ORDER_DESC", i));
			queryParm.setData("SPECIFICATION", tableMParm.getData("SPECIFICATION", i));
			queryParm.setData("UNIT_CHN_DESC", tableMParm.getData("UNIT_CHN_DESC", i));
			queryParm.setData("OWN_PRICE", tableMParm.getData("OWN_PRICE", i));
			queryParm.setData("STOCK_PRICE", tableMParm.getData("STOCK_PRICE", i));
			resultD = IndRequestMTool.getInstance().queryOdiDspnmExmD(queryParm);
			if(resultD.getErrCode() < 0) {
				continue;
			}
			for(int j = 0; j < resultD.getCount("SELECT_FLG") ; j++) {
				inparm.addData("APP_ORG_CODE", this.appOrdCode);
				inparm.addData("CASE_NO",  resultD.getData("CASE_NO", j));
				inparm.addData("ORDER_NO",  resultD.getData("CASE_NO_SEQ", j));
				inparm.addData("ORDER_SEQ",  resultD.getData("SEQ_NO", j));
				inparm.addData("DOSAGE_QTY", resultD.getData("DOSAGE_QTY", j));
				
				inparm.addData("REQUEST_NO", this.request_no);
			}
			
		}
		parm.setData("UPDATE", inparm.getData());
		//alert by wukai on 20160117 根据主表选择数据 重新获取需要更新的数据
		return parm;
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
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
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
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * 格式化字符串(时间格式)
	 * 
	 * @param arg
	 *            String
	 * @return String YYYYMMDDHHMMSS
	 */
	private String formatString(String arg) {
		arg = arg.substring(0, 4) + arg.substring(5, 7) + arg.substring(8, 10)
				+ arg.substring(11, 13) + arg.substring(14, 16)
				+ arg.substring(17, 19);
		return arg;
	}

	/**
	 * 计算零售总金额
	 * 
	 * @return
	 */
	private double getSumRetailMoney() {
		table_m.acceptText();
		table_d.acceptText();
		double sum = 0;
		if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_m.getItemDouble(i, "OWN_AMT");
			}
		} else {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_d.getItemDouble(i, "OWN_AMT");
			}
		}
		return StringTool.round(sum, 4);
	}

	/**
	 * 计算采购/零售总金额
	 * 
	 * @return
	 * @author liyh
	 * @date 20120910
	 */
	private void setSumRetailMoneyOnQuery(TParm parmM) {
		// 销售总金额
		double sum_retail = 0.0;
		// 采购总金额
		double sum_verifyin = 0.0;
		int count = parmM.getCount();
		if (null != parmM && count > 0) {
			for (int i = 0; i < count; i++) {
				sum_retail += parmM.getDouble("OWN_AMT", i);
				sum_verifyin += parmM.getDouble("STOCK_AMT", i);
			}

		}
		setValue("SUM_RETAIL_PRICE", sum_retail);
		setValue("SUM_VERIFYIN_PRICE", sum_verifyin);
	}

	/**
	 * 计算成本总金额
	 * 
	 * @return
	 */
	private double getSumRegMoney() {
		table_m.acceptText();
		table_d.acceptText();
		double sum = 0;
		if (getRadioButton("REQUEST_TYPE_A").isSelected()) {
			for (int i = 0; i < table_m.getRowCount(); i++) {
				if ("N".equals(table_m.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_m.getItemDouble(i, "STOCK_AMT");
			}
		} else {
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if ("N".equals(table_d.getItemString(i, "SELECT_FLG"))) {
					continue;
				}
				sum += table_d.getItemDouble(i, "STOCK_AMT");
			}
		}
		return StringTool.round(sum, 4);
	}

	/**
	 * 取得SYS_FEE信息，放置在状态栏上
	 * 
	 * @param order_code
	 *            String
	 */
	private void setSysStatus(String order_code) {
		TParm order = INDTool.getInstance().getSysFeeOrder(order_code);
		String status_desc = "药品代码:" + order.getValue("ORDER_CODE") + " 药品名称:"
				+ order.getValue("ORDER_DESC") + " 商品名:"
				+ order.getValue("GOODS_DESC") + " 规格:"
				+ order.getValue("SPECIFICATION");
		callFunction("UI|setSysStatus", status_desc);
	}
}
