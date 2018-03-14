package com.javahis.ui.ind;

import java.sql.Timestamp;
import java.util.Date;

import jdo.ind.INDSQL;
import jdo.ind.IndSysParmTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TNumberTextField;
import com.dongyang.ui.TRadioButton;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;

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

		String flg = "Y";
		// 购入单价自动维护
		flg = getCheckBox("REUPRICE").getValue();
		result.setData("REUPRICE_FLG", flg);

		// 出库即入库注记
		flg = getCheckBox("DISCHECK").getValue();
		result.setData("DISCHECK_FLG", flg);

		// 草药自动维护零售价
		flg = getCheckBox("GPRICE").getValue();
		result.setData("GPRICE_FLG", flg);

		double price = 0.00;
		// 草药成本加成比例(零售价)
		price = TypeTool.getDouble(getNumberTextField("GOWN_COSTRATE")
				.getText());
		result.setData("GOWN_COSTRATE", price);

		// 草药成本加成比例(医保价)
		price = TypeTool.getDouble(getNumberTextField("GNHI_COSTRATE")
				.getText());
		result.setData("GNHI_COSTRATE", price);

		// 草药成本加成比例(政府最高价)
		price = TypeTool
				.getDouble(getNumberTextField("GOV_COSTRATE").getText());
		result.setData("GOV_COSTRATE", price);

		// 草药零售价自动更新为新自费价额注记
		flg = getCheckBox("UPDATE_GRETAIL").getValue();
		result.setData("UPDATE_GRETAIL_FLG", flg);

		// 西药成本加成比例(零售价)
		price = TypeTool.getDouble(getNumberTextField("WOWN_COSTRATE")
				.getText());
		result.setData("WOWN_COSTRATE", price);

		// 西药成本加成比例(医保价)
		price = TypeTool.getDouble(getNumberTextField("WNHI_COSTRATE")
				.getText());
		result.setData("WNHI_COSTRATE", price);

		// 西药成本加成比例(政府最高价)
		price = TypeTool.getDouble(getNumberTextField("WGOV_COSTRATE")
				.getText());
		result.setData("WGOV_COSTRATE", price);

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
                result.setData("PHA_PRICE_FLG", this.getValueString("PHA_PRICE_FLG"));

		// OPT_USER,OPT_DATE,OPT_TERM
		Timestamp date = StringTool.getTimestamp(new Date());
		result.setData("OPT_USER", Operator.getID());
		result.setData("OPT_DATE", date);
		result.setData("OPT_TERM", Operator.getIP());

		// 保存数据
		TParm parm = new TParm();
		if ("insert".equals(operation))
			parm = IndSysParmTool.getInstance().onInsert(result);
		else
			parm = IndSysParmTool.getInstance().onUpdate(result);
		if (parm.getErrCode() < 0) {
			this.messageBox("E0001");
			return;
		}
		this.messageBox("P0001");
                operation = "update";
	}

	/**
	 * 批次解锁
	 */
	public void onUnLock() {
		this.openDialog("%ROOT%\\config\\ind\\INDBatchLock.x");
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
			case 0:
				getRadioButton("UNIT_TYPE_0").setSelected(true);
				break;
			case 1:
				getRadioButton("UNIT_TYPE_1").setSelected(true);
				break;
			}

			// 自动拨补方式
			switch (result.getInt("FIXEDAMOUNT_FLG", 0)) {
			case 0:
				getRadioButton("FIXEDAMOUNT_0").setSelected(true);
				break;
			case 1:
				getRadioButton("FIXEDAMOUNT_1").setSelected(true);
				break;
			case 2:
				getRadioButton("FIXEDAMOUNT_2").setSelected(true);
				break;
			}

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
					result.getDouble("GOWN_COSTRATE", 0));

			// 草药成本加成比例(医保价)
			getNumberTextField("GNHI_COSTRATE").setValue(
					result.getDouble("GNHI_COSTRATE", 0));

			// 草药成本加成比例(政府最高价)
			getNumberTextField("GOV_COSTRATE").setValue(
					result.getDouble("GOV_COSTRATE", 0));

			// 草药零售价自动更新为新自费价额注记
			if ("Y".equals(result.getValue("UPDATE_GRETAIL_FLG", 0)))
				getCheckBox("UPDATE_GRETAIL").setSelected(true);
			else
				getCheckBox("UPDATE_GRETAIL").setSelected(false);

			// 西药成本加成比例(零售价)
			getNumberTextField("WOWN_COSTRATE").setValue(
					result.getDouble("WOWN_COSTRATE", 0));

			// 西药成本加成比例(医保价)
			getNumberTextField("WNHI_COSTRATE").setValue(
					result.getDouble("WNHI_COSTRATE", 0));

			// 西药成本加成比例(政府最高价)
			getNumberTextField("WGOV_COSTRATE").setValue(
					result.getDouble("WGOV_COSTRATE", 0));

			// 西零售价自动更新为新自费价额注记
			if ("Y".equals(result.getValue("UPDATE_WRETAIL_FLG", 0)))
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(true);
			else
				getCheckBox("UPDATE_WRETAIL_FLG").setSelected(false);

			// 日结状态
			switch (result.getInt("MANUAL_TYPE", 0)) {
			case 0:
				getRadioButton("MANUAL_TYPE_0").setSelected(true);
				break;
			case 1:
				getRadioButton("MANUAL_TYPE_1").setSelected(true);
				break;
			case 2:
				getRadioButton("MANUAL_TYPE_2").setSelected(true);
				break;
			}
                        // 药品零差价
                        this.setValue("PHA_PRICE_FLG",
                                      result.getValue("PHA_PRICE_FLG", 0));

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
}
