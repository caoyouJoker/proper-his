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
 * <p>胸痛中心急诊医生记录</p>
 * <p>只有新建的病历才会进行以下操作：1、初始化数据 2、设置监听</p>
 * <p>已存在的病历只能手动修改</p>
 * @author wangqing
 */

//20170822
//1、Timi和Grace年龄问题需要考虑婴儿
//2、Timi评分年龄评分错误
//3、胸痛中心急诊医生记录首诊医生必须是急诊医生，需要改sql
//4、胸痛中心急诊医生记录模板，非ACS、非心源性处理措施改为多选，并新增“其他”选项

public class REGDoctorTriageControl extends TControl {
	/**
	 * 胸痛急诊护士记录病历
	 */
	private TWord wordPRE;
	/**
	 * 胸痛急诊医生记录病历
	 */
	private TWord wordEDDR;
	/**
	 * Timi评分病历
	 */
	private TWord wordTIMI;
	/**
	 * Grace评分病历
	 */
	private TWord wordGRACE;
	/**
	 * APTE病历
	 */
	private TWord wordAPTE;
	/**
	 * 胸痛急诊护士记录病历路径
	 */
	private String[] savePREFiles;
	/**
	 * 胸痛急诊医生记录病历路径
	 */
	private String[] saveEDDRFiles;
	/**
	 * Timi评分病历路径
	 */
	private String[] saveTIMIFiles;
	/**
	 * Grace评分病历路径
	 */
	private String[] saveGRACEFiles;
	/**
	 * APTE病历路径
	 */
	private String[] saveAPTEFiles;
	/**
	 * 胸痛急诊医生记录病历新建或者打开，true 表示打开已有病历 false表示新建
	 */
	private boolean updateEDDR = false;
	/**
	 * Timi评分病历新建或者打开，true 表示打开已有病历 false表示新建
	 */
	private boolean updateTIMI = false;
	/**
	 * Grace评分病历新建或者打开，true 表示打开已有病历 false表示新建
	 */
	private boolean updateGRACE = false;
	/**
	 * APTE病历新建或者打开，true 表示打开已有病历 false表示新建
	 */
	private boolean updateAPTE = false;
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
	 * Timi评分记录病历classCodeConfig
	 */
	private static String timiClassCodeConfig = "AMI_TIMI_CLASSCODE";
	/**
	 * Timi评分记录病历subClassCodeConfig
	 */
	private static String timiSubclassCodeConfig = "AMI_TIMI_SUBCLASSCODE";
	/**
	 * Grace评分记录病历classCodeConfig
	 */
	private static String graceClassCodeConfig = "AMI_GRACE_CLASSCODE";
	/**
	 * Grace评分记录病历subClassCodeConfig
	 */
	private static String graceSubclassCodeConfig = "AMI_GRACE_SUBCLASSCODE";
	/**
	 * APTE记录病历classCodeConfig
	 */
	private static String apteClassCodeConfig = "AMI_APTE_CLASSCODE";
	/**
	 * APTE记录病历subClassCodeConfig
	 */
	private static String apteSubclassCodeConfig = "AMI_APTE_SUBCLASSCODE";

	/**
	 * 系统传入参数
	 */
	private TParm sysParm;	
	/**
	 * 急诊就诊号
	 */
	private String caseNo = "";
	/**
	 * 病案号
	 */
	private String mrNo = "";
	/**
	 * 检伤号
	 */
	private String triageNo;
	/**
	 * 患者姓名
	 */
	private String patName = "";
	/**
	 * 患者性别
	 */
	private String patSex = "";
	/**
	 * 患者年龄
	 */
	private String patAge = "";

	/**
	 * 检伤评估接诊时间
	 */
	private String triageTime = "";
	/**
	 * 院内首次心电图时间
	 */
	private String firstInEcgTime = "";
	/**
	 * 心电图诊断时间
	 */
	private String ecgDiagnosisTime = "";
	/**
	 * 检伤评估接诊时间懒加载标识
	 */
	boolean triageTimeLazyFlg = false;
	/**
	 * 院内首次心电图时间懒加载标识
	 */
	boolean firstInEcgTimeLazyFlg = false;


