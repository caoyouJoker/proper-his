package com.javahis.ui.odi;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.SwingUtilities;

import org.apache.commons.lang.StringUtils;

import jdo.bil.BILComparator;
import jdo.bil.BILInvoiceTool;
import jdo.clp.CLPTool;
import jdo.med.MedSmsTool;
import jdo.odi.OdiMainTool;
import jdo.sys.Operator;
import jdo.sys.Pat;
import jdo.sys.PatTool;
import jdo.sys.SYSBedTool;
import jdo.sys.SYSNewRegionTool;
import jdo.sys.SystemTool;

import com.dongyang.config.TConfig;
import com.dongyang.control.TControl;
import com.dongyang.data.TParm;
import com.dongyang.jdo.TJDODBTool;
import com.dongyang.manager.TIOM_Database;
import com.dongyang.ui.TCheckBox;
import com.dongyang.ui.TComboBox;
import com.dongyang.ui.TComponent;
import com.dongyang.ui.TLabel;
import com.dongyang.ui.TMenuItem;
import com.dongyang.ui.TPanel;
import com.dongyang.ui.TRadioButton;
import com.dongyang.ui.TTabbedPane;
import com.dongyang.ui.TTable;
import com.dongyang.ui.TTextField;
import com.dongyang.ui.TTextFormat;
import com.dongyang.ui.event.TComboBoxEvent;
import com.dongyang.ui.event.TTableEvent;
import com.dongyang.util.StringTool;
import com.dongyang.util.TMessage;
import com.javahis.system.textFormat.TextFormatSYSDeptForOprt;
import com.javahis.system.textFormat.TextFormatSYSOperator;
import com.javahis.system.textFormat.TextFormatSYSOperatorStation;
import com.javahis.system.textFormat.TextFormatSYSStation;
import com.javahis.ui.emr.EMRTool;
import com.javahis.ui.inw.INWExecTimerTask;
import com.javahis.ui.sys.LEDEXECUI;
import com.javahis.ui.sys.LEDUI;
import com.javahis.util.AMIUtil;
import com.javahis.util.ExportExcelUtil;
import com.javahis.util.JavaHisDebug;
import com.javahis.util.OdiUtil;
import com.javahis.util.SelectResult;
import com.javahis.util.StringUtil;

import device.PassDriver;

/**
 * <p>
 * Title: 住院管理=>住院医生站系统=>住院病患基本信息管理
 * </p>
 * 
 * <p>
 * Description: 住院病患基本信息管理
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2009年1月
 * </p>
 * 
 * <p>
 * Company: JavaHis
 * </p>
 * 
 * @author Miracle
 * @version JavaHis 1.0
 */
public class ODIMainControl extends TControl {
	private static String TABLE = "TABLE";
	private static String PANEL = "Panel";
	/**
	 * 当前子页面TAG
	 */
	public String workPanelTag = "";
	/**
	 * 设置系统代码
	 */
	private String runFlg = "";
	/**
	 * Socket传送护士站工具
	 */
	// private SocketLink client;
	/**
	 * 护士站权限
	 */
	private boolean inwPopedem;
	/**
	 * 跑马灯
	 */
	private LEDUI ledUi;
	private LEDEXECUI ledUi1;
	/**
	 * 跑马灯参数
	 */
	private TParm ledParm;
	/**
	 * 合理用药
	 */
	private boolean passIsReady = false;

	private boolean enforcementFlg = false;

	private int warnFlg;
	private String srceenWidth = "";
	/**
	 * ICU注记
	 */
	private boolean IsICU = false;
	private boolean oidrFlg; // ========= chenxi
	private String MRNO; // ============== CHENXI 20121224
	// $$=============add by lx 2012-07-03 加入排序功能start==================$$//
	private BILComparator compare = new BILComparator();// modify by wanglong
	// 20121128
	private boolean ascending = false;
	private int sortColumn = -1;

	// $$=============add by lx 2012-07-03 加入排序功能end==================$$//
	public String getSrceenWidth() {
		return srceenWidth;
	}

	public void setSrceenWidth(String srceenWidth) {
		this.srceenWidth = srceenWidth;
	}
	private boolean ibsFlg = false;//====yanjing 20140807


	private INWExecTimerTask ett ;

	/**
	 * @author Wangqing
	 * 教学医生站flg
	 */
	boolean tFlg = false;

	// 2017/08/23 经与医院确认将胸痛中心底色由粉色改为灰色
	private Color CPCColor = Color.GRAY;

	private TComboBox getTComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * 初始化参数
	 */
	public void onInitParameter() {
		// 测试数据 IBS
		// this.setPopedem("deptEnabled",true);
		//		 this.setPopedem("deptAll",true);
		// this.setPopedem("stationEnabled",true);
		// this.setPopedem("stationAll",true);
		// this.setPopedem("odiButtonVisible",true);
		// this.setPopedem("inwCheckVisible",true);
		// this.setPopedem("inwExecuteVisible",true);
		// this.setPopedem("ibsStButVisible",true);
		//		 this.setPopedem("INWLEAD",true);
		// 住院



		this.setPopedem("deptEnabled", true);
		this.setPopedem("deptAll", true);
		this.setPopedem("stationEnabled", true);
		this.setPopedem("stationAll", true);
		this.setPopedem("odiButtonVisible", true);
		this.setPopedem("inwCheckVisible", true);
		// modify shibl 20120317
		// this.setPopedem("inwExecuteVisible", true);
		this.setPopedem("ibsStButVisible", true);

	}


