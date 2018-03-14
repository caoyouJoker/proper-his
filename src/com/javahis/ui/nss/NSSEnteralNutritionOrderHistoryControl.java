package com.javahis.ui.nss;

import org.apache.commons.lang.StringUtils;

import jdo.nss.NSSEnteralNutritionTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;

/**
 * <p>Title: ����Ӫ��������ʷ��¼</p>
 *
 * <p>Description: ����Ӫ��������ʷ��¼</p>
 *
 * <p>Copyright: Copyright (c) 2015</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2015.4.20
 * @version 1.0
 */
public class NSSEnteralNutritionOrderHistoryControl extends TControl {
	
	private TTable tableM;
	private TTable tableD;
	private TParm parameterParm;
	
    public NSSEnteralNutritionOrderHistoryControl() {
        super();
    }

    /**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
		this.onInitPage();
    }
    
	/**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		tableM = getTable("TABLE_M");
		tableD = getTable("TABLE_D");
		
		Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			}
		} else {
			this.messageBox("�޴�������");
			return;
		}
    	
		// ������Ϣ������ݵ���¼�
		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
				+ TTableEvent.CLICKED, this, "onTableMClicked");
		
		// ��ѯָ����������סԺ�����ж����䷽����
		TParm result = this.queryData();
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ��ʷ���ݴ���");
			return;
		}
		
		tableM.setParmValue(result);
	}
	
	/**
	 * ��ѯָ����������סԺ�����ж����䷽����
	 */
	private TParm queryData() {
		TParm queryParm = new TParm();
		queryParm.setData("CASE_NO", parameterParm.getValue("CASE_NO"));
		queryParm.setData("ORDER_DATE_SORT", "Y");
		// ����ѡ�е�סԺҽ����������ʳҽ�������в�ѯ��Ӧ��Ӫ��ʦҽ����������
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderM(queryParm);
		
		int count = result.getCount();
		// ȥ���½�ҽ������
		for (int i = count - 1; i > -1; i--) {
			if (StringUtils.equals(parameterParm.getValue("EN_ORDER_NO"),
					result.getValue("EN_ORDER_NO", i))) {
				result.removeRow(i);
				break;
			}
		}
		return result;
	}
	
	/**
	 * ��Ӷ�TABLE_M�ļ����¼�
	 * 
	 * @param row
	 */
	public void onTableMClicked(int row) {
		if (row < 0) {
			return;
		}
		
		TParm parm = tableM.getParmValue().getRow(row);
		// ����Ӫ��ʦҽ�������ѯ��Ӧ���䷽��ϸSQL
		String sql = NSSEnteralNutritionTool.getInstance().queryENOrderDSql(parm);
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�䷽��ϸ����");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		for (int i = 0; i < result.getCount(); i++) {
			result.setData("FLG", i, "N");
		}
		
		tableD.setParmValue(result);
	}
	
	/**
	 * ȫѡ��ѡ��ѡ���¼�
	 */
	public void onCheckSelectAll() {
		if (tableD.getRowCount() <= 0) {
			getCheckBox("SELECT_ALL").setSelected(false);
			return;
		}
		
		String flg = "N";
		if (getCheckBox("SELECT_ALL").isSelected()) {
			flg = "Y";
		}
		
		for (int i = 0; i < tableD.getRowCount(); i++) {
			tableD.setItem(i, "FLG", flg);
		}
	}
	
    /**
     * ���ط���
     */
    public void onReturn() {
    	// ǿ��ʧȥ�༭����
		if (tableD.getTable().isEditing()) {
			tableD.getTable().getCellEditor().stopCellEditing();
		}
    	
        if (tableD.getRowCount() <= 0) {
        	this.messageBox("�빴ѡ�䷽��ϸ");
            return;
        }
        TParm parm = tableD.getParmValue();
        boolean checkFlg = false;
        
        for (int i = 0; i < parm.getCount(); i++) {
        	if (parm.getBoolean("FLG", i)) {
        		checkFlg = true;
        		break;
        	}
        }
        
        if (!checkFlg) {
        	this.messageBox("�빴ѡ�䷽��ϸ");
        	return;
        }
        
        setReturnValue(parm);
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
