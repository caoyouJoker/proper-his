package com.javahis.ui.inf;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import jdo.sys.Operator;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TPopupMenuEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.StringUtil;

/**
 * Title: ְҵ��¶�Ǽǿ�����
 * 
 * Copyright: Copyright (c) 2017
 * 
 * Company: JavaHis
 * 
 * @author zhanglei 2017/5/14
 * @version 1.0
 */
public class INFExposureQueryControl extends TControl {
	//��ý���Ttable�ؼ�
	private static TTable mainTable;
	

	/**
	 * ��ʼ������
	 */
	public void onInit() {
		super.onInit();
		// ���TABLE����
		mainTable = (TTable) getComponent("TABLE");
		this.initPage();
		
		

	}

	/**
	 * ��ʼ��������
	 */
	private void initPage() {
		Timestamp date = SystemTool.getInstance().getDate();
		setValue("END_TIME", date.toString().substring(0, 10).replace('-', '/')
				+ " 23:59:59");
		setValue("START_TIME",
                 StringTool.rollDate(date, -7).toString().substring(0, 10).
                 replace('-', '/') + " 00:00:00");
		
	}
	/**
	 *��ѯ�Ƿ�ʵϰ��� 
	 */
	public boolean onQueryInternship() {
		String sql = "SELECT INTERNSHIP FROM INF_EXPOSURE";
		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		if(resultParm.getValue("INTERNSHIP").equals("Y")){
			return false;
		}
		return true;
	}
	/**
	 * ��ѯ����
	 */
	public void onQuery() {
		
		mainTable.removeRowAll();// �������
		String startTime = this.getText("START_TIME");
		String endTime = this.getText("END_TIME");
		String user_id = getValueString("USER_ID");
		String user_name = getValueString("PAT_NAME");
		String sql1 = "";
		// ����
		if (!"".equals(user_id)) {
			sql1 += " AND USER_ID = '" + user_id + "'";
		}
		// ����
		if (!"".equals(user_name)) {
			sql1 += " AND PAT_NAME = '" + user_name + "'";
		}
		
		String sql = "SELECT A.EXPOSURE_NO,A.PAT_NAME,A.BIRTH_DATE,A.SEX_CODE,A.DEPT_CODE,A.CELL_PHONE,A.WORKING_YEARS,A.PATIENT_MR_NO,A.PATIENT_DEPT_CODE,A.FIRST_INSPECTION_DATE,A.PATIENT_PASS_SCREENING,A.ANTIHIV,A.HBSAG,A.ANTIHBS,A.ANTIHCV,A.VDRL,A.PATIENT_ANTIHIV,A.PATIENT_HBSAG,A.PATIENT_ANTIHBS,A.PATIENT_ANTIHCV,A.PATIENT_VDRL,A.LNJECTION_NEEDLE,A.INDWELLING_NEEDLE,A.SCALP_ACUPUNCTURE,A.NEEDLE,A.VACUUM_BLOOD_COLLECTOR,A.SURGICAL_INSTRUMENTS,A.GLASS_ITEMS,A.OTHER_TYPE,A.OTHER_TYPE_DESCRIBE,A.BLOOD_COLLECTION,A.CATHETER_PLACEMENT,A.OPERATION,A.FORMULATED_REHYDRATION,A.INJECTION,A.EQUIPMENT,A.OTHER_OPERATION,A.OTHER_OPERATION_DESCRIBE,A.OPEN_NEEDLE,A.MISALIGNMENT_PUNCTURE,A.DOSING_TIME,A.BACK_SLEEVE,A.BENDING_BREAKING_NEEDLE,A.OTHERS_STABBED,A.PARTING_INSTRUMENT,A.CLEANING_ITEMS,A.PIERCING_BOX,A.HIDE_ITEMS,A.BREAK_USE,A.OTHER_ACTION,A.OTHER_ACTION_DESCRIBE,A.CONTACT_POLLUTION,A.WEAR_GLOVES,A.INJURED,A.INJURY_FREQUENCY,A.INJURY_TREATMENT,A.DEPARTMENT_HEADS,A.DEPARTMENT_DATE,A.PREVENTION_HEADS,A.PREVENTION_DATE,A.INFECTED_HEADS,A.INFECTED_DATE,A.OPT_USER,A.OPT_DATE,A.OPT_TERM,A.AGE,A.OCCURRENCE_DATE,A.PLACE_INJURY,A.POSITION_INJURY,A.USER_ID,A.INTERNSHIP,A.INFECTION,A.REPORT_DATE " +
				 ",NVL(B.ROLE_ID,'99') AS ROLE_ID " + 
			     "FROM INF_EXPOSURE A LEFT JOIN SYS_OPERATOR B ON A.USER_ID = B.USER_ID " + 
			     "WHERE REPORT_DATE BETWEEN TO_DATE(" + SystemTool.getInstance().getDateReplace(startTime, true) +
				 ",'YYYYMMDDHH24MISS') " + " AND TO_DATE(" + SystemTool.getInstance().getDateReplace(endTime.substring(0,10), false) + 
				 ",'YYYYMMDDHH24MISS')" + sql1;
		//this.messageBox("startTime:" + SystemTool.getInstance().getDateReplace(startTime, true) + "======endTime:" + SystemTool.getInstance().getDateReplace(endTime.substring(0,10), false));
//		String sql = "SELECT EXPOSURE_NO,PAT_NAME,BIRTH_DATE,SEX_CODE,DEPT_CODE,CELL_PHONE,WORKING_YEARS,PATIENT_MR_NO,PATIENT_DEPT_CODE,FIRST_INSPECTION_DATE,PATIENT_PASS_SCREENING,ANTIHIV,HBSAG,ANTIHBS,ANTIHCV,VDRL,PATIENT_ANTIHIV,PATIENT_HBSAG,PATIENT_ANTIHBS,PATIENT_ANTIHCV,PATIENT_VDRL,LNJECTION_NEEDLE,INDWELLING_NEEDLE,SCALP_ACUPUNCTURE,NEEDLE,VACUUM_BLOOD_COLLECTOR,SURGICAL_INSTRUMENTS,GLASS_ITEMS,OTHER_TYPE,OTHER_TYPE_DESCRIBE,BLOOD_COLLECTION,CATHETER_PLACEMENT,OPERATION,FORMULATED_REHYDRATION,INJECTION,EQUIPMENT,OTHER_OPERATION,OTHER_OPERATION_DESCRIBE,OPEN_NEEDLE,MISALIGNMENT_PUNCTURE,DOSING_TIME,BACK_SLEEVE,BENDING_BREAKING_NEEDLE,OTHERS_STABBED,PARTING_INSTRUMENT,CLEANING_ITEMS,PIERCING_BOX,HIDE_ITEMS,BREAK_USE,OTHER_ACTION,OTHER_ACTION_DESCRIBE,CONTACT_POLLUTION,WEAR_GLOVES,INJURED,INJURY_FREQUENCY,INJURY_TREATMENT,DEPARTMENT_HEADS,DEPARTMENT_DATE,PREVENTION_HEADS,PREVENTION_DATE,INFECTED_HEADS,INFECTED_DATE,OPT_USER,OPT_DATE,OPT_TERM,AGE,OCCURRENCE_DATE,PLACE_INJURY,POSITION_INJURY,USER_ID,INTERNSHIP,INFECTION,REPORT_DATE " + 
//				     "FROM INF_EXPOSURE " + 
//				     "WHERE REPORT_DATE BETWEEN TO_DATE(" + SystemTool.getInstance().getDateReplace(startTime, true) +
//					 ",'YYYYMMDDHH24MISS') " + " AND TO_DATE(" + SystemTool.getInstance().getDateReplace(endTime.substring(0,10), false) + 
//					 ",'YYYYMMDDHH24MISS')" + sql1;
		
		//����,150;����,100;�Ա�,50;   ����,100;  ��ϵ�绰,100;��ɫ,80; ����,50;       �˺������ĵص�,200;����ʱ��,150;   ��¶��λ,200;    �Ƿ��Ⱦ,150;��������,150
		//USER_ID;PAT_NAME;SEX_CODE;DEPT_CODE;CELL_PHONE;ROLE_ID;WORKING_YEARS;PLACE_INJURY;OCCURRENCE_DATE;POSITION_INJURY;INFECTION;REPORT_DATE

		TParm resultParm = new TParm(TJDODBTool.getInstance().select(sql));
		
		if (resultParm.getCount() < 0) {
			this.messageBox("û��Ҫ��ѯ������");
			return;
		}

		mainTable.setParmValue(resultParm);

	}

