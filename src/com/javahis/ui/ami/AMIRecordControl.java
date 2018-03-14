package com.javahis.ui.ami;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.javahis.ui.spc.util.StringUtils;
import com.javahis.util.OdoUtil;
import com.javahis.util.StringUtil;

import jdo.emr.EMRAMITool;
import jdo.emr.EMROpeDrStationTool;
import jdo.emr.EMRPublicTool;
import jdo.ope.OPEAMITool;
import jdo.ope.OPEINTSaveTool;
import jdo.ope.OPESURSaveTool;
import jdo.reg.RegPreedTool;
import jdo.reg.RegSaveTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SystemTool;

import com.javahis.ui.ami.AMIRecord;

/**
 * <p>胸痛中心总表</p>
 * @author wangqing
 */
public class AMIRecordControl extends TControl{
	TTable table;
	private TWord word;
	//机构化病例路径
	private String[] saveFiles;

	private boolean update = false;//true 表示只修改 false表示新增数据保存
	/**
	 * 急诊就诊号
	 */
	private String caseNo="";
	/**
	 * 急诊检伤号
	 */
	private String triageNo = "";
	/**
	 * 胸痛总表classCodeConfig
	 */
	private final String allClassCodeConfig = "AMI_ALL_CLASSCODE";
	/**
	 * 胸痛总表subClassCodeConfig
	 */
	private final String allSubClassCodeConfig = "AMI_ALL_SUBCLASSCODE";
	/**
	 * 胸痛急诊护士记录病历classCodeConfig
	 */
	private static String preClassCodeConfig = "AMI_PRE_CLASSCODE";
	/**
	 * 胸痛急诊护士记录病历subClassCodeConfig
	 */
	private static String preSubclassCodeConfig = "AMI_PRE_SUBCLASSCODE";
	/**
	 * 胸痛急诊医生记录病历classCodeConfig
	 */
	private static String eddrClassCodeConfig = "AMI_EDDR_CLASSCODE";
	/**
	 * 胸痛急诊医生记录病历subClassCodeConfig
	 */
	private static String eddrSubclassCodeConfig = "AMI_EDDR_SUBCLASSCODE";
	/**
	 * 介入室医生病历classCodeConfig
	 */
	private static String irdrClassCodeConfig = "AMI_IRDR_CLASSCODE";
	/**
	 * 介入室医生病历subClassCodeConfig
	 */
	private static String irdrSubclassCodeConfig = "AMI_IRDR_SUBCLASSCODE";
	/**
	 * 介入室护士病历classCodeConfig
	 */
	private static String irnsClassCodeConfig = "AMI_IRNS_CLASSCODE";
	/**
	 * 介入室护士病历subClassCodeConfig
	 */
	private static String irnsSubclassCodeConfig = "AMI_IRNS_SUBCLASSCODE";
	/**
	 * 手术室护士病历classCodeConfig
	 */
	private static String srnsClassCodeConfig = "AMI_SRNS_CLASSCODE";
	/**
	 * 手术室护士病历subClassCodeConfig
	 */
	private static String srnsSubclassCodeConfig = "AMI_SRNS_SUBCLASSCODE";
	/**
	 * 住院医生站胸痛中心病历classCodeConfig
	 */
	private static String odiClassCodeConfig = "AMI_ODI_CLASSCODE";
	/**
	 * 住院医生站胸痛中心病历subClassCodeConfig
	 */
	private static String odiSubclassCodeConfig = "AMI_ODI_SUBCLASSCODE";

	/**
	 * 初始化方法
	 */
	public void onInit (){
		super.onInit();
		table = (TTable) this.getComponent("TABLE");
		word = (TWord) this.getComponent("TWORD");
		callFunction("UI|TABLE|addEventListener", "TABLE->" + TTableEvent.CLICKED, this, "onTABLEClicked");		
	} 


