package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.dongyang.util.StringTool;
import com.javahis.util.ADMUtil;
import com.javahis.util.OdoUtil;
import jdo.adm.ADMDrResvOutTool;
import jdo.emr.EMRAMITool;
import jdo.emr.EMRPublicTool;
import jdo.ibs.IBSOrderdTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

/**
 * <p>住院医生站胸痛中心记录</p>
 * 
 * <p>介入只能打开已有病历</p>
 * 
 * <p>TIMI只能新建病历，不保存，只是用来作为评分工具</p>
 * 
 * @author wangqing
 *
 */
// 20170823
// 1、介入只能打开已有病历
// 2、TIMI只能新建病历，不保存，只是用来作为评分工具

public class REGXTcoreControl extends TControl {
	/**
	 * 住院word
	 */
	private TWord wordOdi;
	/**
	 * 介入word
	 */
	private TWord wordIrdr;
	/**
	 * TIMIword
	 */
	private TWord wordTimi;

	/**
	 * 系统传入参数
	 */
	private TParm allParm;

	/**
	 * 住院saveFiles
	 */
	private String[] saveFilesOdi;
	/**
	 * 介入saveFiles
	 */
	private String[] saveFilesIrdr;
	/**
	 * TIMIsaveFiles
	 */
	private String[] saveFilesTimi;


