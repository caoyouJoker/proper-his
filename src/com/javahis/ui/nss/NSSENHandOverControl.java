package com.javahis.ui.nss;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTableNode;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>
 * Title: 肠内营养交接
 * </p>
 * 
 * <p>
 * Description: 肠内营养交接
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2015
 * </p>
 * 
 * <p>
 * Company: Javahis
 * </p>
 * 
 * @author wangb 2015.9.6
 * @version 1.0
 */
public class NSSENHandOverControl extends TControl {
	
	private TTable table;
	
	 /**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
		this.onInitPage();
		// 输入交接备注响应事件
		this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		
    }
    
    /**
	 * 初始化页面
	 */
	public void onInitPage() {
		clearValue("EN_BAR_CODE");
		table.setParmValue(new TParm());
		getRadioButton("STATUS_N").setSelected(true);
    	// 取得当前日期
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
    	this.setValue("PREPARE_DATE", todayDate);
    	this.setValue("DEPT_CODE", Operator.getDept());
    	this.setValue("STATION_CODE", Operator.getStation());
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		TParm queryParm = this.getQueryParm();
		if (queryParm.getErrCode() < 0) {
			return;
		}
		
		// 查询肠内营养交接数据
		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENHandOverData(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询交接数据错误");
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
			return;
		}
		
		if (result.getCount() < 1) {
			this.messageBox("查无数据");
			return;
		}
		
		table.setParmValue(result);
		
		this.grabFocus("EN_BAR_CODE");
	}
	
	/**
	 * 保存
	 */
	public void onSave() {
		if (table == null || table.getRowCount() < 1) {
			this.messageBox("无保存数据");
			return;
		}
		
		// 强制失去编辑焦点
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}

		TParm parm = table.getParmValue();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			if (parm.getInt("LABEL_QTY", i) != parm.getInt(
					"ACTUAL_HAND_OVER_QTY", i)) {
				if (StringUtils.isEmpty(parm.getValue("HAND_OVER_REMARK", i))) {
					this.messageBox("病人【" + parm.getValue("PAT_NAME", i)
							+ "】应交接数与实际交接数不符，请填写交接备注");
					table.getTable().grabFocus(); 
					table.setSelectedRow(i);
					table.setSelectedColumn(8);
					return;
				}
			}
		}
		
		// 执行批量保存
		TParm result = TIOM_AppServer.executeAction(
				"action.nss.NSSEnteralNutritionAction", "onSaveByENHandOver",
				parm);
		
		if (result.getErrCode() < 0) {
			err(result.getErrCode() + " " + result.getErrText());
			this.messageBox("E0005");
			return;
		}
		
		this.messageBox("P0001");
		this.onQuery();
	}
	
	/**
	 * 切换交接状态响应事件
	 */
	public void onChangeStatus() {
		// 完成状态
		if (getRadioButton("STATUS_N").isSelected()) {
			this.callFunction("UI|save|Enabled", true);
			table.setLockColumns("0,1,2,3,4,5,6,7,9,10");
		} else {
			this.callFunction("UI|save|Enabled", false);
			table.setLockColumns("all");
		}
		
		this.onQuery();
	}
	
	/**
	 * 扫描条码号
	 */
	public void onENBarCodeEnter() {
		String barCode = this.getValueString("EN_BAR_CODE");
		
		if (StringUtils.isEmpty(barCode.trim())) {
			return;
		}
		
		TParm parm = table.getParmValue();
		int count = parm.getCount();;
		int actualCount = 0;
		boolean flag = false;
		
		for (int i = 0; i < count; i++) {
			if (StringUtils.equals(parm.getValue("EN_PREPARE_NO", i), barCode)) {
				// 实际交接数量
				actualCount = parm.getInt("ACTUAL_HAND_OVER_QTY", i) + 1;
				table.setItem(i, "ACTUAL_HAND_OVER_QTY", actualCount);
				
				// 交接人
				if (StringUtils.isEmpty(parm.getValue("HAND_OVER_USER", i))) {
					table.setItem(i, "HAND_OVER_USER", Operator.getID());
				}

				// 交接日期
				if (StringUtils.isEmpty(parm.getValue("HAND_OVER_DATE", i))) {
					table.setItem(i, "HAND_OVER_DATE", SystemTool.getInstance()
							.getDate());
				}
				
				flag = true;
			}
		}
		
		if (!flag) {
			this.messageBox("该条码号不存在");
		}
		
		this.clearValue("EN_BAR_CODE");
		this.grabFocus("EN_BAR_CODE");
	}
	
	/**
	 * 获取查询条件数据
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		if (StringUtils.isEmpty(this.getValueString("PREPARE_DATE"))) {
			this.messageBox("请输入应交接时间");
			parm.setErrCode(-1);
			return parm;
		} else {
			parm.setData("PREPARE_DATE", this.getValueString("PREPARE_DATE")
					.substring(0, 10).replaceAll("-", ""));
		}
		// 科室
		parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
		// 病区
		parm.setData("STATION_CODE", getValueString("STATION_CODE"));
		
		// 完成状态
		if (getRadioButton("STATUS_N").isSelected()) {
			parm.setData("HAND_OVER_STATUS", "N");
		} else {
			parm.setData("HAND_OVER_STATUS", "Y");
		}
		
		return parm;
	}
	
	/**
	 * 输入交接备注响应事件
	 */
	public void onTableChangeValue(Object obj) {
		// 值改变的单元格
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return;
		}
		// 判断数据改变
		if (node.getValue().equals(node.getOldValue())) {
			return;
		}
		
		TParm parm = table.getParmValue();
		int row = node.getRow();
		
		// 交接人
		if (StringUtils.isEmpty(parm.getValue("HAND_OVER_USER", row))) {
			table.setItem(row, "HAND_OVER_USER", Operator.getID());
		}

		// 交接日期
		if (StringUtils.isEmpty(parm.getValue("HAND_OVER_DATE", row))) {
			table.setItem(row, "HAND_OVER_DATE", SystemTool.getInstance()
					.getDate());
		}
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.onInitPage();
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
}
