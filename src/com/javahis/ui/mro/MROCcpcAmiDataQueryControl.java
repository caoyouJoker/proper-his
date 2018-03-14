package com.javahis.ui.mro;

import jdo.mro.MROTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTable;
import com.javahis.util.ExportExcelUtil;

/**
 * <p>Title: CCPC-AMI����ͳ�Ʊ���</p>
 *
 * <p>Description: CCPC-AMI����ͳ�Ʊ���</p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: Javahis</p>
 *
 * @author wangb 2018.1.5
 * @version 1.0
 */
public class MROCcpcAmiDataQueryControl extends TControl {

	private TTable table;
	
	public MROCcpcAmiDataQueryControl() {
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
		
		// ��ѯCCPC-AMI����
		TParm result = MROTool.getInstance().queryCcpcAmiData(queryParm);
		
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
		ExportExcelUtil.getInstance().exportExcel(table, "CCPC-AMI����ͳ��");
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
