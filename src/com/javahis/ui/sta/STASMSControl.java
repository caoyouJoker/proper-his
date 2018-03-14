package com.javahis.ui.sta;

import java.sql.Timestamp;

import jdo.sta.STASMSTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>Title: ҽ���ձ����ŷ���ƽ̨  </p>
 *
 * <p>Description: ҽ���ձ����ŷ���ƽ̨ </p>
 *
 * <p> Copyright: Copyright (c) 2014 </p>
 *
 * <p>Company:BlueCore </p>
 *
 * @author wangbin 2014.07.15
 * @version 1.0
 */
public class STASMSControl extends TControl {
	
	private TParm smsParm;

	/**
	 * ��ʼ���¼�
	 */
    public void onInit() {
        super.onInit();
        initData(); // ��ʼ������
    }
    
    private void initData() {
    	// ͳ������
    	Timestamp today = SystemTool.getInstance().getDate();
    	this.setValue("STA_DATE", today);
    	smsParm = new TParm();
    }
    
    /**
     * ѡ����ϵ��
     */
    public void onChooseUser() {
    	// ȡ�ص�������ѡ�����ϵ��
        TParm result =
                (TParm) this.openDialog("%ROOT%\\config\\sta\\STAPackageChoose.x", new TParm());
        if (result != null && result.getCount() > 0) {
        	smsParm = result;
            String nameList = "";
            for (int i = 0; i < smsParm.getCount(); i++) {
                nameList += smsParm.getValue("USER_NAME", i)+";";
            }
            nameList = nameList.substring(0, nameList.length() - 1);
            this.setValue("SMS_SEND_USERS", nameList);
        }
    }
    
    /**
     * ͳ��ҽ���ձ���Ϣ
     */
    public void onGenerate() {
    	// ͳ������
    	String staDate = this.getValueString("STA_DATE").split(" ")[0].replaceAll("-", "");
    	if (StringUtils.isEmpty(staDate)) {
    		this.messageBox("����дͳ������");
    		return;
    	}
    	
    	// ͳ��ҽ���ձ�����
    	TParm result = STASMSTool.getInstance().onGenerate(staDate);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox(result.getErrText());
    		return;
    	}
    	
    	// ��Ժ�����ܼ�
    	int admInCount = 0;
    	// ��Ժ�����ܼ�
    	int admOutCount = 0;
    	// ��Ժ�����ܼ�
    	int inHospitalPatCount = 0;
    	// Ժ�����������ܼ�
    	int regPatCount = 0;
    	// ��������
    	String emergencyCallCount = "";
    	// סԺ����ͳ������
    	StringBuffer inHospitalStr = new StringBuffer();
    	
    	int resultLen = result.getCount();
    	if (resultLen <= 0) {
    		this.messageBox("û��ͳ�Ƶ�����");
    		return;
    	}
    	
		for (int i = 0; i < resultLen; i++) {
			// ��Ժ�����ܼ�
			admInCount = admInCount + result.getInt("DATA_08", i);
			// ��Ժ�����ܼ�
			admOutCount = admOutCount + result.getInt("DATA_09", i);
			// ��Ժ�����ܼ�
			inHospitalPatCount = inHospitalPatCount + result.getInt("DATA_16", i);
			// ��Ժ�����ܼ�
			regPatCount = regPatCount + result.getInt("DATA_02", i);
			
			// ��������
			if (StringUtils.equals("���ﲿ", result.getValue("DEPT_CHN_DESC", i))) {
				emergencyCallCount = result.getValue("DATA_01", i);
			}
			
			// סԺ����
			if ("Y".equals(result.getValue("IPD_FIT_FLG", i))) {
				inHospitalStr.append(result.getValue("DEPT_CHN_DESC", i));
				inHospitalStr.append(":");
				inHospitalStr.append(result.getValue("DATA_16", i));
				inHospitalStr.append("\r\n");
			}
		}
		
    	// ��������
    	StringBuilder sbSmsMessage = new StringBuilder();
    	
    	sbSmsMessage.append("Ժ����������:").append(regPatCount).append("\r\n");
    	sbSmsMessage.append("��������:").append("#").append("\r\n");
    	sbSmsMessage.append("��Ժ����:").append(admInCount).append("\r\n");
    	sbSmsMessage.append("��Ժ����:").append(admOutCount).append("\r\n");
    	sbSmsMessage.append("�������:").append("\n");
    	sbSmsMessage.append("��������:").append("\n");
    	sbSmsMessage.append("��Ժ����:").append(inHospitalPatCount).append("\r\n");
    	sbSmsMessage.append("#");
    	sbSmsMessage.append("�ջ�:");
    	
		String smsContents = sbSmsMessage.toString().replaceFirst("#",
				emergencyCallCount).replaceFirst("#", inHospitalStr.toString());
		
    	this.setValue("SMS_SEND_CONTENTS", smsContents);
    }
    
    /**
     * ���
     */
    public void onClear() {
    	this.initData();
    	this.setValue("SMS_SEND_USERS", "");
    	this.setValue("SMS_SEND_CONTENTS", "");
    }
    
    /**
     * ���Ͷ���
     */
    public void onSendSMS() {
    	// ��֤��ϵ��
    	if (StringUtils.isEmpty(this.getValueString("SMS_SEND_USERS"))) {
    		this.messageBox("��ѡ����ϵ��");
    		return;
    	}
    	
    	// ��֤��������
    	if (this.getValueString("SMS_SEND_CONTENTS").trim().length() <= 0) {
    		this.messageBox("��༭��������");
    		return;
    	}
    	
    	// ����ҽ���ձ�����XML�̶���һ��ȡMrNo�ڶ���ȡName,Ϊ������ֿ��е����⴦��
    	String[] content = this.getValueString("SMS_SEND_CONTENTS").split("\n");
    	String tempMrNo = "";
    	String tempName = "";
    	String tempSendContent = "";
    	
    	if (content.length > 1) {
    		tempMrNo = content[0];
    		tempName = content[1];
    	} else {
    		tempMrNo = this.getValueString("SMS_SEND_CONTENTS");
    	}
    	
    	for (int i = 0; i < content.length; i++) {
    		if (i > 1) {
    			tempSendContent = tempSendContent + content[i];
    			if (i < content.length - 1) {
    				tempSendContent = tempSendContent + "\n";
    			}
    		}
    	}
    	
    	// ���ű���
    	smsParm.setData("Title", "ҽ���ձ�");
    	smsParm.setData("MrNo", tempMrNo);
    	smsParm.setData("Name", tempName);
    	smsParm.setData("Content", tempSendContent);
    	
    	TParm telParm = new TParm();
    	int parmLen = smsParm.getCount();
		for (int i = 0; i < parmLen; i++) {
			telParm.addData("TEL1", smsParm.getData("TEL", i));
		}
		
		// ���Ͷ���
		STASMSTool.getInstance().sendSMS(smsParm, telParm);
		this.messageBox("�������");
    }
    
    /**
     * nullת��Ϊ���ַ���
     */
    private String null2String(Object obj) {
    	if (ObjectUtils.equals(null, obj)) {
    		return "";
    	} else {
    		return obj.toString();
    	}
    }
}
