package com.javahis.ui.reg;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.EMicroField;
import com.dongyang.tui.text.ENumberChoose;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;

import jdo.emr.EMRAMITool;
import jdo.emr.EMRPublicTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

/**
 * <p>��ʹ���ļ���ҽ����¼</p>
 * <p>ֻ���½��Ĳ����Ż�������²�����1����ʼ������ 2�����ü���</p>
 * <p>�Ѵ��ڵĲ���ֻ���ֶ��޸�</p>
 * @author wangqing
 */

//20170822
//1��Timi��Grace����������Ҫ����Ӥ��
//2��Timi�����������ִ���
//3����ʹ���ļ���ҽ����¼����ҽ�������Ǽ���ҽ������Ҫ��sql
//4����ʹ���ļ���ҽ����¼ģ�壬��ACS������Դ�Դ����ʩ��Ϊ��ѡ����������������ѡ��

public class REGDoctorTriageControl extends TControl {
	/**
	 * ��ʹ���ﻤʿ��¼����
	 */
	private TWord wordPRE;
	/**
	 * ��ʹ����ҽ����¼����
	 */
	private TWord wordEDDR;
	/**
	 * Timi���ֲ���
	 */
	private TWord wordTIMI;
	/**
	 * Grace���ֲ���
	 */
	private TWord wordGRACE;
	/**
	 * APTE����
	 */
	private TWord wordAPTE;
	/**
	 * ��ʹ���ﻤʿ��¼����·��
	 */
	private String[] savePREFiles;
	/**
	 * ��ʹ����ҽ����¼����·��
	 */
	private String[] saveEDDRFiles;
	/**
	 * Timi���ֲ���·��
	 */
	private String[] saveTIMIFiles;
	/**
	 * Grace���ֲ���·��
	 */
	private String[] saveGRACEFiles;
	/**
	 * APTE����·��
	 */
	private String[] saveAPTEFiles;
	/**
	 * ��ʹ����ҽ����¼�����½����ߴ򿪣�true ��ʾ�����в��� false��ʾ�½�
	 */
	private boolean updateEDDR = false;
	/**
	 * Timi���ֲ����½����ߴ򿪣�true ��ʾ�����в��� false��ʾ�½�
	 */
	private boolean updateTIMI = false;
	/**
	 * Grace���ֲ����½����ߴ򿪣�true ��ʾ�����в��� false��ʾ�½�
	 */
	private boolean updateGRACE = false;
	/**
	 * APTE�����½����ߴ򿪣�true ��ʾ�����в��� false��ʾ�½�
	 */
	private boolean updateAPTE = false;
	/**
	 * ��ʹ���ﻤʿ��¼����classCodeConfig
	 */
	private static String preClassCodeConfig = "AMI_PRE_CLASSCODE";
	/**
	 * ��ʹ���ﻤʿ��¼����subClassCodeConfig
	 */
	private static String preSubclassCodeConfig = "AMI_PRE_SUBCLASSCODE";

	/**
	 * ��ʹ����ҽ����¼����classCodeConfig
	 */
	private static String eddrClassCodeConfig = "AMI_EDDR_CLASSCODE";
	/**
	 * ��ʹ����ҽ����¼����subClassCodeConfig
	 */
	private static String eddrSubclassCodeConfig = "AMI_EDDR_SUBCLASSCODE";
	/**
	 * Timi���ּ�¼����classCodeConfig
	 */
	private static String timiClassCodeConfig = "AMI_TIMI_CLASSCODE";
	/**
	 * Timi���ּ�¼����subClassCodeConfig
	 */
	private static String timiSubclassCodeConfig = "AMI_TIMI_SUBCLASSCODE";
	/**
	 * Grace���ּ�¼����classCodeConfig
	 */
	private static String graceClassCodeConfig = "AMI_GRACE_CLASSCODE";
	/**
	 * Grace���ּ�¼����subClassCodeConfig
	 */
	private static String graceSubclassCodeConfig = "AMI_GRACE_SUBCLASSCODE";
	/**
	 * APTE��¼����classCodeConfig
	 */
	private static String apteClassCodeConfig = "AMI_APTE_CLASSCODE";
	/**
	 * APTE��¼����subClassCodeConfig
	 */
	private static String apteSubclassCodeConfig = "AMI_APTE_SUBCLASSCODE";

	/**
	 * ϵͳ�������
	 */
	private TParm sysParm;	
	/**
	 * ��������
	 */
	private String caseNo = "";
	/**
	 * ������
	 */
	private String mrNo = "";
	/**
	 * ���˺�
	 */
	private String triageNo;
	/**
	 * ��������
	 */
	private String patName = "";
	/**
	 * �����Ա�
	 */
	private String patSex = "";
	/**
	 * ��������
	 */
	private String patAge = "";

	/**
	 * ������������ʱ��
	 */
	private String triageTime = "";
	/**
	 * Ժ���״��ĵ�ͼʱ��
	 */
	private String firstInEcgTime = "";
	/**
	 * �ĵ�ͼ���ʱ��
	 */
	private String ecgDiagnosisTime = "";
	/**
	 * ������������ʱ�������ر�ʶ
	 */
	boolean triageTimeLazyFlg = false;
	/**
	 * Ժ���״��ĵ�ͼʱ�������ر�ʶ
	 */
	boolean firstInEcgTimeLazyFlg = false;


