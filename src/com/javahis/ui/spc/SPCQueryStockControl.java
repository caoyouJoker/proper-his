package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCQueryStockTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:查询库存结余Control
 * </p>
 * 
 * <p>
 * Description:查询库存结余Control
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2013
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author shendr 2014-05-16
 * @version 1.0
 */
public class SPCQueryStockControl extends TControl {

	/**
	 * TABLE控件
	 */
	private TTable table;

	/**
	 * 界面初始化
	 */
	public void onInit() {
		// 初始化控件及默认值
		table = getTable("TABLE");
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		this.setValue("TRANDATE", date.toString().substring(0, 7));
		this.setValue("DEPT_CODE", Operator.getDept());
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		// 封装查询参数
		TParm parm = new TParm();
		String dept_code = this.getValueString("DEPT_CODE");
		String trandate = this.getValueString("TRANDATE");
		// 结算日(每个月25号)
		if (!StringUtil.isNullString(trandate)) {
			trandate = trandate.replaceAll("-", "").substring(0, 6) + "25";
		} else {
			messageBox("请选择查询时间");
		}
		parm.setData("DEPT_CODE", dept_code);
		parm.setData("TRANDATE", trandate);
		TParm result = SPCQueryStockTool.getInstance().queryStock(parm);
		if (result.getErrCode() < 0 || result.getCount() <= 0) {
			messageBox("E0008");
			return;
		}
		table.setParmValue(result);
	}

	/**
	 * 导出EXCEL
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(
				table,
				this.getValueString("TRANDATE").replaceAll("-", "").substring(
						0, 7)
						+ "25库存结余");
	}

	/**
	 * 清空
	 */
	public void onClear() {
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("TRANDATE", date);
		this.setValue("DEPT_CODE", Operator.getDept());
		table.removeRowAll();
	}

	/**
	 * 获得TABLE控件
	 * 
	 * @param tag
	 * @return
	 */
	private TTable getTable(String tag) {
		return (TTable) getComponent(tag);
	}

}
