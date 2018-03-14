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
 * ��ʹת�� wuxy 
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
            this.messageBox_("��������Ϊ��");
            SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    try {
                        closeW();//�رմ���
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
	//[[id,name],[0,],[1,��Ժ],[2,תԺ],[3,����]]
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
	 * ������ԭ��ļ����¼�
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
	 * ��շ���
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
	 * ���淽��
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
				this.messageBox("��¼����ʧ�ܣ�");
				return;	
			}	
		} else {
			TParm resultErdDBParm = EMRAMITool.getInstance().insertAmiAdmRecordData(dataParm);
			if (resultErdDBParm.getErrCode() < 0) {	 
				this.messageBox("��¼����ʧ�ܣ�");
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
			this.messageBox("��ʹת�鲻��Ϊ��");
			parm.setErrCode(-1);
			parm.setErrText("��������");
			return parm;
		} else if ("1".equals(CPCOutcome)) {
			if ("0".equals(LEAVE_REASON)) {
				this.messageBox("�����Ժ����Ϊ��");
				parm.setErrCode(-1);
				parm.setErrText("��������");
				return parm;
			}
		} else if ("2".equals(CPCOutcome)) {
			if (StringUtils.isEmpty(CUT_OFF_TIME)) {
				this.messageBox("����ʱ�䲻��Ϊ��");
				parm.setErrCode(-1);
				parm.setErrText("��������");
				return parm;
			} else {
				CUT_OFF_TIME = CUT_OFF_TIME.replace(".0", "");
			}
			if ("".equals(HOSPITAL_NAME)) {
				this.messageBox("ҽԺ���Ʋ���Ϊ��");
				parm.setErrCode(-1);
				parm.setErrText("��������");
				return parm;
			}
		} else {
			if (StringUtils.isEmpty(DIE_TIME)) {
				this.messageBox("����ʱ�䲻��Ϊ��");
				parm.setErrCode(-1);
				parm.setErrText("��������");
				return parm;
			} else {
				DIE_TIME = DIE_TIME.replace(".0", "");
			}
			if ("0".equals(DIE_REASON)) {
				this.messageBox("����ԭ����Ϊ��");
				parm.setErrCode(-1);
				parm.setErrText("��������");
				return parm;
			} else if ("2".equals(DIE_REASON)) {
				if ("".equals(DEAD_DESC)) {
					this.messageBox("����(����Դ��ʱ��д)����Ϊ��");
					parm.setErrCode(-1);
					parm.setErrText("��������");
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
	 * �رմ��ڵķ���
	 */
	private void closeW(){
        this.closeWindow();
    }
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
