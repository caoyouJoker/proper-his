package com.javahis.ui.spc;

import java.sql.Timestamp;
import java.util.Date;

import jdo.spc.INDLabelTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;
import jdo.util.Manager;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.system.textFormat.TextFormatINDOrgForWord;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:电子标签基站覆盖区域维护
 * </p>
 * 
 * <p>
 * Description:电子标签基站覆盖区域维护
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
public class INDLabelControl extends TControl {

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
	 * 获得TRadioButton控件
	 * 
	 * @param tag
	 *            控件TAG
	 * @return
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * 获得TextFormatINDOrgForWord控件
	 * 
	 * @param tag
	 *            控件TAG
	 * @return
	 */
	public TextFormatINDOrgForWord getTextFormatINDOrgForWord(String tag) {
		return (TextFormatINDOrgForWord) getComponent(tag);
	}

	/**
	 * 获得TextFormatSYSStation控件
	 * 
	 * @param tag
	 *            控件TAG
	 * @return
	 */
	public TextFormatSYSStation getTextFormatSYSStation(String tag) {
		return (TextFormatSYSStation) getComponent(tag);
	}

	/**
	 * 依据所选择的使用部门属性,套用不同控件
	 */
	public void onClassifyChange() {
		if (getTRadioButton("CLASSIFY_B").isSelected()) {
			getTextFormatINDOrgForWord("LABEL_IDA").setVisible(false);
			getTextFormatSYSStation("LABEL_IDB").setVisible(true);
		} else if (getTRadioButton("CLASSIFY_A").isSelected()) {
			getTextFormatSYSStation("LABEL_IDB").setVisible(false);
			getTextFormatINDOrgForWord("LABEL_IDA").setVisible(true);
		}
	}

	/**
	 * 清空表格与控件
	 */
	public void onClear() {
		table.removeRowAll();
		String tags = "LABEL_IDA;LABEL_IDB;AP_REGION";
		clearValue(tags);
		if (getTextFormatINDOrgForWord("LABEL_IDA").isVisible()) {
			getTextFormatINDOrgForWord("LABEL_IDA").setEnabled(true);
		} else {
			getTextFormatSYSStation("LABEL_IDB").setEnabled(true);
		}
	}

	/**
	 * 表格单击
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		TParm selParm = table.getParmValue().getRow(row);
		if ("科室".equals(selParm.getData("CLASSIFY")))
			getTRadioButton("CLASSIFY_A").setSelected(true);
		else
			getTRadioButton("CLASSIFY_B").setSelected(true);
		if (getTextFormatINDOrgForWord("LABEL_IDA").isVisible()) {
			setValue("LABEL_IDA", selParm.getData("LABEL_ID") + " "
					+ table.getItemData(row, "ORG_DESC"));
			getTextFormatINDOrgForWord("LABEL_IDA").setEnabled(false);
		} else {
			setValue("LABEL_IDB", selParm.getData("LABEL_ID") + " "
					+ table.getItemData(row, "ORG_DESC"));
			getTextFormatSYSStation("LABEL_IDB").setEnabled(false);
		}
		setValue("AP_REGION", selParm.getData("AP_REGION"));
	}

	/**
	 * 查询
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String LABEL_ID = (String) (getTextFormatINDOrgForWord("LABEL_IDA")
				.isVisible() ? getTextFormatINDOrgForWord("LABEL_IDA")
				.getValue() : getTextFormatSYSStation("LABEL_IDB").getValue());
		if (!StringUtil.isNullString(LABEL_ID)) {
			parm.setData("LABEL_ID", LABEL_ID);
		}
		String CLASSIFY = getTRadioButton("CLASSIFY_A").isSelected() ? "1"
				: "2";
		if (!StringUtil.isNullString(CLASSIFY)) {
			parm.setData("CLASSIFY", CLASSIFY);
		}
		TParm result = INDLabelTool.getInstance().queryInfo(parm);
		for (int i = 0; i < result.getCount(); i++) {
			if ("1".equals(result.getData("CLASSIFY", i)))
				result.setData("CLASSIFY", i, "科室");
			else
				result.setData("CLASSIFY", i, "护士站");
		}
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
		parm.setData("LABEL_ID", getTextFormatINDOrgForWord("LABEL_IDA")
				.isVisible() ? getValueString("LABEL_IDA")
				: getValueString("LABEL_IDB"));
		parm.setData("AP_REGION", getValueString("AP_REGION"));
		parm.setData("CLASSIFY", getTRadioButton("CLASSIFY_A").isSelected() ? 1
				: 2);
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_DATE", date);
		parm.setData("OPT_TERM", Operator.getIP());
		if (table.getSelectedRow() < 0) {
			TParm result = INDLabelTool.getInstance().insertInfo(parm);
			if (result.getErrCode() < 0) {
				messageBox("保存失败");
				return;
			}
			messageBox("保存成功");
		} else {
			parm.setData("LABEL_ID", table.getParmValue().getRow(
					table.getSelectedRow()).getData("LABEL_ID"));
			TParm result = INDLabelTool.getInstance().updateInfo(parm);
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
	 */
	public boolean checkData() {
		if (getTextFormatINDOrgForWord("LABEL_IDA").isVisible()) {
			if (getValueString("LABEL_IDA").length() == 0
					|| getValueString("LABEL_IDA") == "")
				return false;
		} else if (getTextFormatINDOrgForWord("LABEL_IDB").isVisible()) {
			if (getValueString("LABEL_IDB").length() == 0
					|| getValueString("LABEL_IDB") == "")
				return false;
		}
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
		parm.setData("LABEL_ID", table.getItemData(row, "LABEL_ID"));
		TParm result = INDLabelTool.getInstance().deleteInfo(parm);
		if (result.getErrCode() < 0) {
			messageBox("删除失败");
			return;
		}
		messageBox("删除成功");
		onClear();
		onQuery();
	}

