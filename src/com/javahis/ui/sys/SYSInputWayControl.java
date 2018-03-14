package com.javahis.ui.sys;

import java.text.SimpleDateFormat;
import java.util.Date;

import jdo.sys.SYSInputWayTool;
import jdo.sys.Operator;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title:¼��;��ά��
 * </p>
 * 
 * <p>
 * Description:¼��;��ά��
 * </p>
 * 
 * <p>
 * Copyright: Copyright (c) 2017
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author liuyl 2017-04-06
 * @version JavaHis 1.0
 */
public class SYSInputWayControl extends TControl {

	private TTable table;

	public void onInit() {
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		onClear();
	}

	/**
	 * ��ѯ
	 */
	public void onQuery() {
		TParm queryParm = new TParm();
		if (this.getTRadioButton("STARTUSE").isSelected()) {
			// ����
			queryParm.setData("ENABLE_FLG", "Y");
		} else {
			// ͣ��
			queryParm.setData("ENABLE_FLG", "N");
		}
		String temp = null;
		temp = this.getValueString("GDVAS_CODE");
		if (!StringUtils.isEmpty(temp)) {
			queryParm.setData("GDVAS_CODE", temp);
		}
		temp = this.getValueString("GDVAS_DESC");
		if (!StringUtils.isEmpty(temp)) {
			queryParm.setData("GDVAS_DESC", temp);
		}
		TParm result = SYSInputWayTool.getNewInstance().selectInputWay(
				queryParm);
		if (result.getCount("ORDER_CODE") <= 0) {
			// this.messageBox("��������");
			table.setParmValue(result);
		} else {
			table.setParmValue(result);
		}

	}


	/**
	 * ���ѡ���¼� �����渳ֵ
	 */
	public void onTableClicked() {
		int row = table.getSelectedRow();
		if (row < 0) {
			return;
		}
		TParm tableParm = table.getParmValue();
		this.setValue("GDVAS_CODE", tableParm.getData("GDVAS_CODE", row));
		this.setValue("GDVAS_DESC", tableParm.getData("GDVAS_DESC", row));
		if ("Y".equals(tableParm.getData("ENABLE_FLG", row))) {
			getTRadioButton("STARTUSE").setSelected(true);
			getTRadioButton("STOPUSE").setSelected(false);
		} else {
			getTRadioButton("STOPUSE").setSelected(true);
			getTRadioButton("STARTUSE").setSelected(false);
		}
		this.getTextField("GDVAS_CODE").setEnabled(false);
	}

	/**
	 * ������޸�
	 */
	public void onSave() {
		String gdvasCode = this.getValueString("GDVAS_CODE");
		if (StringUtils.isEmpty(gdvasCode)) {
			this.messageBox("������¼��;�����룡");
			return;
		}
		String gdvasDesc = this.getValueString("GDVAS_DESC");
		if (StringUtils.isEmpty(gdvasDesc)) {
			this.messageBox("������¼��;�����ƣ�");
			return;
		}
		TParm saveParm = new TParm();
		saveParm.setData("GDVAS_CODE", gdvasCode);
		saveParm.setData("GDVAS_DESC", gdvasDesc);
		saveParm.setData("OPT_USER", Operator.getID());
		saveParm.setData("OPT_TERM", Operator.getIP());
		saveParm.setData("OPT_DATE", this.formatDate(new Date()));

		if (this.getTRadioButton("STARTUSE").isSelected()) {
			// ����
			saveParm.setData("ENABLE_FLG", "Y");
		} else {
			// ͣ��
			saveParm.setData("ENABLE_FLG", "N");
		}
		int row = table.getSelectedRow();
		if (row >= 0) {
			// ����
			TParm result = null;
			saveParm.setData("OLD_GDVAS_DESC",
					table.getParmValue().getData("GDVAS_DESC", row));
			result = SYSInputWayTool.getNewInstance().updateInputWay(saveParm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
			} else {
				this.messageBox("���³ɹ�");
			}
		} else {
			// ����
			TParm result = SYSInputWayTool.getNewInstance().selectInputWay(
					saveParm);
			for (int j = 0; j < table.getRowCount(); j++) {
                if(saveParm.getValue("GDVAS_CODE").equals(table.getParmValue().getData("GDVAS_CODE", j))){
                	this.messageBox("�Ѵ��ڸ�¼��;������,������¼��;��");
                	this.clearValue("GDVAS_CODE;GDVAS_DESC");
                	return;
                }
			}
			for (int i = 0; i < table.getRowCount(); i++) {
                if(saveParm.getValue("GDVAS_DESC").equals(table.getParmValue().getData("GDVAS_DESC", i))){
                	this.messageBox("�Ѵ��ڸ�¼��;������,������¼��;��");
                	this.clearValue("GDVAS_CODE;GDVAS_DESC");
                	return;
                }
			}
			result = SYSInputWayTool.getNewInstance().saveInputWay(saveParm);
			if (result.getErrCode() < 0) {
				this.messageBox("����ʧ��");
			} else {
				this.messageBox("�����ɹ�");
			}
		}
		this.getTextField("GDVAS_CODE").setEnabled(true);
		this.clearValue("GDVAS_CODE;GDVAS_DESC");
		onQuery();
	}

	private String formatDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddhhmmss");
		return sdf.format(date);
	}

//	/**
//	 * ɾ��
//	 */
//	public void onDelete() {
//		int row = table.getSelectedRow();
//		if (row < 0) {
//			this.messageBox("��ѡ��һ������ɾ��");
//			return;
//		}
//		if (this.messageBox("��ʾ", "ȷ��ɾ��?", YES_NO_OPTION) == NO_OPTION) {
//			return;
//		}
//		TParm tableParm = table.getParmValue();
//		TParm result = SYSInputWayTool.getNewInstance().delectInputWay(
//				tableParm.getRow(row));
//		if (result.getErrCode() < 0) {
//			this.messageBox("ɾ��ʧ��");
//		} else {
//			this.messageBox("ɾ���ɹ�");
//		}
//		this.getTextField("GDVAS_CODE").setEnabled(true);
//		this.clearValue("GDVAS_CODE;GDVAS_DESC");
//		onQuery();
//	}

	/**
	 * ���
	 */
	public void onClear() {
		table.setParmValue(new TParm());
		getTRadioButton("STARTUSE").setSelected(true);
		getTRadioButton("STOPUSE").setSelected(false);
		this.getTextField("GDVAS_CODE").setEnabled(true);
		this.clearValue("GDVAS_CODE;GDVAS_DESC");
	}

	/**
	 * ���ܷ���ֵ����
	 * 
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String gdvas_code = parm.getValue("GDVAS_CODE");
		if (!StringUtil.isNullString(gdvas_code))
			getTextField("GDVAS_CODE").setValue(gdvas_code);
		String gdvas_desc = parm.getValue("GDVAS_DESC");
		if (!StringUtil.isNullString(gdvas_desc))
			getTextField("GDVAS_DESC").setValue(gdvas_desc);
	}

	/**
	 * �õ�TextField����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextField getTextField(String tagName) {
		return (TTextField) getComponent(tagName);
	}
	
	/**
	 * �õ�RadioButton����
	 * 
	 * @param tag
	 * @return
	 */
	private TRadioButton getTRadioButton(String tag) {
		return (TRadioButton) getComponent(tag);
	}
}
