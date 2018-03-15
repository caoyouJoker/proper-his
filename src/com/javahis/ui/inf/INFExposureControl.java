package com.javahis.ui.inf;


import java.sql.Timestamp;

import javax.swing.JComboBox;

import jdo.sys.Operator;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;

/**
 * <p>Title:ְҵ��¶�Ǽǿ����� </p>
 *
 * <p>Dription:ְҵ��¶�ȼ������� </p>
 *
 * <p>Copyright: Copyright (c) 2017</p>
 *
 * <p>Company: ProperSoft </p>
 *
 * @author zhanglei 
 * @version 5.0
 */
public class INFExposureControl extends TControl {
	/**
	 *  ��Ų���
	 */
	private TParm Parameter;
	
	
	 /**
	 *��ʼ�� 
	 */
	public void onInit(){
		
		
		// ��ȡԤԼסԺ���洫��Ĳ���
		Object a = this.getParameter();
		
		if(a instanceof TParm){
			Object obj = this.getParameter();
			TParm parm = null;
			if (obj instanceof TParm){
				parm = (TParm) obj;
			}
			this.setValue("EXPOSURE_NO", parm.getValue("EXPOSURE_NO"));
			onQuery();	
		}else{
			onInitialization();
		}
	}
	/**
	 *��ʼ���������� 
	 */
	public void onInitialization(){
		//�õ���ǰʱ��
		Timestamp date = SystemTool.getInstance().getDate();
		//��ʼ����������
		setValue("OCCURRENCE_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//��ʼ����������
		setValue("REPORT_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//��ʼ�����Ÿ���������
		setValue("DEPARTMENT_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//��ʼ������������
		setValue("PREVENTION_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
		//��ʼ����Ⱦ������
		setValue("INFECTED_DATE",
                date.toString().substring(0, 19).
                replace('-', '/'));
	}
	/**
	 * ���Żس��¼�
	 */
	public void onUSERID(){
		
		String sql = " SELECT A.USER_NAME,A.SEX_CODE,B.DEPT_CODE "
				   + " FROM SYS_OPERATOR A,SYS_OPERATOR_DEPT B WHERE A.USER_ID='" + this.getValue("USER_ID") + "'"
				   + " AND A.USER_ID=B.USER_ID AND B.MAIN_FLG='Y'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //this.messageBox("" + parm);
        if (parm.getCount() < 0){
        	this.messageBox("û�в鵽�ù���Ա������ʵϰ������ʱ��ѡ��-ʵϰ");
        }
        setValue("PAT_NAME",parm.getValue("USER_NAME",0));
        setValue("SEX_CODE",parm.getValue("SEX_CODE",0));
        setValue("DEPT_CODE",parm.getValue("DEPT_CODE",0));
	}
	
	
	
	
	
	
	
	/**
	 * ʵϰ�¼�
	 */
	public void onInternship(){
		if(this.getValue("PAT_NAME").toString().length() > 0){
			this.messageBox("�������ѯ�������Ա���ٸı���ʱ���");
			setValue("INTERNSHIP", "N");
		}
		if (getValueString("INTERNSHIP").equals("N")){
			((TTextField) getComponent("PAT_NAME")).setEnabled(false);
			((TComboBox) getComponent("SEX_CODE")).setEnabled(false);
			((TTextFormat) getComponent("DEPT_CODE")).setEnabled(false);
		}
		else{
			((TTextField) getComponent("PAT_NAME")).setEnabled(true);
			((TComboBox) getComponent("SEX_CODE")).setEnabled(true);
			((TTextFormat) getComponent("DEPT_CODE")).setEnabled(true);
		}		
	}
	/**
	 * ��ѯ
	 * ���ݱ�¶��Ų�ѯ  
	 */
	public void onQuery(){
		
		//�ж�ҳ��ı�¶����û��12λ��ֵ
		
		if(this.getValue("EXPOSURE_NO").toString().length() == 12){
			
			String sql = " SELECT EXPOSURE_NO,PAT_NAME,BIRTH_DATE,SEX_CODE,DEPT_CODE,CELL_PHONE,WORKING_YEARS,PATIENT_MR_NO,PATIENT_DEPT_CODE,FIRST_INSPECTION_DATE,PATIENT_PASS_SCREENING,ANTIHIV,HBSAG,ANTIHBS,ANTIHCV,VDRL,PATIENT_ANTIHIV,PATIENT_HBSAG,PATIENT_ANTIHBS,PATIENT_ANTIHCV,PATIENT_VDRL,LNJECTION_NEEDLE,INDWELLING_NEEDLE,SCALP_ACUPUNCTURE,NEEDLE,VACUUM_BLOOD_COLLECTOR,SURGICAL_INSTRUMENTS,GLASS_ITEMS,OTHER_TYPE,OTHER_TYPE_DESCRIBE,BLOOD_COLLECTION,CATHETER_PLACEMENT,OPERATION,FORMULATED_REHYDRATION,INJECTION,EQUIPMENT,OTHER_OPERATION,OTHER_OPERATION_DESCRIBE,OPEN_NEEDLE,MISALIGNMENT_PUNCTURE,DOSING_TIME,BACK_SLEEVE,BENDING_BREAKING_NEEDLE,OTHERS_STABBED,PARTING_INSTRUMENT,CLEANING_ITEMS,PIERCING_BOX,HIDE_ITEMS,BREAK_USE,OTHER_ACTION,OTHER_ACTION_DESCRIBE,CONTACT_POLLUTION,WEAR_GLOVES,INJURED,INJURY_FREQUENCY,INJURY_TREATMENT,DEPARTMENT_HEADS,DEPARTMENT_DATE,PREVENTION_HEADS,PREVENTION_DATE,INFECTED_HEADS,INFECTED_DATE,OPT_USER,OPT_DATE,OPT_TERM,AGE,OCCURRENCE_DATE,PLACE_INJURY,POSITION_INJURY,USER_ID,INTERNSHIP,REPORT_DATE"
	                + " FROM INF_EXPOSURE where EXPOSURE_NO='" + getValue("EXPOSURE_NO") + "'";
	        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
	        //      
//	        this.messageBox("�����ѯ" + "AGE:" + parm.getValue("AGE") +
//	        		";OCCURRENCE_DATE:" + parm.getValue("OCCURRENCE_DATE") + ";PLACE_INJURY:" + parm.getValue("PLACE_INJURY") + 
//	        		";POSITION_INJURY:" + parm.getValue("POSITION_INJURY") + ";OTHER_ACTION:" + parm.getValue("OTHER_ACTION"));
	       // this.messageBox("��ѯ��¶����Ƿ����:" + parm);
	        if (parm.getCount("EXPOSURE_NO") < 0){
	        	this.messageBox("û�в鵽��Ӧ������");
	        }
	        parm = parm.getRow(0);
	        
	        setValue("INTERNSHIP",parm.getValue("INTERNSHIP"));
	        setValue("USER_ID",parm.getValue("USER_ID"));
	        //System.out.println("parm::::::" + parm);
	        //this.messageBox("1111111111");
	        setValue("AGE",parm.getValue("AGE"));
	        //this.messageBox("222222");
	        setValue("PAT_NAME",parm.getValue("PAT_NAME"));
	        //this.messageBox("33333");
	        setValue("SEX_CODE",parm.getValue("SEX_CODE"));
	        //this.messageBox("44444");
	        setValue("DEPT_CODE",parm.getValue("DEPT_CODE"));
	        //this.messageBox("55555");
	        setValue("CELL_PHONE",parm.getValue("CELL_PHONE"));
	        //this.messageBox("66666");
	        setValue("WORKING_YEARS",parm.getValue("WORKING_YEARS"));
	        //this.messageBox("77777");
	        setValue("PATIENT_MR_NO",parm.getValue("PATIENT_MR_NO"));
	        //this.messageBox("8");
	        setValue("PATIENT_DEPT_CODE",parm.getValue("PATIENT_DEPT_CODE"));
	        //this.messageBox("9");
	        //this.messageBox("" + parm.getValue("FIRST_INSPECTION_DATE").toString().length());
	        //this.messageBox("10");
	        
	        if(parm.getValue("PATIENT_PASS_SCREENING").equals("Y")){
	        	setValue("PATIENT_PASS_SCREENING_Y","Y");
	        }else if(parm.getValue("PATIENT_PASS_SCREENING").equals("Y")){
	        	setValue("PATIENT_PASS_SCREENING_N","Y");
	        }else{
	        	
	        }
	        //this.messageBox("��һ��if");
	        setValue("ANTIHIV",parm.getValue("ANTIHIV"));
	        setValue("HBSAG",parm.getValue("HBSAG"));
	        setValue("ANTIHBS",parm.getValue("ANTIHBS"));
	        setValue("ANTIHCV",parm.getValue("ANTIHCV"));
	        setValue("VDRL",parm.getValue("VDRL"));
	        setValue("PATIENT_ANTIHIV",parm.getValue("PATIENT_ANTIHIV"));
	        setValue("PATIENT_HBSAG",parm.getValue("PATIENT_HBSAG"));
	        setValue("PATIENT_ANTIHBS",parm.getValue("PATIENT_ANTIHBS"));
	        setValue("PATIENT_ANTIHCV",parm.getValue("PATIENT_ANTIHCV"));
	        setValue("PATIENT_VDRL",parm.getValue("PATIENT_VDRL"));
	        setValue("LNJECTION_NEEDLE",parm.getValue("LNJECTION_NEEDLE"));
	        setValue("INDWELLING_NEEDLE",parm.getValue("INDWELLING_NEEDLE"));
	        setValue("SCALP_ACUPUNCTURE",parm.getValue("SCALP_ACUPUNCTURE"));
	        setValue("NEEDLE",parm.getValue("NEEDLE"));
	        setValue("VACUUM_BLOOD_COLLECTOR",parm.getValue("VACUUM_BLOOD_COLLECTOR"));
	        setValue("SURGICAL_INSTRUMENTS",parm.getValue("SURGICAL_INSTRUMENTS"));
	        setValue("GLASS_ITEMS",parm.getValue("GLASS_ITEMS"));
	        setValue("OTHER_TYPE",parm.getValue("OTHER_TYPE"));
	        if(parm.getValue("OTHER_TYPE").equals("Y")){
	        	((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(true);
	        }
	        //this.messageBox("�ڶ���if");
	        setValue("OTHER_TYPE_DESCRIBE",parm.getValue("OTHER_TYPE_DESCRIBE"));
	        setValue("BLOOD_COLLECTION",parm.getValue("BLOOD_COLLECTION"));
	        setValue("CATHETER_PLACEMENT",parm.getValue("CATHETER_PLACEMENT"));
	        setValue("OPERATION",parm.getValue("OPERATION"));
	        setValue("FORMULATED_REHYDRATION",parm.getValue("FORMULATED_REHYDRATION"));
	        setValue("INJECTION",parm.getValue("INJECTION"));
	        setValue("EQUIPMENT",parm.getValue("EQUIPMENT"));
	        setValue("OTHER_OPERATION",parm.getValue("OTHER_OPERATION"));
	        if(parm.getValue("OTHER_OPERATION").equals("Y")){
	        	((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(true);
	        }
	        
	        //this.messageBox("������if");
	        setValue("OTHER_OPERATION_DESCRIBE",parm.getValue("OTHER_OPERATION_DESCRIBE"));
	        setValue("OPEN_NEEDLE",parm.getValue("OPEN_NEEDLE"));
	        setValue("MISALIGNMENT_PUNCTURE",parm.getValue("MISALIGNMENT_PUNCTURE"));
	        setValue("DOSING_TIME",parm.getValue("DOSING_TIME"));
	        setValue("BACK_SLEEVE",parm.getValue("BACK_SLEEVE"));
	        setValue("BENDING_BREAKING_NEEDLE",parm.getValue("BENDING_BREAKING_NEEDLE"));
	        setValue("OTHERS_STABBED",parm.getValue("OTHERS_STABBED"));
	        setValue("PARTING_INSTRUMENT",parm.getValue("PARTING_INSTRUMENT"));
	        setValue("CLEANING_ITEMS",parm.getValue("CLEANING_ITEMS"));
	        setValue("PIERCING_BOX",parm.getValue("PIERCING_BOX"));
	        setValue("HIDE_ITEMS",parm.getValue("HIDE_ITEMS"));
	        setValue("BREAK_USE",parm.getValue("BREAK_USE"));
	        setValue("OTHER_ACTION",parm.getValue("OTHER_ACTION"));
	        //this.messageBox(parm.getValue("OTHER_ACTION"));
	        if(parm.getValue("OTHER_ACTION").equals("Y")){
	        	((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(true);
	        }
	        setValue("OTHER_ACTION_DESCRIBE",parm.getValue("OTHER_ACTION_DESCRIBE"));
	        
	        //this.messageBox("���ĸ�if");
	        if(parm.getValue("CONTACT_POLLUTION").equals("1")){
	        	setValue("CONTACT_POLLUTION_1", "Y");
			}else if(parm.getValue("CONTACT_POLLUTION").equals("2")){
				setValue("CONTACT_POLLUTION_2", "Y");
			}else if(parm.getValue("CONTACT_POLLUTION").equals("3")){
				setValue("CONTACT_POLLUTION_3", "Y");
			}
	        
	        
	        
	        
	        //this.messageBox("�����if");
	        if (parm.getValue("INJURED").equals("1")){
	        	setValue("INJURED_1", "Y");
			}else if(parm.getValue("INJURED").equals("2")){
				setValue("INJURED_2", "Y");
				((TTextField) getComponent("INJURY_FREQUENCY")).setEnabled(true);
			}
	        
	        //this.messageBox("������if");
	        if (parm.getValue("WEAR_GLOVES").equals("1")){
	        	setValue("WEAR_GLOVES_1", "Y");
			}else if(parm.getValue("WEAR_GLOVES").equals("2")){
				setValue("WEAR_GLOVES_2", "Y");
			}else if(parm.getValue("WEAR_GLOVES").equals("3")){
				setValue("WEAR_GLOVES_3", "Y");
			}
	        //this.messageBox("���߸�if");
	        setValue("INJURY_FREQUENCY",parm.getValue("INJURY_FREQUENCY"));
	        
	        
	        if (parm.getValue("INJURY_TREATMENT").equals("1")){
	        	setValue("INJURY_TREATMENT_1", "Y");
			}else if(parm.getValue("INJURY_TREATMENT").equals("2")){
				setValue("INJURY_TREATMENT_2", "Y");
			}else if(parm.getValue("INJURY_TREATMENT").equals("3")){
				setValue("INJURY_TREATMENT_3", "Y");
			}else if(parm.getValue("INJURY_TREATMENT").equals("4")){
				setValue("INJURY_TREATMENT_4", "Y");
			}
	        //this.messageBox("�ڰ˸�if");
	        setValue("DEPARTMENT_HEADS",parm.getValue("DEPARTMENT_HEADS"));
	        if(parm.getValue("DEPARTMENT_DATE").toString().length() > 0){
	        	setValue("DEPARTMENT_DATE",parm.getValue("DEPARTMENT_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("��һ������");
	        setValue("PREVENTION_HEADS",parm.getValue("PREVENTION_HEADS"));
	        if(parm.getValue("PREVENTION_DATE").toString().length() > 0){
	        	setValue("PREVENTION_DATE",parm.getValue("PREVENTION_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("�ڶ�������");
	        setValue("INFECTED_HEADS",parm.getValue("INFECTED_HEADS"));
	        if(parm.getValue("INFECTED_DATE").toString().length() > 0){
	        	setValue("INFECTED_DATE",parm.getValue("INFECTED_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("����������");
	        if(parm.getValue("OCCURRENCE_DATE").toString().length() > 0){
	        	setValue("OCCURRENCE_DATE",parm.getValue("OCCURRENCE_DATE").toString().substring(0,19).replaceAll("-", "/"));
		    }
	        //this.messageBox("���ĸ�����");
	        if(parm.getValue("FIRST_INSPECTION_DATE").toString().length() > 0){
	        	setValue("FIRST_INSPECTION_DATE",parm.getValue("FIRST_INSPECTION_DATE").toString().substring(0,10).replaceAll("-", "/"));
	        }
	        //this.messageBox("���������");
	        if(parm.getValue("REPORT_DATE").toString().length() > 0){
	        	setValue("REPORT_DATE",parm.getValue("REPORT_DATE").toString().substring(0,19).replaceAll("-", "/"));
	        }
	        //this.messageBox("����������");
	        setValue("PLACE_INJURY",parm.getValue("PLACE_INJURY"));
	        setValue("POSITION_INJURY",parm.getValue("POSITION_INJURY"));
		}else{
			this.messageBox("������12λ��¶���");
			return;
		}
//		TParm parmM = new TParm();
//		TParm parmD = new TParm();
//		TParm parm = new TParm();
//		
//		String a = "EXPOSURE_NO";
//		String b = ",b";
//		String c = a + b + ",c";
//		parmM.setData("string", c);
//		parmD.setData("PAT_NAME", this.getValue("PAT_NAME"));
//		parmD.setData("SEX_CODE", this.getValue("SEX_CODE"));
//		
//		parm.setData("DSPNM", parmM.getData());
//		parm.setData("DSPND", parmD.getData());
//		
//		TParm parm1 = parm.getParm("DSPNM");
//		String d = parm1.getValue("string");
//		TParm parm2 = parm.getParm("DSPND");
//		
//		this.messageBox("parm1:" + parm1 + "-parm2:" + parm2 + "-d:" + d + "-PAT_NAME:" + parmD.getValue("PAT_NAME"));
//		
//		
		//getUiDate();		
	}

	/**
     * ��ѯ��¶����Ƿ����
     */
	public Boolean onQueryExposureno(String EXPOSURE_NO){
		String sql = " SELECT EXPOSURE_NO"
                + " FROM INF_EXPOSURE where EXPOSURE_NO='" + EXPOSURE_NO + "'";
        TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
        //this.messageBox("��ѯ��¶����Ƿ����:" + parm);
        if (parm.getCount() <= 0){
        	return false;
        }
		return true;
	}
    /**
     * ȡ�����ݿ������
     * 
     * @return TJDODBTool
     */
    public TJDODBTool getDBTool() {
        return TJDODBTool.getInstance();
    }
	
	/**
	 * ����(�޸�)
	 */
	public void onSave(){
		
//		if(this.getValue("EXPOSURE_NO").toString().length() != 12){
//			this.messageBox("��¶��Ų��Ϸ�");
//			return;
//		}
		/**
		 * ����
		 */
		if(getValue("PAT_NAME").toString().length() <= 0 || 
				 getValue("USER_ID").toString().length() <= 0){
			this.messageBox("����д��Ҫ��ҽ����Ա����������");
			return;
		}
		
		if(onQueryExposureno(this.getValue("EXPOSURE_NO").toString())){
			//this.messageBox("�������");
			//����
			onUPDATE();
			
		}else{
			//this.messageBox("��������");
			//����
			onNew();
			
		}
	}

	/**
     * �����ϲ��ɱ༭�Ŀؼ�
     */
	public void setTextEnabled(boolean boo) {
		callFunction("UI|EXPOSURE_NO|setEnabled", boo);
	}
	
	/**
     * ������Ʒ����ע��
     */
	public void onOtherTypeFlg() {    	    
	    if (getValueString("OTHER_TYPE").equals("N"))
	            ((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(false);
	    else
	       ((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(true);
	}
	 
	 /**
	 * ������Ʒ����ע��
	 */
	 public void onOTHEROPERATIONFlg() {    	    
		if (getValueString("OTHER_OPERATION").equals("N"))
		     ((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(false);
		else
		     ((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(true);
	}
		 
	/**
    * ������Ʒ����ע��
	*/
	public void onOTHER_ACTIONFlg() {    	
		 if (getValueString("OTHER_ACTION").equals("N"))
		     ((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(false);
		 else
			  ((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(true);
	}
	/**
	 * ��������ע��
	 */
	public void onINJURED2Flg() { 
		//this.messageBox(getValueString("INJURED_2"));
		if (getValueString("INJURED_2").equals("N"))
			((TTextField) getComponent("INJURY_FREQUENCY")).setEnabled(false);
		else
			((TTextField) getComponent("INJURY_FREQUENCY")).setEnabled(true);
	}

	/**
	 * ɾ��
	 */
	public void onDelete(){
		
		
		//��ӡpram
		//System.out.println(parm.getCount());
		
		
		
		
		//System.out.println("reParmdsDF:::"+reParm);
//		if(parm.getCount()>0){
//			//ȷ���Ƿ�ɾ��
//			switch(this.messageBox("��ʾ��Ϣ",
//	                            "ȷ��ɾ��!", TControl.YES_NO_OPTION)) {
//	        //����
//	        case 0:
//	            break;
//	            //������
//	        case 1:
//	            return;
//			}
//		
//			
//			if(.getErrCode()<0){
//				this.messageBox("ɾ��ʧ��");
//				return;
//			}
//			this.messageBox("ɾ���ɹ�");
			//onClear();
			
		
	//	}  
	}
	/**
	 * ��ý�������
	 */
	public TParm getUiDate(){
		TParm Parm=new TParm();
		
		//ʵϰ
		if (getValueString("INTERNSHIP").length() > 0){
			Parm.setData("INTERNSHIP", this.getValue("INTERNSHIP"));
			//this.messageBox("��������"+Parm.getValue("OCCURRENCE_DATE"));
		}
		//����
		if (getValueString("USER_ID").length() > 0){
			Parm.setData("USER_ID", this.getValue("USER_ID"));
			//this.messageBox("��������"+Parm.getValue("OCCURRENCE_DATE"));
		}
		//��������
		if (getValueString("OCCURRENCE_DATE").length() > 0){
			Parm.setData("OCCURRENCE_DATE", this.getValue("OCCURRENCE_DATE").toString().substring(0,19).replaceAll("-", "/"));
			//this.messageBox("��������"+Parm.getValue("OCCURRENCE_DATE"));
			}
		//��������
		if (getValueString("REPORT_DATE").length() > 0){
			Parm.setData("REPORT_DATE", this.getValue("REPORT_DATE").toString().substring(0,19).replaceAll("-", "/"));
			//this.messageBox("��������"+Parm.getValue("REPORT_DATE"));
			}
		//����
		if (getValueString("PAT_NAME").length() > 0){
			Parm.setData("PAT_NAME", this.getValue("PAT_NAME"));
			//this.messageBox("����"+Parm.getValue("PAT_NAME"));
		}
		//�Ա�
		if (getValueString("SEX_CODE").length() > 0){
			Parm.setData("SEX_CODE", this.getValue("SEX_CODE"));
			//this.messageBox("�Ա�"+Parm.getValue("SEX_CODE"));
		}
		//����
		if (getValueString("DEPT_CODE").length() > 0){
			Parm.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
			//this.messageBox("����"+Parm.getValue("DEPT_CODE"));
		}
		//�绰
		if (getValueString("CELL_PHONE").length() > 0){
			Parm.setData("CELL_PHONE", this.getValue("CELL_PHONE"));
			//this.messageBox("�绰"+Parm.getValue("CELL_PHONE"));
		}
		//����
		if (getValueString("WORKING_YEARS").length() > 0){
			Parm.setData("WORKING_YEARS", this.getValue("WORKING_YEARS"));
			//this.messageBox("����"+Parm.getValue("WORKING_YEARS"));
		}
		//���߲�����
		if (getValueString("PATIENT_MR_NO").length() > 0){
			Parm.setData("PATIENT_MR_NO", this.getValue("PATIENT_MR_NO"));
			//this.messageBox("���߲�����"+Parm.getValue("PATIENT_MR_NO"));
		}
		//���߿���
		if (getValueString("PATIENT_DEPT_CODE").length() > 0){
			Parm.setData("PATIENT_DEPT_CODE", this.getValue("PATIENT_DEPT_CODE"));
			//this.messageBox("���߿���"+Parm.getValue("PATIENT_DEPT_CODE"));
		}
		//�״μ�������
		if (getValueString("FIRST_INSPECTION_DATE").length() > 0){
			Parm.setData("FIRST_INSPECTION_DATE", this.getValue("FIRST_INSPECTION_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("�״μ�������"+Parm.getValue("FIRST_INSPECTION_DATE"));
		}
		//���ߴ�ɸ���
		if (getValueString("PATIENT_PASS_SCREENING_N").equals("Y")){
			//this.messageBox("PATIENT_PASS_SCREENING_N:"+getValueString("PATIENT_PASS_SCREENING_N"));
			Parm.setData("PATIENT_PASS_SCREENING", 'N');
			//this.messageBox("���ߴ�ɸ���"+Parm.getValue("PATIENT_PASS_SCREENING"));
		}else{
			//this.messageBox("PATIENT_PASS_SCREENING_N:"+getValueString("PATIENT_PASS_SCREENING_N"));
			Parm.setData("PATIENT_PASS_SCREENING", 'Y');
			//this.messageBox("���ߴ�ɸ���"+Parm.getValue("PATIENT_PASS_SCREENING"));
		}
		//ANTIHIV
		if (getValueString("ANTIHIV").length() > 0){
			Parm.setData("ANTIHIV", this.getValue("ANTIHIV"));
			//this.messageBox("ANTIHIV"+Parm.getValue("ANTIHIV"));
		}
		//HBSAG
		if (getValueString("HBSAG").length() > 0){
			Parm.setData("HBSAG", this.getValue("HBSAG"));
			//this.messageBox("HBSAG"+Parm.getValue("HBSAG"));
		}
		//ANTIHBS
		if (getValueString("ANTIHBS").length() > 0){
			Parm.setData("ANTIHBS", this.getValue("ANTIHBS"));
			//this.messageBox("ANTIHBS"+Parm.getValue("ANTIHBS"));
		}
		//ANTIHCV
		if (getValueString("ANTIHCV").length() > 0){
			Parm.setData("ANTIHCV", this.getValue("ANTIHCV"));
			//this.messageBox("ANTIHCV"+Parm.getValue("ANTIHCV"));
		}
		//VDRL
		if (getValueString("VDRL").length() > 0){
			Parm.setData("VDRL", this.getValue("VDRL"));
			//this.messageBox("VDRL"+Parm.getValue("VDRL"));
		}
		//PATIENT_ANTIHIV
		if (getValueString("PATIENT_ANTIHIV").length() > 0){
			Parm.setData("PATIENT_ANTIHIV", this.getValue("PATIENT_ANTIHIV"));
			//this.messageBox("PATIENT_ANTIHIV"+Parm.getValue("PATIENT_ANTIHIV"));
		}
		//PATIENT_HBSAG
		if (getValueString("PATIENT_HBSAG").length() > 0){
			Parm.setData("PATIENT_HBSAG", this.getValue("PATIENT_HBSAG"));
			//this.messageBox("PATIENT_HBSAG"+Parm.getValue("PATIENT_HBSAG"));
		}
		//PATIENT_ANTIHBS
		if (getValueString("PATIENT_ANTIHBS").length() > 0){
			Parm.setData("PATIENT_ANTIHBS", this.getValue("PATIENT_ANTIHBS"));
			//this.messageBox("PATIENT_ANTIHBS"+Parm.getValue("PATIENT_ANTIHBS"));
		}
		//PATIENT_ANTIHCV
		if (getValueString("PATIENT_ANTIHCV").length() > 0){
			Parm.setData("PATIENT_ANTIHCV", this.getValue("PATIENT_ANTIHCV"));
			//this.messageBox("PATIENT_ANTIHCV"+Parm.getValue("PATIENT_ANTIHCV"));
		}
		//PATIENT_VDRL
		if (getValueString("PATIENT_VDRL").length() > 0){
			Parm.setData("PATIENT_VDRL", this.getValue("PATIENT_VDRL"));
			//this.messageBox("PATIENT_VDRL"+Parm.getValue("PATIENT_VDRL"));
		}
		//һ�㶪��ע����
		if (getValueString("LNJECTION_NEEDLE").length() > 0){
			Parm.setData("LNJECTION_NEEDLE", this.getValue("LNJECTION_NEEDLE"));
			//this.messageBox("һ�㶪��ע����"+Parm.getValue("LNJECTION_NEEDLE"));
		}
		//������
		if (getValueString("INDWELLING_NEEDLE").length() > 0){
			Parm.setData("INDWELLING_NEEDLE", this.getValue("INDWELLING_NEEDLE"));
			//this.messageBox("������"+Parm.getValue("INDWELLING_NEEDLE"));
		}
		//ͷƤ��
		if (getValueString("SCALP_ACUPUNCTURE").length() > 0){
			Parm.setData("SCALP_ACUPUNCTURE", this.getValue("SCALP_ACUPUNCTURE"));
			//this.messageBox("ͷƤ��"+Parm.getValue("SCALP_ACUPUNCTURE"));
		}
		//����
		if (getValueString("NEEDLE").length() > 0){
			Parm.setData("NEEDLE", this.getValue("NEEDLE"));
			//this.messageBox("����"+Parm.getValue("NEEDLE"));
		}
		//��ղ�Ѫ��
		if (getValueString("VACUUM_BLOOD_COLLECTOR").length() > 0){
			Parm.setData("VACUUM_BLOOD_COLLECTOR", this.getValue("VACUUM_BLOOD_COLLECTOR"));
			//this.messageBox("��ղ�Ѫ��"+Parm.getValue("VACUUM_BLOOD_COLLECTOR"));
		}
		//�����е
		if (getValueString("SURGICAL_INSTRUMENTS").length() > 0){
			Parm.setData("SURGICAL_INSTRUMENTS", this.getValue("SURGICAL_INSTRUMENTS"));
			//this.messageBox("�����е"+Parm.getValue("SURGICAL_INSTRUMENTS"));
		}
		//������Ʒ
		if (getValueString("GLASS_ITEMS").length() > 0){
			Parm.setData("GLASS_ITEMS", this.getValue("GLASS_ITEMS"));
			//this.messageBox("������Ʒ"+Parm.getValue("GLASS_ITEMS"));
		}
		//������Ʒ�� ����
		if (getValueString("OTHER_TYPE").length() > 0){
			Parm.setData("OTHER_TYPE", this.getValue("OTHER_TYPE"));
			//this.messageBox("������Ʒ�� ����"+Parm.getValue("OTHER_TYPE"));
		}
		//������Ʒ�� ��������
		if (getValueString("OTHER_TYPE_DESCRIBE").length() > 0){
			Parm.setData("OTHER_TYPE_DESCRIBE", this.getValue("OTHER_TYPE_DESCRIBE"));
			//this.messageBox("������Ʒ�� ��������"+Parm.getValue("OTHER_TYPE_DESCRIBE"));
		}
		//�����˷����ص�
		if (getValueString("PLACE_INJURY").length() > 0){
			Parm.setData("PLACE_INJURY", this.getValue("PLACE_INJURY"));
			//this.messageBox("�����˷����ص�"+Parm.getValue("PLACE_INJURY"));
		}
		//�����˷�����λ
		if (getValueString("POSITION_INJURY").length() > 0){
			Parm.setData("POSITION_INJURY", this.getValue("POSITION_INJURY"));
			//this.messageBox("�����˷�����λ"+Parm.getValue("POSITION_INJURY"));
		}
		//��Ѫ
		if (getValueString("BLOOD_COLLECTION").length() > 0){
			Parm.setData("BLOOD_COLLECTION", this.getValue("BLOOD_COLLECTION"));
			//this.messageBox("��Ѫ"+Parm.getValue("BLOOD_COLLECTION"));
		}
		//���õ���
		if (getValueString("CATHETER_PLACEMENT").length() > 0){
			Parm.setData("CATHETER_PLACEMENT", this.getValue("CATHETER_PLACEMENT"));
			//this.messageBox("���õ���"+Parm.getValue("CATHETER_PLACEMENT"));
		}
		//����
		if (getValueString("OPERATION").length() > 0){
			Parm.setData("OPERATION", this.getValue("OPERATION"));
			//this.messageBox("����"+Parm.getValue("OPERATION"));
		}
		//���ò�Һ
		if (getValueString("FORMULATED_REHYDRATION").length() > 0){
			Parm.setData("FORMULATED_REHYDRATION", this.getValue("FORMULATED_REHYDRATION"));
			//this.messageBox("���ò�Һ"+Parm.getValue("FORMULATED_REHYDRATION"));
		}
		//Ƥ�ڣ�Ƥ�»���ע��
		if (getValueString("INJECTION").length() > 0){
			Parm.setData("INJECTION", this.getValue("INJECTION"));
			//this.messageBox("Ƥ�ڣ�Ƥ�»���ע��"+Parm.getValue("INJECTION"));
		}
		//�����������е
		if (getValueString("EQUIPMENT").length() > 0){
			Parm.setData("EQUIPMENT", this.getValue("EQUIPMENT"));
			//this.messageBox("�����������е"+Parm.getValue("EQUIPMENT"));
		}
		//������ʱ�Ĳ��� ����
		if (getValueString("OTHER_OPERATION").length() > 0){
			Parm.setData("OTHER_OPERATION", this.getValue("OTHER_OPERATION"));
			//this.messageBox("������ʱ�Ĳ��� ����"+Parm.getValue("OTHER_OPERATION"));
		}
		//������ʱ�Ĳ��� ��������
		if (getValueString("OTHER_OPERATION_DESCRIBE").length() > 0){
			Parm.setData("OTHER_OPERATION_DESCRIBE", this.getValue("OTHER_OPERATION_DESCRIBE"));
			//this.messageBox("������ʱ�Ĳ��� ��������"+Parm.getValue("OTHER_OPERATION_DESCRIBE"));
		}
		//����ͷ��
		if (getValueString("OPEN_NEEDLE").length() > 0){
			Parm.setData("OPEN_NEEDLE", this.getValue("OPEN_NEEDLE"));
			//this.messageBox("����ͷ��"+Parm.getValue("OPEN_NEEDLE"));
		}
		//δ��׼�򱻴���
		if (getValueString("MISALIGNMENT_PUNCTURE").length() > 0){
			Parm.setData("MISALIGNMENT_PUNCTURE", this.getValue("MISALIGNMENT_PUNCTURE"));
			//this.messageBox("δ��׼�򱻴���"+Parm.getValue("MISALIGNMENT_PUNCTURE"));
		}
		//��ҩʱ
		if (getValueString("DOSING_TIME").length() > 0){
			Parm.setData("DOSING_TIME", this.getValue("DOSING_TIME"));
			//this.messageBox("��ҩʱ"+Parm.getValue("DOSING_TIME"));
		}
		//�׻���ͷ��
		if (getValueString("BACK_SLEEVE").length() > 0){
			Parm.setData("BACK_SLEEVE", this.getValue("BACK_SLEEVE"));
			//this.messageBox("�׻���ͷ��"+Parm.getValue("BACK_SLEEVE"));
		}
		//�ֿ���ͷ����Ͳ�������۶���ͷ
		if (getValueString("BENDING_BREAKING_NEEDLE").length() > 0){
			Parm.setData("BENDING_BREAKING_NEEDLE", this.getValue("BENDING_BREAKING_NEEDLE"));
			//this.messageBox("�ֿ���ͷ����Ͳ�������۶���ͷ"+Parm.getValue("BENDING_BREAKING_NEEDLE"));
		}
		//����֮��������
		if (getValueString("OTHERS_STABBED").length() > 0){
			Parm.setData("OTHERS_STABBED", this.getValue("OTHERS_STABBED"));
			//this.messageBox("����֮��������"+Parm.getValue("OTHERS_STABBED"));
		}
		//�ֺ���е��װ�ϻ�ȡ�µ�Ƭ
		if (getValueString("PARTING_INSTRUMENT").length() > 0){
			Parm.setData("PARTING_INSTRUMENT", this.getValue("PARTING_INSTRUMENT"));
			//this.messageBox("�ֺ���е��װ�ϻ�ȡ�µ�Ƭ"+Parm.getValue("PARTING_INSTRUMENT"));
		}
		//�����������Ʒ
		if (getValueString("CLEANING_ITEMS").length() > 0){
			Parm.setData("CLEANING_ITEMS", this.getValue("CLEANING_ITEMS"));
			//this.messageBox("�����������Ʒ"+Parm.getValue("CLEANING_ITEMS"));
		}
		//������Ʒ�����ռ���
		if (getValueString("PIERCING_BOX").length() > 0){
			Parm.setData("PIERCING_BOX", this.getValue("PIERCING_BOX"));
			//this.messageBox("������Ʒ�����ռ���"+Parm.getValue("PIERCING_BOX"));
		}
		//������Ʒ������������Ʒ��
		if (getValueString("HIDE_ITEMS").length() > 0){
			Parm.setData("HIDE_ITEMS", this.getValue("HIDE_ITEMS"));
			//this.messageBox("������Ʒ������������Ʒ��"+Parm.getValue("HIDE_ITEMS"));
		}
		//ʹ��ʱ������
		if (getValueString("BREAK_USE").length() > 0){
			Parm.setData("BREAK_USE", this.getValue("BREAK_USE"));
			//this.messageBox("ʹ��ʱ������"+Parm.getValue("BREAK_USE"));
		}
		//������ʱ�Ķ��� ����
		if (getValueString("OTHER_ACTION").length() > 0){
			Parm.setData("OTHER_ACTION", this.getValue("OTHER_ACTION"));
			//this.messageBox("������ʱ�Ķ��� ����"+Parm.getValue("OTHER_ACTION"));
		}
		//������ʱ�Ķ��� ��������
		if (getValueString("OTHER_ACTION_DESCRIBE").length() > 0){
			Parm.setData("OTHER_ACTION_DESCRIBE", this.getValue("OTHER_ACTION_DESCRIBE"));
			//this.messageBox("������ʱ�Ķ��� ��������"+Parm.getValue("OTHER_ACTION_DESCRIBE"));
		}
		//��������Ʒ���Ӵ������˵�ѪҺ����Һ��Ⱦ
		if(getValueString("CONTACT_POLLUTION_1").equals("Y")){
			Parm.setData("CONTACT_POLLUTION", "1");
			//this.messageBox("��������Ʒ���Ӵ������˵�ѪҺ����Һ��Ⱦ"+Parm.getValue("CONTACT_POLLUTION"));
		}else if(getValueString("CONTACT_POLLUTION_2").equals("Y")){
			Parm.setData("CONTACT_POLLUTION", "2");
			//this.messageBox("��������Ʒ���Ӵ������˵�ѪҺ����Һ��Ⱦ"+Parm.getValue("CONTACT_POLLUTION"));
		}else if(getValueString("CONTACT_POLLUTION_3").equals("Y")){
			Parm.setData("CONTACT_POLLUTION", "3");
			//this.messageBox("��������Ʒ���Ӵ������˵�ѪҺ����Һ��Ⱦ"+Parm.getValue("CONTACT_POLLUTION"));
		}
		//������ʱ�Ƿ������
		if (getValueString("WEAR_GLOVES_1").equals("Y")){
			Parm.setData("WEAR_GLOVES", "1");
			//this.messageBox("������ʱ�Ƿ������"+Parm.getValue("WEAR_GLOVES"));
		}else if(getValueString("WEAR_GLOVES_2").equals("Y")){
			Parm.setData("WEAR_GLOVES", "2");
			//this.messageBox("������ʱ�Ƿ������"+Parm.getValue("WEAR_GLOVES"));
		}else if(getValueString("WEAR_GLOVES_3").equals("Y")){
			Parm.setData("WEAR_GLOVES", "3");
			//this.messageBox("������ʱ�Ƿ������"+Parm.getValue("WEAR_GLOVES"));
		}
		//���˴���
		if (getValueString("INJURED_1").equals("Y")){
			Parm.setData("INJURED", "1");
			//this.messageBox("���˴���"+Parm.getValue("INJURED"));
		}else if(getValueString("INJURED_2").equals("Y")){
			Parm.setData("INJURED", "2");
			//this.messageBox("���˴���"+Parm.getValue("INJURED"));
		}
		//���˴����ܹ�
		if (getValueString("INJURY_FREQUENCY").length() > 0){
			Parm.setData("INJURY_FREQUENCY", this.getValue("INJURY_FREQUENCY"));
			//this.messageBox("���˴����ܹ�"+Parm.getValue("INJURY_FREQUENCY"));
		}
		//�����˺���
		if (getValueString("INJURY_TREATMENT_1").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "1");
			//this.messageBox("�����˺���"+Parm.getValue("INJURY_TREATMENT"));
		}else if(getValueString("INJURY_TREATMENT_2").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "2");
			//this.messageBox("�����˺���"+Parm.getValue("INJURY_TREATMENT"));
		}else if(getValueString("INJURY_TREATMENT_3").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "3");
			//this.messageBox("�����˺���"+Parm.getValue("INJURY_TREATMENT"));
		}else if(getValueString("INJURY_TREATMENT_4").equals("Y")){
			Parm.setData("INJURY_TREATMENT", "4");
			//this.messageBox("�����˺���"+Parm.getValue("INJURY_TREATMENT"));
		}
		//���Ÿ�����
		if (getValueString("DEPARTMENT_HEADS").length() > 0){
			Parm.setData("DEPARTMENT_HEADS", this.getValue("DEPARTMENT_HEADS"));
			//this.messageBox("���Ÿ�����"+Parm.getValue("DEPARTMENT_HEADS"));
		}
		//���Ÿ�����ǩ��ʱ��
		if (getValueString("DEPARTMENT_DATE").length() > 0){
			Parm.setData("DEPARTMENT_DATE", this.getValue("DEPARTMENT_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("���Ÿ�����ǩ��ʱ��"+Parm.getValue("DEPARTMENT_DATE"));
		}
		//Ԥ������������
		if (getValueString("PREVENTION_HEADS").length() > 0){
			Parm.setData("PREVENTION_HEADS", this.getValue("PREVENTION_HEADS"));
			//this.messageBox("Ԥ������������"+Parm.getValue("PREVENTION_HEADS"));
		}
		//Ԥ������������ǩ��ʱ��
		if (getValueString("PREVENTION_DATE").length() > 0){
			Parm.setData("PREVENTION_DATE", this.getValue("PREVENTION_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("Ԥ������������ǩ��ʱ��"+Parm.getValue("PREVENTION_DATE"));
		}
		//ҽԺ��Ⱦ���������
		if (getValueString("INFECTED_HEADS").length() > 0){
			Parm.setData("INFECTED_HEADS", this.getValue("INFECTED_HEADS"));
			//this.messageBox("ҽԺ��Ⱦ���������"+Parm.getValue("INFECTED_HEADS"));
		}
		//ҽԺ��Ⱦ���������ǩ��ʱ��
		if (getValueString("INFECTED_DATE").length() > 0){
			Parm.setData("INFECTED_DATE", this.getValue("INFECTED_DATE").toString().substring(0,10).replaceAll("-", "/"));
			//this.messageBox("ҽԺ��Ⱦ���������ǩ��ʱ��"+Parm.getValue("INFECTED_DATE"));
		}
		//����
		if (getValueString("AGE").length() > 0){
			Parm.setData("AGE", this.getValue("AGE"));
			//this.messageBox("����"+Parm.getValue("AGE"));
		}
		
		//������
		Parm.setData("OPT_USER", Operator.getID());
		//this.messageBox("������"+Parm.getValue("OPT_USER"));
		//����ʱ��
		Parm.setData("OPT_DATE", SystemTool.getInstance().getDate().toString().substring(0,10).replaceAll("-", "/"));
		//this.messageBox("����ʱ��"+Parm.getValue("OPT_DATE"));
		//����IP
		Parm.setData("OPT_TERM", Operator.getIP());
		//this.messageBox("����IP"+Parm.getValue("OPT_TERM"));
		return Parm;
	}
	
	/**
	 * �޸�
	 */
	public void onUPDATE(){
			TParm Parm=new TParm();
			Parm = this.getUiDate();
			//�õ���¶���
			Parm.setData("EXPOSURE_NO", getValue("EXPOSURE_NO"));
			//this.messageBox("EXPOSURE_NO�޸�:" + Parm.getValue("EXPOSURE_NO"));
			TParm UpdateParm = TIOM_AppServer.executeAction("action.inf.InfExposureAction",
	                "onUpDate", Parm);
	        if (UpdateParm.getErrCode() < 0) {
	            messageBox("����ʧ��");
	            return;
	        }
	        messageBox("����ɹ�");
	}
	/**
	 * ����
	 */
	public void onNew(){
		TParm Parm = new TParm();
		Timestamp date = SystemTool.getInstance().getDate();
		//���ɱ�¶��
		String caseNo = SystemTool.getInstance().getNo("ALL", "REG", "CASE_NO",
						"CASE_NO"); // ����ȡ��ԭ��
		//���ӱ�¶���
		Parm = this.getUiDate();
		Parm.setData("EXPOSURE_NO", caseNo);
		
		//�״α���Ϊ��������
//		Parm.setData("REPORT_DATE", date.toString().substring(0, 19).replace('-', '/'));
		//this.messageBox("��¶���:" + Parm.getValue("EXPOSURE_NO"));
//		this.messageBox("һ�㶪��ע����:" + Parm.getValue("LNJECTION_NEEDLE"));
		//System.out.println("control1111111" + Parm);
		
		
		TParm NewParm = TIOM_AppServer.executeAction("action.inf.InfExposureAction",
                "onNew", Parm);
        if (NewParm.getErrCode() < 0) {
            messageBox("����ʧ��");
            return;
        }
        messageBox("����ɹ�");
		onClear();
	}
	
	/**
	 * ���
	 */
	public void onClear() {
        clearText();
        onInitialization();
    }
	 /**
     * �������
     */
    public void clearText() {
        clearText("EXPOSURE_NO;PLACE_INJURY;POSITION_INJURY;PAT_NAME;BIRTH_DATE;SEX_CODE;DEPT_CODE;CELL_PHONE;WORKING_YEARS;PATIENT_MR_NO;PATIENT_DEPT_CODE;FIRST_INSPECTION_DATE;PATIENT_PASS_SCREENING;ANTIHIV;HBSAG;ANTIHBS;ANTIHCV;VDRL;PATIENT_ANTIHIV;PATIENT_HBSAG;PATIENT_ANTIHBS;PATIENT_ANTIHCV;PATIENT_VDRL;OTHER_TYPE_DESCRIBE;OTHER_OPERATION_DESCRIBE;OTHER_ACTION_DESCRIBE;DEPARTMENT_HEADS;DEPARTMENT_DATE;PREVENTION_HEADS;PREVENTION_DATE;INFECTED_HEADS;INFECTED_DATE;AGE;OCCURRENCE_DATE;INJURY_FREQUENCY;USER_ID;");
        this.setValue("PATIENT_PASS_SCREENING_N", "Y");
        this.setValue("INTERNSHIP", "N");
        this.setValue("LNJECTION_NEEDLE", "N");
        this.setValue("INDWELLING_NEEDLE", "N");
        this.setValue("SCALP_ACUPUNCTURE", "N");
        this.setValue("NEEDLE", "N");
        this.setValue("VACUUM_BLOOD_COLLECTOR", "N");
        this.setValue("SURGICAL_INSTRUMENTS", "N");
        this.setValue("GLASS_ITEMS", "N");
        this.setValue("OTHER_TYPE", "N");
        this.setValue("BLOOD_COLLECTION", "N");
        this.setValue("CATHETER_PLACEMENT", "N");
        this.setValue("OPERATION", "N");
        this.setValue("FORMULATED_REHYDRATION", "N");
        this.setValue("INJECTION", "N");
        this.setValue("EQUIPMENT", "N");
        this.setValue("OTHER_OPERATION", "N");
        this.setValue("OPEN_NEEDLE", "N");
        this.setValue("MISALIGNMENT_PUNCTURE", "N");
        this.setValue("DOSING_TIME", "N");
        this.setValue("BACK_SLEEVE", "N");
        this.setValue("BENDING_BREAKING_NEEDLE", "N");
        this.setValue("OTHERS_STABBED", "N");
        this.setValue("PARTING_INSTRUMENT", "N");
        this.setValue("CLEANING_ITEMS", "N");
        this.setValue("PIERCING_BOX", "N");
        this.setValue("HIDE_ITEMS", "N");
        this.setValue("BREAK_USE", "N");
        this.setValue("OTHER_ACTION", "N");
        ((TTextField) getComponent("OTHER_TYPE_DESCRIBE")).setEnabled(false);
        ((TTextField) getComponent("OTHER_OPERATION_DESCRIBE")).setEnabled(false);
        ((TTextField) getComponent("OTHER_ACTION_DESCRIBE")).setEnabled(false);
        ((TTextField) getComponent("PAT_NAME")).setEnabled(false);
		((TComboBox) getComponent("SEX_CODE")).setEnabled(false);
		((TTextFormat) getComponent("DEPT_CODE")).setEnabled(false);
        
//        this.setValue("CONTACT_POLLUTION_1", "N");
//        this.setValue("CONTACT_POLLUTION_2", "N");
//        this.setValue("CONTACT_POLLUTION_3", "N");
//        this.setValue("WEAR_GLOVES_1", "N");
//        this.setValue("WEAR_GLOVES_2", "N");
//        this.setValue("WEAR_GLOVES_3", "N");
//        this.setValue("INJURED_1", "N");
//        this.setValue("INJURED_2", "N");
//        this.setValue("INJURY_TREATMENT_1", "N");
//        this.setValue("INJURY_TREATMENT_2", "N");
//        this.setValue("INJURY_TREATMENT_3", "N");
//        this.setValue("INJURY_TREATMENT_4", "N");
        
    }




    
//    /**
//     * ��ӡ����
//     */
//    public void onPrint(){
//    	if(table.getRowCount()<=0){
//    		this.messageBox("û��Ҫ��ӡ������");
//    		return;
//    	}
//    	TParm parm=new TParm();
//    	//��һ�������Ǳ�������ڶ����������������Ĳ�������������Ϊ����
//    	//��ͷ����
//    	parm.setData("TT","TEXT","�����б�");
//    	//Timestamp today = SystemTool.getInstance().getDate();
//    	//��ȡǰ��START_DATE��END_DATE�е����ڷŵ������е���Ӧλ��
//    	String START = this.getValueString("START_DATE");
//    	String END = this.getValueString("END_DATE");
//    	String S=START.substring(0,19);
//    	String E=END.substring(0,19);
//    	parm.setData("ZBSJ","TEXT",S);
//    	parm.setData("ZBSJ2","TEXT",E);
//    	//����������,����ÿ�еĿ��Һ����ڰ�������ʱ���.0ȥ��
//    	TParm resultParm=table.getParmValue();
//    	for(int i=0;i<resultParm.getCount();i++){
//    		String optdateA=resultParm.getValue("OPT_DATE",i);
//    		String optdateB=optdateA.substring(0,19);
//    		resultParm.setData("OPT_DATE",i,optdateB);
//    		String databirthA=resultParm.getValue("DATE_BIRTH",i);
//    		String databirthB=databirthA.substring(0,19);
//    		resultParm.setData("DATE_BIRTH",i,databirthB);
//    		if(resultParm.getValue("PRG_ID",i).equals("ODI")){
//    			resultParm.setData("PRG_ID",i,"����");
//    		}else{
//    			resultParm.setData("PRG_ID",i,"����");
//    		}
//    	}
//    	
//    	
//    	//�������
//    	resultParm.addData("SYSTEM", "COLUMNS","MR_NO");
//    	resultParm.addData("SYSTEM", "COLUMNS","OPT_USER");
//    	resultParm.addData("SYSTEM", "COLUMNS","OPT_DATE");
//    	resultParm.addData("SYSTEM", "COLUMNS","OPT_TERM");
//    	resultParm.addData("SYSTEM", "COLUMNS","PRG_ID");
//    	resultParm.addData("SYSTEM", "COLUMNS","DATE_BIRTH");
//    	//�����ŵ�������
//    	parm.setData("T1",resultParm.getData());
//    	//��β����
//    	parm.setData("BW","TEXT","������:����");
//    	this.openPrintWindow(
//                "%ROOT%\\config\\prt\\REG\\d2.jhw",
//                parm);
//    }
    
	/**
	 * �������¼�
	 */
	public void onMrNo() {

		String mrno = this.getValueString("PATIENT_MR_NO");

		if (StringUtils.isEmpty(mrno)) {
			return;
		}
		mrno = PatTool.getInstance().checkMrno(mrno);
		this.setValue("PATIENT_MR_NO", mrno);
			
		//��ò�����Ϣ
//		TParm patParm = PatTool.getInstance().getInfoForMrno(mrno);
//		String patName = PatTool.getInstance().getNameForMrno(mrno);
//		this.setValue("PAT_NAME", patName);

	}
    
}
