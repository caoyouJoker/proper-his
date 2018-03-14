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
 * 胸痛中心急诊患者事件
 * @author WangQing 20170424
 *
 */
public class AMIErdPatCaseControl extends TControl {

	Pat pat;
	TTable table;
	TTable table2;
	/**
	 * 病案号
	 */
	String mrNo;
	/**
	 * 患者姓名
	 */
	String patName;
	/**
	 * 检伤号
	 */
	String triageNo;
	/**
	 * 急诊就诊号
	 */
	String eCaseNo;
	/**
	 * 住院号
	 */
	String inCaseNo;
		
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();	
		table = (TTable) this.getComponent("TABLE");
		table2 = (TTable) this.getComponent("TABLE2");
	}

	/**
	 * 查询
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
			/** 合并病案号start */
			pat = Pat.onQueryByMrNo(getValueString("MR_NO").trim());
			if (pat == null) {
				this.messageBox("查无此病患!");
				return;
			}
			String srcMrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(srcMrNo) && !srcMrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + srcMrNo + " 已合并至 " + "" + pat.getMrNo());
			}
			mrNo = pat.getMrNo();
			this.setValue("MR_NO", pat.getMrNo());
			/** 合并病案号end */
			mrNoStr = "AND A.MR_NO='"+mrNo+"' ";
		}
		if(triageNo != null && !triageNo.trim().equals("")){
			triageNoStr = "AND A.TRIAGE_NO='"+triageNo+"' ";
		}
		if(eCaseNo != null && !eCaseNo.trim().equals("")){
			eCaseNoStr = "AND A.CASE_NO='"+eCaseNo+"' ";
		}
		if(patName != null && !patName.trim().equals("")){// 姓名模糊查询
			patNameStr = "AND A.PAT_NAME like '%"+patName+"%' ";
		}
		if(inCaseNo != null && !inCaseNo.trim().equals("")){
			inCaseNoStr = "AND B.IN_CASE_NO='"+inCaseNo+"' ";
		}	
		// 患者查询sql
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
		if(result.getCount()==0){// 无数据
			return;
		}else if(result.getCount()>1){// 多条数据
			result = (TParm) this.openDialog("%ROOT%\\config\\ami\\AMIErdPatList.x", result);
			//			System.out.println("===result: "+result);
			setValueForParm("TRIAGE_NO;MR_NO;PAT_NAME;E_CASE_NO;IN_CASE_NO", result, -1);
		}else{// 一条数据
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
		// 事件查询sql
		// alter table AMI_SUR_NS_RECORD modify (CABG_DECIDE_TIME date)
		// PULM_AC_START一开始字段为AC_MED_TIME，而AMI_ERD_DR_RECROD表中无此字段，故改为PULM_AC_START
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
				/** 查询参数 start*/
				+ "WHERE A.TRIAGE_NO='"
				+this.getValueString("TRIAGE_NO")
				+"' "
				+ eCaseNoStr
				+ inCaseNoStr
				+ ") "
				/** 查询参数 end*/						
				+ "UNPIVOT INCLUDE NULLS (dtime FOR timename IN ("
				+ "START_TIME              AS '发病时间',"
				+ "CALL_HELP_TIME          AS '呼救时间',"
				+ "FIRST_OUT_ECG_TIME      AS '院前首次心电时间',"
				+ "TRANSFER_DOOR_TIME_OUT  AS '到达转出院大门时间',"
				+ "TRANSFER_DECICE_TIME    AS '决定转院时间',"
				+ "TRANSFER_AMBULANCE_TIME AS '转出院救护车到达时间',"
				+ "TRANSFER_LEAVE_TIME     AS '离开转出院时间',"
				+ "DOOR_TIME               AS '到达本院大门时间',"
				+ "TRANSFER_IN_ADMIT_TIME  AS '院内接诊时间',"
				+ "IN_HP_CONS_TIME         AS '院内发病会诊时间',"
				+ "REG_DATE                AS '院内挂号时间',"
				+ "FIRST_IN_ECG_TIME       AS '院内首次心电时间',"
				+ "TNI_BLOOD_DRAWING_TIME  AS 'TNI取血时间',"
				+ "REPORT_TIME             AS 'TNI报告时间',"
				+ "CT_NOTICE_TIME             AS '通知CT室时间',"
				+ "CT_RESP_TIME             AS 'CT室回覆时间',"
				+ "CT_ARRIVE_TIME             AS 'CT人员到达时间',"
				+ "CTP_ARRIVE_TIME             AS 'CT室患者到达时间',"
				+ "CT_START_TIME             AS 'CT开始扫描时间',"
				+ "CT_REPORT_TIME             AS 'CT报告时间',"
				+ "UT_NOTICE_TIME          AS '通知彩超室时间',"
				+ "UT_START_TIME           AS '彩超检查时间',"
				+ "UT_REPORT_TIME          AS '彩超报告时间',"
				+ "PRI_DIG_TIME            AS '初步诊断时间',"
				+ "EMRADM_FIRST_ADM_TIME   AS '首次给药时间',"
				+ "STEMI_CALL_TIME         AS 'STEMI呼叫心内科时间',"
				+ "STEMI_FIRST_DIG_TIME    AS 'STEMI内科首诊时间',"
				+ "NSTEMI_CALL_IN_TIME     AS 'NSTEMI呼叫心内科时间',"
				+ "NSTEMI_FIRST_MED_TIME   AS 'NSTEMI内科首诊时间',"
				+ "AD_CONS_IN_TIME         AS '主动脉夹层处理-心内科会诊时间',"
				+ "AD_CONS_OUT_NOTICE      AS '主动脉夹层处理-呼叫心外科时间',"
				+ "AD_CONS_OUT_TIME        AS '主动脉夹层处理-心外科会诊时间',"
				+ "PULM_AC_START           AS '肺栓塞-开始抗凝时间',"
				+ "THRO_INFO_START         AS '溶栓开始知情同意时间',"
				+ "THRO_INFO_SIGN          AS '溶栓签署知情同意时间',"
				+ "THRO_START_TIME         AS '溶栓开始时间',"
				+ "THRO_TIME               AS '溶栓结束时间',"
				+ "DECIDE_TIME             AS '决定手术时间',"
				+ "INFO_CONSENT_START_TIME AS '开始知情同意时间',"
				+ "INFO_CONSENT_SIGN_TIME  AS '签署知情同意时间',"
				+ "CCR_START_TIME          AS '启动导管室时间',"
				+ "CCR_READY_TIME          AS '导管室完成准备时间',"
				+ "PAT_ARRIVE_TIME         AS '导管室患者到达时间',"
				+ "PUNCTURE_START_TIME     AS '开始穿刺时间',"
				+ "GRAPHY_START_TIME       AS '造影开始时间',"
				+ "GRAPHY_END_TIME         AS '造影结束时间',"
				+ "SUR_START_TIME          AS '手术开始时间',"
				+ "PBMV_TIME               AS '球囊扩张时间',"
				+ "SUR_END_TIME            AS '手术结束时间',"
				+ "STENT_GRAFT_START_TIME  AS '覆膜支架开始介入时间',"
				+ "STENT_GRAFT_END_TIME    AS '支架释放时间',"
				+ "CABG_DECIDE_TIME        AS '决定手术时间',"
				+ "CABG_START_TIME         AS '开始手术时间',"
				+ "CABG_END_TIME           AS '结束手术时间',"
				+ "THOR_DECIDE_TIME        AS '决定手术时间',"
				+ "THOR_INFO_CONT_START    AS '开始知情同意时间',"
				+ "THOR_INFO_CONT_SIGN     AS '签署知情同意时间',"
				+ "THOR_START_TIME         AS '开始手术时间',"
				+ "THOR_END_TIME           AS '结束手术时间'))"
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
	 * 清空
	 */
	public void onClear(){
		clearValue("TRIAGE_NO;MR_NO;PAT_NAME;E_CASE_NO;IN_CASE_NO");
		table.removeRowAll();
		table2.removeRowAll();
	}






















}
