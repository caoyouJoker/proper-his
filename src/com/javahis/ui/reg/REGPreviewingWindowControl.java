package com.javahis.ui.reg;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jdo.reg.RegSaveTool;
import jdo.erd.ERDLevelTool;
import jdo.reg.REGTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.SystemTool;

import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.data.TSocket;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_AppServer;
import com.dongyang.manager.TIOM_FileServer;
import com.dongyang.tui.text.ECapture;
import com.dongyang.tui.text.ECheckBoxChoose;
import com.dongyang.tui.text.EComponent;
import com.dongyang.tui.text.EFixed;
import com.dongyang.tui.text.ESingleChoose;
import com.dongyang.ui.TFrame;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TToolButton;
import com.dongyang.ui.TWord;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TypeTool;
import com.javahis.ui.database.MicroFieldControl;
import com.javahis.util.OdoUtil;
/**
 * 急诊护士站-胸痛急诊护士记录
 * @author WangQing 20170327
 *
 */
public class REGPreviewingWindowControl extends TControl{

	private TWord word;
	// 打开已经看诊的病患的结构化病历所需要的存储路径saveFilesword
	private String[] saveFiles;
	private String flg="";
	private String erdLevel = "";
	private boolean update = false;//true 表示只修改检伤  false表示新增数据保存


	/**
	 * 新增还是更新标记
	 */
	private boolean isNew;

	/**
	 * 是否存在病历
	 */
	private boolean isExistFile;

	/**
	 * 就诊号（系统传入）
	 */
	private String caseNo;

	/**
	 * 病案号
	 */
	private String mrNo;

	/**
	 * 患者姓名
	 */
	private String patName;

	/**
	 * 患者性别
	 */
	private String sexCode;

	/**
	 * 患者年龄
	 */
	private String age;

	/**
	 * 检伤号
	 */
	private String triageNo;

	public void onInit(){
		super.onInit();
		word = (TWord) this.getComponent("tWord_0");
		isNew = false;
		isExistFile = false;	
		Object o = this.getParameter();
		if(o != null && o instanceof TParm){
			TParm sysParm = (TParm) o;
			caseNo = sysParm.getValue("CASE_NO");
			mrNo = sysParm.getValue("MR_NO");
			patName = sysParm.getValue("PAT_NAME");
			sexCode = sysParm.getValue("SEX_CODE");
			age = sysParm.getValue("AGE");
			triageNo = sysParm.getValue("TRIAGE_NO");
		}
		this.openONWRegNurseStationEmr();
	}


	/**
	 * 打开急诊护士站-胸痛中心病历
	 */
	public void openONWRegNurseStationEmr(){
		// 打开或者新增手术医生站-胸痛中心记录病历
		// 已存在，打开
		// 不存在，新增
		saveFiles = RegSaveTool.getInstance().getELFile(caseNo);
//		System.out.println("======saveFiles[0]:::"+saveFiles[0]);
//		System.out.println("======saveFiles[1]:::"+saveFiles[1]);
//		System.out.println("======saveFiles[2]:::"+saveFiles[2]);
		if(saveFiles==null || saveFiles[0].equals("")){
			isExistFile = false;
		}else{   
			isExistFile = true;
		}   
		if(isExistFile){// 已存在，打开
//			this.messageBox("已存在病历！！！");
			isNew = false;
			word.onOpen(saveFiles[0], saveFiles[1], 3, false);
			word.setCanEdit(true);
			word.update();
		}else{// 不存在，新建
//			this.messageBox("不存在病历！！！");
			isNew = true;		
			saveFiles = RegSaveTool.getInstance().getErdLevelTemplet(); 	
			System.out.println("======saveFiles[0]:::"+saveFiles[0]);
			System.out.println("======saveFiles[1]:::"+saveFiles[1]);
			word.onOpen(saveFiles[0], saveFiles[1], 2, false);
			word.setMicroField("姓名", patName);
			word.setMicroField("性别", this.getSexChnDesc(sexCode));		
			word.setMicroField("年龄", age);
			/*modified by Eric 20170517 add system time*/
			Timestamp sysDate = SystemTool.getInstance().getDate();
//			System.out.println(timestampToString(sysDate));
//			setCaptureValue("FIRST_IN_ECG_TIME", timestampToString(sysDate));
//			setCaptureValue("TNI_BLOOD_DRAWING_TIME", timestampToString(sysDate));
//			setCaptureValue("REPORT_TIME", timestampToString(sysDate));
			
			TParm allParm = new TParm();
			allParm.setData("FILE_HEAD_TITLE_MR_NO", "TEXT", mrNo);
			word.setWordParameter(allParm);
			word.setCanEdit(true);
			word.update();		
		}	
	}

