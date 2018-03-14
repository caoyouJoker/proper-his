package com.javahis.ui.nss;

import java.sql.Timestamp;

import jdo.nss.NSSEnteralNutritionTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.dongyang.util.StringTool;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>
 * Title: Ӫ����ʹ�������ѯ
 * </p>
 * 
 * <p>
 * Description: Ӫ����ʹ�������ѯ
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
 * @author wangb 2015.7.1
 * @version 1.0
 */
public class NSSENNPUsageAmountQueryControl extends TControl {
    public NSSENNPUsageAmountQueryControl() {
        super();
    }

    private TTable table;

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
		table = getTable("TABLE");
		
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
    	
    	clearValue("NUTRITIONAL_POWDER;ORDER_CODE");
    	
    	table.setParmValue(new TParm());
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
    	
    	// ��ѯӪ����ʹ�����
    	TParm result = NSSEnteralNutritionTool.getInstance().queryNutritionalPowderUsage(queryParm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯӪ����ʹ���������");
			err("ERR:" + result.getErrCode() + result.getErrText());
			return;
		}
		
		if (result.getCount() <= 0) {
			this.messageBox("��������");
			table.setParmValue(new TParm());
			return;
		}
		
        table.setParmValue(result);
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
			table.setParmValue(new TParm());
			parm.setErr(-1, "�������ѯʱ��");
    		return parm;
    	}
    	
		parm.setData("QUERY_DATE_S", this.getValueString("QUERY_DATE_S")
				.substring(0, 10).replace("-", "")
				+ "000000");
		parm.setData("QUERY_DATE_E", this.getValueString("QUERY_DATE_E")
				.substring(0, 10).replace("-", "")
				+ "235959");
		
		// Ӫ����
		if (StringUtils.isNotEmpty(this.getValueString("NUTRITIONAL_POWDER").trim())) {
			parm.setData("NUTRITIONAL_POWDER", this.getValueString("NUTRITIONAL_POWDER"));
		}
		
		// ҩƷ����
		if (StringUtils.isNotEmpty(this.getValueString("ORDER_CODE").trim())) {
			parm.setData("ORDER_CODE", this.getValueString("ORDER_CODE"));
		}
		
		return parm;
	}
	
	/**
	 * Ӫ����������ֵ�ı���÷���
	 */
	public void onChangeValue() {
		// Ӫ���۴���
		String nutritaionalPowderCode = this.getValueString("NUTRITIONAL_POWDER");
		if (StringUtils.isEmpty(nutritaionalPowderCode)) {
			this.setValue("ORDER_CODE", "");
		} else {
			TParm parm = new TParm();
			parm.setData("FORMULA_CODE", nutritaionalPowderCode);
			parm = NSSEnteralNutritionTool.getInstance().selectDataPFM(parm);
			
			if (parm.getErrCode() < 0) {
				this.messageBox("��ѯ�䷽�ֵ����");
				return;
			}
			
			this.setValue("ORDER_CODE", parm.getValue("ORDER_CODE", 0));
		}
	}
	
    /**
     * ��շ���
     */
    public void onClear() {
    	// ��ʼ��ҳ��ؼ�����
    	this.onInitControl();
    }
    
	/**
	 * ����Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("ORDER_CODE") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "Ӫ����ʹ�����");
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
