package com.javahis.ui.adm;

import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;

import jdo.adm.ADMReferralTool;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TTable;
import com.dongyang.util.FileTool;
import com.dongyang.util.StringTool;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ת��ת������
 * </p>
 * 
 * <p>
 * Description: ת��ת������
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
public class ADMReferralApplyOutControl extends TControl {
	
	private TTable table;
	private String tempPath = "C:\\JavaHisFile\\temp\\hl7";
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
    	table = getTable("TABLE");
    	
    	File f = new File(tempPath);
		if (!f.exists()) {
			f.mkdirs();
		}
    	
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
		TParm result = ADMReferralTool.getInstance().queryAdmReferralOut(queryParm);
		
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
				.isNotEmpty(this.getValueString("ACCEPT_HOSP_CODE").trim())) {
			parm.setData("ACCEPT_HOSP_CODE", this
					.getValueString("ACCEPT_HOSP_CODE").trim());
		}

		if (StringUtils.isNotEmpty(this.getValueString("MR_NO").trim())) {
			parm.setData("MR_NO", this.getValueString("MR_NO").trim());
		}
		
    	return parm;
    }
    
    /**
     * ��շ���
     */
    public void onClear() {
		clearValue("ACCEPT_HOSP_CODE;MR_NO");
		this.onInitPageControl();
		table.setParmValue(new TParm());
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
     * ��ת��ԭʼHL7�ļ�
     */
    public void onOpenRefHl7() {
    	int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		
    	TParm selectedParm = table.getParmValue().getRow(row);
    	// �������ļ���ȡBpelDataԭʼ�ļ��Ĵ��·��
    	String rootPath = TIOM_FileServer.getPath("BpelData");
    	
    	if (StringUtils.isEmpty(rootPath)) {
    		this.messageBox("��ȡBpelDataԭʼ�ļ��Ĵ��·������");
    		return;
    	}
    	
    	TSocket socket = TIOM_FileServer.getSocket();
    	String applyDate = selectedParm.getValue("APPLY_DATE");
    	// ƴ���ļ��������·��
		String filePath = rootPath
				+ applyDate.substring(0, 10).replaceAll("-", "\\\\") + "\\"
				+ selectedParm.getValue("CASE_NO");
    	
    	String fileNames[] = TIOM_FileServer.listFile(socket, filePath);
    	if (null == fileNames) {
    		this.messageBox(filePath + "·����δ�ҵ��ļ�");
    		return;
    	}
    	int fileCount = fileNames.length;
    	String fileName = "";
    	
    	for (int i = 0; i < fileCount; i++) {
    		// �ҵ������뵥�ŵ��ļ�
    		if (fileNames[i].contains("REF_" + selectedParm.getValue("REFERRAL_NO"))) {
    			fileName = fileNames[i];
    			break;
    		}
    	}
    	
    	if (StringUtils.isEmpty(fileName)) {
    		this.messageBox("δ�ҵ�ָ���ļ�");
    		return;
    	}
    	
		byte data[] = TIOM_FileServer.readFile(socket, filePath + "\\" + fileName);
		if (data == null) {
			messageBox("��������û���ҵ��ļ� " + filePath + "\\" + fileName);
			return;
		}
		
    	try {
    		FileTool.setByte(tempPath + "\\" + fileName, data);
    		// ����cmd���ļ�
			Runtime.getRuntime().exec("cmd.exe /c start "+ tempPath + "\\" + fileName);
		} catch (IOException e) {
			this.messageBox("���ļ�����");
			err("��ת��HL7�ļ�����:" + e.getMessage());
		}
    }
    
    /**
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryByMrNo() {
		// ȡ�ò�����
		String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
		if (StringUtils.isEmpty(mrNo)) {
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("���޴˲�����");
				return;
			}
			
			// modify by huangtt 20160928 EMPI���߲�����ʾ start
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());

			}
			// modify by huangtt 20160928 EMPI���߲�����ʾ end
			
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.onQuery();
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
