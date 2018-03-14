package com.javahis.ui.odi;

import java.util.Date;

import jdo.odi.ODIPICTool;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: һ���ٴ������޸�ҽ��ʱ��
 * </p>
 * 
 * <p>
 * Description: һ���ٴ������޸�ҽ��ʱ��
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
 * @author wangb 2016.6.13
 * @version 1.0
 */
public class ODIBatchModOrderDateControl extends TControl {
	private TParm inParm;
	private TTable packTable;
	private TTable orderTable;

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		// ȡ�ô������
		Object obj = this.getParameter();
		if (obj != null) {
			inParm = (TParm) obj;
		}
		
		packTable = getTable("PACK_TABLE");
		orderTable = getTable("ORDER_TABLE");
		
		this.onQuery();
	}
	
	/**
	 * ��ѯ
	 */
	private void onQuery() {
		TParm packParm = new TParm();
		packParm.setData("DEPTORDR_CODE", inParm.getValue("DEPT_CODE"));
		// ��ѯһ���ٴ������ײ�
		TParm packResult = ODIPICTool.getInstance().queryPICPackMain(packParm);
		
		if (packResult.getErrCode() < 0) {
			this.messageBox("��ѯһ���ٴ������ײʹ���");
			err("ERR:" + packResult.getErrText());
			return;
		} else {
			packTable.setParmValue(packResult);
		}
		
		packParm.setData("CASE_NO", inParm.getValue("CASE_NO", 0));
		// ֻ��ѯ����ҽ��
		packParm.setData("CAT1_TYPE", "LIS");
		
		// ��ѯ����һ���ٴ�ע�ǵ�ҽ��
		TParm orderResult = ODIPICTool.getInstance().queryPICOrder(packParm);
		if (orderResult.getErrCode() < 0) {
			this.messageBox("��ѯһ���ٴ�ҽ������");
			err("ERR:" + orderResult.getErrText());
			return;
		} else {
			orderTable.setParmValue(orderResult);
		}
	}
	
	/**
	 * �Զ��޸�ҽ������ʱ��
	 */
	public void onModifyDate() {
		int selPackTableRow = packTable.getSelectedRow();
		if (selPackTableRow < 0) {
			this.messageBox("��ѡ����Ӧ���ײ�");
			return;
		}
		
		if (orderTable.getParmValue() == null
				|| orderTable.getParmValue().getCount() < 1) {
			this.messageBox("û����Ҫ�޸ĵ�ҽ��");
			return;
		}
		
		String packCode = packTable.getParmValue().getRow(selPackTableRow)
				.getValue("PACK_CODE");
		
		// ��ѯһ���ٴ������ײ���ϸ
		TParm packOrderResult = ODIPICTool.getInstance().queryPICPackOrder(packCode);
		if (packOrderResult.getErrCode() < 0) {
			this.messageBox("��ѯһ���ٴ������ײ���ϸ����");
			err("ERR:" + packOrderResult.getErrText());
			return;
		} else {
			int orderCount = orderTable.getParmValue().getCount();
			int packOrderCount = packOrderResult.getCount();
			int count = orderCount;
			if (orderCount > packOrderCount) {
				count = packOrderCount;
			}
			
			// ǿ��ʧȥ�༭����
			if (orderTable.getTable().isEditing()) {
				orderTable.getTable().getCellEditor().stopCellEditing();
			}
			
			// ҩ������ʱ��
			Date phaDate = StringTool.getDate(orderTable
					.getItemString(0, "EFF_DATE").replace("-", "/")
					.substring(0, 19), "yyyy/MM/dd HH:mm:ss");
			Date date = null;
			int intervalTime = 0;
			for (int i = 0; i < count; i++) {
				intervalTime = (int) Math.round(packOrderResult.getDouble(
						"BC_INTERVAL_TIME", i) * 60);
				date = DateUtils.addMinutes(phaDate, intervalTime);
				orderTable.setItem(i, "EFF_DATE", StringTool.getString(date,
						"yyyy/MM/dd HH:mm:ss"));
			}
		}
	}
	
	/**
	 * ����
	 */
	public void onSave() {
		if (orderTable.getParmValue() == null
				|| orderTable.getParmValue().getCount() < 1) {
			this.messageBox("û����Ҫ�����ҽ��");
			return;
		}
		
		TParm tableParm = orderTable.getParmValue();
		TParm orderParm = new TParm();
		orderParm.setData("CASE_NO", tableParm.getValue("CASE_NO", 0));
		orderParm.setData("ORDER_NO", tableParm.getValue("ORDER_NO", 0));
		orderParm.setData("ORDER_NO_LIST", tableParm.getValue("ORDER_NO").replace(
				"[", "").replace("]", "").replace(" ", "").replace(",", "','"));
		
		// ��ѯҽ����������֤��ҽ���Ƿ񱻻�ʿ���
		TParm result = ODIPICTool.getInstance().queryOdiOrder(orderParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯҽ�����ݴ���");
			err("ERR:" + result.getErrText());
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("����ҽ������");
			return;
		}
		
		// ���ע��
		boolean nsCheckFlg = false;
		
		for (int i = 0; i < result.getCount(); i++) {
			if (StringUtils.isNotEmpty(result.getValue("NS_CHECK_CODE", i))) {
				nsCheckFlg = true;
				break;
			}
		}
		
		if (nsCheckFlg) {
			this.messageBox("��ǰҽ���д�������˵����ݣ���ȡ����˺��ٽ��в���");
			return;
		} else {
			// ִ�б������
			result = TIOM_AppServer.executeAction(
					"action.odi.ODIPICAction",
					"onSaveByBatchModOrderDate", tableParm);
			
			if (result.getErrCode() < 0) {
				err(result.getErrCode() + " " + result.getErrText());
				this.messageBox("E0001");
				return;
			} else {
				this.messageBox("P0001");
				// ������ɺ�ˢ��ҽ��վ��������
				inParm.runListener("addListener", result);
				this.closeWindow();
			}
		}
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
}
