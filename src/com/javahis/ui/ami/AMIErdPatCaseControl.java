package com.javahis.ui.ami;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.ui.TTable;
import com.javahis.util.SelectResult;
import com.javahis.util.StringUtil;

import jdo.sys.Pat;
import jdo.sys.PatTool;

/**
 * ��ʹ���ļ��ﻼ���¼�
 * @author WangQing 20170424
 *
 */
public class AMIErdPatCaseControl extends TControl {

	Pat pat;
	TTable table;
	TTable table2;
	/**
	 * ������
	 */
	String mrNo;
	/**
	 * ��������
	 */
	String patName;
	/**
	 * ���˺�
	 */
	String triageNo;
	/**
	 * ��������
	 */
	String eCaseNo;
	/**
	 * סԺ��
	 */
	String inCaseNo;
		
	/**
	 * ��ʼ��
	 */
	public void onInit(){
		super.onInit();	
		table = (TTable) this.getComponent("TABLE");
		table2 = (TTable) this.getComponent("TABLE2");
	}

	/**
	 * ��ѯ
	 */
	public void onQuery(){			
		mrNo = this.getValueString("MR_NO");
		patName = this.getValueString("PAT_NAME");
		triageNo = this.getValueString("TRIAGE_NO");
		eCaseNo = this.getValueString("E_CASE_NO");
		inCaseNo = this.getValueString("IN_CASE_NO");
		String mrNoStr = "";
		String patNameStr = "";
		String triageNoStr = "";
		String eCaseNoStr = "";
		String inCaseNoStr = "";
		if(mrNo != null && !mrNo.trim().equals("")){
			/** �ϲ�������start */
			pat = Pat.onQueryByMrNo(getValueString("MR_NO").trim());
			if (pat == null) {
				this.messageBox("���޴˲���!");
				return;
			}
			String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + srcMrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
			}
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", pat.getMrNo());
			/** �ϲ�������end */
			mrNoStr = "AND A.MR_NO='"+mrNo+"' ";
		}
		if(triageNo != null && !triageNo.trim().equals("")){
			triageNoStr = "AND A.TRIAGE_NO='"+triageNo+"' ";
		}
		if(eCaseNo != null && !eCaseNo.trim().equals("")){
			eCaseNoStr = "AND A.CASE_NO='"+eCaseNo+"' ";
		}
		if(patName != null && !patName.trim().equals("")){// ����ģ����ѯ
			patNameStr = "AND A.PAT_NAME like '%"+patName+"%' ";
		}
		if(inCaseNo != null && !inCaseNo.trim().equals("")){
			inCaseNoStr = "AND B.IN_CASE_NO='"+inCaseNo+"' ";
		}	
		// ���߲�ѯsql
		String sql = "SELECT A.TRIAGE_NO, A.MR_NO, A.PAT_NAME, A.CASE_NO AS E_CASE_NO, B.IN_CASE_NO "
				+ "FROM ERD_EVALUTION A LEFT JOIN ADM_RESV B "
				+ "ON A.CASE_NO=B.OPD_CASE_NO WHERE 1=1 "
				+ mrNoStr
				+ triageNoStr
				+ eCaseNoStr
				+ inCaseNoStr
				+ patNameStr;
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("===result: "+result);
		if(result.getCount()==0){// ������
			return;
		}else if(result.getCount()>1){// ��������
			result = (TParm) this.openDialog("%ROOT%\\config\\ami\\AMIErdPatList.x", result);
			//			System.out.println("===result: "+result);
			setValueForParm("TRIAGE_NO;MR_NO;PAT_NAME;E_CASE_NO;IN_CASE_NO", result, -1);
		}else{// һ������
			setValueForParm("TRIAGE_NO;MR_NO;PAT_NAME;E_CASE_NO;IN_CASE_NO", result, 0);
		}
		mrNo = this.getValueString("MR_NO");
		patName = this.getValueString("PAT_NAME");
		triageNo = this.getValueString("TRIAGE_NO");
		eCaseNo = this.getValueString("E_CASE_NO");
		inCaseNo = this.getValueString("IN_CASE_NO");
		