	/**
	 * 住院就诊号
	 */
	private String caseNo;
	/**
	 * 急诊就诊号
	 */
	private String opdCaseNo;
	/**
	 * 病案号
	 */
	private String mrNo;	
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
	 * 住院医生站胸痛中心记录病历模板classCodeConfig
	 */
	private final String odiClassCodeConfig = "AMI_ODI_CLASSCODE";
	/**
	 * 住院医生站胸痛中心记录病历模板subclassCodeConfig
	 */
	private final String odiSubclassCodeConfig = "AMI_ODI_SUBCLASSCODE";
	/**
	 * 介入室医生胸痛中心记录病历模板classCodeConfig
	 */
	private final String irdrClassCodeConfig = "AMI_IRDR_CLASSCODE";
	/**
	 * 介入室医生胸痛中心记录病历模板subclassCodeConfig
	 */
	private final String irdrSubclassCodeConfig = "AMI_IRDR_SUBCLASSCODE";
	/**
	 * Timi评分记录病历classCodeConfig
	 */
	private static String timiClassCodeConfig = "AMI_TIMI_CLASSCODE";
	/**
	 * Timi评分记录病历subClassCodeConfig
	 */
	private static String timiSubclassCodeConfig = "AMI_TIMI_SUBCLASSCODE";
	/**
	 * 住院医生站胸痛中心记录病历新建或者打开，true 表示打开已有病历 false表示新建
	 */
	private boolean updateOdi = false;

	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		wordOdi = (TWord) this.getComponent("WORD_ODI");
		wordOdi.setName("WORD_ODI");
		wordIrdr = (TWord) this.getComponent("WORD_OPE");
		wordIrdr.setName("WORD_OPE");
		wordTimi = (TWord) this.getComponent("WORD_TIMI");
		wordTimi.setName("WORD_TIMI");
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox("系统参数 is null");
			return;
		}
		if(obj instanceof TParm){
			allParm = (TParm)obj;
			caseNo = allParm.getValue("CASE_NO");
			opdCaseNo=ADMUtil.getCaseNo(caseNo);
			mrNo = allParm.getValue("MR_NO");
			Pat pat = Pat.onQueryByMrNo(mrNo);
			patName = pat.getName();
			patSex = ("1".equals(pat.getSexCode())?"男":"女");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());	
		}
		openOdiJhw();
		openOpeJhw();
		openTimiJhw();
	}

	/**
	 * 打开住院医生站病历
	 */
	public void openOdiJhw(){
		//		saveFilesOdi = EMRAMITool.getInstance().getSaveFile(opdCaseNo,classCodeConfigOdi,subclassCodeConfigOdi);
		saveFilesOdi = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, odiClassCodeConfig, odiSubclassCodeConfig);
		if(saveFilesOdi != null && saveFilesOdi[0] != null && saveFilesOdi[0].trim().length()>0 
				&& saveFilesOdi[1] != null && saveFilesOdi[1].trim().length()>0 
				&& saveFilesOdi[2] != null && saveFilesOdi[2].trim().length()>0){// 打开已有病历
			System.out.println("打开已有住院医生站病历");
			updateOdi = true;
			wordOdi.onOpen(saveFilesOdi[0], saveFilesOdi[1], 3, false);	
			wordOdi.setCanEdit(true);
			wordOdi.update();		
		}else{// 新建病历
			System.out.println("新建住院医生站病历");
			updateOdi = false;
			//			saveFilesOdi = EMRAMITool.getInstance().getJHWTemplet(subclassCodeConfigOdi);
			saveFilesOdi = EMRPublicTool.getInstance().getEmrTemplet(odiSubclassCodeConfig);
			if(saveFilesOdi == null 
					|| saveFilesOdi[0] == null || saveFilesOdi[0].trim().length()<=0 
					|| saveFilesOdi[1] == null || saveFilesOdi[1].trim().length()<=0 
					|| saveFilesOdi[2] == null || saveFilesOdi[2].trim().length()<=0){
				this.messageBox("没有找到住院医生站胸痛中心记录模板");
				return;
			}
			wordOdi.onOpen(saveFilesOdi[0], saveFilesOdi[1], 2, false);	
			// 查询住院天数
			String daysSql = " SELECT A.IN_DATE, A.DS_DATE, B.BIRTH_DATE FROM ADM_INP A, SYS_PATINFO B"
					+ " WHERE A.MR_NO=B.MR_NO AND A.CASE_NO='"+caseNo+"' AND A.DS_DATE IS NULL ";
			TParm daysResult = new TParm(TJDODBTool.getInstance().select(daysSql));
			// 计算住院天数
			Timestamp tp = daysResult.getTimestamp("DS_DATE", 0);
			Timestamp sysDate = SystemTool.getInstance().getDate();
			if (tp == null) {
				int days = 0;
				if (daysResult.getTimestamp("IN_DATE", 0) == null) {
					daysResult.addData("DAYNUM", "");
				} else {
					days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
							"00:00:00"), StringTool.setTime(daysResult.getTimestamp(
									"IN_DATE", 0), "00:00:00"));
					setECaptureValue(wordOdi, "HOSPITAL_DAY", ""+days);// 住院天数
				}
			} else {
				int days = 0;
				if (daysResult.getTimestamp("IN_DATE", 0) == null) {
					daysResult.addData("DAYNUM", "");
				} else {
					days = StringTool.getDateDiffer(StringTool.setTime(daysResult
							.getTimestamp("DS_DATE", 0), "00:00:00"),
							StringTool.setTime(daysResult.getTimestamp("IN_DATE", 0),
									"00:00:00"));
				}
			}
			//查询病患诊断信息
			TParm diagParm = new TParm();
			diagParm.setData("IO_TYPE","O");//出院诊断
			diagParm.setData("CASE_NO", caseNo);
			diagParm.setData("MAINDIAG_FLG","Y");//主诊断
			TParm diagInfo = ADMDrResvOutTool.getInstance().selectDiag(diagParm);
			if(diagInfo.getCount()>0){
				this.setECaptureValue(wordOdi, "OUT_DIG_TIME", timestampToString(diagInfo.getTimestamp("OPT_DATE", 0), "yyyy/MM/dd HH:mm"));// 出院诊断时间
			}
			// 查询住院总费用
			TParm rexpParm = new TParm();
			rexpParm.setData("CASE_NO", caseNo);
			TParm rexpData = new TParm();
			rexpData = IBSOrderdTool.getInstance().selectdataAll(rexpParm);
			// System.out.println("rexpData收据项目" + rexpData);
			double sunTotAmt = 0.00;
			DecimalFormat    df   = new DecimalFormat("######0.00");   
			for (int i = 0; i < rexpData.getCount(); i++) {
				double totAmt = 0.00;
				totAmt = rexpData.getDouble("AR_AMT", i);
				sunTotAmt = sunTotAmt + totAmt;	
			}
			this.setECaptureValue(wordOdi, "TOTAL_FEE", df.format(sunTotAmt));// 住院总费用
			wordOdi.setMicroField("姓名", patName);
			wordOdi.setMicroField("性别", patSex);
			wordOdi.setMicroField("年龄", patAge);
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordOdi.setWordParameter(allParm);
			wordOdi.setCanEdit(true);
			wordOdi.update();
		}		
	}

	/**
	 * 打开介入医生站病历，只打开已有病历，没有则不做操作
	 */
	public void openOpeJhw(){
		//		saveFilesIrdr = EMRAMITool.getInstance().getSaveFile(opdCaseNo,classCodeConfigOpe,subclassCodeConfigOpe);
		saveFilesIrdr = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, irdrClassCodeConfig, irdrSubclassCodeConfig);
		if(saveFilesIrdr != null && saveFilesIrdr[0] != null && saveFilesIrdr[0].trim().length()>0 
				&& saveFilesIrdr[1] != null && saveFilesIrdr[1].trim().length()>0 
				&& saveFilesIrdr[2] != null && saveFilesIrdr[2].trim().length()>0){// 打已有病历
			wordIrdr.onOpen(saveFilesIrdr[0], saveFilesIrdr[1], 3, false);	
			wordIrdr.setCanEdit(true);
			wordIrdr.update();
		}
	}

	/**
	 * 打开TIMI病历，只新增，不保存
	 */
	public void openTimiJhw(){
//		saveFilesTimi = EMRAMITool.getInstance().getJHWTemplet(timiSubclassCodeConfig);
		saveFilesTimi = EMRPublicTool.getInstance().getEmrTemplet(timiSubclassCodeConfig);
		if(saveFilesTimi == null 
				|| saveFilesTimi[0] == null || saveFilesTimi[0].trim().length()<=0 
				|| saveFilesTimi[1] == null || saveFilesTimi[1].trim().length()<=0 
				|| saveFilesTimi[2] == null || saveFilesTimi[2].trim().length()<=0){
			System.out.println("没有介入Timi评分模板");
			return;
		}
		wordTimi.onOpen(saveFilesTimi[0], saveFilesTimi[1], 2, false);
		wordTimi.addListener(TWordBase.CHECK_BOX_CHOOSE_CLICKED, this, "checkBoxChooseClicked");
		wordTimi.addListener(TWordBase.CALCULATE_EXPRESSION, this, "calculateExpression");// add by wangqing 20170814
		wordTimi.setMicroField("姓名", patName);
		wordTimi.setMicroField("性别", patSex);
		wordTimi.setMicroField("年龄", patAge);
		TParm allParm = new TParm();
		allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
		wordTimi.setWordParameter(allParm);
		wordTimi.setCanEdit(true);
		wordTimi.update();
	}

	/**
	 * 保存方法
	 */
	public void onSave (){	 	
		String path = "";
		String fileName = "";
		// 住院保存		
		if(updateOdi){// 更新
			path = saveFilesOdi[0];
			fileName = saveFilesOdi[1];
		}else{// 新增
			//			TParm erdParm = EMRAMITool.getInstance().saveJHWFile(opdCaseNo, mrNo, classCodeConfigOdi, saveFilesOdi[2],saveFilesOdi[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(opdCaseNo, odiClassCodeConfig, odiSubclassCodeConfig, saveFilesOdi[1]);
			if (erdParm.getErrCode() < 0) {				 
				this.messageBox("E0066");
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");
		}		
		wordOdi.setMessageBoxSwitch(false);
		wordOdi.onSaveAs(path, fileName, 3);
		wordOdi.setCanEdit(true);
		wordOdi.update();
		// 介入保存	
		if(saveFilesIrdr != null 
				&& saveFilesIrdr[0] != null && saveFilesIrdr[0].trim().length()>0 
				&& saveFilesIrdr[1] != null && saveFilesIrdr[1].trim().length()>0 
				&& saveFilesIrdr[2] != null && saveFilesIrdr[2].trim().length()>0){// 打已有病历
			path = saveFilesIrdr[0];
			fileName = saveFilesIrdr[1]; 	
			wordIrdr.setMessageBoxSwitch(false);
			wordIrdr.onSaveAs(path, fileName, 3);
			wordIrdr.setCanEdit(true);
			wordIrdr.update();
		}
		this.messageBox("保存成功！！！");
		this.closeWindow(); 
	}

	/**
	 * Timestamp->String
	 * @param time
	 * @return
	 */
	public String timestampToString(Timestamp ts, String format){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat(format);//yyyy/MM/dd HH:mm
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
	 * 复选框单击事件
	 * @param wordName
	 * @param checkBoxChooseName
	 */
	public void checkBoxChooseClicked(String wordName, String checkBoxChooseName){
		System.out.println("wordName:"+wordName);
		System.out.println("checkBoxChooseName:"+checkBoxChooseName);
		if(wordName != null && wordName.equals("WORD_TIMI")){
			if(checkBoxChooseName != null && checkBoxChooseName.equals("timiAgeCheckBox")){
				int valueAge = Integer.parseInt(patAge.replace("岁", ""));
				System.out.println("===valueAge:"+valueAge);
				ECheckBoxChoose timiAgeCheckBox = getECheckBoxChoose(wordTimi, "timiAgeCheckBox");
				boolean timiAgeCheckBoxStatus = timiAgeCheckBox.isChecked();
				if(timiAgeCheckBoxStatus){
					if (valueAge >= 60 && valueAge <= 64) {
						setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "1");
					} else if (valueAge >= 65 && valueAge <= 74) {
						setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "2");
					} else if (valueAge >= 75) {
						setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "3");
					} 
				}else{
					setCheckBoxChooseValue(wordTimi, "timiAgeCheckBox", "0");
				}
			}
		}	
	}
	
	/**
	 * 计算表达式
	 * @param wordName
	 */
	public void calculateExpression(String wordName){
		if(wordName != null && wordName.equals("WORD_TIMI")){
			String timiTotal = getEFixedValue(wordTimi, "timiTotal");
			String timlLevelStr = getTimlLevel(timiTotal);
			setEFixedValue(wordTimi, "timlLevel", timlLevelStr);
			wordTimi.update();	
//			setCaptureValueArray("timiScore", timiTotal, wordEDDR);
//			wordEDDR.update();		
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
	
//	/**
//	 * 删除已有病历
//	 */
//	public void onDelete(){
//		if(updateOdi){
//			if(EMRPublicTool.getInstance().deleteEmrFile(opdCaseNo, odiClassCodeConfig, odiSubclassCodeConfig)){
//				this.messageBox("删除成功！！！");
//			}else{
//				this.messageBox("删除失败！！！");
//			}
//			this.closeWindow();
//		}else{
//			this.messageBox("没有病历！！！");
//		}
//	}
	
	

}
