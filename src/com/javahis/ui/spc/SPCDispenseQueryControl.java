package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCDispenseQueryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: 药房销售统计
 * </p>
 * 
 * <p>
 * Description: 药房销售统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * f Company: Javahis
 * </p>
 * 
 * @author shendr
 * @version 1.0
 */
public class SPCDispenseQueryControl extends TControl {

	// 表格对象
	public TTable table;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		table = this.getTable("TABLE");
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("ORG_CODE", Operator.getDept());
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		// 查询参数
		String start_date = this.getValueString("START_DATE");
		start_date = start_date.substring(0, 4) + start_date.substring(5, 7)
				+ start_date.substring(8, 10) + start_date.substring(11, 13)
				+ start_date.substring(14, 16) + start_date.substring(17, 19);
		String end_date = this.getValueString("END_DATE");
		end_date = end_date.substring(0, 4) + end_date.substring(5, 7)
				+ end_date.substring(8, 10) + end_date.substring(11, 13)
				+ end_date.substring(14, 16) + end_date.substring(17, 19);
		String org_code = this.getValueString("ORG_CODE");
		String order_code = this.getValueString("ORDER_CODE");
		String check_dis = this.getValueString("CHECK_DIS");
		String check_ret = this.getValueString("CHECK_RET");
		TParm result = SPCDispenseQueryTool.getInstance().querySale(org_code,
				start_date, end_date, check_dis, check_ret, order_code);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			messageBox("E0008");
			return;
		}
		table.setParmValue(result);
		setSumAmt();
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		table.removeRowAll();
		String clearStr = "ORDER_CODE;ORDER_DESC";
		this.clearValue(clearStr);
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		getTCheckBox("CHECK_DIS").setSelected(true);
		getTCheckBox("CHECK_RET").setSelected(false);
		this.setValue("REGION_CODE", Operator.getRegion());
		this.setValue("ORG_CODE", Operator.getDept());
		setSumAmt();
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "药房销售统计");
	}

	/**
	 * 接受返回值方法
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (!StringUtil.isNullString(order_code))
			getTextField("ORDER_CODE").setValue(order_code);
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc))
			getTextField("ORDER_DESC").setValue(order_desc);
	}

	/**
	 * 计算总金额
	 */
	private void setSumAmt() {
		double amt = 0;
		for (int i = 0; i < table.getRowCount(); i++) {
			amt += table.getItemDouble(i, "AMT");
		}
		this.setValue("SUM_AMT", amt);
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
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 得到TCheckBox对象
	 * 
	 * @param tag
	 * @return
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

}
