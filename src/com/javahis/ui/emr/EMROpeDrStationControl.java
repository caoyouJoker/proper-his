package com.javahis.ui.emr;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.javahis.util.ADMUtil;
import com.javahis.util.OdoUtil;
import jdo.emr.EMRAMITool;
import jdo.emr.EMRPublicTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

/**
 * <p>介入室医生-胸痛中心记录</p>
 * 
 * <p>TIMI只能新建病历，不保存，只是用来作为评分工具</p>
 * 
 * @author WangQing 20170224
 *
 */

// 20170823
// 1、TIMI只能新建病历，不保存，只是用来作为评分工具

public class EMROpeDrStationControl extends TControl {
	/**
	 * wordOpe
	 */
	private TWord wordIrdr;
	/**
	 * TIMIword
	 */
	private TWord wordTimi;
	/**
	 * 介入医生saveFiles
	 */
	private String[] saveFilesIrdr;
	/**
	 * TIMIsaveFiles
	 */
	private String[] saveFilesTimi;

	/**
	 * 系统传入参数
	 */
	private TParm allParm;

	/**
	 * 住院就诊号（系统传入）
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
	 * 手术单号
	 */
	private String opBookSeq = "";

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
	 * 介入室医生胸痛中心记录病历新建或者打开，true 表示打开已有病历 false表示新建
	 */
	private boolean updateIrdr = false;
	
