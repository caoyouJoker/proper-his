package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.reg.RegSaveTool;
import jdo.erd.ERDLevelTool;
import jdo.reg.REGTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TToolButton;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.database.MicroFieldControl;
import com.javahis.util.OdoUtil;

public class REGPreviewingWindowNonMenuControl extends TControl{

	private TWord word;
	private TParm parm;
	// 打开已经看诊的病患的结构化病历所需要的存储路径saveFilesword
	private String[] saveFiles;
	private String flg="";
	private String caseNo = "";
	private String mrNo = "";
	private String erdLevel = "";
	private boolean update = false;//true 表示只修改检伤  false表示新增数据保存

	
	public void onInit(){
		parm = (TParm) this.getParameter();
		//System.out.println("triageNo----------"+parm.getData("triageNo"));

		word = (TWord) this.getComponent("tWord_0");
		word.onNewFile();
		word.update();
		word.onOpen("JHW\\门（急）诊病历\\胸痛中心记录", "胸痛中心-院前信息", 2, false);
		//word.onOpen("JHW\\门（急）诊病历\\胸痛中心记录", "急诊护士站-胸痛急诊护士记录", 2, false);
		
		System.out.println("-------------------------------");
		System.out.println("mrNp-------------------------------"+parm.getData("mrNo"));
		//TParm data=RegSaveTool.getInstance().getPreFile(parm.getData("case_no").toString());
		
		TParm data=RegSaveTool.getInstance().getPreFile(parm.getData("triageNo").toString());
		
		System.out.println("-------------------------------"+data);
		//System.out.println("TRANSFER_HP_NAME====>"+data.getValue("TRANSFER_HP_NAME"));
		
		
		parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT", parm.getData("mrNo").toString());
		
		//System.out.println(data.getValue("TRANSFER_HP_NAME"));
		
		//word.onOpen("JHW\\门（急）诊病历\\胸痛中心记录", "急诊护士站-胸痛急诊护士记录", 2, false);
		
	    word.setCanEdit(true);
	    word.setWordParameter(parm);

	    SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");//
	    
		//Pat pat = Pat.onQueryByMrNo("000000003826");
	    Pat pat = Pat.onQueryByMrNo(parm.getData("mrNo").toString());
		word.setMicroField("姓名", pat.getName());
		word.setMicroField("性别", ("1".toString().equals(pat.getSexCode())?"男":"女"));
		word.setMicroField("年龄", OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate()));
		//word.setMicroField("生日", StringTool.getString(pat.getBirthday(),"yyyy年MM月dd日"));
		word.setMicroField("出生日期",  StringTool.getString(pat.getBirthday(), "yyyy/MM/dd"));
		word.setMicroField("联系人电话", pat.getContactsTel());
		