	public void onInit() { 

		super.onInit();
		//add by yangjj 20151023 护士执行定时器
		ett = new INWExecTimerTask();

		ibsFlg = this.getPopedem("INWLEAD");//住院计价是否是护士长权限
		this.getTMenuItem("card").setEnabled(false);
		// shibl 20120329 add
		this.callFunction("UI|tpr|setVisible", false);
		this.callFunction("UI|newtpr|setVisible", false);
		// TABLE双击事件
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");

		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicke");
		//		this.callFunction("UI|tComboBox_0|addEventListener",TComboBoxEvent.SELECTED, this, "onChangeValue");

		// //注册TPanel点击事件
		// callFunction("UI|" + PANEL + "|addEventListener",
		// PANEL + "->" + "", this, "onTableClicked");
		// 设置系统代码
		Object obj = this.getParameter();
		if("OIDR".equals(obj) || "OIDR;1024".equals(obj)){//会诊进入时显示会诊标题  machao  20170602
			this.setTitle("会诊");
		}
		//machao start 20170503 判断是否是从感染页面调入
		if(obj instanceof TParm){
			//this.messageBox(((TParm)obj).getValue("INF")+"");
			if(!StringUtil.isNullString(((TParm)obj).getValue("INF"))){
				obj = ((TParm)obj).getValue("INF").toString();
			}			
		}
		//machao end 20170503

		//this.messageBox(obj+"");
		if (obj != null) {
			// $$add by lx 2012/03/19 处理1024*768;
			String strParameter = this.getParameter().toString();
			String sysID = "";
			// 包含;多个
			if (strParameter.indexOf(";") != -1) {
				sysID = strParameter.split(";")[0];
				this.setSrceenWidth(strParameter.split(";")[1]);
			} else {
				sysID = this.getParameter().toString();
				//machao start 20170503 判断是否是从感染页面调入 感染跳入会诊
				if("OIDR".equals(obj)){
					sysID = (String)obj;
				}				
				//machao end 20170503
			}

			this.setRunFlg(sysID);	

			/*modified by WangQing 20170428 start*/
			if(sysID != null && sysID.equals("T")){
				tFlg = true;
				this.callFunction("UI|setMenuConfig",
						"%ROOT%\\config\\odi\\ODIMainUI2Menu.x");
				TTable table = (TTable) this.getComponent("TABLE");
				table.setHeader("床号,60;姓名,80;病案号,100;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,140;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;预交金余额,120,double;临床路径,130,CLNCPATH_CODE;时程,100,SCHD_CODE;单病种,100,DISE_CODE;住院号,100;病案审核,100,MRO_CHAT_FLG;病历封存,140,SEALED_STATUS;服务等级,140,SERVICE_LEVELOUT;传筛,150;过敏,30;跌倒风险,80;日间手术,80,boolean");// 2017.0331 zhanglei 增加日间手术标记
				table.setEnHeader("BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;schdCode;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;SCHD_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;DAY_OPE_FLG;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");// 2017.0331 zhanglei 增加日间手术标记
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;17,left;18,left");
				this.setTitle("教学医生站");
			}
			/*// modified by WangQing 20170428 end*/

			// =============== modify by chenxi
			if (sysID != null && sysID.equals("OIDR")) {

				this.setOidrFlg(true);
				this.callFunction("UI|setMenuConfig",
						"%ROOT%\\config\\odi\\ODIMainUI2Menu.x");
			} else {
				if (sysID != null && sysID.equals("ODI")) {
					this.setOidrFlg(false);
					this.callFunction("UI|setMenuConfig",
							"%ROOT%\\config\\odi\\ODIMainUI2Menu.x");
					//20151026 wangjc start 删除一周前宕机备份文件
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							OPDBackupDataToPDFControl opdBackupDataToPDFControl = new OPDBackupDataToPDFControl();
							opdBackupDataToPDFControl.deleteFile();
						}
					});
					thread.start();
					//20151026 wangjc end 删除一周前宕机备份文件
				}
			}
			// 动态加载菜单后必须使用的初始化一步
			this.callFunction("UI|onInitMenu");
			// this.messageBox("=come==");
			this.getTMenuItem("card").setEnabled(false);
			// =========== modify by chenxi

			// ======================= chenxi modify 20130305
			if (sysID != null && sysID.equals("MRO")) {
				TTable table = (TTable) this.getComponent("TABLE");
				table
				.setHeader("病案号,100;住院号,100;床号,60;姓名,80;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,120;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;预交金余额,120,double;临床路径,100,CLNCPATH_CODE;单病种,100,DISE_CODE;病案审核,100,MRO_CHAT_FLG;服务等级,140,SERVICE_LEVELOUT");
				table
				.setParmMap("MR_NO;IPD_NO;BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;DISE_CODE;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				table
				.setColumnHorizontalAlignmentData("2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,right;14,left;15,left;16,left;17,left");
			}
			if(sysID != null && sysID.equals("IBSPAYBILL")){//缴费作业表格添加“付款方式二”--xiongwg20150413
				TTable table = (TTable) this.getComponent("TABLE");
				table.setHeader("床号,60;姓名,80;病案号,100;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,120;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;付款方式二,80,CTZ_CODE;预交金余额,120,double;临床路径,100,CLNCPATH_CODE;单病种,100,DISE_CODE;住院号,100;病案审核,100,MRO_CHAT_FLG;服务等级,140,SERVICE_LEVELOUT");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CTZ2_CODE;CUR_AMT;CLNCPATH_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;SCHD_CODE;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,right;14,left;17,left;18,left");
			}
			if(sysID != null && (sysID.equals("ODI")||sysID.equals("OIDR"))){//住院医生站添加“时程”--xiongwg20150413
				TTable table = (TTable) this.getComponent("TABLE");
				table.setHeader("床号,60;姓名,80;病案号,100;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,140;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;预交金余额,120,double;临床路径,130,CLNCPATH_CODE;时程,100,SCHD_CODE;单病种,100,DISE_CODE;住院号,100;病案审核,100,MRO_CHAT_FLG;病历封存,140,SEALED_STATUS;服务等级,140,SERVICE_LEVELOUT;传筛,150;过敏,30;跌倒风险,80;日间手术,80,boolean");// 2017.0331 zhanglei 增加日间手术标记
				table.setEnHeader("BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;schdCode;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;SCHD_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;DAY_OPE_FLG;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");// 2017.0331 zhanglei 增加日间手术标记
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;17,left;18,left");
			}
			// ======================== chenxi modify 20130305
		} else {
			// 测试
			// this.setRunFlg("IBSPAYBILL");
			this.setRunFlg("ODI");
			// this.setRunFlg("INWCHECK");
			// this.setRunFlg("MRO");
			// this.setRunFlg("OIDR");
			// this.setRunFlg("NSSORDER");
			// this.setRunFlg("NSSCHAR");
		}
		// 初始化权限
		this.onInitPopeDem();

		/*modified by WangQing 20170428 start*/		
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.callFunction("UI|DEPT_CODE|setEnabled", false);
			this.callFunction("UI|STATION_CODE|setEnabled", true);
			this.callFunction("UI|VC_CODE|setEnabled", true);
		}
		isKeepWatch();
		/*modified by WangQing 20170428 end*/



		// 清空
		this.onClear();

		/*modified by WangQing 20170428 start*/
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.setValue("VC_CODE", "");// 经治医师
		}
		/*modified by WangQing 20170428 end*/

		this.setValue("STATION_CODE", Operator.getStation()); // ===CHENXI
		// MODIFY
		// 20130321
		// 各个子系统初始化
		this.initSystem();
		// 初始化查询
		this.onQuery();
		// 初始化SYS_FEE
		// this.initSysFeeData();
		// 设置标题
		String s = getConfigString(getRunFlg() + "_Title");
		if (s != null && s.length() > 0)
			this.setTitle(s);	

		// $$=====add by lx 2012/06/24 加入排序方法start============$$//
		addListener(getTTable("TABLE"));
		// $$=====add by lx 2012/06/24 加入排序方法end============$$//
		setMenu();


		//machao start 20170503 判断是否是从感染页面调入 感染跳入会诊
		if(this.getParameter() instanceof TParm){			
			if(!StringUtil.isNullString(((TParm)this.getParameter()).getValue("INF"))){				
				onQueryForMrNo();
			}			
		}
		//machao end 20170503
	}

	/**
	 * 入院路径改变事件
	 */
	public void onChangeValue(){
		if (StringUtils.equals("2", getTComboBox("tComboBox_0").getSelectedID())) {
			this.callFunction("UI|tComboBox_2|setEnabled",true);
		}	
		if(StringUtils.equals("1", getTComboBox("tComboBox_0").getSelectedID())){
			this.callFunction("UI|tComboBox_2|setEnabled", false);
			this.getTComboBox("tComboBox_2").setSelectedIndex(0);
		}
		if(StringUtils.equals("0", getTComboBox("tComboBox_0").getSelectedID())){
			this.callFunction("UI|tComboBox_2|setEnabled", false);
			this.getTComboBox("tComboBox_2").setSelectedIndex(0);
		}
	}


	/**
	 * 设置菜单
	 */
	public void setMenu(){




		if(this.getParameter().equals("INWCHECK")){
			TMenuItem printLis= (TMenuItem) this.getComponent("printLis");
			TMenuItem tnb= (TMenuItem) this.getComponent("tnb");
			TMenuItem tpr= (TMenuItem) this.getComponent("tpr");
			TMenuItem newtpr= (TMenuItem) this.getComponent("newtpr");
			//TMenuItem nis= (TMenuItem) this.getComponent("nis");
			printLis.setVisible(false);
			tnb.setVisible(false);
			tpr.setVisible(false);
			newtpr.setVisible(false);
			//nis.setVisible(false);
		}else if(this.getParameter().equals("INWEXE")){
			TMenuItem printLis= (TMenuItem) this.getComponent("printLis");
			TMenuItem tnb= (TMenuItem) this.getComponent("tnb");
			TMenuItem cxMrshow= (TMenuItem) this.getComponent("cxMrshow");
			printLis.setVisible(false);
			tnb.setVisible(false);
			cxMrshow.setVisible(false);
		}
	}

	/**
	 * 打印床头卡
	 */
	// 新添加caoyong 20130708
	public void onPrintO() {
		onTableMClicked();// 验证是否已选择要打印的病人
	}

	/**
	 * 验证是否已选择要打印的病人以及打印
	 */
	// 新添加caoyong 20130708
	public void onTableMClicked() {
		TTable table = ((TTable) this.getComponent(TABLE));
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = getTTable("TABLE").getParmValue().getRow(row);// 单击选中的行
			TParm date = new TParm();
			if (parm.getValue("DIE_CONDITION").length() != 0
					&& parm.getValue("DIE_CONDITION") != null) {// 判断是否有饮食
				String str = this.Diecondition(parm.getValue("DIE_CONDITION"))
						.getValue("CHN_DESC");
				date.setData("DIE_CONDITION", "TEXT", str);// 饮食
			}
			date.setData("BedNo", "TEXT", parm.getValue("BED_NO_DESC"));// 床 号
			date.setData("Dept", "TEXT", this.Dept(parm.getValue("DEPT_CODE"))
					.getValue("DEPT_CHN_DESC"));// 得到科室名字
			date.setData("Name", "TEXT", parm.getValue("PAT_NAME"));// 名字
			date.setData("Age", "TEXT", parm.getValue("AGE"));// 年龄
			date.setData("Gender", "TEXT", "1"
					.equals(parm.getValue("SEX_CODE")) ? "男" : "女");// 性别转化为汉字
			date.setData("Admission Date", "TEXT", parm.getValue("IN_DATE")
					.substring(0, 13)
					+ "时");// 入院日期
			date.setData("Latest Diagnosis", "TEXT", parm.getValue("MAINDIAG"));// 诊断
			date.setData("Nurse Level", "TEXT", Nursing(parm
					.getValue("NURSING_CLASS")));// 护理等级转化为汉字
			date.setData("MrNo", "TEXT", parm.getValue("MR_NO"));// 病案号
			date.setData("ALLERGY_NOTE", "TEXT", this.Allergie(
					parm.getValue("MR_NO")).getValue("ALLERGY_NOTE"));// 过敏史
			this.openPrintWindow("%ROOT%\\config\\prt\\ODI\\Odi_Patinfo.jhw",
					date);
		} else {
			this.messageBox("请选择您所要打印的病人");
		}
	}

	/**
	 * 护理级别汉字转化
	 */
	// 新添加caoyong 20130708
	public String Nursing(String type) {
		String str = "";
		if ("N0".equals(type)) {
			str = "特级护理";
		}
		if ("N1".equals(type)) {
			str = "一级护理";
		}
		if ("N2".equals(type)) {
			str = "二级护理";
		}
		if ("N3".equals(type)) {
			str = "三级护理";
		}
		return str;
	}

	/**
	 * 取得科室汉字
	 * 
	 * @param dept
	 * @return
	 */
	// 新添加caoyong 20130708
	public TParm Dept(String dept) {
		String sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
				+ dept + "'";
		TParm caseParm = new TParm(this.getDBTool().select(sql));
		return caseParm.getRow(0);
	}

	/**
	 * 取得过敏值
	 */
	// 新添加caoyong 20130708
	public TParm Allergie(String mrno) {
		String sql = "SELECT ALLERGY_NOTE FROM OPD_DRUGALLERGY WHERE MR_NO='"
				+ mrno + "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		return parm.getRow(0);
	}

	/**
	 * 得到饮食
	 */
	// 新添加caoyong 20130708
	public TParm Diecondition(String die) {
		String sql = "select CHN_DESC from SYS_DICTIONARY where GROUP_ID='SYS_DIE_CONDITION' and id='"
				+ die + "'";
		TParm rparm = new TParm(this.getDBTool().select(sql));
		return rparm.getRow(0);
	}

	/**
	 * 单击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableClicke(int row) {
		if (row < 0)
			return;
		TParm parm = getTTable("TABLE").getParmValue().getRow(row);
		if(parm!= null){
			this.setValue("SCHD_CODE", parm.getValue("SCHD_CODE"));
		}
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// 病区
			TextFormatSYSOperatorStation h = (TextFormatSYSOperatorStation) this
					.getComponent("INW_STATION_CODE");
			h.setUserID(Operator.getID());
			h.onQuery();
			// 科室
			TextFormatSYSDeptForOprt d = (TextFormatSYSDeptForOprt) this
					.getComponent("INW_DEPT_CODE");
			d.setStationCode(this.getValueString("INW_STATION_CODE"));
			d.onQuery();
			// 经治医师
			TextFormatSYSOperator k = (TextFormatSYSOperator) this
					.getComponent("INW_VC_CODE");
			k.setDept(this.getValueString("INW_DEPT_CODE"));
			k.onQuery();
			// 出院病区
			TextFormatSYSStation h1 = (TextFormatSYSStation) this
					.getComponent("STATION_CODEOUT");
			h1.setDeptCode(this.getValueString("DEPT_CODEOUT"));
			h1.onQuery();
			// 出院经治医师
			TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
					.getComponent("VC_CODEOUT");
			k1.setDept(this.getValueString("DEPT_CODEOUT"));
			k1.onQuery();
			this.setValue("INW_STATION_CODE", parm.getValue("STATION_CODE"));
			this.setValue("INW_DEPT_CODE", parm.getValue("DEPT_CODE"));
			this.setValue("INW_VC_CODE", parm.getValue("VS_DR_CODE"));
		} else {
			// 病区
			TextFormatSYSStation h = (TextFormatSYSStation) this
					.getComponent("STATION_CODE");
			h.setDeptCode(this.getValueString("DEPT_CODE"));
			h.onQuery();
			// 经治医师
			TextFormatSYSOperator k = (TextFormatSYSOperator) this
					.getComponent("VC_CODE");
			k.setDept(this.getValueString("DEPT_CODE"));
			k.onQuery();
			// 出院病区
			TextFormatSYSStation h1 = (TextFormatSYSStation) this
					.getComponent("STATION_CODEOUT");
			h1.setDeptCode(this.getValueString("DEPT_CODEOUT"));
			h1.onQuery();
			// 出院经治医师
			TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
					.getComponent("VC_CODEOUT");
			k1.setDept(this.getValueString("DEPT_CODEOUT"));
			k1.onQuery();
		}
	}

	public void initSystem() {
		// 医生站初始化
		if (this.getRunFlg().equals("ODI")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|nis|setVisible", true);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// 呼叫Socket计费
		if (this.getRunFlg().equals("IBS")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// 临床路径
		if (this.getRunFlg().equals("CLPMANAGEM")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// 会诊申请 add caoyong 20130922
		if (this.getRunFlg().equals("INPAPPLACATION")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// 越级审批抗菌药物  yanjing 20140219
		if (this.getRunFlg().equals("OVERCHECK")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// 呼叫Socket护士站
		if (this.getRunFlg().equals("INWCHECK")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
			// this.messageBox("===INWCHECK===");
			// 护士相关操作， 可打开床头卡
			this.getTMenuItem("card").setEnabled(true);
			// shibl add
			String userId = Operator.getID();
			this.setValue("USER_ID", userId);
			this.setValue("INW_STATION_CODE", Operator.getStation());
			this.setValue("INW_DEPT_CODE", "");
			callFunction("UI|INW_VC_CODE|onQuery");
			callFunction("UI|INW_STATION_CODE|onQuery");
			callFunction("UI|INW_DEPT_CODE|onQuery");
			((TTextFormat) getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) getComponent("VC_CODE")).setVisible(false);
			((TTextFormat) getComponent("INW_DEPT_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_STATION_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_VC_CODE")).setVisible(true);
			// 打开LEDUI
			openLEDUI();
		}
		if (this.getRunFlg().equals("INWEXE")) {
			this.callFunction("UI|tpr|setVisible", true);
			this.callFunction("UI|newtpr|setVisible", true);
			this.callFunction("UI|create|setVisible", true);
			this.callFunction("UI|transfer|setVisible", true);
			String userId = Operator.getID();
			this.setValue("USER_ID", userId);
			this.setValue("INW_STATION_CODE", Operator.getStation());
			this.setValue("INW_DEPT_CODE", "");
			callFunction("UI|INW_VC_CODE|onQuery");
			callFunction("UI|INW_STATION_CODE|onQuery");
			callFunction("UI|INW_DEPT_CODE|onQuery");
			// shibl add
			((TTextFormat) getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) getComponent("VC_CODE")).setVisible(false);
			((TTextFormat) getComponent("INW_DEPT_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_STATION_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_VC_CODE")).setVisible(true);
			// 护士相关操作， 可打开床头卡
			this.getTMenuItem("card").setEnabled(true);
			// modify shibl 20120317
			openLEDOneUI();
			ett.doJob();
		}

		/**
		 * 护士站医嘱单打印
		 */
		if (this.getRunFlg().equals("SHEET")) {
			// 护士相关操作， 可打开床头卡
			// this.getTMenuItem("bedcard").setEnabled(true);
			String userId = Operator.getID();
			this.setValue("USER_ID", userId);
			this.setValue("INW_STATION_CODE", Operator.getStation());
			this.setValue("INW_DEPT_CODE", "");
			callFunction("UI|INW_VC_CODE|onQuery");
			callFunction("UI|INW_STATION_CODE|onQuery");
			callFunction("UI|INW_DEPT_CODE|onQuery");
			// shibl add
			((TTextFormat) getComponent("DEPT_CODE")).setVisible(false);
			((TTextFormat) getComponent("STATION_CODE")).setVisible(false);
			((TTextFormat) getComponent("VC_CODE")).setVisible(false);
			((TTextFormat) getComponent("INW_DEPT_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_STATION_CODE")).setVisible(true);
			((TTextFormat) getComponent("INW_VC_CODE")).setVisible(true);
		}
		// 住院处病案首页
		if (this.getRunFlg().equals("ADMMRO")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.setValue("DEPT_CODE", "");
			this.setValue("STATION_CODE", "");
			this.setValue("VC_CODE", "");
		}
	}

	/**
	 * 初始化SYS_FEE
	 */
	public void initSysFeeData() {
		if ("ODI".equals(this.getRunFlg())) {
			SwingUtilities.invokeLater(new Runnable() {
				public void run() {
					try {
						// Thread.sleep(100);
						TIOM_Database.getLocalTable("SYS_FEE");
						TIOM_Database.getLocalTable("SYS_DIAGNOSIS");
					} catch (Exception e) {
					}
				}
			});
		}
	}

	/**
	 * 开打LEDUI
	 */
	public void openLEDUI() {
		Component com = (Component) this.getComponent();
		TParm parm = new TParm();
		parm.setData("STATION_CODE", this.getValueString("INW_STATION_CODE"));
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while (com != null && !(com instanceof Frame))
			com = com.getParent();
		ledUi = new LEDUI((Frame) com, this, parm);
		ledUi.openWindow();
	}

	/**
	 * 开打LEDUI
	 */
	public void openLEDOneUI() {
		Component com = (Component) this.getComponent();
		TParm parm = new TParm();
		parm.setData("STATION_CODE", this.getValueString("INW_STATION_CODE"));
		parm.addListener("onSelStation", this, "onSelStationListenerLed");
		while (com != null && !(com instanceof Frame))
			com = com.getParent();
		ledUi1 = new LEDEXECUI((Frame) com, this, parm, true);
		ledUi1.openWindow();
	}

	/**
	 * 拿到病区
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onSelStationListenerLed(TParm parm) {
		// System.out.println("parm"+parm);
		this.ledParm = parm;
	}

	/**
	 * 病区选择事件
	 */
	public void onSel() {
		if (this.getValueString("INW_STATION_CODE").length() != 0)
			this.ledParm.runListener("onListenerLed", this
					.getValueString("INW_STATION_CODE"));
	}

	/**
	 * 开打护士站通讯窗口
	 * 
	 * @param parm
	 *            TParm
	 */
	public void openInwCheckWindow(TParm parm) {
		// 调用护士站选择病患页面
		Object obj = this.openDialog("%ROOT%\\config\\odi\\PatInfoUI.x", parm);
		if (obj != null) {
			TParm action = (TParm) obj;
			//action.setData("LEDUI", ledUi);
			// 调用护士审核界面
			//this.runPaneSocketInwCheck("INWSTATIONCHECK",
			//"inw\\INWOrderCheckMain.x", action);
			ledUi.removeMessage(action);
			// modify shibl 20120317
			if (null != action.getValue("FLG")
					|| action.getValue("FLG").equals("Y")) {// 判断护士站执行操作
				action.setData("LEDUI", ledUi1);
				// System.out.println("action::::"+action);
				// 调用护士执行界面
				this.runPaneSocketInwExe("INWSTATIONEXECUTE",
						"inw\\INWOrderExecMain.x", action);
			} else {
				action.setData("LEDUI", ledUi);
				// 调用护士审核界面
				this.runPaneSocketInwCheck("INWSTATIONCHECK",
						"inw\\INWOrderCheckMain.x", action);
			}

		}
	}

	/**
	 * 开打护士站通讯窗口
	 * 
	 * @param parm
	 *            TParm
	 */
	public void openInwExecWindow(TParm parm) {
		// 调用护士站选择病患页面
		TParm p = new TParm();
		parm.setData("STATUS", "EXEC");
		this.openDialog(
				"%ROOT%\\config\\inw\\INWOrderSingleExecQuery.x",p);
	}

	/**
	 * 护士站执行得到病患详细数据
	 * 
	 * @param tag
	 *            String
	 * @param path
	 *            String
	 * @param parm
	 *            TParm =================pangben 2011-11-10
	 */
	public void runPaneSocketInwExe(String tag, String path, TParm parm) {

		// this.messageBox("runPaneSocketInwExe");
		// 关闭当前工作页面
		// onClosePanel();
		// 得到选中行数据
		this.setValue("MR_NO", parm.getValue("MR_NO"));
		this.setValue("PAT_NAME", parm.getValue("PAT_NAME"));
		this.setValue("IPD_NO", parm.getValue("IPD_NO"));
		this.setValue("CASE_NO", parm.getValue("CASE_NO"));
		TParm actionParm = ExeQuery().getRow(0);
		// System.out.println("actionParm:::"+actionParm);
		TParm action = new TParm();
		action.setData("CASE_NO", parm.getData("CASE_NO"));

		action.setData("STATION_CODE", parm.getData("STATION_CODE"));
		action.setData("POPEDEM", this.isInwPopedem());
		this.setValue("INW_STATION_CODE", parm.getValue("STATION_CODE"));
		// 病区
		TextFormatSYSOperatorStation h = (TextFormatSYSOperatorStation) this
				.getComponent("INW_STATION_CODE");
		h.setUserID(Operator.getID());
		h.onQuery();
		// 科室
		TextFormatSYSDeptForOprt d = (TextFormatSYSDeptForOprt) this
				.getComponent("INW_DEPT_CODE");
		d.setStationCode(this.getValueString("INW_STATION_CODE"));
		d.onQuery();
		// 经治医师
		TextFormatSYSOperator k = (TextFormatSYSOperator) this
				.getComponent("INW_VC_CODE");
		k.setDept(this.getValueString("INW_DEPT_CODE"));
		k.onQuery();
		// 出院病区
		TextFormatSYSStation h1 = (TextFormatSYSStation) this
				.getComponent("STATION_CODEOUT");
		h1.setDeptCode(actionParm.getValue("DEPT_CODE"));
		h1.onQuery();
		// 出院经治医师
		TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
				.getComponent("VC_CODEOUT");
		k1.setDept(actionParm.getValue("DEPT_CODE"));
		k1.onQuery();

		// 身份1
		action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
		// 身份2
		action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
		// 身份3
		action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
		// 姓名
		action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
		// 科室
		action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
		// 住院号
		action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
		// 病案号
		action.setData("INW", "MR_NO", this.getValue("MR_NO"));
		// 入院时间
		action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
		// 保存权限注记
		action.setData("INW", "SAVE_FLG", true);
		action.setData("INW", "LEDUI", parm.getData("LEDUI"));
		this.setValue("BED_NO", actionParm.getValue("BED_NO_DESC"));

		this.setValue("SEX", actionParm.getValue("SEX_CODE"));
		this.setValue("SERVICE_LEVELIN", actionParm.getValue("SERVICE_LEVEL"));
		this.setValue("ADM_DATE", actionParm.getTimestamp("IN_DATE"));
		this.setValue("TOTAL_AMT", actionParm.getDouble("TOTAL_AMT"));
		this.setValue("INW_VC_CODE", actionParm.getValue("VS_DR_CODE"));

		onClosePanel();
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		tabPane.setEnabled(false);
		getTPanel().addItem(tag, "%ROOT%\\config\\" + path, action, false);
		((TTable) this.getComponent(TABLE)).setVisible(false);
		workPanelTag = tag;
	}

	private TParm ExeQuery() {
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态
		TParm actionParm = null;
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		if (selType == 0) {
			// 得到在院查询的参数
			TParm queryData = this.getQueryData("IN");
			// out("得到在院查询的参数"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")) {
				// 得到查询SQL
				String sqlStr = this.creatInwQuerySQL(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// 判断是否是值班医生
				boolean stationFlg = isKeepWatch();
				// 得到查询SQL
				String sqlStr = createODIQuerySQL(queryData, "IN", stationFlg);
				// System.out.println("查询sql:" + sqlStr);
				if (stationFlg) {
					// 查询在院病患基本信息
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				}
			}
			// out("在院查询后的数据"+actionParm);
			// //如果查询为空返回
			// if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// //清空Table
			// callFunction("UI|" + TABLE + "|removeRowAll");
			// return null;
			// }
			// //得到工作页签的TAG并联动查询
			// if (workPanelTag.length() != 0) {
			// this.queryDataOtherTPane(actionParm, "IN");
			// return null;
			// }
			// //设置TABLE上的数据
			// this.setTableData(actionParm, "IN");
		} else {
			// 得到出院查询的参数
			TParm queryData = this.getQueryData("OUT");
			// 判断是否是值班医生
			boolean stationFlg = isKeepWatch();
			// 得到查询SQL
			String sqlStr = createODIQuerySQL(queryData, "OUT", stationFlg);
			// System.out.println("出院查询SQL：" + sqlStr);
			if (stationFlg) {
				// 查询在院病患基本信息
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			}
			// out("在院查询后的数据" + actionParm);
			// //如果查询为空返回
			// if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// //清空Table
			// callFunction("UI|" + TABLE + "|removeRowAll");
			// return;
			// }
			// //得到工作页签的TAG并联动查询
			// if (workPanelTag.length() != 0) {
			// this.queryDataOtherTPane(actionParm, "OUT");
			// return;
			// }
			// //设置TABLE上的数据
			// this.setTableData(actionParm, "OUT");
		}
		return actionParm;
	}

	/**
	 * 初始化权限
	 */
	public void onInitPopeDem() {
		// 权限可否选择科室
		if (!this.getPopedem("deptEnabled") && !this.getRunFlg().equals("OIDR")) {
			this.callFunction("UI|DEPT_CODE|setEnabled", false);
			this.callFunction("UI|DEPT_CODEOUT|setEnabled", false);
		}
		// 权限可否选择病区
		if (!this.getPopedem("stationEnabled")
				&& !this.getRunFlg().equals("OIDR")) {
			this.callFunction("UI|STATION_CODE|setEnabled", false);
			this.callFunction("UI|STATION_CODEOUT|setEnabled", false);
		}
		// 选择护士站COMBO传给护士站
		this.setInwPopedem(this.getPopedem("InwCheckEnabled"));
	}

	/**
	 * 双击事件
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubled(int row) {
		if (row < 0)
			return;
		//住院医生站双击病患前增加登陆科室为空的校验---xiongwg20150624
		if ("ODI".equals(this.getRunFlg()) && Operator.getDept().isEmpty()) {
			this.messageBox("登陆科室为空，请重新登陆");
			return;
		}
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		TParm parm = this.getSelectRowData(TABLE);
		if (selType == 0) {
			if (parm.getData("IN_DATE") == null
					&& parm.getValue("CASE_NO").length() == 0) {
				// 病患没有住院信息！
				// this.messageBox("E0151");
				return;
			}
			// 拿到预交金余额如果不足给提示不强制
			double rPrice = parm.getDouble("CUR_AMT");
			// 黄色警戒
			double yellowPrice = parm.getDouble("YELLOW_SIGN");
			//machao start 20170503 判断是否是从感染页面调入 感染跳入会诊
			Boolean flg =true;
			if(this.getParameter() instanceof TParm){
				if(!StringUtil.isNullString(((TParm)this.getParameter()).getValue("INF"))){
					flg = false;
				}	
			}

			//machao end 20170503

			//			// 红色警戒  yanmm 20170711
			//			if ("ODI".equals(this.getRunFlg())) {	
			//			String sql = "SELECT UNLOCKED_FLG FROM ADM_INP WHERE CASE_NO ='"+parm.getValue("CASE_NO")+"' ";
			//			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			//			//取红黄警戒线
			//			String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
			//			TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));
			//			if(result.getValue("UNLOCKED_FLG",0).equals("Y")){
			//
			//			}else{
			//				if((rPrice <= resultSign.getDouble("RED_SIGN",0) 
			//						|| parm.getValue("STOP_BILL_FLG").equals("Y")) && flg){
			//					this.messageBox("该病患预交金余额不足!\n 已被锁定!");
			//					String sqlSBF = "UPDATE ADM_INP SET STOP_BILL_FLG='Y',UNLOCKED_FLG='N' "
			//							+ "WHERE CASE_NO='" +parm.getValue("CASE_NO")+ "' ";
			//					TJDODBTool.getInstance().update(sqlSBF);
			//					return;
			//				}
			//			}
			//		}
			//
			if (rPrice <= yellowPrice && flg) {
				if (this.messageBox("提示信息 Tips",
						"预交金余额不足！\n Paying insufficient balance gold!",
						this.YES_NO_OPTION) != 0)
					return;
			}
		}
		// 临床路径验证begin
		if (this.getRunFlg().equals("CLPCHECKITEMMAIN")
				|| this.getRunFlg().equals("CLPVARIATION")) {
			if (parm.getValue("CLNCPATH_CODE") == null
					|| "".equals(parm.getValue("CLNCPATH_CODE"))) {
				this.messageBox("该病患没有进入临床路径，请重新选择！");
				return;
			}
		}
		// 出院
		if (selType == 1) {
			if (this.getRunFlg().equals("CLPMANAGEM")) {
				this.messageBox("该病患已经出院不能进入临床路径，请重新选择！");
				return;
			}
		}
		// 临床路径验证end
		this.initOtherUi();
	}


	public void initOtherUi() {
		// 医生站
		if ("ODI".equals(this.getRunFlg())) {
			if ("en".equals(this.getLanguage())) {
				this.setTitle("IP Station");
			} else {
				this.setTitle("住院医生站");
			}
			// 判断是否加锁
			if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
				if (this.messageBox("是否解锁 Whether to unlock", PatTool
						.getInstance().getLockParmString(
								this.getValueString("MR_NO")), 0) == 0) {
					PatTool.getInstance().unLockPat(
							this.getValueString("MR_NO"));
					PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
							"ODI");

				} else {
					return;
				}
			} else {
				// 加锁
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");
			}
			passcheck();
			//===========pangben 2015-8-15 添加临床路径管控
			TParm parm = new TParm();
			if (!getClpOdiStationParm(parm)) {
				return;
			}
			//转科后临床路径病患，医生站操作需要修改时程
			CLPTool.getInstance().intoClpDuration(parm, this);
			//临床路径溢出校验：1.超过天数校验 
			CLPTool.getInstance().onCheckClpOverflow(parm, this);
			// $$==== modified by lx 2012/03/19加入1024*768医生站处理 start====$$//
			if (this.getSrceenWidth() != null
					&& this.getSrceenWidth().equals("1024")) {
				// System.out.println("is 1024.");
				this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");
			} else {
				this.runPane("STATIONMAIN", "odi\\ODIStationUI.x");
			}
			// $$==== modified by lx 2012/03/19加入1024*768医生站处理 end====$$//

		}
		// 医生站
		if ("OIDR".equals(this.getRunFlg())) {
			// $$==== modified by lx 2012/03/19加入1024*768会诊处理 start====$$//
			if (this.getSrceenWidth() != null
					&& this.getSrceenWidth().equals("1024")) {
				if ("en".equals(this.getLanguage())) {
					this.setTitle("IP Station");
				} else {
					this.setTitle("会诊");
				}
				this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");

			} else {
				if ("en".equals(this.getLanguage())) {
					this.setTitle("IP Station");
				} else {
					this.setTitle("会诊");
				}
				this.runPane("STATIONMAIN", "odi\\ODIStationUI.x");
			}
			// $$==== modified by lx 2012/03/19加入1024*768会诊处理 end====$$//
		}

		/*modified by WangQing 20170428 start*/
		// 教学医生站
		if ("T".equals(this.getRunFlg())) {
			this.setTitle("教学医生站");
			this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");
		}
		/*modified by WangQing 20170428 end*/

		// 护士站审核
		if ("INWCHECK".equals(this.getRunFlg())) {
			this.setTitle("住院护士站审核");
			this.runPane("INWSTATIONCHECK", "inw\\INWOrderCheckMain.x");
		}
		// 护士执行
		if ("INWEXE".equals(this.getRunFlg())) {
			this.setTitle("住院护士站执行");

			this.runPane("INWSTATIONEXECUTE", "inw\\INWOrderExecMain.x");
		}
		// 住院计价
		if ("IBS".equals(this.getRunFlg())) {
			this.setTitle("住院计价");
			this.runPane("IBSSTATION", "ibs\\IBSOrderm.x");
		}
		// 费用查询
		if ("IBSQUERYFEE".equals(this.getRunFlg())) {
			this.setTitle("费用查询");
			this.runPane("IBSQUERYFEESTATION", "ibs\\IBSSelOrderm.x");
		}
		// 缴费作业
		if ("IBSPAYBILL".equals(this.getRunFlg())) {
			this.setTitle("缴费作业");
			if (!checkNo()) {
				// 尚未开账请先开账!
				this.messageBox("E0014");
				return;
			}
			this.runPane("IBSCUTBILLSTATION", "bil\\BilIBSRecp.x");
		}
		// 病案管理
		if ("MRO".equals(this.getRunFlg())) {
			this.setTitle("病案管理");
			this.runPane("MROSTATION", "mro\\MRO_Chrtvetrec.x");
		}
		// 病患动态查询
		if ("ADMCHG".equals(this.getRunFlg())) {
			this.setTitle("病患动态查询");
			this.runPane("ADMCHGSTATION", "adm\\ADMQueryChgLog.x");
		}
		// 医嘱单打印
		if ("SHEET".equals(this.getRunFlg())) {
			this.setTitle("医嘱单打印");
			this.runPane("INWSHEET", "inw\\INWOrderSheetPrtAndPreView.x");
		}
		// 订餐
		if ("NSSORDER".equals(this.getRunFlg())) {
			this.setTitle("订餐");
			this.runPane("NSSORDER", "nss\\NSSOrder.x");
		}
		// 订餐缴费
		if ("NSSCHAR".equals(this.getRunFlg())) {
			this.setTitle("订餐缴费");
			this.runPane("NSSCHARGE", "nss\\NSSCharge.x");
		}

		// 临床路径准进准出
		if ("CLPMANAGEM".equals(this.getRunFlg())) {
			// System.out.println("进入临床路径准进准出");
			this.setTitle("临床路径准进准出");
			this.runPane("CLPMANAGEM", "clp\\CLPManagem.x");
		}
		// 临床路径变异分析
		if ("CLPVARIATION".equals(this.getRunFlg())) {
			// System.out.println("进入临床路径变异分析");
			this.setTitle("临床路径变异分析");
			this.runPane("CLPVARIATION", "clp\\CLPVariation.x");
		}
		// 临床路径关键诊疗项目执行
		if ("CLPCHECKITEMMAIN".equals(this.getRunFlg())) {
			// System.out.println("临床路径关键诊疗项目执行");
			this.setTitle("临床路径关键诊疗项目执行");
			this.runPane("CLPCHECKITEMMAIN", "clp\\CLPChkItemMain.x");
		}
		// 转移记录维护
		if ("TRANSHOSPLOG".equals(this.getRunFlg())) {
			// System.out.println("临床路径关键诊疗项目执行");
			this.setTitle("转移记录维护");
			this.runPane("TRANSHOSPLOG", "adm\\ADM_TRANS_LOGUI.x");
		}
		// 加入预交金
		if ("BILPAY".equals(this.getRunFlg())) {
			this.setTitle("预交金");
			this.runPane("BILPAY", "bil\\BILPay.x");
		}
		// 住院处病案首页
		if ("ADMMRO".equals(this.getRunFlg())) {
			int row = this.getTTable(TABLE).getSelectedRow();
			TParm parm = this.getTTable(TABLE).getParmValue().getRow(row);
			TParm pubParm = new TParm();
			pubParm.setData("SYSTEM_CODE", "ADM");
			pubParm.setData("MR_NO", parm.getValue("MR_NO"));
			pubParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			// 住院处调用(USER_TYPE=1)
			pubParm.setData("USER_TYPE", "1");
			pubParm.setData("OPEN_USER", Operator.getID());
			TParm result = (TParm) this.openDialog(
					"%ROOT%\\config\\mro\\MRORecord.x", pubParm);
		}
		// //出入转管理
		// if("ADMTRAN".equals(this.getRunFlg())){
		// this.runPane("ADMTRANSTATION","adm\\ADMWaitTrans.x");
		// }

		// 会诊申请 add caoyong 20130922
		if ("INPAPPLACATION".equals(this.getRunFlg())) {
			/*
			 * int row=this.getTTable(TABLE).getSelectedRow(); TParm
			 * parm=this.getTTable(TABLE).getParmValue().getRow(row); TParm
			 * pubParm = new TParm();
			 * pubParm.setData("CASE_NO",parm.getValue("CASE_NO"));
			 * System.out.println
			 * ("nnnnnnnnnnnnnnnn=="+parm.getValue("CASE_NO"));
			 */
			this.setTitle("会诊申请");
			// this.runPane("INPAPPLACATION", "inp\\INPConsApplication.x");
			this.runPane("INPAPPLACATION", "inp\\INPConsApplication.x");
		}
		// 越级审批抗菌药物    yanjing  20140219
		if ("OVERCHECK".equals(this.getRunFlg())) {
			int row = this.getTTable(TABLE).getSelectedRow();
			TParm parm = this.getTTable(TABLE).getParmValue().getRow(row);
			TParm pubParm1 = new TParm();
			pubParm1.setData("CASE_NO", parm.getValue("CASE_NO"));
			this.setTitle("越级审批抗菌药物");
			this.runPane("OVERCHECK", "odi\\ODIOverRideCheck.x");
		}
	}

	public boolean checkNo() {
		TParm parm = new TParm();
		parm.setData("RECP_TYPE", "IBS");
		parm.setData("CASHIER_CODE", Operator.getID());
		parm.setData("STATUS", "0");
		parm.setData("TERM_IP", Operator.getIP());
		TParm noParm = BILInvoiceTool.getInstance().selectNowReceipt(parm);
		String updateNo = noParm.getValue("UPDATE_NO", 0);
		if (updateNo == null || updateNo.length() == 0) {
			return false;
		}
		return true;
	}

	// $$==============add by lx 2012/02/07加入床头卡功能==========================$$//
	/**
	 * 床头卡
	 */
	public void onBedCard() {
		TTable table = ((TTable) this.getComponent(TABLE));

		// table.clearSelection();
		TParm parm = new TParm();
		parm = (TParm) this.openDialog("%ROOT%\\config\\inw\\INWBedCard.x");
		// this.messageBox(""+parm);

		if (parm != null) {
			this.onClear();
			this.setValue("INW_VC_CODE", "");
			// this.messageBox("case no"+parm.getValue("CASE_NO"));
			//
			// String strCaseNo=parm.getValue("CASE_NO");
			String strIPDNo = parm.getValue("IPD_NO");
			this.setValue("INW_DEPT_CODE", "");
			this.setValue("INW_STATION_CODE", parm.getValue("STATION_CODE"));
			this.setValue("MR_NO", parm.getValue("MR_NO"));
			this.setValue("IPD_NO", strIPDNo);
			// this.getTCheckBox("ALLO_FLG").setSelected(false);
			this.onQuery();
			// 遍历表格记录，取行数；
			// 存在
			int currentRow = 0;
			table.setSelectedRow(currentRow);
			onTableClicke(currentRow);
			onTableDoubled(currentRow);
			//
		}

	}

	// $$==============add by lx
	// 2012/02/07加入床头卡功能END==========================$$//

	/**
	 * 病患信息
	 */
	public void onPatInfo() {

		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		this.openDialog("%ROOT%\\config\\adm\\AdmPatinfo.x", parm);

	}

	/**
	 * TTablePanel切换事件
	 */
	public void onChangedPanel() {
		// this.messageBox("==onChangedPanel==");
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// 清空Table
		callFunction("UI|" + TABLE + "|removeRowAll");
		// SHIBL 20120425 MODIFY
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// 设置科室(出院)
			this.setValue("INW_DEPT_CODE", "");
			// 注解 待Operator完善在给默认值(设置病区在院)
			this.setValue("INW_STATION_CODE", Operator.getStation());
			// 经治医师（护士站）
			this.setValue("INW_VC_CODE", "");
			// 注解 待Operator完善在给默认值(设置病区出院)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		} else {
			// 设置科室(在院)
			this.setValue("DEPT_CODE", Operator.getDept());
			// 设置科室(出院)
			this.setValue("DEPT_CODEOUT", Operator.getDept());
			// 注解 待Operator完善在给默认值(设置病区在院)
			this.setValue("STATION_CODE", Operator.getStation());
			// 注解 待Operator完善在给默认值(设置病区出院)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		}
		// 默认设置在院页签的住院日期
		this.setValue("ADM_DATE", sysDate);
		// 默认设置起始日期
		this.setValue("START_DATE", sysDate);
		// 默认设置终止日期
		this.setValue("END_DATE", StringTool.rollDate(sysDate, 1));
		// 默认设置起始日期
		this.setValue("START_DATEOUT", StringTool.rollDate(sysDate, -7));
		// 默认设置终止日期
		this.setValue("END_DATEOUT", StringTool.rollDate(sysDate, 1));
		// 默认设置出院页签的入院起迄
		// this.setValue("ADM_DATEOUT", SystemTool.getInstance().getDate());
		// 设置人数可编辑状态(在院)
		callFunction("UI|PRESON_NUM|setEnabled", false);
		// 设置人数可编辑状态(出院)
		callFunction("UI|PRESON_NUMOUT|setEnabled", false);
		// 清空相关控件信息(在院)
		this
		.clearValue("BED_NO;IPD_NO;MR_NO;PAT_NAME;SEX;SERVICE_LEVELIN;WEIGHT;TOTAL_AMT;PAY_INS;YJJ_PRICE;GREED_PRICE;YJYE_PRICE;PRESON_NUM");
		// 清空相关控件信息(出院)
		this
		.clearValue("BILL_STATUS;IPD_NOOUT;MR_NOOUT;PAT_NAMEOUT;PERSON_NUMOUT;SERVICE_LEVELOUT");
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态
		int selType = tabPane.getSelectedIndex();
		switch (selType) {
		case 0:// TABLE在院
			// modified by WangQing 20170509 已经出院的病人不能使用出院通知
			callFunction("UI|hos|setEnabled", true);

			if ("IBSPAYBILL".equals(this.getRunFlg())) {// 缴费作业表格添加“付款方式二”--xiongwg20150413
				this
				.getTTable(TABLE)
				.setHeader(
						"床号,60;姓名,80;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,120;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;付款方式二,80,CTZ_CODE;预交金余额,120,double;临床路径,100,CLNCPATH_CODE;单病种,100,DISE_CODE;病案号,100;住院号,100;病案审核,100,MRO_CHAT_FLG;服务等级,140,SERVICE_LEVELOUT");
				this
				.getTTable(TABLE)
				.setEnHeader(
						"BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				this
				.getTTable(TABLE)
				.setParmMap(
						"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CTZ2_CODE;CUR_AMT;CLNCPATH_CODE;DISE_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				this
				.getTTable(TABLE)
				.setColumnHorizontalAlignmentData(
						"0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;15,right;16,right;17,left;18,left;");
			} else if ("ODI".equals(this.getRunFlg())) {
				this
				.getTTable(TABLE)
				.setHeader(
						"床号,60;姓名,80;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,140;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;预交金余额,120,double;临床路径,130,CLNCPATH_CODE;时程,100,SCHD_CODE;单病种,100,DISE_CODE;病案号,100;住院号,100;病案审核,100,MRO_CHAT_FLG;病历封存,140,SEALED_STATUS;服务等级,140,SERVICE_LEVELOUT;传筛,150;过敏,30;跌倒风险,80");
				this
				.getTTable(TABLE)
				.setEnHeader(
						"BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;schdCode;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				this
				.getTTable(TABLE)
				.setParmMap(
						"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;SCHD_CODE;DISE_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				this
				.getTTable(TABLE)
				.setColumnHorizontalAlignmentData(
						"0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,right;12,left;13,left;14,left;15,right;16,right;17,left;18,left;18,left;19,left;20,left");

			} else {
				// this.getTTable(TABLE)
				// .setHeader(
				// "床号,80;姓名,80;性别,60,SEX;年龄,80;入院日期,120,Timestamp;住院天数,80;经治医师,100,USER_ID;最新诊断,120;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;预交金余额,120,double;临床路径,100,CLNCPATH_CODE;病案号,100;住院号,100;病案审核,100,MRO_CHAT_FLG;服务等级,140,SERVICE_LEVELOUT");
				// this.getTTable(TABLE)
				// .setEnHeader(
				// "BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;MrNo;IpdNo;Mr Check;Service Level");
				// this.getTTable(TABLE)
				// .setParmMap(
				// "BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				// this.getTTable(TABLE)
				// .setColumnHorizontalAlignmentData(
				// "0,Left;1,Left;2,Left;3,Left;4,Left;5,Right;6,Left;7,Left;8,Left;9,left;10,left;11,Right;12,Right;13,right;14,right;15,left;16,left;");
				this
				.getTTable(TABLE)
				.setHeader(
						"床号,60;姓名,80;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,120;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;预交金余额,120,double;临床路径,100,CLNCPATH_CODE;单病种,100,DISE_CODE;病案号,100;住院号,100;病案审核,100,MRO_CHAT_FLG;服务等级,140,SERVICE_LEVELOUT;传筛,150;过敏,30;跌倒风险,80");
				this
				.getTTable(TABLE)
				.setEnHeader(
						"BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				this
				.getTTable(TABLE)
				.setParmMap(
						"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;DISE_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				this
				.getTTable(TABLE)
				.setColumnHorizontalAlignmentData(
						"0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,right;12,left;13,left;14,right;15,right;16,left;17,left;;18,left;19,left;20,left");
			}
			/*modified by WangQing 20170516 start*/
			if ("T".equals(this.getRunFlg())) {
				TTable table = (TTable) this.getComponent("TABLE");
				table.setHeader("床号,60;姓名,80;病案号,100;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,140;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;预交金余额,120,double;临床路径,130,CLNCPATH_CODE;时程,100,SCHD_CODE;单病种,100,DISE_CODE;住院号,100;病案审核,100,MRO_CHAT_FLG;病历封存,140,SEALED_STATUS;服务等级,140,SERVICE_LEVELOUT;传筛,150;过敏,30;跌倒风险,80;日间手术,80,boolean");// 2017.0331 zhanglei 增加日间手术标记
				table.setEnHeader("BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;schdCode;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;SCHD_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;DAY_OPE_FLG;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");// 2017.0331 zhanglei 增加日间手术标记
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;17,left;18,left");
			}
			/*modified by WangQing 20170516 end*/
			break;
		case 1:// TABLE出院
			// modified by WangQing 20170509 已经出院的病人不能使用出院通知
			callFunction("UI|hos|setEnabled", false);

			if ("IBSPAYBILL".equals(this.getRunFlg())) {//缴费作业表格添加“付款方式二”--xiongwg20150413
				this
				.getTTable(TABLE)
				.setHeader(
						"床号,60;姓名,80;性别,60,SEX;年龄,40;入院日期,80,Timestamp;住院天数,70;经治医师,100,USER_ID;最新诊断,120;护理等级,120,NURSING_CLASS;病情状态,120,PATIENT_STATUS;付款方式,80,CTZ_CODE;付款方式二,80,CTZ_CODE;预交金余额,120,double;临床路径,100,CLNCPATH_CODE;单病种,100,DISE_CODE;病案号,100;住院号,100;病案审核,100,MRO_CHAT_FLG;服务等级,140,SERVICE_LEVELOUT");
				this
				.getTTable(TABLE)
				.setEnHeader(
						"BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				this
				.getTTable(TABLE)
				.setParmMap(
						"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CTZ2_CODE;CUR_AMT;CLNCPATH_CODE;DISE_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				this
				.getTTable(TABLE)
				.setColumnHorizontalAlignmentData(
						"0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;15,right;16,right;17,left;18,left;");
			} else {
				// this.getTTable(TABLE)
				// .setHeader(
				// "床号,80;姓名,80;性别,60,SEX;年龄,80;入院日期,120,Timestamp;出院日期,120,Timestamp;住院天数,80;出院诊断,120;付款方式,100,CTZ_CODE;账务状态,120,BILL_STATUS;临床路径,100,CLNCPATH_CODE_OUT;病案号,100;住院号,100;病案审核,140,MRO_CHAT_FLG;服务等级,140,SERVICE_LEVELOUT");
				// this.getTTable(TABLE)
				// .setEnHeader(
				// "BedNo;Name;Gender;Age;Admission Date;Discharge Date;Days;Discharge Diagnosis;PayType;Financial Status;CLNCPATH;MrNo;IpdNo;Mr Check;Service Level");
				// this.getTTable(TABLE)
				// .setParmMap(
				// "BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DS_DATE;DAYNUM;MAINDIAG;CTZ1_CODE;BILL_STATUS;CLNCPATH_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE;HEIGHT;WEIGHT;CASE_NOOUT;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				// this.getTTable(TABLE)
				// .setColumnHorizontalAlignmentData(
				// "0,Left;1,Left;2,Left;3,Left;4,Left;5,left;6,right;7,Left;8,Left;9,left;10,left;11,Right;12,Right;13,left;14,left;15,left;");
				this
				.getTTable(TABLE)
				// add by wanglong 20121115
				.setHeader(																																					
						"床号,60;姓名,80;性别,60,SEX;年龄,40;入院日期,80,Timestamp;经治医师,80,USER_ID;出院日期,80,Timestamp;住院天数,70;出院诊断,120;付款方式,100,CTZ_CODE;总金额,120,double;账务状态,120,BILL_STATUS;临床路径,100,CLNCPATH_CODE_OUT;单病种,100,DISE_CODE;病案号,100;住院号,100;病案审核,140,MRO_CHAT_FLG;病历封存,140,SEALED_STATUS;服务等级,140,SERVICE_LEVELOUT;传筛,150;过敏,30;跌倒风险,80");//add machao 2017/6/26  增加增金额列
				this
				.getTTable(TABLE)
				// add by wanglong 20121115
				.setEnHeader(
						"BedNo;Name;Gender;Age;Admission Date;Latest Diagnosis;Discharge Date;Days;Discharge Diagnosis;PayType;Tot_Amt;Financial Status;CLNCPATH;Single Disease;MrNo;IpdNo;Mr Check;Service Level");//add machao 2017/6/26  增加增金额列
				this
				.getTTable(TABLE)
				// add by wanglong 20121115
				.setParmMap(
						"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;VS_DR_CODE;DS_DATE;DAYNUM;MAINDIAG;CTZ1_CODE;TOT_AMT;BILL_STATUS;CLNCPATH_CODE;DISE_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE;HEIGHT;WEIGHT;CASE_NOOUT;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");//add machao 2017/6/26  增加增金额列
				this
				.getTTable(TABLE)
				// add by wanglong 20121115
				.setColumnHorizontalAlignmentData(
						"0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,left;9,left;10,right;11,left;12,left;13,left;14,right;15,right;16,left;17,left;18,left;19,left;20,left;21,left");//add machao 2017/6/26  增加增金额列
			}
			break;
		}
	}

	/**
	 * 得到TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * 清空
	 */
	public void onClear() {
		// this.messageBox("清空begin");
		// 得到TabbedPane控件

		Timestamp sysDate = SystemTool.getInstance().getDate();
		// 清空Table
		callFunction("UI|" + TABLE + "|removeRowAll");
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// 设置科室(出院)
			this.setValue("INW_DEPT_CODE", "");
			// 注解 待Operator完善在给默认值(设置病区在院)
			this.setValue("INW_STATION_CODE", Operator.getStation());
			// 经治医师（护士站）
			this.setValue("INW_VC_CODE", "");
		} else {
			// 设置科室(在院)
			this.setValue("DEPT_CODE", Operator.getDept());
			// 设置科室(出院)
			this.setValue("DEPT_CODEOUT", Operator.getDept());
			// 注解 待Operator完善在给默认值(设置病区在院)
			this.setValue("STATION_CODE", Operator.getStation());
			// 注解 待Operator完善在给默认值(设置病区出院)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		}
		// 默认设置在院页签的住院日期
		this.setValue("ADM_DATE", sysDate);
		// 默认设置起始日期
		this.setValue("START_DATE", sysDate);
		// 默认设置终止日期
		this.setValue("END_DATE", sysDate);
		// 默认设置出院页签的住院日期
		this.setValue("ADM_DATEOUT", sysDate);
		// 设置人数可编辑状态(在院)
		callFunction("UI|PRESON_NUM|setEnabled", false);
		// 设置人数可编辑状态(出院)
		callFunction("UI|PRESON_NUMOUT|setEnabled", false);
		// 设置页签为在院
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		// this.messageBox("selType"+selType);

		// // 表示页签选择的状态(在院)
		// tabPane.setSelectedIndex(0);
		// 清空相关控件信息(在院)
		// ============================== chenxi modify
		this.clearValue("CLNCPATH_CODE;SCHD_CODE;VC_CODE");
		this.setValue("BILL_STATUS", "");
		// ============================== chenxi modify
		this
		.clearValue("BED_NO;MR_NO;PAT_NAME;SEX;SERVICE_LEVELIN;WEIGHT;YELLOW;PRESON_NUM;YJJ_PRICE;YJYE_PRICE;GREED_PRICE;CASE_NO;STATION_CODE;IPD_NO;TOTAL_AMT;AGE");
		// 清空相关控件信息(出院)
		this
		.clearValue("BED_NOOUT;MR_NOOUT;PAT_NAMEOUT;PERSON_NUMOUT;CASE_NOOUT;IPD_NOOUT;SERVICE_LEVELOUT;STATION_CODEOUT;VC_CODEOUT;CLNCPATH_CODE_OUT;AGEOUT;CTZ_CODE");
		// 移除工作页面
		this.onClosePanel();
		// out("清空end");
	}

	/**
	 * 和其他页面联动查询
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 */
	public void queryDataOtherTPane(TParm parm, String type) {
		// System.out.println("联动查询结果"+parm);
		TParm action = new TParm();
		if (workPanelTag.toUpperCase().equals("INWSTATIONCHECK")
				|| workPanelTag.toUpperCase().equals("INWSTATIONEXECUTE")
				|| workPanelTag.toUpperCase().equals("INWSHEET")) {
			if ("IN".equals(type)) {
				// 在院
				action.setData("INW", "CASE_NO", parm.getData("CASE_NO", 0));
				action.setData("INW", "STATION_CODE", parm.getData(
						"STATION_CODE", 0));
				action.setData("INW", "POPEDEM", this.isInwPopedem());
				// 身份1
				action
				.setData("INW", "CTZ1_CODE", parm.getData("CTZ1_CODE",
						0));
				// 身份2
				action
				.setData("INW", "CTZ2_CODE", parm.getData("CTZ2_CODE",
						0));
				// 身份3
				action
				.setData("INW", "CTZ3_CODE", parm.getData("CTZ3_CODE",
						0));
				// 姓名
				action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			} else {
				// 出院
				action.setData("INW", "CASE_NO", parm.getData("CASE_NO", 0));
				action.setData("INW", "STATION_CODE", parm.getData(
						"STATION_CODE", 0));
				action.setData("INW", "POPEDEM", this.isInwPopedem());
				// 身份1
				action
				.setData("INW", "CTZ1_CODE", parm.getData("CTZ1_CODE",
						0));
				// 身份2
				action
				.setData("INW", "CTZ2_CODE", parm.getData("CTZ2_CODE",
						0));
				// 身份3
				action
				.setData("INW", "CTZ3_CODE", parm.getData("CTZ3_CODE",
						0));
				// 姓名
				action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			}
			// 重新赋值参数必须重新加载但会判断是否已经加载如果已经加载则只赋值初始化参数(INW无意义只要不是空字符串就可以)
			getTPanel().addItem(workPanelTag, "INW", action, false);
		}
		// //医生站
		// if(workPanelTag.toUpperCase().equals("STATIONMAIN")){
		// if ("IN".equals(type)) {
		// //在院
		// action.setData("ODI", "CASE_NO",parm.getData("CASE_NO",0));
		// action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
		// action.setData("ODI", "BED_NO", this.getValue("BED_NO"));
		// action.setData("ODI", "IPD_NO", parm.getData("IPD_NO",0));
		// action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
		// action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
		// String orgCode =
		// this.getOrgCode(this.getValue("STATION_CODE").toString(),this.getValue("DEPT_CODE").toString());
		// //拿到对应药房
		// action.setData("ODI", "ORG_CODE", orgCode);
		// action.setData("ODI", "STATION_CODE",this.getValue("STATION_CODE"));
		// action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
		// //停止划价
		// action.setData("ODI", "STOP_BILL_FLG",parm.getData("STOP_BILL_FLG"));
		// }
		// else {
		// //出院
		// action.setData("ODI", "CASE_NO",parm.getData("CASE_NO",0));
		// action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
		// action.setData("ODI", "BED_NO", this.getValue("BED_NO"));
		// action.setData("ODI", "IPD_NO", parm.getData("IPD_NO",0));
		// action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
		// action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
		// String orgCode =
		// this.getOrgCode(this.getValue("STATION_CODE").toString(),this.getValue("DEPT_CODE").toString());
		// //拿到对应药房
		// action.setData("ODI", "ORG_CODE", orgCode);
		// action.setData("ODI", "STATION_CODE",this.getValue("STATION_CODE"));
		// action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
		// //停止划价
		// action.setData("ODI", "STOP_BILL_FLG",parm.getData("STOP_BILL_FLG"));
		// }
		// //重新赋值参数必须重新加载但会判断是否已经加载如果已经加载则只赋值初始化参数(ODI无意义只要不是空字符串就可以)
		// getTPanel().addItem(workPanelTag,"ODI",action,false);
		// }
		// 计价
		if (workPanelTag.toUpperCase().equals("IBSSTATION")) {
			// 出院
			action.setData("IBS", "SCHD_CODE", parm.getData("SCHD_CODE", 0));//临床路径时程
			action.setData("IBS", "CASE_NO", parm.getData("CASE_NO", 0));
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("IBS", "IPD_NO", parm.getData("IPD_NO", 0));
			action.setData("IBS", "MR_NO", this.getValue("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAME"));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODE")
					.toString(), this.getValue("DEPT_CODE").toString());
			// 拿到对应药房
			action.setData("IBS", "ORG_CODE", orgCode);
			action
			.setData("IBS", "STATION_CODE", this
					.getValue("STATION_CODE"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// 停止划价
			action.setData("IBS", "STOP_BILL_FLG", parm
					.getData("STOP_BILL_FLG"));
			// 床号
			action.setData("IBS", "BED_NO", this.getValue("BED_NO"));
			// 身份1
			action.setData("IBS", "CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
			// 身份2
			action.setData("IBS", "CTZ2_CODE", parm.getData("CTZ2_CODE", 0));
			// 身份3
			action.setData("IBS", "CTZ3_CODE", parm.getData("CTZ3_CODE", 0));
			// 红色警戒
			action.setData("IBS", "RED_SIGN", parm.getData("RED_SIGN", 0));
			// 黄色警戒
			action
			.setData("IBS", "YELLOW_SIGN", parm.getData("YELLOW_SIGN",
					0));
			// 绿色通道
			action.setData("IBS", "GREENPATH_VALUE", parm.getData(
					"GREENPATH_VALUE", 0));
			// 医疗总费用
			action.setData("IBS", "TOTAL_AMT", parm.getData("TOTAL_AMT", 0));
			getTPanel().addItem(workPanelTag, "IBS", action, false);
		}
		// 病案首页
		if (workPanelTag.toUpperCase().equals("MROSTATION")) {
			action.setData("MRO", "CASE_NO", parm.getData("CASE_NO", 0));
		}
		// ADM病患动态查询
		if (workPanelTag.toUpperCase().equals("ADMCHGSTATION")) {
			action.setData("ADM", "CASE_NO", parm.getData("CASE_NO", 0));
			action.setData("ADM", "MR_NO", parm.getData("MR_NO", 0));
			action.setData("ADM", "IPD_NO", parm.getData("IPD_NO", 0));
			action.setData("ADM", "ADM_DATE", parm.getData("IN_DATE", 0));
		}
		if (workPanelTag.toUpperCase().equals("CLPMANAGEM")) {
			action.setData("CLPMANAGEM", "CASE_NO", parm.getData("CASE_NO", 0));
			getTPanel().addItem(workPanelTag, "CLPMANAGEM", action, false);
		}
		// 加预加金
		if (workPanelTag.toUpperCase().equals("BILPAY")) {
			action.setData("BILPAY", "CASE_NO", parm.getData("CASE_NO", 0));
			getTPanel().addItem(workPanelTag, "BILPAY", action, false);
		}
	}

	/**
	 * 住院医生站查询
	 */
	public void onQuery() {

		/*modified by WangQing 20170428 start*/
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.onQueryNew();
			return;
		}
		/*modified by WangQing 20170428 end*/

		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		if (selType == 0) {
			// 得到在院查询的参数
			TParm queryData = this.getQueryData("IN");
			TParm actionParm = new TParm();
			// out("得到在院查询的参数"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// 得到查询SQL
				String sqlStr = creatInwQuerySQL(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// 判断是否是值班医生
				boolean stationFlg = isKeepWatch();
				// 得到查询SQL
				String sqlStr = createODIQuerySQL(queryData, "IN", stationFlg);
				System.out.println("查询sql:" + sqlStr);
				if (stationFlg) {
					// 查询在院病患基本信息
					actionParm = new TParm(this.getDBTool().select(sqlStr));
					//this.messageBox("1:" + actionParm.getValue("DAY_OPE_FLG"));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
					//this.messageBox("2:" + actionParm.getValue("DAY_OPE_FLG"));
				}
				//				System.out.println("======sqlStr sqlStr is :::"+sqlStr);
			}
			// out("在院查询后的数据"+actionParm);
			// 如果查询为空返回
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// 清空Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// 得到工作页签的TAG并联动查询
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "IN");
				return;
			}

			// 设置TABLE上的数据
			this.setTableData(actionParm, "IN");

			/*modified by Eric 20170519 胸痛病患提示*/
			System.out.println("------ENTER_ROUTE="+actionParm.getValue("ENTER_ROUTE", 0));
			System.out.println("------PATH_KIND="+actionParm.getValue("PATH_KIND", 0));

			Color red = new Color(255, 0, 0);
			HashMap maper = new HashMap();
			HashMap mappk = new HashMap();
			TTable table = this.getTTable("TABLE");
			SelectResult sr = new SelectResult(actionParm);
			int cnt = sr.size();
			for(int i=0;i<cnt;i++){
				Object er = sr.getRowField(i,"ENTER_ROUTE");
				Object pk = sr.getRowField(i,"PATH_KIND");
				if(er !=null && !"E01".equals(er) && !"".equals(er)){
					maper.put(i, CPCColor);
				}
				if("P01".equals(pk)){
					mappk.put(i, red);
				}
			}
			if (maper.size() > 0) {
				table.setRowColorMap(maper);
			}
			if (mappk.size() > 0) {
				table.setRowTextColorMap(mappk);
			}

		} else {
			// 得到出院查询的参数
			TParm queryData = this.getQueryData("OUT");
			// 判断是否是值班医生
			boolean stationFlg = isKeepWatch();
			TParm actionParm = new TParm();
			// 得到查询SQL
			String sqlStr = createODIQuerySQL(queryData, "OUT", stationFlg);//
			//			 System.out.println("出院查询SQL：" + sqlStr);
			if (stationFlg) {
				// 查询在院病患基本信息
				actionParm = new TParm(this.getDBTool().select(sqlStr));
				//this.messageBox(actionParm.getValue("DAY_OPE_FLG"));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
				//this.messageBox(actionParm.getValue("DAY_OPE_FLG"));
			}
			// out("在院查询后的数据" + actionParm);
			// 如果查询为空返回
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// 清空Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// 得到工作页签的TAG并联动查询
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "OUT");
				return;
			}
			// 设置TABLE上的数据
			this.setTableData(actionParm, "OUT");

			/*modified by Eric 20170519 胸痛病患提示*/
			System.out.println("------ENTER_ROUTE="+actionParm.getValue("ENTER_ROUTE", 0));
			System.out.println("------PATH_KIND="+actionParm.getValue("PATH_KIND", 0));

			Color red = new Color(255, 0, 0);
			HashMap maper = new HashMap();
			HashMap mappk = new HashMap();
			TTable table = this.getTTable("TABLE");
			SelectResult sr = new SelectResult(actionParm);
			int cnt = sr.size();
			for(int i=0;i<cnt;i++){
				Object er = sr.getRowField(i,"ENTER_ROUTE");
				Object pk = sr.getRowField(i,"PATH_KIND");
				if(er !=null && !"E01".equals(er) && !"".equals(er)){
					maper.put(i, CPCColor);
				}
				if("P01".equals(pk)){
					mappk.put(i, red);
				}
			}
			if (maper.size() > 0) {
				table.setRowColorMap(maper);
			}
			if (mappk.size() > 0) {
				table.setRowTextColorMap(mappk);
			}
		}

	}

	/**
	 * 根据病案号查询
	 */
	public void onQueryForMrNo() {
		//		/*modified by WangQing 20170428 start*/
		//		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
		//			this.onQueryNew();
		//			return;
		//		}
		//		/*modified by WangQing 20170428 end*/

		//machao start 20170503 判断是否是从感染页面调入 感染跳入会诊
		if(this.getParameter() instanceof TParm){
			if(((TParm)this.getParameter()).getValue("CASE_NO").length()>0){
				String sql =
						"SELECT B.CLNCPATH_CODE, A.BED_NO_DESC, C.PAT_NAME, C.SEX_CODE, C.BIRTH_DATE, B.IN_DATE, B.DS_DATE, "
								+ "       D.ICD_CHN_DESC AS MAINDIAG, B.CTZ1_CODE, B.MR_NO, B.IPD_NO, B.TOTAL_AMT, B.TOTAL_BILPAY, "
								+ "       B.GREENPATH_VALUE, B.STATION_CODE, B.RED_SIGN, B.YELLOW_SIGN, B.STOP_BILL_FLG, A.BED_NO, "
								+ "       B.CTZ2_CODE, B.CTZ3_CODE, B.VS_DR_CODE, B.DEPT_CODE, B.HEIGHT, B.WEIGHT, B.CASE_NO, "
								+ "       B.CUR_AMT, C.POST_CODE, C.ADDRESS, C.COMPANY_DESC, C.CELL_PHONE, C.TEL_HOME, C.IDNO, C.PAT_NAME1, "
								+ "       B.NURSING_CLASS, B.PATIENT_STATUS, D.ICD_CODE, E.CHECK_FLG AS MRO_CHAT_FLG, A.ENG_DESC, "
								+ "       B.SERVICE_LEVEL, B.BILL_STATUS, B.DISE_CODE "
								+ "  FROM SYS_BED A, ADM_INP B, SYS_PATINFO C, SYS_DIAGNOSIS D, MRO_MRV_TECH E "
								+ " WHERE A.BED_NO = B.BED_NO(+)   "
								+ "   AND A.CASE_NO = B.CASE_NO(+) "
								+ "   AND A.MR_NO = B.MR_NO(+)     "
								+ "   AND A.MR_NO = C.MR_NO(+)     "
								+ "   AND A.ACTIVE_FLG = 'Y'       "
								+ "   AND A.CASE_NO = E.CASE_NO(+) "
								+ "   AND A.MR_NO = E.MR_NO(+)     "
								+ "   AND A.ALLO_FLG = 'Y'         "
								+ "   AND B.CANCEL_FLG <> 'Y'      "
								+ "   AND A.BED_STATUS = '1'       "
								+ "   AND B.REGION_CODE = 'H01'    "
								+ "   AND B.MAINDIAG = D.ICD_CODE(+) "
								+ "   AND B.CASE_NO = '#'              "
								+ "ORDER BY B.CASE_NO DESC             ";
				sql = sql.replaceFirst("#", ((TParm)this.getParameter()).getValue("CASE_NO"));
				System.out.println("sss1:"+sql);
				TParm actionParm = new TParm(this.getDBTool().select(sql));

				if(actionParm.getData("DS_DATE", 0) != null){
					this.setTableData(actionParm, "OUT");
				}else{
					this.setTableData(actionParm, "IN");
				}

				TTable table = ((TTable)getComponent("TABLE"));
				TParm p = table.getParmValue();

				table.setSelectedRow(0);

				TParm dataParm = table.getParmValue().getRow(0);

				this.setValue("BED_NO", dataParm.getData("BED_NO_DESC"));

				this.setValue("MR_NO", dataParm.getData("MR_NO"));	
				this.setValue("PAT_NAME", dataParm.getData("PAT_NAME"));
				this.setValue("AGE", dataParm.getData("AGE"));

				this.setValue("IPD_NO", dataParm.getData("IPD_NO"));
				this.setValue("SEX",dataParm.getData("SEX_CODE"));
				this.setValue("CTZ_CODE",dataParm.getData("CTZ1_CODE"));

				this.setValue("SERVICE_LEVELIN",dataParm.getData("SERVICE_LEVEL"));
				this.setValue("WEIGHT",dataParm.getData("WEIGHT"));							

				this.setValue("VC_CODE",dataParm.getData("VS_DR_CODE"));
				this.setValue("ADM_DATE",dataParm.getData("IN_DATE"));

				this.setValue("PAY_INS",dataParm.getData("PAY_INS"));

				this.setValue("CLNCPATH_CODE",dataParm.getData("CLNCPATH_CODE"));
				this.setValue("SCHD_CODE",dataParm.getData("SCHD_CODE"));

				//((TTextField) getComponent("BED_NO")).setEnabled(false);
				//((TTextField) getComponent("MR_NO")).setEnabled(false);
				//((TTextField) getComponent("IPD_NO")).setEnabled(false);
				//((TTextField) getComponent("TOTAL_AMT")).setEnabled(false);
				//((TTextField) getComponent("PAY_INS")).setEnabled(false);
				//((TTextField) getComponent("PRESON_NUM")).setEnabled(false);

				//((TComboBox) getComponent("DEPT_CODE")).setEnabled(false);
				//((TComboBox) getComponent("STATION_CODE")).setEnabled(false);
				//((TComboBox) getComponent("VC_CODE")).setEnabled(false);
				//((TComboBox) getComponent("ADM_DATE")).setEnabled(false);
				//((TComboBox) getComponent("CLNCPATH_CODE")).setEnabled(false);
				//((TComboBox) getComponent("SCHD_CODE")).setEnabled(false);

				onTableDoubled(0);
				return;
			}
		}
		//end 20170503 machao

		// modify by huangtt 20160928 EMPI患者查重提示 start
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
		}
		//				

		// modify by huangtt 20160928 EMPI患者查重提示 start


		//add by yangjj 20151112 如果界面为护士执行界面，则查询c:\javahis\nis\nis.txt文件，读取nis.txt的病患信息
		if("INWSTATIONEXECUTE".equals(workPanelTag)){
			onClosePanel();
			readTxtFile("C:\\JavaHis\\NIS\\nis.txt");
			return;
		}


		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");

		// 表示页签选择的状态
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		if (selType == 0) {
			// 得到在院查询的参数
			TParm queryData = this.getQueryData("IN");
			TParm actionParm = new TParm();
			// out("得到在院查询的参数"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// 得到查询SQL
				String sqlStr = creatInwQuerySqlForMrNo(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// 判断是否是值班医生
				boolean stationFlg = isKeepWatch();
				// 得到查询SQL
				String sqlStr = createODIQuerySqlForMrNo(queryData, "IN",
						stationFlg);
				// System.out.println("查询sql:" + sqlStr);
				if (stationFlg) {
					// 查询在院病患基本信息
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				}
			}
			// out("在院查询后的数据"+actionParm);
			// 如果查询为空返回
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// 清空Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// 得到工作页签的TAG并联动查询
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "IN");
				return;
			}
			// 设置TABLE上的数据
			this.setTableData(actionParm, "IN");

			/*modified by Eric 20170519 胸痛病患提示*/
			System.out.println("------ENTER_ROUTE="+actionParm.getValue("ENTER_ROUTE", 0));
			System.out.println("------PATH_KIND="+actionParm.getValue("PATH_KIND", 0));

			Color red = new Color(255, 0, 0);
			HashMap maper = new HashMap();
			HashMap mappk = new HashMap();
			TTable table = this.getTTable("TABLE");
			SelectResult sr = new SelectResult(actionParm);
			int cnt = sr.size();
			for(int i=0;i<cnt;i++){
				Object er = sr.getRowField(i,"ENTER_ROUTE");
				Object pk = sr.getRowField(i,"PATH_KIND");
				if(er !=null && !"E01".equals(er) && !"".equals(er)){
					maper.put(i, CPCColor);
				}
				if("P01".equals(pk)){
					mappk.put(i, red);
				}
			}
			if (maper.size() > 0) {
				table.setRowColorMap(maper);
			}
			if (mappk.size() > 0) {
				table.setRowTextColorMap(mappk);
			}
		} else {
			// 得到出院查询的参数
			TParm queryData = this.getQueryData("OUT");
			// 判断是否是值班医生
			boolean stationFlg = isKeepWatch();
			TParm actionParm = new TParm();
			// 得到查询SQL
			String sqlStr = createODIQuerySqlForMrNo(queryData, "OUT",
					stationFlg);
			//			 System.out.println("出院查询SQL：" + sqlStr);
			if (stationFlg) {
				// 查询在院病患基本信息
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			}
			// out("在院查询后的数据" + actionParm);
			// 如果查询为空返回
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// 清空Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// 得到工作页签的TAG并联动查询
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "OUT");
				return;
			}
			// 设置TABLE上的数据
			this.setTableData(actionParm, "OUT");

			/*modified by Eric 20170519 胸痛病患提示*/
			System.out.println("------ENTER_ROUTE="+actionParm.getValue("ENTER_ROUTE", 0));
			System.out.println("------PATH_KIND="+actionParm.getValue("PATH_KIND", 0));

			Color red = new Color(255, 0, 0);
			HashMap maper = new HashMap();
			HashMap mappk = new HashMap();
			TTable table = this.getTTable("TABLE");
			SelectResult sr = new SelectResult(actionParm);
			int cnt = sr.size();
			for(int i=0;i<cnt;i++){
				Object er = sr.getRowField(i,"ENTER_ROUTE");
				Object pk = sr.getRowField(i,"PATH_KIND");
				if(er !=null && !"E01".equals(er) && !"".equals(er)){
					maper.put(i, CPCColor);
				}
				if("P01".equals(pk)){
					mappk.put(i, red);
				}
			}
			if (maper.size() > 0) {
				table.setRowColorMap(maper);
			}
			if (mappk.size() > 0) {
				table.setRowTextColorMap(mappk);
			}
		}

		//add by yangjj 20151028
		/*
		if(("".equals(this.getValueString("MR_NO"))) && ("INWEXE".equals(this.getRunFlg()))){
			readTxtFile("C:\\JavaHis\\NIS\\nis.txt");
		}
		 */
	}

	// ==========modify-begin (by wanglong 20120711)===============
	/**
	 * 住院医生站回车键查询
	 */
	public void onEnterQuery() {

		/*modified by WangQing 20170428 start*/
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.onQueryNew();
			return;
		}
		/*modified by WangQing 20170428 end*/

		// System.out.println("回车查询");
		// 得到出院查询的参数
		TParm queryData = this.getQueryData("OUT");
		// 判断是否是值班医生
		boolean stationFlg = isKeepWatch();
		TParm actionParm = new TParm();
		// 在院还是出院
		String type = "OUT";
		// SQL
		String sql = "";
		// 占床注记ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// 病区
		String stationStr = "";
		if ("IN".equals(type)) {
			stationStr = queryData.getValue("STATION_CODE").length() == 0 ? ""
					: " AND A.STATION_CODE='"
					+ queryData.getValue("STATION_CODE") + "'";
		} else {
			stationStr = queryData.getValue("STATION_CODE").length() == 0 ? ""
					: " AND B.STATION_CODE='"
					+ queryData.getValue("STATION_CODE") + "'";
		}
		// 科室
		String deptCode = "";
		if ("IN".equals(type)) {
			deptCode = queryData.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND B.DEPT_CODE='" + queryData.getValue("DEPT_CODE")
					+ "'";
		} else {
			deptCode = queryData.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND A.DEPT_CODE='" + queryData.getValue("DEPT_CODE")
					+ "'";
		}
		// 账务状态
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = queryData.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='"
					+ queryData.getValue("BILL_STATUS") + "'";
		}
		// ================liming modify 20120217 start
		String vsDoctor = "";
		if ("IN".equals(type)) {
			vsDoctor = queryData.getValue("VC_CODE").length() == 0 ? ""
					: " AND B.VS_DR_CODE='" + queryData.getValue("VC_CODE")
					+ "'";
		}
		// ================liming modify 20120217 end

		// 开始时间
		String startDate = StringTool.getString((Timestamp) queryData
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDate = StringTool.getString((Timestamp) queryData
				.getData("END_DATE"), "yyyyMMdd");
		// 开始时间
		String startDateOut = StringTool.getString((Timestamp) queryData
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDateOut = StringTool.getString((Timestamp) queryData
				.getData("END_DATEOUT"), "yyyyMMdd");

		// System.out.println("结束时间:"+endDate);
		// System.out.println("审核:"+flg);

		// ===========pangben modify 20110512 start
		String region = "";
		if (null != Operator.getRegion() && Operator.getRegion().length() > 0) {
			region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
		}
		// ===========pangben modify 20110512 stop
		if (queryData.getValue("CASE_NO").length() != 0) {
			sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
					+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
					+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
					+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
					+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
					+ ",A.DISE_CODE "// add by wanglong 20121115
					+ ",A.SCHD_CODE "
					+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 				
					+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
					+ " ,E.SEALED_STATUS " //add by huangtt 20161103
					//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
					+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

					/*modified by Eric 20170519 胸痛病患提示*/
					+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

					/*modified by Eric 20170519 胸痛病患提示*/
					+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG,IBS_ORDD G"
					+ " WHERE A.BED_NO=B.BED_NO"
					+ " AND A.MR_NO=C.MR_NO"
					+ " AND A.CANCEL_FLG<>'Y'"
					+ " AND (A.DS_DATE IS NOT NULL OR (A.DS_DATE IS NULL AND LAST_DS_DATE IS NOT NULL))"
					+ " AND A.MR_NO=E.MR_NO(+)"
					+ " AND A.CASE_NO=E.CASE_NO(+)"
					// ==========modify-begin (by wanglong
					// 20120711)===============
					// + " AND (A.DS_DATE BETWEEN TO_DATE('"
					// + startDateOut
					// + "','YYYYMMDD') AND TO_DATE('"
					// + endDateOut
					// + "','YYYYMMDD') "
					// + " OR A.IN_DATE BETWEEN TO_DATE('"
					// + startDate
					// + "','YYYYMMDD') AND TO_DATE('"
					// + endDate
					// + "','YYYYMMDD'))"
					// + stationStr
					+ " AND A.MAINDIAG = D.ICD_CODE(+)"
					// + deptCode
					+ billStatus
					+ region // ===========pangben modify 20110512

					/*modified by Eric 20170519 胸痛病患提示*/
					+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
					+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

					+ " AND A.CASE_NO='"
					+ queryData.getValue("CASE_NO")+"'"
					+ " AND A.CASE_NO = G.CASE_NO "
					+" GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE, A.DS_DATE,"
					+"D.ICD_CHN_DESC,A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
					+ "A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,"
					+ "A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,"
					+ "A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
					+ "(CASE A.ALLERGY WHEN 'Y' THEN '有' ELSE '无' END), "
					+"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
					+"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND"
					+ " ORDER BY A.BED_NO";
			// ==========modify-end========================================
			//System.out.println("sql99::" + sql);
		} else {

			sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
					+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
					+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
					+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
					+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
					+ ",A.DISE_CODE "// add by wanglong 20121115
					+ ",A.SCHD_CODE "
					+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
					+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
					+ " ,E.SEALED_STATUS " //add by huangtt 20161103
					//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
					+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

					/*modified by Eric 20170519 胸痛病患提示*/
					+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

					/*modified by Eric 20170519 胸痛病患提示*/
					+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG,IBS_ORDD G"
					+ " WHERE A.BED_NO=B.BED_NO"
					+ " AND A.MR_NO=C.MR_NO"
					+ " AND A.CANCEL_FLG<>'Y'"
					+ " AND A.MR_NO=E.MR_NO(+)"
					+ " AND A.CASE_NO=E.CASE_NO(+)"
					+ " AND ((A.DS_DATE IS NOT NULL"
					+ " AND (A.DS_DATE BETWEEN TO_DATE('"
					+ startDateOut
					+ "','YYYYMMDD') AND TO_DATE('"
					+ endDateOut
					+ "','YYYYMMDD') "
					+ " OR A.IN_DATE BETWEEN TO_DATE('"
					+ startDate
					+ "','YYYYMMDD') AND TO_DATE('"
					+ endDate
					+ "','YYYYMMDD'))) OR (A.DS_DATE IS NULL AND LAST_DS_DATE IS NOT NULL))"
					+ stationStr

					/*modified by Eric 20170519 胸痛病患提示*/
					+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
					+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

					+ " AND A.MAINDIAG = D.ICD_CODE(+)" + deptCode + region + // ===========pangben
					// modify
					// 20110512
					billStatus 
					+" AND A.CASE_NO = G.CASE_NO "
					+" GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE, A.DS_DATE,"
					+"D.ICD_CHN_DESC,A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
					+ "A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,"
					+ "A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,"
					+ "A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
					+ "(CASE A.ALLERGY WHEN 'Y' THEN '有' ELSE '无' END), "
					+"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
					+"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND "

					+" ORDER BY A.BED_NO";
			//System.out.println("sql19::" + sql);
		}

		String sqlStr = sql;
		//		System.out.println("  出院sql is：："+sqlStr);
		if (stationFlg) {
			// 查询在院病患基本信息
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		} else {
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		}
		// out("在院查询后的数据" + actionParm);
		// 如果查询为空返回
		if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// 清空Table
			callFunction("UI|" + TABLE + "|removeRowAll");
			return;
		}
		// 得到工作页签的TAG并联动查询
		if (workPanelTag.length() != 0) {
			this.queryDataOtherTPane(actionParm, "OUT");
			return;
		}
		// 设置TABLE上的数据
		this.setTableData(actionParm, "OUT");

		/*modified by Eric 20170519 胸痛病患提示*/
		System.out.println("------ENTER_ROUTE="+actionParm.getValue("ENTER_ROUTE", 0));
		System.out.println("------PATH_KIND="+actionParm.getValue("PATH_KIND", 0));

		Color red = new Color(255, 0, 0);
		HashMap maper = new HashMap();
		HashMap mappk = new HashMap();
		TTable table = this.getTTable("TABLE");
		SelectResult sr = new SelectResult(actionParm);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && !"E01".equals(er) && !"".equals(er)){
				maper.put(i, CPCColor);
			}
			if("P01".equals(pk)){
				mappk.put(i, red);
			}
		}
		if (maper.size() > 0) {
			table.setRowColorMap(maper);
		}
		if (mappk.size() > 0) {
			table.setRowTextColorMap(mappk);
		}
	}

	// ==========modify-end========================================

	/**
	 * 护士查询
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 */
	public String creatInwQuerySQL(TParm parm, String type) {
		String sql = "";
		// 占床注记ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// 病区
		String stationStr = "";
		if ("IN".equals(type)) {
			/*stationStr = " AND A.STATION_CODE='"
					+ parm.getValue("STATION_CODE") + "'";*/
			stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
					: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "'";
		} else {
			/*stationStr = " AND B.STATION_CODE='"
					+ parm.getValue("STATION_CODE") + "'";*/
			stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
					: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "'";
		}
		// 科室
		String deptCode = "";
		if ("IN".equals(type)) {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND B.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		} else {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		}
		// 账务状态
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
					+ "'";
		}
		// ================liming modify 20120217 start
		String vsDoctor = "";
		if ("IN".equals(type)) {
			vsDoctor = parm.getValue("VC_CODE").length() == 0 ? ""
					: " AND B.VS_DR_CODE='" + parm.getValue("VC_CODE") + "'";
		}
		// ================liming modify 20120217 end

		// 开始时间
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// 开始时间
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");
		// =====modify-begin (by wanglong
		// 20120723)===============================
		String inDateTerms = "";
		if ("IN".equals(type)) {
			String inDateStr = "" + this.getValue("ADM_DATE");
			String inDateDay = inDateStr.substring(0, inDateStr.indexOf(" "))
					.replace("-", "");
			inDateTerms = "AND B.IN_DATE BETWEEN TO_DATE('" + inDateDay
					+ "000000', 'YYYYMMDDHH24MISS') AND " + " TO_DATE('"
					+ inDateDay + "235959', 'YYYYMMDDHH24MISS')";
		}
		// ======modify-end========================================================
		// 在院
		if (type.equals("IN")) {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND B.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop

			if (parm.getValue("CASE_NO").length() != 0) {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ ",B.DISE_CODE,B.DIE_CONDITION,B.SCHD_CODE "// add by wanglong
						// 20121115 //新增
						// B.DIE_CONDITION字段
						// caoyong 20130708
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "
						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ alloFlg
						+ stationStr
						+ region + vsDoctor + deptCode + // =====pangben modify
						// 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)" + " AND B.CASE_NO='"
						// + parm.getValue("CASE_NO") + "'" +
						// " ORDER BY A.BED_NO";
						+ parm.getValue("CASE_NO") + "'"
						// =====modify-begin (by wanglong 20120723)=====
						// + inDateTerms
						// ======modify-end=============================
						+ " ORDER BY A.BED_NO_DESC";
				//				 System.out.println("sql3::" + sql);
			} else {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ ",B.DISE_CODE,B.DIE_CONDITION,B.SCHD_CODE "// add by wanglong
						// 20121115 //新增
						// B.DIE_CONDITION字段
						// caoyong 20130708
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E,  ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ alloFlg
						+ stationStr
						+ region + vsDoctor + deptCode + // =====pangben modify
						// 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						// =====modify-begin (by wanglong 20120723)=====
						// + inDateTerms
						// ======modify-end=============================
						// + " ORDER BY A.BED_NO";
						+ " ORDER BY A.BED_NO DESC";
				//				 System.out.println("sql4::" + sql);
			}
			// 出院
		} else {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop

			if (parm.getValue("CASE_NO").length() != 0) {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ ",A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ stationStr
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"
						+ deptCode
						+ region

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"


						+ // ============pangben modify 20110512
						" AND A.CASE_NO='" + parm.getValue("CASE_NO") + "'"
						// + billStatus + " ORDER BY A.BED_NO";
						+ billStatus + " ORDER BY A.BED_NO_DESC";
				// System.out.println("sql5::"+sql);
			} else {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,A.MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ ",A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
						+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ stationStr
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"
						+ deptCode
						+ region

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ // ============pangben modify 20110512
						// billStatus + " ORDER BY A.BED_NO";
						billStatus + " ORDER BY A.BED_NO_DESC";
				// System.out.println("sql6::"+sql);
			}
		}
		return sql;
	}

	public String creatInwQuerySqlForMrNo(TParm parm, String type) {
		String sql = "";
		// 占床注记ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// 账务状态
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
					+ "'";
		}
		// 开始时间
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// 开始时间
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");

		// 在院
		if (type.equals("IN")) {
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND B.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			if (parm.getValue("CASE_NO").length() != 0) {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"
						+ alloFlg
						+ region

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ // =====pangben modify 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						+ " AND B.CASE_NO='"
						+ parm.getValue("CASE_NO") + "'" + " ORDER BY A.BED_NO";
				// System.out.println("sql3::" + sql);
			} else {
				sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
						+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
						+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
						+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"
						+ alloFlg
						+ region

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ // =====pangben modify 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						+ " ORDER BY A.BED_NO";
				// System.out.println("sql4::" + sql);
			}
			// 出院
		} else {
			// ===========pangben modify 20110512 start
			String region = "";
			if (null != Operator.getRegion()
					&& Operator.getRegion().length() > 0) {
				region = " AND A.REGION_CODE='" + Operator.getRegion() + "' ";
			}
			// ===========pangben modify 20110512 stop

			if (parm.getValue("CASE_NO").length() != 0) {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
						+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"
						+ region

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ // ============pangben modify 20110512
						" AND A.CASE_NO='"
						+ parm.getValue("CASE_NO")
						+ "'"
						+ billStatus + " ORDER BY A.BED_NO";
				// System.out.println("sql5::"+sql);
			} else {
				sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
						+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
						+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
						+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
						+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
						+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
						+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
						+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO"
						+ " AND A.MR_NO=C.MR_NO"
						+ " AND A.CANCEL_FLG<>'Y'"
						+ " AND A.DS_DATE IS NOT NULL"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND (A.DS_DATE BETWEEN TO_DATE('"
						+ startDateOut
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDateOut
						+ "','YYYYMMDD') "
						+ " OR A.IN_DATE BETWEEN TO_DATE('"
						+ startDate
						+ "','YYYYMMDD') AND TO_DATE('"
						+ endDate
						+ "','YYYYMMDD'))"
						+ " AND A.MAINDIAG = D.ICD_CODE(+)"

						/*modified by Eric 20170519 胸痛病患提示*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ region + // ============pangben modify 20110512



						billStatus + " ORDER BY A.BED_NO";
				// System.out.println("sql6::"+sql);
			}
		}
		return sql;
	}

	/**
	 * 生成ODI医生站SQL
	 * 
	 * @param parm
	 *            TParm 查询参数
	 * @param type
	 *            String 入出院标记
	 * @param flg
	 *            boolean UI标记()
	 * @return String
	 */
	public String createODIQuerySQL(TParm parm, String type, boolean flg) {
		// SQL
		String sql = "";
		// 占床注记ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// 病区
		String stationStr = "";
		if ("IN".equals(type)) {
			stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
					: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "'";
		} else {
			stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
					: " AND B.STATION_CODE='" + parm.getValue("STATION_CODE")
					+ "'";
		}
		// 科室
		String deptCode = "";
		if ("IN".equals(type)) {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND B.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		} else {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		}
		// 账务状态
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
					+ "'";
		}
		// ================liming modify 20120217 start
		String vsDoctor = "";
		if ("IN".equals(type)) {
			vsDoctor = parm.getValue("VC_CODE").length() == 0 ? ""
					: " AND B.VS_DR_CODE='" + parm.getValue("VC_CODE") + "'";
		} else {
			vsDoctor = parm.getValue("VC_CODE").length() == 0 ? ""
					: " AND A.VS_DR_CODE='" + parm.getValue("VC_CODE") + "'"; // chenxi
			// modify
			// 出院患者可以根据经制医生查询
		}
		// ================liming modify 20120217 end

		// 开始时间
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// 开始时间
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");
		// =====modify-begin (by wanglong
		// 20120723)===============================
		String inDateTerms = "";
		if ("IN".equals(type)) {
			String inDateStr = "" + this.getValue("ADM_DATE");
			String inDateDay = inDateStr.substring(0, inDateStr.indexOf(" "))
					.replace("-", "");
			inDateTerms = "AND B.IN_DATE BETWEEN TO_DATE('" + inDateDay
					+ "000000', 'YYYYMMDDHH24MISS') AND " + " TO_DATE('"
					+ inDateDay + "235959', 'YYYYMMDDHH24MISS')";
		}
		// ======modify-end========================================================
		// System.out.println("结束时间:"+endDate);
		// System.out.println("审核:"+flg);
		if (flg) {
			// 在院
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E,ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region
							+ vsDoctor
							+ deptCode // ============= chenxi modify 20130320
							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "'"
							// =====modify-begin (by wanglong 20120723)=====
							// + inDateTerms
							// ======modify-end=============================
							+ " ORDER BY A.BED_NO";
					//					 System.out.println("sql3::" + sql);
				} else {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E,ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region + vsDoctor + deptCode // =============
							// chenxi modify
							// 20130320
							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							// =====modify-begin (by wanglong 20120723)=====
							// + inDateTerms
							// ======modify-end=============================
							+ " ORDER BY A.BED_NO";
					//					 System.out.println("sql4::" + sql);
				}
				// 出院
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG,IBS_ORDD G"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							/**
							 * + " AND (A.DS_DATE BETWEEN TO_DATE('" +
							 * startDateOut + "','YYYYMMDD') AND TO_DATE('" +
							 * endDateOut + "','YYYYMMDD') " +
							 * " OR A.IN_DATE BETWEEN TO_DATE('" + startDate +
							 * "','YYYYMMDD') AND TO_DATE('" + endDate +
							 * "','YYYYMMDD'))"
							 **/
							 + stationStr
							 + " AND A.MAINDIAG = D.ICD_CODE(+)"
							 + deptCode
							 + region

							 /*modified by Eric 20170519 胸痛病患提示*/
							 + " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							 + " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							 + // ============pangben modify 20110512
							 " AND A.CASE_NO='"
							 + parm.getValue("CASE_NO")
							 + "'"
							 + billStatus 
							 + "AND A.CASE_NO = G.CASE_NO"
							 +" GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE, A.DS_DATE,"
							 +"D.ICD_CHN_DESC,A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							 + "A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,"
							 + "A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,"
							 + "A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
							 + "(CASE A.ALLERGY WHEN 'Y' THEN '有' ELSE '无' END), "
							 +"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
							 +"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND"
							 +" ORDER BY A.BED_NO";
					//System.out.println("sql5::"+sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "//增加ibs_ordd中总金额 

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG,IBS_ORDD G"//增加表IBS_ORDD
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ stationStr
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ deptCode

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ vsDoctor + region + // ============pangben modify
							// 20110512
							billStatus 
							+" AND A.CASE_NO = G.CASE_NO "
							+"GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC,"
							+"A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,A.RED_SIGN,A.YELLOW_SIGN,"
							+"A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,"
							+"C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,"
							+"E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
							+"(CASE A.ALLERGY WHEN 'Y' THEN '有' ELSE '无' END),"
							+"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
							+"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND"
							+
							" ORDER BY A.BED_NO";
					//System.out.println("sql6::"+sql);
				}
			}
		} else {
			// 在院
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region
							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND (B.VS_DR_CODE='"
							+ Operator.getID()
							+ "' OR ATTEND_DR_CODE='"
							+ Operator.getID()
							+ "' OR DIRECTOR_DR_CODE='"
							+ Operator.getID()
							+ "')"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO") + "'"
							// =====modify-begin (by wanglong 20120723)=====
							// + inDateTerms
							// ======modify-end=============================

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ " ORDER BY A.BED_NO";
					//					 System.out.println("sql7::" + sql);
				} else {
					sql = "SELECT  B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ stationStr
							+ region
							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND (B.VS_DR_CODE='"
							+ Operator.getID()
							+ "' OR ATTEND_DR_CODE='"
							+ Operator.getID()
							+ "' OR DIRECTOR_DR_CODE='"
							+ Operator.getID()
							+ "')"
							// =====modify-begin (by wanglong 20120723)=====
							// + inDateTerms
							// ======modify-end=============================

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ " ORDER BY A.BED_NO";
					//					 System.out.println("sql8::" + sql);
				}
				// 出院
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG,IBS_ORDD G"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.CANCEL_FLG<>'Y'"
							/**
							 * + " AND A.DS_DATE IS NOT NULL" +
							 * " AND (A.DS_DATE BETWEEN TO_DATE('" +
							 * startDateOut + "','YYYYMMDD') AND TO_DATE('" +
							 * endDateOut + "','YYYYMMDD') " +
							 * " OR A.IN_DATE BETWEEN TO_DATE('" + startDate +
							 * "','YYYYMMDD') AND TO_DATE('" + endDate +
							 * "','YYYYMMDD'))"
							 **/
							 + stationStr
							 + " AND A.MAINDIAG = D.ICD_CODE(+)"
							 + deptCode
							 + billStatus
							 + region
							 + // ===========pangben modify 20110512
							 " AND A.CASE_NO='"
							 + parm.getValue("CASE_NO")

							 /*modified by Eric 20170519 胸痛病患提示*/
							 + "' AND A.CASE_NO=AR.IN_CASE_NO(+)"
							 + " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"
							 + " AND A.CASE_NO = G.CASE_NO "
							 +" GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE, A.DS_DATE,"
							 +"D.ICD_CHN_DESC,A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							 + "A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,"
							 + "A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,"
							 + "A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
							 + "(CASE A.ALLERGY WHEN 'Y' THEN '有' ELSE '无' END), "
							 +"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
							 +"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND"
							 + " ORDER BY A.BED_NO";

					//System.out.println("sqll9::"+sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG,IBS_ORDD G"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ stationStr
							+ " AND A.MAINDIAG = D.ICD_CODE(+)"
							+ deptCode

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ vsDoctor + region + // ===========pangben modify

							// 20110512
							billStatus 
							+"AND A.CASE_NO = G.CASE_NO "
							// modified by wangqing 20170707
							// 出院病人查不到
							+" GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC,"
							+"A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,A.RED_SIGN,A.YELLOW_SIGN,"
							+"A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,"
							+"C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,"
							+"E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
							+"(CASE A.ALLERGY WHEN 'Y' THEN '有' ELSE '无' END),"
							+"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
							+"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND"
							+
							" ORDER BY A.BED_NO";
					// System.out.println("sqll10::"+sql);
				}
			}
		}
		//		 System.out.println("sql:::::::::"+sql);
		return sql;
	}

	public String createODIQuerySqlForMrNo(TParm parm, String type, boolean flg) {
		// SQL
		String sql = "";
		// 占床注记ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// 账务状态
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
					+ "'";
		}
		// 开始时间
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// 开始时间
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("开始时间:"+startDate);
		// 结束时间
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");

		// System.out.println("结束时间:"+endDate);
		// System.out.println("审核:"+flg);
		if (flg) {
			// 在院
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "'" + " ORDER BY A.BED_NO";
					//					 System.out.println("sql3::" + sql);
				} else {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " ORDER BY A.BED_NO";
					//					 System.out.println("sql4::" + sql);
				}
				//				System.out.println("在院 在院 sql sql::" + sql);
				// 出院
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							/**
							 * + " AND (A.DS_DATE BETWEEN TO_DATE('" +
							 * startDateOut + "','YYYYMMDD') AND TO_DATE('" +
							 * endDateOut + "','YYYYMMDD') " +
							 * " OR A.IN_DATE BETWEEN TO_DATE('" + startDate +
							 * "','YYYYMMDD') AND TO_DATE('" + endDate +
							 * "','YYYYMMDD'))"
							 **/
							 + " AND A.MAINDIAG = D.ICD_CODE(+)"
							 + region

							 /*modified by Eric 20170519 胸痛病患提示*/
							 + " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							 + " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							 + // ============pangben modify 20110512
							 " AND A.CASE_NO='"
							 + parm.getValue("CASE_NO")
							 + "'"
							 + billStatus + " ORDER BY A.BED_NO";
					// System.out.println("sql5::"+sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"


							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ " AND A.MAINDIAG = D.ICD_CODE(+)" + region + // ============pangben
							// modify
							// 20110512
							billStatus + " ORDER BY A.BED_NO";
					// System.out.println("sql6::"+sql);
				}
			}
		} else {
			// 在院
			if (type.equals("IN")) {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND B.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = "SELECT B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " AND B.CASE_NO='"
							+ parm.getValue("CASE_NO")
							+ "' ORDER BY A.BED_NO";
					// System.out.println("sql7::" + sql);
				} else {
					sql = "SELECT  B.CLNCPATH_CODE,A.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,B.IN_DATE,B.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " B.CTZ1_CODE,B.MR_NO,B.IPD_NO,B.TOTAL_AMT,B.TOTAL_BILPAY,B.GREENPATH_VALUE,B.STATION_CODE,"
							+ " B.RED_SIGN,B.YELLOW_SIGN,B.STOP_BILL_FLG,A.BED_NO,B.CTZ2_CODE,B.CTZ3_CODE,B.VS_DR_CODE,"
							+ " B.DEPT_CODE,B.HEIGHT,B.WEIGHT,B.CASE_NO,B.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,B.NURSING_CLASS,B.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,A.ENG_DESC,B.SERVICE_LEVEL,B.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.ACTIVE_FLG='Y'"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND B.DS_DATE IS NULL"
							+ alloFlg
							+ region

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " ORDER BY A.BED_NO";
					// System.out.println("sql8::" + sql);
				}
				// 出院
			} else {
				// ===========pangben modify 20110512 start
				String region = "";
				if (null != Operator.getRegion()
						&& Operator.getRegion().length() > 0) {
					region = " AND A.REGION_CODE='" + Operator.getRegion()
					+ "' ";
				}
				// ===========pangben modify 20110512 stop

				if (parm.getValue("CASE_NO").length() != 0) {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							/**
							 * + " AND (A.DS_DATE BETWEEN TO_DATE('" +
							 * startDateOut + "','YYYYMMDD') AND TO_DATE('" +
							 * endDateOut + "','YYYYMMDD') " +
							 * " OR A.IN_DATE BETWEEN TO_DATE('" + startDate +
							 * "','YYYYMMDD') AND TO_DATE('" + endDate +
							 * "','YYYYMMDD'))"
							 **/
							 + " AND A.MAINDIAG = D.ICD_CODE(+)"
							 + billStatus
							 + region

							 /*modified by Eric 20170519 胸痛病患提示*/
							 + " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							 + " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							 + // ===========pangben modify 20110512
							 " AND A.CASE_NO='"
							 + parm.getValue("CASE_NO")
							 + "' ORDER BY A.BED_NO";
					// System.out.println("sql9::"+sql);
				} else {
					sql = " SELECT A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
							+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
							+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
							+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
							+ " ,B.DISE_CODE,B.SCHD_CODE "// add by wanglong 20121115
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 胸痛病患提示*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"

							 /*modified by Eric 20170519 胸痛病患提示*/
							 + " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							 + " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"
							+ " AND (A.DS_DATE BETWEEN TO_DATE('"
							+ startDateOut
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDateOut
							+ "','YYYYMMDD') "
							+ " OR A.IN_DATE BETWEEN TO_DATE('"
							+ startDate
							+ "','YYYYMMDD') AND TO_DATE('"
							+ endDate
							+ "','YYYYMMDD'))"
							+ " AND A.MAINDIAG = D.ICD_CODE(+)" + region + // ===========pangben
							// modify
							// 20110512
							billStatus + " ORDER BY A.BED_NO";
					// System.out.println("sql10::"+sql);
				}
			}
		}
		// System.out.println("sql:::::::::"+sql);
		return sql;
	}

	/**
	 * 得到单选框
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * 查询后放入TABLE中的数据显示
	 * 
	 * @param parm
	 *            TParm 所要放入的数据
	 * @param type
	 *            String 放入数据的类别 IN为在院,OUT为出院
	 */
	public void setTableData(TParm parm, String type) {
		// 放入TABLE数据
		if (type.equals("IN")) {
			// this.messageBox("IN come in");
			// 在院
			TParm actionParm = this.filterTParmData(parm);
			// out("出院数据"+actionParm);
			// 设置TABLE数据(在院)
			callFunction("UI|" + TABLE + "|setParmValue", actionParm);

			//add by huangtt 20161103 添加执行颜色 start

			if (this.getRunFlg().equals("ODI")) {
				TTable table = this.getTTable("TABLE");
				Color RED = new Color(255, 0, 0);
				for (int i = 0; i < actionParm.getCount("SEALED_STATUS"); i++) {
					if(actionParm.getValue("SEALED_STATUS", i).equals("2") || 
							actionParm.getValue("SEALED_STATUS", i).equals("3")){

						table.setRowColor(i, RED);

					}else{
						table.setRowColor(i, null);
					}
				}
			}
			//add by huangtt 20161103 添加执行颜色 end

			//			/*modified by Eric 20170518 start
			//			 * 胸痛中心病患提醒 
			//			 */
			//			TTable table = this.getTTable("TABLE");
			//			Color red = new Color(255, 0, 0);
			//			Color pink =new Color(255,170,255);
			//			HashMap map = new HashMap();
			//			HashMap wmap = new HashMap();
			//			for (int i = 0; i < actionParm.getCount(); i++) {
			//				String er = actionParm.getValue("ENTER_ROUTE", i);
			//				String pk = actionParm.getValue("PATH_KIND",i);
			//				
			//				if(er !=null && !er.trim().equals("") && !"E01".equals(er)){
			//					System.out.println("------test------");
			//					System.out.println("------er="+actionParm.getValue("ENTER_ROUTE", i));
			//					map.put(i, pink);			
			//				}				
			//				if(pk != null && pk.trim().equals("") && "P01".equals(pk)){
			//					System.out.println("---test2---");
			//					System.out.println("------pk="+actionParm.getValue("PATH_KIND",i));
			//					wmap.put(i, red);
			//				}
			//			}
			//			
			//			if (map.size() > 0) {
			//				table.setRowColorMap(map);
			//				
			//			}
			//			if(wmap.size()>0){
			//				table.setRowTextColorMap(wmap);
			//			}
			//			/*modified by Eric 20170518 start*/
			//			







			// 设置查询数据和页面显示数据对应
			TTable tab = (TTable) this.callFunction("UI|" + TABLE + "|getThis");
			tab
			.setModifyTag("DEPT_CODE:DEPT_CODE;STATION_CODE:STATION_CODE;BED_NO:BED_NO_DESC;MR_NO:MR_NO;PAT_NAME:PAT_NAME;SEX:SEX_CODE;HEIGHT:HEIGHT;WEIGHT:WEIGHT;YELLOW:YELLOW_SIGN;ADM_DATE:IN_DATE:Timestamp;YJJ_PRICE:TOTAL_BILPAY;YJYE_PRICE:CUR_AMT;GREED_PRICE:GREENPATH_VALUE;VC_CODE:VS_DR_CODE;CASE_NO:CASE_NO;IPD_NO:IPD_NO;TOTAL_AMT:TOTAL_AMT;SERVICE_LEVELIN:SERVICE_LEVEL;CLNCPATH_CODE:CLNCPATH_CODE;CTZ_CODE:CTZ1_CODE;AGE");
			// 人数计算
			int personNum = parm.getCount("PAT_NAME");
			this.setValue("PRESON_NUM", personNum);
		} else {
			// 出院
			TParm actionParm = this.filterTParmData(parm);
			// out("出院数据"+actionParm);
			// 设置TABLE数据(出院)
			callFunction("UI|" + TABLE + "|setParmValue", actionParm);

			//add by huangtt 20161103 添加执行颜色 start

			if (this.getRunFlg().equals("ODI")) {
				TTable table = this.getTTable("TABLE");
				Color RED = new Color(255, 0, 0);
				for (int i = 0; i < actionParm.getCount("SEALED_STATUS"); i++) {
					if(actionParm.getValue("SEALED_STATUS", i).equals("2") || 
							actionParm.getValue("SEALED_STATUS", i).equals("3")){

						table.setRowColor(i, RED);

					}else{
						table.setRowColor(i, null);
					}
				}
			}

			//add by huangtt 20161103 添加执行颜色 end

			// 设置查询数据和页面显示数据对应
			TTable tab = (TTable) this.callFunction("UI|" + TABLE + "|getThis");
			tab
			.setModifyTag("DEPT_CODEOUT:DEPT_CODE;STATION_CODEOUT:STATION_CODE;MR_NOOUT:MR_NO;PAT_NAMEOUT:PAT_NAME;VC_CODEOUT:VS_DR_CODE;CASE_NOOUT:CASE_NO;IPD_NOOUT:IPD_NO;SERVICE_LEVELOUT:SERVICE_LEVEL;CLNCPATH_CODE_OUT:CLNCPATH_CODE;BILL_STATUS;AGEOUT:AGE");
			// 人数计算PERSON_NUMOUT
			int personNum = parm.getCount("PAT_NAME");
			this.setValue("PERSON_NUMOUT", personNum);
		}
	}

	/**
	 * 过滤查询到的病患基本信息数据住院用于计算年龄和住院天数来放入到查询出来的TParm中
	 * 
	 * @param parm
	 *            TParm 需要过滤的数据
	 * @return TParm
	 */
	public TParm filterTParmData(TParm parm) {
		// System.out.println("过滤TABLE资料"+parm);
		/*
		 * 利用循环来计算此数据中的生日算年龄，入院日期算住院天数
		 * (生日字段:SYS_PATINFO.BIRTH_DATE对应KEY(AGE),入院日期字段
		 * :ADM_INP.IN_DATE对应KEY(DAYNUM))
		 */
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// 返回的行数
		int rowCount = parm.getCount("PAT_NAME");
		for (int i = 0; i < rowCount; i++) {
			Timestamp temp = parm.getTimestamp("BIRTH_DATE", i) == null ? sysDate
					: parm.getTimestamp("BIRTH_DATE", i);
			// 计算年龄
			String age = "0";
			if (parm.getTimestamp("IN_DATE", i) != null){
				//				age = OdiUtil.getInstance().showAge(temp,
				//						parm.getTimestamp("IN_DATE", i));

				//				this.messageBox(temp+"");
				//				this.messageBox(SystemTool.getInstance().getDate()+"");
				age = OdiUtil.getInstance().showAge(temp,
						SystemTool.getInstance().getDate());//传入系统时间  20170807 machao
			}
			else
				age = "";
			parm.addData("AGE", age);
			// 计算住院天数
			Timestamp tp = parm.getTimestamp("DS_DATE", i);
			if (tp == null) {
				int days = 0;
				if (parm.getTimestamp("IN_DATE", i) == null) {
					parm.addData("DAYNUM", "");
				} else {
					days = StringTool.getDateDiffer(StringTool.setTime(sysDate,
							"00:00:00"), StringTool.setTime(parm.getTimestamp(
									"IN_DATE", i), "00:00:00"));
					parm.addData("DAYNUM", days == 0 ? 1 : days);
				}
			} else {
				int days = 0;
				if (parm.getTimestamp("IN_DATE", i) == null) {
					parm.addData("DAYNUM", "");
				} else {
					// ===============modify by chenxi 20120703 start
					days = StringTool.getDateDiffer(StringTool.setTime(parm
							.getTimestamp("DS_DATE", i), "00:00:00"),
							StringTool.setTime(parm.getTimestamp("IN_DATE", i),
									"00:00:00"));
					// =========== modify by chenxi 20120703 stop
					parm.addData("DAYNUM", days == 0 ? 1 : days);
				}
			}
		}
		return parm;
	}

	/**
	 * 得到住院医生站的查询参数
	 * 
	 * @param type
	 *            String IN为在院;OUT为出院
	 * @return TParm
	 */
	public TParm getQueryData(String type) {
		TParm result = new TParm();

		/*modified by WangQing 20170427 start*/
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("病案号" + mrNo + " 已合并至 " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
		}
		result.setData("TYPE", type);// 在院/出院
		/*modified by WangQing 20170428 end*/

		if (type.equals("IN")) {
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// 科室
				result.setData("DEPT_CODE", this.getValue("INW_DEPT_CODE"));
				// 病区
				result.setData("STATION_CODE", this
						.getValue("INW_STATION_CODE"));
				// 经治医师
				result.setData("VC_CODE", this.getValue("INW_VC_CODE"));
			} else {
				// 科室
				result.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
				// 病区
				result.setData("STATION_CODE", this.getValue("STATION_CODE"));
				// 经治医师
				result.setData("VC_CODE", this.getValue("VC_CODE"));
			}
			// 病床
			// result.setData("BED_NO",this.getValue("BED_NO"));
			// 病案号
			String mrNo = getValueString("MR_NO");
			if (mrNo.length() > 0) {
				mrNo = PatTool.getInstance().checkMrno(mrNo);
				setValue("MR_NO", mrNo);
				result.setData("MR_NO", mrNo);
			}
			// 住院号
			String ipdNo = getValueString("IPD_NO");
			if (ipdNo.length() > 0) {
				ipdNo = PatTool.getInstance().checkIpdno(ipdNo);
				setValue("IPD_NO", ipdNo);
				result.setData("IPD_NO", ipdNo);
			}
			// 查询得到就诊号SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE
			// MR_NO=<MR_NO>
			TParm action = new TParm();
			if (mrNo.length() != 0) {
				action.setData("MR_NO", mrNo);
			}
			if (ipdNo.length() != 0) {
				action.setData("IPD_NO", ipdNo);
			}
			// String caseNo = getCaseNo(action);
			String sql = " SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE MR_NO='"
					+ mrNo + "'";
			String caseNo = "";
			TParm caseParm = new TParm(this.getDBTool().select(sql));
			if (caseParm.getCount() <= 0) {
				this.messageBox("查询档次就诊号失败" + action.getErrText());
				return caseParm;
			}
			caseNo = caseParm.getValue("CASE_NO", 0);
			result.setData("CASE_NO", caseNo);
			// 住院日期
			result.setData("ADM_DATE", this.getValue("ADM_DATE"));
		} else {
			//			String mrNoOut = getValueString("MR_NOOUT");
			//			if (mrNoOut.length() > 0) {
			//				mrNoOut = PatTool.getInstance().checkMrno(mrNoOut);
			//				setValue("MR_NO", mrNoOut);
			//				result.setData("MR_NO", mrNoOut);
			//			}
			//			String sql = " SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE MR_NO='"
			//				+ mrNoOut + "'";
			//		String caseNo = "";
			//		TParm caseParm = new TParm(this.getDBTool().select(sql));
			//		if (caseParm.getCount() <= 0) {
			//			this.messageBox("查询档次就诊号失败" + caseParm.getErrText());
			//			return caseParm;
			//		}
			//		caseNo = caseParm.getValue("CASE_NO", 0);
			//		result.setData("CASE_NO", caseNo);
			// 得到出院页签数据
			// 住院起始日期
			result.setData("START_DATE", this.getValue("START_DATE"));
			// 住院终止日期
			result.setData("END_DATE", this.getValue("END_DATE"));
			// 出院起始日期
			result.setData("START_DATEOUT", this.getValue("START_DATEOUT"));
			// 出院终止日期
			result.setData("END_DATEOUT", this.getValue("END_DATEOUT"));
			// 科室
			result.setData("DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// 病区
			result.setData("STATION_CODE", this.getValue("STATION_CODEOUT"));
			// 经治医师
			result.setData("VC_CODE", this.getValue("VC_CODEOUT"));
			// 病案号
			String mrNo = getValueString("MR_NOOUT");
			if (mrNo.length() > 0) {
				mrNo = PatTool.getInstance().checkMrno(mrNo);
				setValue("MR_NOOUT", mrNo);
				result.setData("MR_NO", mrNo);
			}
			// 住院号
			String ipdNo = getValueString("IPD_NOOUT");
			if (ipdNo.length() > 0) {
				ipdNo = PatTool.getInstance().checkIpdno(ipdNo);
				setValue("IPD_NOOUT", ipdNo);
				result.setData("IPD_NO", ipdNo);
			}
			// 查询得到就诊号SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE
			// MR_NO=<MR_NO>
			TParm action = new TParm();
			if (mrNo.length() != 0) {
				action.setData("MR_NO", mrNo);
			}
			if (ipdNo.length() != 0) {
				action.setData("IPD_NO", ipdNo);
			}
			action.setData("NOT_DS", "");// modify by wanglong 20120814
			// 原查询出院病患的SQL少条件，因此自定义一个字段来启动此条件（要先在Module的SQL中加一个新条件）
			String caseNo = getCaseNo(action);
			result.setData("CASE_NO", caseNo);
			// 账务状态
			result.setData("BILL_STATUS", this.getValueString("BILL_STATUS"));
			// 入院起迄
			// result.setData("ADM_DATE",this.getValue("ADM_DATEOUT"));
		}
		return result;
	}

	/**
	 * 当前操作者是否是值班医师
	 * 
	 * @return boolean
	 */
	public boolean isKeepWatch() {
		if (!this.getRunFlg().equals("ODI")) {
			return true;
		}
		// 得到当前用户ID
		String userId = Operator.getID();
		// 得到当前病区
		String stationCode = Operator.getStation();
		TParm action = new TParm(this.getDBTool().select(
				"SELECT DR_CODE FROM ODI_DUTYDRLIST WHERE STATION_CODE='"
						+ stationCode + "' AND DR_CODE='" + userId + "'"));
		// System.out.println("isKeepWatch:"+action.getCount());
		if (action.getInt("ACTION", "COUNT") == 0) {
			// 设置医师
			this.setValue("VC_CODE", Operator.getID());
			// 设置只能是当前医师编辑
			this.callFunction("UI|VC_CODE|setEnabled", false);
			return false;
		}
		// 设置医师
		this.setValue("VC_CODE", Operator.getID());
		// 设置只能是当前医师编辑
		this.callFunction("UI|VC_CODE|setEnabled", true);
		return true;
	}

	/**
	 * 得到CASE_NO
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String getCaseNo(TParm parm) {
		String caseNo = "";
		// 检核
		if (!parm.existData("MR_NO") && !parm.existData("IPD_NO")) {
			return caseNo;
		}
		TParm action = OdiMainTool.getInstance().queryPatCaseNo(parm);
		if (action.getErrCode() < 0) {
			this.messageBox(action.getErrText());
			return caseNo;
		}
		// System.out.println("返回个数:" + action.getInt("ACTION", "COUNT"));
		if (action.getInt("ACTION", "COUNT") > 1) {
			// 调用其他窗体查询CASE_NO
			action.setData("SYSTEM_CODE", "ODI");
			Object obj = this.openDialog("%ROOT%\\config\\odi\\PatInfoUI.x",
					action);
			if (obj != null) {
				TParm actionParm = (TParm) obj;
				caseNo = actionParm.getValue("CASE_NO");
			}
		} else {
			caseNo = action.getValue("CASE_NO", 0);
		}
		return caseNo;
	}

	/**
	 * 得到PANEL
	 * 
	 * @return TPanel
	 */
	public TPanel getTPanel() {
		return (TPanel) this.getComponent(PANEL);
	}

	/**
	 * 启动子面板
	 * 
	 * @param tag
	 *            String
	 * @param path
	 *            String
	 */
	public void runPane(String tag, String path) {
		// 得到系统参数为空提示
		if (this.getRunFlg().length() == 0) {
			// 请设置系统参数！
			this.messageBox("E0153");
			return;
		}
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态
		int selType = tabPane.getSelectedIndex();
		// 关闭当前工作页面
		onClosePanel();
		// 拿到当前选中行
		int selectRow = (Integer) callFunction("UI|" + TABLE
				+ "|getSelectedRow");
		// 是否有选中
		if (selectRow < 0)
			return;
		// 得到选中行数据
		TParm actionParm = this.getSelectRowData(TABLE);
		TTable table = (TTable) this.getComponent("TABLE");

		//		System.out.println("000+++++=======++++actionParm is ::"+table.getParmMap());
		//		System.out.println("+++++=======++++actionParm is ::"+actionParm);
		// 住院医生站调用
		String mrNo = actionParm.getValue("MR_NO");
		if (mrNo == null || mrNo.length() == 0)
			return;
		// System.out.println("得到选中行数据"+actionParm);
		// 定义CASE_NO
		TParm action = new TParm();
		String caseNo = "";
		int i = 0;
		if (selType == 0) {
			// 在院
			caseNo = this.getValueString("CASE_NO");
			IsICU = SYSBedTool.getInstance().checkIsICU(caseNo);
			// 医生站
			//
			//action.setData("ODI", "DAY_OPE_CODE", actionParm.getData("DAY_OPE_CODE"));
			action.setData("ODI", "DAY_OPE_FLG", actionParm.getData("DAY_OPE_FLG"));
			action.setData("ODI", "CASE_NO", caseNo);
			action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("ODI", "BED_NO", actionParm.getData("BED_NO"));
			action.setData("ODI", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ODI", "MR_NO", this.getValue("MR_NO"));

			// 姓名
			action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
			// 姓名1
			action.setData("ODI", "PAT_NAME1", TMessage.getPy(this
					.getValueString("PAT_NAME")));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODE")
					.toString(), this.getValue("DEPT_CODE").toString());
			// 拿到对应药房
			action.setData("ODI", "ORG_CODE", orgCode);
			action
			.setData("ODI", "STATION_CODE", this
					.getValue("STATION_CODE"));
			action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// 停止划价
			action.setData("ODI", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			action.setData("ODI", "CUR_AMT", actionParm.getData("CUR_AMT"));
			action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE"));
			// 生日
			action.setData("ODI", "BIRTH_DATE", actionParm
					.getData("BIRTH_DATE"));
			// 性别
			action.setData("ODI", "SEX_CODE", actionParm.getData("SEX_CODE"));
			// 邮编
			action.setData("ODI", "POST_CODE", actionParm.getData("POST_CODE"));
			// 地址
			action.setData("ODI", "ADDRESS", actionParm.getData("ADDRESS"));
			// 单位
			action.setData("ODI", "COMPANY_DESC", actionParm
					.getData("COMPANY_DESC"));
			// 电话
			action.setData("ODI", "TEL", actionParm.getData("CELL_PHONE"));
			// 家庭电话
			action.setData("ODI", "TEL1", actionParm.getData("TEL_HOME"));
			// 身份证号
			action.setData("ODI", "IDNO", actionParm.getData("IDNO"));
			// 主诊断
			action.setData("ODI", "MAINDIAG", actionParm.getData("MAINDIAG"));
			// 身份
			action.setData("ODI", "CTZ_CODE", actionParm.getData("CTZ1_CODE"));
			// 保存权限注记
			action.setData("ODI", "SAVE_FLG", true);
			// 最新诊断编码
			action.setData("ODI", "ICD_CODE", actionParm.getData("ICD_CODE"));
			// 最新诊断名称
			action.setData("ODI", "ICD_DESC", actionParm.getData("MAINDIAG"));
			// 合理用药
			action.setData("ODI", "PASS", passIsReady);
			action.setData("ODI", "FORCE", enforcementFlg);
			action.setData("ODI", "WARN", warnFlg);
			if (passIsReady) {
				action.setData("ODI", "passflg", initReasonbledMed());
			} else {
				action.setData("ODI", "passflg", false);
			}
			// 会诊注记
			if ("OIDR".equals(this.getRunFlg())) {
				action.setData("ODI", "OIDRFLG", true);
			} else {
				action.setData("ODI", "OIDRFLG", false);
			}

			/*modified by WangQing 20170428 start*/
			// 教学医生站注记
			if ("T".equals(this.getRunFlg())) {
				action.setData("ODI", "T_FLG", true);
			} else {
				action.setData("ODI", "T_FLG", false);
			}
			/*modified by WangQing 20170428 end*/

			// ICU注记
			action.setData("ODI", "ICU_FLG", IsICU);

			// 护士站
			action.setData("INW", "CASE_NO", caseNo);
			action.setData("INW", "STATION_CODE", this
					.getValue("INW_STATION_CODE"));
			action.setData("INW", "POPEDEM", this.isInwPopedem());
			// 身份1
			action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// 身份2
			action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// 身份3
			action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// 姓名
			action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			// 科室
			action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
			// 住院号
			action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
			// 病案号
			action.setData("INW", "MR_NO", this.getValue("MR_NO"));
			// 病区  yanjing  20140224
			action.setData("INW", "STATION_CODE", this.getValue("STATION_CODE"));
			// 床号  yanjing  20140224
			action.setData("INW", "BED_NO", this.getValue("BED_NO"));
			// 经治医生  yanjing  20140224
			action.setData("INW", "VS_DR_CODE", this.getValue("VC_CODE"));
			// 经治医生  yanjing  20140224
			action.setData("INW", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// 入院时间
			action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
			// 保存权限注记
			action.setData("INW", "SAVE_FLG", true);
			// ICU注记
			action.setData("INW", "ICU_FLG", IsICU);
			// ============pangben 2012-7-9 start 添加临床路径代码显示
			action.setData("INW", "CLNCPATH_CODE", actionParm
					.getData("CLNCPATH_CODE"));

			// 计价
			//			System.out.println("----+++====SCHD_CODE SCHD_CODE is ::"+actionParm.getData("SCHD_CODE"));
			action.setData("IBS", "SCHD_CODE", actionParm.getData("SCHD_CODE"));//临床路径时程
			action.setData("IBS", "CASE_NO", caseNo);
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("IBS", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("IBS", "MR_NO", this.getValue("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAME"));
			// 拿到对应药房
			action.setData("IBS", "ORG_CODE", orgCode);
			action
			.setData("IBS", "STATION_CODE", this
					.getValue("STATION_CODE"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// 停止划价
			action.setData("IBS", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			// 床号
			action.setData("IBS", "BED_NO", actionParm.getData("BED_NO"));
			// 身份1
			action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// 身份2
			action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// 身份3
			action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// 红色警戒
			action.setData("IBS", "RED_SIGN", actionParm.getData("RED_SIGN"));
			// 黄色警戒
			action.setData("IBS", "YELLOW_SIGN", actionParm
					.getData("YELLOW_SIGN"));
			// 绿色通道
			action.setData("IBS", "GREENPATH_VALUE", actionParm
					.getData("GREENPATH_VALUE"));
			// 医疗总费用
			action.setData("IBS", "TOTAL_AMT", actionParm.getData("TOTAL_AMT"));
			// 保存权限注记
			action.setData("IBS", "SAVE_FLG", true);
			action.setData("IBS", "INWLEAD_FLG", ibsFlg);
			// ============pangben 2012-7-9 start 添加临床路径代码显示
			action.setData("IBS", "CLNCPATH_CODE", actionParm
					.getData("CLNCPATH_CODE"));
			// 病案首页
			action.setData("MRO", "CASE_NO", caseNo);
			// 保存权限注记
			action.setData("MRO", "SAVE_FLG", true);
			// ADM
			action.setData("ADM", "CASE_NO", caseNo);
			action.setData("ADM", "MR_NO", this.getValue("MR_NO"));
			action.setData("ADM", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ADM", "ADM_DATE", actionParm.getData("IN_DATE"));
			action.setData("ADM", "ADM_FLG", "Y");
			// 保存权限注记
			action.setData("ADM", "SAVE_FLG", true);
			action.setData("NSS", "CASE_NO", caseNo);
			action.setData("NSS", "MR_NO", this.getValue("MR_NO"));
			// 临床路径准进准出
			action.setData("CLP", "CASE_NO", caseNo);
			action.setData("CLP", "MR_NO", this.getValue("MR_NO"));
			action.setData("CLP", "CLNCPATH_CODE", actionParm
					.getValue("CLNCPATH_CODE"));

			// 预交金

			action.setData("BILPAY", "CASE_NO", caseNo);

		} else {
			// 出院
			//action.setData("ODI", "DAY_OPE_CODE", actionParm.getData("DAY_OPE_CODE"));
			action.setData("ODI", "DAY_OPE_FLG", actionParm.getData("DAY_OPE_FLG"));
			caseNo = this.getValueString("CASE_NOOUT");
			action.setData("ODI", "CASE_NO", caseNo);
			action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODEOUT"));
			action.setData("ODI", "BED_NO", actionParm.getData("BED_NO"));
			action.setData("ODI", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ODI", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAMEOUT"));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODEOUT")
					.toString(), this.getValue("DEPT_CODEOUT").toString());
			// 拿到对应药房
			action.setData("ODI", "ORG_CODE", orgCode);
			action.setData("ODI", "STATION_CODE", this
					.getValue("STATION_CODEOUT"));
			action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// 停止划价
			action.setData("ODI", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			action.setData("ODI", "CUR_AMT", actionParm.getData("CUR_AMT"));
			// 住院日期
			action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE"));
			// 生日
			action.setData("ODI", "BIRTH_DATE", actionParm
					.getData("BIRTH_DATE"));
			// 性别
			action.setData("ODI", "SEX_CODE", actionParm.getData("SEX_CODE"));
			// 邮编
			action.setData("ODI", "POST_CODE", actionParm.getData("POST_CODE"));
			// 地址
			action.setData("ODI", "ADDRESS", actionParm.getData("ADDRESS"));
			// 单位
			action.setData("ODI", "COMPANY_DESC", actionParm
					.getData("COMPANY_DESC"));
			// 电话
			action.setData("ODI", "TEL", actionParm.getData("CELL_PHONE"));
			// 家庭电话
			action.setData("ODI", "TEL1", actionParm.getData("TEL_HOME"));
			// 身份证号
			action.setData("ODI", "IDNO", actionParm.getData("IDNO"));
			// 主诊断
			action.setData("ODI", "MAINDIAG", actionParm.getData("MAINDIAG"));
			// 身份
			action.setData("ODI", "CTZ_CODE", actionParm.getData("CTZ1_CODE"));
			// 保存权限注记
			action.setData("ODI", "SAVE_FLG", false);
			// 最新诊断编码
			action.setData("ODI", "ICD_CODE", actionParm.getData("ICD_CODE"));
			// 最新诊断名称
			action.setData("ODI", "ICD_DESC", actionParm.getData("MAINDIAG"));
			// 合理用药
			action.setData("ODI", "PASS", passIsReady);
			action.setData("ODI", "FORCE", enforcementFlg);
			action.setData("ODI", "WARN", warnFlg);
			if (passIsReady) {
				action.setData("ODI", "passflg", initReasonbledMed());
			} else {
				action.setData("ODI", "passflg", false);
			}
			// 会诊注记
			if ("OIDR".equals(this.getRunFlg())) {
				action.setData("ODI", "OIDRFLG", true);
			} else {
				action.setData("ODI", "OIDRFLG", false);
			}

			/*modified by WangQing 20170428 start 
			 * 教学医生站注记*/
			if ("T".equals(this.getRunFlg())) {
				action.setData("ODI", "T_FLG", true);
			} else {
				action.setData("ODI", "T_FLG", false);
			}
			/*modified by WangQing 20170428 end*/

			// ICU注记
			action.setData("ODI", "ICU_FLG", IsICU);

			// 护士站
			action.setData("INW", "CASE_NO", caseNo);
			action.setData("INW", "STATION_CODE", this
					.getValue("INW_STATION_CODEOUT"));
			action.setData("INW", "POPEDEM", this.isInwPopedem());
			// 身份1
			action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// 身份2
			action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// 身份3
			action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// 姓名
			action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			// 科室
			action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
			// 住院号
			action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
			// 病案号
			action.setData("INW", "MR_NO", actionParm.getData("MR_NO"));
			// 病区  yanjing  20140224
			action.setData("INW", "STATION_CODE", this.getValue("STATION_CODE"));
			// 床号  yanjing  20140224
			action.setData("INW", "BED_NO", this.getValue("BED_NO"));
			// 入院时间
			action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
			// 保存权限注记
			action.setData("INW", "SAVE_FLG", false);
			// 计价
			action.setData("IBS", "SCHD_CODE", actionParm.getData("SCHD_CODE"));//临床路径时程
			action.setData("IBS", "CASE_NO", caseNo);
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODEOUT"));
			action.setData("IBS", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("IBS", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAMEOUT"));
			action.setData("IBS", "INWLEAD_FLG", ibsFlg);//住院计价 护士长权限
			// 拿到对应药房
			action.setData("IBS", "ORG_CODE", orgCode);
			action.setData("IBS", "STATION_CODE", this
					.getValue("STATION_CODEOUT"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// 停止划价
			action.setData("IBS", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			// 床号
			action.setData("IBS", "BED_NO", actionParm.getData("BED_NO"));
			// 身份1
			action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// 身份2
			action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// 身份3
			action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// 红色警戒
			action.setData("IBS", "RED_SIGN", actionParm.getData("RED_SIGN"));
			// 黄色警戒
			action.setData("IBS", "YELLOW_SIGN", actionParm
					.getData("YELLOW_SIGN"));
			// 医疗总费用
			action.setData("IBS", "TOTAL_AMT", actionParm.getData("TOTAL_AMT"));
			// 保存权限注记
			action.setData("IBS", "SAVE_FLG", false);
			action.setData("IBS", "CLNCPATH_CODE", actionParm
					.getData("CLNCPATH_CODE"));//=====yanjing 住院计价出院页签初始化报错
			// 病案首页
			action.setData("MRO", "CASE_NO", caseNo);
			// 保存权限注记
			action.setData("MRO", "SAVE_FLG", false);
			// ADM
			action.setData("ADM", "CASE_NO", caseNo);
			action.setData("ADM", "MR_NO", this.getValue("MR_NOOUT"));
			action.setData("ADM", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ADM", "ADM_DATE", actionParm.getData("IN_DATE"));
			action.setData("ADM", "ADM_FLG", "N");
			// 保存权限注记
			action.setData("ADM", "SAVE_FLG", false);
			action.setData("NSS", "CASE_NO", caseNo);
			action.setData("NSS", "MR_NO", this.getValue("MR_NOOUT"));
			// 临床路径准进准出
			action.setData("CLP", "CASE_NO", caseNo);
			action.setData("CLP", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("CLP", "CLNCPATH_CODE", actionParm
					.getValue("CLNCPATH_CODE"));
			// 预交金
			action.setData("BILPAY", "CASE_NO", caseNo);
		}

		// System.out.println("path"+path+"TParm"+action);
		// $$ modified by lx 2012/04/06
		// 住院查询(PDF整理)
		if (path.indexOf("ODIDocQuery") != -1) {
			this.openWindow("%ROOT%\\config\\ODI\\ODIDocQuery.x", action);
		} else {
			// 一期临床登录传参
			if (this.getPopedem("PIC")) {
				action.setData("INW", "ROLE_TYPE", "PIC");
			}
			////machao start 20170503 判断是否是从感染页面调入 感染跳入会诊
			Object obj = this.getParameter();
			if(obj instanceof TParm){
				//this.messageBox(((TParm)obj).getValue("INF")+"");
				if(!StringUtil.isNullString(((TParm)obj).getValue("INF"))){
					//obj = ((TParm)obj).getValue("INF").toString();
					action.setData("INF", "OIDR");
					action.setData("ODI", "MR_NO", ((TParm)this.getParameter()).getValue("MR_NO"));
					action.setData("ODI", "CASE_NO", ((TParm)this.getParameter()).getValue("CASE_NO"));
				}			
			}
			//end machao 20170504
			System.out.println("qqq1:"+action);
			getTPanel().addItem(tag, "%ROOT%\\config\\" + path, action, false);
			workPanelTag = tag;
			tabPane.setEnabled(false);
			((TTable) this.getComponent(TABLE)).setVisible(false);
		}
		//
	}

	/**
	 * 拿到对应药房
	 * 
	 * @param stationCode
	 *            String
	 * @param deptCode
	 *            String
	 * @return String
	 */
	public String getOrgCode(String stationCode, String deptCode) {
		TParm parm = new TParm(this.getDBTool().select(
				"SELECT ORG_CODE FROM SYS_STATION WHERE STATION_CODE='"
						+ stationCode + "'"));
		if (parm.getInt("ACTION", "COUNT") == 0) {
			return "";
		}
		return parm.getValue("ORG_CODE", 0);
	}

	/**
	 * 
	 * @param tag
	 *            String
	 * @param path
	 *            String
	 * @param parm
	 *            TParm
	 */
	public void runPaneSocketInwCheck(String tag, String path, TParm parm) {
		// this.messageBox(""+parm);
		// 关闭当前工作页面
		// onClosePanel();
		// 得到选中行数据
		TParm actionParm = this.getSelectRowData(TABLE);
		TParm action = new TParm();
		action.setData("INW", "CASE_NO", parm.getData("CASE_NO"));
		action.setData("INW", "STATION_CODE", parm.getData("STATION_CODE"));
		action.setData("INW", "POPEDEM", this.isInwPopedem());
		// 身份1
		action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
		// 身份2
		action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
		// 身份3
		action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
		// 姓名
		action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
		// 科室
		action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
		// 住院号
		action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
		// 病案号
		action.setData("INW", "MR_NO", this.getValue("MR_NO"));
		// 入院时间
		action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
		// 保存权限注记
		action.setData("INW", "SAVE_FLG", true);
		action.setData("INW", "LEDUI", parm.getData("LEDUI"));
		onClosePanel();
		getTPanel().addItem(tag, "%ROOT%\\config\\" + path, action, false);
		((TTable) this.getComponent(TABLE)).setVisible(false);
		workPanelTag = tag;
	}

	/**
	 * 得到选中行数据
	 * 
	 * @param tableTag
	 *            String
	 * @return TParm
	 */
	public TParm getSelectRowData(String tableTag) {
		// this.messageBox("===getSelectRowData==");
		int selectRow = (Integer) callFunction("UI|" + tableTag
				+ "|getSelectedRow");
		if (selectRow < 0)
			return new TParm();
		// out("行号" + selectRow);
		//		TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
		TParm parm = this.getTTable(TABLE).getParmValue();
		// out("GRID数据" + parm);
		//		System.out.println("00000 PARM  parmRow is ::"+parm);
		TParm parmRow = parm.getRow(selectRow);
		//		System.out.println("111111parmRow parmRow is ::"+parmRow);
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("INWSHEET")) {
			parmRow.setData("INW_DEPT_CODE", parmRow.getValue("DEPT_CODE"));
			parmRow.setData("INW_STATION_CODE", parmRow
					.getValue("STATION_CODE"));
			parmRow.setData("INW_VC_CODE", parmRow.getValue("VC_CODE"));
		}
		return parmRow;
	}
	/**
	 * 获取临床路径的时程
	 * 
	 */
	private String getSchdCode(String caseNo){
		String schdCode = "";
		String sql = "SELECT SCHD_CODE FROM ADM_INP WHERE CASE_NO = '"+caseNo+"'";
		TParm parm = new TParm(TJDODBTool.getInstance().select(sql));
		if(parm.getCount()>0){
			schdCode =  parm.getValue("SCHD_CODE", 0);
		}
		return schdCode;
	}

	public static void main(String args[]) {
		JavaHisDebug.runFrame("odi\\ODIMainUI.x");

	}

	/**
	 * 关闭事件
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// 清空状态条信息
		callFunction("UI|setSysStatus", "");
		if (!onClosePanel())
			return false;
		// 关闭Socket
		// if (client == null){
		// if (this.getRunFlg().equals("INWCHECK")) {
		// this.messageBox_(2);
		// ledUi.close();
		// }
		// return true;
		// }
		// client.close();
		if (this.getRunFlg().equals("INWCHECK")) {
			ledUi.close();
		}
		// modify shibl 20120317
		if (this.getRunFlg().equals("INWEXE")) {
			//add by yangjj 20151023
			ett.close();

			ledUi1.close();
		}
		return true;
	}

	/**
	 * 关闭工作页面
	 * 
	 * @return boolean
	 */
	public boolean onClosePanel() {
		if (workPanelTag == null || workPanelTag.length() == 0)
			return true;
		TPanel p = (TPanel) getComponent(workPanelTag);
		if (!p.getControl().onClosing())
			return false;
		// 移除当前子UI
		callFunction("UI|" + PANEL + "|removeItem", workPanelTag);
		// 移除子UIMenuBar
		callFunction("UI|removeChildMenuBar");
		// 移除子UIToolBar
		callFunction("UI|removeChildToolBar");
		// 显示UIshowTopMenu
		callFunction("UI|showTopMenu");
		workPanelTag = "";
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 可以编辑
		tabPane.setEnabled(true);
		// 显示TABLE
		((TTable) this.getComponent(TABLE)).setVisible(true);
		return true;
	}

	/**
	 * 返回数据库操作工具
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * 得到系统代码
	 * 
	 * @return String
	 */
	public String getRunFlg() {
		return runFlg;
	}

	public boolean isInwPopedem() {
		return inwPopedem;
	}

	/**
	 * 设置系统代码
	 * 
	 * @param runFlg
	 *            String
	 */
	public void setRunFlg(String runFlg) {
		this.runFlg = runFlg;
	}

	public void setInwPopedem(boolean inwPopedem) {
		this.inwPopedem = inwPopedem;
	}

	/**
	 * 显示当前TOOLBAR
	 */
	public void onShowWindowsEvent() {
		if (workPanelTag == null || workPanelTag.length() == 0) {
			// 显示UIshowTopMenu
			callFunction("UI|showTopMenu");
			return;
		}
		TPanel p = (TPanel) getComponent(workPanelTag);
		p.getControl().callFunction("onShowWindowsFunction");
	}

	public void passcheck() {
		// 合理用药
		passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(
				Operator.getRegion());
		// 预警等级
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		// 是否强制
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		// 合理用药
		if (passIsReady) {
			if (!initReasonbledMed()) {
				this.messageBox("合理用药初始化失败！");
			}
		}
	}

	/**
	 * 初始化合理用药
	 * 
	 * @return boolean
	 */
	public boolean initReasonbledMed() {
		try {
			if (PassDriver.init() != 1) {
				return false;
			}
			// 合理用药初始化
			if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) != 1) {
				return false;
			}
			// 合理用药控制参数
			if (PassDriver.PassSetControlParam(1, 2, 0, 2, 1) != 1) {
				return false;
			}
		} catch (UnsatisfiedLinkError e1) {
			e1.printStackTrace();
			return false;
		} catch (NoClassDefFoundError e2) {
			e2.printStackTrace();
			return false;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 拿到菜单
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * 呼叫体温单接口
	 */
	public void onVitalSign() {
		TParm sumParm = new TParm();
		int row = getTTable("TABLE").getSelectedRow();
		if (row < 0)
			return;
		TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
		String caseNo_ = Rowparm.getValue("CASE_NO");
		String mrNo_ = Rowparm.getValue("MR_NO");
		String station_ = Rowparm.getValue("STATION_CODE");
		String ipdNo_ = Rowparm.getValue("IPD_NO");
		String bedNo_ = Rowparm.getValue("BED_NO");
		sumParm.setData("SUM", "CASE_NO", caseNo_);
		sumParm.setData("SUM", "MR_NO", mrNo_);
		sumParm.setData("SUM", "IPD_NO", ipdNo_);
		sumParm.setData("SUM", "STATION_CODE", station_);
		sumParm.setData("SUM", "BED_NO", bedNo_);
		sumParm.setData("SUM", "ADM_TYPE", "I");
		this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", sumParm, false);

	}

	/**
	 * 呼叫新生儿体温单
	 * 
	 * @param name
	 *            String
	 * @return TPanel
	 */
	public void onNewArrival() {
		TParm sumParm = new TParm();
		int row = getTTable("TABLE").getSelectedRow();
		if (row < 0)
			return;
		TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
		String caseNo_ = Rowparm.getValue("CASE_NO");
		String mrNo_ = Rowparm.getValue("MR_NO");
		String station_ = Rowparm.getValue("STATION_CODE");
		String ipdNo_ = Rowparm.getValue("IPD_NO");
		String bedNo_ = Rowparm.getValue("BED_NO");
		sumParm.setData("SUM", "CASE_NO", caseNo_);
		sumParm.setData("SUM", "MR_NO", mrNo_);
		sumParm.setData("SUM", "IPD_NO", ipdNo_);
		sumParm.setData("SUM", "STATION_CODE", station_);
		sumParm.setData("SUM", "BED_NO", bedNo_);
		sumParm.setData("SUM", "ADM_TYPE", "I");
		this.openDialog("%ROOT%\\config\\sum\\SUMNewArrival.x", sumParm, false);
	}

	/**
	 * PDF整理
	 */
	public void onSubmitPDF() {
		this.runPane("STATIONMAIN", "ODI\\ODIDocQuery.x");
	}

	// $$==============add by lx 2012/06/24 加入排序功能start=============$$//
	/**
	 * 加入表格排序监听方法
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========加入事件===========");
		// System.out.println("++当前结果++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate排序前==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// 调用排序方法;
				// 转换出用户想排序的列和底层数据的列，然后判断 f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// 表格中parm值一致,
				// 1.取paramw值;
				TParm tableData = getTTable("TABLE").getParmValue();
				// 2.转成 vector列名, 行vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.根据点击的列,对vector排序
				// System.out.println("sortColumn===="+sortColumn);
				// 表格排序的列名;
				String tblColumnName = getTTable("TABLE")
						.getParmMap(sortColumn);
				// 转成parm中的列
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// 将排序后的vector转成parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectory转成param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// 行数据->列
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// 行数据;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());

		// add by wangqing 20170629 start
		// 胸痛病患排序后显示错误
		Color red = new Color(255, 0, 0);
		HashMap map = new HashMap();
		HashMap wmap = new HashMap();
		SelectResult sr = new SelectResult(parmTable);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && !"E01".equals(er) && !"".equals(er)){
				map.put(i, CPCColor);

			}
			if("P01".equals(pk)){
				wmap.put(i, red);
			}
		}
		if (map.size() > 0) {
			getTTable("TABLE").setRowColorMap(map);

		}
		if(wmap.size()>0){
			getTTable("TABLE").setRowTextColorMap(wmap);
		}
		// add by wangqing 20170629 end


		getTTable("TABLE").setParmValue(parmTable);
		// System.out.println("排序后===="+parmTable);

	}

	/**
	 * 得到 Vector 值
	 * 
	 * @param group
	 *            String 组名
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int 最大行数
	 * @return Vector
	 */
	private Vector getVector(TParm parm, String group, String names, int size) {
		Vector data = new Vector();
		String nameArray[] = StringTool.parseLine(names, ";");
		if (nameArray.length == 0) {
			return data;
		}
		int count = parm.getCount(group, nameArray[0]);
		if (size > 0 && count > size)
			count = size;
		for (int i = 0; i < count; i++) {
			Vector row = new Vector();
			for (int j = 0; j < nameArray.length; j++) {
				row.add(parm.getData(group, nameArray[j], i));
			}
			data.add(row);
		}
		return data;
	}

	/**
	 * 
	 * @param columnName
	 * @param tblColumnName
	 * @return
	 */
	private int tranParmColIndex(String columnName[], String tblColumnName) {
		int index = 0;
		for (String tmp : columnName) {

			if (tmp.equalsIgnoreCase(tblColumnName)) {
				// System.out.println("tmp相等");
				return index;
			}
			index++;
		}

		return index;
	}

	// $$==============add by lx 2012/06/24 加入排序功能end=============$$//
	// ================= chenxi modify start 20120705
	public boolean isOidrFlg() {
		return oidrFlg;
	}

	public void setOidrFlg(boolean oidrFlg) {
		this.oidrFlg = oidrFlg;
	}

	/**
	 * 临床诊断
	 */

	public void onAddCLNCPath() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("教学医生不允许进行临床诊断");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {

			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		// this.passcheck();
		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("RULE_TYPE", "I");
		TParm pubParm = new TParm();
		pubParm.setData("CASE_NO", parm.getValue("CASE_NO"));
		pubParm.setData("IPD_NO", parm.getValue("IPD_NO"));
		pubParm.setData("MR_NO", parm.getValue("MR_NO"));
		pubParm.setData("DEPT_CODE", parm.getValue("DEPT_CODE"));
		pubParm.setData("DR_CODE", Operator.getID());
		pubParm.setData("RULE_TYPE", "I");
		pubParm.setData("BIRTHDAY", parm.getTimestamp("BIRTH_DATE"));
		pubParm.setData("SEX_CODE", parm.getValue("SEX_CODE"));
		pubParm.setData("IN_DATE", parm.getTimestamp("BIRTH_DATE"));
		// System.out.println("=====puparm====="+parm);
		// parm.setData("BIRTHDAY", );
		// parm.setData("SEX_CODE", getSexCode());
		// parm.setData("IN_DATE", getAdmDate());

		this.openWindow("%ROOT%\\config\\odi\\ODIClnDiagUI.x", pubParm);//modify by wanglong 20140404
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));

	}

	/**
	 * 病例书写
	 */

	public void onAddEmrWrite() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		if (this.isOidrFlg()) {
			parm.setData("RULETYPE", "3");
			// 写入类型(会诊)
			parm.setData("WRITE_TYPE", "OIDR");
		} else if (tFlg) {// modified by wangqing 20170804 规培适用
			parm.setData("RULETYPE", "3");
			// 写入类型(会诊)
			parm.setData("WRITE_TYPE", "T");
		} else {
			parm.setData("RULETYPE", "2");
			// 写入类型(会诊)
			parm.setData("WRITE_TYPE", "");
		}



		parm.setData("ADM_DATE", parm.getData("IN_DATE"));//add by wanglong 20140404
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("ADM_TYPE", "I");
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 病案编目
	 */
	public void onAddBASY() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("SYSTEM_CODE", "ODI");
		parm.setData("USER_TYPE", "2");
		parm.setData("OPEN_USER", Operator.getID());
		this.openDialog("%ROOT%\\config\\mro\\MRORecord.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 病历审查
	 */
	public void onBABM() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("教学医生不能进行病历审查");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		TParm pubParm = new TParm();
		String mrno = parm.getValue("MR_NO");
		String caseno = parm.getValue("CASE_NO");
		pubParm.setData("MRO", "STATE", "ODI");
		pubParm.setData("MRO", "MR_NO", mrno);
		pubParm.setData("MRO", "CASE_NO", caseno);
		this.openDialog("%ROOT%\\config\\mro\\MRO_Chrtvetrec.x", pubParm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 医嘱单
	 */
	public void onSelYZD() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String caseno = parm.getValue("CASE_NO");
		parm.setData("INW", "CASE_NO", caseno);
		this.openDialog("%ROOT%\\config\\inw\\INWOrderSheetPrtAndPreView.x",
				parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 体温表
	 */
	public void onSelTWD() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String caseno = parm.getValue("CASE_NO");
		parm.setData("SUM", "CASE_NO", caseno);
		parm.setData("SUM", "ADM_TYPE", "I");
		//		this.openDialog("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
		// modified by wangqing 2016/11/22
		this.openWindow("%ROOT%\\config\\sum\\SUMVitalSign.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 护理记录
	 */
	public void onHLSel() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("SYSTEM_TYPE", "INW");
		parm.setData("ADM_TYPE", "I");
		parm.setData("RULETYPE", "1");
		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordUI.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 手术麻醉
	 */
	public void onSSMZ() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("教学医生不能进行手术麻醉");
			return;
		}
		/*modified by WangQing 20170428 end*/

		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("ADM_TYPE", "I");
		parm.setData("BOOK_DEPT_CODE", this.getValue("DEPT_CODE"));// 申请部门
		parm.setData("BOOK_DR_CODE", Operator.getID());// 申请人员
		this.openDialog("%ROOT%\\config\\ope\\OPEOpBook.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 备血申请
	 */
	public void onBXResult() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("教学医生不能进行备血申请");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		TParm parmdate = new TParm();
		String caseno = parm.getValue("CASE_NO");
		String deptCode = this.getValueString("DEPT_CODE");
		String mrno = parm.getValue("MR_NO");
		parmdate.setData("ADM_TYPE", "I");
		parmdate.setData("USE_DATE", SystemTool.getInstance().getDate());
		parmdate.setData("DR_CODE", Operator.getID());
		parmdate.setData("CASE_NO", caseno);
		parmdate.setData("MR_NO", mrno);
		parmdate.setData("DEPT_CODE", deptCode);
		this.openDialog("%ROOT%\\config\\bms\\BMSApplyNo.x", parmdate);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 取血申请
	 * add by yangjj 20150427
	 */
	public void onQXResult() {
		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		TParm parmdate = new TParm();
		String caseno = parm.getValue("CASE_NO");
		String deptCode = this.getValueString("DEPT_CODE");
		String mrno = parm.getValue("MR_NO");
		parmdate.setData("ADM_TYPE", "I");
		parmdate.setData("IPD_NO", getValueString("IPD_NO"));
		parmdate.setData("USE_DATE", SystemTool.getInstance().getDate());
		parmdate.setData("DR_CODE", Operator.getID());
		parmdate.setData("CASE_NO", caseno);
		parmdate.setData("MR_NO", mrno);
		parmdate.setData("DEPT_CODE", deptCode);
		this.openDialog("%ROOT%\\config\\bms\\BMSBloodTake.x", parmdate);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));

	}



	/**
	 * 门急诊病历
	 */
	public void onOpdBL() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrno = parm.getValue("MR_NO");
		TParm opdParm = new TParm(this.getDBTool().select(
				"SELECT * FROM REG_PATADM WHERE MR_NO='" + mrno
				+ "' AND (ADM_TYPE='O' OR ADM_TYPE='E')"));
		if (opdParm.getCount() < 0) {
			// 此病患没有门诊病历！
			this.messageBox("E0184");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\OPDInfoUi.x", opdParm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 检验报告
	 */
	public void onLis() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		SystemTool.getInstance().OpenLisWeb(mrNo);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 检查报告
	 */
	public void onRis() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		SystemTool.getInstance().OpenRisWeb(mrNo);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 糖尿病
	 */
	public void onTnb() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		SystemTool.getInstance().OpenTnbWeb(mrNo);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 出院通知
	 * @see 仅限住院病人
	 */
	public void onOutHosp() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("教学医生不能执行出院通知");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// 会诊不许开立医嘱
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170509 -start
		TTable table = (TTable) this.getComponent("TABLE");
		if(table.getSelectedRow()<0){
			this.messageBox("请选择一行数据");
			return;
		}
		// modified by WangQing 20170509 -end
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		//this.messageBox(parm.getValue("MR_NO") + "------" + parm.getValue("CASE_NO"));

		System.out.println("--------there is no love-------");
		System.out.println("------parm="+parm);
		this.openDialog("%ROOT%\\config\\adm\\ADMDrResvOut.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 日间手术变更
	 * 20170331 zhanglei
	 */
	public void onDaySurgery(){
		//获得界面Ttable控件
		TTable table;
		//获得界面Table的控件方法
		table=(TTable)this.getComponent("Table");
		//得到选中行号
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中一行数据");
			return;
		}

		//得到选中行数据
		TParm parm = this.getSelectRowData(TABLE);
		this.openDialog("%ROOT%\\config\\adm\\ADMDaySurgery.x", parm);
		//更新修改的数据
		onQuery();
		//this.openWindow("%ROOT%\\config\\adm\\ADMDaySurgery.x", parm);

		//this.messageBox("CASE_NO:" + parm.getValue("CASE_NO") + "MR_NO:" + parm.getValue("MR_NO") +	"DAY_OPE_FLG:" + parm.getValue("DAY_OPE_FLG"));

	}


	/**
	 * 费用查询
	 */
	public void onSelIbs() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String caseno = parm.getValue("CASE_NO");
		String mrno = parm.getValue("MR_NO");
		parm.setData("IBS", "CASE_NO", caseno);
		parm.setData("IBS", "MR_NO", mrno);
		parm.setData("IBS", "TYPE", "ODISTATION");
		parm.setData("IBS", "TYPE", "ODISTATION");
		this.openWindow("%ROOT%\\config\\ibs\\IBSSelOrderm.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 根据在院/出院找病案号
	 */
	public void onInOut() {
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态
		int selType = tabPane.getSelectedIndex();
		switch (selType) {
		case 0:
			MRNO = this.getValueString("MR_NO");
			break;
		case 1:
			MRNO = this.getValueString("MR_NOOUT");
			break;
		}
	}

	// ========================== chenxi modify 20120705 stop

	/**
	 * 预约挂号
	 * 
	 * wangming
	 * 
	 * 2013.11.04
	 * 
	 */
	public void onReg() {
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态 0在院 1出院
		int selType = tabPane.getSelectedIndex();

		if (selType == 1) {
			this.messageBox("请选择在院病人！");
			return;
		}
		String mrNo = this.getValueString("MR_NO");
		if (null == mrNo || mrNo.length() == 0) {
			this.messageBox("请选择病人！");
			return;
		}
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (null == pat) {
			this.messageBox("无此病人！");
			return;
		}
		String nhiNo = pat.getNhiNo();

		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("NHI_NO", nhiNo);// 医保卡号
		parm.setData("ADM_TYPE", "O"); // add by huangtt 20131204
		this.openWindow("%ROOT%\\config\\reg\\REGAdmForDr.x", parm);
	}

	/**
	 * 导出到Xls
	 */
	public void onExport() {// add by wanglong 20121108
		TTable table = getTTable(TABLE);
		int num = table.getHeader().split(";").length;
		String header = "";
		for (int i = 0; i < num; i++) {
			header += table.getParmMap(i) + ";";
		}
		header = header.substring(0, header.length() - 1);
		table.setParmMap(header);
		if (getTTable(TABLE).getRowCount() > 0) {
			TTabbedPane tabPane = (TTabbedPane) this
					.callFunction("UI|TablePane|getThis");
			if (tabPane.getSelectedIndex() == 0) {// 在院页签
				ExportExcelUtil.getInstance().exportExcel(getTTable(TABLE),
						"在院病患信息统计");
			} else {
				ExportExcelUtil.getInstance().exportExcel(getTTable(TABLE),
						"出院病患信息统计");
			}
		}
	}

	// =============== chenxi modify 20130326 START
	/**
	 * radio选择的控制
	 */
	public void onRadioButton() {
		this.initRadioButton();
		TRadioButton name = (TRadioButton) this
				.callFunction("UI|NAMESELECT|getThis");
		TRadioButton id = (TRadioButton) this
				.callFunction("UI|IDSELECT|getThis");
		if (name.isSelected()) {
			this.clearValue("ID");
			callFunction("UI|NAME|setEnabled", true);
		} else if (id.isSelected()) {
			this.clearValue("NAME");
			callFunction("UI|ID|setEnabled", true);
		}
	}

	/**
	 * 设置radio的初始化
	 */
	public void initRadioButton() {
		callFunction("UI|NAME|setEnabled", false);
		callFunction("UI|ID|setEnabled", false);
	}

	/**
	 * 按照病患姓名和身份证号查询信息(出院)
	 */
	public void onQueryForID() {

		/*modified by WangQing 20170428 start*/
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.onQueryNew();
			return;
		}
		/*modified by WangQing 20170428 end*/

		// this.onClear() ;
		TRadioButton name = (TRadioButton) this
				.callFunction("UI|NAMESELECT|getThis");
		TRadioButton id = (TRadioButton) this
				.callFunction("UI|IDSELECT|getThis");
		// 得到出院查询的参数
		TParm queryData = this.getQueryData("OUT");
		// 判断是否是值班医生
		boolean stationFlg = isKeepWatch();
		TParm actionParm = new TParm();
		// 得到查询SQL
		String type = "";
		if (name.isSelected()) {
			type = "NAME";
		} else if (id.isSelected()) {
			type = "ID";
		}
		String sqlStr = createOUTODIQuerySQL(queryData, type, stationFlg);
		if (stationFlg) {
			// 查询病患基本信息
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		} else {
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		}
		// out("在院查询后的数据" + actionParm);
		// 如果查询为空返回
		if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// 清空Table
			callFunction("UI|" + TABLE + "|removeRowAll");
			return;
		}
		// 得到工作页签的TAG并联动查询
		if (workPanelTag.length() != 0) {
			this.queryDataOtherTPane(actionParm, "OUT");
			return;
		}
		// 设置TABLE上的数据
		this.setTableData(actionParm, "OUT");

		/*modified by Eric 20170519 胸痛病患提示*/
		System.out.println("------ENTER_ROUTE="+actionParm.getValue("ENTER_ROUTE", 0));
		System.out.println("------PATH_KIND="+actionParm.getValue("PATH_KIND", 0));

		Color red = new Color(255, 0, 0);
		HashMap maper = new HashMap();
		HashMap mappk = new HashMap();
		TTable table = this.getTTable("TABLE");
		SelectResult sr = new SelectResult(actionParm);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && !"E01".equals(er) && !"".equals(er)){
				maper.put(i, CPCColor);
			}
			if("P01".equals(pk)){
				mappk.put(i, red);
			}
		}
		if (maper.size() > 0) {
			table.setRowColorMap(maper);
		}
		if (mappk.size() > 0) {
			table.setRowTextColorMap(mappk);
		}

		this.clearValue("NAME;ID;BILL_STATUS");
		this
		.clearValue("BED_NOOUT;MR_NOOUT;PAT_NAMEOUT;PERSON_NUMOUT;CASE_NOOUT;IPD_NOOUT;SERVICE_LEVELOUT;STATION_CODEOUT;VC_CODEOUT;CLNCPATH_CODE_OUT");

	}

	/**
	 * 返回按照病患姓名、身份证号查询的sql
	 * 
	 * @param parm
	 * @param type
	 * @param flg
	 * @return
	 */
	public String createOUTODIQuerySQL(TParm parm, String type, boolean flg) {
		String id = "";
		if (type.equals("NAME")) {
			id = getValue("NAME").toString().length() == 0 ? ""
					: " AND (C.PAT_NAME like '%" + getValue("NAME").toString()
					+ "%' " + " OR LOWER (C.PY1)  LIKE '%"
					+ getValue("NAME").toString() + "%' "
					+ " OR  UPPER (C.PY1)  LIKE '%"
					+ getValue("NAME").toString() + "%') ";
		} else if (type.equals("ID")) {
			id = getValue("ID").toString().length() == 0 ? ""
					: " AND C.IDNO like '%" + getValue("ID").toString() + "%' ";
		}
		// 病区
		String stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
				: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE") + "'";
		// 科室

		String deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
				: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		// 账务状态
		String billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
				: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS") + "'";
		String sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
				+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
				+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
				+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
				+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
				+ ",A.DISE_CODE "
				+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E"
				+ " WHERE A.BED_NO=B.BED_NO"
				+ " AND A.MR_NO=C.MR_NO"
				+ " AND A.CANCEL_FLG<>'Y'"
				+ " AND A.DS_DATE IS NOT NULL"
				+ " AND A.CASE_NO=E.CASE_NO(+)"
				+ " AND A.MR_NO = E.MR_NO(+)"
				+ stationStr
				+ " AND A.MAINDIAG = D.ICD_CODE(+)"
				+ deptCode
				+ id + billStatus + " ORDER BY A.BED_NO";
		// System.out.println("sqll====sql====sql===="+sql);
		return sql;

	}

	// =============== chenxi modify 20130326 END 出院病患检索，根据病患姓名、身份证号查询

	/**
	 * 打印Lis合并报告
	 */
	public void onPrintLis() {
		TTable table = ((TTable) this.getComponent(TABLE));
		int row = table.getSelectedRow();
		TParm data = new TParm();
		if (row != -1) {
			data = getTTable("TABLE").getParmValue().getRow(row);
		}else{
			this.messageBox("请选择一个病患！");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", data.getValue("CASE_NO"));
		TParm result = new TParm();
		result = MedSmsTool.getInstance().queryLisReport(parm);

		if (result.getCount("REPORT_DATE") < 0) {
			this.messageBox("无该病患的数据！");
			return;
		}

		String lisType = "";
		lisType += result.getValue("RPTTYPE_CODE", 0) + "，";
		TParm temp = new TParm();

		for (int i = 0; i < result.getCount("REPORT_DATE"); i++) {
			if(!result.getValue("REPORT_DATE", i).equals("")){
				if(result.getValue("REPORT_DATE", i).indexOf(".") > 0){
					result.setData("REPORT_DATE", i, result.getValue("REPORT_DATE", i)
							.substring(0, result.getValue("REPORT_DATE", i).indexOf("."))
							.substring(0, result.getValue("REPORT_DATE", i).lastIndexOf(":")));
				}
			}
			if(!result.getValue("RPTTYPE_CODE", i).equals(result.getValue("RPTTYPE_CODE", i + 1))){
				lisType += result.getValue("RPTTYPE_CODE", i + 1) + "，";
				temp = getEmptyTParm(result.getValue("RPTTYPE_CODE", i + 1));
				result.setRowData(i + 1, temp);
			}
		}

		temp = getEmptyTParm(result.getValue("RPTTYPE_CODE", 0));
		result.setRowData(0, temp);

		lisType = lisType.substring(0, lisType.lastIndexOf("，") - 1);
		result.addData("SYSTEM", "COLUMNS", "REPORT_DATE");
		result.addData("SYSTEM", "COLUMNS", "TESTITEM_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "OPTITEM_CHN_DESC");
		result.addData("SYSTEM", "COLUMNS", "TEST_VALUE");
		result.addData("SYSTEM", "COLUMNS", "CRTCLLWLMT");
		result.addData("SYSTEM", "COLUMNS", "CKZ");
		result.addData("SYSTEM", "COLUMNS", "TEST_UNIT");
		//		System.out.println("result" + result);
		TParm printParm = new TParm();

		printParm.setData("TABLE", result.getData());
		printParm.setData("MR_NO", "TEXT", data.getValue("MR_NO"));
		printParm.setData("IPD_NO", "TEXT", data.getValue("IPD_NO"));
		printParm.setData("PAT_NAME", "TEXT", data.getValue("PAT_NAME"));
		printParm.setData("AGE", "TEXT", data.getValue("AGE"));
		printParm.setData("SEX", "TEXT", "1".equals(data.getValue("SEX_CODE")) ? "男" : "女");
		printParm.setData("LIS_TYPE", "TEXT", lisType);

		TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		// 0为在院页签的INDEX;1为出院页签的INDEX
		String mrNo = "";
		if (selType == 0) {
			mrNo = this.getValueString("MR_NO");
		}else{
			mrNo = this.getValueString("MR_NOOUT");
		}

		EMRTool emrTool = new EMRTool(data.getValue("CASE_NO"), mrNo , this);
		Object obj = new Object();
		obj = this.openPrintDialog("%ROOT%\\config\\prt\\MED\\MEDLisReport.jhw",
				printParm);
		emrTool.saveEMR(obj, "检验汇总报告", "EMR040002", "EMR04000204", false);
	}

	/**
	 * 得到打印Lis报告的空TParm 用于分割不同检验种类
	 * 
	 * @return
	 */
	public TParm getEmptyTParm(String lisType) {
		TParm parm = new TParm();
		parm.setData("REPORT_DATE", "检验类别：" + lisType);
		parm.setData("TESTITEM_CHN_DESC", "");
		parm.setData("OPTITEM_CHN_DESC", "");
		parm.setData("TEST_VALUE", "");
		parm.setData("CRTCLLWLMT", "");
		parm.setData("CKZ", "");
		parm.setData("TEST_UNIT", "");
		return parm;
	}

	/**
	 * 病历浏览
	 */
	public void onCxShow(){  
		TTable table =(TTable)this.getComponent(TABLE);
		TParm parm = table.getParmValue();
		String mrNo = parm.getValue("MR_NO", table.getSelectedRow());
		String caseNo = parm.getValue("CASE_NO", table.getSelectedRow());
		TParm result = queryPassword();
		String user_password = result.getValue("USER_PASSWORD",0);
		String url = "http://"+getWebServicesIp()+"?userId="+Operator.getID()+"&password="+user_password+"&mrNo="+mrNo+"&caseNo="+caseNo;
		try {
			Runtime.getRuntime().exec(String.valueOf(String.valueOf((new
					StringBuffer("cmd.exe /c start iexplore \"")).append(
							url).append("\""))));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private TParm queryPassword(){
		String sql = "SELECT USER_PASSWORD FROM SYS_OPERATOR WHERE USER_ID = '"+Operator.getID()+"' AND REGION_CODE = '"+Operator.getRegion()+"'";
		return new TParm(TJDODBTool.getInstance().select(sql));
	}

	/**
	 * 获取配置文件
	 * @author shendr
	 */
	public static TConfig getProp() {
		TConfig config=null;
		try{
			config = TConfig
					.getConfig("WEB-INF\\config\\system\\TConfig.x");
		}catch(Exception e){
			e.printStackTrace();
		}
		return config;
	}

	/**
	 * 获取配置文件中的电子病历服务器IP
	 * @return
	 */
	public static String getWebServicesIp() {
		TConfig config = getProp();
		String url = config.getString("", "EMRIP");
		return url;
	}

	/**
	 * CDR
	 */
	public void onQuerySummaryInfo() {
		TParm parm = new TParm();
		TTable table = (TTable)this.getComponent("TABLE");

		int selRow = table.getSelectedRow();
		if (selRow < 0) {
			this.messageBox("请选中要查看的病人信息");
			return;
		}    
		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

		parm.setData("MR_NO", table.getParmValue().getRow(selRow).getValue("MR_NO"));
		// 打开综合查询界面
		tabbedPane.openPanel("CDR_SUMMARY_UI",
				"%ROOT%\\config\\emr\\EMRCdrSummaryInfo.x", parm);
		TComponent component = (TComponent) callFunction(
				"UI|SYSTEM_TAB|findObject", "CDR_SUMMARY_UI");
		if (component != null) {
			tabbedPane.setSelectedComponent((Component) component);
			return;
		}
	}
	/**
	 * 调用NIS护理表单
	 */
	public  void  onNisFormList(){
		TTable table = ((TTable) this.getComponent(TABLE));
		int row = table.getSelectedRow();
		TParm data = new TParm();
		if (row != -1) {
			data = getTTable("TABLE").getParmValue().getRow(row);
		}else{
			this.messageBox("请选择一个病患！");
			return;
		}
		String caseNo=data.getValue("CASE_NO");
		String mrNo=data.getValue("MR_NO");
		SystemTool.getInstance().OpeNisFormList(caseNo, mrNo); 	
	}
	/**
	 * 
	 * @Title: getClpOdiStationParm
	 * @Description: TODO(临床路径管控入参)
	 * @author pangben 2015-8-17
	 * @param parm
	 * @return
	 * @throws
	 */
	private boolean getClpOdiStationParm(TParm parm)
	{
		TTabbedPane tabPane = (TTabbedPane)
				callFunction("UI|TablePane|getThis", new Object[0]);

		int selType = tabPane.getSelectedIndex();

		int selectRow = ((Integer)callFunction("UI|" + TABLE + 
				"|getSelectedRow", new Object[0])).intValue();

		if (selectRow < 0)
			return false;
		TParm actionParm = getSelectRowData(TABLE);
		if (selType == 0) {
			parm.setData("CASE_NO", getValueString("CASE_NO"));
			parm.setData("MR_NO", getValue("MR_NO"));
			parm.setData("PAT_NAME", getValue("PAT_NAME"));
			parm.setData("CTZ_CODE", actionParm.getData("CTZ1_CODE"));
			parm.setData("DEPT_CODE", getValue("DEPT_CODE"));
		} else if (selType == 1) {
			parm.setData("CASE_NO", getValueString("CASE_NOOUT"));
			parm.setData("MR_NO", actionParm.getData("MR_NO"));
			parm.setData("PAT_NAME", getValue("PAT_NAMEOUT"));
		}
		parm.setData("OPT_USER", Operator.getID());
		parm.setData("OPT_TERM", Operator.getIP());
		parm.setData("REGION_CODE", Operator.getRegion());
		return true;
	}

	/**
	 * 病历书写
	 */
	public void onEmrWrite() {
		TParm parm = new TParm();
		int row = getTTable("TABLE").getSelectedRow();
		if (row < 0)
			return;
		TParm Rowparm = getTTable("TABLE").getParmValue().getRow(row);
		parm.setData("SYSTEM_TYPE", "ODI");
		parm.setData("ADM_TYPE", "I");
		parm.setData("CASE_NO", Rowparm.getValue("CASE_NO"));
		parm.setData("PAT_NAME", Rowparm.getValue("PAT_NAME"));
		parm.setData("MR_NO", this.MRNO);
		parm.setData("IPD_NO", Rowparm.getValue("CASE_NO"));
		parm.setData("ADM_DATE", Rowparm.getValue("ADM_DATE"));
		parm.setData("DEPT_CODE", Rowparm.getValue("DEPT_CODE"));
		parm.setData("STATION_CODE", Rowparm.getValue("STATION_CODE"));
		parm.setData("RULETYPE", "3");
		parm.setData("WRITE_TYPE", "OIDR");

		parm.setData("EMR_DATA_LIST", new TParm());
		parm.addListener("EMR_LISTENER", this, "emrListener");
		parm.addListener("EMR_SAVE_LISTENER", this, "emrSaveListener");
		this.openWindow("%ROOT%\\config\\emr\\TEmrWordNewUI.x", parm);// wanglong

	}

	/**
	 * 血糖报告
	 */
	public void getXTReport(){
		SystemTool.getInstance().OpenTnbWeb(this.MRNO);
	}

	public boolean readTxtFile(String filePath){
		String txt = "";
		try {
			String encoding="GBK";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ //判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),encoding);//考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				while((txt = bufferedReader.readLine()) != null){
					TTable table = (TTable) this.getComponent("TABLE");
					TParm tableParm = table.getParmValue();
					for(int i = 0 ; i < tableParm.getCount() ; i++){
						if((txt.split(":"))[1].equals(tableParm.getValue("MR_NO", i))){
							this.setValue("MR_NO", (txt.split(":"))[1]);
							table.setSelectedRow(i);
							TTable tab = (TTable) this.callFunction("UI|" + TABLE + "|getThis");
							tab.exeModifyClicked(i);
							this.onTableClicke(i);
							this.onTableDoubled(i);
						}
					}

				}
				read.close();
				return true;
			}else{
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return false;

	}

	/** 
	 * 检伤评估表单查看 add by huangtt 20151030
	 */
	public void onErdTriage() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrno = parm.getValue("MR_NO");
		TParm opdParm = new TParm(this.getDBTool().select(
				"SELECT * FROM REG_PATADM WHERE MR_NO='" + mrno
				+ "' AND ADM_TYPE='E' AND TRIAGE_NO IS NOT NULL "));
		if (opdParm.getCount() < 0) {
			// 此病患没有门诊病历！
			this.messageBox("此病患没有检伤评估！");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\ERDInfoUi.x", opdParm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * 生成交接单
	 */
	public void onCreate(){
		TParm action = new TParm();
		TTable table = (TTable)this.getComponent("TABLE");
		int index = table.getSelectedRow();//选中行
		if(index<0){
			this.messageBox("请选择病患");
			return;
		}
		TParm parm = table.getParmValue();
		//	       System.out.println("---parm----------------"+parm);
		action.setData("MR_NO",parm.getValue("MR_NO",index));//病案号
		action.setData("CASE_NO",parm.getValue("CASE_NO",index));//就诊号
		action.setData("PAT_NAME",parm.getValue("PAT_NAME",index));//姓名 
		action.setData("FROM_DEPT",parm.getValue("DEPT_CODE",index));//转出科室
		action.setData("PARM", parm.getRow(index));//用于非病历形式的交接单
		//	       System.out.println("---action----------------"+action);
		action.setData("DEPT_TYPE_FLG","ODI");//用于科室选择界面显示科室标记
		//action.setData("DAY_OPE_CODE", "1".equals(parm.getValue("DAY_OPE_CODE",index)) ? "介入手术":"");
		action.setData("DAY_OPE_FLG", parm.getValue("DAY_OPE_FLG",index));
		this.openWindow("%ROOT%\\config\\odi\\ODITransfertype.x", action); 

	}
	/**
	 * 交接一览表
	 */
	public void onTransfer(){
		TParm action = new TParm();
		TTable table = (TTable)this.getComponent("TABLE");
		int index = table.getSelectedRow();//选中行	        
		if(index<0){

		}	        
		else{
			TParm parm = table.getParmValue();
			//		        System.out.println("---parm----------------"+parm);
			action.setData("MR_NO", parm.getValue("MR_NO",index));//病案号
			action.setData("CASE_NO", parm.getValue("CASE_NO",index));//就诊号	
		}	
		//	        System.out.println("---action----------------"+action);
		this.openWindow("%ROOT%\\config\\inw\\INWTransferSheet.x",action);		
	} 
	/**
	 * 评估一览
	 */
	public void onEvalutionRecordOpen() {
		TTable table = (TTable)this.getComponent("TABLE");
		int row = table.getSelectedRow();//选中行
		if (row < 0){
			this.messageBox("请选择一行数据");
			return;
		}
		TParm parm = new TParm();
		TParm Rowparm = table.getParmValue();
		parm.setData("CASE_NO", Rowparm.getValue("CASE_NO",row));
		parm.setData("PAT_NAME", Rowparm.getValue("PAT_NAME",row));
		parm.setData("MR_NO", Rowparm.getValue("MR_NO",row));
		parm.setData("DEPT_CODE", Rowparm.getValue("DEPT_CODE",row));
		parm.setData("BED_NO_DESC", Rowparm.getValue("BED_NO_DESC",row));
		parm.setData("IPD_NO", Rowparm.getValue("IPD_NO",row));

		this.openWindow("%ROOT%\\config\\sys\\SYSAssessmentList.x", parm);// wanglong

	}

	/**
	 * add by wukai on 20160523
	 * 菜单栏 ->病理类型 当前病人的临床项目
	 */
	public void onPathologyType() {
		//this.messageBox("病理类型");
		int row = getTTable(TABLE).getSelectedRow();
		if(row >= 0) {
			TParm parm = this.getSelectRowData(TABLE);
			this.openDialog("%ROOT%\\config\\adm\\AdmPathology.x", parm);
		} else {
			this.messageBox("请选择一个病患");
		}
	}

	/**
	 * 查阅健检总检报告
	 */
	public void onHrmEmr() {
		int row = getTTable(TABLE).getSelectedRow();
		if (row >= 0) {
			TParm parm = this.getSelectRowData(TABLE);
			String sql = "SELECT OPD_CASE_NO FROM ADM_RESV WHERE IN_CASE_NO = '"
					+ parm.getValue("CASE_NO") + "'";
			TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));

			if (queryParm.getErrCode() < 0) {
				this.messageBox("查询关联健检信息错误");
				err("ERR:" + queryParm.getErrText());
				return;
			}
			queryParm.setData("CASE_NO", queryParm.getValue("OPD_CASE_NO", 0)
					.replace(",", "','"));// 健检就诊号
			queryParm.setData("DEPT_ATTRIBUTE", "04");// 总检
			this.openDialog("%ROOT%\\config\\hrm\\HRMTotViewQuery.x", queryParm);
		} else {
			this.messageBox("请选择一个病患");
		}
	}

	/**
	 * 调阅强生血糖报告(web展现)
	 */
	public void getBgReport() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("请选择一行数据");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("是否解锁 Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// 加锁
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		String caseNo = parm.getValue("CASE_NO");
		// 强生血糖web展现
		TParm result = SystemTool.getInstance().OpenJNJWeb(caseNo);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * add lij 201740502
	 * 双向沟通
	 */
	public void onCommunicate(){

		int rowIndex = ((TTable) this.getComponent("TABLE"))
				.getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("请选择一份病历！");
			return;
		}
		TParm parm = new TParm();
		TParm patInfo = (TParm) callFunction("UI|TABLE|getParmValue");
		parm.setData("CASE_NO", patInfo.getValue("CASE_NO", rowIndex));
		parm.setData("CLASS_NAME", "SEND");
		this.openWindow("%ROOT%\\config\\inf\\INFTwoCommunicate.x", parm);
	}


	/**
	 * 查询SQL
	 * @param queryData
	 * @author WangQing
	 * @return
	 */
	public String createQuerySql(TParm parm){
		//		String sql = "";
		// 住院开始时间
		String startDate = StringTool.getString((Timestamp) parm.getData("START_DATE"), "yyyyMMdd");
		// 住院结束时间
		String endDate = StringTool.getString((Timestamp) parm.getData("END_DATE"), "yyyyMMdd");
		// 出院开始时间
		String startDateOut = StringTool.getString((Timestamp) parm.getData("START_DATEOUT"), "yyyyMMdd");
		// 出院结束时间
		String endDateOut = StringTool.getString((Timestamp) parm.getData("END_DATEOUT"), "yyyyMMdd");
		// 在院/出院
		String type = (String) parm.getData("TYPE");
		// 姓名/id (出院)
		String idOrNameStr = "";
		if(type.equals("OUT")){
			TRadioButton name = (TRadioButton) this
					.callFunction("UI|NAMESELECT|getThis");
			// 得到查询SQL
			String idOrName = name.isSelected()?"NAME":"ID";	

			if (idOrName.equals("NAME")) {
				idOrNameStr = getValue("NAME").toString().length() == 0 ? ""
						: " AND (C.PAT_NAME like '%" + getValue("NAME").toString()
						+ "%' " + " OR LOWER (C.PY1)  LIKE '%"
						+ getValue("NAME").toString() + "%' "
						+ " OR  UPPER (C.PY1)  LIKE '%"
						+ getValue("NAME").toString() + "%') ";
			} else if (type.equals("ID")) {
				idOrNameStr = getValue("ID").toString().length() == 0 ? ""
						: " AND C.IDNO like '%" + getValue("ID").toString() + "%' ";
			}
		}	
		// 科室
		String deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
				: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		// 病区
		String stationCode = parm.getValue("STATION_CODE").length() == 0 ? ""
				: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
				+ "'";
		// 主治医师
		String vsDoctor = parm.getValue("VC_CODE").length() == 0 ? ""
				: " AND A.VS_DR_CODE='" + parm.getValue("VC_CODE") + "'";
		// 入院日期区间or出院日期区间
		String dateStr = type.equals("IN") ? "":" AND (A.DS_DATE BETWEEN TO_DATE('"
				+ startDateOut
				+ "','YYYYMMDD') AND TO_DATE('"
				+ endDateOut
				+ "','YYYYMMDD') "
				+ " OR A.IN_DATE BETWEEN TO_DATE('"
				+ startDate
				+ "','YYYYMMDD') AND TO_DATE('"
				+ endDate
				+ "','YYYYMMDD'))";
		// 区域
		String region = (null != Operator.getRegion() && Operator.getRegion().length() > 0)
				?" AND A.REGION_CODE='" + Operator.getRegion() + "' ":"";
		// 就诊号
		String caseNoStr = (null != parm.getValue("CASE_NO") && parm.getValue("CASE_NO").length()>0)
				?" AND A.CASE_NO='"+parm.getValue("CASE_NO")+"' ":"";	
		// 占床注记ALLO_FLG
		String alloFlg = "";
		// 账务状态
		String billStatus = "";
		// 其他
		String otherStr = "";
		if(type.equals("IN")){
			alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.BED_NO IS NOT NULL AND A.CANCEL_FLG<>'Y' AND B.ALLO_FLG='Y' AND B.BED_STATUS='1' AND A.CASE_NO=B.CASE_NO(+)":"";
			otherStr = " AND A.DS_DATE IS NULL "
					+ " AND B.ACTIVE_FLG='Y' ";
		}else{
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
					+ "'";
			otherStr = " AND A.DS_DATE IS NOT NULL"
					+ " AND A.CANCEL_FLG<>'Y'";
		}
		// 主表ADM_INP 从表SYS_BED,SYS_PATINFO,SYSDIAGNOSIS,MRO_MRV_TECH
		// (+)在左边，表示RIGHT JOIN
		//		sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
		//				+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
		//				+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
		//				+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
		//				+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
		//				+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
		//				+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
		//				+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
		//				+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
		//				//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
		//				+ " , A.DAY_OPE_FLG " //add by huangjw 20170322
		//				+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E "
		//				+ " WHERE A.BED_NO=B.BED_NO(+) "
		//				+ " AND A.MR_NO=C.MR_NO(+) "
		//				+ " AND A.MAINDIAG = D.ICD_CODE(+) "
		//				+ " AND A.CASE_NO=E.CASE_NO(+) "
		//
		//                //+ " AND A.MR_NO = B.MR_NO(+)"
		//                //+ " AND A.MR_NO = E.MR_NO(+) "
		//                + dateStr
		//                + alloFlg
		//                + region
		//                + billStatus
		//                + caseNoStr
		//                + otherStr
		//                + deptCode
		//                + stationCode
		//                + vsDoctor
		//                + idOrNameStr
		//                + " ORDER BY A.BED_NO";

		StringBuffer buffer = new StringBuffer();
		String str = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
				+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
				+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
				+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
				+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
				+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
				+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '有' ELSE '无' END) ALLERGY," 
				+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
				+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
				//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
				+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

				/*modified by Eric 20170519 胸痛病患提示*/
				+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

				/*modified by Eric 20170519 胸痛病患提示*/
				+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E , ADM_RESV AR,REG_PATADM REG"
				+ " WHERE A.BED_NO=B.BED_NO(+) "
				+ " AND A.MR_NO=C.MR_NO(+) "

				 /*modified by Eric 20170519 胸痛病患提示*/
				 + " AND A.CASE_NO=AR.IN_CASE_NO(+)"
				 + " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

				+ " AND A.MAINDIAG = D.ICD_CODE(+) "
				+ " AND A.CASE_NO=E.CASE_NO(+) ";
		buffer.append(str);
		buffer.append(dateStr);
		buffer.append(alloFlg);
		buffer.append(region);
		buffer.append(billStatus);
		buffer.append(caseNoStr);
		buffer.append(otherStr);
		buffer.append(deptCode);
		buffer.append(stationCode);
		buffer.append(vsDoctor);
		buffer.append(idOrNameStr);
		String str2 = " ORDER BY A.BED_NO";
		buffer.append(str2);
		String sql = buffer.toString(); 

		System.out.println("===sql: "+sql);
		return sql;
	}


	/**
	 * @author WangQing
	 * 查询方法
	 */
	public void onQueryNew() {
		//add by yangjj 20151112 如果界面为护士执行界面，则查询c:\javahis\nis\nis.txt文件，读取nis.txt的病患信息
		if("INWSTATIONEXECUTE".equals(workPanelTag)){
			onClosePanel();
			readTxtFile("C:\\JavaHis\\NIS\\nis.txt");
			return;
		}
		// 得到TabbedPane控件
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// 表示页签选择的状态 0：在院 1：出院
		int selType = tabPane.getSelectedIndex();
		/** modified by WangQing 20170427 start*/
		String type = (selType == 0)?"IN":"OUT";// 在院/出院标志
		TParm queryData = this.getQueryData(type);
		String sqlStr = this.createQuerySql(queryData);
		TParm actionParm = new TParm();
		actionParm = new TParm(this.getDBTool().select(sqlStr));		
		if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// 清空Table
			callFunction("UI|" + TABLE + "|removeRowAll");
			return;
		}
		// 得到工作页签的TAG并联动查询
		if (workPanelTag.length() != 0) {
			this.queryDataOtherTPane(actionParm, type);
			return;
		}
		System.out.println("#######actionParm:"+actionParm);
		// 设置TABLE上的数据
		this.setTableData(actionParm, type);

		/*modified by Eric 20170519 胸痛病患提示*/
		System.out.println("------ENTER_ROUTE="+actionParm.getValue("ENTER_ROUTE", 0));
		System.out.println("------PATH_KIND="+actionParm.getValue("PATH_KIND", 0));

		Color red = new Color(255, 0, 0);
		HashMap maper = new HashMap();
		HashMap mappk = new HashMap();
		TTable table = this.getTTable("TABLE");
		SelectResult sr = new SelectResult(actionParm);
		int cnt = sr.size();
		for(int i=0;i<cnt;i++){
			Object er = sr.getRowField(i,"ENTER_ROUTE");
			Object pk = sr.getRowField(i,"PATH_KIND");
			if(er !=null && !"E01".equals(er) && !"".equals(er)){
				maper.put(i, red);
			}
			if("P01".equals(pk)){
				mappk.put(i, CPCColor);
			}
		}
		if (maper.size() > 0) {
			table.setRowTextColorMap(maper);
		}
		if (mappk.size() > 0) {
			table.setRowColorMap(mappk);
		}
	}


	/**
	 * 临时解锁 20170705 yanmm
	 */
	public void onUnlock(){
		TTable table;
		table = (TTable) this.getComponent("Table");
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("请选中一行数据");
			return;
		}
		TParm parm = this.getSelectRowData(TABLE);
		// parm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		this.openDialog("%ROOT%\\config\\adm\\AdmGreenChannelUnlock.x", parm);
		onQuery();
	}


	/**
	 * <p>病患知情同意书签名</p>
	 * 
	 * @author wangqing 20170723
	 */
	public void onSign(){
		TParm parm = new TParm();
		TTable t = (TTable) this.getComponent("TABLE");
		int row = t.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择一行数据！");
			return;
		}
		TParm rowParm = t.getParmValue().getRow(row);
		System.out.println("####rowParm="+rowParm);
		parm.setData("MR_NO", rowParm.getValue("MR_NO"));
		parm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
		this.openWindow("%ROOT%\\config\\emr\\EMRInformedConsentForm.x", parm);
	}

	/**
	 * 介入护理记录
	 * @author wangqing 20180115
	 */
	public void onOpeNursingRecord(){
		TTable t = (TTable) this.getComponent("TABLE");
		int row = t.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择一行数据！");
			return;
		}
		TParm rowParm = t.getParmValue().getRow(row);
		TParm parm = new TParm();		
		parm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
		this.openWindow("%ROOT%\\config\\ope\\OPENursingRecord.x", parm);
	}
	
	/**
	 * 其他报告
	 *   == 20180115 zhanglei add
	 */
	public void getPDFQiTa() {
		TTable t = (TTable) this.getComponent("TABLE");
		int row = t.getSelectedRow();
		if(row < 0){
			this.messageBox("请选择一行数据！");
			return;
		}
		TParm rowParm = t.getParmValue().getRow(row);
		TParm parm = new TParm();
		parm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
		parm.setData("MR_NO", rowParm.getValue("MR_NO"));
//		this.messageBox("CASE_NO " + rowParm.getValue("CASE_NO") + " MR_NO " +  rowParm.getValue("MR_NO"));
		this.openDialog("%ROOT%\\config\\sys\\SYSOpeQiTaPDF.x", parm);
	}

	
	
	
}