	/**
	 * 初始化
	 */
	public void onInit(){
		super.onInit();
		wordIrdr = (TWord) this.getComponent("WORD_OPE");
		wordIrdr.setName("WORD_OPE");
		wordTimi = (TWord) this.getComponent("WORD_TIMI");
		wordTimi.setName("WORD_TIMI");
		Object obj = this.getParameter();
		if(obj == null){
			this.messageBox_("系统参数 is null");
			return;
		}
		if(obj instanceof TParm){
			allParm = (TParm)obj;
			caseNo = allParm.getValue("CASE_NO");
			opdCaseNo=ADMUtil.getCaseNo(caseNo);
			mrNo = allParm.getValue("MR_NO");
			opBookSeq = allParm.getValue("OPBOOK_SEQ");
			Pat pat = Pat.onQueryByMrNo(mrNo);
			patName = pat.getName();
			patSex = ("1".equals(pat.getSexCode())?"男":"女");
			patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());	
		}
		openIrdrJhw();
		openTimiJhw();
	}

	/**
	 * 打开介入医生站病历
	 */
	public void openIrdrJhw(){
		saveFilesIrdr = EMRPublicTool.getInstance().getEmrFile(opdCaseNo, irdrClassCodeConfig, irdrSubclassCodeConfig);
		if(saveFilesIrdr != null && saveFilesIrdr[0] != null && saveFilesIrdr[0].trim().length()>0 
				&& saveFilesIrdr[1] != null && saveFilesIrdr[1].trim().length()>0 
				&& saveFilesIrdr[2] != null && saveFilesIrdr[2].trim().length()>0){// 打开已有病历
			System.out.println("打开已有介入医生站病历");
			updateIrdr = true;
			wordIrdr.onOpen(saveFilesIrdr[0], saveFilesIrdr[1], 3, false);
			wordIrdr.setCanEdit(true);
			wordIrdr.update();	
		}else{// 新建
			System.out.println("新建介入医生站病历");
			updateIrdr = false;
			saveFilesIrdr = EMRPublicTool.getInstance().getEmrTemplet(irdrSubclassCodeConfig);
			if(saveFilesIrdr == null 
					|| saveFilesIrdr[0] == null || saveFilesIrdr[0].trim().length()<=0 
					|| saveFilesIrdr[1] == null || saveFilesIrdr[1].trim().length()<=0 
					|| saveFilesIrdr[2] == null || saveFilesIrdr[2].trim().length()<=0){
				this.messageBox("没有找到介入医生站胸痛中心记录模板");
				return;
			}
			wordIrdr.onOpen(saveFilesIrdr[0], saveFilesIrdr[1], 2, false);		
			String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
			String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
			
			this.setECaptureValue(wordIrdr, "DECIDE_TIME", sysDate3);
			this.setECaptureValue(wordIrdr, "INFO_CONSENT_START_TIME", sysDate3);
			this.setECaptureValue(wordIrdr, "INFO_CONSENT_SIGN_TIME", sysDate3);
			
//			TParm opbook = OPEINTSaveTool.getInstance().getOPBOOK(opdCaseNo);//手术
//			TParm inform = OPEINTSaveTool.getInstance().getInformed(opdCaseNo);//开胸探查手术知情同意书
//			if (inform!=null && inform.getCount()>0){
//				// 开始知情同意时间
//				this.setCaptureValueArray("INFO_CONSENT_START_TIME", StringTool.getString(inform.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));		
//			}else{
//				this.setCaptureValueArray("INFO_CONSENT_START_TIME", sysDate3);
//			}
//			
//			if (opbook!=null && opbook.getCount()>0){
//				// 决定手术时间
//				this.setCaptureValueArray("DECIDE_TIME", StringTool.getString(opbook.getTimestamp("OPT_DATE", 0),"yyyy/MM/dd HH:mm"));
//				this.setCaptureValueArray("DECIDE_DR", opbook.getValue("USER_NAME",0));
//				this.setCaptureValueArray("INT_DR", opbook.getValue("USER_NAME",0));			
//			}else{
//				this.setCaptureValueArray("DECIDE_TIME", sysDate3);
//			}	
			
			// add by wangqing 20170707 start
			// 决定医生、介入医生自动带入手术申请时的决定医生和介入医生
			String sql = " SELECT A.BOOK_DR_CODE, A.MAIN_SURGEON, B.USER_NAME AS BOOK_DR_NAME, C.USER_NAME AS MAIN_SURGEON_NAME FROM OPE_OPBOOK A, SYS_OPERATOR B, SYS_OPERATOR C "
					+ "WHERE A.BOOK_DR_CODE=B.USER_ID(+) AND A.MAIN_SURGEON=C.USER_ID(+) AND A.OPBOOK_SEQ ='"+opBookSeq+"' ";
			
			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			if(result.getErrCode()<0){
				return;
			}
			if(result.getCount()<=0){
				this.messageBox("没有介入手术！！！");
			}
			String bookDrCode = result.getValue("BOOK_DR_NAME", 0);// 决定医生
			String mainSurgeon = result.getValue("MAIN_SURGEON_NAME", 0);// 介入医生
			this.setECaptureValue(wordIrdr, "DECIDE_DR", bookDrCode);
			this.setECaptureValue(wordIrdr, "INT_DR", mainSurgeon);
			// add by wangqing 20170707 end
			wordIrdr.setMicroField("姓名", patName);
			wordIrdr.setMicroField("性别", patSex);
			wordIrdr.setMicroField("年龄", patAge);
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			wordIrdr.setWordParameter(allParm);
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
	 * 保存
	 */
	public void onSave(){
		String path = "";
		String fileName = "";
		// 介入保存
		if (updateIrdr) {// 更新
			path = saveFilesIrdr[0];
			fileName = saveFilesIrdr[1];	
		} else {// 新增
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(opdCaseNo, irdrClassCodeConfig, irdrSubclassCodeConfig, saveFilesIrdr[1]);
			if (erdParm.getErrCode() < 0) {				 
				this.messageBox("E0066");
				return;
			}
			path = erdParm.getValue("PATH");
			fileName = erdParm.getValue("FILENAME");		
		} 
		wordIrdr.setMessageBoxSwitch(false);
		wordIrdr.onSaveAs(path, fileName, 3);
		wordIrdr.setCanEdit(true);
		wordIrdr.update();
		this.messageBox("保存成功！！！");
		this.closeWindow(); 
	}

	/**
	 * 获取性别中文描述
	 * @param sexCode
	 * @return
	 */
	public String getSexChnDesc(String sexCode){
		String sql = "SELECT CHN_DESC FROM SYS_DICTIONARY WHERE GROUP_ID = 'SYS_SEX' AND ID = '" + sexCode +"' ";
		TParm result = new TParm(TJDODBTool.getInstance().select(sql));
		String sexChnDesc = result.getValue("CHN_DESC", 0);
		return sexChnDesc;
	}

	/**
	 * 获取年龄
	 * @param birthDate
	 * @param sysDate
	 * @return
	 */
	public String getAge(Timestamp birthDate, Timestamp sysDate){
		return OdoUtil.showAge(birthDate, sysDate);
	}

	/**
	 * Date->String
	 * @param date
	 * @return
	 */
	public String dateToString(Date date){
		//		Date date = new Date();
		String dateStr = "";
		//format的格式可以任意
		//		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH/mm/ss");
		DateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		try {
			//			dateStr = sdf.format(date);
			//			System.out.println(dateStr);
			dateStr = sdf2.format(date);
			System.out.println(dateStr);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return dateStr;
	}

	/**
	 * String->Date
	 * @param dateString
	 * @return
	 */
	public Date stringToDate(String dateStr){
		//		String dateStr = "2010-05-04 12:34:23";
		Date date = new Date();
		//注意format的格式要与日期String的格式相匹配
		DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH/mm/ss");
		try {
			date = sdf.parse(dateStr);
			System.out.println(date.toString());
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return date;		
	}

	/**
	 * Timestamp->Date
	 * @param time
	 * @return
	 */
	public Date timestampToDate(Timestamp time){
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		Date date = new Date();
		try {
			date = ts;
			System.out.println(date);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return date;
	}

	/**
	 * timestampToString
	 * @param ts
	 * @param format
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

}