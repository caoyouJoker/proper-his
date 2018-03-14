package com.javahis.ui.spc;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.*;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import java.sql.Timestamp;
import java.util.Date;
import jdo.spc.INDSQL;
import jdo.spc.IndSysParmTool;
import jdo.sys.Operator;

/**
 * <p>
 * Title:药库参数设定
 * </p>
 * 
 * <p>
 * Description:药库参数设定
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company: javahis
 * </p>
 * 
 * @author zhangy 2009.4.22
 * @version 1.0
 */
public class INDSYSParmControl extends TControl {

	private String operation = "insert";

	public INDSYSParmControl() {
		super();
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		// 权限设定

		// 初始画面数据
		initPage();
	}

	/**
	 * 保存方法
	 * 
	 * @return boolean
	 */
	public void onSave() {
		TParm result = new TParm();
		// 请领使用单位
		if (getRadioButton("UNIT_TYPE_0").isSelected())
			result.setData("UNIT_TYPE", "0");
		else
			result.setData("UNIT_TYPE", "1");
		// 自动拨补方式
		if (getRadioButton("FIXEDAMOUNT_0").isSelected())
			result.setData("FIXEDAMOUNT_FLG", "0");
		else if (getRadioButton("FIXEDAMOUNT_1").isSelected())
			result.setData("FIXEDAMOUNT_FLG", "1");
		else
			result.setData("FIXEDAMOUNT_FLG", "2");
		// 自动拨补数量
		if (getRadioButton("AUTO_FILL_TYPE_0").isSelected())
			result.setData("AUTO_FILL_TYPE", "1");
		else
			result.setData("AUTO_FILL_TYPE", "2");

		// <-- 新增3个字段的维护 update by shendr date:2013.5.24
		result.setData("IS_AUTO_DRUG", Integer.valueOf(getCheckBox(
				"IS_AUTO_DRUG").getValue() != "Y" ? 0 : 1));
		result.setData("TOXIC_LENGTH", getValueString("TOXIC_LENGTH"));
		result.setData("TOXBOX_LENGTH", getValueString("TOXBOX_LENGTH"));
		// -->

		// 购入单价自动维护
		String flg = "Y";
		flg = getCheckBox("REUPRICE").getValue();
		result.setData("REUPRICE_FLG", flg);
		// 出库即入库注记
		flg = getCheckBox("DISCHECK").getValue();
		result.setData("DISCHECK_FLG", flg);
		// 草药自动维护零售价
		flg = getCheckBox("GPRICE").getValue();
		result.setData("GPRICE_FLG", flg);
		double price = 0.0D;
		// 草药成本加成比例(零售价)
		price = TypeTool.getDouble(getNumberTextField("GOWN_COSTRATE")
				.getText());
		result.setData("GOWN_COSTRATE", Double.valueOf(price));
		// 草药成本加成比例(医保价)
		price = TypeTool.getDouble(getNumberTextField("GNHI_COSTRATE")
				.getText());
		result.setData("GNHI_COSTRATE", Double.valueOf(price));
		// 草药成本加成比例(政府最高价)
		price = TypeTool
				.getDouble(getNumberTextField("GOV_COSTRATE").getText());
		result.setData("GOV_COSTRATE", Double.valueOf(price));
		// 草药零售价自动更新为新自费价额注记
		flg = getCheckBox("UPDATE_GRETAIL").getValue();
		result.setData("UPDATE_GRETAIL_FLG", flg);
		// 西药成本加成比例(零售价)
		price = TypeTool.getDouble(getNumberTextField("WOWN_COSTRATE")
				.getText());
		result.setData("WOWN_COSTRATE", Double.valueOf(price));
		// 西药成本加成比例(医保价)
		price = TypeTool.getDouble(getNumberTextField("WNHI_COSTRATE")
				.getText());
		result.setData("WNHI_COSTRATE", Double.valueOf(price));
		// 西药成本加成比例(政府最高价)
		price = TypeTool.getDouble(getNumberTextField("WGOV_COSTRATE")
				.getText());
		result.setData("WGOV_COSTRATE", Double.valueOf(price));
		// 西零售价自动更新为新自费价额注记
		flg = getCheckBox("UPDATE_WRETAIL_FLG").getValue();
		result.setData("UPDATE_WRETAIL_FLG", flg);
		// 日结状态
		if (getRadioButton("MANUAL_TYPE_0").isSelected())
			result.setData("MANUAL_TYPE", "0");
		else if (getRadioButton("MANUAL_TYPE_1").isSelected())
			result.setData("MANUAL_TYPE", "1");
		else
			result.setData("MANUAL_TYPE", "2");
		// 药品零差价
		result.setData("PHA_PRICE_FLG", getValueString("PHA_PRICE_FLG"));
		// 麻精验收入库存放部门 TOXIC_STORAGE_ORG
		result
				.setData("TOXIC_STORAGE_ORG",
						getValueString("TOXIC_STORAGE_ORG"));
		// OPT_USER,OPT_DATE,OPT_TERM
		Timestamp date = StringTool.getTimestamp(new Date());
		result.setData("OPT_USER", Operator.getID());
		result.setData("OPT_DATE", date);
		result.setData("OPT_TERM", Operator.getIP());
		result.setData("MAIN_SUP_CODE", this.getValueString("MAIN_SUP_CODE"));//20150611 wangjc add 主供应商
		// 保存数据
		TParm parm = new TParm();
		if ("insert".equals(operation))
			parm = IndSysParmTool.getInstance().onInsert(result);
		else
			parm = IndSysParmTool.getInstance().onUpdate(result);
		if (parm.getErrCode() < 0) {
			messageBox("E0001");
			return;
		} else {
			messageBox("P0001");
			operation = "update";
			return;
		}
	}