	/**
	 * 保存
	 */
	public void onSave(){
		// 1. 保存病历 2.保存病历数据
		/** 保存病历-----------------start----------------*/
		if(isNew){// 新增
			TParm erdParm = RegSaveTool.getInstance().saveELFile(caseNo, saveFiles[2], saveFiles[1]);
			if (erdParm.getErrCode() < 0) {
				this.messageBox("E0066");
				return;
			}
			String path = erdParm.getValue("PATH");
			String fileName = erdParm.getValue("FILENAME");
			word.onSaveAs(path, fileName, 3); 
		}else{// 更新
			word.onSaveAs(saveFiles[0], saveFiles[1], 3);
		}
		/** 保存病历-----------------end----------------*/

		/**保存病历数据 -----------------start------------------*/

		/** 获取控件---------------------start-----------------*/
		// 获取抓取控件ECapture
		ECapture FIRST_IN_ECG_TIME = (ECapture) word.findObject("FIRST_IN_ECG_TIME", EComponent.CAPTURE_TYPE);
//		ECapture CONSCIOUS = (ECapture) word.findObject("CONSCIOUS", EComponent.CAPTURE_TYPE);
//		ECapture RESPIRATORY_RATE = (ECapture) word.findObject("RESPIRATORY_RATE", EComponent.CAPTURE_TYPE);
//		ECapture PULSE = (ECapture) word.findObject("PULSE", EComponent.CAPTURE_TYPE);
//		ECapture CARDIAC_RATE = (ECapture) word.findObject("CARDIAC_RATE", EComponent.CAPTURE_TYPE);
//		ECapture DIASTOLIC_BLOOD_PRESSURE = (ECapture) word.findObject("DIASTOLIC_BLOOD_PRESSURE", EComponent.CAPTURE_TYPE);
//		ECapture SYSTOLIC_BLOOD_PRESSURE = (ECapture) word.findObject("SYSTOLIC_BLOOD_PRESSURE", EComponent.CAPTURE_TYPE);
//		ECapture KILLIP = (ECapture) word.findObject("KILLIP", EComponent.CAPTURE_TYPE);
		ECapture TNI_BLOOD_DRAWING_TIME = (ECapture) word.findObject("TNI_BLOOD_DRAWING_TIME", EComponent.CAPTURE_TYPE);
		ECapture REPORT_TIME = (ECapture) word.findObject("REPORT_TIME", EComponent.CAPTURE_TYPE);
		ECapture CTNL = (ECapture) word.findObject("CTNL", EComponent.CAPTURE_TYPE);
		ECapture BLOOD_CREATININE = (ECapture) word.findObject("BLOOD_CREATININE", EComponent.CAPTURE_TYPE);
		/** 获取控件---------------------end-----------------*/

		/** 获取控件内容-------------------start----------------*/
		// 获取抓取控件内容
		String FIRST_IN_ECG_TIME_C = FIRST_IN_ECG_TIME.getValue(false);
//		String CONSCIOUS_C = CONSCIOUS.getValue(false);
//		Integer RESPIRATORY_RATE_C = RESPIRATORY_RATE.getValue(false).equals("") ? null : Integer.parseInt(RESPIRATORY_RATE.getValue(false));
//		Integer PULSE_C = PULSE.getValue(false).equals("") ? null : Integer.parseInt(RESPIRATORY_RATE.getValue(false));
//		Integer CARDIAC_RATE_C = CARDIAC_RATE.getValue(false).equals("") ? null : Integer.parseInt(CARDIAC_RATE.getValue(false));
//		Integer DIASTOLIC_BLOOD_PRESSURE_C = DIASTOLIC_BLOOD_PRESSURE.getValue(false).equals("") ? null : Integer.parseInt(DIASTOLIC_BLOOD_PRESSURE.getValue(false));
//		Integer SYSTOLIC_BLOOD_PRESSURE_C = SYSTOLIC_BLOOD_PRESSURE.getValue(false).equals("") ? null : Integer.parseInt(SYSTOLIC_BLOOD_PRESSURE.getValue(false));
//		String KILLIP_C = KILLIP.getValue(false);
		String TNI_BLOOD_DRAWING_TIME_C = TNI_BLOOD_DRAWING_TIME.getValue(false);
		String REPORT_TIME_C = REPORT_TIME.getValue(false);
		Integer CTNL_C = CTNL.getValue(false).equals("") ? null : Integer.parseInt(CTNL.getValue(false));
		Integer BLOOD_CREATININE_C = BLOOD_CREATININE.getValue(false).equals("") ? null : Integer.parseInt(BLOOD_CREATININE.getValue(false));
		/** 获取控件内容-------------------end----------------*/


		TParm emrData = new TParm();
		emrData.setData("TRIAGE_NO", triageNo);// 检伤号
		emrData.setData("CASE_NO", caseNo);// 就诊号
		emrData.setData("FIRST_IN_ECG_TIME", FIRST_IN_ECG_TIME_C);	
//		emrData.setData("CONSCIOUS", CONSCIOUS_C);
//		emrData.setData("RESPIRATORY_RATE", RESPIRATORY_RATE_C);
//		emrData.setData("PULSE", PULSE_C);
//		emrData.setData("CARDIAC_RATE", CARDIAC_RATE_C);
//		emrData.setData("DIASTOLIC_BLOOD_PRESSURE", DIASTOLIC_BLOOD_PRESSURE_C);
//		emrData.setData("SYSTOLIC_BLOOD_PRESSURE", SYSTOLIC_BLOOD_PRESSURE_C);
//		emrData.setData("KILLIP", KILLIP_C);
		emrData.setData("TNI_BLOOD_DRAWING_TIME", TNI_BLOOD_DRAWING_TIME_C);
		emrData.setData("REPORT_TIME", REPORT_TIME_C);	
		emrData.setData("CTNL", CTNL_C);
		emrData.setData("BLOOD_CREATININE", BLOOD_CREATININE_C);
		emrData.setData("OPT_USER", Operator.getID());// 操作人员
		emrData.setData("OPT_DATE", this.dateToString((Date) SystemTool.getInstance().getDate()));// 操作日期
		emrData.setData("OPT_TERM", Operator.getIP());// 操作终端
		if(isNew){// insert
//			String insertSql = "INSERT INTO AMI_ERD_NS_RECORD (TRIAGE_NO, CASE_NO, FIRST_IN_ECG_TIME, CONSCIOUS, RESPIRATORY_RATE, PULSE, CARDIAC_RATE,DIASTOLIC_BLOOD_PRESSURE, SYSTOLIC_BLOOD_PRESSURE, KILLIP, TNI_BLOOD_DRAWING_TIME, REPORT_TIME, CTNL,BLOOD_CREATININE,OPT_USER, OPT_DATE, OPT_TERM) "
//					+ "VALUES ('"
//					+ emrData.getData("TRIAGE_NO")+"', '"
//					
//					+ emrData.getData("CASE_NO")+"', "
//					
//					+ "to_date ('"+ emrData.getData("FIRST_IN_ECG_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') ,'"
//					
//					+ emrData.getData("CONSCIOUS")+"', "
//
//					+ emrData.getData("RESPIRATORY_RATE")+", "
//
//					+ emrData.getData("PULSE")+", "
//
//					+ emrData.getData("CARDIAC_RATE")+", "
//
//					+ emrData.getData("DIASTOLIC_BLOOD_PRESSURE")+", "
//
//					+ emrData.getData("SYSTOLIC_BLOOD_PRESSURE")+", '"
//
//					+ emrData.getData("KILLIP")+"', "
//
//					+ "to_date ('"+ emrData.getData("TNI_BLOOD_DRAWING_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') ,"
//
//                    + "to_date ('"+ emrData.getData("REPORT_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') ,"
//
//					+ emrData.getData("CTNL")+", "
//
//					+ emrData.getData("BLOOD_CREATININE")+", '"
//
//					+ emrData.getData("OPT_USER")+"', "
//					
//					+ "to_date ('"+ emrData.getData("OPT_DATE") + "', 'YYYY-MM-DD HH24:MI:SS') ,'"
//					
//					+ emrData.getData("OPT_TERM") +"')";
			
			String insertSql = "INSERT INTO AMI_ERD_NS_RECORD (TRIAGE_NO, CASE_NO, FIRST_IN_ECG_TIME, TNI_BLOOD_DRAWING_TIME, REPORT_TIME, CTNL,BLOOD_CREATININE,OPT_USER, OPT_DATE, OPT_TERM) "
					+ "VALUES ('"
					+ emrData.getData("TRIAGE_NO")+"', '"
					
					+ emrData.getData("CASE_NO")+"', "
					
					+ "to_date ('"+ emrData.getData("FIRST_IN_ECG_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') ,"
					
					+ "to_date ('"+ emrData.getData("TNI_BLOOD_DRAWING_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') ,"

                    + "to_date ('"+ emrData.getData("REPORT_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') ,"

					+ emrData.getData("CTNL")+", "

					+ emrData.getData("BLOOD_CREATININE")+", '"

					+ emrData.getData("OPT_USER")+"', "
					
					+ "to_date ('"+ emrData.getData("OPT_DATE") + "', 'YYYY-MM-DD HH24:MI:SS') ,'"
					
					+ emrData.getData("OPT_TERM") +"')";
			// 执行插入
			TParm result = new TParm(TJDODBTool.getInstance().update(insertSql));
			System.out.println("======result:::"+result);
		}else{// update
//			String updateSql = "UPDATE AMI_ERD_NS_RECORD SET TRIAGE_NO = '"
//		            + emrData.getData("TRIAGE_NO")
//		            
//		            + "', CASE_NO = '"
//		            
//					+ emrData.getData("CASE_NO") 
//					
//					+ "', FIRST_IN_ECG_TIME = " 
//					
//					+ "to_date ('"+ emrData.getData("FIRST_IN_ECG_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') "
//					
//					+ ", CONSCIOUS = '"
//					
//					+ emrData.getData("CONSCIOUS")
//					
//					+ "', RESPIRATORY_RATE = "
//					
//					+ emrData.getData("RESPIRATORY_RATE")
//					
//					+ ", PULSE = "
//					
//					+ emrData.getData("PULSE")
//					
//					+ ", CARDIAC_RATE = "
//					
//					+ emrData.getData("CARDIAC_RATE")
//					
//					+ ", DIASTOLIC_BLOOD_PRESSURE = "
//					
//					+ emrData.getData("DIASTOLIC_BLOOD_PRESSURE")
//					
//					+ ", SYSTOLIC_BLOOD_PRESSURE = "
//					
//					+ emrData.getData("SYSTOLIC_BLOOD_PRESSURE")
//					
//					+ ", KILLIP = '"
//					
//					+ emrData.getData("KILLIP")
//					
//					+ "', TNI_BLOOD_DRAWING_TIME = "
//					
//					+ "to_date ('"+ emrData.getData("TNI_BLOOD_DRAWING_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') "
//					
//					+ ", REPORT_TIME = "
//					
//					+ "to_date ('"+ emrData.getData("REPORT_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') "
//					
//					+ ", CTNL = "
//					
//					+ emrData.getData("CTNL")
//					
//					+ ", BLOOD_CREATININE = "
//					
//					+ emrData.getData("BLOOD_CREATININE")
//					
//					+ ", OPT_USER = '"
//					
//					+ emrData.getData("OPT_USER")
//					
//					+ "', OPT_DATE = "
//					
//					+ "to_date ('"+ emrData.getData("OPT_DATE") + "', 'YYYY-MM-DD HH24:MI:SS') "
//					
//					+ ", OPT_TERM = '"
//					
//					+ emrData.getData("OPT_TERM")
//					+ "'";
			
			String updateSql = "UPDATE AMI_ERD_NS_RECORD SET TRIAGE_NO = '"
		            + emrData.getData("TRIAGE_NO")
		            
		            + "', CASE_NO = '"
		            
					+ emrData.getData("CASE_NO") 
					
					+ "', FIRST_IN_ECG_TIME = " 
					
					+ "to_date ('"+ emrData.getData("FIRST_IN_ECG_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') "
					
					+ ", TNI_BLOOD_DRAWING_TIME = "
					
					+ "to_date ('"+ emrData.getData("TNI_BLOOD_DRAWING_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') "
					
					+ ", REPORT_TIME = "
					
					+ "to_date ('"+ emrData.getData("REPORT_TIME") + "', 'YYYY-MM-DD HH24:MI:SS') "
					
					+ ", CTNL = "
					
					+ emrData.getData("CTNL")
					
					+ ", BLOOD_CREATININE = "
					
					+ emrData.getData("BLOOD_CREATININE")
					
					+ ", OPT_USER = '"
					
					+ emrData.getData("OPT_USER")
					
					+ "', OPT_DATE = "
					
					+ "to_date ('"+ emrData.getData("OPT_DATE") + "', 'YYYY-MM-DD HH24:MI:SS') "
					
					+ ", OPT_TERM = '"
					
					+ emrData.getData("OPT_TERM")
					+ "'";
			// 执行更新
			TParm result = new TParm(TJDODBTool.getInstance().update(updateSql));
			System.out.println("======result:::"+result);
		}	
		/**保存病历数据 -----------------end------------------*/

				this.closeWindow();

	}

	/**
	 * 删除
	 */
	public void onDelete(){
		this.messageBox("删除！！！");
		if(isExistFile){
			
		}
	}
	
	/**
	 * 清空
	 */
	public void onClear(){
		this.messageBox("清空！！！");
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
	 * Timestamp->String
	 * @param time
	 * @return
	 */
	public String timestampToString(Timestamp ts){
		//		Timestamp ts = new Timestamp(System.currentTimeMillis());
		String tsStr = "";
		DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		try {
			//方法一
			tsStr = sdf.format(ts);
			System.out.println(tsStr);
			//方法二
			//			tsStr = ts.toString();
			//			System.out.println(tsStr);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tsStr;

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
	 * 设置抓取值
	 * @author Eric
	 * @param capture
	 * @param value
	 */
	public void setCaptureValue(String capture, String value){
		ECapture sysTime = (ECapture) word.findObject(capture, EComponent.CAPTURE_TYPE);
		if(sysTime != null){
//			Timestamp sysDate = SystemTool.getInstance().getDate();
//			System.out.println(timestampToString(sysDate));
			sysTime.setFocusLast();
			sysTime.clear();
			sysTime.getFocusManager().pasteString(value);
			word.getFocusManager().update();
		}
	}

}