	/**
	 * 初始化
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
			patSex = ("1".equals(pat.getSexCode())?"男":"女");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());			
		}else{
			this.messageBox("系统传入参数错误！！！");
		}
		TParm result = EMRAMITool.getInstance().getErdEvalutionDataByCaseNo(caseNo);
		triageNo = result.getValue("TRIAGE_NO",0);
		this.setValue("PAT_NAME", patName);
		this.setValue("TRIAGE_NO", result.getValue("TRIAGE_NO",0));
		this.setValue("LEVEL_CODE", result.getValue("LEVEL_CODE",0));
		this.setValue("COME_TIME", result.getValue("GATE_TIME",0));// 到大门时间

		initJHW();
		// add by wangqing 默认选择急诊医生站胸痛中心记录病历
		TTabbedPane tabbledPane1 = (TTabbedPane) this.getComponent("tTabbedPane_0");
		tabbledPane1.setSelectedIndex(1);
	}

	/**
	 * 初始化结构化病历
	 */
	public void initJHW(){
		String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
		String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";

		//胸痛中心急诊护士记录（打开已有病历）
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

		//胸痛中心急诊医生记录
		wordEDDR = (TWord) this.getComponent("tWord_1");
//		wordEDDR.setName("tWord_1");
		saveEDDRFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, eddrClassCodeConfig, eddrSubclassCodeConfig);
		if (saveEDDRFiles != null 
				&& saveEDDRFiles[0] != null && saveEDDRFiles[0].trim().length()>0 
				&& saveEDDRFiles[1] != null && saveEDDRFiles[1].trim().length()>0 
				&& saveEDDRFiles[2] != null && saveEDDRFiles[2].trim().length()>0) {// 打开已有的病历
			updateEDDR = true;
			wordEDDR.onOpen(saveEDDRFiles[0], saveEDDRFiles[1], 3, false);
			// add by wangqing 20170828 带入病案号
			TParm inParam2 = new TParm();
			inParam2.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordEDDR.setWordParameter(inParam2);
			wordEDDR.setCanEdit(true);
			wordEDDR.update();		
		}else{// 新增
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
			// 院内首次心电图时间、心电图诊断时间（院内首次心电图时间+1min）
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
						temp += 1*60*1000;// 晚一分钟
						Date date=new Date(temp);	
						ecgDiagnosisTime = dateFormat.format(date);
					}					
				}
			}	
			this.setECaptureValue(wordEDDR, "FIRST_IN_ECG_TIME", firstInEcgTime);// 院内首次心电图时间	
			// add by wangqing 20171120 end
			// 本院挂号时间
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