	/**
	 * ����
	 */
	public void onRegDetail() {
		//this.messageBox("����");
		int rowIndex = ((TTable) this.getComponent("TABLE")).getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("��ѡ��һ������");
			return;
		}
		
		TParm parm = new TParm();
		
		TParm patInfo = (TParm) callFunction("UI|TABLE|getParmValue");
		parm.setData("EXPOSURE_NO", patInfo.getValue("EXPOSURE_NO", rowIndex));
//		this.messageBox("rowIndex:" + rowIndex);
//		this.messageBox("patInfo:" + patInfo);
//		this.messageBox("parm:" + parm);
		TParm result = (TParm) this
				.openWindow("%ROOT%\\config\\inf\\INFExposureUI.x",parm);
//		if ("".equals(this.getValueString("USER_ID"))) {
//			this.setValue("USER_ID", result.getValue("USER_ID"));
//			this.onQuery();
//		}

	}

	/**
	 * �������
	 */
	public void onExcel() {
		if (mainTable.getRowCount() > 0) {
			ExportExcelUtil.getInstance().exportExcel(mainTable, "ҽ����Ա��¶�Ǽ��б�");
		} else {
			this.messageBox("û�л������");
			return;
		}
	}

	/**
	 * ��շ���
	 */
	public void onClear() {
		this.clearValue("START_TIME;END_TIME;PAT_NAME;USER_ID");
		mainTable.removeRowAll();
		this.onInit();
	}

}
