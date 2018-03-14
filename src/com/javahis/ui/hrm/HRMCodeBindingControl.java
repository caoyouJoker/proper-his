/**
 * 
 */
package com.javahis.ui.hrm;


import jdo.hrm.HRMCodeBindingTool;


import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.ui.TTextField;

/**
 * <p>Title: 分装对照</p>
 *
 * <p>Description: 分装对照</p>
 *
 * <p>Copyright: Copyright (c) 2016</p>
 *
 * <p>Company: JavaHis </p>
 *
 *
 * @author Guangl
 * @version 1.0
 */
public class HRMCodeBindingControl extends TControl {
	
	TTextField application_no;
	TTextField osc_code1;
	TTextField osc_code2;
	TTextField mr_no;
	TTextField plan_code;
	TTextField plan_desc;
	TTextField pat_name;
	TTextField recruit_code;
//	TTextFormat exec_time;
	
	

	@Override
	public void onInit() {
		initPage();
	}

	private void initPage() {
		
		application_no =  (TTextField) this.getComponent("APPLICATION_NO");
		osc_code1 =  (TTextField) this.getComponent("OSC_CODE1");
		osc_code2 =  (TTextField) this.getComponent("OSC_CODE2");
//		application_no.setFocusLostAction("getRecruitInfo");
//		osc_code1.setFocusLostAction("getRecruitInfo");
//		osc_code2.setFocusLostAction("getRecruitInfo");
		
		mr_no = (TTextField) this.getComponent("MR_NO");
		plan_code = (TTextField) this.getComponent("PLAN_CODE");
		plan_desc = (TTextField) this.getComponent("PLAN_DESC");
		pat_name = (TTextField) this.getComponent("PAT_NAME");
		recruit_code = (TTextField) this.getComponent("RECRUIT_CODE");
		
//		exec_time = (TTextFormat) this.getComponent("EXEC_TIME");
//		exec_time.setValue(TJDODBTool.getInstance().getDBTime());
		this.grabFocus("APPLICATION_NO");
	}
	
	public void getRecruitInfo(){
		String applicationno = application_no.getValue();
		TParm info1 = HRMCodeBindingTool.getInstance().getOPDCodeByApplicationCode(applicationno);
		if(info1.getCount()<=0){
			messageBox("抱歉，查无信息！");
			return;
		}
		//Step1,MED_APPLY与ADM_RESV连接，获取健检就诊号 ，病案号，患者姓名，受试者招募号
		mr_no.setValue(info1.getValue("MR_NO",0));
		pat_name.setValue(info1.getValue("PAT_NAME",0));
		recruit_code.setValue(info1.getValue("RECRUIT_NO",0));
		String case_no;
		if("".equals(info1.getValue("OPD_CASE_NO",0))){
			case_no = info1.getValue("OPD_CASE_NO",0);
		}else{
			//多个就诊号只取第一个，用逗号分割，不能只取12位
			case_no = info1.getValue("OPD_CASE_NO",0).split(",")[0];
		}
		
		
		TParm info2 = HRMCodeBindingTool.getInstance().getPlanInfoByOPDCode(case_no);
		plan_code.setValue(info2.getValue("PLAN_NO",0));
		plan_desc.setValue(info2.getValue("PLAN_DESC",0));
		
		osc_code1.setValue(info1.getValue("OSC1",0));
		osc_code2.setValue(info1.getValue("OSC2",0));
		//取的护士计划执行时间
		TParm info3 = HRMCodeBindingTool.getInstance().getPlanExecTime(applicationno, info1.getValue("CASE_NO", 0));
		this.setValue("PLANEXEC_TIME", info3.getValue("DR_NOTE", 0));
		this.grabFocus("OSC_CODE1");
	}
	
	public void onClear(){
		application_no.setValue("");
		osc_code1.setValue("");
		osc_code2.setValue("");
		mr_no.setValue("");
		pat_name.setValue("");
		plan_code.setValue("");
		plan_desc.setValue("");
		recruit_code.setValue("");
		this.clearValue("PLANEXEC_TIME");
//		exec_time = (TTextFormat) this.getComponent("EXEC_TIME");
//		exec_time.setValue(TJDODBTool.getInstance().getDBTime());
		this.grabFocus("APPLICATION_NO");
	}
	
	public void onSave(){
		String applicationno = application_no.getValue();
		String osc1 = osc_code1.getValue();
		String osc2 = osc_code2.getValue();
		
		if("".equals(osc1)||"".equals(osc2)){
			messageBox("注意，waston条码不完全，绑定失败！");
			return;
		}
		if(HRMCodeBindingTool.getInstance().updateOSC(applicationno, osc1, osc2)){
			if(HRMCodeBindingTool.getInstance().insertOSCLog(applicationno, osc1, osc2)){
				messageBox("保存成功！");
				this.onClear();
				return;
			}
			messageBox("保存成功！");
			this.onClear();
		}else{
			messageBox("保存失败！");
		}
	}
	
	public void onQuery(){
		getRecruitInfo();
	}
}