	/**
	 * 病案号回车事件
	 */
	public void onMrNo(){
		this.onQuery();
	}

	
	/**
	 * 查询
	 */
	public void onQuery(){
		String mrNo = "";
		// 合并病案号
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
			setValue("MR_NO", pat.getMrNo());
		}
		String sql="SELECT  A.TRIAGE_NO, TO_CHAR(A.COME_TIME, 'HH24:MI:SS') COME_TIME, B.LEVEL_DESC LEVEL_CODE," +
				"A.SEX_CODE,A.ADM_TYPE,A.IDNO,A.PAT_NAME,A.MR_NO,A.ADM_DATE,A.CASE_NO,"+ 
				"A.DEPT_CODE, A.CLINICAREA_CODE,A.TRIAGE_USER" +
				" FROM ERD_EVALUTION A, REG_ERD_LEVEL B" +
				" WHERE A.LEVEL_CODE = B.LEVEL_CODE AND A.TRIAGE_NO = "+"(SELECT TRIAGE_NO FROM (SELECT MAX(TRIAGE_NO) AS TRIAGE_NO, MR_NO FROM ERD_EVALUTION WHERE MR_NO='"+mrNo+"'))";

		System.out.println("===sql:"+sql);
		TParm tableParm = new TParm(TJDODBTool.getInstance().select(sql));
		System.out.println("tableParm---------->"+tableParm);
		table.setParmValue(tableParm);
	}

	
	/**
	 * table单击事件
	 */
	public void onTABLEClicked(int row){
		TParm tableParm = table.getParmValue();
		caseNo = tableParm.getValue("CASE_NO", row);
		triageNo = tableParm.getValue("TRIAGE_NO", row);
//		saveFiles = getAMIFile(caseNo, "AMI_ALL_CLASSCODE", "AMI_ALL_SUBCLASSCODE");
		saveFiles = EMRPublicTool.getInstance().getEmrFile(triageNo, allClassCodeConfig, allSubClassCodeConfig);
		
		if(saveFiles == null
				|| saveFiles[0] == null || saveFiles[0].trim().length()<=0 
				|| saveFiles[1] == null || saveFiles[1].trim().length()<=0 
				|| saveFiles[2] == null || saveFiles[2].trim().length()<=0){// 新建病历
			update = false;
//			saveFiles = getErdLevelTemplet("AMI_ALL_SUBCLASSCODE");
			saveFiles = EMRPublicTool.getInstance().getEmrTemplet(allSubClassCodeConfig);
			word.onOpen(saveFiles[0], saveFiles[1], 2, true);

			String mrNo = tableParm.getValue("MR_NO", row);
			Pat pat = Pat.onQueryByMrNo(mrNo);
			this.setCaptureValueArray(word, "ID_NO", pat.getIdNo());
			this.setCaptureValueArray(word, "MR_NO", pat.getMrNo());
			this.setCaptureValueArray(word, "PAT_NAME", pat.getName());
			this.setCaptureValueArray(word, "AGE", OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate()));
			this.setCaptureValueArray(word, "PHONE_NO", pat.getCellPhone());		
			if(pat.getSexCode().equals("1")){
				setCheckBoxChooseChecked(word, "male",  true);
			}else if(pat.getSexCode().equals("2")){
				setCheckBoxChooseChecked(word, "female", true);
			}
			TParm parm = new TParm();
			parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT", pat.getMrNo());
			parm.setData("FILE_HEAD_TITLE_IPD_NO", "TEXT",pat.getIpdNo());
			word.setWordParameter(parm);

			this.setData();
			word.setCanEdit(false);
			word.update();		
		}else{// 打开已有病历
			update=true;
			word.onOpen(saveFiles[0], saveFiles[1], 3, true);
			word.setCanEdit(false);
			word.update();
		}

	}

	/**
	 * word赋值
	 */
	public void setData(){
		// 急诊护士胸痛中心记录
		TWord word0 = new TWord();
		String[] saveFiles0;
//		saveFiles0 = RegPreedTool.getInstance().getPreedFile(triageNo);
		saveFiles0 = EMRPublicTool.getInstance().getEmrFile(triageNo, preClassCodeConfig, preSubclassCodeConfig);
		if(saveFiles0 != null 
				&& saveFiles0[0] != null && saveFiles0[0].trim().length()>0 
				&& saveFiles0[1] != null && saveFiles0[1].trim().length()>0 
				&& saveFiles0[2] != null && saveFiles0[2].trim().length()>0){
			word0.onOpen(saveFiles0[0], saveFiles0[1], 3, false);
			String namesCapture0 = "PAT_LOG_TIME;START_ADD_2;START_TIME;CALL_HELP_TIME;ASSESSMENT_LOG;DR_ATTEN_TIME;DOOR_TIME;"
					+ "IN_ADMIT_TIME;TRANSFER_DOOR_TIME_OUT;TRANSFER_DECICE_TIME;TRANSFER_AMBULANCE_TIME;"
					+ "TRANSFER_LEAVE_TIME;TRANSFER_DOOR_TIME_IN;TRANSFER_IN_ADMIT_TIME;BYPASS_EMERGENCY_Y_ARRIVE_TIME;"
					+ "BYPASS_EMERGENCY_N_ARRIVE_TIME;BYPASS_EMERGENCY_N_LEAVE_TIME;SELF_DOOR_TIME;SELF_ADMIT_TIME;"
					+ "SELF_CUU_ARRIVE_TIME;IN_HP_COME_DEPT;IN_HP_CONS_TIME;IN_HP_LEAVE_DEPT_TIME";
			setCaptureValueArray2(namesCapture0, word0, word);
			
			String namesSingleChoose0 = "START_ADD;AT_HOME;CALL_HELP;ERD_TYPE;TRANSFER_NET;TRANSFER_HP_NAME;"
					+ "TRANSFER_TO_CCR;sense";
			setESingleChooseValue2(namesSingleChoose0, word0, word);
			
			String namesCheckBox0 = "NATIVE_REGION_120;OTHER_REGION_120;NATIVE_MEDICAL_INSTITUTION;CNST_CHEST_PAIN;"
					+ "INTER_CHEST_PAIN;CHEST_PAIN_BETTER;ABDOMINAL_PAIN;HARD_BREATHE;SHOCK;CARDIAC_FAILURE;"
					+ "MALIGNANT_ARRHYTHMIA;CARDIOPULMONARY_RESUSCITAT;BLEED;OTHER_ASSESSMENT;AMBULANCE_120;AMBULANCE_IN;"
					+ "AMBULANCE_OUT;BYPASS_EMERGENCY_Y;BYPASS_EMERGENCY_N;BYPASS_CCU_Y;BYPASS_CCU_N";
			setCheckBoxChooseChecked2(namesCheckBox0, word0, word);
			word.update();
		}else{
			System.out.println("===急诊护士胸痛记录不存在===");
		}

		// 胸痛急诊医生记录
		TWord word1 = new TWord();
		String[] saveFiles1;
//		String classCodeConfig = "AMI_EDDR_CLASSCODE";
//		String subclassCodeConfig = "AMI_EDDR_SUBCLASSCODE";
//		saveFiles1 = EMRAMITool.getInstance().getSaveFile(caseNo,classCodeConfig,subclassCodeConfig);
		saveFiles1 = EMRPublicTool.getInstance().getEmrFile(caseNo, eddrClassCodeConfig, eddrSubclassCodeConfig);
		if(saveFiles1 != null 
				&& saveFiles1[0] != null && saveFiles1[0].trim().length()>0 
				&& saveFiles1[1] != null && saveFiles1[1].trim().length()>0 
				&& saveFiles1[2] != null && saveFiles1[2].trim().length()>0){
			word1.onOpen(saveFiles1[0], saveFiles1[1], 3, false);
			String namesCapture1 = "FIRST_DR;FIRST_TIME;FIRST_OUT_ECG_TIME;FIRST_IN_ECG_TIME;respire;pulse;"
					+ "heartRate;systolicBloodPressure;diastolicBloodPressure;TNI_BLOOD_DRAWING_TIME;"
					+ "REPORT_TIME;cTnl;serum_creatinine;REG_DATE;ecgDiagnosisTime;TeleECGTime;"
					+ "priDigTime;priDigDr;emradmFirstAdmTime;emradmClticDose;emradmClticAdmTime;"
					+ "emradmStatinsDesc;emradmStatinsDose;emradmStatinsAdmTime;preadmAspirinDose;"
					+ "preadmFirstAdmTime;preadmClticDose;preadmClticAdmTime;preadmStatinsDesc;"
					+ "preadmStatinsDose;preadmStatinsAdmTime;stemiCallTime;stemiFirstDigTime;"
					+ "stemiOtherDesc;nstemiCallInTime;nstemiFirstMedTime;nstemiOtherDesc;ninfOtherDesc;"
					+ "timiScore;graceScore;acMedTime;acMedDesc;acMedDose;acMedDoseUnit;"
					+ "throInfoStart;throInfoSign;throStartTime;throTime;nacs_other_desc;nacs_treatment;"
					+ "NOT_ACS_TIME;nacs_patient_whereabouts;nacs_dc;noncardiac_other_desc;noncardiac_treatment;"
					+ "noncardiac_time;noncardiac_patient_whereabouts;noncardiac_dc;adConsInTime;adConsOutNotice;"
					+ "adConsOutTime;pulmAcStart";
			setCaptureValueArray2(namesCapture1, word1, word);
			
			String namesSingleChoose1 = "FIRST_MEDICAL_ORG;Killip;throLoc;adType";
			setESingleChooseValue2(namesSingleChoose1, word1, word);
			
			String namesCheckBox1 = "R_ECG_T_Y;R_ECG_T_N;ccpcAmiHeartUt;ccpcAmiNuclide;priDigDiagnosising;"
					+ "priDigStemi;priDigUa;priDigNstemi;priDigDissectingAneurysm;priDigApte;priDigNacs;"
					+ "priDigNccp;priDigUnknow;emradmClopidogrel;emradmTicagrelor;preadmClopidogrel;preadmTicagrelor;"
					+ "stemiEmrPci;stemiThro;stemiRemedy;stemiGraphicOnly;stemiSelPci;stemiSelGraphic;"
					+ "stemiCabg;stemiNoInfusion;stemiOther;EMR_INT_THE;nstemiGraphicOnly;nstemiInter24h;"
					+ "nstemiInter72h;nstemiGraphicOnlyE;nstemiInterSel;nstemiGuard;nstemiCabg;nstemiOther;"
					+ "ninfNoChestPain;ninfVitalSignOk;ninfTimeMissed;ninfBleed;ninfRhi;ninfEconomic;ninfQuit;"
					+ "ninfOther;graceCardiacArrest;graceStChange;graceMkRice;FIRST_ANTCOAGULANT_ADMINISTRATION_N;"
					+ "T_ANTCOAGULANT_ADMINISTRATION_Y;FIT;NOT_FIT;thrombolytic;IS_ARRIVE;NOT_ARRIVE;throMedicine1;"
					+ "throMedicine2;throMedicine3;throDose_all;throDose_half;is_throAgn;not_throAgn;arrhythmia;"
					+ "DCM;ICM;HCM;myocarditis;CHD;VHD;oldMyocardialInfarction;angina;palpitation;atrialFibrillation;"
					+ "hypertension;heartFailure;AF;R_ON_T;APB;SVT;nacs_other;RespiratorySystemDisease;"
					+ "DigestiveSystemDisease;NervousSystemDisease;MentalDisorders;MusculoskeletalDisorders;"
					+ "DiseaseOfSkinSystem;noncardiac_other;pulmonaryEmbolismClassHign;pulmonaryEmbolismClassMiddle;"
					+ "pulmonaryEmbolismClassLow;is_thrombolytic";
			setCheckBoxChooseChecked2(namesCheckBox1, word1, word);
			word.update();
		}else{
			System.out.println("===急诊医生胸痛记录不存在===");
		}
		
		// 介入室医生-胸痛中心记录
		TWord word2 = new TWord();
		String[] saveFiles2; 
//		saveFiles2 = EMROpeDrStationTool.getInstance().getOpeDrStationEmrFile(caseNo);
		saveFiles2 = EMRPublicTool.getInstance().getEmrFile(caseNo, irdrClassCodeConfig, irdrSubclassCodeConfig);
		if(saveFiles2 != null 
				&& saveFiles2[0] != null && saveFiles2[0].trim().length()>0 
				&& saveFiles2[1] != null && saveFiles2[1].trim().length()>0 
				&& saveFiles2[2] != null && saveFiles2[2].trim().length()>0){
			word2.onOpen(saveFiles2[0], saveFiles2[1], 3, false);
			String namesCapture2 = "DECIDE_DR;INT_DR;DECIDE_TIME;INFO_CONSENT_START_TIME;"
					+ "INFO_CONSENT_SIGN_TIME;BEFOR_TIMI;AFTER_TIMI;OTHER_RESN_DESC";
			setCaptureValueArray2(namesCapture2, word2, word);
			
			String namesCheckBox2 = "DES_APPLY_Y;DES_APPLY_N;D2B_DELAY_Y;D2B_DELAY_N;DELAY_SUBCLINICAL;DELAY_NO_FAMILY;"
					+ "DELAY_WRONG_DECISION;DELAY_PROCEDURE;DELAY_ERD;DELAY_COMPLICATION;DELAY_NINF_TIME_MISSED;"
					+ "DELAY_NOT_ERD;DELAY_NON_STAFF;DELAY_NON_MED;DELAY_INFO_CONSENT;DELAY_UNSTABLE;"
					+ "DELAY_TO_CCR;DELAY_CCR_BUSY;DELAY_TRANS;DELAY_FEE;DELAY_CONSULTATION;OTHER_RESN";
			setCheckBoxChooseChecked2(namesCheckBox2, word2, word);
			word.update();	
		}else{
			System.out.println("===介入室医生胸痛记录不存在===");
		}
		// 介入室护士-胸痛中心记录
		TWord word3 = new TWord();
		String[] saveFiles3;
//		saveFiles3 = OPEINTSaveTool.getInstance().getELFile(caseNo);
		saveFiles3 = EMRPublicTool.getInstance().getEmrFile(caseNo, irnsClassCodeConfig, irnsSubclassCodeConfig);
		if(saveFiles3 != null 
				&& saveFiles3[0] != null && saveFiles3[0].trim().length()>0 
				&& saveFiles3[1] != null && saveFiles3[1].trim().length()>0 
				&& saveFiles3[2] != null && saveFiles3[2].trim().length()>0){
			word3.onOpen(saveFiles3[0], saveFiles3[1], 3, false);
			String namesCapture3 = "CCR_START_TIME;CCR_READY_TIME;PAT_ARRIVE_TIME;PUNCTURE_START_TIME;"
					+ "PUNCTURE_END_TIME;GRAPHY_START_TIME;GRAPHY_END_TIME;SUR_START_TIME;"
					+ "PBMV_TIME;SUR_END_TIME;STENT_GRAFT_IN_CCU_TIME;STENT_GRAFT_START_TIME;"
					+ "STENT_GRAFT_END_TIME";
			setCaptureValueArray2(namesCapture3, word3, word);
			word.update();
		}else{
			System.out.println("===介入室护士胸痛记录不存在===");
		}

		// 手术室护士-胸痛中心记录
		TWord word4 = new TWord();
		String[] saveFiles4;
//		saveFiles4 = OPESURSaveTool.getInstance().getELFile(caseNo);
		saveFiles4 = EMRPublicTool.getInstance().getEmrFile(caseNo, srnsClassCodeConfig, srnsSubclassCodeConfig);
		if(saveFiles4 != null 
				&& saveFiles4[0] != null && saveFiles4[0].trim().length()>0 
				&& saveFiles4[1] != null && saveFiles4[1].trim().length()>0 
				&& saveFiles4[2] != null && saveFiles4[2].trim().length()>0){
			word4.onOpen(saveFiles4[0], saveFiles4[1], 3, false);
			String namesCapture4 = "CABG_DECIDE_TIME;CABG_START_TIME;CABG_END_TIME;THOR_DECIDE_TIME;"
					+ "THOR_INFO_CONT_START;THOR_INFO_CONT_SIGN;THOR_START_TIME;THOR_END_TIME;THOR_RESULT";
			setCaptureValueArray2(namesCapture4, word4, word);
			word.update();
		}else{
			System.out.println("===手术室护士胸痛记录不存在===");
		}

		// CT室-胸痛中心记录
		String ctSql = "SELECT CASE_NO, CT_NOTICE_TIME, CT_RESP_TIME, CT_ARRIVE_TIME, PAT_ARRIVE_TIME, "
				+ "CT_START_TIME, CT_REPORT_TIME FROM AMI_CT_RECORD WHERE CASE_NO='"+caseNo+"'";
		TParm ctResult = new TParm(TJDODBTool.getInstance().select(ctSql));
		if(ctResult.getErrCode()<0){
			System.out.println("===CT室胸痛记录不存在===");
			return;
		}
		if(ctResult.getCount()>0){
			System.out.println("------ctResult="+ctResult);
			setCaptureValueArray(word, "CT_NOTICE_TIME", timestampToString(ctResult.getTimestamp("CT_NOTICE_TIME", 0), "yyyy/MM/dd HH:mm"));
			setCaptureValueArray(word, "CT_RESP_TIME", timestampToString(ctResult.getTimestamp("CT_RESP_TIME", 0), "yyyy/MM/dd HH:mm"));
			setCaptureValueArray(word, "CT_ARRIVE_TIME", timestampToString(ctResult.getTimestamp("CT_ARRIVE_TIME", 0), "yyyy/MM/dd HH:mm"));	
			setCaptureValueArray(word, "PAT_ARRIVE_CT_TIME", timestampToString(ctResult.getTimestamp("PAT_ARRIVE_TIME", 0), "yyyy/MM/dd HH:mm"));			
			setCaptureValueArray(word, "CT_START_TIME", timestampToString(ctResult.getTimestamp("CT_START_TIME", 0), "yyyy/MM/dd HH:mm"));
			setCaptureValueArray(word, "CT_REPORT_TIME", timestampToString(ctResult.getTimestamp("CT_REPORT_TIME", 0), "yyyy/MM/dd HH:mm"));
		}

		// 住院医生站胸痛中心记录
		TWord word5 = new TWord();
		String[] saveFiles5;
//		String classCodeConfig5 = "AMI_ODI_CLASSCODE";
//		String subclassCodeConfig5 = "AMI_ODI_SUBCLASSCODE";		
//		saveFiles5 = EMRAMITool.getInstance().getSaveFile(caseNo,classCodeConfig5,subclassCodeConfig5);
		saveFiles5 = EMRPublicTool.getInstance().getEmrFile(caseNo, odiClassCodeConfig, odiSubclassCodeConfig);
		if(saveFiles5 != null 
				&& saveFiles5[0] != null && saveFiles5[0].trim().length()>0 
				&& saveFiles5[1] != null && saveFiles5[1].trim().length()>0 
				&& saveFiles5[2] != null && saveFiles5[2].trim().length()>0){
			word5.onOpen(saveFiles5[0], saveFiles5[1], 3, false);
			String namesCapture5 = "OUT_DIG_TIME;TOTAL_FEE;LAPSE_TRANS_TIME;LAPSE_TRANS_HOS;"
					+ "LAPSE_DIE_TIME;LAPSE_DIE_NONCARDIAGENIC_DESC";
			setCaptureValueArray2(namesCapture5, word5, word);
			
			String namesCheckBox5 = "STATIN_24H_Y;STATIN_24H_N;BETA_BLOCKERS_Y;BETA_BLOCKERS_N;"
					+ "OUT_DIG_STEMI;OUT_DIG_NSTEMI;OUT_DIG_UA;OUT_DIG_DISSECTING_ANEURYSM;"
					+ "OUT_DIG_APTE;OUT_DIG_NACS;OUT_DIG_NCCP;LEFT_HEART_FAILURE_Y;LEFT_HEART_FAILURE_N;"
					+ "LAPSE_OUT_HOS;TRANS_HOS;DIE;CURED;LAPSE_WELL;LAPSE_BRAIN_DIE;OTHER;LAPSE_DIE_CARDIAGENIC;"
					+ "LAPSE_DIE_NONCARDIAGENIC;OUT_MED_DAPT;OUT_MED_ACEI_ARB;OUT_MED_STATIN;OUT_MED_BETA_BLOCKERS";
			setCheckBoxChooseChecked2(namesCheckBox5, word5, word);
			word.update();
		}else{
			System.out.println("===住院医生胸痛记录不存在===");
		}
	}

	/**
	 * 保存
	 */
	public void onSave(){

		String path = "";
		String fileName = "";
		if(update){
			System.out.println("---更新数据---");
			path = saveFiles[0];
			fileName = saveFiles[1];
		}else{
			System.out.println("---插入数据---");
//			TParm erdParm = saveELFile(caseNo, "AMI_ALL_CLASSCODE", "AMI_ALL_SUBCLASSCODE", saveFiles[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, allClassCodeConfig, allSubClassCodeConfig, saveFiles[1]);
			if(erdParm.getErrCode()<0){
				this.messageBox("ERR:" + erdParm.getErrCode() + erdParm.getErrText() +
						erdParm.getErrName());
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}
		word.setMessageBoxSwitch(false);
		word.onSaveAs(path, fileName, 3);
		word.setCanEdit(true);
		this.messageBox("保存成功！");
		//		this.closeWindow();

	}

	
	/**
	 * 设置抓取控件值
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setCaptureValueArray(TWord word, String name, String value) {
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(name == null){
			System.out.println("name is null");
			return;
		}
		if(value == null){
			System.out.println("value is null");
			return;
		}
		if(value.equals("")){
			value = " ";
		}
		ECapture ecap = (ECapture)word.findObject(name, EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->name控件不存在");
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		word.pasteString(value);
	}

	
	/**
	 * 获取抓取控件值
	 * @param word
	 * @param name
	 * @return
	 */
	public String getCaptureValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ECapture ecap = (ECapture) word.findObject(name, EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->name控件不存在");
			return null;
		}
		return ecap.getValue();
	}
	
	
	/**
	 * 批次插入抓取数据
	 * @param names 1;2;3
	 * @param word 数据来源
	 */
	public void setCaptureValueArray2(String names, TWord srcWord, TWord outWord){
		if(names == null){
			System.out.println("names is null");
			return;
		}
		if(srcWord == null){
			System.out.println("srcWord is null");
			return;
		}
		if(outWord == null){
			System.out.println("outWord is null");
			return;
		}
		String[] nameArr = names.split(";"); 
		for(int i=0; i<nameArr.length; i++){
			String value = getCaptureValue(srcWord, nameArr[i]);
			if(value != null){
				setCaptureValueArray(outWord, nameArr[i], value);
			}			
		}	
	}

	
	/**
	 * 设置复选框状态
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setCheckBoxChooseChecked(TWord word, String name, boolean value){
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(name == null){
			System.out.println("name is null");
			return;
		}
		ECheckBoxChoose cbc = (ECheckBoxChoose) word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);
		if(cbc == null) {
			System.out.println("word--->name控件不存在");
			return;	
		}
		cbc.setChecked(value);
	}
	
	
	/**
	 * 获取复选框控件
	 * @param word
	 * @param name
	 * @return
	 */
	public ECheckBoxChoose getCheckBoxChoose(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);//单选框
		if(cbc == null){
			System.out.println("word--->name控件不存在");
		}
		return cbc;
	}

	
	/**
	 * <p>批次设置复选框状态</p>
	 * @param names
	 * @param word
	 */
	public void setCheckBoxChooseChecked2(String names, TWord srcWord, TWord outWord){
		if(names == null){
			System.out.println("names is null");
			return;
		}
		if(srcWord == null){
			System.out.println("srcWord is null");
			return;
		}
		if(outWord == null){
			System.out.println("outWord is null");
			return;
		}
		String[] nameArr = names.split(";"); 
		for(int i=0; i<nameArr.length; i++){
			ECheckBoxChoose cbc = getCheckBoxChoose(srcWord, nameArr[i]);
			if(cbc != null){
				setCheckBoxChooseChecked(outWord, nameArr[i], cbc.isChecked());
			}
		}
	}
	
	
	/**
	 * 获取单选框值
	 * @param word
	 * @param name
	 * @return
	 */
	public String getESingleChooseValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if(sc == null){ 
			System.out.println("word--->name控件不存在");
			return null;		
		}
		return sc.getText();
	}


	/**
	 * 设置单选框值
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setESingleChooseValue(TWord word, String name, String value){
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(name == null){
			System.out.println("name is null");
			return;
		}
		if(value == null){
			System.out.println("value is null");
			return;
		}
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if(sc == null) {
			System.out.println("word--->name控件不存在");
			return;
		}
		sc.setText(value);
	}
	

	/**
	 * 批次插入单选框数据
	 * @param names
	 * @param srcWord
	 * @param outWord
	 */
	public void setESingleChooseValue2(String names, TWord srcWord, TWord outWord){
		if(names == null){
			System.out.println("names is null");
			return;
		}
		if(srcWord == null){
			System.out.println("srcWord is null");
			return;
		}
		if(outWord == null){
			System.out.println("outWord is null");
			return;
		}
		String[] nameArr = names.split(";"); 
		for(int i=0; i<nameArr.length; i++){
			String value = getESingleChooseValue(srcWord, nameArr[i]);
			if(value != null){
				setESingleChooseValue(outWord, nameArr[i], value);
			}
		}
	}



	/**
	 * <p>Timestamp->String</p>
	 * @param ts
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);// yyyy/MM/dd HH:mm:ss
		try {
			//方法一
			tsStr = sdf.format(ts);
			//方法二
			//			tsStr = ts.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}
	


}
