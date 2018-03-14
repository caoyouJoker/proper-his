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
 * Title: ��������ļ����Ϣ
 * </p>
 * 
 * <p>
 * Description: ��������ļ����Ϣ
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
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
		this.onClear();
    }
    
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		String sql = "SELECT 'N' AS FLG,A.* FROM HRM_RECRUIT A WHERE 1 = 1 ";
		
		// ��������
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
			this.messageBox("��ѯ��������ļ��Ϣ����");
			err(result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("��������");
		}
		table.setParmValue(result);
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		clearValue("PAT_NAME;CONTRACT_CODE");
		getRadioButton("SEX_ALL").setSelected(true);
		table.setParmValue(new TParm());
	}
	
	/**
	 * ȫѡ��ѡ��ѡ���¼�
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
	 * ����
	 */
	public void onReturn() {
		TParm selectedParm = table.getParmValue();
		
		// ǿ��ʧȥ�༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		int count = selectedParm.getCount();
		if (!selectedParm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡ�ش�����");
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
	
	/**
	 * �õ�getCheckBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TCheckBox getCheckBox(String tagName) {
		return (TCheckBox) getComponent(tagName);
	}
}
