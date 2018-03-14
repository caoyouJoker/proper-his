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
 * Title: 转诊申请
 * </p>
 * 
 * <p>
 * Description: 转诊申请
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
	private TParm parameterParm; // 页面传输参数
	
	/**
     * 初始化方法
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
     * 初始化界面控件
     */
    private void onInitPageControl() {
    	// 当有传入参数时所有控件禁用
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
    	// 设定当前时间
    	setValue("REFERRAL_DATE", today);
    	TParm parm = new TParm();
    	parm.setData("HOSP_DESC", Operator.getHospitalCHNFullName());
    	TParm result = ADMReferralTool.getInstance().querySysTrnHosp(parm);
    	
    	if (result.getErrCode() < 0) {
    		this.messageBox("查询外转院所信息错误");
    		err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
    		return;
    	}
    	
    	if (result.getCount() < 1) {
    		this.messageBox("查无外转院所信息");
    		return;
    	} else {
    		// 申请院所
        	setValue("APPLY_HOSP_CODE", result.getValue("HOSP_CODE", 0));
    	}
    }
    
    /**
     * 病案号回车事件
     */
    public void onMrNoEnter() {
    	// 取得病案号
		String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
		if (StringUtils.isEmpty(mrNo)) {
			this.messageBox("请输入病案号");
			return;
		} else {
			Pat pat = Pat.onQueryByMrNo(mrNo);
			if (pat == null) {
				this.messageBox("查无此病案号");
				return;
			}
			// modify by huangtt 20160928 EMPI患者查重提示 start
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());

			}
			// modify by huangtt 20160928 EMPI患者查重提示 end
			
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", mrNo);
			this.setPatInfo();
		}
    }
    
    /**
     * 查询方法
     */
    public void onQuery() {
    	this.onMrNoEnter();
    }
    
    /**
     * 保存方法
     */
    public void onSave() {
    	if (this.validate()) {
    		TParm checkParm = new TParm();
    		checkParm.setData("CASE_NO", patInfoParm.getValue("CASE_NO"));
    		checkParm.setData("ACCEPT_HOSP_CODE", getValue("ACCEPT_HOSP_CODE"));
    		checkParm.setData("CANCEL_FLG", "N");
    		
    		// 验证该病人转诊数据是否已经存在，避免重复发送
    		checkParm = ADMReferralTool.getInstance().queryAdmReferralOut(checkParm);
    		
    		if (checkParm.getErrCode() < 0) {
    			this.messageBox("查询转诊转出数据错误");
	    		err("ERR:" + checkParm.getErrCode() + checkParm.getErrText()
						+ checkParm.getErrName());
	    		return;
    		}
    		
    		if (checkParm.getCount() > 0) {
    			this.messageBox("该病人转诊转出申请已经提交");
    			return;
    		}
    		
    		TParm saveParm = this.getSaveParmData();
    		// 将组装好的数据插入转诊申请表
			TParm result = ADMReferralTool.getInstance()
					.insertAdmReferralOut(saveParm);
			
			if (result.getErrCode() < 0) {
	    		this.messageBox("保存失败");
	    		err("ERR:" + result.getErrCode() + result.getErrText()
						+ result.getErrName());
	    		return;
	    	} else {
	    		this.messageBox("保存成功");
	    		// 发送转诊申请hl7消息
	    		this.sendMessage(patInfoParm, saveParm);
	    		// 发送转诊病历
	    		if (!transferRefEMRFile()) {
	    			this.messageBox("病历文件发送失败");
	    		}
	    	}
    	}
    }
    
    /**
     * 根据病案号或身份证号填充相关信息
     */
    private void setPatInfo() {
    	// 取得病案号
		String mrNo = this.getValueString("MR_NO").trim();
		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		TParm result = ADMReferralTool.getInstance().queryPatInfo(parm);
    	
		if (result.getErrCode() < 0) {
			this.messageBox("查询病人信息错误");
			err("ERR:" + result.getErrText());
			return;
		}
		
		if (result.getCount() < 1) {
			this.messageBox("查无该住院病人数据");
			return;
		}
		
		patInfoParm = result.getRow(0);
		// 取得最近一次住院的数据显示到申请界面
		setValueForParm(
				"MR_NO;PAT_NAME;SEX_CODE;IDNO;BIRTH_DATE;ADDRESS;CONTACTS_NAME;CONTACTS_TEL;IN_DATE;DS_DATE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE",
				patInfoParm);
		
		String phoneNumber = patInfoParm.getValue("TEL_HOME") == null ? patInfoParm
				.getValue("CELL_PHONE") : patInfoParm.getValue("TEL_HOME");
		// 联系电话
		setValue("PHONE_NUMBER", phoneNumber);
		patInfoParm.setData("PHONE_NUMBER", phoneNumber);
    }
    
	/**
	 * 获取保存数据
	 * 
	 * @return
	 */
	private TParm getSaveParmData() {
		TParm parm = new TParm();
		parm.setData("REFERRAL_NO", SystemTool.getInstance().getNo("ALL", "ADM", "REFERRAL_APPLY", "REFERRAL_APPLY")); // 转诊单号
		parm.setData("APPLY_HOSP_CODE", getValueString("APPLY_HOSP_CODE")); // 提出转诊申请医院代码
		parm.setData("APPLY_HOSP_DESC", getTextFormat("APPLY_HOSP_CODE").getText()); // 申请转诊医院名称
		parm.setData("ACCEPT_HOSP_CODE", getValueString("ACCEPT_HOSP_CODE")); // 接收转诊医院代码
		parm.setData("ACCEPT_HOSP_DESC", getTextFormat("ACCEPT_HOSP_CODE").getText()); // 接收转诊医院名称
		parm.setData("ADM_TYPE", "I"); // 门急住别(目前只支持住院)
		parm.setData("REFERRAL_DATE", getValueString("REFERRAL_DATE").substring(0, 10).replaceAll("-", "/")); // 转诊日期
		parm.setData("MR_NO", getValueString("MR_NO")); // 病案号
		parm.setData("CASE_NO", patInfoParm.getValue("CASE_NO")); // 就诊序号
		parm.setData("PAT_NAME", getValueString("PAT_NAME")); // 病患姓名
		String birthDate = patInfoParm.getValue("BIRTH_DATE");
		if (StringUtils.isNotEmpty(birthDate) && birthDate.length() >= 10) {
			birthDate = birthDate.substring(0, 10).replaceAll("-", "/");
		}
		parm.setData("BIRTH_DATE", birthDate); // 出生日期
		parm.setData("SEX", getComboBox("SEX_CODE").getSelectedName()); // 性别
		parm.setData("IDNO", patInfoParm.getValue("IDNO")); // 身份证号
		parm.setData("PHONE_NUMBER", patInfoParm.getValue("PHONE_NUMBER")); // 电话号码
		parm.setData("ADDRESS", getValueString("ADDRESS")); // 通讯地址
		parm.setData("CONTACTS_NAME", getValueString("CONTACTS_NAME")); // 紧急联络人
		parm.setData("CONTACTS_TEL", getValueString("CONTACTS_TEL")); // 紧急联络人电话
		String inDate = patInfoParm.getValue("IN_DATE");
		if (StringUtils.isNotEmpty(inDate) && inDate.length() >= 19) {
			inDate = inDate.substring(0, 19);
		}
		parm.setData("IN_DATE", inDate); // 入院日期
		String dsDate = patInfoParm.getValue("DS_DATE");
		if (StringUtils.isNotEmpty(dsDate) && dsDate.length() >= 19) {
			dsDate = dsDate.substring(0, 19);
		}
		parm.setData("DS_DATE", dsDate); // 出院日期
		parm.setData("DEPT_CODE", getValueString("DEPT_CODE")); // 科室
		parm.setData("DEPT_DESC", getTextFormat("DEPT_CODE").getText()); // 科室名称
		parm.setData("STATION_CODE", getValueString("STATION_CODE")); // 病区
		parm.setData("STATION_DESC", getTextFormat("STATION_CODE").getText()); // 病区名称
		parm.setData("VS_DR_CODE", getValueString("VS_DR_CODE")); // 经治医生
		parm.setData("VS_DR_NAME", getTextFormat("VS_DR_CODE").getText()); // 经治医师姓名
		parm.setData("ATTEND_DR_CODE", getValueString("ATTEND_DR_CODE")); // 主治医生
		parm.setData("ATTEND_DR_NAME", getTextFormat("ATTEND_DR_CODE").getText()); // 主治医师姓名
		parm.setData("REFERRAL_GROUNDS", getTextArea("REFERRAL_GROUNDS").getValue()); // 转诊事由
		parm.setData("DISEASE_SUMMARY", getTextArea("DISEASE_SUMMARY").getValue()); // 病情摘要
		parm.setData("APPLY_USER_CODE", Operator.getID()); // 申请人ID
		parm.setData("APPLY_USER_NAME", Operator.getName()); // 申请人姓名
		parm.setData("CANCEL_FLG", "N"); // 取消注记
		parm.setData("OPT_USER", Operator.getID()); // 操作人员
		parm.setData("OPT_TERM", Operator.getIP()); // 操作端末
		return parm;
	}
    
    /**
     * 校验数据合法性
     */
    private boolean validate() {
		if (StringUtils.isEmpty(getValueString("MR_NO").trim())) {
			this.messageBox("病案号不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("PAT_NAME").trim())) {
			this.messageBox("姓名不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("REFERRAL_DATE").trim())) {
			this.messageBox("转诊日期不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("APPLY_HOSP_CODE").trim())) {
			this.messageBox("转出院所不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(getValueString("ACCEPT_HOSP_CODE").trim())) {
			this.messageBox("转入院所不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(getTextArea("REFERRAL_GROUNDS").getValue().trim())) {
			this.messageBox("转诊事由不能为空");
			return false;
		}
		
		if (StringUtils.isEmpty(getTextArea("DISEASE_SUMMARY").getValue().trim())) {
			this.messageBox("病情摘要不能为空");
			return false;
		}
    	
    	return true;
    }
    
    /**
     * 清空方法
     */
    public void onClear() {
    	patInfoParm = new TParm();
		clearValue("MR_NO;PAT_NAME;SEX_CODE;IDNO;BIRTH_DATE;ADDRESS;CONTACTS_NAME;CONTACTS_TEL;IN_DATE;DS_DATE;DEPT_CODE;STATION_CODE;VS_DR_CODE;ATTEND_DR_CODE;PHONE_NUMBER;ACCEPT_HOSP_CODE;REFERRAL_GROUNDS;DISEASE_SUMMARY");
		this.onInitPageControl();
    }
    
    /**
	 * 传送转诊申请消息
	 * 
	 * @param parm 病患信息
	 * @param refParm 病患转诊信息
	 */
	private void sendMessage(TParm parm, TParm refParm) {
		// 转诊
		String type = "ADM_REF";
		List list = new ArrayList();
		// 为避免hl7公共方法将科室用中文覆盖
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
     * 转诊病历传输
     */
    private boolean transferRefEMRFile() {
		TSocket readSocket = TIOM_FileServer.getSocket();
		TSocket sendSocket = TIOM_FileServer.getSocket();
		
		TParm queryParm = new TParm();
		queryParm.setData("HOSP_CODE", getValueString("ACCEPT_HOSP_CODE"));
		// 查询外转院所信息取得对应发送ip以及端口号
		TParm sendSocketParm = ADMReferralTool.getInstance().querySysTrnHosp(queryParm);
		
		if (sendSocketParm.getErrCode() < 0) {
			this.messageBox("查询外转院所信息错误");
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
		// 查询该病患病历信息
		TParm result = ADMReferralTool.getInstance().queryEMRFileIndex(parm);
		
		if (result.getErrCode() < 0) {
			err("ERR:" + result.getErrCode() + result.getErrText()
					+ result.getErrName());
			this.messageBox("查询病历信息错误");
			return false;
		}
		
		String refFileRoot = TIOM_FileServer.getRoot();
		if (StringUtils.isNotEmpty(sendSocketParm.getValue("REF_FILE_ROOT", 0))) {
			refFileRoot = sendSocketParm.getValue("REF_FILE_ROOT", 0) + "/";
		}
		
		// 文件读取路径
		String readTarget = TIOM_FileServer.getRoot()
				+ TIOM_FileServer.getPath("EmrData")
				+ result.getValue("FILE_PATH", 0);
		// 文件发送路径
		String sendTarget = refFileRoot + TIOM_FileServer.getPath("RefEmrData")
				+ result.getValue("FILE_PATH", 0);
		// 创建目标文件夹
		TIOM_FileServer.mkdir(sendSocket, sendTarget);
		
		// 读取该病人本次就诊的所有病历
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
			// 将读取的文件内容写入目标文件夹
			sendFlg = TIOM_FileServer.writeFile(sendSocket, sendFileName, fileByte);
			if (sendFlg) {
				resultFlg = true;
			}
		}
		
		return resultFlg;
    }
    
    /**
     * 得到性别Code
     * @param value
     * @return
     */
    private String getSexCode(String value) {
    	if (StringUtils.equals("男", value)) {
    		return "1";
    	} else if (StringUtils.equals("女", value)) {
    		return "2";
    	} else if (StringUtils.equals("未说明", value)) {
    		return "9";
    	}
    	
    	return "0";
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
	 * 得到TextArea对象
	 * 
	 * @param tagName
	 *            元素TAG名称
	 * @return
	 */
	private TTextArea getTextArea(String tagName) {
		return (TTextArea) getComponent(tagName);
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
