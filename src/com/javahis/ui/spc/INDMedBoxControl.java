package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDMedBoxTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title:病区药盒对照维护
 * </p>
 * 
 * <p>
 * Description:病区药盒对照维护
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author shendr 2013-05-21
 * @version 1.0
 */
public class INDMedBoxControl extends TControl {

	// 定义全局TTable控件
	private TTable table;

	public void onInit() {
		table = this.getTTable("TABLE");
	}

	/**
	 * 获得TTable控件
	 * 
	 * @param tag
	 *            控件TAG
	 * @return
	 */
	public TTable getTTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * 获得TTextField控件
	 * 
	 * @param tag
	 *            控件TAG
	 * @return
	 */
	public TTextField getTTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

	/**
	 * 清空表格与控件
	 */
	public void onClear() {
		table.removeRowAll();
		String tags = "ELETAG_CODE;AP_REGION";
		clearValue(tags);
		getTTextField("ELETAG_CODE").setEnabled(true);
	}

	/**
	 * 表格单击
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		TParm selParm = table.getParmValue().getRow(row);
		setValue("ELETAG_CODE", selParm.getData("ELETAG_CODE"));
		setValue("AP_REGION", selParm.getData("AP_REGION"));
		// 不允许修改电子标签ID
		getTTextField("ELETAG_CODE").setEnabled(false);
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String ELETAG_CODE = getValueString("ELETAG_CODE");
		if (!(ELETAG_CODE.length() == 0 || ELETAG_CODE == ""))
			parm.setData("ELETAG_CODE", ELETAG_CODE);
		TParm result = INDMedBoxTool.getInstance().queryInfo(parm);
		table.setParmValue(result);
	}

	/**
	 * 保存与更新
	 */
	public void onSave() {
		if (!checkData())
			return;
		Timestamp date = StringTool.getTimestamp(new Date());
		TParm parm = new TParm();
		parm.setData("ELETAG_CODE", getValueString("ELETAG_CODE"));
		parm.setData("AP_REGION", getValueString("AP_REGION"));
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		if (table.getSelectedRow() < 0) {
			TParm result = INDMedBoxTool.getInstance().insertInfo(parm);
			if (result.getErrCode() < 0) {
				messageBox("保存失败");
				return;
			}
			messageBox("保存成功");
		} else {
			parm.setData("ELETAG_CODE", table.getParmValue().getRow(
					table.getSelectedRow()).getData("ELETAG_CODE"));
			TParm result = INDMedBoxTool.getInstance().updateInfo(parm);
			if (result.getErrCode() < 0) {
				messageBox("更新失败");
				return;
			}
			messageBox("更新成功");
		}
		onClear();
		onQuery();
	}

	/**
	 * 数据校验
	 * 
	 * @return
	 */
	public boolean checkData() {
		if (getValueString("ELETAG_CODE").length() == 0
				|| getValueString("ELETAG_CODE") == "")
			return false;
		if (getValueString("AP_REGION").length() == 0
				|| getValueString("AP_REGION") == "")
			return false;
		return true;
	}

	/**
	 * 删除
	 */
	public void onDelete() {
		int row = table.getSelectedRow();
		if (row < 0) {
			messageBox("请选择要删除的数据");
			return;
		}
		TParm parm = new TParm();
		parm.setData("ELETAG_CODE", (table.getParmValue().getRow(table
				.getSelectedRow())).getData("ELETAG_CODE"));
		TParm result = INDMedBoxTool.getInstance().deleteInfo(parm);
		if (result.getErrCode() < 0) {
			messageBox("删除失败");
			return;
		}
		messageBox("删除成功");
		onClear();
		onQuery();
	}

}