		if(eCaseNo != null && !eCaseNo.trim().equals("")){
			eCaseNoStr = "and A.CASE_NO='"
					+this.getValueString("E_CASE_NO")
					+"' ";
		}
		if(inCaseNo != null && !inCaseNo.trim().equals("")){
			inCaseNoStr = "and  B.IN_CASE_NO='"
					+this.getValueString("IN_CASE_NO")
					+"' ";
		}	
		// �¼���ѯsql
		// alter table AMI_SUR_NS_RECORD modify (CABG_DECIDE_TIME date)
		// PULM_AC_STARTһ��ʼ�ֶ�ΪAC_MED_TIME����AMI_ERD_DR_RECROD�����޴��ֶΣ��ʸ�ΪPULM_AC_START
		String sql2= " SELECT timename, dtime FROM (SELECT ROWNUM AS NO, timename, dtime FROM ("
				+ "(SELECT ERD.START_TIME,ERD.CALL_HELP_TIME,ERD.FIRST_OUT_ECG_TIME,ERD.TRANSFER_DOOR_TIME_OUT,"
				+ "ERD.TRANSFER_DECICE_TIME,ERD.TRANSFER_AMBULANCE_TIME,ERD.TRANSFER_LEAVE_TIME,"
				+ "ERD.DOOR_TIME,ERD.TRANSFER_IN_ADMIT_TIME,ERD.IN_HP_CONS_TIME,ERD.IN_HP_LEAVE_DEPT,"
				+ "REG.REG_DATE,"
				+ "ERDNS.FIRST_IN_ECG_TIME,ERDNS.TNI_BLOOD_DRAWING_TIME,ERDNS.REPORT_TIME,"
				+ "CT.CT_NOTICE_TIME,CT.CT_RESP_TIME,CT.CT_ARRIVE_TIME,CT.PAT_ARRIVE_TIME as CTP_ARRIVE_TIME,CT.CT_START_TIME,CT.CT_REPORT_TIME,"
				+ "UT.UT_NOTICE_TIME,UT.UT_START_TIME,UT.UT_REPORT_TIME,"
				+ "ERDDR.PRI_DIG_TIME,ERDDR.EMRADM_FIRST_ADM_TIME,ERDDR.STEMI_CALL_TIME,"
				+ "ERDDR.STEMI_FIRST_DIG_TIME,ERDDR.NSTEMI_CALL_IN_TIME,ERDDR.NSTEMI_FIRST_MED_TIME,"
				+ "ERDDR.AD_CONS_IN_TIME,ERDDR.AD_CONS_OUT_NOTICE,"
				+ "ERDDR.AD_CONS_OUT_TIME,ERDDR.PULM_AC_START,ERDDR.THRO_INFO_START,ERDDR.THRO_INFO_SIGN,"
				+ "ERDDR.THRO_START_TIME,ERDDR.THRO_TIME,"
				+ "INTNS.DECIDE_TIME,INTNS.INFO_CONSENT_START_TIME,INTNS.INFO_CONSENT_SIGN_TIME,INTNS.CCR_START_TIME,"
				+ "INTNS.CCR_READY_TIME,INTNS.PAT_ARRIVE_TIME,INTNS.PUNCTURE_START_TIME,INTNS.GRAPHY_START_TIME,"
				+ "INTNS.GRAPHY_END_TIME,INTNS.SUR_START_TIME,INTNS.PBMV_TIME,INTNS.SUR_END_TIME,"
				+ "INTNS.STENT_GRAFT_START_TIME,INTNS.STENT_GRAFT_END_TIME,"
				+ "SURNS.CABG_DECIDE_TIME,SURNS.CABG_START_TIME,SURNS.CABG_END_TIME,SURNS.THOR_DECIDE_TIME,"
				+ "SURNS.THOR_INFO_CONT_START,SURNS.THOR_INFO_CONT_SIGN,SURNS.THOR_START_TIME,SURNS.THOR_END_TIME "
				+ "FROM ERD_EVALUTION A "
				+ "LEFT JOIN AMI_PREERD_INFO ERD ON A.TRIAGE_NO=ERD.TRIAGE_NO"
				+ " LEFT JOIN REG_PATADM REG on A.CASE_NO=REG.CASE_NO"
				+ " LEFT JOIN AMI_ERD_NS_RECORD ERDNS ON A.TRIAGE_NO=ERDNS.TRIAGE_NO"
				+ " LEFT JOIN AMI_CT_RECORD CT on A.CASE_NO=CT.CASE_NO "
				+ " LEFT JOIN AMI_UT_RECORD UT on A.CASE_NO=UT.CASE_NO"
				+ " LEFT JOIN AMI_ERD_DR_RECROD ERDDR on A.CASE_NO=ERDDR.CASE_NO "
				+ " LEFT JOIN AMI_INT_NS_RECORD INTNS on A.CASE_NO=INTNS.CASE_NO "
				+ "LEFT JOIN AMI_SUR_NS_RECORD SURNS ON A.CASE_NO=SURNS.CASE_NO"
				+ " LEFT JOIN ADM_RESV B on A.CASE_NO=B.OPD_CASE_NO "
				/** ��ѯ���� start*/
				+ "WHERE A.TRIAGE_NO='"
				+this.getValueString("TRIAGE_NO")
				+"' "
				+ eCaseNoStr
				+ inCaseNoStr
				+ ") "
				/** ��ѯ���� end*/						
				+ "UNPIVOT INCLUDE NULLS (dtime FOR timename IN ("
				+ "START_TIME              AS '����ʱ��',"
				+ "CALL_HELP_TIME          AS '����ʱ��',"
				+ "FIRST_OUT_ECG_TIME      AS 'Ժǰ�״��ĵ�ʱ��',"
				+ "TRANSFER_DOOR_TIME_OUT  AS '����ת��Ժ����ʱ��',"
				+ "TRANSFER_DECICE_TIME    AS '����תԺʱ��',"
				+ "TRANSFER_AMBULANCE_TIME AS 'ת��Ժ�Ȼ�������ʱ��',"
				+ "TRANSFER_LEAVE_TIME     AS '�뿪ת��Ժʱ��',"
				+ "DOOR_TIME               AS '���ﱾԺ����ʱ��',"
				+ "TRANSFER_IN_ADMIT_TIME  AS 'Ժ�ڽ���ʱ��',"
				+ "IN_HP_CONS_TIME         AS 'Ժ�ڷ�������ʱ��',"
				+ "REG_DATE                AS 'Ժ�ڹҺ�ʱ��',"
				+ "FIRST_IN_ECG_TIME       AS 'Ժ���״��ĵ�ʱ��',"
				+ "TNI_BLOOD_DRAWING_TIME  AS 'TNIȡѪʱ��',"
				+ "REPORT_TIME             AS 'TNI����ʱ��',"
				+ "CT_NOTICE_TIME             AS '֪ͨCT��ʱ��',"
				+ "CT_RESP_TIME             AS 'CT�һظ�ʱ��',"
				+ "CT_ARRIVE_TIME             AS 'CT��Ա����ʱ��',"
				+ "CTP_ARRIVE_TIME             AS 'CT�һ��ߵ���ʱ��',"
				+ "CT_START_TIME             AS 'CT��ʼɨ��ʱ��',"
				+ "CT_REPORT_TIME             AS 'CT����ʱ��',"
				+ "UT_NOTICE_TIME          AS '֪ͨ�ʳ���ʱ��',"
				+ "UT_START_TIME           AS '�ʳ����ʱ��',"
				+ "UT_REPORT_TIME          AS '�ʳ�����ʱ��',"
				+ "PRI_DIG_TIME            AS '�������ʱ��',"
				+ "EMRADM_FIRST_ADM_TIME   AS '�״θ�ҩʱ��',"
				+ "STEMI_CALL_TIME         AS 'STEMI�������ڿ�ʱ��',"
				+ "STEMI_FIRST_DIG_TIME    AS 'STEMI�ڿ�����ʱ��',"
				+ "NSTEMI_CALL_IN_TIME     AS 'NSTEMI�������ڿ�ʱ��',"
				+ "NSTEMI_FIRST_MED_TIME   AS 'NSTEMI�ڿ�����ʱ��',"
				+ "AD_CONS_IN_TIME         AS '�������в㴦��-���ڿƻ���ʱ��',"
				+ "AD_CONS_OUT_NOTICE      AS '�������в㴦��-���������ʱ��',"
				+ "AD_CONS_OUT_TIME        AS '�������в㴦��-����ƻ���ʱ��',"
				+ "PULM_AC_START           AS '��˨��-��ʼ����ʱ��',"
				+ "THRO_INFO_START         AS '��˨��ʼ֪��ͬ��ʱ��',"
				+ "THRO_INFO_SIGN          AS '��˨ǩ��֪��ͬ��ʱ��',"
				+ "THRO_START_TIME         AS '��˨��ʼʱ��',"
				+ "THRO_TIME               AS '��˨����ʱ��',"
				+ "DECIDE_TIME             AS '��������ʱ��',"
				+ "INFO_CONSENT_START_TIME AS '��ʼ֪��ͬ��ʱ��',"
				+ "INFO_CONSENT_SIGN_TIME  AS 'ǩ��֪��ͬ��ʱ��',"
				+ "CCR_START_TIME          AS '����������ʱ��',"
				+ "CCR_READY_TIME          AS '���������׼��ʱ��',"
				+ "PAT_ARRIVE_TIME         AS '�����һ��ߵ���ʱ��',"
				+ "PUNCTURE_START_TIME     AS '��ʼ����ʱ��',"
				+ "GRAPHY_START_TIME       AS '��Ӱ��ʼʱ��',"
				+ "GRAPHY_END_TIME         AS '��Ӱ����ʱ��',"
				+ "SUR_START_TIME          AS '������ʼʱ��',"
				+ "PBMV_TIME               AS '��������ʱ��',"
				+ "SUR_END_TIME            AS '��������ʱ��',"
				+ "STENT_GRAFT_START_TIME  AS '��Ĥ֧�ܿ�ʼ����ʱ��',"
				+ "STENT_GRAFT_END_TIME    AS '֧���ͷ�ʱ��',"
				+ "CABG_DECIDE_TIME        AS '��������ʱ��',"
				+ "CABG_START_TIME         AS '��ʼ����ʱ��',"
				+ "CABG_END_TIME           AS '��������ʱ��',"
				+ "THOR_DECIDE_TIME        AS '��������ʱ��',"
				+ "THOR_INFO_CONT_START    AS '��ʼ֪��ͬ��ʱ��',"
				+ "THOR_INFO_CONT_SIGN     AS 'ǩ��֪��ͬ��ʱ��',"
				+ "THOR_START_TIME         AS '��ʼ����ʱ��',"
				+ "THOR_END_TIME           AS '��������ʱ��'))"
				+ ") "
				+ " ORDER BY dtime nulls last) ";

		result = new TParm(TJDODBTool.getInstance().select(sql2));
		SelectResult sr = new SelectResult(result);
		SelectResult sr0 = sr.getEmptyResult();
		SelectResult sr1 = sr.getEmptyResult();
		Vector<Vector<Object>> rows = sr.getAllRow();
		for(int i=0;i<rows.size();i++){
			if(i%2==0){
				sr0.addRow(rows.get(i));
			}else{
				sr1.addRow(rows.get(i));
			}
		}
		
		table.setParmValue(sr0.getTParm());	
		table2.setParmValue(sr1.getTParm());	
	}
	
	/**
	 * ���
	 */
	public void onClear(){
		clearValue("TRIAGE_NO;MR_NO;PAT_NAME;E_CASE_NO;IN_CASE_NO");
		table.removeRowAll();
		table2.removeRowAll();
	}






















}
