package com.javahis.ui.spc;

import java.sql.Timestamp;

import jdo.spc.SPCCabinetSearchTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:智能柜出入库查询
 * </p>
 * 
 * <p>
 * Description:智能柜出入库查询
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
 * @author shendr 2013-10-11
 * @version 1.0
 */
public class SPCCabinetSearchControl extends TControl {

	// 表格控件
	private TTable table_m;
	private TTable table_d;

	/**
	 * 初始化方法
	 */
	public void onInit() {
		table_m = getTable("TABLE_M");
		table_d = getTable("TABLE_D");
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
		// 状态下拉列表默认选中第一行
		((TComboBox) getComponent("STATUS")).setValue("1");
		OnInitTime();
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
	 * 查询方法
	 */
	public void onQuery() {
		TParm parm = new TParm();
		String cabinet_id = this.getValueString("CABINET_ID");
		String order_code = this.getValueString("ORDER_CODE");
		String start_time = this.getValueString("START_TIME");
		String end_time = this.getValueString("END_TIME");
		String drug_flg = "";// 麻精，普药标记
		String all_flg = "";// 汇总，明细标记
		if (getTRadioButton("TYPE_C").isSelected()) {
			drug_flg = "N";
		} else if (getTRadioButton("TYPE_D").isSelected()) {
			drug_flg = "Y";
		}
		if (getTRadioButton("TYPE_A").isSelected()) {
			all_flg = "Y";
		} else if (getTRadioButton("TYPE_B").isSelected()) {
			all_flg = "N";
		}
		parm.setData("CABINET_ID", cabinet_id);
		parm.setData("ORDER_CODE", order_code);
		parm.setData("START_TIME", start_time);
		parm.setData("DRUG_FLG", drug_flg);
		parm.setData("END_TIME", end_time);
		String status = this.getValueString("STATUS");
		TParm result = new TParm();
		if ("1".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoA(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoAA(parm);
			}
		} else if ("2".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoB(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoBB(parm);
			}
		} else if ("3".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoC(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoCC(parm);
			}
		} else if ("4".equals(status)) {
			if ("Y".equals(all_flg)) {
				result = SPCCabinetSearchTool.getInstance().queryInfoD(parm);
			} else {
				result = SPCCabinetSearchTool.getInstance().queryInfoDD(parm);
			}
		} else if ("5".equals(status)) {
			if ("Y".equals(all_flg)) {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance().queryInfoEDrug(
							parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoENormal(parm);
				}
			} else {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoEEDrug(parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoEENormal(parm);
				}
			}
		} else if ("6".equals(status)) {
			if ("Y".equals(all_flg)) {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance().queryInfoFDrug(
							parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoFNormal(parm);
				}
			} else {
				if ("Y".equals(drug_flg)) {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoFFDrug(parm);
				} else {
					result = SPCCabinetSearchTool.getInstance()
							.queryInfoFFNormal(parm);
				}
			}
		}
		if ("Y".equals(all_flg)) {
			table_m.setParmValue(result);
		} else {
			table_d.setParmValue(result);
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear() {
		String tags = "CABINET_ID;START_TIME;END_TIME;ORDER_CODE;ORDER_DESC;STATUS";
		clearValue(tags);
		getTRadioButton("TYPE_A").setSelected(true);
		getTRadioButton("TYPE_C").setSelected(true);
		table_m.removeRowAll();
		table_d.removeRowAll();
		table_m.setVisible(true);
		table_d.setVisible(false);
		OnInitTime();
	}

	/**
	 * 统计类别改变事件
	 */
	public void onTypeChanged() {
		if (getTRadioButton("TYPE_A").isSelected()) {
			table_m.setVisible(true);
			table_d.setVisible(false);
		} else if (getTRadioButton("TYPE_B").isSelected()) {
			table_m.setVisible(false);
			table_d.setVisible(true);
		}
	}

	/**
	 * 表格单击事件
	 */
	public void onTableClicked() {
		int row = 0;
		TParm selParm = new TParm();
		if (table_d.isVisible()) {
			row = table_d.getSelectedRow();
			selParm = table_d.getParmValue().getRow(row);
		} else {
			row = table_m.getSelectedRow();
			selParm = table_m.getParmValue().getRow(row);
		}
		getTextField("ORDER_CODE").setValue(selParm.getValue("ORDER_CODE"));
		getTextField("ORDER_DESC").setValue(selParm.getValue("ORDER_DESC"));
	}

	/**
	 * 初始化查询时间
	 */
	public void OnInitTime() {
		// 初始化查询时间
		Timestamp date = TJDODBTool.getInstance().getDBTime();
		// 结束时间
		Timestamp dateTime = StringTool.getTimestamp(TypeTool.getString(date)
				.substring(0, 4)
				+ "/"
				+ TypeTool.getString(date).substring(5, 7)
				+ "/01 23:59:59", "yyyy/MM/dd HH:mm:ss");
		// (上个月最后一天)
		setValue("END_TIME", StringTool.rollDate(dateTime, -1));
		// 起始时间(上个月第一天)
		setValue("START_TIME", StringTool.rollDate(dateTime, -1).toString()
				.substring(0, 4)
				+ "/"
				+ StringTool.rollDate(dateTime, -1).toString().substring(5, 7)
				+ "/01 00:00:00");
	}

	/**
	 * 获取TABLE控件
	 */
	public TTable getTable(String tag) {
		return (TTable) getComponent(tag);
	}

	/**
	 * 获取TRadioButton控件
	 */
	public TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}

	/**
	 * 获取TTextField控件
	 * 
	 * @param tag
	 * @return
	 */
	public TTextField getTextField(String tag) {
		return (TTextField) getComponent(tag);
	}

}