//			// 时间格式	
//			this.setECaptureValue(wordEDDR, "TeleECGTime", sysDate3);// 远程心电图传输时间，auto,待讨论

			// add by wangqing 20170801 start
			// 初始化首诊医师下拉控件
			// modified by wangqing 20170822 修改sql
			String firstDrSQL = " SELECT A.USER_ID, B.USER_NAME FROM SYS_OPERATOR_DEPT A LEFT JOIN SYS_OPERATOR B ON A.USER_ID = B.USER_ID "
					+ "WHERE A.DEPT_CODE='0202' AND A.MAIN_FLG = 'Y' AND B.ROLE_ID IN ('ODO', 'OIDR') "
					+ "ORDER BY A.USER_ID";
			TParm firstDrResult = new TParm(TJDODBTool.getInstance().select(firstDrSQL));
			ESingleChoose firstDrSC=(ESingleChoose)wordEDDR.findObject("FIRST_DR", EComponent.SINGLE_CHOOSE_TYPE);//首诊医生下拉单选
			firstDrSC.addData(" ", "0");
			for(int i=0; i<firstDrResult.getCount(); i++){
				firstDrSC.addData(firstDrResult.getValue("USER_NAME", i), firstDrResult.getValue("USER_ID", i));
			}
			// add by wangqing 20170801 end

			//确认是否有开心脏超声的医嘱 		
			TParm ccpcAmiHeartUtParm = EMRAMITool.getInstance().getCheckOrderByCaseNoAndOrderCode(caseNo,"Y0301");
			//若有开心脏超声的医嘱 	
			if (ccpcAmiHeartUtParm.getCount() > 0) {
				this.setCheckBoxChooseStatus(wordEDDR, "ccpcAmiHeartUt", true);// 心脏超声
			}
			//确认是否有开核素的医嘱
			TParm ccpcAmiNuclideParm = EMRAMITool.getInstance().getCheckOrderByCaseNoAndOrderCode(caseNo,"Y010");
			//若有开核素的医嘱
			if (ccpcAmiNuclideParm.getCount() > 0) {
				this.setCheckBoxChooseStatus(wordEDDR, "ccpcAmiNuclide", true);// 核素
			} 

			//确认是否有开阿司匹林、氯吡格雷、替格瑞洛、他汀类药物、抗凝血药品、溶栓药品的医嘱
			TParm speOpdOrderParm = EMRAMITool.getInstance().getSpecialOpdOrderByCaseNo(caseNo);
			//若有开阿司匹林、氯吡格雷、替格瑞洛、他汀类药物、抗凝血药品、溶栓药品的医嘱
			if (speOpdOrderParm.getCount() > 0) {
				for (int i=0;i<speOpdOrderParm.getCount();i++) {
					String str = speOpdOrderParm.getValue("SYS_PHA_CLASS",i);
					String[] strArr = str.split(";");
					for (int j=0;j<strArr.length;j++) {
						if ("1".equals(strArr[j])) {// 急诊给药，阿司匹林
							this.setECaptureValue(wordEDDR, "emradmAspirinDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmAspirinUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmFirstAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));
						} else if ("2".equals(strArr[j])) {// 急诊给药，氯吡格雷
							this.setCheckBoxChooseStatus(wordEDDR, "emradmClopidogrel", true);					
							this.setECaptureValue(wordEDDR, "emradmClticDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmClticUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmClticAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));
						} else if ("3".equals(strArr[j])) {// 急诊给药，替格瑞洛
							this.setCheckBoxChooseStatus(wordEDDR, "emradmTicagrelor", true);					
							this.setECaptureValue(wordEDDR, "emradmClticDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmClticUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmClticAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));					
						} else if ("4".equals(strArr[j])) {// 急诊给药，口服他汀类
							this.setECaptureValue(wordEDDR, "emradmStatinsDesc",speOpdOrderParm.getValue("ORDER_DESC",i));			
							this.setECaptureValue(wordEDDR, "emradmStatinsDose",speOpdOrderParm.getValue("MEDI_QTY",i));
							this.setECaptureValue(wordEDDR, "emradmStatinsUnit",speOpdOrderParm.getValue("UNIT_CHN_DESC",i));
							this.setECaptureValue(wordEDDR, "emradmStatinsAdmTime",StringTool.getString(speOpdOrderParm.getTimestamp("ORDER_DATE", i),"yyyy/MM/dd HH:mm"));
						} else if ("5".equals(strArr[j])) {// 抗凝给药				

						} else if ("6".equals(strArr[j])) {// 溶栓治疗

						}
					}
				}
			}
			TParm inParam2 = new TParm();
			inParam2.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordEDDR.setWordParameter(inParam2);
			wordEDDR.setMicroField("姓名", patName);
			wordEDDR.setMicroField("性别", patSex);
			wordEDDR.setMicroField("年龄", patAge);
			wordEDDR.setCanEdit(true);
			wordEDDR.update();
		}	
		// add by wangqing 首次医疗接触机构改变事件
		wordEDDR.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected2");
		// add by wangqing 初步诊断为STEMI复选框单击事件
		wordEDDR.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");

		//急诊医生站-TIMI评分
		wordTIMI = (TWord) this.getComponent("tWord_2");
//		wordTIMI.setName("tWord_2");
		saveTIMIFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, timiClassCodeConfig, timiSubclassCodeConfig);
		if (saveTIMIFiles != null 
				&& saveTIMIFiles[0] != null && saveTIMIFiles[0].trim().length()>0 
				&& saveTIMIFiles[1] != null && saveTIMIFiles[1].trim().length()>0 
				&& saveTIMIFiles[2] != null && saveTIMIFiles[2].trim().length()>0) {
			updateTIMI = true;
			wordTIMI.onOpen(saveTIMIFiles[0], saveTIMIFiles[1], 3, false);
			// add by wangqing 20170828 带入病案号
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
			wordTIMI.setMicroField("姓名", patName);
			wordTIMI.setMicroField("性别", patSex);
			wordTIMI.setMicroField("年龄", patAge);	
			wordTIMI.setCanEdit(true);
			wordTIMI.update();
		}
		// add by wangqing 20171120
		wordTIMI.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");
		wordTIMI.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");

		//急诊医生站-GRACE评分
		wordGRACE = (TWord) this.getComponent("tWord_3");
//		wordGRACE.setName("tWord_3");
		saveGRACEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, graceClassCodeConfig, graceSubclassCodeConfig);
		if (saveGRACEFiles != null 
				&& saveGRACEFiles[0] != null && saveGRACEFiles[0].trim().length()>0 
				&& saveGRACEFiles[1] != null && saveGRACEFiles[1].trim().length()>0 
				&& saveGRACEFiles[2] != null && saveGRACEFiles[2].trim().length()>0) {
			updateGRACE = true;
			wordGRACE.onOpen(saveGRACEFiles[0], saveGRACEFiles[1], 3, false);		
			// add by wangqing 20170828 带入病案号
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
				this.messageBox("没有找到Grace评分模板");
				return;
			}
			wordGRACE.onOpen(saveGRACEFiles[0], saveGRACEFiles[1], 2, false);		
			//			// add by wangqing 20170802 start
			//			// 初始化年龄
			//			// modified by wangqing 20170822 考虑婴儿的情况		   
			//			int age = Integer.parseInt(patAge.split("岁")[0]);
			//			if(age<=30){
			//				setESingleChooseText(wordGRACE, "age", "≤30");
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
			//				setESingleChooseText(wordGRACE, "age", "≥90");
			//				wordGRACE.onCalculateExpression();
			//			}
			//			// add by wangqing 20170802 end

			TParm inParam4 = new TParm();
			inParam4.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordGRACE.setWordParameter(inParam4);
			wordGRACE.setMicroField("姓名", patName);
			wordGRACE.setMicroField("性别", patSex);
			wordGRACE.setMicroField("年龄", patAge);
			wordGRACE.setEnabled(true);
			wordGRACE.update();
		}
		// add by wangqing 20171120 计算表达式
		wordGRACE.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");

		//肺栓塞APTE死亡危险分层
		wordAPTE = (TWord) this.getComponent("tWord_4");
