package com.javahis.ui.sta;

import java.sql.Timestamp;

import jdo.sta.STASMSTool;
import jdo.sys.SystemTool;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;

/**
 * <p>Title: 医疗日报短信发送平台  </p>
 *
 * <p>Description: 医疗日报短信发送平台 </p>
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
	 * 初始化事件
	 */
    public void onInit() {
        super.onInit();
        initData(); // 初始化数据
    }
    
    private void initData() {
    	// 统计日期
    	Timestamp today = SystemTool.getInstance().getDate();
    	this.setValue("STA_DATE", today);
    	smsParm = new TParm();
    }
    
    /**
     * 选择联系人
     */
    public void onChooseUser() {
    	// 取回弹出框中选择的联系人
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
     * 统计医疗日报信息
     */
    public void onGenerate() {
    	// 统计日期
    	String staDate = this.getValueString("STA_DATE").split(" ")[0].replaceAll("-", "");
    	if (StringUtils.isEmpty(staDate)) {
    		this.messageBox("请填写统计日期");
    		return;
    	}
    	
    	// 统计医疗日报数据
    	TParm result = STASMSTool.getInstance().onGenerate(staDate);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox(result.getErrText());
    		return;
    	}
    	
    	// 入院人数总计
    	int admInCount = 0;
    	// 出院人数总计
    	int admOutCount = 0;
    	// 在院人数总计
    	int inHospitalPatCount = 0;
    	// 院内门诊人数总计
    	int regPatCount = 0;
    	// 急诊人数
    	String emergencyCallCount = "";
    	// 住院科室统计数据
    	StringBuffer inHospitalStr = new StringBuffer();
    	
    	int resultLen = result.getCount();
    	if (resultLen <= 0) {
    		this.messageBox("没有统计到数据");
    		return;
    	}
    	
		for (int i = 0; i < resultLen; i++) {
			// 入院人数总计
			admInCount = admInCount + result.getInt("DATA_08", i);
			// 出院人数总计
			admOutCount = admOutCount + result.getInt("DATA_09", i);
			// 在院人数总计
			inHospitalPatCount = inHospitalPatCount + result.getInt("DATA_16", i);
			// 在院人数总计
			regPatCount = regPatCount + result.getInt("DATA_02", i);
			
			// 急诊人数
			if (StringUtils.equals("急诊部", result.getValue("DEPT_CHN_DESC", i))) {
				emergencyCallCount = result.getValue("DATA_01", i);
			}
			
			// 住院科室
			if ("Y".equals(result.getValue("IPD_FIT_FLG", i))) {
				inHospitalStr.append(result.getValue("DEPT_CHN_DESC", i));
				inHospitalStr.append(":");
				inHospitalStr.append(result.getValue("DATA_16", i));
				inHospitalStr.append("\r\n");
			}
		}
		
    	// 短信内容
    	StringBuilder sbSmsMessage = new StringBuilder();
    	
    	sbSmsMessage.append("院内门诊人数:").append(regPatCount).append("\r\n");
    	sbSmsMessage.append("急诊人数:").append("#").append("\r\n");
    	sbSmsMessage.append("入院人数:").append(admInCount).append("\r\n");
    	sbSmsMessage.append("出院人数:").append(admOutCount).append("\r\n");
    	sbSmsMessage.append("外科手术:").append("\n");
    	sbSmsMessage.append("介入诊疗:").append("\n");
    	sbSmsMessage.append("在院人数:").append(inHospitalPatCount).append("\r\n");
    	sbSmsMessage.append("#");
    	sbSmsMessage.append("普华:");
    	
		String smsContents = sbSmsMessage.toString().replaceFirst("#",
				emergencyCallCount).replaceFirst("#", inHospitalStr.toString());
		
    	this.setValue("SMS_SEND_CONTENTS", smsContents);
    }
    
    /**
     * 清空
     */
    public void onClear() {
    	this.initData();
    	this.setValue("SMS_SEND_USERS", "");
    	this.setValue("SMS_SEND_CONTENTS", "");
    }
    
    /**
     * 发送短信
     */
    public void onSendSMS() {
    	// 验证联系人
    	if (StringUtils.isEmpty(this.getValueString("SMS_SEND_USERS"))) {
    		this.messageBox("请选择联系人");
    		return;
    	}
    	
    	// 验证短信内容
    	if (this.getValueString("SMS_SEND_CONTENTS").trim().length() <= 0) {
    		this.messageBox("请编辑短信内容");
    		return;
    	}
    	
    	// 由于医疗日报分析XML固定第一行取MrNo第二行取Name,为避免出现空行的特殊处理
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
    	
    	// 短信标题
    	smsParm.setData("Title", "医疗日报");
    	smsParm.setData("MrNo", tempMrNo);
    	smsParm.setData("Name", tempName);
    	smsParm.setData("Content", tempSendContent);
    	
    	TParm telParm = new TParm();
    	int parmLen = smsParm.getCount();
		for (int i = 0; i < parmLen; i++) {
			telParm.addData("TEL1", smsParm.getData("TEL", i));
		}
		
		// 发送短信
		STASMSTool.getInstance().sendSMS(smsParm, telParm);
		this.messageBox("操作完毕");
    }
    
    /**
     * null转换为空字符串
     */
    private String null2String(Object obj) {
    	if (ObjectUtils.equals(null, obj)) {
    		return "";
    	} else {
    		return obj.toString();
    	}
    }
}