	/**
	 * 打印报表
	 */
	public void onPrint() {
		table = getTTable("TABLE");
		if (table.getRowCount() <= 0) {
			this.messageBox("没有打印数据");
			return;
		}
		// 打印数据
		TParm data = new TParm();
		// 表头数据
		data.setData("TITLE", "TEXT", Manager.getOrganization()
				.getHospitalCHNFullName(Operator.getRegion())
				+ "电子标签基站覆盖区域统计表");
		data.setData("DATE", "TEXT", "统计时间: "
				+ SystemTool.getInstance().getDate().toString()
						.substring(0, 10).replace('-', '/'));
		// 表格数据
		TParm parm = new TParm();
		TParm tableParm = table.getParmValue();
		// 遍历表格中的元素
		for (int i = 0; i < table.getRowCount(); i++) {
			parm.addData("LABEL_ID", tableParm.getValue("LABEL_ID", i));
			parm.addData("ORG_DESC", tableParm.getValue("ORG_DESC", i));
			parm.addData("CLASSIFY", tableParm.getValue("CLASSIFY", i));
			parm.addData("AP_REGION", tableParm.getValue("AP_REGION", i));
			parm.addData("OPT_USER", tableParm.getValue("OPT_USER", i));
			parm.addData("OPT_DATE", tableParm.getValue("OPT_DATE", i));
			parm.addData("OPT_TERM", tableParm.getValue("OPT_TERM", i));
		}
		// 总行数
		parm.setCount(parm.getCount("LABEL_ID"));
		parm.addData("SYSTEM", "COLUMNS", "LABEL_ID");
		parm.addData("SYSTEM", "COLUMNS", "ORG_DESC");
		parm.addData("SYSTEM", "COLUMNS", "CLASSIFY");
		parm.addData("SYSTEM", "COLUMNS", "AP_REGION");
		parm.addData("SYSTEM", "COLUMNS", "OPT_USER");
		parm.addData("SYSTEM", "COLUMNS", "OPT_DATE");
		parm.addData("SYSTEM", "COLUMNS", "OPT_TERM");
		// 将表格放到容器中
		System.out.println(parm.getData());
		data.setData("TABLE", parm.getData());
		// 表尾数据
		data.setData("USER", "TEXT", "统计人: " + Operator.getName());
		// 调用打印方法
		this.openPrintWindow("%ROOT%\\config\\prt\\spc\\INDLabel.jhw", data);
	}

	/**
	 * 汇出Excel
	 */
	public void onExport() {
		if (table.getRowCount() <= 0) {
			this.messageBox("没有汇出数据");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "电子标签基站覆盖区域查询");
	}

}
