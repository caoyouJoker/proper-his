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
 * Title: ����Ӫ������
 * </p>
 * 
 * <p>
 * Description: ����Ӫ������
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
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
		this.onInitPage();
		// ���뽻�ӱ�ע��Ӧ�¼�
		this.addEventListener("TABLE->" + TTableEvent.CHANGE_VALUE,
				"onTableChangeValue");
		
    }
    
    /**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		clearValue("EN_BAR_CODE");
		table.setParmValue(new TParm());
		getRadioButton("STATUS_N").setSelected(true);
    	// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
    	this.setValue("PREPARE_DATE", todayDate);
    	this.setValue("DEPT_CODE", Operator.getDept());
    	this.setValue("STATION_CODE", Operator.getStation());
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		TParm queryParm = this.getQueryParm();
		if (queryParm.getErrCode() < 0) {
			return;
		}
		
		// ��ѯ����Ӫ����������
		TParm result = NSSEnteralNutritionTool.getInstance()
				.queryENHandOverData(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�������ݴ���");
			err("ERR:" + result.getErrCode() + result.getErrText()
	                + result.getErrName());
			return;
		}
		
		if (result.getCount() < 1) {
			this.messageBox("��������");
			return;
		}
		
		table.setParmValue(result);
		
		this.grabFocus("EN_BAR_CODE");
	}
	
	/**
	 * ����
	 */
	public void onSave() {
		if (table == null || table.getRowCount() < 1) {
			this.messageBox("�ޱ�������");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}

		TParm parm = table.getParmValue();
		int count = parm.getCount();
		for (int i = 0; i < count; i++) {
			if (parm.getInt("LABEL_QTY", i) != parm.getInt(
					"ACTUAL_HAND_OVER_QTY", i)) {
				if (StringUtils.isEmpty(parm.getValue("HAND_OVER_REMARK", i))) {
					this.messageBox("���ˡ�" + parm.getValue("PAT_NAME", i)
							+ "��Ӧ��������ʵ�ʽ���������������д���ӱ�ע");
					table.getTable().grabFocus(); 
					table.setSelectedRow(i);
					table.setSelectedColumn(8);
					return;
				}
			}
		}
		
		// ִ����������
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
	 * �л�����״̬��Ӧ�¼�
	 */
	public void onChangeStatus() {
		// ���״̬
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
	 * ɨ�������
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
				// ʵ�ʽ�������
				actualCount = parm.getInt("ACTUAL_HAND_OVER_QTY", i) + 1;
				table.setItem(i, "ACTUAL_HAND_OVER_QTY", actualCount);
				
				// ������
				if (StringUtils.isEmpty(parm.getValue("HAND_OVER_USER", i))) {
					table.setItem(i, "HAND_OVER_USER", Operator.getID());
				}

				// ��������
				if (StringUtils.isEmpty(parm.getValue("HAND_OVER_DATE", i))) {
					table.setItem(i, "HAND_OVER_DATE", SystemTool.getInstance()
							.getDate());
				}
				
				flag = true;
			}
		}
		
		if (!flag) {
			this.messageBox("������Ų�����");
		}
		
		this.clearValue("EN_BAR_CODE");
		this.grabFocus("EN_BAR_CODE");
	}
	
	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		if (StringUtils.isEmpty(this.getValueString("PREPARE_DATE"))) {
			this.messageBox("������Ӧ����ʱ��");
			parm.setErrCode(-1);
			return parm;
		} else {
			parm.setData("PREPARE_DATE", this.getValueString("PREPARE_DATE")
					.substring(0, 10).replaceAll("-", ""));
		}
		// ����
		parm.setData("DEPT_CODE", getValueString("DEPT_CODE"));
		// ����
		parm.setData("STATION_CODE", getValueString("STATION_CODE"));
		
		// ���״̬
		if (getRadioButton("STATUS_N").isSelected()) {
			parm.setData("HAND_OVER_STATUS", "N");
		} else {
			parm.setData("HAND_OVER_STATUS", "Y");
		}
		
		return parm;
	}
	
	/**
	 * ���뽻�ӱ�ע��Ӧ�¼�
	 */
	public void onTableChangeValue(Object obj) {
		// ֵ�ı�ĵ�Ԫ��
		TTableNode node = (TTableNode) obj;
		if (node == null) {
			return;
		}
		// �ж����ݸı�
		if (node.getValue().equals(node.getOldValue())) {
			return;
		}
		
		TParm parm = table.getParmValue();
		int row = node.getRow();
		
		// ������
		if (StringUtils.isEmpty(parm.getValue("HAND_OVER_USER", row))) {
			table.setItem(row, "HAND_OVER_USER", Operator.getID());
		}

		// ��������
		if (StringUtils.isEmpty(parm.getValue("HAND_OVER_DATE", row))) {
			table.setItem(row, "HAND_OVER_DATE", SystemTool.getInstance()
					.getDate());
		}
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		this.onInitPage();
	}
	
	/**
     * �õ�Table����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TTable getTable(String tagName) {
        return (TTable) getComponent(tagName);
    }
    
    /**
	 * �õ�RadioButton����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TRadioButton getRadioButton(String tagName) {
		return (TRadioButton) getComponent(tagName);
	}
}
