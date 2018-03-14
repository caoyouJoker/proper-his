package com.javahis.ui.nss;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Operator;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.event.TPopupMenuEvent;

/**
 * <p>Title: ����Ӫ�����ӷ���</p>
 *
 * <p>Description: ����Ӫ�����ӷ���</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2016.3.4
 * @version 1.0
 */
public class NSSENExtraFeeDictControl extends TControl {
	
    private TTable table;
    private TTextField orderCode;
	
    /**
	 * ��ʼ��
	 */
	public void onInit() {
		super.onInit();
		table = getTable("TABLE");
		TParm parm = new TParm();
		orderCode = (TTextField) this.getComponent("ORDER_CODE");
		orderCode.setPopupMenuParameter("UD", getConfigParm().newConfig(
				"%ROOT%\\config\\sys\\SYSFeePopup.x"), parm);
		// ������ܷ���ֵ����
		orderCode.addEventListener(TPopupMenuEvent.RETURN_VALUE, this,
				"popReturn");
		
		this.onInitPage();
	}
	
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		callFunction("UI|CATEGORY_CODE|setEnabled", true);
        callFunction("UI|ORDER_CODE|setEnabled", true);
        callFunction("UI|ORDER_DESC|setEnabled", true);
        this.clearValue("CATEGORY_CODE;ORDER_CODE;ORDER_DESC;CHARGE_TYPE");
    	getCheckBox("ACTIVE_FLG").setSelected(true);
		this.onQuery();
	}

	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		TParm queryParm = this
				.getParmForTag("CATEGORY_CODE;ORDER_CODE;CHARGE_TYPE");
		// ��ѯ����Ӫ�����ӷ���
		TParm result = NSSEnteralNutritionTool.getInstance().queryENExtraFee(
				queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ����Ӫ�����ӷ��ô���");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		} else {
			table.setParmValue(result);
		}
	}

	/**
	 * ���淽��
	 */
	public void onSave() {
		// ���ݺ�����У��
		if (this.validateData()) {
			TParm result = new TParm();
			TParm parm = getParmForTag("CATEGORY_CODE;ORDER_CODE;CHARGE_TYPE");
			parm.setData("OPT_USER", Operator.getID());
			parm.setData("OPT_TERM", Operator.getIP());
			
			// ����
			if (table.getSelectedRow() >= 0) {
				parm.setData("CHARGE_TYPE", this.getValue("CHARGE_TYPE"));
				if (getCheckBox("ACTIVE_FLG").isSelected()) {
					parm.setData("ACTIVE_FLG", "Y");
				} else {
					parm.setData("ACTIVE_FLG", "N");
				}
				
				// ���³���Ӫ�����ӷ����ֵ�
				result = NSSEnteralNutritionTool.getInstance().updateENExtraFee(parm);
				
				// ����
			} else {
				
				if (getCheckBox("ACTIVE_FLG").isSelected()) {
					parm.setData("ACTIVE_FLG", "Y");
				} else {
					parm.setData("ACTIVE_FLG", "N");
				}
				
				// ��������Ӫ�����ӷ����ֵ�
				result = NSSEnteralNutritionTool.getInstance().insertENExtraFee(parm);
			}
			
			if (result.getErrCode() < 0) {
				this.messageBox("E0001");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
			} else {
				this.messageBox("P0001");
				this.onClear();
			}
		}
	}
	
	/**
	 * ɾ������
	 */
	public void onDelete() {
		if (table.getSelectedRow() < 0) {
			this.messageBox("��ѡ��Ҫɾ����������");
			return;
		}
		
		if (this.messageBox("ɾ��", "�Ƿ�ɾ��ѡ�е�����", 2) == 0) {
			TParm parm = new TParm();
			parm = getParmForTag("CATEGORY_CODE;ORDER_CODE");
			
			// ɾ������Ӫ�����ӷ����ֵ�
			TParm result = NSSEnteralNutritionTool.getInstance().deleteENExtraFee(parm);
			
			if (result.getErrCode() < 0) {
				this.messageBox("ɾ��ʧ��");
    			err("ERR:" + result.getErrCode() + result.getErrText());
    			return;
			} else {
				this.messageBox("ɾ���ɹ�");
				this.onClear();
			}
		}
	}
	
	/**
	 * ����У��
	 */
	private boolean validateData() {
		if (StringUtils.isEmpty(this.getValueString("CATEGORY_CODE"))) {
			this.messageBox("����Ӫ�����಻��Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(this.getValueString("ORDER_CODE"))) {
			this.messageBox("���ӷ��ñ��벻��Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(this.getValueString("ORDER_DESC"))) {
			this.messageBox("���ӷ������Ʋ���Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(this.getValueString("CHARGE_TYPE"))) {
			this.messageBox("�Ʒѵ�λ����Ϊ��");
			return false;
		}
		
		return true;
	}
	
	/**
     * ����������ݺ󣬴��������Ϣ
     */
    public void onTableClick(){
    	TTable table = getTable("TABLE");
    	int row = table.getSelectedRow();
    	TParm parm = table.getParmValue().getRow(row);
		this.setValueForParm("CATEGORY_CODE;ORDER_CODE;ORDER_DESC;CHARGE_TYPE",
				parm);
		if ("Y".equals(parm.getValue("ACTIVE_FLG"))) {
			getCheckBox("ACTIVE_FLG").setSelected(true);
		} else {
			getCheckBox("ACTIVE_FLG").setSelected(false);
		}
		
		callFunction("UI|CATEGORY_CODE|setEnabled", false);
        callFunction("UI|ORDER_CODE|setEnabled", false);
        callFunction("UI|ORDER_DESC|setEnabled", false);
    }
    
    /**
	 * ���ܷ���ֵ����
	 * @param tag
	 * @param obj
	 */
	public void popReturn(String tag, Object obj) {
		TParm parm = (TParm) obj;
		String order_code = parm.getValue("ORDER_CODE");
		if (StringUtils.isNotEmpty(order_code)) {
			this.setValue("ORDER_CODE", order_code);
		}
		String order_desc = parm.getValue("ORDER_DESC");
		if (StringUtils.isNotEmpty(order_desc)) {
			this.setValue("ORDER_DESC", order_desc);
		}
	}
    
    /**
     * ��շ���
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
     * �õ�TCheckBox����
     *
     * @param tagName
     *            Ԫ��TAG����
     * @return
     */
    private TCheckBox getCheckBox(String tagName) {
        return (TCheckBox) getComponent(tagName);
    }
}