	/**
	 * ��ʼ��
	 */
	public void onInit() {	
		super.onInit();
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			sysParm = (TParm) this.getParameter();
			System.out.println("===sysParm:"+sysParm);
			caseNo = sysParm.getValue("CASE_NO");
			mrNo = sysParm.getValue("MR_NO");
			Pat pat = Pat.onQueryByMrNo(mrNo);
			patName = pat.getName();
			patSex = ("1".equals(pat.getSexCode())?"��":"Ů");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());			
		}else{
			this.messageBox("ϵͳ����������󣡣���");
		}
		TParm result = EMRAMITool.getInstance().getErdEvalutionDataByCaseNo(caseNo);
		triageNo = result.getValue("TRIAGE_NO",0);
		this.setValue("PAT_NAME", patName);
		this.setValue("TRIAGE_NO", result.getValue("TRIAGE_NO",0));
		this.setValue("LEVEL_CODE", result.getValue("LEVEL_CODE",0));
		this.setValue("COME_TIME", result.getValue("GATE_TIME",0));// ������ʱ��

		initJHW();
		// add by wangqing Ĭ��ѡ����ҽ��վ��ʹ���ļ�¼����
		TTabbedPane tabbledPane1 = (TTabbedPane) this.getComponent("tTabbedPane_0");
		tabbledPane1.setSelectedIndex(1);
	}

	/**
	 * ��ʼ���ṹ������
	 */
	public void initJHW(){
		String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
		String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";

		//��ʹ���ļ��ﻤʿ��¼�������в�����
		wordPRE = (TWord) this.getComponent("tWord_0");
//		wordPRE.setName("tWord_0");
		savePREFiles = EMRPublicTool.getInstance().getEmrFile(triageNo, preClassCodeConfig, preSubclassCodeConfig);
		if(savePREFiles != null 
				&& savePREFiles[0] != null && savePREFiles[0].trim().length()>0 
				&& savePREFiles[1] != null && savePREFiles[1].trim().length()>0 
				&& savePREFiles[2] != null && savePREFiles[2].trim().length()>0){
			wordPRE.onOpen(savePREFiles[0], savePREFiles[1], 3, false);
			wordPRE.setCanEdit(false);
			wordPRE.update();
		}

		//��ʹ���ļ���ҽ����¼
		wordEDDR = (TWord) this.getComponent("tWord_1");
//		wordEDDR.setName("tWord_1");
		saveEDDRFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, eddrClassCodeConfig, eddrSubclassCodeConfig);
		if (saveEDDRFiles != null 
				&& saveEDDRFiles[0] != null && saveEDDRFiles[0].trim().length()>0 
				&& saveEDDRFiles[1] != null && saveEDDRFiles[1].trim().length()>0 
				&& saveEDDRFiles[2] != null && saveEDDRFiles[2].trim().length()>0) {// �����еĲ���
			updateEDDR = true;
			wordEDDR.onOpen(saveEDDRFiles[0], saveEDDRFiles[1], 3, false);
			// add by wangqing 20170828 ���벡����
			TParm inParam2 = new TParm();
			inParam2.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordEDDR.setWordParameter(inParam2);
			wordEDDR.setCanEdit(true);
			wordEDDR.update();		
		}else{// ����
			updateEDDR = false;
			saveEDDRFiles = EMRPublicTool.getInstance().getEmrTemplet(eddrSubclassCodeConfig);
			if(saveEDDRFiles == null 
					|| saveEDDRFiles[0] == null || saveEDDRFiles[0].trim().length()<=0 
					|| saveEDDRFiles[1] == null || saveEDDRFiles[1].trim().length()<=0 
					|| saveEDDRFiles[2] == null || saveEDDRFiles[2].trim().length()<=0){
				return;
			}
			wordEDDR.onOpen(saveEDDRFiles[0], saveEDDRFiles[1], 2, false);
			// add by wangqing 20171120 start
			// Ժ���״��ĵ�ͼʱ�䡢�ĵ�ͼ���ʱ�䣨Ժ���״��ĵ�ͼʱ��+1min��
			if(!firstInEcgTimeLazyFlg){
				String firstInEcgTimeSql = " SELECT FIRST_IN_ECG_TIME FROM AMI_ERD_NS_RECORD WHERE TRIAGE_NO='"+triageNo+"' ";
				TParm result = new TParm(TJDODBTool.getInstance().select(firstInEcgTimeSql));
				if(result.getErrCode()<0){
					messageBox("ERR:" + result.getErrCode() + result.getErrText() +
							result.getErrName());
					return;
				}
				if(result.getCount()>0){	
					if(result.getValue("FIRST_IN_ECG_TIME", 0) != null && result.getValue("FIRST_IN_ECG_TIME", 0).trim().length()>0){
						SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
						firstInEcgTime = dateFormat.format(result.getTimestamp("FIRST_IN_ECG_TIME", 0));					
						long temp = result.getTimestamp("FIRST_IN_ECG_TIME", 0).getTime();		
						temp += 1*60*1000;// ��һ����
						Date date=new Date(temp);	
						ecgDiagnosisTime = dateFormat.format(date);
					}					
				}
			}	
			this.setECaptureValue(wordEDDR, "FIRST_IN_ECG_TIME", firstInEcgTime);// Ժ���״��ĵ�ͼʱ��	
			// add by wangqing 20171120 end
			// ��Ժ�Һ�ʱ��
			String regTime = "";
			String regTimeSql = " SELECT TO_CHAR (REG_DATE, 'YYYY/MM/DD HH24:MI') AS REG_DATE FROM REG_PATADM WHERE CASE_NO='"+caseNo+"' ";// modified by wangqing 20170701
			TParm regTimeResult = new TParm(TJDODBTool.getInstance().select(regTimeSql));		
			if(regTimeResult.getErrCode()<0){
				this.messageBox("ERR:" + regTimeResult.getErrCode() + regTimeResult.getErrText() +
						regTimeResult.getErrName());
				return;
			}
			if(regTimeResult.getCount()>0){
				regTime = regTimeResult.getValue("REG_DATE", 0);
			}
			this.setECaptureValue(wordEDDR, "REG_DATE", regTime);	

//			// ʱ���ʽ	
//			this.setECaptureValue(wordEDDR, "TeleECGTime", sysDate3);// Զ���ĵ�ͼ����ʱ�䣬auto,������

			// add by wangqing 20170801 start
			// ��ʼ������ҽʦ�����ؼ�
			// modified by wangqing 20170822 �޸�sql
			String firstDrSQL = " SELECT A.USER_ID, B.USER_NAME FROM SYS_OPERATOR_DEPT A LEFT JOIN SYS_OPERATOR B ON A.USER_ID = B.USER_ID "
					+ "WHERE A.DEPT_CODE='0202' AND A.MAIN_FLG = 'Y' AND B.ROLE_ID IN ('ODO', 'OIDR') "
					+ "ORDER BY A.USER_ID";
			TParm firstDrResult = new TParm(TJDODBTool.getInstance().select(firstDrSQL));
			ESingleChoose firstDrSC=(ESingleChoose)wordEDDR.findObject("FIRST_DR", EComponent.SINGLE_CHOOSE_TYPE);//����ҽ��������ѡ
			firstDrSC.addData(" ", "0");
			for(int i=0; i<firstDrResult.getCount(); i++){
				firstDrSC.addData(firstDrResult.getValue("USER_NAME", i), firstDrResult.getValue("USER_ID", i));
			}
			// add by wangqing 20170801 end

			//ȷ���Ƿ��п����೬����ҽ�� 		
			TParm ccpcAmiHeartUtParm = EMRAMITool.getInstance().getCheckOrderByCaseNoAndOrderCode(caseNo,"Y0301");
			//���п����೬����ҽ�� 	
			if (ccpcAmiHeartUtParm.getCount() > 0) {
				this.setCheckBoxChooseStatus(wordEDDR, "ccpcAmiHeartUt", true);// ���೬��
			}
			//ȷ���Ƿ��п����ص�ҽ��
			TParm ccpcAmiNuclideParm = EMRAMITool.getInstance().getCheckOrderByCaseNoAndOrderCode(caseNo,"Y010");
			//���п����ص�ҽ��
			if (ccpcAmiNuclideParm.getCount() > 0) {
				this.setCheckBoxChooseStatus(wordEDDR, "ccpcAmiNuclide", true);// ����
			} 

			//ȷ���Ƿ��п���˾ƥ�֡��������ס�������塢��͡��ҩ�����ѪҩƷ����˨ҩƷ��ҽ��
			TParm speOpdOrderParm = EMRAMITool.getInstance().getSpecialOpdOrderByCaseNo(caseNo);
			//���п���˾ƥ�֡��������ס�������塢��͡��ҩ�����ѪҩƷ����˨ҩƷ��ҽ��
			if (speOpdOrderParm.getCount() > 0) {
				for (int i=0;i<speOpdOrderParm.getCount();i++) {
					String str = speOpdOrderParm.getValue("SYS_PHA_CLASS",i);
					String[] strArr = str.split(";");
					for (int j=0;j<strArr.length;j++) {
						if ("1".equals(strArr[j])) {// �����ҩ����˾ƥ��
							this.setECaptureValue(wordEDDR, "emradmAspirinDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmAspirinUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmFirstAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));
						} else if ("2".equals(strArr[j])) {// �����ҩ����������
							this.setCheckBoxChooseStatus(wordEDDR, "emradmClopidogrel", true);					
							this.setECaptureValue(wordEDDR, "emradmClticDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmClticUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmClticAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));
						} else if ("3".equals(strArr[j])) {// �����ҩ���������
							this.setCheckBoxChooseStatus(wordEDDR, "emradmTicagrelor", true);					
							this.setECaptureValue(wordEDDR, "emradmClticDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmClticUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmClticAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));					
						} else if ("4".equals(strArr[j])) {// �����ҩ���ڷ���͡��
							this.setECaptureValue(wordEDDR, "emradmStatinsDesc",speOpdOrderParm.getValue("ORDER_DESC",i));			
							this.setECaptureValue(wordEDDR, "emradmStatinsDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmStatinsUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmStatinsAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));
						} else if ("5".equals(strArr[j])) {// ������ҩ				

						} else if ("6".equals(strArr[j])) {// ��˨����

						}
					}
				}
			}
			TParm inParam2 = new TParm();
			inParam2.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordEDDR.setWordParameter(inParam2);
			wordEDDR.setMicroField("����", patName);
			wordEDDR.setMicroField("�Ա�", patSex);
			wordEDDR.setMicroField("����", patAge);
			wordEDDR.setCanEdit(true);
			wordEDDR.update();
		}	
		// add by wangqing �״�ҽ�ƽӴ������ı��¼�
		wordEDDR.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected2");
		// add by wangqing �������ΪSTEMI��ѡ�򵥻��¼�
		wordEDDR.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");

		//����ҽ��վ-TIMI����
		wordTIMI = (TWord) this.getComponent("tWord_2");
//		wordTIMI.setName("tWord_2");
		saveTIMIFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, timiClassCodeConfig, timiSubclassCodeConfig);
		if (saveTIMIFiles != null 
				&& saveTIMIFiles[0] != null && saveTIMIFiles[0].trim().length()>0 
				&& saveTIMIFiles[1] != null && saveTIMIFiles[1].trim().length()>0 
				&& saveTIMIFiles[2] != null && saveTIMIFiles[2].trim().length()>0) {
			updateTIMI = true;
			wordTIMI.onOpen(saveTIMIFiles[0], saveTIMIFiles[1], 3, false);
			// add by wangqing 20170828 ���벡����
			TParm inParam3 = new TParm();
			inParam3.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordTIMI.setWordParameter(inParam3);
			wordTIMI.setCanEdit(true);
			wordTIMI.update();
		} else {	
			updateTIMI = false;
			saveTIMIFiles = EMRPublicTool.getInstance().getEmrTemplet(timiSubclassCodeConfig);
			if(saveTIMIFiles == null 
					|| saveTIMIFiles[0] == null || saveTIMIFiles[0].trim().length()<=0 
					|| saveTIMIFiles[1] == null || saveTIMIFiles[1].trim().length()<=0 
					|| saveTIMIFiles[2] == null || saveTIMIFiles[2].trim().length()<=0){
				return;
			}			
			wordTIMI.onOpen(saveTIMIFiles[0], saveTIMIFiles[1], 2, false);		
			TParm inParam3 = new TParm();
			inParam3.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordTIMI.setWordParameter(inParam3);
			wordTIMI.setMicroField("����", patName);
			wordTIMI.setMicroField("�Ա�", patSex);
			wordTIMI.setMicroField("����", patAge);	
			wordTIMI.setCanEdit(true);
			wordTIMI.update();
		}
		// add by wangqing 20171120
		wordTIMI.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");
		wordTIMI.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");

		//����ҽ��վ-GRACE����
		wordGRACE = (TWord) this.getComponent("tWord_3");
//		wordGRACE.setName("tWord_3");
		saveGRACEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, graceClassCodeConfig, graceSubclassCodeConfig);
		if (saveGRACEFiles != null 
				&& saveGRACEFiles[0] != null && saveGRACEFiles[0].trim().length()>0 
				&& saveGRACEFiles[1] != null && saveGRACEFiles[1].trim().length()>0 
				&& saveGRACEFiles[2] != null && saveGRACEFiles[2].trim().length()>0) {
			updateGRACE = true;
			wordGRACE.onOpen(saveGRACEFiles[0], saveGRACEFiles[1], 3, false);		
			// add by wangqing 20170828 ���벡����
			TParm inParam4 = new TParm();
			inParam4.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordGRACE.setWordParameter(inParam4);
			wordGRACE.setCanEdit(true);
			wordGRACE.update();
		} else {
			updateGRACE = false;
			saveGRACEFiles = EMRPublicTool.getInstance().getEmrTemplet(graceSubclassCodeConfig);
			if(saveGRACEFiles == null 
					|| saveGRACEFiles[0] == null || saveGRACEFiles[0].trim().length()<=0 
					|| saveGRACEFiles[1] == null || saveGRACEFiles[1].trim().length()<=0 
					|| saveGRACEFiles[2] == null || saveGRACEFiles[2].trim().length()<=0){
				this.messageBox("û���ҵ�Grace����ģ��");
				return;
			}
			wordGRACE.onOpen(saveGRACEFiles[0], saveGRACEFiles[1], 2, false);		
			//			// add by wangqing 20170802 start
			//			// ��ʼ������
			//			// modified by wangqing 20170822 ����Ӥ�������		   
			//			int age = Integer.parseInt(patAge.split("��")[0]);
			//			if(age<=30){
			//				setESingleChooseText(wordGRACE, "age", "��30");
			//				wordGRACE.onCalculateExpression();
			//			}else if(age>30 && age<=39){
			//				setESingleChooseText(wordGRACE, "age", "30~39");
			//				wordGRACE.onCalculateExpression();
			//			}else if(age>=40 && age<=49){
			//				setESingleChooseText(wordGRACE, "age", "40~49");
			//				wordGRACE.onCalculateExpression();
			//			}else if(age>=50 && age<=59){
			//				setESingleChooseText(wordGRACE, "age", "50~59");
			//				wordGRACE.onCalculateExpression();
			//			}else if(age>=60 && age<=69){
			//				setESingleChooseText(wordGRACE, "age", "60~69");
			//				wordGRACE.onCalculateExpression();
			//			}else if(age>=70 && age<=79){
			//				setESingleChooseText(wordGRACE, "age", "70~79");
			//				wordGRACE.onCalculateExpression();
			//			}else if(age>=80 && age<=89){
			//				setESingleChooseText(wordGRACE, "age", "80~89");
			//				wordGRACE.onCalculateExpression();
			//			}else if(age>=90){
			//				setESingleChooseText(wordGRACE, "age", "��90");
			//				wordGRACE.onCalculateExpression();
			//			}
			//			// add by wangqing 20170802 end

			TParm inParam4 = new TParm();
			inParam4.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordGRACE.setWordParameter(inParam4);
			wordGRACE.setMicroField("����", patName);
			wordGRACE.setMicroField("�Ա�", patSex);
			wordGRACE.setMicroField("����", patAge);
			wordGRACE.setEnabled(true);
			wordGRACE.update();
		}
		// add by wangqing 20171120 ������ʽ
		wordGRACE.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");

		//��˨��APTE����Σ�շֲ�
		wordAPTE = (TWord) this.getComponent("tWord_4");
//		wordAPTE.setName("tWord_4");
		saveAPTEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, apteClassCodeConfig, apteSubclassCodeConfig);
		if (saveAPTEFiles != null 
				&& saveAPTEFiles[0] != null && saveAPTEFiles[0].trim().length()>0 
				&& saveAPTEFiles[1] != null && saveAPTEFiles[1].trim().length()>0 
				&& saveAPTEFiles[2] != null && saveAPTEFiles[2].trim().length()>0) {
			updateAPTE = true;
			wordAPTE.onOpen(saveAPTEFiles[0], saveAPTEFiles[1], 3, false);
			// add by wangqing 20170828 ���벡����
			TParm inParam5 = new TParm();
			inParam5.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordAPTE.setWordParameter(inParam5);

			wordAPTE.setCanEdit(true);
			wordAPTE.update();
		} else {
			updateAPTE = false;
			saveAPTEFiles = EMRPublicTool.getInstance().getEmrTemplet(apteSubclassCodeConfig);
			if(saveAPTEFiles == null 
					|| saveAPTEFiles[0] == null || saveAPTEFiles[0].trim().length()<=0 
					|| saveAPTEFiles[1] == null || saveAPTEFiles[1].trim().length()<=0 
					|| saveAPTEFiles[2] == null || saveAPTEFiles[2].trim().length()<=0){
				return;
			}
			wordAPTE.onOpen(saveAPTEFiles[0], saveAPTEFiles[1], 2, false);
			TParm inParam5 = new TParm();
			inParam5.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordAPTE.setWordParameter(inParam5);
			//ҽ������
			EFixed atpeDr = wordAPTE.findFixed("atpeDr");
			atpeDr.clearString();
			atpeDr.addString(Operator.getName());
			wordAPTE.setMicroField("����", patName);
			wordAPTE.setMicroField("�Ա�", patSex);
			wordAPTE.setMicroField("����", patAge);
			wordAPTE.setCanEdit(true);
			wordAPTE.update();
		}
	}
	
	/**
	 * <p>�״�ҽ�ƽӴ������ı��¼� add by wangqing 20171120</p>
	 * 
	 * <p>����ҽ�ƻ���Ϊ��Ժ����ʱ���Զ�����Ӵ�ʱ�䣨������˽����Ժ�ڽ���ʱ�䣩���Զ������ĵ�ͼ���ʱ�䣨Ժ���״��ĵ�ʱ����1���ӣ���</p>
	 * 
	 * @param wordName
	 * @param singleChooseName
	 */
	public void singleChooseSelected2(String wordTag, String singleChooseName){
		if(wordTag != null && wordTag.equals("tWord_1")){
			if(singleChooseName != null && singleChooseName.equals("FIRST_MEDICAL_ORG")){
				String firstMedicalOrg = getESingleChooseText(wordEDDR, "FIRST_MEDICAL_ORG");// �״�ҽ�ƽӴ�����
				ECheckBoxChoose priDigStemi = getECheckBoxChoose(wordEDDR, "priDigStemi");
				if(priDigStemi == null){
					this.messageBox("priDigStemi��ѡ�򲻴��ڣ�����");
					return;
				}
				boolean priDigStemiStatus = priDigStemi.isChecked();// �������ΪSTEMI	
				String firstDr = getESingleChooseText(wordEDDR, "FIRST_DR");// ����ҽ��������ֵ��
				if(firstDr==null || firstDr.trim().length()<=0){
					firstDr = getCaptureValue(wordEDDR, "FIRST_DIAGNOSIS_DOCTOR");// ����ҽ����ץȡֵ��
				}				
				// Ժ�ڽ���ʱ��͵�����ʱ�䣨�����أ�
				if(!triageTimeLazyFlg){
					String triageTimeSql = "SELECT (TO_CHAR (ADM_DATE, 'YYYY/MM/DD')||' '||TO_CHAR(COME_TIME, 'HH24:MI')) AS GATE_TIME, TO_CHAR (TRIAGE_TIME, 'YYYY/MM/DD HH24:MI') AS TRIAGE_TIME FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' ";
					TParm triageTimeResult = new TParm(TJDODBTool.getInstance().select(triageTimeSql));			
					//					String triageTime = "";
					//					String gateTime = "";
					if(triageTimeResult.getCount()>0){
						triageTime = triageTimeResult.getValue("TRIAGE_TIME", 0);	// Ժ�ڽ���ʱ��
						//						gateTime = triageTimeResult.getValue("GATE_TIME", 0);	// ������ʱ��			
					}
				}
				// Ժ���״��ĵ�ͼʱ�䡢�ĵ�ͼ���ʱ�䣨Ժ���״��ĵ�ͼʱ��+1min��
				if(!firstInEcgTimeLazyFlg){
					String firstInEcgTimeSql = " SELECT FIRST_IN_ECG_TIME FROM AMI_ERD_NS_RECORD WHERE TRIAGE_NO='"+triageNo+"' ";
					TParm result = new TParm(TJDODBTool.getInstance().select(firstInEcgTimeSql));
					if(result.getErrCode()<0){
						messageBox("ERR:" + result.getErrCode() + result.getErrText() +
								result.getErrName());
						return;
					}
					if(result.getCount()>0){	
						if(result.getValue("FIRST_IN_ECG_TIME", 0) != null && result.getValue("FIRST_IN_ECG_TIME", 0).trim().length()>0){
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
							firstInEcgTime = dateFormat.format(result.getTimestamp("FIRST_IN_ECG_TIME", 0));					
							long temp = result.getTimestamp("FIRST_IN_ECG_TIME", 0).getTime();		
							temp += 1*60*1000;// ��һ����
							Date date=new Date(temp);	
							ecgDiagnosisTime = dateFormat.format(date);
						}					
					}
				}
				if(firstMedicalOrg.equals("��Ժ����")){// �״�ҽ�ƽӴ�����Ϊ��Ժ����
					setECaptureValue(wordEDDR, "FIRST_TIME", triageTime);// �Ӵ�ʱ�䣨Ժ�ڽ���ʱ�䣩
					setECaptureValue(wordEDDR, "ecgDiagnosisTime", ecgDiagnosisTime);// �ĵ�ͼ���ʱ�䣨Ժ���״��ĵ�ͼʱ��+1min��
					if(priDigStemiStatus){
						setECaptureValue(wordEDDR, "priDigTime", ecgDiagnosisTime);// �������ʱ�䣨�ĵ�ͼ���ʱ�䣩��Ժ���״��ĵ�ͼʱ��+1min��
						setECaptureValue(wordEDDR, "priDigDr", firstDr);// �������ҽ��������ҽ����
					}		
				}	
			}
			// 
			if(singleChooseName != null && singleChooseName.equals("preadmStatinsDesc")){
				String preadmStatinsDesc = getESingleChooseText(wordEDDR, "preadmStatinsDesc");// Ժǰ�ڷ���͡������
				if(preadmStatinsDesc.equals("���з���͡")){
					setECaptureValue(wordEDDR, "preadmStatinsDose", "40");
				}else if(preadmStatinsDesc.equals("���淥��͡")){
					setECaptureValue(wordEDDR, "preadmStatinsDose", "20");
				}else{
					setECaptureValue(wordEDDR, "preadmStatinsDose", "");// modified by wangqing 20171120
				}
			}
		}
	}

	/**
	 * ��ѡ�򵥻��¼�
	 * @param wordName
	 * @param checkBoxChooseName
	 */
	public void checkBoxChooseClicked(String wordTag, String checkBoxChooseName){
		// modified by wangqing 20171120
		if(wordTag != null && wordTag.equals("tWord_1")){
			if(checkBoxChooseName != null && checkBoxChooseName.equals("priDigStemi")){
				String firstMedicalOrg = getESingleChooseText(wordEDDR, "FIRST_MEDICAL_ORG");// �״�ҽ�ƽӴ�����
				ECheckBoxChoose priDigStemi = getECheckBoxChoose(wordEDDR, "priDigStemi");
				if(priDigStemi == null){
					this.messageBox("priDigStemi��ѡ�򲻴��ڣ�����");
					return;
				}
				boolean priDigStemiStatus = priDigStemi.isChecked();// �������ΪSTEMI	
				String firstDr = getESingleChooseText(wordEDDR, "FIRST_DR");// ����ҽ��������ֵ��
				if(firstDr==null || firstDr.trim().length()<=0){
					firstDr = getCaptureValue(wordEDDR, "FIRST_DIAGNOSIS_DOCTOR");// ����ҽ����ץȡֵ��
				}
				// Ժ���״��ĵ�ͼʱ�䡢�ĵ�ͼ���ʱ�䣨Ժ���״��ĵ�ͼʱ��+1min��
				if(!firstInEcgTimeLazyFlg){
					String firstInEcgTimeSql = " SELECT FIRST_IN_ECG_TIME FROM AMI_ERD_NS_RECORD WHERE TRIAGE_NO='"+triageNo+"' ";
					TParm result = new TParm(TJDODBTool.getInstance().select(firstInEcgTimeSql));
					if(result.getErrCode()<0){
						messageBox("ERR:" + result.getErrCode() + result.getErrText() +
								result.getErrName());
						return;
					}
					if(result.getCount()>0){	
						if(result.getValue("FIRST_IN_ECG_TIME", 0) != null && result.getValue("FIRST_IN_ECG_TIME", 0).trim().length()>0){
							SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
							firstInEcgTime = dateFormat.format(result.getTimestamp("FIRST_IN_ECG_TIME", 0));					
							long temp = result.getTimestamp("FIRST_IN_ECG_TIME", 0).getTime();		
							temp += 1*60*1000;// ��һ����
							Date date=new Date(temp);	
							ecgDiagnosisTime = dateFormat.format(date);
						}					
					}
				}
				if(priDigStemiStatus){
					if(firstMedicalOrg.equals("��Ժ����")){
						setECaptureValue(wordEDDR, "priDigTime", ecgDiagnosisTime);// �������ʱ�䣨�ĵ�ͼ���ʱ�䣩��Ժ���״��ĵ�ͼʱ��+1min��
						setECaptureValue(wordEDDR, "priDigDr", firstDr);// �������ҽ��������ҽ����
					}
				}
			}
		}

		if(wordTag != null && wordTag.equals("tWord_2")){
			if(checkBoxChooseName != null && checkBoxChooseName.equals("timiAgeCheckBox")){
				// modified by wangqing 20170822 ����Ӥ�������
				int valueAge = Integer.parseInt(patAge.split("��")[0]);
				ECheckBoxChoose timiAgeCheckBox = getECheckBoxChoose(wordTIMI, "timiAgeCheckBox");
				boolean timiAgeCheckBoxStatus = timiAgeCheckBox.isChecked();
				if(timiAgeCheckBoxStatus){
					// modified by wangqing 20170822 >=60 && <=64
					if (valueAge >= 60 && valueAge <= 64) {
						setCheckBoxChooseValue(wordTIMI, "timiAgeCheckBox", "1");
					} else if (valueAge >= 65 && valueAge <= 74) {
						setCheckBoxChooseValue(wordTIMI, "timiAgeCheckBox", "2");
					} else if (valueAge >= 75) {
						setCheckBoxChooseValue(wordTIMI, "timiAgeCheckBox", "3");
					} 
				}else{
					setCheckBoxChooseValue(wordTIMI, "timiAgeCheckBox", "0");
				}
			}
		}
	}

	/**
	 * ������ʽ
	 * @param wordName
	 */
	public void calculateExpression(String wordTag){
		if(wordTag != null && wordTag.equals("tWord_2")){
			String timiTotal = getEFixedValue(wordTIMI, "timiTotal");
			String timlLevelStr = getTimlLevel(timiTotal);
			setEFixedValue(wordTIMI, "timlLevel", timlLevelStr);
			wordTIMI.update();	
			setECaptureValue(wordEDDR, "timiScore", timiTotal);
			wordEDDR.update();		
		}
		if(wordTag != null && wordTag.equals("tWord_3")){
			String graceScore = getEFixedValue(wordGRACE, "graceScore");
			setECaptureValue(wordEDDR, "graceScore", graceScore);	
		}
	}

	/**
	 * ����TIMI�ּ�
	 * @author wangqing 20170628
	 * 
	 * */
	public String getTimlLevel(String timiTotalStr) {
		// add by wangqing 20170628
		if(timiTotalStr == null || timiTotalStr.trim().length()==0){
			return "";
		}
		int timlLevelInt = Integer.parseInt(timiTotalStr);
		String timlLevelStr = "";

		if(timlLevelInt >= 0  &&  timlLevelInt <= 4) {
			timlLevelStr="��Σ";
		} else if (timlLevelInt >= 5  &&  timlLevelInt <= 9) {
			timlLevelStr="��Σ";
		} else if (timlLevelInt >= 10  &&  timlLevelInt <= 14){
			timlLevelStr="��Σ";
		}	
		return timlLevelStr;
	}

	/**
	 * ����
	 */
	public void onSave(){
		String path = "";
		String fileName = "";
		TParm saveParm = new TParm(); 
		/*��ʹ���ļ���ҽ����¼*/
		if(updateEDDR){
			//����JHW
			path = saveEDDRFiles[0];
			fileName = saveEDDRFiles[1];
		} else {
			//����JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, eddrClassCodeConfig, eddrSubclassCodeConfig, saveEDDRFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");		
		}	
		wordEDDR.setMessageBoxSwitch(false);//���Ᵽ���������ɹ��ĶԻ��Ӵ�
		wordEDDR.onSaveAs(path, fileName, 3);
		saveEDDRFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, eddrClassCodeConfig, eddrSubclassCodeConfig);
		updateEDDR = true;

		/*����ҽ��վ-TIMI����*/
		if(updateTIMI){
			//����JHW
			path = saveTIMIFiles[0];
			fileName = saveTIMIFiles[1];
		} else {
			//����JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, timiClassCodeConfig, timiSubclassCodeConfig, saveTIMIFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");		
		}
		wordTIMI.setMessageBoxSwitch(false);//���Ᵽ���������ɹ��ĶԻ��Ӵ�
		wordTIMI.onSaveAs(path, fileName, 3);
		saveTIMIFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, timiClassCodeConfig, timiSubclassCodeConfig);
		updateTIMI = true;

		/*����ҽ��վ-GRACE����*/
		if(updateGRACE){
			//����JHW
			path = saveGRACEFiles[0];
			fileName = saveGRACEFiles[1];
		} else {
			//����JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, graceClassCodeConfig, graceSubclassCodeConfig, saveGRACEFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");		
		}
		wordGRACE.setMessageBoxSwitch(false);//���Ᵽ���������ɹ��ĶԻ��Ӵ�
		wordGRACE.onSaveAs(path, fileName, 3);
		saveGRACEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, graceClassCodeConfig, graceSubclassCodeConfig);
		updateGRACE = true;

		/*��˨��APTE����Σ�շֲ�*/
		if(updateAPTE){
			//����JHW
			path = saveAPTEFiles[0];
			fileName = saveAPTEFiles[1];
		} else {
			//����JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, apteClassCodeConfig, apteSubclassCodeConfig, saveAPTEFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}	
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");	
		}
		wordAPTE.setMessageBoxSwitch(false);//���Ᵽ���������ɹ��ĶԻ��Ӵ�
		wordAPTE.onSaveAs(path, fileName, 3);
		saveAPTEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, apteClassCodeConfig, apteSubclassCodeConfig);
		updateAPTE = true;
		this.messageBox("����ɹ���");	
	}

	/**
	 * <p>����ץȡֵ</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setECaptureValue(TWord word, String name, String value) {
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
		// modified by wangqing 20171120
		// ��д��T40������ֱ��ճ�����ַ������������δ˴�����
//		if(value.equals("")){
//			value = " ";
//		}
		ECapture ecap = (ECapture)word.findObject(name, EComponent.CAPTURE_TYPE);
		if (ecap == null){
			System.out.println("word--->name�ؼ�������");
			return;
		}
		ecap.setFocusLast();
		ecap.clear();
		word.pasteString(value);
	}	

	/**
	 * ���ø�ѡ��״̬
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setCheckBoxChooseStatus(TWord word, String name, boolean value){
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
			System.out.println("word--->name�ؼ�������");
			return;	
		}
		cbc.setChecked(value);
	}

	/**
	 * ���ø�ѡ��ֵ
	 * @param name
	 * @param word
	 * @param value
	 */
	public void setCheckBoxChooseValue(TWord word, String name, String value){
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
		ECheckBoxChoose cbc = (ECheckBoxChoose) word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);
		if(cbc == null) {
			System.out.println("word--->name�ؼ�������");
			return;
		}
		cbc.setCbValue(value);
	}

	/**
	 * ��ȡ��ѡ��ؼ�
	 * @param word
	 * @param name
	 * @return
	 */
	public ECheckBoxChoose getECheckBoxChoose(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);//��ѡ��
		if(cbc == null){
			System.out.println("word--->name�ؼ�������");
		}
		return cbc;
	}

	/**
	 * <p>���õ�ѡtext</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setESingleChooseText(TWord word, String name, String value){
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
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//������ѡ
		if(sc == null) {
			System.out.println("word--->name�ؼ�������");
			return;
		}
		sc.setText(value);
	}

	/**
	 * ��ȡ��ѡtext
	 * @param word
	 * @param name
	 * @return
	 */
	public String getESingleChooseText(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//������ѡ
		if(sc == null){ 
			System.out.println("word--->name�ؼ�������");
			return null;		
		}
		return sc.getText();
	}

	/**
	 * ��ȡ�̶��ı�ֵ
	 * @param word
	 * @param name
	 * @return
	 */
	public String getEFixedValue(TWord word, String name){
		if(word == null){
			System.out.println("word is null");
			return null;
		}
		if(name == null){
			System.out.println("name is null");
			return null;
		}
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// �̶��ı�
		if(f == null){ 
			System.out.println("word--->name�ؼ�������");
			return null;	
		}
		return f.getText();
	}

	/**
	 * <p>���ù̶��ı�ֵ</p>
	 * @param word
	 * @param name
	 * @param value
	 */
	public void setEFixedValue(TWord word, String name, String value){
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
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// �̶��ı�
		if(f == null){ 
			System.out.println("word--->name�ؼ�������");
			return;	
		}
		f.setText(value);
	}

	/**
	 * ��ȡץȡ�ؼ�ֵ
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
			System.out.println("word--->name�ؼ�������");
			return null;
		}
		return ecap.getValue();
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
			//����һ
			tsStr = sdf.format(ts);
			//������
			//			tsStr = ts.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tsStr;
	}

	/**
	 * �ڹ��λ������ʱ���ʽ
	 */
	public void onNow(){
		String now = StringTool.getString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
		TTabbedPane tabbledPane1 = (TTabbedPane) this.getComponent("tTabbedPane_0");
		if(tabbledPane1.getSelectedIndex()==0){
			wordPRE.pasteString(now);
		}else if(tabbledPane1.getSelectedIndex()==1){
			wordEDDR.pasteString(now);
		}else if(tabbledPane1.getSelectedIndex()==2){
			wordTIMI.pasteString(now);
		}else if(tabbledPane1.getSelectedIndex()==3){
			wordGRACE.pasteString(now);
		}else if(tabbledPane1.getSelectedIndex()==4){
			wordAPTE.pasteString(now);
		}

	}

	/**
	 * ����ר��
	 */
	public void onTest(){
		//		System.out.println("===:"+getESingleChooseValue("FIRST_MEDICAL_ORG", wordEDDR));	
		System.out.println("===:"+this.getEFixedValue(wordGRACE, "graceScore"));
	}


}