	/**
	 * 批次解锁
	 */
	public void onUnLock() {
		openDialog("%ROOT%\\config\\spc\\INDBatchLock.x");
	}

	/**
	 * 草药自动维护零售价(CheckBox)改变事件
	 */
	public void onSelectGprice() {
		if (getCheckBox("GPRICE").isSelected()) {
			getNumberTextField("GOWN_COSTRATE").setEnabled(true);
			getNumberTextField("GNHI_COSTRATE").setEnabled(true);
			getNumberTextField("GOV_COSTRATE").setEnabled(true);
		} else {
			getNumberTextField("GOWN_COSTRATE").setEnabled(false);
			getNumberTextField("GNHI_COSTRATE").setEnabled(false);
			getNumberTextField("GOV_COSTRATE").setEnabled(false);
		}
	}

	/**
	 * 初始画面数据
	 */
	private void initPage() {
		TParm result = new TParm(TJDODBTool.getInstance().select(
				INDSQL.getINDSysParm()));
		if (result.getCount() > 0) {
			// 请领使用单位
			switch (result.getInt("UNIT_TYPE", 0)) {
			case 0: // '\0'
				getRadioButton("UNIT_TYPE_0").setSelected(true);
				break;

			case 1: // '\001'
				getRadioButton("UNIT_TYPE_1").setSelected(true);
				break;
			}
			// 自动拨补方式
			switch (result.getInt("FIXEDAMOUNT_FLG", 0)) {
			case 0: // '\0'
				getRadioButton("FIXEDAMOUNT_0").setSelected(true);
				break;

			case 1: // '\001'
				getRadioButton("FIXEDAMOUNT_1").setSelected(true);
				break;

			case 2: // '\002'
				getRadioButton("FIXEDAMOUNT_2").setSelected(true);
				break;
			}
			// 自动拨补数量
			switch (result.getInt("AUTO_FILL_TYPE", 0)) {
			case 1: // '\001'
				getRadioButton("AUTO_FILL_TYPE_0").setSelected(true);
				break;

			case 2: // '\002'
				getRadioButton("AUTO_FILL_TYPE_1").setSelected(true);
				break;
			}
			// <-- 新增3个字段的维护 update by shendr date:2013.5.24
			if ("1".equals(result.getValue("IS_AUTO_DRUG", 0)))
				getCheckBox("IS_AUTO_DRUG").setSelected(true);
			else
				getCheckBox("IS_AUTO_DRUG").setSelected(false);
			getTTextField("TOXIC_LENGTH").setValue(
					result.getValue("TOXIC_LENGTH", 0));
			getTTextField("TOXBOX_LENGTH").setValue(
					result.getValue("TOXBOX_LENGTH", 0));
			// -->
			// 购入单价自动维护
			if ("Y".equals(result.getValue("REUPRICE_FLG", 0)))
				getCheckBox("REUPRICE").setSelected(true);
			else
				getCheckBox("REUPRICE").setSelected(false);
			// 出库即入库注记
			if ("Y".equals(result.getValue("DISCHECK_FLG", 0)))
				getCheckBox("DISCHECK").setSelected(true);
			else
				getCheckBox("DISCHECK").setSelected(false);
			// 草药自动维护零售价
			if ("Y".equals(result.getValue("GPRICE_FLG", 0)))
				getCheckBox("GPRICE").setSelected(true);
			else
				getCheckBox("GPRICE").setSelected(false);
			// 草药成本加成比例(零售价)
			getNumberTextField("GOWN_COSTRATE").setValue(
					Double.valueOf(result.getDouble("GOWN_COSTRATE", 0)));
			// 草药成本加成比例(医保价)
			getNumberTextField("GNHI_COSTRATE").setValue(
					Double.valueOf(result.getDouble("GNHI_COSTRATE", 0)));
			// 草药成本加成比例(政府最高价)
			getNumberTextField("GOV_COSTRATE").setValue(
					Double.valueOf(result.getDouble("GOV_COSTRATE", 0)));
			// 草药零售价自动更新为新自费价额注记
			if ("Y".equals(result.getValue("UPDATE_GRETAIL_FLG", 0)))
				getCheckBox("UPDATE_GRETAIL").setSelected(true);
			else
				getCheckBox("UPDATE_GRETAIL").setSelected(false);
			// 西药成本加成比例(零售价)
			getNumberTextField("WOWN_COSTRATE").setValue(
					Double.valueOf(result.getDouble("WOWN_COSTRATE", 0)));
			// 西药成本加成比例(医保价)
			getNumberTextField("WNHI_COSTRATE").setValue(
					Double.valueOf(result.getDouble("WNHI_COSTRATE", 0)));
			// 西药成本加成比例(政府最高价)
			getNumberTextField("WGOV_COSTRATE").setValue(
					Double.valueOf(result.getDouble("WGOV_COSTRATE", 0)));
			// 西零售价自动更新为新自费价额注记
			if ("Y".equals(result.getValue("UPDATE_WRETAIL_FLG", 0)))
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(true);
			else
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(false);
			// 日结状态
			switch (result.getInt("MANUAL_TYPE", 0)) {
			case 0: // '\0'
				getRadioButton("MANUAL_TYPE_0").setSelected(true);
				break;

			case 1: // '\001'
				getRadioButton("MANUAL_TYPE_1").setSelected(true);
				break;

			case 2: // '\002'
				getRadioButton("MANUAL_TYPE_2").setSelected(true);
				break;
			}
			// 药品零差价
			setValue("PHA_PRICE_FLG", result.getValue("PHA_PRICE_FLG", 0));
			setValue("TOXIC_STORAGE_ORG", result.getValue("TOXIC_STORAGE_ORG",
					0));
			this.setValue("MAIN_SUP_CODE", result.getValue("MAIN_SUP_CODE", 0));//20150611 wangjc add 主供应商
			// 草药自动维护零售价(CheckBox)改变事件
			onSelectGprice();
			operation = "update";
		}
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
	 * 得到NumberTextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TNumberTextField getNumberTextField(String tagName) {
		return (TNumberTextField) getComponent(tagName);
	}

	/**
	 * 得到TTextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}

}