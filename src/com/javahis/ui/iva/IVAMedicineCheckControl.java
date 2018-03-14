package com.javahis.ui.iva;

import java.sql.Timestamp;


import jdo.iva.IVAMedicineCheckTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;

import com.dongyang.ui.TTextFormat;

import com.dongyang.util.StringTool;

import com.javahis.system.textFormat.TextFormatSYSOperator;

/**
 * <p>
 * Title: 病区统药核对Control
 * </p>
 * 
 * <p>
 * Description: 病区统药核对Control
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
 * @author zhangy 2013.07.21
 * @version 1.0
 */
public class IVAMedicineCheckControl extends TControl {
	//明细表
	private TTable table_d;
	//主表
	private TTable table_m;

	// 得到table控件
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}


	// 得到RadioButton控件
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}

	public void onInit() {
		super.onInit();
		
		initPage();
	}

	public void initPage() {
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		this.setValue("IVA_INTGMED_CHECK_USER", Operator.getID());
		// callFunction("UI|TABLE_1|addEventListener",
		// TTableEvent.CHECK_BOX_CLICKED, this,
		// "onTableCheckBoxClicked");
	}

	/*
	 * 查询
	 */
	public void onQuery() {

		table_d = this.getTable("TABLE_1");
		table_m = this.getTable("TABLE_2");
		// 审核人
		String check_user = this.getValueString("IVA_INTGMED_CHECK_USER");
		// 审核区间
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		String station_code = this.getValueString("STATION_CODE");
		// 存放查询条件的值
		TParm tparm = new TParm();
		if (station_code != null && !"".equals(station_code)) {
			tparm.setData("STATION_CODE", station_code);
		}
		if (check_user != null && !"".equals(check_user)) {
			tparm.setData("IVA_INTGMED_CHECK_USER", check_user);
		}
		if(getRadioButton("KIND_UD").isSelected()){
			tparm.setData("KIND", "UD");
		}
		if(getRadioButton("KIND_ST").isSelected()){
			tparm.setData("KIND", "ST");
		}
		if(getRadioButton("KIND_F").isSelected()){
			tparm.setData("KIND", "F");
		}
//		System.out.println("tparm>>>>>>>>>"+tparm);
		if (getRadioButton("BUTTON_1").isSelected()) {
			if ((station_code == null || "".equals(station_code.trim()))) {
				this.messageBox("查询条件不能同时为空！");
				return;
			}
			if (start_time != null && !"".equals(start_time)) {
				tparm.setData("START_TIME_N", start_time.toString().substring(0, 19));
			}
			if (end_time != null && !"".equals(end_time)) {
				tparm.setData("END_TIME_N", end_time.toString().substring(0, 19));
			}
//			System.out.println("KIND======"+tparm.getValue("KIND"));
			TParm tParm = IVAMedicineCheckTool.getInstance().queryInfo(tparm);
			if(tParm.getCount() <= 0){
				table_d.removeRowAll();
				table_m.removeRowAll();
				this.messageBox("未查询到数据");
				return;
			}
			if (tParm.getCount() == 1) {
				tParm.setData("SELECT_FLG", 0, "Y");
			}
			table_d.setParmValue(tParm);
			if (table_d.getRowCount() == 1) {
				onTableinit();
			}
		}
		if (getRadioButton("BUTTON_2").isSelected()) {
			if ((station_code == null || "".equals(station_code.trim()))
					&& (start_time == null || "".equals(start_time.trim()))
					&& (end_time == null || "".equals(end_time.trim()))
					&& (check_user == null || "".equals(check_user.trim()))) {
				this.messageBox("查询条件不能同时为空！");
				return;
			}
			if (start_time != null && !"".equals(start_time)) {
				tparm.setData("START_TIME_Y", start_time.toString().substring(0, 19));
			}
			if (end_time != null && !"".equals(end_time)) {
				tparm.setData("END_TIME_Y", end_time.toString().substring(0, 19));
			}
//			TParm result = IVAMedicineCheckTool.getInstance().queryIn(tparm);
			TParm result = IVAMedicineCheckTool.getInstance().queryInfo(tparm);
			if(result.getCount() <= 0){
				table_d.removeRowAll();
				table_m.removeRowAll();
				this.messageBox("未查询到数据");
				return;
			}
			table_d.setParmValue(result);
			if (table_d.getRowCount() == 1) {
				onTableinit();
			}
		}

	}

	/*
	 * 单选按钮触发事件
	 */
	public void onRidoButton() {
		if (getRadioButton("BUTTON_2").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(false);
			((TextFormatSYSOperator) getComponent("IVA_INTGMED_CHECK_USER"))
					.setEnabled(true);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(true);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(true);
			this.clearValue("IVA_INTGMED_CHECK_USER");
			initPage();
			onClear();

		}
		if (getRadioButton("BUTTON_1").isSelected()) {
			((TMenuItem) getComponent("save")).setEnabled(true);
			((TextFormatSYSOperator) getComponent("IVA_INTGMED_CHECK_USER"))
					.setEnabled(false);
//			((TTextFormat) getComponent("START_TIME")).setEnabled(false);
//			((TTextFormat) getComponent("END_TIME")).setEnabled(false);
			initPage();
			onClear();
		}
	}

	/*
	 * 数据只一条时，默认带出细表信息
	 */
	public void onTableinit() {
		// 得到table控件

		table_d = this.getTable("TABLE_1");
		table_m = this.getTable("TABLE_2");

		// 得到选中条的索引

		TParm parm = new TParm();
		// 判断table中是否有值

		// 得到table的值
		TParm tparm = table_d.getParmValue();

		String tNo = tparm.getValue("INTGMED_NO", 0);
		parm.setData("INTGMED_NO", tNo);
		TParm result = IVAMedicineCheckTool.getInstance().queryInfocheck(parm);

		table_m.setParmValue(result);
	}

	/*
	 * 单击事件
	 */
	public void onTableClicked() {
		// 得到table控件
		
		table_d = this.getTable("TABLE_1");
		table_m = this.getTable("TABLE_2");
		table_m.removeRowAll();
		// 得到选中条的索引
		int row = table_d.getSelectedRow();
		TParm tParm = new TParm();
		// 判断table中是否有值
		if (row != -1) {
			// 得到table的值
			TParm tparm = table_d.getParmValue();
//			this.setValue("INTGMED_NO", tparm.getValue("INTGMED_NO", row));
			if (getRadioButton("BUTTON_2").isSelected()) {
				this.setValue("IVA_INTGMED_CHECK_USER", tparm.getValue(
						"IVA_INTGMED_CHECK_USER", row));
			}
			this.setValue("STATION_CODE", tparm.getValue("STATION_DESC", row));
			tParm.setData("INTGMED_NO", tparm.getValue("INTGMED_NO", row));
		}
		TParm result = IVAMedicineCheckTool.getInstance().queryInfocheck(tParm);
		table_m.setParmValue(result);
		onTableCheckBoxClicked(this);
	}

	/*
	 *  清空控件的值
	 */
	public void onClear() {
//		if (getRadioButton("BUTTON_2").isSelected()) {
//			this.clearValue("IVA_INTGMED_CHECK_USER");
//			this.clearValue("START_TIME");
//			this.clearValue("END_TIME");
//		}
		Timestamp date = SystemTool.getInstance().getDate();
		// 初始化查询区间
		this.setValue("END_TIME", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_TIME", StringTool.rollDate(date, -1).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
		table_d = this.getTable("TABLE_1");
		table_d.removeRowAll();
		table_m = this.getTable("TABLE_2");
		table_m.removeRowAll();
//		this.clearValue("INTGMED_NO");
		this.clearValue("STATION_CODE");

	}

	/*
	 * 更新审核状态
	 */
	public void onSave() {
		
		table_d = this.getTable("TABLE_1");
		TParm tableParm = table_d.getParmValue();
		TParm result = new TParm();
		String check_user = this.getValueString("IVA_INTGMED_CHECK_USER");
		if(table_d.getRowCount()==1){
			result.setData("INTGMED_NO",tableParm.getValue("INTGMED_NO",0));
			result.setData("IVA_INTGMED_CHECK_USER",check_user);
			TParm parm = IVAMedicineCheckTool.getInstance().updateInfo(result);
			if (parm.getErrCode() < 0) {
				this.messageBox("审核失败");
				return;
			}
			messageBox("审核成功");
			this.getTable("TABLE_2").removeRowAll();
			table_d.removeRow(0);
		}
		int selectrow = table_d.getSelectedRow();
		// for (int i = 0; i < table_d.getRowCount(); i++) {
		if (check_user != null && !"".equals(check_user)) {
			if ("N".equals(table_d.getItemString(selectrow, "SELECT_FLG"))) {
				return;
			}
			result.setData("INTGMED_NO", tableParm.getValue("INTGMED_NO",selectrow));
			result.setData("IVA_INTGMED_CHECK_USER", check_user);
			TParm parm = IVAMedicineCheckTool.getInstance().updateInfo(result);

			if (parm.getErrCode() < 0) {
				this.messageBox("审核失败");
				return;
			}
			messageBox("审核成功");
			this.getTable("TABLE_2").removeRowAll();
			table_d.removeRow(selectrow);
		}else{
			messageBox("审核人不能为空");
		}
	}

	/*
	 * 复选框事件
	 */
	public void onTableCheckBoxClicked(Object obj) {
		if (getRadioButton("BUTTON_1").isSelected()) {
			table_d = this.getTable("TABLE_1");
			int column = table_d.getSelectedRow();
			if ("N".equals(table_d.getItemString(column, "SELECT_FLG"))) {
				table_d.setItem(column, "SELECT_FLG", "Y");
			} else {
				table_d.setItem(column, "SELECT_FLG", "N");
			}
			for (int i = 0; i < table_d.getRowCount(); i++) {
				if (i != column) {
					table_d.setItem(i, "SELECT_FLG", "N");
				}
			}

		}
	}

	/*
	 * 回车事件
	 */
	public void onAction1() {
		onQuery();

	}
}
