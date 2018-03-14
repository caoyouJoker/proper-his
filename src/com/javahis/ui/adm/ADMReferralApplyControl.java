package com.javahis.ui.adm;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import jdo.adm.ADMReferralTool;
import jdo.hl7.Hl7Communications;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextArea;
import com.dongyang.ui.TTextFormat;
import com.javahis.util.StringUtil;

/**
 * <p>
 * Title: ת������
 * </p>
 * 
 * <p>
 * Description: ת������
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
public class ADMReferralApplyControl extends TControl {
	
	private TParm patInfoParm;
	private TParm parameterParm; // ҳ�洫�����
	
	/**
     * ��ʼ������
     */
    public void onInit() {
    	super.onInit();
    	
    	Object obj = this.getParameter();
		if (null != obj) {
			if (obj instanceof TParm) {
				this.parameterParm = (TParm) obj;
			}
		}
    	
    	this.onInitPageControl();
    }
    
    /**
     * ��ʼ������ؼ�
     */
    private void onInitPageControl() {
    	// ���д������ʱ���пؼ�����
    	if (null != parameterParm) {
    		callFunction("UI|save|setEnabled", false);
    		callFunction("UI|query|setEnabled", false);
    		callFunction("UI|clear|setEnabled", false);
    		
			setValueForParm(
					"MR_NO;PAT_NAME;SEX_CODE;IDNO;BIRTH_DATE;ADDRESS;CONTACTS_NAME;CONTACTS_TEL;IN_DATE;DS_DATE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE;PHONE_NUMBER;ACCEPT_HOSP_CODE;REFERRAL_GROUNDS;DISEASE_SUMMARY;REFERRAL_DATE;APPLY_HOSP_CODE",
					parameterParm);
			getComboBox("SEX_CODE").setSelectedID(this.getSexCode(parameterParm.getValue("SEX")));
    		
    		callFunction("UI|MR_NO|setEnabled", false);
    		callFunction("UI|REFERRAL_DATE|setEnabled", false);
    		callFunction("UI|ACCEPT_HOSP_CODE|setEnabled", false);
			getTextArea("REFERRAL_GROUNDS").getTextArea().setEditable(false);
    		getTextArea("DISEASE_SUMMARY").getTextArea().setEditable(false);
    		return;
    	}
    	
    	Timestamp today = SystemTool.getInstance().getDate();
    	// �趨��ǰʱ��
    	setValue("REFERRAL_DATE", today);
    	TParm parm = new TParm();
    	parm.setData("HOSP_DESC", Operator.getHospitalCHNFullName());
    	TParm result = ADMReferralTool.getInstance().querySysTrnHosp(parm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("��ѯ��תԺ����Ϣ����");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
    		return;
    	}
    	
    	if (result.getCount() < 1) {
    		this.messageBox("������תԺ����Ϣ");
    		return;
    	} else {
    		// ����Ժ��
        	setValue("APPLY_HOSP_CODE", result.getValue("HOSP_CODE", 0));
    	}
    }
    
    /**
     * �����Żس��¼�
     */
    public void onMrNoEnter() {
    	// ȡ�ò�����
		String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
		if (StringUtils.isEmpty(mrNo)) {
			this.messageBox("�����벡����");
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
			this.setPatInfo();
		}
    }
    
    /**
     * ��ѯ����
     */
    public void onQuery() {
    	this.onMrNoEnter();
    }
    
    /**
     * ���淽��
     */
    public void onSave() {
    	if (this.validate()) {
    		TParm checkParm = new TParm();
    		checkParm.setData("CASE_NO", patInfoParm.getValue("CASE_NO"));
    		checkParm.setData("ACCEPT_HOSP_CODE", getValue("ACCEPT_HOSP_CODE"));
    		checkParm.setData("CANCEL_FLG", "N");
    		
    		// ��֤�ò���ת�������Ƿ��Ѿ����ڣ������ظ�����
    		checkParm = ADMReferralTool.getInstance().queryAdmReferralOut(checkParm);
    		
    		if (checkParm.getErrCode() < 0) {
    			this.messageBox("��ѯת��ת�����ݴ���");
	    		err("ERR:" + checkParm.getErrCode() + checkParm.getErrText()
						+ checkParm.getErrName());
	    		return;
    		}
    		
    		if (checkParm.getCount() > 0) {
    			this.messageBox("�ò���ת��ת�������Ѿ��ύ");
    			return;
    		}
    		
    		TParm saveParm = this.getSaveParmData();
    		// ����װ�õ����ݲ���ת�������
			TParm result = ADMReferralTool.getInstance()
					.insertAdmReferralOut(saveParm);
			
			if (result.getErrCode() < 0) {
	    		this.messageBox("����ʧ��");
	    		err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
	    		return;
	    	} else {
	    		this.messageBox("����ɹ�");
	    		// ����ת������hl7��Ϣ
	    		this.sendMessage(patInfoParm, saveParm);
	    		// ����ת�ﲡ��
	    		if (!transferRefEMRFile()) {
	    			this.messageBox("�����ļ�����ʧ��");
	    		}
	    	}
    	}
    }
    
    /**
     * ���ݲ����Ż����֤����������Ϣ
     */
    private void setPatInfo() {
    	// ȡ�ò�����
		String mrNo = this.getValueString("MR_NO").trim();
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		TParm result = ADMReferralTool.getInstance().queryPatInfo(parm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("��ѯ������Ϣ����");
			err("ERR:" + result.getErrText());
			return;
		}
		
		if (result.getCount() < 1) {
			this.messageBox("���޸�סԺ��������");
			return;
		}
		
		patInfoParm = result.getRow(0);
		// ȡ�����һ��סԺ��������ʾ���������
		setValueForParm(
				"MR_NO;PAT_NAME;SEX_CODE;IDNO;BIRTH_DATE;ADDRESS;CONTACTS_NAME;CONTACTS_TEL;IN_DATE;DS_DATE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE",
				patInfoParm);
		
		String phoneNumber = patInfoParm.getValue("TEL_HOME") == null ? patInfoParm
				.getValue("CELL_PHONE") : patInfoParm.getValue("TEL_HOME");
		// ��ϵ�绰
		setValue("PHONE_NUMBER", phoneNumber);
		patInfoParm.setData("PHONE_NUMBER", phoneNumber);
    }
    
	/**
	 * ��ȡ��������
	 * 
	 * @return
	 */
	private TParm getSaveParmData() {
		TParm parm = new TParm();
		parm.setData("REFERRAL_NO", SystemTool.getInstance().getNo("ALL", "ADM", "REFERRAL_APPLY", "REFERRAL_APPLY")); // ת�ﵥ��
		parm.setData("APPLY_HOSP_CODE", getValueString("APPLY_HOSP_CODE")); // ���ת������ҽԺ����
		parm.setData("APPLY_HOSP_DESC", getTextFormat("APPLY_HOSP_CODE").getText()); // ����ת��ҽԺ����
		parm.setData("ACCEPT_HOSP_CODE", getValueString("ACCEPT_HOSP_CODE")); // ����ת��ҽԺ����
		parm.setData("ACCEPT_HOSP_DESC", getTextFormat("ACCEPT_HOSP_CODE").getText()); // ����ת��ҽԺ����
		parm.setData("ADM_TYPE", "I"); // �ż�ס��(Ŀǰֻ֧��סԺ)
		parm.setData("REFERRAL_DATE", getValueString("REFERRAL_DATE").substring(0, 10).replaceAll("-", "/")); // ת������
		parm.setData("MR_NO", getValueString("MR_NO")); // ������
		parm.setData("CASE_NO", patInfoParm.getValue("CASE_NO")); // �������
		parm.setData("PAT_NAME", getValueString("PAT_NAME")); // ��������
		String birthDate = patInfoParm.getValue("BIRTH_DATE");
		if (StringUtils.isNotEmpty(birthDate) && birthDate.length() >= 10) {
			birthDate = birthDate.substring(0, 10).replaceAll("-", "/");
		}
		parm.setData("BIRTH_DATE", birthDate); // ��������
		parm.setData("SEX", getComboBox("SEX_CODE").getSelectedName()); // �Ա�
		parm.setData("IDNO", patInfoParm.getValue("IDNO")); // ���֤��
		parm.setData("PHONE_NUMBER", patInfoParm.getValue("PHONE_NUMBER")); // �绰����
		parm.setData("ADDRESS", getValueString("ADDRESS")); // ͨѶ��ַ
		parm.setData("CONTACTS_NAME", getValueString("CONTACTS_NAME")); // ����������
		parm.setData("CONTACTS_TEL", getValueString("CONTACTS_TEL")); // ���������˵绰
		String inDate = patInfoParm.getValue("IN_DATE");
		if (StringUtils.isNotEmpty(inDate) && inDate.length() >= 19) {
			inDate = inDate.substring(0, 19);
		}
		parm.setData("IN_DATE", inDate); // ��Ժ����
		String dsDate = patInfoParm.getValue("DS_DATE");
		if (StringUtils.isNotEmpty(dsDate) && dsDate.length() >= 19) {
			dsDate = dsDate.substring(0, 19);
		}
		parm.setData("DS_DATE", dsDate); // ��Ժ����
		parm.setData("DEPT_CODE", getValueString("DEPT_CODE")); // ����
		parm.setData("DEPT_DESC", getTextFormat("DEPT_CODE").getText()); // ��������
		parm.setData("STATION_CODE", getValueString("STATION_CODE")); // ����
		parm.setData("STATION_DESC", getTextFormat("STATION_CODE").getText()); // ��������
		parm.setData("VS_DR_CODE", getValueString("VS_DR_CODE")); // ����ҽ��
		parm.setData("VS_DR_NAME", getTextFormat("VS_DR_CODE").getText()); // ����ҽʦ����
		parm.setData("ATTEND_DR_CODE", getValueString("ATTEND_DR_CODE")); // ����ҽ��
		parm.setData("ATTEND_DR_NAME", getTextFormat("ATTEND_DR_CODE").getText()); // ����ҽʦ����
		parm.setData("REFERRAL_GROUNDS", getTextArea("REFERRAL_GROUNDS").getValue()); // ת������
		parm.setData("DISEASE_SUMMARY", getTextArea("DISEASE_SUMMARY").getValue()); // ����ժҪ
		parm.setData("APPLY_USER_CODE", Operator.getID()); // ������ID
		parm.setData("APPLY_USER_NAME", Operator.getName()); // ����������
		parm.setData("CANCEL_FLG", "N"); // ȡ��ע��
		parm.setData("OPT_USER", Operator.getID()); // ������Ա
		parm.setData("OPT_TERM", Operator.getIP()); // ������ĩ
		return parm;
	}
    
    /**
     * У�����ݺϷ���
     */
    private boolean validate() {
		if (StringUtils.isEmpty(getValueString("MR_NO").trim())) {
			this.messageBox("�����Ų���Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("PAT_NAME").trim())) {
			this.messageBox("��������Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("REFERRAL_DATE").trim())) {
			this.messageBox("ת�����ڲ���Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("APPLY_HOSP_CODE").trim())) {
			this.messageBox("ת��Ժ������Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("ACCEPT_HOSP_CODE").trim())) {
			this.messageBox("ת��Ժ������Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(getTextArea("REFERRAL_GROUNDS").getValue().trim())) {
			this.messageBox("ת�����ɲ���Ϊ��");
			return false;
		}
		
		if (StringUtils.isEmpty(getTextArea("DISEASE_SUMMARY").getValue().trim())) {
			this.messageBox("����ժҪ����Ϊ��");
			return false;
		}
    	
    	return true;
    }
    
    /**
     * ��շ���
     */
    public void onClear() {
    	patInfoParm = new TParm();
		clearValue("MR_NO;PAT_NAME;SEX_CODE;IDNO;BIRTH_DATE;ADDRESS;CONTACTS_NAME;CONTACTS_TEL;IN_DATE;DS_DATE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE;PHONE_NUMBER;ACCEPT_HOSP_CODE;REFERRAL_GROUNDS;DISEASE_SUMMARY");
		this.onInitPageControl();
    }
    
    /**
	 * ����ת��������Ϣ
	 * 
	 * @param parm ������Ϣ
	 * @param refParm ����ת����Ϣ
	 */
	private void sendMessage(TParm parm, TParm refParm) {
		// ת��
		String type = "ADM_REF";
		List list = new ArrayList();
		// Ϊ����hl7�������������������ĸ���
		refParm.setData("DEPT_ID", refParm.getValue("DEPT_CODE"));
		refParm.setData("OUT_DATE", refParm.getValue("DS_DATE"));
		refParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		refParm.setData("APPLICATION_NO", "REF_" + refParm.getValue("REFERRAL_NO"));
		
		String region = Operator.getRegion();
		if (StringUtils.equals("H01", region)) {
			region = "H02";
		} else if (StringUtils.equals("H02", region)) {
			region = "H01";
		}
		
		parm.setData("SEND_COMP", region);
		list.add(parm);
		list.add(refParm);
		TParm resultParm = Hl7Communications.getInstance().Hl7MessageCIS(list,
				type);
		if (resultParm.getErrCode() < 0) {
			messageBox(resultParm.getErrText());
		}
	}
	
	/**
     * ת�ﲡ������
     */
    private boolean transferRefEMRFile() {
		TSocket readSocket = TIOM_FileServer.getSocket();
		TSocket sendSocket = TIOM_FileServer.getSocket();
		
		TParm queryParm = new TParm();
		queryParm.setData("HOSP_CODE", getValueString("ACCEPT_HOSP_CODE"));
		// ��ѯ��תԺ����Ϣȡ�ö�Ӧ����ip�Լ��˿ں�
		TParm sendSocketParm = ADMReferralTool.getInstance().querySysTrnHosp(queryParm);
		
		if (sendSocketParm.getErrCode() < 0) {
			this.messageBox("��ѯ��תԺ����Ϣ����");
			err("ERR:" + sendSocketParm.getErrCode()
					+ sendSocketParm.getErrText() + sendSocketParm.getErrName());
			return false;
		}
		
		if (StringUtils.isNotEmpty(sendSocketParm.getValue("REF_IP", 0))
				&& StringUtils.isNotEmpty(sendSocketParm.getValue("REF_PORT", 0))) {
			sendSocket = new TSocket(sendSocketParm.getValue("REF_IP", 0),
					sendSocketParm.getInt("REF_PORT", 0));
		}
		
		TParm parm = new TParm();
		parm.setData("CASE_NO", patInfoParm.getValue("CASE_NO"));
		// ��ѯ�ò���������Ϣ
		TParm result = ADMReferralTool.getInstance().queryEMRFileIndex(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox("��ѯ������Ϣ����");
			return false;
		}
		
		String refFileRoot = TIOM_FileServer.getRoot();
		if (StringUtils.isNotEmpty(sendSocketParm.getValue("REF_FILE_ROOT", 0))) {
			refFileRoot = sendSocketParm.getValue("REF_FILE_ROOT", 0) + "/";
		}
		
		// �ļ���ȡ·��
		String readTarget = TIOM_FileServer.getRoot()
				+ TIOM_FileServer.getPath("EmrData")
				+ result.getValue("FILE_PATH", 0);
		// �ļ�����·��
		String sendTarget = refFileRoot + TIOM_FileServer.getPath("RefEmrData")
				+ result.getValue("FILE_PATH", 0);
		// ����Ŀ���ļ���
		TIOM_FileServer.mkdir(sendSocket, sendTarget);
		
		// ��ȡ�ò��˱��ξ�������в���
		String fileNames[] = TIOM_FileServer.listFile(readSocket, readTarget);
		int len = fileNames.length;
		String readFileName = "";
		String sendFileName = "";
		byte[] fileByte;
		boolean sendFlg = false;
		boolean resultFlg = false;
		
		for (int i = 0; i < len; i++) {
			readFileName = readTarget + "/" + fileNames[i];
			fileByte = TIOM_FileServer.readFile(readSocket, readFileName);
			if (fileByte == null || fileByte.length == 0) {
				continue;
			}
			
			sendFileName = sendTarget + "/" + fileNames[i];
			// ����ȡ���ļ�����д��Ŀ���ļ���
			sendFlg = TIOM_FileServer.writeFile(sendSocket, sendFileName, fileByte);
			if (sendFlg) {
				resultFlg = true;
			}
		}
		
		return resultFlg;
    }
    
    /**
     * �õ��Ա�Code
     * @param value
     * @return
     */
    private String getSexCode(String value) {
    	if (StringUtils.equals("��", value)) {
    		return "1";
    	} else if (StringUtils.equals("Ů", value)) {
    		return "2";
    	} else if (StringUtils.equals("δ˵��", value)) {
    		return "9";
    	}
    	
    	return "0";
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
	 * �õ�TextArea����
	 * 
	 * @param tagName
	 *            Ԫ��TAG����
	 * @return
	 */
	private TTextArea getTextArea(String tagName) {
		return (TTextArea) getComponent(tagName);
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
