package com.javahis.ui.clp;

import com.dongyang.control.TControl;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComboNode;
import com.dongyang.ui.TTable;
import jdo.sys.SYSRegionTool;
import com.javahis.util.ExportExcelUtil;
import jdo.sys.Operator;
import java.util.Date;
import java.util.Vector;

import com.dongyang.data.TParm;
import com.dongyang.util.StringTool;
import java.sql.Timestamp;
import jdo.clp.CLPVariationShowTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.base.TComboBoxModel;

/**
 * <p>
 * Title: 临床路径变异分析
 * </p>
 * 
 * <p>
 * Description: 临床路径变异分析
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 20119
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author pangben 20110707
 * @version 1.0
 */
public class CLPVariationShowControl extends TControl {
	/**
	 * 初始化方法
	 */
	public void onInit() {
		initPage();
		// 权限添加
		TComboBox cboRegion = (TComboBox) this.getComponent("REGION_CODE");
		cboRegion.setEnabled(SYSRegionTool.getInstance().getRegionIsEnabled(
				this.getValueString("REGION_CODE")));
	}

	private TTable table;

	public CLPVariationShowControl() {
	}

	/**
	 * 查询方法
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String date_s = getValueString("DATE_S");
		String date_e = getValueString("DATE_E");
		if (null == date_s || date_s.length() <= 0 || null == date_e
				|| date_e.length() <= 0) {
			this.messageBox("请输入需要查询的时间范围");
			return;
		}
		date_s = date_s.substring(0, date_s.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		date_e = date_e.substring(0, date_e.lastIndexOf(".")).replace(":", "")
				.replace("-", "").replace(" ", "");
		parm.setData("DATE_S", date_s);
		parm.setData("DATE_E", date_e);
		String clncPath = this.getValueString("CLNCPATH_CODE");
		if (this.checkNullAndEmpty(clncPath)) {
			parm.setData("CLNCPATH_CODE", clncPath);
		}
		String checkTypeCode = this.getValueString("CHKTYPE_CODE");
		if (this.checkNullAndEmpty(checkTypeCode)) {
			parm.setData("CHKTYPE_CODE", checkTypeCode);
		}
		TRadioButton type = (TRadioButton) this.getComponent("BY1");
		parm.setData("REGION_CODE", Operator.getRegion());
		if (type.isSelected()) {
			realWithoutStandard(parm);
		} else {
			standardWithoutReal(parm);
		}
	}

	/**
	 * 标准没有实际有
	 * 
	 * @param parm
	 *            TParm
	 */
	private void standardWithoutReal(TParm parm) {
		TParm result = CLPVariationShowTool.getInstance()
				.selectDataWithoutStandard(parm);
		if (result.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			table.setParmValue(new TParm());
			// 更新总条数
			updataTotalCount();
		}
		TParm parmValue = new TParm();
		int MAIN_COUNT = 0;
		int STANDARD_COUNT = 0;
		TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
		for (int i = 0; i < result.getCount(); i++) {
			String rn = result.getValue("REGION_CODE", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			parmValue.addData("REGION_CODE", rn);// wangzhilei
			// 20120724
			// 修改
			parmValue.addData("CLNCPATH_CHN_DESC", result.getValue(
					"CLNCPATH_CHN_DESC", i));
			parmValue.addData("SCHD_DESC", result.getValue("SCHD_DESC", i));
			parmValue.addData("CHKTYPE_CHN_DESC", result.getValue(
					"CHKTYPE_CHN_DESC", i));
			parmValue.addData("ORDER_DESC", result.getValue("ORDER_DESC", i));
			parmValue.addData("MAIN_COUNT", result.getInt("MAIN_COUNT", i));
			parmValue.addData("STANDARD_COUNT", result.getValue(
					"STANDARD_COUNT", i));
			double percent = (result.getDouble("STANDARD_COUNT", i) - result
					.getDouble("MAIN_COUNT", i))
					/ result.getDouble("STANDARD_COUNT", i);
			parmValue.addData("PERCENT", (StringTool.round(percent, 2) * 100)
					+ "%");
			MAIN_COUNT += result.getInt("MAIN_COUNT", i);
			STANDARD_COUNT += result.getInt("STANDARD_COUNT", i);
		}
		parmValue.addData("REGION_CODE", "总计:");// wangzhilei 20120724 修改
		parmValue.addData("CHKTYPE_CHN_DESC", "");
		parmValue.addData("ORDER_DESC", "");
		parmValue.addData("MAIN_COUNT", MAIN_COUNT);
		parmValue.addData("STANDARD_COUNT", STANDARD_COUNT);
		double percent = (STANDARD_COUNT - MAIN_COUNT) / STANDARD_COUNT;
		parmValue
				.addData("PERCENT", (StringTool.round(percent, 2) * 100) + "%");
		updateTotalCount(result.getCount());
		parmValue.setCount(result.getCount() + 1);
		table.setParmValue(parmValue);

	}

	/**
	 * 设置总行数
	 * 
	 * @param count
	 *            int
	 */
	private void updateTotalCount(int count) {
		this.setValue("COUNT", count);
	}

	/**
	 * 更新总行数
	 */
	private void updataTotalCount() {
		int total = table.getRowCount() - 1;
		total = total < 0 ? 0 : total;
		updateTotalCount(total);
	}

	/**
	 * 标准有实际没有
	 * 
	 * @param parm
	 *            TParm
	 */
	private void realWithoutStandard(TParm parm) {
		TParm result = CLPVariationShowTool.getInstance()
				.selectDataStadardWithoutReal(parm);
		if (result.getCount() <= 0) {
			this.messageBox("没有需要查询的数据");
			table.setParmValue(new TParm());
			// 更新总条数
			updataTotalCount();
		}
		TParm parmValue = new TParm();
		int MAIN_COUNT = 0;
		int STANDARD_COUNT = 0;
		TComboBox com = (TComboBox) this.getComponent("REGION_CODE");
		for (int i = 0; i < result.getCount(); i++) {
			String rn = result.getValue("REGION_CODE", i);
			TComboBoxModel tbm = com.getModel();
			Vector v = tbm.getItems();
			for (int j = 0; j < v.size(); j++) {
				TComboNode tn = (TComboNode) v.get(j);
				if (rn.equals(tn.getID())) {
					rn = tn.getName();
					break;
				}
			}
			parmValue.addData("REGION_CODE", rn);// wangzhilei 20120724 修改
			parmValue.addData("CLNCPATH_CHN_DESC", result.getValue(
					"CLNCPATH_CHN_DESC", i));
			parmValue.addData("SCHD_DESC", result.getValue("SCHD_DESC", i));
			parmValue.addData("CHKTYPE_CHN_DESC", result.getValue(
					"CHKTYPE_CHN_DESC", i));
			parmValue.addData("ORDER_DESC", result.getValue("ORDER_DESC", i));
			parmValue.addData("MAIN_COUNT", result.getInt("STANDARD_COUNT", i)
					- result.getInt("MAIN_COUNT", i)); // MAIN_COUNT是没有使用的，而页面要应用人数，故需要求差
			parmValue.addData("STANDARD_COUNT", result.getValue(
					"STANDARD_COUNT", i));
			double percent = (result.getDouble("MAIN_COUNT", i) / result
					.getDouble("STANDARD_COUNT", i));
			parmValue.addData("PERCENT", (StringTool.round(percent, 2) * 100)
					+ "%");
			MAIN_COUNT += result.getInt("MAIN_COUNT", i);
			STANDARD_COUNT += result.getInt("STANDARD_COUNT", i);
		}
		parmValue.addData("REGION_CODE", "总计:");// wangzhilei 20120724 修改
		parmValue.addData("CHKTYPE_CHN_DESC", "");
		parmValue.addData("ORDER_DESC", "");
		parmValue.addData("MAIN_COUNT", (STANDARD_COUNT - MAIN_COUNT));
		parmValue.addData("STANDARD_COUNT", STANDARD_COUNT);
		double percent = MAIN_COUNT / STANDARD_COUNT; // MAIN_COUNT是没有使用的，而页面要应用人数，故需要求差
		parmValue
				.addData("PERCENT", (StringTool.round(percent, 2) * 100) + "%");
		updateTotalCount(result.getCount());
		parmValue.setCount(result.getCount() + 1);
		table.setParmValue(parmValue);
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		Timestamp date = StringTool.getTimestamp(new Date());
		table = (TTable) getComponent("TABLE");
		this.setValue("REGION_CODE", Operator.getRegion());
		// 初始化查询区间
		this.setValue("DATE_E", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("DATE_S", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");

	}

	/**
	 * 打印方法
	 */
	public void onPrint() {

		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要打印的数据");
			return;
		}
		TParm result = new TParm();
		parm.addData("SYSTEM", "COLUMNS", "REGION_CODE");
		parm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "SCHD_DESC");
		parm.addData("SYSTEM", "COLUMNS", "CHKTYPE_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "ORDER_DESC");
		// parm.addData("SYSTEM", "COLUMNS", "CLNCPATH_CHN_DESC");
		parm.addData("SYSTEM", "COLUMNS", "STANDARD_COUNT");
		parm.addData("SYSTEM", "COLUMNS", "MAIN_COUNT");
		parm.addData("SYSTEM", "COLUMNS", "PERCENT");
		result.setData("S_DATE", "TEXT", getValueString("DATE_S").substring(0,
				getValueString("DATE_S").lastIndexOf(".")));
		result.setData("E_DATE", "TEXT", getValueString("DATE_E").substring(0,
				getValueString("DATE_S").lastIndexOf(".")));
		result.setData("OPT_USER", Operator.getName());
		result.setData("TABLE", parm.getData());
		result.setData("TITLE", "TEXT", (null != Operator
				.getHospitalCHNShortName() ? Operator.getHospitalCHNShortName()
				: "所有院区")
				+ "临床路径变异分析");
		// 卢海加入制表人
		// 表尾
		result.setData("CREATEUSER", "TEXT", Operator.getName());
		this.openPrintWindow("%ROOT%\\config\\prt\\CLP\\CLPNewVariationShow.jhw",
				result);

	}

	/**
	 * 清空
	 */
	public void onClear() {
		initPage();
		table.removeRowAll();
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		// 得到UI对应控件对象的方法
		TParm parm = table.getParmValue();
		if (null == parm || parm.getCount() <= 0) {
			this.messageBox("没有需要导出的数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "临床路径变异分析");
	}

	/**
	 * 检查是否为空或空串
	 * 
	 * @return boolean
	 */
	private boolean checkNullAndEmpty(String checkstr) {
		if (checkstr == null) {
			return false;
		}
		if ("".equals(checkstr)) {
			return false;
		}
		return true;
	}

}
