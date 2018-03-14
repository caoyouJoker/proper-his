package com.javahis.ui.spc;

import jdo.spc.SPCCodeMapTool;
import jdo.spc.SPCDrugSearchTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.system.textFormat.TextFormatSYSUnit;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:麻精追踪报表
 * </p>
 * 
 * <p>
 * Description:麻精追踪报表
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
 * @author shendr 2013-11-27
 * @version 1.0
 */
public class SPCDrugSearchControl extends TControl {

	private TTable table;

	public void onInit() {
		table = getTable("TABLE");
		// 设置弹出菜单
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		getTextField("ORDER_CODE")
				.setPopupMenuParameter(
						"UD",
						getConfigParm().newConfig(
								"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		getTextField("ORDER_CODE").addEventListener(
				TPopupMenuEvent.RETURN_VALUE, this, "popReturn");
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
		if (!StringUtil.isNullString(order_code)) {
			getTextField("ORDER_CODE").setValue(order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_desc)) {
			getTextField("ORDER_DESC").setValue(order_desc);
		}
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		String order_code = this.getValueString("ORDER_CODE");
		String bar_code = this.getValueString("BAR_CODE");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		if (!StringUtil.isNullString(start_time)) {
			start_time = start_time.substring(0, 19);
		}
		if (!StringUtil.isNullString(end_time)) {
			end_time = end_time.substring(0, 19);
		}
		TParm parm = new TParm();
		parm.setData("ORDER_CODE", order_code);
		parm.setData("BAR_CODE", bar_code);
		parm.setData("START_TIME", start_time);
		parm.setData("END_TIME", end_time);
		TParm result = SPCDrugSearchTool.getInstance().query(parm);
		if (result.getErrCode() < 0) {
			messageBox("查询失败");
			return;
		}
		for (int i = 0; i < result.getCount(); i++) {
			if ("A".equals(result.getValue("TYPE", i))
					|| "C".equals(result.getValue("TYPE", i))
					|| "E".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "入库");
			} else if ("B".equals(result.getValue("TYPE", i))
					|| "D".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "出库");
			} else if ("F".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "发药");
			} else if ("G".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "给药");
			} else if ("H".equals(result.getValue("TYPE", i))) {
				result.setData("TYPE", i, "回收");
			}
		}
		table.setParmValue(result);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		String tags = "ORDER_CODE;ORDER_DESC;BAR_CODE;START_TIME;END_TIME";
		this.clearValue(tags);
		table.removeRowAll();
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "麻精追踪报表");
	}

	/**
	 * 获取TABLE控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTable getTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 获取TEXTFIELD控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) this.getComponent(tag);
	}

}
