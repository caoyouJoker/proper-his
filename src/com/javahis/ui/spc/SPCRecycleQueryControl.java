package com.javahis.ui.spc;

import jdo.spc.INDSQL;
import jdo.spc.SPCRecycleQueryTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:麻精空瓶回收记录查询Control
 * </p>
 * 
 * <p>
 * Description:麻精空瓶回收记录查询Control
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
 * @author shendr 2013-09-26
 * @version 1.0
 */
public class SPCRecycleQueryControl extends TControl {

	/**
	 * 药品
	 */
	private TTextField ORDER_CODE;
	private TTextField ORDER_DESC;

	/** 表格 */
	private TTable table;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		ORDER_CODE = (TTextField) getComponent("ORDER_CODE");
		ORDER_DESC = (TTextField) getComponent("ORDER_DESC");
		table = (TTable) getComponent("TABLE");
		TParm parm = new TParm();
		parm.setData("CAT1_TYPE", "PHA");
		// 设置弹出菜单
		ORDER_CODE.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// 定义接受返回值方法
		ORDER_CODE.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
		// 根据库房类别设置药库列表(ORG_CODE)
		onSetOrgCodeByOrgType();
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
		String order_desc = parm.getValue("ORDER_DESC");
		if (!StringUtil.isNullString(order_code)
				&& !StringUtil.isNullString(order_desc)) {
			ORDER_CODE.setValue(order_code);
			ORDER_DESC.setValue(order_desc);
		}
	}

	/**
	 * 根据库房类别设置药库列表(ORG_CODE)
	 * 
	 * @param org_type
	 */
	private void onSetOrgCodeByOrgType() {
		getTextFormat("EXE_DEPT_CODE").setPopupMenuSQL(
				INDSQL.getIndOrgComobo("C", "", Operator.getRegion()));
		getTextFormat("EXE_DEPT_CODE").onQuery();
		getTextFormat("EXE_DEPT_CODE").setText("");
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 查询科室，判断要查询哪一张表
		String exe_dept_code = this.getValueString("EXE_DEPT_CODE");
		String order_code = this.getValueString("ORDER_CODE");
		String reclaim_date = this.getValueString("RECLAIM_DATE");
		TParm parm = new TParm();
		parm.setData("EXE_DEPT_CODE", exe_dept_code);
		parm.setData("ORDER_CODE", order_code);
		parm.setData("RECLAIM_DATE", reclaim_date);
		if (!StringUtil.isNullString(reclaim_date)) {
			reclaim_date = reclaim_date.substring(0, 19);
		}
		TParm result1 = SPCRecycleQueryTool.getInstance().queryOrg(
				exe_dept_code);
		TParm result2 = SPCRecycleQueryTool.getInstance().queryDept(
				exe_dept_code);
		String station_flg = result1.getValue("STATION_FLG", 0);
		String exinv_flg = result1.getValue("EXINV_FLG", 0);
		String emg_fit_flg = result2.getValue("EMG_FIT_FLG", 0);
		TParm result = new TParm();
		if ("Y".equals(station_flg)) {
			if ("Y".equals(emg_fit_flg)) {
				// 急诊区
				result = SPCRecycleQueryTool.getInstance().queryOpd(parm);
			} else if ("N".equals(emg_fit_flg) || "".equals(emg_fit_flg)) {
				// 病区
				result = SPCRecycleQueryTool.getInstance().queryInd(parm);
			}
		} else {
			if ("Y".equals(exinv_flg)) {
				// 手术/介入
				result = SPCRecycleQueryTool.getInstance().querySpc(parm);
			}
		}
		table.setParmValue(result);
	}

	/**
	 * 表格单击事件
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = table.getParmValue().getRow(row);
		setValue("ORDER_CODE", tableParm.getValue("ORDER_CODE"));
		setValue("ORDER_DESC", tableParm.getValue("ORDER_DESC"));
		setValue("RECLAIM_DATE", tableParm.getValue("RECLAIM_DATE").substring(
				0, 19));
	}

	/**
	 * 打印报表
	 */
	public void onPrint() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		// 打印数据
		TParm data = new TParm();
		// 表头数据
		data.setData("TITLE", "TEXT", "麻精空瓶回收记录查询统计表");
		data.setData("DATE", "TEXT", "统计时间: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		// 遍历表格中的元素
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("ORDER_DESC", tableParm.getValue("ORDER_DESC", i));
			parm.addData("SPECIFICATION", tableParm
					.getValue("SPECIFICATION", i));
			parm.addData("BAR_CODE", tableParm.getValue("BAR_CODE", i));
			parm.addData("PAT_NAME", tableParm.getValue("PAT_NAME", i));
			parm.addData("EXE_DEPT_CODE", tableParm
					.getValue("EXE_DEPT_CODE", i));
			parm.addData("BED_NO", tableParm.getValue("BED_NO", i));
			parm.addData("MR_NO", tableParm.getValue("MR_NO", i));
			parm.addData("RECLAIM_USER", tableParm.getValue("RECLAIM_USER", i));
			parm.addData("RECLAIM_DATE", tableParm.getValue("RECLAIM_DATE", i)
					.subSequence(0, 19));
		}
		// 总行数
		parm.setCount(parm.getCount("ORDER_DESC"));
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SPECIFICATION");
		parm.addData("SYSTEM", "COLUMNS", "BAR_CODE");
		parm.addData("SYSTEM", "COLUMNS", "PAT_NAME");
		parm.addData("SYSTEM", "COLUMNS", "EXE_DEPT_CODE");
		parm.addData("SYSTEM", "COLUMNS", "BED_NO");
		parm.addData("SYSTEM", "COLUMNS", "MR_NO");
		parm.addData("SYSTEM", "COLUMNS", "RECLAIM_USER");
		parm.addData("SYSTEM", "COLUMNS", "RECLAIM_DATE");
		// 将表格放到容器中
		data.setData("TABLE", parm.getData());
		// 表尾数据
		data.setData("USER", "TEXT", "统计人: " + Operator.getName());
		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\SPCRecycleQuery.jhw",
				data);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "麻精空瓶回收记录查询");
	}

	/**
	 * 获取TTextFormat控件
	 * 
	 * @return
	 */
	private TTextFormat getTextFormat(String tag) {
		return (TTextFormat) getComponent(tag);
	}

}
