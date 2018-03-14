package com.javahis.ui.udd;

import java.sql.Timestamp;

import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: 药品条码核对
 * </p>
 * 
 * <p>
 * Description: 药品条码核对
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
 * @author wangb 2015.12.7
 * @version 1.0
 */
public class UDDDrugTransferControl extends TControl {
	
	private TTable table;
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
    	// 表格点选事件
		this.callFunction("UI|TABLE|addEventListener", "TABLE->"
				+ TTableEvent.CLICKED, this, "onTableClicked");
		this.onInitPage();
    }
    
    /**
	 * 初始化页面
	 */
	public void onInitPage() {
		table.setParmValue(new TParm());
		clearValue("TRANS_NO;DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;TRANSFER_USER");
		getRadioButton("TRANSFER_N").setSelected(true);
		getCheckBox("CHECK_ALL").setSelected(true);
		Timestamp now = SystemTool.getInstance().getDate();
		Timestamp yes = StringTool.rollDate(now, -1);
		setValue("START_DATE", yes);
		setValue("END_DATE", now);
		grabFocus("TRANS_NO");
	}
	
	/**
	 * 查询
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		this.refreshQueryData("Y");
	}
	
	/**
	 * 刷新表格数据
	 */
	private void refreshQueryData(String noDataMsg) {
		clearValue("MR_NO;PAT_NAME;TRANSFER_USER");
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		
		if (StringUtils.isEmpty(startDate) || StringUtils.isEmpty(endDate)) {
			this.messageBox("调配时间不可为空");
			return;
		} else {
			startDate = startDate.substring(0,10);
			endDate = endDate.substring(0,10);
		}
		
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("SELECT 'Y' AS SEL_FLG,A.TRANSFER_FLG,A.PHA_DISPENSE_NO,B.DEPT_CODE,B.STATION_CODE,B.BED_NO,");
		sbSql.append("C.PAT_NAME,B.MR_NO,B.IPD_NO,B.PHA_DOSAGE_CODE,B.PHA_DOSAGE_DATE,B.ORDER_CODE,B.ORDER_DESC,A.DOSAGE_QTY,");
		sbSql.append("A.DOSAGE_UNIT,B.OWN_PRICE,A.DOSAGE_QTY * B.OWN_PRICE AS SUM_PRICE,A.TRANSFER_USER,A.TRANSFER_DATE,");
		sbSql.append("A.CASE_NO,A.ORDER_NO,A.ORDER_SEQ,A.ORDER_DATE,A.ORDER_DATETIME ");
		sbSql.append(" FROM ODI_DSPND A, ODI_DSPNM B, SYS_PATINFO C ");
		sbSql.append(" WHERE A.CASE_NO = B.CASE_NO AND A.ORDER_NO = B.ORDER_NO AND A.ORDER_SEQ = B.ORDER_SEQ ");
		sbSql.append(" AND A.PHA_DISPENSE_NO = B.PHA_DISPENSE_NO AND B.MR_NO = C.MR_NO AND B.CAT1_TYPE = 'PHA' AND B.DSPN_KIND = 'ST' ");
		sbSql.append(" AND B.PHA_DOSAGE_DATE BETWEEN TO_DATE ('");
		sbSql.append(startDate);
		sbSql.append(" 00:00:00','YYYY/MM/DD HH24:MI:SS') AND TO_DATE ('");
		sbSql.append(endDate);
		sbSql.append(" 23:59:59','YYYY/MM/DD HH24:MI:SS') ");
		
		if (StringUtils.isNotEmpty(this.getValueString("DEPT_CODE"))) {
			sbSql.append(" AND B.DEPT_CODE = '");
			sbSql.append(this.getValueString("DEPT_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(this.getValueString("STATION_CODE"))) {
			sbSql.append(" AND B.STATION_CODE = '");
			sbSql.append(this.getValueString("STATION_CODE"));
			sbSql.append("' ");
		}
		
		if (StringUtils.isNotEmpty(this.getValueString("TRANS_NO"))) {
			sbSql.append(" AND B.PHA_DISPENSE_NO = '");
			sbSql.append(this.getValueString("TRANS_NO"));
			sbSql.append("' ");
		}
		
		if (getRadioButton("TRANSFER_N").isSelected()) {
			sbSql.append(" AND (A.TRANSFER_FLG IS NULL OR A.TRANSFER_FLG <> 'Y' )");
		} else if (getRadioButton("TRANSFER_Y").isSelected()) {
			sbSql.append(" AND A.TRANSFER_FLG = 'Y'");
		}
		
		TParm result = new TParm(TJDODBTool.getInstance().select(sbSql.toString()));
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrText());
			table.setParmValue(new TParm());
			this.messageBox("查询数据错误");
			return;
		}
		
		if (StringUtils.equals("Y", noDataMsg)) {
			if (result.getCount() < 1) {
				table.setParmValue(new TParm());
				this.messageBox("查无数据");
				return;
			}
		}
		
		table.setParmValue(result);
	}
	
	/**
	 * 保存
	 */
	public void onSave() {
		TParm tableParm = table.getParmValue();
		if (tableParm == null || tableParm.getCount() < 1) {
			this.messageBox("无保存数据");
			return;
		}
		int count = tableParm.getCount();
		
		// 强制失去编辑焦点
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		if (!tableParm.getValue("SEL_FLG").contains("Y")) {
			this.messageBox("请勾选需要交接的数据项");
			return;
		}
		
		String type = "nurseTransfer";
		TParm inParm = (TParm) this.openDialog(
				"%ROOT%\\config\\inw\\passWordCheck.x", type);
		String OK = inParm.getValue("RESULT");
		String user = inParm.getValue("USER_ID");
		if (!OK.equals("OK")) {
			return;
		}
		
		TParm parm = new TParm();
		TParm result = new TParm();
		int selCount = 0;
		int successTransCount = 0;
		for (int i = 0; i < count; i++) {
			if (StringUtils.equals("Y", tableParm.getValue("SEL_FLG", i))) {
				selCount++;
				parm = tableParm.getRow(i);
				parm.setData("TRANSFER_USER", user);
				result = this.updateTransData(parm);
				if (result.getErrCode() < 0) {
					err("ERR:" + result.getErrText());
				} else {
					successTransCount++;
				}
			}
		}
		
		if (successTransCount == selCount) {
			this.messageBox("保存成功");
		} else {
			this.messageBox("保存失败");
		}
		this.refreshQueryData("N");
	}
	
	/**
	 * 交接单号回车
	 */
	public void onTransferNoEnter() {
		this.onQuery();
	}
	
	/**
	 * 全选
	 */
	public void onCheckAll() {
		boolean checkFlg = getCheckBox("CHECK_ALL").isSelected();
		int count = table.getParmValue().getCount();
		if (count < 1) {
			return;
		} else {
			for (int i = 0; i < count; i++) {
				table.setItem(i, "SEL_FLG", checkFlg);
			}
		}
	}
	
	/**
	 * 交接状态切换
	 */
	public void onTransRadioChange() {
		table.setParmValue(new TParm());
		if (getRadioButton("TRANSFER_ALL").isSelected()
				|| getRadioButton("TRANSFER_Y").isSelected()) {
			callFunction("UI|save|enabled", false);
		} else {
			callFunction("UI|save|enabled", true);
		}
	}
	
	/**
	 * 添加对tablePat的监听事件
	 * 
	 * @param row
	 */
	public void onTableClicked(int row) {
		if (row < 0) {
			return;
		}
		clearValue("TRANS_NO;DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;TRANSFER_USER");
		TParm parm = table.getParmValue().getRow(row);
		setValueForParm("DEPT_CODE;STATION_CODE;MR_NO;PAT_NAME;TRANSFER_USER", parm);
		setValue("TRANS_NO", parm.getValue("PHA_DISPENSE_NO"));
	}
	
	/**
	 * 清空
	 */
	public void onClear() {
		this.onInitPage();
	}
	
	/**
	 * 交接保存更新数据信息
	 */
	private TParm updateTransData(TParm parm) {
		StringBuffer sbSql = new StringBuffer();
		sbSql.append("UPDATE ODI_DSPND SET TRANSFER_FLG='Y',TRANSFER_USER='");
		sbSql.append(parm.getValue("TRANSFER_USER"));
		sbSql.append("',TRANSFER_DATE=SYSDATE WHERE CASE_NO = '");
		sbSql.append(parm.getValue("CASE_NO"));
		sbSql.append("' AND ORDER_NO = '");
		sbSql.append(parm.getValue("ORDER_NO"));
		sbSql.append("' AND ORDER_SEQ = ");
		sbSql.append(parm.getValue("ORDER_SEQ"));
		sbSql.append(" AND ORDER_DATE = '");
		sbSql.append(parm.getValue("ORDER_DATE"));
		sbSql.append("' AND ORDER_DATETIME = '");
		sbSql.append(parm.getValue("ORDER_DATETIME"));
		sbSql.append("' ");
		
		TParm result = new TParm(TJDODBTool.getInstance().update(
				sbSql.toString()));
		return result;
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
	 * 得到CheckBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
