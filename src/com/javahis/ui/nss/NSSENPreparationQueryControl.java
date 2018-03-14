package com.javahis.ui.nss;

import java.sql.Timestamp;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ����Ӫ�����������ѯ
 * </p>
 * 
 * <p>
 * Description: ����Ӫ�����������ѯ
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
 * @author wangb 2015.6.25
 * @version 1.0
 */
public class NSSENPreparationQueryControl extends TControl {
    public NSSENPreparationQueryControl() {
        super();
    }

    private TTable tableM;
    private TTable tableD;

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
		
		// �����������ݵ����¼�
		this.callFunction("UI|TABLE_M|addEventListener", "TABLE_M->"
				+ TTableEvent.CLICKED, this, "onTableMClicked");
		
		// �ؼ���ʼ��
		this.onInitControl();
	}
	
	/**
	 * �ؼ���ʼ��
	 */
	public void onInitControl() {
		// ȡ�õ�ǰ����
		Timestamp nowDate = SystemTool.getInstance().getDate();

		// �趨Ĭ��չ������
    	this.setValue("QUERY_DATE_S", StringTool.rollDate(nowDate, -7));
    	this.setValue("QUERY_DATE_E", nowDate);
    	
    	clearValue("DEPT_CODE;STATION_CODE;MR_NO;DIETITIANS;DIET_TYPE");
    	
    	tableM.setParmValue(new TParm());
    	tableD.setParmValue(new TParm());
	}

    /**
     * ��ѯ����
     */
    public void onQuery() {
		tableM.setParmValue(new TParm());
		tableD.setParmValue(new TParm());
		
    	// ��ȡ��ѯ��������
    	TParm queryParm = this.getQueryParm();
    	
    	if (queryParm.getErrCode() < 0) {
    		this.messageBox(queryParm.getErrText());
    		return;
    	}
    	
    	TParm result = NSSEnteralNutritionTool.getInstance().queryENDspnM(queryParm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�������ݴ���");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("��������");
			tableM.setParmValue(new TParm());
			tableD.setParmValue(new TParm());
			return;
		}
		
        tableM.setParmValue(result);
    }
    
	/**
	 * ��ȡ��ѯ��������
	 * 
	 * @return
	 */
	private TParm getQueryParm() {
		TParm parm = new TParm();
		if (StringUtils.isEmpty(this.getValueString("QUERY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("QUERY_DATE_E"))) {
			tableM.setParmValue(new TParm());
			parm.setErr(-1, "�������ѯʱ��");
    		return parm;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace('-', '/'));
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace('-', '/'));
		
		// ����
		if (StringUtils.isNotEmpty(this.getValueString("DEPT_CODE").trim())) {
			parm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		}
		// ����
		if (StringUtils.isNotEmpty(this.getValueString("STATION_CODE").trim())) {
			parm.setData("STATION_CODE", this.getValueString("STATION_CODE"));
		}
		// ������
		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO"));
		}
		// Ӫ��ʦ
		if (StringUtils.isNotEmpty(this.getValueString("DIETITIANS").trim())) {
			parm.setData("ORDER_DR_CODE", this.getValueString("DIETITIANS"));
		}
		// ��ʳ����
		if (StringUtils.isNotEmpty(this.getValueString("DIET_TYPE").trim())) {
			parm.setData("ORDER_CODE", this.getValueString("DIET_TYPE"));
		}
		// δȡ��
		parm.setData("CANCEL_FLG", "N");
		
		return parm;
	}
	
	/**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryByMrNo() {
		// ȡ�ò�����
		String mrNo = this.getValueString("MR_NO").trim();
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("���޴˲�����");
				return;
			}
			//modify by huangtt 20160930 EMPI���߲�����ʾ  start
			mrNo = PatTool.getInstance().checkMrno(mrNo);
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
		            this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
		    }
			//modify by huangtt 20160930 EMPI���߲�����ʾ  end
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
		}
	}
	
	/**
	 * ��Ӷ�tableM��ѡ�м����¼�
	 * 
	 * @param row
	 */
	public void onTableMClicked(int row) {
		if (row < 0) {
			return;
		}
		
		tableD.setParmValue(new TParm());
		
		TParm data = tableM.getParmValue();
		int selectedRow = tableM.getSelectedRow();
		
		TParm result = NSSEnteralNutritionTool.getInstance().queryENOrderD(
				data.getRow(selectedRow));
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ�䷽��ϸ����");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		tableD.setParmValue(result);
	}
	
    /**
     * ��շ���
     */
    public void onClear() {
    	// ��ʼ��ҳ��ؼ�����
    	this.onInitControl();
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
