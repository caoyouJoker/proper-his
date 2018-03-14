package com.javahis.ui.adm;

import java.awt.Component;
import java.sql.Timestamp;

import javax.swing.JOptionPane;

import jdo.adm.ADMReferralTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;

/**
 * <p>
 * Title: ת��ת�������ѯ
 * </p>
 * 
 * <p>
 * Description: ת��ת�������ѯ
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
 * @author wangb 2015.8.24
 * @version 1.0
 */
public class ADMReferralApplyInControl extends TControl {
	
	private TTable table;
	private boolean extractEmrFileFlg; // ת�ﲡ����ȡע��
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
    	this.onInitPageControl();
    }
    
    /**
     * ��ʼ������ؼ�
     */
    private void onInitPageControl() {
    	Timestamp today = SystemTool.getInstance().getDate();
    	this.setValue("REF_APPLY_DATE_S", StringTool.rollDate(today, -7));
    	this.setValue("REF_APPLY_DATE_E", today);
    }
    
    
    /**
     * ��ѯ����
     */
	public void onQuery() {

		table.setParmValue(new TParm());

		// ��ȡ��ѯ��������
		TParm queryParm = this.getQueryParm();

		if (queryParm.getErrCode() < 0) {
			this.messageBox(queryParm.getErrText());
			return;
		}
		
		// ��ѯת��ת�������
		TParm result = ADMReferralTool.getInstance().queryAdmReferralIn(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯת��ת����������ʧ��");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() < 1) {
			this.messageBox("��������");
			return;
		}
		
		table.setParmValue(result);
	}
    
    /**
     * ��ý����ѯ����
     */
    private TParm getQueryParm() {
    	TParm parm = new TParm();
    	if (StringUtils.isEmpty(this.getValueString("REF_APPLY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("REF_APPLY_DATE_E"))) {
			table.setParmValue(new TParm());
			parm.setErr(-1, "�������ѯʱ��");
    		return parm;
    	}
    	
		parm.setData("REF_APPLY_DATE_S", this.getValueString("REF_APPLY_DATE_S")
				.substring(0, 10).replace("-", "/"));
		parm.setData("REF_APPLY_DATE_E", this.getValueString("REF_APPLY_DATE_E")
				.substring(0, 10).replace("-", "/"));
		
		if (StringUtils
				.isNotEmpty(this.getValueString("REF_APPLY_HOSP").trim())) {
			parm.setData("REF_APPLY_HOSP", this
					.getValueString("REF_APPLY_HOSP").trim());
		}

		if (StringUtils.isNotEmpty(this.getValueString("IDNO").trim())) {
			parm.setData("IDNO", this.getValueString("IDNO").trim());
		}
		
		if (StringUtils.isNotEmpty(this.getValueString("PAT_NAME").trim())) {
			parm.setData("PAT_NAME", this.getValueString("PAT_NAME").trim());
		}
		
    	return parm;
    }
    
    /**
     * ��շ���
     */
    public void onClear() {
		clearValue("REF_APPLY_HOSP;IDNO;PAT_NAME");
		this.onInitPageControl();
		table.setParmValue(new TParm());
    }
    
    /**
     * ����ת�ﲡ��
     */
    public void onShowEmrFile() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
    	TParm selectedParm = table.getParmValue().getRow(row);
    	
    	TParm queryParm = new TParm();
    	queryParm.setData("REFERRAL_NO", selectedParm.getValue("REFERRAL_NO"));
    	// ��ѯת��ת�������
    	TParm result = ADMReferralTool.getInstance().queryAdmReferralIn(queryParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯת��ת���������");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
    	}
    	
    	// δ��ȡת�ﲡ���蹹������״�ṹ
		if (StringUtils.equals("N", result.getValue("EMR_FILE_EXTRACT_FLG", 0))) {
			selectedParm.setData("CASE_NO", "999999");
		}
    	
    	TParm parm = new TParm();
        parm.setData("SYSTEM_TYPE", "INW");
        parm.setData("REF_FLG", "Y");
        parm.setData("ADM_TYPE", "I");
        parm.setData("CASE_NO", selectedParm.getValue("CASE_NO"));
        parm.setData("PAT_NAME", selectedParm.getValue("PAT_NAME"));
        parm.setData("MR_NO", selectedParm.getValue("MR_NO"));
        parm.setData("IPD_NO", "");
        parm.setData("ADM_DATE", selectedParm.getTimestamp("IN_DATE"));
        parm.setData("DEPT_CODE", selectedParm.getValue("DEPT_DESC"));
        parm.setData("EMR_DATA_LIST", new TParm());
        parm.setData("EMR_FILE_EXTRACT_FLG", result.getValue("EMR_FILE_EXTRACT_FLG", 0));
        this.openDialog("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
    }
    
    /**
     * ��ת�����뵥
     */
    public void onShowReferral() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
    	TParm selectedParm = table.getParmValue().getRow(row);
    	
    	this.openDialog("%ROOT%/config/adm/ADMReferralApply.x", selectedParm);
    }
    
    /**
     * ��ȡת�ﲡ��
     */
    public void onExtractEmrFile() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
    	TParm selectedParm = table.getParmValue().getRow(row);
    	
    	// ��ѯת��ת�������
    	TParm result = ADMReferralTool.getInstance().queryAdmReferralIn(selectedParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯת��ת���������");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
    	}
    	
		if (StringUtils.equals("Y", result.getValue("EMR_FILE_EXTRACT_FLG", 0))) {
			this.messageBox("��ת�ﲡ�˲����Ѿ���ȡ���");
			return;
		}
    	
    	// ��ȡת�ﲡ��
    	result = ADMReferralTool.getInstance().extractEmrFile(selectedParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ȡת�ﲡ������");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
    	}
    	
		class MessageTread extends Thread {
			public void run() {
				try {
					Thread.sleep(3000);
					extractEmrFileFlg = false;
					JOptionPane.showMessageDialog((Component)getComponent(), "��ȡ�ɹ�", "��Ϣ", 1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
    	
		MessageTread messageTread = new MessageTread();
		messageTread.start();
		
		extractEmrFileFlg = true;
		while(extractEmrFileFlg) {
		}
    }
    
	/**
	 * �õ�TextFormat����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
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
	 * �õ�ComboBox����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
}
