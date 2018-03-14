package com.javahis.ui.hrm;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;

/**
 * <p>
 * Title: һ���ٴ������Ϣ
 * </p>
 * 
 * <p>
 * Description: һ���ٴ������Ϣ
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
 * @author wangb 2016.7.11
 * @version 1.0
 */
public class HRMPicInfoControl extends TControl {
	
	private TTable table;
	private TParm parameter;
	
	/**
	 * ��ʼ������
	 */
    public void onInit() {
    	super.onInit();
    	table = (TTable)getComponent("TABLE");
		Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				parameter = (TParm) obj;
				// ԤԼסԺ�½�
				if ("NEW".equals(parameter.getValue("TYPE"))) {
					this.callFunction("UI|BUTTON_RTN|Visible", true);
					this.callFunction("UI|BUTTON_SAVE|Visible", false);
				} else {
					this.callFunction("UI|BUTTON_RTN|Visible", false);
					this.callFunction("UI|BUTTON_SAVE|Visible", true);
				}
			}
		}
    	this.onQuery();
    }
    
    /**
     * ��ѯ
     */
	public void onQuery() {
		String sql = "SELECT 'N' AS FLG,REAL_CHK_DATE,A.COMPANY_CODE,A.CONTRACT_DESC,A.MR_NO,A.PAT_NAME,A.PACKAGE_CODE,B.CASE_NO "
				+ " FROM HRM_CONTRACTD A,HRM_PATADM B WHERE A.MR_NO = B.MR_NO AND A.CONTRACT_CODE = B.CONTRACT_CODE AND A.MR_NO = '"
				+ parameter.getValue("MR_NO")
				+ "' AND REAL_CHK_DATE IS NOT NULL AND A.ROLE_TYPE = 'PIC' ORDER BY REAL_CHK_DATE DESC";

		TParm result = new TParm(TJDODBTool.getInstance().select(sql));

		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�����Ϣ����");
			err(result.getErrText());
			return;
		} else {
			table.setParmValue(result);
		}
	}

	/**
	 * �ش�
	 */
	public void onReturn() {
		TParm selParm = table.getParmValue();
		if (selParm == null || selParm.getCount() < 1) {
			this.messageBox("���������");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
			
		if (!selParm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡҪ�󶨵������Ϣ");
			return;
		}
		
		int count = selParm.getCount();
		for (int i = count - 1; i > -1; i--) {
			if ("N".equals(selParm.getValue("FLG", i))) {
				selParm.removeRow(i);
			}
		}
		this.setReturnValue(selParm);
		this.closeWindow();
	}
	
	/**
	 * ����
	 */
	public void onSave() {
		TParm selParm = table.getParmValue();
		
		if (selParm == null || selParm.getCount() < 1) {
			this.messageBox("���������");
			return;
		}
		
		// ǿ��ʧȥ�༭����
		if (table.getTable().isEditing()) {
			table.getTable().getCellEditor().stopCellEditing();
		}
		
		if (!selParm.getValue("FLG").contains("Y")) {
			this.messageBox("�빴ѡҪ�󶨵������Ϣ");
			return;
		}
		
		int count = selParm.getCount();
		for (int i = count - 1; i > -1; i--) {
			if ("N".equals(selParm.getValue("FLG", i))) {
				selParm.removeRow(i);
			}
		}

		String hrmCaseNo = selParm.getValue("CASE_NO").replace("[", "")
				.replace("]", "").replace(" ", "");
		String sql = "UPDATE ADM_RESV SET OPD_CASE_NO = '" + hrmCaseNo
				+ "' WHERE RESV_NO = '" + parameter.getValue("RESV_NO") + "'";
		
		TParm result = new TParm(TJDODBTool.getInstance().update(sql));

		if (result.getErrCode() < 0) {
			this.messageBox("�������Ϣ����");
			err(result.getErrText());
			return;
		} else {
			this.messageBox("����ɹ�");
		}
		
		this.closeWindow();
	}
	
	/**
	 * ȡ��
	 */
	public void onCancel() {
		this.closeWindow();
	}
}
