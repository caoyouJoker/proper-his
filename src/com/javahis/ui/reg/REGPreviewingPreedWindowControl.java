package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import jdo.emr.EMRPublicTool;
import jdo.reg.RegPreedTool;
import jdo.sys.Pat;
import jdo.sys.SystemTool;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TWord;
import com.dongyang.ui.base.TWordBase;
import com.dongyang.util.StringTool;
import com.javahis.util.OdoUtil;

/**
 * <p>胸痛中心急诊护士记录</p>
 * <p>只有新建的病历才会进行以下操作：1、初始化数据 2、设置监听</p>
 * <p>已存在的病历只能手动修改（病案号为空的除外）</p>
 * @author wangqing 
 *
 */

public class REGPreviewingPreedWindowControl extends TControl{

	/**
	 * 胸痛急诊护士记录病历
	 */
	private TWord word;
	/**
	 * 结构化病历路径
	 */
	private String[] saveFiles;	
	/**
	 * 系统传入参数
	 */
	private TParm parm;	
	/**
	 * 急诊检伤号
	 */
	private String triageNo = "";
	/**
	 * 急诊就诊号
	 */
	private String caseNo = "";
	/**
	 * 病案号
	 */
	private String mrNo = "";
	/**
	 * 身份证号
	 */
	private String idNo = "";
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
	 * 患者生日
	 */
	private String patBirthday = "";
	/**
	 * 联系电话（联系人电话-》本人手机-》本人家庭电话-》本人公司电话）
	 */
	private String cellPhone = "";
	/**
	 * 插入还是更新
	 */
	private boolean update = false;
	
	private final String classCodeConfig = "AMI_PRE_CLASSCODE";
	private final String subClassCodeConfig = "AMI_PRE_SUBCLASSCODE";

