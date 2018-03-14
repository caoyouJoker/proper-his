package com.javahis.ui.udd;

import java.sql.Timestamp;
import java.text.DecimalFormat;

import jdo.sys.SystemTool;
import jdo.udd.UDDReturnQueryTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 住院药房退药统计
 * </p>
 * 
 * <p>
 * Description: 住院药房退药统计
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2009
 * </p>
 * 
 * <p>
 * Company:
 * </p>
 * 
 * @author fuwj 2009.09.22
 * @version 1.0
 */
public class UDDReturnQueryControl extends TControl {

	private TTable TABLE_M;
	private TTable TABLE_D;
	private double masterAmt;
	private double detailAmt;
	private String mAmt ="0";
	private String dAmt="0";

	public UDDReturnQueryControl() {
	}

	/**
	 * 初始化方法
	 */
	public void onInit() {
		TABLE_M = this.getTable("TABLE_M");
		TABLE_D = this.getTable("TABLE_D");
		Timestamp date = SystemTool.getInstance().getDate();
		this.setValue("END_DATE", date.toString().substring(0, 10).replace('-',
				'/')
				+ " 23:59:59");
		this.setValue("START_DATE", StringTool.rollDate(date, -7).toString()
				.substring(0, 10).replace('-', '/')
				+ " 00:00:00");
	}

	/**
	 * 类型变更事件
	 */
	public void onChangeInfoType() {
		if (getRadioButton("RADIO_M").isSelected()) {
			TABLE_M.setVisible(true);
			TABLE_D.setVisible(false);
			this.setValue("AMT", mAmt);	
		} else if (getRadioButton("RADIO_D").isSelected()) {
			TABLE_D.setVisible(true);
			TABLE_M.setVisible(false);
			this.setValue("AMT", dAmt);	
		}
	}

	public void onQuery() {
		masterAmt=0.0;
		detailAmt=0.0;
		TParm result = new TParm();
		String orgCode = this.getValueString("ORG_CODE");
		String dr_code = this.getValueString("VS_DR_CODE");
		TParm searchParm = new TParm();
		searchParm.setData("ORG_CODE", orgCode);
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		searchParm.setData("START_DATE", startDate);
		searchParm.setData("END_DATE", endDate);
		searchParm.setData("VS_DR_CODE",dr_code);
		// if (getRadioButton("RADIO_M").isSelected()) {
		result = UDDReturnQueryTool.getInstance().onQueryMaster(searchParm);
		if (result == null || result.getErrCode() < 0 || result.getCount() <= 0) {
			this.messageBox("无查询数据");
			return;
		}
		TABLE_M.setParmValue(result);
		for(int i=0;i<result.getCount();i++) {
			masterAmt = masterAmt+result.getDouble("AMT",i);
		}
		 DecimalFormat df = new DecimalFormat("0.00");
		 mAmt = String.valueOf(df.format(masterAmt));		
		// }
		result = UDDReturnQueryTool.getInstance().onQueryDetail(searchParm);
		if (result == null || result.getErrCode() < 0 || result.getCount() <= 0) {
			this.messageBox("无查询数据");
			return;
		}
		for (int i=0;i<result.getCount();i++) {
			detailAmt = detailAmt+result.getDouble("AMT",i);				
		}
		dAmt = String.valueOf(df.format(detailAmt));
		TABLE_D.setParmValue(result);	
		if (getRadioButton("RADIO_M").isSelected()) {
			this.setValue("AMT", mAmt);	
		} else if (getRadioButton("RADIO_D").isSelected()) {
			this.setValue("AMT", dAmt);	
		}
	}
	
	public void onClear() {
		TABLE_D.removeRowAll();
		TABLE_M.removeRowAll();
		this.setValue("ORG_CODE", "");	
		this.setValue("VS_DR_CODE", "");	
	}

	/**
	 * 得到Table对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTable getTable(String tagName) {
		return (TTable) getComponent(tagName);
	}

	/**
	 * 得到CheckBox对象
	 * 
	 * @return TCheckBox
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}

	/**
	 * 得到TextField对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
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
	 * 得到TextFormat对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
	}
	
	
}
