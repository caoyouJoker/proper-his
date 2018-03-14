package com.javahis.ui.cpc;

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
 * 胸痛转归 wuxy 
 * @author his
 *
 */
public class CPCOutcomControl extends TControl {
	
	private TComboBox getTComboBox(String tagName) {
	        return (TComboBox) getComponent(tagName);
	}
	TParm allParm ;
	
	public void onInit(){
		super.onInit();
		Object obj = this.getParameter();
        if(obj == null){
            this.messageBox_("参数不可为空");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        closeW();//关闭窗口
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
        //this.messageBox(allParm.getValue("CASE_NO"));
        
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
		TParm dataParm = getCPCOutComeData();
		if (dataParm.getErrCode() == -1) {
			return;
		}
 
		if (amiAdmRecordParm.getCount() > 0) {
			TParm resultErdDBParm = EMRAMITool.getInstance().updateAmiAdmRecordData(dataParm);
			if (resultErdDBParm.getErrCode() < 0) {	 
				this.messageBox("记录保存失败！");
				return;	
			}	
		} else {
			TParm resultErdDBParm = EMRAMITool.getInstance().insertAmiAdmRecordData(dataParm);
			if (resultErdDBParm.getErrCode() < 0) {	 
				this.messageBox("记录保存失败！");
				return;	
			}			
		}
		
		
		

        this.messageBox("P0005");
		
	}
	
	private TParm getCPCOutComeData() {
		TParm parm = new TParm();
		
		String CASE_NO = allParm.getValue("CASE_NO");
		String CPCOutcome = this.getValueString("CPCOutcome");
		String LEAVE_REASON = this.getValueString("LEAVE_REASON");
		String CUT_OFF_TIME = this.getValueString("CUT_OFF_TIME");
		String HOSPITAL_NAME = this.getValueString("HOSPITAL_NAME");
		String DIE_TIME = this.getValueString("DIE_TIME");
		String DIE_REASON = this.getValueString("DIE_REASON");
		String DEAD_DESC = (String)this.getValue("tTextArea_0");
		
		if ("0".equals(CPCOutcome)) {
			this.messageBox("胸痛转归不能为空");
			parm.setErrCode(-1);
			parm.setErrText("参数错误");
			return parm;
		} else if ("1".equals(CPCOutcome)) {
			if ("0".equals(LEAVE_REASON)) {
				this.messageBox("如果出院不能为空");
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
			} else {
				CUT_OFF_TIME = CUT_OFF_TIME.replace(".0", "");
			}
			if ("".equals(HOSPITAL_NAME)) {
				this.messageBox("医院名称不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			}
		} else {
			if (StringUtils.isEmpty(DIE_TIME)) {
				this.messageBox("死亡时间不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			} else {
				DIE_TIME = DIE_TIME.replace(".0", "");
			}
			if ("0".equals(DIE_REASON)) {
				this.messageBox("死亡原因不能为空");
				parm.setErrCode(-1);
				parm.setErrText("参数错误");
				return parm;
			} else if ("2".equals(DIE_REASON)) {
				if ("".equals(DEAD_DESC)) {
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
		
		if ("0".equals(DIE_REASON)) {
			DIE_REASON = "";
		}
		parm.setData("DEAD_CARDIAC",DIE_REASON);
		parm.setData("DEAD_DESC",DEAD_DESC);
		parm.setData("OPT_USER",Operator.getID());
//		parm.setData("OPT_DATE",optDate);
		parm.setData("OPT_TERM",Operator.getIP());
		return parm;
	}
	
	
	/**
	 * 关闭窗口的方法
	 */
	private void closeW(){
        this.closeWindow();
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
