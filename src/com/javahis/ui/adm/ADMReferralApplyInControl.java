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
 * Title: 转诊转入申请查询
 * </p>
 * 
 * <p>
 * Description: 转诊转入申请查询
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
	private boolean extractEmrFileFlg; // 转诊病历提取注记
	
	/**
     * 初始化方法
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
    	this.onInitPageControl();
    }
    
    /**
     * 初始化界面控件
     */
    private void onInitPageControl() {
    	Timestamp today = SystemTool.getInstance().getDate();
    	this.setValue("REF_APPLY_DATE_S", StringTool.rollDate(today, -7));
    	this.setValue("REF_APPLY_DATE_E", today);
    }
    
    
    /**
     * 查询方法
     */
	public void onQuery() {

		table.setParmValue(new TParm());

		// 获取查询条件数据
		TParm queryParm = this.getQueryParm();

		if (queryParm.getErrCode() < 0) {
			this.messageBox(queryParm.getErrText());
			return;
		}
		
		// 查询转诊转入申请表
		TParm result = ADMReferralTool.getInstance().queryAdmReferralIn(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("查询转诊转入申请数据失败");
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
		}
		
		if (result.getCount() < 1) {
			this.messageBox("查无数据");
			return;
		}
		
		table.setParmValue(result);
	}
    
    /**
     * 获得界面查询条件
     */
    private TParm getQueryParm() {
    	TParm parm = new TParm();
    	if (StringUtils.isEmpty(this.getValueString("REF_APPLY_DATE_S"))
    			|| StringUtils.isEmpty(this.getValueString("REF_APPLY_DATE_E"))) {
			table.setParmValue(new TParm());
			parm.setErr(-1, "请输入查询时间");
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
     * 清空方法
     */
    public void onClear() {
		clearValue("REF_APPLY_HOSP;IDNO;PAT_NAME");
		this.onInitPageControl();
		table.setParmValue(new TParm());
    }
    
    /**
     * 查阅转诊病历
     */
    public void onShowEmrFile() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中一行数据");
			return;
		}
    	TParm selectedParm = table.getParmValue().getRow(row);
    	
    	TParm queryParm = new TParm();
    	queryParm.setData("REFERRAL_NO", selectedParm.getValue("REFERRAL_NO"));
    	// 查询转诊转入申请表
    	TParm result = ADMReferralTool.getInstance().queryAdmReferralIn(queryParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询转诊转入申请错误");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
    	}
    	
    	// 未提取转诊病历需构建空树状结构
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
     * 打开转诊申请单
     */
    public void onShowReferral() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中一行数据");
			return;
		}
    	TParm selectedParm = table.getParmValue().getRow(row);
    	
    	this.openDialog("%ROOT%/config/adm/ADMReferralApply.x", selectedParm);
    }
    
    /**
     * 提取转诊病历
     */
    public void onExtractEmrFile() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中一行数据");
			return;
		}
    	TParm selectedParm = table.getParmValue().getRow(row);
    	
    	// 查询转诊转入申请表
    	TParm result = ADMReferralTool.getInstance().queryAdmReferralIn(selectedParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询转诊转入申请错误");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
    	}
    	
		if (StringUtils.equals("Y", result.getValue("EMR_FILE_EXTRACT_FLG", 0))) {
			this.messageBox("该转诊病人病历已经提取完毕");
			return;
		}
    	
    	// 提取转诊病历
    	result = ADMReferralTool.getInstance().extractEmrFile(selectedParm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("提取转诊病历错误");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			return;
    	}
    	
		class MessageTread extends Thread {
			public void run() {
				try {
					Thread.sleep(3000);
					extractEmrFileFlg = false;
					JOptionPane.showMessageDialog((Component)getComponent(), "提取成功", "消息", 1);
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
	 * 得到TextFormat对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextFormat getTextFormat(String tagName) {
		return (TTextFormat) getComponent(tagName);
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
	 * 得到ComboBox对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TComboBox getComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
}