	/**
	 * 初始化
	 */
	public void onInit(){
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			parm = (TParm) this.getParameter();
			triageNo = parm.getValue("triageNo");
			caseNo = parm.getValue("caseNo");
			mrNo = parm.getValue("mrNo");
			if(mrNo != null && !mrNo.trim().equals("")){
				Pat pat = Pat.onQueryByMrNo(mrNo);
				this.idNo = pat.getIdNo();
				patName = pat.getName();
				patSex = ("1".equals(pat.getSexCode())?"男":"女");
				patAge = OdoUtil.showAge(pat.getBirthday(),SystemTool.getInstance().getDate());
				patBirthday = StringTool.getString(pat.getBirthday(), "yyyy/MM/dd");			
				if(pat.getContactsTel() != null && pat.getContactsTel().trim().length()>0){
					cellPhone = pat.getContactsTel();
				}else if (pat.getCellPhone() != null && pat.getCellPhone().trim().length()>0){
					cellPhone = pat.getCellPhone();
				}else if (pat.getTelHome() != null && pat.getTelHome().trim().length()>0){
					cellPhone = pat.getTelHome();
				}else if (pat.getTelCompany() != null && pat.getTelCompany().trim().length()>0){
					cellPhone = pat.getTelCompany();
				}
			}
		}	
		initJHW();
	}

	/**
	 * 初始化结构化病历
	 */
	public void initJHW(){
		word = (TWord) this.getComponent("tWord_0");
		word.setName("tWord_0");
//		saveFiles = RegPreedTool.getInstance().getPreedFile(triageNo);
		saveFiles = EMRPublicTool.getInstance().getEmrFile(triageNo, classCodeConfig, subClassCodeConfig);
		if(saveFiles == null 
				|| saveFiles[0] == null || saveFiles[0].trim().length()<=0 
				|| saveFiles[1] == null || saveFiles[1].trim().length()<=0 
				|| saveFiles[2] == null || saveFiles[2].trim().length()<=0){// 新建病历
			System.out.println("=======新建病历======");
//			saveFiles = RegPreedTool.getInstance().getErdLevelTemplet();
			saveFiles = EMRPublicTool.getInstance().getEmrTemplet(subClassCodeConfig);
			word.onOpen(saveFiles[0], saveFiles[1], 2, false);

			String sysDate2 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd HH:mm");
			String sysDate3 = timestampToString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";

			this.setECaptureValue(word, "PAT_LOG_TIME",sysDate2);//胸痛患者登记时间
			this.setECaptureValue(word, "START_TIME", sysDate3);// 发病时间
			this.setECaptureValue(word, "CALL_HELP_TIME", sysDate3);// 呼救时间
			this.setECaptureValue(word, "TNI_BLOOD_DRAWING_TIME", sysDate3);// 辅助检查，TNI取血时间
			this.setECaptureValue(word, "REPORT_TIME", sysDate3);// 辅助检查，报告时间
	
			word.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected");// add by wangqing 监听来院方式的改变
			word.setCanEdit(true);
			word.update();
			update = false;
		}else{
			System.out.println("=======打开已有病历======");
			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
			word.addListener(TWordBase.SINGLE_CHOOSE_SELECTED, this, "singleChooseSelected");// add by wangqing 监听来院方式的改变
			word.setCanEdit(true);
			word.update();
			update=true;
		}
		
		try{
			if(mrNo != null && !mrNo.trim().equals("")){
				parm.setData("FILE_HEAD_TITLE_MR_NO","TEXT", parm.getData("mrNo").toString());	
				word.setWordParameter(parm);
				word.setMicroField("姓名", patName);
				word.setMicroField("性别", patSex);
				word.setMicroField("年龄", patAge);
				word.setMicroField("出生日期",  patBirthday);			
				word.setMicroField("联系人电话", cellPhone);
				this.setECaptureValue(word, "ID_NO", idNo);
//				word.getFocusManager().reset();
				word.setCanEdit(true);
				word.update();
			}
		}catch(Exception e){

		}
		
			
	}

	/**
	 * 单选框选择事件
	 * @param wordName
	 * @param singleChooseName
	 */
	public void singleChooseSelected(String wordName, String singleChooseName){
		if(wordName != null && wordName.equals("tWord_0")){
			if(singleChooseName != null && singleChooseName.equals("ERD_TYPE")){
				// 查询到大门时间和接诊时间
				String triageTimeSql = "SELECT (TO_CHAR (ADM_DATE, 'YYYY/MM/DD')||' '||TO_CHAR(COME_TIME, 'HH24:MI')) AS GATE_TIME, TO_CHAR (TRIAGE_TIME, 'YYYY/MM/DD HH24:MI') AS TRIAGE_TIME FROM ERD_EVALUTION WHERE TRIAGE_NO='"+triageNo+"' ";
				TParm triageTimeResult = new TParm(TJDODBTool.getInstance().select(triageTimeSql));
				String gateTime = "";
				String triageTime = "";
				if(triageTimeResult.getCount()>0){
					triageTime = triageTimeResult.getValue("TRIAGE_TIME", 0);	// 接诊时间
					gateTime = triageTimeResult.getValue("GATE_TIME", 0);	// 到大门时间			
				}
				String erdType = getESingleChooseText(word, "ERD_TYPE");	
				System.out.println("===erdType:"+erdType);
				if(erdType.equals("呼救(120或其它)出车")){
					setCaptureValueArray2(word, "TRANSFER_DOOR_TIME_IN;TRANSFER_IN_ADMIT_TIME;SELF_DOOR_TIME;SELF_ADMIT_TIME", " ");
					setECaptureValue(word, "DOOR_TIME", gateTime);// 到达本院大门时间
					setECaptureValue(word, "IN_ADMIT_TIME", triageTime);// 院内接诊时间
				}else if(erdType.equals("转院")){
					setCaptureValueArray2(word, "DOOR_TIME;IN_ADMIT_TIME;SELF_DOOR_TIME;SELF_ADMIT_TIME", " ");
					setECaptureValue(word, "TRANSFER_DOOR_TIME_IN", gateTime);// 到达本院大门时间
					setECaptureValue(word, "TRANSFER_IN_ADMIT_TIME", triageTime);// 院内接诊时间
				}else if(erdType.equals("自行来院")){
					setCaptureValueArray2(word, "DOOR_TIME;IN_ADMIT_TIME;TRANSFER_DOOR_TIME_IN;TRANSFER_IN_ADMIT_TIME", " ");
					setECaptureValue(word, "SELF_DOOR_TIME", gateTime);// 到达本院大门时间
					setECaptureValue(word, "SELF_ADMIT_TIME", triageTime);// 院内接诊时间
				}else{
					setCaptureValueArray2(word, "DOOR_TIME;IN_ADMIT_TIME;TRANSFER_DOOR_TIME_IN;TRANSFER_IN_ADMIT_TIME;SELF_DOOR_TIME;SELF_ADMIT_TIME", " ");
				}
			}
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
//			TParm erdParm = RegPreedTool.getInstance().saveELFile(triageNo, saveFiles[2], saveFiles[1]);
			TParm erdParm = EMRPublicTool.getInstance().saveEmrFile(triageNo, classCodeConfig, subClassCodeConfig, saveFiles[1]);
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
		this.closeWindow();
	}

	/**
	 * 删除已有病历
	 */
	public void onDelete(){
		if(update){
			if(EMRPublicTool.getInstance().deleteEmrFile(triageNo, classCodeConfig, subClassCodeConfig)){
				this.messageBox("删除成功！！！");
			}else{
				this.messageBox("删除失败！！！");
			}
			this.closeWindow();
		}else{
			this.messageBox("没有病历！！！");
		}
	}
		
	/**
	 * 删除模版文件
	 * @param templetPath String
	 * @param templetName String
	 * @return boolean
	 */
	public boolean delFileTempletFile(String templetPath, String templetName) {
		//目录表第一个根目录FILESERVER
		String rootName = TIOM_FileServer.getRoot();
		//模板路径服务器
		String templetPathSer = TIOM_FileServer.getPath("EmrTemplet");
		//拿到Socket通讯工具
		TSocket socket = TIOM_FileServer.getSocket();
		//删除文件
		return TIOM_FileServer.deleteFile(socket,
				rootName + templetPathSer +
				templetPath +
				"\\" + templetName + ".jhw");
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
	 * <p>设置多个抓取值</p>
	 * @param names name1;name2;name3
	 * @param value
	 */
	public void setCaptureValueArray2(TWord word, String names, String value) {
		if(word == null){
			System.out.println("word is null");
			return;
		}
		if(names == null){
			System.out.println("names is null");
			return;
		}
		if(value == null){
			System.out.println("value is null");
			return;
		}
		String[] cName = names.split(";");
		for(int i=0; i<cName.length; i++){
			setECaptureValue(word, cName[i], value);
		}	
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
	 * 在当前鼠标焦点录入时间
	 */
	public void onNow(){
		String now = StringTool.getString(new Timestamp(System.currentTimeMillis()), "yyyy/MM/dd ")+"00:00";
		this.word.pasteString(now);
	}

}
