package com.javahis.ui.mro;

import jdo.mro.MROTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: �⼮��Աͳ�Ʋ�ѯ����</p>
 *
 * <p>Description: �⼮��Աͳ�Ʋ�ѯ����</p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2017.12.29
 * @version 1.0
 */
public class MROForeignerDataQueryControl extends TControl {

	private TTable table;
	
	public MROForeignerDataQueryControl() {
		super();
	}
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
    	table = (TTable)this.getComponent("TABLE");
		this.onInitPage();
    }
    
    /**
	 * ��ʼ��ҳ��
	 */
	public void onInitPage() {
		// ȡ�õ�ǰ����
		String todayDate = SystemTool.getInstance().getDate().toString()
				.substring(0, 10).replace('-', '/');
		// �趨Ĭ��չ������
    	this.setValue("START_DATE", todayDate);
    	this.setValue("END_DATE", todayDate);
    	this.clearValue("DEPT_CODE;TOTAL");
    	((TRadioButton)this.getComponent("STATUS_ALL")).setSelected(true);
    	table.setParmValue(new TParm());
	}
	
	/**
	 * ��ѯ
	 */
	public void onQuery() {
		table.setParmValue(new TParm());
		if (!checkData()) {
			return;
		}
		
		TParm queryParm = new TParm();
		queryParm.setData("START_DATE", this.getValueString("START_DATE").substring(0, 10));
		queryParm.setData("END_DATE", this.getValueString("END_DATE").substring(0, 10));
		queryParm.setData("DEPT_CODE", this.getValueString("DEPT_CODE"));
		if (((TRadioButton)getComponent("STATUS_IN")).isSelected()) {
			queryParm.setData("STATUS", "IN");
		} else if (((TRadioButton)getComponent("STATUS_OUT")).isSelected()) {
			queryParm.setData("STATUS", "OUT");
		}
		
		// ��ѯ�⼮��ԱסԺ����
		TParm result = MROTool.getInstance().queryForeignerData(queryParm);
		
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯʧ��");
			return;
		} else if (result.getCount() < 1) {
			this.messageBox("��������");
			this.setValue("TOTAL", "0");
			return;
		} else {
			table.setParmValue(result);
			this.setValue("TOTAL", String.valueOf(result.getCount()));
			return;
		}
	}
	
	/**
	 * ����Excel
	 */
	public void onExport() {
		// �õ�UI��Ӧ�ؼ�����ķ���
		TParm parm = table.getShowParmValue();
		if (null == parm || parm.getCount("MR_NO") <= 0) {
			this.messageBox("û����Ҫ����������");
			return;
		}
		ExportExcelUtil.getInstance().exportExcel(table, "�⼮��Աͳ��");
	}
	
	/**
	 * ���
	 */
	public void onClear() {
		this.onInitPage();
	}
	
	/**
	 * ���ݺ�������֤
	 * 
	 * @return
	 */
	private boolean checkData() {
		String startDate = this.getValueString("START_DATE");
		String endDate = this.getValueString("END_DATE");
		if (StringUtils.isEmpty(startDate)) {
			this.messageBox("����д��ѯ��ʼ����");
			return false;
		}
		if (StringUtils.isEmpty(endDate)) {
			this.messageBox("����д��ѯ��ֹ����");
			return false;
		}
		
		return true;
	}
}