//		wordAPTE.setName("tWord_4");
		saveAPTEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, apteClassCodeConfig, apteSubclassCodeConfig);
		if (saveAPTEFiles != null 
				&& saveAPTEFiles[0] != null && saveAPTEFiles[0].trim().length()>0 
				&& saveAPTEFiles[1] != null && saveAPTEFiles[1].trim().length()>0 
				&& saveAPTEFiles[2] != null && saveAPTEFiles[2].trim().length()>0) {
			updateAPTE = true;
			wordAPTE.onOpen(saveAPTEFiles[0], saveAPTEFiles[1], 3, false);
			// add by wangqing 20170828 带入病案号
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
			//医生姓名
			EFixed atpeDr = wordAPTE.findFixed("atpeDr");
			atpeDr.clearString();
			atpeDr.addString(Operator.getName());
			wordAPTE.setMicroField("姓名", patName);
			wordAPTE.setMicroField("性别", patSex);
			wordAPTE.setMicroField("年龄", patAge);
			wordAPTE.setCanEdit(true);
			wordAPTE.update();
		}
	}
	
	/**
	 * <p>首次医疗接触机构改变事件 add by wangqing 20171120</p>
	 * 
	 * <p>首诊医疗机构为本院急诊时，自动带入接触时间（急诊检伤界面的院内接诊时间）；自动带入心电图诊断时间（院内首次心电时间晚1分钟）；</p>
	 * 
	 * @param wordName
	 * @param singleChooseName
	 */
	public void singleChooseSelected2(String wordTag, String singleChooseName){
		if(wordTag != null && wordTag.equals("tWord_1")){
			if(singleChooseName != null && singleChooseName.equals("FIRST_MEDICAL_ORG")){
				String firstMedicalOrg = getESingleChooseText(wordEDDR, "FIRST_MEDICAL_ORG");// 首次医疗接触机构
				ECheckBoxChoose priDigStemi = getECheckBoxChoose(wordEDDR, "priDigStemi");
				if(priDigStemi == null){
					this.messageBox("priDigStemi复选框不存在！！！");
					return;
				}
				boolean priDigStemiStatus = priDigStemi.isChecked();// 初步诊断为STEMI	
				String firstDr = getESingleChooseText(wordEDDR, "FIRST_DR");// 首诊医生（下拉值）
				if(firstDr==null || firstDr.trim().length()<=0){
					firstDr = getCaptureValue(wordEDDR, "FIRST_DIAGNOSIS_DOCTOR");// 首诊医生（抓取值）
				}				
				// 院内接诊时间和到大门时间（懒加载）
				if(!triageTimeLazyFlg){
					String triageTimeSql = "SELECT (TO_CHAR (ADM_DATE, 'YYYY/MM/DD')||' '||TO_CHAR(COME_TIME, 'HH24:MI')) AS GATE_TIME, TO_CHAR (TRIAGE_TIME, 'YYYY/MM/DD HH24:MI') AS TRIAGE_TIME FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' ";
					TParm triageTimeResult = new TParm(TJDODBTool.getInstance().select(triageTimeSql));			
					//					String triageTime = "";
					//					String gateTime = "";
					if(triageTimeResult.getCount()>0){
						triageTime = triageTimeResult.getValue("TRIAGE_TIME", 0);	// 院内接诊时间
						//						gateTime = triageTimeResult.getValue("GATE_TIME", 0);	// 到大门时间			
					}
				}
				// 院内首次心电图时间、心电图诊断时间（院内首次心电图时间+1min）
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
							temp += 1*60*1000;// 晚一分钟
							Date date=new Date(temp);	
							ecgDiagnosisTime = dateFormat.format(date);
						}					
					}
				}
				if(firstMedicalOrg.equals("本院急诊")){// 首次医疗接触机构为本院急诊
					setECaptureValue(wordEDDR, "FIRST_TIME", triageTime);// 接触时间（院内接诊时间）
					setECaptureValue(wordEDDR, "ecgDiagnosisTime", ecgDiagnosisTime);// 心电图诊断时间（院内首次心电图时间+1min）
					if(priDigStemiStatus){
						setECaptureValue(wordEDDR, "priDigTime", ecgDiagnosisTime);// 初步诊断时间（心电图诊断时间）（院内首次心电图时间+1min）
						setECaptureValue(wordEDDR, "priDigDr", firstDr);// 初步诊断医生（首诊医生）
					}		
				}	
			}
			// 
			if(singleChooseName != null && singleChooseName.equals("preadmStatinsDesc")){
				String preadmStatinsDesc = getESingleChooseText(wordEDDR, "preadmStatinsDesc");// 院前口服他汀类名称
				if(preadmStatinsDesc.equals("阿托伐他汀")){
					setECaptureValue(wordEDDR, "preadmStatinsDose", "40");
				}else if(preadmStatinsDesc.equals("瑞舒伐他汀")){
					setECaptureValue(wordEDDR, "preadmStatinsDose", "20");
				}else{
					setECaptureValue(wordEDDR, "preadmStatinsDose", "");// modified by wangqing 20171120
				}
			}
		}
	}

	/**
	 * 复选框单击事件
	 * @param wordName
	 * @param checkBoxChooseName
	 */
	public void checkBoxChooseClicked(String wordTag, String checkBoxChooseName){
		// modified by wangqing 20171120
		if(wordTag != null && wordTag.equals("tWord_1")){
			if(checkBoxChooseName != null && checkBoxChooseName.equals("priDigStemi")){
				String firstMedicalOrg = getESingleChooseText(wordEDDR, "FIRST_MEDICAL_ORG");// 首次医疗接触机构
				ECheckBoxChoose priDigStemi = getECheckBoxChoose(wordEDDR, "priDigStemi");
				if(priDigStemi == null){
					this.messageBox("priDigStemi复选框不存在！！！");
					return;
				}
				boolean priDigStemiStatus = priDigStemi.isChecked();// 初步诊断为STEMI	
				String firstDr = getESingleChooseText(wordEDDR, "FIRST_DR");// 首诊医生（下拉值）
				if(firstDr==null || firstDr.trim().length()<=0){
					firstDr = getCaptureValue(wordEDDR, "FIRST_DIAGNOSIS_DOCTOR");// 首诊医生（抓取值）
				}
				// 院内首次心电图时间、心电图诊断时间（院内首次心电图时间+1min）
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
							temp += 1*60*1000;// 晚一分钟
							Date date=new Date(temp);	
							ecgDiagnosisTime = dateFormat.format(date);
						}					
					}
				}
				if(priDigStemiStatus){
					if(firstMedicalOrg.equals("本院急诊")){
						setECaptureValue(wordEDDR, "priDigTime", ecgDiagnosisTime);// 初步诊断时间（心电图诊断时间）（院内首次心电图时间+1min）
						setECaptureValue(wordEDDR, "priDigDr", firstDr);// 初步诊断医生（首诊医生）
					}
				}
			}
		}

		if(wordTag != null && wordTag.equals("tWord_2")){
			if(checkBoxChooseName != null && checkBoxChooseName.equals("timiAgeCheckBox")){
				// modified by wangqing 20170822 考虑婴儿的情况
				int valueAge = Integer.parseInt(patAge.split("岁")[0]);
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
	 * 计算表达式
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
	 * 计算TIMI分级
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
			timlLevelStr="低危";
		} else if (timlLevelInt >= 5  &&  timlLevelInt <= 9) {
			timlLevelStr="中危";
		} else if (timlLevelInt >= 10  &&  timlLevelInt <= 14){
			timlLevelStr="高危";
		}	
		return timlLevelStr;
	}

	/**
	 * 保存
	 */
	public void onSave(){
		String path = "";
		String fileName = "";
		TParm saveParm = new TParm(); 
		/*胸痛中心急诊医生记录*/
		if(updateEDDR){
			//更新JHW
			path = saveEDDRFiles[0];
			fileName = saveEDDRFiles[1];
		} else {
			//保存JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, eddrClassCodeConfig, eddrSubclassCodeConfig, saveEDDRFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");		
		}	
		wordEDDR.setMessageBoxSwitch(false);//避免保存后跳保存成功的对话视窗
		wordEDDR.onSaveAs(path, fileName, 3);
		saveEDDRFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, eddrClassCodeConfig, eddrSubclassCodeConfig);
		updateEDDR = true;

		/*急诊医生站-TIMI评分*/
		if(updateTIMI){
			//更新JHW
			path = saveTIMIFiles[0];
			fileName = saveTIMIFiles[1];
		} else {
			//保存JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, timiClassCodeConfig, timiSubclassCodeConfig, saveTIMIFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");		
		}
		wordTIMI.setMessageBoxSwitch(false);//避免保存后跳保存成功的对话视窗
		wordTIMI.onSaveAs(path, fileName, 3);
		saveTIMIFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, timiClassCodeConfig, timiSubclassCodeConfig);
		updateTIMI = true;

		/*急诊医生站-GRACE评分*/
		if(updateGRACE){
			//更新JHW
			path = saveGRACEFiles[0];
			fileName = saveGRACEFiles[1];
		} else {
			//保存JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, graceClassCodeConfig, graceSubclassCodeConfig, saveGRACEFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");		
		}
		wordGRACE.setMessageBoxSwitch(false);//避免保存后跳保存成功的对话视窗
		wordGRACE.onSaveAs(path, fileName, 3);
		saveGRACEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, graceClassCodeConfig, graceSubclassCodeConfig);
		updateGRACE = true;

		/*肺栓塞APTE死亡危险分层*/
		if(updateAPTE){
			//更新JHW
			path = saveAPTEFiles[0];
			fileName = saveAPTEFiles[1];
		} else {
			//保存JHW
			saveParm = EMRPublicTool.getInstance().saveEmrFile(caseNo, apteClassCodeConfig, apteSubclassCodeConfig, saveAPTEFiles[1]);
			if (saveParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}	
			path = saveParm.getValue("PATH");
			fileName = saveParm.getValue("FILENAME");	
		}
		wordAPTE.setMessageBoxSwitch(false);//避免保存后跳保存成功的对话视窗
		wordAPTE.onSaveAs(path, fileName, 3);
		saveAPTEFiles = EMRPublicTool.getInstance().getEmrFile(caseNo, apteClassCodeConfig, apteSubclassCodeConfig);
		updateAPTE = true;
		this.messageBox("保存成功！");	
	}

	/**
	 * <p>设置抓取值</p>
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
		// 重写了T40，可以直接粘贴空字符串，所以屏蔽此处代码
//		if(value.equals("")){
//			value = " ";
//		}
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
	 * 设置复选框状态
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
			System.out.println("word--->name控件不存在");
			return;	
		}
		cbc.setChecked(value);
	}

	/**
	 * 设置复选框值
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
			System.out.println("word--->name控件不存在");
			return;
		}
		cbc.setCbValue(value);
	}

	/**
	 * 获取复选框控件
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
		ECheckBoxChoose cbc=(ECheckBoxChoose)word.findObject(name, EComponent.CHECK_BOX_CHOOSE_TYPE);//单选框
		if(cbc == null){
			System.out.println("word--->name控件不存在");
		}
		return cbc;
	}

	/**
	 * <p>设置单选text</p>
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
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if(sc == null) {
			System.out.println("word--->name控件不存在");
			return;
		}
		sc.setText(value);
	}

	/**
	 * 获取单选text
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
		ESingleChoose sc=(ESingleChoose)word.findObject(name, EComponent.SINGLE_CHOOSE_TYPE);//下拉单选
		if(sc == null){ 
			System.out.println("word--->name控件不存在");
			return null;		
		}
		return sc.getText();
	}

	/**
	 * 获取固定文本值
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
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// 固定文本
		if(f == null){ 
			System.out.println("word--->name控件不存在");
			return null;	
		}
		return f.getText();
	}

	/**
	 * <p>设置固定文本值</p>
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
		EFixed f=(EFixed)word.findObject(name, EComponent.FIXED_TYPE);// 固定文本
		if(f == null){ 
			System.out.println("word--->name控件不存在");
			return;	
		}
		f.setText(value);
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

	/**
	 * 在光标位置输入时间格式
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
	 * 测试专用
	 */
	public void onTest(){
		//		System.out.println("===:"+getESingleChooseValue("FIRST_MEDICAL_ORG", wordEDDR));	
		System.out.println("===:"+this.getEFixedValue(wordGRACE, "graceScore"));
	}


}
