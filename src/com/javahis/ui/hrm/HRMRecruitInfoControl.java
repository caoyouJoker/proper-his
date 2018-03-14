package com.javahis.ui.hrm;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: 受试者招募表信息
 * </p>
 * 
 * <p>
 * Description: 受试者招募表信息
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2016
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2016.7.4
 * @version 1.0
 */
public class HRMRecruitInfoControl extends TControl {

	private TTable table;
	
	 /**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
		this.onClear();
    }
    
	/**
	 * 查询
	 */
	public void onQuery() {
		String sql = "SELECT 'N' AS FLG,A.* FROM HRM_RECRUIT A WHERE 1 = 1 ";
		
		// 方案名称
		String contractCode = this.getValueString("CONTRACT_CODE");
		if (StringUtils.isNotEmpty(contractCode)) {
			sql = sql + " AND CONTRACT_CODE = '" + contractCode + "'";
		}
		
		String patName = this.getValueString("PAT_NAME").trim();
		if (StringUtils.isNotEmpty(patName)) {
			sql = sql + " AND PAT_NAME LIKE '%" + patName + "%'";
		}
		
		if (getRadioButton("SEX_M").isSelected()) {
			sql = sql + " AND SEX_CODE = '1' ";
		} else if (getRadioButton("SEX_F").isSelected()) {
			sql = sql + " AND SEX_CODE = '2' ";
		}
		
		sql = sql + " ORDER BY SEQ ";
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		if (result.getErrCode() < 0) {
			this.messageBox("查询受试者招募信息错误");
			err(result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("查无数据");
		}
		table.setParmValue(result);
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		clearValue("PAT_NAME;CONTRACT_CODE");
		getRadioButton("SEX_ALL").setSelected(true);
		table.setParmValue(new TParm());
	}
	
	/**
	 * 全选复选框选中事件
	 */
	public void onCheckAll() {
		if (table.getRowCount() <= 0) {
			getCheckBox("CHECK_ALL").setSelected(false);
			return;
		}
		
		String flg = "N";
		if (getCheckBox("CHECK_ALL").isSelected()) {
			flg = "Y";
		}
		
		for (int i = 0; i < table.getRowCount(); i++) {
			table.setItem(i, "FLG", flg);
		}
	}
	
	/**
	 * 传回
	 */
	public void onReturn() {
		TParm selectedParm = table.getParmValue();
		
		// 强制失去编辑焦点
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		int count = selectedParm.getCount();
		if (!selectedParm.getValue("FLG").contains("Y")) {
			this.messageBox("请勾选回传数据");
			return;
		}
		
		for (int i = count - 1; i > -1; i--) {
			if (!"Y".equals(selectedParm.getValue("FLG", i))) {
				selectedParm.removeRow(i);
			}
		}
		
		this.setReturnValue(selectedParm);
		this.closeWindow();
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
	 * 得到getCheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