		if (data!=null){
		
		this.setCaptureValueArray("PAT_LOG_TIME",  StringTool.getString(data.getTimestamp("PAT_LOG_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("START_ADD",data.getData("START_ADD", 0).toString());
		this.setCaptureValueArray("START_TIME",StringTool.getString(data.getTimestamp("START_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		ESingleChoose AT_HOME=(ESingleChoose)word.findObject("AT_HOME", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if (data.getData("AT_HOME",0).toString().equals("1")){
			AT_HOME.setText("是");
		}else{
			AT_HOME.setText("否");
		}
		ESingleChoose CALL_HELP=(ESingleChoose)word.findObject("CALL_HELP", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选

		if (data.getData("CALL_HELP",0).toString().equals("1")){
			CALL_HELP.setText("有");
		}else if (data.getData("CALL_HELP",0).toString().equals("2")){
			CALL_HELP.setText("无");
		}else if (data.getData("CALL_HELP",0).toString().equals("3")){
			CALL_HELP.setText("本区域120");
		}else if (data.getData("CALL_HELP",0).toString().equals("4")){
			CALL_HELP.setText("外区120");
		}else if (data.getData("CALL_HELP",0).toString().equals("5")){
			CALL_HELP.setText("当地医疗机构");
		}
		
		
		this.setCaptureValueArray("CALL_HELP_TIME", StringTool.getString(data.getTimestamp("CALL_HELP_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		ECheckBoxChoose CNST_CHEST_PAIN=(ECheckBoxChoose)word.findObject("CNST_CHEST_PAIN", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("CNST_CHEST_PAIN",0).toString().toString().equals("Y")){
			CNST_CHEST_PAIN.setChecked(true);
		}else{
			CNST_CHEST_PAIN.setChecked(false);
		}
		ECheckBoxChoose INTER_CHEST_PAIN=(ECheckBoxChoose)word.findObject("INTER_CHEST_PAIN", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("INTER_CHEST_PAIN",0).toString().toString().equals("Y")){
			INTER_CHEST_PAIN.setChecked(true);
		}else{
			INTER_CHEST_PAIN.setChecked(false);
		}
		ECheckBoxChoose CHEST_PAIN_BETTER=(ECheckBoxChoose)word.findObject("CHEST_PAIN_BETTER", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("CHEST_PAIN_BETTER",0).toString().equals("Y")){
			CHEST_PAIN_BETTER.setChecked(true);
		}else{
			CHEST_PAIN_BETTER.setChecked(false);
		}
		ECheckBoxChoose ABDOMINAL_PAIN=(ECheckBoxChoose)word.findObject("ABDOMINAL_PAIN", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("ABDOMINAL_PAIN",0).toString().equals("Y")){
			ABDOMINAL_PAIN.setChecked(true);
		}else{
			ABDOMINAL_PAIN.setChecked(false);
		}
		ECheckBoxChoose HARD_BREATHE=(ECheckBoxChoose)word.findObject("HARD_BREATHE", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("HARD_BREATHE",0).toString().equals("Y")){
			HARD_BREATHE.setChecked(true);
		}else{
			HARD_BREATHE.setChecked(false);
		}
		ECheckBoxChoose SHOCK=(ECheckBoxChoose)word.findObject("SHOCK", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("SHOCK",0).toString().equals("Y")){
			SHOCK.setChecked(true);
		}else{
			SHOCK.setChecked(false);
		}
		ECheckBoxChoose CARDIAC_FAILURE=(ECheckBoxChoose)word.findObject("CARDIAC_FAILURE", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("CARDIAC_FAILURE",0).toString().equals("Y")){
			CARDIAC_FAILURE.setChecked(true);
		}else{
			CARDIAC_FAILURE.setChecked(false);
		}
		ECheckBoxChoose MALIGNANT_ARRHYTHMIA=(ECheckBoxChoose)word.findObject("MALIGNANT_ARRHYTHMIA", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("MALIGNANT_ARRHYTHMIA",0).toString().equals("Y")){
			MALIGNANT_ARRHYTHMIA.setChecked(true);
		}else{
			MALIGNANT_ARRHYTHMIA.setChecked(false);
		}
		ECheckBoxChoose CARDIOPULMONARY_RESUSCITAT=(ECheckBoxChoose)word.findObject("CARDIOPULMONARY_RESUSCITAT", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("CARDIOPULMONARY_RESUSCITAT",0).toString().equals("Y")){
			CARDIOPULMONARY_RESUSCITAT.setChecked(true);
		}else{
			CARDIOPULMONARY_RESUSCITAT.setChecked(false);
		}
		ECheckBoxChoose BLEED=(ECheckBoxChoose)word.findObject("BLEED", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("BLEED",0).toString().equals("Y")){
			BLEED.setChecked(true);
		}else{
			BLEED.setChecked(false);
		}
		
        ESingleChoose ERD_TYPE=(ESingleChoose)word.findObject("ERD_TYPE", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		
		if (data.getData("ERD_TYPE",0).toString().equals("1")){
			ERD_TYPE.setText("呼救(120或其它)出车");
		}else if (data.getData("ERD_TYPE",0).toString().equals("2")){
			ERD_TYPE.setText("转院");
		}else if (data.getData("ERD_TYPE",0).toString().equals("3")){
			ERD_TYPE.setText("自行来院");
		}else if (data.getData("ERD_TYPE",0).toString().equals("4")){
			ERD_TYPE.setText("院内发病");
		}
		
		ECheckBoxChoose AMBULANCE_120=(ECheckBoxChoose)word.findObject("AMBULANCE_120", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("AMBULANCE_120",0).toString().equals("Y")){
			AMBULANCE_120.setChecked(true);
		}else{
			AMBULANCE_120.setChecked(false);
		}
		ECheckBoxChoose AMBULANCE_IN=(ECheckBoxChoose)word.findObject("AMBULANCE_IN", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("AMBULANCE_IN",0).toString().equals("Y")){
			AMBULANCE_IN.setChecked(true);
		}else{
			AMBULANCE_IN.setChecked(false);
		}
		ECheckBoxChoose AMBULANCE_OUT=(ECheckBoxChoose)word.findObject("AMBULANCE_OUT", EComponent.CHECK_BOX_CHOOSE_TYPE);//checkbox
		if (data.getData("AMBULANCE_OUT",0).toString().equals("Y")){
			AMBULANCE_OUT.setChecked(true);
		}else{
			AMBULANCE_OUT.setChecked(false);
		}
		
		
		this.setCaptureValueArray("DR_ATTEN_TIME", StringTool.getString(data.getTimestamp("DR_ATTEN_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("DOOR_TIME", StringTool.getString(data.getTimestamp("DOOR_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("IN_ADMIT_TIME", StringTool.getString(data.getTimestamp("IN_ADMIT_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		
		ESingleChoose TRANSFER_NET=(ESingleChoose)word.findObject("TRANSFER_NET", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if (data.getData("TRANSFER_NET",0).toString().equals("1")){
			TRANSFER_NET.setText("是");
		}else{
			TRANSFER_NET.setText("否");
		}
		
		this.setCaptureValueArray("TRANSFER_HP_NAME", data.getData("TRANSFER_HP_NAME",0).toString());
		this.setCaptureValueArray("TRANSFER_DOOR_TIME_OUT", StringTool.getString(data.getTimestamp("TRANSFER_DOOR_TIME_OUT", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("TRANSFER_DECICE_TIME", StringTool.getString(data.getTimestamp("TRANSFER_DECICE_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("TRANSFER_AMBULANCE_TIME", StringTool.getString(data.getTimestamp("TRANSFER_AMBULANCE_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("TRANSFER_LEAVE_TIME", StringTool.getString(data.getTimestamp("TRANSFER_LEAVE_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("TRANSFER_DOOR_TIME_IN", StringTool.getString(data.getTimestamp("TRANSFER_DOOR_TIME_IN", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("TRANSFER_IN_ADMIT_TIME", StringTool.getString(data.getTimestamp("TRANSFER_IN_ADMIT_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		
		ESingleChoose TRANSFER_BY_ERD=(ESingleChoose)word.findObject("TRANSFER_BY_ERD", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if (data.getData("TRANSFER_BY_ERD",0).toString().equals("1")){
			TRANSFER_BY_ERD.setText("是");
		}else{
			TRANSFER_BY_ERD.setText("否");
		}
		
		ESingleChoose TRANSFER_TO_CCR=(ESingleChoose)word.findObject("TRANSFER_TO_CCR", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		
		if (data.getData("TRANSFER_TO_CCR",0).toString().equals("1")){
			TRANSFER_TO_CCR.setText("导管室");
		}else if (data.getData("TRANSFER_TO_CCR",0).toString().equals("2")){
			TRANSFER_TO_CCR.setText("CCU");
		}else if (data.getData("TRANSFER_TO_CCR",0).toString().equals("3")){
			TRANSFER_TO_CCR.setText("病房");
		}else if (data.getData("TRANSFER_TO_CCR",0).toString().equals("4")){
			TRANSFER_TO_CCR.setText("其他");
		}
		
		this.setCaptureValueArray("TRANSFER_ARRIVE_TIME", StringTool.getString(data.getTimestamp("TRANSFER_ARRIVE_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("SELF_DOOR_TIME", StringTool.getString(data.getTimestamp("SELF_DOOR_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("SELF_ADMIT_TIME", StringTool.getString(data.getTimestamp("SELF_ADMIT_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		
		ESingleChoose SELF_BY_CCU=(ESingleChoose)word.findObject("SELF_BY_CCU", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if (data.getData("SELF_BY_CCU",0).toString().equals("1")){
			SELF_BY_CCU.setText("是");
		}else{
			SELF_BY_CCU.setText("否");
		}
		
		this.setCaptureValueArray("SELF_CUU_ARRIVE_TIME", StringTool.getString(data.getTimestamp("SELF_CUU_ARRIVE_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		
		this.setCaptureValueArray("IN_HP_COME_DEPT", data.getData("IN_HP_COME_DEPT",0).toString());
		this.setCaptureValueArray("IN_HP_CONS_TIME", StringTool.getString(data.getTimestamp("IN_HP_CONS_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("IN_HP_LEAVE_DEPT", data.getData("IN_HP_LEAVE_DEPT",0).toString());
		
        ESingleChoose FIRST_MEDICAL_ORG=(ESingleChoose)word.findObject("FIRST_MEDICAL_ORG", EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		
		if (data.getData("FIRST_MEDICAL_ORG",0).toString().equals("1")){
			FIRST_MEDICAL_ORG.setText("其它医疗机构");
		}else if (data.getData("FIRST_MEDICAL_ORG",0).toString().equals("2")){
			FIRST_MEDICAL_ORG.setText("120");
		}else if (data.getData("FIRST_MEDICAL_ORG",0).toString().equals("3")){
			FIRST_MEDICAL_ORG.setText("本院急诊");
		}else if (data.getData("FIRST_MEDICAL_ORG",0).toString().equals("4")){
			FIRST_MEDICAL_ORG.setText("本院心内门诊");
		}else if (data.getData("FIRST_MEDICAL_ORG",0).toString().equals("5")){
			FIRST_MEDICAL_ORG.setText("其它科室");
		}
		
		this.setCaptureValueArray("FIRST_DR", data.getData("FIRST_DR",0).toString());
		this.setCaptureValueArray("FIRST_TIME", StringTool.getString(data.getTimestamp("FIRST_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		this.setCaptureValueArray("FIRST_OUT_ECG_TIME", StringTool.getString(data.getTimestamp("FIRST_OUT_ECG_TIME", 0),"yyyy/MM/dd HH:mm:ss"));
		
		}
	}
	
	/**
     * 设置抓取框
     * 
     * @param name String
     * @param value String
     */
    public void setCaptureValueArray(String name, String value) {
        ECapture ecap = this.word.findCapture(name);
        if (ecap == null) return;
        ecap.setFocusLast();
        ecap.clear();
        this.word.pasteString(value);
    }

	
	
}
