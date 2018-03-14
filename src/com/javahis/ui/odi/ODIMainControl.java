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
 * Title: סԺ����=>סԺҽ��վϵͳ=>סԺ����������Ϣ����
 * </p>
 * 
 * <p>
 * Description: סԺ����������Ϣ����
 * </p>
 * 
 * <p>
 * Copyright: Copyright JavaHis (c) 2009��1��
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
	 * ��ǰ��ҳ��TAG
	 */
	public String workPanelTag = "";
	/**
	 * ����ϵͳ����
	 */
	private String runFlg = "";
	/**
	 * Socket���ͻ�ʿվ����
	 */
	// private SocketLink client;
	/**
	 * ��ʿվȨ��
	 */
	private boolean inwPopedem;
	/**
	 * �����
	 */
	private LEDUI ledUi;
	private LEDEXECUI ledUi1;
	/**
	 * ����Ʋ���
	 */
	private TParm ledParm;
	/**
	 * ������ҩ
	 */
	private boolean passIsReady = false;

	private boolean enforcementFlg = false;

	private int warnFlg;
	private String srceenWidth = "";
	/**
	 * ICUע��
	 */
	private boolean IsICU = false;
	private boolean oidrFlg; // ========= chenxi
	private String MRNO; // ============== CHENXI 20121224
	// $$=============add by lx 2012-07-03 ����������start==================$$//
	private BILComparator compare = new BILComparator();// modify by wanglong
	// 20121128
	private boolean ascending = false;
	private int sortColumn = -1;

	// $$=============add by lx 2012-07-03 ����������end==================$$//
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
	 * ��ѧҽ��վflg
	 */
	boolean tFlg = false;

	// 2017/08/23 ����ҽԺȷ�Ͻ���ʹ���ĵ�ɫ�ɷ�ɫ��Ϊ��ɫ
	private Color CPCColor = Color.GRAY;

	private TComboBox getTComboBox(String tagName) {
		return (TComboBox) getComponent(tagName);
	}

	/**
	 * ��ʼ������
	 */
	public void onInitParameter() {
		// �������� IBS
		// this.setPopedem("deptEnabled",true);
		//		 this.setPopedem("deptAll",true);
		// this.setPopedem("stationEnabled",true);
		// this.setPopedem("stationAll",true);
		// this.setPopedem("odiButtonVisible",true);
		// this.setPopedem("inwCheckVisible",true);
		// this.setPopedem("inwExecuteVisible",true);
		// this.setPopedem("ibsStButVisible",true);
		//		 this.setPopedem("INWLEAD",true);
		// סԺ



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
		//add by yangjj 20151023 ��ʿִ�ж�ʱ��
		ett = new INWExecTimerTask();

		ibsFlg = this.getPopedem("INWLEAD");//סԺ�Ƽ��Ƿ��ǻ�ʿ��Ȩ��
		this.getTMenuItem("card").setEnabled(false);
		// shibl 20120329 add
		this.callFunction("UI|tpr|setVisible", false);
		this.callFunction("UI|newtpr|setVisible", false);
		// TABLE˫���¼�
		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.DOUBLE_CLICKED, this, "onTableDoubled");

		callFunction("UI|" + TABLE + "|addEventListener", TABLE + "->"
				+ TTableEvent.CLICKED, this, "onTableClicke");
		//		this.callFunction("UI|tComboBox_0|addEventListener",TComboBoxEvent.SELECTED, this, "onChangeValue");

		// //ע��TPanel����¼�
		// callFunction("UI|" + PANEL + "|addEventListener",
		// PANEL + "->" + "", this, "onTableClicked");
		// ����ϵͳ����
		Object obj = this.getParameter();
		if("OIDR".equals(obj) || "OIDR;1024".equals(obj)){//�������ʱ��ʾ�������  machao  20170602
			this.setTitle("����");
		}
		//machao start 20170503 �ж��Ƿ��ǴӸ�Ⱦҳ�����
		if(obj instanceof TParm){
			//this.messageBox(((TParm)obj).getValue("INF")+"");
			if(!StringUtil.isNullString(((TParm)obj).getValue("INF"))){
				obj = ((TParm)obj).getValue("INF").toString();
			}			
		}
		//machao end 20170503

		//this.messageBox(obj+"");
		if (obj != null) {
			// $$add by lx 2012/03/19 ����1024*768;
			String strParameter = this.getParameter().toString();
			String sysID = "";
			// ����;���
			if (strParameter.indexOf(";") != -1) {
				sysID = strParameter.split(";")[0];
				this.setSrceenWidth(strParameter.split(";")[1]);
			} else {
				sysID = this.getParameter().toString();
				//machao start 20170503 �ж��Ƿ��ǴӸ�Ⱦҳ����� ��Ⱦ�������
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
				table.setHeader("����,60;����,80;������,100;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,140;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,130,CLNCPATH_CODE;ʱ��,100,SCHD_CODE;������,100,DISE_CODE;סԺ��,100;�������,100,MRO_CHAT_FLG;�������,140,SEALED_STATUS;����ȼ�,140,SERVICE_LEVELOUT;��ɸ,150;����,30;��������,80;�ռ�����,80,boolean");// 2017.0331 zhanglei �����ռ��������
				table.setEnHeader("BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;schdCode;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;SCHD_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;DAY_OPE_FLG;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");// 2017.0331 zhanglei �����ռ��������
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;17,left;18,left");
				this.setTitle("��ѧҽ��վ");
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
					//20151026 wangjc start ɾ��һ��ǰ崻������ļ�
					Thread thread = new Thread(new Runnable() {
						@Override
						public void run() {
							// TODO Auto-generated method stub
							OPDBackupDataToPDFControl opdBackupDataToPDFControl = new OPDBackupDataToPDFControl();
							opdBackupDataToPDFControl.deleteFile();
						}
					});
					thread.start();
					//20151026 wangjc end ɾ��һ��ǰ崻������ļ�
				}
			}
			// ��̬���ز˵������ʹ�õĳ�ʼ��һ��
			this.callFunction("UI|onInitMenu");
			// this.messageBox("=come==");
			this.getTMenuItem("card").setEnabled(false);
			// =========== modify by chenxi

			// ======================= chenxi modify 20130305
			if (sysID != null && sysID.equals("MRO")) {
				TTable table = (TTable) this.getComponent("TABLE");
				table
				.setHeader("������,100;סԺ��,100;����,60;����,80;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,120;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,100,CLNCPATH_CODE;������,100,DISE_CODE;�������,100,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
				table
				.setParmMap("MR_NO;IPD_NO;BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;DISE_CODE;MRO_CHAT_FLG;SERVICE_LEVEL;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				table
				.setColumnHorizontalAlignmentData("2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,right;14,left;15,left;16,left;17,left");
			}
			if(sysID != null && sysID.equals("IBSPAYBILL")){//�ɷ���ҵ�����ӡ����ʽ����--xiongwg20150413
				TTable table = (TTable) this.getComponent("TABLE");
				table.setHeader("����,60;����,80;������,100;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,120;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;���ʽ��,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,100,CLNCPATH_CODE;������,100,DISE_CODE;סԺ��,100;�������,100,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CTZ2_CODE;CUR_AMT;CLNCPATH_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SERVICE_LEVEL;SCHD_CODE;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,left;13,right;14,left;17,left;18,left");
			}
			if(sysID != null && (sysID.equals("ODI")||sysID.equals("OIDR"))){//סԺҽ��վ��ӡ�ʱ�̡�--xiongwg20150413
				TTable table = (TTable) this.getComponent("TABLE");
				table.setHeader("����,60;����,80;������,100;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,140;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,130,CLNCPATH_CODE;ʱ��,100,SCHD_CODE;������,100,DISE_CODE;סԺ��,100;�������,100,MRO_CHAT_FLG;�������,140,SEALED_STATUS;����ȼ�,140,SERVICE_LEVELOUT;��ɸ,150;����,30;��������,80;�ռ�����,80,boolean");// 2017.0331 zhanglei �����ռ��������
				table.setEnHeader("BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;schdCode;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;SCHD_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;DAY_OPE_FLG;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");// 2017.0331 zhanglei �����ռ��������
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;17,left;18,left");
			}
			// ======================== chenxi modify 20130305
		} else {
			// ����
			// this.setRunFlg("IBSPAYBILL");
			this.setRunFlg("ODI");
			// this.setRunFlg("INWCHECK");
			// this.setRunFlg("MRO");
			// this.setRunFlg("OIDR");
			// this.setRunFlg("NSSORDER");
			// this.setRunFlg("NSSCHAR");
		}
		// ��ʼ��Ȩ��
		this.onInitPopeDem();

		/*modified by WangQing 20170428 start*/		
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.callFunction("UI|DEPT_CODE|setEnabled", false);
			this.callFunction("UI|STATION_CODE|setEnabled", true);
			this.callFunction("UI|VC_CODE|setEnabled", true);
		}
		isKeepWatch();
		/*modified by WangQing 20170428 end*/



		// ���
		this.onClear();

		/*modified by WangQing 20170428 start*/
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.setValue("VC_CODE", "");// ����ҽʦ
		}
		/*modified by WangQing 20170428 end*/

		this.setValue("STATION_CODE", Operator.getStation()); // ===CHENXI
		// MODIFY
		// 20130321
		// ������ϵͳ��ʼ��
		this.initSystem();
		// ��ʼ����ѯ
		this.onQuery();
		// ��ʼ��SYS_FEE
		// this.initSysFeeData();
		// ���ñ���
		String s = getConfigString(getRunFlg() + "_Title");
		if (s != null && s.length() > 0)
			this.setTitle(s);	

		// $$=====add by lx 2012/06/24 �������򷽷�start============$$//
		addListener(getTTable("TABLE"));
		// $$=====add by lx 2012/06/24 �������򷽷�end============$$//
		setMenu();


		//machao start 20170503 �ж��Ƿ��ǴӸ�Ⱦҳ����� ��Ⱦ�������
		if(this.getParameter() instanceof TParm){			
			if(!StringUtil.isNullString(((TParm)this.getParameter()).getValue("INF"))){				
				onQueryForMrNo();
			}			
		}
		//machao end 20170503
	}

	/**
	 * ��Ժ·���ı��¼�
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
	 * ���ò˵�
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
	 * ��ӡ��ͷ��
	 */
	// �����caoyong 20130708
	public void onPrintO() {
		onTableMClicked();// ��֤�Ƿ���ѡ��Ҫ��ӡ�Ĳ���
	}

	/**
	 * ��֤�Ƿ���ѡ��Ҫ��ӡ�Ĳ����Լ���ӡ
	 */
	// �����caoyong 20130708
	public void onTableMClicked() {
		TTable table = ((TTable) this.getComponent(TABLE));
		int row = table.getSelectedRow();
		if (row != -1) {
			TParm parm = getTTable("TABLE").getParmValue().getRow(row);// ����ѡ�е���
			TParm date = new TParm();
			if (parm.getValue("DIE_CONDITION").length() != 0
					&& parm.getValue("DIE_CONDITION") != null) {// �ж��Ƿ�����ʳ
				String str = this.Diecondition(parm.getValue("DIE_CONDITION"))
						.getValue("CHN_DESC");
				date.setData("DIE_CONDITION", "TEXT", str);// ��ʳ
			}
			date.setData("BedNo", "TEXT", parm.getValue("BED_NO_DESC"));// �� ��
			date.setData("Dept", "TEXT", this.Dept(parm.getValue("DEPT_CODE"))
					.getValue("DEPT_CHN_DESC"));// �õ���������
			date.setData("Name", "TEXT", parm.getValue("PAT_NAME"));// ����
			date.setData("Age", "TEXT", parm.getValue("AGE"));// ����
			date.setData("Gender", "TEXT", "1"
					.equals(parm.getValue("SEX_CODE")) ? "��" : "Ů");// �Ա�ת��Ϊ����
			date.setData("Admission Date", "TEXT", parm.getValue("IN_DATE")
					.substring(0, 13)
					+ "ʱ");// ��Ժ����
			date.setData("Latest Diagnosis", "TEXT", parm.getValue("MAINDIAG"));// ���
			date.setData("Nurse Level", "TEXT", Nursing(parm
					.getValue("NURSING_CLASS")));// ����ȼ�ת��Ϊ����
			date.setData("MrNo", "TEXT", parm.getValue("MR_NO"));// ������
			date.setData("ALLERGY_NOTE", "TEXT", this.Allergie(
					parm.getValue("MR_NO")).getValue("ALLERGY_NOTE"));// ����ʷ
			this.openPrintWindow("%ROOT%\\config\\prt\\ODI\\Odi_Patinfo.jhw",
					date);
		} else {
			this.messageBox("��ѡ������Ҫ��ӡ�Ĳ���");
		}
	}

	/**
	 * ��������ת��
	 */
	// �����caoyong 20130708
	public String Nursing(String type) {
		String str = "";
		if ("N0".equals(type)) {
			str = "�ؼ�����";
		}
		if ("N1".equals(type)) {
			str = "һ������";
		}
		if ("N2".equals(type)) {
			str = "��������";
		}
		if ("N3".equals(type)) {
			str = "��������";
		}
		return str;
	}

	/**
	 * ȡ�ÿ��Һ���
	 * 
	 * @param dept
	 * @return
	 */
	// �����caoyong 20130708
	public TParm Dept(String dept) {
		String sql = "SELECT DEPT_CHN_DESC FROM SYS_DEPT WHERE DEPT_CODE='"
				+ dept + "'";
		TParm caseParm = new TParm(this.getDBTool().select(sql));
		return caseParm.getRow(0);
	}

	/**
	 * ȡ�ù���ֵ
	 */
	// �����caoyong 20130708
	public TParm Allergie(String mrno) {
		String sql = "SELECT ALLERGY_NOTE FROM OPD_DRUGALLERGY WHERE MR_NO='"
				+ mrno + "'";
		TParm parm = new TParm(this.getDBTool().select(sql));
		return parm.getRow(0);
	}

	/**
	 * �õ���ʳ
	 */
	// �����caoyong 20130708
	public TParm Diecondition(String die) {
		String sql = "select CHN_DESC from SYS_DICTIONARY where GROUP_ID='SYS_DIE_CONDITION' and id='"
				+ die + "'";
		TParm rparm = new TParm(this.getDBTool().select(sql));
		return rparm.getRow(0);
	}

	/**
	 * �����¼�
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
			// ����
			TextFormatSYSOperatorStation h = (TextFormatSYSOperatorStation) this
					.getComponent("INW_STATION_CODE");
			h.setUserID(Operator.getID());
			h.onQuery();
			// ����
			TextFormatSYSDeptForOprt d = (TextFormatSYSDeptForOprt) this
					.getComponent("INW_DEPT_CODE");
			d.setStationCode(this.getValueString("INW_STATION_CODE"));
			d.onQuery();
			// ����ҽʦ
			TextFormatSYSOperator k = (TextFormatSYSOperator) this
					.getComponent("INW_VC_CODE");
			k.setDept(this.getValueString("INW_DEPT_CODE"));
			k.onQuery();
			// ��Ժ����
			TextFormatSYSStation h1 = (TextFormatSYSStation) this
					.getComponent("STATION_CODEOUT");
			h1.setDeptCode(this.getValueString("DEPT_CODEOUT"));
			h1.onQuery();
			// ��Ժ����ҽʦ
			TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
					.getComponent("VC_CODEOUT");
			k1.setDept(this.getValueString("DEPT_CODEOUT"));
			k1.onQuery();
			this.setValue("INW_STATION_CODE", parm.getValue("STATION_CODE"));
			this.setValue("INW_DEPT_CODE", parm.getValue("DEPT_CODE"));
			this.setValue("INW_VC_CODE", parm.getValue("VS_DR_CODE"));
		} else {
			// ����
			TextFormatSYSStation h = (TextFormatSYSStation) this
					.getComponent("STATION_CODE");
			h.setDeptCode(this.getValueString("DEPT_CODE"));
			h.onQuery();
			// ����ҽʦ
			TextFormatSYSOperator k = (TextFormatSYSOperator) this
					.getComponent("VC_CODE");
			k.setDept(this.getValueString("DEPT_CODE"));
			k.onQuery();
			// ��Ժ����
			TextFormatSYSStation h1 = (TextFormatSYSStation) this
					.getComponent("STATION_CODEOUT");
			h1.setDeptCode(this.getValueString("DEPT_CODEOUT"));
			h1.onQuery();
			// ��Ժ����ҽʦ
			TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
					.getComponent("VC_CODEOUT");
			k1.setDept(this.getValueString("DEPT_CODEOUT"));
			k1.onQuery();
		}
	}

	public void initSystem() {
		// ҽ��վ��ʼ��
		if (this.getRunFlg().equals("ODI")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|nis|setVisible", true);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// ����Socket�Ʒ�
		if (this.getRunFlg().equals("IBS")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// �ٴ�·��
		if (this.getRunFlg().equals("CLPMANAGEM")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// �������� add caoyong 20130922
		if (this.getRunFlg().equals("INPAPPLACATION")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// Խ����������ҩ��  yanjing 20140219
		if (this.getRunFlg().equals("OVERCHECK")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
		}
		// ����Socket��ʿվ
		if (this.getRunFlg().equals("INWCHECK")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.callFunction("UI|create|setVisible", false);
			this.callFunction("UI|transfer|setVisible", false);
			// this.messageBox("===INWCHECK===");
			// ��ʿ��ز����� �ɴ򿪴�ͷ��
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
			// ��LEDUI
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
			// ��ʿ��ز����� �ɴ򿪴�ͷ��
			this.getTMenuItem("card").setEnabled(true);
			// modify shibl 20120317
			openLEDOneUI();
			ett.doJob();
		}

		/**
		 * ��ʿվҽ������ӡ
		 */
		if (this.getRunFlg().equals("SHEET")) {
			// ��ʿ��ز����� �ɴ򿪴�ͷ��
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
		// סԺ��������ҳ
		if (this.getRunFlg().equals("ADMMRO")) {
			this.callFunction("UI|tpr|setVisible", false);
			this.callFunction("UI|newtpr|setVisible", false);
			this.setValue("DEPT_CODE", "");
			this.setValue("STATION_CODE", "");
			this.setValue("VC_CODE", "");
		}
	}

	/**
	 * ��ʼ��SYS_FEE
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
	 * ����LEDUI
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
	 * ����LEDUI
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
	 * �õ�����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void onSelStationListenerLed(TParm parm) {
		// System.out.println("parm"+parm);
		this.ledParm = parm;
	}

	/**
	 * ����ѡ���¼�
	 */
	public void onSel() {
		if (this.getValueString("INW_STATION_CODE").length() != 0)
			this.ledParm.runListener("onListenerLed", this
					.getValueString("INW_STATION_CODE"));
	}

	/**
	 * ����ʿվͨѶ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void openInwCheckWindow(TParm parm) {
		// ���û�ʿվѡ�񲡻�ҳ��
		Object obj = this.openDialog("%ROOT%\\config\\odi\\PatInfoUI.x", parm);
		if (obj != null) {
			TParm action = (TParm) obj;
			//action.setData("LEDUI", ledUi);
			// ���û�ʿ��˽���
			//this.runPaneSocketInwCheck("INWSTATIONCHECK",
			//"inw\\INWOrderCheckMain.x", action);
			ledUi.removeMessage(action);
			// modify shibl 20120317
			if (null != action.getValue("FLG")
					|| action.getValue("FLG").equals("Y")) {// �жϻ�ʿվִ�в���
				action.setData("LEDUI", ledUi1);
				// System.out.println("action::::"+action);
				// ���û�ʿִ�н���
				this.runPaneSocketInwExe("INWSTATIONEXECUTE",
						"inw\\INWOrderExecMain.x", action);
			} else {
				action.setData("LEDUI", ledUi);
				// ���û�ʿ��˽���
				this.runPaneSocketInwCheck("INWSTATIONCHECK",
						"inw\\INWOrderCheckMain.x", action);
			}

		}
	}

	/**
	 * ����ʿվͨѶ����
	 * 
	 * @param parm
	 *            TParm
	 */
	public void openInwExecWindow(TParm parm) {
		// ���û�ʿվѡ�񲡻�ҳ��
		TParm p = new TParm();
		parm.setData("STATUS", "EXEC");
		this.openDialog(
				"%ROOT%\\config\\inw\\INWOrderSingleExecQuery.x",p);
	}

	/**
	 * ��ʿվִ�еõ�������ϸ����
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
		// �رյ�ǰ����ҳ��
		// onClosePanel();
		// �õ�ѡ��������
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
		// ����
		TextFormatSYSOperatorStation h = (TextFormatSYSOperatorStation) this
				.getComponent("INW_STATION_CODE");
		h.setUserID(Operator.getID());
		h.onQuery();
		// ����
		TextFormatSYSDeptForOprt d = (TextFormatSYSDeptForOprt) this
				.getComponent("INW_DEPT_CODE");
		d.setStationCode(this.getValueString("INW_STATION_CODE"));
		d.onQuery();
		// ����ҽʦ
		TextFormatSYSOperator k = (TextFormatSYSOperator) this
				.getComponent("INW_VC_CODE");
		k.setDept(this.getValueString("INW_DEPT_CODE"));
		k.onQuery();
		// ��Ժ����
		TextFormatSYSStation h1 = (TextFormatSYSStation) this
				.getComponent("STATION_CODEOUT");
		h1.setDeptCode(actionParm.getValue("DEPT_CODE"));
		h1.onQuery();
		// ��Ժ����ҽʦ
		TextFormatSYSOperator k1 = (TextFormatSYSOperator) this
				.getComponent("VC_CODEOUT");
		k1.setDept(actionParm.getValue("DEPT_CODE"));
		k1.onQuery();

		// ���1
		action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
		// ���2
		action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
		// ���3
		action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
		// ����
		action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
		// ����
		action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
		// סԺ��
		action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
		// ������
		action.setData("INW", "MR_NO", this.getValue("MR_NO"));
		// ��Ժʱ��
		action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
		// ����Ȩ��ע��
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
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		TParm actionParm = null;
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		if (selType == 0) {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("IN");
			// out("�õ���Ժ��ѯ�Ĳ���"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")) {
				// �õ���ѯSQL
				String sqlStr = this.creatInwQuerySQL(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// �ж��Ƿ���ֵ��ҽ��
				boolean stationFlg = isKeepWatch();
				// �õ���ѯSQL
				String sqlStr = createODIQuerySQL(queryData, "IN", stationFlg);
				// System.out.println("��ѯsql:" + sqlStr);
				if (stationFlg) {
					// ��ѯ��Ժ����������Ϣ
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				}
			}
			// out("��Ժ��ѯ�������"+actionParm);
			// //�����ѯΪ�շ���
			// if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// //���Table
			// callFunction("UI|" + TABLE + "|removeRowAll");
			// return null;
			// }
			// //�õ�����ҳǩ��TAG��������ѯ
			// if (workPanelTag.length() != 0) {
			// this.queryDataOtherTPane(actionParm, "IN");
			// return null;
			// }
			// //����TABLE�ϵ�����
			// this.setTableData(actionParm, "IN");
		} else {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("OUT");
			// �ж��Ƿ���ֵ��ҽ��
			boolean stationFlg = isKeepWatch();
			// �õ���ѯSQL
			String sqlStr = createODIQuerySQL(queryData, "OUT", stationFlg);
			// System.out.println("��Ժ��ѯSQL��" + sqlStr);
			if (stationFlg) {
				// ��ѯ��Ժ����������Ϣ
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			}
			// out("��Ժ��ѯ�������" + actionParm);
			// //�����ѯΪ�շ���
			// if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// //���Table
			// callFunction("UI|" + TABLE + "|removeRowAll");
			// return;
			// }
			// //�õ�����ҳǩ��TAG��������ѯ
			// if (workPanelTag.length() != 0) {
			// this.queryDataOtherTPane(actionParm, "OUT");
			// return;
			// }
			// //����TABLE�ϵ�����
			// this.setTableData(actionParm, "OUT");
		}
		return actionParm;
	}

	/**
	 * ��ʼ��Ȩ��
	 */
	public void onInitPopeDem() {
		// Ȩ�޿ɷ�ѡ�����
		if (!this.getPopedem("deptEnabled") && !this.getRunFlg().equals("OIDR")) {
			this.callFunction("UI|DEPT_CODE|setEnabled", false);
			this.callFunction("UI|DEPT_CODEOUT|setEnabled", false);
		}
		// Ȩ�޿ɷ�ѡ����
		if (!this.getPopedem("stationEnabled")
				&& !this.getRunFlg().equals("OIDR")) {
			this.callFunction("UI|STATION_CODE|setEnabled", false);
			this.callFunction("UI|STATION_CODEOUT|setEnabled", false);
		}
		// ѡ��ʿվCOMBO������ʿվ
		this.setInwPopedem(this.getPopedem("InwCheckEnabled"));
	}

	/**
	 * ˫���¼�
	 * 
	 * @param row
	 *            int
	 */
	public void onTableDoubled(int row) {
		if (row < 0)
			return;
		//סԺҽ��վ˫������ǰ���ӵ�½����Ϊ�յ�У��---xiongwg20150624
		if ("ODI".equals(this.getRunFlg()) && Operator.getDept().isEmpty()) {
			this.messageBox("��½����Ϊ�գ������µ�½");
			return;
		}
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		TParm parm = this.getSelectRowData(TABLE);
		if (selType == 0) {
			if (parm.getData("IN_DATE") == null
					&& parm.getValue("CASE_NO").length() == 0) {
				// ����û��סԺ��Ϣ��
				// this.messageBox("E0151");
				return;
			}
			// �õ�Ԥ�����������������ʾ��ǿ��
			double rPrice = parm.getDouble("CUR_AMT");
			// ��ɫ����
			double yellowPrice = parm.getDouble("YELLOW_SIGN");
			//machao start 20170503 �ж��Ƿ��ǴӸ�Ⱦҳ����� ��Ⱦ�������
			Boolean flg =true;
			if(this.getParameter() instanceof TParm){
				if(!StringUtil.isNullString(((TParm)this.getParameter()).getValue("INF"))){
					flg = false;
				}	
			}

			//machao end 20170503

			//			// ��ɫ����  yanmm 20170711
			//			if ("ODI".equals(this.getRunFlg())) {	
			//			String sql = "SELECT UNLOCKED_FLG FROM ADM_INP WHERE CASE_NO ='"+parm.getValue("CASE_NO")+"' ";
			//			TParm result = new TParm(TJDODBTool.getInstance().select(sql));
			//			//ȡ��ƾ�����
			//			String sqlSign = "SELECT YELLOW_SIGN,RED_SIGN FROM ODI_SYSPARM ";
			//			TParm resultSign = new TParm(TJDODBTool.getInstance().select(sqlSign));
			//			if(result.getValue("UNLOCKED_FLG",0).equals("Y")){
			//
			//			}else{
			//				if((rPrice <= resultSign.getDouble("RED_SIGN",0) 
			//						|| parm.getValue("STOP_BILL_FLG").equals("Y")) && flg){
			//					this.messageBox("�ò���Ԥ��������!\n �ѱ�����!");
			//					String sqlSBF = "UPDATE ADM_INP SET STOP_BILL_FLG='Y',UNLOCKED_FLG='N' "
			//							+ "WHERE CASE_NO='" +parm.getValue("CASE_NO")+ "' ";
			//					TJDODBTool.getInstance().update(sqlSBF);
			//					return;
			//				}
			//			}
			//		}
			//
			if (rPrice <= yellowPrice && flg) {
				if (this.messageBox("��ʾ��Ϣ Tips",
						"Ԥ�������㣡\n Paying insufficient balance gold!",
						this.YES_NO_OPTION) != 0)
					return;
			}
		}
		// �ٴ�·����֤begin
		if (this.getRunFlg().equals("CLPCHECKITEMMAIN")
				|| this.getRunFlg().equals("CLPVARIATION")) {
			if (parm.getValue("CLNCPATH_CODE") == null
					|| "".equals(parm.getValue("CLNCPATH_CODE"))) {
				this.messageBox("�ò���û�н����ٴ�·����������ѡ��");
				return;
			}
		}
		// ��Ժ
		if (selType == 1) {
			if (this.getRunFlg().equals("CLPMANAGEM")) {
				this.messageBox("�ò����Ѿ���Ժ���ܽ����ٴ�·����������ѡ��");
				return;
			}
		}
		// �ٴ�·����֤end
		this.initOtherUi();
	}


	public void initOtherUi() {
		// ҽ��վ
		if ("ODI".equals(this.getRunFlg())) {
			if ("en".equals(this.getLanguage())) {
				this.setTitle("IP Station");
			} else {
				this.setTitle("סԺҽ��վ");
			}
			// �ж��Ƿ����
			if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
				if (this.messageBox("�Ƿ���� Whether to unlock", PatTool
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
				// ����
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");
			}
			passcheck();
			//===========pangben 2015-8-15 ����ٴ�·���ܿ�
			TParm parm = new TParm();
			if (!getClpOdiStationParm(parm)) {
				return;
			}
			//ת�ƺ��ٴ�·��������ҽ��վ������Ҫ�޸�ʱ��
			CLPTool.getInstance().intoClpDuration(parm, this);
			//�ٴ�·�����У�飺1.��������У�� 
			CLPTool.getInstance().onCheckClpOverflow(parm, this);
			// $$==== modified by lx 2012/03/19����1024*768ҽ��վ���� start====$$//
			if (this.getSrceenWidth() != null
					&& this.getSrceenWidth().equals("1024")) {
				// System.out.println("is 1024.");
				this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");
			} else {
				this.runPane("STATIONMAIN", "odi\\ODIStationUI.x");
			}
			// $$==== modified by lx 2012/03/19����1024*768ҽ��վ���� end====$$//

		}
		// ҽ��վ
		if ("OIDR".equals(this.getRunFlg())) {
			// $$==== modified by lx 2012/03/19����1024*768���ﴦ�� start====$$//
			if (this.getSrceenWidth() != null
					&& this.getSrceenWidth().equals("1024")) {
				if ("en".equals(this.getLanguage())) {
					this.setTitle("IP Station");
				} else {
					this.setTitle("����");
				}
				this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");

			} else {
				if ("en".equals(this.getLanguage())) {
					this.setTitle("IP Station");
				} else {
					this.setTitle("����");
				}
				this.runPane("STATIONMAIN", "odi\\ODIStationUI.x");
			}
			// $$==== modified by lx 2012/03/19����1024*768���ﴦ�� end====$$//
		}

		/*modified by WangQing 20170428 start*/
		// ��ѧҽ��վ
		if ("T".equals(this.getRunFlg())) {
			this.setTitle("��ѧҽ��վ");
			this.runPane("STATIONMAIN", "odi\\ODIStationTxUI.x");
		}
		/*modified by WangQing 20170428 end*/

		// ��ʿվ���
		if ("INWCHECK".equals(this.getRunFlg())) {
			this.setTitle("סԺ��ʿվ���");
			this.runPane("INWSTATIONCHECK", "inw\\INWOrderCheckMain.x");
		}
		// ��ʿִ��
		if ("INWEXE".equals(this.getRunFlg())) {
			this.setTitle("סԺ��ʿվִ��");

			this.runPane("INWSTATIONEXECUTE", "inw\\INWOrderExecMain.x");
		}
		// סԺ�Ƽ�
		if ("IBS".equals(this.getRunFlg())) {
			this.setTitle("סԺ�Ƽ�");
			this.runPane("IBSSTATION", "ibs\\IBSOrderm.x");
		}
		// ���ò�ѯ
		if ("IBSQUERYFEE".equals(this.getRunFlg())) {
			this.setTitle("���ò�ѯ");
			this.runPane("IBSQUERYFEESTATION", "ibs\\IBSSelOrderm.x");
		}
		// �ɷ���ҵ
		if ("IBSPAYBILL".equals(this.getRunFlg())) {
			this.setTitle("�ɷ���ҵ");
			if (!checkNo()) {
				// ��δ�������ȿ���!
				this.messageBox("E0014");
				return;
			}
			this.runPane("IBSCUTBILLSTATION", "bil\\BilIBSRecp.x");
		}
		// ��������
		if ("MRO".equals(this.getRunFlg())) {
			this.setTitle("��������");
			this.runPane("MROSTATION", "mro\\MRO_Chrtvetrec.x");
		}
		// ������̬��ѯ
		if ("ADMCHG".equals(this.getRunFlg())) {
			this.setTitle("������̬��ѯ");
			this.runPane("ADMCHGSTATION", "adm\\ADMQueryChgLog.x");
		}
		// ҽ������ӡ
		if ("SHEET".equals(this.getRunFlg())) {
			this.setTitle("ҽ������ӡ");
			this.runPane("INWSHEET", "inw\\INWOrderSheetPrtAndPreView.x");
		}
		// ����
		if ("NSSORDER".equals(this.getRunFlg())) {
			this.setTitle("����");
			this.runPane("NSSORDER", "nss\\NSSOrder.x");
		}
		// ���ͽɷ�
		if ("NSSCHAR".equals(this.getRunFlg())) {
			this.setTitle("���ͽɷ�");
			this.runPane("NSSCHARGE", "nss\\NSSCharge.x");
		}

		// �ٴ�·��׼��׼��
		if ("CLPMANAGEM".equals(this.getRunFlg())) {
			// System.out.println("�����ٴ�·��׼��׼��");
			this.setTitle("�ٴ�·��׼��׼��");
			this.runPane("CLPMANAGEM", "clp\\CLPManagem.x");
		}
		// �ٴ�·���������
		if ("CLPVARIATION".equals(this.getRunFlg())) {
			// System.out.println("�����ٴ�·���������");
			this.setTitle("�ٴ�·���������");
			this.runPane("CLPVARIATION", "clp\\CLPVariation.x");
		}
		// �ٴ�·���ؼ�������Ŀִ��
		if ("CLPCHECKITEMMAIN".equals(this.getRunFlg())) {
			// System.out.println("�ٴ�·���ؼ�������Ŀִ��");
			this.setTitle("�ٴ�·���ؼ�������Ŀִ��");
			this.runPane("CLPCHECKITEMMAIN", "clp\\CLPChkItemMain.x");
		}
		// ת�Ƽ�¼ά��
		if ("TRANSHOSPLOG".equals(this.getRunFlg())) {
			// System.out.println("�ٴ�·���ؼ�������Ŀִ��");
			this.setTitle("ת�Ƽ�¼ά��");
			this.runPane("TRANSHOSPLOG", "adm\\ADM_TRANS_LOGUI.x");
		}
		// ����Ԥ����
		if ("BILPAY".equals(this.getRunFlg())) {
			this.setTitle("Ԥ����");
			this.runPane("BILPAY", "bil\\BILPay.x");
		}
		// סԺ��������ҳ
		if ("ADMMRO".equals(this.getRunFlg())) {
			int row = this.getTTable(TABLE).getSelectedRow();
			TParm parm = this.getTTable(TABLE).getParmValue().getRow(row);
			TParm pubParm = new TParm();
			pubParm.setData("SYSTEM_CODE", "ADM");
			pubParm.setData("MR_NO", parm.getValue("MR_NO"));
			pubParm.setData("CASE_NO", parm.getValue("CASE_NO"));
			// סԺ������(USER_TYPE=1)
			pubParm.setData("USER_TYPE", "1");
			pubParm.setData("OPEN_USER", Operator.getID());
			TParm result = (TParm) this.openDialog(
					"%ROOT%\\config\\mro\\MRORecord.x", pubParm);
		}
		// //����ת����
		// if("ADMTRAN".equals(this.getRunFlg())){
		// this.runPane("ADMTRANSTATION","adm\\ADMWaitTrans.x");
		// }

		// �������� add caoyong 20130922
		if ("INPAPPLACATION".equals(this.getRunFlg())) {
			/*
			 * int row=this.getTTable(TABLE).getSelectedRow(); TParm
			 * parm=this.getTTable(TABLE).getParmValue().getRow(row); TParm
			 * pubParm = new TParm();
			 * pubParm.setData("CASE_NO",parm.getValue("CASE_NO"));
			 * System.out.println
			 * ("nnnnnnnnnnnnnnnn=="+parm.getValue("CASE_NO"));
			 */
			this.setTitle("��������");
			// this.runPane("INPAPPLACATION", "inp\\INPConsApplication.x");
			this.runPane("INPAPPLACATION", "inp\\INPConsApplication.x");
		}
		// Խ����������ҩ��    yanjing  20140219
		if ("OVERCHECK".equals(this.getRunFlg())) {
			int row = this.getTTable(TABLE).getSelectedRow();
			TParm parm = this.getTTable(TABLE).getParmValue().getRow(row);
			TParm pubParm1 = new TParm();
			pubParm1.setData("CASE_NO", parm.getValue("CASE_NO"));
			this.setTitle("Խ����������ҩ��");
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

	// $$==============add by lx 2012/02/07���봲ͷ������==========================$$//
	/**
	 * ��ͷ��
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
			// ��������¼��ȡ������
			// ����
			int currentRow = 0;
			table.setSelectedRow(currentRow);
			onTableClicke(currentRow);
			onTableDoubled(currentRow);
			//
		}

	}

	// $$==============add by lx
	// 2012/02/07���봲ͷ������END==========================$$//

	/**
	 * ������Ϣ
	 */
	public void onPatInfo() {

		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		this.openDialog("%ROOT%\\config\\adm\\AdmPatinfo.x", parm);

	}

	/**
	 * TTablePanel�л��¼�
	 */
	public void onChangedPanel() {
		// this.messageBox("==onChangedPanel==");
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ���Table
		callFunction("UI|" + TABLE + "|removeRowAll");
		// SHIBL 20120425 MODIFY
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// ���ÿ���(��Ժ)
			this.setValue("INW_DEPT_CODE", "");
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("INW_STATION_CODE", Operator.getStation());
			// ����ҽʦ����ʿվ��
			this.setValue("INW_VC_CODE", "");
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		} else {
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODE", Operator.getDept());
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODEOUT", Operator.getDept());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODE", Operator.getStation());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		}
		// Ĭ��������Ժҳǩ��סԺ����
		this.setValue("ADM_DATE", sysDate);
		// Ĭ��������ʼ����
		this.setValue("START_DATE", sysDate);
		// Ĭ��������ֹ����
		this.setValue("END_DATE", StringTool.rollDate(sysDate, 1));
		// Ĭ��������ʼ����
		this.setValue("START_DATEOUT", StringTool.rollDate(sysDate, -7));
		// Ĭ��������ֹ����
		this.setValue("END_DATEOUT", StringTool.rollDate(sysDate, 1));
		// Ĭ�����ó�Ժҳǩ����Ժ����
		// this.setValue("ADM_DATEOUT", SystemTool.getInstance().getDate());
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUM|setEnabled", false);
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUMOUT|setEnabled", false);
		// �����ؿؼ���Ϣ(��Ժ)
		this
		.clearValue("BED_NO;IPD_NO;MR_NO;PAT_NAME;SEX;SERVICE_LEVELIN;WEIGHT;TOTAL_AMT;PAY_INS;YJJ_PRICE;GREED_PRICE;YJYE_PRICE;PRESON_NUM");
		// �����ؿؼ���Ϣ(��Ժ)
		this
		.clearValue("BILL_STATUS;IPD_NOOUT;MR_NOOUT;PAT_NAMEOUT;PERSON_NUMOUT;SERVICE_LEVELOUT");
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		switch (selType) {
		case 0:// TABLE��Ժ
			// modified by WangQing 20170509 �Ѿ���Ժ�Ĳ��˲���ʹ�ó�Ժ֪ͨ
			callFunction("UI|hos|setEnabled", true);

			if ("IBSPAYBILL".equals(this.getRunFlg())) {// �ɷ���ҵ�����ӡ����ʽ����--xiongwg20150413
				this
				.getTTable(TABLE)
				.setHeader(
						"����,60;����,80;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,120;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;���ʽ��,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,100,CLNCPATH_CODE;������,100,DISE_CODE;������,100;סԺ��,100;�������,100,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
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
						"����,60;����,80;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,140;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,130,CLNCPATH_CODE;ʱ��,100,SCHD_CODE;������,100,DISE_CODE;������,100;סԺ��,100;�������,100,MRO_CHAT_FLG;�������,140,SEALED_STATUS;����ȼ�,140,SERVICE_LEVELOUT;��ɸ,150;����,30;��������,80");
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
				// "����,80;����,80;�Ա�,60,SEX;����,80;��Ժ����,120,Timestamp;סԺ����,80;����ҽʦ,100,USER_ID;�������,120;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,100,CLNCPATH_CODE;������,100;סԺ��,100;�������,100,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
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
						"����,60;����,80;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,120;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,100,CLNCPATH_CODE;������,100,DISE_CODE;������,100;סԺ��,100;�������,100,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT;��ɸ,150;����,30;��������,80");
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
				table.setHeader("����,60;����,80;������,100;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,140;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,130,CLNCPATH_CODE;ʱ��,100,SCHD_CODE;������,100,DISE_CODE;סԺ��,100;�������,100,MRO_CHAT_FLG;�������,140,SEALED_STATUS;����ȼ�,140,SERVICE_LEVELOUT;��ɸ,150;����,30;��������,80;�ռ�����,80,boolean");// 2017.0331 zhanglei �����ռ��������
				table.setEnHeader("BedNo;Name;Gender;Age;Admission Date;Days;VsDR;Latest Diagnosis;Nurse Level;Pat Condition;PayType;Deposit Balance;clncPath;schdCode;Single Disease;MrNo;IpdNo;Mr Check;Service Level");
				table.setParmMap("BED_NO_DESC;PAT_NAME;MR_NO;SEX_CODE;AGE;IN_DATE;DAYNUM;VS_DR_CODE;MAINDIAG;NURSING_CLASS;PATIENT_STATUS;CTZ1_CODE;CUR_AMT;CLNCPATH_CODE;SCHD_CODE;DISE_CODE;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;DAY_OPE_FLG;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE ;HEIGHT;WEIGHT;PAY_INS;CASE_NO;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");// 2017.0331 zhanglei �����ռ��������
				table.setColumnHorizontalAlignmentData("0,left;1,left;2,left;3,left;4,left;5,right;6,left;7,left;8,left;9,left;10,left;11,left;12,right;13,left;14,left;17,left;18,left");
			}
			/*modified by WangQing 20170516 end*/
			break;
		case 1:// TABLE��Ժ
			// modified by WangQing 20170509 �Ѿ���Ժ�Ĳ��˲���ʹ�ó�Ժ֪ͨ
			callFunction("UI|hos|setEnabled", false);

			if ("IBSPAYBILL".equals(this.getRunFlg())) {//�ɷ���ҵ�����ӡ����ʽ����--xiongwg20150413
				this
				.getTTable(TABLE)
				.setHeader(
						"����,60;����,80;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;סԺ����,70;����ҽʦ,100,USER_ID;�������,120;����ȼ�,120,NURSING_CLASS;����״̬,120,PATIENT_STATUS;���ʽ,80,CTZ_CODE;���ʽ��,80,CTZ_CODE;Ԥ�������,120,double;�ٴ�·��,100,CLNCPATH_CODE;������,100,DISE_CODE;������,100;סԺ��,100;�������,100,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
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
				// "����,80;����,80;�Ա�,60,SEX;����,80;��Ժ����,120,Timestamp;��Ժ����,120,Timestamp;סԺ����,80;��Ժ���,120;���ʽ,100,CTZ_CODE;����״̬,120,BILL_STATUS;�ٴ�·��,100,CLNCPATH_CODE_OUT;������,100;סԺ��,100;�������,140,MRO_CHAT_FLG;����ȼ�,140,SERVICE_LEVELOUT");
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
						"����,60;����,80;�Ա�,60,SEX;����,40;��Ժ����,80,Timestamp;����ҽʦ,80,USER_ID;��Ժ����,80,Timestamp;סԺ����,70;��Ժ���,120;���ʽ,100,CTZ_CODE;�ܽ��,120,double;����״̬,120,BILL_STATUS;�ٴ�·��,100,CLNCPATH_CODE_OUT;������,100,DISE_CODE;������,100;סԺ��,100;�������,140,MRO_CHAT_FLG;�������,140,SEALED_STATUS;����ȼ�,140,SERVICE_LEVELOUT;��ɸ,150;����,30;��������,80");//add machao 2017/6/26  �����������
				this
				.getTTable(TABLE)
				// add by wanglong 20121115
				.setEnHeader(
						"BedNo;Name;Gender;Age;Admission Date;Latest Diagnosis;Discharge Date;Days;Discharge Diagnosis;PayType;Tot_Amt;Financial Status;CLNCPATH;Single Disease;MrNo;IpdNo;Mr Check;Service Level");//add machao 2017/6/26  �����������
				this
				.getTTable(TABLE)
				// add by wanglong 20121115
				.setParmMap(
						"BED_NO_DESC;PAT_NAME;SEX_CODE;AGE;IN_DATE;VS_DR_CODE;DS_DATE;DAYNUM;MAINDIAG;CTZ1_CODE;TOT_AMT;BILL_STATUS;CLNCPATH_CODE;DISE_CODE;MR_NO;IPD_NO;MRO_CHAT_FLG;SEALED_STATUS;SERVICE_LEVEL;INFECT_SCR_RESULT;ALLERGY;FALL_RISK;TOTAL_AMT;TOTAL_BILPAY;GREENPATH_VALUE;RED_SIGN;YELLOW_SIGN;STOP_BILL_FLG;BED_NO;STATION_CODE;DEPT_CODE;VS_DR_CODE;HEIGHT;WEIGHT;CASE_NOOUT;IN_DATE;BIRTH_DATE;POST_CODE;ADDRESS;COMPANY_DESC;TEL_HOME;IDNO;PAT_NAME1;ICD_CODE");//add machao 2017/6/26  �����������
				this
				.getTTable(TABLE)
				// add by wanglong 20121115
				.setColumnHorizontalAlignmentData(
						"0,left;1,left;2,left;3,left;4,left;5,left;6,left;7,right;8,left;9,left;10,right;11,left;12,left;13,left;14,right;15,right;16,left;17,left;18,left;19,left;20,left;21,left");//add machao 2017/6/26  �����������
			}
			break;
		}
	}

	/**
	 * �õ�TTable
	 * 
	 * @param tag
	 *            String
	 * @return TTable
	 */
	public TTable getTTable(String tag) {
		return (TTable) this.getComponent(tag);
	}

	/**
	 * ���
	 */
	public void onClear() {
		// this.messageBox("���begin");
		// �õ�TabbedPane�ؼ�

		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ���Table
		callFunction("UI|" + TABLE + "|removeRowAll");
		if (this.getRunFlg().equals("INWCHECK")
				|| this.getRunFlg().equals("INWEXE")
				|| this.getRunFlg().equals("SHEET")) {
			// ���ÿ���(��Ժ)
			this.setValue("INW_DEPT_CODE", "");
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("INW_STATION_CODE", Operator.getStation());
			// ����ҽʦ����ʿվ��
			this.setValue("INW_VC_CODE", "");
		} else {
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODE", Operator.getDept());
			// ���ÿ���(��Ժ)
			this.setValue("DEPT_CODEOUT", Operator.getDept());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODE", Operator.getStation());
			// ע�� ��Operator�����ڸ�Ĭ��ֵ(���ò�����Ժ)
			this.setValue("STATION_CODEOUT", Operator.getStation());
		}
		// Ĭ��������Ժҳǩ��סԺ����
		this.setValue("ADM_DATE", sysDate);
		// Ĭ��������ʼ����
		this.setValue("START_DATE", sysDate);
		// Ĭ��������ֹ����
		this.setValue("END_DATE", sysDate);
		// Ĭ�����ó�Ժҳǩ��סԺ����
		this.setValue("ADM_DATEOUT", sysDate);
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUM|setEnabled", false);
		// ���������ɱ༭״̬(��Ժ)
		callFunction("UI|PRESON_NUMOUT|setEnabled", false);
		// ����ҳǩΪ��Ժ
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		// this.messageBox("selType"+selType);

		// // ��ʾҳǩѡ���״̬(��Ժ)
		// tabPane.setSelectedIndex(0);
		// �����ؿؼ���Ϣ(��Ժ)
		// ============================== chenxi modify
		this.clearValue("CLNCPATH_CODE;SCHD_CODE;VC_CODE");
		this.setValue("BILL_STATUS", "");
		// ============================== chenxi modify
		this
		.clearValue("BED_NO;MR_NO;PAT_NAME;SEX;SERVICE_LEVELIN;WEIGHT;YELLOW;PRESON_NUM;YJJ_PRICE;YJYE_PRICE;GREED_PRICE;CASE_NO;STATION_CODE;IPD_NO;TOTAL_AMT;AGE");
		// �����ؿؼ���Ϣ(��Ժ)
		this
		.clearValue("BED_NOOUT;MR_NOOUT;PAT_NAMEOUT;PERSON_NUMOUT;CASE_NOOUT;IPD_NOOUT;SERVICE_LEVELOUT;STATION_CODEOUT;VC_CODEOUT;CLNCPATH_CODE_OUT;AGEOUT;CTZ_CODE");
		// �Ƴ�����ҳ��
		this.onClosePanel();
		// out("���end");
	}

	/**
	 * ������ҳ��������ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 */
	public void queryDataOtherTPane(TParm parm, String type) {
		// System.out.println("������ѯ���"+parm);
		TParm action = new TParm();
		if (workPanelTag.toUpperCase().equals("INWSTATIONCHECK")
				|| workPanelTag.toUpperCase().equals("INWSTATIONEXECUTE")
				|| workPanelTag.toUpperCase().equals("INWSHEET")) {
			if ("IN".equals(type)) {
				// ��Ժ
				action.setData("INW", "CASE_NO", parm.getData("CASE_NO", 0));
				action.setData("INW", "STATION_CODE", parm.getData(
						"STATION_CODE", 0));
				action.setData("INW", "POPEDEM", this.isInwPopedem());
				// ���1
				action
				.setData("INW", "CTZ1_CODE", parm.getData("CTZ1_CODE",
						0));
				// ���2
				action
				.setData("INW", "CTZ2_CODE", parm.getData("CTZ2_CODE",
						0));
				// ���3
				action
				.setData("INW", "CTZ3_CODE", parm.getData("CTZ3_CODE",
						0));
				// ����
				action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			} else {
				// ��Ժ
				action.setData("INW", "CASE_NO", parm.getData("CASE_NO", 0));
				action.setData("INW", "STATION_CODE", parm.getData(
						"STATION_CODE", 0));
				action.setData("INW", "POPEDEM", this.isInwPopedem());
				// ���1
				action
				.setData("INW", "CTZ1_CODE", parm.getData("CTZ1_CODE",
						0));
				// ���2
				action
				.setData("INW", "CTZ2_CODE", parm.getData("CTZ2_CODE",
						0));
				// ���3
				action
				.setData("INW", "CTZ3_CODE", parm.getData("CTZ3_CODE",
						0));
				// ����
				action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			}
			// ���¸�ֵ�����������¼��ص����ж��Ƿ��Ѿ���������Ѿ�������ֻ��ֵ��ʼ������(INW������ֻҪ���ǿ��ַ����Ϳ���)
			getTPanel().addItem(workPanelTag, "INW", action, false);
		}
		// //ҽ��վ
		// if(workPanelTag.toUpperCase().equals("STATIONMAIN")){
		// if ("IN".equals(type)) {
		// //��Ժ
		// action.setData("ODI", "CASE_NO",parm.getData("CASE_NO",0));
		// action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
		// action.setData("ODI", "BED_NO", this.getValue("BED_NO"));
		// action.setData("ODI", "IPD_NO", parm.getData("IPD_NO",0));
		// action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
		// action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
		// String orgCode =
		// this.getOrgCode(this.getValue("STATION_CODE").toString(),this.getValue("DEPT_CODE").toString());
		// //�õ���Ӧҩ��
		// action.setData("ODI", "ORG_CODE", orgCode);
		// action.setData("ODI", "STATION_CODE",this.getValue("STATION_CODE"));
		// action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
		// //ֹͣ����
		// action.setData("ODI", "STOP_BILL_FLG",parm.getData("STOP_BILL_FLG"));
		// }
		// else {
		// //��Ժ
		// action.setData("ODI", "CASE_NO",parm.getData("CASE_NO",0));
		// action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
		// action.setData("ODI", "BED_NO", this.getValue("BED_NO"));
		// action.setData("ODI", "IPD_NO", parm.getData("IPD_NO",0));
		// action.setData("ODI", "MR_NO", this.getValue("MR_NO"));
		// action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
		// String orgCode =
		// this.getOrgCode(this.getValue("STATION_CODE").toString(),this.getValue("DEPT_CODE").toString());
		// //�õ���Ӧҩ��
		// action.setData("ODI", "ORG_CODE", orgCode);
		// action.setData("ODI", "STATION_CODE",this.getValue("STATION_CODE"));
		// action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
		// //ֹͣ����
		// action.setData("ODI", "STOP_BILL_FLG",parm.getData("STOP_BILL_FLG"));
		// }
		// //���¸�ֵ�����������¼��ص����ж��Ƿ��Ѿ���������Ѿ�������ֻ��ֵ��ʼ������(ODI������ֻҪ���ǿ��ַ����Ϳ���)
		// getTPanel().addItem(workPanelTag,"ODI",action,false);
		// }
		// �Ƽ�
		if (workPanelTag.toUpperCase().equals("IBSSTATION")) {
			// ��Ժ
			action.setData("IBS", "SCHD_CODE", parm.getData("SCHD_CODE", 0));//�ٴ�·��ʱ��
			action.setData("IBS", "CASE_NO", parm.getData("CASE_NO", 0));
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("IBS", "IPD_NO", parm.getData("IPD_NO", 0));
			action.setData("IBS", "MR_NO", this.getValue("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAME"));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODE")
					.toString(), this.getValue("DEPT_CODE").toString());
			// �õ���Ӧҩ��
			action.setData("IBS", "ORG_CODE", orgCode);
			action
			.setData("IBS", "STATION_CODE", this
					.getValue("STATION_CODE"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// ֹͣ����
			action.setData("IBS", "STOP_BILL_FLG", parm
					.getData("STOP_BILL_FLG"));
			// ����
			action.setData("IBS", "BED_NO", this.getValue("BED_NO"));
			// ���1
			action.setData("IBS", "CTZ1_CODE", parm.getData("CTZ1_CODE", 0));
			// ���2
			action.setData("IBS", "CTZ2_CODE", parm.getData("CTZ2_CODE", 0));
			// ���3
			action.setData("IBS", "CTZ3_CODE", parm.getData("CTZ3_CODE", 0));
			// ��ɫ����
			action.setData("IBS", "RED_SIGN", parm.getData("RED_SIGN", 0));
			// ��ɫ����
			action
			.setData("IBS", "YELLOW_SIGN", parm.getData("YELLOW_SIGN",
					0));
			// ��ɫͨ��
			action.setData("IBS", "GREENPATH_VALUE", parm.getData(
					"GREENPATH_VALUE", 0));
			// ҽ���ܷ���
			action.setData("IBS", "TOTAL_AMT", parm.getData("TOTAL_AMT", 0));
			getTPanel().addItem(workPanelTag, "IBS", action, false);
		}
		// ������ҳ
		if (workPanelTag.toUpperCase().equals("MROSTATION")) {
			action.setData("MRO", "CASE_NO", parm.getData("CASE_NO", 0));
		}
		// ADM������̬��ѯ
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
		// ��Ԥ�ӽ�
		if (workPanelTag.toUpperCase().equals("BILPAY")) {
			action.setData("BILPAY", "CASE_NO", parm.getData("CASE_NO", 0));
			getTPanel().addItem(workPanelTag, "BILPAY", action, false);
		}
	}

	/**
	 * סԺҽ��վ��ѯ
	 */
	public void onQuery() {

		/*modified by WangQing 20170428 start*/
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.onQueryNew();
			return;
		}
		/*modified by WangQing 20170428 end*/

		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		if (selType == 0) {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("IN");
			TParm actionParm = new TParm();
			// out("�õ���Ժ��ѯ�Ĳ���"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// �õ���ѯSQL
				String sqlStr = creatInwQuerySQL(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// �ж��Ƿ���ֵ��ҽ��
				boolean stationFlg = isKeepWatch();
				// �õ���ѯSQL
				String sqlStr = createODIQuerySQL(queryData, "IN", stationFlg);
				System.out.println("��ѯsql:" + sqlStr);
				if (stationFlg) {
					// ��ѯ��Ժ����������Ϣ
					actionParm = new TParm(this.getDBTool().select(sqlStr));
					//this.messageBox("1:" + actionParm.getValue("DAY_OPE_FLG"));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
					//this.messageBox("2:" + actionParm.getValue("DAY_OPE_FLG"));
				}
				//				System.out.println("======sqlStr sqlStr is :::"+sqlStr);
			}
			// out("��Ժ��ѯ�������"+actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "IN");
				return;
			}

			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "IN");

			/*modified by Eric 20170519 ��ʹ������ʾ*/
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
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("OUT");
			// �ж��Ƿ���ֵ��ҽ��
			boolean stationFlg = isKeepWatch();
			TParm actionParm = new TParm();
			// �õ���ѯSQL
			String sqlStr = createODIQuerySQL(queryData, "OUT", stationFlg);//
			//			 System.out.println("��Ժ��ѯSQL��" + sqlStr);
			if (stationFlg) {
				// ��ѯ��Ժ����������Ϣ
				actionParm = new TParm(this.getDBTool().select(sqlStr));
				//this.messageBox(actionParm.getValue("DAY_OPE_FLG"));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
				//this.messageBox(actionParm.getValue("DAY_OPE_FLG"));
			}
			// out("��Ժ��ѯ�������" + actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "OUT");
				return;
			}
			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "OUT");

			/*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * ���ݲ����Ų�ѯ
	 */
	public void onQueryForMrNo() {
		//		/*modified by WangQing 20170428 start*/
		//		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
		//			this.onQueryNew();
		//			return;
		//		}
		//		/*modified by WangQing 20170428 end*/

		//machao start 20170503 �ж��Ƿ��ǴӸ�Ⱦҳ����� ��Ⱦ�������
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

		// modify by huangtt 20160928 EMPI���߲�����ʾ start
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
		}
		//				

		// modify by huangtt 20160928 EMPI���߲�����ʾ start


		//add by yangjj 20151112 �������Ϊ��ʿִ�н��棬���ѯc:\javahis\nis\nis.txt�ļ�����ȡnis.txt�Ĳ�����Ϣ
		if("INWSTATIONEXECUTE".equals(workPanelTag)){
			onClosePanel();
			readTxtFile("C:\\JavaHis\\NIS\\nis.txt");
			return;
		}


		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");

		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
		if (selType == 0) {
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("IN");
			TParm actionParm = new TParm();
			// out("�õ���Ժ��ѯ�Ĳ���"+queryData);
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// �õ���ѯSQL
				String sqlStr = creatInwQuerySqlForMrNo(queryData, "IN");
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				// �ж��Ƿ���ֵ��ҽ��
				boolean stationFlg = isKeepWatch();
				// �õ���ѯSQL
				String sqlStr = createODIQuerySqlForMrNo(queryData, "IN",
						stationFlg);
				// System.out.println("��ѯsql:" + sqlStr);
				if (stationFlg) {
					// ��ѯ��Ժ����������Ϣ
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				} else {
					actionParm = new TParm(this.getDBTool().select(sqlStr));
				}
			}
			// out("��Ժ��ѯ�������"+actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "IN");
				return;
			}
			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "IN");

			/*modified by Eric 20170519 ��ʹ������ʾ*/
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
			// �õ���Ժ��ѯ�Ĳ���
			TParm queryData = this.getQueryData("OUT");
			// �ж��Ƿ���ֵ��ҽ��
			boolean stationFlg = isKeepWatch();
			TParm actionParm = new TParm();
			// �õ���ѯSQL
			String sqlStr = createODIQuerySqlForMrNo(queryData, "OUT",
					stationFlg);
			//			 System.out.println("��Ժ��ѯSQL��" + sqlStr);
			if (stationFlg) {
				// ��ѯ��Ժ����������Ϣ
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			} else {
				actionParm = new TParm(this.getDBTool().select(sqlStr));
			}
			// out("��Ժ��ѯ�������" + actionParm);
			// �����ѯΪ�շ���
			if (actionParm.getInt("ACTION", "COUNT") == 0) {
				// ���Table
				callFunction("UI|" + TABLE + "|removeRowAll");
				return;
			}
			// �õ�����ҳǩ��TAG��������ѯ
			if (workPanelTag.length() != 0) {
				this.queryDataOtherTPane(actionParm, "OUT");
				return;
			}
			// ����TABLE�ϵ�����
			this.setTableData(actionParm, "OUT");

			/*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * סԺҽ��վ�س�����ѯ
	 */
	public void onEnterQuery() {

		/*modified by WangQing 20170428 start*/
		if(this.getRunFlg() != null && this.getRunFlg().equals("T")){
			this.onQueryNew();
			return;
		}
		/*modified by WangQing 20170428 end*/

		// System.out.println("�س���ѯ");
		// �õ���Ժ��ѯ�Ĳ���
		TParm queryData = this.getQueryData("OUT");
		// �ж��Ƿ���ֵ��ҽ��
		boolean stationFlg = isKeepWatch();
		TParm actionParm = new TParm();
		// ��Ժ���ǳ�Ժ
		String type = "OUT";
		// SQL
		String sql = "";
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����
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
		// ����
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
		// ����״̬
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

		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) queryData
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) queryData
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) queryData
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) queryData
				.getData("END_DATEOUT"), "yyyyMMdd");

		// System.out.println("����ʱ��:"+endDate);
		// System.out.println("���:"+flg);

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
					+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 				
					+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
					+ " ,E.SEALED_STATUS " //add by huangtt 20161103
					//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
					+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

					/*modified by Eric 20170519 ��ʹ������ʾ*/
					+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

					/*modified by Eric 20170519 ��ʹ������ʾ*/
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

					/*modified by Eric 20170519 ��ʹ������ʾ*/
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
					+ "(CASE A.ALLERGY WHEN 'Y' THEN '��' ELSE '��' END), "
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
					+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
					+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
					+ " ,E.SEALED_STATUS " //add by huangtt 20161103
					//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
					+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

					/*modified by Eric 20170519 ��ʹ������ʾ*/
					+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

					/*modified by Eric 20170519 ��ʹ������ʾ*/
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

					/*modified by Eric 20170519 ��ʹ������ʾ*/
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
					+ "(CASE A.ALLERGY WHEN 'Y' THEN '��' ELSE '��' END), "
					+"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
					+"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND "

					+" ORDER BY A.BED_NO";
			//System.out.println("sql19::" + sql);
		}

		String sqlStr = sql;
		//		System.out.println("  ��Ժsql is����"+sqlStr);
		if (stationFlg) {
			// ��ѯ��Ժ����������Ϣ
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		} else {
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		}
		// out("��Ժ��ѯ�������" + actionParm);
		// �����ѯΪ�շ���
		if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// ���Table
			callFunction("UI|" + TABLE + "|removeRowAll");
			return;
		}
		// �õ�����ҳǩ��TAG��������ѯ
		if (workPanelTag.length() != 0) {
			this.queryDataOtherTPane(actionParm, "OUT");
			return;
		}
		// ����TABLE�ϵ�����
		this.setTableData(actionParm, "OUT");

		/*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * ��ʿ��ѯ
	 * 
	 * @param parm
	 *            TParm
	 * @param type
	 *            String
	 * @return String
	 */
	public String creatInwQuerySQL(TParm parm, String type) {
		String sql = "";
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����
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
		// ����
		String deptCode = "";
		if ("IN".equals(type)) {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND B.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		} else {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		}
		// ����״̬
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

		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
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
		// ��Ժ
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
						// 20121115 //����
						// B.DIE_CONDITION�ֶ�
						// caoyong 20130708
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "
						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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
						// 20121115 //����
						// B.DIE_CONDITION�ֶ�
						// caoyong 20130708
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E,  ADM_RESV AR, REG_PATADM REG"
						+ " WHERE A.BED_NO=B.BED_NO(+)"
						+ " AND A.CASE_NO=B.CASE_NO(+)"
						+ " AND A.MR_NO = B.MR_NO(+)"
						+ " AND A.CASE_NO=E.CASE_NO(+)"
						+ " AND A.MR_NO = E.MR_NO(+)"
						+ " AND A.MR_NO=C.MR_NO(+)"
						+ " AND A.ACTIVE_FLG='Y'"
						+ " AND B.DS_DATE IS NULL"

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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
			// ��Ժ
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
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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
						+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����״̬
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
					+ "'";
		}
		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");

		// ��Ժ
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
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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
						+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
						+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

						+ // =====pangben modify 20110512
						" AND B.MAINDIAG = D.ICD_CODE(+)"
						+ " ORDER BY A.BED_NO";
				// System.out.println("sql4::" + sql);
			}
			// ��Ժ
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
						+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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
						+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
						+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
						+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
						//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
						+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

						/*modified by Eric 20170519 ��ʹ������ʾ*/
						+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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

						/*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * ����ODIҽ��վSQL
	 * 
	 * @param parm
	 *            TParm ��ѯ����
	 * @param type
	 *            String ���Ժ���
	 * @param flg
	 *            boolean UI���()
	 * @return String
	 */
	public String createODIQuerySQL(TParm parm, String type, boolean flg) {
		// SQL
		String sql = "";
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����
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
		// ����
		String deptCode = "";
		if ("IN".equals(type)) {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND B.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		} else {
			deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
					: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		}
		// ����״̬
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
			// ��Ժ���߿��Ը��ݾ���ҽ����ѯ
		}
		// ================liming modify 20120217 end

		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
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
		// System.out.println("����ʱ��:"+endDate);
		// System.out.println("���:"+flg);
		if (flg) {
			// ��Ժ
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E,ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " FROM SYS_BED A,ADM_INP B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E,ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO(+)"
							+ " AND A.CASE_NO=B.CASE_NO(+)"
							+ " AND A.MR_NO = B.MR_NO(+)"
							+ " AND A.MR_NO=C.MR_NO(+)"
							+ " AND A.CASE_NO=E.CASE_NO(+)"
							+ " AND A.MR_NO = E.MR_NO(+)"

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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
				// ��Ժ
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
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							 /*modified by Eric 20170519 ��ʹ������ʾ*/
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
							 + "(CASE A.ALLERGY WHEN 'Y' THEN '��' ELSE '��' END), "
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
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "//����ibs_ordd���ܽ�� 

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG,IBS_ORDD G"//���ӱ�IBS_ORDD
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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
							+"(CASE A.ALLERGY WHEN 'Y' THEN '��' ELSE '��' END),"
							+"(CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END),"
							+"A.FALL_RISK,E.SEALED_STATUS,A.DAY_OPE_FLG,REG.ENTER_ROUTE,REG.PATH_KIND"
							+
							" ORDER BY A.BED_NO";
					//System.out.println("sql6::"+sql);
				}
			}
		} else {
			// ��Ժ
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ " ORDER BY A.BED_NO";
					//					 System.out.println("sql8::" + sql);
				}
				// ��Ժ
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
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							 /*modified by Eric 20170519 ��ʹ������ʾ*/
							 + "' AND A.CASE_NO=AR.IN_CASE_NO(+)"
							 + " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"
							 + " AND A.CASE_NO = G.CASE_NO "
							 +" GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE, A.DS_DATE,"
							 +"D.ICD_CHN_DESC,A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
							 + "A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,"
							 + "A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,"
							 + "A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
							 + "(CASE A.ALLERGY WHEN 'Y' THEN '��' ELSE '��' END), "
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
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND,SUM (TOT_AMT) TOT_AMT "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ vsDoctor + region + // ===========pangben modify

							// 20110512
							billStatus 
							+"AND A.CASE_NO = G.CASE_NO "
							// modified by wangqing 20170707
							// ��Ժ���˲鲻��
							+" GROUP BY A.CLNCPATH_CODE,B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC,"
							+"A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,A.RED_SIGN,A.YELLOW_SIGN,"
							+"A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,"
							+"C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,"
							+"E.CHECK_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS,A.DISE_CODE,A.SCHD_CODE,"
							+"(CASE A.ALLERGY WHEN 'Y' THEN '��' ELSE '��' END),"
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
		// ռ��ע��ALLO_FLG
		String alloFlg = this.getTCheckBox("ALLO_FLG").isSelected() ? " AND A.ALLO_FLG='Y' AND B.CANCEL_FLG<>'Y' AND A.BED_STATUS='1' "
				: "";
		// ����״̬
		String billStatus = "";
		if (!"IN".equals(type)) {
			billStatus = parm.getValue("BILL_STATUS").length() == 0 ? ""
					: " AND A.BILL_STATUS='" + parm.getValue("BILL_STATUS")
					+ "'";
		}
		// ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm
				.getData("START_DATE"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm
				.getData("END_DATE"), "yyyyMMdd");
		// ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm
				.getData("START_DATEOUT"), "yyyyMMdd");
		// System.out.println("��ʼʱ��:"+startDate);
		// ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) parm
				.getData("END_DATEOUT"), "yyyyMMdd");

		// System.out.println("����ʱ��:"+endDate);
		// System.out.println("���:"+flg);
		if (flg) {
			// ��Ժ
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ // =====pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " ORDER BY A.BED_NO";
					//					 System.out.println("sql4::" + sql);
				}
				//				System.out.println("��Ժ ��Ժ sql sql::" + sql);
				// ��Ժ
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
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							 /*modified by Eric 20170519 ��ʹ������ʾ*/
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
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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
			// ��Ժ
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,B.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , B.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , B.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " AND A.CASE_NO=AR.IN_CASE_NO(+)"
							+ " AND AR.OPD_CASE_NO=REG.CASE_NO(+)"

							+ // ============pangben modify 20110512
							" AND B.MAINDIAG = D.ICD_CODE(+)"
							+ " ORDER BY A.BED_NO";
					// System.out.println("sql8::" + sql);
				}
				// ��Ժ
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
							+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
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

							 /*modified by Eric 20170519 ��ʹ������ʾ*/
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
							+ " ,(CASE B.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
							+ " (CASE WHEN B.INFECT_SCR_RESULT IS NOT NULL THEN B.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
							+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
							//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
							+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

							/*modified by Eric 20170519 ��ʹ������ʾ*/
							+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E, ADM_RESV AR,REG_PATADM REG"
							+ " WHERE A.BED_NO=B.BED_NO"
							+ " AND A.MR_NO=C.MR_NO"
							+ " AND A.CANCEL_FLG<>'Y'"
							+ " AND A.DS_DATE IS NOT NULL"

							 /*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * �õ���ѡ��
	 * 
	 * @param tag
	 *            String
	 * @return TCheckBox
	 */
	public TCheckBox getTCheckBox(String tag) {
		return (TCheckBox) this.getComponent(tag);
	}

	/**
	 * ��ѯ�����TABLE�е�������ʾ
	 * 
	 * @param parm
	 *            TParm ��Ҫ���������
	 * @param type
	 *            String �������ݵ���� INΪ��Ժ,OUTΪ��Ժ
	 */
	public void setTableData(TParm parm, String type) {
		// ����TABLE����
		if (type.equals("IN")) {
			// this.messageBox("IN come in");
			// ��Ժ
			TParm actionParm = this.filterTParmData(parm);
			// out("��Ժ����"+actionParm);
			// ����TABLE����(��Ժ)
			callFunction("UI|" + TABLE + "|setParmValue", actionParm);

			//add by huangtt 20161103 ���ִ����ɫ start

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
			//add by huangtt 20161103 ���ִ����ɫ end

			//			/*modified by Eric 20170518 start
			//			 * ��ʹ���Ĳ������� 
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







			// ���ò�ѯ���ݺ�ҳ����ʾ���ݶ�Ӧ
			TTable tab = (TTable) this.callFunction("UI|" + TABLE + "|getThis");
			tab
			.setModifyTag("DEPT_CODE:DEPT_CODE;STATION_CODE:STATION_CODE;BED_NO:BED_NO_DESC;MR_NO:MR_NO;PAT_NAME:PAT_NAME;SEX:SEX_CODE;HEIGHT:HEIGHT;WEIGHT:WEIGHT;YELLOW:YELLOW_SIGN;ADM_DATE:IN_DATE:Timestamp;YJJ_PRICE:TOTAL_BILPAY;YJYE_PRICE:CUR_AMT;GREED_PRICE:GREENPATH_VALUE;VC_CODE:VS_DR_CODE;CASE_NO:CASE_NO;IPD_NO:IPD_NO;TOTAL_AMT:TOTAL_AMT;SERVICE_LEVELIN:SERVICE_LEVEL;CLNCPATH_CODE:CLNCPATH_CODE;CTZ_CODE:CTZ1_CODE;AGE");
			// ��������
			int personNum = parm.getCount("PAT_NAME");
			this.setValue("PRESON_NUM", personNum);
		} else {
			// ��Ժ
			TParm actionParm = this.filterTParmData(parm);
			// out("��Ժ����"+actionParm);
			// ����TABLE����(��Ժ)
			callFunction("UI|" + TABLE + "|setParmValue", actionParm);

			//add by huangtt 20161103 ���ִ����ɫ start

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

			//add by huangtt 20161103 ���ִ����ɫ end

			// ���ò�ѯ���ݺ�ҳ����ʾ���ݶ�Ӧ
			TTable tab = (TTable) this.callFunction("UI|" + TABLE + "|getThis");
			tab
			.setModifyTag("DEPT_CODEOUT:DEPT_CODE;STATION_CODEOUT:STATION_CODE;MR_NOOUT:MR_NO;PAT_NAMEOUT:PAT_NAME;VC_CODEOUT:VS_DR_CODE;CASE_NOOUT:CASE_NO;IPD_NOOUT:IPD_NO;SERVICE_LEVELOUT:SERVICE_LEVEL;CLNCPATH_CODE_OUT:CLNCPATH_CODE;BILL_STATUS;AGEOUT:AGE");
			// ��������PERSON_NUMOUT
			int personNum = parm.getCount("PAT_NAME");
			this.setValue("PERSON_NUMOUT", personNum);
		}
	}

	/**
	 * ���˲�ѯ���Ĳ���������Ϣ����סԺ���ڼ��������סԺ���������뵽��ѯ������TParm��
	 * 
	 * @param parm
	 *            TParm ��Ҫ���˵�����
	 * @return TParm
	 */
	public TParm filterTParmData(TParm parm) {
		// System.out.println("����TABLE����"+parm);
		/*
		 * ����ѭ��������������е����������䣬��Ժ������סԺ����
		 * (�����ֶ�:SYS_PATINFO.BIRTH_DATE��ӦKEY(AGE),��Ժ�����ֶ�
		 * :ADM_INP.IN_DATE��ӦKEY(DAYNUM))
		 */
		Timestamp sysDate = SystemTool.getInstance().getDate();
		// ���ص�����
		int rowCount = parm.getCount("PAT_NAME");
		for (int i = 0; i < rowCount; i++) {
			Timestamp temp = parm.getTimestamp("BIRTH_DATE", i) == null ? sysDate
					: parm.getTimestamp("BIRTH_DATE", i);
			// ��������
			String age = "0";
			if (parm.getTimestamp("IN_DATE", i) != null){
				//				age = OdiUtil.getInstance().showAge(temp,
				//						parm.getTimestamp("IN_DATE", i));

				//				this.messageBox(temp+"");
				//				this.messageBox(SystemTool.getInstance().getDate()+"");
				age = OdiUtil.getInstance().showAge(temp,
						SystemTool.getInstance().getDate());//����ϵͳʱ��  20170807 machao
			}
			else
				age = "";
			parm.addData("AGE", age);
			// ����סԺ����
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
	 * �õ�סԺҽ��վ�Ĳ�ѯ����
	 * 
	 * @param type
	 *            String INΪ��Ժ;OUTΪ��Ժ
	 * @return TParm
	 */
	public TParm getQueryData(String type) {
		TParm result = new TParm();

		/*modified by WangQing 20170427 start*/
		if(getValueString("MR_NO").length() > 0){
			Pat pat = Pat.onQueryByMrNo(getValueString("MR_NO"));		
			String mrNo = PatTool.getInstance().checkMrno(this.getValueString("MR_NO").trim());
			if (!StringUtil.isNullString(mrNo) && !mrNo.equals(pat.getMrNo())) {
				this.messageBox("������" + mrNo + " �Ѻϲ��� " + "" + pat.getMrNo());
				setValue("MR_NO", pat.getMrNo());
			}
		}
		result.setData("TYPE", type);// ��Ժ/��Ժ
		/*modified by WangQing 20170428 end*/

		if (type.equals("IN")) {
			if (this.getRunFlg().equals("INWCHECK")
					|| this.getRunFlg().equals("INWEXE")
					|| this.getRunFlg().equals("SHEET")) {
				// ����
				result.setData("DEPT_CODE", this.getValue("INW_DEPT_CODE"));
				// ����
				result.setData("STATION_CODE", this
						.getValue("INW_STATION_CODE"));
				// ����ҽʦ
				result.setData("VC_CODE", this.getValue("INW_VC_CODE"));
			} else {
				// ����
				result.setData("DEPT_CODE", this.getValue("DEPT_CODE"));
				// ����
				result.setData("STATION_CODE", this.getValue("STATION_CODE"));
				// ����ҽʦ
				result.setData("VC_CODE", this.getValue("VC_CODE"));
			}
			// ����
			// result.setData("BED_NO",this.getValue("BED_NO"));
			// ������
			String mrNo = getValueString("MR_NO");
			if (mrNo.length() > 0) {
				mrNo = PatTool.getInstance().checkMrno(mrNo);
				setValue("MR_NO", mrNo);
				result.setData("MR_NO", mrNo);
			}
			// סԺ��
			String ipdNo = getValueString("IPD_NO");
			if (ipdNo.length() > 0) {
				ipdNo = PatTool.getInstance().checkIpdno(ipdNo);
				setValue("IPD_NO", ipdNo);
				result.setData("IPD_NO", ipdNo);
			}
			// ��ѯ�õ������SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE
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
				this.messageBox("��ѯ���ξ����ʧ��" + action.getErrText());
				return caseParm;
			}
			caseNo = caseParm.getValue("CASE_NO", 0);
			result.setData("CASE_NO", caseNo);
			// סԺ����
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
			//			this.messageBox("��ѯ���ξ����ʧ��" + caseParm.getErrText());
			//			return caseParm;
			//		}
			//		caseNo = caseParm.getValue("CASE_NO", 0);
			//		result.setData("CASE_NO", caseNo);
			// �õ���Ժҳǩ����
			// סԺ��ʼ����
			result.setData("START_DATE", this.getValue("START_DATE"));
			// סԺ��ֹ����
			result.setData("END_DATE", this.getValue("END_DATE"));
			// ��Ժ��ʼ����
			result.setData("START_DATEOUT", this.getValue("START_DATEOUT"));
			// ��Ժ��ֹ����
			result.setData("END_DATEOUT", this.getValue("END_DATEOUT"));
			// ����
			result.setData("DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// ����
			result.setData("STATION_CODE", this.getValue("STATION_CODEOUT"));
			// ����ҽʦ
			result.setData("VC_CODE", this.getValue("VC_CODEOUT"));
			// ������
			String mrNo = getValueString("MR_NOOUT");
			if (mrNo.length() > 0) {
				mrNo = PatTool.getInstance().checkMrno(mrNo);
				setValue("MR_NOOUT", mrNo);
				result.setData("MR_NO", mrNo);
			}
			// סԺ��
			String ipdNo = getValueString("IPD_NOOUT");
			if (ipdNo.length() > 0) {
				ipdNo = PatTool.getInstance().checkIpdno(ipdNo);
				setValue("IPD_NOOUT", ipdNo);
				result.setData("IPD_NO", ipdNo);
			}
			// ��ѯ�õ������SELECT MAX(CASE_NO) AS CASE_NO FROM ADM_INP WHERE
			// MR_NO=<MR_NO>
			TParm action = new TParm();
			if (mrNo.length() != 0) {
				action.setData("MR_NO", mrNo);
			}
			if (ipdNo.length() != 0) {
				action.setData("IPD_NO", ipdNo);
			}
			action.setData("NOT_DS", "");// modify by wanglong 20120814
			// ԭ��ѯ��Ժ������SQL������������Զ���һ���ֶ���������������Ҫ����Module��SQL�м�һ����������
			String caseNo = getCaseNo(action);
			result.setData("CASE_NO", caseNo);
			// ����״̬
			result.setData("BILL_STATUS", this.getValueString("BILL_STATUS"));
			// ��Ժ����
			// result.setData("ADM_DATE",this.getValue("ADM_DATEOUT"));
		}
		return result;
	}

	/**
	 * ��ǰ�������Ƿ���ֵ��ҽʦ
	 * 
	 * @return boolean
	 */
	public boolean isKeepWatch() {
		if (!this.getRunFlg().equals("ODI")) {
			return true;
		}
		// �õ���ǰ�û�ID
		String userId = Operator.getID();
		// �õ���ǰ����
		String stationCode = Operator.getStation();
		TParm action = new TParm(this.getDBTool().select(
				"SELECT DR_CODE FROM ODI_DUTYDRLIST WHERE STATION_CODE='"
						+ stationCode + "' AND DR_CODE='" + userId + "'"));
		// System.out.println("isKeepWatch:"+action.getCount());
		if (action.getInt("ACTION", "COUNT") == 0) {
			// ����ҽʦ
			this.setValue("VC_CODE", Operator.getID());
			// ����ֻ���ǵ�ǰҽʦ�༭
			this.callFunction("UI|VC_CODE|setEnabled", false);
			return false;
		}
		// ����ҽʦ
		this.setValue("VC_CODE", Operator.getID());
		// ����ֻ���ǵ�ǰҽʦ�༭
		this.callFunction("UI|VC_CODE|setEnabled", true);
		return true;
	}

	/**
	 * �õ�CASE_NO
	 * 
	 * @param parm
	 *            TParm
	 * @return String
	 */
	public String getCaseNo(TParm parm) {
		String caseNo = "";
		// ���
		if (!parm.existData("MR_NO") && !parm.existData("IPD_NO")) {
			return caseNo;
		}
		TParm action = OdiMainTool.getInstance().queryPatCaseNo(parm);
		if (action.getErrCode() < 0) {
			this.messageBox(action.getErrText());
			return caseNo;
		}
		// System.out.println("���ظ���:" + action.getInt("ACTION", "COUNT"));
		if (action.getInt("ACTION", "COUNT") > 1) {
			// �������������ѯCASE_NO
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
	 * �õ�PANEL
	 * 
	 * @return TPanel
	 */
	public TPanel getTPanel() {
		return (TPanel) this.getComponent(PANEL);
	}

	/**
	 * ���������
	 * 
	 * @param tag
	 *            String
	 * @param path
	 *            String
	 */
	public void runPane(String tag, String path) {
		// �õ�ϵͳ����Ϊ����ʾ
		if (this.getRunFlg().length() == 0) {
			// ������ϵͳ������
			this.messageBox("E0153");
			return;
		}
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
		int selType = tabPane.getSelectedIndex();
		// �رյ�ǰ����ҳ��
		onClosePanel();
		// �õ���ǰѡ����
		int selectRow = (Integer) callFunction("UI|" + TABLE
				+ "|getSelectedRow");
		// �Ƿ���ѡ��
		if (selectRow < 0)
			return;
		// �õ�ѡ��������
		TParm actionParm = this.getSelectRowData(TABLE);
		TTable table = (TTable) this.getComponent("TABLE");

		//		System.out.println("000+++++=======++++actionParm is ::"+table.getParmMap());
		//		System.out.println("+++++=======++++actionParm is ::"+actionParm);
		// סԺҽ��վ����
		String mrNo = actionParm.getValue("MR_NO");
		if (mrNo == null || mrNo.length() == 0)
			return;
		// System.out.println("�õ�ѡ��������"+actionParm);
		// ����CASE_NO
		TParm action = new TParm();
		String caseNo = "";
		int i = 0;
		if (selType == 0) {
			// ��Ժ
			caseNo = this.getValueString("CASE_NO");
			IsICU = SYSBedTool.getInstance().checkIsICU(caseNo);
			// ҽ��վ
			//
			//action.setData("ODI", "DAY_OPE_CODE", actionParm.getData("DAY_OPE_CODE"));
			action.setData("ODI", "DAY_OPE_FLG", actionParm.getData("DAY_OPE_FLG"));
			action.setData("ODI", "CASE_NO", caseNo);
			action.setData("ODI", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("ODI", "BED_NO", actionParm.getData("BED_NO"));
			action.setData("ODI", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ODI", "MR_NO", this.getValue("MR_NO"));

			// ����
			action.setData("ODI", "PAT_NAME", this.getValue("PAT_NAME"));
			// ����1
			action.setData("ODI", "PAT_NAME1", TMessage.getPy(this
					.getValueString("PAT_NAME")));
			String orgCode = this.getOrgCode(this.getValue("STATION_CODE")
					.toString(), this.getValue("DEPT_CODE").toString());
			// �õ���Ӧҩ��
			action.setData("ODI", "ORG_CODE", orgCode);
			action
			.setData("ODI", "STATION_CODE", this
					.getValue("STATION_CODE"));
			action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// ֹͣ����
			action.setData("ODI", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			action.setData("ODI", "CUR_AMT", actionParm.getData("CUR_AMT"));
			action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����
			action.setData("ODI", "BIRTH_DATE", actionParm
					.getData("BIRTH_DATE"));
			// �Ա�
			action.setData("ODI", "SEX_CODE", actionParm.getData("SEX_CODE"));
			// �ʱ�
			action.setData("ODI", "POST_CODE", actionParm.getData("POST_CODE"));
			// ��ַ
			action.setData("ODI", "ADDRESS", actionParm.getData("ADDRESS"));
			// ��λ
			action.setData("ODI", "COMPANY_DESC", actionParm
					.getData("COMPANY_DESC"));
			// �绰
			action.setData("ODI", "TEL", actionParm.getData("CELL_PHONE"));
			// ��ͥ�绰
			action.setData("ODI", "TEL1", actionParm.getData("TEL_HOME"));
			// ���֤��
			action.setData("ODI", "IDNO", actionParm.getData("IDNO"));
			// �����
			action.setData("ODI", "MAINDIAG", actionParm.getData("MAINDIAG"));
			// ���
			action.setData("ODI", "CTZ_CODE", actionParm.getData("CTZ1_CODE"));
			// ����Ȩ��ע��
			action.setData("ODI", "SAVE_FLG", true);
			// ������ϱ���
			action.setData("ODI", "ICD_CODE", actionParm.getData("ICD_CODE"));
			// �����������
			action.setData("ODI", "ICD_DESC", actionParm.getData("MAINDIAG"));
			// ������ҩ
			action.setData("ODI", "PASS", passIsReady);
			action.setData("ODI", "FORCE", enforcementFlg);
			action.setData("ODI", "WARN", warnFlg);
			if (passIsReady) {
				action.setData("ODI", "passflg", initReasonbledMed());
			} else {
				action.setData("ODI", "passflg", false);
			}
			// ����ע��
			if ("OIDR".equals(this.getRunFlg())) {
				action.setData("ODI", "OIDRFLG", true);
			} else {
				action.setData("ODI", "OIDRFLG", false);
			}

			/*modified by WangQing 20170428 start*/
			// ��ѧҽ��վע��
			if ("T".equals(this.getRunFlg())) {
				action.setData("ODI", "T_FLG", true);
			} else {
				action.setData("ODI", "T_FLG", false);
			}
			/*modified by WangQing 20170428 end*/

			// ICUע��
			action.setData("ODI", "ICU_FLG", IsICU);

			// ��ʿվ
			action.setData("INW", "CASE_NO", caseNo);
			action.setData("INW", "STATION_CODE", this
					.getValue("INW_STATION_CODE"));
			action.setData("INW", "POPEDEM", this.isInwPopedem());
			// ���1
			action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ���2
			action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ���3
			action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ����
			action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			// ����
			action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
			// סԺ��
			action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
			// ������
			action.setData("INW", "MR_NO", this.getValue("MR_NO"));
			// ����  yanjing  20140224
			action.setData("INW", "STATION_CODE", this.getValue("STATION_CODE"));
			// ����  yanjing  20140224
			action.setData("INW", "BED_NO", this.getValue("BED_NO"));
			// ����ҽ��  yanjing  20140224
			action.setData("INW", "VS_DR_CODE", this.getValue("VC_CODE"));
			// ����ҽ��  yanjing  20140224
			action.setData("INW", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// ��Ժʱ��
			action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����Ȩ��ע��
			action.setData("INW", "SAVE_FLG", true);
			// ICUע��
			action.setData("INW", "ICU_FLG", IsICU);
			// ============pangben 2012-7-9 start ����ٴ�·��������ʾ
			action.setData("INW", "CLNCPATH_CODE", actionParm
					.getData("CLNCPATH_CODE"));

			// �Ƽ�
			//			System.out.println("----+++====SCHD_CODE SCHD_CODE is ::"+actionParm.getData("SCHD_CODE"));
			action.setData("IBS", "SCHD_CODE", actionParm.getData("SCHD_CODE"));//�ٴ�·��ʱ��
			action.setData("IBS", "CASE_NO", caseNo);
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODE"));
			action.setData("IBS", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("IBS", "MR_NO", this.getValue("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAME"));
			// �õ���Ӧҩ��
			action.setData("IBS", "ORG_CODE", orgCode);
			action
			.setData("IBS", "STATION_CODE", this
					.getValue("STATION_CODE"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODE"));
			// ֹͣ����
			action.setData("IBS", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			// ����
			action.setData("IBS", "BED_NO", actionParm.getData("BED_NO"));
			// ���1
			action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ���2
			action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ���3
			action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ��ɫ����
			action.setData("IBS", "RED_SIGN", actionParm.getData("RED_SIGN"));
			// ��ɫ����
			action.setData("IBS", "YELLOW_SIGN", actionParm
					.getData("YELLOW_SIGN"));
			// ��ɫͨ��
			action.setData("IBS", "GREENPATH_VALUE", actionParm
					.getData("GREENPATH_VALUE"));
			// ҽ���ܷ���
			action.setData("IBS", "TOTAL_AMT", actionParm.getData("TOTAL_AMT"));
			// ����Ȩ��ע��
			action.setData("IBS", "SAVE_FLG", true);
			action.setData("IBS", "INWLEAD_FLG", ibsFlg);
			// ============pangben 2012-7-9 start ����ٴ�·��������ʾ
			action.setData("IBS", "CLNCPATH_CODE", actionParm
					.getData("CLNCPATH_CODE"));
			// ������ҳ
			action.setData("MRO", "CASE_NO", caseNo);
			// ����Ȩ��ע��
			action.setData("MRO", "SAVE_FLG", true);
			// ADM
			action.setData("ADM", "CASE_NO", caseNo);
			action.setData("ADM", "MR_NO", this.getValue("MR_NO"));
			action.setData("ADM", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ADM", "ADM_DATE", actionParm.getData("IN_DATE"));
			action.setData("ADM", "ADM_FLG", "Y");
			// ����Ȩ��ע��
			action.setData("ADM", "SAVE_FLG", true);
			action.setData("NSS", "CASE_NO", caseNo);
			action.setData("NSS", "MR_NO", this.getValue("MR_NO"));
			// �ٴ�·��׼��׼��
			action.setData("CLP", "CASE_NO", caseNo);
			action.setData("CLP", "MR_NO", this.getValue("MR_NO"));
			action.setData("CLP", "CLNCPATH_CODE", actionParm
					.getValue("CLNCPATH_CODE"));

			// Ԥ����

			action.setData("BILPAY", "CASE_NO", caseNo);

		} else {
			// ��Ժ
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
			// �õ���Ӧҩ��
			action.setData("ODI", "ORG_CODE", orgCode);
			action.setData("ODI", "STATION_CODE", this
					.getValue("STATION_CODEOUT"));
			action.setData("ODI", "DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// ֹͣ����
			action.setData("ODI", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			action.setData("ODI", "CUR_AMT", actionParm.getData("CUR_AMT"));
			// סԺ����
			action.setData("ODI", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����
			action.setData("ODI", "BIRTH_DATE", actionParm
					.getData("BIRTH_DATE"));
			// �Ա�
			action.setData("ODI", "SEX_CODE", actionParm.getData("SEX_CODE"));
			// �ʱ�
			action.setData("ODI", "POST_CODE", actionParm.getData("POST_CODE"));
			// ��ַ
			action.setData("ODI", "ADDRESS", actionParm.getData("ADDRESS"));
			// ��λ
			action.setData("ODI", "COMPANY_DESC", actionParm
					.getData("COMPANY_DESC"));
			// �绰
			action.setData("ODI", "TEL", actionParm.getData("CELL_PHONE"));
			// ��ͥ�绰
			action.setData("ODI", "TEL1", actionParm.getData("TEL_HOME"));
			// ���֤��
			action.setData("ODI", "IDNO", actionParm.getData("IDNO"));
			// �����
			action.setData("ODI", "MAINDIAG", actionParm.getData("MAINDIAG"));
			// ���
			action.setData("ODI", "CTZ_CODE", actionParm.getData("CTZ1_CODE"));
			// ����Ȩ��ע��
			action.setData("ODI", "SAVE_FLG", false);
			// ������ϱ���
			action.setData("ODI", "ICD_CODE", actionParm.getData("ICD_CODE"));
			// �����������
			action.setData("ODI", "ICD_DESC", actionParm.getData("MAINDIAG"));
			// ������ҩ
			action.setData("ODI", "PASS", passIsReady);
			action.setData("ODI", "FORCE", enforcementFlg);
			action.setData("ODI", "WARN", warnFlg);
			if (passIsReady) {
				action.setData("ODI", "passflg", initReasonbledMed());
			} else {
				action.setData("ODI", "passflg", false);
			}
			// ����ע��
			if ("OIDR".equals(this.getRunFlg())) {
				action.setData("ODI", "OIDRFLG", true);
			} else {
				action.setData("ODI", "OIDRFLG", false);
			}

			/*modified by WangQing 20170428 start 
			 * ��ѧҽ��վע��*/
			if ("T".equals(this.getRunFlg())) {
				action.setData("ODI", "T_FLG", true);
			} else {
				action.setData("ODI", "T_FLG", false);
			}
			/*modified by WangQing 20170428 end*/

			// ICUע��
			action.setData("ODI", "ICU_FLG", IsICU);

			// ��ʿվ
			action.setData("INW", "CASE_NO", caseNo);
			action.setData("INW", "STATION_CODE", this
					.getValue("INW_STATION_CODEOUT"));
			action.setData("INW", "POPEDEM", this.isInwPopedem());
			// ���1
			action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ���2
			action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ���3
			action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ����
			action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
			// ����
			action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
			// סԺ��
			action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
			// ������
			action.setData("INW", "MR_NO", actionParm.getData("MR_NO"));
			// ����  yanjing  20140224
			action.setData("INW", "STATION_CODE", this.getValue("STATION_CODE"));
			// ����  yanjing  20140224
			action.setData("INW", "BED_NO", this.getValue("BED_NO"));
			// ��Ժʱ��
			action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
			// ����Ȩ��ע��
			action.setData("INW", "SAVE_FLG", false);
			// �Ƽ�
			action.setData("IBS", "SCHD_CODE", actionParm.getData("SCHD_CODE"));//�ٴ�·��ʱ��
			action.setData("IBS", "CASE_NO", caseNo);
			action.setData("IBS", "VS_DR_CODE", this.getValue("VC_CODEOUT"));
			action.setData("IBS", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("IBS", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("IBS", "PAT_NAME", this.getValue("PAT_NAMEOUT"));
			action.setData("IBS", "INWLEAD_FLG", ibsFlg);//סԺ�Ƽ� ��ʿ��Ȩ��
			// �õ���Ӧҩ��
			action.setData("IBS", "ORG_CODE", orgCode);
			action.setData("IBS", "STATION_CODE", this
					.getValue("STATION_CODEOUT"));
			action.setData("IBS", "DEPT_CODE", this.getValue("DEPT_CODEOUT"));
			// ֹͣ����
			action.setData("IBS", "STOP_BILL_FLG", actionParm
					.getData("STOP_BILL_FLG"));
			// ����
			action.setData("IBS", "BED_NO", actionParm.getData("BED_NO"));
			// ���1
			action.setData("IBS", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
			// ���2
			action.setData("IBS", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
			// ���3
			action.setData("IBS", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
			// ��ɫ����
			action.setData("IBS", "RED_SIGN", actionParm.getData("RED_SIGN"));
			// ��ɫ����
			action.setData("IBS", "YELLOW_SIGN", actionParm
					.getData("YELLOW_SIGN"));
			// ҽ���ܷ���
			action.setData("IBS", "TOTAL_AMT", actionParm.getData("TOTAL_AMT"));
			// ����Ȩ��ע��
			action.setData("IBS", "SAVE_FLG", false);
			action.setData("IBS", "CLNCPATH_CODE", actionParm
					.getData("CLNCPATH_CODE"));//=====yanjing סԺ�Ƽ۳�Ժҳǩ��ʼ������
			// ������ҳ
			action.setData("MRO", "CASE_NO", caseNo);
			// ����Ȩ��ע��
			action.setData("MRO", "SAVE_FLG", false);
			// ADM
			action.setData("ADM", "CASE_NO", caseNo);
			action.setData("ADM", "MR_NO", this.getValue("MR_NOOUT"));
			action.setData("ADM", "IPD_NO", actionParm.getData("IPD_NO"));
			action.setData("ADM", "ADM_DATE", actionParm.getData("IN_DATE"));
			action.setData("ADM", "ADM_FLG", "N");
			// ����Ȩ��ע��
			action.setData("ADM", "SAVE_FLG", false);
			action.setData("NSS", "CASE_NO", caseNo);
			action.setData("NSS", "MR_NO", this.getValue("MR_NOOUT"));
			// �ٴ�·��׼��׼��
			action.setData("CLP", "CASE_NO", caseNo);
			action.setData("CLP", "MR_NO", actionParm.getData("MR_NO"));
			action.setData("CLP", "CLNCPATH_CODE", actionParm
					.getValue("CLNCPATH_CODE"));
			// Ԥ����
			action.setData("BILPAY", "CASE_NO", caseNo);
		}

		// System.out.println("path"+path+"TParm"+action);
		// $$ modified by lx 2012/04/06
		// סԺ��ѯ(PDF����)
		if (path.indexOf("ODIDocQuery") != -1) {
			this.openWindow("%ROOT%\\config\\ODI\\ODIDocQuery.x", action);
		} else {
			// һ���ٴ���¼����
			if (this.getPopedem("PIC")) {
				action.setData("INW", "ROLE_TYPE", "PIC");
			}
			////machao start 20170503 �ж��Ƿ��ǴӸ�Ⱦҳ����� ��Ⱦ�������
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
	 * �õ���Ӧҩ��
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
		// �رյ�ǰ����ҳ��
		// onClosePanel();
		// �õ�ѡ��������
		TParm actionParm = this.getSelectRowData(TABLE);
		TParm action = new TParm();
		action.setData("INW", "CASE_NO", parm.getData("CASE_NO"));
		action.setData("INW", "STATION_CODE", parm.getData("STATION_CODE"));
		action.setData("INW", "POPEDEM", this.isInwPopedem());
		// ���1
		action.setData("INW", "CTZ1_CODE", actionParm.getData("CTZ1_CODE"));
		// ���2
		action.setData("INW", "CTZ2_CODE", actionParm.getData("CTZ2_CODE"));
		// ���3
		action.setData("INW", "CTZ3_CODE", actionParm.getData("CTZ3_CODE"));
		// ����
		action.setData("INW", "PAT_NAME", this.getValue("PAT_NAME"));
		// ����
		action.setData("INW", "DEPT_CODE", this.getValue("INW_DEPT_CODE"));
		// סԺ��
		action.setData("INW", "IPD_NO", actionParm.getData("IPD_NO"));
		// ������
		action.setData("INW", "MR_NO", this.getValue("MR_NO"));
		// ��Ժʱ��
		action.setData("INW", "ADM_DATE", actionParm.getData("IN_DATE"));
		// ����Ȩ��ע��
		action.setData("INW", "SAVE_FLG", true);
		action.setData("INW", "LEDUI", parm.getData("LEDUI"));
		onClosePanel();
		getTPanel().addItem(tag, "%ROOT%\\config\\" + path, action, false);
		((TTable) this.getComponent(TABLE)).setVisible(false);
		workPanelTag = tag;
	}

	/**
	 * �õ�ѡ��������
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
		// out("�к�" + selectRow);
		//		TParm parm = (TParm) callFunction("UI|" + tableTag + "|getParmValue");
		TParm parm = this.getTTable(TABLE).getParmValue();
		// out("GRID����" + parm);
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
	 * ��ȡ�ٴ�·����ʱ��
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
	 * �ر��¼�
	 * 
	 * @return boolean
	 */
	public boolean onClosing() {
		// ���״̬����Ϣ
		callFunction("UI|setSysStatus", "");
		if (!onClosePanel())
			return false;
		// �ر�Socket
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
	 * �رչ���ҳ��
	 * 
	 * @return boolean
	 */
	public boolean onClosePanel() {
		if (workPanelTag == null || workPanelTag.length() == 0)
			return true;
		TPanel p = (TPanel) getComponent(workPanelTag);
		if (!p.getControl().onClosing())
			return false;
		// �Ƴ���ǰ��UI
		callFunction("UI|" + PANEL + "|removeItem", workPanelTag);
		// �Ƴ���UIMenuBar
		callFunction("UI|removeChildMenuBar");
		// �Ƴ���UIToolBar
		callFunction("UI|removeChildToolBar");
		// ��ʾUIshowTopMenu
		callFunction("UI|showTopMenu");
		workPanelTag = "";
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ���Ա༭
		tabPane.setEnabled(true);
		// ��ʾTABLE
		((TTable) this.getComponent(TABLE)).setVisible(true);
		return true;
	}

	/**
	 * �������ݿ��������
	 * 
	 * @return TJDODBTool
	 */
	public TJDODBTool getDBTool() {
		return TJDODBTool.getInstance();
	}

	/**
	 * �õ�ϵͳ����
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
	 * ����ϵͳ����
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
	 * ��ʾ��ǰTOOLBAR
	 */
	public void onShowWindowsEvent() {
		if (workPanelTag == null || workPanelTag.length() == 0) {
			// ��ʾUIshowTopMenu
			callFunction("UI|showTopMenu");
			return;
		}
		TPanel p = (TPanel) getComponent(workPanelTag);
		p.getControl().callFunction("onShowWindowsFunction");
	}

	public void passcheck() {
		// ������ҩ
		passIsReady = SYSNewRegionTool.getInstance().isIREASONABLEMED(
				Operator.getRegion());
		// Ԥ���ȼ�
		warnFlg = Integer.parseInt(TConfig.getSystemValue("WarnFlg"));
		// �Ƿ�ǿ��
		enforcementFlg = "Y".equals(TConfig.getSystemValue("EnforcementFlg"));
		// ������ҩ
		if (passIsReady) {
			if (!initReasonbledMed()) {
				this.messageBox("������ҩ��ʼ��ʧ�ܣ�");
			}
		}
	}

	/**
	 * ��ʼ��������ҩ
	 * 
	 * @return boolean
	 */
	public boolean initReasonbledMed() {
		try {
			if (PassDriver.init() != 1) {
				return false;
			}
			// ������ҩ��ʼ��
			if (PassDriver.PassInit(Operator.getName(), Operator.getDept(), 10) != 1) {
				return false;
			}
			// ������ҩ���Ʋ���
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
	 * �õ��˵�
	 * 
	 * @param tag
	 *            String
	 * @return TMenuItem
	 */
	public TMenuItem getTMenuItem(String tag) {
		return (TMenuItem) this.getComponent(tag);
	}

	/**
	 * �������µ��ӿ�
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
	 * �������������µ�
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
	 * PDF����
	 */
	public void onSubmitPDF() {
		this.runPane("STATIONMAIN", "ODI\\ODIDocQuery.x");
	}

	// $$==============add by lx 2012/06/24 ����������start=============$$//
	/**
	 * �����������������
	 * 
	 * @param table
	 */
	public void addListener(final TTable table) {
		// System.out.println("==========�����¼�===========");
		// System.out.println("++��ǰ���++"+masterTbl.getParmValue());
		// TParm tableDate = masterTbl.getParmValue();
		// System.out.println("===tableDate����ǰ==="+tableDate);
		table.getTable().getTableHeader().addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent mouseevent) {
				int i = table.getTable().columnAtPoint(mouseevent.getPoint());
				int j = table.getTable().convertColumnIndexToModel(i);
				// System.out.println("+i+"+i);
				// System.out.println("+i+"+j);
				// �������򷽷�;
				// ת�����û���������к͵ײ����ݵ��У�Ȼ���ж� f
				if (j == sortColumn) {
					ascending = !ascending;
				} else {
					ascending = true;
					sortColumn = j;
				}
				// table.getModel().sort(ascending, sortColumn);

				// �����parmֵһ��,
				// 1.ȡparamwֵ;
				TParm tableData = getTTable("TABLE").getParmValue();
				// 2.ת�� vector����, ��vector ;
				String columnName[] = tableData.getNames("Data");
				String strNames = "";
				for (String tmp : columnName) {
					strNames += tmp + ";";
				}
				strNames = strNames.substring(0, strNames.length() - 1);
				// System.out.println("==strNames=="+strNames);
				Vector vct = getVector(tableData, "Data", strNames, 0);
				// System.out.println("==vct=="+vct);

				// 3.���ݵ������,��vector����
				// System.out.println("sortColumn===="+sortColumn);
				// ������������;
				String tblColumnName = getTTable("TABLE")
						.getParmMap(sortColumn);
				// ת��parm�е���
				int col = tranParmColIndex(columnName, tblColumnName);
				// System.out.println("==col=="+col);

				compare.setDes(ascending);
				compare.setCol(col);
				java.util.Collections.sort(vct, compare);
				// ��������vectorת��parm;
				cloneVectoryParam(vct, new TParm(), strNames);

				// getTMenuItem("save").setEnabled(false);
			}
		});
	}

	/**
	 * vectoryת��param
	 */
	private void cloneVectoryParam(Vector vectorTable, TParm parmTable,
			String columnNames) {
		//
		// System.out.println("===vectorTable==="+vectorTable);
		// ������->��
		// System.out.println("========names==========="+columnNames);
		String nameArray[] = StringTool.parseLine(columnNames, ";");
		// ������;
		for (Object row : vectorTable) {
			int rowsCount = ((Vector) row).size();
			for (int i = 0; i < rowsCount; i++) {
				Object data = ((Vector) row).get(i);
				parmTable.addData(nameArray[i], data);
			}
		}
		parmTable.setCount(vectorTable.size());

		// add by wangqing 20170629 start
		// ��ʹ�����������ʾ����
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
		// System.out.println("�����===="+parmTable);

	}

	/**
	 * �õ� Vector ֵ
	 * 
	 * @param group
	 *            String ����
	 * @param names
	 *            String "ID;NAME"
	 * @param size
	 *            int �������
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
				// System.out.println("tmp���");
				return index;
			}
			index++;
		}

		return index;
	}

	// $$==============add by lx 2012/06/24 ����������end=============$$//
	// ================= chenxi modify start 20120705
	public boolean isOidrFlg() {
		return oidrFlg;
	}

	public void setOidrFlg(boolean oidrFlg) {
		this.oidrFlg = oidrFlg;
	}

	/**
	 * �ٴ����
	 */

	public void onAddCLNCPath() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("��ѧҽ������������ٴ����");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {

			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * ������д
	 */

	public void onAddEmrWrite() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		if (this.isOidrFlg()) {
			parm.setData("RULETYPE", "3");
			// д������(����)
			parm.setData("WRITE_TYPE", "OIDR");
		} else if (tFlg) {// modified by wangqing 20170804 ��������
			parm.setData("RULETYPE", "3");
			// д������(����)
			parm.setData("WRITE_TYPE", "T");
		} else {
			parm.setData("RULETYPE", "2");
			// д������(����)
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
	 * ������Ŀ
	 */
	public void onAddBASY() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * �������
	 */
	public void onBABM() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("��ѧҽ�����ܽ��в������");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * ҽ����
	 */
	public void onSelYZD() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * ���±�
	 */
	public void onSelTWD() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * �����¼
	 */
	public void onHLSel() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * ��������
	 */
	public void onSSMZ() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("��ѧҽ�����ܽ�����������");
			return;
		}
		/*modified by WangQing 20170428 end*/

		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		parm.setData("ADM_TYPE", "I");
		parm.setData("BOOK_DEPT_CODE", this.getValue("DEPT_CODE"));// ���벿��
		parm.setData("BOOK_DR_CODE", Operator.getID());// ������Ա
		this.openDialog("%ROOT%\\config\\ope\\OPEOpBook.x", parm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * ��Ѫ����
	 */
	public void onBXResult() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("��ѧҽ�����ܽ��б�Ѫ����");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * ȡѪ����
	 * add by yangjj 20150427
	 */
	public void onQXResult() {
		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * �ż��ﲡ��
	 */
	public void onOpdBL() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrno = parm.getValue("MR_NO");
		TParm opdParm = new TParm(this.getDBTool().select(
				"SELECT * FROM REG_PATADM WHERE MR_NO='" + mrno
				+ "' AND (ADM_TYPE='O' OR ADM_TYPE='E')"));
		if (opdParm.getCount() < 0) {
			// �˲���û�����ﲡ����
			this.messageBox("E0184");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\OPDInfoUi.x", opdParm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * ���鱨��
	 */
	public void onLis() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		SystemTool.getInstance().OpenLisWeb(mrNo);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * ��鱨��
	 */
	public void onRis() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		SystemTool.getInstance().OpenRisWeb(mrNo);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * ����
	 */
	public void onTnb() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		SystemTool.getInstance().OpenTnbWeb(mrNo);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * ��Ժ֪ͨ
	 * @see ����סԺ����
	 */
	public void onOutHosp() {

		/*modified by WangQing 20170428 start*/
		if (tFlg) {
			this.messageBox("��ѧҽ������ִ�г�Ժ֪ͨ");
			return;
		}
		/*modified by WangQing 20170428 end*/

		// ���ﲻ����ҽ��
		if (this.isOidrFlg()) {
			this.messageBox("E0011");
			return;
		}
		// modified by WangQing 20170509 -start
		TTable table = (TTable) this.getComponent("TABLE");
		if(table.getSelectedRow()<0){
			this.messageBox("��ѡ��һ������");
			return;
		}
		// modified by WangQing 20170509 -end
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * �ռ��������
	 * 20170331 zhanglei
	 */
	public void onDaySurgery(){
		//��ý���Ttable�ؼ�
		TTable table;
		//��ý���Table�Ŀؼ�����
		table=(TTable)this.getComponent("Table");
		//�õ�ѡ���к�
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}

		//�õ�ѡ��������
		TParm parm = this.getSelectRowData(TABLE);
		this.openDialog("%ROOT%\\config\\adm\\ADMDaySurgery.x", parm);
		//�����޸ĵ�����
		onQuery();
		//this.openWindow("%ROOT%\\config\\adm\\ADMDaySurgery.x", parm);

		//this.messageBox("CASE_NO:" + parm.getValue("CASE_NO") + "MR_NO:" + parm.getValue("MR_NO") +	"DAY_OPE_FLG:" + parm.getValue("DAY_OPE_FLG"));

	}


	/**
	 * ���ò�ѯ
	 */
	public void onSelIbs() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
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
	 * ������Ժ/��Ժ�Ҳ�����
	 */
	public void onInOut() {
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬
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
	 * ԤԼ�Һ�
	 * 
	 * wangming
	 * 
	 * 2013.11.04
	 * 
	 */
	public void onReg() {
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬ 0��Ժ 1��Ժ
		int selType = tabPane.getSelectedIndex();

		if (selType == 1) {
			this.messageBox("��ѡ����Ժ���ˣ�");
			return;
		}
		String mrNo = this.getValueString("MR_NO");
		if (null == mrNo || mrNo.length() == 0) {
			this.messageBox("��ѡ���ˣ�");
			return;
		}
		Pat pat = Pat.onQueryByMrNo(mrNo);
		if (null == pat) {
			this.messageBox("�޴˲��ˣ�");
			return;
		}
		String nhiNo = pat.getNhiNo();

		TParm parm = new TParm();
		parm.setData("MR_NO", mrNo);
		parm.setData("NHI_NO", nhiNo);// ҽ������
		parm.setData("ADM_TYPE", "O"); // add by huangtt 20131204
		this.openWindow("%ROOT%\\config\\reg\\REGAdmForDr.x", parm);
	}

	/**
	 * ������Xls
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
			if (tabPane.getSelectedIndex() == 0) {// ��Ժҳǩ
				ExportExcelUtil.getInstance().exportExcel(getTTable(TABLE),
						"��Ժ������Ϣͳ��");
			} else {
				ExportExcelUtil.getInstance().exportExcel(getTTable(TABLE),
						"��Ժ������Ϣͳ��");
			}
		}
	}

	// =============== chenxi modify 20130326 START
	/**
	 * radioѡ��Ŀ���
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
	 * ����radio�ĳ�ʼ��
	 */
	public void initRadioButton() {
		callFunction("UI|NAME|setEnabled", false);
		callFunction("UI|ID|setEnabled", false);
	}

	/**
	 * ���ղ������������֤�Ų�ѯ��Ϣ(��Ժ)
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
		// �õ���Ժ��ѯ�Ĳ���
		TParm queryData = this.getQueryData("OUT");
		// �ж��Ƿ���ֵ��ҽ��
		boolean stationFlg = isKeepWatch();
		TParm actionParm = new TParm();
		// �õ���ѯSQL
		String type = "";
		if (name.isSelected()) {
			type = "NAME";
		} else if (id.isSelected()) {
			type = "ID";
		}
		String sqlStr = createOUTODIQuerySQL(queryData, type, stationFlg);
		if (stationFlg) {
			// ��ѯ����������Ϣ
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		} else {
			actionParm = new TParm(this.getDBTool().select(sqlStr));
		}
		// out("��Ժ��ѯ�������" + actionParm);
		// �����ѯΪ�շ���
		if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// ���Table
			callFunction("UI|" + TABLE + "|removeRowAll");
			return;
		}
		// �õ�����ҳǩ��TAG��������ѯ
		if (workPanelTag.length() != 0) {
			this.queryDataOtherTPane(actionParm, "OUT");
			return;
		}
		// ����TABLE�ϵ�����
		this.setTableData(actionParm, "OUT");

		/*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * ���ذ��ղ������������֤�Ų�ѯ��sql
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
		// ����
		String stationStr = parm.getValue("STATION_CODE").length() == 0 ? ""
				: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE") + "'";
		// ����

		String deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
				: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		// ����״̬
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

	// =============== chenxi modify 20130326 END ��Ժ�������������ݲ������������֤�Ų�ѯ

	/**
	 * ��ӡLis�ϲ�����
	 */
	public void onPrintLis() {
		TTable table = ((TTable) this.getComponent(TABLE));
		int row = table.getSelectedRow();
		TParm data = new TParm();
		if (row != -1) {
			data = getTTable("TABLE").getParmValue().getRow(row);
		}else{
			this.messageBox("��ѡ��һ��������");
			return;
		}
		TParm parm = new TParm();
		parm.setData("CASE_NO", data.getValue("CASE_NO"));
		TParm result = new TParm();
		result = MedSmsTool.getInstance().queryLisReport(parm);

		if (result.getCount("REPORT_DATE") < 0) {
			this.messageBox("�޸ò��������ݣ�");
			return;
		}

		String lisType = "";
		lisType += result.getValue("RPTTYPE_CODE", 0) + "��";
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
				lisType += result.getValue("RPTTYPE_CODE", i + 1) + "��";
				temp = getEmptyTParm(result.getValue("RPTTYPE_CODE", i + 1));
				result.setRowData(i + 1, temp);
			}
		}

		temp = getEmptyTParm(result.getValue("RPTTYPE_CODE", 0));
		result.setRowData(0, temp);

		lisType = lisType.substring(0, lisType.lastIndexOf("��") - 1);
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
		printParm.setData("SEX", "TEXT", "1".equals(data.getValue("SEX_CODE")) ? "��" : "Ů");
		printParm.setData("LIS_TYPE", "TEXT", lisType);

		TTabbedPane tabPane = (TTabbedPane) this.callFunction("UI|TablePane|getThis");
		int selType = tabPane.getSelectedIndex();
		// 0Ϊ��Ժҳǩ��INDEX;1Ϊ��Ժҳǩ��INDEX
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
		emrTool.saveEMR(obj, "������ܱ���", "EMR040002", "EMR04000204", false);
	}

	/**
	 * �õ���ӡLis����Ŀ�TParm ���ڷָͬ��������
	 * 
	 * @return
	 */
	public TParm getEmptyTParm(String lisType) {
		TParm parm = new TParm();
		parm.setData("REPORT_DATE", "�������" + lisType);
		parm.setData("TESTITEM_CHN_DESC", "");
		parm.setData("OPTITEM_CHN_DESC", "");
		parm.setData("TEST_VALUE", "");
		parm.setData("CRTCLLWLMT", "");
		parm.setData("CKZ", "");
		parm.setData("TEST_UNIT", "");
		return parm;
	}

	/**
	 * �������
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
	 * ��ȡ�����ļ�
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
	 * ��ȡ�����ļ��еĵ��Ӳ���������IP
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
			this.messageBox("��ѡ��Ҫ�鿴�Ĳ�����Ϣ");
			return;
		}    
		Container container = (Container) callFunction("UI|getThis");
		while (!(container instanceof TTabbedPane)) {
			container = container.getParent();
		}
		TTabbedPane tabbedPane = (TTabbedPane) container;

		parm.setData("MR_NO", table.getParmValue().getRow(selRow).getValue("MR_NO"));
		// ���ۺϲ�ѯ����
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
	 * ����NIS�����
	 */
	public  void  onNisFormList(){
		TTable table = ((TTable) this.getComponent(TABLE));
		int row = table.getSelectedRow();
		TParm data = new TParm();
		if (row != -1) {
			data = getTTable("TABLE").getParmValue().getRow(row);
		}else{
			this.messageBox("��ѡ��һ��������");
			return;
		}
		String caseNo=data.getValue("CASE_NO");
		String mrNo=data.getValue("MR_NO");
		SystemTool.getInstance().OpeNisFormList(caseNo, mrNo); 	
	}
	/**
	 * 
	 * @Title: getClpOdiStationParm
	 * @Description: TODO(�ٴ�·���ܿ����)
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
	 * ������д
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
	 * Ѫ�Ǳ���
	 */
	public void getXTReport(){
		SystemTool.getInstance().OpenTnbWeb(this.MRNO);
	}

	public boolean readTxtFile(String filePath){
		String txt = "";
		try {
			String encoding="GBK";
			File file=new File(filePath);
			if(file.isFile() && file.exists()){ //�ж��ļ��Ƿ����
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file),encoding);//���ǵ������ʽ
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
				System.out.println("�Ҳ���ָ�����ļ�");
			}
		} catch (Exception e) {
			System.out.println("��ȡ�ļ����ݳ���");
			e.printStackTrace();
		}
		return false;

	}

	/** 
	 * �����������鿴 add by huangtt 20151030
	 */
	public void onErdTriage() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrno = parm.getValue("MR_NO");
		TParm opdParm = new TParm(this.getDBTool().select(
				"SELECT * FROM REG_PATADM WHERE MR_NO='" + mrno
				+ "' AND ADM_TYPE='E' AND TRIAGE_NO IS NOT NULL "));
		if (opdParm.getCount() < 0) {
			// �˲���û�����ﲡ����
			this.messageBox("�˲���û�м���������");
			return;
		}
		this.openDialog("%ROOT%\\config\\odi\\ERDInfoUi.x", opdParm);
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * ���ɽ��ӵ�
	 */
	public void onCreate(){
		TParm action = new TParm();
		TTable table = (TTable)this.getComponent("TABLE");
		int index = table.getSelectedRow();//ѡ����
		if(index<0){
			this.messageBox("��ѡ�񲡻�");
			return;
		}
		TParm parm = table.getParmValue();
		//	       System.out.println("---parm----------------"+parm);
		action.setData("MR_NO",parm.getValue("MR_NO",index));//������
		action.setData("CASE_NO",parm.getValue("CASE_NO",index));//�����
		action.setData("PAT_NAME",parm.getValue("PAT_NAME",index));//���� 
		action.setData("FROM_DEPT",parm.getValue("DEPT_CODE",index));//ת������
		action.setData("PARM", parm.getRow(index));//���ڷǲ�����ʽ�Ľ��ӵ�
		//	       System.out.println("---action----------------"+action);
		action.setData("DEPT_TYPE_FLG","ODI");//���ڿ���ѡ�������ʾ���ұ��
		//action.setData("DAY_OPE_CODE", "1".equals(parm.getValue("DAY_OPE_CODE",index)) ? "��������":"");
		action.setData("DAY_OPE_FLG", parm.getValue("DAY_OPE_FLG",index));
		this.openWindow("%ROOT%\\config\\odi\\ODITransfertype.x", action); 

	}
	/**
	 * ����һ����
	 */
	public void onTransfer(){
		TParm action = new TParm();
		TTable table = (TTable)this.getComponent("TABLE");
		int index = table.getSelectedRow();//ѡ����	        
		if(index<0){

		}	        
		else{
			TParm parm = table.getParmValue();
			//		        System.out.println("---parm----------------"+parm);
			action.setData("MR_NO", parm.getValue("MR_NO",index));//������
			action.setData("CASE_NO", parm.getValue("CASE_NO",index));//�����	
		}	
		//	        System.out.println("---action----------------"+action);
		this.openWindow("%ROOT%\\config\\inw\\INWTransferSheet.x",action);		
	} 
	/**
	 * ����һ��
	 */
	public void onEvalutionRecordOpen() {
		TTable table = (TTable)this.getComponent("TABLE");
		int row = table.getSelectedRow();//ѡ����
		if (row < 0){
			this.messageBox("��ѡ��һ������");
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
	 * �˵��� ->�������� ��ǰ���˵��ٴ���Ŀ
	 */
	public void onPathologyType() {
		//this.messageBox("��������");
		int row = getTTable(TABLE).getSelectedRow();
		if(row >= 0) {
			TParm parm = this.getSelectRowData(TABLE);
			this.openDialog("%ROOT%\\config\\adm\\AdmPathology.x", parm);
		} else {
			this.messageBox("��ѡ��һ������");
		}
	}

	/**
	 * ���Ľ����ܼ챨��
	 */
	public void onHrmEmr() {
		int row = getTTable(TABLE).getSelectedRow();
		if (row >= 0) {
			TParm parm = this.getSelectRowData(TABLE);
			String sql = "SELECT OPD_CASE_NO FROM ADM_RESV WHERE IN_CASE_NO = '"
					+ parm.getValue("CASE_NO") + "'";
			TParm queryParm = new TParm(TJDODBTool.getInstance().select(sql));

			if (queryParm.getErrCode() < 0) {
				this.messageBox("��ѯ����������Ϣ����");
				err("ERR:" + queryParm.getErrText());
				return;
			}
			queryParm.setData("CASE_NO", queryParm.getValue("OPD_CASE_NO", 0)
					.replace(",", "','"));// ��������
			queryParm.setData("DEPT_ATTRIBUTE", "04");// �ܼ�
			this.openDialog("%ROOT%\\config\\hrm\\HRMTotViewQuery.x", queryParm);
		} else {
			this.messageBox("��ѡ��һ������");
		}
	}

	/**
	 * ����ǿ��Ѫ�Ǳ���(webչ��)
	 */
	public void getBgReport() {
		this.onInOut();
		if (MRNO == null || MRNO.length() == 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		if (PatTool.getInstance().isLockPat(this.getValueString("MR_NO"))) {
			if (this.messageBox("�Ƿ���� Whether to unlock", PatTool.getInstance()
					.getLockParmString(this.getValueString("MR_NO")), 0) == 0) {
				PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
				PatTool.getInstance().lockPat(this.getValueString("MR_NO"),
						"ODI");

			} else {
				return;
			}
		} else {
			// ����
			PatTool.getInstance().lockPat(this.getValueString("MR_NO"), "ODI");

		}
		TParm parm = this.getSelectRowData(TABLE);
		String mrNo = parm.getValue("MR_NO");
		String caseNo = parm.getValue("CASE_NO");
		// ǿ��Ѫ��webչ��
		TParm result = SystemTool.getInstance().OpenJNJWeb(caseNo);
		if (result.getErrCode() < 0) {
			this.messageBox(result.getErrText());
		}
		PatTool.getInstance().unLockPat(this.getValueString("MR_NO"));
	}

	/**
	 * add lij 201740502
	 * ˫��ͨ
	 */
	public void onCommunicate(){

		int rowIndex = ((TTable) this.getComponent("TABLE"))
				.getSelectedRow();
		if (rowIndex < 0) {
			this.messageBox_("��ѡ��һ�ݲ�����");
			return;
		}
		TParm parm = new TParm();
		TParm patInfo = (TParm) callFunction("UI|TABLE|getParmValue");
		parm.setData("CASE_NO", patInfo.getValue("CASE_NO", rowIndex));
		parm.setData("CLASS_NAME", "SEND");
		this.openWindow("%ROOT%\\config\\inf\\INFTwoCommunicate.x", parm);
	}


	/**
	 * ��ѯSQL
	 * @param queryData
	 * @author WangQing
	 * @return
	 */
	public String createQuerySql(TParm parm){
		//		String sql = "";
		// סԺ��ʼʱ��
		String startDate = StringTool.getString((Timestamp) parm.getData("START_DATE"), "yyyyMMdd");
		// סԺ����ʱ��
		String endDate = StringTool.getString((Timestamp) parm.getData("END_DATE"), "yyyyMMdd");
		// ��Ժ��ʼʱ��
		String startDateOut = StringTool.getString((Timestamp) parm.getData("START_DATEOUT"), "yyyyMMdd");
		// ��Ժ����ʱ��
		String endDateOut = StringTool.getString((Timestamp) parm.getData("END_DATEOUT"), "yyyyMMdd");
		// ��Ժ/��Ժ
		String type = (String) parm.getData("TYPE");
		// ����/id (��Ժ)
		String idOrNameStr = "";
		if(type.equals("OUT")){
			TRadioButton name = (TRadioButton) this
					.callFunction("UI|NAMESELECT|getThis");
			// �õ���ѯSQL
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
		// ����
		String deptCode = parm.getValue("DEPT_CODE").length() == 0 ? ""
				: " AND A.DEPT_CODE='" + parm.getValue("DEPT_CODE") + "'";
		// ����
		String stationCode = parm.getValue("STATION_CODE").length() == 0 ? ""
				: " AND A.STATION_CODE='" + parm.getValue("STATION_CODE")
				+ "'";
		// ����ҽʦ
		String vsDoctor = parm.getValue("VC_CODE").length() == 0 ? ""
				: " AND A.VS_DR_CODE='" + parm.getValue("VC_CODE") + "'";
		// ��Ժ��������or��Ժ��������
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
		// ����
		String region = (null != Operator.getRegion() && Operator.getRegion().length() > 0)
				?" AND A.REGION_CODE='" + Operator.getRegion() + "' ":"";
		// �����
		String caseNoStr = (null != parm.getValue("CASE_NO") && parm.getValue("CASE_NO").length()>0)
				?" AND A.CASE_NO='"+parm.getValue("CASE_NO")+"' ":"";	
		// ռ��ע��ALLO_FLG
		String alloFlg = "";
		// ����״̬
		String billStatus = "";
		// ����
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
		// ����ADM_INP �ӱ�SYS_BED,SYS_PATINFO,SYSDIAGNOSIS,MRO_MRV_TECH
		// (+)����ߣ���ʾRIGHT JOIN
		//		sql = " SELECT A.CLNCPATH_CODE, B.BED_NO_DESC,C.PAT_NAME,C.SEX_CODE,C.BIRTH_DATE,A.IN_DATE,A.DS_DATE,D.ICD_CHN_DESC AS MAINDIAG,"
		//				+ " A.CTZ1_CODE,A.MR_NO,A.IPD_NO,A.TOTAL_AMT,A.TOTAL_BILPAY,A.GREENPATH_VALUE,A.STATION_CODE,"
		//				+ " A.RED_SIGN,A.YELLOW_SIGN,A.STOP_BILL_FLG,A.BED_NO,A.CTZ2_CODE,A.CTZ3_CODE,A.VS_DR_CODE,"
		//				+ " A.DEPT_CODE,A.HEIGHT,A.WEIGHT,A.CASE_NO,A.CUR_AMT,C.POST_CODE,C.ADDRESS,C.COMPANY_DESC,"
		//				+ " C.TEL_HOME,C.IDNO,C.PAT_NAME1,A.NURSING_CLASS,A.PATIENT_STATUS,D.ICD_CODE,E.CHECK_FLG AS MRO_CHAT_FLG,B.ENG_DESC,A.SERVICE_LEVEL,A.BILL_STATUS"
		//				+ " ,A.DISE_CODE,A.SCHD_CODE "// add by wanglong 20121115
		//				+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
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
				+ " ,(CASE A.ALLERGY WHEN  'Y' THEN '��' ELSE '��' END) ALLERGY," 
				+ " (CASE WHEN A.INFECT_SCR_RESULT IS NOT NULL THEN A.INFECT_SCR_RESULT ELSE '' END) INFECT_SCR_RESULT,A.FALL_RISK "  // add by huangjw 20151109
				+ " ,E.SEALED_STATUS  "  //add by huangtt 20161103
				//+ " , A.DAY_OPE_CODE " //add by huangjw 20170303
				+ " , A.DAY_OPE_FLG " //add by huangjw 20170322

				/*modified by Eric 20170519 ��ʹ������ʾ*/
				+ " ,REG.ENTER_ROUTE, REG.PATH_KIND "

				/*modified by Eric 20170519 ��ʹ������ʾ*/
				+ " FROM ADM_INP A,SYS_BED B,SYS_PATINFO C,SYS_DIAGNOSIS D,MRO_MRV_TECH E , ADM_RESV AR,REG_PATADM REG"
				+ " WHERE A.BED_NO=B.BED_NO(+) "
				+ " AND A.MR_NO=C.MR_NO(+) "

				 /*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * ��ѯ����
	 */
	public void onQueryNew() {
		//add by yangjj 20151112 �������Ϊ��ʿִ�н��棬���ѯc:\javahis\nis\nis.txt�ļ�����ȡnis.txt�Ĳ�����Ϣ
		if("INWSTATIONEXECUTE".equals(workPanelTag)){
			onClosePanel();
			readTxtFile("C:\\JavaHis\\NIS\\nis.txt");
			return;
		}
		// �õ�TabbedPane�ؼ�
		TTabbedPane tabPane = (TTabbedPane) this
				.callFunction("UI|TablePane|getThis");
		// ��ʾҳǩѡ���״̬ 0����Ժ 1����Ժ
		int selType = tabPane.getSelectedIndex();
		/** modified by WangQing 20170427 start*/
		String type = (selType == 0)?"IN":"OUT";// ��Ժ/��Ժ��־
		TParm queryData = this.getQueryData(type);
		String sqlStr = this.createQuerySql(queryData);
		TParm actionParm = new TParm();
		actionParm = new TParm(this.getDBTool().select(sqlStr));		
		if (actionParm.getInt("ACTION", "COUNT") == 0) {
			// ���Table
			callFunction("UI|" + TABLE + "|removeRowAll");
			return;
		}
		// �õ�����ҳǩ��TAG��������ѯ
		if (workPanelTag.length() != 0) {
			this.queryDataOtherTPane(actionParm, type);
			return;
		}
		System.out.println("#######actionParm:"+actionParm);
		// ����TABLE�ϵ�����
		this.setTableData(actionParm, type);

		/*modified by Eric 20170519 ��ʹ������ʾ*/
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
	 * ��ʱ���� 20170705 yanmm
	 */
	public void onUnlock(){
		TTable table;
		table = (TTable) this.getComponent("Table");
		int row = table.getSelectedRow();
		if (row < 0) {
			this.messageBox("��ѡ��һ������");
			return;
		}
		TParm parm = this.getSelectRowData(TABLE);
		// parm.setData("SAVE_FLG", this.getPopedem("admChangeDr"));
		this.openDialog("%ROOT%\\config\\adm\\AdmGreenChannelUnlock.x", parm);
		onQuery();
	}


	/**
	 * <p>����֪��ͬ����ǩ��</p>
	 * 
	 * @author wangqing 20170723
	 */
	public void onSign(){
		TParm parm = new TParm();
		TTable t = (TTable) this.getComponent("TABLE");
		int row = t.getSelectedRow();
		if(row < 0){
			this.messageBox("��ѡ��һ�����ݣ�");
			return;
		}
		TParm rowParm = t.getParmValue().getRow(row);
		System.out.println("####rowParm="+rowParm);
		parm.setData("MR_NO", rowParm.getValue("MR_NO"));
		parm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
		this.openWindow("%ROOT%\\config\\emr\\EMRInformedConsentForm.x", parm);
	}

	/**
	 * ���뻤���¼
	 * @author wangqing 20180115
	 */
	public void onOpeNursingRecord(){
		TTable t = (TTable) this.getComponent("TABLE");
		int row = t.getSelectedRow();
		if(row < 0){
			this.messageBox("��ѡ��һ�����ݣ�");
			return;
		}
		TParm rowParm = t.getParmValue().getRow(row);
		TParm parm = new TParm();		
		parm.setData("CASE_NO", rowParm.getValue("CASE_NO"));
		this.openWindow("%ROOT%\\config\\ope\\OPENursingRecord.x", parm);
	}
	
	/**
	 * ��������
	 *   == 20180115 zhanglei add
	 */
	public void getPDFQiTa() {
		TTable t = (TTable) this.getComponent("TABLE");
		int row = t.getSelectedRow();
		if(row < 0){
			this.messageBox("��ѡ��һ�����ݣ�");
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
