package com.javahis.ui.ami;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.SwingUtilities;

import jdo.adm.ADMInpTool;
import jdo.emr.EMRAMITool;
import jdo.sys.Operator;

import org.apache.commons.lang.StringUtils;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.jdo.TJDOTool;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.event.TComboBoxEvent;



/**
 * 胸痛转归 
 * @author WangQing 20170509
 *
 */
public class AMIOutcomControl extends TControl {

	private TComboBox getTComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}
	TParm allParm ;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox_("参数不可为空");
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						closeWindow();//关闭窗口
					}
					catch (Exception e) {
					}
				}
			});
			return;
		}
		if(obj instanceof TParm){
			allParm = (TParm)obj;
		}
		System.out.println("------allParm="+allParm);
		String dischCode = allParm.getValue("DISCH_CODE");

		if ("1".equals(dischCode)) {
			this.getTComboBox("CPCOutcome").setSelectedIndex(1);
			this.callFunction("UI|LEAVE_REASON|setEnabled",true);
			this.callFunction("UI|CUT_OFF_TIME|setEnabled",false);
			this.callFunction("UI|HOSPITAL_NAME|setEnabled",false);
			this.callFunction("UI|DIE_TIME|setEnabled",false);
			this.callFunction("UI|DIE_REASON|setEnabled",false);
			this.callFunction("UI|tTextArea_0|setVisible",false);
		} else if ("4".equals(dischCode)) {
			this.getTComboBox("CPCOutcome").setSelectedIndex(3);
			this.callFunction("UI|LEAVE_REASON|setEnabled",false);
			this.callFunction("UI|CUT_OFF_TIME|setEnabled",false);
			this.callFunction("UI|HOSPITAL_NAME|setEnabled",false);
			this.callFunction("UI|DIE_TIME|setEnabled",true);
			this.callFunction("UI|DIE_REASON|setEnabled",true);
		} else {
			this.callFunction("UI|LEAVE_REASON|setEnabled",false);
			this.callFunction("UI|CUT_OFF_TIME|setEnabled",false);
			this.callFunction("UI|HOSPITAL_NAME|setEnabled",false);
			this.callFunction("UI|DIE_TIME|setEnabled",false);
			this.callFunction("UI|DIE_REASON|setEnabled",false);
			this.callFunction("UI|tTextArea_0|setEnabled",false);
		}

		//		this.callFunction("UI|LEAVE_REASON|setEnabled",false);
		//		this.callFunction("UI|CUT_OFF_TIME|setEnabled",false);
		//		this.callFunction("UI|HOSPITAL_NAME|setEnabled",false);
		//		this.callFunction("UI|DIE_TIME|setEnabled",false);
		//		this.callFunction("UI|DIE_REASON|setEnabled",false);
		//		this.callFunction("UI|tTextArea_0|setEnabled",false);
		this.callFunction("UI|CPCOutcome|addEventListener",TComboBoxEvent.SELECTED, this, "onChangeValue");
		this.callFunction("UI|DIE_REASON|addEventListener",TComboBoxEvent.SELECTED, this, "dieReasonChange");
	} 
	//[[id,name],[0,],[1,出院],[2,转院],[3,死亡]]
	public void onChangeValue(){
		if (StringUtils.equals("1", getTComboBox("CPCOutcome").getSelectedID())) {
			this.callFunction("UI|LEAVE_REASON|setEnabled",true);
			this.callFunction("UI|CUT_OFF_TIME|setEnabled",false);
			this.setValue("CUT_OFF_TIME","");
			this.callFunction("UI|HOSPITAL_NAME|setEnabled",false);
			this.setValue("HOSPITAL_NAME","");
			this.callFunction("UI|DIE_TIME|setEnabled",false);
			this.setValue("DIE_TIME","");
			this.callFunction("UI|DIE_REASON|setEnabled",false);
			this.getTComboBox("DIE_REASON").setSelectedIndex(0);
			this.callFunction("UI|tTextArea_0|setVisible",false);
			this.setValue("tTextArea_0","");
		}
		if (StringUtils.equals("2", getTComboBox("CPCOutcome").getSelectedID())) {
			this.callFunction("UI|LEAVE_REASON|setEnabled",false);
			this.getTComboBox("LEAVE_REASON").setSelectedIndex(0);
			this.callFunction("UI|CUT_OFF_TIME|setEnabled",true);
			this.callFunction("UI|HOSPITAL_NAME|setEnabled",true);
			this.callFunction("UI|DIE_TIME|setEnabled",false);
			this.setValue("DIE_TIME","");
			this.callFunction("UI|DIE_REASON|setEnabled",false);
			this.getTComboBox("DIE_REASON").setSelectedIndex(0);
			this.callFunction("UI|tTextArea_0|setVisible",false);
			this.setValue("tTextArea_0","");
		}
		if (StringUtils.equals("3", getTComboBox("CPCOutcome").getSelectedID())) {
			this.callFunction("UI|LEAVE_REASON|setEnabled",false);
			this.getTComboBox("LEAVE_REASON").setSelectedIndex(0);
			this.callFunction("UI|CUT_OFF_TIME|setEnabled",false);
			this.setValue("CUT_OFF_TIME","");
			this.callFunction("UI|HOSPITAL_NAME|setEnabled",false);
			this.setValue("HOSPITAL_NAME","");
			this.callFunction("UI|DIE_TIME|setEnabled",true);
			this.callFunction("UI|DIE_REASON|setEnabled",true);
		}
	}

	/**
	 * 对死亡原因的监听事件
	 */
	public void dieReasonChange(){
		if(StringUtils.equals("1", getTComboBox("DIE_REASON").getSelectedID())){
			this.callFunction("UI|tTextArea_0|setVisible", false);
		}
		if(StringUtils.equals("2", getTComboBox("DIE_REASON").getSelectedID())){
			this.callFunction("UI|tTextArea_0|setVisible", true);
		}
	}

	/**
	 * 清空方法
	 */
	public void onClear(){

		this.getTComboBox("CPCOutcome").setSelectedIndex(0);
		this.getTComboBox("LEAVE_REASON").setSelectedIndex(0);
		this.getTComboBox("DIE_REASON").setSelectedIndex(0);
		this.setValue("CUT_OFF_TIME","");
		this.setValue("HOSPITAL_NAME","");
		this.setValue("DIE_TIME","");
		this.setValue("tTextArea_0","");
		onInit();

	}

	/**
	 * 保存方法
	 * @throws ParseException 
	 */
	public void onSave() throws ParseException{	
		String caseNo = allParm.getValue("CASE_NO");
		TParm amiAdmRecordParm = EMRAMITool.getInstance().getAmiAdmRecordDataByCaseNo(caseNo);
		System.out.println("------amiAdmRecordParm="+amiAdmRecordParm);
		TParm dataParm = getCPCOutComeData();
		if (dataParm.getErrCode() == -1) {
			return;
		}
		System.out.println("------dataParm="+dataParm);
		if (amiAdmRecordParm.getCount() > 0) {// update
			System.out.println("-------------------update-----------------");
//			TParm resultErdDBParm = EMRAMITool.getInstance().updateAmiAdmRecordData(dataParm);
//			
//			if (resultErdDBParm.getErrCode() < 0) {	 
//				this.messageBox("记录保存失败！");
//				return;	
//			}	
			boolean isSuccess = updateData();
			if(!isSuccess){
				this.messageBox("记录保存失败！");
				return;
			}
		} else {// insert
			System.out.println("-------------------insert-----------------");
//			TParm resultErdDBParm = EMRAMITool.getInstance().insertAmiAdmRecordData(dataParm);
//			if (resultErdDBParm.getErrCode() < 0) {	 
//				this.messageBox("记录保存失败！");
//				return;	
//			}	
			boolean isSuccess = insertData();
			if(!isSuccess){
				this.messageBox("记录保存失败！");
				return;
			}
		}
		this.messageBox("保存成功");
		this.closeWindow();
	}

	/**
	 * 获取页面数据
	 * @return
	 */
	private TParm getCPCOutComeData() {
		TParm parm = new TParm();

		String CASE_NO = allParm.getValue("CASE_NO");
		String CPCOutcome = this.getValueString("CPCOutcome");
		String LEAVE_REASON = this.getValueString("LEAVE_REASON");
		String CUT_OFF_TIME = this.getValueString("CUT_OFF_TIME").replace("-", "/").replace(".0", "");
		System.out.println("#########CUT_OFF_TIME="+CUT_OFF_TIME);
		
		String HOSPITAL_NAME = this.getValueString("HOSPITAL_NAME");
		String DIE_TIME = this.getValueString("DIE_TIME").replace("-", "/").replace(".0", "");
		String DIE_REASON = this.getValueString("DIE_REASON");
		String DEAD_DESC = this.getValueString("tTextArea_0");

		if ("0".equals(CPCOutcome) || "".equals(CPCOutcome)) {
			this.messageBox("胸痛转归不能为空");
			parm.setErrCode(-1);
			parm.setErrText("参数错误");
			return parm;
		} else if ("1".equals(CPCOutcome)||"".equals(CPCOutcome)) {
			if ("0".equals(LEAVE_REASON)||"".equals(LEAVE_REASON)) {
				this.messageBox("出院原因不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			}
		} else if ("2".equals(CPCOutcome)) {
			if (StringUtils.isEmpty(CUT_OFF_TIME)) {
				this.messageBox("交接时间不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			} 
			if (StringUtils.isEmpty(HOSPITAL_NAME)) {
				this.messageBox("医院名称不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			}
		} else if("3".equals(CPCOutcome)){
			if (StringUtils.isEmpty(DIE_TIME)) {
				this.messageBox("死亡时间不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			}
			if ("0".equals(DIE_REASON)||"".equals(DIE_REASON)) {
				this.messageBox("死亡原因不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			} else if ("2".equals(DIE_REASON)) {
				if (StringUtils.isEmpty(DEAD_DESC)) {
					this.messageBox("内容(非心源性时填写)不能为空");
					parm.setErrCode(-1);
					parm.setErrText("参数错误");
					return parm;
				}
			}
		}

		parm.setData("CASE_NO",CASE_NO);
		parm.setData("VEST_STATUS",CPCOutcome);
		parm.setData("OUT_STATUS",LEAVE_REASON);
		parm.setData("TRNS_TIME",CUT_OFF_TIME);
		parm.setData("TRNS_HP_NAME",HOSPITAL_NAME);
		parm.setData("DEAD_TIME",DIE_TIME);
		parm.setData("DEAD_CARDIAC",DIE_REASON);
		parm.setData("DEAD_DESC",DEAD_DESC);
		parm.setData("OPT_USER",Operator.getID());
		parm.setData("OPT_TERM",Operator.getIP());
		return parm;
	}

	/**
	 * 插入AMI_ADM_RECORD表
	 * @return
	 */
	private boolean insertData(){
		TParm dataParm = getCPCOutComeData();
		String insertSql = "INSERT INTO AMI_ADM_RECORD (CASE_NO, VEST_STATUS, "
				+ "OUT_STATUS, TRNS_TIME, TRNS_HP_NAME, DEAD_TIME, DEAD_CARDIAC, "
				+ "DEAD_DESC, OPT_USER, OPT_DATE, OPT_TERM) VALUES("
				+ "'"+dataParm.getValue("CASE_NO")
				+"', '"+dataParm.getValue("VEST_STATUS")
				+"', '"+dataParm.getValue("OUT_STATUS")
				+"', "+"to_date('" + dataParm.getValue("TRNS_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
			    +", '"+dataParm.getValue("TRNS_HP_NAME")
			    +"', "+"to_date('" + dataParm.getValue("DEAD_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
			    +", '"+dataParm.getValue("DEAD_CARDIAC")
			    +"', '"+dataParm.getValue("DEAD_DESC")
			    +"', '"+dataParm.getValue("OPT_USER")
			    +"',sysdate"
			    +", '"+dataParm.getValue("OPT_TERM")
			    +"')";
	    System.out.println("------insertSql="+insertSql);
		TParm result = new TParm(TJDODBTool.getInstance().update(insertSql));
		if(result.getErrCode()<0){
			return false;
		}
		return true;
		
	}
	
	private boolean updateData(){ 
		TParm dataParm = getCPCOutComeData();
		String updateSql = " UPDATE AMI_ADM_RECORD SET "
				+ "VEST_STATUS='"+dataParm.getValue("VEST_STATUS")
				+"', OUT_STATUS='"+dataParm.getValue("OUT_STATUS")
				+"', TRNS_TIME=to_date('" + dataParm.getValue("TRNS_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
				+", TRNS_HP_NAME='"+dataParm.getValue("TRNS_HP_NAME")
				+"', DEAD_TIME=to_date('" + dataParm.getValue("DEAD_TIME") + "','yyyy/MM/dd HH24:MI:SS')"
			    +", DEAD_CARDIAC='"+dataParm.getValue("DEAD_CARDIAC")
			    +"', DEAD_DESC='"+dataParm.getValue("DEAD_DESC")
			    +"', OPT_USER='"+dataParm.getValue("OPT_USER")
			    +"', OPT_DATE=sysdate"
			    +", OPT_TERM='"+dataParm.getValue("OPT_TERM")
			    +"' WHERE CASE_NO='"+ dataParm.getValue("CASE_NO")+"' ";
		System.out.println("------updateSql="+updateSql);
		TParm result = new TParm(TJDODBTool.getInstance().update(updateSql));
		if(result.getErrCode()<0){
			return false;
		}
		return true;
				
	}















}
